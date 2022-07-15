package school.campusconnect.utils.youtube;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
//import android.databinding.repacked.org.antlr.v4.Tool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import androidx.core.app.NavUtils;
import school.campusconnect.utils.AppLog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import android.widget.VideoView;

import school.campusconnect.R;
import school.campusconnect.activities.BaseActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.views.DrawableEditText;

public class ReviewActivity extends BaseActivity {
    VideoView mVideoView;
    MediaController mc;
    private String mChosenAccountName;
    private Uri mFileUri;
    DrawableEditText et_title;
    DrawableEditText et_description;
    Toolbar mToolBar;

    public ProgressDialog dialog;

    public static Activity reviewActivity;

    Runnable notification;

    Handler handler;

    UploadService mService;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_review);
        reviewActivity = this;
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        Button uploadButton = (Button) findViewById(R.id.upload_button);
        et_title = (DrawableEditText) findViewById(R.id.et_title);
        et_description = (DrawableEditText) findViewById(R.id.et_description);

        handler = new Handler();

        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            uploadButton.setVisibility(View.GONE);
            et_title.setVisibility(View.GONE);
            et_description.setVisibility(View.GONE);
            setTitle(R.string.playing_the_video_in_upload_progress);
        }
        mFileUri = intent.getData();
        loadAccount();

        reviewVideo(mFileUri);
    }

    private void reviewVideo(Uri mFileUri) {
        try {
            mVideoView = (VideoView) findViewById(R.id.videoView);
            mc = new MediaController(this);
            mVideoView.setMediaController(mc);
            mVideoView.setVideoURI(mFileUri);
            mc.show();
            mVideoView.start();
        } catch (Exception e) {
           AppLog.e(this.getLocalClassName(), e.toString());
        }
    }

    private void loadAccount() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        mChosenAccountName = sp.getString(MainActivity.ACCOUNT_KEY, null);
//        invalidateOptionsMenu();
       AppLog.e("YOUTUBEE", "loadAccount name is " + mChosenAccountName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.review, menu);
        return true;
    }

    public void uploadVideo(View view) {


       AppLog.e("REviewActivity", "UploadVideo Called Account NOT Null");
        // if a video is picked or recorded.
        if (mFileUri != null) {
            GroupDashboardActivityNew.uploadTitle = GroupDashboardActivityNew.enteredTitle;
            GroupDashboardActivityNew.uploadDesc = GroupDashboardActivityNew.enteredDesc;
           AppLog.e("REviewActivity", "UploadVideo Called FILE URI NOT Null");
            Intent uploadIntent = new Intent(this, UploadService.class);
            uploadIntent.setData(mFileUri);
            uploadIntent.putExtra(MainActivity.ACCOUNT_KEY, mChosenAccountName);
            startService(uploadIntent);

            Intent uploadIntent2 = new Intent(this, UploadService.class);
            bindService(uploadIntent2, mConnection, Context.BIND_AUTO_CREATE);

            Toast.makeText(this, R.string.youtube_upload_started,
                    Toast.LENGTH_LONG).show();

        } else {
           AppLog.e("REviewActivity", "UploadVideo Called FILE URI IS Null");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void handleUploadProgress() {
        dialog = new ProgressDialog(ReviewActivity.this);
        dialog.setMessage("Uploading please wait...");
        dialog.setCancelable(false);
        dialog.show();


        notification = new Runnable() {
            public void run() {
                // UpdateProgress Returns True Means We Need To Get More Updates.... If Return False , Then Task Completed
                if (!updateProgress())
                    return;

                handler.postDelayed(notification, 150);

            }
        };
        handler.postDelayed(notification, 150);

    }

    private boolean updateProgress() {
       AppLog.e("ReviewActivity", "Updated Progress Called : " + mService.getUploadProgress());
        if (mService.getUploadProgress() == -1) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                ReviewActivity.this.finish();
            }
            return false;
        }

        if (mService.getUploadProgress() == -2) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                Toast.makeText(this, getResources().getString(R.string.toast_upload_failed), Toast.LENGTH_SHORT).show();
//                ReviewActivity.this.finish();
            }
            return false;
        }

        if (mService.getUploadProgress() != 0) {
            if (mService.getUploadProgress() == 100) {
                dialog.setMessage("Processing video...Please wait...");
            } else {
                dialog.setMessage("Uploading please wait..." + mService.getUploadProgress() + "% completed...");
            }
        }

        return true;
    }


    private class TaskForDialog extends AsyncTask<Void, Void, Void> {

        Runnable notification;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ReviewActivity.this);
            dialog.setMessage("Uploading please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Handler handler = new Handler();
            notification = new Runnable() {
                public void run() {
//                    if (dialog != null && dialog.isShowing()) {
                    publishProgress();
                    handler.postDelayed(notification, 150);
//                    }
                }
            };
            handler.postDelayed(notification, 150);

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            /*if (uploaded == -1)
            {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                return;
            }
            if (uploaded != 0)
                dialog.setMessage("Uploading please wait..." + uploaded + "% completed...");*/
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }


    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            UploadService.LocalBinder binder = (UploadService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            handleUploadProgress();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}
