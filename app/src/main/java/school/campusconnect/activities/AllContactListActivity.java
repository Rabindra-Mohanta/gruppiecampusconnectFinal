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
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.fragments.AllContactListFragment;
import school.campusconnect.network.LeafManager;

public class AllContactListActivity extends BaseActivity implements LeafManager.OnCommunicationListener {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    Intent intent;

    AllContactListFragment fragment;

    @Bind(R.id.edtSearch)
    EditText edtSearch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(R.string.lbl_all_contact);

        searchListener();

        fragment = AllContactListFragment.newInstance(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }


    public void searchListener() {
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
                        Toast.makeText(AllContactListActivity.this, getResources().getString(R.string.toast_input_some_query), Toast.LENGTH_LONG).show();
                    } else {
                        fragment.lastSent = 0;
                        fragment.limit = 9;
//                        getSearchData(edtSearch.getText().toString());
                        fragment.getFilteredList(edtSearch.getText().toString());
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
//                    getSearchData(edtSearch.getText().toString());
                    fragment.lastSent = 0;
                    fragment.limit = 9;
                    fragment.getFilteredList(edtSearch.getText().toString());
                } else if (edtSearch.getText().toString().length() == 0) {
//                    getSearchData("");
                    fragment.lastSent = 0;
                    fragment.limit = 9;
                    fragment.getFilteredList("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

  /*  public void getSearchData(String search) {
        showLoadingDialog();
        LeafManager manager = new LeafManager();
        manager.getAllContactsListBySearch(this, search);
    }*/

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingDialog();
        if (apiId == LeafManager.API_ALL_CONTACT_LIST) {
            new BaseActivity.TaskForGruppieContacts(response).execute();
            /*if (fragment != null) {
                fragment.refreshAdapter(response);
            }*/
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {

    }

    @Override
    public void onException(int apiId, String msg) {

    }

   /* @Override
    protected void onRestart() {
        super.onRestart();
        fragment = AllUserListFragment.newInstance(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }*/
}
