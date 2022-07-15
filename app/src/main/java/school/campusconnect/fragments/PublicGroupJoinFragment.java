package school.campusconnect.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
//import android.databinding.tool.util.L;
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
import java.util.Collections;
import java.util.Comparator;

import school.campusconnect.R;
import school.campusconnect.activities.GroupListActivityNew;
import school.campusconnect.adapters.JoinAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.LayoutListButtonBinding;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.join.JoinItem;
import school.campusconnect.datamodel.join.JoinListResponse;
import school.campusconnect.datamodel.teamdiscussion.TeamPostGetItem;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;

/**
 * Created by frenzin04 on 2/7/2017.
 */

public class PublicGroupJoinFragment extends BaseFragment implements LeafManager.OnCommunicationListener,
        DialogInterface.OnClickListener, LeafManager.OnAddUpdateListener<AddPostValidationError>, JoinAdapter.CheckBoxClick {

    private LayoutListButtonBinding mBinding;
    private JoinAdapter mAdapter;
    LeafManager manager = new LeafManager();
    String mGroupId = "";
    public boolean mIsLoading = false;
    public String team_id="";
    public TeamPostGetItem currentItem;
    ArrayList<JoinItem> list = new ArrayList<JoinItem>();
    private ArrayList<String> selected_ids = new ArrayList<>();
    boolean isScroll = false;
    public int totalPages = 1;
    public int currentPage = 1;

    public PublicGroupJoinFragment() {

    }

    public static PublicGroupJoinFragment newInstance(String groupId) {
        PublicGroupJoinFragment fragment = new PublicGroupJoinFragment();
        Bundle b = new Bundle();
        b.putString("id", groupId);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_list_button, container, false);
        mBinding.setSize(1);
        mBinding.setMessage(R.string.msg_no_groups);
        mGroupId = getArguments().getString("id");
       AppLog.e("SHAREDATA", "3group_id " + mGroupId);
        final LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView.setLayoutManager(manager);
        mBinding.btnSubmit.setVisibility(View.VISIBLE);
        mBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (getSelectedIds().equals(""))
                    Toast.makeText(getActivity(), "Select any friend first!", Toast.LENGTH_SHORT).show();
                else*/
                    joinGroup();
            }
        });

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
        manager.joinList(this, mGroupId+"");

    }

    private void joinGroup() {
        //mAdapter.clear();
        showLoadingBar(mBinding.progressBar);
        mIsLoading = true;
        manager.joinGroup(this, mGroupId+"", getSelectedIds());
//       AppLog.e("TAG", "check ids " + getSelectedIds());

    }

    private void joinGroupDirect() {
        //mAdapter.clear();
        showLoadingBar(mBinding.progressBar);
        mIsLoading = true;
        manager.joinGroupDirect(this, mGroupId+"");
//       AppLog.e("TAG", "check ids " + getSelectedIds());

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
        } catch (Exception e) {
        }

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        switch (apiId) {
            case LeafManager.API_JOIN_LIST:
                hideLoadingBar();
                JoinListResponse res = (JoinListResponse) response;

                list.clear();
                if (res.data != null) {
                    list.addAll(res.data);
                   AppLog.e("ReportResponse", "onSuccess, msg : " + (res).data.toString());
                    String dispName = "";
                    DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
                    int count = databaseHandler.getCount();
                    if (count != 0) {
                        for (int i = 0; i < list.size(); i++) {
                            JoinItem item = list.get(i);
                            try {
                                String name = databaseHandler.getNameFromNum(item.phone.replaceAll(" ", ""));
                                if (!name.equals("")) {
                                    dispName = name;
                                } else {
                                    dispName = item.name;
                                }
                            } catch (NullPointerException e) {
                                dispName = item.name;
                            }
                            item.name = dispName;
                           AppLog.e("ReportResponse" , "name , image : "+item.name+" , "+item.image );
                        }
                    }

                    Collections.sort(list, new Comparator<JoinItem>() {
                        @Override
                        public int compare(JoinItem o1, JoinItem o2) {
                            return o1.name.compareToIgnoreCase(o2.name);
                        }
                    });


                    mAdapter = new JoinAdapter(list, this, count);
                    mBinding.recyclerView.setAdapter(mAdapter);

                    hideLoadingBar();

                  //  mBinding.progressBar.setVisibility(View.GONE);
                } else {
                    try {
                        Toast.makeText(getActivity(), getResources().getString(R.string.toast_add_to_group), Toast.LENGTH_SHORT).show();
                        LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISGROUPUPDATED, true);
                        Toast.makeText(getActivity(), getResources().getString(R.string.toast_add_to_group), Toast.LENGTH_SHORT).show();
                        getActivity().startActivity(new Intent(getActivity(), GroupListActivityNew.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        getActivity().finish();
                    } catch (Exception e) {
                       AppLog.e("Error", "error is " + e.toString());
                    }
                }

                break;

            case LeafManager.API_JOIN_GROUP:
                hideLoadingBar();
                try {
                    LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISGROUPUPDATED, true);
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_add_to_group), Toast.LENGTH_SHORT).show();
                    getActivity().startActivity(new Intent(getActivity(), GroupListActivityNew.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    getActivity().finish();
                } catch (Exception e) {
                   AppLog.e("Error", "error is " + e.toString());
                }
                break;

        }
    }

    @Override
    public void onFailure(int apiId, String msg) {

    }

    @Override
    public void onException(int apiId, String msg) {

    }

    @Override
    public void onClickListener(boolean isChecked, JoinItem item, int position) {

       AppLog.e("ckeckbox..", "clicked " + isChecked + " position is " + position + " id is " + item.id + " id2 is " + list.get(position).id);

        if (isChecked) {
            addSelectedId(list.get(position).id);
            list.get(position).selected = true;
            item.selected = true;
//            mAdapter.updateItem(position, item);
        } else {
            removeId(list.get(position).id);
            list.get(position).selected = false;
            item.selected = false;
//            mAdapter.updateItem(position, item);
        }
    }

    private void removeId(String id) {
        int position = selected_ids.indexOf(id);
       AppLog.e("MULTI_ADD", "removeId: id is " + id + " contains: " + selected_ids.contains(id) + " position is " + position);
        if (position >= 0) {
            selected_ids.remove(position);
        }
    }

    private void addSelectedId(String id) {
        if (!selected_ids.contains(id)) {
           AppLog.e("MULTI_ADD", "addSelectedId: id is " + id);
            selected_ids.add(id);
        } else {
           AppLog.e("MULTI_ADD", "addSelectedId contains: id is " + id);
        }
    }

    private boolean isIdSelected(int id) {
       AppLog.e("MULTI_ADD", "isIdSelected: id is " + id + " contains: " + selected_ids.contains(id));
        return selected_ids.contains(id);
    }

    private String getSelectedIds() {
        String ids = "";
        for (int i = 0; i < selected_ids.size(); i++) {
            if (i != (selected_ids.size() - 1))
                ids = ids + selected_ids.get(i) + ",";
            else
                ids = ids + selected_ids.get(i);
        }
        return ids;
    }

    public void onClickAddComment() {
       /* if (mAdapter.getSelectedgroups().equals(""))
            Toast.makeText(getActivity(), "Select any group first...", Toast.LENGTH_SHORT).show();
        else
            shareData();*/
    }

}
