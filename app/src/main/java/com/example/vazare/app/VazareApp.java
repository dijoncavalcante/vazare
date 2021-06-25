package com.example.vazare.app;


import android.app.Application;

public class VazareApp extends Application {

    private static VazareApp INSTANCE;

    public static VazareApp getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }
}
