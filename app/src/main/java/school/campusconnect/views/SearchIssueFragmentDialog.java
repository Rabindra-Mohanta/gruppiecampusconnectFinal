package school.campusconnect.views;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.BoothStudentActivity;
import school.campusconnect.databinding.FragmentSearchIssueDialogBinding;
import school.campusconnect.datamodel.committee.committeeResponse;
import school.campusconnect.datamodel.issue.IssueListResponse;
import school.campusconnect.fragments.CommitteeListFragment;
import school.campusconnect.utils.ImageUtil;


public class SearchIssueFragmentDialog extends DialogFragment implements View.OnClickListener{


    private static final String TAG = "SearchIssueFragmentDialog";
    FragmentSearchIssueDialogBinding binding;
    SelectListener listener;
    Context context;
    private ArrayList<IssueListResponse.IssueData> issueData;
    private Adapter adapter;
    ArrayList<IssueListResponse.IssueData> filteredList;
    public static SearchIssueFragmentDialog newInstance()
    {
        return new SearchIssueFragmentDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_search_issue_dialog, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inits();
    }

    private void inits() {

      //  binding.btnSave.setOnClickListener(this);
        binding.imgClose.setOnClickListener(this);

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
                    if (binding.tvIssue.getVisibility() == View.GONE)
                    {
                        binding.tvIssue.setVisibility(View.VISIBLE);
                    }
                    binding.tvIssue.setText(getResources().getString(R.string.hint_issue_not_found));
                    filteredList = new ArrayList<>();
                    adapter = new Adapter(filteredList);
                    binding.rvSearchIssue.setAdapter(adapter);
                }
            }
        });
    }

    private void searchData(String text) {

        filteredList = new ArrayList<>();

        for(IssueListResponse.IssueData item :issueData ){

            if (item.issue.toLowerCase().contains(text.toLowerCase()) && item.issue.toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }

        if (filteredList.size()>0)
        {
         binding.tvIssue.setVisibility(View.GONE);
        }
        else
        {
            binding.tvIssue.setVisibility(View.VISIBLE);
        }
        adapter = new Adapter(filteredList);
        binding.rvSearchIssue.setAdapter(adapter);
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
                adapter = new Adapter(filteredList);
                binding.rvSearchIssue.setAdapter(adapter);
                dismiss();
                break;
        }
    }


    public void setData(ArrayList<IssueListResponse.IssueData> data) {
        this.issueData = data;
    }
    public void setListener(SelectListener listener) {
        this.listener = listener;
    }
    public interface SelectListener {

        public void onSelected(String Issue,String IssueID);
    }


    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>
    {
        ArrayList<IssueListResponse.IssueData> list;
        private Context mContext;

        public Adapter(ArrayList<IssueListResponse.IssueData> list) {
            this.list = list;
        }

        @Override
        public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_issue,parent,false);
            return new Adapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final Adapter.ViewHolder holder, final int position) {

            final IssueListResponse.IssueData item = list.get(position);

            holder.txt_issue.setText(item.issue);
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

    private void onTreeClick(IssueListResponse.IssueData issueData) {

        binding.edtSearch.getText().clear();
        filteredList = new ArrayList<>();
        adapter = new Adapter(filteredList);
        binding.rvSearchIssue.setAdapter(adapter);

        listener.onSelected(issueData.issue,issueData.issueId);
        dismiss();
        /*Intent intent = new Intent(getActivity(), BoothStudentActivity.class);
        intent.putExtra("class_data",new Gson().toJson(classData));
        intent.putExtra("committee_data",new Gson().toJson(committeeData));
        intent.putExtra("title",committeeData.getCommitteeName());
        startActivity(intent);*/

    }
}