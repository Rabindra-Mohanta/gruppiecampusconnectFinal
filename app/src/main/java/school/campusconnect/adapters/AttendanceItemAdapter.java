package school.campusconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.AttendanceListRes;

public class AttendanceItemAdapter extends RecyclerView.Adapter<AttendanceItemAdapter.ViewHolder> {

    ArrayList<AttendanceListRes.lastDayData> lastDaysAttendance;
    Context mContext;
    OnClick click;
    String user;
    String studentName;


    public AttendanceItemAdapter(ArrayList<AttendanceListRes.lastDayData> lastDaysAttendance, AttendanceAdapter attendanceAdapter,String position,String name) {
        this.lastDaysAttendance = lastDaysAttendance;
        this.click = attendanceAdapter;
        this.user = position;
        this.studentName = name;
    }

    @NonNull
    @Override
    public AttendanceItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attend_student, parent, false);
        return new AttendanceItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceItemAdapter.ViewHolder holder, int position) {

        AttendanceListRes.lastDayData data = lastDaysAttendance.get(position);

        if (data.attendance.equalsIgnoreCase("present"))
        {
            holder.tvAttendance.setText("P");
            holder.tvAttendance.setBackgroundResource(R.color.color_green);
        }
        else if (data.attendance.equalsIgnoreCase("leave"))
        {
            holder.tvAttendance.setText("L");
            holder.tvAttendance.setBackgroundResource(R.color.color_yellow);
        }
        else
        {
            holder.tvAttendance.setText("A");
            holder.tvAttendance.setBackgroundResource(R.color.red);
        }


        holder.tvAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                click.add(data,user,studentName);

            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                click.add(data,user,studentName);

            }
        });
    }

    @Override
    public int getItemCount() {
        return lastDaysAttendance!=null?lastDaysAttendance.size() : 0;
    }
    public interface OnClick
    {
        public void add(AttendanceListRes.lastDayData lastDayData,String user,String userName);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tvAttendance)
        TextView tvAttendance;

        @Bind(R.id.edit)
        ImageView edit;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
