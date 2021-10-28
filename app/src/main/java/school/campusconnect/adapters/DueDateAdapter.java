package school.campusconnect.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.fees.DueDates;
import school.campusconnect.datamodel.fees.FeesDetailTemp;
import school.campusconnect.utils.AppDialog;

public class DueDateAdapter extends RecyclerView.Adapter<DueDateAdapter.ViewHolder> {
    private Context mContext;
    ArrayList<DueDates> list = new ArrayList<>();

    String role="admin";
    boolean isEdit=false;
    boolean isFromUpdate = false;
    public DueDateAdapter(boolean isFromUpdate) {
        this.isFromUpdate = isFromUpdate;
    }

    public DueDateAdapter(String role, boolean isEdit,boolean isFromUpdate) {
        this.role = role;
        this.isEdit = isEdit;
        this.isFromUpdate = isFromUpdate;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_due_date, parent, false);
        return new ViewHolder(view);
    }

    public void addList(ArrayList<DueDates> list) {
        if(list==null)
            return;

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
            holder.imgDelete.setVisibility(View.GONE);
        }else {
            holder.imgDelete.setVisibility(View.VISIBLE);
            holder.chkCompleted.setVisibility(View.GONE);
        }

        if("completed".equalsIgnoreCase(list.get(i).getStatus())){
            holder.chkCompleted.setChecked(true);
        }else {
            holder.chkCompleted.setChecked(false);
        }
       /* holder.chkCompleted.setOnClickListener(new View.OnClickListener() {
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
        });*/
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
//            chkCompleted.setEnabled(false);
            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isFromUpdate){
                        AppDialog.showConfirmDialog(mContext, "Are you sure you want to delete?", new AppDialog.AppDialogListener() {
                            @Override
                            public void okPositiveClick(DialogInterface dialog) {
                                list.remove(getAdapterPosition());
                                notifyDataSetChanged();
                            }

                            @Override
                            public void okCancelClick(DialogInterface dialog) {

                            }
                        });
                    }else {
                        list.remove(getAdapterPosition());
                        notifyDataSetChanged();
                    }
                }
            });

            if(isFromUpdate){
                etDateAmount.setFocusable(true);
                etDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDateDialog(etDate,getAdapterPosition());
                    }
                });
                etDateAmount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        list.get(getAdapterPosition()).setMinimumAmount(charSequence.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }else {
                etDateAmount.setFocusable(false);
            }

        }
    }

    private void showDateDialog(EditText etDate, int adapterPosition){
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog fragment = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                etDate.setText(format.format(calendar.getTime()));
                list.get(adapterPosition).setDate(format.format(calendar.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        fragment.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        fragment.show();
    }
}