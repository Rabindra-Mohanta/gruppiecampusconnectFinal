package school.campusconnect.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.PhoneContactsItems;
import school.campusconnect.datamodel.issue.IssueListResponse;
import school.campusconnect.datamodel.staff.StaffResponse;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.fragments.IssueFragment;
import school.campusconnect.utils.AppLog;

public class SelectContactActivity extends BaseActivity {

    private static final String TAG = "Select Contact";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;

    @Bind(R.id.edtSearch)
    public EditText edtSearch;

    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;
    ArrayList<PhoneContactsItems> list = new ArrayList<>();
    ArrayList<PhoneContactsItems> filterList = new ArrayList<>();
    private DatabaseHandler databaseHandler;
    private ArrayList<String> mobileList;
    private StaffAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_select_contact));

        inits();

    }

    private void inits() {

        rvClass.setLayoutManager(new LinearLayoutManager(this));
        mobileList = getIntent().getStringArrayListExtra("mobileList");

        databaseHandler = new DatabaseHandler(this);

        adapter=new StaffAdapter();
        rvClass.setAdapter(adapter);

        new TaskForGetContacts().execute();

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
                    adapter.add(list);
                }
            }
        });
    }

    private void searchData(String text) {

        filterList = new ArrayList<>();

        for(PhoneContactsItems item : list){

            if (item.getName().toLowerCase().contains(text.toLowerCase())){
                filterList.add(item);
            }
        }

        adapter.add(filterList);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_contact, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSelect:
                select();
                return true;

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

    // TASK TO GET CONTACTS AND SET IN LIST.... LIST CLEARED BEFORE EXECUTING TASK....
    private class TaskForGetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list.clear();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            list.addAll(hasDuplicates(databaseHandler.getAllContacts()));

            AppLog.e(TAG, "List : " + new Gson().toJson(list));
            AppLog.e(TAG, "List size: " + list.size());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressBar.setVisibility(View.GONE);
            adapter.add(list);

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
                AppLog.e(TAG, "Phone number :" + car.getPhone());
                if (mobileList != null && mobileList.contains(car.getPhone()))
                    it.remove();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return p_cars;
    }
    public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.ViewHolder> {
        List<PhoneContactsItems> list;
        private Context mContext;


        @Override
        public StaffAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff_select, parent, false);
            return new StaffAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final StaffAdapter.ViewHolder holder, final int position) {
            final PhoneContactsItems item = list.get(position);
            holder.chkName.setText(item.getName()+"\n"+item.getPhone());
            if (item.isSelected) {
                holder.chkName.setChecked(true);
            } else {
                holder.chkName.setChecked(false);
            }
            holder.chkName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.isSelected = holder.chkName.isChecked();
                    notifyItemChanged(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    txtEmpty.setText("No Contacts found.");
                } else {
                    txtEmpty.setText("");
                }

                return list.size();
            } else {
                txtEmpty.setText("No Contacts found.");
                return 0;
            }

        }

        public ArrayList<String> getSelectedList() {
            ArrayList<String> selected = new ArrayList<>();
            if (list == null) {
                return selected;
            }
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isSelected)
                    selected.add(list.get(i).getName()+ ",IN," + list.get(i).getPhone().replace(" ", "").replace("+91", ""));
            }
            return selected;
        }

        public void add(ArrayList<PhoneContactsItems> list) {
            this.list = list;
            notifyDataSetChanged();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.chkName)
            CheckBox chkName;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

            }
        }
    }


    private void select() {
        if(adapter!=null){
            ArrayList<String> selected = adapter.getSelectedList();
            if(selected!=null && selected.size()>0){
                Intent intent=new Intent();
                intent.putExtra("mobileList",selected);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }else {
                Toast.makeText(this,"Please select contact",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
