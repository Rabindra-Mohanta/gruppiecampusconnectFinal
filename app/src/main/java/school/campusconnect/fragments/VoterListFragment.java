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
import school.campusconnect.activities.AddVoterActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.VoterListActivity;
import school.campusconnect.databinding.FragmentStreetListBinding;
import school.campusconnect.databinding.FragmentVoterListBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.masterList.StreetListModelResponse;
import school.campusconnect.datamodel.masterList.StreetListTBL;
import school.campusconnect.datamodel.masterList.VoterListModelResponse;
import school.campusconnect.datamodel.masterList.VoterListTBL;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class VoterListFragment extends BaseFragment implements LeafManager.OnCommunicationListener{

    public static String TAG = "VoterListFragment";
    private String team_id;

    FragmentVoterListBinding binding;
    private ArrayList<VoterListModelResponse.VoterData> voterData = new ArrayList<>();
    LeafManager manager;
    private VoterAdapter voterAdapter;
    public static VoterListFragment newInstance() {
        VoterListFragment fragment = new VoterListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_voter_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inits();
        //getDataLocaly();
    }

    private void inits() {

        manager = new LeafManager();

        if (getArguments() != null)
        {
            team_id = getArguments().getString("team_id");
        }

        voterAdapter = new VoterAdapter(voterData);
        binding.rvVoter.setAdapter(voterAdapter);

    }
    private void getDataLocaly() {

        List<VoterListTBL> voterListTBLS = VoterListTBL.getVoterListTBLAll(team_id);

        if (voterListTBLS.size() != 0)
        {
            ArrayList<VoterListModelResponse.VoterData> data = new ArrayList<>();

            for (int i= 0; i<voterListTBLS.size();i++)
            {
                VoterListTBL currentItem = voterListTBLS.get(i);

                VoterListModelResponse.VoterData voterData = new VoterListModelResponse.VoterData();

                voterData.teamId = currentItem.teamId;
                voterData.voterId = currentItem.voterId;
                voterData.serialNumber = currentItem.serialNumber;
                voterData.phone = currentItem.phone;
                voterData.name = currentItem.name;
                voterData.image = currentItem.image;
                voterData.husbandName = currentItem.husbandName;
                voterData.groupId = currentItem.groupId;
                voterData.gender = currentItem.gender;
                voterData.fatherName = currentItem.fatherName;
                voterData.email = currentItem.email;
                voterData.dob = currentItem.dob;
                voterData.bloodGroup = currentItem.bloodGroup;
                voterData.age = currentItem.age;
                voterData.address = currentItem.address;
                voterData.aadharNumber = currentItem.aadharNumber;

                data.add(voterData);
            }
            voterData.addAll(data);
            voterAdapter.notifyDataSetChanged();
        }
        else
        {
            voterListApiCall(true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        voterListApiCall(true);
    }

    private void voterListApiCall(boolean b) {
        if (b)
        {
            if (isConnectionAvailable())
            {
                showLoadingBar(binding.progressBar);
            //    binding.progressBar.setVisibility(View.VISIBLE);
                manager.voterMasterList(this, GroupDashboardActivityNew.groupId,team_id);
            }
            else
            {
                showNoNetworkMsg();
            }
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
    //    binding.progressBar.setVisibility(View.GONE);

        if (apiId == LeafManager.VOTER_MASTER_LIST)
        {
            VoterListModelResponse.VoterListRes res = (VoterListModelResponse.VoterListRes) response;

            ArrayList<VoterListModelResponse.VoterData> voterDataList = res.getData();

            if (voterDataList.size() > 0)
            {
                saveToLocally(voterDataList);
            }
        }
    }

    private void saveToLocally(ArrayList<VoterListModelResponse.VoterData> voterDataList) {

        VoterListTBL.deleteVoterList(team_id);

        for (int i=0;i<voterDataList.size();i++)
        {
            VoterListTBL voterListTBL = new VoterListTBL();

            VoterListModelResponse.VoterData data = voterDataList.get(i);
            voterListTBL.teamId = data.teamId;
            voterListTBL.voterId = data.voterId;
            voterListTBL.serialNumber = data.serialNumber;
            voterListTBL.phone = data.phone;
            voterListTBL.name = data.name;
            voterListTBL.image = data.image;
            voterListTBL.husbandName = data.husbandName;
            voterListTBL.groupId = data.groupId;
            voterListTBL.gender = data.gender;
            voterListTBL.fatherName = data.fatherName;
            voterListTBL.email = data.email;
            voterListTBL.dob = data.dob;
            voterListTBL.bloodGroup = data.bloodGroup;
            voterListTBL.age = data.age;
            voterListTBL.address = data.address;
            voterListTBL.aadharNumber = data.aadharNumber;
            voterListTBL.now = System.currentTimeMillis();

            voterListTBL.save();
        }
        voterData.addAll(voterDataList);
        voterAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        //    binding.progressBar.setVisibility(View.GONE);
        Log.e(TAG,"onFailure"+msg);
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        //    binding.progressBar.setVisibility(View.GONE);
        Log.e(TAG,"onException"+msg);

    }
    public class VoterAdapter extends RecyclerView.Adapter<VoterAdapter.ViewHolder>
    {
        List<VoterListModelResponse.VoterData> list;
        private Context mContext;

        public VoterAdapter(List<VoterListModelResponse.VoterData> list) {
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
            final VoterListModelResponse.VoterData item = list.get(position);

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
                    binding.txtEmpty.setText(getResources().getString(R.string.txt_no_voter_found));
                }
                else {
                    binding.txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                binding.txtEmpty.setText(getResources().getString(R.string.txt_no_voter_found));
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

    private void onTreeClick(VoterListModelResponse.VoterData classData) {
        /*Intent intent = new Intent(getContext(), AddVoterActivity.class);
        intent.putExtra("team_id",classData.teamId);
        intent.putExtra("edit",true);
        startActivity(intent);*/
    }
}