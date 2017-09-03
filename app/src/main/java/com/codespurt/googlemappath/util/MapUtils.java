package com.codespurt.googlemappath.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.codespurt.googlemappath.R;
import com.codespurt.googlemappath.engine.callbacks.NetworkCallback;
import com.codespurt.googlemappath.model.DirectionObject;
import com.codespurt.googlemappath.model.LegsObject;
import com.codespurt.googlemappath.model.PolylineObject;
import com.codespurt.googlemappath.model.RouteObject;
import com.codespurt.googlemappath.model.StepsObject;
import com.codespurt.googlemappath.sync.requests.GetDirections;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CodeSpurt on 01-09-2017.
 */

public class MapUtils {

    private String TAG = MapUtils.class.getSimpleName();

    private Context context;
    private GoogleMap mMap;

    private ProgressDialog progressDialog;
    private String progressDialogMessage = "Fetching Directions...";

    private int MAP_ZOOM_LEVEL = 11;

    // custom marker image
    private final int MARKER_DEFAULT = 1;
    private final int MARKER_DRIVER = 2;

    public MapUtils(Context context) {
        this.context = context;
    }

    public LatLng getCurrentLocation(GoogleApiClient googleApiClient, GoogleMap mMap) {
        LatLng currentLatLng = null;
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (mLastLocation != null) {
            currentLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            setMarker(mMap, currentLatLng, context.getResources().getString(R.string.current_location), MARKER_DEFAULT, true);
        }
        return currentLatLng;
    }

    public void setCurrentLocation(GoogleApiClient googleApiClient, GoogleMap mMap) {
        LatLng currentLatLng = null;
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (mLastLocation != null) {
            currentLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            setMarker(mMap, currentLatLng, context.getResources().getString(R.string.current_location), MARKER_DEFAULT, true);
        }
    }

    public void setMarker(GoogleMap mMap, LatLng latLng, String title, int marker, boolean moveToPosition) {
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        if (title != null || !title.trim().equals("")) {
            options.title(title);
        }
        switch (marker) {
            case MARKER_DEFAULT:
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                break;
            case MARKER_DRIVER:
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.image_driver));
                break;
        }
        mMap.addMarker(options);
        if (moveToPosition) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latLng.latitude, latLng.longitude))
                    .zoom(MAP_ZOOM_LEVEL)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void drawPath(GoogleMap mMap) {
        this.mMap = mMap;
        showProgressDialog();
        mMap.clear();
        new GetDirections(callback);
    }

    NetworkCallback callback = new NetworkCallback() {
        @Override
        public void valid(DirectionObject response) {
            progressDialog.dismiss();
            try {
                if (response.getStatus().equals("OK")) {
                    List<LatLng> directions = getDirectionPolylines(response.getRoutes());
                    if (mMap != null) {
                        drawRouteOnMap(directions);
                    }
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.unable_to_connect_to_google_maps), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void error(int statusCode) {
            progressDialog.dismiss();
            Toast.makeText(context, context.getResources().getString(R.string.unable_to_connect_to_google_maps), Toast.LENGTH_SHORT).show();
        }
    };

    private List<LatLng> getDirectionPolylines(List<RouteObject> routes) {
        List<LatLng> directionList = new ArrayList<>();
        for (RouteObject route : routes) {
            List<LegsObject> legs = route.getLegs();
            for (LegsObject leg : legs) {
                List<StepsObject> steps = leg.getSteps();
                for (StepsObject step : steps) {
                    PolylineObject polyline = step.getPolyline();
                    String points = polyline.getPoints();
                    List<LatLng> singlePolyline = decodePoly(points);
                    for (LatLng direction : singlePolyline) {
                        directionList.add(direction);
                    }
                }
            }
        }
        return directionList;
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    private void drawRouteOnMap(List<LatLng> positions) {
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        options.addAll(positions);
        Polyline polyline = mMap.addPolyline(options);
        // set marker at source location
        setMarker(mMap, new LatLng(positions.get(1).latitude, positions.get(1).longitude), context.getResources().getString(R.string.source), MARKER_DRIVER, true);
        // set marker at destination location
        setMarker(mMap, new LatLng(positions.get(positions.size() - 1).latitude, positions.get(positions.size() - 1).longitude), context.getResources().getString(R.string.destination), MARKER_DEFAULT, false);
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(progressDialogMessage);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
}
