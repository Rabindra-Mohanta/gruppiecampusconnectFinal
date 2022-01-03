package school.campusconnect.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;
import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.activities.ChangePasswordActivity;
import school.campusconnect.activities.CreateTeamActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.ProfileActivity2;
import school.campusconnect.adapters.TeamListAdapterNew;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.GroupDetailResponse;
import school.campusconnect.datamodel.GroupItem;
import school.campusconnect.datamodel.TeamCountTBL;
import school.campusconnect.datamodel.BaseTeamTable;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.datamodel.teamdiscussion.MyTeamsResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppDialog;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.views.SMBDialogUtils;

public class BaseTeamFragment extends BaseFragment implements TeamListAdapterNew.OnTeamClickListener, LeafManager.OnCommunicationListener {
    private static final String TAG = "BaseTeamFragment";
    View view;
    RecyclerView rvTeams;
    ArrayList<MyTeamData> teamList = new ArrayList<>();
    private LeafManager manager;
    private TeamListAdapterNew mAdapter;
    // PullRefreshLayout swipeRefreshLayout;
    DatabaseHandler databaseHandler;
    LeafPreference pref;

    private int REQUEST_LOAD_GALLERY_IMAGE = 112;
    ImageView imgBackground;

    boolean isVisible;
    private ProgressBar progressBar;
    private MenuItem menuItem;
    SharedPreferences wallPref;
    private MenuItem removeWallMenu;
    private GroupItem mGroupItem;

    //  DatabaseReference database;
    //  ArrayList<Query> teamsRef;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_base_team, container, false);

        init();

        setBackgroundImage();

        getTeams();

