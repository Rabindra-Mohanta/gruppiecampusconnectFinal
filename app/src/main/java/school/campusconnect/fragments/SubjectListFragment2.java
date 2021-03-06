package school.campusconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

import school.campusconnect.R;
import school.campusconnect.activities.AddSubjectActivity;
import school.campusconnect.activities.AddSubjectActivity2;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.subjects.SubjectResponse;
import school.campusconnect.datamodel.subjects.SubjectStaffResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import butterknife.Bind;
import butterknife.ButterKnife;

public class SubjectListFragment2 extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.tvNoData)
    public TextView tvNoData;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    String team_id;
    String className;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss,container,false);
        ButterKnife.bind(this,view);
        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        team_id=getArguments().getString("team_id");
        className=getArguments().getString("className");

        showLoadingBar(progressBar,true);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LeafManager leafManager = new LeafManager();
        leafManager.getSubjectStaff(this,GroupDashboardActivityNew.groupId,team_id,"more");
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);
        SubjectStaffResponse res = (SubjectStaffResponse) response;
        List<SubjectStaffResponse.SubjectData> result = res.getData();
        AppLog.e(TAG, "ClassResponse " + result);

        rvClass.setAdapter(new SubjectAdapter(result));
    }

    @Override
    public void onFailure(int apiId, String msg) {
        tvNoData.setText(getResources().getString(R.string.txt_failed_to_load));
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        tvNoData.setText(getResources().getString(R.string.txt_failed_to_load));
        progressBar.setVisibility(View.GONE);
    }

    public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder>
    {
        List<SubjectStaffResponse.SubjectData> list;
        private Context mContext;

        public SubjectAdapter(List<SubjectStaffResponse.SubjectData> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject_staff,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final SubjectStaffResponse.SubjectData item = list.get(position);
            holder.txt_name.setText(item.getName());
            String staffName = item.getStaffNameFormatted();

            if (staffName == null || staffName.trim().isEmpty()) {
                holder.txt_count.setText("");
                holder.txt_count.setVisibility(View.GONE);
                holder.iv_assigned.setVisibility(View.GONE);
                holder.btn_assign.setVisibility(View.VISIBLE);
                holder.btn_assign.setOnClickListener(v -> onTreeClick(list.get(position),true));
            } else {
                holder.txt_count.setText(staffName.trim());
                holder.txt_count.setVisibility(View.VISIBLE);
                holder.iv_assigned.setVisibility(View.VISIBLE);
                holder.btn_assign.setVisibility(View.INVISIBLE);
                holder.btn_assign.setOnClickListener(v -> {});
            }
        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    tvNoData.setText(getResources().getString(R.string.txt_empty_data));
                }
                else {
                    tvNoData.setText("");
                }
                progressBar.setVisibility(View.GONE);
                return list.size();
            }
            else
            {
                tvNoData.setText(getResources().getString(R.string.txt_empty_data));
                progressBar.setVisibility(View.GONE);
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.txt_name)
            TextView txt_name;

            @Bind(R.id.txt_count)
            TextView txt_count;

            @Bind(R.id.btn_assign)
            Button btn_assign;

            @Bind(R.id.img_tree)
            ImageView img_tree;

            @Bind(R.id.iv_assigned)
            ImageView iv_assigned;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onTreeClick(list.get(getAdapterPosition()),false);
                    }
                });
                img_tree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onTreeClick(list.get(getAdapterPosition()),false);
                    }
                });

            }
        }
    }

    private void onTreeClick(SubjectStaffResponse.SubjectData classData,boolean isAssign) {
        Intent intent = new Intent(getActivity(), AddSubjectActivity2.class);
        intent.putExtra("team_id",team_id);
        intent.putExtra("isAssign",isAssign);
        intent.putExtra("className",className);
        intent.putExtra("is_edit",true);
        intent.putExtra("data",new Gson().toJson(classData));
        startActivity(intent);
    }
}
