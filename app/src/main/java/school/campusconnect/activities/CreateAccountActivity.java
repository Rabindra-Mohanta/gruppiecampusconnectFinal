package school.campusconnect.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import school.campusconnect.BuildConfig;
import school.campusconnect.utils.AppLog;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.google.gson.Gson;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.CreateGroupReguest;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.GroupItem;
import school.campusconnect.datamodel.GroupValidationError;
import school.campusconnect.datamodel.addgroup.CreateGroupResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.UploadImageFragment;
import school.campusconnect.views.SMBDialogUtils;

public class CreateAccountActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<GroupValidationError>, LeafManager.OnCommunicationListener {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    @Bind(R.id.person_phone)
    public EditText edtAbout;
    @Bind(R.id.btn_update)
    ImageView btnupdate;

    @Bind(R.id.group_name)
    public EditText edtName;

    @Bind(R.id.tv_aboutgroup)
    public TextView aboutGroup;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    UploadImageFragment imageFragment;
    GroupItem groupItem;

    @Bind(R.id.spType)
    Spinner spType;

    @Bind(R.id.tvGroupType)
    TextView tvGroupType;

    @Bind(R.id.btn_create_acc)
    Button btn_create_acc;

    String groupType;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        String jsonGroup = getIntent().getStringExtra("group_item");
        if (jsonGroup == null) {
            setTitle(R.string.action_create_acc);
            imageFragment = UploadImageFragment.newInstance(null, true, true);
            btn_create_acc.setText(getResources().getString(R.string.lbl_create_group));
        } else {
            groupItem=new Gson().fromJson(jsonGroup, GroupItem.class);
            setTitle(getResources().getString(R.string.action_edit_group));
            imageFragment = UploadImageFragment.newInstance(groupItem.getImage(), true, true);
            edtName.setText(groupItem.getName());
            edtAbout.setText(groupItem.aboutGroup);
            spType.setVisibility(View.GONE);
            tvGroupType.setVisibility(View.GONE);
            btn_create_acc.setText(getResources().getString(R.string.lbl_update_changes));
            groupType=groupItem.type;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, imageFragment).commit();
        getSupportFragmentManager().executePendingTransactions();

