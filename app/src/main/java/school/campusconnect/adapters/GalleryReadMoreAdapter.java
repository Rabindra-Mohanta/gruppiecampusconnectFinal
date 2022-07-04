package school.campusconnect.adapters;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import school.campusconnect.Assymetric.AGVRecyclerViewAdapter;
import school.campusconnect.Assymetric.AsymmetricItem;
import school.campusconnect.Assymetric.multiimages.ItemImage;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.gallery.GalleryPostRes;
import school.campusconnect.utils.AmazoneImageDownload;
import school.campusconnect.utils.AmazoneVideoDownload;
import school.campusconnect.utils.Constants;

public class GalleryReadMoreAdapter extends AGVRecyclerViewAdapter<GalleryReadMoreAdapter.ViewHolder> {
    private List<ItemImage> items;
    private final ArrayList<String> thumbnailImages;
    private final ArrayList<String> allImageList;
    private int currentOffset=0;
    private boolean isCol2Avail=false;
    private int mDisplay = 0;
    private int mTotal = 0;
    private Context context;
    GalleryPostRes.GalleryData item;
    GalleryImageListener listener;
    String imagePreviewUrl = "";
    public GalleryImageListener getListener() {
        return listener;
    }

    public void setListener(GalleryImageListener listener) {
        this.listener = listener;
    }

