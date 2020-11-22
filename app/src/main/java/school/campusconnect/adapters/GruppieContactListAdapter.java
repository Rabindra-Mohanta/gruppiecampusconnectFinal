package school.campusconnect.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import school.campusconnect.utils.AppLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.datamodel.GruppieContactListItem;
import school.campusconnect.utils.ImageUtil;

public class GruppieContactListAdapter extends RecyclerView.Adapter<GruppieContactListAdapter.ImageViewHolder> {
    private List<GruppieContactListItem> list = new ArrayList<>();

    private Context mContext;
    int type = 0;
    int count;
    OnAddFriendListener listener;

    public GruppieContactListAdapter(OnAddFriendListener listener, List<GruppieContactListItem> list, int type, int count)
    {
        if (list == null) return;
        this.list = list;
        this.type = type;
        this.count = count;
        this.listener = listener;
    }

    public void addItems(List<GruppieContactListItem> items) {
       AppLog.e("items ", "count " + items.size());
        list.addAll(new ArrayList<GruppieContactListItem>(items));
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public void updateItem(int position, GruppieContactListItem items) {
        list.set(position, items);
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_gruppiecontact, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final GruppieContactListItem item = list.get(position);

        holder.cb.setVisibility(View.VISIBLE);

        holder.ivAdd.setVisibility(View.GONE);

        if (item.isSelected) {
            holder.cb.setChecked(true);
           AppLog.e("MULTI_ADD", " adapter isSelected is true");
        } else {
            holder.cb.setChecked(false);
           AppLog.e("MULTI_ADD", "adapter isSelected is false");

        }

        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);

        if (count != 0) {
            try {
//               AppLog.e("CONTACTSS", "count is " + databaseHandler.getCount());
                String name = databaseHandler.getNameFromNum(item.getPhone().replaceAll(" ", ""));
//               AppLog.e("CONTACTSS", "api name is " + item.getName());
//               AppLog.e("CONTACTSS", "name is " + name);
//               AppLog.e("CONTACTSS", "num is " + item.getPhone());
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
        }


//        holder.txtName.setText(item.getName());

        holder.txtPhone.setText(item.getPhone());

        if (item.getImage() != null) {
            Picasso.with(mContext).load(item.getImage()).into(holder.imgLead);
        } else {
            holder.imgLead_default.setVisibility(View.VISIBLE);

                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(item.getName()), ImageUtil.getRandomColor(position));
                holder.imgLead_default.setImageDrawable(drawable);

           // Picasso.with(mContext).load(R.drawable.icon_default_user).into(holder.imgLead);
        }

        holder.ivAdd.setImageResource(R.drawable.btn_add);

        holder.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAddFriendToGroup(list.get(position).getId());
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
                    Picasso.with(mContext).load(item.getImage()).into(iv);

                } else
                    {

                        TextDrawable drawable = TextDrawable.builder()
                                .buildRect(ImageUtil.getTextLetter(item.getName()), ImageUtil.getRandomColor(position) );
                        iv.setImageDrawable(drawable);
                    //Picasso.with(mContext).load(R.drawable.icon_default_user).into(iv);

                }
                settingsDialog.show();
            }
        });

        holder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.friendSelected(holder.cb.isChecked(), item, holder.getAdapterPosition());
            }
        });

        holder.relMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.cb.isChecked()) {
                    holder.cb.setChecked(false);
                    listener.friendSelected(false, item, holder.getAdapterPosition());
                } else {
                    holder.cb.setChecked(true);
                    listener.friendSelected(true, item, holder.getAdapterPosition());
                }
            }
        });

       /* holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });*/

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

        @Bind(R.id.relMain)
        RelativeLayout relMain;

        @Bind(R.id.txt_name)
        TextView txtName;

        @Bind(R.id.img_lead)
        ImageView imgLead;

        @Bind(R.id.img_lead_default)
        ImageView imgLead_default;

        @Bind(R.id.txt_number)
        TextView txtPhone;

        @Bind(R.id.img_add)
        ImageView ivAdd;

        @Bind(R.id.cb)
        CheckBox cb;

        @Bind(R.id.relative_name)
        LinearLayout relative_name;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public interface OnAddFriendListener {

        void onAddFriendToGroup(String fid);

        void friendSelected(boolean isChecked, GruppieContactListItem item, int position);

    }


}


