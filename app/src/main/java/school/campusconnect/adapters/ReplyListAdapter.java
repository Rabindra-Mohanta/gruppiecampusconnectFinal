package school.campusconnect.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.recyclerview.widget.RecyclerView;
import school.campusconnect.utils.AppLog;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import school.campusconnect.R;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.datamodel.comments.GroupCommentItem;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.MixOperations;
import school.campusconnect.views.SMBDialogUtils;

/**
 * Created by frenzin04 on 1/12/2017.
 */
public class ReplyListAdapter extends RecyclerView.Adapter<ReplyListAdapter.ImageViewHolder> {

    private List<GroupCommentItem> list = Collections.emptyList();
    private OnItemClickListener listener;
    private Context mContext;
    String type;
    GroupCommentItem item;
    String mGroupId;
    String postId;
    int count;

    public ReplyListAdapter(List<GroupCommentItem> list, OnItemClickListener listener, String type, String groupId, String postId, String comment_id, int count) {
        if (list == null) return;
        this.list = list;
        this.listener = listener;
        this.mGroupId = groupId;
        this.postId = postId;
        this.type = type;
        this.count = count;
    }

    public void addItems(List<GroupCommentItem> items) {
        list.addAll(new ArrayList<GroupCommentItem>(items));
       AppLog.e("items ", "count " + list.size());
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_replies, parent, false);
        //videoView = (VideoView) view.findViewById(R.id.videoview);
        return new ImageViewHolder(view);
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final GroupCommentItem item = list.get(position);

        if(position==list.size()-1)
        {
            holder.viewLine.setVisibility(View.INVISIBLE);
        }
        else {
            holder.viewLine.setVisibility(View.VISIBLE);
        }


       if(position==0)
       {
           holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.reply_comment_bg));
       }

        if (type.equals("personal")) {
            holder.txt_like.setVisibility(View.GONE);
            holder.txt_edit.setVisibility(View.GONE);
        }

        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);

        if (count != 0) {
            try {
//               AppLog.e("CONTACTSS", "count is " + databaseHandler.getCount());
                String name = databaseHandler.getNameFromNum(item.createdByPhone.replaceAll(" ", ""));
//               AppLog.e("CONTACTSS", "api name is " + item.fullname);
//               AppLog.e("CONTACTSS", "name is " + name);
//               AppLog.e("CONTACTSS", "num is " + item.phone);
                if (!name.equals("")) {
                    holder.txtName.setText(name);
                } else {
                    holder.txtName.setText(item.createdByName);
                }
            } catch (NullPointerException e) {
                holder.txtName.setText(item.createdByName);
            }
        } else {
           AppLog.e("CONTACTSS", "count is 0");
            holder.txtName.setText(item.createdByName);
        }

