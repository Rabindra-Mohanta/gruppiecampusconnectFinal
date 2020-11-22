package school.campusconnect.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import school.campusconnect.utils.AppLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.PhoneContactsItems;

public class MyContactListAdapter extends RecyclerView.Adapter<MyContactListAdapter.ImageViewHolder> {
    private List<PhoneContactsItems> list;
    private Context mContext;
    OnItemClickListener listener;


    public MyContactListAdapter(OnItemClickListener listener, List<PhoneContactsItems> list) {
        this.list = list;
        this.listener = listener;
    }
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_gruppiecontact, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final PhoneContactsItems items = list.get(position);

        holder.cb.setVisibility(View.VISIBLE);

        holder.imgLead.setVisibility(View.GONE);
        holder.ivAdd.setVisibility(View.GONE);

        if (items.isSelected) {
            holder.cb.setChecked(true);
           AppLog.e("MULTI_ADD", " adapter isSelected is true");
        } else {
            holder.cb.setChecked(false);
           AppLog.e("MULTI_ADD", "adapter isSelected is false");
        }

        holder.txtName.setText(items.getName());
        holder.txtPhone.setText(items.getPhone());
        holder.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onInvite(new MyContact(items.getName(), items.getPhone()));
            }
        });

        holder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.friendSelected(holder.cb.isChecked(), items, holder.getAdapterPosition());
            }
        });

        holder.relMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.cb.isChecked()) {
                    holder.cb.setChecked(false);
                    listener.friendSelected(false, items, holder.getAdapterPosition());
                } else {
                    holder.cb.setChecked(true);
                    listener.friendSelected(true, items, holder.getAdapterPosition());
                }
            }
        });
        holder.ivAdd.setVisibility(View.GONE);

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

        @Bind(R.id.img_add)
        ImageView ivAdd;

        @Bind(R.id.txt_number)
        TextView txtPhone;

        @Bind(R.id.relative_name)
        LinearLayout relative_name;

        @Bind(R.id.cb)
        CheckBox cb;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public interface OnItemClickListener {
        void onInvite(MyContact myContact);

        void friendSelected(boolean isSelected, PhoneContactsItems item, int position);
    }

    public class MyContact {
        String name;
        String number;

        public MyContact(String s, String s1) {
            this.name = s;
            this.number = s1;
        }

        public String getName() {
            return name;
        }

        public String getNumber() {
            return number;
        }
    }

    public void updateItem(int position, PhoneContactsItems items) {
        list.set(position, items);
    }


}


