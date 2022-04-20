
package school.campusconnect.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.icu.text.Transliterator;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import school.campusconnect.BuildConfig;
import school.campusconnect.datamodel.EventTBL;
import school.campusconnect.firebase.SendNotificationGlobal;
import school.campusconnect.firebase.SendNotificationModel;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AmazoneRemove;
import school.campusconnect.utils.AppLog;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
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
import java.util.HashMap;
import java.util.List;

import school.campusconnect.R;
import school.campusconnect.activities.TestActivity;
import school.campusconnect.activities.AddFriendActivity;
import school.campusconnect.activities.AddPostActivity;
import school.campusconnect.activities.AddQuestionActivity;
import school.campusconnect.activities.FullScreenActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.LikesListActivity;
import school.campusconnect.activities.ProfileActivity2;
import school.campusconnect.activities.SelectShareTypeActivity;
import school.campusconnect.activities.ViewPDFActivity;
import school.campusconnect.activities.PushActivity;
import school.campusconnect.adapters.PostAdapter;
import school.campusconnect.adapters.ReportAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.LayoutListButtonBinding;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.PostDataItem;
import school.campusconnect.datamodel.PostItem;
import school.campusconnect.datamodel.PostResponse;
import school.campusconnect.datamodel.reportlist.ReportItemList;
import school.campusconnect.datamodel.reportlist.ReportResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.DateTimeHelper;
import school.campusconnect.utils.MixOperations;
import school.campusconnect.views.SMBDialogUtils;

//import com.google.api.services.samples.youtube.cmdline.Auth;


