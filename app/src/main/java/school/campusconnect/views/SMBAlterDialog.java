package school.campusconnect.views;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;


public class SMBAlterDialog extends AlertDialog.Builder {


    public SMBAlterDialog(Context context) {

        super(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);

//        super.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
    }

    public void setPositiveButtonClickListener(DialogInterface.OnClickListener listener) {
        super.setPositiveButton(android.R.string.ok, listener);

    }


    public void setNegativeButtonWithListener() {
        super.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

}
