package school.campusconnect.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.baoyz.widget.PullRefreshLayout;
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
import school.campusconnect.activities.AddBoothStudentActivity;
import school.campusconnect.activities.AddClassStudentActivity;
import school.campusconnect.activities.AddCommiteeActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.UpdateMemberActivity;
import school.campusconnect.activities.VoterProfileActivity;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.LeadItem;
import school.campusconnect.datamodel.booths.BoothMemberResponse;
import school.campusconnect.datamodel.booths.BoothResponse;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.committee.CommitteeMemberTBL;
import school.campusconnect.datamodel.committee.committeeResponse;
import school.campusconnect.datamodel.lead.LeadDataTBL;
import school.campusconnect.datamodel.student.StudentRes;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class BoothStudentListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "BoothStudentListFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    @Bind(R.id.ll_admindetail)
    public CardView ll_admindetail;

    @Bind(R.id.tv_adminname)
    public TextView tv_adminname;

    @Bind(R.id.img_call)
    public ImageView img_call;

    @Bind(R.id.etSearch)
    public EditText etSearch;

    @Bind(R.id.swipeRefreshLayout)
    public PullRefreshLayout swipeRefreshLayout;

    private int REQUEST_UPDATE_PROFILE = 9;

    MyTeamData classData;
    committeeResponse.committeeData committeeData;
    private String mGroupId;
    private String teamId;

    private ArrayList<BoothMemberResponse.BoothMemberData> list = new ArrayList<>();

    ClassesStudentAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss_search, container, false);
        ButterKnife.bind(this, view);

        init();

        getDataLocally();

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
            classData = new Gson().fromJson(getArguments().getString("class_data"), MyTeamData.class);
            committeeData = new Gson().fromJson(getArguments().getString("committee_data"), committeeResponse.committeeData.class);
            AppLog.e(TAG, "committeeData : " + committeeData);
            AppLog.e(TAG, "classData : " + classData);
            mGroupId = GroupDashboardActivityNew.groupId;
            teamId = classData.teamId;
        }

        adapter = new ClassesStudentAdapter();
        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvClass.setAdapter(adapter);


        swipeRefreshLayout.setEnabled(false);

        /*swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    CommitteeMemberTBL.deleteCommitteeMember(GroupDashboardActivityNew.groupId, classData.teamId,committeeData.getCommitteeId());
                    getDataLocally();
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    showNoNetworkMsg();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });*/

        if(classData !=null && classData.adminName !=null && !classData.adminName.equalsIgnoreCase(""))
        {
            ll_admindetail.setVisibility(View.VISIBLE);
            tv_adminname.setText(classData.adminName);

            if(classData.phone!=null && !classData.phone.equalsIgnoreCase(""))
                img_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        AppLog.e(TAG ,"ONCLICK called for call : "+classData.phone);
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + classData.phone));
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(intent);
                        }
                        else
                        {
                            AppLog.e(TAG ,"Activity Not Found");
                        }

                    }
                });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        etSearch.setText("");

        if(LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ADD_FRIEND)){
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ADD_FRIEND, false);
            getData();
        }

    }


    private void getDataLocally() {

        List<CommitteeMemberTBL> memberTBLList = CommitteeMemberTBL.getCommitteeMember(GroupDashboardActivityNew.groupId,classData.teamId,committeeData.getCommitteeId());

        list.clear();
        
        if (memberTBLList.size()>0)
        {
            for (int i = 0; i < memberTBLList.size();i++)
            {
                BoothMemberResponse.BoothMemberData data = new BoothMemberResponse.BoothMemberData();
                data.phone = memberTBLList.get(i).phone;
                data.countryCode = memberTBLList.get(i).countryCode;
                data.name = memberTBLList.get(i).name;
                data.image = memberTBLList.get(i).image;
                data.voterId = memberTBLList.get(i).voterId;
                data.id = memberTBLList.get(i).id;
                data.gender = memberTBLList.get(i).gender;
                data.dob = memberTBLList.get(i).dob;
                data.bloodGroup = memberTBLList.get(i).bloodGroup;
                data.aadharNumber = memberTBLList.get(i).aadharNumber;
                data.allowedToAddUser = memberTBLList.get(i).allowedToAddUser;
                data.allowedToAddTeamPost = memberTBLList.get(i).allowedToAddTeamPost;
                data.allowedToAddTeamPostComment = memberTBLList.get(i).allowedToAddTeamPostComment;
                data.salary = memberTBLList.get(i).salary;
                data.roleOnConstituency = memberTBLList.get(i).roleOnConstituency;
                list.add(data);

            }
          adapter.add(list);
        }
        else
        {
            getData();
        }
    }


    private void getData()
    {
        showLoadingBar(progressBar);
    //    progressBar.setVisibility(View.VISIBLE);
        LeafManager leafManager = new LeafManager();
        leafManager.getBoothMember(this, GroupDashboardActivityNew.groupId, classData.teamId,committeeData.getCommitteeId());
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
       // progressBar.setVisibility(View.GONE);
        BoothMemberResponse res = (BoothMemberResponse) response;
        saveToLocal(res.getData());

    }

    private void saveToLocal(ArrayList<BoothMemberResponse.BoothMemberData> data) {

        CommitteeMemberTBL.deleteCommitteeMember(GroupDashboardActivityNew.groupId, classData.teamId,committeeData.getCommitteeId());

        if (data.size() >  0)
        {
            for (int i = 0;i<data.size();i++)
            {
                CommitteeMemberTBL memberTBL = new CommitteeMemberTBL();
                memberTBL.teamId = classData.teamId;
                memberTBL.groupId = GroupDashboardActivityNew.groupId;
                memberTBL.committeeId = committeeData.getCommitteeId();
                memberTBL.phone = data.get(i).phone;
                memberTBL.countryCode = data.get(i).countryCode;
                memberTBL.name = data.get(i).name;
                memberTBL.image = data.get(i).image;
                memberTBL.id = data.get(i).id;
                memberTBL.voterId = data.get(i).voterId;
                memberTBL.gender = data.get(i).gender;
                memberTBL.dob = data.get(i).dob;
                memberTBL.bloodGroup = data.get(i).bloodGroup;
                memberTBL.aadharNumber = data.get(i).aadharNumber;
                memberTBL.allowedToAddUser = data.get(i).allowedToAddUser;
                memberTBL.allowedToAddTeamPostComment = data.get(i).allowedToAddTeamPostComment;
                memberTBL.allowedToAddTeamPost = data.get(i).allowedToAddTeamPost;
                memberTBL.roleOnConstituency = data.get(i).roleOnConstituency;
                memberTBL.salary = data.get(i).salary;
                memberTBL.save();

            }
            adapter.add(data);
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        // progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        // progressBar.setVisibility(View.GONE);
    }



    private boolean checkPermissionForWriteExternal() {
        int result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            AppLog.e("External" + "permission", "checkpermission , granted");
            return true;
        } else {
            AppLog.e("External" + "permission", "checkpermission , denied");
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(getContext(), getResources().getString(R.string.toast_storage_permission_needed), Toast.LENGTH_LONG).show();
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
        File file = new File(csvFolder, getArguments().getString("title")+"_"+getResources().getString(R.string.lbl_members) + ".xls");

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet firstSheet = workbook.createSheet(getArguments().getString("title")+"_"+getResources().getString(R.string.lbl_members));

            HSSFRow rowA = firstSheet.createRow(0);
            rowA.createCell(0).setCellValue("Name");
            rowA.createCell(1).setCellValue("Voter Id");
            rowA.createCell(3).setCellValue("Phone Number");
            rowA.createCell(4).setCellValue("DOB");
            rowA.createCell(5).setCellValue("Gender");
            rowA.createCell(6).setCellValue("Blood Group");


            if (list != null)
            {
                for(int i=0;i<list.size();i++){

                    BoothMemberResponse.BoothMemberData item = list.get(i);
                    HSSFRow rowData = firstSheet.createRow(i + 1);
                    rowData.createCell(0).setCellValue(item.name);
                    rowData.createCell(1).setCellValue(item.voterId);
                    rowData.createCell(3).setCellValue(item.phone);
                    rowA.createCell(4).setCellValue(item.dob);
                    rowA.createCell(5).setCellValue(item.gender);
                    rowA.createCell(6).setCellValue(item.bloodGroup);

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


    public class ClassesStudentAdapter extends RecyclerView.Adapter<ClassesStudentAdapter.ViewHolder> {
        List<BoothMemberResponse.BoothMemberData> list;
        private Context mContext;

        public void add(List<BoothMemberResponse.BoothMemberData> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class_student, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final BoothMemberResponse.BoothMemberData item = list.get(position);

            if (!TextUtils.isEmpty(item.image)) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.image)).resize(50,50).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgTeam,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.img_lead_default.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.image)).resize(50,50).into(holder.imgTeam, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.img_lead_default.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.img_lead_default.setVisibility(View.VISIBLE);
                                        TextDrawable drawable = TextDrawable.builder()
                                                .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position));
                                        holder.img_lead_default.setImageDrawable(drawable);
                                        AppLog.e("Picasso", "Error : ");
                                    }
                                });
                            }
                        });
            } else {
                holder.img_lead_default.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position));
                holder.img_lead_default.setImageDrawable(drawable);
            }


            holder.txt_name.setText(item.name);
            holder.txt_count.setText(getResources().getString(R.string.lbl_role)+" : "+item.roleOnConstituency);

            holder.img_tree.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    txtEmpty.setText(getResources().getString(R.string.txt_no_booth_member_found));
                } else {
                    txtEmpty.setText("");
                }

                return list.size();
            } else {
                txtEmpty.setText(getResources().getString(R.string.txt_no_booth_member_found));
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

    private void editStudent(BoothMemberResponse.BoothMemberData studentData) {

        if (GroupDashboardActivityNew.isAdmin || classData.isTeamAdmin)
        {
            Intent i = new Intent(getActivity(), VoterProfileActivity.class);
            i.putExtra("userID",studentData.id);
            i.putExtra("name",studentData.name);
            i.putExtra("teamID",classData.teamId);
            i.putExtra("committee",true);
            startActivityForResult(i,REQUEST_UPDATE_PROFILE);
        }

    }

    public ArrayList<String> getMobileList(){
        ArrayList<String> mobList = new ArrayList<>();
        if(list!=null){
            for (int i=0;i<list.size();i++){
                mobList.add(list.get(i).phone);
            }
        }
        return mobList;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_UPDATE_PROFILE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
            /*    Intent i = new Intent(getContext(),GroupDashboardActivityNew.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);*/
                CommitteeMemberTBL.deleteCommitteeMember(GroupDashboardActivityNew.groupId, classData.teamId,committeeData.getCommitteeId());
                list.clear();
                adapter.add(list);
                getDataLocally();
            }
        }
    }
}
