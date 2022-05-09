package school.campusconnect.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.TextUtils;

import school.campusconnect.Assymetric.AsymmetricRecyclerView;
import school.campusconnect.Assymetric.AsymmetricRecyclerViewAdapter;
import school.campusconnect.Assymetric.SpacesItemDecoration;
import school.campusconnect.Assymetric.Utils;
import school.campusconnect.BuildConfig;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.ReadUnreadPostUsers;
import school.campusconnect.datamodel.PostItem;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.datamodel.teamdiscussion.TeamPostGetData;
import school.campusconnect.utils.AmazoneAudioDownload;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AmazoneImageDownload;
import school.campusconnect.utils.AmazoneVideoDownload;
import school.campusconnect.utils.AppLog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.activities.CommentsActivity;
import school.campusconnect.activities.ReadMoreActivity;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.MixOperations;
import school.campusconnect.views.SMBDialogUtils;

/**
 * Created by frenzin04 on 1/12/2017.
 */
public class TeamListAdapter extends RecyclerView.Adapter<TeamListAdapter.ImageViewHolder> {

    private static final String TAG = "TeamListAdapter";
    private MyTeamData myTeamData;
    private List<TeamPostGetData> list = Collections.emptyList();
    private OnItemClickListener listener;
    private Context mContext;
    String type;
    TeamPostGetData item;
    String mGroupId;
    String mTeamId;
    int count;
    DatabaseHandler databaseHandler;

    public TeamListAdapter(List<TeamPostGetData> list, OnItemClickListener listener, String type, String id, String team_id, int count, DatabaseHandler databaseHandler, MyTeamData myTeamData) {
        if (list == null) return;
        this.list = list;
        this.listener = listener;
        this.type = type;
        this.mGroupId = id;
        this.mTeamId = team_id;
        this.count = count;
        this.databaseHandler = databaseHandler;
        this.myTeamData = myTeamData;
        AppLog.e(TAG, "team id is " + team_id);
        AppLog.e(TAG, "count : " + count);
        AppLog.e(TAG, "Display width,height " + Constants.screen_width + "," + Constants.screen_height);
    }

    MediaPlayer mediaPlayer  = new MediaPlayer();
    private Handler mHandler = new Handler();
    ImageViewHolder mholder;

    Runnable myRunnable = new Runnable() {
        @Override
        public void run() {

            try{
                int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                Log.e(TAG,"mCurrentPosition"+ mCurrentPosition);
                mholder.tvTimeAudio.setText(formatDate(mCurrentPosition));
                mholder.tvTimeTotalAudio.setText(formatDate(mediaPlayer.getDuration()/1000));
                mholder.seekBarAudio.setProgress(mCurrentPosition);
                if(mediaPlayer.isPlaying())
                    mHandler.postDelayed(myRunnable, 1000);
            }catch (Exception e)
            {
                Log.e(TAG,"exception"+ e.getMessage());
            }

        }
    };
    int pos = -1;


