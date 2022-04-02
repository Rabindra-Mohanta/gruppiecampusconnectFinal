package school.campusconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;

import school.campusconnect.utils.AmazoneRemove;
import school.campusconnect.utils.AppLog;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.adapters.ProfileTabAdapter;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.AddressItem;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.profile.ProfileItem;
import school.campusconnect.datamodel.profile.ProfileItemUpdate;
import school.campusconnect.datamodel.profile.ProfileResponse;
import school.campusconnect.datamodel.ProfileValidationError;
import school.campusconnect.fragments.ProfileBasicFragment;
import school.campusconnect.fragments.ProfileOtherFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.UploadCircleImageFragment;
import school.campusconnect.utils.WrappingViewPager;

public class ProfileActivity2 extends BaseActivity implements View.OnClickListener, LeafManager.OnCommunicationListener, LeafManager.OnAddUpdateListener<ProfileValidationError>, AppBarLayout.OnOffsetChangedListener {

    private static final String TAG = "ProfileActivity2";
    @Bind(R.id.appBar)
    public AppBarLayout appBarLayout;

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tabViewPager)
    WrappingViewPager mPagerView;

    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;

    @Bind(R.id.txt_name)
    public TextView txt_name;

    @Bind(R.id.tvToolbar)
    public TextView tvToolbar;


    @Bind(R.id.txt_progress)
    TextView txtProgress;


    @Bind(R.id.linDetails)
    RelativeLayout linDetails;


    @Bind(R.id.btn_update)
    ImageView btnUpdate;

    @Bind(R.id.btn_next)
    ImageView btn_next;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.progressBar1)
    ProgressBar progressBar1;

    UploadCircleImageFragment imageFragment;
    public int gender = 0;
    public int roll = 0;
    public int tabPosition = 0;
    ProfileTabAdapter tabAdapter;
    ProfileBasicFragment fragment1;
    ProfileOtherFragment fragment2;

    public static String profileImage;

    String toolbarName;

    public ProfileItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        ButterKnife.bind(this);

        initObject();

        initviews();

        if(progressBar!=null)
            progressBar.setVisibility(View.VISIBLE);

        LeafManager manager = new LeafManager();
        manager.getProfileDetails(this);

    }

    private void initObject() {
        setSupportActionBar(mToolBar);
        setBackEnabled(true);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        profileImage = "";
        appBarLayout.addOnOffsetChangedListener(this);
        imageFragment = UploadCircleImageFragment.newInstance(null, true, false);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, imageFragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    private void initviews() {
        txt_name.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        progressBar1.setMax(99);
        progressBar1.setProgress(30);
        progressBar1.setSecondaryProgress(99);


        mTabLayout.addTab(mTabLayout.newTab().setText("Basic Info"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Other Info"));

        mPagerView = (WrappingViewPager) findViewById(R.id.tabViewPager);

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                AppLog.e("getItem", "onTabSelected " + tab.getPosition());
                tabPosition = tab.getPosition();
                mPagerView.setCurrentItem(tab.getPosition());
                if (tabPosition == 0) {
                    btn_next.setVisibility(View.VISIBLE);
                    btnUpdate.setVisibility(View.GONE);
                } else {
                    btn_next.setVisibility(View.GONE);
                    btnUpdate.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    public boolean isValid() {
        boolean valid = true;
        try {
            if (!isValueValid(fragment1.edtName.editText)) {
                valid = false;
            }
            if (!isValueValid(fragment1.edtPhone.editText)) {
                valid = false;
            } else if (fragment1.edtPhone.editText.getText().toString().length() > 15) {
                fragment1.edtPhone.editText.setError(getString(R.string.msg_valid_phone));
                fragment1.edtPhone.editText.requestFocus();
                valid = false;
            }
        } catch (NullPointerException e) {
            valid = false;
        }

        return valid;
    }


    @Override
    public void onClick(View v) {
        hide_keyboard();
        if (v.getId() == R.id.btn_update) {
            callUpdateApi();
        } else if (v.getId() == R.id.btn_next) {
            mPagerView.setCurrentItem(1);
            btn_next.setVisibility(View.GONE);
            btnUpdate.setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.txt_name) {
            Intent intent = new Intent(this, AllContactListActivity.class);
            startActivity(intent);
        }
    }

    public void callUpdateApi() {
        if (isValid()) {
            if(progressBar!=null)
            progressBar.setVisibility(View.VISIBLE);

            LeafManager manager = new LeafManager();
            ProfileItemUpdate item = new ProfileItemUpdate();
            item.name = fragment1.edtName.editText.getText().toString();
            SimpleDateFormat formatTo = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            SimpleDateFormat formatFrom = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
            try {
                item.dob = formatTo.format(formatFrom.parse(fragment1.edtDob.editText.getText().toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            item.email = fragment1.edtEmail.editText.getText().toString();
            item.occupation = fragment1.edtOccupation.editText.getText().toString();
            item.qualification = fragment1.edtQualification.editText.getText().toString();

            item.address = new AddressItem();
            item.address.line1 = fragment2.edtAddressOne.editText.getText().toString();
            item.address.line2 = fragment2.edtAddressTwo.editText.getText().toString();
            item.address.state = fragment2.edtState.editText.getText().toString();
            item.address.pin = fragment2.edtPincode.editText.getText().toString();
            item.address.city = fragment2.edtCity.editText.getText().toString();
            item.address.country = fragment2.edtCountry.editText.getText().toString().toLowerCase();
            if (gender == 1) {
                item.gender = "male";

            } else if (gender == 2) {
                item.gender = "female";
            }

            if (imageFragment.isImageChanged && TextUtils.isEmpty(imageFragment.getmProfileImage())) {
                manager.deleteProPic(this);
                manager.updateProfileDetails(this, item);
               AppLog.e("Profile Activity", "image deleted " + new Gson().toJson(item));
            } else {
                if (imageFragment.isImageChanged && !TextUtils.isEmpty(imageFragment.getmProfileImage())) {
                   AppLog.e("Profile Activity", "Image Changed.." + new Gson().toJson(item));
                    item.image = imageFragment.getmProfileImage();
                }
               AppLog.e("Profile Activity", "Image Not Changed.." + new Gson().toJson(item));
                manager.updateProfileDetails(this, item);
            }

        }
    }
    public void fillDetails(ProfileItem item) {
        if(fragment1==null)
            return;

        profileImage = item.image;

        setUp(item.name);
        if (item.image != null && !item.image.isEmpty() && Constants.decodeUrlToBase64(item.image).contains("http")) {
            imageFragment.updatePhotoFromUrl(item.image);
        } else if (item.image == null) {
            Log.e("ProfileActivity", "image is Null From API ");
            imageFragment.setInitialLatterImage(item.name);
        }

        fragment1.edtEmail.editText.setText(item.email);
        fragment1.edtQualification.editText.setText(item.qualification);
        fragment1.edtOccupation.editText.setText(item.occupation);
        if (item.address != null) {
            fragment2.edtState.editText.setText(item.address.state);
            fragment2.edtPincode.editText.setText(item.address.pin);
            fragment2.edtCountry.editText.setText(item.address.country);
            fragment2.edtCity.editText.setText(item.address.city);
        }

        txtProgress.setVisibility(View.GONE);
        progressBar1.setVisibility(View.GONE);

        if (item.gender != null && !item.gender.isEmpty()) {
            if (item.gender.equalsIgnoreCase("Male")) {
                gender = 1;
            } else {
                gender = 2;
            }


        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                appBarLayout.setExpanded(true, true);
            }
        });

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        if (LeafManager.API_ID_GET_PROFILE == apiId) {
            ProfileResponse res = (ProfileResponse) response;
            AppLog.e(TAG, "ProfileResponse" + res);
            if (res != null) {
                fragment1 = new ProfileBasicFragment();
                fragment2 = new ProfileOtherFragment();
                item = res.data;
                LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.NAME, res.data.name);
                LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.PROFILE_COMPLETE, res.data.profileCompletion);

                LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.PROFILE_IMAGE, res.data.image);
                LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.EMAIL, res.data.email);
               AppLog.e("PROFILE EMAIL", "emails is " + res.data.email);
               AppLog.e("PROFILE IMAGE", "image is " + res.data.image);
                if (tabAdapter == null) {
                    tabAdapter = new ProfileTabAdapter(getSupportFragmentManager(), fragment1, fragment2);
                    mPagerView.setAdapter(tabAdapter);
                }
                mPagerView.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
                mPagerView.setOffscreenPageLimit(0);
                fillDetails(item);
            }
            imageFragment.isImageChanged = false;

        } else if (LeafManager.API_ID_DELETE_PROPIC == apiId) {
            imageFragment.isImageChanged = false;
            LeafPreference.getInstance(ProfileActivity2.this).setBoolean(LeafPreference.ISPROFILEUPDATED, true);
            AmazoneRemove.remove(item.image);
        } else {
            LeafPreference.getInstance(ProfileActivity2.this).setBoolean(LeafPreference.ISPROFILEUPDATED, true);
            Toast.makeText(this, getString(R.string.msg_profile_update), Toast.LENGTH_LONG).show();
            imageFragment.isImageChanged = false;
            finish();
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<ProfileValidationError> error) {
        progressBar.setVisibility(View.GONE);
        try {
            if(error.errors==null)
                return;

            ProfileValidationError fieldErrors = error.errors.get(0);
            if (fieldErrors.name != null) {
                fragment1.edtName.editText.setError(fieldErrors.name);
                fragment1.edtName.editText.requestFocus();
            }

            if (fieldErrors.phone != null) {
                fragment1.edtPhone.editText.setError(fieldErrors.phone);
                fragment1.edtPhone.editText.requestFocus();
            }

            if (fieldErrors.email != null) {
                fragment1.edtEmail.editText.setError(fieldErrors.email);
                fragment1.edtEmail.editText.requestFocus();
            }
            if (fieldErrors.qualification != null ) {
                fragment1.edtQualification.editText.setError(fieldErrors.qualification);
                fragment1.edtQualification.editText.requestFocus();
            }
            if (fieldErrors.occupation != null) {
                fragment1.edtOccupation.editText.setError(fieldErrors.occupation);
                fragment1.edtOccupation.editText.requestFocus();
            }
            if (fieldErrors.dob != null) {
                fragment1.edtDob.editText.setError(fieldErrors.dob);
                fragment1.edtDob.editText.requestFocus();
            }
            if (fieldErrors.gender != null ) {
                fragment1.edtGender.editText.setError(fieldErrors.gender);
                fragment1.edtGender.editText.requestFocus();
            }

            if (fieldErrors.address != null) {
                if (fieldErrors.address.country != null) {
                    fragment2.edtCountry.editText.setError(fieldErrors.address.country);
                    fragment2.edtCountry.editText.requestFocus();
                }
                if (fieldErrors.address.state != null ) {
                    fragment2.edtState.editText.setError(fieldErrors.address.state);
                    fragment2.edtState.editText.requestFocus();
                }
                if (fieldErrors.address.pin != null ) {
                    fragment2.edtPincode.editText.setError(fieldErrors.address.pin);
                    fragment2.edtPincode.editText.requestFocus();
                }
                if (fieldErrors.address.line1 != null) {
                    fragment2.edtAddressOne.editText.setError(fieldErrors.address.line1);
                    fragment2.edtAddressOne.editText.requestFocus();
                }
                if (fieldErrors.address.line2 != null) {
                    fragment2.edtAddressTwo.editText.setError(fieldErrors.address.line2);
                    fragment2.edtAddressTwo.editText.requestFocus();
                }
            }
        } catch (Exception e) {

        }

        Toast.makeText(this, "validation Error", Toast.LENGTH_LONG).show();

        if (error.status.equals("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(this, error.title, Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onFailure(int apiId, String msg) {
        //hideLoadingDialog();
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        if (msg.contains("401:Unauthorized")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onException(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset)
    {
    }


    public void setUp(String name) {

        toolbarName = name;
        tvToolbar.setText(name);
        setTitle(name);
        mToolBar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_white)); }

}
