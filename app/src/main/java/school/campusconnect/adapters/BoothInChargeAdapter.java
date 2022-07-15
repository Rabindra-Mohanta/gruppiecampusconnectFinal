package school.campusconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import school.campusconnect.R;
import school.campusconnect.databinding.ItemBoothInChargeBinding;
import school.campusconnect.datamodel.ticket.TicketListResponse;

public class BoothInChargeAdapter extends RecyclerView.Adapter<BoothInChargeAdapter.ViewHolder> {
    private ArrayList<TicketListResponse.BoothInchargeData> boothInchargeData;
    Context context;
    @NonNull
    @Override
    public BoothInChargeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ItemBoothInChargeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_booth_in_charge,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BoothInChargeAdapter.ViewHolder holder, int position) {

        TicketListResponse.BoothInchargeData data = boothInchargeData.get(position);

        holder.binding.tvName.setText(data.getName());
        holder.binding.tvTeamName.setText(data.getTeamName());
        holder.binding.tvDesg.setText(data.getConstituencyDesignation());

        holder.binding.imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + data.getPhone()));
                context.startActivity(intent);
            }
        });
    }
    public void add(ArrayList<TicketListResponse.BoothInchargeData> boothInchargeData)
    {
        this.boothInchargeData = boothInchargeData;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return boothInchargeData != null ? boothInchargeData.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemBoothInChargeBinding binding;
        public ViewHolder(@NonNull ItemBoothInChargeBinding itemView) {
            super(itemView.getRoot());

            binding = itemView;
        }
    }
}
