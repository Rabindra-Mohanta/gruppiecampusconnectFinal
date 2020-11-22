package school.campusconnect.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.adapters.MarkSheetAdapter;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.MarkSheetListResponse;
import school.campusconnect.datamodel.marksheet.MarkCardListResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.views.SMBDialogUtils;

public class MarkSheetListFragment extends BaseFragment implements LeafManager.OnCommunicationListener,DialogInterface.OnClickListener, MarkSheetAdapter.VendorListener {

    private static final String TAG = "GalleryFragment";
    @Bind(R.id.rvGallery)
    RecyclerView rvGallery;

    @Bind(R.id.txtEmpty)
    TextView txtEmpty;

    @Bind(R.id.img_top)
    ImageView imgTop;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.spMarkCard)
    Spinner spMarkCard;

    public boolean mIsLoading = false;

    private View view;
    private LinearLayoutManager layoutManager;

    ArrayList<MarkSheetListResponse.MarkSheetData> listData=new ArrayList<>();
    MarkSheetAdapter vendorAdapter;
    private int totalPages=0;
    private int currentPage=1;
    private LeafManager manager;
    private String mGroupId;
    private MarkSheetListResponse.MarkSheetData currentItem;
    private String teamId;
    private String mark_card_id;
    private String userId;
    private String rollNo;
    private String role;
    private String name;


    List<MarkCardListResponse.MarkCardData> markCardList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            mGroupId = getArguments().getString("group_id");
            teamId = getArguments().getString("team_id");
            userId = getArguments().getString("user_id");
            rollNo = getArguments().getString("roll_no");
            role = getArguments().getString("role");
            name = getArguments().getString("name");
            mark_card_id = getArguments().getString("mark_card_id");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_marksheet_list,container,false);
        ButterKnife.bind(this,view);

        init();

        return view;
    }

    private void init() {

        manager=new LeafManager();

        layoutManager=new LinearLayoutManager(getActivity());
        rvGallery.setLayoutManager(layoutManager);
        if("teacher".equals(role) || "admin".equals(role)){
            vendorAdapter=new MarkSheetAdapter(listData,this,true);
        }else {
            vendorAdapter=new MarkSheetAdapter(listData,this,false);
        }

        rvGallery.setAdapter(vendorAdapter);

        spMarkCard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (markCardList != null) {
                    mark_card_id = markCardList.get(i).getMarksCardId();
                    AppLog.e(TAG, "getStudents : ");
                    listData.clear();
                    vendorAdapter.notifyDataSetChanged();
                    getData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        progressBar.setVisibility(View.VISIBLE);
        LeafManager leafManager = new LeafManager();
        leafManager.getMarkCardList(this, mGroupId, teamId);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
    private void getData()
    {
        if(isConnectionAvailable())
        {
            showLoadingBar(progressBar);
            mIsLoading = true;
            manager.MarkSheetListResponse(this, mGroupId+"",teamId,mark_card_id,userId,rollNo, currentPage);
        }
        else
        {
            showNoNetworkMsg();
        }

    }



    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        if(getActivity()==null){
            return;
        }
        progressBar.setVisibility(View.GONE);
        switch (apiId) {
            case LeafManager.API_MARK_CARD_LIST: {
                MarkCardListResponse res = (MarkCardListResponse) response;
                markCardList = res.getData();
                AppLog.e(TAG, "ClassResponse " + markCardList);
                int index=0;
                String[] stud = new String[markCardList.size()];
                for (int i = 0; i < markCardList.size(); i++) {
                    stud[i] = markCardList.get(i).getTitle();
                    if(markCardList.get(i).getMarksCardId().equals(mark_card_id)){
                        index = i;
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, stud);
                spMarkCard.setAdapter(adapter);
                spMarkCard.setSelection(index);
                if(markCardList==null || markCardList.size()==0){
                    Toast.makeText(getActivity(), "Please Create Mark Card", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case LeafManager.API_MARK_SHEET_LIST:
                MarkSheetListResponse res = (MarkSheetListResponse) response;
                AppLog.e(TAG, "Post Res ; " + new Gson().toJson(res.data));

               // if (currentPage == 1) {
                    listData.clear();

                    if(res.data!=null){
                        listData.addAll(res.data);
                    }

                    AppLog.e(TAG, "current page 1");

                /*} else {
                    listData.addAll(res.data);
                    AppLog.e(TAG, "current page " + currentPage);
                }*/

                if(listData.size()==0){
                    txtEmpty.setVisibility(View.VISIBLE);
                    txtEmpty.setText("No MarkCard Found!");
                }
                else
                    txtEmpty.setVisibility(View.GONE);

                vendorAdapter.notifyDataSetChanged();

                totalPages = res.totalNumberOfPages;
                mIsLoading = false;
                break;
            case LeafManager.API_MARK_SHEET_DELETE:
                Toast.makeText(getContext(), "Mark Card Deleted Successfully", Toast.LENGTH_SHORT).show();
                if(getActivity()!=null){
                    getActivity().finish();
                }
                break;

        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        mIsLoading = false;
        /*currentPage = currentPage - 1;
        if (currentPage < 0) {
            currentPage = 1;
        }*/
        if (msg.contains("401:Unauthorized") || msg.contains("401")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else if (msg.contains("404")) {
            Toast.makeText(getActivity(), "No posts available.", Toast.LENGTH_SHORT).show();
        } else if (msg.contains("418")) {
            if (apiId == LeafManager.API_REPORT)
                Toast.makeText(getActivity(), "You have already reported this post", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        mIsLoading = false;
       /* currentPage = currentPage - 1;
        if (currentPage < 0) {
            currentPage = 1;
        }*/
        if (getActivity() != null)
            Toast.makeText(getActivity(), getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        AppLog.e("TeamPostFrag", "DIalog Ok Clicked ");
        if (isConnectionAvailable()) {
            showLoadingBar(progressBar);
            LeafManager manager = new LeafManager();
            manager.deleteMarkCart(this, mGroupId+"",teamId,currentItem.marksCardId,userId,rollNo);

        } else {
            showNoNetworkMsg();
        }

    }
    @Override
    public void onDeleteClick(MarkSheetListResponse.MarkSheetData item) {
        currentItem = item;
        SMBDialogUtils.showSMBDialogOKCancel(getActivity(), "Are You Sure Want To Delete ?", this);
    }

    @Override
    public void onPostClick(MarkSheetListResponse.MarkSheetData item) {

    }

    @Override
    public void onClick(MarkSheetListResponse.MarkSheetData markSheetData) {
    /*    Intent intent = new Intent(getActivity(), MarkCardReadMoreActivity.class);
        intent.putExtra("data",new Gson().toJson(markSheetData));
        intent.putExtra("group_id",mGroupId);
        intent.putExtra("team_id",teamId);
        intent.putExtra("user_id",userId);
        intent.putExtra("roll_no",rollNo);
        intent.putExtra("name",name);
        intent.putExtra("role",role);
        startActivity(intent);*/
    }
}
