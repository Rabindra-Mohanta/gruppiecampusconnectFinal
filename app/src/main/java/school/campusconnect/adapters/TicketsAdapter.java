package school.campusconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import school.campusconnect.R;
import school.campusconnect.databinding.ItemTicketsBinding;

public class TicketsAdapter extends RecyclerView.Adapter<TicketsAdapter.ViewHolder> {

    private OnClickListener listener;

    public TicketsAdapter(OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TicketsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTicketsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_tickets,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketsAdapter.ViewHolder holder, int position) {


        holder.binding.imgDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null)
                {
                    listener.add(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemTicketsBinding binding;
        public ViewHolder(@NonNull ItemTicketsBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public interface OnClickListener
    {
        void add(int id);
    }
}
