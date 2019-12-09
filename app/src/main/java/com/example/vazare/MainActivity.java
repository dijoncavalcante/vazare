package com.example.vazare;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.security.Timestamp;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    /*
    variaveis para mostrar o countdown mais vibração
     */
    private static final String FORMAT = "%02d:%02d:%02d";
    /*
    variaveis para a notificação
     */
    // Constants for the notification actions buttons.
    private static final String ACTION_UPDATE_NOTIFICATION = "com.android.example.notifyme.ACTION_UPDATE_NOTIFICATION";
    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    // Notification ID.
    private static final int NOTIFICATION_ID = 0;
    private NotificationManager mNotifyManager;
    private NotificationReceiver mReceiver = new NotificationReceiver();

    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    TextView tvTimetogo;
    TextView tvTimer;
    EditText etInit;
    EditText etDuracao;
    EditText etAlmocoSaida;
    EditText etAlmocoEntrada;
    EditText etSaida;
    EditText ethorasTrabalhadas;
    CheckBox cb0147;
    Button btnCalculate;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();

        Intent alarmIntent = new Intent(this, MyBroadCastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);


        etInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etInit.setText(selectedHour + ":" + selectedMinute);
                        etInit.setError(null);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        etDuracao.setText("08:13");
        etDuracao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etDuracao.setText(selectedHour + ":" + selectedMinute);

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        etAlmocoSaida.setText("12:00");
        etAlmocoSaida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etAlmocoSaida.setText(selectedHour + ":" + selectedMinute);

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        etAlmocoEntrada.setText("13:00");
        etAlmocoEntrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etAlmocoEntrada.setText(selectedHour + ":" + selectedMinute);

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        etSaida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etSaida.setText(selectedHour + ":" + selectedMinute);

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        cb0147.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (campoInicialHasValue()) {
                    calculate();
                } else {
                    cb0147.setChecked(false);
                }
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Valores resetados com sucesso!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Reset();
            }
        });

        /*
            Notificao
         */
        // Create the notification channel.
        createNotificationChannel();
        // Register the broadcast receiver to receive the update action from the notification.
        registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));
        /*
        Logica para calcular o countdown para sair do trabalho
         */
        Date dataHoraEntrada = new Date();
        dataHoraEntrada.setHours(16);
        dataHoraEntrada.setMinutes(8);
        dataHoraEntrada.setSeconds(0);

        Date dataHoraSaida = new Date();
        dataHoraSaida.setHours(16);
        dataHoraSaida.setMinutes(13);
        dataHoraSaida.setSeconds(10);

        long diff = dataHoraSaida.getTime() - dataHoraEntrada.getTime();

        new CountDownTimer(diff, 1000) { // adjust the milli seconds here 16069000

            public void onTick(long millisUntilFinished) {

                String minutosRestantes = String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                tvTimer.setText(minutosRestantes);

                if (tvTimer.getText().toString().equals("00:15:00")) {
                    tvTimer.setBackgroundColor(Color.GREEN);
                    sendNotification(15);
                } else if (tvTimer.getText().toString().equals("00:10:00")) {
                    tvTimer.setBackgroundColor(Color.YELLOW);
                    sendNotification(10);
                } else if (tvTimer.getText().toString().equals("00:05:00")) {
                    tvTimer.setBackgroundColor(Color.RED);
                    // Send the notification
                    sendNotification(5);
                } else if (tvTimer.getText().toString().equals("00:02:00")) {
                    sendNotification(2);
                } else if (tvTimer.getText().toString().equals("00:01:00")) {
                    sendNotification(1);
                }
            }

            public void onFinish() {
                if (cb0147.isChecked()) {
                    tvTimetogo.setText(R.string.horas_extras_0147);
                } else {
                    tvTimetogo.setText(R.string.horas_trabalhadas);
                    sendNotification(1);
                }
            }
        }.start();
    }

    public boolean campoInicialHasValue() {
        if (TextUtils.isEmpty(etInit.getText())) {
            etInit.setError("Informe a hora de entrada!");
            etInit.setFocusable(true);
            etInit.requestFocus();
            return false;
        }
        else
            return  true;
    }

    private void startAlarm() {
        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, 0, 5000, pendingIntent);
        //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,5000,pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP,5000,pendingIntent);
}

    private void cancelAlarm() {
        alarmManager.cancel(pendingIntent);
        Toast.makeText(getApplicationContext(), "Alarm Cancelled", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void calculateOnClick(View view) {
        calculate();
    }
    public void calculate() {
        //TODO teste delete-me
        startAlarm();

        if(!campoInicialHasValue())
            return;
        //hora gasta no almoço
        String almoco = calculateLunch();
        //recuperando o texto/hora inicial
        String texto = etInit.getText().toString();
        //separando a hora e minuto por :
        String[] horaMinuto = texto.split(":");
        //somando as 08:00 horas de trabalho diários
        int horaSaida = Integer.parseInt(horaMinuto[0]) + 8 + Integer.parseInt(almoco.split(":")[0]);
        int minutoSaida = Integer.parseInt(horaMinuto[1]) + 13 + Integer.parseInt(almoco.split(":")[1]);

        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm aaa");
        //horario de saída
        Date date = new Date();
        date.setHours(horaSaida);
        date.setMinutes(minutoSaida);

        etSaida.setText(fmt.format(date));
        //horas trabalhadas
        ethorasTrabalhadas.setText(calculateHorasTrabalhadas());
        //calcular horas permitidas
        if (cb0147.isChecked()) {
            etSaida.setText(calcular0147());
        }
    }


    public String calculateLunch() {
        if (TextUtils.isEmpty(etAlmocoSaida.getText()) || TextUtils.isEmpty(etAlmocoEntrada.getText())) {
            return "01:00";
        }

        String saida = etAlmocoSaida.getText().toString();
        String entrada = etAlmocoEntrada.getText().toString();
        Date dateSaida = new Date();
        dateSaida.setHours(Integer.valueOf(saida.split(":")[0]));
        dateSaida.setMinutes(Integer.valueOf(saida.split(":")[1]));

        Date dateEntrada = new Date();
        dateEntrada.setHours(Integer.valueOf(entrada.split(":")[0]));
        dateEntrada.setMinutes(Integer.valueOf(entrada.split(":")[1]));

        return diff_time(dateSaida, dateEntrada);
    }

    public String calculateHorasTrabalhadas() {
        String horaEntrada = etInit.getText().toString();
        Date dateHoraEntrada = new Date();
        dateHoraEntrada.setHours(Integer.valueOf(horaEntrada.split(":")[0]));
        dateHoraEntrada.setMinutes(Integer.valueOf(horaEntrada.split(":")[1]));
        return diff_time(dateHoraEntrada, new Date());
    }

    public String calcular0147() {
        String saida = etSaida.getText().toString();
        Date dateSaida = new Date();
        dateSaida.setHours(Integer.valueOf(saida.split(":")[0]));
        dateSaida.setMinutes(Integer.valueOf(saida.split(":")[1]));
        return calcular0147(dateSaida);
    }

    public String calcular0147(Date dateHoraInial) {
        int totalHoras = dateHoraInial.getHours() * 60 + dateHoraInial.getMinutes();
        int horasPermitidas = 107;
        int total = totalHoras + horasPermitidas;
        int horas = total / 60;
        int minutos = total % 60;
        return horas + ":" + minutos;
    }

    public void Reset() {
        etInit.setText("");
        etDuracao.setText("08:13");
        etAlmocoSaida.setText("12:00");
        etAlmocoEntrada.setText("13:00");
        etSaida.setText("");
        tvTimer.setText("");
        cb0147.setChecked(false);
    }

    public String diff_time(Date saida, Date retorno) {

        int totalRetorno = retorno.getHours() * 60 + retorno.getMinutes();
        int totalSaida = saida.getHours() * 60 + saida.getMinutes();

        int total = totalRetorno - totalSaida;

        int horas = total / 60;
        int minutos = total % 60;

        return horas + ":" + minutos;
    }

    private void initUI() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        tvTimetogo = findViewById(R.id.tvTimetogo);
        etInit = findViewById(R.id.etInit);
        etDuracao = findViewById(R.id.etDuracao);
        etAlmocoSaida = findViewById(R.id.etAlmocoSaida);
        etAlmocoEntrada = findViewById(R.id.etAlmocoEntrada);
        etSaida = findViewById(R.id.etSaida);
        ethorasTrabalhadas = findViewById(R.id.ethorasTrabalhadas);
        btnCalculate = findViewById(R.id.btnCalculate);
        cb0147 = findViewById(R.id.cb0147);
        tvTimer = findViewById(R.id.tvTimer);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    public void createNotificationChannel() {
        // Create a notification manager object.
        mNotifyManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            getString(R.string.notification_channel_name),
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription(getString(R.string.notification_channel_description));
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    public void sendNotification(int opcao) {
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);
        // Build the notification with all of the parameters using helper method
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder(opcao);
        // Add the action button using the pending intent.
        //definir a imagem da notificacao
        notifyBuilder.addAction(R.drawable.ic_notification_app, getString(R.string.ignore), updatePendingIntent);
        // Deliver the notification.
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }

    public void ignoreNotification() {
        mNotifyManager.cancel(NOTIFICATION_ID);
    }

    private NotificationCompat.Builder getNotificationBuilder(int opcao) {

        // Set up the pending intent that is delivered when the notification is clicked.
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity
                (this, NOTIFICATION_ID, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        String txtNotificacao = "";

        if (opcao == 5) txtNotificacao = getString(R.string.notification_text_5);
        else if (opcao == 10) txtNotificacao = getString(R.string.notification_text_10);
        else if (opcao == 15) txtNotificacao = getString(R.string.notification_text_15);
        else if (opcao == 1) txtNotificacao = getString(R.string.notification_text_1);
        // Build the notification with all of the parameters.
        NotificationCompat.Builder notifyBuilder = new NotificationCompat
                .Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(txtNotificacao)
                .setSmallIcon(R.drawable.ic_notification_app)
                .setAutoCancel(true)
                .setContentIntent(notificationPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        return notifyBuilder;
    }

    public class NotificationReceiver extends BroadcastReceiver {
        private static final String TAG = "NotificationReceiver";

        public NotificationReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // Ignora the notification.
            Log.d(TAG, "onReceive");
            ignoreNotification();
        }
    }
}
