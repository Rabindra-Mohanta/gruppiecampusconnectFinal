package school.campusconnect.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.adapters.ReadUnReadUserAdapter;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ReadUnreadResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;

public class ReadUnreadUserFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "ReadUnreadUserFragment";
    private View view;
    @Bind(R.id.rvReadUsers)
    RecyclerView rvReadUsers;

    @Bind(R.id.rvUnReadUsers)
    RecyclerView rvUnReadUsers;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.tvEmpty1)
    TextView tvEmpty1;

    @Bind(R.id.tvEmpty2)
    TextView tvEmpty2;




    LeafManager leafManager;
    Context mContext;
    private String groupId;
    private String teamId;
    private String postId;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_read_unread_user,container,false);

        init();

        getUsers();

        return view;
    }

    private void getUsers() {
        showLoadingBar(progressBar);
        leafManager.getReadUnreadUser(this,groupId,teamId,postId);
    }

    private void init() {
        leafManager = new LeafManager();
        Bundle bundle=getArguments();
        if(bundle!=null)
        {
            groupId=bundle.getString("group_id");
            teamId=bundle.getString("team_id");
            postId=bundle.getString("post_id");
            AppLog.e(TAG,"groupId : "+groupId);
            AppLog.e(TAG,"teamId : "+teamId);
            AppLog.e(TAG,"postId : "+postId);
        }
        ButterKnife.bind(this,view);
        rvReadUsers.setLayoutManager(new LinearLayoutManager(mContext));
        rvUnReadUsers.setLayoutManager(new LinearLayoutManager(mContext));
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
        ReadUnreadResponse readUnreadResponse = (ReadUnreadResponse) response;
        AppLog.e(TAG,"readUnreadResponse :"+readUnreadResponse);
        if(readUnreadResponse.getData().size()>0)
        {
            rvReadUsers.setAdapter(new ReadUnReadUserAdapter(readUnreadResponse.getData().get(0).getReadUsers()));
            rvUnReadUsers.setAdapter(new ReadUnReadUserAdapter(readUnreadResponse.getData().get(0).getUnreadUsers()));

            if(readUnreadResponse.getData().get(0).getReadUsers().size()==0)
                tvEmpty1.setVisibility(View.VISIBLE);
            else
                tvEmpty1.setVisibility(View.GONE);

            if(readUnreadResponse.getData().get(0).getUnreadUsers().size()==0)
                tvEmpty2.setVisibility(View.VISIBLE);
            else
                tvEmpty2.setVisibility(View.GONE);



        }

    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        if (msg.contains("401:Unauthorized") || msg.contains("401")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else if (msg.contains("404")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_post), Toast.LENGTH_SHORT).show();
        } else if (msg.contains("418")) {
            if (apiId == LeafManager.API_REPORT)
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_already_reported), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        if (getActivity() != null)
            Toast.makeText(getActivity(), getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }
}
