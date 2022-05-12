package school.campusconnect.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.activeandroid.ActiveAndroid;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import school.campusconnect.BuildConfig;
import school.campusconnect.LeafApplication;
import school.campusconnect.utils.AppLog;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import school.campusconnect.R;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.database.RememberPref;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.GroupDataItem;
import school.campusconnect.datamodel.PostDataItem;
import school.campusconnect.datamodel.PostTeamDataItem;
import school.campusconnect.datamodel.BaseTeamTable;
import school.campusconnect.datamodel.gruppiecontacts.GruppieContactGroupIdModel;
import school.campusconnect.datamodel.gruppiecontacts.GruppieContactsModel;
import school.campusconnect.datamodel.notifications.NotificationModel;
import school.campusconnect.datamodel.personalchat.PersonalContactsModel;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.views.SMBDialogUtils;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        AppLog.e(TAG, "onCreate()");

        handleIntent(getIntent());

        ActiveAndroid.initialize(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();
        AppLog.e(TAG, "action : " + action);
        AppLog.e(TAG, "type : " + type);
        if (type == null) {
            return;
        }
        Uri uri = null;
        ArrayList<Uri> uriList = null;
        if (Intent.ACTION_SEND.equals(action)) {
            uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
            uriList = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        }


        new HandleShareFiles(action, type, uri, uriList).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class HandleShareFiles extends AsyncTask<Void, Void, Void> {
        String action;
        String type;
        Uri uri;
        ArrayList<Uri> uriList;

        public HandleShareFiles(String action, String type, Uri uri, ArrayList<Uri> uriList) {
            this.action = action;
            this.type = type;
            this.uri = uri;
            this.uriList = uriList;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String fileType = "";
                String extension = "";
                if (type.startsWith("application/pdf")) {
                    fileType = Constants.FILE_TYPE_PDF;
                    extension = ".pdf";
                } else if (type.startsWith("image/")) {
                    fileType = Constants.FILE_TYPE_IMAGE;
                    extension = ".jpeg";
                } else if (type.startsWith("video/")) {
                    fileType = Constants.FILE_TYPE_VIDEO;
                    extension = ".mp4";
                }

                AppLog.e(TAG, "fileType : " + type);
                ArrayList<String> list = new ArrayList<>();
                if (uriList != null) {
                    for (int i = 0; i < uriList.size(); i++) {
                        /*try {
                            String path = ImageUtil.getPath(LeafApplication.getInstance(), uriList.get(i));
                            if (!TextUtils.isEmpty(path)) {*/
                                list.add(uriList.get(i).toString());
                            /*}
                        } catch (IllegalArgumentException e) {
                            AppLog.e(TAG, " IllegalArgumentException :" + e.getMessage());
                            String fileName = System.currentTimeMillis() + "" + new Random().nextInt(99) + extension;
                            File file = new File(LeafApplication.getInstance().getExternalCacheDir(), fileName);
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            ImageUtil.writeDataToUri(LeafApplication.getInstance(), file, uriList.get(i));
                            if (!TextUtils.isEmpty(file.getAbsolutePath())) {
                                list.add(file.getAbsolutePath());
                            }
                        }*/
                    }
                }

                if (uri != null) {
                   /* try {
                        String path = ImageUtil.getPath(LeafApplication.getInstance(), uri);
                        if (!TextUtils.isEmpty(path)) {*/
                            list.add(uri.toString());
                        /*}
                    } catch (IllegalArgumentException e) {
                        AppLog.e(TAG, " IllegalArgumentException :" + e.getMessage());
                        String fileName = System.currentTimeMillis() + "" + new Random().nextInt(99) + extension;
                        File file = new File(LeafApplication.getInstance().getExternalCacheDir(), fileName);
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        ImageUtil.writeDataToUri(LeafApplication.getInstance(), file, uri);
                        if (!TextUtils.isEmpty(file.getAbsolutePath())) {
                            list.add(file.getAbsolutePath());
                        }
                    }*/
                }

                AppLog.e(TAG, "uriList path: " + list);
                LeafApplication.getInstance().setShareFileList(list, fileType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
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
                      /*  Intent intent = new Intent(SplashActivity.this, LoginActivity2.class);
                        startActivity(intent);
                        finish();*/
                        Intent intent = new Intent(SplashActivity.this, ChangeLanguageActivity.class);
                        intent.putExtra("isSplash",true);
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
                    gotoHomeScreen();
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
                    .setAction(getResources().getString(R.string.action_settings), new View.OnClickListener() {
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


    private void gotoHomeScreen() {


        if (LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.SKIP_PIN) != null && LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.SKIP_PIN).equalsIgnoreCase("yes"))
        {
            if ("constituency".equalsIgnoreCase(BuildConfig.AppCategory)) {
                if (LeafPreference.getInstance(getApplicationContext()).getInt(LeafPreference.CONST_GROUP_COUNT) > 1) {
                    Intent login = new Intent(this, ConstituencyListActivity.class);
                    login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(login);
                    finish();
                } else {
                    Intent login = new Intent(this, GroupDashboardActivityNew.class);
                    login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(login);
                    finish();
                }

            } else {
                if ("CAMPUS".equalsIgnoreCase(BuildConfig.AppCategory)) {
                    if ("taluk".equalsIgnoreCase(LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.ROLE))) {
                        Intent login = new Intent(this, TalukListActivity.class);
                        login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(login);
                        finish();
                    } else {
                        if (LeafPreference.getInstance(getApplicationContext()).getInt(LeafPreference.GROUP_COUNT) > 1) {
                            Intent intent = new Intent(SplashActivity.this, Home.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent login = new Intent(this, GroupDashboardActivityNew.class);
                            login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(login);
                            finish();
                        }
                    }

                } else {
                    Intent intent = new Intent(SplashActivity.this, GroupDashboardActivityNew.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        }
        else
        {
            Intent login = new Intent(this, LoginPinActivity.class);
            login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(login);
            finish();
        }

       /* if ("constituency".equalsIgnoreCase(BuildConfig.AppCategory)) {
            if (LeafPreference.getInstance(getApplicationContext()).getInt(LeafPreference.CONST_GROUP_COUNT) > 1) {
                Intent login = new Intent(this, ConstituencyListActivity.class);
                login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(login);
                finish();
            } else {
                Intent login = new Intent(this, GroupDashboardActivityNew.class);
                login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(login);
                finish();
            }

        } else {
            if ("CAMPUS".equalsIgnoreCase(BuildConfig.AppCategory)) {
                if ("taluk".equalsIgnoreCase(LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.ROLE))) {
                    Intent login = new Intent(this, TalukListActivity.class);
                    login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(login);
                    finish();
                } else {
                    if (LeafPreference.getInstance(getApplicationContext()).getInt(LeafPreference.GROUP_COUNT) > 1) {
                        Intent intent = new Intent(SplashActivity.this, Home.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent login = new Intent(this, GroupDashboardActivityNew.class);
                        login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(login);
                        finish();
                    }
                }

            } else {
                Intent intent = new Intent(SplashActivity.this, GroupDashboardActivityNew.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }*/
    }
}
