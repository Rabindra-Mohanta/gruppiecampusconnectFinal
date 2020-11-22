package school.campusconnect.activities;

import android.content.Intent;
import android.os.Bundle;

import school.campusconnect.R;
import school.campusconnect.utils.AppLog;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TestActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, View.OnClickListener {

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    String YOUTUBE_API_KEY = "AIzaSyDaQC18UKpkamS4h0oLYFGScsulsHN3Vl4";
    String VIDEO_URL ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);

        ImageView iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);

        youTubeView.initialize(YOUTUBE_API_KEY, this);
        VIDEO_URL = getYouTubeId(getIntent().getExtras().getString("url"));
       AppLog.e("TestActivity", "Youtube Video Link : "+VIDEO_URL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == RECOVERY_REQUEST)
        {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(YOUTUBE_API_KEY, this);
        }
    }

    protected YouTubePlayerView getYouTubePlayerProvider()
    {
        return youTubeView;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean b) {
        if (!b) {
            player.cueVideo(VIDEO_URL); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError())
        {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        }
        else
        {
            String error = String.format("Error", errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }


    private String getYouTubeId (String youTubeUrl) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if(matcher.find()){
            return matcher.group();
        } else {
            return "error";
        }
    }

    @Override
    public void onClick(View v) {
        TestActivity.this.finish();
    }
}