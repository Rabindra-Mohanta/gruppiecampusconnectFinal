package school.campusconnect.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.adapters.CustomSpinnerBook;
import school.campusconnect.adapters.CustomSpinnerSubject;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.GroupValidationError;
import school.campusconnect.datamodel.booths.BoothData;
import school.campusconnect.datamodel.booths.BoothResponse;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.ebook.EBooksResponse;
import school.campusconnect.datamodel.subjects.SubjectResponse;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.UploadImageFragment;
import school.campusconnect.views.SMBDialogUtils;

public class AddBoothActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<GroupValidationError> {

    private static final String TAG = "CreateTeamActivity";
    @Bind(R.id.etName)
    EditText etName;
    @Bind(R.id.etBoothNumber)
    EditText etBoothNumber;
    @Bind(R.id.etBoothPresident)
    EditText etBoothPresident;
    @Bind(R.id.etPhone)
    EditText etPhone;
    @Bind(R.id.tvCountry)
    TextView tvCountry;
    @Bind(R.id.iconContact)
    ImageView iconContact;

    @Bind(R.id.etBoothAddress)
    EditText etBoothAddress;

    @Bind(R.id.etAboutBooth)
    EditText etAboutBooth;

    @Bind(R.id.btnCreateClass)
    Button btnCreateClass;

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private UploadImageFragment imageFragment;

    LeafManager leafManager;

