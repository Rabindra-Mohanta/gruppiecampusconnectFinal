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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;



import id.zelory.compressor.Compressor;
import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.datamodel.AddPostRequest;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.network.LeafManager;


public class BackgroundVideoUploadService extends Service implements LeafManager.OnAddUpdateListener<AddPostValidationError> {

    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    public static final String TAG = "BackgroundVideoUploadService";

    private Context context;
    private ArrayList<String> listImages = new ArrayList<>();
    private TransferUtility transferUtility;
    ArrayList<String> listAmazonS3Url = new ArrayList<>();
    private AddPostRequest mainRequest;
    LeafManager manager = new LeafManager();
    private String friend_id, postType, team_id, group_id, videoUrl;
    private boolean isFromCamera, isFromChat;
    private int id = 1;
    private int notifyId = 1;
    int progress = 0;
    NotificationCompat.Builder notificationBuilder;
    Notification notification;
    NotificationManager notificationManager;

    private ArrayList<Integer> compressedVideoCount = new ArrayList<>();
    private ArrayList<Integer> uploadVideoPercentages = new ArrayList<>();

    int compressedCounts = 0;

    ArrayList<Intent> taskIntents = new ArrayList<>();

    Intent currentTask  = null;
    //95387 32882
    @Override
    public void onCreate()
    {
        super.onCreate();

        AppLog.e(TAG , "OnCreate called");
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
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        AppLog.e(TAG , "onStartCommand called : "+startId +" and currentTassk : "+(currentTask !=null));
        if(currentTask != null)
        {
            taskIntents.add(intent);
            AppLog.e(TAG, "onStartCommand currentTask is not null and taskIntent size is : "+taskIntents.size());
            return START_NOT_STICKY;
        }
        else
        {
            currentTask = intent;
        }

        /*isFromCamera = intent.getBooleanExtra("isFromCamera", false);
        isFromChat = intent.getBooleanExtra("isFromChat", false);
        videoUrl = intent.getStringExtra("videoUrl");
        group_id = intent.getStringExtra("group_id");
        team_id = intent.getStringExtra("team_id");
        postType = intent.getStringExtra("postType");
        friend_id = intent.getStringExtra("friend_id");
        listImages = (ArrayList<String>) intent.getSerializableExtra("listImages");
        mainRequest = (AddPostRequest) intent.getSerializableExtra("mainRequest");*/

        /*if (isFromCamera) {
            new VideoCompressor1(mainRequest, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new VideoCompressor1(mainRequest, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
*/
       /* createNotificationChannel();
        Intent notificationIntent = new Intent(this, AddPostActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText("input")
                .setSmallIcon(R.drawable.app_icon)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);*/
        //do heavy work on a background thread
        //stopSelf();

        getDataFromCurrentTask();
        uploadVideos();

        return START_NOT_STICKY;
    }


