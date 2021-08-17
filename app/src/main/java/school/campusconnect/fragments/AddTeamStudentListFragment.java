package school.campusconnect.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.activities.AddTeamStudentActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.LeadItem;
import school.campusconnect.datamodel.LeadResponse;
import school.campusconnect.datamodel.student.StudentRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class AddTeamStudentListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.tvCount)
    public TextView tvCount;


    @Bind(R.id.progressBar)
    public ProgressBar progressBar;
    private AddTeamStudentAdapter adapter;
    String groupId;
    String teamId;
    String classId;
    LeafManager leafManager = new LeafManager();
    private ArrayList<String> teamList=new ArrayList<>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupId = getArguments().getString("id");
        teamId = getArguments().getString("team_id");
        classId = getArguments().getString("class_id");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_team_staff, container, false);
        ButterKnife.bind(this, view);
        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));




        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);
        leafManager.getTeamMember(this, groupId+"", teamId+"",false);

        if(getActivity()!=null){
            ((AddTeamStudentActivity)getActivity()).enableSelection(true);
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        if (getActivity() == null)
            return;

        if(apiId==LeafManager.API_ID_LEAD_LIST){
            LeadResponse res = (LeadResponse) response;
            teamList.clear();
            List<LeadItem> result = res.getResults();

            for (int i=0;i<result.size();i++)
            {
                teamList.add(result.get(i).id);
            }
            leafManager.getStudents(this, GroupDashboardActivityNew.groupId,classId);
        }
        else if (apiId == LeafManager.API_STAFF_STUDENT_TEAM) {
            LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.ISTEAMUPDATED,true);
            getActivity().finish();
        } else {
            progressBar.setVisibility(View.GONE);
            StudentRes res = (StudentRes) response;
            List<StudentRes.StudentData> result = res.getData();
            AppLog.e(TAG, "ClassResponse " + result);

            Iterator<StudentRes.StudentData> it = result.iterator();
            while (it.hasNext()){
                StudentRes.StudentData dt = it.next();
                if(teamList.contains(dt.getUserId())){
                    it.remove();
                }
            }
            adapter = new AddTeamStudentAdapter(result);
            rvClass.setAdapter(adapter);
        }

    }

    @OnClick({R.id.fabAdd})
    public void onClick(View view) {
        if (adapter != null && adapter.getSelectedCount() > 0) {
            LeafManager leafManager = new LeafManager();
            progressBar.setVisibility(View.VISIBLE);
            leafManager.addTeamStaffOrStudent(this, groupId, teamId, adapter.getSelectedIds());
        } else {
            Toast.makeText(getActivity(), "Select Any Student", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity(), msg+"", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onException(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }
    public void selectAll(boolean checked) {
        adapter.selectAll(checked);
    }

    public class AddTeamStudentAdapter extends RecyclerView.Adapter<AddTeamStudentAdapter.ViewHolder> {
        List<StudentRes.StudentData> list;
        private Context mContext;

        public AddTeamStudentAdapter(List<StudentRes.StudentData> list) {
            this.list = list;
        }

        @Override
        public AddTeamStudentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff_add_team, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final AddTeamStudentAdapter.ViewHolder holder, final int position) {
            final StudentRes.StudentData item = list.get(position);

            if (!TextUtils.isEmpty(item.getImage())) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(50, 50).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgTeam,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.img_lead_default.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(50, 50).into(holder.imgTeam, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.img_lead_default.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.img_lead_default.setVisibility(View.VISIBLE);
                                        TextDrawable drawable = TextDrawable.builder()
                                                .buildRound(ImageUtil.getTextLetter(item.getName()), ImageUtil.getRandomColor(position));
                                        holder.img_lead_default.setImageDrawable(drawable);
                                        AppLog.e("Picasso", "Error : ");
                                    }
                                });
                            }
                        });
            } else {
                holder.img_lead_default.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(item.getName()), ImageUtil.getRandomColor(position));
                holder.img_lead_default.setImageDrawable(drawable);
            }

            holder.txt_name.setText(item.getName());
            if(!TextUtils.isEmpty(item._class)){
                holder.txt_count.setText(item.getClass_());
                holder.txt_count.setVisibility(View.VISIBLE);
            }else {
                holder.txt_count.setVisibility(View.GONE);
            }

            if(item.isSelected){
                holder.chk.setChecked(true);
            }else {
                holder.chk.setChecked(false);
            }
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    txtEmpty.setText("No Students found.");
                } else {
                    txtEmpty.setText("");
                }

                return list.size();
            } else {
                txtEmpty.setText("No Students found.");
                return 0;
            }

        }

        public int getSelectedCount() {
            int cnt = 0;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isSelected) {
                    cnt++;
                }
            }
            return cnt;
        }

        public String getSelectedIds() {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isSelected) {
                    stringBuffer.append(list.get(i).getUserId()).append(",");
                }
            }
            return stringBuffer.length() > 0 ? stringBuffer.substring(0, stringBuffer.length() - 1) : "";
        }

        public void selectAll(boolean checked) {
            if(list!=null){
                for (int i=0;i<list.size();i++){
                    list.get(i).isSelected = checked;
                }
            }
            notifyDataSetChanged();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.img_lead)
            ImageView imgTeam;

            @Bind(R.id.img_lead_default)
            ImageView img_lead_default;

            @Bind(R.id.txt_name)
            TextView txt_name;

            @Bind(R.id.txt_count)
            TextView txt_count;

            @Bind(R.id.chk)
            CheckBox chk;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                chk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list.get(getAdapterPosition()).isSelected = chk.isChecked();
                        addRemovedStaff();
                    }
                });
            }
        }
    }

    private void addRemovedStaff() {
        int cnt = adapter.getSelectedCount();
        if (cnt > 0) {
            tvCount.setText(cnt + "");
        } else {
            tvCount.setText("");
        }
    }
}
