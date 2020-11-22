package school.campusconnect.fragments;

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

import com.activeandroid.ActiveAndroid;
import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import school.campusconnect.R;
import school.campusconnect.activities.CommentsActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.NotificationsActivityNew;
import school.campusconnect.activities.ReadMoreActivity;
import school.campusconnect.activities.ReplyActivity;
import school.campusconnect.adapters.NotificationListAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.databinding.LayoutListButtonBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.GroupDataItem;
import school.campusconnect.datamodel.GroupDetailResponse;
import school.campusconnect.datamodel.GroupItem;
import school.campusconnect.datamodel.PostDataItem;
import school.campusconnect.datamodel.PostItem;
import school.campusconnect.datamodel.PostTeamDataItem;
import school.campusconnect.datamodel.ReadGroupPostData;
import school.campusconnect.datamodel.ReadGroupPostResponse;
import school.campusconnect.datamodel.ReadIndividualPostResponse;
import school.campusconnect.datamodel.ReadTeamPostResponse;
import school.campusconnect.datamodel.notifications.NotificationData;
import school.campusconnect.datamodel.notifications.NotificationItem;
import school.campusconnect.datamodel.notifications.NotificationModel;
import school.campusconnect.datamodel.notifications.NotificationResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;

/**
 * Created by frenzin04 on 2/28/2017.
 */

public class NotificationsFragment extends BaseFragment implements LeafManager.OnCommunicationListener, NotificationListAdapter.OnNotificationClick {

    private LayoutListButtonBinding mBinding;

    private NotificationListAdapter mAdapter;

    LeafManager manager = new LeafManager();

    int position = -1;
    int likecount = 0;
    String selectedGroupId;
    public int totalPages = 1;
    public int currentPage = 1;
    int notiType = -1;
    int count;
    int notiTotalCount;

    public boolean mIsLoading = false;
    public boolean callApi = false;

    boolean isScroll = false;
    boolean isRefresh = false;

    ArrayList<NotificationItem> notificationItems = new ArrayList<>();

    ReadGroupPostData readGroupPostData;

    GroupItem item;

    public static final int Noti_Type_Individual_Post_Add = 1;
    public static final int Noti_Type_Group_Post_Add = 8;
    public static final int Noti_Type_Group_Post_Edit = 9;
    public static final int Noti_Type_Group_Comment = 10;
    public static final int Noti_Type_Group_Comment_Reply = 11;
    public static final int Noti_Type_Team_Post_Add = 12;
    public static final int Noti_Type_Team_Post_Edit = 13;
    public static final int Noti_Type_Team_Comment = 14;
    public static final int Noti_Type_Team_Comment_reply = 15;

    public NotificationsFragment() {

    }

    public static NotificationsFragment newInstance(int type, boolean callApi) {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle b = new Bundle();
        b.putInt("type", type);
        b.putBoolean("callApi", callApi);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActiveAndroid.initialize(getActivity());
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_list_button, container, false);
        mBinding.setSize(1);
        mBinding.setMessage(R.string.msg_no_noti);
        notiType = getArguments().getInt("type", 0);
        callApi = getArguments().getBoolean("callApi");

        String count_tag = "";

        final LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView.setLayoutManager(manager);

        List<NotificationModel> dataItemList = NotificationModel.getAll(notiType);

        DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
        count = databaseHandler.getCount();

        notiTotalCount = 0;

        if (!callApi) {
            if (dataItemList.size() != 0) {
                mBinding.progressBar.setVisibility(View.GONE);
                for (int i = 0; i < dataItemList.size(); i++) {

                    if (i < 15)
                        if (!dataItemList.get(i).seen) {
                            notiTotalCount++;
                        }

                    NotificationItem notificationItem = new NotificationItem(dataItemList.get(i).noti_title, dataItemList.get(i).noti_icon, dataItemList.get(i).group_id,
                            dataItemList.get(i).team_id, dataItemList.get(i).post_id, dataItemList.get(i).comment_id, dataItemList.get(i).post_type,
                            dataItemList.get(i).date_time, dataItemList.get(i).friend_id, dataItemList.get(i).created_by, dataItemList.get(i).phone,
                            dataItemList.get(i).seen, dataItemList.get(i).noti_id);
                    notificationItems.add(notificationItem);
                }

                mAdapter = new NotificationListAdapter(getActivity(), notificationItems, count);
                mAdapter.setOnNotificationClick(this);
                mBinding.recyclerView.setAdapter(mAdapter);

                showBadge();

            } else {
                mAdapter = new NotificationListAdapter(getActivity(), notificationItems, count);
                getData();
            }
        } else {
            mAdapter = new NotificationListAdapter(getActivity(), notificationItems, count);
            getData();
        }


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

