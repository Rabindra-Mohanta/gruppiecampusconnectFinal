package school.campusconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.MixOperations;

public class VideoCallingActivity extends AppCompatActivity {
public static final String TAG = "VideoCallingActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_calling);
    }

    @Override
    protected void onStart() {
        super.onStart();



        LocalBroadcastManager.getInstance(VideoCallingActivity.this).registerReceiver(mMessageReceiver, new IntentFilter("PROCTORING_START"));


    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent


            String message = intent.getStringExtra("action");
            AppLog.e(TAG, "onReceive called with action : " + message);
            AppLog.e(TAG, "onReceive called with action : " + intent.getStringExtra("data"));



        }
    };
}