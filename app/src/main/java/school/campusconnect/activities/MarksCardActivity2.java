package school.campusconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.staff.AddStaffRole;
import school.campusconnect.fragments.MarkCardFragment2;
import school.campusconnect.fragments.TimeTableListFragment2;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;

public class MarksCardActivity2 extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;

    @Bind(R.id.iconBack)
    public ImageView iconBack;


    @Bind(R.id.imgHome)
    public ImageView imgHome;

    String role;
    boolean isExaminer;
    String userId,teamId;
    private Menu menu;
    MarkCardFragment2 classListFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people2);

        ButterKnife.bind(this);

        role = getIntent().getStringExtra("role");
        userId = getIntent().getStringExtra("userId");
        teamId = getIntent().getStringExtra("team_id");
        isExaminer=getIntent().getBooleanExtra("examiner",false);
        setTitle(getIntent().getStringExtra("team_name"));

        setSupportActionBar(mToolBar);
        setBackEnabled(false);
        tvTitle.setText(getIntent().getStringExtra("team_name"));
        setTitle("");

        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MarksCardActivity2.this,GroupDashboardActivityNew.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        classListFragment=new MarkCardFragment2();
        classListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,classListFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      if (isExaminer) {
          getMenuInflater().inflate(R.menu.menu_isapproved, menu);
          this.menu = menu;
      }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_isapprove:
                if (classListFragment.isTestApprove){
                    callApproveNotApi();
                }else {
                    callApproveApi();
                }



                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void getSpinnerValue(boolean isTestApprove){
       MenuItem bedMenuItem = menu.findItem(R.id.menu_isapprove);
       if (isTestApprove) {
            bedMenuItem.setTitle(getResources().getString(R.string.lbl_not_approve));
        } else {
            bedMenuItem.setTitle(getResources().getString(R.string.lbl_approve));
       }
    }

    private void callApproveApi() {


            LeafManager leafManager = new LeafManager();


            leafManager.examinerApproved(this, GroupDashboardActivityNew.groupId, teamId, String.valueOf(classListFragment.selectedTestId));
            Toast.makeText(this, "Status Changed to Approve Successfully", Toast.LENGTH_SHORT).show();


            finish();


    }
    private void callApproveNotApi() {


        LeafManager leafManager = new LeafManager();


        leafManager.examinerNotApproved(this, GroupDashboardActivityNew.groupId, teamId, String.valueOf(classListFragment.selectedTestId));
        Toast.makeText(this, "Status Changed to Not Approve Successfully", Toast.LENGTH_SHORT).show();

        finish();

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
