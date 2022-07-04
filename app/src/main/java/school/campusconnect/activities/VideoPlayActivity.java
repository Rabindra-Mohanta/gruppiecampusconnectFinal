package school.campusconnect.activities;

import android.content.Intent;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.FileProvider;

import android.net.Uri;
import android.os.Build;
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

import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.VideoOfflineObject;
import school.campusconnect.utils.AmazoneVideoDownload;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

public class VideoPlayActivity extends AppCompatActivity implements OnPreparedListener {

    public static final String TAG = "VideoPlayActivity";
    VideoView playerView;

    ImageView imgDownload;
    ImageView iconShareExternal;
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
        iconShareExternal = findViewById(R.id.iconShareExternal);
        imgDownload = findViewById(R.id.imgDownload);
        thumbnail = findViewById(R.id.thumbnail);
        beforeDownload = findViewById(R.id.llBeforeDownload);
        afterDownload = findViewById(R.id.llAfterDownload);

        llProgress = findViewById(R.id.llProgress);
        progressBar = findViewById(R.id.progressBar);
        progressBar1 = findViewById(R.id.progressBar1);
        progressBar1.setVisibility(View.VISIBLE);
        llProgress.setVisibility(View.VISIBLE);

        if (new AmazoneVideoDownload(this).isVideoDownloaded(getApplicationContext(),getIntent().getStringExtra("video")))
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

        iconShareExternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isDownloaded = true;

                if (!AmazoneVideoDownload.isVideoDownloaded(getApplicationContext(),getIntent().getStringExtra("video")))
                {
                    isDownloaded = false;
                }


                if (isDownloaded)
                {
                    ArrayList<Uri> files =new ArrayList<>();

                    files.add(AmazoneVideoDownload.getDownloadPath(getApplicationContext(),getIntent().getStringExtra("video")));

                    /*ArrayList<Uri> uris = new ArrayList<>();

                    for(File file: files){

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            uris.add(FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file));
                        } else {
                            uris.add(Uri.fromFile(file));
                        }

                    }
*/
                    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    intent.setType("*/*");
                    intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                    startActivity(Intent.createChooser(intent, "Share File"));
                }
                else
                {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.smb_no_file_download),Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.imgCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncTask.cancel(true);
                beforeDownload.setVisibility(View.VISIBLE);
                afterDownload.setVisibility(View.GONE);
                finish();
            }
        });
        playerView.setOnPreparedListener(this);
    }

    private void startProcess()
    {
        asyncTask = AmazoneVideoDownload.download(this, getIntent().getStringExtra("video"), new AmazoneVideoDownload.AmazoneDownloadSingleListener() {
            @Override
            public void onDownload(Uri file) {
                llProgress.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                progressBar1.setVisibility(View.GONE);

                AppLog.e(TAG, "filename : "+file);

                if (file != null)
                {
                    playerView.setVideoPath(file.toString());
                }

                AppLog.e(GroupDashboardActivityNew.class.getName(), "filename saved in preference : "+getIntent().getStringExtra("video"));

                try {
                    saveVideoNameOffline(getIntent().getStringExtra("video") , file.toString());
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
