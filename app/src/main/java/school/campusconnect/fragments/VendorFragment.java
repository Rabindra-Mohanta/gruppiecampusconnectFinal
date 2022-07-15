package school.campusconnect.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.activities.AddVendorActivity;
import school.campusconnect.activities.FullScreenMultiActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.ViewPDFActivity;
import school.campusconnect.adapters.VendorAdapter;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.VendorPostResponse;
import school.campusconnect.datamodel.videocall.VideoClassResponse;
import school.campusconnect.firebase.SendNotificationGlobal;
import school.campusconnect.firebase.SendNotificationModel;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneRemove;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.views.SMBDialogUtils;

public class VendorFragment extends BaseFragment implements LeafManager.OnCommunicationListener,DialogInterface.OnClickListener, VendorAdapter.VendorListener {

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

    ArrayList<VendorPostResponse.VendorPostData> listData=new ArrayList<>();
    VendorAdapter vendorAdapter;
    private int totalPages=0;
    private int currentPage=1;
    private LeafManager manager;
    private String mGroupId;
    private String role;
    private VendorPostResponse.VendorPostData currentItem;

    LeafPreference leafPreference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_vendor,container,false);
        ButterKnife.bind(this,view);


        if(getArguments()!=null){
            role = getArguments().getString("role");
        }

        manager=new LeafManager();

        leafPreference = LeafPreference.getInstance(VendorFragment.this.getActivity());

        mGroupId=GroupDashboardActivityNew.groupId;

        layoutManager=new LinearLayoutManager(getActivity());
        rvGallery.setLayoutManager(layoutManager);
        vendorAdapter=new VendorAdapter(listData,this,role);
        rvGallery.setAdapter(vendorAdapter);

        scrollListener();

        getData();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        vendorAdapter.notifyDataSetChanged();
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
//                    AppLog.i(TAG, "RecyclerView scrolled: scroll up!");
                    imgTop.setVisibility(View.VISIBLE);
                } else {
//                    AppLog.i(TAG, " RecyclerView scrolled: scroll down!");
                    imgTop.setVisibility(View.GONE);
                }
