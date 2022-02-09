package school.campusconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import school.campusconnect.adapters.TicketCommentAdpater;
import school.campusconnect.adapters.TicketDetailsImageAdapter;

import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.GroupDetailResponse;
import school.campusconnect.datamodel.ticket.TicketListResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppDialog;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;

public class TicketDetailsActivity extends BaseActivity implements View.OnClickListener, LeafManager.OnCommunicationListener {
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

    @Bind(R.id.llExpand)
    public LinearLayout llExpand;

    @Bind(R.id.llRedirectMap)
    public LinearLayout llRedirectMap;

    @Bind(R.id.llBoothCordinator)
    public LinearLayout llBoothCordinator;

    @Bind(R.id.rvimgAttachment)
    public AsymmetricRecyclerView rvimgAttachment;

    @Bind(R.id.rvBoothCordinator)
    public RecyclerView rvBoothCordinator;

    @Bind(R.id.rvComment)
    public RecyclerView rvComment;

    @Bind(R.id.ProgressBar)
    public ProgressBar ProgressBar;

    public static String TAG = "TicketDetailsActivity";
   // ActivityTicketDetailsBinding binding;

    private LeafManager manager;
    private TicketListResponse.TicketData taskData;
    private BoothCordinatorAdapter boothCordinatorAdapter;
    private Boolean expandable = false;
    private String callDepartment,callParty;
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
        if (getIntent() != null)
        {
            taskData = (TicketListResponse.TicketData) getIntent().getSerializableExtra("data");
            Log.e(TAG,"get Issue Text"+taskData.getIssueText());
            rvComment.setAdapter(new TicketCommentAdpater());

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
                if (taskData.getFileName() != null &&  taskData.getFileName().size()> 0)
                {
                    TicketDetailsImageAdapter adapter;

                    rvimgAttachment.setVisibility(View.VISIBLE);
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

                AppDialog.showConfirmDialog(this, "Are You Sure You Want to Approve Ticket ?", new AppDialog.AppDialogListener() {
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
                });
                break;

            case R.id.btnDeny:

                Log.e(TAG,"groupID"+GroupDashboardActivityNew.groupId);

                AppDialog.showConfirmDialog(this, "Are You Sure You Want to Denied Ticket ?", new AppDialog.AppDialogListener() {
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
                });
                 break;
        }
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