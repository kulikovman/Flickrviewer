package ru.kulikovman.flickrviewer;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.kulikovman.flickrviewer.adapters.PhotoAdapter;
import ru.kulikovman.flickrviewer.models.GalleryItem;

public class PhotoGalleryActivity extends AppCompatActivity {
    private static final String TAG = "PhotoGalleryActivity";

    private RecyclerView mRecyclerView;
    private PhotoAdapter mPhotoAdapter;
    private List<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<PhotoAdapter.PhotoHolder> mThumbnailDownloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        // Запуск фонового загрузчика миниатюр
        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoAdapter.PhotoHolder>() {
            @Override
            public void onThumbnailDownloaded(PhotoAdapter.PhotoHolder photoHolder, Bitmap bitmap) {
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                photoHolder.bindDrawable(drawable);
            }
        });

        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");

        // Инициализация RecyclerView
        mRecyclerView = findViewById(R.id.photo_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setHasFixedSize(true);

        setupAdapter();

        // Запуск фоновой задачи
        new FetchItemsTask().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Отключения загрузчика миниатюр
        mThumbnailDownloader.clearQueue();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
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
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            String query = "robot"; // Для тестирования
            if (query == null) {
                return new FlickrFetchr().fetchRecentPhotos();
            } else {
                return new FlickrFetchr().searchPhotos(query);
            }
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            Log.d(TAG, "Список содержит: " + items.size() + " значений");
            mItems = items;
            setupAdapter();
        }
    }
}
