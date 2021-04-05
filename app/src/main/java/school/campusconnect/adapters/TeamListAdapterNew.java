package school.campusconnect.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.utils.AppLog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import school.campusconnect.R;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class TeamListAdapterNew extends RecyclerView.Adapter<TeamListAdapterNew.ViewHolder> {

    private final ArrayList<MyTeamData> teamList;
    Context context;
    OnTeamClickListener listener;
    int selectedPos = 0;

    public TeamListAdapterNew(ArrayList<MyTeamData> teamList, OnTeamClickListener listener) {
        this.teamList = teamList;
        this.listener = listener;
    }

    public void setSelectedPos(int selectedPos) {
        this.selectedPos = selectedPos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

       /* if (selectedPos == position) {
            holder.tvBlur.setAlpha(0);
        } else {
            holder.tvBlur.setAlpha(0.5f);
        }*/

        /*if(teamList.size()!=position)
        {*/
            final MyTeamData team = teamList.get(position);

            AppLog.e("TeamListAdapterNew", "item ; " + new Gson().toJson(team));
            holder.tvTeamName.setText(team.name);
            holder.tvPostCount.setText(team.postUnseenCount + "");
            holder.tvPostCount.setVisibility(team.postUnseenCount != 0 ? View.VISIBLE : View.GONE);

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
       /* }
        else
        {
            holder.imgTeam.setVisibility(View.GONE);
            holder.tvPostCount.setVisibility(View.GONE);
            holder.imgTeamAdd.setVisibility(View.VISIBLE);
            holder.tvTeamName.setText("ADD");
        }*/
    }

    @Override
    public int getItemCount() {
            return teamList.size();
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*if (selectedPos != getAdapterPosition()) {*/
                        if(teamList.size()!=getAdapterPosition())
                        {
                            if (TextUtils.isEmpty(teamList.get(getAdapterPosition()).teamId)) {
                                listener.onGroupClick(teamList.get(getAdapterPosition()));
                                teamList.get(getAdapterPosition()).postUnseenCount = 0;
                            } else {
                                teamList.get(getAdapterPosition()).postUnseenCount = 0;
                                listener.onTeamClick(teamList.get(getAdapterPosition()));
                            }

                            /*int prevPos = selectedPos;
                            selectedPos = getAdapterPosition();
                            tvBlur.setAlpha(0);*/
                            tvPostCount.setText("0");
                            tvPostCount.setVisibility(View.GONE);
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
    }

    public interface OnTeamClickListener {
         void onTeamClick(MyTeamData team);
         void onGroupClick(MyTeamData group);
        void addTeam();
    }
}