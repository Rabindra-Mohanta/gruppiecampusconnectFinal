/*
 * Copyright (c) 2013 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package school.campusconnect.utils.youtube;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Base64;
import school.campusconnect.utils.AppLog;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.youtubetoken.YoutubeTokenResponse;
import school.campusconnect.network.LeafManager;

/**
 * @author Ibrahim Ulukaya <ulukaya@google.com>
 *         <p/>
 *         Intent service to handle uploads.
 */
public class UploadService extends IntentService implements LeafManager.OnCommunicationListener {

    /**
     * defines how long we'll wait for a video to finish processing
     */
    private static final int PROCESSING_TIMEOUT_SEC = 60 * 20; // 20 minutes

    /**
     * controls how often to poll for video processing status
     */
    private static final int PROCESSING_POLL_INTERVAL_SEC = 60;
    /**
     * how long to wait before re-trying the upload
     */
    private static final int UPLOAD_REATTEMPT_DELAY_SEC = 60;
    /**
     * max phone of retry attempts
     */
    private static final int MAX_RETRY = 3;
    private static final String TAG = "UploadService";
    /**
     * processing start time
     */
    private static long mStartTime;
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = new GsonFactory();
    GoogleCredential credential;
    Uri fileUri;

    private int uploadProgress;
    /**
     * tracks the phone of upload attempts
     */
    private int mUploadAttemptCount;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    public void setProgress(int progress)
    {
        this.uploadProgress = progress;
    }

