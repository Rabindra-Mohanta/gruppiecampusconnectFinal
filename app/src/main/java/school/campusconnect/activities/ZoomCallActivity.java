package school.campusconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.LeadItem;
import school.campusconnect.datamodel.PostTeamDataItem;
import school.campusconnect.fragments.LeadListFragment;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.UploadCircleImageFragment;
import us.zoom.sdk.FreeMeetingNeedUpgradeType;
import us.zoom.sdk.InMeetingAudioController;
import us.zoom.sdk.InMeetingChatMessage;
import us.zoom.sdk.InMeetingEventHandler;
import us.zoom.sdk.InMeetingServiceListener;
import us.zoom.sdk.InstantMeetingOptions;
import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.MeetingViewsOptions;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitializeListener;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ZoomCallActivity extends BaseActivity implements SurfaceHolder.Callback {
ProgressBar progressBar;
public static final String TAG = "ZoomCallActivity";
    String meetingID,zoomName,className,meetingPassword,createdID;
    boolean isMessage = false;
    String image, name;
    TextView tvCallerName,tvLbl;
    Button btnCancel;
    UploadCircleImageFragment imageFragment;
    LeadItem leadItem;

    ArrayList<LeadItem.pushTokenData> pushTokenData = new ArrayList<>();
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    public static final String EXTRA_LEAD_ITEM = "extra_lead_item";
    public static final int REQUEST_CODE = 100;

    private SurfaceView surfaceView;

    private String[] neededPermissions = new String[]{CAMERA, WRITE_EXTERNAL_STORAGE};


    CountDownTimer countDownTimer = new CountDownTimer(1*60000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            Log.e(TAG,"onTick"+millisUntilFinished);
        }

        @Override
        public void onFinish() {
            Toast.makeText(getApplicationContext(),"call Not Received",Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_call);

        tvCallerName = findViewById(R.id.tvCallerName);
        tvLbl = findViewById(R.id.tvLbl);
        progressBar = findViewById(R.id.progressBar);
        btnCancel = findViewById(R.id.btnCancel);

        meetingID = getIntent().getStringExtra("meetingID");
        createdID = getIntent().getStringExtra("created");
        meetingPassword = getIntent().getStringExtra("password");
        zoomName = getIntent().getStringExtra("zoomName");
        className = getIntent().getStringExtra("className");
        image = getIntent().getStringExtra("image");
        name = getIntent().getStringExtra("name");
        isMessage = getIntent().getBooleanExtra("isMessage",false);
        leadItem = getIntent().getParcelableExtra(EXTRA_LEAD_ITEM);
        AppLog.e(TAG,"mLeadItem Data" +new Gson().toJson(leadItem));

        tvCallerName.setText(name);

        imageFragment = UploadCircleImageFragment.newInstance(null, true, false);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, imageFragment).commit();
        getSupportFragmentManager().executePendingTransactions();

        surfaceView = findViewById(R.id.surface_camera);
        if (surfaceView != null) {
            boolean result = checkPermission();
            if (result) {
                setupSurfaceHolder();
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }

        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        getWindow().addFlags(flag);

        Log.e(TAG,"meetingID "+meetingID+"\nzoomName"+zoomName+"\nclassName"+className);

        if (isMessage)
        {
            startMeeting(true);

        }else
        {
            Intent intent = getIntent();
            Bundle args = intent.getBundleExtra("BUNDLE");
            pushTokenData = (ArrayList<LeadItem.pushTokenData>) args.getSerializable("data");

            AppLog.e(TAG,"mLeadItem Data size " + pushTokenData.size());

            btnCancel.setVisibility(View.VISIBLE);
            tvLbl.setText("Calling...");
            countDownTimer.start();
        }


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendNotification(pushTokenData).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                finish();
            }
        });
    }

    private void startMeeting(boolean isMessage) {
        try {
            progressBar.setVisibility(View.VISIBLE);

            if (isConnectionAvailable()) {

                initializeZoom(GroupDashboardActivityNew.mGroupItem.zoomKey, GroupDashboardActivityNew.mGroupItem.zoomSecret, GroupDashboardActivityNew.mGroupItem.zoomMail, GroupDashboardActivityNew.mGroupItem.zoomPassword, meetingID, zoomName, className, true,isMessage);
            }
            else {
                showNoNetworkMsg();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,"Exception start meeting "+e.getMessage());
        }
    }

    private void initializeZoom(String zoomKey, String zoomSecret, String zoomMail, String zoomPassword, String meetingId, String zoomName, String className, boolean startOrJoin,boolean isMessage) {

        //showLoadingBar(mBinding.progressBar2);
        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        zoomSDK.initialize(getApplicationContext(), zoomKey, zoomSecret, new ZoomSDKInitializeListener() {
            @Override
            public void onZoomSDKInitializeResult(int i, int i1) {

                AppLog.e(TAG, "Zoom SDK initialized : " + i + " , " + i1 + " , " + startOrJoin);

                try {
                    ZoomSDK.getInstance().getMeetingSettingsHelper().setMuteMyMicrophoneWhenJoinMeeting(true);
                    ZoomSDK.getInstance().getMeetingSettingsHelper().disableCopyMeetingUrl(true);
                    ZoomSDK.getInstance().getMeetingSettingsHelper().setClaimHostWithHostKeyActionEnabled(false);
                    ZoomSDK.getInstance().getMeetingSettingsHelper().disableShowVideoPreviewWhenJoinMeeting(true);
                } catch (Exception ex) {
                }

                if (isMessage)
                {
                    joinZoomMeeting(zoomName,meetingPassword, className, meetingID);
                }
                else if (startOrJoin)
                {
                    startZoomMeeting(zoomMail, zoomPassword, zoomName, className, meetingId);
                }
                else {
                    AppLog.e(TAG, "after initialize : isLogged IN Zoom : " + ZoomSDK.getInstance().isLoggedIn());
                    // joinZoomMeeting(zoomName, zoomPassword, className, meetingId);
                    logoutZoomBeforeJoining(zoomName, zoomPassword, className, meetingId);
                }
            }

            @Override
            public void onZoomAuthIdentityExpired() {
                //hideLoadingBar();
                progressBar.setVisibility(View.GONE);

            }
        });///APP_KEY , APP_SECRET

    }

    @Override
    public void onBackPressed() {
     //   super.onBackPressed();
    }

    private void startZoomMeeting(String zoomMail, String password, String name, String className, String meetingId) {
//        ZoomSDK.getInstance().getMeetingSettingsHelper().setCustomizedMeetingUIEnabled(false);

        AppLog.e(TAG, "startzoommeeting called " + zoomMail + ", " + password + " , " + name + ", " + meetingId);

        ZoomSDK.getInstance().removeAuthenticationListener(ZoomAuthListener);

        ZoomSDK.getInstance().removeAuthenticationListener(ZoomAuthLogoutListener);

        ZoomSDK.getInstance().addAuthenticationListener(ZoomAuthListener);


        if (!ZoomSDK.getInstance().isLoggedIn()) {
            //  ZoomSDK.getInstance().logoutZoom();
            Log.e(TAG, "loginwithzoom Called from startmeeting , not logged in already ");
            ZoomSDK.getInstance().loginWithZoom(zoomMail, password);
        } else {
            Log.e(TAG, "logoutzoom Called from startmeeting , already loggedIn");
            ZoomSDK.getInstance().logoutZoom();
       //     initializeZoom(GroupDashboardActivityNew.mGroupItem.zoomKey, GroupDashboardActivityNew.mGroupItem.zoomSecret, GroupDashboardActivityNew.mGroupItem.zoomMail, GroupDashboardActivityNew.mGroupItem.zoomPassword, meetingID, zoomName, className, true,false);
        }

    }


    private void logoutZoomBeforeJoining(String name, String zoomPassword, String className, String meetingID) {

        AppLog.e(TAG, "logoutZoomBeforeJoining called " + name + ", " + className + ", " + meetingID);

        ZoomSDK.getInstance().removeAuthenticationListener(ZoomAuthLogoutListener);
        ZoomSDK.getInstance().removeAuthenticationListener(ZoomAuthListener);
        ZoomSDK.getInstance().addAuthenticationListener(ZoomAuthLogoutListener);
        ZoomSDK.getInstance().logoutZoom();

    }
    ZoomSDKAuthenticationListener ZoomAuthLogoutListener = new ZoomSDKAuthenticationListener() {
        @Override
        public void onZoomSDKLoginResult(long result) {
            AppLog.e(TAG, "logoutZoomBeforeJoining , onZoomSDKLoginResult : " + result);
        }

        @Override
        public void onZoomSDKLogoutResult(long result) {
            AppLog.e(TAG, "logoutZoomBeforeJoining , onZOomSDKLogoutResult : " + result);

           /* ZoomSDK.getInstance().removeAuthenticationListener(this);
            joinZoomMeeting(item.zoomName.get(0), item.zoomMeetingPassword, item.className, item.jitsiToken);*/
        }

        @Override
        public void onZoomIdentityExpired() {
            AppLog.e(TAG, "onZOomIdentityExpired");
        }

        @Override
        public void onZoomAuthIdentityExpired() {
            AppLog.e(TAG, "onZoomAuthIdentityExpired");
        }

    };

    ZoomSDKAuthenticationListener ZoomAuthListener = new ZoomSDKAuthenticationListener() {
        @Override
        public void onZoomSDKLoginResult(long result) {
            Log.e(TAG, "startmeeting , onZoomLogin Result : " + result);
            if (result == 0) {

                ZoomSDK.getInstance().removeAuthenticationListener(this);
                InstantMeetingOptions opts = new InstantMeetingOptions();
                opts.custom_meeting_id = "item.className";
                opts.meeting_views_options = MeetingViewsOptions.NO_TEXT_MEETING_ID;
                opts.no_invite = true;

                //opts.meeting_views_options = MeetingViewsOptions.NO_TEXT_MEETING_ID;
                if (ZoomCallActivity.class == null)
                    return;

                ZoomSDK.getInstance().getMeetingService().startInstantMeeting(ZoomCallActivity.this, opts);
                ZoomSDK.getInstance().getMeetingService().removeListener(StartMeetListener);
                ZoomSDK.getInstance().getMeetingService().removeListener(JoinMeetListener);
                ZoomSDK.getInstance().getMeetingService().addListener(StartMeetListener);
                ZoomSDK.getInstance().getInMeetingService().addListener(inMeetingListener);
            }

        }

        @Override
        public void onZoomSDKLogoutResult(long result) {
            AppLog.e(TAG, "startmeeting, onZOomSDKLogoutResult : " + result);

//            ZoomSDK.getInstance().loginWithZoom(item.zoomMail, item.zoomPassword);
        }

        @Override
        public void onZoomIdentityExpired() {
            AppLog.e(TAG, "onZOomIdentityExpired");
        }

        @Override
        public void onZoomAuthIdentityExpired() {
            AppLog.e(TAG, "onZoomAuthIdentityExpired");
        }

    };

    InMeetingServiceListener inMeetingListener = new InMeetingServiceListener() {
        @Override
        public void onMeetingNeedPasswordOrDisplayName(boolean b, boolean b1, InMeetingEventHandler inMeetingEventHandler) {

        }

        @Override
        public void onWebinarNeedRegister() {

        }

        @Override
        public void onJoinWebinarNeedUserNameAndEmail(InMeetingEventHandler inMeetingEventHandler) {

        }

        @Override
        public void onMeetingNeedColseOtherMeeting(InMeetingEventHandler inMeetingEventHandler) {

        }

        @Override
        public void onMeetingFail(int i, int i1) {

        }

        @Override
        public void onMeetingLeaveComplete(long l) {
            AppLog.e(TAG, "onMeetingLeaveComplete");
            finish();
        }

        @Override
        public void onMeetingUserJoin(List<Long> list) {

        }

        @Override
        public void onMeetingUserLeave(List<Long> list) {
            AppLog.e(TAG, "onMeetingUserLeave");
            logoutZoomBeforeJoining(zoomName, meetingPassword, className, meetingID);
            finish();
        }

        @Override
        public void onMeetingUserUpdated(long l) {

        }

        @Override
        public void onMeetingHostChanged(long l) {
            AppLog.e(TAG, "onMeetingHostChanged");
        }

        @Override
        public void onMeetingCoHostChanged(long l) {

        }

        @Override
        public void onActiveVideoUserChanged(long l) {

        }

        @Override
        public void onActiveSpeakerVideoUserChanged(long l) {

        }

        @Override
        public void onSpotlightVideoChanged(boolean b) {

        }

        @Override
        public void onUserVideoStatusChanged(long l) {

        }

        @Override
        public void onUserVideoStatusChanged(long l, VideoStatus videoStatus) {

        }

        @Override
        public void onUserNetworkQualityChanged(long l) {

        }

        @Override
        public void onMicrophoneStatusError(InMeetingAudioController.MobileRTCMicrophoneError mobileRTCMicrophoneError) {

        }

        @Override
        public void onUserAudioStatusChanged(long l) {

        }

        @Override
        public void onUserAudioStatusChanged(long l, AudioStatus audioStatus) {

        }

        @Override
        public void onHostAskUnMute(long l) {

        }

        @Override
        public void onHostAskStartVideo(long l) {

        }

        @Override
        public void onUserAudioTypeChanged(long l) {

        }

        @Override
        public void onMyAudioSourceTypeChanged(int i) {

        }

        @Override
        public void onLowOrRaiseHandStatusChanged(long l, boolean b) {

        }

        @Override
        public void onMeetingSecureKeyNotification(byte[] bytes) {

        }

        @Override
        public void onChatMessageReceived(InMeetingChatMessage inMeetingChatMessage) {

        }

        @Override
        public void onSilentModeChanged(boolean b) {

        }

        @Override
        public void onFreeMeetingReminder(boolean b, boolean b1, boolean b2) {

        }

        @Override
        public void onMeetingActiveVideo(long l) {

        }

        @Override
        public void onSinkAttendeeChatPriviledgeChanged(int i) {

        }

        @Override
        public void onSinkAllowAttendeeChatNotification(int i) {

        }

        @Override
        public void onUserNameChanged(long l, String s) {

        }

        @Override
        public void onFreeMeetingNeedToUpgrade(FreeMeetingNeedUpgradeType freeMeetingNeedUpgradeType, String s) {

        }

        @Override
        public void onFreeMeetingUpgradeToGiftFreeTrialStart() {

        }

        @Override
        public void onFreeMeetingUpgradeToGiftFreeTrialStop() {

        }

        @Override
        public void onFreeMeetingUpgradeToProMeeting() {

        }

        @Override
        public void onClosedCaptionReceived(String s) {

        }

        @Override
        public void onRecordingStatus(RecordingStatus recordingStatus) {

        }
    };




    private void joinZoomMeeting(String name, String zoomPassword, String className, String meetingID) {
        JoinMeetingParams params = new JoinMeetingParams();

        AppLog.e(TAG, "joinzoommeeting called " + " , " + name + ", " + meetingID + " ");


        params.meetingNo = meetingID;
        params.password = zoomPassword;

        params.displayName = name;

        JoinMeetingOptions opts = new JoinMeetingOptions();
        opts.no_driving_mode = true;
        //opts.meeting_views_options = MeetingViewsOptions.NO_TEXT_MEETING_ID;
        // opts.no_meeting_end_message = true;
        // opts.no_titlebar = false;
        // opts.meeting_views_options = MeetingViewsOptions.NO_BUTTON_PARTICIPANTS;
        opts.no_bottom_toolbar = false;
        opts.no_invite = true;
        opts.no_video = false;//true
        opts.no_share = true;//false;
        opts.custom_meeting_id = className;
        opts.no_disconnect_audio = true;
        opts.no_audio = true;// set true


        ZoomSDK.getInstance().getMeetingService().removeListener(JoinMeetListener);
        ZoomSDK.getInstance().getMeetingService().removeListener(StartMeetListener);
        ZoomSDK.getInstance().getMeetingService().addListener(JoinMeetListener);

        opts.meeting_views_options = MeetingViewsOptions.NO_BUTTON_PARTICIPANTS + MeetingViewsOptions.NO_TEXT_MEETING_ID;// + MeetingViewsOptions.NO_BUTTON_AUDIO;//+ MeetingViewsOptions.NO_BUTTON_VIDEO +

        ZoomSDK.getInstance().getMeetingService().joinMeetingWithParams(getApplicationContext(), params, opts);

    }

    MeetingServiceListener JoinMeetListener = new MeetingServiceListener() {
        @Override
        public void onMeetingStatusChanged(MeetingStatus meetingStatus, int errorCode, int internalErrorCode) {
            Log.e(TAG, "meetinsstatusChanged Join: " + meetingStatus.name() + " errorcode : " + errorCode + " internalError: " + internalErrorCode);
            if (meetingStatus.name().equalsIgnoreCase("MEETING_STATUS_CONNECTING")) {

                progressBar.setVisibility(View.GONE);
                hideLoadingBar();
            }
            if (meetingStatus.name().equalsIgnoreCase("MEETING_STATUS_DISCONNECTING")) {
                finish();
            }
        }
    };

    MeetingServiceListener StartMeetListener = new MeetingServiceListener() {
        @Override
        public void onMeetingStatusChanged(MeetingStatus meetingStatus, int errorCode, int internalErrorCode) {
            Log.e(TAG, "meetinsstatusChanged : " + meetingStatus.name() + " errorcode : " + errorCode + " internalError: " + internalErrorCode);

            long saveTime = 0;

            if (meetingStatus.name().equalsIgnoreCase("MEETING_STATUS_CONNECTING")) {
                hideLoadingBar();
                progressBar.setVisibility(View.GONE);

            }
            if (meetingStatus.name().equalsIgnoreCase("MEETING_STATUS_INMEETING"))
            {

            }
            if (meetingStatus.name().equalsIgnoreCase("MEETING_STATUS_DISCONNECTING")) {
                finish();
            }
        }
    };



    @Override
    protected void onStart() {
        super.onStart();
        if (image != null && !image.isEmpty() && Constants.decodeUrlToBase64(image).contains("http")) {
            imageFragment.updatePhotoFromUrl(image);
        } else if (image == null) {
            Log.e("ProfileActivity", "image is Null From API ");
            imageFragment.setInitialLatterImage(name);
        }

        registerReceiver(updateReceiver,new IntentFilter("call_accept"));
        registerReceiver(updateReceiver,new IntentFilter("call_decline"));
    }


    BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            countDownTimer.cancel();

            if("call_accept".equalsIgnoreCase(intent.getAction())){
              startMeeting(false);
            }
            if("call_decline".equalsIgnoreCase(intent.getAction())){
                Toast.makeText(getApplicationContext(),"call Denied",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();

    }

    private boolean checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            ArrayList<String> permissionsNotGranted = new ArrayList<>();
            for (String permission : neededPermissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsNotGranted.add(permission);
                }
            }
            if (permissionsNotGranted.size() > 0) {
                boolean shouldShowAlert = false;
                for (String permission : permissionsNotGranted) {
                    shouldShowAlert = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                }
                if (shouldShowAlert) {
                    showPermissionAlert(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]));
                } else {
                    requestPermissions(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]));
                }
                return false;
            }
        }
        return true;
    }

    private void showPermissionAlert(final String[] permissions) {


        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setMessage(R.string.toast_storage_permission_needed);
        alertBuilder.setPositiveButton(getResources().getString(R.string.lbl_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(permissions);
            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void requestPermissions(String[] permissions) {
        ActivityCompat.requestPermissions(ZoomCallActivity.this, permissions, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                for (int result : grantResults) {
                    if (result == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(getApplicationContext(),R.string.toast_storage_permission_needed,Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                setupSurfaceHolder();
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setViewVisibility(int id, int visibility) {
        View view = findViewById(id);
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    private void setupSurfaceHolder() {

        setViewVisibility(R.id.surface_camera, View.VISIBLE);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

    }



    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        startCamera();
    }

    private void startCamera() {
        camera = Camera.open(1);
        camera.setDisplayOrientation(90);
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        resetCamera();
    }

    public void resetCamera() {
        if (surfaceHolder.getSurface() == null) {
            // Return if preview surface does not exist
            return;
        }

        if (camera != null) {
            // Stop if preview surface is already running.
            camera.stopPreview();
            try {
                // Set preview display
                camera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Start the camera preview...
            camera.startPreview();
        }
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        releaseCamera();
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
    private class SendNotification extends AsyncTask<String, String, String> {
        private String server_response;
        ArrayList<LeadItem.pushTokenData> pushTokenData;


        public SendNotification(ArrayList<LeadItem.pushTokenData> pushTokenData) {
            this.pushTokenData = pushTokenData;
        }


        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;

            for (int i = 0;i<pushTokenData.size();i++)
            {
                try {
                    url = new URL("https://fcm.googleapis.com/fcm/send");
                    urlConnection = (HttpURLConnection) url.openConnection();

                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Authorization", BuildConfig.API_KEY_FIREBASE1 + BuildConfig.API_KEY_FIREBASE2);
                    urlConnection.setRequestProperty("Content-Type", "application/json");


                    DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                    try {
                        JSONObject object = new JSONObject();

                        String topic;
                        String title = getResources().getString(R.string.app_name);
                        String name = LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.NAME);
                        String message =  name + " has Cancel Video Call";

                        List<String> listToken = new ArrayList<>();

                        /*for(int i = 0;i<data.pushTokens.size();i++)
                        {
                            if (data.pushTokens.get(i).getDeviceToken() != null && !data.pushTokens.get(i).getDeviceToken().isEmpty())
                            {
                                listToken.add(data.pushTokens.get(i).getDeviceToken());
                            }
                        }

                        String s = TextUtils.join(",", listToken);*/

                        object.put("to", pushTokenData.get(i).getDeviceToken());

                        JSONObject notificationObj = new JSONObject();
                        notificationObj.put("title", title);
                        notificationObj.put("body", message);
                        //  object.put("notification", notificationObj);

                        JSONObject dataObj = new JSONObject();
                        dataObj.put("groupId", GroupDashboardActivityNew.groupId);
                        dataObj.put("createdById", LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.GCM_TOKEN));
                        dataObj.put("createdByImage", LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.PROFILE_IMAGE_NEW));
                        dataObj.put("createdByName", name);
                        dataObj.put("isVideoCall",true);
                        dataObj.put("meetingID",GroupDashboardActivityNew.mGroupItem.zoomMeetingId);
                        dataObj.put("meetingPassword",GroupDashboardActivityNew.mGroupItem.zoomMeetingPassword);
                        dataObj.put("zoomName","Test");
                        dataObj.put("className",className);
                        dataObj.put("iSNotificationSilent",true);
                        dataObj.put("Notification_type", "videoCallEnd");
                        dataObj.put("body", message);
                        object.put("priority","high");
                        object.put("data", dataObj);
                        wr.writeBytes(object.toString());
                        Log.e(TAG, " JSON input : " + object.toString());
                        wr.flush();
                        wr.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Log.e(TAG, " Exception : " + ex.getMessage());
                    }
                    urlConnection.connect();

                    int responseCode = urlConnection.getResponseCode();
                    AppLog.e(TAG, "responseCode :" + responseCode);
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        server_response = readStream(urlConnection.getInputStream());
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    AppLog.e(TAG, "MalformedURLException :" + e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    AppLog.e(TAG, "IOException :" + e.getMessage());
                }
            }


            return server_response;
        }



        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            AppLog.e(TAG, "server_response :" + server_response);

            if (!TextUtils.isEmpty(server_response)) {
                AppLog.e(TAG, "Notification Sent");
            } else {
                AppLog.e(TAG, "Notification Send Fail");
            }
        }
    }

}