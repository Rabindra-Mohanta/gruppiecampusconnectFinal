package school.campusconnect.utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.BaseActivity;
import school.campusconnect.databinding.ActivityRecordAudioBinding;

public class RecordAudioActivity extends BaseActivity {

    public static String TAG = "RecordAudioActivity";

ActivityRecordAudioBinding binding;
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    Boolean isAudio = true;
    private Handler customHandler = new Handler();
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    long secs;
    long mins;
    MediaRecorder mediaRecorder;
    private File fileRecordAudio;
    private Uri uriRecordAudio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_record_audio);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(false);
        setTitle(getResources().getString(R.string.lbl_RecordAudio));
        if (checkPermissionForWriteExternal())
        {
            MediaRecorderReady();
            inits();
        }
        else
        {
            requestPermissionForWriteExternal(1);
        }

    }

    private void inits() {

        binding.BtnRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAudio)
                {
                    isAudio = false;
                    binding.BtnRecording.setImageDrawable(getResources().getDrawable(R.drawable.ic__stop_audio));
                    startAudio();
                }
                else
                {
                    isAudio = true;
                    binding.BtnRecording.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_audio));
                    stopAudio();
                }
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isAudio)
                {

                    String uri = Uri.fromFile(fileRecordAudio).toString();
                    Log.e(TAG,"uri "+uri);
                    Intent i = new Intent();
                    i.putExtra("AudioData",uri);
                    setResult(RESULT_OK,i);
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_stop_audio),Toast.LENGTH_SHORT).show();
                }
            }
        });
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
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MediaRecorderReady();
                    inits();
                    Log.e("AddPost" + "permission", "granted camera");
                } else {
                    Log.e("AddPost" + "permission", "denied camera");
                }
                break;

        }
    }
    public void MediaRecorderReady(){

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
             /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                }
                else
                {
                    mediaRecorder.setOutputFile(fileRecordAudio.getAbsolutePath());
                }*/
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
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);
        mediaRecorder.stop();
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            secs = (int) (updatedTime / 1000);
            mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            binding.tvTimer.setText(String.format("%02d", mins) + ":"
                    + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);
        }

    };

    @Override
    public void onBackPressed() {
    //    super.onBackPressed();

        if (isAudio)
        {
            Intent i = new Intent();
            setResult(RESULT_CANCELED,i);
            finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_stop_audio),Toast.LENGTH_SHORT).show();
        }

    }
}