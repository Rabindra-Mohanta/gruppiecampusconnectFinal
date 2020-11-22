package school.campusconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import school.campusconnect.utils.AppLog;
import android.view.View;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.PublicGroupJoinFragment;

/**
 * Created by frenzin04 on 2/7/2017.
 */

public class PublicGroupJoinActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.btn_update)
    ImageView btn_update;

    Intent intent;
    String group_id;
    PublicGroupJoinFragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_group_list);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(R.string.lbl_join_group);
        if (getIntent().getExtras() != null) {
            group_id = getIntent().getExtras().getString("id");
           AppLog.e("SHAREDATA", "2group_id " + group_id);
        }

        fragment = PublicGroupJoinFragment.newInstance(group_id);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        getSupportFragmentManager().executePendingTransactions();

        btn_update.setVisibility(View.GONE);
    }

}
