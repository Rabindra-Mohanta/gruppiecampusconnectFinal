package school.campusconnect.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.j256.ormlite.stmt.query.In;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.AddChapterPostActivity;
import school.campusconnect.activities.CompletedTopicUserActivity;
import school.campusconnect.activities.FullScreenActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.TestActivity;
import school.campusconnect.activities.ViewPDFActivity;
import school.campusconnect.adapters.TopicPostAdapter;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.chapter.ChapterRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.views.SMBDialogUtils;

public class ChapterListFragment extends BaseFragment implements LeafManager.OnCommunicationListener, TopicPostAdapter.OnItemClickListener, DialogInterface.OnClickListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.llTop)
    public LinearLayout llTop;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    @Bind(R.id.spChapter)
    public Spinner spChapter;


    String team_id;
    String subject_id;
    String subject_name;
    boolean canPost;
    private ArrayList<ChapterRes.ChapterData> chapterList;
    private ChapterRes.TopicData currentItem;
    private int adapterPosition;
    private TopicPostAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chapter_list, container, false);
        ButterKnife.bind(this, view);
        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        team_id = getArguments().getString("team_id");
        subject_id = getArguments().getString("subject_id");
        subject_name = getArguments().getString("subject_name");
        canPost = getArguments().getBoolean("canPost");

        progressBar.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getChapters();


    }

   /* private void addNewTopic() {
        Intent intent = new Intent(getActivity(), AddChapterPostActivity.class);
        intent.putExtra("group_id", GroupDashboardActivityNew.groupId);
        intent.putExtra("team_id", team_id);
        intent.putExtra("subject_id", subject_id);
        intent.putExtra("subject_name", subject_name);
        intent.putExtra("isEdit", true);
        intent.putExtra("chapter_id", chapterList.get(spChapter.getSelectedItemPosition()).chapterId);
        intent.putExtra("chapter_name", chapterList.get(spChapter.getSelectedItemPosition()).chapterName);
        startActivity(intent);
    }*/

    public void getChapters() {
        LeafManager leafManager = new LeafManager();
        leafManager.getChapterList(this, GroupDashboardActivityNew.groupId, team_id, subject_id);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (LeafPreference.getInstance(getActivity()).getBoolean("is_chapter_added")) {
            getChapters();
            LeafPreference.getInstance(getActivity()).setBoolean("is_chapter_added", false);
        }
        if (LeafPreference.getInstance(getActivity()).getBoolean("is_topic_added")) {
            getChapters();
            LeafPreference.getInstance(getActivity()).setBoolean("is_topic_added", false);
        }
        if(adapter!=null){
            adapter.notifyDataSetChanged();
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
            case LeafManager.API_CHAPTER_REMOVE:
                chapterList.remove(spChapter.getSelectedItemPosition());
                bindChapter();
                break;
            case LeafManager.API_TOPIC_REMOVE:
                chapterList.get(spChapter.getSelectedItemPosition()).topicList.remove(adapterPosition);
                adapter.notifyDataSetChanged();
                break;
            case LeafManager.API_TOPIC_STATUS_CHANGE:
                // do nothing
                break;
            default:
                ChapterRes res = (ChapterRes) response;
                chapterList = res.getData();
                AppLog.e(TAG, "ChapterRes " + chapterList);
                bindChapter();

        }

    }

    private void bindChapter() {
        if (chapterList != null && chapterList.size() > 0) {
            txtEmpty.setVisibility(View.GONE);
            llTop.setVisibility(View.VISIBLE);

            String[] strChapter = new String[chapterList.size()];
            for (int i=0;i<strChapter.length;i++){
                strChapter[i]=chapterList.get(i).chapterName;
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), R.layout.item_spinner,strChapter);
            spChapter.setAdapter(adapter);

            spChapter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    AppLog.e(TAG, "onItemSelected : " + position);
                    setData(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
            txtEmpty.setVisibility(View.VISIBLE);
            llTop.setVisibility(View.GONE);
            if(adapter!=null){
                adapter.clear();
            }
        }

    }

    private void setData(int position) {
        adapter = new TopicPostAdapter(chapterList.get(position).topicList, this, canPost);
        rvClass.setAdapter(adapter);

        if (chapterList.get(position).topicList != null && chapterList.get(position).topicList.size() > 0) {
            txtEmpty.setVisibility(View.GONE);
        } else {
            txtEmpty.setVisibility(View.VISIBLE);
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
    public void onDeleteClick(ChapterRes.TopicData item, int adapterPosition) {
        currentItem = item;
        this.adapterPosition = adapterPosition;
        SMBDialogUtils.showSMBDialogOKCancel(getActivity(), "Are you sure you want to delete this topic?", this);
    }

    @Override
    public void onCompleteClick(ChapterRes.TopicData item, int adapterPosition) {
        progressBar.setVisibility(View.VISIBLE);
        LeafManager manager = new LeafManager();
        manager.topicCompleteStatus(ChapterListFragment.this, GroupDashboardActivityNew.groupId, team_id, subject_id, chapterList.get(spChapter.getSelectedItemPosition()).chapterId,item.topicId);
    }

    @Override
    public void onCompletedStudentClick(ChapterRes.TopicData item, int adapterPosition) {
        Intent intent=new Intent(getActivity(), CompletedTopicUserActivity.class);
        intent.putExtra("item",new Gson().toJson(item));
        intent.putExtra("group_id",GroupDashboardActivityNew.groupId);
        intent.putExtra("team_id",team_id);
        intent.putExtra("subject_id",subject_id);
        startActivity(intent);
    }

    public void onDeleteChapterClick() {
        SMBDialogUtils.showSMBDialogOKCancel(getActivity(), "Are you sure you want to delete this chapter?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isConnectionAvailable()) {
                    progressBar.setVisibility(View.VISIBLE);
                    LeafManager manager = new LeafManager();
                    manager.deleteChapter(ChapterListFragment.this, GroupDashboardActivityNew.groupId, team_id, subject_id, chapterList.get(spChapter.getSelectedItemPosition()).chapterId);
                } else {
                    showNoNetworkMsg();
                }
            }
        });
    }

    @Override
    public void onPostClick(ChapterRes.TopicData item) {
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

    @Override
    public void onDeleteVideoClick(ChapterRes.TopicData item, int adapterPosition) {
        AppLog.e(TAG , "onDeleteVideoClick : "+item.fileName.get(0));
        if(item.fileName!=null && item.fileName.size()>0){
            AmazoneDownload.removeVideo(getActivity(),item.fileName.get(0));
            adapter.notifyItemChanged(adapterPosition);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (isConnectionAvailable()) {
            progressBar.setVisibility(View.VISIBLE);
            LeafManager manager = new LeafManager();
            manager.deleteTopic(this, GroupDashboardActivityNew.groupId, team_id, subject_id, chapterList.get(spChapter.getSelectedItemPosition()).chapterId, currentItem.topicId);

        } else {
            showNoNetworkMsg();
        }
    }
}
