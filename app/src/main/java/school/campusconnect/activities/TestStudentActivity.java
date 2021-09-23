package school.campusconnect.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.instacart.library.truetime.TrueTimeRx;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import school.campusconnect.Assymetric.AsymmetricRecyclerView;
import school.campusconnect.Assymetric.AsymmetricRecyclerViewAdapter;
import school.campusconnect.R;
import school.campusconnect.adapters.ChildAdapter;
import school.campusconnect.adapters.ChildVideoAdapter;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.GroupValidationError;
import school.campusconnect.datamodel.StudTestPaperItem;
import school.campusconnect.datamodel.chapter.ChapterRes;
import school.campusconnect.datamodel.test_exam.TestExamRes;
import school.campusconnect.datamodel.test_exam.TestPaperRes;
import school.campusconnect.datamodel.videocall.VideoClassResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.service.FloatingWidgetExamService;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.MixOperations;
import school.campusconnect.views.SMBDialogUtils;
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

public class TestStudentActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<AddPostValidationError> {

    private static final String TAG = TestStudentActivity.class.getSimpleName();
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;

    @Bind(R.id.txt_title)
    TextView txt_title;
    @Bind(R.id.txt_content)
    TextView txtContent;
    @Bind(R.id.txt_readmore)
    TextView txt_readmore;
    @Bind(R.id.recyclerView)
    AsymmetricRecyclerView recyclerView;
    @Bind(R.id.img_play)
    ImageView imgPlay;
    @Bind(R.id.image)
    ImageView imgPhoto;
    @Bind(R.id.constThumb)
    ConstraintLayout constThumb;
    @Bind(R.id.imageThumb)
    ImageView imageThumb;
    @Bind(R.id.imgDownloadPdf)
    ImageView imgDownloadPdf;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.rvAssignment)
    RecyclerView rvAssignment;
    @Bind(R.id.txtEmpty)
    TextView txtEmpty;
    @Bind(R.id.txt_lastDate)
    TextView txt_lastDate;

    @Bind(R.id.txt_teacher)
    TextView txt_teacher;

    @Bind(R.id.btnSubmit)
    Button btnSubmit;

    @Bind(R.id.tvTimer)
    TextView tvTimer;

    @Bind(R.id.llSubmit)
    FrameLayout llSubmit;

    @Bind(R.id.btnStart)
    Button btnStart;

    @Bind(R.id.tvQPaperHide)
    ConstraintLayout tvQPaperHide;

    @Bind(R.id.swipeRefreshLayout)
    PullRefreshLayout swipeRefreshLayout;
    private static final int DRAW_OVER_OTHER_APP_PERMISSION = 123;

    private String group_id;
    private String team_id;
    private String subject_id;
    private String subject_name;
    private String className;
    private boolean showStart;
    TestExamRes.TestExamData item;
    private long currentTimeFromServer;

    VideoClassResponse.ClassData videClassData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_student);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);

        _init();

        String nTopic = item.topicName.length() > 15 ? item.topicName.substring(0, 15) : item.topicName;
        setTitle(nTopic + " (" + className + ")");

        new TESTGOOGLE().execute(null ,null);

    }


    private void getDataLocally() {
        List<StudTestPaperItem> list = StudTestPaperItem.getAll(item.testExamId, team_id, GroupDashboardActivityNew.groupId);
        if (list.size() != 0) {
            ArrayList<TestPaperRes.TestPaperData> result = new ArrayList<>();

            for (int i = 0; i < list.size(); i++) {
                StudTestPaperItem currentItem = list.get(i);

                TestPaperRes.TestPaperData item = new TestPaperRes.TestPaperData();

                item.submittedById = currentItem.submittedById;
                item.testexamVerified = currentItem.testexamVerified;
                item.studentTestExamId = currentItem.studentTestExamId;
                item.studentName = currentItem.studentName;
                item.studentImage = currentItem.studentImage;
                item.description = currentItem.description;
                item.insertedAt = currentItem.insertedAt;
                item.fileType = currentItem.fileType;
                item.fileName = new Gson().fromJson(currentItem.fileName, new TypeToken<ArrayList<String>>() {
                }.getType());
                item.thumbnailImage = new Gson().fromJson(currentItem.thumbnailImage, new TypeToken<ArrayList<String>>() {
                }.getType());
                item.rollNumber = currentItem.rollNumber;
                item.studentDbId = currentItem.studentDbId;
                item.studentDbId = currentItem.studentDbId;
                item.submittedById = currentItem.submittedById;
                item.thumbnail = currentItem.thumbnail;
                item.userId = currentItem.userId;
                item.verifiedComment = currentItem.verifiedComment;
                item.video = currentItem.video;

                result.add(item);
            }


            rvAssignment.setAdapter(new AssignmentAdapter(result));
            showHideSubmitButton(result);
//
            if (LeafPreference.getInstance(this).getInt(team_id + "_test_count_noti") > 0) {
                LeafPreference.getInstance(this).setBoolean(team_id + "_test_count_noti", false);
            }
            getAssignment(false);
//
        } else {
            getAssignment(true);
        }
    }


    private void _init() {
        videClassData = new Gson().fromJson(getIntent().getStringExtra("liveClass"), VideoClassResponse.ClassData.class);
        group_id = getIntent().getStringExtra("group_id");
        team_id = getIntent().getStringExtra("team_id");
        subject_id = getIntent().getStringExtra("subject_id");
        subject_name = getIntent().getStringExtra("subject_name");
        className = getIntent().getStringExtra("className");
        showStart = getIntent().getBooleanExtra("start", false);
        item = (TestExamRes.TestExamData) getIntent().getSerializableExtra("data");

        swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    getAssignment(true);
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    showNoNetworkMsg();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        llSubmit.setVisibility(View.GONE);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(TestStudentActivity.this)) {
                    startMeeting();
                    startBubbleService();
                } else {
                    requestOverlayPermission();
                }
            }
        });

    }

    private void showData() {

        if (item.proctoring) {
            long dtExamStart = MixOperations.getDateFromStringDate(item.testDate + " " + item.testStartTime, "dd-MM-yyyy hh:mm a").getTime();
            long dtExamEnd = MixOperations.getDateFromStringDate(item.testDate + " " + item.testEndTime, "dd-MM-yyyy hh:mm a").getTime();

            AppLog.e(TAG , "dtExamStart time : "+dtExamStart+ " , "+currentTimeFromServer);
            if (currentTimeFromServer > dtExamStart && currentTimeFromServer < dtExamEnd && showStart)
            {
                btnStart.setVisibility(View.VISIBLE);
                btnStart.setBackground(getDrawable(R.drawable.start_button_bg));
                btnStart.setEnabled(true);
            }
            else
            {
                btnStart.setVisibility(View.VISIBLE);
                btnStart.setEnabled(false);
                btnStart.setBackground(getDrawable(R.drawable.start_button_bg_disabled));
            }


            dtExamStart = dtExamStart + (5 * 60000);

            AppLog.e(TAG, "dtExamStart time : " + dtExamStart + " , " + currentTimeFromServer);
            if (currentTimeFromServer > dtExamStart) {
                tvQPaperHide.setVisibility(View.GONE);
            } else {
                tvQPaperHide.setVisibility(View.VISIBLE);
            }
        } else {
            btnStart.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(item.testDate)) {
                long dtExamStart = MixOperations.getDateFromStringDate(item.testDate + " " + item.testStartTime, "dd-MM-yyyy hh:mm a").getTime();
                AppLog.e(TAG, "dtExamStart time : " + dtExamStart + " , " + currentTimeFromServer);

                if (currentTimeFromServer > dtExamStart) {
                    tvQPaperHide.setVisibility(View.GONE);
                } else {
                    tvQPaperHide.setVisibility(View.VISIBLE);
                }
            } else {
                tvQPaperHide.setVisibility(View.GONE);
            }
        }

        txt_title.setText(item.topicName);
        txt_teacher.setText(item.createdByName);
        if (!TextUtils.isEmpty(item.description)) {
            txtContent.setVisibility(View.VISIBLE);
            if (item.description.length() > 200) {
                StringBuilder stringBuilder = new StringBuilder(item.description);
                stringBuilder.setCharAt(197, '.');
                stringBuilder.setCharAt(198, '.');
                stringBuilder.setCharAt(199, '.');
                txtContent.setText(stringBuilder);
                txt_readmore.setVisibility(View.VISIBLE);
                txt_readmore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        txtContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(item.description.length())});
                        txtContent.setText(item.description);
                        txt_readmore.setVisibility(View.GONE);
                    }
                });
            } else {
                txtContent.setText(item.description);
                txt_readmore.setVisibility(View.GONE);
            }
        } else {
            txtContent.setVisibility(View.GONE);
            txt_readmore.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(item.testDate)) {
            String details = "Test/Exam Date : " + item.testDate + "\n"
                    + "Start Time : " + item.testStartTime + ", End Time : " + item.testEndTime + "\n";
            if (!TextUtils.isEmpty(item.lastSubmissionTime)) {
                details = details + "Last Submission Time : " + item.lastSubmissionTime;
            }
            txt_lastDate.setText(details);
            txt_lastDate.setVisibility(View.VISIBLE);
        } else {
            txt_lastDate.setVisibility(View.GONE);
        }

        constThumb.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(item.fileType)) {
            if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
                if (item.fileName != null) {

                    ChildAdapter adapter;
                    if (item.fileName.size() == 3) {
                        adapter = new ChildAdapter(2, item.fileName.size(), this, item.fileName);
                    } else {
                        adapter = new ChildAdapter(Constants.MAX_IMAGE_NUM, item.fileName.size(), this, item.fileName);
                    }
                    recyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(this, recyclerView, adapter));
                    recyclerView.setVisibility(View.VISIBLE);
                }
                imgPlay.setVisibility(View.GONE);
                imgPhoto.setVisibility(View.GONE);
            } else if (item.fileType.equals(Constants.FILE_TYPE_VIDEO)) {
                if (item.fileName != null) {
                    ChildVideoAdapter adapter;
                    if (item.fileName.size() == 3) {
                        adapter = new ChildVideoAdapter(2, this, item.fileName, item.thumbnailImage);
                    } else {
                        adapter = new ChildVideoAdapter(Constants.MAX_IMAGE_NUM, this, item.fileName, item.thumbnailImage);
                    }
                    recyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(this, recyclerView, adapter));
                    recyclerView.setVisibility(View.VISIBLE);
                }
                imgPlay.setVisibility(View.GONE);
                imgPhoto.setVisibility(View.GONE);
            } else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
                constThumb.setVisibility(View.VISIBLE);
                imgPhoto.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                imgPlay.setVisibility(View.GONE);
                if (item.thumbnailImage != null && item.thumbnailImage.size() > 0) {
                    Picasso.with(this).load(Constants.decodeUrlToBase64(item.thumbnailImage.get(0))).into(imageThumb);

                }
                if (item.fileName != null && item.fileName.size() > 0) {
                    if (AmazoneDownload.isPdfDownloaded(item.fileName.get(0))) {
                        imgDownloadPdf.setVisibility(View.GONE);
                    } else {
                        imgDownloadPdf.setVisibility(View.VISIBLE);
                    }
                }

            } else if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
                imgPhoto.getLayoutParams().height = (Constants.screen_width * 204) / 480;
                imgPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Picasso.with(this).load(item.thumbnail).into(imgPhoto);
                imgPhoto.setVisibility(View.VISIBLE);
                imgPlay.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                imgPhoto.setVisibility(View.GONE);
                imgPlay.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }
        } else {
            imgPhoto.setVisibility(View.GONE);
            imgPlay.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public void onPostClick(ChapterRes.TopicData item) {
        if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
            Intent browserIntent = new Intent(this, TestActivity.class);
            browserIntent.putExtra("url", item.video);
            startActivity(browserIntent);

        } else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
            Intent i = new Intent(this, ViewPDFActivity.class);
            i.putExtra("pdf", item.fileName.get(0));
            i.putExtra("name", item.topicName);
            startActivity(i);

        } else if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
            Intent i = new Intent(this, FullScreenActivity.class);
            i.putExtra("image", item.fileName);
            startActivity(i);
        }

    }

    public void onPostClick(TestPaperRes.TestPaperData item) {
        if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
            Intent browserIntent = new Intent(this, TestActivity.class);
            browserIntent.putExtra("url", item.video);
            startActivity(browserIntent);

        } else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
            Intent i = new Intent(this, ViewPDFActivity.class);
            i.putExtra("pdf", item.fileName.get(0));
            i.putExtra("name", item.studentName);
            startActivity(i);

        } else if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
            Intent i = new Intent(this, FullScreenActivity.class);
            i.putExtra("image", item.fileName);
            startActivity(i);
        }

    }

    private void submit() {
        Intent intent = new Intent(this, SubmitTestPaperActivity.class);
        intent.putExtra("group_id", GroupDashboardActivityNew.groupId);
        intent.putExtra("team_id", team_id);
        intent.putExtra("subject_id", subject_id);
        intent.putExtra("subject_name", subject_name);
        intent.putExtra("testExamId", this.item.testExamId);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void getAssignment(boolean isLoading) {
        if(isLoading)
            progressBar.setVisibility(View.VISIBLE);
        LeafManager leafManager = new LeafManager();
        leafManager.getTestPaper(this, GroupDashboardActivityNew.groupId, team_id, subject_id, item.testExamId, "");
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (LeafPreference.getInstance(this).getBoolean("is_paper_added")) {
            getAssignment(true);
            LeafPreference.getInstance(this).setBoolean("is_paper_added", false);
        }

        LocalBroadcastManager.getInstance(TestStudentActivity.this).registerReceiver(mMessageReceiver, new IntentFilter("PROCTORING_START"));
        LocalBroadcastManager.getInstance(TestStudentActivity.this).registerReceiver(mMessageReceiver, new IntentFilter("PROCTORING_END"));
        LocalBroadcastManager.getInstance(TestStudentActivity.this).registerReceiver(mMessageReceiver, new IntentFilter("PROCTORING_RESTART"));

    }

    @Override
    protected void onStop() {
        super.onStop();

       // LocalBroadcastManager.getInstance(TestStudentActivity.this).unregisterReceiver(mMessageReceiver);

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        progressBar.setVisibility(View.GONE);
        switch (apiId) {
            case LeafManager.API_TEST_EXAM_PAPER_LIST:
                TestPaperRes assignmentRes = (TestPaperRes) response;
                rvAssignment.setAdapter(new AssignmentAdapter(assignmentRes.getData()));
                LeafPreference.getInstance(this).remove(team_id + "_test_count_noti");
                saveToDB(assignmentRes.getData());
                break;
            case LeafManager.API_TEST_EXAM_PAPER_DELETE_STUDENT:
                getAssignment(true);
                break;
        }
    }

    private void saveToDB(ArrayList<TestPaperRes.TestPaperData> data) {
        showHideSubmitButton(data);

        StudTestPaperItem.deleteAll(item.testExamId, team_id, group_id);

        for (int i = 0; i < data.size(); i++) {
            TestPaperRes.TestPaperData currentItem = data.get(i);
            StudTestPaperItem item = new StudTestPaperItem();

            item.submittedById = currentItem.submittedById;
            item.testexamVerified = currentItem.testexamVerified;
            item.studentTestExamId = currentItem.studentTestExamId;
            item.studentName = currentItem.studentName;
            item.studentImage = currentItem.studentImage;
            item.description = currentItem.description;
            item.insertedAt = currentItem.insertedAt;
            item.fileType = currentItem.fileType;
            item.fileName = new Gson().toJson(currentItem.fileName);
            item.thumbnailImage = new Gson().toJson(currentItem.thumbnailImage);
            item.rollNumber = currentItem.rollNumber;
            item.studentDbId = currentItem.studentDbId;
            item.studentDbId = currentItem.studentDbId;
            item.submittedById = currentItem.submittedById;
            item.thumbnail = currentItem.thumbnail;
            item.userId = currentItem.userId;
            item.verifiedComment = currentItem.verifiedComment;
            item.video = currentItem.video;

            item.TestId = TestStudentActivity.this.item.testExamId;
            item.teamId = team_id;
            item.groupId = GroupDashboardActivityNew.groupId;
            item.save();
        }
    }

    CountDownTimer countDownTimer;

    private void showHideSubmitButton(ArrayList<TestPaperRes.TestPaperData> data) {
        long now = TrueTimeRx.now().getTime();
        //item.testDate = "24-08-2021";
        //item.testEndTime = "5:40 PM";

        if (data != null && data.size() > 0) {
            llSubmit.setVisibility(View.GONE);
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
        } else {
            if (!TextUtils.isEmpty(item.testDate)) {
                long dtExamStart = MixOperations.getDateFromStringDate(item.testDate + " " + item.testStartTime, "dd-MM-yyyy hh:mm a").getTime();
                long dtExamEnd = MixOperations.getDateFromStringDate(item.testDate + " " + item.testEndTime, "dd-MM-yyyy hh:mm a").getTime();
                long dtExamEndSubmitTime = MixOperations.getDateFromStringDate(item.testDate + " " + item.testEndTime, "dd-MM-yyyy hh:mm a").getTime();
                try {
                    dtExamEndSubmitTime = dtExamEndSubmitTime + (Integer.parseInt(item.lastSubmissionTime.replace("min", "") + "") * 60000);
                } catch (Exception e) {

                }

                if(now > dtExamStart && now < dtExamEndSubmitTime){
                    llSubmit.setVisibility(View.VISIBLE);
                }else {
                    llSubmit.setVisibility(View.GONE);
                }


                if (now > dtExamEnd && now < dtExamEndSubmitTime) {

                    tvQPaperHide.setVisibility(View.GONE);
                    btnStart.setEnabled(false);
                    btnStart.setBackground(getDrawable(R.drawable.start_button_bg_disabled));
                    long diffMillisec = dtExamEndSubmitTime - now;

                    if (countDownTimer != null)
                        countDownTimer.cancel();

                    countDownTimer = new CountDownTimer(diffMillisec, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long sec = millisUntilFinished / 1000;
                            if (sec > 60) {
                                long min = sec / 60;
                                sec = sec % 60;
                                tvTimer.setText(min + "m\n" + sec + "s");
                            } else {
                                tvTimer.setText(sec + "s");
                            }
                        }

                        @Override
                        public void onFinish() {
                            llSubmit.setVisibility(View.GONE);
                        }
                    };
                    countDownTimer.start();
                } else {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                }
            } else {
                tvTimer.setVisibility(View.GONE);
                llSubmit.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<AddPostValidationError> error) {
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

        if (error.status.equals("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            if (apiId == LeafManager.API_ID_DELETE_TEAM) {
                GroupValidationError groupValidationError = (GroupValidationError) error;
                Toast.makeText(this, groupValidationError.message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, error.title, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {
        ArrayList<TestPaperRes.TestPaperData> list;
        private Context mContext;

        public AssignmentAdapter(ArrayList<TestPaperRes.TestPaperData> data) {
            list = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_test_paper, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final TestPaperRes.TestPaperData item = list.get(position);
            holder.txtName.setText(item.studentName);
            holder.txtDate.setText(MixOperations.getFormattedDateOnly(item.insertedAt, Constants.DATE_FORMAT, "dd MMM yyyy\nhh:mm a"));

            holder.constThumb.setVisibility(View.GONE);
            final String name = item.studentName;
            if (!TextUtils.isEmpty(item.studentImage)) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.studentImage)).resize(dpToPx(), dpToPx()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgLead,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.imgLead_default.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.studentImage)).resize(dpToPx(), dpToPx()).into(holder.imgLead, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.imgLead_default.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.imgLead_default.setVisibility(View.VISIBLE);
                                        TextDrawable drawable = TextDrawable.builder()
                                                .buildRound(ImageUtil.getTextLetter(name), ImageUtil.getRandomColor(position));
                                        holder.imgLead_default.setImageDrawable(drawable);
                                        AppLog.e("Picasso", "Error : ");
                                    }
                                });
                            }
                        });
            } else {
                holder.imgLead_default.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(name), ImageUtil.getRandomColor(position));
                holder.imgLead_default.setImageDrawable(drawable);
            }
            if (!TextUtils.isEmpty(item.fileType)) {
                if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
                    if (item.fileName != null) {

                        ChildAdapter adapter;
                        if (item.fileName.size() == 3) {
                            adapter = new ChildAdapter(2, item.fileName.size(), mContext, item.fileName);
                        } else {
                            adapter = new ChildAdapter(Constants.MAX_IMAGE_NUM, item.fileName.size(), mContext, item.fileName);
                        }
                        holder.recyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(mContext, holder.recyclerView, adapter));
                        holder.recyclerView.setVisibility(View.VISIBLE);
                    }
                    holder.imgPlay.setVisibility(View.GONE);
                    holder.imgPhoto.setVisibility(View.GONE);
                } else if (item.fileType.equals(Constants.FILE_TYPE_VIDEO)) {
                    if (item.fileName != null) {
                        ChildVideoAdapter adapter;
                        if (item.fileName.size() == 3) {
                            adapter = new ChildVideoAdapter(2, mContext, item.fileName, item.thumbnailImage);
                        } else {
                            adapter = new ChildVideoAdapter(Constants.MAX_IMAGE_NUM, mContext, item.fileName, item.thumbnailImage);
                        }
                        holder.recyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(mContext, holder.recyclerView, adapter));
                        holder.recyclerView.setVisibility(View.VISIBLE);
                    }
                    holder.imgPlay.setVisibility(View.GONE);
                    holder.imgPhoto.setVisibility(View.GONE);
                } else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
                    holder.constThumb.setVisibility(View.VISIBLE);
                    holder.imgPhoto.setVisibility(View.GONE);
                    holder.recyclerView.setVisibility(View.GONE);
                    holder.imgPlay.setVisibility(View.GONE);
                    if (item.thumbnailImage != null && item.thumbnailImage.size() > 0) {
                        Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.thumbnailImage.get(0))).into(holder.imageThumb);

                    }
                    if (item.fileName != null && item.fileName.size() > 0) {
                        if (AmazoneDownload.isPdfDownloaded(item.fileName.get(0))) {
                            holder.imgDownloadPdf.setVisibility(View.GONE);
                        } else {
                            holder.imgDownloadPdf.setVisibility(View.VISIBLE);
                        }
                    }

                } else if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
                    holder.imgPhoto.getLayoutParams().height = (Constants.screen_width * 204) / 480;
                    holder.imgPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Picasso.with(mContext).load(item.thumbnail).into(holder.imgPhoto);
                    holder.imgPhoto.setVisibility(View.VISIBLE);
                    holder.imgPlay.setVisibility(View.VISIBLE);
                    holder.recyclerView.setVisibility(View.GONE);
                } else {
                    holder.imgPhoto.setVisibility(View.GONE);
                    holder.imgPlay.setVisibility(View.GONE);
                    holder.recyclerView.setVisibility(View.GONE);
                }
            } else {

                holder.imgPhoto.setVisibility(View.GONE);
                holder.imgPlay.setVisibility(View.GONE);
                holder.recyclerView.setVisibility(View.GONE);
            }

            if (!item.testexamVerified) {
                holder.txt_drop_delete.setVisibility(View.VISIBLE);
                holder.ivDelete.setVisibility(View.VISIBLE);
            } else {
                holder.txt_drop_delete.setVisibility(View.GONE);
                holder.ivDelete.setVisibility(View.GONE);
            }


            if (!item.testexamVerified) {
                holder.btnYes.setVisibility(View.GONE);
                holder.txt_NotVerify.setVisibility(View.VISIBLE);
                holder.txt_comments.setVisibility(View.GONE);
            } else {
                holder.btnYes.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(item.verifiedComment)) {
                    holder.txt_comments.setText("Comment :\n" + item.verifiedComment);
                    holder.txt_comments.setVisibility(View.VISIBLE);
                } else {
                    holder.txt_comments.setVisibility(View.GONE);
                }
            }

            if (!TextUtils.isEmpty(item.description)) {
                holder.txtContent.setVisibility(View.VISIBLE);
                if (item.description.length() > 200) {
                    StringBuilder stringBuilder = new StringBuilder(item.description);
                    stringBuilder.setCharAt(197, '.');
                    stringBuilder.setCharAt(198, '.');
                    stringBuilder.setCharAt(199, '.');
                    holder.txtContent.setText(stringBuilder);
                    holder.txt_readmore.setVisibility(View.VISIBLE);
                    holder.txt_readmore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.txtContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(item.description.length())});
                            holder.txtContent.setText(item.description);
                            holder.txt_readmore.setVisibility(View.GONE);
                        }
                    });
                } else {
                    holder.txtContent.setText(item.description);
                    holder.txt_readmore.setVisibility(View.GONE);
                }
            } else {
                holder.txtContent.setVisibility(View.GONE);
                holder.txt_readmore.setVisibility(View.GONE);
            }
        }

        private int dpToPx() {
            return mContext.getResources().getDimensionPixelSize(R.dimen.group_list_image_size);
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    txtEmpty.setText("No Test Paper found.");
                    txtEmpty.setVisibility(View.VISIBLE);
                } else {
                    txtEmpty.setText("");
                    txtEmpty.setVisibility(View.GONE);
                }
                return list.size();
            } else {
                txtEmpty.setText("No Test Paper found.");
                txtEmpty.setVisibility(View.VISIBLE);
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.txt_name)
            TextView txtName;

            @Bind(R.id.txt_content)
            TextView txtContent;

            @Bind(R.id.txt_readmore)
            TextView txt_readmore;

            @Bind(R.id.txt_drop_delete)
            TextView txt_drop_delete;

            @Bind(R.id.img_lead)
            CircleImageView imgLead;

            @Bind(R.id.img_lead_default)
            ImageView imgLead_default;

            @Bind(R.id.txt_date)
            TextView txtDate;

            @Bind(R.id.image)
            ImageView imgPhoto;

            @Bind(R.id.constThumb)
            ConstraintLayout constThumb;

            @Bind(R.id.imageThumb)
            ImageView imageThumb;

            @Bind(R.id.imageLoading)
            ImageView imageLoading;

            @Bind(R.id.img_play)
            ImageView imgPlay;

            @Bind(R.id.iv_delete)
            ImageView ivDelete;

            @Bind(R.id.rel)
            RelativeLayout rel;

            @Bind(R.id.recyclerView)
            AsymmetricRecyclerView recyclerView;

            @Bind(R.id.imgDownloadPdf)
            ImageView imgDownloadPdf;

            @Bind(R.id.lin_drop)
            LinearLayout lin_drop;

            @Bind(R.id.txt_comments)
            TextView txt_comments;

            @Bind(R.id.txt_NotVerify)
            TextView txt_NotVerify;

            @Bind(R.id.btnYes)
            FrameLayout btnYes;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (lin_drop.getVisibility() == View.VISIBLE)
                            lin_drop.setVisibility(View.GONE);
                    }
                });


            }

            @OnClick({R.id.rel, R.id.txt_readmore, R.id.iv_delete,
                    R.id.txt_drop_delete, R.id.txt_name})
            public void OnLikeClick(View v) {
                switch (v.getId()) {
                    case R.id.rel:
                        if (lin_drop.getVisibility() == View.VISIBLE)
                            lin_drop.setVisibility(View.GONE);
                        else if (isConnectionAvailable()) {
                            onPostClick(list.get(getAdapterPosition()));
                        } else {
                            showNoNetworkMsg();
                        }
                        break;
                    case R.id.iv_delete:
                        if (lin_drop.getVisibility() == View.VISIBLE)
                            lin_drop.setVisibility(View.GONE);
                        else
                            lin_drop.setVisibility(View.VISIBLE);
                        break;
                    case R.id.txt_drop_delete:
                        lin_drop.setVisibility(View.GONE);
                        if (isConnectionAvailable()) {
                            onDeleteClick(list.get(getAdapterPosition()));
                        } else {
                            showNoNetworkMsg();
                        }
                        break;
                }
            }
        }
    }

    private void onDeleteClick(TestPaperRes.TestPaperData item) {
        SMBDialogUtils.showSMBDialogOKCancel(this, "Are You Sure Want To Delete This Assignment?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBar.setVisibility(View.VISIBLE);
                LeafManager leafManager = new LeafManager();
                leafManager.deleteTestPaperStudent(TestStudentActivity.this, group_id, team_id, subject_id, TestStudentActivity.this.item.testExamId, item.studentTestExamId);
            }
        });
    }


    /***
     * ======================= ZOOM Related CDE ==========================
     */


    private void startMeeting()
    {
        AppLog.e(TAG , "StartMeeting Called "+isConnectionAvailable() );
        try {
            if (isConnectionAvailable()) {

                // STUDENT SIDE JOIN
                initializeZoom(videClassData.zoomKey, videClassData.zoomSecret, videClassData.zoomMail, videClassData.zoomMeetingPassword, videClassData.jitsiToken, videClassData.zoomName.get(0), videClassData.className, false);

            } else {
                showNoNetworkMsg();
            }
        } catch (Exception e) {
            AppLog.e(TAG , "initialize zoom crashed : "+e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private void initializeZoom(String zoomKey, String zoomSecret, String zoomMail, String zoomPassword, String meetingId, String zoomName, String className, boolean startOrJoin) {

        AppLog.e(TAG , "initializeZoom Called");

        progressBar.setVisibility(View.VISIBLE);
        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        zoomSDK.initialize(this, zoomKey, zoomSecret, new ZoomSDKInitializeListener() {
            @Override
            public void onZoomSDKInitializeResult(int i, int i1) {

                AppLog.e(TAG, "onZoomSDKInitializeResult : " + i + " , " + i1 + " , " + startOrJoin);

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
                AppLog.e(TAG , "onZoomAuthIdentityExpired Called");


            }



        });///APP_KEY , APP_SECRET


    }

    private void startZoomMeeting(String zoomMail, String password, String name, String className, String meetingId) {
//        ZoomSDK.getInstance().getMeetingSettingsHelper().setCustomizedMeetingUIEnabled(false);

        AppLog.e(TAG, "startzoommeeting called " + zoomMail + ", " + password + " , " + name + ", " + meetingId);

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

    ZoomSDKAuthenticationListener ZoomAuthLogoutListener = new ZoomSDKAuthenticationListener() {
        @Override
        public void onZoomSDKLoginResult(long result) {
            AppLog.e(TAG, "logoutZoomBeforeJoining , onZoomSDKLoginResult : " + result);
        }

        @Override
        public void onZoomSDKLogoutResult(long result) {
            AppLog.e(TAG, "logoutZoomBeforeJoining , onZOomSDKLogoutResult : " + result);

            ZoomSDK.getInstance().removeAuthenticationListener(this);
            joinZoomMeeting(videClassData.zoomName.get(0), videClassData.zoomMeetingPassword, videClassData.className, videClassData.jitsiToken);
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

        ZoomSDK.getInstance().getMeetingService().joinMeetingWithParams(this, params, opts);

    }

    MeetingServiceListener JoinMeetListener = new MeetingServiceListener() {
        @Override
        public void onMeetingStatusChanged(MeetingStatus meetingStatus, int errorCode, int internalErrorCode) {
            Log.e(TAG, "meetinsstatusChanged Join: " + meetingStatus.name() + " errorcode : " + errorCode + " internalError: " + internalErrorCode);
            if (meetingStatus.name().equalsIgnoreCase("MEETING_STATUS_CONNECTING")) {
                progressBar.setVisibility(View.GONE);
            }

        }
    };

    MeetingServiceListener StartMeetListener = new MeetingServiceListener() {
        @Override
        public void onMeetingStatusChanged(MeetingStatus meetingStatus, int errorCode, int internalErrorCode) {
            Log.e(TAG, "meetinsstatusChanged : " + meetingStatus.name() + " errorcode : " + errorCode + " internalError: " + internalErrorCode);

            if (meetingStatus.name().equalsIgnoreCase("MEETING_STATUS_CONNECTING")) {
                progressBar.setVisibility(View.GONE);

                /*if (getActivity() != null) {
                    ((VideoClassActivity) getActivity()).startBubbleService();
                }*/
            }

            if (meetingStatus.name().equalsIgnoreCase("MEETING_STATUS_DISCONNECTING")) {
                // Stop Meeting
            }
        }
    };

    ZoomSDKAuthenticationListener ZoomAuthListener = new ZoomSDKAuthenticationListener() {
        @Override
        public void onZoomSDKLoginResult(long result) {
            Log.e(TAG, "startmeeting , onZoomLogin Result : " + result);
            if (result == 0) {

                ZoomSDK.getInstance().removeAuthenticationListener(this);
                InstantMeetingOptions opts = new InstantMeetingOptions();
                opts.custom_meeting_id = videClassData.className;
                opts.meeting_views_options = MeetingViewsOptions.NO_TEXT_MEETING_ID;
                opts.no_invite = true;

                //opts.meeting_views_options = MeetingViewsOptions.NO_TEXT_MEETING_ID;

                ZoomSDK.getInstance().getMeetingService().startInstantMeeting(TestStudentActivity.this, opts);

                ZoomSDK.getInstance().getMeetingService().removeListener(StartMeetListener);
                ZoomSDK.getInstance().getMeetingService().removeListener(JoinMeetListener);
                ZoomSDK.getInstance().getMeetingService().addListener(StartMeetListener);
                ZoomSDK.getInstance().getInMeetingService().addListener(inMeetingListener);
            }

        }

        @Override
        public void onZoomSDKLogoutResult(long result) {
            AppLog.e(TAG, "startmeeting, onZOomSDKLogoutResult : " + result);

            ZoomSDK.getInstance().loginWithZoom(videClassData.zoomMail, videClassData.zoomPassword);
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

    public void requestOverlayPermission() {
        AppLog.e(TAG, "StartRecordingScreen called ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION);
        }

    }

    public void startBubbleService() {
        AppLog.e(TAG, "startBubbleService()");

        Intent uploadIntent2 = new Intent(this, FloatingWidgetExamService.class);
        uploadIntent2.putExtra("data", new Gson().toJson(item));
        bindService(uploadIntent2, mConnection, Context.BIND_AUTO_CREATE);

    }

    private FloatingWidgetExamService mService;

    private boolean mBound;
    /**
     * Defines callbacks for service binding, passed to bindService()
     */

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            FloatingWidgetExamService.LocalBinder binder = (FloatingWidgetExamService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            LocalBroadcastManager.getInstance(TestStudentActivity.this).registerReceiver(mMessageReceiver, new IntentFilter("recording"));
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    //(VideoClassActivity.this).registerReceiver(mMessageReceiver, new IntentFilter("intentKey"));

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent


            String message = intent.getStringExtra("action");
            AppLog.e(TAG, "onReceive called with action : " + message);
            AppLog.e(TAG, "onReceive called with action : " + intent.getStringExtra("data"));


            if (message.equalsIgnoreCase("start")) {
                // Navigate to QA Paper
                openpaper(intent.getStringExtra("data"));
            } else if (message.equalsIgnoreCase("PROCTORING_START")) {
                // update UI based on it
               /* if (item.proctoring) {
                    long dtExamStart = MixOperations.getDateFromStringDate(item.testDate + " " + item.testStartTime, "dd-MM-yyyy hh:mm a").getTime();
                    dtExamStart = dtExamStart + (5 * 60000);

                    AppLog.e(TAG, "dtExamStart time : " + dtExamStart + " , " + currentTimeFromServer);
                    if (currentTimeFromServer > dtExamStart) {
                        tvQPaperHide.setVisibility(View.GONE);
                    } else {
                        tvQPaperHide.setVisibility(View.VISIBLE);
                    }
                }
                showHideSubmitButton(null);*/
                finish();


            }
            else if (message.equalsIgnoreCase("PROCTORING_RESTART"))
            {
                // TO-DO
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnStart.performClick();
                            }
                        });
                    }
                }, 7000);

            }
            else if (message.equalsIgnoreCase("PROCTORING_END")) {

                if (item.proctoring) {
                    long dtExamStart = MixOperations.getDateFromStringDate(item.testDate + " " + item.testStartTime, "dd-MM-yyyy hh:mm a").getTime();
                    dtExamStart = dtExamStart + (5 * 60000);

                    AppLog.e(TAG, "dtExamStart time : " + dtExamStart + " , " + currentTimeFromServer);
                    if (currentTimeFromServer > dtExamStart) {
                        tvQPaperHide.setVisibility(View.GONE);
                    } else {
                        tvQPaperHide.setVisibility(View.VISIBLE);
                    }

                    long dtExamEnd = MixOperations.getDateFromStringDate(item.testDate + " " + item.testEndTime, "dd-MM-yyyy hh:mm a").getTime();

                    AppLog.e(TAG, "dtExamStart time : " + dtExamStart + " , " + currentTimeFromServer);
                    if (currentTimeFromServer > dtExamStart && currentTimeFromServer < dtExamEnd) {
                    } else {

                        btnStart.setEnabled(false);
                        btnStart.setBackground(getDrawable(R.drawable.start_button_bg_disabled));
                    }


                }

                showHideSubmitButton(null);
            }


        }
    };

    private void openpaper(String data) {

        TestExamRes.TestExamData item = new Gson().fromJson(data, TestExamRes.TestExamData.class);
        if (Constants.FILE_TYPE_PDF.equals(item.fileType)) {
            Intent i = new Intent(this, ViewPDFActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("pdf", item.fileName.get(0));
            i.putExtra("name", item.topicName);
            i.putExtra("from", "floatservice");
            startActivity(i);
        } else if (Constants.FILE_TYPE_IMAGE.equals(item.fileType)) {
            Intent i = new Intent(this, FullScreenMultiActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("image_list", item.fileName);
            i.putExtra("from", "floatservice");
            startActivity(i);
        }

    }

    public void removeBubble() {
        AppLog.e("BubbleService", "removeView Activity");
        if (mService != null) {
            mService.removeBubble();
            if (mBound) {
                try {
                    unbindService(mConnection);
                    AppLog.e("BubbleService", "unbindService Activity");
                } catch (Exception e) {
                    AppLog.e("BubbleService", "unbindService error is " + e.toString());
                }
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        removeBubble();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        AppLog.e(TAG, "onActivityReult called  : " + requestCode + " , " + resultCode);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

       LocalBroadcastManager.getInstance(TestStudentActivity.this).unregisterReceiver(mMessageReceiver);

    }

    private class TESTGOOGLE extends AsyncTask<String, String, Long>
    {


        @Override
        protected Long doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL("https://www.google.com");

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                AppLog.e(TAG, "responseCode :" + responseCode);

                DateFormat utcFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
                utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                DateFormat indianFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
                indianFormat .setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

                AppLog.e(TAG, "response  code date :"+new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss").parse(urlConnection.getHeaderField("Date")).getTime());
                return indianFormat.parse(indianFormat.format(utcFormat.parse(urlConnection.getHeaderField("Date")))).getTime();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                AppLog.e(TAG ,"Parse date : "+e.getLocalizedMessage());
                e.printStackTrace();
            }

            return Calendar.getInstance().getTimeInMillis();
        }


        @Override
        protected void onPostExecute(Long s) {
            super.onPostExecute(s);

            currentTimeFromServer = s;
            AppLog.e(TAG, "Date : " + currentTimeFromServer);

            showData();

            getDataLocally();
        }
    }
}
