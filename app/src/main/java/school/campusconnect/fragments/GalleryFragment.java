package school.campusconnect.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.activities.AddGalleryPostActivity;
import school.campusconnect.activities.GalleryDetailActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.adapters.GalleryAdapter;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.calendar.DayEventTBL;
import school.campusconnect.datamodel.calendar.MonthEventTBL;
import school.campusconnect.datamodel.gallery.GalleryPostRes;
import school.campusconnect.datamodel.PostDataItem;
import school.campusconnect.datamodel.gallery.GalleryTable;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AmazoneImageDownload;
import school.campusconnect.utils.AmazoneRemove;
import school.campusconnect.utils.AmazoneVideoDownload;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.DateTimeHelper;
import school.campusconnect.utils.MixOperations;
import school.campusconnect.views.SMBDialogUtils;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

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

   /* @Bind(R.id.swipeRefreshLayout)
    PullRefreshLayout swipeRefreshLayout;*/
   Bitmap BirthdayTempleteBitmap,MlaBitmap,UserBitmap;


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
    private boolean isAddPost = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_gallery,container,false);
        ButterKnife.bind(this,view);

        manager=new LeafManager();

        mGroupId=GroupDashboardActivityNew.groupId;

        layoutManager=new LinearLayoutManager(getActivity());
        rvGallery.setLayoutManager(layoutManager);
        galleryAdapter=new GalleryAdapter(this);
        rvGallery.setAdapter(galleryAdapter);


        if (!LeafPreference.getInstance(getContext()).getString("GalleryTotalPage").isEmpty())
        {
            totalPages = Integer.parseInt(LeafPreference.getInstance(getContext()).getString("GalleryTotalPage"));
        }


        scrollListener();

        checkEvent();

        getLocally();

        return view;
    }


    private void checkEvent() {

        if (GalleryTable.getLastPost().size() > 0)
        {
            Log.e(TAG,"updatedAt"+GalleryTable.getLastPost().get(0).updatedAt);
            if (MixOperations.isNewEventUpdate(LeafPreference.getInstance(getContext()).getString("GALLERY_POST"), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",GalleryTable.getLastPost().get(0)._now)) {
                GalleryTable.deleteGallery();
                getData();
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
     //   galleryAdapter.notifyDataSetChanged();
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
                        getLocally();
                    }
                }
            }
        });

        /*swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    currentPage = 1;
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    showNoNetworkMsg();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });*/


    }
    private void getData()
    {
        if(isConnectionAvailable())
        {
            //showLoadingBar(progressBar);
            progressBar.setVisibility(View.VISIBLE);
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
            GalleryTable.deleteGallery();
            isAddPost = true;
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
       // hideLoadingBar();
        progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_GALLERY_POST:
                GalleryPostRes res = (GalleryPostRes) response;
                AppLog.e(TAG, "Post Res ; " + new Gson().toJson(res.data));
               if  (res.data.size()==0)

            {
                txtEmpty.setVisibility(View.VISIBLE);
            }

                else
            {
                txtEmpty.setVisibility(View.GONE);
            }


                if (currentPage == 1) {
              //      PostDataItem.deleteGeneralPosts(mGroupId+"");
                    GalleryTable.deleteGallery();
                    listData.clear();

                   /* listData.addAll(res.data);
                    AppLog.e(TAG, "current page 1");*/

                } /*else {
                    listData.addAll(res.data);
                    AppLog.e(TAG, "current page " + currentPage);
                }
                listData.clear();*/

                /*if(res.data.size()==0)
                    txtEmpty.setVisibility(View.VISIBLE);
                else
                    txtEmpty.setVisibility(View.GONE);*/

                //galleryAdapter.notifyDataSetChanged();

                totalPages = res.totalNumberOfPages;

                LeafPreference.getInstance(getContext()).setString("GalleryTotalPage",String.valueOf(totalPages));
                mIsLoading = false;

                if(res.data.size()>0)
                    listData.addAll(res.data);
                    galleryAdapter.add(listData);
                    saveToLocallay(res.data);

                break;

            case LeafManager.API_GALLERY_DELETE:
                Toast.makeText(getContext(), getResources().getString(R.string.toast_post_delete_successfully), Toast.LENGTH_SHORT).show();
                currentPage=1;
                AmazoneRemove.remove(currentItem.fileName);
                GalleryTable.deleteGallery();
                getData();
                break;
        }
    }

    private void saveToLocallay(ArrayList<GalleryPostRes.GalleryData> data) {


        GalleryTable.deleteGallery(GroupDashboardActivityNew.groupId,currentPage);

        if (data.size()>0)
        {
            for (int i = 0;i<data.size();i++)
            {
                GalleryTable galleryTable = new GalleryTable();

                galleryTable.albumId = data.get(i).getAlbumId();
                galleryTable.albumName = data.get(i).albumName;
                galleryTable.updatedAt = data.get(i).updatedAt;
                galleryTable.groupId = data.get(i).getGroupId();
                galleryTable.fileType = data.get(i).fileType;
                galleryTable.video = data.get(i).video;
                galleryTable.thumbnail = data.get(i).thumbnail;
                galleryTable.fileName = new Gson().toJson(data.get(i).fileName);
                galleryTable.thumbnailImage = new Gson().toJson(data.get(i).thumbnailImage);
                galleryTable.createdAt = data.get(i).createdAt;
                galleryTable.canEdit = data.get(i).canEdit;

                if (!LeafPreference.getInstance(getContext()).getString("GALLERY_POST").isEmpty())
                {
                    galleryTable._now = LeafPreference.getInstance(getContext()).getString("GALLERY_POST");
                }
                else
                {
                    galleryTable._now = DateTimeHelper.getCurrentTime();
                }

                galleryTable.page = currentPage;

                galleryTable.save();
            }
        }

    }

    private void getLocally()
    {
        List<GalleryTable> galleryTableList = GalleryTable.getGallery(GroupDashboardActivityNew.groupId,currentPage);

        if (galleryTableList != null && galleryTableList.size() > 0)
        {
            for (int i = 0;i<galleryTableList.size();i++)
            {
                GalleryPostRes.GalleryData galleryData = new GalleryPostRes.GalleryData();
                galleryData.albumName = galleryTableList.get(i).albumName;
                galleryData.setAlbumId(galleryTableList.get(i).albumId);
                galleryData.setGroupId(galleryTableList.get(i).groupId);
                galleryData.canEdit = galleryTableList.get(i).canEdit;
                galleryData.updatedAt = galleryTableList.get(i).updatedAt;
                galleryData.fileType = galleryTableList.get(i).fileType;
                galleryData.video = galleryTableList.get(i).video;
                galleryData.thumbnail = galleryTableList.get(i).thumbnail;
                galleryData.fileName = new Gson().fromJson(galleryTableList.get(i).fileName,new TypeToken<ArrayList<String>>(){}.getType());
                galleryData.thumbnailImage = new Gson().fromJson(galleryTableList.get(i).thumbnailImage, new TypeToken<ArrayList<String>>(){}.getType());
                galleryData.createdAt = galleryTableList.get(i).createdAt;
                listData.add(galleryData);
            }

            galleryAdapter.add(listData);
        }
        else
        {
            getData();
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        // hideLoadingBar();
        progressBar.setVisibility(View.GONE);
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
    }

    @Override
    public void onException(int apiId, String msg) {
        // hideLoadingBar();
        progressBar.setVisibility(View.GONE);
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
        SMBDialogUtils.showSMBDialogOKCancel(getActivity(), getResources().getString(R.string.dialog_are_you_want_to_delete), this);
    }

    @Override
    public void onExternalShareClick(GalleryPostRes.GalleryData item) {
        boolean isDownloaded = true;

        if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {


            if (item.fileName.size()> 0)
            {
                for (int i = 0;i<item.fileName.size();i++)
                {
                    if (!AmazoneImageDownload.isImageDownloaded(getContext(),item.fileName.get(i)))
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

                        files.add(AmazoneImageDownload.getDownloadPath(getContext(),item.fileName.get(i)));
                    }

                   /* ArrayList<Uri> uris = new ArrayList<>();

                    for(File file: files){

                        AppLog.e(TAG, "URL "+file.getAbsolutePath());

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            uris.add(FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file));
                        } else {
                            uris.add(Uri.fromFile(file));
                        }

                    }*/

                    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    intent.setType("image/");
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

                   /* ArrayList<Uri> uris = new ArrayList<>();

                    for(File file: files){

                        AppLog.e(TAG, "URL "+file.getAbsolutePath());

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            uris.add(FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file));
                        } else {
                            uris.add(Uri.fromFile(file));
                        }

                    }*/

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
                    if (!AmazoneImageDownload.isImageDownloaded(getContext(),item.fileName.get(i)))
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

                        files.add(AmazoneImageDownload.getDownloadPath(getContext(),item.fileName.get(i)));
                    }

                  /*  ArrayList<Uri> uris = new ArrayList<>();

                    for(File file: files){

                        AppLog.e(TAG, "URL "+file.getAbsolutePath());

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            uris.add(FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file));
                        } else {
                            uris.add(Uri.fromFile(file));
                        }

                    }*/

                    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    intent.setType("image/");
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
            //showLoadingBar(progressBar);
            progressBar.setVisibility(View.VISIBLE);
            LeafManager manager = new LeafManager();
            manager.deleteGalleryPost(this, mGroupId+"",currentItem.getAlbumId());

        } else {
            showNoNetworkMsg();
        }

    }

    public void getChapters() {
        getData();
    }
}
