
package school.campusconnect.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.LeadsListActivity;
import school.campusconnect.activities.NestedTeamActivity;
import school.campusconnect.activities.UpdateMemberActivity;
import school.campusconnect.activities.VoterProfileActivity;
import school.campusconnect.datamodel.booths.BoothMemberResponse;
import school.campusconnect.datamodel.lead.LeadDataTBL;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.utils.AppLog;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.amulyakhare.textdrawable.TextDrawable;
import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import school.campusconnect.R;
import school.campusconnect.activities.AddPostActivity;
import school.campusconnect.activities.LeadDetailActivity;
import school.campusconnect.adapters.LeadAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.LayoutListWithoutRefreshBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.LeadItem;
import school.campusconnect.datamodel.LeadResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class LeadListFragment extends BaseFragment implements LeadAdapter.OnLeadSelectListener,
        LeafManager.OnCommunicationListener {

    private static final String TAG = "LeadListFragment";
    private LayoutListWithoutRefreshBinding mBinding;
    private LeadAdapter mAdapter;
    final int REQUEST_CALL = 234;
    Intent intent;

    String groupId = "";
    private int REQUEST_UPDATE_PROFILE = 9;
    String teamId = "";
    LeafManager mManager = new LeafManager();
    public boolean mIsLoading = false;
    public int totalPages = 1;
    public int currentPage = 1;
    int count;
    public boolean allMember;
    private boolean itemClick;
    private boolean isAdmin;
    private LinearLayoutManager layoutManager;
    private List<LeadItem> list;

    private int teamMemberCount = 0;

    MyTeamData classData;

    public LeadListFragment() {

    }

    public static LeadListFragment newInstance(Bundle b) {
        LeadListFragment fragment = new LeadListFragment();
        fragment.setArguments(b);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_list_without_refresh, container, false);
        mBinding.setSize(1);
        mBinding.setMessage(R.string.msg_leads);
        ActiveAndroid.initialize(getActivity());

        initObjects();

        getToLocally();

        setListener();

        return mBinding.getRoot();
    }

    private void setListener() {
        mBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!mIsLoading && totalPages > currentPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        currentPage = currentPage + 1;
                        getToLocally();
                    }
                }
            }

        });

    }

    private void initObjects() {
        DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
        count = databaseHandler.getCount();

        groupId = getArguments().getString("id");
        teamId = getArguments().getString("team_id");
        if(getArguments().containsKey("team_count"))
        teamMemberCount = getArguments().getInt("team_count");
        else
            teamMemberCount = -1;

        classData = new Gson().fromJson(getArguments().getString("class_data"), MyTeamData.class);

        itemClick = getArguments().getBoolean("item_click", false);
        isAdmin = getArguments().getBoolean("isAdmin", false);
        allMember = getArguments().getBoolean("all",false);
        boolean isNest = getArguments().getBoolean("isNest", false);
        AppLog.e(TAG, "isAdmin is " + isAdmin);
        AppLog.e(TAG, "item_click is " + itemClick);
        AppLog.e(TAG, "teamMemberCount is " + teamMemberCount);


        if (!LeafPreference.getInstance(getContext()).getString("leadTotalPage_"+groupId+"_"+teamId).isEmpty())
        {
            totalPages = Integer.parseInt(LeafPreference.getInstance(getContext()).getString("leadTotalPage_"+groupId+"_"+teamId));
        }

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mAdapter = new LeadAdapter(new ArrayList<LeadItem>(), this, 0, databaseHandler, count, itemClick,isNest);
        mBinding.recyclerView.setAdapter(mAdapter);

        LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISUSERDELETED, false);

        if(classData !=null && classData.adminName !=null && !classData.adminName.equalsIgnoreCase(""))
        {
            mBinding.llAdmindetail.setVisibility(View.VISIBLE);
            mBinding.tvAdminname.setText(classData.adminName);

            if(classData.phone!=null && !classData.phone.equalsIgnoreCase(""))
            mBinding.imgCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    AppLog.e(TAG ,"ONCLICK called for call : "+classData.phone);
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + classData.phone));
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    }

                }
            });
        }



      /*  mBinding.swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    currentPage = 1;
                    LeadDataTBL.deleteLead(groupId,teamId);
                    mAdapter.clear();
                    getToLocally();
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                } else {
                    showNoNetworkMsg();
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                }
            }
        });*/
    }


    @Override
    public void onResume() {
        super.onResume();

        AppLog.e(TAG, "OnResume Called : " + LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISUSERDELETED));
        if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISUSERDELETED)) {
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISUSERDELETED, false);
            currentPage = 1;
            mAdapter.clear();
            LeadDataTBL.deleteLead(groupId,teamId);
            getToLocally();
        }

        if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ADD_FRIEND)) {
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ADD_FRIEND, false);
            currentPage = 1;
            mAdapter.clear();
            LeadDataTBL.deleteLead(groupId,teamId);
            getToLocally();
        }
    }


    private void getData() {
        if (isConnectionAvailable()) {
            showLoadingBar(mBinding.progressBar);
            mIsLoading = true;

            mManager.getTeamMember(this, groupId + "", teamId + "",itemClick);

        } else {
            showNoNetworkMsg();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        AppLog.e(TAG, "onRequestPermissionsResult " + requestCode);
        switch (requestCode) {
            case REQUEST_CALL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppLog.e(TAG, "gra " + requestCode);
                    if (intent != null) {
                        startActivity(intent);
                    }
                } else {
                    AppLog.e(TAG, "not gr " + requestCode);
                }
                return;
        }
    }

    @Override
    public void onCallClick(LeadItem item) {
        intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + item.getPhone()));
      /*  if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            AppLog.e(TAG, "per");
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {*/
            AppLog.e(TAG, "acti");
            startActivity(intent);
        //}
    }

    @Override
    public void onSMSClick(LeadItem item) {
        intent = new Intent(getActivity(), AddPostActivity.class);
        AppLog.e(TAG, "onSMSClick group_id " + groupId);
        intent.putExtra("id", groupId);
        intent.putExtra("friend_id", item.getId());
        intent.putExtra("friend_name", item.getName());
        intent.putExtra("type", "personal");
        intent.putExtra("team_id", teamId);
        intent.putExtra("from_chat", itemClick);
        startActivity(intent);

        if(!itemClick)
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.PERSONAL_POST_ADDED_1,true);

        getActivity().finish();
    }

    @Override
    public void onMailClick(LeadItem item) {

        if (((LeadsListActivity) getActivity()).isConnectionAvailable()) {
            Intent intent = new Intent(getActivity(), NestedTeamActivity.class);
            intent.putExtra("group_id", groupId);
            intent.putExtra("team_id", teamId);
            intent.putExtra("user_id", item.id);
            intent.putExtra("title", item.name);
            startActivity(intent);
        } else {
            ((LeadsListActivity) getActivity()).showNoNetworkMsg();
        }

    }

    @Override
    public void onNameClick(LeadItem item) {
        AppLog.e(TAG, "onNameClick()"+new Gson().toJson(item));
        if (isAdmin) {
            Intent intent = new Intent(getActivity(), LeadDetailActivity.class);
            Bundle b = new Bundle();
            b.putParcelable(LeadDetailActivity.EXTRA_LEAD_ITEM, item);
            intent.putExtras(b);
            intent.putExtra("post", item.getIsPost());
            intent.putExtra("allowedToAddUser", item.allowedToAddUser);
            intent.putExtra("allowedToAddTeamPost", item.allowedToAddTeamPost);
            intent.putExtra("allowedToAddTeamPostComment", item.allowedToAddTeamPostComment);
            intent.putExtra("type", "friend");
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("team_id", teamId);
            startActivity(intent);
        }
    }



    @Override
    public void onSuccess(int apiId, BaseResponse response) {
            hideLoadingBar();

            if (apiId == LeafManager.API_ID_LEAD_LIST)
            {
                LeadResponse res = (LeadResponse) response;


                mBinding.setSize(mAdapter.getItemCount());
                totalPages = res.totalNumberOfPages;

                LeafPreference.getInstance(getContext()).setString("leadTotalPage_"+groupId+"_"+teamId,String.valueOf(totalPages));
                mIsLoading = false;

                saveToLocally(res.getResults());
            }



    }

    private void saveToLocally(List<LeadItem> results) {

        LeadDataTBL.deleteLead(groupId,teamId,currentPage);

        if (results.size() > 0)
        {
            list = new ArrayList<>();
            for (int i = 0 ;i <results.size();i++)
            {
                LeadDataTBL leadDataTBL = new LeadDataTBL();

                leadDataTBL.groupID = groupId;
                leadDataTBL.teamID = teamId;
                leadDataTBL.page = currentPage;
                leadDataTBL.voterId = results.get(i).voterId;
                leadDataTBL.id = results.get(i).id;
                leadDataTBL.roleOnConstituency = results.get(i).roleOnConstituency;
                leadDataTBL.phone = results.get(i).phone;
                leadDataTBL.name = results.get(i).name;
                leadDataTBL.image = results.get(i).image;
                leadDataTBL.gender = results.get(i).gender;
                leadDataTBL.dob = results.get(i).dob;
                leadDataTBL.bloodGroup = results.get(i).bloodGroup;
                leadDataTBL.allowedToAddUser = results.get(i).allowedToAddUser;
                leadDataTBL.allowedToAddTeamPostComment = results.get(i).allowedToAddTeamPostComment;
                leadDataTBL.allowedToAddTeamPost = results.get(i).allowedToAddTeamPost;
                leadDataTBL.aadharNumber = results.get(i).aadharNumber;

                leadDataTBL.save();

            }
            list.addAll(results);
        }

        mAdapter.addItems(results);
        mAdapter.notifyDataSetChanged();
    }


    private void getToLocally() {

        List<LeadDataTBL> leadDataTBL = LeadDataTBL.getLead(groupId,teamId,currentPage);

        mAdapter.clear();

        if (leadDataTBL.size() > 0 &&  ( leadDataTBL.size() == teamMemberCount  || teamMemberCount == -1))
        {
            list = new ArrayList<>();

            for (int i = 0;i<leadDataTBL.size();i++)
            {
                LeadItem leadItem = new LeadItem();
                leadItem.id = leadDataTBL.get(i).id;
                leadItem.voterId = leadDataTBL.get(i).voterId;
                leadItem.roleOnConstituency = leadDataTBL.get(i).roleOnConstituency;
                leadItem.phone = leadDataTBL.get(i).phone;
                leadItem.name = leadDataTBL.get(i).name;
                leadItem.image = leadDataTBL.get(i).image;
                leadItem.gender = leadDataTBL.get(i).gender;
                leadItem.dob = leadDataTBL.get(i).dob;
                leadItem.bloodGroup = leadDataTBL.get(i).bloodGroup;
                leadItem.allowedToAddUser = leadDataTBL.get(i).allowedToAddUser;
                leadItem.allowedToAddTeamPostComment = leadDataTBL.get(i).allowedToAddTeamPostComment;
                leadItem.allowedToAddTeamPost = leadDataTBL.get(i).allowedToAddTeamPost;
                leadItem.aadharNumber = leadDataTBL.get(i).aadharNumber;

                list.add(leadItem);
            }
            mAdapter.addItems(list);
            mBinding.setSize(mAdapter.getItemCount());
            mAdapter.notifyDataSetChanged();
        }
        else
        {
            getData();
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();

        if (apiId == LeafManager.API_ID_LEAD_LIST)
        {
            mIsLoading = false;
            currentPage = currentPage - 1;
            if (currentPage < 0) {
                currentPage = 1;
            }
        }

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

        if (apiId == LeafManager.API_ID_LEAD_LIST)
        {
            mIsLoading = false;
            currentPage = currentPage - 1;
            if (currentPage < 0) {
                currentPage = 1;
            }
        }

        try {
            Toast.makeText(getActivity(), getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            AppLog.e("LeadListFragment", "OnExecption : " + msg);
        }
    }

    public void refreshData(List<LeadItem> result) {
        hideLoadingBar();
        mAdapter.clear();
        mAdapter.addItems(result);
        mAdapter.notifyDataSetChanged();
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.setSize(mAdapter.getItemCount());
        totalPages = 0;
        mIsLoading = false;
    }

    public void search(String strsearch) {
        if(list!=null){
            if(!TextUtils.isEmpty(strsearch)){
                ArrayList<LeadItem> newList = new ArrayList<>();
                for (int i=0;i<list.size();i++){
                    if(list.get(i).name.toLowerCase().contains(strsearch.toLowerCase())){
                        newList.add(list.get(i));
                    }
                }
                mAdapter.clear();
                mAdapter.addItems(newList);
                mAdapter.notifyDataSetChanged();
            }else {
                mAdapter.clear();
                mAdapter.addItems(list);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_UPDATE_PROFILE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                Intent i = new Intent(getContext(), GroupDashboardActivityNew.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        }
    }
}
