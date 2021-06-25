package com.example.vazare;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.vazare.util.Constants;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.vazare.util.Constants.ACTION_UPDATE_NOTIFICATION;
import static com.example.vazare.util.Constants.HORA_FINAL_KEY;
import static com.example.vazare.util.Constants.HORA_INICIAL_KEY;
import static com.example.vazare.util.Constants.MY_PREFERENCE;
import static com.example.vazare.util.Constants.NOTIFICATION_ID;
import static com.example.vazare.util.Constants.PRIMARY_CHANNEL_ID;


public class MyBroadCastReceiver extends BroadcastReceiver {
    private static final String TAG = "MyBroadCastReceiver";
    private NotificationManager mNotifyManager;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        createNotificationChannel(context);
        sendNotification(context);
        sharedPreferences = context.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.remove(HORA_INICIAL_KEY);
        editor.remove(HORA_FINAL_KEY);
        editor.clear();
        editor.commit();
    }

    public void verifySharedPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (sharedPreferences.contains(HORA_FINAL_KEY)) {
            String horaFinal = sharedPreferences.getString(HORA_FINAL_KEY, "");
            if (!horaFinal.isEmpty()) {
                //TODO fazer o calculo para limpar a preferencia
            }
        }
    }

    private NotificationCompat.Builder getNotificationBuilder(Context context) {
        // Set up the pending intent that is delivered when the notification is clicked.
        Intent notificationIntent = new Intent(context, Constants.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Build the notification with all of the parameters.
        NotificationCompat.Builder notifyBuilder = new NotificationCompat
                .Builder(context, PRIMARY_CHANNEL_ID)
                .setContentTitle(context.getResources().getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_text_1))
                .setSmallIcon(R.mipmap.ic_vazare)
                .setAutoCancel(true)
                .setContentIntent(notificationPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        return notifyBuilder;
    }

    public void sendNotification(Context context) {
        Log.d(TAG, "sendNotification");
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);
        // Build the notification with all of the parameters using helper method
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder(context);
        // Add the action button using the pending intent.
        //definir a imagem da notificacao
        //notifyBuilder.addAction(R.mipmap.ic_vazare, context.getResources().getString(R.string.ignore), updatePendingIntent);
        // Deliver the notification.
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }

    public void createNotificationChannel(Context context) {
        Log.d(TAG, "createNotificationChannel");
        // Create a notification manager object.
        mNotifyManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, context.getString(R.string.notification_title), NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription(context.getString(R.string.notification_text_5));
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }


}
