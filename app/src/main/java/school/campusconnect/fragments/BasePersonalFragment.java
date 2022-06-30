package school.campusconnect.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import school.campusconnect.activities.ChatActivity;
import school.campusconnect.activities.StaffListActivity;
import school.campusconnect.utils.AppLog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.LeadsListActivity;
import school.campusconnect.adapters.PersonalListAdapterNew;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.personalchat.PersonalContactsModel;
import school.campusconnect.datamodel.personalchat.PersonalPostItem;
import school.campusconnect.datamodel.personalchat.PersonalPostResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;

public class BasePersonalFragment extends BaseFragment implements PersonalListAdapterNew.OnPersonalListClickListener, LeafManager.OnCommunicationListener {
    private static final String TAG = "BasePersonalFragment";
    View view;
    RecyclerView rvPersonal;
    private ArrayList<PersonalPostItem> personalList=new ArrayList<>();
    TextView tvError;
    private LeafManager manager;
    ProgressBar progressBar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_base_personal,container,false);
        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPersonalList();

    }

    private void getPersonalList() {
        if (isConnectionAvailable()) {
           // showLoadingBar(progressBar);
            progressBar.setVisibility(View.VISIBLE);
            manager.getPersonalContacts(this, GroupDashboardActivityNew.groupId);
        } else {
            showNoNetworkMsg();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_notification).setVisible(false);
        menu.findItem(R.id.action_friend_list).setVisible(false);
        if("admin".equals(((ChatActivity)getActivity()).role)){
            menu.findItem(R.id.menu_add_chat).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            default:
                return super.onOptionsItemSelected(item);
            case R.id.menu_add_chat:

               startActivity(new Intent(getContext(), StaffListActivity.class));

               /* Intent intent2 = new Intent(getActivity(), LeadsListActivity.class);
                intent2.putExtra("id",  GroupDashboardActivityNew.groupId);
                intent2.putExtra("apiCall", false);
                intent2.putExtra("team_id", LeafPreference.getInstance(getActivity()).getString(LeafPreference.LOGIN_ID));
                intent2.putExtra("item_click", true);
                startActivity(intent2);*/
                return true;
        }


    }

    private void init() {
        manager=new LeafManager();
        rvPersonal=view.findViewById(R.id.rvPersonal);
        tvError=view.findViewById(R.id.tvError1);
        progressBar=view.findViewById(R.id.progressBar);
    }

    @Override
    public void onPersonalClick(PersonalPostItem personalPostItem) {
        GroupDashboardActivityNew.selectedUserInChat = personalPostItem.userId;
        ((ChatActivity)getActivity()).changePersonalFrag(personalPostItem);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
       // hideLoadingBar();
        progressBar.setVisibility(View.GONE);
        switch (apiId)
        {
            case LeafManager.API_PERSONAL_CONTACTS:
                PersonalPostResponse res2 = (PersonalPostResponse) response;
                AppLog.e(TAG,"PersonalInbox Response :"+new Gson().toJson(res2));
                PersonalContactsModel.deleteAll();
                personalList = res2.getResults();

                for (int i = 0; i < res2.getResults().size(); i++) {
                    PersonalPostItem item = res2.getResults().get(i);

                    PersonalContactsModel personalContactsModel = new PersonalContactsModel();
                    personalContactsModel.group_id = GroupDashboardActivityNew.groupId;
                    personalContactsModel.friend_id = item.userId+"";
                    personalContactsModel.name = item.name;
                    personalContactsModel.image = item.image;
                    personalContactsModel.phone = item.phone;
                    personalContactsModel.updatedTime = item.updatedAtTime;
                    personalContactsModel.provideSettings = item.provideSettings;
                    personalContactsModel.allowToPost = item.allowToPost;
                    personalContactsModel.allowPostComment = item.allowPostComment;
                    personalContactsModel.save();


                    try {
                        DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());

                        if (databaseHandler.getCount() != 0) {
                            try {
                                String name = databaseHandler.getNameFromNum(personalList.get(i).phone.replaceAll(" ", ""));
                                if (!name.equals("")) {
                                    personalList.get(i).name = name ;
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                if(personalList!=null)
                {
                    if(personalList.size()>0)
                    {
                        rvPersonal.setLayoutManager(new LinearLayoutManager(getActivity()));
                        rvPersonal.setAdapter(new PersonalListAdapterNew(personalList,this));
                        tvError.setVisibility(View.GONE);
                    }
                    else
                    {
                        tvError.setVisibility(View.VISIBLE);
                    }

                }
                else
                {
                    tvError.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        // hideLoadingBar();
        progressBar.setVisibility(View.GONE);

        if (getActivity() != null) {

            if (msg.contains("401:Unauthorized")) {
                Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
                logout();
            } else if (msg.contains("404")) {
                Toast.makeText(getActivity(), "No posts available.", Toast.LENGTH_SHORT).show();
                tvError.setVisibility(View.VISIBLE);
            } else if (msg.contains("418")) {
                if (apiId == LeafManager.API_REPORT)
                    Toast.makeText(getActivity(), "You have already reported this post", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }

        }


    }

    @Override
    public void onException(int apiId, String msg) {
        // hideLoadingBar();
        progressBar.setVisibility(View.GONE);
        if (getActivity() != null)
            Toast.makeText(getActivity(), getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }
}
