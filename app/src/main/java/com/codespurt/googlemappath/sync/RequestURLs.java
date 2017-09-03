package com.codespurt.googlemappath.sync;

import com.codespurt.googlemappath.util.Preferences;

public class RequestURLs {

    private Preferences preferences;
    public static final String API_KEY = "";

    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?";

    public String getDirectionsUrl() {
        preferences = new Preferences();
        return "origin="
                + preferences.get(Preferences.SOURCE_LOCATION_LATITUDE) + ","
                + preferences.get(Preferences.SOURCE_LOCATION_LONGITUDE)
                + "&destination="
                + preferences.get(Preferences.DESTINATION_LOCATION_LATITUDE) + ","
                + preferences.get(Preferences.DESTINATION_LOCATION_LONGITUDE)
                + "&key=" + API_KEY;
    }
}