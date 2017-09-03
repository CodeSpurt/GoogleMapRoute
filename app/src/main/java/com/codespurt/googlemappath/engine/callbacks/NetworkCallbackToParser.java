package com.codespurt.googlemappath.engine.callbacks;

import org.json.JSONObject;

public interface NetworkCallbackToParser {

    void gotValidResponseObject(JSONObject response);

    void gotInvalidResponse(int statusCode);
}