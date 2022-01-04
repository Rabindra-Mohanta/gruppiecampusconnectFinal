package school.campusconnect.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amulyakhare.textdrawable.TextDrawable;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.CreateGroupReguest;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.GroupDetailResponse;
import school.campusconnect.datamodel.GroupItem;
import school.campusconnect.datamodel.GroupValidationError;
import school.campusconnect.datamodel.gruppiecontacts.AllContactModel;
import school.campusconnect.datamodel.gruppiecontacts.GruppieContactGroupIdModel;
import school.campusconnect.datamodel.gruppiecontacts.LeaveResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneHelper;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.crop.Crop;
import school.campusconnect.views.SMBDialogUtils;

public class AboutGroupActivity2 extends BaseActivity implements LeafManager.OnCommunicationListener, LeafManager.OnAddUpdateListener<GroupValidationError> {
    private static final String TAG = "AboutGroupActivity2";

    @Bind(R.id.appBar)
    AppBarLayout appBarLayout;

    @Bind(R.id.img_logo)
    ImageView imgLogo;

    @Bind(R.id.img_logo_default)
    ImageView imgLogo_Default;

    @Bind(R.id.tv_title_groupdetail)
    TextView tvGroupName;

    @Bind(R.id.tv_groupdetail_creator)
    TextView tvGroupCreator;

    @Bind(R.id.tv_title_toolbar)
    TextView tvTitleToolbar;


    @Bind(R.id.tv_posts)
    TextView tvNumOfPosts;

    @Bind(R.id.tv_comments)
    TextView tvNumOfComments;

    @Bind(R.id.txt_about)
    TextView txtAbout;

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    GroupItem item;
    String title;
    String id;
    int totalUsers;
    LeafManager manager;

