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

import java.util.Map;

import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.database.LeafPreference;

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
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        AppLog.e(TAG, "From: " + "onMessageReceived");
        AppLog.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            AppLog.e(TAG, "Message data payload: " + remoteMessage.getData());

            Map<String, String> data = remoteMessage.getData();

            ActiveAndroid.initialize(this);

            AppLog.e(TAG, "Message Notification groupId: " + data.get("groupId"));
            AppLog.e(TAG, "Message Notification createdById: " + data.get("createdById"));
            AppLog.e(TAG, "Message Notification postId: " + data.get("postId"));
            AppLog.e(TAG, "Message Notification dateTime: " + data.get("teamId"));

            AppLog.e(TAG, "Message Notification Title: " + data.get("title"));
            AppLog.e(TAG, "Message Notification Body: " + data.get("body"));
            AppLog.e(TAG, "Message Notification postType: " + data.get("postType"));
            AppLog.e(TAG, "Message Notification Notification_type: " + data.get("Notification_type"));

           /* AppLog.e(TAG, "Message Notification icon: " + data.get("icon"));
            AppLog.e(TAG, "Message Notification type: " + data.get("type"));
            AppLog.e(TAG, "Message Notification dateTime: " + data.get("dateTime"));*/


            String loginId = LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.LOGIN_ID);
            AppLog.e(TAG, "Login Id: " + loginId);
            String createdId = data.get("createdById");


            if (!loginId.equals(createdId)) {
                AppLog.e(TAG, "if...");
                if (!LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.TOKEN).isEmpty()) {
                    AppLog.e(TAG, "if...if...");

                    if ("videoEnd".equals(data.get("Notification_type")))
                    {
                        Intent intent = new Intent("MEETING_END");
                        sendBroadcast(intent);
                    }
                    else if ("videoStart".equals(data.get("Notification_type")))
                    {
                        AppLog.e(TAG, "if...if...if...");
                        Intent intent = new Intent("MEETING_START");
                        intent.putExtra("teamId" ,data.get("teamId"));
                        intent.putExtra("createdByName" ,data.get("createdByName"));
                        intent.setAction("MEETING_START");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    }
                    else if ("videoResume".equals(data.get("Notification_type")))
                    {
                        AppLog.e(TAG, "if...if...if...");
                        Intent intent = new Intent("MEETING_RESUME");
                        intent.putExtra("teamId" ,data.get("teamId"));
                        intent.setAction("MEETING_RESUME");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    }
                    else if("post".equalsIgnoreCase(data.get("Notification_type")))
                    {
                        if("team".equalsIgnoreCase(data.get("postType")))
                        {
                            LeafPreference leafPreference = LeafPreference.getInstance(getApplicationContext());
                            leafPreference.setInt(data.get("teamId")+"_post" , leafPreference.getInt(data.get("teamId")+"_post" )+1);
                        }
                        else if("group".equalsIgnoreCase(data.get("postType")))
                        {
                            LeafPreference leafPreference = LeafPreference.getInstance(getApplicationContext());
                            leafPreference.setInt(data.get("groupId")+"_post" , leafPreference.getInt(data.get("groupId")+"_post" )+1);
                        }
                    }
                    else if("VendorAdd".equalsIgnoreCase(data.get("Notification_type")))
                    {
                        AppLog.e(TAG , "vendorAdd type notifcation .,, preference saving started.");
                            LeafPreference leafPreference = LeafPreference.getInstance(getApplicationContext());
                            leafPreference.setInt(data.get("groupId")+"_vendorpush" , leafPreference.getInt(data.get("groupId")+"_vendorpush" )+1);
                        AppLog.e(TAG , "vendorAdd type notifcation .,, preference saving completed. key : "+(data.get("groupId")+"_vendorpush"));

                    }
                    else if("VendorDelete".equalsIgnoreCase(data.get("Notification_type")))
                    {
                        AppLog.e(TAG , "vendorAdd type notifcation .,, preference saving started.");
                        LeafPreference leafPreference = LeafPreference.getInstance(getApplicationContext());
                        leafPreference.setInt(data.get("groupId")+"_vendorpush" , leafPreference.getInt(data.get("groupId")+"_vendorpush" )+1);
                        AppLog.e(TAG , "vendorAdd type notifcation .,, preference saving completed. key : "+(data.get("groupId")+"_vendorpush"));

                        return;
                    }
                    else if("RuleAdd".equalsIgnoreCase(data.get("Notification_type")))
                    {
                        LeafPreference leafPreference = LeafPreference.getInstance(getApplicationContext());
                        leafPreference.setInt(data.get("groupId")+"_cocpush" , leafPreference.getInt(data.get("groupId")+"_cocpush" )+1);
                    }
                    else if("EBookAdd".equalsIgnoreCase(data.get("Notification_type")))
                    {
                            LeafPreference leafPreference = LeafPreference.getInstance(getApplicationContext());
                            leafPreference.setInt(data.get("groupId")+"_ebookpush" , leafPreference.getInt(data.get("groupId")+"_ebookpush" )+1);
                    }

                    sendNotification(data.get("body"), data.get("title"));

                }
//                BaseActivity.updateMyActivity(this);
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
