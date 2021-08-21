package school.campusconnect.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.CompletedTopicUserActivity;
import school.campusconnect.activities.FullScreenActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.TestActivity;
import school.campusconnect.activities.ViewPDFActivity;
import school.campusconnect.adapters.TestExamPostAdapter;
import school.campusconnect.adapters.TopicPostAdapter;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ChapterTBL;
import school.campusconnect.datamodel.EventTBL;
import school.campusconnect.datamodel.HwItem;
import school.campusconnect.datamodel.TestExamTBL;
import school.campusconnect.datamodel.chapter.ChapterRes;
import school.campusconnect.datamodel.homework.HwRes;
import school.campusconnect.datamodel.test_exam.TestExamRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.MixOperations;
import school.campusconnect.views.SMBDialogUtils;

public class TestExamListFragment extends BaseFragment implements LeafManager.OnCommunicationListener, TestExamPostAdapter.OnItemClickListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    String team_id;
    String subject_id;
    String subject_name;
    boolean canPost;
    private TestExamRes.TestExamData currentItem;
    private int adapterPosition;
    private TestExamPostAdapter adapter;
    private ArrayList<TestExamRes.TestExamData> testList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss, container, false);
        ButterKnife.bind(this, view);
        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        team_id = getArguments().getString("team_id");
        subject_id = getArguments().getString("subject_id");
        subject_name = getArguments().getString("subject_name");
        canPost = getArguments().getBoolean("canPost");


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDataLocally();
    }

    //    EventTBL eventTBL;
    private void getDataLocally() {
       /* eventTBL = EventTBL.getAssignmentEvent(GroupDashboardActivityNew.groupId,team_id,subject_id);
        boolean apiEvent = false;
        if(eventTBL!=null){
            if(eventTBL._now ==0){
                apiEvent = true;
            }
            if(MixOperations.isNewEvent(eventTBL.eventAt,"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",eventTBL._now)){
                apiEvent = true;
            }
        }*/

        List<TestExamTBL> list = TestExamTBL.getAll(subject_id, team_id, GroupDashboardActivityNew.groupId);
        if (list.size() != 0) {
            testList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                TestExamTBL currentItem = list.get(i);
                TestExamRes.TestExamData item = new TestExamRes.TestExamData();
                item.topicName = currentItem.topic;
                item.testStartTime = currentItem.testStartTime;
                item.testEndTime = currentItem.testEndTime;
                item.testExamId = currentItem.testExamId;
                item.testDate = currentItem.testDate;
                item.lastSubmissionTime = currentItem.lastSubmissionTime;
                item.description = currentItem.description;
                item.createdByName = currentItem.createdByName;
                item.createdById = currentItem.createdById;
                item.createdByImage = currentItem.createdByImage;
                item.canPost = currentItem.canPost;
                item.fileType = currentItem.fileType;
                item.fileName = new Gson().fromJson(currentItem.fileName, new TypeToken<ArrayList<String>>() {
                }.getType());
                item.thumbnailImage = new Gson().fromJson(currentItem.thumbnailImage, new TypeToken<ArrayList<String>>() {
                }.getType());
                item.thumbnail = currentItem.thumbnail;
                item.video = currentItem.video;

                item.subjectId = currentItem.subjectId;
                item.teamId = currentItem.teamId;
                testList.add(item);
            }
            setData();

//            if(apiEvent){
            getTestExam();
//            }
        } else {
            getTestExam();
        }
    }

    public void getTestExam() {
        progressBar.setVisibility(View.VISIBLE);
        LeafManager leafManager = new LeafManager();
        leafManager.getTestExamList(this, GroupDashboardActivityNew.groupId, team_id, subject_id);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (LeafPreference.getInstance(getActivity()).getBoolean("is_test_added")) {
            getTestExam();
            LeafPreference.getInstance(getActivity()).setBoolean("is_test_added", false);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);
        switch (apiId) {
            case LeafManager.API_TEST_EXAM_REMOVE:
                getTestExam();
                break;
            default:
                TestExamRes res = (TestExamRes) response;
                testList = res.getData();
                AppLog.e(TAG, "TestExamRes " + testList);
                setData();

                saveToDB(testList);

               /* if(eventTBL!=null){
                    eventTBL._now = System.currentTimeMillis();
                    eventTBL.save();
                }*/
        }

    }

    private void saveToDB(ArrayList<TestExamRes.TestExamData> result) {
        if (result == null)
            return;

        TestExamTBL.deleteAll(subject_id);

        for (int i = 0; i < result.size(); i++) {
            TestExamRes.TestExamData currentItem = result.get(i);
            TestExamTBL item = new TestExamTBL();
            item.topic = currentItem.topicName;
            item.testStartTime = currentItem.testStartTime;
            item.testEndTime = currentItem.testEndTime;
            item.testExamId = currentItem.testExamId;
            item.testDate = currentItem.testDate;
            item.lastSubmissionTime = currentItem.lastSubmissionTime;
            item.description = currentItem.description;
            item.createdByName = currentItem.createdByName;
            item.createdById = currentItem.createdById;
            item.createdByImage = currentItem.createdByImage;
            item.canPost = currentItem.canPost;
            item.fileType = currentItem.fileType;
            item.fileName = new Gson().toJson(currentItem.fileName);
            item.thumbnailImage = new Gson().toJson(currentItem.thumbnailImage);
            item.thumbnail = currentItem.thumbnail;
            item.video = currentItem.video;

            item.subjectId = currentItem.subjectId;
            item.teamId = currentItem.teamId;
            item.groupId = GroupDashboardActivityNew.groupId;


            item.save();
        }
    }

    private void setData() {
        adapter = new TestExamPostAdapter(testList, this, canPost);
        rvClass.setAdapter(adapter);

        if (testList != null && testList.size() > 0) {
            txtEmpty.setVisibility(View.GONE);
            txtEmpty.setText("");
        } else {
            txtEmpty.setVisibility(View.VISIBLE);
            txtEmpty.setText("No Test/Exam Found");
        }

    }

    @Override
    public void onFailure(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDeleteClick(TestExamRes.TestExamData item, int adapterPosition) {
        currentItem = item;
        this.adapterPosition = adapterPosition;
        SMBDialogUtils.showSMBDialogOKCancel(getActivity(), "Are you sure you want to delete this Test/Exam?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isConnectionAvailable()) {
                    progressBar.setVisibility(View.VISIBLE);
                    LeafManager manager = new LeafManager();
                    manager.deleteTestExam(TestExamListFragment.this, GroupDashboardActivityNew.groupId, team_id, subject_id, item.testExamId);
                } else {
                    showNoNetworkMsg();
                }
            }
        });
    }

    @Override
    public void onPostClick(TestExamRes.TestExamData item) {
        if (item.fileType.equals(Constants.FILE_TYPE_YOUTUBE)) {
            Intent browserIntent = new Intent(getActivity(), TestActivity.class);
            browserIntent.putExtra("url", item.video);
            startActivity(browserIntent);

        } else if (item.fileType.equals(Constants.FILE_TYPE_PDF)) {
            Intent i = new Intent(getActivity(), ViewPDFActivity.class);
            i.putExtra("pdf", item.fileName.get(0));
            i.putExtra("name", item.topicName);
            startActivity(i);

        } else if (item.fileType.equals(Constants.FILE_TYPE_IMAGE)) {
            Intent i = new Intent(getActivity(), FullScreenActivity.class);
            i.putExtra("image", item.fileName);
            startActivity(i);
        }
    }
}