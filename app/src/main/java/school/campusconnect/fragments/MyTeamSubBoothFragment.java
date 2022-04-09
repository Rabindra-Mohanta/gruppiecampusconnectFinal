package school.campusconnect.fragments;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.VoterProfileActivity;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.booths.BoothVotersListResponse;
import school.campusconnect.datamodel.booths.MyBoothEventRes;
import school.campusconnect.datamodel.booths.MyBoothEventTBL;
import school.campusconnect.datamodel.booths.MyTeamSubBoothResponse;
import school.campusconnect.datamodel.booths.MyTeamSubBoothTBL;
import school.campusconnect.datamodel.booths.MyTeamVotersTBL;
import school.campusconnect.datamodel.booths.PublicFormBoothTBL;
import school.campusconnect.datamodel.booths.SubBoothWorkerEventRes;
import school.campusconnect.datamodel.booths.SubBoothWorkerEventTBL;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.DateTimeHelper;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.MixOperations;

public class MyTeamSubBoothFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
public static String TAG = "MyTeamSubBoothFragment";

    private List<MyTeamSubBoothResponse.TeamData> filteredList = new ArrayList<>();
    private List<MyTeamSubBoothResponse.TeamData> myTeamDataList = new ArrayList<>();

    ClassesAdapter adapter;

    @Bind(R.id.rvTeams)
    public RecyclerView rvTeams;

    @Bind(R.id.edtSearch)
    public EditText edtSearch;

    private int REQUEST_UPDATE_PROFILE = 9;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss,container,false);
        ButterKnife.bind(this,view);
        Log.e(TAG,"onViewCreated");
        inits();
        getLocally();
        callEvent();
        return view;
    }

    private void callEvent() {

        LeafManager leafManager = new LeafManager();
        leafManager.getSubBoothWorkerEvent(new LeafManager.OnCommunicationListener() {
            @Override
            public void onSuccess(int apiId, BaseResponse response) {
                SubBoothWorkerEventRes res = (SubBoothWorkerEventRes) response;

                new EventAsync(res).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onFailure(int apiId, String msg) {

            }

            @Override
            public void onException(int apiId, String msg) {

            }
        },GroupDashboardActivityNew.groupId);
    }

    private void getLocally() {

        List<MyTeamSubBoothTBL> subBoothTBLList = MyTeamSubBoothTBL.getSubBoothList(GroupDashboardActivityNew.groupId);

        myTeamDataList.clear();

        if (subBoothTBLList != null && subBoothTBLList.size()>0)
        {
            ArrayList<MyTeamSubBoothResponse.TeamData> resultData = new ArrayList<>();

            for (int i=0;i<subBoothTBLList.size();i++)
            {
                MyTeamSubBoothTBL teamSubBoothTBL = subBoothTBLList.get(i);

                MyTeamSubBoothResponse.TeamData teamData = new MyTeamSubBoothResponse.TeamData();
                teamData.teamId = teamSubBoothTBL.teamID;
                teamData.userId = teamSubBoothTBL.userId;
                teamData.name = teamSubBoothTBL.name;
                teamData.subBoothId = teamSubBoothTBL.subBoothId;
                teamData.members = teamSubBoothTBL.members;
                teamData.isTeamAdmin = teamSubBoothTBL.isTeamAdmin;
                teamData.image = teamSubBoothTBL.image;
                teamData.groupId = teamSubBoothTBL.groupId;
                teamData.category = teamSubBoothTBL.category;
                teamData.canAddUser = teamSubBoothTBL.canAddUser;
                teamData.allowTeamPostCommentAll = teamSubBoothTBL.allowTeamPostCommentAll;
                teamData.allowTeamPostAll = teamSubBoothTBL.allowTeamPostAll;

                resultData.add(teamData);

            }
            myTeamDataList.addAll(resultData);
            adapter.add(myTeamDataList);
        }
        else
        {
            subBoothListApiCall();
        }
    }

    private void subBoothListApiCall() {

        if (!isConnectionAvailable()) {
            return;
        }
        LeafManager leafManager = new LeafManager();
        progressBar.setVisibility(View.VISIBLE);
        leafManager.getMyTeamSubBooth(this,GroupDashboardActivityNew.groupId);
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

    private void inits() {



        rvTeams.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ClassesAdapter();
        rvTeams.setAdapter(adapter);

        edtSearch.setHint("Search Sub Booth");

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

        for(MyTeamSubBoothResponse.TeamData item : myTeamDataList){

            if (item.name.toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }

        adapter.add(filteredList);

    }

    private void saveToLocally(ArrayList<MyTeamSubBoothResponse.TeamData> data) {

        MyTeamSubBoothTBL.deleteSubBooth(GroupDashboardActivityNew.groupId);

        for (int i=0;i<data.size();i++)
        {
            MyTeamSubBoothResponse.TeamData teamData = data.get(i);

            MyTeamSubBoothTBL teamSubBoothTBL = new MyTeamSubBoothTBL();

            teamSubBoothTBL.teamID =teamData.teamId;
            teamSubBoothTBL.subBoothId = teamData.subBoothId;
            teamSubBoothTBL.name = teamData.name;
            teamSubBoothTBL.userId = teamData.userId;
            teamSubBoothTBL.members = teamData.members;
            teamSubBoothTBL.isTeamAdmin = teamData.isTeamAdmin;
            teamSubBoothTBL.image = teamData.image;
            teamSubBoothTBL.groupId = teamData.groupId;
            teamSubBoothTBL.category = teamData.category;
            teamSubBoothTBL.canAddUser = teamData.canAddUser;
            teamSubBoothTBL.allowTeamPostCommentAll = teamData.allowTeamPostCommentAll;
            teamSubBoothTBL.allowTeamPostAll = teamData.allowTeamPostAll;

            if (LeafPreference.getInstance(getContext()).getString("SUB_BOOTH_WORKER_EVENT_UPDATE").isEmpty())
            {
                teamSubBoothTBL._now = LeafPreference.getInstance(getContext()).getString("SUB_BOOTH_WORKER_EVENT_UPDATE");
            }
            else
            {
                teamSubBoothTBL._now = DateTimeHelper.getCurrentTime();
            }

            teamSubBoothTBL.save();

        }

        myTeamDataList.addAll(data);
        adapter.add(myTeamDataList);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);
        MyTeamSubBoothResponse res = (MyTeamSubBoothResponse) response;

        saveToLocally(res.getData());

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
        List<MyTeamSubBoothResponse.TeamData> list;
        private Context mContext;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class_student,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final MyTeamSubBoothResponse.TeamData item = list.get(position);

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
            holder.txt_count.setText("Members: "+item.members);

            holder.txt_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTreeClick(item);
                }
            });
            holder.img_lead_default.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (GroupDashboardActivityNew.isAdmin)
                    {
                        Intent i = new Intent(getActivity(), VoterProfileActivity.class);
                        i.putExtra("userID",item.userId);
                        i.putExtra("name",item.name);
                        i.putExtra("teamID",item.teamId);
                        startActivity(i);
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    txtEmpty.setText("No Voters found.");
                }
                else {
                    txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                txtEmpty.setText("No Voters found.");
                return 0;
            }

        }

        public void add(List<MyTeamSubBoothResponse.TeamData> list) {
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

    private void onTreeClick(MyTeamSubBoothResponse.TeamData myTeamData) {

        ((GroupDashboardActivityNew) getActivity()).onTeamSelectedVoter(myTeamData.name,myTeamData.members,myTeamData.subBoothId);
    }

    class EventAsync extends AsyncTask<Void, Void, Void> {
        SubBoothWorkerEventRes res1;
        private boolean needRefresh = false;

        public EventAsync(SubBoothWorkerEventRes res1) {
            this.res1 =res1;
        }

        @Override
        protected Void doInBackground(Void... voids) {


            Log.e(TAG,"lastUpdatedMySubBoothTeamTime  "+res1.data.get(0).lastUpdatedMySubBoothTeamTime);

            LeafPreference.getInstance(getContext()).setString("SUB_BOOTH_WORKER_EVENT_UPDATE",res1.data.get(0).lastUpdatedMySubBoothTeamTime);

            if (MyTeamSubBoothTBL.getLastSubBooth().size() > 0)
            {
                if (MixOperations.isNewEventUpdate(LeafPreference.getInstance(getContext()).getString("SUB_BOOTH_WORKER_EVENT_UPDATE"), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", MyTeamSubBoothTBL.getLastSubBooth().get(0)._now)) {
                    MyTeamSubBoothTBL.deleteAll();
                    needRefresh = true;
                }
                else
                {
                    needRefresh = false;
                }
            }

            if (res1.data.get(0).mySubBoothTeamsLastPostEventAt.size() > 0)
            {
                SubBoothWorkerEventTBL.deleteAll();

                for (int i=0;i<res1.data.get(0).mySubBoothTeamsLastPostEventAt.size();i++)
                {
                    SubBoothWorkerEventTBL workerEventTBL = new SubBoothWorkerEventTBL();

                    workerEventTBL.teamId = res1.data.get(0).mySubBoothTeamsLastPostEventAt.get(i).teamId;
                    workerEventTBL.members = res1.data.get(0).mySubBoothTeamsLastPostEventAt.get(i).members;
                    workerEventTBL.lastTeamPostAt = res1.data.get(0).mySubBoothTeamsLastPostEventAt.get(i).lastTeamPostAt;

                    workerEventTBL.save();
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
                getLocally();
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_UPDATE_PROFILE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                Intent i = new Intent(getContext(), GroupDashboardActivityNew.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        }
    }
}
