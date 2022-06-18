package school.campusconnect.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.poi.sl.usermodel.Line;
import org.spongycastle.asn1.x509.Holder;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.markcard2.AddMarksReq;
import school.campusconnect.datamodel.markcard2.MarkCardResponse2;
import school.campusconnect.datamodel.subjects.SubjectStaffResponse;
import school.campusconnect.datamodel.test_exam.OfflineTestRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class MarkCardFragment2 extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;
    @Bind(R.id.spSubject)
    public Spinner spSubject;

    @Bind(R.id.tvName)
    public TextView tvName;

    @Bind(R.id.llStudent)
    public LinearLayout llStudent;

    @Bind(R.id.tvRollNo)
    public TextView tvRollNo;

    @Bind(R.id.tvDuration)
    public TextView tvDuration;

    @Bind(R.id.rvMarks)
    public RecyclerView rvMarks;

    @Bind(R.id.tvTotalObt)
    public TextView tvTotalObt;

    private ArrayList<OfflineTestRes.ScheduleTestData> offLineTestList;

    String team_id;
    String role;
    String userId;
    private OfflineTestRes.ScheduleTestData selectedTest;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mark_card_2, container, false);
        ButterKnife.bind(this, view);
        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        team_id = getArguments().getString("team_id");
        role = getArguments().getString("role");
