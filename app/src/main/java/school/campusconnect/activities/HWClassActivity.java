package school.campusconnect.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.staff.AddStaffRole;
import school.campusconnect.datamodel.staff.StaffResponse;
import school.campusconnect.fragments.HWClassListFragment;
import school.campusconnect.fragments.StaffFragmentDialog;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.views.SearchIssueFragmentDialog;

public class HWClassActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;

    @Bind(R.id.iconBack)
    public ImageView iconBack;

    @Bind(R.id.imgHome)
    public ImageView imgHome;
    @Bind(R.id.txtEmpty)
    TextView txtEmpty;

    String role;
    private  Boolean isExaminer=false;
    private Menu menu;
    AttendanceSubjectAdapter adapter;
    RecyclerView rvSubject;
    Dialog dialog;
    ArrayList<StaffResponse.StaffData> result = new ArrayList<>();

    private StaffFragmentDialog staffFragmentDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people2);

        staffFragmentDialog = StaffFragmentDialog.newInstance();


        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(false);
        tvTitle.setText(getIntent().getStringExtra("title"));
        role  = getIntent().getStringExtra("role");
        isExaminer=getIntent().getBooleanExtra("examiner",false);
        setTitle("");
        if("admin".equalsIgnoreCase(role) &&  isExaminer)
        {
            apiCall(true);
        }
        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HWClassActivity.this,GroupDashboardActivityNew.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        HWClassListFragment classListFragment=new HWClassListFragment();
        classListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,classListFragment).commit();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if("admin".equalsIgnoreCase(role) &&  isExaminer ){
            getMenuInflater().inflate(R.menu.add_examiner,menu);



            this.menu = menu;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.add_examiner){

            if(result!=null){

                showClassSelectDialog();

            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showClassSelectDialog() {
         dialog = new Dialog(this, R.style.FragmentDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_class);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);

        rvSubject = dialog.findViewById(R.id.rvSubjects);
        TextView title=dialog.findViewById(R.id.title);
        title.setText(getResources().getString(R.string.lbl_select_examiner));


        adapter = new AttendanceSubjectAdapter(result);
        rvSubject.setAdapter(adapter);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (adapter.getSelectedList().isEmpty() || adapter.getItemCount()==0){
                    Snackbar.make(dialog.getWindow().getDecorView(), "Please Select Examiner", Snackbar.LENGTH_LONG).show();
                   // Toast.makeText(dialog.getContext(),"Add Accountant",Toast.LENGTH_LONG).show();
                }else{
                    callAccountantApi();

                }
  }
        });

        dialog.show();
    }


    private void callAccountantApi() {


        if (adapter != null) {


            AddStaffRole addStaffRoles = new AddStaffRole();
            addStaffRoles.setStaffId(adapter.getSelectedList());
            LeafManager leafManager = new LeafManager();


            AppLog.e("TAG", "request :" + addStaffRoles);
            leafManager.addSchoolStaffRole(this, GroupDashboardActivityNew.groupId, "examiner", addStaffRoles);
            // Toast.makeText(HWClassActivity.this, "Examiner added successfully", Toast.LENGTH_SHORT).show();
            Snackbar.make(findViewById(R.id.fragment_container), "Examiner added successfully", Snackbar.LENGTH_LONG).show();

            dialog.dismiss();
        }

    }

    public class AttendanceSubjectAdapter extends RecyclerView.Adapter<AttendanceSubjectAdapter.ViewHolder> {
        List<StaffResponse.StaffData> list;
        private ArrayList<String> staffArray ;
        private Context mContext;

        public AttendanceSubjectAdapter(ArrayList<StaffResponse.StaffData> list) {
            this.list = list;
        }

        @Override
        public AttendanceSubjectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_attendance_subject_1, parent, false);
            return new AttendanceSubjectAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final AttendanceSubjectAdapter.ViewHolder holder, final int position) {
            StaffResponse.StaffData itemList = list.get(position);
            holder.tvName.setText(itemList.getName());
            holder.tvStaff.setVisibility(View.GONE);
            if (itemList.isSelected) {
                holder.chkAttendance.setChecked(true);
            } else {
                holder.chkAttendance.setChecked(false);
            }
            holder.chkAttendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemList.isSelected = holder.chkAttendance.isChecked();
                    notifyItemChanged(position);
                }
            });
        }
        public ArrayList<String> getSelectedList() {
            ArrayList<String> selected = new ArrayList<>();
            if (list == null) {
                return selected;
            }
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isSelected)
                    selected.add(list.get(i).getStaffId());
            }
            return selected;
        }


        @Override
        public int getItemCount() {
            return list != null ? list.size() : 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.chkAttendance)
            CheckBox chkAttendance;

            @Bind(R.id.tvName)
            TextView tvName;

            @Bind(R.id.tvStaff)
            TextView tvStaff;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }


        }


    }

    private void apiCall(boolean isLoading) {
        LeafManager leafManager = new LeafManager();

        leafManager.getStaff(this, GroupDashboardActivityNew.groupId);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);

        switch (apiId)

        {

            case  LeafManager.API_STAFF:

                StaffResponse res = (StaffResponse) response;
                result = res.getData();
                AppLog.e("HWClassActivity", "ClassResponse " + result);
                break;

        }


    }
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_syllabus_filter,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (getIntent().getStringExtra("type").equalsIgnoreCase("Syllabus Tracker") && getIntent().getStringExtra("role").equalsIgnoreCase("admin"))
        {
            menu.findItem(R.id.menu_filter_staff).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_filter_staff)
        {
            staffFragmentDialog.show(getSupportFragmentManager(),"");
            staffFragmentDialog.setData(getIntent().getStringExtra("type"),getIntent().getStringExtra("role"));
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
