package school.campusconnect.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import school.campusconnect.BuildConfig;
import school.campusconnect.LeafApplication;
import school.campusconnect.R;
import school.campusconnect.datamodel.Media.ImagePathTBL;
import school.campusconnect.datamodel.notificationList.AllNotificationTable;
import school.campusconnect.datamodel.notificationList.NotificationListRes;

public class AmazoneImageDownload extends AsyncTask<Void, Integer, String> {
    private static final String TAG = "AmazoneImageDownload";
    private AmazoneDownloadSingleListener listenerSignle;
    String url;
    File file;
    Uri uri;
    Context context;
    private PowerManager.WakeLock mWakeLock;

    public AmazoneImageDownload(Context activity, String url, AmazoneDownloadSingleListener listener) {
        this.url = url;
        this.context = activity;
        this.listenerSignle = listener;
    }

    public AmazoneImageDownload(Context context) {
    this.context = context;
    }

    public static AmazoneImageDownload download(Context activity, String file, AmazoneDownloadSingleListener listener) {
        AmazoneImageDownload asynchTask = new AmazoneImageDownload(activity, file, listener);
        asynchTask.executeOnExecutor(THREAD_POOL_EXECUTOR);
        return asynchTask;
    }


    public static boolean isImageDownloaded(String url) {
        try {
            if (!TextUtils.isEmpty(url)) {
                url = Constants.decodeUrlToBase64(url);
                String key = url.replace(AmazoneHelper.BUCKET_NAME_URL, "");
                File file;
                if (key.contains("/")) {
                    String[] splitStr = key.split("/");
                    file = new File(getDirForMedia(splitStr[0]), splitStr[1]);
                } else {
                    file = new File(getDirForMedia(""), key);
                }
                return file.exists();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    public static boolean isImageDownloaded(Context context,String url) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            try {
                if (!TextUtils.isEmpty(url)) {
                    url = Constants.decodeUrlToBase64(url);
                    String key = url.replace(AmazoneHelper.BUCKET_NAME_URL, "");
                    String fileName;
                    if (key.contains("/")) {
                        String[] splitStr = key.split("/");
                        fileName = splitStr[1];
                    } else {
                        fileName = key;
                    }


                    Log.e(TAG,"File isImageDownloaded Name"+fileName);
                    Uri collection = null;

                    collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                    String[] PROJECTION = new String[]{MediaStore.Files.FileColumns.DISPLAY_NAME,
                            MediaStore.MediaColumns.RELATIVE_PATH};

                    String QUERY = MediaStore.Files.FileColumns.DISPLAY_NAME + " like ?";

                    ContentResolver mContentResolver = context.getContentResolver();

                    Cursor cursor = mContentResolver.query(collection, PROJECTION, QUERY , new String[]{fileName}, null);

                    if (cursor != null) {

                        if (cursor.getCount() > 0) {
                            Log.e(TAG,"IS Image Downloaded");
                            return true;
                        } else {
                            Log.e(TAG,"IS Image Downloaded false");
                            return false;
                        }
                    }
                    Log.e(TAG,"IS Image 1 Downloaded false");
                    return false;
                    // return file.exists();
                }
            }catch (Exception e){

                AppLog.e(TAG,"Exception is Image "+e.getMessage());
                e.printStackTrace();
            }
            return false;
        }
        else
        {
            try {
                if (!TextUtils.isEmpty(url)) {
                    url = Constants.decodeUrlToBase64(url);
                    String key = url.replace(AmazoneHelper.BUCKET_NAME_URL, "");
                    File file;
                    if (key.contains("/")) {
                        String[] splitStr = key.split("/");
                        file = new File(getFile(), splitStr[1]);
                    } else {
                        file = new File(getFile(), key);
                    }
                    return file.exists();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }
    }

    public static File getDownloadPath(String url) {
        try {
            if (!TextUtils.isEmpty(url)) {
                url = Constants.decodeUrlToBase64(url);
                String key = url.replace(AmazoneHelper.BUCKET_NAME_URL, "");
                File file;
                if (key.contains("/")) {
                    String[] splitStr = key.split("/");
                    file = new File(getDirForMedia(splitStr[0]), splitStr[1]);
                } else {
                    file = new File(getDirForMedia(""), key);
                }
                Log.e(TAG,"file path"+file.getAbsolutePath());
                return file;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Uri getDownloadPath(Context context,String url) {


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            try {
                if (!TextUtils.isEmpty(url)) {
                    url = Constants.decodeUrlToBase64(url);
                    String key = url.replace(AmazoneHelper.BUCKET_NAME_URL, "");
                    String fileName;
                    File file;
                    if (key.contains("/")) {
                        String[] splitStr = key.split("/");
                        fileName = splitStr[1];
                        file = new File(getDirForMedia(splitStr[0]), splitStr[1]);
                    } else {
                        fileName = key;
                        file = new File(getDirForMedia(""), key);
                    }


                    Log.e(TAG,"file "+file.getAbsolutePath());

                    Uri collection = null;

                    collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                    String[] PROJECTION = new String[]{MediaStore.Images.Media._ID,
                            MediaStore.MediaColumns.RELATIVE_PATH};

                    String QUERY = MediaStore.Files.FileColumns.DISPLAY_NAME + " like ?";

                    ContentResolver mContentResolver = context.getContentResolver();

                    Cursor cursor = mContentResolver.query(collection, PROJECTION, QUERY , new String[]{fileName}, null);

                    if (cursor != null) {

                        //cursor.moveToNext();
                        cursor.moveToFirst();

                        Uri imageUri=
                                ContentUris
                                        .withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)));


                        Log.e(TAG, "imageUri"+imageUri);

                        Log.e(TAG, "cursor id"+cursor.getString(0));

                        Log.e(TAG, "cursor path "+cursor.getString(1));

                        Log.e(TAG, "Url"+collection);

                        Log.e(TAG, "cursor count"+cursor.getCount());

                        String col = collection.toString();

                        Log.e(TAG, "path "+imageUri);

                        if (cursor.getCount() > 0) {

                            Log.e(TAG, "file saved : " + file.getAbsolutePath());
                            Log.e(TAG, "file getName : " + file.getName());

                                if (ImagePathTBL.getLastInserted(file.getName()).size() == 0)
                                {
                                    ImagePathTBL imagePathTBL = new ImagePathTBL();
                                    imagePathTBL.fileName = file.getName();
                                    imagePathTBL.url = file.getAbsolutePath();
                                    imagePathTBL.save();
                                }

                            return imageUri;

                        } else {
                            return null;
                        }
                    }
                }
            } catch (Exception e) {
                AppLog.e(TAG,"Exception get download"+e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
        else
        {
            try {
                if (!TextUtils.isEmpty(url)) {
                    url = Constants.decodeUrlToBase64(url);
                    String key = url.replace(AmazoneHelper.BUCKET_NAME_URL, "");
                    File file;
                    File file2;
                    String fileName;

                    if (key.contains("/")) {
                        String[] splitStr = key.split("/");
                        fileName = splitStr[1];
                        file = new File(getDirForMedia(splitStr[0]), splitStr[1]);
                    } else {
                        fileName = key;
                        file = new File(getDirForMedia(""), key);
                    }
                    file2 = new File(getFile(),fileName);

                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        ImagePathTBL imagePathTBL = new ImagePathTBL();
                        imagePathTBL.fileName = fileName;
                        imagePathTBL.url = file.getAbsolutePath();
                        imagePathTBL.save();
                        return  FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file2);
                    } else {
                        ImagePathTBL imagePathTBL = new ImagePathTBL();
                        imagePathTBL.fileName = fileName;
                        imagePathTBL.url = file.getAbsolutePath();
                        imagePathTBL.save();
                        return Uri.fromFile(file2);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
    }

    @Override
    protected String doInBackground(Void... voids) {

        try {
            if (!TextUtils.isEmpty(url)) {
                url = Constants.decodeUrlToBase64(url);
                String key = url.replace(AmazoneHelper.BUCKET_NAME_URL, "");
                Log.e(TAG, "download key :" + key);
                if (key.contains("/")) {
                    String[] splitStr = key.split("/");
                    file = new File(getDirForMedia(splitStr[0]), splitStr[1]);
                } else {
                    file = new File(getDirForMedia(""), key);
                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {


                    InputStream input = null;
                    OutputStream output = null;
                    OutputStream output2 = null;
                    HttpURLConnection connection = null;


                    try {
                        URL u = new URL(url);
                        connection = (HttpURLConnection) u.openConnection();
                        connection.connect();

                        // expect HTTP 200 OK, so we don't mistakenly save error report
                        // instead of the file
                        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                            return "Server returned HTTP " + connection.getResponseCode()
                                    + " " + connection.getResponseMessage();
                        }


                        // this will be useful to display download percentage
                        // might be -1: server did not report the length
                        int fileLength = connection.getContentLength();

                        // download the file
                        input = connection.getInputStream();
                        output = new FileOutputStream(file);

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, file.getName());
                        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/*");
                        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+"/"+LeafApplication.getInstance().getResources().getString(R.string.app_name));
                        ContentResolver resolver = context.getContentResolver();
                        Uri uriPath = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
                        AppLog.e(TAG,"URL IMAGE SAVE "+uriPath);
                        output2 = resolver.openOutputStream(uriPath);

                        byte data[] = new byte[4096];
                        long total = 0;
                        int count;
                        while ((count = input.read(data)) != -1) {
                            // allow canceling with back button
                            if (isCancelled()) {
                                input.close();
                                return "Cancel Download";
                            }
                            total += count;
                            // publishing the progress....
                            if (fileLength > 0) // only if total length is known
                                publishProgress((int) (total * 100 / fileLength));
                            output.write(data, 0, count);
                            output2.write(data, 0, count);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Exception : " + e.toString()+"  "+ url);
                        return e.getMessage()+"url path "+url;
                    } finally {
                        try {
                            if (output != null)
                                output.close();
                            if (output2 != null)
                                output2.close();
                            if (input != null)
                                input.close();
                        } catch (IOException ignored) {
                            Log.e(TAG, "Exception : " + ignored.toString()+"  "+ url);
                        }

                        if (connection != null)
                            connection.disconnect();
                    }


                }
                else
                {
                    if (!file.exists()) {

                        InputStream input = null;
                        OutputStream output = null;

                        HttpURLConnection connection = null;
                        try {
                            URL u = new URL(url);
                            connection = (HttpURLConnection) u.openConnection();
                            connection.connect();

                            // expect HTTP 200 OK, so we don't mistakenly save error report
                            // instead of the file
                            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                                return "Server returned HTTP " + connection.getResponseCode()
                                        + " " + connection.getResponseMessage();
                            }


                            // this will be useful to display download percentage
                            // might be -1: server did not report the length
                            int fileLength = connection.getContentLength();

                            // download the file
                            input = connection.getInputStream();
                            output = new FileOutputStream(file);

                            try {

                                File saveImage = new File(getFile(),file.getName());
                                try {
                                    OutputStream outputStream = new FileOutputStream(saveImage);
                                    byte data[] = new byte[4096];
                                    long total = 0;
                                    int count;
                                    while ((count = input.read(data)) != -1) {
                                        // allow canceling with back button
                                        if (isCancelled()) {
                                            input.close();
                                            return "Cancel Download";
                                        }
                                        total += count;
                                        // publishing the progress....
                                        if (fileLength > 0) // only if total length is known
                                        outputStream.write(data, 0, count);

                                    }
                                    if (outputStream != null)
                                        outputStream.close();

                                } catch (FileNotFoundException e) {
                                    AppLog.e(TAG,"FileNotFoundException"+e.getMessage());
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    AppLog.e(TAG,"IOException"+e.getMessage());
                                    e.printStackTrace();
                                }
                            }catch (Exception e)
                            {
                                AppLog.e(TAG,"Exception on Image Media SAve "+e.getMessage());
                            }

                            byte data[] = new byte[4096];
                            long total = 0;
                            int count;
                            while ((count = input.read(data)) != -1) {
                                // allow canceling with back button
                                if (isCancelled()) {
                                    input.close();
                                    return "Cancel Download";
                                }
                                total += count;
                                // publishing the progress....
                                if (fileLength > 0) // only if total length is known
                                    publishProgress((int) (total * 100 / fileLength));
                                output.write(data, 0, count);

                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Exception : " + e.toString()+"  "+ url);
                            return e.getMessage()+"url path "+url;
                        } finally {
                            try {
                                if (output != null)
                                    output.close();

                                if (input != null)
                                    input.close();
                            } catch (IOException ignored) {
                                Log.e(TAG, "Exception : " + ignored.toString()+"  "+ url);
                            }

                            if (connection != null)
                                connection.disconnect();
                        }
                    }
                }

            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in Download: " + e.toString()+"  "+ url);
            return e.getMessage()+"url path "+url;
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        AppLog.e(TAG, "progress : " + progress[0]);
        // if we get here, length is known, now set indeterminate to false'
        if (listenerSignle != null) {
            listenerSignle.progressUpdate(progress[0], 100);
        }
    }

    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);
        mWakeLock.release();
        AppLog.e(TAG, "onPostExecute : "+file);
        if (aVoid == null) {
            if (listenerSignle != null) {

                listenerSignle.onDownload(getDownloadPath(context,url));
            }
        } else {
            if (file != null) {
                file.delete();
            }
            if (listenerSignle != null) {
                listenerSignle.error(aVoid);
            }
        }

    }

    private static  File getDirForMedia(String folder) {
        File mainFolder = LeafApplication.getInstance().AppFilesPath();
        if (!mainFolder.exists()) {
            mainFolder.mkdir();
        }

        if (TextUtils.isEmpty(folder)) {
            return mainFolder;
        } else {
            File file = new File(mainFolder, folder);
            if (!file.exists()) {
                file.mkdir();
            }
            return file;
        }
    }

    private static File getFile(){
        File mainFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), LeafApplication.getInstance().getResources().getString(R.string.app_name));

        if (!mainFolder.exists()) {
            mainFolder.mkdir();
        }
        return mainFolder;
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();
        AppLog.e(TAG, "onCancelled()");
        if (file != null) {
            file.delete();
        }
    }

    public void addGallery(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public interface AmazoneDownloadSingleListener {
        void onDownload(Uri file);

        void error(String msg);

        void progressUpdate(int progress, int max);
    }

}
