package school.campusconnect.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.LeadsListActivity;
import school.campusconnect.activities.NestedTeamActivity;
import school.campusconnect.adapters.NestedTeamAdapter;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.datamodel.teamdiscussion.MyTeamsResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;

public class NestedTeamFragment extends BaseFragment implements LeafManager.OnCommunicationListener, NestedTeamAdapter.ArchiveTeamListener {

    private static final String TAG = "NestedTeamFragment";
    @Bind(R.id.rvTeams)
    RecyclerView rvTeams;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    View view;

    String groupId;

    LeafManager leafManager=new LeafManager();

    ArrayList<MyTeamData> listTeams=new ArrayList<>();

    NestedTeamAdapter nestedTeamAdapter;

    private String team_id;
    private String user_id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_nested_team,container,false);
        ButterKnife.bind(this,view);

        init_();

        getNestedTeam();

        return view;
    }

    private void getNestedTeam() {
        showLoadingBar(progressBar);
        leafManager.getNestedTeams(this,groupId,team_id,user_id);
    }

    private void init_() {
        groupId=getArguments().getString("group_id","");
        team_id=getArguments().getString("team_id","");
        user_id=getArguments().getString("user_id","");
        AppLog.e(TAG,"groupId ; "+groupId);
        AppLog.e(TAG,"team_id ; "+team_id);
        AppLog.e(TAG,"user_id ; "+user_id);

        rvTeams.setLayoutManager(new LinearLayoutManager(getActivity()));
        nestedTeamAdapter=new NestedTeamAdapter(listTeams,this);
        rvTeams.setAdapter(nestedTeamAdapter);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
        switch (apiId)
        {
            case LeafManager.API_ID_NESTED_TEAM:
                MyTeamsResponse res = (MyTeamsResponse) response;
                listTeams.clear();
                listTeams.addAll(res.getResults());
                nestedTeamAdapter.notifyDataSetChanged();
                AppLog.e(TAG, "API_ID_GET_ARCHIVE_TEAM res : " + res);
                break;
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        if(getActivity()==null)
            return;
        if (msg.contains("401:Unauthorized")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        if(getActivity()==null)
            return;
        Toast.makeText(getActivity(), getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int position) {
        ((NestedTeamActivity)getActivity()).setTeamPostFragment(listTeams.get(position));
    }

    @Override
    public void onTreeClick(int position) {
        Intent intent = new Intent(getActivity(), LeadsListActivity.class);
        intent.putExtra("id", groupId);
        intent.putExtra("apiCall", false);
        intent.putExtra("team_id", listTeams.get(position).teamId);
        intent.putExtra("team_name", listTeams.get(position).name);
        intent.putExtra("isAdmin", false);
        intent.putExtra("isNest", true);
        startActivity(intent);
    }
}
