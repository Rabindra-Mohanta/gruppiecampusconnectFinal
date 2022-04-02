package school.campusconnect.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;

import school.campusconnect.BuildConfig;
import school.campusconnect.utils.AppLog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import school.campusconnect.R;
import school.campusconnect.adapters.GroupTabAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.PhoneContactsItems;
import school.campusconnect.datamodel.profile.ProfileResponse;
import school.campusconnect.datamodel.versioncheck.VersionCheckResponse;
import school.campusconnect.fragments.GroupListFragment;
import school.campusconnect.fragments.PublicGroupListFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.ImageUtil;

public class GroupListActivityNew extends BaseActivity implements LeafManager.OnCommunicationListener {

    public final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 9;

    /*@Bind(R.id.toolbar)
    public Toolbar mToolBar;*/

    TextView tv_toolbar_title;
    CircleImageView tv_toolbar_icon;

    GroupListFragment fragment;
    PublicGroupListFragment fragment2;
    int version;
    public static int screenWidth;
    private ImageView tv_toolbar_default;

    @Bind(R.id.toolbar)
    Toolbar mToolBar;

    @Bind(R.id.tabViewPager)
    ViewPager mPagerView;

    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;

    SharedPreferences prefs;

    GroupTabAdapter tabAdapter;
    int tabPosition;

    LeafManager manager;


    Intent intent;
    String groupId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(false);
        ActiveAndroid.initialize(this);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID, 0);
            version = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenWidth = displaymetrics.widthPixels;

        LeafManager manager = new LeafManager();
        if (isConnectionAvailable())
            manager.versionCheck(this);

        /*if (!LeafPreference.getInstance(getApplicationContext()).getBoolean(LeafPreference.LOGOUT_FOR_212)) {
            LeafPreference.getInstance(getApplicationContext()).setBoolean(LeafPreference.LOGOUT_FOR_212, true);
            logout();
        }*/

        //SET PROFILE IMAGE ON TOOLBAR
        tv_toolbar_title = (TextView) mToolBar.findViewById(R.id.tv_toolbar_title);
        tv_toolbar_icon = (CircleImageView) mToolBar.findViewById(R.id.iv_toolbar_icon);
        tv_toolbar_default = (ImageView) mToolBar.findViewById(R.id.iv_toolbar_default);

        String name = LeafPreference.getInstance(this).getString(LeafPreference.NAME);
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(ImageUtil.getTextLetter(name), ImageUtil.getRandomColor(1));
        tv_toolbar_default.setImageDrawable(drawable);
        tv_toolbar_default.setVisibility(View.VISIBLE);

        (tv_toolbar_title).setText(name);
        tv_toolbar_title.setVisibility(View.VISIBLE);

        tv_toolbar_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnectionAvailable()) {
                    Intent intent = new Intent(GroupListActivityNew.this, ProfileActivity2.class);
                    startActivity(intent);
                } else {
                    showNoNetworkMsg();
                }
            }
        });

        tv_toolbar_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnectionAvailable()) {
                    Intent intent = new Intent(GroupListActivityNew.this, ProfileActivity2.class);
                    startActivity(intent);
                } else {
                    showNoNetworkMsg();
                }
            }
        });

        tv_toolbar_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnectionAvailable()) {
                    Intent intent = new Intent(GroupListActivityNew.this, ProfileActivity2.class);
                    startActivity(intent);
                } else {
                    showNoNetworkMsg();
                }
            }
        });


        fragment = GroupListFragment.newInstance();
        fragment2 = PublicGroupListFragment.newInstance();

