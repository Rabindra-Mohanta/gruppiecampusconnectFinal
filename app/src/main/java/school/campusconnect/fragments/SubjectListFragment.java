package school.campusconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.AddSubjectActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.subjects.SubjectResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;

public class SubjectListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss,container,false);
        ButterKnife.bind(this,view);
        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        showLoadingBar(progressBar);
     //   progressBar.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LeafManager leafManager = new LeafManager();
        leafManager.getSubjects(this,GroupDashboardActivityNew.groupId);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
     //   progressBar.setVisibility(View.GONE);
        SubjectResponse res = (SubjectResponse) response;
        List<SubjectResponse.SubjectData> result = res.getData();
        AppLog.e(TAG, "ClassResponse " + result);

        rvClass.setAdapter(new SubjectAdapter(result));
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        //   progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        //   progressBar.setVisibility(View.GONE);
    }

    public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder>
    {
        List<SubjectResponse.SubjectData> list;
        private Context mContext;

        public SubjectAdapter(List<SubjectResponse.SubjectData> list) {
            this.list = list;
        }

        @Override
        public SubjectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SubjectAdapter.ViewHolder holder, final int position) {
            final SubjectResponse.SubjectData item = list.get(position);
            holder.txt_name.setText(item.getName());
            holder.txt_count.setText("Subjects : "+item.getSubjects());
        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    txtEmpty.setText(getResources().getString(R.string.txt_no_subject_found));
                }
                else {
                    txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                txtEmpty.setText(getResources().getString(R.string.txt_no_subject_found));
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.txt_name)
            TextView txt_name;

            @Bind(R.id.txt_count)
            TextView txt_count;
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

    private void onTreeClick(SubjectResponse.SubjectData classData) {
        Intent intent = new Intent(getActivity(), AddSubjectActivity.class);
        intent.putExtra("data",new Gson().toJson(classData));
        intent.putExtra("is_edit",true);
        startActivity(intent);
    }
}
