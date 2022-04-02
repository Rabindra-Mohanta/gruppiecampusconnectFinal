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

public class ProfileOtherFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.country)
    public DrawableEditText edtCountry;

    //    @Bind(R.id.roll)
//    public DrawableEditText edtRoll;

    @Bind(R.id.address_one)
    public DrawableEditText edtAddressOne;

    @Bind(R.id.address_two)
    public DrawableEditText edtAddressTwo;

    @Bind(R.id.city)
    public DrawableEditText edtCity;

    @Bind(R.id.state)
    public DrawableEditText edtState;

    @Bind(R.id.pincode)
    public DrawableEditText edtPincode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

       AppLog.e("OnCreateView", "ProfileOtherFragment called");

        View view = inflater.inflate(R.layout.fragment_profile_other, container, false);
        ButterKnife.bind(this, view);
        edtCountry.editText.setFocusable(false);
        edtPincode.editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
//        edtCountry.editText.setOnClickListener(this);
        edtCountry.editText.setClickable(true);
        edtCountry.editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SMBDialogUtils.showSMBSingleChoiceDialog(getActivity(), R.string.title_country, R.array.array_country, 0,
                        new SMBDailogClickListener(R.id.country));
            }
        });

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
                /*case R.id.gender:
                    edtGender.editText.setText(lw.getAdapter().getItem(lw.getCheckedItemPosition()).toString());
                    gender = lw.getCheckedItemPosition() + 1;
                    break;*/
//                case R.id.roll:
//                    edtRoll.editText.setText(lw.getAdapter().getItem(lw.getCheckedItemPosition()).toString());
//                    break;
                case R.id.city:
                    edtCity.editText.setText(lw.getAdapter().getItem(lw.getCheckedItemPosition()).toString());
                    break;
                case R.id.state:
                    edtState.editText.setText(lw.getAdapter().getItem(lw.getCheckedItemPosition()).toString());
                    break;
                case R.id.country:
                    edtCountry.editText.setText(lw.getAdapter().getItem(lw.getCheckedItemPosition()).toString());
                    break;


            }
        }

        public SMBDailogClickListener(int id) {
            DIALOG_ID = id;
        }
    }

    public void fillDetails(ProfileItem item) {

        if(item.address==null)
            return;

        if (item.address.line1 != null)
        {
            edtAddressOne.editText.setText(item.address.line1);
        }
        if (item.address.line2 != null) {
            edtAddressTwo.editText.setText(item.address.line2);
        }
        edtCity.editText.setText(item.address.district);
        edtState.editText.setText(item.address.state);
        edtPincode.editText.setText(item.address.pin);
        edtCountry.editText.setText(item.address.country);
    }

}