//        holder.txtName.setText(item.fullname);
        holder.txt_comments.setText(item.text);
        holder.txt_time.setText(MixOperations.getFormattedDate(item.insertedAt, Constants.DATE_FORMAT));

        holder.txt_like.setText(String.valueOf(item.likes));

        if (item.isLiked) {
            holder.ivLike.setImageResource(R.drawable.icon_post_liked);
           // holder.txt_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up_active, 0, 0, 0);
        }
        else
            {
                holder.ivLike.setImageResource(R.drawable.icon_post_like);
                //holder.txt_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up, 0, 0, 0);
        }

        if (holder.txt_like.getText().toString().equals("0")) {
            holder.ivLike.setImageResource(R.drawable.icon_post_like);
           // holder.txt_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up, 0, 0, 0);
        }

        if (item.canEdit) {
            holder.txt_edit.setVisibility(View.VISIBLE);
        } else {
            holder.txt_edit.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(item.createdByImage)) {
            Picasso.with(mContext).load(item.createdByImage).resize(dpToPx(), dpToPx()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.img_profile,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.img_profile_default.setVisibility(View.GONE);
                            holder.img_profile.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            Picasso.with(mContext).load(item.createdByImage).resize(dpToPx(), dpToPx()).into(holder.img_profile, new Callback() {
                                @Override
                                public void onSuccess() {
                                    holder.img_profile_default.setVisibility(View.GONE);
                                    holder.img_profile.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onError() {

                                    holder.img_profile_default.setVisibility(View.VISIBLE);
                                    holder.img_profile.setVisibility(View.GONE);
                                    TextDrawable drawable = TextDrawable.builder()
                                            .buildRound(ImageUtil.getTextLetter(item.createdByName), ImageUtil.getRandomColor(position) );
                                    holder.img_profile_default.setImageDrawable(drawable);
                                   AppLog.e("Picasso", "Error : ");
                                }
                            });
                        }
                    });
        }
        else
            {
                holder.img_profile.setVisibility(View.GONE);
                holder.img_profile_default.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                    .buildRound(ImageUtil.getTextLetter(item.createdByName), ImageUtil.getRandomColor(position) );
                holder.img_profile_default.setImageDrawable(drawable);
           // Picasso.with(mContext).load(R.drawable.icon_default_user).into(holder.img_profile);
        }

        holder.img_profile_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               AppLog.e("PostAdapter AA","clicked");
                Dialog settingsDialog = new Dialog(mContext);
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                View newView = (View) inflater.inflate(R.layout.img_layout, null);
                settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(newView);
                ImageView iv = (ImageView) newView.findViewById(R.id.img_popup);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(ImageUtil.getTextLetter(item.createdByName), ImageUtil.getRandomColor(position));
                iv.setImageDrawable(drawable);


                settingsDialog.show();
            }
        });
        holder.img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog settingsDialog = new Dialog(mContext);
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                View newView = (View) inflater.inflate(R.layout.img_layout, null);
                settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(newView);

                settingsDialog.show();
                ImageView iv = (ImageView) newView.findViewById(R.id.img_popup);

                if (!TextUtils.isEmpty(item.createdByImage)) {
                    Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.createdByImage)).into(iv);
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

        @Bind(R.id.txt_comments)
        TextView txt_comments;

        @Bind(R.id.txt_time)
        TextView txt_time;

        @Bind(R.id.img_profile)
        CircleImageView img_profile;

        @Bind(R.id.img_profile_default)
        ImageView img_profile_default;

        @Bind(R.id.txt_edit)
        ImageView txt_edit;

        @Bind(R.id.txt_like)
        TextView txt_like;

        @Bind(R.id.iv_like)
        ImageView ivLike;

        @Bind(R.id.viewLine)
        public View viewLine;

        /*@Bind(R.id.linReply)
        LinearLayout linReply;

        @Bind(R.id.linLike)
        LinearLayout linLike;*/

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.txt_like, R.id.iv_like, R.id.txt_edit})
        public void OnLikeClick(View v) {
            item = list.get(getLayoutPosition());
            switch (v.getId()) {
                case R.id.iv_like:
                case R.id.txt_like:
                    if (isConnectionAvailable()) {
                        listener.onLikeClick(item, getLayoutPosition());
                    } else {
                        showNoNetworkMsg();
                    }
                    break;

                case R.id.txt_edit:
                    if (isConnectionAvailable()) {
                        listener.onDeleteClick(item);
                    } else {
                        showNoNetworkMsg();
                    }
                    break;
            }
        }
    }

    public List<GroupCommentItem> getList() {
        return list;
    }

    public interface OnItemClickListener {
        void onFavClick(GroupCommentItem item, int pos);

        void onLikeClick(GroupCommentItem item, int pos);

        void onEditClick(GroupCommentItem item, int pos);

        void onPostClick(GroupCommentItem item);

        void onReadMoreClick(GroupCommentItem item);

        void onDeleteClick(GroupCommentItem item);
    }

    public boolean isConnectionAvailable() {
        ConnectivityManager connectivityManager;
        NetworkInfo networkinfo;
        connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkinfo = connectivityManager.getActiveNetworkInfo();
        return (networkinfo != null && networkinfo.isConnected());

    }

    public void showNoNetworkMsg() {
        SMBDialogUtils.showSMBDialogOK((Activity) mContext, mContext.getString(R.string.no_internet), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //onBackPressed();
            }
        });
    }

}