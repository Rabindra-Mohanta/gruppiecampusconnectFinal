
package school.campusconnect.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import school.campusconnect.activities.ChatActivity;
import school.campusconnect.activities.PersonalSettingsActivity;
import school.campusconnect.utils.AmazoneRemove;
import school.campusconnect.utils.AppLog;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import school.campusconnect.R;
import school.campusconnect.activities.TestActivity;
import school.campusconnect.activities.AddFriendActivity;
import school.campusconnect.activities.AddPostActivity;
import school.campusconnect.activities.BaseActivity;
import school.campusconnect.activities.FullScreenActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.SelectShareTypeActivity;
import school.campusconnect.activities.ViewPDFActivity;
import school.campusconnect.activities.PushActivity;
import school.campusconnect.adapters.PersonalChatContactsAdapter;
import school.campusconnect.adapters.PersonalPostAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.PostDataItem;
import school.campusconnect.datamodel.PostItem;
import school.campusconnect.datamodel.PostResponse;
import school.campusconnect.datamodel.personalchat.PersonalPostItem;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.views.SMBDialogUtils;

public class PersonalPostsFragmentNew extends BaseFragment implements LeafManager.OnCommunicationListener, PersonalPostAdapter.OnItemClickListener,
        DialogInterface.OnClickListener, LeafManager.OnAddUpdateListener<AddPostValidationError>, PersonalChatContactsAdapter.OnChatSelected, View.OnClickListener {

    private static final String TAG = "PersonalPostsFragmentNew";
    PullRefreshLayout swipeRefreshLayout2;

    RecyclerView recyclerView2;

    ProgressBar progressBar2;

    private View view;

    private PersonalPostAdapter mAdapter2;

    LeafManager manager = new LeafManager();

    String mGroupId = "";

    static PersonalPostsFragmentNew fragment_new;


    ArrayList<PostItem> PostList = new ArrayList<PostItem>();


    private LinearLayoutManager layoutManager;

    boolean isScroll2 = false;

    Dialog dialog;

    public boolean mIsLoading2 = false;
    public int totalPages2 = 1;
    public int currentPage2 = 1;
    public String selectedFriend = "";
    int count;
    public PostItem currentItem;
    private DatabaseHandler databaseHandler;
    private PersonalPostItem personalData;
    private boolean liked;
    private int position;


    public PersonalPostsFragmentNew() {

    }

    public static PersonalPostsFragmentNew newInstance(String groupId, PersonalPostItem personalPostItem) {
        PersonalPostsFragmentNew fragment = new PersonalPostsFragmentNew();
        AppLog.e(TAG, "personal Item " + personalPostItem);
        Bundle b = new Bundle();
        b.putString("id", groupId);
        b.putString("personal_data", new Gson().toJson(personalPostItem));
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if("teacher".equals(((ChatActivity)getActivity()).role) || "admin".equals(((ChatActivity)getActivity()).role)){
            if(personalData.allowToPost)
                menu.findItem(R.id.menu_add_post).setVisible(true);

            if(personalData.provideSettings)
                menu.findItem(R.id.action_settings).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_add_post:
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), AddPostActivity.class);
                    intent.putExtra("id", mGroupId);
                    intent.putExtra("type", "personal");
                    intent.putExtra("friend_id", selectedFriend);
                    intent.putExtra("friend_name", personalData.name);
                    intent.putExtra("from_chat", true);
                    startActivity(intent);
                }
                break;
            case R.id.menu_add_friend:
                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                intent.putExtra("id", mGroupId);
                intent.putExtra("invite", true);
                startActivity(intent);
                break;
            case R.id.action_settings:
                Intent intent1=new Intent(getActivity(),PersonalSettingsActivity.class);
                intent1.putExtra("group_id",mGroupId);
                intent1.putExtra("user_id",selectedFriend);
                startActivity(intent1);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_personal_post, container, false);

        initObject();

        findViews(view);

        setListeners();

        getGroupPostLocaly();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter2.notifyDataSetChanged();
    }

    private void getGroupPostLocaly() {
        List<PostDataItem> dataItemList = PostDataItem.getPersonalChatPosts(mGroupId + "", selectedFriend + "");
        AppLog.e(TAG, "posts are " + mGroupId + " frnd id " + selectedFriend);
        AppLog.e(TAG,"ListSize "+ String.valueOf(dataItemList.size()));

        if (dataItemList.size() != 0) {
            showLoadingBar(progressBar2);
            for (int i = 0; i < dataItemList.size(); i++) {

                PostItem postItem = new PostItem();
                postItem.id = dataItemList.get(i).id;
                postItem.createdById = dataItemList.get(i).createdById;
                postItem.createdBy = dataItemList.get(i).createdBy;
                postItem.createdByImage = dataItemList.get(i).createdByImage;
                postItem.createdAt = dataItemList.get(i).createdAt;
                postItem.title = dataItemList.get(i).title;
                postItem.fileType = dataItemList.get(i).fileType;
                postItem.fileName = new Gson().fromJson(dataItemList.get(i).fileName, new TypeToken<ArrayList<String>>() {
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
            mAdapter2.notifyDataSetChanged();

            if (isConnectionAvailable()) {
                AppLog.e(TAG, "DataFromAPI BG");
                getData(selectedFriend, true);
            }

        } else {
            if (isConnectionAvailable()) {
                getData(selectedFriend, false);
                AppLog.e(TAG, "DataFromAPI");
            }
        }
    }

    private void setListeners() {
        recyclerView2.addOnScrollListener(new RecyclerView.OnScrollListener() {

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

                if (!mIsLoading2 && totalPages2 > currentPage2) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            ) {
                        currentPage2 = currentPage2 + 1;
                        isScroll2 = true;
                        getData(selectedFriend, false);
                    }
                }
            }
        });

        swipeRefreshLayout2.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    currentPage2 = 1;
                    isScroll2 = false;
                    getData(selectedFriend, false);
                    swipeRefreshLayout2.setRefreshing(false);
                } else {
                    showNoNetworkMsg();
                    swipeRefreshLayout2.setRefreshing(false);
                }
            }
        });
    }

    private void initObject() {
        fragment_new = this;
        isScroll2 = false;
        mIsLoading2 = false;

        mGroupId = getArguments().getString("id");
        personalData = new Gson().fromJson(getArguments().getString("personal_data"),PersonalPostItem.class);
        selectedFriend = personalData.userId;

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        databaseHandler = new DatabaseHandler(getActivity());
        count = databaseHandler.getCount();

    }

    private void findViews(View view) {
        recyclerView2 = (RecyclerView) view.findViewById(R.id.recyclerView2);

        swipeRefreshLayout2 = (PullRefreshLayout) view.findViewById(R.id.swipeRefreshLayout2);

        progressBar2 = (ProgressBar) view.findViewById(R.id.progressBar2);

        recyclerView2.setLayoutManager(layoutManager);

        mAdapter2 = new PersonalPostAdapter(PostList, this, "personal",databaseHandler, count,selectedFriend,personalData.allowPostComment);

        recyclerView2.setAdapter(mAdapter2);
    }

    private void getData(String friend_id, boolean isBackground) {
        if (isBackground) {
            showLoadingBar(progressBar2);
            mIsLoading2 = true;
        }
        manager.getPersonalChat(this, mGroupId + "", friend_id + "", currentPage2);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
        if (getActivity() != null)
            ((BaseActivity) getActivity()).hideLoadingDialog();

        switch (apiId) {
            case LeafManager.API_PERSONAL_CHAT:
                PostResponse res = (PostResponse) response;
                AppLog.e(TAG, "Post Res ; " + new Gson().toJson(res.getResults()));

                if (currentPage2 == 1) {
                    PostDataItem.deletePersonalChatPosts(mGroupId + "", selectedFriend);
                    PostList.clear();

                    PostList.addAll(res.getResults());
                    AppLog.e(TAG, "current page 1");

                } else {
                    PostList.addAll(res.getResults());
                    AppLog.e(TAG, "current page " + currentPage2);
                }
                mAdapter2.notifyDataSetChanged();

                totalPages2 = res.totalNumberOfPages;
                mIsLoading2 = false;

                savePostData(res.getResults());

                break;

            case LeafManager.API_ID_DELETE_PERSONAL_POST:
                Toast.makeText(getContext(), "Post Deleted Successfully", Toast.LENGTH_SHORT).show();
                reloadData();
                AmazoneRemove.remove(currentItem.fileName);
                break;


            case LeafManager.API_ID_LIKE_PERSONAL:
                if(response.status.equalsIgnoreCase("liked"))
                {
                    mAdapter2.getList().get(position).isLiked=true;
                    mAdapter2.getList().get(position).likes++;
                }
                else
                {
                    mAdapter2.getList().get(position).isLiked=false;
                    mAdapter2.getList().get(position).likes--;
                }
                mAdapter2.notifyItemChanged(position);
                liked = false;
                break;
        }
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
            postItem.thumbnail = item.thumbnail;

            postItem.group_id = mGroupId + "";
            postItem.type = "personal";
            postItem.friend_id = selectedFriend;
            postItem.you = item.you;

            postItem.save();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        hide_keyboard();
        if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISPERSONALPOSTUPDATED)) {
            reloadData();
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISPERSONALPOSTUPDATED, false);
        }
    }

    private void reloadData() {
        PostList.clear();
        currentPage2 = 1;
        isScroll2 = false;
        getData(selectedFriend, false);
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<AddPostValidationError> error) {
        hideLoadingBar();
        mIsLoading2 = false;
        liked = false;

        currentPage2 = currentPage2 - 1;
        if (currentPage2 < 0) {
            currentPage2 = 1;
        }
        try {
            AppLog.e("PersonalPostFragmentNew", "onFailure  ,, msg : " + error);
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
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        mIsLoading2 = false;
        liked = false;
        currentPage2 = currentPage2 - 1;
        if (currentPage2 < 0) {
            currentPage2 = 1;
        }
        try {
            if (msg.contains("401:Unauthorized") || msg.contains("401")) {
                Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
                logout();
            } else {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
        }

    }


    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        mIsLoading2 = false;
        liked = false;
        currentPage2 = currentPage2 - 1;
        if (currentPage2 < 0) {
            currentPage2 = 1;
        }
    }

    @Override
    public void onFavClick(PostItem item, int pos) {
    }

    @Override
    public void onLikeClick(PostItem item, int position) {
        if (!liked) {
            liked = true;
            this.position = position;
            showLoadingBar(progressBar2);
            manager.setPersonalLike(this, mGroupId+"", item.id);
        }
    }

    @Override
    public void onPostClick1(PostItem item) {
        if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {

            Intent browserIntent = new Intent(getActivity(), TestActivity.class);
            browserIntent.putExtra("url", item.video);
            startActivity(browserIntent);

        } else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
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
    public void onReadMoreClick(PostItem item) {

    }

    @Override
    public void onEditClick(PostItem item) {

    }

    @Override
    public void onDeleteClick(PostItem item) {
        currentItem = item;
        SMBDialogUtils.showSMBDialogOKCancel(getActivity(), "Are You Sure Want To Delete ?", this);
    }

    @Override
    public void onReportClick(PostItem item) {

    }

    @Override
    public void onShareClick(PostItem item) {

        GroupDashboardActivityNew.is_share_edit = true;
        GroupDashboardActivityNew.share_type = "personal";
        GroupDashboardActivityNew.share_title = item.title;
        GroupDashboardActivityNew.share_desc = item.text;

        GroupDashboardActivityNew.share_image = item.image;
        GroupDashboardActivityNew.imageHeight = item.imageHeight;
        GroupDashboardActivityNew.imageWidth = item.imageWidth;

        if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
            GroupDashboardActivityNew.share_image_type = 1;
        } else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
            GroupDashboardActivityNew.share_image_type = 2;
        } else if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
            GroupDashboardActivityNew.share_image_type = 3;
        } else {
            GroupDashboardActivityNew.share_image_type = 4;
        }

        Intent intent = new Intent(getActivity(), SelectShareTypeActivity.class);
        intent.putExtra("id", mGroupId);
        intent.putExtra("post_id", item.id);
        AppLog.e("SHARE", "Post id2 is " + item.id + GroupDashboardActivityNew.share_desc);
        startActivity(intent);
        AppLog.e("SHARE", "Post id1 is " + item.id);
    }

    @Override
    public void onPushClick(PostItem item) {
        GroupDashboardActivityNew.is_share_edit = true;
        GroupDashboardActivityNew.share_type = "personal";
        GroupDashboardActivityNew.share_title = item.title;
        GroupDashboardActivityNew.share_desc = item.text;

        GroupDashboardActivityNew.share_image = item.image;
        GroupDashboardActivityNew.imageHeight = item.imageHeight;
        GroupDashboardActivityNew.imageWidth = item.imageWidth;
        if(TextUtils.isEmpty(item.fileType)){
            GroupDashboardActivityNew.share_image_type = 4;
        }
        else if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
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
    public void onClick(DialogInterface dialogInterface, int i) {
        AppLog.e("PersonalPostFrag", "DIalog Ok Clicked ");
        showLoadingBar(progressBar2);
        LeafManager manager = new LeafManager();
        manager.deletePersonalPostChat(this, mGroupId + "", selectedFriend + "", currentItem.id);
    }

    @Override
    public void onNameClick(PersonalPostItem item) {

    }


    public static PersonalPostsFragmentNew getInstance() {
        return fragment_new;
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

}