    boolean isEdit;
    private MyTeamData classData;
    private int currentCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_booth);

        init();

        setImageFragment();
    }

    private void setImageFragment() {
        if (isEdit) {
            imageFragment = UploadImageFragment.newInstance(classData.image, true, true);
        } else {
            imageFragment = UploadImageFragment.newInstance(null, true, true);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, imageFragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (isEdit) {
            getMenuInflater().inflate(R.menu.menu_edit, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuDelete) {
            if (!isConnectionAvailable()) {
                showNoNetworkMsg();
                return true;
            }
            if (classData == null)
                return true;

            SMBDialogUtils.showSMBDialogOKCancel(this, getResources().getString(R.string.smb_dialog_delete_class), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showLoadingBar(progressBar,false);
                  //  progressBar.setVisibility(View.VISIBLE);
                    leafManager.deleteTeam(AddBoothActivity.this, GroupDashboardActivityNew.groupId, classData.teamId);
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_add_booth));
        leafManager = new LeafManager();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isEdit = bundle.getBoolean("is_edit");

            if (isEdit) {
                classData = new Gson().fromJson(bundle.getString("class_data"), MyTeamData.class);
                etName.setText(classData.name);
                etBoothNumber.setText(classData.boothNumber);
                etPhone.setText(classData.phone);
                etBoothPresident.setText(classData.boothPresidentName);
                btnCreateClass.setText(getResources().getString(R.string.lbl_update));
            }
        }

        currentCountry = 1;
        String[] str = getResources().getStringArray(R.array.array_country);
        tvCountry.setText(str[0]);

        tvCountry.setVisibility(View.GONE);
        iconContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //        Uri uri = Uri.parse("content://contacts");
//        Intent intent = new Intent(Intent.ACTION_PICK, uri);
//        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
//        startActivityForResult(intent, 12);

                Intent intent = new Intent(AddBoothActivity.this,SelectContactActivity.class);
                intent.putExtra("mobileList",new ArrayList<String>());
                startActivityForResult(intent,119);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

            Cursor cursor = getContentResolver().query(uri, projection,
                    null, null, null);
            cursor.moveToFirst();

            int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String number = cursor.getString(numberColumnIndex);

            int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            String name = cursor.getString(nameColumnIndex);

            Log.d(TAG, "ZZZ number : " + number + " , name : " + name);

            etBoothPresident.setText(name);
            etPhone.setText(number.replace(" ", "").replace("+91", ""));
        }
        if(requestCode==119 && resultCode== EBookActivity.RESULT_OK && data!=null){
            ArrayList<String> list = data.getStringArrayListExtra("mobileList");
            if(list!=null && list.size()>0){
                etBoothPresident.setText(list.get(0).split(",")[0]);
                etPhone.setText(list.get(0).split(",")[2]);
            }
        }
    }

    private static final long MIN_CLICK_INTERVAL = 1000; //in millis
    private long lastClickTime = 0;
    @OnClick({R.id.btnCreateClass, R.id.tvCountry})
    public void onClick(View view) {
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime - lastClickTime > MIN_CLICK_INTERVAL) {
            lastClickTime = currentTime;
        }else {
            return;
        }
        Log.e(TAG,"Tap : ");
        switch (view.getId()) {
            case R.id.btnCreateClass:
                if (isValid()) {

                    if (!isConnectionAvailable()) {
                        showNoNetworkMsg();
                        return;
                    }

                    if (isEdit) {
                       /* ClassResponse.ClassData request = new ClassResponse.ClassData();
                        request.className = etName.getText().toString();
                        request.teacherName = etTeacherName.getText().toString();
                        request.phone = etPhone.getText().toString();
                        String[] str = getResources().getStringArray(R.array.array_country_values);
                        request.countryCode = str[currentCountry - 1];

                        if (imageFragment.isImageChanged && TextUtils.isEmpty(imageFragment.getmProfileImage())) {
                            request.classImage = null;
                        } else {
                            request.className = imageFragment.getmProfileImage();
                        }
                        AppLog.e(TAG, "request :" + request);
                        progressBar.setVisibility(View.VISIBLE);
                        leafManager.editTeam(this, GroupDashboardActivityNew.groupId, myTeamData.teamId, request);*/
                    } else {
                        BoothData request = new BoothData();
                        request.boothName = etName.getText().toString();
                        request.boothNumber = etBoothNumber.getText().toString();
                        request.boothPresidentName = etBoothPresident.getText().toString();
                        request.phone = etPhone.getText().toString();
                        request.boothImage = imageFragment.getmProfileImage();
                        String[] str = getResources().getStringArray(R.array.array_country_values);
                        request.countryCode = str[currentCountry - 1];
                        request.boothAddress = etBoothAddress.getText().toString();
                        request.aboutBooth = etAboutBooth.getText().toString();

                        showLoadingBar(progressBar,false);
                    //    progressBar.setVisibility(View.VISIBLE);

                        AppLog.e(TAG, "request :" + request);
                        leafManager.addBooths(this, GroupDashboardActivityNew.groupId, request);


                    }
                }
                break;
            case R.id.tvCountry:
                SMBDialogUtils.showSMBSingleChoiceDialog(this, R.string.title_country, R.array.array_country, currentCountry - 1,
                        new SMBDailogClickListener(R.id.layout_country));
                break;
        }
    }

    public class SMBDailogClickListener implements DialogInterface.OnClickListener {
        private int DIALOG_ID = -1;

        @Override
        public void onClick(DialogInterface dialog, int which) {

            ListView lw = ((AlertDialog) dialog).getListView();


            switch (DIALOG_ID) {

                case R.id.layout_country:
                    tvCountry.setText(lw.getAdapter().getItem(lw.getCheckedItemPosition()).toString());
                    currentCountry = lw.getCheckedItemPosition() + 1;
                    break;

            }
        }

        public SMBDailogClickListener(int id) {
            DIALOG_ID = id;
        }
    }

    public boolean isValid() {
        boolean valid = true;
        if (!isValueValid(etName)) {
            valid = false;
        } else if (!isValueValid(etBoothNumber)) {
            valid = false;
        } else if (!isValueValid(etBoothPresident)) {
            valid = false;
        } else if (!isValueValid(etPhone)) {
            valid = false;
        }/*else if (etPhone.getText().toString().length()!=10) {
            Toast.makeText(this,"Please enter valid phone",Toast.LENGTH_SHORT).show();
            valid = false;
        }*/
      /*  else if (!isValueValid(etBoothAddress)) {
            valid = false;
        }
        else if (!isValueValid(etAboutBooth)) {
            valid = false;
        }*/
        return valid;
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        if (progressBar != null)
            hideLoadingBar();
           // progressBar.setVisibility(View.GONE);

        switch (apiId) {

            case LeafManager.API_ADD_BOOTH:

                hide_keyboard();
                LeafPreference.getInstance(AddBoothActivity.this).setBoolean("booth_add", true);
                finish();
                break;
        }
    }
    @Override
    public void onFailure(int apiId, ErrorResponseModel<GroupValidationError> error) {
        if (progressBar != null)
            hideLoadingBar();
        // progressBar.setVisibility(View.GONE);

        if (error.status.equals("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            if (apiId == LeafManager.API_ID_DELETE_TEAM) {
                GroupValidationError groupValidationError = (GroupValidationError) error;
                Toast.makeText(this, groupValidationError.message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, error.title, Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Override
    public void onException(int apiId, String msg) {
        super.onException(apiId, msg);
        if (progressBar != null)
            hideLoadingBar();
        // progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }
}
