package school.campusconnect.activities;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.ActivityAddSyllabusBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.syllabus.SyllabusListModelRes;
import school.campusconnect.datamodel.syllabus.SyllabusModelReq;
import school.campusconnect.datamodel.syllabus.SyllabusTBL;
import school.campusconnect.network.LeafManager;

public class AddSyllabusActivity extends BaseActivity implements View.OnClickListener, LeafManager.OnCommunicationListener {
ActivityAddSyllabusBinding binding;
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    public static final String TAG = "AddSyllabusActivity";
    TopicAdapter topicAdapter;
    LeafManager manager;
    String teamId,subjectId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_syllabus);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_add_syllabus));

        inits();
        setListner();


    }

    private void inits() {
        topicAdapter = new TopicAdapter();
        binding.rvTopic.setAdapter(topicAdapter);

        teamId = getIntent().getStringExtra("team_id");
        subjectId = getIntent().getStringExtra("subject_id");

        Log.e(TAG,"team ID"+teamId+"\nsubject Id"+subjectId);
        manager = new LeafManager();
    }
    private void setListner()
    {
        binding.imgAdd.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.imgAdd:
                addTopic();
                break;

            case R.id.btnAdd:
                hide_keyboard();
                if (isValid())
                {
                    SyllabusModelReq req = new SyllabusModelReq();

                    ArrayList<SyllabusModelReq.SyllabusModelData> syllabusModelData = new ArrayList<>();

                    SyllabusModelReq.SyllabusModelData data = new SyllabusModelReq.SyllabusModelData();
                    data.setChapterName(binding.etcName.getText().toString());
                    ArrayList<SyllabusModelReq.TopicModelData> arrayList = new ArrayList<>();

                    if (topicAdapter.getList().size() > 0)
                    {
                        SyllabusModelReq.TopicModelData data2 = new SyllabusModelReq.TopicModelData();
                        data2.setTopicName(binding.etTopicName.getText().toString());
                        arrayList.add(data2);

                        for (int i =0;i<topicAdapter.getList().size();i++)
                        {
                            if (topicAdapter.getList().get(i).isEmpty())
                            {
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_add_topic_name),Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        for (int i =0;i<topicAdapter.getList().size();i++)
                        {
                            SyllabusModelReq.TopicModelData data1 = new SyllabusModelReq.TopicModelData();
                            data1.setTopicName(topicAdapter.getList().get(i));
                            arrayList.add(data1);
                        }
                    }
                    else
                    {
                        SyllabusModelReq.TopicModelData modelData = new SyllabusModelReq.TopicModelData();
                        modelData.setTopicName(binding.etTopicName.getText().toString());
                        arrayList.add(modelData);
                    }

                    data.setTopicsList(arrayList);
                    syllabusModelData.add(data);

                    List<SyllabusTBL> list = SyllabusTBL.getSyllabus(teamId,subjectId);

                    if (list.size() > 0)
                    {
                        for (int i = 0;i<list.size();i++)
                        {
                            SyllabusModelReq.SyllabusModelData modelData = new SyllabusModelReq.SyllabusModelData();
                            modelData.setChapterName(list.get(i).chapterName);
                            modelData.setTopicsList(new Gson().fromJson(list.get(i).topicsList, new TypeToken<ArrayList<SyllabusListModelRes.TopicData>>() {}.getType()));
                            syllabusModelData.add(modelData);
                        }
                    }
                    req.setSyllabusModelData(syllabusModelData);

                    Log.e(TAG,"send Request"+new Gson().toJson(req));

                    showLoadingBar(binding.progressBar);
                    manager.addSyllabus(this,GroupDashboardActivityNew.groupId,teamId,subjectId,req);
                }
                break;
        }

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);

        switch (apiId) {
            case LeafManager.API_ADD_SYLLABUS:
                Toast.makeText(this, getResources().getString(R.string.toast_syllabus_add_successfully), Toast.LENGTH_SHORT).show();
                LeafPreference.getInstance(getApplicationContext()).setBoolean(LeafPreference.ISSYLLABUSUPDATED, true);
                finish();
                break;
        }
    }

    @Override
    public void onException(int apiId, String msg) {
        super.onException(apiId, msg);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        hideLoadingBar();
    }

    @Override
    public void onFailure(int apiId, String msg) {
        super.onFailure(apiId, msg);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        hideLoadingBar();
    }

    private boolean isValid()
    {
        if (binding.etcName.getText().toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_enter_chapter_name),Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.etTopicName.getText().toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_add_topic_name),Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void addTopic() {

        if (!binding.etTopicName.getText().toString().isEmpty())
        {
            if (topicAdapter.getList().size() > 0 && topicAdapter.getList().get(topicAdapter.getList().size()-1).isEmpty())
            {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_add_topic_name),Toast.LENGTH_SHORT).show();
            }
            else
            {
                topicAdapter.add("");
            }

        }
        else
        {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_add_topic_name),Toast.LENGTH_SHORT).show();
        }
    }

    public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {
        ArrayList<String> list = new ArrayList<>();
        private Context mContext;

        public ArrayList<String> getList() {
            return this.list;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_topic, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            String item = list.get(position);
            holder.etName.setText(item);
            holder.etName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    list.set(position, s.toString());
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

        public void add(String item) {
            list.add(item);
            notifyItemChanged(list.size()-1);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.etTopicName)
            EditText etName;

            @Bind(R.id.imgDelete)
            ImageView imgDelete;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

            }
        }
    }
}