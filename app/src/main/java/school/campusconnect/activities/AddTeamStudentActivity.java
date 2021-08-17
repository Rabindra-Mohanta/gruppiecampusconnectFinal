package school.campusconnect.activities;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.AddTeamClassListFragment;
import school.campusconnect.fragments.AddTeamStudentListFragment;

public class AddTeamStudentActivity extends BaseActivity {

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
        setTitle(getResources().getString(R.string.lbl_add_students));


        AddTeamClassListFragment classListFragment=new AddTeamClassListFragment();
        classListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,classListFragment).commit();



    }
    AddTeamStudentListFragment classListFragment;
    public void next(Bundle bundle){
        classListFragment=new AddTeamStudentListFragment();
        classListFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,classListFragment).addToBackStack("aa").commit();
    }
    public void enableSelection(boolean isShow){
        if(isShow){
            findViewById(R.id.cardAll).setVisibility(View.VISIBLE);
        }else {
            findViewById(R.id.cardAll).setVisibility(View.GONE);
        }

        CheckBox chkAll = findViewById(R.id.chkAll);
        chkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(classListFragment!=null){
                    classListFragment.selectAll(chkAll.isChecked());
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
