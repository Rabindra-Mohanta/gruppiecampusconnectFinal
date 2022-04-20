package school.campusconnect.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.attendance_report.PreSchoolStudentRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class AttendancePreSchoolListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    private String mGroupId;
    private String teamId;
    private String type="";
    LeafManager leafManager = new LeafManager();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss,container,false);
        ButterKnife.bind(this,view);

        init();

        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressBar.setVisibility(View.VISIBLE);

        AppLog.e(TAG,"getStudents : ");
        leafManager.getPreSchoolStudent(this,GroupDashboardActivityNew.groupId,teamId);

        return view;
    }

    private void init() {
        if(getArguments()!=null)
        {
            mGroupId = getArguments().getString("group_id");
            teamId = getArguments().getString("team_id");
            type = getArguments().getString("type");
            AppLog.e(TAG,"mGroupId : "+mGroupId);
            AppLog.e(TAG,"teamId : "+teamId);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        if(getActivity()==null){
            return;
        }
        progressBar.setVisibility(View.GONE);
        if(apiId== LeafManager.API_ATTENDANCE_PRESCHOOL_IN){
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_attendance_submit), Toast.LENGTH_SHORT).show();
        }
        else if(apiId== LeafManager.API_ATTENDANCE_PRESCHOOL_OUT){
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_attendance_submit), Toast.LENGTH_SHORT).show();
        }else {

            PreSchoolStudentRes res = (PreSchoolStudentRes) response;
            List<PreSchoolStudentRes.PreSchoolStudentData> result = res.getData();
            AppLog.e(TAG, "StudentRes " + result);

            rvClass.setAdapter(new PreSchoolStudentAdapter(result));
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }

    public class PreSchoolStudentAdapter extends RecyclerView.Adapter<PreSchoolStudentAdapter.ViewHolder>
    {
        List<PreSchoolStudentRes.PreSchoolStudentData> list;
        private Context mContext;
        int selected = -1;
        public PreSchoolStudentAdapter(List<PreSchoolStudentRes.PreSchoolStudentData> list) {
            this.list = list;
        }

        @Override
        public PreSchoolStudentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preschool_student,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final PreSchoolStudentAdapter.ViewHolder holder, final int position) {
            final PreSchoolStudentRes.PreSchoolStudentData item = list.get(position);

            if (!TextUtils.isEmpty(item.getStudentImage())) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getStudentImage())).resize(50,50).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgTeam,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.img_lead_default.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getStudentImage())).resize(50,50).into(holder.imgTeam, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.img_lead_default.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.img_lead_default.setVisibility(View.VISIBLE);
                                        TextDrawable drawable = TextDrawable.builder()
                                                .buildRound(ImageUtil.getTextLetter(item.getStudentName()), ImageUtil.getRandomColor(position));
                                        holder.img_lead_default.setImageDrawable(drawable);
                                        AppLog.e("Picasso", "Error : ");
                                    }
                                });
                            }
                        });
            } else {
                holder.img_lead_default.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(item.getStudentName()), ImageUtil.getRandomColor(position));
                holder.img_lead_default.setImageDrawable(drawable);
            }

            holder.txt_name.setText(item.getStudentName());
            holder.txt_count.setText(getResources().getString(R.string.lbl_roll_No)+" "+item.getRollNumber());

            if(type.equalsIgnoreCase("IN")){
                if(item.isAttendanceIn()){
                    holder.chk.setChecked(true);
                    holder.chk.setEnabled(false);
                    holder.imgSubmit.setVisibility(View.INVISIBLE);
                }else {
                    holder.chk.setEnabled(true);
                    holder.imgSubmit.setVisibility(View.VISIBLE);
                    if(selected!=position){
                        holder.chk.setChecked(false);
                    }
                }
            }else {
                if(item.isAttendanceOut()){
                    holder.chk.setChecked(true);
                    holder.chk.setEnabled(false);
                    holder.imgSubmit.setVisibility(View.INVISIBLE);
                }else {
                    if(selected!=position){
                        holder.chk.setChecked(false);
                    }
                    holder.chk.setEnabled(true);
                    holder.imgSubmit.setVisibility(View.VISIBLE);
                }
            }

        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    txtEmpty.setText(getResources().getString(R.string.txt_no_student_found));
                }else {
                    txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                txtEmpty.setText(getResources().getString(R.string.txt_no_student_found));
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

            @Bind(R.id.chk)
            CheckBox chk;

            @Bind(R.id.imgSubmit)
            ImageView imgSubmit;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);

                imgSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(chk.isChecked()){
                            chk.setEnabled(false);
                            imgSubmit.setVisibility(View.INVISIBLE);
                            if(type.equalsIgnoreCase("IN")){
                                list.get(getAdapterPosition()).setAttendanceIn(true);
                            }else {
                                list.get(getAdapterPosition()).setAttendanceOut(true);
                            }
                            callInOutApi(list.get(getAdapterPosition()));
                        }else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.toast_please_select_student), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                chk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(chk.isChecked()){
                            selected = getAdapterPosition();
                            notifyDataSetChanged();
                        }
                    }
                });
            }
        }
    }

    private void callInOutApi(PreSchoolStudentRes.PreSchoolStudentData preSchoolStudentData) {
        progressBar.setVisibility(View.VISIBLE);
        if(type.equalsIgnoreCase("IN")){
            leafManager.attendanceIN(this,mGroupId,teamId,preSchoolStudentData.getUserId()+","+preSchoolStudentData.getRollNumber());
        }else {
            leafManager.attendanceOUT(this,mGroupId,teamId,preSchoolStudentData.getUserId()+","+preSchoolStudentData.getRollNumber());
        }
    }
}
