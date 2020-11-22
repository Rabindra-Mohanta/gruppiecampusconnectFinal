package school.campusconnect.adapters.shareadapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.recyclerview.widget.RecyclerView;
import school.campusconnect.utils.AppLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.sharepost.ShareGroupItemList;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.views.SMBDialogUtils;

/**
 * Created by frenzin04 on 2/7/2017.
 */

public class ActionShareGroupAdapter extends RecyclerView.Adapter<ActionShareGroupAdapter.ImageViewHolder> {

    private List<ShareGroupItemList> list = Collections.emptyList();
    private Context mContext;
    String type;
    ShareGroupItemList item;
    int mGroupId;
    int mPostId;
    FinishActivity finishActivity;

    public ActionShareGroupAdapter(List<ShareGroupItemList> list, FinishActivity finishActivity) {
        if (list == null) return;
        this.list = list;
        this.mGroupId = mGroupId;
        this.mPostId = mPostId;
        this.finishActivity = finishActivity;
    }


    public void addItems(List<ShareGroupItemList> items) {
        list.addAll(new ArrayList<ShareGroupItemList>(items));
       AppLog.e("items ", "count " + list.size());
    }

    @Override
    public ActionShareGroupAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_share, parent, false);
        //videoView = (VideoView) view.findViewById(R.id.videoview);
        return new ActionShareGroupAdapter.ImageViewHolder(view);
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {

        item = list.get(position);

        holder.txtName.setText(item.getName());

        holder.cb.setVisibility(View.GONE);

        holder.txtName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow, 0);

        if (item.getImage() != null && !item.getImage().isEmpty()) {
            holder.iv_icon_default.setVisibility(View.INVISIBLE);
            Picasso.with(mContext).load(item.getImage()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.iv_icon,
                    new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError()
                        {
                            Picasso.with(mContext).load(item.getImage()).into(holder.iv_icon, new Callback()
                            {
                                @Override
                                public void onSuccess()
                                {
                                    holder.iv_icon_default.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onError()
                                {
                                    holder.iv_icon_default.setVisibility(View.VISIBLE);
                                    TextDrawable drawable = TextDrawable.builder()
                                            .buildRound(ImageUtil.getTextLetter(item.getName()), ImageUtil.getRandomColor(position) );
                                    holder.iv_icon_default.setImageDrawable(drawable);
                                   AppLog.e("Picasso", "Error : ");
                                }
                            });
                        }
                    });
        }
        else
        {
            holder.iv_icon_default.setVisibility(View.VISIBLE);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(ImageUtil.getTextLetter(item.getName()), ImageUtil.getRandomColor(position) );
            holder.iv_icon_default.setImageDrawable(drawable);
           // Picasso.with(mContext).load(R.drawable.icon_default_user).transform(new RoundedTransformation(25, 8)).into(holder.iv_icon);
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

        @Bind(R.id.cb)
        CheckBox cb;

        @Bind(R.id.txt_name)
        TextView txtName;

        @Bind(R.id.iv_icon)
        ImageView iv_icon;

        @Bind(R.id.iv_icon_default)
        ImageView iv_icon_default;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   finishActivity.onClickListener(list.get(getAdapterPosition()).getId());
                }
            });
        }

    }

    public List<ShareGroupItemList> getList() {
        return list;
    }

    public boolean isConnectionAvailable() {
        ConnectivityManager connectivityManager;
        NetworkInfo networkinfo;
        connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkinfo = connectivityManager.getActiveNetworkInfo();
        return (networkinfo != null && networkinfo.isConnected());

    }

    public void showNoNetworkMsg() {
        SMBDialogUtils.showSMBDialogOK((Activity) mContext, mContext.getString(R.string.no_internet), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //onBackPressed();
            }
        });
    }

    public interface FinishActivity {

        void onClickListener(String id);

    }

}
