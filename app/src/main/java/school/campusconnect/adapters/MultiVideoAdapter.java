package school.campusconnect.adapters;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.VideoOfflineObject;
import school.campusconnect.utils.AmazoneVideoDownload;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;


public class MultiVideoAdapter extends RecyclerView.Adapter<MultiVideoAdapter.ImageViewHolder> {
    private  ArrayList<String> thumbnailImages;
    private List<String> list = new ArrayList<>();
    private OnImageClickListener listener;
    private Context mContext;
    AmazoneVideoDownload asyncTask;
    public MultiVideoAdapter(List<String> list,ArrayList<String> thumbnailImages, OnImageClickListener listener) {
        if (list == null) return;
        this.list = list;
        this.thumbnailImages = thumbnailImages;
        this.listener = listener;
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_multi_video, parent, false);
        return new ImageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final String item = list.get(position);

        if(thumbnailImages!=null && thumbnailImages.size()>position){
            Picasso.with(mContext).load(Constants.decodeUrlToBase64(thumbnailImages.get(position))).placeholder(R.drawable.video_place_holder).into(holder.ivImage);
        }
        if(new AmazoneVideoDownload(mContext).isVideoDownloaded(item)){
            holder.img_play.setVisibility(View.VISIBLE);
            holder.imgDownloadVideo.setVisibility(View.GONE);
        }else {
            holder.img_play.setVisibility(View.GONE);
            holder.imgDownloadVideo.setVisibility(View.VISIBLE);
        }


        holder.imgDownloadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imgDownloadVideo.setVisibility(View.GONE);
                holder.llProgress.setVisibility(View.VISIBLE);
                holder.progressBar1.setVisibility(View.VISIBLE);

                asyncTask = AmazoneVideoDownload.download(mContext, item, new AmazoneVideoDownload.AmazoneDownloadSingleListener() {
                    @Override
                    public void onDownload(File file) {
                        holder.llProgress.setVisibility(View.GONE);
                        holder.progressBar.setVisibility(View.GONE);
                        holder.progressBar1.setVisibility(View.GONE);
                        holder.img_play.setVisibility(View.VISIBLE);
                        holder.imgDownloadVideo.setVisibility(View.GONE);

                        AppLog.e(GroupDashboardActivityNew.class.getName(), "filename saved in preference : "+item);

                        try {
                            saveVideoNameOffline(item , file.getPath());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void error(String msg) {
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                            }
                        });
                    }

                    @Override
                    public void progressUpdate(int progress, int max) {
                        if(progress>0){
                            holder.progressBar1.setVisibility(View.GONE);
                        }
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.progressBar.setProgress(progress);
                            }
                        });
                    }
                });
            }
        });

        holder.imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imgDownloadVideo.setVisibility(View.VISIBLE);
                holder.llProgress.setVisibility(View.GONE);
                holder.progressBar1.setVisibility(View.GONE);
                asyncTask.cancel(true);
            }
        });

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

        @Bind(R.id.ivImage)
        ImageView ivImage;
        @Bind(R.id.img_play)
        ImageView img_play;
        @Bind(R.id.imgDownloadVideo)
        ImageView imgDownloadVideo;

        @Bind(R.id.imgCancel)
        ImageView imgCancel;

        @Bind(R.id.progressBar)
        ProgressBar progressBar;

        @Bind(R.id.llProgress)
        FrameLayout llProgress;

        @Bind(R.id.progressBar1)
        ProgressBar progressBar1;



        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }



        @OnClick({R.id.ivImage})
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ivImage:
                    listener.onImageClick(list.get(getLayoutPosition()));
                    break;
            }
        }
    }

    public interface OnImageClickListener {
        void onImageClick(String imagePath);
    }
    public void saveVideoNameOffline(String fileName, String filePath)
    {
        VideoOfflineObject offlineObject = new VideoOfflineObject();
        offlineObject.setVideo_filename(fileName);
        offlineObject.setVideo_filepath(filePath);
        offlineObject.setVideo_date(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        LeafPreference preference = LeafPreference.getInstance(mContext);

        if(!preference.getString(LeafPreference.OFFLINE_VIDEONAMES).equalsIgnoreCase(""))
        {
            ArrayList<VideoOfflineObject> offlineObjects = new Gson().fromJson(preference.getString(LeafPreference.OFFLINE_VIDEONAMES), new TypeToken<ArrayList<VideoOfflineObject>>() {
            }.getType());

            offlineObjects.add(offlineObject);
            preference.setString(LeafPreference.OFFLINE_VIDEONAMES , new Gson().toJson(offlineObjects));

        }
        else
        {
            ArrayList<VideoOfflineObject> offlineObjects = new ArrayList<>();
            offlineObjects.add(offlineObject);
            preference.setString(LeafPreference.OFFLINE_VIDEONAMES , new Gson().toJson(offlineObjects));
        }

    }
}


