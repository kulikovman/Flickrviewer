package ru.kulikovman.flickrviewer;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.kulikovman.flickrviewer.adapters.PhotoAdapter;
import ru.kulikovman.flickrviewer.models.FlickrResponse;
import ru.kulikovman.flickrviewer.models.Photo;
import ru.kulikovman.flickrviewer.models.PhotoPreview;

public class PhotoGalleryActivity extends AppCompatActivity {
    private static final String TAG = "PhotoGalleryActivity";

    private static final String RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final String API_KEY = "92cc75b96a9f82a32bc29eb21a254fe4";
    private static final String FORMAT = "json";
    private static final String NOJSONCALLBACK = "1";
    private static final String SIZE_URL = "url_n";

    private Realm mRealm;
    private RecyclerView mRecyclerView;
    private PhotoAdapter mPhotoAdapter;
    private List<Photo> mPhotoList = new ArrayList<>();
    private List<PhotoPreview> mPhotoPreviews = new ArrayList<>();

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        Log.d("log", "Запущен onCreate в PhotoGalleryActivity");

        // Инициализация вью элементов
        mRecyclerView = findViewById(R.id.photo_recycler_view);
        mProgressBar = findViewById(R.id.progress_bar);

        // Инициализируем базу данных
        Realm.init(this);
        mRealm = Realm.getDefaultInstance();

        // Запуск RecyclerView
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, getNumberOfColumns()));
        mRecyclerView.setHasFixedSize(true);
        setupAdapter();

        // Получение списка фотографий
        loadPhotoData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("log", "Запущен onResume в PhotoGalleryActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("log", "Запущен onPause в PhotoGalleryActivity");
    }

    private void loadPhotoData() {
        mProgressBar.setVisibility(View.VISIBLE);

        App.getApi().getRecent(RECENTS_METHOD, API_KEY, FORMAT, NOJSONCALLBACK, 50, 1, SIZE_URL)
                .enqueue(new Callback<FlickrResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<FlickrResponse> call, @NonNull Response<FlickrResponse> response) {
                        mProgressBar.setVisibility(View.GONE);

                        if (response.isSuccessful()) {
                            Log.d(TAG, "Запрос прошел успешно: " + response.code());
                            if (response.body() != null) {
                                // Добавляем новые фото в список
                                for (Photo photo : response.body().getPhotos().getPhoto()) {
                                    if (photo.getUrlN() != null) {







                                        mPhotoList.add(photo);
                                    }
                                }

                                setupAdapter();
                            }
                        } else {
                            Log.d(TAG, "Запрос прошел, но что-то пошло не так: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<FlickrResponse> call, @NonNull Throwable t) {
                        Log.d(TAG, "Ошибка при отправке запроса: " + t.getMessage());
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Подключаем макет меню
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);

        // Обработчик поисковых запросов
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "QueryTextSubmit: " + s);
                QueryPreferences.setStoredQuery(PhotoGalleryActivity.this, s);
                searchView.clearFocus();
                searchView.onActionViewCollapsed();
                //updateItems();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(TAG, "QueryTextChange: " + s);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QueryPreferences.getStoredQuery(PhotoGalleryActivity.this);
                searchView.setQuery(query, false);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Обрабатываем нажатие
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                //QueryPreferences.setStoredQuery(this, null);
                //updateItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupAdapter() {
        if (mPhotoAdapter == null) {
            mPhotoAdapter = new PhotoAdapter(this, mPhotoList);
            mRecyclerView.setAdapter(mPhotoAdapter);
        } else {
            mPhotoAdapter.setPhotos(mPhotoList);
            mPhotoAdapter.notifyDataSetChanged();
        }
    }

    public int getNumberOfColumns() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        int width = 0;
        int height = 0;
        if (wm != null) {
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            height = size.y;
        }

        int numberOfColumns = width / convertDpToPx(this, 120);
        return numberOfColumns != 0 ? numberOfColumns : 3;
    }

    public static int convertDpToPx(Context context, int valueInDp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp,
                context.getResources().getDisplayMetrics());
    }

    /*private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {
        private ProgressDialog mProgressDialog;
        private String mQuery;

        public FetchItemsTask(String query) {
            mQuery = query;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog
                    .show(PhotoGalleryActivity.this, "Loading", "Wait while loading...", false);
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            if (mQuery == null) {
                return new FlickrFetchr().fetchRecentPhotos();
            } else {
                mItems.clear();
                return new FlickrFetchr().searchPhotos(mQuery);
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mProgressDialog.dismiss();
            Log.d(TAG, "Список содержит: " + items.size() + " значений");

            // Добавляем новые фото в список
            for (GalleryItem item : items) {
                if (!mItems.contains(item)) {
                    mItems.add(item);
                }
            }

            setupAdapter();
        }
    }*/
}
