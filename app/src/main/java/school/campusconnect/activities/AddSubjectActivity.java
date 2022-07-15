package school.campusconnect.activities;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.adapters.SubjectAdapter;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.GroupValidationError;
import school.campusconnect.datamodel.subjects.SubjectResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.views.SMBDialogUtils;

public class AddSubjectActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<GroupValidationError> {

    private static final String TAG = "CreateTeamActivity";
    @Bind(R.id.etName)
    EditText etName;

    @Bind(R.id.btnCreateClass)
    Button btnCreateClass;

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.rvSubjects)
    RecyclerView rvSubjects;



    LeafManager leafManager;

    boolean isEdit;
    private SubjectResponse.SubjectData subjectData;

    SubjectAdapter adapter = new SubjectAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (isEdit) {
            getMenuInflater().inflate(R.menu.menu_edit, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuDelete) {
            if (!isConnectionAvailable()) {
                showNoNetworkMsg();
                return true;
            }
            if (subjectData == null)
                return true;

            SMBDialogUtils.showSMBDialogOKCancel(this, getResources().getString(R.string.smb_delete_subject), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showLoadingBar(progressBar,false);
                   // progressBar.setVisibility(View.VISIBLE);
                    leafManager.deleteSubjects(AddSubjectActivity.this, GroupDashboardActivityNew.groupId, subjectData.getSubjectId());
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        ButterKnife.bind(this);

        rvSubjects.setAdapter(adapter);

        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_add_subject));
        leafManager = new LeafManager();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isEdit = bundle.getBoolean("is_edit");
            if (isEdit) {
                subjectData = new Gson().fromJson(bundle.getString("data"), SubjectResponse.SubjectData.class);
                etName.setText(subjectData.getName());
                adapter.addList(subjectData.subjects);
                btnCreateClass.setText(getResources().getString(R.string.lbl_update));
            }
        }

        if(!isEdit){
            adapter.add("Maths");
            adapter.add("Science");
            adapter.add("Eng");
        }
    }

    @OnClick({R.id.btnCreateClass})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCreateClass:
                if (isValid()) {

                    if (!isConnectionAvailable()) {
                        showNoNetworkMsg();
                        return;
                    }
                    if (isEdit) {
                        SubjectResponse.SubjectData request = new SubjectResponse.SubjectData();
                        request.name = etName.getText().toString();
                        request.subjects = adapter.getList();
                        showLoadingBar(progressBar,false);
                        // progressBar.setVisibility(View.VISIBLE);
                        AppLog.e(TAG, "request :" + request);
                        leafManager.editSubject(this, GroupDashboardActivityNew.groupId, subjectData.subjectId, request);
                    } else {
                        SubjectResponse.SubjectData request = new SubjectResponse.SubjectData();
                        request.name = etName.getText().toString();
                        request.subjects = adapter.getList();
                        showLoadingBar(progressBar,false);
                        // progressBar.setVisibility(View.VISIBLE);
                        AppLog.e(TAG, "request :" + request);
                        leafManager.addSubject(this, GroupDashboardActivityNew.groupId, request);
                        // }

                    }
                }
                break;
        }
    }

    public boolean isValid() {
        boolean valid = true;
        if (!isValueValid(etName)) {
            valid = false;
        } else if (adapter.getList().size()==0) {
            Toast.makeText(this, getResources().getString(R.string.toast_add_one_subject), Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        if (progressBar != null)
            hideLoadingBar();
     //       progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_SUBJECTS_ADD:
                finish();
                break;
            case LeafManager.API_SUBJECTS_UPDATE:
                //LeafPreference.getInstance(this).setBoolean(LeafPreference.ISTEAMUPDATED, true);
                finish();
                break;
            case LeafManager.API_SUBJECTS_DELETE:
                //LeafPreference.getInstance(this).setBoolean(LeafPreference.ISTEAMUPDATED, true);
                finish();
                break;
        }
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<GroupValidationError> error) {
        if (progressBar != null)
            hideLoadingBar();
        //       progressBar.setVisibility(View.GONE);

        if (error.status.equals("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            if (apiId == LeafManager.API_SUBJECTS_DELETE) {
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
            hideLoadingBar();
        //       progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }
}
