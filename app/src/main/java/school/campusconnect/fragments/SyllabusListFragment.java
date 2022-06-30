package school.campusconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.EditIssueActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.SyllabusDetailsActivity;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.FragmentSyllabusListBinding;
import school.campusconnect.databinding.ItemTopicDetailsBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.issue.IssueListResponse;
import school.campusconnect.datamodel.syllabus.ChangeStatusPlanModel;
import school.campusconnect.datamodel.syllabus.SyllabusListModelRes;
import school.campusconnect.datamodel.syllabus.SyllabusPlanRequest;
import school.campusconnect.datamodel.syllabus.SyllabusTBL;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;

public class SyllabusListFragment extends BaseFragment implements LeafManager.OnCommunicationListener{
    public static final String TAG = "SyllabusListFragment";
    FragmentSyllabusListBinding binding;
    LeafManager manager;
    String teamId,subjectId,role;
    SyllabusAdapter adapter;
    ArrayList<SyllabusListModelRes.SyllabusData> syllabusDataList = new ArrayList<>();

    ArrayList<SyllabusListModelRes.SyllabusData> syllabusDataSpinnerList = new ArrayList<>();

    public static SyllabusListFragment newInstance() {
        SyllabusListFragment fragment = new SyllabusListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_syllabus_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inits();

        bindChapter();

        //     getLocallyAll();

    }

    private void bindChapter() {

        List<SyllabusTBL> tblList = SyllabusTBL.getSyllabus(teamId,subjectId);

        Log.e(TAG,"tblList "+tblList.size());

        syllabusDataSpinnerList.clear();


        if (tblList.size() > 0)
        {
            binding.llChapterAll.setVisibility(View.VISIBLE);

            for (int i = 0;i<tblList.size();i++)
            {
                SyllabusListModelRes.SyllabusData data = new SyllabusListModelRes.SyllabusData();

                data.setChapterName(tblList.get(i).chapterName);
                data.setChapterId(tblList.get(i).chapterId);
                data.setTopicData(new Gson().fromJson(tblList.get(i).topicsList, new TypeToken<ArrayList<SyllabusListModelRes.TopicData>>() {}.getType()));
                syllabusDataSpinnerList.add(data);
            }
        }
        else
        {
            getLocallyAll();
            binding.llChapterAll.setVisibility(View.GONE);
        }


        if (syllabusDataSpinnerList != null && syllabusDataSpinnerList.size() > 0) {

            String[] strChapter = new String[syllabusDataSpinnerList.size()+1];

            strChapter[0] = "All";

            for (int i = 1; i < strChapter.length; i++) {
                strChapter[i] = syllabusDataSpinnerList.get(i-1).getChapterName();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.item_spinner, strChapter);
            binding.spChapter.setAdapter(adapter);

            binding.spChapter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (position == 0)
                    {
                        getLocallyAll();
                    }
                    else
                    {

                        for (int i = 1; i < strChapter.length; i++) {

                            if (binding.spChapter.getSelectedItem().toString().equalsIgnoreCase(syllabusDataSpinnerList.get(i-1).getChapterName()))
                            {
                                AppLog.e(TAG, "Compare Value " + syllabusDataSpinnerList.get(i-1).getChapterId());
                                getLocallyChapter(syllabusDataSpinnerList.get(i-1).getChapterId());
                            }

                        }
                    }

                  /*  for (int i = 0;i<syllabusDataList.size();i++)
                    {
                        if (binding.spChapter.getSelectedItem().toString().equalsIgnoreCase(syllabusDataList.get(i).getChapterName()))
                        {
                            AppLog.e(TAG, "Compare Value " + syllabusDataList.get(i).getChapterId());
                        }
                    }*/
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }

    }

    private void getLocallyAll() {

        List<SyllabusTBL> tblList = SyllabusTBL.getSyllabus(teamId,subjectId);
        syllabusDataList.clear();
        adapter.add(syllabusDataList);

        if (tblList.size() > 0)
        {
            for (int i = 0;i<tblList.size();i++)
            {
                SyllabusListModelRes.SyllabusData data = new SyllabusListModelRes.SyllabusData();

                data.setChapterName(tblList.get(i).chapterName);
                data.setChapterId(tblList.get(i).chapterId);
                data.setTopicData(new Gson().fromJson(tblList.get(i).topicsList, new TypeToken<ArrayList<SyllabusListModelRes.TopicData>>() {}.getType()));
                syllabusDataList.add(data);
            }
            Collections.reverse(syllabusDataList);
            adapter.add(syllabusDataList);



        }
        else
        {
            apiCall();
        }
    }

