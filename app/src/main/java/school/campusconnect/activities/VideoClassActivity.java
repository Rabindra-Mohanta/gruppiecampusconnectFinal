package school.campusconnect.activities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hbisoft.hbrecorder.HBRecorder;
import com.hbisoft.hbrecorder.HBRecorderListener;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.VideoOfflineObject;
import school.campusconnect.datamodel.videocall.VideoClassResponse;
import school.campusconnect.fragments.VideoClassListFragment;
import school.campusconnect.utils.AppDialog;
import school.campusconnect.utils.AppLog;

public class VideoClassActivity extends BaseActivity  implements HBRecorderListener {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;

    HBRecorder hbRecorder;

    public static int SCREEN_RECORD_REQUEST_CODE = 321;
    public static String TAG = VideoClassActivity.class.getName();

    VideoClassListFragment classListFragment=new VideoClassListFragment();

    Intent recorderIntent ;
    int resultcode;
    private VideoClassResponse.ClassData selectedClassData;

    Uri mUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getIntent().getStringExtra("title"));

    //    VideoClassListFragment classListFragment=new VideoClassListFragment();
        classListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,classListFragment).commit();


        //Init HBRecorder
        hbRecorder = new HBRecorder(this, this);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void HBRecorderOnStart() {
        AppLog.e(TAG,"AAA HBRecorderOnStart()");
    }

    @Override
    public void HBRecorderOnComplete() {
        AppLog.e(TAG,"AAA HBRecorderOnComplete()");

        if(selectedClassData==null){
            return;
        }
        AppDialog.showConfirmDialog(this, "Do you want to share this live class?", new AppDialog.AppDialogListener() {
            @Override
            public void okPositiveClick(DialogInterface dialog)
            {
                dialog.dismiss();
                Intent intent = new Intent(VideoClassActivity.this, RecClassSubjectActivity.class);
                intent.putExtra("team_id",selectedClassData.getId());
                intent.putExtra("title",selectedClassData.className);

                intent.putExtra("path",hbRecorder.getFilePath());
                startActivity(intent);
            }

            @Override
            public void okCancelClick(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void HBRecorderOnError(int errorCode, String reason) {
        AppLog.e(TAG,"AAA HBRecorderOnError() : "+reason);
    }

    public void startRecordingScreen(VideoClassResponse.ClassData selectedClassData) {
        this.selectedClassData = selectedClassData;
        AppLog.e(TAG , "StartRecordingScreen called ");
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent permissionIntent = mediaProjectionManager != null ? mediaProjectionManager.createScreenCaptureIntent() : null;
        startActivityForResult(permissionIntent, SCREEN_RECORD_REQUEST_CODE);
    }

    public void stopRecording()
    {
        if(hbRecorder !=null && selectedClassData!=null)
        {
            hbRecorder.stopScreenRecording();
            AppLog.e(TAG,"hbRecorder.getFilePath() : "+hbRecorder.getFilePath());
          //  AppLog.e(TAG,"mUri : "+mUri.toString());

            AppLog.e(TAG,"hbRecorder.getFileName() : "+hbRecorder.getFileName());
            AppLog.e(TAG,"selected ClassData : "+selectedClassData.getId());

            if(mUri !=null)
            saveVideoNameOffline(hbRecorder.getFileName() ,mUri.toString());
            else
            saveVideoNameOffline(hbRecorder.getFileName() ,hbRecorder.getFilePath());

        }
    }

    public void saveVideoNameOffline(String fileName, String filePath)
    {
        VideoOfflineObject offlineObject = new VideoOfflineObject();
        offlineObject.setVideo_filename(fileName);
        offlineObject.setVideo_filepath(filePath);
        offlineObject.setVideo_date(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        LeafPreference preference = LeafPreference.getInstance(VideoClassActivity.this);

        if(!preference.getString(LeafPreference.OFFLINE_VIDEONAMES).equalsIgnoreCase(""))
        {
            ArrayList<VideoOfflineObject> offlineObjects = new Gson().fromJson(preference.getString(LeafPreference.OFFLINE_VIDEONAMES), new TypeToken<ArrayList<VideoOfflineObject>>() {
            }.getType());

            offlineObjects.add(offlineObject);
            preference.setString(LeafPreference.OFFLINE_VIDEONAMES , new Gson().toJson(offlineObjects));

        }
        else
        {
            ArrayList<VideoOfflineObject> offlineObjects = new ArrayList<>();
            offlineObjects.add(offlineObject);
            preference.setString(LeafPreference.OFFLINE_VIDEONAMES , new Gson().toJson(offlineObjects));
        }

    }


    public void startRecording()
    {
        if(hbRecorder == null)
        {
            return;
        }
        //Start screen recording
        hbRecorder.setAudioSource("DEFAULT");
        hbRecorder.setOutputPath(Environment.getExternalStorageDirectory().getPath());
        if(recorderIntent !=null)
            hbRecorder.startScreenRecording(recorderIntent ,resultcode, this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        AppLog.e(TAG , "onActivityResult called ");
        if (requestCode == SCREEN_RECORD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                AppLog.e(TAG , "startScreenRecording called ");

                resultcode = resultCode;
                recorderIntent = data;

                String path = "";

              /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                {
                    ContentResolver resolver;
                    ContentValues contentValues;
                    String filename = generateFileName();

                    resolver = getContentResolver();
                    contentValues = new ContentValues();
                    contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "Gruppie");
                    contentValues.put(MediaStore.Video.Media.TITLE, filename);
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
                    mUri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
                    //FILE NAME SHOULD BE THE SAME
                    hbRecorder.setFileName(filename);
                    hbRecorder.setOutputUri(mUri);
                }
                else
                {
                    createFolder();
                    hbRecorder.setOutputPath(Environment.getExternalStorageDirectory().getPath()+"/Gruppie");
                }*/

                //Start screen recording

                hbRecorder.enableCustomSettings();

                hbRecorder.setAudioSource("DEFAULT");
                hbRecorder.setVideoBitrate(900000);
                hbRecorder.setVideoEncoder("H264");
               // hbRecorder.setVideoFrameRate(24);



                hbRecorder.setOutputPath(Environment.getExternalStorageDirectory().getPath()+"/gruppie_videos");
                hbRecorder.setFileName("video_"+new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date()));



                File directory = new File(Environment.getExternalStorageDirectory().getPath()+"/gruppie_videos");
                if (! directory.exists()){
                    directory.mkdir();
                    // If you require it to make the entire directory path including parents,
                    // use directory.mkdirs(); here instead.
                }

                AppLog.e(TAG , "Recorder OutputPath : "+Environment.getExternalStorageDirectory().getPath()+"/gruppie_videos");
                hbRecorder.startScreenRecording(data ,resultCode, this);

                if(classListFragment !=null)
                    classListFragment.startMeetingFromActivity();

            }
            else
            {
                if(classListFragment !=null)
                    classListFragment.startMeetingFromActivity();

            }
        }
    }

    //Generate a timestamp to be used as a file name
    private String generateFileName() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate).replace(" ", "");
    }


    private void createFolder()
    {
        File f1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "HBRecorder");
        if (!f1.exists()) {
            if (f1.mkdirs()) {
                AppLog.e(TAG, "folder created : "+f1.getAbsolutePath());
            }
        }
    }

}
