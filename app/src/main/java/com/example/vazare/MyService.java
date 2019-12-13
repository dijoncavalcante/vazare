package com.example.vazare;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MyService extends IntentService {
    private static final String TAG = "MyService";
    private static final String FORMAT = "%02d:%02d:%02d";
    public MyService() {
        super("MyService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent alarmIntent = new Intent(this, MyBroadCastReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
//        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, 0, 10000, pendingIntent);
////        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,2000,pendingIntent);
//        Calendar c = Calendar.getInstance();
//        c.set(Calendar.HOUR, 0);
//        c.set(Calendar.MINUTE, 5);
//        c.set(Calendar.SECOND, 0);
//        String minutosRestantes = String.format(FORMAT,
//                TimeUnit.MILLISECONDS.toHours(c.getTimeInMillis()),
//                TimeUnit.MILLISECONDS.toMinutes(c.getTimeInMillis()) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(c.getTimeInMillis())),
//                TimeUnit.MILLISECONDS.toSeconds(c.getTimeInMillis()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(c.getTimeInMillis())));
//        Log.d(TAG, minutosRestantes);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 0, TimeUnit.MILLISECONDS.toMinutes(c.getTimeInMillis()) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(c.getTimeInMillis())), pendingIntent);
//        Log.d(TAG, "alarmManager.setExact");
//        Toast.makeText(this.getApplicationContext(), "TOAST MyService", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        new CountDownTimer(16069000, 1000) { // adjust the milli seconds here 16069000
//            String minutosRestantes ="";
//            public void onTick(long millisUntilFinished) {
//
//                minutosRestantes = String.format(FORMAT,
//                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
//                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
//                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
//
//                Log.d(TAG, minutosRestantes);
//            }
//
//            public void onFinish() {
//                Log.d(TAG, minutosRestantes);
//            }
//        }.start();
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
    }
}
