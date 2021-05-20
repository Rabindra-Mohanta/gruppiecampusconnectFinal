package school.campusconnect.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.fragments.GeneralPostFragment;
import school.campusconnect.fragments.TeamPostsFragmentNew;
import school.campusconnect.utils.AppLog;

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


import net.frederico.showtipsview.ShowTipsBuilder;
import net.frederico.showtipsview.ShowTipsView;
import net.frederico.showtipsview.ShowTipsViewInterface;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.fragments.BaseTeamFragment;
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
import school.campusconnect.views.SMBDialogUtils;

public class GroupDashboardActivityNew extends BaseActivity
        implements View.OnClickListener, LeafManager.OnCommunicationListener, LeafManager.OnAddUpdateListener<AddPostValidationError> {

    public static final String EXTRA_GROUP_ITEM = "extra_group_item";
    private static final String TAG = "GroupDashboardActNew";
    public static String selectedUserInChat = "";
    public static int notificationUnseenCount = 0;

    @Bind(R.id.toolbar_dashboard)
    public Toolbar mToolBar;

    @Bind(R.id.appBar)
    AppBarLayout appBar;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.tabLayout)
    public TabLayout tabLayout;

    @Bind(R.id.llAuthorizedUser)
    LinearLayout llAuthorizedUser;
    @Bind(R.id.llAllUsers)
    LinearLayout llAllUsers;
    @Bind(R.id.llDoubt)
    LinearLayout llDoubt;
    @Bind(R.id.rlMore)
    RelativeLayout rlMore;
    @Bind(R.id.llFavourite)
    LinearLayout llFavourite;
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

    @Bind(R.id.llEBook)
    LinearLayout llEBook;

    @Bind(R.id.llSubject)
    LinearLayout llSubject;

    @Bind(R.id.llSubject2)
    LinearLayout llSubject2;

    @Bind(R.id.llStaffReg)
    LinearLayout llStaffReg;

    @Bind(R.id.llBusRegister)
    LinearLayout llBusRegister;

    @Bind(R.id.llFees)
    LinearLayout llFees;

    @Bind(R.id.llAttendanceReport)
    LinearLayout llAttendanceReport;


    @Bind(R.id.tapView1)
    public View tapView1;
    @Bind(R.id.tapView2)
    public View tapView2;
    @Bind(R.id.tapView3)
    public View tapView3;

    public TextView tvToolbar;

    SharedPreferences prefs;

    LeafManager manager;

    public GroupItem mGroupItem;

    public static String image;
    public static boolean mAllowPostAll;
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

    String[] tabText = new String[2];
    int[] tabIcon = new int[2];
    int[] tabIcongray = new int[2];
    public TextView tv_Desc;
    private int windowCount = 1;
    private ShowTipsView showtips;

    public static int postCount = 0;
    public static String adminPhone = "";
    public static boolean allowedToAddUser;
    public static String groupCategory = "";
    private AlertDialog editDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_new);
        ButterKnife.bind(this);

        init();

        calculateDisplayMatrix();

        setGroupData(true);

        hideMoreOption();

        cleverTapGroupVisited(mGroupItem);

        setTitle("");

        HomeClick();

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

    }

    /*private void sendNotification(String messageBody, String title) {
        AppLog.e(TAG,"sendNotification() called");

        String CHANNEL_ID = "gruppie_02";// The id of the channel.

        Intent intent = new Intent(this, GroupDashboardActivityNew.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 *//* Request code *//*, intent,
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
        notificationManager.notify(0 *//* ID of notification *//*, notificationBuilder.build());
    }*/

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

        mGroupItem = new Gson().fromJson(LeafPreference.getInstance(this).getString(Constants.GROUP_DATA), GroupItem.class);
        AppLog.e(TAG, "mGroupItem : " + new Gson().toJson(mGroupItem));

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


        setTabLayout();


    }

    private void setTabLayout() {
        tabIcon[0] = R.drawable.icon_home;
        //tabIcon[1] = R.drawable.icon_personal;
        //tabIcon[2]=R.drawable.icon_group;
        tabIcon[1] = R.drawable.icon_more_tab;

        tabIcongray[0] = R.drawable.icon_home_gray;
        //tabIcongray[1] = R.drawable.icon_personal_gray;
        //tabIcongray[2]=R.drawable.icon_group_gray;
        tabIcongray[1] = R.drawable.icon_more_tab_gray;

        tabText[0] = getResources().getString(R.string.lbl_home);
        //tabText[1] = getResources().getString(R.string.lbl_personal);
        //tabText[2]=getResources().getString(R.string.lbl_app_name_tab);
        tabText[1] = getResources().getString(R.string.lbl_more);

        tabLayout.addTab(tabLayout.newTab().setText(tabText[0]));
        tabLayout.addTab(tabLayout.newTab().setText(tabText[1]));
        //tabLayout.addTab(tabLayout.newTab().setText(tabText[2]));
        //tabLayout.addTab(tabLayout.newTab().setText(tabText[3]));


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                AppLog.e(TAG, "TAb Chaged Called");

                changeTab(tab.getPosition());
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

    @OnClick({R.id.rlMore, R.id.llProfile, R.id.llPeople,R.id.llSubject,R.id.llSubject2, R.id.llDiscuss, R.id.llJoinGruppie, R.id.llAuthorizedUser, R.id.llAllUsers, R.id.llFavourite, R.id.llDoubt, R.id.llAboutGroup, R.id.llAddFriend, R.id.llArchiveTeam, R.id.llNotification, R.id.llClass,R.id.llEBook, R.id.llBusRegister,R.id.llFees, R.id.llAttendanceReport, R.id.llStaffReg})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.llProfile:
                if (isConnectionAvailable()) {
                    Intent intent = new Intent(this, ProfileActivity2.class);
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
            case R.id.llFavourite:
                if (isConnectionAvailable()) {
                    Intent intent = new Intent(this, FavoritePostActivity.class);
                    intent.putExtra("id", groupId);
                    startActivity(intent);
                } else {
                    showNoNetworkMsg();
                }
                break;
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
            case R.id.llEBook:
                if (isConnectionAvailable()) {
                    startActivity(new Intent(this, EBookActivity.class));
                } else {
                    showNoNetworkMsg();
                }
                break;

          /*  case R.id.llSubject:
                if (isConnectionAvailable()) {
                    startActivity(new Intent(this, SubjectActivity.class));
                } else {
                    showNoNetworkMsg();
                }
                break;*/
            case R.id.llSubject2:
                if (isConnectionAvailable()) {
                    startActivity(new Intent(this, ClassActivity2.class));
                } else {
                    showNoNetworkMsg();
                }
                break;
            case R.id.llFees:
                if (isConnectionAvailable()) {
                    startActivity(new Intent(this, FeesClassActivity.class));
                } else {
                    showNoNetworkMsg();
                }
                break;
            case R.id.llStaffReg:
                if (isConnectionAvailable()) {
                    startActivity(new Intent(this, StaffActivity.class));
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


        }
        rlMore.setVisibility(View.GONE);
        tabLayout.getTabAt(0).select();
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


    private void updateTabIcons(int pos) {
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


    public void changeTab(int position) {
        AppLog.e(TAG, "pos : " + position);
        switch (position) {
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
        GroupDashboardActivityNew.notificationUnseenCount = mGroupItem.notificationUnseenCount;
        GroupDashboardActivityNew.isPost = mGroupItem.isAdmin || mGroupItem.canPost || mGroupItem.allowPostAll;

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean("isAdmin", isAdmin).apply();
        prefs.edit().putString("id", mGroupItem.getGroupId() + "").apply();

        if (isToolBarTextSet) {
            tvToolbar.setText(mGroupItem.name);
            tv_Desc.setText(LeafPreference.getInstance(this).getInt(Constants.TOTAL_MEMBER) + " users");
            tv_Desc.setVisibility(View.VISIBLE);
        }

    }

    private void hideMoreOption() {
        if (mGroupItem.isAdmin) {
            llAllUsers.setVisibility(View.VISIBLE);
            llAuthorizedUser.setVisibility(View.VISIBLE);
        } else {
            llAllUsers.setVisibility(View.GONE);
            llAuthorizedUser.setVisibility(View.GONE);
        }

        if (mGroupItem.isAdmin) {
            llDiscuss.setVisibility(View.VISIBLE);
        } else {
            llDiscuss.setVisibility(View.GONE);
        }

        if (!mGroupItem.canPost || !mGroupItem.allowPostQuestion)
            llDoubt.setVisibility(View.GONE);

        if (mGroupItem.isAdmin || mGroupItem.canPost) {
            llClass.setVisibility(View.VISIBLE);
            llEBook.setVisibility(View.VISIBLE);
//            llSubject.setVisibility(View.VISIBLE);
            llSubject2.setVisibility(View.VISIBLE);
            llStaffReg.setVisibility(View.VISIBLE);
            llAttendanceReport.setVisibility(View.VISIBLE);
            llBusRegister.setVisibility(View.VISIBLE);
            llFees.setVisibility(View.VISIBLE);
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
                tabLayout.setVisibility(View.VISIBLE);
            }
            super.onBackPressed();
        } else {
            if (tabLayout.getSelectedTabPosition() == 0) {
                if (LeafPreference.getInstance(getApplicationContext()).getInt(LeafPreference.GROUP_COUNT) > 1) {
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
                tabLayout.getTabAt(0).select();
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
            progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_ALL_CONTACT_LIST:
                new TaskForGruppieContacts(response).execute();
                break;
        }
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<AddPostValidationError> error) {
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
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
            progressBar.setVisibility(View.GONE);
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
            progressBar.setVisibility(View.GONE);


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

        BaseTeamFragment baseTeamFragment = new BaseTeamFragment();
        fragmentTransaction.replace(R.id.fragment_container, baseTeamFragment);
        fragmentTransaction.commit();
    }

    public void onTeamSelected(MyTeamData team) {
        setBackEnabled(true);
        tvToolbar.setText(team.name);
        tv_Desc.setText(team.members + " users");
        tv_Desc.setVisibility(View.VISIBLE);
        AppLog.e("getActivity", "team name is =>" + team.name);

        TeamPostsFragmentNew fragTeamPost = TeamPostsFragmentNew.newInstance(team, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragTeamPost).addToBackStack("home").commit();
        showTeamInfoWindow();

        tabLayout.setVisibility(View.GONE);

    }

    public void groupSelected(MyTeamData group) {
        if (group.type.equals("Video Class")) {
            Intent intent = new Intent(this, VideoClassActivity.class);
            intent.putExtra("title",group.name);
            startActivity(intent);
        }else if (group.type.equals("Recorded Class")) {
            Intent intent = new Intent(this, RecordedClassActivity.class);
            intent.putExtra("title",group.name);
            startActivity(intent);
        }else if (group.type.equals("Gallery")) {
            startActivity(new Intent(this, GalleryActivity.class));
        } else if (group.type.equalsIgnoreCase("Calendar")) {
            startActivity(new Intent(this, CalendarActivity.class));
        } else if (group.type.equals("Time Table")) {
            Intent intent = new Intent(this, TimeTableClassActivity2.class);
            intent.putExtra("role", group.role);
            startActivity(intent);
        } else if (group.type.equals("Chat")) {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("role",group.role);
            startActivity(intent);
        } else if (group.type.equals("Vendor Connect")) {
            startActivity(new Intent(this, VendorActivity.class));
        }else if (group.type.equals("E-Books")) {
            if(group.count==0){
                Intent intent=new Intent(this, EBookClassActivity.class);
                intent.putExtra("group_id",groupId);
                intent.putExtra("title",group.name);
                startActivity(intent);
            }
            else if(group.count==1){
                Intent intent=new Intent(this, EBookPdfForTeamActivity.class);
                intent.putExtra("group_id",groupId);
                intent.putExtra("team_id",group.details.teamId);
                intent.putExtra("title", group.details.teamName);
                startActivity(intent);
            }else{
                Intent intent=new Intent(this, ParentKidsForEBookActivity.class);
                intent.putExtra("group_id",groupId);
                intent.putExtra("title",group.name);
                startActivity(intent);
            }
        } else if (group.type.equals("Code Of Conduct")) {
            startActivity(new Intent(this, CodeConductActivity.class));
        } else if (group.type.equals("Attendance")) {
            if ("teacher".equalsIgnoreCase(group.role) && group.count==1) {
                if("preschool".equalsIgnoreCase(group.details.category)){
                    Intent intent = new Intent(this, AttendancePareSchool.class);
                    intent.putExtra("isTeamAdmin", true);
                    intent.putExtra("team_id", group.details.teamId);
                    intent.putExtra("group_id",GroupDashboardActivityNew.groupId);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(this,AttendanceActivity.class);
                    intent.putExtra("group_id",groupId);
                    intent.putExtra("team_id",group.details.teamId);
                    startActivity(intent);
                }

            }
            if ("teacher".equalsIgnoreCase(group.role) && group.count>1) {
                Intent intent = new Intent(this,TeacherClassActivity.class);
                intent.putExtra("is_for_attendance",true);
                startActivity(intent);
            }
            if ("admin".equalsIgnoreCase(group.role)) {
                Intent intent = new Intent(this,TeacherClassActivity.class);
                intent.putExtra("is_for_attendance",true);
                intent.putExtra("role","admin");
                startActivity(intent);
            }


            if ("parent".equalsIgnoreCase(group.role) && group.count==1) {
                Intent intent = new Intent(this, AttendanceDetailActivity.class);
                intent.putExtra("group_id",GroupDashboardActivityNew.groupId);
                intent.putExtra("team_id",group.details.teamId);
                intent.putExtra("title",group.details.studentName);
                intent.putExtra("userId",group.details.userId);
                intent.putExtra("rollNo",group.details.rollNumber);
                startActivity(intent);
            }
            if ("parent".equalsIgnoreCase(group.role) && group.count>1) {
                Intent intent = new Intent(this,ParentKidsActivity.class);
                intent.putExtra("is_for_attendance",true);
                startActivity(intent);
            }

        } else if (group.type.equals("Marks Card")) {
            if ("teacher".equalsIgnoreCase(group.role) && group.count==1) {
                Intent intent = new Intent(this, MarksheetActivity.class);
                intent.putExtra("team_id", group.details.teamId);
                intent.putExtra("group_id",GroupDashboardActivityNew.groupId);
                startActivity(intent);
            }
            if ("teacher".equalsIgnoreCase(group.role) && group.count>1) {
                Intent intent = new Intent(this,TeacherClassActivity.class);
                intent.putExtra("is_for_attendance",false);
                startActivity(intent);
            }

            if ("admin".equalsIgnoreCase(group.role)) {
                Intent intent = new Intent(this,TeacherClassActivity.class);
                intent.putExtra("is_for_attendance",false);
                intent.putExtra("role","admin");
                startActivity(intent);
            }
            if ("parent".equalsIgnoreCase(group.role) && group.count==1) {
                Intent intent = new Intent(this,MarkSheetListActivity.class);
                intent.putExtra("group_id",groupId);
                intent.putExtra("team_id",group.details.teamId);
                intent.putExtra("user_id",group.details.userId);
                intent.putExtra("name",group.details.studentName);
                intent.putExtra("roll_no",group.details.rollNumber);
                intent.putExtra("role","parent");
                startActivity(intent);
            }
            if ("parent".equalsIgnoreCase(group.role) && group.count>1) {
                Intent intent = new Intent(this,ParentKidsActivity.class);
                intent.putExtra("is_for_attendance",false);
                startActivity(intent);
            }
        } else {
            setBackEnabled(true);
            tvToolbar.setText(group.name);
            tv_Desc.setText(group.members + " users");
            tv_Desc.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, GeneralPostFragment.newInstance(group.groupId)).addToBackStack("home").commitAllowingStateLoss();
            tabLayout.setVisibility(View.GONE);
        }

    }


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
    public boolean isBaseFragment(){
        if(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof BaseFragment){
            return true;
        }else {
            return false;
        }
    }
}
