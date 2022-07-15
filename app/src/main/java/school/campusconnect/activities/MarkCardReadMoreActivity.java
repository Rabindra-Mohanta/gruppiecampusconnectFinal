package school.campusconnect.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.Assymetric.AsymmetricRecyclerView;
import school.campusconnect.R;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.MarkSheetListResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.views.SMBDialogUtils;

public class MarkCardReadMoreActivity extends BaseActivity implements DialogInterface.OnClickListener {


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

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.recyclerView)
    AsymmetricRecyclerView recyclerView;

    MarkSheetListResponse.MarkSheetData item;
    private String TAG="VendorReadMoreActivity";
    private MarkSheetListResponse.MarkSheetData currentItem;
    private String mGroupId;
    private String teamId;
    private String userId;
    private String rollNo;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markcard_read_more);

        init();

      //  bindData();
    }
/*
    private void bindData() {
        recyclerView.setRequestedHorizontalSpacing(Utils.dpToPx(this, 3));
        recyclerView.addItemDecoration(
                new SpacesItemDecoration(this.getResources().getDimensionPixelSize(R.dimen.padding_3dp)));

        txt_title.setText(item.title);

        if(!TextUtils.isEmpty(item.fileType))
        {
            if(item.fileType.equals(Constants.FILE_TYPE_IMAGE))
            {
                if (item.fileName!=null) {
               *//* int height=0;
                if (item.imageHeight != 0 && item.imageWidth != 0)
                    height = (Constants.screen_width * item.imageHeight) / item.imageWidth;*//*

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
                Picasso.with(this).load(R.drawable.pdf_thumbnail).into(imgPhoto);
                imgPlay.setVisibility(View.GONE);
                imgPhoto.setVisibility(View.VISIBLE);

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                imgPhoto.setLayoutParams(lp);
                imgPhoto.requestLayout();

                recyclerView.setVisibility(View.GONE);
            }
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

        if("teacher".equals(role) || "admin".equals(role)){
           txt_drop_delete.setVisibility(View.VISIBLE);
           iv_delete.setVisibility(View.VISIBLE);
        } else {
           txt_drop_delete.setVisibility(View.GONE);
           iv_delete.setVisibility(View.GONE);
        }

    }*/

    private void init() {
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        Bundle bundle = getIntent().getExtras();
        mGroupId = bundle.getString("group_id");
        teamId = bundle.getString("team_id");
        userId = bundle.getString("user_id");
        rollNo = bundle.getString("roll_no");
        role = bundle.getString("role");

        setTitle(bundle.getString("name")+" "+getResources().getString(R.string.lbl_select_mark_card));

        String jsonData=bundle.getString("data");
        AppLog.e(TAG,"data : "+jsonData);
        item=new Gson().fromJson(jsonData, MarkSheetListResponse.MarkSheetData.class);
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

    public void onPostClick(MarkSheetListResponse.MarkSheetData item) {
       /* if(TextUtils.isEmpty(item.fileType))
        {
            return;
        }
      *//*  if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {

            Intent browserIntent = new Intent(getActivity(), TestActivity.class);
            browserIntent.putExtra("url", item.video);
            startActivity(browserIntent);

        } else*//* if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
            Intent i = new Intent(this, ViewPDFActivity.class);
            i.putExtra("pdf", item.fileName.get(0));
            i.putExtra("name", item.title);
            startActivity(i);

        } else if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
            Intent i = new Intent(this, FullScreenMultiActivity.class);
            i.putExtra("image_list", item.fileName);
            startActivity(i);
        }*/
    }

    public void onDeleteClick(MarkSheetListResponse.MarkSheetData item) {
        currentItem = item;
        SMBDialogUtils.showSMBDialogOKCancel(this, getResources().getString(R.string.dialog_are_you_want_to_delete), this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        AppLog.e("TeamPostFrag", "DIalog Ok Clicked ");
        if (isConnectionAvailable()) {
            showLoadingBar(progressBar);
            //progressBar.setVisibility(View.VISIBLE);
            LeafManager manager = new LeafManager();
            //manager.deleteMarkCart(this, mGroupId+"",teamId,userId,rollNo,currentItem.marksCardId);

        } else {
            showNoNetworkMsg();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    @Override
    public void onSuccess(int apiId, BaseResponse response) {
      //  progressBar.setVisibility(View.GONE);
        hideLoadingBar();
        switch (apiId) {
            case LeafManager.API_MARK_SHEET_DELETE:
                Toast.makeText(this, getResources().getString(R.string.toast_mark_card_delete_successfully), Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
       // progressBar.setVisibility(View.GONE);
        hideLoadingBar();
        if (msg.contains("401:Unauthorized") || msg.contains("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else if (msg.contains("404")) {
            Toast.makeText(this, getResources().getString(R.string.toast_no_post), Toast.LENGTH_SHORT).show();
        } else if (msg.contains("418")) {
            if (apiId == LeafManager.API_REPORT)
                Toast.makeText(this, getResources().getString(R.string.toast_already_reported), Toast.LENGTH_SHORT).show();
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
