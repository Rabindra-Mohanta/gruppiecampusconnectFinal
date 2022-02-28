package school.campusconnect.adapters;

import android.content.Context;
import android.util.Log;
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
    public static String TAG = "FeedAdapter";
    private Context context;
    public List<NotificationListRes.NotificationListData> results;
    private int itemCount;
    public boolean isExpand = false;
    private onClick onClick;

    public FeedAdapter(FeedAdapter.onClick onClick) {
        this.onClick = onClick;
    }

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

        Log.e(TAG,"NotificationListData "+data.getIdPrimary());
        Log.e(TAG,"NotificationListData "+data.getReadedComment());

        if (data.getReadedComment())
        {
            holder.binding.llReaded.setBackground(context.getResources().getDrawable(R.drawable.feed_transparent_bg));
            holder.binding.viewReaded.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.binding.llReaded.setBackground(null);
            holder.binding.viewReaded.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"notificationClick "+data.getIdPrimary());
                Log.e(TAG,"notificationClick "+data.getReadedComment());

                if (data.getReadedComment())
                {
                    onClick.setReadedComment(data.getIdPrimary(),false);
                    holder.binding.llReaded.setBackground(null);
                    holder.binding.viewReaded.setVisibility(View.INVISIBLE);
                }
                else
                {
                    onClick.setReadedComment(data.getIdPrimary(),true);
                    holder.binding.llReaded.setBackground(context.getResources().getDrawable(R.drawable.feed_transparent_bg));
                    holder.binding.viewReaded.setVisibility(View.VISIBLE);
                }
            }
        });

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

    public void add(List<NotificationListRes.NotificationListData> results,int itemCount)
    {
        this.results = results;
        this.itemCount = itemCount;
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
                isExpand ? Math.min(results.size(), 10) : itemCount : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemFeedBinding binding;
        public ViewHolder(@NonNull ItemFeedBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public interface onClick
    {
        public void setReadedComment(long idPrimary, Boolean readedComment);
    }
}
