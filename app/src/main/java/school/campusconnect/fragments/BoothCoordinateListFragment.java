package school.campusconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.AddEditCoordinateMemberActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.UpdateMemberActivity;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.booths.BoothMemberResponse;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.ImageUtil;

public class BoothCoordinateListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    MyTeamData classData;
    private String mGroupId;
    private String teamId;

    private ArrayList<BoothMemberResponse.BoothMemberData> list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss, container, false);
        ButterKnife.bind(this, view);

        init();

        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));
        showLoadingBar(progressBar);
        //progressBar.setVisibility(View.VISIBLE);

        return view;
    }


    private void init() {
        if (getArguments() != null) {
            classData = new Gson().fromJson(getArguments().getString("class_data"), MyTeamData.class);
            AppLog.e(TAG, "classData : " + classData);
            mGroupId = GroupDashboardActivityNew.groupId;
            teamId = classData.teamId;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LeafManager leafManager = new LeafManager();
        AppLog.e(TAG, "getStudents : ");
        leafManager.getCoordinateMember(this, GroupDashboardActivityNew.groupId, classData.teamId);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
        //progressBar.setVisibility(View.GONE);
        BoothMemberResponse res = (BoothMemberResponse) response;
        list = res.getData();
        AppLog.e(TAG, "StudentRes " + list);

        rvClass.setAdapter(new ClassesStudentAdapter(list));
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        //progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        //progressBar.setVisibility(View.GONE);
    }

    public class ClassesStudentAdapter extends RecyclerView.Adapter<ClassesStudentAdapter.ViewHolder> {
        List<BoothMemberResponse.BoothMemberData> list;
        private Context mContext;

        public ClassesStudentAdapter(List<BoothMemberResponse.BoothMemberData> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class_student, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final BoothMemberResponse.BoothMemberData item = list.get(position);

            holder.img_lead_default.setVisibility(View.VISIBLE);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position));
            holder.img_lead_default.setImageDrawable(drawable);
            holder.txt_name.setText(item.name);
            holder.txt_count.setText(getResources().getString(R.string.txt_phone)+item.phone);
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    txtEmpty.setText(getResources().getString(R.string.txt_no_coordinate_found));
                } else {
                    txtEmpty.setText("");
                }

                return list.size();
            } else {
                txtEmpty.setText(getResources().getString(R.string.txt_no_coordinate_found));
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.img_lead)
            ImageView imgTeam;

            @Bind(R.id.img_lead_default)
            ImageView img_lead_default;

            @Bind(R.id.txt_name)
            TextView txt_name;

            @Bind(R.id.txt_count)
            TextView txt_count;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editStudent(list.get(getAdapterPosition()));
                    }
                });
            }
        }
    }

    private void editStudent(BoothMemberResponse.BoothMemberData studentData) {
        Intent intent = new Intent(getActivity(), AddEditCoordinateMemberActivity.class);
        intent.putExtra("group_id", mGroupId);
        intent.putExtra("team_id", teamId);
        intent.putExtra("isEdit", true);
        intent.putExtra("className", studentData.name);
        intent.putExtra("student_data", new Gson().toJson(studentData));
        startActivity(intent);
    }
}
