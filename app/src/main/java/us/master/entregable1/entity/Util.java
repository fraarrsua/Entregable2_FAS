package us.master.entregable1.entity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Util {

    public static double latitud = 0d;
    public static double longitud = 0d;

    public static String formateaFecha(Calendar calendar) {
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        DateFormat formatoFecha = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
        calendar.setTimeInMillis(0);
        calendar.set(yy, mm, dd, 0, 0, 0);
        Date chosenDate = calendar.getTime();
        return (formatoFecha.format(chosenDate));

    }

    public static String formateaFecha(long fecha) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(fecha * 1000);
        DateFormat formatoFecha = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
        Date chosenDate = calendar.getTime();
        return (formatoFecha.format(chosenDate));
    }

    public static long Calendar2long(Calendar fecha) {
        return (fecha.getTimeInMillis() / 1000);
    }

    public static List<Trip> triplist;
}