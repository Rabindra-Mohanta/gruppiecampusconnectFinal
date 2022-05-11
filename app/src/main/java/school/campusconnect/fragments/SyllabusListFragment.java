package school.campusconnect.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import school.campusconnect.R;
import school.campusconnect.utils.BaseFragment;

public class SyllabusListFragment extends BaseFragment {


    public static SyllabusListFragment newInstance() {
        SyllabusListFragment fragment = new SyllabusListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_syllabus_list, container, false);
    }
}