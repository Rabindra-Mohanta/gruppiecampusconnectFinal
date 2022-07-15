package school.campusconnect.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.widget.Toolbar;
import school.campusconnect.utils.AppLog;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.UserListItem;
import school.campusconnect.datamodel.gruppiecontacts.AllContactModel;
import school.campusconnect.datamodel.gruppiecontacts.GruppieContactGroupIdModel;
import school.campusconnect.datamodel.gruppiecontacts.LeaveResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.views.SMBDialogUtils;

public class UserDetailActivity extends BaseActivity implements DialogInterface.OnClickListener, LeafManager.OnCommunicationListener, LeafManager.OnAddUpdateListener<AddPostValidationError> {

    private UserListItem userListItem;

    @Bind(R.id.txt_email)
    TextView txt_email;

    @Bind(R.id.txt_ocupation)
    TextView txt_ocupation;

    @Bind(R.id.txt_designation)
    TextView txt_designation;

    @Bind(R.id.txt_name)
    TextView txtName;

    @Bind(R.id.txt_phone)
    TextView txtPhone;

    @Bind(R.id.txt_lead_count)
    TextView txtLeadCount;

    @Bind(R.id.txt_city)
    TextView txtCity;

    @Bind(R.id.txt_gender)
    TextView txtGender;

    @Bind(R.id.txt_dob)
    TextView txtDob;

    @Bind(R.id.txt_pincode)
    TextView txtPincode;

    @Bind(R.id.txt_addr_one)
    TextView txtAddress;

    @Bind(R.id.txt_state)
    TextView txtState;

    @Bind(R.id.img_lead)
    ImageView iv_user_image;

    @Bind(R.id.img_lead_default)
    ImageView iv_user_image_default;

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.switchAllowPost)
    public Switch switchAllowPost;

    @Bind(R.id.switchChangeAdmin)
    public Switch switchChangeAdmin;

    String mGroupId;
    SharedPreferences prefs;
    LeafManager manager = new LeafManager();
    boolean isPost, isAdmin, change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_detail);
        ButterKnife.bind(this);
        userListItem = getIntent().getParcelableExtra("user_details");
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.title_details));

        isPost = getIntent().getBooleanExtra("post", false);
        change = getIntent().getBooleanExtra("change", false);
       AppLog.e("Change", "change is " + change);
        txtName.setText(userListItem.getName());
        txtPhone.setText(userListItem.getPhone());

        txtDob.setText(userListItem.getDob());

        txtLeadCount.setText(String.valueOf(userListItem.getLeadCount()));
        txtGender.setText(userListItem.gender);
        txt_email.setText(userListItem.getEmail());
        txt_ocupation.setText(userListItem.getOccupation());

        if (!TextUtils.isEmpty(userListItem.getImage()))
        {
            Picasso.with(this).load(Constants.decodeUrlToBase64(userListItem.getImage())).into(iv_user_image);
        }
        else
        {
            iv_user_image_default.setVisibility(View.VISIBLE);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(ImageUtil.getTextLetter(userListItem.getName()), ImageUtil.getRandomColor(1));
            iv_user_image_default.setImageDrawable(drawable);
        }

        if(userListItem.getAddress()!=null)
        {
            if (userListItem.getAddress().line1 != null) {
                txtAddress.setText(userListItem.getAddress().line1);
                if (userListItem.getAddress().line2 != null) {
                    txtAddress.setText(txtAddress.getText() + "," + userListItem.getAddress().line2);
                }
            }
            txtCity.setText(userListItem.getAddress().city);
            txtState.setText(userListItem.getAddress().state);
            txtPincode.setText(userListItem.getAddress().pin);
        }



        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mGroupId = prefs.getString("id","");
        isAdmin = prefs.getBoolean("isAdmin", false);

        switchAllowPost.setVisibility(View.VISIBLE);
        if (isPost) {
            switchAllowPost.setChecked(true);
        }

        if (change)
            switchChangeAdmin.setVisibility(View.VISIBLE);

        switchAllowPost.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    manager.allowPost(UserDetailActivity.this, mGroupId+"", userListItem.getId());
                    LeafPreference.getInstance(UserDetailActivity.this).setBoolean(LeafPreference.ISUSERDELETED, true);
                } else {
                    manager.notAllowPost(UserDetailActivity.this, mGroupId+"", userListItem.getId()+"");
                    LeafPreference.getInstance(UserDetailActivity.this).setBoolean(LeafPreference.ISUSERDELETED, true);
                }
            }
        });

        switchChangeAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    showWarningDialog();
                } /*else {
                    manager.changeAdmin(UserDetailActivity.this, mGroupId, userListItem.getId());
                    LeafPreference.getInstance(UserDetailActivity.this).setBoolean(LeafPreference.ISGROUPUPDATED, true);
                }*/
            }
        });
    }

    public void showWarningDialog() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_layout_change_admin);
        dialog.setCancelable(false);
        TextView btn_ok = (TextView) dialog.findViewById(R.id.btn_ok);

        TextView btn_cancel = (TextView) dialog.findViewById(R.id.btn_cancel);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.changeAdmin(UserDetailActivity.this, mGroupId+"", userListItem.getId());
                LeafPreference.getInstance(UserDetailActivity.this).setBoolean(LeafPreference.ISGROUPUPDATED, true);
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchChangeAdmin.setChecked(false);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        MenuItem item = menu.findItem(R.id.nav_edit);
        item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_delete) {

            SMBDialogUtils.showSMBDialogOKCancel(this, getResources().getString(R.string.smb_delete)+userListItem.getName()+" "+getResources().getString(R.string.hint_from)+" "+GroupDashboardActivityNew.group_name+getResources().getString(R.string.smb_group), this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingDialog();
       AppLog.e("UserDetail", "onSuccessCalled");

        switch (apiId) {
            case LeafManager.API_DELETE_USER:
                LeaveResponse res = (LeaveResponse) response;
                GruppieContactGroupIdModel.deleteRow(res.data.get(0).groupId+"", res.data.get(0).userId+"");
                AllContactModel.deleteContact(res.data.get(0).groupId+"", res.data.get(0).userId+"");

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_user_deleted), Toast.LENGTH_SHORT).show();
                LeafPreference.getInstance(this).setBoolean(LeafPreference.ISUSERDELETED, true);
                onBackPressed();
                break;
            case LeafManager.API_ALLOW_POST:
                //Toast.makeText(getApplicationContext(),"Allowed to Post",Toast.LENGTH_SHORT).show();

                break;
            case LeafManager.API_NOT_ALLOW_POST:
                //Toast.makeText(getApplicationContext(),"Not allowed to Post",Toast.LENGTH_SHORT).show();

                break;
            case LeafManager.API_CHANGE_ADMIN:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_admin_changed), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingDialog();
       AppLog.e("UserDetail", "onFailureCalled");
        if (msg.contains("401:Unauthorized")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }

        switch (apiId) {
            case LeafManager.API_DELETE_USER:
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                break;

            case LeafManager.API_ALLOW_POST:
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                break;

            case LeafManager.API_NOT_ALLOW_POST:
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @Override
    public void onException(int apiId, String error) {
        hideLoadingDialog();
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if(isConnectionAvailable())
        {
            showLoadingDialog();
            manager.deleteUser(this, mGroupId+"", userListItem.getId());
        }
        else {
            showNoNetworkMsg();
        }

    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<AddPostValidationError> error) {
        if (error.status.equals("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(this, error.title, Toast.LENGTH_SHORT).show();
        }
    }
}
