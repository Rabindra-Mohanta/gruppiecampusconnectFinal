package school.campusconnect.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.AddTeamResponse;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.CreateTeamRequest;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.GroupValidationError;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.UploadImageFragment;
import school.campusconnect.views.SMBDialogUtils;

public class CreateTeamActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<GroupValidationError> {

    private static final String TAG = "CreateTeamActivity";
    @Bind(R.id.etTeamName)
    EditText etTeamName;

    @Bind(R.id.btnCreateTeam)
    Button btnCreateTeam;



    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private UploadImageFragment imageFragment;

    LeafManager leafManager;

    boolean isEdit;
    private MyTeamData myTeamData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        init();

        setImageFragment();
    }

    private void setImageFragment() {
        if (isEdit) {
            imageFragment = UploadImageFragment.newInstance(myTeamData.image, true, true);
        } else
        {
            imageFragment = UploadImageFragment.newInstance(null, true, true);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, imageFragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(isEdit)
        {
            getMenuInflater().inflate(R.menu.menu_edit,menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuDelete) {
            if (!isConnectionAvailable()) {
                showNoNetworkMsg();
                return true;
            }
            if (myTeamData == null)
                return true;

            SMBDialogUtils.showSMBDialogOKCancel(this, "Are you sure you want to permanently delete this team.?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressBar.setVisibility(View.VISIBLE);
                    leafManager.deleteTeam(CreateTeamActivity.this, GroupDashboardActivityNew.groupId, myTeamData.teamId);
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_create_team));
        leafManager = new LeafManager();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
            isEdit=bundle.getBoolean("is_edit");

            if(isEdit)
            {
               myTeamData=new Gson().fromJson(bundle.getString("team_data"), MyTeamData.class);
               etTeamName.setText(myTeamData.name);
               btnCreateTeam.setText(getResources().getString(R.string.lbl_update));
            }
        }
    }

    @OnClick({R.id.btnCreateTeam})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCreateTeam:
                if (isValid()) {

                    if(!isConnectionAvailable())
                    {
                        showNoNetworkMsg();
                        return;
                    }

                    if(isEdit)
                    {
                        CreateTeamRequest request = new CreateTeamRequest();
                        request.name = etTeamName.getText().toString();
                        if (imageFragment.isImageChanged && TextUtils.isEmpty(imageFragment.getmProfileImage())) {
                            request.image=null;
                        }
                        else
                        {
                            request.image=imageFragment.getmProfileImage();
                        }
                        AppLog.e(TAG,"request :"+request);
                        progressBar.setVisibility(View.VISIBLE);
                        leafManager.editTeam(this, GroupDashboardActivityNew.groupId,myTeamData.teamId, request);
                    }
                    else
                    {
                        CreateTeamRequest request = new CreateTeamRequest();
                        request.name = etTeamName.getText().toString();
                        request.image = imageFragment.getmProfileImage();
                        progressBar.setVisibility(View.VISIBLE);
                        AppLog.e(TAG,"request :"+request);
                        leafManager.addTeam(this, GroupDashboardActivityNew.groupId, request);
                    }

                }
                break;
        }
    }

    public boolean isValid() {
        boolean valid = true;
        if (!isValueValid(etTeamName)) {
            valid = false;
        }
        return valid;
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_ID_CREATE_TEAM:

                final AddTeamResponse addTeamResponse = (AddTeamResponse) response;

                Toast.makeText(this, getString(R.string.msg_creted_team), Toast.LENGTH_LONG).show();

                if (LeafPreference.getInstance(this).getInt(LeafPreference.GROUP_COUNT) == 1 && "constituency".equalsIgnoreCase(BuildConfig.AppCategory)) {
                    finish();
                }else {
                    final AlertDialog.Builder adb = new AlertDialog.Builder(this);
                    CharSequence items[] = new CharSequence[] {"Add Staff", "Add Students"};
                    adb.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface d, int n) {
                            AppLog.e(TAG,"ss : "+n);
                            d.dismiss();
                            if(n==0){
                                Intent intent = new Intent(CreateTeamActivity.this, AddTeamStaffActivity.class);
                                intent.putExtra("id", GroupDashboardActivityNew.groupId);
                                intent.putExtra("invite", true);
                                intent.putExtra("from_team", true);
                                intent.putExtra("team_id", addTeamResponse.data.teamId);
                                startActivity(intent);
                            }else {
                                Intent intent = new Intent(CreateTeamActivity.this, AddTeamStudentActivity.class);
                                intent.putExtra("id", GroupDashboardActivityNew.groupId);
                                intent.putExtra("invite", true);
                                intent.putExtra("from_team", true);
                                intent.putExtra("team_id", addTeamResponse.data.teamId);
                                startActivity(intent);
                            }
                            finish();
                        }

                    });
                    adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    AlertDialog dialog = adb.create();
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.show();
                }
                break;
            case LeafManager.API_ID_EDIT_TEAM:
                LeafPreference.getInstance(this).setBoolean(LeafPreference.ISTEAMUPDATED, true);
                finish();
                break;
            case LeafManager.API_ID_DELETE_TEAM:
                LeafPreference.getInstance(this).setBoolean(LeafPreference.ISTEAMUPDATED, true);
                finish();
                break;
        }
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<GroupValidationError> error) {
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

        if (error.status.equals("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            if(apiId==LeafManager.API_ID_DELETE_TEAM)
            {
                GroupValidationError groupValidationError= (GroupValidationError) error;
                Toast.makeText(this, groupValidationError.message, Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, error.title, Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Override
    public void onException(int apiId, String msg) {
        super.onException(apiId, msg);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }
}
