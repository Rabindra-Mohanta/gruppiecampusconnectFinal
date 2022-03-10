package school.campusconnect.activities;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;

import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import school.campusconnect.Assymetric.AsymmetricRecyclerView;
import school.campusconnect.Assymetric.AsymmetricRecyclerViewAdapter;
import school.campusconnect.adapters.ChildAdapter;
import school.campusconnect.adapters.ChildVideoAdapter;
import school.campusconnect.datamodel.readMore.ReadMoreRes;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AmazoneRemove;
import school.campusconnect.utils.AppLog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.adapters.ReportAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.PostItem;
import school.campusconnect.datamodel.reportlist.ReportItemList;
import school.campusconnect.datamodel.reportlist.ReportResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.MixOperations;
import school.campusconnect.views.SMBDialogUtils;

public class ReadMoreActivity extends BaseActivity implements LeafManager.OnCommunicationListener, DialogInterface.OnClickListener, LeafManager.OnAddUpdateListener<AddPostValidationError>{
    private static final String TAG = "ReadMoreActivity";
    @Bind(R.id.view)
    View view;

    @Bind(R.id.view1)
    View view1;

    @Bind(R.id.txt_que)
    TextView txt_que;

    @Bind(R.id.txt_drop_delete)
    TextView txt_drop_delete;

    @Bind(R.id.txt_drop_report)
    TextView txt_drop_report;

    @Bind(R.id.txt_name)
    TextView txtName;

    @Bind(R.id.txt_content)
    TextView txtContent;

    @Bind(R.id.txt_title)
    TextView txt_title;

    @Bind(R.id.txt_readmore)
    TextView txt_readmore;

    @Bind(R.id.img_lead)
    CircleImageView imgLead;

    @Bind(R.id.img_lead_default)
    ImageView imgLead_default;

    @Bind(R.id.txt_date)
    TextView txtDate;

    @Bind(R.id.txt_like)
    TextView txtLike;

    @Bind(R.id.img_like)
    ImageView ivLike;

    @Bind(R.id.txt_fav)
    ImageView txt_fav;

    @Bind(R.id.txt_comments)
    TextView txt_comments;

    @Bind(R.id.img_comments)
    ImageView img_comments;

    @Bind(R.id.image)
    ImageView imgPhoto;

    @Bind(R.id.imageLoading)
    ImageView imageLoading;

    @Bind(R.id.img_play)
    ImageView imgPlay;

    @Bind(R.id.iv_delete)
    ImageView ivDelete;

    @Bind(R.id.rel)
    RelativeLayout rel;

    @Bind(R.id.lin_drop)
    LinearLayout lin_drop;

    @Bind(R.id.linComments)
    LinearLayout linComments;

    @Bind(R.id.linLikes)
    LinearLayout linLikes;

    @Bind(R.id.linPush)
    LinearLayout linPush;

