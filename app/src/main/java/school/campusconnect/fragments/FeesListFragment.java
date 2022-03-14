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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.BuildConfig;
import school.campusconnect.LeafApplication;
import school.campusconnect.R;
import school.campusconnect.activities.AddClassStudentActivity;
import school.campusconnect.activities.FeesListActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.UpdateStudentFeesActivity;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.fees.DueDates;
import school.campusconnect.datamodel.fees.FeePaidDetails;
import school.campusconnect.datamodel.fees.StudentFeesRes;
import school.campusconnect.datamodel.student.StudentRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class FeesListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    private String mGroupId;
    private String teamId;
    private String role;
    private String className;
    private ArrayList<StudentFeesRes.StudentFees> list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss, container, false);
        ButterKnife.bind(this, view);

        init();

        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressBar.setVisibility(View.VISIBLE);

        return view;
    }

    private void init() {
        if (getArguments() != null) {
            mGroupId = GroupDashboardActivityNew.groupId;
            teamId = getArguments().getString("team_id");
            role = getArguments().getString("role");
            className = getArguments().getString("title");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LeafManager leafManager = new LeafManager();
        AppLog.e(TAG, "getStudents : ");
        leafManager.getStudentFeesList(this, GroupDashboardActivityNew.groupId, teamId);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);
        StudentFeesRes res = (StudentFeesRes) response;
        list = res.getData();
        AppLog.e(TAG, "StudentRes " + list);

        rvClass.setAdapter(new ClassesStudentAdapter(list));

        if(getActivity()==null){
            return;
        }
        if (list != null && list.size() > 0) {
            ((FeesListActivity) getActivity()).setOptionMenuName("Update Fees");
        } else {
            ((FeesListActivity) getActivity()).setOptionMenuName("Add Fees");
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


    public class ClassesStudentAdapter extends RecyclerView.Adapter<ClassesStudentAdapter.ViewHolder> {
        List<StudentFeesRes.StudentFees> list;
        private Context mContext;

        public ClassesStudentAdapter(List<StudentFeesRes.StudentFees> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_fees_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final StudentFeesRes.StudentFees item = list.get(position);

            if (!TextUtils.isEmpty(item.getStudentImage())) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getStudentImage())).resize(50, 50).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgTeam,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.img_lead_default.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getStudentImage())).resize(50, 50).into(holder.imgTeam, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.img_lead_default.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.img_lead_default.setVisibility(View.VISIBLE);
                                        TextDrawable drawable = TextDrawable.builder()
                                                .buildRound(ImageUtil.getTextLetter(item.getStudentName()), ImageUtil.getRandomColor(position));
                                        holder.img_lead_default.setImageDrawable(drawable);
                                        AppLog.e("Picasso", "Error : ");
                                    }
                                });
                            }
                        });
            } else {
                holder.img_lead_default.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(item.getStudentName()), ImageUtil.getRandomColor(position));
                holder.img_lead_default.setImageDrawable(drawable);
            }
            holder.txt_name.setText(item.getStudentName());
            holder.txt_count.setText("Total Balance Amount : " + (TextUtils.isEmpty(item.getTotalBalanceAmount()) ? "0" : item.getTotalBalanceAmount()));
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    txtEmpty.setText("No Fee Created.");
                } else {
                    txtEmpty.setText("");
                }

                return list.size();
            } else {
                txtEmpty.setText("No Fee Created.");
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.img_lead)
            ImageView imgTeam;

            @Bind(R.id.img_lead_default)
            ImageView img_lead_default;

            @Bind(R.id.txt_name)
            TextView txt_name;

            @Bind(R.id.txt_count)
            TextView txt_count;

            @Bind(R.id.img_Edit)
            ImageView img_Edit;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editStudent(list.get(getAdapterPosition()));
                    }
                });
            }
        }
    }

    private void editStudent(StudentFeesRes.StudentFees studentData) {
        Intent intent = new Intent(getActivity(), UpdateStudentFeesActivity.class);
        intent.putExtra("group_id", mGroupId);
        intent.putExtra("team_id", teamId);
        intent.putExtra("title", studentData.studentName + " - (" + className + ")");
        intent.putExtra("role", role);
        intent.putExtra("StudentFees", new Gson().toJson(studentData));
        startActivity(intent);
    }

    private boolean checkPermissionForWriteExternal() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            Log.e("External" + "permission", "checkpermission , granted");
            return true;
        } else {
            Log.e("External" + "permission", "checkpermission , denied");
            return false;
        }
    }

    public void exportDataToCSV() {
        if (!checkPermissionForWriteExternal()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(getActivity(), "Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
            } else {
                AppLog.e(TAG, "requestPermissionForWriteExternal");
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1111);
            }
            return;
        }

        File mainFolder = new File(Environment.getExternalStorageDirectory(), LeafApplication.getInstance().getResources().getString(R.string.app_name));
        if (!mainFolder.exists()) {
            mainFolder.mkdir();
        }
        File csvFolder = new File(mainFolder, "Excel");
        if (!csvFolder.exists()) {
            csvFolder.mkdir();
        }
        File file = new File(csvFolder, className+ " (Fees)" + ".xls");

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet firstSheet = workbook.createSheet(className);
            HSSFRow rowA = firstSheet.createRow(0);
            rowA.createCell(0).setCellValue("Name");
            rowA.createCell(1).setCellValue("Phone Number");
            rowA.createCell(2).setCellValue("Roll Number");
            rowA.createCell(3).setCellValue("Student Id");
            rowA.createCell(5).setCellValue("Class");
            rowA.createCell(6).setCellValue("Total Fee");
            rowA.createCell(7).setCellValue("Total Amount Paid");
            rowA.createCell(8).setCellValue("Total Due Amount");
            rowA.createCell(9).setCellValue("Fee Paid Detail");
            rowA.createCell(10).setCellValue("Due Date Detail");

            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    StudentFeesRes.StudentFees item = list.get(i);
                    HSSFRow rowData = firstSheet.createRow(i + 1);
                    rowData.createCell(0).setCellValue(item.getStudentName());
                    rowData.createCell(1).setCellValue(item.getPhone()); // phone
                    rowData.createCell(2).setCellValue(item.getStudentRollNumber());
                    rowData.createCell(3).setCellValue(item.userId);
                    rowData.createCell(5).setCellValue(className);
                    rowData.createCell(6).setCellValue(item.totalFee);
                    rowData.createCell(7).setCellValue(item.totalAmountPaid);

                    if(!TextUtils.isEmpty(item.totalFee) && !TextUtils.isEmpty(item.totalAmountPaid)){
                        int dueAmount = Integer.parseInt(item.totalFee) - Integer.parseInt(item.totalAmountPaid);
                        rowData.createCell(8).setCellValue(dueAmount+"");
                    }else {
                        rowData.createCell(8).setCellValue("0");
                    }
                    StringBuilder feesDetail = new StringBuilder();
                    if(item.feePaidDetails!=null && item.feePaidDetails.size()>0){
                        for (int j=0;j<item.feePaidDetails.size();j++){
                            FeePaidDetails jData = item.feePaidDetails.get(j);
                            feesDetail.append(jData.paidDate).append(" ").append(jData.getAmountPaid()).append(" ").append(jData.status).append("\n");
                        }
                    }
                    rowData.createCell(9).setCellValue(feesDetail.toString());

                    StringBuilder dueDetail = new StringBuilder();
                    if(item.dueDates!=null && item.dueDates.size()>0){
                        for (int j=0;j<item.dueDates.size();j++){
                            DueDates jData = item.dueDates.get(j);
                            dueDetail.append(jData.getDate()).append(" ").append(jData.getMinimumAmount()).append(" ").append(jData.getStatus1()).append("\n");
                        }
                    }
                    rowData.createCell(10).setCellValue(dueDetail.toString());
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
