package ru.kulikovman.flickrviewer;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.IOException;

import ru.kulikovman.flickrviewer.adapters.PhotoAdapter;

public class PhotoGalleryActivity extends AppCompatActivity {
    private static final String TAG = "log";

    private RecyclerView mRecyclerView;
    private PhotoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        // Инициализация вью элементов
        mRecyclerView = findViewById(R.id.photo_recycler_view);

        initRecyclerView();

        // Запуск фоновой задачи
        new FetchItemsTask().execute();

    }

    private void initRecyclerView() {
        mAdapter = new PhotoAdapter();
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(mAdapter);
    }

    private class FetchItemsTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            new FlickrFetchr().fetchItems();
            return null;
        }
    }
}
