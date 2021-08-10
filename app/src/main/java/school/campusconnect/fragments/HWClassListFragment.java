package school.campusconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.HWClassSubjectActivity;
import school.campusconnect.activities.RecClassSubjectActivity;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.videocall.VideoClassResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class HWClassListFragment extends BaseFragment implements LeafManager.OnCommunicationListener{
    private static final String TAG = "VideoClassListFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    @Bind(R.id.swipeRefreshLayout)
    public PullRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss_refresh, container, false);
        ButterKnife.bind(this, view);
        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getVideoClassList();
        init();
    }

    private void init() {
        swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    apiCall();
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    showNoNetworkMsg();
                }
            }
        });
    }

    private void getVideoClassList() {
        String re = LeafPreference.getInstance(getActivity()).getString("video_class_group_id_" + GroupDashboardActivityNew.groupId);
        if (re != null && !TextUtils.isEmpty(re)) {
            AppLog.e(TAG, "Api Calling::: if ");
            ArrayList<VideoClassResponse.ClassData> result = new Gson().fromJson(re, new TypeToken<List<VideoClassResponse.ClassData>>() {
            }.getType());
            rvClass.setAdapter(new ClassesAdapter(result));
        } else {
            AppLog.e(TAG, "Api Calling::: else ");
            apiCall();
        }
    }

    private void apiCall() {
        LeafManager leafManager = new LeafManager();
        progressBar.setVisibility(View.VISIBLE);
        leafManager.getVideoClasses(this, GroupDashboardActivityNew.groupId);
    }

    @Override
    public void onResume() {
        super.onResume();

        LeafManager leafManager = new LeafManager();

        leafManager.getVideoClasses(this, GroupDashboardActivityNew.groupId);

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);
        VideoClassResponse res = (VideoClassResponse) response;
        List<VideoClassResponse.ClassData> result = res.getData();
        AppLog.e(TAG, "ClassResponse " + result);
        LeafPreference.getInstance(getActivity()).setString("video_class_group_id_" + GroupDashboardActivityNew.groupId, new Gson().toJson(result));
        rvClass.setAdapter(new ClassesAdapter(result));
    }

    @Override
    public void onFailure(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }


    public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder> {
        List<VideoClassResponse.ClassData> list;
        private Context mContext;

        public ClassesAdapter(List<VideoClassResponse.ClassData> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final VideoClassResponse.ClassData item = list.get(position);

            if (!TextUtils.isEmpty(item.getImage())) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(50, 50).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgTeam,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.img_lead_default.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(50, 50).into(holder.imgTeam, new Callback() {
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

            if(item.canPost || item.isLive){
                holder.img_tree.setVisibility(View.VISIBLE);
            }else {
                holder.img_tree.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    txtEmpty.setText("No Class found.");
                } else {
                    txtEmpty.setText("");
                }

                return list.size();
            } else {
                txtEmpty.setText("No Class found.");
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
                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onTreeClick(list.get(getAdapterPosition()));
                    }
                });

            }
        }
    }

    private void onTreeClick(VideoClassResponse.ClassData classData)
    {
        Intent intent = new Intent(getContext(), HWClassSubjectActivity.class);
        intent.putExtra("team_id",classData.getId());
        intent.putExtra("title",classData.className);
        startActivity(intent);

    }
}
