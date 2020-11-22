package school.campusconnect.activities;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.AttendanceListRes;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.EditAttendanceReq;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;

public class EditAttendanceActivity extends BaseActivity {

    private static final String TAG = "EditAttendanceActivity";
    @Bind(R.id.toolbar)
    Toolbar mToolBar;

    @Bind(R.id.edtName)
    EditText edtName;

    @Bind(R.id.edtRollNo)
    EditText edtRollNo;



    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private LeafPreference leafPreference;
    private LeafManager leafManager;

    AttendanceListRes.AttendanceData attendanceData;
    private String groupId;
    private String teamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_attendance);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle("Edit Attendance");

        init_();

        edtRollNo.setText(attendanceData.rollNumber);
        edtName.setText(attendanceData.name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuDelete) {
            if (!isConnectionAvailable()) {
                showNoNetworkMsg();
                return true;
            }
            if (attendanceData == null)
                return true;

            progressBar.setVisibility(View.VISIBLE);
            leafManager.removeAttendance(this, groupId, teamId, attendanceData.id);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init_() {
        leafPreference = LeafPreference.getInstance(this);
        leafManager = new LeafManager();
        Bundle bundle = getIntent().getExtras();
        groupId = bundle.getString("group_id", "");
        teamId = bundle.getString("team_id", "");
        attendanceData = new Gson().fromJson(bundle.getString("data", "{}"), AttendanceListRes.AttendanceData.class);
    }

    @OnClick({R.id.btnCreateTeam})
    public void onClick(View view) {
        if (!isConnectionAvailable()) {
            showNoNetworkMsg();
            return;
        }

        if (attendanceData == null)
            return;

        if (isValueValid(edtName)) {
            EditAttendanceReq editReq = new EditAttendanceReq();

            if(!TextUtils.isEmpty(edtRollNo.getText().toString()))
                editReq.rollNumber = edtRollNo.getText().toString();

            editReq.studentName = edtName.getText().toString();

            AppLog.e(TAG,"EditAttendanceReq :"+new Gson().toJson(editReq));
            progressBar.setVisibility(View.VISIBLE);
            leafManager.editAttendance(this, groupId, teamId, attendanceData.id, editReq);
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        switch (apiId) {
            case LeafManager.API_EDIT_ATTENDANCE:
                leafPreference.setBoolean(LeafPreference.EDIT_ATTENDANCE, true);
                finish();
                break;
            case LeafManager.API_REMOVE_ATTENDANCE:
                leafPreference.setBoolean(LeafPreference.EDIT_ATTENDANCE, true);
                finish();
                break;
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        AppLog.e("onFailure", "Failure");
        if (msg.contains("401") || msg.contains("Unauthorized")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
