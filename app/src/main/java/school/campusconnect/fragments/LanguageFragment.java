package school.campusconnect.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;


public class LanguageFragment extends Fragment {

    @Bind(R.id.rd_kannada)
    RadioButton rdKannada;
    @Bind(R.id.rd_english)
    RadioButton rdEnglish2;
    @Bind(R.id.btn_update)
    Button btnUpdate;

    public LanguageFragment() {
        // Required empty public constructor
    }

    public static LanguageFragment newInstance() {
        return new LanguageFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_language, container, false);

        ButterKnife.bind(this,view);
        return view;
    }

    @OnClick(R.id.btn_update)
    public void onClick(View v){
        Toast.makeText(getActivity(),getResources().getString(R.string.toast_update_successfully),Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
