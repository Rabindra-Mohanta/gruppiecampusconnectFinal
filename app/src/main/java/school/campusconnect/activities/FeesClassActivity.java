package school.campusconnect.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
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
import school.campusconnect.datamodel.subjects.SubjectStaffResponse;
import school.campusconnect.fragments.FeesClassListFragment;
import school.campusconnect.fragments.PaidFeesFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.MixOperations;

public class FeesClassActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;

    @Bind(R.id.tabLayout)
    public TabLayout tabLayout;
    ArrayList<ClassResponse.ClassData> result = new ArrayList<>();
    PaidFeesFragment paidFeesFragment;
    String role;
    private Menu menu;
    String selectedClassId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fees_payment);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getIntent().getStringExtra("title"));

        role  = getIntent().getStringExtra("role");

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
                            menu.findItem(R.id.menu_filter).setVisible(true);
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,paidFeesFragment).commit();
                    }else{

                        if(menu!=null){
                            menu.findItem(R.id.menu_filter).setVisible(false);
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
            getDataLocally();
        }else {
            tabLayout.setVisibility(View.GONE);
            FeesClassListFragment classListFragment=new FeesClassListFragment();
            classListFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,classListFragment).commit();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if("admin".equalsIgnoreCase(role)){
            getMenuInflater().inflate(R.menu.menu_admin_fees,menu);
            this.menu = menu;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_filter){
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

        AttendanceSubjectAdapter subjectAdapter = new AttendanceSubjectAdapter(result);
        rvSubject.setAdapter(subjectAdapter);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(selectedClassId)) {
                    dialog.dismiss();
                    paidFeesFragment.callApi(selectedClassId);
                } else {
                    Toast.makeText(FeesClassActivity.this, "Select Class", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                selectedClassId = "";
                paidFeesFragment.callApi(selectedClassId);
            }
        });
        dialog.show();
    }

    public class AttendanceSubjectAdapter extends RecyclerView.Adapter<AttendanceSubjectAdapter.ViewHolder> {
        private ArrayList<ClassResponse.ClassData> listSubject;
        private Context mContext;

        public AttendanceSubjectAdapter(ArrayList<ClassResponse.ClassData> listSubject) {
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
            ClassResponse.ClassData itemList = listSubject.get(position);
            holder.tvName.setText(itemList.getName());
            holder.tvStaff.setVisibility(View.GONE);
            if ((selectedClassId+"").equalsIgnoreCase(itemList.getId())) {
                holder.chkAttendance.setChecked(true);
            } else {
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
                        selectedClassId = listSubject.get(getAdapterPosition()).getId();
                        notifyDataSetChanged();
                    }
                });
            }
        }

    }


    private void getDataLocally() {
        List<ClassListTBL> list = ClassListTBL.getAll(GroupDashboardActivityNew.groupId);
        if (list.size() != 0) {
            result.clear();
            for (int i = 0; i < list.size(); i++) {
                ClassListTBL currentItem = list.get(i);
                ClassResponse.ClassData item = new ClassResponse.ClassData();
                item.id = currentItem.teamId;
                item.teacherName = currentItem.teacherName;
                item.phone = currentItem.phone;
                item.members = currentItem.members;
                item.countryCode = currentItem.countryCode;
                item.className = currentItem.name;
                item.classImage = currentItem.image;
                item.category = currentItem.category;
                item.jitsiToken = currentItem.jitsiToken;
                item.userId = currentItem.userId;
                item.rollNumber = currentItem.rollNumber;
                result.add(item);
            }

            TeamCountTBL dashboardCount = TeamCountTBL.getByTypeAndGroup("ALL", GroupDashboardActivityNew.groupId);
            if (dashboardCount != null) {
                boolean apiCall = false;
                if (dashboardCount.lastApiCalled != 0) {
                    if (MixOperations.isNewEvent(dashboardCount.lastInsertedTeamTime, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", dashboardCount.lastApiCalled)) {
                        apiCall = true;
                    }
                }
                if (dashboardCount.oldCount != dashboardCount.count) {
                    dashboardCount.oldCount = dashboardCount.count;
                    dashboardCount.save();
                    apiCall = true;
                }

                if (apiCall) {
                    apiCall(false);
                }
            }
        } else {
            apiCall(true);
        }
    }
    private void apiCall(boolean isLoading) {
        LeafManager leafManager = new LeafManager();
        leafManager.getClasses(this, GroupDashboardActivityNew.groupId);
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
        ClassResponse res = (ClassResponse) response;
        result = res.getData();
        AppLog.e("FeesClassActivity", "ClassResponse " + result);
    }
}
