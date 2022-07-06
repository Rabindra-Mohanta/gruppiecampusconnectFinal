package school.campusconnect.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.utils.crop.Crop;

public class UploadImageFragment extends BaseUploadImageFragment implements View.OnClickListener {
    public static final int REQUEST_LOAD_CAMERA_IMAGE = 101;
    public static final int REQUEST_LOAD_GALLERY_IMAGE = 102;
    ImageView imgService;
    ImageView imgPlus;
    // RelativeLayout imgPlusLayout;
    public boolean isImageChanged = false;
    public boolean isForUpload = false;
    public boolean isGroupOrProfile = false;

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

    String key = "";

    Bitmap imgBitmap;

    public UploadImageFragment() {

    }

    @Override
    public void setImageString(BitmapDrawable drawable) {
        mProfileImage = ImageUtil.encodeTobase64(drawable.getBitmap());
        //imgPlusLayout.setVisibility(View.GONE);
        imgPlus.setVisibility(View.GONE);
    }

    public void setImageFromString(String base64Image) {
        Bitmap bitmap = ImageUtil.decodeFromBase64(base64Image);
        if (bitmap != null) {
            mProfileImage = base64Image;
            //imgPlusLayout.setVisibility(View.GONE);
            imgPlus.setVisibility(View.GONE);
            imgService.setImageBitmap(bitmap);
        }
    }

    public void updateDefaultPhoto() {
        getDefaultImageView().setVisibility(View.VISIBLE);

        if (!isGroupOrProfile)
            getDefaultImageView().setImageResource(R.drawable.icon_default_profile_add);
        else
            getDefaultImageView().setImageResource(R.drawable.icon_default_group_add2);

        Log.e("UploadImageFragment", "Update Default Photo Called Visibility : " + getDefaultImageView().getVisibility());
    }

    public ImageView getDefaultImageView() {
        return imgPlus;
    }

    @Override
    public ImageView getImageView() {
        return imgService;
    }

    public static UploadImageFragment newInstance(String photoUrl, Boolean isForUpload, Boolean isGroupOrProfile) {
        UploadImageFragment fragment = new UploadImageFragment();
        Bundle b = new Bundle();
        b.putBoolean(ISFORUPLOAD, isForUpload);
        b.putString(EXTRA_PHOTO_URL, photoUrl);
        b.putBoolean(ISGROUP, isGroupOrProfile);
        fragment.setArguments(b);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_upload_image, container, false);


        transferUtility = AmazoneHelper.getTransferUtility(getActivity());
        checkedIndex = INDEX_NOT_CHECKED;
        transferRecordMaps = new ArrayList<HashMap<String, Object>>();

        initData();

        imgService = (ImageView) rootView.findViewById(R.id.img_service);
        //imgPlusLayout = (RelativeLayout) rootView.findViewById(R.id.upload_img);
        imgPlus = (ImageView) rootView.findViewById(R.id.img_plus);

        imgService.setOnClickListener(this);
        imgPlus.setOnClickListener(this);
        //imgPlusLayout.setOnClickListener(this);
        SELECTED_IMAGE_TYPE = COVER_IMAGE;
        String url = getArguments().getString(EXTRA_PHOTO_URL, "");

        _finalUrl=url;

        isForUpload = getArguments().getBoolean(ISFORUPLOAD);
        isGroupOrProfile = getArguments().getBoolean(ISGROUP);
        if (url != null && !url.isEmpty()) {
            if (Constants.decodeUrlToBase64(url).contains("http")) {
                updatePhotoFromUrl(url);
            } else {
                setImageFromString(url);
            }

            imgPlus.setVisibility(View.GONE);
            //imgPlusLayout.setVisibility(View.GONE);
        } else {
            //
            updateDefaultPhoto();
        }
        //imgPlus.setVisibility(View.GONE);
        //imgPlusLayout.setVisibility(View.GONE);
        return rootView;

    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_service:
                if (checkPermissionForWriteExternal()) {
                    showPhotoDialog(R.array.array_image_modify);
                } else {
                    requestPermissionForWriteExternal(21);
                }

