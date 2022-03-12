package school.campusconnect.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.abedelazizshe.lightcompressorlibrary.CompressionListener;
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor;
import com.abedelazizshe.lightcompressorlibrary.VideoQuality;
import com.abedelazizshe.lightcompressorlibrary.config.Configuration;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.mobileconnectors.s3.transferutility.UploadOptions;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;
import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.AddGalleryPostRequest;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.network.LeafManager;

public class BackgroundVideoUploadChapterService extends Service implements LeafManager.OnAddUpdateListener<AddPostValidationError> {

    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    public static final String TAG = "BackgroundVideoUploadService";

    private Context context;
    private ArrayList<String> listImages = new ArrayList<>();
    private TransferUtility transferUtility;
    ArrayList<String> listAmazonS3Url = new ArrayList<>();
    private AddGalleryPostRequest mainRequest;
    LeafManager manager = new LeafManager();
    private String team_id, group_id, videoUrl;
    private int notifyId = 1;
    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;

    private ArrayList<Integer> compressedVideoCount = new ArrayList<>();
    private ArrayList<Integer> uploadVideoPercentages = new ArrayList<>();

    int compressedCounts = 0;

    ArrayList<Intent> taskIntents = new ArrayList<>();

    Intent currentTask = null;
    private String chapter_id;
    private String subject_id;
    private boolean isEdit;
    private String subject_name;

    //95387 32882
    @Override
    public void onCreate() {
        super.onCreate();

        AppLog.e(TAG, "OnCreate called");
        context = getApplicationContext();
        transferUtility = AmazoneHelper.getTransferUtility(this);

        currentTask = null;
        taskIntents = new ArrayList<>();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AppLog.e(TAG, "onStartCommand called : " + startId + " and currentTassk : " + (currentTask != null));
        if (currentTask != null) {
            taskIntents.add(intent);
            AppLog.e(TAG, "onStartCommand currentTask is not null and taskIntent size is : " + taskIntents.size());
            return START_NOT_STICKY;
        } else {
            currentTask = intent;
        }

        getDataFromCurrentTask();
        uploadVideos();

        return START_NOT_STICKY;
    }


