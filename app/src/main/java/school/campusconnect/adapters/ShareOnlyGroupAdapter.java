package school.campusconnect.adapters;

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
import school.campusconnect.datamodel.sharepost.ShareGroupItem;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.views.SMBDialogUtils;

/**
 * Created by frenzin04 on 2/7/2017.
 */

public class ShareOnlyGroupAdapter extends RecyclerView.Adapter<ShareOnlyGroupAdapter.ImageViewHolder> {

    private List<ShareGroupItem> list = Collections.emptyList();
    private Context mContext;
    String type;
    ShareGroupItem item;
    String mGroupId;
    String mPostId;
    FinishActivity finishActivity;

    public ShareOnlyGroupAdapter(List<ShareGroupItem> list, FinishActivity finishActivity, String mGroupId, String mPostId) {
        if (list == null) return;
        this.list = list;
        this.mGroupId = mGroupId;
        this.mPostId = mPostId;
        this.finishActivity = finishActivity;
    }

    public ShareOnlyGroupAdapter() {

    }

    public void addItems(List<ShareGroupItem> items) {
        list.addAll(new ArrayList<ShareGroupItem>(items));
       AppLog.e("items ", "count " + list.size());
    }

    @Override
    public ShareOnlyGroupAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_share, parent, false);
        //videoView = (VideoView) view.findViewById(R.id.videoview);
        return new ShareOnlyGroupAdapter.ImageViewHolder(view);
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {

        item = list.get(position);

        holder.txtName.setText(item.name);

        holder.cb.setVisibility(View.GONE);

        holder.txtName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow, 0);

        if (item.image != null && !item.image.isEmpty()) {
            Picasso.with(mContext).load(item.image).networkPolicy(NetworkPolicy.OFFLINE).into(holder.iv_icon,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.iv_icon_default.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError() {
                            Picasso.with(mContext).load(item.image).into(holder.iv_icon, new Callback() {
                                @Override
                                public void onSuccess() {
                                    holder.iv_icon_default.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onError() {
                                    holder.iv_icon_default.setVisibility(View.VISIBLE);
                                    TextDrawable drawable = TextDrawable.builder()
                                            .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position) );
                                    holder.iv_icon_default.setImageDrawable(drawable);
                                   AppLog.e("Picasso", "Error : ");
                                }
                            });
                        }
                    });
        } else {
            holder.iv_icon_default.setVisibility(View.VISIBLE);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position) );
            holder.iv_icon_default.setImageDrawable(drawable);
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
                   finishActivity.onClickListener(list.get(getAdapterPosition()).id);
                }
            });
        }

    }

    public List<ShareGroupItem> getList() {
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

        void finish();

        void onClickListener(String id);

    }

}
