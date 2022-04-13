package school.campusconnect.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.CoursePostResponse;
import school.campusconnect.datamodel.booths.BoothMemberReq;
import school.campusconnect.datamodel.committee.committeeResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;

public class EditCourseActivity extends BaseActivity {
    private static final String TAG = "EditCourseActivity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.rvSubjects)
    public RecyclerView rvSubjects;

    @Bind(R.id.etName)
    public EditText etName;

    @Bind(R.id.etCourseName)
    public EditText etCourseName;

    @Bind(R.id.etDesc)
    public EditText etDesc;


    @Bind(R.id.btnAdd)
    public Button btnAdd;

    @Bind(R.id.imgAdd)
    public ImageView imgAdd;



    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    LeafManager leafManager;

    String group_id, team_id, category;
    CoursePostResponse.CoursePostData data;
    SubjectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);

        init();
        setTitle(data.courseName);
    }
    private void init() {
        leafManager = new LeafManager();
        group_id = getIntent().getStringExtra("group_id");
        Log.e(TAG,"category "+category);
        data = new Gson().fromJson(getIntent().getStringExtra("data"), CoursePostResponse.CoursePostData.class);
        Log.e(TAG,"committee Data: "+new Gson().toJson(data));
        adapter = new SubjectAdapter(data.subjectList);
        rvSubjects.setAdapter(adapter);

        etCourseName.setText(data.courseName);
        etDesc.setText(data.description);

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etName.getText().toString().trim())) {
                    Toast.makeText(EditCourseActivity.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                } else {
                    hide_keyboard(view);
                   // String str = etName.getText().toString() + ",IN," + etPhone.getText().toString();
                    adapter.add(new CoursePostResponse.SubjectData(etName.getText().toString()));

                    etName.setText("");
                    etName.requestFocus();
                  //  etPhone.setText("");
                }
            }
        });

    }

    private long lastClickTime = 0;

    @OnClick({R.id.btnAdd})
    public void onClick(View view) {
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime - lastClickTime > 1000) {
            lastClickTime = currentTime;
        } else {
            return;
        }
        Log.e(TAG, "Tap : ");
        switch (view.getId()) {
            case R.id.btnAdd:
                hide_keyboard(view);
                CoursePostResponse.SubjectData  str = null;
                if (!TextUtils.isEmpty(etName.getText().toString().trim()))
                {
                    str = new CoursePostResponse.SubjectData(etName.getText().toString());
                }
                else if (TextUtils.isEmpty(etName.getText().toString().trim()) )
                {
                }
                else
                {
                    if (TextUtils.isEmpty(etName.getText().toString().trim())) {
                        Toast.makeText(EditCourseActivity.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if(str==null || adapter.getList().size()>0)
                {
                    CoursePostResponse.CoursePostData request = new CoursePostResponse.CoursePostData();
                    request.courseName = etCourseName.getText().toString();
                    request.courseId  = data.courseId;
                    request.description = etDesc.getText().toString();

                    request.subjectList = adapter.getList();
                    request.individualSubjectCutOff = true;
                    request.totalCutOff = true;
                    request.totalCutOffPercentage = "";


                    AppLog.e(TAG , "edit course request  : "+new Gson().toJson(request));

                    leafManager.editCourse(this ,group_id , data.courseId ,request);

                  /*  if (etPhone.getText().toString().length() < 10) {
                        Toast.makeText(EditCourseActivity.this, "Please Enter Valid Phone Number", Toast.LENGTH_SHORT).show();
                        return;
                    }*/


                    /*BoothMemberReq req = new BoothMemberReq();
                    req.user = adapter.getList();

                    if(!TextUtils.isEmpty(str)){
                        req.user.add(str);
                    }


                    if (data != null)
                    {
                        req.dafaultCommittee = data.getDefaultCommittee();
                        req.committeeId = committeeData.getCommitteeId();
                    }

                    progressBar.setVisibility(View.VISIBLE);
                    leafManager.addBoothsMember(this, group_id, team_id, category, req);*/
                }else {
                    Toast.makeText(EditCourseActivity.this, "Please Add at least one contact", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_EDIT_COURSE:
                Toast.makeText(this, "Course Updated successfully", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        super.onFailure(apiId, msg);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

        if (msg.contains("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onException(int apiId, String msg) {
        super.onException(apiId, msg);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }


    public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {
        private Context mContext;
        ArrayList<CoursePostResponse.SubjectData> list = new ArrayList<>();

        SubjectAdapter(ArrayList<CoursePostResponse.SubjectData> list ) {

            this.list = list;

            if(this.list == null)
               this.list = new ArrayList<>();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_subject, parent, false);
            return new ViewHolder(view);
        }

        public ArrayList<CoursePostResponse.SubjectData> getList() {
            return this.list;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
            CoursePostResponse.SubjectData item = list.get(pos);
            holder.etName.setText(item.subjectName);
         //   holder.etPhone.setText(item[2]);

            holder.etName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    item.subjectName = s.toString();
                    list.set(pos, item);
                }
            });

         /*   holder.etPhone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    item[2] = s.toString();
                    list.set(pos, item[0] + "," + item[1] + "," + item[2]);
                }
            });*/
        }

        @Override
        public int getItemCount() {
            return list!=null ? list.size():0;
        }

        public void add(CoursePostResponse.SubjectData item) {
            list.add(item);
            notifyDataSetChanged();
        }

        public void addAll(ArrayList<CoursePostResponse.SubjectData> mobileList) {
            list.addAll(mobileList);
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.etName)
            EditText etName;

            @Bind(R.id.imgDelete)
            ImageView imgDelete;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                imgDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list.remove(getAdapterPosition());
                        notifyDataSetChanged();
                    }
                });

            }
        }
    }
}
