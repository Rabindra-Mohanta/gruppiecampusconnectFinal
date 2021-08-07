package school.campusconnect.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyz.widget.PullRefreshLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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
import school.campusconnect.adapters.TeamListAdapterNew;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.GroupDetailResponse;
import school.campusconnect.datamodel.GroupItem;
import school.campusconnect.datamodel.TeamListItem;
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

        return view;
    }

    private void setBackgroundImage() {
        if (wallPref.contains(Constants.BACKGROUND_IMAGE)) {
            String path = wallPref.getString(Constants.BACKGROUND_IMAGE, "");
            if (TextUtils.isEmpty(path))
                return;

            File file = new File(path);
            AppLog.e(TAG, "path background : " + path);
            Picasso.with(getActivity()).load(file).into(imgBackground, new Callback() {
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

        menu.findItem(R.id.menu_change_pass).setVisible(true);
        menu.findItem(R.id.menu_set_wallpaper).setVisible(true);
        removeWallMenu = menu.findItem(R.id.menu_remove_wallpaper);
        if (wallPref.contains(Constants.BACKGROUND_IMAGE)) {
            removeWallMenu.setVisible(true);
        } else {
            removeWallMenu.setVisible(false);
        }
        if (LeafPreference.getInstance(getContext()).getInt(LeafPreference.GROUP_COUNT) > 1) {
            menu.findItem(R.id.menu_logout).setVisible(false);
        } else {
            menu.findItem(R.id.menu_logout).setVisible(true);
        }
        menuItem = menu.findItem(R.id.action_notification_list);
        menuItem.setIcon(buildCounterDrawable(GroupDashboardActivityNew.notificationUnseenCount));
        menuItem.setVisible(true);
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

        String path = ImageUtil.getPath(getActivity(), selectedImage);
        try {
            File newFile = new Compressor(getActivity()).setMaxWidth(1000).setQuality(70).compressToFile(new File(path));
            if (newFile != null)
                wallPref.edit().putString(Constants.BACKGROUND_IMAGE, newFile.getAbsolutePath()).apply();
            setBackgroundImage();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error in set wallpaper", Toast.LENGTH_SHORT).show();
        }
    }

    private void getTeams() {
        teamList.clear();
        List<TeamListItem> dataItemList = TeamListItem.getTeamList(GroupDashboardActivityNew.groupId);
        AppLog.e(TAG, "DATABASE size is not 0 --> " + dataItemList.size());

        for (int i = 0; i < dataItemList.size(); i++) {

            MyTeamData myTeamData = new MyTeamData();
            myTeamData.teamId = dataItemList.get(i).team_id;
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
            teamList.add(myTeamData);

        }
        if (teamList.size() > 0) {
            mAdapter.notifyDataSetChanged();
        }

        if (isConnectionAvailable()) {
            showLoadingBar(progressBar);
            manager.myTeamList(this, GroupDashboardActivityNew.groupId);
        }
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
        PullRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        rvTeams.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mAdapter = new TeamListAdapterNew(teamList, this);
        rvTeams.setAdapter(mAdapter);

        swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                if (isConnectionAvailable()) {
                    swipeRefreshLayout.setRefreshing(false);
                    getTeams();
                    if (mGroupItem.canPost) {
                        manager.getGroupDetail(BaseTeamFragment.this, GroupDashboardActivityNew.groupId + "");
                    }
                } else {
                    showNoNetworkMsg();
                }
            }
        });

       // database = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public void onStart() {
        super.onStart();
        ((GroupDashboardActivityNew) getActivity()).tvToolbar.setText(GroupDashboardActivityNew.group_name);
        ((GroupDashboardActivityNew) getActivity()).tv_Desc.setVisibility(View.VISIBLE);
        ((GroupDashboardActivityNew) getActivity()).tv_Desc.setText(GroupDashboardActivityNew.total_user + " users");

        if (getActivity() != null) {

            String re = LeafPreference.getInstance(getActivity()).getString("" + GroupDashboardActivityNew.groupId);

            if (re != null && !TextUtils.isEmpty(re)) {

                List<MyTeamData> result = new Gson().fromJson(re, new TypeToken<List<MyTeamData>>() {
                }.getType());
                teamList.clear();
                teamList.addAll(result);
                mAdapter.notifyDataSetChanged();

            } else {

                getTeams();
                if (mGroupItem.canPost) {
                    manager.getGroupDetail(this, GroupDashboardActivityNew.groupId + "");
                }
            }

            /*if (!LeafPreference.getInstance(getActivity()).getBoolean("home_api")) {
                LeafPreference.getInstance(getActivity()).setBoolean("home_api", true);
                getTeams();
                if (mGroupItem.canPost) {
                    manager.getGroupDetail(this, GroupDashboardActivityNew.groupId + "");
                }
            } else {

                List<MyTeamData> result = new Gson().fromJson(re, new TypeToken<List<MyTeamData>>() {
                }.getType());

                if (re != null && !TextUtils.isEmpty(re)) {
                    teamList.clear();
                    teamList.addAll(result);
                    mAdapter.notifyDataSetChanged();

                   *//* for(int i = 0 ; i < result.size() ; i++)
                    {
                            if(result.get(i).type !=null && result.get(i).type.equalsIgnoreCase(""))
                            {
                                if(result.get(i).teamId !=null && !result.get(i).teamId.equalsIgnoreCase(""))
                                {
                                    final List<PostTeamDataItem> dataItemList = PostTeamDataItem.getTeamPosts(mGroupItem.getGroupId() + "", result.get(i).teamId + "");

                                    Query query = database.child("team_post").child(result.get(i).teamId).orderByKey().equalTo(dataItemList.get(0).id);
                                    query.addValueEventListener(firebaseNewPostListener);
                                    teamsRef.add(query);
                                }
                                else
                                {
                                    List<PostDataItem> dataItemList = PostDataItem.getGeneralPosts(mGroupItem.getGroupId()+"");

                                    Query query = database.child("group_post").child(mGroupItem.getGroupId()).orderByKey().equalTo(dataItemList.get(0).id);
                                    query.addValueEventListener(firebaseNewPostListener);
                                    teamsRef.add(query);
                                }
                            }
                    }*//*

                }
            }*/
        }
    }


    private void firebaseListen(String lastIdFromDB) {
        AppLog.e(TAG, "firebaseListen called : " + lastIdFromDB);
        if (TextUtils.isEmpty(lastIdFromDB)) {
            //  callApi(false);
        } else {

            //  query = myRef.child("team_post").child(team_id).orderByKey().startAfter(lastIdFromDB).limitToFirst(1);
            // query.addListenerForSingleValueEvent(firebaseNewPostListener);
        }
    }

   /* ValueEventListener firebaseNewPostListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            AppLog.e(TAG, "data changed : " + snapshot);
            //  if(snapshot.getValue() !=null)
            //     callApi(true);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };*/

    @Override
    public void onResume() {
        super.onResume();
        isVisible = true;
        if (getActivity() == null)
            return;

        if (LeafPreference.getInstance(getActivity()).getInt(LeafPreference.GROUP_COUNT) > 1) {
            ((GroupDashboardActivityNew) getActivity()).setBackEnabled(true);
        } else {
            ((GroupDashboardActivityNew) getActivity()).setBackEnabled(false);
        }

        if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISTEAMUPDATED)) {
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISTEAMUPDATED, false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isVisible = false;
    }

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

                TeamListItem.deleteAll();
                teamList.clear();
                ArrayList<String> currentTopics = new ArrayList<>();
                for (int i = 0; i < result.size(); i++) {

                    TeamListItem teamListItem = new TeamListItem();
                    MyTeamData item = result.get(i);

                    teamListItem.group_id = item.groupId;
                    teamListItem.team_id = item.teamId;
                    teamListItem.phone = item.phone;
                    teamListItem.image = item.image;
                    teamListItem.members = item.members;
                    teamListItem.canAddUser = item.canAddUser;
                    teamListItem.teamType = item.teamType;
                    teamListItem.type = item.type;
                    teamListItem.enableGps = item.enableGps;
                    teamListItem.enableAttendance = item.enableAttendance;
                    teamListItem.isTeamAdmin = item.isTeamAdmin;
                    teamListItem.allowTeamPostAll = item.allowTeamPostAll;
                    teamListItem.allowTeamPostCommentAll = item.allowTeamPostCommentAll;

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

                    teamListItem.name = item.name;
                    teamListItem.save();

                    if (!TextUtils.isEmpty(result.get(i).teamId)) {
                        String topics = result.get(i).groupId + "_" + result.get(i).teamId;
                        currentTopics.add(topics);
                    }

                  /*  if (TextUtils.isEmpty(item.teamId) && isVisible) {
                        GroupDashboardActivityNew.total_user=item.members+"";
                        ((GroupDashboardActivityNew)getActivity()).tv_Desc.setText(item.members+" users");
                    }*/

                }

                //LeafPreference.getInstance(getActivity()).setString(LeafPreference.HOME_LIST_OFFLINE, new Gson().toJson(result));
                LeafPreference.getInstance(getActivity()).setString("" + GroupDashboardActivityNew.groupId, new Gson().toJson(result));

                teamList.addAll(result);
                mAdapter.notifyDataSetChanged();

                subscribeUnsubscribeTeam(currentTopics);

                break;
            case LeafManager.API_ID_GROUP_DETAIL:
                GroupDetailResponse gRes = (GroupDetailResponse) response;

                AppLog.e(TAG, "group detail ->" + new Gson().toJson(gRes));
                GroupDashboardActivityNew.notificationUnseenCount = gRes.data.get(0).notificationUnseenCount;
                GroupDashboardActivityNew.total_user = gRes.data.get(0).totalUsers + "";
                if (menuItem != null) {
                    menuItem.setIcon(buildCounterDrawable(GroupDashboardActivityNew.notificationUnseenCount));
                }
                if (((GroupDashboardActivityNew) getActivity()).isBaseFragment()) {
                    ((GroupDashboardActivityNew) getActivity()).tv_Desc.setText(GroupDashboardActivityNew.total_user + " users");
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

    public void reloadData() {
       /* rvTeams.scrollToPosition(0);
        mAdapter.setSelectedPos(0);
        if(isConnectionAvailable())
            manager.myTeamList(this, GroupDashboardActivityNew.groupId);
        else
            showNoNetworkMsg();*/
    }
}
