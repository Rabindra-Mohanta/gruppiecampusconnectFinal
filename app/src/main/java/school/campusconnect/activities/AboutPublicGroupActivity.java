package school.campusconnect.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import com.google.android.material.appbar.AppBarLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import school.campusconnect.BuildConfig;
import school.campusconnect.utils.AppLog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
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
import school.campusconnect.utils.ImageCompressionAsyncTask;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.ProfileImage;
import school.campusconnect.utils.crop.Crop;
import school.campusconnect.views.SMBDialogUtils;

public class AboutPublicGroupActivity extends BaseActivity implements LeafManager.OnCommunicationListener, LeafManager.OnAddUpdateListener<GroupValidationError> {
    private static final String TAG = "AboutGroupActivity2";//}, AppBarLayout.OnOffsetChangedListener {

    @Bind(R.id.appBar)
    AppBarLayout appBarLayout;
    @Bind(R.id.img_logo)
    ImageView imgLogo;

    @Bind(R.id.img_logo_default)
    ImageView imgLogo_Default;

    @Bind(R.id.tv_title_groupdetail)
    TextView tvGroupTitle;

    @Bind(R.id.tv_members)
    TextView tvMembers;

    @Bind(R.id.tv_posts)
    TextView tvNumOfPosts;

    @Bind(R.id.tv_likes)
    TextView tvNumOfLikes;


    @Bind(R.id.tv_comments)
    TextView tvNumOfComments;


    @Bind(R.id.tv_title_toolbar)
    TextView tvTitleToolbar;

    @Bind(R.id.tv_groupdetail_creator)
    TextView tvGroupCreator;

    @Bind(R.id.txt_about)
    TextView txtAbout;

    @Bind(R.id.btn_join)
    Button btnJoin;

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    GroupItem item;
    String title;
    String createdBy;
    String aboutGroup;
    String image;
    int members;
    int totalPostCount;
    int totalCommentsCount;
    String id;
    int totalUsers;
    LeafManager manager;
    /*FloatingActionButton btnEdit, btnDelete, btnLeave, btn_change_admin;
    FloatingActionsMenu menuMultipleActions;*/
//    Menu menu;
    private final int IMAGE_1_CAMERA_REQ_CODE = 1;
    private final int IMAGE_1_GALLERY_REQ_CODE = 2;
    private String uploadedImg;
    private Uri imageCaptureFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_public_group);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);

        manager = new LeafManager();

        id = getIntent().getStringExtra("id");

        title = getIntent().getStringExtra("title");
        createdBy = getIntent().getStringExtra("createdBy");
        aboutGroup = getIntent().getStringExtra("aboutGroup");
        members = getIntent().getIntExtra("members", 0);
        image = getIntent().getStringExtra("image");
        totalPostCount = getIntent().getIntExtra("totalPostsCount", 0);
        totalCommentsCount = getIntent().getIntExtra("totalCommentsCount", 0);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        setBackEnabled(true);
        imgLogo_Default.setVisibility(View.VISIBLE);
        TextDrawable drawable1 = TextDrawable.builder()
                .buildRound(ImageUtil.getTextLetter(title), ImageUtil.getRandomColor(1));
        imgLogo_Default.setImageDrawable(drawable1);
        tvGroupTitle.setText(title);
        tvTitleToolbar.setText(title);
        // appBarLayout.addOnOffsetChangedListener(this);
        /*if (id == 1) {
            btnLeave.setVisibility(View.GONE);
            total.setVisibility(View.GONE);
            friends.setVisibility(View.GONE);
        }*/

        tvGroupCreator.setText("By " + createdBy);

//            txtCreator.setText("Created By: " + res.data.createdBy);
//            txtShort.setText("Description : " + res.data.shortDescription);
        txtAbout.setText(aboutGroup);


        if (image != null && !image.isEmpty()) {
            Picasso.with(this).load(image).into(imgLogo);
            imgLogo_Default.setVisibility(View.GONE);
        } else {
            imgLogo_Default.setVisibility(View.VISIBLE);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(ImageUtil.getTextLetter(""), ImageUtil.getRandomColor(1));
            imgLogo_Default.setImageDrawable(drawable);
            //imgLogo.setImageResource(R.drawable.icon_gruppie);
        }

        tvNumOfComments.setText("" + totalCommentsCount);
