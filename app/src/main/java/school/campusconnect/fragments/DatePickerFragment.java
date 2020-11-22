package school.campusconnect.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import school.campusconnect.R;
import school.campusconnect.views.SMBAlterDialog;

public class DatePickerFragment extends DialogFragment {

    private OnDateSelectListener onDateSelectListener;

    public static DatePickerFragment newInstance() {
       return new DatePickerFragment();
    }



    public void setOnDateSelectListener(OnDateSelectListener listener) {
        onDateSelectListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        SMBAlterDialog dialog = new SMBAlterDialog(getContext());

        dialog.setTitle(R.string.hint_lead_dob);
        //bug in support lib http://stackoverflow.com/questions/32784009/styling-custom-dialog-fragment-not-working
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null);

        dialog.setView(view);


        dialog.setPositiveButtonClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog dd = (AlertDialog) dialog;
                DatePicker datePicker = (DatePicker) dd.findViewById(R.id.datePicker1);
                Calendar c = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
                c.setTimeInMillis(System.currentTimeMillis());
                c.set(Calendar.YEAR, datePicker.getYear());
                c.set(Calendar.MONTH, datePicker.getMonth());
                c.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                onDateSelectListener.onDateSelected( c);
            }
        });
        AlertDialog dd = dialog.show();
        return dd;
    }



    public interface OnDateSelectListener {
        void onDateSelected( Calendar c);
    }

}
