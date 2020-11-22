package school.campusconnect.activities;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Switch;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.PersonalSettingRes;
import school.campusconnect.network.LeafManager;

public class PersonalSettingsActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolBar;

    @Bind(R.id.switch_reply)
    Switch switch_reply;

    @Bind(R.id.switch_comment)
    Switch switch_comment;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    LeafManager manager = new LeafManager();
    private String groupId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_settings);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle("Settings");

        groupId = getIntent().getExtras().getString("group_id");
        userId = getIntent().getExtras().getString("user_id");

        getSettingData();
    }

    private void getSettingData() {
        if(isConnectionAvailable())
        {
            progressBar.setVisibility(View.VISIBLE);
            manager.getPersonalSettingsData(this,groupId,userId);
        }
        else
        {
            showNoNetworkMsg();
        }
    }

    @OnClick({ R.id.switch_reply, R.id.switch_comment})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_reply:
                if(progressBar!=null)
                    progressBar.setVisibility(View.VISIBLE);
                manager.allowPersonalReply(this, groupId+"",userId);
                break;

            case R.id.switch_comment:
                if(progressBar!=null)
                    progressBar.setVisibility(View.VISIBLE);
                manager.allowPersonalComment(this, groupId+"",userId);
                break;

        }

    }
    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        // hideLoadingDialog();
        if(progressBar!=null)
            progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_PERSONAL_SETTING_DATA:
                PersonalSettingRes settingRes= (PersonalSettingRes) response;
                if (settingRes.data.allowReplyPost) {
                    switch_reply.setChecked(true);
                }
                if (settingRes.data.allowComment) {
                    switch_comment.setChecked(true);
                }
                break;

            case LeafManager.API_ALLOW_PERSONAL_REPLY:

                break;

            case LeafManager.API_ALLOW_PERSONAL_COMMENT:

                break;

        }

    }

    @Override
    public void onFailure(int apiId, String msg) {
        //hideLoadingDialog();
        if(progressBar!=null)
            progressBar.setVisibility(View.GONE);
        switch (apiId) {
            case LeafManager.API_ALLOW_PERSONAL_REPLY:
                if (switch_reply.isChecked()) {
                    switch_reply.setChecked(false);
                } else {
                    switch_reply.setChecked(true);
                }
                break;

            case LeafManager.API_ALLOW_PERSONAL_COMMENT:
                if (switch_comment.isChecked()) {
                    switch_comment.setChecked(false);
                } else {
                    switch_comment.setChecked(true);
                }
                break;
        }

    }

    @Override
    public void onException(int apiId, String msg) {
        //hideLoadingDialog();
        if(progressBar!=null)
            progressBar.setVisibility(View.GONE);
        switch (apiId) {
            case LeafManager.API_ALLOW_PERSONAL_REPLY:
                if (switch_reply.isChecked()) {
                    switch_reply.setChecked(false);
                } else {
                    switch_reply.setChecked(true);
                }
                break;

            case LeafManager.API_ALLOW_PERSONAL_COMMENT:
                if (switch_comment.isChecked()) {
                    switch_comment.setChecked(false);
                } else {
                    switch_comment.setChecked(true);
                }
                break;
        }
    }
}
