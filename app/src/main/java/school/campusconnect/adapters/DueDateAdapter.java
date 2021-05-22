package school.campusconnect.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.fees.DueDates;
import school.campusconnect.datamodel.fees.FeesDetailTemp;

public class DueDateAdapter extends RecyclerView.Adapter<DueDateAdapter.ViewHolder> {
    private Context mContext;
    ArrayList<DueDates> list = new ArrayList<>();

    String role="admin";
    boolean isEdit=false;

    public DueDateAdapter() {
    }

    public DueDateAdapter(String role, boolean isEdit) {
        this.role = role;
        this.isEdit = isEdit;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_due_date, parent, false);
        return new ViewHolder(view);
    }

    public void addList(ArrayList<DueDates> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }
    public void add(DueDates dueDate) {
        list.add(dueDate);
        notifyDataSetChanged();
    }

    public ArrayList<DueDates> getList() {
        return this.list;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.etDate.setText(list.get(i).getDate());
        holder.etDateAmount.setText(list.get(i).getMinimumAmount());

        if("admin".equalsIgnoreCase(role) && isEdit){
            holder.chkCompleted.setVisibility(View.VISIBLE);
        }else {
            holder.chkCompleted.setVisibility(View.GONE);
        }

        if("completed".equalsIgnoreCase(list.get(i).getStatus())){
            holder.chkCompleted.setChecked(true);
        }else {
            holder.chkCompleted.setChecked(false);
        }
        holder.chkCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.chkCompleted.isChecked()){
                    list.get(i).setStatus("completed");
                }else {
                    list.get(i).setStatus(null);
                }
            }
        });
        holder.chkCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
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
        @Bind(R.id.chkCompleted)
        CheckBox chkCompleted;

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