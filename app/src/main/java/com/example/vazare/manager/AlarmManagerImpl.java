package com.example.vazare.manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.example.vazare.MyBroadCastReceiver;
import com.example.vazare.application.VazareApp;
import com.example.vazare.contracts.IAlarmManager;

import static android.content.Context.ALARM_SERVICE;

public class AlarmManagerImpl implements IAlarmManager {
    private static final String TAG = "AlarmManagerImpl";
    private AlarmManager alarmManager;

    public AlarmManagerImpl() {
        this.init();
    }

    private void init() {
        alarmManager = (AlarmManager) VazareApp.getInstance().getSystemService(ALARM_SERVICE);
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
    public boolean isAlarmExists() {
        boolean alarmUp = (PendingIntent.getBroadcast(VazareApp.getInstance().getApplicationContext(), 0
                , new Intent(VazareApp.getInstance().getApplicationContext(), MyBroadCastReceiver.class)
                , PendingIntent.FLAG_NO_CREATE) != null);
        Log.d(TAG, "Alarm already exists");
        return alarmUp;
    }

    @Override
    public PendingIntent prepareAlarmPendingIntent() {
        return PendingIntent.getBroadcast(VazareApp.getInstance().getApplicationContext(), 0
                , new Intent(VazareApp.getInstance().getApplicationContext(), MyBroadCastReceiver.class), 0);
    }
}