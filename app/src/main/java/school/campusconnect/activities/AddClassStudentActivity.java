package school.campusconnect.activities;


import android.content.DialogInterface;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;

import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;

import school.campusconnect.R;
import school.campusconnect.adapters.PagerAdapterOfAddStudent;

import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.student.StudentRes;

import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.UploadImageFragment;
import school.campusconnect.utils.WrappingViewPager;
import school.campusconnect.views.SMBDialogUtils;


public class AddClassStudentActivity extends BaseActivity implements  LeafManager.OnCommunicationListener {
    private static final String TAG = "AddStudentActivity";
     @Bind(R.id.progressBar)
    ProgressBar progressBar;
    TabLayout tabLayout;
    WrappingViewPager viewPager2;
   public  FragmentManager fragmentManager;
    PagerAdapterOfAddStudent pagerAdapterOfAddStudent;

    Toolbar toolbar;

   public static String group_id, team_id,userId,gruppieRollNoNumber;
    public static boolean isEdit=false;
   public static StudentRes.StudentData studentData;
   public static boolean submitted = false;
    LeafManager leafManager;
    public static UploadImageFragment imageFragment;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_class_student);
        ButterKnife.bind(AddClassStudentActivity.this);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setBackEnabled(true);
        setTitle(getResources().getString(R.string.title_add_student));

        init();
        setImageFragment();

    }






    private void init()
    {
        studentData = new Gson().fromJson(getIntent().getStringExtra("student_data"), StudentRes.StudentData.class);
        leafManager = new LeafManager();
        group_id = getIntent().getStringExtra("group_id");
        team_id = getIntent().getStringExtra("team_id");
        isEdit = getIntent().getBooleanExtra("isEdit",false);


        tabLayout=findViewById(R.id.tabLayout);
        viewPager2=findViewById(R.id.viewPager);
        fragmentManager=getSupportFragmentManager();
        pagerAdapterOfAddStudent=new PagerAdapterOfAddStudent(fragmentManager);
        viewPager2.setAdapter(pagerAdapterOfAddStudent);


        viewPager2.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager2.setOffscreenPageLimit(0);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }


    private void setImageFragment() {


        if (isEdit) {
            imageFragment = UploadImageFragment.newInstance(studentData.getImage(), true, true);

            setTitle(getResources().getString(R.string.title_student_details)+" - ("+getIntent().getStringExtra("className")+")");
        }

        else {
            imageFragment = UploadImageFragment.newInstance(null, true, true);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, imageFragment).commit();
        getSupportFragmentManager().executePendingTransactions();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(isEdit)
        {
            getMenuInflater().inflate(R.menu.menu_edit,menu);
        }

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuDelete) {
            if (!isConnectionAvailable()) {
                showNoNetworkMsg();
                return true;
            }
            if (studentData == null)
                return true;

            SMBDialogUtils.showSMBDialogOKCancel(this, getResources().getString(R.string.smb_delete_student), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showLoadingBar(progressBar,false);
                    //   progressBar.setVisibility(View.VISIBLE);
                    leafManager.deleteClassStudent(AddClassStudentActivity.this, GroupDashboardActivityNew.groupId, team_id,studentData.getUserId());
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        switch (apiId)

        {
            case LeafManager.API_DELETE_STUDENTS:
                Toast.makeText(this, getResources().getString(R.string.toast_delete_student_sucess), Toast.LENGTH_SHORT).show();
                finish();
                break;

        }

    }
}
