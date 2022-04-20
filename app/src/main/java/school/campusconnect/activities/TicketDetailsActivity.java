package school.campusconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.gson.Gson;
import com.scopely.fontain.views.FontTextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.Assymetric.AsymmetricRecyclerView;
import school.campusconnect.Assymetric.AsymmetricRecyclerViewAdapter;

import school.campusconnect.Assymetric.SpacesItemDecoration;
import school.campusconnect.Assymetric.Utils;
import school.campusconnect.R;

import school.campusconnect.adapters.BoothCordinatorAdapter;
import school.campusconnect.adapters.BoothInChargeAdapter;
import school.campusconnect.adapters.TicketDetailsCommentAdpater;
import school.campusconnect.adapters.TicketDetailsImageAdapter;

import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.comments.AddCommentTaskDetailsReq;
import school.campusconnect.datamodel.comments.CommentTaskDetailsRes;
import school.campusconnect.datamodel.ticket.TicketListResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneAudioDownload;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AppDialog;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.views.SMBDialogUtils;

public class TicketDetailsActivity extends BaseActivity implements View.OnClickListener, LeafManager.OnCommunicationListener, TicketDetailsImageAdapter.ListenerOnclick {

    public static String TAG = "TicketDetailsActivity";

    AmazoneAudioDownload asyncTask;

    @Bind(R.id.iconBack)
    public ImageView iconBack;

    @Bind(R.id.toolbar)
    public Toolbar toolbar;

    @Bind(R.id.tv_toolbar_title)
    public FontTextView tv_toolbar_title;

    @Bind(R.id.btnDeny)
    public Button btnDeny;

    @Bind(R.id.btnApprove)
    public Button btnApprove;

    @Bind(R.id.issueTitle)
    public TextView issueTitle;

    @Bind(R.id.tvDepartmentName)
    public TextView tvDepartmentName;

    @Bind(R.id.tvPartyName)
    public TextView tvPartyName;

    @Bind(R.id.tvDepartmentDesg)
    public TextView tvDepartmentDesg;

    @Bind(R.id.tvPartyDesg)
    public TextView tvPartyDesg;

    @Bind(R.id.callDepartmentName)
    public ImageView callDepartmentName;

    @Bind(R.id.callPartyName)
    public ImageView callPartyName;

    @Bind(R.id.tvAttachmentDecs)
    public TextView tvAttachmentDecs;

    @Bind(R.id.tvAttachmentLocation)
    public TextView tvAttachmentLocation;

    @Bind(R.id.tvAttachmentAddress)
    public TextView tvAttachmentAddress;

    @Bind(R.id.imgDropdown)
    public ImageView imgDropdown;

    @Bind(R.id.btnsendComment)
    public ImageView btnsendComment;

    @Bind(R.id.etComment)
    public EditText etComment;

    @Bind(R.id.llExpand)
    public LinearLayout llExpand;

    @Bind(R.id.llRedirectMap)
    public LinearLayout llRedirectMap;

    @Bind(R.id.llBoothCordinator)
    public LinearLayout llBoothCordinator;

    @Bind(R.id.llBoothInCharge)
    public LinearLayout llBoothInCharge;

    @Bind(R.id.llBtn)
    public LinearLayout llBtn;

    @Bind(R.id.rvimgAttachment)
    public AsymmetricRecyclerView rvimgAttachment;

    @Bind(R.id.llAudio)
    public RelativeLayout llAudio;

    @Bind(R.id.rvBoothCordinator)
    public RecyclerView rvBoothCordinator;

    @Bind(R.id.rvBoothInCharge)
    public RecyclerView rvBoothInCharge;

    @Bind(R.id.rvComment)
    public RecyclerView rvComment;

    @Bind(R.id.ProgressBar)
    public ProgressBar ProgressBar;

    @Bind(R.id.scrollView)
    public NestedScrollView scrollView;

    @Bind(R.id.imgDownloadAudio)
    ImageView imgDownloadAudio;

    @Bind(R.id.imgPlayAudio)
    ImageView imgPlayAudio;

