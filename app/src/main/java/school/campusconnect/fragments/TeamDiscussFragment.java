package school.campusconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.LeadsListActivity;
import school.campusconnect.activities.TeamListActivity;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.datamodel.teamdiscussion.MyTeamsResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class TeamDiscussFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvTeams;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss,container,false);
        ButterKnife.bind(this,view);


        rvTeams.setLayoutManager(new LinearLayoutManager(getActivity()));


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LeafManager leafManager = new LeafManager();
        showLoadingBar(progressBar);
      //  progressBar.setVisibility(View.VISIBLE);
        leafManager.myTeamListNew(this,GroupDashboardActivityNew.groupId);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
      //  progressBar.setVisibility(View.GONE);
        MyTeamsResponse res = (MyTeamsResponse) response;
        List<MyTeamData> result = res.getResults();
        AppLog.e(TAG, "Team name is " + result);

        rvTeams.setAdapter(new TeamDiscussNew(result));
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        //  progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        //  progressBar.setVisibility(View.GONE);
    }

    public class TeamDiscussNew extends RecyclerView.Adapter<TeamDiscussNew.ViewHolder>
    {
        List<MyTeamData> list;
        private Context mContext;

        public TeamDiscussNew(List<MyTeamData> list) {
            this.list = list;
        }

        @Override
        public TeamDiscussNew.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_discuss,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final TeamDiscussNew.ViewHolder holder, final int position) {
            final MyTeamData item = list.get(position);

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
            holder.txt_count.setText(getResources().getString(R.string.lbl_members)+" : "+item.members);
        }

        @Override
        public int getItemCount() {
            if(list!=null)
                return list.size();
            else
                return 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.imgTeam)
            ImageView imgTeam;

            @Bind(R.id.imgDefault)
            ImageView img_lead_default;

            @Bind(R.id.tvTeamName)
            TextView txt_name;

            @Bind(R.id.txt_count)
            TextView txt_count;



            @Bind(R.id.img_tree)
            ImageView img_tree;

            @Bind(R.id.rlMain)
            RelativeLayout rlMain;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);

                rlMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClick(list.get(getAdapterPosition()));
                    }
                });
                img_tree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onTreeClick(list.get(getAdapterPosition()));
                    }
                });

            }
        }
    }
    public void onItemClick(MyTeamData myTeamData)
    {
        ((TeamListActivity)getActivity()).teamPost(myTeamData);
    }
    public void onTreeClick(MyTeamData myTeamData)
    {
        try {
            Intent intent = new Intent(getActivity(), LeadsListActivity.class);
            intent.putExtra("id", GroupDashboardActivityNew.groupId);
            intent.putExtra("team_id", myTeamData.teamId);
            intent.putExtra("team_name", myTeamData.name);
            intent.putExtra("isAdmin", false);
            intent.putExtra("isNest", true);
            startActivity(intent);
        } catch (Exception e) {
            AppLog.e("floating", "error is " + e.toString());
        }
    }

}
