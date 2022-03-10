
package school.campusconnect.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import school.campusconnect.utils.AppLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;

import java.util.ArrayList;

import school.campusconnect.R;
import school.campusconnect.activities.TestActivity;
import school.campusconnect.activities.AddQuestionActivity;
import school.campusconnect.activities.FullScreenActivity;
import school.campusconnect.activities.ViewPDFActivity;
import school.campusconnect.adapters.PersonalChatContactsAdapter;
import school.campusconnect.adapters.QuestionAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.LayoutListQuestionsBinding;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.comments.Comment;
import school.campusconnect.datamodel.personalchat.PersonalPostItem;
import school.campusconnect.datamodel.question.QuestionData;
import school.campusconnect.datamodel.question.QuestionResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.views.SMBDialogUtils;

public class QuestionListFragment extends BaseFragment implements LeafManager.OnCommunicationListener, QuestionAdapter.OnItemClickListener,
        DialogInterface.OnClickListener, LeafManager.OnAddUpdateListener<AddPostValidationError>, PersonalChatContactsAdapter.OnChatSelected {

    private LayoutListQuestionsBinding mBinding;
    private QuestionAdapter mAdapter2;
    LeafManager manager = new LeafManager();
    String mGroupId = "";
    String post_id = "";
    int position = -1;
    int likecount = 0;
    int count;
    public static boolean isPersonalChat = false;
    public boolean mIsLoading2 = false;
    public int totalPages2 = 1;
    public int currentPage2 = 1;
    public static boolean isInbox;
    boolean fromNotifications;
    public QuestionData currentItem;
    boolean isScroll2 = false;
    boolean mIsLoading = false;
    static QuestionListFragment fragment_new;
    String strComment = "";

    public DataBinding dataBinding;

    Comment mComment;

    public QuestionListFragment() {

    }

    public static QuestionListFragment newInstance(String groupId, String post_id) {
        QuestionListFragment fragment = new QuestionListFragment();
        Bundle b = new Bundle();
        b.putString("id", groupId);
        b.putString("post_id", post_id);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_list_questions, container, false);
        mBinding.setSize(1);
        mBinding.setMessage(R.string.msg_no_que);
        dataBinding = new DataBinding();
        mComment = new Comment();
        mComment.setCommentEditTextId(new ObservableInt(R.id.edt_comment));
        mComment.setCommentString(new ObservableField<>(""));
        mBinding.setPost(mComment);
        mBinding.setHint("Write a post...");
        mGroupId = getArguments().getString("id");
        post_id = getArguments().getString("post_id");
        fromNotifications = getArguments().getBoolean("fromNotifications");

        mIsLoading2 = false;

        isScroll2 = false;

        isPersonalChat = true;
        if (mAdapter2 != null)
            mAdapter2.clear();
        currentPage2 = 1;
        mIsLoading2 = false;

        final LinearLayoutManager manager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView2.setLayoutManager(manager2);

        isInbox = true;
        isPersonalChat = false;
        fragment_new = this;

        mIsLoading2 = false;
        isScroll2 = false;

        isPersonalChat = true;
        if (mAdapter2 != null)
            mAdapter2.clear();
        currentPage2 = 1;

        DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
        count = databaseHandler.getCount();

        /*mAdapter2 = new QuestionAdapter(new ArrayList<QuestionData>(), this, "personal", mGroupId);
        getData2(selectedFriend);*/

        mAdapter2 = new QuestionAdapter(new ArrayList<QuestionData>(), this, "personal", mGroupId, count);
        getData2();

        mBinding.recyclerView2.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = manager2.getChildCount();
                int totalItemCount = manager2.getItemCount();
                int firstVisibleItemPosition = manager2.findFirstVisibleItemPosition();

                if (!mIsLoading2 && totalPages2 > currentPage2) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            ) {
                        currentPage2 = currentPage2 + 1;
                        isScroll2 = true;
                        getData2();
                    }
                }
            }
        });

        mBinding.swipeRefreshLayout2.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    currentPage2 = 1;
                    isScroll2 = false;
                    getData2();
                    mBinding.swipeRefreshLayout2.setRefreshing(false);
                } else {
                    showNoNetworkMsg();
                    mBinding.swipeRefreshLayout2.setRefreshing(false);
                }
            }
        });

        return mBinding.getRoot();
    }

    private void getData2() {
        showLoadingBar(mBinding.progressBar2);
        mIsLoading = true;
        manager.getQuestions(this, mGroupId+"", currentPage2);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();

        switch (apiId) {

            case LeafManager.API_GET_QUE:
                QuestionResponse res2 = (QuestionResponse) response;

                if (currentPage2 == 1) {
                    mAdapter2 = new QuestionAdapter(new ArrayList<QuestionData>(), this, "personal", mGroupId, count);
                    mAdapter2.addItems(res2.getResults());
                    mBinding.recyclerView2.setAdapter(mAdapter2);

                } else {
                    mAdapter2.addItems(res2.getResults());
                }
                mBinding.setSize(mAdapter2.getItemCount());
                mAdapter2.notifyDataSetChanged();
                mBinding.setSize(mAdapter2.getItemCount());
                totalPages2 = res2.totalPages;
                mIsLoading = false;

                break;

            case LeafManager.API_DELETE_QUE:
                currentPage2 = 1;
                isScroll2 = false;
                getData2();
                break;

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISPERSONALPOSTUPDATED)) {
            mAdapter2.clear();
            currentPage2 = 1;
            isScroll2 = false;
            getData2();
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISPERSONALPOSTUPDATED, false);
        }
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<AddPostValidationError> error) {
        hideLoadingBar();
        mIsLoading = false;

        //Toast.makeText(getActivity(), error.title, Toast.LENGTH_SHORT).show();
        currentPage2 = currentPage2 - 1;
        if (currentPage2 < 0) {
            currentPage2 = 1;
        }
        try {
           AppLog.e("PersonalPostFragmentNew", "onFailure  ,, msg : " + error);
            if (error.status.equals("401")) {
                Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
                logout();
            } else if (error.status.equals("404")) {
                Toast.makeText(getActivity(), "No posts available.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), error.title, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){}

    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        mIsLoading = false;
        //Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        currentPage2 = currentPage2 - 1;
        if (currentPage2 < 0) {
            currentPage2 = 1;
        }
        try {
            if (msg.contains("401:Unauthorized") || msg.contains("401")) {
                Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
                logout();
            } else if (msg.contains("404")) {
                Toast.makeText(getActivity(), "No posts available.", Toast.LENGTH_SHORT).show();
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
        currentPage2 = currentPage2 - 1;
        if (currentPage2 < 0) {
            currentPage2 = 1;
        }
        //Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFavClick(QuestionData item, int pos) {

    }

    @Override
    public void onLikeClick(QuestionData item, int position) {

    }

    @Override
    public void onPostClick1(QuestionData item) {
        /*if (item.video != null) {
            Intent browserIntent = new Intent(getActivity(), TestActivity.class);
            browserIntent.putExtra("url", item.video);
            startActivity(browserIntent);
           *//* try {
                String key = item.video.substring(item.video.indexOf("=") + 1);
               AppLog.e("key ", key);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + key));
                startActivity(intent);
            } catch (Exception ex) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(item.video));
                startActivity(intent);
            }*//*
        } else if (item.pdf != null) {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.pdf));
                startActivity(browserIntent);
            } catch (ActivityNotFoundException e) {

            }
        }*/
        if (item.video != null) {
      /*      String key = item.video.substring(item.video.indexOf("=") + 1);
            PostAdapter.videoView.setVideoURI(Uri.parse("vnd.youtube://" + key));
            PostAdapter.videoView.setMediaController(new MediaController(getActivity()));
            PostAdapter.videoView.requestFocus();
            PostAdapter.videoView.start();*/

            Intent browserIntent = new Intent(getActivity(), TestActivity.class);
            browserIntent.putExtra("url", item.video);
            startActivity(browserIntent);

            // ((QuestionListActivity) getActivity()).playYoutubeVideo(getYouTubeId(item.video));

           /* try {
                String key = item.video.substring(item.video.indexOf("=") + 1);
               AppLog.e("key ", key);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + key));
                startActivity(intent);
            } catch (Exception ex) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.video));
                startActivity(intent);
            }*/
        } else if (item.pdf != null) {
            /*try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.pdf));
                startActivity(browserIntent);
            } catch (ActivityNotFoundException e) {

            }*/
            Intent i = new Intent(getActivity(), ViewPDFActivity.class);
            i.putExtra("pdf", item.pdf);
            i.putExtra("name", item.questionFrom);
            i.putExtra("thumbnail", item.image);
            startActivity(i);
        } else if (item.image != null) {
            Intent i = new Intent(getActivity(), FullScreenActivity.class);
            i.putExtra("image", item.image);
            startActivity(i);
        }
    }

    @Override
    public void onReadMoreClick(QuestionData item) {
        Intent intent = new Intent(getActivity(), AddQuestionActivity.class);
        intent.putExtra("id", mGroupId);
        intent.putExtra("post_id", item.id);
        intent.putExtra("type", "ans");
        startActivity(intent);
    }

    @Override
    public void onEditClick(QuestionData item) {

    }

    @Override
    public void onDeleteClick(QuestionData item) {
        currentItem = item;
        SMBDialogUtils.showSMBDialogOKCancel(getActivity(), "Are You Sure Want To Delete ?", this);
    }

    @Override
    public void onReportClick(QuestionData item) {

    }

    @Override
    public void onShareClick(QuestionData item) {

    }

    public void refreshPersonalAdapter(BaseResponse response) {
        QuestionResponse res = (QuestionResponse) response;
        mAdapter2.clear();
        mAdapter2.addItems(res.getResults());
        mBinding.recyclerView2.setAdapter(mAdapter2);
        mBinding.setSize(mAdapter2.getItemCount());
        mAdapter2.notifyDataSetChanged();
        mBinding.setSize(mAdapter2.getItemCount());
        totalPages2 = res.totalPages;
        mIsLoading = false;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
       AppLog.e("PersonalPostFrag", "DIalog Ok Clicked ");
        showLoadingBar(mBinding.progressBar2);
        LeafManager manager = new LeafManager();
        manager.deleteQue(this, mGroupId+"", currentItem.id);
    }

    @Override
    public void onNameClick(PersonalPostItem item) {

        /*selectedFriend = item.id;
        mIsLoading2 = false;
        ((QuestionListActivity) getActivity()).relPost.setVisibility(View.VISIBLE);

        ((QuestionListActivity) getActivity()).hideFloatingButton();

        mBinding.relTeamList.setVisibility(View.GONE);
        mBinding.relTeamDetails.setVisibility(View.VISIBLE);
        isScroll2 = false;

        isPersonalChat = true;
        if (mAdapter2 != null)
            mAdapter2.clear();
        currentPage2 = 1;

        *//*mAdapter2 = new QuestionAdapter(new ArrayList<QuestionData>(), this, "personal", mGroupId);
        getData2(selectedFriend);*//*
       AppLog.e("PersonalPost", "DataFromAPI");

        List<PostDataItem> dataItemList = PostDataItem.getPersonalChatPosts(mGroupId, selectedFriend);
       AppLog.e("DATABASE_CHAT", "posts are " + mGroupId + " frnd id " + selectedFriend);
       AppLog.e("ListSize", String.valueOf(dataItemList.size()));
        if (dataItemList.size() != 0) {
           AppLog.e("DATABASE_CHAT", "size not 0 --> " + dataItemList.size());
            mBinding.progressBar.setVisibility(View.VISIBLE);
            mData2.clear();
            for (int i = 0; i < dataItemList.size(); i++) {
                QuestionData QuestionData = new QuestionData();
                QuestionData.id = dataItemList.get(i).id;
                QuestionData.createdBy = dataItemList.get(i).createdBy;
                QuestionData.profile = dataItemList.get(i).profile;
                QuestionData.postedAt = dataItemList.get(i).postedAt;
                QuestionData.title = dataItemList.get(i).title;
                QuestionData.shortText = dataItemList.get(i).shortText;
                QuestionData.image = dataItemList.get(i).image;
                QuestionData.pdf = dataItemList.get(i).pdf;
                QuestionData.video = dataItemList.get(i).video;
                QuestionData.text = dataItemList.get(i).text;
                QuestionData.likes = dataItemList.get(i).likes;
                QuestionData.comments = dataItemList.get(i).comments;
                QuestionData.isLiked = dataItemList.get(i).isLiked;
                QuestionData.isFavourited = dataItemList.get(i).isFavourited;
                QuestionData.readMore = dataItemList.get(i).readMore;
                QuestionData.isEdit = dataItemList.get(i).isEdit;
                QuestionData.you = dataItemList.get(i).you;
                QuestionData.team = dataItemList.get(i).team;
                mData2.add(QuestionData);
            }
            mAdapter2 = new QuestionAdapter(new ArrayList<QuestionData>(), this, "personal", mGroupId);
            mAdapter2.addItems(mData2);
            mBinding.recyclerView2.setAdapter(mAdapter2);
            mBinding.setSize(mAdapter2.getItemCount());
            mBinding.progressBar2.setVisibility(View.GONE);
           AppLog.e("PersonalPost", "DataFromLocal");
        } else {
           AppLog.e("DATABASE_CHAT", "size 0");
            mAdapter2 = new QuestionAdapter(new ArrayList<QuestionData>(), this, "personal", mGroupId);
            getData2(selectedFriend);
           AppLog.e("PersonalPost", "DataFromAPI");
        }*/
    }


    public void onClickAddComment(View view) {
       AppLog.e("CLICK", "btn_comment clicked");
        if (!mComment.getCommentString().get().equals(""))
//            addComment(mComment.getCommentString().get());
            Toast.makeText(getActivity(), mComment.getCommentString().get(), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getActivity(), "Please enter post first", Toast.LENGTH_SHORT).show();
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

    public void refreshList() {
        mAdapter2.clear();
        isScroll2 = false;
        currentPage2 = 1;
        getData2();
    }

}
