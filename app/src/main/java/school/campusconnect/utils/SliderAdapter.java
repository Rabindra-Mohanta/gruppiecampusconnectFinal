package school.campusconnect.utils;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import school.campusconnect.R;
import school.campusconnect.databinding.ItemSliderBinding;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.ViewHolder> {
    public static String TAG = "SliderAdapter";
    ArrayList<String> urls;
    Context context;
    AmazoneImageDownload asyncTask;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ItemSliderBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_slider,parent,false);
        return new ViewHolder(binding);
    }

    public void add(ArrayList<String> urls)
    {
        this.urls = urls;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String uri = urls.get(position);
        Log.e(TAG,"Url "+uri);
      //  Glide.with(context).load(uri).into(holder.binding.imgSlider);

       /* if (isAdmin)
        {
            holder.binding.imgEdit.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.binding.imgEdit.setVisibility(View.GONE);


        }*/

        int size = dpToPx(context.getResources().getDisplayMetrics(), 80);

        RequestOptions reqOption = new RequestOptions();
        reqOption.override(size, size);

        Glide.with(context).load(Constants.decodeUrlToBase64(uri)).apply(reqOption).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(holder.binding.imgSlider);



    }
    public static int dpToPx(DisplayMetrics displayMetrics, int dp) {

        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemSliderBinding binding;
        public ViewHolder(@NonNull ItemSliderBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public interface Listner
    {
        void startEditBanner(Uri uri,int position);
    }
}
