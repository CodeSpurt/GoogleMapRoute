package com.codespurt.googlemappath.model;

import java.util.List;

/**
 * Created by CodeSpurt on 01-09-2017.
 */

public class RouteObject {

    private List<LegsObject> legs;

    public RouteObject(List<LegsObject> legs) {
        this.legs = legs;
    }

    public List<LegsObject> getLegs() {
        return legs;
    }
}