    AmazoneAudioDownload asyncTask;

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        new DatabaseHandler(mContext);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_post, parent, false);
        return new ImageViewHolder(view);
    }

    private int dpToPx() {
        return mContext.getResources().getDimensionPixelSize(R.dimen.group_list_image_size);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ImageViewHolder holder) {
        Log.e(TAG, "onDetachedFromRecyclerView: .");

        if (mholder == holder)
        {
            mHandler.removeCallbacks(myRunnable);

            if (mediaPlayer.isPlaying())
            {
                mediaPlayer.pause();
                pos = -1;
                holder.seekBarAudio.setProgress(0);
                holder.tvTimeAudio.setText("00:00");
                holder.tvTimeTotalAudio.setText("00:00");
                holder.imgPauseAudio.setVisibility(View.GONE);
                holder.imgPlayAudio.setVisibility(View.VISIBLE);
            }
            else
            {
                pos = -1;
                holder.seekBarAudio.setProgress(0);
                holder.tvTimeAudio.setText("00:00");
                holder.tvTimeTotalAudio.setText("00:00");
                holder.imgPauseAudio.setVisibility(View.GONE);
                holder.imgPlayAudio.setVisibility(View.VISIBLE);
            }

        }

        super.onViewDetachedFromWindow(holder);
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


    private String formatDate(int second)  {

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
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final TeamPostGetData item = list.get(position);
        AppLog.e(TAG, "item[" + position + "] : " + item);
        AppLog.e(TAG, "Group Id : " + mGroupId);

        String dispName = item.createdBy;
        if (count != 0) {
            try {
                String name = databaseHandler.getNameFromNum(item.phone.replaceAll(" ", ""));
                if (!TextUtils.isEmpty(name)) {
                    dispName = name;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        holder.txtName.setText(dispName);

        holder.txtDate.setText(MixOperations.getFormattedDate(item.createdAt, Constants.DATE_FORMAT));
        holder.txtLike.setText(Constants.coolFormat(item.likes, 0));
        holder.txt_comments.setText(Constants.coolFormat(item.comments, 0) + "");


        if (item.isLiked) {
            Picasso.with(mContext).load(R.drawable.icon_post_liked).into(holder.ivLike);
        } else {
            Picasso.with(mContext).load(R.drawable.icon_post_like).into(holder.ivLike);
        }

        if (item.isFavourited) {
            Picasso.with(mContext).load(R.drawable.icon_post_favd).into(holder.txt_fav);
        } else {
            Picasso.with(mContext).load(R.drawable.icon_post_fav).into(holder.txt_fav);
        }
        holder.constThumb.setVisibility(View.GONE);
        final String name = dispName;
        if (!TextUtils.isEmpty(item.createdByImage)) {
            Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.createdByImage)).resize(dpToPx(), dpToPx()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgLead,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.imgLead_default.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError() {
                            Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.createdByImage)).resize(dpToPx(), dpToPx()).into(holder.imgLead, new Callback() {
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


        if (!TextUtils.isEmpty(item.fileType) && !item.fileType.equalsIgnoreCase("birthdaypost"))
        {
            if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
                if (item.fileName != null) {
               /* int height=0;
                if (item.imageHeight != 0 && item.imageWidth != 0)
                    height = (Constants.screen_width * item.imageHeight) / item.imageWidth;*/
                    ChildAdapter adapter;
                    if (item.fileName.size() == 3) {
                        adapter = new ChildAdapter(2, item.fileName.size(), mContext, item.fileName);
                    } else {
                        adapter = new ChildAdapter(Constants.MAX_IMAGE_NUM, item.fileName.size(), mContext, item.fileName);
                    }
                    holder.recyclerView.setVisibility(View.VISIBLE);
                    holder.recyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(mContext, holder.recyclerView, adapter));
                }
                holder.imgPlay.setVisibility(View.GONE);
                holder.imgPhoto.setVisibility(View.GONE);
                holder.llAudio.setVisibility(View.GONE);
            } else if (item.fileType.equals(Constants.FILE_TYPE_VIDEO)) {
                if (item.fileName != null) {
               /* int height=0;
                if (item.imageHeight != 0 && item.imageWidth != 0)
                    height = (Constants.screen_width * item.imageHeight) / item.imageWidth;*/

                    ChildVideoAdapter adapter;
                    if (item.fileName.size() == 3) {
                        adapter = new ChildVideoAdapter(2,  mContext, item.fileName,item.thumbnailImage);
                    } else {
                        adapter = new ChildVideoAdapter(Constants.MAX_IMAGE_NUM, mContext, item.fileName,item.thumbnailImage);
                    }
                    holder.recyclerView.setVisibility(View.VISIBLE);
                    holder.recyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(mContext, holder.recyclerView, adapter));
                }
                holder.imgPlay.setVisibility(View.GONE);
                holder.llAudio.setVisibility(View.GONE);
                holder.imgPhoto.setVisibility(View.GONE);
            }
            else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
                holder.constThumb.setVisibility(View.VISIBLE);
                holder.imgPhoto.setVisibility(View.GONE);
                holder.recyclerView.setVisibility(View.GONE);
                holder.imgPlay.setVisibility(View.GONE);
                holder.llAudio.setVisibility(View.GONE);
                if(item.thumbnailImage!=null && item.thumbnailImage.size()>0)
                {
                    Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.thumbnailImage.get(0))).into(holder.imageThumb);
                }
                if (item.fileName != null && item.fileName.size() > 0) {
                    if (AmazoneDownload.isPdfDownloaded(item.fileName.get(0))) {
                        holder.imgDownloadPdf.setVisibility(View.GONE);
                    } else {
                        holder.imgDownloadPdf.setVisibility(View.VISIBLE);
                    }
                }
            } else if (item.fileType.equals(Constants.FILE_TYPE_AUDIO))
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
            } else if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
                holder.imgPhoto.getLayoutParams().height = (Constants.screen_width * 204) / 480;
                holder.imgPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Picasso.with(mContext).load(item.thumbnail).into(holder.imgPhoto);
                holder.imgPhoto.setVisibility(View.VISIBLE);
                holder.imgPlay.setVisibility(View.VISIBLE);
                holder.llAudio.setVisibility(View.GONE);
                holder.recyclerView.setVisibility(View.GONE);
            } else {
                holder.imgPhoto.setVisibility(View.GONE);
                holder.llAudio.setVisibility(View.GONE);
                holder.imgPlay.setVisibility(View.GONE);
                holder.recyclerView.setVisibility(View.GONE);
            }
        }
        else if(item.fileType.equalsIgnoreCase("birthdaypost"))
        {
            if (item.fileName != null) {

                ChildAdapter adapter;
                if (item.fileName.size() == 3) {
                    adapter = new ChildAdapter(2, item.fileName.size(), mContext, item.fileName);
                } else {
                    adapter = new ChildAdapter(Constants.MAX_IMAGE_NUM, item.fileName.size(), mContext, item.fileName);
                }
                adapter.setShowDownloadButton(false);
                holder.recyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(mContext, holder.recyclerView, adapter));
                holder.recyclerView.setVisibility(View.VISIBLE);
            }
            holder.llAudio.setVisibility(View.GONE);
            holder.imgPlay.setVisibility(View.GONE);
            holder.imgPhoto.setVisibility(View.GONE);

            if(!AmazoneImageDownload.isImageDownloaded(item.fileName.get(0)))
                listener.callBirthdayPostCreation(item , position);

        }
        else
            {
            holder.imgPhoto.setVisibility(View.GONE);
            holder.imgPlay.setVisibility(View.GONE);
            holder.llAudio.setVisibility(View.GONE);
            holder.recyclerView.setVisibility(View.GONE);
        }


        holder.imgLead_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.e("PostAdapter AA", "clicked");
                Dialog settingsDialog = new Dialog(mContext);
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                View newView = (View) inflater.inflate(R.layout.img_layout, null);
                settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(newView);
                ImageView iv = (ImageView) newView.findViewById(R.id.img_popup);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(ImageUtil.getTextLetter(name), ImageUtil.getRandomColor(position));
                iv.setImageDrawable(drawable);
                settingsDialog.show();
            }
        });

        holder.imgLead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.lin_drop.getVisibility() == View.VISIBLE)
                    holder.lin_drop.setVisibility(View.GONE);
                else {
                    Dialog settingsDialog = new Dialog(mContext);
                    LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                    View newView = (View) inflater.inflate(R.layout.img_layout, null);
                    settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    settingsDialog.setContentView(newView);
                    settingsDialog.show();
                    ImageView iv = (ImageView) newView.findViewById(R.id.img_popup);

                    if (!TextUtils.isEmpty(item.createdByImage)) {
                        Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.createdByImage)).into(iv);
                    } else {
                        TextDrawable drawable = TextDrawable.builder()
                                .buildRect(name, ImageUtil.getRandomColor(position));
                        iv.setImageDrawable(drawable);
                    }
                    settingsDialog.show();
                }
            }
        });


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


      /*  mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e(TAG,"Media Player "+mp.isPlaying());
                notifyItemChanged(position);
            }
        });*/


        holder.imgPlayAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{



                    if (mholder !=null)
                    {
                        AppLog.e(TAG,"mholder not null");
                        mHandler.removeCallbacks(myRunnable);
                        mholder.imgPlayAudio.setVisibility(View.VISIBLE);
                        mholder.imgPauseAudio.setVisibility(View.GONE);
                        mholder.seekBarAudio.setProgress(0);
                        mholder.tvTimeAudio.setText("00:00");
                        mholder.tvTimeTotalAudio.setText("00:00");
                    }

                    mholder = holder;

                    if (mediaPlayer != null && mediaPlayer.isPlaying())
                    {
                        AppLog.e(TAG,"mediaPlayer isPlaying");

                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(Constants.decodeUrlToBase64(item.fileName.get(0)));
                        mediaPlayer.prepare();
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                AppLog.e(TAG  , "ONPrepared : lenght : "+mp.getDuration());
                                mholder.seekBarAudio.setMax(mediaPlayer.getDuration()/1000);
                                mediaPlayer.start();
                                mHandler.post(myRunnable);
                            }
                        });
                      //  mediaPlayer.start();
                        //mholder.seekBarAudio.setMax(mediaPlayer.getDuration());
                        holder.imgPlayAudio.setVisibility(View.GONE);
                        holder.imgPauseAudio.setVisibility(View.VISIBLE);

                    }
                    else if (pos == position)
                    {
                        mediaPlayer.start();
                        holder.imgPauseAudio.setVisibility(View.VISIBLE);
                        holder.imgPlayAudio.setVisibility(View.GONE);
                        mHandler.post(myRunnable);
                    }
                    else
                    {

                        AppLog.e(TAG,"mediaPlayer is Not Playing");

                        if (mediaPlayer != null)
                        {
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                        }

                        mediaPlayer.setDataSource(Constants.decodeUrlToBase64(item.fileName.get(0)));
                        mediaPlayer.prepare();
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                AppLog.e(TAG  , "ONPrepared : lenght : "+mp.getDuration());
                                mholder.seekBarAudio.setMax(mediaPlayer.getDuration()/1000);
                                mediaPlayer.start();
                                mHandler.post(myRunnable);
                            }
                        });
                        //  mediaPlayer.start();

                        holder.imgPauseAudio.setVisibility(View.VISIBLE);
                        holder.imgPlayAudio.setVisibility(View.GONE);


                    }

                }
                catch(Exception e)
                {e.printStackTrace();
                    Log.e(TAG,"Exception"+e.getMessage());}
                pos = position;
            }

        });




        holder.imgPauseAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pos == position)
                {
                    try{
                        mHandler.removeCallbacks(myRunnable);
                        mediaPlayer.pause();
                        mholder.imgPauseAudio.setVisibility(View.GONE);
                        mholder.imgPlayAudio.setVisibility(View.VISIBLE);
                    }catch(Exception e){
                        e.printStackTrace();
                        Log.e(TAG,"Exception"+e.getMessage());
                    }
                }

            }
        });

        holder.seekBarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (pos == position)
                {
                    Log.e(TAG,"progress "+progress + "\ngetDuration "+mediaPlayer.getDuration()/1000+" condition : "+(progress >= mediaPlayer.getDuration()/1000));


                    if (progress >= mediaPlayer.getDuration()/1000)
                    {
                        mHandler.removeCallbacks(myRunnable);
                        mholder.imgPauseAudio.setVisibility(View.GONE);
                        mholder.imgPlayAudio.setVisibility(View.VISIBLE);

                        mholder.seekBarAudio.setProgress(0);
                        mholder.tvTimeAudio.setText("00:00");
                        mholder.tvTimeTotalAudio.setText("00:00");
                    }
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        holder.seekBarAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        holder.txt_title.setText(item.title);
        if (TextUtils.isEmpty(item.title))
            holder.txt_title.setVisibility(View.GONE);
        else
            holder.txt_title.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(item.text)) {
            holder.txtContent.setVisibility(View.VISIBLE);
            if (item.text.length() > 200) {
                StringBuilder stringBuilder = new StringBuilder(item.text);
                stringBuilder.setCharAt(197, '.');
                stringBuilder.setCharAt(198, '.');
                stringBuilder.setCharAt(199, '.');
                holder.txtContent.setText(stringBuilder);
                holder.txt_readmore.setVisibility(View.VISIBLE);
            } else {
                holder.txtContent.setText(item.text);
                holder.txt_readmore.setVisibility(View.GONE);
            }
        } else {
            holder.txtContent.setVisibility(View.GONE);
            holder.txt_readmore.setVisibility(View.GONE);
        }

        // show hide view
        if (item.canEdit) {
            holder.txt_drop_delete.setVisibility(View.VISIBLE);
            holder.txt_drop_report.setVisibility(View.GONE);

            if(BuildConfig.AppCategory.equalsIgnoreCase("constituency"))
            {
                holder.txt_readUnreadUuser.setVisibility(View.GONE);
            }
            else
            {
                holder.txt_readUnreadUuser.setVisibility(View.VISIBLE);
            }

            holder.view1.setVisibility(View.VISIBLE);
        } else {
            holder.txt_drop_delete.setVisibility(View.GONE);
            holder.txt_drop_report.setVisibility(View.VISIBLE);
            holder.txt_readUnreadUuser.setVisibility(View.GONE);
            holder.view1.setVisibility(View.GONE);
            // holder.ivDelete.setVisibility(View.GONE);
        }

        if (item.fileName != null && item.fileName.size() > 0) {
            holder.linExternalPush.setVisibility(View.VISIBLE);
            if(new AmazoneVideoDownload(mContext).isVideoDownloaded(item.fileName.get(0)))
            {
                holder.txt_drop_deletevideo.setVisibility(View.VISIBLE);
                holder.viewDeleteVideo.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.txt_drop_deletevideo.setVisibility(View.GONE);
                holder.viewDeleteVideo.setVisibility(View.GONE);
            }
        }else {
            holder.txt_drop_deletevideo.setVisibility(View.GONE);
            holder.viewDeleteVideo.setVisibility(View.GONE);
            holder.linExternalPush.setVisibility(View.GONE);
        }

        holder.txtLike.setVisibility(View.VISIBLE);
        holder.linLikes.setVisibility(View.VISIBLE);

        if (myTeamData.allowTeamPostCommentAll)
            holder.linComments.setVisibility(View.VISIBLE);
        else
            holder.linComments.setVisibility(View.GONE);


        //holder.llAudio.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return list.size();
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

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.view)
        View view;
        @Bind(R.id.txt_drop_delete)
        TextView txt_drop_delete;
        @Bind(R.id.txt_drop_report)
        TextView txt_drop_report;
        @Bind(R.id.txt_drop_deletevideo)
        TextView txt_drop_deletevideo;
        @Bind(R.id.viewDeleteVideo)
        View viewDeleteVideo;


        @Bind(R.id.view1)
        View view1;
        @Bind(R.id.txt_readUnreadUuser)
        TextView txt_readUnreadUuser;


        @Bind(R.id.txt_name)
        TextView txtName;
        @Bind(R.id.txt_content)
        TextView txtContent;


        @Bind(R.id.txt_title)
        TextView txt_title;

        @Bind(R.id.txt_readmore)
        TextView txt_readmore;


        @Bind(R.id.img_lead)
        ImageView imgLead;
        @Bind(R.id.img_lead_default)
        ImageView imgLead_default;
        @Bind(R.id.txt_date)
        TextView txtDate;
        @Bind(R.id.txt_like)
        TextView txtLike;
        @Bind(R.id.txt_fav)
        ImageView txt_fav;
        @Bind(R.id.txt_comments)
        TextView txt_comments;
        @Bind(R.id.txt_push)
        ImageView txt_push;
        @Bind(R.id.image)
        ImageView imgPhoto;
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
        @Bind(R.id.constThumb)
        ConstraintLayout constThumb;
        @Bind(R.id.imageThumb)
        ImageView imageThumb;


        @Bind(R.id.img_like)
        ImageView ivLike;


        @Bind(R.id.recyclerView)
        AsymmetricRecyclerView recyclerView;

        @Bind(R.id.imgDownloadPdf)
        ImageView imgDownloadPdf;

        @Bind(R.id.linExternalPush)
        LinearLayout linExternalPush;

        @Bind(R.id.external_txt_push)
        ImageView external_txt_push;


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

        @Bind(R.id.tvTimeTotalAudio)
        TextView tvTimeTotalAudio;

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

            if (BuildConfig.AppCategory.equalsIgnoreCase("constituency"))
            {
                linPush.setVisibility(View.GONE);
                txt_fav.setVisibility(View.GONE);
            }
            else
            {
                linPush.setVisibility(View.VISIBLE);
                txt_fav.setVisibility(View.VISIBLE);
            }

            recyclerView.setRequestedHorizontalSpacing(Utils.dpToPx(mContext, 3));
            recyclerView.addItemDecoration(
                    new SpacesItemDecoration(mContext.getResources().getDimensionPixelSize(R.dimen.padding_3dp)));
        }

        @OnClick({R.id.txt_like, R.id.txt_fav, R.id.rel, R.id.txt_readmore, R.id.iv_delete,
                R.id.txt_comments, R.id.txt_drop_delete, R.id.txt_drop_report , R.id.txt_drop_deletevideo, R.id.txt_drop_share, R.id.img_comments,
                R.id.txt_push, R.id.txt_name, R.id.img_like, R.id.txt_readUnreadUuser, R.id.linExternalPush, R.id.external_txt_push})
        public void OnLikeClick(View v) {
            item = list.get(getLayoutPosition());
            switch (v.getId()) {

                case R.id.external_txt_push:
                case R.id.linExternalPush:
                        if (lin_drop.getVisibility() == View.VISIBLE)
                            lin_drop.setVisibility(View.GONE);
                         if (isConnectionAvailable()) {

                            listener.onExternalShareClick(item);
                        } else {
                            showNoNetworkMsg();
                        }
                    break;


                case R.id.img_like:
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else if (isConnectionAvailable()) {
                        listener.onLikeClick(item, getLayoutPosition());
                    } else {
                        showNoNetworkMsg();
                    }
                    break;
                case R.id.txt_like:
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else if (isConnectionAvailable()) {
                        listener.onLikeListClick(item);
                    } else {
                        showNoNetworkMsg();
                    }
                    break;
                case R.id.txt_fav:
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else if (isConnectionAvailable()) {
                        listener.onFavClick(item, getLayoutPosition());
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
                case R.id.txt_readmore:
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else {
                        //listener.onReadMoreClick(item);
                        Intent intent = new Intent(mContext, ReadMoreActivity.class);
                        Gson gson = new Gson();
                        String data = gson.toJson(item);
                        intent.putExtra("data", data);
                        intent.putExtra("type", type);
                        intent.putExtra("team_id", mTeamId);
                        intent.putExtra("from", "post");
                        mContext.startActivity(intent);
                    }
                    break;

                case R.id.iv_delete:
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else
                        lin_drop.setVisibility(View.VISIBLE);
                    break;

                case R.id.iv_edit:
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else if (isConnectionAvailable()) {
                        listener.onEditClick(item);
                    } else {
                        showNoNetworkMsg();
                    }
                    break;

                case R.id.txt_comments:
                case R.id.img_comments:
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else if (isConnectionAvailable()) {
                        Intent intent1 = new Intent(mContext, CommentsActivity.class);
                        intent1.putExtra("id", mGroupId);
                        intent1.putExtra("team_id", mTeamId);
                        intent1.putExtra("post_id", list.get(getAdapterPosition()).id);
                        intent1.putExtra("type", type);
                        mContext.startActivity(intent1);
                    } else {
                        showNoNetworkMsg();
                    }
                    break;

                case R.id.txt_drop_delete:
                    lin_drop.setVisibility(View.GONE);
                    if (isConnectionAvailable()) {
                        listener.onDeleteClick(item);
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

                case R.id.txt_drop_report:
                    lin_drop.setVisibility(View.GONE);
                    if (isConnectionAvailable()) {
                        listener.onReportClick(item);
                    } else {
                        showNoNetworkMsg();
                    }
                    break;

                case R.id.txt_drop_share:
                    lin_drop.setVisibility(View.GONE);
                    if (isConnectionAvailable()) {
                        AppLog.e("onShareClick", "method called");
                        listener.onShareClick(item);
                    } else {
                        showNoNetworkMsg();
                    }
                    break;

                case R.id.txt_push:
                    lin_drop.setVisibility(View.GONE);
                    if (isConnectionAvailable()) {
                        AppLog.e("onShareClick", "method called");
                        listener.onPushClick(item);


                    } else {
                        showNoNetworkMsg();
                    }
                    break;

                case R.id.txt_name:
                    lin_drop.setVisibility(View.GONE);
                    listener.onNameClick(item);
                    break;

                case R.id.txt_readUnreadUuser:
                    lin_drop.setVisibility(View.GONE);
                    Intent intent = new Intent(mContext, ReadUnreadPostUsers.class);
                    intent.putExtra("group_id", GroupDashboardActivityNew.groupId);
                    intent.putExtra("team_id", mTeamId);
                    intent.putExtra("post_id", list.get(getAdapterPosition()).id);
                    mContext.startActivity(intent);
                    break;

            }
        }
    }

    public interface OnItemClickListener {
        void onFavClick(TeamPostGetData item, int pos);

        void onLikeClick(TeamPostGetData item, int pos);

        void onPostClick(TeamPostGetData item);

        void onReadMoreClick(TeamPostGetData item);

        void onEditClick(TeamPostGetData item);

        void onDeleteClick(TeamPostGetData item);

        void onReportClick(TeamPostGetData item);

        void onExternalShareClick(TeamPostGetData item);

        void onShareClick(TeamPostGetData item);

        void onPushClick(TeamPostGetData item);

        void onNameClick(TeamPostGetData item);

        void onLikeListClick(TeamPostGetData item);

        void callBirthdayPostCreation(TeamPostGetData item , int position);

        void onDeleteVideoClick(TeamPostGetData item , int position);
    }

    public void RemoveAll()
    {
        mHandler.removeCallbacks(myRunnable);
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
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
                //onBackPressed();
            }
        });
    }

}
