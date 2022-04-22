package school.campusconnect.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.HomeFragment;
import school.campusconnect.utils.AppLog;
import school.campusconnect.views.SMBDialogUtils;

public class Home extends BaseActivity {
    private static final String TAG = "Home";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;
    private AlertDialog editDialog;
    public static final String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_CALENDAR
    };
    private HomeFragment homeFragment;
    private String from="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setTitle(getResources().getString(R.string.action_groups));

        if(getIntent().hasExtra("from")){
            from = getIntent().getStringExtra("from");

            if("TALUK".equalsIgnoreCase(from)){
                setTitle(getIntent().getStringExtra("talukName"));
                setBackEnabled(true);
            }
            else if("CONSTITUENCY".equalsIgnoreCase(from)){
                setTitle(getIntent().getStringExtra("categoryName"));
                setBackEnabled(true);
            }
        }

        homeFragment = new HomeFragment();
        homeFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();


        if (!hasPermission(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, 222);
        }
    }

    public boolean hasPermission(String[] permissions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(from)) {
            Home.super.onBackPressed();
        } else {
            FragmentManager fm = getSupportFragmentManager();
            AppLog.e(TAG, "BackStack count : " + fm.getBackStackEntryCount());

            editDialog = SMBDialogUtils.showSMBDialogOKCancel_(this, getResources().getString(R.string.msg_app_exit), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Home.super.onBackPressed();
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (editDialog != null) {
            editDialog.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        if (!TextUtils.isEmpty(from)) {
            menu.findItem(R.id.menu_logout).setVisible(false);
            menu.findItem(R.id.menu_change_pin).setVisible(false);

        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                logout();
                finish();
                return true;
            case R.id.menu_search:
                homeFragment.showHideSearch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
