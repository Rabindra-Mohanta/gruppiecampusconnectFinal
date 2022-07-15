package school.campusconnect.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.Assymetric.AsymmetricRecyclerView;
import school.campusconnect.Assymetric.AsymmetricRecyclerViewAdapter;
import school.campusconnect.Assymetric.SpacesItemDecoration;
import school.campusconnect.Assymetric.Utils;
import school.campusconnect.Assymetric.multiimages.ItemImage;
import school.campusconnect.R;
import school.campusconnect.adapters.GalleryReadMoreAdapter;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.gallery.GalleryPostRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneRemove;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.MixOperations;
import school.campusconnect.views.SMBDialogUtils;

public class GalleryDetailActivity extends BaseActivity implements DialogInterface.OnClickListener, GalleryReadMoreAdapter.GalleryImageListener {


    private static final String TAG = "GalleryDetailActivity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.txt_date)
    TextView txtDate;

    @Bind(R.id.iv_delete)
    ImageView iv_delete;

    @Bind(R.id.txt_title)
    TextView txt_title;

    @Bind(R.id.img_play)
    ImageView imgPlay;

    @Bind(R.id.image)
    ImageView imgPhoto;

    @Bind(R.id.txt_drop_delete)
    TextView txt_drop_delete;

    @Bind(R.id.lin_drop)
    LinearLayout lin_drop;

