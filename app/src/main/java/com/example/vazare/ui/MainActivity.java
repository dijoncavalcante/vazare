package com.example.vazare.ui;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import com.example.vazare.R;
import com.example.vazare.manager.AlarmManagerImpl;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static com.example.vazare.R.string.clear_values;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    private NotificationManager mNotifyManager;
    private NotificationReceiver mReceiver;
    AlarmManagerImpl alarmManagerImpl;
    PendingIntent alarmPendingIntent;
    EditText etStart;
    TextInputLayout tvCountdownTimer;
    TextInputLayout tvDuration;
    TextInputLayout tvProgressiveCounting;
    public static EditText etLunchOut;
    public static EditText etLunchIn;
    TextView etEnd;
    SwitchMaterial switchBankHours;
    Button btnCalculate;
    FloatingActionButton fabClear;

    private static final String TAG = "MainActivity";
    public static final String MY_PREFERENCE = "mypref";
    public static final String HORA_INICIAL_KEY = "hora_inicial_key";
    public static final String HORA_FINAL_KEY = "hora_final_key";
    public static final String HORA_SAIDA_ALMOCO_KEY = "hora_saida_almoco";
    public static final String HORA_ENTRADA_ALMOCO_KEY = "hora_entrada_almoco";
    public static final String CHECK_BANK_OF_HOUR_KEY = "check_0147_Key";
    public static final String STR_DURACAO_TRABALHO__DIARIO_2021 = "08:15";
    public static final String STR_DURACAO_TRABALHO_BANCO_HORAS_PERMITIDAS = "10:00";
    public static final Integer INT_DURACAO_TRABALHO__DIARIO_HORA_2021 = 8;
    public static final Integer INT_DURACAO_TRABALHO__DIARIO_MINUTO_2021 = 15;
    private static final String FORMAT_HOUR_MIN_SEC = "%02d:%02d:%02d";
    private static final String FORMAT_HOUR_MIN = "%02d:%02d";
    public static final String ACTION_UPDATE_NOTIFICATION = "com.android.example.notifyme.ACTION_UPDATE_NOTIFICATION";
    public static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    public static final int NOTIFICATION_ID = 0;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    CountDownTimer countDownTimer;

    //cronometro horas trabalhadas
    private static long initialTime;
    private static Handler handler;
    private static boolean isRunning;
    private static final long MILLIS_IN_SEC = 1000L;

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                long ms = (System.currentTimeMillis() - initialTime);
                Log.d(TAG, "initialTime:" + initialTime + " System.currentTimeMillis:" + System.currentTimeMillis() + " ms:" + ms);
                String timeComplete;
                //caso não tenha nenhum campo de ALMOÇO preenchido
                if (TextUtils.isEmpty(etLunchOut.getText()) || TextUtils.isEmpty(etLunchIn.getText())) {
                    timeComplete = fullTime(ms);
                } else {//subtrair o tempo de almoço, caso os dois campos de almoço estejam preenchidos
                    long timeWorkedMillisWithIntervalLunch = ms - intervalLunchMS();
                    Log.d(TAG, "timeWorkedMillisWithIntervalLunch:" + timeWorkedMillisWithIntervalLunch + " = System.currentTimeMillis:" + System.currentTimeMillis() + " - initialTime:" + initialTime + " - ms:" + ms);
                    timeComplete = fullTime(timeWorkedMillisWithIntervalLunch);
                }
                Objects.requireNonNull(tvProgressiveCounting.getEditText()).setText(timeComplete);
                Log.d(TAG, "timeComplete: " + timeComplete);
                handler.postDelayed(runnable, MILLIS_IN_SEC);
            }
        }
    };

    /***
     *  ms / 3600000 % 24  HORAS 86400000  = 24 * 60 * 60 * 1000
     *  (ms / 60000) % 60  MINUTOS 60000   = 60 * 1000
     *  (ms / 1000) % 60)  SEGUNDOS
     * @return Return the hour formated. E.g: 00:00:00
     * */
    @SuppressLint("DefaultLocale")
    public String fullTime(long time) {
        long hour, minute, second;
        hour = (time / 3600000) % 24;
        minute = (time / 60000) % 60;
        second = (time / 1000) % 60;
        return format(FORMAT_HOUR_MIN_SEC, hour, minute, second);
    }

    /***
     *
     * @return Return the quantity of the milliSeconds of the interval of the lunch
     */
    public long intervalLunchMS() {
        Calendar calendarSaidaAlmoco = Calendar.getInstance();
        Calendar calendarEntradaAlmoco = Calendar.getInstance();
        calendarSaidaAlmoco.set(Calendar.SECOND, 0);
        calendarEntradaAlmoco.set(Calendar.SECOND, 0);
        calendarSaidaAlmoco.set(Calendar.HOUR_OF_DAY, Integer.parseInt(etLunchOut.getText().toString().split(":")[0]));
        calendarSaidaAlmoco.set(Calendar.MINUTE, Integer.parseInt(etLunchOut.getText().toString().split(":")[1]));
        calendarEntradaAlmoco.set(Calendar.HOUR_OF_DAY, Integer.parseInt(etLunchIn.getText().toString().split(":")[0]));
        calendarEntradaAlmoco.set(Calendar.MINUTE, Integer.parseInt(etLunchIn.getText().toString().split(":")[1]));

        return calendarEntradaAlmoco.getTimeInMillis() - calendarSaidaAlmoco.getTimeInMillis();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        //TODO Add linha com hora inicial para teste
        /* etInit.setText("09:00"); */
        verifySharedPreference();
        clicks();
        createNotificationChannel();
        // Register the broadcast receiver to receive the update action from the notification.
        registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));
    }

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
                    int hour, minute = 0;
                    Calendar mcurrentTime = Calendar.getInstance();
                    if (TextUtils.isEmpty(etStart.getText())) {
                        hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        minute = mcurrentTime.get(Calendar.MINUTE);
                    } else {
                        hour = getHour(etStart.getText().toString());
                        minute = getMinute(etStart.getText().toString());
                    }
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            etStart.setText(format(FORMAT_HOUR_MIN, selectedHour, selectedMinute));
                            etStart.setError(null);
                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle(R.string.select_time);
                    mTimePicker.show();
                }
        );
    }

    private void etLunchOutClickListener() {
        etLunchOut.setOnClickListener(
                v -> {
                    int hour, minute = 0;
                    Calendar mcurrentTime = Calendar.getInstance();

                    if (TextUtils.isEmpty(etLunchOut.getText())) {
                        hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        minute = mcurrentTime.get(Calendar.MINUTE);
                    } else {
                        hour = getHour(etLunchOut.getText().toString());
                        minute = getMinute(etLunchOut.getText().toString());
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

                        int hour, minute = 0;
                        Calendar mcurrentTime = Calendar.getInstance();
                        if (TextUtils.isEmpty(etLunchIn.getText())) {
                            hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                            minute = mcurrentTime.get(Calendar.MINUTE);
                        } else {
                            hour = getHour(etLunchIn.getText().toString());
                            minute = getMinute(etLunchIn.getText().toString());
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
                    Snackbar.make(view, clear_values, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    reset();
                    clearSharedPreferences();
                    cancelCountDownTimer();
                    cancelAlarm();
                }
        );
    }

    public void cancelCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void verifySharedPreference() {
        sharedPreferences = getApplication().getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (sharedPreferences.contains(HORA_INICIAL_KEY)) {
            String horaInicial = sharedPreferences.getString(HORA_INICIAL_KEY, "");
            if (!horaInicial.isEmpty()) {
                etStart.setText(horaInicial);
                Log.d(TAG, "verifySharedPreference: etStart: " + etStart.getText().toString());

                if (sharedPreferences.contains(CHECK_BANK_OF_HOUR_KEY)) {
                    Boolean check = sharedPreferences.getBoolean(CHECK_BANK_OF_HOUR_KEY, false);
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
        /*        Logica para calcular o countdown para sair do trabalho         */
        Calendar calendarCurrent = Calendar.getInstance();
        Date dataHoraEntrada = new Date();
        dataHoraEntrada.setHours(calendarCurrent.get(Calendar.HOUR_OF_DAY));
        dataHoraEntrada.setMinutes(calendarCurrent.get(Calendar.MINUTE));
        dataHoraEntrada.setSeconds(calendarCurrent.get(Calendar.SECOND));

        Date dataHoraSaida = new Date();
        dataHoraSaida.setHours(getHour(etEnd.getText().toString()));
        dataHoraSaida.setMinutes(getMinute(etEnd.getText().toString()));
        dataHoraSaida.setSeconds(getSecond());

        long diffDate = dataHoraSaida.getTime() - dataHoraEntrada.getTime();

        countDownTimer = new CountDownTimer(diffDate, 1000) { // adjust the milli seconds here 16069000

            public void onTick(long millisUntilFinished) {

                @SuppressLint("DefaultLocale") String minutosRestantes = format(FORMAT_HOUR_MIN_SEC,
                        MILLISECONDS.toHours(millisUntilFinished),
                        MILLISECONDS.toMinutes(millisUntilFinished) - HOURS.toMinutes(MILLISECONDS.toHours(millisUntilFinished)),
                        MILLISECONDS.toSeconds(millisUntilFinished) - MINUTES.toSeconds(MILLISECONDS.toMinutes(millisUntilFinished)));
                tvCountdownTimer.getEditText().setText(minutosRestantes);

                if (tvCountdownTimer.getEditText().getText().toString().equals("00:15:00")) {
                    tvCountdownTimer.setBackgroundColor(Color.GREEN);
                    sendNotification(15);
                } else if (tvCountdownTimer.getEditText().getText().toString().equals("00:10:00")) {
                    tvCountdownTimer.setBackgroundColor(Color.YELLOW);
                    sendNotification(10);
                } else if (tvCountdownTimer.getEditText().getText().toString().equals("00:05:00")) {
                    tvCountdownTimer.setBackgroundColor(Color.RED);
                    // Send the notification
                    sendNotification(5);
                } else if (tvCountdownTimer.getEditText().getText().toString().equals("00:02:00")) {
                    sendNotification(2);
                } else if (tvCountdownTimer.getEditText().getText().toString().equals("00:01:00")) {
                    sendNotification(1);
                }
            }

            public void onFinish() {
                if (switchBankHours.isChecked()) {
                    showAlertDialog(getString(R.string.horas_extras_0147));
                } else {
                    showAlertDialog(getString(R.string.horas_trabalhadas));
                    sendNotification(1);
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

    private void setAlarm(String horario) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        int hora = getHour(horario);
        int minutos = getMinute(horario);
        int segundos = getSecond();
        c.set(Calendar.HOUR_OF_DAY, hora);
        c.set(Calendar.MINUTE, minutos);
        c.set(Calendar.SECOND, segundos);
        //set alarm
        alarmPendingIntent = alarmManagerImpl.prepareAlarmPendingIntent(this);
        alarmManagerImpl.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), alarmPendingIntent);
        Toast.makeText(this, "Agendado para: " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Alarme set to: " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
    }

    private void cancelAlarm() {
        if (alarmManagerImpl.isAlarmExists(this)) {
            alarmManagerImpl.cancel(alarmPendingIntent);
            Toast.makeText(this, "Alarm Cancelled", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        int nightMode = AppCompatDelegate.getDefaultNightMode();
        if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            menu.findItem(R.id.menu_night_mode).setTitle(R.string.day_mode);
        } else {
            menu.findItem(R.id.menu_night_mode).setTitle(R.string.menu_night_mode);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        int nightMode = AppCompatDelegate.getDefaultNightMode();
        if (id == R.id.menu_notificaton) {
            Toast.makeText(this, "Sem notificações.", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.menu_action_settings) {
            return true;
        }
        if (id == R.id.menu_night_mode) {
            if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            recreate();
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
                etEnd.setText(calcular0147());
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
        dateTimeExit.set(Calendar.SECOND, getSecond());
        etEnd.setText(format(FORMAT_HOUR_MIN, dateTimeExit.get(Calendar.HOUR_OF_DAY), dateTimeExit.get(Calendar.MINUTE)));
    }

    public int getHour(String fullTime) {
        String[] horaMinuto = fullTime.split(":");
        int horaSaida = Integer.parseInt(horaMinuto[0]);
        return horaSaida;
    }

    public int getMinute(String fullTime) {
        String[] horaMinuto = fullTime.split(":");
        int minutoSaida = Integer.parseInt(horaMinuto[1]);
        return minutoSaida;
    }

    public int getSecond() {
        return 0;
    }


    public String calculateLunch() {
        if (validateLunch()) {
            return "01:00";
        }
        long timeLunchMS = intervalLunchMS();
        return fullTime(timeLunchMS);
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
        return diff_time(dateHoraEntrada, new Date());
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

    public String calcular0147() {
        String saida = etEnd.getText().toString();
        Date dateSaida = new Date();
        dateSaida.setHours(Integer.parseInt(saida.split(":")[0]));
        dateSaida.setMinutes(Integer.parseInt(saida.split(":")[1]));
        return calcular0147(dateSaida);
    }

    public String calcular0147(Date dateHoraInial) {
        int totalHoras = (dateHoraInial.getHours() * 60) + dateHoraInial.getMinutes();
        int horasPermitidas = 105;//01:45 == 105 minutos permitidos
        int total = totalHoras + horasPermitidas;
        int horas = total / 60;
        int minutos = total % 60;
        return format(FORMAT_HOUR_MIN, horas, minutos);
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
        ignoreNotification();
    }

    public String diff_time(Date saida, Date retorno) {
        int totalRetorno = retorno.getHours() * 60 + retorno.getMinutes();
        int totalSaida = saida.getHours() * 60 + saida.getMinutes();
        int total = totalRetorno - totalSaida;
        int horas = total / 60;
        int minutos = total % 60;
        return format(FORMAT_HOUR_MIN, horas, minutos);
    }

    /**
     * Start the widgets on screen
     */
    private void initUI() {
        setContentView(R.layout.vazare_main);
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        mReceiver = new NotificationReceiver();
        alarmManagerImpl = new AlarmManagerImpl().getInstance(this);
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
        unregisterReceiver(mReceiver);
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

    public void createNotificationChannel() {
        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription(getString(R.string.notification_channel_description));
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    public void sendNotification(int opcao) {
        // Create a notification manager object.
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);
        // Build the notification with all of the parameters using helper method
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder(opcao);
        // Add the action button using the pending intent.
        //definir a imagem da notificacao
        notifyBuilder.addAction(R.mipmap.ic_vazare, getString(R.string.ignore), updatePendingIntent);
        // Deliver the notification.
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }

    public void ignoreNotification() {
        mNotifyManager.cancel(NOTIFICATION_ID);
        Log.d(TAG, "ignoreNotification NOTIFICATION_ID: " + NOTIFICATION_ID);
    }

    private NotificationCompat.Builder getNotificationBuilder(int opcao) {
        // Set up the pending intent that is delivered when the notification is clicked.
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        String txtNotificacao = "";

        if (opcao == 5) txtNotificacao = getString(R.string.notification_text_5);
        else if (opcao == 10) txtNotificacao = getString(R.string.notification_text_10);
        else if (opcao == 15) txtNotificacao = getString(R.string.notification_text_15);
        else if (opcao == 1) txtNotificacao = getString(R.string.notification_text_1);
        // Build the notification with all of the parameters.
        return new NotificationCompat
                .Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(txtNotificacao)
                .setSmallIcon(R.mipmap.ic_vazare)
                .setAutoCancel(true)
                .setContentIntent(notificationPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.hours_worked);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.pedir_corrida, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                /* adb shell "dumpsys activity activities | grep mResumedActivity
                    mResumedActivity: ActivityRecord{5986b19 u0 com.example.vazare/.MainActivity t17}
                    mResumedActivity: ActivityRecord{51b9692 u0 com.ubercab/.presidio.app.core.root.RootActivity t16
                    mResumedActivity: ActivityRecord{5909209 u0 com.taxis99/com.didi.sdk.app.MainActivityImpl t18}
                    mResumedActivity: ActivityRecord{c91a10d u0 net.taxidigital.tocantins/net.taxidigital.ui.main.MainActivity t19}
                 */
                Intent launchIntent;
                if (!switchBankHours.isChecked()) {
                    launchIntent = getPackageManager().getLaunchIntentForPackage("com.ubercab");
                } else {
                    launchIntent = getPackageManager().getLaunchIntentForPackage("com.taxis99");
                    if (launchIntent == null) {
                        launchIntent = getPackageManager().getLaunchIntentForPackage("net.taxidigital.tocantins");
                        if (launchIntent == null) {
                            launchIntent = getPackageManager().getLaunchIntentForPackage("com.ubercab");
                        }
                    }
                }
                if (launchIntent != null) {
                    startActivity(launchIntent);
                    Toast.makeText(MainActivity.this, "Abrindo App de Corrida.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Aplicativos: UBER, 99Taxi ou Tocantins não foram encontrados.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //define um botão como negativo.
        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(MainActivity.this, R.string.close_popup, Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alerta = builder.create();
        alerta.show();
    }

    public boolean validate() {
        if (TextUtils.isEmpty(etStart.getText())) {
            etStart.setError(getString(R.string.put_hour_initial));
            etStart.requestFocus();
            return false;
        }
        return true;
    }

    //--------------------new class//
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
