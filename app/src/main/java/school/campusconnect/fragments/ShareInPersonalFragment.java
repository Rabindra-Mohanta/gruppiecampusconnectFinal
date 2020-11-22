package school.campusconnect.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import school.campusconnect.utils.AppLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;

import java.util.ArrayList;

import school.campusconnect.R;
import school.campusconnect.activities.SharePersonalNameListActivity;
import school.campusconnect.adapters.ShareOnlyGroupAdapter;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.LayoutListButtonBinding;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.sharepost.ShareGroupItem;
import school.campusconnect.datamodel.sharepost.ShareGroupResponse;
import school.campusconnect.datamodel.teamdiscussion.TeamPostGetItem;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;

/**
 * Created by frenzin04 on 2/7/2017.
 */

public class ShareInPersonalFragment extends BaseFragment implements LeafManager.OnCommunicationListener,
        DialogInterface.OnClickListener, LeafManager.OnAddUpdateListener<AddPostValidationError>, ShareOnlyGroupAdapter.FinishActivity {

    private LayoutListButtonBinding mBinding;
    private ShareOnlyGroupAdapter mAdapter;
    LeafManager manager = new LeafManager();
    String mGroupId ="";
    String mPostId = "";
    public boolean mIsLoading = false;
    public TeamPostGetItem currentItem;
    ArrayList<ShareGroupItem> list = new ArrayList<ShareGroupItem>();
    boolean isScroll = false;
    public int totalPages = 1;
    public int currentPage = 1;

    public ShareInPersonalFragment() {

    }

    public static ShareInPersonalFragment newInstance(String groupId, String post_id) {
        ShareInPersonalFragment fragment = new ShareInPersonalFragment();
        Bundle b = new Bundle();
        b.putString("id", groupId);
        b.putString("post_id", post_id);
       AppLog.e("SHARE", "Post id4 is " + post_id);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_list_button, container, false);
        mBinding.setSize(1);
        mBinding.setMessage(R.string.msg_no_groups);
        mGroupId = getArguments().getString("id");
        mPostId = getArguments().getString("post_id");
       AppLog.e("SHAREDATA", "3group_id " + mGroupId);
       AppLog.e("SHAREDATA", "3post_id " + mPostId);
        final LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView.setLayoutManager(manager);

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

               AppLog.e("Count", "visibleItemCount " + visibleItemCount);
               AppLog.e("Count", "totalItemCount " + totalItemCount);
               AppLog.e("Count", "firstVisibleItemPosition " + firstVisibleItemPosition);
               AppLog.e("Count", "lastVisibleItemPosition " + lastVisibleItemPosition);

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
//        manager.getGroupListShare(this);
        manager.getGroupTeamListShare(this, currentPage);
    }

    private void shareData() {
       /* showLoadingBar(mBinding.progressBar);
        mIsLoading = true;
        manager.shareGroupPost(this, null, mGroupId, mPostId, mAdapter.getSelectedgroups(), "group");*/

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

                if (currentPage == 1) {
                    list.clear();
                }

                for (int i = 0; i < res.data.size(); i++) {
                    ShareGroupItem shareGroupItemList = new ShareGroupItem();
                    shareGroupItemList.id = res.data.get(i).id;
                    shareGroupItemList.name = res.data.get(i).name;
                    shareGroupItemList.image = res.data.get(i).image;
                    list.add(shareGroupItemList);
                }
                totalPages = res.totalPages;
                mIsLoading = false;
                if (currentPage == 1) {
                    mAdapter = new ShareOnlyGroupAdapter(list, this, mGroupId, mPostId);
                    mBinding.recyclerView.setAdapter(mAdapter);
                } else {
                    mAdapter.addItems(res.getResults());
                    mAdapter.notifyDataSetChanged();
                }
               AppLog.e("adas ", mAdapter.getItemCount() + "");
               AppLog.e("ReportResponse", "onSuccess, msg : " + (res).data.toString());
                break;

            case LeafManager.API_SHARE:
                hideLoadingBar();
                Toast.makeText(getActivity(), "Shared successfully", Toast.LENGTH_SHORT).show();
                getActivity().finish();
                break;
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {

    }

    @Override
    public void onException(int apiId, String msg) {

    }

    public void finish() {
        getActivity().finish();
    }

    @Override
    public void onClickListener(String id) {
        Intent intent = new Intent(getActivity(), SharePersonalNameListActivity.class);
        intent.putExtra("id", mGroupId);
        intent.putExtra("post_id", mPostId);
        intent.putExtra("selected_group_id", id);
        intent.putExtra("type", "personal");

       AppLog.e("SHAREDATA", "4group_id " + mGroupId);
       AppLog.e("SHAREDATA", "4post_id " + mPostId);
       AppLog.e("SHAREDATA", "4selected_group_id " + id);
//                    finishActivity.finish();
       AppLog.e("SHARE", "Post id2 is " + mPostId);
        startActivity(intent);
    }

    public void onClickAddComment() {
       /* if (mAdapter.getSelectedgroups().equals(""))
            Toast.makeText(getActivity(), "Select any group first...", Toast.LENGTH_SHORT).show();
        else
            shareData();*/
    }

    public void callFilter(String s, int count) {

    }
}
