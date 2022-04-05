package school.campusconnect.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import school.campusconnect.datamodel.ChapterTBL;
import school.campusconnect.datamodel.ClassListTBL;
import school.campusconnect.datamodel.EBookClassItem;
import school.campusconnect.datamodel.EBookItem;
import school.campusconnect.datamodel.EventTBL;
import school.campusconnect.datamodel.HwItem;
import school.campusconnect.datamodel.LiveClassListTBL;
import school.campusconnect.datamodel.StudAssignementItem;
import school.campusconnect.datamodel.StudTestPaperItem;
import school.campusconnect.datamodel.SubjectCountTBL;
import school.campusconnect.datamodel.SubjectItem;
import school.campusconnect.datamodel.TeamCountTBL;
import school.campusconnect.datamodel.TestExamTBL;
import school.campusconnect.datamodel.banner.BannerTBL;
import school.campusconnect.datamodel.baseTeam.BaseTeamTableV2;
import school.campusconnect.datamodel.booths.BoothPresidentTBL;
import school.campusconnect.datamodel.booths.BoothsTBL;
import school.campusconnect.datamodel.booths.EventSubBoothTBL;
import school.campusconnect.datamodel.booths.MemberTeamTBL;
import school.campusconnect.datamodel.booths.MyTeamSubBoothTBL;
import school.campusconnect.datamodel.booths.MyTeamVotersTBL;
import school.campusconnect.datamodel.booths.PublicFormBoothTBL;
import school.campusconnect.datamodel.event.BoothPostEventTBL;
import school.campusconnect.datamodel.event.HomeTeamDataTBL;
import school.campusconnect.datamodel.feed.AdminFeedTable;
import school.campusconnect.datamodel.gallery.GalleryTable;
import school.campusconnect.datamodel.masterList.MasterBoothListTBL;
import school.campusconnect.datamodel.masterList.StreetListTBL;
import school.campusconnect.datamodel.masterList.VoterListTBL;
import school.campusconnect.datamodel.masterList.WorkerListTBL;
import school.campusconnect.datamodel.notificationList.AllNotificationTable;
import school.campusconnect.datamodel.notificationList.NotificationTable;
import school.campusconnect.datamodel.profile.ProfileTBL;
import school.campusconnect.datamodel.ticket.TicketTBL;
import school.campusconnect.fragments.DashboardNewUi.BaseTeamFragmentv2;
import school.campusconnect.fragments.DashboardNewUi.BaseTeamFragmentv3;
import school.campusconnect.datamodel.ticket.TicketTBL;
import school.campusconnect.fragments.BaseTeamFragment;
import school.campusconnect.utils.AppLog;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import java.util.ArrayList;
import java.util.List;

import school.campusconnect.R;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.database.RememberPref;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ContactListItem;
import school.campusconnect.datamodel.ContactListResponse;
import school.campusconnect.datamodel.GroupDataItem;
import school.campusconnect.datamodel.PhoneContactsItems;
import school.campusconnect.datamodel.PostDataItem;
import school.campusconnect.datamodel.PostTeamDataItem;
import school.campusconnect.datamodel.BaseTeamTable;
import school.campusconnect.datamodel.gruppiecontacts.GruppieContactGroupIdModel;
import school.campusconnect.datamodel.gruppiecontacts.GruppieContactsModel;
import school.campusconnect.datamodel.notifications.NotificationModel;
import school.campusconnect.datamodel.personalchat.PersonalContactsModel;
import school.campusconnect.fragments.Fragment_GruppieContacts;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.Constants;
import school.campusconnect.views.SMBDialogUtils;

/**
 * Created by techjini on 15/12/15.
 */
public abstract class BaseActivity extends AppCompatActivity implements LeafManager.OnCommunicationListener {

    private Dialog mProgressDialog;
    private ProgressDialog mProgressBar;
    private int hot_number = 0;
    private TextView tv_noti;// = null;
    private TextView tv_select;// = null;
    private Menu menu;
    public final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 9;

