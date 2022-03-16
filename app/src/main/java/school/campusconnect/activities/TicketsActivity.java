package school.campusconnect.activities;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import school.campusconnect.datamodel.ticket.TicketEventUpdateResponse;
import school.campusconnect.datamodel.ticket.TicketListResponse;
import school.campusconnect.datamodel.ticket.TicketTBL;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.MixOperations;

public class TicketsActivity extends BaseActivity implements TicketsAdapter.OnClickListener, LeafManager.OnCommunicationListener  {

    public static String TAG = "TicketsActivity";
    ActivityTicketsBinding binding;
    String Option = null;
    String OptionSelected = null;
    public static final int REQUEST_TICKET_DETAILS_IMAGE = 101;
    String[] approvalList;

    public int totalPages = 1;
    public int currentPage = 1;
    public boolean mIsLoading = false;
    String Role = null;
    LeafManager leafManager;
    TicketsAdapter adapter;
    private Boolean isFirstTime = true;
    private Boolean isAction = false;
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    TicketTBL currentItem = null;

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

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.rvTickets.setLayoutManager(manager);
        adapter = new TicketsAdapter(this);
        binding.rvTickets.setAdapter(adapter);


        if (getIntent() != null && getIntent().getStringExtra("Role") != null && !getIntent().getStringExtra("Role").isEmpty())
        {
            Role = getIntent().getStringExtra("Role");
        }
        else
        {
            Role = "isPublic";
        }


        binding.rvTickets.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = manager.getChildCount();
                int totalItemCount = manager.getItemCount();
                int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = manager.findLastVisibleItemPosition();

