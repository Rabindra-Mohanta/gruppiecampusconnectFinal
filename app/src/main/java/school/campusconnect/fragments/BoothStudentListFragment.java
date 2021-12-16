package school.campusconnect.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.BuildConfig;
import school.campusconnect.LeafApplication;
import school.campusconnect.R;
import school.campusconnect.activities.AddClassStudentActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.UpdateMemberActivity;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.booths.BoothMemberResponse;
import school.campusconnect.datamodel.booths.BoothResponse;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.student.StudentRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class BoothStudentListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;


    @Bind(R.id.etSearch)
    public EditText etSearch;

    BoothResponse.BoothData classData;
    private String mGroupId;
    private String teamId;

    private ArrayList<BoothMemberResponse.BoothMemberData> list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss_search, container, false);
        ButterKnife.bind(this, view);

        init();

        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressBar.setVisibility(View.VISIBLE);

        return view;
    }
    public void showHideSearch(){
        if(etSearch.getVisibility()==View.VISIBLE){
            etSearch.setVisibility(View.GONE);
        }else {
            etSearch.setVisibility(View.VISIBLE);
        }
    }


    private void init() {
        if (getArguments() != null) {
            classData = new Gson().fromJson(getArguments().getString("class_data"), BoothResponse.BoothData.class);
            AppLog.e(TAG, "classData : " + classData);
            mGroupId = GroupDashboardActivityNew.groupId;
            teamId = classData.boothId;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        etSearch.setText("");
        LeafManager leafManager = new LeafManager();
        AppLog.e(TAG, "getStudents : ");
        leafManager.getBoothMember(this, GroupDashboardActivityNew.groupId, classData.boothId);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);
        BoothMemberResponse res = (BoothMemberResponse) response;
        list = res.getData();
        AppLog.e(TAG, "StudentRes " + list);

        rvClass.setAdapter(new ClassesStudentAdapter(list));
    }

    @Override
    public void onFailure(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
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
            holder.txt_count.setText("Role: "+item.roleOnConstituency);
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    txtEmpty.setText("No Booth Member found.");
                } else {
                    txtEmpty.setText("");
                }

                return list.size();
            } else {
                txtEmpty.setText("No Booth Member found.");
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
        Intent intent = new Intent(getActivity(), UpdateMemberActivity.class);
        intent.putExtra("group_id", mGroupId);
        intent.putExtra("team_id", teamId);
        intent.putExtra("className", studentData.name);
        intent.putExtra("student_data", new Gson().toJson(studentData));
        startActivity(intent);
    }

}
