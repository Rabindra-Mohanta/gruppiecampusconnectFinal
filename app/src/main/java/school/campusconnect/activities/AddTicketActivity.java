package school.campusconnect.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.mobileconnectors.s3.transferutility.UploadOptions;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;
import school.campusconnect.BuildConfig;
import school.campusconnect.LeafApplication;
import school.campusconnect.R;
import school.campusconnect.adapters.UploadImageAdapter;
import school.campusconnect.databinding.ActivityAddTicketBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.add_tikit.AddTicketRequest;
import school.campusconnect.datamodel.booths.SubBoothResponse;
import school.campusconnect.datamodel.issue.IssueListResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneHelper;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.address.AddressActivity;
import school.campusconnect.utils.address.FindAddress;
import school.campusconnect.utils.address.GetLocation;
import school.campusconnect.utils.GetThumbnail;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.views.SMBDialogUtils;

public class AddTicketActivity extends BaseActivity implements View.OnClickListener, UploadImageAdapter.UploadImageListener, LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, FindAddress.FindAddressListener {
    ActivityAddTicketBinding binding;
    public static String TAG = "AddTicketActivity";
    LeafManager leafManager;
    private ArrayList<IssueListResponse.IssueData> resultIssue;

    private ArrayList<SubBoothResponse.TeamData> resultTeam;

    public Uri imageCaptureFile;

    public static final int REQUEST_LOAD_CAMERA_IMAGE = 101;
    public static final int REQUEST_LOAD_GALLERY_IMAGE = 102;
    public static final int REQUEST_LOAD_PDF = 103;
    public static final int REQUEST_LOAD_VIDEO = 104;

    String videoUrl = "";
    String fileTypeImageOrVideo;

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;


    TextView btn_ok;
    TextView btn_cancel;
    TextView btn_upload;

    EditText edt_link;

    Dialog dialog;


    ArrayList<String> listAmazonS3Url = new ArrayList<>();
    ArrayList<String> listImages = new ArrayList<>();
    private String pdfPath = "";
    private TransferUtility transferUtility;
    private AddTicketRequest mainRequest;
    private File cameraFile;
    private UploadImageAdapter imageAdapter;
    private String receiverToken = "";
    private String receiverDeviceType = "";
    private ProgressDialog progressDialog;
    private boolean isEdit;
    private String chapter_id;

    private String sharePath;

    String TeamId = "",IssueID = "";
    String Team = "Select Your Team",Issue = "Select Your Issue";


    boolean gotAddress;
    boolean gotGPS = false;
    String lat, lng,landMark,pinCode,boothNumber;

    private static final int GOOGLE_API_CLIENT_ID = 122;
    private static final int LOCATION_PERMISSION = 1;
    GoogleApiClient mGoogleApiClient;




