package school.campusconnect.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.mobileconnectors.s3.transferutility.UploadOptions;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.utils.crop.Crop;
import school.campusconnect.views.SMBDialogUtils;

public abstract class BaseUploadImageFragment extends BaseFragment {
    public String mProfileImage = "";
    public Uri imageCaptureFile;
    public static final String EXTRA_PHOTO_URL = "photo_url";
    public static final String ISGROUP = "isforgroup";
    public static final String ISFORUPLOAD = "isforupload";

    public int COVER_IMAGE = 1;
    public int MINI_IMAGE = 2;
    public int SELECTED_IMAGE_TYPE = 0;
    ProfileImage myProfileImage;

    String TAG = "AMAZON";

    // Indicates that no upload is currently selected
    private static final int INDEX_NOT_CHECKED = -1;

    // The TransferUtility is the primary class for managing transfer to S3
    private TransferUtility transferUtility;

    // The SimpleAdapter adapts the data about transfers to rows in the UI
//    private SimpleAdapter simpleAdapter;

    // A List of all transfers
    private List<TransferObserver> observers;

    /**
     * This map is used to provide data to the SimpleAdapter above. See the
     * fillMap() function for how it relates observers to rows in the displayed
     * activity.
     */
    private ArrayList<HashMap<String, Object>> transferRecordMaps;

    // Which row in the UI is currently checked (if any)
    private int checkedIndex;

    String _finalUrl = "";
    String key = "";
    Bitmap imgBitmap;

    public BaseUploadImageFragment() {

    }

