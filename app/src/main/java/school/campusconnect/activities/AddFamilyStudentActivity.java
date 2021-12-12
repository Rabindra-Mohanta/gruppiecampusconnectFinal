package school.campusconnect.activities;

import android.content.Context;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import school.campusconnect.datamodel.booths.BoothMemberReq;
import school.campusconnect.datamodel.family.FamilyMemberResponse;
import school.campusconnect.datamodel.subjects.SubjectStaffResponse;
import school.campusconnect.fragments.DatePickerFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;

public class AddFamilyStudentActivity extends BaseActivity {
    private static final String TAG = "AddStudentActivity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.rvSubjects)
    public RecyclerView rvSubjects;

    @Bind(R.id.etName)
    public EditText etName;

    @Bind(R.id.etPhone)
    public EditText etPhone;

    @Bind(R.id.etRelationShip)
    public EditText etRelationShip;
    @Bind(R.id.etAddress)
    public EditText etAddress;


    @Bind(R.id.etdob)
    public EditText etdob;

    @Bind(R.id.etGender)
    public Spinner etGender;

    @Bind(R.id.etBlood)
    public Spinner etBlood;

    @Bind(R.id.etVoterId)
    public EditText etVoterId;

    @Bind(R.id.etAadhar)
    public EditText etAadhar;

    @Bind(R.id.btnAdd)
    public Button btnAdd;

    @Bind(R.id.imgAdd)
    public ImageView imgAdd;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    LeafManager leafManager;

    FamilyMemberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_family_student);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle("Add/Update Family Member");

        init();
    }
    String[] bloodGrpArray;
    String[] genderArray;
    ArrayAdapter<String> genderAdapter;
    ArrayAdapter<String> bloodGrpAdapter;
    private void init() {
        leafManager = new LeafManager();

        ArrayList<FamilyMemberResponse.FamilyMemberData> list = new Gson().fromJson(getIntent().getStringExtra("data"), new TypeToken<ArrayList<FamilyMemberResponse.FamilyMemberData>>() {
        }.getType());

        bloodGrpArray = getResources().getStringArray(R.array.blood_group);
        genderArray = getResources().getStringArray(R.array.gender_array);

        genderAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, genderArray);
        etGender.setAdapter(genderAdapter);

        bloodGrpAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, bloodGrpArray);
        etBlood.setAdapter(bloodGrpAdapter);

        if(list.size()>1){
            adapter = new FamilyMemberAdapter(new ArrayList<>(list.subList(0, list.size() - 1)));
            setData(list.get(list.size()-1));
        }else {
            adapter = new FamilyMemberAdapter(new ArrayList<>());

            if(list.size()==1){
                FamilyMemberResponse.FamilyMemberData item = list.get(0);
                setData(item);
            }
        }

        rvSubjects.setAdapter(adapter);

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etName.getText().toString().trim())) {
                    Toast.makeText(AddFamilyStudentActivity.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(etRelationShip.getText().toString().trim())) {
                    Toast.makeText(AddFamilyStudentActivity.this, "Please Enter Relationship", Toast.LENGTH_SHORT).show();
                } else {
                    hide_keyboard(view);

                    FamilyMemberResponse.FamilyMemberData item =new FamilyMemberResponse.FamilyMemberData();
                    item.name = etName.getText().toString();
                    item.countryCode = "IN";
                    item.relationship = etRelationShip.getText().toString();
                    item.address = etAddress.getText().toString();
                    item.phone = etPhone.getText().toString();
                    item.dob = etdob.getText().toString();
                    if (etGender.getSelectedItemPosition() > 0) {
                        item.gender = etGender.getSelectedItem().toString();
                    } else {
                        item.gender = "";
                    }
                    if (etBlood.getSelectedItemPosition() > 0) {
                        item.bloodGroup = etBlood.getSelectedItem().toString();
                    } else {
                        item.bloodGroup = "";
                    }
                    item.voterId = etVoterId.getText().toString();
                    item.aadharNumber = etAadhar.getText().toString();

                    adapter.add(item);

                    etName.setText("");
                    etRelationShip.setText("");
                    etAddress.setText("");
                    etPhone.setText("");
                    etdob.setText("");
                    etVoterId.setText("");
                    etAadhar.setText("");
                    etGender.setSelection(0);
                    etBlood.setSelection(0);
                }
            }
        });


    }

    private void setData(FamilyMemberResponse.FamilyMemberData item) {

        etName.setText(item.name);
        etRelationShip.setText(item.relationship);
        etAddress.setText(item.address);
        etPhone.setText(item.phone);

        if("self".equalsIgnoreCase(item.relationship)){
            etRelationShip.setEnabled(false);
        }else {
            etRelationShip.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if("self".equalsIgnoreCase(etRelationShip.getText().toString())){
                        etRelationShip.setText("");
                    }
                }
            });
        }

        int index = 0;
        for (int i=0;i<genderArray.length;i++){
            if((item.gender+"").equals(genderArray[i])){
                index = i;
            }
        }
        etGender.setSelection(index);

        index = 0;
        for (int i=0;i<bloodGrpArray.length;i++){
            if((item.bloodGroup+"").equals(bloodGrpArray[i])){
                index = i;
            }
        }
        etBlood.setSelection(index);
    }

    private long lastClickTime = 0;

    @OnClick({R.id.btnAdd,R.id.etdob})
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

                if (TextUtils.isEmpty(etName.getText().toString().trim())) {
                    Toast.makeText(AddFamilyStudentActivity.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(etRelationShip.getText().toString().trim())) {
                    Toast.makeText(AddFamilyStudentActivity.this, "Please Enter Relationship", Toast.LENGTH_SHORT).show();
                } else {
                    FamilyMemberResponse.FamilyMemberData item =new FamilyMemberResponse.FamilyMemberData();
                    item.name = etName.getText().toString();
                    item.countryCode = "IN";
                    item.relationship = etRelationShip.getText().toString();
                    item.address = etAddress.getText().toString();
                    item.phone = etPhone.getText().toString();
                    item.dob = etdob.getText().toString();
                    if (etGender.getSelectedItemPosition() > 0) {
                        item.gender = etGender.getSelectedItem().toString();
                    } else {
                        item.gender = "";
                    }
                    if (etBlood.getSelectedItemPosition() > 0) {
                        item.bloodGroup = etBlood.getSelectedItem().toString();
                    } else {
                        item.bloodGroup = "";
                    }
                    item.voterId = etVoterId.getText().toString();
                    item.aadharNumber = etAadhar.getText().toString();

                    progressBar.setVisibility(View.VISIBLE);
                    FamilyMemberResponse req = new FamilyMemberResponse();
                    req.setData(adapter.getList());

                    req.getData().add(item);
                    AppLog.e(TAG,"req : "+req);
                    leafManager.addFamilyMember(this, GroupDashboardActivityNew.groupId, LeafPreference.getInstance(this).getUserId(),req);
                }
                break;

            case R.id.etdob:
                DatePickerFragment fragment = DatePickerFragment.newInstance();
                fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                    @Override
                    public void onDateSelected(Calendar c) {
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        etdob.setText(format.format(c.getTime()));
                    }
                });
                fragment.show(getSupportFragmentManager(), "datepicker");
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_CREATE_FAMILY_MEMBER:
                Toast.makeText(this, "Add Family Member successfully", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        super.onFailure(apiId, msg);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

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
            progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }


    public class FamilyMemberAdapter extends RecyclerView.Adapter<FamilyMemberAdapter.ViewHolder> {
        ArrayList<FamilyMemberResponse.FamilyMemberData> list = new ArrayList<>();

        FamilyMemberAdapter(ArrayList<FamilyMemberResponse.FamilyMemberData> familyMemberData) {
            list = familyMemberData;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_family, parent, false);
            return new ViewHolder(view);
        }

        public ArrayList<FamilyMemberResponse.FamilyMemberData> getList() {
            return this.list;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
            try {
                FamilyMemberResponse.FamilyMemberData item = list.get(pos);
                holder.etName.setText(item.name);
                holder.etRelationShip.setText(item.relationship);
                holder.etAddress.setText(item.address);
                holder.etPhone.setText(item.phone);


                if(!"self".equalsIgnoreCase(item.relationship)){
                    holder.imgDelete.setVisibility(View.VISIBLE);
                    holder.etRelationShip.setEnabled(true);
                    holder.etName.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            list.get(pos).name = holder.etName.getText().toString();
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
                            list.get(pos).phone = holder.etPhone.getText().toString();
                        }
                    });
                    holder.etRelationShip.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if("self".equalsIgnoreCase(holder.etRelationShip.getText().toString())){
                                holder.etRelationShip.setText("");
                            }
                        }
                    });
                    holder.etAddress.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            list.get(pos).address = holder.etAddress.getText().toString();
                        }
                    });
                    holder.etdob.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatePickerFragment fragment = DatePickerFragment.newInstance();
                            fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                                @Override
                                public void onDateSelected(Calendar c) {
                                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                    holder.etdob.setText(format.format(c.getTime()));
                                }
                            });
                            fragment.show(getSupportFragmentManager(), "datepicker");
                        }
                    });
                    holder.etAadhar.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            list.get(pos).aadharNumber = holder.etAadhar.getText().toString();
                        }
                    });
                    holder.etAddress.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            list.get(pos).address = holder.etAddress.getText().toString();
                        }
                    });
                    holder.etVoterId.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            list.get(pos).voterId = holder.etVoterId.getText().toString();
                        }
                    });


                    String[] bloodGrpArray = getResources().getStringArray(R.array.blood_group);
                    String[] genderArray = getResources().getStringArray(R.array.gender_array);

                    ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(AddFamilyStudentActivity.this, R.layout.item_spinner, R.id.tvItem, genderArray);
                    holder.etGender.setAdapter(genderAdapter);

                    int index = 0;
                    for (int i=0;i<genderArray.length;i++){
                        if((item.gender+"").equals(genderArray[i])){
                            index = i;
                        }
                    }
                    holder.etGender.setSelection(index);


                    ArrayAdapter<String> bloodGrpAdapter = new ArrayAdapter<String>(AddFamilyStudentActivity.this, R.layout.item_spinner, R.id.tvItem, bloodGrpArray);
                    holder.etBlood.setAdapter(bloodGrpAdapter);

                    index = 0;
                    for (int i=0;i<bloodGrpArray.length;i++){
                        if((item.bloodGroup+"").equals(bloodGrpArray[i])){
                            index = i;
                        }
                    }
                    holder.etBlood.setSelection(index);
                }else {
                    holder.imgDelete.setVisibility(View.GONE);
                    holder.etRelationShip.setEnabled(false);
                    holder.etRelationShip.addTextChangedListener(null);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void add(FamilyMemberResponse.FamilyMemberData item) {
            list.add(item);
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.etName)
            public EditText etName;

            @Bind(R.id.etPhone)
            public EditText etPhone;

            @Bind(R.id.etRelationShip)
            public EditText etRelationShip;
            @Bind(R.id.etAddress)
            public EditText etAddress;


            @Bind(R.id.etdob)
            public EditText etdob;

            @Bind(R.id.etGender)
            public Spinner etGender;

            @Bind(R.id.etBlood)
            public Spinner etBlood;

            @Bind(R.id.etVoterId)
            public EditText etVoterId;

            @Bind(R.id.etAadhar)
            public EditText etAadhar;

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
