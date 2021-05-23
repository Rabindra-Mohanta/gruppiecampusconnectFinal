package school.campusconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.fees.DueDates;
import school.campusconnect.datamodel.fees.FeePaidDetails;

public class PaidDateAdapter extends RecyclerView.Adapter<PaidDateAdapter.ViewHolder> {
    private Context mContext;
    ArrayList<FeePaidDetails> list = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paid_date, parent, false);
        return new ViewHolder(view);
    }

    public void addList(ArrayList<FeePaidDetails> list) {
        if(list==null)
            return;

        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }
    public void add(FeePaidDetails dueDate) {
        list.add(dueDate);
        notifyDataSetChanged();
    }

    public ArrayList<FeePaidDetails> getList() {
        return this.list;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.etDate.setText(list.get(i).getDate());
        holder.etDateAmount.setText(list.get(i).getAmountPaid());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.etDate)
        EditText etDate;
        @Bind(R.id.etDateAmount)
        EditText etDateAmount;
        @Bind(R.id.imgDelete)
        ImageView imgDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(getAdapterPosition());
                    notifyDataSetChanged();
                    //showAddSubjectDialog();
                }
            });

        }
    }
}