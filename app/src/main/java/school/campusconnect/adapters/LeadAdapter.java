package school.campusconnect.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.datamodel.LeadItem;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;


public class LeadAdapter extends RecyclerView.Adapter<LeadAdapter.ImageViewHolder> implements Filterable {
    private static final String TAG = "LeadAdapter";
    private  boolean isNest;
    private  DatabaseHandler databaseHandler;
    private List<LeadItem> originalList = new ArrayList<>();
    private List<LeadItem> list = new ArrayList<>();
    private OnLeadSelectListener listener;
    private Context mContext;
    int type = 0;
    int count;

    private FilterNames filterNames;

    boolean itemClick;

    public LeadAdapter(List<LeadItem> list, OnLeadSelectListener listener, int type, DatabaseHandler databaseHandler, int count, boolean itemClick, boolean isNest ) {
        if (list == null) return;
        this.originalList = list;
        this.list = list;
        this.listener = listener;
        this.type = type;
        this.count = count;
        this.itemClick=itemClick;
        this.databaseHandler=databaseHandler;
        this.isNest=isNest;
    }

    public void addItems(List<LeadItem> items) {
        originalList.addAll(new ArrayList<LeadItem>(items));
       AppLog.e(TAG, "item count " + list.size());
    }

    public void updateItem(int position, LeadItem leadItem) {
        originalList.set(position, leadItem);
        notifyItemChanged(position);
    }

    public void clear() {
        originalList.clear();
        notifyDataSetChanged();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_lead, parent, false);
        return new ImageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final LeadItem item = list.get(position);

        if (count != 0) {
            try {
                String name = databaseHandler.getNameFromNum(item.phone.replaceAll(" ", ""));
                if (!TextUtils.isEmpty(name)) {
                    item.name = name;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        holder.txtName.setText(item.name+(item.staff?" (S)":""));
        if(isNest)
        {
            holder.txtCount.setText("Teams : " + item.getTeamCount());
            holder.txtCount.setVisibility(View.VISIBLE);
        }
        else {
            holder.txtCount.setVisibility(View.GONE);
        }


        final String name= item.name;

        if (!TextUtils.isEmpty(item.getImage()))
        {
           AppLog.e("LeadAdapter", "Item Not Empty +"+item.getName()+" , "+item.getImage());
            holder.imgLead_default.setVisibility(View.INVISIBLE);
            Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(dpToPx(), dpToPx()).
                    into(holder.imgLead, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.imgLead_default.setVisibility(View.INVISIBLE);
                           AppLog.e("LeadAdapter", "Item Not Empty , On Success ");
                        }

                        @Override
                        public void onError() {
                           AppLog.e("LeadAdapter", "Item Not Empty , On Error");

                            holder.imgLead_default.setVisibility(View.VISIBLE);
                            TextDrawable drawable = TextDrawable.builder()
                                    .buildRound(ImageUtil.getTextLetter(name), ImageUtil.getRandomColor(position) );
                            holder.imgLead_default.setImageDrawable(drawable);
                        }
                    });
        } else {
           AppLog.e("LeadAdapter", "Item Empty ");
            holder.imgLead_default.setVisibility(View.VISIBLE);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(ImageUtil.getTextLetter(name), ImageUtil.getRandomColor(position) );
            holder.imgLead_default.setImageDrawable(drawable);
        }

        holder.imgLead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog settingsDialog = new Dialog(mContext);
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                View newView = inflater.inflate(R.layout.img_layout, null);
                settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(newView);

                settingsDialog.show();
                ImageView iv = newView.findViewById(R.id.img_popup);

                if (item.getImage() != null && !item.getImage().isEmpty()) {
                    Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).into(iv);
                } else {
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(ImageUtil.getTextLetter(name), ImageUtil.getRandomColor(position) );
                    iv.setImageDrawable(drawable);
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
        @Bind(R.id.txt_count)
        TextView txtCount;
        @Bind(R.id.img_lead)
        ImageView imgLead;
        @Bind(R.id.img_lead_default)
        ImageView imgLead_default;
        @Bind(R.id.img_chat)
        ImageView chat;
        @Bind(R.id.img_tree)
        ImageView tree;
        @Bind(R.id.line)
        View line;
        @Bind(R.id.relative)
        RelativeLayout relative;
        @Bind(R.id.relative_name)
        RelativeLayout relative_name;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if(itemClick)
            {
                chat.setVisibility(View.INVISIBLE);
                relative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onSMSClick(list.get(getLayoutPosition()));
                    }
                });
                relative_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onSMSClick(list.get(getLayoutPosition()));
                    }
                });

                tree.setVisibility(View.GONE);
                line.setVisibility(View.GONE);
            }
            else
            {
                chat.setVisibility(View.VISIBLE);
                tree.setVisibility(View.GONE);
                line.setVisibility(View.GONE);

                if(isNest)
                {
                    tree.setVisibility(View.VISIBLE);
                    line.setVisibility(View.VISIBLE);
                }
                relative_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onNameClick(list.get(getLayoutPosition()));
                    }
                });


            }
        }


        @OnClick({ R.id.img_chat, R.id.img_tree})
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_chat:
                    listener.onSMSClick(list.get(getLayoutPosition()));
                    break;

                case R.id.img_tree:
                    listener.onMailClick(list.get(getLayoutPosition()));
                    break;
                case R.id.relative_name:
                    listener.onNameClick(list.get(getLayoutPosition()));
                    break;
            }
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

            List<LeadItem> filteredList = new ArrayList<>();
            List<LeadItem> list = originalList;
            for (LeadItem rb : originalList) {
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
            list = (ArrayList<LeadItem>) filterResults.values;
            notifyDataSetChanged();
        }
    }

    public interface OnLeadSelectListener {
        void onCallClick(LeadItem item);

        void onSMSClick(LeadItem item);

        void onMailClick(LeadItem item);

        void onNameClick(LeadItem item);
    }

}

