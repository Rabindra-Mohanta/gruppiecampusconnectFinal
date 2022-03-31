package school.campusconnect.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.Assymetric.AsymmetricRecyclerView;
import school.campusconnect.Assymetric.AsymmetricRecyclerViewAdapter;
import school.campusconnect.Assymetric.SpacesItemDecoration;
import school.campusconnect.Assymetric.Utils;
import school.campusconnect.R;
import school.campusconnect.datamodel.GalleryPostRes;
import school.campusconnect.utils.AmazoneVideoDownload;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.MixOperations;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private final ArrayList<GalleryPostRes.GalleryData> listData;
    private Context mContext;
    GalleryListener listener;
    public GalleryAdapter(ArrayList<GalleryPostRes.GalleryData> listData,GalleryListener listener) {
        this.listData=listData;
        this.listener=listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GalleryPostRes.GalleryData item = listData.get(position);
        holder.txt_title.setText(item.getAlbumName());
        holder.txtDate.setText(MixOperations.getFormattedDate(item.getCreatedAt(), Constants.DATE_FORMAT));

        if(!TextUtils.isEmpty(item.fileType))
        {
            if(item.fileType.equals(Constants.FILE_TYPE_IMAGE))
            {
                if (item.fileName!=null) {
               /* int height=0;
                if (item.imageHeight != 0 && item.imageWidth != 0)
                    height = (Constants.screen_width * item.imageHeight) / item.imageWidth;*/

                    ChildAdapter adapter;
                    if(item.fileName.size()==3)
                    {
                        adapter = new ChildAdapter(2, item.fileName.size(), mContext, item.fileName);
                    }
                    else
                    {
                        adapter = new ChildAdapter( Constants.MAX_IMAGE_NUM, item.fileName.size(), mContext, item.fileName);
                    }
                    holder.recyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(mContext, holder.recyclerView, adapter));
                    holder.recyclerView.setVisibility(View.VISIBLE);
                }
                holder.imgPlay.setVisibility(View.GONE);
                holder.imgPhoto.setVisibility(View.GONE);
            }
            else if (item.fileType.equals(Constants.FILE_TYPE_VIDEO)) {
                if (item.fileName != null) {
               /* int height=0;
                if (item.imageHeight != 0 && item.imageWidth != 0)
                    height = (Constants.screen_width * item.imageHeight) / item.imageWidth;*/

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
            }
            else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
                Picasso.with(mContext).load(R.drawable.pdf_thumbnail).into(holder.imgPhoto);
                holder.imgPlay.setVisibility(View.GONE);
                holder.imgPhoto.setVisibility(View.VISIBLE);

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                holder.imgPhoto.setLayoutParams(lp);
                holder.imgPhoto.requestLayout();

                holder.recyclerView.setVisibility(View.GONE);
            }
            else if(item.fileType.equals(Constants.FILE_TYPE_YOUTUBE))
            {
                int height = (Constants.screen_width * 204) / 480;
                holder.imgPhoto.getLayoutParams().height = height;
                holder.imgPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Picasso.with(mContext).load(item.thumbnail).into(holder.imgPhoto);
                holder.imgPhoto.setVisibility(View.VISIBLE);
                holder.imgPlay.setVisibility(View.VISIBLE);
                holder.recyclerView.setVisibility(View.GONE);
            }
            else {
                holder.imgPhoto.setVisibility(View.GONE);
                holder.imgPlay.setVisibility(View.GONE);
                holder.recyclerView.setVisibility(View.GONE);
            }
        }
        else
        {
            holder.imgPhoto.setVisibility(View.GONE);
            holder.imgPlay.setVisibility(View.GONE);
            holder.recyclerView.setVisibility(View.GONE);
        }

        holder.txt_drop_deletevideo.setVisibility(View.GONE);
        holder.viewDeleteVideo.setVisibility(View.GONE);
        holder.iv_delete.setVisibility(View.GONE);

        if (item.canEdit) {
            holder.txt_drop_delete.setVisibility(View.VISIBLE);
            holder.iv_delete.setVisibility(View.VISIBLE);

            if (item.fileName != null && item.fileName.size() > 0) {
                if(new AmazoneVideoDownload(mContext).isVideoDownloaded(item.fileName.get(0)))
                {
                    holder.txt_drop_deletevideo.setVisibility(View.VISIBLE);
                    holder.viewDeleteVideo.setVisibility(View.VISIBLE);
                    holder.iv_delete.setVisibility(View.VISIBLE);
                }
            }
        } else {
            holder.txt_drop_delete.setVisibility(View.GONE);
            holder.iv_delete.setVisibility(View.GONE);
            if (item.fileName != null && item.fileName.size() > 0) {
                if(new AmazoneVideoDownload(mContext).isVideoDownloaded(item.fileName.get(0)))
                {
                    holder.txt_drop_deletevideo.setVisibility(View.VISIBLE);
                    holder.viewDeleteVideo.setVisibility(View.VISIBLE);
                    holder.iv_delete.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.txt_date)
        TextView txtDate;

        @Bind(R.id.iv_delete)
        ImageView iv_delete;

        @Bind(R.id.txt_title)
        TextView txt_title;

        @Bind(R.id.img_play)
        ImageView imgPlay;

        @Bind(R.id.image)
        ImageView imgPhoto;

        @Bind(R.id.txt_drop_delete)
        TextView txt_drop_delete;

        @Bind(R.id.txt_drop_deletevideo)
        TextView txt_drop_deletevideo;
        @Bind(R.id.viewDeleteVideo)
        View viewDeleteVideo;

        @Bind(R.id.lin_drop)
        LinearLayout lin_drop;

        @Bind(R.id.recyclerView)
        AsymmetricRecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else
                        listener.onPostClick(listData.get(getAdapterPosition()));
                }
            });
            recyclerView.setRequestedHorizontalSpacing(Utils.dpToPx(mContext, 3));
            recyclerView.addItemDecoration(
                    new SpacesItemDecoration(mContext.getResources().getDimensionPixelSize(R.dimen.padding_3dp)));
        }

        @OnClick({R.id.iv_delete,R.id.txt_drop_delete,R.id.rel,R.id.txt_drop_deletevideo})
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.rel:
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);

                    listener.onPostClick(listData.get(getAdapterPosition()));

                    break;

                case R.id.iv_delete:
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else
                        lin_drop.setVisibility(View.VISIBLE);
                    break;

                case R.id.txt_drop_delete:
                    lin_drop.setVisibility(View.GONE);
                    listener.onDeleteClick(listData.get(getAdapterPosition()));
                    break;
                case R.id.txt_drop_deletevideo:
                    lin_drop.setVisibility(View.GONE);
                    listener.onDeleteVideoClick(listData.get(getAdapterPosition()) , getAdapterPosition());
                    break;
            }

        }
    }

    public interface GalleryListener
    {
        public void onPostClick(GalleryPostRes.GalleryData galleryData);
        public void onDeleteClick(GalleryPostRes.GalleryData galleryData);
        void onDeleteVideoClick(GalleryPostRes.GalleryData galleryData, int adapterPosition);
    }
}
