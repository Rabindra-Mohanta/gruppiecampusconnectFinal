package school.campusconnect.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ClassListTBL;
import school.campusconnect.datamodel.TeamCountTBL;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.staff.AddStaffRole;
import school.campusconnect.datamodel.staff.StaffResponse;
import school.campusconnect.datamodel.subjects.SubjectStaffResponse;
import school.campusconnect.datamodel.syllabus.SyllabusListModelRes;
import school.campusconnect.fragments.FeesClassListFragment;
import school.campusconnect.fragments.PaidFeesFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.MixOperations;

public class FeesClassActivity extends BaseActivity implements LeafManager.OnCommunicationListener {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;


    @Bind(R.id.tabLayout)
    public TabLayout tabLayout;


    private ArrayList<String> StaffIdList=new ArrayList<>();
    ArrayList<StaffResponse.StaffData> result = new ArrayList<>();

    PaidFeesFragment paidFeesFragment;
    String role;
    private Menu menu;
    String selectedClassId="";
    private  Boolean isaccountant=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fees_payment);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);

        setBackEnabled(true);
        setTitle(getIntent().getStringExtra("title"));

        role  = getIntent().getStringExtra("role");
        isaccountant=getIntent().getBooleanExtra("accountant",false);

        if("admin".equalsIgnoreCase(role)){
            tabLayout.setVisibility(View.VISIBLE);
            FeesClassListFragment classListFragment=new FeesClassListFragment();
            classListFragment.setArguments(getIntent().getExtras());

            paidFeesFragment =new PaidFeesFragment();
            paidFeesFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,paidFeesFragment).commit();

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if(tab.getPosition()==0){
                        if(menu!=null){
                            menu.findItem(R.id.add_accountant).setVisible(true);
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,paidFeesFragment).commit();
                    }else{

                        if(menu!=null){
                            menu.findItem(R.id.add_accountant).setVisible(false);
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,classListFragment).commit();
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
           // getDataLocally();
            apiCall(true);
        }else {
            tabLayout.setVisibility(View.GONE);
            FeesClassListFragment classListFragment=new FeesClassListFragment();
            classListFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,classListFragment).commit();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if("admin".equalsIgnoreCase(role) &&  isaccountant==true ){
            getMenuInflater().inflate(R.menu.add_accountant,menu);


            this.menu = menu;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.add_accountant){
            if(result!=null){
                showClassSelectDialog();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showClassSelectDialog() {
        final Dialog dialog = new Dialog(this, R.style.FragmentDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_class);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);

        RecyclerView rvSubject = dialog.findViewById(R.id.rvSubjects);
        TextView title=dialog.findViewById(R.id.title);
        title.setText(getResources().getString(R.string.lbl_select_accountant));


        AttendanceSubjectAdapter subjectAdapter = new AttendanceSubjectAdapter(result);
        rvSubject.setAdapter(subjectAdapter);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                   callAccountantApi();
                    dialog.dismiss();


            }
        });

        dialog.show();
    }


    private void callAccountantApi() {
        AddStaffRole addStaffRoles=new AddStaffRole(StaffIdList);
        LeafManager leafManager=new LeafManager();

        leafManager.addSchoolStaffRole(this,GroupDashboardActivityNew.groupId,"accountant",addStaffRoles);
    }

    public class AttendanceSubjectAdapter extends RecyclerView.Adapter<AttendanceSubjectAdapter.ViewHolder> {
        private ArrayList<StaffResponse.StaffData> listSubject;
        private Context mContext;

        public AttendanceSubjectAdapter(ArrayList<StaffResponse.StaffData> listSubject) {
            this.listSubject = listSubject;
        }

        @Override
        public AttendanceSubjectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_attendance_subject_1, parent, false);
            return new AttendanceSubjectAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final AttendanceSubjectAdapter.ViewHolder holder, final int position) {
            StaffResponse.StaffData itemList = listSubject.get(position);
            holder.tvName.setText(itemList.getName());
            holder.tvStaff.setVisibility(View.GONE);
            if(itemList.isSelected){
                holder.chkAttendance.setChecked(true);
            }else {
                holder.chkAttendance.setChecked(false);
            }
        }

        @Override
        public int getItemCount() {
            return listSubject != null ? listSubject.size() : 0;
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
                chkAttendance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        chkAttendance.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                               listSubject.get(getAdapterPosition()).isSelected = chkAttendance.isChecked();
                                StaffIdList.add(  listSubject.get(getAdapterPosition()).staffId);


                            }
                        });




                    }
                });
            }
        }

    }


    private void apiCall(boolean isLoading) {
        LeafManager leafManager = new LeafManager();

        leafManager.getStaff(this, GroupDashboardActivityNew.groupId);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(paidFeesFragment!=null && tabLayout.getSelectedTabPosition()==0){
            paidFeesFragment.callApi(selectedClassId);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);

        switch (apiId)

        {

            case  LeafManager.API_STAFF:

                StaffResponse res = (StaffResponse) response;
                result = res.getData();
                AppLog.e("FeesClassActivity", "ClassResponse " + result);
                     break;

            case LeafManager.ADD_SCHOOL_ACCOUNTANT:
                Toast.makeText(getApplicationContext(),"Add Success",Toast.LENGTH_LONG).show();

                break;
        }


    }
}
