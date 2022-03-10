package school.campusconnect.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.Assymetric.AsymmetricRecyclerView;
import school.campusconnect.Assymetric.AsymmetricRecyclerViewAdapter;
import school.campusconnect.Assymetric.SpacesItemDecoration;
import school.campusconnect.Assymetric.Utils;
import school.campusconnect.R;
import school.campusconnect.adapters.ChildAdapter;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.VendorPostResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AmazoneRemove;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.MixOperations;
import school.campusconnect.views.SMBDialogUtils;

public class VendorReadMoreActivity extends BaseActivity implements DialogInterface.OnClickListener {


    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.txt_date)
    TextView txtDate;

    @Bind(R.id.iv_delete)
    ImageView iv_delete;

    @Bind(R.id.txt_title)
    TextView txt_title;

    @Bind(R.id.txt_content)
    TextView txt_content;

    @Bind(R.id.img_play)
    ImageView imgPlay;

    @Bind(R.id.image)
    ImageView imgPhoto;

    @Bind(R.id.txt_drop_delete)
    TextView txt_drop_delete;

    @Bind(R.id.lin_drop)
    LinearLayout lin_drop;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.recyclerView)
    AsymmetricRecyclerView recyclerView;

    @Bind(R.id.imgDownloadPdf)
    ImageView imgDownloadPdf;

    VendorPostResponse.VendorPostData item;
    private String TAG="VendorReadMoreActivity";
    private VendorPostResponse.VendorPostData currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_read_more);

        init();


    }

    @Override
    protected void onStart() {
        super.onStart();
        bindData();
    }

    private void bindData() {
        recyclerView.setRequestedHorizontalSpacing(Utils.dpToPx(this, 3));
        recyclerView.addItemDecoration(
                new SpacesItemDecoration(this.getResources().getDimensionPixelSize(R.dimen.padding_3dp)));

        txt_title.setText(item.vendor);

        txt_content.setText(item.description);

        if(TextUtils.isEmpty(item.description))
        {
            txt_content.setVisibility(View.GONE);
        }
        else
        {
            txt_content.setVisibility(View.VISIBLE);
        }

        txtDate.setText(MixOperations.getFormattedDate(item.createdAt, Constants.DATE_FORMAT));

        if(!TextUtils.isEmpty(item.fileType))
        {
            if(item.fileType.equals(Constants.FILE_TYPE_IMAGE))
            {
                if (item.fileName!=null) {
               /* int height=0;
                if (item.imageHeight != 0 && item.imageWidth != 0)
                    height = (Constants.screen_width * item.imageHeight) / item.imageWidth;*/

                    ChildAdapter adapter;
                    if(item.fileName.size()==3)
                    {
                        adapter = new ChildAdapter(2, item.fileName.size(), this, item.fileName);
                    }
                    else
                    {
                        adapter = new ChildAdapter( Constants.MAX_IMAGE_NUM, item.fileName.size(), this, item.fileName);
                    }
                    recyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(this,recyclerView, adapter));
                    recyclerView.setVisibility(View.VISIBLE);
                }
                imgPlay.setVisibility(View.GONE);
                imgPhoto.setVisibility(View.GONE);
            }
            else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
                imgPlay.setVisibility(View.GONE);
                imgPhoto.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                if(item.thumbnailImage!=null && item.thumbnailImage.size()>0)
                {
                    Picasso.with(this).load(Constants.decodeUrlToBase64(item.thumbnailImage.get(0))).into(imgPhoto);
                }
                if (item.fileName != null && item.fileName.size() > 0) {
                    if (AmazoneDownload.isPdfDownloaded(item.fileName.get(0))) {
                        imgDownloadPdf.setVisibility(View.GONE);
                    } else {
                        imgDownloadPdf.setVisibility(View.VISIBLE);
                    }
                }
            }
            /*else if(item.fileType.equals(Constants.FILE_TYPE_YOUTUBE))
            {
                int height = (Constants.screen_width * 204) / 480;
                holder.imgPhoto.getLayoutParams().height = height;
                holder.imgPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Picasso.with(mContext).load(item.thumbnail).into(holder.imgPhoto);
                holder.imgPhoto.setVisibility(View.VISIBLE);
                holder.imgPlay.setVisibility(View.VISIBLE);
                holder.recyclerView.setVisibility(View.GONE);
            }*/
            else {
                imgPhoto.setVisibility(View.GONE);
                imgPlay.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }
        }
        else
        {
            imgPhoto.setVisibility(View.GONE);
            imgPlay.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }

        if (item.canEdit) {
           txt_drop_delete.setVisibility(View.VISIBLE);
           iv_delete.setVisibility(View.VISIBLE);
        } else {
           txt_drop_delete.setVisibility(View.GONE);
           iv_delete.setVisibility(View.GONE);
        }

    }

    private void init() {
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_vendor_connect));

        String jsonData=getIntent().getStringExtra("data");
        AppLog.e(TAG,"data : "+jsonData);
        item=new Gson().fromJson(jsonData,VendorPostResponse.VendorPostData.class);
    }

    @OnClick({R.id.iv_delete,R.id.txt_drop_delete,R.id.rel,R.id.fragment_container})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.rel:
                if (lin_drop.getVisibility() == View.VISIBLE)
                    lin_drop.setVisibility(View.GONE);
                onPostClick(item);
                break;

            case R.id.iv_delete:
                if (lin_drop.getVisibility() == View.VISIBLE)
                    lin_drop.setVisibility(View.GONE);
                else
                    lin_drop.setVisibility(View.VISIBLE);
                break;

            case R.id.txt_drop_delete:
                lin_drop.setVisibility(View.GONE);
                onDeleteClick(item);
                break;
            case R.id.fragment_container:
                if (lin_drop.getVisibility() == View.VISIBLE)
                    lin_drop.setVisibility(View.GONE);
                break;
        }

    }

    public void onPostClick(VendorPostResponse.VendorPostData item) {
        if(TextUtils.isEmpty(item.fileType))
        {
            return;
        }
      /*  if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {

            Intent browserIntent = new Intent(getActivity(), TestActivity.class);
            browserIntent.putExtra("url", item.video);
            startActivity(browserIntent);

        } else*/ if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
            Intent i = new Intent(this, ViewPDFActivity.class);
            i.putExtra("pdf", item.fileName.get(0));
            i.putExtra("thumbnail", item.thumbnailImage.get(0));
            i.putExtra("name", item.vendor);
            startActivity(i);

        } else if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
            Intent i = new Intent(this, FullScreenActivity.class);
            i.putExtra("image", item.fileName);
            startActivity(i);
        }
    }

    public void onDeleteClick(VendorPostResponse.VendorPostData item) {
        currentItem = item;
        SMBDialogUtils.showSMBDialogOKCancel(this, "Are You Sure Want To Delete ?", this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        AppLog.e("TeamPostFrag", "DIalog Ok Clicked ");
        if (isConnectionAvailable()) {
            progressBar.setVisibility(View.VISIBLE);
            LeafManager manager = new LeafManager();
            manager.deleteVendorPost(this, GroupDashboardActivityNew.groupId+"",currentItem.vendorId);
        } else {
            showNoNetworkMsg();
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_VENDOR_DELETE:
                Toast.makeText(this, "Post Deleted Successfully", Toast.LENGTH_SHORT).show();
                LeafPreference.getInstance(this).setBoolean(LeafPreference.IS_VENDOR_POST_UPDATED, true);
                AmazoneRemove.remove(item.fileName);
                finish();
                break;
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);

        if (msg.contains("401:Unauthorized") || msg.contains("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else if (msg.contains("404")) {
            Toast.makeText(this, "No posts available.", Toast.LENGTH_SHORT).show();
        } else if (msg.contains("418")) {
            if (apiId == LeafManager.API_REPORT)
                Toast.makeText(this, "You have already reported this post", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onException(int apiId, String msg) {
            Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }
}
