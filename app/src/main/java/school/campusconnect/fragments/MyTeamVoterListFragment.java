package school.campusconnect.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.VoterProfileActivity;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.booths.BoothVotersListResponse;
import school.campusconnect.datamodel.booths.BoothsTBL;
import school.campusconnect.datamodel.booths.MyTeamVotersTBL;
import school.campusconnect.datamodel.masterList.VoterListTBL;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;


public class MyTeamVoterListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {

    public static String TAG = "MyTeamVoterListFragment";

    private List<BoothVotersListResponse.VoterData> filteredList = new ArrayList<>();
    private List<BoothVotersListResponse.VoterData> myTeamDataList = new ArrayList<>();

    private int REQUEST_UPDATE_PROFILE = 9;
    ClassesAdapter adapter;

    private String boothID;
    private String isTeamAdmin;

    @Bind(R.id.rvTeams)
    public RecyclerView rvTeams;

    @Bind(R.id.edtSearch)
    public EditText edtSearch;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_team_discuss,container,false);
        ButterKnife.bind(this,view);
        Log.e(TAG,"onViewCreated");
        inits();
        getLocally();
        return view;
    }

    private void getLocally() {

        List<MyTeamVotersTBL> voterListTBLList = MyTeamVotersTBL.getBoothList(GroupDashboardActivityNew.groupId,boothID);

        myTeamDataList.clear();

        if (voterListTBLList != null && voterListTBLList.size() > 0)
        {
            ArrayList<BoothVotersListResponse.VoterData> resultData = new ArrayList<>();

            for (int i=0;i<voterListTBLList.size();i++)
            {
                MyTeamVotersTBL votersTBL = voterListTBLList.get(i);

                BoothVotersListResponse.VoterData voterData = new BoothVotersListResponse.VoterData();

                voterData.userId = votersTBL.userId;
                voterData.voterId = votersTBL.voterId;
                voterData.roleOnConstituency = votersTBL.roleOnConstituency;
                voterData.phone = votersTBL.phone;
                voterData.name = votersTBL.name;
                voterData.image = votersTBL.image;
                voterData.gender = votersTBL.gender;
                voterData.dob = votersTBL.dob;
                voterData.bloodGroup = votersTBL.bloodGroup;
                voterData.allowedToAddUser = votersTBL.allowedToAddUser;
                voterData.allowedToAddTeamPost = votersTBL.allowedToAddTeamPost;
                voterData.allowedToAddTeamPostComment = votersTBL.allowedToAddTeamPostComment;
            //    voterData.address = votersTBL.address;
                voterData.aadharNumber = votersTBL.aadharNumber;
                resultData.add(voterData);

            }
            myTeamDataList.addAll(resultData);
            adapter.add(myTeamDataList);
        }
        else
        {
            voterListApiCall();
        }
    }


    public static MyTeamVoterListFragment newInstance(String boothID , String isTeamAdmin) {
        MyTeamVoterListFragment fragment = new MyTeamVoterListFragment();
        Bundle b = new Bundle();
        b.putString("boothID", boothID);
        b.putString("isTeamAdmin", isTeamAdmin);
        fragment.setArguments(b);
        return fragment;
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

    private void voterListApiCall() {

        if (!isConnectionAvailable()) {
            return;
        }
        LeafManager leafManager = new LeafManager();
        showLoadingBar(progressBar);
        // progressBar.setVisibility(View.VISIBLE);
        leafManager.getBoothVoterList(this, GroupDashboardActivityNew.groupId,boothID);
    }


    private void inits() {

        boothID = getArguments().getString("boothID");
        isTeamAdmin = getArguments().getString("isTeamAdmin");

        rvTeams.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ClassesAdapter();
        rvTeams.setAdapter(adapter);

        edtSearch.setHint(getResources().getString(R.string.hint_search_voter));

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

        for(BoothVotersListResponse.VoterData item : myTeamDataList){

            if (item.name.toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }

        adapter.add(filteredList);

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        hideLoadingBar();
    //    progressBar.setVisibility(View.GONE);

        if (apiId == LeafManager.API_BOOTH_VOTER_LIST)
        {
            BoothVotersListResponse res = (BoothVotersListResponse) response;
            saveToLocally(res.getData());
        }


    }

    private void saveToLocally(ArrayList<BoothVotersListResponse.VoterData> data) {

        MyTeamVotersTBL.deleteBooth(GroupDashboardActivityNew.groupId,boothID);

        for (int i=0;i<data.size();i++)
        {
            BoothVotersListResponse.VoterData voterData= data.get(i);

            MyTeamVotersTBL voterListTBL = new MyTeamVotersTBL();

            voterListTBL.voterId = voterData.voterId;
            voterListTBL.boothId = boothID;
            voterListTBL.userId = voterData.userId;
            voterListTBL.roleOnConstituency = voterData.roleOnConstituency;
            voterListTBL.phone = voterData.phone;
            voterListTBL.name = voterData.name;
            voterListTBL.image = voterData.image;
            voterListTBL.gender = voterData.gender;
            voterListTBL.dob = voterData.dob;

            voterListTBL.bloodGroup = voterData.bloodGroup;
            voterListTBL.allowedToAddUser = voterData.allowedToAddUser;
            voterListTBL.allowedToAddTeamPostComment = voterData.allowedToAddTeamPostComment;

            voterListTBL.allowedToAddTeamPost = voterData.allowedToAddTeamPost;
            voterListTBL.groupId = GroupDashboardActivityNew.groupId;
            voterListTBL.address = "null";

            voterListTBL.aadharNumber = voterData.aadharNumber;
            voterListTBL.now = System.currentTimeMillis();

            voterListTBL.save();
        }

        myTeamDataList.addAll(data);
        adapter.add(myTeamDataList);

    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        //    progressBar.setVisibility(View.GONE);

        Log.e(TAG,"onFailure"+msg);

    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        //    progressBar.setVisibility(View.GONE);

        Log.e(TAG,"onException"+msg);
    }



    public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder>
    {
        List<BoothVotersListResponse.VoterData> list;
        private Context mContext;

        @Override
        public ClassesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class_student,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final BoothVotersListResponse.VoterData item = list.get(position);

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

            holder.img_tree.setVisibility(View.GONE);

            holder.txt_name.setText(item.name);

            holder.txt_count.setVisibility(View.GONE);

          /*  holder.txt_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTreeClick(item);
                }
            });*/
            holder.img_lead_default.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (GroupDashboardActivityNew.isAdmin || isTeamAdmin.equalsIgnoreCase("true"))
                    {
                        Intent i = new Intent(getActivity(), VoterProfileActivity.class);
                        i.putExtra("userID",item.userId);
                        i.putExtra("name",item.name);
                        startActivityForResult(i,REQUEST_UPDATE_PROFILE);
                    }

                }
            });


            holder.imgTeam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (GroupDashboardActivityNew.isAdmin || isTeamAdmin.equalsIgnoreCase("true"))
                    {
                        Intent i = new Intent(getActivity(), VoterProfileActivity.class);
                        i.putExtra("userID",item.userId);
                        i.putExtra("name",item.name);
                        startActivityForResult(i,REQUEST_UPDATE_PROFILE);
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

        public void add(List<BoothVotersListResponse.VoterData> list) {
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

    private void onTreeClick(BoothVotersListResponse.VoterData item) {

        if (GroupDashboardActivityNew.isAdmin || isTeamAdmin.equalsIgnoreCase("true"))
        {
            Intent i = new Intent(getActivity(), VoterProfileActivity.class);
            i.putExtra("userID",item.userId);
            i.putExtra("name",item.name);
            startActivityForResult(i,REQUEST_UPDATE_PROFILE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_UPDATE_PROFILE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                MyTeamVotersTBL.deleteBooth(GroupDashboardActivityNew.groupId,boothID);
                myTeamDataList.clear();
                adapter.add(myTeamDataList);
                getLocally();
            }
        }
    }
}