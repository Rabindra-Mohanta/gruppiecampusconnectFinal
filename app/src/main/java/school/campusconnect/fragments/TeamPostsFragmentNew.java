package school.campusconnect.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import school.campusconnect.BuildConfig;
import school.campusconnect.activities.AboutBoothActivity;
import school.campusconnect.activities.AddBoothActivity;
import school.campusconnect.activities.AddBoothStudentActivity;
import school.campusconnect.activities.AddTeamStaffActivity;
import school.campusconnect.activities.AddTeamStudentActivity;
import school.campusconnect.activities.AddTimeTablePostActivity;
import school.campusconnect.activities.AttendanceActivity;
import school.campusconnect.activities.AttendancePareSchool;
import school.campusconnect.activities.AttendanceReportActivity;
import school.campusconnect.activities.CommitteeActivity;
import school.campusconnect.activities.CreateTeamActivity;
import school.campusconnect.activities.GpsActivity;
import school.campusconnect.activities.LeaveActivity;
import school.campusconnect.activities.LikesListActivity;
import school.campusconnect.activities.TeamSettingsActivity;
import school.campusconnect.activities.TeamUsersActivity;
import school.campusconnect.adapters.ReportAdapter;
import school.campusconnect.datamodel.EventTBL;
import school.campusconnect.datamodel.booths.BoothResponse;
import school.campusconnect.datamodel.booths.BoothsTBL;
import school.campusconnect.datamodel.event.HomeTeamDataTBL;
import school.campusconnect.datamodel.event.TeamPostEventModelRes;
import school.campusconnect.datamodel.reportlist.ReportResponse;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.datamodel.teamdiscussion.MyTeamsResponse;
import school.campusconnect.firebase.SendNotificationGlobal;
import school.campusconnect.firebase.SendNotificationModel;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AmazoneImageDownload;
import school.campusconnect.utils.AmazoneRemove;
import school.campusconnect.utils.AmazoneVideoDownload;
import school.campusconnect.utils.AppLog;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import school.campusconnect.R;
import school.campusconnect.activities.TestActivity;
import school.campusconnect.activities.AddPostActivity;
import school.campusconnect.activities.EditPostActivity;
import school.campusconnect.activities.FullScreenActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.LeadsListActivity;
import school.campusconnect.activities.ProfileActivity2;
import school.campusconnect.activities.SelectShareTypeActivity;
import school.campusconnect.activities.ViewPDFActivity;
import school.campusconnect.activities.PushActivity;
import school.campusconnect.adapters.TeamListAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.LayoutListTeamsBinding;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.PostTeamDataItem;
import school.campusconnect.datamodel.reportlist.ReportItemList;
import school.campusconnect.datamodel.teamdiscussion.TeamPostGetData;
import school.campusconnect.datamodel.teamdiscussion.TeamPostGetResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.DateTimeHelper;
import school.campusconnect.utils.MixOperations;
import school.campusconnect.views.SMBDialogUtils;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

/**
 * Created by frenzin04 on 3/29/2017.
 */

