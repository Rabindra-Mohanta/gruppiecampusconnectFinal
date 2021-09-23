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
import school.campusconnect.activities.CalendarActivity;
import school.campusconnect.activities.GalleryActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.PeopleActivity;
import school.campusconnect.activities.ReadMoreActivity;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.notificationList.NotificationListRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.MixOperations;

public class NotificationListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
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
        View view = inflater.inflate(R.layout.fragment_notification_list,container,false);
        ButterKnife.bind(this,view);
        rvPeople.setLayoutManager(new LinearLayoutManager(getActivity()));



        LeafManager leafManager = new LeafManager();
        progressBar.setVisibility(View.VISIBLE);

        Bundle bundle=getArguments();
        if(bundle!=null)
        {
            isNest=bundle.getBoolean("isNest",false);
            userId=bundle.getString("userId","");
        }

        if(!isNest)
        {
            progressBar.setVisibility(View.VISIBLE);
            //leafManager.getMyPeople(this,GroupDashboardActivityNew.groupId);
            leafManager.getNotificationList(this,GroupDashboardActivityNew.groupId);
        }
        else
        {
            progressBar.setVisibility(View.VISIBLE);
            leafManager.getNotificationList(this,GroupDashboardActivityNew.groupId);
        }
        return view;
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);
        NotificationListRes res = (NotificationListRes) response;
        List<NotificationListRes.NotificationListData> result = res.getData();
        AppLog.e(TAG, "notificationRes " + result);

        rvPeople.setAdapter(new PeopleAdapter(result));
    }

    @Override
    public void onFailure(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }

    public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder>
    {
        List<NotificationListRes.NotificationListData> list;
        private Context mContext;

        public PeopleAdapter(List<NotificationListRes.NotificationListData> list) {
            this.list = list;
        }

        @Override
        public PeopleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final PeopleAdapter.ViewHolder holder, final int position) {
            final NotificationListRes.NotificationListData item = list.get(position);

            if (!TextUtils.isEmpty(item.getCreatedByImage())) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getCreatedByImage())).fit().networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgTeam,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.img_lead_default.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getCreatedByImage())).fit().into(holder.imgTeam, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.img_lead_default.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.img_lead_default.setVisibility(View.VISIBLE);
                                        TextDrawable drawable = TextDrawable.builder()
                                                .buildRound(ImageUtil.getTextLetter(item.getCreatedByImage()), ImageUtil.getRandomColor(position));
                                        holder.img_lead_default.setImageDrawable(drawable);
                                        AppLog.e("Picasso", "Error : ");
                                    }
                                });
                            }
                        });
            } else {
                holder.img_lead_default.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(item.getCreatedByName()), ImageUtil.getRandomColor(position));
                holder.img_lead_default.setImageDrawable(drawable);
            }

            holder.txt_name.setText(item.getCreatedByName());
            holder.txt_count.setText(item.getMessage());

            holder.date_time.setText(MixOperations.getFormattedDate(item.getInsertedAt(),Constants.DATE_FORMAT));
        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    txtEmpty.setText("No Notification found.");
                }
                return list.size();
            }
            else
            {
                txtEmpty.setText("No Notification found.");
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
            @Bind(R.id.date_time)
            TextView date_time;


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
                     //   onMsgClick(list.get(getAdapterPosition()));
                    }
                });
                img_tree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      //  onTreeClick(list.get(getAdapterPosition()));
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClick(list.get(getAdapterPosition()));
                    }
                });

            }
        }
    }

    public void onItemClick(NotificationListRes.NotificationListData item)
    {
        AppLog.d(TAG,"onItemClick()");

        Intent i = new Intent(getContext(),ReadMoreActivity.class);
        Bundle bundle = new Bundle();

        if("schoolCalendar".equals(item.getType())){
            startActivity(new Intent(getContext(), CalendarActivity.class).putExtra("date",item.getInsertedAt()));
            return;
        }

        if("groupPost".equals(item.getType()) && !item.getShowComment())
        {
            //then redirect to "/api/v1/groups/{groupId}/post/{postId}/read"
            bundle.putString("groupId",item.getGroupId());
            bundle.putString("postId",item.getPostId());
            bundle.putString("type",item.getType());
            bundle.putString("userId",item.getUserId());
            i.putExtras(bundle);
            startActivity(i);
        }
        else if("gallery".equals(item.getType()) && !item.getShowComment())
        {
            startActivity(new Intent(getContext(), GalleryActivity.class));
        }
        else   if("teamPost".equals(item.getType()) && !item.getShowComment())
        {
            //"/api/v1/groups/{groupId}/team/{teamId}/post/{postId}/read"
            bundle.putString("groupId",item.getGroupId());
            bundle.putString("teamId",item.getTeamId());
            bundle.putString("postId",item.getPostId());
            bundle.putString("type",item.getType());
            bundle.putString("userId",item.getCreatedById());
            i.putExtras(bundle);
            startActivity(i);
        }
        else if("individualPost".equals(item.getType()) && !item.getShowComment())
        {
            //"/api/v1/groups/{groupId}/user/{userId}/post/{postId}/read"
            bundle.putString("groupId",item.getGroupId());
            bundle.putString("userId",item.getUserId());
            bundle.putString("postId",item.getPostId());
            bundle.putString("type",item.getType());
            i.putExtras(bundle);
            startActivity(i);
        }
        else if("groupPostComment".equals(item.getType()) && item.getShowComment())
        {
            //"/api/v1/groups/{groupId}/post/{postId}/read"
            bundle.putString("groupId",item.getGroupId());
            bundle.putString("postId",item.getPostId());
            bundle.putString("type",item.getType());
            bundle.putString("userId",item.getUserId());
            i.putExtras(bundle);
            startActivity(i);
        }
        else if("teamPostComment".equals(item.getType())  && item.getShowComment())
        {
            ///api/v1/groups/{groupId}/team/{teamId}/post/{postId}/read"
            bundle.putString("groupId",item.getGroupId());
            bundle.putString("teamId",item.getTeamId());
            bundle.putString("postId",item.getPostId());
            bundle.putString("type",item.getType());
            bundle.putString("userId",item.getCreatedById());
            i.putExtras(bundle);
            startActivity(i);

        }
        else if("individualPostComment".equals(item.getType()) && item.getShowComment())
        {
            ///api/v1/groups/{groupId}/user/{userId}/post/{postId}/read"
            bundle.putString("groupId",item.getGroupId());
            bundle.putString("userId",item.getUserId());
            bundle.putString("postId",item.getPostId());
            bundle.putString("type",item.getType());
            i.putExtras(bundle);
            startActivity(i);
        }

    }

    public void onMsgClick(NotificationListRes.NotificationListData item)
    {
        AppLog.d(TAG,"onMsgClick()");
        Intent intent = new Intent(getActivity(), AddPostActivity.class);
        intent.putExtra("id", GroupDashboardActivityNew.groupId);
        intent.putExtra("friend_id", item.getGroupId());
        intent.putExtra("type", "personal");
        intent.putExtra("team_id", "");
        intent.putExtra("from_chat", false);
        startActivity(intent);
    }
    public void onTreeClick(NotificationListRes.NotificationListData item)
    {
        Intent intent = new Intent(getActivity(),PeopleActivity.class);
        intent.putExtra("userId",item.getGroupId());
        intent.putExtra("isNest",true);
        intent.putExtra("title","People of "+item.getCreatedByName());
        startActivity(intent);
    }

}
