package school.campusconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.aabhasjindal.otptextview.ItemView;
import school.campusconnect.R;
import school.campusconnect.adapters.ArchiveTeamAdapter;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.ActivityEditTopicBinding;
import school.campusconnect.databinding.ItemTopicDetailsBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.syllabus.ChangeStatusPlanModel;
import school.campusconnect.datamodel.syllabus.EditTopicModelReq;
import school.campusconnect.datamodel.syllabus.SyllabusListModelRes;
import school.campusconnect.datamodel.syllabus.SyllabusPlanRequest;
import school.campusconnect.fragments.DatePickerFragment;
import school.campusconnect.network.LeafManager;

public class EditTopicActivity extends BaseActivity implements LeafManager.OnCommunicationListener{

    ActivityEditTopicBinding binding;
    SyllabusListModelRes.SyllabusData data;
    String teamID,subjectID,role;

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    ArrayList<EditTopicModelReq.TopicData> topicData = new ArrayList<>();
    LeafManager manager;
    public static final String TAG = "EditTopicActivity";
    TopicAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_edit_topic);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        inits();
        setTitle(data.getChapterName());
    }

    private void inits() {

        if ( getIntent().getExtras() != null) {
            data = (SyllabusListModelRes.SyllabusData) getIntent().getSerializableExtra("data");
            teamID = getIntent().getStringExtra("team_id");
            subjectID = getIntent().getStringExtra("subject_id");
            role = getIntent().getStringExtra("role");
            Log.e(TAG,"data "+new Gson().toJson(data));
        }

        manager = new LeafManager();

        Log.e(TAG,"data size "+data.getTopicData().size());

        for (int i = 0;i<data.getTopicData().size();i++)
        {
            EditTopicModelReq.TopicData data1 = new EditTopicModelReq.TopicData();
            data1.setTopicName(data.getTopicData().get(i).getTopicName());
            data1.setTopicId(data.getTopicData().get(i).getTopicId());
            topicData.add(data1);
        }
        adapter = new TopicAdapter(topicData);
        binding.rvTopic.setAdapter(adapter);

        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(TAG,"data "+new Gson().toJson(adapter.getList()));

                boolean isDone = true;

                for (int i =0;i<adapter.getList().size();i++)
                {
                    if (adapter.getList().get(i).getTopicName().isEmpty())
                    {
                        isDone = false;
                        break;
                    }
                }

                if (!isDone)
                {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_enter_topic_name),Toast.LENGTH_SHORT).show();
                }
                else
                {
                    EditTopicModelReq req = new EditTopicModelReq();
                    req.setTopicData(adapter.getList());

                    Log.e(TAG,"req "+new Gson().toJson(req));
                    showLoadingBar(binding.progressBar,false);
                    manager.EditSyllabus(EditTopicActivity.this,GroupDashboardActivityNew.groupId,teamID,subjectID,data.getChapterId(),req);
                }

            }
        });

    }

    public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder>{

        ArrayList<EditTopicModelReq.TopicData> topicData;
        Context context;

        public TopicAdapter(ArrayList<EditTopicModelReq.TopicData> topicData) {
            this.topicData = topicData;

        }

        public ArrayList<EditTopicModelReq.TopicData> getList() {
            return this.topicData;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            context = parent.getContext();
            View inflate= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit_topic,parent,false);
            return new ViewHolder(inflate);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            EditTopicModelReq.TopicData data = topicData.get(position);
            holder.etName.setText(data.getTopicName());

            holder.etName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    EditTopicModelReq.TopicData topicData1 = new EditTopicModelReq.TopicData();
                    topicData1.setTopicName(s.toString());
                    topicData1.setTopicId(data.getTopicId());
                    topicData.set(position, topicData1);
                }
            });

        }
        @Override
        public int getItemCount() {

            return topicData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            EditText etName;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
               etName = itemView.findViewById(R.id.etTopicName);
            }
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
        if (apiId == LeafManager.API_EDIT_SYLLABUS)
        {
            LeafPreference.getInstance(getApplicationContext()).setBoolean(LeafPreference.ISSYLLABUSUPDATED, true);
            Intent i = new Intent();
            setResult(RESULT_OK,i);
            finish();
        }

        super.onSuccess(apiId, response);
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        super.onFailure(apiId, msg);
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        super.onException(apiId, msg);
    }
}