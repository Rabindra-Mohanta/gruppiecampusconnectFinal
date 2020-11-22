package school.campusconnect.fragments;

import android.app.Dialog;
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
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import school.campusconnect.BuildConfig;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.datamodel.comments.AddCommentRes;
import school.campusconnect.utils.AppLog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
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
import java.util.List;

import school.campusconnect.R;
import school.campusconnect.activities.ReplyActivity;
import school.campusconnect.adapters.ReplyListAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.LayoutListRepliesWithoutRefreshBinding;
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
 * Created by frenzin04 on 1/20/2017.
 */
public class ReplyFragment extends BaseFragment implements LeafManager.OnAddUpdateListener<GroupValidationError>, ReplyListAdapter.OnItemClickListener, LeafManager.OnCommunicationListener {

    private static final String TAG = "ReplyFragment";
    private LayoutListRepliesWithoutRefreshBinding mBinding;
    private ReplyListAdapter mAdapter;
    Intent intent;
    public boolean mIsLoading = false;
    public int totalPages = 1;
    public int currentPage = 1;
    String groupId ="";
    String postId = "";
    String commentId="";
    String teamId = "";
    int position = -1;
    int count;
    List<GroupCommentItem> commentList = new ArrayList<>();

    public DataBinding dataBinding;

    String strComment = "";
    String type = "";

    Comment mComment;
    private String selectedUserId;

    public ReplyFragment() {

    }

    public static ReplyFragment newInstance(Bundle bundle) {
        ReplyFragment fragment = new ReplyFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_list_replies_without_refresh, container, false);
        mBinding.setSize(2);
        mBinding.setMessage(R.string.msg_no_reply);
        mBinding.setHandlers(ReplyActivity.replyActivity);
        dataBinding = new DataBinding();
//        mBinding.setData(dataBinding);
        mComment = new Comment();
        mComment.setCommentEditTextId(new ObservableInt(R.id.edt_comment));
        mComment.setCommentString(new ObservableField<>(""));
        mBinding.setComment(mComment);
        mBinding.setHint("Write a reply...");
        final LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView.setLayoutManager(manager);

        groupId = getArguments().getString("id");
        postId = getArguments().getString("post_id");
        commentId = getArguments().getString("comment_id");
        type = getArguments().getString("type", "");

        DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
        count = databaseHandler.getCount();

        mAdapter = new ReplyListAdapter(commentList, this, type, groupId, postId, commentId, count);

        if (type.equals("team"))
            teamId = getArguments().getString("team_id");

        if (type.equals("favourite"))
            type = "group";

