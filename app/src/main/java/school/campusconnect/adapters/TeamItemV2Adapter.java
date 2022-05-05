package school.campusconnect.adapters;

import android.content.Context;
import android.icu.text.Transliterator;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.ItemTeamV2Binding;
import school.campusconnect.datamodel.baseTeam.BaseTeamv2Response;
import school.campusconnect.datamodel.notificationList.CountNotificationTBL;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class TeamItemV2Adapter extends RecyclerView.Adapter<TeamItemV2Adapter.ViewHolder> {

    public static final String TAG = "TeamItemV2Adapter";

    ArrayList<MyTeamData> featuredIconData;
    Context context;
    OnTeamClickListener listener;
    LeafPreference leafPreference;
    private Boolean isExpanded = false;
    private int itemCount;
    Transliterator transliterator;

    public TeamItemV2Adapter(ArrayList<MyTeamData> featuredIconData, TeamListAdapterNewV2 listener,int itemCount) {
        this.featuredIconData = featuredIconData;
        this.listener = listener;
        this.itemCount = itemCount;
        transliterator = Transliterator.getInstance("Latin-Kannada");
    }

    @NonNull
    @Override
    public TeamItemV2Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        leafPreference = LeafPreference.getInstance(context);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamItemV2Adapter.ViewHolder holder, int position) {

        final MyTeamData team = featuredIconData.get(position);

        AppLog.e("TeamListAdapterNew", "item ; " + new Gson().toJson(team));
       // holder.tvTeamName.setText(transliterator.transliterate(team.name));

        if (context.getResources().getConfiguration().locale.getLanguage().equalsIgnoreCase("kn"))
        {

            if (team.kanName != null)
            {
                holder.tvTeamName.setText(team.kanName);
            }
            else
            {
                holder.tvTeamName.setText(team.name);
            }

        }
        else
        {
            holder.tvTeamName.setText(team.name);
        }


        int postUnseenCount = 0;

        if(team.teamId != null && !team.teamId.equalsIgnoreCase(""))
        {
            postUnseenCount = leafPreference.getInt(team.teamId+"_post");

            /*if (CountNotificationTBL.getCountNotification(team.teamId).size() > 0)
            {
                Log.e(TAG,"CountNotificationTBL Size "+CountNotificationTBL.getCountNotification(team.teamId).size());

                for (int i = 0;i<CountNotificationTBL.getCountNotification(team.teamId).size();i++)
                {
                    if (team.teamId.equalsIgnoreCase(CountNotificationTBL.getCountNotification(team.teamId).get(i).teamID))
                        postUnseenCount = CountNotificationTBL.getCountNotification(team.teamId).get(i).count;
                        Log.e(TAG,"match Count");
                }

            }*/
        }
        else if(team.name.equalsIgnoreCase("notice board"))
        {
            postUnseenCount = leafPreference.getInt(team.groupId+"_post");
        }
        else if(team.type.equals("Home Work"))
        {
            postUnseenCount = leafPreference.getInt(team.groupId+"_HOMEWORK_NOTI_COUNT");
        }else if(team.type.equals("Recorded Class"))
        {
            postUnseenCount = leafPreference.getInt(team.groupId+"_NOTES_VIDEO_NOTI_COUNT");
        }else if(team.type.equals("Test"))
        {
            postUnseenCount = leafPreference.getInt(team.groupId+"_TEST_EXAM_NOTI_COUNT");
        }

        Log.e(TAG,"postUnseenCount "+postUnseenCount);


            /*else if(team.name.equalsIgnoreCase("vendor connect"))
            {
                postUnseenCount = leafPreference.getInt(team.groupId+"_vendorpush");
            }*/
         /*   else if(team.name.equalsIgnoreCase("E-Books"))
            {
                postUnseenCount = leafPreference.getInt(team.groupId+"_ebookpush");
            }
            else if(team.type.equalsIgnoreCase("Code Of Conduct"))
            {
                postUnseenCount = leafPreference.getInt(team.groupId+"_cocpush");
            }*/

        holder.tvPostCount.setText(postUnseenCount + "");
        holder.tvPostCount.setVisibility(postUnseenCount != 0 ? View.VISIBLE : View.GONE);

            /*int postUnseenCount = 0;
            if(team.teamId != null && !team.teamId.equalsIgnoreCase(""))
            postUnseenCount = leafPreference.getInt(team.teamId+"_post");
            else if(team.name.equalsIgnoreCase("notice board"))
            {
                postUnseenCount = leafPreference.getInt(team.groupId+"_post");
            }

            holder.tvPostCount.setText(postUnseenCount + "");
            holder.tvPostCount.setVisibility(postUnseenCount != 0 ? View.VISIBLE : View.GONE);*/

        if (!TextUtils.isEmpty(team.image)) {
            holder.imgTeam.setVisibility(View.VISIBLE);
            holder.imgDefault.setVisibility(View.GONE);

            Picasso.with(context).load(Constants.decodeUrlToBase64(team.image)).networkPolicy(NetworkPolicy.OFFLINE)/*.resize(150,150)*/.into(holder.imgTeam, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(Constants.decodeUrlToBase64(team.image))/*.resize(150,150)*/.into(holder.imgTeam, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            holder.imgTeam.setVisibility(View.GONE);
                            holder.imgDefault.setVisibility(View.VISIBLE);

                            TextDrawable drawable = TextDrawable.builder()
                                    .buildRoundRect(ImageUtil.getTextLetter(team.name), ImageUtil.getRandomColor(position),30);
                            holder.imgDefault.setImageDrawable(drawable);
                        }
                    });
                }
            });


        } else {
            holder.imgTeam.setVisibility(View.GONE);
            holder.imgDefault.setVisibility(View.VISIBLE);

            TextDrawable drawable = TextDrawable.builder()
                    .buildRoundRect(ImageUtil.getTextLetter(team.name), ImageUtil.getRandomColor(position),30);
            holder.imgDefault.setImageDrawable(drawable);
        }
        holder.imgTeamAdd.setVisibility(View.GONE);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (selectedPos != getAdapterPosition()) {*/
                if(featuredIconData.size()!=position)
                {
                    if (TextUtils.isEmpty(team.teamId)) {
                        listener.onGroupClick(team);
                        team.postUnseenCount = 0;
                    } else {
                        team.postUnseenCount = 0;
                        listener.onTeamClick(team);
                    }

                            /*int prevPos = selectedPos;
                            selectedPos = getAdapterPosition();
                            tvBlur.setAlpha(0);*/
                    holder.tvPostCount.setText("0");
                    holder.tvPostCount.setVisibility(View.GONE);
                    //notifyItemChanged(prevPos);
                }
                else
                {
                    listener.addTeam();
                }

            }

            //}
        });
    }

    public void isExpanded()
    {
        if (isExpanded)
        {
            isExpanded = false;
        }
        else
        {
            isExpanded = true;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return featuredIconData != null ? isExpanded ? featuredIconData.size() : itemCount>featuredIconData.size()?featuredIconData.size():itemCount : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgTeamAdd;
        TextView tvTeamName, tvBlur, tvPostCount;
        ImageView imgTeam,imgDefault;

        public ViewHolder(View itemView) {
            super(itemView);
            imgTeam = itemView.findViewById(R.id.imgTeam);
            imgTeamAdd = itemView.findViewById(R.id.imgTeamAdd);
            imgDefault = itemView.findViewById(R.id.imgDefault);
            tvTeamName = itemView.findViewById(R.id.tvTeamName);
            tvBlur = itemView.findViewById(R.id.tvBlur);
            tvPostCount = itemView.findViewById(R.id.tvPostCount);

        }
    }
    public interface OnTeamClickListener {
        void onTeamClick(MyTeamData team);
        void onGroupClick(MyTeamData group);
        void addTeam();
    }
}
