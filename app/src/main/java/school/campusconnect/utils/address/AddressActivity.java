package school.campusconnect.utils.address;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import school.campusconnect.R;
import school.campusconnect.activities.BaseActivity;

public class AddressActivity extends BaseActivity {
    String TAG="AddressActivity";
    private static final int REQUEST_CHECK_SETTINGS = 123;

    FrameLayout fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        init();

    }
    private void init()
    {
        fragment = findViewById(R.id.fragment);
        Bundle bundle;
        bundle = getIntent().getExtras();
        selectFrag(new SelectAddressFragment(),bundle,false,false);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.e(getClass().getSimpleName(),"OnResume Called");
    }

    public void selectFrag(Fragment fragment, Bundle bundle, boolean isBackStack, boolean isPopBack)
    {
        if (bundle != null)
            fragment.setArguments(bundle);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        String backStateName = fragment.getClass().getName();
        boolean fragmentPopped = fm.popBackStackImmediate(backStateName, 0);

        if (isPopBack) {
            fm.popBackStack();
        }
        if (isBackStack) {
            fragmentTransaction.addToBackStack("");
        }

        if (!fragmentPopped) {
            fragmentTransaction.replace(R.id.fragment, fragment);
        }

        fragmentTransaction.commit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        Log.e(TAG,"OnActivity Result Called" + requestCode);

        switch (requestCode){

            case REQUEST_CHECK_SETTINGS:{

                Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragment);
                if (frag instanceof SelectAddressFragment)
                {
                    Log.e(TAG,resultCode+"");

                    if(resultCode==RESULT_OK)
                        ((SelectAddressFragment)frag).getGPS();
                    else
                        Toast.makeText(getApplicationContext(), "Please Enable Gps", Toast.LENGTH_SHORT).show();
                }
            }

            default:
                super.onActivityResult(requestCode,resultCode,data);
                break;

        }

    }
}