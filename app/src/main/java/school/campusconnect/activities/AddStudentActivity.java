package school.campusconnect.activities;

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.AddStudentReq;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.views.SMBDialogUtils;

public class AddStudentActivity extends BaseActivity {
    private static final String TAG = "AddStudentActivity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.etName)
    public EditText etName;

    @Bind(R.id.etCountry)
    public EditText etCountry;

    @Bind(R.id.etParentNumber)
    public EditText etParentNumber;


    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.etRollNo)
    public EditText etRollNo;
    private int currentCountry;

    LeafManager leafManager;

    String group_id, team_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.title_add_student));

        group_id = getIntent().getStringExtra("group_id");
        team_id = getIntent().getStringExtra("team_id");

        leafManager = new LeafManager();

        currentCountry = 1;
        String[] str = getResources().getStringArray(R.array.array_country);
        etCountry.setText(str[0]);
    }
    private long lastClickTime = 0;
    @OnClick({R.id.btnCreateTeam, R.id.etCountry})
    public void onClick(View view) {
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime - lastClickTime > 1000) {
            lastClickTime = currentTime;
        }else {
            return;
        }
        Log.e(TAG,"Tap : ");

        switch (view.getId()) {
            case R.id.btnCreateTeam:
                if (isValid()) {
                    AddStudentReq addStudentReq = new AddStudentReq();
                    addStudentReq.studentName = etName.getText().toString();
                    addStudentReq.parentNumber = etParentNumber.getText().toString();
                    addStudentReq.rollNumber = etRollNo.getText().toString();
                    String[] str = getResources().getStringArray(R.array.array_country_values);
                    addStudentReq.countryCode = str[currentCountry - 1];
                    AppLog.e(TAG, "send data : " + addStudentReq);

                    showLoadingBar(progressBar,false);
                    //progressBar.setVisibility(View.VISIBLE);
                    leafManager.addStudent(this, group_id, team_id, addStudentReq);
                }
                break;
            case R.id.etCountry:
                SMBDialogUtils.showSMBSingleChoiceDialog(this, R.string.title_country, R.array.array_country, currentCountry - 1,
                        new SMBDailogClickListener(R.id.layout_country));
                break;
        }
    }

    private boolean isValid() {

        if (!isValueValid(etName)) {
            return false;
        } else if (currentCountry == 0) {
            Toast.makeText(this, getResources().getString(R.string.msg_selectcountry), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isValueValid(etParentNumber)) {
            return false;
        } else if (!isValueValid(etRollNo)) {
            return false;
        }
        return true;
    }

    public static class SMBDailogClickListener implements DialogInterface.OnClickListener {
        private int DIALOG_ID = -1;

        @Override
        public void onClick(DialogInterface dialog, int which) {

            ListView lw = ((AlertDialog) dialog).getListView();



        }

        public SMBDailogClickListener(int id) {
            DIALOG_ID = id;
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        if (progressBar != null)
            hideLoadingBar();
         //   progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_ADD_STUDENT:
                Toast.makeText(this, getResources().getString(R.string.toast_student_added), Toast.LENGTH_SHORT).show();
                LeafPreference.getInstance(this).setBoolean(LeafPreference.IS_STUDENT_ADDED,true);
                finish();
                break;
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        super.onFailure(apiId, msg);
        if (progressBar != null)
            hideLoadingBar();
            //progressBar.setVisibility(View.GONE);

        if (msg.contains("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onException(int apiId, String msg) {
        super.onException(apiId, msg);
        if (progressBar != null)
            hideLoadingBar();
        //    progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }
}
