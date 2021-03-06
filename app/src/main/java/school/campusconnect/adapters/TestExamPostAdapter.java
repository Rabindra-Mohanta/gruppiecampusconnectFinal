package school.campusconnect.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import school.campusconnect.Assymetric.AsymmetricRecyclerView;
import school.campusconnect.Assymetric.AsymmetricRecyclerViewAdapter;
import school.campusconnect.Assymetric.SpacesItemDecoration;
import school.campusconnect.Assymetric.Utils;
import school.campusconnect.R;
import school.campusconnect.datamodel.chapter.ChapterRes;
import school.campusconnect.datamodel.test_exam.TestExamRes;
import school.campusconnect.fragments.TestExamListFragment;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AmazoneVideoDownload;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.MixOperations;
import school.campusconnect.views.SMBDialogUtils;

public class TestExamPostAdapter extends RecyclerView.Adapter<TestExamPostAdapter.ImageViewHolder> {

    private static final String TAG = "PostAdapter";
    private boolean canEdit;
    private List<TestExamRes.TestExamData> list = Collections.emptyList();
    private OnItemClickListener listener;
    private Context mContext;
    TestExamRes.TestExamData item;

    public TestExamPostAdapter(ArrayList<TestExamRes.TestExamData> list, OnItemClickListener listener, boolean canEdit) {
        if (list == null) return;
        this.list = list;
        this.listener = listener;
        this.canEdit = canEdit;

        AppLog.e("PostAdapter", "Display width,height " + Constants.screen_width + "," + Constants.screen_height);
    }
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_test_exam_post, parent, false));
    }

    private int dpToPx() {
        return mContext.getResources().getDimensionPixelSize(R.dimen.group_list_image_size);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final TestExamRes.TestExamData item = list.get(position);

        holder.constThumb.setVisibility(View.GONE);

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
                    if (AmazoneDownload.isPdfDownloaded(mContext,item.fileName.get(0))) {
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
        holder.txt_drop_deletevideo.setVisibility(View.GONE);
        holder.viewDeleteVideo.setVisibility(View.GONE);
        holder.llMore.setVisibility(View.GONE);

        if (canEdit) {
            holder.llMore.setVisibility(View.VISIBLE);
            if (item.fileName != null && item.fileName.size() > 0) {
                if(new AmazoneVideoDownload(mContext).isVideoDownloaded(mContext,item.fileName.get(0)))
                {
                    holder.txt_drop_deletevideo.setVisibility(View.VISIBLE);
                    holder.viewDeleteVideo.setVisibility(View.VISIBLE);
                    holder.llMore.setVisibility(View.VISIBLE);
                }
            }
        } else {
            holder.llMore.setVisibility(View.GONE);
            if (item.fileName != null && item.fileName.size() > 0) {
                if(new AmazoneVideoDownload(mContext).isVideoDownloaded(mContext,item.fileName.get(0)))
                {
                    holder.txt_drop_deletevideo.setVisibility(View.VISIBLE);
                    holder.viewDeleteVideo.setVisibility(View.VISIBLE);
                    holder.llMore.setVisibility(View.VISIBLE);
                }
            }
        }

        holder.txt_title.setText(item.topicName);

        if (!TextUtils.isEmpty(item.description)) {
            holder.txtContent.setText(item.description);
            holder.txtContent.setVisibility(View.VISIBLE);
        } else {
            holder.txtContent.setVisibility(View.GONE);
            holder.txt_readmore.setVisibility(View.GONE);
        }
        holder.txt_createdBy.setText(item.createdByName);
        holder.txtDate.setText("Test Date : "+item.testDate);
    }


    @Override
    public int getItemCount() {
        if (list != null)
            return list.size();
        else
            return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clear() {
        if(list!=null){
            list.clear();
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.view)
        View view;

        @Bind(R.id.view1)
        View view1;

        @Bind(R.id.txt_que)
        TextView txt_que;

        @Bind(R.id.txt_drop_delete)
        TextView txt_drop_delete;

        @Bind(R.id.txt_drop_report)
        TextView txt_drop_report;
        @Bind(R.id.txt_drop_deletevideo)
        TextView txt_drop_deletevideo;
        @Bind(R.id.viewDeleteVideo)
        View viewDeleteVideo;

        @Bind(R.id.txt_name)
        TextView txtName;

        @Bind(R.id.txt_content)
        TextView txtContent;

        @Bind(R.id.txt_title)
        TextView txt_title;

        @Bind(R.id.txt_createdBy)
        TextView txt_createdBy;


        @Bind(R.id.txt_readmore)
        TextView txt_readmore;


        @Bind(R.id.img_lead)
        CircleImageView imgLead;

        @Bind(R.id.img_lead_default)
        ImageView imgLead_default;

        @Bind(R.id.txt_date)
        TextView txtDate;

        @Bind(R.id.txt_like)
        TextView txtLike;

        @Bind(R.id.img_like)
        ImageView ivLike;

        @Bind(R.id.txt_fav)
        ImageView txt_fav;

        @Bind(R.id.txt_comments)
        TextView txt_comments;

        @Bind(R.id.img_comments)
        ImageView img_comments;

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

        @Bind(R.id.lin_drop)
        LinearLayout lin_drop;

        @Bind(R.id.linComments)
        LinearLayout linComments;

        @Bind(R.id.linLikes)
        LinearLayout linLikes;

        @Bind(R.id.linPush)
        LinearLayout linPush;

        @Bind(R.id.recyclerView)
        AsymmetricRecyclerView recyclerView;

        @Bind(R.id.imgDownloadPdf)
        ImageView imgDownloadPdf;

        @Bind(R.id.llMore)
        LinearLayout llMore;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                }
            });
            recyclerView.setRequestedHorizontalSpacing(Utils.dpToPx(mContext, 3));
            recyclerView.addItemDecoration(
                    new SpacesItemDecoration(mContext.getResources().getDimensionPixelSize(R.dimen.padding_3dp)));
        }

        @OnClick({R.id.txt_like, R.id.txt_fav, R.id.rel, R.id.txt_readmore, R.id.iv_delete,
                R.id.txt_comments, R.id.txt_drop_delete, R.id.txt_drop_report, R.id.txt_drop_share,R.id.txt_drop_deletevideo,
                R.id.txt_que, R.id.txt_push, R.id.txt_name, R.id.txt_like_list, R.id.img_comments, R.id.img_like})
        public void OnLikeClick(View v) {
            item = list.get(getLayoutPosition());
            switch (v.getId()) {
                case R.id.iv_delete:
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else
                        lin_drop.setVisibility(View.VISIBLE);
                    break;
                case R.id.txt_drop_delete:
                    lin_drop.setVisibility(View.GONE);
                    if (isConnectionAvailable()) {
                        listener.onDeleteClick(item,getAdapterPosition());
                    } else {
                        showNoNetworkMsg();
                    }
                    break;
                case R.id.rel:
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else if (isConnectionAvailable()) {
                        listener.onPostClick(item);
                    } else {
                        showNoNetworkMsg();
                    }
                    break;
            }
        }
    }
    public interface OnItemClickListener {

        void onDeleteClick(TestExamRes.TestExamData item, int adapterPosition);

        void onPostClick(TestExamRes.TestExamData item);
    }

    public boolean isConnectionAvailable() {
        ConnectivityManager connectivityManager;
        NetworkInfo networkinfo;
        connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkinfo = connectivityManager.getActiveNetworkInfo();
        return (networkinfo != null && networkinfo.isConnected());

    }

    public void showNoNetworkMsg() {
        SMBDialogUtils.showSMBDialogOK((Activity) mContext, mContext.getString(R.string.no_internet), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }
}