//        tvNumOfLikes.setText(/*"Total Users : " + */"" + totalLikesCount);
        tvNumOfPosts.setText("" + totalPostCount);
        tvMembers.setText("" + members);


        imgLogo_Default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               AppLog.e("AboutGroup AA", "clicked");
                final Dialog settingsDialog = new Dialog(AboutPublicGroupActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View newView = (View) inflater.inflate(R.layout.img_layout_profile, null);
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

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutPublicGroupActivity.this, PublicGroupJoinActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               AppLog.e("AboutGroup BB", "clicked");
                final Dialog settingsDialog = new Dialog(AboutPublicGroupActivity.this);
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
                    Picasso.with(AboutPublicGroupActivity.this).load(item.getImage()).into(iv);
                } else {

                    //holder.img_lead_default.setVisibility(View.VISIBLE);
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(title, ImageUtil.getRandomColor(1));
                    iv.setImageDrawable(drawable);
                    //Picasso.with(mContext).load(R.drawable.icon_default_user).into(iv);

                }

                iv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new ImageDownloadAndSave().execute(item.getImage());
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

        /*if (isConnectionAvailable()) {
            if (progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
            manager.getGroupDetail(this, id);
        } else {
            showNoNetworkMsg();
        }*/

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
                                //openCamera(IMAGE_1_CAMERA_REQ_CODE);
                                startCamera(IMAGE_1_CAMERA_REQ_CODE);
                                break;
                            case 1:
                                //openGallery(IMAGE_1_GALLERY_REQ_CODE);
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
        // showLoadingDialog();
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
        manager.deleteGroupPic(this, id+"");
    }

    /***
     * Open Gallery for Get Image From Gallery
     *
     * @param GALLERY_REQ_CODE
     */
    private void openGallery(int GALLERY_REQ_CODE) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), GALLERY_REQ_CODE);// - See more at: http://www.theappguruz.com/blog/android-take-photo-camera-gallery-code-sample#sthash.0NmKTQM0.dpuf
    }

    /***
     * Open Camera for Capture Image From Camera
     *
     * @param CAMERA_REQ_CODE
     */
    private void openCamera(int CAMERA_REQ_CODE) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_REQ_CODE);
        }
    }

    private void startCamera(int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            imageCaptureFile = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+ ".fileprovider", ImageUtil.getOutputMediaFile());
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

                String fname = imageName;
                File file = new File(myDir, fname);
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
        /*final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat) {
            MediaScannerConnection.scanFile(this, new String[]{file.getPath()}, new String[]{"mp3*//*"}, null);
        } else {
            this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                    + Environment.getExternalStorageDirectory())));
        }*/
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
//        getMenuInflater().inflate(R.menu.menu_about_group, menu);
//        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        Intent intent;
        switch (menuItem.getItemId()) {

            case R.id.nav_edit_group:
                intent = new Intent(AboutPublicGroupActivity.this, CreateAccountActivity.class);
                intent.putExtra("group_item", this.item);
                startActivityForResult(intent, 123);
                break;

           /* case R.id.nav_delete_group:
                if (isConnectionAvailable()) {
                    if (totalUsers > 1) {
                        Toast.makeText(AboutPublicGroupActivity.this, getString(R.string.msg_group_delete), Toast.LENGTH_SHORT).show();
                        break;
                    }
                    SMBDialogUtils.showSMBDialogOKCancel(AboutPublicGroupActivity.this, "Are you sure about deleting this group?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // showLoadingDialog();
                            if (progressBar != null)
                                progressBar.setVisibility(View.VISIBLE);
                            manager.deleteGroup(AboutPublicGroupActivity.this, id);
                        }
                    });
                } else {
                    showNoNetworkMsg();
                }
                break;*/

            case R.id.nav_change_admin:
                intent = new Intent(AboutPublicGroupActivity.this, AllUserListActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("change", true);
                startActivity(intent);
                break;

            /*case R.id.nav_leave_group:
                if (isConnectionAvailable()) {
                    if (totalUsers > 1 && item.isAdmin) {
                        Toast.makeText(AboutPublicGroupActivity.this, getString(R.string.msg_group_leave), Toast.LENGTH_SHORT).show();
                        break;
                    }
                    SMBDialogUtils.showSMBDialogOKCancel(AboutPublicGroupActivity.this, "Are you sure about leaving this group?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // showLoadingDialog();
                            if (progressBar != null)
                                progressBar.setVisibility(View.VISIBLE);
                            manager.leaveGroup(AboutPublicGroupActivity.this, id);
                        }
                    });
                } else {
                    showNoNetworkMsg();
                }
                break;*/

            case R.id.nav_group_settings:
                intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("id", item.getGroupId());
//                intent.putExtra("title", mGroupItem.getName());
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
      /*  if (requestCode == 123 && resultCode == RESULT_OK) {
            showLoadingDialog();
            LeafManager manager = new LeafManager();
            manager.getGroupDetail(this, id);
        }

        // From Camera
        if (requestCode == IMAGE_1_CAMERA_REQ_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            uploadedImg= MixOperations.convertBase64(imageBitmap);

            if(item!=null)
            {
                CreateGroupReguest request=new CreateGroupReguest();
                request.aboutGroup=item.aboutGroup;
                request.name=item.name;
                request.shortDescription=item.shortDescription;
                request.imageFile=uploadedImg;
                showLoadingDialog();
               AppLog.e(TAG,"Request Post Data :"+request);
                manager.editGroup(AboutGroupActivity2.this,request,id);
            }

            //convert to base64 and add to model class
        }


        // From Gallery
        if (requestCode == IMAGE_1_GALLERY_REQ_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Bitmap bitmap;
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    Bitmap bm = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
                    uploadedImg= MixOperations.convertBase64(bm);

                    if(item!=null)
                    {
                        CreateGroupReguest request=new CreateGroupReguest();
                        request.aboutGroup=item.aboutGroup;
                        request.name=item.name;
                        request.shortDescription=item.shortDescription;
                        request.imageFile=uploadedImg;
                        showLoadingDialog();
                        manager.editGroup(AboutGroupActivity2.this,request,id);

                       AppLog.e(TAG,"Request Post Data :"+request);
                    }

                } catch (IOException e) {
                   AppLog.e(TAG, e.toString());
                }

            } else {
               AppLog.e(TAG, "Retrieve Content null");
            }
            //convert to base64 and add to model class
        }*/

        if (requestCode == IMAGE_1_GALLERY_REQ_CODE && resultCode == Activity.RESULT_OK && data != null) {

            onImageSelected(data, 109);

        } else if (requestCode == IMAGE_1_CAMERA_REQ_CODE && resultCode == Activity.RESULT_OK) {
            if (imageCaptureFile != null) {
                galleryAddPic(imageCaptureFile.getPath());

                beginCrop(imageCaptureFile, 109);

            } else {
                Toast.makeText(this, getString(R.string.msg_unable_get_camera_image), Toast.LENGTH_SHORT).show();
            }


        } else if (requestCode == 109) {
           AppLog.e(TAG, "handle Crop");
            handleCrop(resultCode, data);
        }
    }

    public void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            final Uri output = Crop.getOutput(result);

            galleryAddPic(output.getPath());
            ImageCompressionAsyncTask
                    imageCompressionAsyncTask = new ImageCompressionAsyncTask(this, 600, 400, false);

            imageCompressionAsyncTask.setOnImageCompressed(new ImageCompressionAsyncTask.OnImageCompressed() {
                @Override
                public void onCompressedImage(ProfileImage profileImage) {
                    profileImage.imageUrl = output.toString();
                    if (item != null) {
                        CreateGroupReguest request = new CreateGroupReguest();
                        request.aboutGroup = item.aboutGroup;
                        request.name = item.name;
                        request.shortDescription = item.shortDescription;
                        request.avatar = profileImage.imageString;
                        //showLoadingDialog();
                       AppLog.e(TAG, "Request Post Data :" + request);
                        if (progressBar != null)
                            progressBar.setVisibility(View.VISIBLE);
                        manager.editGroup(AboutPublicGroupActivity.this, request, id+"");
                    }
                }
            });
            imageCompressionAsyncTask.execute(output.toString());

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
                Uri destination = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+ ".fileprovider", outputFile);
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
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        // hideLoadingDialog();
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        if (apiId == LeafManager.API_ID_LEAVE_GROUP) {
            LeaveResponse res = (LeaveResponse) response;
            AllContactModel.deleteContact(res.data.get(0).groupId+"", res.data.get(0).userId+"");
            /*for (int i = 0; i < res.data.referrerIds.length; i++){
                GruppieContactGroupIdModel.deleteRow(res.data.groupId, Integer.parseInt(res.data.referrerIds[i]));
            }*/
            GruppieContactGroupIdModel.deleteRow(item.getGroupId()+"", LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.LOGIN_ID));
            LeafPreference.getInstance(this).setBoolean(LeafPreference.ISGROUPUPDATED, true);
            Intent intent = new Intent(this, GroupListActivityNew.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else if (apiId == LeafManager.API_ID_DELETE_GROUP) {
            LeafPreference.getInstance(this).setBoolean(LeafPreference.ISGROUPUPDATED, true);
            Toast.makeText(AboutPublicGroupActivity.this, getResources().getString(R.string.msg_deleted_group), Toast.LENGTH_SHORT).show();

            cleverTapDeleteGroup();

            Intent intent = new Intent(this, GroupListActivityNew.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else if (apiId == LeafManager.API_ID_EDIT_GROUP) {
            Toast.makeText(this, "Successfully updated group details.", Toast.LENGTH_LONG).show();
            if (progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
            if (isConnectionAvailable()) {
                manager.getGroupDetail(this, id+"");
            } else {
                showNoNetworkMsg();
            }
        } else if (apiId == LeafManager.API_ID_DELETE_GROUPPIC) {

            Toast.makeText(this, "Successfully updated group details", Toast.LENGTH_LONG).show();
            //  showLoadingDialog();
            if (progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
            if (isConnectionAvailable()) {
                manager.getGroupDetail(this, id+"");
            } else {
                showNoNetworkMsg();
            }
        } else {
            GroupDetailResponse res = (GroupDetailResponse) response;
            item = res.data.get(0);

            totalUsers = res.data.get(0).totalUsers;
            if (!item.isAdmin) {
                //menuMultipleActions.setVisibility(View.GONE);
//                btnDelete.setVisibility(View.GONE);
//                btnEdit.setVisibility(View.GONE);
//                btn_change_admin.setVisibility(View.GONE);


                /*menu.findItem(R.id.nav_edit_group).setVisible(false);
                menu.findItem(R.id.nav_delete_group).setVisible(false);
                menu.findItem(R.id.nav_change_admin).setVisible(false);
                menu.findItem(R.id.nav_group_settings).setVisible(false);*/


            } else {
//                btnLeave.setVisibility(View.GONE);
                /*menu.findItem(R.id.nav_leave_group).setVisible(false);
                if (!GroupDashboardActivityNew.mAllowAdminChange) {
//                    btn_change_admin.setVisibility(View.GONE);
                    menu.findItem(R.id.nav_change_admin).setVisible(false);
                }*/
            }
//            btn_change_admin.setVisibility(View.GONE);
            // title = item.getName();|
            title = res.data.get(0).name;

           AppLog.e("txtName", "txtName is " + title);
            item.setName(title);
//            mToolBar.setTitle(title);
//            txtName.setText(title);
            DatabaseHandler databaseHandler = new DatabaseHandler(this);

            if (databaseHandler.getCount() != 0) {
                try {
//                   AppLog.e("CONTACTSS", "count is " + databaseHandler.getCount());
                    String name = databaseHandler.getNameFromNum(item.adminPhone.replaceAll(" ", ""));
//                   AppLog.e("CONTACTSS", "api name is " + res.data.createdBy);
//                   AppLog.e("CONTACTSS", "name is " + name);
//                   AppLog.e("CONTACTSS", "num is " + item.adminPhone);
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


//            txtCreator.setText("Created By: " + res.data.createdBy);
//            txtShort.setText("Description : " + res.data.shortDescription);
            txtAbout.setText(res.data.get(0).aboutGroup);


            LeafPreference.getInstance(this).setBoolean("IS_GROUP_ICON_CHANGE", true);
            LeafPreference.getInstance(this).setString("NEW_GROUP_ICON", res.data.get(0).image);
            if (res.data.get(0).image != null && !res.data.get(0).image.isEmpty()) {
                Picasso.with(this).load(res.data.get(0).image).into(imgLogo);
                imgLogo_Default.setVisibility(View.GONE);
            } else {
                imgLogo_Default.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(1));
                imgLogo_Default.setImageDrawable(drawable);
                //imgLogo.setImageResource(R.drawable.icon_gruppie);
            }

            tvNumOfComments.setText("" + res.data.get(0).getTotalCommentsCount());
            tvNumOfPosts.setText("" + res.data.get(0).getTotalPostsCount());
//            friends.setText("Total Friends : " + res.myFriendsCountInGroup);
           /* if (res.data.isAdmin) {
                btnDelete.setVisibility(View.VISIBLE);
                btnEdit.setVisibility(View.VISIBLE);
            } else {
                btnDelete.setVisibility(View.INVISIBLE);
                btnEdit.setVisibility(View.INVISIBLE);
            }*/
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

  /*  @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset == 0) {
            imgLogo.setVisibility(View.VISIBLE);
        } else {
            imgLogo.setVisibility(View.INVISIBLE);
        }
    }*/
}
