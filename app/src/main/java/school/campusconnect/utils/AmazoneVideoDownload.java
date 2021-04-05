package school.campusconnect.utils;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
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

import school.campusconnect.LeafApplication;
import school.campusconnect.R;

public class AmazoneVideoDownload extends AsyncTask<Void, Integer, String> {
    private static final String TAG = "AmazoneDownload";
    private AmazoneDownloadSingleListener listenerSignle;
    String url;
    File file;
    Activity activity;
    private PowerManager.WakeLock mWakeLock;

    public AmazoneVideoDownload(Activity activity, String url, AmazoneDownloadSingleListener listener) {
        this.url = url;
        this.activity = activity;
        this.listenerSignle = listener;
    }

    public static AmazoneVideoDownload download(Activity activity, String file, AmazoneDownloadSingleListener listener) {
        AmazoneVideoDownload asynchTask = new AmazoneVideoDownload(activity, file, listener);
        asynchTask.executeOnExecutor(THREAD_POOL_EXECUTOR);
        return asynchTask;
    }

    public static boolean isVideoDownloaded(String url) {
        try {
            if (!TextUtils.isEmpty(url)) {
                url = Constants.decodeUrlToBase64(url);
                String key = url.replace(AmazoneHelper.BUCKET_NAME_URL, "");
                File file;
                if (key.contains("/")) {
                    String[] splitStr = key.split("/");
                    file = new File(getDirForMedia(splitStr[0]), splitStr[1] + ".mp4");
                } else {
                    file = new File(getDirForMedia(""), key + ".mp4");
                }
                return file.exists();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
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
                    file = new File(getDirForMedia(splitStr[0]), splitStr[1] + ".mp4");
                } else {
                    file = new File(getDirForMedia(""), key + ".mp4");
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
            AppLog.e(TAG, "onPostExecute  :" + file.getAbsolutePath());
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

    private static File getDirForMedia(String folder) {
        File mainFolder = new File(Environment.getExternalStorageDirectory(), LeafApplication.getInstance().getResources().getString(R.string.app_name));
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

    public interface AmazoneDownloadSingleListener {
        void onDownload(File file);

        void error(String msg);

        void progressUpdate(int progress, int max);
    }

}