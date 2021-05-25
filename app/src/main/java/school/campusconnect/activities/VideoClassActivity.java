package school.campusconnect.activities;

import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.hbisoft.hbrecorder.HBRecorder;
import com.hbisoft.hbrecorder.HBRecorderListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.VideoClassListFragment;
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

    }

    @Override
    public void HBRecorderOnComplete() {

    }

    @Override
    public void HBRecorderOnError(int errorCode, String reason) {

    }

    public void startRecordingScreen() {
        AppLog.e(TAG , "StartRecordingScreen called ");
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent permissionIntent = mediaProjectionManager != null ? mediaProjectionManager.createScreenCaptureIntent() : null;
        startActivityForResult(permissionIntent, SCREEN_RECORD_REQUEST_CODE);
    }

    public void stopRecording()
    {
        if(hbRecorder !=null)
            hbRecorder.stopScreenRecording();

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

                //Start screen recording
                hbRecorder.setAudioSource("DEFAULT");
                hbRecorder.setOutputPath(Environment.getExternalStorageDirectory().getPath());
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
}
