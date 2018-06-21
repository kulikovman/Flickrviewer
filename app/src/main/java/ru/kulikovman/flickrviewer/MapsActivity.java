package ru.kulikovman.flickrviewer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.kulikovman.flickrviewer.models.location.Location;
import ru.kulikovman.flickrviewer.models.location.LocationResponse;
import ru.kulikovman.flickrviewer.models.location.PhotoWithLocation;
import ru.kulikovman.flickrviewer.models.photo.PhotoResponse;
import ru.kulikovman.flickrviewer.models.photo.Photo;
import ru.kulikovman.flickrviewer.models.photo.Photos;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private UiSettings mUiSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mUiSettings = mMap.getUiSettings();

        // Включение кнопок масштаба и текущего местоположения
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);

        // Проверка разрешений на определение текущего местонахождения
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Запрос если их нет
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 0);
        }
        mMap.setMyLocationEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        // Здесь нужно получить тестовые фото в районе Сиднея
        getPhotoByGeo(10, -34, 151);

    }

    public void getPhotoByGeo(int perPage, double lat, double lon) {
        App.getApi().getSearchByGeo(getString(R.string.search_method), getString(R.string.api_key),
                getString(R.string.format), getString(R.string.nojsoncallback),
                getString(R.string.size_url_s), perPage, lat, lon)
                .enqueue(new Callback<PhotoResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PhotoResponse> call, @NonNull Response<PhotoResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                PhotoResponse photoResponse = response.body();
                                if (photoResponse != null) {
                                    Photos photos = photoResponse.getPhotos();
                                    if (photos != null) {
                                        List<Photo> photoList = photos.getPhoto();

                                        // Для каждой фотки получаем координаты и ставим маркер на карте
                                        for (Photo photo : photoList) {
                                            Log.d(TAG, "Получено фото: " + photo.getId() + " | " + photo.getTitle());
                                            getPhotoLocation(photo.getId(), photo.getUrlN());
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "Response is not successful: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PhotoResponse> call, @NonNull Throwable t) {
                        showErrorToast(t);
                    }
                });
    }

    private void getPhotoLocation(String photoId, String imageUrl) {
        App.getApi().getPhotoLocation(getString(R.string.location_method), getString(R.string.api_key),
                getString(R.string.format), getString(R.string.nojsoncallback), photoId)
                .enqueue(new Callback<LocationResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<LocationResponse> call, @NonNull Response<LocationResponse> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Response is successful: " + response.code());
                            if (response.body() != null) {
                                Log.d(TAG, "response.body() != null");
                                LocationResponse locationResponse = response.body();
                                if (locationResponse != null) {
                                    Log.d(TAG, "locationResponse != null");
                                    PhotoWithLocation photoWithLocation = locationResponse.getPhotoWithLocation();
                                    if (photoWithLocation != null) {
                                        Log.d(TAG, "photoWithLocation != null");
                                        Location location = photoWithLocation.getLocation();
                                        if (location != null) {
                                            Log.d(TAG, "location != null");
                                            String lat = location.getLatitude();
                                            String lon = location.getLongitude();
                                            if (lat != null && lon != null) {
                                                Log.d(TAG, "lat != null && lon != null");
                                                Log.d(TAG, "Координаты: " + lat + " | " + lon);
                                            }
                                        }
                                    }
                                }

                            }
                        } else {
                            Log.d(TAG, "Response is not successful: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<LocationResponse> call, @NonNull Throwable t) {
                        showErrorToast(t);
                    }
                });
    }

    private void showErrorToast(@NonNull Throwable t) {
        Toast.makeText(MapsActivity.this, "Error with internet connection", Toast.LENGTH_LONG).show();
        Log.d(TAG, "Error with internet connection: " + t.getMessage());
    }
}
