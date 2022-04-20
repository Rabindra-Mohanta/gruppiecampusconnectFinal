package school.campusconnect.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import school.campusconnect.Assymetric.AsymmetricRecyclerView;
import school.campusconnect.Assymetric.AsymmetricRecyclerViewAdapter;
import school.campusconnect.R;
import school.campusconnect.adapters.ChildAdapter;
import school.campusconnect.adapters.ChildHwAdapter;
import school.campusconnect.adapters.ChildVideoAdapter;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.GroupValidationError;
import school.campusconnect.datamodel.chapter.ChapterRes;
import school.campusconnect.datamodel.gruppiecontacts.SendMsgToStudentReq;
import school.campusconnect.datamodel.homework.AssignmentRes;
import school.campusconnect.datamodel.homework.HwRes;
import school.campusconnect.datamodel.homework.ReassignReq;
import school.campusconnect.firebase.SendNotificationGlobal;
import school.campusconnect.firebase.SendNotificationModel;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.MixOperations;
import school.campusconnect.views.SMBDialogUtils;

public class HWParentActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<AddPostValidationError> {

    private static final String TAG = HWParentActivity.class.getSimpleName();
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
    @Bind(R.id.spStatus)
    public Spinner spStatus;
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
    @Bind(R.id.txt_date)
    TextView txt_date;
    @Bind(R.id.llNotSubmitted)
    LinearLayout llNotSubmitted;
    @Bind(R.id.et_msg)
    EditText et_msg;
    @Bind(R.id.btnSend)
    Button btnSend;
    @Bind(R.id.rel)
    RelativeLayout relMain;


    @Bind(R.id.iconBack)
    public ImageView iconBack;

    @Bind(R.id.imgHome)
    public ImageView imgHome;

