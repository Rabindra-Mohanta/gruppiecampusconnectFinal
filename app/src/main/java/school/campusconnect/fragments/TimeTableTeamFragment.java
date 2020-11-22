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
import school.campusconnect.activities.AddTimeTablePostActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.datamodel.teamdiscussion.MyTeamsResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class TimeTableTeamFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvTeams;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_table_team,container,false);
        ButterKnife.bind(this,view);


        rvTeams.setLayoutManager(new LinearLayoutManager(getActivity()));


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LeafManager leafManager = new LeafManager();
        progressBar.setVisibility(View.VISIBLE);
        leafManager.myTeamListNew(this,GroupDashboardActivityNew.groupId);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);
        MyTeamsResponse res = (MyTeamsResponse) response;
        List<MyTeamData> result = res.getResults();
        AppLog.e(TAG, "Team name is " + result);

        rvTeams.setAdapter(new TeamTimeTable(result));
    }

    @Override
    public void onFailure(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }

    public class TeamTimeTable extends RecyclerView.Adapter<TeamTimeTable.ViewHolder>
    {
        List<MyTeamData> list;
        private Context mContext;

        public TeamTimeTable(List<MyTeamData> list) {
            this.list = list;
        }

        @Override
        public TeamTimeTable.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_table_team,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final TeamTimeTable.ViewHolder holder, final int position) {
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
            holder.txt_count.setText("Members : "+item.members);
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
            }
        }
    }
    public void onItemClick(MyTeamData myTeamData)
    {
        Intent intent2=new Intent(getActivity(),AddTimeTablePostActivity.class);
        intent2.putExtra("group_id",GroupDashboardActivityNew.groupId);
        intent2.putExtra("team_id",myTeamData.teamId);
        startActivity(intent2);
    }
}