    public GalleryReadMoreAdapter(int mDisplay, int mTotal, Context context, GalleryPostRes.GalleryData item) {
        this.allImageList = item.fileName;
        this.thumbnailImages = item.thumbnailImage;
        this.item = item;
        this.mDisplay = mDisplay;
        this.mTotal = mTotal;
        this.context = context;
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
        imagePreviewUrl = LeafPreference.getInstance(context).getString("PREVIEW_URL","https://ik.imagekit.io/mxfzvmvkayv/");
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

            return items.size();

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
        private final TextView textView;
        private final ArrayList<String> allImageList;
        private final ImageView imgDownloadVideo;
        private final ImageView img_play;

        private final ImageView imgDownload;
        private final ImageView imgCancel;
        private final ProgressBar progressBar;
        private final FrameLayout llProgress;
        private final ProgressBar progressBar1;
        AmazoneImageDownload asyncTask;

        public ViewHolder(ViewGroup parent, int viewType, List<ItemImage> items, ArrayList<String> allImageList) {
            super(LayoutInflater.from(parent.getContext()).inflate(item.fileType.equals(Constants.FILE_TYPE_VIDEO)?R.layout.adapter_video_item:R.layout.adapter_item, parent, false));

            this.allImageList = allImageList;
            for (String s : allImageList) {
                Log.e("ViewHolder Images", s);
            }


            mImageView = (ImageView) itemView.findViewById(R.id.mImageView);
            textView = (TextView) itemView.findViewById(R.id.tvCount);
            imgDownloadVideo = (ImageView) itemView.findViewById(R.id.imgDownloadVideo);
            img_play = (ImageView) itemView.findViewById(R.id.img_play);

            imgDownload = (ImageView) itemView.findViewById(R.id.imgDownload);
            imgCancel = (ImageView) itemView.findViewById(R.id.imgCancel);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            progressBar1 = (ProgressBar) itemView.findViewById(R.id.progressBar1);
            llProgress = (FrameLayout) itemView.findViewById(R.id.llProgress);
        }


        public void bind(final List<ItemImage> item, final int position, int mDisplay, int mTotal, final Context mContext) {

            Log.e("MULTI_BIND", "image " + position + "is " + Constants.decodeUrlToBase64(item.get(position).getImagePath()));

            if (GalleryReadMoreAdapter.this.item.fileType.equals(Constants.FILE_TYPE_VIDEO)) {
                if(thumbnailImages!=null && thumbnailImages.size()>position){
                    Picasso.with(mContext).load(Constants.decodeUrlToBase64(thumbnailImages.get(position))).placeholder(R.drawable.video_place_holder).into(mImageView);
                }
                if(new AmazoneVideoDownload(mContext).isVideoDownloaded(mContext,item.get(position).getImagePath())){
                    img_play.setVisibility(View.VISIBLE);
                    imgDownloadVideo.setVisibility(View.GONE);
                }else {
                    img_play.setVisibility(View.GONE);
                    imgDownloadVideo.setVisibility(View.VISIBLE);
                }
            }else {

                if(AmazoneImageDownload.isImageDownloaded(context,item.get(position).getImagePath())){
                    llProgress.setVisibility(View.GONE);
                    imgDownload.setVisibility(View.GONE);
                    Picasso.with(mContext).load(AmazoneImageDownload.getDownloadPath(mContext,item.get(position).getImagePath())).fit().placeholder(R.drawable.placeholder_image).into(mImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Log.e("Picasso", "Error : ");
                        }
                    });
                }
                else {
                    String path = Constants.decodeUrlToBase64(item.get(position).getImagePath());
                    String newStr = path.substring(path.indexOf("/images")+1);
                    Picasso.with(mContext).load(imagePreviewUrl+newStr+"?tr=w-50").placeholder(R.drawable.placeholder_image).into(mImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Log.e("Picasso", "Error : ");
                        }
                    });
                    imgDownload.setVisibility(View.VISIBLE);
                    imgDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imgDownload.setVisibility(View.GONE);
                            llProgress.setVisibility(View.VISIBLE);
                            progressBar1.setVisibility(View.VISIBLE);
                            asyncTask = AmazoneImageDownload.download(mContext, item.get(position).getImagePath(), new AmazoneImageDownload.AmazoneDownloadSingleListener() {
                                @Override
                                public void onDownload(Uri file) {
                                    llProgress.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                    progressBar1.setVisibility(View.GONE);
                                    Picasso.with(mContext).load(file).placeholder(R.drawable.placeholder_image).fit().into(mImageView, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError() {
                                            Log.e("Picasso", "Error : ");
                                        }
                                    });
                                }

                                @Override
                                public void error(String msg) {
                                    ((Activity)mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            llProgress.setVisibility(View.GONE);
                                            progressBar.setVisibility(View.GONE);
                                            progressBar1.setVisibility(View.GONE);
                                            Toast.makeText(mContext, msg + "", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void progressUpdate(int progress, int max) {
                                    ((Activity)mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(progress>0){
                                                progressBar1.setVisibility(View.GONE);
                                            }
                                            progressBar.setProgress(progress);
                                        }
                                    });
                                }
                            });


                        }
                    });
                    imgCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imgDownload.setVisibility(View.VISIBLE);
                            llProgress.setVisibility(View.GONE);
                            progressBar1.setVisibility(View.GONE);
                            asyncTask.cancel(true);
                        }
                    });



                    if (!AmazoneImageDownload.isImageDownloaded(context,item.get(position).getImagePath())) {

                        imgDownload.setVisibility(View.GONE);
                        llProgress.setVisibility(View.VISIBLE);
                        progressBar1.setVisibility(View.VISIBLE);

                        asyncTask = AmazoneImageDownload.download(mContext, item.get(position).getImagePath(), new AmazoneImageDownload.AmazoneDownloadSingleListener() {
                            @Override
                            public void onDownload(Uri file) {
                                llProgress.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                progressBar1.setVisibility(View.GONE);
                                Picasso.with(mContext).load(file).placeholder(R.drawable.placeholder_image).fit().into(mImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                            }

                            @Override
                            public void error(String msg) {
                                ((Activity)mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        llProgress.setVisibility(View.GONE);
                                        progressBar.setVisibility(View.GONE);
                                        progressBar1.setVisibility(View.GONE);
                                        Toast.makeText(mContext, msg + "", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void progressUpdate(int progress, int max) {
                                ((Activity)mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(progress>0){
                                            progressBar1.setVisibility(View.GONE);
                                        }
                                        progressBar.setProgress(progress);
                                    }
                                });
                            }
                        });
                    }
                }
              /*  Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.get(position).getImagePath())).placeholder(R.drawable.placeholder_image).into(mImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Log.e("Picasso", "Error : ");
                    }
                });*/
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
                    if(listener!=null){
                        listener.onItemClick(allImageList,thumbnailImages);
                    }
                }
            });
            mImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(listener!=null){
                        listener.onItemLongClick(item.get(position));
                    }
                    return true;
                }
            });

            // textView.setText(String.valueOf(item.getPosition()));
        }
    }
    public interface GalleryImageListener{
        public void onItemClick(ArrayList<String> itemImage,ArrayList<String> thumbnailImages);
        public void onItemLongClick(ItemImage itemImage);
    }
}