    private String group_id;
    private String team_id;
    private String subject_id;
    private String subject_name;
    private String className;
    private HwRes.HwData item;
    private AssignmentRes.AssignmentData selectedAssignment;
    private ArrayList<AssignmentRes.AssignmentData> assignmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hw_parent);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);

        _init();

        String nTopic = item.topic.length() > 15 ? item.topic.substring(0, 15) : item.topic;
        setBackEnabled(false);
        tvTitle.setText(nTopic + " (" + className + ")");
        setTitle("");
        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HWParentActivity.this,GroupDashboardActivityNew.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        showData();

    }

    private void _init() {
        group_id = getIntent().getStringExtra("group_id");
        team_id = getIntent().getStringExtra("team_id");
        subject_id = getIntent().getStringExtra("subject_id");
        subject_name = getIntent().getStringExtra("subject_name");
        className = getIntent().getStringExtra("className");
        item = (HwRes.HwData) getIntent().getSerializableExtra("data");

        String[] strStatus = new String[3];
        strStatus[0] = "Not Verified";
        strStatus[1] = "Verified";
        strStatus[2] = "Not Submitted";
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_spinner, strStatus);
        spStatus.setAdapter(adapter);

        spStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AppLog.e(TAG, "onItemSelected : " + position);
                getAssignment();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(assignmentList==null){
                    return;
                }
                if (!TextUtils.isEmpty(et_msg.getText().toString())) {
                    progressBar.setVisibility(View.VISIBLE);
                    LeafManager leafManager = new LeafManager();
                    leafManager.sendMsgToNotSubmittedStudents(HWParentActivity.this, group_id, getStudentIds(), new SendMsgToStudentReq(et_msg.getText().toString()));
                } else {
                    Toast.makeText(HWParentActivity.this, "Please enter msg", Toast.LENGTH_SHORT).show();
                }
            }
        });

        relMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPostClick(item);
            }
        });
    }

    private String getStudentIds() {
        StringBuilder strIds = new StringBuilder();
        for (int i=0;i<assignmentList.size();i++){
            if(i==0){
                strIds.append(assignmentList.get(i).userId);
            }else {
                strIds.append(",").append(assignmentList.get(i).userId);
            }
        }
        return strIds.toString();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        showData();
    }

    private void showData() {
        constThumb.setVisibility(View.GONE);
        txt_title.setText(item.topic);
        txt_teacher.setText(item.createdByName);
        txt_date.setText(MixOperations.getFormattedDateOnly(item.postedAt, Constants.DATE_FORMAT, "dd MMM yyyy\nhh:mm a"));
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
        if (!TextUtils.isEmpty(item.lastSubmissionDate)) {
            txt_lastDate.setVisibility(View.VISIBLE);
            txt_lastDate.setText("Last Submission Date : " + item.lastSubmissionDate);
        } else {
            txt_lastDate.setVisibility(View.GONE);
            txt_lastDate.setText("");
        }
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

    public void onPostClick(HwRes.HwData item) {
        if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
            Intent browserIntent = new Intent(this, TestActivity.class);
            browserIntent.putExtra("url", item.video);
            startActivity(browserIntent);

        } else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
            Intent i = new Intent(this, ViewPDFActivity.class);
            i.putExtra("pdf", item.fileName.get(0));
            i.putExtra("thumbnail", item.thumbnailImage.get(0));
            i.putExtra("name", "");
            startActivity(i);

        } else if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
            Intent i = new Intent(this, FullScreenActivity.class);
            i.putExtra("image", item.fileName);
            startActivity(i);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 99 && resultCode == RESULT_OK) {
            notVerifyAssignmentFromActResult(data.getBooleanExtra("isVerify", false), data.getStringExtra("comments"), data.getStringArrayListExtra("_finalUrl"));
        }
    }

    public void onPostClick(AssignmentRes.AssignmentData item) {
        this.selectedAssignment = item;
        if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
            Intent browserIntent = new Intent(this, TestActivity.class);
            browserIntent.putExtra("url", item.video);
            startActivity(browserIntent);

        } else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
            Intent i = new Intent(this, ViewPDFActivity.class);
            i.putExtra("pdf", item.fileName.get(0));
            i.putExtra("thumbnail", item.thumbnailImage.get(0));
            i.putExtra("name", item.studentName);
            startActivity(i);

        } else if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
            if (item.fileName != null && item.fileName.size() > 0) {
                Intent i = new Intent(this, HomeWorkEditActivity.class);
                i.putExtra("item", new Gson().toJson(item));
                startActivityForResult(i, 99);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuDelete:
                deleteHwPost();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteHwPost() {
        SMBDialogUtils.showSMBDialogOKCancel(this, getResources().getString(R.string.smb_delete_home_work), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBar.setVisibility(View.VISIBLE);
                LeafManager leafManager = new LeafManager();
                leafManager.deleteAssignmentTeacher(HWParentActivity.this, group_id, team_id, subject_id, HWParentActivity.this.item.assignmentId);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public void getAssignment() {
        int pos = spStatus.getSelectedItemPosition();
        llNotSubmitted.setVisibility(View.GONE);
        String filter = "notVerified";
        if (pos == 0) {
            filter = "notVerified";
        } else if (pos == 1) {
            filter = "verified";
        } else {
            filter = "notSubmitted";
        }
        progressBar.setVisibility(View.VISIBLE);
        LeafManager leafManager = new LeafManager();
        leafManager.getAssignment(this, GroupDashboardActivityNew.groupId, team_id, subject_id, item.assignmentId, filter);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        progressBar.setVisibility(View.GONE);
        switch (apiId) {
            case LeafManager.API_ASSIGNMENT_LIST:
                AssignmentRes assignmentRes = (AssignmentRes) response;
                assignmentList=assignmentRes.getData();
                rvAssignment.setAdapter(new AssignmentAdapter(assignmentList));

                if(spStatus.getSelectedItemPosition()==2 && assignmentRes.getData()!=null && assignmentRes.getData().size()>0){
                    llNotSubmitted.setVisibility(View.VISIBLE);
                }
                break;
            case LeafManager.API_DELETE_ASSIGNMENT_TEACHER:
                LeafPreference.getInstance(HWParentActivity.this).setBoolean("is_hw_added", true);
                finish();
                break;
            case LeafManager.API_VERIFY_ASSIGNMENT:
            case LeafManager.API_REASSIGN_ASSIGNMENT:
                getAssignment();
                sendNotification();
                break;
            case LeafManager.API_SEND_MSG_TO_NOTSUBMITTED_STUDENT:
                et_msg.setText("");
                Toast.makeText(this, "Message send successfully", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void sendNotification() {
        SendNotificationModel notificationModel = new SendNotificationModel();
        notificationModel.to = "/topics/" + GroupDashboardActivityNew.groupId + "_" + team_id;
        notificationModel.data.title = getResources().getString(R.string.app_name);
        notificationModel.data.body = "";
        notificationModel.data.Notification_type = "ASSIGNMENT_STATUS";
        notificationModel.data.iSNotificationSilent = true;
        notificationModel.data.groupId = GroupDashboardActivityNew.groupId;
        notificationModel.data.teamId = team_id;
        notificationModel.data.createdById = LeafPreference.getInstance(this).getString(LeafPreference.LOGIN_ID);
        notificationModel.data.postId = "";
        notificationModel.data.postType = "";
        SendNotificationGlobal.send(notificationModel);
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
        ArrayList<AssignmentRes.AssignmentData> list;
        private Context mContext;

        public AssignmentAdapter(ArrayList<AssignmentRes.AssignmentData> data) {
            list = data;
        }

        @Override
        public AssignmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assignment, parent, false);
            return new AssignmentAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final AssignmentAdapter.ViewHolder holder, final int position) {
            final AssignmentRes.AssignmentData item = list.get(position);
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

                        ChildHwAdapter adapter;
                        if (item.fileName.size() == 3) {
                            adapter = new ChildHwAdapter(2, item.fileName.size(), mContext, item.fileName, HWParentActivity.this, item);
                        } else {
                            adapter = new ChildHwAdapter(Constants.MAX_IMAGE_NUM, item.fileName.size(), mContext, item.fileName, HWParentActivity.this, item);
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

            holder.btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyAssignment(item);
                }
            });
            holder.btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reAssignment(item);
                }
            });
           /* holder.txt_NotVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notVerifyAssignment(item);
                }
            });*/

            if (spStatus.getSelectedItemPosition() == 2) {
                holder.imgChat.setVisibility(View.VISIBLE);
                holder.llAction.setVisibility(View.GONE);
                holder.imgChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onChatClick(item);
                    }
                });
            } else {
                holder.imgChat.setVisibility(View.GONE);
                holder.llAction.setVisibility(View.VISIBLE);

                if (!item.assignmentVerified && !item.assignmentReassigned) {
                    holder.btnYes.setVisibility(View.GONE);
                    holder.btnNo.setVisibility(View.GONE);
//                    holder.txt_NotVerify.setVisibility(View.VISIBLE);
                    holder.txt_comments.setVisibility(View.GONE);
                } else {
//                    holder.txt_NotVerify.setVisibility(View.GONE);
                    if (item.assignmentVerified) {
                        holder.btnYes.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(item.verifiedComment)) {
                            holder.txt_comments.setText("Comment :\n" + item.verifiedComment);
                            holder.txt_comments.setVisibility(View.VISIBLE);
                        } else {
                            holder.txt_comments.setVisibility(View.GONE);
                        }
                    } else {
                        holder.btnYes.setVisibility(View.GONE);
                        if (item.assignmentReassigned) {
                            holder.btnNo.setVisibility(View.VISIBLE);
                            if (!TextUtils.isEmpty(item.reassignComment)) {
                                holder.txt_comments.setText("Comment :\n" + item.reassignComment);
                                holder.txt_comments.setVisibility(View.VISIBLE);
                            } else {
                                holder.txt_comments.setVisibility(View.GONE);
                            }
                        } else {
                            holder.txt_comments.setVisibility(View.GONE);
                            holder.btnNo.setBackgroundResource(R.drawable.assignement_no);
                        }
                    }
                }
            }

        }

        private int dpToPx() {
            return mContext.getResources().getDimensionPixelSize(R.dimen.group_list_image_size);
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    txtEmpty.setText("No Assignment found.");
                    txtEmpty.setVisibility(View.VISIBLE);
                } else {
                    txtEmpty.setText("");
                    txtEmpty.setVisibility(View.GONE);
                }
                return list.size();
            } else {
                txtEmpty.setText("No Assignment found.");
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

            @Bind(R.id.btnYes)
            FrameLayout btnYes;

            @Bind(R.id.btnNo)
            FrameLayout btnNo;

            @Bind(R.id.txt_NotVerify)
            TextView txt_NotVerify;

            @Bind(R.id.imgChat)
            ImageView imgChat;

            @Bind(R.id.llAction)
            FrameLayout llAction;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                ivDelete.setVisibility(View.GONE);
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
                }
            }
        }
    }

    private void onChatClick(AssignmentRes.AssignmentData item) {
        Intent intent = new Intent(this, AddPostActivity.class);
        intent.putExtra("id", GroupDashboardActivityNew.groupId);
        intent.putExtra("friend_id", item.userId);
        intent.putExtra("type", "personal");
        intent.putExtra("team_id", "");
        intent.putExtra("friend_name", item.studentName);
        intent.putExtra("from_chat", true);
        startActivity(intent);
    }

    /* private void notVerifyAssignment(AssignmentRes.AssignmentData item) {
         final Dialog dialog = new Dialog(this, R.style.AppTheme_AlertDialogStyle);
         dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
         dialog.setContentView(R.layout.dialog_add_comment);
         final EditText etTitle = dialog.findViewById(R.id.etTitle);
         final TextView tvComment = dialog.findViewById(R.id.tvComment);
         final CheckBox chkVerify = dialog.findViewById(R.id.chkVerify);
         final CheckBox chkReAssign = dialog.findViewById(R.id.chkReAssign);
         chkVerify.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 chkVerify.setChecked(true);
                 chkReAssign.setChecked(false);
                 etTitle.setVisibility(View.GONE);
                 tvComment.setVisibility(View.GONE);
             }
         });
         chkReAssign.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 chkVerify.setChecked(false);
                 chkReAssign.setChecked(true);
                 etTitle.setVisibility(View.VISIBLE);
                 tvComment.setVisibility(View.VISIBLE);
             }
         });

         chkReAssign.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 if (isChecked) {
                     etTitle.setVisibility(View.GONE);
                     tvComment.setVisibility(View.GONE);
                 } else {
                     etTitle.setVisibility(View.VISIBLE);
                     tvComment.setVisibility(View.VISIBLE);
                 }
             }
         });
         dialog.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (chkVerify.isChecked()) {
                     dialog.dismiss();
                     progressBar.setVisibility(View.VISIBLE);
                     LeafManager leafManager = new LeafManager();
                     leafManager.verifyAssignment(HWParentActivity.this, group_id, team_id, subject_id, HWParentActivity.this.item.assignmentId, item.studentAssignmentId, !item.assignmentVerified,new ReassignReq(etTitle.getText().toString()));
                 } else {
                     if (!TextUtils.isEmpty(etTitle.getText().toString().trim())) {
                         dialog.dismiss();
                         progressBar.setVisibility(View.VISIBLE);
                         LeafManager leafManager = new LeafManager();
                         leafManager.reassignAssignment(HWParentActivity.this, group_id, team_id, subject_id, HWParentActivity.this.item.assignmentId, item.studentAssignmentId, !item.assignmentReassigned, new ReassignReq(etTitle.getText().toString()));
                     } else {
                         Toast.makeText(HWParentActivity.this, "Please Add Comment", Toast.LENGTH_SHORT).show();
                     }
                 }
             }
         });
         dialog.show();
     }
 */
    private void notVerifyAssignmentFromActResult(boolean isVerify, String comments, String _finalUrl) {

        if (isVerify) {
            progressBar.setVisibility(View.VISIBLE);
            LeafManager leafManager = new LeafManager();
            ReassignReq reassignReq = new ReassignReq(comments);
            reassignReq.fileName = new ArrayList<>();
            reassignReq.fileName.add(_finalUrl);
            AppLog.e(TAG, "reassignReq :" + reassignReq);
            leafManager.verifyAssignment(HWParentActivity.this, group_id, team_id, subject_id, HWParentActivity.this.item.assignmentId, selectedAssignment.studentAssignmentId, true, reassignReq);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            LeafManager leafManager = new LeafManager();
            leafManager.reassignAssignment(HWParentActivity.this, group_id, team_id, subject_id, HWParentActivity.this.item.assignmentId, selectedAssignment.studentAssignmentId, true, new ReassignReq(comments));
        }
    }

    private void notVerifyAssignmentFromActResult(boolean isVerify, String comments, ArrayList<String> _finalUrl) {

        if (isVerify) {
            progressBar.setVisibility(View.VISIBLE);
            LeafManager leafManager = new LeafManager();
            ReassignReq reassignReq = new ReassignReq(comments);
            reassignReq.fileName = new ArrayList<>();
            reassignReq.fileName = _finalUrl;
            AppLog.e(TAG, "reassignReq :" + reassignReq);
            leafManager.verifyAssignment(HWParentActivity.this, group_id, team_id, subject_id, HWParentActivity.this.item.assignmentId, selectedAssignment.studentAssignmentId, true, reassignReq);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            LeafManager leafManager = new LeafManager();
            ReassignReq reassignReq = new ReassignReq(comments);
            reassignReq.fileName = new ArrayList<>();
            reassignReq.fileName = _finalUrl;
            AppLog.e(TAG, "reassignReq :" + reassignReq);
            leafManager.reassignAssignment(HWParentActivity.this, group_id, team_id, subject_id, HWParentActivity.this.item.assignmentId, selectedAssignment.studentAssignmentId, true, reassignReq);
        }
    }

    private void verifyAssignment(AssignmentRes.AssignmentData item) {
        String msg = "Are You Sure Want To un-verify " + item.studentName + " Assignment?";
        SMBDialogUtils.showSMBDialogOKCancel(this, msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBar.setVisibility(View.VISIBLE);
                LeafManager leafManager = new LeafManager();
                leafManager.verifyAssignment(HWParentActivity.this, group_id, team_id, subject_id, HWParentActivity.this.item.assignmentId, item.studentAssignmentId, !item.assignmentVerified, new ReassignReq("text"));
            }
        });
    }

    private void reAssignment(AssignmentRes.AssignmentData item) {
        SMBDialogUtils.showSMBDialogOKCancel(this, getResources().getString(R.string.smb_want_to_move) + item.studentName + getResources().getString(R.string.smb_assignment_not_verified), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBar.setVisibility(View.VISIBLE);
                LeafManager leafManager = new LeafManager();
                leafManager.reassignAssignment(HWParentActivity.this, group_id, team_id, subject_id, HWParentActivity.this.item.assignmentId, item.studentAssignmentId, !item.assignmentReassigned, new ReassignReq("text"));
            }
        });
    }
}
