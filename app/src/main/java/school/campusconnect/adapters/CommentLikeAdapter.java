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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.datamodel.likelist.LikeListData;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

/**
 * Created by frenzin05 on 8/30/2017.
 */

public class CommentLikeAdapter extends RecyclerView.Adapter<CommentLikeAdapter.MyViewHolder> {
    private final Context context;
    private final List<LikeListData> list;

    public CommentLikeAdapter(Context context, List<LikeListData> list) {
        this.context=context;
        this.list=list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_like,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final LikeListData item = list.get(position);

        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        int count = databaseHandler.getCount();
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

        final String nm=dispname;
        if (item.getImage() != null && !item.getImage().isEmpty()) {
           AppLog.e("LeadAdapter", "Item Not Empty ");
            Picasso.with(context).load(Constants.decodeUrlToBase64(item.getImage())).resize(dpToPx(), dpToPx()).
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
                    .buildRound(ImageUtil.getTextLetter(nm), ImageUtil.getRandomColor(position));
            holder.imgLead_default.setImageDrawable(drawable);
        }

        holder.imgLead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog settingsDialog = new Dialog(context);
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                View newView = (View) inflater.inflate(R.layout.img_layout, null);
                settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(newView);

                settingsDialog.show();
                ImageView iv = (ImageView) newView.findViewById(R.id.img_popup);

                if (item.getImage() != null && !item.getImage().isEmpty()) {
                    Picasso.with(context).load(item.getImage()).into(iv);
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
        return context.getResources().getDimensionPixelSize(R.dimen.group_list_image_size);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

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
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