    @Bind(R.id.imgPauseAudio)
    ImageView imgPauseAudio;

    @Bind(R.id.llProgress)
    FrameLayout llProgress;

    @Bind(R.id.progressBarAudioDownload)
    ProgressBar progressBarAudioDownload;

    @Bind(R.id.imgCancelDownloadAudio)
    ImageView imgCancelDownloadAudio;

    @Bind(R.id.tvTimeAudio)
    TextView tvTimeAudio;

    @Bind(R.id.seekBarAudio)
    SeekBar seekBarAudio;

    MediaPlayer mediaPlayer  = new MediaPlayer();
    private Handler mHandler = new Handler();

    Runnable myRunnable = new Runnable() {
        @Override
        public void run() {


            try{
                int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                Log.e(TAG,"mCurrentPosition"+ mCurrentPosition);
                tvTimeAudio.setText(formatDate(mCurrentPosition));
                seekBarAudio.setProgress(mCurrentPosition*10);
                mHandler.postDelayed(myRunnable, 1000);
            }catch (Exception e)
            {
                Log.e(TAG,"exception"+ e.getMessage());
            }

        }
    };

   // ActivityTicketDetailsBinding binding;
    private LeafManager manager;
    private TicketListResponse.TicketData taskData;
    private BoothCordinatorAdapter boothCordinatorAdapter;
    private BoothInChargeAdapter boothInChargeAdapter;
    private TicketDetailsCommentAdpater ticketDetailsCommentAdpater;
    private Boolean expandable = false;
    private String callDepartment,callParty;
    private String Option,Role,SelectedOption;
    private String Status;
    public static final String[] permissions = new String[]{Manifest.permission.CALL_PHONE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_details);
       // binding = DataBindingUtil.setContentView(this,R.layout.activity_ticket_details);
        inits();
        listner();
    }


    private void deleteTicket() {

        if (isConnectionAvailable()) {

            SMBDialogUtils.showSMBDialogOKCancel(this, getResources().getString(R.string.dialog_are_you_want_to_delete), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ProgressBar.setVisibility(View.VISIBLE);
                    manager.deleteTicket(TicketDetailsActivity.this, GroupDashboardActivityNew.groupId,taskData.getIssuePostId());
                }
            });
        }
        else
        {
            showNoNetworkMsg();
        }
    }


    private String formatDate(int second)  {

        String seconds , minutes;
        if(second>60)
        {
            if(second % 60 < 10)
                seconds = "0"+(second % 60);
            else
                seconds = ""+(second%60);

            if(second/60 < 10)
                minutes = "0"+second/60;
            else
                minutes = ""+second/60;
        }
        else
        {
            minutes = "00";
            if(second % 60 < 10)
                seconds = "0"+(second % 60);
            else
                seconds = ""+(second%60);
        }
        return minutes+":"+seconds;
    }

    private void listner() {

        btnDeny.setOnClickListener(this);
        btnApprove.setOnClickListener(this);
        callDepartmentName.setOnClickListener(this);
        callPartyName.setOnClickListener(this);
        imgDropdown.setOnClickListener(this);
        btnsendComment.setOnClickListener(this);
        llRedirectMap.setOnClickListener(this);

        imgPlayAudio.setOnClickListener(this);
        imgDownloadAudio.setOnClickListener(this);
        imgCancelDownloadAudio.setOnClickListener(this);
        imgPauseAudio.setOnClickListener(this);

    }

    private void inits() {

        ButterKnife.bind(this);

        manager = new LeafManager();

        tv_toolbar_title.setText(getResources().getString(R.string.lbl_tikit_details));

        toolbar.inflateMenu(R.menu.menu_delete_ticket);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.menuDeleteTicket) {
                    deleteTicket();
                    return true;
                }
                return false;
            }
        });

        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        boothCordinatorAdapter = new BoothCordinatorAdapter();
        boothInChargeAdapter = new BoothInChargeAdapter();
        ticketDetailsCommentAdpater = new TicketDetailsCommentAdpater();
        rvComment.setAdapter(ticketDetailsCommentAdpater);

        if (getIntent() != null)
        {
            taskData = (TicketListResponse.TicketData) getIntent().getSerializableExtra("data");
            Option = getIntent().getStringExtra("Option");
            SelectedOption = getIntent().getStringExtra("SelectedOption");
            Role = getIntent().getStringExtra("Role");

            Log.e(TAG,"Role"+ Role);

            if (Role.equalsIgnoreCase("isPartyTaskForce"))
            {
                if (Option != null)
                {
                    if (Option.equalsIgnoreCase("approved"))
                    {
                        btnApprove.setText("Not Approve");
                        btnDeny.setText("Deny");
                    }
                    else if (Option.equalsIgnoreCase("notApproved"))
                    {
                        btnApprove.setText("Approve");
                        btnDeny.setText("Deny");
                    }
                    else if (Option.equalsIgnoreCase("denied"))
                    {
                        btnApprove.setText("Approve");
                        btnDeny.setText("Not Approve");
                    }
                    else if (Option.equalsIgnoreCase("overDue"))
                    {
                        llBtn.setVisibility(View.GONE);
                    }
                }
            }
            else if (Role.equalsIgnoreCase("isDepartmentTaskForce"))
            {
                if (Option != null)
                {
                    if (Option.equalsIgnoreCase("open"))
                    {
                        btnApprove.setText("Close");
                        btnDeny.setText("Hold");
                    }
                    else if (Option.equalsIgnoreCase("hold"))
                    {
                        btnApprove.setText("Open");
                        btnDeny.setText("Close");
                    }
                    else if (Option.equalsIgnoreCase("close"))
                    {
                        btnApprove.setText("Open");
                        btnDeny.setText("Hold");
                    }
                    else if (Option.equalsIgnoreCase("overDue"))
                    {
                        llBtn.setVisibility(View.GONE);
                    }
                }
            }
            else if (Role.equalsIgnoreCase("isAdmin"))
            {
                if (Option != null)
                {
                    if (Option.equalsIgnoreCase("approved"))
                    {
                        btnApprove.setText("Not Approve");
                        btnDeny.setText("Deny");
                    }
                    else if (Option.equalsIgnoreCase("notApproved"))
                    {
                        btnApprove.setText("Approve");
                        btnDeny.setText("Deny");
                    }
                    else if (Option.equalsIgnoreCase("denied"))
                    {
                        btnApprove.setText("Approve");
                        btnDeny.setText("Not Approve");
                    }
                    else if (Option.equalsIgnoreCase("overDue"))
                    {
                        llBtn.setVisibility(View.GONE);
                    }
                }
            }
            else
            {
                llBtn.setVisibility(View.GONE);
            }

            if (taskData != null)
            {
                issueTitle.setText("Constituency Issue "+taskData.getConstituencyIssueJurisdiction());

                tvDepartmentName.setText(taskData.getConstituencyIssueDepartmentTaskForce().getName());
                tvDepartmentDesg.setText(taskData.getConstituencyIssueDepartmentTaskForce().getConstituencyDesignation());
                callDepartment = taskData.getConstituencyIssueDepartmentTaskForce().getPhone();

                tvPartyName.setText(taskData.getConstituencyIssuePartyTaskForce().getName());
                tvPartyDesg.setText(taskData.getConstituencyIssuePartyTaskForce().getConstituencyDesignation());
                callParty = taskData.getConstituencyIssuePartyTaskForce().getPhone();

                tvAttachmentDecs.setText(taskData.getIssueText());
                tvAttachmentLocation.setText(taskData.getIssueLocation().getLandmark());
                tvAttachmentAddress.setText(taskData.getIssueLocation().getAddress());


                if (taskData.getBoothCoordinators() != null && taskData.getBoothCoordinators().size() > 0)
                {
                    boothCordinatorAdapter.add(taskData.getBoothCoordinators());
                    rvBoothCordinator.setAdapter(boothCordinatorAdapter);
                }
                else
                {
                    llBoothCordinator.setVisibility(View.GONE);
                }

                if (taskData.getBoothIncharge() != null && taskData.getBoothIncharge().size() > 0)
                {
                    boothInChargeAdapter.add(taskData.getBoothIncharge());
                    rvBoothInCharge.setAdapter(boothInChargeAdapter);
                }
                else
                {
                    llBoothInCharge.setVisibility(View.GONE);
                }

                if (taskData.getFileName() != null &&  taskData.getFileName().size()> 0)
                {
                    if (taskData.getFileType().equals(Constants.FILE_TYPE_AUDIO))
                    {
                        if (AmazoneAudioDownload.isAudioDownloaded(taskData.getFileName().get(0))) {
                            imgDownloadAudio.setVisibility(View.GONE);
                            imgPlayAudio.setVisibility(View.VISIBLE);
                        } else {
                            imgDownloadAudio.setVisibility(View.VISIBLE);
                        }

                        rvimgAttachment.setVisibility(View.GONE);
                        llAudio.setVisibility(View.VISIBLE);

                    }
                    else
                    {
                        rvimgAttachment.setVisibility(View.VISIBLE);
                        llAudio.setVisibility(View.GONE);

                        TicketDetailsImageAdapter adapter;

                        rvimgAttachment.setRequestedHorizontalSpacing(Utils.dpToPx(getApplicationContext(), 3));
                        rvimgAttachment.addItemDecoration(
                                new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.padding_3dp)));
                        if(taskData.getFileName().size()==3)
                        {
                            adapter = new TicketDetailsImageAdapter(this,2, taskData.getFileName().size(), getApplicationContext(), taskData);
                        }
                        else
                        {
                            adapter = new TicketDetailsImageAdapter( this,Constants.MAX_IMAGE_NUM, taskData.getFileName().size(), getApplicationContext(), taskData);
                        }
                        rvimgAttachment.setAdapter(new AsymmetricRecyclerViewAdapter<>(getApplicationContext(), rvimgAttachment, adapter));

                    }
                }
                else
                {
                    rvimgAttachment.setVisibility(View.GONE);
                    llAudio.setVisibility(View.GONE);
                }
                getComment();

            }

            reqPermission();
        }

        seekBarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (progress == 100)
                {
                    mHandler.removeCallbacks(myRunnable);
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    imgPauseAudio.setVisibility(View.GONE);
                    imgPlayAudio.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mHandler.removeCallbacks(myRunnable);
            }
        });
    }


    public void reqPermission(){
        if (!hasPermission(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, 222);
        }
    }
    public boolean hasPermission(String[] permissions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 222)
        {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        ProgressBar.setVisibility(View.GONE);

        switch (apiId)
        {

            case LeafManager.API_DELETE_TICKET:
                Intent returnIntentv1 = getIntent();
                returnIntentv1.putExtra("Option",SelectedOption);
                setResult(Activity.RESULT_OK,returnIntentv1);
                finish();
                break;


            case LeafManager.APPROVED_TICKET:
                BaseResponse res1 = (BaseResponse) response;
                AppLog.e(TAG, "BaseResponse " + new Gson().toJson(res1));
                Intent returnIntent = getIntent();
                returnIntent.putExtra("Option",SelectedOption);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
                break;


            case LeafManager.LIST_COMMENT:
                CommentTaskDetailsRes res2 = (CommentTaskDetailsRes) response;
                AppLog.e(TAG, "CommentTaskDetailsRes " + new Gson().toJson(res2));
                ticketDetailsCommentAdpater.add(res2.getCommentData());

                if (res2.getCommentData().size()>0)
                {
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
                break;

            case LeafManager.ADD_COMMENT:
                etComment.setText("");
                hideKeyboard();
                BaseResponse res3 = (BaseResponse) response;
                getComment();
                break;

        }
    }


    @Override
    public void onFailure(int apiId, String msg) {
        ProgressBar.setVisibility(View.GONE);
        AppLog.e(TAG, "onFailure " + msg);
    }

    @Override
    public void onException(int apiId, String msg) {
        ProgressBar.setVisibility(View.GONE);
        AppLog.e(TAG, "onException " + msg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imgDropdown:
                setExpandable();
                break;

            case R.id.callDepartmentName:
                callDepName();
                break;

            case R.id.callPartyName:
                callPrtName();
                break;

            case R.id.llRedirectMap:
                redirectMap();
                break;

            case R.id.btnApprove:

                Log.e(TAG,"groupID"+GroupDashboardActivityNew.groupId);


                if (btnApprove.getText().toString().equalsIgnoreCase("Not Approve"))
                {
                    Status = "notApproved";
                }
                else if (btnApprove.getText().toString().equalsIgnoreCase("Approve"))
                {
                    Status = "approved";
                }
                else if (btnApprove.getText().toString().equalsIgnoreCase("Close"))
                {
                    Status = "close";
                }
                else if (btnApprove.getText().toString().equalsIgnoreCase("Open"))
                {
                    Status = "open";
                }
                AppDialog.showConfirmDialog(this, getResources().getString(R.string.dialog_are_you_sure)+Status+getResources().getString(R.string.dialog_ticket), new AppDialog.AppDialogListener() {
                    @Override
                    public void okPositiveClick(DialogInterface dialog) {
                        dialog.dismiss();
                        ProgressBar.setVisibility(View.VISIBLE);

                        if (Role.equalsIgnoreCase("isAdmin"))
                        {
                            manager.setApprovedTicketByAdmin(TicketDetailsActivity.this,GroupDashboardActivityNew.groupId,taskData.getIssuePostId(),Status);
                        }
                        else
                        {
                            manager.setApprovedTicket(TicketDetailsActivity.this,GroupDashboardActivityNew.groupId,taskData.getIssuePostId(),Status);
                        }

                    }
                    @Override
                    public void okCancelClick(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                });
                break;

            case R.id.btnDeny:

                Log.e(TAG,"groupID"+GroupDashboardActivityNew.groupId);

                if (btnDeny.getText().toString().equalsIgnoreCase("Deny"))
                {
                    Status = "denied";
                }
                if (btnDeny.getText().toString().equalsIgnoreCase("Not Approve"))
                {
                    Status = "notApproved";
                }
                else if (btnDeny.getText().toString().equalsIgnoreCase("Hold"))
                {
                    Status = "hold";
                }
                else if (btnDeny.getText().toString().equalsIgnoreCase("Close"))
                {
                    Status = "close";
                }


                AppDialog.showConfirmDialog(this, getResources().getString(R.string.dialog_are_you_sure)+Status+getResources().getString(R.string.dialog_ticket), new AppDialog.AppDialogListener() {
                    @Override
                    public void okPositiveClick(DialogInterface dialog) {
                        dialog.dismiss();
                        ProgressBar.setVisibility(View.VISIBLE);
                        if (Role.equalsIgnoreCase("isAdmin"))
                        {
                            manager.setApprovedTicketByAdmin(TicketDetailsActivity.this,GroupDashboardActivityNew.groupId,taskData.getIssuePostId(),Status);
                        }
                        else
                        {
                            manager.setApprovedTicket(TicketDetailsActivity.this,GroupDashboardActivityNew.groupId,taskData.getIssuePostId(),Status);
                        }
                    }

                    @Override
                    public void okCancelClick(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                });
                 break;

            case R.id.btnsendComment:
                if (isValid())
                {
                    sendComment();
                }
                break;

            case R.id.imgDownloadAudio:
                startDownload();
                break;

            case R.id.imgCancelDownloadAudio:
                cancelDownload();
                break;

            case R.id.imgPlayAudio:
                playAudio();
                break;

            case R.id.imgPauseAudio:
                pauseAudio();
                break;
        }
    }

    private void startDownload() {
        imgDownloadAudio.setVisibility(View.GONE);
        llProgress.setVisibility(View.VISIBLE);
        if (isConnectionAvailable()) {
            asyncTask = AmazoneAudioDownload.download(this,taskData.getFileName().get(0) , new AmazoneAudioDownload.AmazoneDownloadSingleListener() {
                @Override
                public void onDownload(File file) {
                  llProgress.setVisibility(View.GONE);
                  imgPlayAudio.setVisibility(View.VISIBLE);
                }

                @Override
                public void error(String msg) {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_error_in_download)+msg,Toast.LENGTH_SHORT).show();
                    imgDownloadAudio.setVisibility(View.VISIBLE);
                    llProgress.setVisibility(View.GONE);
                }

                @Override
                public void progressUpdate(int progress, int max) {

                   progressBarAudioDownload.setProgress(progress);

                }
            });
        } else {
            showNoNetworkMsg();
        }
    }
    private void cancelDownload() {
        imgDownloadAudio.setVisibility(View.VISIBLE);
        llProgress.setVisibility(View.GONE);
        asyncTask.cancel(true);
    }
    private void playAudio() {

        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying())
            {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.setDataSource(Constants.decodeUrlToBase64(taskData.getFileName().get(0)));
                mediaPlayer.prepare();
                mediaPlayer.start();
                imgPlayAudio.setVisibility(View.GONE);
                imgPauseAudio.setVisibility(View.VISIBLE);

                runOnUiThread(myRunnable);
            }
            else
            {
                mediaPlayer.setDataSource(Constants.decodeUrlToBase64(taskData.getFileName().get(0)));
                mediaPlayer.prepare();
                mediaPlayer.start();

                imgPauseAudio.setVisibility(View.VISIBLE);
                imgPlayAudio.setVisibility(View.GONE);
                runOnUiThread(myRunnable);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
    private void pauseAudio() {

        mHandler.removeCallbacks(myRunnable);
        mediaPlayer.stop();
        mediaPlayer.reset();
        imgPauseAudio.setVisibility(View.GONE);
        imgPlayAudio.setVisibility(View.VISIBLE);
    }

    private Boolean isValid()
    {
        if (etComment.getText().toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_add_comment),Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void sendComment()
    {
        AddCommentTaskDetailsReq req = new AddCommentTaskDetailsReq();
        req.setText(etComment.getText().toString());
        ProgressBar.setVisibility(View.VISIBLE);
        manager.setAddCommentTaskDetails(this,GroupDashboardActivityNew.groupId,taskData.getIssuePostId(),req);
    }
    private void getComment()
    {
        ProgressBar.setVisibility(View.VISIBLE);
        manager.getCommentTaskDetails(this,GroupDashboardActivityNew.groupId,taskData.getIssuePostId());
    }
    private void redirectMap()
    {
        String map = "http://maps.google.co.in/maps?q=" + taskData.getIssueLocation().getAddress();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
        startActivity(intent);
    }
    private void callDepName()
    {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + callDepartment));
        startActivity(intent);
    }
    private void callPrtName()
    {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + callParty));
        startActivity(intent);
    }
    private void setExpandable() {

        if (expandable)
        {
            expandable = false;
            llExpand.setVisibility(View.GONE);
            imgDropdown.setRotation(360);
        }else
        {
            expandable = true;
            llExpand.setVisibility(View.VISIBLE);
            imgDropdown.setRotation(180);
        }

    }

    @Override
    public void onBackPressed() {


        Intent returnIntent = getIntent();
        returnIntent.putExtra("Option",SelectedOption);
        setResult(Activity.RESULT_CANCELED,returnIntent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mHandler.removeCallbacks(myRunnable);
    }

    @Override
    public void onClick(TicketListResponse.TicketData item, ArrayList<String> allImageList) {
        if(item.getFileType().equals(Constants.FILE_TYPE_IMAGE)){
            if (allImageList.size() == 1){
                Intent i = new Intent(this, FullScreenActivity.class);
                i.putExtra("image", item.getFileName().get(0));
                this.startActivity(i);
            } else {
                Intent i = new Intent(this, FullScreenMultiActivity.class);
                i.putStringArrayListExtra("image_list", item.getFileName());
                this.startActivity(i);
            }
        }/*else {
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
        }*/
    }
}