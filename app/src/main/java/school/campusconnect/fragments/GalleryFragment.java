package school.campusconnect.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.AddGalleryPostActivity;
import school.campusconnect.activities.GalleryDetailActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.adapters.GalleryAdapter;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.GalleryPostRes;
import school.campusconnect.datamodel.PostDataItem;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AmazoneRemove;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.views.SMBDialogUtils;

public class GalleryFragment extends BaseFragment implements LeafManager.OnCommunicationListener, GalleryAdapter.GalleryListener, DialogInterface.OnClickListener {

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

    ArrayList<GalleryPostRes.GalleryData> listData=new ArrayList<>();
    GalleryAdapter galleryAdapter;
    private int totalPages=0;
    private int currentPage=1;
    private LeafManager manager;
    private String mGroupId;
    private GalleryPostRes.GalleryData currentItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_gallery,container,false);
        ButterKnife.bind(this,view);

        manager=new LeafManager();

        mGroupId=GroupDashboardActivityNew.groupId;

        layoutManager=new LinearLayoutManager(getActivity());
        rvGallery.setLayoutManager(layoutManager);
        galleryAdapter=new GalleryAdapter(listData,this);
        rvGallery.setAdapter(galleryAdapter);

        scrollListener();

        getData();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        galleryAdapter.notifyDataSetChanged();
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
        if(isConnectionAvailable())
        {
            showLoadingBar(progressBar);
            mIsLoading = true;
            manager.getGalleryPost(this, mGroupId+"", currentPage);
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
        if (GroupDashboardActivityNew.isPost)
            menu.findItem(R.id.menu_add_post).setVisible(true);
        else
            menu.findItem(R.id.menu_add_post).setVisible(false);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(getActivity()==null)
            return;

        if(LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISGALLERY_POST_UPDATED))
        {
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISGALLERY_POST_UPDATED, false);
            currentPage=1;
            getData();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_post:
                if(getActivity()!=null)
                {
                    Intent intent = new Intent(getActivity(), AddGalleryPostActivity.class);
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
            case LeafManager.API_GALLERY_POST:
                GalleryPostRes res = (GalleryPostRes) response;
                AppLog.e(TAG, "Post Res ; " + new Gson().toJson(res.data));

                if (currentPage == 1) {
                    PostDataItem.deleteGeneralPosts(mGroupId+"");
                    listData.clear();

                    listData.addAll(res.data);
                    AppLog.e(TAG, "current page 1");

                } else {
                    listData.addAll(res.data);
                    AppLog.e(TAG, "current page " + currentPage);
                }

                if(listData.size()==0)
                    txtEmpty.setVisibility(View.VISIBLE);
                else
                    txtEmpty.setVisibility(View.GONE);

                galleryAdapter.notifyDataSetChanged();

                totalPages = res.totalNumberOfPages;
                mIsLoading = false;
                break;
            case LeafManager.API_GALLERY_DELETE:
                Toast.makeText(getContext(), "Post Deleted Successfully", Toast.LENGTH_SHORT).show();
                currentPage=1;
                AmazoneRemove.remove(currentItem.fileName);
                getData();
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
    public void onPostClick(GalleryPostRes.GalleryData item) {
       /* if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
            Intent browserIntent = new Intent(getActivity(), TestActivity.class);
            browserIntent.putExtra("url", item.video);
            startActivity(browserIntent);

        } else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
            Intent i = new Intent(getActivity(), ViewPDFActivity.class);
            i.putExtra("pdf", item.fileName.get(0));
            i.putExtra("name", item.albumName);
            startActivity(i);

        } else if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
            Intent i = new Intent(getActivity(), FullScreenActivity.class);
            i.putExtra("image", item.fileName);
            startActivity(i);
        }*/
       Intent intent = new Intent(getActivity(), GalleryDetailActivity.class);
       intent.putExtra("data",new Gson().toJson(item));
       startActivity(intent);
    }

    @Override
    public void onDeleteClick(GalleryPostRes.GalleryData item) {
        currentItem = item;
        SMBDialogUtils.showSMBDialogOKCancel(getActivity(), "Are You Sure Want To Delete ?", this);
    }

    @Override
    public void onDeleteVideoClick(GalleryPostRes.GalleryData item, int adapterPosition) {
        AppLog.e(TAG , "onDeleteVideoClick : "+item.fileName.get(0));
        if(item.fileName!=null && item.fileName.size()>0){
            AmazoneDownload.removeVideo(getActivity(),item.fileName.get(0));
            galleryAdapter.notifyItemChanged(adapterPosition);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        AppLog.e("TeamPostFrag", "DIalog Ok Clicked ");
        if (isConnectionAvailable()) {
            showLoadingBar(progressBar);
            LeafManager manager = new LeafManager();
            manager.deleteGalleryPost(this, mGroupId+"",currentItem.getAlbumId());

        } else {
            showNoNetworkMsg();
        }

    }
}
