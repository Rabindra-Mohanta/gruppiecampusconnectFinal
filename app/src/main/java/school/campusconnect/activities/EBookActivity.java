package school.campusconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.EBookListFragment;

public class EBookActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;
    private Menu menu;
    private boolean IsFromHome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_register_e_book));
        if(getIntent()!=null && getIntent().hasExtra("IsFromHome")){
            IsFromHome = getIntent().getBooleanExtra("IsFromHome",false);
            setTitle(getIntent().getStringExtra("title"));
            if(menu!=null){
                menu.findItem(R.id.menu_add).setVisible(false);
            }
        }

        EBookListFragment classListFragment=new EBookListFragment();
        classListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,classListFragment).commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!IsFromHome){
            getMenuInflater().inflate(R.menu.menu_ebook,menu);
        }
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add:
                startActivity(new Intent(this,AddEBookActivity.class));
                return true;
             default:
                 return super.onOptionsItemSelected(item);
        }
    }
}
