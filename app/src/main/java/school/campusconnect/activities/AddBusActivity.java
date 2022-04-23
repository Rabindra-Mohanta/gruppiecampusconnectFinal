package school.campusconnect.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.GroupValidationError;
import school.campusconnect.datamodel.bus.BusResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.UploadImageFragment;
import school.campusconnect.views.SMBDialogUtils;

public class AddBusActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<GroupValidationError> {

    private static final String TAG = "CreateTeamActivity";
    @Bind(R.id.etName)
    EditText etName;
    @Bind(R.id.etRouteName)
    EditText etRouteName;
    @Bind(R.id.etPhone)
    EditText etPhone;
    @Bind(R.id.tvCountry)
    TextView tvCountry;


    @Bind(R.id.btnCreateClass)
    Button btnCreateClass;

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private UploadImageFragment imageFragment;

    LeafManager leafManager;

    boolean isEdit;
    private BusResponse.BusData classData;
    private int currentCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus);

        init();

        setImageFragment();
    }

    private void setImageFragment() {
        if (isEdit) {
            imageFragment = UploadImageFragment.newInstance(classData.getImage(), true, true);
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

            SMBDialogUtils.showSMBDialogOKCancel(this, getResources().getString(R.string.smb_dialog_delete_bus), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //progressBar.setVisibility(View.VISIBLE);
                    //leafManager.deleteTeam(AddBusActivity.this, GroupDashboardActivityNew.groupId, classData.getId());
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
        setTitle(getResources().getString(R.string.lbl_add_class));
        leafManager = new LeafManager();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isEdit = bundle.getBoolean("is_edit");

            if (isEdit) {
                classData = new Gson().fromJson(bundle.getString("class_data"), BusResponse.BusData.class);
                etName.setText(classData.getDriverName());
                etPhone.setText(classData.getPhone());
                etRouteName.setText(classData.getRouteName());
                btnCreateClass.setText(getResources().getString(R.string.lbl_update));
            }
        }

        currentCountry = 1;
        String[] str = getResources().getStringArray(R.array.array_country);
        tvCountry.setText(str[0]);
    }

    @OnClick({R.id.btnCreateClass, R.id.tvCountry})
    public void onClick(View view) {
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
                        BusResponse.BusData request = new BusResponse.BusData();
                        request.driverName = etName.getText().toString();
                        request.routeName = etRouteName.getText().toString();
                        request.phone = etPhone.getText().toString();
                        request.image = imageFragment.getmProfileImage();
                        String[] str = getResources().getStringArray(R.array.array_country_values);
                        request.countryCode = str[currentCountry - 1];
                        showLoadingBar(progressBar);
                     //   progressBar.setVisibility(View.VISIBLE);
                        AppLog.e(TAG, "request :" + request);
                        leafManager.addBus(this, GroupDashboardActivityNew.groupId, request);
                        // }

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
        } else if (!isValueValid(etRouteName)) {
            valid = false;
        } else if (!isValueValid(etPhone)) {
            valid = false;
        }
        return valid;
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        if (progressBar != null)
            hideLoadingBar();
            //progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_ADD_BUS:
                finish();
                break;
            case LeafManager.API_ID_EDIT_TEAM:
                LeafPreference.getInstance(this).setBoolean(LeafPreference.ISTEAMUPDATED, true);
                finish();
                break;
            case LeafManager.API_ID_DELETE_TEAM:
                LeafPreference.getInstance(this).setBoolean(LeafPreference.ISTEAMUPDATED, true);
                finish();
                break;
        }
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<GroupValidationError> error) {
        if (progressBar != null)
            hideLoadingBar();
        //progressBar.setVisibility(View.GONE);

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
        //progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }
}
