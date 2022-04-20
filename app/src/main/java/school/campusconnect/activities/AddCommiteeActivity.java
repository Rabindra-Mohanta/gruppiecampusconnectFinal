package school.campusconnect.activities;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.ActivityAddCommiteeBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.committee.AddCommitteeReq;
import school.campusconnect.network.LeafManager;
import school.campusconnect.views.SMBDialogUtils;

public class AddCommiteeActivity extends BaseActivity implements View.OnClickListener, LeafManager.OnCommunicationListener {

    private static final String TAG = "AddCommiteeActivity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    private String TeamID;
    private String committeeID;
    private LeafManager leafManager;
    ActivityAddCommiteeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_commitee);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        inits();
    }

    private void inits() {

        leafManager = new LeafManager();

        if(getIntent() != null)
        {
            TeamID = getIntent().getStringExtra("team_id");

            if (getIntent().getStringExtra("screen").equalsIgnoreCase("add"))
            {
                binding.btnAddCommittee.setText("Add");
                setTitle(getResources().getString(R.string.lbl_add_committee));
            }
            else if (getIntent().getStringExtra("screen").equalsIgnoreCase("update"))
            {
                setTitle(getResources().getString(R.string.lbl_update_committee));
                committeeID = getIntent().getStringExtra("committee_id");
                binding.etName.setText(getIntent().getStringExtra("committee_name"));
                binding.btnAddCommittee.setText("Update");
            }
        }

        binding.btnAddCommittee.setOnClickListener(this);

    }

    public boolean isValid()
    {
        boolean valid = true;
        if (!isValueValid(binding.etName)) {
            valid = false;
        }
        return valid;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btnAddCommittee:

                if (isValid())
                {
                    AddCommitteeReq req = new AddCommitteeReq();
                    req.setCommitteeName(binding.etName.getText().toString());

                    binding.progressBar.setVisibility(View.VISIBLE);

                    if (binding.btnAddCommittee.getText().toString().equalsIgnoreCase("Add"))
                    {
                        leafManager.addCommittee(this, GroupDashboardActivityNew.groupId,TeamID,req);
                    }
                    else if (binding.btnAddCommittee.getText().toString().equalsIgnoreCase("Update"))
                    {
                        leafManager.updateCommittee(this, GroupDashboardActivityNew.groupId,TeamID,committeeID,req);
                    }
                }
                break;
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        binding.progressBar.setVisibility(View.GONE);

        switch (apiId)
        {
            case LeafManager.ADD_COMMITTEE:
                LeafPreference.getInstance(AddCommiteeActivity.this).setBoolean(LeafPreference.ADD_COMMITTEE, true);
                finish();
                break;

            case LeafManager.UPDATE_COMMITTEE:

                if (getIntent().getStringExtra("screen").equalsIgnoreCase("update"))
                {
                    Intent i = getIntent();
                    i.putExtra("Delete","No");
                    i.putExtra("Title",binding.etName.getText().toString());
                    setResult(RESULT_OK,i);
                    finish();
                }
                break;

            case LeafManager.REMOVE_COMMITTEE:

                if (getIntent().getStringExtra("screen").equalsIgnoreCase("update"))
                {
                    Intent i = getIntent();
                    i.putExtra("Delete","Yes");
                    setResult(RESULT_OK,i);
                    finish();
                }
                break;

        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_committee_delete,menu);

        if (getIntent().getStringExtra("screen").equalsIgnoreCase("add"))
        {
            menu.findItem(R.id.menu_delete_committee).setVisible(false);
        }
        else if (getIntent().getStringExtra("screen").equalsIgnoreCase("update"))
        {
            menu.findItem(R.id.menu_delete_committee).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.menu_delete_committee:
                SMBDialogUtils.showSMBDialogOKCancel(this, getResources().getString(R.string.smb_Delete_commitee), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isConnectionAvailable()) {
                            binding.progressBar.setVisibility(View.VISIBLE);
                            LeafManager manager = new LeafManager();
                            manager.removeCommittee(AddCommiteeActivity.this, GroupDashboardActivityNew.groupId,TeamID,committeeID);
                        } else {
                            showNoNetworkMsg();
                        }
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}