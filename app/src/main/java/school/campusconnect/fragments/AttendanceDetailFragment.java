package school.campusconnect.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.BuildConfig;
import school.campusconnect.LeafApplication;
import school.campusconnect.R;
import school.campusconnect.activities.ApplyLeaveActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.attendance_report.AttendanceDetailRes;
import school.campusconnect.datamodel.attendance_report.AttendanceReportParentRes;
import school.campusconnect.datamodel.attendance_report.AttendanceReportRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.MixOperations;

public class AttendanceDetailFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "AttendanceDetailFragment";

    @Bind(R.id.tvMonth)
    public TextView tvMonth;

    @Bind(R.id.rvStudents)
    public RecyclerView rvStudents;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    @Bind(R.id.llLeaveReq)
    public CardView llLeaveReq;

    Calendar calendar;
    private String mGroupId;
    private String teamId;
    private String userId;
    private String rollNo;

    ArrayList<AttendanceReportParentRes.AttendanceReportData> attendanceReport = new ArrayList<>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGroupId = getArguments().getString("group_id");
            teamId = getArguments().getString("team_id");
            userId = getArguments().getString("userId");
            rollNo = getArguments().getString("rollNo");
            calendar = (Calendar) getArguments().getSerializable("calendar");
        }
        if(calendar==null){
            calendar = Calendar.getInstance();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance_details, container, false);
        ButterKnife.bind(this, view);

        init();

        getAttendanceDetail();

        return view;
    }

    private void init() {
        rvStudents.setLayoutManager(new LinearLayoutManager(getActivity()));

        llLeaveReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ApplyLeaveActivity.class);
                i.putExtra("teamID",teamId);
                startActivity(i);
            }
        });
    }

    private void getAttendanceDetail() {

        attendanceReport.clear();

        tvMonth.setText(MixOperations.getMonth(calendar.getTime()).toUpperCase());
        LeafManager leafManager = new LeafManager();
        showLoadingBar(progressBar);

       // progressBar.setVisibility(View.VISIBLE);
      //  leafManager.getAttendanceDetail(this, GroupDashboardActivityNew.groupId, teamId,userId,rollNo, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        leafManager.getAttendanceReportParent(this,GroupDashboardActivityNew.groupId,teamId,calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.YEAR),userId);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @OnClick({R.id.imgLeft, R.id.imgRight})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgLeft:
                calendar.add(Calendar.MONTH, -1);
                getAttendanceDetail();
                break;
            case R.id.imgRight:
                calendar.add(Calendar.MONTH, 1);
                getAttendanceDetail();
                break;

        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
      //  progressBar.setVisibility(View.GONE);
        if (getActivity() == null)
            return;
      /*  AttendanceDetailRes res = (AttendanceDetailRes) response;
        rvStudents.setAdapter(new ReportDetailAdapter(res.getData()));*/

        AttendanceReportParentRes res = (AttendanceReportParentRes) response;

        if (res.getData().size() > 0)
        {
            attendanceReport.addAll(res.getData().get(0).getAttendanceReport());
            rvStudents.setAdapter(new ReportDetailAdapterV1(res.getData().get(0).getAttendanceReport()));
        }
        else
        {
            rvStudents.setAdapter(null);
        }

    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        AppLog.e(TAG,"onFailure"+msg);
        //  progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        AppLog.e(TAG,"onException"+msg);
        //  progressBar.setVisibility(View.GONE);
    }

    public class ReportDetailAdapterV1 extends RecyclerView.Adapter<ReportDetailAdapterV1.ViewHolder> {
        ArrayList<AttendanceReportParentRes.AttendanceReportData> attendanceReport;

        public ReportDetailAdapterV1(ArrayList<AttendanceReportParentRes.AttendanceReportData> attendanceReport) {
            this.attendanceReport = attendanceReport;
        }



        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_details_v1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final AttendanceReportParentRes.AttendanceReportData data = attendanceReport.get(position);

            holder.tvDate.setText(data.getDate()+"\n("+data.getTime()+")");
            holder.tvAttendance.setText(data.getAttendance());

        }

        @Override
        public int getItemCount() {
            if (attendanceReport != null) {
                if (attendanceReport.size() == 0) {
                    txtEmpty.setText(getResources().getString(R.string.msg_no_data_found));
                } else {
                    txtEmpty.setText("");
                }

                return attendanceReport.size();
            } else {
                txtEmpty.setText(getResources().getString(R.string.msg_no_data_found));
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.tvDate)
            TextView tvDate;

            @Bind(R.id.tvAttendance)
            TextView tvAttendance;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }


    public class ReportDetailAdapter extends RecyclerView.Adapter<ReportDetailAdapter.ViewHolder> {
        List<AttendanceDetailRes.AttendanceDetailData> listMorning;

        public ReportDetailAdapter(List<AttendanceDetailRes.AttendanceDetailData> listMorning) {
            this.listMorning = listMorning;
        }

        @Override
        public ReportDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_detail, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ReportDetailAdapter.ViewHolder holder, final int position) {
            final AttendanceDetailRes.AttendanceDetailData itemMorning = listMorning.get(position);

            holder.tvDay.setText(itemMorning.getDay() + "");

            if (TextUtils.isEmpty(itemMorning.getMorningAttendance())) {
                holder.tvMorning.setText("");
            } else {
                holder.tvMorning.setText(itemMorning.getMorningAttendance().equalsIgnoreCase("true") ? "P" : "A");
            }

            if (TextUtils.isEmpty(itemMorning.getAfternoonAttendance())) {
                holder.tvEvering.setText("");
            } else {
                holder.tvEvering.setText(itemMorning.getAfternoonAttendance().equalsIgnoreCase("true") ? "P" : "A");
            }
        }

        @Override
        public int getItemCount() {
            if (listMorning != null) {
                if (listMorning.size() == 0) {
                    txtEmpty.setText(getResources().getString(R.string.msg_no_data_found));
                } else {
                    txtEmpty.setText("");
                }

                return listMorning.size();
            } else {
                txtEmpty.setText(getResources().getString(R.string.msg_no_data_found));
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.tvDay)
            TextView tvDay;


            @Bind(R.id.tvMorning)
            TextView tvMorning;

            @Bind(R.id.tvEvering)
            TextView tvEvering;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }


    private boolean checkPermissionForWriteExternal() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            AppLog.e("External" + "permission", "checkpermission , granted");
            return true;
        } else {
            AppLog.e("External" + "permission", "checkpermission , denied");
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_storage_permission_needed), Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 21);
            }
            return false;
        }
    }
    public void exportDataToCSV() {

        if (!checkPermissionForWriteExternal()) {
            return;
        }

        if (attendanceReport.size() == 0)
        {
            Toast.makeText(getContext(),getResources().getString(R.string.toast_no_data_found),Toast.LENGTH_SHORT).show();
            return;
        }

        File mainFolder = new File(getActivity().getFilesDir(), LeafApplication.getInstance().getResources().getString(R.string.app_name));
        if (!mainFolder.exists()) {
            mainFolder.mkdir();
        }
        File csvFolder = new File(mainFolder,"Excel");
        if (!csvFolder.exists()) {
            csvFolder.mkdir();
        }
        File file = new File(csvFolder, getArguments().getString("title")+"_"+tvMonth.getText().toString() + ".xls");

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet firstSheet = workbook.createSheet(getArguments().getString("title")+"_"+tvMonth.getText().toString());

            HSSFRow rowA = firstSheet.createRow(0);
            rowA.createCell(0).setCellValue("Date");
            rowA.createCell(1).setCellValue("Attendance");

            if (attendanceReport != null)
            {
                for(int i=0;i<attendanceReport.size();i++){

                    AttendanceReportParentRes.AttendanceReportData item = attendanceReport.get(i);
                    HSSFRow rowData = firstSheet.createRow(i + 1);
                    rowData.createCell(0).setCellValue(item.getDate()+"\n( "+item.getTime()+" )");
                    rowData.createCell(1).setCellValue(item.getAttendance());
                }
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                workbook.write(fos);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            shareFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shareFile(File file) {
        Uri uriFile;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            uriFile = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".fileprovider", file);
        } else {
            uriFile = Uri.fromFile(file);
        }
        Intent sharingIntent = new Intent();
        sharingIntent.setAction(Intent.ACTION_SEND);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uriFile) ;
        sharingIntent.setType("text/csv");
        startActivity(Intent.createChooser(sharingIntent, "share file with"));
    }
}
