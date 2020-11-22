package school.campusconnect.utils;

import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import school.campusconnect.LeafApplication;
import school.campusconnect.R;

public class AmazoneDownload extends AsyncTask<Void, Integer, String> {
    private static final String TAG = "AmazoneDownload";
    private AmazoneDownloadSingleListener listenerSignle;
    String url;
    File file;

    public AmazoneDownload(String url, AmazoneDownloadSingleListener listener) {
        this.url = url;
        this.listenerSignle = listener;
        PDFBoxResourceLoader.init(LeafApplication.getInstance());
    }

    public static AmazoneDownload download(String file, AmazoneDownloadSingleListener listener) {
        AmazoneDownload asynch = new AmazoneDownload(file, listener);
        asynch.executeOnExecutor(THREAD_POOL_EXECUTOR);
        return asynch;
    }
    public static boolean isPdfDownloaded(String url) {
        try {
            if (!TextUtils.isEmpty(url)) {
                url = Constants.decodeUrlToBase64(url);
                String key = url.replace(AmazoneHelper.BUCKET_NAME_URL, "");
                File file;
                if (key.contains("/")) {
                    String[] splitStr = key.split("/");
                    file = new File(getDirForMedia(splitStr[0]), splitStr[1] + ".pdf");
                } else {
                    file = new File(getDirForMedia(""), key + ".pdf");
                }
                return file.exists();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {

            url = Constants.decodeUrlToBase64(url);
            String key = url.replace(AmazoneHelper.BUCKET_NAME_URL, "");
            Log.e(TAG, "download key :" + key);
            if (key.contains("/")) {
                String[] splitStr = key.split("/");
                file = new File(getDirForMedia(splitStr[0]), splitStr[1] + ".pdf");
            } else {
                file = new File(getDirForMedia(""), key + ".pdf");
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
                } catch (IOException e) {
                    e.printStackTrace();
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
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e.toString());
            return e.getMessage();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        AppLog.e(TAG, "progress : " + values[0]);
        // if we get here, length is known, now set indeterminate to false'
        if (listenerSignle != null) {
            listenerSignle.progressUpdate(values[0], 100);
        }
    }

    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);
        if (file != null) {
            AppLog.e(TAG, "onPostExecute  :" + file.getAbsolutePath());
            if (listenerSignle != null) {
                listenerSignle.onDownload(file);
            }
        } else {
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


    public interface AmazoneDownloadSingleListener {
        void onDownload(File file);

        void error(String msg);

        void progressUpdate(int progress, int max);
    }
}
