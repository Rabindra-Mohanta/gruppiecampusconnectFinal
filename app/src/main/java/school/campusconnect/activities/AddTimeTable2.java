package school.campusconnect.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import school.campusconnect.R;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.GroupValidationError;
import school.campusconnect.datamodel.staff.StaffResponse;
import school.campusconnect.datamodel.subjects.AddSubjectStaffReq;
import school.campusconnect.datamodel.time_table.SubStaffTTReq;
import school.campusconnect.datamodel.time_table.SubjectStaffTTResponse;
import school.campusconnect.datamodel.time_table.TimeTableList2Response;
import school.campusconnect.fragments.TimeTableListFragment2;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddTimeTable2 extends BaseActivity implements LeafManager.OnAddUpdateListener<GroupValidationError> {

    private static final String TAG = "CreateTeamActivity";
    @Bind(R.id.spDay)
    Spinner spDay;

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.et_period)
    EditText et_period;

    @Bind(R.id.spSubject)
    Spinner spSubject;

    @Bind(R.id.spStaff)
    Spinner spStaff;

    @Bind(R.id.rvTimeTable)
    RecyclerView rvTimeTable;


    LeafManager leafManager;
    private String team_id;
    private String day = "";
    private String period = "";
    private ArrayList<SubjectStaffTTResponse.SubjectStaffTTData> subjStaffList;
    private ArrayList<SubjectStaffTTResponse.SubjectStaffTTData> subjStaffListDialog;

    boolean isAddStaffApiForEdit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tt);

        init();

    }

    private void getSubjectStaff(SubStaffTTReq subStaffTTReq) {
        LeafManager leafManager = new LeafManager();
        progressBar.setVisibility(View.VISIBLE);
        leafManager.getSubjectStaffTT(this, GroupDashboardActivityNew.groupId, team_id, subStaffTTReq);
    }


    private void init() {
        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);
        setBackEnabled(true);

        leafManager = new LeafManager();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            team_id = bundle.getString("team_id");
            setTitle(bundle.getString("team_name"));
        }
        String[] days = new String[7];
        days[0] = "Monday";
        days[1] = "Tuesday";
        days[2] = "Wednesday";
        days[3] = "Thursday";
        days[4] = "Friday";
        days[5] = "Saturday";
        days[6] = "Sunday";
        ArrayAdapter<String> bloodGrpAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, days);
        spDay.setAdapter(bloodGrpAdapter);

        reset();

        spDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                day = (position + 1) + "";
                callCurrentTimeTableApi();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        et_period.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                period = et_period.getText().toString();
                callSubjectStaffApi();
            }
        });


    }

    private void callCurrentTimeTableApi() {
        LeafManager leafManager = new LeafManager();
        progressBar.setVisibility(View.VISIBLE);
        leafManager.getTTNewDayWise(this, GroupDashboardActivityNew.groupId, team_id, day);
    }


    private void reset() {

        ArrayAdapter<String> spSubAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner2, R.id.tvItem, new String[]{"Select"});
        spSubject.setAdapter(spSubAdapter);

        ArrayAdapter<String> spStaffAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner2, R.id.tvItem, new String[]{"Select"});
        spStaff.setAdapter(spStaffAdapter);


    }

    private void callSubjectStaffApi() {
        isAddStaffApiForEdit = false;
        AppLog.e(TAG, "callSubjectStaffApi : day : " + day + ", period :" + period);
        /*AppLog.e(TAG,"callSubjectStaffApi : day of week: "+Calendar.getInstance().get(Calendar.DAY_OF_WEEK));*/
        if (!TextUtils.isEmpty(day) && !TextUtils.isEmpty(period)) {
            reset();
            getSubjectStaff(new SubStaffTTReq(day, period));
        }
    }

    @OnClick({R.id.btnAdd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAdd:
                if (isValid()) {

                    if (!isConnectionAvailable()) {
                        showNoNetworkMsg();
                        return;
                    }
                    SubStaffTTReq request = new SubStaffTTReq(day, period);
                    AppLog.e(TAG, "request :" + request);
                    String subject_with_staff_id = subjStaffList.get(spSubject.getSelectedItemPosition()).getSubjectWithStaffId();
                    String staff_id = subjStaffList.get(spSubject.getSelectedItemPosition()).getSubjectWithStaffs().get(spStaff.getSelectedItemPosition()).getStaffId();

                    progressBar.setVisibility(View.VISIBLE);
                    leafManager.addSubjectStaffTT(this, GroupDashboardActivityNew.groupId, team_id, subject_with_staff_id, staff_id, request);

                }
                break;
        }
    }

    public boolean isValid() {
        boolean valid = true;
        if (!isValueValid(et_period)) {
            valid = false;
        } else if (subjStaffList == null) {
            Toast.makeText(this, "Please Select Subject and Staff", Toast.LENGTH_SHORT).show();
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
            case LeafManager.API_TT_ADD_SUB_Staff:
                Toast.makeText(this, "Successfully Added Time Table", Toast.LENGTH_SHORT).show();
                callCurrentTimeTableApi();
                break;
            case LeafManager.API_TT_ADD:
                progressBar.setVisibility(View.GONE);
                SubjectStaffTTResponse res = (SubjectStaffTTResponse) response;
                if(isAddStaffApiForEdit){
                    subjStaffListDialog = res.getData();
                    setSubjectSpinnerDialog();
                }else {
                    subjStaffList = res.getData();
                    AppLog.e(TAG, "ClassResponse " + subjStaffList);
                    setSubjectSpinner();
                }
                break;
            case LeafManager.API_TT_LIST_2:
                progressBar.setVisibility(View.GONE);
                TimeTableList2Response res2 = (TimeTableList2Response) response;
                List<TimeTableList2Response.TimeTableData2> result = res2.getData();
                AppLog.e(TAG, "ClassResponse " + result);
                if (res2.getData() != null && res2.getData().size() > 0 && res2.getData().get(0).getSessions() != null) {
                    rvTimeTable.setAdapter(new SessionAdapter(res2.getData().get(0).getSessions()));
                    et_period.setText((res2.getData().get(0).getSessions().size() + 1) + "");
                } else {
                    rvTimeTable.setAdapter(new SessionAdapter(null));
                    et_period.setText("1");
                }
                break;
        }
    }

    private void setSubjectSpinner() {
        String[] subject = new String[subjStaffList.size()];

        for (int i = 0; i < subjStaffList.size(); i++) {
            subject[i] = subjStaffList.get(i).subjectName;
        }
        ArrayAdapter<String> bloodGrpAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner2, R.id.tvItem, subject);
        spSubject.setAdapter(bloodGrpAdapter);

        spSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setStaffSpinner(subjStaffList.get(position).getSubjectWithStaffs());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setStaffSpinner(ArrayList<SubjectStaffTTResponse.SubjectWithStaffs> subjectWithStaffs) {
        String[] staff = new String[subjectWithStaffs.size()];

        for (int i = 0; i < subjectWithStaffs.size(); i++) {
            staff[i] = subjectWithStaffs.get(i).getStaffName();
        }
        ArrayAdapter<String> bloodGrpAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner2, R.id.tvItem, staff);
        spStaff.setAdapter(bloodGrpAdapter);
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<GroupValidationError> error) {
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

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
            progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }

    public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {
        ArrayList<TimeTableList2Response.SessionsTimeTable> list;
        private Context mContext;

        public SessionAdapter(ArrayList<TimeTableList2Response.SessionsTimeTable> list) {
            this.list = list;
        }

        @Override
        public SessionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_time_table, parent, false);
            return new SessionAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SessionAdapter.ViewHolder holder, final int position) {
            final TimeTableList2Response.SessionsTimeTable item = list.get(position);
            holder.et_period.setText(item.getPeriod());

            holder.spSubject.setAdapter(new ArrayAdapter<String>(AddTimeTable2.this, R.layout.item_spinner2, R.id.tvItem, new String[]{item.getSubjectName()}));

            holder.spStaff.setAdapter(new ArrayAdapter<String>(AddTimeTable2.this, R.layout.item_spinner2, R.id.tvItem, new String[]{item.getTeacherName()}));

            holder.imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEdit(item);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                return list.size();
            } else {
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.et_period)
            EditText et_period;

            @Bind(R.id.spSubject)
            Spinner spSubject;

            @Bind(R.id.spStaff)
            Spinner spStaff;

            @Bind(R.id.imgEdit)
            ImageView imgEdit;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    EditText et_period_dialog;

    Spinner spSubject_dialog;

    Spinner spStaff_dialog;

    private void showEdit(TimeTableList2Response.SessionsTimeTable item) {
        final Dialog dialog = new Dialog(this, R.style.AppDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.edit_tt_dialog);
        et_period_dialog = dialog.findViewById(R.id.et_period);
        et_period_dialog.setText(item.getPeriod());
        spSubject_dialog = dialog.findViewById(R.id.spSubject);
        spStaff_dialog = dialog.findViewById(R.id.spStaff);
        dialog.findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spSubject_dialog.getSelectedItemPosition()==-1 || spStaff_dialog.getSelectedItemPosition()==-1){
                    Toast.makeText(AddTimeTable2.this, "Please Select Subject and Staff", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isConnectionAvailable()) {
                    showNoNetworkMsg();
                    return;
                }
                SubStaffTTReq request = new SubStaffTTReq(day, item.getPeriod());
                AppLog.e(TAG, "request :" + request);
                String subject_with_staff_id = subjStaffListDialog.get(spSubject_dialog.getSelectedItemPosition()).getSubjectWithStaffId();
                String staff_id = subjStaffListDialog.get(spSubject_dialog.getSelectedItemPosition()).getSubjectWithStaffs().get(spStaff_dialog.getSelectedItemPosition()).getStaffId();

                progressBar.setVisibility(View.VISIBLE);
                leafManager.addSubjectStaffTT(AddTimeTable2.this, GroupDashboardActivityNew.groupId, team_id, subject_with_staff_id, staff_id, request);
                dialog.dismiss();
            }
        });
        dialog.show();
        isAddStaffApiForEdit = true;
        SubStaffTTReq subStaffTTReq = new SubStaffTTReq();
        subStaffTTReq.setDay(day);
        subStaffTTReq.setPeriod(item.getPeriod());
        getSubjectStaff(subStaffTTReq);
    }


    private void setSubjectSpinnerDialog() {
        String[] subject = new String[subjStaffListDialog.size()];

        for (int i = 0; i < subjStaffListDialog.size(); i++) {
            subject[i] = subjStaffListDialog.get(i).subjectName;
        }
        ArrayAdapter<String> bloodGrpAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner2, R.id.tvItem, subject);
        spSubject_dialog.setAdapter(bloodGrpAdapter);

        spSubject_dialog.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setStaffSpinnerDialog(subjStaffListDialog.get(position).getSubjectWithStaffs());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setStaffSpinnerDialog(ArrayList<SubjectStaffTTResponse.SubjectWithStaffs> subjectWithStaffs) {
        String[] staff = new String[subjectWithStaffs.size()];

        for (int i = 0; i < subjectWithStaffs.size(); i++) {
            staff[i] = subjectWithStaffs.get(i).getStaffName();
        }
        ArrayAdapter<String> bloodGrpAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner2, R.id.tvItem, staff);
        spStaff_dialog.setAdapter(bloodGrpAdapter);
    }

}
