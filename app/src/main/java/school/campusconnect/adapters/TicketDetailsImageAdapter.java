package school.campusconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import school.campusconnect.Assymetric.AGVRecyclerViewAdapter;
import school.campusconnect.Assymetric.AsymmetricItem;
import school.campusconnect.Assymetric.multiimages.ItemImage;
import school.campusconnect.R;
import school.campusconnect.activities.FullScreenActivity;
import school.campusconnect.activities.FullScreenMultiActivity;
import school.campusconnect.activities.FullScreenVideoMultiActivity;
import school.campusconnect.activities.GalleryDetailActivity;
import school.campusconnect.activities.VideoPlayActivity;
import school.campusconnect.datamodel.GalleryPostRes;
import school.campusconnect.datamodel.ticket.TicketListResponse;
import school.campusconnect.utils.Constants;

public class TicketDetailsImageAdapter extends AGVRecyclerViewAdapter<TicketDetailsImageAdapter.ViewHolder> {

    private List<ItemImage> items;
    private final ArrayList<String> allImageList;
    private int currentOffset=0;
    private boolean isCol2Avail=false;
    private int mDisplay = 0;
    private int mTotal = 0;
    private Context context;
    TicketListResponse.TicketData item;
    ListenerOnclick listener;
    public TicketDetailsImageAdapter(ListenerOnclick listener,int mDisplay, int mTotal, Context context, TicketListResponse.TicketData item) {
        this.listener = listener;
        this.allImageList = item.getFileName();
        this.item = item;
        this.mDisplay = mDisplay;
        this.mTotal = mTotal;
        this.context = context;
        for (String s : allImageList) {
            Log.e("ChildAdapter Images", s);
        }

        items=new ArrayList<>();

        // ArrayList<ItemImage> tempData=new ArrayList<>();
        for (int i = 0; i < allImageList.size(); i++) {
            ItemImage itemImage = new ItemImage(allImageList.get(i));
            int colSpan1;
            int rowSpan1;

            if (allImageList.size() == 1) {
                colSpan1 = 2;
                rowSpan1 = 2;
            } else {
                colSpan1 = 1;
                rowSpan1 = 1;
            }

            if (colSpan1 == 2 && !isCol2Avail)
                isCol2Avail = true;
            else if (colSpan1 == 2 && isCol2Avail)
                colSpan1 = 1;

            itemImage.setColumnSpan(colSpan1);
            itemImage.setRowSpan(rowSpan1);
            itemImage.setPosition(currentOffset + i);
            items.add(itemImage);

        }
    }

    @Override
    public AsymmetricItem getItem(int position) {
        return (AsymmetricItem) items.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? 1 : 0;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("RecyclerViewActivity", "onCreateView");
        return new ViewHolder(parent, viewType, items, allImageList);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketDetailsImageAdapter.ViewHolder holder, int position) {
        Log.d("RecyclerViewActivity", "onBindView position=" + position);
        holder.bind(items, position, mDisplay, mTotal, context);

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null)
                {
                    listener.onClick(item,allImageList);
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        if(mDisplay==2 && items.size()>2)
            return 2;
        else if(mDisplay==4 && items.size()>4)
        {
            return 4;
        }
        else
        {
            return items.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final ImageView mImageView;
        private final TextView textView;
        private final ArrayList<String> allImageList;
        public ViewHolder(ViewGroup parent, int viewType, List<ItemImage> items, ArrayList<String> allImageList) {
            super(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.adapter_item, parent, false));

            this.allImageList = allImageList;
            for (String s : allImageList) {
                Log.e("ViewHolder Images", s);
            }
            mImageView = (ImageView) itemView.findViewById(R.id.mImageView);
            textView = (TextView) itemView.findViewById(R.id.tvCount);
        }


        public void bind(final List<ItemImage> item, final int position, int mDisplay, int mTotal, final Context mContext) {

            Log.e("MULTI_BIND", "image " + position + "is " + Constants.decodeUrlToBase64(item.get(position).getImagePath()));

            Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.get(position).getImagePath())).placeholder(R.drawable.placeholder_image).fit().centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(mImageView,
                    new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.get(position).getImagePath())).placeholder(R.drawable.placeholder_image).fit().centerCrop().into(mImageView, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Log.e("Picasso", "Error : ");
                                }
                            });
                        }
                    });

            textView.setText("+" + (mTotal - mDisplay));
            if (mTotal > mDisplay) {
                if (position == mDisplay - 1) {
                    textView.setVisibility(View.VISIBLE);
                    mImageView.setAlpha(72);
                } else {
                    textView.setVisibility(View.INVISIBLE);
                    mImageView.setAlpha(255);
                }
            } else {
                mImageView.setAlpha(255);
                textView.setVisibility(View.INVISIBLE);
            }



        }
    }

    public interface ListenerOnclick
    {
        void onClick(TicketListResponse.TicketData item, ArrayList<String> allImageList);
    }
}
