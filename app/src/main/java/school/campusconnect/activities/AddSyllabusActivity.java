package school.campusconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.ActivityAddSyllabusBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.syllabus.SyllabusListModelRes;
import school.campusconnect.datamodel.syllabus.SyllabusModelReq;
import school.campusconnect.datamodel.syllabus.SyllabusPlanRequest;
import school.campusconnect.datamodel.syllabus.SyllabusTBL;
import school.campusconnect.fragments.DatePickerFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;

public class AddSyllabusActivity extends BaseActivity implements View.OnClickListener, LeafManager.OnCommunicationListener {
ActivityAddSyllabusBinding binding;
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    public static final String TAG = "AddSyllabusActivity";
    TopicAdapter topicAdapter;
    LeafManager manager;
    String teamId,subjectId;
    ArrayList<SyllabusListModelRes.SyllabusData> syllabusDataList = new ArrayList<>();
    ArrayList<SyllabusListModelRes.TopicData> topicData = new ArrayList<>();
    ArrayList<SyllabusListModelRes.TopicData> selectedTopicData = new ArrayList<>();

    String chapterID = null,TopicId = null;
    private boolean isEdit = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_syllabus);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.title_add_chapter_plan));

        inits();

        bindChapter();

        setListner();


    }

    private void inits() {

        topicAdapter = new TopicAdapter();
        //binding.rvTopic.setAdapter(topicAdapter);

        teamId = getIntent().getStringExtra("team_id");
        subjectId = getIntent().getStringExtra("subject_id");

        Log.e(TAG,"team ID"+teamId+"\nsubject Id"+subjectId);
        manager = new LeafManager();
    }
    private void setListner()
    {
       // binding.imgAdd.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
        binding.imgAddChapter.setOnClickListener(this);
        binding.ImgRemoveChapter.setOnClickListener(this);


        binding.etFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerFragment fragment = DatePickerFragment.newInstance();

                fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                    @Override
                    public void onDateSelected(Calendar c) {
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                       binding.etFromDate.setText(format.format(c.getTime()));
                    }
                });
                fragment.show(getSupportFragmentManager(), "datepicker");
                fragment.setTitle(R.string.lbl_from_date);
            }
        });

        binding.etToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerFragment fragment = DatePickerFragment.newInstance();

                fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                    @Override
                    public void onDateSelected(Calendar c) {
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                       binding.etToDate.setText(format.format(c.getTime()));
                    }
                });
                fragment.show(getSupportFragmentManager(), "datepicker");
                fragment.setTitle(R.string.lbl_to_date);
            }
        });

    }

    private void bindChapter() {

        List<SyllabusTBL> tblList = SyllabusTBL.getSyllabus(teamId,subjectId);

        if (tblList.size() > 0)
        {
            for (int i = 0;i<tblList.size();i++)
            {
                SyllabusListModelRes.SyllabusData data = new SyllabusListModelRes.SyllabusData();

                data.setChapterName(tblList.get(i).chapterName);
                data.setChapterId(tblList.get(i).chapterId);
                data.setTopicData(new Gson().fromJson(tblList.get(i).topicsList, new TypeToken<ArrayList<SyllabusListModelRes.TopicData>>() {}.getType()));
                syllabusDataList.add(data);
            }

        }
        else
        {
            isEdit = false;
            chapterID = null;
            TopicId = null;
            selectedTopicData.clear();

            binding.llTopicData.setVisibility(View.GONE);
            binding.llChapterData.setVisibility(View.GONE);
            binding.etcName.setVisibility(View.VISIBLE);
            binding.etTopicName.setVisibility(View.VISIBLE);
        }

        if (syllabusDataList != null && syllabusDataList.size() > 0) {

            binding.llChapterData.setVisibility(View.VISIBLE);
            binding.llTopicData.setVisibility(View.VISIBLE);
            binding.etTopicName.setVisibility(View.GONE);
            binding.etcName.setVisibility(View.GONE);

            String[] strChapter = new String[syllabusDataList.size()];
            for (int i = 0; i < strChapter.length; i++) {
                strChapter[i] = syllabusDataList.get(i).getChapterName();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner, strChapter);
            binding.spChapter.setAdapter(adapter);

            binding.spChapter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    AppLog.e(TAG, "onItemSelected : " + binding.spChapter.getSelectedItem().toString());
                    AppLog.e(TAG, "onItemSelected ID: " + syllabusDataList.get(position).getChapterId());
                    TopicId = null;
                    selectedTopicData.clear();

                    chapterID = syllabusDataList.get(position).getChapterId();
                    setTopic(syllabusDataList.get(position).getTopicData());


                  /*  for (int i = 0;i<syllabusDataList.size();i++)
                    {
                        if (binding.spChapter.getSelectedItem().toString().equalsIgnoreCase(syllabusDataList.get(i).getChapterName()))
                        {
                            AppLog.e(TAG, "Compare Value " + syllabusDataList.get(i).getChapterId());
                        }
                    }*/
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });




        }

    }

    private void setTopic(ArrayList<SyllabusListModelRes.TopicData> topicData) {

        this.topicData.clear();
        this.topicData.addAll(topicData);

        selectedTopicData.clear();

        String[] strTopic = new String[topicData.size()];
        for (int i = 0; i < strTopic.length; i++) {
            strTopic[i] = topicData.get(i).getTopicName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner, strTopic);
        binding.spTopic.setAdapter(adapter);

        binding.spTopic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                AppLog.e(TAG, "onItemSelected : " + binding.spTopic.getSelectedItem().toString());
                AppLog.e(TAG, "onItemSelected ID: " + topicData.get(position).getTopicId());

                selectedTopicData.clear();
                selectedTopicData.add(topicData.get(position));

                TopicId = topicData.get(position).getTopicId();
                binding.etToDate.setText(topicData.get(position).getToDate());
                binding.etFromDate.setText(topicData.get(position).getFromDate());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
//            case R.id.imgAdd:
//                addTopic();
//                break;

            case R.id.btnAdd:
                hide_keyboard();
                if (isValid())
                {

                    if (isEdit)
                    {
                        SyllabusPlanRequest request = new SyllabusPlanRequest();

                        ArrayList<SyllabusPlanRequest.TopicData> list = new ArrayList<>();

                        for (int i = 0;i<topicData.size();i++)
                        {
                            if (!TopicId.equals(topicData.get(i).getTopicId()))
                            {
                                SyllabusPlanRequest.TopicData topicData1 = new SyllabusPlanRequest.TopicData();
                                topicData1.setToDate(topicData.get(i).getToDate());
                                topicData1.setFromDate(topicData.get(i).getFromDate());
                                topicData1.setActualStartDate(topicData.get(i).getActualStartDate());
                                topicData1.setActualEndDate(topicData.get(i).getActualEndDate());
                                topicData1.setTopicId(topicData.get(i).getTopicId());
                                topicData1.setTopicName(topicData.get(i).getTopicName());

                                list.add(topicData1);
                            }
                        }

                        SyllabusPlanRequest.TopicData topicData = new SyllabusPlanRequest.TopicData();
                        topicData.setTopicId(TopicId);
                        topicData.setTopicName(binding.spTopic.getSelectedItem().toString());
                        topicData.setToDate(binding.etToDate.getText().toString());
                        topicData.setFromDate(binding.etFromDate.getText().toString());

                        list.add(topicData);

                        request.setTopicData(list);
                        Log.e(TAG,"req is Update "+new Gson().toJson(request));
                        showLoadingBar(binding.progressBar,false);
                        manager.statusPlan(this,GroupDashboardActivityNew.groupId,teamId,subjectId,chapterID,request);
                    }
                    else
                    {
                        SyllabusModelReq req = new SyllabusModelReq();

                        ArrayList<SyllabusModelReq.SyllabusModelData> syllabusModelData = new ArrayList<>();

                        SyllabusModelReq.SyllabusModelData data = new SyllabusModelReq.SyllabusModelData();
                        data.setChapterName(binding.etcName.getText().toString());

                        ArrayList<SyllabusModelReq.TopicModelData> arrayList = new ArrayList<>();
                        SyllabusModelReq.TopicModelData modelData = new SyllabusModelReq.TopicModelData();
                        modelData.setTopicName(binding.etTopicName.getText().toString());
                        modelData.setToDate(binding.etToDate.getText().toString());
                        modelData.setFromDate(binding.etFromDate.getText().toString());

                        arrayList.add(modelData);

                        if (topicAdapter.getList().size() > 0)
                        {
                            /*SyllabusModelReq.TopicModelData data2 = new SyllabusModelReq.TopicModelData();
                            data2.setTopicName(binding.etTopicName.getText().toString());
                            data2.setToDate(binding.etToDate.getText().toString());
                            data2.setFromDate(binding.etFromDate.getText().toString());

                            arrayList.add(data2);*/

                            for (int i =0;i<topicAdapter.getList().size();i++)
                            {
                                Log.e(TAG,"topicAdapter data pos"+ i +" data "+topicAdapter.getList().get(i).getTopicName());
                                if (topicAdapter.getList().get(i).getTopicName() == null || topicAdapter.getList().get(i).getTopicName().isEmpty())
                                {
                                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_add_topic_name)+ i,Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                            for (int i =0;i<topicAdapter.getList().size();i++)
                            {
                                SyllabusModelReq.TopicModelData data1 = new SyllabusModelReq.TopicModelData();
                                data1.setTopicName(topicAdapter.getList().get(i).getTopicName());
                                data1.setToDate(topicAdapter.getList().get(i).getToDate());
                                data1.setFromDate(topicAdapter.getList().get(i).getFromDate());
                                arrayList.add(data1);
                            }
                        }

                        data.setTopicsList(arrayList);
                        syllabusModelData.add(data);

                        List<SyllabusTBL> list = SyllabusTBL.getSyllabus(teamId,subjectId);

//                        if (list.size() > 0)
//                        {
//                            for (int i = 0;i<list.size();i++)
//                            {
//                                SyllabusModelReq.SyllabusModelData modelData1 = new SyllabusModelReq.SyllabusModelData();
//                                modelData1.setChapterName(list.get(i).chapterName);
//                                modelData1.setTopicsList(new Gson().fromJson(list.get(i).topicsList, new TypeToken<ArrayList<SyllabusListModelRes.TopicData>>() {}.getType()));
//                                syllabusModelData.add(modelData1);
//                            }
//                        }
                        req.setSyllabusModelData(syllabusModelData);

                        Log.e(TAG,"send Request "+new Gson().toJson(req));

                        showLoadingBar(binding.progressBar,false);
                        manager.addSyllabus(this,GroupDashboardActivityNew.groupId,teamId,subjectId,binding.etcName.getText().toString());
                    }

                }
                break;

            case R.id.imgAddChapter:

                isEdit = false;

                binding.llChapterData.setVisibility(View.GONE);
                binding.llTopicData.setVisibility(View.GONE);
                binding.etcName.getText().clear();
                binding.etTopicName.getText().clear();
                binding.etToDate.getText().clear();
                binding.etFromDate.getText().clear();

                binding.ImgRemoveChapter.setVisibility(View.VISIBLE);
                binding.etcName.setVisibility(View.VISIBLE);
                binding.etTopicName.setVisibility(View.VISIBLE);
               // binding.imgAdd.setVisibility(View.VISIBLE);
                //binding.rvTopic.setVisibility(View.VISIBLE);
                chapterID = null;
                selectedTopicData.clear();
                TopicId = null;
                break;

            case R.id.ImgRemoveChapter:

                isEdit = true;
                binding.llChapterData.setVisibility(View.VISIBLE);
                binding.llTopicData.setVisibility(View.VISIBLE);
                binding.etTopicName.getText().clear();
                binding.etToDate.getText().clear();
                binding.etFromDate.getText().clear();
                //binding.imgAdd.setVisibility(View.GONE);
               // binding.rvTopic.setVisibility(View.GONE);
                binding.etTopicName.setVisibility(View.GONE);

                binding.etcName.getText().clear();
                binding.ImgRemoveChapter.setVisibility(View.GONE);

                binding.etcName.setVisibility(View.GONE);
                bindChapter();
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

            case LeafManager.API_STATUS_PLAN:
             //   Toast.makeText(this, getResources().getString(R.string.toast_syllabus_add_successfully), Toast.LENGTH_SHORT).show();
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
        if(!isEdit)
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
        }

        return true;
    }
    private void addTopic() {


        if (!binding.etTopicName.getText().toString().isEmpty())
        {
            Log.e(TAG,"topicAdapter" + topicAdapter.getList().size());

            if (topicAdapter.getList().size() > 0 && topicAdapter.getList().get(topicAdapter.getList().size()-1).getTopicName().isEmpty())
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
        ArrayList<SyllabusModelReq.TopicModelData> list = new ArrayList<>();

        private Context mContext;

        public ArrayList<SyllabusModelReq.TopicModelData> getList() {
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

            SyllabusModelReq.TopicModelData item = list.get(position);

            holder.etName.setText(item.getTopicName());
            holder.etFromDate.setText(item.getFromDate());
            holder.etToDate.setText(item.getToDate());

            holder.etName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    Log.e(TAG,"Position");
                    SyllabusModelReq.TopicModelData item = new SyllabusModelReq.TopicModelData();
                    item.setTopicName(s.toString());
                    item.setFromDate(holder.etFromDate.getText().toString());
                    item.setToDate(holder.etToDate.getText().toString());
                    list.set(position, item);
                }
            });

            holder.etFromDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatePickerFragment fragment = DatePickerFragment.newInstance();

                    fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                        @Override
                        public void onDateSelected(Calendar c) {
                            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            holder.etFromDate.setText(format.format(c.getTime()));

                            SyllabusModelReq.TopicModelData item = new SyllabusModelReq.TopicModelData();
                            item.setTopicName(holder.etName.getText().toString());
                            item.setFromDate(format.format(c.getTime()));
                            item.setToDate(holder.etToDate.getText().toString());
                            list.set(position, item);
                        }
                    });
                    fragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "datepicker");
                    fragment.setTitle(R.string.lbl_from_date);
                }
            });

            holder.etToDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatePickerFragment fragment = DatePickerFragment.newInstance();

                    fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                        @Override
                        public void onDateSelected(Calendar c) {
                            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            holder.etToDate.setText(format.format(c.getTime()));

                            SyllabusModelReq.TopicModelData item = new SyllabusModelReq.TopicModelData();
                            item.setTopicName(holder.etName.getText().toString());
                            item.setToDate(format.format(c.getTime()));
                            item.setFromDate(holder.etFromDate.getText().toString());
                            list.set(position, item);
                        }
                    });
                    fragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "datepicker");
                    fragment.setTitle(R.string.lbl_to_date);
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
            SyllabusModelReq.TopicModelData itemData = new SyllabusModelReq.TopicModelData();
            itemData.setTopicName(item);
            list.add(itemData);
            notifyItemChanged(list.size()-1);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.etTopicName)
            EditText etName;

            @Bind(R.id.etFromDate)
            EditText etFromDate;

            @Bind(R.id.etToDate)
            EditText etToDate;

            @Bind(R.id.imgDelete)
            ImageView imgDelete;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

            }
        }
    }
}