/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package school.campusconnect.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.activeandroid.ActiveAndroid;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Map;

import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.firebase.SendNotificationModel;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        AppLog.e(TAG, "From: " + "onMessageReceived");
        AppLog.e(TAG, "From: " + remoteMessage.getFrom());

        if (LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.TOKEN).isEmpty()) {
            return;
        }

        if (remoteMessage.getData().size() > 0) {

            ActiveAndroid.initialize(this);

            AppLog.e(TAG, "Message data payload: " + remoteMessage.getData());
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
            SendNotificationModel.SendNotiData data = new Gson().fromJson(new JSONObject(remoteMessage.getData()).toString(), SendNotificationModel.SendNotiData.class);
            AppLog.e(TAG, "Data Notification: " + data);

            String loginId = LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.LOGIN_ID);

            if (!loginId.equals(data.createdById)) {

                LeafPreference leafPreference = LeafPreference.getInstance(getApplicationContext());

                switch (data.Notification_type) {
                    case "videoEnd": {
                        Intent intent = new Intent("MEETING_END");
                        intent.putExtra("teamId", data.teamId);
                        intent.putExtra("createdByName", data.createdByName);
                        intent.setAction("MEETING_END");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                        leafPreference.remove(data.teamId + "_liveclass");
                    }
                    break;

                    case "videoStart": {
                        Intent intent = new Intent("MEETING_START");
                        intent.putExtra("teamId", data.teamId);
                        intent.putExtra("createdByName", data.createdByName);
                        intent.setAction("MEETING_START");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                        leafPreference.setString(data.teamId + "_liveclass", new Gson().toJson(data));
                        AppLog.e(TAG , "onMessageReceived : videoStart "+new Gson().toJson(data));
                    }
                    break;

                    case "videoResume": {
                        Intent intent = new Intent("MEETING_RESUME");
                        intent.putExtra("teamId", data.teamId);
                        intent.setAction("MEETING_RESUME");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    }
                    break;

                    case "post": {
                        if ("team".equalsIgnoreCase(data.postType)) {
                            leafPreference.setInt(data.teamId + "_post", leafPreference.getInt(data.teamId + "_post") + 1);
                        } else if ("group".equalsIgnoreCase(data.postType)) {
                            leafPreference.setInt(data.groupId + "_post", leafPreference.getInt(data.groupId + "_post") + 1);
                        }
                    }
                    break;
                    case "VendorAdd": {
                        AppLog.e(TAG, "vendorAdd type notifcation .,, preference saving started.");
                        leafPreference.setInt(data.groupId + "_vendorpush", leafPreference.getInt(data.groupId + "_vendorpush") + 1);
                    }
                    break;

                    case "RuleAdd": {
                        leafPreference.setInt(data.groupId + "_cocpush", leafPreference.getInt(data.groupId + "_cocpush") + 1);
                    }
                    break;

                    case "EBookAdd": {
                        leafPreference.setInt(data.teamId + "_ebookpush", leafPreference.getInt(data.teamId + "_ebookpush") + 1);
                    }
                    break;
                    case "ASSIGNMENT_STATUS": {
                        leafPreference.setInt(data.teamId + "_ass_count_noti", leafPreference.getInt(data.teamId + "_ass_count_noti") + 1);
                    }
                    case "TEST_PAPER_STATUS": {
                        leafPreference.setInt(data.teamId + "_test_count_noti", leafPreference.getInt(data.teamId + "_test_count_noti") + 1);
                    }
                    break;

                    case "DELETE_EBOOK": {
                        leafPreference.setBoolean(data.teamId + "_ebook_delete", true);
                    }
                    break;
                    case "DELETE_VENDOR": {
                        leafPreference.setBoolean(data.groupId + "_vendor_delete", true);
                    }
                    break;
                    case "DELETE_RULE": {
                        leafPreference.setBoolean(data.groupId + "_rule_delete", true);
                    }
                    break;

                    case "DELETE_POST": {
                        if ("team".equalsIgnoreCase(data.postType)) {
                            leafPreference.setBoolean(data.teamId + "_post_delete", true);
                        } else if ("group".equalsIgnoreCase(data.postType)) {
                            leafPreference.setBoolean(data.groupId + "_post_delete", true);
                        }
                    }
                    break;


                }
                if (!data.iSNotificationSilent) {
                    sendNotification(data.body, data.title);
                }
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            AppLog.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     * @param title
     */
    private void sendNotification(String messageBody, String title) {
        AppLog.e(TAG, "sendNotification() called");

        String CHANNEL_ID = "gruppie_02";// The id of the channel.

        Intent intent = new Intent(this, GroupDashboardActivityNew.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(messageBody))
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setShowBadge(true);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
        }
        assert notificationManager != null;
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        AppLog.e(TAG, "Refreshed token: " + s);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(s);
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        LeafPreference lp = LeafPreference.getInstance(getApplicationContext());
        lp.setString(LeafPreference.GCM_TOKEN, token);
    }
}
