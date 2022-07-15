package school.campusconnect.utils;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import school.campusconnect.R;

public class MyWidgetProvider extends BroadcastReceiver {

    private static final String MyOnClick1 = "myOnClickTag1";
    private static final String MyOnClick2 = "myOnClickTag2";


    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "Image clicked", Toast.LENGTH_SHORT).show();
    };
}