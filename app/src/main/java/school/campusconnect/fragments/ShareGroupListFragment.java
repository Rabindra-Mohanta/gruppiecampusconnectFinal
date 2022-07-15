package school.campusconnect.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.utils.AppLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;

import java.util.ArrayList;

import school.campusconnect.R;
import school.campusconnect.activities.EditPostActivity;
import school.campusconnect.activities.SelectShareTypeActivity;
import school.campusconnect.activities.ShareGroupListActivity;
import school.campusconnect.activities.ShareGroupTeamListActivity;
import school.campusconnect.adapters.ShareGroupAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.LayoutListShareGroupBinding;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.sharepost.ShareGroupItemList;
import school.campusconnect.datamodel.sharepost.ShareGroupResponse;
import school.campusconnect.datamodel.teamdiscussion.TeamPostGetItem;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;

/**
 * Created by frenzin04 on 2/6/2017.
 */

public class ShareGroupListFragment extends BaseFragment implements LeafManager.OnCommunicationListener,
        DialogInterface.OnClickListener, LeafManager.OnAddUpdateListener<AddPostValidationError> {

    private LayoutListShareGroupBinding mBinding;
    public ShareGroupAdapter mAdapter;
    LeafManager manager = new LeafManager();
    String selected_group_id = "";
    String mGroupId ="";
    String mPostId = "";
    int share = 0;
    public boolean mIsLoading = false;
    public String team_id;// = 1;
    public TeamPostGetItem currentItem;
    ArrayList<ShareGroupItemList> list = new ArrayList<ShareGroupItemList>();
    boolean isTeam = false;
    String type;
    boolean isScroll = false;
    public int totalPages = 1;
    public int currentPage = 1;

    public ShareGroupListFragment() {

    }

    public static ShareGroupListFragment newInstance(String groupId, String post_id, String selected_group_id, String type, int share) {
        ShareGroupListFragment fragment = new ShareGroupListFragment();
        Bundle b = new Bundle();
        b.putString("id", groupId);
        b.putString("post_id", post_id);
        b.putString("selected_group_id", selected_group_id);
        b.putInt("share", share);
        b.putString("type", type);
       AppLog.e("SHARE", "Post id4 is " + post_id);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_list_share_group, container, false);
        mBinding.setSize(1);
        mBinding.setMessage(R.string.msg_no_groups);
        mGroupId = getArguments().getString("id");
        mPostId = getArguments().getString("post_id");
        share = getArguments().getInt("share", 0);
        selected_group_id = getArguments().getString("selected_group_id");
        type = getArguments().getString("type", "");
       AppLog.e("SHARE", "Post id5 is " + mPostId);
        final LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView.setLayoutManager(manager);

        isTeam = type.equals("team");

       AppLog.e("SHAREDATA", "6group_id " + mGroupId);
       AppLog.e("SHAREDATA", "6post_id " + mPostId);
       AppLog.e("SHAREDATA", "6selected_group_id " + selected_group_id);

        getData();

        mBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = manager.getChildCount();
                int totalItemCount = manager.getItemCount();
                int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = manager.findLastVisibleItemPosition();

//               AppLog.e("Count", "visibleItemCount " + visibleItemCount);
//               AppLog.e("Count", "totalItemCount " + totalItemCount);
//               AppLog.e("Count", "firstVisibleItemPosition " + firstVisibleItemPosition);
//               AppLog.e("Count", "lastVisibleItemPosition " + lastVisibleItemPosition);

                if (!mIsLoading && totalPages > currentPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            ) {
                        currentPage = currentPage + 1;
                       AppLog.e("GeneralPostScroll", "onScrollCalled " + currentPage);
                        isScroll = true;
                        getData();
                    }
                }
            }
        });

        mBinding.swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    isScroll = false;
                    currentPage = 1;
                    getData();
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                } else {
                    showNoNetworkMsg();
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        return mBinding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
       AppLog.e("TeamPost", "OnResume : " + LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISTEAMPOSTUPDATED));
        if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISTEAMPOSTUPDATED)) {
            mAdapter.clear();
            getData();
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISTEAMPOSTUPDATED, false);
        }
    }

    private void getData() {
        //mAdapter.clear();
        showLoadingBar(mBinding.progressBar);
        mIsLoading = true;
        if (isTeam)
            manager.getSelectTeamListShare(this, selected_group_id, currentPage);
        else
            manager.getGroupListShare(this);

    }

    private void shareData() {
        showLoadingBar(mBinding.progressBar);
        mIsLoading = true;
        if (GroupDashboardActivityNew.share_type.equals("group")) {
            if (isTeam)
                manager.shareGroupPost(this, null, mGroupId+"", mPostId+"", mAdapter.getSelectedgroups(), selected_group_id+"", "team");
            else
                manager.shareGroupPost(this, null, mGroupId+"", mPostId+"", mAdapter.getSelectedgroups(), "0", type);
        } else if (GroupDashboardActivityNew.share_type.equals("team")) {
            if (isTeam)
                manager.shareTeamPost(this, null, mGroupId+"", GroupDashboardActivityNew.team_id+"", mPostId+"", mAdapter.getSelectedgroups(), selected_group_id+"", "team");
            else
                manager.shareTeamPost(this, null, mGroupId+"", GroupDashboardActivityNew.team_id+"", mPostId+"", mAdapter.getSelectedgroups(), "0", type);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<AddPostValidationError> error) {
        hideLoadingBar();

        try {
           AppLog.e("GeneralPostFragment", "onFailure  ,, msg : " + error);
            if (error.status.equals("401")) {
                Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
                logout();
            } else {
                Toast.makeText(getActivity(), error.title, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){}

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        switch (apiId) {
            case LeafManager.API_SHARE_GROUP_LIST:
                hideLoadingBar();
                ShareGroupResponse res = (ShareGroupResponse) response;
                ArrayList<ShareGroupItemList> listNew = new ArrayList<ShareGroupItemList>();

                if (currentPage == 1) {
                    list.clear();
                }

                for (int i = 0; i < res.data.size(); i++) {
                    ShareGroupItemList shareGroupItemList = new ShareGroupItemList(res.data.get(i).id+"", res.data.get(i).name, res.data.get(i).image, res.data.get(i).phone, false);
                    list.add(shareGroupItemList);
                    listNew.add(shareGroupItemList);
                }

                if (currentPage == 1) {
                    try {
                        mAdapter = new ShareGroupAdapter(list, "team", new DatabaseHandler(getActivity()).getCount());
                        mBinding.recyclerView.setAdapter(mAdapter);
                    }catch (Exception e){}

                } else {
                    mAdapter.addItems(listNew);
                    mAdapter.notifyDataSetChanged();
                }
               AppLog.e("adas ", mAdapter.getItemCount() + "");
               AppLog.e("ReportResponse", "onSuccess, msg : " + (res).data.toString());
                break;

            case LeafManager.API_SHARE:
                Constants.requestCode = Constants.finishCode;
                hideLoadingBar();
                try {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_shared_successfully), Toast.LENGTH_SHORT).show();

                    if (SelectShareTypeActivity.selectShareTypeActivity != null) {
                        SelectShareTypeActivity.selectShareTypeActivity.finish();
                    }

                    if (ShareGroupTeamListActivity.shareGroupTeamListActivity != null)
                        ShareGroupTeamListActivity.shareGroupTeamListActivity.finish();

                    getActivity().finish();

                }catch (Exception e){}

                break;
        }

        if(getActivity()!=null)
        {
            if(getActivity() instanceof ShareGroupListActivity)
            {
                ((ShareGroupListActivity)getActivity()).updateCount();
            }
        }


        hide_keyboard();

    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();

    }

    @Override
    public void onException(int apiId, String msg) {

    }

    public void onClickAddComment() {
        if (mAdapter.getSelectedgroups().equals(""))
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_select_any_group_first), Toast.LENGTH_SHORT).show();
        else {
            if (GroupDashboardActivityNew.is_share_edit) {
                Constants.requestCode = Constants.notFinishCode;
//                getActivity().finish();
                Intent intent = new Intent(getActivity(), EditPostActivity.class);
                intent.putExtra("id", mGroupId);
                intent.putExtra("post_id", mPostId);
                intent.putExtra("selected_g_id", selected_group_id);
                intent.putExtra("selected_ids", mAdapter.getSelectedgroups());
                intent.putExtra("title", GroupDashboardActivityNew.share_title);
                intent.putExtra("desc", GroupDashboardActivityNew.share_desc);
               AppLog.e("desc", "desc is " + GroupDashboardActivityNew.share_desc);
                intent.putExtra("type", type);
                intent.putExtra("sharetype", GroupDashboardActivityNew.share_type);
                startActivity(intent);
            } else
                shareData();
        }
    }
}