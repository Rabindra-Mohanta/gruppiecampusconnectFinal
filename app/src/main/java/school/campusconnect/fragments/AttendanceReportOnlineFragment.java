package school.campusconnect.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.BuildConfig;
import school.campusconnect.LeafApplication;
import school.campusconnect.R;
import school.campusconnect.activities.AttendanceDetailActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.attendance_report.AttendanceReportRes;
import school.campusconnect.datamodel.attendance_report.OnlineAttendanceRes;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.student.StudentRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.MixOperations;

import static school.campusconnect.network.LeafManager.API_CLASSES;

public class AttendanceReportOnlineFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvMonth)
    public RecyclerView rvMonth;
    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;
    @Bind(R.id.progressBar)
    public ProgressBar progressBar;
    private String team_id;
    private String className;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance_report_online, container, false);
        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            team_id = getArguments().getString("team_id", "");
            className = getArguments().getString("className", "");
        }

        showMonths();

        return view;
    }

    private void showMonths() {
        List<MonthData> listMonth = new ArrayList<>();
        listMonth.add(new MonthData("Jan", 1));
        listMonth.add(new MonthData("Feb", 2));
        listMonth.add(new MonthData("Mar", 3));
        listMonth.add(new MonthData("Apr", 4));
        listMonth.add(new MonthData("May", 5));
        listMonth.add(new MonthData("Jun", 6));
        listMonth.add(new MonthData("Jul", 7));
        listMonth.add(new MonthData("Aug", 8));
        listMonth.add(new MonthData("Sep", 9));
        listMonth.add(new MonthData("Oct", 10));
        listMonth.add(new MonthData("Nob", 11));
        listMonth.add(new MonthData("Dec", 11));
        MonthAdapter adapter = new MonthAdapter(listMonth);
        rvMonth.setAdapter(adapter);
    }

    private void getAttendanceReport(int month) {
        progressBar.setVisibility(View.VISIBLE);
        LeafManager leafManager = new LeafManager();
        leafManager.getAttendanceReportOnline(this, GroupDashboardActivityNew.groupId, team_id, month, Calendar.getInstance().get(Calendar.YEAR));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void onClick(View view) {

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);
        if (getActivity() == null)
            return;

        OnlineAttendanceRes res = (OnlineAttendanceRes) response;
        if (res.data != null && res.data.size() > 0) {
            exportDataToCSV(res.data);
        } else {
            Toast.makeText(getActivity(), "No Data Found", Toast.LENGTH_SHORT).show();
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

    public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.ViewHolder> {
        List<MonthData> list;
        private Context mContext;

        public MonthAdapter(List<MonthData> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_month_report, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final MonthData item = list.get(position);
            holder.txt_name.setText(item.dispName);
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    txtEmpty.setText("No Data found.");
                } else {
                    txtEmpty.setText("");
                }

                return list.size();
            } else {
                txtEmpty.setText("No Data found.");
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.txt_name)
            TextView txt_name;

            @Bind(R.id.img_tree)
            Button img_tree;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                img_tree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!checkPermissionForWriteExternal()) {
                            return;
                        }
                        getAttendanceReport(list.get(getAdapterPosition()).month);
                    }
                });
            }
        }
    }

    public static class MonthData {
        public String dispName;
        public int month;

        public MonthData(String dispName, int month) {
            this.dispName = dispName;
            this.month = month;
        }

        public MonthData() {
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
                Toast.makeText(getActivity(), "Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 21);
            }
            return false;
        }
    }

    public void exportDataToCSV(ArrayList<HashMap<String, String>> data) {
        File mainFolder = new File(Environment.getExternalStorageDirectory(), LeafApplication.getInstance().getResources().getString(R.string.app_name));
        if (!mainFolder.exists()) {
            mainFolder.mkdir();
        }
        File csvFolder = new File(mainFolder, "Excel");
        if (!csvFolder.exists()) {
            csvFolder.mkdir();
        }
        File file = new File(csvFolder, className + "_.xls");

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet firstSheet = workbook.createSheet(className);
            HSSFRow rowA = firstSheet.createRow(0);
            HashMap<String, String> item1 = data.get(0);
            Iterator it1 = item1.entrySet().iterator();
            int cnt = 0;
            while (it1.hasNext()) {
                Map.Entry pair = (Map.Entry) it1.next();
                rowA.createCell(cnt).setCellValue(pair.getKey().toString());
                cnt++;
            }
            for (int i = 0; i < data.size(); i++) {
                HSSFRow rowData = firstSheet.createRow(i + 1);
                HashMap<String, String> item = data.get(i);
                Iterator it = item.entrySet().iterator();
                cnt = 0;
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    rowData.createCell(cnt).setCellValue(pair.getValue().toString());
                    cnt++;
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
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uriFile);
        sharingIntent.setType("text/csv");
        startActivity(Intent.createChooser(sharingIntent, "share file with"));
    }
}
