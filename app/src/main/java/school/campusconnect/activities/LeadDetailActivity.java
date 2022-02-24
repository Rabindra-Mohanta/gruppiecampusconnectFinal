package school.campusconnect.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import butterknife.OnClick;
import school.campusconnect.utils.AppLog;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.amulyakhare.textdrawable.TextDrawable;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.LeadItem;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.views.SMBDialogUtils;

public class LeadDetailActivity extends BaseActivity implements LeafManager.OnAddUpdateListener, DialogInterface.OnClickListener, LeafManager.OnCommunicationListener {

    public static final String EXTRA_LEAD_ITEM = "extra_lead_item";
    private LeadItem mLeadItem;
    @Bind(R.id.txt_name)
    TextView txtName;
    @Bind(R.id.txt_phone)
    TextView txtPhone;
    @Bind(R.id.txt_email)
    TextView txtEmail;
    @Bind(R.id.img_lead)
    ImageView imgLead;

    @Bind(R.id.img_lead_default)
    ImageView imgLead_Default;

    @Bind(R.id.txt_lead_count)
    TextView txtLeadCount;
    @Bind(R.id.txt_roll)
    TextView txtRoll;
    @Bind(R.id.txt_dob)
    TextView txtDob;
    @Bind(R.id.txt_gender)
    TextView txtGender;
    @Bind(R.id.txt_addr_one)
    TextView txtAddr;
    @Bind(R.id.txt_city)
    TextView txtCity;
    @Bind(R.id.txt_state)
    TextView txtState;
    @Bind(R.id.txt_pincode)
    TextView txtPincode;
    @Bind(R.id.txt_lead_id)
    TextView txtLeadId;
    @Bind(R.id.txt_ocupation)
    TextView txtOcupation;
    @Bind(R.id.label_friend_count)
    TextView lblFriendCount;
    @Bind(R.id.label_otherfriend)
    TextView lblOtherFriend;
    @Bind(R.id.label_phone)
    TextView lblPhone;

    @Bind(R.id.llEmail)
    LinearLayout llEmail;

    @Bind(R.id.llGender)
    LinearLayout llGender;

    @Bind(R.id.llDOB)
    LinearLayout llDOB;

    @Bind(R.id.llAddress)
    LinearLayout llAddress;

    @Bind(R.id.llCity)
    LinearLayout llCity;

    @Bind(R.id.llState)
    LinearLayout llState;

    @Bind(R.id.llPinCode)
    LinearLayout llPinCode;

    @Bind(R.id.llOtherFriend)
    LinearLayout llOtherFriend;

    @Bind(R.id.llDesignation)
    LinearLayout llDesignation;

    @Bind(R.id.llOcupation)
    LinearLayout llOcupation;


    @Bind(R.id.switchAllowPost)
    public Switch switchAllowPost;

    @Bind(R.id.switchAllowAddUser)
    public Switch switchAllowAddUser;

