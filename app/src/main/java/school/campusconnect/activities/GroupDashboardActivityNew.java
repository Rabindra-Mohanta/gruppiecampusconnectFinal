package school.campusconnect.activities;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.icu.text.Transliterator;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.activeandroid.ActiveAndroid;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.util.DisplayMetrics;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import school.campusconnect.BuildConfig;
import school.campusconnect.datamodel.EventTBL;
import school.campusconnect.datamodel.LiveClassListTBL;
import school.campusconnect.datamodel.LoginRequest;
import school.campusconnect.datamodel.SubjectCountTBL;
import school.campusconnect.datamodel.TeamCountTBL;
import school.campusconnect.datamodel.VideoOfflineObject;
import school.campusconnect.datamodel.banner.BannerTBL;
import school.campusconnect.datamodel.baseTeam.BaseTeamTableV2;
import school.campusconnect.datamodel.booths.BoothsTBL;
import school.campusconnect.datamodel.event.HomeTeamDataTBL;
import school.campusconnect.datamodel.event.UpdateDataEventRes;
import school.campusconnect.datamodel.notificationList.NotificationTable;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.datamodel.videocall.VideoClassResponse;
import school.campusconnect.fragments.BoothListMyTeamFragment;
import school.campusconnect.fragments.BoothPresidentListMyTeamFragment;
import school.campusconnect.fragments.DashboardNewUi.BaseTeamFragmentv2;
import school.campusconnect.fragments.BoothListFragment;
import school.campusconnect.fragments.DashboardNewUi.BaseTeamFragmentv3;
import school.campusconnect.fragments.GeneralPostFragment;
import school.campusconnect.fragments.GenralPostConstituencyFragment;
import school.campusconnect.fragments.MemberTeamListFragment;
import school.campusconnect.fragments.MyTeamSubBoothFragment;
import school.campusconnect.fragments.MyTeamVoterListFragment;
import school.campusconnect.fragments.PublicForumListFragment;
import school.campusconnect.fragments.TeamPostsFragmentNew;
import school.campusconnect.utils.AppLog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import net.frederico.showtipsview.ShowTipsBuilder;
import net.frederico.showtipsview.ShowTipsView;
import net.frederico.showtipsview.ShowTipsViewInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import school.campusconnect.R;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.GroupItem;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.MixOperations;
import school.campusconnect.views.SMBDialogUtils;

