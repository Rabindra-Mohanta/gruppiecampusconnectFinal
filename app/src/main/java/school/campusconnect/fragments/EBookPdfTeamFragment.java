package school.campusconnect.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.AddEBookActivity2;
import school.campusconnect.activities.EBookReadMoreActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.ViewPDFActivity;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.EBookClassItem;
import school.campusconnect.datamodel.PostItem;
import school.campusconnect.datamodel.ebook.EBooksTeamResponse;
import school.campusconnect.datamodel.EBookItem;
import school.campusconnect.firebase.SendNotificationGlobal;
import school.campusconnect.firebase.SendNotificationModel;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.views.SMBDialogUtils;

public class EBookPdfTeamFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    private String team_id;
    private String role = "";
    private ArrayList<EBooksTeamResponse.SubjectBook> eBookList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss, container, false);
        ButterKnife.bind(this, view);
        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        init();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDataLocally();
    }

    private void init() {
        if (getArguments() != null) {
            team_id = getArguments().getString("team_id");
            role = getArguments().getString("role");
        }
    }

    private void getDataLocally() {
        eBookList.clear();
        List<EBookItem> list = EBookItem.getAll(team_id, GroupDashboardActivityNew.groupId);
        if (list.size() != 0) {
            for (int i = 0; i < list.size(); i++) {
                EBookItem currentItem = list.get(i);
                EBooksTeamResponse.SubjectBook item = new EBooksTeamResponse.SubjectBook();
                item.subjectName = currentItem.subjectName;
                item.ebookId = currentItem.ebookId;
                item.description = currentItem.description;
                item.fileName = new Gson().fromJson(currentItem.fileName, new TypeToken<ArrayList<String>>() {
                }.getType());
                item.thumbnailImage = new Gson().fromJson(currentItem.thumbnailImage, new TypeToken<ArrayList<String>>() {
                }.getType());
                eBookList.add(item);
            }
            ebookPdfAdapter = new ClassesAdapter(eBookList);
            rvClass.setAdapter(ebookPdfAdapter);

            if ("admin".equalsIgnoreCase(role)) {
                getEBooksList();
            } else {
                if (LeafPreference.getInstance(getContext()).getInt(team_id + "_ebookpush") > 0
                        || LeafPreference.getInstance(getContext()).getBoolean(team_id + "_ebook_delete")) {

                    LeafPreference.getInstance(getContext()).setBoolean(team_id + "_ebook_delete", false);
                    getEBooksList();
                }
            }
        } else {
            getEBooksList();
        }
    }

    private void getEBooksList() {
        if (!isConnectionAvailable()) {
            showNoNetworkMsg();
        }
        LeafManager leafManager = new LeafManager();
        progressBar.setVisibility(View.VISIBLE);
        leafManager.getEBooksForTeam(this, GroupDashboardActivityNew.groupId, team_id);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (LeafPreference.getInstance(getContext()).getBoolean("is_ebook_added")) {
            LeafPreference.getInstance(getContext()).setBoolean("is_ebook_added", false);
            getEBooksList();
        }
    }

    ClassesAdapter ebookPdfAdapter;

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        if (getActivity() == null) {
            return;
        }

        if (apiId == LeafManager.API_EBOOK_DELETE) {
            getEBooksList();
            sendNotification();
        } else {
            AppLog.e(TAG, "apiId-------: " + apiId);
            progressBar.setVisibility(View.GONE);
            EBooksTeamResponse eBooksTeamResponse = (EBooksTeamResponse) response;
            eBookList.clear();
            eBookList.addAll(eBooksTeamResponse.getData());
            ebookPdfAdapter = new ClassesAdapter(eBookList);
            rvClass.setAdapter(ebookPdfAdapter);
            saveToDB(eBooksTeamResponse.getData());

            LeafPreference.getInstance(getContext()).remove(team_id + "_ebookpush");
        }
    }

    private void saveToDB(ArrayList<EBooksTeamResponse.SubjectBook> data) {
        if (data == null)
            return;

        EBookItem.deleteAll();

        for (int i = 0; i < data.size(); i++) {
            EBooksTeamResponse.SubjectBook currentItem = data.get(i);
            EBookItem eBookItem = new EBookItem();
            eBookItem.subjectName = currentItem.subjectName;
            eBookItem.ebookId = currentItem.ebookId;
            eBookItem.description = currentItem.description;
            eBookItem.fileName = new Gson().toJson(currentItem.fileName);
            eBookItem.thumbnailImage = new Gson().toJson(currentItem.thumbnailImage);
            eBookItem.teamId = team_id;
            eBookItem.groupId = GroupDashboardActivityNew.groupId;
            eBookItem.save();
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
        ArrayList<EBooksTeamResponse.SubjectBook> list;
        private Context mContext;

        public ClassesAdapter(ArrayList<EBooksTeamResponse.SubjectBook> list) {
            this.list = list;
        }

        @Override
        public ClassesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_pdf, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ClassesAdapter.ViewHolder holder, final int position) {
            final EBooksTeamResponse.SubjectBook item = list.get(position);
            holder.txt_title.setText(item.getSubjectName());
            holder.txt_content.setText(item.getDescription());
            if (item.fileName.size() > 1) {
                holder.tvCount.setText("+" + (item.fileName.size() - 1));
                holder.tvCount.setVisibility(View.VISIBLE);
                holder.constThumb.setVisibility(View.GONE);
            } else {
                holder.constThumb.setVisibility(View.VISIBLE);
                holder.tvCount.setVisibility(View.GONE);
                if (item.thumbnailImage != null && item.thumbnailImage.size() > 0) {
                    Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.thumbnailImage.get(0))).into(holder.imageThumb);
                }
                if (item.fileName != null && item.fileName.size() > 0) {
                    if (AmazoneDownload.isPdfDownloaded(item.fileName.get(0))) {
                        holder.imgDownloadPdf.setVisibility(View.GONE);
                    } else {
                        holder.imgDownloadPdf.setVisibility(View.VISIBLE);
                    }
                }
            }
            if ("admin".equalsIgnoreCase(role)) {
                holder.iv_delete.setVisibility(View.VISIBLE);
            } else {
                holder.iv_delete.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    txtEmpty.setText("No E-Book Found.");
                } else {
                    txtEmpty.setText("");
                }

                return list.size();
            } else {
                txtEmpty.setText("No E-Book Found.");
                return 0;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.txt_title)
            TextView txt_title;
            @Bind(R.id.txt_content)
            TextView txt_content;
            @Bind(R.id.tvCount)
            TextView tvCount;
            @Bind(R.id.image)
            ImageView image;
            @Bind(R.id.constThumb)
            ConstraintLayout constThumb;
            @Bind(R.id.imageThumb)
            ImageView imageThumb;
            @Bind(R.id.imgDownloadPdf)
            ImageView imgDownloadPdf;
            @Bind(R.id.iv_delete)
            ImageView iv_delete;
            @Bind(R.id.txt_drop_delete)
            TextView txt_drop_delete;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                iv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        txt_drop_delete.setVisibility(View.VISIBLE);
                    }
                });
                txt_drop_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        txt_drop_delete.setVisibility(View.GONE);
                        deletePost(list.get(getAdapterPosition()));
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        txt_drop_delete.setVisibility(View.GONE);
                    }
                });

                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (list.get(getAdapterPosition()).fileName.size() > 1) {
                            Intent i = new Intent(getActivity(), EBookReadMoreActivity.class);
                            i.putExtra("fileName", list.get(getAdapterPosition()).fileName);
                            i.putExtra("thumbnailImage", list.get(getAdapterPosition()).thumbnailImage);
                            i.putExtra("subjectName", list.get(getAdapterPosition()).subjectName);
                            i.putExtra("description", list.get(getAdapterPosition()).description);
                            startActivity(i);
                        } else {
                            Intent i = new Intent(getActivity(), ViewPDFActivity.class);
                            i.putExtra("pdf", list.get(getAdapterPosition()).fileName.get(0));
                            i.putExtra("name", list.get(getAdapterPosition()).subjectName);
                            startActivity(i);
                        }
                    }
                });
            }
        }
    }

    private void deletePost(EBooksTeamResponse.SubjectBook subjectBook) {
        if (!isConnectionAvailable()) {
            showNoNetworkMsg();
        }
        SMBDialogUtils.showSMBDialogOKCancel(getActivity(), "Are you sure you want to delete this E-Books.?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBar.setVisibility(View.VISIBLE);
                LeafManager leafManager = new LeafManager();
                leafManager.deleteEBookTeam(EBookPdfTeamFragment.this, GroupDashboardActivityNew.groupId, team_id, subjectBook.ebookId);
            }
        });
    }

    public class EbookPdfAdapter extends RecyclerView.Adapter<EbookPdfAdapter.ViewHolder> {
        ArrayList<String> list;
        private Context mContext;

        public EbookPdfAdapter(ArrayList<String> fileName) {
            list = fileName;
        }

        @Override
        public EbookPdfAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final EbookPdfAdapter.ViewHolder holder, final int position) {
            holder.txt_name.setText((position + 1) + " Book");
            list.get(position);
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    txtEmpty.setText("No Pdf Found.");
                } else {
                    txtEmpty.setText("");
                }

                return list.size();
            } else {
                txtEmpty.setText("No Pdf Found.");
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.txt_name)
            TextView txt_name;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), ViewPDFActivity.class);
                        i.putExtra("pdf", list.get(getAdapterPosition()));
                        i.putExtra("name", (getAdapterPosition() + 1) + " Book");
                        startActivity(i);
                    }
                });
            }
        }
    }

    private void sendNotification() {

        SendNotificationModel notificationModel = new SendNotificationModel();
        notificationModel.to = "/topics/" + GroupDashboardActivityNew.groupId+"_"+team_id;
        notificationModel.data.title = getResources().getString(R.string.app_name);
        notificationModel.data.body = "E-Book deleted";
        notificationModel.data.Notification_type = "DELETE_EBOOK";
        notificationModel.data.iSNotificationSilent = true;
        notificationModel.data.groupId = GroupDashboardActivityNew.groupId;
        notificationModel.data.teamId = team_id;
        notificationModel.data.createdById = LeafPreference.getInstance(getActivity()).getString(LeafPreference.LOGIN_ID);
        notificationModel.data.postId = "";
        notificationModel.data.postType = "";
        SendNotificationGlobal.send(notificationModel);
    }

}
