package school.campusconnect.utils;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

import school.campusconnect.LeafApplication;

public class AmazoneRemove extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "AmazoneRemove";
    String url;
    ArrayList<String> fileUrls;

    public AmazoneRemove(String url) {
        this.url = url;
    }

    public AmazoneRemove(ArrayList<String> fileUrls) {
        this.fileUrls = fileUrls;
    }

    public static void remove(String file) {
        new AmazoneRemove(file).executeOnExecutor(THREAD_POOL_EXECUTOR);
    }

    public static void remove(ArrayList<String> fileName) {
        new AmazoneRemove(fileName).executeOnExecutor(THREAD_POOL_EXECUTOR);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            if (!TextUtils.isEmpty(url)) {
                url = Constants.decodeUrlToBase64(url);
                String key = url.replace(AmazoneHelper.BUCKET_NAME_URL,"");
                Log.e(TAG, "Remove key :" + key);
                AmazoneHelper.getS3Client(LeafApplication.getInstance()).deleteObject(AmazoneHelper.BUCKET_NAME, key);
            } else if (fileUrls != null) {
                for (int i = 0; i < fileUrls.size(); i++) {
                    String url = fileUrls.get(i);
                    url = Constants.decodeUrlToBase64(url);
                    String key = url.replace(AmazoneHelper.BUCKET_NAME_URL,"");
                    Log.e(TAG, "Remove key :" + key);
                    AmazoneHelper.getS3Client(LeafApplication.getInstance()).deleteObject(AmazoneHelper.BUCKET_NAME, key);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.e(TAG, "Delete Success:");
        url = null;
        fileUrls = null;
    }
}