    @Bind(R.id.recyclerView)
    AsymmetricRecyclerView recyclerView;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.rlMain)
    public RelativeLayout rlMain;

    @Bind(R.id.imgDownloadPdf)
    ImageView imgDownloadPdf;

    DatabaseHandler databaseHandler;

    String type, from;
    String id;

    LeafManager manager = new LeafManager();
    String mGroupId;
    String mTeamId;
    PostItem item;
    private ReportAdapter mAdapter2;
    RecyclerView dialogRecyclerView;
    ProgressBar dialogProgressBar;
    ArrayList<ReportItemList> list = new ArrayList<>();
    private boolean liked;
    private String userId;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readmore);
        ButterKnife.bind(this);

        init();


    }

    @Override
    protected void onStart() {
        super.onStart();
        setData();
    }

    @Override
    protected void onResume() {
        super.onResume();

      /*  if (item != null) {
            AppLog.e(TAG, "Item Found-------------");
            getData();
        } else {
            AppLog.e(TAG, "Item Empty--------------");
        }
*/
    }

    private void setData() {

        if (item == null) {
            item = new PostItem();
            return;
        }

        String dispName = "";
        if (databaseHandler.getCount() > 0) {
            try {
                String name = databaseHandler.getNameFromNum(item.phone.replaceAll(" ", ""));
                if (!TextUtils.isEmpty(name)) {
                    dispName = name;
                } else {
                    dispName = item.createdBy;
                }
            } catch (NullPointerException e) {
                dispName = item.createdBy;
            }
        } else {
            dispName = item.createdBy;
        }
        txtName.setText(dispName);
        txtDate.setText(MixOperations.getFormattedDate(item.createdAt, Constants.DATE_FORMAT));
        txtLike.setText(Constants.coolFormat(item.likes, 0));
        txt_comments.setText(Constants.coolFormat(item.comments, 0) + "");

        if (item.isLiked) {
            Picasso.with(this).load(R.drawable.icon_post_liked).into(ivLike);
        } else {
            Picasso.with(this).load(R.drawable.icon_post_like).into(ivLike);
        }

        if (item.isFavourited) {
            Picasso.with(this).load(R.drawable.icon_post_favd).into(txt_fav);
        } else {
            Picasso.with(this).load(R.drawable.icon_post_fav).into(txt_fav);
        }


        AppLog.e(TAG, "Item Width=>" + item.imageWidth + ",Height=>" + item.imageHeight);

        final String name = dispName;
        if (!TextUtils.isEmpty(item.createdByImage)) {
            Picasso.with(this).load(Constants.decodeUrlToBase64(item.createdByImage)).networkPolicy(NetworkPolicy.OFFLINE).into(imgLead,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            imgLead_default.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError() {
                            Picasso.with(ReadMoreActivity.this).load(Constants.decodeUrlToBase64(item.createdByImage)).into(imgLead, new Callback() {
                                @Override
                                public void onSuccess() {
                                    imgLead_default.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onError() {
                                    imgLead_default.setVisibility(View.VISIBLE);
                                    TextDrawable drawable = TextDrawable.builder()
                                            .buildRound(ImageUtil.getTextLetter(name), ImageUtil.getRandomColor(1));
                                    imgLead_default.setImageDrawable(drawable);
                                    AppLog.e("Picasso", "Error : ");
                                }
                            });
                        }
                    });
        } else {
            imgLead_default.setVisibility(View.VISIBLE);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(ImageUtil.getTextLetter(name), ImageUtil.getRandomColor(2));
            imgLead_default.setImageDrawable(drawable);
        }

        if (!TextUtils.isEmpty(item.fileType)) {
            if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
                if (item.fileName != null) {
               /* int height=0;
                if (item.imageHeight != 0 && item.imageWidth != 0)
                    height = (Constants.screen_width * item.imageHeight) / item.imageWidth;*/

                    ChildAdapter adapter = new ChildAdapter(item.fileName.size(), item.fileName.size(), this, item.fileName);
                    recyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(this, recyclerView, adapter));
                    recyclerView.setVisibility(View.VISIBLE);
                }
                imgPlay.setVisibility(View.GONE);
                imgPhoto.setVisibility(View.GONE);
            } else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
                imgPlay.setVisibility(View.GONE);
                imgPhoto.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                if(item.thumbnailImage!=null && item.thumbnailImage.size()>0)
                {
                    Picasso.with(this).load(Constants.decodeUrlToBase64(item.thumbnailImage.get(0))).into(imgPhoto);
                }
                if (item.fileName != null && item.fileName.size() > 0) {
                    if (AmazoneDownload.isPdfDownloaded(item.fileName.get(0))) {
                        imgDownloadPdf.setVisibility(View.GONE);
                    } else {
                        imgDownloadPdf.setVisibility(View.VISIBLE);
                    }
                }

            } else if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
                int height = (Constants.screen_width * 204) / 480;
                imgPhoto.getLayoutParams().height = height;
                imgPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Picasso.with(this).load(item.thumbnail).into(imgPhoto);
                imgPhoto.setVisibility(View.VISIBLE);
                imgPlay.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                imgPhoto.setVisibility(View.GONE);
                imgPlay.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }
        } else {
            imgPhoto.setVisibility(View.GONE);
            imgPlay.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }

        imgLead_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lin_drop.getVisibility() == View.VISIBLE)
                    lin_drop.setVisibility(View.GONE);

                AppLog.e("PostAdapter AA", "clicked");
                Dialog settingsDialog = new Dialog(ReadMoreActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View newView = (View) inflater.inflate(R.layout.img_layout, null);
                settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(newView);
                ImageView iv = (ImageView) newView.findViewById(R.id.img_popup);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(ImageUtil.getTextLetter(name), ImageUtil.getRandomColor(1));
                iv.setImageDrawable(drawable);
                settingsDialog.show();
            }
        });
        imgLead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lin_drop.getVisibility() == View.VISIBLE)
                    lin_drop.setVisibility(View.GONE);
                else {
                    Dialog settingsDialog = new Dialog(ReadMoreActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View newView = (View) inflater.inflate(R.layout.img_layout, null);
                    settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    settingsDialog.setContentView(newView);
                    settingsDialog.show();
                    ImageView iv = (ImageView) newView.findViewById(R.id.img_popup);

                    if (!TextUtils.isEmpty(item.createdByImage)) {
                        Picasso.with(ReadMoreActivity.this).load(Constants.decodeUrlToBase64(item.createdByImage)).into(iv);
                    } else {
                        TextDrawable drawable = TextDrawable.builder()
                                .buildRect(name, ImageUtil.getRandomColor(1));
                        iv.setImageDrawable(drawable);
                    }
                    settingsDialog.show();
                }
            }
        });

        if (item.canEdit) {
            txt_drop_delete.setVisibility(View.VISIBLE);
            txt_drop_report.setVisibility(View.GONE);
        } else {
            txt_drop_delete.setVisibility(View.GONE);
            txt_drop_report.setVisibility(View.VISIBLE);
        }

        txtLike.setVisibility(View.VISIBLE);
        linLikes.setVisibility(View.VISIBLE);

        if (type.equals("group") || type.equals("favourite") || type.equals("team")) {
            txt_fav.setVisibility(View.VISIBLE);
        } else {
            txt_fav.setVisibility(View.GONE);
        }

        if (item.groupId.equals(Constants.VIVID_GROUP_ID)) {
            AppLog.e("PostAdapter Codition", "Group Id : " + item.groupId);
            txt_fav.setVisibility(View.GONE);
        }

        if (!GroupDashboardActivityNew.mAllowPostQuestion) {
            txt_que.setVisibility(View.GONE);
            view1.setVisibility(View.GONE);
        } else {
            txt_que.setVisibility(View.VISIBLE);
            view1.setVisibility(View.VISIBLE);
        }

        txt_title.setText(item.title);
        if (TextUtils.isEmpty(item.title))
            txt_title.setVisibility(View.GONE);
        else
            txt_title.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(item.text))
            txtContent.setText(item.text);
        else {
            txtContent.setVisibility(View.GONE);
        }
    }

    private void init() {
        mGroupId = GroupDashboardActivityNew.groupId;
        databaseHandler = new DatabaseHandler(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle("Post");
        String data = getIntent().getStringExtra("data");

        if (data == null) {
            getData();
            return;
        }

        Gson gson = new Gson();
        item = gson.fromJson(data, PostItem.class);
        type = getIntent().getStringExtra("type");
        from = getIntent().getStringExtra("from");
        mTeamId = getIntent().getStringExtra("team_id");

        if (type.equals("personal"))
            userId = getIntent().getStringExtra("user_id");



        AppLog.e(TAG, "data : " + data);
        AppLog.e(TAG, "type : " + type);
        AppLog.e(TAG, "team_id : " + mTeamId);
        AppLog.e(TAG, "userId : " + userId);

        txt_title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData cData = ClipData.newPlainText("text", txt_title.getText());
                cManager.setPrimaryClip(cData);
                return true;
            }
        });
    }

    private void getData() {

        Bundle bundle = getIntent().getExtras();

        String type = bundle.getString("type", null);
        String mGroupId = bundle.getString("groupId", null);
        String mTeamId = bundle.getString("teamId", null);
        String userId = bundle.getString("userId", null);
        String postId = bundle.getString("postId", null);


        AppLog.e(TAG, "DATA-  TYPE: " + type + " GRP: " + mGroupId + " TEAM: " + mTeamId + " USR: " + userId + " POST: " + postId);


        this.type = type;
        if(mGroupId!=null){
            this.mGroupId = mGroupId;
        }
        this.mTeamId = mTeamId;
        this.userId = userId;

        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);

        if (type.equalsIgnoreCase("groupPost") || type.equalsIgnoreCase("groupPostComment")) {
            linComments.setVisibility(type.equalsIgnoreCase("groupPost") ? View.GONE : View.VISIBLE);

            if (isConnectionAvailable()) {
                manager.readMore_GroupPost(ReadMoreActivity.this, mGroupId, postId);
            } else {
                showNoNetworkMsg();
            }
        }

        if (type.equalsIgnoreCase("gallery")) {
            linComments.setVisibility(View.GONE);
            if (isConnectionAvailable()) {
                manager.readMore_Gallery(ReadMoreActivity.this, mGroupId, postId);
            } else {
                showNoNetworkMsg();
            }
        }

        if (type.equalsIgnoreCase("teamPost") || type.equalsIgnoreCase("teamPostComment")) {

            linComments.setVisibility(type.equalsIgnoreCase("teamPost") ? View.GONE : View.VISIBLE);

            if (isConnectionAvailable()) {
                manager.readMore_TeamPost(ReadMoreActivity.this, mGroupId, mTeamId, postId);
            } else {
                showNoNetworkMsg();
            }


        }
        if (type.equalsIgnoreCase("individualPost") || type.equalsIgnoreCase("individualPostComment")) {
            linComments.setVisibility(type.equalsIgnoreCase("individualPost") ? View.GONE : View.VISIBLE);

            if (isConnectionAvailable()) {
                manager.readMore_Individual(ReadMoreActivity.this, mGroupId, userId, postId);
            } else {
                showNoNetworkMsg();
            }
        }


    }

    @OnClick({R.id.txt_like, R.id.txt_fav, R.id.rel, R.id.txt_readmore, R.id.iv_delete,
            R.id.txt_comments, R.id.txt_drop_delete, R.id.txt_drop_report, R.id.txt_drop_share,
            R.id.txt_que, R.id.txt_push, R.id.txt_name, R.id.txt_like_list, R.id.img_comments, R.id.img_like, R.id.rlMain})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_like:
                if (lin_drop.getVisibility() == View.VISIBLE)
                    lin_drop.setVisibility(View.GONE);
                else if (isConnectionAvailable()) {
                    onLikeClick(item, 0);
                } else {
                    showNoNetworkMsg();
                }

                break;
            case R.id.rlMain:
                if (lin_drop.getVisibility() == View.VISIBLE)
                    lin_drop.setVisibility(View.GONE);
                break;
            case R.id.txt_fav:
                if (lin_drop.getVisibility() == View.VISIBLE)
                    lin_drop.setVisibility(View.GONE);
                else if (isConnectionAvailable()) {
                    onFavClick(item, 0);
                } else {
                    showNoNetworkMsg();
                }
                break;
            case R.id.rel:
                if (lin_drop.getVisibility() == View.VISIBLE)
                    lin_drop.setVisibility(View.GONE);
                else if (isConnectionAvailable()) {
                    onPostClick(item);
                } else {
                    showNoNetworkMsg();
                }
                break;
            case R.id.iv_delete:
                if (lin_drop.getVisibility() == View.VISIBLE)
                    lin_drop.setVisibility(View.GONE);
                else
                    lin_drop.setVisibility(View.VISIBLE);
                break;

            case R.id.iv_edit:
                if (isConnectionAvailable()) {
                    onEditClick(item);
                } else {
                    showNoNetworkMsg();
                }
                break;

            case R.id.txt_name:
                onNameClick(item);
                break;

            case R.id.img_comments:
            case R.id.txt_comments:
                AppLog.e("COMMETS_", "type is " + type);
                AppLog.e(TAG, "CMT: DATA-  TYPE: " + type + " GRP: " + mGroupId + " TEAM: " + mTeamId + " USR: " + userId + " POST: " + item.id);

                if (lin_drop.getVisibility() == View.VISIBLE)
                    lin_drop.setVisibility(View.GONE);
                else {
                    if (isConnectionAvailable()) {
                        if (isConnectionAvailable()) {
                            Intent intent1 = new Intent(this, CommentsActivity.class);
                            intent1.putExtra("id", mGroupId);
                            intent1.putExtra("team_id", mTeamId);
                            intent1.putExtra("post_id", item.id);
                            intent1.putExtra("user_id", userId);
                            intent1.putExtra("type", type);
                            startActivity(intent1);
                        } else {
                            showNoNetworkMsg();
                        }
                    } else {
                        showNoNetworkMsg();
                    }
                }

                break;

            case R.id.txt_drop_delete:
                lin_drop.setVisibility(View.GONE);
                if (isConnectionAvailable()) {
                    onDeleteClick(item);
                } else {
                    showNoNetworkMsg();
                }
                break;

            case R.id.txt_drop_report:
                lin_drop.setVisibility(View.GONE);
                if (isConnectionAvailable()) {
                    onReportClick(item);
                } else {
                    showNoNetworkMsg();
                }
                break;

            case R.id.txt_drop_share:
                lin_drop.setVisibility(View.GONE);
                if (isConnectionAvailable()) {
                    onShareClick(item);
                } else {
                    showNoNetworkMsg();
                }
                break;

            case R.id.txt_que:
                lin_drop.setVisibility(View.GONE);
                if (isConnectionAvailable()) {
                    onQueClick(item);
                } else {
                    showNoNetworkMsg();
                }
                break;

            case R.id.txt_push:
                lin_drop.setVisibility(View.GONE);
                if (isConnectionAvailable()) {
                    onPushClick(item);
                } else {
                    showNoNetworkMsg();
                }
                break;

            case R.id.txt_like_list:
            case R.id.txt_like:
                lin_drop.setVisibility(View.GONE);
                if (isConnectionAvailable()) {
                    onLikeListClick(item);
                } else {
                    showNoNetworkMsg();
                }
                break;

        }
    }

    private void cleverTapLike(int fav) {
        if (isConnectionAvailable()) {
            if (isConnectionAvailable()) {
                try {
                    CleverTapAPI cleverTap = CleverTapAPI.getInstance(this);
                    AppLog.e("GeneralPost", "Success to found cleverTap objects=>");

                    HashMap<String, Object> likeAction = new HashMap<String, Object>();
                    likeAction.put("id", mGroupId);
                    likeAction.put("group_name", GroupDashboardActivityNew.group_name);
                    likeAction.put("post_id", item.id);
                    if (fav == 1) {
                        likeAction.put("isLiked", true);
                    } else {
                        likeAction.put("isLiked", false);
                    }
                    cleverTap.event.push("Liked", likeAction);
                    AppLog.e("GeneralPost", "Success");

                } catch (CleverTapMetaDataNotFoundException e) {
                    AppLog.e("GeneralPost", "CleverTapMetaDataNotFoundException=>" + e.toString());
                    // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
                } catch (CleverTapPermissionsNotSatisfied e) {
                    AppLog.e("GeneralPost", "CleverTapPermissionsNotSatisfied=>" + e.toString());
                    // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
                }

            }
        }


    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (isConnectionAvailable()) {
            if (progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
            if (type.equals("team")) {
                manager.deleteTeamPost(ReadMoreActivity.this, mGroupId + "", mTeamId + "", item.id);
            } else {
                manager.deletePost(ReadMoreActivity.this, mGroupId + "", item.id, "group");
            }
        } else {
            showNoNetworkMsg();
        }

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        // hideLoadingDialog();
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        switch (apiId) {
            case LeafManager.API_ID_FAV:
                if (response.status.equalsIgnoreCase("favourite")) {
                    item.isFavourited = true;

                } else {
                    item.isFavourited = false;
                }
                if (item.isFavourited) {
                    Picasso.with(this).load(R.drawable.icon_post_favd).into(txt_fav);
                } else {
                    Picasso.with(this).load(R.drawable.icon_post_fav).into(txt_fav);
                }

                if (type.equalsIgnoreCase("group"))
                    LeafPreference.getInstance(ReadMoreActivity.this).setBoolean(LeafPreference.ISGENERALPOSTUPDATED, true);
                else if (type.equalsIgnoreCase("team"))
                    LeafPreference.getInstance(ReadMoreActivity.this).setBoolean(LeafPreference.ISTEAMPOSTUPDATED, true);

                break;

            case LeafManager.API_ID_LIKE:
            case LeafManager.API_ID_LIKE_TEAM:
            case LeafManager.API_ID_LIKE_PERSONAL:
                if (response.status.equalsIgnoreCase("liked")) {
                    item.isLiked = true;
                    item.likes++;
                } else {
                    item.isLiked = false;
                    item.likes--;
                }
                txtLike.setText(Constants.coolFormat(item.likes, 0));
                if (item.isLiked) {
                    Picasso.with(this).load(R.drawable.icon_post_liked).into(ivLike);
                } else {
                    Picasso.with(this).load(R.drawable.icon_post_like).into(ivLike);
                }

                liked = false;

                if (type.equalsIgnoreCase("group"))
                    LeafPreference.getInstance(ReadMoreActivity.this).setBoolean(LeafPreference.ISGENERALPOSTUPDATED, true);
                else if (type.equalsIgnoreCase("team"))
                    LeafPreference.getInstance(ReadMoreActivity.this).setBoolean(LeafPreference.ISTEAMPOSTUPDATED, true);
                else
                    LeafPreference.getInstance(ReadMoreActivity.this).setBoolean(LeafPreference.ISPERSONALPOSTUPDATED, true);
                break;

            case LeafManager.API_ID_DELETE_POST:
                Toast.makeText(getApplicationContext(), "Post Deleted Succesfully", Toast.LENGTH_SHORT).show();
                if (type.equalsIgnoreCase("group"))
                    LeafPreference.getInstance(ReadMoreActivity.this).setBoolean(LeafPreference.ISGENERALPOSTUPDATED, true);
                else if (type.equalsIgnoreCase("team"))
                    LeafPreference.getInstance(ReadMoreActivity.this).setBoolean(LeafPreference.ISTEAMPOSTUPDATED, true);
                AmazoneRemove.remove(item.fileName);
                onBackPressed();
                break;

            case LeafManager.API_REPORT_LIST:
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
                if (dialogProgressBar != null)
                    dialogProgressBar.setVisibility(View.GONE);
                ReportResponse response1 = (ReportResponse) response;

                list.clear();

                for (int i = 0; i < response1.data.size(); i++) {
                    ReportItemList reportItemList = new ReportItemList(response1.data.get(i).type, response1.data.get(i).reason, false);
                    list.add(reportItemList);
                }

                ReportAdapter.selected_position = -1;
                mAdapter2 = new ReportAdapter(list);
                AppLog.e("adas ", mAdapter2.getItemCount() + "");
                AppLog.e("ReportResponse", "onSucces  ,, msg : " + ((ReportResponse) response).data.toString());
                dialogRecyclerView.setAdapter(mAdapter2);
                break;

            case LeafManager.API_REPORT:
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Post Reported Successfully", Toast.LENGTH_SHORT).show();
                break;

            case LeafManager.API_READ_MORE_GROUP_POST: {
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);

                AppLog.e(TAG, "API_READ_MORE_GROUP_POST: " + response.toString());

                ReadMoreRes res = (ReadMoreRes) response;
                ReadMoreRes.Data data = res.getData();

                setApiData(data);


                break;
            }

            case LeafManager.API_READ_MORE_TEAM_POST: {

                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);

                AppLog.e(TAG, "API_READ_MORE_TEAM_POST: " + response.toString());

                ReadMoreRes res = (ReadMoreRes) response;
                ReadMoreRes.Data data = res.getData();

                setApiData(data);


                break;
            }

            case LeafManager.API_READ_MORE_INDIVIDUAL: {

                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);

                AppLog.e(TAG, "API_READ_MORE_TEAM_POST_COMMENT: " + response.toString());

                ReadMoreRes res = (ReadMoreRes) response;
                ReadMoreRes.Data data = res.getData();

                setApiData(data);

                break;
            }

            case LeafManager.API_READ_MORE_GALLERY: {

                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);

                AppLog.e(TAG, "API_READ_MORE_TEAM_POST: " + response.toString());

                ReadMoreRes res = (ReadMoreRes) response;
                ReadMoreRes.Data data = res.getData();

                setApiData(data);


            }