    public synchronized void showLoadingDialog() {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = new Dialog(this,R.style.MyAlertDialogStyle);
                mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mProgressDialog.setContentView(R.layout.dialog_layout);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
            } else {
                if (!mProgressDialog.isShowing()) {
                    mProgressDialog.show();
                }
            }
        }catch (Exception e)
        {
           AppLog.e("BaseActivity",e.toString());
        }

    }

    public void showKeyboard(View view)
    {
        InputMethodManager inputMethodManager =
                (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                view.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }

    public synchronized void showLoadingDialogText() {
        try {
            if (mProgressBar == null) {
                mProgressBar = new ProgressDialog(this);
                mProgressBar.setMessage("Initializing...");
                mProgressBar.setCancelable(false);
                mProgressBar.show();
            } else {
                if (!mProgressBar.isShowing()) {
                    mProgressBar.show();
                }
            }
        }catch (Exception e)
        {
           AppLog.e("BaseActivity",e.toString());
        }

    }
    public synchronized void hideLoadingDialogText() {
        if (mProgressBar != null && mProgressBar.isShowing()) {
            mProgressBar.hide();
            mProgressBar.cancel();
            mProgressBar = null;
        }
    }

    public synchronized void hideLoadingDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }


    public void setTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }

    public void setHomeAsUpIndicator(int resId) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(resId);
        }
    }


    public void setBackEnabled(boolean flag) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(flag);
            getSupportActionBar().setHomeButtonEnabled(flag);
        }
    }

    public boolean isValueValid(EditText editView) {
        boolean isValid = true;

        String enteredValue = editView.getEditableText().toString().trim();
        if (enteredValue.matches("")) {
            editView.setError(getString(R.string.msg_required));
            editView.requestFocus();
            isValid = false;
        }
        return isValid;
    }


    public boolean isValueValidOnly(EditText editView) {
        boolean isValid = true;

        String enteredValue = editView.getEditableText().toString().trim();
        if (enteredValue.matches("")) {
            isValid = false;
        }
        return isValid;
    }

    public boolean isValueValidOnlyString(String s) {
        boolean isValid = true;

//        String enteredValue = editView.getEditableText().toString().trim();
        if (s.matches("")) {
            isValid = false;
        }
        return isValid;
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(mMessageReceiver, new IntentFilter("notification"));
        SharedPreferences preferences = getSharedPreferences("pref_noti_count", MODE_PRIVATE);
        hot_number = preferences.getInt("noti_count", 0);
        updateHotCount(hot_number);


    }

    public void hide_keyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public void hide_keyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (view == null) {
            view = new View(this);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            hide_keyboard();

            if (this instanceof GroupDashboardActivityNew) {
                onBackPressed();
            } else {
                finish();
            }
            return true;


        }else if (item.getItemId() == R.id.action_notification_list) {
            if(this instanceof GroupDashboardActivityNew){
                ((GroupDashboardActivityNew)this).llNotification.performClick();
            }
        } else if (item.getItemId() == R.id.action_sync) {
            getContactsWithPermission();
        } else if (item.getItemId() == R.id.action_add_multi) {
           AppLog.e("ADD_MULTI", "InviteFriendActivity1, add clicked");
            /*if(BaseActivity.this instanceof InviteFriendActivity)
            {
                ((InviteFriendActivity) BaseActivity.this).addPost();
               AppLog.e("ADD_MULTI", "InviteFriendActivity2, add clicked");
            }*/
            try {
                RelativeLayout relNoti = (RelativeLayout) menu.findItem(R.id.action_add_multi).getActionView();
                relNoti.setVisibility(View.VISIBLE);
                tv_select = (TextView) relNoti.findViewById(R.id.actionbar_notifcation_textview);
                updateTickCount(0);
                new MyMenuItemStuffListener(relNoti, "Notifications") {
                    @Override
                    public void onClick(View v) {
                        if (BaseActivity.this instanceof AddFriendActivity) {
                          /* AppLog.e("ADD_MULTI", "addFrnds, add clicked");
                                ((AddFriendActivity) BaseActivity.this).phoneContactFragment.addMultipleInvites();*/
                        } else if (BaseActivity.this instanceof AddPostActivity) {
                            ((AddPostActivity) BaseActivity.this).addPost();
                           AppLog.e("ADD_MULTI", "AddPostActivity, add clicked");

                        }
                        else if (BaseActivity.this instanceof AddQuestionActivity) {
                            ((AddQuestionActivity) BaseActivity.this).addQuestion();
                           AppLog.e("ADD_MULTI", "AddQuestionActivity, add clicked");
                        } else if (BaseActivity.this instanceof PushActivity) {
                            ((PushActivity) BaseActivity.this).isValid();
                           AppLog.e("ADD_MULTI", "PushActivity, add clicked");
                        } else if (BaseActivity.this instanceof SharePersonalNameListActivity) {
                            ((SharePersonalNameListActivity) BaseActivity.this).onClickAddComment(null);
                           AppLog.e("ADD_MULTI", "SharePersonalNameListActivity, add clicked");
                        } else if (BaseActivity.this instanceof ShareGroupListActivity) {
                            ((ShareGroupListActivity) BaseActivity.this).onClickAddComment(null);
                           AppLog.e("ADD_MULTI", "ShareGroupListActivity, add clicked");
                        } else if (BaseActivity.this instanceof EditPostActivity) {
                            ((EditPostActivity) BaseActivity.this).addPost();
                           AppLog.e("ADD_MULTI", "EditPostActivity, add clicked");
                        } else if(BaseActivity.this instanceof InviteFriendActivity)
                        {
                            ((InviteFriendActivity) BaseActivity.this).addPost();
                           AppLog.e("ADD_MULTI", "InviteFriendActivity2, add clicked");
                        }
                    }
                };
            } catch (NullPointerException e) {
               AppLog.e("THIS", "error is " + e.toString());
            }

            /*if (this instanceof AddFriendActivity) {
               AppLog.e("ADD_MULTI", "addFrnds, add clicked");
                if (((AddFriendActivity) this).tabAdapter != null) {
                    ((AddFriendActivity) this).tabAdapter.addMultipleFriends(((AddFriendActivity) this).tabPosition);
                }
            } else if (this instanceof AddPostActivity) {
                ((AddPostActivity) this).addPost();
               AppLog.e("ADD_MULTI", "AddPostActivity, add clicked");
            } else if (this instanceof AddQuestionActivity) {
                ((AddQuestionActivity) this).addQuestion();
               AppLog.e("ADD_MULTI", "AddQuestionActivity, add clicked");
            } else if (this instanceof PushActivity) {
                ((PushActivity) this).addPost();
               AppLog.e("ADD_MULTI", "PushActivity, add clicked");
            } else if (this instanceof SharePersonalNameListActivity) {
                ((SharePersonalNameListActivity) this).onClickAddComment(null);
               AppLog.e("ADD_MULTI", "SharePersonalNameListActivity, add clicked");
            } else if (this instanceof ShareGroupListActivity) {
                ((ShareGroupListActivity) this).onClickAddComment(null);
               AppLog.e("ADD_MULTI", "ShareGroupListActivity, add clicked");
            } else if (this instanceof EditPostActivity) {
                ((EditPostActivity) this).addPost();
               AppLog.e("ADD_MULTI", "EditPostActivity, add clicked");
            }*/
        } else if (item.getItemId() == R.id.action_invite) {
            if (this instanceof AddFriendActivity) {
                ((AddFriendActivity) BaseActivity.this).callInvitePage();
            }
        } else if (item.getItemId() == R.id.action_sync2) {
            if (this instanceof AddFriendActivity) {
                getContactsWithPermission();
            }
        }
        else if(item.getItemId()==R.id.menu_add_friend)
        {
            if(this instanceof LeadsListActivity)
                ((LeadsListActivity)BaseActivity.this).onAddFrendSelected();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.menu = menu;

            getMenuInflater().inflate(R.menu.menu_save_v2, menu);

            menu.findItem(R.id.menu_add_friend).setVisible(false);
            menu.findItem(R.id.menu_add_post).setVisible(false);
            menu.findItem(R.id.menu_leave_team).setVisible(false);
            final Context context = this;

            RelativeLayout badgeLayout = (RelativeLayout) menu.findItem(R.id.action_cart).getActionView();
            TextView tv = (TextView) badgeLayout.findViewById(R.id.actionbar_notifcation_textview);
            tv.setText("2");
            badgeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BaseActivity.this, FavoritePostActivity.class);
                    startActivity(intent);
                }
            });

           AppLog.e("THIS", "this is " + this.getClass().getName());
            try {
                RelativeLayout relNoti = (RelativeLayout) menu.findItem(R.id.action_notification).getActionView();
                relNoti.setVisibility(View.VISIBLE);
                tv_noti = (TextView) relNoti.findViewById(R.id.actionbar_notifcation_textview);
                hot_number = this.getSharedPreferences("pref_noti_count", MODE_PRIVATE).getInt("noti_count", 0);
                updateHotCount(hot_number);
                new MyMenuItemStuffListener(relNoti, "Notifications") {
                    @Override
                    public void onClick(View v) {

                    }
                };
            } catch (NullPointerException e) {
               AppLog.e("THIS", "error is " + e.toString());
            }



            if (this instanceof GroupDashboardActivityNew) {
                menu.findItem(R.id.action_add_multi).setVisible(false);
//                menu.findItem(R.id.action_add_count).setVisible(false);
//                menu.findItem(R.id.action_notification).setVisible(false);


                Fragment fm = this.getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                if(fm instanceof BaseTeamFragmentv2){
                    menu.findItem(R.id.action_notification_list).setVisible(true);
                }
                if(fm instanceof BaseTeamFragmentv3){
                    menu.findItem(R.id.action_notification_list).setVisible(true);
                }else{
                    menu.findItem(R.id.action_notification_list).setVisible(false);
                }

                menu.findItem(R.id.action_notification_list).setVisible(true);
            } else if (this instanceof GroupListActivityNew) {
                menu.findItem(R.id.action_add_multi).setVisible(false);
//                menu.findItem(R.id.action_add_count).setVisible(false);
                menu.findItem(R.id.action_cart).setVisible(false);
                menu.findItem(R.id.action_notification).setVisible(true);
                menu.findItem(R.id.action_notification_list).setVisible(false);
            } else if (this instanceof AddFriendActivity) {
//                menu.findItem(R.id.action_add_multi).setVisible(true);
                menu.findItem(R.id.action_invite).setVisible(true);

                if(!GroupDashboardActivityNew.groupCategory.equals(Constants.CATEGORY_SCHOOL) && !GroupDashboardActivityNew.groupCategory.equals(Constants.CATEGORY_CONSTITUENCY))
                menu.findItem(R.id.action_sync).setVisible(true);

//                menu.findItem(R.id.action_add_count).setVisible(true);
                menu.findItem(R.id.action_notification).setVisible(false);

                menu.findItem(R.id.action_notification_list).setVisible(false);
            }else if(this instanceof AddPostActivity)
            {
                menu.findItem(R.id.action_add_multi).setVisible(false);

                menu.findItem(R.id.action_notification_list).setVisible(false);
            }
            else if(this instanceof LeadsListActivity)
            {
                menu.findItem(R.id.menu_add_friend).setVisible(false);

                menu.findItem(R.id.action_notification_list).setVisible(false);
            }
            else if(this instanceof InviteFriendActivity)
            {
                menu.findItem(R.id.action_add_multi).setVisible(false);
                menu.findItem(R.id.action_notification).setVisible(false);

                menu.findItem(R.id.action_notification_list).setVisible(false);
            }

            else if (this instanceof AllContactListActivity) {
                menu.findItem(R.id.action_sync).setVisible(true);

                menu.findItem(R.id.action_notification_list).setVisible(false);
            }else if ( this instanceof SharePersonalNameListActivity) {
                menu.findItem(R.id.action_add_multi).setVisible(true);

                menu.findItem(R.id.action_notification_list).setVisible(false);
            } /*else if (this instanceof AddQuestionActivity) {
                menu.findItem(R.id.action_add_multi).setVisible(true);
            }*/  else if (this instanceof PushActivity) {
                menu.findItem(R.id.action_add_multi).setVisible(true);

                menu.findItem(R.id.action_notification_list).setVisible(false);
            }
            else if(this instanceof ShareGroupListActivity)
            {
                menu.findItem(R.id.action_add_multi).setVisible(true);

                menu.findItem(R.id.action_notification_list).setVisible(false);
            }
            else if(this instanceof EditPostActivity)
            {
                menu.findItem(R.id.action_add_multi).setVisible(false);

                menu.findItem(R.id.action_notification_list).setVisible(false);
            }

            else {
                menu.findItem(R.id.action_add_multi).setVisible(false);
//                menu.findItem(R.id.action_add_count).setVisible(false);
                menu.findItem(R.id.action_notification).setVisible(false);

                menu.findItem(R.id.action_notification_list).setVisible(false);
            }


        setAddCount(0);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getSupportFragmentManager().getFragments() != null) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }


    public void setNotifications(Toolbar toolbar) {
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.layout_badge, null);
        toolbar.addView(mCustomView);

    }

    //  public abstract void onNotificationnClick();

    public boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean isConnectionAvailable() {
        ConnectivityManager connectivityManager;
        NetworkInfo networkinfo;
        connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkinfo = connectivityManager.getActiveNetworkInfo();
        return (networkinfo != null && networkinfo.isConnectedOrConnecting());

    }

    public void showNoNetworkMsg() {
        /*SMBDialogUtils.showSMBDialogOK(this, getString(R.string.no_internet), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //onBackPressed();
            }
        });*/

        try {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.toolbar), R.string.no_internet, Snackbar.LENGTH_LONG)
                    .setAction("SETTINGS", new View.OnClickListener() {
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
            SMBDialogUtils.showSMBDialogOK(this, getString(R.string.no_internet), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    dialog.dismiss();
                    //onBackPressed();
                }
            });
        }
    }

    public void logout() {
       AppLog.e("Logout", "onSuccessCalled");
        LeafPreference.getInstance(this).clearData();
        RememberPref.getInstance(this).clearData();
        this.getSharedPreferences("pref_noti_count", MODE_PRIVATE).edit().clear().commit();
        AppLog.e("GroupList", "Grouplist token : " + LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.GCM_TOKEN));

        GroupDataItem.deleteAll();
        PostDataItem.deleteAllPosts();
