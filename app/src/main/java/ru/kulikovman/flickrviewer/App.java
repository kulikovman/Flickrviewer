package ru.kulikovman.flickrviewer;

import android.app.Application;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {
    private static FlickrApi mFlickrApi;
    private Retrofit mRetrofit;

    @Override
    public void onCreate() {
        super.onCreate();

        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.flickr.com/services/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mFlickrApi = mRetrofit.create(FlickrApi.class);
    }

    public static FlickrApi getApi() {
        return mFlickrApi;
    }
}
