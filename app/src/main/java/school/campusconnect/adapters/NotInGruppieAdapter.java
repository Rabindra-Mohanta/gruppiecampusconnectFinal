package school.campusconnect.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import school.campusconnect.utils.AppLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.notingruppie.NotInGruppieData;

public class NotInGruppieAdapter extends RecyclerView.Adapter<NotInGruppieAdapter.ImageViewHolder> implements Filterable {
    private List<NotInGruppieData> list = new ArrayList<>();
    private List<NotInGruppieData> originalList = new ArrayList<>();
    private List<String> numbers = new ArrayList<>();

    private Context mContext;
    OnItemClickListener listener;

    private FilterNames filterNames;

    public NotInGruppieAdapter(OnItemClickListener listener, List<NotInGruppieData> list) {
        this.list = list;
        this.originalList = list;
        //this.numbers = list2;
        this.listener = listener;
    }


    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_gruppiecontact, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final NotInGruppieData items = list.get(position);

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

        holder.txtName.setText(items.name);
        holder.txtPhone.setText(items.phone);
        holder.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onInvite(new MyContact(items.name, items.phone));
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

/*
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

                if (item.getImage() != null && !item.getImage().isEmpty())
                {
                    Picasso.with(mContext).load(item.getImage()).into(iv);

                }
                else
                {
                    Picasso.with(mContext).load(R.drawable.icon_default_user).into(iv);

                }
                settingsDialog.show();
            }
        });*/

//        holder.ivAdd.setImageResource(R.drawable.btn_invite);
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

        void friendSelected(boolean isSelected, NotInGruppieData item, int position);
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

    public void updateItem(int position, NotInGruppieData items) {
        list.set(position, items);
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

            List<NotInGruppieData> filteredList = new ArrayList<>();
            List<NotInGruppieData> list = originalList;
            for (int i = 0; i < list.size(); i++) {
            }
            for (NotInGruppieData rb : originalList) {
                if ((rb.name.trim().toLowerCase()).contains(charSequence.toString())) {
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
            list = (ArrayList<NotInGruppieData>) filterResults.values;
            notifyDataSetChanged();
        }
    }

}


