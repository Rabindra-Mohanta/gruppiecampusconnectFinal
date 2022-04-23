package school.campusconnect.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import school.campusconnect.utils.AppLog;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.AddPostRequestDescription;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.BaseValidationError;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.Constants;

public class EditPostActivity extends BaseActivity implements View.OnClickListener, LeafManager.OnAddUpdateListener<BaseValidationError>, LeafManager.OnCommunicationListener,
        DialogInterface.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar mToolBar;

    @Bind(R.id.et_title)
    EditText edtTitle;

    @Bind(R.id.et_description)
    EditText edtDesc;

    @Bind(R.id.img)
    ImageView img;

    @Bind(R.id.btn_update)
    ImageView btnUpdate;

    @Bind(R.id.btn_edit_post)
    Button btn_Post;

    @Bind(R.id.btn_delete_post)
    Button btn_delete_Post;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.btnShare)
    Button btnShare;

    String postType = "";
    String selected_ids = "";
    String shareType = "";

    String group_id = "";
    String selected_g_id = "";
    String post_id = "";
    String team_id = "";

    boolean checkDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);
        setBackEnabled(true);

        btn_Post.setVisibility(View.GONE);

        checkDesc = true;
        postType = "team";

        if (getIntent() != null) {
            group_id = getIntent().getExtras().getString("id");
            if (getIntent().hasExtra("type"))
                postType = getIntent().getExtras().getString("type");
            if (getIntent().hasExtra("post_id"))
                post_id = getIntent().getExtras().getString("post_id");
            if (getIntent().hasExtra("team_id"))
                team_id = getIntent().getExtras().getString("team_id");
            if (getIntent().hasExtra("sharetype"))
                shareType = getIntent().getExtras().getString("sharetype");
            if (getIntent().hasExtra("selected_g_id"))
                selected_g_id = getIntent().getExtras().getString("selected_g_id");
            if (getIntent().hasExtra("selected_ids"))
                selected_ids = getIntent().getExtras().getString("selected_ids");

//            edtTitle.setText(getIntent().getExtras().getString("title"));

           AppLog.e("EDITPOST", "title is " + getIntent().getExtras().getString("title", ""));
           AppLog.e("EDITPOST", "desc is " + getIntent().getExtras().getString("desc", ""));
           AppLog.e("EDITPOST", "desc is " + GroupDashboardActivityNew.share_desc);

            String title=getIntent().getExtras().getString("title", "");
            if(!TextUtils.isEmpty(title))
            {
                edtTitle.setVisibility(View.VISIBLE);
                edtTitle.setText(title);
            }
            else
            {
                edtTitle.setVisibility(View.GONE);
            }

            edtDesc.setText(getIntent().getExtras().getString("desc", ""));
           AppLog.e("desc", "desc is " + getIntent().getExtras().getString("desc"));

            if (GroupDashboardActivityNew.imageHeight != 0 && GroupDashboardActivityNew.imageWidth != 0) {
               AppLog.e("PostAdapter", "Height Image=>" + Constants.screen_width);
                int height = (Constants.screen_width * GroupDashboardActivityNew.imageHeight) / GroupDashboardActivityNew.imageWidth;
                img.getLayoutParams().height = height;
               AppLog.e("PostAdapter", "Item Width=>" + GroupDashboardActivityNew.imageWidth + ",Height=>" + GroupDashboardActivityNew.imageHeight);
               AppLog.e("PostAdapter", "Height Image=>" + height);
            }

            switch (GroupDashboardActivityNew.share_image_type) {
                case 1:
                    checkDesc = false;
                    if (GroupDashboardActivityNew.share_image != null && !GroupDashboardActivityNew.share_image.isEmpty())
                        Picasso.with(this).load(GroupDashboardActivityNew.share_image).into(img);
                    break;
                case 2:
                    checkDesc = false;
                    Picasso.with(this).load(R.drawable.pdf_thumbnail).into(img);
                    break;
                case 3:
                    checkDesc = false;
                    if (GroupDashboardActivityNew.share_image != null && !GroupDashboardActivityNew.share_image.isEmpty())
                        Picasso.with(this).load(GroupDashboardActivityNew.share_image).into(img);
                    break;
            }

        }

        setTitle(getResources().getString(R.string.action_edit_post));
        btn_Post.setOnClickListener(this);
        btn_delete_Post.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnUpdate.setVisibility(View.GONE);

    }


    public boolean isValid() {
        boolean valid = true;

        /*if (!isValueValid(edtTitle)) {
            valid = false;
        }*/

//        if (postType.equals("team") || postType.equals("group"))

        if (checkDesc)
            if (!isValueValid(edtDesc)) {
                valid = false;
            }


        return valid;
    }

    public void addPost() {
        if (isValid()) {
            hide_keyboard();
            //  showLoadingDialog();
            if (progressBar != null)

                showLoadingBar(progressBar);
            //  progressBar.setVisibility(View.VISIBLE);

            LeafManager manager = new LeafManager();
            AddPostRequestDescription request = new AddPostRequestDescription();
            request.text = edtDesc.getText().toString();
            request.title = edtTitle.getText().toString();
           AppLog.e("app", "here" + new Gson().toJson(request));
//                    label:
            switch (GroupDashboardActivityNew.share_type) {
                case "group":
                    switch (postType) {
                        case "team":
                            manager.shareGroupPost(this, request, group_id+"", post_id+"", selected_ids, selected_g_id+"", "team_edit");
                            break;
                        case "group":
                            manager.shareGroupPost(this, request, group_id+"", post_id+"", selected_ids, selected_g_id+"", "group_edit");
                            break;
                        case "personal":
                            manager.shareGroupPost(this, request, group_id+"", post_id+"", selected_ids, selected_g_id+"", "personal_edit");
                            break;
                    }
                    break;
                case "team":
                    switch (postType) {
                        case "team":
                            manager.shareTeamPost(this, request, group_id+"", GroupDashboardActivityNew.team_id+"", post_id+"", selected_ids, selected_g_id+"", "team_edit");
                            break;
                        case "group":
                            manager.shareTeamPost(this, request, group_id+"", GroupDashboardActivityNew.team_id+"", post_id+"", selected_ids, selected_g_id+"", "group_edit");
                            break;
                        case "personal":
                            manager.shareTeamPost(this, request, group_id+"", GroupDashboardActivityNew.team_id+"", post_id+"", selected_ids, selected_g_id+"", "personal_edit");
                            break /*label*/;
                    }
                    break;
                case "personal":
                    switch (postType) {
                        case "team":
                            manager.sharePersonalPost(this, request, group_id+"", post_id+"", selected_ids, selected_g_id+"", "team_edit");
                            break;
                        case "group":
                            manager.sharePersonalPost(this, request, group_id+"", post_id+"", selected_ids, selected_g_id+"", "group_edit");
                            break;
                        case "personal":
                            manager.sharePersonalPost(this, request, group_id+"", post_id+"", selected_ids, selected_g_id+"", "personal_edit");
                            break /*label*/;
                    }
                    break;
            }
        }
    }

    public void onClick(View v) {
        hide_keyboard();
        switch (v.getId()) {

            case R.id.btn_edit_post:
            case R.id.btn_update:
            case R.id.btnShare:
                addPost();
                break;
            case R.id.btn_delete_post:
                /*if (isValid()) {
                    showLoadingDialog();
                    *//*LeafManager manager = new LeafManager();
                    EditPostRequest request = new EditPostRequest();
                    request.text = edtDesc.getText().toString();
                    request.title = edtTitle.getText().toString();
                   AppLog.e("app", "here " + new Gson().toJson(request));
                    manager.editPost(EditPostActivity.this, id, post_id, request, postType);*//*
                    SMBDialogUtils.showSMBDialogOKCancel(this, "Are You Sure Want To Delete?", this);
                }*/
                break;
        }
    }


    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        Constants.requestCode = Constants.finishCode;

        //  hideLoadingDialog();
        if (progressBar != null)
            hideLoadingBar();
           // progressBar.setVisibility(View.GONE);

        Toast.makeText(this, getResources().getString(R.string.toast_shared_successfully), Toast.LENGTH_SHORT).show();

        if (SelectShareTypeActivity.selectShareTypeActivity != null)
            SelectShareTypeActivity.selectShareTypeActivity.finish();

        if (ShareGroupListActivity.shareGroupListActivity != null)
            ShareGroupListActivity.shareGroupListActivity.finish();

        if (ShareGroupTeamListActivity.shareGroupTeamListActivity != null)
            ShareGroupTeamListActivity.shareGroupTeamListActivity.finish();

        if (SharePersonalNameListActivity.sharePersonalNameListActivity != null)
            SharePersonalNameListActivity.sharePersonalNameListActivity.finish();

        if (ShareInPersonalActivity.shareInPersonalActivity != null)
            ShareInPersonalActivity.shareInPersonalActivity.finish();

        if (PushActivity.pushActivity != null)
            PushActivity.pushActivity.finish();

        EditPostActivity.this.finish();

    }

    @Override
    public void onFailure(int apiId, String msg) {
//        For OnCommunicationListener
        //hideLoadingDialog();
        if (progressBar != null)
            hideLoadingBar();
        // progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<BaseValidationError> error) {
        //hideLoadingDialog();
        if (progressBar != null)
            hideLoadingBar();
        // progressBar.setVisibility(View.GONE);
       AppLog.e("GeneralPostFragment", "onFailure  ,, msg : " + error);
        if (error.status.equals("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(this, error.title, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onException(int apiId, String error) {
        //hideLoadingDialog();
        if (progressBar != null)
            hideLoadingBar();
        // progressBar.setVisibility(View.GONE);
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(Constants.requestCode);
    }

}
