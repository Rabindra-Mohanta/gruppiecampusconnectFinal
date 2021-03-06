package school.campusconnect.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Calendar;

import school.campusconnect.adapters.ReportAdapter;
import school.campusconnect.datamodel.attendance_report.AttendenceEditRequest;
import school.campusconnect.datamodel.attendance_report.LeaveRes;
import school.campusconnect.datamodel.subjects.AbsentStudentReq;
import school.campusconnect.datamodel.subjects.AbsentSubjectReq;
import school.campusconnect.datamodel.subjects.SubjectResponse;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.adapters.AttendanceAdapter;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.AbsentAttendanceRes;
import school.campusconnect.datamodel.AttendanceListRes;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.subjects.SubjectResponsev1;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppDialog;
import school.campusconnect.utils.AppLog;

public class AttendanceActivity extends BaseActivity implements AttendanceAdapter.listener {

    private static final String TAG = "AttendanceActivity";
    @Bind(R.id.toolbar)
    Toolbar mToolBar;

    @Bind(R.id.rvAttendance)
    RecyclerView rvAttendance;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.txtEmpty)
    TextView txtEmpty;

    private String groupId;
    private String teamId;
    private LeafPreference leafPreference;
    private LeafManager leafManager;

    ArrayList<AttendanceListRes.AttendanceData> listAttendance = new ArrayList<>();
    ArrayList<AttendanceListRes.AttendanceData> listAbsent = new ArrayList<>();
    AttendanceAdapter attendanceAdapter;
    private ArrayList<String> userIds = new ArrayList<>();
    private ArrayList<SubjectResponsev1.SubjectData> subjectList;

    Calendar calendar;
    int year;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.title_attendance)+" - ("+getIntent().getStringExtra("className")+")");

        init_();

        getAttendanceList();

        getSubjectList();
    }
    private void getSubjectList(){
        leafManager.getAttendanceSubject(this,groupId,teamId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_attendance, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_class:{
                Intent attendanceIntent = new Intent(this, AttendanceReportActivity.class);
                attendanceIntent.putExtra("team_id", teamId);
                attendanceIntent.putExtra("className", getIntent().getStringExtra("className"));
                startActivity(attendanceIntent);
                return true;
            }
            case R.id.menu_generate_report_online:
                Intent attendanceIntent = new Intent(this, AttendanceReportOnlineActivity.class);
                attendanceIntent.putExtra("team_id", teamId);
                attendanceIntent.putExtra("className", getIntent().getStringExtra("className"));
                startActivity(attendanceIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init_() {
        leafPreference = LeafPreference.getInstance(this);
        leafManager = new LeafManager();
        Bundle bundle = getIntent().getExtras();
        groupId = bundle.getString("group_id", "");
        teamId = bundle.getString("team_id", "");
        AppLog.e(TAG, ",groupId:" + groupId + ",teamId:" + teamId);

        calendar = Calendar.getInstance();
        year =  calendar.get(Calendar.YEAR);
        rvAttendance.setLayoutManager(new LinearLayoutManager(this));
        attendanceAdapter = new AttendanceAdapter(listAttendance, groupId, teamId,this);
        rvAttendance.setAdapter(attendanceAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (leafPreference.getBoolean(LeafPreference.EDIT_ATTENDANCE)) {
            getAttendanceList();
            leafPreference.setBoolean(LeafPreference.EDIT_ATTENDANCE, false);
        }

        if (leafPreference.getBoolean(LeafPreference.IS_STUDENT_ADDED)) {
            importStudents();
            leafPreference.setBoolean(LeafPreference.IS_STUDENT_ADDED, false);
        }


    }

    @OnClick({R.id.btnSubmit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit:

//                if(!isSubmitted())
//                {
                listAbsent.clear();

                StringBuilder absentName = new StringBuilder();
                for (int i = 0; i < listAttendance.size(); i++) {
                    if (!listAttendance.get(i).isChecked) {
                        listAbsent.add(listAttendance.get(i));

                        absentName.append("- ").append(listAttendance.get(i).name).append("\n");
                    }
                }

                    /*if(listAbsent.size()>0)
                    {*/
                showAbsentStudentDialog(absentName.toString());
                    /*}
                    else
                    {
                        Toast.makeText(this, "No Absent Student", Toast.LENGTH_SHORT).show();
                    }*/
               /* }
                else
                {
                    Toast.makeText(this, "Attendance Already Submitted", Toast.LENGTH_SHORT).show();
                }*/


                break;
        }
    }

    private boolean isSubmitted() {
        if (leafPreference.contains(LeafPreference.SUBMITTED_DATA)) {
            ArrayList<String> submittedData = new Gson().fromJson(leafPreference.getString(LeafPreference.SUBMITTED_DATA), new TypeToken<ArrayList<String>>() {
            }.getType());
            for (int i = 0; i < submittedData.size(); i++) {

            }
        }
        return false;
    }

    private void showAbsentStudentDialog(String absentName) {
        final Dialog dialog = new Dialog(this,R.style.FragmentDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.absent_dialog_layout);

        TextView tvStudents = dialog.findViewById(R.id.tvStudents);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);

        RecyclerView rvSubject = dialog.findViewById(R.id.rvSubjects);
        EditText etSubject = dialog.findViewById(R.id.etSubject);

        if(!TextUtils.isEmpty(absentName)){
            dialog.findViewById(R.id.llSubject).setVisibility(View.VISIBLE);
        }else {
            dialog.findViewById(R.id.llSubject).setVisibility(View.VISIBLE);
        }

        AttendanceSubjectAdapter subjectAdapter = new AttendanceSubjectAdapter(subjectList, new AttendanceSubjectListener() {
            @Override
            public void itemClick(int checkPos) {
                if(checkPos>=0){
                    etSubject.setText("");
                    etSubject.setEnabled(false);
                }else {
                    etSubject.setEnabled(true);
                }
            }
        });
        rvSubject.setAdapter(subjectAdapter);
        tvStudents.setText(absentName);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                String subjectName;
                if(!TextUtils.isEmpty(subjectAdapter.getSelected())){
                    subjectName = subjectAdapter.getSelected();
                }else {
                    subjectName = etSubject.getText().toString().trim();
                }
                callAbsentStudentApi(subjectName);
            }
        });

        dialog.show();
    }

    private void callAbsentStudentApi(String subjectName) {


        userIds.clear();
        if (!isConnectionAvailable()) {
            showNoNetworkMsg();
            return;
        }
        //showLoadingBar(progressBar,false);
        progressBar.setVisibility(View.VISIBLE);


        for (int i = 0; i < listAbsent.size(); i++) {
       //     userIds.add(listAbsent.get(i).userId + "," + listAbsent.get(i).rollNumber);
            userIds.add(listAbsent.get(i).userId);
        }

        AbsentStudentReq req = new AbsentStudentReq();

        if (!subjectName.isEmpty())
        {
            req.setSubjectId(subjectName);
        }
        req.setAbsentStudentIds(userIds);

        Log.e(TAG,"req "+new Gson().toJson(req));

        leafManager.sendAbsentiesv1(this,groupId,teamId,req);
       // leafManager.sendAbsenties(this, groupId, teamId, userIds,new AbsentSubjectReq(TextUtils.isEmpty(subjectName)?null:subjectName));
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.attendance_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_attendance)
        {
            importStudents();
            return true;
        }
        if(item.getItemId()==R.id.menu_add_student)
        {
            Intent intent=new Intent(this,AddStudentActivity.class);
            intent.putExtra("group_id",groupId);
            intent.putExtra("team_id",teamId);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    private void importStudents() {
        if (!isConnectionAvailable()) {
            showNoNetworkMsg();
            return;
        }
     //   showLoadingBar(progressBar,false);
        progressBar.setVisibility(View.VISIBLE);
        leafManager.importStudent(this, groupId, teamId);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
      //  hideLoadingBar();


            progressBar.setVisibility(View.GONE);
        switch (apiId) {
            case LeafManager.API_IMPORT_STUDENTS:
                getAttendanceList();
                break;

            case LeafManager.API_GET_LEAVE_ATTENDACNCE:
                LeaveRes leaveRes = (LeaveRes) response;
                openLeaveDialog(leaveRes);
                break;

            case LeafManager.API_GET_ATTENDANCE:
                AttendanceListRes attendanceListRes = (AttendanceListRes) response;
                AppLog.e(TAG, "attendanceListRes :" + attendanceListRes);

                if (attendanceListRes.data.size() == 0)
                {
                    txtEmpty.setText(getResources().getString(R.string.msg_no_report));
                }
                else
                {
                    txtEmpty.setText("");
                }

                listAttendance.clear();
                listAttendance.addAll(attendanceListRes.data);

                for (int i = 0; i < listAttendance.size(); i++) {
                    listAttendance.get(i).isChecked = true;
                }
                attendanceAdapter.notifyDataSetChanged();
                break;
            case LeafManager.API_TAKE_ATTENDANCE:
                Toast.makeText(this, getResources().getString(R.string.toast_success), Toast.LENGTH_SHORT).show();
              /*  AbsentAttendanceRes absentAttendanceRes = (AbsentAttendanceRes) response;
                AppLog.e(TAG, "AbsentAttendanceRes : " + absentAttendanceRes);*/
                new SendNotification().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                finish();
                break;

            case LeafManager.API_ATTENDANCE_SUBJECT:
                SubjectResponsev1 subjectResponse = (SubjectResponsev1) response;

                if(subjectResponse.getData()!=null && subjectResponse.getData().size()>0){
                    subjectList= subjectResponse.getData();
                }
                break;

            case LeafManager.API_EDIT_ATTENDANCE_STUDENT:
                Toast.makeText(this, getResources().getString(R.string.toast_success), Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    private void openLeaveDialog(LeaveRes leaveRes) {

        Dialog dialogEdit = new Dialog(AttendanceActivity.this, R.style.AppDialog);
        dialogEdit.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogEdit.setContentView(R.layout.dialog_leave_attendance);

        TextView studentName = (TextView) dialogEdit.findViewById(R.id.tvStudentName);
        TextView tvfromDate = (TextView) dialogEdit.findViewById(R.id.tvFromDate);
        TextView tvToDate = (TextView) dialogEdit.findViewById(R.id.tvToDate);
        TextView tvNumberDay = (TextView) dialogEdit.findViewById(R.id.tvNumberDay);
        TextView tvReason = (TextView) dialogEdit.findViewById(R.id.tvReason);


        studentName.setText(nameStudent);
        tvfromDate.setText(leaveRes.getData().get(0).getLeaveApplies().getFromDate());
        tvToDate.setText(leaveRes.getData().get(0).getLeaveApplies().getToDate());
        tvNumberDay.setText(leaveRes.getData().get(0).getLeaveApplies().getNoOfDays());
        tvReason.setText(leaveRes.getData().get(0).getLeaveApplies().getReason());

        dialogEdit.show();

    }

    private void getAttendanceList() {
        if (!isConnectionAvailable()) {
            showNoNetworkMsg();
            return;
        }
        showLoadingBar(progressBar,true);
    //    progressBar.setVisibility(View.VISIBLE);
        leafManager.getAttendance(this, groupId, teamId);
    }

    @Override
    public void onFailure(int apiId, String msg) {
        if (progressBar != null)
          //  hideLoadingBar();
            progressBar.setVisibility(View.GONE);
        AppLog.e("onFailure", "Failure");
        if (msg.contains("401") || msg.contains("Unauthorized")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onException(int apiId, String msg) {
        if (progressBar != null)
         //   hideLoadingBar();
          progressBar.setVisibility(View.GONE);
    }

    private String nameStudent;
    @Override
    public void edit(AttendanceListRes.lastDayData attendance, String item,String name) {

        nameStudent = name;
        if (!attendance.attendance.equalsIgnoreCase("leave"))
        {
            boolean[] isEdit = {true};

            Dialog dialogEdit = new Dialog(AttendanceActivity.this, R.style.AppDialog);
            dialogEdit.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogEdit.setContentView(R.layout.dialog_edit_attendance);

            TextView studentName = (TextView) dialogEdit.findViewById(R.id.tvStudent);
            TextView tvDateTime = (TextView) dialogEdit.findViewById(R.id.tvDateTime);
            TextView tvTeacherName = (TextView) dialogEdit.findViewById(R.id.tvTeacherName);
            TextView tvSubjectName = (TextView) dialogEdit.findViewById(R.id.tvSubjectName);
            Spinner spAttend = (Spinner) dialogEdit.findViewById(R.id.spAttend);
            Button btnEdit = (Button) dialogEdit.findViewById(R.id.btnEdit);

            //  studentName.setTextColor(getResources().getColor(R.color.grey));
            tvDateTime.setTextColor(getResources().getColor(R.color.grey));
            tvSubjectName.setTextColor(getResources().getColor(R.color.grey));
            tvTeacherName.setTextColor(getResources().getColor(R.color.grey));

            ArrayAdapter<String> ad = new ArrayAdapter<String>(this, R.layout.item_spinner_new_disable, R.id.tvItem, new String[]{"present","absent"});
            spAttend.setAdapter(ad);
            spAttend.setEnabled(false);

            if (attendance.attendance.equalsIgnoreCase("present"))
            {
                spAttend.setSelection(0);
            }
            else
            {
                spAttend.setSelection(1);
            }
            studentName.setText(name);
            tvDateTime.setText(attendance.date+","+attendance.time);
            tvSubjectName.setText(attendance.subjectName);
            tvTeacherName.setText(attendance.teacherName);


            Log.e(TAG,"isAdmin"+GroupDashboardActivityNew.isAdmin);
            Log.e(TAG,"teacherID "+attendance.teacherId);
            Log.e(TAG,"LOGIN ID "+LeafPreference.getInstance(this).getString(LeafPreference.LOGIN_ID));

            if (GroupDashboardActivityNew.isAdmin || attendance.teacherId.equalsIgnoreCase(LeafPreference.getInstance(this).getString(LeafPreference.LOGIN_ID)))
            {
                btnEdit.setVisibility(View.VISIBLE);
            }
            else
            {
                btnEdit.setVisibility(View.GONE);
            }
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isEdit[0])
                    {
                        isEdit[0] = false;
                        //          studentName.setTextColor(getResources().getColor(R.color.colorPrimary));
                        tvDateTime.setTextColor(getResources().getColor(R.color.colorPrimary));
                        tvSubjectName.setTextColor(getResources().getColor(R.color.colorPrimary));
                        tvTeacherName.setTextColor(getResources().getColor(R.color.colorPrimary));
                        btnEdit.setText(getResources().getString(R.string.lbl_save));

                        ArrayAdapter<String> ad = new ArrayAdapter<String>(AttendanceActivity.this, R.layout.item_spinner_new, R.id.tvItem, new String[]{"present","absent"});
                        spAttend.setAdapter(ad);
                        spAttend.setEnabled(true);

                        if (attendance.attendance.equalsIgnoreCase("present"))
                        {
                            spAttend.setSelection(0);
                        }
                        else
                        {
                            spAttend.setSelection(1);
                        }
                    }
                    else
                    {
                        isEdit[0] = true;
                        dialogEdit.dismiss();
                        AttendenceEditRequest request = new AttendenceEditRequest();
                        request.setUserId(item);
                        request.setAttendanceId(attendance.attendanceId);
                        request.setAttendance(spAttend.getSelectedItem().toString());

                /*if (attendance.attendance.equalsIgnoreCase("present"))
                {
                    request.setAttendance("absent");
                }
                else
                {
                    request.setAttendance("present");
                }*/

                        Log.e(TAG,"edit attendance req "+new Gson().toJson(request));

                        leafManager.editAttendanceStudent(AttendanceActivity.this,groupId,teamId,request);
                    }

                }
            });

            dialogEdit.show();
        }
        else
        {
            showLoadingBar(progressBar,true);
            leafManager.getLeave(this,GroupDashboardActivityNew.groupId,teamId,item,attendance.date, year);
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
                urlConnection.setRequestProperty("Authorization", BuildConfig.API_KEY_FIREBASE1+BuildConfig.API_KEY_FIREBASE2);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                try {
                    JSONObject object = new JSONObject();

                    String title = getResources().getString(R.string.app_name);
                    String message = "Attendance submitted";
                    String topic = groupId + "_" + teamId;
                    object.put("to", "/topics/" + topic);

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title", title);
                    notificationObj.put("body", message);
                    object.put("notification", notificationObj);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("groupId", groupId);
                    dataObj.put("createdById", LeafPreference.getInstance(AttendanceActivity.this).getString(LeafPreference.LOGIN_ID));
                    dataObj.put("teamId", teamId);
                    dataObj.put("title", title);
                    dataObj.put("Notification_type", "attendance");
                    dataObj.put("body", message);
                    object.put("data", dataObj);

                    wr.writeBytes(object.toString());
                    Log.e(TAG, " JSON input : " + object.toString());
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


    public class AttendanceSubjectAdapter extends RecyclerView.Adapter<AttendanceSubjectAdapter.ViewHolder> {
        int checkPos = -1;
        private final ArrayList<SubjectResponsev1.SubjectData> listSubject;
        private Context mContext;
        AttendanceSubjectListener listener;
        public AttendanceSubjectAdapter(ArrayList<SubjectResponsev1.SubjectData> listSubject,AttendanceSubjectListener listener) {
            this.listSubject=listSubject;
            this.listener=listener;
        }

        @Override
        public AttendanceSubjectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_attendance_subject, parent, false);
            return new AttendanceSubjectAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final AttendanceSubjectAdapter.ViewHolder holder, final int position) {

            holder.tvName.setText(listSubject.get(position).subjectName);
            if(checkPos==position){
                holder.chkAttendance.setChecked(true);
            }else {
                holder.chkAttendance.setChecked(false);
            }
        }

        @Override
        public int getItemCount() {
            return listSubject!=null?listSubject.size():0;
        }

        public String getSelected() {

            if(checkPos>=0){
                return listSubject.get(checkPos).subjectId;
            }else {
                return "";
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.chkAttendance)
            CheckBox chkAttendance;

            @Bind(R.id.tvName)
            TextView tvName;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
                chkAttendance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(checkPos==getAdapterPosition()){
                            checkPos = -1;
                        }else {
                            checkPos = getAdapterPosition();
                        }
                        notifyDataSetChanged();
                        listener.itemClick(checkPos);
                    }
                });
            }
        }

    }
    public interface AttendanceSubjectListener{
        void itemClick(int checkPos);
    }
}
