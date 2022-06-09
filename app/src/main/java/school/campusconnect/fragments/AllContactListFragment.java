
package school.campusconnect.fragments;

import android.app.Activity;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.List;

import school.campusconnect.R;
import school.campusconnect.adapters.ContactListAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.LayoutListWithoutRefreshBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ContactListItem;
import school.campusconnect.datamodel.ContactListResponse;
import school.campusconnect.datamodel.gruppiecontacts.GruppieContactGroupIdModel;
import school.campusconnect.datamodel.gruppiecontacts.GruppieContactsModel;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;

public class AllContactListFragment extends BaseFragment implements LeafManager.OnCommunicationListener, ContactListAdapter.OnCallListener {

    private LayoutListWithoutRefreshBinding mBinding;
    private ContactListAdapter mAdapter;

    Intent intent;

    LeafManager mManager = new LeafManager();
    public boolean mIsLoading = false;
    public int totalPages = 1;
    public int currentPage = 1;

    public int lastSent;
    public int limit;

    List<ContactListItem> list = new ArrayList<>();
    List<String> list_id = new ArrayList<>();

    public AllContactListFragment() {

    }

    public static AllContactListFragment newInstance(Bundle b) {
        AllContactListFragment fragment = new AllContactListFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_list_without_refresh, container, false);
        mBinding.setSize(1);
        mBinding.setMessage(R.string.msg_no_users);
        lastSent = 0;
        limit = 9;
        ActiveAndroid.initialize(getActivity());
        final LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView.setLayoutManager(manager);
        mAdapter = new ContactListAdapter(this, new ArrayList<ContactListItem>(), ContactListAdapter.OPTION_CALL);
        // groupId = getArguments().getInt("id", -1);

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

                if (lastVisibleItemPosition > lastSent) {
                   AppLog.e("Friends_Ids", "lastVisibleItemPosition called - limit is " + limit + " lastVisibleItemPosition is " + lastVisibleItemPosition);
                    callApi();
                }

