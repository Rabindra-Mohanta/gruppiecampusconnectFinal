package school.campusconnect.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.AddPostRequest;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.GroupItem;
import school.campusconnect.datamodel.sharepost.ShareGroupItemList;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.datamodel.teamdiscussion.MyTeamsResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.views.SMBDialogUtils;

/**
 * Created by frenzin04 on 4/14/2017.
 */

public class NewShareActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<AddPostValidationError> {

    private static final String TAG = "PushActivity";
    public static int selectCount;
    public Toolbar mToolBar;
    RecyclerView recyclerView;

    ProgressBar progressBar;
    TextView txtEmpty;

    String mGroupId = "";

    public boolean mIsLoading = false;

    public static Activity pushActivity;

    LeafManager leafManager = new LeafManager();
    private String mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_share);
        selectCount = 0;
        pushActivity = this;
        findViews();
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(R.string.lbl_share_post);
        final LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        progressBar.setVisibility(View.GONE);

        GroupItem mGroupItem = new Gson().fromJson(LeafPreference.getInstance(this).getString(Constants.GROUP_DATA), GroupItem.class);
        if(!TextUtils.isEmpty(mGroupItem.getGroupId())){
            mGroupId = mGroupItem.getGroupId();
            AppLog.e(TAG, "GroupID =>" + mGroupId);
        }else {
            Intent intent = new Intent(this, LoginActivity2.class);
            startActivity(intent);
            finish();
            return;
        }

        getData();
        getIntetData();
    }

    private void getIntetData() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    mText = sharedText;
                }
            }
        }
        if(TextUtils.isEmpty(mText)){
            Toast.makeText(pushActivity, "No Data Found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        leafManager.myTeamList(this, mGroupId + "");
    }


    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_MY_TEAM_LIST:
                ArrayList<ShareGroupItemList> mData =new ArrayList<>();
                MyTeamsResponse res = (MyTeamsResponse) response;
                List<MyTeamData> result = res.getResults();
                AppLog.e("DATABASE", "Team name is " + res.getResults().toString());
                for (int i = 0; i < result.size(); i++) {
                    if (i == 0)
                        continue;

                    AppLog.e("DATABASE", "Team name is " + result.get(i).name);

                    if (result.get(i).allowTeamPostAll) {
                        ShareGroupItemList itemList = new ShareGroupItemList(result.get(i).teamId + "",
                                result.get(i).name, result.get(i).image,
                                result.get(i).phone, false);
                        mData.add(itemList);
                    }
                }
                recyclerView.setAdapter(new ClassesAdapter(mData));
                break;
            default:
                Toast.makeText(this, "Shared successfully", Toast.LENGTH_SHORT).show();
                Intent login = new Intent(this, GroupDashboardActivityNew.class);
                login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(login);
                finish();
                break;
        }
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<AddPostValidationError> error) {
        super.onFailure(apiId, error.message);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
        super.onFailure(apiId, msg);
    }

    @Override
    public void onException(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
        super.onException(apiId, msg);
    }

    private void findViews() {
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        txtEmpty = findViewById(R.id.txtEmpty);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder>
    {
        List<ShareGroupItemList> list;
        private Context mContext;

        public ClassesAdapter(List<ShareGroupItemList> list) {
            this.list = list;
        }

        @Override
        public ClassesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ClassesAdapter.ViewHolder holder, final int position) {
            final ShareGroupItemList item = list.get(position);

            if (!TextUtils.isEmpty(item.getImage())) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(50,50).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgTeam,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.img_lead_default.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(50,50).into(holder.imgTeam, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.img_lead_default.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.img_lead_default.setVisibility(View.VISIBLE);
                                        TextDrawable drawable = TextDrawable.builder()
                                                .buildRound(ImageUtil.getTextLetter(item.getName()), ImageUtil.getRandomColor(position));
                                        holder.img_lead_default.setImageDrawable(drawable);
                                        AppLog.e("Picasso", "Error : ");
                                    }
                                });
                            }
                        });
            } else {
                holder.img_lead_default.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(item.getName()), ImageUtil.getRandomColor(position));
                holder.img_lead_default.setImageDrawable(drawable);
            }

            holder.txt_name.setText(item.getName());
        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    txtEmpty.setText("No Class found.");
                }
                else {
                    txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                txtEmpty.setText("No Class found.");
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.img_lead)
            ImageView imgTeam;

            @Bind(R.id.img_lead_default)
            ImageView img_lead_default;

            @Bind(R.id.txt_name)
            TextView txt_name;

            @Bind(R.id.txt_count)
            TextView txt_count;
            @Bind(R.id.img_tree)
            ImageView img_tree;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
                txt_count.setVisibility(View.GONE);
                //img_tree.setVisibility(View.GONE);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addPost(list.get(getAdapterPosition()));
                    }
                });
                img_tree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addPost(list.get(getAdapterPosition()));
                    }
                });

            }
        }
    }


    public void addPost(ShareGroupItemList team) {
        SMBDialogUtils.showSMBDialogOKCancel(this, getResources().getString(R.string.smb_share_in) + team.getName() + getResources().getString(R.string.smb_), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBar.setVisibility(View.VISIBLE);
                AddPostRequest request = new AddPostRequest();
                request.text = mText;
                request.title = "";
                leafManager.addPost(NewShareActivity.this, mGroupId, team.getId(), request, "team", "0", false);
            }
        });
    }

}
