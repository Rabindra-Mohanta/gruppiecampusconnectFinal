package school.campusconnect.activities;


import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import school.campusconnect.BuildConfig;
import school.campusconnect.datamodel.GroupItem;
import school.campusconnect.datamodel.LeadResponse;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.fragments.HomeFragment;
import school.campusconnect.utils.AppLog;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.adapters.LeadAdapter;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.LeadItem;
import school.campusconnect.fragments.LeadListFragment;
import school.campusconnect.network.LeafManager;


public class LeadsListActivity extends BaseActivity implements LeafManager.OnCommunicationListener, LeadAdapter.OnLeadSelectListener {

    private static final String TAG = "LeadsListActivity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    @Bind(R.id.edtSearch)
    EditText edtSearch;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    Intent intent;
    MyTeamData classData;
    LeadListFragment fragment;
    String groupId = "";
    String teamId = "";
    boolean apiCall = false;
    int teamMemberCount=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lead_list);
        ButterKnife.bind(this);

        initObjects();

        fragment = LeadListFragment.newInstance(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        getSupportFragmentManager().executePendingTransactions();

    }

    private void initObjects() {
        searchListener();
        setSupportActionBar(mToolBar);
        setBackEnabled(true);

        if (getIntent() != null) {
            groupId = getIntent().getExtras().getString("id");
            teamId = getIntent().getExtras().getString("team_id");
            apiCall = getIntent().getBooleanExtra("apiCall",false);
            teamMemberCount = getIntent().getExtras().getInt("team_count");
            classData = new Gson().fromJson(getIntent().getStringExtra("class_data"), MyTeamData.class);
            AppLog.e(TAG, "groupId is " + groupId);
            AppLog.e(TAG, "teamId is " + teamId);
            AppLog.e(TAG, "classData is " + classData);
            if (getIntent().hasExtra("team_name")) {
                setTitle(getIntent().getExtras().getString("team_name", "")+" "+getResources().getString(R.string.lbl_members));
            }else {
                setTitle(getResources().getString(R.string.lbl_my_people));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (BuildConfig.AppCategory.equalsIgnoreCase("constituency"))
        {
            getMenuInflater().inflate(R.menu.menu_add_staff_team,menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menu_invite:
                Intent intent = new Intent(getApplicationContext(), AddBoothStudentActivity.class);
                intent.putExtra("group_id",groupId);
                intent.putExtra("team_id", teamId);
                intent.putExtra("category", classData.category);
                startActivity(intent);

                break;
        }
        return super.onOptionsItemSelected(item);

    }*/

    public void onAddFrendSelected()
    {
        LeafPreference.getInstance(this).setBoolean(LeafPreference.ADD_FRIEND,true);
        if (isConnectionAvailable()) {
            Intent intent = new Intent(LeadsListActivity.this, AddFriendActivity.class);
            intent.putExtras(getIntent().getExtras());
            startActivity(intent);
        } else {
            showNoNetworkMsg();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void searchListener() {

        edtSearch.setCursorVisible(false);

        edtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.e(TAG, "edtSearch onClick ");
                edtSearch.setCursorVisible(true);
            }
        });


        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                fragment.search(s.toString());
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fragment.search("");
    }

    private void getSearchData(String search) {
        if(isConnectionAvailable())
        {
            if(progressBar!=null)
                showLoadingBar(progressBar);
          //  progressBar.setVisibility(View.VISIBLE);
            LeafManager manager = new LeafManager();
            manager.getLeadsListBySearch_new(this, groupId+"", teamId+"", search);
        }
        else {
            showNoNetworkMsg();
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
       AppLog.e(TAG, "Success");
       AppLog.e(TAG,"response lead list:"+ response.toString());
        if(progressBar!=null)
            hideLoadingBar();
        //progressBar.setVisibility(View.GONE);

      /*  switch (apiId) {
            case LeafManager.API_ID_LEAD_LIST_SEARCH:
                LeadResponse res = (LeadResponse) response;
                break;
        }*/
    }

    @Override
    public void onFailure(int apiId, String msg) {
       AppLog.e(TAG, "Failure");
        if(progressBar!=null)
            hideLoadingBar();
    //    progressBar.setVisibility(View.GONE);
        if (msg.contains("401:Unauthorized")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        }
        else {
            if(apiId==LeafManager.API_ID_LEAVE_TEAM)
            {
                if (msg.contains("404")) {
                    Toast.makeText(this, getResources().getString(R.string.toast_you_can_not_leave_team), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }

            }
            else {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onException(int apiId, String msg) {
       AppLog.e(TAG,"Exception:"+ msg);
    }

    @Override
    public void onCallClick(LeadItem item) {

    }

    @Override
    public void onStartMeeting(LeadItem item) {

    }

    @Override
    public void onSMSClick(LeadItem item) {

    }

    @Override
    public void onMailClick(LeadItem item) {

    }

    @Override
    public void onNameClick(LeadItem item) {

    }
}
