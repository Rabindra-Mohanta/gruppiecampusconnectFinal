
package school.campusconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.activities.LeadsListActivity;
import school.campusconnect.activities.NestedTeamActivity;
import school.campusconnect.activities.UpdateMemberActivity;
import school.campusconnect.activities.VoterProfileActivity;
import school.campusconnect.datamodel.booths.BoothMemberResponse;
import school.campusconnect.utils.AppLog;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import school.campusconnect.R;
import school.campusconnect.activities.AddPostActivity;
import school.campusconnect.activities.LeadDetailActivity;
import school.campusconnect.adapters.LeadAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.LayoutListWithoutRefreshBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.LeadItem;
import school.campusconnect.datamodel.LeadResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class LeadListFragment extends BaseFragment implements LeadAdapter.OnLeadSelectListener,
        LeafManager.OnCommunicationListener {

    private static final String TAG = "LeadListFragment";
    private LayoutListWithoutRefreshBinding mBinding;
    private LeadAdapter mAdapter;
    final int REQUEST_CALL = 234;
    Intent intent;
    String boothClick;
    String groupId = "";
    String teamId = "";
    LeafManager mManager = new LeafManager();
    public boolean mIsLoading = false;
    public int totalPages = 1;
    public int currentPage = 1;
    int count;
    public boolean allMember;
    private boolean itemClick;
    private boolean isAdmin;
    private LinearLayoutManager layoutManager;
    private List<LeadItem> list;

    public LeadListFragment() {

    }

    public static LeadListFragment newInstance(Bundle b) {
        LeadListFragment fragment = new LeadListFragment();
        fragment.setArguments(b);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_list_without_refresh, container, false);
        mBinding.setSize(1);
        mBinding.setMessage(R.string.msg_no_leads);
        ActiveAndroid.initialize(getActivity());

        initObjects();

        getData();

        setListener();

        return mBinding.getRoot();
    }

    private void setListener() {
        mBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!mIsLoading && totalPages > currentPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        currentPage = currentPage + 1;
                        getData();
                    }
                }
            }

        });

    }

    private void initObjects() {
        DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
        count = databaseHandler.getCount();

        groupId = getArguments().getString("id");
        teamId = getArguments().getString("team_id");
        boothClick = getArguments().getString("boothClick");
        itemClick = getArguments().getBoolean("item_click", false);
        isAdmin = getArguments().getBoolean("isAdmin", false);
        allMember = getArguments().getBoolean("all",false);
        boolean isNest = getArguments().getBoolean("isNest", false);
        AppLog.e(TAG, "isAdmin is " + isAdmin);
        AppLog.e(TAG, "item_click is " + itemClick);


        if (boothClick != null && boothClick.equalsIgnoreCase("yes"))
        {

        }
        else
        {
            mBinding.recyclerView.setVisibility(View.VISIBLE);
            layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            mBinding.recyclerView.setLayoutManager(layoutManager);

            mAdapter = new LeadAdapter(new ArrayList<LeadItem>(), this, 0, databaseHandler, count, itemClick,isNest);

            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISUSERDELETED, false);
        }

    }


    @Override
    public void onResume() {
        super.onResume();

        if (boothClick != null && boothClick.equalsIgnoreCase("yes"))
        {

        }
        else
        {
            AppLog.e(TAG, "OnResume Called : " + LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISUSERDELETED));
            if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISUSERDELETED)) {
                LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISUSERDELETED, false);
                currentPage = 1;
                mAdapter.clear();
                getData();
            }

            if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ADD_FRIEND)) {
                LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ADD_FRIEND, false);
                currentPage = 1;
                mAdapter.clear();
                getData();
            }
        }


    }


    private void getData() {
        if (isConnectionAvailable()) {
            showLoadingBar(mBinding.progressBar);
            mIsLoading = true;

            if (boothClick != null && boothClick.equalsIgnoreCase("yes"))
            {
                mManager.getBoothMember(this, groupId, teamId ,getArguments().getString("committee_id"));
            }
            else
            {
                mManager.getTeamMember(this, groupId + "", teamId + "",itemClick);
            }

        } else {
            showNoNetworkMsg();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        AppLog.e(TAG, "onRequestPermissionsResult " + requestCode);
        switch (requestCode) {
            case REQUEST_CALL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppLog.e(TAG, "gra " + requestCode);
                    if (intent != null) {
                        startActivity(intent);
                    }
                } else {
                    AppLog.e(TAG, "not gr " + requestCode);
                }
                return;
        }
    }

    @Override
    public void onCallClick(LeadItem item) {
        intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + item.getPhone()));
      /*  if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            AppLog.e(TAG, "per");
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {*/
            AppLog.e(TAG, "acti");
            startActivity(intent);
        //}
    }

    @Override
    public void onSMSClick(LeadItem item) {
        intent = new Intent(getActivity(), AddPostActivity.class);
        AppLog.e(TAG, "onSMSClick group_id " + groupId);
        intent.putExtra("id", groupId);
        intent.putExtra("friend_id", item.getId());
        intent.putExtra("friend_name", item.getName());
        intent.putExtra("type", "personal");
        intent.putExtra("team_id", teamId);
        intent.putExtra("from_chat", itemClick);
        startActivity(intent);

        if(!itemClick)
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.PERSONAL_POST_ADDED_1,true);

        getActivity().finish();
    }

    @Override
    public void onMailClick(LeadItem item) {

        if (((LeadsListActivity) getActivity()).isConnectionAvailable()) {
            Intent intent = new Intent(getActivity(), NestedTeamActivity.class);
            intent.putExtra("group_id", groupId);
            intent.putExtra("team_id", teamId);
            intent.putExtra("user_id", item.id);
            intent.putExtra("title", item.name);
            startActivity(intent);
        } else {
            ((LeadsListActivity) getActivity()).showNoNetworkMsg();
        }

    }

    @Override
    public void onNameClick(LeadItem item) {
        AppLog.e(TAG, "onNameClick()"+new Gson().toJson(item));
        if (isAdmin) {
            Intent intent = new Intent(getActivity(), LeadDetailActivity.class);
            Bundle b = new Bundle();
            b.putParcelable(LeadDetailActivity.EXTRA_LEAD_ITEM, item);
            intent.putExtras(b);
            intent.putExtra("post", item.getIsPost());
            intent.putExtra("allowedToAddUser", item.allowedToAddUser);
            intent.putExtra("allowedToAddTeamPost", item.allowedToAddTeamPost);
            intent.putExtra("allowedToAddTeamPostComment", item.allowedToAddTeamPostComment);
            intent.putExtra("type", "friend");
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("team_id", teamId);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
            hideLoadingBar();

            if (apiId == LeafManager.API_ID_LEAD_LIST)
            {
                LeadResponse res = (LeadResponse) response;

                list = res.getResults();
                mAdapter.addItems(list);
                mAdapter.notifyDataSetChanged();
                if (currentPage == 1) {
                    mBinding.recyclerView.setAdapter(mAdapter);
                }
                mBinding.setSize(mAdapter.getItemCount());
                totalPages = res.totalNumberOfPages;
                mIsLoading = false;
            }
            else
            {

            }


    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();

        if (apiId == LeafManager.API_ID_LEAD_LIST)
        {
            mIsLoading = false;
            currentPage = currentPage - 1;
            if (currentPage < 0) {
                currentPage = 1;
            }
        }

        if (msg.contains("401:Unauthorized")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();

        if (apiId == LeafManager.API_ID_LEAD_LIST)
        {
            mIsLoading = false;
            currentPage = currentPage - 1;
            if (currentPage < 0) {
                currentPage = 1;
            }
        }

        try {
            Toast.makeText(getActivity(), getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            AppLog.e("LeadListFragment", "OnExecption : " + msg);
        }
    }

    public void refreshData(List<LeadItem> result) {
        hideLoadingBar();
        mAdapter.clear();
        mAdapter.addItems(result);
        mAdapter.notifyDataSetChanged();
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.setSize(mAdapter.getItemCount());
        totalPages = 0;
        mIsLoading = false;
    }

    public void search(String strsearch) {
        if(list!=null){
            if(!TextUtils.isEmpty(strsearch)){
                ArrayList<LeadItem> newList = new ArrayList<>();
                for (int i=0;i<list.size();i++){
                    if(list.get(i).name.toLowerCase().contains(strsearch.toLowerCase())){
                        newList.add(list.get(i));
                    }
                }
                mAdapter.clear();
                mAdapter.addItems(newList);
                mAdapter.notifyDataSetChanged();
            }else {
                mAdapter.clear();
                mAdapter.addItems(list);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public class ClassesStudentAdapter extends RecyclerView.Adapter<ClassesStudentAdapter.ViewHolder> {
        List<BoothMemberResponse.BoothMemberData> list;
        private Context mContext;

        public ClassesStudentAdapter(List<BoothMemberResponse.BoothMemberData> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class_student, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final BoothMemberResponse.BoothMemberData item = list.get(position);

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
            holder.txt_count.setText("Role: "+item.roleOnConstituency);
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    mBinding.setMessage(R.string.msg_no_leads);
                }
                return list.size();
            } else {
                mBinding.setMessage(R.string.msg_no_leads);
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
                    public void onClick(View view) {
                        editStudent(list.get(getAdapterPosition()));
                    }
                });
            }
        }
    }

    private void editStudent(BoothMemberResponse.BoothMemberData studentData) {
        Intent i = new Intent(getActivity(), VoterProfileActivity.class);
        i.putExtra("userID",studentData.id);
        i.putExtra("name",studentData.name);
        startActivity(i);
    }
}
