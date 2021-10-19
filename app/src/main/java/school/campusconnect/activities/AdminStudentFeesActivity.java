package school.campusconnect.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.Assymetric.AsymmetricRecyclerView;
import school.campusconnect.Assymetric.AsymmetricRecyclerViewAdapter;
import school.campusconnect.R;
import school.campusconnect.adapters.ChildAdapter;
import school.campusconnect.adapters.ChildHwAdapter;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.fees.DueDates;
import school.campusconnect.datamodel.fees.PaidStudentFeesRes;
import school.campusconnect.datamodel.fees.StudentFeesRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppDialog;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;

public class AdminStudentFeesActivity extends BaseActivity {

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
    @Bind(R.id.btnApprove)
    public Button btnApprove;
    @Bind(R.id.btnHold)
    public Button btnHold;
    @Bind(R.id.rvDueDates)
    public RecyclerView rvDueDates;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.etPaidAmount)
    EditText etPaidAmount;
    @Bind(R.id.etDate)
    EditText etDate;

    @Bind(R.id.recyclerView)
    AsymmetricRecyclerView recyclerView;
    String team_id;
    String user_id;
    String groupId;
    DueDateAdapter dueDateAdapter;

    PaidStudentFeesRes.StudentFees resData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_student_fees);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getIntent().getStringExtra("title"));

        groupId = getIntent().getStringExtra("groupId");
        team_id = getIntent().getStringExtra("team_id");
        user_id = getIntent().getStringExtra("user_id");
        resData = new Gson().fromJson(getIntent().getStringExtra("data"), PaidStudentFeesRes.StudentFees.class);
        AppLog.e(TAG, "team_id : " + team_id);
        AppLog.e(TAG, "user_id : " + user_id);

        _init();

        getStudentFeesDetail();
    }

    private void _init() {
        dueDateAdapter = new DueDateAdapter();
        rvDueDates.setAdapter(dueDateAdapter);

        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resData != null) {
                    AppDialog.showConfirmDialog(AdminStudentFeesActivity.this, "Are you sure you want to approve?", new AppDialog.AppDialogListener() {
                        @Override
                        public void okPositiveClick(DialogInterface dialog) {
                            LeafManager leafManager = new LeafManager();
                            progressBar.setVisibility(View.VISIBLE);
                            leafManager.approveOrHoldFees(AdminStudentFeesActivity.this, groupId, team_id, user_id, resData.paymentId, "approve");
                        }

                        @Override
                        public void okCancelClick(DialogInterface dialog) {

                        }
                    });


                }
            }
        });
        btnHold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resData != null) {
                    AppDialog.showConfirmDialog(AdminStudentFeesActivity.this, "Are you sure you want to hold?", new AppDialog.AppDialogListener() {
                        @Override
                        public void okPositiveClick(DialogInterface dialog) {
                            LeafManager leafManager = new LeafManager();
                            progressBar.setVisibility(View.VISIBLE);
                            leafManager.approveOrHoldFees(AdminStudentFeesActivity.this, groupId, team_id, user_id, resData.paymentId, "hold");
                        }

                        @Override
                        public void okCancelClick(DialogInterface dialog) {

                        }
                    });
                }
            }
        });
    }

    private void getStudentFeesDetail() {
        if (resData != null) {

            if("onHold".equalsIgnoreCase(resData.status)){
                btnApprove.setVisibility(View.VISIBLE);
                btnHold.setVisibility(View.GONE);
            }else if("approved".equalsIgnoreCase(resData.status)){
                btnApprove.setVisibility(View.GONE);
                btnHold.setVisibility(View.GONE);
            }else {
                btnApprove.setVisibility(View.VISIBLE);
                btnHold.setVisibility(View.VISIBLE);
            }

            etTotalPaid.setText(TextUtils.isEmpty(resData.totalAmountPaid) ? "0" : resData.totalAmountPaid);
            etTotalFees.setText(resData.totalFee);
            try {
                if (!TextUtils.isEmpty(resData.totalFee) && !TextUtils.isEmpty(resData.totalAmountPaid)) {
                    int dueAmount = Integer.parseInt(resData.totalFee) - Integer.parseInt(resData.totalAmountPaid);
                    etDueAmount.setText(dueAmount + "");
                } else {
                    etDueAmount.setText(resData.totalFee);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            dueDateAdapter.addList(resData.dueDates);

            etPaidAmount.setText(resData.amountPaid);
            etDate.setText(resData.paidDate);

            if (resData.paidImageAttachment != null && resData.paidImageAttachment.size() > 0) {
                ChildAdapter adapter;
                if (resData.paidImageAttachment.size() == 3) {
                    adapter = new ChildAdapter(2, resData.paidImageAttachment.size(), this, resData.paidImageAttachment);
                } else {
                    adapter = new ChildAdapter(Constants.MAX_IMAGE_NUM, resData.paidImageAttachment.size(), this, resData.paidImageAttachment);
                }
                recyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(this, recyclerView, adapter));
            }

        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        Toast.makeText(this, "Status successfully updated", Toast.LENGTH_SHORT).show();
        finish();
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
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_due_date, parent, false);
            return new ViewHolder(view);
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
        public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
            holder.etDate.setText(list.get(i).getDate());
            holder.etDateAmount.setText(list.get(i).getMinimumAmount());

            holder.imgDelete.setVisibility(View.GONE);

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
