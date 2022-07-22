package school.campusconnect.fragments;

import android.os.Bundle;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Toast;


import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;

import java.util.ArrayList;


import school.campusconnect.AddClassViewModel;
import school.campusconnect.R;

import school.campusconnect.activities.AddClassStudentActivity;

import school.campusconnect.databinding.FragmentOtherInfoBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.profileCaste.CasteResponse;

import school.campusconnect.datamodel.profileCaste.ReligionResponse;
import school.campusconnect.datamodel.profileCaste.SubCasteResponse;
import school.campusconnect.datamodel.student.StudentRes;
import school.campusconnect.network.LeafManager;

import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.UploadCircleImageFragment;


public class OtherInfoFragment extends BaseFragment implements LeafManager.OnCommunicationListener, SearchCastFragmentDialog.SelectListener, SearchSubCasteDialogFragment.SelectListener {

    private boolean onceClick=true;
    AddClassViewModel addClassViewModel;
    FragmentOtherInfoBinding otherInfoBinding;
    String currentPhoneNo;
    String ReligionData;
    String CastData;
    String SubCastData;
    String CategoryData;

    public int currentCountry;

    String religion;
    String caste;
    String subcaste;

    String group_id, team_id;
    private boolean isEdit = false;

    StudentRes.StudentData studentData;

    boolean submitted = false;
    LeafManager leafManager;
    private UploadCircleImageFragment imageFragment;

    ArrayAdapter<String> religionAdapter;
    ArrayList<String> religionList = new ArrayList<>();
    ArrayList<String> casteList = new ArrayList<>();
    ArrayList<String> subCasteList = new ArrayList<>();
    ArrayList<CasteResponse.CasteData> casteDataList = new ArrayList<>();

    boolean isFirstTimeReligion = true;
    boolean isFirstTimeCaste = true;
    boolean isFirstTimeSubCaste = true;

    String casteId = null;


    SearchSubCasteDialogFragment searchSubCasteDialogFragment;
    SearchCastFragmentDialog searchCastFragmentDialog;
    public boolean isCasteClickable = false;


