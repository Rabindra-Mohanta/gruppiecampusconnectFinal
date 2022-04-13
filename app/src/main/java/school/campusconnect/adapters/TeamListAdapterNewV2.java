package school.campusconnect.adapters;

import android.icu.text.Transliterator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

import school.campusconnect.R;
import school.campusconnect.databinding.ItemTeamListV2Binding;
import school.campusconnect.datamodel.baseTeam.BaseTeamv2Response;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;

public class TeamListAdapterNewV2 extends RecyclerView.Adapter<TeamListAdapterNewV2.ViewHolder> implements TeamItemV2Adapter.OnTeamClickListener {
    public static String TAG = "TeamListAdapterNewV2";
    private ArrayList<BaseTeamv2Response.TeamListData> teamData;
    private OnTeamClickListener listener;
    private Boolean isExpanded = false;
    private String Category;
    Transliterator transliterator;

    public TeamListAdapterNewV2(ArrayList<BaseTeamv2Response.TeamListData> teamList,OnTeamClickListener listener,String Category) {
        this.listener = listener;
        this.teamData = teamList;
        this.Category = Category;
        transliterator = Transliterator.getInstance("Latin-Kannada");
    }


    @NonNull
    @Override
    public TeamListAdapterNewV2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTeamListV2Binding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_team_list_v2,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamListAdapterNewV2.ViewHolder holder, int position) {

        BaseTeamv2Response.TeamListData data = teamData.get(position);

        //holder.binding.tvActivityName.setText(transliterator.transliterate(data.getActivity()));
        holder.binding.tvActivityName.setText(data.getActivity());

        if (Category.equalsIgnoreCase("constituency"))
        {
            holder.binding.rvActivityName.setAdapter(new TeamItemV2Adapter(data.getFeaturedIconData(),this,8));
        }
        else
        {
            holder.binding.rvActivityName.setAdapter(new TeamItemV2Adapter(data.getFeaturedIconData(),this,8));
        }


        holder.binding.imgExpandFeedBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.binding.imgExpandFeedBefore.setVisibility(View.GONE);
                holder.binding.imgExpandFeedAfter.setVisibility(View.VISIBLE);
                ((TeamItemV2Adapter) holder.binding.rvActivityName.getAdapter()).isExpanded();
            }
        });
        holder.binding.imgExpandFeedAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.binding.imgExpandFeedAfter.setVisibility(View.GONE);
                holder.binding.imgExpandFeedBefore.setVisibility(View.VISIBLE);
                ((TeamItemV2Adapter) holder.binding.rvActivityName.getAdapter()).isExpanded();
            }
        });

    }

    @Override
    public int getItemCount() {
        return teamData != null ? teamData.size() : 0;
    }

    @Override
    public void onTeamClick(MyTeamData team) {
        Log.e(TAG,"Team Data :"+new Gson().toJson(team));
        listener.onTeamClick(team);
    }

    @Override
    public void onGroupClick(MyTeamData group) {
        Log.e(TAG,"Group Data :"+new Gson().toJson(group));
        listener.onGroupClick(group);
    }

    @Override
    public void addTeam() {
        listener.addTeamClick();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemTeamListV2Binding binding;
        public ViewHolder(@NonNull ItemTeamListV2Binding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
    public interface OnTeamClickListener {
        void onTeamClick(MyTeamData team);
        void onGroupClick(MyTeamData group);
        void addTeamClick();
    }
}
