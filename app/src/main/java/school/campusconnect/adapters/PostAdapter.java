package school.campusconnect.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;

import school.campusconnect.Assymetric.AsymmetricRecyclerView;
import school.campusconnect.Assymetric.AsymmetricRecyclerViewAdapter;
import school.campusconnect.Assymetric.SpacesItemDecoration;
import school.campusconnect.Assymetric.Utils;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AppLog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;
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
import school.campusconnect.activities.CommentsActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.ReadMoreActivity;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.datamodel.PostItem;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.MixOperations;
import school.campusconnect.views.SMBDialogUtils;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ImageViewHolder> {

    private static final String TAG = "PostAdapter";
    private List<PostItem> list = Collections.emptyList();
    private OnItemClickListener listener;
    private Context mContext;
    String type;
    PostItem item;
    int count;
    DatabaseHandler databaseHandler;

    public PostAdapter(List<PostItem> list, OnItemClickListener listener, String type, DatabaseHandler databaseHandler, int count) {
        if (list == null) return;
        this.list = list;
        this.listener = listener;
        this.type = type;
        this.count = count;
        this.databaseHandler = databaseHandler;

        AppLog.e("PostAdapter", "Display width,height " + Constants.screen_width + "," + Constants.screen_height);
        AppLog.e(TAG, "Contact DB Count :" + count);
    }

    public void addItems(List<PostItem> items) {
        list.addAll(new ArrayList<PostItem>(items));
        AppLog.e("GeneralPostScroll ", "count " + list.size());
        this.notifyDataSetChanged();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_post, parent, false));
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    private int dpToPx() {
        return mContext.getResources().getDimensionPixelSize(R.dimen.group_list_image_size);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final PostItem item = list.get(position);
        AppLog.e("PostAdapter", "item[" + position + "] : " + item);
        String dispName = item.createdBy;
        if (count != 0) {
            try {
                String name = databaseHandler.getNameFromNum(item.phone.replaceAll(" ", ""));
                if (!TextUtils.isEmpty(name)) {
                    dispName = name;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        holder.txtName.setText(dispName);
        holder.txtDate.setText(MixOperations.getFormattedDate(item.createdAt, Constants.DATE_FORMAT));
        holder.txtLike.setText(Constants.coolFormat(item.likes, 0));
        holder.txt_comments.setText(Constants.coolFormat(item.comments, 0) + "");

        if (item.isLiked) {
            Picasso.with(mContext).load(R.drawable.icon_post_liked).into(holder.ivLike);
        } else {
            Picasso.with(mContext).load(R.drawable.icon_post_like).into(holder.ivLike);
        }

        if (item.isFavourited) {
            Picasso.with(mContext).load(R.drawable.icon_post_favd).into(holder.txt_fav);
        } else {
            Picasso.with(mContext).load(R.drawable.icon_post_fav).into(holder.txt_fav);
        }

        AppLog.e(TAG, "Item Width=>" + item.imageWidth + ",Height=>" + item.imageHeight);
        holder.constThumb.setVisibility(View.GONE);
        final String name = dispName;
        if (!TextUtils.isEmpty(item.createdByImage)) {
            Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.createdByImage)).resize(dpToPx(), dpToPx()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgLead,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.imgLead_default.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError() {
                            Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.createdByImage)).resize(dpToPx(), dpToPx()).into(holder.imgLead, new Callback() {
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

        if (!TextUtils.isEmpty(item.fileType)) {
            if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
                if (item.fileName != null) {

                    ChildAdapter adapter;
                    if (item.fileName.size() == 3) {
                        adapter = new ChildAdapter(2, item.fileName.size(), mContext, item.fileName);
                    } else {
                        adapter = new ChildAdapter(Constants.MAX_IMAGE_NUM, item.fileName.size(), mContext, item.fileName);
                    }
                    holder.recyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(mContext, holder.recyclerView, adapter));
                    holder.recyclerView.setVisibility(View.VISIBLE);
                }
                holder.imgPlay.setVisibility(View.GONE);
                holder.imgPhoto.setVisibility(View.GONE);
            } else if (item.fileType.equals(Constants.FILE_TYPE_VIDEO)) {
                if (item.fileName != null) {
                    ChildVideoAdapter adapter;
                    if (item.fileName.size() == 3) {
                        adapter = new ChildVideoAdapter(2, mContext, item.fileName, item.thumbnailImage);
                    } else {
                        adapter = new ChildVideoAdapter(Constants.MAX_IMAGE_NUM, mContext, item.fileName, item.thumbnailImage);
                    }
                    holder.recyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(mContext, holder.recyclerView, adapter));
                    holder.recyclerView.setVisibility(View.VISIBLE);
                }
                holder.imgPlay.setVisibility(View.GONE);
                holder.imgPhoto.setVisibility(View.GONE);
            } else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
                holder.constThumb.setVisibility(View.VISIBLE);
                holder.imgPhoto.setVisibility(View.GONE);
                holder.recyclerView.setVisibility(View.GONE);
                holder.imgPlay.setVisibility(View.GONE);
                if (item.thumbnailImage != null && item.thumbnailImage.size() > 0) {
                    Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.thumbnailImage.get(0))).into(holder.imageThumb);

                }
                if (item.fileName != null && item.fileName.size() > 0) {
                    if (AmazoneDownload.isPdfDownloaded(item.fileName.get(0))) {
                        holder.imgDownloadPdf.setVisibility(View.GONE);
                    } else {
                        holder.imgDownloadPdf.setVisibility(View.VISIBLE);
                    }
                }

            } else if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
                holder.imgPhoto.getLayoutParams().height = (Constants.screen_width * 204) / 480;
                holder.imgPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Picasso.with(mContext).load(item.thumbnail).into(holder.imgPhoto);
                holder.imgPhoto.setVisibility(View.VISIBLE);
                holder.imgPlay.setVisibility(View.VISIBLE);
                holder.recyclerView.setVisibility(View.GONE);
            } else {
                holder.imgPhoto.setVisibility(View.GONE);
                holder.imgPlay.setVisibility(View.GONE);
                holder.recyclerView.setVisibility(View.GONE);
            }
        } else {

            holder.imgPhoto.setVisibility(View.GONE);
            holder.imgPlay.setVisibility(View.GONE);
            holder.recyclerView.setVisibility(View.GONE);
        }

        holder.imgLead_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.e("PostAdapter AA", "clicked");
                Dialog settingsDialog = new Dialog(mContext);
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                View newView = (View) inflater.inflate(R.layout.img_layout, null);
                settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(newView);
                ImageView iv = (ImageView) newView.findViewById(R.id.img_popup);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(ImageUtil.getTextLetter(name), ImageUtil.getRandomColor(position));
                iv.setImageDrawable(drawable);
                settingsDialog.show();
            }
        });
        holder.imgLead.setOnClickListener(new View.OnClickListener() {
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

                    if (!TextUtils.isEmpty(item.createdByImage)) {
                        Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.createdByImage)).into(iv);
                    } else {
                        TextDrawable drawable = TextDrawable.builder()
                                .buildRect(name, ImageUtil.getRandomColor(position));
                        iv.setImageDrawable(drawable);
                    }
                    settingsDialog.show();
                }
            }
        });

        if (item.canEdit) {
            holder.txt_drop_delete.setVisibility(View.VISIBLE);
            holder.txt_drop_report.setVisibility(View.GONE);
        } else {
            holder.txt_drop_delete.setVisibility(View.GONE);
            holder.txt_drop_report.setVisibility(View.VISIBLE);
        }
        AppLog.e(TAG, "type : " + type);
        if (type.equals("group") || type.equals("favourite")) {
            holder.txtLike.setVisibility(View.VISIBLE);
            holder.txt_fav.setVisibility(View.VISIBLE);
            holder.linLikes.setVisibility(View.VISIBLE);
        } else {
            holder.txtLike.setVisibility(View.GONE);
            holder.txt_fav.setVisibility(View.GONE);
            holder.linLikes.setVisibility(View.GONE);
        }

        if (!GroupDashboardActivityNew.mAllowPostQuestion) {
            holder.txt_que.setVisibility(View.GONE);
            holder.view1.setVisibility(View.GONE);
        } else {
            holder.txt_que.setVisibility(View.VISIBLE);
            holder.view1.setVisibility(View.VISIBLE);
        }

        holder.txt_title.setText(item.title);
        if (TextUtils.isEmpty(item.title))
            holder.txt_title.setVisibility(View.GONE);
        else
            holder.txt_title.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(item.text)) {
            holder.txtContent.setVisibility(View.VISIBLE);
            if (item.text.length() > 200) {
                StringBuilder stringBuilder = new StringBuilder(item.text);
                stringBuilder.setCharAt(197, '.');
                stringBuilder.setCharAt(198, '.');
                stringBuilder.setCharAt(199, '.');
                holder.txtContent.setText(stringBuilder);
                holder.txt_readmore.setVisibility(View.VISIBLE);
            } else {
                holder.txtContent.setText(item.text);
                holder.txt_readmore.setVisibility(View.GONE);
            }
        } else {
            holder.txtContent.setVisibility(View.GONE);
            holder.txt_readmore.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        if (list != null)
            return list.size();
        else
            return 0;
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

        @Bind(R.id.view1)
        View view1;

        @Bind(R.id.txt_que)
        TextView txt_que;

        @Bind(R.id.txt_drop_delete)
        TextView txt_drop_delete;

        @Bind(R.id.txt_drop_report)
        TextView txt_drop_report;

        @Bind(R.id.txt_name)
        TextView txtName;

        @Bind(R.id.txt_content)
        TextView txtContent;

        @Bind(R.id.txt_title)
        TextView txt_title;

        @Bind(R.id.txt_readmore)
        TextView txt_readmore;


        @Bind(R.id.img_lead)
        CircleImageView imgLead;

        @Bind(R.id.img_lead_default)
        ImageView imgLead_default;

        @Bind(R.id.txt_date)
        TextView txtDate;

        @Bind(R.id.txt_like)
        TextView txtLike;

        @Bind(R.id.img_like)
        ImageView ivLike;

        @Bind(R.id.txt_fav)
        ImageView txt_fav;

        @Bind(R.id.txt_comments)
        TextView txt_comments;

        @Bind(R.id.img_comments)
        ImageView img_comments;

        @Bind(R.id.image)
        ImageView imgPhoto;

        @Bind(R.id.constThumb)
        ConstraintLayout constThumb;

        @Bind(R.id.imageThumb)
        ImageView imageThumb;

        @Bind(R.id.imageLoading)
        ImageView imageLoading;

        @Bind(R.id.img_play)
        ImageView imgPlay;

        @Bind(R.id.iv_delete)
        ImageView ivDelete;

        @Bind(R.id.rel)
        RelativeLayout rel;

        @Bind(R.id.lin_drop)
        LinearLayout lin_drop;

        @Bind(R.id.linComments)
        LinearLayout linComments;

        @Bind(R.id.linLikes)
        LinearLayout linLikes;

        @Bind(R.id.linPush)
        LinearLayout linPush;

        @Bind(R.id.recyclerView)
        AsymmetricRecyclerView recyclerView;

        @Bind(R.id.imgDownloadPdf)
        ImageView imgDownloadPdf;

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
            recyclerView.setRequestedHorizontalSpacing(Utils.dpToPx(mContext, 3));
            recyclerView.addItemDecoration(
                    new SpacesItemDecoration(mContext.getResources().getDimensionPixelSize(R.dimen.padding_3dp)));
        }

        @OnClick({R.id.txt_like, R.id.txt_fav, R.id.rel, R.id.txt_readmore, R.id.iv_delete,
                R.id.txt_comments, R.id.txt_drop_delete, R.id.txt_drop_report, R.id.txt_drop_share,
                R.id.txt_que, R.id.txt_push, R.id.txt_name, R.id.txt_like_list, R.id.img_comments, R.id.img_like})
        public void OnLikeClick(View v) {
            item = list.get(getLayoutPosition());
            switch (v.getId()) {
                case R.id.img_like:
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else if (isConnectionAvailable()) {
                        listener.onLikeClick(item, getLayoutPosition());
                    } else {
                        showNoNetworkMsg();
                    }

                    break;
                case R.id.txt_like:
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else if (isConnectionAvailable()) {
                        listener.onLikeListClick(item);
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
                        listener.onPostClick(item);
                    } else {
                        showNoNetworkMsg();
                    }
                    break;
                case R.id.txt_readmore:
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else {
                        Intent intent = new Intent(mContext, ReadMoreActivity.class);
                        Gson gson = new Gson();
                        String data = gson.toJson(item);
                        intent.putExtra("data", data);
                        intent.putExtra("type", type);
                        intent.putExtra("from", "post");
                        mContext.startActivity(intent);
                    }
                    break;

                case R.id.iv_delete:
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

                case R.id.txt_name:
                    listener.onNameClick(item);
                    break;

                case R.id.img_comments:
                case R.id.txt_comments:
                    AppLog.e(TAG, "type is " + type);

                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else if (type.equals("group") || type.equals("favourite")) {
                        if (isConnectionAvailable()) {
                            Intent intent1 = new Intent(mContext, CommentsActivity.class);
                            intent1.putExtra("id", GroupDashboardActivityNew.groupId);
                            if (type.equals("group")) {
                                intent1.putExtra("post_id", list.get(getAdapterPosition()).id);
                            } else {
                                intent1.putExtra("post_id", list.get(getAdapterPosition()).postIdForBookmark);
                            }

                            intent1.putExtra("type", "group");
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

                case R.id.txt_que:
                    lin_drop.setVisibility(View.GONE);
                    if (isConnectionAvailable()) {
                        listener.onQueClick(item);
                    } else {
                        showNoNetworkMsg();
                    }
                    break;

                case R.id.txt_push:
                    lin_drop.setVisibility(View.GONE);
                    if (isConnectionAvailable()) {
                        listener.onPushClick(item);
                    } else {
                        showNoNetworkMsg();
                    }
                    break;

            }
        }
    }

    public List<PostItem> getList() {
        return list;
    }

    public interface OnItemClickListener {
        void onFavClick(PostItem item, int pos);

        void onLikeClick(PostItem item, int pos);

        void onPostClick(PostItem item);

        void onReadMoreClick(PostItem item);

        void onEditClick(PostItem item);

        void onDeleteClick(PostItem item);

        void onReportClick(PostItem item);

        void onShareClick(PostItem item);

        void onQueClick(PostItem item);

        void onPushClick(PostItem item);

        void onNameClick(PostItem item);

        void onLikeListClick(PostItem item);

        void onMoreOptionClick(PostItem item);
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
            }
        });
    }
}