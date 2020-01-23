package com.example.vazare;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.Nullable;

public class MyService extends IntentService {
    private static final String TAG = "MyService";

    public MyService() {
        super("MyService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
    }
}
