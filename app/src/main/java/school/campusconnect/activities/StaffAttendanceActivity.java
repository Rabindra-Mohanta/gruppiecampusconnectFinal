package school.campusconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.ActivityStaffAttendanceBinding;
import school.campusconnect.databinding.ItemStaffAttendanceBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.attendance_report.AttendenceEditRequest;
import school.campusconnect.datamodel.staff.ChangeStaffAttendanceReq;
import school.campusconnect.datamodel.staff.StaffAttendanceRes;
import school.campusconnect.datamodel.staff.TakeAttendanceReq;
import school.campusconnect.network.LeafManager;

public class StaffAttendanceActivity extends BaseActivity {
ActivityStaffAttendanceBinding binding;
public static final String TAG = "StaffAttendanceActivity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    StaffAttendance adapter;

    Calendar calendar;
    int day,month,year;
    LeafManager leafManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_staff_attendance);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.menu_staff_attendance));

        inits();
    }

    private void inits()
    {

        leafManager = new LeafManager();

        calendar = Calendar.getInstance();

        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH)+1;
        year = calendar.get(Calendar.YEAR);




        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAttendance();

            }
        });
    }

    private void apiCall()
    {
        adapter = new StaffAttendance();
        binding.rvAttendance.setAdapter(adapter);
        showLoadingBar(binding.progressBar,false);
        leafManager.getStaffAttendance(this,GroupDashboardActivityNew.groupId,day,month,year);

    }

    private void submitAttendance() {

        TakeAttendanceReq req = new TakeAttendanceReq();
        ArrayList<TakeAttendanceReq.AttendanceObject> attendanceObjects = new ArrayList<>();


        if (adapter.getAfterNoonAttendance().size() == 0 && adapter.getMorningAttendance().size() == 0)
        {
            return;
        }

        if (adapter.getMorningAttendance().size() > 0)
        {
            TakeAttendanceReq.AttendanceObject data= new TakeAttendanceReq.AttendanceObject();

            data.setUserId(adapter.getMorningAttendance());
            data.setAttendance("present");
            data.setSession("morning");
            attendanceObjects.add(data);
        }

        if (adapter.getAfterNoonAttendance().size() > 0)
        {
            TakeAttendanceReq.AttendanceObject data= new TakeAttendanceReq.AttendanceObject();

            data.setUserId(adapter.getAfterNoonAttendance());
            data.setAttendance("present");
            data.setSession("afternoon");
            attendanceObjects.add(data);
        }

        req.setAttendanceObjects(attendanceObjects);

        Log.e(TAG,"req submit data "+ new Gson().toJson(req));

        leafManager.takeStaffAttendance(this,GroupDashboardActivityNew.groupId,req);

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();

        if (LeafManager.API_STAFF_ATTENDACNCE == apiId)
        {
            StaffAttendanceRes res = (StaffAttendanceRes) response;
            binding.rvAttendance.setItemViewCacheSize(res.getStaffAttendData().size());
            adapter.add(res.getStaffAttendData());
        }

        if (LeafManager.API_TAKE_STAFF_ATTENDACNCE == apiId)
        {
            apiCall();
           Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_attendance_submit),Toast.LENGTH_SHORT).show();
        }

        if (LeafManager.API_CHNAGE_STAFF_ATTENDACNCE == apiId)
        {
            if (dialogMorning != null && dialogMorning.isShowing())
            {
                dialogMorning.dismiss();
            }
            if (dialogAfterNoon != null && dialogAfterNoon.isShowing())
            {
                dialogAfterNoon.dismiss();
            }

            apiCall();
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_success),Toast.LENGTH_SHORT).show();

        }
        super.onSuccess(apiId, response);
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        super.onFailure(apiId, msg);
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        super.onException(apiId, msg);
    }

    @Override
    protected void onStart() {
        apiCall();
        super.onStart();
    }

    public class StaffAttendance extends RecyclerView.Adapter<StaffAttendance.ViewHolder>
    {
        ArrayList<StaffAttendanceRes.StaffAttendData> staffAttendData;

        ArrayList<String> morningAttendance = new ArrayList<>();
        ArrayList<String> afterNoonAttendance = new ArrayList<>();
        @NonNull
        @Override
        public StaffAttendance.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
           ItemStaffAttendanceBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.item_staff_attendance,parent,false);
           return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull StaffAttendance.ViewHolder holder, int position) {
            StaffAttendanceRes.StaffAttendData data = staffAttendData.get(position);

           holder.binding.tvStaffName.setText(data.getName());


            if (data.getAttendance() != null && data.getAttendance().size() > 0)
            {
                for (int i = 0;i<data.getAttendance().size();i++)
                {
                    if (data.getAttendance().get(i).getSession().equalsIgnoreCase("morning"))
                    {
                        if (data.getAttendance().get(i).getAttendance().equalsIgnoreCase("present"))
                        {
                            holder.binding.chMorning.setChecked(true);
                        }
                        else{
                            holder.binding.chMorning.setChecked(false);
                        }
                    }

                    if (data.getAttendance().get(i).getSession().equalsIgnoreCase("afternoon"))
                    {
                        if (data.getAttendance().get(i).getAttendance().equalsIgnoreCase("present"))
                        {
                            holder.binding.chAfterNoon.setChecked(true);
                        }
                        else{
                            holder.binding.chAfterNoon.setChecked(false);
                        }
                    }
                }

                holder.binding.chAfterNoon.setClickable(false);
                holder.binding.chMorning.setEnabled(false);
            }
            else
            {
                holder.binding.chAfterNoon.setEnabled(true);
                holder.binding.chMorning.setEnabled(true);
                holder.binding.chMorning.setChecked(false);
                holder.binding.chMorning.setChecked(false);
            }

            holder.binding.chMorning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                    {
                        morningAttendance.add(data.getUserId());
                    }
                    else
                    {
                        if (morningAttendance != null && morningAttendance.size() > 0)
                        {
                            for (int i = 0 ;i<morningAttendance.size();i++)
                            {
                                if (data.getUserId().equalsIgnoreCase(morningAttendance.get(i)))
                                {
                                    morningAttendance.remove(i);
                                }
                            }
                        }
                    }
                }
            });

            holder.binding.chAfterNoon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                    {
                        afterNoonAttendance.add(data.getUserId());
                    }
                    else
                    {
                        if (afterNoonAttendance != null && afterNoonAttendance.size() > 0)
                        {
                            for (int i = 0 ;i<afterNoonAttendance.size();i++)
                            {
                                if (data.getUserId().equalsIgnoreCase(afterNoonAttendance.get(i)))
                                {
                                    afterNoonAttendance.remove(i);
                                }
                            }
                        }
                    }
                }
            });

            holder.binding.llMorning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (data.getAttendance() != null && data.getAttendance().size() > 0)
                    {
                        for (int i = 0;i<data.getAttendance().size();i++)
                        {
                            if (data.getAttendance().get(i).getSession().equalsIgnoreCase("morning"))
                            {
                                if (data.getAttendance().get(i).getAttendanceId() != null && !data.getAttendance().get(i).getAttendanceId().isEmpty())
                                {
                                    morningItem(data);
                                }
                            }
                        }
                    }
                }
            });

            holder.binding.llAfterNoon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (data.getAttendance() != null && data.getAttendance().size() > 0)
                    {
                        for (int i = 0;i<data.getAttendance().size();i++)
                        {
                            if (data.getAttendance().get(i).getSession().equalsIgnoreCase("afternoon"))
                            {
                                if (data.getAttendance().get(i).getAttendanceId() != null && !data.getAttendance().get(i).getAttendanceId().isEmpty())
                                {
                                    afternoonItem(data);
                                }
                            }
                        }
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return staffAttendData != null ? staffAttendData.size() : 0;
        }

        public void add(ArrayList<StaffAttendanceRes.StaffAttendData> staffAttendData) {
            this.staffAttendData = staffAttendData;
            notifyDataSetChanged();
        }

        public ArrayList<String> getMorningAttendance() {
           return morningAttendance;
        }

        public ArrayList<String> getAfterNoonAttendance() {
            return afterNoonAttendance;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ItemStaffAttendanceBinding binding;
            public ViewHolder(@NonNull ItemStaffAttendanceBinding itemView) {
                super(itemView.getRoot());
                binding = itemView;
            }
        }
    }
    Dialog dialogMorning;
    public void morningItem(StaffAttendanceRes.StaffAttendData data)
    {

        boolean[] isEdit = {true};

        dialogMorning = new Dialog(StaffAttendanceActivity.this, R.style.AppDialog);
        dialogMorning.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogMorning.setContentView(R.layout.dialog_change_staff_attendance);

        TextView staffName = (TextView) dialogMorning.findViewById(R.id.tvStaffName);
        TextView tvSession = (TextView) dialogMorning.findViewById(R.id.tvSession);
        Spinner spAttend = (Spinner) dialogMorning.findViewById(R.id.spAttend);
        Button btnEdit = (Button) dialogMorning.findViewById(R.id.btnEdit);

        staffName.setText(data.getName());
        tvSession.setText(data.getAttendance().get(0).getSession());

        ArrayAdapter<String> ad = new ArrayAdapter<String>(this, R.layout.item_spinner_new_disable, R.id.tvItem, new String[]{"present","absent"});
        spAttend.setAdapter(ad);
        spAttend.setEnabled(false);

        if (data.getAttendance().get(0).getAttendance().equalsIgnoreCase("present"))
        {
            spAttend.setSelection(0);
        }
        else
        {
            spAttend.setSelection(1);
        }


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isEdit[0])
                {
                    isEdit[0] = false;

                    btnEdit.setText(getResources().getString(R.string.lbl_save));

                    ArrayAdapter<String> ad = new ArrayAdapter<String>(StaffAttendanceActivity.this, R.layout.item_spinner_new, R.id.tvItem, new String[]{"present","absent"});
                    spAttend.setAdapter(ad);
                    spAttend.setEnabled(true);

                    if (data.getAttendance().get(0).getAttendance().equalsIgnoreCase("present"))
                    {
                        spAttend.setSelection(0);
                    }
                    else
                    {
                        spAttend.setSelection(1);
                    }
                }
                else
                {
                    isEdit[0] = true;

                    showLoadingBar(binding.progressBar,false);
                    ChangeStaffAttendanceReq req = new ChangeStaffAttendanceReq();
                    req.setDay(String.valueOf(day));
                    req.setMonth(String.valueOf(month));
                    req.setYear(String.valueOf(year));
                    req.setSession(tvSession.getText().toString());
                    req.setAttendance(spAttend.getSelectedItem().toString());
                    req.setAttendanceId(data.getAttendance().get(0).getAttendanceId());
                    req.setUserId(data.getUserId());
                    Log.e(TAG,"req "+new Gson().toJson(req));

                    leafManager.changeStaffAttendance(StaffAttendanceActivity.this,GroupDashboardActivityNew.groupId,req);

                }

            }
        });

        dialogMorning.show();

    }
    Dialog dialogAfterNoon;
    public void afternoonItem(StaffAttendanceRes.StaffAttendData data)
    {
        boolean[] isEdit = {true};

        dialogAfterNoon = new Dialog(StaffAttendanceActivity.this, R.style.AppDialog);
        dialogAfterNoon.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAfterNoon.setContentView(R.layout.dialog_change_staff_attendance);

        TextView staffName = (TextView) dialogAfterNoon.findViewById(R.id.tvStaffName);
        TextView tvSession = (TextView) dialogAfterNoon.findViewById(R.id.tvSession);
        Spinner spAttend = (Spinner) dialogAfterNoon.findViewById(R.id.spAttend);
        Button btnEdit = (Button) dialogAfterNoon.findViewById(R.id.btnEdit);

        staffName.setText(data.getName());
        tvSession.setText(data.getAttendance().get(1).getSession());

        ArrayAdapter<String> ad = new ArrayAdapter<String>(this, R.layout.item_spinner_new_disable, R.id.tvItem, new String[]{"present","absent"});
        spAttend.setAdapter(ad);
        spAttend.setEnabled(false);

        if (data.getAttendance().get(1).getAttendance().equalsIgnoreCase("present"))
        {
            spAttend.setSelection(0);
        }
        else
        {
            spAttend.setSelection(1);
        }


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isEdit[0])
                {
                    isEdit[0] = false;

                    btnEdit.setText(getResources().getString(R.string.lbl_save));

                    ArrayAdapter<String> ad = new ArrayAdapter<String>(StaffAttendanceActivity.this, R.layout.item_spinner_new, R.id.tvItem, new String[]{"present","absent"});
                    spAttend.setAdapter(ad);
                    spAttend.setEnabled(true);

                    if (data.getAttendance().get(1).getAttendance().equalsIgnoreCase("present"))
                    {
                        spAttend.setSelection(0);
                    }
                    else
                    {
                        spAttend.setSelection(1);
                    }
                }
                else
                {
                    isEdit[0] = true;

                    showLoadingBar(binding.progressBar,false);
                    ChangeStaffAttendanceReq req = new ChangeStaffAttendanceReq();
                    req.setDay(String.valueOf(day));
                    req.setMonth(String.valueOf(month));
                    req.setYear(String.valueOf(year));
                    req.setSession(tvSession.getText().toString());
                    req.setAttendance(spAttend.getSelectedItem().toString());
                    req.setAttendanceId(data.getAttendance().get(1).getAttendanceId());
                    req.setUserId(data.getUserId());

                    Log.e(TAG,"req "+new Gson().toJson(req));

                    leafManager.changeStaffAttendance(StaffAttendanceActivity.this,GroupDashboardActivityNew.groupId,req);

                }

            }
        });

        dialogAfterNoon.show();
    }
}