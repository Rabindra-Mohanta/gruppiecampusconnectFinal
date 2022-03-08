package school.campusconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.StreetListActivity;
import school.campusconnect.databinding.FragmentWorkerListBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.masterList.BoothMasterListModelResponse;
import school.campusconnect.datamodel.masterList.MasterBoothListTBL;
import school.campusconnect.datamodel.masterList.WorkerListResponse;
import school.campusconnect.datamodel.masterList.WorkerListTBL;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;


public class WorkerListFragment extends BaseFragment implements LeafManager.OnCommunicationListener{

    public static String TAG = "WorkerListFragment";
    FragmentWorkerListBinding binding;
    LeafManager manager;
    String committeeID,teamID;
    ArrayList<WorkerListResponse.WorkerData> workerDataArrayList = new ArrayList<>();
    WorkerAdapter workerAdapter;
    public static WorkerListFragment newInstance() {
        WorkerListFragment fragment = new WorkerListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_worker_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inits();

        getDataLocaly();
    }

    private void getDataLocaly() {

        List<WorkerListTBL> list = WorkerListTBL.getWorkerList(teamID);

        if (list.size() != 0)
        {
            ArrayList<WorkerListResponse.WorkerData> workerData1 = new ArrayList<>();

            for (int i = 0;i<list.size();i++)
            {
                WorkerListTBL currentItem = list.get(i);
                WorkerListResponse.WorkerData workerData = new WorkerListResponse.WorkerData();
                workerData.voterId = currentItem.voterId;
                workerData.userId = currentItem.userId;
                workerData.roleOnConstituency = currentItem.roleOnConstituency;
                workerData.phone = currentItem.phone;
                workerData.name = currentItem.name;
                workerData.image = currentItem.image;
                workerData.gender = currentItem.gender;
                workerData.dob = currentItem.dob;
                workerData.bloodGroup = currentItem.bloodGroup;
                workerData.allowedToAddUser = currentItem.allowedToAddUser;
                workerData.allowedToAddTeamPostComment = currentItem.allowedToAddTeamPostComment;
                workerData.allowedToAddTeamPost = currentItem.allowedToAddTeamPost;
                workerData.address = currentItem.address;
                workerData.aadharNumber = currentItem.aadharNumber;
                workerData1.add(workerData);
            }
            workerDataArrayList.addAll(workerData1);
            workerAdapter.notifyDataSetChanged();
        }
        else
        {
            workerListApi(true);
        }

    }

    private void inits() {

        manager = new LeafManager();

        workerAdapter = new WorkerAdapter(workerDataArrayList);
        binding.rvWorkers.setAdapter(workerAdapter);

        Bundle bundle=getArguments();

        if(bundle!=null)
        {
            teamID =bundle.getString("team_id","");
            committeeID =bundle.getString("committee_id","");

            Log.e(TAG,"teamID"+teamID+"\ncommitteeID"+committeeID);

        }

    }
    public void workerListApi(Boolean isCall)
    {
        if (isCall)
        {
            if(isConnectionAvailable())
            {
                binding.progressBar.setVisibility(View.VISIBLE);
                manager.getWorkerList(this,GroupDashboardActivityNew.groupId,teamID,committeeID);
            }
            else
            {
                showNoNetworkMsg();
            }
        }

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        binding.progressBar.setVisibility(View.GONE);

        if (LeafManager.WORKER_LIST == apiId)
        {
            WorkerListResponse res = (WorkerListResponse) response;
            ArrayList<WorkerListResponse.WorkerData> workerData = res.getData();

            if (workerData != null && workerData.size()>0)
            {
                saveToLocaly(workerData);
            }

        }
    }

    private void saveToLocaly(ArrayList<WorkerListResponse.WorkerData> workerData) {


        WorkerListTBL.deleteWorkerList(teamID);

        for (int i = 0;i<workerData.size();i++)
        {
            WorkerListTBL workerListTBL = new WorkerListTBL();

            workerListTBL.teamID = teamID;
            workerListTBL.now = System.currentTimeMillis();
            workerListTBL.voterId = workerData.get(i).getVoterId();
            workerListTBL.userId = workerData.get(i).getUserId();
            workerListTBL.roleOnConstituency = workerData.get(i).getRoleOnConstituency();
            workerListTBL.phone = workerData.get(i).getPhone();
            workerListTBL.name = workerData.get(i).getName();
            workerListTBL.image = workerData.get(i).getImage();
            workerListTBL.gender = workerData.get(i).getGender();
            workerListTBL.dob = workerData.get(i).getDob();
            workerListTBL.bloodGroup = workerData.get(i).getBloodGroup();
            workerListTBL.allowedToAddUser = workerData.get(i).getAllowedToAddUser();
            workerListTBL.allowedToAddTeamPostComment = workerData.get(i).getAllowedToAddTeamPostComment();
            workerListTBL.allowedToAddTeamPost = workerData.get(i).getAllowedToAddTeamPost();
            workerListTBL.address = workerData.get(i).getAddress();
            workerListTBL.aadharNumber = workerData.get(i).getAadharNumber();

            workerListTBL.save();
        }
        workerDataArrayList.addAll(workerData);

    }

    @Override
    public void onFailure(int apiId, String msg) {
        binding.progressBar.setVisibility(View.GONE);
        AppLog.e(TAG,"onFailure"+msg);
    }

    @Override
    public void onException(int apiId, String msg) {
        binding.progressBar.setVisibility(View.GONE);
        AppLog.e(TAG,"onException"+msg);
    }

    public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.ViewHolder>
    {
        List<WorkerListResponse.WorkerData> list;
        private Context mContext;

        public WorkerAdapter(List<WorkerListResponse.WorkerData> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_master_booth,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final WorkerListResponse.WorkerData item = list.get(position);

            if (!TextUtils.isEmpty(item.image)) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.image)).resize(50,50).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgTeam,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.img_lead_default.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.image)).resize(50,50).into(holder.imgTeam, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.img_lead_default.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.img_lead_default.setVisibility(View.VISIBLE);
                                        TextDrawable drawable = TextDrawable.builder()
                                                .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position));
                                        holder.img_lead_default.setImageDrawable(drawable);
                                        AppLog.e("Picasso", "Error : ");
                                    }
                                });
                            }
                        });
            } else {
                holder.img_lead_default.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position));
                holder.img_lead_default.setImageDrawable(drawable);
            }

            holder.txt_name.setText(item.name);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTreeClick(item);
                }
            });
        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    binding.txtEmpty.setText("No Workers found.");
                }
                else {
                    binding.txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                binding.txtEmpty.setText("No Workers found.");
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



            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);

            }
        }
    }

    private void onTreeClick(WorkerListResponse.WorkerData classData) {

        Intent intent = new Intent(getActivity(), StreetListActivity.class);
        intent.putExtra("teamID",teamID);
        startActivity(intent);
    }
}