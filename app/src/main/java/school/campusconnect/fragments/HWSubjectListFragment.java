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
import school.campusconnect.activities.AddChapterPostActivity;
import school.campusconnect.activities.ChapterActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.HWListActivity;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.EBookClassItem;
import school.campusconnect.datamodel.SubjectItem;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.subjects.SubjectStaffResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;

public class HWSubjectListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    String team_id;
    String className;

    @Bind(R.id.swipeRefreshLayout)
    public PullRefreshLayout swipeRefreshLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss_refresh, container, false);
        ButterKnife.bind(this, view);
        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        team_id = getArguments().getString("team_id");
        className = getArguments().getString("title");

        init();

        getDataLocally();

        return view;
    }

    private void init() {
        swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    callAPi();
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    showNoNetworkMsg();
                }
            }
        });
    }

    private void getDataLocally() {
        List<SubjectItem> list = SubjectItem.getAll(team_id,GroupDashboardActivityNew.groupId);
        if (list.size() != 0) {
            ArrayList<SubjectStaffResponse.SubjectData> subList= new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                SubjectItem currentItem = list.get(i);
                SubjectStaffResponse.SubjectData item = new SubjectStaffResponse.SubjectData();
                item.staffName = new Gson().fromJson(currentItem.staffName,new TypeToken<ArrayList<SubjectStaffResponse.SubjectStaff>>() {}.getType());
                item.subjectId = currentItem.subjectId;
                item.name = currentItem.subjectName;
                item.canPost = currentItem.canPost;
                subList.add(item);
            }
            rvClass.setAdapter(new SubjectAdapter(subList));
        } else {
           callAPi();
        }
    }

    private void callAPi() {
        progressBar.setVisibility(View.VISIBLE);
        LeafManager leafManager = new LeafManager();
        leafManager.getSubjectStaff(this, GroupDashboardActivityNew.groupId, team_id,"");
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);
        SubjectStaffResponse res = (SubjectStaffResponse) response;
        List<SubjectStaffResponse.SubjectData> result = res.getData();
        AppLog.e(TAG, "ClassResponse " + result);

        rvClass.setAdapter(new SubjectAdapter(result));

        saveToDB(result);
    }
    private void saveToDB(List<SubjectStaffResponse.SubjectData> result) {
        if (result == null)
            return;
        SubjectItem.deleteAll();
        for (int i = 0; i < result.size(); i++) {
            SubjectStaffResponse.SubjectData currentItem = result.get(i);
            SubjectItem classItem = new SubjectItem();
            classItem.staffName = new Gson().toJson(currentItem.staffName);
            classItem.subjectId = currentItem.subjectId;
            classItem.subjectName = currentItem.name;
            classItem.canPost = currentItem.canPost;
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

    public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {
        List<SubjectStaffResponse.SubjectData> list;
        private Context mContext;

        public SubjectAdapter(List<SubjectStaffResponse.SubjectData> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject_staff, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final SubjectStaffResponse.SubjectData item = list.get(position);
            holder.txt_name.setText(item.getName());
            holder.txt_count.setText(item.getStaffNameFormatted());
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    txtEmpty.setText("No Subject found.");
                } else {
                    txtEmpty.setText("");
                }

                return list.size();
            } else {
                txtEmpty.setText("No Subject found.");
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.txt_name)
            TextView txt_name;

            @Bind(R.id.txt_count)
            TextView txt_count;
            @Bind(R.id.img_tree)
            ImageView img_tree;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                img_tree.setVisibility(View.VISIBLE);
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

    private void onTreeClick(SubjectStaffResponse.SubjectData classData) {
        Intent intent = new Intent(getActivity(), HWListActivity.class);
        intent.putExtra("team_id",team_id);
        intent.putExtra("className",className);
        intent.putExtra("subject_id",classData.subjectId);
        intent.putExtra("subject_name",classData.name);
        intent.putExtra("canPost",classData.canPost);
        intent.putExtra("title",classData.name);
        startActivity(intent);
    }
}
