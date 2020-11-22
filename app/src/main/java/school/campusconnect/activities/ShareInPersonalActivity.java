package school.campusconnect.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import school.campusconnect.utils.AppLog;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.ShareInPersonalFragment;
import school.campusconnect.utils.Constants;

/**
 * Created by frenzin04 on 3/8/2017.
 */

public class ShareInPersonalActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    @Bind(R.id.edtSearch)
    EditText edtSearch;
    @Bind(R.id.btn_update)
    ImageView btn_update;
    Intent intent;
    String group_id;
    String post_id;
    ShareInPersonalFragment fragment;

    public static Activity shareInPersonalActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_group_list);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(R.string.lbl_select_group);
        shareInPersonalActivity = this;
        edtSearch.setVisibility(View.GONE);

        if (getIntent().getExtras() != null) {
            group_id = getIntent().getExtras().getString("id");
            post_id = getIntent().getExtras().getString("post_id");
           AppLog.e("SHAREDATA", "2group_id " + group_id);
           AppLog.e("SHAREDATA", "2post_id " + post_id);
        }

        fragment = ShareInPersonalFragment.newInstance(group_id, post_id);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        getSupportFragmentManager().executePendingTransactions();
        btn_update.setVisibility(View.GONE);

        searchListener();

    }

    private void searchListener() {
        edtSearch.setCursorVisible(false);
        edtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               AppLog.e("AddFriend", "onClick ");
                edtSearch.setCursorVisible(true);
            }
        });
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                edtSearch.setCursorVisible(false);
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                        if (edtSearch.getText().toString().isEmpty()) {
                            fragment.callFilter(edtSearch.getText().toString(),0);
                        } else {
                            fragment.callFilter(edtSearch.getText().toString(), edtSearch.getText().toString().length());
                        }
                    return true;
                }

                return false;
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fragment.callFilter(edtSearch.getText().toString(), count);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
