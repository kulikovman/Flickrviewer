package ru.kulikovman.flickrviewer;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

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
import ru.kulikovman.flickrviewer.models.PhotoPreview;

public class FlickrFetchr {
    private static final String TAG = "FlickrFetchr";

    private static final String API_KEY = "92cc75b96a9f82a32bc29eb21a254fe4";
    private static final String RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final String FORMAT = "json";
    private static final int NOJSONCALLBACK = 1;
    private static final String SIZE_URL = "url_n";
    private static final int PER_PAGE = 60;








    private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    private static final Uri ENDPOINT = Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("per_page", "100")
            .appendQueryParameter("page", "1")
            .appendQueryParameter("extras", "url_n")
            .build();


    private static FlickrFetchr sFlickrFetchr;
    private RealmHelper mRealmHelper;
    private Realm mRealm;

    public static FlickrFetchr get() {
        if (sFlickrFetchr == null) {
            sFlickrFetchr = new FlickrFetchr();
        }
        return sFlickrFetchr;
    }

    FlickrFetchr() {
        mRealm = Realm.getDefaultInstance();
        mRealmHelper = RealmHelper.get();
    }

    public void loadRecentPhoto(int page) {
        App.getApi().getRecent(RECENTS_METHOD, API_KEY, FORMAT, NOJSONCALLBACK, PER_PAGE, page, SIZE_URL)
                .enqueue(new Callback<FlickrResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<FlickrResponse> call, @NonNull Response<FlickrResponse> response) {
                        //mProgressBar.setVisibility(View.GONE);

                        if (response.isSuccessful()) {
                            Log.d(TAG, "Запрос прошел успешно: " + response.code());
                            if (response.body() != null) {
                                // Добавляем новые фото в список
                                for (Photo photo : response.body().getPhotos().getPhoto()) {
                                    // Если есть ссылка на миниатюру
                                    if (photo.getUrlN() != null) {
                                        PhotoPreview preview = new PhotoPreview(photo.getId());
                                        preview.setTitle(photo.getTitle());
                                        preview.setUrl(photo.getUrlN());

                                        // Сохраняем объект в базу
                                        if (!mRealmHelper.isExistUrl(preview.getUrl())) {
                                            mRealm.beginTransaction();
                                            mRealm.insert(preview);
                                            mRealm.commitTransaction();
                                        } else {
                                            Log.d(TAG, "Такая картинка уже есть в базе: " + preview.getUrl());
                                        }

                                        // Добавляем в старый список
                                        //mPhotoList.add(photo);
                                    }
                                }

                                RealmResults<PhotoPreview> previews = mRealm.where(PhotoPreview.class).findAll();
                                Log.d(TAG, "Объектов в базе: " + previews.size());

                                //setUpPhotoRecyclerView();
                            }
                        } else {
                            Log.d(TAG, "Запрос прошел, но что-то пошло не так: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<FlickrResponse> call, @NonNull Throwable t) {
                        Log.d(TAG, "Ошибка при отправке запроса: " + t.getMessage());
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

    public List<GalleryItem> fetchRecentPhotos() {
        String url = buildUrl(FETCH_RECENTS_METHOD, null);
        return downloadGalleryItems(url);
    }

    public List<GalleryItem> searchPhotos(String query) {
        String url = buildUrl(SEARCH_METHOD, query);
        return downloadGalleryItems(url);
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

    private String buildUrl(String method, String query) {
        Uri.Builder uriBuilder = ENDPOINT.buildUpon().appendQueryParameter("method", method);

        if (method.equals(SEARCH_METHOD)) {
            uriBuilder.appendQueryParameter("text", query);
        }
        return uriBuilder.build().toString();
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
