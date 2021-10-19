package school.campusconnect.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import school.campusconnect.activities.FullScreenMultiActivity;
import school.campusconnect.datamodel.fees.DueDates;
import school.campusconnect.datamodel.fees.FeePaidDetails;
import school.campusconnect.utils.AppDialog;

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
        holder.etDate.setText(list.get(i).paidDate);
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
                    Intent i = new Intent(mContext, FullScreenMultiActivity.class);
                    i.putStringArrayListExtra("image_list", list.get(getAdapterPosition()).attachment);
                    mContext.startActivity(i);
                }
            });

          /*  if(isFromUpdate){
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
                        list.get(getAdapterPosition()).setAmountPaid(charSequence.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }else {*/
                etDateAmount.setFocusable(false);
//            }

        }
    }/*
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
    }*/
}