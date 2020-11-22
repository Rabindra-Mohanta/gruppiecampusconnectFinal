package school.campusconnect.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;


public class ImageCompressionAsyncTask extends AsyncTask<String, Void, ProfileImage> {
    public interface OnImageCompressed {
        void onCompressedImage(ProfileImage bitmap);
    }

    private Context mContext;
    private int mWidth;
    private int mHeight;
    private OnImageCompressed mOnImageCompressed;
    private boolean isImageWebFormat;

    public ImageCompressionAsyncTask(Context context, int width, int height, boolean isWenb)
    {
        this.mContext = context;
        mWidth = width;
        mHeight = height;
        isImageWebFormat = isWenb;
    }

    public void setOnImageCompressed(OnImageCompressed listener)
    {
        this.mOnImageCompressed = listener;
    }


    @Override
    protected ProfileImage doInBackground(String... params)
    {
        ProfileImage profileImage = new ProfileImage();
       AppLog.e("AAAAAAAA","Param[0]=>"+params[0]);
        if(params[0].contains("commerceforum.gruppie.fileprovider"))
        {
                File mediaStorageDir = new File(
                        Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        ImageUtil.IMAGE_DIRECTORY_NAME);
                String fileName=params[0].substring(params[0].lastIndexOf('/'));
                String realPath=mediaStorageDir.getAbsolutePath()+fileName;
               AppLog.e("AAAAAAA","PATH ==>"+realPath);
            profileImage.imageUrl = realPath;
            profileImage.image = BitmapFactory.decodeFile(realPath);
        }
        else
        {
          //  profileImage.imageUrl = new ImageUtil(mContext).compressImage(params[0], mWidth, mHeight);
            profileImage.imageUrl = new ImageUtil(mContext).getRealPathFromURI(params[0]);
            profileImage.image = ImageUtil.decodeBitmapFromPath(mContext,profileImage.imageUrl);
        }




       AppLog.e("IsImageWeb", String.valueOf(isImageWebFormat));

        if (isImageWebFormat) {
            profileImage.imageString = ImageUtil.encodeTobase64(profileImage.image);
           AppLog.e("Check", "If called");
        } else {
           AppLog.e("Check", "Else called");
            profileImage.imageString = ImageUtil.encodeTobase64Upload(profileImage.image);
        }
        return profileImage;
    }

    @Override
    protected void onPostExecute(ProfileImage result) {
        super.onPostExecute(result);
        if (mOnImageCompressed != null)
        {
            mOnImageCompressed.onCompressedImage(result);
        }
    }

}