    public void getDataFromCurrentTask()
    {
        AppLog.e(TAG , "getDataFromCurrentTask called ");
        isFromCamera = currentTask.getBooleanExtra("isFromCamera", false);
        isFromChat = currentTask.getBooleanExtra("isFromChat", false);
        videoUrl = currentTask.getStringExtra("videoUrl");
        group_id = currentTask.getStringExtra("group_id");
        team_id = currentTask.getStringExtra("team_id");
        postType = currentTask.getStringExtra("postType");
        friend_id = currentTask.getStringExtra("friend_id");

        listImages = new ArrayList<>();
        listAmazonS3Url = new ArrayList<>();
        mainRequest = null;

        listImages = (ArrayList<String>) currentTask.getSerializableExtra("listImages");
        mainRequest = (AddPostRequest) currentTask.getSerializableExtra("mainRequest");
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
            serviceChannel.setSound(null , null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response)
    {
        AppLog.e(TAG , "API onSuccess : "+apiId +" taskIntent size : "+taskIntents.size());

        if(taskIntents.size() > 0)
        {
            currentTask = taskIntents.remove(0);

            AppLog.e(TAG , "onSuccess nexttask assigned now size is : "+taskIntents.size());
            getDataFromCurrentTask();
            uploadVideos();

            return;
        }

        Intent intent = new Intent("postadded");
        sendBroadcast(intent);

        stopForeground(false);
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

    public void uploadVideos()
    {
        compressedVideoCount = new ArrayList<>();
        uploadVideoPercentages = new ArrayList<>();

        for(String s : listImages)
        compressedVideoCount.add(0);

        for(String s : listImages)
            uploadVideoPercentages.add(0);

        compressedCounts = 0;

        createNotificationChannel();
        Intent notificationIntent = new Intent(context, GroupDashboardActivityNew.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, notificationIntent, 0);
        notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("Video Uploading...")
                    .setContentText("0%")
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentIntent(pendingIntent)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setProgress(100, 0, false)

                    .setAutoCancel(false);
            Notification notification = notificationBuilder.build();

            AppLog.e(TAG , "start foregournd called 1");
            this.startForeground(notifyId, notification);
        }
        else
        {

            notificationBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle("Video Upload")
                    .setContentText("0%")
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentIntent(pendingIntent)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setProgress(100, 0, false)
                    .setAutoCancel(false);
            Notification notification = notificationBuilder.build();

            AppLog.e(TAG , "start foregournd  2");
            this.startForeground(notifyId, notification);
        }

        notificationBuilder.setProgress(100, 0, false);
        notificationManager.notify(notifyId, notificationBuilder.build());


        try {

                compressVideo(0);

        } catch (Exception e)
        {
            Toast.makeText(context, getResources().getString(R.string.toast_error_comression) + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }

    public void compressVideo(int index)
    {
        if(compressedCounts == listImages.size())
        {
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


        File videoCompresed  = ImageUtil.getOutputMediaVideo(finalI);


        AppLog.e(TAG, "compression Started id : "+finalI+", output path : "+videoCompresed.getPath());

        String width = "";
        String height = "";
        try {
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(getApplicationContext(), Uri.parse(listImages.get(finalI)));
            height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        }
        catch(Exception ex)
        {
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

                        AppLog.e(TAG, "Compression onSuccess id :  "+finalI + " & getPath  : "+videoCompresed.getPath());

                        File file = new File(videoCompresed.getPath());
                        // Get length of file in bytes
                        long fileSizeInBytes = file.length();
                        long fileSizeInKB = fileSizeInBytes / 1024;
                        long fileSizeInMB = fileSizeInKB / 1024;

                        AppLog.e(TAG, "onSuccess: with size :  "+fileSizeInMB + " compressCounts : "+compressedCounts);

                        listImages.set(finalI, Uri.fromFile(file).toString());

                        compressedCounts++;
                        compressVideo(compressedCounts);
                    }

                    @Override
                    public void onFailure(String failureMessage) {
                        // On Failure
                        AppLog.e(TAG , "onFailure called : "+failureMessage);
                        compressedCounts++;

                        compressVideo(compressedCounts);
                    }

                    @Override
                    public void onProgress(float v) {
                        // Update UI with progress value
                        compressedVideoCount.set(finalI , (int) v) ;
                        publishCompressProgress((int) v);
                    }

                    @Override
                    public void onCancelled() {
                        // On Cancelled
                    }
                },new Configuration(VideoQuality.LOW , false , false , Double.parseDouble(height) ,Double.parseDouble(width) , 2500000)
        );

    /*    VideoCompressor.start(listImages.get(finalI), videoCompresed.getPath(), new CompressionListener()
        {
            @Override
            public void onStart()
            {
                // Compression start
            }

            @Override
            public void onSuccess()
            {
                // On Compression success
                AppLog.e(TAG, "Compression onSuccess id :  "+finalI + " & getPath  : "+videoCompresed.getPath());

                File file = new File(videoCompresed.getPath());
                // Get length of file in bytes
                long fileSizeInBytes = file.length();
                long fileSizeInKB = fileSizeInBytes / 1024;
                long fileSizeInMB = fileSizeInKB / 1024;

                AppLog.e(TAG, "onSuccess: with size :  "+fileSizeInMB + " compressCounts : "+compressedCounts);
                listImages.set(finalI, file.getPath());

                compressedCounts++;

                compressVideo(compressedCounts);

            }

            @Override
            public void onFailure(String failureMessage) {
                // On Failure

                AppLog.e(TAG , "Compression "+finalI+" onFailure : "+failureMessage);

                compressedCounts++;

                compressVideo(compressedCounts);
            }

            @Override
            public void onProgress(float progressPercent)
            {

                compressedVideoCount.set(finalI , (int) progressPercent) ;
                publishCompressProgress((int) progressPercent);
            }

            @Override
            public void onCancelled() {
                // On Cancelled
            }
        }, VideoQuality.LOW, true, true);*/


    }


    public void publishCompressProgress(int percentage)
    {
        int total = 0;
        for(int i : compressedVideoCount)
        {
            total += i;
        }

        int newPercentage = (total ) / compressedVideoCount.size();

        notificationBuilder.setContentTitle("Preparing Videos ...");
        notificationBuilder.setContentText("" + newPercentage + "%");
        notificationBuilder.setProgress(100, newPercentage, false);

        Notification notification = notificationBuilder.build();
        //BackgroundVideoUploadService.this.startForeground(0, notification);
        notificationManager.notify(notifyId, notification);
    }

    public void publishUploadProgress()
    {
        int total = 0;
        for(int i : uploadVideoPercentages)
        {
            total += i;
        }

        int newPercentage = (total ) / uploadVideoPercentages.size();

        notificationBuilder.setContentTitle("Upload Videos ...");
        notificationBuilder.setContentText("" + newPercentage + "%");
        notificationBuilder.setProgress(100, newPercentage, false);

        Notification notification = notificationBuilder.build();
        //BackgroundVideoUploadService.this.startForeground(0, notification);
        notificationManager.notify(notifyId, notification);
    }

    private void uploadToAmazone(AddPostRequest request) {
       // mainRequest = request;
        //request.fileName = listAmazonS3Url;
        AppLog.e(TAG, "send data " + new Gson().toJson(request));

        if (request.fileType.equals(Constants.FILE_TYPE_VIDEO)) {
            AppLog.e(TAG, "Final videos :: " + listImages.toString());
            GetThumbnail.create(listImages, new GetThumbnail.GetThumbnailListener()
            {
                @Override
                public void onThumbnail(ArrayList<String> listThumbnails) {
                    if (listThumbnails != null) {
                        uploadThumbnail(listThumbnails, 0);
                    } else {
                        Toast.makeText(context, getResources().getString(R.string.toast_upload_failed), Toast.LENGTH_SHORT).show();
                    }

                }
            }, Constants.FILE_TYPE_VIDEO);
        } else
            {
            for (int i = 0; i < listImages.size(); i++)
            {
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

    private void uploadThumbnail(ArrayList<String> listThumbnails, int index)
    {
        if (index == listThumbnails.size())
        {
            mainRequest.thumbnailImage = listThumbnails;
            upLoadImageOnCloud(0);
        } else
            {
            final String key = AmazoneHelper.getAmazonS3KeyThumbnail(mainRequest.fileType);
//            File file = new File(listThumbnails.get(index));

            TransferObserver observer = null;
            if (!TextUtils.isEmpty(listThumbnails.get(index))) {
//                AppLog.e(TAG, "file " + file.getName() + " , " + file.getAbsolutePath());
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
                                Toast.makeText(context, getResources().getString(R.string.toast_upload_failed), Toast.LENGTH_SHORT).show();
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


            } else {
                AppLog.e(TAG, "onStateChanged " + index);

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

        AppLog.e(TAG, "upLoadImageOnCloud: position "+pos);

        if (pos == listImages.size())
        {
            if (Constants.FILE_TYPE_VIDEO.equals(mainRequest.fileType))
            {
                //progressDialog.dismiss();
                notificationBuilder.setContentTitle("Video Upload Successfully");
                notificationBuilder.setProgress(0, 0, false);
                notificationBuilder.setContentText("Success");
                notificationBuilder.setAutoCancel(true);
                notificationManager.notify(notifyId, notificationBuilder.build());
            }
            mainRequest.fileName = listAmazonS3Url;

            AppLog.e(TAG , "addPost mainRequest : "+new Gson().toJson(mainRequest));
            manager.addPost(this, group_id, team_id, mainRequest, postType, friend_id, isFromChat);
        } else {
            final String key = AmazoneHelper.getAmazonS3Key(mainRequest.fileType);

            TransferObserver observer = null;
            try {
                UploadOptions option = UploadOptions.
                        builder().bucket(AmazoneHelper.BUCKET_NAME).
                        cannedAcl(CannedAccessControlList.PublicRead).build();
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
            }catch (IOException e) {
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


}
