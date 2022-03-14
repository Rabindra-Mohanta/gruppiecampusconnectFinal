package school.campusconnect.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.mobileconnectors.s3.transferutility.UploadOptions;
import com.amazonaws.services.s3.model.CannedAccessControlList;/*
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;*/
import com.google.gson.Gson;
import com.iceteck.silicompressorr.SiliCompressor;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;
import school.campusconnect.BuildConfig;
import school.campusconnect.LeafApplication;
import school.campusconnect.R;
import school.campusconnect.adapters.UploadImageAdapter;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.AddPostRequest;
import school.campusconnect.datamodel.AddPostResponse;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneHelper;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BackgroundVideoUploadService;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.GetThumbnail;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.youtube.MainActivity;
import school.campusconnect.views.SMBDialogUtils;


public class AddPostActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<AddPostValidationError>, View.OnClickListener, UploadImageAdapter.UploadImageListener {

    private static final String TAG = "AddPostActivity";
    @Bind(R.id.toolbar)
    Toolbar mToolBar;

    @Bind(R.id.et_title)
    EditText edtTitle;

    @Bind(R.id.et_description)
    EditText edtDesc;

    @Bind(R.id.llImage)
    LinearLayout llImage;

    @Bind(R.id.llVideo)
    LinearLayout llVideo;

    @Bind(R.id.llYoutubeLink)
    LinearLayout llYoutubeLink;

    @Bind(R.id.llDoc)
    LinearLayout llDoc;

    @Bind(R.id.img_image)
    ImageView img_image;

    @Bind(R.id.img_youtube)
    ImageView img_youtube;

    @Bind(R.id.img_video)
    ImageView img_video;


    @Bind(R.id.imgDoc)
    ImageView imgDoc;

    @Bind(R.id.tvLabelTitle)
    TextView tvLabelTitle;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.pbImgLoading)
    ProgressBar pbImgLoading;

    @Bind(R.id.btnShare)
    Button btnShare;

    @Bind(R.id.llPersonalSetting)
    LinearLayout llPersonalSetting;

    @Bind(R.id.switch_reply)
    Switch switch_reply;

    @Bind(R.id.switch_comment)
    Switch switch_comment;

    @Bind(R.id.rvImages)
    RecyclerView rvImages;


    TextView btn_ok;
    TextView btn_cancel;
    TextView btn_upload;

    EditText edt_link;

    Dialog dialog;

    String group_id;

    public Uri imageCaptureFile;


    public static final int REQUEST_LOAD_CAMERA_IMAGE = 101;
    public static final int REQUEST_LOAD_GALLERY_IMAGE = 102;
    public static final int REQUEST_LOAD_PDF = 103;
    public static final int REQUEST_LOAD_VIDEO = 104;
    public static final int REQUEST_RECORD_VIDEO = 105;
    String friend_id;
    String team_id;

    String postType = "team";
    String videoUrl = "";
    String fileTypeImageOrVideo;

    LeafManager manager = new LeafManager();

    ArrayList<String> listAmazonS3Url = new ArrayList<>();
    ArrayList<String> listImages = new ArrayList<>();
    private String pdfPath = "";
    private TransferUtility transferUtility;
    private AddPostRequest mainRequest;
    private boolean isFromChat;
    private String userName;
    private String team_name;
    private UploadImageAdapter imageAdapter;
    private String friendName;

    private ProgressDialog progressDialog;
    private boolean isFromCamera;

    private Boolean isGalleryMultiple = false;
    private Boolean isClear = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        ButterKnife.bind(this);

        init();

        setListener();

        ArrayList<String> shareList = LeafApplication.getInstance().getShareFileList();
        if (shareList != null && shareList.size() > 0) {
            SMBDialogUtils.showSMBDialogYesNoCancel(this, "Attach Selected file?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    String fileType = LeafApplication.getInstance().getType();
                    if (Constants.FILE_TYPE_IMAGE.equalsIgnoreCase(fileType)
                            || Constants.FILE_TYPE_VIDEO.equalsIgnoreCase(fileType)) {
                        listImages.addAll(shareList);
                        fileTypeImageOrVideo = fileType;
                        showLastImage();
                    } else if (Constants.FILE_TYPE_PDF.equalsIgnoreCase(fileType)) {
                        pdfPath = shareList.get(0);
                        Picasso.with(AddPostActivity.this).load(R.drawable.pdf_thumbnail).into(imgDoc);
                    }
                }
            });
        }
    }

    private void setListener() {
        llImage.setOnClickListener(this);
        llVideo.setOnClickListener(this);
        llYoutubeLink.setOnClickListener(this);
        llDoc.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        switch_reply.setOnClickListener(this);
        switch_comment.setOnClickListener(this);
        btnShare.setEnabled(false);

        edtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                shareButtonEnableDisable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                shareButtonEnableDisable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        userName = LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.NAME);

