package school.campusconnect.fragments;

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
import school.campusconnect.activities.ArchiveTeamActivity;
import school.campusconnect.adapters.ArchiveTeamAdapter;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.datamodel.teamdiscussion.MyTeamsResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;

public class ArchiveTeamFragment extends BaseFragment implements LeafManager.OnCommunicationListener, ArchiveTeamAdapter.ArchiveTeamListener {

    private static final String TAG = "ArchiveTeamFragment";
    @Bind(R.id.rvTeams)
    RecyclerView rvTeams;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    View view;

    String groupId;

    LeafManager leafManager=new LeafManager();

    ArrayList<MyTeamData> listTeams=new ArrayList<>();

    ArchiveTeamAdapter archiveTeamAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_archive_team,container,false);
        ButterKnife.bind(this,view);

        init_();

        getArchiveList();

        return view;
    }

    private void getArchiveList() {
        showLoadingBar(progressBar,false);
        leafManager.getArchiveTeams(this,groupId);
    }

    private void init_() {
        groupId=getArguments().getString("id","");
        AppLog.e(TAG,"groupId ; "+groupId);

        rvTeams.setLayoutManager(new LinearLayoutManager(getActivity()));
        archiveTeamAdapter=new ArchiveTeamAdapter(listTeams,this);
        rvTeams.setAdapter(archiveTeamAdapter);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
        switch (apiId)
        {
            case LeafManager.API_ID_ARCHIVE_TEAM:
                MyTeamsResponse res = (MyTeamsResponse) response;
                listTeams.clear();
                listTeams.addAll(res.getResults());
                archiveTeamAdapter.notifyDataSetChanged();
                AppLog.e(TAG, "API_ID_GET_ARCHIVE_TEAM res : " + res);
                break;
            case LeafManager.API_ID_RESTORE_ARCHIVE_TEAM:
                getArchiveList();
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
        ((ArchiveTeamActivity)getActivity()).setTeamPostFragment(listTeams.get(position));
    }

    @Override
    public void onRestoreClick(int position) {
        showLoadingBar(progressBar,false);
        leafManager.restoreArchiveTeam(this,groupId,listTeams.get(position).teamId);
    }
}
