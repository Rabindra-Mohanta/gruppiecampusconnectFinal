package school.campusconnect.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.baoyz.widget.PullRefreshLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.zipow.videobox.confapp.SuspendFeature;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.BuildConfig;
import school.campusconnect.LeafApplication;
import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.VideoClassActivity;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.DialogMeetingOnOffBinding;
import school.campusconnect.databinding.DialogVideoAttendanceShareBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ClassListTBL;
import school.campusconnect.datamodel.LiveClassListTBL;
import school.campusconnect.datamodel.TeamCountTBL;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.subjects.SubjectStaffResponse;
import school.campusconnect.datamodel.videocall.JoinLiveClassReq;
import school.campusconnect.datamodel.videocall.LiveClassEventRes;
import school.campusconnect.datamodel.videocall.MeetingStatusModel;
import school.campusconnect.datamodel.videocall.MeetingStatusModelApi;
import school.campusconnect.datamodel.videocall.StartMeetingRes;
import school.campusconnect.datamodel.videocall.StopMeetingReq;
import school.campusconnect.datamodel.videocall.VideoClassResponse;
import school.campusconnect.firebase.SendNotificationModel;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.MixOperations;
import us.zoom.sdk.FreeMeetingNeedUpgradeType;
import us.zoom.sdk.InMeetingAudioController;
import us.zoom.sdk.InMeetingChatMessage;
import us.zoom.sdk.InMeetingEventHandler;
import us.zoom.sdk.InMeetingServiceListener;
import us.zoom.sdk.InstantMeetingOptions;
import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.MeetingViewsOptions;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class VideoClassListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "VideoClassListFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    @Bind(R.id.progressBarZoom)
    public ProgressBar progressBarZoom;
