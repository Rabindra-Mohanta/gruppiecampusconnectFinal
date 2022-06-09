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
import school.campusconnect.activities.AttendanceDetailActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.MarkSheetListActivity;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.classs.ParentKidsResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class ParentKidListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;
    boolean isForAttendance;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            isForAttendance = getArguments().getBoolean("is_for_attendance");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss,container,false);
        ButterKnife.bind(this,view);
        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));


        showLoadingBar(progressBar,false);
        //   progressBar.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LeafManager leafManager = new LeafManager();
        leafManager.getParentKids(this,GroupDashboardActivityNew.groupId);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
      //  progressBar.setVisibility(View.GONE);
        ParentKidsResponse res = (ParentKidsResponse) response;
        List<ParentKidsResponse.ParentKidsData> result = res.getData();
        AppLog.e(TAG, "ClassResponse " + result);

        rvClass.setAdapter(new ClassesAdapter(result));
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

    public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder>
    {
        List<ParentKidsResponse.ParentKidsData> list;
        private Context mContext;

        public ClassesAdapter(List<ParentKidsResponse.ParentKidsData> list) {
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
            final ParentKidsResponse.ParentKidsData item = list.get(position);

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
            holder.txt_count.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    txtEmpty.setText(getResources().getString(R.string.txt_no_kid_found));
                }
                else {
                    txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                txtEmpty.setText(getResources().getString(R.string.txt_no_kid_found));
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

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onTreeClick(list.get(getAdapterPosition()));
                    }
                });

            }
        }
    }

    private void onTreeClick(ParentKidsResponse.ParentKidsData classData) {
        if(isForAttendance){
            Intent intent = new Intent(getActivity(), AttendanceDetailActivity.class);
            intent.putExtra("group_id",GroupDashboardActivityNew.groupId);
            intent.putExtra("team_id",classData.teamId);
            intent.putExtra("title",classData.name);
            intent.putExtra("userId",classData.userId);
            intent.putExtra("rollNo",classData.rollNumber);
            startActivity(intent);
        }else {
            Intent intent = new Intent(getActivity(), MarkSheetListActivity.class);
            intent.putExtra("group_id",GroupDashboardActivityNew.groupId);
            intent.putExtra("team_id",classData.teamId);
            intent.putExtra("user_id",classData.getUserId());
            intent.putExtra("name",classData.name);
            intent.putExtra("roll_no",classData.getRollNumber());
            intent.putExtra("role","parent");
            startActivity(intent);
        }

    }
}
