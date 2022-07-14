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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.Media.ImagePathTBL;
import school.campusconnect.utils.AmazoneHelper;
import school.campusconnect.utils.AmazoneImageDownload;
import school.campusconnect.utils.Constants;


public class MultiImageAdapter extends RecyclerView.Adapter<MultiImageAdapter.ImageViewHolder> {
    private List<String> list = new ArrayList<>();
    private OnImageClickListener listener;
    private Context mContext;
    String imagePreviewUrl = "";
    public MultiImageAdapter(List<String> list, OnImageClickListener listener) {
        if (list == null) return;
        this.list = list;
        this.listener = listener;
        imagePreviewUrl = LeafPreference.getInstance(mContext).getString("PREVIEW_URL","https://ik.imagekit.io/mxfzvmvkayv/");
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_multi_image, parent, false);
        return new ImageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        String item = list.get(position);

        if (item != null && !item.isEmpty()) {
            Log.e("LeadAdapter", "Item Not Empty ");

            String key =  Constants.decodeUrlToBase64(item).replace(AmazoneHelper.BUCKET_NAME_URL, "");
            String Filepath;

            if (key.contains("/")) {
                String[] splitStr = key.split("/");
                Filepath = splitStr[1];
            } else {
                Filepath = key;
            }

            if (ImagePathTBL.getLastInserted(Filepath).size() > 0)
            {
                String uri = ImagePathTBL.getLastInserted(Filepath).get(0).url;

                Picasso picasso = Picasso.with(mContext);

                picasso.load(new File(uri)).placeholder(R.drawable.placeholder_image).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {


                        if (!AmazoneImageDownload.isImageDownloaded(mContext,item))
                        {
                            holder.asyncTask = AmazoneImageDownload.download(mContext, item, new AmazoneImageDownload.AmazoneDownloadSingleListener() {
                                @Override
                                public void onDownload(Uri file) {
                                    holder.llProgress.setVisibility(View.GONE);
                                    holder.progressBar.setVisibility(View.GONE);
                                    holder.progressBar1.setVisibility(View.GONE);


                                    Glide.with(mContext).load(file).placeholder(R.drawable.placeholder_image).into(holder.ivImage);
                                }

                                @Override
                                public void error(String msg) {

                                    ((Activity)mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            holder.llProgress.setVisibility(View.GONE);
                                            holder.progressBar.setVisibility(View.GONE);
                                            holder.progressBar1.setVisibility(View.GONE);
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
                                                holder.progressBar1.setVisibility(View.GONE);
                                            }
                                            holder.progressBar.setProgress(progress);
                                        }
                                    });
                                }
                            });
                        }
                        else
                        {
                            Glide.with(mContext).load(AmazoneImageDownload.getDownloadPath(mContext,item)).diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .placeholder(R.drawable.placeholder_image).into(holder.ivImage);
                        }
                    }
                });

            }
            else
            {
                if(AmazoneImageDownload.isImageDownloaded(mContext,item)){
                    holder.llProgress.setVisibility(View.GONE);
                    holder.imgDownload.setVisibility(View.GONE);
                    Glide.with(mContext).load(AmazoneImageDownload.getDownloadPath(mContext,item)).diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true).placeholder(R.drawable.placeholder_image).into(holder.ivImage);
                }
                else
                {
                    {
                        String path = Constants.decodeUrlToBase64(item);
                        String newStr = path.substring(path.indexOf("/images")+1);
                        Glide.with(mContext).load(imagePreviewUrl+newStr+"?tr=w-50").diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true).placeholder(R.drawable.placeholder_image).into(holder.ivImage);

                        holder.imgDownload.setVisibility(View.VISIBLE);
                        holder.imgDownload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.imgDownload.setVisibility(View.GONE);
                                holder.llProgress.setVisibility(View.VISIBLE);
                                holder.progressBar1.setVisibility(View.VISIBLE);
                                holder.asyncTask = AmazoneImageDownload.download(mContext, item, new AmazoneImageDownload.AmazoneDownloadSingleListener() {
                                    @Override
                                    public void onDownload(Uri file) {
                                        holder.llProgress.setVisibility(View.GONE);
                                        holder.progressBar.setVisibility(View.GONE);
                                        holder. progressBar1.setVisibility(View.GONE);
                                        Picasso.with(mContext).load(file).placeholder(R.drawable.placeholder_image).fit().into(holder.ivImage, new Callback() {
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
                                                holder.llProgress.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                                holder.progressBar1.setVisibility(View.GONE);
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
                                                    holder.progressBar1.setVisibility(View.GONE);
                                                }
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
                                holder.imgDownload.setVisibility(View.VISIBLE);
                                holder.llProgress.setVisibility(View.GONE);
                                holder.progressBar1.setVisibility(View.GONE);
                                holder.asyncTask.cancel(true);
                            }
                        });

                        holder.imgDownload.setVisibility(View.GONE);
                        holder.llProgress.setVisibility(View.VISIBLE);
                        holder.progressBar1.setVisibility(View.VISIBLE);

                        holder.asyncTask = AmazoneImageDownload.download(mContext, item, new AmazoneImageDownload.AmazoneDownloadSingleListener() {
                            @Override
                            public void onDownload(Uri file) {
                                holder.llProgress.setVisibility(View.GONE);
                                holder.progressBar.setVisibility(View.GONE);
                                holder.progressBar1.setVisibility(View.GONE);

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                    Picasso.with(mContext).load(file).placeholder(R.drawable.placeholder_image).fit().into(holder.ivImage, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError() {

                                        }
                                    });
                                }
                                else
                                {
                                    Glide.with(mContext).load(file).placeholder(R.drawable.placeholder_image).into(holder.ivImage);
                                }


                            }

                            @Override
                            public void error(String msg) {
                                ((Activity)mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.llProgress.setVisibility(View.GONE);
                                        holder.progressBar.setVisibility(View.GONE);
                                        holder.progressBar1.setVisibility(View.GONE);
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
                                            holder.progressBar1.setVisibility(View.GONE);
                                        }
                                        holder.progressBar.setProgress(progress);
                                    }
                                });
                            }
                        });

                    }
                }
            }




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

        @Bind(R.id.imgDownload)
        ImageView imgDownload;

        @Bind(R.id.imgCancel)
        ImageView imgCancel;

        @Bind(R.id.progressBar)
        ProgressBar progressBar;

        @Bind(R.id.progressBar1)
        ProgressBar progressBar1;

        @Bind(R.id.llProgress)
        FrameLayout llProgress;

        AmazoneImageDownload asyncTask;


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


