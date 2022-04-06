package school.campusconnect.utils;

import android.text.TextUtils;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeHelper {
    private static final String TAG = "DateTimeHelper";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String DATE_FORMAT_TIME = "yyyy-MM-dd HH:mm:ss";


    public static Calendar getCalendarObj(String strDate, String format, boolean returnDefaultIfError) {
        if (TextUtils.isEmpty(strDate)) {
            if (returnDefaultIfError) {
                return Calendar.getInstance();
            } else {
                return null;
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        try {
            Date date = sdf.parse(strDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            Log.e(TAG, e.toString());
            if (returnDefaultIfError) {
                return Calendar.getInstance();
            } else {
                return null;
            }
        }
    }

    public static String convertFormat(String strDate, String format, String convertFormat) {
        if (TextUtils.isEmpty(strDate)) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        SimpleDateFormat sdfNew = new SimpleDateFormat(convertFormat, Locale.getDefault());

        try {
            Date date = sdf.parse(strDate);
            return sdfNew.format(date);
        } catch (ParseException e) {
            Log.e(TAG, e.toString());
            return "";
        }
    }

    public static String covertTimeFormat(String strDate, String format, String convertFormat) {
        if (TextUtils.isEmpty(strDate)) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        SimpleDateFormat sdfNew = new SimpleDateFormat(convertFormat, Locale.getDefault());

        try {
            Date date = sdf.parse(strDate);
            return sdfNew.format(date);
        } catch (ParseException e) {
            Log.e(TAG, e.toString());
            return "";
        }
    }
    public static String convertFormat(Date date, String convertFormat) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdfNew = new SimpleDateFormat(convertFormat, Locale.getDefault());
        return sdfNew.format(date);
    }

    public static String getDate(String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
            Date date = new Date();
            return sdf.format(date);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.toString());
            return "";
        }
    }

    public static String getDate(Calendar calendar, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
            return sdf.format(calendar.getTime());
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.toString());
            return "";
        }
    }

    public static String getAge(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(strDate));

            int diff = Calendar.getInstance().get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
            return diff + "";
        } catch (ParseException e) {
            Log.e(TAG, e.toString());
            return "";
        }
    }

    public static String getPriceFormatted(long price) {
       /* NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(0);
        format.setCurrency(Currency.getInstance("INR"));
        return format.format(price);*/

        DecimalFormat formatter = new DecimalFormat("##,##,###");
        String priceFormat = "₹ " + formatter.format(price);
        return priceFormat;
    }

    public static String getPriceFormattedFloat(float price) {

        Log.e(TAG,"price"+price);
        DecimalFormat formatter = new DecimalFormat("##.#");
        String priceFormat =formatter.format(price);
        Log.e(TAG,"After Format price"+String.format("%.1f", price));
        return priceFormat;

    }

    public static String getCurrentTime() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Log.e(TAG,"getCurrentTime" +sdf.format(new Date()));
        return sdf.format(new Date());
    }

}