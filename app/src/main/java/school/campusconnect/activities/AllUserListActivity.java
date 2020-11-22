package school.campusconnect.activities;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.fragments.AllUserListFragment;
import school.campusconnect.network.LeafManager;

public class AllUserListActivity extends BaseActivity implements LeafManager.OnCommunicationListener {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    @Bind(R.id.edtSearch)
    EditText edtSearch;
    Intent intent;
    AllUserListFragment fragment;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    String groupId;

    boolean change;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        ButterKnife.bind(this);

        if (getIntent() != null) {
            groupId = getIntent().getExtras().getString("id");
            change = getIntent().getExtras().getBoolean("change");
        }
       // searchListener();
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(R.string.lbl_all_users);
        fragment = AllUserListFragment.newInstance(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        getSupportFragmentManager().executePendingTransactions();

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
                edtSearch.setCursorVisible(false);
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    if (isConnectionAvailable()) {
                        if (edtSearch.getText().toString().isEmpty()) {
                            Toast.makeText(AllUserListActivity.this, "Input some query first", Toast.LENGTH_LONG).show();
                        } else {
                            fragment.lastSent = 0;
                            fragment.limit = 9;
                            getSearchData(edtSearch.getText().toString());
//                            fragment.getFilteredList(edtSearch.getText().toString());
//                            getSearchData(edtSearch.getText().toString());
                        }
                    } else {
                        showNoNetworkMsg();
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
                if (edtSearch.getText().toString().length() > 2) {
                    if (isConnectionAvailable()) {
                        fragment.lastSent = 0;
                        fragment.limit = 9;
                            getSearchData(edtSearch.getText().toString());
//                        fragment.getFilteredList(edtSearch.getText().toString());
//                        getSearchData(edtSearch.getText().toString());
                    } else {
                        showNoNetworkMsg();
                    }
                } else if (edtSearch.getText().toString().length() == 0) {
                    if (isConnectionAvailable()) {
                        fragment.lastSent = 0;
                        fragment.limit = 9;
//                            getSearchData(edtSearch.getText().toString());
//                        fragment.getFilteredList("");
                        getSearchData("");
                    } else {
                        showNoNetworkMsg();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void getSearchData(String search) {
        if(isConnectionAvailable())
        {
           // showLoadingDialog();
            if(progressBar!=null)
            progressBar.setVisibility(View.VISIBLE);
            LeafManager manager = new LeafManager();
            manager.getAllUsersListBySearch(this, groupId+"", search);
        }
        else {
            showNoNetworkMsg();
        }

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
       AppLog.e("onSuccessCalled", "Success");
       AppLog.e("ListSearchResponse", response.toString());
      //  hideLoadingDialog();
        if(progressBar!=null)
        progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_ALL_USERS_LIST:
                fragment.refreshData(response);
                break;
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
       AppLog.e("onFailureCalled", "Failure");
      //  hideLoadingDialog();
        if(progressBar!=null)
        progressBar.setVisibility(View.GONE);
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
}