/*
    @Bind(R.id.swipeRefreshLayout)
    public PullRefreshLayout swipeRefreshLayout;*/

    VideoClassResponse.ClassData item;

    boolean isSentNotification = false;

    boolean videoClassClicked = false;

    boolean isFirstTime = true;
    ClassesAdapter classesAdapter = new ClassesAdapter();
    private ArrayList<SubjectStaffResponse.SubjectData> subjectList;
    private ArrayList<VideoClassResponse.ClassData> result;
    private VideoClassResponse.ClassData listItemData;

    boolean isZoomStarted;

    private long ReconnectTime = 0;


    LeafPreference leafPreference;

    LeafManager leafManager;
    private long mLastClickTime = 0;
    String category="";


    CountDownTimer countDownTimer = new CountDownTimer(3*60000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            Log.e(TAG,"onTick"+millisUntilFinished);
        }

        @Override
        public void onFinish() {

            isAutomatically = true;

            if (isAutomatically)
            {
                Log.e(TAG,"countDownTimer onFinish isAutomatically");
                ZoomSDK.getInstance().getMeetingService().leaveCurrentMeeting(true);
            }
            else
            {
                Log.e(TAG,"countDownTimer onFinish");
            }

        }
    };
    private boolean isAutomatically = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppLog.e(TAG, "onCreate called");

        isSentNotification = false;
        isZoomStarted = false;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AppLog.e(TAG, "onCreateView called");

        View view = inflater.inflate(R.layout.fragment_team_discuss, container, false);
        ButterKnife.bind(this, view);
        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        classesAdapter = new ClassesAdapter();
        rvClass.setAdapter(classesAdapter);

        init();

        getDataLocally();

        leafPreference = LeafPreference.getInstance(getActivity());

        return view;
    }


    private void init() {
        leafManager = new LeafManager();

        if (getArguments() != null) {
            category = getArguments().getString("category");
        }
       /* swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    swipeRefreshLayout.setRefreshing(false);
                    calApi();
                } else {
                    showNoNetworkMsg();
                }
            }
        });*/
    }

    private void getDataLocally() {
        videoClassClicked = false;
        List<LiveClassListTBL> list = LiveClassListTBL.getAll(GroupDashboardActivityNew.groupId);
        if (list.size() != 0) {
            result = new ArrayList<>();
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
            classesAdapter.setList(result);

            TeamCountTBL dashboardCount = TeamCountTBL.getByTypeAndGroup("LIVE", GroupDashboardActivityNew.groupId);
            if (dashboardCount != null) {
                boolean apiCall = false;
                if (dashboardCount.lastApiCalled != 0) {
                    if (MixOperations.isNewEvent(dashboardCount.lastInsertedTeamTime, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", dashboardCount.lastApiCalled)) {
                        apiCall = true;
                    }
                }

                if (dashboardCount.oldCount != dashboardCount.count) {
                    dashboardCount.oldCount = dashboardCount.count;
                    dashboardCount.save();
                    apiCall = true;
                }

                if (apiCall) {
                    calApi(false);
                } else {
                    getLiveClassEventApi();
                }
            } else {
                getLiveClassEventApi();
            }
        } else {
            calApi(true);
        }
    }

    private void getLiveClassEventApi() {
        if (isConnectionAvailable()) {
            showLoadingBar(progressBar);
        //    progressBar.setVisibility(View.VISIBLE);
            leafManager.getLiveClassEvents(this, GroupDashboardActivityNew.groupId);
        }
    }


    private void calApi(boolean isLoading) {
        if(isLoading)
            showLoadingBar(progressBar);
        //    progressBar.setVisibility(View.VISIBLE);
        leafManager.getVideoClasses(this, GroupDashboardActivityNew.groupId);
    }

    @Override
    public void onStart() {
        super.onStart();
        AppLog.e(TAG, "onStart called");
        videoClassClicked = false;
        LocalBroadcastManager.getInstance(VideoClassListFragment.this.getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("MEETING_START"));
        LocalBroadcastManager.getInstance(VideoClassListFragment.this.getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("MEETING_RESUME"));
        LocalBroadcastManager.getInstance(VideoClassListFragment.this.getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("MEETING_END"));
    }

    @Override
    public void onResume() {
        super.onResume();
        AppLog.e(TAG, "onResume called");


        // setListener();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        AppLog.e(TAG, "onDestroyView called");

    }

    @Override
    public void onStop() {
        super.onStop();

        AppLog.e(TAG, "onStop called");

       /* if (!isZoomStarted)
        {
            mHandler.removeCallbacks(myRunnable);
        }*/


        LocalBroadcastManager.getInstance(VideoClassListFragment.this.getActivity()).unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent

            String message = intent.getAction();

            AppLog.e(TAG, "onReceive called with action : " + message);
            if (message.equalsIgnoreCase("MEETING_START")) {

                if (intent.getExtras().containsKey("teamId")) {
                   /* int position = getPositionOf(intent.getExtras().getString("teamId"));
                    if (position >= 0) {
                        listItemData.isLive = true;
                        listItemData.createdByName = intent.getExtras().getString("createdByName");
                        classesAdapter.notifyDataSetChanged();
                    }*/
                    getLiveClassEventApi();
                }
            }
            if (message.equalsIgnoreCase("MEETING_END")) {

                if (intent.getExtras().containsKey("teamId")) {
                    int position = getPositionOf(intent.getExtras().getString("teamId"));
                    if (position >= 0) {
                        listItemData.isLive = false;
                        listItemData.createdByName = "";
                        classesAdapter.notifyDataSetChanged();
                    }
                }

            }

            if (message.equalsIgnoreCase("MEETING_RESUME")) {

                if (intent.getExtras().containsKey("teamId")) {
                    int position = getPositionOf(intent.getExtras().getString("teamId"));
                    if (position >= 0 && !listItemData.canPost) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (getActivity() == null) {
                                    return;
                                }

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        onTreeClick(listItemData);
                                        randomJoin(listItemData);
                                    }
                                });
                            }
                        }, 7000);

                    }

                }
            }

        }
    };

    private int getPositionOf(String teamId) {
        if(result !=null)
        {
             for (int i = 0; i < result.size(); i++) {
            if (result.get(i).getId().equalsIgnoreCase(teamId)) {
                listItemData = result.get(i);
                return i;
            }
            }
        }

        return -1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppLog.e(TAG, "onDestroy called");
        isZoomStarted = false;
        //  removeListener();
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
     //   progressBar.setVisibility(View.GONE);
        if (apiId == LeafManager.API_Video_Class) {
            VideoClassResponse res = (VideoClassResponse) response;
            result = res.getData();
            AppLog.e(TAG, "ClassResponse " + result);
            classesAdapter.setList(result);

            TeamCountTBL dashboardCount = TeamCountTBL.getByTypeAndGroup("LIVE", GroupDashboardActivityNew.groupId);
            if (dashboardCount != null) {
                dashboardCount.lastApiCalled = System.currentTimeMillis();
                dashboardCount.save();
            }

            saveToDB(result);

            getLiveClassEventApi();

            //  initFirebase();
        } else if (apiId == LeafManager.API_SUBJECT_STAFF) {
            SubjectStaffResponse subjectStaffResponse = (SubjectStaffResponse) response;
            subjectList = subjectStaffResponse.getData();
            try {
                showSubjectSelectDialog();
            } catch (Exception ex) {

            }
        } else if (apiId == LeafManager.API_ONLINE_ATTENDANCE_PUSH) {
        } else if (apiId == LeafManager.API_LIVE_CLASS_START) {
            if (item != null) {
                item.createdById = leafPreference.getUserId();
                item.createdByName = leafPreference.getUserName();
                if (classesAdapter != null) {
                    classesAdapter.notifyDataSetChanged();
                }
            }
        } else if (apiId == LeafManager.API_LIVE_CLASS_END) {
            if (getActivity() != null) {
                if (item != null) {
                    item.isLive = false;
                    item.meetingIdOnLive = "";
                    item.createdByName = "";
                    item.createdById = "";
                    if (classesAdapter != null) {
                        classesAdapter.notifyDataSetChanged();
                    }
                }
                ((VideoClassActivity) getActivity()).showSharePopup();
            }
        } else if (apiId == LeafManager.API_LIVE_CLASS_EVENTS) {
            LiveClassEventRes res = (LiveClassEventRes) response;
            refreshLiveClassBaseOnEventList(res.data);
        }
    }

    private void refreshLiveClassBaseOnEventList(ArrayList<LiveClassEventRes.LiveClassEventData> liveClassData) {
        if (result != null && result.size() > 0 && liveClassData != null && liveClassData.size() > 0) {
            for (int i = 0; i < result.size(); i++) {
                VideoClassResponse.ClassData iData = result.get(i);
                for (int j = 0; j < liveClassData.size(); j++) {
                    LiveClassEventRes.LiveClassEventData jData = liveClassData.get(j);
                    if (iData.id.equalsIgnoreCase(jData.teamId) && iData.groupId.equalsIgnoreCase(jData.groupId)) {
                        iData.meetingIdOnLive = jData.meetingOnLiveId;
                        iData.createdByName = jData.createdByName;
                        iData.createdById = jData.createdById;
                        iData.isLive = true;
                    } /*else {
                        iData.isLive = false;
                    }*/
                }
            }

            if (classesAdapter != null)
                classesAdapter.notifyDataSetChanged();
        }

    }

    private void saveToDB(ArrayList<VideoClassResponse.ClassData> result) {
        if (result == null)
            return;

        LiveClassListTBL.deleteAll(GroupDashboardActivityNew.groupId);
        for (int i = 0; i < result.size(); i++) {
            VideoClassResponse.ClassData currentItem = result.get(i);
            LiveClassListTBL item = new LiveClassListTBL();
            item.zoomPassword = currentItem.zoomPassword;
            item.zoomName = new Gson().toJson(currentItem.zoomName);
            item.zoomMail = currentItem.zoomMail;
            item.zoomSecret = currentItem.zoomSecret;
            item.zoomMeetingPassword = currentItem.zoomMeetingPassword;
            item.zoomKey = currentItem.zoomKey;
            item.teamId = currentItem.id;
            item.name = currentItem.className;
            item.jitsiToken = currentItem.jitsiToken;
            item.groupId = currentItem.groupId;
            item.canPost = currentItem.canPost;
            item.save();
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        //   progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        //   progressBar.setVisibility(View.GONE);
    }

    public void callEventApi() {
        isStartApiCalled = false;
        getLiveClassEventApi();
    }


    public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder> {
        List<VideoClassResponse.ClassData> list;
        private Context mContext;

        public ClassesAdapter(List<VideoClassResponse.ClassData> list) {
            this.list = list;
        }


        public ClassesAdapter() {
            list = new ArrayList<>();
        }

        public void setList(List<VideoClassResponse.ClassData> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public ClassesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_class, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ClassesAdapter.ViewHolder holder, final int position) {
            final VideoClassResponse.ClassData item = list.get(position);

            if (item.canPost && item.isLive) {
                //   holder.imgOnline.setVisibility(View.VISIBLE);
                if (item.isCreatedByMe()) {
                    holder.tv_stop.setVisibility(View.VISIBLE);
                } else {
                    holder.tv_stop.setVisibility(View.GONE);
                }
            } else {
                //    holder.imgOnline.setVisibility(View.GONE);
                holder.tv_stop.setVisibility(View.GONE);
            }


            if (!TextUtils.isEmpty(item.createdByName)) {
                holder.tvInfo.setVisibility(View.VISIBLE);
                holder.tvInfo.setText(item.createdByName);
            } else
                holder.tvInfo.setVisibility(View.GONE);

            AppLog.e(TAG, "class item  " + new Gson().toJson(item));


            if (!TextUtils.isEmpty(item.getImage())) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(50, 50).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgTeam,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.img_lead_default.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(50, 50).into(holder.imgTeam, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.img_lead_default.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.img_lead_default.setVisibility(View.VISIBLE);
                                        TextDrawable drawable = TextDrawable.builder()
                                                .buildRound(ImageUtil.getTextLetter(item.getName()), ImageUtil.getRandomColor(position));
                                        holder.img_lead_default.setImageDrawable(drawable);
                                        AppLog.e("Picasso", "Error : ");
                                    }
                                });
                            }
                        });
            } else {
                holder.img_lead_default.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(item.getName()), ImageUtil.getRandomColor(position));
                holder.img_lead_default.setImageDrawable(drawable);
            }


            holder.txt_name.setText(item.getName());


            holder.txt_count.setVisibility(View.GONE);

            if (item.canPost || item.isLive) {
                holder.img_tree.setVisibility(View.VISIBLE);
                holder.img_tree.setEnabled(true);
                holder.img_tree.setColorFilter(ContextCompat.getColor(mContext, R.color.color_green), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                holder.img_tree.setVisibility(View.VISIBLE);
                holder.img_tree.setEnabled(false);
                holder.img_tree.setColorFilter(ContextCompat.getColor(mContext, R.color.color_divider), android.graphics.PorterDuff.Mode.SRC_IN);
            }

          /*  if (item.isLive)
                holder.imgOnline.setVisibility(View.VISIBLE);
            else
                holder.imgOnline.setVisibility(View.GONE);*/


            holder.tv_stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoClassListFragment.this.item = list.get(position);
                    dialogMeetingConfirmation();
                }
            });

          /*  if (!leafPreference.getString(item.getId() + "_liveclass").equalsIgnoreCase("")) {
                SendNotificationModel.SendNotiData notiData = new Gson().fromJson(leafPreference.getString(item.getId() + "_liveclass"), SendNotificationModel.SendNotiData.class);

                AppLog.e(TAG, "class adapter notidata : " + notiData.createdByName);

                if (notiData.createdByName != null && !notiData.createdByName.equalsIgnoreCase("")) {
                    AppLog.e(TAG, "class adapter notidata  set ");
                    holder.tvInfo.setVisibility(View.VISIBLE);
                    holder.tvInfo.setText(notiData.createdByName);
                }
            }
*/


           /* holder.tvInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final SMBAlterDialog dialog =
                            new SMBAlterDialog(getActivity());
                    dialog.setTitle(R.string.app_name);
                    dialog.setMessage("Meeting Created By " + list.get(position).createdName);
                    dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();


                }
            });*/


        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    txtEmpty.setText("No Class found.");
                } else {
                    txtEmpty.setText("");
                }

                return list.size();
            } else {
                txtEmpty.setText("No Class found.");
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.img_lead)
            ImageView imgTeam;

            @Bind(R.id.img_lead_default)
            ImageView img_lead_default;

            @Bind(R.id.txt_name)
            TextView txt_name;

            @Bind(R.id.txt_count)
            TextView txt_count;
            @Bind(R.id.img_tree)
            ImageView img_tree;

            @Bind(R.id.imgOnline)
            ImageView imgOnline;

            @Bind(R.id.tv_stop)
            TextView tv_stop;

            @Bind(R.id.tv_info)
            TextView tvInfo;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                img_tree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            listItemData = list.get(getAdapterPosition());
                            onTreeClick(listItemData);

                            if (listItemData.canPost && !listItemData.isLive) {
                                if(!isStartApiCalled){
                                    isStartApiCalled = true;
                                    leafManager.startLiveClass(VideoClassListFragment.this, GroupDashboardActivityNew.groupId, listItemData.getId());
                                }
                            }
                            if (!listItemData.canPost) {
                                randomJoin(listItemData);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

               /* txt_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onNameClick(list.get(getAdapterPosition()));
                    }
                });*/


            }
        }
    }

    boolean isJoinApiCalled = false;
    boolean isStartApiCalled = false;

    private void randomJoin(VideoClassResponse.ClassData listItemData) {
        if (!isJoinApiCalled) {
            isJoinApiCalled = true;
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppLog.e(TAG, "---- run  Join called ----");
                    isJoinApiCalled = false;
                    if (!TextUtils.isEmpty(listItemData.meetingIdOnLive)) {
                        leafManager.joinLiveClass(VideoClassListFragment.this, GroupDashboardActivityNew.groupId, listItemData.getId(), new JoinLiveClassReq(listItemData.meetingIdOnLive));
                    }
                }
            }, new Random().nextInt(15000));
        }


    }

    private void joinRandomTimeApi() {

    }

    private void onTreeClick(VideoClassResponse.ClassData classData) {
        AppLog.e(TAG, "onTreeClick : " + classData.getId());


        if (SystemClock.elapsedRealtime() - mLastClickTime < 8000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        this.item = classData;

        // TODO : DISABLED VIDEO SCREEN RECORD
       /* if (classData.canPost && !listItemData.isLive && Build.VERSION.SDK_INT != Build.VERSION_CODES.Q) {
            ((VideoClassActivity) getActivity()).startRecordingScreen(this.item);
        } else {*/
            videoClassClicked = true;
            startMeeting();
            progressBarZoom.setVisibility(View.VISIBLE);
//        }

    }

    public void startMeetingFromActivity() {
        if (!videoClassClicked) {
            showLoadingBar(progressBar);
           // progressBar.setVisibility(View.VISIBLE);
            Log.e(TAG, "onTreeClick  : " + videoClassClicked);
            videoClassClicked = true;
            startMeeting();
            progressBarZoom.setVisibility(View.VISIBLE);

        }
    }

    private void onNameClick(VideoClassResponse.ClassData classData) {
        AppLog.e(TAG, "onNameClick called ");

        //  ((VideoClassActivity) getActivity()).startRecordingScreen();
        return;

    }

    private void stopMeeting(VideoClassResponse.ClassData classData) {
        this.item = classData;

        //NOFIREBASEDATABASE
        // myRef.child("live_class").child(item.getId()).removeValue();
        new SendNotification(false, item.jitsiToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        //   EnterSubjectDialog();

        if ("booth".equalsIgnoreCase(category) ||"boothVideo".equalsIgnoreCase(category) || "constituency".equalsIgnoreCase(category)) {
           // progressBar.setVisibility(View.VISIBLE);
            showLoadingBar(progressBar);
            leafManager.endLiveClass(VideoClassListFragment.this, GroupDashboardActivityNew.groupId, item.getId(), new StopMeetingReq(item.meetingIdOnLive));
        }else {
            getSubjectList(classData.getId());
        }


        if (getActivity() != null && item.canPost) {
            ((VideoClassActivity) getActivity()).stopRecording();
        }


    }

    private void initializeZoom(String zoomKey, String zoomSecret, String zoomMail, String zoomPassword, String meetingId, String zoomName, String className, boolean startOrJoin) {

        // progressBar.setVisibility(View.VISIBLE);
        showLoadingBar(progressBar);
        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        zoomSDK.initialize(getActivity(), zoomKey, zoomSecret, new ZoomSDKInitializeListener() {
            @Override
            public void onZoomSDKInitializeResult(int i, int i1) {

                AppLog.e(TAG, "Zoom SDK initialized : " + i + " , " + i1 + " , " + startOrJoin);

                try {
                    ZoomSDK.getInstance().getMeetingSettingsHelper().setMuteMyMicrophoneWhenJoinMeeting(true);
                    ZoomSDK.getInstance().getMeetingSettingsHelper().disableCopyMeetingUrl(true);
                    ZoomSDK.getInstance().getMeetingSettingsHelper().setClaimHostWithHostKeyActionEnabled(false);
                    ZoomSDK.getInstance().getMeetingSettingsHelper().disableShowVideoPreviewWhenJoinMeeting(true);
                } catch (Exception ex) {
                }

                if (startOrJoin)
                    startZoomMeeting(zoomMail, zoomPassword, zoomName, className, meetingId);
                else {
                    AppLog.e(TAG, "after initialize : isLogged IN Zoom : " + ZoomSDK.getInstance().isLoggedIn());
                    // joinZoomMeeting(zoomName, zoomPassword, className, meetingId);
                    logoutZoomBeforeJoining(zoomName, zoomPassword, className, meetingId);
                }
            }

            @Override
            public void onZoomAuthIdentityExpired() {
                hideLoadingBar();
                //progressBar.setVisibility(View.GONE);

            }
        });///APP_KEY , APP_SECRET


    }

    private void startMeeting() {

        try {
            Log.e(TAG, "On Click To startMeeting called : " + item.createdByName);

            isZoomStarted = true;

            if (isConnectionAvailable()) {

                if (item.canPost && item.isLive && item.isCreatedByMe()) {
                    initializeZoom(item.zoomKey, item.zoomSecret, item.zoomMail, item.zoomPassword, item.jitsiToken, item.zoomName.get(0), item.className, true);

                    AppLog.e(TAG, "SENDNOTIICATION CODE REACEDH");
                    new SendNotification(true, item.jitsiToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    isSentNotification = true;
                } else if (item.canPost && !item.isLive) {

                    //NOFIREBASEDATABASE
                    /*MeetingStatusModel model = new MeetingStatusModel();
                    model.t_id = item.getId();
                    SimpleDateFormat format = new SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                    format.setTimeZone(TimeZone.getTimeZone("UTC"));
                    model.create_at = format.format(Calendar.getInstance().getTime());
                    model.tech_id = LeafPreference.getInstance(getActivity()).getString(LeafPreference.LOGIN_ID);
                    model.tech_name = LeafPreference.getInstance(getActivity()).getString(LeafPreference.NAME);
                    myRef.child("live_class").child(item.getId()).setValue(model);
                    myRef.child("attendance").child(item.getId()).setValue(model);*/

                    initializeZoom(item.zoomKey, item.zoomSecret, item.zoomMail, item.zoomPassword, item.jitsiToken, item.zoomName.get(0), item.className, true);

                    AppLog.e(TAG, "SENDNOTIICATION CODE REACEDH");
                    new SendNotification(true, item.jitsiToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    isSentNotification = true;

                } else {
                    initializeZoom(item.zoomKey, item.zoomSecret, item.zoomMail, item.zoomMeetingPassword, item.jitsiToken, item.zoomName.get(0), item.className, false);

                    String loginID = LeafPreference.getInstance(getActivity()).getString(LeafPreference.LOGIN_ID);
                    String loginName = LeafPreference.getInstance(getActivity()).getString(LeafPreference.NAME);

                    SimpleDateFormat format = new SimpleDateFormat(
                            "hh:mma", Locale.getDefault());
                    format.setTimeZone(TimeZone.getTimeZone("UTC"));

                    //NOFIREBASEDATABASE
                  /*  MeetingStatusModel.AttendanceLiveClass selected = null;
                    selected = new MeetingStatusModel.AttendanceLiveClass();
                    selected.sname = loginName;
                    selected.joinAt.add(format.format(Calendar.getInstance().getTime()).toUpperCase());

                    myRef.child("attendance").child(item.getId()).child("attendance").child(loginID).setValue(selected);*/
               /* }
                myRef.child("attendance").child(item.getId()).setValue(model);

                    myRef.child("attendance").child(item.getId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            MeetingStatusModel model = task.getResult().getValue(MeetingStatusModel.class);
                            SimpleDateFormat format = new SimpleDateFormat(
                                    "hh:mma", Locale.getDefault());
                            format.setTimeZone(TimeZone.getTimeZone("UTC"));

                            String loginID = LeafPreference.getInstance(getActivity()).getString(LeafPreference.LOGIN_ID);
                            String loginName = LeafPreference.getInstance(getActivity()).getString(LeafPreference.NAME);
                            if (model != null)
                            {
                                MeetingStatusModel.AttendanceLiveClass selected = null;
                                for (int i = 0; i < model.attendance.size(); i++) {
                                    MeetingStatusModel.AttendanceLiveClass att = model.attendance.get(i);
                                    if (loginID.equalsIgnoreCase(att.uid)) {
                                        selected = att;
                                        break;
                                    }
                                }
                                if (selected != null)
                                {
                                    selected.joinAt.add(format.format(Calendar.getInstance().getTime()).toUpperCase());
                                } else {
                                    selected = new MeetingStatusModel.AttendanceLiveClass();
                                    selected.uid = loginID;
                                    selected.sname = loginName;
                                    selected.joinAt.add(format.format(Calendar.getInstance().getTime()).toUpperCase());
                                    model.attendance.add(selected);
                                }
                                myRef.child("attendance").child(item.getId()).setValue(model);
                            }
                        }
                    });*/
                }
            } else {
                showNoNetworkMsg();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private class SendNotification extends AsyncTask<String, String, String> {
        private String server_response;
        boolean isStart;
        String roomName;

        public SendNotification(boolean isStart, String Room) {
            this.isStart = isStart;
            this.roomName = Room;
        }

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL("https://fcm.googleapis.com/fcm/send");
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", BuildConfig.API_KEY_FIREBASE1 + BuildConfig.API_KEY_FIREBASE2);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                try {
                    JSONObject object = new JSONObject();

                    String topic;
                    String title = getResources().getString(R.string.app_name);
                    String name = LeafPreference.getInstance(getActivity()).getString(LeafPreference.NAME);
                    String message = isStart ? name + " teacher has started live class" : name + " teacher has ended live class";
                    topic = GroupDashboardActivityNew.groupId + "_" + item.getId();
                    object.put("to", "/topics/" + topic);

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title", title);
                    notificationObj.put("body", message);
                    object.put("notification", notificationObj);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("groupId", GroupDashboardActivityNew.groupId);
                    dataObj.put("createdById", LeafPreference.getInstance(getActivity()).getString(LeafPreference.LOGIN_ID));
                    dataObj.put("createdByName", name);
                    dataObj.put("teamId", item.getId());
                    dataObj.put("title", title);
                    dataObj.put("Notification_type", isStart ? "videoStart" : "videoEnd");
                    dataObj.put("body", message);
                    dataObj.put("roomName", roomName);
                    object.put("data", dataObj);
                    wr.writeBytes(object.toString());
                    Log.e(TAG, " JSON input : " + object.toString());
                    wr.flush();
                    wr.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                AppLog.e(TAG, "responseCode :" + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_response = readStream(urlConnection.getInputStream());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return server_response;
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            AppLog.e(TAG, "server_response :" + server_response);

            if (!TextUtils.isEmpty(server_response)) {
                AppLog.e(TAG, "Notification Sent");
            } else {
                AppLog.e(TAG, "Notification Send Fail");
            }
        }
    }


    private class SendNotificationResume extends AsyncTask<String, String, String> {
        private String server_response;
        String roomName;

        public SendNotificationResume(String Room) {

            this.roomName = Room;
        }

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL("https://fcm.googleapis.com/fcm/send");
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", BuildConfig.API_KEY_FIREBASE1 + BuildConfig.API_KEY_FIREBASE2);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                try {
                    JSONObject object = new JSONObject();

                    String topic;
                    String title = getResources().getString(R.string.app_name);
                    String name = LeafPreference.getInstance(getActivity()).getString(LeafPreference.NAME);
                    //  String message = isStart ? name + " teacher has started live class" : name + " teacher has ended live class";
                    topic = GroupDashboardActivityNew.groupId + "_" + item.getId();
                    object.put("to", "/topics/" + topic);

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title", title);
                    //   notificationObj.put("body", message);
                    // object.put("notification", notificationObj);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("groupId", GroupDashboardActivityNew.groupId);
                    dataObj.put("createdById", LeafPreference.getInstance(getActivity()).getString(LeafPreference.LOGIN_ID));
                    dataObj.put("teamId", item.getId());
                    dataObj.put("title", title);
                    dataObj.put("Notification_type", "videoResume");
                    dataObj.put("body", "");
                    dataObj.put("roomName", roomName);
                    object.put("data", dataObj);
                    wr.writeBytes(object.toString());
                    Log.e(TAG, " JSON input : " + object.toString());
                    wr.flush();
                    wr.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                AppLog.e(TAG, "responseCode :" + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_response = readStream(urlConnection.getInputStream());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return server_response;
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            AppLog.e(TAG, "server_response :" + server_response);

            if (!TextUtils.isEmpty(server_response)) {
                AppLog.e(TAG, "Notification Sent");
            } else {
                AppLog.e(TAG, "Notification Send Fail");
            }
        }
    }


    private void startZoomMeeting(String zoomMail, String password, String name, String className, String meetingId) {
//        ZoomSDK.getInstance().getMeetingSettingsHelper().setCustomizedMeetingUIEnabled(false);

        AppLog.e(TAG, "startzoommeeting called " + zoomMail + ", " + password + " , " + name + ", " + meetingId);

        isSentNotification = false;

        ZoomSDK.getInstance().removeAuthenticationListener(ZoomAuthListener);

        ZoomSDK.getInstance().removeAuthenticationListener(ZoomAuthLogoutListener);

        ZoomSDK.getInstance().addAuthenticationListener(ZoomAuthListener);


        if (!ZoomSDK.getInstance().isLoggedIn()) {
            //  ZoomSDK.getInstance().logoutZoom();
            Log.e(TAG, "loginwithzoom Called from startmeeting , not logged in already ");
            ZoomSDK.getInstance().loginWithZoom(zoomMail, password);
        } else {
            Log.e(TAG, "logoutzoom Called from startmeeting , already loggedIn");
            ZoomSDK.getInstance().logoutZoom();
        }

    }


    private void logoutZoomBeforeJoining(String name, String zoomPassword, String className, String meetingID) {

        AppLog.e(TAG, "logoutZoomBeforeJoining called " + name + ", " + className + ", " + meetingID);


        ZoomSDK.getInstance().removeAuthenticationListener(ZoomAuthLogoutListener);
        ZoomSDK.getInstance().removeAuthenticationListener(ZoomAuthListener);
        ZoomSDK.getInstance().addAuthenticationListener(ZoomAuthLogoutListener);
        ZoomSDK.getInstance().logoutZoom();

    }


    private void joinZoomMeeting(String name, String zoomPassword, String className, String meetingID) {
        JoinMeetingParams params = new JoinMeetingParams();

        AppLog.e(TAG, "joinzoommeeting called " + " , " + name + ", " + meetingID + " ");


        params.meetingNo = meetingID;
        params.password = zoomPassword;

        params.displayName = name;


//        ZoomSDK.getInstance().getMeetingSettingsHelper().setCustomizedMeetingUIEnabled(false);


        JoinMeetingOptions opts = new JoinMeetingOptions();
        opts.no_driving_mode = true;
        //opts.meeting_views_options = MeetingViewsOptions.NO_TEXT_MEETING_ID;
        // opts.no_meeting_end_message = true;
        // opts.no_titlebar = false;
        // opts.meeting_views_options = MeetingViewsOptions.NO_BUTTON_PARTICIPANTS;
        opts.no_bottom_toolbar = false;
        opts.no_invite = true;
        opts.no_video = false;//true
        opts.no_share = true;//false;
        opts.custom_meeting_id = className;


        opts.no_disconnect_audio = true;
        opts.no_audio = true;// set true


        opts.meeting_views_options = MeetingViewsOptions.NO_BUTTON_PARTICIPANTS + MeetingViewsOptions.NO_TEXT_MEETING_ID;// + MeetingViewsOptions.NO_BUTTON_AUDIO;//+ MeetingViewsOptions.NO_BUTTON_VIDEO +


      /*  StartMeetingOptions startMeetingOptions = new StartMeetingOptions();
        startMeetingOptions.no_video = false;*/


        ZoomSDK.getInstance().getMeetingService().removeListener(JoinMeetListener);
        ZoomSDK.getInstance().getMeetingService().removeListener(StartMeetListener);
        ZoomSDK.getInstance().getMeetingService().addListener(JoinMeetListener);

        ZoomSDK.getInstance().getMeetingService().joinMeetingWithParams(getActivity(), params, opts);

    }


    InMeetingServiceListener inMeetingListener = new InMeetingServiceListener() {
        @Override
        public void onMeetingNeedPasswordOrDisplayName(boolean b, boolean b1, InMeetingEventHandler inMeetingEventHandler) {

        }

        @Override
        public void onWebinarNeedRegister() {

        }

        @Override
        public void onJoinWebinarNeedUserNameAndEmail(InMeetingEventHandler inMeetingEventHandler) {

        }

        @Override
        public void onMeetingNeedColseOtherMeeting(InMeetingEventHandler inMeetingEventHandler) {

        }

        @Override
        public void onMeetingFail(int i, int i1) {

        }

        @Override
        public void onMeetingLeaveComplete(long l) {
            AppLog.e(TAG, "onMeetingLeaveComplete");

        }

        @Override
        public void onMeetingUserJoin(List<Long> list) {

        }

        @Override
        public void onMeetingUserLeave(List<Long> list) {
            AppLog.e(TAG, "onMeetingUserLeave");
        }

        @Override
        public void onMeetingUserUpdated(long l) {

        }

        @Override
        public void onMeetingHostChanged(long l) {
            AppLog.e(TAG, "onMeetingHostChanged");
        }

        @Override
        public void onMeetingCoHostChanged(long l) {

        }

        @Override
        public void onActiveVideoUserChanged(long l) {

        }

        @Override
        public void onActiveSpeakerVideoUserChanged(long l) {

        }

        @Override
        public void onSpotlightVideoChanged(boolean b) {

        }

        @Override
        public void onUserVideoStatusChanged(long l) {

        }

        @Override
        public void onUserVideoStatusChanged(long l, VideoStatus videoStatus) {

        }

        @Override
        public void onUserNetworkQualityChanged(long l) {

        }

        @Override
        public void onMicrophoneStatusError(InMeetingAudioController.MobileRTCMicrophoneError mobileRTCMicrophoneError) {

        }

        @Override
        public void onUserAudioStatusChanged(long l) {

        }

        @Override
        public void onUserAudioStatusChanged(long l, AudioStatus audioStatus) {

        }

        @Override
        public void onHostAskUnMute(long l) {

        }

        @Override
        public void onHostAskStartVideo(long l) {

        }

        @Override
        public void onUserAudioTypeChanged(long l) {

        }

        @Override
        public void onMyAudioSourceTypeChanged(int i) {

        }

        @Override
        public void onLowOrRaiseHandStatusChanged(long l, boolean b) {

        }

        @Override
        public void onMeetingSecureKeyNotification(byte[] bytes) {

        }

        @Override
        public void onChatMessageReceived(InMeetingChatMessage inMeetingChatMessage) {

        }

        @Override
        public void onSilentModeChanged(boolean b) {

        }

        @Override
        public void onFreeMeetingReminder(boolean b, boolean b1, boolean b2) {

        }

        @Override
        public void onMeetingActiveVideo(long l) {

        }

        @Override
        public void onSinkAttendeeChatPriviledgeChanged(int i) {

        }

        @Override
        public void onSinkAllowAttendeeChatNotification(int i) {

        }

        @Override
        public void onUserNameChanged(long l, String s) {

        }

        @Override
        public void onFreeMeetingNeedToUpgrade(FreeMeetingNeedUpgradeType freeMeetingNeedUpgradeType, String s) {

        }

        @Override
        public void onFreeMeetingUpgradeToGiftFreeTrialStart() {

        }

        @Override
        public void onFreeMeetingUpgradeToGiftFreeTrialStop() {

        }

        @Override
        public void onFreeMeetingUpgradeToProMeeting() {

        }

        @Override
        public void onClosedCaptionReceived(String s) {

        }

        @Override
        public void onRecordingStatus(RecordingStatus recordingStatus) {

        }
    };


    MeetingServiceListener JoinMeetListener = new MeetingServiceListener() {
        @Override
        public void onMeetingStatusChanged(MeetingStatus meetingStatus, int errorCode, int internalErrorCode) {
            Log.e(TAG, "meetinsstatusChanged Join: " + meetingStatus.name() + " errorcode : " + errorCode + " internalError: " + internalErrorCode);
            if (meetingStatus.name().equalsIgnoreCase("MEETING_STATUS_CONNECTING")) {
                hideLoadingBar();
                //progressBar.setVisibility(View.GONE);
                progressBarZoom.setVisibility(View.GONE);
            }

        }
    };



    MeetingServiceListener StartMeetListener = new MeetingServiceListener() {
        @Override
        public void onMeetingStatusChanged(MeetingStatus meetingStatus, int errorCode, int internalErrorCode) {
            Log.e(TAG, "meetinsstatusChanged : " + meetingStatus.name() + " errorcode : " + errorCode + " internalError: " + internalErrorCode);

            long saveTime = 0;

            if (meetingStatus.name().equalsIgnoreCase("MEETING_STATUS_CONNECTING")) {
                hideLoadingBar();
                //progressBar.setVisibility(View.GONE);

                progressBarZoom.setVisibility(View.GONE);

                saveTime = System.currentTimeMillis();
            //    LeafPreference.getInstance(getContext()).setString(LeafPreference.VIDEO_CALL_START_TIME,item.className+"_"+getCurrentTimeStamp());

             //   startTimer();
                /*if (getActivity() != null) {
                    ((VideoClassActivity) getActivity()).startBubbleService();
                }*/
            }

            if (meetingStatus.name().equalsIgnoreCase("MEETING_STATUS_INMEETING"))
            {
                startTimer();
            }
            if (meetingStatus.name().equalsIgnoreCase("MEETING_STATUS_DISCONNECTING")) {
//                AppLog.e(TAG, "meeting Disconnecting : " + item.canPost + " , " + meetingCreatedBy);

              /*  if (getActivity() != null && item.canPost && !item.isLive) {
                    ((VideoClassActivity) getActivity()).stopRecording();
                }*/


                isZoomStarted = false;


                if (isAutomatically)
                {
                    LeafPreference.getInstance(getContext()).remove(LeafPreference.VIDEO_CALL_START_TIME);
                    new SendNotificationResume(item.jitsiToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    startZoomMeeting(item.zoomName.get(0), item.zoomMeetingPassword, "", item.className, item.jitsiToken);
                }
                else
                {
                    if (isFirstTime)
                    {
                  /*  Log.e(TAG,"prefrence save time"+ LeafPreference.getInstance(getContext()).getString(LeafPreference.VIDEO_CALL_START_TIME));
                    String savedTime = LeafPreference.getInstance(getContext()).getString(LeafPreference.VIDEO_CALL_START_TIME);

                    String[] savedValuePart = savedTime.split("_");
                    String StartTime = savedValuePart[1];*/

                        if(checkTiming(saveTime))
                        {
                            isFirstTime = false;
                            LeafPreference.getInstance(getContext()).remove(LeafPreference.VIDEO_CALL_START_TIME);
                            new SendNotificationResume(item.jitsiToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            startZoomMeeting(item.zoomName.get(0), item.zoomMeetingPassword, "", item.className, item.jitsiToken);
                        }
                        else
                        {
                            dialogMeetingConfirmation();
                        }
                    }
                    else
                    {
                        dialogMeetingConfirmation();
                    }

                }
                endTimer();

                /*if (isFirstTime)
                {
                  *//*  Log.e(TAG,"prefrence save time"+ LeafPreference.getInstance(getContext()).getString(LeafPreference.VIDEO_CALL_START_TIME));
                    String savedTime = LeafPreference.getInstance(getContext()).getString(LeafPreference.VIDEO_CALL_START_TIME);

                    String[] savedValuePart = savedTime.split("_");
                    String StartTime = savedValuePart[1];*//*

                    if(checkTiming(saveTime))
                    {
                        isFirstTime = false;
                        LeafPreference.getInstance(getContext()).remove(LeafPreference.VIDEO_CALL_START_TIME);
                        new SendNotificationResume(item.jitsiToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        startZoomMeeting(item.zoomName.get(0), item.zoomMeetingPassword, "", item.className, item.jitsiToken);
                    }
                    else
                    {
                        dialogMeetingConfirmation();
                    }
                }
                else
                {
                    dialogMeetingConfirmation();
                }*/

          /*      try {
                    ((VideoClassActivity) getActivity()).removeBubble();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/


            }

        }
    };

    public String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date
            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    private boolean checkTiming(long time) {

        long diff = System.currentTimeMillis() - time;

        if (diff >= (39*60*1000))
        {
            return true;
        }


     /*   Log.e(TAG,"time "+time);

        try {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date oldDate = format.parse(time);

            Date currentDate = new Date();

            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            Log.e(TAG,"Hours "+hours);
            Log.e(TAG,"minutes "+minutes);
            Log.e(TAG,"seconds "+seconds);
            Log.e(TAG,"days "+days);

            if (minutes >= 1 || minutes <=3)
            {
                return true;
            }
            else
            {
                return false;
            }

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,"Exception "+e.getMessage());
        }*/
        return false;
    }


    CountDownTimer timer;
    long timeOfStopMeeting;

    private void dialogMeetingConfirmation() {
        if (getActivity() == null) {
            return;
        }

        //NOFIREBASEDATABASE
      /*  if (item.firebaseLive != null) {
            item.firebaseLive.auto_join = false;
            myRef.child("live_class").child(item.getId()).setValue(item.firebaseLive);
        }*/

        timeOfStopMeeting = System.currentTimeMillis();
        Dialog dialog = new Dialog(getActivity(), R.style.AppDialog);
        DialogMeetingOnOffBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.dialog_meeting_on_off, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        if ("booth".equalsIgnoreCase(category) || "boothVideo".equalsIgnoreCase(category) || "constituency".equalsIgnoreCase(category)) {
            binding.tvMsg.setText(getResources().getString(R.string.strMsgConst));
        }


        binding.tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                timer.cancel();

                new SendNotificationResume(item.jitsiToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                // joinZoomMeeting(item.zoomName.get(0), item.zoomMeetingPassword, item.className, item.jitsiToken);
                startZoomMeeting(item.zoomName.get(0), item.zoomMeetingPassword, "", item.className, item.jitsiToken);
                //NOFIREBASEDATABASE
               /* long curr = System.currentTimeMillis();
                long diffSec = (curr - timeOfStopMeeting) / 1000;
                if (diffSec > 10) {
                    if (item.firebaseLive != null) {
                        item.firebaseLive.auto_join = true;
                        myRef.child("live_class").child(item.getId()).setValue(item.firebaseLive);
                    }
                }
                else
                {
                    long newSec = 11 - diffSec;
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (item.firebaseLive != null) {
                                item.firebaseLive.auto_join = true;
                                myRef.child("live_class").child(item.getId()).setValue(item.firebaseLive);
                            }
                        }
                    }, newSec * 1000);

                }*/
            }
        });

        binding.tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                timer.cancel();
                stopMeeting(item);
            }
        });

        timer = new CountDownTimer(11000, 1000) {
            @Override
            public void onTick(long l) {

                binding.circularProgressBar.setProgressMax(11000);
                binding.circularProgressBar.setProgress(l);
                binding.tvTime.setText("" + l / 1000);
            }

            @Override
            public void onFinish() {
                binding.circularProgressBar.setProgress(0);
                binding.tvTime.setText("00");

                stopMeeting(item);
//                dialogMeetingConfirmation();
                dialog.dismiss();

                /*if (item.canPost && item.meetingCreatedBy && !isSentNotification) {
                    isSentNotification = true;
                    stopMeeting(item);
                } else {

                }*/
            }
        }.start();

    }

    ZoomSDKAuthenticationListener ZoomAuthLogoutListener = new ZoomSDKAuthenticationListener() {
        @Override
        public void onZoomSDKLoginResult(long result) {
            AppLog.e(TAG, "logoutZoomBeforeJoining , onZoomSDKLoginResult : " + result);
        }

        @Override
        public void onZoomSDKLogoutResult(long result) {
            AppLog.e(TAG, "logoutZoomBeforeJoining , onZOomSDKLogoutResult : " + result);

            ZoomSDK.getInstance().removeAuthenticationListener(this);
            joinZoomMeeting(item.zoomName.get(0), item.zoomMeetingPassword, item.className, item.jitsiToken);
        }

        @Override
        public void onZoomIdentityExpired() {
            AppLog.e(TAG, "onZOomIdentityExpired");
        }

        @Override
        public void onZoomAuthIdentityExpired() {
            AppLog.e(TAG, "onZoomAuthIdentityExpired");
        }

    };

    ZoomSDKAuthenticationListener ZoomAuthListener = new ZoomSDKAuthenticationListener() {
        @Override
        public void onZoomSDKLoginResult(long result) {
            Log.e(TAG, "startmeeting , onZoomLogin Result : " + result);
            if (result == 0) {

                ZoomSDK.getInstance().removeAuthenticationListener(this);
                InstantMeetingOptions opts = new InstantMeetingOptions();
                opts.custom_meeting_id = item.className;
                opts.meeting_views_options = MeetingViewsOptions.NO_TEXT_MEETING_ID;
                opts.no_invite = true;

                //opts.meeting_views_options = MeetingViewsOptions.NO_TEXT_MEETING_ID;
                if (getActivity() == null)
                    return;

                ZoomSDK.getInstance().getMeetingService().startInstantMeeting(VideoClassListFragment.this.getActivity(), opts);

                ZoomSDK.getInstance().getMeetingService().removeListener(StartMeetListener);
                ZoomSDK.getInstance().getMeetingService().removeListener(JoinMeetListener);
                ZoomSDK.getInstance().getMeetingService().addListener(StartMeetListener);
                ZoomSDK.getInstance().getInMeetingService().addListener(inMeetingListener);
            }

        }

        @Override
        public void onZoomSDKLogoutResult(long result) {
            AppLog.e(TAG, "startmeeting, onZOomSDKLogoutResult : " + result);

            ZoomSDK.getInstance().loginWithZoom(item.zoomMail, item.zoomPassword);
        }

        @Override
        public void onZoomIdentityExpired() {
            AppLog.e(TAG, "onZOomIdentityExpired");
        }

        @Override
        public void onZoomAuthIdentityExpired() {

            AppLog.e(TAG, "onZoomAuthIdentityExpired");

        }

    };

    private boolean checkPermissionForWriteExternal() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            AppLog.e("External" + "permission", "checkpermission , granted");
            return true;
        } else {
            AppLog.e("External" + "permission", "checkpermission , denied");
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_storage_permission_needed), Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 21);
            }
            return false;
        }
    }

    public void exportDataToCSV(MeetingStatusModelApi apiModel, boolean isDownload) {
        if (!checkPermissionForWriteExternal()) {
            return;
        }

        File mainFolder = new File(Environment.getExternalStorageDirectory(), LeafApplication.getInstance().getResources().getString(R.string.app_name));
        if (!mainFolder.exists()) {
            mainFolder.mkdir();
        }
        File csvFolder = new File(mainFolder, "Excel");
        if (!csvFolder.exists()) {
            csvFolder.mkdir();
        }

        String fileName = item.getName() + "_live_" + MixOperations.convertDate(Calendar.getInstance().getTime(), "yyyyMMddHHmmss");

        File file = new File(csvFolder, fileName + ".xls");

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet firstSheet = workbook.createSheet(item.getName());

            int indexRow = 0;

            HSSFRow row1 = firstSheet.createRow(indexRow);
            row1.createCell(0).setCellValue("Class Name");
            row1.createCell(1).setCellValue(item.getName());
            indexRow = indexRow + 1;

            HSSFRow row11 = firstSheet.createRow(indexRow);
            row11.createCell(0).setCellValue("Subject Name");
            row11.createCell(1).setCellValue(apiModel.subjectName + "");
            indexRow = indexRow + 1;

            HSSFRow row2 = firstSheet.createRow(indexRow);
            row2.createCell(0).setCellValue("Teacher Name");
            row2.createCell(1).setCellValue(apiModel.meetingCreatedByName);
            indexRow = indexRow + 1;

            HSSFRow row3 = firstSheet.createRow(indexRow);
            row3.createCell(0).setCellValue("Class Started At");
            row3.createCell(1).setCellValue(MixOperations.getFormattedDateOnly(apiModel.meetingCreatedAtTime, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd MMM yyyy hh:mm a"));
            indexRow = indexRow + 1;

            HSSFRow row4 = firstSheet.createRow(indexRow);
            row4.createCell(0).setCellValue("Class Ended At");
            row4.createCell(1).setCellValue(MixOperations.getFormattedDateOnly(apiModel.meetingEndedAtTime, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd MMM yyyy hh:mm a"));
            indexRow = indexRow + 2;

            HSSFRow rowA = firstSheet.createRow(indexRow);
            rowA.createCell(0).setCellValue("Student Name");
            rowA.createCell(1).setCellValue("Join At");
            indexRow = indexRow + 1;

            if (apiModel.attendance != null) {
                for (int i = 0; i < apiModel.attendance.size(); i++) {
                    MeetingStatusModelApi.AttendanceLiveClass att = apiModel.attendance.get(i);
                    HSSFRow rowData = firstSheet.createRow(i + indexRow);
                    rowData.createCell(0).setCellValue(att.studentName);
                    rowData.createCell(1).setCellValue(MixOperations.getFormattedDateOnly(att.meetingJoinedAtTime.get(0), "hh:mma", "hh:mm a"));
                }
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                workbook.write(fos);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (isDownload) {
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_attendance_report_save_successfully), Toast.LENGTH_SHORT).show();
            } else {
                shareFile(file);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shareFile(File file) {
        Uri uriFile;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            uriFile = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".fileprovider", file);
        } else {
            uriFile = Uri.fromFile(file);
        }
        Intent sharingIntent = new Intent();
        sharingIntent.setAction(Intent.ACTION_SEND);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uriFile);
        sharingIntent.setType("text/csv");
        startActivity(Intent.createChooser(sharingIntent, "share file with"));
    }

    private void showSubjectSelectDialog() {
        final Dialog dialog = new Dialog(getActivity(), R.style.FragmentDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_subject);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
        RecyclerView rvSubject = dialog.findViewById(R.id.rvSubjects);

        AttendanceSubjectAdapter subjectAdapter = new AttendanceSubjectAdapter(subjectList);
        rvSubject.setAdapter(subjectAdapter);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(subjectAdapter.getSelected())) {
                    dialog.dismiss();
                    StopMeetingReq stopMeetingReq = new StopMeetingReq(item.meetingIdOnLive, subjectAdapter.getSelected());
                    AppLog.e(TAG, "stopMeetingReq : " + stopMeetingReq);

                    showLoadingBar(progressBar);
                   // progressBar.setVisibility(View.VISIBLE);

                    leafManager.endLiveClass(VideoClassListFragment.this, GroupDashboardActivityNew.groupId, item.getId(), stopMeetingReq);
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.lbl_select_subject), Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showLoadingBar(progressBar);
                // progressBar.setVisibility(View.VISIBLE);
                leafManager.endLiveClass(VideoClassListFragment.this, GroupDashboardActivityNew.groupId, item.getId(), new StopMeetingReq(item.meetingIdOnLive));
            }
        });
        dialog.show();
    }

    public class AttendanceSubjectAdapter extends RecyclerView.Adapter<AttendanceSubjectAdapter.ViewHolder> {
        String selectedId = "";
        private final ArrayList<SubjectStaffResponse.SubjectData> listSubject;
        private Context mContext;

        public AttendanceSubjectAdapter(ArrayList<SubjectStaffResponse.SubjectData> listSubject) {
            this.listSubject = listSubject;
        }

        @Override
        public AttendanceSubjectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_attendance_subject_1, parent, false);
            return new AttendanceSubjectAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final AttendanceSubjectAdapter.ViewHolder holder, final int position) {
            SubjectStaffResponse.SubjectData itemList = subjectList.get(position);
            holder.tvName.setText(itemList.getName());
            holder.tvStaff.setText("[" + itemList.getStaffNameFormatted() + "]");
            if (selectedId.equalsIgnoreCase(itemList.getSubjectId())) {
                holder.chkAttendance.setChecked(true);
            } else {
                holder.chkAttendance.setChecked(false);
            }
        }

        @Override
        public int getItemCount() {
            return listSubject != null ? listSubject.size() : 0;
        }

        public String getSelected() {
            return selectedId;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.chkAttendance)
            CheckBox chkAttendance;

            @Bind(R.id.tvName)
            TextView tvName;

            @Bind(R.id.tvStaff)
            TextView tvStaff;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                chkAttendance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedId = listSubject.get(getAdapterPosition()).subjectId;
                        notifyDataSetChanged();
                    }
                });
            }
        }

    }

    private void getSubjectList(String teamId) {
        showLoadingBar(progressBar);
        // progressBar.setVisibility(View.VISIBLE);
        leafManager.getSubjectStaff(this, GroupDashboardActivityNew.groupId, teamId, "");
    }


    private void startTimer()
    {
        countDownTimer.start();
    }

    private void endTimer()
    {
        isAutomatically = false;
        countDownTimer.cancel();
    }

}
