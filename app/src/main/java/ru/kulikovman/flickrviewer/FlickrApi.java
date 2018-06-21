package ru.kulikovman.flickrviewer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.kulikovman.flickrviewer.models.FlickrResponse;

public interface FlickrApi {

    // Пример запроса:
    // https://api.flickr.com/services/rest/?method=flickr.photos.search
    // &api_key=92cc75b96a9f82a32bc29eb21a254fe4
    // &format=json&nojsoncallback=1&per_page=60&page=1&extras=url_n&text=girl

    @GET("rest/")
    Call<FlickrResponse> getRecent(@Query("method") String method,
                                   @Query("api_key") String api_key,
                                   @Query("format") String format,
                                   @Query("nojsoncallback") String nojsoncallback,
                                   @Query("extras") String size_url,
                                   @Query("per_page") int per_page,
                                   @Query("page") int page
    );

    @GET("rest/")
    Call<FlickrResponse> getSearch(@Query("method") String method,
                                   @Query("api_key") String api_key,
                                   @Query("format") String format,
                                   @Query("nojsoncallback") String nojsoncallback,
                                   @Query("extras") String size_url,
                                   @Query("per_page") int per_page,
                                   @Query("page") int page,
                                   @Query("text") String searchQuery
    );


    // Пример запроса:
    // https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=92cc75b96a9f82a32bc29eb21a254fe4&format=json&nojsoncallback=1&extras=url_s&per_page=10&lat=37.7994&lon=122.3950

    @GET("rest/")
    Call<FlickrResponse> getSearchByGeo(@Query("method") String method,
                                        @Query("api_key") String api_key,
                                        @Query("format") String format,
                                        @Query("nojsoncallback") String nojsoncallback,
                                        @Query("extras") String size_url,
                                        @Query("per_page") int per_page,
                                        @Query("lat") double lat,
                                        @Query("lon") double lon
    );

    // Пример запроса:
    // https://api.flickr.com/services/rest/?method=flickr.photos.geo.getLocation&api_key=92cc75b96a9f82a32bc29eb21a254fe4&format=json&nojsoncallback=1&photo_id=27807466577
    // id = 27807466577

    @GET("rest/")
    Call<FlickrResponse> getPhotoLocation(@Query("method") String method,
                                          @Query("api_key") String api_key,
                                          @Query("format") String format,
                                          @Query("nojsoncallback") String nojsoncallback,
                                          @Query("photo_id") String photo_id
    );
}
