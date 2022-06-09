package school.campusconnect.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.AsyncTask;
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

import com.activeandroid.ActiveAndroid;
import com.baoyz.widget.PullRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import school.campusconnect.R;
import school.campusconnect.activities.EditPostActivity;
import school.campusconnect.activities.SelectShareTypeActivity;
import school.campusconnect.activities.ShareGroupTeamListActivity;
import school.campusconnect.activities.SharePersonalNameListActivity;
import school.campusconnect.adapters.ShareGroupAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.LayoutListShareGroupBinding;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.LeadItem;
import school.campusconnect.datamodel.LeadResponse;
import school.campusconnect.datamodel.gruppiecontacts.GruppieContactsModel;
import school.campusconnect.datamodel.sharepost.ShareGroupItemList;
import school.campusconnect.datamodel.sharepost.ShareGroupResponse;
import school.campusconnect.datamodel.teamdiscussion.TeamPostGetItem;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;

/**
 * Created by frenzin04 on 2/6/2017.
 */

public class SharePersonalNameListFragment extends BaseFragment implements LeafManager.OnCommunicationListener,
        DialogInterface.OnClickListener, LeafManager.OnAddUpdateListener<AddPostValidationError> {

    private LayoutListShareGroupBinding mBinding;
    public ShareGroupAdapter mAdapter;
    LeafManager manager = new LeafManager();
    String selected_group_id = "";
    String mGroupId = "";
    String mPostId = "";
    int share = 0;
    int count;
    public boolean mIsLoading = false;
    public TeamPostGetItem currentItem;
    ArrayList<ShareGroupItemList> list = new ArrayList<ShareGroupItemList>();
    public static ArrayList<String> list_id = new ArrayList<String>();
    boolean isTeam = false;
    String type;
    boolean isScroll = false;
    public int totalPages = 1;
    public int currentPage = 1;

    public SharePersonalNameListFragment() {

    }

    public static SharePersonalNameListFragment newInstance(String groupId, String post_id, String selected_group_id, String type, int share) {
        SharePersonalNameListFragment fragment = new SharePersonalNameListFragment();
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
        ActiveAndroid.initialize(getActivity());
        list_id.clear();
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
        /*//mAdapter.clear();
        showLoadingBar(mBinding.progressBar);
        hideLoadingBar();
        mIsLoading = true;
//        if (isTeam)
        manager.getPersonalNameListShare(this, selected_group_id, currentPage);
//        else
//            manager.getGroupListShare(this);*/
        showLoadingBar(mBinding.progressBar,false);
        hideLoadingBar();
        new TaskForFriends().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void shareData() {
        showLoadingBar(mBinding.progressBar,false);
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

                if (currentPage == 1) {
                    list.clear();
                }
                ArrayList<ShareGroupItemList> listNew = new ArrayList<ShareGroupItemList>();
                for (int i = 0; i < res.data.size(); i++) {
                    ShareGroupItemList shareGroupItemList = new ShareGroupItemList(res.data.get(i).id+"", res.data.get(i).name, res.data.get(i).image, res.data.get(i).phone, false);
                    list.add(shareGroupItemList);
                    listNew.add(shareGroupItemList);
                }

                totalPages = res.totalPages;
                mIsLoading = false;

                if (currentPage == 1) {
                    mAdapter = new ShareGroupAdapter(list, "personal", new DatabaseHandler(getActivity()).getCount());
                    mBinding.recyclerView.setAdapter(mAdapter);
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
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_shared_successfully), Toast.LENGTH_SHORT).show();

                if (SelectShareTypeActivity.selectShareTypeActivity != null) {
                    SelectShareTypeActivity.selectShareTypeActivity.finish();
                }

                if (ShareGroupTeamListActivity.shareGroupTeamListActivity != null)
                    ShareGroupTeamListActivity.shareGroupTeamListActivity.finish();

                getActivity().finish();

                break;
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

    public void getFilteredList(String str) {

        mAdapter.clear();
        list.clear();
        List<GruppieContactsModel> userIdList = GruppieContactsModel.getAll();

        for (int i = 0; i < userIdList.size(); i++) {
            boolean selected = false;
            if (list_id.contains(userIdList.get(i).contact_id)) {
                selected = true;
            }
            ShareGroupItemList item = new ShareGroupItemList(userIdList.get(i).contact_id+"",
                    userIdList.get(i).contact_name, userIdList.get(i).contact_image,
                    userIdList.get(i).contact_phone, selected);
            list.add(item);
        }

//        mAdapter.addItems(list);
        mAdapter.notifyDataSetChanged();
        mBinding.setSize(mAdapter.getItemCount());
    }

    public void refreshData(BaseResponse response) {
        hideLoadingBar();
        mAdapter.clear();
        LeadResponse res = (LeadResponse) response;
        List<LeadItem> list = res.getResults();
        List<ShareGroupItemList> nameList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            boolean selected = false;
            if (list_id.contains(list.get(i).id)) {
                selected = true;
            }
            ShareGroupItemList item = new ShareGroupItemList(list.get(i).id, list.get(i).name, list.get(i).image, list.get(i).phone, selected);
            nameList.add(item);
        }
        mAdapter.addItems(nameList);
        mAdapter.notifyDataSetChanged();
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.setSize(mAdapter.getItemCount());
        totalPages = res.totalNumberOfPages;
        mIsLoading = false;
    }

    private class TaskForFriends extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list.clear();
            mAdapter = new ShareGroupAdapter(list, "personal", new DatabaseHandler(getActivity()).getCount());
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<GruppieContactsModel> userIdList = GruppieContactsModel.getAll();

            for (int i = 0; i < userIdList.size(); i++) {
                ShareGroupItemList item = new ShareGroupItemList(userIdList.get(i).contact_id+"",
                        userIdList.get(i).contact_name, userIdList.get(i).contact_image,
                        userIdList.get(i).contact_phone, false);
                list.add(item);
            }
//            mAdapter.addItems(list);
            mAdapter.notifyDataSetChanged();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
            if (currentPage == 1) {
                mBinding.recyclerView.setAdapter(mAdapter);
            }
            mBinding.setSize(mAdapter.getItemCount());
            mIsLoading = false;

            if(getActivity()!=null)
            {
                if(getActivity() instanceof SharePersonalNameListActivity)
                {
                    ((SharePersonalNameListActivity)getActivity()).updateCount();
                }
            }
        }
    }

    public static void removeSelectedId(String id) {
       AppLog.e("SELECTEDd", "removeSelectedId: " + id);
        for (int i = 0; i < list_id.size(); i++) {
            if (list_id.get(i) == id) {
                list_id.remove(i);
               AppLog.e("SELECTEDd", "removeSelectedId: index is " + i);
                break;
            }
        }

    }

}