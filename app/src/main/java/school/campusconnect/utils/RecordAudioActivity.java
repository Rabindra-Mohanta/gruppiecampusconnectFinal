package school.campusconnect.utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
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
    private String fileRecordAudio;
    private Uri uriRecordAudio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_record_audio);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(false);
        setTitle(getResources().getString(R.string.lbl_RecordAudio));
        MediaRecorderReady();
        inits();
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
                    Log.e(TAG,"uri "+fileRecordAudio);
                    Intent i = new Intent();
                    i.putExtra("AudioData",fileRecordAudio);
                    setResult(RESULT_OK,i);
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Stop Audio Recording...",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void MediaRecorderReady(){
        fileRecordAudio = ImageUtil.getOutputMediaAudio().getAbsolutePath();
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(fileRecordAudio);
    }

    private void startAudio() {


        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
            Toast.makeText(getApplicationContext(),"Stop Audio Recording...",Toast.LENGTH_SHORT).show();
        }

    }
}