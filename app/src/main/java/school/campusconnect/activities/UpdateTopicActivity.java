package school.campusconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;

import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.ActivityUpdateTopicBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.syllabus.ChangeStatusPlanModel;
import school.campusconnect.datamodel.syllabus.SyllabusListModelRes;
import school.campusconnect.datamodel.syllabus.SyllabusPlanRequest;
import school.campusconnect.datamodel.syllabus.SyllabusTBL;
import school.campusconnect.datamodel.syllabus.TodaySyllabusPlanRes;
import school.campusconnect.fragments.DatePickerFragment;
import school.campusconnect.network.LeafManager;

public class UpdateTopicActivity extends BaseActivity implements LeafManager.OnCommunicationListener {

    public static final String TAG = "UpdateTopicActivity";

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    ActivityUpdateTopicBinding binding;
    TodaySyllabusPlanRes.TodaySyllabusData data;


    LeafManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_update_topic);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        inits();
        setTitle(data.getTopicName() +"( "+data.getChapterName()+" )");

    }

    private void inits() {

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            data = (TodaySyllabusPlanRes.TodaySyllabusData) getIntent().getSerializableExtra("data"); //Obtaining data
            Log.e(TAG,"data "+new Gson().toJson(data));
        }

        manager = new LeafManager();

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
                fragment.setTitle(R.string.lbl_from_date);
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
                fragment.setTitle(R.string.lbl_to_date);
            }
        });


        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.etFromDate.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_please_select_from_date),Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (binding.etToDate.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_please_select_to_date),Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                   /* SyllabusPlanRequest request = new SyllabusPlanRequest();

                    ArrayList<SyllabusPlanRequest.TopicData> list = new ArrayList<>();

                    SyllabusPlanRequest.TopicData topicData = new SyllabusPlanRequest.TopicData();
                    topicData.setTopicId(data.getTopicId());
                    topicData.setTopicName(data.getTopicName());
                    topicData.setActualStartDate(binding.etFromDate.getText().toString());
                    topicData.setActualEndDate(binding.etToDate.getText().toString());
                    list.add(topicData);


                    request.setTopicData(list);
                    Log.e(TAG,"req is Not Update"+new Gson().toJson(request));
*/

                    ChangeStatusPlanModel.ChangeStatusModelReq req = new ChangeStatusPlanModel.ChangeStatusModelReq();
                    req.setToDate(data.getToDate());
                    req.setFromDate(data.getFromDate());
                    req.setActualStartDate(binding.etFromDate.getText().toString());
                    req.setActualEndDate(binding.etToDate.getText().toString());

                    Log.e(TAG,"req is "+new Gson().toJson(req));

                    showLoadingBar(binding.progressBar,false);
                    manager.changeStatusPlan(UpdateTopicActivity.this,GroupDashboardActivityNew.groupId,data.getTeamId(),data.getSubjectId(),data.getChapterId(),req);
                }
            }
        });
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        if (apiId == LeafManager.API_CHANGE_STATUS_PLAN)
        {
            SyllabusTBL.deleteAll(data.getTeamId(),data.getSubjectId());
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_please_select_from_date),Toast.LENGTH_SHORT).show();
            finish();
        }
        super.onSuccess(apiId, response);
    }

    @Override
    public void onFailure(int apiId, String msg) {
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        super.onFailure(apiId, msg);
    }

    @Override
    public void onException(int apiId, String msg) {
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        super.onException(apiId, msg);
    }
}