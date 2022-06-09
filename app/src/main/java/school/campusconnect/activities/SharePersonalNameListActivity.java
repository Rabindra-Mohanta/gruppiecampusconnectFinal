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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.fragments.SharePersonalNameListFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.Constants;

/**
 * Created by frenzin04 on 3/8/2017.
 */

public class SharePersonalNameListActivity extends BaseActivity implements LeafManager.OnCommunicationListener {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.btn_update)
    ImageView btn_update;
    @Bind(R.id.edtSearch)
    EditText edtSearch;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    Intent intent;
    String group_id;
    String post_id;
    String selected_group_id;
    int share;
    String type;
    SharePersonalNameListFragment fragment;
    public static Activity sharePersonalNameListActivity;
    public static int selectCount=0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_group_list);
        selectCount=0;
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(R.string.lbl_select_friend);
        searchListener();
        sharePersonalNameListActivity = this;
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

        fragment = SharePersonalNameListFragment.newInstance(group_id, post_id, selected_group_id, type, share);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        getSupportFragmentManager().executePendingTransactions();

        edtSearch.setVisibility(View.VISIBLE);

        btn_update.setVisibility(View.GONE);
        btn_update.setOnClickListener(new View.OnClickListener() {
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

    public void searchListener() {

        edtSearch.setCursorVisible(false);

        edtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               AppLog.e("GroupDashboard", "onClick ");
                edtSearch.setCursorVisible(true);
            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    edtSearch.setCursorVisible(false);
//                    if (isConnectionAvailable()) {
                    if (edtSearch.getText().toString().isEmpty()) {
                        Toast.makeText(SharePersonalNameListActivity.this, getResources().getString(R.string.toast_input_some_query), Toast.LENGTH_LONG).show();
                    } else {
                        fragment.getFilteredList(edtSearch.getText().toString());
                    }
//                    } else {
//                        showNoNetworkMsg();
//                    }
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
                if (edtSearch.getText().toString().length() > 2) {
                    /*if (isConnectionAvailable()) {
                        getSearchData(edtSearch.getText().toString());
                    } else {
                        showNoNetworkMsg();
                    }*/
                    fragment.getFilteredList(edtSearch.getText().toString());
                } else if (edtSearch.getText().toString().length() == 0) {
                    /*if (isConnectionAvailable()) {
                        getSearchData("");
                    } else {
                        showNoNetworkMsg();
                    }*/
                    fragment.getFilteredList("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void getSearchData(String search) {
       // showLoadingDialog();
        if(progressBar!=null)
            showLoadingBar(progressBar,false);
       // progressBar.setVisibility(View.VISIBLE);
        LeafManager manager = new LeafManager();
//        if (myTeamList)
        manager.getLeadsListBySearch(this, selected_group_id+"", search);
//            manager.getLeadsListBySearch_new(this, groupId, LeafPreference.getInstance(getApplicationContext()).getInt(LeafPreference.LOGIN_ID), search);
//        else
//            manager.getLeadsListBySearch_new(this, groupId, teamId, search);

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
       AppLog.e("onSuccessCalled", "Success");
       AppLog.e("ListSearchResponse", response.toString());
       // hideLoadingDialog();
        if(progressBar!=null)
            hideLoadingBar();
       // progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_ID_LEAD_LIST_SEARCH:
                fragment.refreshData(response);
                break;
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
       AppLog.e("onFailureCalled", "Failure");
        //hideLoadingDialog();
        if(progressBar!=null)
            hideLoadingBar();
       // progressBar.setVisibility(View.GONE);
        if (msg.contains("401:Unauthorized")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onException(int apiId, String msg) {
       AppLog.e("Exception:", msg);
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
