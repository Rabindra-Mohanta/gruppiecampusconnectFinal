package school.campusconnect.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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
import android.widget.Button;
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
    private AlertDialog alertDialog;
    private boolean chapterClicked,topicClicked=false;



    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_syllabus);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.title_add_chapter_plan));
        inits();

        setListner();
        bindChapter();


    }

    private void inits() {

        topicAdapter = new TopicAdapter();


        teamId = getIntent().getStringExtra("team_id");
        subjectId = getIntent().getStringExtra("subject_id");


        manager = new LeafManager();


        String[] strChapter = new String[1];
        strChapter[0]=getResources().getString(R.string.toast_please_select_chapter);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner, strChapter);
        binding.spChapter.setAdapter(adapter);



    }
    private void setListner()
    {

        binding.btnAdd.setOnClickListener(this);
        binding.imgAddChapter.setOnClickListener(this);
        binding.imgAddTopic.setOnClickListener(this);

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

        }


        if(isEdit)
        {



            if (syllabusDataList != null && syllabusDataList.size() > 0) {

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



            case R.id.imgAdd:
                //addTopic();
                break;

            case R.id.btnAdd:
                hide_keyboard();


                if (isEdit)
                {
                    SyllabusPlanRequest request = new SyllabusPlanRequest();

                    ArrayList<SyllabusPlanRequest.TopicData> list = new ArrayList<>();



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

                    if(chapterClicked)
                    {
                        data.setChapterName(binding.txtChapter.getText().toString());
                    }
                    else
                    {
                        data.setChapterName(binding.spChapter.getSelectedItem().toString());
                    }


                    ArrayList<SyllabusModelReq.TopicModelData> arrayList = new ArrayList<>();
                    SyllabusModelReq.TopicModelData modelData = new SyllabusModelReq.TopicModelData();


                    if(topicClicked)
                    {
                        modelData.setTopicName(binding.txtTopic.getText().toString());
                    }
                    else
                    {
                        data.setChapterName(binding.spTopic.getSelectedItem().toString());
                    }



                    modelData.setToDate(binding.etToDate.getText().toString());
                    modelData.setFromDate(binding.etFromDate.getText().toString());

                    arrayList.add(modelData);


                    data.setTopicsList(arrayList);
                    syllabusModelData.add(data);

                    List<SyllabusTBL> list = SyllabusTBL.getSyllabus(teamId,subjectId);

//
                    req.setSyllabusModelData(syllabusModelData);

                    Log.e(TAG,"send Request "+new Gson().toJson(req));

                    showLoadingBar(binding.progressBar,false);

                    if(chapterClicked)
                    {


                        if(binding.etFromDate.getEditableText().toString().isEmpty()|| binding.etToDate.getEditableText().toString().isEmpty())

                        {
                            Toast.makeText(getApplicationContext(),"Plan date is required",Toast.LENGTH_LONG).show();


                        }
                        else
                        {
                            Log.d("TAG","binding.etFromDate"+binding.etFromDate.getText().toString());
                            manager.addSyllabus(this,GroupDashboardActivityNew.groupId,teamId,subjectId,req);
                        }

                    }
                    else
                    {
                        SyllabusPlanRequest request = new SyllabusPlanRequest();
                        ArrayList<SyllabusPlanRequest.TopicData> list1 = new ArrayList<>();
                        SyllabusPlanRequest.TopicData topicData = new SyllabusPlanRequest.TopicData();
                        topicData.setTopicName(binding.txtTopic.getText().toString());

                        if(binding.etFromDate.getEditableText().toString().isEmpty()|| binding.etToDate.getEditableText().toString().isEmpty())
                        {
                            Toast.makeText(getApplicationContext(),"Plan date is required",Toast.LENGTH_LONG).show();

                        }

                        else
                        {
                            list1.add(topicData);
                            request.setTopicData(list1);


                            manager.statusPlan(this,GroupDashboardActivityNew.groupId,teamId,subjectId,chapterID,request);

                        }


                    }





                }






                break;


            case R.id.imgAddChapter:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View root=getLayoutInflater().inflate(R.layout.dialog_enter_chapter,null);
                builder.setView(root);
                builder.setCancelable(false);
                ImageView btnCancel=root.findViewById(R.id.btnCancel);
                EditText etName=root.findViewById(R.id.etName);
                Button btnSubmit=root.findViewById(R.id.btnSubmit);
                alertDialog= builder.create();
                alertDialog.show();
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String chapterStr=etName.getText().toString();
                        if(chapterStr.equals(""))
                        {
                            Toast.makeText(getApplicationContext(),"Plz Add Chapter Name",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                            isEdit=false;
                            binding.spChapter.setVisibility(View.GONE);
                            binding.txtChapter.setVisibility(View.VISIBLE);
                            binding.txtChapter.setText(chapterStr);
                            chapterClicked=true;
                            binding.spTopic.setVisibility(View.GONE);
                            binding.txtTopic.setVisibility(View.VISIBLE);
                            binding.txtTopic.setText("");
                            binding.etFromDate.setText("");
                            binding.etToDate.setText("");
                            alertDialog.dismiss();
                        }


                    }
                });

                break;

            case R.id.imgAddTopic:


                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                View root1=getLayoutInflater().inflate(R.layout.dialog_enter_topic,null);
                builder1.setView(root1);
                builder1.setCancelable(false);
                ImageView btnCancel1=root1.findViewById(R.id.btnCancel);
                EditText etName1=root1.findViewById(R.id.etName);
                Button btnSubmit1=root1.findViewById(R.id.btnSubmit);
                alertDialog= builder1.create();
                alertDialog.show();
                btnCancel1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                btnSubmit1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String topicStr=etName1.getText().toString();


                        if(topicStr.equals(""))
                        {
                            Toast.makeText(getApplicationContext(),"Plz Add Topic Name",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                            binding.spTopic.setVisibility(View.GONE);
                            binding.txtTopic.setVisibility(View.VISIBLE);
                            binding.txtTopic.setText(topicStr);
                            binding.etToDate.setText("");
                            binding.etFromDate.setText("");
                            topicClicked=true;
                            isEdit=false;
                            alertDialog.dismiss();
                        }
                    }
                });


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
                binding.progressBar.setVisibility(View.GONE);
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
        public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

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