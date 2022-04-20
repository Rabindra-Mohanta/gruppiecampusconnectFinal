package school.campusconnect.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ViewDataBinding;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import school.campusconnect.BuildConfig;
import school.campusconnect.activities.CommentsActivity;
import school.campusconnect.activities.CommentsLikeActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.ProfileActivity2;
import school.campusconnect.adapters.CommentListAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.LayoutListCommentsWithoutRefreshBinding;

import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import school.campusconnect.datamodel.comments.AddCommentRes;
import school.campusconnect.utils.AppLog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.squareup.picasso.Picasso;

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
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.GroupValidationError;
import school.campusconnect.datamodel.comments.AddGroupCommentRequest;
import school.campusconnect.datamodel.comments.Comment;
import school.campusconnect.datamodel.comments.GroupCommentData;
import school.campusconnect.datamodel.comments.GroupCommentItem;
import school.campusconnect.datamodel.comments.GroupCommentResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.views.SMBDialogUtils;

/**
 * Created by frenzin04 on 1/18/2017.
 */
public class CommentsFragment extends BaseFragment implements LeafManager.OnAddUpdateListener<GroupValidationError>, CommentListAdapter.OnItemClickListener, LeafManager.OnCommunicationListener {

    private static final String TAG = "CommentsFragment";
    private LayoutListCommentsWithoutRefreshBinding mBinding;
    private CommentListAdapter mAdapter;
    Intent intent;
    public boolean mIsLoading = false;
    public int totalPages = 1;
    public int currentPage = 1;
    String groupId = "";
    String postId = "";
    String teamId = "";
    int position = -1;
    int count;
    List<GroupCommentItem> commentList = new ArrayList<>();

    public DataBinding dataBinding;

    String strComment = "";
    String type;

    Comment mComment;
    private DatabaseHandler databaseHandler;
    private LinearLayoutManager linearLayoutManager;
    public static String selectedUserId;

    public CommentsFragment() {

    }

    public static CommentsFragment newInstance(Bundle bundle) {
        CommentsFragment fragment = new CommentsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_list_comments_without_refresh, container, false);
        mBinding.setSize(1);
        mBinding.setMessage(R.string.msg_no_comments);
        mBinding.setHandlers(CommentsActivity.commentsActivity);
        dataBinding = new DataBinding();
        mComment = new Comment();
        mComment.setCommentEditTextId(new ObservableInt(R.id.edt_comment));
        mComment.setCommentString(new ObservableField<>(""));
        mBinding.setComment(mComment);
        mBinding.setHint(getResources().getString(R.string.hint_write_comment));

        init();

        setupListeners();

        setCommentImage();

