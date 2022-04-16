package school.campusconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.BoothStudentActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.LeadsListActivity;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.FragmentCommitteeListBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.committee.CommitteeTBL;
import school.campusconnect.datamodel.committee.committeeResponse;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.DateTimeHelper;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.MixOperations;


public class CommitteeListFragment extends Fragment implements LeafManager.OnCommunicationListener {
public static String TAG = "CommitteeListFragment";
FragmentCommitteeListBinding binding;
private String TeamID;
private String TeamName;
private String boothClick;
private MyTeamData classData;
private LeafManager leafManager;
CommitteeAdapter adapter;
ArrayList<committeeResponse.committeeData> committeeDataList = new ArrayList<>();
int teamMemberCount = -1;

    public static CommitteeListFragment newInstance() {
        CommitteeListFragment fragment = new CommitteeListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_committee_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inits();

        getdataLocally();

    }



    private void inits() {

        leafManager = new LeafManager();

        adapter = new CommitteeAdapter();
        binding.rvCommitte.setAdapter(adapter);

        if (getArguments() != null) {
            classData = new Gson().fromJson(getArguments().getString("class_data"), MyTeamData.class);
            AppLog.e(TAG, "classData : " + classData);
            TeamID = classData.teamId;
            if(getArguments().containsKey("team_count"))
            teamMemberCount = getArguments().getInt("team_count");

            AppLog.e(TAG , "teammembercount : "+teamMemberCount);
            boothClick = getArguments().getString("isBoothClick");
            TeamName = getArguments().getString("title");
        }

        if (boothClick.equalsIgnoreCase("yes"))
        {
            binding.allCommittee.setVisibility(View.VISIBLE);
        }
        else
        {
            binding.allCommittee.setVisibility(View.GONE);
        }


        binding.allCommittee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LeadsListActivity.class);
                intent.putExtra("id", GroupDashboardActivityNew.groupId);
                intent.putExtra("team_id", classData.teamId);
                intent.putExtra("team_name", classData.name);
                intent.putExtra("class_data",new Gson().toJson(classData));
                intent.putExtra("all",true);
                if(teamMemberCount != -1)
                intent.putExtra("team_count", teamMemberCount);
                intent.putExtra("isAdmin", classData.isTeamAdmin);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();

