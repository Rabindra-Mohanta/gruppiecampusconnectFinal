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
import school.campusconnect.activities.AddStaffActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.staff.StaffResponse;
import school.campusconnect.datamodel.student.StudentRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class StaffListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;
    @Bind(R.id.etSearch)
    public EditText etSearch;

    boolean isAdmin;
    private ArrayList<StaffResponse.StaffData> result;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss_search,container,false);
        ButterKnife.bind(this,view);
        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        isAdmin = getArguments().getBoolean("isAdmin");

        showLoadingBar(progressBar);
        // progressBar.setVisibility(View.VISIBLE);

        init();

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
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(result!=null){
                    if(!TextUtils.isEmpty(s.toString())){
                        ArrayList<StaffResponse.StaffData> newList = new ArrayList<>();
                        for (int i=0;i<result.size();i++){
                            if(result.get(i).name.toLowerCase().contains(s.toString().toLowerCase())){
                                newList.add(result.get(i));
                            }
                        }
                        rvClass.setAdapter(new StaffAdapter(newList));
                    }else {
                        rvClass.setAdapter(new StaffAdapter(result));
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
        leafManager.getStaff(this,GroupDashboardActivityNew.groupId);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
        //progressBar.setVisibility(View.GONE);
        StaffResponse res = (StaffResponse) response;
        result = res.getData();
        AppLog.e(TAG, "ClassResponse " + result);

        rvClass.setAdapter(new StaffAdapter(result));
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        //progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        //progressBar.setVisibility(View.GONE);
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

        File mainFolder = new File(getActivity().getFilesDir(), LeafApplication.getInstance().getResources().getString(R.string.app_name));
        if (!mainFolder.exists()) {
            mainFolder.mkdir();
        }
        File csvFolder = new File(mainFolder,"Excel");
        if (!csvFolder.exists()) {
            csvFolder.mkdir();
        }
        File file = new File(csvFolder, "staff" + ".xls");

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet firstSheet = workbook.createSheet("Staff");
            HSSFRow rowA = firstSheet.createRow(0);
            rowA.createCell(0).setCellValue("Name");
            rowA.createCell(1).setCellValue("Phone Number");
            rowA.createCell(2).setCellValue("Class");
            rowA.createCell(3).setCellValue("Designation");
            rowA.createCell(4).setCellValue("Email");
            rowA.createCell(5).setCellValue("Address");
            rowA.createCell(6).setCellValue("Aadhar Number");
            rowA.createCell(7).setCellValue("Blood Group");
            rowA.createCell(8).setCellValue("Cast");
            rowA.createCell(9).setCellValue("Gender");
            rowA.createCell(10).setCellValue("Qualification");

            if(result!=null){
                for(int i=0;i<result.size();i++){
                    StaffResponse.StaffData item = result.get(i);
                    HSSFRow rowData = firstSheet.createRow(i + 1);
                    rowData.createCell(0).setCellValue(item.getName());
                    rowData.createCell(1).setCellValue(item.getPhone());
                    rowData.createCell(2).setCellValue(item.className);
                    rowData.createCell(3).setCellValue(item.designation);
                    rowData.createCell(4).setCellValue(item.email);
                    rowData.createCell(5).setCellValue(item.address);
                    rowData.createCell(6).setCellValue(item.aadharNumber);
                    rowData.createCell(7).setCellValue(item.bloodGroup);
                    rowData.createCell(8).setCellValue(item.caste);
                    rowData.createCell(9).setCellValue(item.gender);
                    rowData.createCell(10).setCellValue(item.qualification);
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

    public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.ViewHolder>
    {
        List<StaffResponse.StaffData> list;
        private Context mContext;

        public StaffAdapter(List<StaffResponse.StaffData> list) {
            this.list = list;
        }

        @Override
        public StaffAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final StaffAdapter.ViewHolder holder, final int position) {
            final StaffResponse.StaffData item = list.get(position);

            if (!TextUtils.isEmpty(item.getImage())) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(50,50).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgTeam,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.img_lead_default.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(50,50).into(holder.imgTeam, new Callback() {
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
            if(!TextUtils.isEmpty(item.designation)){
                holder.txt_count.setText("[" + item.getDesignation() + "]");
                holder.txt_count.setVisibility(View.VISIBLE);
            }else {
                holder.txt_count.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {

                    txtEmpty.setText(getResources().getString(R.string.txt_no_staff_found));
                }
                else {
                    txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                txtEmpty.setText(getResources().getString(R.string.txt_no_staff_found));
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
            @Bind(R.id.img_tree)
            ImageView img_tree;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onTreeClick(list.get(getAdapterPosition()));
                    }
                });
                img_tree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onTreeClick(list.get(getAdapterPosition()));
                    }
                });

            }
        }
    }

    private void onTreeClick(StaffResponse.StaffData classData) {
        Intent intent = new Intent(getActivity(), AddStaffActivity.class);
        intent.putExtra("group_id",GroupDashboardActivityNew.groupId);
        intent.putExtra("staff_data",new Gson().toJson(classData));
        intent.putExtra("isEdit",true);
        intent.putExtra("isAdmin",isAdmin);
        intent.putExtra("isPost",classData.isPost);
        startActivity(intent);
    }
}
