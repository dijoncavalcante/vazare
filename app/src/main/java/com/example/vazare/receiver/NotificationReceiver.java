package com.example.vazare.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.example.vazare.util.Constants.NOTIFICATION_ID;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";
    NotificationManager notificationManager;

    public NotificationReceiver(Context context) {
        notificationManager = context.getSystemService(NotificationManager.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        ignoreNotification(notificationManager, context);
    }

    public void ignoreNotification(NotificationManager mNotifyManager, Context context) {
        mNotifyManager.cancel(NOTIFICATION_ID);
        Log.d(TAG, "ignoreNotification NOTIFICATION_ID: " + NOTIFICATION_ID);
    }
}