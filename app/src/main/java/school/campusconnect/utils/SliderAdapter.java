package school.campusconnect.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import java.util.ArrayList;

import school.campusconnect.R;
import school.campusconnect.databinding.ItemSliderBinding;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.ViewHolder> {
    public static String TAG = "SliderAdapter";
    ArrayList<String> urls;
    Context context;
    private Boolean isAdmin;
    private Listner listner;

    public SliderAdapter(ArrayList<String> urls, Context context, Listner listner,Boolean isAdmin) {
        this.urls = urls;
        this.context = context;
        this.listner = listner;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSliderBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_slider,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Uri uri = Uri.parse(urls.get(position));
        Log.e(TAG,"Url"+uri);
        Glide.with(context).load(uri).into(holder.binding.imgSlider);

       /* if (isAdmin)
        {
            holder.binding.imgEdit.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.binding.imgEdit.setVisibility(View.GONE);
        }*/
        holder.binding.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                     listner.startEditBanner(uri,position);
            }
        });


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