public class TeamPostsFragmentNew extends BaseFragment implements LeafManager.OnCommunicationListener,
        DialogInterface.OnClickListener, LeafManager.OnAddUpdateListener<AddPostValidationError>, TeamListAdapter.OnItemClickListener, View.OnClickListener {

    private static final String TAG = "TeamPostsFragmentNew";
    private LayoutListTeamsBinding mBinding;
    private
    TeamListAdapter mAdapter2;
    LeafManager manager = new LeafManager();
    String mGroupId = "";
    int position = -1;
    ArrayList<Integer> birthdayPostCreationQueue ; /// QUEUE TO MAINTAIN BIRTHDAY POST TASKS... SO IT DOESNT REPEAT.

    private int REQUEST_CODE_UPDATE_TEAM = 9;
    public boolean mIsLoading = false;
    public int totalPages2 = 1;
    public int totalPagesBooth = 1;
    public int totalPagesMember = 1;
    public int totalPagesTeam = 1;
    public int currentPage2 = 1;
    public static String team_id;// = 1;
    int count;// = 1;
    public TeamPostGetData currentItem;

    static TeamPostsFragmentNew fragment_new;

    ArrayList<ReportItemList> list = new ArrayList<>();
    private String teamName;
    private LinearLayoutManager manager2;
    private ArrayList<TeamPostGetData> teamPostList = new ArrayList<>();
    private String teamType;
    private MyTeamData teamData;
    private RecyclerView dialogRecyclerView;
    private ProgressBar dialogProgressBar;
    private ReportAdapter mAdapter3;
    private boolean liked;
    private boolean isFromMain;
    private String isBoothClick;
    private String isMemberClick;
    LeafPreference leafPreference;
    private String type;

    private Menu menu;
    public TeamPostsFragmentNew() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        AppLog.e(TAG, "TeamPostsFragmentNew");
        leafPreference = LeafPreference.getInstance(TeamPostsFragmentNew.this.getActivity());
    }



    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);
       /* if(teamType.equals(Constants.TEAM_TYPE_CREATED) && teamData.isTeamAdmin)
        {
            menu.findItem(R.id.menu_edit_team).setVisible(true);
        }
*/
        this.menu = menu;
        menu.findItem(R.id.action_notification_list).setVisible(false);

        if (isFromMain)
        {

            if (teamData.isTeamAdmin || teamData.allowTeamPostAll) {
                menu.findItem(R.id.menu_add_post).setVisible(true);
            }


            menu.findItem(R.id.menu_more).setVisible(true);


            if (teamData.isTeamAdmin) {
                //menu.findItem(R.id.action_settings).setVisible(true);
                menu.findItem(R.id.menu_edit_team).setVisible(true);
                //menu.findItem(R.id.menu_change_admin).setVisible(true);
                menu.findItem(R.id.menu_archive_team).setVisible(true);
                if (GroupDashboardActivityNew.groupCategory.equals(Constants.CATEGORY_SCHOOL) || GroupDashboardActivityNew.groupCategory.equals(Constants.CATEGORY_CONSTITUENCY))
                    menu.findItem(R.id.menu_add_time_table).setVisible(true);
            } else {
               /* if (teamData.isClass && !teamData.allowTeamPostAll) {
                    menu.findItem(R.id.menu_leave_team).setVisible(false);
                } else {
                    menu.findItem(R.id.menu_leave_team).setVisible(true);
                }*/
                menu.findItem(R.id.menu_archive_team).setVisible(false);
            }


            if (teamData.canAddUser || teamData.isTeamAdmin) {
                menu.findItem(R.id.menu_add_friend).setVisible(true);
            } else {
                menu.findItem(R.id.menu_add_friend).setVisible(false);
            }


            if (teamData.leaveRequest) {
                menu.findItem(R.id.menu_leave_request).setVisible(true);
            } else {
                menu.findItem(R.id.menu_leave_request).setVisible(false);
            }

            if (!teamData.isTeamAdmin && teamData.isClass && /*!teamData.allowTeamPostAll &&*/ !teamData.leaveRequest) {
                menu.findItem(R.id.menu_more).setVisible(false);
            }
           /* if (teamData.isTeamAdmin && teamData.enableAttendance) {
                menu.findItem(R.id.menu_get_attendance).setVisible(true);
            }*/


        }
        else
        {

            if (teamData.allowedToAddTeamPost) {
                menu.findItem(R.id.menu_add_post).setVisible(true);
            }
        }

        menu.findItem(R.id.menu_add_time_table).setVisible(false);


        if (true)
        {
            if (type.equalsIgnoreCase("team"))
            {
                Log.e(TAG,"type"+type);
                if (HomeTeamDataTBL.getAll().size()>0)
                {
                    List<HomeTeamDataTBL> homeTeamDataTBLList= HomeTeamDataTBL.getAll();

                    for (int i = 0;i<homeTeamDataTBLList.size();i++)
                    {
                        if (teamData.teamId.equalsIgnoreCase(homeTeamDataTBLList.get(i).teamId))
                        {
                            if (homeTeamDataTBLList.get(i).canPost)
                            {
                                menu.findItem(R.id.menu_add_post).setVisible(true);
                            }
                            else
                            {
                                menu.findItem(R.id.menu_add_post).setVisible(false);
                            }
                        }
                    }


                }

                Log.e(TAG,"teamData "+new Gson().toJson(teamData));

                if (!teamData.isTeamAdmin)
                {
                    menu.findItem(R.id.menu_more).setVisible(false);
                }
            }
            else if (type.equalsIgnoreCase("member"))
            {
                if (!teamData.isTeamAdmin)
                {
                    menu.findItem(R.id.menu_more).setVisible(false);
                }
            }

            if (type.equalsIgnoreCase("team"))
            {
                if (teamData.isTeamAdmin)
                {
                    menu.findItem(R.id.menu_archive_team).setVisible(true);
                }
                else
                {
                    menu.findItem(R.id.menu_archive_team).setVisible(false);
                }
            }
            else
            {
                menu.findItem(R.id.menu_archive_team).setVisible(false);
            }

            if (type.equalsIgnoreCase("booth"))
            {
                menu.findItem(R.id.menu_add_friend).setVisible(false);
                menu.findItem(R.id.menu_about_booth).setVisible(true);
            }

            if (teamData.category.equalsIgnoreCase("booth"))
            {
                menu.findItem(R.id.menu_add_friend).setVisible(false);
                menu.findItem(R.id.menu_about_booth).setVisible(true);
            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_add_post:
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), AddPostActivity.class);
                    intent.putExtra("id", mGroupId);
                    intent.putExtra("type", "team");
                    intent.putExtra("team_id", team_id);
                    intent.putExtra("team_name", teamName);
                    startActivity(intent);
                }
                break;
            case R.id.menu_zoom:
                startMeeting();
                break;


            case R.id.menu_about_booth:
                Intent i = new Intent(getActivity(), AboutBoothActivity.class);
                i.putExtra("class_data",new Gson().toJson(teamData));
                startActivity(i);
                break;

            case R.id.menu_add_friend:

                if ( teamData.subCategory!= null && teamData.subCategory.equalsIgnoreCase("boothPresidents"))
                {
                    show_Dialog(R.array.booth);
                }
                else
                {
                    if ("subBooth".equalsIgnoreCase(teamData.category) || "booth".equalsIgnoreCase(teamData.category) || "constituency".equalsIgnoreCase(teamData.category))
                    {
                        Intent intent = new Intent(getContext(), AddBoothStudentActivity.class);
                        intent.putExtra("group_id", mGroupId);
                        intent.putExtra("team_id", team_id);
                        intent.putExtra("category", teamData.category);
                        startActivity(intent);
                    }
                    else {
                        final AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                        CharSequence items[] = new CharSequence[]{"Add Staff", "Add Students"};
                        adb.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface d, int n) {
                                AppLog.e(TAG, "ss : " + n);
                                d.dismiss();
                                if (n == 0) {
                                    Intent intent = new Intent(getActivity(), AddTeamStaffActivity.class);
                                    intent.putExtra("id", mGroupId);
                                    intent.putExtra("invite", true);
                                    intent.putExtra("from_team", true);
                                    intent.putExtra("team_id", team_id);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(getActivity(), AddTeamStudentActivity.class);
                                    intent.putExtra("id", mGroupId);
                                    intent.putExtra("invite", true);
                                    intent.putExtra("from_team", true);
                                    intent.putExtra("team_id", team_id);
                                    startActivity(intent);
                                }
                            }

                        });
                        adb.setNegativeButton("Cancel", null);
                        AlertDialog dialog = adb.create();
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.show();
                    }
                }

                break;
            case R.id.action_settings:
                Intent intent1 = new Intent(getActivity(), TeamSettingsActivity.class);
                intent1.putExtra("group_id", mGroupId);
                intent1.putExtra("team_id", team_id);
                intent1.putExtra("teamType", teamData.teamType);
                startActivity(intent1);
                break;
            case R.id.menu_change_admin: {
                Intent intent2 = new Intent(getActivity(), TeamUsersActivity.class);
                intent2.putExtra("group_id", mGroupId);
                intent2.putExtra("team_id", team_id);
                startActivity(intent2);
                break;
            }
            case R.id.menu_edit_team:
                Intent addTeamIntent = new Intent(getActivity(), CreateTeamActivity.class);
                addTeamIntent.putExtra("is_edit", true);
                addTeamIntent.putExtra("team_data", new Gson().toJson(teamData));
                startActivityForResult(addTeamIntent,REQUEST_CODE_UPDATE_TEAM);
                break;
            case R.id.menu_leave_team:
                SMBDialogUtils.showSMBDialogOKCancel(getActivity(), getResources().getString(R.string.smb_leave_team), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showLoadingBar(mBinding.progressBar2);
                        LeafManager manager = new LeafManager();
                        manager.leaveTeam(TeamPostsFragmentNew.this, mGroupId, team_id);
                    }
                });
                break;
            case R.id.menu_archive_team:
                SMBDialogUtils.showSMBDialogOKCancel(getActivity(), getResources().getString(R.string.smb_archive) + teamName + getResources().getString(R.string.smb_), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showLoadingBar(mBinding.progressBar2);
                        LeafManager manager = new LeafManager();
                        manager.archiveTeam(TeamPostsFragmentNew.this, mGroupId, team_id);
                    }
                });

                break;
            case R.id.menu_add_time_table:
                Intent intent2 = new Intent(getActivity(), AddTimeTablePostActivity.class);
                intent2.putExtra("group_id", mGroupId);
                intent2.putExtra("team_id", team_id);
                startActivity(intent2);
                break;
            case R.id.menu_leave_request: {
                Intent leaveIntent = new Intent(getActivity(), LeaveActivity.class);
                leaveIntent.putExtra("group_id", mGroupId);
                leaveIntent.putExtra("team_id", team_id);
                startActivity(leaveIntent);
                break;
            }
            case R.id.menu_get_attendance: {
                Intent attendanceIntent = new Intent(getContext(), AttendanceReportActivity.class);
                attendanceIntent.putExtra("team_id", team_id);
                startActivity(attendanceIntent);
                break;
            }


        }
        return super.onOptionsItemSelected(item);
    }

    public void show_Dialog(int resId) {
        SMBDialogUtils.showSMBSingleChoiceDialog(getActivity() ,resId, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListView lw = ((AlertDialog) dialog).getListView();

                switch (lw.getCheckedItemPosition()) {

                    case 0:
                        startActivity(new Intent(getContext(), AddBoothActivity.class));
                        break;
                    case 1:

                        Intent intent = new Intent(getContext(), AddBoothStudentActivity.class);
                        intent.putExtra("group_id", mGroupId);
                        intent.putExtra("team_id", team_id);
                        intent.putExtra("category", teamData.category);
                        startActivity(intent);
                        break;

                }
            }
        });
    }

    public static TeamPostsFragmentNew newInstance(MyTeamData myTeamData, boolean isFromMain,String boothClick,String memberClick) {
        TeamPostsFragmentNew fragment = new TeamPostsFragmentNew();
        Bundle b = new Bundle();
        b.putString("team_data", new Gson().toJson(myTeamData));
        b.putBoolean("isFromMain", isFromMain);
        b.putString("boothClick", boothClick);
        b.putString("memberClick", memberClick);
        AppLog.e(TAG, "newInstance: myTeamData is " + myTeamData);
        fragment.setArguments(b);
        return fragment;
    }

    public static TeamPostsFragmentNew getInstance() {
        return fragment_new;
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_list_teams, container, false);

        mBinding.setSize(0);
        mBinding.setMessage(R.string.msg_no_post);

        ActiveAndroid.initialize(getActivity());

        init();

        getDataLocaly();

        setFloatingButton();

        mBinding.fabTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GpsActivity.class);
                intent.putExtra("isTeamAdmin", teamData.isTeamAdmin);
                intent.putExtra("team_id", teamData.teamId);
                intent.putExtra("group_id", teamData.groupId);
                startActivity(intent);
            }
        });

        mBinding.fabAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("preschool".equalsIgnoreCase(teamData.category)) {
                    Intent intent = new Intent(getActivity(), AttendancePareSchool.class);
                    intent.putExtra("isTeamAdmin", teamData.isTeamAdmin);
                    intent.putExtra("team_id", teamData.teamId);
                    intent.putExtra("group_id", teamData.groupId);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getActivity(), AttendanceActivity.class);
                    intent.putExtra("isTeamAdmin", teamData.isTeamAdmin);
                    intent.putExtra("team_id", teamData.teamId);
                    intent.putExtra("className", teamData.name);
                    intent.putExtra("group_id", teamData.groupId);
                    startActivity(intent);
                }

            }
        });

       /* mBinding.fabJoinMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
            startMeeting();
            }
        });*/

        return mBinding.getRoot();
    }

    private void callApi(boolean isInBackground) {
        if (isConnectionAvailable()) {
            getData2(team_id, isInBackground);
            AppLog.e("TeamPostFrag", "DataFromAPI");
        } else {
            mBinding.setSize(0);
            hideLoadingBar();
           // mBinding.progressBar2.setVisibility(View.GONE);
        }
    }

    private void firebaseListen(String lastIdFromDB, boolean apiEvent) {
        AppLog.e(TAG, "firebaseListen called : " + lastIdFromDB);
        //NOFIREBASEDATABASE
        if (TextUtils.isEmpty(lastIdFromDB) || leafPreference.getInt(team_id + "_post") > 0
                || leafPreference.getBoolean(team_id + "_post_delete") || apiEvent) {
            leafPreference.setBoolean(team_id + "_post_delete", false);
            callApi(true);
        }

    }

    private void startMeeting() {
        /*ZoomSDK.getInstance().getMeetingSettingsHelper().setCustomizedMeetingUIEnabled(false);

        String number = "88529395648";
        String name = "Test Team Meeting";

        JoinMeetingParams params = new JoinMeetingParams();
        params.meetingNo = number;
        params.displayName = name;

        JoinMeetingOptions opts = new JoinMeetingOptions();
        opts.no_driving_mode = true;
        // opts.no_meeting_end_message = true;
        // opts.no_titlebar = false;z
        // opts.meeting_views_options = MeetingViewsOptions.NO_BUTTON_PARTICIPANTS;
        opts.no_bottom_toolbar = false;
        opts.no_invite = true;
        //  opts.no_video = false;//true
        opts.no_share = true;
        opts.no_audio = false;//false;
        opts.no_disconnect_audio = true; // set true
        opts.meeting_views_options = MeetingViewsOptions.NO_BUTTON_PARTICIPANTS;//+ MeetingViewsOptions.NO_BUTTON_AUDIO;//+ MeetingViewsOptions.NO_BUTTON_VIDEO +

        AppLog.e(TAG, " onclcik join");
        ZoomSDK.getInstance().getMeetingService().addListener(this);
        ZoomSDK.getInstance().getMeetingSettingsHelper().setMuteMyMicrophoneWhenJoinMeeting(true);
        ZoomSDK.getInstance().getMeetingService().joinMeetingWithParams(getActivity(), params, opts);
*/
    }

    private void setFloatingButton() {
        if (!isFromMain)
            return;

     //   if (teamData.teamType.equalsIgnoreCase(Constants.TEAM_TYPE_CREATED) && teamData.enableGps) // remove this 9-3-22 (client change)

        if (teamData.enableGps)
        {
            mBinding.fabTrack.setVisibility(View.VISIBLE);
        } else {
            mBinding.fabTrack.setVisibility(View.GONE);
        }

       /* if (teamData.teamType.equalsIgnoreCase(Constants.TEAM_TYPE_CREATED) && teamData.enableAttendance && teamData.isTeamAdmin) {
            mBinding.fabAttendance.setVisibility(View.VISIBLE);
        } else {
            mBinding.fabAttendance.setVisibility(View.GONE);
        }*/
    }

    EventTBL eventTBL;

    private void getDataLocaly() {

        boolean apiEvent = false;

        AppLog.e(TAG,"type"+type);

        if (BuildConfig.AppCategory.equalsIgnoreCase("constituency"))
        {
          /*  if (getArguments().getBoolean("isFromMain"))
            {
                if (type.equalsIgnoreCase("booth"))
                {
                    if (BoothPostEventTBL.getAll().size()>0)
                    {
                        List<BoothPostEventTBL> postEventTBLList= BoothPostEventTBL.getAll();

                        List<PostTeamDataItem> dataItemList = PostTeamDataItem.getTeamPosts(mGroupId + "", team_id + "",type,currentPage2);

                        if (dataItemList.size() > 0)
                        {
                            for (int i = 0;i<postEventTBLList.size();i++)
                            {
                                if (teamData.boothId.equalsIgnoreCase(postEventTBLList.get(i).boothId))
                                {
                                    if (MixOperations.isNewEventUpdate(postEventTBLList.get(i).lastBoothPostAt,"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", dataItemList.get(dataItemList.size()-1)._now))
                                    {
                                        PostTeamDataItem.deleteTeamPosts(team_id,type);
                                        AppLog.e(TAG,"isNewEvent");
                                        callApi(true);
                                        break;
                                    }

                                }
                            }
                        }


                    }
                }
                if (type.equalsIgnoreCase("member"))
                {
                    if (EventSubBoothTBL.getAll().size()>0)
                    {
                        List<EventSubBoothTBL> eventSubBoothTBLS= EventSubBoothTBL.getAll();

                        List<PostTeamDataItem> dataItemList = PostTeamDataItem.getTeamPosts(mGroupId + "", team_id + "",type,currentPage2);

                        if (dataItemList.size() > 0)
                        {
                            for (int i = 0;i<eventSubBoothTBLS.size();i++)
                            {
                                if (team_id.equalsIgnoreCase(eventSubBoothTBLS.get(i).teamId))
                                {
                                    if (MixOperations.isNewEventUpdate(eventSubBoothTBLS.get(i).lastTeamPostAt,"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", dataItemList.get(dataItemList.size()-1)._now))
                                    {
                                        PostTeamDataItem.deleteTeamPosts(team_id,type);
                                        AppLog.e(TAG,"isNewEvent");
                                        callApi(true);
                                        break;
                                    }

                                }
                            }
                        }


                    }
                }
                if (type.equalsIgnoreCase("team"))
                {
                    if (HomeTeamDataTBL.getAll().size()>0)
                    {
                        List<HomeTeamDataTBL> homeTeamDataTBLList= HomeTeamDataTBL.getAll();

                        List<PostTeamDataItem> dataItemList = PostTeamDataItem.getTeamPosts(mGroupId + "", team_id + "",type,currentPage2);

                        if (dataItemList.size() > 0)
                        {
                            for (int i = 0;i<homeTeamDataTBLList.size();i++)
                            {
                                if (teamData.teamId.equalsIgnoreCase(homeTeamDataTBLList.get(i).teamId))
                                {
                                    if (MixOperations.isNewEventUpdate(homeTeamDataTBLList.get(i).lastTeamPostAt,"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", dataItemList.get(dataItemList.size()-1)._now))
                                    {
                                        PostTeamDataItem.deleteTeamPosts(team_id,type);
                                        AppLog.e(TAG,"isNewEvent");
                                        callApi(true);
                                        break;
                                    }

                                }
                            }
                        }


                    }
                }


            }*/

            if (HomeTeamDataTBL.getAll().size()>0)
            {
                List<HomeTeamDataTBL> homeTeamDataTBLList= HomeTeamDataTBL.getAll();

                List<PostTeamDataItem> dataItemList = PostTeamDataItem.getTeamPosts(mGroupId + "", team_id + "",type,currentPage2);

                if (dataItemList.size() > 0)
                {
                    for (int i = 0;i<homeTeamDataTBLList.size();i++)
                    {
                        if (teamData.teamId.equalsIgnoreCase(homeTeamDataTBLList.get(i).teamId))
                        {
                            if (MixOperations.isNewEventUpdate(homeTeamDataTBLList.get(i).lastTeamPostAt,"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", dataItemList.get(dataItemList.size()-1)._now))
                            {
                                PostTeamDataItem.deleteTeamPosts(team_id,type);
                                AppLog.e(TAG,"isNewEvent");
                                callApi(true);
                                break;
                            }

                        }
                    }
                }
            }
        }
        else
        {
            eventTBL = EventTBL.getTeamEvent(mGroupId, team_id);

            if (eventTBL != null) {
                if (eventTBL._now == 0) {
                    apiEvent = true;
                }
                if (MixOperations.isNewEvent(eventTBL.eventAt, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", eventTBL._now)) {
                    apiEvent = true;
                }

            }

        }

        final List<PostTeamDataItem> dataItemList = PostTeamDataItem.getTeamPosts(mGroupId + "", team_id + "",type,currentPage2);
        AppLog.e(TAG, "local list size is " + dataItemList.size());
        String lastId = null;
        showLoadingBar(mBinding.progressBar2);
        if (dataItemList.size() != 0) {

            for (int i = 0; i < dataItemList.size(); i++) {

                TeamPostGetData teamPostGetdata = new TeamPostGetData();

                if (i == 0) {
                    lastId = dataItemList.get(i).id;
                }

                teamPostGetdata.id = dataItemList.get(i).id;
                teamPostGetdata.createdById = dataItemList.get(i).createdById;
                teamPostGetdata.createdBy = dataItemList.get(i).createdBy;
                teamPostGetdata.createdByImage = dataItemList.get(i).createdByImage;
                teamPostGetdata.createdAt = dataItemList.get(i).createdAt;
                teamPostGetdata.title = dataItemList.get(i).title;
                teamPostGetdata.fileType = dataItemList.get(i).fileType;
                teamPostGetdata.fileName = new Gson().fromJson(dataItemList.get(i).fileName, new TypeToken<ArrayList<String>>() {
                }.getType());
                teamPostGetdata.thumbnailImage = new Gson().fromJson(dataItemList.get(i).thumbnailImage, new TypeToken<ArrayList<String>>() {
                }.getType());
                teamPostGetdata.updatedAt = dataItemList.get(i).updatedAt;
                teamPostGetdata.text = dataItemList.get(i).text;
                teamPostGetdata.imageWidth = dataItemList.get(i).imageWidth;
                teamPostGetdata.imageHeight = dataItemList.get(i).imageHeight;
                teamPostGetdata.video = dataItemList.get(i).video;
                teamPostGetdata.text = dataItemList.get(i).text;
                teamPostGetdata.likes = dataItemList.get(i).likes;
                teamPostGetdata.comments = dataItemList.get(i).comments;
                teamPostGetdata.isLiked = dataItemList.get(i).isLiked;
                teamPostGetdata.canEdit = dataItemList.get(i).canEdit;
                teamPostGetdata.phone = dataItemList.get(i).phone;
                teamPostGetdata.thumbnail = dataItemList.get(i).thumbnail;
                teamPostGetdata.isFavourited = dataItemList.get(i).isFavourited;

                teamPostList.add(teamPostGetdata);
            }
            hideLoadingBar();
            AppLog.e(TAG, "DataFromLocal");
            mAdapter2.notifyDataSetChanged();
            mBinding.setSize(mAdapter2.getItemCount());


            firebaseListen(lastId, apiEvent);


        } else {


            firebaseListen("", apiEvent);
        }


        mBinding.recyclerView2.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = manager2.getChildCount();
                int totalItemCount = manager2.getItemCount();
                int firstVisibleItemPosition = manager2.findFirstVisibleItemPosition();
//                AppLog.e("TeamFragment", "visibleItemCount,totalItemCount,firstVisibleItemPosition=" + visibleItemCount + "," + totalItemCount + "," + firstVisibleItemPosition);
//                AppLog.e("TeamFragment", "mIsLoading,totalPages2,currentPage2=" + mIsLoading + "," + totalPages2 + "," + currentPage2);
                if (!mIsLoading && totalPages2 > currentPage2) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        currentPage2 = currentPage2 + 1;
                        getDataLocaly();
                    }
                }
            }
        });
        mBinding.swipeRefreshLayout2.setEnabled(false);

    //    mBinding.swipeRefreshLayout2.setEnabled(false);
       /* mBinding.swipeRefreshLayout2.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    currentPage2 = 1;
                    getData2(team_id, false);
                    mBinding.swipeRefreshLayout2.setRefreshing(false);
                } else {
                    showNoNetworkMsg();
                    mBinding.swipeRefreshLayout2.setRefreshing(false);
                }
            }
        });*/
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter2.notifyDataSetChanged();

        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateReceiver, new IntentFilter("UPDATE_TEAM"));
        }
        getContext().registerReceiver(updateReceiver,new IntentFilter("postadded"));
    }

    BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if("postadded".equalsIgnoreCase(intent.getAction())){
                PostTeamDataItem.deleteTeamPosts(team_id,type);
                AppLog.e(TAG," onPostExecute isNewEvent");
                callApi(true);
            }

            if("UPDATE_TEAM".equalsIgnoreCase(intent.getAction())){
                PostTeamDataItem.deleteTeamPosts(team_id,type);
                AppLog.e(TAG," onPostExecute isNewEvent");
                callApi(true);
            }
        }
    };
    @Override
    public void onDestroy() {
        super.onDestroy();

        mAdapter2.RemoveAll();
        //NOFIREBASEDATABASE
     /*   if(query !=null)
            query.removeEventListener(firebaseNewPostListener);*/

    }

    private void saveTotalPage(int totalPages2) {


        if (totalPages2 != 0)
        {
            LeafPreference.getInstance(getContext()).setString(mGroupId+"_"+team_id+"_"+type,String.valueOf(totalPages2));
        }
    }

    private void init() {

        DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
        count = databaseHandler.getCount();

        currentPage2 = 1;
        fragment_new = this;

        teamData = new Gson().fromJson(getArguments().getString("team_data"), MyTeamData.class);
        isFromMain = getArguments().getBoolean("isFromMain", false);
        isBoothClick = getArguments().getString("boothClick");
        isMemberClick = getArguments().getString("memberClick");

        if (isBoothClick != null && isBoothClick.equalsIgnoreCase("yes"))
        {
            type = "booth";
        }
        else if (isMemberClick != null && isMemberClick.equalsIgnoreCase("yes"))
        {
            type = "member";
        }
        else
        {
            type = "team";
        }

        AppLog.e(TAG, "isBoothClick : " + isBoothClick);
        AppLog.e(TAG, "teamData : " + new Gson().fromJson(getArguments().getString("team_data"), MyTeamData.class));
        AppLog.e(TAG, "type : " + type);
        AppLog.e(TAG, "isFromMain : " + isFromMain);

        mGroupId = !TextUtils.isEmpty(teamData.groupId) ? teamData.groupId : GroupDashboardActivityNew.groupId;

        if (type.equalsIgnoreCase("member"))
        {
            team_id = teamData.boothId;
        }
        else {
            team_id = teamData.teamId;
        }


        if (!LeafPreference.getInstance(getContext()).getString(mGroupId+"_"+team_id+"_"+type).isEmpty())
        {
            totalPages2 = Integer.parseInt(LeafPreference.getInstance(getContext()).getString(mGroupId+"_"+team_id+"_"+type));
        }

        teamName = teamData.name;
        teamType = teamData.teamType;

        mBinding.llBlankView.setOnClickListener(this);
        mBinding.llReport.setOnClickListener(this);
        mBinding.llAskDoubt.setOnClickListener(this);
        mBinding.llRemovePost.setOnClickListener(this);

        manager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView2.setLayoutManager(manager2);
        mAdapter2 = new TeamListAdapter(teamPostList, this, "team", mGroupId, team_id, count, databaseHandler, teamData);
        mBinding.recyclerView2.setAdapter(mAdapter2);


/*
        CHANGES 2-6-22 (MEMBER COUNT PROBLEM)

        if(!type.equalsIgnoreCase("team"))
        {
            callEventApiTeamPost();
        }*/

        callEventApiTeamPost();


        if (getActivity() instanceof GroupDashboardActivityNew) {

          /*  ((GroupDashboardActivityNew) getActivity()).tv_Desc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!teamData.isTeamAdmin && !teamData.allowTeamPostAll)
                        return;

                    try {
                        Intent intent = new Intent(getActivity(), LeadsListActivity.class);
                        intent.putExtra("id", mGroupId);
                        intent.putExtra("team_id", team_id);
                        intent.putExtra("team_name", teamName);
                        intent.putExtra("isAdmin", teamData.isTeamAdmin);
                        startActivity(intent);
                    } catch (Exception e) {
                        AppLog.e("floating", "error is " + e.toString());
                    }
                }
            });*/

            ((GroupDashboardActivityNew) getActivity()).tvToolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int members = 0;

                    if (HomeTeamDataTBL.getTeamPost(team_id).size()>0)
                    {
                        List<HomeTeamDataTBL> homeTeamDataTBLList= HomeTeamDataTBL.getTeamPost(team_id);

                        for (int i = 0;i<homeTeamDataTBLList.size();i++)
                        {
                            if (team_id.equalsIgnoreCase(homeTeamDataTBLList.get(i).teamId))
                            {
                                members = homeTeamDataTBLList.get(i).members;
                            }
                        }

                    }

                    if (isBoothClick != null && isBoothClick.equalsIgnoreCase("yes"))
                    {
                        Intent intent = new Intent(getContext(), CommitteeActivity.class);
                        intent.putExtra("class_data",new Gson().toJson(teamData));
                        intent.putExtra("title",teamData.name);
                        intent.putExtra("team_count", members);
                        intent.putExtra("isBoothClick","yes");
                        startActivity(intent);
                    }
                    else
                    {
                        if (!teamData.isTeamAdmin && !teamData.allowTeamPostAll)
                            return;

                        if (teamData.category.equalsIgnoreCase("booth"))
                        {
                            Intent intent = new Intent(getContext(), CommitteeActivity.class);
                            intent.putExtra("class_data",new Gson().toJson(teamData));
                            intent.putExtra("title",teamData.name);
                            intent.putExtra("team_count", members);
                            intent.putExtra("isBoothClick","yes");
                            startActivity(intent);
                        }
                        else {
                            try {
                                Intent intent = new Intent(getContext(), LeadsListActivity.class);
                                intent.putExtra("id", mGroupId);
                                intent.putExtra("apiCall", true);
                                intent.putExtra("team_id", team_id);
                                intent.putExtra("class_data",new Gson().toJson(teamData));
                                intent.putExtra("team_name", teamName);
                                intent.putExtra("team_count", members);
                                intent.putExtra("isAdmin", teamData.isTeamAdmin);
                                startActivity(intent);
                                AppLog.e("Team id : ", team_id + "");
                            }
                            catch (Exception e) {
                                AppLog.e("floating", "error is " + e.toString());
                            }
                        }
                    }
                }
            });
        }

       /* if(getActivity() instanceof NestedTeamActivity)
        {
            ((NestedTeamActivity) getActivity()).tvTitle.setText(teamData.name);
            ((NestedTeamActivity) getActivity()).tv_Desc.setText(teamData.members+" users");
            ((NestedTeamActivity) getActivity()).lin_toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(getActivity(), LeadsListActivity.class);
                        intent.putExtra("id", mGroupId);
                        intent.putExtra("team_id", team_id);
                        intent.putExtra("team_name", teamName);
                        intent.putExtra("isAdmin", teamData.isTeamAdmin);
                        startActivity(intent);
                        AppLog.e("Team id : ",team_id+"");
                    } catch (Exception e) {
                        AppLog.e("floating", "error is " + e.toString());
                    }
                }
            });
        }*/

    }

    public void callEventApiTeamPost() {
        // new EventAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        LeafManager leafManager = new LeafManager();
        leafManager.getTeamPostEvent(new LeafManager.OnCommunicationListener() {
            @Override
            public void onSuccess(int apiId, BaseResponse response) {

                AppLog.e(TAG, "onSuccess : " + response);

                TeamPostEventModelRes res = (TeamPostEventModelRes) response;

                if(getActivity() != null)
                {
                     ((GroupDashboardActivityNew) getActivity()).tv_Desc.setText(getResources().getString(R.string.lbl_members)+" : "+String.valueOf(res.getData().get(0).getMembers()));
                }
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
        }, GroupDashboardActivityNew.groupId,team_id);
    }


    @Override
    public void onResume() {
        super.onResume();
        hide_keyboard();


        AppLog.e("TeamPost", "OnResume : " + LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISTEAMPOSTUPDATED));
        if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISTEAMPOSTUPDATED)) {
            currentPage2 = 1;
            getData2(team_id, false);
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISTEAMPOSTUPDATED, false);
        }

        if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISTEAMUPDATED)) {

            if (GroupDashboardActivityNew.mGroupItem.canPost)
            {
                 if ("subBooth".equalsIgnoreCase(teamData.category) || "booth".equalsIgnoreCase(teamData.category) || "constituency".equalsIgnoreCase(teamData.category)) {
                    manager.getBooths(this, GroupDashboardActivityNew.groupId,"");
                } else {
                    manager.myTeamList(this, GroupDashboardActivityNew.groupId);
                }
            }
            //((GroupDashboardActivityNew)getActivity()).HomeClick();
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISTEAMUPDATED, false);
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        hideLoadingBar();

        switch (apiId) {
            case LeafManager.API_TEAM_POST_LIST:

                TeamPostGetResponse res2 = (TeamPostGetResponse) response;

                totalPages2 = res2.totalNumberOfPages;

                saveTotalPage(totalPages2);

                mIsLoading = false;

                if (currentPage2 == 1) {
                    PostTeamDataItem.deleteTeamPosts(team_id,type);
                    teamPostList.clear();
                }
                teamPostList.addAll(res2.getResults());
                mBinding.setSize(mAdapter2.getItemCount());
                mAdapter2.notifyDataSetChanged();

                saveTeamPost(res2.getResults());

                if (eventTBL != null) {
                    eventTBL._now = System.currentTimeMillis();
                    eventTBL.save();
                }
                break;

            case LeafManager.API_ID_FAV:
                if (response.status.equalsIgnoreCase("favourite")) {
                    teamPostList.get(position).isFavourited = true;
                } else {
                    teamPostList.get(position).isFavourited = false;
                }
                mAdapter2.notifyItemChanged(position);
                TeamPostGetData select = teamPostList.get(position);
                PostTeamDataItem.updateFav(select.id, select.isFavourited ? 1 : 0);
                break;


            case LeafManager.API_ID_LIKE_TEAM:

                if (response.status.equalsIgnoreCase("liked")) {
                    teamPostList.get(position).isLiked = true;
                    teamPostList.get(position).likes++;
                } else {
                    teamPostList.get(position).isLiked = false;
                    teamPostList.get(position).likes--;
                }
                mAdapter2.notifyItemChanged(position);
                liked = false;

                TeamPostGetData select2 = teamPostList.get(position);
                PostTeamDataItem.updateLike(select2.id, select2.isLiked ? 1 : 0, select2.likes);

                break;

            case LeafManager.API_ID_DELETE_POST:
                Toast.makeText(getContext(), "Post Deleted Successfully", Toast.LENGTH_SHORT).show();
                currentPage2 = 1;
                getData2(team_id, false);
                AmazoneRemove.remove(currentItem.fileName);
                sendNotification(currentItem);
                break;

            case LeafManager.API_REPORT_LIST:
                hideLoadingBar();
                ReportResponse response1 = (ReportResponse) response;

                list.clear();

                for (int i = 0; i < response1.data.size(); i++) {
                    ReportItemList reportItemList = new ReportItemList(response1.data.get(i).type, response1.data.get(i).reason, false);
                    list.add(reportItemList);
                }

                ReportAdapter.selected_position = -1;
                mAdapter3 = new ReportAdapter(list);
                AppLog.e("adas ", mAdapter3.getItemCount() + "");
                AppLog.e("ReportResponse", "onSucces  ,, msg : " + ((ReportResponse) response).data.toString());
                dialogRecyclerView.setAdapter(mAdapter3);
                break;

            case LeafManager.API_REPORT:
                hideLoadingBar();
                Toast.makeText(getContext(), "Post Reported Successfully", Toast.LENGTH_SHORT).show();
                break;
            case LeafManager.API_ID_LEAVE_TEAM:
                Toast.makeText(getContext(), "Leave Team Successfully", Toast.LENGTH_SHORT).show();
                /*((BaseTeamFragment)getParentFragment()).reloadData();*/
                getActivity().onBackPressed();
                break;
            case LeafManager.API_ID_ARCHIVE_TEAM:
                Toast.makeText(getContext(), "Archive Team Successfully", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
                break;

            case LeafManager.API_MY_TEAM_LIST: {
                MyTeamsResponse res = (MyTeamsResponse) response;
                List<MyTeamData> result = res.getResults();
                AppLog.e(TAG, "Team list Res :: " + new Gson().toJson(result));
                boolean isFind = false;
                for (MyTeamData myTeam : result) {
                    if (team_id.equals(myTeam.teamId)) {
                        teamData = myTeam;
                        teamName = teamData.name;
                        isFind = true;
                        refreshData();
                        break;
                    }
                }
                /*if (!isFind)
                    getActivity().onBackPressed();*/
            }
            break;
            case LeafManager.API_GET_BOOTHS:
                BoothResponse res = (BoothResponse) response;
                List<MyTeamData> result = res.getData();
                AppLog.e(TAG, "Team list Res :: " + new Gson().toJson(result));
                boolean isFind = false;
                for (MyTeamData myTeam : result) {
                    if (team_id.equals(myTeam.teamId)) {
                        teamData = myTeam;
                        teamName = teamData.name;
                        isFind = true;
                        refreshData();
                        break;
                    }
                }
               /* if (!isFind)
                    getActivity().onBackPressed();*/
                break;


        }

    }



    private void refreshData() {
        setFloatingButton();
        if (getActivity() != null) {
            ((GroupDashboardActivityNew) getActivity()).tvToolbar.setText(teamData.name);
          //  ((GroupDashboardActivityNew) getActivity()).tv_Desc.setText("Members : " + teamData.members);
        }
    }

    private void sendNotification(TeamPostGetData currentItem) {

        SendNotificationModel notificationModel = new SendNotificationModel();
        notificationModel.to = "/topics/" + mGroupId + "_" + team_id;
        notificationModel.data.title = getResources().getString(R.string.app_name);
        notificationModel.data.body = "Team Post deleted";
        notificationModel.data.Notification_type = "DELETE_POST";
        notificationModel.data.iSNotificationSilent = true;
        notificationModel.data.groupId = mGroupId;
        notificationModel.data.teamId = team_id;
        notificationModel.data.createdById = LeafPreference.getInstance(getActivity()).getString(LeafPreference.LOGIN_ID);
        notificationModel.data.postId = currentItem.id;
        notificationModel.data.postType = "team";
        SendNotificationGlobal.send(notificationModel);
    }

    private void saveTeamPost(List<TeamPostGetData> results) {
        for (int i = 0; i < results.size(); i++) {

            TeamPostGetData item = results.get(i);

            PostTeamDataItem postItem = new PostTeamDataItem();

            postItem.id = item.id;
            postItem.createdById = item.createdById;
            postItem.createdBy = item.createdBy;
            postItem.createdByImage = item.createdByImage;
            postItem.createdAt = item.createdAt;
            postItem.title = item.title;
            postItem.fileType = item.fileType;
            if (item.fileName != null) {
                postItem.fileName = new Gson().toJson(item.fileName);
                postItem.thumbnailImage = new Gson().toJson(item.thumbnailImage);
            }
            postItem.updatedAt = item.updatedAt;
            postItem.text = item.text;
            postItem.imageHeight = item.imageHeight;
            postItem.imageWidth = item.imageWidth;
            postItem.video = item.video;
            postItem.text = item.text;
            postItem.likes = item.likes;
            postItem.comments = item.comments;
            postItem.isLiked = item.isLiked;
            postItem.canEdit = item.canEdit;
            postItem.phone = item.phone;
            postItem.group_id = mGroupId + "";
            postItem.team_id = team_id + "";
            postItem.thumbnail = item.thumbnail;
            postItem.isFavourited = item.isFavourited;
            postItem.type = type;
            postItem.page = currentPage2;

            AppLog.e(TAG,"type"+type);


            if (true)
            {
                if (getArguments().getBoolean("isFromMain"))
                {
                   /* if (type.equalsIgnoreCase("booth"))
                    {
                        if (BoothPostEventTBL.getAll().size()>0)
                        {
                            List<BoothPostEventTBL> postEventTBLList= BoothPostEventTBL.getAll();

                            for (int n = 0;n<postEventTBLList.size();n++)
                            {
                                if (teamData.boothId.equalsIgnoreCase(postEventTBLList.get(n).boothId))
                                {
                                    postItem._now = postEventTBLList.get(n).lastBoothPostAt;

                                }
                            }

                        }
                        else
                        {
                            postItem._now = DateTimeHelper.getCurrentTime();
                        }
                    }
                    if (type.equalsIgnoreCase("member"))
                    {
                        if (EventSubBoothTBL.getAll().size()>0)
                        {
                            List<EventSubBoothTBL> eventSubBoothTBLS= EventSubBoothTBL.getAll();

                            for (int j = 0;j<eventSubBoothTBLS.size();j++)
                            {
                                if (teamData.boothId.equalsIgnoreCase(eventSubBoothTBLS.get(j).teamId))
                                {

                                    postItem._now = eventSubBoothTBLS.get(j).lastTeamPostAt;
                                }

                            }

                        }
                        else
                        {
                            postItem._now = DateTimeHelper.getCurrentTime();
                        }
                    }
                    if (type.equalsIgnoreCase("team"))
                    {
                        if (HomeTeamDataTBL.getAll().size()>0)
                        {
                            List<HomeTeamDataTBL> homeTeamDataTBLList= HomeTeamDataTBL.getAll();

                            for (int m = 0;m<homeTeamDataTBLList.size();m++)
                            {
                                if (teamData.teamId.equalsIgnoreCase(homeTeamDataTBLList.get(m).teamId))
                                {
                                    postItem._now = homeTeamDataTBLList.get(m).lastTeamPostAt;
                                }
                            }
                        }
                        else
                        {
                            postItem._now = DateTimeHelper.getCurrentTime();
                        }
                    }
*/

                    if (HomeTeamDataTBL.getAll().size()>0)
                    {
                        List<HomeTeamDataTBL> homeTeamDataTBLList= HomeTeamDataTBL.getAll();

                        for (int m = 0;m<homeTeamDataTBLList.size();m++)
                        {
                            if (team_id.equalsIgnoreCase(homeTeamDataTBLList.get(m).teamId))
                            {
                                postItem._now = homeTeamDataTBLList.get(m).lastTeamPostAt;
                            }
                        }
                    }
                    else
                    {
                        postItem._now = DateTimeHelper.getCurrentTime();
                    }
                }
            }

            postItem.save();
        }
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<AddPostValidationError> error) {
        hideLoadingBar();
        liked = false;
        mIsLoading = false;
        try {
            currentPage2 = currentPage2 - 1;
            if (currentPage2 < 0) {
                currentPage2 = 1;
            }

            AppLog.e("GeneralPostFragment", "onFailure  ,, msg : " + error);
            if (error.status.equals("401")) {
                Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
                logout();
            } else if (error.status.equals("404")) {
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_post), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), error.title, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
        }


    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        liked = false;
        mIsLoading = false;
        try {

            currentPage2 = currentPage2 - 1;
            if (currentPage2 < 0) {
                currentPage2 = 1;
            }

            if (msg.contains("401:Unauthorized")) {
                Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
                logout();
            } else if (msg.contains("404")) {
                Toast.makeText(getActivity(), "No posts available.", Toast.LENGTH_SHORT).show();
                logout();
            } else if (msg.contains("418")) {
                if (apiId == LeafManager.API_REPORT)
                    Toast.makeText(getActivity(), "You have already reported this post", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
        }

    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        liked = false;
        mIsLoading = false;

        currentPage2 = currentPage2 - 1;
        if (currentPage2 < 0) {
            currentPage2 = 1;
        }

    }

    private void getData2(String team_id, boolean isInBackground) {
        if (!isInBackground) {
            showLoadingBar(mBinding.progressBar2);
            mIsLoading = true;
        }
        manager.teamPostGetApi(this, mGroupId + "", team_id + "", getActivity(), currentPage2);
        leafPreference.remove(team_id + "_post");

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        AppLog.e("TeamPostFrag", "DIalog Ok Clicked ");

        if (isConnectionAvailable()) {
            showLoadingBar(mBinding.progressBar2);

            LeafManager manager = new LeafManager();
            manager.deleteTeamPost(this, mGroupId + "", team_id + "", currentItem.id);
        } else {
            showNoNetworkMsg();
        }

    }

    @Override
    public void onFavClick(TeamPostGetData item, int pos) {
        showLoadingBar(mBinding.progressBar2);
        position = pos;
        manager.setFav(this, mGroupId + "", item.id);

    }

    @Override
    public void onLikeClick(TeamPostGetData item, int pos) {
        if (!liked) {
            liked = true;
            this.position = pos;

            //calling api in background
        //    showLoadingBar(mBinding.progressBar2);
      //      manager.setTeamLike(this, mGroupId + "", team_id, item.id);

            LeafManager leafManager = new LeafManager();
            leafManager.setTeamLike(new LeafManager.OnCommunicationListener() {
                @Override
                public void onSuccess(int apiId, BaseResponse response) {

                    new LikeAsync(response).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


                }

                @Override
                public void onFailure(int apiId, String msg) {

                }

                @Override
                public void onException(int apiId, String msg) {

                }
            },mGroupId + "", team_id, item.id);



        }
    }



    @Override
    public void onPostClick(TeamPostGetData item) {
        if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {

            Intent browserIntent = new Intent(getActivity(), TestActivity.class);
            browserIntent.putExtra("url", item.video);
            startActivity(browserIntent);

        } else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
            Intent i = new Intent(getActivity(), ViewPDFActivity.class);
            i.putExtra("pdf", item.fileName.get(0));
            i.putExtra("thumbnail", item.thumbnailImage.get(0));
            i.putExtra("name", item.title);
            startActivity(i);

        } else if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
            Intent i = new Intent(getActivity(), FullScreenActivity.class);
            i.putExtra("image", item.fileName);
            startActivity(i);
        }
    }

    @Override
    public void onReadMoreClick(TeamPostGetData item) {

    }

    @Override
    public void onEditClick(TeamPostGetData item) {
        Intent intent = new Intent(getActivity(), EditPostActivity.class);
        intent.putExtra("id", mGroupId);
        intent.putExtra("post_id", item.id);
        intent.putExtra("team_id", team_id);
        intent.putExtra("title", item.title);
        intent.putExtra("desc", item.text);
        intent.putExtra("type", "team");
        AppLog.e("TeamPostFrag", "data for Edit : " + item.text + " and short : " + item.text);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(TeamPostGetData item) {
        currentItem = item;
        SMBDialogUtils.showSMBDialogOKCancel(getActivity(), getResources().getString(R.string.dialog_are_you_want_to_delete), this);
    }

    @Override
    public void onReportClick(TeamPostGetData item) {
        currentItem = item;
        showReportDialog();
    }

    @Override
    public void onExternalShareClick(TeamPostGetData item) {
        //chanage share data Post share option to external applications

        boolean isDownloaded = true;
        String type;

        if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {


            if (item.fileName.size()> 0)
            {
                for (int i = 0;i<item.fileName.size();i++)
                {
                    if (!AmazoneImageDownload.isImageDownloaded((item.fileName.get(i))))
                    {
                        isDownloaded = false;
                    }
                }

                if (isDownloaded)
                {
                    ArrayList<File> files =new ArrayList<>();

                    for (int i = 0;i<item.fileName.size();i++)
                    {
                        AppLog.e(TAG, "URL DECODE"+Constants.decodeUrlToBase64(item.fileName.get(i)));

                        files.add(AmazoneImageDownload.getDownloadPath(item.fileName.get(i)));
                    }

                    ArrayList<Uri> uris = new ArrayList<>();

                    for(File file: files){

                        AppLog.e(TAG, "URL "+file.getAbsolutePath());

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            uris.add(FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file));
                        } else {
                            uris.add(Uri.fromFile(file));
                        }

                    }

                    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    intent.setType("image/");
                    intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    startActivity(Intent.createChooser(intent, "Share File"));
                }
                else
                {
                    Toast.makeText(getContext(),getResources().getString(R.string.smb_no_file_download),Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(getContext(),getResources().getString(R.string.smb_no_file_attached),Toast.LENGTH_SHORT).show();
            }


        }
        else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {

            if (item.fileName.size()> 0)
            {

                for (int i = 0;i<item.fileName.size();i++)
                {
                    if (!AmazoneDownload.isPdfDownloaded((item.fileName.get(i))))
                    {
                        isDownloaded = false;
                    }
                }

                if (isDownloaded)
                {
                    ArrayList<File> files =new ArrayList<>();

                    for (int i = 0;i<item.fileName.size();i++)
                    {
                        AppLog.e(TAG, "URL DECODE"+Constants.decodeUrlToBase64(item.fileName.get(i)));

                        files.add(AmazoneDownload.getDownloadPath(item.fileName.get(i)));
                    }

                    ArrayList<Uri> uris = new ArrayList<>();

                    for(File file: files){

                        AppLog.e(TAG, "URL "+file.getAbsolutePath());

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            uris.add(FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file));
                        } else {
                            uris.add(Uri.fromFile(file));
                        }

                    }

                    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    intent.setType("application/pdf");
                    intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    startActivity(Intent.createChooser(intent, "Share File"));
                }
                else
                {
                    Toast.makeText(getContext(),getResources().getString(R.string.smb_no_file_download),Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(getContext(),getResources().getString(R.string.smb_no_file_attached),Toast.LENGTH_SHORT).show();
            }

        }
        else if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, item.video);
            intent.setType("text/plain");
            startActivity(intent);
        }
        else if(item.fileType.equals(Constants.FILE_TYPE_VIDEO)){

            if (item.fileName.size()> 0)
            {

                for (int i = 0;i<item.fileName.size();i++)
                {
                    if (!AmazoneVideoDownload.isVideoDownloaded((item.fileName.get(i))))
                    {
                        isDownloaded = false;
                    }
                }

                if (isDownloaded)
                {
                    ArrayList<File> files =new ArrayList<>();

                    for (int i = 0;i<item.fileName.size();i++)
                    {
                        AppLog.e(TAG, "URL DECODE"+Constants.decodeUrlToBase64(item.fileName.get(i)));

                        files.add(AmazoneVideoDownload.getDownloadPath(item.fileName.get(i)));
                    }

                    ArrayList<Uri> uris = new ArrayList<>();

                    for(File file: files){

                        AppLog.e(TAG, "URL "+file.getAbsolutePath());

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            uris.add(FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file));
                        } else {
                            uris.add(Uri.fromFile(file));
                        }

                    }

                    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    intent.setType("video/*");
                    intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    startActivity(Intent.createChooser(intent, "Share File"));
                }
                else
                {
                    Toast.makeText(getContext(),getResources().getString(R.string.smb_no_file_download),Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(getContext(),getResources().getString(R.string.smb_no_file_attached),Toast.LENGTH_SHORT).show();
            }

        }
        else if(item.fileType.equalsIgnoreCase("birthdaypost")){

            if (item.fileName.size()> 0)
            {

                for (int i = 0;i<item.fileName.size();i++)
                {
                    if (!AmazoneImageDownload.isImageDownloaded((item.fileName.get(i))))
                    {
                        isDownloaded = false;
                    }
                }

                if (isDownloaded)
                {
                    ArrayList<File> files =new ArrayList<>();

                    for (int i = 0;i<item.fileName.size();i++)
                    {
                        AppLog.e(TAG, "URL DECODE"+Constants.decodeUrlToBase64(item.fileName.get(i)));

                        files.add(AmazoneImageDownload.getDownloadPath(item.fileName.get(i)));
                    }

                    ArrayList<Uri> uris = new ArrayList<>();

                    for(File file: files){

                        AppLog.e(TAG, "URL "+file.getAbsolutePath());

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            uris.add(FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file));
                        } else {
                            uris.add(Uri.fromFile(file));
                        }

                    }

                    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    intent.setType("image/");
                    intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    startActivity(Intent.createChooser(intent, "Share File"));
                }
                else
                {
                    Toast.makeText(getContext(),getResources().getString(R.string.smb_no_file_download),Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(getContext(),getResources().getString(R.string.smb_no_file_attached),Toast.LENGTH_SHORT).show();
            }
        }

     /*   if (!item.fileType.equals(Constants.FILE_TYPE_YOUTUBE))
        {
            if (item.fileName != null && item.fileName.size() > 0)
            {
                if (isDownloaded)
                {
                    ArrayList<File> files =new ArrayList<>();

                    for (int i = 0;i<item.fileName.size();i++)
                    {
                        AppLog.e(TAG, "URL DECODE"+Constants.decodeUrlToBase64(item.fileName.get(i)));


                        if(item.fileType.equals(Constants.FILE_TYPE_IMAGE))
                        {
                            files.add(AmazoneImageDownload.getDownloadPath(item.fileName.get(i)));
                        }
                        else if (item.fileType.equals(Constants.FILE_TYPE_PDF))
                        {
                            files.add(AmazoneDownload.getDownloadPath(item.fileName.get(i)));
                        }
                        else if (item.fileType.equals(Constants.FILE_TYPE_VIDEO))
                        {
                            files.add(AmazoneVideoDownload.getDownloadPath(item.fileName.get(i)));
                        }

                    }

                    ArrayList<Uri> uris = new ArrayList<>();

                    for(File file: files){

                        AppLog.e(TAG, "URL "+file.getAbsolutePath());

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            uris.add(FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file));
                        } else {
                            uris.add(Uri.fromFile(file));
                        }

                    }


                    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    intent.setType("");
                    intent.putExtra(Intent.EXTRA_TEXT,item.fileType);
                    intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    startActivity(Intent.createChooser(intent, "Share File"));
                }
                else
                {
                    Toast.makeText(getContext(),getResources().getString(R.string.smb_no_file_download),Toast.LENGTH_SHORT).show();
                }
            }

        }*/

    }

    @Override
    public void onShareClick(TeamPostGetData item) {
        GroupDashboardActivityNew.is_share_edit = true;
        GroupDashboardActivityNew.share_type = "team";
        GroupDashboardActivityNew.share_title = item.title;
        GroupDashboardActivityNew.share_desc = item.text;

        GroupDashboardActivityNew.share_image = "";
        GroupDashboardActivityNew.imageHeight = item.imageHeight;
        GroupDashboardActivityNew.imageWidth = item.imageWidth;


        if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
            GroupDashboardActivityNew.share_image_type = 1;
        } else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
            GroupDashboardActivityNew.share_image_type = 2;
        } else if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
            GroupDashboardActivityNew.share_image_type = 3;
        } else {
            GroupDashboardActivityNew.share_image_type = 4;
        }

        GroupDashboardActivityNew.team_id = team_id;
        Intent intent = new Intent(getActivity(), SelectShareTypeActivity.class);
        intent.putExtra("id", mGroupId);
        intent.putExtra("post_id", item.id);
        AppLog.e("SHARE", "Post id2 is " + item.id);
        startActivity(intent);
        AppLog.e("onShareClick", "in method");
