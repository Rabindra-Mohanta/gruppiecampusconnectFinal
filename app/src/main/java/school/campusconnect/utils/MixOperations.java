package school.campusconnect.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by frenzin05 on 8/17/2017.
 */

public class MixOperations {

    private static final String TAG = "MixOperations";

    public static boolean hasPermission(Context context, String[] permissions) {

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && context!=null && permissions!=null)
        {
            for(String permission:permissions)
            {
                if(ActivityCompat.checkSelfPermission(context,permission)!= PackageManager.PERMISSION_GRANTED)
                    return false;
            }
        }
        return true;
    }
    //for only date
    public static String getFormattedDate(String dt, String inputPattern)
    {
        Date date;
        String str;

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
            date = inputFormat.parse(dt);

            Date now = Calendar.getInstance().getTime();
           AppLog.e(TAG,"Now =>"+now.toString());
           AppLog.e(TAG,"Date =>"+date.toString());

            long diff = now.getTime() - date.getTime();
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
           AppLog.e(TAG,"minutes diff=>"+minutes);
            if(minutes<1)
            {
                return "Just now";
            }
            else
                {
                if(minutes>60)
                {
                    long hours = TimeUnit.MILLISECONDS.toHours(diff);

                    if(hours>24)
                    {
                        long days = TimeUnit.MILLISECONDS.toDays(diff);

                        if(days>31)
                        {
                            return outputFormat.format(date);
                        }
                        else {
                            return days+" days ago";
                        }
                    }
                    else {
                        return hours+" hours ago";
                    }
                }
                else {
                    return minutes+" minutes ago";
                }
            }

        } catch (Exception e) {
           AppLog.e(TAG,e.toString());
            return "";
        }
    }

    public static String getFormattedDateOnly(String dt, String inputPattern,String of)
    {
        Date date;
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat outputFormat = new SimpleDateFormat(of);
            date = inputFormat.parse(dt);
            return outputFormat.format(date);
        } catch (Exception e) {
            AppLog.e(TAG,e.toString());
            return "";
        }
    }
    public static boolean isNewEvent(String eventAt, String inputPattern,long now)
    {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            long eventAtMillisec = inputFormat.parse(eventAt).getTime();

            Log.e(TAG,"now "+now);
            Log.e(TAG,"eventAtMillisec "+eventAtMillisec);
            if(now<eventAtMillisec){
                return true;
            }
        } catch (Exception e) {
            AppLog.e(TAG,e.toString());
            return false;
        }
        return false;
    }
    public static boolean isNewEvent(String eventAt, String inputPattern,String now)
    {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            long eventAtMillisec = inputFormat.parse(eventAt).getTime();
            long nowMillis = inputFormat.parse(now).getTime();
            if(nowMillis<eventAtMillisec)
            {
                return true;
            }
        } catch (Exception e) {
            AppLog.e(TAG,e.toString());
            return false;
        }
        return false;
    }
    public static String convertBase64(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }
    public static Date getDateFromStringDate(String dt,String format)
    {
        SimpleDateFormat inputFormat = new SimpleDateFormat(format);

        try {
            return inputFormat.parse(dt);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }
    public static String convertDate(Date date,String format)
    {
        SimpleDateFormat inputFormat = new SimpleDateFormat(format);
        return inputFormat.format(date);
    }
    public static String getMonth(Date date)
    {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MMM");
        return inputFormat.format(date);
    }


    public static void deleteVideoFile(String filepath)
    {

        AppLog.e(TAG , "deleteVideoFile called : "+filepath );
        if(filepath ==null)
        {
            return;
        }

        File file = new File(filepath);
        AppLog.e(TAG, "file exists ? " +file.exists()+" , filepath : "+filepath);

        if (file.exists())
        {
            file.delete();
            // contentResolver.delete(filesUri, where, selectionArgs);
        }
        AppLog.e(TAG, "file deleted ? " +!file.exists());

    }
}
