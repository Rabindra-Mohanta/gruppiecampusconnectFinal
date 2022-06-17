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

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import school.campusconnect.R;
import school.campusconnect.activities.LiveTeacherClassActivity;
import school.campusconnect.activities.VideoCallingActivity;
import school.campusconnect.activities.VideoClassActivity;
import school.campusconnect.activities.ZoomCallActivity;
import school.campusconnect.utils.Constants;

public class IncomingLiveClassService extends Service {

    public String TAG = "IncomingLiveClassService";
    String teamID,createdName,createdImage,title,category;
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(Constants.STARTFOREGROUND_ACTION)) {

            Log.e(TAG,"STARTFOREGROUND_ACTION");

            Intent intents = new Intent("MEETING_START");
            intents.putExtra("teamId", teamID);
            intents.putExtra("createdByName", createdName);
            intents.setAction("MEETING_START");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

            createdImage = intent.getStringExtra("createdImage");
            createdName = intent.getStringExtra("createdName");
            teamID = intent.getStringExtra("teamId");
            title = intent.getStringExtra("title");
            category = intent.getStringExtra("category");

            sendVideoCallNotification(intent.getStringExtra("msg"));
        }
        else if (intent.getAction().equals(Constants.DECLINE_ACTION_SCREEN)) {

            stopForeground(true);
            stopSelf();
        }
        else if (intent.getAction().equals(Constants.ACCPET_ACTION)) {

            stopForeground(true);
            stopSelf();

            Intent intents = new Intent(this, VideoClassActivity.class);
            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intents.putExtra("title", title);
            intents.putExtra("category",category);
            startActivity(intents);
        }
        else if (intent.getAction().equals(Constants.DECLINE_ACTION)) {

            stopForeground(true);
            stopSelf();

        }
        else if (intent.getAction().equals(Constants.STOPFOREGROUND_ACTION)) {

            stopForeground(true);
            stopSelf();
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

    private void sendVideoCallNotification(String s)
    {
        Log.e(TAG,"STARTFOREGROUND_ACTION");

        Intent fullScreenIntent = new Intent(this, LiveTeacherClassActivity.class);
        fullScreenIntent.putExtra("teamId",teamID);
        fullScreenIntent.putExtra("name",createdName);
        fullScreenIntent.putExtra("image",createdImage);
        fullScreenIntent.putExtra("title",title);
        fullScreenIntent.putExtra("category",category);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //   RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.layout_video_calling_notification);
        Intent Accept = new Intent(this, IncomingLiveClassService.class);
        Accept.setAction(Constants.ACCPET_ACTION);
        /*Accept.putExtra("className",className);
        Accept.putExtra("createdID",createID);
        Accept.putExtra("meetingID",meetingID);
        Accept.putExtra("zoomName",zoomName);*/
        PendingIntent pAcceptIntent = PendingIntent.getService(this, 0, Accept, 0);

        Intent decline = new Intent(this, IncomingLiveClassService.class);
        decline.setAction(Constants.DECLINE_ACTION);
        /*  decline.putExtra("createdID",createID);*/
        PendingIntent pdeclineIntent = PendingIntent.getService(this, 0, decline, 0);

      /*  mRemoteViews.setOnClickPendingIntent(R.id.btnStart, pAcceptIntent);
        mRemoteViews.setOnClickPendingIntent(R.id.btnStop, pdeclineIntent);

        mRemoteViews.setTextViewText(R.id.tvNotificationTitle,s);*/

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "1213")
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(s)
                        .addAction(R.drawable.red_btn_bg_normal,"Denied",pdeclineIntent)
                        .addAction(R.drawable.green_call_btn_bg,"Accept",pAcceptIntent)
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

    public int createID(){
        Date now = new Date();
        return Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
    }
}
