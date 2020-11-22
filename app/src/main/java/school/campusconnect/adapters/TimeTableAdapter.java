package school.campusconnect.adapters;

import android.content.Context;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.Assymetric.AsymmetricRecyclerView;
import school.campusconnect.Assymetric.AsymmetricRecyclerViewAdapter;
import school.campusconnect.Assymetric.SpacesItemDecoration;
import school.campusconnect.Assymetric.Utils;
import school.campusconnect.R;
import school.campusconnect.datamodel.TimeTableRes;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.MixOperations;

public class TimeTableAdapter extends RecyclerView.Adapter<TimeTableAdapter.ViewHolder> {
    private final ArrayList<TimeTableRes.TimeTableData> listData;
    private Context mContext;
    TimeTableListener listener;
    public TimeTableAdapter(ArrayList<TimeTableRes.TimeTableData> listData, TimeTableListener listener) {
        this.listData=listData;
        this.listener=listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.time_table_item,parent,false);
        return new ViewHolder(view);
    }
    private int dpToPx() {
        return mContext.getResources().getDimensionPixelSize(R.dimen.group_list_image_size);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final TimeTableRes.TimeTableData item = listData.get(position);
        holder.txt_name.setText(item.createdBy);
        holder.txt_title.setText(item.title);
        holder.txtDate.setText(MixOperations.getFormattedDate(item.createdAt, Constants.DATE_FORMAT));

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
                                            .buildRound(ImageUtil.getTextLetter(item.createdBy), ImageUtil.getRandomColor(position));
                                    holder.imgLead_default.setImageDrawable(drawable);
                                    AppLog.e("Picasso", "Error : ");
                                }
                            });
                        }
                    });
        } else {
            holder.imgLead_default.setVisibility(View.VISIBLE);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(ImageUtil.getTextLetter(item.createdBy), ImageUtil.getRandomColor(position));
            holder.imgLead_default.setImageDrawable(drawable);
        }
        holder.constThumb.setVisibility(View.GONE);
        if(!TextUtils.isEmpty(item.fileType))
        {
            if(item.fileType.equals(Constants.FILE_TYPE_IMAGE))
            {
                if (item.fileName!=null) {
               /* int height=0;
                if (item.imageHeight != 0 && item.imageWidth != 0)
                    height = (Constants.screen_width * item.imageHeight) / item.imageWidth;*/

                    ChildAdapter adapter;
                    if(item.fileName.size()==3)
                    {
                        adapter = new ChildAdapter(2, item.fileName.size(), mContext, item.fileName);
                    }
                    else
                    {
                        adapter = new ChildAdapter( Constants.MAX_IMAGE_NUM, item.fileName.size(), mContext, item.fileName);
                    }
                    holder.recyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(mContext, holder.recyclerView, adapter));
                    holder.recyclerView.setVisibility(View.VISIBLE);
                }
                holder.imgPlay.setVisibility(View.GONE);
                holder.imgPhoto.setVisibility(View.GONE);
            }
            else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
                holder.constThumb.setVisibility(View.VISIBLE);
                holder.imgPhoto.setVisibility(View.GONE);
                holder.recyclerView.setVisibility(View.GONE);
                holder.imgPlay.setVisibility(View.GONE);
                if(item.thumbnailImage!=null && item.thumbnailImage.size()>0)
                {
                    Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.thumbnailImage.get(0))).into(holder.imageThumb);

                }
                if (item.fileName != null && item.fileName.size() > 0) {
                    if (AmazoneDownload.isPdfDownloaded(item.fileName.get(0))) {
                        holder.imgDownloadPdf.setVisibility(View.GONE);
                    } else {
                        holder.imgDownloadPdf.setVisibility(View.VISIBLE);
                    }
                }

            }
           /* else if(item.fileType.equals(Constants.FILE_TYPE_YOUTUBE))
            {
                int height = (Constants.screen_width * 204) / 480;
                holder.imgPhoto.getLayoutParams().height = height;
                holder.imgPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Picasso.with(mContext).load(item.thumbnail).into(holder.imgPhoto);
                holder.imgPhoto.setVisibility(View.VISIBLE);
                holder.imgPlay.setVisibility(View.VISIBLE);
                holder.recyclerView.setVisibility(View.GONE);
            }*/
            else {
                holder.imgPhoto.setVisibility(View.GONE);
                holder.imgPlay.setVisibility(View.GONE);
                holder.recyclerView.setVisibility(View.GONE);
            }
        }
        else
        {
            holder.imgPhoto.setVisibility(View.GONE);
            holder.imgPlay.setVisibility(View.GONE);
            holder.recyclerView.setVisibility(View.GONE);
        }

        if (item.canEdit) {
            holder.txt_drop_delete.setVisibility(View.VISIBLE);
            holder.iv_delete.setVisibility(View.VISIBLE);
        } else {
            holder.txt_drop_delete.setVisibility(View.GONE);
            holder.iv_delete.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.txt_name)
        TextView txt_name;

        @Bind(R.id.img_lead_default)
        ImageView imgLead_default;

        @Bind(R.id.img_lead)
        ImageView imgLead;


        @Bind(R.id.txt_date)
        TextView txtDate;


        @Bind(R.id.iv_delete)
        ImageView iv_delete;

        @Bind(R.id.txt_title)
        TextView txt_title;

        @Bind(R.id.img_play)
        ImageView imgPlay;

        @Bind(R.id.image)
        ImageView imgPhoto;

        @Bind(R.id.constThumb)
        ConstraintLayout constThumb;

        @Bind(R.id.imageThumb)
        ImageView imageThumb;

        @Bind(R.id.txt_drop_delete)
        TextView txt_drop_delete;

        @Bind(R.id.lin_drop)
        LinearLayout lin_drop;

        @Bind(R.id.recyclerView)
        AsymmetricRecyclerView recyclerView;

        @Bind(R.id.imgDownloadPdf)
        ImageView imgDownloadPdf;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
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

        @OnClick({R.id.iv_delete,R.id.txt_drop_delete,R.id.rel})
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.rel:
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);

                    listener.onPostClick(listData.get(getAdapterPosition()));

                    break;

                case R.id.iv_delete:
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else
                        lin_drop.setVisibility(View.VISIBLE);
                    break;

                case R.id.txt_drop_delete:
                    lin_drop.setVisibility(View.GONE);
                    listener.onDeleteClick(listData.get(getAdapterPosition()));
                    break;
            }

        }
    }

    public interface TimeTableListener
    {
        public void onPostClick(TimeTableRes.TimeTableData galleryData);
        public void onDeleteClick(TimeTableRes.TimeTableData galleryData);
    }
}
