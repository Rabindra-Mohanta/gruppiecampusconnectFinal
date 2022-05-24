package school.campusconnect.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import school.campusconnect.R;
import school.campusconnect.activities.VideoCallingActivity;
import school.campusconnect.activities.ZoomCallActivity;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;

public class IncomingVideoCallService extends Service {
    String TAG = "IncomingVideoCallService";
    String meetingID,zoomName,className;
    String image,name;

    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(Constants.STARTFOREGROUND_ACTION)) {

            meetingID = intent.getStringExtra("meetingID");
            zoomName = intent.getStringExtra("zoomName");
            className = intent.getStringExtra("className");
            image= intent.getStringExtra("image");
            name = intent.getStringExtra("name");

            sendVideoCallNotification(intent.getStringExtra("msg"));

            Log.e(TAG, "START SERVICE");

        } else if (intent.getAction().equals(Constants.ACCPET_ACTION)) {

            stopForeground(true);
            stopSelf();

            meetingID = intent.getStringExtra("meetingID");
            zoomName = intent.getStringExtra("zoomName");
            className = intent.getStringExtra("className");

            Intent fullScreenIntent = new Intent(this, ZoomCallActivity.class);
            fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            fullScreenIntent.putExtra("className",className);
            fullScreenIntent.putExtra("meetingID",meetingID);
            fullScreenIntent.putExtra("zoomName",zoomName);
            startActivity(fullScreenIntent);
            Log.e(TAG, "Clicked ACCEPT");
        }
        else if (intent.getAction().equals(Constants.DECLINE_ACTION)) {

            Log.e(TAG, "Clicked DECLINE");
            stopForeground(true);
            stopSelf();
        }
        else if (intent.getAction().equals(Constants.STOPFOREGROUND_ACTION)) {
            Log.e(TAG, "Received Stop Foreground Intent");
            stopForeground(true);
            stopSelf();

        /*    Intent fullScreenIntent = new Intent(this, ZoomCallActivity.class);
            fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            fullScreenIntent.putExtra("className",className);
            fullScreenIntent.putExtra("meetingID",meetingID);
            fullScreenIntent.putExtra("zoomName",zoomName);
            startActivity(fullScreenIntent);*/

        }
        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public int createID(){
        Date now = new Date();
        return Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
    }

    private void sendVideoCallNotification(String s)
    {

        Intent fullScreenIntent = new Intent(this, VideoCallingActivity.class);
        fullScreenIntent.putExtra("className",className);
        fullScreenIntent.putExtra("meetingID",meetingID);
        fullScreenIntent.putExtra("zoomName",zoomName);
        fullScreenIntent.putExtra("name",name);
        fullScreenIntent.putExtra("image",image);
        fullScreenIntent.putExtra("isMessage",true);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.layout_video_calling_notification);

        Intent Accept = new Intent(this, IncomingVideoCallService.class);
        Accept.setAction(Constants.ACCPET_ACTION);
        Accept.putExtra("className",className);
        Accept.putExtra("meetingID",meetingID);
        Accept.putExtra("zoomName",zoomName);
        PendingIntent pAcceptIntent = PendingIntent.getService(this, 0, Accept, 0);

        Intent decline = new Intent(this, IncomingVideoCallService.class);
        decline.setAction(Constants.DECLINE_ACTION);
        PendingIntent pdeclineIntent = PendingIntent.getService(this, 0, decline, 0);

        mRemoteViews.setOnClickPendingIntent(R.id.btnStart, pAcceptIntent);
        mRemoteViews.setOnClickPendingIntent(R.id.btnStop, pdeclineIntent);

        mRemoteViews.setTextViewText(R.id.tvNotificationTitle,s);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "1213")
                        .setSmallIcon(R.drawable.app_icon)
                        .setContent(mRemoteViews)
                        .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_CALL)
                        .setChannelId("1213")
                        .setAutoCancel(true)
                        .setFullScreenIntent(fullScreenPendingIntent, true);

        Notification incomingCallNotification = notificationBuilder.build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        AudioAttributes attr = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel("1213", name, importance);
            mChannel.setShowBadge(true);
            mChannel.enableVibration(true);
            mChannel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI,attr);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
        }

        startForeground(createID(), incomingCallNotification);

    }
}
