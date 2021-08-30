package school.campusconnect.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
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
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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
import school.campusconnect.datamodel.StudAssignementItem;
import school.campusconnect.datamodel.chapter.ChapterRes;
import school.campusconnect.datamodel.homework.AssignmentRes;
import school.campusconnect.datamodel.homework.HwRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.MixOperations;
import school.campusconnect.views.SMBDialogUtils;

public class HWStudentActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<AddPostValidationError> {

    private static final String TAG = HWStudentActivity.class.getSimpleName();
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
    @Bind(R.id.txt_date)
    TextView txt_date;
    @Bind(R.id.btnSubmit)
    Button btnSubmit;


    @Bind(R.id.swipeRefreshLayout)
    PullRefreshLayout swipeRefreshLayout;

    private String group_id;
    private String team_id;
    private String subject_id;
    private String subject_name;
    private String className;
    private HwRes.HwData item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hw_student);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);

        _init();
        String nTopic = item.topic.length() > 15 ? item.topic.substring(0, 15) : item.topic;
        setTitle(nTopic + " (" + className + ")");

        showData();

        getDataLocally();

    }

    private void getDataLocally() {
        List<StudAssignementItem> list = StudAssignementItem.getAll(item.assignmentId, team_id, GroupDashboardActivityNew.groupId);
        if (list.size() != 0) {
            ArrayList<AssignmentRes.AssignmentData> result = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                StudAssignementItem currentItem = list.get(i);

                AssignmentRes.AssignmentData item = new AssignmentRes.AssignmentData();
                item.assignmentReassigned = currentItem.assignmentReassigned;
                item.assignmentVerified = currentItem.assignmentVerified;
                item.studentName = currentItem.studentName;
                item.description = currentItem.description;
                item.fileName = new Gson().fromJson(currentItem.fileName, new TypeToken<ArrayList<String>>() {
                }.getType());
                item.thumbnailImage = new Gson().fromJson(currentItem.thumbnailImage, new TypeToken<ArrayList<String>>() {
                }.getType());

                item.fileType = currentItem.fileType;
                item.insertedAt = currentItem.insertedAt;
                item.reassignComment = currentItem.reassignComment;
                item.reassignedAt = currentItem.reassignedAt;
                item.rollNumber = currentItem.rollNumber;
                item.studentAssignmentId = currentItem.studentAssignmentId;
                item.studentDbId = currentItem.studentDbId;
                item.studentDbId = currentItem.studentDbId;
                item.submittedById = currentItem.submittedById;
                item.thumbnail = currentItem.thumbnail;
                item.userId = currentItem.userId;
                item.verifiedComment = currentItem.verifiedComment;
                item.video = currentItem.video;

                result.add(item);

                if(i==list.size()-1 && currentItem.assignmentReassigned){
                    btnSubmit.setVisibility(View.VISIBLE);
                }
            }
            rvAssignment.setAdapter(new AssignmentAdapter(result));

            if (LeafPreference.getInstance(this).getInt(team_id + "_ass_count_noti") > 0) {

                LeafPreference.getInstance(this).setBoolean(team_id + "_ass_count_noti", false);
                getAssignment(false);
            }

        } else {
            getAssignment(true);
        }
    }


    private void _init() {
        group_id = getIntent().getStringExtra("group_id");
        team_id = getIntent().getStringExtra("team_id");
        subject_id = getIntent().getStringExtra("subject_id");
        subject_name = getIntent().getStringExtra("subject_name");
        className = getIntent().getStringExtra("className");
        item = (HwRes.HwData) getIntent().getSerializableExtra("data");

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
        btnSubmit.setVisibility(View.GONE);
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

    public void onPostClick(AssignmentRes.AssignmentData item) {
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
    private void submit(){
        Intent intent = new Intent(this, SubmitAssignmentActivity.class);
        intent.putExtra("group_id", GroupDashboardActivityNew.groupId);
        intent.putExtra("team_id", team_id);
        intent.putExtra("subject_id", subject_id);
        intent.putExtra("subject_name", subject_name);
        intent.putExtra("assignmentId", this.item.assignmentId);
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
        leafManager.getAssignment(this, GroupDashboardActivityNew.groupId, team_id, subject_id, item.assignmentId, "");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (LeafPreference.getInstance(this).getBoolean("is_assignment_added")) {
            getAssignment(false);
            LeafPreference.getInstance(this).setBoolean("is_assignment_added", false);
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        progressBar.setVisibility(View.GONE);
        switch (apiId) {
            case LeafManager.API_ASSIGNMENT_LIST:
                AssignmentRes assignmentRes = (AssignmentRes) response;
                rvAssignment.setAdapter(new AssignmentAdapter(assignmentRes.getData()));
                LeafPreference.getInstance(this).remove(team_id + "_ass_count_noti");
                saveToDB(assignmentRes.getData());
                break;
            case LeafManager.API_DELETE_ASSIGNMENT_STUDENT:
                getAssignment(true);
                break;
        }
    }

    private void saveToDB(ArrayList<AssignmentRes.AssignmentData> data) {
        btnSubmit.setVisibility(View.GONE);
        if (data == null || data.size()==0){
            btnSubmit.setVisibility(View.VISIBLE);
            return;
        }

        StudAssignementItem.deleteAll(item.assignmentId,team_id,group_id);

        for (int i = 0; i < data.size(); i++) {
            AssignmentRes.AssignmentData currentItem = data.get(i);
            StudAssignementItem item = new StudAssignementItem();

            item.description = currentItem.description;
            item.fileName = new Gson().toJson(currentItem.fileName);
            item.thumbnailImage = new Gson().toJson(currentItem.thumbnailImage);

            item.assignmentReassigned = currentItem.assignmentReassigned;
            item.assignmentVerified = currentItem.assignmentVerified;
            item.studentName = currentItem.studentName;
            item.description = currentItem.description;

            item.fileType = currentItem.fileType;
            item.insertedAt = currentItem.insertedAt;
            item.reassignComment = currentItem.reassignComment;
            item.reassignedAt = currentItem.reassignedAt;
            item.rollNumber = currentItem.rollNumber;
            item.studentAssignmentId = currentItem.studentAssignmentId;
            item.studentDbId = currentItem.studentDbId;
            item.studentDbId = currentItem.studentDbId;
            item.submittedById = currentItem.submittedById;
            item.thumbnail = currentItem.thumbnail;
            item.userId = currentItem.userId;
            item.verifiedComment = currentItem.verifiedComment;
            item.video = currentItem.video;

            item.AssignId = this.item.assignmentId;
            item.teamId = team_id;
            item.groupId = GroupDashboardActivityNew.groupId;
            item.save();

            if(i==data.size()-1 && currentItem.assignmentReassigned){
                btnSubmit.setVisibility(View.VISIBLE);
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

            if (!item.assignmentVerified && !item.assignmentReassigned) {
                holder.txt_drop_delete.setVisibility(View.VISIBLE);
                holder.ivDelete.setVisibility(View.VISIBLE);
            } else {
                holder.txt_drop_delete.setVisibility(View.GONE);
                holder.ivDelete.setVisibility(View.GONE);
            }


            if (!item.assignmentVerified && !item.assignmentReassigned) {
                holder.btnYes.setVisibility(View.GONE);
                holder.btnNo.setVisibility(View.GONE);
                holder.txt_NotVerify.setVisibility(View.VISIBLE);
                holder.txt_comments.setVisibility(View.GONE);
            } else {
                holder.txt_NotVerify.setVisibility(View.GONE);
                if (item.assignmentVerified) {
                    holder.btnYes.setVisibility(View.VISIBLE);
                    if(!TextUtils.isEmpty(item.verifiedComment)){
                        holder.txt_comments.setText("Comment :\n" + item.verifiedComment);
                        holder.txt_comments.setVisibility(View.VISIBLE);
                    }else {
                        holder.txt_comments.setVisibility(View.GONE);
                    }
                } else {
                    holder.btnYes.setVisibility(View.GONE);
                    if (item.assignmentReassigned) {
                        holder.btnNo.setVisibility(View.VISIBLE);
                        if(!TextUtils.isEmpty(item.reassignComment)){
                            holder.txt_comments.setText("Comment :\n" + item.reassignComment);
                            holder.txt_comments.setVisibility(View.VISIBLE);
                        }else {
                            holder.txt_comments.setVisibility(View.GONE);
                        }
                    } else {
                        holder.txt_comments.setVisibility(View.GONE);
                        holder.btnNo.setBackgroundResource(R.drawable.assignement_no);
                    }
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

            @Bind(R.id.txt_NotVerify)
            TextView txt_NotVerify;

            @Bind(R.id.btnYes)
            FrameLayout btnYes;

            @Bind(R.id.btnNo)
            FrameLayout btnNo;

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

    private void onDeleteClick(AssignmentRes.AssignmentData item) {
        SMBDialogUtils.showSMBDialogOKCancel(this, "Are You Sure Want To Delete This Assignment?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBar.setVisibility(View.VISIBLE);
                LeafManager leafManager = new LeafManager();
                leafManager.deleteAssignmentStudent(HWStudentActivity.this, group_id, team_id, subject_id, HWStudentActivity.this.item.assignmentId, item.studentAssignmentId);
            }
        });
    }
}
