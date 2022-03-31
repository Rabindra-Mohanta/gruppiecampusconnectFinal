package school.campusconnect.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.databinding.FragmentSearchCastDialogBinding;
import school.campusconnect.databinding.FragmentSearchIssueDialogBinding;
import school.campusconnect.datamodel.issue.IssueListResponse;
import school.campusconnect.datamodel.profileCaste.CasteResponse;
import school.campusconnect.views.SearchIssueFragmentDialog;


public class SearchCastFragmentDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "SearchCastFragmentDialog";
    FragmentSearchCastDialogBinding binding;
    SelectListener listener;
    Context context;
    private ArrayList<CasteResponse.CasteData> casteData;
    private Adapter adapter;
    ArrayList<CasteResponse.CasteData> filteredList;

    public SearchCastFragmentDialog() {
        // Required empty public constructor
    }

    public static SearchCastFragmentDialog newInstance() {
        SearchCastFragmentDialog fragment = new SearchCastFragmentDialog();
        return fragment;
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //setStyle(DialogFragment.STYLE_NORMAL, R.style.dialog_theme);
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_search_cast_dialog, container, false);
        return binding.getRoot();
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inits();
    }

    private void inits() {


        binding.imgClose.setOnClickListener(this);
        adapter = new Adapter();
        binding.rvSearch.setAdapter(adapter);
        adapter.add(casteData);



        binding.edtSearch.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                if (s.toString().length() > 2)
                {
                    searchData(s.toString());
                }
                else
                {
                    if (binding.rvSearch.getVisibility() == View.GONE)
                    {
                        binding.tvIssue.setVisibility(View.GONE);
                        binding.rvSearch.setVisibility(View.VISIBLE);
                    }

                    adapter.add(casteData);
                }

            }
        });
    }

    private void searchData(String text) {

        filteredList = new ArrayList<>();

        for(CasteResponse.CasteData item :casteData ){

            if (item.getCasteName().toLowerCase().contains(text.toLowerCase()) && item.getCasteName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }

        if (filteredList.size()>0)
        {
            binding.rvSearch.setVisibility(View.VISIBLE);
            binding.tvIssue.setVisibility(View.GONE);
        }
        else
        {
            binding.rvSearch.setVisibility(View.GONE);
            binding.tvIssue.setVisibility(View.VISIBLE);
        }
        adapter.add(filteredList);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnSave:
                dismiss();
                break;

            case R.id.imgClose:
                binding.edtSearch.getText().clear();
                filteredList = new ArrayList<>();
                adapter.add(filteredList);
                dismiss();
                break;
        }
    }


    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>
    {
        ArrayList<CasteResponse.CasteData> list;
        private Context mContext;


        public void add(ArrayList<CasteResponse.CasteData> list)
        {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_issue,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final CasteResponse.CasteData item = list.get(position);

            holder.txt_issue.setText(item.getCasteName());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {


            @Bind(R.id.txt_issue)
            TextView txt_issue;


            public ViewHolder(View itemView) {
                super(itemView);

                ButterKnife.bind(this,itemView);


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onTreeClick(list.get(getAdapterPosition()));
                    }
                });

            }
        }
    }


    private void onTreeClick(CasteResponse.CasteData issueData) {

        binding.edtSearch.getText().clear();
        filteredList = new ArrayList<>();
        adapter.add(filteredList);

        listener.onSelected(issueData);
        dismiss();
        /*Intent intent = new Intent(getActivity(), BoothStudentActivity.class);
        intent.putExtra("class_data",new Gson().toJson(classData));
        intent.putExtra("committee_data",new Gson().toJson(committeeData));
        intent.putExtra("title",committeeData.getCommitteeName());
        startActivity(intent);*/

    }

    public void setData(ArrayList<CasteResponse.CasteData> data) {
        this.casteData = data;
    }
    public void setListener(SelectListener listener) {
        this.listener = listener;
    }
    public interface SelectListener {

        public void onSelected(CasteResponse.CasteData casteData);
    }
}