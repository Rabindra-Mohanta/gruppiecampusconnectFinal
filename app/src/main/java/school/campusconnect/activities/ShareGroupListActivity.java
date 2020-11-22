package school.campusconnect.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import school.campusconnect.utils.AppLog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.ShareGroupListFragment;
import school.campusconnect.utils.Constants;

/**
 * Created by frenzin04 on 2/6/2017.
 */

public class ShareGroupListActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    @Bind(R.id.edtSearch)
    public EditText edtSearch;
    @Bind(R.id.btn_update)
    ImageView btnUpdate;
    Intent intent;
    String group_id;
    String post_id;
    String selected_group_id;
    int share;
    String type;
    ShareGroupListFragment fragment;
    public static Activity shareGroupListActivity;

    public static int selectCount=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_group_list);
        selectCount=0;
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(R.string.lbl_select_group);
        shareGroupListActivity = this;
        if (getIntent().getExtras() != null) {
            group_id = getIntent().getExtras().getString("id");
            post_id = getIntent().getExtras().getString("post_id");
            selected_group_id = getIntent().getExtras().getString("selected_group_id");
            share = getIntent().getExtras().getInt("share");
            type = getIntent().getExtras().getString("type", "");
            if (type.equals("team"))
                setTitle(R.string.lbl_select_team);
           AppLog.e("SHARE", "Post id3 is " + post_id);
           AppLog.e("SHAREDATA", "5group_id " + group_id);
           AppLog.e("SHAREDATA", "5post_id " + post_id);
           AppLog.e("SHAREDATA", "5selected_group_id " + selected_group_id);
        }

        edtSearch.setVisibility(View.GONE);

        fragment = ShareGroupListFragment.newInstance(group_id, post_id, selected_group_id, type, share);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        getSupportFragmentManager().executePendingTransactions();

        btnUpdate.setVisibility(View.GONE);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddComment(v);
            }
        });

    }

    public void onClickAddComment(View view) {
       AppLog.e("CLICK", "btn_comment clicked");
        fragment.onClickAddComment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(Constants.requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       AppLog.e("onActivityResult", "called requestCode " + requestCode + " resultCode " + resultCode);
        if (resultCode == Constants.finishCode)
            this.finish();
    }

    public  void updateCount() {
        if(fragment.mAdapter.getSelectedCount()>0)
        {
            ((TextView)findViewById(R.id.txtCount)).setText(fragment.mAdapter.getSelectedCount()+"");
        }
        else {
            ((TextView)findViewById(R.id.txtCount)).setText("");
        }

    }
}
