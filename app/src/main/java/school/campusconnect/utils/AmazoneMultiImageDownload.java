package school.campusconnect.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import school.campusconnect.LeafApplication;


/* NOTE  4/7/22  */

/*NOT USED IF NEED TO USE CHANGE TO MEDIA STORAGE*/



public class AmazoneMultiImageDownload extends AsyncTask<Void, Integer, String> {
    private static final String TAG = "AmzMultiImageDownload";
    private AmazoneDownloadMultiListener listenerMulti;
    ArrayList<String> url;
    ArrayList<File> files;
    Context context;
    private PowerManager.WakeLock mWakeLock;

    public AmazoneMultiImageDownload(Context activity, ArrayList<String> url, AmazoneDownloadMultiListener listener) {
        this.url = url;
        this.context = activity;
        this.listenerMulti = listener;
        this.files = new ArrayList<>();
    }

    public AmazoneMultiImageDownload(Context context) {
    this.context = context;
    }

    public static AmazoneMultiImageDownload download(Context activity, ArrayList<String> file, AmazoneDownloadMultiListener listener) {
        AmazoneMultiImageDownload asynchTask = new AmazoneMultiImageDownload(activity, file, listener);
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
                    file = new File(getDirForMedia(splitStr[0]), splitStr[1] + ".png");
                } else {
                    file = new File(getDirForMedia(""), key + ".png");
                }
                return file.exists();
            }
        }catch (Exception e){
            e.printStackTrace();
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
                    file = new File(getDirForMedia(splitStr[0]), splitStr[1] + ".png");
                } else {
                    file = new File(getDirForMedia(""), key + ".png");
                }
                return file;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

        for(int i = 0 ; i < url.size() ; i++)
        {
            String url_item= url.get(i);
            File file ;
            try {
                if (!TextUtils.isEmpty(url_item)) {
                    url_item = Constants.decodeUrlToBase64(url_item);
                    String key = url_item.replace(AmazoneHelper.BUCKET_NAME_URL, "");
                    Log.e(TAG, "download key :" + key);
                    if (key.contains("/")) {
                        String[] splitStr = key.split("/");
                        file = new File(getDirForMedia(splitStr[0]), splitStr[1] + ".png");
                    } else {
                        file = new File(getDirForMedia(""), key + ".png");
                    }
                    if (!file.exists()) {
                        InputStream input = null;
                        OutputStream output = null;
                        HttpURLConnection connection = null;
                        try {
                            URL u = new URL(url_item);
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
                            files.add(file);

                        } catch (Exception e) {
                            files.add(null);
                            return e.getMessage();
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
                Log.e(TAG, "Exception : " + e.toString());
                return e.getMessage();
            }

        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        AppLog.e(TAG, "progress : " + progress[0]);
        // if we get here, length is known, now set indeterminate to false'
        if (listenerMulti != null) {
            listenerMulti.progressUpdate(progress[0], 100);
        }
    }

    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);
        mWakeLock.release();
        AppLog.e(TAG, "onPostExecute : ");
        if (aVoid == null) {
          //  AppLog.e(TAG, "onPostExecute  :" + file.getAbsolutePath());
            if (listenerMulti != null) {
                listenerMulti.onDownload(files);
            }
        } else {

            if (listenerMulti != null) {
                listenerMulti.error(aVoid);
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

    }

    public void addGallery(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public interface AmazoneDownloadMultiListener {
        void onDownload(ArrayList<File> files);

        void error(String msg);

        void progressUpdate(int progress, int max);
    }

}