                if (!mIsLoading && totalPages > currentPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            ) {
                        currentPage = currentPage + 1;
                       AppLog.e("GeneralPostScroll", "onScrollCalled " + currentPage);
                        isScroll = true;
                        getData();
                    }
                } else {
                   AppLog.e("GeneralPostScroll", "else onScrollCalled " + currentPage);
                }
            }
        });

        mBinding.swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    isRefresh = true;
                    currentPage = 1;
                    isScroll = false;
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

    private void getData() {
        // mAdapter.clear();
        showLoadingBar(mBinding.progressBar);
        mIsLoading = true;
        manager.getNotifications(this, currentPage, notiType);
    }

    private void showBadge() {
        try {
            if (notiTotalCount >= 0)
                switch (notiType) {
                    case 1:
                        ((NotificationsActivityNew) getActivity()).showGroupCounter(String.valueOf(notiTotalCount));
                        break;
                    case 2:
                        ((NotificationsActivityNew) getActivity()).showTeamCounter(String.valueOf(notiTotalCount));
                        break;
                    case 3:
                        ((NotificationsActivityNew) getActivity()).showPersonalCounter(String.valueOf(notiTotalCount));
                        break;
                }
        } catch (NullPointerException e) {
           AppLog.e("showBadge", "error is " + e.toString());
        }
    }

    private void nullBadge() {

        switch (notiType) {
            case 1:
                ((NotificationsActivityNew) getActivity()).nullGroupBadge();
                break;
            case 2:
                ((NotificationsActivityNew) getActivity()).nullTeamBadge();
                break;
            case 3:
                ((NotificationsActivityNew) getActivity()).nullPerBadge();
                break;
        }

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
//       AppLog.e("ReadGroupPostResponse", "response is " + ((ReadGroupPostResponse) response).data);
        hideLoadingBar();

        if (apiId == LeafManager.API_NOTI_LIST) {
//            nullBadge();
            NotificationResponse notificationResponse = (NotificationResponse) response;

            if (currentPage == 1) {
                notiTotalCount = 0;
                notificationItems.clear();
            }

            if (!isScroll)
                NotificationModel.deleteAll(notiType);

            for (int i = 0; i < notificationResponse.data.size(); i++) {

                NotificationData item = notificationResponse.data.get(i);

                if (currentPage == 1) {
                    if (!item.notificationSeen) {
                        notiTotalCount++;
                    }
                }

                NotificationModel notificationModel = new NotificationModel();
                notificationModel.noti_title = item.body;
                notificationModel.noti_icon = item.image;
                notificationModel.date_time = item.postedAt;
                notificationModel.group_id = item.groupId;
                notificationModel.team_id = item.teamId;
                notificationModel.post_id = item.postId;
                notificationModel.comment_id = item.commentId;
                notificationModel.post_type = item.type;
                notificationModel.friend_id = item.friendId;
                notificationModel.created_by = item.createdBy;
                notificationModel.phone = item.phone;
                notificationModel.noti_type = notiType;
                notificationModel.noti_id = item.id;
                notificationModel.seen = item.notificationSeen;

                NotificationItem notificationItem = new NotificationItem(item.body, item.image, item.groupId,
                        item.teamId, item.postId, item.commentId, item.type, item.postedAt, item.friendId,
                        item.createdBy, item.phone, item.notificationSeen, item.id);
                notificationItems.add(notificationItem);
                notificationModel.save();
            }

            totalPages = notificationResponse.totalPages;
           AppLog.e("NNOTI", "total pages are " + totalPages);
            mIsLoading = false;

            if (currentPage == 1 && getActivity()!=null) {
                if (!isRefresh)
                    showBadge();
                else
                    isRefresh = false;
                mAdapter = new NotificationListAdapter(getActivity(), notificationItems, count);
                mAdapter.setOnNotificationClick(this);
                mBinding.recyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }

            mBinding.setSize(notificationItems.size());

        } else if (apiId == LeafManager.API_READ_GROUP_REQUEST) {
            ReadGroupPostResponse readGroupPostResponse = (ReadGroupPostResponse) response;
            readGroupPostData = readGroupPostResponse.data = ((ReadGroupPostResponse) response).data;

           AppLog.e("ReadGroupPostResponse", "id is " + readGroupPostData.id);
           AppLog.e("ReadGroupPostResponse", "createdBy is " + readGroupPostData.createdBy);
           AppLog.e("ReadGroupPostResponse", "profile is " + readGroupPostData.profile);
           AppLog.e("ReadGroupPostResponse", "postedAt is " + readGroupPostData.postedAt);
           AppLog.e("ReadGroupPostResponse", "title is " + readGroupPostData.title);
           AppLog.e("ReadGroupPostResponse", "text is " + readGroupPostData.text);
           AppLog.e("ReadGroupPostResponse", "shortText is " + readGroupPostData.shortText);
           AppLog.e("ReadGroupPostResponse", "pdf is " + readGroupPostData.pdf);
           AppLog.e("ReadGroupPostResponse", "image is " + readGroupPostData.image);
           AppLog.e("ReadGroupPostResponse", "video is " + readGroupPostData.video);
           AppLog.e("ReadGroupPostResponse", "likes is " + readGroupPostData.likes);
           AppLog.e("ReadGroupPostResponse", "isLiked is " + readGroupPostData.isLiked);
           AppLog.e("ReadGroupPostResponse", "isFavourited is " + readGroupPostData.isFavourited);
           AppLog.e("ReadGroupPostResponse", "isEdit is " + readGroupPostData.isEdit);

           /* PostItem item = new PostItem();
            item.id = readGroupPostData.id+"";
            item.createdBy = readGroupPostData.createdBy;
            item.createdByImage = readGroupPostData.profile;
            item.createdAt = readGroupPostData.postedAt;
            item.title = readGroupPostData.title;
            item.text = readGroupPostData.shortText;
            item.image = readGroupPostData.image;
            item.video = readGroupPostData.video;
            item.text = readGroupPostData.text;
            item.likes = readGroupPostData.likes;
            item.isLiked = readGroupPostData.isLiked;
            item.isFavourited = readGroupPostData.isFavourited;
            item.canEdit = readGroupPostData.isEdit;
            item.comments=readGroupPostData.comments;
*/

            try {
                Intent intent = new Intent(getActivity(), ReadMoreActivity.class);
                Gson gson = new Gson();
                String data = gson.toJson(item);
                intent.putExtra("data", data);
                intent.putExtra("type", "group");
                intent.putExtra("from", "noti");
                intent.putExtra("id",selectedGroupId);
                startActivity(intent);

                PostDataItem postItem = new PostDataItem();

                if (postItem.isPostAvailable(item.id)) {
                    PostDataItem.deletePost(item.id);
                }

              /*  postItem.id = readGroupPostData.id+"";
                postItem.createdBy = readGroupPostData.createdBy;
                postItem.createdByImage = readGroupPostData.profile;
                postItem.createdAt = readGroupPostData.postedAt;
                postItem.title = readGroupPostData.title;
                postItem.fileType = readGroupPostData.fileType;
                postItem.updatedAt = readGroupPostData.updatedAt;
                postItem.text = readGroupPostData.shortText;
                postItem.imageWidth = readGroupPostData.imageWidth;
                postItem.imageHeight = readGroupPostData.imageHeight;
                postItem.video = readGroupPostData.video;
                postItem.text = readGroupPostData.text;
                postItem.likes = readGroupPostData.likes;
                postItem.isLiked = readGroupPostData.isLiked;
                postItem.isFavourited = readGroupPostData.isFavourited;
                postItem.canEdit = readGroupPostData.isEdit;
                postItem.type = "group";
                postItem.group_id = selectedGroupId+"";
                postItem.save();*/
            }catch (Exception e){}


        } else if (apiId == LeafManager.API_READ_INDIVIDUAL_REQUEST) {

            ReadIndividualPostResponse readIndividualPostResponse = (ReadIndividualPostResponse) response;
           AppLog.e("IndividualPostResponse", "id is " + readIndividualPostResponse.id);
           AppLog.e("IndividualPostResponse", "createdBy is " + readIndividualPostResponse.profile);
           AppLog.e("IndividualPostResponse", "profile is " + readIndividualPostResponse.createdBy);
           AppLog.e("IndividualPostResponse", "postedAt is " + readIndividualPostResponse.postedAt);
           AppLog.e("IndividualPostResponse", "title is " + readIndividualPostResponse.pdf);
           AppLog.e("IndividualPostResponse", "text is " + readIndividualPostResponse.image);
           AppLog.e("IndividualPostResponse", "shortText is " + readIndividualPostResponse.video);
           AppLog.e("IndividualPostResponse", "pdf is " + readIndividualPostResponse.shortText);
           AppLog.e("IndividualPostResponse", "image is " + readIndividualPostResponse.text);

            PostDataItem postItem = new PostDataItem();

            if (postItem.isPostAvailable(readIndividualPostResponse.id+"")) {
                PostDataItem.deletePost(readIndividualPostResponse.id+"");
            }

            postItem.id = readIndividualPostResponse.id+"";
            postItem.createdBy = readIndividualPostResponse.createdBy;
            postItem.createdByImage = readIndividualPostResponse.profile;
            postItem.createdAt = readIndividualPostResponse.postedAt;
//            postItem.title = readIndividualPostResponse.title;
            postItem.text = readIndividualPostResponse.shortText;
            postItem.imageWidth = readIndividualPostResponse.imageWidth;
            postItem.imageHeight = readIndividualPostResponse.imageHeight;
            postItem.imageWidth = readIndividualPostResponse.imageWidth;
            postItem.imageHeight = readIndividualPostResponse.imageHeight;
            postItem.video = readIndividualPostResponse.video;
            postItem.text = readIndividualPostResponse.text;
//            postItem.likes = readIndividualPostResponse.likes;
//            postItem.isLiked = readIndividualPostResponse.isLiked;
//            postItem.isFavourited = readIndividualPostResponse.isFavourited;
//            postItem.isEdit = readIndividualPostResponse.isEdit;
            postItem.type = "group";
            postItem.group_id = selectedGroupId+"";
            postItem.save();

            PostItem item = new PostItem();
            item.id = readIndividualPostResponse.id+"";
            item.createdBy = readIndividualPostResponse.createdBy;
            item.createdByImage = readIndividualPostResponse.profile;
            item.createdAt = readIndividualPostResponse.postedAt;
//            item.title = readIndividualPostResponse.title;
            item.text = readIndividualPostResponse.shortText;
            item.image = readIndividualPostResponse.image;
            item.video = readIndividualPostResponse.video;
            item.text = readIndividualPostResponse.text;
            item.comments=readIndividualPostResponse.comments;
//            item.likes = readIndividualPostResponse.likes;
//            item.isLiked = readIndividualPostResponse.isLiked;
//            item.isFavourited = readIndividualPostResponse.isFavourited;
//            item.isEdit = readIndividualPostResponse.isEdit;

            try {
                Intent intent = new Intent(getActivity(), ReadMoreActivity.class);
                Gson gson = new Gson();
                String data = gson.toJson(item);
                intent.putExtra("data", data);
                intent.putExtra("type", "personal");
                intent.putExtra("from", "noti");
                intent.putExtra("id",selectedGroupId);
            }catch (Exception e){}

        } else if (apiId == LeafManager.API_READ_TEAM_REQUEST) {
            PostItem item = new PostItem();
            ReadTeamPostResponse res = ((ReadTeamPostResponse) response);
            item.id = res.id+"";// = ((ReadTeamPostResponse) response).id;
            item.title = res.title;// = ((ReadTeamPostResponse) response).title;
            item.createdByImage = res.profile;// = ((ReadTeamPostResponse) response).profile;
            item.createdBy = res.createdBy;// = ((ReadTeamPostResponse) response).createdBy;
            item.createdAt = res.postedAt;// = ((ReadTeamPostResponse) response).postedAt;
            item.image = res.image;// = ((ReadTeamPostResponse) response).image;
            item.video = res.video;// = ((ReadTeamPostResponse) response).video;
            item.text = res.text;// = ((ReadTeamPostResponse) response).shortText;
            item.text = res.text;// = ((ReadTeamPostResponse) response).text;
            item.canEdit = res.isEdit;// = ((ReadTeamPostResponse) response).isEdit;
            item.readMore = res.readMore;// = ((ReadTeamPostResponse) response).readMore;
            item.comments=res.comments;
           AppLog.e("ReadTeamPostResponse", "id is " + res.id);
           AppLog.e("ReadTeamPostResponse", "title is " + res.title);
           AppLog.e("ReadTeamPostResponse", "profile is " + res.profile);
           AppLog.e("ReadTeamPostResponse", "createdBy is " + res.createdBy);
           AppLog.e("ReadTeamPostResponse", "postedAt is " + res.postedAt);
           AppLog.e("ReadTeamPostResponse", "pdf is " + res.pdf);
           AppLog.e("ReadTeamPostResponse", "image is " + res.image);
           AppLog.e("ReadTeamPostResponse", "video is " + res.video);
           AppLog.e("ReadTeamPostResponse", "shortText is " + res.shortText);
           AppLog.e("ReadTeamPostResponse", "text is " + res.text);
           AppLog.e("ReadTeamPostResponse", "isEdit is " + res.isEdit);
           AppLog.e("ReadTeamPostResponse", "readMore is " + res.readMore);

            if (PostTeamDataItem.isPostAvailable(res.id)) {
                PostTeamDataItem.deletePost(res.id);
            }

           /* PostTeamDataItem postDataItem = new PostTeamDataItem();
            postDataItem.id = res.id;
            postDataItem.createdBy = res.createdBy;
            postDataItem.createdByImage = res.profile;
            postDataItem.updatedAt = res.postedAt;
            postDataItem.title = res.title;
            postDataItem.text = res.shortText;
            postDataItem.image = res.image;
            postDataItem.imageWidth = res.imageWidth;
            postDataItem.imageHeight = res.imageHeight;
            postDataItem.imageHeight=res.imageHeight;
            postDataItem.imageWidth=res.imageWidth;
            postDataItem.video = res.video;
            postDataItem.text = res.text;
            postDataItem.likes = 0;
            postDataItem.isLiked = false;
            postDataItem.canEdit = res.isEdit;
            postDataItem.group_id = notificationItems.get(position).getGroupId();
            postDataItem.team_id = notificationItems.get(position).getTeamId();
            postDataItem.save();*/


            try {
                Intent intent = new Intent(getActivity(), ReadMoreActivity.class);
                Gson gson = new Gson();
                String data = gson.toJson(item);
                intent.putExtra("data", data);
                if (position != -1)
                intent.putExtra("team_id", notificationItems.get(position).getTeamId());
                intent.putExtra("type", "team");
                intent.putExtra("from", "noti");
                intent.putExtra("id",selectedGroupId);
                startActivity(intent);
            }catch (Exception e){}


        } else if (apiId == LeafManager.API_NOTI_SEEN) {
            if (position != -1) {
                NotificationModel.updateSeen(notificationItems.get(position).getId());
                notificationItems.get(position).setSeen(true);
                NotificationModel notificationModel = new NotificationModel();
                notificationModel.noti_title = notificationItems.get(position).getStrTitle();
                notificationModel.noti_icon = notificationItems.get(position).getStrIcon();
                notificationModel.date_time = notificationItems.get(position).getDateTime();
                notificationModel.group_id = notificationItems.get(position).getGroupId();
                notificationModel.team_id = notificationItems.get(position).getTeamId();
                notificationModel.post_id = notificationItems.get(position).getPostId();
                notificationModel.comment_id = notificationItems.get(position).getCommentId();
                notificationModel.post_type = notificationItems.get(position).getPostType();
                notificationModel.friend_id = notificationItems.get(position).getFriendId();
                notificationModel.created_by = notificationItems.get(position).getStrName();
                notificationModel.phone = notificationItems.get(position).getPhone();
                notificationModel.noti_type = notiType;
                notificationModel.noti_id = notificationItems.get(position).getId();
                notificationModel.seen = true;
                notificationModel.save();
                mAdapter.notifyItemChanged(position);
            }
        } else {

            GroupDetailResponse res = (GroupDetailResponse) response;

            item = res.data.get(0);


//            GroupDataItem groupDataItem = new GroupDataItem();

            if (GroupDataItem.isGroupAvailable(item.id)) {
                GroupDataItem.deleteGroup(item.id);
            }

           AppLog.e("GroupDataItem", "id is " + item.id);
           AppLog.e("GroupDataItem", "adminName is " + item.adminName);
           AppLog.e("GroupDataItem", "name is " + item.name);
           AppLog.e("GroupDataItem", "aboutGroup is " + item.aboutGroup);
           AppLog.e("GroupDataItem", "createdBy is " + item.adminName);
           AppLog.e("GroupDataItem", "shortDescription is " + item.shortDescription);
           AppLog.e("GroupDataItem", "image is " + item.image);
           AppLog.e("GroupDataItem", "isAllowedToPost is " + item.canPost);
           AppLog.e("GroupDataItem", "isAdmin is " + item.isAdmin);

            GroupDataItem groupDataItem = new GroupDataItem();
            groupDataItem.id = item.id;
            groupDataItem.adminName = item.adminName;
            groupDataItem.name = item.name;
            groupDataItem.aboutGroup = item.aboutGroup;
            groupDataItem.createdBy = item.adminName;
            groupDataItem.shortDescription = item.shortDescription;
            groupDataItem.image = item.image;
            groupDataItem.canPost = item.canPost;
            groupDataItem.isAdmin = item.isAdmin;
            groupDataItem.save();

            try {
                Intent intent = new Intent(getActivity(), GroupDashboardActivityNew.class);

                Bundle b = new Bundle();
                b.putParcelable(GroupDashboardActivityNew.EXTRA_GROUP_ITEM, item);
                boolean admin = item.isAdmin;
                boolean isPost = item.canPost;
                intent.putExtra("admin", admin);
                intent.putExtra("post", isPost);
                intent.putExtras(b);
                startActivity(intent);
            } catch (NullPointerException e) {
               AppLog.e("Intent", "error is " + e.toString());
            }
        }


    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();

       AppLog.e("onFailure", "msg is " + msg);

        try {
            if (msg.contains("404")) {
                if (apiId == LeafManager.API_READ_GROUP_REQUEST || apiId == LeafManager.API_READ_INDIVIDUAL_REQUEST || apiId == LeafManager.API_READ_TEAM_REQUEST)
                    Toast.makeText(getActivity(), "This post is deleted.", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), "This group is deleted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){}


    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        try {
            Toast.makeText(getActivity(), getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
           AppLog.e("onException", "error is " + e.toString());
        }

    }

    @Override
    public void onNotiClick(int position) {
        /*1 = Individual Post Add
        2 = Allowed User To Post In Group
        3 = Removed User To Post In Group
        4 = New Group created Successfully
        5 = User Added To The Group
        6 = User Removed From The Group By Lead
        7 = User Removed From The Group By Group Admin
        8 = Group Post Add
        9 = Group Post Edit*/
        Intent intent;

        this.position = position;

        if (isConnectionAvailable()) {

            if (!notificationItems.get(position).isSeen()) {
                notiTotalCount--;

                showBadge();
            }
            LeafManager manager = new LeafManager();

            manager.seeNotifications(this, notificationItems.get(position).getId(), notiType);

            selectedGroupId = notificationItems.get(position).getGroupId();

           AppLog.e("NOTI_", "type is " + notificationItems.get(position).getPostType());

            switch (notificationItems.get(position).getPostType()) {

                default:
                    showLoadingBar(mBinding.progressBar);
                    manager.getGroupDetail(this, notificationItems.get(position).getGroupId()+"");
                    break;

                case Noti_Type_Individual_Post_Add:
                    /*showLoadingBar(mBinding.progressBar);
                    manager.readIndividualRequest(this, notificationItems.get(position).getGroupId(), notificationItems.get(position).getPostId());*/
                    break;

                case Noti_Type_Group_Post_Add:
                    showLoadingBar(mBinding.progressBar);
                    manager.readGroupRequest(this, notificationItems.get(position).getGroupId(), notificationItems.get(position).getPostId());
                    break;

                case Noti_Type_Group_Post_Edit:
                    showLoadingBar(mBinding.progressBar);
                    manager.readGroupRequest(this, notificationItems.get(position).getGroupId(), notificationItems.get(position).getPostId());
                    break;

                case Noti_Type_Group_Comment: // type for group comment
                    intent = new Intent(getActivity(), CommentsActivity.class);
                    intent.putExtra("id", notificationItems.get(position).getGroupId());
                    intent.putExtra("post_id", notificationItems.get(position).getPostId());
                    intent.putExtra("type", "group");
                    startActivity(intent);
                    break;

                case Noti_Type_Group_Comment_Reply: // type for group comment reply
                    intent = new Intent(getActivity(), ReplyActivity.class);
                    intent.putExtra("id", notificationItems.get(position).getGroupId());
                    String team_id = "";
                    if (!notificationItems.get(position).getTeamId().equals("")) {
                        team_id = notificationItems.get(position).getTeamId();
                    }
                    intent.putExtra("team_id", team_id);
                    intent.putExtra("post_id", notificationItems.get(position).getPostId());
                    intent.putExtra("comment_id", notificationItems.get(position).getCommentId());
                    intent.putExtra("user_id", "");
                    intent.putExtra("type", "group");
                    startActivity(intent);
                    break;

                case Noti_Type_Team_Post_Add:

                case Noti_Type_Team_Post_Edit:
                    showLoadingBar(mBinding.progressBar);
                    manager.readPostRequest(this, notificationItems.get(position).getGroupId(), notificationItems.get(position).getTeamId(), notificationItems.get(position).getPostId());
                    break;

                case Noti_Type_Team_Comment: // type for team comment
                    intent = new Intent(getActivity(), CommentsActivity.class);
                    intent.putExtra("id", notificationItems.get(position).getGroupId());
                    intent.putExtra("team_id", notificationItems.get(position).getTeamId());
                    intent.putExtra("post_id", notificationItems.get(position).getPostId());
                    intent.putExtra("type", "team");
                    startActivity(intent);
                    break;

                case Noti_Type_Team_Comment_reply: // type for team comment reply
                    intent = new Intent(getActivity(), ReplyActivity.class);
                    intent.putExtra("id", notificationItems.get(position).getGroupId());
                    intent.putExtra("team_id", notificationItems.get(position).getTeamId());
                    intent.putExtra("post_id", notificationItems.get(position).getPostId());
                    intent.putExtra("comment_id", notificationItems.get(position).getCommentId());
                    intent.putExtra("type", "team");
                    startActivity(intent);
                    break;

            }
        } else {
            if (notificationItems.get(position).getPostType() == Noti_Type_Individual_Post_Add || notificationItems.get(position).getPostType() == Noti_Type_Group_Post_Add || notificationItems.get(position).getPostType() == Noti_Type_Group_Post_Edit) {

                PostDataItem postItem = new PostDataItem();

                if (postItem.isPostAvailable(notificationItems.get(position).getPostId()+"")) {

                    postItem = PostDataItem.getPost(notificationItems.get(position).getPostId());
                    PostItem item = new PostItem();
                    switch (notificationItems.get(position).getPostType()) {

                        case Noti_Type_Individual_Post_Add:
                            item.id = postItem.id;
                            item.createdBy = postItem.createdBy;
                            item.createdByImage = postItem.createdByImage;
                            item.createdAt = postItem.createdAt;
//            item.title = postItem.title;
                            item.text = postItem.text;
                            item.imageWidth = postItem.imageWidth;
                            item.imageHeight = postItem.imageHeight;
                            item.imageHeight = postItem.imageHeight;
                            item.imageWidth = postItem.imageWidth;
                            item.imageHeight=postItem.imageHeight;
                            item.imageWidth=postItem.imageWidth;
                            item.video = postItem.video;
                            item.text = postItem.text;
//            item.likes = readIndividualPostResponse.likes;
//            item.isLiked = readIndividualPostResponse.isLiked;
//            item.isFavourited = readIndividualPostResponse.isFavourited;
//            item.isEdit = readIndividualPostResponse.isEdit;

                            Intent intent2 = new Intent(getActivity(), ReadMoreActivity.class);
                            Gson gson2 = new Gson();
                            String data2 = gson2.toJson(item);
                            intent2.putExtra("data", data2);
                            intent2.putExtra("type", "personal");
                            intent2.putExtra("from", "noti");
                            startActivity(intent2);
                            break;

                        case Noti_Type_Group_Post_Add:
                            item.id = postItem.id;
                            item.createdBy = postItem.createdBy;
                            item.createdByImage = postItem.createdByImage;
                            item.createdAt = postItem.createdAt;
                            item.title = postItem.title;
                            item.updatedAt = postItem.updatedAt;
                            item.text = postItem.text;
                            item.imageWidth = postItem.imageWidth;
                            item.imageHeight = postItem.imageHeight;
                            item.video = postItem.video;
                            item.text = postItem.text;
                            item.likes = postItem.likes;
                            item.isLiked = postItem.isLiked;
                            item.isFavourited = postItem.isFavourited;
                            item.canEdit = postItem.canEdit;

                            Intent intent1 = new Intent(getActivity(), ReadMoreActivity.class);
                            Gson gson1 = new Gson();
                            String data1 = gson1.toJson(item);
                            intent1.putExtra("data", data1);
                            intent1.putExtra("type", "group");
                            intent1.putExtra("from", "noti");
                            startActivity(intent1);
                            break;


                        case Noti_Type_Group_Post_Edit:
                            item.id = postItem.id;
                            item.createdBy = postItem.createdBy;
                            item.createdByImage = postItem.createdByImage;
                            item.createdAt = postItem.createdAt;
                            item.title = postItem.title;
                            item.fileType = postItem.fileType;
                            item.updatedAt = postItem.updatedAt;
                            item.text = postItem.text;
                            item.imageWidth = postItem.imageWidth;
                            item.imageHeight = postItem.imageHeight;
                            item.video = postItem.video;
                            item.text = postItem.text;
                            item.likes = postItem.likes;
                            item.isLiked = postItem.isLiked;
                            item.isFavourited = postItem.isFavourited;
                            item.canEdit = postItem.canEdit;

                            intent = new Intent(getActivity(), ReadMoreActivity.class);
                            Gson gson = new Gson();
                            String data = gson.toJson(item);
                            intent.putExtra("data", data);
                            intent.putExtra("type", "group");
                            intent.putExtra("from", "noti");
                            startActivity(intent);
                            break;

                    }
                } else {
                    showNoNetworkMsg();
                }
            } else if (notificationItems.get(position).getPostType() == Noti_Type_Team_Post_Add || notificationItems.get(position).getPostType() == Noti_Type_Team_Post_Edit) {


                PostTeamDataItem postItem = new PostTeamDataItem();

                if (postItem.isPostAvailable(notificationItems.get(position).getPostId())) {

                    postItem = PostTeamDataItem.getPost(notificationItems.get(position).getPostId());
                    PostItem item = new PostItem();
                    switch (notificationItems.get(position).getPostType()) {

                        case Noti_Type_Team_Post_Add:

                        case Noti_Type_Team_Post_Edit:
                          /*  item.id = postItem.id;
                            item.createdBy = postItem.createdBy;
                            item.createdByImage = postItem.createdByImage;
                            item.createdAt = postItem.updatedAt;
                            item.title = postItem.title;
                            item.text = postItem.text;
                            item.image = postItem.image;
                            item.imageWidth = postItem.imageWidth;
                            item.imageHeight = postItem.imageHeight;
                            item.imageWidth = postItem.imageWidth;
                            item.imageHeight = postItem.imageHeight;
                            item.video = postItem.video;
                            item.text = postItem.text;
                            item.likes = postItem.likes;
                            item.isLiked = postItem.isLiked;
                            item.canEdit = postItem.canEdit;*/

                            intent = new Intent(getActivity(), ReadMoreActivity.class);
                            Gson gson = new Gson();
                            String data = gson.toJson(item);
                            intent.putExtra("data", data);
                            intent.putExtra("type", "team");
                            intent.putExtra("team_id", postItem.team_id);
                            intent.putExtra("from", "noti");
                            startActivity(intent);
                            break;

                    }
                } else {
                    showNoNetworkMsg();
                }

            } else if (notificationItems.get(position).getPostType() == Noti_Type_Group_Comment || notificationItems.get(position).getPostType() == Noti_Type_Group_Comment_Reply
                    || notificationItems.get(position).getPostType() == Noti_Type_Team_Comment || notificationItems.get(position).getPostType() == Noti_Type_Team_Comment_reply) {

                showNoNetworkMsg();

            } else {

                if (GroupDataItem.isGroupAvailable(notificationItems.get(position).getGroupId()+"")) {

                    GroupDataItem groupDataItem = GroupDataItem.getGroup(notificationItems.get(position).getGroupId());

                    GroupItem item = new GroupItem();

                    item.adminName = groupDataItem.adminName;
                    item.id = groupDataItem.id;
                    item.name = groupDataItem.name;
                    item.adminName = groupDataItem.adminName;
                    item.shortDescription = groupDataItem.shortDescription;
                    item.image = groupDataItem.image;
                    item.aboutGroup = groupDataItem.aboutGroup;
                    item.canPost = groupDataItem.canPost;
                    item.isAdmin = groupDataItem.isAdmin;

                   AppLog.e("DATA", "adminName " + item.adminName);
                   AppLog.e("DATA", "id " + item.id);
                   AppLog.e("DATA", "name " + item.name);
                   AppLog.e("DATA", "createdBy " + item.adminName);
                   AppLog.e("DATA", "shortDescription " + item.shortDescription);
                   AppLog.e("DATA", "image " + item.image);
                   AppLog.e("DATA", "aboutGroup " + item.aboutGroup);
                   AppLog.e("DATA", "isAllowedToPost " + item.canPost);
                   AppLog.e("DATA", "isAdmin " + item.isAdmin);

                    intent = new Intent(getActivity(), GroupDashboardActivityNew.class);

                    Bundle b = new Bundle();
                    b.putParcelable(GroupDashboardActivityNew.EXTRA_GROUP_ITEM, item);
                    boolean admin = item.isAdmin;
                    boolean isPost = item.canPost;
                    intent.putExtra("admin", admin);
                    intent.putExtra("post", isPost);
                    intent.putExtras(b);
                    startActivity(intent);
                } else {
                    showNoNetworkMsg();
                }
            }
        }
    }

}
