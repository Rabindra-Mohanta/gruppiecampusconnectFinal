package school.campusconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.EBookReadMoreActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.ViewPDFActivity;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ebook.EBooksTeamResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;

public class EBookPdfTeamFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    private String team_id;

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

        return view;
    }

    private void init() {
        if (getArguments() != null) {
            team_id = getArguments().getString("team_id");
            LeafManager leafManager = new LeafManager();
            progressBar.setVisibility(View.VISIBLE);
            leafManager.getEBooksForTeam(this, GroupDashboardActivityNew.groupId, team_id);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (ebookPdfAdapter != null) {
            ebookPdfAdapter.notifyDataSetChanged();
        }
    }

    ClassesAdapter ebookPdfAdapter;

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        if (getActivity() == null) {
            return;
        }

        AppLog.e(TAG, "apiId-------: " + apiId);
        progressBar.setVisibility(View.GONE);
        EBooksTeamResponse eBooksTeamResponse = (EBooksTeamResponse) response;
        ebookPdfAdapter = new ClassesAdapter(eBooksTeamResponse.getData());
        rvClass.setAdapter(ebookPdfAdapter);
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

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

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
}
