package ru.kulikovman.flickrviewer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.kulikovman.flickrviewer.models.FlickrResponse;

public interface FlickrApi {

    // Пример запроса:
    // https://api.flickr.com/services/rest/
    // ?method=flickr.photos.getRecent
    // &api_key=92cc75b96a9f82a32bc29eb21a254fe4
    // &format=json
    // &nojsoncallback=1
    // &per_page=15
    // &page=1
    // &extras=url_n

    @GET("rest/")
    Call<FlickrResponse> getRecent(@Query("method") String method,
                                   @Query("api_key") String api_key,
                                   @Query("format") String format,
                                   @Query("nojsoncallback") String nojsoncallback,
                                   @Query("per_page") int per_page,
                                   @Query("page") int page,
                                   @Query("extras") String size_url);

    @GET("rest/")
    Call<FlickrResponse> getSearch(@Query("method") String method,
                                   @Query("api_key") String apiKey,
                                   @Query("format") String format,
                                   @Query("nojsoncallback") String set,
                                   @Query("extras") String sizeUrl,
                                   @Query("text") String searchTerm);
}
