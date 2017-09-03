package com.codespurt.googlemappath;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codespurt.googlemappath.util.Alerts;
import com.codespurt.googlemappath.util.MapUtils;
import com.codespurt.googlemappath.util.Preferences;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static String TAG = "GoogleMapPath";

    private Alerts alerts;
    private MapUtils mapUtils;
    private Preferences preferences;

    private Button selectSource, selectDestination, drawPath;

    private final int GPS_PERMISSION_CODE = 1001;

    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE = 1;
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION = 2;

    private GoogleMap mMap;

    // for getting location
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private LatLng currentLatLng, sourceLatLng, destinationLatLng;
    private Marker currentLocationMarker, sourceLocationMarker, destinationLocationMarker;

    private List<LatLng> latLngList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectSource = (Button) findViewById(R.id.select_source);
        selectDestination = (Button) findViewById(R.id.select_destination);
        drawPath = (Button) findViewById(R.id.draw_path);

        alerts = new Alerts(this);
        mapUtils = new MapUtils(this);
        preferences = new Preferences(this);
        latLngList = new ArrayList<>();

        if (alerts.isGooglePlayServicesAvailable()) {
            // check permissions
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // request GPS permission
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, GPS_PERMISSION_CODE);
            }

            // setup google map fragment
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
            mapFragment.getMapAsync(this);

            selectSource.setOnClickListener(this);
            selectDestination.setOnClickListener(this);
            drawPath.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select_source:
                if (alerts.isNetworkAvailable()) {
                    try {
                        // use MODE_FULLSCREEN flag for fullscreen
                        Intent sourceIntent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
                        startActivityForResult(sourceIntent, PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.select_destination:
                if (alerts.isNetworkAvailable()) {
                    try {
                        // use MODE_FULLSCREEN flag for fullscreen
                        Intent destinationIntent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
                        startActivityForResult(destinationIntent, PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.draw_path:
                if (preferences.get(Preferences.SOURCE_LOCATION_LATITUDE) == null ||
                        preferences.get(Preferences.SOURCE_LOCATION_LONGITUDE) == null) {
                    Toast.makeText(this, getResources().getString(R.string.source_empty), Toast.LENGTH_SHORT).show();
                } else if (preferences.get(Preferences.DESTINATION_LOCATION_LATITUDE) == null ||
                        preferences.get(Preferences.DESTINATION_LOCATION_LONGITUDE) == null) {
                    Toast.makeText(this, getResources().getString(R.string.destination_empty), Toast.LENGTH_SHORT).show();
                } else if (alerts.isNetworkAvailable()) {
                    // draw path
                    mapUtils.drawPath(mMap);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE:
                    Place source = PlaceAutocomplete.getPlace(this, data);
                    Log.d(TAG, "Source: " + source.getName());
                    sourceLatLng = source.getLatLng();
                    preferences.save(Preferences.SOURCE_LOCATION_LATITUDE, Preferences.SOURCE_LOCATION_LONGITUDE, sourceLatLng);
                    break;
                case PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION:
                    Place destination = PlaceAutocomplete.getPlace(this, data);
                    Log.d(TAG, "Destination: " + destination.getName());
                    destinationLatLng = destination.getLatLng();
                    preferences.save(Preferences.DESTINATION_LOCATION_LATITUDE, Preferences.DESTINATION_LOCATION_LONGITUDE, destinationLatLng);
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case GPS_PERMISSION_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                        mapUtils.setCurrentLocation(googleApiClient, mMap);
                    }
                } else {
                    Toast.makeText(this, getResources().getString(R.string.permission_required), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // check if GPS permission already granted
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        // map ui settings
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        buildGoogleApiClient();

        googleApiClient.connect();
    }

    // GoogleApiClient.ConnectionCallbacks
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mapUtils.setCurrentLocation(googleApiClient, mMap);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    // GoogleApiClient.OnConnectionFailedListener
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, R.string.unable_to_connect_to_google_maps, Toast.LENGTH_SHORT).show();
    }

    // LocationListener
    @Override
    public void onLocationChanged(Location location) {

    }

    private void setLocationRequestParams() {
        locationRequest = new LocationRequest();
        // 30 seconds
        locationRequest.setInterval(30 * 1000);
        // 15 seconds
        locationRequest.setFastestInterval(15 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        // 0.1 meter or 10 cm
        locationRequest.setSmallestDisplacement(0.1F);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        selectSource.setOnClickListener(null);
        selectDestination.setOnClickListener(null);
        drawPath.setOnClickListener(null);
    }
}