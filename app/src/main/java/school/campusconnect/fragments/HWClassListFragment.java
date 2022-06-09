package school.campusconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.HWClassSubjectActivity;
import school.campusconnect.activities.MarksCardActivity2;
import school.campusconnect.activities.StaffClassListActivity;
import school.campusconnect.activities.TimeTabelActivity2;
import school.campusconnect.activities.UpdateTopicActivity;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.FragmentHwclassListBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ClassListTBL;
import school.campusconnect.datamodel.TeamCountTBL;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.staff.StaffResponse;
import school.campusconnect.datamodel.syllabus.StaffAnalysisRes;
import school.campusconnect.datamodel.syllabus.TodaySyllabusPlanRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.MixOperations;

public class HWClassListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {

    FragmentHwclassListBinding binding;
    private static final String TAG = "HWClassListFragment";


    String staffID;
    String date;
    private Boolean expandable = false;
    private Boolean expandableChart = false;

    TodayTopicsAdapter adapter;
    private ArrayList<StaffResponse.StaffData> staffData = new ArrayList<>();
    ArrayList<ClassResponse.ClassData> resultClass = new ArrayList<>();
/*
    @Bind(R.id.swipeRefreshLayout)
    public PullRefreshLayout swipeRefreshLayout;*/

    String role;
    String type;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_hwclass_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();