        if(type.equals("personal"))
            selectedUserId=getArguments().getString("user_id");

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
                    mAdapter = new ReplyListAdapter(commentList, ReplyFragment.this, type, groupId, postId, commentId, count);
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
        setCommentImage();
        return mBinding.getRoot();
    }
    private void setCommentImage() {
        if(!TextUtils.isEmpty(LeafPreference.getInstance(getActivity()).getString(LeafPreference.PROFILE_IMAGE)))
        {
            Picasso.with(getActivity()).load(LeafPreference.getInstance(getActivity()).getString(LeafPreference.PROFILE_IMAGE)).into(mBinding.imgCmt);
            mBinding.imgCmt.setVisibility(View.VISIBLE);
        }
        else
        {
            mBinding.imgCmt.setVisibility(View.GONE);
        }
    }

    public void onClickAddComment(View view) {
        mComment.setCommentEditTextId(new ObservableInt(R.id.edt_comment));
        mComment.setCommentString(new ObservableField<>(mBinding.edtComment.getText().toString()));
        mBinding.setComment(mComment);
       AppLog.e("CLICK", "btn_comment clicked");
        if (!mComment.getCommentString().get().equals(""))
            addComment(mComment.getCommentString().get());
        else
            Toast.makeText(getActivity(), "Please enter reply first", Toast.LENGTH_SHORT).show();
    }

    private void getData() {
        switch (type) {
            case "group": {
                showLoadingBar(mBinding.progressBar);
                LeafManager mManager = new LeafManager();
                mIsLoading = true;
                mManager.getGroupReply(this, groupId+"", postId+"", commentId+"", currentPage);
                break;
            }
            case "team": {
                showLoadingBar(mBinding.progressBar);
                LeafManager mManager = new LeafManager();
                mIsLoading = true;
                mManager.getTeamReply(this, groupId+"", teamId+"", postId+"", commentId+"", currentPage);
                break;
            }
            case "personal": {
                showLoadingBar(mBinding.progressBar);
                LeafManager mManager = new LeafManager();
                mIsLoading = true;
                mManager.getPersonalReply(this, groupId, postId, commentId,selectedUserId, currentPage);
                break;
            }
        }
    }

    private void addComment(String comment) {
        switch (type) {
            case "group": {
                //showLoadingDialog("Please wait...");
                showLoadingBar(mBinding.progressBar);
                LeafManager mManager = new LeafManager();
                mIsLoading = true;

                AddGroupCommentRequest addGroupCommentRequest = new AddGroupCommentRequest();
                addGroupCommentRequest.text = comment;
                mManager.addGroupComment(this, groupId+"", postId+"", commentId+"", addGroupCommentRequest, "reply_add");
                LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISGENERALPOSTUPDATED, true);
                break;
            }
            case "team": {
                //showLoadingDialog("Please wait...");
                showLoadingBar(mBinding.progressBar);
                LeafManager mManager = new LeafManager();
                mIsLoading = true;

                AddGroupCommentRequest addGroupCommentRequest = new AddGroupCommentRequest();
                addGroupCommentRequest.text = comment;
                mManager.addTeamComment(this, groupId+"", teamId+"", postId+"", commentId+"", addGroupCommentRequest, "reply_add");
                LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISTEAMPOSTUPDATED, true);
                break;
            }
            case "personal": {
                //showLoadingDialog("Please wait...");
                showLoadingBar(mBinding.progressBar);
                LeafManager mManager = new LeafManager();
                mIsLoading = true;

                AddGroupCommentRequest addGroupCommentRequest = new AddGroupCommentRequest();
                addGroupCommentRequest.text = comment;
                mManager.addPersonalComment(this, groupId+"", postId+"", commentId+"",selectedUserId, addGroupCommentRequest, "reply_add");
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

//                mAdapter2 = new TeamListAdapter(new ArrayList<TeamPostGetItem>(), this, "team", mGroupId);
//                mAdapter2.addItems(res2.getResults());
                /*if (currentPage2 == 1) {
                    mBinding.recyclerView2.setAdapter(mAdapter2);
                }
                mBinding.setSize(mAdapter2.getItemCount());
                mAdapter2.notifyDataSetChanged();
                mBinding.setSize(mAdapter2.getItemCount());*/
            totalPages = res2.totalNumberOfPages;
            mIsLoading = false;

            List<GroupCommentData> responseList = res2.getResults();
           AppLog.e("COMMENT", "response list size is " + responseList.size());
            for (int i = 0; i < responseList.size(); i++) {
                GroupCommentItem groupCommentItem = new GroupCommentItem();
                groupCommentItem.id = responseList.get(i).id;
                groupCommentItem.insertedAt = responseList.get(i).insertedAt;
                groupCommentItem.text = responseList.get(i).text;
                groupCommentItem.createdByName = responseList.get(i).createdByName;
                groupCommentItem.createdByImage = responseList.get(i).createdByImage;
                groupCommentItem.canEdit = responseList.get(i).canEdit;
                groupCommentItem.likes = responseList.get(i).likes;
                groupCommentItem.isLiked = responseList.get(i).isLiked;
                groupCommentItem.replies = responseList.get(i).replies;
                groupCommentItem.createdByPhone = responseList.get(i).createdByPhone;
                commentList.add(groupCommentItem);
               AppLog.e("COMMENT", "for called " + i);
            }

           AppLog.e("COMMENT", "comment list size is " + commentList.size());

            if (currentPage == 1) {
                mAdapter = new ReplyListAdapter(commentList, this, type, groupId, postId, commentId, count);
                mBinding.recyclerView.setAdapter(mAdapter);
            }
            mBinding.setSize(mAdapter.getItemCount());
            mAdapter.notifyDataSetChanged();
            mBinding.setSize(mAdapter.getItemCount());

        } else if (apiId == LeafManager.API_ID_ADD_GROUP_COMMENT) {
            //hideLoadingDialog();
            hideLoadingBar();
            currentPage = 1;
            commentList.clear();
            getData();
            mComment.setCommentEditTextId(new ObservableInt(R.id.edt_comment));
            mComment.setCommentString(new ObservableField<>(""));
            mBinding.setComment(mComment);
            if(getActivity()!=null)
                LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.Replay_List_Changes,true);

            AddCommentRes addCommentRes = (AddCommentRes) response;
            AppLog.e(TAG,"Add Reply Res : "+addCommentRes);

            if(addCommentRes.data!=null && addCommentRes.data.size()>0)
            {
                for (int i=0;i<addCommentRes.data.size();i++){
                    new SendNotification(addCommentRes.data.get(i).deviceToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }


        } else if (apiId == LeafManager.API_GROUP_COMMENT_LIKE) {
            if(getActivity()!=null)
                LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.Replay_List_Changes,true);
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
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
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
           AppLog.e("GeneralPostFragment", "onFailure  ,, msg : " + msg);
            if (msg.contains("401")) {
                Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
                logout();
            } else if (msg.contains("404")) {
            } else {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){}


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
            } else if (error.status.equals("404")) {
            } else {
                Toast.makeText(getActivity(), error.title, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){}


    }

    @Override
    public void onException(int apiId, String error) {
        hideLoadingBar();
        //hideLoadingDialog();
        try {
            Toast.makeText(getActivity(), getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
        }catch (Exception e){}

    }

    @Override
    public void onFavClick(GroupCommentItem item, int pos) {

    }

    @Override
    public void onLikeClick(GroupCommentItem item, int pos) {
        LeafManager mManager = new LeafManager();
        showLoadingBar(mBinding.progressBar);
        this.position = pos;

        if (type.equals("group"))
            mManager.likeUnlikeGroupComment(this, groupId+"", postId+"", item.id);
        else if (type.equals("team"))
            mManager.likeUnlikeTeamComment(this, groupId+"", teamId+"", postId+"", item.id);
    }

    @Override
    public void onPostClick(GroupCommentItem item) {

    }

    @Override
    public void onReadMoreClick(GroupCommentItem item) {

    }

    @Override
    public void onEditClick(GroupCommentItem item, int pos) {

        showEditDialog(item.text, item.id);

    }

    @Override
    public void onDeleteClick(final GroupCommentItem item) {
        SMBDialogUtils.showSMBDialogOKCancel(getActivity(), "Are You Sure Want To Delete ?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteComment(item.id);
            }
        });

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

    public void showEditDialog(String comment, final String comment_id) {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_layout_comment_edit);

        Button btn_edit = (Button) dialog.findViewById(R.id.btn_edit);

        Button btn_delete = (Button) dialog.findViewById(R.id.btn_delete);

        final EditText edt_comment = (EditText) dialog.findViewById(R.id.edt_comment);

        edt_comment.setText(comment);

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edt_comment.getText().toString().equals("")) {
                    editComment(edt_comment.getText().toString(), comment_id);
                    dialog.dismiss();
                } else
                    Toast.makeText(getActivity(), "Enter comment first", Toast.LENGTH_SHORT).show();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteComment(comment_id);
                dialog.dismiss();
            }
        });

        dialog.show();
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
                mManager.addGroupComment(this, groupId+"", postId+"", comment_id+"", addGroupCommentRequest, "comment_edit");
                break;
            }
            case "team": {
                //showLoadingDialog("Please wait...");
                showLoadingBar(mBinding.progressBar);
                LeafManager mManager = new LeafManager();
                mIsLoading = true;

                AddGroupCommentRequest addGroupCommentRequest = new AddGroupCommentRequest();
                addGroupCommentRequest.text = comment;
                mManager.addTeamComment(this, groupId+"", teamId+"", postId+"", comment_id, addGroupCommentRequest, "comment_edit");
                break;
            }
            case "personal": {
                //showLoadingDialog("Please wait...");
                showLoadingBar(mBinding.progressBar);
                LeafManager mManager = new LeafManager();
                mIsLoading = true;

                AddGroupCommentRequest addGroupCommentRequest = new AddGroupCommentRequest();
                addGroupCommentRequest.text = comment;
                mManager.addPersonalComment(this, groupId+"", postId+"", comment_id+"",selectedUserId, addGroupCommentRequest, "comment_edit");
                break;
            }
        }
    }

    private void deleteComment(String comment_id) {
        switch (type) {
            case "group": {
                //showLoadingDialog("Please wait...");
                showLoadingBar(mBinding.progressBar);
                LeafManager mManager = new LeafManager();
                mIsLoading = true;

                mManager.addGroupComment(this, groupId+"", postId+"", comment_id+"", null, "reply_delete");
                break;
            }
            case "team": {
                //showLoadingDialog("Please wait...");
                showLoadingBar(mBinding.progressBar);
                LeafManager mManager = new LeafManager();
                mIsLoading = true;

                mManager.addTeamComment(this, groupId+"", teamId+"", postId+"", comment_id, null, "reply_delete");
                break;
            }
            case "personal": {
                //showLoadingDialog("Please wait...");
                showLoadingBar(mBinding.progressBar);
                LeafManager mManager = new LeafManager();
                mIsLoading = true;

                mManager.addPersonalComment(this, groupId+"", postId+"", comment_id+"", selectedUserId,null, "reply_delete");
                break;
            }
        }
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
                    message=LeafPreference.getInstance(getActivity()).getString(LeafPreference.NAME)+" replied on your comment";
                    object.put("to",receiverToken);
                    JSONObject notificationObj=new JSONObject();
                    notificationObj.put("title",title);
                    notificationObj.put("body",message);
                    object.put("notification",notificationObj);

                    JSONObject dataObj=new JSONObject();
                    dataObj.put("groupId",GroupDashboardActivityNew.groupId);
                    dataObj.put("createdById",LeafPreference.getInstance(getActivity()).getString(LeafPreference.LOGIN_ID));
                    dataObj.put("title",title);
                    dataObj.put("postType","add_reply");
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
