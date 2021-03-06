package school.campusconnect.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;

public class UploadImageAdapter extends RecyclerView.Adapter<UploadImageAdapter.ViewHolder> {

    public static String TAG = "UploadImageAdapter";
    private final ArrayList<String> uploadImages;
    Context context;
    UploadImageListener listener;
    private String fileTypeImageOrVideo= Constants.FILE_TYPE_IMAGE;
    private RecyclerView recyclerView;

    public UploadImageAdapter(ArrayList<String> uploadImages, UploadImageListener listener) {
        this.uploadImages=uploadImages;
        this.listener=listener;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        dragDropFeature();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context=parent.getContext();
        View view= LayoutInflater.from(context).inflate(R.layout.item_image_upload,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            holder.imgRemove.setVisibility(View.VISIBLE);
            try {
                if(Constants.FILE_TYPE_IMAGE.equals(fileTypeImageOrVideo)){
//                    File file=new File(uploadImages.get(position));


                    int size = dpToPx(context.getResources().getDisplayMetrics(), 80);

                    RequestOptions reqOption = new RequestOptions();
                    reqOption.override(size, size);

                    Glide.with(context).load(uploadImages.get(position)).apply(reqOption).diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true).into(holder.imgUpload);

              //      Picasso.with(context).load(uploadImages.get(position)).resize(80,80).into(holder.imgUpload);
                }else {
                    // TODO : URI : Display Video Thumbnain from URI
                 //   Bitmap bMap = ThumbnailUtils.createVideoThumbnail(uploadImages.get(position) , MediaStore.Video.Thumbnails.MICRO_KIND);
               //    holder.imgUpload.setImageBitmap(bMap);

                    MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
                    mMMR.setDataSource(context, Uri.parse(uploadImages.get(position)));
                    holder.imgUpload.setImageBitmap(mMMR.getFrameAtTime());

                }
            }catch (Exception e)
            {
                AppLog.e("UploadImageAdapter" , "error on crateing bitmap : "+e.getLocalizedMessage());
                e.printStackTrace();
            }
    }

    public static int dpToPx(DisplayMetrics displayMetrics, int dp) {

        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    public int getItemCount() {
            return uploadImages.size();
    }

    public void setType(String fileTypeImageOrVideo) {
        this.fileTypeImageOrVideo = fileTypeImageOrVideo;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.imgUpload)
        ImageView imgUpload;
        @Bind(R.id.imgRemove)
        ImageView imgRemove;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }
        @OnClick({R.id.imgUpload,R.id.imgRemove})
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.imgUpload:
                    break;
                case R.id.imgRemove:
                    uploadImages.remove(getAdapterPosition());
                    notifyDataSetChanged();
                    break;
            }
        }
    }
    public interface UploadImageListener
    {
        public void onImageSelect();
        public void onImageRemove();
    }

    private void dragDropFeature() {
        // Extend the Callback class
        ItemTouchHelper.Callback _ithCallback = new ItemTouchHelper.Callback() {
            //and in your imlpementaion of
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                // get the viewHolder's and target's positions in your adapter data, swap them
                Collections.swap(uploadImages/*RecyclerView.Adapter's data collection*/, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                // and notify the adapter that its dataset has changed
                notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //TODO
            }

            //defines the enabled move directions in each state (idle, swiping, dragging).
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                        ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
            }
        };
        // Create an `ItemTouchHelper` and attach it to the `RecyclerView`
        ItemTouchHelper ith = new ItemTouchHelper(_ithCallback);
        ith.attachToRecyclerView(recyclerView);
    }
}
