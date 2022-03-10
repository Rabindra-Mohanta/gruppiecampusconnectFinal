package school.campusconnect.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.jaygoo.widget.OnRangeChangedListener;


import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

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
import school.campusconnect.utils.AmazoneAudioDownload;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AmazoneVideoDownload;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.MixOperations;
import school.campusconnect.views.SMBDialogUtils;

public class TopicPostAdapter extends RecyclerView.Adapter<TopicPostAdapter.ImageViewHolder> {

    private static final String TAG = "PostAdapter";
    private boolean canEdit;
    private List<ChapterRes.TopicData> list = Collections.emptyList();
    private OnItemClickListener listener;
    private Context mContext;
    ChapterRes.TopicData item;
    MediaPlayer mediaPlayer  = new MediaPlayer();;
    private Handler mHandler = new Handler();
    Runnable myRunnable;
    int pos;

    AmazoneAudioDownload asyncTask;

    public TopicPostAdapter(List<ChapterRes.TopicData> list, OnItemClickListener listener,boolean canEdit) {
        if (list == null) return;
        this.list = list;
        this.listener = listener;
        this.canEdit = canEdit;

        AppLog.e("PostAdapter", "Display width,height " + Constants.screen_width + "," + Constants.screen_height);
    }
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic_post, parent, false));
    }

    private int dpToPx() {
        return mContext.getResources().getDimensionPixelSize(R.dimen.group_list_image_size);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final ChapterRes.TopicData item = list.get(position);

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
                holder.llAudio.setVisibility(View.GONE);
            }
            else if (item.fileType.equals(Constants.FILE_TYPE_VIDEO)) {
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
                holder.llAudio.setVisibility(View.GONE);
            }
            else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
                holder.constThumb.setVisibility(View.VISIBLE);
                holder.imgPhoto.setVisibility(View.GONE);
                holder.recyclerView.setVisibility(View.GONE);
                holder.imgPlay.setVisibility(View.GONE);
                holder.llAudio.setVisibility(View.GONE);
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

            }
            else if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
                holder.imgPhoto.getLayoutParams().height = (Constants.screen_width * 204) / 480;
                holder.imgPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Picasso.with(mContext).load(item.thumbnail).into(holder.imgPhoto);
                holder.imgPhoto.setVisibility(View.VISIBLE);
                holder.llAudio.setVisibility(View.GONE);
                holder.imgPlay.setVisibility(View.VISIBLE);
                holder.recyclerView.setVisibility(View.GONE);
            }
            else if (item.fileType.equals(Constants.FILE_TYPE_AUDIO))
            {
                holder.llAudio.setVisibility(View.VISIBLE);
                holder.imgPhoto.setVisibility(View.GONE);
                holder.imgPlay.setVisibility(View.GONE);
                holder.recyclerView.setVisibility(View.GONE);

                if (AmazoneAudioDownload.isAudioDownloaded(item.fileName.get(0)))
                {
                    holder.imgPlayAudio.setVisibility(View.VISIBLE);
                    holder.imgDownloadAudio.setVisibility(View.GONE);
                    holder.imgPauseAudio.setVisibility(View.GONE);
                    holder.llProgress.setVisibility(View.GONE);
                }
                else
                {
                    holder.imgDownloadAudio.setVisibility(View.VISIBLE);
                    holder.imgPlayAudio.setVisibility(View.GONE);
                    holder.imgPauseAudio.setVisibility(View.GONE);
                    holder.llProgress.setVisibility(View.GONE);
                }
            }
            else {
                holder.llAudio.setVisibility(View.GONE);
                holder.imgPhoto.setVisibility(View.GONE);
                holder.imgPlay.setVisibility(View.GONE);
                holder.recyclerView.setVisibility(View.GONE);
            }
        } else {
            holder.llAudio.setVisibility(View.GONE);
            holder.imgPhoto.setVisibility(View.GONE);
            holder.imgPlay.setVisibility(View.GONE);
            holder.recyclerView.setVisibility(View.GONE);
        }

        holder.chkCompleted.setChecked(item.topicCompleted);

        holder.txt_drop_deletevideo.setVisibility(View.GONE);
        holder.viewDeleteVideo.setVisibility(View.GONE);
        holder.llMore.setVisibility(View.GONE);

        if (canEdit) {
            holder.llMore.setVisibility(View.VISIBLE);
            holder.chkCompleted.setVisibility(View.GONE);
            if (item.fileName != null && item.fileName.size() > 0) {
                if(new AmazoneVideoDownload(mContext).isVideoDownloaded(item.fileName.get(0)))
                {
                    holder.txt_drop_deletevideo.setVisibility(View.VISIBLE);
                    holder.viewDeleteVideo.setVisibility(View.VISIBLE);
                    holder.llMore.setVisibility(View.VISIBLE);
                }
            }
        } else {
            holder.chkCompleted.setVisibility(View.VISIBLE);
            holder.chkCompleted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.topicCompleted = holder.chkCompleted.isChecked();
                    listener.onCompleteClick(item,position);
                }
            });

            holder.llMore.setVisibility(View.GONE);
            if (item.fileName != null && item.fileName.size() > 0) {
                if(new AmazoneVideoDownload(mContext).isVideoDownloaded(item.fileName.get(0)))
                {
                    holder.llMore.setVisibility(View.VISIBLE);
                    holder.txt_drop_deletevideo.setVisibility(View.VISIBLE);

                    holder.viewDeleteVideo.setVisibility(View.GONE);
                    holder.txt_drop_delete.setVisibility(View.GONE);
                    holder.txt_drop_share.setVisibility(View.GONE);
                }
            }
        }

        holder.txt_title.setText(item.topicName);
        holder.txt_createdBy.setText(item.createdByName);
        holder.txtDate.setText(MixOperations.getFormattedDate(item.insertedAt, Constants.DATE_FORMAT));

        holder.imgDownloadAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.llProgress.setVisibility(View.VISIBLE);
                startProcess(item.fileName.get(0),holder);

            }
        });
        holder.imgCancelDownloadAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncTask.cancel(true);
            }
        });

        holder.imgPlayAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{

                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(Constants.decodeUrlToBase64(item.fileName.get(0)));
                    mediaPlayer.prepare();
                    mediaPlayer.start();


                    holder.imgPauseAudio.setVisibility(View.VISIBLE);
                    holder.imgPlayAudio.setVisibility(View.GONE);

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            notifyItemChanged(position);
                        }
                    });

                    ((AppCompatActivity) mContext).runOnUiThread(myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                            Log.e(TAG,"mCurrentPosition"+ mCurrentPosition);
                            holder.tvTimeAudio.setText(formatDate(mCurrentPosition));
                            holder.seekBarAudio.setProgress(mCurrentPosition*10);
                            mHandler.postDelayed(myRunnable, 1000);
                        }
                    });
                    Log.e(TAG,"media Player data"+mediaPlayer.getCurrentPosition()+"\n"+mediaPlayer.getDuration());
                }catch(Exception e){e.printStackTrace();
                    Log.e(TAG,"Exception"+e.getMessage());}
            }
        });

        holder.imgPauseAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    mHandler.removeCallbacks(myRunnable);
                    mediaPlayer.pause();
                    holder.imgPauseAudio.setVisibility(View.GONE);
                    holder.imgPlayAudio.setVisibility(View.VISIBLE);
                }catch(Exception e){
                    e.printStackTrace();
                    Log.e(TAG,"Exception"+e.getMessage());
                }
            }
        });

        holder.seekBarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);//if user drags the seekbar, it gets the position and updates in textView.
                }
                Log.e(TAG,"progress"+progress);

                if (progress == 100)
                {
                    mHandler.removeCallbacks(myRunnable);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



    }

    private void startProcess(String s, ImageViewHolder holder) {
        if (isConnectionAvailable()) {
            asyncTask = AmazoneAudioDownload.download(mContext, s, new AmazoneAudioDownload.AmazoneDownloadSingleListener() {
                @Override
                public void onDownload(File file) {
                    notifyItemChanged(holder.getAdapterPosition());
                }

                @Override
                public void error(String msg) {

                }

                @Override
                public void progressUpdate(int progress, int max) {
                 /*   if(progress>0){
                        holder.progressBarAudioDownload.setVisibility(View.GONE);
                    }*/
                    holder.progressBarAudioDownload.setProgress(progress);

                }
            });
        } else {
            showNoNetworkMsg();
        }
    }

    private String formatDate(int second) /* This is your topStory.getTime()*1000 */ {
      /*  DateFormat sdf = new SimpleDateFormat("mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(second);
        TimeZone tz = TimeZone.getDefault();
        sdf.setTimeZone(tz);*/
        String seconds , minutes;
        if(second>60)
        {
            if(second % 60 < 10)
            seconds = "0"+(second % 60);
            else
                seconds = ""+(second%60);

            if(second/60 < 10)
            minutes = "0"+second/60;
            else
                minutes = ""+second/60;
        }
        else
        {
            minutes = "00";
            if(second % 60 < 10)
                seconds = "0"+(second % 60);
            else
                seconds = ""+(second%60);
        }



        return minutes+":"+seconds;
    //    return sdf.format(calendar.getTime());
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
        @Bind(R.id.txt_drop_share)
        TextView txt_drop_share;
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

        @Bind(R.id.chkCompleted)
        SwitchCompat chkCompleted;

        /*Audio View*/

        @Bind(R.id.llAudio)
        RelativeLayout llAudio;

        @Bind(R.id.imgDownloadAudio)
        ImageView imgDownloadAudio;

        @Bind(R.id.imgPlayAudio)
        ImageView imgPlayAudio;

        @Bind(R.id.imgPauseAudio)
        ImageView imgPauseAudio;

        @Bind(R.id.llProgress)
        FrameLayout llProgress;

        @Bind(R.id.progressBarAudioDownload)
        ProgressBar progressBarAudioDownload;

        @Bind(R.id.imgCancelDownloadAudio)
        ImageView imgCancelDownloadAudio;

        @Bind(R.id.tvTimeAudio)
        TextView tvTimeAudio;

        @Bind(R.id.seekBarAudio)
        SeekBar seekBarAudio;


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
                case R.id.txt_drop_share:
                    lin_drop.setVisibility(View.GONE);
                    if (isConnectionAvailable()) {
                        listener.onCompletedStudentClick(item,getAdapterPosition());
                    } else {
                        showNoNetworkMsg();
                    }
                    break;
                case R.id.txt_drop_deletevideo:
                    lin_drop.setVisibility(View.GONE);
                    if (isConnectionAvailable()) {
                        listener.onDeleteVideoClick(item , getAdapterPosition());
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

    public List<ChapterRes.TopicData> getList() {
        return list;
    }

    public interface OnItemClickListener {

        void onDeleteClick(ChapterRes.TopicData item, int adapterPosition);

        void onCompleteClick(ChapterRes.TopicData item, int adapterPosition);

        void onCompletedStudentClick(ChapterRes.TopicData item, int adapterPosition);

        void onPostClick(ChapterRes.TopicData item);

        void onDeleteVideoClick(ChapterRes.TopicData item, int adapterPosition);
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