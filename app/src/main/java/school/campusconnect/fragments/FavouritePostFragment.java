
package school.campusconnect.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import school.campusconnect.BuildConfig;
import school.campusconnect.activities.AddQuestionActivity;
import school.campusconnect.activities.FullScreenActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.ViewPDFActivity;
import school.campusconnect.datamodel.Media.ImagePathTBL;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AmazoneHelper;
import school.campusconnect.utils.AmazoneImageDownload;
import school.campusconnect.utils.AmazoneRemove;
import school.campusconnect.utils.AmazoneVideoDownload;
import school.campusconnect.utils.AppLog;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;

import java.io.File;
import java.util.ArrayList;

import school.campusconnect.R;
import school.campusconnect.activities.TestActivity;
import school.campusconnect.activities.LikesListActivity;
import school.campusconnect.activities.SelectShareTypeActivity;
import school.campusconnect.activities.PushActivity;
import school.campusconnect.adapters.PostAdapter;
import school.campusconnect.adapters.ReportAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.LayoutListBinding;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.PostItem;
import school.campusconnect.datamodel.PostResponse;
import school.campusconnect.datamodel.reportlist.ReportItemList;
import school.campusconnect.datamodel.reportlist.ReportResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.views.SMBDialogUtils;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

public class FavouritePostFragment extends BaseFragment implements LeafManager.OnCommunicationListener, PostAdapter.OnItemClickListener, LeafManager.OnAddUpdateListener<AddPostValidationError>, DialogInterface.OnClickListener {

    private static final String TAG = "FavouritePostFragment";
    private LayoutListBinding mBinding;
    private PostAdapter mAdapter;
    LeafManager manager = new LeafManager();
    String mGroupId = "";
    int position = -1;
    int count;
    public boolean mIsLoading = false;
    public int totalPages = 1;
    public int currentPage = 1;
    public PostItem currentItem;
    private ReportAdapter mAdapter2;
    RecyclerView dialogRecyclerView;
    ProgressBar dialogProgressBar;
    ArrayList<ReportItemList> list = new ArrayList<>();
    private DatabaseHandler databaseHandler;

    ArrayList<PostItem> PostList = new ArrayList<PostItem>();
    private LinearLayoutManager layoutManager;

    public FavouritePostFragment() {

    }

    public static FavouritePostFragment newInstance(Bundle b) {
        FavouritePostFragment fragment = new FavouritePostFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_list, container, false);

        init();

        setListeners();

        getData();

