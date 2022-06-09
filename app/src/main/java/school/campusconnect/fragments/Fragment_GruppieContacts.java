
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

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import school.campusconnect.R;
import school.campusconnect.activities.AddFriendActivity;
import school.campusconnect.activities.BaseActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.adapters.ContactListAdapter;
import school.campusconnect.adapters.GruppieContactListAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.LayoutListWithoutRefreshBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ContactListItem;
import school.campusconnect.datamodel.ContactListResponse;
import school.campusconnect.datamodel.GruppieContactListItem;
import school.campusconnect.datamodel.GruppieContactListResponse;
import school.campusconnect.datamodel.gruppiecontacts.GruppieContactGroupIdModel;
import school.campusconnect.datamodel.gruppiecontacts.GruppieContactsModel;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;

public class Fragment_GruppieContacts extends BaseFragment implements LeafManager.OnCommunicationListener, GruppieContactListAdapter.OnAddFriendListener {

    private LayoutListWithoutRefreshBinding mBinding;
    private GruppieContactListAdapter mAdapter;
    private ArrayList<GruppieContactListItem> list_items = new ArrayList<>();
    private ArrayList<String> selected_ids = new ArrayList<>();
    Intent intent;

    boolean searched;

    LeafManager mManager = new LeafManager();
    public boolean mIsLoading = false;
    public int totalPages = 1;
    public int currentPage = 1;
    public String groupId;
    public String friendId;
    List<String> list_id = new ArrayList<>();

    public int lastSent;
    public int limit;
    int count;

    public static boolean synchFromAddFrend=false;

    public Fragment_GruppieContacts() {
    }

    public static Fragment_GruppieContacts newInstance(Bundle b) {
        Fragment_GruppieContacts fragment = new Fragment_GruppieContacts();
       AppLog.e("Fragment_GruppieContact", "newInstance : " + b.getString("id"));
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_list_without_refresh, container, false);
        mBinding.setSize(1);
        synchFromAddFrend=false;
        mBinding.setMessage(R.string.msg_no_users);
        final LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView.setLayoutManager(manager);
        DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
        int count = databaseHandler.getCount();
        mAdapter = new GruppieContactListAdapter(Fragment_GruppieContacts.this, new ArrayList<GruppieContactListItem>(), ContactListAdapter.OPTION_ADD, count);
        groupId = getArguments().getString("id", "");

        lastSent = 0;
        limit = 9;

        searched = false;

        new TaskForFriends().execute();
//        getData();

       AppLog.e("Fragment_GruppieContact", "OnCreateView , GroupID : " + groupId);
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

                if (lastVisibleItemPosition >= lastSent) {
                   AppLog.e("Friends_Ids", "lastVisibleItemPosition called- limit is " + limit + " lastVisibleItemPosition is " + lastVisibleItemPosition);
                    callApi();
                }

                /*if (!mIsLoading && totalNumberOfPages > currentPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            ) {
                        currentPage = currentPage + 1;
                        getData();
                    }
                }*/
            }

        });

        /*mBinding.swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    mAdapter.clear();
                    currentPage = 1;
//                    getData();
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                } else {
                    showNoNetworkMsg();
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                }
            }
        });*/
