package com.example.vazare.manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.vazare.MyBroadCastReceiver;
import com.example.vazare.contracts.IAlarmManager;

import static android.content.Context.ALARM_SERVICE;

public class AlarmManagerImpl implements IAlarmManager {
    private static String TAG = "AlarmManagerImpl";
    private AlarmManager alarmManager;

    public AlarmManagerImpl(Context context) {
        this.init(context);
    }

    private void init(Context context) {
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
    }

    @Override
    public void set(int type, long triggerAtMillis, PendingIntent operation) {
        Log.d(TAG, "Set alarm with information: " + "type: " + type + "triggerAtMillis: "
                + triggerAtMillis + "operation: " + operation);
        alarmManager.cancel(operation);
        alarmManager.set(type, triggerAtMillis, operation);
    }

    @Override
    public void cancel(PendingIntent pendingIntent) {
        Log.d(TAG, "Alarm Cancelled");
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    @Override
    public boolean isAlarmExists(Context context) {
        boolean alarmUp = (PendingIntent.getBroadcast(context, 0
                , new Intent(context, MyBroadCastReceiver.class)
                , PendingIntent.FLAG_NO_CREATE) != null);
        Log.d(TAG, "Alarm already exists");
        return alarmUp;
    }

    @Override
    public PendingIntent prepareAlarmPendingIntent(Context context) {
        return PendingIntent.getBroadcast(context, 0, new Intent(context, MyBroadCastReceiver.class), 0);
    }
}