package school.campusconnect.utils;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.Window;

import org.jetbrains.annotations.NotNull;

import school.campusconnect.R;


public class AppDialog {
    static AlertDialog dialog;
    public static AlertDialog showAlertDialog(Context context, String msg) {
        if (dialog != null) {
            dialog.dismiss();
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        return dialog;
    }

    public static AlertDialog showConfirmDialog(Context context, String msg, final AppDialogListener listener) {
        if (dialog != null) {
            dialog.dismiss();
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.strYes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(listener!=null){
                    listener.okPositiveClick(dialog);
                }
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.strNo), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(listener!=null){
                    listener.okCancelClick(dialog);
                }
            }
        });
        dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        return dialog;
    }
    public interface AppDialogListener{
        void okPositiveClick(DialogInterface dialog);
        void okCancelClick(DialogInterface dialog);
    }
    public static AlertDialog showUpdateDialog(Context context, String msg,@NotNull AppUpdateDialogListener appUpdateDialogListener) {
        if (dialog != null) {
            dialog.dismiss();
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.lbl_update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                appUpdateDialogListener.onUpdateClick(dialog);
            }
        });
        dialog = builder.create();
        dialog.setTitle(context.getResources().getString(R.string.dialog_update_available));
        dialog.show();
        return dialog;
    }
    public interface AppUpdateDialogListener{
        void onUpdateClick(DialogInterface dialog);
    }
}
