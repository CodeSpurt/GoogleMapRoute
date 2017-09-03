package com.codespurt.googlemappath.sync;

import com.codespurt.googlemappath.model.DirectionObject;
import com.google.gson.Gson;

import org.json.JSONObject;

public class ResponseManager {

    private static ResponseManager instance = null;

    // singleton
    private ResponseManager() {

    }

    public static synchronized ResponseManager getInstance() {
        if (instance == null) {
            instance = new ResponseManager();
        }
        return instance;
    }

    public DirectionObject parseResponseObject(JSONObject response) {
        Gson gson = new Gson();
        DirectionObject wrapper = gson.fromJson(response.toString(), DirectionObject.class);
        return wrapper;
    }
}