package school.campusconnect.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;
import school.campusconnect.BuildConfig;
import school.campusconnect.LeafApplication;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.AddVendorPostRequest;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneHelper;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.GetThumbnail;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.youtube.MainActivity;
import school.campusconnect.views.SMBDialogUtils;


public class AddVendorActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<AddPostValidationError>, View.OnClickListener {

    private static final String TAG = "AddPostActivity";
    @Bind(R.id.toolbar)
    Toolbar mToolBar;

    @Bind(R.id.et_title)
    EditText edtTitle;

    @Bind(R.id.et_description)
    EditText edtDesc;

    @Bind(R.id.llImage)
    LinearLayout llImage;

    @Bind(R.id.llVideo)
    LinearLayout llVideo;

    @Bind(R.id.llDoc)
    LinearLayout llDoc;

    @Bind(R.id.img_image)
    ImageView img_image;

    @Bind(R.id.img_youtube)
    ImageView img_youtube;

    @Bind(R.id.imgDoc)
    ImageView imgDoc;

    @Bind(R.id.tvLabelTitle)
    TextView tvLabelTitle;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.pbImgLoading)
    ProgressBar pbImgLoading;

    @Bind(R.id.btnShare)
    Button btnShare;


    TextView btn_ok;
    TextView btn_cancel;
    TextView btn_upload;

    EditText edt_link;

    Dialog dialog;

    String group_id;

    public Uri imageCaptureFile;


    public static final int REQUEST_LOAD_CAMERA_IMAGE = 101;
    public static final int REQUEST_LOAD_GALLERY_IMAGE = 102;
    public static final int REQUEST_LOAD_PDF = 103;

    String videoUrl = "";

    LeafManager manager = new LeafManager();

    ArrayList<String> listAmazonS3Url = new ArrayList<>();
    ArrayList<String> listImages = new ArrayList<>();
    private String pdfPath = "";
    private TransferUtility transferUtility;
    private AddVendorPostRequest mainRequest;
    private File cameraFile;
    private String receiverToken="";
    private String receiverDeviceType="";
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_add_vendor);
        ButterKnife.bind(this);

        init();

        setListener();


        ArrayList<String> shareList = LeafApplication.getInstance().getShareFileList();
        if(shareList!=null && shareList.size()>0){
            String fileType = LeafApplication.getInstance().getType();
            if(Constants.FILE_TYPE_VIDEO.equalsIgnoreCase(fileType)){
                return;
            }
            SMBDialogUtils.showSMBDialogYesNoCancel(this, "Attach Selected file?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if(Constants.FILE_TYPE_IMAGE.equalsIgnoreCase(fileType)
                            || Constants.FILE_TYPE_VIDEO.equalsIgnoreCase(fileType)){
                        listImages.addAll(shareList);
                        showLastImage();
                    }else if(Constants.FILE_TYPE_PDF.equalsIgnoreCase(fileType)){
                        pdfPath = shareList.get(0);
                        Picasso.with(AddVendorActivity.this).load(R.drawable.pdf_thumbnail).into(imgDoc);
                    }
                }
            });
        }

    }

    private void setListener() {
        llImage.setOnClickListener(this);
        llVideo.setOnClickListener(this);
        llDoc.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnShare.setEnabled(false);

        edtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                shareButtonEnableDisable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                shareButtonEnableDisable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        group_id=GroupDashboardActivityNew.groupId;

//        edtDesc.setMovementMethod(new ScrollingMovementMethod());
//        edtTitle.setMovementMethod(new ScrollingMovementMethod());

        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle("Add Vendor");

        transferUtility = AmazoneHelper.getTransferUtility(this);

    }

    private void shareButtonEnableDisable() {
        if (isValid(false)) {
            btnShare.setEnabled(true);
        } else {
            btnShare.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void addPost() {
        hide_keyboard();
        if (isConnectionAvailable()) {
            if (isValid(true)) {
                if (progressBar != null)
                    progressBar.setVisibility(View.VISIBLE);
                btnShare.setEnabled(false);

                AddVendorPostRequest request = new AddVendorPostRequest();
                request.vendor = edtTitle.getText().toString();
                request.description = edtDesc.getText().toString();

               /* if (!TextUtils.isEmpty(videoUrl)) {
                    request.video = videoUrl;
                    request.fileType = Constants.FILE_TYPE_YOUTUBE;

                    Log.e(TAG, "send data " + new Gson().toJson(request));
                    mainRequest=request;
                    manager.addGalleryPost(this, group_id,  request);
                } else*/ if (!TextUtils.isEmpty(pdfPath)) {
                    request.fileType = Constants.FILE_TYPE_PDF;
                    progressDialog.setMessage("Preparing Pdf...");
                    progressDialog.show();
                    uploadToAmazone(request);
                } else if (listImages.size() > 0) {
                    request.fileType = Constants.FILE_TYPE_IMAGE;
                    progressDialog.setMessage("Uploading Image...");
                    progressDialog.show();
                    uploadToAmazone(request);
                } else {
                    Log.e(TAG, "send data " + new Gson().toJson(request));
                    mainRequest=request;
                    manager.addVendorPost(this, group_id, request);

                }
            }
        } else {
            showNoNetworkMsg();
        }

    }

    private void uploadToAmazone(AddVendorPostRequest request) {
        mainRequest = request;
        //request.fileName = listAmazonS3Url;
        Log.e(TAG, "send data " + new Gson().toJson(request));

        if (request.fileType.equals(Constants.FILE_TYPE_PDF)) {

            AppLog.e(TAG, "Final PAth :: " + listImages.toString());
            listImages.clear();
            listImages.add(pdfPath);
            GetThumbnail.create(listImages, new GetThumbnail.GetThumbnailListener() {
                @Override
                public void onThumbnail(ArrayList<String> listThumbnails) {
                    if(listThumbnails!=null){
                        uploadThumbnail(listThumbnails,0);
                    }else {
                        Toast.makeText(AddVendorActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    }

                }
            },Constants.FILE_TYPE_PDF);
        } else {
            for (int i = 0; i < listImages.size(); i++) {
                try {
                    File newFile = new Compressor(this).setMaxWidth(1000).setQuality(90).compressToFile(new File(listImages.get(i)));
                    listImages.set(i, newFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            AppLog.e(TAG, "Final PAth :: " + listImages.toString());
            upLoadImageOnCloud(0);
        }
    }
    private void uploadThumbnail(ArrayList<String> listThumbnails, int index) {
        if (index == listThumbnails.size()) {
            mainRequest.thumbnailImage = listThumbnails;
            upLoadImageOnCloud(0);
        }else {
            final String key = AmazoneHelper.getAmazonS3KeyThumbnail(mainRequest.fileType);
            File file = new File(listThumbnails.get(index));
            TransferObserver observer = transferUtility.upload(AmazoneHelper.BUCKET_NAME, key,
                    file , CannedAccessControlList.PublicRead);

            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    AppLog.e(TAG, "onStateChanged: " + id + ", " + state.name());
                    if (state.toString().equalsIgnoreCase("COMPLETED")) {
                        Log.e("Thumbnail", "onStateChanged " + index);

                        String _finalUrl = AmazoneHelper.BUCKET_NAME_URL + key;

                        Log.e("FINALURL", "url is " + _finalUrl);

                        _finalUrl = Constants.encodeStringToBase64(_finalUrl);

                        Log.e("FINALURL", "encoded url is " + _finalUrl);

                        listThumbnails.set(index,_finalUrl);

                        uploadThumbnail(listThumbnails,index+1);

                    }
                    if (TransferState.FAILED.equals(state)) {
                        progressBar.setVisibility(View.GONE);
                        if (progressDialog!=null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(AddVendorActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                    int percentDone = (int) percentDonef;
                    if (Constants.FILE_TYPE_PDF.equals(mainRequest.fileType)) {
                        progressDialog.setMessage("Preparing Pdf " + percentDone + "% " + (index + 1) + " out of " + listImages.size() + ", please wait...");
                    }
                    AppLog.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                            + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
                }

                @Override
                public void onError(int id, Exception ex) {
                    progressBar.setVisibility(View.GONE);
                    if (progressDialog!=null) {
                        progressDialog.dismiss();
                    }
                    AppLog.e(TAG, "Upload Error : " + ex);
                    Toast.makeText(AddVendorActivity.this, getResources().getString(R.string.image_upload_error), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void upLoadImageOnCloud(final int pos) {

        if (pos == listImages.size()) {
            if (progressDialog!=null) {
                progressDialog.dismiss();
            }
            mainRequest.fileName = listAmazonS3Url;
            AppLog.e(TAG,"send data : "+new Gson().toJson(mainRequest));
            manager.addVendorPost(this, group_id, mainRequest);
        } else {
            final String key = AmazoneHelper.getAmazonS3Key(mainRequest.fileType);
            File file = new File(listImages.get(pos));
            TransferObserver observer = transferUtility.upload(AmazoneHelper.BUCKET_NAME, key,
                    file , CannedAccessControlList.PublicRead);

            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    AppLog.e(TAG, "onStateChanged: " + id + ", " + state.name());
                    if (state.toString().equalsIgnoreCase("COMPLETED")) {
                        Log.e("MULTI_IMAGE", "onStateChanged " + pos);
                        updateList(pos, key);
                    }
                    if (TransferState.FAILED.equals(state)) {
                        Toast.makeText(AddVendorActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
                        if(progressDialog!=null)
                            progressDialog.dismiss();
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                    int percentDone = (int) percentDonef;
                    if (Constants.FILE_TYPE_PDF.equals(mainRequest.fileType)) {
                        progressDialog.setMessage("Uploading Pdf " + percentDone + "% " + (pos + 1) + " out of " + listImages.size() + ", please wait...");
                    } else if (Constants.FILE_TYPE_IMAGE.equals(mainRequest.fileType)) {
                        progressDialog.setMessage("Uploading Image " + percentDone + "% " + (pos + 1) + " out of " + listImages.size() + ", please wait...");
                    }
                    AppLog.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                            + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
                }

                @Override
                public void onError(int id, Exception ex) {
                    progressBar.setVisibility(View.GONE);
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    AppLog.e(TAG, "Upload Error : " + ex);
                    Toast.makeText(AddVendorActivity.this, getResources().getString(R.string.image_upload_error), Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private void updateList(int pos, String key) {
        String _finalUrl = AmazoneHelper.BUCKET_NAME_URL + key;

        Log.e("FINALURL", "url is " + _finalUrl);

        _finalUrl = Constants.encodeStringToBase64(_finalUrl);

        Log.e("FINALURL", "encoded url is " + _finalUrl);

        listAmazonS3Url.add(_finalUrl);

        upLoadImageOnCloud(pos + 1);
    }


    public boolean isValid(boolean showToast) {
        boolean valid=true;

        Log.e("edtDesc : ", edtTitle.getText().toString());
        Log.e("videoUrl : ", videoUrl);
        Log.e("image paths : ", listImages.toString());

        if (!isValueValidOnly(edtTitle)) {
            if (showToast)
                Toast.makeText(this, "Please Add Vendor Name",Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if(!isValueValidOnly(edtDesc) && TextUtils.isEmpty(pdfPath) && listImages.size()==0)
        {
            if(showToast)
                Toast.makeText(this, "Please Add Description or Image or pdf",Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }


    public void onClick(View v) {
        hide_keyboard();
        switch (v.getId()) {
            case R.id.btnShare:
                addPost();
                break;

            case R.id.llImage:
                if (checkPermissionForWriteExternal()) {
                    if (listImages.size() > 0) {
                        showPhotoDialog(R.array.array_image_modify);
                    } else {
                        showPhotoDialog(R.array.array_image);
                    }

                } else {
                    requestPermissionForWriteExternal(21);
                }
                break;

            case R.id.llVideo:
                if (checkPermissionForWriteExternal()) {
                    showYoutubeDialog();
                } else {
                    requestPermissionForWriteExternal(22);
                }
                break;
            case R.id.llDoc:
                if (checkPermissionForWriteExternal()) {
                    selectPdf(REQUEST_LOAD_PDF);
                } else {
                    requestPermissionForWriteExternal(23);
                }
                break;

        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        switch (apiId)
        {

            default:
                Toast.makeText(AddVendorActivity.this, "Successfully Posted", Toast.LENGTH_SHORT).show();
                LeafPreference.getInstance(this).setBoolean(LeafPreference.IS_VENDOR_POST_UPDATED, true);
                new SendNotification().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                finish();

                break;
        }

    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<AddPostValidationError> error) {
        btnShare.setEnabled(true);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        Log.e("AddPostActivity", "OnFailure " + error.title + " , " + error.type);
        if (error.status.equals("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
            finish();
        } else {
            if(error.errors==null)
                return;
            if (error.errors.get(0).video != null) {
                Toast.makeText(this, error.errors.get(0).video, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, error.title, Toast.LENGTH_SHORT).show();
            }
        }


    }


    @Override
    public void onException(int apiId, String error) {
        btnShare.setEnabled(true);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        Toast.makeText(AddVendorActivity.this, error, Toast.LENGTH_SHORT).show();

    }

    private class SendNotification extends AsyncTask<String, String, String> {
        private String server_response;

        public SendNotification()
        {

        }

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL("https://fcm.googleapis.com/fcm/send");
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", BuildConfig.API_KEY_FIREBASE1 + BuildConfig.API_KEY_FIREBASE2);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                try {
                    JSONObject object = new JSONObject();

                    String topic;
                    String title = getResources().getString(R.string.app_name);
                    String name = LeafPreference.getInstance(AddVendorActivity.this).getString(LeafPreference.NAME);
                    String message = name + " has added new vendor." ;
                    topic = GroupDashboardActivityNew.groupId ;
                    object.put("to", "/topics/" + topic);

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title", title);
                    notificationObj.put("body", message);
                   // object.put("notification", notificationObj);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("groupId", GroupDashboardActivityNew.groupId);
                    dataObj.put("createdById", LeafPreference.getInstance(AddVendorActivity.this).getString(LeafPreference.LOGIN_ID));
                    dataObj.put("teamId", group_id);
                    dataObj.put("title", title);
                    dataObj.put("Notification_type",  "VendorAdd");
                    dataObj.put("body", message);
                    object.put("data", dataObj);
                    wr.writeBytes(object.toString());
                    Log.e(TAG, " JSON input : " + object.toString());
                    wr.flush();
                    wr.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                AppLog.e(TAG, "responseCode :" + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_response = readStream(urlConnection.getInputStream());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return server_response;
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            AppLog.e(TAG, "server_response :" + server_response);

            if (!TextUtils.isEmpty(server_response)) {
                AppLog.e(TAG, "Notification Sent");
            } else {
                AppLog.e(TAG, "Notification Send Fail");
            }
        }
    }




    public void showPhotoDialog(int resId) {
        SMBDialogUtils.showSMBSingleChoiceDialog(AddVendorActivity.this,
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
                                removeImage();
                                break;
                        }
                    }
                });
    }


    private void startCamera(int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            cameraFile=ImageUtil.getOutputMediaFile();
            imageCaptureFile = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", cameraFile);
        } else {
            cameraFile=ImageUtil.getOutputMediaFile();
            imageCaptureFile = Uri.fromFile(cameraFile);
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
            Log.e("External" + "permission", "checkpermission , granted");
            return true;
        } else {
            Log.e("External" + "permission", "checkpermission , denied");
            return false;
        }
    }

    public void requestPermissionForWriteExternal(int code) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            AppLog.e(TAG, "requestPermissionForWriteExternal");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOAD_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            final Uri selectedImage = data.getData();
            ClipData clipData = data.getClipData();

            if (clipData == null) {
                listImages.clear();
                String path = ImageUtil.getPath(this, selectedImage);
                listImages.add(path);
            } else {
                listImages.clear();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    final Uri uri1 = item.getUri();
                    String path = ImageUtil.getPath(this, uri1);
                    listImages.add(path);
                }
            }
            showLastImage();
            removePdf();

        } else if (requestCode == REQUEST_LOAD_CAMERA_IMAGE && resultCode == Activity.RESULT_OK) {
            String path = cameraFile.getAbsolutePath();
            AppLog.e(TAG, "path : " + path);
            listImages.add(path);
            showLastImage();
            removePdf();
        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_LOAD_PDF) {
                Uri selectedImageURI = data.getData();
                Log.e("SelectedURI : ", selectedImageURI.toString());
                if (selectedImageURI.toString().startsWith("content")) {
                    pdfPath = ImageUtil.getPath(this, selectedImageURI);
                } else {
                    pdfPath = selectedImageURI.getPath();
                }

                if (TextUtils.isEmpty(pdfPath)) {
                    Toast.makeText(getApplicationContext(), "Please select a pdf file", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.e("PDF", "imgUrl is " + pdfPath);

                if (!TextUtils.isEmpty(pdfPath))
                    Picasso.with(this).load(R.drawable.pdf_thumbnail).into(imgDoc);

                removeImage();
            }
        }

        shareButtonEnableDisable();

    }

    private void showLastImage() {
        if (listImages.size() > 0) {
            Picasso.with(this).load(new File(listImages.get(listImages.size() - 1))).resize(100, 100).into(img_image);
        } else {
            Toast.makeText(this, getResources().getString(R.string.lbl_select_img), Toast.LENGTH_SHORT).show();
        }
    }

    private void removeImage() {
        listImages.clear();
        Picasso.with(this).load(R.drawable.icon_gallery).into(img_image);
    }

    private void removePdf() {
        pdfPath = "";
        Picasso.with(this).load(R.drawable.icon_doc).into(imgDoc);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 21:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showPhotoDialog(R.array.array_image);
                    Log.e("AddPost" + "permission", "granted camera");
                } else {
                    Log.e("AddPost" + "permission", "denied camera");
                }
                break;

            case 22:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showYoutubeDialog();
                    Log.e("AddPost" + "permission", "granted camera");
                } else {
                    Log.e("AddPost" + "permission", "denied camera");
                }
            case 23:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectPdf(REQUEST_LOAD_PDF);
                } else {
                    Log.e("AddPost" + "permission", "denied camera");
                }
                break;
        }
    }

    public static String extractYoutubeId(String ytUrl) {

        final String[] videoIdRegex = {"\\?vi?=([^&])", "watch\\?.v=([^&])", "(?:embed|vi?)/([^/?])", "^([A-Za-z0-9\\-\\_]*)"};
        String youTubeLinkWithoutProtocolAndDomain = youTubeLinkWithoutProtocolAndDomain(ytUrl);

        for (String regex : videoIdRegex) {
            Pattern compiledPattern = Pattern.compile(regex);
            Matcher matcher = compiledPattern.matcher(youTubeLinkWithoutProtocolAndDomain);

            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        return null;
    }

    public static String youTubeLinkWithoutProtocolAndDomain(String url) {
        final String youTubeUrlRegEx = "^(https?)?(://)?(www.)?(m.)?((youtube.com)|(youtu.be))/";
        Pattern compiledPattern = Pattern.compile(youTubeUrlRegEx);
        Matcher matcher = compiledPattern.matcher(url);

        if (matcher.find()) {
            return url.replace(matcher.group(), "");
        }
        return url;
    }

    public void showYoutubeDialog() {
        dialog = new Dialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_youtube);

        btn_ok = (TextView) dialog.findViewById(R.id.btn_ok);

        btn_cancel = (TextView) dialog.findViewById(R.id.btn_cancel);

        btn_upload = (TextView) dialog.findViewById(R.id.btn_upload);
        btn_upload.setVisibility(View.INVISIBLE);

        edt_link = (EditText) dialog.findViewById(R.id.edt_link);

        if (!videoUrl.equals(""))
            btn_cancel.setText("Remove");

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoUrl = edt_link.getText().toString();
                if (videoUrl.equals(""))
                    Toast.makeText(AddVendorActivity.this, "Enter youtube link", Toast.LENGTH_SHORT).show();
                else {
                    String videoId = "";
                    videoId = extractYoutubeId(videoUrl);

                    if (GroupDashboardActivityNew.selectedUrl.equals(videoUrl))
                        videoId = GroupDashboardActivityNew.selectedYoutubeId;

                    Log.e("VideoId is->", "" + videoId);

                    String img_url = "http://img.youtube.com/vi/" + videoId + "/0.jpg"; // this is link which will give u thumnail image of that video

                    Log.e("img_url is->", "" + img_url);

                    Picasso.with(AddVendorActivity.this)
                            .load(img_url)
                            .placeholder(R.drawable.icon_youtube)
                            .into(img_youtube, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Log.e("onSuccess is->", "onSuccess");
                                    shareButtonEnableDisable();
                                }

                                @Override
                                public void onError() {
                                    Log.e("onError is->", "onError");
                                    videoUrl = "";
                                    Toast.makeText(AddVendorActivity.this, "Not a valid youtube link", Toast.LENGTH_SHORT).show();
                                }
                            });
                    dialog.dismiss();
                }
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isValueValid(edtTitle)) {
                    dialog.dismiss();
                    return;
                }
                if (!isValueValid(edtDesc)) {
                    dialog.dismiss();
                    return;
                }
                GroupDashboardActivityNew.enteredTitle = edtTitle.getText().toString();
                GroupDashboardActivityNew.enteredDesc = edtDesc.getText().toString();

                startActivity(new Intent(AddVendorActivity.this, MainActivity.class));
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoUrl = "";
                Picasso.with(AddVendorActivity.this)
                        .load(R.drawable.icon_youtube)
                        .into(img_youtube);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

  /*  private class SendNotification extends AsyncTask<String,String,String>
    {
        private final AddPostRequest requestData;
        private String server_response;

        public SendNotification(AddPostRequest mainRequest) {
            requestData=mainRequest;
        }

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL("https://fcm.googleapis.com/fcm/send");
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty ("Authorization", BuildConfig.API_KEY_FIREBASE);
                urlConnection.setRequestProperty ("Content-Type", "application/json");

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                try {
                    JSONObject object = new JSONObject();

                    String topic;
                    String title=getResources().getString(R.string.app_name);
                    String message="";
                    if(postType.equals("group"))
                    {
                        message=userName+" Has Posted in "+GroupDashboardActivityNew.group_name;
                        topic=group_id;
                        object.put("to","/topics/"+ topic);
                    }
                    else if(postType.equals("team"))
                    {
                        message=userName+" Has Posted in "+team_name+" Team";
                        topic=group_id+"_"+team_id;
                        object.put("to","/topics/"+ topic);
                    }
                    else
                    {
                        message=userName+" Has Sent you Message";
                        object.put("to",receiverToken);
                    }
                    JSONObject notificationObj=new JSONObject();
                    notificationObj.put("title",title);
                    notificationObj.put("body",message);
                    object.put("notification",notificationObj);

                    JSONObject dataObj=new JSONObject();
                    dataObj.put("groupId",group_id);
                    dataObj.put("createdById",LeafPreference.getInstance(AddGalleryPostActivity.this).getString(LeafPreference.LOGIN_ID));
                    dataObj.put("postId","");
                    dataObj.put("teamId",team_id);
                    dataObj.put("title",title);
                    dataObj.put("postType",postType);
                    dataObj.put("Notification_type","post");
                    dataObj.put("body",message);
                    object.put("data",dataObj);

                    wr.writeBytes(object.toString());
                    Log.e(TAG , " JSON input : "+ object.toString());
                    wr.flush();
                    wr.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                AppLog.e(TAG,"responseCode :"+responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_response = readStream(urlConnection.getInputStream());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return server_response;
        }
        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            AppLog.e(TAG,"server_response :"+server_response);

            if(!TextUtils.isEmpty(server_response))
            {
                AppLog.e(TAG,"Notification Sent");
            }
            else
            {
                AppLog.e(TAG,"Notification Send Fail");
            }
        }
    }
*/
}
