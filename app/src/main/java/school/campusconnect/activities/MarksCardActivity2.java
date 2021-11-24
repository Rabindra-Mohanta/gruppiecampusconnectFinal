package school.campusconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.MarkCardFragment2;
import school.campusconnect.fragments.TimeTableListFragment2;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people2);

        ButterKnife.bind(this);

        role = getIntent().getStringExtra("role");
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


        MarkCardFragment2 classListFragment=new MarkCardFragment2();
        classListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,classListFragment).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
