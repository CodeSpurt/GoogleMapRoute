package com.codespurt.googlemappath.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.codespurt.googlemappath.R;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by CodeSpurt on 03-09-2017.
 */

public class Preferences {

    // params to be saved
    public static String SOURCE_LOCATION_LATITUDE = "source_location_latitude";
    public static String SOURCE_LOCATION_LONGITUDE = "source_location_longitude";
    public static String DESTINATION_LOCATION_LATITUDE = "destination_location_latitude";
    public static String DESTINATION_LOCATION_LONGITUDE = "destination_location_longitude";

    private Context context;
    private static SharedPreferences sharedPref;
    private String fileName;

    public Preferences() {

    }

    public Preferences(Context context) {
        this.context = context;
        fileName = context.getResources().getString(R.string.app_name);
        sharedPref = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    public void save(String key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void save(String key, int value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, context.getResources().getString(value));
        editor.commit();
    }

    public void save(String key1, String key2, LatLng value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key1, String.valueOf(value.latitude));
        editor.putString(key2, String.valueOf(value.longitude));
        editor.commit();
    }

    public String get(String key) {
        String defaultValue = "";
        return sharedPref.getString(key, defaultValue);
    }
}
