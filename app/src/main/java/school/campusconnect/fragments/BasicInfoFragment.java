package school.campusconnect.fragments;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.Toast;


import androidx.databinding.DataBindingUtil;

import androidx.lifecycle.ViewModelProvider;


import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


import school.campusconnect.AddClassViewModel;
import school.campusconnect.R;
import school.campusconnect.activities.AddClassStudentActivity;


import school.campusconnect.activities.AddStudentActivity;
import school.campusconnect.databinding.FragmentBasicInfoBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.profileCaste.CasteResponse;
import school.campusconnect.datamodel.profileCaste.SubCasteResponse;
import school.campusconnect.datamodel.student.StudentRes;
import school.campusconnect.network.LeafManager;


import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.UploadImageFragment;
import school.campusconnect.views.SMBDialogUtils;


public class BasicInfoFragment extends BaseFragment implements LeafManager.OnCommunicationListener, SearchCastFragmentDialog.SelectListener, SearchSubCasteDialogFragment.SelectListener {

    public String UserName;

    FragmentBasicInfoBinding basicInfoBinding;
    public static AddClassViewModel addClassViewModel;

    private UploadImageFragment imageFragment;

    private String[] str;
    String group_id, team_id, userId, gruppieRollNoNumber;
    private boolean isEdit = false;
    public StudentRes.StudentData studentData;

    public LeafManager leafManager;
    public int currentCountry;

    SearchCastFragmentDialog searchCastFragmentDialog;
    SearchSubCasteDialogFragment searchSubCasteDialogFragment;


    public BasicInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_basic_info, container, false);
        basicInfoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_basic_info, container, false);


        addClassViewModel = new ViewModelProvider(this).get(AddClassViewModel.class);
        init();
        addClassViewModel.studentDataMutableLiveData.postValue(studentData);

        basicInfoBinding.setMyStudent(addClassViewModel);
        basicInfoBinding.setLifecycleOwner(this);
        currentCountry = 1;
        str = getResources().getStringArray(R.array.array_country);


        return basicInfoBinding.getRoot();

    }


    private void init() {
        studentData = AddClassStudentActivity.studentData;
        leafManager = new LeafManager();
        group_id = AddClassStudentActivity.group_id;
        team_id = AddClassStudentActivity.team_id;
        imageFragment = AddClassStudentActivity.imageFragment;
        searchCastFragmentDialog = SearchCastFragmentDialog.newInstance();
        searchCastFragmentDialog.setListener(this);
        searchSubCasteDialogFragment = SearchSubCasteDialogFragment.newInstance();
        searchSubCasteDialogFragment.setListener(this);


        userId = studentData.getUserId();
        gruppieRollNoNumber = studentData.getGruppieRollNumber();


        basicInfoBinding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });

//        basicInfoBinding.etCountry.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SMBDialogUtils.showSMBSingleChoiceDialog(getActivity(), R.string.title_country, R.array.array_country, currentCountry - 1,
//                        new AddStudentActivity.SMBDailogClickListener(getActivity().findViewById(R.id.etCountry).getId()));
//            }
//        });
        basicInfoBinding.etdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatePickerFragment fragment = DatePickerFragment.newInstance();
                fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                    @Override
                    public void onDateSelected(Calendar c) {
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        basicInfoBinding.etdob.setText(format.format(c.getTime()));
                    }
                });
                fragment.show(getChildFragmentManager(), "datepicker");


            }
        });


        basicInfoBinding.etdoj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatePickerFragment fragment2 = DatePickerFragment.newInstance();
                fragment2.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                    @Override
                    public void onDateSelected(Calendar c) {
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        basicInfoBinding.etdoj.setText(format.format(c.getTime()));
                    }
                });
                fragment2.show(getChildFragmentManager(), "datepicker");

            }
        });


    }


    private boolean isValid() {

        if (!isValueValid(basicInfoBinding.etName)) {
            return false;
        } else if (!isValueValid(basicInfoBinding.etPhone)) {
            return false;
        }

        return true;


    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        switch (apiId) {

            case LeafManager.API_EDIT_STUDENTS:

                String phone = "";

                if (!basicInfoBinding.etPhone.getText().toString().contains("+91")) {
                    phone = "+91" + basicInfoBinding.etPhone.getText().toString();
                } else {
                    phone = basicInfoBinding.etPhone.getText().toString();
                }

                Log.e(TAG, "phone " + phone);

                if (!studentData.getPhone().toString().equalsIgnoreCase(phone)) {

                    StudentRes.StudentData addStudentReq = new StudentRes.StudentData();
                    String[] str = getResources().getStringArray(R.array.array_country_values);
                    addStudentReq.countryCode = str[currentCountry - 1];
                    addStudentReq.phone = basicInfoBinding.etPhone.getText().toString();

                    //progressBar.setVisibility(View.VISIBLE);
                    leafManager.editClassStudentPhone(this, group_id, studentData.getUserId(), addStudentReq);
                } else {

                    Toast.makeText(getContext(), getResources().getString(R.string.toast_edit_student_sucess), Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }


                break;

            case LeafManager.API_UPDATE_PHONE_STUDENT:
                Toast.makeText(getContext(), getResources().getString(R.string.toast_edit_student_sucess), Toast.LENGTH_SHORT).show();
                getActivity().finish();
                break;
        }

    }

    @Override
    public void onFailure(int apiId, String msg) {

    }

    @Override
    public void onException(int apiId, String msg) {


    }

    @Override
    public void onSelected(CasteResponse.CasteData casteData) {

    }

    @Override
    public void onSelected(SubCasteResponse.SubCasteData casteData) {

    }


    private void updateData() {
        if (isValid()) {


            basicInfoBinding.image.setText(imageFragment.getmProfileImage());

            AppLog.e("basic data", "basic sending Data " + new Gson().toJson(addClassViewModel.studentDataMutableLiveData.getValue()));
            leafManager.editClassStudent(this, group_id, team_id, studentData.getUserId(), gruppieRollNoNumber, addClassViewModel.studentDataMutableLiveData.getValue());


        }
    }


}
