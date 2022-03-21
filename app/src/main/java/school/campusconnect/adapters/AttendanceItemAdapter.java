package school.campusconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public AttendanceItemAdapter(ArrayList<AttendanceListRes.lastDayData> lastDaysAttendance) {
        this.lastDaysAttendance = lastDaysAttendance;
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
        if (data.attendance)
        {
            holder.tvAttendance.setText("P");
            holder.tvAttendance.setBackgroundResource(R.color.color_green);
        }
        else
        {
            holder.tvAttendance.setText("A");
            holder.tvAttendance.setBackgroundResource(R.color.red);
        }

    }

    @Override
    public int getItemCount() {
        return lastDaysAttendance!=null?lastDaysAttendance.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tvAttendance)
        TextView tvAttendance;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
