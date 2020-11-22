package school.campusconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import school.campusconnect.R;
import school.campusconnect.datamodel.ebook.EBooksResponse;

public class CustomSpinnerBook extends ArrayAdapter<EBooksResponse.EBookData> {
    public CustomSpinnerBook(@NonNull Context context, int resource, @NonNull List<EBooksResponse.EBookData> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_spinner_custom,parent, false);
        }
        EBooksResponse.EBookData rowItem = getItem(position);
        TextView tvItem = (TextView) convertView.findViewById(R.id.tvItem);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.tvSubject);
        if(rowItem.subjectBooks!=null){
            tvItem.setText(rowItem.className);
            txtTitle.setText(rowItem.subjectBooks.toString());
            txtTitle.setVisibility(View.VISIBLE);
        }else {
            tvItem.setText(rowItem.className);
            txtTitle.setVisibility(View.GONE);
        }
        return convertView;
    }
}
