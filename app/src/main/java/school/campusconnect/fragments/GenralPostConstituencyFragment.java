package school.campusconnect.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.activeandroid.ActiveAndroid;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.activities.AddFriendActivity;
import school.campusconnect.activities.AddPostActivity;
import school.campusconnect.activities.AddQuestionActivity;
import school.campusconnect.activities.FullScreenActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.LikesListActivity;
import school.campusconnect.activities.ProfileActivity2;
import school.campusconnect.activities.PushActivity;
import school.campusconnect.activities.SelectShareTypeActivity;
import school.campusconnect.activities.TestActivity;
import school.campusconnect.activities.ViewPDFActivity;
import school.campusconnect.adapters.PostAdapter;
import school.campusconnect.adapters.ReportAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.LayoutListButtonBinding;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.EventTBL;
import school.campusconnect.datamodel.LoginRequest;
import school.campusconnect.datamodel.PostDataItem;
import school.campusconnect.datamodel.PostItem;
import school.campusconnect.datamodel.PostResponse;
import school.campusconnect.datamodel.reportlist.ReportItemList;
import school.campusconnect.datamodel.reportlist.ReportResponse;
import school.campusconnect.firebase.SendNotificationGlobal;
import school.campusconnect.firebase.SendNotificationModel;
import school.campusconnect.fragments.DashboardNewUi.BaseTeamFragmentv3;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AmazoneImageDownload;
import school.campusconnect.utils.AmazoneMultiImageDownload;
import school.campusconnect.utils.AmazoneRemove;
import school.campusconnect.utils.AmazoneVideoDownload;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.DateTimeHelper;
import school.campusconnect.utils.MixOperations;
import school.campusconnect.views.SMBDialogUtils;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