//        showShareDialog(R.array.share, item);
    }

    @Override
    public void onPushClick(TeamPostGetData item) {



        GroupDashboardActivityNew.is_share_edit = true;
        GroupDashboardActivityNew.share_type = "team";
        GroupDashboardActivityNew.share_title = item.title;
        GroupDashboardActivityNew.share_desc = item.text;

        GroupDashboardActivityNew.share_image = "";
        GroupDashboardActivityNew.imageHeight = item.imageHeight;
        GroupDashboardActivityNew.imageWidth = item.imageWidth;

        if (TextUtils.isEmpty(item.fileType)) {
            GroupDashboardActivityNew.share_image_type = 4;
        } else if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
            GroupDashboardActivityNew.share_image_type = 1;
        } else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
            GroupDashboardActivityNew.share_image_type = 2;
        } else if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
            GroupDashboardActivityNew.share_image_type = 3;
        } else {
            GroupDashboardActivityNew.share_image_type = 4;
        }

        GroupDashboardActivityNew.team_id = team_id;
        Intent intent = new Intent(getActivity(), PushActivity.class);
        intent.putExtra("id", mGroupId);
        intent.putExtra("post_id", item.id);
        startActivity(intent);

    }

    @Override
    public void onNameClick(TeamPostGetData item) {
        if (item.createdById.equals(LeafPreference.getInstance(getActivity()).getString(LeafPreference.LOGIN_ID))) {
            AppLog.e("onNameClick", "else if called");
            Intent intent = new Intent(getActivity(), ProfileActivity2.class);
            startActivity(intent);
        } else {
            AppLog.e("onNameClick", "else called");
        }
    }

    @Override
    public void onLikeListClick(TeamPostGetData item) {

        if (isConnectionAvailable()) {
            Intent intent = new Intent(getActivity(), LikesListActivity.class);
            intent.putExtra("id", mGroupId);
            intent.putExtra("post_id", item.id);
            intent.putExtra("type", "team");
            intent.putExtra("team_id", team_id);
            startActivity(intent);
        } else {
            showNoNetworkMsg();
        }
    }

    @Override
    public void callBirthdayPostCreation(TeamPostGetData item, int position) {
        Log.e(TAG,"callBirthdayPostCreation"+position);
        if(birthdayPostCreationQueue == null)
        {
            birthdayPostCreationQueue = new ArrayList<>();
        }

        if(birthdayPostCreationQueue.contains(position))
        {
            Log.e(TAG,"callBirthdayPostCreation contains"+position);
            return;
        }

        birthdayPostCreationQueue.add(position);
        createBirthPostAndSave(item ,position);
    }

    Bitmap BirthdayTempleteBitmap = null;
    Bitmap UserBitmap = null;
    Bitmap MlaBitMap = null;
    File fileSaveLocation = null;
    private void createBirthPostAndSave(TeamPostGetData item , int position)
    {

        ArrayList<String> imageList = new ArrayList<>();
        imageList.add(item.fileName.get(0));
        imageList.add(item.bdayUserImage);
        imageList.add(item.createdByImage);

        if(getActivity() == null)
        {
            return;
        }



        AmazoneImageDownload.download(getActivity(), item.bdayUserImage , new AmazoneImageDownload.AmazoneDownloadSingleListener() {
            @Override
            public void onDownload(File file) {

                if(getActivity() == null)
                {
                    return;
                }
                if(file == null)
                {
                    return;
                }
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                UserBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),bmOptions);

                Log.e(TAG,"UserBitmap H "+ UserBitmap.getHeight());
                Log.e(TAG,"UserBitmap W "+ UserBitmap.getWidth());

                checkAllDownload(item.bdayUserName,item.createdBy,position,item.fileName.get(0));
            }

            @Override
            public void error(String msg) {

            }

            @Override
            public void progressUpdate(int progress, int max) {

            }
        });

        AmazoneImageDownload.download(getActivity(), item.createdByImage , new AmazoneImageDownload.AmazoneDownloadSingleListener() {
            @Override
            public void onDownload(File file) {

                if(getActivity() == null)
                {
                    return;
                }
                if(file == null)
                {
                    return;
                }
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                MlaBitMap = BitmapFactory.decodeFile(file.getAbsolutePath(),bmOptions);

                Log.e(TAG,"UserBitmap H "+ MlaBitMap.getHeight());
                Log.e(TAG,"UserBitmap W "+ MlaBitMap.getWidth());

                checkAllDownload(item.bdayUserName,item.createdBy,position,item.fileName.get(0));
            }

            @Override
            public void error(String msg) {

            }

            @Override
            public void progressUpdate(int progress, int max) {

            }
        });



        /* AmazoneMultiImageDownload.download(getActivity(), imageList, new AmazoneMultiImageDownload.AmazoneDownloadMultiListener() {
            @Override
            public void onDownload(ArrayList<File> file) {

                Log.e(TAG,"onDownload "+position);

                if(getActivity() == null)
                {
                    return;
                }


                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap BirthdayTempleteBitmap = BitmapFactory.decodeFile(file.get(0).getAbsolutePath(),bmOptions);

                Log.e(TAG,"BirthdayTempleteBitmap H "+BirthdayTempleteBitmap.getHeight());
                Log.e(TAG,"BirthdayTempleteBitmap W "+BirthdayTempleteBitmap.getWidth());


                Bitmap UserBitmap,MlaBitMap;

                if(file.size()>1 && file.get(1)!=null )
                {
                    UserBitmap = BitmapFactory.decodeFile(file.get(1).getAbsolutePath(),bmOptions);
                    Log.e(TAG,"UserBitmap if");
                }
                else
                {
                    UserBitmap = drawableToBitmap(getActivity().getResources().getDrawable(R.drawable.user));
                    Log.e(TAG,"UserBitmap else");
                }

                if(file.size()>2 && file.get(2)!=null )
                {
                    MlaBitMap = BitmapFactory.decodeFile(file.get(2).getAbsolutePath(),bmOptions);
                    Log.e(TAG,"MlaBitMap if");
                }
                else
                {
                    MlaBitMap = drawableToBitmap(getActivity().getResources().getDrawable(R.drawable.mla));
                    Log.e(TAG,"MlaBitMap else");
                }

                Log.e(TAG,"UserBitmap H "+UserBitmap.getHeight());
                Log.e(TAG,"UserBitmap W "+UserBitmap.getWidth());

                if(item.bdayUserName ==null || item.bdayUserName.equalsIgnoreCase(""))
                {
                    item.bdayUserName = "Username";
                }

                createBitmap(BirthdayTempleteBitmap,MlaBitMap,UserBitmap, item.bdayUserName,file.get(0),item.createdBy);
                birthdayPostCreationQueue.remove(Integer.valueOf(position));
                Log.e(TAG,"mAdapter notifyItemChanged"+position);
                mAdapter.notifyItemChanged(position);

                Log.e(TAG , "created Bitmap saved at : "+file.get(0));
            }
            @Override
            public void error(String msg) {

            }

            @Override
            public void progressUpdate(int progress, int max) {

            }
        });*/


    }


    public void checkAllDownload(String UserName,String mlaName,int position,String file)
    {
        if (UserBitmap != null && MlaBitMap != null)
        {

            AmazoneImageDownload.download(getActivity(), file, new AmazoneImageDownload.AmazoneDownloadSingleListener() {
                @Override
                public void onDownload(File file) {

                    if(getActivity() == null)
                    {
                        return;

                    }
                    if(file == null)
                    {
                        return;
                    }

                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    BirthdayTempleteBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),bmOptions);

                    createBitmap(BirthdayTempleteBitmap,MlaBitMap,UserBitmap, UserName,file,mlaName);
                    birthdayPostCreationQueue.remove(Integer.valueOf(position));
                    Log.e(TAG,"mAdapter notifyItemChanged"+position);
                    mAdapter2.notifyItemChanged(position);
                    Log.e(TAG , "created Bitmap saved at : "+file);

                }

                @Override
                public void error(String msg) {

                }

                @Override
                public void progressUpdate(int progress, int max) {

                }
            });

        }
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    private File createBitmap(Bitmap birthdayTempleteBitmap, Bitmap mlaBitmap, Bitmap userBitmap , String userName, File file,String mlaName) {

        Bitmap result = Bitmap.createBitmap(birthdayTempleteBitmap.getWidth(), birthdayTempleteBitmap.getHeight(), birthdayTempleteBitmap.getConfig());

        userBitmap = getResizedBitmap(userBitmap, (int) (195*(birthdayTempleteBitmap.getWidth()/540.0f)),(int) (195*(birthdayTempleteBitmap.getWidth()/540.0f)));
        mlaBitmap = getResizedBitmap(mlaBitmap,(int) (152*(birthdayTempleteBitmap.getWidth()/540.0f)),(int) (152*(birthdayTempleteBitmap.getWidth()/540.0f)));

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(birthdayTempleteBitmap,0,0,null);
        Paint paint = new Paint();
        paint.setColor(getActivity().getResources().getColor(R.color.birthDayTextColor));
        paint.setTextSize(result.getHeight()*0.07f);

        paint.setTextAlign(Paint.Align.CENTER);
        Rect rect = new Rect();

        Log.e(TAG,"userBitmap w : "+userBitmap.getWidth()+" h : "+userBitmap.getHeight());
        Log.e(TAG,"mlaBitmap w : "+mlaBitmap.getWidth()+" h : "+mlaBitmap.getHeight());

        Log.e(TAG,"result w : "+result.getWidth()+" h : "+result.getHeight());

        paint.getTextBounds(userName,0,userName.length(),rect);
        canvas.drawText(userName,result.getWidth()*0.107f,result.getHeight()*0.52f,paint);


        Paint paint2 = new Paint();
        paint2.setColor(getActivity().getResources().getColor(R.color.mlaTextColor));

        paint2.setTextAlign(Paint.Align.RIGHT);

        Rect rect2 = new Rect();


        paint2.setTextSize(result.getHeight()*0.05f);
        paint2.getTextBounds(mlaName,0,mlaName.length(),rect2);
        canvas.drawText(mlaName,result.getWidth()*0.94f,result.getHeight()*0.95f,paint2);

        canvas.drawBitmap(getRoundedCornerBitmap(mlaBitmap,mlaBitmap.getWidth()/2), (float) (birthdayTempleteBitmap.getWidth()-mlaBitmap.getWidth()*1.195), (float) (birthdayTempleteBitmap.getHeight()-mlaBitmap.getHeight()*1.380), null);
        canvas.drawBitmap(getRoundedCornerBitmap(userBitmap,userBitmap.getWidth()/2),userBitmap.getWidth()*0.107f , userBitmap.getWidth()*0.135f, null);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.JPEG, 60, bytes);

        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(bytes.toByteArray());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"IOException"+e.getMessage());
        }

        return file;

    }

    //OLD BIRTHDAY POST
    /*private File createBitmap(Bitmap birthdayTempleteBitmap, Bitmap mlaBitmap, Bitmap userBitmap , String userName, File file,String mlaName) {

        Bitmap result = Bitmap.createBitmap(birthdayTempleteBitmap.getWidth(), birthdayTempleteBitmap.getHeight(), birthdayTempleteBitmap.getConfig());

        userBitmap = getResizedBitmap(userBitmap, (int) (195*(birthdayTempleteBitmap.getWidth()/540.0f)),(int) (195*(birthdayTempleteBitmap.getWidth()/540.0f)));
        mlaBitmap = getResizedBitmap(mlaBitmap,(int) (152*(birthdayTempleteBitmap.getWidth()/540.0f)),(int) (152*(birthdayTempleteBitmap.getWidth()/540.0f)));

        String[] splitUserName = userName.split("\\s+");

        String[] splitmlaName = mlaName.split("\\s+");

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(birthdayTempleteBitmap,0,0,null);
        Paint paint = new Paint();
        paint.setColor(getActivity().getResources().getColor(R.color.birthDayTextColor));
        paint.setTextSize(result.getHeight()*0.07f);

        paint.setTextAlign(Paint.Align.CENTER);
        Rect rect = new Rect();
        Rect rect1 = new Rect();

        if (splitUserName.length > 1)
        {
            paint.getTextBounds(splitUserName[0],0,splitUserName[0].length(),rect);
            paint.getTextBounds(splitUserName[1],0,splitUserName[1].length(),rect1);

            canvas.drawText(splitUserName[0],(result.getWidth()*0.74f),result.getHeight()*0.44f,paint);
            canvas.drawText(splitUserName[1],(result.getWidth()*0.74f),result.getHeight()*0.46f+rect1.height(),paint);
        }
        else
        {
            paint.getTextBounds(userName,0,userName.length(),rect);
            canvas.drawText(userName,result.getWidth()*0.8f,result.getHeight()*0.44f,paint);
        }


        Paint paint2 = new Paint();
        paint2.setColor(getActivity().getResources().getColor(R.color.mlaTextColor));

        paint2.setTextAlign(Paint.Align.CENTER);

        Rect rect2 = new Rect();
        Rect rect3 = new Rect();

        if (splitmlaName.length > 1)
        {
            paint2.setTextSize(result.getHeight()*0.03f);
            paint2.getTextBounds(splitmlaName[0],0,splitmlaName[0].length(),rect2);
            paint2.getTextBounds(splitmlaName[1],0,splitmlaName[1].length(),rect3);

            canvas.drawText(splitmlaName[0],result.getWidth()*0.81f,result.getHeight()*0.93f,paint2);
            canvas.drawText(splitmlaName[1],result.getWidth()*0.81f,result.getHeight()*0.95f,paint2);
        }
        else
        {
            paint2.setTextSize(result.getHeight()*0.05f);
            paint2.getTextBounds(mlaName,0,mlaName.length(),rect2);
            canvas.drawText(mlaName,result.getWidth()*0.81f,result.getHeight()*0.95f,paint2);
        }

        canvas.drawBitmap(getRoundedCornerBitmap(mlaBitmap,mlaBitmap.getWidth()/2), (float) (birthdayTempleteBitmap.getWidth()-mlaBitmap.getWidth()*1.195), (float) (birthdayTempleteBitmap.getHeight()-mlaBitmap.getHeight()*1.380), null);
        canvas.drawBitmap(getRoundedCornerBitmap(userBitmap,userBitmap.getWidth()/2),userBitmap.getWidth()*0.199f , userBitmap.getWidth()*0.342f, null);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.JPEG, 60, bytes);

        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(bytes.toByteArray());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"IOException"+e.getMessage());
        }

        return file;

    }*/

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;

    }

    public Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    @Override
    public void onDeleteVideoClick(TeamPostGetData item, int position) {
        AppLog.e(TAG, "onDeleteVideoClick : " + item.fileName.get(0));
        if (item.fileName != null && item.fileName.size() > 0) {
            AmazoneDownload.removeVideo(getActivity(), item.fileName.get(0));
            mAdapter2.notifyItemChanged(position);
        }
    }

    private static String[] fromString(String string) {

        if (string != null && !string.equals("null"))
            return string.replace("[", "").replace("]", "").split(", ");
        else
            return new String[0];
    }

    public void showReportDialog() {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_layout_report_list);

        dialogRecyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);

        dialogProgressBar = (ProgressBar) dialog.findViewById(R.id.progressBar);

        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        TextView tv_report = (TextView) dialog.findViewById(R.id.tv_report);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tv_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReport(list.get(ReportAdapter.selected_position).type);
                dialog.dismiss();
            }
        });

        final LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        dialogRecyclerView.setLayoutManager(manager);
        getReportData();

        dialog.show();
    }

    private void getReportData() {
        LeafManager mManager = new LeafManager();
        showLoadingBar(dialogProgressBar);
        mManager.getReportList(this);
    }

    private void sendReport(int report_id) {
        LeafManager mManager = new LeafManager();
        showLoadingBar(mBinding.progressBar2);
        mManager.reportPost(this, mGroupId + "", currentItem.id, report_id);
    }

    @Override
    public void onClick(View v) {

    }
