package ru.kulikovman.flickrviewer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.kulikovman.flickrviewer.models.FlickrResponse;
import ru.kulikovman.flickrviewer.models.Photo;
import ru.kulikovman.flickrviewer.models.PhotoLocation;
import ru.kulikovman.flickrviewer.models.Photos;

public class FlickrFetcher {
    private final String TAG = "FlickrFetcher";

    private final String API_KEY = "92cc75b96a9f82a32bc29eb21a254fe4";
    private final String RECENTS_METHOD = "flickr.photos.getRecent";
    private final String SEARCH_METHOD = "flickr.photos.search";
    private final String LOCATION_METHOD = "flickr.photos.geo.getLocation";
    private final String FORMAT = "json";
    private final int NOJSONCALLBACK = 1;
    private final String SIZE_URL_N = "url_n";
    private final String SIZE_URL_S = "url_s";
    private final int PER_PAGE = 60;

    private RealmHelper mRealmHelper;
    private Realm mRealm;
    private Context mContext;
    private List<Photo> mPhotos;
    private LinearLayout mProgressBarContainer;

    FlickrFetcher(Context context, LinearLayout progressBarContainer) {
        mContext = context;
        mProgressBarContainer = progressBarContainer;
        mRealm = Realm.getDefaultInstance();
        mRealmHelper = RealmHelper.get();
    }

    public void loadPhoto() {
        getRecentPhoto(1, false);
    }

    public void loadPhoto(boolean clearData) {
        getRecentPhoto(1, clearData);
    }

    public void loadPhoto(String searchQuery, boolean clearData) {
        if (searchQuery == null) {
            getRecentPhoto(1, clearData);
        } else {
            getSearchPhoto(searchQuery, 1, clearData);
        }
    }

    public void loadPhoto(String searchQuery, int page, boolean clearData) {
        if (searchQuery == null) {
            getRecentPhoto(page, clearData);
        } else {
            getSearchPhoto(searchQuery, page, clearData);
        }
    }

    private void getRecentPhoto(int page, final boolean clearData) {
        App.getApi().getRecent(RECENTS_METHOD, API_KEY, FORMAT, NOJSONCALLBACK, SIZE_URL_N, PER_PAGE, page)
                .enqueue(new Callback<FlickrResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<FlickrResponse> call, @NonNull Response<FlickrResponse> response) {
                        hideProgressBar();
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                FlickrResponse flickrResponse = response.body();
                                if (flickrResponse != null) {
                                    Photos photos = flickrResponse.getPhotos();
                                    if (photos != null) {
                                        mPhotos = photos.getPhoto();
                                    }
                                }

                                putReceivedPhotosInBase(clearData);
                            }
                        } else {
                            Log.d(TAG, "Запрос прошел, но что-то пошло не так: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<FlickrResponse> call, @NonNull Throwable t) {
                        showErrorToast(t);
                    }
                });
    }

    private void getSearchPhoto(String searchQuery, int page, final boolean clearData) {
        App.getApi().getSearch(SEARCH_METHOD, API_KEY, FORMAT, NOJSONCALLBACK, SIZE_URL_N, PER_PAGE, page, searchQuery)
                .enqueue(new Callback<FlickrResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<FlickrResponse> call, @NonNull Response<FlickrResponse> response) {
                        hideProgressBar();
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                FlickrResponse flickrResponse = response.body();
                                if (flickrResponse != null) {
                                    Photos photos = flickrResponse.getPhotos();
                                    if (photos != null) {
                                        mPhotos = photos.getPhoto();
                                    }
                                }

                                putReceivedPhotosInBase(clearData);
                            }
                        } else {
                            Log.d(TAG, "Запрос прошел, но что-то пошло не так: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<FlickrResponse> call, @NonNull Throwable t) {
                        showErrorToast(t);
                    }
                });
    }

    public void getPhotoByGeo(final GoogleMap map, int perPage, double lat, double lon) {
        App.getApi().getSearchGeo(SEARCH_METHOD, API_KEY, FORMAT, NOJSONCALLBACK, SIZE_URL_S, perPage, lat, lon)
                .enqueue(new Callback<FlickrResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<FlickrResponse> call, @NonNull Response<FlickrResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                FlickrResponse flickrResponse = response.body();
                                if (flickrResponse != null) {
                                    Photos photos = flickrResponse.getPhotos();
                                    if (photos != null) {
                                        List<Photo> photoList = photos.getPhoto();

                                        // Для каждой фотки получаем координаты и ставим маркер на карте
                                        for (Photo photo : photoList) {
                                            getPhotoLocation(map, photo.getId());
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "Запрос прошел, но что-то пошло не так: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<FlickrResponse> call, @NonNull Throwable t) {
                        showErrorToast(t);
                    }
                });
    }

    private void getPhotoLocation(GoogleMap map, String photoId) {
        App.getApi().getPhotoLocation(LOCATION_METHOD, API_KEY, FORMAT, NOJSONCALLBACK, photoId)
                .enqueue(new Callback<FlickrResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<FlickrResponse> call, @NonNull Response<FlickrResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {

                            }
                        } else {
                            Log.d(TAG, "Запрос прошел, но что-то пошло не так: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<FlickrResponse> call, @NonNull Throwable t) {
                        showErrorToast(t);
                    }
                });
    }

    private void putReceivedPhotosInBase(boolean clearData) {
        if (mPhotos != null) {
            // Очистка базы
            if (clearData) {
                mRealm.beginTransaction();
                mRealm.deleteAll();
                mRealm.commitTransaction();
            }

            // Добавляем новые фото в список
            for (Photo photo : mPhotos) {
                // Если есть ссылка на миниатюру
                if (photo.getUrlN() != null) {
                    // Если такого фото еще нет, то добавляем в базу
                    if (!mRealmHelper.isExistUrl(photo.getUrlN())) {
                        mRealm.beginTransaction();
                        mRealm.insert(photo);
                        mRealm.commitTransaction();
                    } else {
                        Log.d(TAG, "Такая картинка уже есть в базе: " + photo.getUrlN());
                    }
                }
            }
        }
    }

    private void showErrorToast(@NonNull Throwable t) {
        hideProgressBar();
        Toast.makeText(mContext, "Error with internet connection", Toast.LENGTH_LONG).show();
        Log.d(TAG, "Error with internet connection: " + t.getMessage());
    }

    private void hideProgressBar() {
        mProgressBarContainer.setVisibility(View.INVISIBLE);
    }

    // Старый способ подключения
    private byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();

            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }
}
