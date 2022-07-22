package school.campusconnect.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.text.InputFilter;
import school.campusconnect.utils.AppLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.ProfileActivity2;
import school.campusconnect.datamodel.profile.ProfileItem;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.views.DrawableEditText;
import school.campusconnect.views.SMBDialogUtils;

/**
 * Created by frenzin04 on 7/14/2017.
 */

public class ProfileBasicFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.lead_name)
    public DrawableEditText edtName;

    @Bind(R.id.phoneNumber)
    public DrawableEditText edtPhone;

    @Bind(R.id.gender)
    public DrawableEditText edtGender;

    @Bind(R.id.dob)
    public DrawableEditText edtDob;

    @Bind(R.id.email)
    public DrawableEditText edtEmail;

    @Bind(R.id.occupation)
    public DrawableEditText edtOccupation;

    @Bind(R.id.qualification)
    public DrawableEditText edtQualification;

    @Bind(R.id.address)
    public DrawableEditText address;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       AppLog.e("OnCreateView", "ProfileBasicFragment called");
        View view = inflater.inflate(R.layout.fragment_profile_basic, container, false);
        ButterKnife.bind(this, view);
        edtPhone.editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        edtGender.editText.setFocusable(false);
        edtPhone.editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
        edtPhone.editText.setFocusable(false);
        //edtState.editText.setOnClickListener(this);
        //edtCity.editText.setOnClickListener(this);
        //edtRoll.editText.setOnClickListener(this);
//        edtGender.editText.setOnClickListener(this);
//        edtDob.editText.setOnClickListener(this);
        edtDob.editText.setFocusable(false);
        edtDob.editText.setClickable(true);

        edtGender.editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((ProfileActivity2) getActivity()).gender > 0) {
                    SMBDialogUtils.showSMBSingleChoiceDialog(getActivity(), R.string.title_gender, R.array.array_gender, (((ProfileActivity2) getActivity()).gender) - 1,
                            new SMBDailogClickListener(R.id.gender));
                } else {
                    SMBDialogUtils.showSMBSingleChoiceDialog(getActivity(), R.string.title_gender, R.array.array_gender, ((ProfileActivity2) getActivity()).gender,
                            new SMBDailogClickListener(R.id.gender));
                }
            }
        });

        edtDob.editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment fragment = DatePickerFragment.newInstance();
                fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                    @Override
                    public void onDateSelected(Calendar c) {
                        SimpleDateFormat format = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
                        edtDob.editText.setText(format.format(c.getTime()));
                    }
                });
                fragment.show(getChildFragmentManager(), "datepicker");
            }
        });

       /* edtPhone.editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChangeNumberActivity.class);
                startActivity(intent);
            }
        });*/
        fillDetails(((ProfileActivity2) getActivity()).item);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (((View) view.getParent().getParent().getParent()).getId()) {


            case R.id.gender:
                if (((ProfileActivity2) getActivity()).gender > 0) {
                    SMBDialogUtils.showSMBSingleChoiceDialog(getActivity(), R.string.title_gender, R.array.array_gender, (((ProfileActivity2) getActivity()).gender) - 1,
                            new SMBDailogClickListener(R.id.gender));
                } else {
                    SMBDialogUtils.showSMBSingleChoiceDialog(getActivity(), R.string.title_gender, R.array.array_gender, ((ProfileActivity2) getActivity()).gender,
                            new SMBDailogClickListener(R.id.gender));
                }

                break;
            case R.id.roll:
                SMBDialogUtils.showSMBSingleChoiceDialog(getActivity(), R.string.title_roll, R.array.array_roll, ((ProfileActivity2) getActivity()).roll,
                        new SMBDailogClickListener(R.id.roll));
                break;
           /* case R.id.city:
                SMBDialogUtils.showSMBSingleChoiceDialog(this, R.string.title_city, R.array.array_city, city,
                        new SMBDailogClickListener(R.id.city));
                break;*/

            case R.id.dob:
                DatePickerFragment fragment = DatePickerFragment.newInstance();
                fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                    @Override
                    public void onDateSelected(Calendar c) {
                        SimpleDateFormat format = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
                        edtDob.editText.setText(format.format(c.getTime()));
                    }
                });
                fragment.show(getChildFragmentManager(), "datepicker");
                break;
            case R.id.state:
               /* SMBDialogUtils.showSMBSingleChoiceDialog(this, R.string.title_state, R.array.array_state, state,
                        new SMBDailogClickListener(R.id.state));*/
                break;/* SMBDialogUtils.showSMBSingleChoiceDialog(this, R.string.title_state, R.array.array_state, state,
                        new SMBDailogClickListener(R.id.state));*/
            case R.id.country:
                SMBDialogUtils.showSMBSingleChoiceDialog(getActivity(), R.string.title_country, R.array.array_country, 0,
                        new SMBDailogClickListener(R.id.country));
                break;
        }
    }

    public class SMBDailogClickListener implements DialogInterface.OnClickListener {
        private int DIALOG_ID = -1;

        @Override
        public void onClick(DialogInterface dialog, int which) {

            ListView lw = ((AlertDialog) dialog).getListView();


            switch (DIALOG_ID) {
                case R.id.gender:
                    edtGender.editText.setText(lw.getAdapter().getItem(lw.getCheckedItemPosition()).toString());
                    ((ProfileActivity2) getActivity()).gender = lw.getCheckedItemPosition() + 1;
                    break;
//                case R.id.roll:
//                    edtRoll.editText.setText(lw.getAdapter().getItem(lw.getCheckedItemPosition()).toString());
//                    break;
                /*case R.id.city:
                    edtCity.editText.setText(lw.getAdapter().getItem(lw.getCheckedItemPosition()).toString());
                    break;
                case R.id.state:
                    edtState.editText.setText(lw.getAdapter().getItem(lw.getCheckedItemPosition()).toString());
                    break;
                case R.id.countryCode:
                    edtCountry.editText.setText(lw.getAdapter().getItem(lw.getCheckedItemPosition()).toString());
                    break;*/


            }
        }

        public SMBDailogClickListener(int id) {
            DIALOG_ID = id;
        }
    }

    public void fillDetails(ProfileItem item) {
        if(item.name != null){
            edtName.editText.setText(item.name);
        }

        edtPhone.editText.setText(item.phone);
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat format1 = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
        try {
            edtDob.editText.setText(format1.format(format.parse(item.dob)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        edtEmail.editText.setText(item.email);
        edtQualification.editText.setText(item.qualification);
        edtOccupation.editText.setText(item.occupation);
        if (item.gender != null && !item.gender.isEmpty()) {
            if (item.gender.equalsIgnoreCase("Male")) {
                edtGender.editText.setText("male");
            } else {
                edtGender.editText.setText("female");
            }
        }

    }

}