    public int getUploadProgress()
    {
        return uploadProgress;
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        UploadService getService() {
            // Return this instance of LocalService so clients can call public methods
            return UploadService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public UploadService() {
        super("YTUploadService");
    }

    private static void zzz(int duration) throws InterruptedException {
       AppLog.d(TAG, String.format("Sleeping for [%d] ms ...", duration));
        Thread.sleep(duration);
       AppLog.d(TAG, String.format("Sleeping for [%d] ms ... done", duration));
    }

    private static boolean timeoutExpired(long startTime, int timeoutSeconds) {
        long currTime = System.currentTimeMillis();
        long elapsed = currTime - startTime;
        if (elapsed >= timeoutSeconds * 1000) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        fileUri = intent.getData();
        credential = new GoogleCredential().setAccessToken(MainActivity.token);

        String appName = getResources().getString(R.string.app_name);
        final YouTube youtube =
                new YouTube.Builder(transport, jsonFactory, credential).setApplicationName(
                        appName).build();

        uploadProgress = 0;

        try {
            tryUploadAndShowSelectableNotification(fileUri, youtube);
        } catch (InterruptedException e) {
            // ignore
           AppLog.e("YYOUTUBEE", "error2 is " + e.toString());
        }

    }

    private void tryUploadAndShowSelectableNotification(final Uri fileUri, final YouTube youtube) throws InterruptedException {

        while (true)
        {
           AppLog.e(TAG , "TryUploadAndShowNotification Called");
            String videoId = tryUpload(fileUri, youtube);

           AppLog.e(TAG , "TryUpload Completed And VideoID : "+videoId);

            if (videoId != null) {

               AppLog.i(TAG, String.format("Uploaded video with ID: %s", videoId) + "\nurl is " + fileUri.toString() + "\nyt url is " + youtube.getBaseUrl());
                tryShowSelectableNotification(videoId, youtube);
//                if (ReviewActivity.dialog != null && ReviewActivity.dialog.isShowing()) {
                   AppLog.e(TAG, "Video ID Not Null Notification Shown");
//                    ReviewActivity.dialog.dismiss();
                    uploadProgress = -1;
                    GroupDashboardActivityNew.ifVideoSelected = true;
                    GroupDashboardActivityNew.selectedUrl = "https://www.youtube.com/watch?v=" + videoId;
                    GroupDashboardActivityNew.selectedYoutubeId = videoId;
                    //ReviewActivity.reviewActivity.finish();
                    MainActivity.mainActivity.finish();

                /*} else {
                   AppLog.e("YYOUTUBEE", "tryUploadAndShowSelectableNotification7");
                }*/
                return;
            }
            else
            {
                   AppLog.e(TAG, "Video ID IS Null Notification NOT Shown");
//                if (ReviewActivity.dialog != null && ReviewActivity.dialog.isShowing()) {
//                    ReviewActivity.dialog.dismiss();
                    //ReviewActivity.uploaded = -1;
                uploadProgress = -1;
                    //ReviewActivity.reviewActivity.finish();
                /*} else {
                   AppLog.e("YYOUTUBEE", "tryUploadAndShowSelectableNotification10");
                }*/
               AppLog.e(TAG, String.format("Failed to upload %s", fileUri.toString()));

                if (mUploadAttemptCount++ < MAX_RETRY) {
                   AppLog.e("YYOUTUBEE", "tryUploadAndShowSelectableNotification12");
                   AppLog.i(TAG, String.format("Will retry to upload the video ([%d] out of [%d] reattempts)",
                            mUploadAttemptCount, MAX_RETRY));
                   AppLog.e("YYOUTUBEE", "tryUploadAndShowSelectableNotification13");
                    zzz(UPLOAD_REATTEMPT_DELAY_SEC * 1000);
                   AppLog.e("YYOUTUBEE", "tryUploadAndShowSelectableNotification14");
                } else {
                   AppLog.e("YYOUTUBEE", "tryUploadAndShowSelectableNotification15");
                   AppLog.e(TAG, String.format("Giving up on trying to upload %s after %d attempts",
                            fileUri.toString(), mUploadAttemptCount));
                    return;
                }
               AppLog.e("YYOUTUBEE", "tryUploadAndShowSelectableNotification16");
            }
        }
    }

    private void tryShowSelectableNotification(final String videoId, final YouTube youtube)
            throws InterruptedException {
        mStartTime = System.currentTimeMillis();
        boolean processed = false;
        while (!processed) {
            processed = ResumableUpload.checkIfProcessed(videoId, youtube);
            if (!processed) {
                // wait a while
               AppLog.d(TAG, String.format("Video [%s] is not processed yet, will retry after [%d] seconds",
                        videoId, PROCESSING_POLL_INTERVAL_SEC));
                if (!timeoutExpired(mStartTime, PROCESSING_TIMEOUT_SEC)) {
                    zzz(PROCESSING_POLL_INTERVAL_SEC * 1000);
                } else {
                   AppLog.d(TAG, String.format("Bailing out polling for processing status after [%d] seconds",
                            PROCESSING_TIMEOUT_SEC));
                    return;
                }
            } else {
//                ResumableUpload.showSelectableNotification(videoId, getApplicationContext());
                return;
            }
        }
    }

    private String tryUpload(Uri mFileUri, YouTube youtube)
    {
       AppLog.e(TAG , "TryUpload Called ");
        long fileSize;
        InputStream fileInputStream = null;
        String videoId = null;
        try {
            fileSize = getContentResolver().openFileDescriptor(mFileUri, "r").getStatSize();
            fileInputStream = getContentResolver().openInputStream(mFileUri);
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(mFileUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            videoId = ResumableUpload.upload(youtube, fileInputStream, fileSize, mFileUri, cursor.getString(column_index), getApplicationContext() ,UploadService.this);

        }
        catch (FileNotFoundException e)
        {
           AppLog.e(getApplicationContext().toString(), e.getMessage());
        }
        finally
        {
            try
            {
                fileInputStream.close();
            }
            catch (IOException e)
            {
                // ignore
            }
        }
        return videoId;
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        YoutubeTokenResponse res = (YoutubeTokenResponse) response;

        byte[] data = Base64.decode(res.base64Token, Base64.DEFAULT);
        String text = "";
        try {
            text = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
           AppLog.e(TAG, "error is " + e.toString());
        }

       AppLog.e("Decoded", "decoded string is " + text);

//        credential = new GoogleCredential().setAccessToken("ya29.GltGBAA3cC-UpU8EjEYiOvR2vwMtxll2UKepahcg9q3vroGyHvh2XMDPGJk4CK2WmQrSyKUW1wAXz0wCUOKSHOSrjQ1_AOpdVO2Ixo8OROgkinIA5sH409bTknyM");
        credential = new GoogleCredential().setAccessToken(text);


        String appName = getResources().getString(R.string.app_name);
        final YouTube youtube =
                new YouTube.Builder(transport, jsonFactory, credential).setApplicationName(
                        appName).build();


        try {
            tryUploadAndShowSelectableNotification(fileUri, youtube);
        } catch (InterruptedException e) {
            // ignore
           AppLog.e("YYOUTUBEE", "error1 is " + e.toString());
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {

    }

    @Override
    public void onException(int apiId, String msg) {

    }
}
