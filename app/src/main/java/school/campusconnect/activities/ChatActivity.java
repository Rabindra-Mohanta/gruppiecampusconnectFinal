package school.campusconnect.activities;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import androidx.fragment.app.FragmentTransaction;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.personalchat.PersonalPostItem;
import school.campusconnect.fragments.BasePersonalFragment;
import school.campusconnect.fragments.PersonalPostsFragmentNew;

public class ChatActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    public String role="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        role= getIntent().getStringExtra("role");
        init();

        BasePersonalFragment basePersonalFragment = new BasePersonalFragment();
        basePersonalFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,basePersonalFragment).commit();
    }

    public void changePersonalFrag(PersonalPostItem personalPostItem) {
        setTitle(personalPostItem.name);
        PersonalPostsFragmentNew fragPersonalPost = PersonalPostsFragmentNew.newInstance(GroupDashboardActivityNew.groupId, personalPostItem);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragPersonalPost);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void init() {
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_chat));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setTitle(getResources().getString(R.string.lbl_chat));
    }
}
