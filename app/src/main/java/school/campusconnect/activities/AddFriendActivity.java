package school.campusconnect.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;

import school.campusconnect.fragments.Fragment_PhoneContacts;
import school.campusconnect.utils.AppLog;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.adapters.MyContactListAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.PhoneContactsItems;
import school.campusconnect.network.LeafManager;

public class AddFriendActivity extends BaseActivity
        implements View.OnClickListener, LeafManager.OnCommunicationListener, MyContactListAdapter.OnItemClickListener {

    String TAG = "AddFriendActivity";

    @Bind(R.id.toolbar)
    Toolbar mToolBar;

    @Bind(R.id.fabAdd)
    ImageView fabAdd;

    @Bind(R.id.tvCount)
    TextView tvCount;

    @Bind(R.id.edtSearch)
    public EditText edtSearch;

    LeafManager manager;

    public String groupId;
    Fragment_PhoneContacts phoneContactFragment;
    private boolean isFromTeam;
    private String teamId;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLog.e(TAG, "onCreate Started");
        setContentView(R.layout.app_bar_addfriend);

        initObject();

        searchListener();

        phoneContactFragment = Fragment_PhoneContacts.newInstance(getIntent().getExtras());

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, phoneContactFragment).commit();


        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneContactFragment.addMultipleInvites(isFromTeam,teamId);
            }
        });

        tvCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneContactFragment.addMultipleInvites(isFromTeam,teamId);
            }
        });

        AppLog.e(TAG, "onCreate Completed");
    }

    private void initObject() {
        manager = new LeafManager();

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(R.string.action_add_lead);

        groupId = getIntent().getExtras().getString("id");

        isFromTeam=getIntent().hasExtra("from_team");

        if(isFromTeam)
        {
            teamId=getIntent().getExtras().getString("team_id");
        }
        AppLog.e(TAG, "groupId " + groupId);
        AppLog.e(TAG, "isFromTeam " + isFromTeam);
        AppLog.e(TAG, "teamId " + teamId);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
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
                    if (isConnectionAvailable()) {
                        if (edtSearch.getText().toString().isEmpty()) {
                            Toast.makeText(AddFriendActivity.this, getResources().getString(R.string.toast_input_some_query), Toast.LENGTH_LONG).show();
                        } else {
                            getMyContactSearchData(edtSearch.getText().toString());
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
                    getMyContactSearchData(edtSearch.getText().toString());
                } else if (edtSearch.getText().toString().length() == 0) {
                    getMyContactSearchData("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        AppLog.e(TAG, "onResume Started");
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    public void getMyContactSearchData(String search) {
        if(isFromTeam)
        {
            ArrayList<PhoneContactsItems> contactsItem = new DatabaseHandler(this).getSearchedListTeam(search);
            phoneContactFragment.refreshAdapterPhone(contactsItem);
        }
        else
        {
            ArrayList<PhoneContactsItems> contactsItem = new DatabaseHandler(this).getSearchedList(search);
            phoneContactFragment.refreshAdapterPhone(contactsItem);
        }

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        AppLog.e("onSuccess", "Success");
        AppLog.e("SearchResponse", response.toString());
        hideLoadingDialog();

        switch (apiId) {
            case LeafManager.API_ALL_CONTACT_LIST:
                new BaseActivity.TaskForGruppieContacts(response).execute();
                break;
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingDialog();
        AppLog.e("onFailure", "Failure");
        if (msg.contains("401:Unauthorized")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingDialog();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onInvite(MyContactListAdapter.MyContact myContact) {

    }

    @Override
    public void friendSelected(boolean isSelected, PhoneContactsItems item, int position) {

    }

    public void setAddCountAct(int size) {
        AppLog.e(TAG, "Size1=>" + size);
        super.setAddCount(size);
        AppLog.e(TAG, "Size2=>" + size);
        if (size == 0)
            tvCount.setVisibility(View.INVISIBLE);
        else {
            tvCount.setVisibility(View.VISIBLE);
            tvCount.setText("" + Integer.toString(size));
        }
    }

    public void callInvitePage() {
        Intent i = new Intent(AddFriendActivity.this, InviteFriendActivity.class);
        i.putExtras(getIntent());
        startActivity(i);
    }
}
