package school.campusconnect.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import school.campusconnect.datamodel.LeadItem;
import school.campusconnect.datamodel.LeadResponse;
import school.campusconnect.utils.AppLog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import school.campusconnect.R;
import school.campusconnect.activities.AddFriendActivity;
import school.campusconnect.activities.BaseActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.InviteFriendActivity;
import school.campusconnect.adapters.MyContactListAdapter;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.LayoutListWithoutRefreshBinding;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.PhoneContactsItems;
import school.campusconnect.datamodel.gruppiecontacts.GruppieContactGroupIdModel;
import school.campusconnect.datamodel.gruppiecontacts.GruppieContactsModel;
import school.campusconnect.datamodel.gruppiecontacts.InviteResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;

public class Fragment_PhoneContacts extends BaseFragment implements MyContactListAdapter.OnItemClickListener, LeafManager.OnAddUpdateListener<AddPostValidationError>,LeafManager.OnCommunicationListener {


    String TAG = "Fragment_PhoneContacts";
    private LayoutListWithoutRefreshBinding mBinding;
    public MyContactListAdapter mAdapter;

    ArrayList<String> contactNames = new ArrayList<>();
    ArrayList<String> contactNumbers = new ArrayList<>();
    ArrayList<PhoneContactsItems> list = new ArrayList<>();
    ArrayList<String> selected_names = new ArrayList<>();
    private ArrayList<String> selected_ids = new ArrayList<>();
    public final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 9;
    String groupId;
    DatabaseHandler databaseHandler;
    private String inviteData;
    private boolean isFromTeam;
    private String teamId;
    private ArrayList<String> teamList;

    public Fragment_PhoneContacts() {

    }

    public static Fragment_PhoneContacts newInstance(Bundle b) {
        Fragment_PhoneContacts fragment = new Fragment_PhoneContacts();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_list_without_refresh, container, false);

        initObjects();

        if(isFromTeam)
        {
            getTeamMember();
        }
        else
        {
            getData();
        }

