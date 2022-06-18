package school.campusconnect.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import school.campusconnect.BuildConfig;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AmazoneVideoDownload;
import school.campusconnect.utils.AppLog;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import java.io.File;
import java.util.ArrayList;

import school.campusconnect.R;
import school.campusconnect.utils.Constants;
import school.campusconnect.views.SMBDialogUtils;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

public class ViewPDFActivity extends BaseActivity {

    public static ArrayList<Bitmap> itemData;
    //private VigerPDF vigerPDF;
    private ImageView ivDownload;
    private String title = "";
    private String pdf = "";
    private String thumbnailPath = null;
    ProgressBar progressBar;
    PDFView pdfView;
    TextView tvCurrentPage;
    FloatingActionButton fabButton;
    private int totalCount;
    private int currentPage=0;

    RelativeLayout llAfterDownload;
    RelativeLayout llBeforeDownload;
    ImageView imgDownloadPdf;
    ImageView iconShareExternal;
    ImageView thumbnail;
    AmazoneDownload asyncTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);
        ivDownload = (ImageView) findViewById(R.id.ivDownload);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        pdfView = findViewById(R.id.pdfView);
        fabButton = findViewById(R.id.fabButton);
        tvCurrentPage = findViewById(R.id.tvCurrentPage);
        iconShareExternal = findViewById(R.id.iconShareExternal);
        imgDownloadPdf = (ImageView) findViewById(R.id.imgDownloadPdf);
        thumbnail = (ImageView) findViewById(R.id.thumbnail);
        llAfterDownload = (RelativeLayout) findViewById(R.id.llAfterDownload);
        llBeforeDownload = (RelativeLayout) findViewById(R.id.llBeforeDownload);

        itemData = new ArrayList<>();



        if (checkPermissionForWriteExternal()) {
            inits();
        } else {
            requestPermissionForWriteExternal(22);
        }
        ;

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectPageDialog();
            }
        });

        imgDownloadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llBeforeDownload.setVisibility(View.GONE);
                llAfterDownload.setVisibility(View.VISIBLE);
                download(pdf);
            }
        });

        iconShareExternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isDownloaded = true;

                if (!AmazoneDownload.isPdfDownloaded(pdf))
                {
                    isDownloaded = false;
                }


                if (isDownloaded)
                {
                    ArrayList<File> files =new ArrayList<>();

                    files.add(AmazoneDownload.getDownloadPath(pdf));

                    ArrayList<Uri> uris = new ArrayList<>();

                    for(File file: files){

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            uris.add(FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file));
                        } else {
                            uris.add(Uri.fromFile(file));
                        }

                    }

                    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    intent.setType("*/*");
                    intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    startActivity(Intent.createChooser(intent, "Share File"));
                }
                else
                {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.smb_no_file_download),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void startDownload(){
        llBeforeDownload.setVisibility(View.GONE);
        llAfterDownload.setVisibility(View.VISIBLE);
        download(pdf);
    }

    private void inits() {

        final Intent intent = getIntent();
        if (intent.getExtras() != null) {
            pdf = intent.getExtras().getString("pdf", "");
            AppLog.e("PGFVIEW", "pdf string is " + pdf);




           /* if (checkPermissionForWriteExternal()) {
                download(pdf);
            } else {
                requestPermissionForWriteExternal(22);
            }*/
        }

        if (AmazoneDownload.isPdfDownloaded(pdf))
        {
            llAfterDownload.setVisibility(View.VISIBLE);
            llBeforeDownload.setVisibility(View.GONE);
            download(pdf);
        }
        else
        {
            llAfterDownload.setVisibility(View.GONE);
            llBeforeDownload.setVisibility(View.VISIBLE);

            if (intent.getStringExtra("thumbnail") != null && !intent.getStringExtra("thumbnail").isEmpty())
            {
                thumbnailPath = intent.getStringExtra("thumbnail");
                AppLog.e("PGFVIEW", "thumbnailPath " + thumbnailPath);
            }

            if (thumbnailPath != null)
            {
                Glide.with(this).load(Constants.decodeUrlToBase64(thumbnailPath)).into(thumbnail);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(asyncTask!=null){
            asyncTask.cancel(true);
        }
    }

    private void showSelectPageDialog() {
        String[] countNumber = new String[totalCount];
        for (int i=0;i<countNumber.length;i++){
            countNumber[i]=(i+1)+"";
        }
        SMBDialogUtils.showSMBSingleChoiceDialog(this,
                R.string.lbl_select_page, countNumber, currentPage,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lw = ((AlertDialog) dialog).getListView();
                        pdfView.jumpTo(lw.getCheckedItemPosition(),true);
                    }
                });
    }

    private void download(String pdf) {
        View llProgress = findViewById(R.id.llProgress);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        ProgressBar progressBar1 = findViewById(R.id.progressBar1);
        progressBar1.setVisibility(View.VISIBLE);
        llProgress.setVisibility(View.VISIBLE);
        asyncTask = AmazoneDownload.download(this,pdf, new AmazoneDownload.AmazoneDownloadSingleListener() {
            @Override
            public void onDownload(File file) {
                llProgress.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                progressBar1.setVisibility(View.GONE);
                showPdf(file);
            }

            @Override
            public void error(String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        llProgress.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        progressBar1.setVisibility(View.GONE);
                        Toast.makeText(ViewPDFActivity.this, msg + "", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void progressUpdate(int progress, int max) {
                if(progress>0){
                    progressBar1.setVisibility(View.GONE);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(progress);
                    }
                });
            }
        });
        findViewById(R.id.imgCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncTask.cancel(true);
                finish();
            }
        });
    }

    private void showPdf(File file) {
        OnPageChangeListener listener=new OnPageChangeListener() {
            @Override
            public void onPageChanged(int page, int pageCount) {
                tvCurrentPage.setText((page+1)+"/"+pageCount);
                ViewPDFActivity.this.currentPage = page;
                ViewPDFActivity.this.totalCount = pageCount;
            }
        };
        pdfView.fromFile(file)
                .autoSpacing(true)
                .onPageChange(listener)
                .load();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 22:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    inits();
                    AppLog.e("AddPost" + "permission", "granted camera");
                } else {
                    AppLog.e("AddPost" + "permission", "denied camera");
                }
        }
    }

    private boolean checkPermissionForWriteExternal() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            AppLog.e("External" + "permission", "checkpermission , granted");
            return true;
        } else {
            AppLog.e("External" + "permission", "checkpermission , denied");
            return false;
        }
    }

    public void requestPermissionForWriteExternal(int code) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, getResources().getString(R.string.toast_storage_permission_needed), Toast.LENGTH_LONG).show();
            this.finish();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
        }
    }
/*

    private void fromFile(final File file) {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
        itemData.clear();
        adapter.notifyDataSetChanged();
        VigerPDF.cancle();
        vigerPDF.initFromFile(file, new OnResultListener() {

            @Override
            public void resultData(Bitmap data, int numOfPages) {
                AppLog.e("data", "run " + data.toString());
                AppLog.e("data", "numOfPages " + numOfPages);
                itemData.add(data);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                data.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bytes = stream.toByteArray();
                itemByteArray.add(bytes);
                if (itemData.size() == numOfPages) {
                    if (progressBar != null)
                        progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    Intent intent = new Intent(ViewPDFActivity.this, CurlActivity.class);
                    intent.putExtra("pdf", file);
                    intent.putExtra("name", title);
                    startActivity(intent);
                    ViewPDFActivity.this.finish();
                    AppLog.e("data", "activity started");
                }
            }

            @Override
            public void progressData(int progress) {
                AppLog.e("data", "" + progress);
            }

            @Override
            public void failed(Throwable t) {
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
                Toast.makeText(ViewPDFActivity.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                AppLog.e("data", "run " + t.toString());
            }

        });
    }
*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // if (vigerPDF != null) vigerPDF.cancle();
    }

}