//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();


        // only for gingerbread and newer versions
        mTabLayout.addTab(mTabLayout.newTab().setText("My Groups"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Public Groups"));

        mPagerView = (ViewPager) findViewById(R.id.tabViewPager);

        tabAdapter = new GroupTabAdapter(getSupportFragmentManager(), groupId, fragment, fragment2);
        mPagerView.setAdapter(tabAdapter);
        mPagerView.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mPagerView.setOffscreenPageLimit(2);

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabPosition = tab.getPosition();
                mPagerView.setCurrentItem(tab.getPosition());

                /*View v1 = tab.getCustomView();
                TextView tv1 = (TextView) v1.findViewById(R.id.tab_badge);
                tv1.setTextColor(ContextCompat.getColor(GroupListActivityNew.this, R.color.colorPrimary));*/

               AppLog.e("TAB POS", "position is " + tabPosition);
                final int sdk = android.os.Build.VERSION.SDK_INT;
                if (tabPosition == 0) {


                   AppLog.e("TAB POS", "position is " + tabPosition);
                    View v2 = mTabLayout.getTabAt(1).getCustomView();
                    TextView tv2 = (TextView) v2.findViewById(R.id.tab_badge);
                    tv2.setText("Public Groups");
                    ImageView img2 = (ImageView) v2.findViewById(R.id.tab_icon);

                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        img2.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_public_black_24dp));
                    } else {
                        img2.setBackground(getResources().getDrawable(R.drawable.ic_public_black_24dp));
                    }
                    tv2.setTextColor(ContextCompat.getColor(GroupListActivityNew.this, R.color.color_grey_icon));
//                    mTabLayout.getTabAt(1).setCustomView(v2);


                    View v1 = mTabLayout.getTabAt(0).getCustomView();
                    TextView tv = (TextView) v1.findViewById(R.id.tab_badge);
                    tv.setText("My Groups");
                    ImageView img = (ImageView) v1.findViewById(R.id.tab_icon);
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        img.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_group_work_black_24dp));
                    } else {
                        img.setBackground(getResources().getDrawable(R.drawable.ic_group_work_black_24dp));
                    }
                    tv.setTextColor(ContextCompat.getColor(GroupListActivityNew.this, R.color.colorPrimary));
//                    mTabLayout.getTabAt(0).setCustomView(v1);

                } else {
                    View v2 = mTabLayout.getTabAt(1).getCustomView();
                    TextView tv2 = (TextView) v2.findViewById(R.id.tab_badge);
                    tv2.setText("Public Groups");
                    ImageView img2 = (ImageView) v2.findViewById(R.id.tab_icon);

                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        img2.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_public_black_24dp));
                    } else {
                        img2.setBackground(getResources().getDrawable(R.drawable.ic_public_black_24dp));
                    }
                    tv2.setTextColor(ContextCompat.getColor(GroupListActivityNew.this, R.color.colorPrimary));
//                    mTabLayout.getTabAt(1).setCustomView(v2);

                    View v1 = mTabLayout.getTabAt(0).getCustomView();
                    TextView tv1 = (TextView) v1.findViewById(R.id.tab_badge);
                    tv1.setText("My Groups");
                    ImageView img = (ImageView) v1.findViewById(R.id.tab_icon);
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        img.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_group_work_black_24dp));
                    } else {
                        img.setBackground(getResources().getDrawable(R.drawable.ic_group_work_black_24dp));
                    }
                    tv1.setTextColor(ContextCompat.getColor(GroupListActivityNew.this, R.color.color_grey_icon));
//                    mTabLayout.getTabAt(0).setCustomView(v1);
                }
               AppLog.e("TAB POS", "position is " + tabPosition);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        //error
//        FirebaseMessaging.getInstance().subscribeToTopic("testfirebasetopic");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fab.setBackgroundColor(getResources().getColor(R.color.color_green, null));
        } else {
            fab.setBackgroundColor(getResources().getColor(R.color.color_green));
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupListActivityNew.this, CreateAccountActivity.class);
                startActivity(intent);
            }
        });


