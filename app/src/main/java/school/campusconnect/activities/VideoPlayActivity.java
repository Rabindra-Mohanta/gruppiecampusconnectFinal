package school.campusconnect.activities;

import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import school.campusconnect.R;
import school.campusconnect.utils.AmazoneVideoDownload;

public class VideoPlayActivity extends AppCompatActivity implements OnPreparedListener {

    VideoView playerView;
    AmazoneVideoDownload asyncTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        playerView = findViewById(R.id.video_view);
        View llProgress = findViewById(R.id.llProgress);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        ProgressBar progressBar1 = findViewById(R.id.progressBar1);
        progressBar1.setVisibility(View.VISIBLE);
        llProgress.setVisibility(View.VISIBLE);
        asyncTask = AmazoneVideoDownload.download(this, getIntent().getStringExtra("video"), new AmazoneVideoDownload.AmazoneDownloadSingleListener() {
            @Override
            public void onDownload(File file) {
                llProgress.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                progressBar1.setVisibility(View.GONE);
                playerView.setVideoPath(file.getPath());
            }

            @Override
            public void error(String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(VideoPlayActivity.this, msg + "", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void progressUpdate(int progress, int max) {
                if(progress>0){
                    progressBar1.setVisibility(View.GONE);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(progress);
                    }
                });
            }
        });
        findViewById(R.id.imgCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncTask.cancel(true);
                finish();
            }
        });
        playerView.setOnPreparedListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(asyncTask!=null){
            asyncTask.cancel(true);
        }
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPrepared() {
        playerView.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(playerView!=null && !playerView.isPlaying()){
            playerView.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(playerView!=null && playerView.isPlaying()){
            playerView.pause();
        }
    }
}
