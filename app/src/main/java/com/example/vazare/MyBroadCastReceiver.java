package com.example.vazare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyBroadCastReceiver extends BroadcastReceiver {
    private static final String TAG = "MyBroadCastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        Log.d(TAG, "onReceive");
        if (intent.getAction() != null) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                Log.d(TAG, "BOOT_COMPLETED");
                Intent serviceIntent = new Intent(context, MyService.class);
                context.startService(serviceIntent);
            }
        }else {
            Log.d(TAG, "Alarm Manager just ran");
            Toast.makeText(context.getApplicationContext(), "Alarm Manager just ran", Toast.LENGTH_LONG).show();
        }
    }
}
