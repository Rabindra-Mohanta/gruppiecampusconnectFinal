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
import school.campusconnect.activities.MarksCardActivity2;
import school.campusconnect.activities.RecClassSubjectActivity;
import school.campusconnect.activities.TimeTabelActivity2;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ClassListTBL;
import school.campusconnect.datamodel.SubjectItem;
import school.campusconnect.datamodel.TeamCountTBL;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.subjects.SubjectStaffResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.MixOperations;

public class HWClassListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "HWClassListFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;
/*
    @Bind(R.id.swipeRefreshLayout)
    public PullRefreshLayout swipeRefreshLayout;*/

    String role;
    String type;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss, container, false);
        ButterKnife.bind(this, view);
        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        role = getArguments().getString("role");
        type = getArguments().getString("type");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();

        getDataLocally();
    }

    private void init() {

        AppLog.e(TAG, "HWClassListFragment");
      /*  swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    swipeRefreshLayout.setRefreshing(false);
                    apiCall();
                } else {
                    showNoNetworkMsg();
                }
            }
        });*/
    }

    private void getDataLocally() {
        List<ClassListTBL> list = ClassListTBL.getAll(GroupDashboardActivityNew.groupId);
        if (list.size() != 0) {
            ArrayList<ClassResponse.ClassData> result = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                ClassListTBL currentItem = list.get(i);
                ClassResponse.ClassData item = new ClassResponse.ClassData();
                item.id = currentItem.teamId;
                item.teacherName = currentItem.teacherName;
                item.phone = currentItem.phone;
                item.members = currentItem.members;
                item.countryCode = currentItem.countryCode;
                item.className = currentItem.name;
                item.classImage = currentItem.image;
                item.category = currentItem.category;
                item.jitsiToken = currentItem.jitsiToken;
                item.userId = currentItem.userId;
                item.rollNumber = currentItem.rollNumber;
                result.add(item);
            }
            rvClass.setAdapter(new ClassesAdapter(result));

            TeamCountTBL dashboardCount = TeamCountTBL.getByTypeAndGroup("ALL", GroupDashboardActivityNew.groupId);
            if (dashboardCount != null) {
                boolean apiCall = false;
                if (dashboardCount.lastApiCalled != 0) {
                    if (MixOperations.isNewEvent(dashboardCount.lastInsertedTeamTime, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", dashboardCount.lastApiCalled)) {
                        apiCall = true;
                    }
                }
                if (dashboardCount.oldCount != dashboardCount.count) {
                    dashboardCount.oldCount = dashboardCount.count;
                    dashboardCount.save();
                    apiCall = true;
                }

                if (apiCall) {
                    apiCall(false);
                }
            }
        } else {
            apiCall(true);
        }
    }

    private void apiCall(boolean isLoading) {
        if(isLoading)
            progressBar.setVisibility(View.VISIBLE);
        LeafManager leafManager = new LeafManager();
        if ("teacher".equalsIgnoreCase(role)) {
            leafManager.getTeacherClasses(this, GroupDashboardActivityNew.groupId);
        }else if ("parent".equalsIgnoreCase(role)) {
            leafManager.getParentKidsNew(this, GroupDashboardActivityNew.groupId);
        } else {
            leafManager.getClasses(this, GroupDashboardActivityNew.groupId);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);
        ClassResponse res = (ClassResponse) response;
        List<ClassResponse.ClassData> result = res.getData();
        AppLog.e(TAG, "ClassResponse " + new Gson().toJson(result));
        rvClass.setAdapter(new ClassesAdapter(result));

        TeamCountTBL dashboardCount = TeamCountTBL.getByTypeAndGroup("ALL", GroupDashboardActivityNew.groupId);
        if(dashboardCount!=null){
            dashboardCount.lastApiCalled = System.currentTimeMillis();
            dashboardCount.save();
        }

        saveToDB(result);
    }

    private void saveToDB(List<ClassResponse.ClassData> result) {
        if (result == null)
            return;

        ClassListTBL.deleteAll(GroupDashboardActivityNew.groupId);
        for (int i = 0; i < result.size(); i++) {
            ClassResponse.ClassData currentItem = result.get(i);
            ClassListTBL item = new ClassListTBL();
            item.teamId = currentItem.id;
            item.teacherName = currentItem.teacherName;
            item.phone = currentItem.phone;
            item.members = currentItem.members;
            item.countryCode = currentItem.countryCode;
            item.name = currentItem.className;
            item.image = currentItem.classImage;
            item.category = currentItem.category;
            item.jitsiToken = currentItem.jitsiToken;
            item.groupId = GroupDashboardActivityNew.groupId;
            item.save();
        }
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
        List<ClassResponse.ClassData> list;
        private Context mContext;

        public ClassesAdapter(List<ClassResponse.ClassData> list) {
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
            final ClassResponse.ClassData item = list.get(position);

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

    private void onTreeClick(ClassResponse.ClassData classData) {
        if(type.equals("Home Work") || type.equals("Recorded Class")){
            Intent intent = new Intent(getContext(), HWClassSubjectActivity.class);
            intent.putExtra("team_id", classData.getId());
            intent.putExtra("title", classData.className);
            intent.putExtra("type", type);
            startActivity(intent);
        }else if("Time Table".equalsIgnoreCase(type)){
            Intent intent = new Intent(getActivity(), TimeTabelActivity2.class);
            intent.putExtra("team_id",classData.getId());
            intent.putExtra("team_name",classData.getName());
            intent.putExtra("role",role);
            startActivity(intent);
        }else if("Marks Card".equalsIgnoreCase(type)){
            Intent intent = new Intent(getActivity(), MarksCardActivity2.class);
            intent.putExtra("team_id",classData.getId());
            intent.putExtra("userId",classData.userId);
            intent.putExtra("team_name",classData.getName());
            intent.putExtra("role",role);
            startActivity(intent);
        }

    }
}
