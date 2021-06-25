package com.example.vazare.contracts;

import android.app.PendingIntent;

public interface IAlarmManager {

    void set(int type, long triggerAtMillis, PendingIntent operation);

    void cancel(PendingIntent pendingIntent);

    boolean isAlarmExists();

    PendingIntent prepareAlarmPendingIntent();
}
