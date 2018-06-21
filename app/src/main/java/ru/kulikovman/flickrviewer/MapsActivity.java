package ru.kulikovman.flickrviewer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.kulikovman.flickrviewer.models.location.LocationResponse;
import ru.kulikovman.flickrviewer.models.photo.Photo;
import ru.kulikovman.flickrviewer.models.photo.PhotoResponse;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        OnCameraMoveListener, OnCameraIdleListener {

    private final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private List<Photo> mPhotoList;
    private double mLat;
    private double mLon;

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
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveListener(this);

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

        // Получение иекущих координат
        getCurrentLocation();

        // Открытие карты по текущим координатам
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLat, mLon), 10.0f));

        // Фото вокруг стартовой точки координат
        getPhotoByGeo(50, mLat, mLon, 10);

    }

    @Override
    public void onCameraMove() {
        Log.d(TAG, "onCameraMove");
    }

    @Override
    public void onCameraIdle() {
        Log.d(TAG, "onCameraIdle");

        // Обновляем координаты
        CameraPosition position = mMap.getCameraPosition();
        mLat = position.target.latitude;
        mLon = position.target.longitude;

        // Загружаем новые фото
        getPhotoByGeo(30, mLat, mLon, 5);
    }

    public void getPhotoByGeo(int perPage, double lat, double lon, int radius) {
        App.getApi().getSearchByGeo(getString(R.string.search_method), getString(R.string.api_key),
                getString(R.string.format), getString(R.string.nojsoncallback),
                getString(R.string.size_url_s), perPage, lat, lon, radius)
                .enqueue(new Callback<PhotoResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PhotoResponse> call, @NonNull Response<PhotoResponse> response) {
                        if (response.isSuccessful()) {
                            try {
                                mPhotoList = response.body().getPhotos().getPhoto();
                            } catch (Exception ignored) {
                            }

                            if (mPhotoList != null) {
                                // Получаем координаты фотографий
                                for (Photo photo : mPhotoList) {
                                    getPhotoLocation(photo.getId(), photo.getTitle(), photo.getUrlS());
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

    private void getPhotoLocation(String photoId, final String title, final String imageUrl) {
        App.getApi().getPhotoLocation(getString(R.string.location_method), getString(R.string.api_key),
                getString(R.string.format), getString(R.string.nojsoncallback), photoId)
                .enqueue(new Callback<LocationResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<LocationResponse> call, @NonNull Response<LocationResponse> response) {
                        if (response.isSuccessful()) {
                            try {
                                mLat = Double.parseDouble(response.body().getPhoto().getLocation().getLatitude());
                                mLon = Double.parseDouble(response.body().getPhoto().getLocation().getLongitude());
                            } catch (Exception ignored) {
                                mLat = 0;
                                mLon = 0;
                            }

                            if (mLat != 0 && mLon != 0) {
                                Log.d(TAG, "Координаты: " + mLat + " | " + mLon + " | " + imageUrl);

                                // Получаем картинку и создаем маркер на карте
                                Picasso.get()
                                        .load(imageUrl)
                                        .resize(150, 150)
                                        .centerCrop()
                                        .into(new Target() {
                                            @Override
                                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                mMap.addMarker(new MarkerOptions()
                                                        .position(new LatLng(mLat, mLon))
                                                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                                        .title(title)
                                                );
                                            }

                                            @Override
                                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                            }

                                            @Override
                                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                                            }
                                        });
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

    public void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager != null ? locationManager.getBestProvider(new Criteria(), true) : null;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 0);
        }

        Location location = locationManager != null ? locationManager.getLastKnownLocation(provider) : null;

        if (location != null) {
            mLat = location.getLatitude();
            mLon = location.getLongitude();
            Log.d(TAG, "lat | lon = " + mLat + " | " + mLon);
        }
    }



}
