package school.campusconnect.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import school.campusconnect.BuildConfig;
import school.campusconnect.activities.AddBoothStudentActivity;
import school.campusconnect.activities.AddTeamStaffActivity;
import school.campusconnect.activities.AddTeamStudentActivity;
import school.campusconnect.activities.AddTimeTablePostActivity;
import school.campusconnect.activities.AttendanceActivity;
import school.campusconnect.activities.AttendancePareSchool;
import school.campusconnect.activities.AttendanceReportActivity;
import school.campusconnect.activities.CreateTeamActivity;
import school.campusconnect.activities.GpsActivity;
import school.campusconnect.activities.LeaveActivity;
import school.campusconnect.activities.LikesListActivity;
import school.campusconnect.activities.TeamSettingsActivity;
import school.campusconnect.activities.TeamUsersActivity;
import school.campusconnect.adapters.ReportAdapter;
import school.campusconnect.datamodel.EventTBL;
import school.campusconnect.datamodel.PostDataItem;
import school.campusconnect.datamodel.PostItem;
import school.campusconnect.datamodel.booths.BoothResponse;
import school.campusconnect.datamodel.reportlist.ReportResponse;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.datamodel.teamdiscussion.MyTeamsResponse;
import school.campusconnect.firebase.SendNotificationGlobal;
import school.campusconnect.firebase.SendNotificationModel;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AmazoneRemove;
import school.campusconnect.utils.AppLog;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
import school.campusconnect.utils.MixOperations;
import school.campusconnect.views.SMBDialogUtils;

/**
 * Created by frenzin04 on 3/29/2017.
 */

