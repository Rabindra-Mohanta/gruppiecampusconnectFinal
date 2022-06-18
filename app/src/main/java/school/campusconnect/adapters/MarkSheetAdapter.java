package school.campusconnect.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.datamodel.MarkSheetListResponse;

public class MarkSheetAdapter extends RecyclerView.Adapter<MarkSheetAdapter.ViewHolder> {
    private final ArrayList<MarkSheetListResponse.MarkSheetData> listData;
    private Context mContext;
    VendorListener listener;
    boolean canEdit;
    public MarkSheetAdapter(ArrayList<MarkSheetListResponse.MarkSheetData> listData, VendorListener listener,boolean canEdit) {
        this.listData=listData;
        this.listener=listener;
        this.canEdit = canEdit;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.marksheet_item,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        SubjectMarksAdapter adapter = new SubjectMarksAdapter(listData.get(position).subjectMarks,listData.get(position).maxMarks,listData.get(position).minMarks);
        holder.rvMarks.setAdapter(adapter);
        if (canEdit) {
            holder.txt_drop_delete.setVisibility(View.VISIBLE);
            holder.iv_delete.setVisibility(View.VISIBLE);
        } else {
            holder.txt_drop_delete.setVisibility(View.GONE);
            holder.iv_delete.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.iv_delete)
        ImageView iv_delete;

        @Bind(R.id.rvMarks)
        RecyclerView rvMarks;

        @Bind(R.id.txt_drop_delete)
        TextView txt_drop_delete;

        @Bind(R.id.lin_drop)
        LinearLayout lin_drop;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else {
                        listener.onClick(listData.get(getAdapterPosition()));
                    }
                }
            });

        }

        @OnClick({R.id.iv_delete,R.id.txt_drop_delete})
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.rel:
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else
                        listener.onPostClick(listData.get(getAdapterPosition()));
                    break;

                case R.id.iv_delete:
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else
                        lin_drop.setVisibility(View.VISIBLE);
                    break;

                case R.id.txt_drop_delete:
                    lin_drop.setVisibility(View.GONE);
                    listener.onDeleteClick(listData.get(getAdapterPosition()));
                    break;
            }

        }
    }

    public interface VendorListener
    {
        public void onDeleteClick(MarkSheetListResponse.MarkSheetData galleryData);

        void onPostClick(MarkSheetListResponse.MarkSheetData markSheetData);

        void onClick(MarkSheetListResponse.MarkSheetData markSheetData);
    }

    class SubjectMarksAdapter extends RecyclerView.Adapter<SubjectMarksAdapter.ViewHolder>{

        private final ArrayList<Map<String, String>> subjectMarks;
        private final ArrayList<Map<String, String>> maxMarks;
        private final ArrayList<Map<String, String>> minMarks;

        public SubjectMarksAdapter(ArrayList<Map<String, String>> subjectMarks, ArrayList<Map<String, String>> maxMarks, ArrayList<Map<String, String>> minMarks) {
            this.subjectMarks = subjectMarks;
            this.maxMarks = maxMarks;
            this.minMarks = minMarks;
        }

        @NonNull
        @Override
        public SubjectMarksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_show_subject_marks, viewGroup, false);
            return new SubjectMarksAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SubjectMarksAdapter.ViewHolder viewHolder, int i) {
            final Map.Entry item = getItem(i);
            final Map.Entry itemMax = getItemMax(i);
            final Map.Entry itemMin = getItemMin(i);
            if(item!=null){
                viewHolder.tvSubject.setText(item.getKey().toString()+"");
                viewHolder.etMarks.setText(item.getValue().toString()+"");
                viewHolder.etMarksMax.setText(itemMax.getValue().toString()+"");
                viewHolder.etMarksMin.setText(itemMin.getValue().toString()+"");

                viewHolder.etMarks.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String maxMarksInput = viewHolder.etMarksMax.getText().toString();
                        String inputMarks = s.toString();
                        Float inputMarksFloat = Float.valueOf(inputMarks);
                        if (!maxMarksInput.isEmpty()) {
                            Float maxMarksFloat = Float.valueOf(maxMarksInput);
                            if (maxMarksFloat > inputMarksFloat) {
                                item.setValue(s.toString());
                                viewHolder.etMarks.setText(s.toString());
                            } else {
                                viewHolder.etMarks.setText(item.getValue().toString());
                            }
                        } else {
                            item.setValue(s.toString());
                            viewHolder.etMarks.setText(s.toString());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        }
        private Map.Entry getItem(int position){
            Map<String, String> item = subjectMarks.get(0);
            Iterator<Map.Entry<String, String>> it = item.entrySet().iterator();
            int cnt = 0;
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                if(cnt==position){
                    return pair;
                }
                cnt++;
            }
            return null;
        }
        private Map.Entry getItemMax(int position){
            Map<String, String> item = maxMarks.get(0);
            Iterator<Map.Entry<String, String>> it = item.entrySet().iterator();
            int cnt = 0;
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                if(cnt==position){
                    return pair;
                }
                cnt++;
            }
            return null;
        }
        private Map.Entry getItemMin(int position){
            Map<String, String> item = minMarks.get(0);
            Iterator<Map.Entry<String, String>> it = item.entrySet().iterator();
            int cnt = 0;
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                if(cnt==position){
                    return pair;
                }
                cnt++;
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return subjectMarks.get(0).entrySet().size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.tvSubject)
            TextView tvSubject;
            @Bind(R.id.etMarks)
            TextView etMarks;
            @Bind(R.id.etMarksMax)
            TextView etMarksMax;
            @Bind(R.id.etMarksMin)
            TextView etMarksMin;



            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
    }
}
