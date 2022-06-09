package school.campusconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.adapters.TicketsAdapter;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.ActivitySelectRoleBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.GroupDetailResponse;
import school.campusconnect.datamodel.ticket.TicketListResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;


public class SelectRoleActivity extends BaseActivity implements LeafManager.OnCommunicationListener, View.OnClickListener {
    public static String TAG = "SelectRoleActivity";
    ActivitySelectRoleBinding binding;
    LeafManager leafManager;
    String Role = null;
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_select_role);
        inits();
    }

    private void inits() {

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_select_role));

        intent = new Intent(this, TicketsActivity.class);
        leafManager = new LeafManager();

        showLoadingBar(binding.progressBar,false);
        //binding.progressBar.setVisibility(View.VISIBLE);

        leafManager.getGroupDetail(this, GroupDashboardActivityNew.groupId);

        binding.llisAdmin.setOnClickListener(this);
        binding.llisPartyTaskForce.setOnClickListener(this);
        binding.llisDepartMentTaskForce.setOnClickListener(this);
        binding.llisBoothPresident.setOnClickListener(this);
        binding.llisBoothMember.setOnClickListener(this);

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        hideLoadingBar();
       // binding.progressBar.setVisibility(View.GONE);

        switch (apiId)
        {
            case LeafManager.API_ID_GROUP_DETAIL:

                GroupDetailResponse res1 = (GroupDetailResponse) response;

                AppLog.e(TAG, "GroupDetailResponse " + new Gson().toJson(res1));


                if (res1.data.get(0).isAdmin)
                {
                    binding.llisAdmin.setVisibility(View.VISIBLE);
                }
                if (res1.data.get(0).isPartyTaskForce)
                {
                    binding.llisPartyTaskForce.setVisibility(View.VISIBLE);

                }
                if (res1.data.get(0).isDepartmentTaskForce)
                {
                    binding.llisDepartMentTaskForce.setVisibility(View.VISIBLE);

                }
                if (res1.data.get(0).isBoothPresident)
                {
                    binding.llisBoothPresident.setVisibility(View.VISIBLE);

                }
                if (res1.data.get(0).isBoothMember)
                {
                    binding.llisBoothMember.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        // binding.progressBar.setVisibility(View.GONE);
        AppLog.e(TAG, "onFailure " + msg);
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        // binding.progressBar.setVisibility(View.GONE);
        AppLog.e(TAG, "onException " + msg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {

            case R.id.llisAdmin:
                intent.putExtra("Role", "isAdmin");
                startActivity(intent);
                break;

            case R.id.llisPartyTaskForce:
                intent.putExtra("Role", "isPartyTaskForce");
                startActivity(intent);
                break;

            case R.id.llisDepartMentTaskForce:
                intent.putExtra("Role", "isDepartmentTaskForce");
                startActivity(intent);
                break;

            case R.id.llisBoothPresident:
                intent.putExtra("Role", "isBoothPresident");
                startActivity(intent);
                break;

            case R.id.llisBoothMember:
                intent.putExtra("Role", "isBoothMember");
                startActivity(intent);
                break;
        }
    }
}