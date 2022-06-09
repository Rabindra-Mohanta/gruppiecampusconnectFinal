package school.campusconnect.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;

import school.campusconnect.utils.AppLog;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.AddFriendValidationError;
import school.campusconnect.datamodel.AddLeadRequest;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.gruppiecontacts.AllContactModel;
import school.campusconnect.datamodel.gruppiecontacts.GruppieContactGroupIdModel;
import school.campusconnect.datamodel.gruppiecontacts.GruppieContactsModel;
import school.campusconnect.datamodel.gruppiecontacts.InviteResponseSingle;
import school.campusconnect.network.LeafManager;
import school.campusconnect.views.DrawableEditText;
import school.campusconnect.views.SMBDialogUtils;

public class InviteFriendActivity extends BaseActivity implements View.OnClickListener, LeafManager.OnAddUpdateListener<AddFriendValidationError> {

    private static final String TAG = "InviteFriendActivity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.lead_name)
    public DrawableEditText edtLeadName;

    @Bind(R.id.phoneNumber)
    public DrawableEditText edtPhone;

    @Bind(R.id.email)
    public DrawableEditText edtEmail;

    @Bind(R.id.btn_update)
    ImageView btn_update;

    @Bind(R.id.layout_country)
    DrawableEditText edtCountry;

