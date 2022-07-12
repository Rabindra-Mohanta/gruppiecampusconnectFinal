package school.campusconnect.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
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
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
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
import school.campusconnect.fragments.ChapterListFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneHelper;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BackgroundVideoUploadChapterService;
import school.campusconnect.utils.BackgroundVideoUploadService;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.GetThumbnail;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.RecordAudioActivity;
import school.campusconnect.utils.crop.CropDialogActivity;
import school.campusconnect.utils.youtube.MainActivity;
import school.campusconnect.views.SMBDialogUtils;

import static android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;


public class AddChapterPostActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<AddPostValidationError>, View.OnClickListener, UploadImageAdapter.UploadImageListener {

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

    @Bind(R.id.imgAddChapter)
    ImageView imgAddChapter;

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

    @Bind(R.id.btnCancel)
    Button btnCancel;

    @Bind(R.id.rvImages)
    RecyclerView rvImages;

    @Bind(R.id.spChapter)
    Spinner spChapter;

    @Bind(R.id.cardChapterName)
    CardView cardChapterName;

    @Bind(R.id.llTop)
    LinearLayout llTop;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;

    @Bind(R.id.iconBack)
    public ImageView iconBack;

    @Bind(R.id.imgHome)
    public ImageView imgHome;





    //audio


    @Bind(R.id.llAudioPreview)
    RelativeLayout llAudioPreview;

    @Bind(R.id.llAudioTimer)
    RelativeLayout llAudioTimer;

    @Bind(R.id.llAudio)
    LinearLayout llAudio;

    @Bind(R.id.imgAudio)
    ImageView imgAudio;

    @Bind(R.id.imgDeleteAudio)
    ImageView imgDeleteAudio;


    @Bind(R.id.imgPlayAudio)
    ImageView imgPlayAudio;

    @Bind(R.id.imgPauseAudio)
    ImageView imgPauseAudio;


    @Bind(R.id.tvTimeAudio)
    TextView tvTimeAudio;

    @Bind(R.id.tvTimeTotalAudio)
    TextView tvTimeTotalAudio;

    @Bind(R.id.seekBarAudio)
    SeekBar seekBarAudio;

    @Bind(R.id.tvAudioTimer)
    TextView tvTimer;

    private String audioPath = "";
    MediaPlayer mediaPlayer  = new MediaPlayer();
    public static final int REQUEST_LOAD_RECORD_AUDIO = 107;
    public static final int REQUEST_LOAD_AUDIO = 106;
    private Boolean isPause = false;
    //end audio

    TextView btn_ok;
    TextView btn_cancel;
    TextView btn_upload;

    EditText edt_link;

    Dialog dialog;

    String group_id;
    String team_id;
    String subject_id;
    String subject_name;

    String chapterId;
    boolean canDelete;
    public Uri imageCaptureFile;

    private Boolean isGalleryMultiple = false;
    private Boolean isClear = true;

    public static final int REQUEST_LOAD_CAMERA_IMAGE = 101;
    public static final int REQUEST_LOAD_GALLERY_IMAGE = 102;
    public static final int REQUEST_LOAD_PDF = 103;
    public static final int REQUEST_LOAD_VIDEO = 104;
    public static final int REQUEST_RECORD_VIDEO = 105;


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
    private ProgressDialog progressDialog;
    private boolean isEdit = true;
    private String chapter_id;
    private ArrayList<ChapterRes.ChapterData> chapterList;
    private String sharePath;




    private Handler mHandler = new Handler();
    Runnable myRunnable = new Runnable() {
        @Override
        public void run() {

            try{
                int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                Log.e(TAG,"mCurrentPosition"+ mCurrentPosition);
                seekBarAudio.setProgress(mCurrentPosition);
                tvTimeAudio.setText(formatDate(mCurrentPosition));
                tvTimeTotalAudio.setText(formatDate(mediaPlayer.getDuration()/1000));
                if(mediaPlayer.isPlaying())
                    mHandler.postDelayed(myRunnable, 1000);
            }catch (Exception e)
            {
                Log.e(TAG,"exception"+ e.getMessage());
            }

        }
    };




