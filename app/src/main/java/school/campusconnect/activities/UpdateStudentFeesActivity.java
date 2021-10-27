package school.campusconnect.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.googlecode.mp4parser.authoring.Edit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.adapters.DueDateAdapter;
import school.campusconnect.adapters.FeesDetailAdapter;
import school.campusconnect.adapters.PaidDateAdapter;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.GroupValidationError;
import school.campusconnect.datamodel.fees.DueDates;
import school.campusconnect.datamodel.fees.FeePaidDetails;
import school.campusconnect.datamodel.fees.FeesDetailTemp;
import school.campusconnect.datamodel.fees.FeesRes;
import school.campusconnect.datamodel.fees.StudentFeesRes;
import school.campusconnect.datamodel.fees.UpdateStudentFees;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;

public class UpdateStudentFeesActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<GroupValidationError> {

    private static final String TAG = "CreateTeamActivity";

    @Bind(R.id.etTotalFees)
    EditText etTotalFees;

    @Bind(R.id.rvFeesDetails)
    RecyclerView rvFeesDetails;

    @Bind(R.id.etFeesType)
    EditText etFeesType;

    @Bind(R.id.etFeesTypeVal)
    EditText etFeesTypeVal;

    @Bind(R.id.imgAddFees)
    ImageView imgAddFees;

    @Bind(R.id.rvDueDates)
    RecyclerView rvDueDates;

    @Bind(R.id.etDate)
    EditText etDate;

    @Bind(R.id.etDateAmount)
    EditText etDateAmount;

    @Bind(R.id.imgAddDue)
    ImageView imgAddDue;

    @Bind(R.id.rvPaid)
    RecyclerView rvPaid;

    @Bind(R.id.etDatePaid)
    EditText etDatePaid;

    @Bind(R.id.etPaidAmount)
    EditText etPaidAmount;

    @Bind(R.id.imgAddPaid)
    ImageView imgAddPaid;


    @Bind(R.id.etTotalBalance)
    EditText etTotalBalance;

    @Bind(R.id.etTotalPaid)
    EditText etTotalPaid;


    @Bind(R.id.btnCreate)
    Button btnCreate;

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    LeafManager leafManager;

    private String groupId;
    private String teamId;
    private String role;
    private String title;

