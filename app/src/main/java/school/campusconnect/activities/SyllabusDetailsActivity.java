package school.campusconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import school.campusconnect.databinding.ActivitySyllabusDetailsBinding;
import school.campusconnect.databinding.ItemTopicDetailsBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.syllabus.ChangeStatusPlanModel;
import school.campusconnect.datamodel.syllabus.SyllabusListModelRes;
import school.campusconnect.fragments.DatePickerFragment;
import school.campusconnect.network.LeafManager;

public class SyllabusDetailsActivity extends BaseActivity {

    ActivitySyllabusDetailsBinding binding;

    SyllabusListModelRes.SyllabusData data;
    String teamID,subjectID;
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    TopicAdapter adapter;
    LeafManager manager;
public static final String TAG = "SyllabusDetailsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_syllabus_details);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        inits();
        setTitle(data.getChapterName());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_topic,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menuEditTopic:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void inits() {

        if ( getIntent().getExtras() != null) {
            data = (SyllabusListModelRes.SyllabusData) getIntent().getSerializableExtra("data");
            teamID = getIntent().getStringExtra("team_id");
            subjectID = getIntent().getStringExtra("subject_id");
            Log.e(TAG,"data "+new Gson().toJson(data));
        }

        manager = new LeafManager();

        Log.e(TAG,"data size "+data.getTopicData().size());

        adapter = new TopicAdapter(data.getTopicData());
        binding.rvTopic.setAdapter(adapter);


    }