        if (type.equalsIgnoreCase("Syllabus Tracker") && role.equalsIgnoreCase("admin"))
        {
            //binding.spFilter.setVisibility(View.VISIBLE);
        }
        else
        {
            getDataLocally();
        }

    }

    private void init() {

        AppLog.e(TAG, "HWClassListFragment");
      /*  swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    swipeRefreshLayout.setRefreshing(false);
                    apiCall();
                } else {
                    showNoNetworkMsg();
                }
            }
        });*/

    //    binding.rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        role = getArguments().getString("role");
        Log.e(TAG,"role"+role);

        type = getArguments().getString("type");
        Log.e(TAG,"type"+type);
        date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        staffID = LeafPreference.getInstance(getContext()).getString(LeafPreference.LOGIN_ID);

        Log.e(TAG,"date"+date);

        binding.imgDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExpandable();
            }
        });

        binding.imgDropdownAnylysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (expandableChart)
                {
                    expandableChart = false;
                    binding.llChartDetails.setVisibility(View.GONE);
                    binding.imgDropdownAnylysis.setRotation(360);
                }else
                {
                    expandableChart = true;
                    binding.llChartDetails.setVisibility(View.VISIBLE);
                    binding.imgDropdownAnylysis.setRotation(180);
                }
            }
        });



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_spinner, new String[]{"Class Wise","Staff Wise"});
        binding.spFilter.setAdapter(adapter);

        binding.spFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0)
                {
                    binding.rvStaff.setVisibility(View.GONE);
                    binding.rvClass.setVisibility(View.VISIBLE);

                    if (resultClass.size() > 0)
                    {
                        binding.rvClass.setAdapter(new ClassesAdapter(resultClass));
                    }
                    else
                    {
                        getDataLocally();
                    }
                }
                else
                {
                    binding.rvStaff.setVisibility(View.VISIBLE);
                    binding.rvClass.setVisibility(View.GONE);

                    if (staffData.size() > 0)
                    {
                        binding.rvStaff.setAdapter(new StaffAdapter(staffData));
                    }
                    else
                    {
                        staffData.clear();
                        apiCallStaff();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void getDataLocally() {

        List<ClassListTBL> list = ClassListTBL.getAll(GroupDashboardActivityNew.groupId);
        if (list.size() != 0) {

            for (int i = 0; i < list.size(); i++) {
                ClassListTBL currentItem = list.get(i);
                ClassResponse.ClassData item = new ClassResponse.ClassData();
                item.id = currentItem.teamId;
                item.teacherName = currentItem.teacherName;
                item.phone = currentItem.phone;
                item.members = currentItem.members;
                item.countryCode = currentItem.countryCode;
                item.className = currentItem.name;
                item.classImage = currentItem.image;
                item.category = currentItem.category;
                item.jitsiToken = currentItem.jitsiToken;
                item.userId = currentItem.userId;
                item.rollNumber = currentItem.rollNumber;
                resultClass.add(item);
            }
            binding.rvClass.setAdapter(new ClassesAdapter(resultClass));

            TeamCountTBL dashboardCount = TeamCountTBL.getByTypeAndGroup("ALL", GroupDashboardActivityNew.groupId);
            if (dashboardCount != null) {
                boolean apiCall = false;
                if (dashboardCount.lastApiCalled != 0) {
                    if (MixOperations.isNewEvent(dashboardCount.lastInsertedTeamTime, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", dashboardCount.lastApiCalled)) {
                        apiCall = true;
                    }
                }
                if (dashboardCount.oldCount != dashboardCount.count) {
                    dashboardCount.oldCount = dashboardCount.count;
                    dashboardCount.save();
                    apiCall = true;
                }

                if (apiCall) {
                    apiCall(false);
                }
            }
        } else {
            apiCall(true);
        }
    }


    private void apiCallStaff()
    {
        LeafManager leafManager = new LeafManager();
        leafManager.getStaff(this, GroupDashboardActivityNew.groupId);
    }
    private void apiCall(boolean isLoading) {
        if(isLoading)
            showLoadingBar(binding.progressBar,false);
           // progressBar.setVisibility(View.VISIBLE);
        LeafManager leafManager = new LeafManager();
        if ("teacher".equalsIgnoreCase(role)) {
            leafManager.getTeacherClasses(this, GroupDashboardActivityNew.groupId);
        }else if ("parent".equalsIgnoreCase(role)) {
            leafManager.getParentKidsNew(this, GroupDashboardActivityNew.groupId);
        } else {
            leafManager.getClasses(this, GroupDashboardActivityNew.groupId);
        }
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
        entries.add(new BarEntry(0.0f, (float) staffAnalysisData.getTotalTopicsCount()));
        entries.add(new BarEntry(0.5f, (float) staffAnalysisData.getTotalTopicsCompleted()));
        entries.add(new BarEntry(1.0f, (float) staffAnalysisData.getTotalTopicsPending()));

        BarDataSet dataset = new BarDataSet(entries, "");
        dataset.setDrawValues(true);

        dataset.setColors(new int[] {Color.YELLOW, Color.GREEN, Color.RED});

        binding.chart1.getXAxis().setDrawGridLines(false);
        binding.chart1.getAxisLeft().setDrawGridLines(false);
        binding.chart1.getAxisRight().setDrawGridLines(false);

        binding.chart1.getLegend().setEnabled(false);


        /*ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add(String.valueOf(staffAnalysisData.getTotalTopicsCount()));
        xAxisLabel.add(String.valueOf(staffAnalysisData.getTotalTopicsCompleted()));
        xAxisLabel.add(String.valueOf(staffAnalysisData.getTotalTopicsPending()));

        ValueFormatter formatter = new ValueFormatter() {

            @Override
            public String getFormattedValue(float value) {
                return xAxisLabel.get((int) value);
            }
        };*/





        XAxis xAxis = binding.chart1.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setDrawLabels(false);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // xAxis.setValueFormatter(formatter);

        YAxis yAxis = binding.chart1.getAxisRight();
        yAxis.setEnabled(false);
        yAxis.setAxisMinValue(0f);
        yAxis.setDrawGridLines(false);

        YAxis yAxisLeft = binding.chart1.getAxisLeft();
        yAxisLeft.setAxisMinValue(0f);
        yAxisLeft.setDrawLabels(false);

        binding.chart1.setDescription(null);


        BarData data = new BarData(dataset);

        data.setBarWidth(0.4f);

        binding.chart1.setTouchEnabled(false);
        binding.chart1.setDragEnabled(false);
        binding.chart1.setScaleEnabled(false);
        binding.chart1.setFitBars(false);
        binding.chart1.setBackground(null);
        binding.chart1.setData(data);
        binding.chart1.invalidate();

        /*pie graph*/

        binding.pieChart.setUsePercentValues(true);
        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.setDragDecelerationFrictionCoef(0.9f);
        binding.pieChart.setDrawCenterText(false);
        binding.pieChart.setDrawHoleEnabled(false);
        binding.pieChart.setTouchEnabled(false);

        binding.pieChart.getLegend().setEnabled(false);

        ArrayList<PieEntry> yValues = new ArrayList<>();
        yValues.add(new PieEntry(staffAnalysisData.getTotalTopicsPending()));
        yValues.add(new PieEntry(staffAnalysisData.getTotalEndDelay()));
        yValues.add(new PieEntry(staffAnalysisData.getTotalTopicsCompleted()-staffAnalysisData.getTotalEndDelay()));

      /*  yValues.add(new PieEntry(5));
        yValues.add(new PieEntry(6));
        yValues.add(new PieEntry(75));*/

        binding.pieChart.setDrawEntryLabels(false);

        PieDataSet dataSet = new PieDataSet(yValues, "");

        dataSet.setColors(Color.parseColor("#FF5F1F"), Color.parseColor("#98FF91"), Color.parseColor("#91CBFF"));
        PieData pieData = new PieData((dataSet));

        binding.pieChart.setData(pieData);
        binding.pieChart.invalidate();


    }

    @Override
    public void onStart() {

        if (type.equalsIgnoreCase("Syllabus Tracker") && role.equalsIgnoreCase("teacher"))
        {
            LeafManager leafManager = new LeafManager();
            showLoadingBar(binding.progressBar,false);
            leafManager.getTodaySyllabusPlanList(this,GroupDashboardActivityNew.groupId,date);
            leafManager.getStaffAnalysis(this,GroupDashboardActivityNew.groupId,staffID);
        }
        else
        {
            binding.llTodayData.setVisibility(View.GONE);
            binding.llAnalysis.setVisibility(View.GONE);
        }

        if (type.equalsIgnoreCase("Syllabus Tracker") && role.equalsIgnoreCase("admin"))
        {
            binding.spFilter.setVisibility(View.VISIBLE);
        }
        else
        {
            binding.spFilter.setVisibility(View.GONE);
        }

        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
        //    progressBar.setVisibility(View.GONE);

        if (apiId == LeafManager.API_TODAY_DATE_WISE_SYLLBUS_PLAN)
        {
            TodaySyllabusPlanRes res = (TodaySyllabusPlanRes) response;
            if (res.getTodaySyllabusData().size() > 0)
            {
                adapter = new TodayTopicsAdapter(res.getTodaySyllabusData());
                binding.rvTodayData.setAdapter(adapter);
            }
            else
            {
                binding.llTodayData.setVisibility(View.GONE);
            }
        }
        else if (apiId == LeafManager.API_STAFF)
        {
            StaffResponse res = (StaffResponse) response;
            staffData = res.getData();
            binding.rvStaff.setAdapter(new StaffAdapter(staffData));
        }
        else if (apiId == LeafManager.API_STAFF_ANALYSIS)
        {
            StaffAnalysisRes res = (StaffAnalysisRes) response;

            if (res.getData().size() > 0)
            {
                setData(res.getData().get(0));
            }
        }
        else {
            ClassResponse res = (ClassResponse) response;
            resultClass.clear();
            resultClass = res.getData();
            AppLog.e(TAG, "ClassResponse " + new Gson().toJson(resultClass));
            binding.rvClass.setAdapter(new ClassesAdapter(resultClass));

            TeamCountTBL dashboardCount = TeamCountTBL.getByTypeAndGroup("ALL", GroupDashboardActivityNew.groupId);
            if(dashboardCount!=null){
                dashboardCount.lastApiCalled = System.currentTimeMillis();
                dashboardCount.save();
            }

            saveToDB(resultClass);
        }
    }

    private void saveToDB(List<ClassResponse.ClassData> result) {
        if (result == null)
            return;

        ClassListTBL.deleteAll(GroupDashboardActivityNew.groupId);
        for (int i = 0; i < result.size(); i++) {
            ClassResponse.ClassData currentItem = result.get(i);
            ClassListTBL item = new ClassListTBL();
            item.teamId = currentItem.id;
            item.teacherName = currentItem.teacherName;
            item.phone = currentItem.phone;
            item.members = currentItem.members;
            item.countryCode = currentItem.countryCode;
            item.name = currentItem.className;
            item.image = currentItem.classImage;
            item.category = currentItem.category;
            item.jitsiToken = currentItem.jitsiToken;
            item.groupId = GroupDashboardActivityNew.groupId;
            item.save();
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
        //    progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
        //    progressBar.setVisibility(View.GONE);
    }


    public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder> {
        List<ClassResponse.ClassData> list;
        private Context mContext;

        public ClassesAdapter(List<ClassResponse.ClassData> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final ClassResponse.ClassData item = list.get(position);

            if (!TextUtils.isEmpty(item.getImage())) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(50, 50).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgTeam,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.img_lead_default.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(50, 50).into(holder.imgTeam, new Callback() {
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
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    binding.txtEmpty.setText(getResources().getString(R.string.txt_no_class_found));
                } else {
                    binding.txtEmpty.setText("");
                }

                return list.size();
            } else {
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
                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onTreeClick(list.get(getAdapterPosition()));
                    }
                });

            }
        }
    }

    private void onTreeClick(ClassResponse.ClassData classData) {
        if(type.equals("Home Work") || type.equals("Recorded Class") || type.equals("Syllabus Tracker")){
            Intent intent = new Intent(getContext(), HWClassSubjectActivity.class);
            intent.putExtra("team_id", classData.getId());
            intent.putExtra("title", classData.className);
            intent.putExtra("role", role);
            intent.putExtra("type", type);
            startActivity(intent);
        }else if("Time Table".equalsIgnoreCase(type)){
            Intent intent = new Intent(getActivity(), TimeTabelActivity2.class);
            intent.putExtra("team_id",classData.getId());
            intent.putExtra("team_name",classData.getName());
            intent.putExtra("role",role);
            startActivity(intent);
        }else if("Marks Card".equalsIgnoreCase(type)){
            Intent intent = new Intent(getActivity(), MarksCardActivity2.class);
            intent.putExtra("team_id",classData.getId());
            intent.putExtra("userId",classData.userId);
            intent.putExtra("team_name",classData.getName());
            intent.putExtra("role",role);
            startActivity(intent);
        }

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
        public TodayTopicsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_today_topics,parent,false);
            return new TodayTopicsAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final TodayTopicsAdapter.ViewHolder holder, final int position)
        {
            final TodaySyllabusPlanRes.TodaySyllabusData item = list.get(position);

            holder.tvTeamName.setText(item.getTeamName());
            holder.tvTopicName.setText(item.getTopicName());
            holder.tvChpName.setText(item.getChapterName());

            holder.imgTree.setVisibility(View.VISIBLE);

            holder.imgTree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), UpdateTopicActivity.class);
                    i.putExtra("data",item);
                    startActivity(i);
                }
            });
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
        public class ViewHolder extends RecyclerView.ViewHolder
        {

            @Bind(R.id.tvTeamName)
            TextView tvTeamName;

            @Bind(R.id.tvTopicName)
            TextView tvTopicName;

            @Bind(R.id.tvChpName)
            TextView tvChpName;

            @Bind(R.id.imgTree)
            ImageView imgTree;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);

            }
        }

    }


    public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.ViewHolder>
    {
        List<StaffResponse.StaffData> list;
        private Context mContext;

        public StaffAdapter(List<StaffResponse.StaffData> list) {
            this.list = list;
        }

        @Override
        public StaffAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff,parent,false);
            return new StaffAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final StaffAdapter.ViewHolder holder, final int position) {
            final StaffResponse.StaffData item = list.get(position);

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
            if(!TextUtils.isEmpty(item.designation)){
                holder.txt_count.setText("[" + item.getDesignation() + "]");
                holder.txt_count.setVisibility(View.VISIBLE);
            }else {
                holder.txt_count.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {

                    binding.txtEmpty.setText(getResources().getString(R.string.txt_no_staff_found));
                }
                else {
                    binding.txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                binding.txtEmpty.setText(getResources().getString(R.string.txt_no_staff_found));
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
                        onTreeClickStaff(list.get(getAdapterPosition()));
                    }
                });
                img_tree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onTreeClickStaff(list.get(getAdapterPosition()));
                    }
                });

            }
        }
    }

    private void onTreeClickStaff(StaffResponse.StaffData classData) {

        Intent intent = new Intent(getActivity(), StaffClassListActivity.class);
        intent.putExtra("staff_id",classData.getStaffId());
        intent.putExtra("title",classData.getName());
        intent.putExtra("role",role);
        intent.putExtra("type",type);
        startActivity(intent);
    }
}
