package school.campusconnect.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.utils.AmazoneVideoDownload;
import school.campusconnect.utils.Constants;


public class MultiVideoAdapter extends RecyclerView.Adapter<MultiVideoAdapter.ImageViewHolder> {
    private  ArrayList<String> thumbnailImages;
    private List<String> list = new ArrayList<>();
    private OnImageClickListener listener;
    private Context mContext;

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

        if(thumbnailImages!=null && thumbnailImages.size()>=position){
            Picasso.with(mContext).load(Constants.decodeUrlToBase64(thumbnailImages.get(position))).placeholder(R.drawable.video_place_holder).into(holder.ivImage);
        }
        if(AmazoneVideoDownload.isVideoDownloaded(item)){
            holder.img_play.setVisibility(View.VISIBLE);
            holder.imgDownloadVideo.setVisibility(View.GONE);
        }else {
            holder.img_play.setVisibility(View.GONE);
            holder.imgDownloadVideo.setVisibility(View.VISIBLE);
        }

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

}