        return view;
    }

    private void setBackgroundImage() {
        if (wallPref.contains(Constants.BACKGROUND_IMAGE)) {
            String path = wallPref.getString(Constants.BACKGROUND_IMAGE, "");
            if (TextUtils.isEmpty(path))
                return;

            AppLog.e(TAG, "path background : " + path);
            Picasso.with(getActivity()).load(path).into(imgBackground, new Callback() {
                @Override
                public void onSuccess() {
                    AppLog.e(TAG, "onSuccess background");
                }

                @Override
                public void onError() {
                    AppLog.e(TAG, "onError background");
                    imgBackground.setImageResource(R.drawable.app_icon);
                }
            });
        } else {
            imgBackground.setImageResource(R.drawable.app_icon);
        }

        if (wallPref.contains(Constants.BACKGROUND_IMAGE)) {
            if (removeWallMenu != null)
                removeWallMenu.setVisible(true);
        } else {
            if (removeWallMenu != null)
                removeWallMenu.setVisible(false);
        }
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
            menu.findItem(R.id.menu_profile).setVisible(true);
        }

        removeWallMenu = menu.findItem(R.id.menu_remove_wallpaper);

        if (LeafPreference.getInstance(getContext()).getInt(LeafPreference.CONST_GROUP_COUNT) > 1 && "constituency".equalsIgnoreCase(BuildConfig.AppCategory)) {
            menu.findItem(R.id.menu_logout).setVisible(false);
            menu.findItem(R.id.menu_change_pass).setVisible(false);
            menu.findItem(R.id.menu_set_wallpaper).setVisible(false);
        } else if (LeafPreference.getInstance(getContext()).getInt(LeafPreference.GROUP_COUNT) > 1) {
            menu.findItem(R.id.menu_logout).setVisible(false);
            menu.findItem(R.id.menu_change_pass).setVisible(false);
            menu.findItem(R.id.menu_set_wallpaper).setVisible(false);
        } else {
            if (wallPref.contains(Constants.BACKGROUND_IMAGE)) {
                removeWallMenu.setVisible(true);
            } else {
                removeWallMenu.setVisible(false);
            }
            menu.findItem(R.id.menu_logout).setVisible(true);
            menu.findItem(R.id.menu_change_pass).setVisible(true);
            menu.findItem(R.id.menu_set_wallpaper).setVisible(true);
        }
        menuItem = menu.findItem(R.id.action_notification_list);
        menuItem.setIcon(buildCounterDrawable(LeafPreference.getInstance(getContext()).getInt(GroupDashboardActivityNew.groupId + "_notification_count")));
        menuItem.setVisible(true);

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
            case R.id.menu_logout:
                logout();
                getActivity().finish();
                return true;
            case R.id.menu_set_wallpaper:
                if (checkPermissionForWriteExternal()) {
                    startGallery(REQUEST_LOAD_GALLERY_IMAGE);
                } else {
                    requestPermissionForWriteExternal(21);
                }
                break;
            case R.id.menu_remove_wallpaper:
                wallPref.edit().clear().commit();
                setBackgroundImage();
                break;
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

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void requestPermissionForWriteExternal(int code) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getActivity(), "Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            AppLog.e(TAG, "requestPermissionForWriteExternal");
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 21) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGallery(REQUEST_LOAD_GALLERY_IMAGE);
            }
        }
    }

    private void startGallery(int requestCode) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOAD_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            final Uri selectedImage = data.getData();

            SMBDialogUtils.showSMBDialogOKCancel(getActivity(), "Do you like to set this as a wallpaper?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectedImage(selectedImage);
                }
            });


        }
    }

    private void selectedImage(Uri selectedImage) {
        try {
           if (selectedImage != null)
                wallPref.edit().putString(Constants.BACKGROUND_IMAGE,selectedImage.toString()).apply();
            setBackgroundImage();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error in set wallpaper", Toast.LENGTH_SHORT).show();
        }
    }

    private void getTeams() {
        List<BaseTeamTable> dataItemList = BaseTeamTable.getTeamList(GroupDashboardActivityNew.groupId);
        if (dataItemList != null && dataItemList.size() > 0) {
            teamList.clear();
            for (int i = 0; i < dataItemList.size(); i++) {
                MyTeamData myTeamData = new MyTeamData();
                myTeamData.teamId = dataItemList.get(i).team_id;
                myTeamData.isClass = dataItemList.get(i).isClass;
                myTeamData.name = dataItemList.get(i).name;
                myTeamData.phone = dataItemList.get(i).phone;
                myTeamData.image = dataItemList.get(i).image;
                myTeamData.groupId = dataItemList.get(i).group_id;
                myTeamData.members = dataItemList.get(i).members;
                myTeamData.canAddUser = dataItemList.get(i).canAddUser;
                myTeamData.teamType = dataItemList.get(i).teamType;
                myTeamData.type = dataItemList.get(i).type;
                myTeamData.enableGps = dataItemList.get(i).enableGps;
                myTeamData.enableAttendance = dataItemList.get(i).enableAttendance;
                myTeamData.isTeamAdmin = dataItemList.get(i).isTeamAdmin;
                myTeamData.allowTeamPostAll = dataItemList.get(i).allowTeamPostAll;
                myTeamData.allowTeamPostCommentAll = dataItemList.get(i).allowTeamPostCommentAll;
                myTeamData.category = dataItemList.get(i).category;
                myTeamData.postUnseenCount = dataItemList.get(i).postUnseenCount;
                myTeamData.role = dataItemList.get(i).role;
                myTeamData.count = dataItemList.get(i).count;
                myTeamData.allowedToAddTeamPost = dataItemList.get(i).allowedToAddTeamPost;
                myTeamData.leaveRequest = dataItemList.get(i).leaveRequest;
                myTeamData.details = new Gson().fromJson(dataItemList.get(i).details, MyTeamData.TeamDetails.class);

                teamList.add(myTeamData);
            }
            mAdapter.notifyDataSetChanged();
        } else {
            apiCall();
        }
    }

    private void apiCall() {
        if (!isConnectionAvailable()) {
            return;
        }
        showLoadingBar(progressBar);
        manager.myTeamList(this, GroupDashboardActivityNew.groupId);
    }

    private void init() {
        pref = LeafPreference.getInstance(getActivity());
        mGroupItem = new Gson().fromJson(LeafPreference.getInstance(getContext()).getString(Constants.GROUP_DATA), GroupItem.class);
        wallPref = getActivity().getSharedPreferences(BuildConfig.APPLICATION_ID + ".wall", Context.MODE_PRIVATE);

        databaseHandler = new DatabaseHandler(getActivity());

        manager = new LeafManager();
        rvTeams = view.findViewById(R.id.rvTeams);
        imgBackground = view.findViewById(R.id.imgBackground);
        progressBar = view.findViewById(R.id.progressBar);
//        PullRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        rvTeams.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mAdapter = new TeamListAdapterNew(teamList, this);
        rvTeams.setAdapter(mAdapter);

//        swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                if (isConnectionAvailable()) {
//                    swipeRefreshLayout.setRefreshing(false);
//                    apiCall();
//                    if (mGroupItem.canPost) {
//                        manager.getGroupDetail(BaseTeamFragment.this, GroupDashboardActivityNew.groupId + "");
//                    }
//                } else {
//                    showNoNetworkMsg();
//                }
//            }
//        });

        // database = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            ((GroupDashboardActivityNew) getActivity()).tvToolbar.setText(GroupDashboardActivityNew.group_name);
            ((GroupDashboardActivityNew) getActivity()).callEventApi();
        }

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
        if (getActivity() == null)
            return;

        if (LeafPreference.getInstance(getActivity()).getInt(LeafPreference.CONST_GROUP_COUNT) > 1 && "constituency".equalsIgnoreCase(mGroupItem.category)) {
            ((GroupDashboardActivityNew) getActivity()).setBackEnabled(true);
        } else if (LeafPreference.getInstance(getActivity()).getInt(LeafPreference.GROUP_COUNT) > 1) {
            ((GroupDashboardActivityNew) getActivity()).setBackEnabled(true);
        } else {
            ((GroupDashboardActivityNew) getActivity()).setBackEnabled(false);
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


    @Override
    public void onTeamClick(MyTeamData team) {
        ((GroupDashboardActivityNew) getActivity()).onTeamSelected(team);
    }

    @Override
    public void onGroupClick(MyTeamData group) {
        ((GroupDashboardActivityNew) getActivity()).groupSelected(group);
    }

    @Override
    public void addTeam() {
        startActivity(new Intent(getActivity(), CreateTeamActivity.class));
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
        if (getActivity() == null)
            return;

        switch (apiId) {
            case LeafManager.API_MY_TEAM_LIST:

                MyTeamsResponse res = (MyTeamsResponse) response;
                List<MyTeamData> result = res.getResults();
                AppLog.e("API", "data " + new Gson().toJson(result));

                BaseTeamTable.deleteTeams(GroupDashboardActivityNew.groupId);
                teamList.clear();
                ArrayList<String> currentTopics = new ArrayList<>();
                for (int i = 0; i < result.size(); i++) {

                    BaseTeamTable baseTeamTable = new BaseTeamTable();
                    MyTeamData item = result.get(i);

                    baseTeamTable.group_id = item.groupId;
                    baseTeamTable.team_id = item.teamId;
                    baseTeamTable.isClass = item.isClass;
                    baseTeamTable.phone = item.phone;
                    baseTeamTable.image = item.image;
                    baseTeamTable.members = item.members;
                    baseTeamTable.canAddUser = item.canAddUser;
                    baseTeamTable.teamType = item.teamType;
                    baseTeamTable.type = item.type;
                    baseTeamTable.enableGps = item.enableGps;
                    baseTeamTable.enableAttendance = item.enableAttendance;
                    baseTeamTable.isTeamAdmin = item.isTeamAdmin;
                    baseTeamTable.allowTeamPostAll = item.allowTeamPostAll;
                    baseTeamTable.allowTeamPostCommentAll = item.allowTeamPostCommentAll;
                    baseTeamTable.category = item.category;
                    baseTeamTable.postUnseenCount = item.postUnseenCount;
                    baseTeamTable.role = item.role;
                    baseTeamTable.count = item.count;
                    baseTeamTable.allowedToAddTeamPost = item.allowedToAddTeamPost;
                    baseTeamTable.leaveRequest = item.leaveRequest;
                    baseTeamTable.details = new Gson().toJson(item.details);

                    try {
                        if (!item.name.equalsIgnoreCase("My Team")) {
                            if (databaseHandler.getCount() != 0) {
                                try {
                                    String name = databaseHandler.getNameFromNum(item.phone.replaceAll(" ", ""));
                                    if (!TextUtils.isEmpty(name)) {
                                        item.name = name + " Team";
                                    }
                                } catch (NullPointerException e) {
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        AppLog.e("CONTACTSS", "error is " + e.toString());
                    }

                    baseTeamTable.name = item.name;
                    baseTeamTable.save();

                    if (!TextUtils.isEmpty(result.get(i).teamId)) {
                        String topics = result.get(i).groupId + "_" + result.get(i).teamId;
                        currentTopics.add(topics);
                    }

                  /*  if (TextUtils.isEmpty(item.teamId) && isVisible) {
                        GroupDashboardActivityNew.total_user=item.members+"";
                        ((GroupDashboardActivityNew)getActivity()).tv_Desc.setText(item.members+" users");
                    }*/

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
            case LeafManager.API_ID_GROUP_DETAIL:
                GroupDetailResponse gRes = (GroupDetailResponse) response;

                AppLog.e(TAG, "group detail ->" + new Gson().toJson(gRes));
                GroupDashboardActivityNew.total_user = gRes.data.get(0).totalUsers + "";
                if (((GroupDashboardActivityNew) getActivity()).isBaseFragment()) {
                    // ((GroupDashboardActivityNew) getActivity()).tv_Desc.setText(GroupDashboardActivityNew.total_user + " users");
                }
//                checkVersionUpdate(gRes.data.get(0).appVersion);
                break;

        }
    }

    private void checkVersionUpdate(int appVersion) {
        if (getActivity() != null) {
            AppLog.e(TAG, "appVersion : " + appVersion);
            AppLog.e(TAG, "BuildConfig.VERSION_CODE : " + BuildConfig.VERSION_CODE);
            if (BuildConfig.VERSION_CODE < appVersion) {
                AppDialog.showUpdateDialog(getActivity(), "New version is available. download new version from play store", new AppDialog.AppUpdateDialogListener() {
                    @Override
                    public void onUpdateClick(DialogInterface dialog) {
                        final String appPackageName = BuildConfig.APPLICATION_ID; // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                });
            } else {
                AppLog.e(TAG, "checkVersionUpdate : latest");
            }
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
        hideLoadingBar();

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
        hideLoadingBar();
        if (getActivity() != null)
            Toast.makeText(getActivity(), getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }

    public void checkAndRefresh(boolean apiCall) {
        if (apiCall) {
            AppLog.e(TAG, "---- Refresh Team -----");
            apiCall();
        }
    }
}