    @Bind(R.id.btn_add_lead)
    Button addLead;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    String groupId;
    int currentCountry;
    private boolean isFromTeam;
    private String teamId;
    private AddLeadRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);

        initObject();

        getIntentData();

        setData();

    }

    private void initObject() {
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(R.string.action_invite_friend);
    }

    private void getIntentData() {
        groupId = getIntent().getExtras().getString("id");

        if (getIntent().getExtras().containsKey("name")) {
            edtLeadName.editText.setText(getIntent().getExtras().getString("name"));
        }
        if (getIntent().getExtras().containsKey("phone")) {
            edtPhone.editText.setText(getNumberFormatted(getIntent().getExtras().getString("phone")));
        }

        isFromTeam=getIntent().hasExtra("from_team");

        if(isFromTeam)
        {
            teamId=getIntent().getExtras().getString("team_id");
        }
        AppLog.e(TAG, "groupId " + groupId);
        AppLog.e(TAG, "isFromTeam " + isFromTeam);
        AppLog.e(TAG, "teamId " + teamId);
    }

    private void setData() {
        edtLeadName.editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        edtPhone.editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
        edtCountry.editText.setOnClickListener(this);
        edtCountry.editText.setFocusable(false);
        addLead.setOnClickListener(this);

        currentCountry = 1;
        String[] str = getResources().getStringArray(R.array.array_country);
        edtCountry.editText.setText(str[0]);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLead.performClick();
            }
        });
    }


    public boolean isValid() {
        boolean valid = true;
        if (!isValueValid(edtLeadName.editText)) {
            valid = false;
        }
        if (!isValueValid(edtPhone.editText)) {
            valid = false;
        } else if (edtPhone.editText.getText().toString().length() != 10) {
            edtPhone.editText.setError(getString(R.string.msg_valid_phone));
            edtPhone.editText.requestFocus();
            valid = false;
        }

        if (currentCountry == 0) {
            Toast.makeText(this, getResources().getString(R.string.msg_selectcountry), Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (currentCountry != 1) {
            AppLog.e("Check", "3 called");

            if (edtEmail.editText.getText().toString().isEmpty()) {
                edtEmail.editText.setError(getString(R.string.msg_mandatory_email));
                edtEmail.editText.requestFocus();
                valid = false;

            } else if (!isValidEmail(edtEmail.editText.getText().toString())) {
                edtEmail.editText.setError(getString(R.string.msg_valid_email));
                edtEmail.editText.requestFocus();
                valid = false;
            }
        } else {
            AppLog.e("Check", "Else called");
            if (!edtEmail.editText.getText().toString().isEmpty()) {
                if (!isValidEmail(edtEmail.editText.getText().toString())) {
                    edtEmail.editText.setError(getString(R.string.msg_valid_email));
                    edtEmail.editText.requestFocus();
                    valid = false;
                }
            }
        }
        return valid;
    }

    @Override
    public void onClick(View v) {
        hide_keyboard();
        if (v.getId() == R.id.btn_add_lead) {
            if (isConnectionAvailable()) {
                if (isValid()) {
                    if (progressBar != null)
                        showLoadingBar(progressBar,false);
                     //   progressBar.setVisibility(View.VISIBLE);
                     request = new AddLeadRequest();
                    request.name = edtLeadName.editText.getText().toString();
                    request.phone = edtPhone.editText.getText().toString();
                    String[] str = getResources().getStringArray(R.array.array_country_values);
                    request.countryCode = str[currentCountry - 1];

                    if (!edtEmail.editText.getText().toString().isEmpty()) {
                        request.email = edtEmail.editText.getText().toString();
                    }
                    AppLog.e(TAG, "AddLeadRequest send data : " + new Gson().toJson(request));
                    LeafManager manager = new LeafManager();
                    manager.inviteFriend(this, groupId + "",teamId,isFromTeam, request);

                    cleverTapInviteFriend(request.phone);
                }
            } else {
                showNoNetworkMsg();
            }
            return;
        }

        if (v.getId() == R.id.editText) {

            AppLog.e("SignUpActivity", "Clicked Spinner Country");
            SMBDialogUtils.showSMBSingleChoiceDialog(this, R.string.title_country, R.array.array_country, currentCountry - 1,
                    new SMBDailogClickListener(R.id.layout_country));
            return;
        }
    }

    private void cleverTapInviteFriend(String number) {
        try {
            CleverTapAPI cleverTap = CleverTapAPI.getInstance(this);
            AppLog.e("InviteFrend", "Success to found cleverTap objects=>");
            HashMap<String, Object> addFriendAction = new HashMap<String, Object>();
            addFriendAction.put("id", groupId);
            addFriendAction.put("group_name", GroupDashboardActivityNew.group_name);
            addFriendAction.put("phone", number);
            cleverTap.event.push("Invite Friend", addFriendAction);

            AppLog.e("InviteFrend", "Success");

        } catch (CleverTapMetaDataNotFoundException e) {
            AppLog.e("InviteFrend", "CleverTapMetaDataNotFoundException=>" + e.toString());
        } catch (CleverTapPermissionsNotSatisfied e) {
            AppLog.e("InviteFrend", "CleverTapPermissionsNotSatisfied=>" + e.toString());
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            if (uri != null) {
                Cursor c = null;
                try {
                    c = getContentResolver().query(uri, new String[]{
                                    ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                            null, null, null);

                    if (c != null && c.moveToFirst()) {
                        String number = c.getString(0);
                        edtLeadName.editText.setError(null);
                        edtPhone.editText.setError(null);
                        edtLeadName.editText.setText(c.getString(1));

                        String newNumber = "";
                        for (int i = 0; i < number.length(); i++) {
                            if (Character.isDigit(number.charAt(i))) {
                                newNumber = newNumber + number.charAt(i);
                            }
                        }
                        if (newNumber.trim().length() > 10)
                            edtPhone.editText.setText(newNumber.trim().substring(newNumber.trim().length() - 10));
                        else
                            edtPhone.editText.setText(newNumber);
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getNumberFormatted(String number) {
        String newNumber = "";
        for (int i = 0; i < number.length(); i++) {
            if (Character.isDigit(number.charAt(i))) {
                newNumber = newNumber + number.charAt(i);
            }
        }
        if (newNumber.trim().length() > 10)
            return newNumber.trim().substring(newNumber.trim().length() - 10);
        else
            return newNumber;

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        if (progressBar != null)
            hideLoadingBar();
            //progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getString(R.string.msg_added_lead), Toast.LENGTH_LONG).show();

        LeafPreference.getInstance(InviteFriendActivity.this).setBoolean(LeafPreference.ISUSERDELETED1, true);
        AppLog.e(TAG,"ISUSERDELETED1 :"+true);

        edtLeadName.editText.setText("");
        edtPhone.editText.setText("");

        if(!isFromTeam)
        {
            DatabaseHandler databaseHandler = new DatabaseHandler(this);
            ArrayList<String> phoneList = databaseHandler.getPhone();
            for (int i = 0; i < phoneList.size(); i++) {
                if (phoneList.get(i).equals(getIntent().getExtras().getString("phone"))) {
                    databaseHandler.updateContact(getIntent().getExtras().getString("phone"));
                }
            }
        }



        InviteResponseSingle res = (InviteResponseSingle) response;
        AppLog.e(TAG,"InviteResponseSingle :"+new Gson().toJson(res));
        if(res.data!=null)
        {
            GruppieContactsModel list = GruppieContactsModel.getUserDetail(res.data.get(0).userId);

            if (list == null) {
                GruppieContactsModel model = new GruppieContactsModel();
                model.contact_id = res.data.get(0).userId;
                model.contact_name = res.data.get(0).name;
                model.contact_phone = res.data.get(0).phone;
                model.save();
            }

            List<AllContactModel> listAll = AllContactModel.getByGroup(res.data.get(0).userId + "");
            if (listAll.size() == 0) {
                AllContactModel model = new AllContactModel();
                model.group_id = groupId + "";
                model.all_id = res.data.get(0).userId + "";
                model.all_name = res.data.get(0).name;
                model.all_phone = res.data.get(0).phone;
                model.all_groups = "[" + GroupDashboardActivityNew.group_name + "]";
                model.all_group_ids = "[" + groupId + "]";
                model.save();
            }

            GruppieContactGroupIdModel idModel = new GruppieContactGroupIdModel();
            idModel.group_id = groupId + "";
            idModel.user_id = res.data.get(0).userId + "";
            idModel.save();

        }

        finish();

        try {
            String body = "I have added you to " + GroupDashboardActivityNew.group_name + ", to login plz download the " + getResources().getString(R.string.app_name) + " app from\n" +
                    "\n" +
                    "gruppie.in/android/"+getResources().getString(R.string.app_name).replace(" ","")+"\n" +
                    "gruppie.in/ios/"+getResources().getString(R.string.app_name).replace(" ","")+"\n" +
                    "Plz add your friends to this group";

            String data = request.phone;
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", data);
            smsIntent.putExtra("sms_body", body);
            startActivity(smsIntent);
        } catch (Exception e) {
            AppLog.e("Invite SMs", "error is " + e.toString());
        }

    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<AddFriendValidationError> error) {
        //hideLoadingDialog();
        if (progressBar != null)
            hideLoadingBar();
        //progressBar.setVisibility(View.GONE);

        AppLog.e("AddLeadActivity", "onFailure  ,, msg : " + error);
        if (error.status.equals("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        }
        else
        {
            if(error.errors==null)
                return;

            if (error.errors.get(0).phone != null) {
                edtPhone.editText.setError(error.errors.get(0).phone);
                edtPhone.editText.requestFocus();
            }
        }


    }


    @Override
    public void onException(int apiId, String msg) {
        //hideLoadingDialog();
        if (progressBar != null)
            hideLoadingBar();
        //progressBar.setVisibility(View.GONE);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void addPost() {
        AppLog.e("Invite Activity", "on Tick save");
        //addLead.performClick();
    }


    public class SMBDailogClickListener implements DialogInterface.OnClickListener {
        private int DIALOG_ID = -1;

        @Override
        public void onClick(DialogInterface dialog, int which) {

            ListView lw = ((AlertDialog) dialog).getListView();


            switch (DIALOG_ID) {

                case R.id.layout_country:
                    edtCountry.editText.setText(lw.getAdapter().getItem(lw.getCheckedItemPosition()).toString());
                    currentCountry = lw.getCheckedItemPosition() + 1;
                    break;

            }
        }

        public SMBDailogClickListener(int id) {
            DIALOG_ID = id;
        }
    }
}
