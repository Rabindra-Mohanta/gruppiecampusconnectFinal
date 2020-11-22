package school.campusconnect.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.OtherLeadItem;


public class OtherLeadAdapter extends RecyclerView.Adapter<OtherLeadAdapter.ImageViewHolder> {
    private List<OtherLeadItem> list = Collections.emptyList();
    private OnLeadSelectListener listener;


    public OtherLeadAdapter(List<OtherLeadItem> list, OnLeadSelectListener listener) {
        if (list == null) return;
        this.list = list;
        this.listener = listener;
    }


    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other_lead, parent, false);
        return new ImageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        OtherLeadItem item = list.get(position);
        holder.txtName.setText(item.getName());

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

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.txt_name)
        TextView txtName;
        @Bind(R.id.img_call)
        ImageView imgPhone;
        @Bind(R.id.img_chat)
        ImageView imgSMS;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_call:
                    listener.onCallClick(list.get(getLayoutPosition()));
                    break;
                case R.id.img_chat:
                    listener.onSMSClick(list.get(getLayoutPosition()));
                    break;

            }
        }
    }

    public interface OnLeadSelectListener {
        void onCallClick(OtherLeadItem item);

        void onSMSClick(OtherLeadItem item);

        void onEmailClick(OtherLeadItem item);
    }
}