        if(LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ADD_COMMITTEE)){
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ADD_COMMITTEE, false);
             getCommittee();
        }


    }

    private void getdataLocally() {

        List<CommitteeTBL> memberTBLS = CommitteeTBL.getMember(GroupDashboardActivityNew.groupId,TeamID);

        committeeDataList.clear();

        if (memberTBLS.size() > 0)
        {
            for (int i= 0;i<memberTBLS.size();i++)
            {
                committeeResponse.committeeData data = new committeeResponse.committeeData();

                data.committeeId = memberTBLS.get(i).committeeId;
                data.committeeName = memberTBLS.get(i).committeeName;
                data.defaultCommittee = memberTBLS.get(i).defaultCommittee;
                data._now = memberTBLS.get(i)._now;

                committeeDataList.add(data);
            }

            if(MixOperations.isNewEvent(classData.lastCommitteeForBoothUpdatedEventAt , "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" ,committeeDataList.get(0)._now ))
            {
                getCommittee();
            }
            else
            adapter.add(committeeDataList);
        }
        else
        {
            getCommittee();
        }
    }



    private void getCommittee() {
        binding.progressBar.setVisibility(View.VISIBLE);
        leafManager.getCommittee(this, GroupDashboardActivityNew.groupId,TeamID);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        binding.progressBar.setVisibility(View.GONE);

        switch (apiId)
        {
            case LeafManager.LIST_COMMITTEE:
                committeeResponse res = (committeeResponse) response;
                AppLog.e(TAG, "ClassResponse " + new Gson().toJson(res));
                saveToLocal(res.getCommitteeData());
                break;

            case LeafManager.REMOVE_COMMITTEE:
                CommitteeTBL.deleteMember(GroupDashboardActivityNew.groupId,TeamID);
                getdataLocally();
                break;
        }
    }

    private void saveToLocal(ArrayList<committeeResponse.committeeData> committeeData) {

        CommitteeTBL.deleteMember(GroupDashboardActivityNew.groupId,TeamID);

        if (committeeData.size() > 0)
        {
            for (int i = 0; i< committeeData.size();i++)
            {
                CommitteeTBL committeeTBL = new CommitteeTBL();
                committeeTBL.committeeId = committeeData.get(i).getCommitteeId();
                committeeTBL.committeeName = committeeData.get(i).getCommitteeName();
                committeeTBL.defaultCommittee = committeeData.get(i).getDefaultCommittee();
                committeeTBL.groupId = GroupDashboardActivityNew.groupId;
                committeeTBL.teamId = TeamID;
                committeeTBL._now = DateTimeHelper.getCurrentTime();
                committeeTBL.save();
            }
        }
        adapter.add(committeeData);
    }

    @Override
    public void onFailure(int apiId, String msg) {
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        binding.progressBar.setVisibility(View.GONE);
    }

    public class CommitteeAdapter extends RecyclerView.Adapter<CommitteeAdapter.ViewHolder>
    {
        List<committeeResponse.committeeData> list;
        private Context mContext;

        public void add(List<committeeResponse.committeeData> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_committee,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final CommitteeAdapter.ViewHolder holder, final int position) {

            final committeeResponse.committeeData item = list.get(position);

            holder.txtCommitteeName.setText(item.getCommitteeName());

            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(ImageUtil.getTextLetter(item.getCommitteeName()), ImageUtil.getRandomColor(position));
            holder.img_lead_default.setImageDrawable(drawable);
        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    binding.txtEmpty.setText("No Committee found.");
                }
                else {
                    binding.txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                binding.txtEmpty.setText("No Committee found.");
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {


            @Bind(R.id.txtCommitteeName)
            TextView txtCommitteeName;

            @Bind(R.id.img_tree)
            ImageView img_tree;

            @Bind(R.id.img_lead_default)
            ImageView img_lead_default;

            public ViewHolder(View itemView) {
                super(itemView);

                ButterKnife.bind(this,itemView);


                img_tree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onTreeClick(list.get(getAdapterPosition()));
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onTreeClick(list.get(getAdapterPosition()));
                    }
                });
               /* imgDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onDeleteClick(list.get(getAdapterPosition()));
                    }
                });*/

            }
        }
    }

    private void onTreeClick(committeeResponse.committeeData committeeData) {


    /*    if (boothClick != null && boothClick.equalsIgnoreCase("yes"))
        {
            Intent intent = new Intent(getActivity(), LeadsListActivity.class);
            intent.putExtra("id", GroupDashboardActivityNew.groupId);
            intent.putExtra("team_id", classData.teamId);
            intent.putExtra("team_name", classData.name);
            intent.putExtra("boothClick", "yes");
            intent.putExtra("committee_id",committeeData.getCommitteeId());
            intent.putExtra("isAdmin", classData.isTeamAdmin);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(getActivity(), BoothStudentActivity.class);
            intent.putExtra("class_data",new Gson().toJson(classData));
            intent.putExtra("committee_data",new Gson().toJson(committeeData));
            intent.putExtra("title",committeeData.getCommitteeName());
            startActivity(intent);
        }*/

        Intent intent = new Intent(getActivity(), BoothStudentActivity.class);
        intent.putExtra("class_data",new Gson().toJson(classData));
        intent.putExtra("committee_data",new Gson().toJson(committeeData));
        intent.putExtra("title",committeeData.getCommitteeName());
        startActivity(intent);
    }
    private void onDeleteClick(committeeResponse.committeeData committeeData) {
        binding.progressBar.setVisibility(View.VISIBLE);
        leafManager.removeCommittee(this,GroupDashboardActivityNew.groupId,TeamID,committeeData.getCommitteeId());
    }
}