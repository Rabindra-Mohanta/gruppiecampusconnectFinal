package school.campusconnect.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.AddBoothActivity;
import school.campusconnect.activities.BoothStudentActivity;
import school.campusconnect.activities.ClassStudentActivity;
import school.campusconnect.activities.CommitteeActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.adapters.TeamListAdapterNew;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.booths.BoothResponse;
import school.campusconnect.datamodel.booths.BoothsTBL;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.feed.AdminFeedTable;
import school.campusconnect.datamodel.feed.AdminFeederResponse;
import school.campusconnect.datamodel.masterList.BoothMasterListModelResponse;
import school.campusconnect.datamodel.masterList.MasterBoothListTBL;
import school.campusconnect.datamodel.staff.StaffResponse;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.datamodel.ticket.TicketListResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class BoothListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "BoothListFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;
    @Bind(R.id.etSearch)
    public EditText etSearch;
    private ArrayList<MyTeamData> result = new ArrayList<>();

    private ClassesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss_search,container,false);
        ButterKnife.bind(this,view);

        init();

        getDataLocally();

        return view;
    }

    private void getDataLocally() {

        List<BoothsTBL> boothListTBl = BoothsTBL.getBoothList(GroupDashboardActivityNew.groupId);

        result.clear();

        if (boothListTBl != null && boothListTBl.size() > 0)
        {
            ArrayList<MyTeamData> resultData = new ArrayList<>();

            for (int i=0;i<boothListTBl.size();i++)
            {
                BoothsTBL boothList = boothListTBl.get(i);

                MyTeamData myTeamData = new MyTeamData();
                myTeamData.teamId = boothList.teamId;
                myTeamData.postUnseenCount = boothList.postUnseenCount;
                myTeamData.phone = boothList.phone;
                myTeamData.name = boothList.name;
                myTeamData.boothId = boothList.boothId;
                myTeamData.members = boothList.members;
                myTeamData.boothNumber = boothList.boothNumber;
                myTeamData.groupId = boothList.groupId;
                myTeamData.canAddUser = boothList.canAddUser;

                myTeamData.allowTeamPostCommentAll = boothList.allowTeamPostCommentAll;
                myTeamData.allowTeamPostAll = boothList.allowTeamPostAll;
                myTeamData.isTeamAdmin = boothList.isTeamAdmin;
                myTeamData.isClass = boothList.isClass;
                myTeamData.teamType = boothList.teamType;
                myTeamData.enableGps = boothList.enableGps;
                myTeamData.enableAttendance = boothList.enableAttendance;
                myTeamData.type = boothList.type;
                myTeamData.userId = boothList.userId;

                myTeamData.userName = boothList.userName;
                myTeamData.adminName = boothList.adminName;
                myTeamData.userImage = boothList.userImage;

                myTeamData.category = boothList.category;
                myTeamData.role = boothList.role;
                myTeamData.count = boothList.count;
                myTeamData.allowedToAddTeamPost = boothList.allowedToAddTeamPost;
                myTeamData.leaveRequest = boothList.leaveRequest;
                myTeamData.details = new Gson().fromJson(boothList.TeamDetails, new TypeToken<MyTeamData.TeamDetails>() {}.getType());

                resultData.add(myTeamData);

            }
            result.addAll(resultData);
            adapter.add(result);
        }
        else
        {
            boothListApiCall();
        }

    }



    private void init() {

        adapter = new ClassesAdapter();
        rvClass.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rvClass.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(result!=null){
                    if(!TextUtils.isEmpty(s.toString())){
                        ArrayList<MyTeamData> newList = new ArrayList<>();
                        for (int i=0;i<result.size();i++){
                            if(result.get(i).name.toLowerCase().contains(s.toString().toLowerCase())){
                                newList.add(result.get(i));
                            }
                        }
                        adapter.add(newList);
                    }else {
                        adapter.add(result);
                    }
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.menu_add_booth).setVisible(true);
        menu.findItem(R.id.menu_search).setVisible(true);
        menu.findItem(R.id.action_notification_list).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add_booth:
                startActivity(new Intent(getContext(), AddBoothActivity.class));
                return true;

            case R.id.menu_search:
                showHideSearch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            ((GroupDashboardActivityNew) getActivity()).tvToolbar.setText(GroupDashboardActivityNew.group_name);
            ((GroupDashboardActivityNew) getActivity()).tv_Desc.setVisibility(View.GONE);
        }
        etSearch.setText("");

    }


    private void boothListApiCall() {

        if (!isConnectionAvailable()) {
            return;
        }
        LeafManager leafManager = new LeafManager();
        progressBar.setVisibility(View.VISIBLE);
        leafManager.getBooths(this,GroupDashboardActivityNew.groupId,"");
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);
        BoothResponse res = (BoothResponse) response;
        //result = res.getData();
        AppLog.e(TAG, "ClassResponse " + result);
        saveToLocally(res.getData());
       // rvClass.setAdapter(new ClassesAdapter(result));
    }

    private void saveToLocally(ArrayList<MyTeamData> boothList) {

        BoothsTBL.deleteBooth(GroupDashboardActivityNew.groupId);

        for (int i = 0;i<boothList.size();i++)
        {
            BoothsTBL boothsTBL = new BoothsTBL();

            boothsTBL.teamId = boothList.get(i).teamId;
            boothsTBL.postUnseenCount = boothList.get(i).postUnseenCount;
            boothsTBL.phone = boothList.get(i).phone;
            boothsTBL.name = boothList.get(i).name;
            boothsTBL.members = boothList.get(i).members;
            boothsTBL.boothNumber = boothList.get(i).boothNumber;
            boothsTBL.groupId = boothList.get(i).groupId;
            boothsTBL.canAddUser = boothList.get(i).canAddUser;

            boothsTBL.allowTeamPostCommentAll = boothList.get(i).allowTeamPostCommentAll;
            boothsTBL.allowTeamPostAll = boothList.get(i).allowTeamPostAll;
            boothsTBL.isTeamAdmin = boothList.get(i).isTeamAdmin;
            boothsTBL.isClass = boothList.get(i).isClass;
            boothsTBL.teamType = boothList.get(i).teamType;
            boothsTBL.enableGps = boothList.get(i).enableGps;
            boothsTBL.enableAttendance = boothList.get(i).enableAttendance;
            boothsTBL.type = boothList.get(i).type;
            boothsTBL.userId = boothList.get(i).userId;
            boothsTBL.userName = boothList.get(i).userName;
            boothsTBL.adminName = boothList.get(i).adminName;
            boothsTBL.userImage = boothList.get(i).userImage;
            boothsTBL.boothId = boothList.get(i).boothId;

            boothsTBL.category = boothList.get(i).category;
            boothsTBL.role = boothList.get(i).role;
            boothsTBL.count = boothList.get(i).count;
            boothsTBL.allowedToAddTeamPost = boothList.get(i).allowedToAddTeamPost;
            boothsTBL.leaveRequest = boothList.get(i).leaveRequest;
            boothsTBL.TeamDetails =new Gson().toJson(boothList.get(i).details);
            boothsTBL._now = System.currentTimeMillis();
            boothsTBL.save();
        }

        result.addAll(boothList);
        adapter.add(result);

    }

    @Override
    public void onFailure(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }



    public void showHideSearch() {
        if (etSearch.getVisibility() == View.VISIBLE) {
            etSearch.setVisibility(View.GONE);
        } else {
            etSearch.setVisibility(View.VISIBLE);
        }
    }


    public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder>
    {
        List<MyTeamData> list;
        private Context mContext;


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_list,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            final MyTeamData team = list.get(position);

            holder.tvTeamName.setText(team.name);
            holder.tvPostCount.setText("");
            holder.tvPostCount.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(team.image)) {
                holder.imgTeam.setVisibility(View.VISIBLE);
                holder.imgDefault.setVisibility(View.GONE);

                Picasso.with(mContext).load(Constants.decodeUrlToBase64(team.image)).networkPolicy(NetworkPolicy.OFFLINE)/*.resize(150,150)*/.into(holder.imgTeam, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(mContext).load(Constants.decodeUrlToBase64(team.image))/*.resize(150,150)*/.into(holder.imgTeam, new Callback() {
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
        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    txtEmpty.setText("No Booths found.");
                }
                else {
                    txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                txtEmpty.setText("No Booths found.");
                return 0;
            }

        }

        public void add(List<MyTeamData> list) {
            this.list = list;
            notifyDataSetChanged();
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
                        onTreeClick(list.get(getAdapterPosition()));
                    }

                    //}
                });

            }
        }
    }

    private void onTreeClick(MyTeamData classData) {



        ((GroupDashboardActivityNew) getActivity()).onTeamSelected(classData);
//
//        Intent intent = new Intent(getActivity(), BoothStudentActivity.class);
//        intent.putExtra("class_data",new Gson().toJson(classData));
//        intent.putExtra("title",classData.boothName);
//        startActivity(intent);
    }
}
