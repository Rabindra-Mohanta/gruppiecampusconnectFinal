package school.campusconnect.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import school.campusconnect.BuildConfig;
import school.campusconnect.LeafApplication;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.VideoOfflineObject;

public class AmazoneDownload extends AsyncTask<Void, Integer, String> {
    private static final String TAG = "AmazoneDownload";
    private static Context mContext;
    private AmazoneDownloadSingleListener listenerSignle;
    String url;
    File file;

    public AmazoneDownload(String url, AmazoneDownloadSingleListener listener) {
        this.url = url;
        this.listenerSignle = listener;
        PDFBoxResourceLoader.init(LeafApplication.getInstance());
    }

    public static AmazoneDownload download(Context context, String file, AmazoneDownloadSingleListener listener) {
        mContext = context;
        AmazoneDownload asynch = new AmazoneDownload(file, listener);
        asynch.executeOnExecutor(THREAD_POOL_EXECUTOR);
        return asynch;
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
                return file;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isPdfDownloaded(String url) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void removeVideo(Context context, String fileName) {
        try {
            LeafPreference leafPreference = LeafPreference.getInstance(context);
            if (!leafPreference.getString(LeafPreference.OFFLINE_VIDEONAMES).equalsIgnoreCase("")) {
                ArrayList<VideoOfflineObject> list = new Gson().fromJson(leafPreference.getString(LeafPreference.OFFLINE_VIDEONAMES), new TypeToken<ArrayList<VideoOfflineObject>>() {
                }.getType());
                int count = list.size();

                for (Iterator<VideoOfflineObject> iterator = list.iterator(); iterator.hasNext(); ) {
                    VideoOfflineObject offlineObject = iterator.next();
                    try {
                        AppLog.e(TAG, "filename from pref : " + offlineObject.getVideo_filename());
                        if (offlineObject.getVideo_filename() != null && offlineObject.getVideo_filename().equalsIgnoreCase(fileName)) {
                            MixOperations.deleteVideoFile(offlineObject.video_filepath);
                            iterator.remove();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (count != list.size()) {
                    leafPreference.setString(LeafPreference.OFFLINE_VIDEONAMES, new Gson().toJson(list));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected String doInBackground(Void... voids) {
        try {
            Log.e(TAG + "_ViewPDF", "doInBackground called");

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

                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        // allow canceling with back button
                        if (isCancelled()) {
                            input.close();
                            AppLog.e(TAG, "Is Cancelled:::::: ");
                            file.delete();
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

            if (values[0] != 100) {

            }
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
        File mainFolder = LeafApplication.getInstance().AppFilesPath();

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

    public void addGallery(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mContext.sendBroadcast(mediaScanIntent);
    }

    public interface AmazoneDownloadSingleListener {
        void onDownload(File file);

        void error(String msg);

        void progressUpdate(int progress, int max);
    }
}
