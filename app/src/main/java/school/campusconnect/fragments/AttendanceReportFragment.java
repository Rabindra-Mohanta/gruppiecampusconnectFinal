package school.campusconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.activities.AttendanceDetailActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.attendance_report.AttendanceReportRes;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.MixOperations;

import static school.campusconnect.network.LeafManager.API_CLASSES;

public class AttendanceReportFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.spClass)
    public Spinner spClass;

    @Bind(R.id.imgLeft)
    public ImageView imgLeft;
    @Bind(R.id.tvMonth)
    public TextView tvMonth;
    @Bind(R.id.imgRight)
    public ImageView imgRight;

    @Bind(R.id.rvStudents)
    public RecyclerView rvStudents;


    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;
    @Bind(R.id.llClass)
    public LinearLayout llClass;


    @Bind(R.id.progressBar)
    public ProgressBar progressBar;
    Calendar calendar;
    private ArrayList<ClassResponse.ClassData> listClass;
    private String selectedTeamId="";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance_report,container,false);
        ButterKnife.bind(this,view);

        init();



        if(getArguments()!=null){
            selectedTeamId=getArguments().getString("team_id","");
        }

        if(TextUtils.isEmpty(selectedTeamId)){
            LeafManager leafManager = new LeafManager();
            leafManager.getClasses(this,GroupDashboardActivityNew.groupId);
        }else {
            llClass.setVisibility(View.GONE);
            getAttendanceReport();
        }


        return view;
    }

    private void init() {

        rvStudents.setLayoutManager(new LinearLayoutManager(getActivity()));
        calendar = Calendar.getInstance();
        spClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(listClass!=null){
                    selectedTeamId = listClass.get(i).getId();
                    getAttendanceReport();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getAttendanceReport() {
        progressBar.setVisibility(View.VISIBLE);
        tvMonth.setText(MixOperations.getMonth(calendar.getTime()).toUpperCase());
        LeafManager leafManager = new LeafManager();
        leafManager.getAttendanceReport(this,GroupDashboardActivityNew.groupId,selectedTeamId,calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.YEAR));
    }

    @Override
    public void onStart() {
        super.onStart();
    }
    @OnClick({R.id.imgLeft,R.id.imgRight})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.imgLeft:
                calendar.add(Calendar.MONTH,-1);
                getAttendanceReport();
                break;
            case R.id.imgRight:
                calendar.add(Calendar.MONTH,1);
                getAttendanceReport();
                break;

        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);
        if(getActivity()==null)
            return;

        if(apiId==API_CLASSES){
            ClassResponse res = (ClassResponse) response;
            listClass = res.getData();
            String[] stud = new String[listClass.size()];
            for (int i=0;i<listClass.size();i++){
                stud[i] = listClass.get(i).getName();
            }
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,stud);
            spClass.setAdapter(adapter);
        }
        else {
            AttendanceReportRes res = (AttendanceReportRes) response;
            rvStudents.setAdapter(new ReportStudentAdapter(res.result));
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

    public class ReportStudentAdapter extends RecyclerView.Adapter<ReportStudentAdapter.ViewHolder>
    {
        List<AttendanceReportRes.AttendanceReportData> list;
        private Context mContext;

        public ReportStudentAdapter(List<AttendanceReportRes.AttendanceReportData> list) {
            this.list = list;
        }

        @Override
        public ReportStudentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_student,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ReportStudentAdapter.ViewHolder holder, final int position) {
            final AttendanceReportRes.AttendanceReportData item = list.get(position);

            holder.tvRollNo.setText(item.getRollNumber());
            holder.tvName.setText(item.getStudentName());
            holder.tvMorning.setText(item.getMorningPresentCount()+"("+item.getTotalMorningAttendance()+")");
            holder.tvEvering.setText(item.getAfternoonPresentCount()+"("+item.getTotalAfternoonAttendance()+")");
        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    txtEmpty.setText("No Data found.");
                }else {
                    txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                txtEmpty.setText("No Data found.");
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.tvRollNo) TextView tvRollNo;

            @Bind(R.id.tvName) TextView tvName;

            @Bind(R.id.tvMorning) TextView tvMorning;

            @Bind(R.id.tvEvering) TextView tvEvering;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editStudent(list.get(getAdapterPosition()));
                    }
                });
            }
        }
    }
    private void editStudent(AttendanceReportRes.AttendanceReportData studentData) {
        Intent intent = new Intent(getActivity(), AttendanceDetailActivity.class);
        intent.putExtra("group_id",GroupDashboardActivityNew.groupId);
        intent.putExtra("team_id",selectedTeamId);
        intent.putExtra("title",studentData.getStudentName());
        intent.putExtra("calendar",calendar);
        intent.putExtra("userId",studentData.getUserId());
        intent.putExtra("rollNo",studentData.getRollNumber());
        startActivity(intent);
    }
}