    public void updatePhotoFromUrl(String url) {

        Log.e(TAG,"updatePhotoFromUrl");

        if (!TextUtils.isEmpty(url)) {
            _finalUrl = url;

            Picasso.with(getContext()).load(Constants.decodeUrlToBase64(url)).memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE).into(getImageView(), new Callback() {
                @Override
                public void onSuccess() {
                    BitmapDrawable drawable = (BitmapDrawable) getImageView().getDrawable();
                    setImageString(drawable);
                }

                @Override
                public void onError() {

                }
            });

        }
    }


    abstract public void setImageString(BitmapDrawable drawable);

    abstract public ImageView getImageView();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        transferUtility = AmazoneHelper.getTransferUtility(getActivity());
        checkedIndex = INDEX_NOT_CHECKED;
        transferRecordMaps = new ArrayList<HashMap<String, Object>>();

        initData();

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    public void showPhotoDialog(int resId) {
        SMBDialogUtils.showSMBSingleChoiceDialog(getActivity(),
                R.string.lbl_select_img, resId, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lw = ((AlertDialog) dialog).getListView();
                        switch (lw.getCheckedItemPosition()) {
                            case 0:
                                startCamera(getRequestCode(0));
                                break;
                            case 1:
                                startGallery(getRequestCode(1));
                                break;
                            case 2:
                                removeImage();
                                break;
                        }
                    }
                });
    }

    abstract public void removeImage();

    abstract public int getRequestCode(int id);

    private void startCamera(int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            imageCaptureFile = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".fileprovider", ImageUtil.getOutputMediaFile());
        } else {
            imageCaptureFile = Uri.fromFile(ImageUtil.getOutputMediaFile());
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureFile);
        if (getParentFragment() != null) {
            getParentFragment().startActivityForResult(intent, requestCode);
        } else {
            startActivityForResult(intent, requestCode);
        }

    }


    private void startGallery(int requestCode) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        if (getParentFragment() != null) {
            getParentFragment().startActivityForResult(galleryIntent, requestCode);
        } else {
            startActivityForResult(galleryIntent, requestCode);
        }
    }

    public void onImageSelected(Intent data, int reCode) {
        // When an Image is picked
        Uri selectedImage = data.getData();
        beginCrop(selectedImage, reCode);
    }

    public Uri onImageSelected(Intent data) {
        // When an Image is picked
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        // Get the cursor
        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        // Move to first row
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        final String image = cursor.getString(columnIndex);
        cursor.close();

        Uri sourceImage = Uri.parse("file:////" + image);
        return sourceImage;
    }

    abstract public void beginCrop(Uri source, int code);

    abstract public void setImageToView(ProfileImage profileImage);


    public void handleCrop(int resultCode, Intent result) {
        Log.e("CROP_TRACK", "from profile handleCrop");
        if (resultCode == Activity.RESULT_OK) {
            Log.e("CROP_TRACK", "from profile RESULT_OK");
            final Uri output = Crop.getOutput(result);


            galleryAddPic(output.getPath());
            ImageCompressionAsyncTask
                    imageCompressionAsyncTask = new ImageCompressionAsyncTask(getActivity(), 600, 400, false);

            imageCompressionAsyncTask.setOnImageCompressed(new ImageCompressionAsyncTask.OnImageCompressed() {
                @Override
                public void onCompressedImage(ProfileImage profileImage) {
                    profileImage.imageUrl = output.toString();

                    myProfileImage = profileImage;
                    galleryAddPic(profileImage);

//                    setImageToView(profileImage);


                    Log.e("CROP_TRACK", "from profile setImageToView called " + profileImage.imageUrl);
                    Log.e("CROP_TRACK", "from profile setImageToView called " + profileImage.imageString);
                    Log.e("S3_TRACK", "from profile key is " + key);
                }
            });
            imageCompressionAsyncTask.execute(output.toString());

        } else if (resultCode == Crop.RESULT_ERROR) {
            Log.e("CROP_TRACK", "from profile ERROR");
            Toast.makeText(getActivity(), Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void galleryAddPic(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    public void galleryAddPic(final ProfileImage profileImage) {

        showLoadingDialog(getString(R.string.please_wait));


        key = AmazoneHelper.getAmazonS3Key(Constants.FILE_TYPE_IMAGE);
        beginUpload(profileImage.imageUrl, key);

        /*try {
            ContentResolver test = getActivity().getContentResolver();
            InputStream initialStream = test.openInputStream(contentUri);


            byte[] buffer = new byte[initialStream.available()];
            initialStream.read(buffer);

            File xpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);


            final File targetFile = new File(profileImage.imageUrl, key);
            OutputStream outStream = null;

            outStream = new FileOutputStream(targetFile);

            outStream.write(buffer);

            MediaScannerConnection.scanFile(getActivity(),
                    new String[]{targetFile.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.e("ExternalStorage", "Scanned " + path + ":");
                            Log.e("ExternalStorage", "Scanned file " + targetFile.getPath() + ":");
                            Log.e("ExternalStorage", "-> uri=" + uri);

                            beginUpload(targetFile.getPath(), key);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ERROR", "error is " + e.toString());
        }*/

        /*final File finalTargetFile = targetFile;
        MediaScannerConnection.scanFile(getActivity(),
                new String[]{f.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.e("ExternalStorage", "Scanned " + path + ":");
                        Log.e("ExternalStorage", "-> uri=" + uri);

                        beginUpload(finalTargetFile.getPath(), key);
                    }
                });*/
    }


    public String getmProfileImage() {
        return _finalUrl;
    }

    //===========================================================================

    /*
     * Begins to upload the file specified by the file path.
     */
    private void beginUpload(String filePath, String key) {
        Log.e("KEYY", "key is " + key);
        if (filePath == null) {
            Log.e("UPLOADTEST", "filepath null");
            Toast.makeText(getActivity(), "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();
            return;
        }
        TransferObserver observer ;
        UploadOptions option = UploadOptions.
                builder().bucket(AmazoneHelper.BUCKET_NAME).
                cannedAcl(CannedAccessControlList.PublicRead).build();
        try {
            observer = transferUtility.upload(key,
                    getContext().getContentResolver().openInputStream(Uri.parse(filePath)), option);

            /*
             * Note that usually we set the transfer listener after initializing the
             * transfer. However it isn't required in this sample app. The flow is
             * click upload button -> start an activity for image selection
             * startActivityForResult -> onActivityResult -> beginUpload -> onResume
             * -> set listeners to in progress transfers.
             */

            observer.setTransferListener(new UploadListener());
            Log.e("UPLOADTEST", "observer started");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e("UPLOADTEST", "upload started");

    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /*
     * A TransferListener class that can listen to a upload task and be notified
     * when the status changes.
     */
    private class UploadListener implements TransferListener {

        // Simply updates the UI list when notified.
        @Override
        public void onError(int id, Exception e) {
            Log.e(TAG, "Error during upload: " + id, e);
//            updateList();
            hideLoadingDialog();
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.e(TAG, String.format("onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent));
//            updateList();
        }

        @Override
        public void onStateChanged(int id, TransferState newState) {
            Log.e(TAG, "onStateChanged: " + id + ", " + newState.name());

            if (newState.toString().equalsIgnoreCase("COMPLETED")) {
                updateList();
                hideLoadingDialog();
            }

        }
    }

    private void updateList() {

        _finalUrl = AmazoneHelper.BUCKET_NAME_URL + key;

        Log.e("FINALURL", "url is " + _finalUrl);

        _finalUrl = Constants.encodeStringToBase64(_finalUrl);

        Log.e("FINALURL", "encoded url is " + _finalUrl);

        myProfileImage.imageString = _finalUrl;
        Log.e("FINALURL", "url from myProfileImage is " + myProfileImage.imageString);
        setImageToView(myProfileImage);
        TransferObserver observer = null;
        HashMap<String, Object> map = null;
        for (int i = 0; i < observers.size(); i++) {
            observer = observers.get(i);
            map = transferRecordMaps.get(i);
            //Util.fillMap(map, observer, i == checkedIndex);
        }
//        simpleAdapter.notifyDataSetChanged();
    }

    /**
     * Gets all relevant transfers from the Transfer Service for populating the
     * UI
     */
    private void initData() {
        transferRecordMaps.clear();
        // Use TransferUtility to get all upload transfers.
        observers = transferUtility.getTransfersWithType(TransferType.UPLOAD);
        TransferListener listener = new UploadListener();
        for (TransferObserver observer : observers) {
            // For each transfer we will will create an entry in
            // transferRecordMaps which will display
            // as a single row in the UI
            HashMap<String, Object> map = new HashMap<String, Object>();
            // Util.fillMap(map, observer, false);
            transferRecordMaps.add(map);

            // Sets listeners to in progress transfers
            if (TransferState.WAITING.equals(observer.getState())
                    || TransferState.WAITING_FOR_NETWORK.equals(observer.getState())
                    || TransferState.IN_PROGRESS.equals(observer.getState())) {
                observer.setTransferListener(listener);
            }
        }

    }

}