    public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder>{

        ArrayList<SyllabusListModelRes.TopicData> topicData;
        Context context;

        public TopicAdapter(ArrayList<SyllabusListModelRes.TopicData> topicData) {
            this.topicData = topicData;
        }

        @NonNull
        @Override
        public TopicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            context = parent.getContext();
            ItemTopicDetailsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.item_topic_details,parent,false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull TopicAdapter.ViewHolder holder, int position) {
            SyllabusListModelRes.TopicData data = topicData.get(position);

            holder.binding.txtName.setText(data.getTopicName());

            if (GroupDashboardActivityNew.isPost)
            {
                holder.binding.imgEdit.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.binding.imgEdit.setVisibility(View.GONE);
            }

            if (data.getToDate() != null && !data.getToDate().isEmpty())
            {
                holder.binding.etToDate.setText(data.getToDate());
                holder.binding.etToDate.setEnabled(false);
                holder.binding.etToDate.setTextColor(getResources().getColor(R.color.grey));
            }
            else
            {
                holder.binding.etToDate.setEnabled(true);
                holder.binding.etToDate.setTextColor(getResources().getColor(R.color.white));
            }

            if (data.getFromDate() != null && !data.getFromDate().isEmpty())
            {
                holder.binding.etFromDate.setText(data.getFromDate());
                holder.binding.etFromDate.setEnabled(false);
                holder.binding.etFromDate.setTextColor(getResources().getColor(R.color.grey));
            }
            else
            {
                holder.binding.etFromDate.setEnabled(true);
                holder.binding.etFromDate.setTextColor(getResources().getColor(R.color.white));
            }

            if (data.getActualStartDate() != null && !data.getActualStartDate().isEmpty())
            {
                holder.binding.etActualToDate.setText(data.getActualStartDate());
                holder.binding.etActualToDate.setEnabled(false);
                holder.binding.etActualToDate.setTextColor(getResources().getColor(R.color.grey));
            }
            else
            {
                holder.binding.etActualToDate.setEnabled(true);
                holder.binding.etActualToDate.setTextColor(getResources().getColor(R.color.white));
            }

            if (data.getActualEndDate() != null && !data.getActualEndDate().isEmpty())
            {
                holder.binding.etActualFromDate.setText(data.getActualEndDate());
                holder.binding.etActualFromDate.setEnabled(false);
                holder.binding.etActualFromDate.setTextColor(getResources().getColor(R.color.grey));
            }
            else
            {
                holder.binding.etActualFromDate.setEnabled(true);
                holder.binding.etActualFromDate.setTextColor(getResources().getColor(R.color.white));
            }

            holder.binding.imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    holder.binding.etActualFromDate.setTextColor(getResources().getColor(R.color.white));
                    holder.binding.etActualToDate.setTextColor(getResources().getColor(R.color.white));
                    holder.binding.etFromDate.setTextColor(getResources().getColor(R.color.white));
                    holder.binding.etToDate.setTextColor(getResources().getColor(R.color.white));

                    holder.binding.etActualFromDate.setEnabled(true);
                    holder.binding.etActualToDate.setEnabled(true);
                    holder.binding.etFromDate.setEnabled(true);
                    holder.binding.etToDate.setEnabled(true);


                }
            });


            holder.binding.imgTree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (holder.binding.llExpand.getVisibility()==View.GONE)
                    {
                        holder.binding.llExpand.setVisibility(View.VISIBLE);
                        holder.binding.imgTree.setRotation(270);
                    }
                    else
                    {
                        holder.binding.llExpand.setVisibility(View.GONE);
                        holder.binding.imgTree.setRotation(90);
                    }
                }
            });

            holder.binding.etFromDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatePickerFragment fragment = DatePickerFragment.newInstance();
                    fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                        @Override
                        public void onDateSelected(Calendar c) {
                            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            holder.binding.etFromDate.setText(format.format(c.getTime()));
                        }
                    });
                    fragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "datepicker");
                }
            });

            holder.binding.etToDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatePickerFragment fragment = DatePickerFragment.newInstance();
                    fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                        @Override
                        public void onDateSelected(Calendar c) {
                            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            holder.binding.etToDate.setText(format.format(c.getTime()));
                        }
                    });
                    fragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "datepicker");
                }
            });




            holder.binding.etActualFromDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatePickerFragment fragment = DatePickerFragment.newInstance();
                    fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                        @Override
                        public void onDateSelected(Calendar c) {
                            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            holder.binding.etActualFromDate.setText(format.format(c.getTime()));
                        }
                    });
                    fragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "datepicker");
                }
            });

            holder.binding.etActualToDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatePickerFragment fragment = DatePickerFragment.newInstance();
                    fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                        @Override
                        public void onDateSelected(Calendar c) {
                            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            holder.binding.etActualToDate.setText(format.format(c.getTime()));
                        }
                    });
                    fragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "datepicker");
                }
            });


            holder.binding.btnDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTreeClick(holder.binding.etToDate.getText().toString(),holder.binding.etFromDate.getText().toString(),holder.binding.etActualToDate.getText().toString(),holder.binding.etActualFromDate.getText().toString(),data.getTopicId());
                }
            });
        }



        @Override
        public int getItemCount() {
            Log.e(TAG,"topicData"+topicData.size());
            return topicData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ItemTopicDetailsBinding binding;
            public ViewHolder(@NonNull ItemTopicDetailsBinding itemView) {
                super(itemView.getRoot());
                binding = itemView;
            }
        }
    }

    private void onTreeClick(String planto, String planfrom, String actualto, String actualfrom,String topicId) {

        ChangeStatusPlanModel.ChangeStatusModelReq req = new ChangeStatusPlanModel.ChangeStatusModelReq();
        req.setToDate(planto);
        req.setFromDate(planfrom);
        req.setActualStartDate(actualto);
        req.setActualEndDate(actualfrom);

        Log.e(TAG,"req "+new Gson().toJson(req));
        showLoadingBar(binding.progressBar);
        manager.changeStatusPlan(this,GroupDashboardActivityNew.groupId,teamID,subjectID,topicId,req);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
        if (apiId == LeafManager.API_CHANGE_STATUS_PLAN)
        {
          //  Toast.makeText(getApplicationContext(),"STATUS CHANGED",Toast.LENGTH_SHORT).show();
            LeafPreference.getInstance(getApplicationContext()).setBoolean(LeafPreference.ISSYLLABUSUPDATED, true);
            finish();
        }
        super.onSuccess(apiId, response);
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        super.onFailure(apiId, msg);
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        super.onException(apiId, msg);
    }
}