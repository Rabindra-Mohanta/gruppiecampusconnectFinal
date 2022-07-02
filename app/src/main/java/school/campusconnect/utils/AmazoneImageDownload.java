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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import school.campusconnect.BuildConfig;
import school.campusconnect.LeafApplication;
import school.campusconnect.R;

public class AmazoneImageDownload extends AsyncTask<Void, Integer, String> {
    private static final String TAG = "AmazoneDownload";
    private AmazoneDownloadSingleListener listenerSignle;
    String url;
    File file;
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


                    Log.e(TAG,"File Name"+fileName);
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
                            return false;
                        }
                    }

                    // return file.exists();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else
        {
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
        }
        return false;
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
                    if (key.contains("/")) {
                        String[] splitStr = key.split("/");
                        fileName = splitStr[1];
                    } else {
                        fileName = key;
                    }

                    Log.e(TAG,"File Name Get"+fileName);

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
                            return imageUri;
                        } else {
                            return null;
                        }
                    }
                }
            } catch (Exception e) {
                AppLog.e(TAG,"Exception"+e.getMessage());
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
                    if (key.contains("/")) {
                        String[] splitStr = key.split("/");
                        file = new File(getDirForMedia(splitStr[0]), splitStr[1]);
                    } else {
                        file = new File(getDirForMedia(""), key);
                    }
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        return  FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
                    } else {

                        return Uri.fromFile(file);
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


                          if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                              ContentValues contentValues = new ContentValues();
                              contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, file.getName());
                              contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/*");
                              contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+"/"+LeafApplication.getInstance().getResources().getString(R.string.app_name));
                              ContentResolver resolver = context.getContentResolver();
                              Uri uriPath = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
                              output = resolver.openOutputStream(uriPath);
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
                        }

                        if (connection != null)
                            connection.disconnect();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e.toString()+"  "+ url);
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
        AppLog.e(TAG, "onPostExecute : ");
        if (aVoid == null) {
            if (listenerSignle != null) {

                listenerSignle.onDownload(file);
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

    private static   File getDirForMedia(String folder) {
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
        void onDownload(File file);

        void error(String msg);

        void progressUpdate(int progress, int max);
    }

}
