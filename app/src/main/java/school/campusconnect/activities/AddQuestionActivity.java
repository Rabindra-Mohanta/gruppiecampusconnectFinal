package school.campusconnect.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.util.Base64;
import android.util.Base64OutputStream;

import school.campusconnect.BuildConfig;
import school.campusconnect.utils.AppLog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.poi.ss.formula.functions.T;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.AddPostRequestDescription;
import school.campusconnect.datamodel.AddPostRequestFile;
import school.campusconnect.datamodel.AddPostRequestFile_Friend;
import school.campusconnect.datamodel.AddPostRequestVideo;
import school.campusconnect.datamodel.AddPostRequestVideo_Friend;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageCompressionAsyncTask;
import school.campusconnect.utils.ImageCompressionAsyncTask_Post;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.ProfileImage;
import school.campusconnect.utils.youtube.MainActivity;
import school.campusconnect.views.DrawableEditText;
import school.campusconnect.views.SMBDialogUtils;

public class AddQuestionActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<AddPostValidationError>, View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar mToolBar;

    @Bind(R.id.et_title)
    DrawableEditText edtTitle;

    /*@Bind(R.id.et_description)
    DrawableEditText edtDesc;*/

    @Bind(R.id.et_uploadfile)
    EditText edtFile;

    @Bind(R.id.btn_update)
    ImageView btn_update;
    /*@Bind(R.id.et_uploadvideo)
    DrawableEditText edtVideo;*/

    @Bind(R.id.img_image)
    ImageView img_image;

    @Bind(R.id.img_youtube)
    ImageView img_youtube;

    @Bind(R.id.btn_add_que)
    Button btn_Post;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    TextView btn_ok;
    TextView btn_cancel;
    TextView btn_upload;

    EditText edt_link;

    Dialog dialog;

    String group_id;
    String post_id;
    boolean ifImageOrVideo;
    public Uri imageCaptureFile;
    ProfileImage proImage;

    public static final int REQUEST_LOAD_CAMERA_IMAGE = 101;
    public static final int REQUEST_LOAD_GALLERY_IMAGE = 102;
    public static final int REQUEST_LOAD_PDF = 103;

    String postType;
    String imgUrl;
    String videoUrl = "";
    String team_id;
    boolean isQue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        ButterKnife.bind(this);
        btn_Post.setVisibility(View.GONE);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        GroupDashboardActivityNew.selectedUrl = "";
        GroupDashboardActivityNew.selectedYoutubeId = "";
        GroupDashboardActivityNew.uploadTitle = "";
        GroupDashboardActivityNew.uploadDesc = "";
        GroupDashboardActivityNew.enteredTitle = "";
        GroupDashboardActivityNew.enteredDesc = "";

        edtTitle.editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_edit_black, 0, 0, 0);
        /*edtDesc.editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_chat_black, 0, 0, 0);
        edtDesc.editText.setSingleLine(false);*/

        imgUrl = "";

        postType = "que";

        if (getIntent() != null) {
            group_id = getIntent().getExtras().getString("id");
            post_id = getIntent().getExtras().getString("post_id");
            postType = getIntent().getExtras().getString("type");
        }

        isQue = postType.equals("que");

        if (isQue)
            setTitle(getResources().getString(R.string.title_add_doubt));
        else {
            setTitle(getResources().getString(R.string.title_add_answer));
            edtTitle.setHint(getString(R.string.hint_postans));
            btn_Post.setText(getResources().getString(R.string.title_add_answer));
        }

        ifImageOrVideo = false;

        img_image.setFocusable(false);
        img_image.setOnClickListener(this);
        img_youtube.setOnClickListener(this);
        btn_Post.setOnClickListener(this);
        btn_update.setOnClickListener(this);

    }

    public boolean isValid() {
        boolean valid = true;

        if (!isValueValidOnly(edtTitle.editText)) {
            valid = false;
            Toast.makeText(this, getResources().getString(R.string.toast_add_description), Toast.LENGTH_SHORT).show();
            return valid;
        }

//        if (!isValueValidOnly(edt_post) && !isValueValidOnly(edtVideo.editText) && !isValueValidOnly(img_thumbnail)) {
        if (!isValueValidOnly(edtTitle.editText) && !isValueValidOnlyString(videoUrl) && !isValueValidOnlyString(imgUrl)) {
            valid = false;
            Toast.makeText(this, getResources().getString(R.string.toast_add_description_image_or_video), Toast.LENGTH_SHORT).show();
//        } else if (isValueValidOnly(img_thumbnail) && isValueValidOnly(edtVideo.editText)) {
        } else if (isValueValidOnlyString(imgUrl) && isValueValidOnlyString(videoUrl)) {
            valid = false;
            removeImage();
            Toast.makeText(this, "" + getResources().getString(R.string.msg_upload2), Toast.LENGTH_SHORT).show();
        }


  /*      if (!isValueValidOnly(edtFile) && !isValueValidOnly(edtVideo.editText)) {
            valid = false;
            Toast.makeText(this, "" + getResources().getString(R.string.msg_uploady), Toast.LENGTH_SHORT).show();

        }
        if (!edtFile.getText().toString().isEmpty()) {

            if (isValueValidOnly(edtFile) && isValueValidOnly(edtVideo.editText)) {
                valid = false;
                removeImage();
                Toast.makeText(this, "" + getResources().getString(R.string.msg_upload2), Toast.LENGTH_SHORT).show();
            }
        }*/


//        else if (edtPwd.editText.getText().toString().length() < 6) {
//            edtPwd.editText.setError(getString(R.string.msg_valid_pwd));
//            edtPwd.editText.requestFocus();
//            valid = false;
//        }
        return valid;
    }

    /*public boolean isValid() {
        boolean valid = true;

       *//* if (!isValueValid(edtTitle.editText)) {
            valid = false;
        }*//*

        if (postType.equals("team") || postType.equals("group"))
            if (!isValueValid(edtTitle.editText)) {
                valid = false;
            }

//        if (!isValueValidOnly(edtDesc.editText) && !isValueValidOnly(edtVideo.editText) && !isValueValidOnly(img_image)) {
        if (*//*!isValueValidOnly(edtDesc.editText) &&*//* !isValueValidOnlyString(videoUrl) && !isValueValidOnlyString(imgUrl)) {
            valid = false;
            Toast.makeText(this, "Please Add Description Or Select Image Or Video", Toast.LENGTH_SHORT).show();
//        } else if (isValueValidOnly(img_image) && isValueValidOnly(edtVideo.editText)) {
        } else if (isValueValidOnlyString(imgUrl) && isValueValidOnlyString(videoUrl)) {
            valid = false;
            removeImage();
            Toast.makeText(this, "" + getResources().getString(R.string.msg_upload2), Toast.LENGTH_SHORT).show();
        }


  *//*      if (!isValueValidOnly(edtFile) && !isValueValidOnly(edtVideo.editText)) {
            valid = false;
            Toast.makeText(this, "" + getResources().getString(R.string.msg_uploady), Toast.LENGTH_SHORT).show();

        }
        if (!edtFile.getText().toString().isEmpty()) {

            if (isValueValidOnly(edtFile) && isValueValidOnly(edtVideo.editText)) {
                valid = false;
                removeImage();
                Toast.makeText(this, "" + getResources().getString(R.string.msg_upload2), Toast.LENGTH_SHORT).show();
            }
        }*//*


//        else if (edtPwd.editText.getText().toString().length() < 6) {
//            edtPwd.editText.setError(getString(R.string.msg_valid_pwd));
//            edtPwd.editText.requestFocus();
//            valid = false;
//        }
        return valid;
    }*/

    public void addQuestion() {
        if (isConnectionAvailable()) {
            if (isValid()) {
              //  showLoadingDialog();

                showLoadingBar(progressBar,false);
                //progressBar.setVisibility(View.VISIBLE);

//                        if (!edtVideo.editText.getText().toString().equals("")) {
                if (!videoUrl.equals("")) {
                    LeafManager manager = new LeafManager();
                    AddPostRequestVideo request = new AddPostRequestVideo();
                    request.text = edtTitle.editText.getText().toString();

//                            request.video = edtVideo.editText.getText().toString();
                    request.video = videoUrl;
                    //Create Youtube Watch LInk      //  String youtubeLink = edtVideo.editText.getText().toString();
                    // youtubeLink = "https://www.youtube.com/watch?v="+getYouTubeId(youtubeLink);
                    //AppLog.e("AddPost" , "youtubeLInk : "+youtubeLink);
                    // request.video = youtubeLink;

                            /*if (postType.equals("group"))
                                manager.addPostVideo(AddQuestionActivity.this, id, team_id, 0, request, "group");
                            else if (postType.equals("team"))
                                manager.addPostVideo(AddQuestionActivity.this, id, team_id, 0, request, "team");
                            else {*/
                    AddPostRequestVideo_Friend request2 = new AddPostRequestVideo_Friend();
                    request2.text = edtTitle.editText.getText().toString();
//                                request2.video = edtVideo.editText.getText().toString();
                    request2.video = videoUrl;
                   AppLog.e("app", "here 1 " + new Gson().toJson(request2));
                    if (isQue)
//                                    manager.addPostVideo_Friend(AddQuestionActivity.this, id, friend_id, request2, postType);
                        manager.addQueVideo(this, group_id+"", post_id+"", request2);
                    else
                        manager.addAnsVideo(this, group_id+"", post_id+"", request2);
                            /*}*/
//                        } else if (!img_thumbnail.getText().toString().equals("")) {
                } else if (!imgUrl.equals("")) {
                    LeafManager manager = new LeafManager();
                    AddPostRequestFile request = new AddPostRequestFile();
                    request.text = edtTitle.editText.getText().toString();

                    request.imageFile = proImage.imageString;


                    AddPostRequestFile_Friend request2 = new AddPostRequestFile_Friend();
                    request2.text = edtTitle.editText.getText().toString();
                    request2.imageFile = proImage.imageString;
                   AppLog.e("app", "here 2 " + new Gson().toJson(request2));

                    if (isQue)
//                                    manager.addPostFile_Friend(AddQuestionActivity.this, id, friend_id, request2, postType);
                        manager.addQueImage(this, group_id+"", post_id+"", request2);
                    else
                        manager.addAnsImage(this, group_id+"", post_id+"", request2);

                } else if (!edtTitle.editText.getText().toString().equals("")) {
                    LeafManager manager = new LeafManager();
                    AddPostRequestDescription request = new AddPostRequestDescription();
                    request.text = edtTitle.editText.getText().toString();


                    AddPostRequestFile_Friend request2 = new AddPostRequestFile_Friend();
                    request2.text = edtTitle.editText.getText().toString();
                    request2.imageFile = null;
                   AppLog.e("app", "here 3 " + new Gson().toJson(request2));
                    if (isQue)
//                                    manager.addPostFile_Friend(AddQuestionActivity.this, id, friend_id, request2, postType);
                        manager.addQueImage(this, group_id+"", post_id+"", request2);
                    else
                        manager.addAnsImage(this, group_id+"", post_id+"", request2);
                }
            }
        } else {
            showNoNetworkMsg();
        }
    }

    public void onClick(View v) {
        hide_keyboard();
        switch (v.getId()) {

            case R.id.btn_add_que:
            case R.id.btn_update:
                addQuestion();
                break;

            case R.id.img_image:
                if (checkPermissionForWriteExternal()) {
//                    if (img_image.getText().toString().equalsIgnoreCase(""))
                    if (imgUrl.equalsIgnoreCase(""))
                        showPhotoDialog(R.array.array_image_pdf);
                    else {
                        showPhotoDialog(R.array.array_image_pdf_modify);
                    }
                } else {
                    requestPermissionForWriteExternal(21);
                }
                //Toast.makeText(AddPostActivity.this , "Not Added Yet , Add Video Only" , Toast.LENGTH_SHORT).show();
                break;

            case R.id.img_youtube:
                if (checkPermissionForWriteExternal()) {
                    showYoutubeDialog();
                } else {
                    requestPermissionForWriteExternal(22);
                }
                break;

        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
      //  hideLoadingDialog();
        hideLoadingBar();
    //    progressBar.setVisibility(View.GONE);
        Toast.makeText(AddQuestionActivity.this, getResources().getString(R.string.toast_successfully_posted), Toast.LENGTH_SHORT).show();
        /*if (postType.equalsIgnoreCase("group"))
            LeafPreference.getInstance(AddQuestionActivity.this).setBoolean(LeafPreference.ISGENERALPOSTUPDATED, true);
        else if (postType.equalsIgnoreCase("team"))
            LeafPreference.getInstance(AddQuestionActivity.this).setBoolean(LeafPreference.ISTEAMPOSTUPDATED, true);
        else
            LeafPreference.getInstance(AddQuestionActivity.this).setBoolean(LeafPreference.ISPERSONALPOSTUPDATED, true);*/

        onBackPressed();
        finish();
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<AddPostValidationError> error) {
       // hideLoadingDialog();
        hideLoadingBar();
        //    progressBar.setVisibility(View.GONE);
       AppLog.e("AddPostActivity", "OnFailure " + error.title + " , " + error.type);
       AppLog.e("AddContactActivity", "onFailure  ,, msg : " + error);
        if (error.status.equals("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
            finish();
        } else {
            if(error.errors==null)
                return;
            if (error.errors.get(0).video != null) {
//                edtVideo.editText.setError(error.errors.video[0]);
                Toast.makeText(this, error.errors.get(0).video, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, error.title, Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    public void onException(int apiId, String error) {
      //  hideLoadingDialog();
        hideLoadingBar();
        //    progressBar.setVisibility(View.GONE);
        Toast.makeText(AddQuestionActivity.this, error, Toast.LENGTH_SHORT).show();
    }


    public void showPhotoDialog(int resId) {
        SMBDialogUtils.showSMBSingleChoiceDialog(AddQuestionActivity.this,
                R.string.lbl_select_img, resId, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lw = ((AlertDialog) dialog).getListView();
                        switch (lw.getCheckedItemPosition()) {
                            case 0:
                                startCamera(REQUEST_LOAD_CAMERA_IMAGE);
                                break;
                            case 1:
                                startGallery(REQUEST_LOAD_GALLERY_IMAGE);
                                break;
                            case 2:
                                selectPdf(REQUEST_LOAD_PDF);
                                break;
                            case 3:
                                removeImage();
                                break;
                        }
                    }
                });
    }

    private String getYouTubeId(String youTubeUrl) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return "error";
        }
    }

    private void startCamera(int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(Build.VERSION.SDK_INT> Build.VERSION_CODES.M)
        {
            imageCaptureFile= FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+ ".fileprovider",ImageUtil.getOutputMediaFile());
        }
        else {
            imageCaptureFile = Uri.fromFile(ImageUtil.getOutputMediaFile());
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureFile);
        startActivityForResult(intent, requestCode);

    }



    private void selectPdf(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
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
            Toast.makeText(this, getResources().getString(R.string.toast_storage_permission_needed), Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
        }
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOAD_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            if (checkPermissionForWriteExternal()) {
                Uri uri = onImageSelected(data);
               AppLog.e("AddPostActivity", "ImageSelected URI : " + uri.toString());
                ImageCompressionAsyncTask_Post imageCompressionAsyncTask = new ImageCompressionAsyncTask_Post(AddQuestionActivity.this, 800, 480, false);
                imageCompressionAsyncTask.setOnImageCompressed(new ImageCompressionAsyncTask_Post.OnImageCompressed() {
                    @Override
                    public void onCompressedImage(ProfileImage profileImage) {
                        proImage = profileImage;
                        if (profileImage.imageString.isEmpty()) {
                            Toast.makeText(AddQuestionActivity.this, "Not able to compress selected image. Please verify", Toast.LENGTH_SHORT).show();
                        } else {
                           AppLog.e("AddPOstActivity", "imageUrl : " + profileImage.imageUrl);
                            imgUrl = profileImage.imageUrl;
//                            img_image.setText(profileImage.imageUrl);
                           AppLog.e("IMAGE__", "image is " + imgUrl);
//                            Picasso.with(AddPostActivity.this).load(imgUrl).into(img_image);
                            img_image.setImageBitmap(profileImage.image);
                        }

                    }
                });
                imageCompressionAsyncTask.execute(uri.toString());
            } else {
                Toast.makeText(this, "Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
            }


        } else if (requestCode == REQUEST_LOAD_CAMERA_IMAGE && resultCode == Activity.RESULT_OK) {
           AppLog.e("AddPost", "onActivityResult ");
            if (checkPermissionForWriteExternal()) {
                if (imageCaptureFile != null) {
                    galleryAddPic(imageCaptureFile.getPath());
                   AppLog.e("AddPost", "imageCapture File Not Null ");
                    ImageCompressionAsyncTask imageCompressionAsyncTask = new ImageCompressionAsyncTask(AddQuestionActivity.this, 800, 400, false);
                    imageCompressionAsyncTask.setOnImageCompressed(new ImageCompressionAsyncTask.OnImageCompressed() {
                        @Override
                        public void onCompressedImage(ProfileImage profileImage) {
                            proImage = profileImage;
                           AppLog.e("AddPost", "onCOmpressedImage : " + profileImage.imageUrl);
                            if (profileImage.imageString.isEmpty()) {
                                Toast.makeText(AddQuestionActivity.this, "Not able to compress selected image. Please verify", Toast.LENGTH_SHORT).show();
                            } else {
                                imgUrl = profileImage.imageUrl;
//                                img_image.setText(profileImage.imageUrl);
                               AppLog.e("IMAGE__", "image is " + imgUrl);
//                                Picasso.with(AddPostActivity.this).load(imgUrl).into(img_image);
                                img_image.setImageBitmap(profileImage.image);
                            }
                        }
                    });
                    imageCompressionAsyncTask.execute(imageCaptureFile.toString());

                } else {
                    Toast.makeText(AddQuestionActivity.this, getString(R.string.msg_unable_get_camera_image), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
            }
        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_LOAD_PDF) {
//                Uri videoUri = data.getData();
//                Uri videoUri = onImageSelected(data);
//                String fileSelected = data.getStringExtra("pdf");
                Uri selectedImageURI = data.getData();
                imgUrl = getPDFPath(selectedImageURI);
               AppLog.e("PDF", "imgUrl is " + imgUrl);
                File file = new File(imgUrl);
                try {
                    InputStream inputStream = null;//You can get an inputStream using any IO API

                    inputStream = new FileInputStream(file.getAbsolutePath());

                    proImage = new ProfileImage();

                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        output64.write(buffer, 0, bytesRead);
                    }
                    output64.close();

                    proImage.imageString = output.toString();
                    proImage.imageUrl = imgUrl;

                } catch (IOException e) {
                    e.printStackTrace();
                }


               AppLog.e("PDF", "Video URI= " + imgUrl);
                if (!imgUrl.equals(""))
                    Picasso.with(this).load(R.drawable.pdf_thumbnail).into(img_image);
            }
        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOAD_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            if (checkPermissionForWriteExternal()) {
                Uri uri = onImageSelected(data);
                AppLog.e("AddPostActivity", "ImageSelected URI : " + uri.toString());
                ImageCompressionAsyncTask_Post imageCompressionAsyncTask = new ImageCompressionAsyncTask_Post(AddQuestionActivity.this, 800, 480, false);
                imageCompressionAsyncTask.setOnImageCompressed(new ImageCompressionAsyncTask_Post.OnImageCompressed() {
                    @Override
                    public void onCompressedImage(ProfileImage profileImage) {
                        proImage = profileImage;
                        if (profileImage.imageString.isEmpty()) {
                            Toast.makeText(AddQuestionActivity.this, getResources().getString(R.string.toast_not_able_to_compress), Toast.LENGTH_SHORT).show();
                        } else {
                            AppLog.e("AddPOstActivity", "imageUrl : " + profileImage.imageUrl);
                            imgUrl = profileImage.imageUrl;
//                            allUrl = profileImage.imageUrl;
//                            img_thumbnail.setText(profileImage.imageUrl);
                            AppLog.e("IMAGE__", "image is " + imgUrl);
//                            Picasso.with(AddQuestionActivity.this).load(imgUrl).into(img_thumbnail);
                            img_image.setImageBitmap(profileImage.image);
                        }

                    }
                });
                imageCompressionAsyncTask.execute(uri.toString());
            } else {
                Toast.makeText(this, getResources().getString(R.string.toast_storage_permission_needed), Toast.LENGTH_LONG).show();
            }


        } else if (requestCode == REQUEST_LOAD_CAMERA_IMAGE && resultCode == Activity.RESULT_OK) {
            AppLog.e("AddPost", "onActivityResult ");
            if (checkPermissionForWriteExternal()) {
                if (imageCaptureFile != null) {
                    galleryAddPic(imageCaptureFile.getPath());
                    AppLog.e("AddPost", "imageCapture File Not Null ");
                    ImageCompressionAsyncTask imageCompressionAsyncTask = new ImageCompressionAsyncTask(AddQuestionActivity.this, Constants.image_width, Constants.image_height, false);
                    imageCompressionAsyncTask.setOnImageCompressed(new ImageCompressionAsyncTask.OnImageCompressed() {
                        @Override
                        public void onCompressedImage(ProfileImage profileImage) {
                            proImage = profileImage;
                            AppLog.e("AddPost", "onCOmpressedImage : " + profileImage.imageUrl);
                            if (profileImage.imageString.isEmpty()) {
                                Toast.makeText(AddQuestionActivity.this, getResources().getString(R.string.toast_not_able_to_compress), Toast.LENGTH_SHORT).show();
                            } else {
                                imgUrl = profileImage.imageUrl;
//                                allUrl = profileImage.imageUrl;
//                                img_thumbnail.setText(profileImage.imageUrl);
                                AppLog.e("IMAGE__", "image is " + imgUrl);
//                                Picasso.with(AddQuestionActivity.this).load(imgUrl).into(img_thumbnail);
                                img_image.setImageBitmap(profileImage.image);
                            }
                        }
                    });
                    imageCompressionAsyncTask.execute(imageCaptureFile.toString());

                } else {
                    Toast.makeText(AddQuestionActivity.this, getString(R.string.msg_unable_get_camera_image), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.toast_storage_permission_needed), Toast.LENGTH_LONG).show();
            }
        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_LOAD_PDF) {
//                Uri videoUri = data.getData();
//                Uri videoUri = onImageSelected(data);
//                String fileSelected = data.getStringExtra("pdf");
                Uri selectedImageURI = data.getData();
                AppLog.e("SelectedURI : ", selectedImageURI.toString());
                if (selectedImageURI.toString().startsWith("content")) {
                    imgUrl = getPDFPath(selectedImageURI);
                } else {
                    imgUrl = selectedImageURI.getPath();
                }
                AppLog.e("PDF", "imgUrl is " + imgUrl);
                File file = new File(imgUrl);
                try {
                    InputStream inputStream = null;//You can get an inputStream using any IO API

                    inputStream = new FileInputStream(file.getAbsolutePath());

                    proImage = new ProfileImage();

                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        output64.write(buffer, 0, bytesRead);
                    }
                    output64.close();

                    proImage.imageString = output.toString();
                    proImage.imageUrl = imgUrl;

                } catch (IOException e) {
                    e.printStackTrace();
                }


                AppLog.e("PDF", "Video URI= " + imgUrl);
                if (!imgUrl.equals(""))
                    Picasso.with(this).load(R.drawable.pdf_thumbnail).into(img_image);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GroupDashboardActivityNew.ifVideoSelected) {
            if (dialog != null && dialog.isShowing() && edt_link != null) {
                edt_link.setText(GroupDashboardActivityNew.selectedUrl);
                btn_ok.performClick();
            }
            GroupDashboardActivityNew.ifVideoSelected = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 21:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (img_image.getText().toString().equalsIgnoreCase(""))
                    if (imgUrl.equalsIgnoreCase(""))
                        showPhotoDialog(R.array.array_image_pdf);
                    else {
                        showPhotoDialog(R.array.array_image_pdf_modify);
                    }
                   AppLog.e("AddPost" + "permission", "granted camera");
                } else {
                   AppLog.e("AddPost" + "permission", "denied camera");
                }
                break;
            case 22:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (img_image.getText().toString().equalsIgnoreCase(""))
                    showYoutubeDialog();
                   AppLog.e("AddPost" + "permission", "granted camera");
                } else {
                   AppLog.e("AddPost" + "permission", "denied camera");
                }
                break;
        }
    }

    public void removeImage() {
//        img_image.setText("");
        imgUrl = "";
        Picasso.with(this).load(R.drawable.icon_attachment).into(img_image);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getPDFPath(Uri uri) {
        try {
            final String id = DocumentsContract.getDocumentId(uri);
            final Uri contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
           AppLog.e("PDF", "selection1 is " + cursor.getString(column_index));
            return cursor.getString(column_index);
        } catch (Exception e) {
            Toast.makeText(this, getResources().getString(R.string.toast_select_pdf), Toast.LENGTH_LONG).show();
            /*try {
               AppLog.e("PDF", "path is " + getPath(this, uri));
                return getPath(this, uri);
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
               AppLog.e("PDF", "error is " + e1.toString());
                return "";
            }*/
            return "";
        }
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return "";
    }

    public String getFileName(Context context, Uri uri) {
        if (uri != null && context != null) {
            Cursor returnCursor =
                    context.getContentResolver().query(uri, null, null, null, null);
            if (returnCursor != null) {
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                if (nameIndex >= 0 && sizeIndex >= 0) {
                   AppLog.e("PDF", "File Name : " + returnCursor.getString(nameIndex));
                   AppLog.e("PDF", "File Size : " + Long.toString(returnCursor.getLong(sizeIndex)));
                    Boolean isValidFile = checkOtherFileType(returnCursor.getString(nameIndex));

                    if (!isValidFile) {
                        return returnCursor.getString(nameIndex);
                    }
                }
            }
        }
        return "";
    }

    private Boolean checkOtherFileType(String filePath) {
        if (!filePath.equals("")) {
            String filePathInLowerCase = filePath.toLowerCase();
            if (filePathInLowerCase.endsWith(".pdf")) {
                return true;
            }
        }
        return false;
    }

    public String getPDF(Uri uri) {
        String uriString = uri.toString();
        File myFile = new File(uriString);
        String path = myFile.getAbsolutePath();
        String displayName = null;
       AppLog.e("PDF", "selection2 is 00");
        if (uriString.startsWith("content://")) {
           AppLog.e("PDF", "selection2 is 11");
            Cursor cursor = null;
            try {
               AppLog.e("PDF", "selection2 is 22");
                cursor = getContentResolver().query(uri, null, null, null, null);
               AppLog.e("PDF", "selection2 is 33");
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                   AppLog.e("PDF", "selection2 is 44" + displayName);
                    return displayName;
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        } else if (uriString.startsWith("file://")) {
           AppLog.e("PDF", "selection2 is 55");
            displayName = myFile.getName();
           AppLog.e("PDF", "selection2 is 66" + displayName);


            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    return cursor.getString(cursor.getColumnIndexOrThrow("_data"));
                }
            } finally {
                cursor.close();
            }
        }
        return "";
    }

    public String getPdf(Uri selectedImage) {
       AppLog.e("PDF", "selection2 is 00");
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
       AppLog.e("PDF", "selection2 is 11");
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
       AppLog.e("PDF", "selection2 is 22");
        int columnindex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
       AppLog.e("PDF", "selection2 is 33");
        cursor.moveToFirst();
       AppLog.e("PDF", "selection2 is 44");
        String file_path = cursor.getString(columnindex);
       AppLog.e("PDF", "selection2 is 55");
       AppLog.d(getClass().getName(), "file_path" + file_path);
       AppLog.e("PDF", "selection2 is 66");
        String fileUri = String.valueOf(Uri.parse("file://" + file_path));
       AppLog.e("PDF", "selection2 is 77");
        cursor.close();
       AppLog.e("PDF", "selection2 is 88");
        return fileUri;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
       AppLog.e("PDF", "selection2 is 00");
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
       AppLog.e("PDF", "selection2 is 11");
        try {
           AppLog.e("PDF", "selection2 is 22");
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
           AppLog.e("PDF", "selection2 is 33" + cursor);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
               AppLog.e("PDF", "selection2 is " + cursor.getString(column_index));
                return cursor.getString(column_index);
            } else
               AppLog.e("PDF", "selection2 is 44");
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return "";
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        String[] projection = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DURATION};
        Cursor cursor = getContentResolver().query(contentURI, projection, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DURATION};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        cursor.moveToFirst();
        String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
        int fileSize = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
        long duration = TimeUnit.MILLISECONDS.toSeconds(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));
        //some extra potentially useful data to help with filtering if necessary
        System.out.println("size: " + fileSize);
        System.out.println("path: " + filePath);
        System.out.println("duration: " + duration);

        return filePath;
    }

    public Uri onImageSelected(Intent data) {
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
        return sourceImage;
    }

    public void galleryAddPic(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    public static String extractYoutubeId(String ytUrl) {
        String vId = null;
        Pattern pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.matches()) {
            vId = matcher.group(1);
        }
        return vId;
    }
    /*public String extractYoutubeId(String url) throws MalformedURLException {
        String query = new URL(url).getQuery();
        String[] param = query.split("&");
        String id = null;
        for (String row : param) {
            String[] param1 = row.split("=");
            if (param1[0].equals("v")) {
                id = param1[1];
            }
        }
        return id;
    }*/

    public void showYoutubeDialog() {
        dialog = new Dialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_youtube);

        btn_ok = (TextView) dialog.findViewById(R.id.btn_ok);

        btn_cancel = (TextView) dialog.findViewById(R.id.btn_cancel);

        btn_upload = (TextView) dialog.findViewById(R.id.btn_upload);

        edt_link = (EditText) dialog.findViewById(R.id.edt_link);

        if (!videoUrl.equals(""))
            btn_cancel.setText(getResources().getString(R.string.lbl_remove));

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isValueValid(edtTitle.editText)) {
                    dialog.dismiss();
                    return;
                }
                /*if (!isValueValid(edtDesc.editText)) {
                    return;
                }*/
                GroupDashboardActivityNew.enteredTitle = edtTitle.editText.getText().toString();
