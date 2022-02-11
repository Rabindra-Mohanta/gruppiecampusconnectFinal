package school.campusconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;

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
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;

public class TicketDetailsActivity extends BaseActivity implements View.OnClickListener, LeafManager.OnCommunicationListener {

    public static String TAG = "TicketDetailsActivity";


    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

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

   // ActivityTicketDetailsBinding binding;
    private LeafManager manager;
    private TicketListResponse.TicketData taskData;
    private BoothCordinatorAdapter boothCordinatorAdapter;
    private BoothInChargeAdapter boothInChargeAdapter;
    private TicketDetailsCommentAdpater ticketDetailsCommentAdpater;
    private Boolean expandable = false;
    private String callDepartment,callParty;
    private String Option,Role;

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

    private void listner() {

        btnDeny.setOnClickListener(this);
        btnApprove.setOnClickListener(this);
        callDepartmentName.setOnClickListener(this);
        callPartyName.setOnClickListener(this);
        imgDropdown.setOnClickListener(this);
        btnsendComment.setOnClickListener(this);
        llRedirectMap.setOnClickListener(this);
    }

    private void inits() {

        ButterKnife.bind(this);

        manager = new LeafManager();

        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_tikit_details));
        boothCordinatorAdapter = new BoothCordinatorAdapter();
        boothInChargeAdapter = new BoothInChargeAdapter();
        ticketDetailsCommentAdpater = new TicketDetailsCommentAdpater();
        rvComment.setAdapter(ticketDetailsCommentAdpater);

        if (getIntent() != null)
        {
            taskData = (TicketListResponse.TicketData) getIntent().getSerializableExtra("data");
            Option = getIntent().getStringExtra("Option");
            Role = getIntent().getStringExtra("Role");

            Log.e(TAG,"get Issue Text"+taskData.getIssueText());

          /*  if (Role.equalsIgnoreCase("isBoothCoordinator"))
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
                }
            }
            else
            {
                llBtn.setVisibility(View.GONE);
            }*/

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
                    TicketDetailsImageAdapter adapter;

                    rvimgAttachment.setRequestedHorizontalSpacing(Utils.dpToPx(getApplicationContext(), 3));
                    rvimgAttachment.addItemDecoration(
                            new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.padding_3dp)));
                    if(taskData.getFileName().size()==3)
                    {
                        adapter = new TicketDetailsImageAdapter(2, taskData.getFileName().size(), getApplicationContext(), taskData);
                    }
                    else
                    {
                        adapter = new TicketDetailsImageAdapter( Constants.MAX_IMAGE_NUM, taskData.getFileName().size(), getApplicationContext(), taskData);
                    }
                    rvimgAttachment.setAdapter(new AsymmetricRecyclerViewAdapter<>(getApplicationContext(), rvimgAttachment, adapter));
                }
                else
                {
                    rvimgAttachment.setVisibility(View.GONE);
                }
                getComment();

            }

            reqPermission();
        }
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
            case LeafManager.APPROVED_TICKET:
                BaseResponse res1 = (BaseResponse) response;
                AppLog.e(TAG, "BaseResponse " + new Gson().toJson(res1));
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

              /*  AppDialog.showConfirmDialog(this, "Are You Sure You Want to Approve Ticket ?", new AppDialog.AppDialogListener() {
                    @Override
                    public void okPositiveClick(DialogInterface dialog) {
                        dialog.dismiss();
                        ProgressBar.setVisibility(View.VISIBLE);
                        manager.setApprovedTicket(TicketDetailsActivity.this,GroupDashboardActivityNew.groupId,taskData.getIssuePostId(),"approved");
                    }
                    @Override
                    public void okCancelClick(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                });*/
                break;

            case R.id.btnDeny:

                Log.e(TAG,"groupID"+GroupDashboardActivityNew.groupId);

              /*  AppDialog.showConfirmDialog(this, "Are You Sure You Want to Denied Ticket ?", new AppDialog.AppDialogListener() {
                    @Override
                    public void okPositiveClick(DialogInterface dialog) {
                        dialog.dismiss();
                        ProgressBar.setVisibility(View.VISIBLE);
                        manager.setApprovedTicket(TicketDetailsActivity.this,GroupDashboardActivityNew.groupId,taskData.getIssuePostId(),"denied");
                    }

                    @Override
                    public void okCancelClick(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                });*/
                 break;

            case R.id.btnsendComment:
                if (isValid())
                {
                    sendComment();
                }
                break;
        }
    }
    private Boolean isValid()
    {
        if (etComment.getText().toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Add Comment",Toast.LENGTH_SHORT).show();
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
}