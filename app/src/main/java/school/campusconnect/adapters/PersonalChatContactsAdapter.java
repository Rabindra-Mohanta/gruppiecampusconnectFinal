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
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.datamodel.personalchat.PersonalPostItem;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.MixOperations;


public class PersonalChatContactsAdapter extends RecyclerView.Adapter<PersonalChatContactsAdapter.ImageViewHolder> {
    private List<PersonalPostItem> list = new ArrayList<>();
    private OnChatSelected listener;
    private Context mContext;
    private int count;
    private String todayDate;

    public PersonalChatContactsAdapter(List<PersonalPostItem> list, OnChatSelected listener, int count, String todayDate) {
        if (list == null) return;
        this.list = list;
        this.listener = listener;
        this.count = count;
        this.todayDate = todayDate;
    }

    public void addItems(List<PersonalPostItem> items) {
        list.addAll(new ArrayList<PersonalPostItem>(items));
       AppLog.e("items ", "count " + list.size());
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_personal_contacts_chat, parent, false);
        return new ImageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final PersonalPostItem item = list.get(position);

        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);

        if (count != 0) {
            try {
//               AppLog.e("CONTACTSS", "count is " + databaseHandler.getCount());
                String name = databaseHandler.getNameFromNum(item.phone.replaceAll(" ", ""));
//               AppLog.e("CONTACTSS", "api name is " + item.name);
//               AppLog.e("CONTACTSS", "name is " + name);
//               AppLog.e("CONTACTSS", "num is " + item.adminPhone);
                if (!name.equals("")) {
                    holder.txtName.setText(name);
                    item.name = name;
                } else {
                    holder.txtName.setText(item.name);
                }
            } catch (NullPointerException e) {
                holder.txtName.setText(item.name);
            }
        } else {
           AppLog.e("CONTACTSS", "count is 0");
            holder.txtName.setText(item.name);
        }

        if (!todayDate.equals(""))
            try {
                String[] separated = item.updatedAtTime.split(" ");
               AppLog.e("DATEEE", "todayDate is " + todayDate);
               AppLog.e("DATEEE", "separated is " + separated[0]);
                if (separated[0].equals(todayDate))
                    holder.txt_time.setText(separated[1].substring(0, separated[1].length() - 3));
                else
                    holder.txt_time.setText(MixOperations.getFormattedDate(separated[0],"yyyy-MM-dd"));

            } catch (Exception e) {
               AppLog.e("ERROR", "error is " + e.toString());
            }

        if (item.image != null && !item.image.isEmpty()) {
           AppLog.e("LeadAdapter", "Item Not Empty ");
            Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.image)).resize(dpToPx(), dpToPx()).
                    into(holder.imgLead, new Callback() {
                        @Override
                        public void onSuccess()
                        {
                           AppLog.e("LeadAdapter", "Item Not Empty , On Success ");
                        }

                        @Override
                        public void onError() {
                           AppLog.e("LeadAdapter", "Item Not Empty , On Error");
                            holder.imgLead_default.setVisibility(View.VISIBLE);
                            TextDrawable drawable = TextDrawable.builder()
                                    .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position) );
                            holder.imgLead_default.setImageDrawable(drawable);
                          /*  Picasso.with(mContext).load(R.drawable.ic_person).
                                    into(holder.imgLead);*/
                        }
                    });
        } else {
           AppLog.e("LeadAdapter", "Item Empty ");
            holder.imgLead_default.setVisibility(View.VISIBLE);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position) );
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

                if (item.image != null && !item.image.isEmpty()) {
                    Picasso.with(mContext).load(item.image).into(iv);

                } else {

                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position) );
                    iv.setImageDrawable(drawable);
                   // Picasso.with(mContext).load(R.drawable.icon_default_user).into(iv);

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

        @Bind(R.id.txt_time)
        TextView txt_time;

        @Bind(R.id.img_lead)
        ImageView imgLead;

        @Bind(R.id.img_lead_default)
        ImageView imgLead_default;

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

    public interface OnChatSelected {
        void onNameClick(PersonalPostItem item);
    }

    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "dd-MM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = "";

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    private int dpToPx() {
        return mContext.getResources().getDimensionPixelSize(R.dimen.group_list_image_size);
    }

}


