package school.campusconnect.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import school.campusconnect.R;
import school.campusconnect.datamodel.subjects.SubjectResponse;

public class CustomSpinnerSubject extends ArrayAdapter<SubjectResponse.SubjectData> {
    public CustomSpinnerSubject(@NonNull Context context, int resource, @NonNull List<SubjectResponse.SubjectData> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_spinner_custom,parent, false);
        }
        SubjectResponse.SubjectData rowItem = getItem(position);
        TextView tvItem = (TextView) convertView.findViewById(R.id.tvItem);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.tvSubject);
        if(rowItem.subjects!=null){
            tvItem.setText(rowItem.name);
            txtTitle.setText(rowItem.subjects.toString());
            txtTitle.setVisibility(View.VISIBLE);
        }else {
            tvItem.setText(rowItem.name);
            txtTitle.setVisibility(View.GONE);
        }
        return convertView;
    }
}
