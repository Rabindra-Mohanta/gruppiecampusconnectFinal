package school.campusconnect.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.baoyz.widget.PullRefreshLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.VideoClassActivity;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.DialogMeetingOnOffBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.subjects.SubjectStaffResponse;
import school.campusconnect.datamodel.videocall.MeetingStatusModel;
import school.campusconnect.datamodel.videocall.MeetingStatusModelApi;
import school.campusconnect.datamodel.videocall.VideoClassResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.views.SMBAlterDialog;
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

    @Bind(R.id.swipeRefreshLayout)
    public PullRefreshLayout swipeRefreshLayout;


    VideoClassResponse.ClassData item;

    boolean isSentNotification = false;

    boolean videoClassClicked = false;


    ClassesAdapter classesAdapter = new ClassesAdapter();

    private ArrayList<SubjectStaffResponse.SubjectData> subjectList;
    private ArrayList<VideoClassResponse.ClassData> result;
    private VideoClassResponse.ClassData listItemData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isSentNotification = false;
    }

    DatabaseReference myRef;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss, container, false);
        ButterKnife.bind(this, view);
        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        classesAdapter = new ClassesAdapter();
        rvClass.setAdapter(classesAdapter);

        getVideoClassList();

        init();

        return view;
    }

    private void init() {
        swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    swipeRefreshLayout.setRefreshing(false);
                    LeafManager leafManager = new LeafManager();
                    progressBar.setVisibility(View.VISIBLE);
                    leafManager.getVideoClasses(VideoClassListFragment.this, GroupDashboardActivityNew.groupId);
                } else {
                    showNoNetworkMsg();
                }
            }
        });
    }

    private void initFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    AppLog.e(TAG, "isSuccessful : true");
                    setListener();
                } else {
                    AppLog.e(TAG, "isSuccessful : false");
                }
            }
        });

    }

    private void setListener() {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AppLog.e(TAG, "onDataChange");
                HashMap<String, MeetingStatusModel> liveTeamIds = new HashMap<>();
                // Get Post object and use the values to update the UI
                Iterable<DataSnapshot> list = dataSnapshot.getChildren();
                // ..
                Iterator<DataSnapshot> itr = list.iterator();
                while (itr.hasNext()) {
                    DataSnapshot val = itr.next();
                    AppLog.e(TAG, "key : " + val.getKey());
                    liveTeamIds.put((val.getKey() + ""), val.getValue(MeetingStatusModel.class));
                }
                refreshAdapter(liveTeamIds);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        myRef.child("live_class").addValueEventListener(postListener);
    }

    private void refreshAdapter(HashMap<String, MeetingStatusModel> liveTeamIds) {
        String myId = LeafPreference.getInstance(getActivity()).getString(LeafPreference.LOGIN_ID);
        if (result != null && result.size() > 0) {
            for (int i = 0; i < result.size(); i++) {
                if (liveTeamIds.containsKey(result.get(i).getId())) {
                    result.get(i).isLive = true;
                    MeetingStatusModel val = liveTeamIds.get(result.get(i).getId());
                    result.get(i).firebaseLive = val;
                    if (myId.equalsIgnoreCase(val.tech_id)) {
                        result.get(i).meetingCreatedBy = true;
                    } else {
                        result.get(i).meetingCreatedBy = false;
                        if (val.auto_join && !result.get(i).isJoining) {
                            result.get(i).isJoining = true;
                            item = result.get(i);
                            // TODO : AUTO JOIN STUDENT
                            startMeeting();
                        }
                        if (!val.auto_join) {
                            result.get(i).isJoining = false;
                        }
                    }
                    result.get(i).createdName = val.tech_name;
                } else {
                    result.get(i).isJoining = false;
                    result.get(i).isLive = false;
                    result.get(i).firebaseLive = null;
                    result.get(i).createdName = "";
                    result.get(i).meetingCreatedBy = false;
                }
            }
            classesAdapter.notifyDataSetChanged();
            LeafPreference.getInstance(getActivity()).setString(LeafPreference.VIDEO_CLASS_LIST_OFFLINE, new Gson().toJson(result));
        }
    }

    private void getVideoClassList() {
        videoClassClicked = false;

        String re = LeafPreference.getInstance(getActivity()).getString(LeafPreference.VIDEO_CLASS_LIST_OFFLINE);
        if (!TextUtils.isEmpty(re)) {
            result = new Gson().fromJson(re, new TypeToken<List<VideoClassResponse.ClassData>>() {
            }.getType());
            classesAdapter.setList(result);
            initFirebase();
        } else {
            LeafManager leafManager = new LeafManager();
            progressBar.setVisibility(View.VISIBLE);
            leafManager.getVideoClasses(this, GroupDashboardActivityNew.groupId);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);
        if (apiId == LeafManager.API_Video_Class) {
            VideoClassResponse res = (VideoClassResponse) response;
            result = res.getData();
            AppLog.e(TAG, "ClassResponse " + result);
            classesAdapter.setList(result);
            LeafPreference.getInstance(getActivity()).setString(LeafPreference.VIDEO_CLASS_LIST_OFFLINE, new Gson().toJson(result));

            initFirebase();
        }
        if (apiId == LeafManager.API_SUBJECT_STAFF) {
            SubjectStaffResponse subjectStaffResponse = (SubjectStaffResponse) response;
            subjectList = subjectStaffResponse.getData();
            try {
                //showSubjectSelectDialog();
            } catch (Exception ex) {

            }
        }
        if (apiId == LeafManager.API_ONLINE_ATTENDANCE_PUSH) {
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
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
                holder.imgOnline.setVisibility(View.VISIBLE);
                if (item.getMeetingCreatedBy()) {
                    holder.tv_stop.setVisibility(View.VISIBLE);
                } else {
                    holder.tv_stop.setVisibility(View.GONE);
                }

            } else {
                holder.imgOnline.setVisibility(View.GONE);
                holder.tv_stop.setVisibility(View.GONE);
            }


            if (item.createdName != null && !item.createdName.equalsIgnoreCase("")) {
                holder.tvInfo.setVisibility(View.VISIBLE);
            } else
                holder.tvInfo.setVisibility(View.GONE);

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
            } else {
                holder.img_tree.setVisibility(View.GONE);
            }


            holder.tv_stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoClassListFragment.this.item = list.get(position);
                    dialogMeetingConfirmation();
                }
            });


            holder.tvInfo.setOnClickListener(new View.OnClickListener() {
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
            });


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

    private void onTreeClick(VideoClassResponse.ClassData classData) {
        this.item = classData;
        videoClassClicked = true;

        startMeeting();
        progressBarZoom.setVisibility(View.VISIBLE);
    }

    public void startMeetingFromActivity() {
        if (!videoClassClicked) {
            progressBar.setVisibility(View.VISIBLE);
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

        EnterSubjectDialog();
    }

    private void EnterSubjectDialog() {
        final Dialog dialog = new Dialog(getActivity(), R.style.AppDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_enter_subject);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
        EditText etName = dialog.findViewById(R.id.etName);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                progressBar.setVisibility(View.VISIBLE);

                myRef.child("attendance").child(item.getId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                        MeetingStatusModel model = task.getResult().getValue(MeetingStatusModel.class);

                        MeetingStatusModelApi apiModel = new MeetingStatusModelApi();
                        apiModel.teamId = model.t_id;
                        apiModel.meetingCreatedAtTime = model.create_at;
                        apiModel.meetingCreatedById = model.tech_id;
                        apiModel.meetingCreatedByName = model.tech_name;

                        for (int i = 0; i < model.attendance.size(); i++) {
                            MeetingStatusModelApi.AttendanceLiveClass ca = new MeetingStatusModelApi.AttendanceLiveClass();
                            ca.userId = model.attendance.get(i).uid;
                            ca.studentName = model.attendance.get(i).sname;
                            ca.meetingJoinedAtTime = model.attendance.get(i).joinAt;
                            apiModel.attendance.add(ca);
                        }

                        apiModel.month = "" + (Calendar.getInstance().get(Calendar.MONTH) + 1);
                        apiModel.subjectName = etName.getText().toString();
                        SimpleDateFormat format = new SimpleDateFormat(
                                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                        format.setTimeZone(TimeZone.getTimeZone("UTC"));
                        apiModel.meetingEndedAtTime = format.format(Calendar.getInstance().getTime());
                        AppLog.e(TAG, "apiModel : " + apiModel);

                        LeafManager leafManager = new LeafManager();
                        leafManager.attendancePush(VideoClassListFragment.this, GroupDashboardActivityNew.groupId, item.getId(), apiModel);

                        myRef.child("live_class").child(item.getId()).removeValue();
                        myRef.child("attendance").child(item.getId()).removeValue();

                        new SendNotification(false, item.jitsiToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    }
                });
            }
        });
        dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                myRef.child("attendance").child(item.getId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                        MeetingStatusModel model = task.getResult().getValue(MeetingStatusModel.class);

                        MeetingStatusModelApi apiModel = new MeetingStatusModelApi();
                        apiModel.teamId = model.t_id;
                        apiModel.meetingCreatedAtTime = model.create_at;
                        apiModel.meetingCreatedById = model.tech_id;
                        apiModel.meetingCreatedByName = model.tech_name;

                        for (int i=0;i<model.attendance.size();i++){
                            MeetingStatusModelApi.AttendanceLiveClass ca=new MeetingStatusModelApi.AttendanceLiveClass();
                            ca.userId = model.attendance.get(i).uid;
                            ca.studentName = model.attendance.get(i).sname;
                            ca.meetingJoinedAtTime = model.attendance.get(i).joinAt;
                            apiModel.attendance.add(ca);
                        }

                        apiModel.month = "" + (Calendar.getInstance().get(Calendar.MONTH) + 1);
                        apiModel.subjectName = etName.getText().toString();
                        SimpleDateFormat format = new SimpleDateFormat(
                                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                        format.setTimeZone(TimeZone.getTimeZone("UTC"));
                        apiModel.meetingEndedAtTime = format.format(Calendar.getInstance().getTime());
                        AppLog.e(TAG, "apiModel : " + apiModel);

                        LeafManager leafManager = new LeafManager();
                        leafManager.attendancePush(VideoClassListFragment.this, GroupDashboardActivityNew.groupId, item.getId(), apiModel);

                        myRef.child("live_class").child(item.getId()).removeValue();
                        myRef.child("attendance").child(item.getId()).removeValue();

                        new SendNotification(false, item.jitsiToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    }
                });
            }
        });
        dialog.show();
    }

    private void initializeZoom(String zoomKey, String zoomSecret, String zoomMail, String zoomPassword, String meetingId, String zoomName, String className, boolean startOrJoin) {

        progressBar.setVisibility(View.VISIBLE);
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
                progressBar.setVisibility(View.GONE);

            }
        });///APP_KEY , APP_SECRET


    }

    private void startMeeting() {
        try {
            Log.e(TAG, "On Click To startMeeting called : " + item.getMeetingCreatedBy());
            if (isConnectionAvailable()) {

                if (item.canPost && item.isLive && item.meetingCreatedBy) {
                    initializeZoom(item.zoomKey, item.zoomSecret, item.zoomMail, item.zoomPassword, item.jitsiToken, item.zoomName.get(0), item.className, true);

                    AppLog.e(TAG, "SENDNOTIICATION CODE REACEDH");
                    new SendNotification(true, item.jitsiToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    isSentNotification = true;
                } else if (item.canPost && !item.isLive) {
                    MeetingStatusModel model = new MeetingStatusModel();
                    model.t_id = item.getId();
                    SimpleDateFormat format = new SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                    format.setTimeZone(TimeZone.getTimeZone("UTC"));
                    model.create_at = format.format(Calendar.getInstance().getTime());
                    model.tech_id = LeafPreference.getInstance(getActivity()).getString(LeafPreference.LOGIN_ID);
                    model.tech_name = LeafPreference.getInstance(getActivity()).getString(LeafPreference.NAME);
                    myRef.child("live_class").child(item.getId()).setValue(model);
                    myRef.child("attendance").child(item.getId()).setValue(model);

                    initializeZoom(item.zoomKey, item.zoomSecret, item.zoomMail, item.zoomPassword, item.jitsiToken, item.zoomName.get(0), item.className, true);

                    AppLog.e(TAG, "SENDNOTIICATION CODE REACEDH");
                    new SendNotification(true, item.jitsiToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    isSentNotification = true;

                } else {
                    initializeZoom(item.zoomKey, item.zoomSecret, item.zoomMail, item.zoomMeetingPassword, item.jitsiToken, item.zoomName.get(0), item.className, false);
                    myRef.child("attendance").child(item.getId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            MeetingStatusModel model = task.getResult().getValue(MeetingStatusModel.class);
                            SimpleDateFormat format = new SimpleDateFormat(
                                    "hh:mma", Locale.getDefault());
                            format.setTimeZone(TimeZone.getTimeZone("UTC"));

                            String loginID = LeafPreference.getInstance(getActivity()).getString(LeafPreference.LOGIN_ID);
                            String loginName = LeafPreference.getInstance(getActivity()).getString(LeafPreference.NAME);
                            if (model != null) {
                                MeetingStatusModel.AttendanceLiveClass selected = null;
                                for (int i = 0; i < model.attendance.size(); i++) {
                                    MeetingStatusModel.AttendanceLiveClass att = model.attendance.get(i);
                                    if (loginID.equalsIgnoreCase(att.uid)) {
                                        selected = att;
                                        break;
                                    }
                                }
                                if (selected != null) {
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
                    });
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
                progressBar.setVisibility(View.GONE);
                progressBarZoom.setVisibility(View.GONE);
            }

        }
    };


    MeetingServiceListener StartMeetListener = new MeetingServiceListener() {
        @Override
        public void onMeetingStatusChanged(MeetingStatus meetingStatus, int errorCode, int internalErrorCode) {
            Log.e(TAG, "meetinsstatusChanged : " + meetingStatus.name() + " errorcode : " + errorCode + " internalError: " + internalErrorCode);

            if (meetingStatus.name().equalsIgnoreCase("MEETING_STATUS_CONNECTING")) {
                progressBar.setVisibility(View.GONE);
                progressBarZoom.setVisibility(View.GONE);

                if (getActivity() != null) {
                    ((VideoClassActivity) getActivity()).startBubbleService();
                }
            }

            if (meetingStatus.name().equalsIgnoreCase("MEETING_STATUS_DISCONNECTING")) {
//                AppLog.e(TAG, "meeting Disconnecting : " + item.canPost + " , " + meetingCreatedBy);

                if (getActivity() != null && item.canPost && !item.isLive) {
                    ((VideoClassActivity) getActivity()).stopRecording();
                }

                try {
                    ((VideoClassActivity) getActivity()).removeBubble();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                dialogMeetingConfirmation();
            }
        }
    };

    CountDownTimer timer;
    long timeOfStopMeeting;

    private void dialogMeetingConfirmation() {
        if (getActivity() == null) {
            return;
        }

        if (item.firebaseLive != null) {
            item.firebaseLive.auto_join = false;
            myRef.child("live_class").child(item.getId()).setValue(item.firebaseLive);
        }

        timeOfStopMeeting = System.currentTimeMillis();
        Dialog dialog = new Dialog(getActivity(), R.style.AppDialog);
        DialogMeetingOnOffBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.dialog_meeting_on_off, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        binding.tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                timer.cancel();

                joinZoomMeeting(item.zoomName.get(0), item.zoomMeetingPassword, item.className, item.jitsiToken);

                long curr = System.currentTimeMillis();
                long diffSec = (curr - timeOfStopMeeting) / 1000;
                if (diffSec > 10) {
                    if (item.firebaseLive != null) {
                        item.firebaseLive.auto_join = true;
                        myRef.child("live_class").child(item.getId()).setValue(item.firebaseLive);
                    }
                } else {
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

                }
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
        progressBar.setVisibility(View.VISIBLE);
        LeafManager leafManager = new LeafManager();
        leafManager.getSubjectStaff(this, GroupDashboardActivityNew.groupId, teamId, "");
    }

}
