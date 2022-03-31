package school.campusconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
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
import school.campusconnect.activities.BoothCoordinateActivity;
import school.campusconnect.activities.CommitteeActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;

import school.campusconnect.activities.VoterProfileActivity;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.booths.BoothResponse;
import school.campusconnect.datamodel.booths.BoothsTBL;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.fragments.DashboardNewUi.BaseTeamFragmentv3;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class BoothListMyTeamFragment extends BaseFragment implements LeafManager.OnCommunicationListener{

    public static String TAG = "BoothListMyTeamFragment";

    private List<MyTeamData> filteredList = new ArrayList<>();
    private List<MyTeamData> myTeamDataList = new ArrayList<>();

    ClassesAdapter adapter;

    @Bind(R.id.rvTeams)
    public RecyclerView rvTeams;

    @Bind(R.id.edtSearch)
    public EditText edtSearch;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss,container,false);
        ButterKnife.bind(this,view);
        Log.e(TAG,"onViewCreated");
        inits();

        getDataLocally();

        return view;
    }


    private void getDataLocally() {

        List<BoothsTBL> boothListTBl = BoothsTBL.getBoothList(GroupDashboardActivityNew.groupId);

        myTeamDataList.clear();

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

                myTeamData.userName = boothList.userName;
                myTeamData.adminName = boothList.adminName;
                myTeamData.userImage = boothList.userImage;
                myTeamData.boothId = boothList.boothId;
                myTeamData.userId = boothList.userId;
                myTeamData.category = boothList.category;
                myTeamData.role = boothList.role;
                myTeamData.count = boothList.count;
                myTeamData.allowedToAddTeamPost = boothList.allowedToAddTeamPost;
                myTeamData.leaveRequest = boothList.leaveRequest;
                myTeamData.details = new Gson().fromJson(boothList.TeamDetails, new TypeToken<MyTeamData.TeamDetails>() {}.getType());

                resultData.add(myTeamData);

            }
            myTeamDataList.addAll(resultData);
            adapter.add(myTeamDataList);
        }
        else
        {
            boothListApiCall();
        }

    }

    private void inits() {




        rvTeams.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ClassesAdapter();
        rvTeams.setAdapter(adapter);

        edtSearch.setHint("Search Booths");

        edtSearch.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                if (s.toString().length() > 2)
                {
                    searchData(s.toString());
                }
                else
                {
                    adapter.add(myTeamDataList);
                }
            }
        });
    }
    private void searchData(String text) {

        filteredList = new ArrayList<>();

        for(MyTeamData item : myTeamDataList){

            if (item.name.toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }

        adapter.add(filteredList);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.action_notification_list).setVisible(false);
        menu.findItem(R.id.menu_search).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){


            case R.id.menu_search:
                showHideSearch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showHideSearch() {

        if (edtSearch.getVisibility() == View.VISIBLE) {
            edtSearch.setVisibility(View.GONE);
        } else {
            edtSearch.setVisibility(View.VISIBLE);
        }
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
            boothsTBL.userId =  boothList.get(i).userId;
            boothsTBL._now = System.currentTimeMillis();
            boothsTBL.save();
        }

        myTeamDataList.addAll(boothList);
        adapter.add(myTeamDataList);

    }

    @Override
    public void onFailure(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }


    public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder>
    {
        List<MyTeamData> list;
        private Context mContext;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class_student,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final MyTeamData item = list.get(position);

            if (!TextUtils.isEmpty(item.image)) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.image)).resize(50,50).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgTeam,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.img_lead_default.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.image)).resize(50,50).into(holder.imgTeam, new Callback() {
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

            holder.txt_name.setText(item.adminName+"( "+item.name+" )");
            holder.txt_count.setText("Member : "+String.valueOf(item.members));


            holder.txt_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), VoterProfileActivity.class);
                    i.putExtra("userID",item.userId);
                    i.putExtra("name",item.name);
                    startActivity(i);
                }
            });
            holder.img_lead_default.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), VoterProfileActivity.class);
                    i.putExtra("userID",item.userId);
                    i.putExtra("name",item.name);
                    startActivity(i);
                }
            });
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



                img_tree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onTreeClick(list.get(getAdapterPosition()));
                    }
                });

            }
        }
    }

    private void onTreeClick(MyTeamData classData) {

        ((GroupDashboardActivityNew) getActivity()).onBoothTeams(classData.name,classData.boothId,"myTeam",true);
    }


}
