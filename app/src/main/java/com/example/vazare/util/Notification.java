package com.example.vazare.util;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.vazare.R;
import com.example.vazare.presentation.MainActivity;

import static com.example.vazare.util.Constants.ACTION_UPDATE_NOTIFICATION;
import static com.example.vazare.util.Constants.NOTIFICATION_ID;
import static com.example.vazare.util.Constants.PRIMARY_CHANNEL_ID;

public class Notification {

    public void createNotificationChannel(NotificationManager mNotifyManager, Context context) {
        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, context.getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription(context.getString(R.string.notification_channel_description));
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    public void sendNotification(int option, NotificationManager notificationManager, Context context) {
        // Create a notification manager object.
        notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);
        // Build the notification with all of the parameters using helper method
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder(option, context);
        // Add the action button using the pending intent.
        //definir a imagem da notificacao
        notifyBuilder.addAction(R.mipmap.ic_vazare, context.getString(R.string.ignore), updatePendingIntent);
        // Deliver the notification.
        notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }

    private NotificationCompat.Builder getNotificationBuilder(int option, Context context) {
        // Set up the pending intent that is delivered when the notification is clicked.
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        String txtNotificacao = "";

        if (option == 5) txtNotificacao = context.getString(R.string.notification_text_5);
        else if (option == 10) txtNotificacao = context.getString(R.string.notification_text_10);
        else if (option == 15) txtNotificacao = context.getString(R.string.notification_text_15);
        else if (option == 1) txtNotificacao = context.getString(R.string.notification_text_1);
        // Build the notification with all of the parameters.
        return new NotificationCompat
                .Builder(context, PRIMARY_CHANNEL_ID)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(txtNotificacao)
                .setSmallIcon(R.mipmap.ic_vazare)
                .setAutoCancel(true)
                .setContentIntent(notificationPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
    }

    /**
     * adb shell "dumpsys activity activities | grep mResumedActivity
     * mResumedActivity: ActivityRecord{5986b19 u0 com.example.vazare/.MainActivity t17}
     * mResumedActivity: ActivityRecord{51b9692 u0 com.ubercab/.presidio.app.core.root.RootActivity t16
     * mResumedActivity: ActivityRecord{5909209 u0 com.taxis99/com.didi.sdk.app.MainActivityImpl t18}
     * mResumedActivity: ActivityRecord{c91a10d u0 net.taxidigital.tocantins/net.taxidigital.ui.main.MainActivity t19}
     */
    public void showAlertDialog(String message, Context context, boolean isSwitchChecked) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.hours_worked);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.pedir_corrida, (arg0, arg1) -> {
            Intent launchIntent;
            if (!isSwitchChecked) {
                launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.ubercab");
            } else {
                launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.taxis99");
                if (launchIntent == null) {
                    launchIntent = context.getPackageManager().getLaunchIntentForPackage("net.taxidigital.tocantins");
                    if (launchIntent == null) {
                        launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.ubercab");
                    }
                }
            }
            if (launchIntent != null) {
                context.startActivity(launchIntent);
                Toast.makeText(context, "Abrindo App de Corrida.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Aplicativos: UBER, 99Taxi ou Tocantins não foram encontrados.", Toast.LENGTH_SHORT).show();
            }
        });
        //define um botão como negativo.
        builder.setNegativeButton(R.string.close, (arg0, arg1) -> Toast.makeText(context, R.string.close_popup, Toast.LENGTH_SHORT).show());
        AlertDialog alert = builder.create();
        alert.show();
    }
}
