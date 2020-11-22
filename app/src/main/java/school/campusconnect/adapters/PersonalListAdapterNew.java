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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import school.campusconnect.R;
import school.campusconnect.datamodel.personalchat.PersonalPostItem;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.MixOperations;
import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalListAdapterNew extends RecyclerView.Adapter<PersonalListAdapterNew.ViewHolder> {

    private Context context;
    OnPersonalListClickListener listener;
    ArrayList<PersonalPostItem> personalList;

    public PersonalListAdapterNew(ArrayList<PersonalPostItem> personalList,OnPersonalListClickListener listener) {
        this.listener = listener;
        this.personalList=personalList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_personal_list_new,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PersonalPostItem item = personalList.get(position);


        holder.tvTeamName.setText(item.name);
        holder.tvDate.setText((MixOperations.getFormattedDate(item.updatedAtTime, Constants.DATE_FORMAT)));
        holder.tvPostCount.setText(item.postUnreadCount + "");
        holder.tvPostCount.setVisibility(item.postUnreadCount != 0 ? View.VISIBLE : View.GONE);

        if(!TextUtils.isEmpty(item.image)) {
            holder.imgTeam.setVisibility(View.VISIBLE);
            holder.imgDefault.setVisibility(View.GONE);
            Picasso.with(context).load(Constants.decodeUrlToBase64(item.image)).resize(dpToPx(), dpToPx()).into(holder.imgTeam, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    holder.imgTeam.setVisibility(View.GONE);
                    holder.imgDefault.setVisibility(View.VISIBLE);

                    TextDrawable drawable = TextDrawable.builder()
                            .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position));
                    holder.imgDefault.setImageDrawable(drawable);
                }
            });
        }
        else
        {
            holder.imgTeam.setVisibility(View.GONE);
            holder.imgDefault.setVisibility(View.VISIBLE);

            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position) );
            holder.imgDefault.setImageDrawable(drawable);
        }
    }
    private int dpToPx() {
        return context.getResources().getDimensionPixelSize(R.dimen.group_list_image_size);
    }
    @Override
    public int getItemCount() {
        return personalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgTeam;
        TextView tvTeamName,tvDate,tvPostCount;
        ImageView imgDefault;
        public ViewHolder(View itemView) {
            super(itemView);
            imgTeam = itemView.findViewById(R.id.imgTeam);
            imgDefault = itemView.findViewById(R.id.imgDefault);
            tvTeamName = itemView.findViewById(R.id.tvTeamName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPostCount = itemView.findViewById(R.id.tvPostCount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPersonalClick(personalList.get(getAdapterPosition()));
                    personalList.get(getAdapterPosition()).postUnreadCount = 0;
                    tvPostCount.setText("0");
                    tvPostCount.setVisibility(View.GONE);
                }
            });
        }
    }
    public interface OnPersonalListClickListener
    {
        public void onPersonalClick(PersonalPostItem personalPostItem);
    }
}
