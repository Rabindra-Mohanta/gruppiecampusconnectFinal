package school.campusconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.school.AddClassStudentV2Activity;
import school.campusconnect.activities.school.AddStaffV2Activity;
import school.campusconnect.fragments.AddTeamStaffListFragment;

public class AddTeamStaffActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_add_staff));


        AddTeamStaffListFragment classListFragment=new AddTeamStaffListFragment();
        classListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,classListFragment).commit();


        findViewById(R.id.cardAll).setVisibility(View.VISIBLE);
        findViewById(R.id.imgAddStudent).setVisibility(View.VISIBLE);

        ImageView imgAddStudent = findViewById(R.id.imgAddStudent);

        imgAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddStaffV2Activity.class);
                startActivity(i);
            }
        });

        CheckBox chkAll = findViewById(R.id.chkAll);
        chkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classListFragment.selectAll(chkAll.isChecked());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
