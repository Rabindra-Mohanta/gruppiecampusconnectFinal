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
import school.campusconnect.activities.ShareGroupListActivity;
import school.campusconnect.activities.SharePersonalNameListActivity;
import school.campusconnect.activities.PushActivity;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.datamodel.sharepost.ShareGroupItemList;
import school.campusconnect.fragments.SharePersonalNameListFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.views.SMBDialogUtils;

/**
 * Created by frenzin04 on 2/6/2017.
 */

public class ShareGroupAdapter extends RecyclerView.Adapter<ShareGroupAdapter.ImageViewHolder> {
    private List<ShareGroupItemList> list = Collections.emptyList();
    private Context mContext;
    String type;
    ShareGroupItemList item;
    int mGroupId;
    int mTeamId;
    int count;

    public ShareGroupAdapter(List<ShareGroupItemList> list, String type, int count) {
        if (list == null) return;
        this.list = list;
        this.type = type;
        this.count = count;

    }

    public ShareGroupAdapter() {

    }

    public void addItems(List<ShareGroupItemList> items) {
        list.addAll(new ArrayList<ShareGroupItemList>(items));
       AppLog.e("items ", "count " + list.size());
    }

    @Override
    public ShareGroupAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_share, parent, false);
        //videoView = (VideoView) view.findViewById(R.id.videoview);
        return new ShareGroupAdapter.ImageViewHolder(view);
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ShareGroupAdapter.ImageViewHolder holder, final int position) {

        item = list.get(position);

        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);

        if (count != 0) {
            try {
//               AppLog.e("CONTACTSS", "count is " + databaseHandler.getCount());
                String name = databaseHandler.getNameFromNum(item.getPhone().replaceAll(" ", ""));
//               AppLog.e("CONTACTSS", "api name is " + item.getName());
//               AppLog.e("CONTACTSS", "name is " + name);
//               AppLog.e("CONTACTSS", "num is " + item.getPhone());
                if (!name.equals("")) {
                    if (type.equals("team")) {
                        if (!item.getName().equalsIgnoreCase("My Team"))
                            holder.txtName.setText(name + " Team");
                        else
                            holder.txtName.setText(item.getName());
                    } else
                        holder.txtName.setText(name);
                } else {
                    holder.txtName.setText(item.getName());
                }
            } catch (NullPointerException e) {
                holder.txtName.setText(item.getName());
            }
        } else {
           AppLog.e("CONTACTSS", "count is 0");
            holder.txtName.setText(item.getName());
        }

//        holder.txtName.setText(item.getName());

        if (item.isSelected()) {
            holder.cb.setChecked(true);
        } else {
            holder.cb.setChecked(false);
        }

        if (item.getImage() != null && !item.getImage().isEmpty()) {
            Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).networkPolicy(NetworkPolicy.OFFLINE).resize(dpToPx(), dpToPx()).into(holder.iv_icon,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.iv_icon_default.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError() {
                            Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).into(holder.iv_icon, new Callback() {
                                @Override
                                public void onSuccess() {
                                    holder.iv_icon_default.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onError() {
                                    holder.iv_icon_default.setVisibility(View.VISIBLE);
                                    TextDrawable drawable = TextDrawable.builder()
                                            .buildRound(ImageUtil.getTextLetter(item.getName()), ImageUtil.getRandomColor(position) );
                                    holder.iv_icon_default.setImageDrawable(drawable);
                                   AppLog.e("Picasso", "Error : ");
                                }
                            });
                        }
                    });
        } else {
            holder.iv_icon_default.setVisibility(View.VISIBLE);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(ImageUtil.getTextLetter(item.getName()), ImageUtil.getRandomColor(position) );
            holder.iv_icon_default.setImageDrawable(drawable);
            //Picasso.with(mContext).load(R.drawable.icon_default_user).into(holder.iv_icon);
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

            txtName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cb.performClick();
                }
            });

            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        list.get(getAdapterPosition()).setSelected(true);
                        SharePersonalNameListFragment.list_id.add(list.get(getAdapterPosition()).getId());

                        if(mContext instanceof ShareGroupListActivity)
                        {
                            ShareGroupListActivity.selectCount++;
                            ((ShareGroupListActivity)mContext).updateCount();
                        }
                        if(mContext instanceof SharePersonalNameListActivity)
                        {
                            SharePersonalNameListActivity.selectCount++;
                            ((SharePersonalNameListActivity)mContext).updateCount();
                        }
                        if(mContext instanceof PushActivity)
                        {
                            PushActivity.selectCount++;
                            ((PushActivity)mContext).updateCount();
                        }




                    } else {
                        list.get(getAdapterPosition()).setSelected(false);
                        SharePersonalNameListFragment.removeSelectedId(list.get(getAdapterPosition()).getId());

                        if(mContext instanceof ShareGroupListActivity)
                        {
                            ShareGroupListActivity.selectCount--;
                            ((ShareGroupListActivity)mContext).updateCount();
                        }
                        if(mContext instanceof SharePersonalNameListActivity)
                        {
                            SharePersonalNameListActivity.selectCount--;
                            ((SharePersonalNameListActivity)mContext).updateCount();
                        }
                        if(mContext instanceof PushActivity)
                        {
                            PushActivity.selectCount--;
                            ((PushActivity)mContext).updateCount();
                        }

                    }
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

    public void setAllFalse() {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSelected(false);
        }
        notifyDataSetChanged();
    }

    public String getSelectedgroups() {
        String groupsId = "";
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isSelected()) {
                groupsId = groupsId + list.get(i).getId() + ",";
            }
        }
        if (groupsId.length() > 0) {
            groupsId = groupsId.substring(0, groupsId.length() - 1);
        }
        return groupsId;
    }

    public int getSelectedCount() {
        int groupsId=0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isSelected()) {
                groupsId++;
            }
        }
        return groupsId;
    }

    private int dpToPx() {
        return mContext.getResources().getDimensionPixelSize(R.dimen.group_list_image_size);
    }

}