package school.campusconnect.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import school.campusconnect.BuildConfig;
import school.campusconnect.datamodel.profileCaste.CasteResponse;
import school.campusconnect.datamodel.profileCaste.ProfessionResponce;
import school.campusconnect.datamodel.profileCaste.ReligionResponse;
import school.campusconnect.datamodel.profileCaste.SubCasteResponse;
import school.campusconnect.fragments.DatePickerFragment;
import school.campusconnect.fragments.SearchCastFragmentDialog;
import school.campusconnect.fragments.SearchSubCasteDialogFragment;
import school.campusconnect.utils.AppLog;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.SignUpRequest;
import school.campusconnect.datamodel.SignupValidationError;
import school.campusconnect.network.LeafManager;

public class SignUpActivity2 extends BaseActivity implements LeafManager.OnCommunicationListener,LeafManager.OnAddUpdateListener<SignupValidationError>,SearchCastFragmentDialog.SelectListener,SearchSubCasteDialogFragment.SelectListener {


    public String TAG = "SignUpActivity2";
    @Bind(R.id.layout_signup_name)
    EditText edtName;

    @Bind(R.id.layout_signup_email)
    EditText edtEmail;

    @Bind(R.id.tvEmailLbl)
    TextView tvEmailLbl;

    @Bind(R.id.btnNext)
    Button btnNext;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.etdob)
    public EditText etdob;

