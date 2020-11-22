package school.campusconnect.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import school.campusconnect.utils.AppLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.datamodel.notifications.NotificationItem;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.MixOperations;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ImageViewHolder> {

    private List<NotificationItem> list = Collections.emptyList();

    Context context;

    int count;

    OnNotificationClick onNotificationClick;

    public void setOnNotificationClick(OnNotificationClick onNotificationClick) {
        this.onNotificationClick = onNotificationClick;
    }

    public NotificationListAdapter(Context context, List<NotificationItem> list, int count) {
        if (list == null)
            return;
        this.list = list;
        this.count = count;
        this.context = context;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notifications, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final NotificationItem item = list.get(position);

//        if (position < unseenCount) {
        if (item.isSeen())
            holder.linMain.setBackgroundColor(context.getResources().getColor(R.color.seen));
        else
            holder.linMain.setBackgroundColor(context.getResources().getColor(R.color.unseen));
//        }

        DatabaseHandler databaseHandler = new DatabaseHandler(context);

        if (count != 0) {
            try {
//               AppLog.e("CONTACTSS", "count is " + databaseHandler.getCount());
                String name = databaseHandler.getNameFromNum(item.getPhone().replaceAll(" ", ""));
//               AppLog.e("CONTACTSS", "api name is " + item.getStrName());
//               AppLog.e("CONTACTSS", "name is " + name);
//               AppLog.e("CONTACTSS", "num is " + item.getPhone());
                if (!name.equals("")) {
                    String body = item.getStrTitle().replace(item.getStrName(), name);
//                    String fullBody = name + body;
                    holder.txtName.setText(body);
                } else {
                    holder.txtName.setText(item.getStrTitle());
                }
            } catch (NullPointerException e) {
                holder.txtName.setText(item.getStrTitle());
            }
        } else {
           AppLog.e("CONTACTSS", "count is 0");
            holder.txtName.setText(/*item.getStrName() + " " + */item.getStrTitle());
        }

//        holder.txtName.setText(item.getStrTitle());
        holder.date_time.setText(MixOperations.getFormattedDate(item.getDateTime(),"yyyy-MM-dd hh:mma"));
        try {
            if (!item.getStrIcon().equals(""))
                Picasso.with(context).load(item.getStrIcon()).into(holder.iv_icon, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.iv_icon_default.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {
                        holder.iv_icon_default.setVisibility(View.VISIBLE);
                        TextDrawable drawable = TextDrawable.builder()
                                .buildRound(ImageUtil.getTextLetter(item.getStrName()), ImageUtil.getRandomColor(position) );
                        holder.iv_icon_default.setImageDrawable(drawable);
                        //Picasso.with(context).load(R.drawable.icon_default_user).into(holder.iv_icon);
                    }
                });
        } catch (NullPointerException e) {
           AppLog.e("Noti Image", "error is " + e.toString());
            holder.iv_icon_default.setVisibility(View.VISIBLE);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(ImageUtil.getTextLetter(item.getStrName()), ImageUtil.getRandomColor(position) );
            holder.iv_icon_default.setImageDrawable(drawable);
           // Picasso.with(context).load(R.drawable.icon_default_user).into(holder.iv_icon);
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

        @Bind(R.id.txt_name)
        TextView txtName;

        @Bind(R.id.date_time)
        TextView date_time;

        @Bind(R.id.iv_icon)
        ImageView iv_icon;

        @Bind(R.id.iv_icon_default)
        ImageView iv_icon_default;

        @Bind(R.id.linMain)
        LinearLayout linMain;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNotificationClick.onNotiClick(getAdapterPosition());
                }
            });
        }
    }

    public interface OnNotificationClick {

        void onNotiClick(int position);

    }

}


