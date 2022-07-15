package school.campusconnect.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.mobileconnectors.s3.transferutility.UploadOptions;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.google.gson.Gson;
import com.iceteck.silicompressorr.SiliCompressor;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
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
import school.campusconnect.datamodel.AddGalleryPostRequest;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.chapter.ChapterRes;
import school.campusconnect.datamodel.homework.AddHwPostRequest;
import school.campusconnect.fragments.DatePickerFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneHelper;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BackgroundVideoUploadChapterService;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.GetThumbnail;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.crop.CropDialogActivity;
import school.campusconnect.utils.youtube.MainActivity;
import school.campusconnect.views.SMBDialogUtils;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;


public class AddHwPostActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<AddPostValidationError>, View.OnClickListener, UploadImageAdapter.UploadImageListener {

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

    @Bind(R.id.et_date)
    EditText et_date;

    @Bind(R.id.et_time)
    EditText et_time;

    @Bind(R.id.cardDate)
    CardView cardDate;

    @Bind(R.id.cardTime)
    CardView cardTime;

    @Bind(R.id.switchDate)
    SwitchCompat switchDate;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;

    @Bind(R.id.iconBack)
    public ImageView iconBack;

    @Bind(R.id.imgHome)
    public ImageView imgHome;

    TextView btn_ok;
    TextView btn_cancel;
    TextView btn_upload;

    EditText edt_link;

    Dialog dialog;

    String group_id;
    String team_id;
    String subject_id;
    String subject_name;
    String className;

    public Uri imageCaptureFile;


    public static final int REQUEST_LOAD_CAMERA_IMAGE = 101;
    public static final int REQUEST_LOAD_GALLERY_IMAGE = 102;
    public static final int REQUEST_LOAD_PDF = 103;
    public static final int REQUEST_LOAD_VIDEO = 104;

    String videoUrl = "";
    String fileTypeImageOrVideo;

    private Boolean isGalleryMultiple = false;
    private Boolean isClear = true;

    LeafManager manager = new LeafManager();

