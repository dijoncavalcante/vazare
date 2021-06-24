package com.example.vazare.contracts;

import android.app.PendingIntent;
import android.content.Context;

import com.example.vazare.manager.AlarmManagerImpl;

public interface IAlarmManager {

    void set(int type, long triggerAtMillis, PendingIntent operation);

    void cancel(PendingIntent pendingIntent);

    boolean isAlarmExists(Context context);

    PendingIntent prepareAlarmPendingIntent(Context context);
}