        String[] values=getResources().getStringArray(R.array.publc_private_array);
        spType.setAdapter(new ArrayAdapter<String>(this,R.layout.spinner_layout,R.id.tvSp,values));


        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==1)
                {
                    groupType="private";
                }
                else if(position==2)
                {
                    groupType="public";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if (BuildConfig.AppCategory.equalsIgnoreCase("constituency"))
        {
            aboutGroup.setText(getResources().getString(R.string.lbl_about_constituency));
        }
        else
        {

            aboutGroup.setText(getResources().getString(R.string.lbl_about_group));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.nav_delete_group:
                if (isConnectionAvailable()) {
                    if (groupItem.totalUsers > 1) {
                        Toast.makeText(CreateAccountActivity.this, getString(R.string.msg_group_delete), Toast.LENGTH_SHORT).show();
                        break;
                    }
                    SMBDialogUtils.showSMBDialogOKCancel(CreateAccountActivity.this, getResources().getString(R.string.smb_delete_group), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // showLoadingDialog();
                            if(progressBar!=null)
                                showLoadingBar(progressBar);
                                //progressBar.setVisibility(View.VISIBLE);
                            LeafManager manager=new LeafManager();
                            manager.deleteGroup(CreateAccountActivity.this,groupItem.getGroupId()+"");
                        }
                    });
                } else {
                    showNoNetworkMsg();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about_group,menu);
        this.menu=menu;
        if(groupItem!=null)
        {
            menu.findItem(R.id.nav_edit_group).setVisible(false);
            menu.findItem(R.id.nav_delete_group).setVisible(false);
            menu.findItem(R.id.nav_change_admin).setVisible(false);
            menu.findItem(R.id.nav_group_settings).setVisible(false);
           // menu.findItem(R.id.nav_leave_group).setVisible(false);
        }
        else
        {
            menu.findItem(R.id.nav_edit_group).setVisible(false);
            menu.findItem(R.id.nav_delete_group).setVisible(false);
            menu.findItem(R.id.nav_change_admin).setVisible(false);
            menu.findItem(R.id.nav_group_settings).setVisible(false);
            //menu.findItem(R.id.nav_leave_group).setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }


    public boolean isValid() {
        boolean valid = true;
        if (!isValueValid(edtName)) {
            valid = false;
        }

        else if(groupItem==null)
        {
            if(spType.getSelectedItemPosition()==-1 || spType.getSelectedItemPosition()==0)
            {
                Toast.makeText(this, getResources().getString(R.string.toast_select_group_type), Toast.LENGTH_SHORT).show();
                valid=false;
            }
        }
        return valid;
    }

    @OnClick({R.id.btn_create_acc,R.id.btn_update})
    public void onClick(View v) {
        hide_keyboard();
        switch (v.getId()) {

            case R.id.btn_update:
            case R.id.btn_create_acc:
                if (isConnectionAvailable()) {
                    if (isValid()) {
                        if(progressBar!=null)
                            showLoadingBar(progressBar);
                        //progressBar.setVisibility(View.VISIBLE);
                        LeafManager manager = new LeafManager();

                        CreateGroupReguest request = new CreateGroupReguest();
                        request.aboutGroup = edtAbout.getText().toString();
                        request.name = edtName.getText().toString();
                        request.type=groupType;
                        request.category = "";
                        request.subCategory = "";
                        request.avatar = imageFragment.getmProfileImage();

                        if (imageFragment.isImageChanged && TextUtils.isEmpty(imageFragment.getmProfileImage())) {
                            if (groupItem != null) {
                                manager.deleteGroupPic(this, groupItem.getGroupId());
                            }
                        }

                        if (groupItem != null) {
                           AppLog.e("here ", "errh " + groupItem.id);
                           AppLog.e("CreateGroup Edit", "request sttring : " + new Gson().toJson(request));
                            manager.editGroup(this, request, groupItem.id);
                        } else {
                           AppLog.e("CreateGroup", "request string : " + new Gson().toJson(request));
                            manager.addGroup(this, request);

                            cleverTapCreateAcc(request);
                        }

                    }
                } else {
                    showNoNetworkMsg();
                }
                break;

        }
    }

    private void cleverTapCreateAcc(CreateGroupReguest request) {
        if(isConnectionAvailable())
        {
            try {
                CleverTapAPI cleverTap = CleverTapAPI.getInstance(getApplicationContext());
               AppLog.e("CreateAcc","Success to found cleverTap objects=>");

                HashMap<String, Object> createAccAction = new HashMap<String, Object>();

                createAccAction.put("name",request.name);
                createAccAction.put("about",request.aboutGroup);

                cleverTap.event.push("New Group Created", createAccAction);
               AppLog.e("CreateAcc","Success");


            } catch (CleverTapMetaDataNotFoundException e) {
               AppLog.e("CreateAcc","CleverTapMetaDataNotFoundException=>"+e.toString());
                // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
            } catch (CleverTapPermissionsNotSatisfied e) {
               AppLog.e("CreateAcc","CleverTapPermissionsNotSatisfied=>"+e.toString());
                // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
            }

        }

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        if(progressBar!=null)
            hideLoadingBar();
            //progressBar.setVisibility(View.GONE);
        if (apiId == LeafManager.API_ID_CREATE_GROUP) {
            Toast.makeText(this, getString(R.string.msg_creted), Toast.LENGTH_LONG).show();
            LeafPreference.getInstance(this).setBoolean(LeafPreference.ISGROUPUPDATED, true);
            CreateGroupResponse res = (CreateGroupResponse) response;
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra("id", res.data.groupId);
            intent.putExtra("created", true);
            startActivity(intent);
           AppLog.e("CREATE", "id is " + res.data.groupId);
        } else if (apiId == LeafManager.API_ID_DELETE_GROUPPIC) {

        }else if (apiId == LeafManager.API_ID_DELETE_GROUP) {
            LeafPreference.getInstance(this).setBoolean(LeafPreference.ISGROUPUPDATED, true);
            Toast.makeText(CreateAccountActivity.this, getResources().getString(R.string.msg_deleted_group), Toast.LENGTH_SHORT).show();

            cleverTapDeleteGroup();

            Intent intent = new Intent(this, GroupListActivityNew.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, getResources().getString(R.string.toast_successfully_updated_group_details), Toast.LENGTH_LONG).show();
            LeafPreference.getInstance(this).setBoolean(LeafPreference.ISGROUPUPDATED, true);
        }
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onFailure(int apiId, String msg) {

    }

    private void cleverTapDeleteGroup() {
        if(isConnectionAvailable())
        {
            try {
                CleverTapAPI cleverTap = CleverTapAPI.getInstance(getApplicationContext());
               AppLog.e("AboutGroup2","Success to found cleverTap objects=>");
                HashMap<String, Object> deleteGrpAction = new HashMap<String, Object>();
                deleteGrpAction.put("id",groupItem.getGroupId()+"");
                deleteGrpAction.put("group_name",groupItem.getName());
                cleverTap.event.push("Remove Group", deleteGrpAction);
               AppLog.e("AboutGroup2","Success");

            } catch (CleverTapMetaDataNotFoundException e) {
               AppLog.e("AboutGroup2","CleverTapMetaDataNotFoundException=>"+e.toString());
                // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
            } catch (CleverTapPermissionsNotSatisfied e) {
               AppLog.e("AboutGroup2","CleverTapPermissionsNotSatisfied=>"+e.toString());
                // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
            }

        }
    }


    @Override
    public void onFailure(int apiId, ErrorResponseModel<GroupValidationError> error) {
        //hideLoadingDialog();
        if(progressBar!=null)
            hideLoadingBar();
        //progressBar.setVisibility(View.GONE);

       AppLog.e("CreateAccount", "onFailure  ,, msg : " + error);
        if (error.status.equals("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            if(error.errors==null)
                return;

            if (error.errors.get(0).name != null) {
                edtName.setError(error.errors.get(0).name);
                edtName.requestFocus();
            }
        }


    }


    @Override
    public void onException(int apiId, String msg) {
        //hideLoadingDialog();
        if(progressBar!=null)
            hideLoadingBar();
        //progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }
}
