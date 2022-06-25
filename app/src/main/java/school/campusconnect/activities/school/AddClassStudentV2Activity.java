package school.campusconnect.activities.school;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

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

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.AddClassStudentActivity;
import school.campusconnect.activities.AddSyllabusActivity;
import school.campusconnect.activities.BaseActivity;
import school.campusconnect.databinding.ActivityAddClassStudentV2Binding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.student.AddMultipleStudentReq;
import school.campusconnect.datamodel.syllabus.SyllabusModelReq;
import school.campusconnect.fragments.DatePickerFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.views.SMBDialogUtils;

public class AddClassStudentV2Activity extends BaseActivity implements LeafManager.OnCommunicationListener {

    ActivityAddClassStudentV2Binding binding;
    private static String TAG = "AddClassStudentV2Activity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    private int currentCountry;

    LeafManager manager;
    String groupId,teamId;
    StudentAdapter studentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_class_student_v2);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.title_add_student));

        intis();

    }

    private void addStudent() {

        if (!binding.etName.getText().toString().isEmpty())
        {
            if (!binding.etPhone.getText().toString().isEmpty())
            {
                Log.e(TAG,"student" + studentAdapter.getList().size());

                if (studentAdapter.getList().size() > 0 && studentAdapter.getList().get(studentAdapter.getList().size()-1).getName().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_enter_name),Toast.LENGTH_SHORT).show();
                }
                else if (studentAdapter.getList().size() > 0 && studentAdapter.getList().get(studentAdapter.getList().size()-1).getPhone().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_enter_phone),Toast.LENGTH_SHORT).show();
                }
                else
                {
                    studentAdapter.add("",1);
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_enter_phone),Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_enter_name),Toast.LENGTH_SHORT).show();
        }

    }

    private void intis()
    {


        groupId = getIntent().getStringExtra("group_id");
        teamId = getIntent().getStringExtra("team_id");

        manager = new LeafManager();

        studentAdapter = new StudentAdapter();
        binding.rvStudent.setAdapter(studentAdapter);

        currentCountry = 1;
        String[] str = getResources().getStringArray(R.array.array_country);
        binding.etCountry.setText(str[0]);

        binding.imgAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addStudent();

            }
        });

        binding.etCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SMBDialogUtils.showSMBSingleChoiceDialog(AddClassStudentV2Activity.this, R.string.title_country, R.array.array_country, currentCountry - 1,
                        new SMBDailogClickListener(R.id.layout_country));
            }
        });


        binding.btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiAddStudent();
            }
        });
    }

    private void ApiAddStudent() {

        String[] str = getResources().getStringArray(R.array.array_country_values);

        if (binding.etName.getText().toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_enter_name),Toast.LENGTH_SHORT).show();
            return;
        }
        else if (binding.etPhone.getText().toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_enter_phone),Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            AddMultipleStudentReq req = new AddMultipleStudentReq();
            ArrayList<AddMultipleStudentReq.StudentData> list = new ArrayList<>();

            if (studentAdapter.getList().size() > 0)
            {
                for (int i = 0 ;i<studentAdapter.getList().size();i++)
                {
                    if (studentAdapter.getList().get(i).getName().isEmpty())
                    {
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_enter_name),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (studentAdapter.getList().get(i).getPhone().isEmpty())
                    {
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_enter_phone),Toast.LENGTH_SHORT).show();
                        return;
                    }
                }


                AddMultipleStudentReq.StudentData data = new AddMultipleStudentReq.StudentData();
                data.setName(binding.etName.getText().toString());
                data.setPhone(binding.etPhone.getText().toString());
                data.setCountryCode(str[currentCountry - 1]);

                list.add(data);

                for (int i = 0 ;i<studentAdapter.getList().size();i++) {

                    AddMultipleStudentReq.StudentData dataIn = new AddMultipleStudentReq.StudentData();
                    dataIn.setName(studentAdapter.getList().get(i).getName());
                    dataIn.setPhone(studentAdapter.getList().get(i).getPhone());
                    dataIn.setCountryCode(str[studentAdapter.getList().get(i).getcCode() - 1]);

                    Log.e(TAG,"added Data from list"+new Gson().toJson(dataIn));

                    list.add(dataIn);
                }

                req.setStudentData(list);
            }
            else
            {
                AddMultipleStudentReq.StudentData data = new AddMultipleStudentReq.StudentData();
                data.setName(binding.etName.getText().toString());
                data.setPhone(binding.etPhone.getText().toString());
                data.setCountryCode(str[currentCountry - 1]);
                list.add(data);

                req.setStudentData(list);
            }

            Log.e(TAG,"new Student ReQ "+new Gson().toJson(req));

            manager.addMultipleStudent(this,groupId,teamId,req);


        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();

        if (apiId == LeafManager.API_ADD_STUDENT_MULTIPLE)
        {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_add_student_sucess),Toast.LENGTH_SHORT).show();
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

    public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {
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
        public void onBindViewHolder(final ViewHolder holder, final int position) {

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
                    SMBDialogUtils.showSMBSingleChoiceDialog(AddClassStudentV2Activity.this, R.string.title_country, R.array.array_country, item.getcCode() - 1, new DialogInterface.OnClickListener() {
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