package ru.kulikovman.photoviewer;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import io.realm.Realm;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.kulikovman.photoviewer.api.FlickrApi;

public class App extends Application {
    private static Application sInstance;
    private static FlickrApi sFlickrApi;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("log", "Запущен onCreate в App");

        // Инициализируем базу данных
        Realm.init(this);

        // Запускаем логирование трафика
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        // Инициализируем Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.flickr.com/services/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        sFlickrApi = retrofit.create(FlickrApi.class);
    }

    public static FlickrApi getApi() {
        return sFlickrApi;
    }

    public static Context getContext() {
        return sInstance.getApplicationContext();
    }
}
