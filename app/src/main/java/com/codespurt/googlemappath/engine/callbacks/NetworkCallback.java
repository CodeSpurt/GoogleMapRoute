package com.codespurt.googlemappath.engine.callbacks;

import com.codespurt.googlemappath.model.DirectionObject;

public interface NetworkCallback {

    void valid(DirectionObject response);

    void error(int statusCode);
}