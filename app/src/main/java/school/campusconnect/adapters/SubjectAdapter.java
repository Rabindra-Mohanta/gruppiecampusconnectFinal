package school.campusconnect.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<String> list = new ArrayList<>();
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subjects_rv, parent, false);
        return new ViewHolder(view);
    }
    public void addList(ArrayList<String> list){
        this.list.addAll(list);
        notifyDataSetChanged();
    }
    public ArrayList<String> getList(){
        return this.list;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        if(i==list.size()){
            holder.tvSubject.setText("");
            holder.tvSubject.setEnabled(true);
            holder.imgDelete.setImageResource(R.drawable.add);
            holder.imgDelete.setColorFilter(null);
        }else {
            holder.tvSubject.setText(list.get(i));
            holder.tvSubject.setEnabled(false);
            holder.imgDelete.setImageResource(R.drawable.ic_close);
            holder.imgDelete.setColorFilter(ContextCompat.getColor(mContext, android.R.color.holo_red_dark), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public int getItemCount() {
        return list.size()+1;
    }

    public void add(String val) {
        list.add(val);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tvSubject)
        EditText tvSubject;
        @Bind(R.id.imgDelete)
        ImageView imgDelete;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(list.size()!=getAdapterPosition()){
                        list.remove(getAdapterPosition());
                        notifyDataSetChanged();
                    }else {
                        //showAddSubjectDialog();
                        if(!TextUtils.isEmpty(tvSubject.getText().toString().trim())){
                            list.add(tvSubject.getText().toString());
                            notifyDataSetChanged();
                            hide_keyboard(v);
                        }else {
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.toast_please_add_subject), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }
    public void hide_keyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (view == null) {
            view = new View(mContext);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showAddSubjectDialog() {
        final Dialog dialog=new Dialog(mContext,R.style.AppTheme_AlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_subject);
        final EditText etTitle=dialog.findViewById(R.id.etTitle);
        dialog.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(etTitle.getText().toString().trim())){
                    list.add(etTitle.getText().toString());
                    notifyDataSetChanged();
                    dialog.dismiss();
                }else {
                    Toast.makeText(mContext,  mContext.getResources().getString(R.string.toast_please_add_subject), Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }
}
