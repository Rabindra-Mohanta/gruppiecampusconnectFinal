package school.campusconnect.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;

public class TeamSubjectAdapter extends RecyclerView.Adapter<TeamSubjectAdapter.ViewHolder> {
    private Context mContext;
    ArrayList<AddSubjectData> list = new ArrayList<>();
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_subjects_rv, parent, false);
        return new ViewHolder(view);
    }
    public void addList(ArrayList<String> list){
        for (int i=0;i<list.size();i++){
            this.list.add(new AddSubjectData(list.get(i),"",""));
        }
        notifyDataSetChanged();
    }
    public ArrayList<AddSubjectData> getList(){
        return this.list;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
       /* if(i==list.size()){
            holder.tvSubject.setText("");
            holder.tvSubject.setEnabled(true);
            holder.imgDelete.setImageResource(R.drawable.add);
            holder.etMarks.setVisibility(View.GONE);
            holder.imgDelete.setColorFilter(null);
        }else {*/
            holder.tvSubject.setText(list.get(i).name);
            holder.tvSubject.setEnabled(false);
            holder.etMarks.setText(list.get(i).maxMarks);
            holder.etMinMarks.setText(list.get(i).minMarks);
            holder.etMarks.setVisibility(View.VISIBLE);
            holder.imgDelete.setImageResource(R.drawable.ic_close);
            holder.imgDelete.setColorFilter(ContextCompat.getColor(mContext, android.R.color.holo_red_dark), android.graphics.PorterDuff.Mode.SRC_IN);
        //}
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void add(AddSubjectData addSubjectData) {
        list.add(addSubjectData);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tvSubject)
        EditText tvSubject;
        @Bind(R.id.imgDelete)
        ImageView imgDelete;
        @Bind(R.id.etMarks)
        EditText etMarks;
        @Bind(R.id.etMinMarks)
        EditText etMinMarks;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        list.remove(getAdapterPosition());
                        notifyDataSetChanged();
                        //showAddSubjectDialog();
                }
            });

            etMarks.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    list.get(getAdapterPosition()).maxMarks = charSequence.toString();
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            etMinMarks.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    list.get(getAdapterPosition()).minMarks = charSequence.toString();
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
    }
/*
    private void showAddSubjectDialog() {
        final Dialog dialog=new Dialog(mContext,R.style.AppTheme_AlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_subject);
        final EditText etTitle=dialog.findViewById(R.id.etTitle);
        dialog.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(etTitle.getText().toString().trim())){
                    list.add(new AddSubjectData(etTitle.getText().toString(),""));
                    notifyDataSetChanged();
                    dialog.dismiss();
                }else {
                    Toast.makeText(mContext, "Please Add Subject", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }*/
    public static class AddSubjectData{
        public String name;
        public String maxMarks;
        public String minMarks;

        public AddSubjectData(String name, String maxMarks,String minMarks) {
            this.name = name;
            this.maxMarks = maxMarks;
            this.minMarks = minMarks;
        }

        public AddSubjectData() {
        }
    }
}