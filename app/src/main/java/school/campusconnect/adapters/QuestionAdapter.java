package school.campusconnect.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.recyclerview.widget.RecyclerView;
import school.campusconnect.utils.AppLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.activities.CommentsActivity;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.datamodel.question.QuestionData;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.views.SMBDialogUtils;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ImageViewHolder> {

    private List<QuestionData> list = Collections.emptyList();
    private OnItemClickListener listener;
    private Context mContext;
    String type;
    QuestionData item;
    String mGroupId;
    int count;

    public QuestionAdapter(List<QuestionData> list, OnItemClickListener listener, String type, String id, int count) {
        if (list == null) return;
        this.list = list;
        this.listener = listener;
        this.type = type;
        this.mGroupId = id;
        this.count = count;
    }

    public QuestionAdapter() {

    }

    public void addItems(List<QuestionData> items) {
        list.addAll(new ArrayList<QuestionData>(items));
       AppLog.e("GeneralPostScroll ", "count " + list.size());
        this.notifyDataSetChanged();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view;
//        if (type.equals("group"))
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_posts, parent, false);
//        else
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_questions, parent, false);
        //videoView = (VideoView) view.findViewById(R.id.videoview);
        return new ImageViewHolder(view);
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {
        final QuestionData item = list.get(position);


//       AppLog.e("asf", new Gson().toJson(item));
//       AppLog.e("PostAdapter", "Group Id : " + mGroupId);

        /*if (item.you){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(65, 5, 10, 5);
            holder.card.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 5, 65, 5);
            holder.card.setLayoutParams(params);
        }*/

        String createBy;
        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);

        if (count != 0) {
            try {
//               AppLog.e("CONTACTSS", "count is " + databaseHandler.getCount());
                String name = databaseHandler.getNameFromNum(item.phone.replaceAll(" ", ""));
//               AppLog.e("CONTACTSS", "api name is " + item.questionFrom);
//               AppLog.e("CONTACTSS", "name is " + name);
//               AppLog.e("CONTACTSS", "num is " + item.adminPhone);
                if (!name.equals("")) {
                    holder.txtName.setText(name /*+ " (" + item.questionForPost + ")"*/);
                } else {
                    holder.txtName.setText(item.questionFrom /*+ " (" + item.questionForPost + ")"*/);
                }
            } catch (NullPointerException e) {
                holder.txtName.setText(item.questionFrom /*+ " (" + item.questionForPost + ")"*/);
            }
        } else {
           AppLog.e("CONTACTSS", "count is 0");
            holder.txtName.setText(item.questionFrom /*+ " (" + item.questionForPost + ")"*/);
        }
//        createBy = item.questionFrom;

