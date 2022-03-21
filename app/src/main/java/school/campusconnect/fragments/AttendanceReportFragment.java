package school.campusconnect.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import school.campusconnect.activities.AttendanceDetailActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.adapters.FixedGridLayoutManager;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.attendance_report.AttendanceReportRes;
import school.campusconnect.datamodel.attendance_report.AttendanceReportResv2;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.student.StudentRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.MixOperations;

import static school.campusconnect.network.LeafManager.API_CLASSES;

public class AttendanceReportFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.spClass)
    public Spinner spClass;

    @Bind(R.id.imgLeft)
    public ImageView imgLeft;
    @Bind(R.id.tvMonth)
    public TextView tvMonth;
    @Bind(R.id.imgRight)
    public ImageView imgRight;

    @Bind(R.id.rvStudents)
    public RecyclerView rvStudents;


    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;
    @Bind(R.id.llClass)
    public LinearLayout llClass;


    @Bind(R.id.progressBar)
    public ProgressBar progressBar;
    Calendar calendar;
    private ArrayList<ClassResponse.ClassData> listClass;
    private ArrayList<AttendanceReportRes.AttendanceReportData> attendanceReportList;
    private ArrayList<AttendanceReportResv2.AttendanceReportData> attendanceReportListv2;
    private String selectedTeamId="";
    private String className="";
    private String classNameExcel="";

    private int ColumnCount = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance_report,container,false);
        ButterKnife.bind(this,view);

        init();



        if(getArguments()!=null){
            selectedTeamId=getArguments().getString("team_id","");
            className=getArguments().getString("className","");
        }

        if(TextUtils.isEmpty(selectedTeamId)){
            LeafManager leafManager = new LeafManager();
            leafManager.getClasses(this,GroupDashboardActivityNew.groupId);
        }else {
            llClass.setVisibility(View.GONE);
            getAttendanceReport();
        }


        return view;
    }

    private void init() {









        calendar = Calendar.getInstance();

        ColumnCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + 2;
        Log.e(TAG,"RowCount"+ColumnCount);
        FixedGridLayoutManager fixedGridLayoutManager = new FixedGridLayoutManager();
        fixedGridLayoutManager.setTotalColumnCount(5);
        rvStudents.setLayoutManager(fixedGridLayoutManager);

        spClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(listClass!=null){
                    selectedTeamId = listClass.get(i).getId();
                    classNameExcel = listClass.get(i).getName();
                    getAttendanceReport();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getAttendanceReport() {
        progressBar.setVisibility(View.VISIBLE);
        tvMonth.setText(MixOperations.getMonth(calendar.getTime()).toUpperCase());
        LeafManager leafManager = new LeafManager();
        leafManager.getAttendanceReportOffline(this,GroupDashboardActivityNew.groupId,selectedTeamId,calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.YEAR));
    }

    @Override
    public void onStart() {
        super.onStart();
    }
    @OnClick({R.id.imgLeft,R.id.imgRight})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.imgLeft:
                calendar.add(Calendar.MONTH,-1);
                getAttendanceReport();
                break;
            case R.id.imgRight:
                calendar.add(Calendar.MONTH,1);
                getAttendanceReport();
                break;

        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);
        if(getActivity()==null)
            return;

        if(apiId==API_CLASSES){
            ClassResponse res = (ClassResponse) response;
            listClass = res.getData();
            String[] stud = new String[listClass.size()];
            for (int i=0;i<listClass.size();i++){
                stud[i] = listClass.get(i).getName();
            }
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,stud);
            spClass.setAdapter(adapter);
        }
        else {
            AttendanceReportResv2 res = (AttendanceReportResv2) response;
            attendanceReportListv2 = res.result;

            rvStudents.setAdapter(new ReportStudentAdapterV2(res.result));
        }

    }

    @Override
    public void onFailure(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }

    public class ReportStudentAdapter extends RecyclerView.Adapter<ReportStudentAdapter.ViewHolder>
    {
        List<AttendanceReportRes.AttendanceReportData> list;
        private Context mContext;

        public ReportStudentAdapter(List<AttendanceReportRes.AttendanceReportData> list) {
            this.list = list;
        }

        @Override
        public ReportStudentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_student,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ReportStudentAdapter.ViewHolder holder, final int position) {
            final AttendanceReportRes.AttendanceReportData item = list.get(position);

            holder.tvRollNo.setText(item.getRollNumber());
            holder.tvName.setText(item.getStudentName());
            holder.tvMorning.setText(item.getMorningPresentCount()+"("+item.getTotalMorningAttendance()+")");
            holder.tvEvering.setText(item.getAfternoonPresentCount()+"("+item.getTotalAfternoonAttendance()+")");
        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    txtEmpty.setText("No Data found.");
                }else {
                    txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                txtEmpty.setText("No Data found.");
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.tvRollNo) TextView tvRollNo;

            @Bind(R.id.tvName) TextView tvName;

            @Bind(R.id.tvMorning) TextView tvMorning;

            @Bind(R.id.tvEvering) TextView tvEvering;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editStudent(list.get(getAdapterPosition()));
                    }
                });
            }
        }
    }

    public class ReportStudentAdapterV2 extends RecyclerView.Adapter<ReportStudentAdapterV2.ViewHolder>
    {
        List<AttendanceReportResv2.AttendanceReportData> list;
        private Context mContext;

        public ReportStudentAdapterV2(List<AttendanceReportResv2.AttendanceReportData> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_student_date,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            Log.e(TAG,"onBindViewHolder"+getItemCount());
        /*    final AttendanceReportResv2.AttendanceReportData item = list.get(position);*/



        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    txtEmpty.setText("No Data found.");
                    return ColumnCount * 5;
                }else {
                    txtEmpty.setText("");
                }
                return ColumnCount * 5;
              //  return list.size();
            }
            else
            {
                txtEmpty.setText("No Data found.");
                return ColumnCount * 5;
            }

        }



        public class ViewHolder extends RecyclerView.ViewHolder {

            /*@Bind(R.id.tvStudentData)
            TextView tvStudentData;*/



            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);

               /* itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editStudent(list.get(getAdapterPosition()));
                    }
                });*/
            }
        }
    }

    private void editStudent(AttendanceReportRes.AttendanceReportData studentData) {
        Intent intent = new Intent(getActivity(), AttendanceDetailActivity.class);
        intent.putExtra("group_id",GroupDashboardActivityNew.groupId);
        intent.putExtra("team_id",selectedTeamId);
        intent.putExtra("title",studentData.getStudentName()+"- ("+className+")");
        intent.putExtra("calendar",calendar);
        intent.putExtra("userId",studentData.getUserId());
        intent.putExtra("rollNo",studentData.getRollNumber());
        startActivity(intent);
    }


    private boolean checkPermissionForWriteExternal() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            AppLog.e("External" + "permission", "checkpermission , granted");
            return true;
        } else {
            AppLog.e("External" + "permission", "checkpermission , denied");
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(getActivity(), "Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
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

        File mainFolder = new File(getActivity().getFilesDir(), LeafApplication.getInstance().getResources().getString(R.string.app_name));
        if (!mainFolder.exists()) {
            mainFolder.mkdir();
        }
        File csvFolder = new File(mainFolder,"Excel");
        if (!csvFolder.exists()) {
            csvFolder.mkdir();
        }
        File file = new File(csvFolder, classNameExcel+"_"+tvMonth.getText().toString() + ".xls");

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet firstSheet = workbook.createSheet(classNameExcel+"_"+tvMonth.getText().toString());

            HSSFRow rowA = firstSheet.createRow(0);
            rowA.createCell(0).setCellValue("Roll No");
            rowA.createCell(1).setCellValue("Name");
            rowA.createCell(2).setCellValue("Morning Attendance");
            rowA.createCell(3).setCellValue("Evening Attendance");


            if (attendanceReportList != null)
            {
                for(int i=0;i<attendanceReportList.size();i++){

                    AttendanceReportRes.AttendanceReportData item = attendanceReportList.get(i);
                    HSSFRow rowData = firstSheet.createRow(i + 1);
                    rowData.createCell(0).setCellValue(item.getRollNumber());
                    rowData.createCell(1).setCellValue(item.getStudentName());
                    rowData.createCell(2).setCellValue(item.getMorningPresentCount()+"("+item.getTotalMorningAttendance()+")");
                    rowData.createCell(3).setCellValue(item.getAfternoonPresentCount()+"("+item.getTotalAfternoonAttendance()+")");

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
