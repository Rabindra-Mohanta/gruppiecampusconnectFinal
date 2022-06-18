
package school.campusconnect.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import school.campusconnect.utils.AppLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import school.campusconnect.R;
import school.campusconnect.adapters.LikeAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.LayoutListWithoutRefreshBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.likelist.LikeListData;
import school.campusconnect.datamodel.likelist.LikeListResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;

public class LikeListFragment extends BaseFragment implements LikeAdapter.OnLeadSelectListener,
        LeafManager.OnCommunicationListener {

    private LayoutListWithoutRefreshBinding mBinding;
    private LikeAdapter mAdapter;
    final int REQUEST_CALL = 234;
    final int REQUEST_SMS = 235;
    Intent intent;
    String groupId ="";
    String postId = "";
    String type = "";
    LeafManager mManager = new LeafManager();
    public boolean mIsLoading = false;
    public int totalPages = 1;
    public int currentPage = 1;

    int count;
    private String teamId;

    public LikeListFragment() {

    }

    public static LikeListFragment newInstance(Bundle b) {
        LikeListFragment fragment = new LikeListFragment();
        fragment.setArguments(b);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_list_without_refresh, container, false);
        mBinding.setSize(1);
        mBinding.setMessage(R.string.msg_no_likes);
        final LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView.setLayoutManager(manager);
        groupId = getArguments().getString("id");
        postId = getArguments().getString("post_id");
        type = getArguments().getString("type");
        if(type.equals("team"))
        {
            teamId=getArguments().getString("team_id");
        }
        DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
        count = databaseHandler.getCount();

        mAdapter = new LikeAdapter(new ArrayList<LikeListData>(), this, count);

        getData();

        LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISUSERDELETED, false);

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
                if (!mIsLoading && totalPages > currentPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        currentPage = currentPage + 1;
                        getData();
                    }
                }
            }

        });


        /*mBinding.swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {

                    currentPage = 1;
                    mAdapter = new LikeAdapter(list_items, LeadListFragment.this, 0);
                    getData();
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                } else {
                    showNoNetworkMsg();
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                }
            }
        });*/

        return mBinding.getRoot();
    }

    private void getData() {
        showLoadingBar(mBinding.progressBar);
        mIsLoading = true;
        if(type.equals("group"))
            mManager.likeList(this, groupId+"", postId+"", currentPage);
        else
            mManager.likeListTeam(this, groupId+"",teamId, postId+"", currentPage);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
       AppLog.e("sdfas", "onRequestPermissionsResult " + requestCode);
        switch (requestCode) {
            case REQUEST_CALL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   AppLog.e("sdfas", "gra " + requestCode);
                    if (intent != null) {
                        startActivity(intent);
                    }
                } else {
                   AppLog.e("sdfas", "not gr " + requestCode);
                }
                return;

            case REQUEST_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (intent != null) {
                        startActivity(intent);
                    } else {
                       AppLog.e("sdfas", "not gr " + requestCode);
                    }
                    return;
                }
        }
    }

    @Override
    public void onCallClick(LikeListData item) {
        intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + item.getPhone()));

      /*  if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
           AppLog.e("sdfas", "per");
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {*/
           AppLog.e("sdfas", "acti");
            startActivity(intent);
       // }
    }

    @Override
    public void onSMSClick(LikeListData item) {}

    @Override
    public void onMailClick(LikeListData item) {}

    @Override
    public void onNameClick(LikeListData item) {}

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        hideLoadingBar();
        LikeListResponse res = (LikeListResponse) response;

        List<LikeListData> list = res.data;
//        DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());

        /*for (int i = 0; i < list.size(); i++) {
            LikeListData item = list.get(i);

            if (count != 0) {
                try {
//                       AppLog.e("CONTACTSS", "count is " + databaseHandler.getCount());
                    String name = databaseHandler.getNameFromNum(item.getPhone().replaceAll(" ", ""));
//                       AppLog.e("CONTACTSS", "api name is " + item.getName());
//                       AppLog.e("CONTACTSS", "name is " + name);
//                       AppLog.e("CONTACTSS", "num is " + item.getPhone());
                    if (!name.equals("")) {
                        item.setName(name);
                    } else {
                        item.setName(item.getName());
                    }
                } catch (NullPointerException e) {
                    item.setName(item.getName());
                }
            } else {
               AppLog.e("CONTACTSS", "count is 0");
                item.setName(item.getName());
            }
        }*/

        mAdapter.addItems(list);
        mAdapter.notifyDataSetChanged();
        if (currentPage == 1) {
            mBinding.recyclerView.setAdapter(mAdapter);
        }
        mBinding.setSize(mAdapter.getItemCount());
        totalPages = res.totalNumberOfPages;
        mIsLoading = false;

    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        mIsLoading = false;
       // Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        currentPage = currentPage - 1;
        if (currentPage < 0) {
            currentPage = 1;
        }
        try {
            if (msg.contains("401:Unauthorized")) {
                Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
                logout();
            } else {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){}

    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        mIsLoading = false;
        currentPage = currentPage - 1;
        if (currentPage < 0) {
            currentPage = 1;
        }
        try {
            Toast.makeText(getActivity(), getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
           AppLog.e("LeadListFragment", "OnExecption : " + msg);
        }
    }
}
