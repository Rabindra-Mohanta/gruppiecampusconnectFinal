package school.campusconnect.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import school.campusconnect.utils.AppLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.UserDetailActivity;
import school.campusconnect.datamodel.UserListItem;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ImageViewHolder> implements Filterable {
    private List<UserListItem> originalList = new ArrayList<>();
    private List<UserListItem> list = new ArrayList<>();

    private Context mContext;
    int type = 0;
    private boolean change;

    private FilterNames filterNames;

    public UserListAdapter(List<UserListItem> list, int type, boolean change)
    {
        if (list == null) return;
        this.originalList = list;
        this.list = list;
        this.type = type;
        this.change = change;
    }


    public void addItems(List<UserListItem> items)
    {
        originalList.addAll(new ArrayList<UserListItem>(items));
       AppLog.e("items ", "count " + originalList.size());
    }

    public void updateItem(int position, UserListItem userListItem)
    {
        originalList.set(position, userListItem);
    }

    public void clear()
    {
        originalList.clear();
        list.clear();
        notifyDataSetChanged();
    }
    private int dpToPx() {
        return mContext.getResources().getDimensionPixelSize(R.dimen.group_list_image_size);
    }
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_users, parent, false);
        return new ImageViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ImageViewHolder holder, final int position) {
        final UserListItem item = originalList.get(position);

        /*DatabaseHandler databaseHandler = new DatabaseHandler(mContext);

        if (databaseHandler.getCount() != 0) {
            try {
               AppLog.e("CONTACTSS", "count is " + databaseHandler.getCount());
                String name = databaseHandler.getNameFromNum(item.getPhone().replaceAll(" ", ""));
               AppLog.e("CONTACTSS", "api name is " + item.getName());
               AppLog.e("CONTACTSS", "name is " + name);
               AppLog.e("CONTACTSS", "num is " + item.getPhone());
                if (!name.equals("")) {
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
        }*/

        holder.txtName.setText(item.getName());
        holder.txtPhone.setText(item.getPhone());
        if (item.getImage() != null) {
            holder.imgLead_default.setVisibility(View.INVISIBLE);
            Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(dpToPx(), dpToPx()).into(holder.imgLead);
        } else {
            holder.imgLead_default.setVisibility(View.VISIBLE);

                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(item.getName()), ImageUtil.getRandomColor(position));
                holder.imgLead_default.setImageDrawable(drawable);
                        //Picasso.with(mContext).load(R.drawable.icon_default_user).into(holder.imgLead);
        }

        holder.relative_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserListItem userListItem = originalList.get(position);
                Intent intent = new Intent(mContext, UserDetailActivity.class);
                Bundle b = new Bundle();
                b.putParcelable("user_details", userListItem);
                intent.putExtras(b);
                intent.putExtra("post", userListItem.getIsPost());
                intent.putExtra("change", change);
               AppLog.e("Change", "change is " + change);
                mContext.startActivity(intent);

            }
        });


        holder.imgLead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog settingsDialog = new Dialog(mContext);
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                View newView = (View) inflater.inflate(R.layout.img_layout, null);
                settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(newView);

                settingsDialog.show();
                ImageView iv = (ImageView) newView.findViewById(R.id.img_popup);

                if (item.getImage() != null && !item.getImage().isEmpty()) {
                    Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).into(iv);

                } else {
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(ImageUtil.getTextLetter(item.getName()), ImageUtil.getRandomColor(position) );
                   iv.setImageDrawable(drawable);
                    //Picasso.with(mContext).load(R.drawable.icon_default_user).into(iv);

                }
                settingsDialog.show();
            }
        });

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

        @Bind(R.id.img_lead)
        ImageView imgLead;

        @Bind(R.id.img_lead_default)
        ImageView imgLead_default;

        @Bind(R.id.txt_phone)
        TextView txtPhone;

        @Bind(R.id.relative_name)
        RelativeLayout relative_name;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    @Override
    public Filter getFilter() {
        filterNames = new FilterNames();
        return filterNames;
    }

    private class FilterNames extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();

            List<UserListItem> filteredList = new ArrayList<>();
            List<UserListItem> list = originalList;
            for (int i = 0; i < list.size(); i++) {
            }
            for (UserListItem rb : originalList) {
                if ((rb.getName().trim().toLowerCase()).contains(charSequence.toString())) {
                    filteredList.add(rb);
                }
            }
            list = filteredList;
            results.values = list;
            results.count = list.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            list = (ArrayList<UserListItem>) filterResults.values;
            notifyDataSetChanged();
        }
    }


}


