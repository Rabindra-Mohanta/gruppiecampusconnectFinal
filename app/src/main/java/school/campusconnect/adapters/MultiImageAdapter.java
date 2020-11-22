package school.campusconnect.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.utils.Constants;


public class MultiImageAdapter extends RecyclerView.Adapter<MultiImageAdapter.ImageViewHolder> {
    private List<String> list = new ArrayList<>();
    private OnImageClickListener listener;
    private Context mContext;

    public MultiImageAdapter(List<String> list, OnImageClickListener listener) {
        if (list == null) return;
        this.list = list;
        this.listener = listener;
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
        final String item = list.get(position);

        if (item != null && !item.isEmpty()) {
            Log.e("LeadAdapter", "Item Not Empty ");
            Picasso.with(mContext).load(Constants.decodeUrlToBase64(item)).placeholder(R.drawable.placeholder_image).networkPolicy(NetworkPolicy.OFFLINE).into(holder.ivImage,
                    new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(mContext).load(Constants.decodeUrlToBase64(item)).placeholder(R.drawable.placeholder_image).into(holder.ivImage);
                        }
                    });
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


