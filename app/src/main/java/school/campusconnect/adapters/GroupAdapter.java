package school.campusconnect.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import school.campusconnect.utils.AppLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import school.campusconnect.R;
import school.campusconnect.datamodel.GroupItem;
import school.campusconnect.utils.ImageUtil;


public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ImageViewHolder> {
    private List<GroupItem> list = Collections.emptyList();
    private OnGroupSelectListener listener;
    private Context mcontext;

    public GroupAdapter(List<GroupItem> list, OnGroupSelectListener listener)
    {
        if (list == null) return;
        this.list = list;
        this.listener = listener;
    }


    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mcontext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_group, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final GroupItem item = list.get(position);
       AppLog.e("GroupAdapter", "Image Path : " + item.getImage());
        holder.txtName.setText(item.getName());

        //Picasso.with(mcontext).load(R.drawable.ribbon).transform(new RoundedTransformation(50, 4)).into(holder.imgBack);
        // holder.imgGroup.setVisibility(View.INVISIBLE);
        // .networkPolicy(NetworkPolicy.OFFLINE)

        if (item.getImage() != null && !item.getImage().equals(""))
            Picasso.with(mcontext).load(item.getImage()).networkPolicy(NetworkPolicy.OFFLINE).resize(dpToPx(), dpToPx()).into(holder.imgGroupNew, new Callback() {
                @Override
                public void onSuccess() {
                    holder.imgGroupNewDefault.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onError() {
                    Picasso.with(mcontext).load(item.getImage()).into(holder.imgGroupNew, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.imgGroupNewDefault.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError()
                        {

                            holder.imgGroupNewDefault.setVisibility(View.VISIBLE);
                            TextDrawable drawable = TextDrawable.builder()
                                    .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position) );
                            holder.imgGroupNewDefault.setImageDrawable(drawable);
                           AppLog.e("Picasso", "Error : ");
                        }
                    });
                }
            });

        else
        {
            if (item.getName().equalsIgnoreCase("gruppie"))
            {
                holder.imgGroupNew.setImageResource(R.drawable.icon_gruppie);
                holder.imgGroupNewDefault.setVisibility(View.INVISIBLE);
            }
            else
            {
               // holder.imgGroupNew.setImageResource(R.drawable.icon_default_groups);
                holder.imgGroupNewDefault.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position) );
                holder.imgGroupNewDefault.setImageDrawable(drawable);
            }

        }


        if (item.isAdmin) {
            holder.imgAdmin.setVisibility(View.VISIBLE);
        } else {
            holder.imgAdmin.setVisibility(View.GONE);
        }

        if (!item.isAdmin && item.canPost) {
            holder.imgAdmin.setVisibility(View.VISIBLE);
            holder.imgAdmin.setImageResource(R.drawable.icon_post);
        }

        if (item.isAdmin && item.canPost) {
            holder.imgAdmin.setVisibility(View.VISIBLE);
            holder.imgAdmin.setImageResource(R.drawable.icon_admin);
        }

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

        @Bind(R.id.txt_group_name)
        TextView txtName;

        @Bind(R.id.img_admin_icon)
        ImageView imgAdmin;

        @Bind(R.id.img_group_new)
        CircleImageView imgGroupNew;

        @Bind(R.id.img_group_new_default)
        ImageView imgGroupNewDefault;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        @OnClick({R.id.relative})
        public void onMovieClick(View v) {
            switch (v.getId()) {

                case R.id.relative:
                   AppLog.e("GIT", "sd is " + list.get(getLayoutPosition()).shortDescription);
                    listener.onGroupSelect(list.get(getLayoutPosition()));
                    break;
            }
//            notifyDataSetChanged();
        }

    }

    public interface OnGroupSelectListener {
        void onGroupSelect(GroupItem item);

        void onReply();
    }

    private int dpToPx() {
        return mcontext.getResources().getDimensionPixelSize(R.dimen.group_list_image_size);
    }

}


