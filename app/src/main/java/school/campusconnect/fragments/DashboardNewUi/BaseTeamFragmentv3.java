package school.campusconnect.fragments.DashboardNewUi;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.mobileconnectors.s3.transferutility.UploadOptions;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;

import id.zelory.compressor.Compressor;
import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.activities.AddChapterPostActivity;
import school.campusconnect.activities.AddTicketActivity;
import school.campusconnect.activities.CalendarActivity;
import school.campusconnect.activities.ChangeLanguageActivity;
import school.campusconnect.activities.ChangeNumberActivity;
import school.campusconnect.activities.ChangePasswordActivity;
import school.campusconnect.activities.ChangePinActivity;
import school.campusconnect.activities.CreateTeamActivity;
import school.campusconnect.activities.FamilyMemberActivity;
import school.campusconnect.activities.GalleryActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.IssueActivity;
import school.campusconnect.activities.NotificationListActivity;
import school.campusconnect.activities.ProfileActivity2;
import school.campusconnect.activities.ProfileConstituencyActivity;
import school.campusconnect.activities.ReadMoreActivity;
import school.campusconnect.activities.RemoveBannerActivity;
import school.campusconnect.adapters.FeedAdapter;
import school.campusconnect.adapters.TeamListAdapterNewV2;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.FragmentBaseTeamFragmentv2Binding;
import school.campusconnect.databinding.FragmentBaseTeamFragmentv3Binding;
import school.campusconnect.databinding.ItemAdminFeedBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.GroupItem;
import school.campusconnect.datamodel.TeamCountTBL;
import school.campusconnect.datamodel.banner.BannerAddReq;
import school.campusconnect.datamodel.banner.BannerRes;
import school.campusconnect.datamodel.banner.BannerTBL;
import school.campusconnect.datamodel.baseTeam.BaseTeamTableV2;
import school.campusconnect.datamodel.baseTeam.BaseTeamv2Response;
import school.campusconnect.datamodel.booths.BoothsTBL;
import school.campusconnect.datamodel.feed.AdminFeedTable;
import school.campusconnect.datamodel.feed.AdminFeederResponse;
import school.campusconnect.datamodel.notificationList.NotificationListRes;
import school.campusconnect.datamodel.notificationList.NotificationTable;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneHelper;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.DateTimeHelper;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.PicassoImageLoadingService;
import school.campusconnect.utils.SliderAdapter;
import school.campusconnect.views.SMBDialogUtils;
import ss.com.bannerslider.Slider;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

import static android.app.Activity.RESULT_OK;
import static school.campusconnect.utils.Constants.INTERVAL_EVENTAPI;

public class BaseTeamFragmentv3 extends BaseFragment implements LeafManager.OnCommunicationListener, TeamListAdapterNewV2.OnTeamClickListener, View.OnClickListener, FeedAdapter.onClick, SliderAdapter.Listner {


    private static final String TAG = "BaseTeamFragmentv3";

    //SliderBannerAdapter SliderBannerAdapter;
    private LeafManager manager;
    private TeamListAdapterNewV2 mAdapter;

    private FeedAdapter feedAdapter;
    private FeedAdminAdapter feedAdminAdapter;
    private Boolean isExpand = false;
    // PullRefreshLayout swipeRefreshLayout;
    DatabaseHandler databaseHandler;
    LeafPreference pref;

    private int multiGalleryCount = 0;
    private Boolean isGalleryMultiple = false;
    private Boolean isClear = true;
    private TransferUtility transferUtility;

    int pos = 0;
    private Boolean isEdit = false;
    ArrayList<BaseTeamv2Response.TeamListData> teamList = new ArrayList<>();
    ArrayList<NotificationListRes.NotificationListData> notificationList = new ArrayList<>();
    ArrayList<AdminFeederResponse.FeedData> adminNotificationList = new ArrayList<>();
    ArrayList<String> imageSliderList = new ArrayList<>();
    ArrayList<String> listAmazonS3Url = new ArrayList<>();
    private SliderBannerAdapter sliderAdapter;
    boolean isVisible;

    BannerRes.BannerData bannerData = new BannerRes.BannerData();

    ArrayList<BannerRes.BannerData> bannerDataList = new ArrayList<>();

    private MenuItem menuItem;

    private MenuItem removeWallMenu;
    private GroupItem mGroupItem;

    private File cameraFile;
    public Uri imageCaptureFile;


    public static final int REQUEST_LOAD_CAMERA_IMAGE = 101;
    public static final int REQUEST_LOAD_GALLERY_IMAGE = 102;

    private int visibleCount;

    private ProgressDialog progressDialog;

    FragmentBaseTeamFragmentv3Binding binding;

    LinearLayoutManager linearLayoutManager;

