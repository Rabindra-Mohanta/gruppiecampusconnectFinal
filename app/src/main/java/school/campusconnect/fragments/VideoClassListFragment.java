package school.campusconnect.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.VideoClassActivity;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.videocall.StartMeetingRes;
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
import us.zoom.sdk.InviteOptions;
import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.MeetingViewsOptions;
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.StartMeetingParams;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitializeListener;
import us.zoom.sdk.ZoomUIService;

import static school.campusconnect.network.LeafManager.API_JISTI_MEETING_START;
import static school.campusconnect.network.LeafManager.API_JISTI_MEETING_STOP;

public class VideoClassListFragment extends BaseFragment implements LeafManager.OnCommunicationListener{
    private static final String TAG = "VideoClassListFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;


    @Bind(R.id.progressBarZoom)
    public ProgressBar progressBarZoom;

    VideoClassResponse.ClassData item;

    boolean meetingCreatedBy =false;

    boolean isSentNotification = false;

    boolean videoClassClicked = false;


    ClassesAdapter classesAdapter = new ClassesAdapter();
    VideoClassResponse.ClassData selectedClassdata ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isSentNotification = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss, container, false);
        ButterKnife.bind(this, view);
        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));


        progressBar.setVisibility(View.VISIBLE);


        classesAdapter = new ClassesAdapter();
        rvClass.setAdapter(classesAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        videoClassClicked = false;
    }

    @Override
    public void onResume() {
        super.onResume();

        LeafManager leafManager = new LeafManager();

        leafManager.getVideoClasses(this, GroupDashboardActivityNew.groupId);

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        if(apiId == API_JISTI_MEETING_START)
        {
            Log.e(TAG , "OnSuccess called ");

            StartMeetingRes startMeetingRes = (StartMeetingRes) response;

            if(startMeetingRes.data != null && !startMeetingRes.data.get(0).jitsiToken.equalsIgnoreCase(""))
            {

                //startActivity(meetIntent);
                //showJitsiOptions(meetIntent , startMeetingRes.data.get(0).jitsiToken);

                meetingCreatedBy = startMeetingRes.data.get(0).isMeetingCreatedBy();

                Log.e(TAG , "meetingCreatedBy after startMeeting API call :"+startMeetingRes.data.get(0).isMeetingCreatedBy());

                if(item.canPost && meetingCreatedBy)
                {
                    // startZoomMeeting(item.zoomMail ,item.zoomPassword , item.jitsiToken , item.zoomName.get(0));
                    initializeZoom(item.zoomKey , item.zoomSecret , item.zoomMail , item.zoomPassword , item.jitsiToken ,  item.zoomName.get(0) , item.className ,true);
                }
                else
                {
                    //joinZoomMeeting(item.zoomMail , item.zoomPassword , item.jitsiToken , item.zoomName.get(0));
                    initializeZoom(item.zoomKey , item.zoomSecret , item.zoomMail, item.zoomPassword , item.jitsiToken  ,  item.zoomName.get(0)  , item.className , false);
                }

                if(startMeetingRes.data.get(0).isMeetingCreatedBy() && !isSentNotification)
                {
                    AppLog.e(TAG , "SENDNOTIICATION CODE REACEDH");
                    new SendNotification(true, item.jitsiToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    isSentNotification =  true;
                }
            }
            return;
        }
        else if(apiId == API_JISTI_MEETING_STOP)
        {

            if (apiId == LeafManager.API_JISTI_MEETING_STOP) {
                StartMeetingRes startMeetingRes = (StartMeetingRes) response;
                if(startMeetingRes.data!=null && startMeetingRes.data.size()>0)
                {
                    AppLog.e(TAG , "SENDNOTIFICAITNO CODE ReACHED AT STOP");
                    new SendNotification(false , item.jitsiToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

                LeafManager leafManager = new LeafManager();

                leafManager.getVideoClasses(this, GroupDashboardActivityNew.groupId);

            }
            return;

        }



        progressBar.setVisibility(View.GONE);
        VideoClassResponse res = (VideoClassResponse) response;
        List<VideoClassResponse.ClassData> result = res.getData();
        AppLog.e(TAG, "ClassResponse " + result);

       // rvClass.setAdapter(new ClassesAdapter(result));

        classesAdapter.setList(result);
    }


    public void showJitsiOptions(Intent meetIntent , String meetingId)
    {
      /*  Dialog dialog = new Dialog(getActivity());
        LayoutInflater inflater = ( getActivity()).getLayoutInflater();
        View newView = (View) inflater.inflate(R.layout.custom_dialog_jitsioption, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(newView);
*/
     /* String[] choices = new String[]{"Open In Jitsi App", "Open Within App"};

        SMBDialogUtils.showSMBSingleChoiceDialog(getActivity(),
                R.string.lbl_startmeeting, choices, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lw = ((AlertDialog) dialog).getListView();
                      // pdfView.jumpTo(lw.getCheckedItemPosition(),true);
                        if(which == 0)
                        {
                            final String appPackageName = BuildConfig.APPLICATION_ID; // getPackageName() from Context or Activity object
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://meet.jitsi.si/" + meetingId)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=org.jitsi.meet")));
                            }
                            dialog.dismiss();
                        }
                        else
                        {
                            startActivity(meetIntent);
                            dialog.dismiss();
                        }
                    }
                });*/


        final Dialog dialog=new Dialog(getActivity(),R.style.AppTheme_AlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_jitsioption);
        final RadioButton rbgruppie=dialog.findViewById(R.id.rb_gruppie);
        final RadioButton rbjitsi=dialog.findViewById(R.id.rb_jitsi);

        rbjitsi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                final String appPackageName = BuildConfig.APPLICATION_ID; // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://meet.jit.si/" + meetingId)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=org.jitsi.meet")));
                }
                dialog.dismiss();

            }
        });

        rbgruppie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(meetIntent);
                dialog.dismiss();

            }
        });

        dialog.show();


     /*   SMBAlterDialog dialog = new SMBAlterDialog(getActivity());
        dialog.setTitle(R.string.app_name);
        dialog.setNegativeButtonWithListener();
        dialog.setMessage("START MEETING");
        dialog.setPositiveButton("Open In Jitsi App", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String appPackageName = BuildConfig.APPLICATION_ID; // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://meet.frenzinsoftwares.com/" + meetingId)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=org.jitsi.meet")));
                }
                dialog.dismiss();

            }
        });

        dialog.setNegativeButton("Open Within App", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(meetIntent);
                dialog.dismiss();
            }
        });
        dialog.show();*/
