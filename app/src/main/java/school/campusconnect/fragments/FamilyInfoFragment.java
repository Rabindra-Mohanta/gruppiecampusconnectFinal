package school.campusconnect.fragments;

import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;


import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;

import school.campusconnect.AddClassViewModel;
import school.campusconnect.R;
import school.campusconnect.activities.AddClassStudentActivity;
import school.campusconnect.databinding.FragmentFamilyInfoBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.student.StudentRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.UploadCircleImageFragment;

public class FamilyInfoFragment extends BaseFragment implements LeafManager.OnCommunicationListener {

    FragmentFamilyInfoBinding familyInfoBinding;
    AddClassViewModel addClassViewModel;
    String group_id, team_id;
    private boolean isEdit = false;
    StudentRes.StudentData studentData;
    public int currentCountry;
    LeafManager leafManager;
    String currentPhoneNo;
    private boolean onceClick = true;

    private UploadCircleImageFragment imageFragment;

    public FamilyInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        familyInfoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_family_info, container, false);

        currentPhoneNo = BasicInfoFragment.currentPhoneNo;
        init();
        addClassViewModel = new ViewModelProvider(this).get(AddClassViewModel.class);
        addClassViewModel.setData(BasicInfoFragment.addClassViewModel.studentDataMutableLiveData);
        familyInfoBinding.setLifecycleOwner(this);
        familyInfoBinding.setMyStudentData(addClassViewModel);


        currentCountry = 1;


        return familyInfoBinding.getRoot();

    }

    private void init() {

        studentData = AddClassStudentActivity.studentData;


        group_id = AddClassStudentActivity.group_id;
        team_id = AddClassStudentActivity.team_id;


        leafManager = new LeafManager();
        isEdit = AddClassStudentActivity.isEdit;
        imageFragment = AddClassStudentActivity.imageFragment;


        familyInfoBinding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                updateData();


            }
        });


    }

    private boolean isValid() {

        if (!isValueValidPhone(familyInfoBinding.etfatherNumber) || !isValueValidPhone(familyInfoBinding.etmotherNumber) || !isValueValid(familyInfoBinding.etfatherNumber) || !isValueValid(familyInfoBinding.etmotherNumber)) {
            return false;
        }

        return true;


    }

    private void updateData() {









                String phone = "";


                phone = addClassViewModel.studentDataMutableLiveData.getValue().getPhone();

            if(currentPhoneNo.length()<10 || phone.length()<10)
            {
                Toast.makeText(getContext(),"Enter valid phone number In Basic Info",Toast.LENGTH_LONG).show();
            }
               else if (currentPhoneNo.equals(phone)) {


                } else {


                        familyInfoBinding.progressBar.setVisibility(View.VISIBLE);
                        StudentRes.StudentData addStudentReq = new StudentRes.StudentData();
                        String[] str = getResources().getStringArray(R.array.array_country_values);
                        addStudentReq.countryCode = str[currentCountry - 1];
                        addStudentReq.phone = addClassViewModel.studentDataMutableLiveData.getValue().getPhone();

                        //progressBar.setVisibility(View.VISIBLE);
                        leafManager.editClassStudentPhone(this, group_id, studentData.getUserId(),team_id, addStudentReq);

                }

            if(currentPhoneNo.length()<10 || phone.length()<10)
            {

            }
            else
            {

                familyInfoBinding.image.setText(imageFragment.getmProfileImage());

                AppLog.e("family data", "family sending Data " + new Gson().toJson(addClassViewModel.studentDataMutableLiveData.getValue()));

                familyInfoBinding.progressBar.setVisibility(View.VISIBLE);
                leafManager.editClassStudent(this, group_id, team_id, studentData.getUserId(), studentData.gruppieRollNumber, addClassViewModel.studentDataMutableLiveData.getValue());

            }




    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        switch (apiId) {

            case LeafManager.API_EDIT_STUDENTS:
                familyInfoBinding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), getResources().getString(R.string.toast_edit_student_sucess), Toast.LENGTH_SHORT).show();
                getActivity().finish();
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        Toast.makeText(getContext(), "something went wrong try again", Toast.LENGTH_SHORT).show();
        familyInfoBinding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        Toast.makeText(getContext(), "something went wrong try again", Toast.LENGTH_SHORT).show();
        familyInfoBinding.progressBar.setVisibility(View.GONE);
    }
}