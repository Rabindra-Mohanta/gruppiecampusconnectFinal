package school.campusconnect.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import butterknife.OnClick;
import school.campusconnect.databinding.ActivityVoterProfileBinding;
import school.campusconnect.databinding.ItemOtherLeadBinding;
import school.campusconnect.datamodel.booths.VoterProfileResponse;
import school.campusconnect.fragments.LeadListFragment;
import school.campusconnect.utils.AppLog;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Random;

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
import school.campusconnect.utils.UploadCircleImageFragment;
import school.campusconnect.views.SMBDialogUtils;

public class LeadDetailActivity extends BaseActivity implements LeafManager.OnAddUpdateListener, DialogInterface.OnClickListener, LeafManager.OnCommunicationListener {

    public static final String TAG = "LeadDetailActivity";
    public static final String EXTRA_LEAD_ITEM = "extra_lead_item";
    private LeadItem mLeadItem;

  /*  @Bind(R.id.txt_name)
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
    ProgressBar progressBar;*/
    String mGroupId;
    String mFriendId;
    SharedPreferences prefs;
    LeafManager manager = new LeafManager();
    boolean isPost, isAdmin;
    String imageUrl;
    String type;
    private String teamId;
    private boolean allowedToAddUser;
    private boolean allowedToAddTeamPost;
    private boolean allowedToAddTeamPostComment;
    UploadCircleImageFragment imageFragment;
    String voterId,caste,sub_caste,religion,blood_group;
    ItemOtherLeadBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.item_other_lead);
       // setContentView(R.layout.item_other_lead);
      //  ButterKnife.bind(this);
        mLeadItem = getIntent().getParcelableExtra(EXTRA_LEAD_ITEM);
        AppLog.e(TAG,"mLeadItem Data" +new Gson().toJson(mLeadItem));
        Toolbar mToolBar= binding.getRoot().findViewById(R.id.toolbar);


        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.title_details));
        ActiveAndroid.initialize(this);

        binding.llPhone.setVisibility(View.GONE);

        isPost = getIntent().getBooleanExtra("post", false);
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        allowedToAddUser = getIntent().getBooleanExtra("allowedToAddUser", false);
        allowedToAddTeamPost = getIntent().getBooleanExtra("allowedToAddTeamPost", false);
        allowedToAddTeamPostComment = getIntent().getBooleanExtra("allowedToAddTeamPostComment", false);
        type = getIntent().getExtras().getString("type");
        teamId = getIntent().getStringExtra("team_id");

        voterId = getIntent().getStringExtra("voterId");
        caste = getIntent().getStringExtra("caste");
        sub_caste = getIntent().getStringExtra("subCaste");
        religion = getIntent().getStringExtra("religion");
        blood_group = getIntent().getStringExtra("blood");


        AppLog.e(TAG,"voterId  " +voterId + "\ncaste  " +caste + "\nsub_caste  " +sub_caste+ "\nreligion  " +religion+ "\nblood_group  " +blood_group);


        binding.txtName.setText(mLeadItem.getName());
        binding.txtPhone.setText(mLeadItem.getPhone());

        binding.txtEmail.setText(mLeadItem.getEmail());

        binding.txtDob.setText(mLeadItem.getDob());


        if (!TextUtils.isEmpty(mLeadItem.getImage()))
        {
            AppLog.e("LeadAdapter", "Item Not Empty +"+mLeadItem.getName()+" , "+mLeadItem.getImage());

            Picasso.with(getApplicationContext()).load(Constants.decodeUrlToBase64(mLeadItem.getImage())).resize(dpToPx(), dpToPx()).
                    into(binding.imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                            AppLog.e("LeadAdapter", "Item Not Empty , On Success ");
                        }

                        @Override
                        public void onError() {
                            AppLog.e("LeadAdapter", "Item Not Empty , On Error");

                              binding.imageView.setVisibility(View.GONE);
                              binding.imageByName.setVisibility(View.VISIBLE);
                            TextDrawable drawable = TextDrawable.builder()
                                    .buildRound(ImageUtil.getTextLetter(mLeadItem.getName()), ImageUtil.getRandomColor(0)) ;
                            binding.imageByName.setImageDrawable(drawable);
                        }
                    });
        } else {
            AppLog.e("LeadAdapter", "Item Empty ");
            binding.imageView.setVisibility(View.GONE);
            binding.imageByName.setVisibility(View.VISIBLE);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(ImageUtil.getTextLetter(mLeadItem.getName()), ImageUtil.getRandomColor(0 ));
            binding.imageByName.setImageDrawable(drawable);
        }

        binding.txtGender.setText(mLeadItem.gender);
        binding.txtLeadCount.setText(String.valueOf(mLeadItem.getLeadCount()));

        binding.txtOcupation.setText(mLeadItem.getOccupation());


        mGroupId = GroupDashboardActivityNew.groupId;
        if (isAdmin) {
            binding.switchAllowPost.setVisibility(View.VISIBLE);
          //  binding.switchAllowAddUser.setVisibility(View.VISIBLE);
            binding.switchAllowComment.setVisibility(View.VISIBLE);
        } else {
            binding.switchAllowPost.setVisibility(View.GONE);
           // binding.switchAllowAddUser.setVisibility(View.GONE);
            binding.switchAllowComment.setVisibility(View.GONE);
        }

        if (allowedToAddTeamPost)
            binding.switchAllowPost.setChecked(true);

        if (allowedToAddUser)
            binding.switchAllowAddUser.setChecked(true);

        if (allowedToAddTeamPostComment)
            binding.switchAllowComment.setChecked(true);

        if (type.equalsIgnoreCase("nestedfriend")) {
            binding.txtPhone.setVisibility(View.GONE);
           // binding..setVisibility(View.GONE);
        }

        binding.etVoterId.setText(voterId);
        binding.etReligion.setText(religion);
        binding.etCaste.setText(caste);
        binding.etSubCaste.setText(sub_caste);
        binding.etBlood.setText(blood_group);

        fillDetails(mLeadItem);
    }

    private int dpToPx() {
        return getResources().getDimensionPixelSize(R.dimen.profile_image_size);
    }


    private void fillDetails(LeadItem data) {


        binding.etGender.setEnabled(false);
      //  binding.etGender.setTextColor(getResources().getColor(R.color.grey));

        binding.etBlood.setEnabled(false);
       // binding.etBlood.setTextColor(getResources().getColor(R.color.grey));

        binding.etName.setEnabled(false);
       // binding.etName.setTextColor(getResources().getColor(R.color.grey));

        binding.etRole.setEnabled(false);
     //   binding.etRole.setTextColor(getResources().getColor(R.color.grey));

     //   binding.etPhone.setEnabled(false);
     //   binding.etPhone.setTextColor(getResources().getColor(R.color.grey));



        binding.etEmail.setEnabled(false);
       // binding.etEmail.setTextColor(getResources().getColor(R.color.grey));

      //  binding.etdob.setEnabled(false);
     //   binding.etdob.setTextColor(getResources().getColor(R.color.grey));

        binding.etEducation.setEnabled(false);
      //  binding.etEducation.setTextColor(getResources().getColor(R.color.grey));

        binding.etProfession.setEnabled(false);
      //  binding.etProfession.setTextColor(getResources().getColor(R.color.grey));

        binding.etdob.setEnabled(false);
        binding.etReligion.setEnabled(false);
        binding.etCaste.setEnabled(false);
        binding.etSubCaste.setEnabled(false);

        binding.etCaste.setEnabled(false);
      //  binding.etCaste.setTextColor(getResources().getColor(R.color.grey));

        binding.etSubCaste.setEnabled(false);
      //  binding.etSubCaste.setTextColor(getResources().getColor(R.color.grey));

        binding.etReligion.setEnabled(false);
      //  binding.etReligion.setTextColor(getResources().getColor(R.color.grey));


        binding.etName.setText(data.name);
        binding.etPhone.setText(data.phone);


        binding.etEmail.setText(data.email);
    //    binding.etdob.setText(data.dob);
        binding.etEducation.setText(data.qualification);
        binding.etProfession.setText(data.occupation);

        binding.etdob.setText(data.dob);







        binding.etGender.setText(data.gender);



        binding.etAadhar.setText(data.aadharNumber);

     //   binding.etReligion.setText(data.religion);


       // manager.getReligion(this);

        binding.switchAllowPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingBar(binding.progressBar);
                //  binding.progressBar.setVisibility(View.VISIBLE);
                manager.allowTeamPost(LeadDetailActivity.this, mGroupId, teamId, mLeadItem.getId());
            }
        });

        binding.switchAllowAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingBar(binding.progressBar);
                //  binding.progressBar.setVisibility(View.VISIBLE);
                manager.allowAddOtherMember(LeadDetailActivity.this, mGroupId, teamId, mLeadItem.getId());
            }
        });

        binding.switchAllowComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingBar(binding.progressBar);
                //  binding.progressBar.setVisibility(View.VISIBLE);
                manager.allowTeamPostComment(LeadDetailActivity.this, mGroupId, teamId, mLeadItem.getId());
            }
        });

    }

  /*  @OnClick({R.id.switchAllowAddUser, R.id.switchAllowPost, R.id.switchAllowComment})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.switchAllowPost:
                if (binding.progressBar != null)

                break;
            case R.id.switchAllowAddUser:
                if (binding.progressBar != null)
                    showLoadingBar(binding.progressBar);
                   // binding.progressBar.setVisibility(View.VISIBLE);
                    manager.allowAddOtherMember(LeadDetailActivity.this, mGroupId, teamId, mLeadItem.getId());
                break;
            case R.id.switchAllowComment:
                if (binding.progressBar != null)
                    showLoadingBar(binding.progressBar);
                   // binding.progressBar.setVisibility(View.VISIBLE);
                    manager.allowTeamPostComment(LeadDetailActivity.this, mGroupId, teamId, mLeadItem.getId());
                break;

        }
    }*/

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
            SMBDialogUtils.showSMBDialogOKCancel(this, getResources().getString(R.string.smb_delete)+mLeadItem.getName()+getResources().getString(R.string.smb_from_team), this);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (isConnectionAvailable()) {
            mFriendId = mLeadItem.getId();
            //  showLoadingDialog();
            if (binding.progressBar != null)
                showLoadingBar(binding.progressBar);
               // binding.progressBar.setVisibility(View.VISIBLE);
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
        if (binding.progressBar != null)
            hideLoadingBar();
           // binding.progressBar.setVisibility(View.GONE);
        AppLog.e("UserDetail", "onSuccessCalled");

        switch (apiId) {
            case LeafManager.API_DELETE_MY_FRIEND:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_user_deleted), Toast.LENGTH_SHORT).show();
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
                if (binding.switchAllowAddUser.isChecked()) {
                    binding.switchAllowAddUser.setChecked(false);
                } else {
                    binding.switchAllowAddUser.setChecked(true);
                }
                break;
            case LeafManager.API_ALLOW_ADD_TEAM_POST:
                if (binding.switchAllowPost.isChecked()) {
                    binding.switchAllowPost.setChecked(false);
                } else {
                    binding.switchAllowPost.setChecked(true);
                }
                break;
            case LeafManager.API_ALLOW_ADD_TEAM_POST_COMMENT:
                if (binding.switchAllowComment.isChecked()) {
                    binding.switchAllowComment.setChecked(false);
                } else {
                    binding.switchAllowComment.setChecked(true);
                }
                break;


        }

    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel error) {
        //hideLoadingDialog();
        if (binding.progressBar != null)
            hideLoadingBar();
           // binding.progressBar.setVisibility(View.GONE);
        AppLog.e("LeadDetail", "onFailurecalled");
        if (error.status.equals("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(this, error.title, Toast.LENGTH_SHORT).show();
        }

        switch (apiId) {
            case LeafManager.API_ALLOW_OTHER_TO_ADD:
                if (binding.switchAllowAddUser.isChecked()) {
                    binding.switchAllowAddUser.setChecked(false);
                } else {
                    binding.switchAllowAddUser.setChecked(true);
                }
                break;
            case LeafManager.API_ALLOW_ADD_TEAM_POST:
                if (binding.switchAllowPost.isChecked()) {
                    binding.switchAllowPost.setChecked(false);
                } else {
                    binding.switchAllowPost.setChecked(true);
                }
                break;
            case LeafManager.API_ALLOW_ADD_TEAM_POST_COMMENT:
                if (binding.switchAllowComment.isChecked()) {
                    binding.switchAllowComment.setChecked(false);
                } else {
                    binding.switchAllowComment.setChecked(true);
                }
                break;
        }

    }

    @Override
    public void onException(int apiId, String error) {
        //hideLoadingDialog();
        if (binding.progressBar != null)
            hideLoadingBar();
           // binding.progressBar.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();

        switch (apiId) {
            case LeafManager.API_ALLOW_OTHER_TO_ADD:
                if (binding.switchAllowAddUser.isChecked()) {
                    binding.switchAllowAddUser.setChecked(false);
                } else {
                    binding.switchAllowAddUser.setChecked(true);
                }
                break;
            case LeafManager.API_ALLOW_ADD_TEAM_POST:
                if (binding.switchAllowPost.isChecked()) {
                    binding. switchAllowPost.setChecked(false);
                } else {
                    binding.switchAllowPost.setChecked(true);
                }
                break;
            case LeafManager.API_ALLOW_ADD_TEAM_POST_COMMENT:
                if (binding.switchAllowComment.isChecked()) {
                    binding.switchAllowComment.setChecked(false);
                } else {
                    binding.switchAllowComment.setChecked(true);
                }
                break;
        }

    }

}
