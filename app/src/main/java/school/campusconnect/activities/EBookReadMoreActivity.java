package school.campusconnect.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.Constants;

public class EBookReadMoreActivity extends BaseActivity{


    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.txt_title)
    TextView txt_title;

    @Bind(R.id.txt_content)
    TextView txt_content;

    @Bind(R.id.rvPdf)
    RecyclerView rvPdf;

    private String TAG="EBookReadMoreActivity";

    ArrayList<String> fileName;
    ArrayList<String> thumbnailImage;
    String subjectName;
    String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebook_more);

        init();

        bindData();
    }

    private void bindData() {

        txt_title.setText(subjectName);
        txt_content.setText(description);

        if(TextUtils.isEmpty(description))
        {
            txt_content.setVisibility(View.GONE);
        }
        else
        {
            txt_content.setVisibility(View.VISIBLE);
        }

        rvPdf.setAdapter(new EbookPdfAdapter(fileName));

    }

    private void init() {
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.ebooks));
        subjectName=getIntent().getStringExtra("subjectName");
        description=getIntent().getStringExtra("description");
        fileName= (ArrayList<String>) getIntent().getSerializableExtra("fileName");
        thumbnailImage= (ArrayList<String>) getIntent().getSerializableExtra("thumbnailImage");
    }

    public class EbookPdfAdapter extends RecyclerView.Adapter<EbookPdfAdapter.ViewHolder>
    {
        ArrayList<String> list;
        private Context mContext;
        public EbookPdfAdapter(ArrayList<String> fileName) {
            list = fileName;
        }

        @Override
        public EbookPdfAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_pdf_item,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final EbookPdfAdapter.ViewHolder holder, final int position) {
            if(thumbnailImage!=null && thumbnailImage.size()>position){
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(thumbnailImage.get(position))).into(holder.imageThumb);
            }
            if (list != null && list.size() > 0) {
                if (AmazoneDownload.isPdfDownloaded(list.get(0))) {
                    holder.imgDownloadPdf.setVisibility(View.GONE);
                } else {
                    holder.imgDownloadPdf.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                return list.size();
            }
            else
            {
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.imageThumb)
            ImageView imageThumb;
            @Bind(R.id.imgDownloadPdf)
            ImageView imgDownloadPdf;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(EBookReadMoreActivity.this, ViewPDFActivity.class);
                        i.putExtra("pdf", list.get(getAdapterPosition()));
                        i.putExtra("thumbnail", thumbnailImage.get(getAdapterPosition()));
                        i.putExtra("name", subjectName);
                        startActivity(i);
                    }
                });
            }
        }
    }

}