/*
    @Override
    public void onMeetingStatusChanged(MeetingStatus meetingStatus, int i, int i1) {

    }*/


    class LikeAsync extends AsyncTask<Void, Void, Void> {
        BaseResponse res;
        private Boolean isChanged = false;

        public LikeAsync(BaseResponse data) {
            this.res = data;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (res.status.equalsIgnoreCase("liked")) {
                teamPostList.get(position).isLiked = true;
                teamPostList.get(position).likes++;
            } else {
                teamPostList.get(position).isLiked = false;
                teamPostList.get(position).likes--;
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mAdapter2.notifyItemChanged(position);
            liked = false;

            TeamPostGetData select2 = teamPostList.get(position);
            PostTeamDataItem.updateLike(select2.id, select2.isLiked ? 1 : 0, select2.likes);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_UPDATE_TEAM)
        {
            if (resultCode == Activity.RESULT_OK)
            {

                if (type.equalsIgnoreCase("team"))
                {
                    Intent login = new Intent(getActivity(), GroupDashboardActivityNew.class);
                    login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(login);
                }
                else if (type.equalsIgnoreCase("booth"))
                {
                    BoothsTBL.deleteBooth(GroupDashboardActivityNew.groupId);
                    Intent login = new Intent(getActivity(), GroupDashboardActivityNew.class);
                    login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(login);
                }


            }
        }
    }


    class EventAsync extends AsyncTask<Void, Void, Void> {
        TeamPostEventModelRes res1;
        private boolean needRefresh = false;

        public EventAsync(TeamPostEventModelRes res1) {
            this.res1 =res1;
        }

        @Override
        protected Void doInBackground(Void... voids) {


            if (res1.getData().size() > 0)
            {

//                ((GroupDashboardActivityNew) getActivity()).tv_Desc.setText(getResources().getString(R.string.lbl_members)+" : "+String.valueOf(res1.getData().get(0).getMembers()));

                List<HomeTeamDataTBL> homeTeamDataTBLList= HomeTeamDataTBL.getTeamPost(team_id);

                if (homeTeamDataTBLList.size() > 0)
                {
                    if (homeTeamDataTBLList.get(0).canPost != res1.getData().get(0).isCanPost())
                    {
                        if (res1.getData().get(0).isCanPost())
                        {
                            menu.findItem(R.id.menu_add_post).setVisible(true);
                        }
                        else
                        {
                            menu.findItem(R.id.menu_add_post).setVisible(false);
                        }
                    }
                }

                HomeTeamDataTBL.deleteTeamPost(team_id);

                for (int i=0;i<res1.getData().size();i++)
                {

                    HomeTeamDataTBL homeTeamDataTBL = new HomeTeamDataTBL();
                    homeTeamDataTBL.teamId = res1.getData().get(i).getTeamId();
                    homeTeamDataTBL.members =  res1.getData().get(i).getMembers();
                    homeTeamDataTBL.lastTeamPostAt =  res1.getData().get(i).getLastTeamPostAt();
                    homeTeamDataTBL.canPost =  res1.getData().get(i).isCanPost();
                    homeTeamDataTBL.canComment = res1.getData().get(i).isCanComment();

                    homeTeamDataTBL.save();

                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (HomeTeamDataTBL.getTeamPost(team_id).size()>0)
            {
                List<HomeTeamDataTBL> homeTeamDataTBLList= HomeTeamDataTBL.getTeamPost(team_id);

                List<PostTeamDataItem> dataItemList = PostTeamDataItem.getTeamPosts(mGroupId + "", team_id + "",type,currentPage2);

                if (dataItemList.size() > 0)
                {
                    for (int i = 0;i<homeTeamDataTBLList.size();i++)
                    {
                        if (team_id.equalsIgnoreCase(homeTeamDataTBLList.get(i).teamId))
                        {
                            if (MixOperations.isNewEventUpdate(homeTeamDataTBLList.get(i).lastTeamPostAt,"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", dataItemList.get(dataItemList.size()-1)._now))
                            {
                                PostTeamDataItem.deleteTeamPosts(team_id,type);
                                AppLog.e(TAG," onPostExecute isNewEvent");
                                callApi(true);
                            }

                        }
                    }
                }

            }
        }
    }
}