    @Bind(R.id.cardView)
    CardView cardView;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.recyclerView)
    AsymmetricRecyclerView recyclerView;

    GalleryPostRes.GalleryData item;
    private String itemFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_detail);

        init();



    }

    @Override
    protected void onStart() {
        super.onStart();
        setData();
    }

    private void setData() {
        txt_title.setText(item.getAlbumName());
        txtDate.setText(MixOperations.getFormattedDate(item.getCreatedAt(), Constants.DATE_FORMAT));

        if (!TextUtils.isEmpty(item.fileType)) {
            if (item.fileType.equals(Constants.FILE_TYPE_IMAGE) || item.fileType.equals(Constants.FILE_TYPE_VIDEO)) {
                if (item.fileName != null) {
                    GalleryReadMoreAdapter adapter = new GalleryReadMoreAdapter(item.fileName.size(), item.fileName.size(), this, item);
                    adapter.setListener(this);
                    recyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(this, recyclerView, adapter));
                    recyclerView.setVisibility(View.VISIBLE);
                }
                imgPlay.setVisibility(View.GONE);
                imgPhoto.setVisibility(View.GONE);
            }else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
                Picasso.with(this).load(R.drawable.pdf_thumbnail).into(imgPhoto);
                imgPlay.setVisibility(View.GONE);
                imgPhoto.setVisibility(View.VISIBLE);

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                imgPhoto.setLayoutParams(lp);
                imgPhoto.requestLayout();

                recyclerView.setVisibility(View.GONE);
            } else if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
                int height = (Constants.screen_width * 204) / 480;
                imgPhoto.getLayoutParams().height = height;
                imgPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Picasso.with(this).load(item.thumbnail).into(imgPhoto);
                imgPhoto.setVisibility(View.VISIBLE);
                imgPlay.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                imgPhoto.setVisibility(View.GONE);
                imgPlay.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }
        } else {
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_add_post).setIcon(R.drawable.ic_post_new);
        if (item.canEdit)
            menu.findItem(R.id.menu_add_post).setVisible(true);
        else
            menu.findItem(R.id.menu_add_post).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_post:
                Intent intent = new Intent(this, AddGalleryPostActivity.class);
                intent.putExtra("isEdit", true);
                intent.putExtra("album_id", this.item.getAlbumId());
                intent.putExtra("type", this.item.getFileType());
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        item = new Gson().fromJson(getIntent().getStringExtra("data"), GalleryPostRes.GalleryData.class);
        AppLog.e(TAG, "item : " + item);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_gallery));

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lin_drop.getVisibility() == View.VISIBLE)
                    lin_drop.setVisibility(View.GONE);
            }
        });
        recyclerView.setRequestedHorizontalSpacing(Utils.dpToPx(this, 3));
        recyclerView.addItemDecoration(
                new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.padding_3dp)));
    }

    @OnClick({R.id.iv_delete, R.id.txt_drop_delete, R.id.rel})
    public void onClick(View view) {
        switch (view.getId()) {
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
        }

    }

    public void onPostClick(GalleryPostRes.GalleryData item) {
        if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
            Intent browserIntent = new Intent(this, TestActivity.class);
            browserIntent.putExtra("url", item.video);
            startActivity(browserIntent);

        } else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
            Intent i = new Intent(this, ViewPDFActivity.class);
            i.putExtra("pdf", item.fileName.get(0));
            i.putExtra("thumbnail", item.thumbnailImage.get(0));
            i.putExtra("name", item.albumName);
            startActivity(i);

        } else if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
            Intent i = new Intent(this, FullScreenActivity.class);
            i.putExtra("image", item.fileName);
            startActivity(i);
        }
    }

    public void onDeleteClick(GalleryPostRes.GalleryData item) {
        SMBDialogUtils.showSMBDialogOKCancel(this, getResources().getString(R.string.dialog_are_you_want_to_delete), this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        AppLog.e("TeamPostFrag", "DIalog Ok Clicked ");
        if (isConnectionAvailable()) {
            showLoadingBar(progressBar);
           // progressBar.setVisibility(View.VISIBLE);
            LeafManager manager = new LeafManager();
            manager.deleteGalleryPost(this, GroupDashboardActivityNew.groupId + "", item.getAlbumId());

        } else {
            showNoNetworkMsg();
        }

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        if (progressBar != null)
            hideLoadingBar();
       //     progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_GALLERY_DELETE:
                LeafPreference.getInstance(this).setBoolean(LeafPreference.ISGALLERY_POST_UPDATED, true);
                Toast.makeText(this, getResources().getString(R.string.toast_post_delete_successfully), Toast.LENGTH_SHORT).show();
                AmazoneRemove.remove(item.fileName);
                finish();
                break;
            case LeafManager.API_GALLERY_FILE_DELETE:
                LeafPreference.getInstance(this).setBoolean(LeafPreference.ISGALLERY_POST_UPDATED, true);
                Toast.makeText(this, getResources().getString(R.string.toast_file_delete_successfully), Toast.LENGTH_SHORT).show();
                AmazoneRemove.remove(itemFile);
                finish();
                break;

        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        super.onFailure(apiId, msg);
        if (progressBar != null)
            hideLoadingBar();
         //   progressBar.setVisibility(View.GONE);

        if (msg.contains("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onException(int apiId, String msg) {
        super.onException(apiId, msg);
        if (progressBar != null)
            hideLoadingBar();
          //  progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onItemClick(ArrayList<String> allImageList,ArrayList<String> thumbnailImages) {
        if(item.fileType.equals(Constants.FILE_TYPE_IMAGE)){
            if (allImageList.size() == 1){
                Intent i = new Intent(this, FullScreenActivity.class);
                i.putExtra("image", allImageList.get(0));
                i.putExtra("album_id", this.item.getAlbumId());
                i.putExtra("type", this.item.getFileType());
                i.putExtra("edit",this.item.canEdit);
                this.startActivity(i);
            } else {
                Intent i = new Intent(this, FullScreenMultiActivity.class);
                i.putStringArrayListExtra("image_list", allImageList);
                i.putExtra("album_id", this.item.getAlbumId());
                i.putExtra("type", this.item.getFileType());
                i.putExtra("edit",this.item.canEdit);
                this.startActivity(i);
            }
        }else {
            if (allImageList.size() == 1){
                Intent i = new Intent(this, VideoPlayActivity.class);
                i.putExtra("video", allImageList.get(0));
                i.putExtra("thumbnail", thumbnailImages.get(0));
                this.startActivity(i);
            } else {
                Intent i = new Intent(this, FullScreenVideoMultiActivity.class);
                i.putStringArrayListExtra("video_list", allImageList);
                i.putStringArrayListExtra("thumbnailImages", thumbnailImages);
                this.startActivity(i);
            }
        }
    }

    @Override
    public void onItemLongClick(final ItemImage itemImage) {
        this.itemFile = itemImage.getImagePath();
        if(item.canEdit){
            SMBDialogUtils.showSMBDialogOKCancel(this, getResources().getString(R.string.dialog_are_you_want_to_delete), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (isConnectionAvailable()) {
                        showLoadingBar(progressBar);
                       // progressBar.setVisibility(View.VISIBLE);
                        LeafManager manager = new LeafManager();
                        manager.deleteGalleryFile(GalleryDetailActivity.this, GroupDashboardActivityNew.groupId + "", item.getAlbumId(), itemImage.getImagePath());
                    } else {
                        showNoNetworkMsg();
                    }
                }
            });
        }

    }
}