*/

        final String image = LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.PROFILE_IMAGE);
        if (image != null && !image.isEmpty()) {

            Picasso.with(this).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(tv_toolbar_icon, new Callback() {
                @Override
                public void onSuccess() {
                    tv_toolbar_default.setVisibility(View.GONE);
                    tv_toolbar_icon.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    Picasso.with(GroupListActivityNew.this).load(image).into(tv_toolbar_icon, new Callback() {
                        @Override
                        public void onSuccess() {
                            tv_toolbar_default.setVisibility(View.GONE);
                            tv_toolbar_icon.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            tv_toolbar_icon.setVisibility(View.GONE);
                            tv_toolbar_default.setVisibility(View.VISIBLE);
                        }
                    });

                }
            });
        } else {
            tv_toolbar_icon.setVisibility(View.GONE);
            tv_toolbar_default.setVisibility(View.VISIBLE);
        }
        /*mTabLayout.getTabAt(0).setIcon(R.drawable.ic_group_work_black_24dp);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_public_black_24dp);*/
        createTabIcons();
    }

    public void goToPublic(){
        mPagerView.setCurrentItem(1);
        try {
            TabLayout.Tab tab = mTabLayout.getTabAt(1);
            tab.select();
        } catch (NullPointerException e) {
           AppLog.e("MAINFRAG", "onresume error is " + e.toString());
        }
    }

    private void createTabIcons() {
//        https://stackoverflow.com/questions/40896907/can-a-custom-view-be-used-as-a-tabitem
        View v1 = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        TextView tv = (TextView) v1.findViewById(R.id.tab_badge);
        tv.setText("My Groups");
        ImageView img = (ImageView) v1.findViewById(R.id.tab_icon);
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            img.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_group_work_black_24dp));
        } else {
            img.setBackground(getResources().getDrawable(R.drawable.ic_group_work_black_24dp));
        }
        tv.setTextColor(ContextCompat.getColor(GroupListActivityNew.this, R.color.colorPrimary));
        mTabLayout.getTabAt(0).setCustomView(v1);

        View v2 = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        TextView tv2 = (TextView) v2.findViewById(R.id.tab_badge);
        tv2.setText("Public Groups");
        ImageView img2 = (ImageView) v2.findViewById(R.id.tab_icon);
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            img2.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_public_black_24dp));
        } else {
            img2.setBackground(getResources().getDrawable(R.drawable.ic_public_black_24dp));
        }
        mTabLayout.getTabAt(1).setCustomView(v2);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*if (item.getItemId() == R.id.action_notification) {
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
        } else*/

        if (item.getItemId() == R.id.nav_logout) {
            if (isConnectionAvailable()) {
                logoutUser();
            } else {
                showNoNetworkMsg();
            }
        } else if (item.getItemId() == R.id.nav_all_contacts) {
//            if (isConnectionAvailable()) {
            Intent intent = new Intent(this, AllContactListActivity.class);
            startActivity(intent);
//            } else {
//                showNoNetworkMsg();
//            }
        } else if (item.getItemId() == R.id.nav_change_pwd) {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_change_number) {
            Intent intent = new Intent(this, ChangeNumberActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_creategroup) {
            Intent intent = new Intent(GroupListActivityNew.this, CreateAccountActivity.class);
            startActivity(intent);
        }

        /*else if (item.getItemId() == R.id.nav_profile) {
            if (isConnectionAvailable()) {
                Intent intent = new Intent(GroupListActivityNew.this, ProfileActivity.class);
                startActivity(intent);
            } else {
                showNoNetworkMsg();
            }
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
       AppLog.e("GroupListAct onresume", "onResume: called");
        if (LeafPreference.getInstance(this).getBoolean(LeafPreference.ISPROFILEUPDATED)) {
           AppLog.e("GroupListAct onresume", "onResume: prof is updated");
            LeafManager manager = new LeafManager();
            manager.getProfileDetails(this);
            LeafPreference.getInstance(this).setBoolean(LeafPreference.ISPROFILEUPDATED, false);
        }
    }

    private void logoutUser() {
        LeafManager manager = new LeafManager();
        manager.logout(this);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        if (apiId == LeafManager.API_VERSION) {
            VersionCheckResponse res = (VersionCheckResponse) response;
            if (res.data.version > version) {
                showUpdate(res.data.message);
            }
        } else if (apiId == LeafManager.API_ID_GET_PROFILE) {
            ProfileResponse res = (ProfileResponse) response;

            LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.NAME, res.data.name);
            LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.PROFILE_COMPLETE, res.data.profileCompletion);
            LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.PROFILE_IMAGE, res.data.image);

            LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.EMAIL, res.data.email);
           AppLog.e("PROFILE EMAIL", "emails is " + res.data.email);
           AppLog.e("PROFILE IMAGE", "image is " + res.data.image);
            String name = LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.NAME);
            (tv_toolbar_title).setText(name);

            final String image = LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.PROFILE_IMAGE);
            if (image != null && !image.isEmpty()) {

                Picasso.with(this).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(tv_toolbar_icon, new Callback() {
                    @Override
                    public void onSuccess() {
                        tv_toolbar_default.setVisibility(View.GONE);
                        tv_toolbar_icon.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                        Picasso.with(GroupListActivityNew.this).load(image).into(tv_toolbar_icon, new Callback() {
                            @Override
                            public void onSuccess() {
                                tv_toolbar_default.setVisibility(View.GONE);
                                tv_toolbar_icon.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError() {
                                tv_toolbar_icon.setVisibility(View.GONE);
                                tv_toolbar_default.setVisibility(View.VISIBLE);
                            }
                        });

                    }
                });
            } else {
                tv_toolbar_icon.setVisibility(View.GONE);
                tv_toolbar_default.setVisibility(View.VISIBLE);
            }
        } else {
           AppLog.e("Logout", "onSuccessCalled");
            logout();
           finish();
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
       AppLog.e("Logout", "onFailureCalled");
        //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        if (msg.contains("401:Unauthorized")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onException(int apiId, String msg) {
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fragment.getContacts();
                }
        }

    }

    public void showUpdate(String msg) {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_update);

        TextView btn_ok = (TextView) dialog.findViewById(R.id.btn_ok);

        TextView btn_cancel = (TextView) dialog.findViewById(R.id.btn_cancel);

        final TextView txt_text = (TextView) dialog.findViewById(R.id.txt_text);

        txt_text.setText(msg);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = BuildConfig.APPLICATION_ID; // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void getContactsWithPermission() {
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
        new TaskForContactsFirstTime().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class TaskForContactsFirstTime extends AsyncTask<Void, Void, Void> {

        DatabaseHandler databaseHandler;

        ArrayList<PhoneContactsItems> list = new ArrayList<>();
        ArrayList<String> contactNames = new ArrayList<>();
        ArrayList<String> contactNumbers = new ArrayList<>();

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            databaseHandler = new DatabaseHandler(GroupListActivityNew.this);
            dialog = new ProgressDialog(GroupListActivityNew.this);
            dialog.setMessage("Syncing your contacts to Gruppie please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
               AppLog.e("Called", "elseCalled" + " ContactsFromPhone");
                Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER};

                Cursor people = GroupListActivityNew.this.getContentResolver().query(uri, projection, null, null, null);
                assert people != null;
                int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                people.moveToFirst();
                do {
                    String name = people.getString(indexName);
                    String number = people.getString(indexNumber);
                    // Do work...
                    contactNames.add(name);
                    contactNumbers.add(number);
                } while (people.moveToNext());


            } catch (Exception ignored) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            list = new ArrayList<>();
            for (int i = 0; i < contactNames.size(); i++) {
                PhoneContactsItems items = new PhoneContactsItems();
                items.setName(contactNames.get(i));
                items.setPhone(contactNumbers.get(i));
                list.add(items);
            }
            Collections.sort(list, new ContactName());
            for (int i = 0; i < list.size(); i++) {
                databaseHandler.addContacts(list.get(i).getName(), list.get(i).getPhone());
            }

            if (dialog != null && dialog.isShowing())
                dialog.dismiss();

        }
    }

    public class ContactName implements Comparator<PhoneContactsItems> {
        @Override
        public int compare(PhoneContactsItems items, PhoneContactsItems t1) {
            return items.getName().compareTo(t1.getName());
        }
    }

}