//    @Bind(R.id.etDesig)
//    public EditText etDesig;

    @Bind(R.id.etprofession)
    Spinner etprofession;

    @Bind(R.id.etCategory)
    public EditText etCategory;

    @Bind(R.id.etEducation)
    public EditText etEducation;

    @Bind(R.id.etCaste)
    public TextView etCaste;

    @Bind(R.id.etSubCaste)
    public TextView etSubCaste;

    @Bind(R.id.etReligion)
    public Spinner etReligion;

    @Bind(R.id.llData)
    public LinearLayout llData;

    @Bind(R.id.etgender)
    Spinner etgender;

    String pNumber;
    String countryCode;
    String countryName = "";
    private Boolean validateUser = false;


    ArrayAdapter<String> religionAdapter;
    ArrayAdapter<String> professionAdapter;
    ArrayList<String> religionList = new ArrayList<>();
    ArrayList<String> professionList = new ArrayList<>();
    ArrayList<String> casteList = new ArrayList<>();
    ArrayList<String> subCasteList = new ArrayList<>();
    ArrayList<CasteResponse.CasteData> casteDataList = new ArrayList<>();

    boolean isFirstTimeReligion = true;
    boolean isFirstTimeCaste = true;
    boolean isFirstTimeSubCaste = true;

    String casteId = null;

    String religion = null;
    String caste = null;
    String subcaste = null;
    String gender;


    SearchCastFragmentDialog searchCastFragmentDialog;
    SearchSubCasteDialogFragment searchSubCasteDialogFragment;
    public boolean isCasteClickable = false;

    LeafManager leafManager;

    public static final String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);
        ButterKnife.bind(this);

        leafManager = new LeafManager();

        searchCastFragmentDialog = SearchCastFragmentDialog.newInstance();
        searchCastFragmentDialog.setListener(this);

        searchSubCasteDialogFragment = SearchSubCasteDialogFragment.newInstance();
        searchSubCasteDialogFragment.setListener(this);


        pNumber = LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.phoneNumber);
        AppLog.e("TAG", "Number is " + pNumber);
        countryCode = "IN";

        Intent intent = getIntent();

        String[] genderArray=getResources().getStringArray(R.array.gender_array);
        ArrayAdapter<String> genderAdapter= new ArrayAdapter<String>(getApplicationContext(),R.layout.item_spinner,R.id.tvItem,genderArray);
        etgender.setAdapter(genderAdapter);

        if (intent.getExtras() != null) {
            countryCode = intent.getExtras().getString("countryCode", "IN");
            countryName = intent.getExtras().getString("Country", "");
            validateUser = intent.getBooleanExtra("userFlag",false);
        }

        if (countryName.equalsIgnoreCase("India")) {
            tvEmailLbl.setVisibility(View.GONE);
            edtEmail.setVisibility(View.GONE);
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSignUp();
            }
        });

        edtName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnNext.performClick();
                    handled = true;
                }
                return handled;
            }
        });


        etCaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isCasteClickable)
                {
                    searchCastFragmentDialog.show(getSupportFragmentManager(),"");
                }
            }
        });

        etSubCaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isCasteClickable)
                {
                    searchSubCasteDialogFragment.show(getSupportFragmentManager(),"");
                }

            }
        });


        etdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment fragment = DatePickerFragment.newInstance();
                fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                    @Override
                    public void onDateSelected(Calendar c) {
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        etdob.setText(format.format(c.getTime()));
                    }
                });
                fragment.show(getSupportFragmentManager(), "datepicker");
            }
        });



        etgender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position !=0 )
                {
                    gender = etgender.getSelectedItem().toString();
                }
                else
                {

                    gender=null;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        etReligion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0)
                {
                    religion = etReligion.getSelectedItem().toString();
                    isCasteClickable = true;
                    showLoadingBar(progressBar);
                    leafManager.getCaste(SignUpActivity2.this,etReligion.getSelectedItem().toString());
                }
                else
                {
                    religion = null;
                    isCasteClickable = false;
                    etCaste.setText("");
                    etSubCaste.setText("");
                    etCategory.setText("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (BuildConfig.AppCategory.equalsIgnoreCase("constituency"))
        {
            llData.setVisibility(View.VISIBLE);
            leafManager.getReligion(this);
            leafManager.getProfession(this);


        }
    }

    @Override
    public void onSelected(CasteResponse.CasteData casteData) {

        etCaste.setText(casteData.getCasteName());
        etCategory.setText(casteData.getCategoryName());

        casteId = casteData.getCasteId();

        if (casteId != null)
        {
            showLoadingBar(progressBar);
            leafManager.getSubCaste(this,casteId);
        }
    }

    @Override
    public void onSelected(SubCasteResponse.SubCasteData casteData) {

        etSubCaste.setText(casteData.getSubCasteName());

    }

    public boolean isValid() {





        if (TextUtils.isEmpty(edtName.getText().toString()))
        {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_enter_name),Toast.LENGTH_SHORT).show();
            return false;
        }



        if (BuildConfig.AppCategory.equalsIgnoreCase("constituency"))
        {
            if (TextUtils.isEmpty(etEducation.getText().toString()))
            {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_enter_education),Toast.LENGTH_SHORT).show();
                return false;
            }

            if(etgender.getSelectedItem().toString().isEmpty())
            {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.hint_lead_gender),Toast.LENGTH_LONG).show();
                return  false;
            }

            if (etprofession.getSelectedItem().toString().isEmpty())
            {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_select_profession),Toast.LENGTH_SHORT).show();
                return false;
            }

            if (TextUtils.isEmpty(etdob.getText().toString()))
            {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_enter_dob),Toast.LENGTH_SHORT).show();
                return false;
            }

            if (religion == null || TextUtils.isEmpty(religion))
            {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_select_religion),Toast.LENGTH_SHORT).show();
                return false;
            }

            if (TextUtils.isEmpty(etCaste.getText().toString()))
            {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_select_caste),Toast.LENGTH_SHORT).show();
                return false;
            }

            if (TextUtils.isEmpty(etSubCaste.getText().toString()))
            {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_select_sub_caste),Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    SignUpRequest request;
    public void doSignUp() {

        if (isConnectionAvailable()) {

            if (isValid()) {

             //       progressBar.setVisibility(View.VISIBLE);
                LeafManager manager = new LeafManager();
                request = new SignUpRequest();
                request.name = edtName.getText().toString();
                request.phone = pNumber;
                request.countryCode = countryCode;


                if (BuildConfig.AppCategory.equalsIgnoreCase("constituency"))
                {
                    request.designation = etprofession.getSelectedItem().toString();
                    request.dob = etdob.getText().toString();
                    request.caste = etCaste.getText().toString();
                    request.subCaste = etSubCaste.getText().toString();
                    request.religion = etReligion.getSelectedItem().toString();
                    request.category = etCategory.getText().toString();
                    request.qualification = etEducation.getText().toString();
                    request.gender=etgender.getSelectedItem().toString();


                    AppLog.e("TAG", new Gson().toJson(request));

                  //  reqPermission();

                    if(hasPermission(permissions))
                    {
                        Intent i = new Intent(getApplicationContext(), SignupImageActivity.class);
                        i.putExtra("req",request);
                        i.putExtra("userFlag",validateUser);
                        startActivity(i);
                        finish();
                    }
                    else
                    {
                        ActivityCompat.requestPermissions(this, permissions, 222);
                    }


                }
                else
                {
                    if (progressBar != null)
                        showLoadingBar(progressBar,true);

                    AppLog.e("TAG", new Gson().toJson(request));

                    btnNext.setEnabled(false);
                    manager.doSignUp(this, request);
                }



            }
        } else {

            showNoNetworkMsg();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (hasPermission(permissions))
        {
            Intent i = new Intent(getApplicationContext(), SignupImageActivity.class);
            i.putExtra("req",request);
            i.putExtra("userFlag",validateUser);
            startActivity(i);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void reqPermission(){
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
    public void onSuccess(int apiId, BaseResponse response) {
        btnNext.setEnabled(true);
        //hideLoadingDialog();
        if (progressBar != null)
            hideLoadingBar();
        //   progressBar.setVisibility(View.GONE);







        if (LeafManager.API_RELIGION_GET == apiId)
        {
            ReligionResponse res = (ReligionResponse) response;

            AppLog.e(TAG, "ReligionResponse" + res);

            religionList.clear();
            religionList.add(0,"select religion");
            religionList.addAll(res.getReligionData().get(0).getReligionList());

            if (res.getReligionData().size() > 0)
            {
                religionAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, religionList);
                etReligion.setAdapter(religionAdapter);
                etReligion.setEnabled(true);
            }
            if (isFirstTimeReligion)
            {
                isFirstTimeReligion = false;
                etReligion.setEnabled(true);
                etReligion.setSelection(religionAdapter.getPosition("select religion"));
            }
        }

        else if (LeafManager.API_CASTE_GET == apiId)
        {
            CasteResponse res = (CasteResponse) response;

            AppLog.e(TAG, "CasteResponse" + res);

           /* casteDataList.clear();
            casteDataList.addAll(res.getCasteData());*/

            casteDataList.clear();
            casteDataList.addAll(res.getCasteData());
            casteList.clear();

            for (int i=0;i<res.getCasteData().size();i++)
            {
                casteList.add(res.getCasteData().get(i).getCasteName());
            }

            if (casteList.size() > 0)
            {
                if (isFirstTimeCaste)
                {
                    etCaste.setTextColor(getResources().getColor(R.color.grey));

                    if (caste != null)
                    {
                        etCaste.setText(caste);
                    }
                    else
                    {
                        etCaste.setText(res.getCasteData().get(0).getCasteName());
                    }
                    for (int i=0;i<casteDataList.size();i++)
                    {
                        if (etCaste.getText().toString().toLowerCase().trim().equalsIgnoreCase(casteDataList.get(i).getCasteName().toLowerCase().trim()))
                        {
                            casteId = casteDataList.get(i).getCasteId();
                            etCategory.setText(casteDataList.get(i).getCategoryName());
                        }
                    }

                    etCaste.setTextColor(getResources().getColor(R.color.white));
                    etCaste.setEnabled(true);

                   /* if (isEdit)
                    {
                        isFirstTimeCaste = false;
                        etCaste.setTextColor(getResources().getColor(R.color.white));
                        etCaste.setEnabled(true);
                    }
                    else {

                    }*/
                }
                else
                {
                    casteId = res.getCasteData().get(0).getCasteId();
                    etCaste.setText(res.getCasteData().get(0).getCasteName());
                    etCategory.setText(res.getCasteData().get(0).getCategoryName());
                }

                searchCastFragmentDialog.setData(res.getCasteData());

            }

            if (casteId != null)
            {
                showLoadingBar(progressBar);
                leafManager.getSubCaste(this,casteId);
            }

        }

        else if (LeafManager.API_SUB_CASTE_GET == apiId)
        {
            SubCasteResponse res = (SubCasteResponse) response;

            AppLog.e(TAG, "SubCasteResponse" + res);

            casteId = null;

            subCasteList.clear();

            for (int i=0;i<res.getSubCasteData().size();i++)
            {
                subCasteList.add(res.getSubCasteData().get(i).getSubCasteName());
            }


            if (subCasteList.size() > 0)
            {

                if (isFirstTimeSubCaste)
                {
                    etSubCaste.setTextColor(getResources().getColor(R.color.grey));

                    if (subcaste != null)
                    {
                        etSubCaste.setText(subcaste);
                    }
                    else
                    {
                        etSubCaste.setText(res.getSubCasteData().get(0).getSubCasteName());
                    }

                    isFirstTimeSubCaste = false;
                    etSubCaste.setTextColor(getResources().getColor(R.color.white));
                    etSubCaste.setEnabled(true);

                }
                else
                {
                    etSubCaste.setText(res.getSubCasteData().get(0).getSubCasteName());
                }

                searchSubCasteDialogFragment.setData(res.getSubCasteData());
            }
        }



       else  if(LeafManager.API_PROFESSION_GET==apiId)

        {
            ProfessionResponce res = (ProfessionResponce) response;
            professionList.clear();
            professionList.add(0,"select profession");
            professionList.addAll(res.getProfessionDataList().get(0).getProfession());
            if(res.getProfessionDataList().size() > 0)
            {
                professionAdapter=new ArrayAdapter<String>(this,R.layout.item_spinner,R.id.tvItem,professionList);
                etprofession.setAdapter(professionAdapter);
            }
        }
        else
        {
            Intent i = new Intent(this, UserExistActivity.class);
            i.putExtra("userFlag",validateUser);
            startActivity(i);
            finish();

            btnNext.setEnabled(true);
        }

    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<SignupValidationError> error) {
        btnNext.setEnabled(true);
        //hideLoadingDialog();
        if (progressBar != null)
            hideLoadingBar();
        //   progressBar.setVisibility(View.GONE);

    }

    @Override
    public void onException(int apiId, String error) {
        btnNext.setEnabled(true);
        //hideLoadingDialog();
        if (progressBar != null)
            hideLoadingBar();
        //   progressBar.setVisibility(View.GONE);
        Toast.makeText(SignUpActivity2.this, error, Toast.LENGTH_SHORT).show();
    }
}
