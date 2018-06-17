package ru.kulikovman.flickrviewer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.kulikovman.flickrviewer.models.FlickrResponse;
import ru.kulikovman.flickrviewer.models.GalleryItem;
import ru.kulikovman.flickrviewer.models.Photo;

public class FlickrFetcher {
    private static final String TAG = "FlickrFetcher";

    private static final String API_KEY = "92cc75b96a9f82a32bc29eb21a254fe4";
    private static final String RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final String FORMAT = "json";
    private static final int NOJSONCALLBACK = 1;
    private static final String SIZE_URL = "url_n";
    private static final int PER_PAGE = 60;

    private static FlickrFetcher sFlickrFetcher;
    private RealmHelper mRealmHelper;
    private Realm mRealm;
    private Context mContext;

    public static FlickrFetcher get(Context context) {
        if (sFlickrFetcher == null) {
            sFlickrFetcher = new FlickrFetcher(context);
        }
        return sFlickrFetcher;
    }

    FlickrFetcher(Context context) {
        mContext = context;
        mRealm = Realm.getDefaultInstance();
        mRealmHelper = RealmHelper.get();
    }

    public void loadPhoto() {
        getRecentPhoto(1);
    }

    public void loadPhoto(int page) {
        getRecentPhoto(page);
    }

    public void loadPhoto(String searchQuery) {
        if (searchQuery == null) {
            getRecentPhoto(1);
        } else {
            getSearchPhoto(searchQuery, 1);
        }
    }

    public void loadPhoto(String searchQuery, int page) {
        if (searchQuery == null) {
            getRecentPhoto(page);
        } else {
            getSearchPhoto(searchQuery, page);
        }
    }

    public void getRecentPhoto(int page) {
        App.getApi().getRecent(RECENTS_METHOD, API_KEY, FORMAT, NOJSONCALLBACK, PER_PAGE, SIZE_URL, page)
                .enqueue(new Callback<FlickrResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<FlickrResponse> call, @NonNull Response<FlickrResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                // Добавляем новые фото в список
                                for (Photo photo : response.body().getPhotos().getPhoto()) {
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
                        } else {
                            Log.d(TAG, "Запрос прошел, но что-то пошло не так: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<FlickrResponse> call, @NonNull Throwable t) {
                        Toast.makeText(mContext, "Error with internet connection", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Error with internet connection: " + t.getMessage());
                    }
                });
    }

    private void getSearchPhoto(String searchQuery, int page) {
        App.getApi().getSearch(SEARCH_METHOD, API_KEY, FORMAT, NOJSONCALLBACK, PER_PAGE, SIZE_URL, page, searchQuery)
                .enqueue(new Callback<FlickrResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<FlickrResponse> call, @NonNull Response<FlickrResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                // Добавляем новые фото в список
                                for (Photo photo : response.body().getPhotos().getPhoto()) {
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
                        } else {
                            Log.d(TAG, "Запрос прошел, но что-то пошло не так: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<FlickrResponse> call, @NonNull Throwable t) {
                        Toast.makeText(mContext, "Error with internet connection", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Error with internet connection: " + t.getMessage());
                    }
                });
    }


    public byte[] getUrlBytes(String urlSpec) throws IOException {
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

    private List<GalleryItem> downloadGalleryItems(String url) {
        List<GalleryItem> items = new ArrayList<>();

        try {
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);

            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }

        return items;
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws IOException, JSONException {
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));

            if (!photoJsonObject.has("url_n")) {
                continue;
            }

            item.setUrl(photoJsonObject.getString("url_n"));
            items.add(item);
        }
    }
}
