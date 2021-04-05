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
import androidx.cardview.widget.CardView;
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
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.google.gson.Gson;
import com.iceteck.silicompressorr.SiliCompressor;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;
import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.adapters.UploadImageAdapter;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.AddGalleryPostRequest;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneHelper;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.GetThumbnail;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.youtube.MainActivity;
import school.campusconnect.views.SMBDialogUtils;


public class AddGalleryPostActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<AddPostValidationError>, View.OnClickListener, UploadImageAdapter.UploadImageListener {

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

    @Bind(R.id.rvImages)
    RecyclerView rvImages;

    @Bind(R.id.cardAlbumName)
    CardView cardAlbumName;

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

    String videoUrl = "";
    String fileTypeImageOrVideo;

    LeafManager manager = new LeafManager();

    ArrayList<String> listAmazonS3Url = new ArrayList<>();
    ArrayList<String> listImages = new ArrayList<>();
    private String pdfPath = "";
    private TransferUtility transferUtility;
    private AddGalleryPostRequest mainRequest;
    private File cameraFile;
    private UploadImageAdapter imageAdapter;
    private String receiverToken = "";
    private String receiverDeviceType = "";
    boolean isEdit;
    String album_id;
    private String type;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gallery_post);
        ButterKnife.bind(this);

        init();

        setListener();

    }

    private void setListener() {
        llImage.setOnClickListener(this);
        llVideo.setOnClickListener(this);
        llYoutubeLink.setOnClickListener(this);
        llDoc.setOnClickListener(this);
        btnShare.setOnClickListener(this);
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

        group_id = GroupDashboardActivityNew.groupId;
//        edtDesc.setMovementMethod(new ScrollingMovementMethod());
//        edtTitle.setMovementMethod(new ScrollingMovementMethod());

        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        transferUtility = AmazoneHelper.getTransferUtility(this);

        if (getIntent() != null) {
            isEdit = getIntent().getBooleanExtra("isEdit", false);
            if (isEdit) {
                album_id = getIntent().getStringExtra("album_id");
                type = getIntent().getStringExtra("type");
                cardAlbumName.setVisibility(View.GONE);
                tvLabelTitle.setVisibility(View.GONE);

                if (Constants.FILE_TYPE_IMAGE.equalsIgnoreCase(type)) {
                    llVideo.setEnabled(false);
                    llYoutubeLink.setEnabled(false);
                    img_youtube.setColorFilter(ContextCompat.getColor(this, R.color.divider_post), android.graphics.PorterDuff.Mode.SRC_IN);
                    img_video.setColorFilter(ContextCompat.getColor(this, R.color.divider_post), android.graphics.PorterDuff.Mode.SRC_IN);
                }else if (Constants.FILE_TYPE_VIDEO.equalsIgnoreCase(type)) {
                    llYoutubeLink.setEnabled(false);
                    img_youtube.setColorFilter(ContextCompat.getColor(this, R.color.divider_post), android.graphics.PorterDuff.Mode.SRC_IN);
                    llImage.setEnabled(false);
                    img_image.setColorFilter(ContextCompat.getColor(this, R.color.divider_post), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                else {
                    llImage.setEnabled(false);
                    llVideo.setEnabled(false);
                    img_image.setColorFilter(ContextCompat.getColor(this, R.color.divider_post), android.graphics.PorterDuff.Mode.SRC_IN);
                    img_video.setColorFilter(ContextCompat.getColor(this, R.color.divider_post), android.graphics.PorterDuff.Mode.SRC_IN);
                }
            }
        }
        rvImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new UploadImageAdapter(listImages, this);
        rvImages.setAdapter(imageAdapter);

        if(isEdit){
            setTitle("Add File to album");
        }else {
            setTitle("Add to Gallery");
        }


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

                AddGalleryPostRequest request = new AddGalleryPostRequest();
                request.albumName = edtTitle.getText().toString();

                if (!TextUtils.isEmpty(videoUrl)) {
                    request.video = videoUrl;
                    request.fileType = Constants.FILE_TYPE_YOUTUBE;

                    Log.e(TAG, "send data " + new Gson().toJson(request));
                    mainRequest = request;

                    if (isEdit) {
                        request.fileType = Constants.FILE_TYPE_VIDEO;
                        request.fileName = new ArrayList<>();
                        request.fileName.add(videoUrl);
                        manager.addGalleryFile(this, group_id, album_id, request);
                    } else {
                        manager.addGalleryPost(this, group_id, request);
                    }

                } else if (!TextUtils.isEmpty(pdfPath)) {
                    // request.fileType = Constants.FILE_TYPE_PDF;
                    //uploadToAmazone(request);
                }
                else if (listImages.size() > 0 && Constants.FILE_TYPE_VIDEO.equals(fileTypeImageOrVideo)) {
                    request.fileType = fileTypeImageOrVideo;
                    Log.e(TAG, "send data " + new Gson().toJson(request));
//                    progressDialog.setMessage("Preparing Video...");
//                    progressDialog.show();
//                    compressVideo(request, 0);
                    new VideoCompressor(request).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                else if (listImages.size() > 0) {
                    request.fileType = Constants.FILE_TYPE_IMAGE;
                    uploadToAmazone(request);
                } /*else {
                    Log.e(TAG, "send data " + new Gson().toJson(request));
                    mainRequest = request;
                    manager.addGalleryPost(this, group_id, request);

                }*/
            }
        } else {
            showNoNetworkMsg();
        }

    }
    public class VideoCompressor extends AsyncTask<Void, Integer, Boolean> {
        private AddGalleryPostRequest addPostRequest;
        public VideoCompressor(AddGalleryPostRequest addPostRequest) {
            this.addPostRequest = addPostRequest;
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
            progressDialog.setMessage("Preparing Video... "+values[0]+" out of "+listImages.size()+", please wait...");
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                for (int i = 0; i < listImages.size(); i++) {
                    int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onProgressUpdate((finalI +1));
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
                        listImages.set(i, SiliCompressor.with(AddGalleryPostActivity.this).compressVideo(listImages.get(i), getExternalCacheDir().getAbsolutePath()));
                        Log.e(TAG, "compressPath : " + videoUrl);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(AddGalleryPostActivity.this, "Error In Compression :" + e.getMessage(), Toast.LENGTH_SHORT).show();
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


    private void uploadToAmazone(AddGalleryPostRequest request) {
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
                    if(listThumbnails!=null){
                        uploadThumbnail(listThumbnails,0);
                    }else {
                        Toast.makeText(AddGalleryPostActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    }

                }
            },Constants.FILE_TYPE_PDF);
        } else if (request.fileType.equals(Constants.FILE_TYPE_VIDEO)) {
            AppLog.e(TAG, "Final videos :: " + listImages.toString());
            GetThumbnail.create(listImages, new GetThumbnail.GetThumbnailListener() {
                @Override
                public void onThumbnail(ArrayList<String> listThumbnails) {
                    if(listThumbnails!=null){
                        uploadThumbnail(listThumbnails,0);
                    }else {
                        Toast.makeText(AddGalleryPostActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    }

                }
            },Constants.FILE_TYPE_VIDEO);
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
        }else {
            final String key = AmazoneHelper.getAmazonS3KeyThumbnail(mainRequest.fileType);
            File file = new File(listThumbnails.get(index));
            TransferObserver observer = transferUtility.upload(AmazoneHelper.BUCKET_NAME, key,
                    file , CannedAccessControlList.PublicRead);

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

                        listThumbnails.set(index,_finalUrl);

                        uploadThumbnail(listThumbnails,index+1);

                    }
                    if (TransferState.FAILED.equals(state)) {
                        progressBar.setVisibility(View.GONE);
                        if (Constants.FILE_TYPE_VIDEO.equals(mainRequest.fileType)) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(AddGalleryPostActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
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
                    progressBar.setVisibility(View.GONE);
                    if (Constants.FILE_TYPE_VIDEO.equals(mainRequest.fileType)) {
                        progressDialog.dismiss();
                    }
                    AppLog.e(TAG, "Upload Error : " + ex);
                    Toast.makeText(AddGalleryPostActivity.this, getResources().getString(R.string.image_upload_error), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void upLoadImageOnCloud(final int pos) {

        if (pos == listImages.size()) {
            if (Constants.FILE_TYPE_VIDEO.equals(mainRequest.fileType)) {
                progressDialog.dismiss();
            }
            mainRequest.fileName = listAmazonS3Url;
            AppLog.e(TAG, "send data : " + new Gson().toJson(mainRequest));
            if (isEdit) {
                manager.addGalleryFile(this, group_id, album_id, mainRequest);
            } else {
                manager.addGalleryPost(this, group_id, mainRequest);
            }

        } else {
            final String key = AmazoneHelper.getAmazonS3Key(mainRequest.fileType);
            File file = new File(listImages.get(pos));
            TransferObserver observer = transferUtility.upload(AmazoneHelper.BUCKET_NAME, key,
                    file , CannedAccessControlList.PublicRead);

            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    AppLog.e(TAG, "onStateChanged: " + id + ", " + state.name());
                    if (state.toString().equalsIgnoreCase("COMPLETED")) {
                        Log.e("MULTI_IMAGE", "onStateChanged " + pos);
                        updateList(pos, key);
                    }
                    if (TransferState.FAILED.equals(state)) {
                        Toast.makeText(AddGalleryPostActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                    int percentDone = (int) percentDonef;
                    if (Constants.FILE_TYPE_VIDEO.equals(mainRequest.fileType)) {
                        progressDialog.setMessage("Uploading Video... " + percentDone + "% " + (pos + 1) + " out of " + listImages.size()+", please wait...");
                    }
                    AppLog.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                            + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
                }

                @Override
                public void onError(int id, Exception ex) {
                    progressBar.setVisibility(View.GONE);
                    if (Constants.FILE_TYPE_VIDEO.equals(mainRequest.fileType)) {
                        progressDialog.dismiss();
                    }
                    AppLog.e(TAG, "Upload Error : " + ex);
                    Toast.makeText(AddGalleryPostActivity.this, getResources().getString(R.string.image_upload_error), Toast.LENGTH_SHORT).show();
                }
            });
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
        boolean valid = true;

        Log.e("edtDesc : ", edtTitle.getText().toString());
        Log.e("videoUrl : ", videoUrl);
        Log.e("image paths : ", listImages.toString());
        Log.e("videoType : ", fileTypeImageOrVideo + "");

        if (isEdit) {
            if (listImages.size() == 0 && TextUtils.isEmpty(videoUrl)) {
                if (showToast)
                    Toast.makeText(this, "Please Add Image or video", Toast.LENGTH_SHORT).show();
                valid = false;
            }
            if (!TextUtils.isEmpty(videoUrl) && listImages.size() > 0) {
                valid = false;
                removeImage();
                removePdf();
                Toast.makeText(this, "" + getResources().getString(R.string.msg_upload2), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (!isValueValidOnly(edtTitle)) {
                if (showToast)
                    Toast.makeText(this, "Please Add Album Name", Toast.LENGTH_SHORT).show();
                valid = false;
            }
            if (listImages.size() == 0 && TextUtils.isEmpty(videoUrl)) {
                if (showToast)
                    Toast.makeText(this, "Please Add Image or video", Toast.LENGTH_SHORT).show();
                valid = false;
            }
            if (!TextUtils.isEmpty(videoUrl) && listImages.size() > 0) {
                valid = false;
                removeImage();
                removePdf();
                Toast.makeText(this, "" + getResources().getString(R.string.msg_upload2), Toast.LENGTH_SHORT).show();
            }
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
                   /* if (listImages.size() > 0) {
                        showPhotoDialog(R.array.array_image_modify);
                    } else {*/
                        showPhotoDialog(R.array.array_image);
                    //}

                } else {
                    requestPermissionForWriteExternal(21);
                }
                break;

            case R.id.llVideo:
                if (checkPermissionForWriteExternal()) {
                    selectVideoIntent();
                } else {
                    requestPermissionForWriteExternal(22);
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

            default:
                Toast.makeText(AddGalleryPostActivity.this, "Successfully Posted", Toast.LENGTH_SHORT).show();

                LeafPreference.getInstance(AddGalleryPostActivity.this).setBoolean(LeafPreference.ISGALLERY_POST_UPDATED, true);

                finish();
                break;
            case LeafManager.API_GALLERY_FILE_ADD:
                Toast.makeText(AddGalleryPostActivity.this, "Add File Successfully", Toast.LENGTH_SHORT).show();

                LeafPreference.getInstance(AddGalleryPostActivity.this).setBoolean(LeafPreference.ISGALLERY_POST_UPDATED, true);

                Intent intent = new Intent(this,GalleryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
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

            if (!TextUtils.isEmpty(error.message)) {
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show();
            }else if (error.errors.get(0).video != null) {
                Toast.makeText(this, error.errors.get(0).video, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, error.title, Toast.LENGTH_SHORT).show();
            }
        }


    }


    @Override
    public void onException(int apiId, String error) {
        btnShare.setEnabled(true);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        Toast.makeText(AddGalleryPostActivity.this, error, Toast.LENGTH_SHORT).show();

    }


    public void showPhotoDialog(int resId) {
        SMBDialogUtils.showSMBSingleChoiceDialog(AddGalleryPostActivity.this,
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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
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
        if (requestCode == REQUEST_LOAD_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            listImages.clear();
            fileTypeImageOrVideo = Constants.FILE_TYPE_IMAGE;
            final Uri selectedImage = data.getData();
            ClipData clipData = data.getClipData();
            if (clipData == null) {
                String path = ImageUtil.getPath(this, selectedImage);
                listImages.add(path);
            } else {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    final Uri uri1 = item.getUri();
                    String path = ImageUtil.getPath(this, uri1);
                    listImages.add(path);
                }
            }
            showLastImage();
            removePdf();
        } else if (requestCode == REQUEST_LOAD_CAMERA_IMAGE && resultCode == Activity.RESULT_OK) {
            listImages.clear();
            fileTypeImageOrVideo = Constants.FILE_TYPE_IMAGE;
            String path = cameraFile.getAbsolutePath();
            AppLog.e(TAG, "path : " + path);
            listImages.add(path);
            showLastImage();
            removePdf();
        } else if (requestCode == REQUEST_LOAD_VIDEO && resultCode == Activity.RESULT_OK) {
            listImages.clear();
            fileTypeImageOrVideo = Constants.FILE_TYPE_VIDEO;
            final Uri selectedImage = data.getData();
            ClipData clipData = data.getClipData();
            if (clipData == null) {
                String path = ImageUtil.getPath(this, selectedImage);
                listImages.add(path);
            } else {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    final Uri uri1 = item.getUri();
                    String path = ImageUtil.getPath(this, uri1);
                    listImages.add(path);
                }
            }
            showLastImage();
            removePdf();
        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_LOAD_PDF) {
                Uri selectedImageURI = data.getData();
                Log.e("SelectedURI : ", selectedImageURI.toString());
                if (selectedImageURI.toString().startsWith("content")) {
                    pdfPath = ImageUtil.getPath(this, selectedImageURI);
                } else {
                    pdfPath = selectedImageURI.getPath();
                }
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

            case 22:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectVideoIntent();
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
                    Toast.makeText(AddGalleryPostActivity.this, "Enter youtube link", Toast.LENGTH_SHORT).show();
                else {
                    String videoId = "";
                    videoId = extractYoutubeId(videoUrl);

                    if (GroupDashboardActivityNew.selectedUrl.equals(videoUrl))
                        videoId = GroupDashboardActivityNew.selectedYoutubeId;

                    Log.e("VideoId is->", "" + videoId);

                    String img_url = "http://img.youtube.com/vi/" + videoId + "/0.jpg"; // this is link which will give u thumnail image of that video

                    Log.e("img_url is->", "" + img_url);

                    Picasso.with(AddGalleryPostActivity.this)
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
                                    Toast.makeText(AddGalleryPostActivity.this, "Not a valid youtube link", Toast.LENGTH_SHORT).show();
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

                startActivity(new Intent(AddGalleryPostActivity.this, MainActivity.class));
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

  /*  private class SendNotification extends AsyncTask<String,String,String>
    {
        private final AddPostRequest requestData;
        private String server_response;

        public SendNotification(AddPostRequest mainRequest) {
            requestData=mainRequest;
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
                urlConnection.setRequestProperty ("Authorization", BuildConfig.API_KEY_FIREBASE);
                urlConnection.setRequestProperty ("Content-Type", "application/json");

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                try {
                    JSONObject object = new JSONObject();

                    String topic;
                    String title=getResources().getString(R.string.app_name);
                    String message="";
                    if(postType.equals("group"))
                    {
                        message=userName+" Has Posted in "+GroupDashboardActivityNew.group_name;
                        topic=group_id;
                        object.put("to","/topics/"+ topic);
                    }
                    else if(postType.equals("team"))
                    {
                        message=userName+" Has Posted in "+team_name+" Team";
                        topic=group_id+"_"+team_id;
                        object.put("to","/topics/"+ topic);
                    }
                    else
                    {
                        message=userName+" Has Sent you Message";
                        object.put("to",receiverToken);
                    }
                    JSONObject notificationObj=new JSONObject();
                    notificationObj.put("title",title);
                    notificationObj.put("body",message);
                    object.put("notification",notificationObj);

                    JSONObject dataObj=new JSONObject();
                    dataObj.put("groupId",group_id);
                    dataObj.put("createdById",LeafPreference.getInstance(AddGalleryPostActivity.this).getString(LeafPreference.LOGIN_ID));
                    dataObj.put("postId","");
                    dataObj.put("teamId",team_id);
                    dataObj.put("title",title);
                    dataObj.put("postType",postType);
                    dataObj.put("Notification_type","post");
                    dataObj.put("body",message);
                    object.put("data",dataObj);

                    wr.writeBytes(object.toString());
                    Log.e(TAG , " JSON input : "+ object.toString());
                    wr.flush();
                    wr.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                AppLog.e(TAG,"responseCode :"+responseCode);
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
            AppLog.e(TAG,"server_response :"+server_response);

            if(!TextUtils.isEmpty(server_response))
            {
                AppLog.e(TAG,"Notification Sent");
            }
            else
            {
                AppLog.e(TAG,"Notification Send Fail");
            }
        }
    }
*/
}