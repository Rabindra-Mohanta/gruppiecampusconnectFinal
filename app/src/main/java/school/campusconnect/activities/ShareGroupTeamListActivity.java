package school.campusconnect.activities;

import android.app.Activity;
import android.content.Intent;
//import android.databinding.tool.util.L;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import school.campusconnect.utils.AppLog;
import android.view.View;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.ShareGroupTeamListFragment;
import school.campusconnect.utils.Constants;

/**
 * Created by frenzin04 on 2/7/2017.
 */

public class ShareGroupTeamListActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    @Bind(R.id.btn_update)
    ImageView btn_update;
    Intent intent;
    String group_id;
    String post_id;
    ShareGroupTeamListFragment fragment;
    public static Activity shareGroupTeamListActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_group_list);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(R.string.lbl_select_group);
        shareGroupTeamListActivity = this;
        if (getIntent().getExtras() != null) {
            group_id = getIntent().getExtras().getString("id");
            post_id = getIntent().getExtras().getString("post_id");
           AppLog.e("SHAREDATA", "2group_id " + group_id);
           AppLog.e("SHAREDATA", "2post_id " + post_id);
        }

        fragment = ShareGroupTeamListFragment.newInstance(group_id, post_id);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        getSupportFragmentManager().executePendingTransactions();

        btn_update.setVisibility(View.GONE);
    }

    public void onClickAddComment(View view) {
       AppLog.e("CLICK", "btn_comment clicked");
        fragment.onClickAddComment();
    }

    @Override
    protected void onDestroy() {
       AppLog.e("onDestroy", "called");
        setResult(Constants.requestCode);
        super.onDestroy();
    }

}
