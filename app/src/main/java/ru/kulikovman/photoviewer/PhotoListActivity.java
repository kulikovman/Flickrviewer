package ru.kulikovman.photoviewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.kulikovman.photoviewer.adapters.PhotoAdapter;
import ru.kulikovman.photoviewer.models.photo.Photo;
import ru.kulikovman.photoviewer.models.photo.PhotoResponse;

public class PhotoListActivity extends AppCompatActivity {
    private static final String TAG = "PhotoListActivity";

    private Realm mRealm;
    private RealmHelper mRealmHelper;

    private PhotoAdapter mPhotoAdapter;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private RecyclerView mPhotoRecyclerView;

    private LinearLayout mProgressBarContainer;
    private String mSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);
        Log.d(TAG, "Запущен onCreate в PhotoListActivity");

        // Инициализация вью элементов
        mPhotoRecyclerView = findViewById(R.id.photo_recycler_view);
        mProgressBarContainer = findViewById(R.id.progress_bar_container);

        // Инициализация разных нужностей
        mRealm = Realm.getDefaultInstance();
        mRealmHelper = RealmHelper.get();

        // Восстанавливаем поисковый запрос и заголовок
        mSearchQuery = PreferencesHelper.loadSearchQuery(this);
        setTitle(createNewTitle(mSearchQuery));

        // Если база пустая
        if (mRealm.isEmpty()) {
            showProgressBar();
            loadRecentPhoto(1, false);
        }

        // Запуск списка фотографий
        setupPhotoRecyclerView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    private void setupPhotoRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, getNumberOfColumns());
        mPhotoRecyclerView.setLayoutManager(gridLayoutManager);
        mPhotoRecyclerView.setHasFixedSize(true);
        mPhotoAdapter = new PhotoAdapter(this, mRealmHelper.getPhotoList());
        mPhotoRecyclerView.setAdapter(mPhotoAdapter);

        mScrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Загрузка дополнительных фото
                if (mSearchQuery == null || mSearchQuery.isEmpty()) {
                    loadRecentPhoto(page, false);
                } else {
                    loadSearchPhoto(mSearchQuery, page, false);
                }

            }
        };

        mPhotoRecyclerView.addOnScrollListener(mScrollListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Подключаем макет меню
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // Обработчик поисковых запросов
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchQuery) {
                preparingForLoadPhoto(searchQuery);

                // Получение фото
                showProgressBar();
                loadSearchPhoto(mSearchQuery, 1, true);

                // Свертывание поиска
                searchView.clearFocus();
                searchView.onActionViewCollapsed();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Обрабатываем нажатие
        switch (item.getItemId()) {
            case R.id.menu_recent_photo:
                preparingForLoadPhoto("");

                // Загружаем фото
                showProgressBar();
                loadRecentPhoto(1, true);
                return true;
            case R.id.menu_photo_on_map:
                // Открываем карту
                Intent intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void preparingForLoadPhoto(String searchQuery) {
        if (mScrollListener != null) {
            mScrollListener.resetState();
        }

        mSearchQuery = searchQuery;
        setTitle(createNewTitle(searchQuery));
        PreferencesHelper.saveSearchQuery(PhotoListActivity.this, searchQuery);
    }

    private void loadRecentPhoto(int page, final boolean clearData) {
        App.getApi().getRecent(getString(R.string.recent_method), getString(R.string.api_key),
                getString(R.string.format), getString(R.string.nojsoncallback), getString(R.string.size_url_n),
                60, page)
                .enqueue(new Callback<PhotoResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PhotoResponse> call, @NonNull Response<PhotoResponse> response) {
                        hideProgressBar();
                        if (response.isSuccessful()) {
                            putReceivedPhotosInBase(response, clearData);
                        } else {
                            Log.d(TAG, "Response is not successful: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PhotoResponse> call, @NonNull Throwable t) {
                        showErrorToast(t);
                    }
                });
    }

    private void loadSearchPhoto(String searchQuery, int page, final boolean clearData) {
        App.getApi().getSearch(getString(R.string.search_method), getString(R.string.api_key),
                getString(R.string.format), getString(R.string.nojsoncallback), getString(R.string.size_url_n),
                60, page, searchQuery)
                .enqueue(new Callback<PhotoResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PhotoResponse> call, @NonNull Response<PhotoResponse> response) {
                        hideProgressBar();
                        if (response.isSuccessful()) {
                            putReceivedPhotosInBase(response, clearData);
                        } else {
                            Log.d(TAG, "Response is not successful: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PhotoResponse> call, @NonNull Throwable t) {
                        showErrorToast(t);
                    }
                });
    }

    private void putReceivedPhotosInBase(Response<PhotoResponse> response, boolean clearData) {
        List<Photo> photoList = new ArrayList<>();
        try {
            photoList = response.body().getPhotos().getPhoto();
        } catch (Exception ignored) {
        }

        if (photoList != null) {
            // Очистка базы
            if (clearData) {
                mRealm.beginTransaction();
                mRealm.deleteAll();
                mRealm.commitTransaction();
            }

            // Добавляем новые фото в базу
            for (Photo photo : photoList) {
                // Если есть ссылка на миниатюру и она еще не загружена
                if (photo.getUrlN() != null && !mRealmHelper.isExistUrl(photo.getUrlN())) {
                    mRealm.beginTransaction();
                    mRealm.insert(photo);
                    mRealm.commitTransaction();
                } else {
                    Log.d(TAG, "The photo does not have a thumbnail or is already in the database");
                }
            }
        }
    }

    private void showErrorToast(@NonNull Throwable t) {
        hideProgressBar();
        Toast.makeText(PhotoListActivity.this, "Error with internet connection", Toast.LENGTH_LONG).show();
        Log.d(TAG, "Error with internet connection: " + t.getMessage());
    }

    private void showProgressBar() {
        mProgressBarContainer.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mProgressBarContainer.setVisibility(View.INVISIBLE);
    }

    private String createNewTitle(String title) {
        if (title == null || title.isEmpty()) {
            return getString(R.string.title_recent_photo);
        } else {
            return title.substring(0, 1).toUpperCase() + title.substring(1);
        }
    }

    public int getNumberOfColumns() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        int width = 0;
        if (wm != null) {
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            width = size.x;
        }

        int numberOfColumns = width / convertDpToPx(this, 120);
        return numberOfColumns != 0 ? numberOfColumns : 3;
    }

    public static int convertDpToPx(Context context, int valueInDp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp,
                context.getResources().getDisplayMetrics());
    }
}
