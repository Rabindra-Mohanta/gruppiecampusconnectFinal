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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.ContactListItem;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ImageViewHolder> implements Filterable {
    private List<ContactListItem> list = new ArrayList<>();
    private List<ContactListItem> originalList = new ArrayList<>();

    public static int OPTION_ADD = 1;
    public static int OPTION_INVITE = 2;
    public static int OPTION_CALL = 3;

    private Context mContext;
    int type = 0;

    OnCallListener listener;

    FilterNames filterNames;

    public ContactListAdapter(OnCallListener listener, List<ContactListItem> list, int type) {
        if (list == null) return;
        this.list = list;
        this.originalList = list;
        this.type = type;
        this.listener = listener;
    }

    public void addItems(List<ContactListItem> items) {
//        list.addAll(new ArrayList<ContactListItem>(items));
        originalList.addAll(new ArrayList<ContactListItem>(items));
       AppLog.e("items ", "count " + list.size());
    }

    public void updateItem(int position, ContactListItem contactListItem) {
        originalList.set(position, contactListItem);
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_gruppiecontact, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, final int position) {
        final ContactListItem item = list.get(position);

        holder.txtName.setText(item.getName());
        holder.txtPhone.setText(item.getPhone());

        holder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onNameClick(list.get(position));
            }
        });

       AppLog.e("ContactList" , "item name : "+item.getName()+"  , image : "+item.getImage()+" , "+(item.getImage() != null));

        if (item.getImage() != null)
        {
           AppLog.e("ContactList" , "image Not Null : "+item.getName()+" , "+(item.getImage() != null));
            Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(dpToPx(), dpToPx()).into(holder.imgLead);
            holder.imgLead_default.setVisibility(View.INVISIBLE);
        }
        else
        {
           AppLog.e("ContactList" , "image Not Null : "+item.getName());
            holder.imgLead_default.setVisibility(View.VISIBLE);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(ImageUtil.getTextLetter(item.getName()), ImageUtil.getRandomColor(position));//, mContext.getResources().getDimensionPixelSize(R.dimen.padding_25dp));
            holder.imgLead_default.setImageDrawable(drawable);
         /*   Picasso.with(mContext).load(R.drawable.icon_default_user)
                    .into(holder.imgLead);*/
        }

        if (type == ContactListAdapter.OPTION_INVITE) {
            holder.ivAdd.setImageResource(R.drawable.btn_invite);
        } else if (type == ContactListAdapter.OPTION_ADD) {
            holder.ivAdd.setImageResource(R.drawable.btn_add);
        } else if (type == ContactListAdapter.OPTION_CALL) {
            holder.ivAdd.setImageResource(R.drawable.ic_phone);
        }

        holder.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCallClick(list.get(position).getPhone());
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

                }
                else
                {

                        TextDrawable drawable = TextDrawable.builder()
                                .buildRect(ImageUtil.getTextLetter(item.getName()), ImageUtil.getRandomColor(position));// mContext.getResources().getDimensionPixelSize(R.dimen.padding_25dp));
                        iv.setImageDrawable(drawable);
                    //Picasso.with(mContext).load(R.drawable.icon_default_user).into(iv);

                }
                settingsDialog.show();
            }
        });

    }

    private int dpToPx() {
        return mContext.getResources().getDimensionPixelSize(R.dimen.group_list_image_size);
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

        @Bind(R.id.txt_gruppie)
        TextView txt_gruppie;

        @Bind(R.id.img_lead)
        ImageView imgLead;

        @Bind(R.id.img_lead_default)
        ImageView imgLead_default;

        @Bind(R.id.img_add)
        ImageView ivAdd;

        @Bind(R.id.txt_number)
        TextView txtPhone;

        @Bind(R.id.relative_name)
        LinearLayout relative_name;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onNameClick(list.get(getAdapterPosition()));
                }
            });
        }

    }

    public interface OnCallListener {
        public void onCallClick(String number);

        public void onNameClick(ContactListItem cli);
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

            List<ContactListItem> filteredList = new ArrayList<>();
            List<ContactListItem> list = originalList;
            for (int i = 0; i < list.size(); i++) {
            }
            for (ContactListItem rb : originalList) {
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
            list = (ArrayList<ContactListItem>) filterResults.values;
            notifyDataSetChanged();
        }
    }


}