    FeesDetailAdapter feesAdapter = new FeesDetailAdapter(true);
    DueDateAdapter dueDateAdapter ;
    PaidDateAdapter paidDateAdapter = new PaidDateAdapter();
    StudentFeesRes.StudentFees studentFees;
    UpdateStudentFees updateStudentFees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fees_student_update);

        init();

        showData();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if ("admin".equalsIgnoreCase(role)) {
            getMenuInflater().inflate(R.menu.menu_fees_edit, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuEdit) {
            Intent intent = new Intent(this, EditStudentFeesActivity.class);
            intent.putExtra("group_id", groupId);
            intent.putExtra("team_id", teamId);
            intent.putExtra("title", title);
            intent.putExtra("role", role);
            intent.putExtra("StudentFees", new Gson().toJson(studentFees));
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        ButterKnife.bind(this);

        rvFeesDetails.setAdapter(feesAdapter);

        rvPaid.setAdapter(paidDateAdapter);

        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        title=getIntent().getStringExtra("title");
        setTitle(title);
        leafManager = new LeafManager();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            groupId = bundle.getString("group_id", "");
            teamId = bundle.getString("team_id", "");
            role = bundle.getString("role", "");
            AppLog.e(TAG, ",groupId:" + groupId + ",teamId:" + teamId+", role : "+role);

            studentFees = new Gson().fromJson(getIntent().getStringExtra("StudentFees"), StudentFeesRes.StudentFees.class);
        }

        dueDateAdapter = new DueDateAdapter(role,true,false);
        rvDueDates.setAdapter(dueDateAdapter);

        imgAddFees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etFeesType.getText().toString().trim())) {
                    Toast.makeText(UpdateStudentFeesActivity.this, "Please Enter Fees Type", Toast.LENGTH_SHORT).show();
                    etFeesType.requestFocus();
                } else if (TextUtils.isEmpty(etFeesTypeVal.getText().toString().trim())) {
                    etFeesTypeVal.requestFocus();
                    Toast.makeText(UpdateStudentFeesActivity.this, "Please Enter Fees Amount", Toast.LENGTH_SHORT).show();
                } else {
                    feesAdapter.add(new FeesDetailTemp(etFeesType.getText().toString(), etFeesTypeVal.getText().toString()));
                    hide_keyboard(view);
                    etFeesType.setText("");
                    etFeesTypeVal.setText("");
                    etFeesTypeVal.clearFocus();
                }
            }
        });

        imgAddDue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etDate.getText().toString().trim())) {
                    Toast.makeText(UpdateStudentFeesActivity.this, "Please Select Due Date", Toast.LENGTH_SHORT).show();
                    etDate.requestFocus();
                } else if (TextUtils.isEmpty(etDateAmount.getText().toString().trim())) {
                    etDateAmount.requestFocus();
                    Toast.makeText(UpdateStudentFeesActivity.this, "Please Enter Due Amount", Toast.LENGTH_SHORT).show();
                } else {
                    dueDateAdapter.add(new DueDates(etDate.getText().toString(), etDateAmount.getText().toString()));
                    hide_keyboard(view);
                    etDate.setText("");
                    etDateAmount.setText("");
                    etDateAmount.clearFocus();
                }
            }
        });

        imgAddPaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etDatePaid.getText().toString().trim())) {
                    Toast.makeText(UpdateStudentFeesActivity.this, "Please Select Paid Date", Toast.LENGTH_SHORT).show();
                    etDatePaid.requestFocus();
                } else if (TextUtils.isEmpty(etPaidAmount.getText().toString().trim())) {
                    etPaidAmount.requestFocus();
                    Toast.makeText(UpdateStudentFeesActivity.this, "Please Enter Paid Amount", Toast.LENGTH_SHORT).show();
                } else {
                    paidDateAdapter.add(new FeePaidDetails(etDatePaid.getText().toString(), etPaidAmount.getText().toString()));
                    hide_keyboard(view);
                    etDatePaid.setText("");
                    etPaidAmount.setText("");
                    etPaidAmount.clearFocus();
                }
            }
        });

        if(!"admin".equalsIgnoreCase(role)){
            etTotalFees.setEnabled(false);
            findViewById(R.id.llFees).setVisibility(View.GONE);
            findViewById(R.id.llDue).setVisibility(View.GONE);
            findViewById(R.id.llPaid).setVisibility(View.GONE);
            findViewById(R.id.btnCreate).setVisibility(View.INVISIBLE);
        }

    }

    public void hide_keyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (view == null) {
            view = new View(this);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @OnClick({R.id.btnCreate, R.id.etDate,R.id.etDatePaid})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCreate:
                if (isValid()) {

                    if (!TextUtils.isEmpty(etDatePaid.getText().toString().trim()) &&
                            !TextUtils.isEmpty(etPaidAmount.getText().toString().trim())) {

                    } else if (TextUtils.isEmpty(etDatePaid.getText().toString().trim()) &&
                            TextUtils.isEmpty(etPaidAmount.getText().toString().trim())) {

                    } else {
                        if (TextUtils.isEmpty(etDatePaid.getText().toString().trim())) {
                            Toast.makeText(UpdateStudentFeesActivity.this, "Please Select Paid Date", Toast.LENGTH_SHORT).show();
                            etDatePaid.requestFocus();
                            return;
                        } else if (TextUtils.isEmpty(etPaidAmount.getText().toString().trim())) {
                            etPaidAmount.requestFocus();
                            Toast.makeText(UpdateStudentFeesActivity.this, "Please Enter Due Paid Amount", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }


                    if (!isConnectionAvailable()) {
                        showNoNetworkMsg();
                        return;
                    }

                    updateStudentFees = new UpdateStudentFees();
                    updateStudentFees.totalFee = etTotalFees.getText().toString();

                    ArrayList<FeesDetailTemp> list = feesAdapter.getList();
                    updateStudentFees.feeDetails = new HashMap<>();
                    for (int i = 0; i < list.size(); i++) {
                        if (TextUtils.isEmpty(list.get(i).getType())) {
                            Toast.makeText(this, "Please Enter Type", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (TextUtils.isEmpty(list.get(i).getAmount())) {
                            Toast.makeText(this, "Please Enter Amount", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            updateStudentFees.feeDetails.put(list.get(i).getType(), list.get(i).getAmount());
                        }
                    }
                    if (!TextUtils.isEmpty(etFeesType.getText().toString().trim()) &&
                            !TextUtils.isEmpty(etFeesTypeVal.getText().toString().trim())) {
                        updateStudentFees.feeDetails.put(etFeesType.getText().toString(), etFeesTypeVal.getText().toString());
                    }

                    updateStudentFees.dueDates = dueDateAdapter.getList();
                    if (!TextUtils.isEmpty(etDate.getText().toString().trim()) &&
                            !TextUtils.isEmpty(etDateAmount.getText().toString().trim())) {
                        updateStudentFees.dueDates.add(new DueDates(etDate.getText().toString(), etDateAmount.getText().toString()));
                    }

                    updateStudentFees.paidDates = paidDateAdapter.getList();
                    if (!TextUtils.isEmpty(etDatePaid.getText().toString().trim()) &&
                            !TextUtils.isEmpty(etPaidAmount.getText().toString().trim())) {
                        updateStudentFees.paidDates.add(new FeePaidDetails(etDatePaid.getText().toString(), etPaidAmount.getText().toString()));
                    }

                    if (validateFees()) {
                        progressBar.setVisibility(View.VISIBLE);
                        AppLog.e(TAG, "request :" + updateStudentFees);
                        leafManager.addStudentPaidFees(this, GroupDashboardActivityNew.groupId, teamId, studentFees.studentDbId, updateStudentFees);
                    }
                }
                break;
            case R.id.etDate: {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog fragment = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        etDate.setText(format.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                fragment.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                fragment.show();
                break;
            }

            case R.id.etDatePaid: {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog fragment = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        etDatePaid.setText(format.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
//                fragment.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                fragment.show();
                break;
            }

        }
    }

        private boolean validateFees () {

            int total = Integer.parseInt(etTotalFees.getText().toString());

            int feedDetailAmount = 0;

            for (Map.Entry<String, String> stringStringEntry : updateStudentFees.feeDetails.entrySet()) {
                feedDetailAmount = feedDetailAmount + Integer.parseInt(stringStringEntry.getValue());
            }

            if (feedDetailAmount > total) {
                Toast.makeText(this, "Fees Detail Amount should not be greater than total fees amount", Toast.LENGTH_SHORT).show();
                return false;
            }

            if(updateStudentFees.paidDates!=null){
                int paidFees = 0;
                for (int i = 0; i < updateStudentFees.paidDates.size(); i++) {
                    paidFees = paidFees + Integer.parseInt(updateStudentFees.paidDates.get(i).getAmountPaid());
                }
                if (paidFees > total) {
                    Toast.makeText(this, "Paid Amount should not more than Total Fees", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            return true;
        }

        public boolean isValid () {
            boolean valid = true;
            if (!isValueValidOnly(etTotalFees)) {
                Toast.makeText(this, "Please add total fees", Toast.LENGTH_SHORT).show();
                valid = false;
            }
            return valid;
        }

        @Override
        public void onSuccess ( int apiId, BaseResponse response){
            super.onSuccess(apiId, response);
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);

            switch (apiId) {
                case LeafManager.API_STUDENT_FEES_ADD:
                    finish();
                    break;

            }
        }

        private void showData () {
            etTotalBalance.setText(TextUtils.isEmpty(studentFees.totalBalanceAmount)?"0":studentFees.totalBalanceAmount);
            etTotalPaid.setText(TextUtils.isEmpty(studentFees.totalAmountPaid)?"0":studentFees.totalAmountPaid);
            etTotalFees.setText(studentFees.totalFee);
            feesAdapter.addAll(studentFees.feeDetails);
            dueDateAdapter.addList(studentFees.dueDates);
            paidDateAdapter.addList(studentFees.feePaidDetails);
        }

        @Override
        public void onFailure ( int apiId, ErrorResponseModel<GroupValidationError > error){
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);

            if (error.status.equals("401")) {
                Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
                logout();
            } else {
                if (apiId == LeafManager.API_SUBJECTS_DELETE) {
                    GroupValidationError groupValidationError = (GroupValidationError) error;
                    Toast.makeText(this, groupValidationError.message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, error.title, Toast.LENGTH_SHORT).show();
                }


            }
        }

        @Override
        public void onException ( int apiId, String msg){
            super.onException(apiId, msg);
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);
            Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
        }
    }
