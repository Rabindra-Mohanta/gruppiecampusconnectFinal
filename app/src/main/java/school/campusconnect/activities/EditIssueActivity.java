package school.campusconnect.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.GroupValidationError;
import school.campusconnect.datamodel.issue.IssueListResponse;
import school.campusconnect.datamodel.issue.RegisterIssueReq;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.views.SMBDialogUtils;

public class EditIssueActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<GroupValidationError> {

    private static final String TAG = "EditIssueActivity";
    @Bind(R.id.etName)
    EditText etName;
    @Bind(R.id.etPhone)
    EditText etPhone;
    @Bind(R.id.etDesig)
    EditText etDesig;
    @Bind(R.id.etName2)
    EditText etName2;
    @Bind(R.id.etPhone2)
    EditText etPhone2;
    @Bind(R.id.etDesig2)
    EditText etDesig2;


    @Bind(R.id.btnCreateClass)
    Button btnCreateClass;

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    LeafManager leafManager;

    IssueListResponse.IssueData issueData;

    boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_issue);

        init();
    }

    private void init() {
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);

        leafManager = new LeafManager();

        issueData = new Gson().fromJson(getIntent().getStringExtra("data"), IssueListResponse.IssueData.class);
        setTitle(issueData.issue + " - " + issueData.jurisdiction);

        etName.setText(issueData.departmentUser.name);
        etPhone.setText(issueData.departmentUser.phone);
        etDesig.setText(issueData.departmentUser.designation);

        etName2.setText(issueData.partyUser.name);
        etPhone2.setText(issueData.partyUser.phone);
        etDesig2.setText(issueData.partyUser.designation);

        etName.setEnabled(false);
        etName.setTextColor(getResources().getColor(R.color.grey));
        etPhone.setEnabled(false);
        etPhone.setTextColor(getResources().getColor(R.color.grey));
        etDesig.setEnabled(false);
        etDesig.setTextColor(getResources().getColor(R.color.grey));
        etName2.setEnabled(false);
        etName2.setTextColor(getResources().getColor(R.color.grey));
        etPhone2.setEnabled(false);
        etPhone2.setTextColor(getResources().getColor(R.color.grey));
        etDesig2.setEnabled(false);
        etPhone2.setTextColor(getResources().getColor(R.color.grey));

    }

    private static final long MIN_CLICK_INTERVAL = 1000; //in millis
    private long lastClickTime = 0;

    @OnClick({R.id.btnCreateClass})
    public void onClick(View view) {
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime - lastClickTime > MIN_CLICK_INTERVAL) {
            lastClickTime = currentTime;
        } else {
            return;
        }
        Log.e(TAG, "Tap : ");
        switch (view.getId()) {
            case R.id.btnCreateClass:

                if (!isEdit) {
                    isEdit = true;
                    etName.setEnabled(true);
                    etPhone.setEnabled(true);
                    etDesig.setEnabled(true);
                    etName2.setEnabled(true);
                    etPhone2.setEnabled(true);
                    etDesig2.setEnabled(true);

                    etName.setTextColor(getResources().getColor(R.color.white));
                    etPhone.setTextColor(getResources().getColor(R.color.white));
                    etDesig.setTextColor(getResources().getColor(R.color.white));
                    etName2.setTextColor(getResources().getColor(R.color.white));
                    etPhone2.setTextColor(getResources().getColor(R.color.white));
                    etDesig2.setTextColor(getResources().getColor(R.color.white));

                    btnCreateClass.setText("Save");
                    return;
                }
                isEdit = false;
                etName.setEnabled(false);
                etPhone.setEnabled(false);
                etDesig.setEnabled(false);
                etName2.setEnabled(false);
                etPhone2.setEnabled(false);
                etDesig2.setEnabled(false);
                btnCreateClass.setText("Edit");

                if (isValid()) {

                    if (!isConnectionAvailable()) {
                        showNoNetworkMsg();
                        return;
                    }

                    issueData.departmentUser.name = etName.getText().toString();
                    issueData.departmentUser.phone = "+91"+etPhone.getText().toString();
                    issueData.departmentUser.designation = etDesig.getText().toString();

                    issueData.partyUser.name = etName2.getText().toString();
                    issueData.partyUser.phone = "+91"+etPhone2.getText().toString();
                    issueData.partyUser.designation = etDesig2.getText().toString();

                    progressBar.setVisibility(View.VISIBLE);
                    AppLog.e(TAG, "request :" + new Gson().toJson(issueData));
                    leafManager.editIssue(this, GroupDashboardActivityNew.groupId, issueData.issueId, issueData);

                }
                break;
        }
    }

    public boolean isValid() {
        boolean valid = true;
        if (!isValueValid(etName)) {
            valid = false;
        } else if (!isValueValid(etPhone)) {
            valid = false;
        }
        if (!isValueValid(etName2)) {
            valid = false;
        } else if (!isValueValid(etPhone2)) {
            valid = false;
        }
        return valid;
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_ADD_TASK_FORCE:
                finish();
                break;
            case LeafManager.API_ISSUE_REMOE:
                finish();
                break;

        }
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<GroupValidationError> error) {
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

        if (error.status.equals("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            if (apiId == LeafManager.API_ID_DELETE_TEAM) {
                GroupValidationError groupValidationError = (GroupValidationError) error;
                Toast.makeText(this, groupValidationError.message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, error.title, Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Override
    public void onException(int apiId, String msg) {
        super.onException(apiId, msg);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete_issue, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuDelete:
                delete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void delete() {
        SMBDialogUtils.showSMBDialogOKCancel(this, getResources().getString(R.string.dialog_are_you_want_to_delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBar.setVisibility(View.VISIBLE);
                LeafManager leafManager = new LeafManager();
                leafManager.deleteIssue(EditIssueActivity.this, GroupDashboardActivityNew.groupId,issueData.issueId);
            }
        });
    }
}
