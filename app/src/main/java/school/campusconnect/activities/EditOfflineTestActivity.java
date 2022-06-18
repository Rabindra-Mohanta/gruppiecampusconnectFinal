package school.campusconnect.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.GroupValidationError;
import school.campusconnect.datamodel.subjects.SubjectStaffResponse;
import school.campusconnect.datamodel.test_exam.OfflineTestRes;
import school.campusconnect.datamodel.test_exam.TestOfflineSubjectMark;
import school.campusconnect.network.LeafManager;
import school.campusconnect.views.SMBDialogUtils;

public class EditOfflineTestActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<GroupValidationError> {

    private static final String TAG = "AddOfflineTestActivity";

    @Bind(R.id.etTitle)
    EditText etTitle;

    @Bind(R.id.etResultDate)
    EditText etResultDate;

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.rvSubjects)
    RecyclerView rvSubjects;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;


    @Bind(R.id.iconBack)
    public ImageView iconBack;

    LeafManager leafManager;

    SubjectMarkAdapter adapter = new SubjectMarkAdapter();

    String groupId;
    String teamId;
    private ArrayList<SubjectStaffResponse.SubjectData> subjectList;
    OfflineTestRes.ScheduleTestData data;
    String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_offline_test);

        init();

        showData();

        LeafManager leafManager = new LeafManager();
        leafManager.getSubjectStaff(this, GroupDashboardActivityNew.groupId, teamId, "more");
    }

    private void showData() {
        if (data != null) {
            etTitle.setText(data.title);
            adapter.addAll(data.subjectMarksDetails);
            etResultDate.setText(data.resultDate);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if("admin".equalsIgnoreCase(role) || "teacher".equalsIgnoreCase(role)){
            getMenuInflater().inflate(R.menu.menu_test_offline_edit, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_schedule_exam) {
            if(data==null)
                return false;

            SMBDialogUtils.showSMBDialogOKCancel(this, getResources().getString(R.string.smb_delete_test), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressBar.setVisibility(View.VISIBLE);
                    leafManager.deleteOfflineTestList(EditOfflineTestActivity.this, groupId,teamId, data.offlineTestExamId);
                }
            });

            return true;
        }
        if(item.getItemId() == R.id.menu_schedule_exam_edit){
            Intent intent = new Intent(this, AddOfflineTestActivity.class);
            intent.putExtra("groupId", GroupDashboardActivityNew.groupId);
            intent.putExtra("teamId", teamId);
            intent.putExtra("isEdit", true);
            intent.putExtra("data", new Gson().toJson(data));
            intent.putExtra("subjectList", new Gson().toJson(subjectList));
            intent.putExtra("title", tvTitle.getText().toString());
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void init() {

        ButterKnife.bind(this);

        groupId = getIntent().getStringExtra("groupId");
        teamId = getIntent().getStringExtra("teamId");
        role = getIntent().getStringExtra("role");
        data = new Gson().fromJson(getIntent().getStringExtra("classData"), OfflineTestRes.ScheduleTestData.class);

        rvSubjects.setAdapter(adapter);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(false);
        tvTitle.setText(getIntent().getStringExtra("title"));
        setTitle("");

        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if("admin".equalsIgnoreCase(role) || "teacher".equalsIgnoreCase(role)){
            etTitle.setEnabled(true);
        }
        else
        {
            etTitle.setEnabled(false);
        }

        leafManager = new LeafManager();
    }

    public void hide_keyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (view == null) {
            view = new View(this);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @OnClick({ R.id.etResultDate})
    public void onClick(View view) {
        switch (view.getId()) {
          /*  case R.id.btnCreateClass:
                if (isValid()) {

                    if (!isConnectionAvailable()) {
                        showNoNetworkMsg();
                        return;
                    }

                    OfflineTestReq request = new OfflineTestReq();
                    request.title = etTitle.getText().toString();
                    request.resultDate = etResultDate.getText().toString();
                    request.subjectMarksDetails = adapter.getList();

                    AppLog.e(TAG, "request :" + new Gson().toJson(request));
                    SMBDialogUtils.showSMBDialogOK(this, "This will schedule new test/exam, kindly remove old test/exam if you are not required", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LeafManager leafManager = new LeafManager();
                            progressBar.setVisibility(View.VISIBLE);
                            leafManager.createOfflineTest(EditOfflineTestActivity.this, groupId, teamId, request);
                        }
                    });
                }
                break;
          */  case R.id.etResultDate: {


                if("admin".equalsIgnoreCase(role) || "teacher".equalsIgnoreCase(role))
                {
                }
                else
                {
                    return;
                }

           /*     DatePickerFragment fragment = DatePickerFragment.newInstance();

                fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                    @Override
                    public void onDateSelected(Calendar c) {
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        etResultDate.setText(format.format(c.getTime()));
                    }
                });
                fragment.show(getSupportFragmentManager(), "datepicker");
                fragment.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                fragment.setTitle(R.string.lbl_ResultDate);*/

                pickDate(
                        Calendar.getInstance().getTimeInMillis(),
                        cal -> {
                            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            etResultDate.setText(format.format(cal.getTime()));
                        });
                break;
            }
        }

    }

    public boolean isValid() {
        boolean valid = true;
        if (!isValueValidOnly(etTitle)) {
            Toast.makeText(this, getResources().getString(R.string.toast_please_enter_title), Toast.LENGTH_SHORT).show();
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
            case LeafManager.API_SUBJECT_STAFF:
                SubjectStaffResponse res = (SubjectStaffResponse) response;
                subjectList = res.getData();
               /* String strSubject[] = new String[subjectList.size()];
                for (int i = 0; i < subjectList.size(); i++) {
                    strSubject[i] = subjectList.get(i).name;
                }
                ArrayAdapter<String> spSubAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner_white, R.id.tvItem, strSubject);
                spSubject.setAdapter(spSubAdapter);*/
                break;
            case LeafManager.API_CREATE_OFFLINE_TEST:
                LeafPreference.getInstance(EditOfflineTestActivity.this).setBoolean("is_offline_test_added", true);
                finish();
                break;
            case LeafManager.API_REMOVE_OFFLINE_TEST:
                LeafPreference.getInstance(EditOfflineTestActivity.this).setBoolean("is_offline_test_added", true);
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

    public class SubjectMarkAdapter extends RecyclerView.Adapter<SubjectMarkAdapter.ViewHolder> {
        private Context mContext;
        ArrayList<TestOfflineSubjectMark> list = new ArrayList<>();

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_test_offline_edit, parent, false);
            return new ViewHolder(view);
        }

        public ArrayList<TestOfflineSubjectMark> getList() {
            return this.list;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
            holder.tvSubject.setText(list.get(i).subjectName);
            holder.tvDate.setText(list.get(i).date);
            holder.etDate.setText(list.get(i).date);
            holder.etSubject.setText(list.get(i).subjectName);
            holder.etMaxMarks.setText(list.get(i).maxMarks);
            holder.etMinMarks.setText(list.get(i).minMarks);
            holder.etStartTime.setText(list.get(i).startTime);
            holder.etEndTime.setText(list.get(i).endTime);
            if(list.get(i).viewIsVisible){
                list.get(i).viewIsVisible = false;
                holder.llMain.setVisibility(View.VISIBLE);
                holder.imgDelete.setImageResource(R.drawable.arrow_up);
            }else{
                holder.llMain.setVisibility(View.GONE);
                holder.imgDelete.setImageResource(R.drawable.arrow_down);
            }

            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.llMain.getVisibility()==View.VISIBLE){
                        holder.llMain.setVisibility(View.GONE);
                        holder.imgDelete.setImageResource(R.drawable.arrow_down);
                        list.get(i).viewIsVisible = false;
                    }else {
                        list.get(i).viewIsVisible = true;
                        holder.llMain.setVisibility(View.VISIBLE);
                        holder.imgDelete.setImageResource(R.drawable.arrow_up);
                    }
                }
            });

            holder.etMinMarks.setEnabled(false);
            holder.etMaxMarks.setEnabled(false);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void add(TestOfflineSubjectMark addSubjectData) {
            list.add(addSubjectData);
            notifyDataSetChanged();
        }

        public void addAll(ArrayList<TestOfflineSubjectMark> addSubjectData) {
            list.addAll(addSubjectData);
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.etDate)
            EditText etDate;

            @Bind(R.id.etStartTime)
            EditText etStartTime;

            @Bind(R.id.etMaxMarks)
            EditText etMaxMarks;

            @Bind(R.id.etMinMarks)
            EditText etMinMarks;

            @Bind(R.id.etSubject)
            EditText etSubject;

            @Bind(R.id.etEndTime)
            EditText etEndTime;
            @Bind(R.id.imgDelete)
            ImageView imgDelete;
            @Bind(R.id.llMain)
            LinearLayout llMain;
            @Bind(R.id.tvDate)
            TextView tvDate;
            @Bind(R.id.tvSubject)
            TextView tvSubject;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