    /*record audio*/
    private Handler customHandler = new Handler();
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    long secs = 0L;
    long mins  = 0L;
    MediaRecorder mediaRecorder;
    private File fileRecordAudio = null;


    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            secs = (int) (updatedTime / 1000);
            mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            tvTimer.setText(String.format("%02d", mins) + ":"
                    + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);
        }

    };


    private String formatDate(int second)  {

        String seconds , minutes;
        if(second>60)
        {
            if(second % 60 < 10)
                seconds = "0"+(second % 60);
            else
                seconds = ""+(second%60);

            if(second/60 < 10)
                minutes = "0"+second/60;
            else
                minutes = ""+second/60;
        }
        else
        {
            minutes = "00";
            if(second % 60 < 10)
                seconds = "0"+(second % 60);
            else
                seconds = ""+(second%60);
        }
        return minutes+":"+seconds;
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chapter_post);
        ButterKnife.bind(this);

        init();

        setListener();

        getChapters();

        if(!TextUtils.isEmpty(sharePath)){
            fileTypeImageOrVideo = Constants.FILE_TYPE_VIDEO;
            listImages.add(sharePath);
            showLastImage();
        }

        ArrayList<String> shareList = LeafApplication.getInstance().getShareFileList();
        if(shareList!=null && shareList.size()>0){

            SMBDialogUtils.showSMBDialogYesNoCancel(this, getResources().getString(R.string.smb_attach_file), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    String fileType = LeafApplication.getInstance().getType();
                    if(Constants.FILE_TYPE_IMAGE.equalsIgnoreCase(fileType)
                            || Constants.FILE_TYPE_VIDEO.equalsIgnoreCase(fileType)){
                        listImages.addAll(shareList);
                        fileTypeImageOrVideo = fileType;
                        showLastImage();
                    }else if(Constants.FILE_TYPE_PDF.equalsIgnoreCase(fileType)){
                        pdfPath = shareList.get(0);
                        Picasso.with(AddChapterPostActivity.this).load(R.drawable.pdf_thumbnail).into(imgDoc);
                    }
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (canDelete) {
            getMenuInflater().inflate(R.menu.menu_chapter, menu);
            menu.findItem(R.id.menu_add_post).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_delete_chapter) {

            if (imgAddChapter.getVisibility() == View.VISIBLE)
            {
                SMBDialogUtils.showSMBDialogOKCancel(this, getResources().getString(R.string.smb_delete_chapter), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isConnectionAvailable()) {
                            showLoadingBar(progressBar);
                            //   progressBar.setVisibility(View.VISIBLE);
                            LeafManager manager = new LeafManager();
                            manager.deleteChapter(AddChapterPostActivity.this, GroupDashboardActivityNew.groupId, team_id, subject_id, chapter_id);
                        } else {
                            showNoNetworkMsg();
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_please_select_chapter),Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void getChapters() {

        showLoadingBar(progressBar,false);
      //  progressBar.setVisibility(View.VISIBLE);
        LeafManager leafManager = new LeafManager();
        leafManager.getChapterList(this, GroupDashboardActivityNew.groupId, team_id, subject_id);
    }

    private void setListener() {
        llImage.setOnClickListener(this);
        llVideo.setOnClickListener(this);
        llYoutubeLink.setOnClickListener(this);
        llDoc.setOnClickListener(this);
        llAudio.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        imgAddChapter.setOnClickListener(this);

        //btnShare.setEnabled(false);

        if (checkPermissionForWriteExternal()) {

        }
        else {
            requestPermissionForWriteExternal(24);
        }


        if(checkPermissionForAudio())
        {

        }
        else {
            requestPermissionForRecordAudio(254);
        }


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



        imgDeleteAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mediaPlayer.isPlaying())
                {
                    mHandler.removeCallbacks(myRunnable);
                    mediaPlayer.pause();
                }

                tvTimer.setText("00:00");
                tvTimeTotalAudio.setText("00:00");
                tvTimeAudio.setText("00:00");
                mHandler.removeCallbacks(myRunnable);
                imgPauseAudio.setVisibility(View.GONE);
                imgPlayAudio.setVisibility(View.VISIBLE);
                seekBarAudio.setProgress(0);
                tvTimeAudio.setText("00:00");
                tvTimeTotalAudio.setText("00:00");
                isPause = false;
                startTime = 0L;
                timeInMilliseconds = 0L;
                timeSwapBuff = 0L;
                updatedTime = 0L;
                secs = 0L;
                mins  = 0L;
                removePdf();
                removeImage();
                llAudioPreview.setVisibility(View.GONE);
            }
        });

        imgAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.e(TAG,"onTouch "+event.getAction());

                Log.e(TAG,"onTouch Y"+ event.getY());

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    if (checkPermissionForWriteExternal()) {


                        MediaRecorderReady();
                    }
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {

                    Log.e(TAG,"onTouch Y ACTION_UP"+ event.getY());

                    if (checkPermissionForWriteExternal()) {

                        String uri = Uri.fromFile(fileRecordAudio).toString();
                        Log.e(TAG,"uri "+uri);
                        audioPath = uri;
                        listImages.clear();
                        fileTypeImageOrVideo = Constants.FILE_TYPE_AUDIO;
                        listImages.add(audioPath);
                        showLastImage();
                        removePdf();
                        removeImage();
                        stopAudio();
                        llAudioPreview.setVisibility(View.VISIBLE);
                    }
                    return true;
                }
                return false;
            }
        });




        imgPlayAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (isPause)
                {
                    isPause = false;
                    mediaPlayer.start();
                    imgPauseAudio.setVisibility(View.VISIBLE);
                    imgPlayAudio.setVisibility(View.GONE);
                    mHandler.post(myRunnable);
                }
                else
                {
                    if (mediaPlayer != null)
                    {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }

                    try {
                        mediaPlayer.setDataSource(audioPath);
                        mediaPlayer.prepare();
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                AppLog.e(TAG  , "ONPrepared : lenght : "+mp.getDuration());
                                seekBarAudio.setMax(mediaPlayer.getDuration()/1000);
                                mediaPlayer.start();
                                mHandler.post(myRunnable);
                            }
                        });
                        //  mediaPlayer.start();

                        imgPauseAudio.setVisibility(View.VISIBLE);
                        imgPlayAudio.setVisibility(View.GONE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        });

        imgPauseAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    isPause = true;
                    mHandler.removeCallbacks(myRunnable);
                    mediaPlayer.pause();
                    imgPauseAudio.setVisibility(View.GONE);
                    imgPlayAudio.setVisibility(View.VISIBLE);
                }catch(Exception e){
                    e.printStackTrace();
                    Log.e(TAG,"Exception"+e.getMessage());
                }
            }
        });


        seekBarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                Log.e(TAG,"progress "+progress + "\ngetDuration "+mediaPlayer.getDuration()/1000+" condition : "+(progress >= mediaPlayer.getDuration()/1000));


                if (progress >= mediaPlayer.getDuration()/1000)
                {
                    mHandler.removeCallbacks(myRunnable);
                    imgPauseAudio.setVisibility(View.GONE);
                    imgPlayAudio.setVisibility(View.VISIBLE);

                    seekBarAudio.setProgress(0);
                    tvTimeAudio.setText("00:00");
                    tvTimeTotalAudio.setText("00:00");
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        seekBarAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


    }

    private void init() {


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        group_id = GroupDashboardActivityNew.groupId;

        setSupportActionBar(mToolBar);
        transferUtility = AmazoneHelper.getTransferUtility(this);

        setBackEnabled(false);
        tvTitle.setText(getResources().getString(R.string.menu_add_chapter));
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
                Intent intent = new Intent(AddChapterPostActivity.this,GroupDashboardActivityNew.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        if (getIntent() != null) {
            group_id = getIntent().getStringExtra("group_id");
            team_id = getIntent().getStringExtra("team_id");
            subject_id = getIntent().getStringExtra("subject_id");
            subject_name = getIntent().getStringExtra("subject_name");
            sharePath = getIntent().getStringExtra("path");
            canDelete = getIntent().getBooleanExtra("isDelete",false);
            chapterId = getIntent().getStringExtra("chapter_id");
        }

        if (!canDelete)
        {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0,0,0);
            btnShare.setLayoutParams(layoutParams);
            addOrRemoveProperty(btnShare,RelativeLayout.ALIGN_PARENT_RIGHT,true);

        }
        rvImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new UploadImageAdapter(listImages, this);
        rvImages.setAdapter(imageAdapter);
    }

    private void addOrRemoveProperty(View view, int property, boolean flag){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        if(flag){
            layoutParams.addRule(property);
        }else {
            layoutParams.removeRule(property);
        }
        view.setLayoutParams(layoutParams);
    }



    private void shareButtonEnableDisable() {
  //      btnShare.setEnabled(isValid(false));
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
                //  progressBar.setVisibility(View.VISIBLE);
              //  btnShare.setEnabled(false);

                btnShare.setEnabled(false);
                btnShare.setTextColor(getResources().getColor(R.color.grey));
                mainRequest = new AddGalleryPostRequest();

                mainRequest.albumName = edtTitle.getText().toString();
                mainRequest.topicName = edtDesc.getText().toString();

                if (!TextUtils.isEmpty(videoUrl)) {
                    mainRequest.video = videoUrl;
                    mainRequest.fileType = Constants.FILE_TYPE_YOUTUBE;

                    Log.e(TAG, "send data " + new Gson().toJson(mainRequest));

                    if (isEdit) {

                        mainRequest.fileType = Constants.FILE_TYPE_YOUTUBE;
                        mainRequest.fileName = new ArrayList<>();
                        mainRequest.fileName.add(videoUrl);

                        manager.addChapterTopicPost(this, group_id, team_id, subject_id, chapter_id, mainRequest);
                    } else {
                        manager.addChapterPost(this, group_id, team_id, subject_id, mainRequest);
                    }

                } else if (!TextUtils.isEmpty(pdfPath)) {
                    mainRequest.fileType = Constants.FILE_TYPE_PDF;
                    progressDialog.setMessage("Preparing Pdf...");
                    progressDialog.show();
                    uploadToAmazone(mainRequest);

                }  else if (!TextUtils.isEmpty(audioPath)) {
                    mainRequest.fileType = Constants.FILE_TYPE_AUDIO;
                    progressDialog.setMessage("Preparing Audio...");
                    progressDialog.show();
                    uploadToAmazone(mainRequest);
                }

                else if (listImages.size() > 0 && Constants.FILE_TYPE_VIDEO.equals(fileTypeImageOrVideo)) {
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
            }
        } else {
            showNoNetworkMsg();
        }

    }
    public void startService() {

        Intent serviceIntent = new Intent(this, BackgroundVideoUploadChapterService.class);
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

        Toast.makeText(this, getResources().getString(R.string.toast_video_upload_background), Toast.LENGTH_SHORT).show();
        finish();

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
                        listImages.set(i, SiliCompressor.with(AddChapterPostActivity.this).compressVideo(listImages.get(i), getExternalCacheDir().getAbsolutePath() , w ,h , 950000));
                        Log.e(TAG, "compressPath : " + videoUrl);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(AddChapterPostActivity.this, getResources().getString(R.string.toast_error_comression) + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    if (listThumbnails != null) {
                        uploadThumbnail(listThumbnails, 0);
                    } else {
                        Toast.makeText(AddChapterPostActivity.this, getResources().getString(R.string.toast_upload_failed), Toast.LENGTH_SHORT).show();
                    }

                }
            }, Constants.FILE_TYPE_PDF);
        }
        else if (request.fileType.equals(Constants.FILE_TYPE_VIDEO)) {
            AppLog.e(TAG, "Final videos :: " + listImages.toString());
            GetThumbnail.create(listImages, new GetThumbnail.GetThumbnailListener() {
                @Override
                public void onThumbnail(ArrayList<String> listThumbnails) {
                    if (listThumbnails != null) {
                        uploadThumbnail(listThumbnails, 0);
                    } else {
                        Toast.makeText(AddChapterPostActivity.this,getResources().getString(R.string.toast_upload_failed), Toast.LENGTH_SHORT).show();
                    }

                }
            }, Constants.FILE_TYPE_VIDEO);
        }
        else if (request.fileType.equals(Constants.FILE_TYPE_AUDIO)){
            listImages.clear();
            listImages.add(audioPath);
            upLoadImageOnCloud(0);
        }
        else {
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
                           // progressBar.setVisibility(View.GONE);
                            if (progressDialog!=null) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(AddChapterPostActivity.this, getResources().getString(R.string.toast_upload_failed), Toast.LENGTH_SHORT).show();
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
                        // progressBar.setVisibility(View.GONE);
                        if (progressDialog!=null) {
                            progressDialog.dismiss();
                        }
                        AppLog.e(TAG, "Upload Error : " + ex);
                        Toast.makeText(AddChapterPostActivity.this, getResources().getString(R.string.image_upload_error), Toast.LENGTH_SHORT).show();
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
            if (isEdit) {
                manager.addChapterTopicPost(this, group_id, team_id, subject_id, chapter_id, mainRequest);
            } else {
                manager.addChapterPost(this, group_id, team_id, subject_id, mainRequest);
            }

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
                            Toast.makeText(AddChapterPostActivity.this, getResources().getString(R.string.toast_upload_failed), Toast.LENGTH_SHORT).show();
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
                        else if (Constants.FILE_TYPE_AUDIO.equals(mainRequest.fileType)) {
                            progressDialog.setMessage("Uploading Audio " + percentDone + "% " + (pos + 1) + " out of " + listImages.size() + ", please wait...");
                        }

                        AppLog.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                                + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        hideLoadingBar();
                        // progressBar.setVisibility(View.GONE);
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        AppLog.e(TAG, "Upload Error : " + ex);
                        Toast.makeText(AddChapterPostActivity.this, getResources().getString(R.string.image_upload_error), Toast.LENGTH_SHORT).show();
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
        Log.e("audioUrl : ", audioPath);
        Log.e("image paths : ", listImages.toString());
        Log.e("videoType : ", fileTypeImageOrVideo + "");

        if (cardChapterName.getVisibility() == View.VISIBLE)
        {
            if (!isValueValidOnly(edtTitle)) {
                if (showToast)
                    Toast.makeText(this, getResources().getString(R.string.toast_enter_chapter_name), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (!isValueValidOnly(edtDesc)) {
            if (showToast)
                Toast.makeText(this, getResources().getString(R.string.toast_enter_topic_name), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (listImages.size() == 0 && TextUtils.isEmpty(videoUrl) && TextUtils.isEmpty(pdfPath) && TextUtils.isEmpty(audioPath)) {
            if (showToast)
                Toast.makeText(this, getResources().getString(R.string.toast_add_image_or_video_or_audio), Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (!TextUtils.isEmpty(videoUrl) && listImages.size() > 0) {
            valid = false;
            removeImage();
            removePdf();
            removeAudio();
            Toast.makeText(this, "" + getResources().getString(R.string.msg_upload2), Toast.LENGTH_SHORT).show();
        }
        AppLog.e(TAG, "valid : " + valid);
        return valid;
    }


    public void onClick(View v) {
        hide_keyboard();
        switch (v.getId()) {
            case R.id.btnShare:
                addPost();
                break;

            case R.id.imgAddChapter:
                imgAddChapter.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                llTop.setVisibility(View.GONE);
                edtTitle.setHint("");
                cardChapterName.setVisibility(View.VISIBLE);
                isEdit = false;
                break;

            case R.id.btnCancel:
                imgAddChapter.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.GONE);
                isEdit = true;
                llTop.setVisibility(View.VISIBLE);
                cardChapterName.setVisibility(View.GONE);
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

          /*  case R.id.llAudio:
                if (checkPermissionForWriteExternal()) {
                    showAudioDialog(R.array.array_audio);
                } else {
                    requestPermissionForWriteExternal(24);
                }
                break;*/
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
        Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        galleryIntent.setType("video/*");
        galleryIntent.setFlags(FLAG_GRANT_READ_URI_PERMISSION|FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Video"), REQUEST_LOAD_VIDEO);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        btnShare.setEnabled(true);
        btnShare.setTextColor(getResources().getColor(R.color.white));
        if (progressBar != null)
            hideLoadingBar();
        // progressBar.setVisibility(View.GONE);
        switch (apiId) {

            case LeafManager.API_CHAPTER_ADD:
                Toast.makeText(AddChapterPostActivity.this, getResources().getString(R.string.toast_posted_successfully), Toast.LENGTH_SHORT).show();
                if (isEdit) {
                    LeafPreference.getInstance(AddChapterPostActivity.this).setBoolean("is_topic_added", true);
                    new SendNotification(edtTitle.getText().toString(), false).execute();
                } else {
                    LeafPreference.getInstance(AddChapterPostActivity.this).setBoolean("is_chapter_added", true);
                    new SendNotification(edtTitle.getText().toString(), true).execute();
                }
                finish();
                break;

            case LeafManager.API_CHAPTER_LIST:
                ChapterRes res = (ChapterRes) response;
                chapterList = res.getData();
                AppLog.e(TAG, "ChapterRes " + chapterList);
                bindChapter();
                break;

            case LeafManager.API_CHAPTER_REMOVE:
                    LeafPreference.getInstance(this).setBoolean("is_chapter_added", true);
                    finish();
                    break;
        }

    }

    private void bindChapter() {

        if (chapterList != null && chapterList.size() > 0) {

            String[] strChapter = new String[chapterList.size()];
            for (int i=0;i<chapterList.size();i++){
                strChapter[i]=chapterList.get(i).chapterName;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_spinner,strChapter);
            spChapter.setAdapter(adapter);

            spChapter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    AppLog.e(TAG, "onItemSelected : " + position);
                    /*if(position==strChapter.length-1){
                        cardChapterName.setVisibility(View.VISIBLE);
                        edtTitle.setText("");
                        isEdit = false;
                    }else {


                    }*/

/*                    edtTitle.setText("");
                    edtTitle.setText(chapterList.get(position).chapterName);*/
                    chapter_id = chapterList.get(position).chapterId;
                   // cardChapterName.setVisibility(View.GONE);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
            edtTitle.setHint("");
            isEdit = false;
            llTop.setVisibility(View.GONE);
            cardChapterName.setVisibility(View.VISIBLE);
            imgAddChapter.setVisibility(View.GONE);
        }

    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<AddPostValidationError> error) {
        btnShare.setEnabled(true);
        btnShare.setTextColor(getResources().getColor(R.color.white));
        if (progressBar != null)
            hideLoadingBar();
        // progressBar.setVisibility(View.GONE);
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
       btnShare.setEnabled(true);
        btnShare.setTextColor(getResources().getColor(R.color.white));
        if (progressBar != null)
            hideLoadingBar();
        // progressBar.setVisibility(View.GONE);
        Toast.makeText(AddChapterPostActivity.this, error, Toast.LENGTH_SHORT).show();

    }


    public void showPhotoDialog(int resId) {
        SMBDialogUtils.showSMBSingleChoiceDialog(AddChapterPostActivity.this,
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


    public void showAudioDialog(int resId) {
        SMBDialogUtils.showSMBSingleChoiceDialog(AddChapterPostActivity.this,
                R.string.lbl_select_audio, resId, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lw = ((AlertDialog) dialog).getListView();
                        switch (lw.getCheckedItemPosition()) {
                            case 0:

                                break;
                            case 1:
                                selectAudioFromFile(REQUEST_LOAD_AUDIO);
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

    private void selectAudioFromFile(int requestCode)
    {
        if(checkPermissionForAudio())
        {
            Intent audioIntent = new Intent(Intent.ACTION_GET_CONTENT);
            audioIntent.setType("audio/*");
            audioIntent.setFlags(FLAG_GRANT_READ_URI_PERMISSION|FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            startActivityForResult(Intent.createChooser(audioIntent, "Select Audio"), requestCode);
        }
        else {
            requestPermissionForRecordAudio(25);
        }
    }

    private void startRecording(int requestCode) {

        if(checkPermissionForAudio())
        {
            Intent intent = new Intent(getApplicationContext(), RecordAudioActivity.class);
            startActivityForResult(intent, requestCode);
        }
        else {
            requestPermissionForRecordAudio(24);
        }
    }



    private void selectPdf(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION|FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
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
    private boolean checkPermissionForAudio() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (result == PackageManager.PERMISSION_GRANTED) {
            Log.e("External" + "permission", "checkpermission , granted");
            return true;
        } else {
            Log.e("External" + "permission", "checkpermission , denied");
            return false;
        }
    }

    public void requestPermissionForRecordAudio(int code) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(this, getResources().getString(R.string.toast_audio_permission_needed), Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, code);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, code);
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
            removeAudio();

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
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
            removeAudio();

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
               //     isGalleryMultiple = true;
                    showCropDialog(uri1,false);
                }
            }

        }



        else if (requestCode == REQUEST_LOAD_RECORD_AUDIO) {

            if (resultCode == Activity.RESULT_OK) {
                audioPath = data.getStringExtra("AudioData");
                Log.e(TAG,"audioPath"+ audioPath);

                listImages.clear();
                fileTypeImageOrVideo = Constants.FILE_TYPE_AUDIO;
                listImages.add(audioPath);

                llAudioPreview.setVisibility(View.VISIBLE);

                showLastImage();
                removePdf();
                removeImage();
            }
        }

        else if (requestCode == REQUEST_LOAD_AUDIO) {

            if (resultCode == Activity.RESULT_OK) {
                final Uri selectedAudio = data.getData();

                if (selectedAudio.toString().startsWith("content")) {
                    audioPath = ImageUtil.getPath(this, selectedAudio);
                } else {
                    audioPath = selectedAudio.getPath();
                }
                Log.e(TAG,"audioPath"+ audioPath);

                listImages.clear();
                fileTypeImageOrVideo = Constants.FILE_TYPE_AUDIO;
                listImages.add(audioPath);

                llAudioPreview.setVisibility(View.VISIBLE);


                removePdf();
                removeImage();

            }
        }

        else if (requestCode == REQUEST_LOAD_CAMERA_IMAGE && resultCode == Activity.RESULT_OK) {
            listImages.clear();
            /*fileTypeImageOrVideo = Constants.FILE_TYPE_IMAGE;*/
//            String path = cameraFile.getAbsolutePath();
            AppLog.e(TAG, "imageCaptureFile : " + imageCaptureFile);
  //          listImages.add(imageCaptureFile.toString());
            isGalleryMultiple = false;

         /*   showLastImage();
            removePdf();
            removeAudio();*/

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
                AddChapterPostActivity.this.getContentResolver().takePersistableUriPermission(selectedImage, FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    final Uri uri1 = item.getUri();
//                    String path = ImageUtil.getPath(this, uri1);
                    listImages.add(uri1.toString());
                    AddChapterPostActivity.this.getContentResolver().takePersistableUriPermission(
                            uri1, FLAG_GRANT_READ_URI_PERMISSION);
                }
            }


            showLastImage();
            removePdf();
            removeAudio();

        }

        else if (requestCode == REQUEST_LOAD_PDF) {
            if (resultCode == Activity.RESULT_OK) {
                pdfPath = data.getData().toString();
                Log.e("pdfUri : ", pdfPath);
               /* if (selectedImageURI.toString().startsWith("content")) {
                    pdfUri = ImageUtil.getPath(this, selectedImageURI);
                } else {
                    pdfUri = selectedImageURI.getPath();
                }
*/
                if (TextUtils.isEmpty(pdfPath)) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_select_pdf), Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.e("PDF", "imgUrl is " + pdfPath);

                if (!TextUtils.isEmpty(pdfPath))
                    Picasso.with(this).load(R.drawable.pdf_thumbnail).into(imgDoc);
                removeImage();
                removeAudio();
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

    private void removeAudio() {

        audioPath = "";
     //   Picasso.with(this).load(R.drawable.icon_doc).into(imgDoc);

       /* listImages.clear();
        Picasso.with(this).load(R.drawable.icon_gallery).into(img_image);
        showLastImage();
        shareButtonEnableDisable();*/
        llAudioPreview.setVisibility(View.GONE);
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


            case 254:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("AddPost" + "permission", "granted camera");
                } else {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_audio_permission_needed),Toast.LENGTH_SHORT).show();
                    Log.e("AddPost" + "permission", "denied camera");
                }
                break;


            case 24:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //  startRecording(REQUEST_LOAD_RECORD_AUDIO);
                    //MediaRecorderReady();
                    Log.e("AddPost" + "permission", "granted camera");
                } else {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_storage_permission_needed),Toast.LENGTH_SHORT).show();
                    Log.e("AddPost" + "permission", "denied camera");
                }
                break;

            case 26:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectAudioFromFile(REQUEST_LOAD_AUDIO);
                    Log.e("AddPost" + "permission", "granted camera");
                } else {
                    Log.e("AddPost" + "permission", "denied camera");
                }
                break;



            case 25:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectAudioFromFile(REQUEST_LOAD_AUDIO);
                    Log.e("AddPost" + "permission", "granted camera");
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
                    Toast.makeText(AddChapterPostActivity.this, getResources().getString(R.string.lbl_enter_youtube_link), Toast.LENGTH_SHORT).show();
                else {
                    String videoId = "";
                    videoId = extractYoutubeId(videoUrl);

                    if (GroupDashboardActivityNew.selectedUrl.equals(videoUrl))
                        videoId = GroupDashboardActivityNew.selectedYoutubeId;

                    Log.e("VideoId is->", "" + videoId);

                    String img_url = "http://img.youtube.com/vi/" + videoId + "/0.jpg"; // this is link which will give u thumnail image of that video

                    Log.e("img_url is->", "" + img_url);

                    Picasso.with(AddChapterPostActivity.this)
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
                                    Toast.makeText(AddChapterPostActivity.this, getResources().getString(R.string.toast_valid_youtube_link), Toast.LENGTH_SHORT).show();
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

                startActivity(new Intent(AddChapterPostActivity.this, MainActivity.class));
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
                    dataObj.put("createdById", LeafPreference.getInstance(AddChapterPostActivity.this).getString(LeafPreference.LOGIN_ID));
                    dataObj.put("postId", "");
                    dataObj.put("teamId", team_id);
                    dataObj.put("title", title);
                    dataObj.put("postType", isChapter ? "chapter" : "topic");
                    dataObj.put("Notification_type", "NOTES_VIDEO");
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






    /*record audio*/
    public void MediaRecorderReady(){

        if (fileRecordAudio != null)
        {

            if (mediaPlayer.isPlaying())
            {
                mHandler.removeCallbacks(myRunnable);
                mediaPlayer.pause();
            }

            fileRecordAudio.delete();
            mediaRecorder.reset();
            tvTimer.setText("00:00");
            tvTimeTotalAudio.setText("00:00");
            tvTimeAudio.setText("00:00");
            mHandler.removeCallbacks(myRunnable);
            imgPauseAudio.setVisibility(View.GONE);
            imgPlayAudio.setVisibility(View.VISIBLE);
            seekBarAudio.setProgress(0);
            tvTimeAudio.setText("00:00");
            tvTimeTotalAudio.setText("00:00");
            isPause = false;
            startTime = 0L;
            timeInMilliseconds = 0L;
            timeSwapBuff = 0L;
            updatedTime = 0L;
            secs = 0L;
            mins  = 0L;
            mediaRecorder.release();
        }
        try {
            fileRecordAudio = ImageUtil.getOutputMediaAudio(getApplicationContext());
            mediaRecorder=new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

            Log.e(TAG,"fileRecordAudio "+fileRecordAudio.getAbsolutePath());
            Log.e(TAG,"fileRecordAudio URL "+Uri.fromFile(fileRecordAudio));

            try {
                FileDescriptor fileDescriptor = getContentResolver().openFileDescriptor(Uri.fromFile(fileRecordAudio),"rwt").getFileDescriptor();
                mediaRecorder.setOutputFile(fileDescriptor);

                removePdf();
                removeImage();
                startAudio();


            } catch (FileNotFoundException e) {
                //e.printStackTrace();
                Log.e(TAG,"FileNotFoundException "+e.getMessage());
            }



        } catch(RuntimeException stopException) {
            // handle cleanup here
            Log.e(TAG,"RuntimeException "+stopException.getMessage());
        }

    }

    private void startAudio() {


        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            llAudioTimer.setVisibility(View.VISIBLE);
            llAudioPreview.setVisibility(View.GONE);
            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Log.e(TAG,"IllegalStateException "+e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"IOException "+e.getMessage());
        }
    }

    private void stopAudio() {
        llAudioTimer.setVisibility(View.GONE);
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);


        try {
            mediaRecorder.stop();
        } catch(RuntimeException stopException) {
            Log.e(TAG,"RuntimeException "+stopException.getMessage());
        }

    }
}
