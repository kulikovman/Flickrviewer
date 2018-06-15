package ru.kulikovman.flickrviewer;

import android.net.Uri;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.kulikovman.flickrviewer.models.Photo;
import ru.kulikovman.flickrviewer.models.PhotosResponse;

public interface FlickrApi {

    // https://api.flickr.com/services/rest/
    // ?method=flickr.photos.getRecent
    // &api_key=92cc75b96a9f82a32bc29eb21a254fe4
    // &format=json
    // &nojsoncallback=1
    // &per_page=15
    // &page=1
    // &extras=url_n


    @GET("rest/")
    Call<PhotosResponse> getRecent(@Query("method") String method,
                                   @Query("api_key") String apiKey,
                                   @Query("format") String format,
                                   @Query("nojsoncallback") String set,
                                   @Query("extras") String sizeUrl,
                                   @Query("per_page") int perPage,
                                   @Query("page") int page);

    @GET("/rest")
    Call<List<PhotosResponse>> getSearch(@Query("method") String method,
                                         @Query("api_key") String apiKey,
                                         @Query("format") String format,
                                         @Query("nojsoncallback") String set,
                                         @Query("extras") String sizeUrl,
                                         @Query("text") String searchTerm);
}
