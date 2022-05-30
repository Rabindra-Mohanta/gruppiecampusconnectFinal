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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.adapters.DueDateAdapter;
import school.campusconnect.adapters.FeesDetailAdapter;
import school.campusconnect.adapters.TeamSubjectAdapter;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.GroupValidationError;
import school.campusconnect.datamodel.fees.DueDates;
import school.campusconnect.datamodel.fees.FeesDetailTemp;
import school.campusconnect.datamodel.fees.FeesRes;
import school.campusconnect.datamodel.marksheet.SubjectTeamResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;

public class AddFeesActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<GroupValidationError> {

    private static final String TAG = "CreateTeamActivity";
    @Bind(R.id.etName)
    EditText etName;

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

    @Bind(R.id.btnCreate)
    Button btnCreate;

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    LeafManager leafManager;

    private String groupId;
    private String teamId;

    FeesDetailAdapter feesAdapter = new FeesDetailAdapter(true);
    DueDateAdapter dueDateAdapter = new DueDateAdapter(true);
    FeesRes.Fees feesReq = new FeesRes.Fees();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fees);

        init();

      //  progressBar.setVisibility(View.VISIBLE);
        LeafManager leafManager = new LeafManager();
        leafManager.getFeesDetails(this, groupId, teamId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

      /*  if (isEdit) {
            getMenuInflater().inflate(R.menu.menu_edit, menu);
        }
*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      /*  if (item.getItemId() == R.id.menuDelete) {
            if (!isConnectionAvailable()) {
                showNoNetworkMsg();
                return true;
            }

            SMBDialogUtils.showSMBDialogOKCancel(this, "Are you sure you want to permanently delete this Subjects.?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressBar.setVisibility(View.VISIBLE);
                    leafManager.deleteSubjects(AddMarkCardActivity.this, GroupDashboardActivityNew.groupId, subjectData.getSubjectId());
                }
            });
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        ButterKnife.bind(this);

        rvFeesDetails.setAdapter(feesAdapter);
        rvDueDates.setAdapter(dueDateAdapter);

        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getIntent().getStringExtra("title"));
        leafManager = new LeafManager();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            groupId = bundle.getString("group_id", "");
            teamId = bundle.getString("team_id", "");
            AppLog.e(TAG, ",groupId:" + groupId + ",teamId:" + teamId);
        }

        imgAddFees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etFeesType.getText().toString().trim())) {
                    Toast.makeText(AddFeesActivity.this, getResources().getString(R.string.toast_enter_fees_type), Toast.LENGTH_SHORT).show();
                    etFeesType.requestFocus();
                } else if (TextUtils.isEmpty(etFeesTypeVal.getText().toString().trim())) {
                    etFeesTypeVal.requestFocus();
                    Toast.makeText(AddFeesActivity.this, getResources().getString(R.string.toast_enter_fees_amount), Toast.LENGTH_SHORT).show();
                } else {
                    feesAdapter.add(new FeesDetailTemp(etFeesType.getText().toString(), etFeesTypeVal.getText().toString()));
                    hide_keyboard(view);
                    etFeesType.setText("");
                    etFeesType.clearFocus();
                    etFeesTypeVal.setText("");
                    etFeesTypeVal.clearFocus();
                }
            }
        });

        imgAddDue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etDate.getText().toString().trim())) {
                    Toast.makeText(AddFeesActivity.this, getResources().getString(R.string.toast_select_due_date), Toast.LENGTH_SHORT).show();
                    etDate.requestFocus();
                } else if (TextUtils.isEmpty(etDateAmount.getText().toString().trim())) {
                    etDateAmount.requestFocus();
                    Toast.makeText(AddFeesActivity.this, getResources().getString(R.string.toast_enter_due_amount), Toast.LENGTH_SHORT).show();
                } else {
                    dueDateAdapter.add(new DueDates(etDate.getText().toString(), etDateAmount.getText().toString()));
                    hide_keyboard(view);
                    etDate.setText("");
                    etDateAmount.setText("");
                    etDateAmount.clearFocus();
                }
            }
        });


    }

    public void hide_keyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (view == null) {
            view = new View(this);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @OnClick({R.id.btnCreate, R.id.etDate})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCreate:
                if (isValid()) {

                    if (!TextUtils.isEmpty(etDate.getText().toString().trim()) &&
                            !TextUtils.isEmpty(etDateAmount.getText().toString().trim())) {

                    } else if (TextUtils.isEmpty(etDate.getText().toString().trim()) &&
                            TextUtils.isEmpty(etDateAmount.getText().toString().trim())) {

                    } else {
                        if (TextUtils.isEmpty(etDate.getText().toString().trim())) {
                            Toast.makeText(AddFeesActivity.this, getResources().getString(R.string.toast_select_due_date), Toast.LENGTH_SHORT).show();
                            etDate.requestFocus();
                            return;
                        } else if (TextUtils.isEmpty(etDateAmount.getText().toString().trim())) {
                            etDateAmount.requestFocus();
                            Toast.makeText(AddFeesActivity.this, getResources().getString(R.string.toast_enter_due_amount), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }


                    if (!isConnectionAvailable()) {
                        showNoNetworkMsg();
                        return;
                    }

                    feesReq.feeTitle = etName.getText().toString();
                    feesReq.totalFee = etTotalFees.getText().toString();

                    ArrayList<FeesDetailTemp> list = feesAdapter.getList();
                    feesReq.feeDetails = new HashMap<>();
                    for (int i = 0; i < list.size(); i++) {
                        if (TextUtils.isEmpty(list.get(i).getType())) {
                            Toast.makeText(this, getResources().getString(R.string.toast_please_enter_type), Toast.LENGTH_SHORT).show();
                            return;
                        } else if (TextUtils.isEmpty(list.get(i).getAmount())) {
                            Toast.makeText(this, getResources().getString(R.string.toast_please_enter_amount), Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            feesReq.feeDetails.put(list.get(i).getType(), list.get(i).getAmount());
                        }
                    }
                    if (!TextUtils.isEmpty(etFeesType.getText().toString().trim()) &&
                            !TextUtils.isEmpty(etFeesTypeVal.getText().toString().trim())) {
                        feesReq.feeDetails.put(etFeesType.getText().toString(), etFeesTypeVal.getText().toString());
                    }

                    feesReq.dueDates = dueDateAdapter.getList();
                    if (!TextUtils.isEmpty(etDate.getText().toString().trim()) &&
                            !TextUtils.isEmpty(etDateAmount.getText().toString().trim())) {
                        feesReq.dueDates.add(new DueDates(etDate.getText().toString(), etDateAmount.getText().toString()));
                    }

                    if(validateFees()){
                        showLoadingBar(progressBar);
                    //    progressBar.setVisibility(View.VISIBLE);
                        AppLog.e(TAG, "request :" + feesReq);
                        leafManager.createFees(this, GroupDashboardActivityNew.groupId, teamId, feesReq);
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
        }
    }

    private boolean validateFees() {
        if (feesReq.dueDates==null || feesReq.dueDates.size() == 0) {
            Toast.makeText(this,  getResources().getString(R.string.toast_add_at_least_one_due_date), Toast.LENGTH_SHORT).show();
            return false;
        }
        int total = Integer.parseInt(etTotalFees.getText().toString());

        int feedDetailAmount = 0;

        for (Map.Entry<String, String> stringStringEntry : feesReq.feeDetails.entrySet()) {
            feedDetailAmount = feedDetailAmount + Integer.parseInt(stringStringEntry.getValue());
        }

        if(feedDetailAmount>total){
            Toast.makeText(this, getResources().getString(R.string.toast_fees_details_amount_should_not_be_greater), Toast.LENGTH_SHORT).show();
            return false;
        }

        int dueFees = 0;
        for (int i=0;i<feesReq.dueDates.size();i++){
            dueFees = dueFees + Integer.parseInt(feesReq.dueDates.get(i).getMinimumAmount());
        }
        if(dueFees!=total){
            Toast.makeText(this, getResources().getString(R.string.toast_total_fees_and_due_date_amount_same), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public boolean isValid() {
        boolean valid = true;
       /* if (!isValueValidOnly(etName)) {
            Toast.makeText(this, "Please add Title", Toast.LENGTH_SHORT).show();
            valid = false;
        }else*/ if (!isValueValidOnly(etTotalFees)) {
            Toast.makeText(this, getResources().getString(R.string.toast_please_add_total_fees), Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        if (progressBar != null)
            hideLoadingBar();
          //  progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_FEES_CREATE:
                finish();
                break;
            case LeafManager.API_FEES_RES:
                FeesRes res = (FeesRes) response;
                if(res.getData()!=null && res.getData().size()>0){
                    feesReq = res.getData().get(0);
                    showData();
                }
                break;

        }
    }

    private void showData() {
        etName.setText(feesReq.feeTitle);
        etTotalFees.setText(feesReq.totalFee);
        feesAdapter.addAll(feesReq.feeDetails);
        dueDateAdapter.addList(feesReq.dueDates);
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<GroupValidationError> error) {
        if (progressBar != null)
            hideLoadingBar();
        //  progressBar.setVisibility(View.GONE);

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
    public void onException(int apiId, String msg) {
        super.onException(apiId, msg);
        if (progressBar != null)
            hideLoadingBar();
        //  progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }
}