//        NotificationModel.deleteAll();
        BaseTeamTable.deleteAll();
        BaseTeamTableV2.deleteAll();
        TicketTBL.deleteAll();
        MasterBoothListTBL.deleteAll();
        BoothsTBL.deleteAll();
        PublicFormBoothTBL.deleteAll();
        GalleryTable.deleteGallery();
        MemberTeamTBL.deleteAll();
        WorkerListTBL.deleteAll();
        VoterListTBL.deleteAll();
        StreetListTBL.deleteAll();
        NotificationTable.deleteAll();
        AllNotificationTable.deleteAll();
        AdminFeedTable.deleteAll();
        ProfileTBL.deleteAll();
        PostTeamDataItem.deleteAllPosts();
        PersonalContactsModel.deleteAll();
        GruppieContactsModel.deleteAll();
        HwItem.deleteAll();
        TestExamTBL.deleteAll();
        ChapterTBL.deleteAll();
        HomeTeamDataTBL.deleteAll();
        EventTBL.deleteAll();
        ClassListTBL.deleteAll();
        LiveClassListTBL.deleteAll();
        TeamCountTBL.deleteAll();
        SubjectCountTBL.deleteAll();
        StudAssignementItem.deleteAll();
        StudTestPaperItem.deleteAll();
        SubjectItem.deleteAll();
        EBookItem.deleteAll();
        EBookClassItem.deleteAll();
        BoothPostEventTBL.deleteAll();
        BannerTBL.deleteAll();
        EventSubBoothTBL.deleteAll();
        MyTeamVotersTBL.deleteAll();
        MyTeamSubBoothTBL.deleteAll();
        BoothPresidentTBL.deleteAll();

