package school.campusconnect.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.SignUpRequest;
import school.campusconnect.datamodel.SignupValidationError;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.UploadCircleImageFragment;

public class SignupImageActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<SignupValidationError>{

    public static final String TAG = "SignupImageActivity";

    UploadCircleImageFragment imageFragment;

    @Bind(R.id.btnNext)
    Button btnNext;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private Boolean validateUser = false;


    public static final String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    SignUpRequest request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_image);

        ButterKnife.bind(this);

        imageFragment = UploadCircleImageFragment.newInstance(null, true, false);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, imageFragment).commit();
        getSupportFragmentManager().executePendingTransactions();
        imageFragment.setEditEnabled(true);

        inits();

        reqPermission();

    }
    public void reqPermission(){
        if (!hasPermission(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, 222);
        }
    }

    public boolean hasPermission(String[] permissions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
            }
        }
        return true;
    }

    @Override
    protected void onStart() {
     //   imageFragment.setInitialLatterImage("Piyush");
        super.onStart();
    }

    private void inits() {


        if (getIntent() != null)
        {
            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                request = (SignUpRequest) getIntent().getSerializableExtra("req"); //Obtaining data
            }

            validateUser = getIntent().getBooleanExtra("userFlag",false);
            Log.e(TAG,"REQUEST "+new Gson().toJson(request));

        }
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isConnectionAvailable()) {

                    if (imageFragment.isImageChanged && !TextUtils.isEmpty(imageFragment.getmProfileImage())) {

                        request.image = imageFragment.getmProfileImage();
                    }

                    LeafManager manager = new LeafManager();

                    AppLog.e("TAG", new Gson().toJson(request));

                    btnNext.setEnabled(false);

                    manager.doSignUp(SignupImageActivity.this, request);

                } else {

                    showNoNetworkMsg();
                }


            }
        });




    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        btnNext.setEnabled(true);


        if (progressBar != null)
            hideLoadingBar();
        //   progressBar.setVisibility(View.GONE);

        Intent i = new Intent(this, UserExistActivity.class);
        i.putExtra("userFlag",validateUser);
        startActivity(i);
        finish();

        btnNext.setEnabled(true);

    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<SignupValidationError> error) {
        btnNext.setEnabled(true);
        //hideLoadingDialog();
        if (progressBar != null)
            hideLoadingBar();
        //   progressBar.setVisibility(View.GONE);

    }

    @Override
    public void onException(int apiId, String error) {
        btnNext.setEnabled(true);
        //hideLoadingDialog();
        if (progressBar != null)
            hideLoadingBar();
        //   progressBar.setVisibility(View.GONE);
        Toast.makeText(SignupImageActivity.this, error, Toast.LENGTH_SHORT).show();
    }
}