    public OtherInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        otherInfoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_other_info, container, false);
        init();
        addClassViewModel = new ViewModelProvider(this).get(AddClassViewModel.class);
        addClassViewModel.setData(BasicInfoFragment.addClassViewModel.studentDataMutableLiveData);
        otherInfoBinding.setLifecycleOwner(this);
        otherInfoBinding.setMyStudent(addClassViewModel);


        currentCountry = 1;


        return otherInfoBinding.getRoot();
    }

    private void init() {
        studentData = AddClassStudentActivity.studentData;
        currentPhoneNo = studentData.phone;

        group_id = AddClassStudentActivity.group_id;
        team_id = AddClassStudentActivity.team_id;
        imageFragment = AddClassStudentActivity.imageFragment;

        leafManager = new LeafManager();
        searchCastFragmentDialog = SearchCastFragmentDialog.newInstance();
        searchCastFragmentDialog.setListener(this);
        Log.e(TAG, "student Data1" + new Gson().toJson(studentData));
        searchSubCasteDialogFragment = SearchSubCasteDialogFragment.newInstance();
        searchSubCasteDialogFragment.setListener(this);

        String[] BloodArray = getResources().getStringArray(R.array.blood_group);
        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_spinner, R.id.tvItem, BloodArray);
        otherInfoBinding.etBlood.setAdapter(bloodAdapter);

        String[] DisabilityArray = getResources().getStringArray(R.array.disability);
        ArrayAdapter<String> DisabilityAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_spinner, R.id.tvItem, DisabilityArray);
        otherInfoBinding.etDisability.setAdapter(DisabilityAdapter);
        String[] GenderArray = getResources().getStringArray(R.array.gender_array);
        ArrayAdapter<String> GenderAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_spinner, R.id.tvItem, GenderArray);
        otherInfoBinding.etGender.setAdapter(GenderAdapter);


        religion = studentData.getReligion();
        caste = studentData.caste;
        subcaste = studentData.getSubCaste();


        for (int i = 0; i < BloodArray.length; i++) {
            if (BloodArray[i].equals(studentData.bloodGroup)) {
                otherInfoBinding.etBlood.setSelection(i);
                break;
            }
        }

        for (int i = 0; i < GenderArray.length; i++) {
            if (GenderArray[i].equals(studentData.gender)) {
                otherInfoBinding.etGender.setSelection(i);
                break;
            }
        }

        for (int i = 0; i < DisabilityArray.length; i++) {
            if (DisabilityArray[i].equals(studentData.getDisability())) {
                otherInfoBinding.etDisability.setSelection(i);
                break;
            }
        }


        otherInfoBinding.etBlood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                otherInfoBinding.blood.setText(otherInfoBinding.etBlood.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        otherInfoBinding.etGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                otherInfoBinding.gender.setText(otherInfoBinding.etGender.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        otherInfoBinding.etDisability.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                otherInfoBinding.disability.setText(otherInfoBinding.etDisability.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        otherInfoBinding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onceClick)

                    UpdateData();


            }
        });

        leafManager.getReligion(this);


        otherInfoBinding.etReligion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (position != 0) {
                    isCasteClickable = true;
                    otherInfoBinding.religion.setText(otherInfoBinding.etReligion.getSelectedItem().toString());
                    getCaste();
                } else {
                    isCasteClickable = false;
                    otherInfoBinding.etCaste.setText("");
                    otherInfoBinding.etSubCaste.setText("");
                    otherInfoBinding.etCategory.setText("");
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        otherInfoBinding.etCaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isCasteClickable) {

                    searchCastFragmentDialog.show(getActivity().getSupportFragmentManager(), "");
                }
            }
        });

        otherInfoBinding.etSubCaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isCasteClickable) {
                    searchSubCasteDialogFragment.show(getActivity().getSupportFragmentManager(), "");
                }

            }
        });

    }

    private void getCaste() {
        leafManager.getCaste(this, otherInfoBinding.etReligion.getSelectedItem().toString());
    }

    private void UpdateData() {


        String phone = "";


            phone = addClassViewModel.studentDataMutableLiveData.getValue().getPhone();


        if(currentPhoneNo.length()<10 || phone.length()<10)
        {
            Toast.makeText(getContext(),"Enter valid phone number In Basic Info",Toast.LENGTH_LONG).show();
        }
       else if (currentPhoneNo.equals(phone)) {


        }


        else {





                  otherInfoBinding.progressBar.setVisibility(View.VISIBLE);

            StudentRes.StudentData addStudentReq = new StudentRes.StudentData();
            String[] str = getResources().getStringArray(R.array.array_country_values);
            addStudentReq.countryCode = str[currentCountry - 1];
            addStudentReq.phone = addClassViewModel.studentDataMutableLiveData.getValue().getPhone();

            //progressBar.setVisibility(View.VISIBLE);
            leafManager.editClassStudentPhone(this, group_id, studentData.getUserId(), addStudentReq);
            }



        if(currentPhoneNo.length()<10 || phone.length()<10)
        {

        }
else
        {

            otherInfoBinding.image.setText(imageFragment.getmProfileImage());
            AppLog.e("other data", "other sending Data " + new Gson().toJson(addClassViewModel.studentDataMutableLiveData.getValue()));

            otherInfoBinding.progressBar.setVisibility(View.VISIBLE);
            leafManager.editClassStudent(this, group_id, team_id, studentData.getUserId(), studentData.gruppieRollNumber, addClassViewModel.studentDataMutableLiveData.getValue());


        }





    }


    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        switch (apiId) {
            case LeafManager.API_RELIGION_GET: {
                ReligionResponse res = (ReligionResponse) response;
                otherInfoBinding.progressBar.setVisibility(View.GONE);
                AppLog.e(TAG, "ReligionResponse" + res);

                religionList.clear();
                religionList.add(0, "select religion");
                religionList.addAll(res.getReligionData().get(0).getReligionList());

                if (res.getReligionData().size() > 0) {
                    if (getActivity()!=null) {
                        religionAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item_spinner, R.id.tvItem, religionList);
                    }
                    otherInfoBinding.etReligion.setAdapter(religionAdapter);
                    otherInfoBinding.etReligion.setEnabled(true);
                }
                if (isFirstTimeReligion) {
                    isFirstTimeReligion = false;
                    otherInfoBinding.etReligion.setEnabled(true);

                    if (religion != null) {
                        otherInfoBinding.etReligion.setSelection(religionAdapter.getPosition(religion));

                        ReligionData = String.valueOf(religionAdapter.getPosition(religion));
                    } else {
                        otherInfoBinding.etReligion.setSelection(religionAdapter.getPosition("select religion"));

                    }
                }

                break;
            }

            case LeafManager.API_CASTE_GET: {
                CasteResponse res = (CasteResponse) response;
                otherInfoBinding.progressBar.setVisibility(View.GONE);

                AppLog.e(TAG, "CasteResponse" + res);

           /* casteDataList.clear();
            casteDataList.addAll(res.getCasteData());*/

                casteDataList.clear();
                casteDataList.addAll(res.getCasteData());
                casteList.clear();

                for (int i = 0; i < res.getCasteData().size(); i++) {
                    casteList.add(res.getCasteData().get(i).getCasteName());
                }

                if (casteList.size() > 0) {
                    if (isFirstTimeCaste) {
                        if (caste != null) {
                            otherInfoBinding.etCaste.setText(caste);


                            CastData = caste;
                        } else {
                            otherInfoBinding.etCaste.setText(res.getCasteData().get(0).getCasteName());

                            CastData = res.getCasteData().get(0).getCasteName();
                        }
                        for (int i = 0; i < casteDataList.size(); i++) {
                            if (otherInfoBinding.etCaste.getText().toString().toLowerCase().trim().equalsIgnoreCase(casteDataList.get(i).getCasteName().toLowerCase().trim())) {
                                casteId = casteDataList.get(i).getCasteId();
                                otherInfoBinding.etCategory.setText(casteDataList.get(i).getCategoryName());

                                CategoryData = casteDataList.get(i).getCategoryName();
                            }
                        }

                        otherInfoBinding.etCaste.setTextColor(getResources().getColor(R.color.white));
                        otherInfoBinding.etCaste.setEnabled(true);

                        isFirstTimeCaste = false;

                    } else {
                        casteId = res.getCasteData().get(0).getCasteId();
                        otherInfoBinding.etCaste.setText(res.getCasteData().get(0).getCasteName());

                        otherInfoBinding.etCategory.setText(res.getCasteData().get(0).getCategoryName());

                        CategoryData = casteDataList.get(0).getCategoryName();
                    }
                }

                if (casteId != null) {

                    leafManager.getSubCaste(this, casteId);
                }

                searchCastFragmentDialog.setData(res.getCasteData());

                break;
            }

            case LeafManager.API_SUB_CASTE_GET: {
                SubCasteResponse res = (SubCasteResponse) response;
                otherInfoBinding.progressBar.setVisibility(View.GONE);

                AppLog.e(TAG, "SubCasteResponse" + res);

                casteId = null;

                subCasteList.clear();

                for (int i = 0; i < res.getSubCasteData().size(); i++) {
                    subCasteList.add(res.getSubCasteData().get(i).getSubCasteName());
                }


                if (subCasteList.size() > 0) {

                    if (isFirstTimeSubCaste) {
                        otherInfoBinding.etSubCaste.setTextColor(getResources().getColor(R.color.grey));

                        if (subcaste != null) {
                            otherInfoBinding.etSubCaste.setText(subcaste);
                            SubCastData = subcaste;
                        } else {
                            otherInfoBinding.etSubCaste.setText(res.getSubCasteData().get(0).getSubCasteName());
                            SubCastData = res.getSubCasteData().get(0).getSubCasteName();
                        }

                        isFirstTimeSubCaste = false;
                        otherInfoBinding.etSubCaste.setTextColor(getResources().getColor(R.color.white));
                        otherInfoBinding.etSubCaste.setEnabled(true);
                    } else {
                        otherInfoBinding.etSubCaste.setText(res.getSubCasteData().get(0).getSubCasteName());
                        SubCastData = res.getSubCasteData().get(0).getSubCasteName();
                    }

                }
                searchSubCasteDialogFragment.setData(res.getSubCasteData());
                break;
            }
            case LeafManager.API_EDIT_STUDENTS:
                Toast.makeText(getContext(), getResources().getString(R.string.toast_edit_student_sucess), Toast.LENGTH_SHORT).show();
                getActivity().finish();
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        otherInfoBinding.progressBar.setVisibility(View.GONE);
        Toast.makeText(getContext(), "something went wrong try again", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onException(int apiId, String msg) {
        otherInfoBinding.progressBar.setVisibility(View.GONE);
        Toast.makeText(getContext(), "something went wrong try again", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onSelected(CasteResponse.CasteData casteData) {
        otherInfoBinding.etCaste.setText(casteData.getCasteName());
        otherInfoBinding.etCategory.setText(casteData.getCategoryName());

        casteId = casteData.getCasteId();

        if (casteId != null) {

            leafManager.getSubCaste(this, casteId);
        }
    }

    @Override
    public void onSelected(SubCasteResponse.SubCasteData casteData) {
        otherInfoBinding.etSubCaste.setText(casteData.getSubCasteName());
    }


}

