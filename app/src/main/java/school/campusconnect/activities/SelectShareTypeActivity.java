package school.campusconnect.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import school.campusconnect.utils.AppLog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.utils.Constants;

/**
 * Created by frenzin04 on 2/8/2017.
 */

public class SelectShareTypeActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolBar;

    @Bind(R.id.btn_next_share)
    Button btn_next;


    @Bind(R.id.rb_to_groups)
    TextView rb_to_groups;

    @Bind(R.id.rb_to_teams)
    TextView rb_to_teams;

    @Bind(R.id.rb_to_friends)
    TextView rb_to_friends;

    String group_id;
    String post_id;
    String selected_group_id;

    boolean is_edit;

    String type;
    String share;
    String title;
    String desc;

    public static Activity selectShareTypeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_select_type);
        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);
        setBackEnabled(true);

        selectShareTypeActivity = this;

        setTitle(R.string.lbl_select_type);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }

        if (getIntent().getExtras() != null) {
            group_id = getIntent().getExtras().getString("id");
            post_id = getIntent().getExtras().getString("post_id");
            selected_group_id = getIntent().getExtras().getString("selected_group_id");
            type = getIntent().getExtras().getString("type", "");
            share = getIntent().getExtras().getString("share", "");
            is_edit = getIntent().getExtras().getBoolean("is_edit", false);
            title = getIntent().getExtras().getString("title", "");
            desc = getIntent().getExtras().getString("desc", "");
           AppLog.e("SHARE", "Post id3 is " + post_id);
           AppLog.e("SHAREDATA", "5group_id " + group_id);
           AppLog.e("SHAREDATA", "5post_id " + post_id);
           AppLog.e("SHAREDATA", "5selected_group_id " + selected_group_id);
        }

        rb_to_groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               AppLog.e("SelectShareType", "click to group");

                Intent intent = new Intent(SelectShareTypeActivity.this, ShareGroupListActivity.class);
                intent.putExtra("id", group_id);
                intent.putExtra("post_id", post_id);
                intent.putExtra("share", 1);
                intent.putExtra("type", "group");
                Constants.requestCode = Constants.notFinishCode;
                Constants.finishActivity = false;
                startActivity(intent);
//                SelectShareTypeActivity.this.finish();
            }
        });

        rb_to_teams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               AppLog.e("SelectShareType", "click to team");
                Intent intent = new Intent(SelectShareTypeActivity.this, ShareGroupTeamListActivity.class);
                intent.putExtra("id", group_id);
                intent.putExtra("post_id", post_id);
                intent.putExtra("type", "team");
               AppLog.e("SHAREDATA", "1group_id " + group_id);
               AppLog.e("SHAREDATA", "1post_id " + post_id);
//                startActivityForResult(intent, 0);
                Constants.requestCode = Constants.notFinishCode;
                startActivity(intent);
//                SelectShareTypeActivity.this.finish();
            }
        });

        rb_to_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               AppLog.e("SelectShareType", "click to friend");
//                Toast.makeText(getApplicationContext(), "Coming soon", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SelectShareTypeActivity.this, ShareInPersonalActivity.class);
                intent.putExtra("id", group_id);
                intent.putExtra("post_id", post_id);
                intent.putExtra("share", 3);
                intent.putExtra("type", "personal");
                Constants.requestCode = Constants.notFinishCode;
                Constants.finishActivity = false;
                startActivity(intent);
//                SelectShareTypeActivity.this.finish();
            }
        });

        /*btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (getSelectedRadioButton()){

                    case 0:
                        Toast.makeText(SelectShareTypeActivity.this, "Select any type first", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Intent intent = new Intent(SelectShareTypeActivity.this, ShareGroupListActivity.class);
                        intent.putExtra("id", id);
                        intent.putExtra("post_id", post_id);
                        intent.putExtra("share", getSelectedRadioButton());
                        intent.putExtra("type", "group");
                        startActivity(intent);
                        SelectShareTypeActivity.this.finish();
                        break;
                    case 2:
                        intent = new Intent(SelectShareTypeActivity.this, ShareGroupTeamListActivity.class);
                        intent.putExtra("id", id);
                        intent.putExtra("post_id", post_id);
                       AppLog.e("SHAREDATA", "1group_id " + id);
                       AppLog.e("SHAREDATA", "1post_id " + post_id);
                        startActivity(intent);
                        SelectShareTypeActivity.this.finish();
                        break;
                    case 3:
                        break;

                }
            }
        });*/
    }

    /*public int getSelectedRadioButton() {

        int selected_btn_flag = 0;

        try {
            int selectedId = radioGroup.getCheckedRadioButtonId();

            RadioButton selectedBtn = (RadioButton) findViewById(selectedId);

            if (selectedBtn.getText().toString().equalsIgnoreCase(getString(R.string.to_groups))) {
                selected_btn_flag = 1;
            } else if (selectedBtn.getText().toString().equalsIgnoreCase(getString(R.string.to_teams))) {
                selected_btn_flag = 2;
            } else if (selectedBtn.getText().toString().equalsIgnoreCase(getString(R.string.to_friends))) {
                selected_btn_flag = 3;
            }
        } catch (NullPointerException e) {
           AppLog.e("SelectType", "error is " + e.toString());
        }
        return selected_btn_flag;
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       AppLog.e("onActivityResult", "called requestCode " + requestCode + " resultCode " + resultCode);
        if (resultCode == Constants.finishCode)
            this.finish();
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
        }
    }

}