    public static BaseTeamFragmentv3 newInstance() {
        BaseTeamFragmentv3 fragment = new BaseTeamFragmentv3();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private Handler mHandler = new Handler();

    Runnable myRunnable = new Runnable() {
        @Override
        public void run() {

            try{

                visibleCount = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                Log.e(TAG,"Count"+visibleCount);
                binding.rvFeed.smoothScrollBy(0,60);

                Log.e(TAG,"last item"+feedAdapter.getItemCount());

                if (visibleCount+1 == feedAdapter.getItemCount())
                {
                    binding.rvFeed.smoothScrollToPosition(0);
                }
                else
                {
                    binding.rvFeed.smoothScrollBy(0,0);
                }
               // binding.rvFeed.smoothScrollToPosition( visibleCount+1 >= 10 ? 0: visibleCount+1 );
                mHandler.postDelayed(myRunnable, 3000);

            }catch (Exception e)
            {
                Log.e(TAG,"exception"+ e.getMessage());
            }

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_base_team_fragmentv3, container, false);

        inits();

        getBannerList();

        getNotification();

        getTeams();

        return binding.getRoot();
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (GroupDashboardActivityNew.allowedToAddUser)
            menu.findItem(R.id.menu_add_team).setVisible(true);
        else
            menu.findItem(R.id.menu_add_team).setVisible(false);


        if (mGroupItem.canPost) {
            menu.findItem(R.id.menu_profile).setVisible(false);
        } else {
            menu.findItem(R.id.menu_profile).setVisible(false);
        }


        if (LeafPreference.getInstance(getContext()).getInt(LeafPreference.CONST_GROUP_COUNT) > 1 && "constituency".equalsIgnoreCase(BuildConfig.AppCategory)) {
            menu.findItem(R.id.menu_logout).setVisible(false);
            menu.findItem(R.id.menu_change_mobile).setVisible(false);
            menu.findItem(R.id.menu_change_pin).setVisible(false);
            menu.findItem(R.id.menu_change_language).setVisible(false);
          //  menu.findItem(R.id.menu_change_language).setVisible(false);
            menu.findItem(R.id.menu_change_pass).setVisible(false);

        } else if (LeafPreference.getInstance(getContext()).getInt(LeafPreference.GROUP_COUNT) > 1) {
            menu.findItem(R.id.menu_logout).setVisible(false);
            menu.findItem(R.id.menu_change_pin).setVisible(false);
            menu.findItem(R.id.menu_change_language).setVisible(false);
           // menu.findItem(R.id.menu_change_language).setVisible(false);
            menu.findItem(R.id.menu_change_mobile).setVisible(false);
            menu.findItem(R.id.menu_change_pass).setVisible(false);

        } else {

            if (LeafPreference.getInstance(getContext()).getString(LeafPreference.SKIP_PIN).equalsIgnoreCase("yes"))
            {
                menu.findItem(R.id.menu_change_pin).setVisible(false);
            }
            else
            {
                menu.findItem(R.id.menu_change_pin).setVisible(true);
            }

            menu.findItem(R.id.menu_logout).setVisible(true);
            menu.findItem(R.id.menu_change_language).setVisible(true);
            menu.findItem(R.id.menu_change_pass).setVisible(true);
            menu.findItem(R.id.menu_change_mobile).setVisible(true);
        }

        menuItem = menu.findItem(R.id.action_notification_list);
        menuItem.setIcon(buildCounterDrawable(LeafPreference.getInstance(getContext()).getInt(GroupDashboardActivityNew.groupId + "_notification_count")));
        menuItem.setVisible(false);

        if ("constituency".equalsIgnoreCase(mGroupItem.category)) {
            menu.findItem(R.id.menu_add_team).setVisible(true);
        }
    }

    private Drawable buildCounterDrawable(int count) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.count_bg, null);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.count);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText("" + count);
        }

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_team:
                addTeam();
                return true;
            case R.id.menu_change_pass:
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
                return true;

            case R.id.menu_change_pin:
                Intent intentpin= new Intent(getActivity(), ChangePinActivity.class);
                startActivity(intentpin);
                return true;

            case R.id.menu_change_language:
                Intent intent1= new Intent(getActivity(), ChangeLanguageActivity.class);
                intent1.putExtra("isDashboard",true);
                startActivity(intent1);
                return true;


            case R.id.menu_change_mobile:
                Intent intentNumber= new Intent(getActivity(), ChangeNumberActivity.class);
                startActivity(intentNumber);
                return true;

            case R.id.menu_logout:
                logout();
                getActivity().finish();
                return true;


            case R.id.menu_profile: {
                if (isConnectionAvailable()) {
                    Intent intent2 = new Intent(getActivity(), ProfileActivity2.class);
                    startActivity(intent2);
                } else {
                    showNoNetworkMsg();
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkPermissionForWriteExternal() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            Log.e("External" + "permission", "checkpermission , granted");
            return true;
        } else {
            Log.e("External" + "permission", "checkpermission , denied");
            return false;
        }
    }


    public void requestPermissionForWriteExternal(int code) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_storage_permission_needed), Toast.LENGTH_LONG).show();
        } else {
            AppLog.e(TAG, "requestPermissionForWriteExternal");
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void inits() {

        pref = LeafPreference.getInstance(getActivity());

       // Log.e(TAG,"profile Image"+pref.getString(LeafPreference.PROFILE_IMAGE));

        databaseHandler = new DatabaseHandler(getActivity());
        transferUtility = AmazoneHelper.getTransferUtility(getActivity());

        mGroupItem = new Gson().fromJson(LeafPreference.getInstance(getContext()).getString(Constants.GROUP_DATA), GroupItem.class);

        manager = new LeafManager();

        mAdapter = new TeamListAdapterNewV2(teamList,this,BuildConfig.AppCategory);
        binding.rvTeams.setAdapter(mAdapter);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvFeed.setLayoutManager(linearLayoutManager);
        feedAdapter = new FeedAdapter(this);
        binding.rvFeed.setAdapter(feedAdapter);

        feedAdminAdapter = new FeedAdminAdapter();
        binding.rvAdminFeed.setAdapter(feedAdminAdapter);

        binding.imgExpandFeedBefore.setOnClickListener(this);
        binding.imgExpandFeedAfter.setOnClickListener(this);
        binding.imgExpandAdminFeedBefore.setOnClickListener(this);
        binding.imgExpandAdminFeedAfter.setOnClickListener(this);

        binding.tvViewMoreFeed.setOnClickListener(this);
        binding.imgEditBanner.setOnClickListener(this);


      /*  binding.imgEditVoter.setOnClickListener(this);

        binding.llMyFamily.setOnClickListener(this);
        binding.llMyIssue.setOnClickListener(this);*/

        binding.llAdTicket.setOnClickListener(this);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);

        enableOption();


    }

    private void enableOption() {

        if (mGroupItem.name !=null && mGroupItem.name.equalsIgnoreCase("Gruppie MLA")) {
            binding.llAdTicket.setVisibility(View.VISIBLE);
        }
        else
        {
            binding.llAdTicket.setVisibility(View.GONE);
        }

        if (mGroupItem.isAdmin)
        {
            binding.imgEditBanner.setVisibility(View.VISIBLE);
        }
        else
        {
            binding.imgEditBanner.setVisibility(View.GONE);
        }
    }


    private void apiCall() {

        if (!isConnectionAvailable()) {
            return;
        }
    //    binding.progressBar.setVisibility(View.VISIBLE);
        showLoadingBar(binding.progressBar);
        manager.myTeamListV2(this, GroupDashboardActivityNew.groupId);
    }
    @Override
    public void onStart() {
        super.onStart();

        if (notificationList!=null && notificationList.size() > 0)
        {
            mHandler.post(myRunnable);
        }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    AppLog.e(TAG , "run called  at  : "+System.currentTimeMillis());
                    if (getActivity() != null)
                    {
                        ((GroupDashboardActivityNew) getActivity()).callEventApi();
                    }
                }
            } , new Random().nextInt(INTERVAL_EVENTAPI));



        if (getContext() != null && menuItem != null) {
            menuItem.setIcon(buildCounterDrawable(LeafPreference.getInstance(getContext()).getInt(GroupDashboardActivityNew.groupId + "_notification_count")));
        }
        //  ((GroupDashboardActivityNew) getActivity()).tv_Desc.setVisibility(View.VISIBLE);
        //   ((GroupDashboardActivityNew) getActivity()).tv_Desc.setText(GroupDashboardActivityNew.total_user + " users");

    }

    @Override
    public void onResume() {
        super.onResume();
        isVisible = true;

        callNotificationAdminApi();

        if (getActivity() == null)
            return;

        String name = pref.getString(LeafPreference.PROFILE_NAME);
        String voterID = pref.getString(LeafPreference.PROFILE_VOTERID);
        String image = pref.getString(LeafPreference.PROFILE_IMAGE_NEW);



        if (getActivity() != null) {
            ((GroupDashboardActivityNew) getActivity()).tvToolbar.setText(name);
            ((GroupDashboardActivityNew) getActivity()).tv_Desc.setVisibility(View.VISIBLE);
            ((GroupDashboardActivityNew) getActivity()).tv_toolbar_icon.setVisibility(View.VISIBLE);
            ((GroupDashboardActivityNew) getActivity()).tv_Desc.setText(voterID);

            if (image.isEmpty())
            {
                ((GroupDashboardActivityNew) getActivity()).tv_toolbar_default.setVisibility(View.VISIBLE);
                ((GroupDashboardActivityNew) getActivity()).tv_toolbar_icon.setVisibility(View.GONE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(name), ImageUtil.getRandomColor(1));
                ((GroupDashboardActivityNew) getActivity()).tv_toolbar_default.setImageDrawable(drawable);
            }
            else {
                ((GroupDashboardActivityNew) getActivity()).tv_toolbar_default.setVisibility(View.GONE);
                Glide.with(getContext()).load(Constants.decodeUrlToBase64(image)).into(((GroupDashboardActivityNew) getActivity()).tv_toolbar_icon);
            }

        }


       /* AppLog.e(TAG,"name "+name);
        AppLog.e(TAG,"voter Id"+voterID);
        AppLog.e(TAG,"image "+image);

        binding.tvVoterName.setText("Name : "+name);
        binding.tvVoterId.setText("Voter ID : "+voterID);*/



        if (LeafPreference.getInstance(getActivity()).getInt(LeafPreference.CONST_GROUP_COUNT) > 1 && "constituency".equalsIgnoreCase(mGroupItem.category)) {
            ((GroupDashboardActivityNew) getActivity()).setBackEnabled(true);
        } else if (LeafPreference.getInstance(getActivity()).getInt(LeafPreference.GROUP_COUNT) > 1) {
            ((GroupDashboardActivityNew) getActivity()).setBackEnabled(true);
        } else {
            ((GroupDashboardActivityNew) getActivity()).setBackEnabled(false);
        }

        if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISBOOTHUPDATED)) {
            BoothsTBL.deleteBooth(GroupDashboardActivityNew.groupId);
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISBOOTHUPDATED, false);
        }

        if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISTEAMUPDATED)) {
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISTEAMUPDATED, false);
        }

        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("NOTIFICATION_COUNT_UPDATE"));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isVisible = false;
        try {
            getActivity().unregisterReceiver(mMessageReceiver);
        } catch (Exception ex) {
            AppLog.e("BroadcastReceiver error", "error--> " + ex.toString());
        }
    }

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (getContext() != null && menuItem != null) {
                menuItem.setIcon(buildCounterDrawable(LeafPreference.getInstance(getContext()).getInt(GroupDashboardActivityNew.groupId + "_notification_count")));
            }
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    public void addTeam() {
        startActivity(new Intent(getActivity(), CreateTeamActivity.class));
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {


        if (getActivity() == null)
            return;

        switch (apiId) {
            case LeafManager.API_MY_TEAM_LISTV2:

             //   binding.progressBar.setVisibility(View.GONE);

                hideLoadingBar();

                BaseTeamv2Response res = (BaseTeamv2Response) response;
                AppLog.e(TAG, "BaseTeamv2Response " + new Gson().toJson(res.getTeamData()));

                ArrayList<BaseTeamv2Response.TeamListData> result = res.getTeamData();

                AppLog.e(TAG, "data " + new Gson().toJson(result));

                BaseTeamTableV2.deleteTeams(GroupDashboardActivityNew.groupId);

                teamList.clear();

                ArrayList<String> currentTopics = new ArrayList<>();

                for (int i = 0; i < result.size(); i++) {

                    BaseTeamTableV2 baseTeamTable = new BaseTeamTableV2();

                    BaseTeamv2Response.TeamListData data = result.get(i);


                    baseTeamTable.group_id = GroupDashboardActivityNew.groupId;
                    baseTeamTable.activity = data.getActivity();
                    baseTeamTable.kanActivity = data.getKanActivity();
                    baseTeamTable.featureIcons = new Gson().toJson(data.getFeaturedIconData());
                    AppLog.e(TAG , "kanActivity : "+data.getKanActivity());
                    if (!LeafPreference.getInstance(getContext()).getString("MY_TEAM_INSERT").isEmpty())
                    {
                        baseTeamTable._now = LeafPreference.getInstance(getContext()).getString("MY_TEAM_INSERT");
                    }
                    else
                    {
                        baseTeamTable._now = DateTimeHelper.getCurrentTime();
                    }

                    if (!LeafPreference.getInstance(getContext()).getString("MY_TEAM_UPDATE").isEmpty())
                    {
                        baseTeamTable.update_team = LeafPreference.getInstance(getContext()).getString("MY_TEAM_UPDATE");
                    }
                    else
                    {
                        baseTeamTable.update_team = DateTimeHelper.getCurrentTime();
                    }

                    for(int j = 0 ; j < data.getFeaturedIconData().size(); j++)
                    {
                        try {
                            if (!data.getFeaturedIconData().get(j).name.equalsIgnoreCase("My Team")) {
                                if (databaseHandler.getCount() != 0) {
                                    try {
                                        String name = databaseHandler.getNameFromNum(data.getFeaturedIconData().get(j).phone.replaceAll(" ", ""));
                                        if (!TextUtils.isEmpty(name)) {
                                            data.getFeaturedIconData().get(j).name = name + " Team";
                                        }
                                    } catch (NullPointerException e) {
                                    }
                                }
                            }
                        } catch (NullPointerException e) {
                            AppLog.e("CONTACTS", "error is " + e.toString());
                        }

                    }

                    baseTeamTable.save();

                    for(int j =0; j < data.getFeaturedIconData().size();j++)
                    {
                        if (!TextUtils.isEmpty(data.getFeaturedIconData().get(j).groupId) && !TextUtils.isEmpty(data.getFeaturedIconData().get(j).teamId))
                        {
                            String topics = data.getFeaturedIconData().get(j).groupId + "_" + data.getFeaturedIconData().get(j).teamId;
                            currentTopics.add(topics);
                        }
                        else if (!TextUtils.isEmpty(data.getFeaturedIconData().get(j).groupId))
                        {
                            String topics = data.getFeaturedIconData().get(j).groupId;
                            currentTopics.add(topics);
                        }
                    }


                }


                teamList.addAll(result);
                mAdapter.notifyDataSetChanged();

                TeamCountTBL dashboardCount = TeamCountTBL.getByTypeAndGroup("DASHBOARD", GroupDashboardActivityNew.groupId);
                if (dashboardCount != null) {
                    dashboardCount.lastApiCalled = System.currentTimeMillis();
                    dashboardCount.save();
                }
                subscribeUnsubscribeTeam(currentTopics);
                break;

            case LeafManager.API_NOTIFICATION_LIST:

                NotificationListRes res1 = (NotificationListRes) response;
                List<NotificationListRes.NotificationListData> results = res1.getData();

                AppLog.e(TAG, "notificationRes " + new Gson().toJson(results));

                NotificationTable.deleteNotification(GroupDashboardActivityNew.groupId);

                if(results.size()>0)
                {
                    notificationList.clear();

                    if (results.size()>10)
                    {
                        for (int i = 0; i < 10; i++) {

                            NotificationTable notificationTable = new NotificationTable();

                            NotificationListRes.NotificationListData notificationListData= results.get(i);

                            notificationTable.teamId = notificationListData.getTeamId();
                            notificationTable.groupId = notificationListData.getGroupId();
                            notificationTable.userId = notificationListData.getUserId();
                            notificationTable.type = notificationListData.getType();
                            notificationTable.showComment = notificationListData.getShowComment();
                            notificationTable.postId = notificationListData.getPostId();
                            notificationTable.message = notificationListData.getMessage();
                            notificationTable.insertedAt = notificationListData.getInsertedAt();
                            notificationTable.createdByPhone = notificationListData.getCreatedByPhone();
                            notificationTable.createdByName = notificationListData.getCreatedByName();
                            notificationTable.createdByImage = notificationListData.getCreatedByImage();
                            notificationTable.createdById = notificationListData.getCreatedById();
                            notificationTable.readedComment = "true";

                            if (!LeafPreference.getInstance(getContext()).getString("FEED_API").isEmpty())
                            {
                                notificationTable._now = LeafPreference.getInstance(getContext()).getString("FEED_API");
                            }
                            else
                            {
                                notificationTable._now = DateTimeHelper.getCurrentTime();
                            }


                            notificationTable.save();
                        }
                    }
                    else
                    {
                        for (int i = 0; i < results.size(); i++) {

                            NotificationTable notificationTable = new NotificationTable();

                            NotificationListRes.NotificationListData notificationListData= results.get(i);

                            notificationTable.teamId = notificationListData.getTeamId();
                            notificationTable.groupId = notificationListData.getGroupId();
                            notificationTable.userId = notificationListData.getUserId();
                            notificationTable.type = notificationListData.getType();
                            notificationTable.showComment = notificationListData.getShowComment();
                            notificationTable.postId = notificationListData.getPostId();
                            notificationTable.message = notificationListData.getMessage();
                            notificationTable.insertedAt = notificationListData.getInsertedAt();
                            notificationTable.createdByPhone = notificationListData.getCreatedByPhone();
                            notificationTable.createdByName = notificationListData.getCreatedByName();
                            notificationTable.createdByImage = notificationListData.getCreatedByImage();
                            notificationTable.createdById = notificationListData.getCreatedById();
                            notificationTable.readedComment = "true";

                            if (!LeafPreference.getInstance(getContext()).getString("FEED_API").isEmpty())
                            {
                                notificationTable._now = LeafPreference.getInstance(getContext()).getString("FEED_API");
                            }
                            else
                            {
                                notificationTable._now = DateTimeHelper.getCurrentTime();
                            }


                            notificationTable.save();

                        }
                    }

                    getNotification();

                    TeamCountTBL dashboardCountv2 = TeamCountTBL.getByTypeAndGroup("DASHBOARD", GroupDashboardActivityNew.groupId);
                    if (dashboardCountv2 != null) {
                        dashboardCountv2.lastApiCalledNotification = System.currentTimeMillis();
                        dashboardCountv2.save();
                    }
                }

                break;

            case LeafManager.ADMIN_FEEDER_LIST:

                AdminFeederResponse adminFeederResponse = (AdminFeederResponse) response;

                List<AdminFeederResponse.FeedData> res1Data = adminFeederResponse.getFeedData();

                AppLog.e(TAG, "AdminNotificationRes " + new Gson().toJson(res1Data));

                AdminFeedTable.deleteAdminNotification(GroupDashboardActivityNew.groupId);

                adminNotificationList.clear();

                for (int i = 0; i < res1Data.size(); i++) {

                    AdminFeedTable adminFeedTable = new AdminFeedTable();

                    AdminFeederResponse.FeedData feedData= res1Data.get(i);

                    adminFeedTable.groupID = GroupDashboardActivityNew.groupId;
                    adminFeedTable.totalSubBoothsCount = feedData.getTotalSubBoothsCount();
                    adminFeedTable.totalSubBoothDiscussion = feedData.getTotalSubBoothDiscussion();
                    adminFeedTable.totalOpenIssuesCount = feedData.getTotalOpenIssuesCount();
                    adminFeedTable.totalBoothsDiscussion = feedData.getTotalBoothsDiscussion();
                    adminFeedTable.totalBoothsCount = feedData.getTotalBoothsCount();
                    adminFeedTable.totalAnnouncementCount = feedData.getTotalAnnouncementCount();
                    adminFeedTable.save();

                }
                adminNotificationList.addAll(res1Data);
                feedAdminAdapter.add(adminNotificationList);


                binding.imgExpandAdminFeedBefore.setVisibility(View.GONE);




                break;

            case LeafManager.API_BANNER_LIST:

          //      binding.progressBarBanner.setVisibility(View.GONE);
                hideLoadingBar();

                BannerRes bannerRes = (BannerRes) response;

                if (bannerRes.getBannerData().get(0).getFileName() != null && bannerRes.getBannerData().get(0).getFileName().size()>0)
                {
                  /*  binding.rvSliderBanner.setVisibility(View.VISIBLE);
                    binding.imgSlider.setVisibility(View.GONE);
                    Slider.init(new PicassoImageLoadingService(getContext()));
                    bannerData = bannerRes.getBannerData().get(0);

                    Log.e(TAG,"banner Data"+new Gson().toJson(bannerData));*/



                    Log.e(TAG,"API_BANNER_LIST banner Data"+new Gson().toJson(bannerRes.getBannerData()));

                    BannerTBL.deleteBanner(GroupDashboardActivityNew.groupId);

                    bannerDataList.clear();

                    for (int i = 0; i < bannerRes.getBannerData().size(); i++) {

                        BannerTBL bannerTBL = new BannerTBL();

                        BannerRes.BannerData bannerData=  bannerRes.getBannerData().get(0);

                        bannerTBL.groupId = GroupDashboardActivityNew.groupId;
                        bannerTBL.updatedAt = bannerData.updatedAt;

                        if (!LeafPreference.getInstance(getContext()).getString("BANNER_API").isEmpty())
                        {
                            bannerTBL._now = LeafPreference.getInstance(getContext()).getString("BANNER_API");
                        }
                        else
                        {
                            bannerTBL._now = DateTimeHelper.getCurrentTime();
                        }

                      //  bannerTBL._now = System.currentTimeMillis();
                        bannerTBL.fileType = bannerData.fileType;
                        bannerTBL.fileName = new Gson().toJson(bannerData.fileName);

                        bannerTBL.save();
                    }
                    getBannerList();
                    //sliderAdapter.add(bannerRes.getBannerData().get(0).fileName);


                }
                else
                {
                    bannerDataList.clear();
                    bannerData = new BannerRes.BannerData();
                    binding.rvSliderBanner.setVisibility(View.GONE);
                    binding.imgSlider.setVisibility(View.VISIBLE);
                }
                break;

            case LeafManager.API_ADD_BANNER_LIST:
                bannerListApiCall();
                break;

        }
    }
    private void subscribeUnsubscribeTeam(ArrayList<String> currentTopics) {
        String jsonTopic = pref.getString(LeafPreference.Subscribed_Teams);
        AppLog.e(TAG, "Prev Topics : " + jsonTopic);
        AppLog.e(TAG, "current Topics : " + new Gson().toJson(currentTopics));
        if (TextUtils.isEmpty(jsonTopic)) {
            for (int i = 0; i < currentTopics.size(); i++) {
                subScribeTeam(currentTopics.get(i));
            }
        } else {
            ArrayList<String> prefTopicList = new Gson().fromJson(jsonTopic, new TypeToken<ArrayList<String>>() {
            }.getType());
            // subscribe teams if not exist in pref
            for (int i = 0; i < currentTopics.size(); i++) {
                if (!prefTopicList.contains(currentTopics.get(i))) {
                    subScribeTeam(currentTopics.get(i));
                }
            }

            //unSubscribe team
            for (int i = 0; i < prefTopicList.size(); i++) {
                if (!currentTopics.contains(prefTopicList.get(i))) {
                    unSubScribeTeam(prefTopicList.get(i));
                }
            }
        }
    }

    private void subScribeTeam(final String topics) {
        FirebaseMessaging.getInstance().subscribeToTopic(topics)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            AppLog.e(TAG, "subscribeToTopic : " + topics + " : Successful()");
                            addTopicToPref(topics);
                        } else {
                            AppLog.e(TAG, "subscribeToTopic : " + topics + " Fail()");
                        }

                    }
                });
    }

    private void addTopicToPref(String topics) {
        if (TextUtils.isEmpty(pref.getString(LeafPreference.Subscribed_Teams))) {
            ArrayList<String> teamTopics = new ArrayList<>();
            teamTopics.add(topics);
            pref.setString(LeafPreference.Subscribed_Teams, new Gson().toJson(teamTopics));
        } else {
            ArrayList<String> teamTopics = new Gson().fromJson(pref.getString(LeafPreference.Subscribed_Teams), new TypeToken<ArrayList<String>>() {
            }.getType());
            teamTopics.add(topics);
            pref.setString(LeafPreference.Subscribed_Teams, new Gson().toJson(teamTopics));
        }
    }

    private void unSubScribeTeam(final String topics) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topics)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            AppLog.e(TAG, "unsubscribeFromTopic : " + topics + " : Successful()");
                            removeTopicFromPref(topics);
                        } else {
                            AppLog.e(TAG, "unsubscribeFromTopic : " + topics + " Fail()");
                        }

                    }
                });
    }

    private void removeTopicFromPref(String topics) {
        if (!TextUtils.isEmpty(pref.getString(LeafPreference.Subscribed_Teams))) {
            ArrayList<String> teamTopics = new Gson().fromJson(pref.getString(LeafPreference.Subscribed_Teams), new TypeToken<ArrayList<String>>() {
            }.getType());
            teamTopics.remove(topics);

            if (teamTopics.size() > 0)
                pref.setString(LeafPreference.Subscribed_Teams, new Gson().toJson(teamTopics));
            else
                pref.remove(LeafPreference.Subscribed_Teams);
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
     //   binding.progressBar.setVisibility(View.GONE);

        hideLoadingBar();

        if (apiId == LeafManager.API_BANNER_LIST)
        {
            hideLoadingBar();
          //  binding.progressBarBanner.setVisibility(View.GONE);
            binding.rvSliderBanner.setVisibility(View.GONE);
            binding.imgSlider.setVisibility(View.VISIBLE);
        }
        if (getActivity() != null) {

            if (msg.contains("401:Unauthorized")) {
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
    }

    @Override
    public void onException(int apiId, String msg) {
//        binding.progressBar.setVisibility(View.GONE);

        hideLoadingBar();

        if (apiId == LeafManager.API_BANNER_LIST)
        {
            hideLoadingBar();
  //          binding.progressBarBanner.setVisibility(View.GONE);
            binding.rvSliderBanner.setVisibility(View.GONE);
            binding.imgSlider.setVisibility(View.VISIBLE);
        }

        if (getActivity() != null)
            Toast.makeText(getActivity(), getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onTeamClick(MyTeamData team) {
        Log.e(TAG,"Team Data :"+new Gson().toJson(team));
        ((GroupDashboardActivityNew) getActivity()).onTeamSelected(team,"no","no");
    }

    @Override
    public void onGroupClick(MyTeamData group) {
        Log.e(TAG,"Group Data :"+new Gson().toJson(group));
        ((GroupDashboardActivityNew) getActivity()).groupSelected(group);
    }

    @Override
    public void addTeamClick() {
        startActivity(new Intent(getActivity(), CreateTeamActivity.class));
    }

    private void getNotification() {

        if (mGroupItem.isAdmin)
        {
            binding.llAdminFeed.setVisibility(View.VISIBLE);

            List<AdminFeedTable> adminFeedTableList = AdminFeedTable.getAdminNotificationList(GroupDashboardActivityNew.groupId);

            if (adminFeedTableList != null && adminFeedTableList.size() > 0)
            {
                adminNotificationList.clear();

                for (int i=0;i<adminFeedTableList.size();i++)
                {
                    AdminFeederResponse.FeedData feedData = new AdminFeederResponse.FeedData();

                    feedData.setTotalSubBoothsCount(adminFeedTableList.get(i).totalSubBoothsCount);
                    feedData.setTotalSubBoothDiscussion(adminFeedTableList.get(i).totalSubBoothDiscussion);
                    feedData.setTotalOpenIssuesCount(adminFeedTableList.get(i).totalOpenIssuesCount);
                    feedData.setTotalBoothsDiscussion(adminFeedTableList.get(i).totalBoothsDiscussion);
                    feedData.setTotalBoothsCount(adminFeedTableList.get(i).totalBoothsCount);
                    feedData.setTotalAnnouncementCount(adminFeedTableList.get(i).totalAnnouncementCount);

                    adminNotificationList.add(feedData);

                }
                feedAdminAdapter.add(adminNotificationList);
                if(adminNotificationList.size()>8){
                    binding.imgExpandAdminFeedBefore.setVisibility(View.VISIBLE);
                }else {
                    binding.imgExpandAdminFeedBefore.setVisibility(View.GONE);
                }

            }
            else
            {
                notificationApiCall();
            }
        }
        else
        {
            binding.llNormalFeed.setVisibility(View.VISIBLE);

            List<NotificationTable> notificationTableList = NotificationTable.getAllNotificationList(GroupDashboardActivityNew.groupId,1);

            if (notificationTableList != null && notificationTableList.size() > 0)
            {
                notificationList.clear();
                for (int i=0;i<notificationTableList.size();i++)
                {
                    NotificationListRes.NotificationListData notificationListData = new NotificationListRes.NotificationListData();
                    notificationListData.setGroupId(notificationTableList.get(i).groupId);
                    notificationListData.setUserId(notificationTableList.get(i).userId);
                    notificationListData.setType(notificationTableList.get(i).type);
                    notificationListData.setShowComment(notificationTableList.get(i).showComment);
                    notificationListData.setPostId(notificationTableList.get(i).postId);
                    notificationListData.setMessage(notificationTableList.get(i).message);
                    notificationListData.setInsertedAt(notificationTableList.get(i).insertedAt);
                    notificationListData.setCreatedByPhone(notificationTableList.get(i).createdByPhone);
                    notificationListData.setCreatedByName(notificationTableList.get(i).createdByName);
                    notificationListData.setCreatedByImage(notificationTableList.get(i).createdByImage);
                    notificationListData.setCreatedById(notificationTableList.get(i).createdById);
                    notificationListData.setTeamId(notificationTableList.get(i).teamId);
                    notificationListData.setIdPrimary(notificationTableList.get(i).getId());

                    notificationListData.setReadedComment(notificationTableList.get(i).readedComment);


                    notificationList.add(notificationListData);
                }
           /*     if (notificationList.size()>1)
                {
                    feedAdapter.add(notificationList,4);
                }
                else
                {

                }*/
                feedAdapter.add(notificationList,notificationList.size());

                mHandler.post(myRunnable);


                if(notificationList.size()>8){
                    binding.imgExpandFeedBefore.setVisibility(View.VISIBLE);
                }else {
                    binding.imgExpandFeedBefore.setVisibility(View.GONE);
                }
            }
            else
            {
                notificationApiCall();
            }
        }
    }

    private void callNotificationAdminApi()
    {
        if (mGroupItem.isAdmin) {
            new AdminDashboard().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

    }
    class AdminDashboard extends AsyncTask<Void, Void, Void>{
        private boolean isDataChange = false;
        @Override
        protected Void doInBackground(Void... voids) {

            LeafManager leafManager = new LeafManager();
            leafManager.getAdminFeederList(new LeafManager.OnCommunicationListener() {
                @Override
                public void onSuccess(int apiId, BaseResponse response) {

                    AdminFeederResponse adminFeederResponse = (AdminFeederResponse) response;
                    Log.e(TAG,"adminFeederResponse "+new Gson().toJson(adminFeederResponse));

                    List<AdminFeedTable> adminFeedTableList = AdminFeedTable.getAdminNotificationList(GroupDashboardActivityNew.groupId);

                    if (adminFeedTableList != null && adminFeedTableList.size() > 0) {


                        for (int i = 0; i < adminFeedTableList.size(); i++) {

                            if (adminFeedTableList.get(i).totalSubBoothsCount != adminFeederResponse.feedData.get(0).getTotalSubBoothsCount())
                            {
                                isDataChange = true;
                            }
                            if (adminFeedTableList.get(i).totalAnnouncementCount != adminFeederResponse.feedData.get(0).getTotalAnnouncementCount())
                            {
                                isDataChange = true;
                            }
                            if (adminFeedTableList.get(i).totalBoothsCount != adminFeederResponse.feedData.get(0).getTotalBoothsCount())
                            {
                                isDataChange = true;
                            }
                            if (adminFeedTableList.get(i).totalBoothsDiscussion != adminFeederResponse.feedData.get(0).getTotalBoothsDiscussion())
                            {
                                isDataChange = true;
                            }
                            if (adminFeedTableList.get(i).totalSubBoothDiscussion != adminFeederResponse.feedData.get(0).getTotalSubBoothDiscussion())
                            {
                                isDataChange = true;
                            }
                            if (adminFeedTableList.get(i).totalOpenIssuesCount != adminFeederResponse.feedData.get(0).getTotalOpenIssuesCount())
                            {
                                isDataChange = true;
                            }

                        }
                    }

                    if (isDataChange)
                    {
                        AdminFeedTable.deleteAdminNotification(GroupDashboardActivityNew.groupId);

                        adminNotificationList.clear();

                        for (int i = 0; i < adminFeederResponse.feedData.size(); i++) {

                            AdminFeedTable adminFeedTable = new AdminFeedTable();

                            AdminFeederResponse.FeedData feedData= adminFeederResponse.feedData.get(i);

                            adminFeedTable.groupID = GroupDashboardActivityNew.groupId;
                            adminFeedTable.totalSubBoothsCount = feedData.getTotalSubBoothsCount();
                            adminFeedTable.totalSubBoothDiscussion = feedData.getTotalSubBoothDiscussion();
                            adminFeedTable.totalOpenIssuesCount = feedData.getTotalOpenIssuesCount();
                            adminFeedTable.totalBoothsDiscussion = feedData.getTotalBoothsDiscussion();
                            adminFeedTable.totalBoothsCount = feedData.getTotalBoothsCount();
                            adminFeedTable.totalAnnouncementCount = feedData.getTotalAnnouncementCount();
                            adminFeedTable.save();

                        }
                        adminNotificationList.addAll(adminFeederResponse.getFeedData());
                        feedAdminAdapter.add(adminNotificationList);


                    }

                }

                @Override
                public void onFailure(int apiId, String msg) {

                }

                @Override
                public void onException(int apiId, String msg) {

                }
            },GroupDashboardActivityNew.groupId,"isAdmin");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


        }
    }

    private void notificationApiCall() {

        if (!isConnectionAvailable()) {
            return;
        }

        if (mGroupItem.isAdmin)
        {
            manager.getAdminFeederList(this,GroupDashboardActivityNew.groupId,"isAdmin");
        }
        else
        {
            manager.getNotificationList(this,GroupDashboardActivityNew.groupId,"1");
        }

    }

    private void getBannerList() {

        List<BannerTBL> bannerTBLList = BannerTBL.getBanner(GroupDashboardActivityNew.groupId);

        if (bannerTBLList != null && bannerTBLList.size() > 0)
        {
            bannerDataList.clear();

            for (int i = 0;i<bannerTBLList.size();i++)
            {
                BannerRes.BannerData bannerData = new BannerRes.BannerData();
                bannerData.updatedAt = bannerTBLList.get(i).updatedAt;
                bannerData.fileType = bannerTBLList.get(i).fileType;
                bannerData.fileName = new Gson().fromJson(bannerTBLList.get(i).fileName,new TypeToken<ArrayList<String>>() {}.getType());
                bannerDataList.add(bannerData);
            }
            Slider.init(new PicassoImageLoadingService(getContext()));
            sliderAdapter = new SliderBannerAdapter(bannerDataList.get(0).fileName,getContext());
            binding.rvSliderBanner.setAdapter(sliderAdapter);
            binding.rvSliderBanner.setInterval(7000);

        }
        else
        {
            bannerListApiCall();
        }

        if (bannerDataList != null && bannerDataList.size() >0)
        {
            binding.rvSliderBanner.setVisibility(View.VISIBLE);
            binding.imgSlider.setVisibility(View.GONE);

            bannerData = new BannerRes.BannerData();
            bannerData = bannerDataList.get(0);

            Log.e(TAG,"banner Data"+new Gson().toJson(bannerData));

            //sliderAdapter.add(bannerRes.getBannerData().get(0).fileName);
        }
        else
        {
            bannerData = new BannerRes.BannerData();
            bannerDataList.clear();
            binding.rvSliderBanner.setVisibility(View.GONE);
            binding.imgSlider.setVisibility(View.VISIBLE);
        }

    }



    public void bannerListApiCall() {

        Log.e(TAG,"refresh bannerListApiCall");

        if (!isConnectionAvailable()) {
            return;
        }
        showLoadingBar(binding.progressBarBanner);
    //    binding.progressBarBanner.setVisibility(View.VISIBLE);
        manager.getBannerList(this,GroupDashboardActivityNew.groupId);
    }

    private void getTeams() {

        List<BaseTeamTableV2> dataItemList = BaseTeamTableV2.getTeamList(GroupDashboardActivityNew.groupId);

        if (dataItemList != null && dataItemList.size() > 0) {
            teamList.clear();
            for (int i = 0; i < dataItemList.size(); i++) {
                BaseTeamv2Response.TeamListData myTeamData= new BaseTeamv2Response.TeamListData();
                myTeamData.activity = dataItemList.get(i).activity;
                myTeamData.kanActivity = dataItemList.get(i).kanActivity;
                myTeamData.MyTeamData= new Gson().fromJson(dataItemList.get(i).featureIcons, new TypeToken<ArrayList<MyTeamData>>() {}.getType());
                teamList.add(myTeamData);
            }
            mAdapter.notifyDataSetChanged();
        } else {
            apiCall();
        }
    }

    public void checkAndRefresh(boolean apiCall) {
        if (apiCall) {
            AppLog.e(TAG, "---- Refresh Team -----");
            apiCall();
        }
    }
    public void checkAndRefreshNotification(boolean apiCall) {
        if (apiCall) {
            AppLog.e(TAG, "---- Refresh Notification -----");
            notificationApiCall();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imgExpandFeedBefore:
                binding.imgExpandFeedAfter.setVisibility(View.VISIBLE);
                binding.imgExpandFeedBefore.setVisibility(View.GONE);
                binding.tvViewMoreFeed.setVisibility(View.VISIBLE);
                if (notificationList !=null && notificationList.size()>0)
                {
                    mHandler.removeCallbacks(myRunnable);
                    ViewGroup.LayoutParams layoutParams = binding.rvFeed.getLayoutParams();
                    layoutParams.width= ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height= ViewGroup.LayoutParams.WRAP_CONTENT;
                    binding.rvFeed.setLayoutParams(layoutParams);
                }
                feedAdapter.expand();
                break;

            case R.id.imgExpandFeedAfter:
                binding.imgExpandFeedBefore.setVisibility(View.VISIBLE);
                binding.imgExpandFeedAfter.setVisibility(View.GONE);
                binding.tvViewMoreFeed.setVisibility(View.GONE);

                if (notificationList !=null && notificationList.size() > 0)
                {
                    mHandler.post(myRunnable);
                    ViewGroup.LayoutParams layoutParam = binding.rvFeed.getLayoutParams();
                    layoutParam.width= ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParam.height= (int) getResources().getDimension(R.dimen.nav_header_height);
                    binding.rvFeed.setLayoutParams(layoutParam);
                }

                feedAdapter.expand();
                break;

            case R.id.imgExpandAdminFeedBefore:
                binding.imgExpandAdminFeedAfter.setVisibility(View.VISIBLE);
                binding.imgExpandAdminFeedBefore.setVisibility(View.GONE);


                feedAdminAdapter.expand();
                break;

            case R.id.imgExpandAdminFeedAfter:
                binding.imgExpandAdminFeedBefore.setVisibility(View.VISIBLE);
                binding.imgExpandAdminFeedAfter.setVisibility(View.GONE);
                feedAdminAdapter.expand();
                break;

            case R.id.imgEditBanner:
                if (checkPermissionForWriteExternal()) {

                    if (bannerData.getFileName()!=null && bannerData.getFileName().size() > 0)
                    {
                        showPhotoDialog(R.array.array_image_banner);
                    }
                    else
                    {
                        showPhotoDialog(R.array.array_image);
                    }
                } else {
                    requestPermissionForWriteExternal(21);
                }
                break;

            case R.id.llAdTicket:
                if (isConnectionAvailable()) {
                    startActivity(new Intent(getActivity(), AddTicketActivity.class));
                } else {
                    showNoNetworkMsg();
                }
                break;

          /*  case R.id.imgEditVoter:
                AppLog.e(TAG,"imgEditVoter");
                if (isConnectionAvailable()) {
                    Intent intent;
                    intent = new Intent(getActivity(), ProfileConstituencyActivity.class);
                    startActivity(intent);
                } else {
                    showNoNetworkMsg();
                }
                break;

            case R.id.llMyFamily:
                startActivity(new Intent(getActivity(), FamilyMemberActivity.class));
                break;

            case R.id.llMyIssue:
                startActivity(new Intent(getActivity(), IssueActivity.class));
                break;*/

            case R.id.tvViewMoreFeed:
                if (isConnectionAvailable()) {
                    Intent intent = new Intent(getContext(), NotificationListActivity.class);
                    intent.putExtra("id", GroupDashboardActivityNew.groupId);
                    intent.putExtra("title", mGroupItem.getName());
                    startActivity(intent);
                } else {
                    showNoNetworkMsg();
                }
                break;
        }

    }

    @Override
    public void onStop() {
        super.onStop();

        if (feedAdapter != null)
        {
          // feedAdapter.removeCallBack();
            mHandler.removeCallbacks(myRunnable);

        }
    }
    @Override
    public void setReadedComment(long idPrimary, Boolean readedComment) {
       /* NotificationTable.updateNotification(String.valueOf(readedComment),idPrimary);
        getNotification();*/
    }
    public void onItemClick(NotificationListRes.NotificationListData item,Boolean readComment)
    {
        AppLog.d(TAG,"onItemClick()");

        Intent i = new Intent(getContext(), ReadMoreActivity.class);
        Bundle bundle = new Bundle();

        if("schoolCalendar".equals(item.getType())){
            startActivity(new Intent(getContext(), CalendarActivity.class).putExtra("date",item.getInsertedAt()));
            return;
        }

        if("groupPost".equals(item.getType()) && !item.getShowComment())
        {
            //then redirect to "/api/v1/groups/{groupId}/post/{postId}/read"
            bundle.putString("groupId",item.getGroupId());
            bundle.putString("postId",item.getPostId());
            bundle.putString("type",item.getType());
            bundle.putString("userId",item.getUserId());
            i.putExtras(bundle);
            startActivity(i);
        }
        else if("gallery".equals(item.getType()) && !item.getShowComment())
        {
            startActivity(new Intent(getContext(), GalleryActivity.class));
        }
        else   if("teamPost".equals(item.getType()) && !item.getShowComment())
        {
            //"/api/v1/groups/{groupId}/team/{teamId}/post/{postId}/read"
            bundle.putString("groupId",item.getGroupId());
            bundle.putString("teamId",item.getTeamId());
            bundle.putString("postId",item.getPostId());
            bundle.putString("type",item.getType());
            bundle.putString("userId",item.getCreatedById());
            i.putExtras(bundle);
            startActivity(i);
        }
        else if("individualPost".equals(item.getType()) && !item.getShowComment())
        {
            //"/api/v1/groups/{groupId}/user/{userId}/post/{postId}/read"
            bundle.putString("groupId",item.getGroupId());
            bundle.putString("userId",item.getUserId());
            bundle.putString("postId",item.getPostId());
            bundle.putString("type",item.getType());
            i.putExtras(bundle);
            startActivity(i);
        }
        else if("groupPostComment".equals(item.getType()) && item.getShowComment())
        {
            //"/api/v1/groups/{groupId}/post/{postId}/read"
            bundle.putString("groupId",item.getGroupId());
            bundle.putString("postId",item.getPostId());
            bundle.putString("type",item.getType());
            bundle.putString("userId",item.getUserId());
            i.putExtras(bundle);
            startActivity(i);
        }
        else if("teamPostComment".equals(item.getType())  && item.getShowComment())
        {
            ///api/v1/groups/{groupId}/team/{teamId}/post/{postId}/read"
            bundle.putString("groupId",item.getGroupId());
            bundle.putString("teamId",item.getTeamId());
            bundle.putString("postId",item.getPostId());
            bundle.putString("type",item.getType());
            bundle.putString("userId",item.getCreatedById());
            i.putExtras(bundle);
            startActivity(i);

        }
        else if("individualPostComment".equals(item.getType()) && item.getShowComment())
        {
            ///api/v1/groups/{groupId}/user/{userId}/post/{postId}/read"
            bundle.putString("groupId",item.getGroupId());
            bundle.putString("userId",item.getUserId());
            bundle.putString("postId",item.getPostId());
            bundle.putString("type",item.getType());
            i.putExtras(bundle);
            startActivity(i);
        }

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                NotificationTable.updateNotification(String.valueOf(readComment),item.getIdPrimary());
                getNotification();
            }
        },1000);

    }

    @Override
    public void startEditBanner(Uri uri,int position) {


        pos = position;
        isEdit = true;
        /*if (checkPermissionForWriteExternal()) {
                   *//* if (listImages.size() > 0) {
                        showPhotoDialog(R.array.array_image_modify);
                    } else {*//*
            showPhotoDialog(R.array.array_image);
            //}

        } else {
            requestPermissionForWriteExternal(21);
        }*/

        CropImage.activity(uri)
                .start(getContext(),this);

    }

    public void showPhotoDialog(int resId) {
        SMBDialogUtils.showSMBSingleChoiceDialog(getActivity(),
                R.string.lbl_select_img, resId, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lw = ((AlertDialog) dialog).getListView();
                        switch (lw.getCheckedItemPosition()) {
                            case 0:
                                startCamera(REQUEST_LOAD_CAMERA_IMAGE);
                                break;
                            case 1:
                                startGallery(REQUEST_LOAD_GALLERY_IMAGE);
                                break;
                            case 2:
                                startRemoveBanner();
                                break;

                        }
                    }
                });
    }

    private void startRemoveBanner() {

        Intent i = new Intent(getActivity(), RemoveBannerActivity.class);
        i.putExtra("data",bannerData);
        startActivityForResult(i,13);
    }

    private void startCamera(int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            cameraFile = ImageUtil.getOutputMediaFile();
            imageCaptureFile = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".fileprovider", cameraFile);
        } else {
            cameraFile = ImageUtil.getOutputMediaFile();
            imageCaptureFile = Uri.fromFile(cameraFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureFile);
        startActivityForResult(intent, requestCode);

    }
    protected void startGallery(int requestCode) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG,"result Code "+resultCode);

        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        if (requestCode == 13 && resultCode == RESULT_OK)
        {
            getBannerList();
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();
                Log.e(TAG,"result Uri Crop Image "+resultUri);


                if (isGalleryMultiple)
                {
                    if (isClear)
                    {
                        isClear = false;
                        imageSliderList.clear();

                    }
                    imageSliderList.add(resultUri.toString());
                }
                else
                {
                    imageSliderList.clear();
                    imageSliderList.add(resultUri.toString());
                }
                //Glide.with(getContext()).load(resultUri).into(binding.imgSlider);

             /*   if (isEdit)
                {
                    imageSlider.set(pos,resultUri);
                    sliderAdapter.add(imageSlider);
                }
                else
                {
                    imageSlider.add(resultUri);
                    sliderAdapter.add(imageSlider);
                }*/

                if (multiGalleryCount == imageSliderList.size())
                {
                    multiGalleryCount = 0;
                 //   binding.progressBar.setVisibility(View.VISIBLE);
                    progressDialog.setMessage("Uploading Image...");
                    progressDialog.show();
                    uploadToAmazon();

                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e(TAG,"error"+error);
            }
        }

        if (requestCode == REQUEST_LOAD_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            final Uri selectedImage = data.getData();
            AppLog.e(TAG, "selectedImage : " + selectedImage);

            ClipData clipData = data.getClipData();

            if (clipData == null) {

                multiGalleryCount = 1;
                isGalleryMultiple = false;
//                String path = ImageUtil.getPath(this, selectedImage);
                //  listImages.add(selectedImage.toString());
                CropImage.activity(selectedImage)
                        .start(getContext(),this);
            } else {

                multiGalleryCount = clipData.getItemCount();
                isClear = true;

                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    final Uri uri1 = item.getUri();
//                    String path = ImageUtil.getPath(this, uri1);
                    //    listImages.add(uri1.toString());
                    isGalleryMultiple = true;
                    CropImage.activity(uri1)
                            .start(getContext(),this);
                }
            }



        }
        else if (requestCode == REQUEST_LOAD_CAMERA_IMAGE && resultCode == Activity.RESULT_OK) {
               AppLog.e(TAG, "imageCaptureFile : " + imageCaptureFile);

               multiGalleryCount = 1;

//            isEdit = false;
            isGalleryMultiple = false;

            CropImage.activity(imageCaptureFile)
                    .setOutputUri(imageCaptureFile)
                    .start(getContext(),this);
        }
    }

    private void uploadToAmazon() {

        for (int i = 0; i < imageSliderList.size(); i++) {
            try {
                File newFile = new Compressor(getContext()).setMaxWidth(1000).setQuality(90).compressToFile(new File(imageSliderList.get(i)));
                imageSliderList.set(i, newFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        AppLog.e(TAG, "Final PAth :: " + imageSliderList.toString());

        listAmazonS3Url.clear();

        upLoadImageOnCloud(0);

    }

    private void upLoadImageOnCloud(final int pos) {

        BannerAddReq req = new BannerAddReq();

        if (pos == imageSliderList.size()) {
            if (progressDialog!=null) {
                progressDialog.dismiss();
            }

          ///  mainRequest.fileName = listAmazonS3Url;

            Log.e(TAG,"banner Data upLoadImageOnCloud "+new Gson().toJson(bannerData));

            Log.e(TAG,"banner Data imageSliderList Size"+imageSliderList.size());



            if (bannerData.getFileName()!=null && bannerData.getFileName().size() > 0)
            {
                listAmazonS3Url.addAll(bannerData.getFileName());
            }

            Log.e(TAG,"banner Data listAmazonS3Url Size "+ listAmazonS3Url.size());

            req.setBannerFile(listAmazonS3Url);
            req.setBannerFileType("image");

            AppLog.e(TAG, "send data : " + new Gson().toJson(req));

            manager.addBannerList(this,GroupDashboardActivityNew.groupId,req);


        } else {
            final String key = AmazoneHelper.getAmazonS3Key("image");

            TransferObserver observer ;
            UploadOptions option = UploadOptions.
                    builder().bucket(AmazoneHelper.BUCKET_NAME).
                    cannedAcl(CannedAccessControlList.PublicRead).build();
            try {
                observer = transferUtility.upload(key, getContext().getContentResolver().openInputStream(Uri.parse(imageSliderList.get(pos))), option);
                observer.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        AppLog.e(TAG, "onStateChanged: " + id + ", " + state.name());
                        if (state.toString().equalsIgnoreCase("COMPLETED")) {
                            Log.e("MULTI_IMAGE", "onStateChanged " + pos);
                            updateList(pos, key);
                        }
                        if (TransferState.FAILED.equals(state)) {
                            Toast.makeText(getContext(), "Failed to upload", Toast.LENGTH_SHORT).show();
                            if(progressDialog!=null)
                                progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                        int percentDone = (int) percentDonef;
                        progressDialog.setMessage("Uploading Image " + percentDone + "% " + (pos + 1) + " out of " + imageSliderList.size() + ", please wait...");

                        AppLog.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                                + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                //        binding.progressBar.setVisibility(View.GONE);
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        AppLog.e(TAG, "Upload Error : " + ex);
                        Toast.makeText(getContext(), getResources().getString(R.string.image_upload_error), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }


        }


    }
    private void updateList(int pos, String key) {
        String _finalUrl = AmazoneHelper.BUCKET_NAME_URL + key;

        Log.e("FINALURL", "url is " + _finalUrl);

        _finalUrl = Constants.encodeStringToBase64(_finalUrl);

        Log.e("FINALURL", "encoded url is " + _finalUrl);

        listAmazonS3Url.add(_finalUrl);

        upLoadImageOnCloud(pos + 1);
    }

    public class FeedAdminAdapter extends RecyclerView.Adapter<FeedAdminAdapter.ViewHolder> {

        private ArrayList<AdminFeederResponse.FeedData> feedData;
        public boolean isExpand = false;
        @NonNull
        @Override
        public FeedAdminAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemAdminFeedBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.item_admin_feed,parent,false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull FeedAdminAdapter.ViewHolder holder, int position) {

            AdminFeederResponse.FeedData data = feedData.get(position);

            holder.binding.tvTotalOpenIssue.setText(String.valueOf(data.getTotalOpenIssuesCount()));
            holder.binding.tvTotalAnnouncementCount.setText(String.valueOf(data.getTotalAnnouncementCount()));
            holder.binding.tvTotalBoothCount.setText(String.valueOf(data.getTotalBoothsCount()));
            holder.binding.tvTotalBoothsDiscussionCount.setText(String.valueOf(data.getTotalBoothsDiscussion()));
            holder.binding.tvTotalPublicStreetsCount.setText(String.valueOf(data.getTotalSubBoothsCount()));
            holder.binding.tvTotalPublicDiscussionCount.setText(String.valueOf(data.getTotalSubBoothDiscussion()));
            
            /*if (!isExpand)
            {
                holder.binding.llTotalDiscussionCount.setVisibility(View.GONE);
                holder.binding.llTotalPublicStreet.setVisibility(View.GONE);
            }else
            {
                holder.binding.llTotalDiscussionCount.setVisibility(View.VISIBLE);
                holder.binding.llTotalPublicStreet.setVisibility(View.VISIBLE);
            }*/

            holder.binding.llOpenTickets.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((GroupDashboardActivityNew) getActivity()).issueClick();
                }
            });

            holder.binding.llAnnouncement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((GroupDashboardActivityNew) getActivity()).announcementClick();
                }
            });

            holder.binding.llBoothCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  ((GroupDashboardActivityNew) getActivity()).updateTabIcons(1);
                    ((GroupDashboardActivityNew) getActivity()).tabLayout.getTabAt(1).select();
                }
            });

            holder.binding.llDiscuss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((GroupDashboardActivityNew) getActivity()).tabLayout.getTabAt(1).select();

                }
            });

            holder.binding.llPublicStreet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((GroupDashboardActivityNew) getActivity()).tabLayout.getTabAt(2).select();
                }
            });

            holder.binding.llPublicDiscussion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((GroupDashboardActivityNew) getActivity()).tabLayout.getTabAt(2).select();

                }
            });
        }


        public void expand()
        {
            if (isExpand)
            {
                isExpand = false;
            }
            else
            {
                isExpand = true;
            }
           // notifyDataSetChanged();
        }

        
        public void add(ArrayList<AdminFeederResponse.FeedData> feedData)
        {
            this.feedData = feedData;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {

            return feedData != null ? feedData.size() : 0;

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ItemAdminFeedBinding binding;
            public ViewHolder(@NonNull ItemAdminFeedBinding itemView) {
                super(itemView.getRoot());
                binding = itemView;
            }
        }
    }

    public class SliderBannerAdapter extends ss.com.bannerslider.adapters.SliderAdapter
    {

        ArrayList<String> urls;
        Context context;

        public SliderBannerAdapter(ArrayList<String> urls, Context context)
        {
            this.urls = urls;
            this.context = context;
        }

        @Override
        public int getItemCount() {
            return urls.size();
        }

        @Override
        public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
            imageSlideViewHolder.bindImageSlide(Constants.decodeUrlToBase64(urls.get(position)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 21:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (bannerData.getFileName()!=null && bannerData.getFileName().size() > 0)
                    {
                        showPhotoDialog(R.array.array_image_banner);
                    }
                    else
                    {
                        showPhotoDialog(R.array.array_image);
                    }
                    Log.e("AddPost" + "permission", "granted camera");
                } else {
                    Log.e("AddPost" + "permission", "denied camera");
                }
                break;
        }

    }
}