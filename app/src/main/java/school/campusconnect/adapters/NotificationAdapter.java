package school.campusconnect.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.PostItem;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ImageViewHolder> {
    private List<PostItem> list = Collections.emptyList();
    private OnLeadSelectListener listener;
    private Context mContext;


    public NotificationAdapter(List<PostItem> list)
    {
        if (list == null) return;
        this.list = list;
    }


    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notify, parent, false);
        return new ImageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        PostItem item = list.get(position);
//       AppLog.e("asf", new Gson().toJson(item));
//        holder.txtName.setText(item.getName());
//        holder.txtContent.setText(item.getContent());
//
//        if (item.getImage() != null && !item.getImage().isEmpty()) {
//            Picasso.with(mContext).load(item.getImage()).transform(new RoundedTransformation(40, 0)).
//                    into(holder.imgLead);
//        } else {
//            Picasso.with(mContext).load(R.drawable.ic_person).transform(new RoundedTransformation(40, 0)).
//                    into(holder.imgLead);
//
//        }
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

        @Bind(R.id.txt_name)
        TextView txtName;
        @Bind(R.id.txt_content)
        TextView txtContent;
        @Bind(R.id.img_lead)
        ImageView imgLead;

        @Bind(R.id.img_lead_default)
        ImageView imgLead_default;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }


    }

    public interface OnLeadSelectListener {
        void onLeadClick(PostItem item);
    }
}


