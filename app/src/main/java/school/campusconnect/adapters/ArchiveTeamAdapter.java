package school.campusconnect.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class ArchiveTeamAdapter extends RecyclerView.Adapter<ArchiveTeamAdapter.ViewHolder> {
    ArrayList<MyTeamData> teamList;
    Context context;
    ArchiveTeamListener listener;
    public ArchiveTeamAdapter(ArrayList<MyTeamData> teamList,ArchiveTeamListener listener) {
        this.teamList = teamList;
        this.listener=listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.archive_team_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MyTeamData team = teamList.get(position);

        AppLog.e("TeamListAdapterNew", "item ; " + new Gson().toJson(team));
        holder.tvTeamName.setText(team.name);

        if (!TextUtils.isEmpty(team.image)) {
            holder.imgTeam.setVisibility(View.VISIBLE);
            holder.imgDefault.setVisibility(View.GONE);
            Picasso.with(context).load(Constants.decodeUrlToBase64(team.image)).resize(dpToPx(),dpToPx()).into(holder.imgTeam, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    holder.imgTeam.setVisibility(View.INVISIBLE);
                    holder.imgDefault.setVisibility(View.VISIBLE);

                    TextDrawable drawable = TextDrawable.builder()
                            .buildRound(ImageUtil.getTextLetter(team.name), ImageUtil.getRandomColor(position));
                    holder.imgDefault.setImageDrawable(drawable);
                }
            });
        } else {
            holder.imgTeam.setVisibility(View.INVISIBLE);
            holder.imgDefault.setVisibility(View.VISIBLE);

            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(ImageUtil.getTextLetter(team.name), ImageUtil.getRandomColor(position));
            holder.imgDefault.setImageDrawable(drawable);
        }
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.flRestore)
        FrameLayout flRestore;

        @Bind(R.id.rlMain)
        RelativeLayout rlMain;

        @Bind(R.id.imgTeam)
        ImageView imgTeam;

        @Bind(R.id.imgDefault)
        ImageView imgDefault;

        @Bind(R.id.tvTeamName)
        TextView tvTeamName;

        @Bind(R.id.swipeRevealLayout)
        SwipeRevealLayout swipeRevealLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            swipeRevealLayout.close(true);


            flRestore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRestoreClick(getAdapterPosition());
                    swipeRevealLayout.close(true);
                }
            });

            rlMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
    public interface ArchiveTeamListener
    {
        public void onItemClick(int position);
        public void onRestoreClick(int position);
    }

    private int dpToPx() {
        return context.getResources().getDimensionPixelSize(R.dimen.group_list_image_size);
    }
}
