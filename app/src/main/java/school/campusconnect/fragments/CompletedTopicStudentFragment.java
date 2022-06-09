package school.campusconnect.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.adapters.ReadUnReadUserAdapter;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ReadUnreadResponse;
import school.campusconnect.datamodel.chapter.ChapterRes;
import school.campusconnect.datamodel.student.StudentRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.MixOperations;

public class CompletedTopicStudentFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
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
    private String group_id;
    private String team_id;
    private String subject_id;
    private ChapterRes.TopicData item;
    private ArrayList<StudentRes.StudentData> list;
    private ArrayList<String> selectedStudentIds = new ArrayList<>();
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_completed_topic_student,container,false);

        init();

        getUsers();

        return view;
    }

    private void getUsers() {
        showLoadingBar(progressBar,false);
        leafManager.getStudents(this, GroupDashboardActivityNew.groupId, team_id);
    }

    private void init() {
        ButterKnife.bind(this,view);
        leafManager = new LeafManager();
        Bundle bundle=getArguments();
        if(bundle!=null)
        {
            item=new Gson().fromJson(bundle.getString("item"),ChapterRes.TopicData.class);
            group_id=bundle.getString("group_id");
            team_id=bundle.getString("team_id");
            subject_id=bundle.getString("subject_id");
            if(item.studentList!=null){
                for (int i = 0; i < item.studentList.size(); i++) {
                    selectedStudentIds.add(item.studentList.get(i).studentDbId);
                }
                rvReadUsers.setAdapter(new CompletedStudentAdapter(item.studentList));
            }
        }

        rvReadUsers.setLayoutManager(new LinearLayoutManager(mContext));
        rvUnReadUsers.setLayoutManager(new LinearLayoutManager(mContext));
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
        StudentRes res = (StudentRes) response;
        list = res.getData();
        AppLog.e(TAG, "StudentRes " + list);
        if(list!=null)
        {

            Iterator<StudentRes.StudentData> it = list.iterator();
            while (it.hasNext()){
                StudentRes.StudentData current = it.next();
                if(selectedStudentIds.contains(current.studentDbId)){
                    it.remove();
                }
            }
            rvUnReadUsers.setAdapter(new NotCompletedStudentAdapter(list));
            if(list.size()==0)
                tvEmpty2.setVisibility(View.VISIBLE);
            else
                tvEmpty2.setVisibility(View.GONE);
        }else {
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

    public class CompletedStudentAdapter extends RecyclerView.Adapter<CompletedStudentAdapter.ViewHolder> {
        ArrayList<ChapterRes.TopicData.StudentTopicCompleted> list;
        private Context mContext;

        public CompletedStudentAdapter(ArrayList<ChapterRes.TopicData.StudentTopicCompleted> readUsers) {
            this.list = readUsers;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_read_unread,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final ChapterRes.TopicData.StudentTopicCompleted item = list.get(position);
            holder.txtName.setText(list.get(position).name);
            holder.tvDate.setText("");

            final String name = list.get(position).name;
            /*if (!TextUtils.isEmpty(item.getImage())) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(dpToPx(), dpToPx()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgLead,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.imgLead_default.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(dpToPx(), dpToPx()).into(holder.imgLead, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.imgLead_default.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.imgLead_default.setVisibility(View.VISIBLE);
                                        TextDrawable drawable = TextDrawable.builder()
                                                .buildRound(ImageUtil.getTextLetter(name), ImageUtil.getRandomColor(position));
                                        holder.imgLead_default.setImageDrawable(drawable);
                                        AppLog.e("Picasso", "Error : ");
                                    }
                                });
                            }
                        });
            } else {*/
                holder.imgLead_default.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(name), ImageUtil.getRandomColor(position));
                holder.imgLead_default.setImageDrawable(drawable);
//            }

        }
        private int dpToPx() {
            return mContext.getResources().getDimensionPixelSize(R.dimen.group_list_image_size);
        }
        @Override
        public int getItemCount() {
            if(list==null)
                return 0;
            else
                return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.txt_name)
            TextView txtName;

            @Bind(R.id.tvDate)
            TextView tvDate;

            @Bind(R.id.img_lead)
            ImageView imgLead;

            @Bind(R.id.img_lead_default)
            ImageView imgLead_default;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    public class NotCompletedStudentAdapter extends RecyclerView.Adapter<NotCompletedStudentAdapter.ViewHolder> {
        ArrayList<StudentRes.StudentData> list;
        private Context mContext;

        public NotCompletedStudentAdapter(ArrayList<StudentRes.StudentData> readUsers) {
            this.list = readUsers;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_read_unread,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final StudentRes.StudentData item = list.get(position);
            holder.txtName.setText(list.get(position).getName());
            holder.tvDate.setText("");

            final String name = list.get(position).getName();
            if (!TextUtils.isEmpty(item.getImage())) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(dpToPx(), dpToPx()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgLead,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.imgLead_default.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(dpToPx(), dpToPx()).into(holder.imgLead, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.imgLead_default.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.imgLead_default.setVisibility(View.VISIBLE);
                                        TextDrawable drawable = TextDrawable.builder()
                                                .buildRound(ImageUtil.getTextLetter(name), ImageUtil.getRandomColor(position));
                                        holder.imgLead_default.setImageDrawable(drawable);
                                        AppLog.e("Picasso", "Error : ");
                                    }
                                });
                            }
                        });
            } else {
                holder.imgLead_default.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(name), ImageUtil.getRandomColor(position));
                holder.imgLead_default.setImageDrawable(drawable);
            }

        }
        private int dpToPx() {
            return mContext.getResources().getDimensionPixelSize(R.dimen.group_list_image_size);
        }
        @Override
        public int getItemCount() {
            if(list==null)
                return 0;
            else
                return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.txt_name)
            TextView txtName;

            @Bind(R.id.tvDate)
            TextView tvDate;

            @Bind(R.id.img_lead)
            ImageView imgLead;

            @Bind(R.id.img_lead_default)
            ImageView imgLead_default;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
