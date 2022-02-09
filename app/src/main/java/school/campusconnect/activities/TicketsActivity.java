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

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.adapters.TicketsAdapter;
import school.campusconnect.databinding.ActivityTicketsBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.GroupDetailResponse;
import school.campusconnect.datamodel.ticket.TicketListResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;

public class TicketsActivity extends BaseActivity implements TicketsAdapter.OnClickListener, LeafManager.OnCommunicationListener  {

    public static String TAG = "TicketsActivity";
    ActivityTicketsBinding binding;
    String Option = "notApproved";
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


        bindSp();

        adapter = new TicketsAdapter(this);
        binding.rvTickets.setAdapter(adapter);

        binding.progressBar.setVisibility(View.VISIBLE);
        Log.e(TAG,"Group ID"+GroupDashboardActivityNew.groupId);
        Log.e(TAG,"Role"+Role);
        leafManager.getGroupDetail(this, GroupDashboardActivityNew.groupId);
        //leafManager.getTickets(this,GroupDashboardActivityNew.groupId,Role,Option, String.valueOf(Page));
    }

    private void bindSp() {

        approvalList = getResources().getStringArray(R.array.array_approval);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_spinner_white,approvalList);
        binding.spApproval.setAdapter(adapter);


        binding.spApproval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AppLog.e(TAG, "onItemSelected : " + position);
               // Option = approvalList[position];

                if (!isFirstTime)
                {
                    getTicketListApi();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void add(TicketListResponse.TicketData data) {

        Intent i = new Intent(getApplicationContext(),TicketDetailsActivity.class);
        i.putExtra("data",data);
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
                Role = res1.data.get(0).type;
                Log.e(TAG,"Role"+Role);
                binding.progressBar.setVisibility(View.VISIBLE);
                getTicketListApi();
                   break;

            case LeafManager.API_LIST_TICKET:
                isFirstTime = false;
                TicketListResponse res = (TicketListResponse) response;
                AppLog.e(TAG, "TicketListResponse " + new Gson().toJson(res));
                adapter.addData(res.getTicketData());
                break;
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
        leafManager.getTickets(this,GroupDashboardActivityNew.groupId,"isBoothCoordinator",Option, String.valueOf(Page));
    }

}