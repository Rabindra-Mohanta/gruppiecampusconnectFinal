package school.campusconnect.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import school.campusconnect.utils.AppLog;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.utils.Constants;

public class FullScreenActivity extends BaseActivity {

    @Bind(R.id.ivImage)
    ImageView ivImage;
    @Bind(R.id.ivDownload)
    ImageView ivDownload;
    @Bind(R.id.iconBack)
    ImageView iconBack;
 @Bind(R.id.iconRotate)
    ImageView iconRotate;

    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_add_post_screen);
        ButterKnife.bind(this);

        image = Constants.decodeUrlToBase64(getIntent().getStringExtra("image"));
        Picasso.with(this).load(image).into(ivImage);

        ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkPermissionForWriteExternal()) {
                    new ImageDownloadAndSave().execute();
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
                    new ImageDownloadAndSave().execute();
                   AppLog.e("AddPost" + "permission", "granted camera");
                } else {
                   AppLog.e("AddPost" + "permission", "denied camera");
                }

                break;
        }
    }

    public void requestPermissionForWriteExternal(int code) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
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
            Toast.makeText(getApplicationContext(), "Image Downloaded.", Toast.LENGTH_SHORT).show();
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

