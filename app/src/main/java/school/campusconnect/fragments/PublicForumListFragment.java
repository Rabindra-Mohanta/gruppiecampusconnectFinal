package school.campusconnect.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.AddBoothActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.VoterProfileActivity;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.GroupItem;
import school.campusconnect.datamodel.booths.BoothResponse;
import school.campusconnect.datamodel.booths.BoothsTBL;
import school.campusconnect.datamodel.booths.EventSubBoothTBL;
import school.campusconnect.datamodel.booths.MemberTeamTBL;
import school.campusconnect.datamodel.booths.MyBoothEventRes;
import school.campusconnect.datamodel.booths.MyBoothEventTBL;
import school.campusconnect.datamodel.booths.PublicFormBoothTBL;
import school.campusconnect.datamodel.booths.SubBoothEventRes;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.DateTimeHelper;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.MixOperations;

public class PublicForumListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "PublicForumListFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.edtSearch)
    public EditText edtSearch;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    private int REQUEST_UPDATE_PROFILE = 9;

    ClassesAdapter adapter;

    private GroupItem mGroupItem;

    private List<MyTeamData> filteredList = new ArrayList<>();
    private List<MyTeamData> myTeamDataList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss,container,false);
        ButterKnife.bind(this,view);

        _init();

        checkBoothType();

        return view;
    }

    private void checkBoothType() {

        if(mGroupItem.canPost){
            getDataBoothLocally();
        }else {
            getDataMyBoothLocally();
            callEventApi();
        }

    }

    private void callEventApi() {

        LeafManager leafManager = new LeafManager();
        leafManager.getMyBoothEvent(new LeafManager.OnCommunicationListener() {
            @Override
            public void onSuccess(int apiId, BaseResponse response) {

                MyBoothEventRes res1 = (MyBoothEventRes) response;
                new EventAsync(res1).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onFailure(int apiId, String msg) {

            }

            @Override
            public void onException(int apiId, String msg) {

            }
        },GroupDashboardActivityNew.groupId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG,"onDestroyView");
    }

    private void getDataBoothLocally() {

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
            myTeamDataList.addAll(resultData);
            adapter.add(myTeamDataList);
        }
        else
        {
            boothListApiCall();
        }

    }

    private void getDataMyBoothLocally() {

        List<PublicFormBoothTBL> boothListTBl =  PublicFormBoothTBL.getBoothList(GroupDashboardActivityNew.groupId);

        myTeamDataList.clear();

        if (boothListTBl != null && boothListTBl.size() > 0)
        {
            ArrayList<MyTeamData> resultData = new ArrayList<>();

            for (int i=0;i<boothListTBl.size();i++)
            {
                PublicFormBoothTBL boothList = boothListTBl.get(i);

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

    private void _init() {

        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ClassesAdapter();
        rvClass.setAdapter(adapter);

        mGroupItem = new Gson().fromJson(LeafPreference.getInstance(getContext()).getString(Constants.GROUP_DATA), GroupItem.class);

        edtSearch.setHint(getResources().getString(R.string.hint_search_booth));

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

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            ((GroupDashboardActivityNew) getActivity()).tvToolbar.setText(GroupDashboardActivityNew.group_name);
            ((GroupDashboardActivityNew) getActivity()).tv_Desc.setVisibility(View.GONE);
        }
    }

    private void boothListApiCall() {

        if (!isConnectionAvailable()) {
            return;
        }
     //   progressBar.setVisibility(View.VISIBLE);
        showLoadingBar(progressBar);
        LeafManager leafManager = new LeafManager();

        if(mGroupItem.canPost){
            leafManager.getBooths(this,GroupDashboardActivityNew.groupId,"");
        }else {
            leafManager.getMyBooths(this,GroupDashboardActivityNew.groupId);
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
     //   progressBar.setVisibility(View.GONE);

        hideLoadingBar();
        BoothResponse res = (BoothResponse) response;
        List<MyTeamData> result = res.getData();
        AppLog.e(TAG, "ClassResponse " + result);


        if(mGroupItem.canPost){
            saveToBoothLocally(res.getData());
        }else {
            saveToMyBoothLocally(res.getData());
        }



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_UPDATE_PROFILE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
            /*    Intent i = new Intent(getContext(),GroupDashboardActivityNew.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);*/

                if(mGroupItem.canPost){
                    BoothsTBL.deleteBooth(GroupDashboardActivityNew.groupId);
                    myTeamDataList.clear();
                    adapter.add(myTeamDataList);
                    getDataBoothLocally();
                }else {
                    PublicFormBoothTBL.deleteBooth(GroupDashboardActivityNew.groupId);
                    myTeamDataList.clear();
                    adapter.add(myTeamDataList);
                    getDataMyBoothLocally();
                }
                
            }
        }
    }

    private void saveToBoothLocally(ArrayList<MyTeamData> boothList) {

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

            if (!LeafPreference.getInstance(getContext()).getString("BOOTH_INSERT").isEmpty())
            {
                boothsTBL._now = LeafPreference.getInstance(getContext()).getString("BOOTH_INSERT");
            }
            else
            {
                boothsTBL._now = DateTimeHelper.getCurrentTime();
            }

            boothsTBL.save();
        }

        myTeamDataList.addAll(boothList);
        adapter.add(myTeamDataList);

    }


    private void saveToMyBoothLocally(ArrayList<MyTeamData> boothList) {

        PublicFormBoothTBL.deleteBooth(GroupDashboardActivityNew.groupId);

        for (int i = 0;i<boothList.size();i++)
        {
            PublicFormBoothTBL boothsTBL = new PublicFormBoothTBL();

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

            boothsTBL.category = boothList.get(i).category;
            boothsTBL.role = boothList.get(i).role;
            boothsTBL.count = boothList.get(i).count;
            boothsTBL.allowedToAddTeamPost = boothList.get(i).allowedToAddTeamPost;
            boothsTBL.leaveRequest = boothList.get(i).leaveRequest;
            boothsTBL.TeamDetails =new Gson().toJson(boothList.get(i).details);

            if (!LeafPreference.getInstance(getContext()).getString("MY_BOOTH_EVENT_UPDATE").isEmpty())
            {
                boothsTBL._now =  LeafPreference.getInstance(getContext()).getString("MY_BOOTH_EVENT_UPDATE");
            }
            else
            {
                boothsTBL._now = DateTimeHelper.getCurrentTime();
            }

            boothsTBL.save();
        }

        myTeamDataList.addAll(boothList);
        adapter.add(myTeamDataList);

    }

    @Override
    public void onFailure(int apiId, String msg) {
     //   progressBar.setVisibility(View.GONE);
        hideLoadingBar();
    }

    @Override
    public void onException(int apiId, String msg) {
      //  progressBar.setVisibility(View.GONE);
        hideLoadingBar();
    }

    public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder>
    {
        List<MyTeamData> list;
        private Context mContext;

        /*public ClassesAdapter(List<MyTeamData> list) {
            this.list = list;
        }*/

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
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


            holder.txt_name.setText(item.name);
            holder.txt_count.setText("");
            holder.txt_count.setVisibility(View.GONE);

            holder.img_lead_default.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (GroupDashboardActivityNew.isAdmin)
                    {
                        Intent i = new Intent(getActivity(), VoterProfileActivity.class);
                        i.putExtra("userID",item.userId);
                        i.putExtra("name",item.name);
                        i.putExtra("teamID",item.teamId);
                        startActivityForResult(i,REQUEST_UPDATE_PROFILE);
                    }

                }
            });
        }

        public void add(List<MyTeamData> list) {
            this.list = list;
            notifyDataSetChanged();
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

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.img_lead)
            ImageView imgTeam;

            @Bind(R.id.img_lead_default)
            ImageView img_lead_default;

            @Bind(R.id.txt_name)
            TextView txt_name;

            @Bind(R.id.txt_count)
            TextView txt_count;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
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

        ((GroupDashboardActivityNew) getActivity()).onBoothTeams(classData.name,classData.teamId,"normal",true);
    }


    class EventAsync extends AsyncTask<Void, Void, Void> {
        MyBoothEventRes res1;
        private boolean needRefresh = false;

        public EventAsync(MyBoothEventRes res1) {
            this.res1 =res1;
        }

        @Override
        protected Void doInBackground(Void... voids) {


            Log.e(TAG,"lastUpdatedSubBoothTeamTime  "+res1.data.get(0).lastUpdatedMyBoothTeamTime);

            LeafPreference.getInstance(getContext()).setString("MY_BOOTH_EVENT_UPDATE",res1.data.get(0).lastUpdatedMyBoothTeamTime);

            if (PublicFormBoothTBL.getLastBooth(GroupDashboardActivityNew.groupId).size() > 0)
            {
                if (MixOperations.isNewEventUpdate(LeafPreference.getInstance(getContext()).getString("MY_BOOTH_EVENT_UPDATE"), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", PublicFormBoothTBL.getLastBooth(GroupDashboardActivityNew.groupId).get(0)._now)) {
                    PublicFormBoothTBL.deleteAll();
                    needRefresh = true;
                }
                else
                {
                    needRefresh = false;
                }
            }

            if (res1.data.get(0).myBoothTeamsLastPostEventAt.size() > 0)
            {
                MyBoothEventTBL.deleteAll();

                for (int i=0;i<res1.data.get(0).myBoothTeamsLastPostEventAt.size();i++)
                {
                    MyBoothEventTBL myBoothEventTBL = new MyBoothEventTBL();

                    myBoothEventTBL.teamId = res1.data.get(0).myBoothTeamsLastPostEventAt.get(i).teamId;
                    myBoothEventTBL.members = res1.data.get(0).myBoothTeamsLastPostEventAt.get(i).members;
                    myBoothEventTBL.lastTeamPostAt = res1.data.get(0).myBoothTeamsLastPostEventAt.get(i).lastTeamPostAt;

                    myBoothEventTBL.save();
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (needRefresh)
            {
                adapter.notifyDataSetChanged();
                checkBoothType();
            }
        }
    }
}