//        edtDesc.setMovementMethod(new ScrollingMovementMethod());
//        edtTitle.setMovementMethod(new ScrollingMovementMethod());

        setSupportActionBar(mToolBar);
        setBackEnabled(true);

        if (getIntent() != null) {
            group_id = getIntent().getExtras().getString("id");
            if (getIntent().hasExtra("type"))
                postType = getIntent().getExtras().getString("type");
            if (getIntent().hasExtra("friend_id"))
                friend_id = getIntent().getExtras().getString("friend_id");
            if (getIntent().hasExtra("team_id")) {
                team_id = getIntent().getExtras().getString("team_id");
                team_name = getIntent().getExtras().getString("team_name");
            }


            isFromChat = getIntent().getBooleanExtra("from_chat", false);
            AppLog.e(TAG, "isFromChat : " + isFromChat);

            friendName = getIntent().getStringExtra("friend_name");
        }

        if (postType.equals("team"))
            setTitle("Add Post To Team");
        else if (postType.equals("group"))
            setTitle("Post a new notice");
        else {
            setTitle("Post to " + friendName);
            edtTitle.setVisibility(View.GONE);
            tvLabelTitle.setVisibility(View.GONE);

            if (!isFromChat) {
                llPersonalSetting.setVisibility(View.VISIBLE);
            } else {
                llPersonalSetting.setVisibility(View.GONE);
            }
        }

        transferUtility = AmazoneHelper.getTransferUtility(this);

        rvImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new UploadImageAdapter(listImages, this);
        rvImages.setAdapter(imageAdapter);

