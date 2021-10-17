package school.campusconnect.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.adapters.PaidDateAdapter;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.fees.DueDates;
import school.campusconnect.datamodel.fees.StudentFeesRes;
import school.campusconnect.fragments.FeesClassListFragment;
import school.campusconnect.fragments.PaidFeesFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppDialog;
import school.campusconnect.utils.AppLog;

public class StudentFeesActivity extends BaseActivity {

    private static final String TAG = "StudentFeesActivity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;

    @Bind(R.id.etTotalFees)
    public EditText etTotalFees;
    @Bind(R.id.etTotalPaid)
    public EditText etTotalPaid;
    @Bind(R.id.etDueAmount)
    public EditText etDueAmount;
    @Bind(R.id.btnPay)
    public Button btnPay;
    @Bind(R.id.rvPaid)
    public RecyclerView rvPaid;
    @Bind(R.id.rvDueDates)
    public RecyclerView rvDueDates;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    String team_id;
    String user_id;
    String groupId;
    DueDateAdapter dueDateAdapter;
    PaidDateAdapter paidDateAdapter = new PaidDateAdapter();

    StudentFeesRes.StudentFees resData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_fees);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getIntent().getStringExtra("title"));

        groupId = getIntent().getStringExtra("groupId");
        team_id = getIntent().getStringExtra("team_id");
        user_id = getIntent().getStringExtra("user_id");
        AppLog.e(TAG, "team_id : " + team_id);
        AppLog.e(TAG, "user_id : " + user_id);

        _init();

        getStudentFeesDetail();
    }

    private void _init() {
        dueDateAdapter = new DueDateAdapter();
        rvDueDates.setAdapter(dueDateAdapter);
        rvPaid.setAdapter(paidDateAdapter);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resData!=null){
                    Intent intent = new Intent(StudentFeesActivity.this, StudentFeesPayActivity.class);
                    intent.putExtra("title",getIntent().getStringExtra("title"));
                    intent.putExtra("groupId",groupId);
                    intent.putExtra("team_id",team_id);
                    intent.putExtra("user_id",user_id);
                    intent.putExtra("resData",new Gson().toJson(resData));
                    startActivity(intent);
                }
            }
        });
    }

    private void getStudentFeesDetail() {
        LeafManager leafManager = new LeafManager();
        progressBar.setVisibility(View.VISIBLE);
        leafManager.getStudentFees(this, groupId, team_id, user_id);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

        StudentFeesRes res = (StudentFeesRes) response;
        if (res.getData().size() > 0) {
            resData = res.getData().get(0);

            etTotalPaid.setText(TextUtils.isEmpty(resData.totalAmountPaid) ? "0" : resData.totalAmountPaid);
            etTotalFees.setText(resData.totalFee);
            try {
                if(!TextUtils.isEmpty(resData.totalFee) && !TextUtils.isEmpty(resData.totalAmountPaid)){
                    int dueAmount = Integer.parseInt(resData.totalFee) - Integer.parseInt(resData.totalAmountPaid);
                    etDueAmount.setText(dueAmount+"");
                }else {
                    etDueAmount.setText(resData.totalFee);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            dueDateAdapter.addList(resData.dueDates);
            paidDateAdapter.addList(resData.feePaidDetails);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class DueDateAdapter extends RecyclerView.Adapter<DueDateAdapter.ViewHolder> {
        private Context mContext;
        ArrayList<DueDates> list = new ArrayList<>();

        @NonNull
        @Override
        public DueDateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_due_date, parent, false);
            return new DueDateAdapter.ViewHolder(view);
        }

        public void addList(ArrayList<DueDates> list) {
            if (list == null)
                return;

            this.list.clear();
            this.list.addAll(list);
            notifyDataSetChanged();
        }

        public void add(DueDates dueDate) {
            list.add(dueDate);
            notifyDataSetChanged();
        }

        public ArrayList<DueDates> getList() {
            return this.list;
        }

        @Override
        public void onBindViewHolder(@NonNull DueDateAdapter.ViewHolder holder, int i) {
            holder.etDate.setText(list.get(i).getDate());
            holder.etDateAmount.setText(list.get(i).getMinimumAmount());

            holder.imgDelete.setVisibility(View.GONE);
            holder.chkCompleted.setEnabled(false);

            if ("completed".equalsIgnoreCase(list.get(i).getStatus())) {
                holder.chkCompleted.setChecked(true);
            } else {
                holder.chkCompleted.setChecked(false);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.etDate)
            EditText etDate;
            @Bind(R.id.etDateAmount)
            EditText etDateAmount;
            @Bind(R.id.imgDelete)
            ImageView imgDelete;
            @Bind(R.id.chkCompleted)
            CheckBox chkCompleted;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                etDateAmount.setFocusable(false);
            }
        }
    }
}