        return mBinding.getRoot();


    }

    private void init() {
        databaseHandler = new DatabaseHandler(getActivity());
        count = databaseHandler.getCount();

        groupId = getArguments().getString("id", "");
        postId = getArguments().getString("post_id");

        type = getArguments().getString("type");

        AppLog.e("COMMENTS_", "type is " + type);

        if (type.equals("team") || type.equals("teamPostComment") )
            teamId = getArguments().getString("team_id");
        if (type.equals("favourite"))
            type = "group";

        if(type.equals("personal"))
            selectedUserId=getArguments().getString("user_id");

        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new CommentListAdapter(commentList, this, type, groupId, postId, teamId, count);

    }

    private void setupListeners() {
        mBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

                if (!mIsLoading && totalPages > currentPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            ) {
                        currentPage = currentPage + 1;
                        getData();
                    }
                }
            }
        });
        getData();

        mBinding.swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    mAdapter = new CommentListAdapter(commentList, CommentsFragment.this, type, groupId, postId, teamId, count);
                    currentPage = 1;
                    mAdapter.clear();
                    getData();
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                } else {
                    showNoNetworkMsg();
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

    }

    private void setCommentImage() {
        if (!TextUtils.isEmpty(LeafPreference.getInstance(getActivity()).getString(LeafPreference.PROFILE_IMAGE))) {
            Picasso.with(getActivity()).load(LeafPreference.getInstance(getActivity()).getString(LeafPreference.PROFILE_IMAGE)).into(mBinding.imgCmt);
            mBinding.imgCmt.setVisibility(View.VISIBLE);
        } else {
            mBinding.imgCmt.setVisibility(View.GONE);
        }
    }


    public void onClickAddComment(View view) {
        mComment.setCommentEditTextId(new ObservableInt(R.id.edt_comment));
        mComment.setCommentString(new ObservableField<>(mBinding.edtComment.getText().toString()));
        mBinding.setComment(mComment);
        hide_keyboard();
        AppLog.e("CLICK", "btn_comment clicked");
        if (!mComment.getCommentString().get().trim().equals(""))
            addComment(mComment.getCommentString().get());
        else
            Toast.makeText(getActivity(), "Please enter comment first", Toast.LENGTH_SHORT).show();
    }

    private void getData() {
        switch (type) {
            case "groupPostComment":
            case "group": {
                showLoadingBar(mBinding.progressBar);
                LeafManager mManager = new LeafManager();
                mIsLoading = true;
                mManager.getGroupComment(this, groupId + "", postId + "", currentPage);
                break;
            }
            case "teamPostComment":
            case "team": {
                showLoadingBar(mBinding.progressBar);
                LeafManager mManager = new LeafManager();
                mIsLoading = true;
                mManager.getTeamComment(this, groupId + "", teamId + "", postId + "", currentPage);
                break;
            }
            case "individualPostComment":
            case "personal": {
                showLoadingBar(mBinding.progressBar);
                LeafManager mManager = new LeafManager();
                mIsLoading = true;
                mManager.getPersonalComment(this, groupId, postId, selectedUserId,currentPage);
                break;
            }
        }

    }

    private void addComment(String comment) {
        switch (type) {
            case "groupPostComment":
            case "group": {
                showLoadingBar(mBinding.progressBar);
                LeafManager mManager = new LeafManager();
                mIsLoading = true;

                AddGroupCommentRequest addGroupCommentRequest = new AddGroupCommentRequest();
                addGroupCommentRequest.text = comment;
                mManager.addGroupComment(this, groupId + "", postId + "", "", addGroupCommentRequest, "comment_add");

                cleverTapComments(comment, type);
                LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISGENERALPOSTUPDATED, true);
                break;
            }
            case "teamPostComment":
            case "team": {
                showLoadingBar(mBinding.progressBar);
                LeafManager mManager = new LeafManager();
                mIsLoading = true;

                AddGroupCommentRequest addGroupCommentRequest = new AddGroupCommentRequest();
                addGroupCommentRequest.text = comment;
                mManager.addTeamComment(this, groupId + "", teamId + "", postId + "", "", addGroupCommentRequest, "comment_add");

                cleverTapComments(comment, type);
                LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISTEAMPOSTUPDATED, true);
                break;
            }
            case "individualPostComment":
            case "personal": {
                showLoadingBar(mBinding.progressBar);
                LeafManager mManager = new LeafManager();
                mIsLoading = true;

                AddGroupCommentRequest addGroupCommentRequest = new AddGroupCommentRequest();
                addGroupCommentRequest.text = comment;
                mManager.addPersonalComment(this, groupId + "", postId + "", "",selectedUserId, addGroupCommentRequest, "comment_add");
                cleverTapComments(comment, type);
                LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISPERSONALPOSTUPDATED, true);
                break;
            }
        }
    }

    private void cleverTapComments(String comment, String type) {
        if (isConnectionAvailable()) {
            try {
                CleverTapAPI cleverTap = CleverTapAPI.getInstance(getActivity());
                AppLog.e("GroupDashBoard", "Success to found cleverTap objects=>");

                HashMap<String, Object> commentAction = new HashMap<String, Object>();
                commentAction.put("id", groupId);
                commentAction.put("group_name", GroupDashboardActivityNew.group_name);
                commentAction.put("post_id", postId);

                if (type.equals("team"))
                    commentAction.put("team_id", teamId);

                commentAction.put("type", type);
                commentAction.put("comment", comment);

                cleverTap.event.push("Commented", commentAction);

            } catch (CleverTapMetaDataNotFoundException e) {
                AppLog.e("GroupDashBoard", "CleverTapMetaDataNotFoundException=>" + e.toString());
                // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
            } catch (CleverTapPermissionsNotSatisfied e) {
                AppLog.e("GroupDashBoard", "CleverTapPermissionsNotSatisfied=>" + e.toString());
                // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
            } catch (Exception ignored) {
            }

        }
    }

    private void editComment(String comment, String comment_id) {
        switch (type) {
            case "group": {
                //showLoadingDialog("Please wait...");
                showLoadingBar(mBinding.progressBar);
                LeafManager mManager = new LeafManager();
                mIsLoading = true;

                AddGroupCommentRequest addGroupCommentRequest = new AddGroupCommentRequest();
                addGroupCommentRequest.text = comment;
                mManager.addGroupComment(this, groupId + "", postId + "", comment_id + "", addGroupCommentRequest, "comment_edit");
                break;
            }
            case "team": {
                //showLoadingDialog("Please wait...");
                showLoadingBar(mBinding.progressBar);
                LeafManager mManager = new LeafManager();
                mIsLoading = true;

                AddGroupCommentRequest addGroupCommentRequest = new AddGroupCommentRequest();
                addGroupCommentRequest.text = comment;
                mManager.addTeamComment(this, groupId + "", teamId + "", postId + "", comment_id + "", addGroupCommentRequest, "comment_edit");
                break;
            }
            case "personal": {
                //showLoadingDialog("Please wait...");
                showLoadingBar(mBinding.progressBar);
                LeafManager mManager = new LeafManager();
                mIsLoading = true;

                AddGroupCommentRequest addGroupCommentRequest = new AddGroupCommentRequest();
                addGroupCommentRequest.text = comment;
                mManager.addPersonalComment(this, groupId + "", postId + "", comment_id + "",selectedUserId, addGroupCommentRequest, "comment_edit");
                break;
            }
        }
    }

    private void deleteComment(String comment_id) {
        switch (type) {
            case "groupPostComment":
            case "group": {
                // showLoadingDialog("Please wait...");
                showLoadingBar(mBinding.progressBar);
                LeafManager mManager = new LeafManager();
                mIsLoading = true;

                mManager.addGroupComment(this, groupId + "", postId + "", comment_id + "", null, "reply_delete");
                LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISGENERALPOSTUPDATED, true);
                break;
            }
            case "teamPostComment":
            case "team": {
                //showLoadingDialog("Please wait...");
                showLoadingBar(mBinding.progressBar);
                LeafManager mManager = new LeafManager();
                mIsLoading = true;

                mManager.addTeamComment(this, groupId + "", teamId + "", postId + "", comment_id + "", null, "reply_delete");
                LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISTEAMPOSTUPDATED, true);
                break;
            }
            case "individualPostComment":
            case "personal": {
                //showLoadingDialog("Please wait...");
                showLoadingBar(mBinding.progressBar);
                LeafManager mManager = new LeafManager();
                mIsLoading = true;

                mManager.addPersonalComment(this, groupId + "", postId + "", comment_id + "",selectedUserId, null, "reply_delete");
                LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISPERSONALPOSTUPDATED, true);
                break;
            }
        }
    }


    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        if (apiId == LeafManager.API_ID_GET_GROUP_COMMENT) {
            hideLoadingBar();
            AppLog.e("COMMENT", "list called");

            GroupCommentResponse res2 = (GroupCommentResponse) response;

            totalPages = res2.totalNumberOfPages;
            mIsLoading = false;

            List<GroupCommentData> responseList = res2.getResults();
            AppLog.e("COMMENT", "response list size is " + responseList.size());
            for (int i = 0; i < responseList.size(); i++) {
                GroupCommentItem groupCommentItem = new GroupCommentItem();
                groupCommentItem.id = responseList.get(i).id;
                groupCommentItem.createdById = responseList.get(i).createdById;
                groupCommentItem.insertedAt = responseList.get(i).insertedAt;
                groupCommentItem.text = responseList.get(i).text;
                groupCommentItem.createdByName = responseList.get(i).createdByName;
                groupCommentItem.createdByImage = responseList.get(i).createdByImage;
                groupCommentItem.canEdit = responseList.get(i).canEdit;
                groupCommentItem.likes = responseList.get(i).likes;
                groupCommentItem.replies = responseList.get(i).replies;
                groupCommentItem.isLiked = responseList.get(i).isLiked;
                groupCommentItem.createdByPhone = responseList.get(i).createdByPhone;
                commentList.add(groupCommentItem);
                AppLog.e("COMMENT", "for called " + i);
            }

            AppLog.e("COMMENT", "comment list size is " + commentList.size());

            if (currentPage == 1) {
                mAdapter = new CommentListAdapter(commentList, this, type, groupId, postId, teamId, count);
                mBinding.recyclerView.setAdapter(mAdapter);
            }
            mBinding.setSize(mAdapter.getItemCount());
            mAdapter.notifyDataSetChanged();
            mBinding.setSize(mAdapter.getItemCount());

        } else if (apiId == LeafManager.API_ID_ADD_GROUP_COMMENT) {
            // hideLoadingDialog();
            hideLoadingBar();
            currentPage = 1;
            commentList.clear();
            getData();
            mComment.setCommentEditTextId(new ObservableInt(R.id.edt_comment));
            mComment.setCommentString(new ObservableField<>(""));
            mBinding.setComment(mComment);

            AddCommentRes addCommentRes = (AddCommentRes) response;
            AppLog.e(TAG,"Add Comment Res : "+addCommentRes);

            if(addCommentRes.data!=null && addCommentRes.data.size()>0)
            {
                    for (int i=0;i<addCommentRes.data.size();i++){
                        new SendNotification(addCommentRes.data.get(i).deviceToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
            }

        } else if (apiId == LeafManager.API_GROUP_COMMENT_LIKE) {
            hideLoadingBar();
            if(response.status.equalsIgnoreCase("liked"))
            {
                mAdapter.getList().get(position).isLiked=true;
                mAdapter.getList().get(position).likes++;
            }
            else
            {
                mAdapter.getList().get(position).isLiked=false;
                mAdapter.getList().get(position).likes--;
            }
            mAdapter.notifyItemChanged(position);

            AppLog.e("Comments_", "onSuccess API_GROUP_COMMENT_LIKE");
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        //hideLoadingDialog();
        hideLoadingBar();
        mIsLoading = false;

        if (apiId == LeafManager.API_ID_GET_GROUP_COMMENT) {
            currentPage = currentPage - 1;
            if (currentPage < 0) {
                currentPage = 1;
            }
        }

        try {
            AppLog.e("GeneralPostFragment", "onFailure  ,, msg : " + msg);
            if (msg.contains("401")) {
                Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
                logout();
            } else {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
        }


    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<GroupValidationError> error) {
        hideLoadingBar();
        //hideLoadingDialog();
        mIsLoading = false;

        if (apiId == LeafManager.API_ID_GET_GROUP_COMMENT) {
            currentPage = currentPage - 1;
            if (currentPage < 0) {
                currentPage = 1;
            }
        }

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
    public void onException(int apiId, String error) {
        //  hideLoadingDialog();
        hideLoadingBar();
        try {
            Toast.makeText(getActivity(), getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        }

    }

    @Override
    public void onFavClick(GroupCommentItem item, int pos) {

    }

    @Override
    public void onLikeClick(GroupCommentItem item, int pos) {
        LeafManager mManager = new LeafManager();
        showLoadingBar(mBinding.progressBar);
        this.position = pos;
        if (type.equals("group") || type.equalsIgnoreCase("groupPostComment"))
            mManager.likeUnlikeGroupComment(this, groupId + "", postId + "", item.id);
        else if (type.equals("team") || type.equalsIgnoreCase("teamPostComment"))
            mManager.likeUnlikeTeamComment(this, groupId + "", teamId + "", postId + "", item.id);
    }

    @Override
    public void onPostClick(GroupCommentItem item) {

    }

    @Override
    public void onReadMoreClick(GroupCommentItem item) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if(LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.Replay_List_Changes))
        {
            mAdapter.clear();
            getData();
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.Replay_List_Changes,false);
        }
    }

    @Override
    public void onEditClick(final GroupCommentItem item, int pos) {
        SMBDialogUtils.showSMBDialogOKCancel(getActivity(), getResources().getString(R.string.dialog_are_you_want_to_delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteComment(item.id);
            }
        });

    }

    @Override
    public void onDeleteClick(GroupCommentItem item) {

    }

    @Override
    public void onNameClick(GroupCommentItem item) {
        if (item.createdById.equals(LeafPreference.getInstance(getActivity()).getString(LeafPreference.LOGIN_ID))) {
            AppLog.e("onNameClick", "else if called");
            Intent intent = new Intent(getActivity(), ProfileActivity2.class);
            startActivity(intent);
        } else {
            AppLog.e("onNameClick", "else called");
        }
    }

    @Override
    public void onLikeListClick(GroupCommentItem item) {
        Intent intent = new Intent(getActivity(), CommentsLikeActivity.class);

        if (TextUtils.isEmpty(teamId)) {
            intent.putExtra("is_for_group", true);
        } else {
            intent.putExtra("is_for_group", false);
            intent.putExtra("team_id", teamId);
        }
        intent.putExtra("id", groupId);
        intent.putExtra("post_id", postId);
        intent.putExtra("comment_id", item.id);
        startActivity(intent);
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


    public class DataBinding {
        @androidx.databinding.BindingAdapter("bind")
        public void addTextChangedListener(EditText view, final int variable) {
            final ViewDataBinding binding = DataBindingUtil.findBinding(view);

            view.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    binding.setVariable(variable, s.toString());
                    binding.executePendingBindings();
                    strComment = s.toString();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

    }

    public class Echo {
        public ObservableField<String> text = new ObservableField<>();
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        AppLog.e("tag", "onTextChanged " + s);
    }


    private class SendNotification extends AsyncTask<String,String,String>
    {
        private String receiverToken;
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
                urlConnection.setRequestProperty ("Authorization", BuildConfig.API_KEY_FIREBASE1+BuildConfig.API_KEY_FIREBASE2);
                urlConnection.setRequestProperty ("Content-Type", "application/json");

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                try {
                    JSONObject object = new JSONObject();

                    String topic;
                    String title=getResources().getString(R.string.app_name);
                    String message="";
                        message=LeafPreference.getInstance(getActivity()).getString(LeafPreference.NAME)+" commented on your post";
                        object.put("to",receiverToken);
                    JSONObject notificationObj=new JSONObject();
                    notificationObj.put("title",title);
                    notificationObj.put("body",message);
                    object.put("notification",notificationObj);

                    JSONObject dataObj=new JSONObject();
                    dataObj.put("groupId",GroupDashboardActivityNew.groupId);
                    dataObj.put("createdById",LeafPreference.getInstance(getActivity()).getString(LeafPreference.LOGIN_ID));
                    dataObj.put("title",title);
                    dataObj.put("postType","add_comment");
                    dataObj.put("Notification_type","post");
                    dataObj.put("body",message);
                    object.put("data",dataObj);

                    wr.writeBytes(object.toString());
                    Log.e(TAG , " JSON input : "+ object.toString());
                    wr.flush();
                    wr.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                AppLog.e(TAG,"responseCode :"+responseCode);
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
            AppLog.e(TAG,"server_response :"+server_response);

            if(!TextUtils.isEmpty(server_response))
            {
                AppLog.e(TAG,"Notification Sent");
            }
            else
            {
                AppLog.e(TAG,"Notification Send Fail");
            }
        }
    }
}
