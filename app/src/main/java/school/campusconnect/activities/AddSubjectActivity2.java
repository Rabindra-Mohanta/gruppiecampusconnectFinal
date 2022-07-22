package school.campusconnect.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import school.campusconnect.R;
import school.campusconnect.adapters.SubjectAdapter;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.GroupValidationError;
import school.campusconnect.datamodel.staff.StaffResponse;
import school.campusconnect.datamodel.subjects.AddSubjectStaffReq;
import school.campusconnect.datamodel.subjects.SubjectResponse;
import school.campusconnect.datamodel.subjects.SubjectStaffResponse;
import school.campusconnect.fragments.SubjectListFragment2;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.views.SMBDialogUtils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddSubjectActivity2 extends BaseActivity implements LeafManager.OnAddUpdateListener<GroupValidationError> {

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

    @Bind(R.id.txtEmpty)
    TextView txtEmpty;

    LeafManager leafManager;
    private String team_id;
    StaffAdapter adapter;
    boolean isEdit;
    boolean isAssign;
    private SubjectStaffResponse.SubjectData subjectData;
    private ArrayList<String> selectedStaffIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject2);

        init();

        LeafManager leafManager = new LeafManager();
        showLoadingBar(progressBar,true);
       // progressBar.setVisibility(View.VISIBLE);
        leafManager.getStaff(this, GroupDashboardActivityNew.groupId);
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
                  //  progressBar.setVisibility(View.VISIBLE);
                    leafManager.deleteSubjectStaff(AddSubjectActivity2.this, GroupDashboardActivityNew.groupId,team_id, subjectData.getSubjectId());
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_add_subject)+" - ("+getIntent().getStringExtra("className")+")");
        leafManager = new LeafManager();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            team_id = bundle.getString("team_id");
            isEdit = bundle.getBoolean("is_edit");
            isAssign = bundle.getBoolean("isAssign");
            if (isEdit) {
                setTitle(getResources().getString(R.string.lbl_update_subject)+" - ("+getIntent().getStringExtra("className")+")");
                subjectData = new Gson().fromJson(bundle.getString("data"), SubjectStaffResponse.SubjectData.class);

                for (int i = 0; i < subjectData.staffName.size(); i++) {
                    selectedStaffIds.add(subjectData.staffName.get(i).staffId);
                }

                etName.setText(subjectData.getName());
                btnCreateClass.setText(getResources().getString(R.string.lbl_update));
            }
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
                        if (isAssign)
                        {
                            btnCreateClass.setEnabled(false);
                            AddSubjectStaffReq request = new AddSubjectStaffReq();
                            request.setSubjectName(etName.getText().toString());
                            request.setStaffId(adapter.getSelectedList());
                            request.setSubjectId(subjectData.subjectId);

                            showLoadingBar(progressBar,false);
                            // progressBar.setVisibility(View.VISIBLE);
                            AppLog.e(TAG, "request :" + request);
                            leafManager.assignTeacher(this, GroupDashboardActivityNew.groupId, team_id, request);
                        }
                        else
                        {
                            btnCreateClass.setEnabled(false);
                            AddSubjectStaffReq request = new AddSubjectStaffReq();
                            request.setSubjectName(etName.getText().toString());
                            request.setStaffId(adapter.getSelectedList());
                            showLoadingBar(progressBar,false);
                            // progressBar.setVisibility(View.VISIBLE);
                            AppLog.e(TAG, "request :" + request);
                            leafManager.updateSubjectStaff(this, GroupDashboardActivityNew.groupId, team_id,subjectData.getSubjectId(), request);
                        }
                    }
                    else
                    {
                        btnCreateClass.setEnabled(false);
                        AddSubjectStaffReq request = new AddSubjectStaffReq();
                        request.setSubjectName(etName.getText().toString());
                        request.setStaffId(adapter.getSelectedList());
                        showLoadingBar(progressBar,false);
                        //progressBar.setVisibility(View.VISIBLE);
                        AppLog.e(TAG, "request :" + request);
                        leafManager.addSubjectStaff(this, GroupDashboardActivityNew.groupId, team_id, request);
                    }
                }
                break;
        }
    }

    public boolean isValid() {
        boolean valid = true;
        if (!isValueValid(etName)) {
            valid = false;
        } else if (adapter.getSelectedList().size() == 0) {
            Toast.makeText(this, getResources().getString(R.string.toast_add_one_teacher), Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        if (progressBar != null)
            hideLoadingBar();
           // progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_ADD_SUBJECT_STAFF:
            case LeafManager.API_UPDATE_SUBJECT_STAFF:
            case LeafManager.API_DELETE_SUBJECT_STAFF:
            case LeafManager.API_ASSIGN_TEACHER:
                finish();
                break;
            case LeafManager.API_STAFF:
                hideLoadingBar();
               // progressBar.setVisibility(View.GONE);
                StaffResponse res = (StaffResponse) response;
                List<StaffResponse.StaffData> result = res.getData();
                AppLog.e(TAG, "ClassResponse " + result);

                if (result != null) {
                    for (int i = 0; i < result.size(); i++) {
                        if (selectedStaffIds.contains(result.get(i).staffId)) {
                            result.get(i).isSelected = true;
                        }
                    }
                    adapter = new StaffAdapter(result);
                    rvSubjects.setAdapter(adapter);
                }
                break;

        }
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<GroupValidationError> error) {
        if (progressBar != null)
            hideLoadingBar();
           // progressBar.setVisibility(View.GONE);

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
         //   progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }

    public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.ViewHolder> {
        List<StaffResponse.StaffData> list;
        private Context mContext;

        public StaffAdapter(List<StaffResponse.StaffData> list) {
            this.list = list;
        }

        @Override
        public StaffAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff_select, parent, false);
            return new StaffAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final StaffAdapter.ViewHolder holder, final int position) {
            final StaffResponse.StaffData item = list.get(position);
            holder.chkName.setText(item.getName());
            if (item.isSelected) {
                holder.chkName.setChecked(true);
            } else {
                holder.chkName.setChecked(false);
            }
            holder.chkName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.isSelected = holder.chkName.isChecked();
                    notifyItemChanged(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    txtEmpty.setText(getResources().getString(R.string.txt_no_staff));
                } else {
                    txtEmpty.setText("");
                }

                return list.size();
            } else {
                txtEmpty.setText(getResources().getString(R.string.txt_no_staff));
                return 0;
            }

        }

        public ArrayList<String> getSelectedList() {
            ArrayList<String> selected = new ArrayList<>();
            if (list == null) {
                return selected;
            }
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isSelected)
                    selected.add(list.get(i).getStaffId());
            }
            return selected;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.chkName)
            CheckBox chkName;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

            }
        }
    }

}
