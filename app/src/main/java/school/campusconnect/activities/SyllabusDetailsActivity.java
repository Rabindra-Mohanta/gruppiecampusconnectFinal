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

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.databinding.ActivitySyllabusDetailsBinding;
import school.campusconnect.databinding.ItemTopicDetailsBinding;
import school.campusconnect.datamodel.syllabus.SyllabusListModelRes;
import school.campusconnect.fragments.DatePickerFragment;

public class SyllabusDetailsActivity extends BaseActivity {

    ActivitySyllabusDetailsBinding binding;

    SyllabusListModelRes.SyllabusData data;

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    TopicAdapter adapter;

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
            Log.e(TAG,"data "+new Gson().toJson(data));
        }

        Log.e(TAG,"data size "+data.getTopicData().size());

        adapter = new TopicAdapter(data.getTopicData());
        binding.rvTopic.setAdapter(adapter);


    }

    public static class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder>{

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
                            SimpleDateFormat format = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
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
                            SimpleDateFormat format = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
                            holder.binding.etToDate.setText(format.format(c.getTime()));
                        }
                    });
                    fragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "datepicker");
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
}