package com.codespurt.googlemappath.model;

import java.util.List;

/**
 * Created by CodeSpurt on 01-09-2017.
 */

public class DirectionObject {

    private List<RouteObject> routes;
    private String status;

    public DirectionObject(List<RouteObject> routes, String status) {
        this.routes = routes;
        this.status = status;
    }

    public List<RouteObject> getRoutes() {
        return routes;
    }

    public String getStatus() {
        return status;
    }
}
