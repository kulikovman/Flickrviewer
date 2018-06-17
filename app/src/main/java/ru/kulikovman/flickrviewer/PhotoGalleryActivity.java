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
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        Log.d(TAG, "Запущен onCreate в PhotoGalleryActivity");

        // Инициализация вью элементов
        mPhotoRecyclerView = findViewById(R.id.photo_recycler_view);
        mProgressBar = findViewById(R.id.progress_bar);

        // Получаем важные штуки
        mRealmHelper = RealmHelper.get();
        mFlickrFetcher = FlickrFetcher.get();

        // Запуск списка
        setUpPhotoRecyclerView();

        // Если база пустая, то подгружаем новые фото
        if (mRealmHelper.baseIsEmpty()) {
            Log.d(TAG, "База пустая, начинается загрузка...");
            //mProgressBar.setVisibility(View.VISIBLE);
            mFlickrFetcher.loadRecentPhoto(1);
        }
    }

    private void setUpPhotoRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, getNumberOfColumns());
        mPhotoRecyclerView.setLayoutManager(gridLayoutManager);
        mPhotoRecyclerView.setHasFixedSize(true);
        mPhotoAdapter = new PhotoAdapter(this, mRealmHelper.getPhotoList());
        mPhotoRecyclerView.setAdapter(mPhotoAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Подгрузка новых данных
                mFlickrFetcher.loadRecentPhoto(page);
                Log.d(TAG, "Загрузка фотографий: " + page);
            }
        };

        mPhotoRecyclerView.addOnScrollListener(scrollListener);
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

            setUpPhotoRecyclerView();
        }
    }*/
}
