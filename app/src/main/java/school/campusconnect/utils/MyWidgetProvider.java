package school.campusconnect.utils;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import school.campusconnect.R;

public class MyWidgetProvider extends AppWidgetProvider {

    private static final String MyOnClick1 = "myOnClickTag1";
    private static final String MyOnClick2 = "myOnClickTag2";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // Get all ids
        ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_video_calling_notification);

            remoteViews.setOnClickPendingIntent(R.id.btnStart, getPendingSelfIntent(context, MyOnClick1));
            remoteViews.setOnClickPendingIntent(R.id.btnStop, getPendingSelfIntent(context, MyOnClick2));


            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (MyOnClick1.equals(intent.getAction())) {
            Toast.makeText(context, "Button1", Toast.LENGTH_SHORT).show();
        } else if (MyOnClick2.equals(intent.getAction())) {
            Toast.makeText(context, "Button2", Toast.LENGTH_SHORT).show();
        }
    };
}