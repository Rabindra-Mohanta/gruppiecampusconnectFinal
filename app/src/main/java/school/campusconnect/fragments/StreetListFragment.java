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
import school.campusconnect.activities.VoterListActivity;
import school.campusconnect.activities.WorkerListActivity;
import school.campusconnect.curl.CurlMesh;
import school.campusconnect.databinding.FragmentStreetListBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.masterList.BoothMasterListModelResponse;
import school.campusconnect.datamodel.masterList.StreetListModelResponse;
import school.campusconnect.datamodel.masterList.StreetListTBL;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;


public class StreetListFragment extends BaseFragment implements LeafManager.OnCommunicationListener{

    public static String TAG = "StreetListFragment";
    FragmentStreetListBinding binding;
    String teamId;
    LeafManager manager;
    private ArrayList<StreetListModelResponse.StreetData> streetDataArrayList = new ArrayList<>();
    private StreetAdapter streetAdapter;

    public static StreetListFragment newInstance() {
        StreetListFragment fragment = new StreetListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_street_list, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inits();

        getDataLocally();
    }

    private void getDataLocally() {

        List<StreetListTBL> streetListTBLS = StreetListTBL.getStreetListAll(teamId);

        if (streetListTBLS.size() != 0)
        {
            ArrayList<StreetListModelResponse.StreetData> data = new ArrayList<>();

            for (int i= 0; i<streetListTBLS.size();i++)
            {
                StreetListTBL currentItem = streetListTBLS.get(i);

                StreetListModelResponse.StreetData streetData= new StreetListModelResponse.StreetData();

                streetData.voterId = currentItem.voterId;
                streetData.userId = currentItem.userId;
                streetData.roleOnConstituency = currentItem.roleOnConstituency;
                streetData.phone = currentItem.phone;
                streetData.name = currentItem.name;
                streetData.image = currentItem.image;
                streetData.gender = currentItem.gender;
                streetData.dob = currentItem.dob;
                streetData.bloodGroup = currentItem.bloodGroup;
                streetData.allowedToAddUser = currentItem.allowedToAddUser;
                streetData.allowedToAddTeamPostComment = currentItem.allowedToAddTeamPostComment;
                streetData.allowedToAddTeamPost = currentItem.allowedToAddTeamPost;
                streetData.address = currentItem.address;
                streetData.aadharNumber = currentItem.aadharNumber;
                data.add(streetData);
            }
            streetDataArrayList.addAll(data);
            streetAdapter.notifyDataSetChanged();

        }
        else
        {
            streetListApiCall(true);
        }

    }

    private void streetListApiCall(boolean b) {
        if (b)
        {
            if (isConnectionAvailable())
            {
                showLoadingBar(binding.progressBar);
            //    binding.progressBar.setVisibility(View.VISIBLE);
                manager.getWorkerStreetList(this, GroupDashboardActivityNew.groupId,teamId,"masterList");
            }
            else
            {
                showNoNetworkMsg();
            }
        }
    }

    private void inits() {

        manager = new LeafManager();

        Bundle bundle=getArguments();

        if(bundle!=null)
        {
            teamId = getArguments().getString("teamID");
        }
        streetAdapter = new StreetAdapter(streetDataArrayList);
        binding.rvStreets.setAdapter(streetAdapter);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
       // binding.progressBar.setVisibility(View.GONE);

        if (LeafManager.WORKER_STREET_LIST == apiId)
        {
            StreetListModelResponse res = (StreetListModelResponse) response;

            ArrayList<StreetListModelResponse.StreetData> resList= res.getData();

            if (resList.size()>0)
            {
                saveToLocally(resList);
            }
        }
    }

    private void saveToLocally(ArrayList<StreetListModelResponse.StreetData> resList) {

        StreetListTBL.deleteStreetList(teamId);

        for (int i=0;i<resList.size();i++)
        {
            StreetListTBL streetListTBL = new StreetListTBL();

            StreetListModelResponse.StreetData data = resList.get(i);
            streetListTBL.teamId = teamId;
            streetListTBL.now = System.currentTimeMillis();
            streetListTBL.voterId = data.voterId;
            streetListTBL.userId =data.userId;
            streetListTBL.roleOnConstituency = data.roleOnConstituency;
            streetListTBL.phone = data.phone;
            streetListTBL.name = data.name;
            streetListTBL.image = data.image;
            streetListTBL.gender =  data.gender;
            streetListTBL.dob =  data.dob;
            streetListTBL.bloodGroup = data.bloodGroup;
            streetListTBL.allowedToAddUser =data.allowedToAddUser;
            streetListTBL.allowedToAddTeamPostComment = data.allowedToAddTeamPostComment;
            streetListTBL.allowedToAddTeamPost = data.allowedToAddTeamPost;
            streetListTBL.address = data.address;
            streetListTBL.aadharNumber = data.aadharNumber;

            streetListTBL.save();
        }
        streetDataArrayList.addAll(resList);
        streetAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        // binding.progressBar.setVisibility(View.GONE);
        Log.e(TAG,"onFailure"+msg);

    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        // binding.progressBar.setVisibility(View.GONE);
        Log.e(TAG,"onException"+msg);
    }

    public class StreetAdapter extends RecyclerView.Adapter<StreetAdapter.ViewHolder>
    {
        List<StreetListModelResponse.StreetData> list;
        private Context mContext;

        public StreetAdapter(List<StreetListModelResponse.StreetData> list) {
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
            final StreetListModelResponse.StreetData item = list.get(position);

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
                    binding.txtEmpty.setText(getResources().getString(R.string.txt_no_street_found));
                }
                else {
                    binding.txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                binding.txtEmpty.setText(getResources().getString(R.string.txt_no_street_found));
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

    private void onTreeClick(StreetListModelResponse.StreetData classData) {
        Intent intent = new Intent(getActivity(), VoterListActivity.class);
        intent.putExtra("team_id",teamId);
        startActivity(intent);
    }
}