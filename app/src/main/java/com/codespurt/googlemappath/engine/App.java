package com.codespurt.googlemappath.engine;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.codespurt.googlemappath.sync.NetworkManager;
import com.codespurt.googlemappath.util.Preferences;

/**
 * Created by CodeSpurt on 01-09-2017.
 */

public class App extends Application {

    private Preferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = new Preferences(getApplicationContext());

        NetworkManager.getInstance(this);
    }
}