//                AppLog.e(TAG, "visibleItemCount " + visibleItemCount);
//                AppLog.e(TAG, "totalItemCount " + totalItemCount);
//                AppLog.e(TAG, "firstVisibleItemPosition " + firstVisibleItemPosition);
//                AppLog.e(TAG, "lastVisibleItemPosition " + lastVisibleItemPosition);

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

    private void getData()
    {
        //showLoadingBar(progressBar);
        progressBar.setVisibility(View.VISIBLE);
        String re = LeafPreference.getInstance(getActivity()).getString( GroupDashboardActivityNew.groupId+"_vendor");

        if (re != null && !TextUtils.isEmpty(re))
        {
            AppLog.e(TAG, "Api Calling::: if ");
            VendorPostResponse response = new Gson().fromJson(re, new TypeToken<VendorPostResponse>()
            {}.getType());
            if (currentPage == 1)
            {
                listData.clear();

                listData.addAll(response.data);
                AppLog.e(TAG, "current page 1");
            }
            else
            {
                listData.addAll(response.data);
                AppLog.e(TAG, "current page " + currentPage);
            }

            if(listData.size()==0)

            {
                progressBar.setVisibility(View.GONE);
                txtEmpty.setVisibility(View.VISIBLE);
            }

            else
                txtEmpty.setVisibility(View.GONE);

            vendorAdapter.notifyDataSetChanged();

            totalPages = response.totalNumberOfPages;

            if (!isConnectionAvailable()) {
                showNoNetworkMsg();
            }
            if ("admin".equalsIgnoreCase(role) || leafPreference.getInt(mGroupId + "_vendorpush") > 0
                    || LeafPreference.getInstance(getContext()).getBoolean(mGroupId + "_vendor_delete")) {

                LeafPreference.getInstance(getContext()).setBoolean(mGroupId + "_vendor_delete", false);

                //showLoadingBar(progressBar);
                //  progressBar.setVisibility(View.VISIBLE);
                mIsLoading = true;
                manager.getVendorPost(this, mGroupId+"", currentPage);

            }
        }
        else
        {
            if(isConnectionAvailable())
            {
                //showLoadingBar(progressBar);
                progressBar.setVisibility(View.VISIBLE);
                mIsLoading = true;
                manager.getVendorPost(this, mGroupId+"", currentPage);
            }
            else
            {
                showNoNetworkMsg();
            }
        }

    }

    private void getDataFromAPI()
    {

        if(isConnectionAvailable())
        {
            //showLoadingBar(progressBar);
            progressBar.setVisibility(View.VISIBLE);
            mIsLoading = true;
            manager.getVendorPost(this, mGroupId+"", currentPage);
            leafPreference.remove(mGroupId+"_vendorpush");
        }
        else
        {
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
        {
            menu.findItem(R.id.menu_add_post).setIcon(R.drawable.posticon);
            menu.findItem(R.id.menu_add_post).setVisible(true);
        }
        else
            menu.findItem(R.id.menu_add_post).setVisible(false);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(getActivity()==null)
            return;

        AppLog.e(TAG , "onreSume called");

        if(LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.IS_VENDOR_POST_UPDATED))
        {
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.IS_VENDOR_POST_UPDATED, false);
            currentPage=1;
            getDataFromAPI();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_post:
                if(getActivity()!=null)
                {
                    Intent intent = new Intent(getActivity(), AddVendorActivity.class);
                    startActivity(intent);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        //   hideLoadingBar();
        progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_VENDOR_POST:
                VendorPostResponse res = (VendorPostResponse) response;
                AppLog.e(TAG, "Post Res ; " + new Gson().toJson(res.data));

                LeafPreference.getInstance(getActivity()).setString(GroupDashboardActivityNew.groupId+"_vendor", new Gson().toJson(res));

                if (currentPage == 1) {
                    listData.clear();

                    listData.addAll(res.data);
                    AppLog.e(TAG, "current page 1");

                } else {
                    listData.addAll(res.data);
                    AppLog.e(TAG, "current page " + currentPage);
                }

                leafPreference.remove(mGroupId+"_vendorpush");

                if(listData.size()==0)

                {
                    progressBar.setVisibility(View.GONE);
                    txtEmpty.setVisibility(View.VISIBLE);
                }

                else
                    txtEmpty.setVisibility(View.GONE);

                vendorAdapter.notifyDataSetChanged();

                totalPages = res.totalNumberOfPages;
                mIsLoading = false;
                break;
            case LeafManager.API_VENDOR_DELETE:
                Toast.makeText(getContext(), getResources().getString(R.string.toast_post_delete_successfully), Toast.LENGTH_SHORT).show();
                currentPage=1;
                getDataFromAPI();
                AmazoneRemove.remove(currentItem.fileName);
                sendNotification();
                break;
        }
    }


    @Override
    public void onFailure(int apiId, String msg) {
        //   hideLoadingBar();
        progressBar.setVisibility(View.GONE);
        mIsLoading = false;
        currentPage = currentPage - 1;
        if (currentPage < 0) {
            currentPage = 1;
        }
        if (msg.contains("401:Unauthorized") || msg.contains("401")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else if (msg.contains("404")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_post), Toast.LENGTH_SHORT).show();
        } else if (msg.contains("418")) {
            if (apiId == LeafManager.API_REPORT)
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_already_reported), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onException(int apiId, String msg) {
        //   hideLoadingBar();
        progressBar.setVisibility(View.GONE);
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
            //showLoadingBar(progressBar);
            progressBar.setVisibility(View.VISIBLE);
            LeafManager manager = new LeafManager();
            manager.deleteVendorPost(this, mGroupId+"",currentItem.vendorId);

        } else {
            showNoNetworkMsg();
        }

    }

    @Override
    public void onPostClick(VendorPostResponse.VendorPostData item) {
        if(TextUtils.isEmpty(item.fileType))
        {
            return;
        }
      /*  if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {

            Intent browserIntent = new Intent(getActivity(), TestActivity.class);
            browserIntent.putExtra("url", item.video);
            startActivity(browserIntent);

        } else*/ if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
            Intent i = new Intent(getActivity(), ViewPDFActivity.class);
            i.putExtra("pdf", item.fileName.get(0));
            i.putExtra("thumbnail", item.thumbnailImage.get(0));
            i.putExtra("name", item.vendor);
            startActivity(i);

        } else if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
            Intent i = new Intent(getActivity(), FullScreenMultiActivity.class);
            i.putExtra("image_list", item.fileName);
            startActivity(i);
        }
    }

    @Override
    public void onDeleteClick(VendorPostResponse.VendorPostData item) {
        currentItem = item;
        SMBDialogUtils.showSMBDialogOKCancel(getActivity(), getResources().getString(R.string.dialog_are_you_want_to_delete), this);
    }
    private void sendNotification() {
        SendNotificationModel notificationModel = new SendNotificationModel();
        notificationModel.to = "/topics/" + GroupDashboardActivityNew.groupId;
        notificationModel.data.title = getResources().getString(R.string.app_name);
        notificationModel.data.body = "Vendor deleted";
        notificationModel.data.Notification_type = "DELETE_VENDOR";
        notificationModel.data.iSNotificationSilent = true;
        notificationModel.data.groupId = GroupDashboardActivityNew.groupId;
        notificationModel.data.teamId = "";
        notificationModel.data.createdById = LeafPreference.getInstance(getActivity()).getString(LeafPreference.LOGIN_ID);
        notificationModel.data.postId = "";
        notificationModel.data.postType = "";
        SendNotificationGlobal.send(notificationModel);
    }
}
