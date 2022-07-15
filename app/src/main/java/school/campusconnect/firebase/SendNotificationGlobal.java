package school.campusconnect.firebase;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

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
import school.campusconnect.LeafApplication;
import school.campusconnect.R;
import school.campusconnect.utils.AppLog;

public class SendNotificationGlobal extends AsyncTask<String, String, String> {
    private final String TAG = "SendNotificationGlobal";
    private final SendNotificationModel notificationModel;
    private String server_response;

    public SendNotificationGlobal(SendNotificationModel notificationModel) {
        this.notificationModel = notificationModel;
    }

    public static void send(SendNotificationModel notificationModel) {
        new SendNotificationGlobal(notificationModel).executeOnExecutor(THREAD_POOL_EXECUTOR);
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
                String object = new Gson().toJson(notificationModel);
                wr.writeBytes(object);
                Log.e(TAG, " JSON input : " + object);
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