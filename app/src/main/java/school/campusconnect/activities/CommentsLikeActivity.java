package school.campusconnect.activities;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import school.campusconnect.utils.AppLog;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import school.campusconnect.R;
import school.campusconnect.adapters.CommentLikeAdapter;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.likelist.LikeListData;
import school.campusconnect.datamodel.likelist.LikeListResponse;
import school.campusconnect.network.LeafManager;

public class CommentsLikeActivity extends BaseActivity implements LeafManager.OnCommunicationListener {

    private static final String TAG = "CommentsLikeActivity";
    RecyclerView rvLikeList;
    LeafManager manager;
    boolean isForGroup=false;
    String groupId;
    String postId;
    String commentId;
    String teamId;
    public int totalPages = 1;
    public int currentPage = 1;
    public boolean mIsLoading = false;
    private ArrayList<LikeListData> list;
    private CommentLikeAdapter likesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_like);

        initObjects();

        findViewById(R.id.txtEmpty).setVisibility(View.GONE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setBackEnabled(true);
        setTitle(R.string.lbl_likes);

        getIntentData();

        rvLikeList= (RecyclerView) findViewById(R.id.rvLikeList);
        final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        rvLikeList.setLayoutManager(linearLayoutManager);
        manager=new LeafManager();

        if(isConnectionAvailable())
        {
            getGroupComments();
        }
        else {
            showNoNetworkMsg();
        }


        rvLikeList.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                if (!mIsLoading && totalPages > currentPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        currentPage = currentPage + 1;
                        getGroupComments();
                    }
                }
            }
        });

    }

    private void initObjects() {
        list=new ArrayList<>();
        likesAdapter=new CommentLikeAdapter(this,list);
    }

    private void getGroupComments() {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        if(isForGroup)
            manager.groupCommentLikeList(this,groupId+"",postId+"",commentId+"",currentPage);
        else
            manager.teamCommentLikeList(this,groupId+"",teamId+"",postId+"",commentId+"",currentPage);

    }

    private void getIntentData() {
        if(getIntent().getExtras()!=null)
        {

            isForGroup=getIntent().getBooleanExtra("is_for_group",false);

            if(!isForGroup)
            {
                teamId=getIntent().getStringExtra("team_id");
               AppLog.e(TAG,"teamId => "+postId);
            }

            groupId=getIntent().getStringExtra("id");
            postId=getIntent().getStringExtra("post_id");
            commentId=getIntent().getStringExtra("comment_id");

           AppLog.e(TAG,"isForGroup => "+isForGroup);
           AppLog.e(TAG,"groupId => "+groupId);
           AppLog.e(TAG,"postId => "+postId);
           AppLog.e(TAG,"commentId => "+commentId);
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);

        findViewById(R.id.progressBar).setVisibility(View.GONE);
        LikeListResponse res = (LikeListResponse) response;
        totalPages = res.totalNumberOfPages;
        if(currentPage==1)
        {
            list.addAll(res.data);
            rvLikeList.setAdapter(likesAdapter);
        }
        else {
            list.addAll(res.data);
            likesAdapter.notifyDataSetChanged();
        }

        if(res.data!=null)
        {
            if(res.data.size()<=0)
            {
                findViewById(R.id.txtEmpty).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        super.onFailure(apiId, msg);
        findViewById(R.id.txtEmpty).setVisibility(View.VISIBLE);
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        if (msg.contains("401:Unauthorized")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onException(int apiId, String msg) {
        super.onException(apiId, msg);
        findViewById(R.id.txtEmpty).setVisibility(View.VISIBLE);
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        try {
            Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
           AppLog.e("LeadListFragment", "OnExecption : " + msg);
        }
    }
}