public class GenralPostConstituencyFragment extends BaseFragment implements LeafManager.OnCommunicationListener,
        PostAdapter.OnItemClickListener, DialogInterface.OnClickListener, LeafManager.OnAddUpdateListener<AddPostValidationError>, View.OnClickListener {

    private static final String TAG = "GenralPostConstFragment";
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

    ArrayList<Integer> birthdayPostCreationQueue ; /// QUEUE TO MAINTAIN BIRTHDAY POST TASKS... SO IT DOESNT REPEAT.

    //private Query query;

    public GenralPostConstituencyFragment() {

    }

    public static GenralPostConstituencyFragment newInstance(String groupId,String type) {
        GenralPostConstituencyFragment fragment = new GenralPostConstituencyFragment();
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
        leafPreference = LeafPreference.getInstance(GenralPostConstituencyFragment.this.getActivity());
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
                            && firstVisibleItemPosition >= 0)
                    {
                        currentPage = currentPage + 1;
                        AppLog.e(TAG, "onScrollCalled " + currentPage);
                      //  getData(false);

                        getGroupPostLocaly();
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

 

    private void getGroupPostLocaly() {



        boolean apiEvent = false;


        if (PostDataItem.getLastGeneralPost().size() > 0)
        {
            if (MixOperations.isNewEventUpdate(LeafPreference.getInstance(getContext()).getString("ANNOUNCEMENT_POST"), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", PostDataItem.getLastGeneralPost().get(0)._now)) {
                apiEvent = true;
                PostDataItem.deleteGeneralPosts(mGroupId);
                PostDataItem.deleteGeneralPostsBirthday(mGroupId);
            }

        }

        List<PostDataItem> dataItemList = PostDataItem.getGeneralPosts(mGroupId,currentPage);

        AppLog.e(TAG , "data item list size : "+dataItemList.size() + " , apieVent  :"+apiEvent);
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
                postItem.type = dataItemList.get(i).type;
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
            AppLog.e(TAG, "DataFromLocal : lastId : "+lastId);
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

        if (!LeafPreference.getInstance(getContext()).getString("totalPageGeneralPostConstituency").isEmpty())
        {
            totalPages = Integer.parseInt(LeafPreference.getInstance(getContext()).getString("totalPageGeneralPostConstituency"));
        }

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
            PostDataItem.deleteGeneralPosts(mGroupId);
            PostDataItem.deleteGeneralPostsBirthday(mGroupId);
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
    public void onDestroy() {
        super.onDestroy();

        mAdapter.RemoveAll();
        //NOFIREBASEDATABASE
     /*   if(query !=null)
            query.removeEventListener(firebaseNewPostListener);*/

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
                    PostDataItem.deleteGeneralPostsBirthday(mGroupId);
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
                LeafPreference.getInstance(getContext()).setString("totalPageGeneralPostConstituency",String.valueOf(totalPages));
                mIsLoading = false;

                savePostData(res.getResults());

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
                Toast.makeText(getContext(), "Post Deleted Successfully", Toast.LENGTH_SHORT).show();
                PostList.clear();
                PostDataItem.deleteGeneralPosts(mGroupId);
                PostDataItem.deleteGeneralPostsBirthday(mGroupId);
                mAdapter.notifyDataSetChanged();
                AmazoneRemove.remove(currentItem.fileName);
                currentPage = 1;
                getData(false);
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
                Toast.makeText(getContext(), "Post Reported Successfully", Toast.LENGTH_SHORT).show();
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

    private void savePostData(List<PostItem> results)
    {

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

            if(item.type.equalsIgnoreCase("birthdayPost"))
            postItem.type = item.type;
            else
            postItem.type = "group";

            postItem.group_id = mGroupId + "";
            postItem.page = currentPage;
            postItem.thumbnail = item.thumbnail;

            if (!LeafPreference.getInstance(getContext()).getString("ANNOUNCEMENT_POST").isEmpty())
            {
                postItem._now = LeafPreference.getInstance(getContext()).getString("ANNOUNCEMENT_POST");
            }
            else
            {
                postItem._now = DateTimeHelper.getCurrentTime();
            }

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
            Toast.makeText(getActivity(), "No posts available.", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), "No posts available.", Toast.LENGTH_SHORT).show();
        } else if (msg.contains("418")) {
            if (apiId == LeafManager.API_REPORT)
                Toast.makeText(getActivity(), "You have already reported this post", Toast.LENGTH_SHORT).show();
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
    public void onExternalShareClick(PostItem item) {

        boolean isDownloaded = true;

        if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {


            if (item.fileName.size()> 0)
            {
                for (int i = 0;i<item.fileName.size();i++)
                {
                    if (!AmazoneImageDownload.isImageDownloaded((item.fileName.get(i))))
                    {
                        isDownloaded = false;
                    }
                }

                if (isDownloaded)
                {
                    ArrayList<File> files =new ArrayList<>();

                    for (int i = 0;i<item.fileName.size();i++)
                    {
                        AppLog.e(TAG, "URL DECODE"+Constants.decodeUrlToBase64(item.fileName.get(i)));

                        files.add(AmazoneImageDownload.getDownloadPath(item.fileName.get(i)));
                    }

                    ArrayList<Uri> uris = new ArrayList<>();

                    for(File file: files){

                        AppLog.e(TAG, "URL "+file.getAbsolutePath());

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            uris.add(FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file));
                        } else {
                            uris.add(Uri.fromFile(file));
                        }

                    }

                    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    intent.setType("image/");
                    intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    startActivity(Intent.createChooser(intent, "Share File"));
                }
                else
                {
                    Toast.makeText(getContext(),getResources().getString(R.string.smb_no_file_download),Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(getContext(),getResources().getString(R.string.smb_no_file_attached),Toast.LENGTH_SHORT).show();
            }


        }
        else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {

            if (item.fileName.size()> 0)
            {

                for (int i = 0;i<item.fileName.size();i++)
                {
                    if (!AmazoneDownload.isPdfDownloaded((item.fileName.get(i))))
                    {
                        isDownloaded = false;
                    }
                }

                if (isDownloaded)
                {
                    ArrayList<File> files =new ArrayList<>();

                    for (int i = 0;i<item.fileName.size();i++)
                    {
                        AppLog.e(TAG, "URL DECODE"+Constants.decodeUrlToBase64(item.fileName.get(i)));

                        files.add(AmazoneDownload.getDownloadPath(item.fileName.get(i)));
                    }

                    ArrayList<Uri> uris = new ArrayList<>();

                    for(File file: files){

                        AppLog.e(TAG, "URL "+file.getAbsolutePath());

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            uris.add(FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file));
                        } else {
                            uris.add(Uri.fromFile(file));
                        }

                    }

                    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    intent.setType("application/pdf");
                    intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    startActivity(Intent.createChooser(intent, "Share File"));
                }
                else
                {
                    Toast.makeText(getContext(),getResources().getString(R.string.smb_no_file_download),Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(getContext(),getResources().getString(R.string.smb_no_file_attached),Toast.LENGTH_SHORT).show();
            }

        }
        else if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, item.video);
            intent.setType("text/plain");
            startActivity(intent);
        }
        else if(item.fileType.equals(Constants.FILE_TYPE_VIDEO)){

            if (item.fileName.size()> 0)
            {

                for (int i = 0;i<item.fileName.size();i++)
                {
                    if (!AmazoneVideoDownload.isVideoDownloaded((item.fileName.get(i))))
                    {
                        isDownloaded = false;
                    }
                }

                if (isDownloaded)
                {
                    ArrayList<File> files =new ArrayList<>();

                    for (int i = 0;i<item.fileName.size();i++)
                    {
                        AppLog.e(TAG, "URL DECODE"+Constants.decodeUrlToBase64(item.fileName.get(i)));

                        files.add(AmazoneVideoDownload.getDownloadPath(item.fileName.get(i)));
                    }

                    ArrayList<Uri> uris = new ArrayList<>();

                    for(File file: files){

                        AppLog.e(TAG, "URL "+file.getAbsolutePath());

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            uris.add(FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file));
                        } else {
                            uris.add(Uri.fromFile(file));
                        }

                    }

                    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    intent.setType("video/*");
                    intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    startActivity(Intent.createChooser(intent, "Share File"));
                }
                else
                {
                    Toast.makeText(getContext(),getResources().getString(R.string.smb_no_file_download),Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(getContext(),getResources().getString(R.string.smb_no_file_attached),Toast.LENGTH_SHORT).show();
            }

        }
        else if(item.fileType.equalsIgnoreCase("birthdaypost")){

            if (item.fileName.size()> 0)
            {

                for (int i = 0;i<item.fileName.size();i++)
                {
                    if (!AmazoneImageDownload.isImageDownloaded((item.fileName.get(i))))
                    {
                        isDownloaded = false;
                    }
                }

                if (isDownloaded)
                {
                    ArrayList<File> files =new ArrayList<>();

                    for (int i = 0;i<item.fileName.size();i++)
                    {
                        AppLog.e(TAG, "URL DECODE"+Constants.decodeUrlToBase64(item.fileName.get(i)));

                        files.add(AmazoneImageDownload.getDownloadPath(item.fileName.get(i)));
                    }

                    ArrayList<Uri> uris = new ArrayList<>();

                    for(File file: files){

                        AppLog.e(TAG, "URL "+file.getAbsolutePath());

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            uris.add(FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file));
                        } else {
                            uris.add(Uri.fromFile(file));
                        }

                    }

                    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    intent.setType("image/");
                    intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    startActivity(Intent.createChooser(intent, "Share File"));
                }
                else
                {
                    Toast.makeText(getContext(),getResources().getString(R.string.smb_no_file_download),Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(getContext(),getResources().getString(R.string.smb_no_file_attached),Toast.LENGTH_SHORT).show();
            }
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

    @Override
    public void callBirthdayPostCreation(PostItem item, int position)
    {

        Log.e(TAG,"callBirthdayPostCreation"+position);
        if(birthdayPostCreationQueue == null)
        {
            birthdayPostCreationQueue = new ArrayList<>();
        }

        if(birthdayPostCreationQueue.contains(position))
        {
            Log.e(TAG,"callBirthdayPostCreation contains"+position);
            return;
        }

        birthdayPostCreationQueue.add(position);
        createBirthPostAndSave(item ,position);

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
                    String userName = LeafPreference.getInstance(GenralPostConstituencyFragment.this.getActivity()).getString(LeafPreference.NAME);


                    message = userName + " has deleted a post.";
                    object.put("to", receiverToken);

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title", title);
                    notificationObj.put("body", message);
                    //   object.put("notification", notificationObj);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("groupId", mGroupId);
                    dataObj.put("createdById", LeafPreference.getInstance(GenralPostConstituencyFragment.this.getActivity()).getString(LeafPreference.LOGIN_ID));
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

    Bitmap BirthdayTempleteBitmap = null;
    Bitmap UserBitmap = null;
    Bitmap MlaBitMap = null;
    File fileSaveLocation = null;

    private void createBirthPostAndSave(PostItem item , int position)
    {

        ArrayList<String> imageList = new ArrayList<>();
        imageList.add(item.fileName.get(0));
        imageList.add(item.bdayUserImage);
        imageList.add(item.createdByImage);

        if(getActivity() == null)
        {
            return;
        }



        AmazoneImageDownload.download(getActivity(), item.bdayUserImage , new AmazoneImageDownload.AmazoneDownloadSingleListener() {
            @Override
            public void onDownload(File file) {

                if(getActivity() == null)
                {
                    return;
                }
                if(file == null)
                {
                    return;
                }
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                UserBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),bmOptions);

                Log.e(TAG,"UserBitmap H "+ UserBitmap.getHeight());
                Log.e(TAG,"UserBitmap W "+ UserBitmap.getWidth());

                checkAllDownload(item.bdayUserName,item.createdBy,position,item.fileName.get(0));
            }

            @Override
            public void error(String msg) {

            }

            @Override
            public void progressUpdate(int progress, int max) {

            }
        });

        AmazoneImageDownload.download(getActivity(), item.createdByImage , new AmazoneImageDownload.AmazoneDownloadSingleListener() {
            @Override
            public void onDownload(File file) {

                if(getActivity() == null)
                {
                    return;
                }
                 if(file == null)
                {
                    return;
                }
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                MlaBitMap = BitmapFactory.decodeFile(file.getAbsolutePath(),bmOptions);

                Log.e(TAG,"UserBitmap H "+ MlaBitMap.getHeight());
                Log.e(TAG,"UserBitmap W "+ MlaBitMap.getWidth());

                checkAllDownload(item.bdayUserName,item.createdBy,position,item.fileName.get(0));
            }

            @Override
            public void error(String msg) {

            }

            @Override
            public void progressUpdate(int progress, int max) {

            }
        });



        /* AmazoneMultiImageDownload.download(getActivity(), imageList, new AmazoneMultiImageDownload.AmazoneDownloadMultiListener() {
            @Override
            public void onDownload(ArrayList<File> file) {

                Log.e(TAG,"onDownload "+position);

                if(getActivity() == null)
                {
                    return;
                }


                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap BirthdayTempleteBitmap = BitmapFactory.decodeFile(file.get(0).getAbsolutePath(),bmOptions);

                Log.e(TAG,"BirthdayTempleteBitmap H "+BirthdayTempleteBitmap.getHeight());
                Log.e(TAG,"BirthdayTempleteBitmap W "+BirthdayTempleteBitmap.getWidth());


                Bitmap UserBitmap,MlaBitMap;

                if(file.size()>1 && file.get(1)!=null )
                {
                    UserBitmap = BitmapFactory.decodeFile(file.get(1).getAbsolutePath(),bmOptions);
                    Log.e(TAG,"UserBitmap if");
                }
                else
                {
                    UserBitmap = drawableToBitmap(getActivity().getResources().getDrawable(R.drawable.user));
                    Log.e(TAG,"UserBitmap else");
                }

                if(file.size()>2 && file.get(2)!=null )
                {
                    MlaBitMap = BitmapFactory.decodeFile(file.get(2).getAbsolutePath(),bmOptions);
                    Log.e(TAG,"MlaBitMap if");
                }
                else
                {
                    MlaBitMap = drawableToBitmap(getActivity().getResources().getDrawable(R.drawable.mla));
                    Log.e(TAG,"MlaBitMap else");
                }

                Log.e(TAG,"UserBitmap H "+UserBitmap.getHeight());
                Log.e(TAG,"UserBitmap W "+UserBitmap.getWidth());

                if(item.bdayUserName ==null || item.bdayUserName.equalsIgnoreCase(""))
                {
                    item.bdayUserName = "Username";
                }

                createBitmap(BirthdayTempleteBitmap,MlaBitMap,UserBitmap, item.bdayUserName,file.get(0),item.createdBy);
                birthdayPostCreationQueue.remove(Integer.valueOf(position));
                Log.e(TAG,"mAdapter notifyItemChanged"+position);
                mAdapter.notifyItemChanged(position);

                Log.e(TAG , "created Bitmap saved at : "+file.get(0));
            }
            @Override
            public void error(String msg) {

            }

            @Override
            public void progressUpdate(int progress, int max) {

            }
        });*/


    }


    public void checkAllDownload(String UserName,String mlaName,int position,String file)
    {
        if (UserBitmap != null && MlaBitMap != null)
        {

            AmazoneImageDownload.download(getActivity(), file, new AmazoneImageDownload.AmazoneDownloadSingleListener() {
                @Override
                public void onDownload(File file) {

                    if(getActivity() == null)
                    {
                        return;

                    }
                    if(file == null)
                    {
                        return;
                    }

                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    BirthdayTempleteBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),bmOptions);

                    createBitmap(BirthdayTempleteBitmap,MlaBitMap,UserBitmap, UserName,file,mlaName);
                    birthdayPostCreationQueue.remove(Integer.valueOf(position));
                    Log.e(TAG,"mAdapter notifyItemChanged"+position);
                    mAdapter.notifyItemChanged(position);
                    Log.e(TAG , "created Bitmap saved at : "+file);

                }

                @Override
                public void error(String msg) {

                }

                @Override
                public void progressUpdate(int progress, int max) {

                }
            });

        }
    }


    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

     private File createBitmap(Bitmap birthdayTempleteBitmap, Bitmap mlaBitmap, Bitmap userBitmap , String userName, File file,String mlaName) {

        Bitmap result = Bitmap.createBitmap(birthdayTempleteBitmap.getWidth(), birthdayTempleteBitmap.getHeight(), birthdayTempleteBitmap.getConfig());

        userBitmap = getResizedBitmap(userBitmap, (int) (195*(birthdayTempleteBitmap.getWidth()/540.0f)),(int) (195*(birthdayTempleteBitmap.getWidth()/540.0f)));
        mlaBitmap = getResizedBitmap(mlaBitmap,(int) (152*(birthdayTempleteBitmap.getWidth()/540.0f)),(int) (152*(birthdayTempleteBitmap.getWidth()/540.0f)));

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(birthdayTempleteBitmap,0,0,null);
        Paint paint = new Paint();
        paint.setColor(getActivity().getResources().getColor(R.color.birthDayTextColor));
        paint.setTextSize(result.getHeight()*0.07f);

        paint.setTextAlign(Paint.Align.LEFT);
        Rect rect = new Rect();

        Log.e(TAG,"userBitmap w : "+userBitmap.getWidth()+" h : "+userBitmap.getHeight());
        Log.e(TAG,"mlaBitmap w : "+mlaBitmap.getWidth()+" h : "+mlaBitmap.getHeight());

         Log.e(TAG,"result w : "+result.getWidth()+" h : "+result.getHeight());

         paint.getTextBounds(userName,0,userName.length(),rect);
         canvas.drawText(userName,result.getWidth()*0.050f,result.getHeight()*0.52f,paint);


        Paint paint2 = new Paint();
        paint2.setColor(getActivity().getResources().getColor(R.color.mlaTextColor));

        paint2.setTextAlign(Paint.Align.RIGHT);

        Rect rect2 = new Rect();

         paint2.setTextSize(result.getHeight()*0.05f);
         paint2.getTextBounds(mlaName,0,mlaName.length(),rect2);
         canvas.drawText(mlaName,result.getWidth()*0.94f,result.getHeight()*0.95f,paint2);

        canvas.drawBitmap(getRoundedCornerBitmap(mlaBitmap,mlaBitmap.getWidth()/2), (float) (birthdayTempleteBitmap.getWidth()-mlaBitmap.getWidth()*1.195), (float) (birthdayTempleteBitmap.getHeight()-mlaBitmap.getHeight()*1.380), null);
        canvas.drawBitmap(getRoundedCornerBitmap(userBitmap,userBitmap.getWidth()/2),userBitmap.getWidth()*0.107f , userBitmap.getWidth()*0.135f, null);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.JPEG, 60, bytes);

        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(bytes.toByteArray());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"IOException"+e.getMessage());
        }

        return file;

    }


    //OLD POST BIRTHDAY
    /*private File createBitmap(Bitmap birthdayTempleteBitmap, Bitmap mlaBitmap, Bitmap userBitmap , String userName, File file,String mlaName) {

        Bitmap result = Bitmap.createBitmap(birthdayTempleteBitmap.getWidth(), birthdayTempleteBitmap.getHeight(), birthdayTempleteBitmap.getConfig());

        userBitmap = getResizedBitmap(userBitmap, (int) (195*(birthdayTempleteBitmap.getWidth()/540.0f)),(int) (195*(birthdayTempleteBitmap.getWidth()/540.0f)));
        mlaBitmap = getResizedBitmap(mlaBitmap,(int) (152*(birthdayTempleteBitmap.getWidth()/540.0f)),(int) (152*(birthdayTempleteBitmap.getWidth()/540.0f)));

        String[] splitUserName = userName.split("\\s+");

        String[] splitmlaName = mlaName.split("\\s+");

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(birthdayTempleteBitmap,0,0,null);
        Paint paint = new Paint();
        paint.setColor(getActivity().getResources().getColor(R.color.birthDayTextColor));
        paint.setTextSize(result.getHeight()*0.07f);

        paint.setTextAlign(Paint.Align.CENTER);
        Rect rect = new Rect();
        Rect rect1 = new Rect();

        if (splitUserName.length > 1)
        {
            paint.getTextBounds(splitUserName[0],0,splitUserName[0].length(),rect);
            paint.getTextBounds(splitUserName[1],0,splitUserName[1].length(),rect1);

            canvas.drawText(splitUserName[0],(result.getWidth()*0.74f),result.getHeight()*0.44f,paint);
            canvas.drawText(splitUserName[1],(result.getWidth()*0.74f),result.getHeight()*0.46f+rect1.height(),paint);
        }
        else
        {
            paint.getTextBounds(userName,0,userName.length(),rect);
            canvas.drawText(userName,result.getWidth()*0.8f,result.getHeight()*0.44f,paint);
        }


        Paint paint2 = new Paint();
        paint2.setColor(getActivity().getResources().getColor(R.color.mlaTextColor));

        paint2.setTextAlign(Paint.Align.CENTER);

        Rect rect2 = new Rect();
        Rect rect3 = new Rect();

        if (splitmlaName.length > 1)
        {
            paint2.setTextSize(result.getHeight()*0.03f);
            paint2.getTextBounds(splitmlaName[0],0,splitmlaName[0].length(),rect2);
            paint2.getTextBounds(splitmlaName[1],0,splitmlaName[1].length(),rect3);

            canvas.drawText(splitmlaName[0],result.getWidth()*0.81f,result.getHeight()*0.93f,paint2);
            canvas.drawText(splitmlaName[1],result.getWidth()*0.81f,result.getHeight()*0.95f,paint2);
        }
        else
        {
            paint2.setTextSize(result.getHeight()*0.05f);
            paint2.getTextBounds(mlaName,0,mlaName.length(),rect2);
            canvas.drawText(mlaName,result.getWidth()*0.81f,result.getHeight()*0.95f,paint2);
        }

        canvas.drawBitmap(getRoundedCornerBitmap(mlaBitmap,mlaBitmap.getWidth()/2), (float) (birthdayTempleteBitmap.getWidth()-mlaBitmap.getWidth()*1.195), (float) (birthdayTempleteBitmap.getHeight()-mlaBitmap.getHeight()*1.380), null);
        canvas.drawBitmap(getRoundedCornerBitmap(userBitmap,userBitmap.getWidth()/2),userBitmap.getWidth()*0.199f , userBitmap.getWidth()*0.342f, null);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.JPEG, 60, bytes);

        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(bytes.toByteArray());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"IOException"+e.getMessage());
        }

        return file;

    }*/

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;

    }

    public Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}