                if (!mIsLoading && totalPages > currentPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                    ) {
                        currentPage = currentPage + 1;
                        showLocaly();
                    }
                }
            }
        });

        bindSp();
        //leafManager.getGroupDetail(this, GroupDashboardActivityNew.groupId);

        //leafManager.getTickets(this,GroupDashboardActivityNew.groupId,Role,Option, String.valueOf(Page));
    }
    private void bindSp() {

        Log.e(TAG,"Role"+Role);

        if (Role.equalsIgnoreCase("isPartyTaskForce"))
        {
            approvalList = getResources().getStringArray(R.array.array_isPartyTaskForce);
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

                AppLog.e(TAG, "Option Spinner: " + Option);

                OptionSelected = approvalList[position];

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
                else if (Option.equalsIgnoreCase("Denied"))
                {
                    Option = "denied";
                }
                else if (Option.equalsIgnoreCase("Over Due"))
                {
                    Option = "overDue";
                }

                getUpdateTicketListApi();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void add(TicketListResponse.TicketData data) {

        Intent i = new Intent(getApplicationContext(),TicketDetailsActivity.class);
        i.putExtra("data",data);
        i.putExtra("Option",Option);
        i.putExtra("SelectedOption",OptionSelected);
        i.putExtra("Role",Role);
        startActivityForResult(i,REQUEST_TICKET_DETAILS_IMAGE);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        binding.progressBar.setVisibility(View.GONE);

        switch (apiId)
        {
            case LeafManager.API_ID_GROUP_DETAIL:
                GroupDetailResponse res1 = (GroupDetailResponse) response;
                AppLog.e(TAG, "GroupDetailResponse " + new Gson().toJson(res1));
                break;

            case LeafManager.API_LIST_TICKET:
                isFirstTime = false;
                TicketListResponse res = (TicketListResponse) response;
                AppLog.e(TAG, "TicketListResponse " + new Gson().toJson(res));
                totalPages = res.getTotalNumberOfPages();
                mIsLoading = false;

                saveToLocal(res);

                break;

            case LeafManager.UPDATED_TICKET_LIST_EVENT:

                TicketEventUpdateResponse eventUpdateResponse = (TicketEventUpdateResponse) response;
                AppLog.e(TAG, "TicketEventUpdateResponse " + new Gson().toJson(eventUpdateResponse));

                compareLocally(eventUpdateResponse);

        }
    }

    private void compareLocally(TicketEventUpdateResponse eventUpdateResponse) {

        boolean apiCall = false;

        if (currentItem == null)
        {
            Log.e(TAG,"_now");
            apiCall = true;
        }

        if (currentItem != null)
        {
            if(MixOperations.isNewEvent(eventUpdateResponse.data.get(0).eventAt,"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",currentItem._now)){
                Log.e(TAG,"isNewEvent");
                apiCall = true;
            }
        }

        showLocaly();

        if(apiCall){
            Log.e(TAG,"call This");
            getTicketListApi();
        }
        else
        {
            Log.e(TAG,"false This");
        }

    }


    private void saveToLocal(TicketListResponse res) {

        TicketTBL.deleteAll(GroupDashboardActivityNew.groupId, Role, Option,String.valueOf(currentPage));

        Log.e(TAG,"saveToLocal "+res.getTicketData().size());
        for (int i = 0; i < res.getTicketData().size(); i++) {

            TicketListResponse.TicketData currentItemData =  res.getTicketData().get(i);

            currentItem = new TicketTBL();

            currentItem.groupId = GroupDashboardActivityNew.groupId;
            currentItem.role = Role;
            currentItem.option = Option;
            currentItem.page = String.valueOf(currentPage);
            currentItem.TotalPage = String.valueOf(totalPages);
            currentItem.issueText = currentItemData.getIssueText();
            currentItem.issuePostId = currentItemData.getIssuePostId();
            currentItem.issuePartyTaskForceStatus = currentItemData.getIssuePartyTaskForceStatus();
            currentItem.issueLocation = new Gson().toJson(currentItemData.getIssueLocation());
            currentItem.issueDepartmentTaskForceStatus = currentItemData.getIssueDepartmentTaskForceStatus();
            currentItem.issueCreatedByPhone = currentItemData.getIssueCreatedByPhone();
            currentItem.issueCreatedByName = currentItemData.getIssueCreatedByName();
            currentItem.issueCreatedByImage = currentItemData.getIssueCreatedByImage();
            currentItem.issueCreatedById = currentItemData.getIssueCreatedById();
            currentItem.issueCreatedAt = currentItemData.getIssueCreatedAt();
            currentItem.fileType = currentItemData.getFileType();
            currentItem.fileName = new Gson().toJson(currentItemData.getFileName());
            currentItem.constituencyIssuePartyTaskForce = new Gson().toJson(currentItemData.getConstituencyIssuePartyTaskForce());
            currentItem.constituencyIssueJurisdiction = currentItemData.getConstituencyIssueJurisdiction();
            currentItem.constituencyIssueDepartmentTaskForce = new Gson().toJson(currentItemData.getConstituencyIssueDepartmentTaskForce());
            currentItem.constituencyIssue = currentItemData.getConstituencyIssue();
            currentItem.boothIncharge = new Gson().toJson(currentItemData.getBoothIncharge());
            currentItem.adminStatus = currentItemData.getAdminStatus();
            currentItem._now = System.currentTimeMillis();
            currentItem.save();
        }

        if (res.getTicketData().size()>0)
        {
            binding.txtEmpty.setText("");
            showLocaly();
            Log.e(TAG,"show local list");
        }
        else
        {
            adapter.addData(res.getTicketData());
        }
    }

    public static String getCurrentTimeStamp(){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    private void showLocaly() {

        List<TicketTBL> list = TicketTBL.getAll(GroupDashboardActivityNew.groupId, Role, Option, String.valueOf(currentPage));

        Log.e(TAG,"list size in local"+list.size());

        if (list.size() != 0) {

            ArrayList<TicketListResponse.TicketData> result = new ArrayList<>();

            for (int i = 0; i < list.size(); i++) {

                currentItem = list.get(i);

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
            binding.txtEmpty.setText("");
            adapter.addData(result);
        } else {
            binding.txtEmpty.setText("No Tickets Found...");
            getTicketListApi();
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
    private void getUpdateTicketListApi() {
        binding.progressBar.setVisibility(View.VISIBLE);
        leafManager.getTicketsUpdateEvent(this,GroupDashboardActivityNew.groupId,Role,Option);
    }

    private void getTicketListApi() {
        binding.progressBar.setVisibility(View.VISIBLE);
        mIsLoading = true;
        leafManager.getTickets(this,GroupDashboardActivityNew.groupId,Role,Option, String.valueOf(currentPage));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TICKET_DETAILS_IMAGE)
        {
            if (resultCode == RESULT_OK)
            {
                Log.e(TAG,"Option Selected"+data.getStringExtra("Option"));
                binding.spApproval.setSelection(Arrays.asList(approvalList).indexOf(data.getStringExtra("Option")));
                getTicketListApi();
            }

            if (resultCode == RESULT_CANCELED)
            {
                binding.spApproval.setSelection(Arrays.asList(approvalList).indexOf(data.getStringExtra("Option")));
            }
        }
    }
}