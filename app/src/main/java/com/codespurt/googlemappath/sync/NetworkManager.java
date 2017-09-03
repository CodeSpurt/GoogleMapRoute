package com.codespurt.googlemappath.sync;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.codespurt.googlemappath.engine.callbacks.NetworkCallbackToParser;

import org.json.JSONObject;

public class NetworkManager {

    private static final String TAG = "NetworkManager";

    private static NetworkManager instance = null;

    private RequestQueue requestQueue;

    // singleton
    private NetworkManager(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized NetworkManager getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkManager(context);
        }
        return instance;
    }

    public static synchronized NetworkManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(NetworkManager.class.getSimpleName() + " is not initialized, call getInstance(...) first");
        }
        return instance;
    }

    // GET - get json object
    public void hitServiceGetObject(String urlSuffix, final NetworkCallbackToParser callback) {
        String url = RequestURLs.BASE_URL + urlSuffix;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (response.toString() != null) {
                    Log.d(TAG, "Get Response : " + response.toString());
                    callback.gotValidResponseObject(response);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (error.networkResponse != null) {
                    Log.d(TAG, "Error Response code: " + error.networkResponse.statusCode);
                    callback.gotInvalidResponse(error.networkResponse.statusCode);
                }
            }
        });
        requestQueue.add(request);
    }
}
