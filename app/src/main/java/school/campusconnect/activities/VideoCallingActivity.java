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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.MixOperations;

public class VideoCallingActivity extends AppCompatActivity {
public static final String TAG = "VideoCallingActivity";
    private static final String ACTION_STOP_LISTEN = "action_stop_listen";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_calling);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        getWindow().addFlags(flag);


        if (getIntent() != null && ACTION_STOP_LISTEN.equals(getIntent().getAction())) {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();



       LocalBroadcastManager.getInstance(VideoCallingActivity.this).registerReceiver(mMessageReceiver, new IntentFilter(ACTION_STOP_LISTEN));


    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent

            AppLog.e(TAG, "onReceive called with action : " + intent.getAction());

        /*    String message = intent.getStringExtra("action");

            AppLog.e(TAG, "onReceive called with action : " + intent.getStringExtra("data"));*/



        }
    };
}