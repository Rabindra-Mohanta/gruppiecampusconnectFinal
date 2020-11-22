package school.campusconnect.utils;

import android.util.Log;

import school.campusconnect.BuildConfig;

public class AppLog {
    public static void e(String TAG, String msg) {
        if (BuildConfig.DEBUG)
            Log.e(TAG, msg);
    }

    public static void i(String TAG, String msg) {
        if (BuildConfig.DEBUG)
            Log.i(TAG, msg);
    }

    public static void d(String TAG, String msg) {
        if (BuildConfig.DEBUG)
            Log.d(TAG, msg);
    }

    public static void v(String TAG, String msg) {
        if (BuildConfig.DEBUG)
            Log.v(TAG, msg);
    }

}