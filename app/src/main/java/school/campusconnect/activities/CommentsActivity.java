package school.campusconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import school.campusconnect.utils.AppLog;
import android.view.View;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.CommentsFragment;

/**
 * Created by frenzin04 on 1/18/2017.
 */
public class CommentsActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    Intent intent;
    String group_id;
    String team_id;
    String post_id;
    String type;
    CommentsFragment fragment;
    public static CommentsActivity commentsActivity;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_teams);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(R.string.lbl_comments);

        commentsActivity = this;

        if (getIntent().getExtras() != null) {
            fragment = CommentsFragment.newInstance(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            getSupportFragmentManager().executePendingTransactions();
        }


    }

    public void addTextChangedListener(View view) {
       AppLog.e("CLICK", "getText clicked");
        fragment.dataBinding.addTextChangedListener((EditText) view, 0);
    }

    public void onClickAddComment(View view) {
       AppLog.e("CLICK", "btn_comment clicked");
        fragment.onClickAddComment(view);
    }

}
