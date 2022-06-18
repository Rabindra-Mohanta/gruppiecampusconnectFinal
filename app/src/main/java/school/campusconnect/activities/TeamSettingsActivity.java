package school.campusconnect.activities;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.TeamSettingRes;
import school.campusconnect.network.LeafManager;

public class TeamSettingsActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolBar;

    @Bind(R.id.switch_comment_all)
    Switch switch_comment_all;

    @Bind(R.id.switch_post_all)
    Switch switch_post_all;

    @Bind(R.id.switch_enable_gps)
    Switch switch_enable_gps;

    @Bind(R.id.switch_attendance)
    Switch switch_attendance;

    @Bind(R.id.switch_allow_addMember)
    Switch switch_allow_addMember;

    @Bind(R.id.llGps)
    RelativeLayout llGps;

    @Bind(R.id.llAttendance)
    RelativeLayout llAttendance;


    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    LeafManager manager = new LeafManager();
    private String groupId;
    private String teamId;
    private String teamType;
    private TeamSettingRes settingRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_settings);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.action_settings));

        groupId = getIntent().getExtras().getString("group_id");
        teamId = getIntent().getExtras().getString("team_id");
        teamType = getIntent().getExtras().getString("teamType","");


       /* if (teamType.equalsIgnoreCase(Constants.TEAM_TYPE_CREATED) && GroupDashboardActivityNew.groupCategory.equals(Constants.CATEGORY_SCHOOL)) {
            llAttendance.setVisibility(View.VISIBLE);
            llGps.setVisibility(View.VISIBLE);
        } else {
            llGps.setVisibility(View.GONE);
            llAttendance.setVisibility(View.GONE);
        }*/


        getSettingData();
    }

    private void getSettingData() {
        if (isConnectionAvailable()) {
            showLoadingBar(progressBar);
          //  progressBar.setVisibility(View.VISIBLE);
            manager.getTeamSettingsData(this, groupId, teamId);
        } else {
            showNoNetworkMsg();
        }
    }

    @OnClick({R.id.switch_comment_all, R.id.switch_post_all, R.id.switch_enable_gps, R.id.switch_allow_addMember,R.id.switch_attendance})
    public void onClick(View v) {

        if (settingRes == null)
            return;

        switch (v.getId()) {
            case R.id.switch_post_all:
                if (progressBar != null)
                    showLoadingBar(progressBar);
                  //  progressBar.setVisibility(View.VISIBLE);
                manager.allowTeamPostAll(this, groupId + "", teamId);
                break;

            case R.id.switch_comment_all:
                if (progressBar != null)
                    showLoadingBar(progressBar);
                   // progressBar.setVisibility(View.VISIBLE);
                manager.allowTeamCommentAll(this, groupId + "", teamId);
                break;
            case R.id.switch_allow_addMember:
                if (progressBar != null)
                    showLoadingBar(progressBar);
                  //  progressBar.setVisibility(View.VISIBLE);
                Log.e("TeamSetting",switch_allow_addMember.isChecked()+"");
                    manager.allowUsersToAddTeamMember(this, groupId + "", teamId,switch_allow_addMember.isChecked());
                break;
            case R.id.switch_enable_gps:
                if(switch_enable_gps.isChecked())
                {
                    if (!switch_attendance.isChecked()) {
                        if (progressBar != null)
                            showLoadingBar(progressBar);
                        //    progressBar.setVisibility(View.VISIBLE);
                        manager.enableDisableGps(this, groupId + "", teamId);
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.toast_please_disable_attendance), Toast.LENGTH_SHORT).show();
                        switch_enable_gps.setChecked(false);
                    }
                }
                else
                {
                    if (progressBar != null)
                        showLoadingBar(progressBar);
                        //progressBar.setVisibility(View.VISIBLE);
                    manager.enableDisableGps(this, groupId + "", teamId);
                }


                break;
            case R.id.switch_attendance:
                if(switch_attendance.isChecked())
                {
                    if (!switch_enable_gps.isChecked()) {
                        if (progressBar != null)
                            showLoadingBar(progressBar);
                           // progressBar.setVisibility(View.VISIBLE);
                        manager.enableDisableAttendance(this, groupId + "", teamId);
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.toast_please_disable_gps), Toast.LENGTH_SHORT).show();
                        switch_attendance.setChecked(false);
                    }
                }
                else
                {
                    if (progressBar != null)
                        showLoadingBar(progressBar);
                      //  progressBar.setVisibility(View.VISIBLE);
                    manager.enableDisableAttendance(this, groupId + "", teamId);
                }

                break;


        }

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        // hideLoadingDialog();
        if (progressBar != null)
            hideLoadingBar();
         //   progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_TEAM_SETTING_DATA:
                settingRes = (TeamSettingRes) response;
                if (settingRes.data.allowTeamPostAll) {
                    switch_post_all.setChecked(true);
                }
                if (settingRes.data.allowTeamPostCommentAll) {
                    switch_comment_all.setChecked(true);
                }
                if (settingRes.data.enableGps) {
                    switch_enable_gps.setChecked(true);
                }
                if (settingRes.data.enableAttendance) {
                    switch_attendance.setChecked(true);
                }
                if (settingRes.data.allowTeamUsersToAddMembersToGroup) {
                    switch_allow_addMember.setChecked(true);
                }
                break;

            case LeafManager.API_ALLOW_TEAM_POST_ALL:
                LeafPreference.getInstance(this).setBoolean(LeafPreference.ISTEAMUPDATED, true);

                break;

            case LeafManager.API_ALLOW_TEAM_COMMENT_ALL:
                LeafPreference.getInstance(this).setBoolean(LeafPreference.ISTEAMUPDATED, true);

                break;
            case LeafManager.API_ALLOW_USER_TO_ADD_MEMBER_GROUP:
                LeafPreference.getInstance(this).setBoolean(LeafPreference.ISTEAMUPDATED, true);

                break;
            case LeafManager.API_ENABLE_DISABLE_GPS:
                LeafPreference.getInstance(this).setBoolean(LeafPreference.ISTEAMUPDATED, true);
                break;
            case LeafManager.API_ENABLE_ATTENANCE:
                LeafPreference.getInstance(this).setBoolean(LeafPreference.ISTEAMUPDATED, true);
                break;

        }

    }

    @Override
    public void onFailure(int apiId, String msg) {
        //hideLoadingDialog();
        if (progressBar != null)
            hideLoadingBar();
          //  progressBar.setVisibility(View.GONE);
        switch (apiId) {
            case LeafManager.API_ALLOW_TEAM_POST_ALL:
                if (switch_post_all.isChecked()) {
                    switch_post_all.setChecked(false);
                } else {
                    switch_post_all.setChecked(true);
                }
                break;

            case LeafManager.API_ALLOW_TEAM_COMMENT_ALL:
                if (switch_comment_all.isChecked()) {
                    switch_comment_all.setChecked(false);
                } else {
                    switch_comment_all.setChecked(true);
                }
                break;
            case LeafManager.API_ENABLE_DISABLE_GPS:
                if (switch_enable_gps.isChecked()) {
                    switch_enable_gps.setChecked(false);
                } else {
                    switch_enable_gps.setChecked(true);
                }
                break;
            case LeafManager.API_ENABLE_ATTENANCE:
                if (switch_attendance.isChecked()) {
                    switch_attendance.setChecked(false);
                } else {
                    switch_attendance.setChecked(true);
                }
                break;
             case LeafManager.API_ALLOW_USER_TO_ADD_MEMBER_GROUP:
                if (switch_allow_addMember.isChecked()) {
                    switch_allow_addMember.setChecked(false);
                } else {
                    switch_allow_addMember.setChecked(true);
                }
                break;



        }

    }

    @Override
    public void onException(int apiId, String msg) {
        //hideLoadingDialog();
        if (progressBar != null)
            hideLoadingBar();
         //   progressBar.setVisibility(View.GONE);
        switch (apiId) {
            case LeafManager.API_ALLOW_TEAM_POST_ALL:
                if (switch_post_all.isChecked()) {
                    switch_post_all.setChecked(false);
                } else {
                    switch_post_all.setChecked(true);
                }
                break;

            case LeafManager.API_ALLOW_TEAM_COMMENT_ALL:
                if (switch_comment_all.isChecked()) {
                    switch_comment_all.setChecked(false);
                } else {
                    switch_comment_all.setChecked(true);
                }
                break;
            case LeafManager.API_ENABLE_DISABLE_GPS:
                if (switch_enable_gps.isChecked()) {
                    switch_enable_gps.setChecked(false);
                } else {
                    switch_enable_gps.setChecked(true);
                }
                break;
            case LeafManager.API_ENABLE_ATTENANCE:
                if (switch_attendance.isChecked()) {
                    switch_attendance.setChecked(false);
                } else {
                    switch_attendance.setChecked(true);
                }
                break;
            case LeafManager.API_ALLOW_USER_TO_ADD_MEMBER_GROUP:
                if (switch_allow_addMember.isChecked()) {
                    switch_allow_addMember.setChecked(false);
                } else {
                    switch_allow_addMember.setChecked(true);
                }
                break;

        }
    }
}
