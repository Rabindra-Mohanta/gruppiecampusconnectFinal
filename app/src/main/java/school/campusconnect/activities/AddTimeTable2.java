package school.campusconnect.activities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
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
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import school.campusconnect.views.SMBDialogUtils;

public class AddTimeTable2 extends BaseActivity implements LeafManager.OnAddUpdateListener<GroupValidationError> {

    private static final String TAG = "AddTimeTable2";
    /*@Bind(R.id.spDay)
    Spinner spDay;*/

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.et_period)
    EditText et_period;

    @Bind(R.id.etTimeAddNew)
    EditText etTimeAddNew;

    @Bind(R.id.spSubject)
    Spinner spSubject;

    @Bind(R.id.spStaff)
    Spinner spStaff;

    @Bind(R.id.rvTimeTable)
    RecyclerView rvTimeTable;

    private TextView tvTimePickerTitle;
    LeafManager leafManager;
    private String team_id;
    private String day = "";
    private String period = "";
    private String start_time = "09:00 AM";
    private String end_time = "09:40 AM";


    private String start_time_edit = "";
    private String end_time_edit = "";
    private String start_time_new = "";
    private String end_time_new = "";
    private ArrayList<TimeTableList2Response.SessionsTimeTable> periodList = new ArrayList<>();
    private String periodLast;
    private ArrayList<SubjectStaffTTResponse.SubjectStaffTTData> subjStaffList;
    private ArrayList<SubjectStaffTTResponse.SubjectStaffTTData> subjStaffListDialog;

    boolean isAddStaffApiForEdit = false;

    boolean isNewAdd = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tt);

        init();


    }

    private void getSubjectStaff(SubStaffTTReq subStaffTTReq) {
        Log.e(TAG,"SubStaffTTReq"+new Gson().toJson(subStaffTTReq));
        LeafManager leafManager = new LeafManager();
        progressBar.setVisibility(View.VISIBLE);
        leafManager.getSubjectStaffTT(this, GroupDashboardActivityNew.groupId, team_id, subStaffTTReq);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete_tt,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.menu_delete:
                SMBDialogUtils.showSMBDialogOKCancel(this, "Are you sure you want to delete time-table day?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LeafManager leafManager=new LeafManager();
                        progressBar.setVisibility(View.VISIBLE);
                        leafManager.deleteTTNewByDay(AddTimeTable2.this,GroupDashboardActivityNew.groupId,team_id,day);
                    }
                });
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void init() {
        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);
        setBackEnabled(true);

        tvTimePickerTitle = new TextView(getApplicationContext());
        tvTimePickerTitle.setPadding(0, 20, 00, 20);
        tvTimePickerTitle.setGravity(Gravity.CENTER);
        tvTimePickerTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
        tvTimePickerTitle.setTextColor(Color.parseColor("#293f6e"));
        tvTimePickerTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
        leafManager = new LeafManager();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            team_id = bundle.getString("team_id");
            day = bundle.getString("day");
            setTitle(bundle.getString("team_name")+" ( "+getWeekDay(day)+" )");
        }
        String[] days = new String[7];

        days[0] = "Monday";
        days[1] = "Tuesday";
        days[2] = "Wednesday";
        days[3] = "Thursday";
        days[4] = "Friday";
        days[5] = "Saturday";
        days[6] = "Sunday";

        reset();

        callCurrentTimeTableApi();

       /* ArrayAdapter<String> bloodGrpAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, days);
        spDay.setAdapter(bloodGrpAdapter);*/



        /*spDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                day = (position + 1) + "";
                Log.e(TAG,"day "+day);
                callCurrentTimeTableApi();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
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

    private String getWeekDay(String day) {
        switch (day){
            case "1":return "Monday";
            case "2":return "Tuesday";
            case "3":return "Wednesday";
            case "4":return "Thursday";
            case "5":return "Friday";
            case "6":return "Saturday";
            case "7":return "Sunday";
        }
        return "";
    }


    private void reset() {

        ArrayAdapter<String> spSubAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner_new, R.id.tvItem, new String[]{"Select"});
        spSubject.setAdapter(spSubAdapter);

        ArrayAdapter<String> spStaffAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner_new, R.id.tvItem, new String[]{"Select"});
        spStaff.setAdapter(spStaffAdapter);


    }

    private void callSubjectStaffApi() {
        isAddStaffApiForEdit = false;
        AppLog.e(TAG, "callSubjectStaffApi : day : " + day + ", period :" + period);
        /*AppLog.e(TAG,"callSubjectStaffApi : day of week: "+Calendar.getInstance().get(Calendar.DAY_OF_WEEK));*/
        if (!TextUtils.isEmpty(day) && !TextUtils.isEmpty(period)) {
            reset();
            getSubjectStaff(new SubStaffTTReq(day, period,start_time,end_time));
        }
    }

    @OnClick({R.id.btnAdd,R.id.etTimeAddNew})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAdd:
                if (isValid()) {

                    if (!isConnectionAvailable()) {
                        showNoNetworkMsg();
                        return;
                    }

                    SubStaffTTReq request = new SubStaffTTReq(day, period,start_time_new,end_time_new);
                    start_time_new = "";
                    end_time_new = "";
                    AppLog.e(TAG, "request :" + request);
                    String subject_with_staff_id = subjStaffList.get(spSubject.getSelectedItemPosition()).getSubjectWithStaffId();
                    String staff_id = subjStaffList.get(spSubject.getSelectedItemPosition()).getSubjectWithStaffs().get(spStaff.getSelectedItemPosition()).getStaffId();

                    progressBar.setVisibility(View.VISIBLE);
                    leafManager.addSubjectStaffTT(this, GroupDashboardActivityNew.groupId, team_id, subject_with_staff_id, staff_id, request);

                }
                break;

            case R.id.etTimeAddNew:

                isNewAdd = true;

                final Calendar calendar = Calendar.getInstance();

                TimePickerDialog fragment = new TimePickerDialog(AddTimeTable2.this, android.R.style.Theme_Material_Light_Dialog,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());

                        openEndTimeDialog(format.format(calendar.getTime()));

                        // holder.et_time.setText(format.format(calendar.getTime()));
                    }
                },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false);

                fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        etTimeAddNew.setText("");
                    }
                });

                if (tvTimePickerTitle.getParent() != null)
                {
                    ((ViewGroup)tvTimePickerTitle.getParent()).removeView(tvTimePickerTitle);
                }
                tvTimePickerTitle.setText("Select Start Time");
                fragment.setCustomTitle(tvTimePickerTitle);
                fragment.show();
                break;
        }
    }

    public boolean isValid() {
        boolean valid = true;
        if (!isValueValid(et_period)) {
            valid = false;
        }
        else if (etTimeAddNew.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please Select Start and End Time", Toast.LENGTH_SHORT).show();
            valid = false;
        }else if (subjStaffList == null) {
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
                etTimeAddNew.setText("");
                Toast.makeText(this, "Successfully Added Time Table", Toast.LENGTH_SHORT).show();
                callCurrentTimeTableApi();
                break;

            case LeafManager.API_TT_REMOVE_DAY:
                finish();
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
                    periodList = res2.getData().get(0).getSessions();
                    periodLast = res2.getData().get(0).getSessions().size() + 1 + "";
                } else {
                    periodList = new ArrayList<>();
                    rvTimeTable.setAdapter(new SessionAdapter(null));
                    et_period.setText("1");
                    periodLast = 1 + "";
                }
                break;
        }
    }

    private void setSubjectSpinner() {
        String[] subject = new String[subjStaffList.size()];

        for (int i = 0; i < subjStaffList.size(); i++) {
            subject[i] = subjStaffList.get(i).subjectName;
        }
        ArrayAdapter<String> bloodGrpAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner_new, R.id.tvItem, subject);
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
        ArrayAdapter<String> bloodGrpAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner_new, R.id.tvItem, staff);
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

            holder.et_time.setText(item.getStartTime()+" to "+item.getEndTime());

            holder.et_subject.setText(item.getSubjectName());

            holder.et_staff.setText(item.getTeacherName());

        /*    holder.spSubject.setAdapter(new ArrayAdapter<String>(AddTimeTable2.this, R.layout.item_spinner_new, R.id.tvItem, new String[]{item.getSubjectName()}));

            holder.spStaff.setAdapter(new ArrayAdapter<String>(AddTimeTable2.this, R.layout.item_spinner_new, R.id.tvItem, new String[]{item.getTeacherName()}));

         */   holder.imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEdit(item);
                }
            });

            /*holder.et_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Calendar calendar = Calendar.getInstance();

                    TimePickerDialog fragment = new TimePickerDialog(AddTimeTable2.this, android.R.style.Theme_Material_Light_Dialog,new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);
                            SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());

                            openEndTimeDialog(holder.et_time,format.format(calendar.getTime()));

                           // holder.et_time.setText(format.format(calendar.getTime()));
                        }
                    },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false);

                    fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            holder.et_time.setText("");
                        }
                    });
                    fragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            holder.et_time.setText("");
                        }
                    });
                    if (tvTimePickerTitle.getParent() != null)
                    {
                        ((ViewGroup)tvTimePickerTitle.getParent()).removeView(tvTimePickerTitle);
                    }
                    tvTimePickerTitle.setText("Start Time : ");
                    fragment.setCustomTitle(tvTimePickerTitle);
                    fragment.show();
                }
            });*/
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

            @Bind(R.id.et_time)
            EditText et_time;

            @Bind(R.id.et_staff)
            EditText et_staff;

            @Bind(R.id.et_subject)
            EditText et_subject;


         /*   @Bind(R.id.spSubject)
            Spinner spSubject;

            @Bind(R.id.spStaff)
            Spinner spStaff;*/

            @Bind(R.id.imgEdit)
            ImageView imgEdit;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
    private boolean checktimings(String time, String endtime) {

        String pattern = "hh:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            if(date1.before(date2)) {
                return true;
            } else {

                return false;
            }
        } catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }

    private void openEndTimeDialog(String startDate) {

        final Calendar calendar = Calendar.getInstance();

        TimePickerDialog fragment = new TimePickerDialog(AddTimeTable2.this, android.R.style.Theme_Material_Light_Dialog,new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());


                if (checktimings(startDate,format.format(calendar.getTime())))
                {
                    if (isNewAdd)
                    {
                        start_time_new = startDate;
                        end_time_new = format.format(calendar.getTime());
                        etTimeAddNew.setText(start_time_new+" to "+end_time_new);

                    }
                    else
                    {
                        start_time_edit = startDate;
                        end_time_edit = format.format(calendar.getTime());
                        et_time.setText(start_time_edit+" to "+end_time_edit);
                    }
                }
                else
                {

                    if (isNewAdd)
                    {
                        etTimeAddNew.setText("");
                    }
                    else
                    {
                        et_time.setText("");
                    }
                    openEndTimeDialog(startDate);
                    Toast.makeText(getApplicationContext(),"Select End Time After a Start Time",Toast.LENGTH_SHORT).show();
                }
            }
        },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false);

        Button button1 = (Button) fragment.getButton(fragment.BUTTON_POSITIVE);


        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (isNewAdd)
                {
                    etTimeAddNew.setText("");
                }
                else
                {
                    et_time.setText("");
                }

            }
        });


        if (tvTimePickerTitle.getParent() != null)
        {
            ((ViewGroup)tvTimePickerTitle.getParent()).removeView(tvTimePickerTitle);
        }
        tvTimePickerTitle.setText("Select End Time");

        fragment.setCustomTitle(tvTimePickerTitle);
        fragment.show();
    }


    EditText et_period_dialog;

    EditText et_time;

    Spinner spSubject_dialog;

    Spinner spStaff_dialog;

    private void showEdit(TimeTableList2Response.SessionsTimeTable item) {
        final Dialog dialog = new Dialog(this, R.style.AppDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.edit_tt_dialog);
        et_period_dialog = dialog.findViewById(R.id.et_period);
        et_time = dialog.findViewById(R.id.et_time);
        et_time.setText(item.getStartTime()+" to "+item.getEndTime());
        et_period_dialog.setText(item.getPeriod());
        spSubject_dialog = dialog.findViewById(R.id.spSubject);
        spStaff_dialog = dialog.findViewById(R.id.spStaff);


        et_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isNewAdd = false;

                final Calendar calendar = Calendar.getInstance();

                TimePickerDialog fragment = new TimePickerDialog(AddTimeTable2.this, android.R.style.Theme_Material_Light_Dialog,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());

                        openEndTimeDialog(format.format(calendar.getTime()));

                        // holder.et_time.setText(format.format(calendar.getTime()));
                    }
                },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false);

                fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        et_time.setText("");
                    }
                });

                if (tvTimePickerTitle.getParent() != null)
                {
                    ((ViewGroup)tvTimePickerTitle.getParent()).removeView(tvTimePickerTitle);
                }
                tvTimePickerTitle.setText("Select Start Time");
                fragment.setCustomTitle(tvTimePickerTitle);
                fragment.show();
            }
        });


        dialog.findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(TAG,"Last Index of Period Item"+periodLast);

                if (periodList.size() > 0)
                {
                    for (int i = 0;i<periodList.size();i++)
                    {
                        if (periodList.get(i).getPeriod().toLowerCase().trim().equalsIgnoreCase(et_period_dialog.getText().toString().toLowerCase().trim()))
                        {
                            Toast.makeText(AddTimeTable2.this, "Period Value Already Used...", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

                if (periodLast.equalsIgnoreCase(et_period_dialog.getText().toString().toLowerCase().trim()))
                {
                    Toast.makeText(AddTimeTable2.this, "Period Value Already Used...", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (et_time.getText().toString().isEmpty())
                {
                    Toast.makeText(AddTimeTable2.this, "Select Start and End Time...", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(spSubject_dialog.getSelectedItemPosition()==-1 || spStaff_dialog.getSelectedItemPosition()==-1){
                    Toast.makeText(AddTimeTable2.this, "Please Select Subject and Staff", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isConnectionAvailable()) {
                    showNoNetworkMsg();
                    return;
                }
                SubStaffTTReq request = new SubStaffTTReq(day, item.getPeriod(),start_time_edit,end_time_edit);
                start_time_edit = "";
                end_time_edit = "";
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
        subStaffTTReq.setStartTime(start_time);
        subStaffTTReq.setEndTime(end_time);

        getSubjectStaff(subStaffTTReq);
    }


    private void setSubjectSpinnerDialog() {
        String[] subject = new String[subjStaffListDialog.size()];

        for (int i = 0; i < subjStaffListDialog.size(); i++) {
            subject[i] = subjStaffListDialog.get(i).subjectName;
        }
        ArrayAdapter<String> bloodGrpAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner_new, R.id.tvItem, subject);
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
        ArrayAdapter<String> bloodGrpAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner_new, R.id.tvItem, staff);
        spStaff_dialog.setAdapter(bloodGrpAdapter);
    }

}
