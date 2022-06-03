package school.campusconnect.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.VideoCallingActivity;
import school.campusconnect.activities.ZoomCallActivity;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.LeadItem;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BackgroundVideoUploadChapterService;
import school.campusconnect.utils.Constants;

public class IncomingVideoCallService extends Service {
    String TAG = "IncomingVideoCallService";
    String meetingID,zoomName,className,createID,password;
    String image,name;


    CountDownTimer countDownTimer = new CountDownTimer(1*60000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            Log.e(TAG,"onTick"+millisUntilFinished);
        }

        @Override
        public void onFinish() {

            new SendNotification(createID,"Decline","RejectVideoCall").execute();
            Log.e(TAG, "Clicked DECLINE");
            stopForeground(true);
            stopSelf();
        }
    };

    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(Constants.STARTFOREGROUND_ACTION)) {

            countDownTimer.start();

            password = intent.getStringExtra("password");
            meetingID = intent.getStringExtra("meetingID");
            createID = intent.getStringExtra("createdID");
            zoomName = intent.getStringExtra("zoomName");
            className = intent.getStringExtra("className");
            image= intent.getStringExtra("image");
            name = intent.getStringExtra("name");

            sendVideoCallNotification(intent.getStringExtra("msg"));

            Log.e(TAG, "START SERVICE"+createID);

        }else if (intent.getAction().equals(Constants.ACCPET_ACTION_SCREEN)) {
            countDownTimer.cancel();

            Log.e(TAG, "Clicked ACCEPT"+createID);

            meetingID = intent.getStringExtra("meetingID");
            createID = intent.getStringExtra("createdID");
            zoomName = intent.getStringExtra("zoomName");
            className = intent.getStringExtra("className");
            image= intent.getStringExtra("image");
            name = intent.getStringExtra("name");

            new SendNotification(createID,"Accept CAll","AcceptVideoCall").execute();

            stopForeground(true);
            stopSelf();

            Intent fullScreenIntent = new Intent(this, ZoomCallActivity.class);
            fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            fullScreenIntent.putExtra("className",className);
            fullScreenIntent.putExtra("password",password);
            fullScreenIntent.putExtra("meetingID",meetingID);
            fullScreenIntent.putExtra("zoomName",zoomName);
            fullScreenIntent.putExtra("image",image);
            fullScreenIntent.putExtra("created",createID);
            fullScreenIntent.putExtra("isMessage",true);
            fullScreenIntent.putExtra("name",name);
            startActivity(fullScreenIntent);
        }
        else if (intent.getAction().equals(Constants.DECLINE_ACTION_SCREEN)) {
            countDownTimer.cancel();

            createID = intent.getStringExtra("createdID");

            Log.e(TAG, "Received Stop Foreground Intent"+createID);

            new SendNotification(createID,"Decline","RejectVideoCall").execute();

            stopForeground(true);
            stopSelf();
        }
        else if (intent.getAction().equals(Constants.ACCPET_ACTION)) {

            countDownTimer.cancel();

            Log.e(TAG, "Clicked ACCEPT"+createID);

         /*   meetingID = intent.getStringExtra("meetingID");
            createID = intent.getStringExtra("createdID");
            zoomName = intent.getStringExtra("zoomName");
            className = intent.getStringExtra("className");
            image= intent.getStringExtra("image");
            name = intent.getStringExtra("name");*/

            new SendNotification(createID,"Accept CAll","AcceptVideoCall").execute();

            stopForeground(true);
            stopSelf();

            Intent fullScreenIntent = new Intent(this, ZoomCallActivity.class);
            fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            fullScreenIntent.putExtra("className",className);
            fullScreenIntent.putExtra("password",password);
            fullScreenIntent.putExtra("meetingID",meetingID);
            fullScreenIntent.putExtra("zoomName",zoomName);
            fullScreenIntent.putExtra("image",image);
            fullScreenIntent.putExtra("isMessage",true);
            fullScreenIntent.putExtra("name",name);
            startActivity(fullScreenIntent);

        }
        else if (intent.getAction().equals(Constants.DECLINE_ACTION)) {

            countDownTimer.cancel();

          //  createID = intent.getStringExtra("createdID");

            Log.e(TAG, "Clicked DECLINE"+createID);

            new SendNotification(createID,"Decline","RejectVideoCall").execute();



            stopForeground(true);
            stopSelf();
        }
        else if (intent.getAction().equals(Constants.STOPFOREGROUND_ACTION)) {

            countDownTimer.cancel();

          //  createID = intent.getStringExtra("createdID");

            Log.e(TAG, "Received Stop Foreground Intent"+createID);

       //     new SendNotification(createID,"Decline","RejectVideoCall").execute();


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
        fullScreenIntent.putExtra("password",password);
        fullScreenIntent.putExtra("name",name);
        fullScreenIntent.putExtra("createdID",createID);
        fullScreenIntent.putExtra("image",image);
        fullScreenIntent.putExtra("isMessage",true);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

     //   RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.layout_video_calling_notification);
        Intent Accept = new Intent(this, IncomingVideoCallService.class);
        Accept.setAction(Constants.ACCPET_ACTION);
        /*Accept.putExtra("className",className);
        Accept.putExtra("createdID",createID);
        Accept.putExtra("meetingID",meetingID);
        Accept.putExtra("zoomName",zoomName);*/
        PendingIntent pAcceptIntent = PendingIntent.getService(this, 0, Accept, 0);

        Intent decline = new Intent(this, IncomingVideoCallService.class);
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

    private class SendNotification extends AsyncTask<String, String, String> {
        private String server_response;
        String data,msg,type;

        public SendNotification(String leadItem,String msg,String type) {
            this.data = leadItem;
            this.msg = msg;
            this.type = type;
        }

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL("https://fcm.googleapis.com/fcm/send");
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", BuildConfig.API_KEY_FIREBASE1 + BuildConfig.API_KEY_FIREBASE2);
                urlConnection.setRequestProperty("Content-Type", "application/json");


                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                try {
                    JSONObject object = new JSONObject();

                    String topic;
                    String title = getResources().getString(R.string.app_name);
                    String name = LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.NAME);


                    object.put("to", data);

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title", title);
                    notificationObj.put("body", msg);
                    //  object.put("notification", notificationObj);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("groupId", GroupDashboardActivityNew.groupId);
                    dataObj.put("createdById", LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.LOGIN_ID));
                    dataObj.put("createdByImage", LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.PROFILE_IMAGE_NEW));
                    dataObj.put("createdByName", name);
                    dataObj.put("iSNotificationSilent",true);
                    dataObj.put("isVideoCall",false);
                    dataObj.put("Notification_type", type);
                    dataObj.put("body", msg);
                    object.put("data", dataObj);
                    wr.writeBytes(object.toString());
                    Log.e(TAG, " JSON input : " + object.toString());
                    wr.flush();
                    wr.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.e(TAG, " Exception : " + ex.getMessage());
                }
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                AppLog.e(TAG, "responseCode :" + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_response = readStream(urlConnection.getInputStream());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                AppLog.e(TAG, "MalformedURLException :" + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                AppLog.e(TAG, "IOException :" + e.getMessage());
            }


            return server_response;
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            AppLog.e(TAG, "server_response :" + server_response);

            if (!TextUtils.isEmpty(server_response)) {
                AppLog.e(TAG, "Notification Sent");
            } else {
                AppLog.e(TAG, "Notification Send Fail");
            }
        }
    }

}
