package school.campusconnect.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.poi.sl.usermodel.Line;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.subjects.SubjectStaffResponse;
import school.campusconnect.datamodel.time_table.TimeTableList2Response;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import butterknife.Bind;
import butterknife.ButterKnife;

public class TimeTableListFragment2 extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    String team_id;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss,container,false);
        ButterKnife.bind(this,view);
        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        team_id=getArguments().getString("team_id");

        progressBar.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LeafManager leafManager = new LeafManager();
        leafManager.getTTNew(this,GroupDashboardActivityNew.groupId,team_id);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);
        TimeTableList2Response res = (TimeTableList2Response) response;
        List<TimeTableList2Response.TimeTableData2> result = res.getData();
        AppLog.e(TAG, "ClassResponse " + result);
        if(result!=null){
            int currDay = getIntDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
            AppLog.e(TAG,"currDay : "+currDay);
            for (int i=0;i<result.size();i++){
                if(result.get(i).getDay().equals(currDay+"")){
                    result.get(i).isSelected = true;
                }
            }
        }
        rvClass.setAdapter(new SubjectAdapter(result));
    }
    private int getIntDay(int i) {
        switch (i) {
            case 1:
                return 7;
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
                return 3;
            case 5:
                return 4;
            case 6:
                return 5;
            case 7:
                return 6;
        }
        return 0;
    }

    @Override
    public void onFailure(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }

    public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder>
    {
        List<TimeTableList2Response.TimeTableData2> list;
        private Context mContext;

        public SubjectAdapter(List<TimeTableList2Response.TimeTableData2> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tt,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final TimeTableList2Response.TimeTableData2 item = list.get(position);
            holder.txt_name.setText(getWeekDay(item.getDay()));
            if(item.isSelected){
                holder.imgDown.setImageResource(R.drawable.arrow_up);
                holder.rvSession.setVisibility(View.VISIBLE);
            }else {
                holder.rvSession.setVisibility(View.GONE);
                holder.imgDown.setImageResource(R.drawable.arrow_down);
            }
            holder.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.isSelected = !item.isSelected;
                    if(item.isSelected){
                        holder.rvSession.setVisibility(View.VISIBLE);
                        holder.imgDown.setImageResource(R.drawable.arrow_up);
                    }else {
                        holder.rvSession.setVisibility(View.GONE);
                        holder.imgDown.setImageResource(R.drawable.arrow_down);
                    }
                }
            });

            holder.rvSession.setAdapter(new SessionAdapter(item.getSessions()));
        }

        private String getWeekDay(String day) {
            switch (day){
                case "1":return "Monday";
                case "2":return "Tuesday";
                case "3":return "Wednesday";
                case "4":return "Thursday";
                case "5":return "Friday";
                case "6":return "Saturday";
                case "7":return "Sunday";
            }
            return "";
        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    txtEmpty.setText("No Time Table found.");
                }
                else {
                    txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                txtEmpty.setText("No Time Table found.");
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.llMain)
            LinearLayout llMain;

            @Bind(R.id.txt_name)
            TextView txt_name;

            @Bind(R.id.imgDown)
            ImageView imgDown;

            @Bind(R.id.rvSession)
            RecyclerView rvSession;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
    }

    public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder>
    {
        ArrayList<TimeTableList2Response.SessionsTimeTable> list;
        private Context mContext;

        public SessionAdapter(ArrayList<TimeTableList2Response.SessionsTimeTable> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tt_session,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final TimeTableList2Response.SessionsTimeTable item = list.get(position);
            holder.txt_period.setText(item.getPeriod());
            holder.txt_subject.setText(item.getSubjectName());
            holder.txt_staff.setText(item.getTeacherName());
        }
        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    txtEmpty.setText("No Session found.");
                }
                else {
                    txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                txtEmpty.setText("No Session found.");
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.txt_period)
            TextView txt_period;

            @Bind(R.id.txt_subject)
            TextView txt_subject;

            @Bind(R.id.txt_staff)
            TextView txt_staff;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
    }
}
