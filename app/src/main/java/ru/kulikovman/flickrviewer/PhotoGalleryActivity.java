package ru.kulikovman.flickrviewer;

import android.content.Context;
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
import android.widget.ProgressBar;

import io.realm.Realm;
import ru.kulikovman.flickrviewer.adapters.PhotoAdapter;

public class PhotoGalleryActivity extends AppCompatActivity {
    private static final String TAG = "PhotoGalleryActivity";

    private Realm mRealm;
    private RecyclerView mPhotoRecyclerView;
    private PhotoAdapter mPhotoAdapter;

    private RealmHelper mRealmHelper;
    private FlickrFetcher mFlickrFetcher;

    private ProgressBar mProgressBar;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private String mSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        Log.d(TAG, "Запущен onCreate в PhotoGalleryActivity");

        // Инициализация вью элементов
        mPhotoRecyclerView = findViewById(R.id.photo_recycler_view);
        mProgressBar = findViewById(R.id.progress_bar);

        // Получаем важные штуки
        mRealm = Realm.getDefaultInstance();
        mRealmHelper = RealmHelper.get();
        mFlickrFetcher = FlickrFetcher.get(this);

        // Если база пустая, то загружаем новые фото
        if (mRealmHelper.baseIsEmpty()) {
            mFlickrFetcher.loadPhoto();
        }

        // Запуск списка
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
                mFlickrFetcher.loadPhoto(mSearchQuery, page);
            }
        };

        mPhotoRecyclerView.addOnScrollListener(mScrollListener);
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
                // Сброс базы и настроек перед новым запросом
                mRealm.beginTransaction();
                mRealm.deleteAll();
                mRealm.commitTransaction();
                mScrollListener.resetState();
                mSearchQuery = s;

                // Получение фото
                mFlickrFetcher.loadPhoto(mSearchQuery);

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
            case R.id.menu_item_clear:
                //QueryPreferences.setStoredQuery(this, null);
                //updateItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
