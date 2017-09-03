package com.codespurt.googlemappath.model;

/**
 * Created by CodeSpurt on 01-09-2017.
 */

public class StepsObject {

    private PolylineObject polyline;

    public StepsObject(PolylineObject polyline) {
        this.polyline = polyline;
    }

    public PolylineObject getPolyline() {
        return polyline;
    }
}
