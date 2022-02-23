package school.campusconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import school.campusconnect.R;
import school.campusconnect.databinding.ItemFeedBinding;
import school.campusconnect.datamodel.notificationList.NotificationListRes;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.MixOperations;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
    private Context context;
    private List<NotificationListRes.NotificationListData> results;
    public boolean isExpand = false;

    @NonNull
    @Override
    public FeedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ItemFeedBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_feed,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedAdapter.ViewHolder holder, int position) {

        NotificationListRes.NotificationListData data = results.get(position);

        if (position == 0)
        {
            holder.binding.llBottomLine.setVisibility(View.GONE);
        }

        holder.binding.tvTime.setText(MixOperations.getFormattedDate(data.getInsertedAt(), Constants.DATE_FORMAT));
        holder.binding.tvdesc.setText(data.getMessage());
        /*if (position == 0)
        {
            holder.binding.llReaded.setBackground(null);
            holder.binding.viewReaded.setVisibility(View.INVISIBLE);
            holder.binding.tvTime.setText("12:30 PM");
            holder.binding.tvdesc.setTextColor(context.getResources().getColor(R.color.red));
        }

        if (position == 1)
        {
            holder.binding.llReaded.setBackground(null);
            holder.binding.viewReaded.setVisibility(View.INVISIBLE);
        }
        if (position == 2)
        {
            holder.binding.llReaded.setBackground(context.getResources().getDrawable(R.drawable.feed_transparent_bg));
            holder.binding.viewReaded.setVisibility(View.VISIBLE);
        }*/


    }

    public void add(List<NotificationListRes.NotificationListData> results)
    {
        this.results = results;
        notifyDataSetChanged();
    }
    public void expand()
    {
        if (isExpand)
        {
            isExpand = false;
        }
        else
        {
            isExpand = true;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        return results != null ?
                isExpand ? Math.min(results.size(), 5) : 2 : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemFeedBinding binding;
        public ViewHolder(@NonNull ItemFeedBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
