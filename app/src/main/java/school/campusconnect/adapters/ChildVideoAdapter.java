package school.campusconnect.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import school.campusconnect.Assymetric.AGVRecyclerViewAdapter;
import school.campusconnect.Assymetric.AsymmetricItem;
import school.campusconnect.Assymetric.multiimages.ItemImage;
import school.campusconnect.R;
import school.campusconnect.activities.FullScreenVideoMultiActivity;
import school.campusconnect.activities.VideoPlayActivity;
import school.campusconnect.utils.AmazoneVideoDownload;
import school.campusconnect.utils.Constants;

public class ChildVideoAdapter extends AGVRecyclerViewAdapter<ChildVideoAdapter.ViewHolder> {
    private final ArrayList<String> thumbnailImages;
    private List<ItemImage> items;
    private final ArrayList<String> allImageList;
    private int currentOffset=0;
    private boolean isCol2Avail=false;
    private int mDisplay = 0;
    private int mTotal = 0;
    private Context context;

    AmazoneVideoDownload videoDownloader;

    public ChildVideoAdapter(int mDisplay, Context context, ArrayList<String> allImageList,ArrayList<String> thumbnailImages) {
        this.allImageList = allImageList;
        this.thumbnailImages = thumbnailImages;
        this.mDisplay = mDisplay;
        this.mTotal = allImageList.size();
        this.context = context;
        videoDownloader = new AmazoneVideoDownload(context);
        for (String s : allImageList) {
            Log.e("ChildAdapter Images", s);
        }

        items=new ArrayList<>();

       // ArrayList<ItemImage> tempData=new ArrayList<>();
        for (int i = 0; i < allImageList.size(); i++) {
            ItemImage itemImage = new ItemImage(allImageList.get(i));
            int colSpan1;
            int rowSpan1;

            if (allImageList.size() == 1) {
                colSpan1 = 2;
                rowSpan1 = 2;
            } else {
                colSpan1 = 1;
                rowSpan1 = 1;
            }

            if (colSpan1 == 2 && !isCol2Avail)
                isCol2Avail = true;
            else if (colSpan1 == 2 && isCol2Avail)
                colSpan1 = 1;

            itemImage.setColumnSpan(colSpan1);
            itemImage.setRowSpan(rowSpan1);
            itemImage.setPosition(currentOffset + i);
            items.add(itemImage);

          /*  int size = Constants.MAX_IMAGE_NUM;
            if (tempData.size() < size)
                size = tempData.size();
            for (int j = 0; j < size; j++) {
                items.add(tempData.get(j));
            }*/
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("RecyclerViewActivity", "onCreateView");
        return new ViewHolder(parent, viewType, items, allImageList);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("RecyclerViewActivity", "onBindView position=" + position);
        holder.bind(items, position, mDisplay, mTotal, context);
    }

    @Override
    public int getItemCount() {
        if(mDisplay==2 && items.size()>2)
            return 2;
        else if(mDisplay==4 && items.size()>4)
        {
            return 4;
        }
        else
        {
            return items.size();
        }

    }

    @Override
    public AsymmetricItem getItem(int position) {
        return (AsymmetricItem) items.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? 1 : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mImageView;
        private final ImageView imgDownloadVideo;
        private final ImageView img_play;
        private final TextView textView;
        private final ArrayList<String> allImageList;
        public ViewHolder(ViewGroup parent, int viewType, List<ItemImage> items, ArrayList<String> allImageList) {
            super(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.adapter_video_item, parent, false));

            this.allImageList = allImageList;
            for (String s : allImageList) {
                Log.e("ViewHolder Images", s);
            }
            mImageView = (ImageView) itemView.findViewById(R.id.mImageView);
            textView = (TextView) itemView.findViewById(R.id.tvCount);
            imgDownloadVideo = (ImageView) itemView.findViewById(R.id.imgDownloadVideo);
            img_play = (ImageView) itemView.findViewById(R.id.img_play);
        }

        public void bind(final List<ItemImage> item, final int position, int mDisplay, int mTotal, final Context mContext) {

            Log.e("MULTI_BIND", "image " + position + "is " + Constants.decodeUrlToBase64(item.get(position).getImagePath()));

            if(thumbnailImages!=null && thumbnailImages.size()>position){
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(thumbnailImages.get(position))).placeholder(R.drawable.video_place_holder).into(mImageView);

            }
            if(new AmazoneVideoDownload(mContext).isVideoDownloaded(item.get(position).getImagePath())){
                img_play.setVisibility(View.VISIBLE);
                imgDownloadVideo.setVisibility(View.GONE);
            }else {
                img_play.setVisibility(View.GONE);
                imgDownloadVideo.setVisibility(View.VISIBLE);
            }


            textView.setText("+" + (mTotal - mDisplay));
            if (mTotal > mDisplay) {
                if (position == mDisplay - 1) {
                    textView.setVisibility(View.VISIBLE);
                    mImageView.setAlpha(72);
                } else {
                    textView.setVisibility(View.INVISIBLE);
                    mImageView.setAlpha(255);
                }
            } else {
                mImageView.setAlpha(255);
                textView.setVisibility(View.INVISIBLE);
            }

            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (allImageList.size() == 1){

                        Intent i = new Intent(mContext, VideoPlayActivity.class);
                        i.putExtra("video", allImageList.get(0));

                        if (thumbnailImages != null) {
                            i.putExtra("thumbnail", thumbnailImages.get(0));
                        }
                        mContext.startActivity(i);
                    } else {
                        Intent i = new Intent(mContext, FullScreenVideoMultiActivity.class);
                        i.putStringArrayListExtra("video_list", allImageList);
                        i.putStringArrayListExtra("thumbnailImages", thumbnailImages);
                        mContext.startActivity(i);
                    }
                }
            });

            // textView.setText(String.valueOf(item.getPosition()));
        }
    }
}


