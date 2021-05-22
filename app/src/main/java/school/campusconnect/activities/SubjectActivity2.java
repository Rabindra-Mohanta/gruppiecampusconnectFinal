package school.campusconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import school.campusconnect.R;
import school.campusconnect.fragments.SubjectListFragment;
import school.campusconnect.fragments.SubjectListFragment2;
import butterknife.Bind;
import butterknife.ButterKnife;

public class SubjectActivity2 extends BaseActivity {

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
        setTitle(getResources().getString(R.string.lbl_register_subject)+" - ("+getIntent().getStringExtra("className")+")");


        SubjectListFragment2 classListFragment=new SubjectListFragment2();
        classListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,classListFragment).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_class,menu);
        menu.findItem(R.id.menu_add_class).setTitle("Add Subject");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add_class:
                Intent intent = new Intent(this, AddSubjectActivity2.class);
                intent.putExtra("team_id",getIntent().getStringExtra("team_id"));
                intent.putExtra("className",getIntent().getStringExtra("className"));
                startActivity(intent);
                return true;
             default:
                 return super.onOptionsItemSelected(item);
        }
    }
}
