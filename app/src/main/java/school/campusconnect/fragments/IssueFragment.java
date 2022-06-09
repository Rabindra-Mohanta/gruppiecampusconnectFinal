package school.campusconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.EditIssueActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.SubjectActivity2;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.issue.IssueListResponse;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class IssueFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    @Bind(R.id.etSearch)
    public EditText etSearch;

    private ArrayList<IssueListResponse.IssueData> result;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss_search,container,false);
        ButterKnife.bind(this,view);
        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        init();
        showLoadingBar(progressBar,false);
   //     progressBar.setVisibility(View.VISIBLE);

        return view;
    }

    private void init() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(result!=null){
                    if(!TextUtils.isEmpty(s.toString())){
                        ArrayList<IssueListResponse.IssueData> newList = new ArrayList<>();
                        for (int i=0;i<result.size();i++){
                            if((result.get(i).issue+""+result.get(i).jurisdiction).toLowerCase().contains(s.toString().toLowerCase())){
                                newList.add(result.get(i));
                            }
                        }
                        rvClass.setAdapter(new ClassesAdapter(newList));
                    }else {
                        rvClass.setAdapter(new ClassesAdapter(result));
                    }
                }
            }
        });
    }
    public void showHideSearch() {
        if (etSearch.getVisibility() == View.VISIBLE) {
            etSearch.setVisibility(View.GONE);
        } else {
            etSearch.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        LeafManager leafManager = new LeafManager();
        leafManager.getIssues(this,GroupDashboardActivityNew.groupId);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
        //    progressBar.setVisibility(View.GONE);
        IssueListResponse res = (IssueListResponse) response;
        result = res.getData();
        AppLog.e(TAG, "ClassResponse " + result);

        rvClass.setAdapter(new ClassesAdapter(result));
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        //    progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        //    progressBar.setVisibility(View.GONE);
    }

    public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder>
    {
        List<IssueListResponse.IssueData> list;
        private Context mContext;

        public ClassesAdapter(List<IssueListResponse.IssueData> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_issue,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final IssueListResponse.IssueData item = list.get(position);
            holder.txt_name.setText(item.issue+" - "+item.jurisdiction);
        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    txtEmpty.setText(getResources().getString(R.string.txt_no_issue_found));
                }
                else {
                    txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                txtEmpty.setText(getResources().getString(R.string.txt_no_issue_found));
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.txt_name)
            TextView txt_name;

            @Bind(R.id.img_tree)
            ImageView img_tree;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onTreeClick(list.get(getAdapterPosition()));
                    }
                });
                img_tree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onTreeClick(list.get(getAdapterPosition()));
                    }
                });

            }
        }
    }

    private void onTreeClick(IssueListResponse.IssueData classData) {
        Intent intent = new Intent(getActivity(), EditIssueActivity.class);
        intent.putExtra("data",new Gson().toJson(classData));
        startActivity(intent);
    }
}
