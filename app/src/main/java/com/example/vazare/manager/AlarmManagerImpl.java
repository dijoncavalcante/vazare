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

    private static final AlarmManagerImpl singletonInstance = new AlarmManagerImpl();
    private AlarmManager alarmManager;
    private static String TAG = "AlarmManagerImpl";

    public AlarmManagerImpl() {
    }

    @Override
    public AlarmManagerImpl getInstance(Context context) {
        return singletonInstance.init(context);
    }

    private AlarmManagerImpl init(Context context) {
        singletonInstance.alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        return singletonInstance;
    }

    @Override
    public void set(int type, long triggerAtMillis, PendingIntent operation) {
        singletonInstance.alarmManager.setExactAndAllowWhileIdle(type, triggerAtMillis, operation);
        Log.d(TAG, "Set alarm with information: " + "type: " + type + "triggerAtMillis: " + triggerAtMillis + "operation: " + operation);
    }

    @Override
    public void cancel(PendingIntent pendingIntent) {
        singletonInstance.alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
        Log.d(TAG, "Alarm Cancelled");
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
        return PendingIntent
                .getBroadcast(
                        context
                        , 0
                        , new Intent(
                                context
                                , MyBroadCastReceiver.class)
                        , 0);
    }
}