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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import school.campusconnect.BuildConfig;
import school.campusconnect.LeafApplication;
import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.AddClassStudentActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.GroupItem;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.student.StudentRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class ClassStudentListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;


    @Bind(R.id.etSearch)
    public EditText etSearch;

    ClassResponse.ClassData classData;
    private String mGroupId;
    private String teamId;
    private ArrayList<StudentRes.StudentData> list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss_search, container, false);
        ButterKnife.bind(this, view);

        init();

        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressBar.setVisibility(View.VISIBLE);

        return view;
    }
    public void showHideSearch(){
        if(etSearch.getVisibility()==View.VISIBLE){
            etSearch.setVisibility(View.GONE);
        }else {
            etSearch.setVisibility(View.VISIBLE);
        }
    }


    private void init() {
        if (getArguments() != null) {
            classData = new Gson().fromJson(getArguments().getString("class_data"), ClassResponse.ClassData.class);
            AppLog.e(TAG, "classData : " + classData);
            mGroupId = GroupDashboardActivityNew.groupId;
            teamId = classData.getId();
        }
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(list!=null){
                    if(!TextUtils.isEmpty(s.toString())){
                        ArrayList<StudentRes.StudentData> newList = new ArrayList<>();
                        for (int i=0;i<list.size();i++){
                            if(list.get(i).name.toLowerCase().contains(s.toString().toLowerCase())){
                                newList.add(list.get(i));
                            }
                        }
                        rvClass.setAdapter(new ClassesStudentAdapter(newList));
                    }else {
                        rvClass.setAdapter(new ClassesStudentAdapter(list));
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        etSearch.setText("");
        LeafManager leafManager = new LeafManager();
        AppLog.e(TAG, "getStudents : ");
        leafManager.getStudents(this, GroupDashboardActivityNew.groupId, classData.getId());
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);
        StudentRes res = (StudentRes) response;
        list = res.getData();
        AppLog.e(TAG, "StudentRes " + list);

        rvClass.setAdapter(new ClassesStudentAdapter(list));
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
        List<StudentRes.StudentData> list;
        private Context mContext;

        public ClassesStudentAdapter(List<StudentRes.StudentData> list) {
            this.list = list;
        }

        @Override
        public ClassesStudentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class_student, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ClassesStudentAdapter.ViewHolder holder, final int position) {
            final StudentRes.StudentData item = list.get(position);

            if (!TextUtils.isEmpty(item.getImage())) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(50, 50).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgTeam,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.img_lead_default.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(50, 50).into(holder.imgTeam, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.img_lead_default.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.img_lead_default.setVisibility(View.VISIBLE);
                                        TextDrawable drawable = TextDrawable.builder()
                                                .buildRound(ImageUtil.getTextLetter(item.getName()), ImageUtil.getRandomColor(position));
                                        holder.img_lead_default.setImageDrawable(drawable);
                                        AppLog.e("Picasso", "Error : ");
                                    }
                                });
                            }
                        });
            } else {
                holder.img_lead_default.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(item.getName()), ImageUtil.getRandomColor(position));
                holder.img_lead_default.setImageDrawable(drawable);
            }

            holder.txt_name.setText(item.getName());
            holder.txt_count.setText(item.getClass_());
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    txtEmpty.setText("No Students found.");
                } else {
                    txtEmpty.setText("");
                }

                return list.size();
            } else {
                txtEmpty.setText("No Students found.");
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

    private void editStudent(StudentRes.StudentData studentData) {
        Intent intent = new Intent(getActivity(), AddClassStudentActivity.class);
        intent.putExtra("group_id", mGroupId);
        intent.putExtra("team_id", teamId);
        intent.putExtra("className", classData.className);
        intent.putExtra("isEdit", true);
        intent.putExtra("student_data", new Gson().toJson(studentData));
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

        File mainFolder = new File(Environment.getExternalStorageDirectory(), LeafApplication.getInstance().getResources().getString(R.string.app_name));
        if (!mainFolder.exists()) {
            mainFolder.mkdir();
        }
        File csvFolder = new File(mainFolder,"Excel");
        if (!csvFolder.exists()) {
            csvFolder.mkdir();
        }
        File file = new File(csvFolder, classData.className + ".xls");

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet firstSheet = workbook.createSheet(classData.className);
            HSSFRow rowA = firstSheet.createRow(0);
            rowA.createCell(0).setCellValue("Name");
            rowA.createCell(1).setCellValue("Phone Number");
            rowA.createCell(2).setCellValue("Roll Number");
            rowA.createCell(3).setCellValue("Student Id");
            rowA.createCell(4).setCellValue("Admission Number");
            rowA.createCell(5).setCellValue("Class");
            rowA.createCell(6).setCellValue("Section");
            rowA.createCell(7).setCellValue("Date of Birth");
            rowA.createCell(8).setCellValue("Father Name");
            rowA.createCell(9).setCellValue("Mother Name");
            rowA.createCell(10).setCellValue("Father Number");
            rowA.createCell(11).setCellValue("Mother Number");
            rowA.createCell(12).setCellValue("Email");
            rowA.createCell(13).setCellValue("Address");

            if(list!=null){
                for(int i=0;i<list.size();i++){
                    StudentRes.StudentData item = list.get(i);
                    HSSFRow rowData = firstSheet.createRow(i + 1);
                    rowData.createCell(0).setCellValue(item.getName());
                    rowData.createCell(1).setCellValue(item.getPhone());
                    rowData.createCell(2).setCellValue(item.getRollNumber());
                    rowData.createCell(3).setCellValue(item.studentDbId);
                    rowData.createCell(4).setCellValue(item.admissionNumber);
                    rowData.createCell(5).setCellValue(item._class);
                    rowData.createCell(6).setCellValue(item.section);
                    rowData.createCell(7).setCellValue(item.dob);
                    rowData.createCell(8).setCellValue(item.fatherName);
                    rowData.createCell(9).setCellValue(item.motherName);
                    rowData.createCell(10).setCellValue(item.fatherNumber);
                    rowData.createCell(11).setCellValue(item.motherNumber);
                    rowData.createCell(12).setCellValue(item.email);
                    rowData.createCell(13).setCellValue(item.address);
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
