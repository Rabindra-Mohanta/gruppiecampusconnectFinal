package school.campusconnect.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import school.campusconnect.BuildConfig;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.utils.AmazoneImageDownload;
import school.campusconnect.utils.AppLog;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.utils.Constants;
import school.campusconnect.views.TouchImageView;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

public class FullScreenActivity extends BaseActivity {

    public static String TAG = "FullScreenActivity";
    @Bind(R.id.ivImage)
    TouchImageView ivImage;

    @Bind(R.id.ivDownload)
    ImageView ivDownload;
    @Bind(R.id.iconBack)
    ImageView iconBack;
    @Bind(R.id.iconRotate)
    ImageView iconRotate;

    String image;

    @Bind(R.id.iconShareExternal)
    ImageView iconShareExternal;

    @Bind(R.id.iconAdd)
    ImageView iconAdd;

    @Bind(R.id.imgCancel)
    ImageView imgCancel;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.progressBar1)
    ProgressBar progressBar1;

    @Bind(R.id.llProgress)
    FrameLayout llProgress;

    String imagePreviewUrl = "";
    AmazoneImageDownload asyncTask;

    private String album_id = "",type = "";
    private boolean isEdit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_add_post_screen);
        ButterKnife.bind(this);

        image = Constants.decodeUrlToBase64(getIntent().getStringExtra("image"));
        album_id = getIntent().getStringExtra("album_id");
        type= getIntent().getStringExtra("type");
        isEdit = getIntent().getBooleanExtra("edit",false);


        if (isEdit)
        {
            iconAdd.setVisibility(View.VISIBLE);
        }
        else
        {
            iconAdd.setVisibility(View.GONE);
        }

        iconAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddGalleryPostActivity.class);
                intent.putExtra("isEdit", true);
                intent.putExtra("album_id", album_id);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });

        Log.e(TAG,"isEdit"+isEdit+"\n type"+type+"\nalbum id"+album_id);
        Log.e(TAG,"image path"+image);
        imagePreviewUrl = LeafPreference.getInstance(this).getString("PREVIEW_URL","https://ik.imagekit.io/mxfzvmvkayv/");
      //  Picasso.with(this).load(image).into(ivImage);

        if(AmazoneImageDownload.isImageDownloaded(image)){
            llProgress.setVisibility(View.GONE);
            ivDownload.setVisibility(View.GONE);
            Picasso.with(this).load(AmazoneImageDownload.getDownloadPath(image)).placeholder(R.drawable.placeholder_image).networkPolicy(NetworkPolicy.OFFLINE).into(ivImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Log.e("Picasso", "Error : ");
                }
            });
        }
        else
        {


            {
                String path = Constants.decodeUrlToBase64(image);
                String newStr = path.substring(path.indexOf("/images")+1);
                Picasso.with(this).load(imagePreviewUrl+newStr+"?tr=w-50").placeholder(R.drawable.placeholder_image).into(ivImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Log.e("Picasso", "Error : ");
                    }
                });
                ivDownload.setVisibility(View.VISIBLE);


            }

            downloadImage();
        }



        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncTask.cancel(true);
            }
        });

        iconShareExternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isDownloaded = true;

                if (!AmazoneImageDownload.isImageDownloaded((image)))
                {
                    isDownloaded = false;
                }

                if (isDownloaded)
                {
                    ArrayList<File> files =new ArrayList<>();

                    files.add(AmazoneImageDownload.getDownloadPath(image));

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

        ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkPermissionForWriteExternal()) {

                    downloadImage();

                } else {
                    requestPermissionForWriteExternal(21);
                }
            }
        });
        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        iconRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float r = ivImage.getRotation();
                r = r+90;
                if(r>360){
                    r=90;
                }
                ivImage.setRotation(r);
            }
        });
    }

    
    private void downloadImage() {

        ivDownload.setVisibility(View.GONE);
        llProgress.setVisibility(View.VISIBLE);
        progressBar1.setVisibility(View.VISIBLE);
        asyncTask = AmazoneImageDownload.download(getApplicationContext(), image, new AmazoneImageDownload.AmazoneDownloadSingleListener() {
            @Override
            public void onDownload(File file) {
                llProgress.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                progressBar1.setVisibility(View.GONE);
                Picasso.with(getApplicationContext()).load(file).placeholder(R.drawable.placeholder_image).into(ivImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Log.e("Picasso", "Error : ");
                    }
                });
            }

            @Override
            public void error(String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        llProgress.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        progressBar1.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), msg + "", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void progressUpdate(int progress, int max) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(progress>0){
                            progressBar1.setVisibility(View.GONE);
                        }
                        progressBar.setProgress(progress);
                    }
                });
            }
        });
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 21:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (img_thumbnail.getText().toString().equalsIgnoreCase(""))
                    downloadImage();
                   AppLog.e("AddPost" + "permission", "granted camera");
                } else {
                   AppLog.e("AddPost" + "permission", "denied camera");
                }

                break;
        }
    }

    public void requestPermissionForWriteExternal(int code) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, getResources().getString(R.string.toast_storage_permission_needed), Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
        }
    }

    private class ImageDownloadAndSave extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... arg0) {
            String name = System.currentTimeMillis() + "_gruppie.jpg";
            downloadImagesToSdCard(image, name);
            return null;
        }

        private void downloadImagesToSdCard(String downloadUrl, String imageName) {
            try {
                URL url = new URL(image);
                String sdCard = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(sdCard, "Gruppie");

                if (!myDir.exists()) {
                    myDir.mkdir();
                   AppLog.v("", "inside mkdir");
                }

                String fname = imageName;
                File file = new File(myDir, fname);
                if (file.exists())
                    file.delete();

                URLConnection ucon = url.openConnection();
                InputStream inputStream = null;
                HttpURLConnection httpConn = (HttpURLConnection) ucon;
                httpConn.setRequestMethod("GET");
                httpConn.connect();

                if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = httpConn.getInputStream();
                }

                FileOutputStream fos = new FileOutputStream(file);
                int totalSize = httpConn.getContentLength();
                int downloadedSize = 0;
                byte[] buffer = new byte[1024];
                int bufferLength = 0;
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                   AppLog.i("Progress:", "downloadedSize:" + downloadedSize + "totalSize:" + totalSize);
                }

                fos.close();
                notifyMediaStoreScanner(file);
               AppLog.d("test", "Image Saved in sdcard..");

            } catch (IOException io) {
                io.printStackTrace();
               AppLog.e("IMAGEERROR", "error is " + io.toString());
            } catch (Exception e) {
                e.printStackTrace();
               AppLog.e("IMAGEERROR", "error is " + e.toString());
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_image_download), Toast.LENGTH_SHORT).show();
            super.onPostExecute(bitmap);
        }
    }

    public final void notifyMediaStoreScanner(final File file) {
        /*final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat) {
            MediaScannerConnection.scanFile(this, new String[]{file.getPath()}, new String[]{"mp3*//*"}, null);
        } else {
            this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                    + Environment.getExternalStorageDirectory())));
        }*/
        try {
            MediaStore.Images.Media.insertImage(this.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
            this.sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

