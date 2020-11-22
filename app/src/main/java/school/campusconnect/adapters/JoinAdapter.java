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
import android.widget.CompoundButton;
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
import school.campusconnect.datamodel.join.JoinItem;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.views.SMBDialogUtils;

/**
 * Created by frenzin04 on 2/7/2017.
 */

public class JoinAdapter extends RecyclerView.Adapter<JoinAdapter.ImageViewHolder> {

    private List<JoinItem> list = Collections.emptyList();
    private Context mContext;
    String type;

    int count;
    CheckBoxClick listener;

    public JoinAdapter(List<JoinItem> list, CheckBoxClick listener, int count) {
        if (list == null) return;
        this.list = list;
        this.listener = listener;
        this.count = count;
    }

    public JoinAdapter() {

    }

    public void addItems(List<JoinItem> items) {
        list.addAll(new ArrayList<JoinItem>(items));
       AppLog.e("items ", "count " + list.size());
    }

    @Override
    public JoinAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_join, parent, false);
        //videoView = (VideoView) view.findViewById(R.id.videoview);
        return new JoinAdapter.ImageViewHolder(view);
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {

        final JoinItem  item = list.get(position);

       /* String dispName = "";
        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);
        if (count != 0) {
            try {
//               AppLog.e("CONTACTSS", "count is " + databaseHandler.getCount());
                String name = databaseHandler.getNameFromNum(item.adminPhone.replaceAll(" ", ""));

                if (!name.equals("")) {
                    holder.txtName.setText(name);
                    dispName = name;
                } else {
                    holder.txtName.setText(item.name);
                    dispName = item.name;
                }
            } catch (NullPointerException e) {
                holder.txtName.setText(item.name);
                dispName = item.name;
            }
        } else {
           AppLog.e("CONTACTSS", "count is 0");
            holder.txtName.setText(item.name);
        }
*/
        holder.txtName.setText(item.name);
        holder.txt_mob.setText(item.phone);

        if (item.selected) {
            holder.cb.setChecked(true);
        } else {
            holder.cb.setChecked(false);
        }

       AppLog.e("ReportResponse" , "OnBind image : "+item.image);

        /*holder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickListener(holder.cb.isChecked(), item, holder.getAdapterPosition());
            }
        });*/

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
                                            .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position));
                                    holder.iv_icon_default.setImageDrawable(drawable);
                                   AppLog.e("Picasso", "Error : ");
                                }
                            });
                        }
                    });
        } else {
            holder.iv_icon_default.setVisibility(View.VISIBLE);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position));
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

        @Bind(R.id.txt_mob)
        TextView txt_mob;

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
                    cb.performClick();
                }
            });

            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        list.get(getAdapterPosition()).selected = true;
//                        SharePersonalNameListFragment.list_id.add(list.get(getAdapterPosition()).getId());
                        listener.onClickListener(true, list.get(getAdapterPosition()), getAdapterPosition());

                    } else {
                        list.get(getAdapterPosition()).selected = false;
//                        SharePersonalNameListFragment.removeSelectedId(list.get(getAdapterPosition()).getId());
                        listener.onClickListener(false, list.get(getAdapterPosition()), getAdapterPosition());

                    }
                }
            });

        }

    }

    public List<JoinItem> getList() {
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

    public interface CheckBoxClick {

        void onClickListener(boolean checked, JoinItem item, int position);

    }

    public void updateItem(int position, JoinItem items) {
        list.set(position, items);
    }

}
