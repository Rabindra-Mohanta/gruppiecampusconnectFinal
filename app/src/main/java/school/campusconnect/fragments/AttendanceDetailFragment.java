package school.campusconnect.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.attendance_report.AttendanceDetailRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.MixOperations;

public class AttendanceDetailFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";

    @Bind(R.id.tvMonth)
    public TextView tvMonth;

    @Bind(R.id.rvStudents)
    public RecyclerView rvStudents;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;
    Calendar calendar;
    private String mGroupId;
    private String teamId;
    private String userId;
    private String rollNo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGroupId = getArguments().getString("group_id");
            teamId = getArguments().getString("team_id");
            userId = getArguments().getString("userId");
            rollNo = getArguments().getString("rollNo");
            calendar = (Calendar) getArguments().getSerializable("calendar");
        }
        if(calendar==null){
            calendar = Calendar.getInstance();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance_details, container, false);
        ButterKnife.bind(this, view);

        init();

        getAttendanceDetail();

        return view;
    }

    private void init() {
        rvStudents.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void getAttendanceDetail() {
        tvMonth.setText(MixOperations.getMonth(calendar.getTime()).toUpperCase());
        LeafManager leafManager = new LeafManager();
        progressBar.setVisibility(View.VISIBLE);
        leafManager.getAttendanceDetail(this, GroupDashboardActivityNew.groupId, teamId,userId,rollNo, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @OnClick({R.id.imgLeft, R.id.imgRight})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgLeft:
                calendar.add(Calendar.MONTH, -1);
                getAttendanceDetail();
                break;
            case R.id.imgRight:
                calendar.add(Calendar.MONTH, 1);
                getAttendanceDetail();
                break;

        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);
        if (getActivity() == null)
            return;
        AttendanceDetailRes res = (AttendanceDetailRes) response;
        rvStudents.setAdapter(new ReportDetailAdapter(res.getData()));
    }

    @Override
    public void onFailure(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }

    public class ReportDetailAdapter extends RecyclerView.Adapter<ReportDetailAdapter.ViewHolder> {
        List<AttendanceDetailRes.AttendanceDetailData> listMorning;

        public ReportDetailAdapter(List<AttendanceDetailRes.AttendanceDetailData> listMorning) {
            this.listMorning = listMorning;
        }

        @Override
        public ReportDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_detail, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ReportDetailAdapter.ViewHolder holder, final int position) {
            final AttendanceDetailRes.AttendanceDetailData itemMorning = listMorning.get(position);

            holder.tvDay.setText(itemMorning.getDay() + "");

            if (TextUtils.isEmpty(itemMorning.getMorningAttendance())) {
                holder.tvMorning.setText("");
            } else {
                holder.tvMorning.setText(itemMorning.getMorningAttendance().equalsIgnoreCase("true") ? "P" : "A");
            }

            if (TextUtils.isEmpty(itemMorning.getAfternoonAttendance())) {
                holder.tvEvering.setText("");
            } else {
                holder.tvEvering.setText(itemMorning.getAfternoonAttendance().equalsIgnoreCase("true") ? "P" : "A");
            }
        }

        @Override
        public int getItemCount() {
            if (listMorning != null) {
                if (listMorning.size() == 0) {
                    txtEmpty.setText("No Data found.");
                } else {
                    txtEmpty.setText("");
                }

                return listMorning.size();
            } else {
                txtEmpty.setText("No Data found.");
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.tvDay)
            TextView tvDay;


            @Bind(R.id.tvMorning)
            TextView tvMorning;

            @Bind(R.id.tvEvering)
            TextView tvEvering;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
