package school.campusconnect.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import school.campusconnect.utils.AppLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
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
import school.campusconnect.datamodel.likelist.LikeListData;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;


public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.ImageViewHolder> implements Filterable {
    private List<LikeListData> originalList = new ArrayList<>();
    private List<LikeListData> list = new ArrayList<>();
    private OnLeadSelectListener listener;
    private Context mContext;

    int count;
    Fragment f;

    private FilterNames filterNames;

    public LikeAdapter(List<LikeListData> list, OnLeadSelectListener listener, int count) {
        if (list == null) return;
        this.originalList = list;
        this.list = list;
        this.listener = listener;
        this.count = count;
    }

    public void addItems(List<LikeListData> items) {
//        list.addAll(new ArrayList<LikeListData>(items));
        originalList.addAll(new ArrayList<LikeListData>(items));
       AppLog.e("items ", "count " + list.size());
    }

    public void updateItem(int position, LikeListData LikeListData) {
        originalList.set(position, LikeListData);
        notifyItemChanged(position);
    }

    public void clear() {
        originalList.clear();
        notifyDataSetChanged();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_like, parent, false);
        return new ImageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final LikeListData item = list.get(position);

            DatabaseHandler databaseHandler = new DatabaseHandler(mContext);
            String dispname="";
            if (count != 0) {
                try {
//                   AppLog.e("CONTACTSS", "count is " + databaseHandler.getCount());
                    String name = databaseHandler.getNameFromNum(item.getPhone().replaceAll(" ", ""));
//                   AppLog.e("CONTACTSS", "api name is " + item.getName());
//                   AppLog.e("CONTACTSS", "name is " + name);
//                   AppLog.e("CONTACTSS", "num is " + item.getPhone());
                    if (!name.equals("")) {
                        holder.txtName.setText(name);
                        dispname=name;
                    } else {
                        holder.txtName.setText(item.getName());
                        dispname=item.getName();
                    }
                } catch (NullPointerException e) {
                    holder.txtName.setText(item.getName());
                    dispname=item.getName();
                }
            } else {
               AppLog.e("CONTACTSS", "count is 0");
                holder.txtName.setText(item.getName());
                dispname=item.getName();
            }



            holder.txtCount.setText(item.getPhone());

//
        final String nm=dispname;
        if (item.getImage() != null && !item.getImage().isEmpty()) {
           AppLog.e("LeadAdapter", "Item Not Empty ");
            Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(dpToPx(), dpToPx()).
                    into(holder.imgLead, new Callback() {
                        @Override
                        public void onSuccess() {
                           AppLog.e("LeadAdapter", "Item Not Empty , On Success ");
                            holder.imgLead_default.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError() {
                           AppLog.e("LeadAdapter", "Item Not Empty , On Error");
                            holder.imgLead_default.setVisibility(View.VISIBLE);
                            TextDrawable drawable = TextDrawable.builder()
                                    .buildRound(ImageUtil.getTextLetter(nm), ImageUtil.getRandomColor(position) );
                            holder.imgLead_default.setImageDrawable(drawable);

                           /* Picasso.with(mContext).load(R.drawable.ic_person).
                                    into(holder.imgLead);*/
                        }
                    });
        } else {
           AppLog.e("LeadAdapter", "Item Empty ");
            holder.imgLead_default.setVisibility(View.VISIBLE);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(ImageUtil.getTextLetter(nm), ImageUtil.getRandomColor(position) );
            holder.imgLead_default.setImageDrawable(drawable);

           /* Picasso.with(mContext).load(R.drawable.icon_default_user).
                    into(holder.imgLead, new Callback() {
                        @Override
                        public void onSuccess() {
                           AppLog.e("LeadAdapter", "Item Empty , On Success ");
                        }

                        @Override
                        public void onError() {
                           AppLog.e("LeadAdapter", "Item Empty , On failure ");
                            Picasso.with(mContext).load(R.drawable.icon_default_user).into(holder.imgLead);
                        }
                    });*/
        }

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
                                .buildRect(ImageUtil.getTextLetter(nm), ImageUtil.getRandomColor(position) );
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
        @Bind(R.id.txt_count)
        TextView txtCount;
        @Bind(R.id.img_call)
        ImageView call;
        @Bind(R.id.img_lead)
        ImageView imgLead;

        @Bind(R.id.img_lead_default)
        ImageView imgLead_default;


        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }


        @OnClick({R.id.img_call/*, R.id.img_chat, R.id.relative_name, R.id.img_tree*/})
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_call:
                    listener.onCallClick(list.get(getLayoutPosition()));
                    break;

                /*case R.id.img_chat:
                    listener.onSMSClick(list.get(getLayoutPosition()));
                    break;

                case R.id.img_tree:
                    listener.onMailClick(list.get(getLayoutPosition()));
                    break;

                case R.id.relative_name:
                    listener.onNameClick(list.get(getLayoutPosition()));
                    break;*/
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

            List<LikeListData> filteredList = new ArrayList<>();
            List<LikeListData> list = originalList;
            /*for (int i = 0; i < list.size(); i++) {
            }*/
            for (LikeListData rb : originalList) {
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
            list = (ArrayList<LikeListData>) filterResults.values;
            notifyDataSetChanged();
        }
    }

    public interface OnLeadSelectListener {
        void onCallClick(LikeListData item);

        void onSMSClick(LikeListData item);

        void onMailClick(LikeListData item);

        void onNameClick(LikeListData item);
    }

}


