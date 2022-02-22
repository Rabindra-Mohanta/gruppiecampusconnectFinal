package school.campusconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.HWParentActivity;
import school.campusconnect.activities.HWStudentActivity;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.EventTBL;
import school.campusconnect.datamodel.HwItem;
import school.campusconnect.datamodel.SubjectItem;
import school.campusconnect.datamodel.homework.HwRes;
import school.campusconnect.datamodel.subjects.SubjectStaffResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.MixOperations;

public class HWListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    String team_id;
    String className;
    String subject_id;
    String subject_name;
    boolean canPost;
/*
    @Bind(R.id.swipeRefreshLayout)
    public PullRefreshLayout swipeRefreshLayout;*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss, container, false);
        ButterKnife.bind(this, view);
        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        team_id = getArguments().getString("team_id");
        subject_id = getArguments().getString("subject_id");
        subject_name = getArguments().getString("subject_name");
        className = getArguments().getString("className");
        canPost = getArguments().getBoolean("canPost");

        init();

        getDataLocally();

        return view;
    }

    private void init() {
       /* swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    getChapters();
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    showNoNetworkMsg();
                }
            }
        });*/
    }
    EventTBL eventTBL;
    private void getDataLocally() {
        eventTBL = EventTBL.getAssignmentEvent(GroupDashboardActivityNew.groupId,team_id,subject_id);
        boolean apiEvent = false;
        if(eventTBL!=null){
            if(eventTBL._now ==0){
                apiEvent = true;
            }
            if(MixOperations.isNewEvent(eventTBL.eventAt,"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",eventTBL._now)){
                apiEvent = true;
            }
        }
        List<HwItem> list = HwItem.getAll(subject_id,team_id,GroupDashboardActivityNew.groupId);
        if (list.size() != 0) {
            ArrayList<HwRes.HwData> result= new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                HwItem currentItem = list.get(i);
                HwRes.HwData item = new HwRes.HwData();
                item.topic = currentItem.topic;
                item.subjectId = currentItem.subjectId;
                item.lastSubmissionDate = currentItem.lastSubmissionDate;
                item.teamId = currentItem.teamId;
                item.groupId = currentItem.groupId;
                item.description = currentItem.description;
                item.createdByName = currentItem.createdByName;
                item.postedAt = currentItem.postedAt;
                item.createdByImage = currentItem.createdByImage;
                item.createdById = currentItem.createdById;
                item.canPost = currentItem.canPost;
                item.assignmentId = currentItem.assignmentId;
                item.studentAssignmentId = currentItem.studentAssignmentId;
                item.fileType = currentItem.fileType;
                item.fileName =new Gson().fromJson(currentItem.fileName,new TypeToken<ArrayList<String>>() {}.getType());
                item.thumbnailImage =new Gson().fromJson(currentItem.thumbnailImage,new TypeToken<ArrayList<String>>() {}.getType());
                item.thumbnail = currentItem.thumbnail;
                item.video = currentItem.video;
                result.add(item);
            }
            rvClass.setAdapter(new HwAdapter(result));

            if(apiEvent){
                getChapters(false);
            }
        } else {
            getChapters(true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (LeafPreference.getInstance(getActivity()).getBoolean("is_hw_added")) {
            getChapters(true);
            LeafPreference.getInstance(getActivity()).setBoolean("is_hw_added", false);
        }
    }

    public void getChapters(boolean isLoading) {
        if(isLoading)
            progressBar.setVisibility(View.VISIBLE);
        LeafManager leafManager = new LeafManager();
        leafManager.getHwList(this, GroupDashboardActivityNew.groupId, team_id, subject_id);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);
        HwRes res = (HwRes) response;
        List<HwRes.HwData> result = res.getData();
        AppLog.e(TAG, "HwRes " + result);

        rvClass.setAdapter(new HwAdapter(result));

        saveToDB(result);

        if(eventTBL!=null){
            eventTBL._now = System.currentTimeMillis();
            eventTBL.save();
        }
    }
    private void saveToDB(List<HwRes.HwData> result) {
        if (result == null)
            return;

        HwItem.deleteAll(subject_id);

        for (int i = 0; i < result.size(); i++) {
            HwRes.HwData currentItem = result.get(i);
            HwItem classItem = new HwItem();
            classItem.topic = currentItem.topic;
            classItem.subjectId = currentItem.subjectId;
            classItem.lastSubmissionDate = currentItem.lastSubmissionDate;
            classItem.teamId = currentItem.teamId;
            classItem.groupId = currentItem.groupId;
            classItem.description = currentItem.description;
            classItem.createdByName = currentItem.createdByName;
            classItem.postedAt = currentItem.postedAt;
            classItem.createdByImage = currentItem.createdByImage;
            classItem.createdById = currentItem.createdById;
            classItem.canPost = currentItem.canPost;
            classItem.assignmentId = currentItem.assignmentId;
            classItem.studentAssignmentId = currentItem.studentAssignmentId;
            classItem.fileType = currentItem.fileType;
            classItem.fileName =new Gson().toJson(currentItem.fileName);
            classItem.thumbnailImage =new Gson().toJson(currentItem.thumbnailImage);
            classItem.thumbnail = currentItem.thumbnail;
            classItem.video = currentItem.video;
            classItem.teamId = team_id;
            classItem.groupId = GroupDashboardActivityNew.groupId;
            classItem.save();
        }
    }



    @Override
    public void onFailure(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }

    public class HwAdapter extends RecyclerView.Adapter<HwAdapter.ViewHolder> {
        List<HwRes.HwData> list;
        private Context mContext;

        public HwAdapter(List<HwRes.HwData> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hw, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final HwRes.HwData item = list.get(position);
            holder.txt_name.setText(item.topic);
            if(!TextUtils.isEmpty(item.lastSubmissionDate))
            {
                holder.txt_date.setVisibility(View.VISIBLE);
                holder.txt_date.setText(item.lastSubmissionDate);
            }else {
                holder.txt_date.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    txtEmpty.setText("No Homework found.");
                } else {
                    txtEmpty.setText("");
                }

                return list.size();
            } else {
                txtEmpty.setText("No Homework found.");
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.txt_name)
            TextView txt_name;
            @Bind(R.id.txt_date)
            TextView txt_date;


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

    private void onTreeClick(HwRes.HwData data) {
        Intent intent;
        if (data.canPost) {
            intent = new Intent(getActivity(), HWParentActivity.class);
        } else {
            intent = new Intent(getActivity(), HWStudentActivity.class);
        }
        intent.putExtra("group_id", GroupDashboardActivityNew.groupId);
        intent.putExtra("team_id", team_id);
        intent.putExtra("subject_id", subject_id);
        intent.putExtra("subject_name", subject_name);
        intent.putExtra("className", className);
        intent.putExtra("data", data);
        startActivity(intent);
    }
}
