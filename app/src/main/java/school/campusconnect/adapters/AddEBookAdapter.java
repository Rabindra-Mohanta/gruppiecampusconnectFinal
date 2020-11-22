package school.campusconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.ebook.EBooksResponse;

public class AddEBookAdapter extends RecyclerView.Adapter<AddEBookAdapter.ViewHolder> {
    private Context mContext;
    ArrayList<EBooksResponse.SubjectBook> list = new ArrayList<>();
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ebook_add, parent, false);
        return new ViewHolder(view);
    }

    public ArrayList<EBooksResponse.SubjectBook> getList(){
        return this.list;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
            holder.tvSubject.setText(list.get(i).subjectName);
            holder.etDesc.setText(list.get(i).description);
            holder.etPdf.setText(list.get(i).fileName.size() + " book selected");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void add(EBooksResponse.SubjectBook addSubjectData) {
        list.add(addSubjectData);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tvSubject)
        EditText tvSubject;
        @Bind(R.id.etDesc)
        EditText etDesc;

        @Bind(R.id.imgDelete)
        ImageView imgDelete;
        @Bind(R.id.etPdf)
        TextView etPdf;

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
        }
    }
}