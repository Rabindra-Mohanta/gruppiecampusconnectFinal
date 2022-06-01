package school.campusconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.HWClassListFragment;
import school.campusconnect.fragments.StaffFragmentDialog;
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
        getMenuInflater().inflate(R.menu.menu_syllabus_filter,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (getIntent().getStringExtra("type").equalsIgnoreCase("Syllabus Tracker"))
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
