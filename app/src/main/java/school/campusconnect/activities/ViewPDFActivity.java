package school.campusconnect.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AppLog;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import java.io.File;
import java.util.ArrayList;

import school.campusconnect.R;
import school.campusconnect.views.SMBDialogUtils;

public class ViewPDFActivity extends BaseActivity {

    public static ArrayList<Bitmap> itemData;
    //private VigerPDF vigerPDF;
    private ImageView ivDownload;
    private String title = "";
    private String pdf = "";
    ProgressBar progressBar;
    PDFView pdfView;
    TextView tvCurrentPage;
    FloatingActionButton fabButton;
    private int totalCount;
    private int currentPage=0;
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

        itemData = new ArrayList<>();

        final Intent intent = getIntent();
        if (intent.getExtras() != null) {
            pdf = intent.getExtras().getString("pdf", "");
            AppLog.e("PGFVIEW", "pdf string is " + pdf);

            if (checkPermissionForWriteExternal()) {
                download(pdf);
            } else {
                requestPermissionForWriteExternal(22);
            }
        }
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectPageDialog();
            }
        });
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
                    download(pdf);
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
            Toast.makeText(this, "Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
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
