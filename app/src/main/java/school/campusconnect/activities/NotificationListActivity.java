package school.campusconnect.activities;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.fragments.NotificationListFragment;

public class NotificationListActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;
    private boolean isNest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null)
        {
            isNest=bundle.getBoolean("isNest",false);
            setTitle(bundle.getString("title",""));
        }
        if(!isNest)
            setTitle(getResources().getString(R.string.text_notifications));


        NotificationListFragment notificationListFragment=new NotificationListFragment();
        notificationListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,notificationListFragment).commit();

        LeafPreference.getInstance(this).remove(GroupDashboardActivityNew.groupId + "_notification_count");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
