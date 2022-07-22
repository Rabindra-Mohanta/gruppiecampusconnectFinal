package school.campusconnect.activities;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.BuildConfig;
import school.campusconnect.LeafApplication;
import school.campusconnect.R;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.staff.StaffAttendanceRes;

import school.campusconnect.fragments.DatePickerFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
public class StaffAttendanceReport extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    @Bind(R.id.attendanceReportRv)
    public RecyclerView attendanceReportRv;
    @Bind(R.id.progressBar)
    public ProgressBar progressBar;
    @Bind(R.id.date)
    public TextView date;
    @Bind(R.id.imgDatePicker)
    public ImageView imgDatePicker;
    private String DateStr;
    StaffAttendance adapter;
    Calendar calendar;
    int day, month, year;
    LeafManager leafManager;
    StaffAttendanceRes.StaffAttendData data;
    ArrayList<StaffAttendanceRes.StaffAttendData> staffAttendData1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_staff_attendance_report);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setTitle(getResources().getString(R.string.lbl_attendance_report));
        setBackEnabled(true);
        init();

    }

    public class StaffAttendance extends RecyclerView.Adapter<StaffAttendanceReport.ViewHOlder> {
        ArrayList<StaffAttendanceRes.StaffAttendData> staffAttendData;
        ArrayList<String> morningAttendance = new ArrayList<>();
        ArrayList<String> afterNoonAttendance = new ArrayList<>();

        @NonNull
        @Override
        public StaffAttendanceReport.ViewHOlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff_attendance_report, parent, false);
            return new ViewHOlder(root);
        }

        @Override
        public void onBindViewHolder(@NonNull StaffAttendanceReport.ViewHOlder holder, int position) {
            data = staffAttendData.get(position);
            holder.tvStaffName.setText(data.getName());
            if (data.getAttendance() != null && data.getAttendance().size() > 0) {
                for (int i = 0; i < data.getAttendance().size(); i++) {
                    if (data.getAttendance().get(i).getSession().equalsIgnoreCase("morning")) {
                        if (data.getAttendance().get(i).getAttendance().equalsIgnoreCase("present")) {
                            holder.Morning.setText("P");
                        } else {
                            holder.Morning.setText("A");
                        }
                    }
                    if (data.getAttendance().get(i).getSession().equalsIgnoreCase("afternoon")) {
                        if (data.getAttendance().get(i).getAttendance().equalsIgnoreCase("present")) {
                            holder.afterNoon.setText("P");
                        } else {
                            holder.afterNoon.setText("A");
                        }
                    }
                }


            } else {
                holder.afterNoon.setText("");
                holder.Morning.setText("");


            }


        }

        @Override
        public int getItemCount() {
            return staffAttendData != null ? staffAttendData.size() : 0;

        }

        public void add(ArrayList<StaffAttendanceRes.StaffAttendData> staffAttendData) {
            this.staffAttendData = staffAttendData;

            notifyDataSetChanged();
        }
    }

    public class ViewHOlder extends RecyclerView.ViewHolder {
        TextView tvStaffName;
        TextView Morning;
        TextView afterNoon;

        public ViewHOlder(@NonNull View itemView) {
            super(itemView);

            tvStaffName = itemView.findViewById(R.id.tvStaffName);
            Morning = itemView.findViewById(R.id.Morning);
            afterNoon = itemView.findViewById(R.id.afterNoon);


        }
    }


    private void init() {

        leafManager = new LeafManager();

        calendar = Calendar.getInstance();

        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);
        setInitialData();
        imgDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageDatePickerClick();
            }
        });

    }

    private void apiCall(Integer day, Integer month, Integer year) {
        progressBar.setVisibility(View.VISIBLE);
        adapter = new StaffAttendance();
        attendanceReportRv.setAdapter(adapter);



        leafManager.getStaffAttendance(this, GroupDashboardActivityNew.groupId, day, month, year);
        leafManager.getStaffAttendanceOfFullMornt(this, GroupDashboardActivityNew.groupId, month, year);
    }

    void setInitialData() {
        DateStr=day+"-"+month+"-"+year;
        date.setText(DateStr);
    }

    void onImageDatePickerClick() {
        DatePickerFragment fragment2 = DatePickerFragment.newInstance();
        fragment2.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
            @Override
            public void onDateSelected(Calendar c) {
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                date.setText(format.format(c.getTime()));

                day = c.get(Calendar.DAY_OF_MONTH);
                month = c.get(Calendar.MONTH) + 1;
                year = c.get(Calendar.YEAR);
                apiCall(day, month, year);
            }
        });
        fragment2.setTitle(R.string.select_date);
        fragment2.show(getSupportFragmentManager(), "datepicker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_print_attendance_report, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_print_attendance_list:

                exportDataToCSV();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        apiCall(day, month, year);
        super.onStart();
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        progressBar.setVisibility(View.GONE);
        if (LeafManager.API_STAFF_ATTENDACNCE == apiId) {
            StaffAttendanceRes res = (StaffAttendanceRes) response;

            adapter.add(res.getStaffAttendData());

        }

        if (LeafManager.API_STAFF_ATTENDACNCE_FULL_MORNTh == apiId) {
            StaffAttendanceRes res = (StaffAttendanceRes) response;

            if(staffAttendData1!=null)
            {
                staffAttendData1.clear();
            }
            Log.e("Data Data","Data Data:"+new Gson().toJson(res));
            staffAttendData1 = res.getStaffAttendData();
        }
        super.onSuccess(apiId, response);


    }

    @Override
    public void onException(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_something_went_wrong), Toast.LENGTH_LONG).show();
        super.onException(apiId, msg);
    }

    @Override
    public void onFailure(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_something_went_wrong), Toast.LENGTH_LONG).show();
        super.onFailure(apiId, msg);
    }


    public void exportDataToCSV() {
        if (!checkPermissionForWriteExternal()) {
            return;
        }

        File mainFolder = new File(this.getFilesDir(), LeafApplication.getInstance().getResources().getString(R.string.app_name));
        if (!mainFolder.exists()) {
            mainFolder.mkdir();
        }
        File csvFolder = new File(mainFolder, "Excel");
        if (!csvFolder.exists()) {
            csvFolder.mkdir();
        }
        File file = new File(csvFolder, data + ".xls");

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet firstSheet = workbook.createSheet("Staff Attendance Report");
            HSSFRow rowA = firstSheet.createRow(0);
            rowA.createCell(0).setCellValue("Name");
            int l=1;



            for(int k=0;k<staffAttendData1.size();k++)

            {
                for(int i=0;i<staffAttendData1.get(k).getAttendance().size();i++)
                {


                    rowA.createCell(i+l).setCellValue(staffAttendData1.get(k).getAttendance().get(i).getSession()+" ("+staffAttendData1.get(k).getAttendance().get(i).getDate()+")");



                }

            }




            if (staffAttendData1.size() > 0) {


                for (int j=0;j<staffAttendData1.size();j++)
                {

                    StaffAttendanceRes.StaffAttendData item = staffAttendData1.get(j);
                    HSSFRow rowData = firstSheet.createRow(j + 1);
                    rowData.createCell(0).setCellValue(item.getName());
                    int l1=1;
                    for (int i = 0; i < item.getAttendance().size(); i++) {

                        StaffAttendanceRes.AttendanceData staffAttendData = item.getAttendance().get(i);

                        rowData.createCell(l1+i).setCellValue(staffAttendData.getAttendance());



                    }

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


    private boolean checkPermissionForWriteExternal() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            AppLog.e("External" + "permission", "checkpermission , granted");
            return true;
        } else {
            AppLog.e("External" + "permission", "checkpermission , denied");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 21);
            }
            return false;
        }
    }


    private void shareFile(File file) {
        Uri uriFile;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            uriFile = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", file);
        } else {
            uriFile = Uri.fromFile(file);
        }
        Intent sharingIntent = new Intent();
        sharingIntent.setAction(Intent.ACTION_SEND);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uriFile);
        sharingIntent.setType("text/csv");
        startActivity(Intent.createChooser(sharingIntent, "share file with"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 21: {
                int gratResult = grantResults[0];
                if (gratResult == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                }

                break;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}




