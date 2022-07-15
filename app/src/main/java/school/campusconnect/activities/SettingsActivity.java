package school.campusconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.SettingRes;
import school.campusconnect.network.LeafManager;

/**
 * Created by frenzin04 on 5/25/2017.
 */

public class SettingsActivity extends BaseActivity implements LeafManager.OnCommunicationListener {

    @Bind(R.id.toolbar)
    Toolbar mToolBar;

    @Bind(R.id.switch_post_que)
    Switch switch_post_que;

    @Bind(R.id.switch_share)
    Switch switch_share;

    @Bind(R.id.switch_everyone)
    Switch switch_everyone;

    @Bind(R.id.switch_change_admin)
    Switch switch_change_admin;


    @Bind(R.id.btn_add_friends)
    Button btn_add_friends;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    LeafManager manager = new LeafManager();

    String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.action_setting));
        groupId = getIntent().getExtras().getString("id");

        if (getIntent().getExtras().getBoolean("created", false)) {
            btn_add_friends.setVisibility(View.VISIBLE);
        } else {
            getSettingData();
        }

    }

    private void getSettingData() {
        if (isConnectionAvailable()) {
            showLoadingBar(progressBar,false);
            //progressBar.setVisibility(View.VISIBLE);
            manager.getSettingsData(this, groupId);
        } else {
            showNoNetworkMsg();
        }
    }

    @OnClick({R.id.switch_post_que, R.id.switch_share, R.id.switch_everyone, R.id.btn_add_friends, R.id.switch_change_admin})
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.switch_post_que:
                if (progressBar != null)
                    showLoadingBar(progressBar,false);
                //progressBar.setVisibility(View.VISIBLE);
                manager.allowPostQue(this, groupId + "");
                break;

            case R.id.switch_share:
                if (progressBar != null)
                    showLoadingBar(progressBar,false);
                // progressBar.setVisibility(View.VISIBLE);
                manager.allowShare(this, groupId + "");
                break;
            case R.id.switch_everyone:
                if (progressBar != null)
                    showLoadingBar(progressBar,false);
                // progressBar.setVisibility(View.VISIBLE);
                manager.allowPostToAll(this, groupId + "");
                break;
            case R.id.switch_change_admin:
                if (progressBar != null)
                    showLoadingBar(progressBar,false);
                   // progressBar.setVisibility(View.VISIBLE);
                manager.allowChangeAdmin(this, groupId + "");
                break;


            case R.id.btn_add_friends:
                Intent intent = new Intent(this, AddFriendActivity.class);
                intent.putExtra("id", groupId);
                startActivity(intent);
                this.finish();
                break;


        }

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        // hideLoadingDialog();
        if (progressBar != null)
            hideLoadingBar();
        // progressBar.setVisibility(View.GONE);
        LeafPreference.getInstance(this).setBoolean(LeafPreference.ISGROUPUPDATED, true);
        switch (apiId) {
            case LeafManager.API_SETTING_DATA:
                SettingRes settingRes = (SettingRes) response;
                if (GroupDashboardActivityNew.mAllowPostQuestion) {
                    switch_post_que.setChecked(true);
                }
                if (settingRes.data.allowPostShare) {
                    switch_share.setChecked(true);
                }
                if (settingRes.data.allowPostAll) {
                    switch_everyone.setChecked(true);
                }
                if (settingRes.data.allowAdminChange) {
                    switch_change_admin.setChecked(true);
                }

                break;

            case LeafManager.API_ALLOW_POST_QUE:
                GroupDashboardActivityNew.mAllowPostQuestion = switch_post_que.isChecked();
                break;

            case LeafManager.API_ALLOW_SHARE:
                GroupDashboardActivityNew.mAllowPostShare = switch_share.isChecked();
                break;
            case LeafManager.API_ALLOW_TO_POST_ALL:
                GroupDashboardActivityNew.mAllowPostAll = switch_everyone.isChecked();
                break;
            case LeafManager.API_ALLOW_CHANGE_ADMIN:
                GroupDashboardActivityNew.mAllowAdminChange = switch_change_admin.isChecked();
                break;


        }

    }

    @Override
    public void onFailure(int apiId, String msg) {
        //hideLoadingDialog();
        if (progressBar != null)
            hideLoadingBar();
        // progressBar.setVisibility(View.GONE);
        switch (apiId) {
            case LeafManager.API_ALLOW_POST_QUE:
                if (switch_post_que.isChecked()) {
                    switch_post_que.setChecked(false);
                } else {
                    switch_post_que.setChecked(true);
                }
                break;

            case LeafManager.API_ALLOW_SHARE:
                if (switch_share.isChecked()) {
                    switch_share.setChecked(false);
                } else {
                    switch_share.setChecked(true);
                }
                break;
            case LeafManager.API_ALLOW_TO_POST_ALL:
                if (switch_everyone.isChecked()) {
                    switch_everyone.setChecked(false);
                } else {
                    switch_everyone.setChecked(true);
                }
                break;
            case LeafManager.API_ALLOW_CHANGE_ADMIN:
                if (switch_change_admin.isChecked()) {
                    switch_change_admin.setChecked(false);
                } else {
                    switch_change_admin.setChecked(true);
                }
                break;


        }

    }

    @Override
    public void onException(int apiId, String msg) {
        //hideLoadingDialog();
        if (progressBar != null)
            hideLoadingBar();
        // progressBar.setVisibility(View.GONE);
        switch (apiId) {
            case LeafManager.API_ALLOW_POST_QUE:
                if (switch_post_que.isChecked()) {
                    switch_post_que.setChecked(false);
                } else {
                    switch_post_que.setChecked(true);
                }
                break;

            case LeafManager.API_ALLOW_SHARE:
                if (switch_share.isChecked()) {
                    switch_share.setChecked(false);
                } else {
                    switch_share.setChecked(true);
                }
                break;
            case LeafManager.API_ALLOW_TO_POST_ALL:
                if (switch_everyone.isChecked()) {
                    switch_everyone.setChecked(false);
                } else {
                    switch_everyone.setChecked(true);
                }
                break;

            case LeafManager.API_ALLOW_CHANGE_ADMIN:
                if (switch_change_admin.isChecked()) {
                    switch_change_admin.setChecked(false);
                } else {
                    switch_change_admin.setChecked(true);
                }
                break;

        }
    }
}