    @Bind(R.id.switchAllowComment)
    public Switch switchAllowComment;


    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.llPhone)
    public LinearLayout llPhone;



    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    String mGroupId;
    String mFriendId;
    SharedPreferences prefs;
    LeafManager manager = new LeafManager();
    boolean isPost, isAdmin;
    String type;
    private String teamId;
    private boolean allowedToAddUser;
    private boolean allowedToAddTeamPost;
    private boolean allowedToAddTeamPostComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_other_lead);
        ButterKnife.bind(this);
        mLeadItem = getIntent().getParcelableExtra(EXTRA_LEAD_ITEM);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle("Details");
        ActiveAndroid.initialize(this);

        llPhone.setVisibility(View.GONE);

        isPost = getIntent().getBooleanExtra("post", false);
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        allowedToAddUser = getIntent().getBooleanExtra("allowedToAddUser", false);
        allowedToAddTeamPost = getIntent().getBooleanExtra("allowedToAddTeamPost", false);
        allowedToAddTeamPostComment = getIntent().getBooleanExtra("allowedToAddTeamPostComment", false);
        type = getIntent().getExtras().getString("type");
        teamId = getIntent().getStringExtra("team_id");

        txtName.setText(mLeadItem.getName());
        txtPhone.setText(mLeadItem.getPhone());

        if (mLeadItem.getEmail() == null || mLeadItem.getEmail().isEmpty())
        {
            llEmail.setVisibility(View.GONE);
        }

        txtEmail.setText(mLeadItem.getEmail());

        if (mLeadItem.getDob() == null || mLeadItem.getDob().isEmpty())
        {
            llDOB.setVisibility(View.GONE);
        }

        txtDob.setText(mLeadItem.getDob());

        if (mLeadItem.getGender() == null || mLeadItem.getGender().isEmpty())
        {
            llGender.setVisibility(View.GONE);
        }

        txtGender.setText(mLeadItem.gender);
        txtLeadCount.setText(String.valueOf(mLeadItem.getLeadCount()));

        if (mLeadItem.getOccupation() == null || mLeadItem.getOccupation().isEmpty())
        {
            llOcupation.setVisibility(View.GONE);
        }

        txtOcupation.setText(mLeadItem.getOccupation());

        if (!TextUtils.isEmpty(mLeadItem.getImage())) {
            Picasso.with(this).load(Constants.decodeUrlToBase64(mLeadItem.getImage())).into(imgLead);
        } else {
            imgLead_Default.setVisibility(View.VISIBLE);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(ImageUtil.getTextLetter(mLeadItem.name), ImageUtil.getRandomColor(1));
            imgLead_Default.setImageDrawable(drawable);
        }


        txtOcupation.setText(mLeadItem.getOccupation());
        if (mLeadItem.getAddress() != null) {
            if (mLeadItem.getAddress().line1 != null) {
                txtAddr.setText(mLeadItem.getAddress().line1);
                if (mLeadItem.getAddress().line2 != null) {
                    txtAddr.setText(txtAddr.getText() + "," + mLeadItem.getAddress().line2);
                }
            }
            else
            {
                llAddress.setVisibility(View.GONE);
            }

            if (mLeadItem.getAddress().district == null || mLeadItem.getAddress().district.isEmpty())
            {
                llCity.setVisibility(View.GONE);
            }

            if (mLeadItem.getAddress().state == null || mLeadItem.getAddress().state.isEmpty())
            {
                llState.setVisibility(View.GONE);
            }

            if (mLeadItem.getAddress().pin == null || mLeadItem.getAddress().pin.isEmpty())
            {
                llPinCode.setVisibility(View.GONE);
            }

            txtCity.setText(mLeadItem.getAddress().district);
            txtState.setText(mLeadItem.getAddress().state);
            txtPincode.setText(mLeadItem.getAddress().pin);
        }
        else
        {
            llAddress.setVisibility(View.GONE);
            llCity.setVisibility(View.GONE);
            llState.setVisibility(View.GONE);
            llPinCode.setVisibility(View.GONE);
        }

        // txtOtherLeads.setText(TextUtils.join("\n", mLeadItem.getOtherLeads()));
        mGroupId = GroupDashboardActivityNew.groupId;
        if (isAdmin) {
            switchAllowPost.setVisibility(View.VISIBLE);
            switchAllowAddUser.setVisibility(View.VISIBLE);
            switchAllowComment.setVisibility(View.VISIBLE);
        } else {
            switchAllowPost.setVisibility(View.GONE);
            switchAllowAddUser.setVisibility(View.GONE);
            switchAllowComment.setVisibility(View.GONE);
        }

        if (allowedToAddTeamPost)
            switchAllowPost.setChecked(true);

        if (allowedToAddUser)
            switchAllowAddUser.setChecked(true);

        if (allowedToAddTeamPostComment)
            switchAllowComment.setChecked(true);

        if (type.equalsIgnoreCase("nestedfriend")) {
            txtPhone.setVisibility(View.GONE);
            lblPhone.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.switchAllowAddUser, R.id.switchAllowPost, R.id.switchAllowComment})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.switchAllowPost:
                if (progressBar != null)
                    progressBar.setVisibility(View.VISIBLE);
                manager.allowTeamPost(LeadDetailActivity.this, mGroupId, teamId, mLeadItem.getId());
                break;
            case R.id.switchAllowAddUser:
                if (progressBar != null)
                    progressBar.setVisibility(View.VISIBLE);
                manager.allowAddOtherMember(LeadDetailActivity.this, mGroupId, teamId, mLeadItem.getId());
                break;
            case R.id.switchAllowComment:
                if (progressBar != null)
                    progressBar.setVisibility(View.VISIBLE);
                manager.allowTeamPostComment(LeadDetailActivity.this, mGroupId, teamId, mLeadItem.getId());
                break;

        }
    }

    private void cleverTapAllowPost(boolean isAllow) {
        if (isConnectionAvailable()) {
            try {
                CleverTapAPI cleverTap = CleverTapAPI.getInstance(getApplicationContext());
                AppLog.e("LeadDetailActivity", "Success to found cleverTap objects=>");

                if (isAllow) {
                    HashMap<String, Object> allowPostAction = new HashMap<String, Object>();
                    allowPostAction.put("id", mGroupId);
                    allowPostAction.put("group_name", GroupDashboardActivityNew.group_name);
                    allowPostAction.put("user_id", mLeadItem.getId());
                    allowPostAction.put("user_name", mLeadItem.getName());
                    allowPostAction.put("user_number", mLeadItem.getPhone());
                    cleverTap.event.push("Allow Post", allowPostAction);
                } else {
                    HashMap<String, Object> allowPostAction = new HashMap<String, Object>();
                    allowPostAction.put("id", mGroupId);
                    allowPostAction.put("group_name", GroupDashboardActivityNew.group_name);
                    allowPostAction.put("user_id", mLeadItem.getId());
                    allowPostAction.put("user_name", mLeadItem.getName());
                    allowPostAction.put("user_number", mLeadItem.getPhone());
                    cleverTap.event.push("Remove Allow Post", allowPostAction);
                }
                AppLog.e("LeadDetailActivity", "Success ");

            } catch (CleverTapMetaDataNotFoundException e) {
                AppLog.e("LeadDetailActivity", "CleverTapMetaDataNotFoundException=>" + e.toString());
                // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
            } catch (CleverTapPermissionsNotSatisfied e) {
                AppLog.e("LeadDetailActivity", "CleverTapPermissionsNotSatisfied=>" + e.toString());
                // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        MenuItem item = menu.findItem(R.id.nav_edit);
        item.setVisible(false);
        AppLog.e("LeadDetail", "OnCreateOption : " + type);
        if (type.equalsIgnoreCase("lead") || type.equalsIgnoreCase("nestedfriend")) {
            MenuItem item2 = menu.findItem(R.id.nav_delete);
            item2.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_edit) {
        } else if (item.getItemId() == R.id.nav_delete) {
            SMBDialogUtils.showSMBDialogOKCancel(this, "Are you sure you want to delete "+mLeadItem.getName()+" from team ?", this);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (isConnectionAvailable()) {
            mFriendId = mLeadItem.getId();
            //  showLoadingDialog();
            if (progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
            LeafManager manager = new LeafManager();
            manager.removeTeamUser(this, mGroupId + "", teamId, mLeadItem.getId());

            cleverTapRemoveFriend();
        } else {
            showNoNetworkMsg();
        }

    }

    private void cleverTapRemoveFriend() {
        if (isConnectionAvailable()) {
            try {
                CleverTapAPI cleverTap = CleverTapAPI.getInstance(getApplicationContext());
                AppLog.e("LeadDetailActivity", "Success to found cleverTap objects=>");
                HashMap<String, Object> removeAction = new HashMap<String, Object>();
                removeAction.put("id", mGroupId);
                removeAction.put("group_name", GroupDashboardActivityNew.group_name);
                removeAction.put("user_id", mLeadItem.getId());
                removeAction.put("user_name", mLeadItem.getName());
                removeAction.put("user_number", mLeadItem.getPhone());
                cleverTap.event.push("Remove Friend", removeAction);

                AppLog.e("LeadDetailActivity", "Success ");

            } catch (CleverTapMetaDataNotFoundException e) {
                AppLog.e("LeadDetailActivity", "CleverTapMetaDataNotFoundException=>" + e.toString());
                // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
            } catch (CleverTapPermissionsNotSatisfied e) {
                AppLog.e("LeadDetailActivity", "CleverTapPermissionsNotSatisfied=>" + e.toString());
                // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
            }

        }

    }


    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        LeafPreference.getInstance(this).setBoolean(LeafPreference.ISUSERDELETED, true);
        // hideLoadingDialog();
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        AppLog.e("UserDetail", "onSuccessCalled");

        switch (apiId) {
            case LeafManager.API_DELETE_MY_FRIEND:
                Toast.makeText(getApplicationContext(), "User Deleted", Toast.LENGTH_SHORT).show();
                LeafPreference.getInstance(this).setBoolean(LeafPreference.ISTEAMUPDATED,true);
                onBackPressed();
                break;

            case LeafManager.API_NOT_ALLOW_POST:
//                AllContactModel.updateIsPost(false, mGroupId, mLeadItem.getId());
                //Toast.makeText(getApplicationContext(),"Not allowed to Post",Toast.LENGTH_SHORT).show();
                break;
            case LeafManager.API_ALLOW_OTHER_TO_ADD:
                break;
            case LeafManager.API_ALLOW_ADD_TEAM_POST:
                break;
            case LeafManager.API_ALLOW_ADD_TEAM_POST_COMMENT:
                break;
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        if (msg.contains("401:Unauthorized")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }

        switch (apiId) {
            case LeafManager.API_ALLOW_OTHER_TO_ADD:
                if (switchAllowAddUser.isChecked()) {
                    switchAllowAddUser.setChecked(false);
                } else {
                    switchAllowAddUser.setChecked(true);
                }
                break;
            case LeafManager.API_ALLOW_ADD_TEAM_POST:
                if (switchAllowPost.isChecked()) {
                    switchAllowPost.setChecked(false);
                } else {
                    switchAllowPost.setChecked(true);
                }
                break;
            case LeafManager.API_ALLOW_ADD_TEAM_POST_COMMENT:
                if (switchAllowComment.isChecked()) {
                    switchAllowComment.setChecked(false);
                } else {
                    switchAllowComment.setChecked(true);
                }
                break;


        }

    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel error) {
        //hideLoadingDialog();
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        AppLog.e("LeadDetail", "onFailurecalled");
        if (error.status.equals("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(this, error.title, Toast.LENGTH_SHORT).show();
        }

        switch (apiId) {
            case LeafManager.API_ALLOW_OTHER_TO_ADD:
                if (switchAllowAddUser.isChecked()) {
                    switchAllowAddUser.setChecked(false);
                } else {
                    switchAllowAddUser.setChecked(true);
                }
                break;
            case LeafManager.API_ALLOW_ADD_TEAM_POST:
                if (switchAllowPost.isChecked()) {
                    switchAllowPost.setChecked(false);
                } else {
                    switchAllowPost.setChecked(true);
                }
                break;
            case LeafManager.API_ALLOW_ADD_TEAM_POST_COMMENT:
                if (switchAllowComment.isChecked()) {
                    switchAllowComment.setChecked(false);
                } else {
                    switchAllowComment.setChecked(true);
                }
                break;
        }

    }

    @Override
    public void onException(int apiId, String error) {
        //hideLoadingDialog();
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();

        switch (apiId) {
            case LeafManager.API_ALLOW_OTHER_TO_ADD:
                if (switchAllowAddUser.isChecked()) {
                    switchAllowAddUser.setChecked(false);
                } else {
                    switchAllowAddUser.setChecked(true);
                }
                break;
            case LeafManager.API_ALLOW_ADD_TEAM_POST:
                if (switchAllowPost.isChecked()) {
                    switchAllowPost.setChecked(false);
                } else {
                    switchAllowPost.setChecked(true);
                }
                break;
            case LeafManager.API_ALLOW_ADD_TEAM_POST_COMMENT:
                if (switchAllowComment.isChecked()) {
                    switchAllowComment.setChecked(false);
                } else {
                    switchAllowComment.setChecked(true);
                }
                break;
        }

    }

}
