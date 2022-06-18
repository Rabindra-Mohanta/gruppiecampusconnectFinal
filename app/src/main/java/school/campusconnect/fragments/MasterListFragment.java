package school.campusconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.BoothCoordinateActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.WorkerListActivity;
import school.campusconnect.databinding.FragmentMasterListBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ClassListTBL;
import school.campusconnect.datamodel.TeamCountTBL;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.masterList.BoothMasterListModelResponse;
import school.campusconnect.datamodel.masterList.MasterBoothListTBL;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.MixOperations;


public class MasterListFragment extends BaseFragment implements LeafManager.OnCommunicationListener{

    public static final String TAG = "MasterListFragment";
    FragmentMasterListBinding binding;
    LeafManager manager;
    BoothAdapter boothAdapter;
    ArrayList<BoothMasterListModelResponse.BoothMasterListData> boothMasterListData = new ArrayList<>();
    public static MasterListFragment newInstance() {
        MasterListFragment fragment = new MasterListFragment();
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
        binding =  DataBindingUtil.inflate(inflater,R.layout.fragment_master_list, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inits();

        getDataLocaly();
    }

    private void getDataLocaly() {

        List<MasterBoothListTBL> list = MasterBoothListTBL.getMasterBoothList(GroupDashboardActivityNew.groupId);

        if (list.size() != 0) {

            ArrayList<BoothMasterListModelResponse.BoothMasterListData> result = new ArrayList<>();

            for (int i = 0; i < list.size(); i++) {

                MasterBoothListTBL currentItem = list.get(i);

                BoothMasterListModelResponse.BoothMasterListData item = new BoothMasterListModelResponse.BoothMasterListData();

                item.teamId = currentItem.teamId;
                item.phone = currentItem.phone;
                item.name = currentItem.name;
                item.isTeamAdmin = currentItem.isTeamAdmin;
                item.image = currentItem.image;
                item.groupId = currentItem.groupId;
                item.category =currentItem.category;
                item.canAddUser = currentItem.canAddUser;
                item.boothId = currentItem.boothId;
                item.boothCommittee = new Gson().fromJson(currentItem.boothCommittee, new TypeToken<BoothMasterListModelResponse.boothCommitteeData>() {}.getType());
                item.adminName = currentItem.adminName;
                result.add(item);
            }

            boothMasterListData.addAll(result);
            boothAdapter.notifyDataSetChanged();

        } else {
            masterBoothListApi(true);
        }
    }

    private void inits() {

        manager = new LeafManager();

        boothAdapter = new BoothAdapter(boothMasterListData);
        binding.rvBooths.setAdapter(boothAdapter);

    }

    public void masterBoothListApi(Boolean isCall)
    {
        if (isCall)
        {
            if(isConnectionAvailable())
            {
                showLoadingBar(binding.progressBar);
             //   binding.progressBar.setVisibility(View.VISIBLE);
                manager.getMasterBoothList(this,GroupDashboardActivityNew.groupId,"masterList");
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
       // binding.progressBar.setVisibility(View.GONE);

        if (apiId == LeafManager.MASTER_BOOTH_LIST)
        {
            BoothMasterListModelResponse res = (BoothMasterListModelResponse) response;

            if (res != null && res.getData().size()>0)
            {
                saveToLocaly(res);
            }
        }
    }

    private void saveToLocaly(BoothMasterListModelResponse res) {

        ArrayList<BoothMasterListModelResponse.BoothMasterListData> result = res.getData();

        MasterBoothListTBL.deleteMasterBoothList(GroupDashboardActivityNew.groupId);

        for (int i = 0;i<result.size();i++)
        {
            MasterBoothListTBL masterBoothListTBL = new MasterBoothListTBL();

            BoothMasterListModelResponse.BoothMasterListData data = result.get(i);

            masterBoothListTBL.teamId = data.getTeamId();
            masterBoothListTBL.phone = data.getPhone();
            masterBoothListTBL.name = data.getName();
            masterBoothListTBL.isTeamAdmin = data.getTeamAdmin();
            masterBoothListTBL.image = data.getImage();
            masterBoothListTBL.groupId = data.getGroupId();
            masterBoothListTBL.category = data.getCategory();
            masterBoothListTBL.canAddUser = data.getCanAddUser();
            masterBoothListTBL.boothId = data.getBoothId();
            masterBoothListTBL.boothCommittee = new Gson().toJson(data.getBoothCommittee());
            masterBoothListTBL.adminName = data.getAdminName();
            masterBoothListTBL.now = System.currentTimeMillis();

            masterBoothListTBL.save();
        }

        boothMasterListData.addAll(result);
        boothAdapter.notifyDataSetChanged();

    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        // binding.progressBar.setVisibility(View.GONE);
        AppLog.e(TAG,"on Exception"+msg);
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        // binding.progressBar.setVisibility(View.GONE);
        AppLog.e(TAG,"on Failure"+msg);

    }

    public class BoothAdapter extends RecyclerView.Adapter<BoothAdapter.ViewHolder>
    {
        List<BoothMasterListModelResponse.BoothMasterListData> list;
        private Context mContext;

        public BoothAdapter(List<BoothMasterListModelResponse.BoothMasterListData> list) {
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
            final BoothMasterListModelResponse.BoothMasterListData item = list.get(position);

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

                    binding.txtEmpty.setText(getResources().getString(R.string.txt_no_booth_found));
                }
                else {
                    binding.txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                binding.txtEmpty.setText(getResources().getString(R.string.txt_no_booth_found));
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

    private void onTreeClick(BoothMasterListModelResponse.BoothMasterListData classData) {
        Intent intent = new Intent(getActivity(), WorkerListActivity.class);
        intent.putExtra("data",classData);
        intent.putExtra("name",classData.name);
        startActivity(intent);
    }
}