//        role = "student";
        userId = getArguments().getString("userId");

        _init();

        getSubjectList();

        return view;
    }

    private void _init() {
        spSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (offLineTestList != null) {
                    selectedTest = offLineTestList.get(position);
                    getMarkCardList();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getMarkCardList() {
        LeafManager leafManager = new LeafManager();
        showLoadingBar(progressBar);
       // progressBar.setVisibility(View.VISIBLE);
        if ("admin".equalsIgnoreCase(role) || "teacher".equalsIgnoreCase(role)) {
            leafManager.getMarkCard2List(this, GroupDashboardActivityNew.groupId, team_id, selectedTest.offlineTestExamId);
        } else {
            leafManager.getMarkCard2ListForStudent(this, GroupDashboardActivityNew.groupId, team_id, selectedTest.offlineTestExamId, userId);
        }

    }

    private void getSubjectList() {
        LeafManager leafManager = new LeafManager();
        showLoadingBar(progressBar);
        // progressBar.setVisibility(View.VISIBLE);

        leafManager.getOfflineTestList(this, GroupDashboardActivityNew.groupId, team_id);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        if (getActivity() == null)
            return;

        hideLoadingBar();
        //progressBar.setVisibility(View.GONE);
        switch (apiId) {
            case LeafManager.API_OFFLINE_TEST_LIST: {
                OfflineTestRes res = (OfflineTestRes) response;
                offLineTestList = res.data;
                String strSubject[] = new String[offLineTestList.size()];
                for (int i = 0; i < offLineTestList.size(); i++) {
                    strSubject[i] = offLineTestList.get(i).title;
                }
                ArrayAdapter<String> spSubAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item_spinner, R.id.tvItem, strSubject);
                spSubject.setAdapter(spSubAdapter);
                break;
            }
            case LeafManager.API_MARK_CARD_LIST_2: {
                MarkCardResponse2 resMarks = (MarkCardResponse2) response;
                //resMarks.data
                if ("admin".equalsIgnoreCase(role) || "teacher".equalsIgnoreCase(role)) {
                    rvClass.setAdapter(new ClassesAdapter(resMarks.data));
                    rvClass.setVisibility(View.VISIBLE);
                    llStudent.setVisibility(View.GONE);
                } else {
                    llStudent.setVisibility(View.VISIBLE);
                    rvClass.setVisibility(View.GONE);

                    if (resMarks.data != null && resMarks.data.size() > 0) {
                        MarkCardResponse2.MarkCardStudent data = resMarks.data.get(0);
                        tvName.setText(data.studentName);
                        tvRollNo.setText(data.rollNumber);
                        tvDuration.setText(data.examDuration);
                        rvMarks.setAdapter(new MarksAdapter(data.subjectMarksDetails));
                        tvTotalObt.setText(data.totalObtainedMarks + "/" + data.totalMaxMarks);
                    }
                }

                break;
            }
        }

    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        //progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        //progressBar.setVisibility(View.GONE);
    }

    public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder> {
        List<MarkCardResponse2.MarkCardStudent> list;
        private Context mContext;

        public ClassesAdapter(ArrayList<MarkCardResponse2.MarkCardStudent> list) {
            this.list = list;
        }

        @Override
        public ClassesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mark_card_2, parent, false);
            return new ClassesAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ClassesAdapter.ViewHolder holder, final int position) {
            final MarkCardResponse2.MarkCardStudent item = list.get(position);

            if (!TextUtils.isEmpty(item.studentImage)) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.studentImage)).resize(50, 50).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgTeam,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.img_lead_default.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.studentImage)).resize(50, 50).into(holder.imgTeam, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.img_lead_default.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.img_lead_default.setVisibility(View.VISIBLE);
                                        TextDrawable drawable = TextDrawable.builder()
                                                .buildRound(ImageUtil.getTextLetter(item.studentName), ImageUtil.getRandomColor(position));
                                        holder.img_lead_default.setImageDrawable(drawable);
                                        AppLog.e("Picasso", "Error : ");
                                    }
                                });
                            }
                        });
            } else {
                holder.img_lead_default.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(item.studentName), ImageUtil.getRandomColor(position));
                holder.img_lead_default.setImageDrawable(drawable);
            }

            holder.txt_name.setText(item.studentName);
            holder.txt_count.setText("Total : " + item.totalObtainedMarks + "/" + item.totalMaxMarks);

            if(item.isExpand){
                holder.llMarkCard.setVisibility(View.VISIBLE);
                holder.img_tree.setImageResource(R.drawable.arrow_up);
            }else {
                holder.llMarkCard.setVisibility(View.GONE);
                holder.img_tree.setImageResource(R.drawable.arrow_down);
            }
            holder.img_tree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.isExpand) {
                        item.isExpand = false;
                        holder.llMarkCard.setVisibility(View.GONE);
                        holder.img_tree.setImageResource(R.drawable.arrow_down);
                    } else {
                        item.isExpand = true;
                        holder.llMarkCard.setVisibility(View.VISIBLE);
                        holder.img_tree.setImageResource(R.drawable.arrow_up);
                    }
                }
            });



            MarksAdapter marksAdapter = new MarksAdapter(item.subjectMarksDetails);
            holder.rvMarkCard.setAdapter(marksAdapter);
            marksAdapter.addItem(item.isEdit);

            if (item.isEdit)
            {
                marksAdapter.addItem(item.isEdit);
                holder.btnAdd.setText(getResources().getString(R.string.lbl_save));
            }
            else
            {
                marksAdapter.addItem(item.isEdit);
                holder.btnAdd.setText(getResources().getString(R.string.lbl_edit));
            }

            holder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (!item.isEdit)
                    {
                        item.isEdit = true;
                        marksAdapter.addItem(item.isEdit);
                        holder.btnAdd.setText(getResources().getString(R.string.lbl_save));
                    }
                    else
                    {
                        holder.llMarkCard.setVisibility(View.GONE);
                        holder.img_tree.setImageResource(R.drawable.arrow_up);
                        item.isEdit = false;

                        if (item.subjectMarksDetails != null) {
                            int totalObt = 0;

                            for (int i = 0; i < item.subjectMarksDetails.size(); i++) {

                                if (!TextUtils.isEmpty(item.subjectMarksDetails.get(i).obtainedMarks)) {
                                    totalObt += Integer.parseInt(item.subjectMarksDetails.get(i).obtainedMarks);
                                }
                            }
                            item.totalObtainedMarks = totalObt + "";
                        }
                        notifyItemChanged(position);

                        addMarksApi(item);
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    txtEmpty.setText(getResources().getString(R.string.txt_no_student_found));
                } else {
                    txtEmpty.setText("");
                }

                return list.size();
            } else {
                txtEmpty.setText(getResources().getString(R.string.txt_no_student_found));
                return 0;
            }

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

            @Bind(R.id.img_tree)
            ImageView img_tree;

            @Bind(R.id.llMarkCard)
            LinearLayout llMarkCard;

            @Bind(R.id.rvMarkCard)
            RecyclerView rvMarkCard;

            @Bind(R.id.btnAdd)
            Button btnAdd;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

            }
        }
    }

    private void addMarksApi(MarkCardResponse2.MarkCardStudent item) {
        LeafManager leafManager = new LeafManager();
        showLoadingBar(progressBar);
       // progressBar.setVisibility(View.VISIBLE);
        AddMarksReq req = new AddMarksReq();
        req.subjectMarksDetails = item.subjectMarksDetails;
        leafManager.addObtainMark(this, GroupDashboardActivityNew.groupId, team_id, item.offlineTestExamId, item.userId, req);
    }


    String oldValue = "";

    public class MarksAdapter extends RecyclerView.Adapter<MarksAdapter.ViewHolder> {
        List<MarkCardResponse2.SubjectMarkData> list;
        private Context mContext;
        ViewHolder holder;
        boolean isEdit = false;
        public MarksAdapter(ArrayList<MarkCardResponse2.SubjectMarkData> list) {
            this.list = list;
        }

        @Override
        public MarksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mark_detail, parent, false);
            return new MarksAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MarksAdapter.ViewHolder holder, final int position) {

            this.holder = holder;

            final MarkCardResponse2.SubjectMarkData item = list.get(position);

            holder.tvSubject.setText(item.subjectName);
            holder.tvMax.setText(item.maxMarks);
            holder.tvMin.setText(item.minMarks);
            holder.etObtain.setText(item.obtainedMarks);

            if ("admin".equalsIgnoreCase(role) || "teacher".equalsIgnoreCase(role))
            {
                if (!isEdit)
                {
                    holder.etObtain.setEnabled(false);
                    holder.etObtain.setTextColor(getResources().getColor(R.color.grey));
                }
                else
                {
                    holder.etObtain.setEnabled(true);
                    holder.etObtain.setTextColor(getResources().getColor(R.color.black));
                }
            }

            if ("admin".equalsIgnoreCase(role) || "teacher".equalsIgnoreCase(role)) {
            }else {
                holder.etObtain.setEnabled(false);
            }

            holder.etObtain.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    oldValue = s.toString();
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String inputMarkStr = holder.etObtain.getText().toString();
                    if (!inputMarkStr.equalsIgnoreCase(oldValue)){
                        if (!inputMarkStr.trim().isEmpty()) {
                            if (Float.parseFloat(inputMarkStr) <= Float.parseFloat(item.maxMarks)) {
                                item.obtainedMarks = inputMarkStr;
                            } else {
                                item.obtainedMarks = oldValue;
                                holder.etObtain.setText(oldValue);
                                if (oldValue.length() > 0) {
                                    holder.etObtain.setSelection(oldValue.length() - 1);
                                }
                            }
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

        }

        @Override
        public int getItemCount() {
            if (list != null) {
                return list.size();
            } else {
                return 0;
            }

        }

        public void addItem(boolean isEdit) {
            this.isEdit = isEdit;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.tvSubject)
            TextView tvSubject;

            @Bind(R.id.tvMax)
            TextView tvMax;

            @Bind(R.id.tvMin)
            TextView tvMin;

            @Bind(R.id.etObtain)
            EditText etObtain;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

            }
        }
    }
}
