package ru.kulikovman.flickrviewer;

import android.os.AsyncTask;
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
    private static final String TAG = "log";

    private RecyclerView mRecyclerView;
    private PhotoAdapter mPhotoAdapter;
    private List<GalleryItem> mItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        // Инициализация RecyclerView
        mRecyclerView = findViewById(R.id.photo_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setHasFixedSize(true);

        setupAdapter();

        // Запуск фоновой задачи
        new FetchItemsTask().execute();

    }

    private void setupAdapter() {
        if (mPhotoAdapter == null) {
            mPhotoAdapter = new PhotoAdapter(mItems);
            mRecyclerView.setAdapter(mPhotoAdapter);
        } else {
            mPhotoAdapter.setGalleryItems(mItems);
            mPhotoAdapter.notifyDataSetChanged();
        }
    }

    private class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            new FlickrFetchr().fetchItems();
            return new FlickrFetchr().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            Log.d(TAG, "Список содержит: " + items.size() + " значений");
            mItems = items;
            setupAdapter();
        }
    }
}
