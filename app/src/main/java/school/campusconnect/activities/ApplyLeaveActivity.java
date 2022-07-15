package school.campusconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.databinding.ActivityApplyLeaveBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.attendance_report.ApplyLeaveReq;
import school.campusconnect.fragments.DatePickerFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;

public class ApplyLeaveActivity extends BaseActivity implements LeafManager.OnCommunicationListener{

    public static final String TAG = "ApplyLeaveActivity";
    ActivityApplyLeaveBinding binding;

    String halfValue = null;

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    LeafManager manager;

    String teamId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_apply_leave);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_leave_request));

        bindData();

        inits();

    }

    private void inits() {

        teamId = getIntent().getStringExtra("teamID");

        manager = new LeafManager();
        binding.etDays.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                if (binding.etDays.getText().length() > 0)
                {
                    binding.tvSelectDay.setText(binding.etDays.getText().toString() + " Days ");

                    if (halfValue != null)
                    {
                        binding.tvSelectHalf.setText(" And " +binding.spHalf.getSelectedItem().toString());
                    }
                }
                else
                {
                    binding.tvSelectDay.setText("");

                    if (halfValue != null)
                    {
                        binding.tvSelectHalf.setText(" "+binding.spHalf.getSelectedItem().toString());
                    }

                }
            }
        });

        binding.etFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment fragment = DatePickerFragment.newInstance();
                fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                    @Override
                    public void onDateSelected(Calendar c) {
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        binding.etFromDate.setText(format.format(c.getTime()));
                    }
                });
                fragment.show(getSupportFragmentManager(), "datepicker");
            }
        });

        binding.etToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment fragment = DatePickerFragment.newInstance();
                fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                    @Override
                    public void onDateSelected(Calendar c) {
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        binding.etToDate.setText(format.format(c.getTime()));
                    }
                });
                fragment.show(getSupportFragmentManager(), "datepicker");
            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValid())
                {
                    ApplyLeaveReq req = new ApplyLeaveReq();

                    ApplyLeaveReq.LeaveData leaveData = new ApplyLeaveReq.LeaveData();
                    leaveData.setReason(binding.etReason.getText().toString());
                    leaveData.setFromDate(binding.etFromDate.getText().toString());
                    leaveData.setToDate(binding.etToDate.getText().toString());

                    if (!binding.etDays.getText().toString().isEmpty())
                    {
                        if (halfValue != null)
                        {
                            int value = Integer.parseInt(binding.etDays.getText().toString());
                            leaveData.setNoOfDays(String.valueOf(value+0.5));
                        }
                        else
                        {
                            leaveData.setNoOfDays(binding.etDays.getText().toString());
                        }
                    }
                    else
                    {
                        if (halfValue != null)
                        {
                            leaveData.setNoOfDays(String.valueOf(0.5));
                        }
                    }


                    req.setData(leaveData);

                    AppLog.e(TAG,"req for leave "+new Gson().toJson(req));

                    manager.applyForLeave(ApplyLeaveActivity.this,GroupDashboardActivityNew.groupId,teamId,req);

                }
            }
        });
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();

        if (apiId == LeafManager.API_APPLY_FOR_LEAVE)
        {
            Toast.makeText(getApplicationContext(),"Your Request is Send Successfully",Toast.LENGTH_SHORT).show();
            finish();
        }
        super.onSuccess(apiId, response);
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        AppLog.e(TAG,"on Fail "+msg);
        super.onFailure(apiId, msg);
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        AppLog.e(TAG,"on Exception "+msg);
        super.onException(apiId, msg);
    }

    private boolean isValid()
    {
        if (!isValueValid(binding.etFromDate))
        {
            return false;
        }
        else if (!isValueValid(binding.etToDate))
        {
            return false;
        }
        return true;
    }

    private void bindData() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_spinner_white,new String[]{"Select Half ","Half"});
        binding.spHalf.setAdapter(adapter);
        //    binding.spTeam.setSelection(strTeam.length-1);

        binding.spHalf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AppLog.e(TAG, "onItemSelected : " + position);

                if (position == 0)
                {
                    halfValue = null;
                    binding.tvSelectHalf.setText("");
                }
                else
                {
                    halfValue = binding.spHalf.getSelectedItem().toString();

                    if (binding.etDays.getText().toString().isEmpty() || binding.etDays.getText().toString().equalsIgnoreCase("0"))
                    {
                        binding.tvSelectHalf.setText(" "+binding.spHalf.getSelectedItem().toString());
                    }
                    else
                    {
                        binding.tvSelectHalf.setText(" And " +binding.spHalf.getSelectedItem().toString());
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}