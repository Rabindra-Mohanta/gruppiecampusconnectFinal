package school.campusconnect.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.LeadItem;
import school.campusconnect.datamodel.LeadResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.views.SMBDialogUtils;

public class TeamUsersActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;

    @Bind(R.id.rvTeams)
    public RecyclerView rvPeople;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;
    private String teamId;
    LeafManager leafManager = new LeafManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_users);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
         setTitle(getResources().getString(R.string.lbl_select_Admin));

        rvPeople.setLayoutManager(new LinearLayoutManager(this));

        teamId = getIntent().getStringExtra("team_id");


        progressBar.setVisibility(View.VISIBLE);
        leafManager.getTeamMember(this,GroupDashboardActivityNew.groupId,teamId,false);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        progressBar.setVisibility(View.GONE);
        if(apiId== LeafManager.API_CHANGE_ADMIN_TEAM){
            gotoHomeScreen();
        }else {
            LeadResponse res = (LeadResponse) response;

            List<LeadItem> result = res.getResults();

            rvPeople.setAdapter(new TeamUserAdapter(result));
        }

    }

    private void gotoHomeScreen()
    {
        Intent intent = new Intent(this, GroupDashboardActivityNew.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFailure(int apiId, String msg) {
        super.onFailure(apiId, msg);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        super.onException(apiId, msg);
        progressBar.setVisibility(View.GONE);
    }

    public class TeamUserAdapter extends RecyclerView.Adapter<TeamUserAdapter.ViewHolder>
    {
        List<LeadItem> list;
        private Context mContext;

        public TeamUserAdapter(List<LeadItem> list) {
            this.list = list;
        }

        @Override
        public TeamUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_people,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final TeamUserAdapter.ViewHolder holder, final int position) {
            final LeadItem item = list.get(position);

            if (!TextUtils.isEmpty(item.image)) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.image)).fit().networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgTeam,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.img_lead_default.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.image)).fit().into(holder.imgTeam, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.img_lead_default.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.img_lead_default.setVisibility(View.VISIBLE);
                                        TextDrawable drawable = TextDrawable.builder()
                                                .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position));
                                        holder.img_lead_default.setImageDrawable(drawable);
                                        AppLog.e("Picasso", "Error : ");
                                    }
                                });
                            }
                        });
            } else {
                holder.img_lead_default.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position));
                holder.img_lead_default.setImageDrawable(drawable);
            }

            holder.txt_name.setText(item.name);
            holder.txt_count.setText(item.phone);
        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    txtEmpty.setText("No Users found.");
                }
                return list.size();
            }
            else
            {
                txtEmpty.setText("No Users found.");
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

            @Bind(R.id.llRight)
            LinearLayout llRight;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
                llRight.setVisibility(View.GONE);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onUserClick(list.get(getAdapterPosition()));
                    }
                });

            }
        }
    }

    private void onUserClick(final LeadItem leadItem) {
        SMBDialogUtils.showSMBDialogOKCancel_(this, "Are you sure you want to change team admin", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBar.setVisibility(View.VISIBLE);
                leafManager.changeTeamAdmin(TeamUsersActivity.this,GroupDashboardActivityNew.groupId,teamId,leadItem.getId());
            }
        });

    }
}
