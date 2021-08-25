package school.campusconnect.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.EBookPdfForTeamActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.SelectEBookActivity;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.EBookClassItem;
import school.campusconnect.datamodel.EBookItem;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.ebook.EBooksTeamResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class EBookClassListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "EBookClassListFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;
    String role;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss, container, false);
        ButterKnife.bind(this, view);
        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(getArguments()!=null){
            role = getArguments().getString("role","");
        }

        getDataLocally();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        AppLog.e(TAG , "onStart Called ");
    }
    private void getDataLocally() {
        List<EBookClassItem> list = EBookClassItem.getAll(GroupDashboardActivityNew.groupId);
        if (list.size() != 0) {
            ArrayList<ClassResponse.ClassData> classList= new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                EBookClassItem currentItem = list.get(i);
                ClassResponse.ClassData item = new ClassResponse.ClassData();
                item.category = currentItem.category;
                item.classImage = currentItem.classImage;
                item.className = currentItem.className;
                AppLog.e(TAG ,  "Classnames getting from db  : "+currentItem.className);

                item.countryCode = currentItem.countryCode;
                item.members = currentItem.members;
                item.phone = currentItem.phone;
                item.teacherName = currentItem.teacherName;
                item.setId(currentItem.teamId);
                classList.add(item);
            }
            rvClass.setAdapter(new ClassesAdapter(classList));
        } else {
            getClassList();
        }
    }

    private void getClassList() {
        LeafManager leafManager = new LeafManager();
        progressBar.setVisibility(View.VISIBLE);
        leafManager.getClasses(this, GroupDashboardActivityNew.groupId);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);
        ClassResponse res = (ClassResponse) response;
        List<ClassResponse.ClassData> result = res.getData();
        AppLog.e(TAG, "ClassResponse " + result);

        rvClass.setAdapter(new ClassesAdapter(result));

        saveToDB(result);
    }

    private void saveToDB(List<ClassResponse.ClassData> result) {
        if (result == null)
            return;

        for (int i = 0; i < result.size(); i++) {
            ClassResponse.ClassData currentItem = result.get(i);
            EBookClassItem classItem = new EBookClassItem();
            classItem.category = currentItem.category;
            classItem.classImage = currentItem.classImage;

            AppLog.e(TAG ,  "Classnames getting saved : "+currentItem.className);
            classItem.className = currentItem.className;
            classItem.countryCode = currentItem.countryCode;
            classItem.members = currentItem.members;
            classItem.teacherName = currentItem.teacherName;
            classItem.phone = currentItem.phone;
            classItem.teamId = currentItem.getId();
            classItem.groupId = GroupDashboardActivityNew.groupId;
            classItem.save();
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

    public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder> {
        List<ClassResponse.ClassData> list;
        private Context mContext;

        public ClassesAdapter(List<ClassResponse.ClassData> list) {
            this.list = list;
        }

        @Override
        public ClassesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class_book, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ClassesAdapter.ViewHolder holder, final int position) {
            final ClassResponse.ClassData item = list.get(position);

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
            AppLog.e(TAG ,  "Classnames getting displayed  : "+item.getName());


           /* if (item.ebookId.equals("false")) {
                holder.btnUpload.setVisibility(View.VISIBLE);
                holder.img_tree.setVisibility(View.GONE);
            } else {*/
                holder.btnUpload.setVisibility(View.GONE);
                holder.img_tree.setVisibility(View.VISIBLE);
//            }
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    txtEmpty.setText("No Class found.");
                } else {
                    txtEmpty.setText("");
                }

                return list.size();
            } else {
                txtEmpty.setText("No Class found.");
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.img_lead)
            ImageView imgTeam;
            @Bind(R.id.btnUpload)
            ImageView btnUpload;

            @Bind(R.id.img_lead_default)
            ImageView img_lead_default;

            @Bind(R.id.txt_name)
            TextView txt_name;

            @Bind(R.id.txt_count)
            TextView txt_count;
            @Bind(R.id.img_tree)
            ImageView img_tree;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onTreeClick(list.get(getAdapterPosition()));
                    }
                });
               /* btnUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        add(list.get(getAdapterPosition()));
                    }
                });*/
            }
        }
    }

    private void onTreeClick(ClassResponse.ClassData classData) {
        Intent intent = new Intent(getActivity(), EBookPdfForTeamActivity.class);
        intent.putExtra("group_id", GroupDashboardActivityNew.groupId);
        intent.putExtra("team_id", classData.getId());
        intent.putExtra("title", classData.className);
        intent.putExtra("role", role);
        startActivity(intent);
    }

    private void add(ClassResponse.ClassData classData) {
        Intent intent = new Intent(getActivity(), SelectEBookActivity.class);
        intent.putExtra("group_id", GroupDashboardActivityNew.groupId);
        intent.putExtra("team_id", classData.getId());
        startActivityForResult(intent, 201);
    }
}
