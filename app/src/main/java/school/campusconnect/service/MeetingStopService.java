package school.campusconnect.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.videocall.StartMeetingRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;

public class MeetingStopService extends Service implements LeafManager.OnCommunicationListener {
    String TAG = "MeetingStopService";
    @Override
    public void onCreate() {
        super.onCreate();
        AppLog.e(TAG,"onCreate()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppLog.e(TAG,"onDestroy()");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        AppLog.e(TAG,"onTaskRemoved()");

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        AppLog.e(TAG,"onSuccess()");
        if (apiId == LeafManager.API_JISTI_MEETING_STOP) {
            StartMeetingRes startMeetingRes = (StartMeetingRes) response;
            if(startMeetingRes.data!=null && startMeetingRes.data.size()>0){
                new SendNotification(false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        AppLog.e(TAG,"onFailure()");
        if (apiId == LeafManager.API_JISTI_MEETING_STOP) {

        }
    }

    @Override
    public void onException(int apiId, String msg) {
        AppLog.e(TAG,"onException()");
        if (apiId == LeafManager.API_JISTI_MEETING_STOP) {
        }
    }
    private class SendNotification extends AsyncTask<String, String, String> {
        private String server_response;
        boolean isStart;
        public SendNotification(boolean isStart) {
            this.isStart = isStart;
        }

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;

            /*try {
                url = new URL("https://fcm.googleapis.com/fcm/send");
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", BuildConfig.API_KEY_FIREBASE1+BuildConfig.API_KEY_FIREBASE2);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                try {
                    JSONObject object = new JSONObject();

                    String topic;
                    String title = getResources().getString(R.string.app_name);
                    String name = LeafPreference.getInstance(MeetingStopService.this).getString(LeafPreference.NAME);
                    String message = isStart?name+" teacher has started live class":name+" teacher has ended live class";
                    topic = JitsiPlayerActivity.groupId + "_" + JitsiPlayerActivity.teamId;
                    object.put("to", "/topics/" + topic);

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title", title);
                    notificationObj.put("body", message);
                    object.put("notification", notificationObj);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("groupId", JitsiPlayerActivity.groupId);
                    dataObj.put("createdById", LeafPreference.getInstance(MeetingStopService.this).getString(LeafPreference.LOGIN_ID));
                    dataObj.put("teamId", JitsiPlayerActivity.teamId);
                    dataObj.put("title", title);
                    dataObj.put("Notification_type", isStart?"videoStart":"videoEnd");
                    dataObj.put("body", message);
                    object.put("data", dataObj);
                    wr.writeBytes(object.toString());
                    Log.e(TAG, " JSON input : " + object.toString());
                    wr.flush();
                    wr.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                AppLog.e(TAG, "responseCode :" + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_response = readStream(urlConnection.getInputStream());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/

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
