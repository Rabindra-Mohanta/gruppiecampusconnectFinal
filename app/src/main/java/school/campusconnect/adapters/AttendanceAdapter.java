package school.campusconnect.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.EditAttendanceActivity;
import school.campusconnect.datamodel.AttendanceListRes;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {

    private final ArrayList<AttendanceListRes.AttendanceData> listAttendance;
    private final String groupId;
    private final String teamId;
    private Context mContext;
    public AttendanceAdapter(ArrayList<AttendanceListRes.AttendanceData> listAttendance, String groupId, String teamId) {
        this.listAttendance=listAttendance;
        this.groupId=groupId;
        this.teamId=teamId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_attendance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final AttendanceListRes.AttendanceData item = listAttendance.get(position);
        holder.tvName.setText(item.name);
        holder.tvNumber.setText("Roll No."+(TextUtils.isEmpty(item.rollNumber)?"":item.rollNumber));

        holder.chkAttendance.setChecked(item.isChecked);

        holder.rvStudentAttendance.setAdapter(new AttendanceItemAdapter(item.lastDaysAttendance));


        if (!TextUtils.isEmpty(item.studentImage))
        {
            holder.imgLead_default.setVisibility(View.INVISIBLE);
            Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.studentImage)).resize(50, 50).
                    into(holder.imgLead, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.imgLead_default.setVisibility(View.INVISIBLE);
                            AppLog.e("LeadAdapter", "Item Not Empty , On Success ");
                        }

                        @Override
                        public void onError() {
                            AppLog.e("LeadAdapter", "Item Not Empty , On Error");

                            holder.imgLead_default.setVisibility(View.VISIBLE);
                            TextDrawable drawable = TextDrawable.builder()
                                    .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position) );
                            holder.imgLead_default.setImageDrawable(drawable);
                        }
                    });
        } else {
            AppLog.e("LeadAdapter", "Item Empty ");
            holder.imgLead_default.setVisibility(View.VISIBLE);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position) );
            holder.imgLead_default.setImageDrawable(drawable);
        }



    }

    @Override
    public int getItemCount() {
        return listAttendance.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.chkAttendance)
        CheckBox chkAttendance;

        @Bind(R.id.tvName)
        TextView tvName;

        @Bind(R.id.tvNumber)
        TextView tvNumber;

        @Bind(R.id.rvStudentAttendance)
        RecyclerView rvStudentAttendance;

        @Bind(R.id.imgEdit)
        ImageView imgEdit;
        @Bind(R.id.img_lead)
        ImageView imgLead;
        @Bind(R.id.img_lead_default)
        ImageView imgLead_default;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            chkAttendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listAttendance.get(getAdapterPosition()).isChecked=!listAttendance.get(getAdapterPosition()).isChecked;
                    notifyItemChanged(getAdapterPosition());
                }
            });

            imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext, EditAttendanceActivity.class);
                    intent.putExtra("data",new Gson().toJson(listAttendance.get(getAdapterPosition())));
                    intent.putExtra("group_id",groupId);
                    intent.putExtra("team_id",teamId);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
