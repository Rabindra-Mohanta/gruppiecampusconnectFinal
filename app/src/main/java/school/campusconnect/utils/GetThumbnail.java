package school.campusconnect.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

import school.campusconnect.BuildConfig;
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
//                String path = listFiles.get(index);
//                File file = new File(path);
                PDDocument document = PDDocument.load(LeafApplication.getInstance().getContentResolver().openInputStream(Uri.parse(listFiles.get(index))));
                // Create a renderer for the document
                PDFRenderer renderer = new PDFRenderer(document);
                // Render the image to an RGB Bitmap
                Bitmap pageImage = renderer.renderImage(0, 1, Bitmap.Config.RGB_565);
                // Save the render result to an image
                File renderFile = new File(getThumbnainDir(), System.currentTimeMillis()+new Random().nextInt()+".png");
                FileOutputStream fileOut = new FileOutputStream(renderFile);
                pageImage.compress(Bitmap.CompressFormat.PNG, 100, fileOut);
                fileOut.close();

                Uri imageCaptureFile;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    imageCaptureFile = FileProvider.getUriForFile(LeafApplication.getInstance(), BuildConfig.APPLICATION_ID + ".fileprovider", renderFile);
                }else {
                    imageCaptureFile = Uri.fromFile(renderFile);
                }
                thumbnailList.add(imageCaptureFile.toString());


            } catch (Exception e) {
                e.printStackTrace();
                AssetManager assetFiles = LeafApplication.getInstance().getAssets();
                try {
                    File renderFile = new File(getThumbnainDir(),"pdf_default.png");
                    if (!renderFile.exists()) {
                        InputStream in = assetFiles.open("images/pdf_default.png");
                        FileOutputStream out = new FileOutputStream(renderFile);
                        copyAssetFiles(in, out);
                    }
                    Uri imageCaptureFile;
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        imageCaptureFile = FileProvider.getUriForFile(LeafApplication.getInstance(), BuildConfig.APPLICATION_ID + ".fileprovider", renderFile);
                    }else {
                        imageCaptureFile = Uri.fromFile(renderFile);
                    }
                    thumbnailList.add(imageCaptureFile.toString());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                createThumbnailMultiPdf(index+1);
            } finally {
                createThumbnailMultiPdf(index+1);
            }
        }
    }
    private static void copyAssetFiles(InputStream in, OutputStream out) {
        try {

            byte[] buffer = new byte[1024];
            int read;

            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void createThumbnailMultiVideo(int index)
    {
        if (listFiles.size() > 0 && index < listFiles.size())
        {
            try
            {
                String path = listFiles.get(index);
                File file = new File(path);

              //  Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
              //  String filename = file.getName().substring(0, file.getName().lastIndexOf('.'));

                MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
                mMMR.setDataSource(LeafApplication.getInstance(), Uri.parse(listFiles.get(index)));
                Bitmap bmp = mMMR.getFrameAtTime();

                // Save the render result to an image
                // TODO : URI : Display Video Thumbnail from URI
                File renderFile = new File(getThumbnainDir(), "thumbnail_"+System.currentTimeMillis()+".png");
                FileOutputStream fileOut = new FileOutputStream(renderFile);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fileOut);
                fileOut.close();


                Uri imageCaptureFile;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    imageCaptureFile = FileProvider.getUriForFile(LeafApplication.getInstance(), BuildConfig.APPLICATION_ID + ".fileprovider", renderFile);
                }else {
                    imageCaptureFile = Uri.fromFile(renderFile);
                }
                thumbnailList.add(imageCaptureFile.toString());

            }
            catch (Exception e)
            {
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
