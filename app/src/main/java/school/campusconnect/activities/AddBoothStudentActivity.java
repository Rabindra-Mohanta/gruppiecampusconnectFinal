package school.campusconnect.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.LeadItem;
import school.campusconnect.datamodel.LeadResponse;
import school.campusconnect.datamodel.booths.BoothMemberReq;
import school.campusconnect.datamodel.committee.committeeResponse;
import school.campusconnect.datamodel.lead.LeadDataTBL;
import school.campusconnect.datamodel.student.StudentRes;
import school.campusconnect.datamodel.test_exam.TestOfflineSubjectMark;
import school.campusconnect.fragments.DatePickerFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.UploadImageFragment;
import school.campusconnect.views.SMBDialogUtils;

public class AddBoothStudentActivity extends BaseActivity {
    private static final String TAG = "AddBoothStudentActivity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.rvSubjects)
    public RecyclerView rvSubjects;

    @Bind(R.id.etName)
    public EditText etName;

    @Bind(R.id.etPhone)
    public EditText etPhone;

    @Bind(R.id.btnAdd)
    public Button btnAdd;

    @Bind(R.id.imgAdd)
    public ImageView imgAdd;

    @Bind(R.id.importFromContact)
    public Button importFromContact;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    LeafManager leafManager;

    String group_id, team_id, category;
    committeeResponse.committeeData committeeData;
    ContactsAdapter adapter;
    ArrayList<String> mobileList = new ArrayList<>();
    private ArrayList<LeadItem> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_booth_student);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.title_add_)+" "+ getResources().getString(R.string.title_booth_member));
        init();
    }
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(isEdit)
        {
            getMenuInflater().inflate(R.menu.menu_edit,menu);
        }

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuDelete) {
            if (!isConnectionAvailable()) {
                showNoNetworkMsg();
                return true;
            }
            if (studentData == null)
                return true;

            SMBDialogUtils.showSMBDialogOKCancel(this, "Are you sure you want to permanently delete this student.?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressBar.setVisibility(View.VISIBLE);
                    leafManager.deleteClassStudent(AddBoothStudentActivity.this, GroupDashboardActivityNew.groupId, team_id,studentData.getUserId());
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/

    private void init() {


        leafManager = new LeafManager();
        group_id = getIntent().getStringExtra("group_id");
        team_id = getIntent().getStringExtra("team_id");
        category = getIntent().getStringExtra("category");
        Log.e(TAG,"category "+category);
        committeeData = new Gson().fromJson(getIntent().getStringExtra("committee_data"), committeeResponse.committeeData.class);
        Log.e(TAG,"committee Data: "+new Gson().toJson(committeeData));
     //   mobileList = getIntent().getStringArrayListExtra("mobileList");
        adapter = new ContactsAdapter();
        rvSubjects.setAdapter(adapter);

        Log.e(TAG,"mobileList "+mobileList.size());

        List<LeadDataTBL> leadDataTBL = LeadDataTBL.getLeadData(group_id,team_id);

        Log.e(TAG,"leadDataTBLList"+leadDataTBL.size());

        if (leadDataTBL.size() == 0)
        {
            if (isConnectionAvailable()) {
                showLoadingBar(progressBar,true);
                leafManager.getTeamMember(this, group_id + "", team_id + "",false);
            } else {
                showNoNetworkMsg();
            }
        }
        else
        {
            if (leadDataTBL.size() > 0)
            {
                list = new ArrayList<>();
                for (int i = 0;i<leadDataTBL.size();i++)
                {
                    LeadItem leadItem = new LeadItem();
                    leadItem.id = leadDataTBL.get(i).id;
                    leadItem.voterId = leadDataTBL.get(i).voterId;
                    leadItem.roleOnConstituency = leadDataTBL.get(i).roleOnConstituency;
                    leadItem.phone = leadDataTBL.get(i).phone;
                    leadItem.name = leadDataTBL.get(i).name;
                    leadItem.image = leadDataTBL.get(i).image;
                    leadItem.gender = leadDataTBL.get(i).gender;
                    leadItem.dob = leadDataTBL.get(i).dob;
                    leadItem.bloodGroup = leadDataTBL.get(i).bloodGroup;
                    leadItem.allowedToAddUser = leadDataTBL.get(i).allowedToAddUser;
                    leadItem.allowedToAddTeamPostComment = leadDataTBL.get(i).allowedToAddTeamPostComment;
                    leadItem.allowedToAddTeamPost = leadDataTBL.get(i).allowedToAddTeamPost;
                    leadItem.aadharNumber = leadDataTBL.get(i).aadharNumber;
                    leadItem.pushTokens = new Gson().fromJson(leadDataTBL.get(i).pushToken,new TypeToken<ArrayList<LeadItem.pushTokenData>>() {}.getType());
                    leadItem.isLive = leadDataTBL.get(i).isLive;

                    list.add(leadItem);
                }
            }

            for (int i = 0;i<list.size();i++)
            {
                if (list.get(i).phone != null && !list.get(i).phone.isEmpty())
                    mobileList.add(list.get(i).phone);
            }
        }
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etName.getText().toString().trim())) {
                    Toast.makeText(AddBoothStudentActivity.this, getResources().getString(R.string.toast_enter_name), Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(etPhone.getText().toString().trim())) {
                    Toast.makeText(AddBoothStudentActivity.this, getResources().getString(R.string.toast_enter_phone), Toast.LENGTH_SHORT).show();
                }
                else if (etPhone.getText().toString().length() < 10) {
                    Toast.makeText(AddBoothStudentActivity.this, getResources().getString(R.string.toast_valid_phone), Toast.LENGTH_SHORT).show();
                }else {
                    hide_keyboard(view);
                    String str = etName.getText().toString() + ",IN," + etPhone.getText().toString();
                    adapter.add(str);
                    etName.setText("");
                    etName.requestFocus();
                    etPhone.setText("");
                }
            }
        });
        importFromContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectContact();
            }
        });
    }

    private void selectContact() {
//        Uri uri = Uri.parse("content://contacts");
//        Intent intent = new Intent(Intent.ACTION_PICK, uri);
//        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
//        startActivityForResult(intent, 12);
            Intent intent = new Intent(this,SelectContactActivity.class);
            intent.putExtra("mobileList",mobileList);
            startActivityForResult(intent,119);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

            Cursor cursor = getContentResolver().query(uri, projection,
                    null, null, null);
            cursor.moveToFirst();

            int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String number = cursor.getString(numberColumnIndex);

            int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            String name = cursor.getString(nameColumnIndex);

            Log.d(TAG, "ZZZ number : " + number + " , name : " + name);
            adapter.add(name + ",IN," + number.replace(" ", "").replace("+91", ""));
        }
        if(requestCode==119 && resultCode== EBookActivity.RESULT_OK && data!=null){
            adapter.addAll(data.getStringArrayListExtra("mobileList"));
        }
    }

    private long lastClickTime = 0;

    @OnClick({R.id.btnAdd})
    public void onClick(View view) {
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime - lastClickTime > 1000) {
            lastClickTime = currentTime;
        } else {
            return;
        }
        Log.e(TAG, "Tap : ");
        switch (view.getId()) {
            case R.id.btnAdd:
                hide_keyboard(view);
                String str = "";
                if (!TextUtils.isEmpty(etName.getText().toString().trim()) && !TextUtils.isEmpty(etPhone.getText().toString().trim())) {
                    str = etName.getText().toString() + ",IN," + etPhone.getText().toString();
                }
                else if (TextUtils.isEmpty(etName.getText().toString().trim()) && TextUtils.isEmpty(etPhone.getText().toString().trim())) {

                }
                else {
                    if (TextUtils.isEmpty(etName.getText().toString().trim())) {
                        Toast.makeText(AddBoothStudentActivity.this, getResources().getString(R.string.toast_enter_name), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (TextUtils.isEmpty(etPhone.getText().toString().trim())) {
                        Toast.makeText(AddBoothStudentActivity.this, getResources().getString(R.string.toast_enter_phone), Toast.LENGTH_SHORT).show();
                        return;
                    }

                }

                if(!TextUtils.isEmpty(str) || adapter.getList().size()>0){

                    if (etPhone.getText().toString().length() < 10) {
                        Toast.makeText(AddBoothStudentActivity.this, getResources().getString(R.string.toast_valid_phone), Toast.LENGTH_SHORT).show();
                        return;
                    }


                    BoothMemberReq req = new BoothMemberReq();
                    req.user = adapter.getList();

                    if(!TextUtils.isEmpty(str)){
                        req.user.add(str);
                    }


                    if (committeeData != null)
                    {
                        req.dafaultCommittee = committeeData.getDefaultCommittee();
                        req.committeeId = committeeData.getCommitteeId();
                    }
                    showLoadingBar(progressBar,false);
                //    progressBar.setVisibility(View.VISIBLE);
                    leafManager.addBoothsMember(this, group_id, team_id, category, req);
                }else {
                    Toast.makeText(AddBoothStudentActivity.this, getResources().getString(R.string.toast_add_one_contact), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);

        if (progressBar != null)
            hideLoadingBar();
           // progressBar.setVisibility(View.GONE);

        switch (apiId) {

            case LeafManager.API_ADD_BOOTH_MEMEBER:
                hide_keyboard();

                LeafPreference.getInstance(AddBoothStudentActivity.this).setBoolean(LeafPreference.ADD_FRIEND, true);
                Toast.makeText(this, getResources().getString(R.string.toast_add_member_successfully), Toast.LENGTH_SHORT).show();
                finish();
                break;

            case LeafManager.API_ID_LEAD_LIST:
                LeadResponse res = (LeadResponse) response;
                list.addAll(res.getResults());

                for (int i = 0;i<list.size();i++)
                {
                    if (list.get(i).phone != null && !list.get(i).phone.isEmpty())
                        mobileList.add(list.get(i).phone);
                }
                break;
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        super.onFailure(apiId, msg);
        if (progressBar != null)
            hideLoadingBar();
        // progressBar.setVisibility(View.GONE);

        if (msg.contains("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onException(int apiId, String msg) {
        super.onException(apiId, msg);
        if (progressBar != null)
            hideLoadingBar();
        // progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }


    public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
        private Context mContext;
        ArrayList<String> list = new ArrayList<>();

        ContactsAdapter() {
        }

        @NonNull
        @Override
        public ContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_member, parent, false);
            return new ContactsAdapter.ViewHolder(view);
        }

        public ArrayList<String> getList() {
            return this.list;
        }

        @Override
        public void onBindViewHolder(@NonNull ContactsAdapter.ViewHolder holder, int pos) {
            String[] item = list.get(pos).split(",");
            holder.etName.setText(item[0]);
            holder.etPhone.setText(item[2]);

            holder.etName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    item[0] = s.toString();
                    list.set(pos, item[0] + "," + item[1] + "," + item[2]);
                }
            });

            holder.etPhone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    item[2] = s.toString();
                    list.set(pos, item[0] + "," + item[1] + "," + item[2]);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void add(String item) {
            list.add(item);
            notifyDataSetChanged();
        }

        public void addAll(ArrayList<String> mobileList) {
            list.addAll(mobileList);
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.etName)
            EditText etName;

            @Bind(R.id.etPhone)
            EditText etPhone;

            @Bind(R.id.imgDelete)
            ImageView imgDelete;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                imgDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list.remove(getAdapterPosition());
                        notifyDataSetChanged();
                    }
                });

            }
        }
    }
}