/*
        edtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                (edtTitle);
            }
        });
        edtDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyboard(edtDesc);
            }
        });*/

    }

    private void shareButtonEnableDisable() {
        if (isValid(false)) {
            btnShare.setEnabled(true);
        } else {
            btnShare.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void addPost() {
        hide_keyboard();
        if (isConnectionAvailable()) {
            if (isValid(true)) {
                if (progressBar != null)
                    progressBar.setVisibility(View.VISIBLE);
                btnShare.setEnabled(false);

                AddPostRequest request = new AddPostRequest();
                request.text = edtDesc.getText().toString();
                request.title = edtTitle.getText().toString();
                if (!TextUtils.isEmpty(videoUrl)) {
                    request.video = videoUrl;
                    request.fileType = Constants.FILE_TYPE_YOUTUBE;

                    Log.e(TAG, "send data " + new Gson().toJson(request));
                    mainRequest = request;
                    manager.addPost(this, group_id, team_id, request, postType, friend_id, isFromChat);
                } else if (listImages.size() > 0 && Constants.FILE_TYPE_VIDEO.equals(fileTypeImageOrVideo)) {
                    request.fileType = fileTypeImageOrVideo;
                    mainRequest = request;
                    Log.e(TAG, "send data " + new Gson().toJson(request));
//                    progressDialog.setMessage("Preparing Video...");
//                    progressDialog.show();
//                    compressVideo(request, 0);

                    startService();
                    /* if(isFromCamera){
                     *//* AppDialog.showConfirmDialog(this, "Do you want to compress?", new AppDialog.AppDialogListener() {
                            @Override
                            public void okPositiveClick(DialogInterface dialog) {
                                dialog.dismiss();
                                new VideoCompressor(request,true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }

                            @Override
                            public void okCancelClick(DialogInterface dialog) {
                                dialog.dismiss();
                                new VideoCompressor(request,false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        });*//*
                        new VideoCompressor(request,false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }else {
                        new VideoCompressor(request,false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }*/

                } else if (!TextUtils.isEmpty(pdfPath)) {
                    request.fileType = Constants.FILE_TYPE_PDF;
                    progressDialog.setMessage("Preparing Pdf...");
                    progressDialog.show();
                    uploadToAmazone(request);
                } else if (listImages.size() > 0) {
                    request.fileType = Constants.FILE_TYPE_IMAGE;
                    progressDialog.setMessage("Uploading Image...");
                    progressDialog.show();
                    uploadToAmazone(request);
                } else {
                    Log.e(TAG, "send data " + new Gson().toJson(request));
                    mainRequest = request;
                    manager.addPost(this, group_id, team_id, request, postType, friend_id, isFromChat);

                }
            }
        } else {
            showNoNetworkMsg();
        }

    }


    public void startService() {

        Intent serviceIntent = new Intent(this, BackgroundVideoUploadService.class);
        serviceIntent.putExtra("isFromCamera", isFromCamera);
        serviceIntent.putExtra("videoUrl", videoUrl);
        serviceIntent.putExtra("mainRequest", mainRequest);
        serviceIntent.putExtra("listImages", listImages);
        serviceIntent.putExtra("group_id", group_id);
        serviceIntent.putExtra("team_id", team_id);
        serviceIntent.putExtra("postType", postType);
        serviceIntent.putExtra("friend_id", friend_id);
        serviceIntent.putExtra("isFromChat", isFromChat);
        ContextCompat.startForegroundService(this, serviceIntent);

        Toast.makeText(this, "Video Uploading in background", Toast.LENGTH_SHORT).show();
        finish();

    }
/*
    private void compressVideo(AddPostRequest request, final int pos) {
        AppLog.e(TAG, "compressVideo() : " + pos);
        if (pos == listImages.size()) {
           // progressDialog.dismiss();
            //Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            progressDialog.setMessage("Uploading Video...");
            uploadToAmazone(request);
        } else {
            try {
                File file = new File(listImages.get(pos));
                // Get length of file in bytes
                long fileSizeInBytes = file.length();
                long fileSizeInMB = (fileSizeInBytes / 1024) / 1024;
                AppLog.e(TAG, "fileSizeInMB : " + fileSizeInMB);
                if (fileSizeInMB <= 10) {
                    compressVideo(request, pos + 1);
                } else {
                    File destFile = new File(getExternalCacheDir(), file.getName());
                    AppLog.e(TAG, "source : " + file.getAbsolutePath());
                    AppLog.e(TAG, "dest : " + destFile.getAbsolutePath());

                    *//*MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
                    metaRetriever.setDataSource(file.getAbsolutePath());
                    String height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                    String width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                    AppLog.e(TAG, "width : " + width +", height :"+height);
                    int widthInt  = Integer.parseInt(width);
                    int heightInt  = Integer.parseInt(height);*//*
                    new Mp4Composer(file.getAbsolutePath(), destFile.getAbsolutePath())
                            .size( 540, 960)
                            .fillMode(FillMode.PRESERVE_ASPECT_FIT)
                            .listener(new Mp4Composer.Listener() {
                                @Override
                                public void onProgress(double progress) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.setMessage("Preparing Video... " + ((int) (progress * 100)) + "% " + (pos + 1) + " out of " + listImages.size());
                                        }
                                    });
                                }

                                @Override
                                public void onCompleted() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            long fileSizeInBytes = destFile.length();
                                            long fileSizeInMB = (fileSizeInBytes / 1024) / 1024;
                                            AppLog.e(TAG, "compress video size of " + pos + " : " + fileSizeInMB);
                                            listImages.set(pos, destFile.getAbsolutePath());
                                            compressVideo(request, pos + 1);
                                        }
                                    });
                                }

                                @Override
                                public void onCanceled() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                        }
                                    });
                                }

                                @Override
                                public void onFailed(Exception exception) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            exception.printStackTrace();
                                            progressDialog.dismiss();
                                            Toast.makeText(AddPostActivity.this, "Error In Compression :" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(AddPostActivity.this, "Error In Compression :" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }*/

    public class VideoCompressor extends AsyncTask<Void, Integer, Boolean> {
        private AddPostRequest addPostRequest;
        private boolean isCompress;

        public VideoCompressor(AddPostRequest addPostRequest, boolean isCompress) {
            this.addPostRequest = addPostRequest;
            this.isCompress = isCompress;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Preparing Video...");
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage("Preparing Video... " + values[0] + " out of " + listImages.size() + ", please wait...");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {
                if (isFromCamera) {
                    if (isCompress) {
                        for (int i = 0; i < listImages.size(); i++) {
                            int finalI = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    onProgressUpdate((finalI + 1));
                                }
                            });
                            listImages.set(i, SiliCompressor.with(AddPostActivity.this).compressVideo(listImages.get(i), getExternalCacheDir().getAbsolutePath()));
                            Log.e(TAG, "compressPath : " + videoUrl);
                        }
                    } else {
                        return true;
                    }
                } else {
                    for (int i = 0; i < listImages.size(); i++) {
                        int finalI = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onProgressUpdate((finalI + 1));
                            }
                        });
                        File file = new File(listImages.get(i));
                        // Get length of file in bytes
                        long fileSizeInBytes = file.length();
                        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                        long fileSizeInKB = fileSizeInBytes / 1024;
                        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                        long fileSizeInMB = fileSizeInKB / 1024;
                        AppLog.e(TAG, "fileSizeInMB : " + fileSizeInMB);
                        if (fileSizeInMB > 10) {
                            listImages.set(i, SiliCompressor.with(AddPostActivity.this).compressVideo(listImages.get(i), getExternalCacheDir().getAbsolutePath()));
                            Log.e(TAG, "compressPath : " + videoUrl);
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(AddPostActivity.this, "Error In Compression :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
            if (aVoid) {
                progressDialog.setMessage("Uploading Video...");
                uploadToAmazone(addPostRequest);
            } else {
                progressDialog.dismiss();
            }
        }
    }

    private void uploadToAmazone(AddPostRequest request) {
        mainRequest = request;
        //request.fileName = listAmazonS3Url;
        Log.e(TAG, "send data " + new Gson().toJson(request));

        if (request.fileType.equals(Constants.FILE_TYPE_PDF)) {
            AppLog.e(TAG, "Final PAth :: " + listImages.toString());
            listImages.clear();
            listImages.add(pdfPath);
            GetThumbnail.create(listImages, new GetThumbnail.GetThumbnailListener() {
                @Override
                public void onThumbnail(ArrayList<String> listThumbnails) {
                    if (listThumbnails != null) {
                        uploadThumbnail(listThumbnails, 0);
                    } else {
                        Toast.makeText(AddPostActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    }

                }
            }, Constants.FILE_TYPE_PDF);
        } else if (request.fileType.equals(Constants.FILE_TYPE_VIDEO)) {
            AppLog.e(TAG, "Final videos :: " + listImages.toString());
            GetThumbnail.create(listImages, new GetThumbnail.GetThumbnailListener() {
                @Override
                public void onThumbnail(ArrayList<String> listThumbnails) {
                    if (listThumbnails != null) {
                        uploadThumbnail(listThumbnails, 0);
                    } else {
                        Toast.makeText(AddPostActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
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
                            Log.e("Thumbnail", "onStateChanged " + index);

                            String _finalUrl = AmazoneHelper.BUCKET_NAME_URL + key;

                            Log.e("FINALURL", "url is " + _finalUrl);

                            _finalUrl = Constants.encodeStringToBase64(_finalUrl);

                            Log.e("FINALURL", "encoded url is " + _finalUrl);

                            listThumbnails.set(index, _finalUrl);

                            uploadThumbnail(listThumbnails, index + 1);

                        }
                        if (TransferState.FAILED.equals(state)) {
                            progressBar.setVisibility(View.GONE);
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(AddPostActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                        int percentDone = (int) percentDonef;

                        if (Constants.FILE_TYPE_PDF.equals(mainRequest.fileType)) {
                            progressDialog.setMessage("Preparing Pdf " + percentDone + "% " + (index + 1) + " out of " + listImages.size() + ", please wait...");
                        }
                        AppLog.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                                + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        progressBar.setVisibility(View.GONE);
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        AppLog.e(TAG, "Upload Error : " + ex);
                        Toast.makeText(AddPostActivity.this, getResources().getString(R.string.image_upload_error), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void upLoadImageOnCloud(final int pos) {
        if (pos == listImages.size()) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            mainRequest.fileName = listAmazonS3Url;
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
                            Toast.makeText(AddPostActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
                            if (progressDialog != null)
                                progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                        int percentDone = (int) percentDonef;
                        if (Constants.FILE_TYPE_VIDEO.equals(mainRequest.fileType)) {
                            progressDialog.setMessage("Uploading Video " + percentDone + "% " + (pos + 1) + " out of " + listImages.size() + ", please wait...");
                        } else if (Constants.FILE_TYPE_PDF.equals(mainRequest.fileType)) {
                            progressDialog.setMessage("Uploading Pdf " + percentDone + "% " + (pos + 1) + " out of " + listImages.size() + ", please wait...");
                        } else if (Constants.FILE_TYPE_IMAGE.equals(mainRequest.fileType)) {
                            progressDialog.setMessage("Uploading Image " + percentDone + "% " + (pos + 1) + " out of " + listImages.size() + ", please wait...");
                        }
                        AppLog.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                                + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        progressBar.setVisibility(View.GONE);
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        AppLog.e(TAG, "Upload Error : " + ex);
                        Toast.makeText(AddPostActivity.this, getResources().getString(R.string.image_upload_error), Toast.LENGTH_SHORT).show();
                    }
                });
            }catch (Exception e){
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


    public boolean isValid(boolean showToast) {
        boolean valid;

        Log.e("PostType : ", postType + "");
        Log.e("edtDesc : ", edtDesc.getText().toString());
        Log.e("videoUrl : ", videoUrl + "");
        Log.e("videoType : ", fileTypeImageOrVideo + "");
        Log.e("image paths : ", listImages.toString());

        if (!TextUtils.isEmpty(videoUrl)) {
            valid = true;
        } else if (!TextUtils.isEmpty(pdfPath)) {
            valid = true;
        } else if (listImages.size() > 0) {
            valid = true;
        } else {
            if (postType.equals("team") || postType.equals("group")) {
                if (isValueValidOnly(edtTitle) || isValueValidOnly(edtDesc)) {
                    valid = true;
                } else {
                    valid = false;
                    if (showToast)
                        Toast.makeText(this, "Please Add Title and Description or Select Image Or Video", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (isValueValidOnly(edtDesc)) {
                    valid = true;
                } else {
                    valid = false;
                    if (showToast)
                        Toast.makeText(this, "Please Add Description or Select Image Or Video", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (!TextUtils.isEmpty(videoUrl) && listImages.size() > 0) {
            valid = false;
            removeImage();
            removePdf();
            videoUrl = "";
            img_video.setImageResource(R.drawable.icon_youtube);
            Picasso.with(AddPostActivity.this)
                    .load(R.drawable.icon_popup_youtube)
                    .into(img_youtube);
            Toast.makeText(this, "User cannot upload image and video at the same time", Toast.LENGTH_SHORT).show();
        }

        return valid;
    }


    public void onClick(View v) {
        hide_keyboard();
        switch (v.getId()) {
            case R.id.btnShare:
                addPost();
                break;

            case R.id.llImage:
                if (checkPermissionForWriteExternal()) {
                    /*if (listImages.size() > 0) {
                        showPhotoDialog(R.array.array_image_modify);
                    } else {
                    */
                    showPhotoDialog(R.array.array_image);
                    //}

                } else {
                    requestPermissionForWriteExternal(21);
                }
                break;

            case R.id.llVideo:
                if (checkPermissionForCamera()) {
                    showVideoDialog(R.array.array_video);
                } else {
                    requestPermissionForCamera(25);
                }
                break;
            case R.id.llYoutubeLink:
                // if (checkPermissionForWriteExternal()) {
                showYoutubeDialog();
                /*} else {
                    requestPermissionForWriteExternal(22);
                }*/
                break;

            case R.id.llDoc:
                if (checkPermissionForWriteExternal()) {
                    selectPdf(REQUEST_LOAD_PDF);
                } else {
                    requestPermissionForWriteExternal(23);
                }
                break;
            case R.id.switch_reply:
                if (progressBar != null)
                    progressBar.setVisibility(View.VISIBLE);
                manager.allowPersonalReply(this, group_id + "", friend_id);
                break;

            case R.id.switch_comment:
                if (progressBar != null)
                    progressBar.setVisibility(View.VISIBLE);
                manager.allowPersonalComment(this, group_id + "", friend_id);
                break;


        }
    }

    private void recordVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        File cameraFile;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            cameraFile = ImageUtil.getOutputMediaVideo();
            imageCaptureFile = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", cameraFile);
        } else {
            cameraFile = ImageUtil.getOutputMediaVideo();
            imageCaptureFile = Uri.fromFile(cameraFile);
        }

        AppLog.e(TAG , "Video Capture URI : ");

        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureFile);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_RECORD_VIDEO);
        }
    }

    private void selectVideoIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("video/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Video"), REQUEST_LOAD_VIDEO);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        switch (apiId) {
            case LeafManager.API_ALLOW_PERSONAL_REPLY:

                break;

            case LeafManager.API_ALLOW_PERSONAL_COMMENT:

                break;

            default:
                Toast.makeText(AddPostActivity.this, "Successfully Posted", Toast.LENGTH_SHORT).show();
                if (postType.equalsIgnoreCase("group")) {
                    LeafPreference.getInstance(AddPostActivity.this).setBoolean(LeafPreference.ISGENERALPOSTUPDATED, true);
                    new SendNotification("").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    //NOFIREBASEDATABASE
                 /*   FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference();
                    AddPostResponse addPostResponse = (AddPostResponse) response;
                    AppLog.e(TAG, "AddPostResponse : " + addPostResponse);
                    myRef.child("group_post").child(group_id).child(addPostResponse.data.get(0).postId).setValue("");*/

                } else if (postType.equalsIgnoreCase("team")) {
                    LeafPreference.getInstance(AddPostActivity.this).setBoolean(LeafPreference.ISTEAMPOSTUPDATED, true);
                    new SendNotification("").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    //NOFIREBASEDATABASE
                   /* FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference();
                    AddPostResponse addPostResponse = (AddPostResponse) response;
                    AppLog.e(TAG, "AddPostResponse : " + addPostResponse);
                    myRef.child("team_post").child(team_id).child(addPostResponse.data.get(0).postId).setValue("");*/
                } else {
                    LeafPreference.getInstance(AddPostActivity.this).setBoolean(LeafPreference.ISPERSONALPOSTUPDATED, true);
                    LeafPreference.getInstance(this).setBoolean(LeafPreference.PERSONAL_POST_ADDED_2, true);
                    AddPostResponse addPostResponse = (AddPostResponse) response;
                    AppLog.e(TAG, "AddPostResponse : " + addPostResponse);

                    if (addPostResponse.data != null) {
                        for (int i = 0; i < addPostResponse.data.size(); i++) {
                            new SendNotification(addPostResponse.data.get(i).deviceToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    }
                    if (!isFromChat) {
                        startActivity(new Intent(this, ChatActivity.class));
                    }
                }


                finish();

                break;
        }

    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<AddPostValidationError> error) {
        btnShare.setEnabled(true);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        Log.e("AddPostActivity", "OnFailure " + error.title + " , " + error.type);
        if (error.status.equals("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
            finish();
        } else {
            if (error.errors == null)
                return;
            if (error.errors.get(0).video != null) {
                Toast.makeText(this, error.errors.get(0).video, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, error.title, Toast.LENGTH_SHORT).show();
            }
        }
        switch (apiId) {
            case LeafManager.API_ALLOW_PERSONAL_REPLY:
                if (switch_reply.isChecked()) {
                    switch_reply.setChecked(false);
                } else {
                    switch_reply.setChecked(true);
                }
                break;

            case LeafManager.API_ALLOW_PERSONAL_COMMENT:
                if (switch_comment.isChecked()) {
                    switch_comment.setChecked(false);
                } else {
                    switch_comment.setChecked(true);
                }
                break;
        }


    }


    @Override
    public void onException(int apiId, String error) {
        btnShare.setEnabled(true);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        Toast.makeText(AddPostActivity.this, error, Toast.LENGTH_SHORT).show();

        switch (apiId) {
            case LeafManager.API_ALLOW_PERSONAL_REPLY:
                if (switch_reply.isChecked()) {
                    switch_reply.setChecked(false);
                } else {
                    switch_reply.setChecked(true);
                }
                break;

            case LeafManager.API_ALLOW_PERSONAL_COMMENT:
                if (switch_comment.isChecked()) {
                    switch_comment.setChecked(false);
                } else {
                    switch_comment.setChecked(true);
                }
                break;
        }
    }

    public void showVideoDialog(int resId) {
        SMBDialogUtils.showSMBSingleChoiceDialog(AddPostActivity.this,
                R.string.lbl_select_video, resId, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lw = ((AlertDialog) dialog).getListView();
                        switch (lw.getCheckedItemPosition()) {
                            case 0:
                                recordVideoIntent();
                                break;
                            case 1:
                                selectVideoIntent();
                                break;
                        }
                    }
                });
    }

    public void showPhotoDialog(int resId) {
        SMBDialogUtils.showSMBSingleChoiceDialog(AddPostActivity.this,
                R.string.lbl_select_img, resId, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lw = ((AlertDialog) dialog).getListView();
                        switch (lw.getCheckedItemPosition()) {
                            case 0:
                                startCamera(REQUEST_LOAD_CAMERA_IMAGE);
                                break;
                            case 1:
                                startGallery(REQUEST_LOAD_GALLERY_IMAGE);
                                break;
                            case 2:
                                removeImage();
                                break;
                        }
                    }
                });
    }


    private void startCamera(int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File cameraFile;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            cameraFile = ImageUtil.getOutputMediaFile();
            imageCaptureFile = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", cameraFile);
        } else {
            cameraFile = ImageUtil.getOutputMediaFile();
            imageCaptureFile = Uri.fromFile(cameraFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureFile);
        startActivityForResult(intent, requestCode);

    }


    private void selectPdf(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent, requestCode);
    }

    private boolean checkPermissionForWriteExternal() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            Log.e("External" + "permission", "checkpermission , granted");
            return true;
        } else {
            Log.e("External" + "permission", "checkpermission , denied");
            return false;
        }
    }

    private boolean checkPermissionForCamera() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED) {
            Log.e("Camera" + "permission", "checkpermission , granted");
            return true;
        } else {
            Log.e("Camera" + "permission", "checkpermission , denied");
            return false;
        }
    }

    private void requestPermissionForCamera(int code) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            AppLog.e(TAG, "requestPermissionForWriteExternal");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, code);
        }
    }

    public void requestPermissionForWriteExternal(int code) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            AppLog.e(TAG, "requestPermissionForWriteExternal");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);


            Log.e(TAG,"result Uri Crop Image "+result.getUri());

            if (resultCode == RESULT_OK) {


                Uri resultUri = result.getUri();
                Log.e(TAG,"result Uri Crop Image "+resultUri);

                if (isGalleryMultiple)
                {
                    if (isClear)
                    {
                        isClear = false;
                        listImages.clear();

                    }
                    fileTypeImageOrVideo = Constants.FILE_TYPE_IMAGE;
                    listImages.add(resultUri.toString());
                }
                else
                {
                    listImages.clear();
                    fileTypeImageOrVideo = Constants.FILE_TYPE_IMAGE;

                    listImages.add(resultUri.toString());
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e(TAG,"error"+error);
            }

            showLastImage();
            removePdf();

        }

        else if (requestCode == REQUEST_LOAD_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null) {


            final Uri selectedImage = data.getData();
            ClipData clipData = data.getClipData();

            isClear = true;

            if (clipData == null) {

                isGalleryMultiple = false;
//                String path = ImageUtil.getPath(this, selectedImage);
                //  listImages.add(selectedImage.toString());
                CropImage.activity(selectedImage)
                        .start(this);
            } else {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    final Uri uri1 = item.getUri();
//                    String path = ImageUtil.getPath(this, uri1);
                    //    listImages.add(uri1.toString());
                    isGalleryMultiple = true;
                    CropImage.activity(uri1)
                            .start(this);
                }
            }

        }
        else if (requestCode == REQUEST_LOAD_CAMERA_IMAGE && resultCode == Activity.RESULT_OK) {
           /* listImages.clear();
            fileTypeImageOrVideo = Constants.FILE_TYPE_IMAGE;*/
//            String path = cameraFile.getAbsolutePath();
            AppLog.e(TAG, "imageCaptureFile : " + imageCaptureFile);
            //          listImages.add(imageCaptureFile.toString());
            isGalleryMultiple = false;

         /*   showLastImage();
            removePdf();
            removeAudio();*/

            CropImage.activity(imageCaptureFile)
                    .setOutputUri(imageCaptureFile)
                    .start(this);

        }

        else if (requestCode == REQUEST_RECORD_VIDEO && resultCode == Activity.RESULT_OK) {
            listImages.clear();
            fileTypeImageOrVideo = Constants.FILE_TYPE_VIDEO;
            isFromCamera = true;
//            String path = cameraFile.getAbsolutePath();
            AppLog.e(TAG, "imageCaptureFile : " + imageCaptureFile);
            listImages.add(imageCaptureFile.toString());
            showLastImage();
            removePdf();
        } else if (requestCode == REQUEST_LOAD_VIDEO && resultCode == Activity.RESULT_OK) {
            listImages.clear();
            fileTypeImageOrVideo = Constants.FILE_TYPE_VIDEO;
            isFromCamera = false;
            final Uri selectedImage = data.getData();
            AppLog.e(TAG, "selectedVideo : " + selectedImage);
            ClipData clipData = data.getClipData();
            if (clipData == null) {
//                String path = ImageUtil.getPath(this, selectedImage);
                listImages.add(selectedImage.toString());
            } else {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    final Uri uri1 = item.getUri();
//                    String path = ImageUtil.getPath(this, uri1);
                    listImages.add(uri1.toString());
                }
            }
            showLastImage();
            removePdf();
        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_LOAD_PDF) {
                pdfPath = data.getData().toString();
                Log.e("pdfUri : ", pdfPath);
               /* if (selectedImageURI.toString().startsWith("content")) {
                    pdfUri = ImageUtil.getPath(this, selectedImageURI);
                } else {
                    pdfUri = selectedImageURI.getPath();
                }
*/
                if (TextUtils.isEmpty(pdfPath)) {
                    Toast.makeText(getApplicationContext(), "Please select a pdf file", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.e("PDF", "imgUrl is " + pdfPath);

                if (!TextUtils.isEmpty(pdfPath))
                    Picasso.with(this).load(R.drawable.pdf_thumbnail).into(imgDoc);
                removeImage();
            }
        }

        shareButtonEnableDisable();

    }

    private void showLastImage() {
        imageAdapter.setType(fileTypeImageOrVideo);
        imageAdapter.notifyDataSetChanged();
    }

    private void removeImage() {
        listImages.clear();
        Picasso.with(this).load(R.drawable.icon_gallery).into(img_image);
        showLastImage();
        shareButtonEnableDisable();
    }

    private void removePdf() {
        pdfPath = "";
        Picasso.with(this).load(R.drawable.icon_doc).into(imgDoc);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 21:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showPhotoDialog(R.array.array_image);
                    Log.e("AddPost" + "permission", "granted camera");
                } else {
                    Log.e("AddPost" + "permission", "denied camera");
                }
                break;

            case 25:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showVideoDialog(R.array.array_video);
                    Log.e("AddPost" + "permission", "granted camera");
                } else {
                    Log.e("AddPost" + "permission", "denied camera");
                }
                break;
            case 23:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectPdf(REQUEST_LOAD_PDF);
                } else {
                    Log.e("AddPost" + "permission", "denied camera");
                }
                break;
        }
    }

    public static String extractYoutubeId(String ytUrl) {

        final String[] videoIdRegex = {"\\?vi?=([^&])", "watch\\?.v=([^&])", "(?:embed|vi?)/([^/?])", "^([A-Za-z0-9\\-\\_]*)"};
        String youTubeLinkWithoutProtocolAndDomain = youTubeLinkWithoutProtocolAndDomain(ytUrl);

        for (String regex : videoIdRegex) {
            Pattern compiledPattern = Pattern.compile(regex);
            Matcher matcher = compiledPattern.matcher(youTubeLinkWithoutProtocolAndDomain);

            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        return null;
    }

    public static String youTubeLinkWithoutProtocolAndDomain(String url) {
        final String youTubeUrlRegEx = "^(https?)?(://)?(www.)?(m.)?((youtube.com)|(youtu.be))/";
        Pattern compiledPattern = Pattern.compile(youTubeUrlRegEx);
        Matcher matcher = compiledPattern.matcher(url);

        if (matcher.find()) {
            return url.replace(matcher.group(), "");
        }
        return url;
    }

    public void showYoutubeDialog() {
        dialog = new Dialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_youtube);

        btn_ok = (TextView) dialog.findViewById(R.id.btn_ok);

        btn_cancel = (TextView) dialog.findViewById(R.id.btn_cancel);

        btn_upload = (TextView) dialog.findViewById(R.id.btn_upload);
        btn_upload.setVisibility(View.INVISIBLE);

        edt_link = (EditText) dialog.findViewById(R.id.edt_link);

        if (!videoUrl.equals(""))
            btn_cancel.setText("Remove");

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoUrl = edt_link.getText().toString();
                if (videoUrl.equals(""))
                    Toast.makeText(AddPostActivity.this, "Enter youtube link", Toast.LENGTH_SHORT).show();
                else {
                    fileTypeImageOrVideo = Constants.FILE_TYPE_YOUTUBE;
                    String videoId = "";
                    videoId = extractYoutubeId(videoUrl);

                    if (GroupDashboardActivityNew.selectedUrl.equals(videoUrl))
                        videoId = GroupDashboardActivityNew.selectedYoutubeId;

                    Log.e("VideoId is->", "" + videoId);

                    String img_url = "http://img.youtube.com/vi/" + videoId + "/0.jpg"; // this is link which will give u thumnail image of that video

                    Log.e("img_url is->", "" + img_url);

                    Picasso.with(AddPostActivity.this)
                            .load(img_url)
                            .placeholder(R.drawable.icon_popup_youtube)
                            .into(img_youtube, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Log.e("onSuccess is->", "onSuccess");
                                    shareButtonEnableDisable();
                                }

                                @Override
                                public void onError() {
                                    Log.e("onError is->", "onError");
                                    videoUrl = "";
                                    Toast.makeText(AddPostActivity.this, "Not a valid youtube link", Toast.LENGTH_SHORT).show();
                                }
                            });
                    dialog.dismiss();
                }
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isValueValid(edtTitle)) {
                    dialog.dismiss();
                    return;
                }
                if (!isValueValid(edtDesc)) {
                    dialog.dismiss();
                    return;
                }
                GroupDashboardActivityNew.enteredTitle = edtTitle.getText().toString();
                GroupDashboardActivityNew.enteredDesc = edtDesc.getText().toString();

                startActivity(new Intent(AddPostActivity.this, MainActivity.class));
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Picasso.with(AddPostActivity.this)
                        .load(R.drawable.icon_popup_youtube)
                        .into(img_youtube);*/
                hide_keyboard(edt_link);
                dialog.dismiss();

            }
        });

        dialog.show();
    }

    @Override
    public void onImageSelect() {

    }

    @Override
    public void onImageRemove() {
        shareButtonEnableDisable();
    }

    private class SendNotification extends AsyncTask<String, String, String> {
        String receiverToken;
        private String server_response;

        public SendNotification(String token) {
            receiverToken = token;
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
                    if (postType.equals("group")) {
                        message = userName + " Has Posted in " + GroupDashboardActivityNew.group_name;
                        topic = group_id;
                        object.put("to", "/topics/" + topic);
                    } else if (postType.equals("team")) {
                        message = userName + " Has Posted in " + team_name + " Team";
                        topic = group_id + "_" + team_id;
                        object.put("to", "/topics/" + topic);
                    } else {
                        message = userName + " Has Sent you Message";
                        object.put("to", receiverToken);
                    }
                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title", title);
                    notificationObj.put("body", message);
                    //   object.put("notification", notificationObj);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("groupId", group_id);
                    dataObj.put("createdById", LeafPreference.getInstance(AddPostActivity.this).getString(LeafPreference.LOGIN_ID));
                    dataObj.put("postId", "");
                    dataObj.put("teamId", team_id);
                    dataObj.put("title", title);
                    dataObj.put("postType", postType);
                    dataObj.put("Notification_type", "post");
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
