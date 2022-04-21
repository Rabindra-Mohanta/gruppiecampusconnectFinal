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
import androidx.databinding.BaseObservable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
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
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import android.util.Base64;
import android.util.Base64OutputStream;

import school.campusconnect.BuildConfig;
import school.campusconnect.utils.AppLog;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import school.campusconnect.fragments.QuestionListFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageCompressionAsyncTask;
import school.campusconnect.utils.ImageCompressionAsyncTask_Post;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.ProfileImage;
import school.campusconnect.views.SMBDialogUtils;

/**
 * Created by frenzin04 on 3/9/2017.
 */

public class QuestionListActivity extends BaseActivity implements View.OnClickListener, LeafManager.OnAddUpdateListener<AddPostValidationError> {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    @Bind(R.id.relPost)
    RelativeLayout relPost;
    @Bind(R.id.edt_post)
    EditText edt_post;
    @Bind(R.id.img_post)
    ImageView img_post;
    @Bind(R.id.img_thumbnail)
    ImageView img_thumbnail;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    Intent intent;
    String group_id;
    String post_id;
    String type;
    QuestionListFragment fragment;

    String allUrl = "";
    String imgUrl = "";
    String videoUrl = "";
    boolean myTeamList;

    public Uri imageCaptureFile;
    ProfileImage proImage;

