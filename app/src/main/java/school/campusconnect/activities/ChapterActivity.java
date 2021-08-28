package school.campusconnect.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.ChapterListFragment;
import school.campusconnect.views.SMBDialogUtils;

public class ChapterActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;

    boolean canPost;
    String team_id;
    String subject_id;
    String subject_name;
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getIntent().getStringExtra("title")+" - ("+getIntent().getStringExtra("className")+")");

        canPost = getIntent().getBooleanExtra("canPost",false);
        team_id = getIntent().getStringExtra("team_id");
        subject_id = getIntent().getStringExtra("subject_id");
        subject_name = getIntent().getStringExtra("subject_name");
        path = getIntent().getStringExtra("path");


        ChapterListFragment classListFragment=new ChapterListFragment();
        classListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,classListFragment).commit();

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (canPost) {
            getMenuInflater().inflate(R.menu.menu_chapter, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add_post) {
            Intent intent = new Intent(this,AddChapterPostActivity.class);
            intent.putExtra("group_id",GroupDashboardActivityNew.groupId);
            intent.putExtra("team_id",team_id);
            intent.putExtra("subject_id",subject_id);
            intent.putExtra("subject_name",subject_name);
            intent.putExtra("path",path);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.menu_delete_chapter) {
            ((ChapterListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container)).onDeleteChapterClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(updateReceiver,new IntentFilter("chapter_refresh"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(updateReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if("chapter_refresh".equalsIgnoreCase(intent.getAction())){
                ((ChapterListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container)).getChapters(true);
            }
        }
    };
}
