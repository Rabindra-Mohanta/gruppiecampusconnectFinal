package school.campusconnect.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.EBookReadMoreActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.ViewPDFActivity;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ebook.EBooksResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AmazoneRemove;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.views.SMBDialogUtils;

public class EBookPdfFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    EBooksResponse.EBookData data;
    private String mGroupId;
    private String bookId;
    private boolean IsFromHome;
    ClassesAdapter ebookAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss,container,false);
        ButterKnife.bind(this,view);
        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        init();

        return view;
    }
    private void init(){
        if(getArguments()!=null)
        {
            IsFromHome = getArguments().getBoolean("IsFromHome");
            data=new Gson().fromJson(getArguments().getString("data"), EBooksResponse.EBookData.class);
            AppLog.e(TAG,"data : "+data);
            mGroupId = GroupDashboardActivityNew.groupId;
            bookId =data.getBooksId();

            ebookAdapter = new ClassesAdapter(data.subjectBooks);
            rvClass.setAdapter(ebookAdapter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ebookAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(!IsFromHome) {
            inflater.inflate(R.menu.menu_ebook_pdf,menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_delete:
                if (!isConnectionAvailable()) {
                    showNoNetworkMsg();
                    return true;
                }
                if (data == null)
                    return true;

                SMBDialogUtils.showSMBDialogOKCancel(getActivity(), getResources().getString(R.string.smb_delete_book), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showLoadingBar(progressBar);
                    //    progressBar.setVisibility(View.VISIBLE);
                        LeafManager leafManager = new LeafManager();
                        leafManager.deleteEBook(EBookPdfFragment.this, GroupDashboardActivityNew.groupId, data.getBooksId());
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        if(getActivity()==null){
            return;
        }
        ArrayList<String> list = new ArrayList<>();
        for (int i=0;i<data.subjectBooks.size();i++){
            if(data.subjectBooks.get(i).fileName!=null){
                list.addAll(data.subjectBooks.get(i).fileName);
            }
        }
        AmazoneRemove.remove(list);

        hideLoadingBar();
        //progressBar.setVisibility(View.GONE);
        getActivity().finish();
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

    public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder>
    {
        ArrayList<EBooksResponse.SubjectBook> list;
        private Context mContext;

        public ClassesAdapter(ArrayList<EBooksResponse.SubjectBook> list) {
            this.list = list;
        }

        @Override
        public ClassesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_pdf,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ClassesAdapter.ViewHolder holder, final int position) {
            final EBooksResponse.SubjectBook item = list.get(position);

            holder.txt_title.setText(item.getSubjectName());
            holder.txt_content.setText(item.getDescription());
            if(item.fileName.size()>1){
                holder.tvCount.setText("+"+(item.fileName.size()-1));
                holder.tvCount.setVisibility(View.VISIBLE);
                holder.constThumb.setVisibility(View.GONE);
            }else {
                holder.constThumb.setVisibility(View.VISIBLE);
                holder.tvCount.setVisibility(View.GONE);
                if(item.thumbnailImage!=null && item.thumbnailImage.size()>0)
                {
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
            if(list!=null)
            {
                if(list.size()==0)
                {
                    txtEmpty.setText(getResources().getString(R.string.txt_no_ebook_found));
                }
                else {
                    txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                txtEmpty.setText(getResources().getString(R.string.txt_no_ebook_found));
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.txt_title)
            TextView txt_title;
            @Bind(R.id.txt_content)
            TextView txt_content;
            @Bind(R.id.image)
            ImageView image;
            @Bind(R.id.tvCount)
            TextView tvCount;
            @Bind(R.id.constThumb)
            ConstraintLayout constThumb;
            @Bind(R.id.imageThumb)
            ImageView imageThumb;
            @Bind(R.id.imgDownloadPdf)
            ImageView imgDownloadPdf;
            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);

                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(list.get(getAdapterPosition()).fileName.size()>1){
                            Intent i = new Intent(getActivity(), EBookReadMoreActivity.class);
                            i.putExtra("fileName", list.get(getAdapterPosition()).fileName);
                            i.putExtra("thumbnailImage", list.get(getAdapterPosition()).thumbnailImage);
                            i.putExtra("subjectName", list.get(getAdapterPosition()).subjectName);
                            i.putExtra("description", list.get(getAdapterPosition()).description);
                            startActivity(i);
                        }else {
                            Intent i = new Intent(getActivity(), ViewPDFActivity.class);
                            i.putExtra("pdf", list.get(getAdapterPosition()).fileName.get(0));
                            i.putExtra("name", list.get(getAdapterPosition()).subjectName);
                            i.putExtra("thumbnail", list.get(getAdapterPosition()).thumbnailImage.get(0));
                            startActivity(i);
                        }
                    }
                });
            }
        }
    }

}
