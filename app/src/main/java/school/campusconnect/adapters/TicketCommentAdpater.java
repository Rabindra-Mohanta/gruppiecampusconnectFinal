package school.campusconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import school.campusconnect.R;
import school.campusconnect.databinding.ItemCommentTicketDetailsBinding;

public class TicketCommentAdpater extends RecyclerView.Adapter<TicketCommentAdpater.ViewHolder> {
    Context context;
    @NonNull
    @Override
    public TicketCommentAdpater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ItemCommentTicketDetailsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_comment_ticket_details,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketCommentAdpater.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemCommentTicketDetailsBinding binding;
        public ViewHolder(@NonNull ItemCommentTicketDetailsBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