        return mBinding.getRoot();
    }

    private void setListeners() {
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

//        mBinding.swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                if (isConnectionAvailable()) {
//                    currentPage = 1;
//                    getData();
//                    mBinding.swipeRefreshLayout.setRefreshing(false);
//                } else {
//                    showNoNetworkMsg();
//                    mBinding.swipeRefreshLayout.setRefreshing(false);
//                }
//            }
//        });


    }

    private void init() {
        databaseHandler = new DatabaseHandler(getActivity());
        count = databaseHandler.getCount();

        mBinding.setSize(1);
        mBinding.setMessage(R.string.msg_no_post);
        mGroupId = getArguments().getString("id");
        mBinding.swipeRefreshLayout.setVisibility(View.GONE);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mAdapter = new PostAdapter(PostList, this, "favourite",databaseHandler,count);
        mBinding.recyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();

        AppLog.e(TAG, "onResume : " + LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISGENERALPOSTUPDATED));
        if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISGENERALPOSTUPDATED)) {
            mAdapter.clear();
            currentPage = 1;
            getData();
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISGENERALPOSTUPDATED, false);
        }
    }

    private void getData() {
        mBinding.progressBar.setVisibility(View.VISIBLE);
        mIsLoading = true;
        manager.getFavPosts(this, mGroupId+"", currentPage);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        //hideLoadingBar();
        mBinding.progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_ID_FAV_POST:
                PostResponse res = (PostResponse) response;
                if (currentPage == 1) {
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
                break;

            case LeafManager.API_ID_FAV:
                if(response.status.equalsIgnoreCase("favourite"))
                {
                    PostList.get(position).isFavourited=true;
                }
                else
                {
                    PostList.get(position).isFavourited=false;
                }
                mAdapter.notifyItemChanged(position);
                getData();
                break;

            case LeafManager.API_ID_LIKE:
                if(response.status.equalsIgnoreCase("liked"))
                {
                    PostList.get(position).isLiked=true;
                    PostList.get(position).likes++;
                }
                else
                {
                    PostList.get(position).isLiked=false;
                    PostList.get(position).likes--;
                }
                mAdapter.notifyItemChanged(position);
                break;

            case LeafManager.API_ID_DELETE_POST:
                try {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_post_delete_successfully), Toast.LENGTH_SHORT).show();
                    getData();
                    AmazoneRemove.remove(currentItem.fileName);
                }catch (Exception e){}

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
                try {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_post_reported_sucessfully), Toast.LENGTH_SHORT).show();
                }catch (Exception e){}

                break;
        }

    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<AddPostValidationError> error) {
        mBinding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(int apiId, String msg) {
        mBinding.progressBar.setVisibility(View.GONE);
        mIsLoading = false;
        currentPage = currentPage - 1;
        if (currentPage < 0) {
            currentPage = 1;
        }
        if (msg.contains("401:Unauthorized")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
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
        hideLoadingBar();
        mIsLoading = false;
        currentPage = currentPage - 1;
        if (currentPage < 0) {
            currentPage = 1;
        }
        try {
            Toast.makeText(getActivity(), getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
           AppLog.e("FavouritePostFrag", "onException : " + msg);
        }
    }

    @Override
    public void onFavClick(PostItem item, int pos) {
        showLoadingBar(mBinding.progressBar);
        position = pos;

        manager.setFav(this, mGroupId+"", item.postIdForBookmark);
    }

    @Override
    public void onLikeClick(PostItem item, int position) {

        showLoadingBar(mBinding.progressBar);
            this.position = position;

            manager.setLikes(this, mGroupId+"", item.postIdForBookmark);

    }

    @Override
    public void onPostClick(PostItem item) {
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
        SMBDialogUtils.showSMBDialogOKCancel(getActivity(), getResources().getString(R.string.dialog_are_you_want_to_delete), FavouritePostFragment.this);
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
        intent.putExtra("post_id", item.postIdForBookmark);
       AppLog.e("SHARE", "Post id2 is " + item.postIdForBookmark);
        startActivity(intent);
       AppLog.e("SHARE", "Post id1 is " + item.postIdForBookmark);
    }

    @Override
    public void onQueClick(PostItem item) {
        Intent intent = new Intent(getActivity(), AddQuestionActivity.class);
        intent.putExtra("id", mGroupId);
        intent.putExtra("post_id", item.postIdForBookmark);
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
        intent.putExtra("post_id", item.postIdForBookmark);
        startActivity(intent);
    }

    @Override
    public void onNameClick(PostItem item) {

    }

    @Override
    public void onLikeListClick(PostItem item) {
        if (isConnectionAvailable()) {
            Intent intent = new Intent(getActivity(), LikesListActivity.class);
            intent.putExtra("id", mGroupId);
            intent.putExtra("post_id", item.postIdForBookmark);
            intent.putExtra("type", "group");
            startActivity(intent);
        } else {
            showNoNetworkMsg();
        }
    }

    @Override
    public void onMoreOptionClick(PostItem item) {

    }

    @Override
    public void onExternalShareClick(PostItem item) {

        boolean isDownloaded = true;

        if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {


            if (item.fileName.size()> 0)
            {
                for (int i = 0;i<item.fileName.size();i++)
                {
                    String key =  Constants.decodeUrlToBase64(item.fileName.get(i)).replace(AmazoneHelper.BUCKET_NAME_URL, "");
                    String Filepath;

                    if (key.contains("/")) {
                        String[] splitStr = key.split("/");
                        Filepath = splitStr[1];
                    } else {
                        Filepath = key;
                    }

                    if (ImagePathTBL.getLastInserted(Filepath).size() == 0)
                    {
                        isDownloaded = false;
                    }
                }

                if (isDownloaded)
                {
                    ArrayList<File> files =new ArrayList<>();

                    for (int i = 0;i<item.fileName.size();i++)
                    {
                        String key =  Constants.decodeUrlToBase64(item.fileName.get(i)).replace(AmazoneHelper.BUCKET_NAME_URL, "");
                        String Filepath;

                        if (key.contains("/")) {
                            String[] splitStr = key.split("/");
                            Filepath = splitStr[1];
                        } else {
                            Filepath = key;
                        }

                        files.add(new File(ImagePathTBL.getLastInserted(Filepath).get(0).url));

                    }

                    ArrayList<Uri> uris = new ArrayList<>();

                    for(File file: files){

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
                    if (!AmazoneDownload.isPdfDownloaded(getContext(),item.fileName.get(i)))
                    {
                        isDownloaded = false;
                    }
                }

                if (isDownloaded)
                {
                    ArrayList<Uri> files =new ArrayList<>();

                    for (int i = 0;i<item.fileName.size();i++)
                    {
                        AppLog.e(TAG, "URL DECODE"+Constants.decodeUrlToBase64(item.fileName.get(i)));

                        files.add(AmazoneDownload.getDownloadPath(getContext(),item.fileName.get(i)));
                    }

                    /*ArrayList<Uri> uris = new ArrayList<>();

                    for(File file: files){

                        AppLog.e(TAG, "URL "+file.getAbsolutePath());

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            uris.add(FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file));
                        } else {
                            uris.add(Uri.fromFile(file));
                        }

                    }
*/
                    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    intent.setType("application/pdf");
                    intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
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
                    if (!AmazoneVideoDownload.isVideoDownloaded(getContext(),item.fileName.get(i)))
                    {
                        isDownloaded = false;
                    }
                }

                if (isDownloaded)
                {
                    ArrayList<Uri> files =new ArrayList<>();

                    for (int i = 0;i<item.fileName.size();i++)
                    {
                        AppLog.e(TAG, "URL DECODE"+Constants.decodeUrlToBase64(item.fileName.get(i)));

                        files.add(AmazoneVideoDownload.getDownloadPath(getContext(),item.fileName.get(i)));
                    }

                    /*ArrayList<Uri> uris = new ArrayList<>();

                    for(File file: files){

                        AppLog.e(TAG, "URL "+file.getAbsolutePath());

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            uris.add(FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file));
                        } else {
                            uris.add(Uri.fromFile(file));
                        }

                    }*/

                    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    intent.setType("video/*");
                    intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
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
                    String key =  Constants.decodeUrlToBase64(item.fileName.get(i)).replace(AmazoneHelper.BUCKET_NAME_URL, "");
                    String Filepath;

                    if (key.contains("/")) {
                        String[] splitStr = key.split("/");
                        Filepath = splitStr[1];
                    } else {
                        Filepath = key;
                    }

                    if (ImagePathTBL.getLastInserted(Filepath).size() == 0)
                    {
                        isDownloaded = false;
                    }
                }

                if (isDownloaded)
                {
                    ArrayList<File> files =new ArrayList<>();

                    for (int i = 0;i<item.fileName.size();i++)
                    {
                        String key =  Constants.decodeUrlToBase64(item.fileName.get(i)).replace(AmazoneHelper.BUCKET_NAME_URL, "");
                        String Filepath;

                        if (key.contains("/")) {
                            String[] splitStr = key.split("/");
                            Filepath = splitStr[1];
                        } else {
                            Filepath = key;
                        }

                        files.add(new File(ImagePathTBL.getLastInserted(Filepath).get(0).url));

                    }

                    ArrayList<Uri> uris = new ArrayList<>();

                    for(File file: files){

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
        AppLog.e(TAG , "onDeleteVideoClick : "+item.fileName.get(0));
        if(item.fileName!=null && item.fileName.size()>0){
            AmazoneDownload.removeVideo(getActivity(),item.fileName.get(0));
            mAdapter.notifyItemChanged(adapterPosition);
        }
    }

    @Override
    public void callBirthdayPostCreation(PostItem item, int position) {

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        AppLog.e("TeamPostFrag", "DIalog Ok Clicked ");
        if (isConnectionAvailable()) {
            showLoadingBar(mBinding.progressBar);
            LeafManager manager = new LeafManager();
            manager.deletePost(this, mGroupId+"", currentItem.postIdForBookmark, "group");

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
        mManager.reportPost(this, mGroupId+"",  currentItem.postIdForBookmark, report_id);
    }

}