public class GroupDashboardActivityNew extends BaseActivity
        implements View.OnClickListener, LeafManager.OnCommunicationListener, LeafManager.OnAddUpdateListener<AddPostValidationError> {

    public static final String EXTRA_GROUP_ITEM = "extra_group_item";
    private static final String TAG = "GroupDashboardActNew";
    public static String selectedUserInChat = "";

    @Bind(R.id.toolbar_dashboard)
    public Toolbar mToolBar;


    @Bind(R.id.appBar)
    AppBarLayout appBar;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.tabLayout)
    public TabLayout tabLayout;

   /* @Bind(R.id.fabAddTicket)
    public TextView fabAddTicket;

    @Bind(R.id.llAdTicket)
    public CardView llAdTicket;*/

    @Bind(R.id.llAuthorizedUser)
    LinearLayout llAuthorizedUser;

    @Bind(R.id.llAllUsers)
    LinearLayout llAllUsers;

    @Bind(R.id.llDoubt)
    LinearLayout llDoubt;

    @Bind(R.id.rlMore)
    RelativeLayout rlMore;



    @Bind(R.id.llArchiveTeam)
    LinearLayout llArchiveTeam;

    @Bind(R.id.llDiscuss)
    LinearLayout llDiscuss;

    @Bind(R.id.llPeople)
    LinearLayout llPeople;

    @Bind(R.id.llNotification)
    LinearLayout llNotification;

    @Bind(R.id.llClass)
    LinearLayout llClass;

    @Bind(R.id.llFamily)
    LinearLayout llFamily;

    @Bind(R.id.llSubject)
    LinearLayout llSubject;

    @Bind(R.id.llSubject2)
    LinearLayout llSubject2;

    @Bind(R.id.llStaffReg)
    LinearLayout llStaffReg;

    @Bind(R.id.llMakeAdmin)
    LinearLayout llMakeAdmin;

    @Bind(R.id.llBusRegister)
    LinearLayout llBusRegister;

    @Bind(R.id.llIssueRegister)
    LinearLayout llIssueRegister;

    @Bind(R.id.llBothRegister)
    LinearLayout llBothRegister;

    @Bind(R.id.llBookmark)
    LinearLayout llBookmark;

    @Bind(R.id.llBothCoordinateRegister)
    LinearLayout llBothCoordinateRegister;

    @Bind(R.id.llMyTeam)
    LinearLayout llMyTeam;

    @Bind(R.id.llAttendanceReport)
    LinearLayout llAttendanceReport;

    @Bind(R.id.tapView1)
    public View tapView1;

    @Bind(R.id.tapView2)
    public View tapView2;

    @Bind(R.id.tapView3)
    public View tapView3;

    @Bind(R.id.tvAbout)
    public TextView tvAbout;

    public TextView tvToolbar;
    public TextView tv_Desc;

    SharedPreferences prefs;

    LeafManager manager;

    public static GroupItem mGroupItem;

    public static String image;
    public static boolean mAllowPostAll;
    public static boolean notificationApiCall = false;
    public static int imageHeight;
    public static String share_image;
    public static int imageWidth;
    public static int share_image_type;

    public static boolean isAdmin = false;
    public static boolean isPost = false;
    public static boolean mAllowPostShare;
    public static boolean mAllowAdminChange;
    public static boolean mAllowPostQuestion;
    public static boolean mAllowDuplicate;

    public static boolean makeAdmin = false;

    public static String group_name = "";

    public static boolean is_share_edit;
    public static boolean ifVideoSelected;

    public static String share_title;
    public static String share_desc;
    public static String share_type;

    public static String team_id;

    public static String selectedUrl = "";
    public static String selectedYoutubeId = "";
    public static String uploadTitle = "";
    public static String uploadDesc = "";
    public static String enteredTitle = "";
    public static String enteredDesc = "";
    public Uri imageCaptureFile;

    public CircleImageView tv_toolbar_icon;
    public ImageView tv_toolbar_default;
    public static String groupId = "";
    public static String total_user = "";

    int prevTabPos = 0;

    String[] tabText;
    int[] tabIcon;
    int[] tabIcongray;
    // public TextView tv_Desc;
    private int windowCount = 1;
    private ShowTipsView showtips;

    public static int postCount = 0;
    public static String adminPhone = "";
    public static boolean allowedToAddUser;
    public static String groupCategory = "";
    private AlertDialog editDialog;

    public static final String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE
    };


    Transliterator transliterator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_new);
        ButterKnife.bind(this);
     //   progressBar.setVisibility(View.VISIBLE);

        init();

        ActiveAndroid.initialize(this);

        calculateDisplayMatrix();

        setGroupData(true);

        enableOptions();

        cleverTapGroupVisited(mGroupItem);

        setTitle("");

        HomeClick();

        DeleteOldSavedVideos();

        AppLog.e(TAG, "UserId : " + LeafPreference.getInstance(this).getString(LeafPreference.LOGIN_ID));
        AppLog.e(TAG, "Category :" + mGroupItem.category);

        if (mGroupItem != null) {
            if (!mGroupItem.category.equalsIgnoreCase(Constants.CATEGORY_SCHOOL)) {
                DatabaseHandler databaseHandler = new DatabaseHandler(this);
                if (databaseHandler.getCount() == 0) {
                    getContactsWithPermission();
                }
            }
        }
        // sendNotification("Message","Title");

       reqPermission();
    }
    public void reqPermission(){
        if (!hasPermission(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, 222);
        }
    }



    public void callEventApi() {

     //      progressBar.setVisibility(View.GONE);

       // new EventAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        LeafManager leafManager = new LeafManager();
        leafManager.getUpdateEventList(new LeafManager.OnCommunicationListener() {
            @Override
            public void onSuccess(int apiId, BaseResponse response) {
                AppLog.e(TAG, "onSuccess : " + response.status);
                UpdateDataEventRes res = (UpdateDataEventRes) response;

                if (res.data == null || res.data.size() == 0)
                    return;

                new EventAsync(res).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }

            @Override
            public void onFailure(int apiId, String msg) {
                AppLog.e(TAG, "onFailure : " + msg);
            }

            @Override
            public void onException(int apiId, String msg) {
                AppLog.e(TAG, "onException : " + msg);
            }
        }, groupId);
    }


    class EventAsync extends AsyncTask<Void, Void, Void> {
        UpdateDataEventRes res;
        boolean ifNeedToLogout = false;
        TeamCountTBL dashboardCount;
        public EventAsync(UpdateDataEventRes data) {
            this.res = data;
        }


        @Override
        protected Void doInBackground(Void... voids) {

            LeafPreference.getInstance(GroupDashboardActivityNew.this).setString(LeafPreference.ACCESS_KEY,res.data.get(0).teamsListCount.accessKey);
            LeafPreference.getInstance(GroupDashboardActivityNew.this).setString(LeafPreference.SECRET_KEY,res.data.get(0).teamsListCount.secretKey);

            LeafPreference.getInstance(GroupDashboardActivityNew.this).setString("PREVIEW_URL",res.data.get(0).imagePreviewUrl);

            ArrayList<UpdateDataEventRes.EventResData> eventList = res.data.get(0).eventList;


            for (int i = 0; i < eventList.size(); i++) {
                UpdateDataEventRes.EventResData curr = eventList.get(i);
                EventTBL eventTBL = null;
                if (curr.eventType.equalsIgnoreCase("1")) {
                    eventTBL = EventTBL.getGroupEvent(curr.groupId);
                } else if (curr.eventType.equalsIgnoreCase("2")) {
                    eventTBL = EventTBL.getTeamEvent(curr.groupId, curr.teamId);
                } else if (curr.eventType.equalsIgnoreCase("3")) {
                    eventTBL = EventTBL.getNotesVideoEvent(curr.groupId, curr.teamId, curr.subjectId);
                } else if (curr.eventType.equalsIgnoreCase("4")) {
                    eventTBL = EventTBL.getAssignmentEvent(curr.groupId, curr.teamId, curr.subjectId);
                } else if (curr.eventType.equalsIgnoreCase("6")) {
                    eventTBL = EventTBL.getTestEvent(curr.groupId, curr.teamId, curr.subjectId);
                } else if (curr.eventType.equalsIgnoreCase("5"))
                {
                    eventTBL = EventTBL.getAdminEvents(curr.groupId);

                    if (eventTBL == null)
                        AppLog.e(TAG, "eventTBL is  nulll ");
                    else
                        AppLog.e(TAG, "eventTBL eventat : " + eventTBL.eventAt);

                    if (eventTBL == null || MixOperations.isNewEvent(curr.eventAt, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", eventTBL.eventAt)) {
                        ifNeedToLogout = true;
                    }

                }

                if (eventTBL == null) {
                    eventTBL = new EventTBL();
                    eventTBL.groupId = curr.groupId;
                    eventTBL.subjectId = curr.subjectId;
                    eventTBL.teamId = curr.teamId;
                    eventTBL.eventType = curr.eventType;
                    eventTBL.eventName = curr.eventName;
                }
                eventTBL.insertedId = curr.insertedId;
                eventTBL.eventAt = curr.eventAt;
                eventTBL.save();

            }


            UpdateDataEventRes.TeamListCount teamCount = res.data.get(0).teamsListCount;
            TeamCountTBL groupCount = TeamCountTBL.getByTypeAndGroup("GROUP", groupId);
            if (groupCount == null) {
                groupCount = new TeamCountTBL();
                groupCount.typeOfTeam = "GROUP";
                groupCount.oldCount = teamCount.schoolGroupCount;
            } else {
                if (groupCount.oldCount == 1 && teamCount.schoolGroupCount > 1) {
                    ifNeedToLogout = true;
                } else if (groupCount.oldCount > 1 && teamCount.schoolGroupCount == 1) {
                    ifNeedToLogout = true;
                } else if (groupCount.oldCount != teamCount.schoolGroupCount) {
                    LeafPreference.getInstance(GroupDashboardActivityNew.this).setBoolean("group_list_refresh", true);
                }
                groupCount.oldCount = teamCount.schoolGroupCount;
            }
            groupCount.lastInsertedTeamTime = teamCount.lastInsertedTeamTime;
            groupCount.lastNotificationAt = teamCount.lastNotificationAt;
            groupCount.count = teamCount.schoolGroupCount;
            groupCount.groupId = groupId;
            groupCount.save();


            TeamCountTBL liveCount = TeamCountTBL.getByTypeAndGroup("LIVE", groupId);
            if (liveCount == null) {
                liveCount = new TeamCountTBL();
                liveCount.typeOfTeam = "LIVE";
                liveCount.oldCount = teamCount.liveClassTeamCount;
            }
            liveCount.lastNotificationAt = teamCount.lastNotificationAt;
            liveCount.lastInsertedTeamTime = teamCount.lastInsertedTeamTime;
            liveCount.count = teamCount.liveClassTeamCount;
            liveCount.groupId = groupId;
            liveCount.save();


            TeamCountTBL allCount = TeamCountTBL.getByTypeAndGroup("ALL", groupId);
            if (allCount == null) {
                allCount = new TeamCountTBL();
                allCount.typeOfTeam = "ALL";
                allCount.oldCount = teamCount.getAllClassTeamCount;
            }
            allCount.lastNotificationAt = teamCount.lastNotificationAt;
            allCount.lastInsertedTeamTime = teamCount.lastInsertedTeamTime;
            allCount.count = teamCount.getAllClassTeamCount;
            allCount.groupId = groupId;
            allCount.save();


            dashboardCount = TeamCountTBL.getByTypeAndGroup("DASHBOARD", groupId);
            if (dashboardCount == null) {
                dashboardCount = new TeamCountTBL();
                dashboardCount.typeOfTeam = "DASHBOARD";
                dashboardCount.oldCount = teamCount.dashboardTeamCount;
            }

            dashboardCount.lastNotificationAt = teamCount.lastNotificationAt;
            dashboardCount.lastInsertedTeamTime = teamCount.lastInsertedTeamTime;
            dashboardCount.count = teamCount.dashboardTeamCount;
            dashboardCount.groupId = groupId;

            dashboardCount.save();



            if (BuildConfig.AppCategory.equalsIgnoreCase("constituency"))
            {
                LeafPreference.getInstance(GroupDashboardActivityNew.this).setString("BANNER_API",res.data.get(0).bannerPostEventAt);
                LeafPreference.getInstance(GroupDashboardActivityNew.this).setString("FEED_API",res.data.get(0).notificationFeedEventAt);
                LeafPreference.getInstance(GroupDashboardActivityNew.this).setString("PROFILE_API",res.data.get(0).myProfileUpdatedEventAt);
                LeafPreference.getInstance(GroupDashboardActivityNew.this).setString("MY_TEAM_UPDATE",res.data.get(0).lastUpdatedTeamTime);
                LeafPreference.getInstance(GroupDashboardActivityNew.this).setString("MY_TEAM_INSERT",res.data.get(0).lastInsertedTeamTime);
                LeafPreference.getInstance(GroupDashboardActivityNew.this).setString("BOOTH_INSERT",res.data.get(0).lastInsertedBoothTeamTime);
                LeafPreference.getInstance(GroupDashboardActivityNew.this).setString("ANNOUNCEMENT_POST",res.data.get(0).announcementPostEventAt);
                LeafPreference.getInstance(GroupDashboardActivityNew.this).setString("CALENDAR_POST",res.data.get(0).calendarEventAt);
                LeafPreference.getInstance(GroupDashboardActivityNew.this).setString("GALLERY_POST",res.data.get(0).galleryPostEventAt);
              /*  if (HomeTeamDataTBL.getAll().size() > 0)
                {
                    HomeTeamDataTBL.deleteAll();
                }

                if (res.data.get(0).homeTeamData != null)
                {
                    for (int i = 0;i<res.data.get(0).homeTeamData.size();i++)
                    {
                        HomeTeamDataTBL homeTeamDataTBL = new HomeTeamDataTBL();
                        homeTeamDataTBL.teamId = res.data.get(0).homeTeamData.get(i).teamId;
                        homeTeamDataTBL.members = res.data.get(0).homeTeamData.get(i).members;
                        homeTeamDataTBL.lastTeamPostAt = res.data.get(0).homeTeamData.get(i).lastTeamPostAt;

                        homeTeamDataTBL.lastCommitteeForBoothUpdatedEventAt = res.data.get(0).homeTeamData.get(i).lastCommitteeForBoothUpdatedEventAt;
                        homeTeamDataTBL.canPost = res.data.get(0).homeTeamData.get(i).canPost;
                        homeTeamDataTBL.canComment = res.data.get(0).homeTeamData.get(i).canComment;

                        homeTeamDataTBL.save();
                    }
                }*/
                 /*        if (BoothPostEventTBL.getAll().size() > 0)
                {
                    BoothPostEventTBL.deleteAll();
                }

                if (res.data.get(0).allBoothsPostEventAt != null)
                {
                    for (int i = 0;i<res.data.get(0).allBoothsPostEventAt.size();i++)
                    {
                        BoothPostEventTBL boothPostEventTBL = new BoothPostEventTBL();
                        boothPostEventTBL.boothId = res.data.get(0).allBoothsPostEventAt.get(i).boothId;
                        boothPostEventTBL.members = res.data.get(0).allBoothsPostEventAt.get(i).members;
                        boothPostEventTBL.lastBoothPostAt = res.data.get(0).allBoothsPostEventAt.get(i).lastBoothPostAt;

                        boothPostEventTBL.save();

                        BoothsTBL boothsTBL = BoothsTBL.getBoothSingle(res.data.get(0).allBoothsPostEventAt.get(i).boothId);

                        if(boothsTBL !=null)
                        {
                            boothsTBL.members = res.data.get(0).allBoothsPostEventAt.get(i).members;
                            boothsTBL.lastCommitteeForBoothUpdatedEventAt = res.data.get(0).allBoothsPostEventAt.get(i).lastCommitteeForBoothUpdatedEventAt;
                            boothsTBL.save();
                        }


                    }
                }*/
            }else
            {
                LeafPreference.getInstance(GroupDashboardActivityNew.this).setString("GALLERY_POST",res.data.get(0).teamsListCount.galleryPostEventAt);
            }


            ArrayList<UpdateDataEventRes.SubjectCountList> subCountList = res.data.get(0).subjectCountList;
            for (int i = 0; i < subCountList.size(); i++) {
                UpdateDataEventRes.SubjectCountList curr = subCountList.get(i);
                SubjectCountTBL tbl = SubjectCountTBL.getTeamCount(curr.teamId, groupId);
                if (tbl == null) {
                    tbl = new SubjectCountTBL();
                    tbl.teamId = curr.teamId;
                    tbl.groupId = GroupDashboardActivityNew.groupId;
                    tbl.oldSubjectCount = curr.subjectCount;
                }
                tbl.subjectCount = curr.subjectCount;
                tbl.save();
            }
           /* LeafManager leafManager = new LeafManager();
            leafManager.getUpdateEventList(new LeafManager.OnCommunicationListener() {
                @Override
                public void onSuccess(int apiId, BaseResponse response) {
                    AppLog.e(TAG, "onSuccess : " + response.status);
                    UpdateDataEventRes res = (UpdateDataEventRes) response;
                    if (res.data == null || res.data.size() == 0)
                        return;

                    LeafPreference.getInstance(GroupDashboardActivityNew.this).setString(LeafPreference.ACCESS_KEY,res.data.get(0).teamsListCount.accessKey);
                    LeafPreference.getInstance(GroupDashboardActivityNew.this).setString(LeafPreference.SECRET_KEY,res.data.get(0).teamsListCount.secretKey);

                    LeafPreference.getInstance(GroupDashboardActivityNew.this).setString("PREVIEW_URL",res.data.get(0).imagePreviewUrl);

                    ArrayList<UpdateDataEventRes.EventResData> eventList = res.data.get(0).eventList;

                    boolean ifNeedToLogout = false;
                    for (int i = 0; i < eventList.size(); i++) {
                        UpdateDataEventRes.EventResData curr = eventList.get(i);
                        EventTBL eventTBL = null;
                        if (curr.eventType.equalsIgnoreCase("1")) {
                            eventTBL = EventTBL.getGroupEvent(curr.groupId);
                        } else if (curr.eventType.equalsIgnoreCase("2")) {
                            eventTBL = EventTBL.getTeamEvent(curr.groupId, curr.teamId);
                        } else if (curr.eventType.equalsIgnoreCase("3")) {
                            eventTBL = EventTBL.getNotesVideoEvent(curr.groupId, curr.teamId, curr.subjectId);
                        } else if (curr.eventType.equalsIgnoreCase("4")) {
                            eventTBL = EventTBL.getAssignmentEvent(curr.groupId, curr.teamId, curr.subjectId);
                        } else if (curr.eventType.equalsIgnoreCase("6")) {
                            eventTBL = EventTBL.getTestEvent(curr.groupId, curr.teamId, curr.subjectId);
                        } else if (curr.eventType.equalsIgnoreCase("5")) {
                            eventTBL = EventTBL.getAdminEvents(curr.groupId);

                            if (eventTBL == null)
                                AppLog.e(TAG, "eventTBL is  nulll ");
                            else
                                AppLog.e(TAG, "eventTBL eventat : " + eventTBL.eventAt);

                            if (eventTBL == null || MixOperations.isNewEvent(curr.eventAt, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", eventTBL.eventAt)) {
                                ifNeedToLogout = true;
                            }

                        }

                        if (eventTBL == null) {
                            eventTBL = new EventTBL();
                            eventTBL.groupId = curr.groupId;
                            eventTBL.subjectId = curr.subjectId;
                            eventTBL.teamId = curr.teamId;
                            eventTBL.eventType = curr.eventType;
                            eventTBL.eventName = curr.eventName;
                        }
                        eventTBL.insertedId = curr.insertedId;
                        eventTBL.eventAt = curr.eventAt;
                        eventTBL.save();

                    }



                    UpdateDataEventRes.TeamListCount teamCount = res.data.get(0).teamsListCount;
                    TeamCountTBL groupCount = TeamCountTBL.getByTypeAndGroup("GROUP", groupId);
                    if (groupCount == null) {
                        groupCount = new TeamCountTBL();
                        groupCount.typeOfTeam = "GROUP";
                        groupCount.oldCount = teamCount.schoolGroupCount;
                    } else {
                        if (groupCount.oldCount == 1 && teamCount.schoolGroupCount > 1) {
                            ifNeedToLogout = true;
                        } else if (groupCount.oldCount > 1 && teamCount.schoolGroupCount == 1) {
                            ifNeedToLogout = true;
                        } else if (groupCount.oldCount != teamCount.schoolGroupCount) {
                            LeafPreference.getInstance(GroupDashboardActivityNew.this).setBoolean("group_list_refresh", true);
                        }
                        groupCount.oldCount = teamCount.schoolGroupCount;
                    }
                    groupCount.lastInsertedTeamTime = teamCount.lastInsertedTeamTime;
                    groupCount.lastNotificationAt = teamCount.lastNotificationAt;
                    groupCount.count = teamCount.schoolGroupCount;
                    groupCount.groupId = groupId;
                    groupCount.save();

                    if (ifNeedToLogout) {
                        showLogoutPopup();
                        return;
                    }

                    TeamCountTBL liveCount = TeamCountTBL.getByTypeAndGroup("LIVE", groupId);
                    if (liveCount == null) {
                        liveCount = new TeamCountTBL();
                        liveCount.typeOfTeam = "LIVE";
                        liveCount.oldCount = teamCount.liveClassTeamCount;
                    }
                    liveCount.lastNotificationAt = teamCount.lastNotificationAt;
                    liveCount.lastInsertedTeamTime = teamCount.lastInsertedTeamTime;
                    liveCount.count = teamCount.liveClassTeamCount;
                    liveCount.groupId = groupId;
                    liveCount.save();


                    TeamCountTBL allCount = TeamCountTBL.getByTypeAndGroup("ALL", groupId);
                    if (allCount == null) {
                        allCount = new TeamCountTBL();
                        allCount.typeOfTeam = "ALL";
                        allCount.oldCount = teamCount.getAllClassTeamCount;
                    }
                    allCount.lastNotificationAt = teamCount.lastNotificationAt;
                    allCount.lastInsertedTeamTime = teamCount.lastInsertedTeamTime;
                    allCount.count = teamCount.getAllClassTeamCount;
                    allCount.groupId = groupId;
                    allCount.save();


                    TeamCountTBL dashboardCount = TeamCountTBL.getByTypeAndGroup("DASHBOARD", groupId);
                    if (dashboardCount == null) {
                        dashboardCount = new TeamCountTBL();
                        dashboardCount.typeOfTeam = "DASHBOARD";
                        dashboardCount.oldCount = teamCount.dashboardTeamCount;
                    }
                    dashboardCount.lastNotificationAt = teamCount.lastNotificationAt;
                    dashboardCount.lastInsertedTeamTime = teamCount.lastInsertedTeamTime;
                    dashboardCount.count = teamCount.dashboardTeamCount;
                    dashboardCount.groupId = groupId;

                    Fragment currFrag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                    if (currFrag instanceof BaseTeamFragmentv2) {
                        boolean apiCall = false;
                        boolean apiCallNotification = false;
                        if (dashboardCount.lastApiCalled != 0) {
                            if (MixOperations.isNewEvent(dashboardCount.lastInsertedTeamTime, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", dashboardCount.lastApiCalled)) {
                                apiCall = true;
                            }
                        }

                        if (dashboardCount.lastApiCalledNotification != 0) {
                            if (MixOperations.isNewEvent(dashboardCount.lastNotificationAt, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", dashboardCount.lastApiCalledNotification)) {
                                apiCallNotification = true;
                            }
                        }

                        if (dashboardCount.oldCount != dashboardCount.count) {
                            dashboardCount.oldCount = dashboardCount.count;
                            dashboardCount.save();
                            apiCall = true;
                        }
                     //   notificationApiCall = apiCallNotification;
                        ((BaseTeamFragmentv2) currFrag).checkAndRefresh(apiCall);
                        ((BaseTeamFragmentv2) currFrag).checkAndRefreshNotification(apiCallNotification);

                    }

                    if (BuildConfig.AppCategory.equalsIgnoreCase("constituency"))
                    {
                        LeafPreference.getInstance(GroupDashboardActivityNew.this).setString("BANNER_API",res.data.get(0).bannerPostEventAt);
                        LeafPreference.getInstance(GroupDashboardActivityNew.this).setString("FEED_API",res.data.get(0).notificationFeedEventAt);
                        LeafPreference.getInstance(GroupDashboardActivityNew.this).setString("PROFILE_API",res.data.get(0).myProfileUpdatedEventAt);
                        LeafPreference.getInstance(GroupDashboardActivityNew.this).setString("MY_TEAM_UPDATE",res.data.get(0).lastUpdatedTeamTime);
                        LeafPreference.getInstance(GroupDashboardActivityNew.this).setString("MY_TEAM_INSERT",res.data.get(0).lastInsertedTeamTime);
                        LeafPreference.getInstance(GroupDashboardActivityNew.this).setString("BOOTH_INSERT",res.data.get(0).lastInsertedBoothTeamTime);

                        if (HomeTeamDataTBL.getAll().size() > 0)
                        {
                            HomeTeamDataTBL.deleteAll();
                        }

                        if (res.data.get(0).homeTeamData != null)
                        {
                             for (int i = 0;i<res.data.get(0).homeTeamData.size();i++)
                            {
                                HomeTeamDataTBL homeTeamDataTBL = new HomeTeamDataTBL();
                                homeTeamDataTBL.teamId = res.data.get(0).homeTeamData.get(i).teamId;
                                homeTeamDataTBL.members = res.data.get(0).homeTeamData.get(i).members;
                                homeTeamDataTBL.lastTeamPostAt = res.data.get(0).homeTeamData.get(i).lastTeamPostAt;

                                homeTeamDataTBL.save();
                            }
                        }


                        if (BoothPostEventTBL.getAll().size() > 0)
                        {
                            BoothPostEventTBL.deleteAll();
                        }

                        if (res.data.get(0).allBoothsPostEventAt != null)
                        {
                            for (int i = 0;i<res.data.get(0).allBoothsPostEventAt.size();i++)
                            {
                                BoothPostEventTBL boothPostEventTBL = new BoothPostEventTBL();
                                boothPostEventTBL.boothId = res.data.get(0).allBoothsPostEventAt.get(i).boothId;
                                boothPostEventTBL.members = res.data.get(0).allBoothsPostEventAt.get(i).members;
                                boothPostEventTBL.lastBoothPostAt = res.data.get(0).allBoothsPostEventAt.get(i).lastBoothPostAt;

                                boothPostEventTBL.save();
                            }
                        }


                    }

                    if (currFrag instanceof BaseTeamFragmentv3) {

            if (dashboardCount.lastApiCalled != 0) {
                            if (MixOperations.isNewEvent(dashboardCount.lastInsertedTeamTime, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", dashboardCount.lastApiCalled)) {
                                apiCall = true;
                            }
                        }

                        if (dashboardCount.lastApiCalledNotification != 0) {
                            if (MixOperations.isNewEvent(dashboardCount.lastNotificationAt, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", dashboardCount.lastApiCalledNotification)) {
                                apiCallNotification = true;
                            }
                        }

                        if (dashboardCount.oldCount != dashboardCount.count) {
                            dashboardCount.oldCount = dashboardCount.count;
                            dashboardCount.save();
                            apiCall = true;
                        }
                        notificationApiCall = apiCallNotification;
                        ((BaseTeamFragmentv3) currFrag).checkAndRefresh(apiCall);
                        ((BaseTeamFragmentv3) currFrag).checkAndRefreshNotification(apiCallNotification);



                        if (mGroupItem.isBoothWorker != res.data.get(0).roleData.isBoothWorker)
                        {
                           showLogoutPopup();
                        }
                        else if (mGroupItem.isAdmin != res.data.get(0).roleData.isAdmin)
                        {
                            showLogoutPopup();
                        }
                        else if (mGroupItem.isPublic != res.data.get(0).roleData.isPublic)
                        {
                            showLogoutPopup();
                        }
                        else if (mGroupItem.isPartyTaskForce != res.data.get(0).roleData.isPartyTaskForce)
                        {
                            showLogoutPopup();
                        }
                        else if (mGroupItem.isDepartmentTaskForce != res.data.get(0).roleData.isDepartmentTaskForce)
                        {
                            showLogoutPopup();
                        }
                        else if (mGroupItem.isBoothPresident != res.data.get(0).roleData.isBoothPresident)
                        {
                            showLogoutPopup();
                        }
                        else if (mGroupItem.isAuthorizedUser != res.data.get(0).roleData.isAuthorizedUser)
                        {
                            showLogoutPopup();
                        }

                        List<BaseTeamTableV2> teamList = BaseTeamTableV2.getTeamList(groupId);

                        if (teamList.size() > 0)
                        {
                            if (MixOperations.isNewEvent(LeafPreference.getInstance(getApplicationContext()).getString("MY_TEAM_UPDATE"), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", teamList.get(teamList.size()-1)._now)) {
                                ((BaseTeamFragmentv3) currFrag).checkAndRefresh(true);
                            }

                            if (MixOperations.isNewEvent(LeafPreference.getInstance(getApplicationContext()).getString("MY_TEAM_INSERT"), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", teamList.get(teamList.size()-1)._now)) {
                                ((BaseTeamFragmentv3) currFrag).checkAndRefresh(true);
                            }
                        }


                        List<BannerTBL> bannerTBL = BannerTBL.getBanner(groupId);

                        if (bannerTBL.size() > 0)
                        {
                            if (MixOperations.isNewEvent(LeafPreference.getInstance(getApplicationContext()).getString("BANNER_API"), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", bannerTBL.get(bannerTBL.size()-1)._now)) {
                                ((BaseTeamFragmentv3) currFrag).bannerListApiCall();
                            }
                        }


                        List<NotificationTable> notificationTableList = NotificationTable.getAllNotificationList(groupId,1);

                        if (notificationTableList.size() > 0)
                        {
                            if (MixOperations.isNewEvent(LeafPreference.getInstance(getApplicationContext()).getString("FEED_API"), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", notificationTableList.get(notificationTableList.size()-1)._now)) {
                                ((BaseTeamFragmentv3) currFrag).checkAndRefreshNotification(true);
                                notificationApiCall = true;
                            }
                        }

                        if (res.data.get(0).lastInsertedBoothTeamTime != null) {
                            if (BoothsTBL.getBoothList(groupId).size() > 0) {
                                List<BoothsTBL> boothsTBLList = BoothsTBL.getLastBoothList(groupId);

                                if (MixOperations.isNewEvent(LeafPreference.getInstance(getApplicationContext()).getString("BOOTH_INSERT"), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", boothsTBLList.get(boothsTBLList.size() - 1)._now)) {
                                    AppLog.e(TAG,"new Booth Add");
                                    BoothsTBL.deleteBooth(groupId);
                                }
                            }
                        }

                    }

                    dashboardCount.save();

                    ArrayList<UpdateDataEventRes.SubjectCountList> subCountList = res.data.get(0).subjectCountList;
                    for (int i = 0; i < subCountList.size(); i++) {
                        UpdateDataEventRes.SubjectCountList curr = subCountList.get(i);
                        SubjectCountTBL tbl = SubjectCountTBL.getTeamCount(curr.teamId, groupId);
                        if (tbl == null) {
                            tbl = new SubjectCountTBL();
                            tbl.teamId = curr.teamId;
                            tbl.groupId = GroupDashboardActivityNew.groupId;
                            tbl.oldSubjectCount = curr.subjectCount;
                        }
                        tbl.subjectCount = curr.subjectCount;
                        tbl.save();
                    }
                }

                @Override
                public void onFailure(int apiId, String msg) {
                    AppLog.e(TAG, "onFailure : " + msg);
                }

                @Override
                public void onException(int apiId, String msg) {
                    AppLog.e(TAG, "onException : " + msg);
                }
            }, groupId);
            return null;*/

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (ifNeedToLogout) {
                showLogoutPopup();
            }

            Fragment currFrag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (currFrag instanceof BaseTeamFragmentv2)
            {
                boolean apiCall = false;
                boolean apiCallNotification = false;

                if (MixOperations.isNewEvent(dashboardCount.lastInsertedTeamTime, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", dashboardCount.lastApiCalled)) {
                    apiCall = true;
                }

             /*   if (dashboardCount.lastApiCalled != 0) {

                }

                if (dashboardCount.lastApiCalledNotification != 0) {

                }*/

                List<NotificationTable> notificationTableList = NotificationTable.getAllNotificationList(groupId,1);

                Log.e(TAG,"notificationTableList"+notificationTableList.size());

                if (notificationTableList.size() > 0)
                {
                    Log.e(TAG,"dashboardCount.lastNotificationAt "+dashboardCount.lastNotificationAt);
                    Log.e(TAG,"notificationTableList.lastNotificationAt "+Long.parseLong(notificationTableList.get(notificationTableList.size()-1)._now));

                    if (MixOperations.isNewEvent(dashboardCount.lastNotificationAt, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Long.parseLong(notificationTableList.get(notificationTableList.size()-1)._now))) {
                        apiCallNotification = true;
                    }
                }


                if (dashboardCount.oldCount != dashboardCount.count) {
                    dashboardCount.oldCount = dashboardCount.count;
                    dashboardCount.save();
                    apiCall = true;
                }
                notificationApiCall = apiCallNotification;
                ((BaseTeamFragmentv2) currFrag).checkAndRefresh(apiCall);
                ((BaseTeamFragmentv2) currFrag).checkAndRefreshNotification(apiCallNotification);

            }
            if (currFrag instanceof BaseTeamFragmentv3)
            {

                        /*if (dashboardCount.lastApiCalled != 0) {
                            if (MixOperations.isNewEvent(dashboardCount.lastInsertedTeamTime, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", dashboardCount.lastApiCalled)) {
                                apiCall = true;
                            }
                        }

                        if (dashboardCount.lastApiCalledNotification != 0) {
                            if (MixOperations.isNewEvent(dashboardCount.lastNotificationAt, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", dashboardCount.lastApiCalledNotification)) {
                                apiCallNotification = true;
                            }
                        }

                        if (dashboardCount.oldCount != dashboardCount.count) {
                            dashboardCount.oldCount = dashboardCount.count;
                            dashboardCount.save();
                            apiCall = true;
                        }
                        notificationApiCall = apiCallNotification;*/
                      /*  ((BaseTeamFragmentv3) currFrag).checkAndRefresh(apiCall);
                        ((BaseTeamFragmentv3) currFrag).checkAndRefreshNotification(apiCallNotification);*/


                if(mGroupItem==null){
                    return;
                }
                if(res.data==null){
                    return;
                }
                if(res.data.size()>0){
                    if(res.data.get(0).roleData==null){
                        return;
                    }
                }

                if (mGroupItem.isBoothWorker != res.data.get(0).roleData.isBoothWorker)
                {
                    showLogoutPopup();
                }
                else if (mGroupItem.isAdmin != res.data.get(0).roleData.isAdmin)
                {
                    showLogoutPopup();
                }
                else if (mGroupItem.isPublic != res.data.get(0).roleData.isPublic)
                {
                    showLogoutPopup();
                }
                else if (mGroupItem.isPartyTaskForce != res.data.get(0).roleData.isPartyTaskForce)
                {
                    showLogoutPopup();
                }
                else if (mGroupItem.isDepartmentTaskForce != res.data.get(0).roleData.isDepartmentTaskForce)
                {
                    showLogoutPopup();
                }
                else if (mGroupItem.isBoothPresident != res.data.get(0).roleData.isBoothPresident)
                {
                    showLogoutPopup();
                }
                else if (mGroupItem.isAuthorizedUser != res.data.get(0).roleData.isAuthorizedUser)
                {
                    showLogoutPopup();
                }

                List<BaseTeamTableV2> teamList = BaseTeamTableV2.getTeamList(groupId);

                if (teamList.size() > 0)
                {

                    if (MixOperations.isNewEventUpdate(LeafPreference.getInstance(getApplicationContext()).getString("MY_TEAM_UPDATE"), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", teamList.get(teamList.size()-1).update_team)) {
                        ((BaseTeamFragmentv3) currFrag).checkAndRefresh(true);
                    }

                    if (MixOperations.isNewEventUpdate(LeafPreference.getInstance(getApplicationContext()).getString("MY_TEAM_INSERT"), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", teamList.get(teamList.size()-1)._now)) {
                        ((BaseTeamFragmentv3) currFrag).checkAndRefresh(true);
                    }
                }

                List<BannerTBL> bannerTBL = BannerTBL.getBanner(groupId);

                if (bannerTBL.size() > 0)
                {
                    if (MixOperations.isNewEventUpdate(LeafPreference.getInstance(getApplicationContext()).getString("BANNER_API"), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", bannerTBL.get(bannerTBL.size()-1)._now)) {
                        BannerTBL.deleteBanner(groupId);
                        ((BaseTeamFragmentv3) currFrag).bannerListApiCall();
                    }
                }

                List<NotificationTable> notificationTableList = NotificationTable.getAllNotificationList(groupId,1);

                if (notificationTableList.size() > 0)
                {
                    if (MixOperations.isNewEventUpdate(LeafPreference.getInstance(getApplicationContext()).getString("FEED_API"), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", notificationTableList.get(notificationTableList.size()-1)._now)) {
                        ((BaseTeamFragmentv3) currFrag).checkAndRefreshNotification(true);
                        notificationApiCall = true;
                    }
                }

                if (res.data.get(0).lastInsertedBoothTeamTime != null) {
                    if (BoothsTBL.getBoothList(groupId).size() > 0) {
                        List<BoothsTBL> boothsTBLList = BoothsTBL.getLastBoothList(groupId);

                        if (MixOperations.isNewEventUpdate(LeafPreference.getInstance(getApplicationContext()).getString("BOOTH_INSERT"), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", boothsTBLList.get(boothsTBLList.size() - 1)._now)) {
                            AppLog.e(TAG,"new Booth Add");
                            BoothsTBL.deleteBooth(groupId);
                        }
                    }
                }
            }
        }
    }

    private void showLogoutPopup() {
        SMBDialogUtils.showSMBDialogOK(GroupDashboardActivityNew.this, getResources().getString(R.string.smb_dialog_permission_change), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logoutWithoutEvents();
            }
        });
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

    /*Not USED*/
    private void sendNotification(String messageBody, String title) {

        AppLog.e(TAG,"sendNotification() called");

        String CHANNEL_ID = "gruppie_02";// The id of the channel.

        Intent intent = new Intent(this, GroupDashboardActivityNew.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_noti)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setShowBadge(true);
            AppLog.e(TAG,"canShowBadge :"+mChannel.canShowBadge());
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
        }
        assert notificationManager != null;
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void showHomeInfowWindow() {

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_first_time", true)) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("is_first_time", false).apply();

            if (isPost) {
                showtips = new ShowTipsBuilder(this)
                        .setTarget(tapView2)
                        .setDescription("Click to Add Post")
                        .setBackgroundAlpha(100)
                        .setCloseButtonTextColor(getResources().getColor(R.color.colorPrimaryBg))
                        .build();
                showtips.show(this);

                showtips.setCallback(new ShowTipsViewInterface() {
                    @Override
                    public void gotItClicked() {
                        showtips = new ShowTipsBuilder(GroupDashboardActivityNew.this)
                                .setTarget(tapView3)
                                .setDescription("Click to Add Friends")
                                .setBackgroundAlpha(100)
                                .setCloseButtonTextColor(getResources().getColor(R.color.colorPrimaryBg))
                                .build();
                        showtips.show(GroupDashboardActivityNew.this);
                    }
                });
            } else {
                showtips = new ShowTipsBuilder(this)
                        .setTarget(tapView2)
                        .setDescription("Click to Add Friends")
                        .setBackgroundAlpha(100)
                        .setCloseButtonTextColor(getResources().getColor(R.color.colorPrimaryBg))
                        .build();
                showtips.show(this);
            }

        }
    }

    private void calculateDisplayMatrix() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        AppLog.e(TAG, "Screen widht:" + width + ",height:" + height);
        Constants.screen_width = width - (dpToPx(10));
        Constants.screen_height = height;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private void init() {


        LoginRequest request = new Gson().fromJson(LeafPreference.getInstance(this).getString(LeafPreference.LOGIN_REQ), new TypeToken<LoginRequest>() {}.getType());

        AppLog.e(TAG, "request : " + new Gson().toJson(request));

        mGroupItem = new Gson().fromJson(LeafPreference.getInstance(this).getString(Constants.GROUP_DATA), GroupItem.class);
        AppLog.e(TAG, "mGroupItem : " + new Gson().toJson(mGroupItem));

        AppLog.e(TAG, "VIDEO_CALL : " + LeafPreference.getInstance(this).getString("LIVE_CALL"));

        tv_toolbar_icon = findViewById(R.id.iv_toolbar_icon);
        tv_toolbar_default = findViewById(R.id.iv_toolbar_default);
        tvToolbar = findViewById(R.id.tv_toolbar_title_dashboard);
        tv_Desc = findViewById(R.id.tv_Desc);
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.e("Dashboard", "NavigationOnClicked");
                hide_keyboard();
                onBackPressed();
            }
        });
        LeafPreference.getInstance(this).setBoolean("IS_GROUP_ICON_CHANGE", false);

        manager = new LeafManager();

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            transliterator = Transliterator.getInstance("Latin-Kannada");
        }*/
    }

    //NOFIREBASEDATABASE
   /* private void initFirebaseAuth(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()==null)
        {
            mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        AppLog.e(TAG, "isSuccessful : true");
                    } else {
                        AppLog.e(TAG, "isSuccessful : false");
                    }
                }
            });
        }
        else
        {

        }
    }*/
    @OnClick({R.id.rlMore, R.id.llProfile,R.id.llBookmark, R.id.llPeople, R.id.llSubject,R.id.llBothRegister,R.id.llBothCoordinateRegister, R.id.llFamily,R.id.llIssueRegister, R.id.llSubject2, R.id.llDiscuss, R.id.llJoinGruppie, R.id.llAuthorizedUser, R.id.llAllUsers,  R.id.llDoubt, R.id.llAboutGroup, R.id.llAddFriend, R.id.llArchiveTeam, R.id.llNotification, R.id.llClass, R.id.llBusRegister, R.id.llAttendanceReport, R.id.llStaffReg,R.id.llMakeAdmin,R.id.llMyTeam,R.id.iv_toolbar_icon,R.id.iv_toolbar_default})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.llProfile:
                if (isConnectionAvailable()) {
                    Intent intent;
                    if("constituency".equalsIgnoreCase(mGroupItem.category)){
                        intent = new Intent(this, ProfileConstituencyActivity.class);
                    }else {
                        intent = new Intent(this, ProfileActivity2.class);
                    }
                    startActivity(intent);
                } else {
                    showNoNetworkMsg();
                }
                break;


            case R.id.iv_toolbar_icon:
                if (isConnectionAvailable()) {
                    Intent intent;
                    intent = new Intent(this, ProfileConstituencyActivity.class);
                    startActivity(intent);
                } else {
                    showNoNetworkMsg();
                }
                break;

            case R.id.iv_toolbar_default:
                if (isConnectionAvailable()) {
                    Intent intent;
                    intent = new Intent(this, ProfileConstituencyActivity.class);
                    startActivity(intent);
                } else {
                    showNoNetworkMsg();
                }
                break;

            case R.id.llJoinGruppie:
                joinGruppieEvent();
                break;
            case R.id.llAuthorizedUser:
                if (isConnectionAvailable()) {
                    Intent intent = new Intent(this, AuthorizedUserActivity.class);
                    intent.putExtra("id", groupId);
                    startActivity(intent);
                } else {
                    showNoNetworkMsg();
                }
                break;
            case R.id.llAllUsers:
                if (isConnectionAvailable()) {
                    Intent intent = new Intent(this, AllUserListActivity.class);
                    intent.putExtra("id", groupId);
                    intent.putExtra("change", false);
                    startActivity(intent);
                } else {
                    showNoNetworkMsg();
                }
                break;

            case R.id.llMakeAdmin:
                rlMore.setVisibility(View.GONE);
                makeAdminClick();
                return;


            case R.id.llMyTeam:
                rlMore.setVisibility(View.GONE);
                myTeamClick();
                return;



            case R.id.llArchiveTeam:
                if (isConnectionAvailable()) {
                    Intent intent = new Intent(this, ArchiveTeamActivity.class);
                    intent.putExtra("id", groupId);
                    startActivity(intent);
                } else {
                    showNoNetworkMsg();
                }
                break;
            case R.id.llDoubt:
                if (isConnectionAvailable()) {
                    Intent intent = new Intent(this, QuestionListActivity.class);
                    intent.putExtra("id", groupId);
                    startActivity(intent);
                } else {
                    showNoNetworkMsg();
                }
                break;
            case R.id.llNotification:

                AppLog.e(TAG, "llNotification: called");

                if (isConnectionAvailable()) {
                    Intent intent = new Intent(this, NotificationListActivity.class);
                    intent.putExtra("id", groupId);
                    intent.putExtra("title", mGroupItem.getName());
                    startActivity(intent);
                } else {
                    showNoNetworkMsg();
                }
                break;

            case R.id.llAboutGroup:
                if (isConnectionAvailable()) {
                    Intent intent = new Intent(this, AboutGroupActivity2.class);
                    intent.putExtra("id", groupId);
                    intent.putExtra("title", mGroupItem.getName());
                    startActivity(intent);
                } else {
                    showNoNetworkMsg();
                }
                break;
            case R.id.llAddFriend:
                /*if (isConnectionAvailable()) {
                    Intent intent = new Intent(this, AddFriendActivity.class);
                    intent.putExtra("id", groupId);
                    intent.putExtra("invite", true);
                    startActivity(intent);
                } else {
                    showNoNetworkMsg();
                }*/
                break;
            case R.id.llDiscuss:
                if (isConnectionAvailable()) {
                    startActivity(new Intent(this, TeamListActivity.class));
                } else {
                    showNoNetworkMsg();
                }

                break;
            case R.id.llPeople:
                if (isConnectionAvailable()) {
                    startActivity(new Intent(this, PeopleActivity.class));
                } else {
                    showNoNetworkMsg();
                }

                break;
            case R.id.llClass:
                if (isConnectionAvailable()) {
                    startActivity(new Intent(this, ClassActivity.class));
                } else {
                    showNoNetworkMsg();
                }
                break;
            case R.id.llBothRegister:
                if (isConnectionAvailable()) {
                    Intent intent = new Intent(this, BoothActivity.class);
                    intent.putExtra("type","MEMBER");
                    startActivity(intent);
                } else {
                    showNoNetworkMsg();
                }
                break;
            case R.id.llBothCoordinateRegister:
                if (isConnectionAvailable()) {
                    Intent intent = new Intent(this, BoothActivity.class);
                    intent.putExtra("type","COORDINATE");
                    startActivity(intent);
                } else {
                    showNoNetworkMsg();
                }
                break;
            case R.id.llFamily:
                if (isConnectionAvailable()) {
                    startActivity(new Intent(this, FamilyMemberActivity.class));
                } else {
                    showNoNetworkMsg();
                }
                break;
            case R.id.llBookmark:
                if (isConnectionAvailable()) {
                    Intent intent = new Intent(this, FavoritePostActivity.class);
                    intent.putExtra("id", groupId);
                    startActivity(intent);
                } else {
                    showNoNetworkMsg();
                }
                break;


            case R.id.llSubject2:
                if (isConnectionAvailable()) {
                    startActivity(new Intent(this, ClassActivity2.class));
                } else {
                    showNoNetworkMsg();
                }
                break;
            case R.id.llIssueRegister:
                if (isConnectionAvailable()) {
                    startActivity(new Intent(this, IssueActivity.class));
                } else {
                    showNoNetworkMsg();
                }
                break;

           /* case R.id.fabAddTicket:

                if (isConnectionAvailable()) {
                    startActivity(new Intent(this, AddTicketActivity.class));
                } else {
                    showNoNetworkMsg();
                }
                break;*/

            case R.id.llStaffReg:
                if (isConnectionAvailable()) {
                    Intent intent = new Intent(this, StaffActivity.class);
                    intent.putExtra("isAdmin", isAdmin);
                    startActivity(intent);
                } else {
                    showNoNetworkMsg();
                }
                break;

            case R.id.llBusRegister:
                if (isConnectionAvailable()) {
                    startActivity(new Intent(this, BusActivity.class));
                } else {
                    showNoNetworkMsg();
                }
                break;
            case R.id.llAttendanceReport:
                if (isConnectionAvailable()) {
                    startActivity(new Intent(this, AttendanceReportActivity.class));
                } else {
                    showNoNetworkMsg();
                }
                break;

            case R.id.rlMore:
                rlMore.setVisibility(View.GONE);
                tabLayout.getTabAt(0).select();
                return;


        }
        rlMore.setVisibility(View.VISIBLE);
        //tabLayout.getTabAt(1).select();
    }

    private void createTabIcons() {
        for (int i = 0; i < tabText.length; i++) {
            View v = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            TextView tv = (TextView) v.findViewById(R.id.tab_badge);
            tv.setText(tabText[i]);
            ImageView img = (ImageView) v.findViewById(R.id.tab_icon);

            if (i == 0) {
                img.setImageResource(tabIcon[i]);
                img.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
                tv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            } else {
                img.setImageResource(tabIcongray[i]);
                img.setColorFilter(ContextCompat.getColor(this, R.color.colorTextLight), android.graphics.PorterDuff.Mode.SRC_IN);
                tv.setTextColor(ContextCompat.getColor(this, R.color.colorTextLight));
            }
            tabLayout.getTabAt(i).setCustomView(v);
        }
    }


    public void updateTabIcons(int pos) {

        Log.e(TAG,"position Tab"+pos);

        for (int i = 0; i < tabText.length; i++) {
            View v = tabLayout.getTabAt(i).getCustomView();
            TextView tv = (TextView) v.findViewById(R.id.tab_badge);
            tv.setText(tabText[i]);
            ImageView img = (ImageView) v.findViewById(R.id.tab_icon);
            img.setImageResource(tabIcon[i]);
            if (i == pos) {
                img.setImageResource(tabIcon[i]);
                img.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
                tv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            } else {
                img.setImageResource(tabIcongray[i]);
                img.setColorFilter(ContextCompat.getColor(this, R.color.colorTextLight), android.graphics.PorterDuff.Mode.SRC_IN);
                tv.setTextColor(ContextCompat.getColor(this, R.color.colorTextLight));
            }
            tabLayout.getTabAt(i).setCustomView(v);
        }
    }

    private void setGroupData(boolean isToolBarTextSet) {

        groupId = mGroupItem.getGroupId();
        total_user = mGroupItem.totalUsers + "";
        mAllowPostAll = mGroupItem.allowPostAll;
        postCount = mGroupItem.groupPostUnreadCount;
        group_name = mGroupItem.getName();
        mAllowPostShare = mGroupItem.isPostShareAllowed;
        mAllowAdminChange = mGroupItem.isAdminChangeAllowed;
        mAllowPostQuestion = mGroupItem.allowPostQuestion;
        image = mGroupItem.image;
        adminPhone = mGroupItem.adminPhone;
        allowedToAddUser = mGroupItem.allowedToAddUser;
        postCount = mGroupItem.groupPostUnreadCount;
        groupCategory = mGroupItem.category;

        GroupDashboardActivityNew.isAdmin = mGroupItem.isAdmin;
        GroupDashboardActivityNew.isPost = mGroupItem.isAdmin || mGroupItem.canPost || mGroupItem.allowPostAll;

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean("isAdmin", isAdmin).apply();
        prefs.edit().putString("id", mGroupItem.getGroupId() + "").apply();

        if (isToolBarTextSet) {
            tvToolbar.setText(mGroupItem.name);
            tv_Desc.setVisibility(View.GONE);
        }
    }

    private void enableOptions() {

        AppLog.e(TAG,"mGroupItem.category"+mGroupItem.category);
        AppLog.e(TAG,"mGroupItem.isAdmin"+mGroupItem.isAdmin);

        //remove 3 option view discuss,my people and bookmarks 3-1-22
        if (mGroupItem.isAdmin) {
            llDiscuss.setVisibility(View.GONE);
            llBothCoordinateRegister.setVisibility(View.GONE);
        } else {
            llDiscuss.setVisibility(View.GONE);
        }

        if (!mGroupItem.canPost || !mGroupItem.allowPostQuestion)
            llDoubt.setVisibility(View.GONE);

        if (mGroupItem.isAdmin || mGroupItem.canPost) {
            llClass.setVisibility(View.VISIBLE);
            llSubject2.setVisibility(View.VISIBLE);
            llStaffReg.setVisibility(View.VISIBLE);
            llAttendanceReport.setVisibility(View.VISIBLE);
            llBusRegister.setVisibility(View.VISIBLE);
        }

        if ("constituency".equalsIgnoreCase(mGroupItem.category)) {
            llClass.setVisibility(View.GONE);
            llSubject2.setVisibility(View.GONE);
            llStaffReg.setVisibility(View.GONE);
            llBusRegister.setVisibility(View.GONE);
            llAttendanceReport.setVisibility(View.GONE);

            tvAbout.setText(getResources().getString(R.string.lbl_about_constituency));

         /*   if (mGroupItem.name !=null && mGroupItem.name.equalsIgnoreCase("Gruppie MLA"))
            {

            }*/

            if (mGroupItem.isAdmin)
            {
                llMakeAdmin.setVisibility(View.VISIBLE);
            }
            else
            {
                llMakeAdmin.setVisibility(View.GONE);
            }

            if (mGroupItem.isAdmin || mGroupItem.isBoothPresident || mGroupItem.isBoothWorker) {

                llMyTeam.setVisibility(View.VISIBLE);
            }
            else
            {
                llMyTeam.setVisibility(View.GONE);
            }


            if (mGroupItem.isAdmin || mGroupItem.isPartyTaskForce || mGroupItem.isDepartmentTaskForce || mGroupItem.isBoothPresident || mGroupItem.isBoothMember) {
                llArchiveTeam.setVisibility(View.VISIBLE);

            }
            else
            {
                llArchiveTeam.setVisibility(View.GONE);
            }


            llDiscuss.setVisibility(View.GONE);
            llPeople.setVisibility(View.GONE);

            llFamily.setVisibility(View.GONE);
     //       llAdTicket.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);



            if (mGroupItem.canPost) {

                llIssueRegister.setVisibility(View.VISIBLE);
                llBothRegister.setVisibility(View.VISIBLE);
                llBookmark.setVisibility(View.VISIBLE);

                if (mGroupItem.isAdmin)
                {
                    llBothCoordinateRegister.setVisibility(View.GONE);
                }

                tabText = new String[4];
                tabIcon = new int[4];
                tabIcongray = new int[4];

                tabIcon[0] = R.drawable.icon_home;
                tabIcongray[0] = R.drawable.icon_home_gray;
                tabText[0] = getResources().getString(R.string.lbl_home);
                tabLayout.addTab(tabLayout.newTab().setText(tabText[0]));

                tabIcon[1] = R.drawable.icon_booth_home;
                tabIcongray[1] = R.drawable.icon_booth_grey;
                tabText[1] = getResources().getString(R.string.lbl_Booths);
                tabLayout.addTab(tabLayout.newTab().setText(tabText[1]));

                tabIcon[2] = R.drawable.icon_public_forum;
                tabIcongray[2] = R.drawable.icon_public_forum_grey;
                tabText[2] = getResources().getString(R.string.lbl_public_forum);
                tabLayout.addTab(tabLayout.newTab().setText(tabText[2]));

                tabIcon[3] = R.drawable.icon_more_tab;
                tabIcongray[3] = R.drawable.icon_more_tab_gray;
                tabText[3] = getResources().getString(R.string.lbl_more);
                tabLayout.addTab(tabLayout.newTab().setText(tabText[3]));
            } else if (mGroupItem.isBoothPresident) {
                tabText = new String[3];
                tabIcon = new int[3];
                tabIcongray = new int[3];

                tabIcon[0] = R.drawable.icon_home;
                tabIcongray[0] = R.drawable.icon_home_gray;
                tabText[0] = getResources().getString(R.string.lbl_home);
                tabLayout.addTab(tabLayout.newTab().setText(tabText[0]));

                tabIcon[1] = R.drawable.icon_public_forum;
                tabIcongray[1] = R.drawable.icon_public_forum_grey;
                tabText[1] = getResources().getString(R.string.lbl_public_forum);
                tabLayout.addTab(tabLayout.newTab().setText(tabText[1]));

                tabIcon[2] = R.drawable.icon_more_tab;
                tabIcongray[2] = R.drawable.icon_more_tab_gray;
                tabText[2] = getResources().getString(R.string.lbl_more);
                tabLayout.addTab(tabLayout.newTab().setText(tabText[2]));
            } else {
                tabText = new String[2];
                tabIcon = new int[2];
                tabIcongray = new int[2];

                tabIcon[0] = R.drawable.icon_home;
                tabIcongray[0] = R.drawable.icon_home_gray;
                tabText[0] = getResources().getString(R.string.lbl_home);
                tabLayout.addTab(tabLayout.newTab().setText(tabText[0]));

                tabIcon[1] = R.drawable.icon_more_tab;
                tabIcongray[1] = R.drawable.icon_more_tab_gray;
                tabText[1] = getResources().getString(R.string.lbl_more);
                tabLayout.addTab(tabLayout.newTab().setText(tabText[1]));
            }

        }

        else {

            tabText = new String[2];
            tabIcon = new int[2];
            tabIcongray = new int[2];

            tabIcon[0] = R.drawable.icon_home;
            tabIcongray[0] = R.drawable.icon_home_gray;
            tabText[0] = getResources().getString(R.string.lbl_home);
            tabLayout.addTab(tabLayout.newTab().setText(tabText[0]));

            tabIcon[1] = R.drawable.icon_more_tab;
            tabIcongray[1] = R.drawable.icon_more_tab_gray;
            tabText[1] = getResources().getString(R.string.lbl_more);
            tabLayout.addTab(tabLayout.newTab().setText(tabText[1]));

            if (mGroupItem.canPost) {
                tabLayout.setVisibility(View.VISIBLE);
            } else {
                tabLayout.setVisibility(View.GONE);
            }
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                AppLog.e(TAG, "TAb Chaged Called");
                if (tabIcon.length == 4) {
                    switch (tab.getPosition()) {
                        case 0:
                            rlMore.setVisibility(View.GONE);
                            HomeClick();
                            break;
                        case 1:
                            rlMore.setVisibility(View.GONE);
                            // Booth Click
                            boothClick();
                            break;
                        case 2:
                            rlMore.setVisibility(View.GONE);

                            // Public Forum CLick
                            publicForumClick();
                            break;
                        case 3:
                            AppLog.e(TAG, "Set More Visible triggered");
                            rlMore.setVisibility(View.VISIBLE);
                            break;
                    }
                } else if (tabIcon.length == 3) {
                    switch (tab.getPosition()) {
                        case 0:
                            rlMore.setVisibility(View.GONE);
                            HomeClick();
                            break;
                        case 1:
                            rlMore.setVisibility(View.GONE);
                            // Public Forum CLick
                            publicForumClick();
                            break;
                        case 2:
                            AppLog.e(TAG, "Set More Visible triggered");
                            rlMore.setVisibility(View.VISIBLE);
                            break;
                    }
                } else if (tabIcon.length == 2) {
                    switch (tab.getPosition()) {
                        case 0:
                            rlMore.setVisibility(View.GONE);
                            HomeClick();
                            break;
                        case 1:
                            AppLog.e(TAG, "Set More Visible triggered");
                            rlMore.setVisibility(View.VISIBLE);
                            break;
                    }
                }
                updateTabIcons(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //prevTabPos = tab.getPosition();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        createTabIcons();
    }


    public void boothClick() {

        AppLog.e(TAG,"boothClick ");

        tvToolbar.setText(GroupDashboardActivityNew.group_name);
        tv_Desc.setVisibility(View.GONE);
        tv_toolbar_icon.setVisibility(View.GONE);
        tv_toolbar_default.setVisibility(View.GONE);

        BoothListFragment classListFragment = new BoothListFragment();
        classListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, classListFragment).commit();
    }

    public void publicForumClick() {

        tvToolbar.setText(GroupDashboardActivityNew.group_name);
        tv_Desc.setVisibility(View.GONE);
        tv_toolbar_icon.setVisibility(View.GONE);
        tv_toolbar_default.setVisibility(View.GONE);

        if (mGroupItem.canPost || (mGroupItem.isBoothPresident && mGroupItem.boothCount > 1)) {

            Log.e(TAG,"publicForumClick if");
            PublicForumListFragment classListFragment = new PublicForumListFragment();
            classListFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, classListFragment).commit();
        }
        else
        {
            Log.e(TAG,"publicForumClick else");
            onBoothTeams(mGroupItem.boothName, mGroupItem.boothId,"normal", false);
        }
    }

    private void makeAdminClick()
    {
        tvToolbar.setText(GroupDashboardActivityNew.group_name);
        tv_Desc.setVisibility(View.GONE);
        tv_toolbar_icon.setVisibility(View.GONE);
        tv_toolbar_default.setVisibility(View.GONE);
        makeAdmin = true;

        tabLayout.setVisibility(View.GONE);

        if (mGroupItem.isAdmin) {

            setBackEnabled(true);

            BoothListMyTeamFragment boothListMyTeamFragment = new BoothListMyTeamFragment();
            boothListMyTeamFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, boothListMyTeamFragment).commit();

        }
    }

    private void myTeamClick() {

        AppLog.e(TAG, "myTeamClick");

        tvToolbar.setText(GroupDashboardActivityNew.group_name);
        tv_Desc.setVisibility(View.GONE);
        tv_toolbar_icon.setVisibility(View.GONE);
        tv_toolbar_default.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);

        makeAdmin = false;

        if (mGroupItem.isAdmin) {

            setBackEnabled(true);

            BoothListMyTeamFragment boothListMyTeamFragment = new BoothListMyTeamFragment();
            boothListMyTeamFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, boothListMyTeamFragment).commit();

        }
        if (mGroupItem.isBoothPresident) {

            if (mGroupItem.boothCount > 1)
            {
                setBackEnabled(true);

                BoothPresidentListMyTeamFragment classListFragment = new BoothPresidentListMyTeamFragment();
                classListFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, classListFragment).commit();
            }
            else
            {
                onBoothTeams(mGroupItem.boothName, mGroupItem.boothId,"myTeam", false);
            }
        }

        if (mGroupItem.isBoothWorker)
        {
                if (mGroupItem.subBoothCount > 1)
                {

                    setBackEnabled(true);

                    MyTeamSubBoothFragment classListFragment = new MyTeamSubBoothFragment();
                    classListFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, classListFragment).commit();

                }
                else
                {
                    onTeamSelectedVoter(mGroupItem.subBoothName,mGroupItem.subBoothMembers, mGroupItem.subBoothId  , "true");
                }
        }


    }


    public void getContactsWithPermission() {
        AppLog.e(TAG, "getContactsWithPermission ");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            getContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContacts();
                }
        }

    }

    private void cleverTapGroupVisited(GroupItem mGroupItem) {
        if (isConnectionAvailable()) {
            try {
                CleverTapAPI cleverTap = CleverTapAPI.getInstance(getApplicationContext());
                AppLog.e("GroupDashBoard", "Success to found cleverTap objects=>");

                HashMap<String, Object> groupVisit = new HashMap<String, Object>();
                groupVisit.put("id", mGroupItem.getGroupId() + "");
                groupVisit.put("group_name", mGroupItem.getName());

                cleverTap.event.push("Group Visited", groupVisit);
                AppLog.e("GroupDashBoard", "Success");

            } catch (CleverTapMetaDataNotFoundException e) {
                AppLog.e("GroupDashBoard", "CleverTapMetaDataNotFoundException=>" + e.toString());
                // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
            } catch (CleverTapPermissionsNotSatisfied e) {
                AppLog.e("GroupDashBoard", "CleverTapPermissionsNotSatisfied=>" + e.toString());
                // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
            }

        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }


    @Override
    public boolean onSupportNavigateUp() {
        AppLog.e("DashBoard", "oNSupportNavigateUp");
        hide_keyboard();
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        AppLog.e(TAG, "BackStack count : " + fm.getBackStackEntryCount());

        if (fm.getBackStackEntryCount() > 0) {

            if (fm.getBackStackEntryCount() == 1) {
                if ("constituency".equalsIgnoreCase(mGroupItem.category)) {
                    tabLayout.setVisibility(View.VISIBLE);
                 //   fabAddTicket.setVisibility(View.VISIBLE);
                } else if (mGroupItem.canPost)
                    tabLayout.setVisibility(View.VISIBLE);
              //      fabAddTicket.setVisibility(View.VISIBLE);
            }
            super.onBackPressed();
        } else {
            if (tabLayout.getSelectedTabPosition() == 0) {
                if (LeafPreference.getInstance(getApplicationContext()).getInt(LeafPreference.GROUP_COUNT) > 1 || "constituency".equalsIgnoreCase(mGroupItem.category)) {
                    GroupDashboardActivityNew.super.onBackPressed();
                } else {
                    editDialog = SMBDialogUtils.showSMBDialogOKCancel_(this, getResources().getString(R.string.msg_app_exit), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GroupDashboardActivityNew.super.onBackPressed();
                        }
                    });
                }
            } else {
                if (tabLayout.getTabAt(0) != null) {
                    tabLayout.getTabAt(0).select();
                    if ("constituency".equalsIgnoreCase(mGroupItem.category)) {
                        tabLayout.setVisibility(View.VISIBLE);
                    } else if (mGroupItem.canPost)
                        tabLayout.setVisibility(View.VISIBLE);
                }
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        hide_keyboard();

        boolean pp1 = LeafPreference.getInstance(this).getBoolean(LeafPreference.PERSONAL_POST_ADDED_1);
        boolean pp2 = LeafPreference.getInstance(this).getBoolean(LeafPreference.PERSONAL_POST_ADDED_2);
        AppLog.e(TAG, "PERSONAL_POST_ADDED_1,PERSONAL_POST_ADDED_2 :: " + pp1 + "," + pp2);
        FragmentManager fm = getSupportFragmentManager();
        if (pp1 && pp2 && fm.getBackStackEntryCount() > 0) {
            onBackPressed();
        }
        LeafPreference.getInstance(this).setBoolean(LeafPreference.PERSONAL_POST_ADDED_1, false);
        LeafPreference.getInstance(this).setBoolean(LeafPreference.PERSONAL_POST_ADDED_2, false);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (editDialog != null) {
            editDialog.cancel();
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        AppLog.e("onSuccess", "Success");
        AppLog.e("SearchResponse", response.toString());
        hideLoadingDialog();
        if (progressBar != null)

            hideLoadingBar();
        // progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_ALL_CONTACT_LIST:
                new TaskForGruppieContacts(response).execute();
                break;
        }
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<AddPostValidationError> error) {
        if (progressBar != null)
            hideLoadingBar();
        // progressBar.setVisibility(View.GONE);
        AppLog.e("onFailure", "Failure");
        if (error.status.equals("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            //  Toast.makeText(this, error.title, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        if (progressBar != null)
            hideLoadingBar();
        // progressBar.setVisibility(View.GONE);
        AppLog.e("onFailure", "Failure");
        if (msg.contains("401:Unauthorized")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            //  Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onException(int apiId, String msg) {
        if (progressBar != null)
            hideLoadingBar();
        // progressBar.setVisibility(View.GONE);
    }


    private void joinGruppieEvent() {
        String gruppiePackage = "nnr.gruppie";

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + gruppiePackage)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + gruppiePackage)));
        }
    }

    public void HomeClick() {

        tvToolbar.setText(GroupDashboardActivityNew.group_name);
        tv_Desc.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

         if ("constituency".equalsIgnoreCase(BuildConfig.AppCategory)) {

             BaseTeamFragmentv3 baseTeamFragment = new BaseTeamFragmentv3();
             fragmentTransaction.replace(R.id.fragment_container, baseTeamFragment);
             fragmentTransaction.commit();

         }
         else if ("CAMPUS".equalsIgnoreCase(BuildConfig.AppCategory)) {

             BaseTeamFragmentv2 baseTeamFragment = new BaseTeamFragmentv2();
             fragmentTransaction.replace(R.id.fragment_container, baseTeamFragment);
             fragmentTransaction.commit();

        }
    }

    public void onTeamSelected(MyTeamData team,String myBooth,String member) {

        AppLog.e(TAG,"onTeamSelected "+team.name);
        AppLog.e(TAG,"onTeamSelected "+new Gson().toJson(team));

     /*   if (mGroupItem.isAdmin || mGroupItem.isBoothPresident)
        {
            if (team.subCategory != null && team.subCategory.equalsIgnoreCase("boothPresidents"))
            {
                Intent intent = new Intent(this, CommitteeActivity.class);
                intent.putExtra("class_data",new Gson().toJson(team));
                intent.putExtra("title",team.name);
                startActivity(intent);
            }
            else
            {
                setBackEnabled(true);
                tvToolbar.setText(team.name);
                tv_toolbar_icon.setVisibility(View.GONE);
                tv_Desc.setText("Members : "+String.valueOf(team.members));
                tv_Desc.setVisibility(View.VISIBLE);

                AppLog.e("getActivity", "team name is =>" + team.name);

                TeamPostsFragmentNew fragTeamPost = TeamPostsFragmentNew.newInstance(team, true);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragTeamPost).addToBackStack("home").commit();
                showTeamInfoWindow();

                tabLayout.setVisibility(View.GONE);
            }
        }
        else {
            setBackEnabled(true);
            tvToolbar.setText(team.name);
            tv_toolbar_icon.setVisibility(View.GONE);
            tv_Desc.setText("Members : "+String.valueOf(team.members));
            tv_Desc.setVisibility(View.VISIBLE);

            AppLog.e("getActivity", "team name is =>" + team.name);

            TeamPostsFragmentNew fragTeamPost = TeamPostsFragmentNew.newInstance(team, true);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragTeamPost).addToBackStack("home").commit();
            showTeamInfoWindow();

            tabLayout.setVisibility(View.GONE);
        }*/

        if (true)
        {
          /*  if (myBooth.equalsIgnoreCase("yes"))
            {
                if (BoothPostEventTBL.getAll().size() > 0)
                {
                    List<BoothPostEventTBL> homeTeamDataTBLList = BoothPostEventTBL.getAll();

                    for (int i = 0;i<homeTeamDataTBLList.size();i++)
                    {

                        if (team.boothId.equalsIgnoreCase(homeTeamDataTBLList.get(i).boothId))
                        {
                            tv_Desc.setText(getResources().getString(R.string.lbl_members)+" : "+String.valueOf(homeTeamDataTBLList.get(i).members));
                            break;
                        }
                        else {
                            tv_Desc.setText(getResources().getString(R.string.lbl_members)+" : "+String.valueOf(team.members));
                        }
                    }
                }
                else
                {
                    tv_Desc.setText(getResources().getString(R.string.lbl_members)+" : "+String.valueOf(team.members));
                }
            }
            if (member.equalsIgnoreCase("yes"))
            {
                if (EventSubBoothTBL.getAll().size() > 0)
                {
                    List<EventSubBoothTBL> eventSubBoothTBLS = EventSubBoothTBL.getAll();

                    for (int i = 0;i<eventSubBoothTBLS.size();i++)
                    {
                        if (team.teamId.equalsIgnoreCase(eventSubBoothTBLS.get(i).teamId))
                        {
                            tv_Desc.setText(getResources().getString(R.string.lbl_members)+" : "+String.valueOf(eventSubBoothTBLS.get(i).members));
                            break;
                        }
                        else {
                            tv_Desc.setText(getResources().getString(R.string.lbl_members)+" : "+String.valueOf(team.members));
                        }
                    }
                }
                else
                {
                    tv_Desc.setText(getResources().getString(R.string.lbl_members)+" : "+String.valueOf(team.members));
                }
            }
            else{

                if (HomeTeamDataTBL.getAll().size() > 0)
                {
                    List<HomeTeamDataTBL> homeTeamDataTBLList = HomeTeamDataTBL.getAll();

                    for (int i = 0;i<homeTeamDataTBLList.size();i++)
                    {

                        if (team.teamId.equalsIgnoreCase(homeTeamDataTBLList.get(i).teamId))
                        {
                            tv_Desc.setText(getResources().getString(R.string.lbl_members)+" : "+String.valueOf(homeTeamDataTBLList.get(i).members));
                            break;
                        }
                        else {
                            tv_Desc.setText(getResources().getString(R.string.lbl_members)+" : "+String.valueOf(team.members));
                        }
                    }
                }
                else
                {
                    tv_Desc.setText(getResources().getString(R.string.lbl_members)+" : "+String.valueOf(team.members));
                }
            }*/

            if (HomeTeamDataTBL.getAll().size() > 0)
            {
                List<HomeTeamDataTBL> homeTeamDataTBLList = HomeTeamDataTBL.getAll();

                for (int i = 0;i<homeTeamDataTBLList.size();i++)
                {
                    AppLog.e(TAG,"HomeTeamDataTBL Team ID "+homeTeamDataTBLList.get(i).teamId);

                    if (team.teamId.equalsIgnoreCase(homeTeamDataTBLList.get(i).teamId))
                    {
                        tv_Desc.setText(getResources().getString(R.string.lbl_members)+" : "+String.valueOf(homeTeamDataTBLList.get(i).members));
                        break;
                    }
                    else {
                        tv_Desc.setText(getResources().getString(R.string.lbl_members)+" : "+String.valueOf(team.members));
                    }
                }
            }
            else
            {
                tv_Desc.setText(getResources().getString(R.string.lbl_members)+" : "+String.valueOf(team.members));
            }
        }
        else
        {
            tv_Desc.setText(getResources().getString(R.string.lbl_members)+" : "+String.valueOf(team.members));
        }

        setBackEnabled(true);

        tvToolbar.setText(team.name);
        tv_toolbar_icon.setVisibility(View.GONE);
        tv_toolbar_default.setVisibility(View.GONE);
        tv_Desc.setVisibility(View.VISIBLE);

        AppLog.e("getActivity", "team name is =>" + team.name);

        TeamPostsFragmentNew fragTeamPost = TeamPostsFragmentNew.newInstance(team, true,myBooth,member);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragTeamPost).addToBackStack("home").commit();
        showTeamInfoWindow();

        tabLayout.setVisibility(View.GONE);
    }

    public void onTeamSelectedVoter(String name, int members, String boothId , String isTeamAdmin) {

        Log.e(TAG,"boothId"+boothId);

        setBackEnabled(true);
        tvToolbar.setText(name);
        tv_Desc.setText(getResources().getString(R.string.lbl_members)+" : "+String.valueOf(members));
        tv_Desc.setVisibility(View.VISIBLE);
        tv_toolbar_icon.setVisibility(View.GONE);
        tv_toolbar_default.setVisibility(View.GONE);

        MyTeamVoterListFragment myTeamVoterListFragment = MyTeamVoterListFragment.newInstance(boothId, isTeamAdmin);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myTeamVoterListFragment).addToBackStack("home").commit();
        tabLayout.setVisibility(View.GONE);

    }

    public void onBoothTeams(String name, String team_id,String screen, boolean isBackStack) {

        AppLog.e(TAG,"onBoothTeams "+name);

        setBackEnabled(true);
        tvToolbar.setText(name + "");
        tv_Desc.setVisibility(View.GONE);
        MemberTeamListFragment fragTeamPost = new MemberTeamListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("team_id", team_id);
        bundle.putString("name", name);
        bundle.putString("screen",screen);
        fragTeamPost.setArguments(bundle);
        if (isBackStack) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragTeamPost)
                    .addToBackStack("home").commit();
            tabLayout.setVisibility(View.GONE);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragTeamPost).commit();
        }
    }

    public void groupSelected(MyTeamData group) {

        Log.e(TAG,"groupSelected "+new Gson().toJson(group));

        if (group.type.equals("Syllabus Tracker"))
        {
            Intent intent = new Intent(this, HWClassActivity.class);
            intent.putExtra("title", group.name);
            intent.putExtra("type", group.type);
            intent.putExtra("role", group.role);
            startActivity(intent);
        }
        else if (group.type.equals("Video Class")) {
            Intent intent = new Intent(this, VideoClassActivity.class);
            intent.putExtra("title", group.name);
            intent.putExtra("category", group.category);
            startActivity(intent);

        }else if (group.type.equals("My Family")) {
            startActivity(new Intent(this, FamilyMemberActivity.class));
        }
        /*else if (group.type.equals("Recorded Class")) {
            Intent intent;
            if ("admin".equalsIgnoreCase(group.role)) {
                intent = new Intent(this, RecordedClassActivity.class);
                intent.putExtra("title", group.name);
            } else {
                if (group.count == 1) {
                    intent = new Intent(this, RecClassSubjectActivity.class);
                    intent.putExtra("group_id", groupId);
                    intent.putExtra("team_id", group.details.teamId);
                    intent.putExtra("title", group.details.studentName);
                } else {
                    intent = new Intent(this, RecordedClassActivity.class);
                    intent.putExtra("title", group.name);
                }
            }
            intent.putExtra("type", group.type);
            intent.putExtra("role", group.role);
            startActivity(intent);

        }*/
        else if (group.type.equals("Home Work") || group.type.equals("Recorded Class")
                || group.type.equals("Time Table") || group.type.equals("Marks Card")) {

            if (group.type.equalsIgnoreCase("Home Work")) {
                LeafPreference.getInstance(this).remove(groupId + "_HOMEWORK_NOTI_COUNT");
            } else if (group.type.equalsIgnoreCase("Recorded Class")) {
                LeafPreference.getInstance(this).remove(groupId + "_NOTES_VIDEO_NOTI_COUNT");
            }

            Intent intent;
            if ("admin".equalsIgnoreCase(group.role)) {
                intent = new Intent(this, HWClassActivity.class);
                intent.putExtra("title", group.name);
            }
            else {
                if (group.count == 1) {
                    if (group.type.equals("Home Work") || group.type.equals("Recorded Class")) {
                        intent = new Intent(this, HWClassSubjectActivity.class);
                } else if (group.type.equals("Marks Card")) {
                        intent = new Intent(this, MarksCardActivity2.class);
                    } else {
                        intent = new Intent(this, TimeTabelActivity2.class);
                    }
                    intent.putExtra("group_id", groupId);
                    intent.putExtra("team_id", group.details.teamId);
                    intent.putExtra("title", group.details.studentName);

                } else {
                    intent = new Intent(this, HWClassActivity.class);
                    intent.putExtra("title", group.name);
                }
            }

            intent.putExtra("type", group.type);
            intent.putExtra("role", group.role);
            startActivity(intent);
        }
        else if (group.type.equals("Test")) {
            LeafPreference.getInstance(this).remove(groupId + "_TEST_EXAM_NOTI_COUNT");
            Intent intent;
            if ("admin".equalsIgnoreCase(group.role)) {
                intent = new Intent(this, TestClassActivity.class);
                intent.putExtra("title", group.name);
            } else {
                if (group.count == 1) {
                    intent = new Intent(this, TestClassSubjectActivity.class);
                    intent.putExtra("group_id", groupId);
                    intent.putExtra("team_id", group.details.teamId);
                    intent.putExtra("title", group.details.studentName);

                    // GETTING CLASSLIST TO SEND TO EXAM SCREEN...
                    List<LiveClassListTBL> list = LiveClassListTBL.getAll(GroupDashboardActivityNew.groupId);
                    ArrayList<VideoClassResponse.ClassData> result = new ArrayList<>();

                    if (list.size() != 0) {

                        for (int i = 0; i < list.size(); i++) {
                            LiveClassListTBL currentItem = list.get(i);
                            VideoClassResponse.ClassData item = new VideoClassResponse.ClassData();
                            item.zoomPassword = currentItem.zoomPassword;
                            item.zoomName = new Gson().fromJson(currentItem.zoomName, new TypeToken<ArrayList<String>>() {
                            }.getType());
                            item.zoomMail = currentItem.zoomMail;
                            item.zoomSecret = currentItem.zoomSecret;
                            item.zoomMeetingPassword = currentItem.zoomMeetingPassword;
                            item.zoomKey = currentItem.zoomKey;
                            item.id = currentItem.teamId;
                            item.className = currentItem.name;
                            item.jitsiToken = currentItem.jitsiToken;
                            item.groupId = currentItem.groupId;
                            item.canPost = currentItem.canPost;
                            result.add(item);
                        }

                    }

                    if (result != null && result.size() > 0)
                        intent.putExtra("liveClass", new Gson().toJson(result.get(0)));


                } else {
                    intent = new Intent(this, TestClassActivity.class);
                    intent.putExtra("title", group.name);
                }
            }
            intent.putExtra("role", group.role);
            startActivity(intent);
        }

        /*else if (group.type.equals("Time Table")) {
            Intent intent;
            if ("admin".equalsIgnoreCase(group.role)) {
                intent = new Intent(this, TimeTableClassActivity2.class);
                intent.putExtra("title", group.name);
            } else {
                if (group.count == 1) {
                    intent = new Intent(this, TimeTabelActivity2.class);
                    intent.putExtra("group_id", groupId);
                    intent.putExtra("team_id", group.details.teamId);
                    intent.putExtra("team_name", group.details.studentName);
                } else {
                    intent = new Intent(this, TimeTableClassActivity2.class);
                    intent.putExtra("title", group.name);
                }
            }
            intent.putExtra("role", group.role);
            startActivity(intent);

        }*/
        else if (group.type.equals("Gallery")) {

            LeafPreference.getInstance(this).remove(groupId + "_gallerypost");

            startActivity(new Intent(this, GalleryActivity.class));
        } else if (group.type.equalsIgnoreCase("Calendar")) {
            startActivity(new Intent(this, CalendarActivity.class));
        } else if (group.type.equals("Fees")) {

            if ("admin".equalsIgnoreCase(group.role)) {
                Intent intent = new Intent(this, FeesClassActivity.class);
                intent.putExtra("title", group.name);
                intent.putExtra("role", group.role);
                startActivity(intent);
            } else {
                if (group.count == 1) {
                    Intent intent;
                    if ("teacher".equalsIgnoreCase(group.role)) {
                        intent = new Intent(this, FeesListActivity.class);
                        intent.putExtra("group_id", groupId);
                        intent.putExtra("team_id", group.details.teamId);
                        intent.putExtra("title", group.details.studentName);
                        intent.putExtra("role", group.role);
                    } else {
                        intent = new Intent(this, StudentFeesActivity.class);
                        intent.putExtra("groupId", groupId);
                        intent.putExtra("title", group.details.studentName);
                        intent.putExtra("team_id", group.details.teamId);
                        intent.putExtra("user_id", group.details.userId);
                    }
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(this, FeesClassActivity.class);
                    intent.putExtra("title", group.name);
                    intent.putExtra("role", group.role);
                    startActivity(intent);
                }
            }

        } else if (group.type.equals("Chat")) {

            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("role", group.role);
            startActivity(intent);

        } else if (group.type.equals("Vendor Connect")) {
            Intent intent = new Intent(this, VendorActivity.class);
            intent.putExtra("group_id", groupId);
            intent.putExtra("role", group.role);
            startActivity(intent);
        } else if (group.type.equals("E-Books")) {
            if (group.count == 0) {
                Intent intent = new Intent(this, EBookClassActivity.class);
                intent.putExtra("group_id", groupId);
                intent.putExtra("title", group.name);
                intent.putExtra("role", group.role);
                startActivity(intent);
            } else if (group.count == 1) {
                Intent intent = new Intent(this, EBookPdfForTeamActivity.class);
                intent.putExtra("group_id", groupId);
                intent.putExtra("team_id", group.details.teamId);
                intent.putExtra("title", group.details.teamName);
                intent.putExtra("role", group.role);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, ParentKidsForEBookActivity.class);
                intent.putExtra("group_id", groupId);
                intent.putExtra("title", group.name);
                intent.putExtra("role", group.role);
                startActivity(intent);
            }
        } else if (group.type.equals("Code Of Conduct")) {
            Intent intent = new Intent(this, CodeConductActivity.class);
            intent.putExtra("group_id", groupId);
            intent.putExtra("role", group.role);
            startActivity(intent);

        } else if (group.type.equals("Attendance")) {
            if ("teacher".equalsIgnoreCase(group.role) && group.count == 1) {
                if ("preschool".equalsIgnoreCase(group.details.category)) {
                    Intent intent = new Intent(this, AttendancePareSchool.class);
                    intent.putExtra("isTeamAdmin", true);
                    intent.putExtra("team_id", group.details.teamId);
                    intent.putExtra("group_id", GroupDashboardActivityNew.groupId);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, AttendanceActivity.class);
                    intent.putExtra("group_id", groupId);
                    intent.putExtra("team_id", group.details.teamId);
                    intent.putExtra("className", group.details.teamName);
                    startActivity(intent);
                }

            }
            if ("teacher".equalsIgnoreCase(group.role) && group.count > 1) {
                Intent intent = new Intent(this, TeacherClassActivity.class);
                intent.putExtra("is_for_attendance", true);
                startActivity(intent);
            }
            if ("admin".equalsIgnoreCase(group.role)) {
                Intent intent = new Intent(this, TeacherClassActivity.class);
                intent.putExtra("is_for_attendance", true);
                intent.putExtra("role", "admin");
                startActivity(intent);
            }


            if ("parent".equalsIgnoreCase(group.role) && group.count == 1) {
                Intent intent = new Intent(this, AttendanceDetailActivity.class);
                intent.putExtra("group_id", GroupDashboardActivityNew.groupId);
                intent.putExtra("team_id", group.details.teamId);
                intent.putExtra("title", group.details.studentName);
                intent.putExtra("userId", group.details.userId);
                intent.putExtra("rollNo", group.details.rollNumber);
                startActivity(intent);
            }
            if ("parent".equalsIgnoreCase(group.role) && group.count > 1) {
                Intent intent = new Intent(this, ParentKidsActivity.class);
                intent.putExtra("is_for_attendance", true);
                startActivity(intent);
            }

        }/* else if (group.type.equals("Marks Card")) {
            if ("teacher".equalsIgnoreCase(group.role) && group.count == 1) {
                Intent intent = new Intent(this, MarksheetActivity.class);
                intent.putExtra("team_id", group.details.teamId);
                intent.putExtra("className", group.details.teamName);
                intent.putExtra("group_id", GroupDashboardActivityNew.groupId);
                startActivity(intent);
            }
            if ("teacher".equalsIgnoreCase(group.role) && group.count > 1) {
                Intent intent = new Intent(this, TeacherClassActivity.class);
                intent.putExtra("is_for_attendance", false);
                startActivity(intent);
            }

            if ("admin".equalsIgnoreCase(group.role)) {
                Intent intent = new Intent(this, TeacherClassActivity.class);
                intent.putExtra("is_for_attendance", false);
                intent.putExtra("role", "admin");
                startActivity(intent);
            }
            if ("parent".equalsIgnoreCase(group.role) && group.count == 1) {
                Intent intent = new Intent(this, MarkSheetListActivity.class);
                intent.putExtra("group_id", groupId);
                intent.putExtra("team_id", group.details.teamId);
                intent.putExtra("className", group.details.teamName);
                intent.putExtra("user_id", group.details.userId);
                intent.putExtra("name", group.details.studentName);
                intent.putExtra("roll_no", group.details.rollNumber);
                intent.putExtra("role", "parent");
                startActivity(intent);
            }
            if ("parent".equalsIgnoreCase(group.role) && group.count > 1) {
                Intent intent = new Intent(this, ParentKidsActivity.class);
                intent.putExtra("is_for_attendance", false);
                startActivity(intent);
            }
        } */
        else if (group.type.equals("Course")) {
            Intent intent = new Intent(this, CourseActivity.class);
            intent.putExtra("group_id", groupId);
            intent.putExtra("role", group.role);
            startActivity(intent);
        }
        else if (group.type.equals("Issues")){

            int Count = 0;
            String Role = null;

            if (mGroupItem.isAdmin)
            {
                Count++;
                Role = "isAdmin";
            }
            if (mGroupItem.isPartyTaskForce)
            {
                Count++;
                Role = "isPartyTaskForce";
            }
            if (mGroupItem.isDepartmentTaskForce)
            {
                Count++;
                Role = "isDepartmentTaskForce";
            }
            if (mGroupItem.isBoothPresident)
            {
                Count++;
                Role = "isBoothPresident";
            }
            if (mGroupItem.isBoothMember)
            {
                Count++;
                Role = "isBoothMember";
            }

            if (Count > 1)
            {
                Intent intent = new Intent(this, SelectRoleActivity.class);
                startActivity(intent);

            }
            else if (1 == Count)
            {
                Intent intent = new Intent(this, TicketsActivity.class);
                intent.putExtra("Role", Role);
                startActivity(intent);
            }
            else
            {
                Intent intent = new Intent(this, TicketsActivity.class);
                intent.putExtra("Role", Role);
                startActivity(intent);
            }

        }
        else if (group.type.equals("Master List")){

            Intent intent = new Intent(this, MasterListActivity.class);
            intent.putExtra("name", group.name);
            startActivity(intent);
        }
        else {
            Log.e(TAG,"GeneralPostFragment Start"+group.name);
            setBackEnabled(true);
            tvToolbar.setText(group.name);
            tv_toolbar_icon.setVisibility(View.GONE);
            tv_toolbar_default.setVisibility(View.GONE);
            tv_Desc.setText(getResources().getString(R.string.lbl_members)+" : "+String.valueOf(group.members));

            if (group.name.equalsIgnoreCase("Notice Board") || group.name.equalsIgnoreCase("Announcement"))
            {
                tv_Desc.setVisibility(View.GONE);
            }
            else
            {
                tv_Desc.setVisibility(View.VISIBLE);
            }

            if (BuildConfig.AppCategory.equalsIgnoreCase("constituency"))
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, GenralPostConstituencyFragment.newInstance(group.groupId,group.name)).addToBackStack("home").commitAllowingStateLoss();
            }
            else
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, GeneralPostFragment.newInstance(group.groupId,group.name)).addToBackStack("home").commitAllowingStateLoss();
            }

            tabLayout.setVisibility(View.GONE);
        }

    }

    public void announcementClick()
    {
        setBackEnabled(true);
        tv_toolbar_icon.setVisibility(View.GONE);
        tv_toolbar_default.setVisibility(View.GONE);

        tvToolbar.setText(getResources().getString(R.string.lbl_announcements));
        tv_Desc.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, GenralPostConstituencyFragment.newInstance(groupId,"Announcement")).addToBackStack("home").commitAllowingStateLoss();
        tabLayout.setVisibility(View.GONE);
    }

    public void issueClick()
    {
        int Count = 0;
        String Role = null;

        if (mGroupItem.isAdmin)
        {
            Count++;
            Role = "isAdmin";
        }
        if (mGroupItem.isPartyTaskForce)
        {
            Count++;
            Role = "isPartyTaskForce";
        }
        if (mGroupItem.isDepartmentTaskForce)
        {
            Count++;
            Role = "isDepartmentTaskForce";
        }
        if (mGroupItem.isBoothPresident)
        {
            Count++;
            Role = "isBoothPresident";
        }
        if (mGroupItem.isBoothMember)
        {
            Count++;
            Role = "isBoothMember";
        }

        if (Count > 1)
        {
            Intent intent = new Intent(this, SelectRoleActivity.class);
            startActivity(intent);

        }
        else if (1 == Count)
        {
            Intent intent = new Intent(this, TicketsActivity.class);
            intent.putExtra("Role", Role);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(this, TicketsActivity.class);
            intent.putExtra("Role", Role);
            startActivity(intent);
        }
    }

    //}


    public void showTeamInfoWindow() {
       /* if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_first_time_team", true)) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("is_first_time_team", false).apply();

            showInfoWindow(windowCount);
        }*/

    }

    private void showInfoWindow(int whichInfo) {
        switch (whichInfo) {
            case 1:
                showtips = new ShowTipsBuilder(this)
                        .setTarget(tapView1)
                        .setDescription("Click to show Team List")
                        .setBackgroundAlpha(100)
                        .setCloseButtonTextColor(getResources().getColor(R.color.colorPrimaryBg))
                        .build();
                showtips.show(this);

                break;
            case 2:
                showtips = new ShowTipsBuilder(this)
                        .setTarget(tapView2)
                        .setDescription("Click to Add Post")
                        .setBackgroundAlpha(100)
                        .setCloseButtonTextColor(getResources().getColor(R.color.colorPrimaryBg))
                        .build();
                showtips.show(this);
                break;
            case 3:
                showtips = new ShowTipsBuilder(this)
                        .setTarget(tapView3)
                        .setDescription("Click to Add Friends")
                        .setBackgroundAlpha(100)
                        .setCloseButtonTextColor(getResources().getColor(R.color.colorPrimaryBg))
                        .build();
                showtips.show(this);
                break;
            default:
                break;
        }

        windowCount++;
        showtips.setCallback(new ShowTipsViewInterface() {
            @Override
            public void gotItClicked() {
                showInfoWindow(windowCount);
            }
        });

    }

    public boolean isBaseFragment() {
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof BaseFragment) {
            return true;
        } else {
            return false;
        }
    }

    private void DeleteOldSavedVideos() {

        LeafPreference leafPreference = LeafPreference.getInstance(GroupDashboardActivityNew.this);
        if (!leafPreference.getString(LeafPreference.OFFLINE_VIDEONAMES).equalsIgnoreCase(""))
        {

            ArrayList<VideoOfflineObject> list = new Gson().fromJson(leafPreference.getString(LeafPreference.OFFLINE_VIDEONAMES), new TypeToken<ArrayList<VideoOfflineObject>>() {}.getType());
            AppLog.e(TAG, "list before : " + list);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -30);
            String sevendayDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

            AppLog.e(TAG, "DeleteOldSaveVideos called with sevendaydate is : " + sevendayDate);

            int i = 0;

            for (Iterator<VideoOfflineObject> iterator = list.iterator(); iterator.hasNext(); ) {
                VideoOfflineObject offlineObject = iterator.next();

                try {

                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(offlineObject.getVideo_date());


                    if (calendar.getTimeInMillis() > date.getTime())
                    {
                        MixOperations.deleteVideoFile(offlineObject.video_filepath);
                        iterator.remove();
                        i++;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }





                if (i > 20) { /// Adding this condition to avoid too many deletion on main thread.
                    break;
                }
            }
            AppLog.e(TAG, "list after : " + list);
            leafPreference.setString(LeafPreference.OFFLINE_VIDEONAMES, new Gson().toJson(list));
        }
    }
    
}
