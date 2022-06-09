
package school.campusconnect.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import school.campusconnect.utils.AppLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.baoyz.widget.PullRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import school.campusconnect.R;
import school.campusconnect.adapters.UserListAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.LayoutListBinding;
import school.campusconnect.datamodel.AddressItem;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ContactListItem;
import school.campusconnect.datamodel.ContactListResponse;
import school.campusconnect.datamodel.UserListItem;
import school.campusconnect.datamodel.UserListResponse;
import school.campusconnect.datamodel.gruppiecontacts.AllContactModel;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;

public class AllUserListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {

    private LayoutListBinding mBinding;
    private UserListAdapter mAdapter;

    Intent intent;
    String groupId = "";
    boolean change;
    LeafManager mManager = new LeafManager();
    public boolean mIsLoading = false;
    public int totalPages = 1;
    public int currentPage = 1;

    ArrayList<UserListItem> list_items = new ArrayList<UserListItem>();

    public int lastSent;
    public int limit;

    List<String> list_id = new ArrayList<>();

    private boolean toUpdate;

    public AllUserListFragment() {

    }

    public static AllUserListFragment newInstance(Bundle b) {
        AllUserListFragment fragment = new AllUserListFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_list/*_without_refresh*/, container, false);
        mBinding.setSize(1);
        mBinding.setMessage(R.string.msg_no_users);
        final LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView.setLayoutManager(manager);
        groupId = getArguments().getString("id");
        change = getArguments().getBoolean("change", false);

        lastSent = 0;
        limit = 9;

        ActiveAndroid.initialize(getActivity());

       AppLog.e("Change", "change is " + change);

        mAdapter = new UserListAdapter(new ArrayList<UserListItem>(), 0, change);

        mBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = manager.getChildCount();
                int totalItemCount = manager.getItemCount();
                int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = manager.findLastVisibleItemPosition();
                if (!mIsLoading && totalPages > currentPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            ) {
                        currentPage = currentPage + 1;
                        getData();
                    }
                }
            }
        });

        mBinding.swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    mAdapter.clear();
                    mAdapter = new UserListAdapter(new ArrayList<UserListItem>(), 0, change);
                    currentPage = 1;
                    getData();
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                } else {
                    showNoNetworkMsg();
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        List<AllContactModel> userIdList = AllContactModel.getAll(groupId+"");

        if (userIdList.size() != 0) {
            showLoadingBar(mBinding.progressBar,false);
            new TaskForAllUsers().execute();
        } else
            getData();

        return mBinding.getRoot();
    }
    private static String[] fromString(String string) {
        if (string != null && !string.equals("null"))
            return string.replace("[", "").replace("]", "").split(", ");
        else
            return new String[0];
    }

    @Override
    public void onResume() {
        super.onResume();
       AppLog.e("AllUserList", "OnResume Called : " + LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISUSERDELETED));
        if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISUSERDELETED))
        {
            list_items.clear();
            list_id.clear();
            mAdapter.clear();
            currentPage = 1;
            getData();
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISUSERDELETED, false);
        }
    }

    private void getData() {
        if(isConnectionAvailable())
        {
            toUpdate = false;
            showLoadingBar(mBinding.progressBar,false);
            mIsLoading = true;
            mManager.getAllUsersList(this, groupId+"", currentPage);
        }
        else {
            showNoNetworkMsg();
        }

    }
    private String getFriendIds() {

        if (limit >= list_items.size())
            limit = list_items.size() - 1;

        String friendsIds = "";

        for (int i = lastSent; i <= limit; i++) {
           AppLog.e("Friends_Ids", "for is " + list_items.get(i).getId());
            friendsIds = friendsIds + list_items.get(i).getId() + ",";
        }

        if (!friendsIds.equals("")) {
            friendsIds = friendsIds.substring(0, friendsIds.length() - 1);
        }

       AppLog.e("Friends_Ids", "list is " + friendsIds);
        lastSent = lastSent + 10;
        limit = limit + 10;
       AppLog.e("Friends_Ids", "callApi called - lastSent is " + lastSent + " limit is " + limit);

        return friendsIds;
    }


    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        try {
            DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
            int count = databaseHandler.getCount();

            if (apiId == LeafManager.API_UPDATE_CONTACT_LIST) {

                ContactListResponse res = (ContactListResponse) response;
                List<ContactListItem> dbSaveList = new ArrayList<ContactListItem>();
                List<ContactListItem> list = res.getResults();

                for (int i = 0; i < list.size(); i++) {
                    int position = list_id.indexOf(list.get(i).getId());
                    if (position > 0) {
                        ContactListItem item1 = res.getResults().get(i);
                        UserListItem item = new UserListItem();
                        item.setId(item1.getId()+"");
                        item.setName(item1.getName());
                        item.setPhone(item1.getPhone());

                        item.setImage(item1.getImage());

                        item.setName(list_items.get(position).getName());

                        this.list_items.set(position, item);
                        mAdapter.updateItem(position, item);
                        mAdapter.notifyItemChanged(position);
                        item1.setName(item.getName());
                        dbSaveList.add(item1);
                    }
                }

                new TaskForUpdateDB(dbSaveList).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {



               AppLog.e("MAIN", "called");

                hideLoadingBar();

                UserListResponse res_main = (UserListResponse) response;

                AllContactModel.deleteAll();

                UserListResponse res = new UserListResponse();
                ArrayList<UserListItem> list_items = new ArrayList<UserListItem>();
                for (int i = 0; i < res_main.getResults().size(); i++) {
                    UserListItem item1 = res_main.getResults().get(i);

                    if (count != 0) {
                        try {
//                       AppLog.e("CONTACTSS", "count is " + databaseHandler.getCount());
                            String name = databaseHandler.getNameFromNum(item1.getPhone().replaceAll(" ", ""));
//                       AppLog.e("CONTACTSS", "api name is " + item.getName());
//                       AppLog.e("CONTACTSS", "name is " + name);
//                       AppLog.e("CONTACTSS", "num is " + item.getPhone());
                            if (!name.equals("")) {
                                item1.setName(name);
                            } else {
//                            list.get(i).setName(list.get(i).getName());
                            }
                        } catch (NullPointerException e) {
//                        list.get(i).setName(list.get(i).getName());
                        }
                    } else {
                       AppLog.e("CONTACTSS", "count is 0");
//                    list.get(i).setName(list.get(i).getName());
                    }
                    list_items.add(item1);

                   AppLog.e("MAIN", "called " + list_items.get(i).getName());

                /*AllContactModel model = new AllContactModel();

                model.id = groupId;
                model.all_id = item1.getId();
//                model.all_name = item1.getName();
                model.all_name = item.getName();
                model.all_phone = item1.getPhone();
                model.all_email = item1.getEmail();
                model.all_leadCount = item1.getLeadCount();
                model.all_dob = item1.getDob();
                model.all_qualification = item1.getQualification();
                model.all_occupation = item1.getOccupation();
                model.all_image = item1.getImage();
                model.all_otherLeads = Arrays.toString(item1.getOtherLeads());
                model.all_groups = Arrays.toString(item1.getGroups());
                model.all_group_ids = Arrays.toString(item1.getGroupIds());
                model.all_gender = item1.gender;
                model.all_id = item1.getId();
                model.line1 = item1.getAddress().line1;
                model.line2 = item1.getAddress().line2;
                model.district = item1.getAddress().district;
                model.state = item1.getAddress().state;
                model.countryCode = item1.getAddress().countryCode;
                model.pin = item1.getAddress().pin;
                model.is_post = item1.isAllowedToPost;
                model.save();*/
                }

                res.data = list_items;
                res.totalNumberOfPages = res_main.totalNumberOfPages;
                mAdapter.addItems(list_items);

                if (currentPage == 1) {
                    mBinding.recyclerView.setAdapter(mAdapter);
                }

                mAdapter.notifyDataSetChanged();
                mBinding.setSize(mAdapter.getItemCount());
                totalPages = res.totalNumberOfPages;
                mIsLoading = false;

            }
        }catch (Exception e){}
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        mIsLoading = false;
        //Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        try {
            if (msg.contains("401:Unauthorized") || msg.contains("401")) {
                Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
                logout();
            } else {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){}


    }


    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        mIsLoading = false;
        try {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
           AppLog.e("AllUserList", "onException : " + msg);
        }
    }

    public void refreshData(BaseResponse response) {
        hideLoadingBar();
        mAdapter.clear();
        UserListResponse res = (UserListResponse) response;
        mAdapter.addItems(res.getResults());
        mAdapter.notifyDataSetChanged();
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.setSize(mAdapter.getItemCount());
        totalPages = res.totalNumberOfPages;
        mIsLoading = false;
    }

    public void getFilteredList(String str) {
       AppLog.e("FILTER", "string is " + str);
        list_items.clear();
        list_id.clear();
        mAdapter.clear();
        List<AllContactModel> list = AllContactModel.getFilteredList(groupId+"", str);
       AppLog.e("FILTER", "size is " + list.size());
        for (int i = 0; i < list.size(); i++) {
           AppLog.e("FILTER", "name is " + list.get(i).all_id);
            AddressItem addressItem = new AddressItem();
            addressItem.line1 = list.get(i).line1;
            addressItem.line2 = list.get(i).line2;
            addressItem.district = list.get(i).district;
            addressItem.state = list.get(i).state;
            addressItem.country = list.get(i).country;
            addressItem.pin = list.get(i).pin;

            UserListItem item = new UserListItem();
            item.setId(list.get(i).all_id);
            item.setName(list.get(i).all_name);
            item.setPhone(list.get(i).all_phone);
            item.setEmail(list.get(i).all_email);
            item.setLeadCount(list.get(i).all_leadCount);
            item.setDob(list.get(i).all_dob);
            item.setQualification(list.get(i).all_qualification);
            item.setOccupation(list.get(i).all_occupation);
            item.setImage(list.get(i).all_image);
            item.setOtherLeads(fromString(list.get(i).all_otherLeads));
            item.setAddress(addressItem);
            item.gender = list.get(i).all_gender;
            item.isPost = list.get(i).is_post;

            list_items.add(item);
            list_id.add(list.get(i).all_id);
        }

        mAdapter.addItems(list_items);
        mAdapter.notifyDataSetChanged();
        mBinding.setSize(mAdapter.getItemCount());

    }

    private class TaskForAllUsers extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            List<AllContactModel> userIdList = AllContactModel.getAll(groupId+"");
           AppLog.e("AllUserList" , "TaskForAllUser DoInBackground : "+groupId+" , size : "+userIdList.size() );

            toUpdate = true;
            DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
            for (int i = 0; i < userIdList.size(); i++) {

                String contactName;
                /*if (databaseHandler.getCount() != 0) {
                    try {
                        String name = databaseHandler.getNameFromNum(userIdList.get(i).all_phone.replaceAll(" ", ""));
                        if (!name.equals("")) {
                            contactName = name;
                        } else {
                            contactName = userIdList.get(i).all_name;
                        }
                    } catch (NullPointerException e) {
                        contactName = userIdList.get(i).all_name;
                    }

                } else {
                   AppLog.e("CONTACTSS", "count is 0");
                    contactName = userIdList.get(i).all_name;
                }*/

                UserListItem item = new UserListItem();

                AddressItem addressItem = new AddressItem();
                addressItem.line1 = userIdList.get(i).line1;
                addressItem.line2 = userIdList.get(i).line2;
                addressItem.district = userIdList.get(i).district;
                addressItem.state = userIdList.get(i).state;
                addressItem.country = userIdList.get(i).country;
                addressItem.pin = userIdList.get(i).pin;

                item.setId(userIdList.get(i).all_id);
                item.setName(userIdList.get(i).all_name);

               AppLog.e("AllUserList", "name is" + userIdList.get(i).all_name);

                item.setPhone(userIdList.get(i).all_phone);
                item.setEmail(userIdList.get(i).all_email);
                item.setLeadCount(userIdList.get(i).all_leadCount);
                item.setDob(userIdList.get(i).all_dob);
                item.setQualification(userIdList.get(i).all_qualification);
                item.setOccupation(userIdList.get(i).all_occupation);
                item.setImage(userIdList.get(i).all_image);
                item.setOtherLeads(fromString(userIdList.get(i).all_otherLeads));
                item.setAddress(addressItem);
                item.gender = userIdList.get(i).all_gender;
                item.isPost = userIdList.get(i).is_post;
                list_items.add(item);
                list_id.add(userIdList.get(i).all_id);
            }

           AppLog.e("AllUserList", "size is " + list_items.size());

            mAdapter.addItems(list_items);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter.notifyDataSetChanged();
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
            if (currentPage == 1) {
                mBinding.recyclerView.setAdapter(mAdapter);
            }
            mBinding.setSize(mAdapter.getItemCount());
            mIsLoading = false;
            hideLoadingBar();
        }
    }

    private class TaskForUpdateDB extends AsyncTask<Void, Void, Void> {

        List<ContactListItem> dbSaveList;

        public TaskForUpdateDB(List<ContactListItem> dbSaveList) {
            this.dbSaveList = dbSaveList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {


            for (int i = 0; i < dbSaveList.size(); i++) {

                ContactListItem item1 = dbSaveList.get(i);

                AllContactModel.deleteContact(groupId+"", item1.getId()+"");

                AllContactModel model = new AllContactModel();
                model.group_id = groupId+"";
                model.all_id = item1.getId()+"";
                model.all_name = item1.getName();
                model.all_phone = item1.getPhone();
                model.all_image = item1.getImage();
                model.all_id = item1.getId()+"";
                model.save();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

}
