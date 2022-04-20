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
import school.campusconnect.adapters.LeadAdapter;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.LeadItem;
import school.campusconnect.fragments.AuthorizedUserFragment;
import school.campusconnect.network.LeafManager;


public class AuthorizedUserActivity extends BaseActivity implements LeafManager.OnCommunicationListener, LeadAdapter.OnLeadSelectListener {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    @Bind(R.id.edtSearch)
    EditText edtSearch;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    Intent intent;
    AuthorizedUserFragment fragment;
    String groupId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lead_list);
        ButterKnife.bind(this);

        if (getIntent() != null) {
            groupId = getIntent().getExtras().getString("id");
           AppLog.e("PERSNOL_NEW", "gid5 is " + groupId);
        }

        searchListener();
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(R.string.lbl_autho);
        fragment = AuthorizedUserFragment.newInstance(groupId);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    /* @Override
    protected void onRestart() {
        super.onRestart();
        fragment = AuthorizedUserFragment.newInstance(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }*/

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
                            Toast.makeText(AuthorizedUserActivity.this, getResources().getString(R.string.toast_input_some_query), Toast.LENGTH_LONG).show();
                        } else {
                            getSearchData(edtSearch.getText().toString());
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
                        getSearchData(edtSearch.getText().toString());
                    } else {
                        showNoNetworkMsg();
                    }
                } else if (edtSearch.getText().toString().length() == 0) {
                    if (isConnectionAvailable()) {
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
      //  showLoadingDialog();
        if(progressBar!=null)
        progressBar.setVisibility(View.VISIBLE);
        LeafManager manager = new LeafManager();
        manager.getAuthorizedListFromSearch(this, groupId+"", search);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
       AppLog.e("onSuccessCalled", "Success");
       AppLog.e("ListSearchResponse", response.toString());
      //  hideLoadingDialog();
        if(progressBar!=null)
        progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_AUTHOREIZED_USER:
                fragment.refreshData(response);
                break;
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
       AppLog.e("onFailureCalled", "Failure");
     //   hideLoadingDialog();
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

    @Override
    public void onCallClick(LeadItem item) {

    }

    @Override
    public void onSMSClick(LeadItem item) {

    }

    @Override
    public void onMailClick(LeadItem item) {

    }

    @Override
    public void onNameClick(LeadItem item) {

    }
}