/*

        TextView btn_ok = (TextView) dialog.findViewById(R.id.btn_ok);

        TextView btn_cancel = (TextView) dialog.findViewById(R.id.btn_cancel);


        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = BuildConfig.APPLICATION_ID; // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://meet.jit.si/" + meetingId)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=org.jitsi.meet")));
                }
                dialog.dismiss();
                new SendNotification(true , item.jitsiToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(meetIntent);
                dialog.dismiss();
            }
        });

        dialog.show();
*/

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



        public ClassesAdapter()
        {
            list = new ArrayList<>();
        }

        public void setList(List<VideoClassResponse.ClassData> list)
        {
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

            if(item.canPost && item.alreadyOnJitsiLive){
                holder.imgOnline.setVisibility(View.VISIBLE);

                if(item.getMeetingCreatedBy()){
                    holder.tv_stop.setVisibility(View.VISIBLE);
                }else {
                    holder.tv_stop.setVisibility(View.GONE);
                }

            }else {
                holder.imgOnline.setVisibility(View.GONE);
                holder.tv_stop.setVisibility(View.GONE);
            }



            if(item.getMeetingCreatedByName() != null && !item.getMeetingCreatedByName().equalsIgnoreCase(""))
            {
                holder.tvInfo.setVisibility(View.VISIBLE);
            }
            else
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

            if(item.canPost || item.alreadyOnJitsiLive){
                holder.img_tree.setVisibility(View.VISIBLE);
            }else {
                holder.img_tree.setVisibility(View.GONE);
            }


            holder.tv_stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                    final SMBAlterDialog dialog =
                            new SMBAlterDialog(getActivity());
                    dialog.setTitle(R.string.app_name);
                    dialog.setMessage("Are You Sure Want To End Meeting ?");
                    dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            stopMeeting(list.get(position));
                            dialog.dismiss();
                        }
                    });
                    dialog.show();


                }
            });


            holder.tvInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                    final SMBAlterDialog dialog =
                            new SMBAlterDialog(getActivity());
                    dialog.setTitle(R.string.app_name);
                    dialog.setMessage("Meeting Created By "+list.get(position).getMeetingCreatedByName());
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
                        onTreeClick(list.get(getAdapterPosition()));
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

    private void onTreeClick(VideoClassResponse.ClassData classData)
    {
        selectedClassdata = classData;

        if(classData.canPost && !classData.alreadyOnJitsiLive) {
            ((VideoClassActivity) getActivity()).startRecordingScreen(selectedClassdata);
        }
        else
        {
            videoClassClicked = true;
            startMeeting(selectedClassdata);
            progressBarZoom.setVisibility(View.VISIBLE);
        }

       /* if(!videoClassClicked)
        {
            Log.e(TAG , "onTreeClick  : "+videoClassClicked);
            videoClassClicked = true;
            startMeeting(classData);

          //  ((VideoClassActivity) getActivity()).startRecordingScreen();
        }*/
    }

    public void startMeetingFromActivity()
    {
        if(!videoClassClicked)
        {
            Log.e(TAG , "onTreeClick  : "+videoClassClicked);
            videoClassClicked = true;
            startMeeting(selectedClassdata);
            progressBarZoom.setVisibility(View.VISIBLE);

        }
    }

    private void onNameClick(VideoClassResponse.ClassData classData)
    {
            AppLog.e(TAG , "onNameClick called ");

          //  ((VideoClassActivity) getActivity()).startRecordingScreen();
            return;

    }


    private void stopMeeting(VideoClassResponse.ClassData classData)
    {
        this.item = classData;
        LeafManager leafManager = new LeafManager();
        leafManager.stopMeeting(VideoClassListFragment.this, GroupDashboardActivityNew.groupId, classData.getId());
    }

    private void initializeZoom(String zoomKey , String zoomSecret , String zoomMail , String zoomPassword , String meetingId , String zoomName , String className, boolean startOrJoin)
    {

        progressBar.setVisibility(View.VISIBLE);
        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        zoomSDK.initialize(getActivity(), zoomKey, zoomSecret, new ZoomSDKInitializeListener() {
            @Override
            public void onZoomSDKInitializeResult(int i, int i1) {

                AppLog.e(TAG , "Zoom SDK initialized : "+i+" , "+i1+" , "+startOrJoin);

                ZoomSDK.getInstance().getMeetingSettingsHelper().setMuteMyMicrophoneWhenJoinMeeting(true);
                ZoomSDK.getInstance().getMeetingSettingsHelper().disableCopyMeetingUrl(true);
                ZoomSDK.getInstance().getMeetingSettingsHelper().setClaimHostWithHostKeyActionEnabled(false);
                ZoomSDK.getInstance().getMeetingSettingsHelper().disableShowVideoPreviewWhenJoinMeeting(true);


                if(startOrJoin)
                    startZoomMeeting(zoomMail , zoomPassword , zoomName, className,  meetingId);
                else
                {
                    AppLog.e(TAG, "after initialize : isLogged IN Zoom : "+ZoomSDK.getInstance().isLoggedIn());
                   // joinZoomMeeting(zoomName, zoomPassword, className, meetingId);
                     logoutZoomBeforeJoining(zoomName ,zoomPassword , className ,meetingId);
                }


            }

            @Override
            public void onZoomAuthIdentityExpired() {
                progressBar.setVisibility(View.GONE);

            }
        });///APP_KEY , APP_SECRET


    }


    private void startMeeting(VideoClassResponse.ClassData item)
    {
        Log.e(TAG ,"On Click To startMeeting called : "+item.getMeetingCreatedBy());
        if(isConnectionAvailable())
        {

            meetingCreatedBy = false;


            this.item = item;
            if (item.canPost && !item.alreadyOnJitsiLive)
            {
                LeafManager leafManager = new LeafManager();
                leafManager.startMeeting(this, GroupDashboardActivityNew.groupId, item.getId());
                return;
            }

            // showJitsiOptions(meetIntent  , item.getJitsiToken() );
           /* if(item.canPost && !item.alreadyOnJitsiLive)
                initializeZoom(item.zoomKey , item.zoomSecret , item.zoomMail, item.zoomPassword , item.jitsiToken  ,  item.zoomName.get(0)  , item.className , true);
            else*/
                initializeZoom(item.zoomKey , item.zoomSecret , item.zoomMail, item.zoomMeetingPassword , item.jitsiToken  ,  item.zoomName.get(0)  , item.className, false);


            // startActivity(meetIntent);

        }
        else
        {
            showNoNetworkMsg();
        }

    }


    private class SendNotification extends AsyncTask<String, String, String> {
        private String server_response;
        boolean isStart;
        String roomName;
        public SendNotification(boolean isStart ,String Room) {
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
                urlConnection.setRequestProperty("Authorization", BuildConfig.API_KEY_FIREBASE1+BuildConfig.API_KEY_FIREBASE2);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                try {
                    JSONObject object = new JSONObject();

                    String topic;
                    String title = getResources().getString(R.string.app_name);
                    String name = LeafPreference.getInstance(getActivity()).getString(LeafPreference.NAME);
                    String message = isStart?name+" teacher has started live class":name+" teacher has ended live class";
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
                    dataObj.put("Notification_type", isStart?"videoStart":"videoEnd");
                    dataObj.put("body", message);
                    dataObj.put("roomName" , roomName);
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



    private void startZoomMeeting(String zoomMail , String password , String name , String className , String meetingId)
    {
//        ZoomSDK.getInstance().getMeetingSettingsHelper().setCustomizedMeetingUIEnabled(false);

        AppLog.e(TAG , "startzoommeeting called "+zoomMail+", "+password+" , "+name +", "+meetingId);

        isSentNotification = false;

        ZoomSDK.getInstance().removeAuthenticationListener(ZoomAuthListener);

        ZoomSDK.getInstance().removeAuthenticationListener(ZoomAuthLogoutListener);

        ZoomSDK.getInstance().addAuthenticationListener(ZoomAuthListener);


        if(!ZoomSDK.getInstance().isLoggedIn())
        {
          //  ZoomSDK.getInstance().logoutZoom();
            Log.e(TAG ,"loginwithzoom Called from startmeeting , not logged in already ");
            ZoomSDK.getInstance().loginWithZoom(zoomMail, password);
        }else
        {
            Log.e(TAG ,"logoutzoom Called from startmeeting , already loggedIn");
            ZoomSDK.getInstance().logoutZoom();
        }

    }


    private void logoutZoomBeforeJoining(String name , String zoomPassword , String className ,String meetingID)
    {

        AppLog.e(TAG , "logoutZoomBeforeJoining called "+name+", "+className+ ", "+meetingID);


        ZoomSDK.getInstance().removeAuthenticationListener(ZoomAuthLogoutListener);
        ZoomSDK.getInstance().removeAuthenticationListener(ZoomAuthListener);
        ZoomSDK.getInstance().addAuthenticationListener(ZoomAuthLogoutListener);

        ZoomSDK.getInstance().logoutZoom();

    }



    private void joinZoomMeeting(String name , String zoomPassword , String className ,String meetingID)
    {
        JoinMeetingParams params = new JoinMeetingParams();


        AppLog.e(TAG , "joinzoommeeting called "+" , "+name +", "+meetingID + " ");


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


        opts.meeting_views_options = MeetingViewsOptions.NO_BUTTON_PARTICIPANTS+MeetingViewsOptions.NO_TEXT_MEETING_ID;// + MeetingViewsOptions.NO_BUTTON_AUDIO;//+ MeetingViewsOptions.NO_BUTTON_VIDEO +


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
            AppLog.e(TAG , "onMeetingLeaveComplete");

        }

        @Override
        public void onMeetingUserJoin(List<Long> list) {

        }

        @Override
        public void onMeetingUserLeave(List<Long> list) {
            AppLog.e(TAG , "onMeetingUserLeave");
        }

        @Override
        public void onMeetingUserUpdated(long l) {

        }

        @Override
        public void onMeetingHostChanged(long l) {
            AppLog.e(TAG , "onMeetingHostChanged");
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
            Log.e(TAG , "meetinsstatusChanged : "+meetingStatus.name()+" errorcode : "+errorCode+" internalError: "+internalErrorCode);
            if(meetingStatus.name().equalsIgnoreCase("MEETING_STATUS_CONNECTING")) {
                progressBar.setVisibility(View.GONE);
                progressBarZoom.setVisibility(View.GONE);
            }

        }
    };


    MeetingServiceListener StartMeetListener = new MeetingServiceListener() {
        @Override
        public void onMeetingStatusChanged(MeetingStatus meetingStatus, int errorCode, int internalErrorCode) {
            Log.e(TAG, "meetinsstatusChanged : " + meetingStatus.name() + " errorcode : " + errorCode + " internalError: " + internalErrorCode);

            if(meetingStatus.name().equalsIgnoreCase("MEETING_STATUS_CONNECTING"))
            {
                progressBar.setVisibility(View.GONE);
                progressBarZoom.setVisibility(View.GONE);
            }


            if(meetingStatus.name().equalsIgnoreCase("MEETING_STATUS_DISCONNECTING"))
            {
                AppLog.e(TAG , "meeting Disconnecting : "+item.canPost + " , "+meetingCreatedBy);

                if(getActivity() !=null && item.canPost && !item.alreadyOnJitsiLive)
                {
                    ((VideoClassActivity)getActivity()).stopRecording();
                }


                if (item.canPost && meetingCreatedBy && !isSentNotification ) {
                    isSentNotification = true;
                    stopMeeting(item);
                } else {

                }
            }

        }
    };

    ZoomSDKAuthenticationListener ZoomAuthLogoutListener = new ZoomSDKAuthenticationListener() {
        @Override
        public void onZoomSDKLoginResult(long result) {
            AppLog.e(TAG , "logoutZoomBeforeJoining , onZoomSDKLoginResult : "+result);
        }

        @Override
        public void onZoomSDKLogoutResult(long result) {
            AppLog.e(TAG , "logoutZoomBeforeJoining , onZOomSDKLogoutResult : "+result);

            joinZoomMeeting(item.zoomName.get(0) , item.zoomMeetingPassword , item.className , item.jitsiToken);
        }

        @Override
        public void onZoomIdentityExpired() {
            AppLog.e(TAG , "onZOomIdentityExpired");
        }

        @Override
        public void onZoomAuthIdentityExpired() {

            AppLog.e(TAG , "onZoomAuthIdentityExpired");

        }

    };

    ZoomSDKAuthenticationListener ZoomAuthListener = new ZoomSDKAuthenticationListener() {
        @Override
        public void onZoomSDKLoginResult(long result) {
            Log.e(TAG ,"startmeeting , onZoomLogin Result : "+result);
            if(result ==0 )
            {
                InstantMeetingOptions opts = new InstantMeetingOptions();
                opts.custom_meeting_id = item.className;
                opts.meeting_views_options = MeetingViewsOptions.NO_TEXT_MEETING_ID ;
                opts.no_invite = true;


                //opts.meeting_views_options = MeetingViewsOptions.NO_TEXT_MEETING_ID;
                if(getActivity() ==null)
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
            AppLog.e(TAG , "startmeeting, onZOomSDKLogoutResult : "+result);
            ZoomSDK.getInstance().loginWithZoom(item.zoomMail, item.zoomPassword);
        }

        @Override
        public void onZoomIdentityExpired() {
            AppLog.e(TAG , "onZOomIdentityExpired");
        }

        @Override
        public void onZoomAuthIdentityExpired() {

            AppLog.e(TAG , "onZoomAuthIdentityExpired");

        }

    };

}
