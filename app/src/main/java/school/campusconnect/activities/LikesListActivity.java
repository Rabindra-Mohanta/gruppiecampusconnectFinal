package school.campusconnect.activities;


import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.LikeListFragment;


public class LikesListActivity extends BaseActivity /*implements LeafManager.OnCommunicationListener, LeadAdapter.OnLeadSelectListener*/ {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    @Bind(R.id.edtSearch)
    EditText edtSearch;

    Intent intent;
    LikeListFragment fragment;
    String groupId = "";
    String teamId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lead_list);
        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(R.string.lbl_likes);
        edtSearch.setVisibility(View.GONE);
        fragment = LikeListFragment.newInstance(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        getSupportFragmentManager().executePendingTransactions();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
