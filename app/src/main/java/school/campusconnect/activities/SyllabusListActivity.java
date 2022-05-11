package school.campusconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.HWSubjectListFragment;
import school.campusconnect.fragments.SyllabusListFragment;

public class SyllabusListActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus_list);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getIntent().getExtras().getString("title"));

        SyllabusListFragment syllabusListFragment=new SyllabusListFragment();
        syllabusListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,syllabusListFragment).commit();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menuAdd:
                Intent intent = new Intent(getApplicationContext(), AddSyllabusActivity.class);
                intent.putExtra("team_id", getIntent().getExtras().getString("team_id"));
                intent.putExtra("subject_id", getIntent().getExtras().getString("subject_id"));
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}