               /*AppLog.e("Count", "visibleItemCount " + visibleItemCount);
               AppLog.e("Count", "totalItemCount " + totalItemCount);

                if (!mIsLoading && totalNumberOfPages > currentPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            ) {
                        currentPage = currentPage + 1;
                        getData();
                    }
                }*/
            }
        });

       /* mBinding.swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    mAdapter.clear();
                    currentPage = 1;
                    getData();
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                } else {
                    showNoNetworkMsg();
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                }
            }
        });*/

        List<GruppieContactsModel> dblist = GruppieContactsModel.getAll();

        if (dblist.size() != 0) {
            showLoadingBar(mBinding.progressBar,false);
            new TaskForAllContacts().execute();
//            callApi();
        } else
            getData();

        return mBinding.getRoot();
    }

    public void callApi() {
        if (isConnectionAvailable())
            getUpdatedData();
    }

    private static String[] fromString(String string) {
//        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        /* String result[] = new String[strings.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = strings[i];
        }*/
       AppLog.e("ALL C", "fromString: " + string);
        if (string != null && !string.equals("null"))
            return string.replace("[", "").replace("]", "").split(", ");
        else
            return new String[0];
    }

    private static int[] fromStringInt(String string) {
        if (string != null && !string.equals("null")) {
            String[] strings = string.replace("[", "").replace("]", "").split(", ");
            int result[] = new int[strings.length];
            for (int i = 0; i < result.length; i++) {
                result[i] = Integer.parseInt(strings[i]);
            }
            return result;
        } else
            return new int[0];

    }

    @Override
    public void onResume() {
        super.onResume();
       AppLog.e("AllUserList", "OnResume Called : " + LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISUSERDELETED));
        if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISUSERDELETED)) {
            list.clear();
            list_id.clear();
            mAdapter.clear();
            currentPage = 1;
//            getData();
            new TaskForAllContacts().execute();
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISUSERDELETED, false);
        }
    }

    private void getData() {
        if (isConnectionAvailable()) {
            showLoadingBar(mBinding.progressBar,false);
            mIsLoading = true;
            mManager.getAllContactsList(this/*, currentPage*/);
        } else {
            showNoNetworkMsg();
        }

    }

    private void getUpdatedData() {
//        showLoadingBar(mBinding.progressBar,false);
//        mIsLoading = true;
        mManager.updateContactsList(this, getFriendIds());
//        getFriendIds();
    }

    private String getFriendIds() {

        if (limit >= list.size())
            limit = list.size() - 1;

        String friendsIds = "";

        for (int i = lastSent; i <= limit; i++) {
//           AppLog.e("Friends_Ids", "for is " + list.get(i).getId());
            friendsIds = friendsIds + list.get(i).getId() + ",";
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

        if (apiId == LeafManager.API_UPDATE_CONTACT_LIST) {

            ContactListResponse res = (ContactListResponse) response;
            List<ContactListItem> dbSaveList = new ArrayList<>();
            List<ContactListItem> list = res.getResults();

            for (int i = 0; i < list.size(); i++) {
                int position = list_id.indexOf(list.get(i).getId());
                if (position > 0) {

                    list.get(i).setName(this.list.get(position).getName());

                    this.list.set(position, list.get(i));
                    mAdapter.updateItem(position, list.get(i));
                    mAdapter.notifyItemChanged(position);

                    ContactListItem item = res.getResults().get(i);
                    item.setName(list.get(i).getName());

                    dbSaveList.add(item);

                }
            }

            new TaskForUpdateDB(dbSaveList).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } else {

            hideLoadingBar();
            ContactListResponse res = (ContactListResponse) response;
            List<ContactListItem> list = res.getResults();

            mAdapter.addItems(list);
            mAdapter.notifyDataSetChanged();
            if (currentPage == 1) {
                mBinding.recyclerView.setAdapter(mAdapter);
                GruppieContactsModel.deleteAll();
                GruppieContactGroupIdModel.deleteAll();
            }
            mBinding.setSize(mAdapter.getItemCount());
            mIsLoading = false;

            try {
                DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
                int count = databaseHandler.getCount();

                for (int i = 0; i < res.getResults().size(); i++) {
                    ContactListItem item = res.getResults().get(i);

                    if (count != 0) {
                        try {
                            String name = databaseHandler.getNameFromNum(item.getPhone().replaceAll(" ", ""));
                            if (!name.equals("")) {
                                item.setName(name);
                            } else {
                                item.setName(item.getName());
                            }
                        } catch (NullPointerException e) {
                            item.setName(item.getName());
                        }
                    } else {
                       AppLog.e("CONTACTSS", "count is 0");
                        item.setName(item.getName());
                    }
                    GruppieContactsModel model = new GruppieContactsModel();
                    model.contact_id = item.getId();
                    model.contact_name = item.getName();
                    model.contact_phone = item.getPhone();
                    model.contact_image = item.getImage();
                    model.save();
                }
            } catch (Exception e) {
            }

        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        mIsLoading = false;
        //Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        if (msg.contains("401:Unauthorized") || msg.contains("401")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }

        /*if (msg.contains("401")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }*/
    }


    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        mIsLoading = false;
        try {

            Toast.makeText(getActivity(), getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
           AppLog.e("AllContactListFrag", "onException : " + msg);
        }
    }


    @Override
    public void onCallClick(String number) {
      /*  if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 100);
        } else {*/
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + number));
            startActivity(callIntent);
/*
        }*/

    }

    @Override
    public void onNameClick(ContactListItem client) {
    }

    public void getFilteredList(String str) {
       AppLog.e("FILTER", "string is " + str);
        list.clear();
        list_id.clear();
        mAdapter.clear();
        List<GruppieContactsModel> dbList = GruppieContactsModel.getFilteredList(str);
       AppLog.e("FILTER", "size is " + dbList.size());
        for (int i = 0; i < dbList.size(); i++) {
            ContactListItem item = new ContactListItem();
            item.setId(dbList.get(i).contact_id);
            item.setName(dbList.get(i).contact_name);
            item.setPhone(dbList.get(i).contact_phone);
            item.setImage(dbList.get(i).contact_image);
            this.list.add(item);
            list_id.add(dbList.get(i).contact_id);
        }

        mAdapter.addItems(this.list);
        mAdapter.notifyDataSetChanged();
        mBinding.setSize(mAdapter.getItemCount());

        callApi();
    }

    private class TaskForAllContacts extends AsyncTask<Void, Void, Void> {

        //ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();*/
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<GruppieContactsModel> dblist = GruppieContactsModel.getAll();
            for (int i = 0; i < dblist.size(); i++) {
                ContactListItem item = new ContactListItem(dblist.get(i).contact_id, dblist.get(i).contact_name, dblist.get(i).contact_phone,dblist.get(i).contact_image);
                list.add(item);
                list_id.add(dblist.get(i).contact_id);
            }

            mAdapter.addItems(list);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter.notifyDataSetChanged();
            if (currentPage == 1) {
                mBinding.recyclerView.setAdapter(mAdapter);
            }
            mBinding.setSize(mAdapter.getItemCount());
            mIsLoading = false;
            hideLoadingBar();
           AppLog.e("DATABASE_CHECK", "gruppie contacts size is " + list.size());
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

                ContactListItem item = dbSaveList.get(i);

                GruppieContactsModel.deleteContact(item.getId());
                GruppieContactGroupIdModel.deleteFriend(item.getId()+"");

                GruppieContactsModel model = new GruppieContactsModel();
                model.contact_id = item.getId();
                model.contact_name = item.getName();
                model.contact_phone = item.getPhone();
                model.contact_image = item.getImage();
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