public class GeneralPostFragment extends BaseFragment implements LeafManager.OnCommunicationListener,
        PostAdapter.OnItemClickListener, DialogInterface.OnClickListener, LeafManager.OnAddUpdateListener<AddPostValidationError>, View.OnClickListener {

    private static final String TAG = "GeneralPostFragment";
    private LayoutListButtonBinding mBinding;
    private PostAdapter mAdapter;
    private ReportAdapter mAdapter2;
    LeafManager manager = new LeafManager();
    String mGroupId = "";
    int position = -1;
    int count;
    public boolean mIsLoading = false;
    public boolean liked = false;
    public int totalPages = 1;
    public int currentPage = 1;
    public PostItem currentItem;
    ArrayList<PostItem> PostList = new ArrayList<PostItem>();
    RecyclerView dialogRecyclerView;
    ProgressBar dialogProgressBar;
    ArrayList<ReportItemList> list = new ArrayList<>();

    DatabaseHandler databaseHandler;
    private LinearLayoutManager layoutManager;

    LeafPreference leafPreference;

    EventTBL eventTBL;

    String type;

    //private Query query;

    public GeneralPostFragment() {

    }

    public static GeneralPostFragment newInstance(String groupId,String type) {
        GeneralPostFragment fragment = new GeneralPostFragment();
        Bundle b = new Bundle();
        b.putString("id", groupId);
        b.putString("type", type);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        leafPreference = LeafPreference.getInstance(GeneralPostFragment.this.getActivity());
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_notification).setVisible(false);
        menu.findItem(R.id.action_notification_list).setVisible(false);
        menu.findItem(R.id.action_notification1).setVisible(false);

        if (GroupDashboardActivityNew.isPost)
            menu.findItem(R.id.menu_add_post).setVisible(true);
        else
            menu.findItem(R.id.menu_add_post).setVisible(false);

        menu.findItem(R.id.menu_add_friend).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_post:
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), AddPostActivity.class);
                    intent.putExtra("id", mGroupId);
                    intent.putExtra("type", "group");
                    startActivity(intent);
                }
                break;
            case R.id.menu_add_friend:
                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                intent.putExtra("id", mGroupId);
                intent.putExtra("invite", true);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActiveAndroid.initialize(getActivity());
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_list_button, container, false);
        mBinding.setSize(1);
        init();

        getGroupPostLocaly();

        scrollListener();


        return mBinding.getRoot();

    }

    private void scrollListener() {
        mBinding.imgTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.imgTop.setVisibility(View.GONE);
                mBinding.recyclerView.smoothScrollToPosition(0);
            }
        });

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
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                if (firstVisibleItemPosition != 0) {
                    // AppLog.i(TAG, "RecyclerView scrolled: scroll up!");
                    mBinding.imgTop.setVisibility(View.VISIBLE);
                } else {
//                    AppLog.i(TAG, " RecyclerView scrolled: scroll down!");
                    mBinding.imgTop.setVisibility(View.GONE);
                }
              /*  AppLog.e(TAG, "visibleItemCount " + visibleItemCount);
                AppLog.e(TAG, "totalItemCount " + totalItemCount);
                AppLog.e(TAG, "firstVisibleItemPosition " + firstVisibleItemPosition);
                AppLog.e(TAG, "lastVisibleItemPosition " + lastVisibleItemPosition);
*/
                if (!mIsLoading && totalPages > currentPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                    ) {
                        currentPage = currentPage + 1;
                        AppLog.e(TAG, "onScrollCalled " + currentPage);
                        getData(false);
                    }
                }

            }
        });

        mBinding.swipeRefreshLayout.setEnabled(false);

       /* mBinding.swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    currentPage = 1;
                    getData(false);
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                } else {
                    showNoNetworkMsg();
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                }
            }
        });*/


    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //NOFIREBASEDATABASE
     /*   if(query !=null)
            query.removeEventListener(firebaseNewPostListener);*/

    }

    private void getGroupPostLocaly() {


        eventTBL = EventTBL.getGroupEvent(mGroupId);
        boolean apiEvent = false;
        if(eventTBL!=null){
            if(eventTBL._now ==0){
                apiEvent = true;
            }
            if(MixOperations.isNewEvent(eventTBL.eventAt,"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",eventTBL._now)){
                apiEvent = true;
            }

        }

        List<PostDataItem> dataItemList = PostDataItem.getGeneralPosts(mGroupId + "");
        String lastId = null;
        if (dataItemList.size() != 0) {
            showLoadingBar(mBinding.progressBar);
            for (int i = 0; i < dataItemList.size(); i++) {

                PostItem postItem = new PostItem();
                postItem.id = dataItemList.get(i).id;

                if (i == 0) {
                    lastId = dataItemList.get(i).id;
                }

                postItem.createdById = dataItemList.get(i).createdById;
                postItem.createdBy = dataItemList.get(i).createdBy;
                postItem.createdByImage = dataItemList.get(i).createdByImage;
                postItem.createdAt = dataItemList.get(i).createdAt;
                postItem.title = dataItemList.get(i).title;
                postItem.fileType = dataItemList.get(i).fileType;
                postItem.fileName = new Gson().fromJson(dataItemList.get(i).fileName, new TypeToken<ArrayList<String>>() {
                }.getType());
                postItem.thumbnailImage = new Gson().fromJson(dataItemList.get(i).thumbnailImage, new TypeToken<ArrayList<String>>() {
                }.getType());
                postItem.updatedAt = dataItemList.get(i).updatedAt;
                postItem.text = dataItemList.get(i).text;
                postItem.imageWidth = dataItemList.get(i).imageWidth;
                postItem.imageHeight = dataItemList.get(i).imageHeight;
                postItem.video = dataItemList.get(i).video;
                postItem.text = dataItemList.get(i).text;
                postItem.likes = dataItemList.get(i).likes;
                postItem.comments = dataItemList.get(i).comments;
                postItem.isLiked = dataItemList.get(i).isLiked;
                postItem.isFavourited = dataItemList.get(i).isFavourited;
                postItem.canEdit = dataItemList.get(i).canEdit;
                postItem.phone = dataItemList.get(i).phone;
                postItem.thumbnail = dataItemList.get(i).thumbnail;

                PostList.add(postItem);
            }
            hideLoadingBar();
            AppLog.e(TAG, "DataFromLocal");
            mAdapter.notifyDataSetChanged();

            mBinding.setSize(mAdapter.getItemCount());
            firebaseListen(lastId,apiEvent);

        } else {
            firebaseListen("", apiEvent);
            mBinding.setSize(0);
        }

        /*    if (isConnectionAvailable()) {
                AppLog.e(TAG, "DataFromAPI BG");
                getData(true);
            } else {
                mBinding.setSize(mAdapter.getItemCount());
            }


        } else {
            if (isConnectionAvailable()) {
                getData(false);
                AppLog.e(TAG, "DataFromAPI");
            } else {
                mBinding.setSize(0);
            }
        }*/
    }

    private void firebaseListen(String lastIdFromDB, boolean apiEvent) {
        AppLog.e(TAG, "firebaseListen called : " + lastIdFromDB);
        if (TextUtils.isEmpty(lastIdFromDB)){
            getData(false);
        }else if (leafPreference.getInt(mGroupId + "_post") > 0
                || leafPreference.getBoolean(mGroupId + "_post_delete") || apiEvent){
            leafPreference.setBoolean(mGroupId + "_post_delete",false);
            getData(true);
        }
    }

    private void init() {
        liked = false;

        databaseHandler = new DatabaseHandler(getActivity());
        count = databaseHandler.getCount();
        type = getArguments().getString("type");

        Log.e(TAG,"type"+type);

        mBinding.setSize(1);
        mBinding.setMessage(R.string.msg_no_post);
        mGroupId = getArguments().getString("id");

        mBinding.llBlankView.setOnClickListener(this);
        mBinding.llReport.setOnClickListener(this);
        mBinding.llRemovePost.setOnClickListener(this);
        mBinding.llAskDoubt.setOnClickListener(this);

        // Post RecyclerView
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mAdapter = new PostAdapter(PostList, this, "group", databaseHandler, count);
        mBinding.recyclerView.setAdapter(mAdapter);

    }


    @Override
    public void onResume() {
        super.onResume();

        AppLog.e(TAG, "onResume : " + LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISGENERALPOSTUPDATED));
        if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISGENERALPOSTUPDATED)) {
            mAdapter.clear();
            currentPage = 1;
            getData(false);
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISGENERALPOSTUPDATED, false);
        }
    }

    private void getData(boolean isInBackground) {
        AppLog.e(TAG, "getData called");
        if (!isInBackground) {
            showLoadingBar(mBinding.progressBar);
            mIsLoading = true;
        }

        manager.getGeneralPosts(this, mGroupId + "", currentPage);
        leafPreference.remove(mGroupId + "_post");
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
        switch (apiId) {
            case LeafManager.API_ID_GENERAL_POST:
                PostResponse res = (PostResponse) response;
                AppLog.e(TAG, "Post Res ; " + new Gson().toJson(res.getResults()));

                if (currentPage == 1) {
                    PostDataItem.deleteGeneralPosts(mGroupId + "");
                    PostList.clear();

                    PostList.addAll(res.getResults());
                    AppLog.e(TAG, "current page 1");

                } else {
                    PostList.addAll(res.getResults());
                    AppLog.e(TAG, "current page " + currentPage);
                }
                mBinding.setSize(mAdapter.getItemCount());
                mAdapter.notifyDataSetChanged();

                totalPages = res.totalNumberOfPages;
                mIsLoading = false;

                savePostData(res.getResults());

                if(eventTBL!=null){
                    eventTBL._now = System.currentTimeMillis();
                    eventTBL.save();
                }
                break;

            case LeafManager.API_ID_FAV:
                if (response.status.equalsIgnoreCase("favourite")) {
                    PostList.get(position).isFavourited = true;
                } else {
                    PostList.get(position).isFavourited = false;
                }
                mAdapter.notifyItemChanged(position);
                PostItem select = PostList.get(position);
                PostDataItem.updateFav(select.id,select.isFavourited?1:0);

                break;

            case LeafManager.API_ID_LIKE:
                if (response.status.equalsIgnoreCase("liked")) {
                    PostList.get(position).isLiked = true;
                    PostList.get(position).likes++;
                } else {
                    PostList.get(position).isLiked = false;
                    PostList.get(position).likes--;
                }
                mAdapter.notifyItemChanged(position);
                liked = false;

                PostItem select2 = PostList.get(position);
                PostDataItem.updateLike(select2.id,select2.isLiked?1:0,select2.likes);
                break;

            case LeafManager.API_ID_DELETE_POST:
                Toast.makeText(getContext(), getResources().getString(R.string.toast_post_delete_successfully), Toast.LENGTH_SHORT).show();
                PostList.clear();
                currentPage = 1;
                getData(false);
                AmazoneRemove.remove(currentItem.fileName);
                sendNotification(currentItem);
                break;

            case LeafManager.API_REPORT_LIST:
                hideLoadingBar();
                ReportResponse response1 = (ReportResponse) response;

                list.clear();

                for (int i = 0; i < response1.data.size(); i++) {
                    ReportItemList reportItemList = new ReportItemList(response1.data.get(i).type, response1.data.get(i).reason, false);
                    list.add(reportItemList);
                }
                ReportAdapter.selected_position = -1;
                mAdapter2 = new ReportAdapter(list);
                AppLog.e("adas ", mAdapter2.getItemCount() + "");
                AppLog.e("ReportResponse", "onSucces  ,, msg : " + ((ReportResponse) response).data.toString());
                dialogRecyclerView.setAdapter(mAdapter2);
                break;

            case LeafManager.API_REPORT:
                hideLoadingBar();
                Toast.makeText(getContext(), getResources().getString(R.string.toast_post_reported_sucessfully), Toast.LENGTH_SHORT).show();
                break;
        }

    }

    private void sendNotification(PostItem currentItem) {

        SendNotificationModel notificationModel = new SendNotificationModel();
        notificationModel.to = "/topics/" + mGroupId;
        notificationModel.data.title = getResources().getString(R.string.app_name);
        notificationModel.data.body = "Group Post deleted";
        notificationModel.data.Notification_type = "DELETE_POST";
        notificationModel.data.iSNotificationSilent = true;
        notificationModel.data.groupId = mGroupId;
        notificationModel.data.teamId = "";
        notificationModel.data.createdById = LeafPreference.getInstance(getActivity()).getString(LeafPreference.LOGIN_ID);
        notificationModel.data.postId = currentItem.id;
        notificationModel.data.postType = "group";
        SendNotificationGlobal.send(notificationModel);
    }

    private void savePostData(List<PostItem> results) {

        for (int i = 0; i < results.size(); i++) {

            PostItem item = results.get(i);
            PostDataItem postItem = new PostDataItem();

            postItem.id = item.id;
            postItem.createdById = item.createdById;
            postItem.createdBy = item.createdBy;
            postItem.createdByImage = item.createdByImage;
            postItem.createdAt = item.createdAt;
            postItem.title = item.title;
            postItem.fileType = item.fileType;
            if (item.fileName != null) {
                postItem.fileName = new Gson().toJson(item.fileName);
                postItem.thumbnailImage = new Gson().toJson(item.thumbnailImage);
            }
            postItem.updatedAt = item.updatedAt;
            postItem.text = item.text;
            postItem.imageHeight = item.imageHeight;
            postItem.imageWidth = item.imageWidth;
            postItem.video = item.video;
            postItem.text = item.text;
            postItem.likes = item.likes;
            postItem.comments = item.comments;
            postItem.isLiked = item.isLiked;
            postItem.isFavourited = item.isFavourited;
            postItem.canEdit = item.canEdit;
            postItem.phone = item.phone;
            postItem.type = "group";
            postItem.group_id = mGroupId + "";
            postItem.thumbnail = item.thumbnail;

            postItem.save();
        }

    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<AddPostValidationError> error) {
        hideLoadingBar();
        mIsLoading = false;
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
        liked = false;
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
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_post), Toast.LENGTH_SHORT).show();
        } else if (msg.contains("418")) {
            if (apiId == LeafManager.API_REPORT)
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_already_reported), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
        liked = false;
    }


    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        mIsLoading = false;
        currentPage = currentPage - 1;
        if (currentPage < 0) {
            currentPage = 1;
        }
        liked = false;
        if (getActivity() != null)
            Toast.makeText(getActivity(), getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFavClick(PostItem item, int pos) {
        showLoadingBar(mBinding.progressBar);
        position = pos;
        int fav = 0;
        if (item.isFavourited) {
            fav = 0;
        } else {
            fav = 1;
        }
        manager.setFav(this, mGroupId + "", item.id);

        cleverTapFav(item, fav);
    }

    private void cleverTapFav(PostItem item, int fav) {
        if (isConnectionAvailable()) {
            try {
                CleverTapAPI cleverTap = CleverTapAPI.getInstance(getActivity());
                AppLog.e("GeneralPost", "Success to found cleverTap objects=>");
                if (fav == 1) {
                    HashMap<String, Object> favAction = new HashMap<String, Object>();
                    favAction.put("id", mGroupId);
                    favAction.put("group_name", GroupDashboardActivityNew.group_name);
                    favAction.put("post_id", item.id);
                    favAction.put("post_title", item.title);
                    cleverTap.event.push("Add Favorite", favAction);
                } else {
                    HashMap<String, Object> favAction = new HashMap<String, Object>();
                    favAction.put("id", mGroupId);
                    favAction.put("group_name", GroupDashboardActivityNew.group_name);
                    favAction.put("post_id", item.id);
                    favAction.put("post_title", item.title);
                    cleverTap.event.push("Remove Favorite", favAction);
                }
                AppLog.e("GeneralPost", "Success to found cleverTap objects=>");

            } catch (CleverTapMetaDataNotFoundException e) {
                AppLog.e("GeneralPost", "CleverTapMetaDataNotFoundException=>" + e.toString());
                // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
            } catch (CleverTapPermissionsNotSatisfied e) {
                AppLog.e("GeneralPost", "CleverTapPermissionsNotSatisfied=>" + e.toString());
                // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
            } catch (Exception ignored) {
            }

        }
    }

    @Override
    public void onLikeClick(PostItem item, int position) {
        if (!liked) {
            liked = true;
            this.position = position;
            showLoadingBar(mBinding.progressBar);
            manager.setLikes(this, mGroupId + "", item.id);


            int fav = 0;
            if (item.isLiked) {
                fav = 0;
            } else {
                fav = 1;
            }
            cleverTapLike(item, fav);
        }
    }

    private void cleverTapLike(PostItem item, int fav) {
        if (isConnectionAvailable()) {
            try {
                CleverTapAPI cleverTap = CleverTapAPI.getInstance(getActivity());
                AppLog.e("GeneralPost", "Success to found cleverTap objects=>");

                HashMap<String, Object> likeAction = new HashMap<String, Object>();
                likeAction.put("id", mGroupId);
                likeAction.put("group_name", GroupDashboardActivityNew.group_name);
                likeAction.put("post_id", item.id);
                if (fav == 1) {
                    likeAction.put("isLiked", true);
                } else {
                    likeAction.put("isLiked", false);
                }
                cleverTap.event.push("Liked", likeAction);
                AppLog.e("GeneralPost", "Success");

            } catch (CleverTapMetaDataNotFoundException e) {
                AppLog.e("GeneralPost", "CleverTapMetaDataNotFoundException=>" + e.toString());
                // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
            } catch (CleverTapPermissionsNotSatisfied e) {
                AppLog.e("GeneralPost", "CleverTapPermissionsNotSatisfied=>" + e.toString());
                // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
            }

        }


    }

    @Override
    public void onPostClick(PostItem item) {

        Log.e(TAG,"onPostClick"+item.fileType);

        if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
            Intent browserIntent = new Intent(getActivity(), TestActivity.class);
            browserIntent.putExtra("url", item.video);
            startActivity(browserIntent);

        } else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
            Intent i = new Intent(getActivity(), ViewPDFActivity.class);
            i.putExtra("pdf", item.fileName.get(0));
            i.putExtra("thumbnail", item.thumbnailImage.get(0));
            i.putExtra("name", item.title);
            startActivity(i);

        } else if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
            Intent i = new Intent(getActivity(), FullScreenActivity.class);
            i.putExtra("image", item.fileName);
            startActivity(i);
        }

    }

    @Override
    public void onReadMoreClick(PostItem item) {
    }

    @Override
    public void onEditClick(PostItem item) {
    }

    @Override
    public void onDeleteClick(PostItem item) {
        currentItem = item;
        SMBDialogUtils.showSMBDialogOKCancel(getActivity(), getResources().getString(R.string.dialog_are_you_want_to_delete), this);
    }

    @Override
    public void onReportClick(PostItem item) {
        currentItem = item;
        showReportDialog();
    }

    @Override
    public void onShareClick(PostItem item) {
        GroupDashboardActivityNew.is_share_edit = true;
        GroupDashboardActivityNew.share_type = "group";
        GroupDashboardActivityNew.share_title = item.title;
        GroupDashboardActivityNew.share_desc = item.text;
        GroupDashboardActivityNew.share_image = item.image;
        GroupDashboardActivityNew.imageHeight = item.imageHeight;
        GroupDashboardActivityNew.imageWidth = item.imageWidth;

        if (item.image != null && !item.image.isEmpty()) {
            GroupDashboardActivityNew.share_image_type = 1;
        } else if (item.pdf != null) {
            GroupDashboardActivityNew.share_image_type = 2;
        } else if (item.video != null) {
            GroupDashboardActivityNew.share_image_type = 3;
        } else {
            GroupDashboardActivityNew.share_image_type = 4;
        }

        AppLog.e("desc", "desc1 is " + GroupDashboardActivityNew.share_desc);
        AppLog.e("desc", "desc1 is " + item.text);
        Intent intent = new Intent(getActivity(), SelectShareTypeActivity.class);
        intent.putExtra("group_id", mGroupId);
        intent.putExtra("post_id", item.id);
        AppLog.e("SHARE", "Post id2 is " + item.id);
        startActivity(intent);
        AppLog.e("SHARE", "Post id1 is " + item.id);
    }

    @Override
    public void onQueClick(PostItem item) {
        Intent intent = new Intent(getActivity(), AddQuestionActivity.class);
        intent.putExtra("id", mGroupId);
        intent.putExtra("post_id", item.id);
        intent.putExtra("type", "que");
        startActivity(intent);
    }

    @Override
    public void onPushClick(PostItem item) {
        GroupDashboardActivityNew.is_share_edit = true;
        GroupDashboardActivityNew.share_type = "group";
        GroupDashboardActivityNew.share_title = item.title;
        GroupDashboardActivityNew.share_desc = item.text;

        GroupDashboardActivityNew.share_image = item.image;
        GroupDashboardActivityNew.imageHeight = item.imageHeight;
        GroupDashboardActivityNew.imageWidth = item.imageWidth;
        if (TextUtils.isEmpty(item.fileType)) {
            GroupDashboardActivityNew.share_image_type = 4;
        } else if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
            GroupDashboardActivityNew.share_image_type = 1;
        } else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
            GroupDashboardActivityNew.share_image_type = 2;
        } else if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
            GroupDashboardActivityNew.share_image_type = 3;
        } else {
            GroupDashboardActivityNew.share_image_type = 4;
        }

        Intent intent = new Intent(getActivity(), PushActivity.class);
        intent.putExtra("id", mGroupId);
        intent.putExtra("post_id", item.id);
        startActivity(intent);
    }

    @Override
    public void onNameClick(PostItem item) {
        if (item.createdById.equals(LeafPreference.getInstance(getActivity()).getString(LeafPreference.LOGIN_ID))) {
            AppLog.e("onNameClick", "else if called");
            Intent intent = new Intent(getActivity(), ProfileActivity2.class);
            startActivity(intent);
        } else {
            AppLog.e("onNameClick", "else called");
        }
    }

    @Override
    public void onLikeListClick(PostItem item) {
        if (isConnectionAvailable()) {
            Intent intent = new Intent(getActivity(), LikesListActivity.class);
            intent.putExtra("id", mGroupId);
            intent.putExtra("post_id", item.id);
            intent.putExtra("type", "group");
            startActivity(intent);
        } else {
            showNoNetworkMsg();
        }
    }

    @Override
    public void onMoreOptionClick(PostItem item) {
        currentItem = item;
        mBinding.rlPush.setVisibility(View.VISIBLE);
        mBinding.llReport.setVisibility(View.VISIBLE);
        mBinding.llRemovePost.setVisibility(View.VISIBLE);
        mBinding.llAskDoubt.setVisibility(View.VISIBLE);
        if (!item.canEdit) {
            mBinding.llRemovePost.setVisibility(View.GONE);
        }

        if (!GroupDashboardActivityNew.mAllowPostQuestion) {
            mBinding.llAskDoubt.setVisibility(View.GONE);
        }

    }

    @Override
    public void onDeleteVideoClick(PostItem item, int adapterPosition) {
        AppLog.e(TAG, "onDeleteVideoClick : " + item.fileName.get(0));
        if (item.fileName != null && item.fileName.size() > 0) {
            AmazoneDownload.removeVideo(getActivity(), item.fileName.get(0));
            mAdapter.notifyItemChanged(adapterPosition);
        }
    }

    private static String[] fromString(String string) {
//        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        /* String result[] = new String[strings.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = strings[i];
        }*/
        if (string != null && !string.equals("null"))
            return string.replace("[", "").replace("]", "").split(", ");
        else
            return new String[0];
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        AppLog.e("TeamPostFrag", "DIalog Ok Clicked ");
        if (isConnectionAvailable()) {
            showLoadingBar(mBinding.progressBar);
            LeafManager manager = new LeafManager();
            manager.deletePost(this, mGroupId + "", currentItem.id, "group");
            new SendNotification("").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            showNoNetworkMsg();
        }

    }

    public void showReportDialog() {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_layout_report_list);

        dialogRecyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);

        dialogProgressBar = (ProgressBar) dialog.findViewById(R.id.progressBar);

        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        TextView tv_report = (TextView) dialog.findViewById(R.id.tv_report);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tv_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendReport(list.get(ReportAdapter.selected_position).type);
                } catch (ArrayIndexOutOfBoundsException e) {
                    AppLog.e("Report", "error is " + e.toString());
                }
                dialog.dismiss();
            }
        });

        final LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        dialogRecyclerView.setLayoutManager(manager);
        getReportData();

        dialog.show();
    }

    private void getReportData() {
        LeafManager mManager = new LeafManager();
        showLoadingBar(dialogProgressBar);
        mManager.getReportList(this);
    }

    private void sendReport(int report_id) {
        LeafManager mManager = new LeafManager();
        showLoadingBar(mBinding.progressBar);
        mManager.reportPost(this, mGroupId + "", currentItem.id, report_id);
    }

    @Override
    public void onClick(View v) {
    }


    private void showContextMenu(View view, boolean isModifyMenu) {


        if (mBinding.llMediaPost.getVisibility() == View.GONE) {
            // to show the layout when icon is tapped
            mBinding.llMediaPost.setVisibility(View.VISIBLE);

            if (isModifyMenu) {
                mBinding.llRemove.setVisibility(View.VISIBLE);
            } else {
                mBinding.llRemove.setVisibility(View.INVISIBLE);
            }
        } else {
            mBinding.llMediaPost.setVisibility(View.GONE);
        }


    }

    private class SendNotification extends AsyncTask<String, String, String> {
        String receiverToken;
        private String server_response;

        public SendNotification(String token) {
            receiverToken = token;
        }

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL("https://fcm.googleapis.com/fcm/send");
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", BuildConfig.API_KEY_FIREBASE1 + BuildConfig.API_KEY_FIREBASE2);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                try {
                    JSONObject object = new JSONObject();

                    String topic;
                    String title = getResources().getString(R.string.app_name);
                    String message = "";
                    String userName = LeafPreference.getInstance(GeneralPostFragment.this.getActivity()).getString(LeafPreference.NAME);


                    message = userName + " has deleted a post.";
                    object.put("to", receiverToken);

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title", title);
                    notificationObj.put("body", message);
                    //   object.put("notification", notificationObj);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("groupId", mGroupId);
                    dataObj.put("createdById", LeafPreference.getInstance(GeneralPostFragment.this.getActivity()).getString(LeafPreference.LOGIN_ID));
                    dataObj.put("postId", "");
                    dataObj.put("teamId", mGroupId);
                    dataObj.put("title", title);
                    dataObj.put("postType", "group");
                    dataObj.put("Notification_type", "post");
                    dataObj.put("body", message);
                    object.put("data", dataObj);

                    wr.writeBytes(object.toString());
                    Log.e(TAG, " JSON input : " + object.toString());
                    wr.flush();
                    wr.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                AppLog.e(TAG, "responseCode :" + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_response = readStream(urlConnection.getInputStream());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return server_response;
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            AppLog.e(TAG, "server_response :" + server_response);

            if (!TextUtils.isEmpty(server_response)) {
                AppLog.e(TAG, "Notification Sent");
            } else {
                AppLog.e(TAG, "Notification Send Fail");
            }
        }
    }


}
