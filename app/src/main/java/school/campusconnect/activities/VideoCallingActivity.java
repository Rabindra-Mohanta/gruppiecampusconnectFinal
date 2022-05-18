package school.campusconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.MixOperations;

public class VideoCallingActivity extends AppCompatActivity {
public static final String TAG = "VideoCallingActivity";
String meetingID,zoomName,className;
Button accept,decline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_calling);

        accept = findViewById(R.id.btnStart);
        decline = findViewById(R.id.btnStop);

        meetingID = getIntent().getStringExtra("meetingID");
        zoomName = getIntent().getStringExtra("zoomName");
        className = getIntent().getStringExtra("className");

        Log.e(TAG,"meetingID "+meetingID+"\nzoomName"+zoomName+"\nclassName"+className);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }


        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        getWindow().addFlags(flag);


        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fullScreenIntent = new Intent(getApplicationContext(), ZoomCallActivity.class);
                fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                fullScreenIntent.putExtra("className",className);
                fullScreenIntent.putExtra("meetingID",meetingID);
                fullScreenIntent.putExtra("zoomName",zoomName);
                startActivity(fullScreenIntent);
            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}