package school.campusconnect.activities;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.databinding.ActivityStaffClassListBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.syllabus.StaffAnalysisRes;
import school.campusconnect.datamodel.syllabus.TodaySyllabusPlanRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class StaffClassListActivity extends BaseActivity implements LeafManager.OnCommunicationListener{

    public static final String TAG = "StaffClassListActivity";
    ActivityStaffClassListBinding binding;
    String staffID;
    String date;
    private Boolean expandable = false;
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    TodayTopicsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_staff_class_list);
        staffID = getIntent().getStringExtra("staff_id");
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getIntent().getStringExtra("title"));
        inits();
    }

    private void inits() {

        date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        Log.e(TAG,"date"+date);

        binding.imgDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExpandable();
            }
        });


        LeafManager leafManager = new LeafManager();
        showLoadingBar(binding.progressBar);
        leafManager.getClassesOfStaff(this,GroupDashboardActivityNew.groupId,staffID);
        leafManager.getTodaySyllabusPlanList(this,GroupDashboardActivityNew.groupId,date);
        leafManager.getStaffAnalysis(this,GroupDashboardActivityNew.groupId,staffID);
    }

    private void setExpandable() {

        if (expandable)
        {
            expandable = false;
            adapter.isExpand(expandable);
            binding.imgDropdown.setRotation(360);
        }else
        {
            expandable = true;
            adapter.isExpand(expandable);
            binding.imgDropdown.setRotation(180);
        }

    }

    private void setData(StaffAnalysisRes.StaffAnalysisData staffAnalysisData) {

        /*bar graph*/

        Log.e(TAG,"set data call");

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0.5f, 2));
        entries.add(new BarEntry(1f, 1));
        entries.add(new BarEntry(1.5f, 3));


        BarDataSet dataset = new BarDataSet(entries, "");

        dataset.setColors(new int[] {Color.YELLOW, Color.GREEN, Color.RED});

        binding.chart1.getXAxis().setDrawGridLines(false);
        binding.chart1.getAxisLeft().setDrawGridLines(false);
        binding.chart1.getAxisRight().setDrawGridLines(false);

        binding.chart1.getLegend().setEnabled(false);


        XAxis xAxis = binding.chart1.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


        YAxis yAxis = binding.chart1.getAxisRight();
        yAxis.setEnabled(false);
        yAxis.setDrawGridLines(false);

        binding.chart1.setDescription(null);

       /* ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("Sun");
        xAxisLabel.add("Mon");
        xAxisLabel.add("Tue");

        XAxis xAxis = binding.chart1.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        ValueFormatter formatter = new ValueFormatter() {

            @Override
            public String getFormattedValue(float value) {
                return xAxisLabel.get((int) value);
            }
        };

        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);
        xAxis.setTextSize(5f);
        xAxis.setTextColor(Color.RED);*/

        BarData data = new BarData(dataset);

        data.setBarWidth(0.4f);

        binding.chart1.setTouchEnabled(false);
        binding.chart1.setDragEnabled(false);
        binding.chart1.setScaleEnabled(false);
        binding.chart1.setFitBars(true);
        binding.chart1.setBackground(null);


        binding.chart1.setData(data);
        binding.chart1.invalidate();

        /*pie graph*/

        binding.pieChart.setUsePercentValues(true);
        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.setDragDecelerationFrictionCoef(0.9f);
        binding.pieChart.setDrawCenterText(false);
        binding.pieChart.setDrawHoleEnabled(false);

        binding.pieChart.getLegend().setEnabled(false);

        ArrayList<PieEntry> yValues = new ArrayList<>();
        yValues.add(new PieEntry(34f));
        yValues.add(new PieEntry(56f));
        yValues.add(new PieEntry(66f));

        binding.pieChart.setDrawEntryLabels(false);

        PieDataSet dataSet = new PieDataSet(yValues, "");

        dataSet.setColors(Color.YELLOW, Color.GREEN, Color.RED);
        PieData pieData = new PieData((dataSet));

        binding.pieChart.setData(pieData);
        binding.pieChart.invalidate();


    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        hideLoadingBar();

        if (apiId == LeafManager.API_CLASS_OF_STAFF)
        {

            ClassResponse res = (ClassResponse) response;
            List<ClassResponse.ClassData> result = res.getData();
            binding.rvClass.setAdapter(new ClassesAdapter(result));
        }

        if (apiId == LeafManager.API_TODAY_DATE_WISE_SYLLBUS_PLAN)
        {
            TodaySyllabusPlanRes res = (TodaySyllabusPlanRes) response;


            if (res.getTodaySyllabusData().size() > 0)
            {
                adapter = new  TodayTopicsAdapter(res.getTodaySyllabusData());
                binding.rvClass.setAdapter(adapter);
            }
            else
            {
                binding.llTodayData.setVisibility(View.GONE);
            }

        }

        if (apiId == LeafManager.API_STAFF_ANALYSIS)
        {
            StaffAnalysisRes res = (StaffAnalysisRes) response;

            if (res.getData().size() > 0)
            {
                setData(res.getData().get(0));
            }

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

    public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder>
    {
        List<ClassResponse.ClassData> list;
        private Context mContext;

        public ClassesAdapter(List<ClassResponse.ClassData> list) {
            this.list = list;
        }

        @Override
        public ClassesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class,parent,false);
            return new ClassesAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ClassesAdapter.ViewHolder holder, final int position) {
            final ClassResponse.ClassData item = list.get(position);

            if (!TextUtils.isEmpty(item.getImage())) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(50,50).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgTeam,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.img_lead_default.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(50,50).into(holder.imgTeam, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.img_lead_default.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.img_lead_default.setVisibility(View.VISIBLE);
                                        TextDrawable drawable = TextDrawable.builder()
                                                .buildRound(ImageUtil.getTextLetter(item.getName()), ImageUtil.getRandomColor(position));
                                        holder.img_lead_default.setImageDrawable(drawable);
                                        AppLog.e("Picasso", "Error : ");
                                    }
                                });
                            }
                        });
            } else {
                holder.img_lead_default.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(item.getName()), ImageUtil.getRandomColor(position));
                holder.img_lead_default.setImageDrawable(drawable);
            }

            holder.txt_name.setText(item.getName());
            holder.txt_count.setVisibility(View.GONE);
            holder.img_tree.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    binding.txtEmpty.setText(getResources().getString(R.string.txt_no_class_found));
                }
                else {
                    binding.txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                binding.txtEmpty.setText(getResources().getString(R.string.txt_no_class_found));
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.img_lead)
            ImageView imgTeam;

            @Bind(R.id.img_lead_default)
            ImageView img_lead_default;

            @Bind(R.id.txt_name)
            TextView txt_name;

            @Bind(R.id.txt_count)
            TextView txt_count;
            @Bind(R.id.img_tree)
            ImageView img_tree;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onTreeClick(list.get(getAdapterPosition()));
                    }
                });
                img_tree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onTreeClick(list.get(getAdapterPosition()));
                    }
                });

            }
        }
    }

    private void onTreeClick(ClassResponse.ClassData classData) {

    }


    public class TodayTopicsAdapter extends RecyclerView.Adapter<TodayTopicsAdapter.ViewHolder>
    {
        ArrayList<TodaySyllabusPlanRes.TodaySyllabusData> list;
        private Context mContext;
        private boolean isExpand = false;
        public TodayTopicsAdapter(ArrayList<TodaySyllabusPlanRes.TodaySyllabusData> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_today_topics,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final TodaySyllabusPlanRes.TodaySyllabusData item = list.get(position);

            holder.tvTeamName.setText(item.getTeamName());
            holder.tvTopicName.setText(item.getTopicName());
            holder.tvChpName.setText(item.getChapterName());
        }


        private void isExpand(boolean b)
        {
           this.isExpand = b;
           notifyDataSetChanged();
        }
        @Override
        public int getItemCount() {

            if(list!=null && list.size() > 0)
            {
                if (isExpand)
                {
                    return list.size();
                }
                else
                {
                    return 1;
                }
            }
            else
            {
                return 0;
            }

        }
        public class ViewHolder extends RecyclerView.ViewHolder {


            @Bind(R.id.tvTeamName)
            TextView tvTeamName;

            @Bind(R.id.tvTopicName)
            TextView tvTopicName;

            @Bind(R.id.tvChpName)
            TextView tvChpName;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);

            }
        }
    }
}