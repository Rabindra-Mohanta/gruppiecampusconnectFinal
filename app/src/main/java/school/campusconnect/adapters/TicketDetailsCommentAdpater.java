package school.campusconnect.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import school.campusconnect.R;
import school.campusconnect.databinding.ItemCommentTicketDetailsBinding;
import school.campusconnect.datamodel.comments.CommentTaskDetailsRes;
import school.campusconnect.datamodel.ticket.TicketListResponse;
import school.campusconnect.utils.DateTimeHelper;

public class TicketDetailsCommentAdpater extends RecyclerView.Adapter<TicketDetailsCommentAdpater.ViewHolder> {

    public static String TAG = "TicketDetailsCommentAdpater";
    Context context;
    ArrayList<CommentTaskDetailsRes.CommentData> commentTaskDetailsRes;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ItemCommentTicketDetailsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_comment_ticket_details,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CommentTaskDetailsRes.CommentData data = commentTaskDetailsRes.get(position);

        Log.e(TAG,"commentData"+new Gson().toJson(data));

        holder.binding.tvName.setText(data.getName());
        holder.binding.tvDesc.setText(data.getText());

       // holder.binding.tvcommentTime.setText(data.getInsertedAt());

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate = df.format(c);

        String[] TimeDate = data.getInsertedAt().split("T");
        String Date = TimeDate[0];
        String Time = TimeDate[1].substring(0,TimeDate[0].length()-1);

        try {
            Date date1 = df.parse(todayDate);
            Date date2 = df.parse(Date);

            if (date2.before(date1))
            {
                Time = DateTimeHelper.convertFormat(Time, "HH:mm:ss", "hh:mm a")+"\n"+Date;
            }
            else
            {
                Time = DateTimeHelper.convertFormat(Time, "HH:mm:ss", "hh:mm a");
            }
             holder.binding.tvcommentTime.setText(Time);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
    @Override
    public int getItemCount() {
        return commentTaskDetailsRes != null ? commentTaskDetailsRes.size() : 0;
    }

    public void add(ArrayList<CommentTaskDetailsRes.CommentData> commentTaskDetailsRes)
    {
        Log.e(TAG,"add");
        this.commentTaskDetailsRes = commentTaskDetailsRes;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemCommentTicketDetailsBinding binding;
        public ViewHolder(@NonNull ItemCommentTicketDetailsBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