//                GroupDashboardActivityNew.enteredDesc = edtDesc.editText.getText().toString();

                startActivity(new Intent(AddQuestionActivity.this, MainActivity.class));
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoUrl = edt_link.getText().toString();
                if (videoUrl.equals(""))
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.lbl_enter_youtube_link), Toast.LENGTH_LONG).show();
                else {
                    String videoId = "";
//                    try {
                    videoId = extractYoutubeId(videoUrl);
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    }

                    if (GroupDashboardActivityNew.selectedUrl.equals(videoUrl))
                        videoId = GroupDashboardActivityNew.selectedYoutubeId;

                   AppLog.e("VideoId is->", "" + videoId);

                    String img_url = "http://img.youtube.com/vi/" + videoId + "/0.jpg"; // this is link which will give u thumnail image of that video

                   AppLog.e("img_url is->", "" + img_url);

                    // picasso jar file download image for u and set image in imagview

                    Picasso.with(AddQuestionActivity.this)
                            .load(img_url)
                            .placeholder(R.drawable.icon_youtube)
                            .into(img_youtube, new Callback() {
                                @Override
                                public void onSuccess() {
                                   AppLog.e("onSuccess is->", "onSuccess");
                                }

                                @Override
                                public void onError() {
                                   AppLog.e("onError is->", "onError");
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_valid_youtube_link), Toast.LENGTH_LONG).show();
                                }
                            });
                    dialog.dismiss();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoUrl = "";
                Picasso.with(AddQuestionActivity.this)
                        .load(R.drawable.icon_youtube)
                        .into(img_youtube);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
