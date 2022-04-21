package school.campusconnect.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.mobileconnectors.s3.transferutility.UploadOptions;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;
import school.campusconnect.BuildConfig;
import school.campusconnect.LeafApplication;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.AddVendorPostRequest;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.CoursePostResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneHelper;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.GetThumbnail;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.crop.CropDialogActivity;
import school.campusconnect.utils.youtube.MainActivity;
import school.campusconnect.views.SMBDialogUtils;


public class AddCourseActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<AddPostValidationError>, View.OnClickListener {

    private static final String TAG = "AddCourseActivity";
    @Bind(R.id.toolbar)
    Toolbar mToolBar;

    @Bind(R.id.etName)
    EditText edtTitle;

    @Bind(R.id.etDesc)
    EditText edtDesc;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;



    @Bind(R.id.btnCreateClass)
    Button btnShare;

    String group_id;
    LeafManager manager = new LeafManager();

    private CoursePostResponse.CoursePostData mainRequest;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        ButterKnife.bind(this);

        init();

        setListener();

    }

    private void setListener() {

        btnShare.setOnClickListener(this);
    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        group_id=GroupDashboardActivityNew.groupId;

        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.title_add_course));

    }

    private void shareButtonEnableDisable() {
       /* if (isValid(false)) {
            btnShare.setEnabled(true);
        } else {
            btnShare.setEnabled(true);
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void addPost() {
        hide_keyboard();
        if (isConnectionAvailable()) {
            if (isValid(true)) {
                if (progressBar != null)
                    progressBar.setVisibility(View.VISIBLE);
              //  btnShare.setEnabled(false);

                CoursePostResponse.CoursePostData request = new CoursePostResponse.CoursePostData();
                request.courseName = edtTitle.getText().toString();
                request.description = edtDesc.getText().toString();


                    Log.e(TAG, "send data " + new Gson().toJson(request));
                    mainRequest=request;
                    manager.addCourse(this, group_id, request);


            }
        } else {
            showNoNetworkMsg();
        }

    }


    public boolean isValid(boolean showToast) {
        boolean valid=true;

        Log.e("edtDesc : ", edtTitle.getText().toString());


        if (!isValueValidOnly(edtTitle)) {
            if (showToast)
                Toast.makeText(this, getResources().getString(R.string.toast_add_course_name),Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }


    public void onClick(View v)
    {
        hide_keyboard();
        switch (v.getId())
        {
            case R.id.btnCreateClass:
                addPost();
                break;

        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        switch (apiId)
        {

            default:
                Toast.makeText(AddCourseActivity.this, getResources().getString(R.string.toast_successfully_posted), Toast.LENGTH_SHORT).show();
                LeafPreference.getInstance(this).setBoolean(LeafPreference.IS_VENDOR_POST_UPDATED, true);
                new SendNotification().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                finish();

                break;
        }

    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<AddPostValidationError> error) {
     //   btnShare.setEnabled(true);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        Log.e("AddPostActivity", "OnFailure " + error.title + " , " + error.type);
        if (error.status.equals("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
            finish();
        } else {
            if(error.errors==null)
                return;
            if (error.errors.get(0).video != null) {
                Toast.makeText(this, error.errors.get(0).video, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, error.title, Toast.LENGTH_SHORT).show();
            }
        }


    }


    @Override
    public void onException(int apiId, String error) {
  //      btnShare.setEnabled(true);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        Toast.makeText(AddCourseActivity.this, error, Toast.LENGTH_SHORT).show();

    }

    private class SendNotification extends AsyncTask<String, String, String> {
        private String server_response;

        public SendNotification()
        {

        }

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;

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
                    String name = LeafPreference.getInstance(AddCourseActivity.this).getString(LeafPreference.NAME);
                    String message = name + " has added new vendor." ;
                    topic = GroupDashboardActivityNew.groupId ;
                    object.put("to", "/topics/" + topic);

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title", title);
                    notificationObj.put("body", message);
                   // object.put("notification", notificationObj);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("groupId", GroupDashboardActivityNew.groupId);
                    dataObj.put("createdById", LeafPreference.getInstance(AddCourseActivity.this).getString(LeafPreference.LOGIN_ID));
                    dataObj.put("teamId", group_id);
                    dataObj.put("title", title);
                    dataObj.put("Notification_type",  "VendorAdd");
                    dataObj.put("body", message);
                    object.put("data", dataObj);
                    wr.writeBytes(object.toString());
                    Log.e(TAG, " JSON input : " + object.toString());
                    wr.flush();
                    wr.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                AppLog.e(TAG, "responseCode :" + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_response = readStream(urlConnection.getInputStream());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        if (requestCode == 10)
        {
            String uri = data.getStringExtra("Data");
            Log.e(TAG,"uri"+ uri);
        }

        shareButtonEnableDisable();

    }
    public static int dpToPx(DisplayMetrics displayMetrics, int dp) {

        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}
