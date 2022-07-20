
package school.campusconnect.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.BuildConfig;
import school.campusconnect.LeafApplication;
import school.campusconnect.activities.AddBoothStudentActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.LeadsListActivity;
import school.campusconnect.activities.NestedTeamActivity;
import school.campusconnect.activities.UpdateMemberActivity;
import school.campusconnect.activities.VoterProfileActivity;
import school.campusconnect.activities.ZoomCallActivity;
import school.campusconnect.datamodel.attendance_report.AttendanceReportRes;
import school.campusconnect.datamodel.booths.BoothMemberResponse;
import school.campusconnect.datamodel.committee.CommitteeTBL;
import school.campusconnect.datamodel.event.TeamEventDataTBL;
import school.campusconnect.datamodel.event.TeamEventModelRes;
import school.campusconnect.datamodel.lead.LeadDataTBL;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.utils.AppLog;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.amulyakhare.textdrawable.TextDrawable;
import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import school.campusconnect.R;
import school.campusconnect.activities.AddPostActivity;
import school.campusconnect.activities.LeadDetailActivity;
import school.campusconnect.adapters.LeadAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.LayoutListWithoutRefreshBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.LeadItem;
import school.campusconnect.datamodel.LeadResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.DateTimeHelper;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.MixOperations;
import us.zoom.sdk.FreeMeetingNeedUpgradeType;
import us.zoom.sdk.InMeetingAudioController;
import us.zoom.sdk.InMeetingChatMessage;
import us.zoom.sdk.InMeetingEventHandler;
import us.zoom.sdk.InMeetingServiceListener;
import us.zoom.sdk.InstantMeetingOptions;
import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.MeetingViewsOptions;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class LeadListFragment extends BaseFragment implements LeadAdapter.OnLeadSelectListener,
        LeafManager.OnCommunicationListener {

    private static final String TAG = "LeadListFragment";
    private LayoutListWithoutRefreshBinding mBinding;
     LeadAdapter mAdapter;
    final int REQUEST_CALL = 234;
    Intent intent;
    LeadItem leadItem;
    String groupId = "";
    private int REQUEST_UPDATE_PROFILE = 9;
    String teamId = "";
    private boolean apiCall;
    LeafManager mManager = new LeafManager();
    public boolean mIsLoading = false;
    public int totalPages = 1;
    public int currentPage = 1;
    int count;
    public boolean allMember;
    private boolean itemClick;
    private boolean isAdmin;
    private LinearLayoutManager layoutManager;
    private List<LeadItem> list;

    private int teamMemberCount = 0;

    MyTeamData classData;

    /*zoom*/

    private boolean isZoomStarted = false;

    public LeadListFragment() {

    }

    public static LeadListFragment newInstance(Bundle b) {
        LeadListFragment fragment = new LeadListFragment();
        fragment.setArguments(b);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_list_without_refresh, container, false);
        mBinding.setSize(1);
        mBinding.setMessage(R.string.msg_leads);
        ActiveAndroid.initialize(getActivity());

        initObjects();

        getToLocally();

        setListener();

        return mBinding.getRoot();
    }


    private void setListener() {
        mBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!mIsLoading && totalPages > currentPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        currentPage = currentPage + 1;
                        getToLocally();
                    }
                }
            }

        });

    }

    private void initObjects() {
        DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
        count = databaseHandler.getCount();

        groupId = getArguments().getString("id");
        teamId = getArguments().getString("team_id");
        apiCall = getArguments().getBoolean("apiCall",false);

        if(getArguments().containsKey("team_count"))
            teamMemberCount = getArguments().getInt("team_count");
        else
            teamMemberCount = -1;

        classData = new Gson().fromJson(getArguments().getString("class_data"), MyTeamData.class);

        itemClick = getArguments().getBoolean("item_click", false);
        isAdmin = getArguments().getBoolean("isAdmin", false);
        allMember = getArguments().getBoolean("all",false);
        boolean isNest = getArguments().getBoolean("isNest", false);

        AppLog.e(TAG, "classData is " + new Gson().toJson(classData));

        AppLog.e(TAG, "GroupDashboardActivityNew isAdmin " + GroupDashboardActivityNew.isAdmin);
        AppLog.e(TAG, "GroupDashboardActivityNew isAdmin is " + GroupDashboardActivityNew.mGroupItem.isAdmin);

        AppLog.e(TAG, "isAdmin is " + isAdmin);
        AppLog.e(TAG, "apiCall is " + apiCall);
        AppLog.e(TAG, "item_click is " + itemClick);
        AppLog.e(TAG, "teamMemberCount is " + teamMemberCount);


        if (!LeafPreference.getInstance(getContext()).getString("leadTotalPage_"+groupId+"_"+teamId).isEmpty())
        {
            totalPages = Integer.parseInt(LeafPreference.getInstance(getContext()).getString("leadTotalPage_"+groupId+"_"+teamId));
        }

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mAdapter = new LeadAdapter(new ArrayList<LeadItem>(), this, 0, databaseHandler, count, itemClick,isNest);
        mBinding.recyclerView.setAdapter(mAdapter);

        LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISUSERDELETED, false);

        if(classData !=null && classData.adminName !=null && !classData.adminName.equalsIgnoreCase(""))
        {
            mBinding.llAdmindetail.setVisibility(View.VISIBLE);
            mBinding.tvAdminname.setText(classData.adminName);

            if(classData.phone!=null && !classData.phone.equalsIgnoreCase(""))
            mBinding.imgCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    AppLog.e(TAG ,"ONCLICK called for call : "+classData.phone);
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + classData.phone));
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    }

                }
            });
        }



      /*  mBinding.swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    currentPage = 1;
                    LeadDataTBL.deleteLead(groupId,teamId);
                    mAdapter.clear();
                    getToLocally();
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                } else {
                    showNoNetworkMsg();
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                }
            }
        });*/

     /*  CHANGES IN 2-6-22 (MEMBER COUNT PROBLEM)

     if (apiCall)
        {
            callEventApiTeam();
        }*/


        callEventApiTeam();

 //
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {

        if (BuildConfig.AppCategory.equalsIgnoreCase("constituency"))
        {
            if (GroupDashboardActivityNew.isAdmin && GroupDashboardActivityNew.isPost)
                menu.findItem(R.id.menu_print_member_list).setVisible(true);
            else
                menu.findItem(R.id.menu_print_member_list).setVisible(false);

            if (classData.isTeamAdmin && GroupDashboardActivityNew.isAdmin)
            {
                menu.findItem(R.id.menu_invite).setVisible(true);
            }
            else
            {
                menu.findItem(R.id.menu_invite).setVisible(false);
            }
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menu_invite:
                Intent intent = new Intent(getContext(), AddBoothStudentActivity.class);
                intent.putExtra("group_id",groupId);
                intent.putExtra("team_id", teamId);
                intent.putExtra("category", classData.category);
                startActivity(intent);
                break;


            case R.id.menu_print_member_list:
                exportDataToCSV();
                break;
        }
        return super.onOptionsItemSelected(item);

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
        File file = new File(csvFolder, getArguments().getString("team_name")+"_"+getResources().getString(R.string.lbl_members) + ".xls");

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet firstSheet = workbook.createSheet(getArguments().getString("team_name")+"_"+getResources().getString(R.string.lbl_members));

            HSSFRow rowA = firstSheet.createRow(0);
            rowA.createCell(0).setCellValue("Name");
            rowA.createCell(1).setCellValue("Voter Id");
            rowA.createCell(2).setCellValue("Email");
            rowA.createCell(3).setCellValue("Phone Number");
            rowA.createCell(4).setCellValue("Education");
            rowA.createCell(5).setCellValue("Profession");
            rowA.createCell(6).setCellValue("DOB");
            rowA.createCell(7).setCellValue("Gender");
            rowA.createCell(8).setCellValue("Blood Group");


            if (list != null)
            {
                for(int i=0;i<list.size();i++){

                    LeadItem item = list.get(i);
                    HSSFRow rowData = firstSheet.createRow(i + 1);
                    rowData.createCell(0).setCellValue(item.name);
                    rowData.createCell(1).setCellValue(item.voterId);
                    rowData.createCell(2).setCellValue(item.email);
                    rowData.createCell(3).setCellValue(item.phone);
                    rowData.createCell(4).setCellValue(item.qualification);
                    rowA.createCell(5).setCellValue(item.occupation);
                    rowA.createCell(6).setCellValue(item.dob);
                    rowA.createCell(7).setCellValue(item.gender);
                    rowA.createCell(8).setCellValue(item.bloodGroup);

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



    @Override
    public void onResume() {
        super.onResume();

        AppLog.e(TAG, "OnResume Called : " + LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISUSERDELETED));
        if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISUSERDELETED)) {
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISUSERDELETED, false);
            currentPage = 1;
            mAdapter.clear();
            LeadDataTBL.deleteLead(groupId,teamId);
            getToLocally();
        }

        if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ADD_FRIEND)) {
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ADD_FRIEND, false);
            currentPage = 1;
            mAdapter.clear();
            LeadDataTBL.deleteLead(groupId,teamId);
            getToLocally();
        }
    }


    public void getData(boolean call) {

        if (call)
        {
            if (isConnectionAvailable()) {
                showLoadingBar(mBinding.progressBar);
                mIsLoading = true;

                mManager.getTeamMember(this, groupId + "", teamId + "",itemClick);

            } else {
                showNoNetworkMsg();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        AppLog.e(TAG, "onRequestPermissionsResult " + requestCode);
        switch (requestCode) {
            case REQUEST_CALL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppLog.e(TAG, "gra " + requestCode);
                    if (intent != null) {
                        startActivity(intent);
                    }
                } else {
                    AppLog.e(TAG, "not gr " + requestCode);
                }
                return;
        }
    }

    @Override
    public void onCallClick(LeadItem item) {
        intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + item.getPhone()));
      /*  if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            AppLog.e(TAG, "per");
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {*/
            AppLog.e(TAG, "acti");
            startActivity(intent);
        //}
    }

    @Override
    public void onStartMeeting(LeadItem item) {
      //  startMeetingZoom(item);

        Log.e(TAG,"LeadItem "+new Gson().toJson(item));

        new SendNotification(item).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        Intent fullScreenIntent = new Intent(getContext(), ZoomCallActivity.class);
        fullScreenIntent.putExtra("className",item.name);
        fullScreenIntent.putExtra("meetingID",GroupDashboardActivityNew.mGroupItem.zoomMeetingId);
        fullScreenIntent.putExtra("zoomName","Test");
        Bundle args = new Bundle();
        args.putSerializable("data",(Serializable)item.pushTokens);
        fullScreenIntent.putExtra("BUNDLE",args);
        fullScreenIntent.putExtra("image",item.image);
        fullScreenIntent.putExtra("isMessage",false);
        fullScreenIntent.putExtra("name",item.name);
        startActivity(fullScreenIntent);
    }

    @Override
    public void onSMSClick(LeadItem item) {
        intent = new Intent(getActivity(), AddPostActivity.class);
        AppLog.e(TAG, "onSMSClick group_id " + groupId);
        intent.putExtra("id", groupId);
        intent.putExtra("friend_id", item.getId());
        intent.putExtra("friend_name", item.getName());
        intent.putExtra("type", "personal");
        intent.putExtra("team_id", teamId);
        intent.putExtra("from_chat", itemClick);
        startActivity(intent);

        if(!itemClick)
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.PERSONAL_POST_ADDED_1,true);

        getActivity().finish();
    }

    @Override
    public void onMailClick(LeadItem item) {

        if (((LeadsListActivity) getActivity()).isConnectionAvailable()) {
            Intent intent = new Intent(getActivity(), NestedTeamActivity.class);
            intent.putExtra("group_id", groupId);
            intent.putExtra("team_id", teamId);
            intent.putExtra("user_id", item.id);
            intent.putExtra("title", item.name);
            startActivity(intent);
        } else {
            ((LeadsListActivity) getActivity()).showNoNetworkMsg();
        }

    }

    @Override
    public void onNameClick(LeadItem item) {
        AppLog.e(TAG, "onNameClick()"+new Gson().toJson(item));
        if (isAdmin) {
            Intent intent = new Intent(getActivity(), LeadDetailActivity.class);
            Bundle b = new Bundle();
            b.putParcelable(LeadDetailActivity.EXTRA_LEAD_ITEM, item);
            intent.putExtras(b);

            intent.putExtra("voterId",item.voterId);
            intent.putExtra("caste",item.caste);
            intent.putExtra("subCaste",item.subCaste);
            intent.putExtra("religion",item.religion);
            intent.putExtra("blood",item.bloodGroup);



            intent.putExtra("post", item.getIsPost());
            intent.putExtra("allowedToAddUser", item.allowedToAddUser);
            intent.putExtra("allowedToAddTeamPost", item.allowedToAddTeamPost);
            intent.putExtra("allowedToAddTeamPostComment", item.allowedToAddTeamPostComment);
            intent.putExtra("type", "friend");
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("team_id", teamId);
            startActivity(intent);
        }
    }



    @Override
    public void onSuccess(int apiId, BaseResponse response) {
            hideLoadingBar();

            if (apiId == LeafManager.API_ID_LEAD_LIST)
            {
                LeadResponse res = (LeadResponse) response;


                mBinding.setSize(mAdapter.getItemCount());
                totalPages = res.totalNumberOfPages;

                LeafPreference.getInstance(getContext()).setString("leadTotalPage_"+groupId+"_"+teamId,String.valueOf(totalPages));
                mIsLoading = false;

                saveToLocally(res.getResults());
            }



    }

    private void saveToLocally(List<LeadItem> results) {

        LeadDataTBL.deleteLead(groupId,teamId,currentPage);

        if (results.size() > 0)
        {
            list = new ArrayList<>();

       //     for (int j = 0;j<740;j++)
        //    {
         //       Log.e(TAG,"j position"+j);

                for (int i = 0 ;i < results.size();i++)
                {
                    LeadDataTBL leadDataTBL = new LeadDataTBL();

                    leadDataTBL.pushToken = new Gson().toJson(results.get(i).pushTokens);
                    leadDataTBL.groupID = groupId;
                    leadDataTBL.teamID = teamId;
                    leadDataTBL.page = currentPage;
                    leadDataTBL.voterId = results.get(i).voterId;
                    leadDataTBL.id = results.get(i).id;
                    leadDataTBL.roleOnConstituency = results.get(i).roleOnConstituency;
                    leadDataTBL.phone = results.get(i).phone;
                    leadDataTBL.name = results.get(i).name;
                    leadDataTBL.image = results.get(i).image;
                    leadDataTBL.gender = results.get(i).gender;
                    leadDataTBL.dob = results.get(i).dob;
                    leadDataTBL.bloodGroup = results.get(i).bloodGroup;
                    leadDataTBL.allowedToAddUser = results.get(i).allowedToAddUser;
                    leadDataTBL.allowedToAddTeamPostComment = results.get(i).allowedToAddTeamPostComment;
                    leadDataTBL.allowedToAddTeamPost = results.get(i).allowedToAddTeamPost;
                    leadDataTBL.aadharNumber = results.get(i).aadharNumber;

                    leadDataTBL.caste = results.get(i).caste;
                    leadDataTBL.subCaste = results.get(i).subCaste;
                    leadDataTBL.religion = results.get(i).religion;

                    if (TeamEventDataTBL.getTeamEvent(teamId).size()>0)
                    {
                        List<TeamEventDataTBL> teamEvent= TeamEventDataTBL.getTeamEvent(teamId);

                        for (int j = 0;j<teamEvent.size();j++)
                        {
                            if (teamId.equalsIgnoreCase(teamEvent.get(j).teamId))
                            {
                                leadDataTBL._now = teamEvent.get(j).lastUserToTeamUpdatedAtEventAt;
                            }
                        }
                    }
                    else
                    {
                        leadDataTBL._now = DateTimeHelper.getCurrentTime();
                    }

                    leadDataTBL.isLive = false;

                    leadDataTBL.save();

                }
                list.addAll(results);
        }

        mAdapter.addItems(list);
        mAdapter.notifyDataSetChanged();
    }


    private void getToLocally() {

        List<LeadDataTBL> leadDataTBL = LeadDataTBL.getLead(groupId,teamId,currentPage);

        mAdapter.clear();

        Log.e(TAG,"leadDataTBlSize "+leadDataTBL.size() +"team Member Count "+teamMemberCount);

       // if (leadDataTBL.size() > 0 &&  ( leadDataTBL.size() == teamMemberCount  || teamMemberCount == -1))  CHANGES 2-6-22 (MEMBER COUNT PROBLEM)

        if (leadDataTBL.size() > 0)
        {
            list = new ArrayList<>();

            for (int i = 0;i<leadDataTBL.size();i++)
            {
                LeadItem leadItem = new LeadItem();
                leadItem.id = leadDataTBL.get(i).id;
                leadItem.voterId = leadDataTBL.get(i).voterId;
                leadItem.roleOnConstituency = leadDataTBL.get(i).roleOnConstituency;
                leadItem.phone = leadDataTBL.get(i).phone;
                leadItem.name = leadDataTBL.get(i).name;
                leadItem.image = leadDataTBL.get(i).image;
                leadItem.gender = leadDataTBL.get(i).gender;
                leadItem.dob = leadDataTBL.get(i).dob;
                leadItem.bloodGroup = leadDataTBL.get(i).bloodGroup;
                leadItem.allowedToAddUser = leadDataTBL.get(i).allowedToAddUser;
                leadItem.allowedToAddTeamPostComment = leadDataTBL.get(i).allowedToAddTeamPostComment;
                leadItem.allowedToAddTeamPost = leadDataTBL.get(i).allowedToAddTeamPost;
                leadItem.aadharNumber = leadDataTBL.get(i).aadharNumber;
                leadItem.pushTokens = new Gson().fromJson(leadDataTBL.get(i).pushToken,new TypeToken<ArrayList<LeadItem.pushTokenData>>() {}.getType());
                leadItem.isLive = leadDataTBL.get(i).isLive;
                leadItem.caste = leadDataTBL.get(i).caste;
                leadItem.subCaste = leadDataTBL.get(i).subCaste;
                leadItem.religion = leadDataTBL.get(i).religion;

                list.add(leadItem);
            }
            mAdapter.addItems(list);
            mBinding.setSize(mAdapter.getItemCount());
            mAdapter.notifyDataSetChanged();
        }
        else
        {
            getData(true);
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();

        if (apiId == LeafManager.API_ID_LEAD_LIST)
        {
            mIsLoading = false;
            currentPage = currentPage - 1;
            if (currentPage < 0) {
                currentPage = 1;
            }
        }

        if (msg.contains("401:Unauthorized")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();

        if (apiId == LeafManager.API_ID_LEAD_LIST)
        {
            mIsLoading = false;
            currentPage = currentPage - 1;
            if (currentPage < 0) {
                currentPage = 1;
            }
        }

        try {
            Toast.makeText(getActivity(), getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            AppLog.e("LeadListFragment", "OnExecption : " + msg);
        }
    }
    public void callEventApiTeam() {


        LeafManager leafManager = new LeafManager();
        leafManager.getTeamEvent(new LeafManager.OnCommunicationListener() {
            @Override
            public void onSuccess(int apiId, BaseResponse response) {

                AppLog.e(TAG, "onSuccess : " + response.status);
                TeamEventModelRes res = (TeamEventModelRes) response;

                new EventAsync(res).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }

            @Override
            public void onFailure(int apiId, String msg) {
                AppLog.e(TAG, "onFailure : " + msg);
            }

            @Override
            public void onException(int apiId, String msg) {
                AppLog.e(TAG, "onException : " + msg);
            }
        }, GroupDashboardActivityNew.groupId,teamId);
    }

    public void refreshData(List<LeadItem> result) {
        hideLoadingBar();
        mAdapter.clear();
        mAdapter.addItems(result);
        mAdapter.notifyDataSetChanged();
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.setSize(mAdapter.getItemCount());
        totalPages = 0;
        mIsLoading = false;
    }

    public void search(String strsearch) {

      /*  List<LeadDataTBL> leadDataTBL = LeadDataTBL.searchList(strsearch);

        Log.e(TAG,"search value "+leadDataTBL.size());

        mAdapter.clear();

        if (leadDataTBL.size() > 0)
        {
            list = new ArrayList<>();

            for (int i = 0;i<leadDataTBL.size();i++)
            {
                LeadItem leadItem = new LeadItem();
                leadItem.id = leadDataTBL.get(i).id;
                leadItem.voterId = leadDataTBL.get(i).voterId;
                leadItem.roleOnConstituency = leadDataTBL.get(i).roleOnConstituency;
                leadItem.phone = leadDataTBL.get(i).phone;
                leadItem.name = leadDataTBL.get(i).name;
                leadItem.image = leadDataTBL.get(i).image;
                leadItem.gender = leadDataTBL.get(i).gender;
                leadItem.dob = leadDataTBL.get(i).dob;
                leadItem.bloodGroup = leadDataTBL.get(i).bloodGroup;
                leadItem.allowedToAddUser = leadDataTBL.get(i).allowedToAddUser;
                leadItem.allowedToAddTeamPostComment = leadDataTBL.get(i).allowedToAddTeamPostComment;
                leadItem.allowedToAddTeamPost = leadDataTBL.get(i).allowedToAddTeamPost;
                leadItem.aadharNumber = leadDataTBL.get(i).aadharNumber;
                leadItem.pushTokens = new Gson().fromJson(leadDataTBL.get(i).pushToken,new TypeToken<ArrayList<LeadItem.pushTokenData>>() {}.getType());
                leadItem.isLive = leadDataTBL.get(i).isLive;

                list.add(leadItem);
            }
            mAdapter.addItems(list);
            mBinding.setSize(mAdapter.getItemCount());
            mAdapter.notifyDataSetChanged();
        }*/

        if(list!=null){
            if(!TextUtils.isEmpty(strsearch)){
                ArrayList<LeadItem> newList = new ArrayList<>();
                for (int i=0;i<list.size();i++){
                    if(list.get(i).name.toLowerCase().contains(strsearch.toLowerCase())){
                        newList.add(list.get(i));
                    }
                }
                mAdapter.clear();
                mAdapter.addItems(newList);
                mAdapter.notifyDataSetChanged();
            }else {
                mAdapter.clear();
                mAdapter.addItems(list);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_UPDATE_PROFILE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                Intent i = new Intent(getContext(), GroupDashboardActivityNew.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        }
    }

    class EventAsync extends AsyncTask<Void, Void, Void> {

        TeamEventModelRes res1;
        private boolean needRefresh = false;

        public EventAsync(TeamEventModelRes res1) {
            this.res1 =res1;
        }

        @Override
        protected Void doInBackground(Void... voids) {


            if (res1.getData().size() > 0)
            {
                TeamEventDataTBL.deleteTeamEvent(teamId);

                for (int i = 0;i<res1.getData().size();i++)
                {
                    TeamEventDataTBL teamEventDataTBL = new TeamEventDataTBL();
                    teamEventDataTBL.teamId = res1.getData().get(i).getTeamId();
                    if (res1.getData().get(i).getLastCommitteeForBoothUpdatedEventAt() != null)
                    {
                        teamEventDataTBL.lastCommitteeForBoothUpdatedEventAt = res1.getData().get(i).getLastCommitteeForBoothUpdatedEventAt();
                    }
                    teamEventDataTBL.lastUserToTeamUpdatedAtEventAt = res1.getData().get(i).getLastUserToTeamUpdatedAtEventAt();
                    teamEventDataTBL.members = res1.getData().get(i).getMembers();

                    teamEventDataTBL.save();
                }

            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (TeamEventDataTBL.getTeamEvent(teamId).size()>0)
            {
                List<TeamEventDataTBL> teamEvent= TeamEventDataTBL.getTeamEvent(teamId);

                List<LeadDataTBL> leadDataTBLList = LeadDataTBL.getLeadData(GroupDashboardActivityNew.groupId,teamId);

                if (leadDataTBLList.size() > 0)
                {
                    for (int i = 0;i<teamEvent.size();i++)
                    {
                        if (teamId.equalsIgnoreCase(teamEvent.get(i).teamId))
                        {
                            if (teamEvent.get(i).lastUserToTeamUpdatedAtEventAt != null && MixOperations.isNewEventUpdate(teamEvent.get(i).lastUserToTeamUpdatedAtEventAt,"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", leadDataTBLList.get(leadDataTBLList.size()-1)._now))
                            {
                                currentPage = 1;
                                Log.e(TAG,"call Event");
                                LeadDataTBL.deleteLead(GroupDashboardActivityNew.groupId,teamId);
                                list.clear();
                                mAdapter.clear();
                                getData(true);
                            }
                        }
                    }
                }

            }
        }
    }


    private class SendNotification extends AsyncTask<String, String, String> {
        private String server_response;
        LeadItem data;

        public SendNotification(LeadItem leadItem) {
            this.data = leadItem;
        }


        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;

            for (int i = 0;i<data.pushTokens.size();i++)
            {
                try {
                    url = new URL("https://fcm.googleapis.com/fcm/send");
                    urlConnection = (HttpURLConnection) url.openConnection();

                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Authorization", BuildConfig.API_KEY_FIREBASE1 + BuildConfig.API_KEY_FIREBASE2);
                    urlConnection.setRequestProperty("Content-Type", "application/json");


                    DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                    try {
                        JSONObject object = new JSONObject();

                        String topic;
                        String title = getResources().getString(R.string.app_name);
                        String name = LeafPreference.getInstance(getActivity()).getString(LeafPreference.NAME);
                        String message =  name + " has Video Calling you.";


                        List<String> listToken = new ArrayList<>();

                        /*for(int i = 0;i<data.pushTokens.size();i++)
                        {
                            if (data.pushTokens.get(i).getDeviceToken() != null && !data.pushTokens.get(i).getDeviceToken().isEmpty())
                            {
                                listToken.add(data.pushTokens.get(i).getDeviceToken());
                            }
                        }

                        String s = TextUtils.join(",", listToken);*/

                        object.put("to", data.pushTokens.get(i).getDeviceToken());

                        JSONObject notificationObj = new JSONObject();
                        notificationObj.put("title", title);
                        notificationObj.put("body", message);
                        //  object.put("notification", notificationObj);

                        JSONObject dataObj = new JSONObject();
                        dataObj.put("groupId", GroupDashboardActivityNew.groupId);
                        dataObj.put("createdById", LeafPreference.getInstance(getActivity()).getString(LeafPreference.GCM_TOKEN));
                        dataObj.put("createdByImage", LeafPreference.getInstance(getActivity()).getString(LeafPreference.PROFILE_IMAGE_NEW));
                        dataObj.put("createdByName", name);
                        dataObj.put("isVideoCall",true);
                        dataObj.put("meetingID",GroupDashboardActivityNew.mGroupItem.zoomMeetingId);
                        dataObj.put("meetingPassword",GroupDashboardActivityNew.mGroupItem.zoomMeetingPassword);
                        dataObj.put("zoomName","Test");
                        dataObj.put("className",data.name);
                        dataObj.put("iSNotificationSilent",true);
                        dataObj.put("Notification_type", "videoCallStart");
                        dataObj.put("body", message);
                        object.put("priority","high");
                        object.put("data", dataObj);
                        wr.writeBytes(object.toString());
                        Log.e(TAG, " JSON input : " + object.toString());
                        wr.flush();
                        wr.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Log.e(TAG, " Exception : " + ex.getMessage());
                    }
                    urlConnection.connect();

                    int responseCode = urlConnection.getResponseCode();
                    AppLog.e(TAG, "responseCode :" + responseCode);
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        server_response = readStream(urlConnection.getInputStream());
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    AppLog.e(TAG, "MalformedURLException :" + e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    AppLog.e(TAG, "IOException :" + e.getMessage());
                }
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



}