    public void getDataFromCurrentTask() {
        AppLog.e(TAG, "getDataFromCurrentTask called ");
        videoUrl = currentTask.getStringExtra("videoUrl");
        group_id = currentTask.getStringExtra("group_id");
        team_id = currentTask.getStringExtra("team_id");
        chapter_id = currentTask.getStringExtra("chapter_id");
        subject_id = currentTask.getStringExtra("subject_id");
        subject_name = currentTask.getStringExtra("subject_name");
        isEdit = currentTask.getBooleanExtra("isEdit", false);

        listImages = new ArrayList<>();
        listAmazonS3Url = new ArrayList<>();
        mainRequest = null;

        listImages = (ArrayList<String>) currentTask.getSerializableExtra("listImages");
        mainRequest = (AddGalleryPostRequest) currentTask.getSerializableExtra("mainRequest");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            serviceChannel.setSound(null, null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        AppLog.e(TAG, "API onSuccess : " + apiId + " taskIntent size : " + taskIntents.size());

        if (taskIntents.size() > 0) {
            currentTask = taskIntents.remove(0);

            AppLog.e(TAG, "onSuccess nexttask assigned now size is : " + taskIntents.size());
            getDataFromCurrentTask();
            uploadVideos();

            return;
        } else {
            if (isEdit) {
                LeafPreference.getInstance(BackgroundVideoUploadChapterService.this).setBoolean("is_topic_added", true);
                new SendNotification(mainRequest.albumName, false).execute();
            } else {
                LeafPreference.getInstance(BackgroundVideoUploadChapterService.this).setBoolean("is_chapter_added", true);
                new SendNotification(mainRequest.albumName, true).execute();
            }
        }
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<AddPostValidationError> error) {
        if (error.status.equals("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();

        } else {
            if (error.errors == null)
                return;
            if (error.errors.get(0).video != null) {
                Toast.makeText(context, error.errors.get(0).video, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, error.title, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onException(int apiId, String error) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }

    public void uploadVideos() {
        compressedVideoCount = new ArrayList<>();
        uploadVideoPercentages = new ArrayList<>();

        for (String s : listImages)
            compressedVideoCount.add(0);

        for (String s : listImages)
            uploadVideoPercentages.add(0);

        compressedCounts = 0;

        createNotificationChannel();
        Intent notificationIntent = new Intent(context, GroupDashboardActivityNew.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, notificationIntent, 0);
        notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("Video Uploading...")
                    .setContentText("0%")
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentIntent(pendingIntent)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setProgress(100, 0, false)

                    .setAutoCancel(false);
            Notification notification = notificationBuilder.build();

            AppLog.e(TAG, "start foregournd called 1");
            this.startForeground(notifyId, notification);
        } else {

            notificationBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle("Video Upload")
                    .setContentText("0%")
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentIntent(pendingIntent)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setProgress(100, 0, false)
                    .setAutoCancel(false);
            Notification notification = notificationBuilder.build();

            AppLog.e(TAG, "start foregournd  2");
            this.startForeground(notifyId, notification);
        }

        notificationBuilder.setProgress(100, 0, false);
        notificationManager.notify(notifyId, notificationBuilder.build());


        try {

            compressVideo(0);

        } catch (Exception e) {
            Toast.makeText(context, "Error In Compression :" + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }

    public void compressVideo(int index) {
        if (compressedCounts == listImages.size()) {
            uploadToAmazone(mainRequest);
            return;
        }

        int finalI = index;

        File file = new File(listImages.get(finalI));
        // Get length of file in bytes
        long fileSizeInBytes = file.length();
        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        long fileSizeInKB = fileSizeInBytes / 1024;
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        long fileSizeInMB = fileSizeInKB / 1024;


        File videoCompresed = ImageUtil.getOutputMediaVideo(finalI);


        AppLog.e(TAG, "compression Started id : " + finalI + ", output path : " + videoCompresed.getPath());

        String width = "";
        String height = "";
        try {
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(getApplicationContext(), Uri.parse(listImages.get(finalI)));
            height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        } catch (Exception ex) {
            compressedCounts++;
            compressVideo(compressedCounts);
        }

        VideoCompressor.start(
                getApplicationContext(), // => This is required if srcUri is provided. If not, pass null.
                Uri.parse(listImages.get(finalI)), // => Source can be provided as content uri, it requires context.
                null, // => This could be null if srcUri and context are provided.
                videoCompresed.getPath(),
                new CompressionListener() {
                    @Override
                    public void onStart() {
                        // Compression start
                    }

                    @Override
                    public void onSuccess() {
                        // On Compression success

                        AppLog.e(TAG, "Compression onSuccess id :  " + finalI + " & getPath  : " + videoCompresed.getPath());

                        File file = new File(videoCompresed.getPath());
                        // Get length of file in bytes
                        long fileSizeInBytes = file.length();
                        long fileSizeInKB = fileSizeInBytes / 1024;
                        long fileSizeInMB = fileSizeInKB / 1024;

                        AppLog.e(TAG, "onSuccess: with size :  " + fileSizeInMB + " compressCounts : " + compressedCounts);

                        listImages.set(finalI, Uri.fromFile(file).toString());

                        compressedCounts++;
                        compressVideo(compressedCounts);
                    }

                    @Override
                    public void onFailure(String failureMessage) {
                        // On Failure
                        AppLog.e(TAG, "onFailure called : " + failureMessage);
                        compressedCounts++;

                        compressVideo(compressedCounts);
                    }

                    @Override
                    public void onProgress(float v) {
                        // Update UI with progress value
                        compressedVideoCount.set(finalI, (int) v);
                        publishCompressProgress((int) v);
                    }

                    @Override
                    public void onCancelled() {
                        // On Cancelled
                    }
                }, new Configuration(VideoQuality.LOW, false, false, Double.parseDouble(height), Double.parseDouble(width), 2500000)
        );


    }


    public void publishCompressProgress(int percentage) {
        int total = 0;
        for (int i : compressedVideoCount) {
            total += i;
        }

        int newPercentage = (total) / compressedVideoCount.size();

        notificationBuilder.setContentTitle("Preparing Videos ...");
        notificationBuilder.setContentText("" + newPercentage + "%");
        notificationBuilder.setProgress(100, newPercentage, false);

        Notification notification = notificationBuilder.build();
        //BackgroundVideoUploadService.this.startForeground(0, notification);
        notificationManager.notify(notifyId, notification);
    }

    public void publishUploadProgress() {
        int total = 0;
        for (int i : uploadVideoPercentages) {
            total += i;
        }

        int newPercentage = (total) / uploadVideoPercentages.size();

        notificationBuilder.setContentTitle("Upload Videos ...");
        notificationBuilder.setContentText("" + newPercentage + "%");
        notificationBuilder.setProgress(100, newPercentage, false);

        Notification notification = notificationBuilder.build();
        //BackgroundVideoUploadService.this.startForeground(0, notification);
        notificationManager.notify(notifyId, notification);
    }

    private void uploadToAmazone(AddGalleryPostRequest request) {
        // mainRequest = request;
        //request.fileName = listAmazonS3Url;
        AppLog.e(TAG, "send data " + new Gson().toJson(request));

        if (request.fileType.equals(Constants.FILE_TYPE_VIDEO)) {
            AppLog.e(TAG, "Final videos :: " + listImages.toString());
            GetThumbnail.create(listImages, new GetThumbnail.GetThumbnailListener() {
                @Override
                public void onThumbnail(ArrayList<String> listThumbnails) {
                    if (listThumbnails != null) {
                        uploadThumbnail(listThumbnails, 0);
                    } else {
                        Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show();
                    }

                }
            }, Constants.FILE_TYPE_VIDEO);
        } else {
            for (int i = 0; i < listImages.size(); i++) {
                try {
                    File newFile = new Compressor(this).setMaxWidth(1000).setQuality(90).compressToFile(new File(listImages.get(i)));
                    listImages.set(i, newFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            AppLog.e(TAG, "Final PAth :: " + listImages.toString());
            upLoadImageOnCloud(0);
        }
    }

    private void uploadThumbnail(ArrayList<String> listThumbnails, int index) {
        if (index == listThumbnails.size()) {
            mainRequest.thumbnailImage = listThumbnails;
            upLoadImageOnCloud(0);
        } else {
            final String key = AmazoneHelper.getAmazonS3KeyThumbnail(mainRequest.fileType);
//            File file = new File(listThumbnails.get(index));

            TransferObserver observer = null;
            try {
                UploadOptions option = UploadOptions.
                        builder().bucket(AmazoneHelper.BUCKET_NAME).
                        cannedAcl(CannedAccessControlList.PublicRead).build();
                observer = transferUtility.upload(key,
                        getContentResolver().openInputStream(Uri.parse(listThumbnails.get(index))), option);

                observer.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        AppLog.e(TAG, "onStateChanged: " + id + ", " + state.name());
                        if (state.toString().equalsIgnoreCase("COMPLETED")) {
                            AppLog.e(TAG, "onStateChanged " + index);

                            String _finalUrl = AmazoneHelper.BUCKET_NAME_URL + key;

                            AppLog.e(TAG, "url is " + _finalUrl);

                            _finalUrl = Constants.encodeStringToBase64(_finalUrl);

                            AppLog.e(TAG, "encoded url is " + _finalUrl);

                            listThumbnails.set(index, _finalUrl);

                            uploadThumbnail(listThumbnails, index + 1);

                        }
                        if (TransferState.FAILED.equals(state)) {
                            //progressBar.setVisibility(View.GONE);
                            if (Constants.FILE_TYPE_VIDEO.equals(mainRequest.fileType)) {
                                //progressDialog.dismiss();
                            }
                            Toast.makeText(context, "Failed to upload", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                        int percentDone = (int) percentDonef;
                        AppLog.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                                + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        //progressBar.setVisibility(View.GONE);
                        if (Constants.FILE_TYPE_VIDEO.equals(mainRequest.fileType)) {
                            //progressDialog.dismiss();
                        }
                        AppLog.e(TAG, "Upload Error : " + ex);
                        Toast.makeText(context, getResources().getString(R.string.image_upload_error), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception ex) {
                Log.e("Thumbnail", "onStateChanged " + index);

                String _finalUrl = AmazoneHelper.BUCKET_NAME_URL + key;

                AppLog.e(TAG, "url is " + _finalUrl);

                _finalUrl = Constants.encodeStringToBase64(_finalUrl);

                AppLog.e(TAG, "encoded url is " + _finalUrl);

                listThumbnails.set(index, _finalUrl);

                uploadThumbnail(listThumbnails, index + 1);
            }


        }
    }

    private void upLoadImageOnCloud(final int pos) {

        AppLog.e(TAG, "upLoadImageOnCloud: position " + pos);

        if (pos == listImages.size()) {
            if (Constants.FILE_TYPE_VIDEO.equals(mainRequest.fileType)) {
                //progressDialog.dismiss();
                notificationBuilder.setContentTitle("Video Upload Successfully");
                notificationBuilder.setProgress(0, 0, false);
                notificationBuilder.setContentText("Success");
                notificationBuilder.setAutoCancel(true);
                notificationManager.notify(notifyId, notificationBuilder.build());
            }
            mainRequest.fileName = listAmazonS3Url;

            AppLog.e(TAG, "send data : " + new Gson().toJson(mainRequest));
            if (isEdit) {
                manager.addChapterTopicPost(this, group_id, team_id, subject_id, chapter_id, mainRequest);
            } else {
                manager.addChapterPost(this, group_id, team_id, subject_id, mainRequest);
            }
        } else {
            final String key = AmazoneHelper.getAmazonS3Key(mainRequest.fileType);
//            File file = new File(listImages.get(pos));
            UploadOptions option = UploadOptions.
                    builder().bucket(AmazoneHelper.BUCKET_NAME).
                    cannedAcl(CannedAccessControlList.PublicRead).build();
            TransferObserver observer = null;
            try {
                observer = transferUtility.upload(key,
                        getContentResolver().openInputStream(Uri.parse(listImages.get(pos))), option);
                observer.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        AppLog.e(TAG, "onStateChanged: " + id + ", " + state.name());
                        if (state.toString().equalsIgnoreCase("COMPLETED")) {
                            Log.e("MULTI_IMAGE", "onStateChanged " + pos);
                            updateList(pos, key);
                        }
                        if (TransferState.FAILED.equals(state)) {
                            //progressDialog.dismiss();
                            Toast.makeText(context, getResources().getString(R.string.image_upload_error), Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                        int percentDone = (int) percentDonef;
                        if (Constants.FILE_TYPE_VIDEO.equals(mainRequest.fileType)) {
                            // progressDialog.setMessage("Uploading Video... " + percentDone + "% " + (pos + 1) + " out of " + listImages.size() + ", please wait...");
                            uploadVideoPercentages.set(pos, percentDone);
                            publishUploadProgress();
                        }
                        AppLog.d(TAG, "ID:" + id + " bytesCurrent: " + bytesCurrent
                                + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        //progressBar.setVisibility(View.GONE);
                        if (Constants.FILE_TYPE_VIDEO.equals(mainRequest.fileType)) {
                            //progressDialog.dismiss();
                        }
                        AppLog.e(TAG, "Upload Error : " + ex);
                        Toast.makeText(context, getResources().getString(R.string.image_upload_error), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    private void updateList(int pos, String key) {
        String _finalUrl = AmazoneHelper.BUCKET_NAME_URL + key;

        Log.e("FINALURL", "url is " + _finalUrl);

        _finalUrl = Constants.encodeStringToBase64(_finalUrl);

        Log.e("FINALURL", "encoded url is " + _finalUrl);

        listAmazonS3Url.add(_finalUrl);

        upLoadImageOnCloud(pos + 1);
    }

    private class SendNotification extends AsyncTask<String, String, String> {

        private final String chapterName;
        private final boolean isChapter;
        private String server_response;

        public SendNotification(String chapterName, boolean isChapter) {
            this.chapterName = chapterName;
            this.isChapter = isChapter;
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
                    String message = "";

                    if (isChapter) {
                        message = subject_name + " : New chapter added";
                    } else {
                        message = " New topic added to " + chapterName;
                    }

                    topic = group_id + "_" + team_id;
                    object.put("to", "/topics/" + topic);

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title", title);
                    notificationObj.put("body", message);
                    object.put("notification", notificationObj);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("groupId", group_id);
                    dataObj.put("createdById", LeafPreference.getInstance(BackgroundVideoUploadChapterService.this).getString(LeafPreference.LOGIN_ID));
                    dataObj.put("postId", "");
                    dataObj.put("teamId", team_id);
                    dataObj.put("title", title);
                    dataObj.put("postType", isChapter ? "chapter" : "topic");
                    dataObj.put("Notification_type", "VideoClass");
                    dataObj.put("body", message);
                    object.put("data", dataObj);

                    wr.writeBytes(object.toString());
                    Log.e(" JSON input : ", object.toString());
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
            Intent intent = new Intent("chapter_refresh");
            sendBroadcast(intent);
            stopForeground(false);
        }
    }
}
