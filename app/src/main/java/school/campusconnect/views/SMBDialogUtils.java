package school.campusconnect.views;

import android.app.Activity;
import android.content.DialogInterface;
import school.campusconnect.utils.AppLog;

import school.campusconnect.R;


public class SMBDialogUtils {

    public static void showSMBSingleChoiceDialog(Activity activity, int titleId, int itemsId, int selectedId, DialogInterface.OnClickListener listener) {
        final SMBAlterDialog dialog =
                new SMBAlterDialog(activity);
        dialog.setTitle(titleId);
        dialog.setSingleChoiceItems(itemsId, selectedId, null);
        dialog.setPositiveButtonClickListener(listener);
        dialog.setNegativeButtonWithListener();
        dialog.show();
       AppLog.e("sdds", "sow diss");
    }
    public static void showSMBSingleChoiceDialog(Activity activity, int titleId, String itemsId[], int selectedId, DialogInterface.OnClickListener listener) {
        final SMBAlterDialog dialog =
                new SMBAlterDialog(activity);
        dialog.setTitle(titleId);
        dialog.setSingleChoiceItems(itemsId, selectedId, null);
        dialog.setPositiveButtonClickListener(listener);
        dialog.setNegativeButtonWithListener();
        dialog.show();
       AppLog.e("sdds", "sow diss");
    }

    public static void showSMBDialog(Activity activity, int msgId) {
        final SMBAlterDialog dialog = new SMBAlterDialog(activity);
        dialog.setTitle(R.string.app_name);
        dialog.setMessage(msgId);
        dialog.setPositiveButton(activity.getString(android.R.string.ok
        ), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public static void showSMBDialog(Activity activity, String msg) {
        final SMBAlterDialog dialog =
                new SMBAlterDialog(activity);
        dialog.setTitle(R.string.app_name);

        dialog.setMessage(msg);
        dialog.setPositiveButton(activity.getString(android.R
                .string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showSMBDialogOK(Activity activity, String msg, DialogInterface.OnClickListener okListener) {
        final SMBAlterDialog dialog =
                new SMBAlterDialog(activity);
        dialog.setTitle(R.string.app_name);
        dialog.setMessage(msg);
        dialog.setPositiveButton(activity.getString(android.R.string.ok), okListener);
        dialog.show();
    }

    public static void showSMBDialogOKCancel(Activity activity, String msg, DialogInterface.OnClickListener okListener)
    {
        final SMBAlterDialog dialog = new SMBAlterDialog(activity);
        dialog.setTitle(R.string.app_name);
        dialog.setNegativeButtonWithListener();
        dialog.setMessage(msg);
        dialog.setPositiveButton(activity.getString(android.R.string.ok), okListener);
        dialog.show();
    }public static void showSMBDialogConfirmCancel(Activity activity, String msg, DialogInterface.OnClickListener okListener)
    {
        final SMBAlterDialog dialog = new SMBAlterDialog(activity);
        dialog.setTitle(R.string.app_name);
        dialog.setNegativeButtonWithListener();
        dialog.setMessage(msg);
        dialog.setPositiveButton(activity.getString(android.R.string.yes), okListener);
        dialog.show();
    }
    public static androidx.appcompat.app.AlertDialog showSMBDialogOKCancel_(Activity activity, String msg, DialogInterface.OnClickListener okListener)
    {
        final SMBAlterDialog dialog = new SMBAlterDialog(activity);
        dialog.setTitle(R.string.app_name);
        dialog.setNegativeButtonWithListener();
        dialog.setMessage(msg);
        dialog.setPositiveButton(activity.getString(android.R.string.ok), okListener);
        return dialog.show();
    }


//    public static void showSMBDialogOK(Activity activity, String msg, DialogInterface.OnClickListener okListener, DialogInterface.OnCancelListener cancelListener) {
//        final SMBAlterDialog dialog =
//                new SMBAlterDialog(activity);
//        dialog.setTitle(R.string.app_title);
//        dialog.setMessage(msg);
//        dialog.setPositiveButton(activity.getString(R.string.lbl_ok), okListener);
//        dialog.setOnCancelListener(cancelListener);
//        dialog.show();
//    }


}
