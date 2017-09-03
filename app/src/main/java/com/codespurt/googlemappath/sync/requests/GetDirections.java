package com.codespurt.googlemappath.sync.requests;

import com.codespurt.googlemappath.engine.callbacks.NetworkCallback;
import com.codespurt.googlemappath.engine.callbacks.NetworkCallbackToParser;
import com.codespurt.googlemappath.model.DirectionObject;
import com.codespurt.googlemappath.sync.NetworkManager;
import com.codespurt.googlemappath.sync.RequestURLs;
import com.codespurt.googlemappath.sync.ResponseManager;

import org.json.JSONObject;

/**
 * Created by CodeSpurt on 03-09-2017.
 */

public class GetDirections {

    private NetworkCallback apiCallback;

    private NetworkCallbackToParser callback = new NetworkCallbackToParser() {
        @Override
        public void gotValidResponseObject(JSONObject response) {
            // convert JSONObject to Java Object
            DirectionObject wrapper = ResponseManager.getInstance().parseResponseObject(response);
            if (apiCallback != null) {
                apiCallback.valid(wrapper);
            }
        }

        @Override
        public void gotInvalidResponse(int statusCode) {
            if (apiCallback != null) {
                apiCallback.error(statusCode);
            }
        }
    };

    public GetDirections(NetworkCallback apiCallback) {
        this.apiCallback = apiCallback;
        hitService();
    }

    private void hitService() {
        NetworkManager.getInstance().hitServiceGetObject(new RequestURLs().getDirectionsUrl(), callback);
    }
}