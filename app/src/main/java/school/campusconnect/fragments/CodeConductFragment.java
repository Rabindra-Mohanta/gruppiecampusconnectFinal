package school.campusconnect.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.AddCodeConductActivity;
import school.campusconnect.activities.FullScreenActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.ViewPDFActivity;
import school.campusconnect.adapters.CodeConductAdapter;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.CodeConductResponse;
import school.campusconnect.datamodel.PostDataItem;
import school.campusconnect.datamodel.VendorPostResponse;
import school.campusconnect.firebase.SendNotificationGlobal;
import school.campusconnect.firebase.SendNotificationModel;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneRemove;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.views.SMBDialogUtils;

public class CodeConductFragment extends BaseFragment implements LeafManager.OnCommunicationListener, DialogInterface.OnClickListener, CodeConductAdapter.CodeConductListener {

    private static final String TAG = "GalleryFragment";
    @Bind(R.id.rvGallery)
    RecyclerView rvGallery;

    @Bind(R.id.txtEmpty)
    TextView txtEmpty;

    @Bind(R.id.img_top)
    ImageView imgTop;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.swipeRefreshLayout)
    PullRefreshLayout swipeRefreshLayout;

    public boolean mIsLoading = false;

    private View view;
    private LinearLayoutManager layoutManager;

    ArrayList<CodeConductResponse.CodeConductData> listData = new ArrayList<>();
    CodeConductAdapter codeConductAdapter;
    private int totalPages = 0;
    private int currentPage = 1;
    private LeafManager manager;
    private String mGroupId;
    private String role;
    private CodeConductResponse.CodeConductData currentItem;

    LeafPreference leafPreference;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_code_conduct, container, false);
        ButterKnife.bind(this, view);

        manager = new LeafManager();


        if (getArguments() != null) {
            role = getArguments().getString("role");
        }

        leafPreference = LeafPreference.getInstance(CodeConductFragment.this.getActivity());

        mGroupId = GroupDashboardActivityNew.groupId;

        layoutManager = new LinearLayoutManager(getActivity());
        rvGallery.setLayoutManager(layoutManager);
        codeConductAdapter = new CodeConductAdapter(listData, this, role);
        rvGallery.setAdapter(codeConductAdapter);

        scrollListener();

        getData();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        codeConductAdapter.notifyDataSetChanged();
    }

    private void scrollListener() {
        imgTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgTop.setVisibility(View.GONE);
                rvGallery.smoothScrollToPosition(0);
            }
        });

        rvGallery.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                if (firstVisibleItemPosition != 0) {
                    AppLog.i(TAG, "RecyclerView scrolled: scroll up!");
                    imgTop.setVisibility(View.VISIBLE);
                } else {
                    AppLog.i(TAG, " RecyclerView scrolled: scroll down!");
                    imgTop.setVisibility(View.GONE);
                }
                AppLog.e(TAG, "visibleItemCount " + visibleItemCount);
                AppLog.e(TAG, "totalItemCount " + totalItemCount);
                AppLog.e(TAG, "firstVisibleItemPosition " + firstVisibleItemPosition);
                AppLog.e(TAG, "lastVisibleItemPosition " + lastVisibleItemPosition);

                if (!mIsLoading && totalPages > currentPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                    ) {
                        currentPage = currentPage + 1;
                        AppLog.e(TAG, "onScrollCalled " + currentPage);
                        getData();
                    }
                }
            }
        });

        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    currentPage = 1;
                    getData();
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    showNoNetworkMsg();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

    }


    private void getData() {

        String re = LeafPreference.getInstance(getActivity()).getString(GroupDashboardActivityNew.groupId + "_coc");

        if (re != null && !TextUtils.isEmpty(re)) {
            AppLog.e(TAG, "Api Calling::: if ");
            CodeConductResponse res = new Gson().fromJson(re, new TypeToken<CodeConductResponse>() {
            }.getType());
            AppLog.e(TAG, "Post Res ; " + new Gson().toJson(res.data));

            if (currentPage == 1) {
                PostDataItem.deleteGeneralPosts(mGroupId + "");
                listData.clear();

                listData.addAll(res.data);
                AppLog.e(TAG, "current page 1");

            } else {
                listData.addAll(res.data);
                AppLog.e(TAG, "current page " + currentPage);
            }

            if (listData.size() == 0)
                txtEmpty.setVisibility(View.VISIBLE);
            else
                txtEmpty.setVisibility(View.GONE);

            codeConductAdapter.notifyDataSetChanged();

            totalPages = res.totalNumberOfPages;
            mIsLoading = false;

            //NOFIREBASEDATABASE

            if (!isConnectionAvailable()) {
                showNoNetworkMsg();
            }
            if ("admin".equalsIgnoreCase(role) || leafPreference.getInt(mGroupId + "_cocpush") > 0
                    || LeafPreference.getInstance(getContext()).getBoolean(mGroupId + "_rule_delete")) {

                LeafPreference.getInstance(getContext()).setBoolean(mGroupId + "_rule_delete", false);

                showLoadingBar(progressBar);
                mIsLoading = true;
                manager.getCodeOfConductPost(this, mGroupId + "", currentPage);

            }
            //initFirebase();
        } else {
            if (isConnectionAvailable()) {
                showLoadingBar(progressBar);
                mIsLoading = true;
                manager.getCodeOfConductPost(this, mGroupId + "", currentPage);
            } else {
                showNoNetworkMsg();
            }

        }


    }

    private void getDataFromAPI() {
        if (isConnectionAvailable()) {
            showLoadingBar(progressBar);
            mIsLoading = true;
            manager.getCodeOfConductPost(this, mGroupId + "", currentPage);
        } else {
            showNoNetworkMsg();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if ("admin".equalsIgnoreCase(role))
            menu.findItem(R.id.menu_add_post).setVisible(true);
        else
            menu.findItem(R.id.menu_add_post).setVisible(false);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() == null)
            return;

        if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.IS_CODE_CONDUCT_UPDATED)) {
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.IS_CODE_CONDUCT_UPDATED, false);
            currentPage = 1;
            getDataFromAPI();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_post:
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), AddCodeConductActivity.class);
                    startActivity(intent);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();

        switch (apiId) {
            case LeafManager.API_CODE_CONDUCT_POST:
                CodeConductResponse res = (CodeConductResponse) response;
                AppLog.e(TAG, "Post Res ; " + new Gson().toJson(res.data));

                LeafPreference.getInstance(getActivity()).setString(GroupDashboardActivityNew.groupId + "_coc", new Gson().toJson(res));

                if (currentPage == 1) {
                    PostDataItem.deleteGeneralPosts(mGroupId + "");
                    listData.clear();

                    listData.addAll(res.data);
                    AppLog.e(TAG, "current page 1");

                } else {
                    listData.addAll(res.data);
                    AppLog.e(TAG, "current page " + currentPage);
                }

                if (listData.size() == 0)
                    txtEmpty.setVisibility(View.VISIBLE);
                else
                    txtEmpty.setVisibility(View.GONE);

                leafPreference.remove(GroupDashboardActivityNew.groupId + "_cocpush");

                codeConductAdapter.notifyDataSetChanged();

                totalPages = res.totalNumberOfPages;
                mIsLoading = false;
                break;
            case LeafManager.API_CODE_CONDUCT_DELETE:
                Toast.makeText(getContext(), "Post Deleted Successfully", Toast.LENGTH_SHORT).show();
                currentPage = 1;
                getData();
                AmazoneRemove.remove(currentItem.fileName);
                sendNotification();
                break;
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        mIsLoading = false;
        currentPage = currentPage - 1;
        if (currentPage < 0) {
            currentPage = 1;
        }
        if (msg.contains("401:Unauthorized") || msg.contains("401")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else if (msg.contains("404")) {
            Toast.makeText(getActivity(), "No posts available.", Toast.LENGTH_SHORT).show();
        } else if (msg.contains("418")) {
            if (apiId == LeafManager.API_REPORT)
                Toast.makeText(getActivity(), "You have already reported this post", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        mIsLoading = false;
        currentPage = currentPage - 1;
        if (currentPage < 0) {
            currentPage = 1;
        }
        if (getActivity() != null)
            Toast.makeText(getActivity(), getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        AppLog.e("TeamPostFrag", "DIalog Ok Clicked ");
        if (isConnectionAvailable()) {
            showLoadingBar(progressBar);
            LeafManager manager = new LeafManager();
            manager.deleteCodeConduct(this, mGroupId + "", currentItem.cocId);

        } else {
            showNoNetworkMsg();
        }

    }

    @Override
    public void onPostClick(CodeConductResponse.CodeConductData item) {
/*  if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {

            Intent browserIntent = new Intent(getActivity(), TestActivity.class);
            browserIntent.putExtra("url", item.video);
            startActivity(browserIntent);

        } else*/
        if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
            Intent i = new Intent(getActivity(), ViewPDFActivity.class);
            i.putExtra("pdf", item.fileName.get(0));
            i.putExtra("name", item.title);
            startActivity(i);

        } else if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
            Intent i = new Intent(getActivity(), FullScreenActivity.class);
            i.putExtra("image", item.fileName);
            startActivity(i);
        }
    }

    @Override
    public void onDeleteClick(CodeConductResponse.CodeConductData item) {
        currentItem = item;
        SMBDialogUtils.showSMBDialogOKCancel(getActivity(), "Are You Sure Want To Delete ?", this);
    }

    private void sendNotification() {
        SendNotificationModel notificationModel = new SendNotificationModel();
        notificationModel.to = "/topics/" + GroupDashboardActivityNew.groupId;
        notificationModel.data.title = getResources().getString(R.string.app_name);
        notificationModel.data.body = "Rule deleted";
        notificationModel.data.Notification_type = "DELETE_RULE";
        notificationModel.data.iSNotificationSilent = true;
        notificationModel.data.groupId = GroupDashboardActivityNew.groupId;
        notificationModel.data.teamId = "";
        notificationModel.data.createdById = LeafPreference.getInstance(getActivity()).getString(LeafPreference.LOGIN_ID);
        notificationModel.data.postId = "";
        notificationModel.data.postType = "";
        SendNotificationGlobal.send(notificationModel);
    }
}
