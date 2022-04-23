package school.campusconnect.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import school.campusconnect.activities.AddTimeTable2;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.subjects.SubjectStaffResponse;
import school.campusconnect.datamodel.time_table.TimeTableList2Response;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.views.SMBDialogUtils;

public class TimeTableListFragment2 extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TimeTableListFragment2";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    String team_id;
    String team_name;
    String role;
    private ArrayList<TimeTableList2Response.TimeTableData2> result;
    private ArrayList<TimeTableList2Response.TimeTableData2> resultv2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss,container,false);
        ButterKnife.bind(this,view);
        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));
      //  rvClass.setAdapter(new SubjectAdapterv2());

        team_id=getArguments().getString("team_id");
        team_name = getArguments().getString("team_name");
        role=getArguments().getString("role");

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getList();
    }

    private void getList() {
        showLoadingBar(progressBar);
        // progressBar.setVisibility(View.VISIBLE);
        LeafManager leafManager = new LeafManager();
        leafManager.getTTNew(this,GroupDashboardActivityNew.groupId,team_id);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
        //progressBar.setVisibility(View.GONE);
        switch (apiId){
            case LeafManager.API_TT_REMOVE_DAY:
                getList();
                break;
            case LeafManager.API_TT_REMOVE:
                if(getActivity()!=null){
                    getActivity().finish();
                }
                break;

            default:
                TimeTableList2Response res = (TimeTableList2Response) response;
                result = res.getData();

                AppLog.e(TAG, "ClassResponse " + result);
                if(result!=null){
                    int currDay = getIntDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
                    AppLog.e(TAG,"currDay : "+currDay);

                    resultv2 = new ArrayList<>();
                    for (int i=0;i<result.size();i++){
                        if(result.get(i).getDay().equals(currDay+"")){
                            result.get(i).isSelected = true;
                        }
                    }

                    for (int i=0;i<7;i++)
                    {
                            int isInList = -1;
                            for (int j= 0;j<result.size();j++)
                            {
                                if(result.get(j).getDay().equals(""+(i+1))){
                                    isInList = j;
                                }
                            }
                            if(isInList == -1)
                            {
                                TimeTableList2Response.TimeTableData2 tt2 = new TimeTableList2Response.TimeTableData2();
                                tt2.setDay(""+(i+1));
                                resultv2.add(tt2);
                            }
                            else
                            {
                                resultv2.add(result.get(isInList));
                            }



                    }
                }

                rvClass.setAdapter(new SubjectAdapter(resultv2));
        }

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
        hideLoadingBar();
        //progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        //progressBar.setVisibility(View.GONE);
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

            Log.e(TAG,"isAdmin"+GroupDashboardActivityNew.isAdmin);
            Log.e(TAG,"allowedToAddUser"+GroupDashboardActivityNew.allowedToAddUser);

            if (GroupDashboardActivityNew.mGroupItem.isAdmin || GroupDashboardActivityNew.mGroupItem.canPost)
            {
                holder.imgEdit.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.imgEdit.setVisibility(View.GONE);
            }

            holder.imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), AddTimeTable2.class);
                    intent.putExtra("team_id",team_id);
                    intent.putExtra("team_name",team_name);
                    intent.putExtra("day",item.day);
                    startActivity(intent);
                }
            });


            holder.rvSession.setAdapter(new SessionAdapter(item.getSessions()));

           /* if("admin".equalsIgnoreCase(role)){
                holder.imgRemove.setVisibility(View.VISIBLE);
                holder.imgRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteTimeTableDay(item);
                    }
                });
            }else {
                holder.imgRemove.setVisibility(View.GONE);
            }*/

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
                    txtEmpty.setText(getResources().getString(R.string.txt_no_time_table_found));
                }
                else {
                    txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                txtEmpty.setText(getResources().getString(R.string.txt_no_time_table_found));
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

            @Bind(R.id.imgEdit)
            ImageView imgEdit;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
    }

    private void deleteTimeTableDay(TimeTableList2Response.TimeTableData2 item) {

        Log.e(TAG,"Which day delete :"+item.day);
        SMBDialogUtils.showSMBDialogOKCancel(getActivity(), getResources().getString(R.string.smb_delete_time_table), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LeafManager leafManager=new LeafManager();
                showLoadingBar(progressBar);
                // progressBar.setVisibility(View.VISIBLE);
                leafManager.deleteTTNewByDay(TimeTableListFragment2.this,GroupDashboardActivityNew.groupId,team_id,item.day);
            }
        });


    }




    public void deleteTT() {
        if(result!=null && result.size()>0){
            SMBDialogUtils.showSMBDialogOKCancel(getActivity(), getResources().getString(R.string.smb_delete_time_table_), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LeafManager leafManager=new LeafManager();
                    showLoadingBar(progressBar);
                    // progressBar.setVisibility(View.VISIBLE);
                    leafManager.deleteTTNew(TimeTableListFragment2.this,GroupDashboardActivityNew.groupId,team_id);
                }
            });
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
            holder.txt_time.setText(item.getStartTime()+" - "+item.getEndTime());
            holder.txt_subject_staff.setText(item.getTeacherName());

          /*  holder.txt_subject.setText(item.getSubjectName());
            holder.txt_staff.setText(item.getTeacherName());*/
        }
        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    txtEmpty.setText(getResources().getString(R.string.txt_no_time_table_found));
                }
                else {
                    txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                txtEmpty.setText(getResources().getString(R.string.txt_no_time_table_found));
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.txt_period)
            TextView txt_period;

            @Bind(R.id.txt_time)
            TextView txt_time;

            @Bind(R.id.txt_subject_staff)
            TextView txt_subject_staff;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
    }
}
