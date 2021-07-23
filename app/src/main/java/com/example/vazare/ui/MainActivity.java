package com.example.vazare.ui;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.vazare.R;
import com.example.vazare.SettingsManagerActivity;
import com.example.vazare.manager.AlarmManagerImpl;
import com.example.vazare.receiver.NotificationReceiver;
import com.example.vazare.util.Notification;
import com.example.vazare.util.VazareUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static com.example.vazare.R.string.clear_values;
import static com.example.vazare.R.string.not_clear_values;
import static com.example.vazare.util.Constants.ACTION_UPDATE_NOTIFICATION;
import static com.example.vazare.util.Constants.CHECK_BANK_OF_HOUR_KEY;
import static com.example.vazare.util.Constants.FORMAT_HOUR_MIN;
import static com.example.vazare.util.Constants.FORMAT_HOUR_MIN_SEC;
import static com.example.vazare.util.Constants.HORA_ENTRADA_ALMOCO_KEY;
import static com.example.vazare.util.Constants.HORA_FINAL_KEY;
import static com.example.vazare.util.Constants.HORA_INICIAL_KEY;
import static com.example.vazare.util.Constants.HORA_SAIDA_ALMOCO_KEY;
import static com.example.vazare.util.Constants.INT_DURACAO_TRABALHO__DIARIO_HORA_2021;
import static com.example.vazare.util.Constants.INT_DURACAO_TRABALHO__DIARIO_MINUTO_2021;
import static com.example.vazare.util.Constants.MILLIS_IN_SEC;
import static com.example.vazare.util.Constants.MY_PREFERENCE;
import static com.example.vazare.util.Constants.SECOND_DEFAULT;
import static com.example.vazare.util.Constants.STR_DURACAO_TRABALHO_BANCO_HORAS_PERMITIDAS;
import static com.example.vazare.util.Constants.STR_DURACAO_TRABALHO__DIARIO_2021;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    Toolbar toolbar;
    private NotificationManager mNotifyManager;
    private NotificationReceiver notificationReceiver;
    AlarmManagerImpl alarmManagerImpl;
    PendingIntent alarmPendingIntent;
    EditText etStart;
    TextInputLayout tvCountdownTimer;
    TextInputLayout tvDuration;
    TextInputLayout tvProgressiveCounting;
    @SuppressLint("StaticFieldLeak")
    public static EditText etLunchOut;
    @SuppressLint("StaticFieldLeak")
    public static EditText etLunchIn;
    TextView etEnd;
    SwitchMaterial switchBankHours;
    Button btnCalculate;
    FloatingActionButton fabClear;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    CountDownTimer countDownTimer;
    private long initialTime;
    private Handler handler;
    private boolean isRunning;
    private VazareUtils vazareUtils;
    Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        notification.createNotificationChannel(mNotifyManager, this);
        // Register the broadcast receiver to receive the update action from the notification.
        registerReceiver(notificationReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));
        //TODO Add linha com hora inicial para teste
        /* etInit.setText("09:00"); */
        verifySharedPreference();
        clicks();
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                long ms = (System.currentTimeMillis() - initialTime);
                Log.d(TAG, "initialTime:" + initialTime + " System.currentTimeMillis:" + System.currentTimeMillis() + " ms:" + ms);
                String timeComplete;
                //caso não tenha nenhum campo de ALMOÇO preenchido
                if (TextUtils.isEmpty(etLunchOut.getText()) || TextUtils.isEmpty(etLunchIn.getText())) {
                    timeComplete = vazareUtils.fullTime(ms);
                } else {//subtrair o tempo de almoço, caso os dois campos de almoço estejam preenchidos
                    long timeWorkedMillisWithIntervalLunch = ms - vazareUtils.intervalLunchMS(etLunchOut.getText().toString(), etLunchIn.getText().toString());
                    Log.d(TAG, "timeWorkedMillisWithIntervalLunch:" + timeWorkedMillisWithIntervalLunch + " = System.currentTimeMillis:" + System.currentTimeMillis() + " - initialTime:" + initialTime + " - ms:" + ms);
                    timeComplete = vazareUtils.fullTime(timeWorkedMillisWithIntervalLunch);
                }
                Objects.requireNonNull(tvProgressiveCounting.getEditText()).setText(timeComplete);
                Log.d(TAG, "timeComplete: " + timeComplete);
                handler.postDelayed(runnable, MILLIS_IN_SEC);
            }
        }
    };

    /**
     * Listen the events of the click
     */
    private void clicks() {
        etStartClickListener();
        etLunchOutClickListener();
        etLunchInClickListener();
        switchBankHoursCheckedChangeListener();
        fabClearClickListener();
    }

    private void etStartClickListener() {
        etStart.setOnClickListener(
                v -> {
                    int hour;
                    int minute;
                    Calendar mcurrentTime = Calendar.getInstance();
                    if (TextUtils.isEmpty(etStart.getText())) {
                        hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        minute = mcurrentTime.get(Calendar.MINUTE);
                    } else {
                        hour = vazareUtils.getHour(etStart.getText().toString());
                        minute = vazareUtils.getMinute(etStart.getText().toString());
                    }
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog, (timePicker, selectedHour, selectedMinute) -> {
                        etStart.setText(format(FORMAT_HOUR_MIN, selectedHour, selectedMinute));
                        etStart.setError(null);
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle(R.string.select_time);
                    mTimePicker.show();
                }
        );
    }

    private void etLunchOutClickListener() {
        etLunchOut.setOnClickListener(
                v -> {
                    int hour, minute;
                    Calendar mcurrentTime = Calendar.getInstance();

                    if (TextUtils.isEmpty(etLunchOut.getText())) {
                        hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        minute = mcurrentTime.get(Calendar.MINUTE);
                    } else {
                        hour = vazareUtils.getHour(etLunchOut.getText().toString());
                        minute = vazareUtils.getMinute(etLunchOut.getText().toString());
                    }
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            etLunchOut.setText(format(FORMAT_HOUR_MIN, selectedHour, selectedMinute));

                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle(R.string.select_time);
                    mTimePicker.show();
                }
        );
    }

    private void etLunchInClickListener() {
        etLunchIn.setOnClickListener(
                v -> {
                    if (!TextUtils.isEmpty(etLunchOut.getText())) {
                        etLunchOut.setError(null);
                        final Date dateSaidaAlmoco = new Date();
                        final Date dateEntradaAlmoco = new Date();
                        dateSaidaAlmoco.setHours(Integer.parseInt(etLunchOut.getText().toString().split(":")[0]));
                        dateSaidaAlmoco.setMinutes(Integer.parseInt(etLunchOut.getText().toString().split(":")[1]));

                        int hour, minute;
                        Calendar mcurrentTime = Calendar.getInstance();
                        if (TextUtils.isEmpty(etLunchIn.getText())) {
                            hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                            minute = mcurrentTime.get(Calendar.MINUTE);
                        } else {
                            hour = vazareUtils.getHour(etLunchIn.getText().toString());
                            minute = vazareUtils.getMinute(etLunchIn.getText().toString());
                        }

                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                dateEntradaAlmoco.setHours(selectedHour);
                                dateEntradaAlmoco.setMinutes(selectedMinute);

                                if (dateSaidaAlmoco.getTime() >= dateEntradaAlmoco.getTime()) {
                                    etLunchIn.setError(getString(R.string.validate_return_lunch_bigger));
                                    etLunchIn.requestFocus();
                                    return;
                                }

                                etLunchIn.setError(null);
                                etLunchIn.setText(format(FORMAT_HOUR_MIN, selectedHour, selectedMinute));
                            }
                        }, hour, minute, true);//Yes 24 hour time
                        mTimePicker.setTitle(R.string.select_time);
                        mTimePicker.show();
                    } else {
                        etLunchOut.setError(getString(R.string.validate_return_lunch));
                        etLunchOut.requestFocus();
                    }
                }
        );
    }

    private void switchBankHoursCheckedChangeListener() {
        switchBankHours.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (campoInicialHasValue()) {
                        calculate();
                    } else {
                        switchBankHours.setChecked(false);
                        Objects.requireNonNull(tvDuration.getEditText()).setText("");
                    }
                }
        );
    }

    private void fabClearClickListener() {
        fabClear.setOnClickListener(
                view -> {
                    if (!etStart.getText().toString().isEmpty() || !etEnd.getText().toString().isEmpty()
                            || !etLunchIn.getText().toString().isEmpty() || !etLunchOut.getText().toString().isEmpty()) {
                        reset();
                        clearSharedPreferences();
                        cancelCountDownTimer();
                        cancelAlarm();
                        Snackbar.make(view, clear_values, Snackbar.LENGTH_SHORT).setAction("Limpar", null).show();
                    } else {
                        Snackbar.make(view, not_clear_values, Snackbar.LENGTH_SHORT).setAction("Limpar", null).show();
                    }
                }
        );
    }

    public void cancelCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @SuppressLint("CommitPrefEdits")
    public void verifySharedPreference() {
        sharedPreferences = getApplication().getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (sharedPreferences.contains(HORA_INICIAL_KEY)) {
            String horaInicial = sharedPreferences.getString(HORA_INICIAL_KEY, "");
            if (!horaInicial.isEmpty()) {
                etStart.setText(horaInicial);
                Log.d(TAG, "verifySharedPreference: etStart: " + etStart.getText().toString());

                if (sharedPreferences.contains(CHECK_BANK_OF_HOUR_KEY)) {
                    boolean check = sharedPreferences.getBoolean(CHECK_BANK_OF_HOUR_KEY, false);
                    if (check) {
                        switchBankHours.setChecked(check);
                        tvDuration.getEditText().setText(STR_DURACAO_TRABALHO_BANCO_HORAS_PERMITIDAS);
                    } else {// a duração normal de um dia de trabalho
                        tvDuration.getEditText().setText(STR_DURACAO_TRABALHO__DIARIO_2021);
                    }
                    Log.d(TAG, "verifySharedPreference: switchBankHours: " + switchBankHours.isChecked());
                }
            }
        }
        if (sharedPreferences.contains(HORA_SAIDA_ALMOCO_KEY)) {
            String horaSaidaAlmoco = sharedPreferences.getString(HORA_SAIDA_ALMOCO_KEY, "");
            if (!horaSaidaAlmoco.isEmpty()) {
                etLunchOut.setText(horaSaidaAlmoco);
                Log.d(TAG, "verifySharedPreference: etAlmocoSaida: " + etLunchOut.getText().toString());
            }
        }
        if (sharedPreferences.contains(HORA_ENTRADA_ALMOCO_KEY)) {
            String horaEntradaAlmoco = sharedPreferences.getString(HORA_ENTRADA_ALMOCO_KEY, "");
            if (!horaEntradaAlmoco.isEmpty()) {
                etLunchIn.setText(horaEntradaAlmoco);
                Log.d(TAG, "verifySharedPreference: etAlmocoEntrada: " + etLunchIn.getText().toString());
            }
        }
        if (sharedPreferences.contains(HORA_FINAL_KEY)) {
            String horaFinal = sharedPreferences.getString(HORA_FINAL_KEY, "");
            if (!horaFinal.isEmpty()) {
                etEnd.setText(horaFinal);
                countDownTimerNotification();
                Log.d(TAG, "verifySharedPreference: tvSaida: " + etEnd.getText().toString());
            }
        }

        if (!TextUtils.isEmpty(etStart.getText().toString())) {
            calculateHourWorked();
        }
    }

    public void countDownTimerNotification() {
        /* Logica para calcular o countdown para sair do trabalho         */
        Calendar calendarCurrent = Calendar.getInstance();
        Calendar calendarEndDay = Calendar.getInstance();
        calendarEndDay.set(Calendar.HOUR_OF_DAY, vazareUtils.getHour(etEnd.getText().toString()));
        calendarEndDay.set(Calendar.MINUTE, vazareUtils.getMinute(etEnd.getText().toString()));
        calendarEndDay.set(Calendar.SECOND, SECOND_DEFAULT);
        long diffDate = calendarEndDay.getTimeInMillis() - calendarCurrent.getTimeInMillis();
        countDownTimer = new CountDownTimer(diffDate, 1000) {
            public void onTick(long millisUntilFinished) {

                @SuppressLint("DefaultLocale") String minutosRestantes = format(FORMAT_HOUR_MIN_SEC,
                        MILLISECONDS.toHours(millisUntilFinished),
                        MILLISECONDS.toMinutes(millisUntilFinished) - HOURS.toMinutes(MILLISECONDS.toHours(millisUntilFinished)),
                        MILLISECONDS.toSeconds(millisUntilFinished) - MINUTES.toSeconds(MILLISECONDS.toMinutes(millisUntilFinished)));
                tvCountdownTimer.getEditText().setText(minutosRestantes);

                switch (tvCountdownTimer.getEditText().getText().toString()) {
                    case "00:15:00":
                        tvCountdownTimer.setBackgroundColor(Color.GREEN);
                        notification.sendNotification(15, mNotifyManager, getApplication());
                        break;
                    case "00:10:00":
                        tvCountdownTimer.setBackgroundColor(Color.YELLOW);
                        notification.sendNotification(10, mNotifyManager, getApplication());
                        break;
                    case "00:05:00":
                        tvCountdownTimer.setBackgroundColor(Color.RED);
                        // Send the notification
                        notification.sendNotification(5, mNotifyManager, getApplication());
                        break;
                    case "00:02:00":
                        notification.sendNotification(2, mNotifyManager, getApplication());
                        break;
                    case "00:01:00":
                        notification.sendNotification(1, mNotifyManager, getApplication());
                        break;
                }
            }

            public void onFinish() {
                if (switchBankHours.isChecked()) {
                    notification.showAlertDialog(getString(R.string.horas_extras_0147), getApplicationContext(), switchBankHours.isChecked());
                } else {
                    notification.showAlertDialog(getString(R.string.horas_trabalhadas), getApplicationContext(), switchBankHours.isChecked());
                    notification.sendNotification(1, mNotifyManager, getApplication());
                }
                //TODO não limpar quando acabar os minutos restantes
                // clearSharedPreferences();
            }
        }.start();
    }

    public void initCronometro(Date dateHoraEntrada) {
        if (!isRunning) {
            isRunning = true;
            initialTime = dateHoraEntrada.getTime();
            handler.postDelayed(runnable, MILLIS_IN_SEC);
        }
    }

    public boolean campoInicialHasValue() {
        if (TextUtils.isEmpty(etStart.getText())) {
            etStart.setError("Informe a hora de entrada!");
            etStart.requestFocus();
            return false;
        } else
            return true;
    }

    private void setAlarm(String hour) {
        Calendar calendar = vazareUtils.getCalendar(hour);
        alarmManagerImpl.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendar.getTimeInMillis(), alarmPendingIntent);
        Toast.makeText(this, "Agendado para: " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Alarme set to: " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
    }

    private void cancelAlarm() {
        if (alarmManagerImpl.isAlarmExists()) {
            alarmManagerImpl.cancel(alarmPendingIntent);
            Toast.makeText(this, "Alarm Cancelled", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_notificaton) {
            Toast.makeText(this, "Sem notificações.", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.menu_action_settings) {
            Intent intent = new Intent(this, SettingsManagerActivity.class);
            startActivity(intent);
        }
        return true;
    }

    public void calculateOnClick(View view) {
        calculate();
    }

    public void calculate() {
        if (validate()) {
            //clear some fields or action
            cancelCountDownTimer();
            clearSharedPreferences();

            calculateSaida();
            //TODO Validar se o calculo é necessário antes iniciar o alarme
            setAlarm(etEnd.getText().toString());
            //horas trabalhadas
            calculateHorasTrabalhadas();
            //calcular horas permitidas
            if (switchBankHours.isChecked()) {
                etEnd.setText(vazareUtils.calculateBH(etEnd.getText().toString()));
                tvDuration.getEditText().setText(STR_DURACAO_TRABALHO_BANCO_HORAS_PERMITIDAS);
            } else {
                tvDuration.getEditText().setText(STR_DURACAO_TRABALHO__DIARIO_2021);
            }
            //start countDownTimer
            countDownTimerNotification();
            //save fields on saveSharedPreferences
            saveSharedPreferences();
        }
    }

    public void calculateSaida() {
        String almoco = calculateLunch();
        String[] horaMinuto = etStart.getText().toString().split(":");
        int horaSaida = Integer.parseInt(horaMinuto[0]) + INT_DURACAO_TRABALHO__DIARIO_HORA_2021 + Integer.parseInt(almoco.split(":")[0]);
        int minutoSaida = Integer.parseInt(horaMinuto[1]) + INT_DURACAO_TRABALHO__DIARIO_MINUTO_2021 + Integer.parseInt(almoco.split(":")[1]);
        //horario de saída
        Calendar dateTimeExit = Calendar.getInstance();
        dateTimeExit.set(Calendar.HOUR_OF_DAY, horaSaida);
        dateTimeExit.set(Calendar.MINUTE, minutoSaida);
        dateTimeExit.set(Calendar.SECOND, SECOND_DEFAULT);
        etEnd.setText(format(FORMAT_HOUR_MIN, dateTimeExit.get(Calendar.HOUR_OF_DAY), dateTimeExit.get(Calendar.MINUTE)));
    }

    public String calculateLunch() {
        if (validateLunch()) {
            return "01:00";
        }
        long timeLunchMS = vazareUtils.intervalLunchMS(etLunchOut.getText().toString(), etLunchIn.getText().toString());
        return vazareUtils.fullTime(timeLunchMS);
    }

    public boolean validateLunch() {
        if (TextUtils.isEmpty(etLunchOut.getText()) || TextUtils.isEmpty(etLunchIn.getText())) {
            return true;
        }
        if (!TextUtils.isEmpty(etLunchOut.getText().toString()) && !TextUtils.isEmpty(etLunchIn.getText().toString())) {
            Date dateSaida = new Date();
            dateSaida.setHours(Integer.parseInt(etLunchOut.getText().toString().split(":")[0]));
            dateSaida.setMinutes(Integer.parseInt(etLunchOut.getText().toString().split(":")[1]));
            Date dateEntrada = new Date();
            dateEntrada.setHours(Integer.parseInt(etLunchIn.getText().toString().split(":")[0]));
            dateEntrada.setMinutes(Integer.parseInt(etLunchIn.getText().toString().split(":")[1]));
            int totalSaidaAlmoco = dateSaida.getHours() * 60 + dateSaida.getMinutes();
            int totalRetornoAlmoco = dateEntrada.getHours() * 60 + dateEntrada.getMinutes();
            //se a saida do almoco(12:00) for maior que a hora de retorno do almoço(13:00)
            if (totalSaidaAlmoco > totalRetornoAlmoco) {
                etLunchIn.setError("Horário de entrada do almoço deve ser maior que o horário de saída para o almoço");
                etLunchIn.setFocusable(true);
                return false;
            }
        }

        return false;
    }

    public String calculateHorasTrabalhadas() {
        String horaEntrada = etStart.getText().toString();
        Date dateHoraEntrada = new Date();
        dateHoraEntrada.setHours(Integer.parseInt(horaEntrada.split(":")[0]));
        dateHoraEntrada.setMinutes(Integer.parseInt(horaEntrada.split(":")[1]));
        //calcular horas trabalhadas
        initCronometro(dateHoraEntrada);
//        retorna meu horário ja trabalhado até o momento
        return vazareUtils.diff_time(dateHoraEntrada, new Date());
    }

    public void calculateHourWorked() {
        String horaEntrada = etStart.getText().toString();
        Date dateHoraEntrada = new Date();
        dateHoraEntrada.setHours(Integer.parseInt(horaEntrada.split(":")[0]));
        dateHoraEntrada.setMinutes(Integer.parseInt(horaEntrada.split(":")[1]));
        Log.d(TAG, "calculateHourWorked: dateHoraEntrada: " + dateHoraEntrada);
        //calcular horas trabalhadas
        initCronometro(dateHoraEntrada);
    }

    public void reset() {
        etStart.setText("");
        Objects.requireNonNull(tvDuration.getEditText()).setText("");
        etLunchOut.setText("");
        etLunchIn.setText("");
        etEnd.setText("");
        Objects.requireNonNull(tvCountdownTimer.getEditText()).setText("");
        Objects.requireNonNull(tvProgressiveCounting.getEditText()).setText("");
        switchBankHours.setChecked(false);
        isRunning = false;
        handler.removeCallbacks(runnable);
        notificationReceiver.ignoreNotification(mNotifyManager, this);
    }

    /**
     * Start the widgets on screen
     */
    private void initUI() {
        setContentView(R.layout.vazare_main);
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        notificationReceiver = new NotificationReceiver(this);
        notification = new Notification();
        alarmManagerImpl = new AlarmManagerImpl();
        handler = new Handler();
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        etStart = findViewById(R.id.et_start);
        tvDuration = findViewById(R.id.tv_duration);
        etLunchOut = findViewById(R.id.et_lunch_out);
        etLunchIn = findViewById(R.id.et_lunch_in);
        etEnd = findViewById(R.id.et_end);
        tvProgressiveCounting = findViewById(R.id.tv_progressive_counting);
        btnCalculate = findViewById(R.id.btn_calculate);
        switchBankHours = findViewById(R.id.switch_banck_hours);
        tvCountdownTimer = findViewById(R.id.tv_countdown_timer);
        fabClear = findViewById(R.id.fab_clear);
        alarmPendingIntent = alarmManagerImpl.prepareAlarmPendingIntent();
        vazareUtils = new VazareUtils();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        saveSharedPreferences();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        unregisterReceiver(notificationReceiver);
        super.onDestroy();
    }

    public void saveSharedPreferences() {
        editor.putString(HORA_INICIAL_KEY, etStart.getText().toString());
        editor.putString(HORA_FINAL_KEY, etEnd.getText().toString());
        editor.putString(HORA_SAIDA_ALMOCO_KEY, etLunchOut.getText().toString());
        editor.putString(HORA_ENTRADA_ALMOCO_KEY, etLunchIn.getText().toString());
        editor.putBoolean(CHECK_BANK_OF_HOUR_KEY, switchBankHours.isChecked());
        editor.commit();
        Log.d(TAG, "salveSharedPreferences:"
                + " etInit: " + etStart.getText().toString()
                + " etAlmocoSaida: " + etLunchOut.getText().toString()
                + " etAlmocoEntrada: " + etLunchIn.getText().toString()
                + " tvSaida: " + etEnd.getText().toString()
                + " switchBankHours: " + switchBankHours.isChecked());
    }

    public void clearSharedPreferences() {
        editor.remove(HORA_INICIAL_KEY);
        editor.remove(HORA_FINAL_KEY);
        editor.remove(CHECK_BANK_OF_HOUR_KEY);
        editor.remove(HORA_SAIDA_ALMOCO_KEY);
        editor.remove(HORA_ENTRADA_ALMOCO_KEY);
        editor.clear();
        editor.commit();
    }

    public boolean validate() {
        if (TextUtils.isEmpty(etStart.getText())) {
            etStart.setError(getString(R.string.put_hour_initial));
            etStart.requestFocus();
            return false;
        }
        return true;
    }
}