    Handler handler;
    Runnable gpsTimer = new Runnable() {
        @Override
        public void run() {
            if (!gotGPS) {
                locationUpdate(null);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_ticket);
        init();

        setListener();

        if(!TextUtils.isEmpty(sharePath)){
            fileTypeImageOrVideo = Constants.FILE_TYPE_VIDEO;
            listImages.add(sharePath);
            showLastImage();
        }

        ArrayList<String> shareList = LeafApplication.getInstance().getShareFileList();
        if(shareList!=null && shareList.size()>0){
            SMBDialogUtils.showSMBDialogYesNoCancel(this, "Attach Selected file?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    String fileType = LeafApplication.getInstance().getType();
                    if(Constants.FILE_TYPE_IMAGE.equalsIgnoreCase(fileType)
                            || Constants.FILE_TYPE_VIDEO.equalsIgnoreCase(fileType)){
                        listImages.addAll(shareList);
                        fileTypeImageOrVideo = fileType;
                        showLastImage();
                    }else if(Constants.FILE_TYPE_PDF.equalsIgnoreCase(fileType)){
                        pdfPath = shareList.get(0);
                        Picasso.with(AddTicketActivity.this).load(R.drawable.pdf_thumbnail).into(binding.imgDoc);
                    }
                }
            });
        }

    }



    private void init()
    {
        ButterKnife.bind(this);

        leafManager = new LeafManager();
        transferUtility = AmazoneHelper.getTransferUtility(this);

        leafManager.getIssues(this,GroupDashboardActivityNew.groupId);

        gotAddress = false;
        gotGPS = false;
        handler = new Handler();

        binding.progressBar.setVisibility(View.VISIBLE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_add_tikit));

        binding.rvImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new UploadImageAdapter(listImages, this);
        binding.rvImages.setAdapter(imageAdapter);

    }
    private void setListener() {

        binding.llImage.setOnClickListener(this);
        binding.llVideo.setOnClickListener(this);
        binding.llYoutubeLink.setOnClickListener(this);
        binding.llDoc.setOnClickListener(this);
        binding.etLocation.setOnClickListener(this);

        binding.btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!gotAddress)
            getGPS();
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.e(TAG, "OnStop");

        // = false;
        if (handler != null)
            handler.removeCallbacks(gpsTimer);

        if (mGoogleApiClient != null) {
            mGoogleApiClient.stopAutoManage(AddTicketActivity.this);
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        binding.progressBar.setVisibility(View.GONE);

        switch (apiId)
        {
            case LeafManager.API_ISSUE_LIST:
                IssueListResponse res = (IssueListResponse) response;
                resultIssue = res.getData();
                AppLog.e(TAG, "ClassResponse " + resultIssue);
                bindIssue();
                binding.progressBar.setVisibility(View.VISIBLE);
                leafManager.getSubBooth(this,GroupDashboardActivityNew.groupId);
                break;

            case LeafManager.API_SUB_BOOTH_TEAM_LIST:
                SubBoothResponse res1 = (SubBoothResponse) response;
                resultTeam = res1.getData();
                AppLog.e(TAG, "ClassResponse " + response);
                bindTeam();
                break;

            case LeafManager.API_ADD_TICKET:
                BaseResponse res2 = (BaseResponse) response;
                AppLog.e(TAG, "ClassResponse " + res2);
                finish();
                break;
        }

    }


    @Override
    public void onFailure(int apiId, String msg) {
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        binding.progressBar.setVisibility(View.GONE);
    }

    private void bindIssue() {

        if (resultIssue != null && resultIssue.size() > 0) {

            String[] strIssue = new String[resultIssue.size()+1];

            for (int i=0;i<resultIssue.size();i++){
                strIssue[i]=resultIssue.get(i).issue;
            }
            strIssue[strIssue.length-1]="Select Your Issue";

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_spinner_white,strIssue);
            binding.spIssue.setAdapter(adapter);
            binding.spIssue.setSelection(strIssue.length-1);

            binding.spIssue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    AppLog.e(TAG, "onItemSelected : " + position);

                    if(position != strIssue.length-1){
                        Issue = resultIssue.get(position).issue;
                        IssueID = resultIssue.get(position).issueId;
                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void bindTeam() {

        if (resultTeam != null && resultTeam.size() > 0) {

            String[] strTeam = new String[resultTeam.size()+1];

            for (int i=0;i<resultTeam.size();i++){
                strTeam[i]=resultTeam.get(i).name;
            }
            strTeam[strTeam.length-1]="Select Your Team";

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_spinner_white,strTeam);
            binding.spTeam.setAdapter(adapter);
            binding.spTeam.setSelection(strTeam.length-1);

            binding.spTeam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    AppLog.e(TAG, "onItemSelected : " + position);

                    if(position != strTeam.length-1){
                        Team = resultTeam.get(position).name;
                        IssueID = resultTeam.get(position).teamId;
                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.llImage:
                if (checkPermissionForWriteExternal()) {
                   /* if (listImages.size() > 0) {
                        showPhotoDialog(R.array.array_image_modify);
                    } else {*/
                    showPhotoDialog(R.array.array_image);
                    //}

                } else {
                    requestPermissionForWriteExternal(21);
                }
                break;

            case R.id.llVideo:
                if (checkPermissionForWriteExternal()) {
                    selectVideoIntent();
                } else {
                    requestPermissionForWriteExternal(22);
                }
                break;
            case R.id.llYoutubeLink:
                // if (checkPermissionForWriteExternal()) {
                showYoutubeDialog();
                /*} else {
                    requestPermissionForWriteExternal(22);
                }*/
                break;
            case R.id.llDoc:
                if (checkPermissionForWriteExternal()) {
                    selectPdf(REQUEST_LOAD_PDF);
                } else {
                    requestPermissionForWriteExternal(23);
                }
                break;

            case R.id.etLocation:
                selectPlace();
                break;
            case R.id.btnSubmit:
                addTicket();
                break;

        }
    }


    public void addTicket() {
        hide_keyboard();

        if (isConnectionAvailable()) {
            if (isValid(true)) {

                if (binding.progressBar != null)
                    binding.progressBar.setVisibility(View.VISIBLE);

                mainRequest = new AddTicketRequest();

                mainRequest.text = binding.etDescription.getText().toString();
                mainRequest.location = new AddTicketRequest.LocationData();
                mainRequest.location.landmark = landMark;
                mainRequest.location.address = binding.etLocation.getText().toString();
                mainRequest.location.latitude = lat;
                mainRequest.location.longitude = lng;
                mainRequest.location.pincode = pinCode;

                if (!TextUtils.isEmpty(videoUrl)) {

                    mainRequest.fileType = Constants.FILE_TYPE_YOUTUBE;

                    Log.e(TAG, "send data " + new Gson().toJson(mainRequest));
                    mainRequest.fileType = Constants.FILE_TYPE_YOUTUBE;
                    mainRequest.fileName = new ArrayList<>();
                    mainRequest.fileName.add(videoUrl);

                    leafManager.addTicket(this, GroupDashboardActivityNew.groupId, TeamId, IssueID, mainRequest);

                } else if (!TextUtils.isEmpty(pdfPath)) {
                    mainRequest.fileType = Constants.FILE_TYPE_PDF;
                    progressDialog.setMessage("Preparing Pdf...");
                    progressDialog.show();
                    uploadToAmazone(mainRequest);
                } else if (listImages.size() > 0 && Constants.FILE_TYPE_VIDEO.equals(fileTypeImageOrVideo)) {
                    mainRequest.fileType = fileTypeImageOrVideo;
                    Log.e(TAG, "send data " + new Gson().toJson(mainRequest));
                 //   startService();
//                    new VideoCompressor(request).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else if (listImages.size() > 0) {
                    mainRequest.fileType = Constants.FILE_TYPE_IMAGE;
                    progressDialog.setMessage("Uploading Image...");
                    progressDialog.show();
                    uploadToAmazone(mainRequest);
                }
            }
        } else {
            showNoNetworkMsg();
        }

    }

    public boolean isValid(boolean showToast) {
        boolean valid = true;


        Log.e("videoUrl : ", videoUrl);
        Log.e("image paths : ", listImages.toString());
        Log.e("videoType : ", fileTypeImageOrVideo + "");

        if (Team.equalsIgnoreCase("Select Your Team")) {
            if (showToast)
                Toast.makeText(this, "Please Select Team", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Issue.equalsIgnoreCase("Select Your Issue")) {
            if (showToast)
                Toast.makeText(this, "Please Select Issue", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (binding.etDescription.getText().toString().isEmpty()) {
            if (showToast)
                Toast.makeText(this, "Please Enter Description", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.etLocation.getText().toString().isEmpty()) {
            if (showToast)
                Toast.makeText(this, "Please Select Location", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (listImages.size() == 0 && TextUtils.isEmpty(videoUrl) && TextUtils.isEmpty(pdfPath)) {
            if (showToast)
                Toast.makeText(this, "Please Add Image or video or pdf", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (!TextUtils.isEmpty(videoUrl) && listImages.size() > 0) {
            valid = false;
            removeImage();
            removePdf();
            Toast.makeText(this, "" + getResources().getString(R.string.msg_upload2), Toast.LENGTH_SHORT).show();
        }
        AppLog.e(TAG, "valid : " + valid);
        return valid;
    }

    public void showPhotoDialog(int resId) {
        SMBDialogUtils.showSMBSingleChoiceDialog(AddTicketActivity.this,
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
            cameraFile = ImageUtil.getOutputMediaFile();
            imageCaptureFile = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", cameraFile);
        } else {
            cameraFile = ImageUtil.getOutputMediaFile();
            imageCaptureFile = Uri.fromFile(cameraFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureFile);
        startActivityForResult(intent, requestCode);

    }

    private void selectVideoIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("video/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Video"), REQUEST_LOAD_VIDEO);
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
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }


        if ( requestCode == Constants.KEY_RESULT_ADDRESS && resultCode == Activity.RESULT_OK)
        {
            gotAddress(data.getExtras());
        }

        else if (requestCode == REQUEST_LOAD_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            listImages.clear();
            fileTypeImageOrVideo = Constants.FILE_TYPE_IMAGE;
            final Uri selectedImage = data.getData();
            ClipData clipData = data.getClipData();
            if (clipData == null) {
//                String path = ImageUtil.getPath(this, selectedImage);
                listImages.add(selectedImage.toString());
            } else {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    final Uri uri1 = item.getUri();
//                    String path = ImageUtil.getPath(this, uri1);
                    listImages.add(uri1.toString());
                }
            }
            showLastImage();
            removePdf();

        }

        else if (requestCode == REQUEST_LOAD_CAMERA_IMAGE && resultCode == Activity.RESULT_OK) {
            listImages.clear();
            fileTypeImageOrVideo = Constants.FILE_TYPE_IMAGE;
//            String path = cameraFile.getAbsolutePath();
            AppLog.e(TAG, "imageCaptureFile : " + imageCaptureFile);
            listImages.add(imageCaptureFile.toString());
            showLastImage();
            removePdf();
        }  else if (requestCode == REQUEST_LOAD_VIDEO && resultCode == Activity.RESULT_OK) {
            listImages.clear();
            fileTypeImageOrVideo = Constants.FILE_TYPE_VIDEO;
            final Uri selectedImage = data.getData();
            AppLog.e(TAG, "selectedVideo : " + selectedImage);
            ClipData clipData = data.getClipData();
            if (clipData == null) {
//                String path = ImageUtil.getPath(this, selectedImage);
                listImages.add(selectedImage.toString());
            } else {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    final Uri uri1 = item.getUri();
//                    String path = ImageUtil.getPath(this, uri1);
                    listImages.add(uri1.toString());
                }
            }
            showLastImage();
            removePdf();
        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_LOAD_PDF) {
                pdfPath = data.getData().toString();
                Log.e("pdfUri : ", pdfPath);
               /* if (selectedImageURI.toString().startsWith("content")) {
                    pdfUri = ImageUtil.getPath(this, selectedImageURI);
                } else {
                    pdfUri = selectedImageURI.getPath();
                }
*/
                if (TextUtils.isEmpty(pdfPath)) {
                    Toast.makeText(getApplicationContext(), "Please select a pdf file", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.e("PDF", "imgUrl is " + pdfPath);

                if (!TextUtils.isEmpty(pdfPath))
                    Picasso.with(this).load(R.drawable.pdf_thumbnail).into(binding.imgDoc);
                removeImage();
            }
        }


    }

    private void showLastImage() {
        imageAdapter.setType(fileTypeImageOrVideo);
        imageAdapter.notifyDataSetChanged();
    }

    private void removeImage() {
        listImages.clear();
        Picasso.with(this).load(R.drawable.icon_gallery).into(binding.imgImage);
        showLastImage();
    }

    private void removePdf() {
        pdfPath = "";
        Picasso.with(this).load(R.drawable.icon_doc).into(binding.imgDoc);
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
                    selectVideoIntent();
                    Log.e("AddPost" + "permission", "granted camera");
                } else {
                    Log.e("AddPost" + "permission", "denied camera");
                }
                break;
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
                    Toast.makeText(AddTicketActivity.this, "Enter youtube link", Toast.LENGTH_SHORT).show();
                else {
                    String videoId = "";
                    videoId = extractYoutubeId(videoUrl);

                    if (GroupDashboardActivityNew.selectedUrl.equals(videoUrl))
                        videoId = GroupDashboardActivityNew.selectedYoutubeId;

                    Log.e("VideoId is->", "" + videoId);

                    String img_url = "http://img.youtube.com/vi/" + videoId + "/0.jpg"; // this is link which will give u thumnail image of that video

                    Log.e("img_url is->", "" + img_url);

                    Picasso.with(AddTicketActivity.this)
                            .load(img_url)
                            .placeholder(R.drawable.icon_popup_youtube)
                            .into(binding.imgYoutube, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Log.e("onSuccess is->", "onSuccess");
                                }

                                @Override
                                public void onError() {
                                    Log.e("onError is->", "onError");
                                    videoUrl = "";
                                    Toast.makeText(AddTicketActivity.this, "Not a valid youtube link", Toast.LENGTH_SHORT).show();
                                }
                            });
                    dialog.dismiss();
                }
            }
        });


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*Picasso.with(AddPostActivity.this)
                        .load(R.drawable.icon_popup_youtube)
                        .into(img_youtube);*/
                hide_keyboard(edt_link);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onImageSelect() {

    }

    @Override
    public void onImageRemove() {

    }
    private void uploadToAmazone(AddTicketRequest request) {
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
                    if (listThumbnails != null) {
                        uploadThumbnail(listThumbnails, 0);
                    } else {
                        Toast.makeText(AddTicketActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    }

                }
            }, Constants.FILE_TYPE_PDF);
        } else if (request.fileType.equals(Constants.FILE_TYPE_VIDEO)) {
            AppLog.e(TAG, "Final videos :: " + listImages.toString());
            GetThumbnail.create(listImages, new GetThumbnail.GetThumbnailListener() {
                @Override
                public void onThumbnail(ArrayList<String> listThumbnails) {
                    if (listThumbnails != null) {
                        uploadThumbnail(listThumbnails, 0);
                    } else {
                        Toast.makeText(AddTicketActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    }

                }
            }, Constants.FILE_TYPE_VIDEO);
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

            upLoadImageOnCloud(0);
        } else {
            final String key = AmazoneHelper.getAmazonS3KeyThumbnail(mainRequest.fileType);

            TransferObserver observer ;
            UploadOptions option = UploadOptions.
                    builder().bucket(AmazoneHelper.BUCKET_NAME).
                    cannedAcl(CannedAccessControlList.PublicRead).build();
            try {
                observer = transferUtility.upload(key,
                        getContentResolver().openInputStream(Uri.parse(listThumbnails.get(index))), option);
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

                            listThumbnails.set(index, _finalUrl);

                            uploadThumbnail(listThumbnails, index + 1);

                        }
                        if (TransferState.FAILED.equals(state)) {
                            binding.progressBar.setVisibility(View.GONE);
                            if (progressDialog!=null) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(AddTicketActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
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
                        binding.progressBar.setVisibility(View.GONE);
                        if (progressDialog!=null) {
                            progressDialog.dismiss();
                        }
                        AppLog.e(TAG, "Upload Error : " + ex);
                        Toast.makeText(AddTicketActivity.this, getResources().getString(R.string.image_upload_error), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    private void upLoadImageOnCloud(final int pos) {

        if (pos == listImages.size()) {
            if (progressDialog!=null) {
                progressDialog.dismiss();
            }
            mainRequest.fileName = listAmazonS3Url;
            AppLog.e(TAG, "send data : " + new Gson().toJson(mainRequest));

            leafManager.addTicket(this, GroupDashboardActivityNew.groupId, TeamId, IssueID, mainRequest);


        } else {
            final String key = AmazoneHelper.getAmazonS3Key(mainRequest.fileType);

            TransferObserver observer ;
            UploadOptions option = UploadOptions.
                    builder().bucket(AmazoneHelper.BUCKET_NAME).
                    cannedAcl(CannedAccessControlList.PublicRead).build();
            try {
                observer = transferUtility.upload(key,
                        getContentResolver().openInputStream(Uri.parse(listImages.get(pos))), option);
                observer.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        AppLog.e(TAG, "onStateChanged: " + id + ", " + state.name());
                        if (state.toString().equalsIgnoreCase("COMPLETED")) {
                            Log.e("MULTI_IMAGE", "onStateChanged " + pos);
                            updateList(pos, key);
                        }
                        if (TransferState.FAILED.equals(state)) {
                            Toast.makeText(AddTicketActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
                            if(progressDialog!=null)
                                progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                        int percentDone = (int) percentDonef;
                        if (Constants.FILE_TYPE_VIDEO.equals(mainRequest.fileType)) {
                            progressDialog.setMessage("Uploading Video " + percentDone + "% " + (pos + 1) + " out of " + listImages.size() + ", please wait...");
                        } else if (Constants.FILE_TYPE_PDF.equals(mainRequest.fileType)) {
                            progressDialog.setMessage("Uploading Pdf " + percentDone + "% " + (pos + 1) + " out of " + listImages.size() + ", please wait...");
                        } else if (Constants.FILE_TYPE_IMAGE.equals(mainRequest.fileType)) {
                            progressDialog.setMessage("Uploading Image " + percentDone + "% " + (pos + 1) + " out of " + listImages.size() + ", please wait...");
                        }

                        AppLog.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                                + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        binding.progressBar.setVisibility(View.GONE);
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        AppLog.e(TAG, "Upload Error : " + ex);
                        Toast.makeText(AddTicketActivity.this, getResources().getString(R.string.image_upload_error), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }


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

//Location
    private void selectPlace() {

        Intent intent = new Intent(getApplicationContext(), AddressActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("frompreview", true);
        intent.putExtras(bundle);
        startActivityForResult(intent, Constants.KEY_RESULT_ADDRESS);
    }

    public void gotAddress(Bundle bundlenew) {

        gotAddress = true;

        Log.e(TAG, "GotADrress " + bundlenew.getString(Constants.KEY_BUNDLE_ADDRESS));

        binding.etLocation.setText(bundlenew.getString(Constants.KEY_BUNDLE_ADDRESS));
        Address locationData = bundlenew.getParcelable(Constants.KEY_BUNDLE_LOCATION);
        landMark = bundlenew.getString(Constants.KEY_BUNDLE_ADDRESS);
        pinCode = bundlenew.getString(Constants.KEY_BUNDLE_POSTAL);
        lat = String.valueOf(locationData.getLatitude());
        lng = String.valueOf(locationData.getLongitude());

       // Log.e(TAG, "OwnerLatLong" + locationData + "\n" + owner_long);



        Address location = bundlenew.getParcelable(Constants.KEY_BUNDLE_LOCATION);


        if (location != null) {
            lat = "" + location.getLatitude();
            lng = "" + location.getLongitude();
        } else {
            return;
        }
        //refreshScreen();

       /* if(maxRange <= 25)
        {
            if(lat !=null)
            PreviewListFragment.getPreviewList(categoryId,subCategoryId,lat,lng,range,catName);
        }
        else
        {*/
        if (lat != null)
            //PreviewListFragment.getPreviewList(categoryId, subCategoryId, filters, filterNames, lat, lng, (maxRange * range) / 5, catName);
            // }

            Log.e(TAG, "gotAddress: ");
    }


    public void getGPS() {
        int hasPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasPermission == PackageManager.PERMISSION_GRANTED) {
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                        .addApi(Places.GEO_DATA_API)
                        .addApi(LocationServices.API)
                        .enableAutoManage(AddTicketActivity.this, GOOGLE_API_CLIENT_ID, this)
                        .addConnectionCallbacks(this)
                        .build();
            }
            mGoogleApiClient.connect();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
            }
        }

    }

    @Override
    public void locationUpdate(android.location.Location location) {
        Log.e(TAG, "LocationUpdateFromFusedAPI");

        gotGPS = true;

        /*if (getLocation != null) {
            getLocation.stopFusedLocationUpdates();
            getLocation.stopNetworkLocationUpdates();
        }*/

        if (handler != null)
            handler.removeCallbacks(gpsTimer);


        if (location == null) {
            FindAddress findAddress = new FindAddress(location.getLatitude(), location.getLongitude(), getApplicationContext(), this);
            findAddress.execute();

            lat = "" + location.getLatitude();
            lng = "" + location.getLongitude();

            return;
        } else {


            //Address Detection Removed

            //sbRange.setProgress(15);
        }

        // showDialog();
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationDetect(Address locality) {

    }


}