    public static final int REQUEST_LOAD_CAMERA_IMAGE = 101;
    public static final int REQUEST_LOAD_GALLERY_IMAGE = 102;
    public static final int REQUEST_LOAD_PDF = 103;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_chat_list);


        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(R.string.lbl_que);

        if (getIntent().getExtras() != null) {
            group_id = getIntent().getExtras().getString("id");
            post_id = getIntent().getExtras().getString("post_id");
            /*post_id = getIntent().getExtras().getInt("post_id");
            type = getIntent().getExtras().getString("type");*/

            fragment = QuestionListFragment.newInstance(group_id, post_id);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            getSupportFragmentManager().executePendingTransactions();
        }

        img_post.setOnClickListener(this);
        img_thumbnail.setOnClickListener(this);

    }

    public void addTextChangedListener(View view) {
       AppLog.e("CLICK", "getText clicked");
        fragment.dataBinding.addTextChangedListener((EditText) view, 0);
    }

    public void onClickAddComment(View view) {
       AppLog.e("CLICK", "btn_comment clicked");
        fragment.onClickAddComment(view);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

       AppLog.e("onSuccess", "Success");
       AppLog.e("SearchResponse", response.toString());
        //hideLoadingDialog();
        if(progressBar!=null)
        progressBar.setVisibility(View.GONE);

        switch (apiId) {

            case LeafManager.API_ADD_QUE:
                edt_post.setText("");
                removeImage();
                fragment.refreshList();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_posted_successfully), Toast.LENGTH_SHORT).show();
                break;

        }
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<AddPostValidationError> error) {
        //hideLoadingDialog();
        if(progressBar!=null)
        progressBar.setVisibility(View.GONE);
       AppLog.e("onFailure", "Failure");
        if (error.status.equals("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(this, error.title, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onException(int apiId, String error) {
        //hideLoadingDialog();
        if(progressBar!=null)
        progressBar.setVisibility(View.GONE);
    }

    public class ObservableString extends BaseObservable {

        private String value = "";

        public ObservableString(String value) {
            this.value = value;
        }

        public ObservableString() {
        }

        public String get() {
            return value != null ? value : "";
        }

        public void set(String value) {
            if (value == null) value = "";
            if (!this.value.contentEquals(value)) {
                this.value = value;
                notifyChange();
            }
        }

        public boolean isEmpty() {
            return value == null || value.isEmpty();
        }

        public void clear() {
            set(null);
        }
    }

    @BindingAdapter("android:text")
    public static void bindIntegerInText(AppCompatEditText tv, int value) {
        tv.setText(String.valueOf(value));
    }

    @InverseBindingAdapter(attribute = "android:text")
    public static String getIntegerFromBinding(TextView view) {
        return view.getText().toString();
    }

    public boolean isValid() {
        boolean valid = true;

        if (!isValueValidOnly(edt_post)) {
            valid = false;
            Toast.makeText(this, getResources().getString(R.string.toast_add_description), Toast.LENGTH_SHORT).show();
            return valid;
        }

//        if (!isValueValidOnly(edt_post) && !isValueValidOnly(edtVideo.editText) && !isValueValidOnly(img_thumbnail)) {
        if (!isValueValidOnly(edt_post) && !isValueValidOnlyString(videoUrl) && !isValueValidOnlyString(imgUrl)) {
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

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.img_thumbnail:
                if (checkPermissionForWriteExternal()) {
//                    if (img_image.getText().toString().equalsIgnoreCase(""))
                    if (allUrl.equalsIgnoreCase(""))
                        showPhotoDialog(R.array.array_image_chat);
                    else {
                        showPhotoDialog(R.array.array_image_chat_modify);
                    }
                } else {
                    requestPermissionForWriteExternal(21);
                }
                break;

            case R.id.img_post:
                if (isConnectionAvailable()) {
                    if (isValid()) {
                       // showLoadingDialog();
                        if(progressBar!=null)
                        progressBar.setVisibility(View.VISIBLE);

//                        if (!edtVideo.editText.getText().toString().equals("")) {
                        if (!videoUrl.equals("")) {
                            LeafManager manager = new LeafManager();
                            AddPostRequestVideo request = new AddPostRequestVideo();
                            request.text = edt_post.getText().toString();

//                            request.video = edtVideo.editText.getText().toString();
                            request.video = videoUrl;
                            //Create Youtube Watch LInk      //  String youtubeLink = edtVideo.editText.getText().toString();
                            // youtubeLink = "https://www.youtube.com/watch?v="+getYouTubeId(youtubeLink);
                            //AppLog.e("AddPost" , "youtubeLInk : "+youtubeLink);
                            // request.video = youtubeLink;
                           AppLog.e("app", "here " + new Gson().toJson(request));
                            /*if (postType.equals("group"))
                                manager.addPostVideo(QuestionListActivity.this, id, team_id, 0, request, "group");
                            else if (postType.equals("team"))
                                manager.addPostVideo(QuestionListActivity.this, id, team_id, 0, request, "team");
                            else {*/
                            AddPostRequestVideo_Friend request2 = new AddPostRequestVideo_Friend();
                            request2.text = edt_post.getText().toString();
//                                request2.video = edtVideo.editText.getText().toString();
                            request2.video = videoUrl;

                            if (myTeamList)
//                                    manager.addPostVideo_Friend(QuestionListActivity.this, id, friend_id, request2, postType);
                                manager.addQueVideo(this, group_id+"", post_id+"", request2);
                            else
                                manager.addQueVideo(this, group_id+"", post_id+"", request2);
                            /*}*/
//                        } else if (!img_thumbnail.getText().toString().equals("")) {
                        } else if (!imgUrl.equals("")) {
                            LeafManager manager = new LeafManager();
                            AddPostRequestFile request = new AddPostRequestFile();
                            request.text = edt_post.getText().toString();

                            request.imageFile = proImage.imageString;

                           AppLog.e("app", "here " + new Gson().toJson(request));

                            AddPostRequestFile_Friend request2 = new AddPostRequestFile_Friend();
                            request2.text = edt_post.getText().toString();
                            request2.imageFile = proImage.imageString;


                            if (myTeamList)
//                                    manager.addPostFile_Friend(QuestionListActivity.this, id, friend_id, request2, postType);
                                manager.addQueImage(this, group_id+"", post_id+"", request2);
                            else
                                manager.addQueImage(this, group_id+"", post_id+"", request2);

                        } else if (!edt_post.getText().toString().equals("")) {
                            LeafManager manager = new LeafManager();
                            AddPostRequestDescription request = new AddPostRequestDescription();
                            request.text = edt_post.getText().toString();

                           AppLog.e("app", "here " + new Gson().toJson(request));


                            AddPostRequestFile_Friend request2 = new AddPostRequestFile_Friend();
                            request2.text = edt_post.getText().toString();
                            request2.imageFile = null;
                            if (myTeamList)
//                                    manager.addPostFile_Friend(QuestionListActivity.this, id, friend_id, request2, postType);
                                manager.addQueImage(this, group_id+"", post_id+"", request2);
                            else
                                manager.addQueImage(this, group_id+"", post_id+"", request2);

                        }
                    }
                } else {
                    showNoNetworkMsg();
                }
                break;
        }
    }

    public void showPhotoDialog(int resId) {
        SMBDialogUtils.showSMBSingleChoiceDialog(QuestionListActivity.this,
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
                                showYoutubeDialog();
                                break;
                            case 4:
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOAD_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            if (checkPermissionForWriteExternal()) {
                Uri uri = onImageSelected(data);
               AppLog.e("AddPostActivity", "ImageSelected URI : " + uri.toString());
                ImageCompressionAsyncTask_Post imageCompressionAsyncTask = new ImageCompressionAsyncTask_Post(QuestionListActivity.this, 800, 480, false);
                imageCompressionAsyncTask.setOnImageCompressed(new ImageCompressionAsyncTask_Post.OnImageCompressed() {
                    @Override
                    public void onCompressedImage(ProfileImage profileImage) {
                        proImage = profileImage;
                        if (profileImage.imageString.isEmpty()) {
                            Toast.makeText(QuestionListActivity.this, getResources().getString(R.string.toast_not_able_to_compress), Toast.LENGTH_SHORT).show();
                        } else {
                           AppLog.e("AddPOstActivity", "imageUrl : " + profileImage.imageUrl);
                            imgUrl = profileImage.imageUrl;
                            allUrl = profileImage.imageUrl;
//                            img_thumbnail.setText(profileImage.imageUrl);
                           AppLog.e("IMAGE__", "image is " + imgUrl);
//                            Picasso.with(QuestionListActivity.this).load(imgUrl).into(img_thumbnail);
                            img_thumbnail.setImageBitmap(profileImage.image);
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
                    ImageCompressionAsyncTask imageCompressionAsyncTask = new ImageCompressionAsyncTask(QuestionListActivity.this, Constants.image_width, Constants.image_height, false);
                    imageCompressionAsyncTask.setOnImageCompressed(new ImageCompressionAsyncTask.OnImageCompressed() {
                        @Override
                        public void onCompressedImage(ProfileImage profileImage) {
                            proImage = profileImage;
                           AppLog.e("AddPost", "onCOmpressedImage : " + profileImage.imageUrl);
                            if (profileImage.imageString.isEmpty()) {
                                Toast.makeText(QuestionListActivity.this,  getResources().getString(R.string.toast_not_able_to_compress), Toast.LENGTH_SHORT).show();
                            } else {
                                imgUrl = profileImage.imageUrl;
                                allUrl = profileImage.imageUrl;
//                                img_thumbnail.setText(profileImage.imageUrl);
                               AppLog.e("IMAGE__", "image is " + imgUrl);
//                                Picasso.with(QuestionListActivity.this).load(imgUrl).into(img_thumbnail);
                                img_thumbnail.setImageBitmap(profileImage.image);
                            }
                        }
                    });
                    imageCompressionAsyncTask.execute(imageCaptureFile.toString());

                } else {
                    Toast.makeText(QuestionListActivity.this, getString(R.string.msg_unable_get_camera_image), Toast.LENGTH_SHORT).show();
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
                imgUrl = getPDFPath(selectedImageURI);
                allUrl = getPDFPath(selectedImageURI);
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
                    Picasso.with(this).load(R.drawable.pdf_thumbnail).into(img_thumbnail);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 21:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (img_thumbnail.getText().toString().equalsIgnoreCase(""))
                    if (allUrl.equalsIgnoreCase(""))
                        showPhotoDialog(R.array.array_image_chat);
                    else {
                        showPhotoDialog(R.array.array_image_chat_modify);
                    }
                   AppLog.e("AddPost" + "permission", "granted camera");
                } else {
                   AppLog.e("AddPost" + "permission", "denied camera");
                }
                break;
        }
    }

    public void removeImage() {
//        img_thumbnail.setText("");
        allUrl = "";
        videoUrl = "";
        imgUrl = "";
        Picasso.with(this).load(R.drawable.icon_attachment).into(img_thumbnail);
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
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_select_pdf), Toast.LENGTH_SHORT).show();
//            return getPDF(uri);
            return "";
        }
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
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_youtube);

        TextView btn_ok = (TextView) dialog.findViewById(R.id.btn_ok);

        TextView btn_cancel = (TextView) dialog.findViewById(R.id.btn_cancel);

        final EditText edt_link = (EditText) dialog.findViewById(R.id.edt_link);

        if (!videoUrl.equals(""))
            btn_cancel.setText(getResources().getString(R.string.lbl_remove));

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoUrl = edt_link.getText().toString();
                allUrl = edt_link.getText().toString();
                if (videoUrl.equals(""))
                    Toast.makeText(QuestionListActivity.this, getResources().getString(R.string.lbl_enter_youtube_link), Toast.LENGTH_SHORT).show();
                else {
                    String videoId = "";
//                    try {
                    videoId = extractYoutubeId(videoUrl);
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    }

                   AppLog.e("VideoId is->", "" + videoId);

                    String img_url = "http://img.youtube.com/vi/" + videoId + "/0.jpg"; // this is link which will give u thumnail image of that video

                   AppLog.e("img_url is->", "" + img_url);

                    // picasso jar file download image for u and set image in imagview

                    Picasso.with(QuestionListActivity.this)
                            .load(img_url)
                            .placeholder(R.drawable.icon_youtube)
                            .into(img_thumbnail, new Callback() {
                                @Override
                                public void onSuccess() {
                                   AppLog.e("onSuccess is->", "onSuccess");
                                }

                                @Override
                                public void onError() {
                                   AppLog.e("onError is->", "onError");
                                    Toast.makeText(QuestionListActivity.this, getResources().getString(R.string.toast_valid_youtube_link), Toast.LENGTH_SHORT).show();
                                    removeImage();
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
                allUrl = "";
                Picasso.with(QuestionListActivity.this)
                        .load(R.drawable.icon_youtube)
                        .into(img_thumbnail);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
