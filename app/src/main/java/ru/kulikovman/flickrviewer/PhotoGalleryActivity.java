package ru.kulikovman.flickrviewer;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.kulikovman.flickrviewer.adapters.PhotoAdapter;
import ru.kulikovman.flickrviewer.models.FlickrResponse;
import ru.kulikovman.flickrviewer.models.GalleryItem;
import ru.kulikovman.flickrviewer.models.Photo;

public class PhotoGalleryActivity extends AppCompatActivity {
    private static final String TAG = "PhotoGalleryActivity";

    private static final String RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final String API_KEY = "92cc75b96a9f82a32bc29eb21a254fe4";
    private static final String FORMAT = "json";
    private static final String NOJSONCALLBACK = "1";
    private static final String SIZE_URL = "url_n";

    private RecyclerView mRecyclerView;
    private PhotoAdapter mPhotoAdapter;
    private List<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<PhotoAdapter.PhotoHolder> mThumbnailDownloader;

    private List<Photo> mPhotoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        // Инициализация RecyclerView
        mRecyclerView = findViewById(R.id.photo_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setHasFixedSize(true);

        setupAdapter();

        // Получение списка фотографий
        //updateItems();


        App.getApi().getRecent(RECENTS_METHOD, API_KEY, FORMAT, NOJSONCALLBACK, SIZE_URL, 15, 1)
                .enqueue(new Callback<FlickrResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<FlickrResponse> call, @NonNull Response<FlickrResponse> response) {
                        if (response.isSuccessful()) {
                            //Log.d(TAG, "response " + response.body().getPhotos().getPhoto().size());
                            Log.d(TAG, "Запрос прошел успешно: " + response.code());
                        } else {
                            Log.d(TAG, "Неудача: " + response.code());
                        }

                /*List<FlickrResponse> photos = response.body();

                if (photos == null) {
                    Log.d(TAG, "photosResponses == null");
                } else {
                    Log.d(TAG, "Количество объектов: " + photos.size());
                }*/

                /*if (response.body() != null) {

                    //mPhotoList.addAll(photosResponses);

                    setupAdapter();
                }*/

                        //Log.d(TAG, "Что-то не сработало...");
                    }

                    @Override
                    public void onFailure(@NonNull Call<FlickrResponse> call, @NonNull Throwable t) {
                        Log.d(TAG, "An error occurred during networking");
                        Log.d(TAG, "Описание ошибки: " + t.getMessage());
                        Log.d(TAG, "Описание ошибки: " + t.toString());
                    }
                });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /*// Отключения загрузчика миниатюр
        mThumbnailDownloader.clearQueue();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");*/
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
                updateItems();
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
                QueryPreferences.setStoredQuery(this, null);
                updateItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateItems() {
        String query = QueryPreferences.getStoredQuery(this);
        new FetchItemsTask(query).execute();
    }

    private void setupAdapter() {
        if (mPhotoAdapter == null) {
            mPhotoAdapter = new PhotoAdapter(this, mItems, mThumbnailDownloader);
            mRecyclerView.setAdapter(mPhotoAdapter);
        } else {
            mPhotoAdapter.setGalleryItems(mItems);
            mPhotoAdapter.notifyDataSetChanged();
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {
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
    }
}