    ArrayList<String> listAmazonS3Url = new ArrayList<>();
    ArrayList<String> listImages = new ArrayList<>();
    private String pdfPath = "";
    private TransferUtility transferUtility;
    private AddHwPostRequest mainRequest;
    private File cameraFile;
    private UploadImageAdapter imageAdapter;
    private String receiverToken = "";
    private String receiverDeviceType = "";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hw_post);
        ButterKnife.bind(this);

        init();

        setListener();

        String fileType = LeafApplication.getInstance().getType();
        if(Constants.FILE_TYPE_IMAGE.equalsIgnoreCase(fileType) || Constants.FILE_TYPE_PDF.equalsIgnoreCase(fileType)){
            ArrayList<String> shareList = LeafApplication.getInstance().getShareFileList();
            if(shareList!=null && shareList.size()>0){
                SMBDialogUtils.showSMBDialogYesNoCancel(this, getResources().getString(R.string.smb_attach_file), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        String fileType = LeafApplication.getInstance().getType();
                        if(Constants.FILE_TYPE_IMAGE.equalsIgnoreCase(fileType)){
                            listImages.addAll(shareList);
                            fileTypeImageOrVideo = fileType;
                            showLastImage();
                        }else if(Constants.FILE_TYPE_PDF.equalsIgnoreCase(fileType)){
                            pdfPath = shareList.get(0);
                            Picasso.with(AddHwPostActivity.this).load(R.drawable.pdf_thumbnail).into(imgDoc);
                        }
                    }
                });
            }
        }
    }

    private void setListener() {
        llImage.setOnClickListener(this);
        llVideo.setOnClickListener(this);
        llYoutubeLink.setOnClickListener(this);
        llDoc.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        //btnShare.setEnabled(false);

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

        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerFragment fragment = DatePickerFragment.newInstance();

                fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                    @Override
                    public void onDateSelected(Calendar c) {
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        et_date.setText(format.format(c.getTime()));
                    }
                });
                fragment.show(getSupportFragmentManager(), "datepicker");
                fragment.setTitle(R.string.lbl_from_date);

                /*final Calendar calendar = Calendar.getInstance();
                DatePickerDialog fragment = new DatePickerDialog(AddHwPostActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        et_date.setText(format.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                fragment.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                fragment.show();*/
            }
        });
        et_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                TimePickerDialog fragment = new TimePickerDialog(AddHwPostActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                        et_time.setText(format.format(calendar.getTime()));
                    }
                },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false);
                fragment.show();
            }
        });

        switchDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cardDate.setVisibility(View.VISIBLE);
                    cardTime.setVisibility(View.VISIBLE);
                }else {
                    cardDate.setVisibility(View.GONE);
                    cardTime.setVisibility(View.GONE);
                }
            }
        });
    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        group_id = GroupDashboardActivityNew.groupId;

        transferUtility = AmazoneHelper.getTransferUtility(this);

        setSupportActionBar(mToolBar);
        setBackEnabled(false);
        tvTitle.setText(getResources().getString(R.string.txt_add_homework));
        setTitle("");
        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddHwPostActivity.this,GroupDashboardActivityNew.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        if (getIntent() != null) {
            group_id = getIntent().getStringExtra("group_id");
            team_id = getIntent().getStringExtra("team_id");
            subject_id = getIntent().getStringExtra("subject_id");
            subject_name = getIntent().getStringExtra("subject_name");
            className = getIntent().getStringExtra("className");
        }
        rvImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new UploadImageAdapter(listImages, this);
        rvImages.setAdapter(imageAdapter);
    }


    private void shareButtonEnableDisable() {
       // btnShare.setEnabled(isValid(false));
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
                    showLoadingBar(progressBar,false);
           //         progressBar.setVisibility(View.VISIBLE);
              //  btnShare.setEnabled(false);

                mainRequest = new AddHwPostRequest();

                mainRequest.title = edtTitle.getText().toString();
                mainRequest.text = edtDesc.getText().toString();
                mainRequest.lastSubmissionDate = et_date.getText().toString();
                mainRequest.lastSubmissionTime = et_date.getText().toString();

                if (!TextUtils.isEmpty(videoUrl)) {
                    mainRequest.video = videoUrl;
                    mainRequest.fileType = Constants.FILE_TYPE_YOUTUBE;

                    Log.e(TAG, "send data " + new Gson().toJson(mainRequest));

                  /*  if (isEdit) {
                        mainRequest.fileType = Constants.FILE_TYPE_VIDEO;
                        mainRequest.fileName = new ArrayList<>();
                        mainRequest.fileName.add(videoUrl);
                        manager.addChapterTopicPost(this, group_id, team_id, subject_id, chapter_id, mainRequest);
                    } else {*/
                        manager.addHwPost(this, group_id, team_id, subject_id, mainRequest);
//                    }

                } else if (!TextUtils.isEmpty(pdfPath)) {
                    mainRequest.fileType = Constants.FILE_TYPE_PDF;
                    progressDialog.setMessage("Preparing Pdf...");
                    progressDialog.show();
                    uploadToAmazone(mainRequest);
                } else if (listImages.size() > 0 && Constants.FILE_TYPE_VIDEO.equals(fileTypeImageOrVideo)) {
                    mainRequest.fileType = fileTypeImageOrVideo;
                    Log.e(TAG, "send data " + new Gson().toJson(mainRequest));
                    startService();
//                    new VideoCompressor(request).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else if (listImages.size() > 0) {
                    mainRequest.fileType = Constants.FILE_TYPE_IMAGE;
                    progressDialog.setMessage("Uploading Image...");
                    progressDialog.show();
                    uploadToAmazone(mainRequest);
                }
                else
                {
                    manager.addHwPost(this, group_id, team_id, subject_id, mainRequest);
                }
            }
        } else {
            showNoNetworkMsg();
        }

    }
    public void startService() {

      /*  Intent serviceIntent = new Intent(this, BackgroundVideoUploadChapterService.class);
        serviceIntent.putExtra("videoUrl", videoUrl);
        serviceIntent.putExtra("mainRequest", mainRequest);
        serviceIntent.putExtra("listImages", listImages);
        serviceIntent.putExtra("group_id", group_id);
        serviceIntent.putExtra("team_id", team_id);
        serviceIntent.putExtra("chapter_id", chapter_id);
        serviceIntent.putExtra("subject_id", subject_id);
        serviceIntent.putExtra("subject_name", subject_name);
        serviceIntent.putExtra("isEdit", isEdit);
        ContextCompat.startForegroundService(this, serviceIntent);

        Toast.makeText(this, "Video Uploading in background", Toast.LENGTH_SHORT).show();
        finish();*/

    }

    public class VideoCompressor extends AsyncTask<Void, Integer, Boolean> {
        private AddHwPostRequest addPostRequest;

        public VideoCompressor(AddHwPostRequest addPostRequest) {
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
            progressDialog.setMessage("Preparing Video " + values[0] + " out of " + listImages.size() + ", please wait...");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
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


                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(listImages.get(i));

                    Bitmap bmp = retriever.getFrameAtTime();
                    String orient = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
                    int h;
                    int w;
                    if (orient.equals("0")) {
                        h = bmp.getHeight();
                        w = bmp.getWidth();
                    } else {
                        w = bmp.getHeight();
                        h = bmp.getWidth();
                    }

                    AppLog.e(TAG, "fileSizeInMB : " + fileSizeInMB);
                    if (fileSizeInMB > 10) {
                        listImages.set(i, SiliCompressor.with(AddHwPostActivity.this).compressVideo(listImages.get(i), getExternalCacheDir().getAbsolutePath() , w ,h , 950000));
                        Log.e(TAG, "compressPath : " + videoUrl);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(AddHwPostActivity.this, getResources().getString(R.string.toast_error_comression) + e.getMessage(), Toast.LENGTH_SHORT).show();
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


    private void uploadToAmazone(AddHwPostRequest request) {
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
                        Toast.makeText(AddHwPostActivity.this, getResources().getString(R.string.toast_upload_failed), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AddHwPostActivity.this, getResources().getString(R.string.toast_upload_failed), Toast.LENGTH_SHORT).show();
                    }

                }
            }, Constants.FILE_TYPE_VIDEO);
        } else {
            for (int i = 0; i < listImages.size(); i++) {
                Bitmap bitmap = null;
                try {
                    InputStream is =  getContentResolver().openInputStream(Uri.parse(listImages.get(i)));
                    bitmap =ImageUtil.scaleDown(BitmapFactory.decodeStream(is), 1200, false);
                    listImages.set(i, ImageUtil.resizeImage(getApplicationContext(), bitmap, "test"));

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    AppLog.e(TAG , "Error Occurred : "+e.getLocalizedMessage());
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

            TransferObserver observer ;
            UploadOptions option = UploadOptions.
                    builder().bucket(AmazoneHelper.BUCKET_NAME).
                    cannedAcl(CannedAccessControlList.PublicRead).build();
            try {
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
                            hideLoadingBar();
                     //       progressBar.setVisibility(View.GONE);
                            if (progressDialog!=null) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(AddHwPostActivity.this, getResources().getString(R.string.toast_upload_failed), Toast.LENGTH_SHORT).show();
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
                        hideLoadingBar();
                        //       progressBar.setVisibility(View.GONE);
                        if (progressDialog!=null) {
                            progressDialog.dismiss();
                        }
                        AppLog.e(TAG, "Upload Error : " + ex);
                        Toast.makeText(AddHwPostActivity.this, getResources().getString(R.string.image_upload_error), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    private void upLoadImageOnCloud(final int pos) {

        if (pos == listImages.size()) {
            if (progressDialog!=null) {
                progressDialog.dismiss();
            }
            mainRequest.fileName = listAmazonS3Url;
            AppLog.e(TAG, "send data : " + new Gson().toJson(mainRequest));
          /*  if (isEdit) {
                manager.addChapterTopicPost(this, group_id, team_id, subject_id, chapter_id, mainRequest);
            } else {*/
                manager.addHwPost(this, group_id, team_id, subject_id, mainRequest);
//            }

        } else {
            final String key = AmazoneHelper.getAmazonS3Key(mainRequest.fileType);
            TransferObserver observer ;
            UploadOptions option = UploadOptions.
                    builder().bucket(AmazoneHelper.BUCKET_NAME).
                    cannedAcl(CannedAccessControlList.PublicRead).build();
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
                            Toast.makeText(AddHwPostActivity.this, getResources().getString(R.string.toast_upload_failed), Toast.LENGTH_SHORT).show();
                            if(progressDialog!=null)
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
                        hideLoadingBar();
                        //       progressBar.setVisibility(View.GONE);
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        AppLog.e(TAG, "Upload Error : " + ex);
                        Toast.makeText(AddHwPostActivity.this, getResources().getString(R.string.image_upload_error), Toast.LENGTH_SHORT).show();
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


    public boolean isValid(boolean showToast) {
        boolean valid = true;

        Log.e("edtDesc : ", edtTitle.getText().toString());
        Log.e("videoUrl : ", videoUrl);
        Log.e("image paths : ", listImages.toString());
        Log.e("videoType : ", fileTypeImageOrVideo + "");

        if (!isValueValidOnly(edtTitle)) {
            if (showToast)
                Toast.makeText(this, getResources().getString(R.string.toast_enter_topic_name), Toast.LENGTH_SHORT).show();
            return false;
        }
      /*  if (!isValueValidOnly(edtDesc)) {
            if (showToast)
                Toast.makeText(this, "Please Enter Description", Toast.LENGTH_SHORT).show();
            return false;
        } if (!isValueValidOnly(et_date)) {
            if (showToast)
                Toast.makeText(this, "Please Select Date", Toast.LENGTH_SHORT).show();
            return false;
        }*/

       /* if (listImages.size() == 0 && TextUtils.isEmpty(videoUrl) && TextUtils.isEmpty(pdfPath)) {
            if (showToast)
                Toast.makeText(this, "Please Add Image or video or pdf", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (!TextUtils.isEmpty(videoUrl) && listImages.size() > 0) {
            valid = false;
            removeImage();
            removePdf();
            Toast.makeText(this, "" + getResources().getString(R.string.msg_upload2), Toast.LENGTH_SHORT).show();
        }*/
        AppLog.e(TAG, "valid : " + valid);
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
            hideLoadingBar();
        //       progressBar.setVisibility(View.GONE);
        switch (apiId) {

            case LeafManager.API_HW_ADD:
                Toast.makeText(AddHwPostActivity.this, getResources().getString(R.string.toast_successfully_posted), Toast.LENGTH_SHORT).show();
               /* if (isEdit) {
                    LeafPreference.getInstance(AddHwPostActivity.this).setBoolean("is_topic_added", true);
                    new SendNotification(edtTitle.getText().toString(), false).execute();
                } else {*/
                    LeafPreference.getInstance(AddHwPostActivity.this).setBoolean("is_hw_added", true);
                    new SendNotification(subject_name+" ("+className+")").execute();
//                }
                finish();
                break;
        }

    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<AddPostValidationError> error) {
      //  btnShare.setEnabled(true);
        if (progressBar != null)
            hideLoadingBar();
        //       progressBar.setVisibility(View.GONE);
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
            } else if (error.errors.get(0).video != null) {
                Toast.makeText(this, error.errors.get(0).video, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, error.title, Toast.LENGTH_SHORT).show();
            }
        }


    }


    @Override
    public void onException(int apiId, String error) {
     //   btnShare.setEnabled(true);
        if (progressBar != null)
            hideLoadingBar();
        //       progressBar.setVisibility(View.GONE);
        Toast.makeText(AddHwPostActivity.this, error, Toast.LENGTH_SHORT).show();

    }


    public void showPhotoDialog(int resId) {
        SMBDialogUtils.showSMBSingleChoiceDialog(AddHwPostActivity.this,
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
            Toast.makeText(this, getResources().getString(R.string.toast_storage_permission_needed), Toast.LENGTH_LONG).show();
        } else {
            AppLog.e(TAG, "requestPermissionForWriteExternal");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
        }
    }
    private void showCropDialog(Uri imageCapture,boolean isCamera) {

        Log.e(TAG,"imageGEt "+imageCapture);
        Intent i = new Intent(getApplicationContext(), CropDialogActivity.class);
        i.putExtra("path",String.valueOf(imageCapture));
        i.putExtra("isCamera",isCamera);
        startActivityForResult(i,10);


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (requestCode == 10)
        {
            String uri = data.getStringExtra("Data");
            Log.e(TAG,"uri"+ uri);


            fileTypeImageOrVideo = Constants.FILE_TYPE_IMAGE;
            listImages.add(uri);

            showLastImage();
            removePdf();


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
            listImages.clear();
            if (clipData == null) {

                isGalleryMultiple = false;
//                String path = ImageUtil.getPath(this, selectedImage);
                //  listImages.add(selectedImage.toString());
                showCropDialog(selectedImage,false);
            } else {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    final Uri uri1 = item.getUri();
//                    String path = ImageUtil.getPath(this, uri1);
                    //    listImages.add(uri1.toString());
                    isGalleryMultiple = true;
                    showCropDialog(uri1,false);
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

            listImages.clear();

            showCropDialog(imageCaptureFile,true);

        }

        else if (requestCode == REQUEST_LOAD_VIDEO && resultCode == Activity.RESULT_OK) {
            listImages.clear();
            fileTypeImageOrVideo = Constants.FILE_TYPE_VIDEO;
            final Uri selectedImage = data.getData();
            AppLog.e(TAG, "selectedVideo : " + selectedImage);
            ClipData clipData = data.getClipData();
            if (clipData == null) {
//                String path = ImageUtil.getPath(this, selectedImage);
                listImages.add(selectedImage.toString());
                AddHwPostActivity.this.getContentResolver().takePersistableUriPermission(
                        selectedImage, FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    final Uri uri1 = item.getUri();
//                    String path = ImageUtil.getPath(this, uri1);
                    listImages.add(uri1.toString());
                    AddHwPostActivity.this.getContentResolver().takePersistableUriPermission(
                            uri1, FLAG_GRANT_READ_URI_PERMISSION);
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
                    Toast.makeText(getApplicationContext(),  getResources().getString(R.string.toast_select_pdf), Toast.LENGTH_SHORT).show();
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
            btn_cancel.setText(getResources().getString(R.string.lbl_remove));

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoUrl = edt_link.getText().toString();
                if (videoUrl.equals(""))
                    Toast.makeText(AddHwPostActivity.this, getResources().getString(R.string.lbl_enter_youtube_link), Toast.LENGTH_SHORT).show();
                else {
                    String videoId = "";
                    videoId = extractYoutubeId(videoUrl);

                    if (GroupDashboardActivityNew.selectedUrl.equals(videoUrl))
                        videoId = GroupDashboardActivityNew.selectedYoutubeId;

                    Log.e("VideoId is->", "" + videoId);

                    String img_url = "http://img.youtube.com/vi/" + videoId + "/0.jpg"; // this is link which will give u thumnail image of that video

                    Log.e("img_url is->", "" + img_url);

                    Picasso.with(AddHwPostActivity.this)
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
                                    Toast.makeText(AddHwPostActivity.this, getResources().getString(R.string.toast_valid_youtube_link), Toast.LENGTH_SHORT).show();
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

                startActivity(new Intent(AddHwPostActivity.this, MainActivity.class));
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

        private final String chapterName;
        private String server_response;

        public SendNotification(String chapterName) {
            this.chapterName = chapterName;
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

                        message = "New Homework added to " + chapterName;

                    topic = group_id + "_" + team_id;
                    object.put("to", "/topics/" + topic);

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title", title);
                    notificationObj.put("body", message);
                    object.put("notification", notificationObj);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("groupId", group_id);
                    dataObj.put("createdById", LeafPreference.getInstance(AddHwPostActivity.this).getString(LeafPreference.LOGIN_ID));
                    dataObj.put("postId", "");
                    dataObj.put("teamId", team_id);
                    dataObj.put("title", title);
                    dataObj.put("postType", "homework");
                    dataObj.put("Notification_type", "homework");
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
