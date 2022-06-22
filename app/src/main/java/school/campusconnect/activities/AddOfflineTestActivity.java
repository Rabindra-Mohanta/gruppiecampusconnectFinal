package school.campusconnect.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.GroupValidationError;
import school.campusconnect.datamodel.VideoOfflineObject;
import school.campusconnect.datamodel.subjects.SubjectStaffResponse;
import school.campusconnect.datamodel.syllabus.SyllabusModelReq;
import school.campusconnect.datamodel.test_exam.OfflineTestReq;
import school.campusconnect.datamodel.test_exam.OfflineTestRes;
import school.campusconnect.datamodel.test_exam.TestOfflineSubjectMark;
import school.campusconnect.fragments.DatePickerFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BackgroundVideoUploadChapterService;
import school.campusconnect.views.SMBDialogUtils;

public class AddOfflineTestActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<GroupValidationError> {

    private static final String TAG = "AddOfflineTestActivity";

    @Bind(R.id.etDate)
    EditText etDate;

    @Bind(R.id.etTitle)
    EditText etTitle;

    @Bind(R.id.etStartTime)
    EditText etStartTime;

    @Bind(R.id.etMaxMarks)
    EditText etMaxMarks;

    @Bind(R.id.etMinMarks)
    EditText etMinMarks;

    @Bind(R.id.spSubject)
    Spinner spSubject;

    @Bind(R.id.etEndTime)
    EditText etEndTime;

    @Bind(R.id.etResultDate)
    EditText etResultDate;

