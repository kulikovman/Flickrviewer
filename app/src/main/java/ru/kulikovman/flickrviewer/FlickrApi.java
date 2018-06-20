package ru.kulikovman.flickrviewer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.kulikovman.flickrviewer.models.FlickrResponse;

public interface FlickrApi {

    // Пример Flickr запроса миниатюры:
    // https://api.flickr.com/services/rest/?method=flickr.photos.search
    // &api_key=92cc75b96a9f82a32bc29eb21a254fe4
    // &format=json&nojsoncallback=1&per_page=60&page=1&extras=url_n&text=girl

    // https://api.flickr.com/services/rest/?method=flickr.photos.getSizes&api_key=92cc75b96a9f82a32bc29eb21a254fe4&format=json&nojsoncallback=1&photo_id=27983986247


    @GET("rest/")
    Call<FlickrResponse> getRecent(@Query("method") String method,
                                   @Query("api_key") String api_key,
                                   @Query("format") String format,
                                   @Query("nojsoncallback") int nojsoncallback,
                                   @Query("extras") String size_url,
                                   @Query("per_page") int per_page,
                                   @Query("page") int page);

    @GET("rest/")
    Call<FlickrResponse> getSearch(@Query("method") String method,
                                   @Query("api_key") String api_key,
                                   @Query("format") String format,
                                   @Query("nojsoncallback") int nojsoncallback,
                                   @Query("extras") String size_url,
                                   @Query("per_page") int per_page,
                                   @Query("page") int page,
                                   @Query("text") String searchQuery);


    // https://api.flickr.com/services/rest/?method=flickr.photos.getSizes&api_key=92cc75b96a9f82a32bc29eb21a254fe4&format=json&nojsoncallback=1&photo_id=27983986247

    @GET("rest/")
    Call<FlickrResponse> getSearchGeo(@Query("method") String method,
                                      @Query("api_key") String api_key,
                                      @Query("format") String format,
                                      @Query("nojsoncallback") int nojsoncallback,
                                      @Query("extras") String size_url,
                                      @Query("per_page") int per_page,
                                      @Query("page") int page,
                                      @Query("text") String searchQuery);
}
