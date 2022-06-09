package school.campusconnect.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import school.campusconnect.BuildConfig;
import school.campusconnect.utils.AppLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.baoyz.widget.PullRefreshLayout;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.GroupListActivityNew;
import school.campusconnect.adapters.GroupAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.LayoutListBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ContactListItem;
import school.campusconnect.datamodel.ContactListResponse;
import school.campusconnect.datamodel.GroupDataItem;
import school.campusconnect.datamodel.GroupItem;
import school.campusconnect.datamodel.GroupResponse;
import school.campusconnect.datamodel.PhoneContactsItems;
import school.campusconnect.datamodel.gruppiecontacts.GruppieContactGroupIdModel;
import school.campusconnect.datamodel.gruppiecontacts.GruppieContactsModel;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;

public class GroupListFragment extends BaseFragment implements GroupAdapter.OnGroupSelectListener, LeafManager.OnCommunicationListener {

    private LayoutListBinding mBinding;
    private GroupAdapter mAdapter;
    LeafManager mManager = new LeafManager();
    GroupItem groupLocalDataItem;
    ArrayList<GroupItem> mData = new ArrayList<>();
    int version;

    public final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 9;

    public GroupListFragment() {

    }

    public static GroupListFragment newInstance() {
        return new GroupListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_list, container, false);
        mBinding.setSize(1);
        mBinding.setMessage(R.string.msg_no_groups);
        mBinding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false));
       // ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getContext(), R.dimen.item_offset_4dp);
        //mBinding.recyclerView.addItemDecoration(itemDecoration);
        ActiveAndroid.initialize(getActivity());

        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID, 0);
            version = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // Get data from local database
        List<GroupDataItem> dataItemList = GroupDataItem.getAll();
        if (dataItemList.size() != 0) // Check if data is available or not
        {
            if (version > 24) {
                if (!LeafPreference.getInstance(getActivity()).getBoolean("group_count_boolean")) {
                    mAdapter = new GroupAdapter(new ArrayList<GroupItem>(), this);
                    LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISGROUPUPDATED, false);
                    LeafPreference.getInstance(getActivity()).setBoolean("group_count_boolean", true);
                    getData();
                   AppLog.e("Data", "DataFromAPI");
                } else {
                  showLoadingBar(mBinding.progressBar,false);
                    for (int i = 0; i < dataItemList.size(); i++) {
                        if (!dataItemList.get(i).id.equals(Constants.VIVID_GROUP_ID)) {
                            groupLocalDataItem = new GroupItem();
                            groupLocalDataItem.id = dataItemList.get(i).id;
                            groupLocalDataItem.adminName = dataItemList.get(i).adminName;
                            groupLocalDataItem.name = dataItemList.get(i).name;
                            groupLocalDataItem.aboutGroup = dataItemList.get(i).aboutGroup;
                            groupLocalDataItem.adminName = dataItemList.get(i).adminName;
                            groupLocalDataItem.shortDescription = dataItemList.get(i).shortDescription;
                            groupLocalDataItem.image = dataItemList.get(i).image;
                            groupLocalDataItem.canPost = dataItemList.get(i).canPost;
                            groupLocalDataItem.isAdmin = dataItemList.get(i).isAdmin;
                            groupLocalDataItem.isPostShareAllowed = dataItemList.get(i).isPostShareAllowed;
                            groupLocalDataItem.isAdminChangeAllowed = dataItemList.get(i).isAdminChangeAllowed;
                            groupLocalDataItem.allowPostQuestion = dataItemList.get(i).allowPostQuestion;
                            groupLocalDataItem.totalUsers = dataItemList.get(i).totalUsers;
                            mData.add(groupLocalDataItem);
                        }
                    }
                    mAdapter = new GroupAdapter(mData, this);
                    mBinding.recyclerView.setAdapter(mAdapter);
                   hideLoadingBar();
                   AppLog.e("Data", "DataFromLocal");
                }
            } else {
               showLoadingBar(mBinding.progressBar,false);
                for (int i = 0; i < dataItemList.size(); i++) {
                    if (!dataItemList.get(i).id.equals(Constants.VIVID_GROUP_ID)) {
                        groupLocalDataItem = new GroupItem();
                        groupLocalDataItem.id = dataItemList.get(i).id;
                        groupLocalDataItem.adminName = dataItemList.get(i).adminName;
                        groupLocalDataItem.name = dataItemList.get(i).name;
                        groupLocalDataItem.aboutGroup = dataItemList.get(i).aboutGroup;
                        groupLocalDataItem.adminName = dataItemList.get(i).createdBy;
                        groupLocalDataItem.shortDescription = dataItemList.get(i).shortDescription;
                        groupLocalDataItem.image = dataItemList.get(i).image;
                        groupLocalDataItem.canPost = dataItemList.get(i).canPost;
                        groupLocalDataItem.isAdmin = dataItemList.get(i).isAdmin;
                        groupLocalDataItem.isPostShareAllowed = dataItemList.get(i).isPostShareAllowed;
                        groupLocalDataItem.isAdminChangeAllowed = dataItemList.get(i).isAdminChangeAllowed;
                        groupLocalDataItem.allowPostQuestion = dataItemList.get(i).allowPostQuestion;
                        mData.add(groupLocalDataItem);
                    }
                }
                mAdapter = new GroupAdapter(mData, this);
                mBinding.recyclerView.setAdapter(mAdapter);
               hideLoadingBar();
               AppLog.e("Data", "DataFromLocal");
            }
        }
        // Get data from API
        else {
            mAdapter = new GroupAdapter(new ArrayList<GroupItem>(), this);
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISGROUPUPDATED, false);
            getData();
           AppLog.e("Data", "DataFromAPI");
        }


        mBinding.swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable()) {
                    getData();
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                } else {
                    showNoNetworkMsg();
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
//        if (!LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISTEAMPOSTUPDATED)) {
        if (databaseHandler.getCount() == 0) {
            getContactsWithPermission();
        }

        /*try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "/data/data/" + getActivity().getPackageName() + "/databases/gruppie";
                String backupDBPath = "gruppie_db.db";
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {

        }*/

        return mBinding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISGROUPUPDATED) && getActivity()!=null) {
            getData();
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISGROUPUPDATED, false);
        }
    }

    private void getData() {
        if(isConnectionAvailable())
        {
            showLoadingBar(mBinding.progressBar,false);
            mManager.getGroupList(this);
        }
        else {
            showNoNetworkMsg();
        }

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

//        FirebaseMessaging.getInstance().unsubscribeFromTopic("testfirebasetopic");
//        FirebaseMessaging.getInstance().subscribeToTopic("");

        if (apiId == LeafManager.API_ID_GROUP_LIST) {

            hideLoadingBar();
            GroupResponse response1 = (GroupResponse) response;
            List<GroupItem> data = response1.data;
            if (data.size() > 0)
                data.remove(0);

            mAdapter = new GroupAdapter(data, this);
           AppLog.e("adas ", mAdapter.getItemCount() + "");
           AppLog.e("GroupListFragment", "onSucces  ,, msg : " + ((GroupResponse) response).data.toString());
            mBinding.recyclerView.setAdapter(mAdapter);
            mBinding.setSize(mAdapter.getItemCount());

            //Saving data into database
            GroupDataItem.deleteAll(); // Clear old data
            List<GroupItem> list = response1.data;
            for (int i = 0; i < list.size(); i++) {
                GroupItem item = list.get(i);
                GroupDataItem groupDataItem = new GroupDataItem();
                groupDataItem.id = item.id;
                groupDataItem.adminName = item.adminName;
                groupDataItem.name = item.name;
                groupDataItem.aboutGroup = item.aboutGroup;
                groupDataItem.createdBy = item.adminName;
                groupDataItem.shortDescription = item.shortDescription;
                groupDataItem.image = item.image;
                groupDataItem.canPost = item.canPost;
                groupDataItem.isAdmin = item.isAdmin;
                groupDataItem.isPostShareAllowed = item.isPostShareAllowed;
                groupDataItem.isAdminChangeAllowed = item.isAdminChangeAllowed;
                groupDataItem.allowPostQuestion = item.allowPostQuestion;
                groupDataItem.totalUsers = item.totalUsers;
               AppLog.e("CountSaved", "count is " + item.totalUsers);
                groupDataItem.save();
            }

            if (data.size() == 0){
                ((GroupListActivityNew) getActivity()).goToPublic();
            }


        } else if (apiId == LeafManager.API_ALL_CONTACT_LIST) {

            new TaskForGruppieContacts(response).execute();

        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        mBinding.setSize(mAdapter.getItemCount());
       AppLog.e("GroupListFragment", "onFailure  ,, msg : " + msg);
        if(getActivity()!=null)
        {
            if (msg.contains("401:Unauthorized")) {
                Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
                logout();
            } else {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        }


    }


    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        try {
            Toast.makeText(getActivity(), getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
           AppLog.e("GroupListFrag", "onException : " + msg);
        }
       AppLog.e("GroupListFragment", "onException  ,, msg : " + msg);
        mBinding.setSize(mAdapter.getItemCount());
    }

    @Override
    public void onGroupSelect(GroupItem item) {
       AppLog.e("GIT", "sd is " + item.shortDescription);
       AppLog.e("ALLOW", "onGroupSelect: mAllowPost is " + item.canPost);
       AppLog.e("ALLOW", "onGroupSelect: mAllowPostShare is " + item.isPostShareAllowed);
       AppLog.e("ALLOW", "onGroupSelect: mAllowAdminChange is " + item.isAdminChangeAllowed);
       AppLog.e("ALLOW", "onGroupSelect: mAllowPostQuestion is " + item.allowPostQuestion);

        Intent intent = new Intent(getActivity(), GroupDashboardActivityNew.class);
        Bundle b = new Bundle();
        b.putParcelable(GroupDashboardActivityNew.EXTRA_GROUP_ITEM, item);
        boolean admin = item.isAdmin;
        boolean isPost = item.canPost;
        boolean allowPostShare = item.isPostShareAllowed;
        boolean allowAdminChange = item.isAdminChangeAllowed;
        boolean allowPostQuestion = item.allowPostQuestion;
        intent.putExtra("admin", admin);
        intent.putExtra("post", isPost);
        intent.putExtra("isPostShareAllowed", allowPostShare);
        intent.putExtra("isAdminChangeAllowed", allowAdminChange);
        intent.putExtra("allowPostQuestion", allowPostQuestion);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public void onReply() {
        // getActivity().findViewById(R.id.linear_post).setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContacts();
                }
        }

    }

    public void getContactsWithPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            getContacts();
            // start service code
        }
    }

    public void getContacts() {
        new TaskForContactsFirstTime().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        showLoadingDialog("Initializing...");

       /* if(isConnectionAvailable() && getActivity()!=null)
        {

            showLoadingBar(mBinding.progressBar,false);
            mManager.getAllContactsList(GroupListFragment.this*//*, 1*//*);
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISALLCONTACTSAVED, true);
        }
        else {
            showNoNetworkMsg();
        }*/

    }

    private class TaskForContactsFirstTime extends AsyncTask<Void, Void, Void> {

        DatabaseHandler databaseHandler;

        ArrayList<PhoneContactsItems> list = new ArrayList<>();
        //ArrayList<String> contactNames = new ArrayList<>();
 //       ArrayList<String> contactNumbers = new ArrayList<>();

//        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                databaseHandler = new DatabaseHandler(getActivity());
                databaseHandler.deleteAll();
            }
            catch (Exception ignored)
            {

            }
            /*dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Initializing...");
            dialog.setCancelable(false);
            dialog.show();*/
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
               AppLog.e("Called", "elseCalled" + " ContactsFromPhone");
                Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER};

                Cursor people = getActivity().getContentResolver().query(uri, projection, null, null, null);
                if(people!=null)
                {
                    int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    list = new ArrayList<>();
                    people.moveToFirst();
                    do {
                        PhoneContactsItems items = new PhoneContactsItems();

                        String name = people.getString(indexName);
                        String number = people.getString(indexNumber);

                        items.setName(name);
                       AppLog.e("RESS", "num is " + number);
                        String num = number.replaceAll(",", "").replaceAll("\\.", "").replaceAll(" ", "").replaceAll(",", "").replaceAll("-", "").replaceAll("\\(", "").replaceAll("\\)", "");
                       AppLog.e("RESS", "replaced num is " + num);

                        if (num.length() > 2) {
                            String desiredString = num.substring(0, 2);
                            if (desiredString.equals("00")) {
                                num = num.substring(2);
                                num = "+" + num;
                            }
                        }

                        if (num.length() > 1) {
                            String desiredString = num.substring(0, 1);
                            if (desiredString.equals("0")) {
                                num = num.substring(1);
                                num = "+" + LeafPreference.getInstance(getActivity()).getString(LeafPreference.CALLING_CODE) + num;
                            }
                        }

                        if (num.length() > 1) {
                            String desiredString = num.substring(0, 1);
                            if (!desiredString.equals("+")) {
                                num = "+" + LeafPreference.getInstance(getActivity()).getString(LeafPreference.CALLING_CODE) + num;
                            }
                        }

                        items.setPhone(num);
                        list.add(items);
                    } while (people.moveToNext());

                    databaseHandler.addContacts(list);
                }


            } catch (Exception ignored) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            /*if (dialog != null && dialog.isShowing())
                dialog.dismiss();*/
            if(isConnectionAvailable() && getActivity()!=null)
            {
                mManager.getAllContactsList(GroupListFragment.this/*, 1*/);
                LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISALLCONTACTSAVED, true);
            }
            else {
                showNoNetworkMsg();
            }
        }
    }

    private class TaskForGruppieContacts extends AsyncTask<Void, Void, Void> {

        BaseResponse response;

        public TaskForGruppieContacts(BaseResponse res) {
            this.response = res;
        }

        //        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           AppLog.e("GroupListFrag","Start....Start");
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                GruppieContactsModel.deleteAll();
                GruppieContactGroupIdModel.deleteAll();

//            hideLoadingBar();
                ContactListResponse res = (ContactListResponse) response;
                List<ContactListItem> list1 = res.getResults();
                DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
                int count = databaseHandler.getCount();
                databaseHandler.storeWritableDBObject();
                for (ContactListItem item:list1)
                {
                    if (count != 0) {
                        try {
                            String name = databaseHandler.getNameFromNumFirstTime2(item.getPhone().replaceAll(" ", ""));
//
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
                }
                databaseHandler.stopTransaction();


                ActiveAndroid.beginTransaction();
                for (int i = 0; i < list1.size(); i++) {
                    ContactListItem item = list1.get(i);

                    GruppieContactsModel model = new GruppieContactsModel();
                    model.contact_id = item.getId();
                    model.contact_name = item.getName();
                    model.contact_phone = item.getPhone();
                    model.contact_image = item.getImage();
                    model.contact_id = item.getId();
                    model.save();

                }
                ActiveAndroid.setTransactionSuccessful();
                String num = LeafPreference.getInstance(getActivity()).getString(LeafPreference.NUM);
                databaseHandler.updateYourNum(num);
            }catch (Exception ignored)
            {

            }
            finally {
                ActiveAndroid.endTransaction();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideLoadingDialog();

/*
            if(getActivity()!=null)
            {
                Intent intent=new Intent(getActivity(), BackgroundTaskForUpdate.class);
                getActivity().startService(intent);
            }
*/

           AppLog.e("GroupListFrag","DONE....DONE");

        }
    }


    public class ContactName implements Comparator<PhoneContactsItems> {
        @Override
        public int compare(PhoneContactsItems items, PhoneContactsItems t1) {
            return items.getName().compareTo(t1.getName());
        }
    }

}