//        holder.txtName.setText(createBy);

        holder.txtContent.setText(item.question);
        holder.txt_post.setText(item.questionForPost);
        Locale current = mContext.getResources().getConfiguration().locale;

        SimpleDateFormat simpleDateFormat;
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mma", Locale.US);

        Date tempDate = null;
        try {
            tempDate = simpleDateFormat.parse(item.postedAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd MMM, yyyy, hh:mm a");

        holder.txtDate.setText(outputDateFormat.format(tempDate));


        holder.txt_drop_delete.setVisibility(View.VISIBLE);

        holder.imgPhoto.setVisibility(View.VISIBLE);
        if (item.image != null && !item.image.isEmpty()) {
            if(item.imageHeight!=0 && item.imageWidth!=0)
            {
                int height=(Constants.screen_width *item.imageHeight)/item.imageWidth;
                holder.imgPhoto.getLayoutParams().height =height;
               AppLog.e("PostAdapter","Item Width=>"+item.imageWidth+",Height=>"+item.imageHeight);
               AppLog.e("PostAdapter","Height Image=>"+height);

            }
            Picasso.with(mContext).load(item.image)/*.placeholder(R.drawable.icon_loading)*/.networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgPhoto,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.imageLoading.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            holder.imageLoading.setVisibility(View.GONE);
                            Picasso.with(mContext).load(item.image).into(holder.imgPhoto, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                   AppLog.e("Picasso", "Error : ");
                                }
                            });
                        }
                    });
        } else if (item.pdf != null) {
            holder.imgPhoto.setBackground(null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            holder.imgPhoto.setLayoutParams(lp);
            Picasso.with(mContext).load(R.drawable.pdf_thumbnail).into(holder.imgPhoto, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                   AppLog.e("Picasso", "Error : ");
                }
            });
        } else {
            holder.imgPhoto.setBackground(null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            holder.imgPhoto.setLayoutParams(lp);
            holder.imgPhoto.setVisibility(View.GONE);
            holder.imageLoading.setVisibility(View.GONE);
        }
//        holder.txtContent.setText(item.text);

        /*if (item.image != null && !item.profile.isEmpty()) {
            Picasso.with(mContext).load(item.profile).networkPolicy(NetworkPolicy.OFFLINE).transform(new RoundedTransformation(25, 8)).into(holder.imgLead,
                    new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(mContext).load(item.profile).into(holder.imgLead, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                   AppLog.e("Picasso", "Error : ");
                                }
                            });
                        }
                    });
        } else {
            Picasso.with(mContext).load(R.drawable.icon_default_user).transform(new RoundedTransformation(25, 8)).into(holder.imgLead);
        }*/

        holder.imgLead.setVisibility(View.VISIBLE);
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(ImageUtil.getTextLetter(item.questionFrom), ImageUtil.getRandomColor(position) );
        holder.imgLead.setImageDrawable(drawable);

        if (item.video != null) {
            holder.imgPlay.setVisibility(View.VISIBLE);
        } else {
            holder.imgPlay.setVisibility(View.GONE);
        }

        /*if (type.equals("group")) {
            holder.txtLike.setVisibility(View.VISIBLE);
            holder.txtFav.setVisibility(View.VISIBLE);
        } else if (type.equals("favourite")) {
            holder.txtLike.setVisibility(View.VISIBLE);
            holder.txtFav.setVisibility(View.VISIBLE);
        } else {*/
           /* holder.txtLike.setVisibility(View.GONE);
            holder.txtFav.setVisibility(View.GONE);*/
//        }

        /*if (mGroupId == 1) {
           AppLog.e("PostAdapter Codition", "Group Id : " + mGroupId);
            holder.txtFav.setVisibility(View.INVISIBLE);
        }*/

   /*     if (type.equals("team")) {
            GroupDashboardActivity.btnLayout.setVisibility(View.GONE);
        } else {
            GroupDashboardActivity.btnLayout.setVisibility(View.VISIBLE);
        }

*/

        if (holder.txtContent.getText().toString().equals("")) {
            holder.txtContent.setVisibility(View.GONE);
        }
        holder.ivEdit.setVisibility(View.GONE);

        /*holder.imgLead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.lin_drop.getVisibility() == View.VISIBLE)
                    holder.lin_drop.setVisibility(View.GONE);
                else {
                    Dialog settingsDialog = new Dialog(mContext);
                    LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                    View newView = (View) inflater.inflate(R.layout.img_layout, null);
                    settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    settingsDialog.setContentView(newView);

                    settingsDialog.show();
                    ImageView iv = (ImageView) newView.findViewById(R.id.img_popup);

                    if (item.profile != null && !item.profile.isEmpty()) {
                        Picasso.with(mContext).load(item.profile).into(iv);

                    } else {
                        Picasso.with(mContext).load(R.drawable.icon_default_user).into(iv);

                    }
                    settingsDialog.show();
                }
            }
        });*/
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

        @Bind(R.id.view)
        View view;
        @Bind(R.id.txt_drop_share)
        TextView txt_drop_share;
        @Bind(R.id.txt_drop_delete)
        TextView txt_drop_delete;
        @Bind(R.id.txt_drop_report)
        TextView txt_drop_report;
        @Bind(R.id.txt_name)
        TextView txtName;
        @Bind(R.id.txt_content)
        TextView txtContent;
        @Bind(R.id.txt_post)
        TextView txt_post;
        @Bind(R.id.img_lead)
        ImageView imgLead;

        @Bind(R.id.img_lead_default)
        ImageView imgLead_default;

        @Bind(R.id.txt_date)
        TextView txtDate;
        /*@Bind(R.id.txt_like)
        TextView txtLike;*/
        /*@Bind(R.id.txt_fav)
        TextView txtFav;*/
        @Bind(R.id.txt_title)
        TextView txtTitle;
        @Bind(R.id.txt_readmore)
        TextView txtReadMore;
        /*@Bind(R.id.txt_comments)
        TextView txt_comments;*/
        @Bind(R.id.image)
        ImageView imgPhoto;
        @Bind(R.id.imageLoading)
        ImageView imageLoading;
        @Bind(R.id.img_play)
        ImageView imgPlay;
        @Bind(R.id.iv_delete)
        ImageView ivDelete;
        @Bind(R.id.iv_edit)
        ImageView ivEdit;
        @Bind(R.id.rel)
        RelativeLayout rel;
        @Bind(R.id.lin_drop)
        LinearLayout lin_drop;
        @Bind(R.id.card)
        LinearLayout card;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                }
            });

            /*itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(mContext, "adsf", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });*/

        }

        @OnClick({R.id.rel, R.id.txt_readmore, R.id.iv_edit, R.id.iv_delete,
                R.id.txt_drop_delete, R.id.txt_drop_report, R.id.txt_drop_share})
        public void OnLikeClick(View v) {
            item = list.get(getLayoutPosition());
            switch (v.getId()) {
                case R.id.txt_like:
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else if (isConnectionAvailable()) {
                        listener.onLikeClick(item, getLayoutPosition());
                    } else {
                        showNoNetworkMsg();
                    }

                    break;
                case R.id.txt_fav:
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else if (isConnectionAvailable()) {
                        listener.onFavClick(item, getLayoutPosition());
                    } else {
                        showNoNetworkMsg();
                    }
                    break;
                case R.id.rel:
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else if (isConnectionAvailable()) {
                        listener.onPostClick1(item);
                    } else {
                        showNoNetworkMsg();
                    }
                    break;
                case R.id.txt_readmore:
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else {
                        listener.onReadMoreClick(item);
                        /*Intent intent = new Intent(mContext, ReadMoreActivity.class);
                        Gson gson = new Gson();
                        String data = gson.toJson(item);
                        intent.putExtra("data", data);
                        intent.putExtra("type", type);
                        intent.putExtra("from", "post");
                        mContext.startActivity(intent);*/
                    }
                    break;

                case R.id.iv_delete:
                    /*if (isConnectionAvailable()) {
                        listener.onDeleteClick(item);
                    } else {
                        showNoNetworkMsg();
                    }*/
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else
                        lin_drop.setVisibility(View.VISIBLE);
                    break;

                case R.id.iv_edit:
                    if (isConnectionAvailable()) {
                        listener.onEditClick(item);
                    } else {
                        showNoNetworkMsg();
                    }
                    break;

                case R.id.txt_comments:
                   AppLog.e("COMMETS_", "type is " + type);

                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else {
                        if (isConnectionAvailable()) {

                            /*Intent intent = new Intent(mContext, ReadMoreActivity.class);
                            Gson gson = new Gson();
                            String data = gson.toJson(item);
                            intent.putExtra("data", data);
                            intent.putExtra("type", type);
                            intent.putExtra("from", "personal");
                            mContext.startActivity(intent);*/

                            Intent intent1 = new Intent(mContext, CommentsActivity.class);
                            intent1.putExtra("id", mGroupId);
                            intent1.putExtra("post_id", list.get(getAdapterPosition()).id);
                            intent1.putExtra("type", type);
                            mContext.startActivity(intent1);
                        } else {
                            showNoNetworkMsg();
                        }
                    }
                    break;

                case R.id.txt_drop_delete:
                    lin_drop.setVisibility(View.GONE);
                    if (isConnectionAvailable()) {
                        listener.onDeleteClick(item);
                    } else {
                        showNoNetworkMsg();
                    }
                    break;

                case R.id.txt_drop_report:
                    lin_drop.setVisibility(View.GONE);
                    if (isConnectionAvailable()) {
                        listener.onReportClick(item);
                    } else {
                        showNoNetworkMsg();
                    }
                    break;

                case R.id.txt_drop_share:
                    lin_drop.setVisibility(View.GONE);
                    if (isConnectionAvailable()) {
                        listener.onShareClick(item);
                    } else {
                        showNoNetworkMsg();
                    }
                    break;

            }
        }
    }

    public List<QuestionData> getList() {
        return list;
    }

    public interface OnItemClickListener {
        void onFavClick(QuestionData item, int pos);

        void onLikeClick(QuestionData item, int pos);

        void onPostClick1(QuestionData item);

        void onReadMoreClick(QuestionData item);

        void onEditClick(QuestionData item);

        void onDeleteClick(QuestionData item);

        void onReportClick(QuestionData item);

        void onShareClick(QuestionData item);
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