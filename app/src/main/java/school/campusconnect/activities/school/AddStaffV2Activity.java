package school.campusconnect.activities.school;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.BaseActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.databinding.ActivityAddClassStudentV2Binding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.student.AddMultipleStaffReq;
import school.campusconnect.datamodel.student.AddMultipleStudentReq;
import school.campusconnect.network.LeafManager;
import school.campusconnect.views.SMBDialogUtils;

public class AddStaffV2Activity extends BaseActivity implements LeafManager.OnCommunicationListener{

    ActivityAddClassStudentV2Binding binding;
    private static String TAG = "AddStaffV2Activity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    private int currentCountry;
    private boolean onceClick=true;

    LeafManager manager;

    StaffAdapter staffAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_class_student_v2);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_add_staff));

        intis();

    }

    private void addStaff() {

        if (!binding.etName.getText().toString().isEmpty())
        {
            if (!binding.etPhone.getText().toString().isEmpty())
            {
                Log.e(TAG,"student" + staffAdapter.getList().size());

                if (staffAdapter.getList().size() > 0 && staffAdapter.getList().get(staffAdapter.getList().size()-1).getName().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_enter_name),Toast.LENGTH_SHORT).show();
                }
                else if (staffAdapter.getList().size() > 0 && staffAdapter.getList().get(staffAdapter.getList().size()-1).getPhone().length()<10)
                {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.msg_valid_phone),Toast.LENGTH_SHORT).show();
                }
                else
                {
                    staffAdapter.add("",1);
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.msg_valid_phone),Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_enter_name),Toast.LENGTH_SHORT).show();
        }

    }

    private void intis()
    {



        manager = new LeafManager();

        staffAdapter = new StaffAdapter();
        binding.rvStudent.setAdapter(staffAdapter);

        currentCountry = 1;
        String[] str = getResources().getStringArray(R.array.array_country);
        binding.etCountry.setText(str[0]);

        binding.imgAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addStaff();

            }
        });

        binding.etCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SMBDialogUtils.showSMBSingleChoiceDialog(AddStaffV2Activity.this, R.string.title_country, R.array.array_country, currentCountry - 1,
                        new SMBDailogClickListener(R.id.layout_country));
            }
        });


        binding.btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    ApiAddStaff();




            }
        });
    }

    private void ApiAddStaff() {

        String[] str = getResources().getStringArray(R.array.array_country_values);

        if (binding.etName.getText().toString().isEmpty())
        {
            if(isValueValid(binding.etName));
            return;
        }
        else if (binding.etPhone.getText().toString().isEmpty() ||  binding.etPhone.getText().toString().length()<10)
        {
            if(isValueValidPhone(binding.etPhone));
            return;
        }
        else
        {
            AddMultipleStaffReq req = new AddMultipleStaffReq();
            ArrayList<AddMultipleStaffReq.staffData> list = new ArrayList<>();

            if (staffAdapter.getList().size() > 0)
            {
                for (int i = 0 ;i<staffAdapter.getList().size();i++)
                {
                    if (staffAdapter.getList().get(i).getName().isEmpty())
                    {
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_enter_name),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (staffAdapter.getList().get(i).getPhone().isEmpty()||staffAdapter.getList().get(i).getPhone().length()<10)
                    {
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.msg_valid_phone),Toast.LENGTH_SHORT).show();
                        return;
                    }
                }


                AddMultipleStaffReq.staffData data = new AddMultipleStaffReq.staffData();
                data.setName(binding.etName.getText().toString());
                data.setPhone(binding.etPhone.getText().toString());
                data.setCountryCode(str[currentCountry - 1]);

                list.add(data);

                for (int i = 0 ;i<staffAdapter.getList().size();i++) {

                    AddMultipleStaffReq.staffData dataIn = new AddMultipleStaffReq.staffData();
                    dataIn.setName(staffAdapter.getList().get(i).getName());
                    dataIn.setPhone(staffAdapter.getList().get(i).getPhone());
                    dataIn.setCountryCode(str[staffAdapter.getList().get(i).getcCode() - 1]);

                    Log.e(TAG,"added Data from list"+new Gson().toJson(dataIn));

                    list.add(dataIn);
                }

                req.setStaffData(list);
            }
            else
            {
                AddMultipleStaffReq.staffData data = new AddMultipleStaffReq.staffData();
                data.setName(binding.etName.getText().toString());
                data.setPhone(binding.etPhone.getText().toString());
                data.setCountryCode(str[currentCountry - 1]);
                list.add(data);

                req.setStaffData(list);
            }

            Log.e(TAG,"new Student ReQ "+new Gson().toJson(req));
            if(onceClick)
            { onceClick=false;
            manager.addMultipleStaff(this, GroupDashboardActivityNew.groupId,req);
            }

        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();

        if (apiId == LeafManager.API_ADD_STAFF_MULTIPLE)
        {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_add_staff_successfully),Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }


    public class SMBDailogClickListener implements DialogInterface.OnClickListener {
        private int DIALOG_ID = -1;

        @Override
        public void onClick(DialogInterface dialog, int which) {

            ListView lw = ((AlertDialog) dialog).getListView();


            switch (DIALOG_ID) {

                case R.id.layout_country:
                    binding.etCountry.setText(lw.getAdapter().getItem(lw.getCheckedItemPosition()).toString());
                    currentCountry = lw.getCheckedItemPosition() + 1;
                    break;

            }
        }

        public SMBDailogClickListener(int id) {
            DIALOG_ID = id;
        }
    }

    public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.ViewHolder> {
        ArrayList<AddMultipleStudentReq.StudentData> list = new ArrayList<>();

        private Context mContext;

        public ArrayList<AddMultipleStudentReq.StudentData> getList() {
            return this.list;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_class_student_multiple, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            AddMultipleStudentReq.StudentData item = list.get(position);

            String[] str = getResources().getStringArray(R.array.array_country);
            holder.etCountry.setText(str[0]);

            holder.etName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {


                    AddMultipleStudentReq.StudentData item = new AddMultipleStudentReq.StudentData();
                    item.setName(s.toString());
                    item.setCountryCode(holder.etCountry.getText().toString());
                    item.setPhone(holder.etPhone.getText().toString());
                    item.setcCode(1);
                    list.set(position, item);
                }
            });


            holder.etCountry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SMBDialogUtils.showSMBSingleChoiceDialog(AddStaffV2Activity.this, R.string.title_country, R.array.array_country, item.getcCode() - 1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ListView lw = ((AlertDialog) dialogInterface).getListView();
                            holder.etCountry.setText(lw.getAdapter().getItem(lw.getCheckedItemPosition()).toString());

                            AddMultipleStudentReq.StudentData item = new AddMultipleStudentReq.StudentData();
                            item.setPhone(holder.etPhone.getText().toString());
                            item.setCountryCode(holder.etCountry.getText().toString());
                            item.setName(holder.etName.getText().toString());
                            item.setcCode(lw.getCheckedItemPosition() + 1);
                            list.set(position, item);
                        }
                    });
                }
            });

            holder.etPhone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {


                    AddMultipleStudentReq.StudentData item = new AddMultipleStudentReq.StudentData();
                    item.setPhone(s.toString());
                    item.setCountryCode(holder.etCountry.getText().toString());
                    item.setName(holder.etName.getText().toString());
                    item.setcCode(1);

                    list.set(position, item);
                }
            });

            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(position);
                    notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void add(String item,int i) {
            AddMultipleStudentReq.StudentData itemData = new AddMultipleStudentReq.StudentData();
            itemData.setName(item);
            itemData.setcCode(i);
            list.add(itemData);
            notifyItemChanged(list.size()-1);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.etName)
            EditText etName;

            @Bind(R.id.etCountry)
            EditText etCountry;

            @Bind(R.id.etPhone)
            EditText etPhone;

            @Bind(R.id.imgDelete)
            ImageView imgDelete;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

            }
        }
    }

}
