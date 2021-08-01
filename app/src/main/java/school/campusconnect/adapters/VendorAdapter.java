package school.campusconnect.adapters;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.activities.VendorReadMoreActivity;
import school.campusconnect.datamodel.VendorPostResponse;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.MixOperations;

public class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.ViewHolder> {
    private final ArrayList<VendorPostResponse.VendorPostData> listData;
    private final String role;
    private Context mContext;
    VendorListener listener;
    public VendorAdapter(ArrayList<VendorPostResponse.VendorPostData> listData, VendorListener listener, String role) {
        this.listData=listData;
        this.listener=listener;
        this.role=role;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.vendor_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final VendorPostResponse.VendorPostData item = listData.get(position);
        holder.txt_title.setText(item.vendor);

        if(!TextUtils.isEmpty(item.description))
        {
            holder.txt_content.setVisibility(View.VISIBLE);
            if(item.description.length()>100)
            {
                StringBuilder stringBuilder=new StringBuilder(item.description);
                stringBuilder.setCharAt(97,'.');
                stringBuilder.setCharAt(98,'.');
                stringBuilder.setCharAt(99,'.');
                holder.txt_content.setText(stringBuilder);
            }
            else
            {
                holder.txt_content.setText(item.description);
            }
        }
        else
        {
            holder.txt_content.setVisibility(View.GONE);
        }
        holder.imgDownloadPdf.setVisibility(View.GONE);
        holder.txtDate.setText(MixOperations.getFormattedDate(item.createdAt, Constants.DATE_FORMAT));
        if(!TextUtils.isEmpty(item.fileType))
        {
            switch (item.fileType) {
                case Constants.FILE_TYPE_IMAGE:
                    if (item.fileName != null) {
                        holder.imgPhoto.setVisibility(View.VISIBLE);
                        Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.fileName.get(0))).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.novender).error(R.drawable.novender).into(holder.imgPhoto, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.fileName.get(0))).placeholder(R.drawable.novender).error(R.drawable.novender).into(holder.imgPhoto);
                            }
                        });
                    }
                    break;
                case Constants.FILE_TYPE_PDF:
                    if(item.thumbnailImage!=null && item.thumbnailImage.size()>0)
                    {
                        Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.thumbnailImage.get(0))).into(holder.imgPhoto);

                    }
                    if (item.fileName != null && item.fileName.size() > 0) {
                        if (AmazoneDownload.isPdfDownloaded(item.fileName.get(0))) {
                            holder.imgDownloadPdf.setVisibility(View.GONE);
                        } else {
                            holder.imgDownloadPdf.setVisibility(View.VISIBLE);
                        }
                    }

                    break;
                default:
                    holder.imgPhoto.setImageResource(R.drawable.novender);
                    break;
            }
        }
        else
        {
            holder.imgPhoto.setImageResource(R.drawable.novender);
        }

        if ("admin".equalsIgnoreCase(role)) {
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
        @Bind(R.id.txt_date)
        TextView txtDate;

        @Bind(R.id.iv_delete)
        ImageView iv_delete;

        @Bind(R.id.txt_title)
        TextView txt_title;

        @Bind(R.id.txt_content)
        TextView txt_content;

        @Bind(R.id.imageThumb)
        ImageView imgPhoto;

        @Bind(R.id.txt_drop_delete)
        TextView txt_drop_delete;

        @Bind(R.id.lin_drop)
        LinearLayout lin_drop;

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
                    else
                    {
                        Intent intent = new Intent(mContext, VendorReadMoreActivity.class);
                        Gson gson = new Gson();
                        String data = gson.toJson(listData.get(getAdapterPosition()));
                        intent.putExtra("data",data);
                        mContext.startActivity(intent);
                    }
                }
            });
        }

        @OnClick({R.id.iv_delete,R.id.txt_drop_delete,R.id.rel,R.id.txt_readmore})
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

    public interface VendorListener
    {
        public void onPostClick(VendorPostResponse.VendorPostData galleryData);
        public void onDeleteClick(VendorPostResponse.VendorPostData galleryData);
    }
}
