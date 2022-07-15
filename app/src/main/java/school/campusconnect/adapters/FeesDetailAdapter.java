package school.campusconnect.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.fees.FeesDetailTemp;
import school.campusconnect.utils.AppDialog;

public class FeesDetailAdapter extends RecyclerView.Adapter<FeesDetailAdapter.ViewHolder> {
    private Context mContext;
    ArrayList<FeesDetailTemp> list = new ArrayList<>();
    boolean isFromUpdate = false;

    public FeesDetailAdapter(boolean isFromUpdate) {
        this.isFromUpdate = isFromUpdate;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fees_detail, parent, false);
        return new ViewHolder(view);
    }

    public void addList(ArrayList<FeesDetailTemp> list) {
        if (list == null)
            return;

        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void add(FeesDetailTemp feesDetailTemp) {
        list.add(feesDetailTemp);
        notifyDataSetChanged();
    }

    public ArrayList<FeesDetailTemp> getList() {
        return this.list;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.etFeesType.setText(list.get(i).getType());
        holder.etFeesTypeVal.setText(list.get(i).getAmount());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addAll(HashMap<String, String> feeDetails) {
        if (feeDetails != null && feeDetails.size() > 0) {
            list = new ArrayList<>();
            for (Map.Entry<String, String> stringStringEntry : feeDetails.entrySet()) {
                list.add(new FeesDetailTemp(stringStringEntry.getKey(), stringStringEntry.getValue()));
            }
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.etFeesType)
        EditText etFeesType;
        @Bind(R.id.etFeesTypeVal)
        EditText etFeesTypeVal;
        @Bind(R.id.imgDelete)
        ImageView imgDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFromUpdate) {
                        AppDialog.showConfirmDialog(mContext, mContext.getResources().getString(R.string.dialog_are_you_want_to_delete), new AppDialog.AppDialogListener() {
                            @Override
                            public void okPositiveClick(DialogInterface dialog) {
                                list.remove(getAdapterPosition());
                                notifyDataSetChanged();
                            }

                            @Override
                            public void okCancelClick(DialogInterface dialog) {

                            }
                        });
                    } else {
                        list.remove(getAdapterPosition());
                        notifyDataSetChanged();
                    }
                }
            });


            if (isFromUpdate) {
                etFeesType.setFocusable(true);
                etFeesTypeVal.setFocusable(true);
                etFeesType.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        list.get(getAdapterPosition()).setType(charSequence.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                etFeesTypeVal.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        list.get(getAdapterPosition()).setAmount(charSequence.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }else {
                etFeesType.setFocusable(false);
                etFeesTypeVal.setFocusable(false);
            }

        }
    }
}