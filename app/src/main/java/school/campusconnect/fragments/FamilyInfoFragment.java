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
import school.campusconnect.utils.UploadImageFragment;

public class FamilyInfoFragment extends BaseFragment implements LeafManager.OnCommunicationListener {





    FragmentFamilyInfoBinding familyInfoBinding;
    AddClassViewModel addClassViewModel;
    String group_id, team_id;
    private boolean isEdit=false;
    StudentRes.StudentData studentData;

    LeafManager leafManager;

    private UploadImageFragment imageFragment;

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

        familyInfoBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_family_info,container,false);
        init();
        addClassViewModel=new ViewModelProvider(this).get(AddClassViewModel.class);
        addClassViewModel.setData(BasicInfoFragment.addClassViewModel.studentDataMutableLiveData);
        familyInfoBinding.setMyStudentData(addClassViewModel);
        familyInfoBinding.setLifecycleOwner(this);

        return familyInfoBinding.getRoot();

    }

    private void init() {

        studentData=AddClassStudentActivity.studentData;
        leafManager=new LeafManager();
        group_id=AddClassStudentActivity.group_id;
        team_id=AddClassStudentActivity.team_id;
        isEdit=AddClassStudentActivity.isEdit;
        imageFragment = AddClassStudentActivity.imageFragment;






            familyInfoBinding.btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateData();
                }
            });







    }

    private void updateData() {





        familyInfoBinding.image.setText(imageFragment.getmProfileImage());

        AppLog.e("family data", "family sending Data " + new Gson().toJson(addClassViewModel.studentDataMutableLiveData.getValue()));

        leafManager.editClassStudent(this, group_id, team_id,studentData.getUserId(),studentData.gruppieRollNumber, addClassViewModel.studentDataMutableLiveData.getValue());



    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        switch (apiId)
        {

            case LeafManager.API_EDIT_STUDENTS:
                Toast.makeText(getContext(), getResources().getString(R.string.toast_edit_student_sucess), Toast.LENGTH_SHORT).show();
                getActivity(). finish();
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {

    }

    @Override
    public void onException(int apiId, String msg) {

    }
}