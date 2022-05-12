package school.campusconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.EditIssueActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.SyllabusDetailsActivity;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.FragmentSyllabusListBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.issue.IssueListResponse;
import school.campusconnect.datamodel.syllabus.SyllabusListModelRes;
import school.campusconnect.datamodel.syllabus.SyllabusTBL;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;

public class SyllabusListFragment extends BaseFragment implements LeafManager.OnCommunicationListener{
public static final String TAG = "SyllabusListFragment";
FragmentSyllabusListBinding binding;
LeafManager manager;
String teamId,subjectId;
SyllabusAdapter adapter;
ArrayList<SyllabusListModelRes.SyllabusData> syllabusDataList = new ArrayList<>();
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

        getLocally();

    }

    private void getLocally() {

        List<SyllabusTBL> tblList = SyllabusTBL.getSyllabus(teamId,subjectId);
        syllabusDataList.clear();

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

            adapter.add(syllabusDataList);
        }
        else
        {
            apiCall();
        }
    }

    private void inits() {

        teamId = getArguments().getString("team_id");
        subjectId = getArguments().getString("subject_id");

        Log.e(TAG,"team ID"+teamId+"\nsubject Id"+subjectId);

        manager = new LeafManager();

        adapter = new SyllabusAdapter();
        binding.rvSyllabus.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISSYLLABUSUPDATED)) {
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISSYLLABUSUPDATED, false);
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
        adapter.add(syllabusDataList);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_issue,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            SyllabusListModelRes.SyllabusData data = list.get(position);

            holder.txt_name.setText(data.getChapterName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTreeClick(data);
                }
            });
            holder.img_tree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTreeClick(data);
                }
            });
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

            @Bind(R.id.img_tree)
            ImageView img_tree;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
    }

    private void onTreeClick(SyllabusListModelRes.SyllabusData classData) {
        Intent intent = new Intent(getContext(), SyllabusDetailsActivity.class);
        intent.putExtra("data",classData);
        startActivity(intent);
    }
}