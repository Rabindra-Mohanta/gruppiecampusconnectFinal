package school.campusconnect.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import school.campusconnect.R;
import school.campusconnect.activities.LoginActivity2;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.database.RememberPref;
import school.campusconnect.datamodel.ChapterTBL;
import school.campusconnect.datamodel.ClassListTBL;
import school.campusconnect.datamodel.EBookClassItem;
import school.campusconnect.datamodel.EBookItem;
import school.campusconnect.datamodel.EventTBL;
import school.campusconnect.datamodel.GroupDataItem;
import school.campusconnect.datamodel.HwItem;
import school.campusconnect.datamodel.LiveClassListTBL;
import school.campusconnect.datamodel.PostDataItem;
import school.campusconnect.datamodel.PostTeamDataItem;
import school.campusconnect.datamodel.StudAssignementItem;
import school.campusconnect.datamodel.StudTestPaperItem;
import school.campusconnect.datamodel.SubjectCountTBL;
import school.campusconnect.datamodel.SubjectItem;
import school.campusconnect.datamodel.TeamCountTBL;
import school.campusconnect.datamodel.BaseTeamTable;
import school.campusconnect.datamodel.TestExamTBL;
import school.campusconnect.datamodel.banner.BannerTBL;
import school.campusconnect.datamodel.baseTeam.BaseTeamTableV2;
import school.campusconnect.datamodel.booths.BoothPresidentTBL;
import school.campusconnect.datamodel.booths.BoothsTBL;
import school.campusconnect.datamodel.booths.EventSubBoothTBL;
import school.campusconnect.datamodel.booths.MemberTeamTBL;
import school.campusconnect.datamodel.booths.MyBoothEventTBL;
import school.campusconnect.datamodel.booths.MyTeamSubBoothTBL;
import school.campusconnect.datamodel.booths.MyTeamVotersTBL;
import school.campusconnect.datamodel.booths.PublicFormBoothTBL;
import school.campusconnect.datamodel.booths.SubBoothWorkerEventTBL;
import school.campusconnect.datamodel.calendar.DayEventTBL;
import school.campusconnect.datamodel.calendar.MonthEventTBL;
import school.campusconnect.datamodel.committee.CommitteeTBL;
import school.campusconnect.datamodel.event.BoothPostEventTBL;
import school.campusconnect.datamodel.event.HomeTeamDataTBL;
import school.campusconnect.datamodel.feed.AdminFeedTable;
import school.campusconnect.datamodel.gallery.GalleryTable;
import school.campusconnect.datamodel.gruppiecontacts.GruppieContactGroupIdModel;
import school.campusconnect.datamodel.gruppiecontacts.GruppieContactsModel;
import school.campusconnect.datamodel.lead.LeadDataTBL;
import school.campusconnect.datamodel.masterList.MasterBoothListTBL;
import school.campusconnect.datamodel.masterList.StreetListTBL;
import school.campusconnect.datamodel.masterList.VoterListTBL;
import school.campusconnect.datamodel.masterList.WorkerListTBL;
import school.campusconnect.datamodel.notificationList.AllNotificationTable;
import school.campusconnect.datamodel.notificationList.NotificationTable;
import school.campusconnect.datamodel.personalchat.PersonalContactsModel;
import school.campusconnect.datamodel.profile.ProfileTBL;
import school.campusconnect.datamodel.ticket.TicketTBL;
import school.campusconnect.views.SMBDialogUtils;

public class BaseFragment extends Fragment {

    private ProgressDialog mProgressDialog;
    private View mProgressBar;