    @Bind(R.id.imgAdd)
    ImageView imgAdd;

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.rvSubjects)
    RecyclerView rvSubjects;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;

    @Bind(R.id.llDate)
    public LinearLayout llDate;

    @Bind(R.id.llMark)
    public LinearLayout llMark;

    @Bind(R.id.iconBack)
    public ImageView iconBack;

    @Bind(R.id.imgHome)
    public ImageView imgHome;
    @Bind(R.id.btnCreateClass)
    public Button btnCreateClass;

    LeafManager leafManager;


    String groupId;
    String teamId;
    private ArrayList<SubjectStaffResponse.SubjectData> subjectList;
    SubjectMarkAdapter adapter;

    boolean isEdit = false;

    OfflineTestRes.ScheduleTestData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offline_test);

        init();

    }
    String oldValue = "";
    private void init() {

        ButterKnife.bind(this);

        groupId = getIntent().getStringExtra("groupId");
        teamId = getIntent().getStringExtra("teamId");
        isEdit = getIntent().getBooleanExtra("isEdit", false);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(false);
        tvTitle.setText(getIntent().getStringExtra("title"));
        setTitle("");

        etMinMarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldValue = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputMarkStr = etMinMarks.getText().toString();
                if (!inputMarkStr.isEmpty()) {
                    if (etMaxMarks.getText().toString().isEmpty()) {
                        Toast.makeText(AddOfflineTestActivity.this, "Enter Max Marks before Entering Min Marks", Toast.LENGTH_SHORT).show();
                        etMinMarks.setText("");
                        return;
                    }else if (Float.parseFloat(inputMarkStr) > Float.parseFloat(etMaxMarks.getText().toString())) {
                        etMinMarks.setText(oldValue);
                        if (oldValue.length() > 0) {
                            etMinMarks.setSelection(oldValue.length() - 1);
                        }
                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        spSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0)
                {
                    llDate.setVisibility(View.GONE);
                    llMark.setVisibility(View.GONE);
                }
                else
                {
                    llDate.setVisibility(View.VISIBLE);
                    llMark.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (isEdit) {
            btnCreateClass.setText(getResources().getString(R.string.lbl_update));
            data = new Gson().fromJson(getIntent().getStringExtra("data"), OfflineTestRes.ScheduleTestData.class);
            subjectList = new Gson().fromJson(getIntent().getStringExtra("subjectList"), new TypeToken<ArrayList<SubjectStaffResponse.SubjectData>>() {
            }.getType());

            String strSubject[] = new String[subjectList.size()+1];
            strSubject[0] = getResources().getString(R.string.lbl_select_subject);
            for (int i = 0; i < subjectList.size(); i++) {
                strSubject[i+1] = subjectList.get(i).name;
            }
            ArrayAdapter<String> spSubAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner_white, R.id.tvItem, strSubject);
            spSubject.setAdapter(spSubAdapter);

            adapter = new SubjectMarkAdapter();
            rvSubjects.setAdapter(adapter);

            etTitle.setText(data.title);
            etResultDate.setText(data.resultDate);

            // show last item
            TestOfflineSubjectMark lastItem = data.subjectMarksDetails.get(data.subjectMarksDetails.size() - 1);
            etDate.setText(lastItem.date);
            int index = 0;
            for (int i=0;i<subjectList.size();i++){
                if(subjectList.get(i).subjectId.equals(lastItem.subjectId)){
                    index=i;
                }
            }
            spSubject.setSelection(index);

            llDate.setVisibility(View.VISIBLE);
            llMark.setVisibility(View.VISIBLE);

            etStartTime.setText(lastItem.startTime);
            etEndTime.setText(lastItem.endTime);
            etMinMarks.setText(lastItem.minMarks);
            etMaxMarks.setText(lastItem.maxMarks);

            for (int j=0;j<data.subjectMarksDetails.size()-1;j++){
                adapter.add(data.subjectMarksDetails.get(j));
            }

        } else {
            showLoadingBar(progressBar,true);
            LeafManager leafManager = new LeafManager();
            leafManager.getSubjectStaff(this, GroupDashboardActivityNew.groupId, teamId, "more");
        }

        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddOfflineTestActivity.this, GroupDashboardActivityNew.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        leafManager = new LeafManager();

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etDate.getText().toString().trim())) {
                    Toast.makeText(AddOfflineTestActivity.this, getResources().getString(R.string.toast_select_date), Toast.LENGTH_SHORT).show();
                } else if (spSubject.getSelectedItemPosition() == 0) {
                    Toast.makeText(AddOfflineTestActivity.this, getResources().getString(R.string.toast_please_select_subject), Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(etStartTime.getText().toString().trim())) {
                    Toast.makeText(AddOfflineTestActivity.this, getResources().getString(R.string.toast_please_select_start_time), Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(etEndTime.getText().toString().trim())) {
                    Toast.makeText(AddOfflineTestActivity.this, getResources().getString(R.string.toast_please_select_end_time), Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(etMaxMarks.getText().toString().trim())) {
                    Toast.makeText(AddOfflineTestActivity.this, getResources().getString(R.string.toast_please_enter_max_mark), Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(etMinMarks.getText().toString().trim())) {
                    Toast.makeText(AddOfflineTestActivity.this, getResources().getString(R.string.toast_please_enter_min_mark), Toast.LENGTH_SHORT).show();
                } else {
                    TestOfflineSubjectMark t1 = new TestOfflineSubjectMark();
                    t1.date = etDate.getText().toString();
                    t1.subjectName = spSubject.getSelectedItem().toString();
                    t1.subjectId = subjectList.get(spSubject.getSelectedItemPosition()).subjectId;
                    t1.maxMarks = etMaxMarks.getText().toString();
                    t1.minMarks = etMinMarks.getText().toString();
                    t1.startTime = etStartTime.getText().toString();
                    t1.endTime = etEndTime.getText().toString();
                    if (adapter != null) {
                        llDate.setVisibility(View.GONE);
                        llMark.setVisibility(View.GONE);
                        adapter.add(t1);
                        etDate.setText("");
                        spSubject.setSelection(0);
                        hide_keyboard(view);
                    }

                }
            }
        });


    }

    public void hide_keyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (view == null) {
            view = new View(this);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @OnClick({R.id.btnCreateClass, R.id.etDate, R.id.etResultDate, R.id.etStartTime, R.id.etEndTime})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCreateClass:
                if (isValid()) {
                    if (!TextUtils.isEmpty(etDate.getText().toString().trim())) {
                        if (TextUtils.isEmpty(etStartTime.getText().toString().trim())) {
                            Toast.makeText(AddOfflineTestActivity.this, getResources().getString(R.string.toast_please_select_start_time), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (spSubject.getSelectedItemPosition() == 0) {
                            Toast.makeText(AddOfflineTestActivity.this, getResources().getString(R.string.toast_please_select_subject), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(etEndTime.getText().toString().trim())) {
                            Toast.makeText(AddOfflineTestActivity.this, getResources().getString(R.string.toast_please_select_end_time), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(etMaxMarks.getText().toString().trim())) {
                            Toast.makeText(AddOfflineTestActivity.this, getResources().getString(R.string.toast_please_enter_max_mark), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(etMinMarks.getText().toString().trim())) {
                            Toast.makeText(AddOfflineTestActivity.this, getResources().getString(R.string.toast_please_enter_min_mark), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    if (!isConnectionAvailable()) {
                        showNoNetworkMsg();
                        return;
                    }

                    OfflineTestReq request = new OfflineTestReq();
                    request.title = etTitle.getText().toString();
                    request.resultDate = etResultDate.getText().toString();
                    request.subjectMarksDetails = adapter.getList();

                    TestOfflineSubjectMark t1 = null;

                    if (!TextUtils.isEmpty(etDate.getText().toString().trim()) &&
                            !TextUtils.isEmpty(etStartTime.getText().toString().trim()) &&
                            !TextUtils.isEmpty(etEndTime.getText().toString().trim()) &&
                            !TextUtils.isEmpty(etMaxMarks.getText().toString().trim()) &&
                            !TextUtils.isEmpty(etMinMarks.getText().toString().trim())
                    ) {
                        t1 = new TestOfflineSubjectMark();
                        t1.date = etDate.getText().toString();
                        t1.subjectName = spSubject.getSelectedItem().toString();
                        t1.subjectId = subjectList.get(spSubject.getSelectedItemPosition()).subjectId;
                        t1.maxMarks = etMaxMarks.getText().toString();
                        t1.minMarks = etMinMarks.getText().toString();
                        t1.startTime = etStartTime.getText().toString();
                        t1.endTime = etEndTime.getText().toString();

                    }
                    if (request.subjectMarksDetails.size() == 0 && t1 == null) {
                        Toast.makeText(this, getResources().getString(R.string.toast_please_select_one_subject_mark), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (t1 != null) {
                        request.subjectMarksDetails.add(t1);
                    }
                    AppLog.e(TAG, "request :" + new Gson().toJson(request));
                    LeafManager leafManager = new LeafManager();
                    if (isEdit) {
                        SMBDialogUtils.showSMBDialogOK(this, getResources().getString(R.string.smb_dialog_this_will_schedule_new_test), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LeafManager leafManager = new LeafManager();
                                showLoadingBar(progressBar,false);
                                //progressBar.setVisibility(View.VISIBLE);
                                leafManager.createOfflineTest(AddOfflineTestActivity.this, groupId, teamId, request);
                            }
                        });
                    } else {
                        showLoadingBar(progressBar,false);
                        //progressBar.setVisibility(View.VISIBLE);
                        leafManager.createOfflineTest(this, groupId, teamId, request);
                    }
                }
                break;

            case R.id.etDate: {


                DatePickerFragment fragment = DatePickerFragment.newInstance();

                fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                    @Override
                    public void onDateSelected(Calendar c) {
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        etDate.setText(format.format(c.getTime()));
                    }
                });
                fragment.show(getSupportFragmentManager(), "datepicker");
                fragment.setTitle(R.string.lbl_Date);


               /* final Calendar calendar = Calendar.getInstance();
                DatePickerDialog fragment = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        etDate.setText(format.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                fragment.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                fragment.show();*/
                break;
            }


            case R.id.etStartTime: {
                Calendar calendar = Calendar.getInstance();

                TimePickerDialog fragment1 = new TimePickerDialog(this,  android.R.style.Theme_Holo_Light_Dialog_NoActionBar,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                        etStartTime.setText(format.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                fragment1.show();
                break;
            }
            case R.id.etEndTime: {
                Calendar calendar = Calendar.getInstance();
                TimePickerDialog fragment1 = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                        etEndTime.setText(format.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                fragment1.show();
                break;
            }
            case R.id.etResultDate: {

                DatePickerFragment fragment = DatePickerFragment.newInstance();

                fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                    @Override
                    public void onDateSelected(Calendar c) {
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        etResultDate.setText(format.format(c.getTime()));
                    }
                });
                fragment.show(getSupportFragmentManager(), "datepicker");
                fragment.setTitle(R.string.lbl_ResultDate);


               /* final Calendar calendar = Calendar.getInstance();
                DatePickerDialog fragment = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        etResultDate.setText(format.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                fragment.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                fragment.show();*/
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
            hideLoadingBar();
           // progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_SUBJECT_STAFF:
                SubjectStaffResponse res = (SubjectStaffResponse) response;
                subjectList = res.getData();
                String strSubject[] = new String[subjectList.size()+1];
                strSubject[0] = getResources().getString(R.string.lbl_select_subject);
                for (int i = 0; i < subjectList.size(); i++) {
                    strSubject[i+1] = subjectList.get(i).name;
                }
                ArrayAdapter<String> spSubAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner_white, R.id.tvItem, strSubject);
                spSubject.setAdapter(spSubAdapter);

                adapter = new SubjectMarkAdapter();
                rvSubjects.setAdapter(adapter);
                break;
            case LeafManager.API_CREATE_OFFLINE_TEST:
                LeafPreference.getInstance(AddOfflineTestActivity.this).setBoolean("is_offline_test_added", true);
                new SendNotification().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                finish();
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
        // progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }

    public class SubjectMarkAdapter extends RecyclerView.Adapter<SubjectMarkAdapter.ViewHolder> {
        private Context mContext;
        ArrayList<TestOfflineSubjectMark> list = new ArrayList<>();
        String strSubject[];

        SubjectMarkAdapter() {
            strSubject = new String[subjectList.size()];
            for (int i = 0; i < subjectList.size(); i++) {
                strSubject[i] = subjectList.get(i).name;
            }
        }

        @NonNull
        @Override
        public SubjectMarkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_test_offline_add, parent, false);
            return new SubjectMarkAdapter.ViewHolder(view);
        }

        public ArrayList<TestOfflineSubjectMark> getList() {
            return this.list;
        }

        @Override
        public void onBindViewHolder(@NonNull SubjectMarkAdapter.ViewHolder holder, int pos) {
            holder.etDate.setText(list.get(pos).date);
            holder.etMaxMarks.setText(list.get(pos).maxMarks);
            holder.etMinMarks.setText(list.get(pos).minMarks);
            holder.etStartTime.setText(list.get(pos).startTime);
            holder.etEndTime.setText(list.get(pos).endTime);

            holder.etDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatePickerFragment fragment = DatePickerFragment.newInstance();

                    fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                        @Override
                        public void onDateSelected(Calendar c) {
                            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            holder.etDate.setText(format.format(c.getTime()));
                        }
                    });
                    fragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "datepicker");
                    fragment.setTitle(R.string.lbl_Date);


                   /* final Calendar calendar = Calendar.getInstance();
                    DatePickerDialog fragment = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, month);
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            etDate.setText(format.format(calendar.getTime()));
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    fragment.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                    fragment.show();*/
                }
            });

            holder.etStartTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance();
                    TimePickerDialog fragment1 = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);
                            SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                            holder.etStartTime.setText(format.format(calendar.getTime()));
                            list.get(pos).startTime = format.format(calendar.getTime());
                        }
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                    fragment1.show();
                }
            });
            holder.etEndTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance();
                    TimePickerDialog fragment1 = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);
                            SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                            holder.etEndTime.setText(format.format(calendar.getTime()));
                            list.get(pos).endTime = format.format(calendar.getTime());
                        }
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                    fragment1.show();
                }
            });

            holder.etMaxMarks.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    if(list.size() > pos)
                    {
                        list.get(pos).maxMarks = s.toString();
                    }

                }
            });

            holder.etMinMarks.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(list.size() > pos)
                    {
                        list.get(pos).minMarks = s.toString();
                    }
                }
            });

            ArrayAdapter<String> spSubAdapter = new ArrayAdapter<String>(mContext, R.layout.item_spinner_white, R.id.tvItem, strSubject);
            holder.spSubject.setAdapter(spSubAdapter);
            int index = 0;
            for (int i = 0; i < subjectList.size(); i++) {
                if (list.get(pos).subjectId.equals(subjectList.get(i).subjectId)) {
                    index = i;
                }
            }
            holder.spSubject.setSelection(index);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void add(TestOfflineSubjectMark addSubjectData) {
            list.add(addSubjectData);
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

            @Bind(R.id.spSubject)
            Spinner spSubject;

            @Bind(R.id.etEndTime)
            EditText etEndTime;
            @Bind(R.id.imgDelete)
            ImageView imgDelete;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                imgDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list.remove(getAdapterPosition());
                        notifyDataSetChanged();
                    }
                });

                spSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        list.get(getAdapterPosition()).subjectId = subjectList.get(position).subjectId;
                        list.get(getAdapterPosition()).subjectName = subjectList.get(position).name;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }
        }
    }


    private class SendNotification extends AsyncTask<String, String, String> {


        private String server_response;



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
                    String message = "";



                    message = " New Test/Exam added to " + getIntent().getStringExtra("title");
                    topic = groupId + "_" + teamId;
                    object.put("to", "/topics/" + topic);

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title", title);
                    notificationObj.put("body", message);
                    object.put("notification", notificationObj);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("groupId", groupId);
                    dataObj.put("createdById", LeafPreference.getInstance(AddOfflineTestActivity.this).getString(LeafPreference.LOGIN_ID));
                    dataObj.put("postId", "");
                    dataObj.put("teamId", teamId);
                    dataObj.put("title", title);
                    dataObj.put("postType", "offline_test");
                    dataObj.put("Notification_type", "OFFLINE_TEST_EXAM");
                    dataObj.put("body", message);
                    object.put("data", dataObj);

                    wr.writeBytes(object.toString());
                    Log.e(" JSON input : ", object.toString());
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
}
