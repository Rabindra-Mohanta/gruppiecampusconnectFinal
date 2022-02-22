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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.activities.ChangePasswordActivity;
import school.campusconnect.activities.CreateTeamActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.ProfileActivity2;
import school.campusconnect.adapters.FeedAdapter;
import school.campusconnect.adapters.TeamListAdapterNew;
import school.campusconnect.adapters.TeamListAdapterNewV2;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.FragmentBaseTeamFragmentv2Binding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.BaseTeamTable;
import school.campusconnect.datamodel.GroupDetailResponse;
import school.campusconnect.datamodel.GroupItem;
import school.campusconnect.datamodel.TeamCountTBL;
import school.campusconnect.datamodel.baseTeam.BaseTeamTableV2;
import school.campusconnect.datamodel.baseTeam.BaseTeamv2Response;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.datamodel.teamdiscussion.MyTeamsResponse;
import school.campusconnect.datamodel.ticket.TicketListResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.views.SMBDialogUtils;

public class BaseTeamFragmentv2 extends BaseFragment implements LeafManager.OnCommunicationListener, TeamListAdapterNewV2.OnTeamClickListener {
    private static final String TAG = "BaseTeamFragmentv2";


    private LeafManager manager;
    private TeamListAdapterNewV2 mAdapter;
    // PullRefreshLayout swipeRefreshLayout;
    DatabaseHandler databaseHandler;
    LeafPreference pref;

    ArrayList<BaseTeamv2Response.TeamListData> teamList = new ArrayList<>();

    boolean isVisible;

    private MenuItem menuItem;

    private MenuItem removeWallMenu;
    private GroupItem mGroupItem;

    FragmentBaseTeamFragmentv2Binding binding;
    public static BaseTeamFragmentv2 newInstance() {
        BaseTeamFragmentv2 fragment = new BaseTeamFragmentv2();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_base_team_fragmentv2, container, false);

        inits();

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
            menu.findItem(R.id.menu_profile).setVisible(true);
        }



        if (LeafPreference.getInstance(getContext()).getInt(LeafPreference.CONST_GROUP_COUNT) > 1 && "constituency".equalsIgnoreCase(BuildConfig.AppCategory)) {
            menu.findItem(R.id.menu_logout).setVisible(false);
            menu.findItem(R.id.menu_change_pass).setVisible(false);

        } else if (LeafPreference.getInstance(getContext()).getInt(LeafPreference.GROUP_COUNT) > 1) {
            menu.findItem(R.id.menu_logout).setVisible(false);
            menu.findItem(R.id.menu_change_pass).setVisible(false);

        } else {

            menu.findItem(R.id.menu_logout).setVisible(true);
            menu.findItem(R.id.menu_change_pass).setVisible(true);
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
            Toast.makeText(getActivity(), "Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
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


        databaseHandler = new DatabaseHandler(getActivity());
        binding.rvFeed.setAdapter(new FeedAdapter());

        mGroupItem = new Gson().fromJson(LeafPreference.getInstance(getContext()).getString(Constants.GROUP_DATA), GroupItem.class);


        manager = new LeafManager();

        mAdapter = new TeamListAdapterNewV2(teamList,this);
        binding.rvTeams.setAdapter(mAdapter);
    }

    private void apiCall() {
        if (!isConnectionAvailable()) {
            return;
        }
        binding.progressBar.setVisibility(View.VISIBLE);
        manager.myTeamListV2(this, GroupDashboardActivityNew.groupId);
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            ((GroupDashboardActivityNew) getActivity()).tvToolbar.setText(GroupDashboardActivityNew.group_name);
            ((GroupDashboardActivityNew) getActivity()).tv_Desc.setVisibility(View.GONE);
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

    public void addTeam() {
        startActivity(new Intent(getActivity(), CreateTeamActivity.class));
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        binding.progressBar.setVisibility(View.GONE);

        if (getActivity() == null)
            return;

        switch (apiId) {
            case LeafManager.API_MY_TEAM_LISTV2:

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

                    baseTeamTable.group_id = data.getFeaturedIconData().get(i).groupId;
                    baseTeamTable.activity = data.getActivity();
                    baseTeamTable.featureIcons = new Gson().toJson(data.getFeaturedIconData());

                    try {
                        if (!data.getFeaturedIconData().get(i).name.equalsIgnoreCase("My Team")) {
                            if (databaseHandler.getCount() != 0) {
                                try {
                                    String name = databaseHandler.getNameFromNum(data.getFeaturedIconData().get(i).phone.replaceAll(" ", ""));
                                    if (!TextUtils.isEmpty(name)) {
                                        data.getFeaturedIconData().get(i).name = name + " Team";
                                    }
                                } catch (NullPointerException e) {
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        AppLog.e("CONTACTSS", "error is " + e.toString());
                    }

                    baseTeamTable.save();

                    if (!TextUtils.isEmpty(data.getFeaturedIconData().get(i).groupId)) {
                        String topics = data.getFeaturedIconData().get(i).groupId + "_" + data.getFeaturedIconData().get(i).teamId;
                        currentTopics.add(topics);
                    }

             /*       MyTeamData item = result.get(i);

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
*/
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
        binding.progressBar.setVisibility(View.GONE);

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
        binding.progressBar.setVisibility(View.GONE);
        if (getActivity() != null)
            Toast.makeText(getActivity(), getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onTeamClick(MyTeamData team) {
        Log.e(TAG,"Team Data :"+new Gson().toJson(team));
        ((GroupDashboardActivityNew) getActivity()).onTeamSelected(team);
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

    private void getTeams() {

        List<BaseTeamTableV2> dataItemList = BaseTeamTableV2.getTeamList(GroupDashboardActivityNew.groupId);

        if (dataItemList != null && dataItemList.size() > 0) {
            teamList.clear();
            for (int i = 0; i < dataItemList.size(); i++) {
                BaseTeamv2Response.TeamListData myTeamData= new BaseTeamv2Response.TeamListData();
                myTeamData.activity = dataItemList.get(i).activity;
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
}