//        GruppieContactAddressModel.deleteAll();
        GruppieContactGroupIdModel.deleteAll();
        new DatabaseHandler(this).deleteAll();

        Intent intent = new Intent(this, LoginActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void logoutWithoutEvents() {
        AppLog.e("Logout", "onSuccessCalled");
        LeafPreference.getInstance(this).clearData();
        RememberPref.getInstance(this).clearData();
        AppLog.e("GroupList", "Grouplist token : " + LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.GCM_TOKEN));
        GroupDataItem.deleteAll();
        PostDataItem.deleteAllPosts();
//        NotificationModel.deleteAll();
        BaseTeamTable.deleteAll();
        BannerTBL.deleteAll();
        BaseTeamTableV2.deleteAll();
        MasterBoothListTBL.deleteAll();
        WorkerListTBL.deleteAll();
        HomeTeamDataTBL.deleteAll();
        StreetListTBL.deleteAll();
        TicketTBL.deleteAll();
        BoothPostEventTBL.deleteAll();
        EventSubBoothTBL.deleteAll();
        NotificationTable.deleteAll();
        VoterListTBL.deleteAll();
        ProfileTBL.deleteAll();
        MemberTeamTBL.deleteAll();
        AllNotificationTable.deleteAll();
        AdminFeedTable.deleteAll();
        BoothsTBL.deleteAll();
        PublicFormBoothTBL.deleteAll();
        PostTeamDataItem.deleteAllPosts();
        PersonalContactsModel.deleteAll();
        GruppieContactsModel.deleteAll();
        MyTeamVotersTBL.deleteAll();
        GalleryTable.deleteGallery();
        BoothPresidentTBL.deleteAll();

//        GruppieContactAddressModel.deleteAll();
        GruppieContactGroupIdModel.deleteAll();
        this.getSharedPreferences("pref_noti_count", MODE_PRIVATE).edit().clear().commit();
        new DatabaseHandler(this).deleteAll();

        HwItem.deleteAll();
        TestExamTBL.deleteAll();
        ChapterTBL.deleteAll();
        //EventTBL.deleteAll();
        ClassListTBL.deleteAll();
        LiveClassListTBL.deleteAll();
        TeamCountTBL.deleteAll();
        SubjectCountTBL.deleteAll();
        StudAssignementItem.deleteAll();
        StudTestPaperItem.deleteAll();
        SubjectItem.deleteAll();
        EBookItem.deleteAll();
        EBookClassItem.deleteAll();
        MyTeamSubBoothTBL.deleteAll();

        Intent intent = new Intent(this, LoginActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // call the updating code on the main thread,
// so we can call this asynchronously
    public void updateHotCount(final int new_hot_number) {
        hot_number = new_hot_number;
        if (tv_noti == null) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (new_hot_number == 0)
                    tv_noti.setVisibility(View.INVISIBLE);
                else {
                    tv_noti.setVisibility(View.VISIBLE);
                    tv_noti.setText(Integer.toString(new_hot_number));
                }
            }
        });
    }

    public void updateTickCount(final int new_hot_number) {
        hot_number = new_hot_number;
        if (tv_select == null) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (new_hot_number == 0)
                    tv_select.setVisibility(View.INVISIBLE);
                else {
                    tv_select.setVisibility(View.VISIBLE);
                    tv_select.setText("" + Integer.toString(new_hot_number));
                }
            }
        });
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        hideLoadingDialog();
//        new TaskForGruppieContacts(response).execute();

       AppLog.e("TaskForGruppieContacts", "onSuccess called");

        /*GruppieContactsModel.deleteAll();
        GruppieContactGroupIdModel.deleteAll();

        ContactListResponse res = (ContactListResponse) response;
        List<ContactListItem> list1 = res.getResults();
        DatabaseHandler databaseHandler = new DatabaseHandler(this);

            *//*for (int i = 0; i < list1.size(); i++) {
                ContactListItem item = list1.get(i);

                if (databaseHandler.getCount() != 0) {
                    try {
                       AppLog.e("CONTACTSS", "count is " + databaseHandler.getCount());
                        String name = databaseHandler.getNameFromNum(item.getPhone().replaceAll(" ", ""));
                       AppLog.e("CONTACTSS", "api name is " + item.getName());
                       AppLog.e("CONTACTSS", "name is " + name);
                       AppLog.e("CONTACTSS", "num is " + item.getPhone());
                        if (!name.equals("")) {
                            item.setName(name);
                        } else {
                            item.setName(item.getName());
                        }
                    } catch (NullPointerException e) {
                        item.setName(item.getName());
                    }
                } else {
                   AppLog.e("CONTACTSS", "count is 0");
                    item.setName(item.getName());
                }
            }*//*
//            sdg;
//            new TaskForGruppieContactsFirstTime(res.getResults()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        int count = databaseHandler.getCount();
        for (int i = 0; i < res.getResults().size(); i++) {
            ContactListItem item = res.getResults().get(i);

            if (count != 0) {
                try {
//                       AppLog.e("CONTACTSS", "count is " + databaseHandler.getCount());
                    String name = databaseHandler.getNameFromNumFirstTime(item.getPhone().replaceAll(" ", ""));
//                       AppLog.e("CONTACTSS", "api name is " + item.getName());
//                       AppLog.e("CONTACTSS", "name is " + name);
//                       AppLog.e("CONTACTSS", "num is " + item.getPhone());
                    if (!name.equals("")) {
                        item.setName(name);
                    } else {
                        item.setName(item.getName());
                    }
                } catch (NullPointerException e) {
                    item.setName(item.getName());
                }
            } else {
               AppLog.e("CONTACTSS", "count is 0");
                item.setName(item.getName());
            }
            GruppieContactsModel model = new GruppieContactsModel();
            model.contact_id = item.getId();
//                model.contact_name = item.getName();
            model.contact_name = item.getName();
            model.contact_phone = item.getPhone();
            model.contact_email = item.getEmail();
            model.contact_leadCount = item.getLeadCount();
            model.contact_dob = item.getDob();
            model.contact_qualification = item.getQualification();
            model.contact_occupation = item.getOccupation();
            model.contact_image = item.getImage();
            model.contact_otherLeads = Arrays.toString(item.getOtherLeads());
            model.contact_groups = Arrays.toString(item.getGroups());
            model.contact_group_ids = Arrays.toString(item.getGroupIds());
            model.group_ids_of_admin = Arrays.toString(Constants.getAdminArray(item.getGroupIdsOfAdmin()));
            model.usingGruppie = item.usingGruppie;
            model.contact_gender = item.gender;
            model.contact_id = item.getId();
            model.line1 = item.getAddress().line1;
            model.line2 = item.getAddress().line2;
            model.district = item.getAddress().district;
            model.state = item.getAddress().state;
            model.countryCode = item.getAddress().countryCode;
            model.pin = item.getAddress().pin;
            model.save();

           AppLog.e("GId", "Array is " + Arrays.toString(item.getGroupIds()));

            for (int j = 0; j < item.getGroupIds().length; j++) {
                GruppieContactGroupIdModel idModel = new GruppieContactGroupIdModel();
                idModel.id = item.getGroupIds()[j];
                idModel.user_id = item.getId();
               AppLog.e("GID", "gid " + idModel.id + " uid " + idModel.user_id);
                idModel.save();
            }

        }

        String num = LeafPreference.getInstance(this).getString(LeafPreference.NUM);
        databaseHandler.updateYourNum(num);
        hideLoadingDialog();*/
    }

    @Override
    public void onFailure(int apiId, String msg) {
        AppLog.e("Base","onFailure : "+msg);
        hideLoadingDialog();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingDialog();
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }



    // used for adding count on top of menu item like notifications
    static abstract class MyMenuItemStuffListener implements View.OnClickListener, View.OnLongClickListener {
        private String hint;
        private View view;

        MyMenuItemStuffListener(View view, String hint) {
            this.view = view;
            this.hint = hint;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        abstract public void onClick(View v);

        @Override
        public boolean onLongClick(View v) {
            final int[] screenPos = new int[2];
            final Rect displayFrame = new Rect();
            view.getLocationOnScreen(screenPos);
            view.getWindowVisibleDisplayFrame(displayFrame);
            final Context context = view.getContext();
            final int width = view.getWidth();
            final int height = view.getHeight();
            final int midy = screenPos[1] + height / 2;
            final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            Toast cheatSheet = Toast.makeText(context, hint, Toast.LENGTH_SHORT);
            if (midy < displayFrame.height()) {
                cheatSheet.setGravity(Gravity.TOP | Gravity.RIGHT,
                        screenWidth - screenPos[0] - width / 2, height);
            } else {
                cheatSheet.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, height);
            }
            cheatSheet.show();
            return true;
        }
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
//            String message = intent.getStringExtra("message");

            //do other stuff here
            SharedPreferences preferences = context.getSharedPreferences("pref_noti_count", MODE_PRIVATE);
            hot_number = preferences.getInt("noti_count", 0);
            updateHotCount(hot_number);
        }
    };


    static public void updateMyActivity(Context context) {

        Intent intent = new Intent("notification");

        //put whatever data you want to send, if any
//        intent.putExtra("message", message);

        //send broadcast
        context.sendBroadcast(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            this.unregisterReceiver(mMessageReceiver);
        } catch (Exception ex) {
           AppLog.e("BroadcastReceiver error", "error--> " + ex.toString());
        }
    }

    @Override
    public void onBackPressed() {
        /*if (this instanceof TeamListPersonalActivity) {
            if (TeamPostFragment_New.isTeamPosts) {
                TeamPostFragment_New.getInstance().showContactList();
            } else {
                super.onBackPressed();
            }
        } else {*/
        try {
            super.onBackPressed();
        }
        catch (Exception e)
        {

        }

//        }
    }

    public void setAddCount(int size) {

        try {
            RelativeLayout relNoti = (RelativeLayout) menu.findItem(R.id.action_add_multi).getActionView();
            relNoti.setVisibility(View.VISIBLE);
            tv_select = (TextView) relNoti.findViewById(R.id.actionbar_notifcation_textview);
            updateTickCount(size);
            new MyMenuItemStuffListener(relNoti, "Select") {
                @Override
                public void onClick(View v) {
                    if (BaseActivity.this instanceof AddFriendActivity) {
                      /* AppLog.e("ADD_MULTI", "addFrnds, add clicked");
                       ((AddFriendActivity) BaseActivity.this).phoneContactFragment.addMultipleInvites();*/
                    } else if (BaseActivity.this instanceof AddPostActivity) {
                        ((AddPostActivity) BaseActivity.this).addPost();
                       AppLog.e("ADD_MULTI", "AddPostActivity, add clicked");
                    } else if (BaseActivity.this instanceof AddQuestionActivity) {
                        ((AddQuestionActivity) BaseActivity.this).addQuestion();
                       AppLog.e("ADD_MULTI", "AddQuestionActivity, add clicked");
                    } else if (BaseActivity.this instanceof PushActivity) {
                        ((PushActivity) BaseActivity.this).isValid();
                       AppLog.e("ADD_MULTI", "PushActivity, add clicked");
                    } else if (BaseActivity.this instanceof SharePersonalNameListActivity) {
                        ((SharePersonalNameListActivity) BaseActivity.this).onClickAddComment(null);
                       AppLog.e("ADD_MULTI", "SharePersonalNameListActivity, add clicked");
                    } else if (BaseActivity.this instanceof ShareGroupListActivity) {
                        ((ShareGroupListActivity) BaseActivity.this).onClickAddComment(null);
                       AppLog.e("ADD_MULTI", "ShareGroupListActivity, add clicked");
                    } else if (BaseActivity.this instanceof EditPostActivity) {
                        ((EditPostActivity) BaseActivity.this).addPost();
                       AppLog.e("ADD_MULTI", "EditPostActivity, add clicked");
                    }else if(BaseActivity.this instanceof InviteFriendActivity)
                    {
                        ((InviteFriendActivity) BaseActivity.this).addPost();
                       AppLog.e("ADD_MULTI", "InviteFriendActivity2, add clicked");
                    }
                }
            };
        } catch (NullPointerException e) {
           AppLog.e("THIS", "error is " + e.toString());
        }

        /*try {
            menu.findItem(R.id.action_add_count).setTitle(size + " Selected");
        } catch (NullPointerException e) {
           AppLog.e("MULTI_ADD", "error is " + e.toString());
        }*/
    }

    public void getContactsWithPermission() {
       /* try {
            Intent intent=new Intent(this, BackgroundTaskForUpdate.class);
            stopService(intent);
        }catch (Exception ignored){}
        */
       if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            getContacts();
            // start service code
        }
    }

    public void getContacts() {
       AppLog.e("BaseAct","getContacts ");
        new TaskForContactsFirstTime().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        if(this instanceof GroupDashboardActivityNew)
            showLoadingDialogText();
        else
            showLoadingDialog();
    }

    private class TaskForContactsFirstTime extends AsyncTask<Void, Void, Void> {

        DatabaseHandler databaseHandler;

        ArrayList<PhoneContactsItems> list = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            databaseHandler = new DatabaseHandler(BaseActivity.this);
            databaseHandler.deleteAll();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
               AppLog.e("TaskForContactsFirstTime", "ContactsFromPhone");
                Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER};

                Cursor people = getContentResolver().query(uri, projection, null, null, null);
                if(people!=null)
                {
                    int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    list = new ArrayList<>();
                    people.moveToFirst();
                    do {
                        PhoneContactsItems items = new PhoneContactsItems();

                        String name = people.getString(indexName);
                        String number = people.getString(indexNumber);

                        items.setName(name);
                        String num = number.replaceAll(",", "").replaceAll("\\.", "").replaceAll(" ", "").replaceAll("-", "").replaceAll("\\(", "").replaceAll("\\)", "");

                        if (num.length() > 2) {
                            String desiredString = num.substring(0, 2);
                            if (desiredString.equals("00")) {
                                num = num.substring(2);
                                num = "+" + num;
                            }
                        }

                        if (num.length() > 1) {
                            String desiredString = num.substring(0, 1);
                            if (desiredString.equals("0")) {
                                num = num.substring(1);
                                num = "+" + LeafPreference.getInstance(BaseActivity.this).getString(LeafPreference.CALLING_CODE) + num;
                            }
                        }

                        if (num.length() > 1) {
                            String desiredString = num.substring(0, 1);
                            if (!desiredString.equals("+")) {
                                num = "+" + LeafPreference.getInstance(BaseActivity.this).getString(LeafPreference.CALLING_CODE) + num;
                            }
                        }

                        items.setPhone(num);
                        list.add(items);
                    } while (people.moveToNext());

                    databaseHandler.addContacts(list);
                }


            } catch (Exception ignored) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(isConnectionAvailable())
            {
                LeafManager mManager = new LeafManager();
//                mManager.getAllContactsList(BaseActivity.this/*, 1*/);
                LeafPreference.getInstance(BaseActivity.this).setBoolean(LeafPreference.ISALLCONTACTSAVED, true);
                hideLoadingDialog();
                hideLoadingDialogText();
                if(BaseActivity.this instanceof GroupDashboardActivityNew)
                    ((GroupDashboardActivityNew)BaseActivity.this).reqPermission();
            }
            else {
                showNoNetworkMsg();
            }

        }
    }

    public void showInvite(boolean show) {

        if (show) {
            menu.findItem(R.id.action_invite).setVisible(true);
        } else {
            //false
            menu.findItem(R.id.action_invite).setVisible(true);
        }

    }

    public void showInviteFriends(boolean show) {

        if (show) {
            menu.findItem(R.id.action_invite).setVisible(true);
        } else {
            //false
            menu.findItem(R.id.action_invite).setVisible(false);
        }

    }

    /*public class TaskForGruppieContacts extends AsyncTask<Void, Void, Void> {

        BaseResponse response;

        public TaskForGruppieContacts(BaseResponse res) {
            this.response = res;
        }

        //        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {

            GruppieContactsModel.deleteAll();
            GruppieContactGroupIdModel.deleteAll();

            ContactListResponse res = (ContactListResponse) response;
            List<ContactListItem> list1 = res.getResults();
            DatabaseHandler databaseHandler = new DatabaseHandler(BaseActivity.this);

            *//*for (int i = 0; i < list1.size(); i++) {
                ContactListItem item = list1.get(i);

                if (databaseHandler.getCount() != 0) {
                    try {
                       AppLog.e("CONTACTSS", "count is " + databaseHandler.getCount());
                        String name = databaseHandler.getNameFromNum(item.getPhone().replaceAll(" ", ""));
                       AppLog.e("CONTACTSS", "api name is " + item.getName());
                       AppLog.e("CONTACTSS", "name is " + name);
                       AppLog.e("CONTACTSS", "num is " + item.getPhone());
                        if (!name.equals("")) {
                            item.setName(name);
                        } else {
                            item.setName(item.getName());
                        }
                    } catch (NullPointerException e) {
                        item.setName(item.getName());
                    }
                } else {
                   AppLog.e("CONTACTSS", "count is 0");
                    item.setName(item.getName());
                }
            }*//*
//            sdg;
//            new TaskForGruppieContactsFirstTime(res.getResults()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            int count = databaseHandler.getCount();
            for (int i = 0; i < res.getResults().size(); i++) {
                ContactListItem item = res.getResults().get(i);

                if (count != 0) {
                    try {
//                       AppLog.e("CONTACTSS", "count is " + databaseHandler.getCount());
                        String name = databaseHandler.getNameFromNumFirstTime(item.getPhone().replaceAll(" ", ""));
//                       AppLog.e("CONTACTSS", "api name is " + item.getName());
//                       AppLog.e("CONTACTSS", "name is " + name);
//                       AppLog.e("CONTACTSS", "num is " + item.getPhone());
                        if (!name.equals("")) {
                            item.setName(name);
                        } else {
                            item.setName(item.getName());
                        }
                    } catch (NullPointerException e) {
                        item.setName(item.getName());
                    }
                } else {
                   AppLog.e("CONTACTSS", "count is 0");
                    item.setName(item.getName());
                }
                GruppieContactsModel model = new GruppieContactsModel();
                model.contact_id = item.getId();
//                model.contact_name = item.getName();
                model.contact_name = item.getName();
                model.contact_phone = item.getPhone();
                model.contact_email = item.getEmail();
                model.contact_leadCount = item.getLeadCount();
                model.contact_dob = item.getDob();
                model.contact_qualification = item.getQualification();
                model.contact_occupation = item.getOccupation();
                model.contact_image = item.getImage();
                model.contact_otherLeads = Arrays.toString(item.getOtherLeads());
                model.contact_groups = Arrays.toString(item.getGroups());
                model.contact_group_ids = Arrays.toString(item.getGroupIds());
                model.group_ids_of_admin = Arrays.toString(Constants.getAdminArray(item.getGroupIdsOfAdmin()));
                model.usingGruppie = item.usingGruppie;
                model.contact_gender = item.gender;
                model.contact_id = item.getId();
                model.line1 = item.getAddress().line1;
                model.line2 = item.getAddress().line2;
                model.district = item.getAddress().district;
                model.state = item.getAddress().state;
                model.countryCode = item.getAddress().countryCode;
                model.pin = item.getAddress().pin;
                model.save();

               AppLog.e("GId", "Array is " + Arrays.toString(item.getGroupIds()));

                for (int j = 0; j < item.getGroupIds().length; j++) {
                    GruppieContactGroupIdModel idModel = new GruppieContactGroupIdModel();
                    idModel.id = item.getGroupIds()[j];
                    idModel.user_id = item.getId();
                   AppLog.e("GID", "gid " + idModel.id + " uid " + idModel.user_id);
                    idModel.save();
                }

            }

            String num = LeafPreference.getInstance(BaseActivity.this).getString(LeafPreference.NUM);
            databaseHandler.updateYourNum(num);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideLoadingDialog();
            if (BaseActivity.this instanceof AddFriendActivity) {
               AppLog.e("TaskForGruppieContacts", "Intent called");
                Intent intent = new Intent(BaseActivity.this, AddFriendActivity.class);
                intent.putExtra("id", ((AddFriendActivity) BaseActivity.this).groupId);
                intent.putExtra("invite", true);
                startActivity(intent);
                BaseActivity.this.finish();
            }
        }
    }*/

    public class TaskForGruppieContacts extends AsyncTask<Void, Void, Void> {

        BaseResponse response;

        public TaskForGruppieContacts(BaseResponse res) {
            this.response = res;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            GruppieContactsModel.deleteAll();
            GruppieContactGroupIdModel.deleteAll();

            ContactListResponse res = (ContactListResponse) response;
            List<ContactListItem> list1 = res.getResults();
            DatabaseHandler databaseHandler = new DatabaseHandler(BaseActivity.this);

            int count = databaseHandler.getCount();
            ActiveAndroid.beginTransaction();
            for (int i = 0; i < list1.size(); i++) {
                ContactListItem item = list1.get(i);

                if (count != 0) {
                    try {
                        String name = databaseHandler.getNameFromNumFirstTime(item.getPhone().replaceAll(" ", ""));
                        if (!name.equals("")) {
                            item.setName(name);
                        } else {
                            item.setName(item.getName());
                        }
                    } catch (NullPointerException e) {
                        item.setName(item.getName());
                    }
                } else {
                   AppLog.e("CONTACTSS", "count is 0");
                    item.setName(item.getName());
                }
                GruppieContactsModel model = new GruppieContactsModel();
                model.contact_id = item.getId();
                model.contact_name = item.getName();
                model.contact_phone = item.getPhone();
                model.contact_image = item.getImage();

                model.save();

            }
            ActiveAndroid.setTransactionSuccessful();
            ActiveAndroid.endTransaction();
            String num = LeafPreference.getInstance(BaseActivity.this).getString(LeafPreference.NUM);
            databaseHandler.updateYourNum(num);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideLoadingDialog();
            hideLoadingDialogText();
            if (BaseActivity.this instanceof AddFriendActivity) {
               AppLog.e("TaskForGruppieContacts", "Intent called");
                Intent intent = new Intent(BaseActivity.this, AddFriendActivity.class);
                intent.putExtra("id", ((AddFriendActivity) BaseActivity.this).groupId);
                intent.putExtra("invite", true);
                startActivity(intent);
                BaseActivity.this.finish();

                if(Fragment_GruppieContacts.synchFromAddFrend)
                {
                    Fragment_GruppieContacts.synchFromAddFrend=false;
                    Toast.makeText(BaseActivity.this, "Some Problem Occurred Please try again", Toast.LENGTH_SHORT).show();
                }

            } else if (BaseActivity.this instanceof AllContactListActivity) {
                Intent intent = new Intent(BaseActivity.this, AllContactListActivity.class);
                startActivity(intent);
                BaseActivity.this.finish();
            }
        }
    }

    public void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
    protected void startGallery(int requestCode) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), requestCode);
    }
}
