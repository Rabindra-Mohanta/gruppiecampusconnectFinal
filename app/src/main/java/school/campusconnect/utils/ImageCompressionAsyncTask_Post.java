package school.campusconnect.utils;

import android.content.Context;
import android.os.AsyncTask;


public class ImageCompressionAsyncTask_Post extends AsyncTask<String, Void, ProfileImage> {
    public interface OnImageCompressed {
        void onCompressedImage(ProfileImage bitmap);
    }

    private Context mContext;
    private int mWidth;
    private int mHeight;
    private OnImageCompressed mOnImageCompressed;
    private boolean isImageWebFormat;

    public ImageCompressionAsyncTask_Post(Context context, int width, int height , boolean isWeb) {
        this.mContext = context;
        mWidth = width;
        mHeight = height;
        isImageWebFormat = isWeb;
    }

    public void setOnImageCompressed(OnImageCompressed listener) {
        this.mOnImageCompressed = listener;
    }


    @Override
    protected ProfileImage doInBackground(String... params) {
        ProfileImage profileImage = new ProfileImage();
        try {
            //profileImage.imageUrl = new ImageUtil(mContext).compressImage(params[0], mWidth, mHeight);
            profileImage.imageUrl = new ImageUtil(mContext).getRealPathFromURI(params[0]);

            profileImage.image = ImageUtil.decodeBitmapFromPath(mContext,profileImage.imageUrl);
            if(isImageWebFormat)
                profileImage.imageString = ImageUtil.encodeTobase64Upload(profileImage.image);
            else
            {
                profileImage.imageString = ImageUtil.encodeTobase64Upload(profileImage.image);
            }
        }catch (ArithmeticException e){}

       AppLog.e("ImageCompression" , "ImageFile : "+profileImage.imageString);
        return profileImage;
    }

    @Override
    protected void onPostExecute(ProfileImage result) {
        super.onPostExecute(result);
        if (mOnImageCompressed != null) {
            mOnImageCompressed.onCompressedImage(result);
        }
    }

}