//            case LeafManager.API_READ_MORE_INDIVIDUAL: {
//
//                if (progressBar != null)
//                    progressBar.setVisibility(View.GONE);
//
//                AppLog.e(TAG, "API_READ_MORE_TEAM_POST: " + response.toString());
//
//                ReadMoreRes res = (ReadMoreRes) response;
//                ReadMoreRes.Data data = res.getData();
//
//                setApiData(data);
//
//
//            }


        }
    }

    private void setApiData(ReadMoreRes.Data data) {


        item.id = data.getId();

        txtName.setText(data.getCreatedBy());
        txtDate.setText(MixOperations.getFormattedDate(data.getUpdatedAt(), Constants.DATE_FORMAT));
        txtLike.setText(Constants.coolFormat(data.getLikes(), 0));
        txt_comments.setText(Constants.coolFormat(data.getComments(), 0) + "");

        txtContent.setText(data.getText());
        txt_title.setText(data.getTitle());

        if (TextUtils.isEmpty(data.getTitle()))
            txt_title.setVisibility(View.GONE);

        if (data.isLiked()) {
            Picasso.with(this).load(R.drawable.icon_post_liked).into(ivLike);
        } else {
            Picasso.with(this).load(R.drawable.icon_post_like).into(ivLike);
        }

        if (data.isFavourited()) {
            Picasso.with(this).load(R.drawable.icon_post_favd).into(txt_fav);
        } else {
            Picasso.with(this).load(R.drawable.icon_post_fav).into(txt_fav);
        }

        item.fileType = data.getFileType();
        item.fileName = data.getFileName();
        item.thumbnail = data.getThumbnail();
        item.video = data.getVideo();

        if (!TextUtils.isEmpty(item.fileType)) {
            if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
                if (item.fileName != null) {
                    ChildAdapter adapter = new ChildAdapter(item.fileName.size() == 3 ? 2 : Constants.MAX_IMAGE_NUM, item.fileName.size(), this, item.fileName);
                    recyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(this, recyclerView, adapter));
                    recyclerView.setVisibility(View.VISIBLE);
                }
                imgPlay.setVisibility(View.GONE);
                imgPhoto.setVisibility(View.GONE);
            } else if (item.fileType.equals(Constants.FILE_TYPE_VIDEO)) {
                if (item.fileName != null) {
                    ChildVideoAdapter adapter;
                    if (item.fileName.size() == 3) {
                        adapter = new ChildVideoAdapter(2,  this, item.fileName,item.thumbnailImage);
                    } else {
                        adapter = new ChildVideoAdapter(Constants.MAX_IMAGE_NUM, this, item.fileName,item.thumbnailImage);
                    }
                    recyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(this, recyclerView, adapter));
                    recyclerView.setVisibility(View.VISIBLE);
                }
                imgPlay.setVisibility(View.GONE);
                imgPhoto.setVisibility(View.GONE);
            }  else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
                Picasso.with(this).load(R.drawable.pdf_thumbnail).into(imgPhoto);
                imgPlay.setVisibility(View.GONE);
                imgPhoto.setVisibility(View.VISIBLE);

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                imgPhoto.setLayoutParams(lp);
                imgPhoto.requestLayout();

                recyclerView.setVisibility(View.GONE);
            } else if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
                int height = (Constants.screen_width * 204) / 480;
                imgPhoto.getLayoutParams().height = height;
                imgPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Picasso.with(this).load(item.thumbnail).into(imgPhoto);
                imgPhoto.setVisibility(View.VISIBLE);
                imgPlay.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                imgPhoto.setVisibility(View.GONE);
                imgPlay.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }
        }


    }


    @Override
    public void onFailure(int apiId, ErrorResponseModel<AddPostValidationError> error) {
        //hideLoadingDialog();
        progressBar.setVisibility(View.GONE);
        AppLog.e("OnFailure", "OnFailure Called");
        if (error.status.equals("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(this, error.title, Toast.LENGTH_SHORT).show();
        }
        liked = false;
    }

    @Override
    public void onFailure(int apiId, String msg) {
        //hideLoadingDialog();
        progressBar.setVisibility(View.GONE);
        if (msg.contains("401:Unauthorized")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else if (msg.contains("418")) {
            if (apiId == LeafManager.API_REPORT)
                Toast.makeText(this, "You have already reported this post", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
        liked = false;

    }

    @Override
    public void onException(int apiId, String msg) {
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
        liked = false;
    }

    public void onFavClick(PostItem item, int pos) {
        progressBar.setVisibility(View.VISIBLE);
        int fav = 0;
        if (item.isFavourited) {
            fav = 0;
        } else {
            fav = 1;
        }

        manager.setFav(this, mGroupId + "", item.id);

        cleverTapFav(item, fav);
    }

    private void cleverTapFav(PostItem item, int fav) {
        if (isConnectionAvailable()) {
            try {
                CleverTapAPI cleverTap = CleverTapAPI.getInstance(this);
                AppLog.e("GeneralPost", "Success to found cleverTap objects=>");
                if (fav == 1) {
                    HashMap<String, Object> favAction = new HashMap<String, Object>();
                    favAction.put("id", mGroupId);
                    favAction.put("group_name", GroupDashboardActivityNew.group_name);
                    favAction.put("post_id", item.id);
                    favAction.put("post_title", item.title);
                    cleverTap.event.push("Add Favorite", favAction);
                } else {
                    HashMap<String, Object> favAction = new HashMap<String, Object>();
                    favAction.put("id", mGroupId);
                    favAction.put("group_name", GroupDashboardActivityNew.group_name);
                    favAction.put("post_id", item.id);
                    favAction.put("post_title", item.title);
                    cleverTap.event.push("Remove Favorite", favAction);
                }
                AppLog.e("GeneralPost", "Success to found cleverTap objects=>");

            } catch (CleverTapMetaDataNotFoundException e) {
                AppLog.e("GeneralPost", "CleverTapMetaDataNotFoundException=>" + e.toString());
                // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
            } catch (CleverTapPermissionsNotSatisfied e) {
                AppLog.e("GeneralPost", "CleverTapPermissionsNotSatisfied=>" + e.toString());
                // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
            } catch (Exception ignored) {
            }

        }
    }


    public void onLikeClick(PostItem item, int pos) {


        if (!liked) {
            liked = true;
            progressBar.setVisibility(View.VISIBLE);
            if (type.equals("group") || type.equals("favourite"))
                manager.setLikes(this, mGroupId + "", item.id);
            else if (type.equals("team") || type.equalsIgnoreCase("teamPost") || type.equalsIgnoreCase("teamPostComment"))
                manager.setTeamLike(this, mGroupId, mTeamId, item.id);
            else
                manager.setPersonalLike(this, mGroupId, item.id);


            int fav = 0;
            if (item.isLiked) {
                fav = 0;
            } else {
                fav = 1;
            }
            cleverTapLike(fav);
        }
    }

    public void onPostClick(PostItem item) {
        AppLog.e(TAG, "onPostClick() :" + item);
        if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {

            Intent browserIntent = new Intent(this, TestActivity.class);
            browserIntent.putExtra("url", item.video);
            startActivity(browserIntent);

        } else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
            Intent i = new Intent(this, ViewPDFActivity.class);
            i.putExtra("pdf", item.fileName.get(0));
            i.putExtra("thumbnail", item.thumbnailImage.get(0));
            i.putExtra("name", item.title);
            startActivity(i);

        } else if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
            Intent i = new Intent(this, FullScreenActivity.class);
            i.putExtra("image", item.fileName);
            startActivity(i);
        }
    }

    public void onReadMoreClick(PostItem item) {

    }


    public void onEditClick(PostItem item) {

    }


    public void onDeleteClick(PostItem item) {
        SMBDialogUtils.showSMBDialogOKCancel(this, "Are You Sure Want To Delete ?", this);
    }


    public void onReportClick(PostItem item) {
        showReportDialog();
    }

    public void onShareClick(PostItem item) {

    }

    public void onQueClick(PostItem item) {

    }

    public void onPushClick(PostItem item) {
        GroupDashboardActivityNew.is_share_edit = true;
        GroupDashboardActivityNew.share_type = type;
        GroupDashboardActivityNew.share_title = item.title;
        GroupDashboardActivityNew.share_desc = item.text;

        GroupDashboardActivityNew.share_image = item.image;
        GroupDashboardActivityNew.imageHeight = item.imageHeight;
        GroupDashboardActivityNew.imageWidth = item.imageWidth;

        GroupDashboardActivityNew.team_id = mTeamId;
        if (TextUtils.isEmpty(item.fileType)) {
            GroupDashboardActivityNew.share_image_type = 4;
        } else if (TextUtils.isEmpty(item.fileType)) {
            GroupDashboardActivityNew.share_image_type = 4;
        } else if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
            GroupDashboardActivityNew.share_image_type = 1;
        } else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
            GroupDashboardActivityNew.share_image_type = 2;
        } else if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
            GroupDashboardActivityNew.share_image_type = 3;
        } else {
            GroupDashboardActivityNew.share_image_type = 4;
        }

        Intent intent = new Intent(this, PushActivity.class);
        intent.putExtra("id", mGroupId);
        intent.putExtra("post_id", item.id);
        startActivity(intent);
    }


    public void onNameClick(PostItem item) {

    }

    public void onLikeListClick(PostItem item) {
        if (type.equals("group")) {
            if (isConnectionAvailable()) {
                Intent intent = new Intent(ReadMoreActivity.this, LikesListActivity.class);
                intent.putExtra("id", mGroupId);
                intent.putExtra("post_id", item.id);
                intent.putExtra("type", "group");
                startActivity(intent);
            } else {
                showNoNetworkMsg();
            }
        } else if (type.equals("team")) {
            if (isConnectionAvailable()) {
                Intent intent = new Intent(ReadMoreActivity.this, LikesListActivity.class);
                intent.putExtra("id", mGroupId);
                intent.putExtra("post_id", item.id);
                intent.putExtra("type", "team");
                intent.putExtra("team_id", mTeamId);
                startActivity(intent);
            } else {
                showNoNetworkMsg();
            }
        }
    }


    public void onMoreOptionClick(PostItem item) {

    }


    public void showReportDialog() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_layout_report_list);

        dialogRecyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);

        dialogProgressBar = (ProgressBar) dialog.findViewById(R.id.progressBar);

        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        TextView tv_report = (TextView) dialog.findViewById(R.id.tv_report);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tv_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReport(list.get(ReportAdapter.selected_position).type);
                dialog.dismiss();
            }
        });

        final LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        dialogRecyclerView.setLayoutManager(manager);
        getReportData();

        dialog.show();
    }

    private void getReportData() {
        LeafManager mManager = new LeafManager();
        //showLoadingDialog();
        progressBar.setVisibility(View.VISIBLE);
        mManager.getReportList(this);
    }

    private void sendReport(int report_id) {
        LeafManager mManager = new LeafManager();
        //showLoadingDialog();
        progressBar.setVisibility(View.VISIBLE);
        mManager.reportPost(this, mGroupId + "", item.id, report_id);
    }

    public void push() {
        Intent intent;
        switch (type) {

            case "group":
                GroupDashboardActivityNew.is_share_edit = true;
                GroupDashboardActivityNew.share_type = "group";
                GroupDashboardActivityNew.share_title = item.title;
                GroupDashboardActivityNew.share_desc = item.text;

                GroupDashboardActivityNew.share_image = item.image;
                GroupDashboardActivityNew.imageHeight = item.imageHeight;
                GroupDashboardActivityNew.imageWidth = item.imageWidth;

                if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
                    GroupDashboardActivityNew.share_image_type = 1;
                } else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
                    GroupDashboardActivityNew.share_image_type = 2;
                } else if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
                    GroupDashboardActivityNew.share_image_type = 3;
                } else {
                    GroupDashboardActivityNew.share_image_type = 4;
                }

                intent = new Intent(this, PushActivity.class);
                intent.putExtra("id", mGroupId);
                intent.putExtra("post_id", item.id);
                intent.putExtra("from", from);
                startActivity(intent);
                break;

            case "team":

                GroupDashboardActivityNew.is_share_edit = true;
                GroupDashboardActivityNew.share_type = "team";
                GroupDashboardActivityNew.share_title = item.title;
                GroupDashboardActivityNew.share_desc = item.text;

                GroupDashboardActivityNew.share_image = item.image;
                GroupDashboardActivityNew.imageHeight = item.imageHeight;
                GroupDashboardActivityNew.imageWidth = item.imageWidth;

                if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
                    GroupDashboardActivityNew.share_image_type = 1;
                } else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
                    GroupDashboardActivityNew.share_image_type = 2;
                } else if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
                    GroupDashboardActivityNew.share_image_type = 3;
                } else {
                    GroupDashboardActivityNew.share_image_type = 4;
                }

                GroupDashboardActivityNew.team_id = mTeamId;
                intent = new Intent(this, PushActivity.class);
                intent.putExtra("id", mGroupId);
                intent.putExtra("post_id", item.id);
                intent.putExtra("from", from);
                startActivity(intent);
                break;

            case "personal":

                GroupDashboardActivityNew.is_share_edit = true;
                GroupDashboardActivityNew.share_type = "personal";
                GroupDashboardActivityNew.share_title = item.title;
                GroupDashboardActivityNew.share_desc = item.text;

                GroupDashboardActivityNew.share_image = item.image;
                GroupDashboardActivityNew.imageHeight = item.imageHeight;
                GroupDashboardActivityNew.imageWidth = item.imageWidth;

                if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
                    GroupDashboardActivityNew.share_image_type = 1;
                } else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
                    GroupDashboardActivityNew.share_image_type = 2;
                } else if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
                    GroupDashboardActivityNew.share_image_type = 3;
                } else {
                    GroupDashboardActivityNew.share_image_type = 4;
                }

                intent = new Intent(this, PushActivity.class);
                intent.putExtra("id", mGroupId);
                intent.putExtra("post_id", item.id);
                intent.putExtra("from", from);
                startActivity(intent);
                break;

            default:
                GroupDashboardActivityNew.is_share_edit = true;
                GroupDashboardActivityNew.share_type = "personal";
                GroupDashboardActivityNew.share_title = item.title;
                GroupDashboardActivityNew.share_desc = item.text;

                GroupDashboardActivityNew.share_image = item.image;
                GroupDashboardActivityNew.imageHeight = item.imageHeight;
                GroupDashboardActivityNew.imageWidth = item.imageWidth;

                if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
                    GroupDashboardActivityNew.share_image_type = 1;
                } else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
                    GroupDashboardActivityNew.share_image_type = 2;
                } else if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
                    GroupDashboardActivityNew.share_image_type = 3;
                } else {
                    GroupDashboardActivityNew.share_image_type = 4;
                }

                intent = new Intent(this, PushActivity.class);
                intent.putExtra("id", mGroupId);
                intent.putExtra("post_id", item.id);
                intent.putExtra("from", from);
                startActivity(intent);
                break;


        }


    }
}