public class TeamPostsFragmentNew extends BaseFragment implements LeafManager.OnCommunicationListener,
        DialogInterface.OnClickListener, LeafManager.OnAddUpdateListener<AddPostValidationError>, TeamListAdapter.OnItemClickListener, View.OnClickListener {

    private static final String TAG = "TeamPostsFragmentNew";
    private LayoutListTeamsBinding mBinding;
    private TeamListAdapter mAdapter2;
    LeafManager manager = new LeafManager();
    String mGroupId = "";
    int position = -1;
    public boolean mIsLoading = false;
    public int totalPages2 = 1;
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

    LeafPreference leafPreference;

    //private Query query;

    public TeamPostsFragmentNew() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

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

        menu.findItem(R.id.action_notification_list).setVisible(false);

        if (isFromMain) {
            menu.findItem(R.id.menu_more).setVisible(true);


            if (teamData.isTeamAdmin || teamData.allowTeamPostAll) {
                menu.findItem(R.id.menu_add_post).setVisible(true);
            }
            if (teamData.isTeamAdmin) {
                //menu.findItem(R.id.action_settings).setVisible(true);
                menu.findItem(R.id.menu_edit_team).setVisible(true);
                menu.findItem(R.id.menu_change_admin).setVisible(true);
                menu.findItem(R.id.menu_archive_team).setVisible(true);
                if (GroupDashboardActivityNew.groupCategory.equals(Constants.CATEGORY_SCHOOL) || GroupDashboardActivityNew.groupCategory.equals(Constants.CATEGORY_CONSTITUENCY))
                    menu.findItem(R.id.menu_add_time_table).setVisible(true);
            } else {
               /* if (teamData.isClass && !teamData.allowTeamPostAll) {
                    menu.findItem(R.id.menu_leave_team).setVisible(false);
                } else {
                    menu.findItem(R.id.menu_leave_team).setVisible(true);
                }*/
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


        } else {
            if (teamData.allowedToAddTeamPost) {
                menu.findItem(R.id.menu_add_post).setVisible(true);
            }
        }

        menu.findItem(R.id.menu_add_time_table).setVisible(false);


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
            case R.id.menu_add_friend:
                if ("subBooth".equalsIgnoreCase(teamData.category) || "booth".equalsIgnoreCase(teamData.category) || "constituency".equalsIgnoreCase(teamData.category)) {
                    Intent intent = new Intent(getContext(), AddBoothStudentActivity.class);
                    intent.putExtra("group_id", mGroupId);
                    intent.putExtra("team_id", team_id);
                    intent.putExtra("category", teamData.category);
                    startActivity(intent);
                } else {
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
                startActivity(addTeamIntent);
                break;
            case R.id.menu_leave_team:
                SMBDialogUtils.showSMBDialogOKCancel(getActivity(), "Are you sure you want to leave?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showLoadingBar(mBinding.progressBar2);
                        LeafManager manager = new LeafManager();
                        manager.leaveTeam(TeamPostsFragmentNew.this, mGroupId, team_id);
                    }
                });
                break;
            case R.id.menu_archive_team:
                SMBDialogUtils.showSMBDialogOKCancel(getActivity(), "Are you sure you want to archive " + teamName + "?", new DialogInterface.OnClickListener() {
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

    public static TeamPostsFragmentNew newInstance(MyTeamData myTeamData, boolean isFromMain) {
        TeamPostsFragmentNew fragment = new TeamPostsFragmentNew();
        Bundle b = new Bundle();
        b.putString("team_data", new Gson().toJson(myTeamData));
        b.putBoolean("isFromMain", isFromMain);
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

        mBinding.setSize(1);
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
                } else {
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
            mBinding.progressBar2.setVisibility(View.GONE);
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

        if (teamData.teamType.equalsIgnoreCase(Constants.TEAM_TYPE_CREATED) && teamData.enableGps) {
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
        eventTBL = EventTBL.getTeamEvent(mGroupId, team_id);
        boolean apiEvent = false;
        if (eventTBL != null) {
            if (eventTBL._now == 0) {
                apiEvent = true;
            }
            if (MixOperations.isNewEvent(eventTBL.eventAt, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", eventTBL._now)) {
                apiEvent = true;
            }

        }

        final List<PostTeamDataItem> dataItemList = PostTeamDataItem.getTeamPosts(mGroupId + "", team_id + "");
        AppLog.e(TAG, "local list size is " + dataItemList.size());
        showLoadingBar(mBinding.progressBar2);
        String lastId = null;
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
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                    ) {
                        currentPage2 = currentPage2 + 1;
                        getData2(team_id, false);
                    }
                }
            }
        });

        mBinding.swipeRefreshLayout2.setEnabled(false);
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //NOFIREBASEDATABASE
     /*   if(query !=null)
            query.removeEventListener(firebaseNewPostListener);*/

    }

    private void init() {
        DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
        count = databaseHandler.getCount();

        currentPage2 = 1;
        fragment_new = this;

        teamData = new Gson().fromJson(getArguments().getString("team_data"), MyTeamData.class);
        isFromMain = getArguments().getBoolean("isFromMain", false);
        AppLog.e(TAG, "isFromMain : " + isFromMain);

        mGroupId = !TextUtils.isEmpty(teamData.groupId) ? teamData.groupId : GroupDashboardActivityNew.groupId;
        team_id = teamData.teamId;
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
                    if (!teamData.isTeamAdmin && !teamData.allowTeamPostAll)
                        return;

                    try {
                        Intent intent = new Intent(getActivity(), LeadsListActivity.class);
                        intent.putExtra("id", mGroupId);
                        intent.putExtra("team_id", team_id);
                        intent.putExtra("team_name", teamName);
                        intent.putExtra("isAdmin", teamData.isTeamAdmin);
                        startActivity(intent);
                        AppLog.e("Team id : ", team_id + "");
                    } catch (Exception e) {
                        AppLog.e("floating", "error is " + e.toString());
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
            if ("subBooth".equalsIgnoreCase(teamData.category) || "booth".equalsIgnoreCase(teamData.category) || "constituency".equalsIgnoreCase(teamData.category)) {
                manager.getBooths(this, GroupDashboardActivityNew.groupId);
            } else {
                manager.myTeamList(this, GroupDashboardActivityNew.groupId);
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
                mIsLoading = false;

                if (currentPage2 == 1) {
                    PostTeamDataItem.deleteTeamPosts(team_id);
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
                if (!isFind)
                    getActivity().onBackPressed();
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
                if (!isFind)
                    getActivity().onBackPressed();
                break;


        }

    }

    private void refreshData() {
        setFloatingButton();
        if (getActivity() != null) {
            ((GroupDashboardActivityNew) getActivity()).tvToolbar.setText(teamData.name);
            //  ((GroupDashboardActivityNew) getActivity()).tv_Desc.setText(teamData.members + " users");
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
                Toast.makeText(getActivity(), "No posts available.", Toast.LENGTH_SHORT).show();
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
            showLoadingBar(mBinding.progressBar2);
            manager.setTeamLike(this, mGroupId + "", team_id, item.id);
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
        SMBDialogUtils.showSMBDialogOKCancel(getActivity(), "Are You Sure Want To Delete ?", this);
    }

    @Override
    public void onReportClick(TeamPostGetData item) {
        currentItem = item;
        showReportDialog();
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
}
