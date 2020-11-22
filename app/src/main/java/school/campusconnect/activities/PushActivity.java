package school.campusconnect.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import school.campusconnect.datamodel.AddPostRequestDescription;
import school.campusconnect.datamodel.LeadResponse;
import school.campusconnect.utils.AppLog;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import school.campusconnect.R;
import school.campusconnect.adapters.PersonalChatContactsAdapter;
import school.campusconnect.adapters.ShareGroupAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.gruppiecontacts.GruppieContactsModel;
import school.campusconnect.datamodel.personalchat.PersonalPostItem;
import school.campusconnect.datamodel.sharepost.ShareGroupItemList;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.datamodel.teamdiscussion.MyTeamsResponse;
import school.campusconnect.datamodel.teamdiscussion.TeamListItemModel;
import school.campusconnect.datamodel.teamdiscussion.TeamPostGetItem;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.Constants;

/**
 * Created by frenzin04 on 4/14/2017.
 */

public class PushActivity extends BaseActivity implements
        PersonalChatContactsAdapter.OnChatSelected, View.OnClickListener {

    private static final String TAG = "PushActivity";
    public static int selectCount;
    public Toolbar mToolBar;

    RecyclerView recyclerView;
    RecyclerView recyclerView2;

    RelativeLayout relTeam;
    RelativeLayout relIndividual;
    RelativeLayout relative_group;

    TextView txt_grup;
    TextView txt_personal;

    Button btn_share;
    ImageView btn_update;

    ImageView imgArrow;
    ImageView img_grup;

    ProgressBar progressBar;
    ProgressBar progressBar2;

    private ShareGroupAdapter mAdapter;
    private ShareGroupAdapter mAdapter2;
    String mGroupId = "";
    String postId = "";
    public boolean mIsLoading = false;
    public boolean mIsLoading2 = false;
    public int totalPages = 1;
    public int currentPage = 1;
    int count;// = 1;
    boolean isPost;
    public TeamPostGetItem currentItem;
    public ArrayList<ShareGroupItemList> mData = new ArrayList<ShareGroupItemList>();
    ArrayList<ShareGroupItemList> mData2 = new ArrayList<ShareGroupItemList>();
    public ArrayList<TeamListItemModel> mData_with_contact_name = new ArrayList<TeamListItemModel>();

    public static boolean indiShow = false;

    public static Activity pushActivity;

    String image = "";

    LeafManager leafManager = new LeafManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);
        selectCount = 0;
        findViews();
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(R.string.lbl_share_post);
        mGroupId = getIntent().getExtras().getString("id");
        postId = getIntent().getExtras().getString("post_id");
        pushActivity = this;
        ActiveAndroid.initialize(this);

        AppLog.e(TAG, "GroupID =>" + mGroupId);


        if (!GroupDashboardActivityNew.isPost)
            relative_group.setVisibility(View.GONE);

        indiShow = false;

        if (!TextUtils.isEmpty(GroupDashboardActivityNew.image)) {
            Picasso.with(this).load(Constants.decodeUrlToBase64(GroupDashboardActivityNew.image)).into(img_grup);
        } else {
            img_grup.setImageResource(R.drawable.icon_default_groups);
        }

        final LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        final LinearLayoutManager manager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(manager);
        recyclerView2.setLayoutManager(manager2);

        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        count = databaseHandler.getCount();

        mAdapter = new ShareGroupAdapter(new ArrayList<ShareGroupItemList>(), "team", 0);


        mAdapter.addItems(mData);
        recyclerView.setAdapter(mAdapter);
        progressBar.setVisibility(View.GONE);

        getData();

        progressBar2.setVisibility(View.VISIBLE);
        mAdapter2 = new ShareGroupAdapter(new ArrayList<ShareGroupItemList>(), "personal", 0);
        //new TaskForFriends().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        getPersonalList();

    }
    private void getPersonalList() {
        leafManager.getTeamMember(this, GroupDashboardActivityNew.groupId,"",true);
    }
    private void getData() {
        //mAdapter.clear();
        mIsLoading = true;
        leafManager.myTeamList(this, mGroupId + "");
    }


    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar2.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_MY_TEAM_LIST:
                mData.clear();
                MyTeamsResponse res = (MyTeamsResponse) response;
                List<MyTeamData> result = res.getResults();
                AppLog.e("DATABASE", "Team name is " + res.getResults().toString());
                for (int i = 0; i < result.size(); i++) {
                    if(i==0)
                        continue;

                    AppLog.e("DATABASE", "Team name is " + result.get(i).name);

                    if(result.get(i).allowTeamPostAll)
                    {
                        ShareGroupItemList itemList = new ShareGroupItemList(result.get(i).teamId + "",
                                result.get(i).name, result.get(i).image,
                                result.get(i).phone, false);
                        mData.add(itemList);
                    }
                }
                mAdapter.addItems(mData);
                recyclerView.setAdapter(mAdapter);
                break;
            case LeafManager.API_ID_LEAD_LIST:
                mAdapter2.clear();
                LeadResponse res2 = (LeadResponse) response;
                AppLog.e(TAG,"PersonalInbox Response :"+new Gson().toJson(res2));
                for (int i = 0; i < res2.getResults().size(); i++) {
                    ShareGroupItemList itemList = new ShareGroupItemList(res2.getResults().get(i).id + "",
                            res2.getResults().get(i).name, res2.getResults().get(i).image,
                            res2.getResults().get(i).phone, false);
                    mData2.add(itemList);
                }
                mAdapter2.addItems(mData2);
                recyclerView2.setAdapter(mAdapter2);
                break;
            default:
                Constants.requestCode = Constants.finishCode;
                Toast.makeText(this, "Shared successfully", Toast.LENGTH_SHORT).show();

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
                break;
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        progressBar2.setVisibility(View.GONE);
        super.onFailure(apiId, msg);
    }

    @Override
    public void onException(int apiId, String msg) {
        progressBar2.setVisibility(View.GONE);
        super.onException(apiId, msg);
    }

    private void findViews() {
        /*swipeRefreshLayout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout2 = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout2);*/

        mToolBar = (Toolbar) findViewById(R.id.toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView2 = (RecyclerView) findViewById(R.id.recyclerView2);

        relTeam = (RelativeLayout) findViewById(R.id.relTeam);
        relIndividual = (RelativeLayout) findViewById(R.id.relIndividual);
        relative_group = (RelativeLayout) findViewById(R.id.relative_group);

        txt_grup = (TextView) findViewById(R.id.txt_grup);
        txt_personal = (TextView) findViewById(R.id.txt_personal);

        btn_share = (Button) findViewById(R.id.btn_share);

        imgArrow = (ImageView) findViewById(R.id.imgArrow);
        img_grup = (ImageView) findViewById(R.id.img_grup);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);

        btn_update = (ImageView) findViewById(R.id.btn_update);
        btn_update.setOnClickListener(this);

        relTeam.setOnClickListener(this);
        btn_share.setOnClickListener(this);
        relative_group.setOnClickListener(this);
        imgArrow.setOnClickListener(this);
        txt_personal.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.relTeam:
                break;

            case R.id.imgArrow:
                if (indiShow) {
                    indiShow = false;
                    mAdapter2.setAllFalse();
                    mAdapter2.notifyDataSetChanged();
                    relTeam.setVisibility(View.GONE);
                    relIndividual.setVisibility(View.GONE);
                    imgArrow.setBackgroundResource(R.drawable.icon_expand);
                } else {
                    indiShow = true;
                    mAdapter.setAllFalse();
                    mAdapter.notifyDataSetChanged();
                    relTeam.setVisibility(View.VISIBLE);
                    relIndividual.setVisibility(View.VISIBLE);
                    imgArrow.setBackgroundResource(R.drawable.icon_collapse);
                }
                break;

            case R.id.txt_personal:
                if (indiShow) {
                    indiShow = false;
                    mAdapter2.setAllFalse();
                    mAdapter2.notifyDataSetChanged();
                    relTeam.setVisibility(View.GONE);
                    relIndividual.setVisibility(View.GONE);
                    imgArrow.setBackgroundResource(R.drawable.icon_expand);
                } else {
                    indiShow = true;
                    mAdapter.setAllFalse();
                    mAdapter.notifyDataSetChanged();
                    relTeam.setVisibility(View.VISIBLE);
                    relIndividual.setVisibility(View.VISIBLE);
                    imgArrow.setBackgroundResource(R.drawable.icon_collapse);
                }
                break;

            case R.id.relative_group:
                progressBar.setVisibility(View.VISIBLE);
                addPost("group",mGroupId,String.valueOf(mGroupId));
                break;

            case R.id.btn_share:
            case R.id.btn_update:
                isValid();
                break;

        }
    }
    public void isValid() {
        if (indiShow) {
            if (!mAdapter2.getSelectedgroups().equals("")) {
                progressBar2.setVisibility(View.VISIBLE);
                addPost("personal",mGroupId,mAdapter2.getSelectedgroups());
            } else {
                Toast.makeText(this, "Please select any friend first", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (!mAdapter.getSelectedgroups().equals("")) {
                progressBar.setVisibility(View.VISIBLE);
                addPost("team",mGroupId, mAdapter.getSelectedgroups());
            } else {
                Toast.makeText(this, "Please select any team first", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void addPost(String postType,String selected_g_id,String selected_ids) {

            LeafManager manager = new LeafManager();
            AddPostRequestDescription request = new AddPostRequestDescription();
            request.text = GroupDashboardActivityNew.share_desc;
            request.title = GroupDashboardActivityNew.share_title;

            switch (GroupDashboardActivityNew.share_type) {
                case "group":
                    switch (postType) {
                        case "team":
                            manager.shareGroupPost(this, request, mGroupId+"", postId+"", selected_ids, selected_g_id+"", "team_edit");
                            break;
                        case "group":
                            manager.shareGroupPost(this, request, mGroupId+"", postId+"", selected_ids, selected_g_id+"", "group_edit");
                            break;
                        case "personal":
                            manager.shareGroupPost(this, request, mGroupId+"", postId+"", selected_ids, selected_g_id+"", "personal_edit");
                            break;
                    }
                    break;
                case "team":
                    switch (postType) {
                        case "team":
                            manager.shareTeamPost(this, request, mGroupId+"", GroupDashboardActivityNew.team_id+"", postId+"", selected_ids, selected_g_id+"", "team_edit");
                            break;
                        case "group":
                            manager.shareTeamPost(this, request, mGroupId+"", GroupDashboardActivityNew.team_id+"", postId+"", selected_ids, selected_g_id+"", "group_edit");
                            break;
                        case "personal":
                            manager.shareTeamPost(this, request, mGroupId+"", GroupDashboardActivityNew.team_id+"", postId+"", selected_ids, selected_g_id+"", "personal_edit");
                            break /*label*/;
                    }
                    break;
                case "personal":
                    switch (postType) {
                        case "team":
                            manager.sharePersonalPost(this, request, mGroupId+"", postId+"", selected_ids, selected_g_id+"", "team_edit");
                            break;
                        case "group":
                            manager.sharePersonalPost(this, request, mGroupId+"", postId+"", selected_ids, selected_g_id+"", "group_edit");
                            break;
                        case "personal":
                            manager.sharePersonalPost(this, request, mGroupId+"", postId+"", selected_ids, selected_g_id+"", "personal_edit");
                            break /*label*/;
                    }
                    break;
            }
    }


    public void updateCount() {
        if (selectCount > 0) {
            ((TextView) findViewById(R.id.txtCount)).setText(selectCount + "");
        } else {
            ((TextView) findViewById(R.id.txtCount)).setText("");
        }
    }

    private class TaskForFriends extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(PushActivity.this);
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<GruppieContactsModel> userIdList = GruppieContactsModel.getAll();

            for (int i = 0; i < userIdList.size(); i++) {
                ShareGroupItemList itemList = new ShareGroupItemList(userIdList.get(i).contact_id + "",
                        userIdList.get(i).contact_name, userIdList.get(i).contact_image,
                        userIdList.get(i).contact_phone, false);
                mData2.add(itemList);
                AppLog.e("ASDFff", "id " + userIdList.get(i).contact_id);
            }
            mAdapter2.addItems(mData2);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar2.setVisibility(View.GONE);
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
            recyclerView2.setAdapter(mAdapter2);
            mIsLoading2 = false;
        }
    }


    @Override
    public void onNameClick(PersonalPostItem item) {
        addPost("personal",mGroupId,mAdapter2.getSelectedgroups());
    }
}
