package ru.kulikovman.flickrviewer;

import android.net.Uri;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.kulikovman.flickrviewer.models.Photo;

public interface FlickrApi {

    /*static final Uri ENDPOINT = Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", "dfgdfhdfh")
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("per_page", "100")
            .appendQueryParameter("page", "1")
            .appendQueryParameter("extras", "url_n")
            .build();*/

    // https://api.flickr.com/services/rest/
    // ?method=flickr.photos.getRecent
    // &api_key=92cc75b96a9f82a32bc29eb21a254fe4
    // &format=json
    // &nojsoncallback=1
    // &per_page=15
    // &page=1
    // &extras=url_n


    @GET("/rest")
    Call<List<Photo>> getRecent(@Query("method") String method,
                                @Query("api_key") String apiKey,
                                @Query("format") String format,
                                @Query("nojsoncallback") String set,
                                @Query("extras") String sizeUrl);

    @GET("/rest")
    Call<List<Photo>> getSearch(@Query("method") String method,
                                @Query("api_key") String apiKey,
                                @Query("format") String format,
                                @Query("nojsoncallback") String set,
                                @Query("extras") String sizeUrl,
                                @Query("text") String searchTerm);
}
