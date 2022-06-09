package school.campusconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.MarksheetActivity;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.marksheet.MarkCardListResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;

public class MarkCardListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;
    private String groupId;
    private String teamId;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss,container,false);
        ButterKnife.bind(this,view);
        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        init();

        showLoadingBar(progressBar,false);
     //   progressBar.setVisibility(View.VISIBLE);

        return view;
    }

    private void init() {
        Bundle bundle = getActivity().getIntent().getExtras();
        groupId = bundle.getString("group_id", "");
        teamId = bundle.getString("team_id", "");
        AppLog.e(TAG, ",groupId:" + groupId + ",teamId:" + teamId);
    }

    @Override
    public void onStart() {
        super.onStart();
        LeafManager leafManager = new LeafManager();
        leafManager.getMarkCardList(this,groupId,teamId);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
       // progressBar.setVisibility(View.GONE);
        MarkCardListResponse res = (MarkCardListResponse) response;
        List<MarkCardListResponse.MarkCardData> result = res.getData();
        AppLog.e(TAG, "ClassResponse " + result);

        rvClass.setAdapter(new MarkCardListAdapter(result));
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        // progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        // progressBar.setVisibility(View.GONE);
    }

    public class MarkCardListAdapter extends RecyclerView.Adapter<MarkCardListAdapter.ViewHolder>
    {
        List<MarkCardListResponse.MarkCardData> list;
        private Context mContext;

        public MarkCardListAdapter(List<MarkCardListResponse.MarkCardData> list) {
            this.list = list;
        }

        @Override
        public MarkCardListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mark_card,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MarkCardListAdapter.ViewHolder holder, final int position) {
            final MarkCardListResponse.MarkCardData item = list.get(position);
            holder.txt_name.setText(item.getTitle());
            holder.txt_count.setText("");
        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    txtEmpty.setText(getResources().getString(R.string.txt_no_mark_card_found));
                }
                else {
                    txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                txtEmpty.setText(getResources().getString(R.string.txt_no_mark_card_found));
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.txt_name)
            TextView txt_name;

            @Bind(R.id.txt_count)
            TextView txt_count;
            @Bind(R.id.img_tree)
            ImageView img_tree;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onTreeClick(list.get(getAdapterPosition()));
                    }
                });
                img_tree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onTreeClick(list.get(getAdapterPosition()));
                    }
                });

            }
        }
    }

    private void onTreeClick(MarkCardListResponse.MarkCardData classData) {
        Intent intent = new Intent(getActivity(), MarksheetActivity.class);
        intent.putExtra("group_id",groupId);
        intent.putExtra("team_id",teamId);
        intent.putExtra("mark_card_id",classData.marksCardId);
        startActivity(intent);
    }
}
