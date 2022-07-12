package school.campusconnect.adapters;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import school.campusconnect.R;
import school.campusconnect.databinding.ItemFeedBinding;
import school.campusconnect.datamodel.notificationList.NotificationListRes;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.MixOperations;

public class SchoolFeedAdapater extends RecyclerView.Adapter<SchoolFeedAdapater.ViewHolder> {
    public static String TAG = "SchoolFeedAdapater";
    private Context context;
    public List<NotificationListRes.NotificationListData> results;

    private int itemCount;

    private Boolean isAssigned = false;
    public boolean isExpand = false;
    private onClick onClick;
    int CountScroll = 0;


    int totalItem = 0;
    ViewHolder viewHolder;

    private Handler mHandler = new Handler();

    Runnable myRunnable = new Runnable() {
        @Override
        public void run() {


            try{
                if (totalItem == CountScroll)
                {
                    Animation anim = new AlphaAnimation(0.0f, 1.0f);
                    anim.setDuration(1000); //You can manage the blinking time with this parameter
                    anim.setStartOffset(20);
                    anim.setRepeatMode(Animation.REVERSE);
                    anim.setRepeatCount(0);
                    viewHolder.itemView.startAnimation(anim);

                    if (results.get(totalItem-CountScroll+1).getReadedComment()!= null && results.get(totalItem-CountScroll+1).getReadedComment().equalsIgnoreCase("true"))
                    {
                        viewHolder.binding.llReaded.setBackground(context.getResources().getDrawable(R.drawable.feed_transparent_bg));
                        viewHolder.binding.viewReaded.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        viewHolder.binding.llReaded.setBackground(null);
                        viewHolder.binding.viewReaded.setVisibility(View.INVISIBLE);
                    }

                    viewHolder.binding.tvdesc.setText(results.get(totalItem-CountScroll+1).getMessage());
                    Log.e(TAG,"Msg totalItem"+results.get(totalItem-CountScroll+1).getMessage());
                    CountScroll = 0;

                }
                else
                {
                    Animation anim = new AlphaAnimation(0.0f, 1.0f);
                    anim.setDuration(1000); //You can manage the blinking time with this parameter
                    anim.setStartOffset(20);
                    anim.setRepeatMode(Animation.REVERSE);
                    anim.setRepeatCount(0);
                    viewHolder.itemView.startAnimation(anim);

                    if (results.get(CountScroll).getReadedComment()!= null && results.get(CountScroll).getReadedComment().equalsIgnoreCase("true"))
                    {
                        viewHolder.binding.llReaded.setBackground(context.getResources().getDrawable(R.drawable.feed_transparent_bg));
                        viewHolder.binding.viewReaded.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        viewHolder.binding.llReaded.setBackground(null);
                        viewHolder.binding.viewReaded.setVisibility(View.INVISIBLE);
                    }

                    viewHolder.binding.tvdesc.setText(results.get(CountScroll).getMessage());
                    Log.e(TAG,"Msg "+results.get(CountScroll).getMessage());


                    CountScroll = CountScroll + 1;
                }
                mHandler.postDelayed(myRunnable, 4000);

            }catch (Exception e)
            {
                Log.e(TAG,"exception"+ e.getMessage());
            }

        }
    };


    public SchoolFeedAdapater(onClick onClick) {
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ItemFeedBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_feed,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        viewHolder = holder;

        if (!isAssigned)
        {
             ((AppCompatActivity) context).runOnUiThread(myRunnable);
            isAssigned = true;
        }

        Log.e(TAG,"position "+position);
        NotificationListRes.NotificationListData data = results.get(position);


        if (position == 0)
        {
            holder.binding.llBottomLine.setVisibility(View.GONE);
        }
        else
        {
            holder.binding.llBottomLine.setVisibility(View.VISIBLE);
        }

        holder.binding.tvTime.setText(MixOperations.getFormattedDate(data.getInsertedAt(), Constants.DATE_FORMAT));
        holder.binding.tvdesc.setText(data.getMessage());


        if (data.getReadedComment()!= null && data.getReadedComment().equalsIgnoreCase("true"))
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

                // onClick.onItemClick(data);

                if (data.getReadedComment().equalsIgnoreCase("true"))
                {
                    //  onClick.setReadedComment(data.getIdPrimary(),false);
                    onClick.onItemClick(data,false);
                 /*   holder.binding.llReaded.setBackground(null);
                    holder.binding.viewReaded.setVisibility(View.INVISIBLE);*/
                }
                else
                {
                    //  onClick.setReadedComment(data.getIdPrimary(),true);
                    onClick.onItemClick(data,false);
                }
            }
        });

        holder.binding.tvdesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // onClick.onItemClick(data);

                if (data.getReadedComment().equalsIgnoreCase("true"))
                {
                    //  onClick.setReadedComment(data.getIdPrimary(),false);
                    onClick.onItemClick(data,false);
                 /*   holder.binding.llReaded.setBackground(null);
                    holder.binding.viewReaded.setVisibility(View.INVISIBLE);*/
                }
                else
                {
                    //  onClick.setReadedComment(data.getIdPrimary(),true);
                    onClick.onItemClick(data,false);
                  /*  holder.binding.llReaded.setBackground(context.getResources().getDrawable(R.drawable.feed_transparent_bg));
                    holder.binding.viewReaded.setVisibility(View.VISIBLE);*/
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

        if (results.size()>=10)
        {
            this.totalItem = 10;
        }
        else
        {
            this.totalItem = results.size();
        }

        this.itemCount = itemCount;
        notifyDataSetChanged();
    }
    public void expand()
    {
        if (isExpand)
        {
             ((AppCompatActivity) context).runOnUiThread(myRunnable);
            isExpand = false;
        }
        else
        {
                mHandler.removeCallbacks(myRunnable);
            isExpand = true;
            CountScroll = 0;
        }
        notifyDataSetChanged();
    }

    public void removeCallBack()
    {
        isAssigned = false;
        mHandler.removeCallbacks(myRunnable);
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
        public void onItemClick(NotificationListRes.NotificationListData data,Boolean readComment);
    }
}
