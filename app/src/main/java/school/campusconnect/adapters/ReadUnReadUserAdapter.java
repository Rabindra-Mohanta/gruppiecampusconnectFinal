package school.campusconnect.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.ReadUnreadResponse;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.MixOperations;

public class ReadUnReadUserAdapter extends RecyclerView.Adapter<ReadUnReadUserAdapter.ViewHolder> {
    ArrayList<ReadUnreadResponse.UserData> list;
    private Context mContext;

    public ReadUnReadUserAdapter(ArrayList<ReadUnreadResponse.UserData> readUsers) {
        this.list = readUsers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_read_unread,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ReadUnreadResponse.UserData item = list.get(position);
        holder.txtName.setText(list.get(position).getName());
        holder.tvDate.setText(MixOperations.getFormattedDate(list.get(position).getPostSeenTime(), Constants.DATE_FORMAT));

        final String name = list.get(position).getName();
        if (!TextUtils.isEmpty(item.getImage())) {
            Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(dpToPx(), dpToPx()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgLead,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.imgLead_default.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError() {
                            Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(dpToPx(), dpToPx()).into(holder.imgLead, new Callback() {
                                @Override
                                public void onSuccess() {
                                    holder.imgLead_default.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onError() {
                                    holder.imgLead_default.setVisibility(View.VISIBLE);
                                    TextDrawable drawable = TextDrawable.builder()
                                            .buildRound(ImageUtil.getTextLetter(name), ImageUtil.getRandomColor(position));
                                    holder.imgLead_default.setImageDrawable(drawable);
                                    AppLog.e("Picasso", "Error : ");
                                }
                            });
                        }
                    });
        } else {
            holder.imgLead_default.setVisibility(View.VISIBLE);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(ImageUtil.getTextLetter(name), ImageUtil.getRandomColor(position));
            holder.imgLead_default.setImageDrawable(drawable);
        }

    }
    private int dpToPx() {
        return mContext.getResources().getDimensionPixelSize(R.dimen.group_list_image_size);
    }
    @Override
    public int getItemCount() {
        if(list==null)
            return 0;
        else
            return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.txt_name)
        TextView txtName;

        @Bind(R.id.tvDate)
        TextView tvDate;

        @Bind(R.id.img_lead)
        ImageView imgLead;

        @Bind(R.id.img_lead_default)
        ImageView imgLead_default;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
