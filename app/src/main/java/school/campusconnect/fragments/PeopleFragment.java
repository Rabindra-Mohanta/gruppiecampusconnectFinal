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
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.AddPostActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.PeopleActivity;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.LeadItem;
import school.campusconnect.datamodel.LeadResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class PeopleFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvPeople;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;
    private boolean isNest;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss,container,false);
        ButterKnife.bind(this,view);
        rvPeople.setLayoutManager(new LinearLayoutManager(getActivity()));



        LeafManager leafManager = new LeafManager();
        showLoadingBar(progressBar);
        //progressBar.setVisibility(View.VISIBLE);

        Bundle bundle=getArguments();
        if(bundle!=null)
        {
            isNest=bundle.getBoolean("isNest",false);
            userId=bundle.getString("userId","");
        }

        if(!isNest)
        {
            showLoadingBar(progressBar);
            //progressBar.setVisibility(View.VISIBLE);
            leafManager.getMyPeople(this,GroupDashboardActivityNew.groupId);
        }
        else
        {
            showLoadingBar(progressBar);
            //progressBar.setVisibility(View.VISIBLE);
            leafManager.getNestPeople(this,GroupDashboardActivityNew.groupId,userId);
        }
        return view;
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
     //   progressBar.setVisibility(View.GONE);
        LeadResponse res = (LeadResponse) response;
        List<LeadItem> result = res.getResults();
        AppLog.e(TAG, "peopleResponse " + result);

        rvPeople.setAdapter(new PeopleAdapter(result));
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        //   progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        //   progressBar.setVisibility(View.GONE);
    }

    public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder>
    {
        List<LeadItem> list;
        private Context mContext;

        public PeopleAdapter(List<LeadItem> list) {
            this.list = list;
        }

        @Override
        public PeopleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_people,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final PeopleAdapter.ViewHolder holder, final int position) {
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
            holder.txt_count.setText("People : "+item.leadCount);
        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    txtEmpty.setText("No People found.");
                }
                return list.size();
            }
            else
            {
                txtEmpty.setText("No People found.");
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

            @Bind(R.id.img_chat)
            ImageView img_chat;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);

                img_chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onMsgClick(list.get(getAdapterPosition()));
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
    public void onMsgClick(LeadItem item)
    {
        AppLog.d(TAG,"onMsgClick()");
        Intent intent = new Intent(getActivity(), AddPostActivity.class);
        intent.putExtra("id", GroupDashboardActivityNew.groupId);
        intent.putExtra("friend_id", item.getId());
        intent.putExtra("friend_name", item.getName());
        intent.putExtra("type", "personal");
        intent.putExtra("team_id", "");
        intent.putExtra("from_chat", false);
        startActivity(intent);
    }
    public void onTreeClick(LeadItem leadItem)
    {
        Intent intent = new Intent(getActivity(),PeopleActivity.class);
        intent.putExtra("userId",leadItem.id);
        intent.putExtra("isNest",true);
        intent.putExtra("title","People of "+leadItem.name);
        startActivity(intent);
    }

}
