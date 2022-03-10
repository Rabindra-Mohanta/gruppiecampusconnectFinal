package school.campusconnect.activities;

import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.VideoOfflineObject;
import school.campusconnect.utils.AmazoneVideoDownload;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;

public class VideoPlayActivity extends AppCompatActivity implements OnPreparedListener {

    public static final String TAG = "VideoPlayActivity";
    VideoView playerView;

    ImageView imgDownload;
    ImageView thumbnail;
    RelativeLayout beforeDownload;
    RelativeLayout afterDownload;
    String thumbnailPath;
    AmazoneVideoDownload asyncTask;
    ProgressBar progressBar;
    ProgressBar progressBar1;
    View llProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        playerView = findViewById(R.id.video_view);
        imgDownload = findViewById(R.id.imgDownload);
        thumbnail = findViewById(R.id.thumbnail);
        beforeDownload = findViewById(R.id.llBeforeDownload);
        afterDownload = findViewById(R.id.llAfterDownload);

        llProgress = findViewById(R.id.llProgress);
        progressBar = findViewById(R.id.progressBar);
        progressBar1 = findViewById(R.id.progressBar1);
        progressBar1.setVisibility(View.VISIBLE);
        llProgress.setVisibility(View.VISIBLE);

        if (new AmazoneVideoDownload(this).isVideoDownloaded(getIntent().getStringExtra("video")))
        {
            beforeDownload.setVisibility(View.GONE);
            afterDownload.setVisibility(View.VISIBLE);
            startProcess();
        }
        else
        {
            beforeDownload.setVisibility(View.VISIBLE);
            afterDownload.setVisibility(View.GONE);
            thumbnailPath = getIntent().getStringExtra("thumbnail");
            Log.e(TAG,"thumbnailPath"+thumbnailPath);

            Glide.with(this).load(Constants.decodeUrlToBase64(thumbnailPath)).into(thumbnail);
        }

        imgDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beforeDownload.setVisibility(View.GONE);
                afterDownload.setVisibility(View.VISIBLE);
                startProcess();
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

    private void startProcess()
    {
        asyncTask = AmazoneVideoDownload.download(this, getIntent().getStringExtra("video"), new AmazoneVideoDownload.AmazoneDownloadSingleListener() {
            @Override
            public void onDownload(File file) {
                llProgress.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                progressBar1.setVisibility(View.GONE);
                playerView.setVideoPath(file.getPath());

                AppLog.e(GroupDashboardActivityNew.class.getName(), "filename saved in preference : "+getIntent().getStringExtra("video"));

                try {
                    saveVideoNameOffline(getIntent().getStringExtra("video") , file.getPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }

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

    public void saveVideoNameOffline(String fileName, String filePath)
    {
        VideoOfflineObject offlineObject = new VideoOfflineObject();
        offlineObject.setVideo_filename(fileName);
        offlineObject.setVideo_filepath(filePath);
        offlineObject.setVideo_date(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        LeafPreference preference = LeafPreference.getInstance(VideoPlayActivity.this);

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

}