                break;
            /*case R.id.upload_img:
                showPhotoDialog(R.array.array_image);
                break;*/
            case R.id.img_plus:
                if (checkPermissionForWriteExternal()) {
                    showPhotoDialog(R.array.array_image);
                } else {
                    requestPermissionForWriteExternal(22);
                }
                break;

        }

    }

    @Override
    public void beginCrop(Uri source, int code) {
        File outputFile;
//        File outputDir = getActivity().getCacheDir();
        File outputDir = new File(Environment.getExternalStorageDirectory() + File.separator);
        try {
            // Create a media file folderName
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());
            outputFile = File.createTempFile("IMG_" + timeStamp, ".jpg", outputDir);
            Uri destination = Uri.fromFile(outputFile);
            Crop.of(source, destination).withAspect(10, 10).start(getActivity(), code);
        } catch (IOException e) {
            e.printStackTrace();
            outputFile = ImageUtil.getOutputMediaFile();
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {

                Uri destination = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID+ ".fileprovider", outputFile);
                Crop.of(source, destination).withAspect(10, 10).start(getActivity(), code);
            } else {
                Uri destination = Uri.fromFile(outputFile);
                Crop.of(source, destination).withAspect(10, 10).start(getActivity(), code);
            }
        }


    }

    @Override
    public void removeImage() {
        imgService.setImageBitmap(null);
        isImageChanged = true;
        mProfileImage = "";
        //imgPlusLayout.setVisibility(View.VISIBLE);
        imgPlus.setVisibility(View.VISIBLE);
        updateDefaultPhoto();
        _finalUrl="";
    }

    @Override
    public int getRequestCode(int id) {
        if (id == 0)
            return REQUEST_LOAD_CAMERA_IMAGE;
        else return REQUEST_LOAD_GALLERY_IMAGE;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOAD_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null) {

            Log.e("CHECKLOG", "called if");

            boolean excludeCrop = getArguments().getBoolean("exclude_crop", false);
            if (excludeCrop) {
                Uri uri = onImageSelected(data);
                ImageCompressionAsyncTask imageCompressionAsyncTask = new ImageCompressionAsyncTask(getActivity(), 656, 230, false);
                imageCompressionAsyncTask.setOnImageCompressed(new ImageCompressionAsyncTask.OnImageCompressed() {
                    @Override
                    public void onCompressedImage(ProfileImage profileImage) {
                        if (profileImage.imageString.isEmpty()) {
                            Toast.makeText(getActivity(), getResources().getString(R.string.toast_not_able_to_compress), Toast.LENGTH_SHORT).show();
                        } else {
                            setImageToView(profileImage);
                        }

                    }
                });
                imageCompressionAsyncTask.execute(uri.toString());
            } else {
                onImageSelected(data, 109);
            }

        } else if (requestCode == REQUEST_LOAD_CAMERA_IMAGE && resultCode == Activity.RESULT_OK) {
            Log.e("CHECKLOG", "called else if");

            if (imageCaptureFile != null) {
                galleryAddPic(imageCaptureFile.getPath());
                boolean excludeCrop = getArguments().getBoolean("exclude_crop", false);
                if (excludeCrop) {

                    ImageCompressionAsyncTask imageCompressionAsyncTask = new ImageCompressionAsyncTask(getActivity(), 656, 230, !isForUpload);
                    imageCompressionAsyncTask.setOnImageCompressed(new ImageCompressionAsyncTask.OnImageCompressed() {
                        @Override
                        public void onCompressedImage(ProfileImage profileImage) {
                            if (profileImage.imageString.isEmpty()) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.toast_not_able_to_compress), Toast.LENGTH_SHORT).show();
                            } else {
                                setImageToView(profileImage);
                            }
                        }
                    });
                    imageCompressionAsyncTask.execute(imageCaptureFile.toString());
                } else {
                    beginCrop(imageCaptureFile, 109);
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.msg_unable_get_camera_image), Toast.LENGTH_SHORT).show();
            }


        } else if (requestCode == 109) {
            Log.e("CHECKLOG", "called last");
            handleCrop(resultCode, data);
        }
    }

    @Override
    public void setImageToView(ProfileImage profileImage) {
        //imgPlusLayout.setVisibility(View.GONE);
        imgPlus.setVisibility(View.GONE);
        mProfileImage = profileImage.imageString;
        profileImage.imageString = mProfileImage;
        imgService.setImageBitmap(profileImage.image);
        isImageChanged = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 21:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showPhotoDialog(R.array.array_image_modify);
                    Log.e("AddPost" + "permission", "granted camera");
                } else {
                    Log.e("AddPost" + "permission", "denied camera");
                }
                break;

            case 22:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showPhotoDialog(R.array.array_image);
                    Log.e("AddPost" + "permission", "granted camera");
                } else {
                    Log.e("AddPost" + "permission", "denied camera");
                }
                break;
        }
    }

    private boolean checkPermissionForWriteExternal() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            Log.e("External" + "permission", "checkpermission , granted");
            return true;
        } else {
            Log.e("External" + "permission", "checkpermission , denied");
            return false;
        }
    }

    public void requestPermissionForWriteExternal(int code) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_storage_permission_needed), Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
        }
    }

    //===========================================================================

    /*
     * Gets the file path of the given Uri.
     */
    @SuppressLint("NewApi")
    private String getPath(Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(getActivity(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = getActivity().getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
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

//            mBinding.imgMedia.setImageBitmap(imgBitmap);
        _finalUrl = AmazoneHelper.BUCKET_NAME_URL + key;

        Log.e("FINALURL", "url is " + _finalUrl);

        _finalUrl = Constants.encodeStringToBase64(_finalUrl);

        Log.e("FINALURL", "encoded url is " + _finalUrl);

        TransferObserver observer = null;
        HashMap<String, Object> map = null;
        for (int i = 0; i < observers.size(); i++) {
            observer = observers.get(i);
            map = transferRecordMaps.get(i);
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