    public void showLoadingDialog(String msg) {
        if(getActivity()!=null)
        {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage(msg);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
            } else {
                if (!mProgressDialog.isShowing()) {
                    mProgressDialog.show();
                }
            }
        }

    }

    public void showProgressLoadingDialog(String msg, int maxCount) {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(msg);
        mProgressDialog.setMax(maxCount);
        mProgressDialog.setProgress(0);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.show();
    }

    public void setCountOnLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.setProgress(mProgressDialog.getProgress() + 1);
        }
    }

    /*public void showLoadingDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.please_wait));
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        } else {
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        }
    }*/

    public void showLoadingBar(View v) {
       AppLog.e("PBAR showLoadingBar", "called");
//        if (mProgressBar == null) {
        // mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar = v;
//        } else {
        mProgressBar.setVisibility(View.VISIBLE);
//        }
    }

    public void hideLoadingBar() {
       AppLog.e("PBAR hideLoadingBar", "called");
        if (mProgressBar == null) {
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mProgressBar = null;
        }
    }

    public void hideLoadingDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void hide_keyboard() {
        if(getActivity()!=null)
        {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = getActivity().getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(getActivity());
            }
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    public boolean isConnectionAvailable() {
        ConnectivityManager connectivityManager;
        NetworkInfo networkinfo;
        if(getActivity() !=null)
        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        else
        return false;

        networkinfo = connectivityManager.getActiveNetworkInfo();

        return (networkinfo != null && networkinfo.isConnectedOrConnecting());

    }

    public void showNoNetworkMsg() {
        /*SMBDialogUtils.showSMBDialogOK(getActivity(), getString(R.string.no_internet), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });*/
        try {
            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.toolbar), R.string.no_internet, Snackbar.LENGTH_SHORT)
                    .setAction(getResources().getString(R.string.action_settings), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
                    }).setActionTextColor(Color.WHITE);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.snackbar_textsize));
            textView.setTextColor(Color.WHITE);
            snackbar.show();

        } catch (Exception e) {
           AppLog.e("SnackBar", "error is " + e.toString());
            SMBDialogUtils.showSMBDialogOK(getActivity(), getString(R.string.no_internet), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    dialog.dismiss();
                }
            });
        }
    }

    public boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    protected boolean isValueValid(EditText editView) {
        boolean isValid = true;

        String enteredValue = editView.getEditableText().toString().trim();
        if (enteredValue.matches("")) {
            editView.setError(getString(R.string.msg_required));
            editView.requestFocus();
            isValid = false;
        }


        return isValid;
    }

    public void logout() {
        if(getActivity()!=null)
        {
           AppLog.e("Logout", "onSuccessCalled");
            LeafPreference.getInstance(getActivity()).clearData();
            RememberPref.getInstance(getActivity()).clearData();
           AppLog.e("GroupList", "Grouplist token : " + LeafPreference.getInstance(getActivity()).getString(LeafPreference.GCM_TOKEN));
            GroupDataItem.deleteAll();
            PostDataItem.deleteAllPosts();
//            NotificationModel.deleteAll();
            BaseTeamTable.deleteAll();
            BaseTeamTableV2.deleteAll();
            MasterBoothListTBL.deleteAll();
            WorkerListTBL.deleteAll();
            StreetListTBL.deleteAll();
            BoothsTBL.deleteAll();
            MemberTeamTBL.deleteAll();
            ProfileTBL.deleteAll();
            PublicFormBoothTBL.deleteAll();
            TicketTBL.deleteAll();
            DayEventTBL.deleteAllEvent();
            MonthEventTBL.deleteAllEvent();
            NotificationTable.deleteAll();
            AllNotificationTable.deleteAll();
            AdminFeedTable.deleteAll();
            EventSubBoothTBL.deleteAll();
            VoterListTBL.deleteAll();
            BannerTBL.deleteAll();
            PostTeamDataItem.deleteAllPosts();
            PersonalContactsModel.deleteAll();
            GruppieContactsModel.deleteAll();
            CommitteeTBL.deleteMember();
            LeadDataTBL.deleteAll();
//        GruppieContactAddressModel.deleteAll();
            BoothPostEventTBL.deleteAll();
            GruppieContactGroupIdModel.deleteAll();
            GalleryTable.deleteGallery();
            getActivity().getSharedPreferences("pref_noti_count", Context.MODE_PRIVATE).edit().clear().commit();
            new DatabaseHandler(getActivity()).deleteAll();

            HwItem.deleteAll();
            TestExamTBL.deleteAll();
            SubBoothWorkerEventTBL.deleteAll();
            MyBoothEventTBL.deleteAll();
            ChapterTBL.deleteAll();
            EventTBL.deleteAll();
            ClassListTBL.deleteAll();
            LiveClassListTBL.deleteAll();
            TeamCountTBL.deleteAll();
            SubjectCountTBL.deleteAll();
            HomeTeamDataTBL.deleteAll();
            StudAssignementItem.deleteAll();
            StudTestPaperItem.deleteAll();
            SubjectItem.deleteAll();
            EBookItem.deleteAll();
            EBookClassItem.deleteAll();
            MyTeamVotersTBL.deleteAll();
            BoothPresidentTBL.deleteAll();
            MyTeamSubBoothTBL.deleteAll();

            updateViews("en");
           /* Intent intent = new Intent(getActivity(), LoginActivity2.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);*/
        }

    }
    private void updateViews(String languageCode) {

        SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Locale.Helper.Selected.Language", languageCode);
        Log.e("LocaleHelper","language "+languageCode);
        editor.apply();

        Intent intent = new Intent(getActivity(), LoginActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
