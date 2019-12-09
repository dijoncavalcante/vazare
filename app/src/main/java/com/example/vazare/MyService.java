package com.example.vazare;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyService extends IntentService {
    private static final String TAG = "MyService";
    public MyService() {
        super("MyService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(this, MyBroadCastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, 0, 10000, pendingIntent);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,5000,pendingIntent);
        Log.d(TAG, "alarmManager.setExact");
    }
}