//        getData();

        return mBinding.getRoot();
    }

    private static String[] fromString(String string) {
//        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        /* String result[] = new String[strings.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = strings[i];
        }*/
        if (string != null && !string.equals("null"))
            return string.replace("[", "").replace("]", "").split(", ");
        else
            return new String[0];
    }

    public void callApi() {
        if (isConnectionAvailable())
            getUpdatedData();
    }

    private void getUpdatedData() {
//        showLoadingBar(mBinding.progressBar);
//        mIsLoading = true;
        mManager.updatedLeadsList(this, groupId+"", getFriendIds());
//        getFriendIds();
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
    public void onResume() {
        super.onResume();
       AppLog.e("AllUserList", "OnResume Called : " + LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISUSERDELETED));
        if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISUSERDELETED)) {
            mAdapter.clear();
            list_items.clear();
            list_id.clear();
            currentPage = 1;
            new TaskForFriends().execute();
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISUSERDELETED, false);
        }
    }

    private void getData() {
//        showLoadingBar(mBinding.progressBar);
        mIsLoading = true;
        mManager.getGruppieContactList(this, groupId+"", currentPage);
    }

    /*private void getData(){
        List<GruppieContactsModel> list = GruppieContactsModel.getAddFriendList(groupId);

        Set<GruppieContactsModel> unique = new LinkedHashSet<GruppieContactsModel>(list);
        List<GruppieContactsModel> userIdList = new ArrayList<GruppieContactsModel>(unique);


        showLoadingBar(mBinding.progressBar);
        List<GruppieContactListItem> data = new ArrayList<>();
        for (int i = 0; i < userIdList.size(); i++) {

            AddressItem addressItem = new AddressItem();
            addressItem.line1 = userIdList.get(i).line1;
            addressItem.line2 = userIdList.get(i).line2;
            addressItem.district = userIdList.get(i).district;
            addressItem.state = userIdList.get(i).state;
            addressItem.countryCode = userIdList.get(i).countryCode;
            addressItem.pin = userIdList.get(i).pin;

            GruppieContactListItem item = new GruppieContactListItem();
            item.setId(userIdList.get(i).contact_id);
            item.setName(userIdList.get(i).contact_name);
            item.setPhone(userIdList.get(i).contact_phone);
            item.setEmail(userIdList.get(i).contact_email);
            item.setLeadCount(userIdList.get(i).contact_leadCount);
            item.setDob(userIdList.get(i).contact_dob);
            item.setQualification(userIdList.get(i).contact_qualification);
            item.setOccupation(userIdList.get(i).contact_occupation);
            item.setImage(userIdList.get(i).contact_image);
            item.setOtherLeads(fromString(userIdList.get(i).contact_otherLeads));
            item.setAddress(addressItem);
            item.gender = userIdList.get(i).contact_gender;
            data.add(item);
        }


        mAdapter.addItems(data);
        mAdapter.notifyDataSetChanged();
        if (currentPage == 1) {
            mBinding.recyclerView.setAdapter(mAdapter);
        }
        mBinding.setSize(mAdapter.getItemCount());
        mIsLoading = false;

        hideLoadingBar();
    }*/

    public void searchedData(String input) {
        lastSent=0;
        limit=9;
        currentPage = 1;
        mAdapter.clear();
        list_items.clear();
        list_id.clear();

        List<GruppieContactsModel> list = GruppieContactsModel.getAll();

        Set<GruppieContactsModel> unique = new LinkedHashSet<GruppieContactsModel>(list);
        List<GruppieContactsModel> userIdList = new ArrayList<GruppieContactsModel>(unique);


        List<GruppieContactListItem> data = new ArrayList<>();
        for (int i = 0; i < userIdList.size(); i++) {

            GruppieContactListItem item = new GruppieContactListItem();
            item.setId(userIdList.get(i).contact_id);
            item.setName(userIdList.get(i).contact_name);
            item.setPhone(userIdList.get(i).contact_phone);
            item.setImage(userIdList.get(i).contact_image);
            if (isIdSelected(userIdList.get(i).contact_id)) {
                item.isSelected = true;
            }

            data.add(item);
            list_items.add(item);
            list_id.add(userIdList.get(i).contact_id);
        }


        mAdapter.addItems(data);
        mAdapter.notifyDataSetChanged();
        if (currentPage == 1) {
            mBinding.recyclerView.setAdapter(mAdapter);
        }
        mBinding.setSize(mAdapter.getItemCount());
        mIsLoading = false;
        callApi();

    }

    public void refreshAdapter(BaseResponse response, String search) {
        searched = search.length() != 0;
        GruppieContactListResponse res = (GruppieContactListResponse) response;
        mAdapter.clear();
        list_items.clear();
        list_id.clear();
        mAdapter.addItems(res.getResults());
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.setSize(mAdapter.getItemCount());
        mAdapter.notifyDataSetChanged();
        mBinding.setSize(mAdapter.getItemCount());
        //totalNumberOfPages = res.totalNumberOfPages;
        mIsLoading = false;
    }

    public void addMultipleFriends() {
       AppLog.e("ADD_MULTI", "addMultipleFriends fragment, add clicked");
        if (isConnectionAvailable()) {
            if (selected_ids.size() != 0) {
//                if (canAdd()) {
                showLoadingBar(mBinding.progressBar,false);
                LeafManager manager = new LeafManager();
//            manager.addFriendToGroup(Fragment_GruppieContacts.this, groupId, fId, getActivity());
                manager.addMultipleFriendToGroup(Fragment_GruppieContacts.this, groupId+"", getSelectedIds(), getActivity());

                cleverTapAddFriend(getSelectedIds());

                /*} else {
                    Toast.makeText(getActivity(), "Cannot add more than 20 friends", Toast.LENGTH_SHORT).show();
                }*/
            } else
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_select_friend_first), Toast.LENGTH_SHORT).show();
        } else {
            showNoNetworkMsg();
        }

    }

    private void cleverTapAddFriend(String selectedIds) {
        try {
            CleverTapAPI cleverTap = CleverTapAPI.getInstance(getActivity());
           AppLog.e("AddFriend","Success to found cleverTap objects=>");

            String ids[]=selectedIds.split(",");
            for (int i=0;i<ids.length;i++)
            {
                HashMap<String, Object> addFriendAction = new HashMap<String, Object>();
                addFriendAction.put("id",groupId);
                addFriendAction.put("group_name", GroupDashboardActivityNew.group_name);
                addFriendAction.put("friend_id", ids[i]);
                cleverTap.event.push("Add Friends", addFriendAction);
            }
           AppLog.e("AddFriend","Success =>");

        } catch (CleverTapMetaDataNotFoundException e) {
           AppLog.e("AddFriend","CleverTapMetaDataNotFoundException=>"+e.toString());
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
           AppLog.e("AddFriend","CleverTapPermissionsNotSatisfied=>"+e.toString());
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }

    }

    private void refreshAdapterPhone(BaseResponse response) {
        GruppieContactListResponse res = (GruppieContactListResponse) response;
        mAdapter.clear();
        list_items.clear();
        list_id.clear();
        mAdapter.addItems(res.getResults());
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.setSize(mAdapter.getItemCount());
        mAdapter.notifyDataSetChanged();
        mBinding.setSize(mAdapter.getItemCount());
        //totalNumberOfPages = res.totalNumberOfPages;
        mIsLoading = false;
    }


    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
        if (apiId == LeafManager.API_UPDATE_CONTACT_LIST) {

            ContactListResponse res = (ContactListResponse) response;
            List<ContactListItem> dbSaveList = new ArrayList<>();
            List<ContactListItem> list = res.getResults();
//            DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
//            int count = databaseHandler.getCount();
            for (int i = 0; i < list.size(); i++) {
                int position = list_id.indexOf(list.get(i).getId());
                if(position>0)
                {
                   AppLog.e("ASDFff", "if called " + list.get(i).getId());
                    ContactListItem item1 = res.getResults().get(i);
                    GruppieContactListItem item = new GruppieContactListItem();
                    item.setId(item1.getId());
                    item.setName(item1.getName());
                    item.setPhone(item1.getPhone());
                    item.setImage(item1.getImage());

                    if (isIdSelected(item1.getId())) {
                        item.isSelected = true;
                    }

                    item.setName(list_items.get(position).getName());

                    item1.setName(item.getName());

                    this.list_items.set(position, item);
                    mAdapter.updateItem(position, item);
                    dbSaveList.add(item1); }
                else {
                   AppLog.e("ASDFff", "else called " + list.get(i).getId());
                }


            }

            new TaskForUpdateDB(dbSaveList).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } else if (apiId == LeafManager.API_GRUPPIE_CONTACT_LIST) {
            hideLoadingBar();
            GruppieContactListResponse res = (GruppieContactListResponse) response;
            mAdapter.addItems(res.getResults());
            mAdapter.notifyDataSetChanged();
            if (currentPage == 1) {
                mBinding.recyclerView.setAdapter(mAdapter);
            }
            mBinding.setSize(mAdapter.getItemCount());
            totalPages = res.pages;
            mIsLoading = false;
        } else if (apiId == LeafManager.API_ADD_FRIEND_TOGROUP) {
            try {
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_add_success), Toast.LENGTH_SHORT).show();
//            GruppieContactGroupIdModel.deleteRow(groupId, friendId);

                if (getSelectedIds().contains(",")) {

                    int[] ids = fromStringInt(getSelectedIds());
                    for (int i = 0; i < ids.length; i++) {
                        GruppieContactGroupIdModel idModel = new GruppieContactGroupIdModel();
                        idModel.group_id = groupId+"";
                        idModel.user_id = ids[i]+"";
                        idModel.save();
                    }

                } else {
                    GruppieContactGroupIdModel idModel = new GruppieContactGroupIdModel();
                    idModel.group_id = groupId+"";
                    idModel.user_id = getSelectedIds();
                    idModel.save();
                }
                currentPage = 1;
                totalPages = 1;
                mAdapter.clear();
                list_items.clear();
                list_id.clear();
                selected_ids.clear();
                ((AddFriendActivity) getActivity()).setAddCountAct(0);
                new TaskForFriends().execute();
            }catch (Exception e){}

        }
    }

    private static int[] fromStringInt(String string) {
        if (string != null && !string.equals("null")) {
            String[] strings = string/*.replace("[", "").replace("]", "")*/.split(",");
            int result[] = new int[strings.length];
            for (int i = 0; i < result.length; i++) {
                result[i] = Integer.parseInt(strings[i]);
            }
            return result;
        } else
            return new int[0];

    }

    @Override
    public void onFailure(int apiId, String msg) {
        try {
            if (apiId == LeafManager.API_GRUPPIE_CONTACT_LIST) {
                hideLoadingBar();
                mIsLoading = false;
                //  Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

                if (msg.contains("401:Unauthorized")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
                    logout();
                } else {
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                }
            } else if (apiId == LeafManager.API_ADD_FRIEND_TOGROUP) {
                hideLoadingBar();
                mIsLoading = false;
                //  Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                if (msg.contains("401:Unauthorized")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
                    logout();
                }
                else if(msg.contains("404"))
                {
                    //TODO Synchronize contact

                    ((BaseActivity)getActivity()).getContactsWithPermission();
                    synchFromAddFrend=true;

                }
                else {
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                }

            }
        }catch (Exception e){}

    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        mIsLoading = false;
        try {
            Toast.makeText(getActivity(), getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
           AppLog.e("Fragment_GruContacts", "OnException : " + msg);
        }
    }

    @Override
    public void onAddFriendToGroup(String fId) {
        friendId = fId;
        if (isConnectionAvailable()) {
            showLoadingBar(mBinding.progressBar,false);
            LeafManager manager = new LeafManager();
//            manager.addFriendToGroup(Fragment_GruppieContacts.this, groupId, fId, getActivity());
//            manager.addMultipleFriendToGroup(Fragment_GruppieContacts.this, groupId, fId, getActivity());
        } else {
            showNoNetworkMsg();
        }
    }

    @Override
    public void friendSelected(boolean isChecked, GruppieContactListItem item, int position) {
        if (isChecked) {
            addSelectedId(item.getId());
            list_items.get(position).isSelected = true;
            item.isSelected = true;
            mAdapter.updateItem(position, item);
        } else {
            removeId(item.getId());
            list_items.get(position).isSelected = false;
            item.isSelected = false;
            mAdapter.updateItem(position, item);
        }
    }

    private class TaskForFriends extends AsyncTask<Void, Void, Void> {

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
        protected Void doInBackground(Void... params) {


            List<GruppieContactsModel> list = GruppieContactsModel.getAll();

            Set<GruppieContactsModel> unique = new LinkedHashSet<GruppieContactsModel>(list);
            List<GruppieContactsModel> userIdList = new ArrayList<GruppieContactsModel>(unique);

            for (int i = 0; i < userIdList.size(); i++) {

               AppLog.e("QUERY", "UserId : " + userIdList.get(i).contact_id);
                GruppieContactListItem item = new GruppieContactListItem();
                item.setId(userIdList.get(i).contact_id);
                item.setName(userIdList.get(i).contact_name);
                item.setPhone(userIdList.get(i).contact_phone);
                item.setImage(userIdList.get(i).contact_image);
                list_items.add(item);
                list_id.add(userIdList.get(i).contact_id);
            }

            mAdapter.addItems(list_items);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
            mAdapter.notifyDataSetChanged();
            if (currentPage == 1) {
                mBinding.recyclerView.setAdapter(mAdapter);
            }
            mBinding.setSize(mAdapter.getItemCount());
            mIsLoading = false;
           AppLog.e("DATABASE_CHECK", "add friends size is " + list_items.size());
            hideLoadingBar();
        }
    }

    private class TaskForUpdateDB extends AsyncTask<Void, Void, Void> {

        List<ContactListItem> dbSaveList;

        private TaskForUpdateDB(List<ContactListItem> dbSaveList) {
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

                GruppieContactsModel.deleteContact(item1.getId());
                GruppieContactGroupIdModel.deleteFriend(item1.getId()+"");

                GruppieContactsModel model = new GruppieContactsModel();
                model.contact_id = item1.getId();
                model.contact_name = item1.getName();
                model.contact_phone = item1.getPhone();
                model.contact_image = item1.getImage();
                model.save();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    private void removeId(String id) {
        int position = selected_ids.indexOf(id);
       AppLog.e("MULTI_ADD", "removeId: id is " + id + " contains: " + selected_ids.contains(id) + " position is " + position);
        if (position >= 0) {
            selected_ids.remove(position);
        }

        ((AddFriendActivity) getActivity()).setAddCountAct(selected_ids.size());

    }

    private void addSelectedId(String id) {
        if (!selected_ids.contains(id)) {
           AppLog.e("MULTI_ADD", "addSelectedId: id is " + id);
            selected_ids.add(id);
        } else {
           AppLog.e("MULTI_ADD", "addSelectedId contains: id is " + id);
        }
        ((AddFriendActivity) getActivity()).setAddCountAct(selected_ids.size());
    }

    private boolean isIdSelected(String id) {
       AppLog.e("MULTI_ADD", "isIdSelected: id is " + id + " contains: " + selected_ids.contains(id));
        return selected_ids.contains(id);
    }

    private String getSelectedIds() {
        StringBuilder ids = new StringBuilder();
        for (int i = 0; i < selected_ids.size(); i++) {
            if (i != (selected_ids.size() - 1))
                ids.append(selected_ids.get(i)).append(",");
            else
                ids.append(selected_ids.get(i));
        }
        return ids.toString();
    }

    private boolean canAdd() {
        return selected_ids.size() <= 20;
    }

    public int getSelectedCount() {
        return selected_ids.size();
    }

}