        return mBinding.getRoot();
    }

    private void getTeamMember() {
        if(isConnectionAvailable())
        {
            LeafManager manager=new LeafManager();
            showLoadingBar(mBinding.progressBar2);
            manager.getTeamMember(this, groupId+"", teamId+"",false);
        }
        else
        {
            showNoNetworkMsg();
        }

    }

    private void initObjects() {
        mBinding.setSize(1);
        mBinding.setMessage(R.string.msg_no_users);
        databaseHandler = new DatabaseHandler(getActivity());

        final LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView.setLayoutManager(manager);

        mAdapter=new MyContactListAdapter(this,list);
        mBinding.recyclerView.setAdapter(mAdapter);

        Bundle bundle=getArguments();

        groupId = bundle.getString("id", "");
        isFromTeam=bundle.containsKey("from_team");

        if(isFromTeam)
        {
            teamId=bundle.getString("team_id");
        }
        AppLog.e(TAG, "groupId " + groupId);
        AppLog.e(TAG, "isFromTeam " + isFromTeam);
        AppLog.e(TAG, "teamId " + teamId);
    }


    @Override
    public void onResume() {
        super.onResume();
        AppLog.e(TAG, "onResume called");
        AppLog.e("INVITEEE", "OnResume Called : " + LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISUSERDELETED1));
        if (LeafPreference.getInstance(getActivity()).getBoolean(LeafPreference.ISUSERDELETED1)) {

            if(isFromTeam)
            {
                getTeamMember();
            }
            else
            {
                getData();
            }

            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISUSERDELETED1, false);
        } else {
            AppLog.e("INVITEEE", "else called");
        }
    }

    public void getData() {
        getContactsWithPermission();
    }

    public void getContactsWithPermission() {
        if(getActivity()==null)
            return;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions( new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            getContacts();
        }
    }


    public void getContacts() {
        if (databaseHandler.getCount() != 0) {
            new TaskForGetContacts().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new TaskForContactsFirstTime().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private class TaskForContactsFirstTime extends AsyncTask<Void, Void, Void> {

        DatabaseHandler databaseHandler;

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            databaseHandler = new DatabaseHandler(getActivity());
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Syncing your contacts to Gruppie please wait...");
            dialog.setCancelable(false);

            if(!GroupDashboardActivityNew.groupCategory.equalsIgnoreCase(Constants.CATEGORY_SCHOOL))
                dialog.show();
            else
                showLoadingBar(mBinding.progressBar2);

            list.clear();
            contactNames.clear();
            contactNumbers.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                AppLog.e("Called", "elseCalled" + " ContactsFromPhone");
                Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER};

                Cursor people = getActivity().getContentResolver().query(uri, projection, null, null, null);
                int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                people.moveToFirst();
                do {
                    String name = people.getString(indexName);
                    String number = people.getString(indexNumber);
                    contactNames.add(name);
                    contactNumbers.add(number);
                } while (people.moveToNext());

                for (int i = 0; i < contactNames.size(); i++) {
                    PhoneContactsItems items = new PhoneContactsItems();
                    items.setName(contactNames.get(i));

                    String num = contactNumbers.get(i).replaceAll(",", "").replaceAll("\\.", "").replaceAll(" ", "").replaceAll("-", "").replaceAll("\\(", "").replaceAll("\\)", "");

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
                    databaseHandler.addContacts(items.getName(), items.getPhone());
                }

                list.addAll(hasDuplicates(list));

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
            hideLoadingBar();

            mAdapter.notifyDataSetChanged();
        }
    }


    // TASK TO GET CONTACTS AND SET IN LIST.... LIST CLEARED BEFORE EXECUTING TASK....
    private class TaskForGetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list.clear();
            showLoadingBar(mBinding.progressBar2);
        }

        @Override
        protected Void doInBackground(Void... params) {

            if(isFromTeam)
            {
                list.addAll(hasDuplicates(databaseHandler.getAllContacts()));
            }
            else
            {
                list.addAll(hasDuplicates(databaseHandler.getContacts()));
            }

            AppLog.e(TAG,"List : "+new Gson().toJson(list));
            AppLog.e(TAG,"List size: "+list.size());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            hideLoadingBar();

            mAdapter.notifyDataSetChanged();
        }
    }


    public void refreshAdapterPhone(ArrayList<PhoneContactsItems> arrayList) {
        list.clear();
        list.addAll(hasDuplicates(arrayList));
        for (int i = 0; i < list.size(); i++) {
            if (isIdSelected(list.get(i).getPhone())) {
                list.get(i).isSelected = true;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContacts();
                } else {
                }
        }

    }

    @Override
    public void onInvite(MyContactListAdapter.MyContact myContact) {
        Intent intent = new Intent(getActivity(), InviteFriendActivity.class);
        intent.putExtra("id", groupId);
        intent.putExtra("name", myContact.getName());
        intent.putExtra("phone", myContact.getNumber());
        startActivity(intent);
    }

    @Override
    public void friendSelected(boolean isChecked, PhoneContactsItems item, int position) {
        if (isChecked) {
            AppLog.e("FragPhoneContacts", "isCheck True ,  selected size :  " + selected_ids.size());
            if (selected_ids.size() >= 100) {
                item.isSelected = false;
                list.get(position).isSelected = false;
                mAdapter.updateItem(position, item);
                mAdapter.notifyItemChanged(position);
                Toast.makeText(getActivity(), "Cannot invite more than 100 friends", Toast.LENGTH_SHORT).show();
                return;
            }

            item.isSelected = true;
            addSelectedId(item.getPhone(), item);
            list.get(position).isSelected = true;
            mAdapter.updateItem(position, item);
        } else {
            removeId(item.getPhone());
            list.get(position).isSelected = false;
            item.isSelected = false;
            mAdapter.updateItem(position, item);
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
        hideLoadingDialog();

        if(apiId==LeafManager.API_ID_LEAD_LIST)
        {
            LeadResponse res = (LeadResponse) response;

            List<LeadItem> result = res.getResults();

            teamList=new ArrayList<>();

            for (int i=0;i<result.size();i++)
            {
                teamList.add(result.get(i).phone);
            }
        }
        else
        {
            try {
                String body = "I have added you to " + GroupDashboardActivityNew.group_name + ", to login plz download the " + getResources().getString(R.string.app_name) + " app from\n" +
                        "\n" +
                        "gruppie.in/android/"+getResources().getString(R.string.app_name).replace(" ","")+"\n" +
                        "gruppie.in/ios/"+getResources().getString(R.string.app_name).replace(" ","")+"\n" +
                        "Plz add your friends to this group";
                try {

                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("address", inviteData);
                    smsIntent.putExtra("sms_body", body);
                    startActivity(smsIntent);
                } catch (Exception e) {
                    AppLog.e("MULTI_ADD", "error is " + e.toString());
                }

                if (((AddFriendActivity) getActivity()).edtSearch != null)
                    ((AddFriendActivity) getActivity()).edtSearch.setText("");

                InviteResponse res = (InviteResponse) response;

                if (res.data != null) {
                    for (int i = 0; i < res.data.size(); i++) {
                        AppLog.e("INVITES", "name is " + res.data.get(i).name);
                        GruppieContactsModel model = new GruppieContactsModel();
                        model.contact_id = res.data.get(i).userId;
                        model.contact_name = res.data.get(i).name;
                        model.contact_phone = res.data.get(i).phone;
                        model.save();

                        GruppieContactGroupIdModel idModel = new GruppieContactGroupIdModel();
                        idModel.group_id = res.data.get(i).groupId + "";
                        idModel.user_id = res.data.get(i).userId + "";
                        idModel.save();

                    }
                    if(!isFromTeam)
                    {
                        DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
                        for (int i = 0; i < selected_ids.size(); i++) {
                            databaseHandler.updateContact(selected_ids.get(i));
                        }

                    }
                }
                LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISTEAMUPDATED,true);
                LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISUSERDELETED1, true);
                selected_ids.clear();
                selected_names.clear();
                ((AddFriendActivity) getActivity()).setAddCountAct(0);

            } catch (Exception e) {
            }
        }
        getData();
    }

    @Override
    public void onFailure(int apiId, String msg) {

    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<AddPostValidationError> error) {
        hideLoadingBar();
        try {
            if (error.status.contains("401:Unauthorized") || error.status.contains("401")) {
                Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
                logout();
            } else if (error.status.contains("404")) {
                //TODO Synchronize contact

                ((BaseActivity) getActivity()).getContactsWithPermission();
                Fragment_GruppieContacts.synchFromAddFrend = true;

            } else {
                Toast.makeText(getActivity(), error.title, Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ignored) {
        }

    }


    @Override
    public void onException(int apiId, String error) {
        hideLoadingBar();
        try {
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            AppLog.e("Multiple Invite", "error is  " + e.toString());
        }
        AppLog.e("Multiple Invite", "error is  " + error);
    }

    private void removeId(String num) {
        int position = selected_ids.indexOf(num);
        AppLog.e("MULTI_ADD", "removeId: id is " + num + " contains: " + selected_ids.contains(num) + " position is " + position);
        if (position >= 0) {
            selected_ids.remove(position);
            selected_names.remove(position);
        }
        ((AddFriendActivity) getActivity()).setAddCountAct(selected_ids.size());
    }

    private void addSelectedId(String num, PhoneContactsItems items) {
        if (!selected_ids.contains(num)) {
            AppLog.e("MULTI_ADD", "addSelectedId: id is " + num);
            selected_ids.add(num);

            String sendNum = items.getPhone().replaceAll(" ", "");

            if (sendNum.length() > 3) {
                String desiredString1 = sendNum.substring(0, 3);
                if (desiredString1.equals("+91"))
                    sendNum = sendNum.substring(3);
            }

            String countryCode = LeafPreference.getInstance(getActivity()).getString(LeafPreference.COUNTRY_CODE);
            String callingCode = LeafPreference.getInstance(getActivity()).getString(LeafPreference.CALLING_CODE);
            String name = items.getName();
            name = name.replaceAll(",", "");
            String details = name + "," + /*callingCode + "," +*/ countryCode + "," + sendNum;
            selected_names.add(details);
            if (selected_ids.size() > 100) {
                Toast.makeText(getActivity(), "Cannot invite more than 100 friends", Toast.LENGTH_SHORT).show();
            }
        } else {
            AppLog.e("MULTI_ADD", "addSelectedId contains: id is " + num);
        }
        ((AddFriendActivity) getActivity()).setAddCountAct(selected_ids.size());
    }

    private boolean isIdSelected(String num) {
        AppLog.e("MULTI_ADD", "isIdSelected: id is " + num + " contains: " + selected_ids.contains(num));
        return selected_ids.contains(num);
    }

    private String getSelectedIdsForShare() {
        String ids = "";
        for (int i = 0; i < selected_ids.size(); i++) {
            if (i != (selected_ids.size() - 1))
                ids = ids + selected_ids.get(i) + ";";
            else
                ids = ids + selected_ids.get(i);
        }
        return ids;
    }

    private boolean canAdd() {
        return selected_ids.size() <= 100;
    }

    public int getSelectedCount() {
        return selected_ids.size();
    }

    public void addMultipleInvites(boolean isFromTeam, String teamId) {
        if (selected_ids.size() != 0) {

            if (canAdd()) {

                String data = "";

                if (getSelectedCount() == 1) {
                    data = selected_ids.get(0);
                } else {
                    data = getSelectedIdsForShare();
                }
                LeafManager mManager = new LeafManager();
                showLoadingBar(mBinding.progressBar2);
                mManager.inviteMultipleFriends(this, groupId + "", selected_names, isFromTeam, teamId);
                cleverTapInviteFriend(selected_names);
                AppLog.e("MULTI_ADD", "data is " + data);
                inviteData = data;

            } else {
                Toast.makeText(getActivity(), "Cannot invite more than 20 friends", Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(getActivity(), "Please select any friend first", Toast.LENGTH_SHORT).show();
    }

    private void cleverTapInviteFriend(ArrayList<String> selected_names) {
        try {
            CleverTapAPI cleverTap = CleverTapAPI.getInstance(getActivity());
            AppLog.e("InviteFriend", "Success to found cleverTap objects=>");
            for (String name : selected_names) {
                HashMap<String, Object> addFriendAction = new HashMap<String, Object>();
                addFriendAction.put("id", groupId);
                addFriendAction.put("group_name", GroupDashboardActivityNew.group_name);
                addFriendAction.put("phone", name);
                cleverTap.event.push("Invite Friend", addFriendAction);
            }

            AppLog.e("InviteFriend", "Success");

        } catch (CleverTapMetaDataNotFoundException e) {
            AppLog.e("InviteFriend", "CleverTapMetaDataNotFoundException=>" + e.toString());
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            AppLog.e("InviteFriend", "CleverTapPermissionsNotSatisfied=>" + e.toString());
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }

    }

    public ArrayList<PhoneContactsItems> hasDuplicates(ArrayList<PhoneContactsItems> p_cars) {
        final List<String> usedNames = new ArrayList<String>();
        Iterator<PhoneContactsItems> it = p_cars.iterator();
        while (it.hasNext()) {
            PhoneContactsItems car = it.next();
            String name = car.getPhone().replaceAll(",", "").replaceAll("\\.", "").replaceAll(" ", "").replaceAll("-", "").replaceAll("\\(", "").replaceAll("\\)", "");

            try {
                if (name.length() > 3) {
                    String desiredString1 = name.substring(0, 3);
                    if (desiredString1.equals("+91"))
                        name = name.substring(3);
                }
                if (usedNames.contains(name)) {
                    it.remove();

                } else {
                    usedNames.add(name);
                }

                if(isFromTeam)
                {
                    AppLog.e(TAG,"Phone number :"+car.getPhone());
                    if(teamList.contains(car.getPhone()))
                        it.remove();
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        return p_cars;
    }

}
