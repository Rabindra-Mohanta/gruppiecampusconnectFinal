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
import school.campusconnect.databinding.ItemBoothCordinatorBinding;
import school.campusconnect.datamodel.ticket.TicketListResponse;

public class BoothCordinatorAdapter extends RecyclerView.Adapter<BoothCordinatorAdapter.ViewHolder> {
    private ArrayList<TicketListResponse.BoothCoordinatorData> boothCoordinators;
    Context context;
    @NonNull
    @Override
    public BoothCordinatorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ItemBoothCordinatorBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_booth_cordinator,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BoothCordinatorAdapter.ViewHolder holder, int position) {
        TicketListResponse.BoothCoordinatorData data = boothCoordinators.get(position);

        holder.binding.tvName.setText(data.getName());
        holder.binding.tvDesg.setText(data.getConstituencyDesignation());

        holder.binding.imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + data.getPhone()));
                context.startActivity(intent);
            }
        });
    }
    public void add(ArrayList<TicketListResponse.BoothCoordinatorData> boothCoordinators)
    {
        this.boothCoordinators = boothCoordinators;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return boothCoordinators != null ? boothCoordinators.size() :0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemBoothCordinatorBinding binding;
        public ViewHolder(@NonNull ItemBoothCordinatorBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
