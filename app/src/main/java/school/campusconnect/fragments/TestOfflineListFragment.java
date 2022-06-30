package school.campusconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.AddOfflineTestActivity;
import school.campusconnect.activities.EditOfflineTestActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.TestExamActivity;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.SubjectCountTBL;
import school.campusconnect.datamodel.SubjectItem;
import school.campusconnect.datamodel.subjects.SubjectStaffResponse;
import school.campusconnect.datamodel.test_exam.OfflineTestRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;

public class TestOfflineListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    String team_id;
    String className;
    String role;
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
        className = getArguments().getString("title");
        role = getArguments().getString("role");

        init();

        getDataLocally();

        return view;
    }

    private void init() {
        /*swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    callAPi();
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    showNoNetworkMsg();
                }
            }
        });*/
    }

    private void getDataLocally() {
        callAPi(true);
    }

    private void callAPi(boolean isLoading) {
        if (isLoading)
            //showLoadingBar(progressBar);
            progressBar.setVisibility(View.VISIBLE);
        LeafManager leafManager = new LeafManager();
        leafManager.getOfflineTestList(this, GroupDashboardActivityNew.groupId, team_id);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(LeafPreference.getInstance(getActivity()).getBoolean("is_offline_test_added")){
            LeafPreference.getInstance(getActivity()).setBoolean("is_offline_test_added", false);
            getDataLocally();
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        //hideLoadingBar();
        progressBar.setVisibility(View.GONE);
        OfflineTestRes res = (OfflineTestRes) response;
        ArrayList<OfflineTestRes.ScheduleTestData> result = res.data;
        AppLog.e(TAG, "ClassResponse " + result);

        rvClass.setAdapter(new SubjectAdapter(result));

    }

    @Override
    public void onFailure(int apiId, String msg) {
        //hideLoadingBar();
        progressBar.setVisibility(View.GONE);
        txtEmpty.setText(getResources().getString(R.string.txt_no_test_found));
    }

    @Override
    public void onException(int apiId, String msg) {
        //hideLoadingBar();
        progressBar.setVisibility(View.GONE);
        txtEmpty.setText(getResources().getString(R.string.txt_no_test_found));
    }

    public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {
        List<OfflineTestRes.ScheduleTestData> list;
        private Context mContext;

        public SubjectAdapter(List<OfflineTestRes.ScheduleTestData> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject_staff_2, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final OfflineTestRes.ScheduleTestData item = list.get(position);
            holder.txt_name.setText(item.title);
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    txtEmpty.setText(getResources().getString(R.string.txt_no_test_found));
                } else {
                    txtEmpty.setText("");
                }

                return list.size();
            } else {
                txtEmpty.setText(getResources().getString(R.string.txt_no_test_found));
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

    private void onTreeClick(OfflineTestRes.ScheduleTestData classData) {
        Intent intent = new Intent(getActivity(), EditOfflineTestActivity.class);
        intent.putExtra("groupId", GroupDashboardActivityNew.groupId);
        intent.putExtra("teamId", team_id);
        intent.putExtra("title", className);
        intent.putExtra("role", role);
        intent.putExtra("classData", new Gson().toJson(classData));
        startActivity(intent);

    }
}
