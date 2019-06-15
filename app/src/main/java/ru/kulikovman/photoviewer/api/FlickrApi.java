package ru.kulikovman.photoviewer.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.kulikovman.photoviewer.models.location.LocationResponse;
import ru.kulikovman.photoviewer.models.photo.PhotoResponse;

public interface FlickrApi {

    // https://api.flickr.com/services/rest/?method=flickr.photos.getPopular&api_key=92cc75b96a9f82a32bc29eb21a254fe4&format=json&nojsoncallback=1&extras=url_n&per_page=20&page=1

    @GET("rest/")
    Call<PhotoResponse> getRecent(@Query("method") String method,
                                  @Query("api_key") String api_key,
                                  @Query("format") String format,
                                  @Query("nojsoncallback") String nojsoncallback,
                                  @Query("extras") String size_url,
                                  @Query("per_page") int per_page,
                                  @Query("page") int page
    );

    @GET("rest/")
    Call<PhotoResponse> getSearch(@Query("method") String method,
                                  @Query("api_key") String api_key,
                                  @Query("format") String format,
                                  @Query("nojsoncallback") String nojsoncallback,
                                  @Query("extras") String size_url,
                                  @Query("per_page") int per_page,
                                  @Query("page") int page,
                                  @Query("text") String searchQuery
    );

    @GET("rest/")
    Call<PhotoResponse> getSearchByGeo(@Query("method") String method,
                                       @Query("api_key") String api_key,
                                       @Query("format") String format,
                                       @Query("nojsoncallback") String nojsoncallback,
                                       @Query("extras") String size_url,
                                       @Query("per_page") int per_page,
                                       @Query("lat") double lat,
                                       @Query("lon") double lon,
                                       @Query("radius") int radius
    );

    @GET("rest/")
    Call<LocationResponse> getPhotoLocation(@Query("method") String method,
                                            @Query("api_key") String api_key,
                                            @Query("format") String format,
                                            @Query("nojsoncallback") String nojsoncallback,
                                            @Query("photo_id") String photo_id
    );
}
