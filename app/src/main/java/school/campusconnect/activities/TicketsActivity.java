package school.campusconnect.activities;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.adapters.TicketsAdapter;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.ActivityTicketsBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.GroupDetailResponse;
import school.campusconnect.datamodel.StudTestPaperItem;
import school.campusconnect.datamodel.test_exam.TestPaperRes;
import school.campusconnect.datamodel.ticket.TicketListResponse;
import school.campusconnect.datamodel.ticket.TicketTBL;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;

public class TicketsActivity extends BaseActivity implements TicketsAdapter.OnClickListener, LeafManager.OnCommunicationListener  {

    public static String TAG = "TicketsActivity";
    ActivityTicketsBinding binding;
    String Option = null;
    String[] approvalList;
    int Page = 1;
    String Role = null;
    LeafManager leafManager;
    TicketsAdapter adapter;
    private Boolean isFirstTime = true;
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_tickets);

        inits();
    }

    private void inits() {

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_tikit));

        leafManager = new LeafManager();

        adapter = new TicketsAdapter(this);
        binding.rvTickets.setAdapter(adapter);



        if (getIntent() != null)
        {
            Role = getIntent().getStringExtra("Role");
        }
        //leafManager.getGroupDetail(this, GroupDashboardActivityNew.groupId);

        //leafManager.getTickets(this,GroupDashboardActivityNew.groupId,Role,Option, String.valueOf(Page));

    }

    private void bindSp() {

        Log.e(TAG,"Role"+Role);

        if (Role.equalsIgnoreCase("isPartyTaskForce"))
        {
            approvalList = getResources().getStringArray(R.array.array_boothCoordinator);
        }
        else if (Role.equalsIgnoreCase("isDepartmentTaskForce"))
        {
            approvalList = getResources().getStringArray(R.array.array_isDepartmentTaskForce);
        }
        else if (Role.equalsIgnoreCase("isAdmin"))
        {
            approvalList = getResources().getStringArray(R.array.array_isAdmin);
        }
        else
        {
            approvalList = getResources().getStringArray(R.array.array_other);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_spinner_white,approvalList);
        binding.spApproval.setAdapter(adapter);

        binding.spApproval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AppLog.e(TAG, "onItemSelected : " + position);

                Option = approvalList[position];

                if (Option.equalsIgnoreCase("Not Approved"))
                {
                    Option = "notApproved";
                }
                else if (Option.equalsIgnoreCase("Approved"))
                {
                    Option = "approved";
                }
                else if (Option.equalsIgnoreCase("Hold"))
                {
                    Option = "hold";
                }
                else if (Option.equalsIgnoreCase("Open"))
                {
                    Option = "open";
                }
                else if (Option.equalsIgnoreCase("Close"))
                {
                    Option = "close";
                }
                else if (Option.equalsIgnoreCase("Deny"))
                {
                    Option = "deny";
                }

                getTicketListApi();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        bindSp();
    }

    @Override
    public void add(TicketListResponse.TicketData data) {

        Intent i = new Intent(getApplicationContext(),TicketDetailsActivity.class);
        i.putExtra("data",data);
        i.putExtra("Option",Option);
        i.putExtra("Role",Role);
        startActivity(i);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        binding.progressBar.setVisibility(View.GONE);

        switch (apiId)
        {
            case LeafManager.API_ID_GROUP_DETAIL:
                GroupDetailResponse res1 = (GroupDetailResponse) response;

                AppLog.e(TAG, "GroupDetailResponse " + new Gson().toJson(res1));

                Log.e(TAG,"Role"+Role);

                if (res1.data.get(0).isDepartmentTaskForce)
                {
                    Role = "isDepartmentTaskForce";
                }
                else if (res1.data.get(0).isPartyTaskForce)
                {
                    Role = "isPartyTaskForce";
                }
                else
                {
                    Role = "isAdmin";
                }
                getTicketListApi();
                break;

            case LeafManager.API_LIST_TICKET:
                isFirstTime = false;
                TicketListResponse res = (TicketListResponse) response;
                AppLog.e(TAG, "TicketListResponse " + new Gson().toJson(res));

                saveToLocal(res);

                break;
        }
    }

    private void saveToLocal(TicketListResponse res) {

        TicketTBL.deleteAll(GroupDashboardActivityNew.groupId, Role, Option,String.valueOf(Page));

        for (int i = 0; i < res.getTicketData().size(); i++) {

            TicketListResponse.TicketData currentItem =  res.getTicketData().get(i);

            TicketTBL item = new TicketTBL();

            item.groupId = GroupDashboardActivityNew.groupId;
            item.role = Role;
            item.option = Option;
            item.page = String.valueOf(Page);
            item.issueText = currentItem.getIssueText();
            item.issuePostId = currentItem.getIssuePostId();
            item.issuePartyTaskForceStatus = currentItem.getIssuePartyTaskForceStatus();
            item.issueLocation = new Gson().toJson(currentItem.getIssueLocation());
            item.issueDepartmentTaskForceStatus = currentItem.getIssueDepartmentTaskForceStatus();
            item.issueCreatedByPhone = currentItem.getIssueCreatedByPhone();
            item.issueCreatedByName = currentItem.getIssueCreatedByName();
            item.issueCreatedByImage = currentItem.getIssueCreatedByImage();
            item.issueCreatedById = currentItem.getIssueCreatedById();
            item.issueCreatedAt = currentItem.getIssueCreatedAt();
            item.fileType = currentItem.getFileType();
            item.fileName = new Gson().toJson(currentItem.getFileName());
            item.constituencyIssuePartyTaskForce = new Gson().toJson(currentItem.getConstituencyIssuePartyTaskForce());
            item.constituencyIssueJurisdiction = currentItem.getConstituencyIssueJurisdiction();
            item.constituencyIssueDepartmentTaskForce = new Gson().toJson(currentItem.getConstituencyIssueDepartmentTaskForce());
            item.constituencyIssue = currentItem.getConstituencyIssue();
            item.boothIncharge = new Gson().toJson(currentItem.getBoothIncharge());
            item.adminStatus = currentItem.getAdminStatus();
            item.save();
        }

        showLocaly();

    }

    private void showLocaly() {


        List<TicketTBL> list = TicketTBL.getAll(GroupDashboardActivityNew.groupId, Role, Option, String.valueOf(Page));

        AppLog.e(TAG,"list");

        if (list.size() != 0) {

            ArrayList<TicketListResponse.TicketData> result = new ArrayList<>();

            for (int i = 0; i < list.size(); i++) {

                TicketTBL currentItem = list.get(i);

                TicketListResponse.TicketData item = new TicketListResponse.TicketData();

                item.setIssueText(currentItem.issueText);
                item.setIssuePostId(currentItem.issuePostId);
                item.setIssuePartyTaskForceStatus(currentItem.issuePartyTaskForceStatus);
                item.setIssueLocation(new Gson().fromJson(currentItem.issueLocation, new TypeToken<TicketListResponse.IssueLocationData>() {}.getType()));
                item.setIssueDepartmentTaskForceStatus(currentItem.issueDepartmentTaskForceStatus);
                item.setIssueCreatedByPhone(currentItem.issueCreatedByPhone);
                item.setIssueCreatedByName(currentItem.issueCreatedByName);
                item.setIssueCreatedByImage(currentItem.issueCreatedByImage);
                item.setIssueCreatedById(currentItem.issueCreatedById);
                item.setIssueCreatedAt(currentItem.issueCreatedAt);
                item.setFileType(currentItem.fileType);
                item.setFileName(new Gson().fromJson(currentItem.fileName, new TypeToken<ArrayList<String>>() {}.getType()));
                item.setConstituencyIssuePartyTaskForce(new Gson().fromJson(currentItem.constituencyIssuePartyTaskForce, new TypeToken<TicketListResponse.ConstituencyIssuePartyTaskForceData>() {}.getType()));
                item.setConstituencyIssueJurisdiction(currentItem.constituencyIssueJurisdiction);
                item.setConstituencyIssueDepartmentTaskForce(new Gson().fromJson(currentItem.constituencyIssueDepartmentTaskForce, new TypeToken<TicketListResponse.ConstituencyIssueDepartmentTaskForce>() {}.getType()));
                item.setConstituencyIssue(currentItem.constituencyIssue);
                item.setBoothIncharge(new Gson().fromJson(currentItem.boothIncharge, new TypeToken<ArrayList<TicketListResponse.BoothInchargeData>>() {}.getType()));
                item.setAdminStatus(currentItem.adminStatus);

                result.add(item);
            }


            adapter.addData(result);
//

          /*  getAssignment(false);*/
//
        } else {
           // getAssignment(true);
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        binding.progressBar.setVisibility(View.GONE);
        AppLog.e(TAG, "onFailure " + msg);
    }

    @Override
    public void onException(int apiId, String msg) {
        binding.progressBar.setVisibility(View.GONE);
        AppLog.e(TAG, "onException " + msg);
    }
    private void getTicketListApi() {
        binding.progressBar.setVisibility(View.VISIBLE);
        leafManager.getTickets(this,GroupDashboardActivityNew.groupId,Role,Option, String.valueOf(Page));
    }

}