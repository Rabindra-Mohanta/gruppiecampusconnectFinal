package school.campusconnect.fragments;

import android.content.DialogInterface;
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

import com.activeandroid.ActiveAndroid;
import com.baoyz.widget.PullRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import school.campusconnect.R;
import school.campusconnect.adapters.AuthorizedUserAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.LayoutListTeamsBinding;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.authorizeduser.AuthorizedUserData;
import school.campusconnect.datamodel.authorizeduser.AuthorizedUserModel;
import school.campusconnect.datamodel.authorizeduser.AuthorizedUserResponse;
import school.campusconnect.datamodel.teamdiscussion.TeamListItemModel;
import school.campusconnect.datamodel.teamdiscussion.TeamPostGetItem;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;

/**
 * Created by frenzin04 on 2/13/2017.
 */

public class AuthorizedUserFragment extends BaseFragment implements LeafManager.OnCommunicationListener,
        DialogInterface.OnClickListener, LeafManager.OnAddUpdateListener<AddPostValidationError>, AuthorizedUserAdapter.UnAuthorizeUser {

    private LayoutListTeamsBinding mBinding;
    private AuthorizedUserAdapter mAdapter;
    LeafManager manager = new LeafManager();
    String mGroupId = "";
    int position = -1;
    int likecount = 0;
    int count;
    public boolean mIsLoading = false;
    public int totalPages = 1;
    public int currentPage = 1;
    public String team_id;// = 1;
    boolean isInbox = true;
    public TeamPostGetItem currentItem;
    ArrayList<TeamListItemModel> mData = new ArrayList<TeamListItemModel>();
    boolean isScroll = false;


    public static AuthorizedUserFragment newInstance(String groupId) {
       AppLog.e("PERSNOL_NEW", "gid2 is " + groupId);
        AuthorizedUserFragment fragment = new AuthorizedUserFragment();
        Bundle b = new Bundle();
        b.putString("id", groupId);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_list_teams, container, false);
        mBinding.setSize(1);
        mBinding.setMessage(R.string.msg_no_users);
        mGroupId = getArguments().getString("id");
       AppLog.e("PERSNOL_NEW", "gid3-1 is " + mGroupId);
        final LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView2.setLayoutManager(manager);

        ActiveAndroid.initialize(getActivity());

        DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
        count = databaseHandler.getCount();
        mAdapter = new AuthorizedUserAdapter(new ArrayList<AuthorizedUserModel>(), this, count);
        getData();
       AppLog.e("TeamPost", "DataFromAPI");

        mBinding.recyclerView2.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                if (!mIsLoading && totalPages > currentPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            ) {
                        currentPage = currentPage + 1;
                        isScroll = true;
                        getData();
                    }
                }
            }
        });

        mBinding.swipeRefreshLayout2.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    currentPage = 1;
                    isScroll = false;
                    getData();
                    mBinding.swipeRefreshLayout2.setRefreshing(false);
                } else {
                    showNoNetworkMsg();
                    mBinding.swipeRefreshLayout2.setRefreshing(false);
                }
            }
        });

        return mBinding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        //   getData(isInbox);
       AppLog.e("TeamPost", "OnResume : " + LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISTEAMPOSTUPDATED));
        if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISTEAMPOSTUPDATED)) {
            mAdapter.clear();
            currentPage = 1;
            getData();
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISTEAMPOSTUPDATED, false);
        }
    }

    public void getInboxData() {
        isInbox = true;
        mAdapter.getList().clear();
        mAdapter.notifyDataSetChanged();
        getData();
    }

    public void getOutBoxData() {
        isInbox = false;
        mAdapter.getList().clear();
        mAdapter.notifyDataSetChanged();
        getData();
    }

    private void getData() {
        //mAdapter.clear();
        showLoadingBar(mBinding.progressBar2);
        mIsLoading = true;
        manager.getAuthorizedList(this, mGroupId+"", currentPage);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {


        switch (apiId) {

            case LeafManager.API_AUTHOREIZED_USER:
                hideLoadingDialog();
                hideLoadingBar();
                AuthorizedUserResponse res = (AuthorizedUserResponse) response;

                List<AuthorizedUserData> result = res.getResults();

                ArrayList<AuthorizedUserModel> authorizedUserModels = new ArrayList<>();

                for (int i = 0; i < result.size(); i++) {
                    AuthorizedUserData item = result.get(i);
                    AuthorizedUserModel authorizedUserModel = new AuthorizedUserModel();
                    authorizedUserModel.id = item.id;
                    authorizedUserModel.name = item.name;
                    authorizedUserModel.phone = item.phone;
                    authorizedUserModel.image = item.image;
                    authorizedUserModel.profileCompletion = item.profileCompletion;
                    authorizedUserModel.email = item.email;
                    authorizedUserModel.gender = item.gender;
                    authorizedUserModel.dob = item.dob;
                    authorizedUserModel.qualification = item.qualification;
                    authorizedUserModel.occupation = item.occupation;
                    authorizedUserModels.add(authorizedUserModel);
                }

                if (currentPage == 1) {
                    mAdapter = new AuthorizedUserAdapter(authorizedUserModels, this, count);
                    mBinding.recyclerView2.setAdapter(mAdapter);
                }
                mAdapter.notifyDataSetChanged();
                mBinding.setSize(mAdapter.getItemCount());
                hide_keyboard();
                break;

            case LeafManager.API_REPORT:
                hideLoadingBar();
                Toast.makeText(getContext(), getResources().getString(R.string.toast_post_reported_sucessfully), Toast.LENGTH_SHORT).show();
                break;

            case LeafManager.API_NOT_ALLOW_POST:
                currentPage = 1;
                getData();
                break;

        }

    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<AddPostValidationError> error) {
        hideLoadingBar();
        mIsLoading = false;
       // Toast.makeText(getActivity(), error.title, Toast.LENGTH_SHORT).show();
        currentPage = currentPage - 1;
        if (currentPage < 0) {
            currentPage = 1;
        }

       AppLog.e("GeneralPostFragment", "onFailure  ,, msg : " + error);
        if (error.status.equals("401")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else if (error.status.equals("404")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_post), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), error.title, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        mIsLoading = false;
//        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        currentPage = currentPage - 1;
        if (currentPage < 0) {
            currentPage = 1;
        }
        if (msg.contains("401:Unauthorized")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else if (msg.contains("404")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_post), Toast.LENGTH_SHORT).show();
            logout();
        } else if (msg.contains("418")) {
            if (apiId == LeafManager.API_REPORT)
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_already_reported), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
        /*if (msg.contains("404")) {
            Toast.makeText(getActivity(), "No posts available.", Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        mIsLoading = false;
        currentPage = currentPage - 1;
        if (currentPage < 0) {
            currentPage = 1;
        }
        //Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    /*@Override
    public void onFavClick(TeamListItemModel item, int pos) {
       *//* showLoadingBar(mBinding.progressBar);
        position = pos;
        int fav = 0;
        if (item.isFavourited) {
            fav = 0;
        } else {
            fav = 1;
        }
        manager.setFav(this, mGroupId, item.id, fav);*//*

    }

    @Override
    public void onLikeClick(TeamListItemModel item, int position) {

       *//* showLoadingBar(mBinding.progressBar);
        this.position = position;
        int fav = 0;
        if (item.isLiked) {
            fav = 0;
            likecount = item.likes - 1;
        } else {
            fav = 1;
            likecount = item.likes + 1;
        }
        manager.setLikes(this, mGroupId, item.id, fav);*//*

    }*/

    @Override
    public void onClick(DialogInterface dialog, int which) {
       AppLog.e("TeamPostFrag", "DIalog Ok Clicked ");

        if(isConnectionAvailable())
        {
            showLoadingBar(mBinding.progressBar2);

            LeafManager manager = new LeafManager();
            manager.deleteTeamPost(this, mGroupId+"", team_id+"", currentItem.id);
        }
        else {
            showNoNetworkMsg();
        }

    }

    public void refreshData(BaseResponse response) {
        hideLoadingBar();
        mAdapter.clear();

        AuthorizedUserResponse res = (AuthorizedUserResponse) response;

        List<AuthorizedUserData> result = res.getResults();

        ArrayList<AuthorizedUserModel> authorizedUserModels = new ArrayList<>();

        for (int i = 0; i < result.size(); i++) {
            AuthorizedUserData item = result.get(i);
            AuthorizedUserModel authorizedUserModel = new AuthorizedUserModel();
            authorizedUserModel.id = item.id;
            authorizedUserModel.name = item.name;
            authorizedUserModel.phone = item.phone;
            authorizedUserModel.image = item.image;
            authorizedUserModel.profileCompletion = item.profileCompletion;
            authorizedUserModel.email = item.email;
            authorizedUserModel.gender = item.gender;
            authorizedUserModel.dob = item.dob;
            authorizedUserModel.qualification = item.qualification;
            authorizedUserModel.occupation = item.occupation;
            authorizedUserModels.add(authorizedUserModel);
        }

        mAdapter.addItems(authorizedUserModels);
        mAdapter.notifyDataSetChanged();
        mBinding.recyclerView2.setAdapter(mAdapter);
        mBinding.setSize(mAdapter.getItemCount());
        totalPages = res.totalNumberOfPages;
        mIsLoading = false;
    }

    @Override
    public void unAuthorize(AuthorizedUserModel item) {
        //showLoadingDialog(getString(R.string.please_wait));
        showLoadingBar(mBinding.progressBar2);
        manager.notAllowPost(this, mGroupId+"", item.id);
    }

}
