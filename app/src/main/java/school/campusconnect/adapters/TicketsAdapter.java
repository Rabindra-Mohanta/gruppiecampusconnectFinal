package school.campusconnect.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import school.campusconnect.R;
import school.campusconnect.databinding.ItemTicketsBinding;
import school.campusconnect.datamodel.ticket.TicketListResponse;
import school.campusconnect.utils.Constants;

public class TicketsAdapter extends RecyclerView.Adapter<TicketsAdapter.ViewHolder> {

    private OnClickListener listener;
    private Context mContext;
    private ArrayList<TicketListResponse.TicketData> ticketData;
    public TicketsAdapter(OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TicketsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        mContext = parent.getContext();
        ItemTicketsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_tickets,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketsAdapter.ViewHolder holder, int position) {

        TicketListResponse.TicketData data = ticketData.get(position);

        if (data.getIssueCreatedByName() != null)
        {
            holder.binding.tvName.setText(data.getIssueCreatedByName());
        }
        else
        {
            holder.binding.tvName.setText("");
        }

        if (data.getConstituencyIssue() != null && data.getConstituencyIssueJurisdiction() != null)
        {
            holder.binding.tvIssue.setText(data.getConstituencyIssue() +"-"+data.getConstituencyIssueJurisdiction());
        }
        else
        {
            holder.binding.tvIssue.setText("");
        }



    /*    holder.binding.tvIssue.setText("Issue - "+data.getConstituencyIssue());

        if (data.getBoothCoordinators() != null && data.getBoothCoordinators().size()>0)
        {
            holder.binding.tvCordinator.setText("Coordinator - "+data.getBoothCoordinators().get(0).getName());
        }*/

        if (data.getFileName() != null && data.getFileName().size()>0)
        {
            Picasso.with(mContext).load(Constants.decodeUrlToBase64(data.getFileName().get(0).toString())).placeholder(R.drawable.placeholder_image).networkPolicy(NetworkPolicy.OFFLINE).into(holder.binding.imgTicket,
                    new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(mContext).load(Constants.decodeUrlToBase64(data.getFileName().get(0).toString())).placeholder(R.drawable.placeholder_image).into(holder.binding.imgTicket, new Callback() {
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
        }




        holder.binding.imgDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null)
                {
                    listener.add(data);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null)
                {
                    listener.add(data);
                }
            }
        });
    }
    public void addData(ArrayList<TicketListResponse.TicketData> ticketData)
    {
        this.ticketData = ticketData;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return ticketData != null ? ticketData.size() : 0;
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
        void add(TicketListResponse.TicketData id);
    }
}
