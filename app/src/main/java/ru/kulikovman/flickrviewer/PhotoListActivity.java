package ru.kulikovman.flickrviewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
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
import android.widget.LinearLayout;

import io.realm.Realm;
import ru.kulikovman.flickrviewer.adapters.PhotoAdapter;

public class PhotoListActivity extends AppCompatActivity {
    private static final String TAG = "PhotoListActivity";

    private Realm mRealm;
    private RealmHelper mRealmHelper;
    private FlickrFetcher mFlickrFetcher;
    private SharedPreferences mSharedPref;

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
        mFlickrFetcher = new FlickrFetcher(this, mProgressBarContainer);

        // Восстанавливаем поисковый запрос и заголовок
        mSharedPref = getPreferences(Context.MODE_PRIVATE);
        mSearchQuery = mSharedPref.getString(getString(R.string.search_query), "");
        setTitle(createNewTitle(mSearchQuery));

        // Если база пустая
        if (mRealm.isEmpty()) {
            showProgressBar();
            mFlickrFetcher.loadPhoto();
        }

        // Запуск сетки фотографий
        setupPhotoRecyclerView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Запущен onPause в PhotoListActivity");

        // Сохранение поискового запроса
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(getString(R.string.search_query), mSearchQuery);
        editor.apply();
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
                mFlickrFetcher.loadPhoto(mSearchQuery, page, false);
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
            public boolean onQueryTextSubmit(String s) {
                // Подготовка к запросу
                mScrollListener.resetState();
                mSearchQuery = s;
                setTitle(createNewTitle(s));

                // Получение фото
                showProgressBar();
                mFlickrFetcher.loadPhoto(mSearchQuery, true);

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
                mScrollListener.resetState();
                setTitle(createNewTitle(""));
                mSearchQuery = "";
                showProgressBar();
                mFlickrFetcher.loadPhoto(true);
                return true;
            case R.id.menu_photo_on_map:
                // Открываем карту

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showProgressBar() {
        mProgressBarContainer.setVisibility(View.VISIBLE);
    }

    private String createNewTitle(String title) {
        if(title == null || title.isEmpty()) {
            return getString(R.string.app_name);
        } else {
            return title.substring(0, 1).toUpperCase() + title.substring(1);
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
                    .show(PhotoListActivity.this, "Loading", "Wait while loading...", false);
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            if (mQuery == null) {
                return new FlickrFetcher().fetchRecentPhotos();
            } else {
                mItems.clear();
                return new FlickrFetcher().searchPhotos(mQuery);
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

            setupPhotoRecyclerView();
        }
    }*/
}
