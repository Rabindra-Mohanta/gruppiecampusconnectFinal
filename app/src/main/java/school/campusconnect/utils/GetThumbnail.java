package school.campusconnect.utils;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import school.campusconnect.LeafApplication;

public class GetThumbnail extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "GetThumbnail";
    private final String type;
    private GetThumbnailListener thumbnailListener;
    ArrayList<String> thumbnailList = new ArrayList<>();
    ArrayList<String> listFiles;

    public GetThumbnail(ArrayList<String> listFiles, GetThumbnailListener listener,String type) {
        this.listFiles = listFiles;
        this.thumbnailListener = listener;
        this.type = type;
    }

    public static void create(ArrayList<String> fileName, GetThumbnailListener listener,String type) {
        new GetThumbnail(fileName, listener,type).executeOnExecutor(THREAD_POOL_EXECUTOR);
    }

    public static File getThumbnainDir() {
        return LeafApplication.getInstance().getExternalCacheDir();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if(Constants.FILE_TYPE_PDF.equalsIgnoreCase(type)){
            createThumbnailMultiPdf(0);
        }else {
            createThumbnailMultiVideo(0);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        AppLog.e(TAG, "onPostExecute  thumbnailList:" + thumbnailList);
        thumbnailListener.onThumbnail(thumbnailList);
    }

    private void createThumbnailMultiPdf(int index) {
        if (listFiles.size() > 0 && index < listFiles.size()) {
            try {
                String path = listFiles.get(index);
                File file = new File(path);
                PDDocument document = PDDocument.load(file);
                // Create a renderer for the document
                PDFRenderer renderer = new PDFRenderer(document);
                // Render the image to an RGB Bitmap
                Bitmap pageImage = renderer.renderImage(0, 1, Bitmap.Config.RGB_565);
                // Save the render result to an image
                File renderFile = new File(getThumbnainDir(), file.getName().replace(".pdf", ".png"));
                thumbnailList.add(renderFile.getAbsolutePath());
                FileOutputStream fileOut = new FileOutputStream(renderFile);
                pageImage.compress(Bitmap.CompressFormat.PNG, 100, fileOut);
                fileOut.close();
            } catch (Exception e) {
                e.printStackTrace();
                thumbnailList.add("");
            } finally {
                createThumbnailMultiPdf(index+1);
            }
        }
    }
    private void createThumbnailMultiVideo(int index) {
        if (listFiles.size() > 0 && index < listFiles.size()) {
            try {
                String path = listFiles.get(index);
                File file = new File(path);
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
                String filename = file.getName().substring(0, file.getName().lastIndexOf('.'));
                // Save the render result to an image
                File renderFile = new File(getThumbnainDir(), filename+".png");
                thumbnailList.add(renderFile.getAbsolutePath());
                FileOutputStream fileOut = new FileOutputStream(renderFile);
                thumb.compress(Bitmap.CompressFormat.PNG, 100, fileOut);
                fileOut.close();
            } catch (Exception e) {
                e.printStackTrace();
                thumbnailList.add("");
            } finally {
                createThumbnailMultiVideo(index+1);
            }
        }
    }

    public interface GetThumbnailListener {
        void onThumbnail(ArrayList<String> listThumbnails);
    }
}
