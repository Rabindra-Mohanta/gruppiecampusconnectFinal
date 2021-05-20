package school.campusconnect.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.fees.StudentFeesRes;
import school.campusconnect.datamodel.student.StudentRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class FeesListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    ClassResponse.ClassData classData;
    private String mGroupId;
    private String teamId;
    private ArrayList<StudentFeesRes.StudentFees> list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss, container, false);
        ButterKnife.bind(this, view);

        init();

        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressBar.setVisibility(View.VISIBLE);

        return view;
    }

    private void init() {
        if (getArguments() != null) {
            classData = new Gson().fromJson(getArguments().getString("class_data"), ClassResponse.ClassData.class);
            AppLog.e(TAG, "classData : " + classData);
            mGroupId = GroupDashboardActivityNew.groupId;
            teamId = classData.getId();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LeafManager leafManager = new LeafManager();
        AppLog.e(TAG, "getStudents : ");
        leafManager.getStudentFeesList(this, GroupDashboardActivityNew.groupId, classData.getId());
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);
        StudentFeesRes res = (StudentFeesRes) response;
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
        List<StudentFeesRes.StudentFees> list;
        private Context mContext;

        public ClassesStudentAdapter(List<StudentFeesRes.StudentFees> list) {
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
            final StudentFeesRes.StudentFees item = list.get(position);

           /* if (!TextUtils.isEmpty(item.getImage())) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(50, 50).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgTeam,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.img_lead_default.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(50, 50).into(holder.imgTeam, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.img_lead_default.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.img_lead_default.setVisibility(View.VISIBLE);
                                        TextDrawable drawable = TextDrawable.builder()
                                                .buildRound(ImageUtil.getTextLetter(item.getName()), ImageUtil.getRandomColor(position));
                                        holder.img_lead_default.setImageDrawable(drawable);
                                        AppLog.e("Picasso", "Error : ");
                                    }
                                });
                            }
                        });
            } else {
                holder.img_lead_default.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(item.getName()), ImageUtil.getRandomColor(position));
                holder.img_lead_default.setImageDrawable(drawable);
            }
*/
            holder.txt_name.setText(item.getStudentName());
            holder.txt_count.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    txtEmpty.setText("No Students found.");
                } else {
                    txtEmpty.setText("");
                }

                return list.size();
            } else {
                txtEmpty.setText("No Students found.");
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

    private void editStudent(StudentFeesRes.StudentFees studentData) {
        Intent intent = new Intent(getActivity(), AddClassStudentActivity.class);
        intent.putExtra("group_id", mGroupId);
        intent.putExtra("team_id", teamId);
        intent.putExtra("isEdit", true);
        intent.putExtra("student_data", new Gson().toJson(studentData));
        startActivity(intent);
    }
}
