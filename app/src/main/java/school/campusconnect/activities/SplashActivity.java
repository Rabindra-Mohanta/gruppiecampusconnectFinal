package school.campusconnect.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import school.campusconnect.Assymetric.Utils;
import school.campusconnect.BuildConfig;
import school.campusconnect.LeafApplication;
import school.campusconnect.datamodel.GroupResponse;
import school.campusconnect.utils.AppLog;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iceteck.silicompressorr.RealPathUtils;

import java.util.ArrayList;

import school.campusconnect.R;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.database.RememberPref;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.GroupDataItem;
import school.campusconnect.datamodel.GroupDetailResponse;
import school.campusconnect.datamodel.PostDataItem;
import school.campusconnect.datamodel.PostTeamDataItem;
import school.campusconnect.datamodel.TeamListItem;
import school.campusconnect.datamodel.gruppiecontacts.GruppieContactGroupIdModel;
import school.campusconnect.datamodel.gruppiecontacts.GruppieContactsModel;
import school.campusconnect.datamodel.notifications.NotificationModel;
import school.campusconnect.datamodel.personalchat.PersonalContactsModel;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.views.SMBDialogUtils;

public class SplashActivity extends AppCompatActivity implements LeafManager.OnCommunicationListener {

    private static final String TAG = "SplashActivity";
    private Handler mHandler;
    private LeafManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        AppLog.e(TAG, "onCreate()");
        manager = new LeafManager();

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();
        AppLog.e(TAG,"action : "+action);
        AppLog.e(TAG,"type : "+type);
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            Uri uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
            AppLog.e(TAG,"uri : "+uri);
            ArrayList<String> list = new ArrayList<>();
            String path = ImageUtil.getPath(this, uri);
            AppLog.e(TAG,"path : "+path);
            if(!TextUtils.isEmpty(path)){
                list.add(path);
            }
            String fileType = "";
            if (type.startsWith("application/pdf")) {
                fileType = Constants.FILE_TYPE_PDF;
            }else if (type.startsWith("image/")) {
                fileType = Constants.FILE_TYPE_IMAGE;
            }else if (type.startsWith("video/")) {
                fileType = Constants.FILE_TYPE_VIDEO;
            }
            LeafApplication.getInstance().setShareFileList(list,fileType);
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            ArrayList<Uri> uriList = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            AppLog.e(TAG,"uriList : "+uriList);
            ArrayList<String> list = new ArrayList<>();
            if(uriList!=null){
                for (int i=0;i<uriList.size();i++){
                    String path = ImageUtil.getPath(this, uriList.get(i));
                    if(!TextUtils.isEmpty(path)){
                        list.add(path);
                    }
                }
            }
            AppLog.e(TAG,"uriList path: "+list);
            String fileType = "";
            if (type.startsWith("application/pdf")) {
                fileType = Constants.FILE_TYPE_PDF;
            }else if (type.startsWith("image/")) {
                fileType = Constants.FILE_TYPE_IMAGE;
            }else if (type.startsWith("video/")) {
                fileType = Constants.FILE_TYPE_VIDEO;
            }
            LeafApplication.getInstance().setShareFileList(list,fileType);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLog.e(TAG, "OnResume Called");
        init();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    boolean isAllPermissionGranted = true;
                    for (int i = 0; i < permissions.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            isAllPermissionGranted = false;
                            AppLog.i(TAG, "I=" + i);
                        }
                    }

                    if (isAllPermissionGranted) {
                        init();
                    } else {
                        finish();
                    }

                } else {
                    finish();
                }

                break;
        }
    }

    private void init() {

        mHandler = new Handler();
        AppLog.e(TAG, "Init Called");
        if (LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.TOKEN).isEmpty()) {
            AppLog.e(TAG, "Token Empty");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isConnectionAvailable()) {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity2.class);
                        startActivity(intent);
                        finish();
                    } else {
                        showNoNetworkMsg();
                    }
                    // }
                }
            }, 1500);
        } else {
            mHandler.postDelayed(new Runnable() {


                @Override
                public void run() {
                    if (!isConnectionAvailable())
                        gotoHomeScreen();
                    else {
                        if("CAMPUS".equalsIgnoreCase(BuildConfig.AppCategory)){
                            SplashActivity.this.manager.getGroups(SplashActivity.this);
                        }else {
                            SplashActivity.this.manager.getGroupDetail(SplashActivity.this, BuildConfig.APP_ID);
                        }

                    }
                }
            }, 1000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
    }

    public boolean isConnectionAvailable() {
        ConnectivityManager connectivityManager;
        NetworkInfo networkinfo;
        connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        networkinfo = connectivityManager.getActiveNetworkInfo();
        return (networkinfo != null && networkinfo.isConnected());
    }

    public void showNoNetworkMsg() {
        try {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                    .setAction("SETTINGS", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onBackPressed();
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
                    }).setActionTextColor(Color.WHITE);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.snackbar_textsize));
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        } catch (Exception e) {
            AppLog.e("SnackBar", "error is " + e.toString());
            SMBDialogUtils.showSMBDialogOK(this, getString(R.string.no_internet), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    dialog.dismiss();
                    //onBackPressed();
                }
            });
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        if("CAMPUS".equalsIgnoreCase(BuildConfig.AppCategory)){
            GroupResponse res = (GroupResponse) response;
            AppLog.e(TAG, "ClassResponse " + res.data);
            LeafPreference.getInstance(getApplicationContext()).setInt(LeafPreference.GROUP_COUNT, res.data.size());
            gotoHomeScreen();
        }else {
            if (apiId == LeafManager.API_ID_GROUP_DETAIL) {
                GroupDetailResponse gRes = (GroupDetailResponse) response;
                AppLog.e(TAG, "group detail ->" + new Gson().toJson(gRes));
                saveGroupDetails(gRes);
            }
        }
    }
    //save group detail
    private void saveGroupDetails(GroupDetailResponse gRes) {
        LeafPreference.getInstance(this).setString(Constants.GROUP_DATA, new Gson().toJson(gRes.data.get(0)));
        LeafPreference.getInstance(this).setInt(Constants.TOTAL_MEMBER, gRes.data.get(0).totalUsers);
        gotoHomeScreen();
    }
    private void gotoHomeScreen()
    {
        if("CAMPUS".equalsIgnoreCase(BuildConfig.AppCategory)){
            if(LeafPreference.getInstance(getApplicationContext()).getInt(LeafPreference.GROUP_COUNT)>1){
                Intent intent = new Intent(SplashActivity.this, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }else {
                Intent login = new Intent(this, GroupDashboardActivityNew.class);
                login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(login);
                finish();
            }
        }else {
            Intent intent = new Intent(SplashActivity.this, GroupDashboardActivityNew.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        AppLog.e("splash", "onFailure");
        if (msg.contains("401") || msg.contains("Unauthorized")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_LONG).show();
            logout();
        }
        else if (msg.contains("Not Found"))
        {
            logout();
        }
        else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onException(int apiId, String msg) {
        AppLog.e("splash", "onException : "+msg);
        /*if (apiCallCount <= 1) {
            manager.getGroupDetail(SplashActivity.this, Constants.group_id_str);
            apiCallCount++;
        } else {
            showNoNetworkMsg();
        }*/
    }

    public void logout() {
        AppLog.e("Logout", "onSuccessCalled");
        LeafPreference.getInstance(this).clearData();
        RememberPref.getInstance(this).clearData();
        AppLog.e("GroupList", "Grouplist token : " + LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.GCM_TOKEN));
        GroupDataItem.deleteAll();
        PostDataItem.deleteAllPosts();
        NotificationModel.deleteAll();
        TeamListItem.deleteAll();
        PostTeamDataItem.deleteAllPosts();
        PersonalContactsModel.deleteAll();
        GruppieContactsModel.deleteAll();
        GruppieContactGroupIdModel.deleteAll();
        getSharedPreferences("pref_noti_count", MODE_PRIVATE).edit().clear().apply();
        new DatabaseHandler(this).deleteAll();
        Intent intent = new Intent(this, LoginActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