    private void getLocallyChapter(String chapterID) {

        List<SyllabusTBL> tblList = SyllabusTBL.getSyllabusChapter(teamId,subjectId,chapterID);
        syllabusDataList.clear();
        adapter.add(syllabusDataList);

        if (tblList.size() > 0)
        {
            for (int i = 0;i<tblList.size();i++)
            {
                SyllabusListModelRes.SyllabusData data = new SyllabusListModelRes.SyllabusData();

                data.setChapterName(tblList.get(i).chapterName);
                data.setChapterId(tblList.get(i).chapterId);
                data.setTopicData(new Gson().fromJson(tblList.get(i).topicsList, new TypeToken<ArrayList<SyllabusListModelRes.TopicData>>() {}.getType()));
                syllabusDataList.add(data);
            }
            Collections.reverse(syllabusDataList);
            adapter.add(syllabusDataList);
        }
    }

    private void inits() {

        teamId = getArguments().getString("team_id");
        subjectId = getArguments().getString("subject_id");
        role = getArguments().getString("role");
        Log.e(TAG,"team ID"+teamId+"\nsubject Id"+subjectId+"Role \n"+role);

        manager = new LeafManager();

        adapter = new SyllabusAdapter();
        binding.rvSyllabus.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISSYLLABUSUPDATED)) {
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISSYLLABUSUPDATED, false);
            syllabusDataList.clear();
            SyllabusTBL.deleteAll(teamId,subjectId);
            adapter.add(syllabusDataList);
            apiCall();
        }
    }

    private void apiCall()
    {
        if (isConnectionAvailable())
        {
            showLoadingBar(binding.progressBar);
            manager.getSyllabus(this, GroupDashboardActivityNew.groupId,teamId,subjectId);
        }
        else
        {
            showNoNetworkMsg();
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        switch (apiId)
        {
            case LeafManager.API_GET_SYLLABUS:
                SyllabusListModelRes res = (SyllabusListModelRes) response;
                saveToLocally(res.getSyllabusData());
                break;

            case LeafManager.API_STATUS_PLAN:
                syllabusDataList.clear();
                SyllabusTBL.deleteAll(teamId,subjectId);
                adapter.add(syllabusDataList);
                apiCall();
                break;

            case LeafManager.API_CHANGE_STATUS_PLAN:
                syllabusDataList.clear();
                SyllabusTBL.deleteAll(teamId,subjectId);
                adapter.add(syllabusDataList);
                apiCall();
                break;
        }
    }

    private void saveToLocally(ArrayList<SyllabusListModelRes.SyllabusData> syllabusData) {
        syllabusDataList.clear();
        SyllabusTBL.deleteAll(teamId,subjectId);

        if (syllabusData.size() > 0)
        {
            for (int i = 0;i<syllabusData.size();i++)
            {
                SyllabusTBL tbl = new SyllabusTBL();
                tbl.teamID = teamId;
                tbl.subjectID = subjectId;
                tbl.chapterId = syllabusData.get(i).getChapterId();
                tbl.chapterName = syllabusData.get(i).getChapterName();
                tbl.topicsList = new Gson().toJson(syllabusData.get(i).getTopicData());
                tbl.save();
            }
        }
        syllabusDataList.addAll(syllabusData);
        Collections.reverse(syllabusDataList);
        adapter.add(syllabusDataList);

        bindChapter();

    }

    @Override
    public void onFailure(int apiId, String msg) {

        hideLoadingBar();
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }


    public class SyllabusAdapter extends RecyclerView.Adapter<SyllabusAdapter.ViewHolder>
    {
        private Context mContext;
        private ArrayList<SyllabusListModelRes.SyllabusData> list;


        public void add(ArrayList<SyllabusListModelRes.SyllabusData> list)
        {
            this.list = list;
            notifyDataSetChanged();
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter_list_v2,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            SyllabusListModelRes.SyllabusData data = list.get(position);

            holder.txt_name.setText(data.getChapterName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //       onTreeClick(data);
                }
            });
          /*  holder.img_tree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTreeClick(data);
                }
            });
*/
            if (data.getTopicData() != null && data.getTopicData().size() > 0)
            {
                holder.topicData.setVisibility(View.VISIBLE);
                holder.topicData.setAdapter(new TopicAdapter(data.getTopicData(),data.getChapterId()));
            }
            else
            {
                holder.topicData.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {

            if(list!=null)
            {
                if(list.size()==0)
                {
                    binding.txtEmpty.setText(getResources().getString(R.string.txt_no_syllabus_found));
                }
                else {
                    binding.txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                binding.txtEmpty.setText(getResources().getString(R.string.txt_no_syllabus_found));
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.txt_name)
            TextView txt_name;

         /*   @Bind(R.id.img_tree)
            ImageView img_tree;*/

            @Bind(R.id.topicData)
            RecyclerView topicData;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
    }

    private void onTreeClick(SyllabusListModelRes.SyllabusData classData) {
        Intent intent = new Intent(getContext(), SyllabusDetailsActivity.class);
        intent.putExtra("data",classData);
        intent.putExtra("role",role);
        intent.putExtra("team_id",teamId);
        intent.putExtra("subject_id",subjectId);
        startActivity(intent);
    }









    public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder>{

        ArrayList<SyllabusListModelRes.TopicData> topicData;
        String chapterID;
        Context context;

        public TopicAdapter(ArrayList<SyllabusListModelRes.TopicData> topicData,String chID) {
            this.topicData = topicData;
            this.chapterID = chID;
        }

        @NonNull
        @Override
        public TopicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            context = parent.getContext();
            ItemTopicDetailsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.item_topic_details,parent,false);
            return new TopicAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull TopicAdapter.ViewHolder holder, int position) {
            SyllabusListModelRes.TopicData data = topicData.get(position);

            holder.binding.txtName.setText(data.getTopicName());


            if (data.getToDate() != null && !data.getToDate().isEmpty())
            {
                holder.binding.etToDate.setText(data.getToDate());
                holder.binding.etToDate.setEnabled(false);
                holder.binding.etToDate.setTextColor(getResources().getColor(R.color.grey));
            }
            else
            {
                holder.binding.etToDate.setEnabled(false);
                holder.binding.etToDate.setTextColor(getResources().getColor(R.color.grey));
            }

            if (data.getFromDate() != null && !data.getFromDate().isEmpty())
            {
                holder.binding.etFromDate.setText(data.getFromDate());
                holder.binding.etFromDate.setEnabled(false);
                holder.binding.etFromDate.setTextColor(getResources().getColor(R.color.grey));
            }
            else
            {
                holder.binding.etFromDate.setEnabled(false);
                holder.binding.etFromDate.setTextColor(getResources().getColor(R.color.grey));
            }

            if (data.getActualStartDate() != null && !data.getActualStartDate().isEmpty())
            {
                holder.binding.etActualToDate.setText(data.getActualStartDate());
                holder.binding.etActualToDate.setEnabled(false);
                holder.binding.etActualToDate.setTextColor(getResources().getColor(R.color.grey));
            }
            else
            {
                holder.binding.etActualToDate.setEnabled(false);
                holder.binding.etActualToDate.setTextColor(getResources().getColor(R.color.grey));
            }

            if (data.getActualEndDate() != null && !data.getActualEndDate().isEmpty())
            {
                holder.binding.etActualFromDate.setText(data.getActualEndDate());
                holder.binding.etActualFromDate.setEnabled(false);
                holder.binding.etActualFromDate.setTextColor(getResources().getColor(R.color.grey));
            }
            else
            {
                holder.binding.etActualFromDate.setEnabled(false);
                holder.binding.etActualFromDate.setTextColor(getResources().getColor(R.color.grey));
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

                        if (role != null && role.equalsIgnoreCase("admin"))
                        {
                            holder.binding.imgEdit.setVisibility(View.VISIBLE);
                        }
                    }
                    else
                    {
                        if (holder.binding.imgEdit.getVisibility() == View.VISIBLE)
                        {
                            holder.binding.imgEdit.setVisibility(View.GONE);
                        }
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
                    fragment.setTitle(R.string.lbl_from_date);
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
                    fragment.setTitle(R.string.lbl_to_date);
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
                    fragment.setTitle(R.string.lbl_from_date);
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
                    fragment.setTitle(R.string.lbl_to_date);
                }
            });

            holder.binding.btnDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTreeClickDone(holder.binding.etToDate.getText().toString(),holder.binding.etFromDate.getText().toString(),holder.binding.etActualToDate.getText().toString(),holder.binding.etActualFromDate.getText().toString(),data.getTopicId(),data.getTopicName(),topicData,chapterID);
                }
            });

            if (role != null && (role.equalsIgnoreCase("parent") || (role.equalsIgnoreCase("teacher"))))
            {
                holder.binding.etActualFromDate.setTextColor(getResources().getColor(R.color.grey));
                holder.binding.etActualToDate.setTextColor(getResources().getColor(R.color.grey));
                holder.binding.etFromDate.setTextColor(getResources().getColor(R.color.grey));
                holder.binding.etToDate.setTextColor(getResources().getColor(R.color.grey));

                holder.binding.etActualFromDate.setEnabled(false);
                holder.binding.etActualToDate.setEnabled(false);
                holder.binding.etFromDate.setEnabled(false);
                holder.binding.etToDate.setEnabled(false);

                holder.binding.btnDone.setVisibility(View.GONE);
                holder.binding.imgEdit.setVisibility(View.GONE);
            }

            if (role != null && role.equalsIgnoreCase("teacher"))
            {
                holder.binding.etFromDate.setEnabled(true);
                holder.binding.etToDate.setEnabled(true);
                holder.binding.etFromDate.setTextColor(getResources().getColor(R.color.white));
                holder.binding.etToDate.setTextColor(getResources().getColor(R.color.white));

                holder.binding.btnDone.setVisibility(View.VISIBLE);
            }
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



    private void onTreeClickDone(String planto, String planfrom, String actualto, String actualfrom,String topicId,String topicName,ArrayList<SyllabusListModelRes.TopicData> topicData, String chapterID) {

        boolean isUpdate = true;

        for (int i = 0;i<topicData.size();i++)
        {
            if (topicId.equals(topicData.get(i).getTopicId()))
            {
                if (topicData.get(i).getToDate() == null && topicData.get(i).getFromDate() == null && topicData.get(i).getActualStartDate() == null && topicData.get(i).getActualEndDate() == null){
                    isUpdate = false;
                }
            }
        }

        if (isUpdate)
        {

            ChangeStatusPlanModel.ChangeStatusModelReq req = new ChangeStatusPlanModel.ChangeStatusModelReq();
            req.setToDate(planto);
            req.setFromDate(planfrom);
            req.setActualStartDate(actualto);
            req.setActualEndDate(actualfrom);

            Log.e(TAG,"req is Update"+new Gson().toJson(req));
            showLoadingBar(binding.progressBar);
            manager.changeStatusPlan(this,GroupDashboardActivityNew.groupId,teamId,subjectId,topicId,req);
        }
        else
        {
            SyllabusPlanRequest request = new SyllabusPlanRequest();

            ArrayList<SyllabusPlanRequest.TopicData> list = new ArrayList<>();

            for (int i = 0;i<topicData.size();i++)
            {
                if (!topicId.equals(topicData.get(i).getTopicId()))
                {
                    SyllabusPlanRequest.TopicData topicData1 = new SyllabusPlanRequest.TopicData();
                    topicData1.setToDate(topicData.get(i).getToDate());
                    topicData1.setFromDate(topicData.get(i).getFromDate());
                    topicData1.setActualStartDate(topicData.get(i).getActualStartDate());
                    topicData1.setActualEndDate(topicData.get(i).getActualEndDate());
                    topicData1.setTopicId(topicData.get(i).getTopicId());
                    topicData1.setTopicName(topicData.get(i).getTopicName());

                    list.add(topicData1);
                }
            }

            SyllabusPlanRequest.TopicData topicData1 = new SyllabusPlanRequest.TopicData();

            if (!planto.isEmpty())
            {
                topicData1.setToDate(planto);
            }
            if (!planfrom.isEmpty())
            {
                topicData1.setFromDate(planfrom);
            }
            if (!actualto.isEmpty())
            {
                topicData1.setActualStartDate(actualto);
            }
            if (!actualfrom.isEmpty())
            {
                topicData1.setActualEndDate(actualfrom);
            }
            if (!topicId.isEmpty())
            {
                topicData1.setTopicId(topicId);
            }
            if (!topicName.isEmpty())
            {
                topicData1.setTopicName(topicName);
            }

            list.add(topicData1);

            request.setTopicData(list);
            Log.e(TAG,"req is Not Update"+new Gson().toJson(request));

            showLoadingBar(binding.progressBar);
            manager.statusPlan(this,GroupDashboardActivityNew.groupId,teamId,subjectId,chapterID,request);
        }

    }
}