    Menu menu;
    private final int IMAGE_1_CAMERA_REQ_CODE = 1;
    private final int IMAGE_1_GALLERY_REQ_CODE = 2;
    private Uri imageCaptureFile;
    private TransferUtility transferUtility;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_group2);
        ButterKnife.bind(this);

        init();

        setData();

        if (isConnectionAvailable()) {
            if (progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
            manager.getGroupDetail(this, id);
        } else {
            showNoNetworkMsg();
        }

    }

    private void setData() {
        tvGroupName.setText(title);
        imgLogo_Default.setVisibility(View.VISIBLE);
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(ImageUtil.getTextLetter(title), ImageUtil.getRandomColor(1));
        imgLogo_Default.setImageDrawable(drawable);

        imgLogo_Default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.e("AboutGroup AA", "clicked");
                final Dialog settingsDialog = new Dialog(AboutGroupActivity2.this);
                LayoutInflater inflater = getLayoutInflater();
                @SuppressLint("InflateParams") View newView = (View) inflater.inflate(R.layout.img_layout_profile, null);
                settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(newView);
                ImageView iv = (ImageView) newView.findViewById(R.id.img_popup);
                ImageView ivEdit = (ImageView) newView.findViewById(R.id.ivEdit);
                ivEdit.setVisibility(View.GONE);
                if (item != null)
                    if (item.isAdmin)
                        ivEdit.setVisibility(View.VISIBLE);

                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(ImageUtil.getTextLetter(title), ImageUtil.getRandomColor(1));
                iv.setImageDrawable(drawable);

                ivEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (checkPermissionForWriteExternal()) {
                            showPhotoDialog(R.array.array_image);
                            settingsDialog.dismiss();
                        } else {
                            requestPermissionForWriteExternal(21);
                        }
                    }
                });
                settingsDialog.show();
            }
        });

        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.e("AboutGroup BB", "clicked");
                final Dialog settingsDialog = new Dialog(AboutGroupActivity2.this);
                LayoutInflater inflater = getLayoutInflater();
                View newView = (View) inflater.inflate(R.layout.img_layout_profile, null);
                settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(newView);
                ImageView iv = (ImageView) newView.findViewById(R.id.img_popup);
                ImageView ivEdit = (ImageView) newView.findViewById(R.id.ivEdit);
                ivEdit.setVisibility(View.GONE);

                assert item != null;
                if (item.isAdmin)
                    ivEdit.setVisibility(View.VISIBLE);

                if (item.getImage() != null && !item.getImage().isEmpty()) {
                    Picasso.with(AboutGroupActivity2.this).load(Constants.decodeUrlToBase64(item.image)).into(iv);
                } else {

                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(title, ImageUtil.getRandomColor(1));
                    iv.setImageDrawable(drawable);

                }

                iv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new ImageDownloadAndSave().execute(Constants.decodeUrlToBase64(item.getImage()));
                        return true;
                    }
                });

                ivEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (checkPermissionForWriteExternal()) {
                            showPhotoDialog(R.array.array_image_modify);
                            settingsDialog.dismiss();
                        } else {
                            requestPermissionForWriteExternal(21);
                        }
                    }
                });


                settingsDialog.show();

            }
        });
    }

    private void showPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Password");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().equals("012345")) {
                    Intent intent = new Intent(AboutGroupActivity2.this, ClassAddTokenActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(AboutGroupActivity2.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void init() {

        transferUtility = AmazoneHelper.getTransferUtility(this);

        setSupportActionBar(mToolBar);

        manager = new LeafManager();

        id = getIntent().getStringExtra("id");

        title = getIntent().getStringExtra("title");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        setBackEnabled(true);
    }

    public void showPhotoDialog(int resId) {
        SMBDialogUtils.showSMBSingleChoiceDialog(this,
                R.string.lbl_select_img, resId, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lw = ((AlertDialog) dialog).getListView();
                        switch (lw.getCheckedItemPosition()) {
                            case 0:
                                startCamera(IMAGE_1_CAMERA_REQ_CODE);
                                break;
                            case 1:
                                startGallery(IMAGE_1_GALLERY_REQ_CODE);
                                break;

                            case 2:
                                removeImage();
                                break;
                        }
                    }
                });
    }

    private void removeImage() {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
        manager.deleteGroupPic(this, id + "");
    }

    private void startCamera(int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            imageCaptureFile = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", ImageUtil.getOutputMediaFile());
        } else {
            imageCaptureFile = Uri.fromFile(ImageUtil.getOutputMediaFile());
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureFile);
        startActivityForResult(intent, requestCode);

    }


    private boolean checkPermissionForWriteExternal() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            AppLog.e("External" + "permission", "checkpermission , granted");
            return true;
        } else {
            AppLog.e("External" + "permission", "checkpermission , denied");
            return false;
        }
    }

    public void requestPermissionForWriteExternal(int code) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
        }
    }

    private class ImageDownloadAndSave extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... arg0) {
            String name = System.currentTimeMillis() + "_gruppie.jpg";
            String url = arg0[0];
            if (url != "") {
                downloadImagesToSdCard(arg0[0], name);
            } else {

                AppLog.e("AboutActivity2", arg0[0] = "");
            }

            return null;
        }

        private void downloadImagesToSdCard(String downloadUrl, String imageName) {
            try {
                URL url = new URL(downloadUrl);
                String sdCard = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(sdCard, "Gruppie");

                if (!myDir.exists()) {
                    myDir.mkdir();
                    AppLog.v("", "inside mkdir");
                }

                File file = new File(myDir, imageName);
                if (file.exists())
                    file.delete();

                URLConnection ucon = url.openConnection();
                InputStream inputStream = null;
                HttpURLConnection httpConn = (HttpURLConnection) ucon;
                httpConn.setRequestMethod("GET");
                httpConn.connect();

                if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = httpConn.getInputStream();
                }

                FileOutputStream fos = new FileOutputStream(file);
                int totalSize = httpConn.getContentLength();
                int downloadedSize = 0;
                byte[] buffer = new byte[1024];
                int bufferLength = 0;
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                    AppLog.i("Progress:", "downloadedSize:" + downloadedSize + "totalSize:" + totalSize);
                }

                fos.close();
                notifyMediaStoreScanner(file);
                AppLog.d("test", "Image Saved in sdcard..");

            } catch (IOException io) {
                io.printStackTrace();
                AppLog.e("IMAGEERROR", "error is " + io.toString());
            } catch (Exception e) {
                e.printStackTrace();
                AppLog.e("IMAGEERROR", "error is " + e.toString());
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Toast.makeText(getApplicationContext(), "Image Downloaded.", Toast.LENGTH_SHORT).show();
            super.onPostExecute(bitmap);
        }
    }

    public final void notifyMediaStoreScanner(final File file) {
        try {
            MediaStore.Images.Media.insertImage(this.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
            this.sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about_group, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        Intent intent;
        switch (menuItem.getItemId()) {

            case R.id.nav_edit_group:
                intent = new Intent(AboutGroupActivity2.this, CreateAccountActivity.class);
                intent.putExtra("group_item", new Gson().toJson(this.item));
                startActivity(intent);
                break;

            /*case R.id.nav_delete_group:
                if (isConnectionAvailable()) {
                    if (totalUsers > 1) {
                        Toast.makeText(AboutGroupActivity2.this, getString(R.string.msg_group_delete), Toast.LENGTH_SHORT).show();
                        break;
                    }
                    SMBDialogUtils.showSMBDialogOKCancel(AboutGroupActivity2.this, "Are you sure about deleting this group?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           // showLoadingDialog();
                            if(progressBar!=null)
                            progressBar.setVisibility(View.VISIBLE);
                            manager.deleteGroup(AboutGroupActivity2.this, id);
                        }
                    });
                } else {
                    showNoNetworkMsg();
                }
                break;*/

            case R.id.nav_change_admin:
                intent = new Intent(AboutGroupActivity2.this, AllUserListActivity.class);
                intent.putExtra("id", item.id);
                intent.putExtra("change", true);
                startActivity(intent);
                break;

            /*case R.id.nav_leave_group:
                if (isConnectionAvailable()) {
                    if (totalUsers > 1 && item.isAdmin) {
                        Toast.makeText(AboutGroupActivity2.this, getString(R.string.msg_group_leave), Toast.LENGTH_SHORT).show();
                        break;
                    }
                    SMBDialogUtils.showSMBDialogOKCancel(AboutGroupActivity2.this, "Are you sure about leaving this group?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           // showLoadingDialog();
                            if(progressBar!=null)
                            progressBar.setVisibility(View.VISIBLE);
                            manager.leaveGroup(AboutGroupActivity2.this, id);
                        }
                    });
                } else {
                    showNoNetworkMsg();
                }
                break;*/

            case R.id.nav_group_settings:
                intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("id", item.getGroupId());
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LeafPreference.getInstance(this).getBoolean(LeafPreference.ISGROUPUPDATED)) {
            if (isConnectionAvailable()) {
                if (progressBar != null)
                    progressBar.setVisibility(View.VISIBLE);
                manager.getGroupDetail(this, id);
            } else {
                showNoNetworkMsg();
            }
            LeafPreference.getInstance(this).setBoolean(LeafPreference.ISGROUPUPDATED, false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_1_GALLERY_REQ_CODE && resultCode == Activity.RESULT_OK && data != null) {

            onImageSelected(data, 109);

        } else if (requestCode == IMAGE_1_CAMERA_REQ_CODE && resultCode == Activity.RESULT_OK) {
            if (imageCaptureFile != null) {
                beginCrop(imageCaptureFile, 109);

            } else {
                Toast.makeText(this, getString(R.string.msg_unable_get_camera_image), Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == 109) {
            Log.e(TAG, "handle Crop");
            handleCrop(resultCode, data);
        }
    }

    public void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            final Uri output = Crop.getOutput(result);

            galleryAddPic(output.getPath());


        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void beginCrop(Uri source, int code) {
        File outputFile;
        File outputDir = getCacheDir();
        try {
            // Create a media file folderName
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());
            outputFile = File.createTempFile("IMG_" + timeStamp, ".jpg", outputDir);
            Uri destination = Uri.fromFile(outputFile);
            Crop.of(source, destination).withAspect(10, 10).start(this, code);
        } catch (IOException e) {
            e.printStackTrace();
            outputFile = ImageUtil.getOutputMediaFile();
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                Uri destination = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", outputFile);
                Crop.of(source, destination).withAspect(10, 10).start(this, code);
            } else {
                Uri destination = Uri.fromFile(outputFile);
                Crop.of(source, destination).withAspect(10, 10).start(this, code);
            }
        }


    }

    public void onImageSelected(Intent data, int reCode) {
        // When an Image is picked
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        // Get the cursor
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        // Move to first row
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        final String image = cursor.getString(columnIndex);
        cursor.close();

        Uri sourceImage = Uri.parse("file:////" + image);

        beginCrop(sourceImage, reCode);
    }

    public void galleryAddPic(String path) {
        final File f = new File(path);

        MediaScannerConnection.scanFile(this,
                new String[]{f.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.e("ExternalStorage", "Scanned " + path.replaceAll("file:", "") + ":");
                        Log.e("ExternalStorage", "Scanned file " + f.getPath().replaceAll("file:", "") + ":");
                        Log.e("ExternalStorage", "-> uri=" + uri);
                        key = AmazoneHelper.getAmazonS3Key(Constants.FILE_TYPE_IMAGE);
                        beginUpload(path.replaceAll("file:", ""), key);
                    }
                });
    }

    /*
     * Begins to upload the file specified by the file path.
     */
    private void beginUpload(String filePath, String key) {
        Log.e("KEYY", "key is " + key);
        if (filePath == null) {
            Log.e("UPLOADTEST", "filepath null");
            Toast.makeText(this, "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();
            return;
        }
        File file = new File(filePath);
        TransferObserver observer = transferUtility.upload(AmazoneHelper.BUCKET_NAME, key,
                file , CannedAccessControlList.PublicRead);

        Log.e("UPLOADTEST", "upload started");
        /*
         * Note that usually we set the transfer listener after initializing the
         * transfer. However it isn't required in this sample app. The flow is
         * click upload button -> start an activity for image selection
         * startActivityForResult -> onActivityResult -> beginUpload -> onResume
         * -> set listeners to in progress transfers.
         */

        observer.setTransferListener(new UploadListener());
        Log.e("UPLOADTEST", "observer started");
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
            hideLoadingDialog();
            Toast.makeText(AboutGroupActivity2.this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.e(TAG, String.format("onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent));
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

        String _finalUrl = AmazoneHelper.BUCKET_NAME_URL + key;

        Log.e("FINALURL", "url is " + _finalUrl);

        _finalUrl = Constants.encodeStringToBase64(_finalUrl);

        Log.e("FINALURL", "encoded url is " + _finalUrl);


        if (item != null) {
            CreateGroupReguest request = new CreateGroupReguest();
            request.aboutGroup = item.aboutGroup;
            request.name = item.name;
            request.shortDescription = item.shortDescription;
            request.avatar = _finalUrl;
            request.type = item.type;
            request.category = item.category;
            request.subCategory = item.subCategory;
            Log.e(TAG, "Request Post Data :" + request);
            if (progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
            manager.editGroup(AboutGroupActivity2.this, request, id);
        }
    }


    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        // hideLoadingDialog();
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        switch (apiId) {
            case LeafManager.API_ID_LEAVE_GROUP: {
                LeaveResponse res = (LeaveResponse) response;
                AllContactModel.deleteContact(res.data.get(0).groupId + "", res.data.get(0).userId + "");
                GruppieContactGroupIdModel.deleteRow(item.getGroupId() + "", LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.LOGIN_ID));
                LeafPreference.getInstance(this).setBoolean(LeafPreference.ISGROUPUPDATED, true);
                Intent intent = new Intent(this, GroupListActivityNew.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            }
            case LeafManager.API_ID_DELETE_GROUP: {
                LeafPreference.getInstance(this).setBoolean(LeafPreference.ISGROUPUPDATED, true);
                Toast.makeText(AboutGroupActivity2.this, getResources().getString(R.string.msg_deleted_group), Toast.LENGTH_SHORT).show();

                cleverTapDeleteGroup();

                Intent intent = new Intent(this, GroupListActivityNew.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            }
            case LeafManager.API_ID_EDIT_GROUP:
                Toast.makeText(this, "Successfully updated group details.", Toast.LENGTH_LONG).show();
                if (progressBar != null)
                    progressBar.setVisibility(View.VISIBLE);
                if (isConnectionAvailable()) {
                    manager.getGroupDetail(this, id + "");
                } else {
                    showNoNetworkMsg();
                }
                break;
            case LeafManager.API_ID_DELETE_GROUPPIC:

                Toast.makeText(this, "Successfully updated group details", Toast.LENGTH_LONG).show();
                //  showLoadingDialog();
                if (progressBar != null)
                    progressBar.setVisibility(View.VISIBLE);
                if (isConnectionAvailable()) {
                    manager.getGroupDetail(this, id + "");
                } else {
                    showNoNetworkMsg();
                }
                break;
            default: {
                GroupDetailResponse res = (GroupDetailResponse) response;
                item = res.data.get(0);

                AppLog.e(TAG, "GroupDetailResponse : " + new Gson().toJson(item));

                if (item.isAdmin) {
                    tvGroupName.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            showPasswordDialog();
                            return true;
                        }
                    });
                }

                if (!item.isAdmin) {
                    menu.findItem(R.id.nav_edit_group).setVisible(false);
                    menu.findItem(R.id.nav_change_admin).setVisible(false);
                    menu.findItem(R.id.nav_group_settings).setVisible(false);
                } else {
                    menu.findItem(R.id.nav_edit_group).setVisible(true);
                    menu.findItem(R.id.nav_group_settings).setVisible(true);
                    if (item.isAdminChangeAllowed) {
                        menu.findItem(R.id.nav_change_admin).setVisible(true);
                    } else {
                        menu.findItem(R.id.nav_change_admin).setVisible(false);
                    }
                }
                DatabaseHandler databaseHandler = new DatabaseHandler(this);
                if (databaseHandler.getCount() != 0) {
                    try {
                        String name = databaseHandler.getNameFromNum(item.adminPhone.replaceAll(" ", ""));
                        if (!name.equals("")) {
                            tvGroupCreator.setText("By " + name);
                        } else {
                            tvGroupCreator.setText("By " + res.data.get(0).adminName);
                        }
                    } catch (NullPointerException e) {
                        tvGroupCreator.setText(/*"Created By: " + */res.data.get(0).adminName);
                    }
                } else {
                    AppLog.e("CONTACTSS", "count is 0");
                    tvGroupCreator.setText("By " + res.data.get(0).adminName);
                }
                txtAbout.setText(res.data.get(0).aboutGroup);


                LeafPreference.getInstance(this).setBoolean("IS_GROUP_ICON_CHANGE", true);
                LeafPreference.getInstance(this).setString("NEW_GROUP_ICON", res.data.get(0).image);
                if (res.data.get(0).image != null && !res.data.get(0).image.isEmpty()) {
                    Picasso.with(this).load(Constants.decodeUrlToBase64(res.data.get(0).image)).into(imgLogo);
                    imgLogo_Default.setVisibility(View.GONE);
                    imgLogo.setVisibility(View.VISIBLE);
                } else {
                    imgLogo_Default.setVisibility(View.VISIBLE);
                    imgLogo.setVisibility(View.GONE);
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(1));
                    imgLogo_Default.setImageDrawable(drawable);
                }

                tvNumOfComments.setText("" + res.data.get(0).getTotalCommentsCount());
                tvNumOfPosts.setText("" + res.data.get(0).getTotalPostsCount());

                tvGroupName.setText(item.name);

                tvTitleToolbar.setText(("constituency".equalsIgnoreCase(item.category))?getResources().getString(R.string.lbl_about_constituency):"About School");
                break;
            }
        }
    }

    private void cleverTapDeleteGroup() {
        if (isConnectionAvailable()) {
            try {
                CleverTapAPI cleverTap = CleverTapAPI.getInstance(getApplicationContext());
                AppLog.e("AboutGroup2", "Success to found cleverTap objects=>");
                HashMap<String, Object> deleteGrpAction = new HashMap<String, Object>();
                deleteGrpAction.put("id", id);
                deleteGrpAction.put("group_name", title);
                cleverTap.event.push("Remove Group", deleteGrpAction);
                AppLog.e("AboutGroup2", "Success");

            } catch (CleverTapMetaDataNotFoundException e) {
                AppLog.e("AboutGroup2", "CleverTapMetaDataNotFoundException=>" + e.toString());
                // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
            } catch (CleverTapPermissionsNotSatisfied e) {
                AppLog.e("AboutGroup2", "CleverTapPermissionsNotSatisfied=>" + e.toString());
                // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
            }

        }
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<GroupValidationError> error) {

    }

    @Override
    public void onFailure(int apiId, String msg) {
        // hideLoadingDialog();
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        if (msg.contains("401:Unauthorized")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onException(int apiId, String msg) {
        // hideLoadingDialog();
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }
}
