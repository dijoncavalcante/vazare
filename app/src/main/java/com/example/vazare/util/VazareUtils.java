package com.example.vazare.util;

import android.annotation.SuppressLint;

import java.util.Calendar;
import java.util.Date;

import static com.example.vazare.util.Constants.FORMAT_HOUR_MIN;
import static com.example.vazare.util.Constants.FORMAT_HOUR_MIN_SEC;
import static com.example.vazare.util.Constants.SECOND_DEFAULT;
import static java.lang.String.format;

public class VazareUtils {
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
    public long intervalLunchMS(String etLunchOut, String etLunchIn) {
        Calendar calendarSaidaAlmoco = Calendar.getInstance();
        Calendar calendarEntradaAlmoco = Calendar.getInstance();
        calendarSaidaAlmoco.set(Calendar.SECOND, 0);
        calendarEntradaAlmoco.set(Calendar.SECOND, 0);
        calendarSaidaAlmoco.set(Calendar.HOUR_OF_DAY, Integer.parseInt(etLunchOut.split(":")[0]));
        calendarSaidaAlmoco.set(Calendar.MINUTE, Integer.parseInt(etLunchOut.split(":")[1]));
        calendarEntradaAlmoco.set(Calendar.HOUR_OF_DAY, Integer.parseInt(etLunchIn.split(":")[0]));
        calendarEntradaAlmoco.set(Calendar.MINUTE, Integer.parseInt(etLunchIn.split(":")[1]));
        return calendarEntradaAlmoco.getTimeInMillis() - calendarSaidaAlmoco.getTimeInMillis();
    }

    public int getMinute(String fullTime) {
        String[] horaMinuto = fullTime.split(":");
        int minutoSaida = Integer.parseInt(horaMinuto[1]);
        return minutoSaida;
    }

    public int getHour(String fullTime) {
        String[] horaMinuto = fullTime.split(":");
        int horaSaida = Integer.parseInt(horaMinuto[0]);
        return horaSaida;
    }

    public Calendar getCalendar(String hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, getHour(hour));
        calendar.set(Calendar.MINUTE, getMinute(hour));
        calendar.set(Calendar.SECOND, SECOND_DEFAULT);
        return calendar;
    }

    public String calculateBH(Date dateHoraInial) {
        int totalHoras = (dateHoraInial.getHours() * 60) + dateHoraInial.getMinutes();
        int horasPermitidas = 105;//01:45 == 105 minutos permitidos
        int total = totalHoras + horasPermitidas;
        int horas = total / 60;
        int minutos = total % 60;
        return format(FORMAT_HOUR_MIN, horas, minutos);
    }

    public String diff_time(Date saida, Date retorno) {
        int totalRetorno = retorno.getHours() * 60 + retorno.getMinutes();
        int totalSaida = saida.getHours() * 60 + saida.getMinutes();
        int total = totalRetorno - totalSaida;
        int horas = total / 60;
        int minutos = total % 60;
        return format(FORMAT_HOUR_MIN, horas, minutos);
    }

    public String calculateBH(String horaSaida) {
        String saida = horaSaida;
        Date dateSaida = new Date();
        dateSaida.setHours(Integer.parseInt(saida.split(":")[0]));
        dateSaida.setMinutes(Integer.parseInt(saida.split(":")[1]));
        return calculateBH(dateSaida);
    }
}
