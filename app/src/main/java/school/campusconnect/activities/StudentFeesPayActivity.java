package school.campusconnect.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.mobileconnectors.s3.transferutility.UploadOptions;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;
import school.campusconnect.Assymetric.Utils;
import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.adapters.PaidDateAdapter;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.fees.DueDates;
import school.campusconnect.datamodel.fees.PayFeesRequest;
import school.campusconnect.datamodel.fees.StudentFeesRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneHelper;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.views.SMBDialogUtils;

public class StudentFeesPayActivity extends BaseActivity {

    private static final String TAG = "StudentFeesActivity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;

    @Bind(R.id.etTotalFees)
    public EditText etTotalFees;
    @Bind(R.id.etTotalPaid)
    public EditText etTotalPaid;
    @Bind(R.id.etDueAmount)
    public EditText etDueAmount;
    @Bind(R.id.btnPay)
    public Button btnPay;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.etDueAmountPay)
    EditText etDueAmountPay;
    @Bind(R.id.etDate)
    EditText etDate;
    @Bind(R.id.etMode)
    EditText etMode;
    @Bind(R.id.imgAttach)
    ImageView imgAttach;
    String team_id;
    String user_id;
    String groupId;

    StudentFeesRes.StudentFees resData;

    ArrayList<String> listAmazonS3Url = new ArrayList<>();
    ArrayList<String> listImages = new ArrayList<>();
    private TransferUtility transferUtility;
    private ProgressDialog progressDialog;

    public Uri imageCaptureFile;
    public static final int REQUEST_LOAD_CAMERA_IMAGE = 101;
    public static final int REQUEST_LOAD_GALLERY_IMAGE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_fees_pay);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getIntent().getStringExtra("title"));

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        transferUtility = AmazoneHelper.getTransferUtility(this);

        groupId = getIntent().getStringExtra("groupId");
        team_id = getIntent().getStringExtra("team_id");
        user_id = getIntent().getStringExtra("user_id");
        resData = new Gson().fromJson(getIntent().getStringExtra("resData"), StudentFeesRes.StudentFees.class);
        AppLog.e(TAG, "team_id : " + team_id);
        AppLog.e(TAG, "user_id : " + user_id);


        etTotalPaid.setText(TextUtils.isEmpty(resData.totalAmountPaid) ? "0" : resData.totalAmountPaid);
        etTotalFees.setText(resData.totalFee);
        try {
            if (!TextUtils.isEmpty(resData.totalFee) && !TextUtils.isEmpty(resData.totalAmountPaid)) {
                int dueAmount = Integer.parseInt(resData.totalFee) - Integer.parseInt(resData.totalAmountPaid);
                etDueAmount.setText(dueAmount + "");
            } else {
                etDueAmount.setText(resData.totalFee);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payFeesApi();
            }
        });
        imgAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissionForWriteExternal()) {
                    showPhotoDialog(R.array.array_image);
                }else {
                    requestPermissionForWriteExternal(21);
                }
            }
        });
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog fragment = new DatePickerDialog(StudentFeesPayActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        etDate.setText(format.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
//                fragment.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
                fragment.show();
            }
        });
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
            Toast.makeText(this, getResources().getString(R.string.toast_storage_permission_needed), Toast.LENGTH_LONG).show();
        } else {
            AppLog.e(TAG, "requestPermissionForWriteExternal");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
        }
    }
    public void showPhotoDialog(int resId) {
        SMBDialogUtils.showSMBSingleChoiceDialog(StudentFeesPayActivity.this,
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
    protected void startGallery(int requestCode) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), requestCode);
    }
    private void removeImage() {
        listImages.clear();
        Picasso.with(this).load(R.drawable.icon_gallery).into(imgAttach);
    }
    private void startCamera(int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File cameraFile;
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (requestCode == REQUEST_LOAD_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            listImages.clear();
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
            if(listImages.size()>0){
                Picasso.with(this).load(listImages.get(listImages.size() - 1)).resize(100, 100).into(imgAttach);
            }
        } else if (requestCode == REQUEST_LOAD_CAMERA_IMAGE && resultCode == Activity.RESULT_OK) {
            listImages.clear();
//            String path = cameraFile.getAbsolutePath();
            AppLog.e(TAG, "imageCaptureFile : " + imageCaptureFile);
            listImages.add(imageCaptureFile.toString());
            if(listImages.size()>0){
                Picasso.with(this).load(listImages.get(listImages.size() - 1)).resize(100, 100).into(imgAttach);
            }
        }
    }

    PayFeesRequest payFeesRequest;

    private void payFeesApi() {
        if (isValid()) {
            if (!isConnectionAvailable()) {
                showNoNetworkMsg();
                return;
            }
            payFeesRequest = new PayFeesRequest();
            payFeesRequest.paidDate = etDate.getText().toString();
            payFeesRequest.amountPaid = etDueAmountPay.getText().toString();
            payFeesRequest.paymentMode = etMode.getText().toString();
            progressDialog.setMessage("Uploading Image...");
            progressDialog.show();
            uploadToAmazone();
        }
    }

    private void uploadToAmazone() {
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

    private boolean isValid() {
        boolean valid = true;

        try {
            int actualDue = Integer.parseInt(etDueAmount.getText().toString());
            int payDue = Integer.parseInt(etDueAmountPay.getText().toString());
            if(payDue>actualDue){
                Toast.makeText(this, getResources().getString(R.string.toast_paid_amount_should_not_grater_than_amount), Toast.LENGTH_SHORT).show();
                valid = false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if (!isValueValid(etDueAmountPay)) {
            valid = false;
        } else if (!isValueValid(etDate)) {
            etDueAmountPay.setError(null);
            valid = false;
        } else if (!isValueValid(etMode)) {
            etDueAmountPay.setError(null);
            etDate.setError(null);
            valid = false;
        } else if (listImages.size() == 0) {
            etDueAmountPay.setError(null);
            etDate.setError(null);
            etMode.setError(null);
            Toast.makeText(this, getResources().getString(R.string.toast_please_add_attachment), Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

        Toast.makeText(StudentFeesPayActivity.this, getResources().getString(R.string.toast_successfully_paid), Toast.LENGTH_SHORT).show();
        LeafPreference.getInstance(StudentFeesPayActivity.this).setBoolean("fees_paid", true);
        finish();
    }

    @Override
    public void onFailure(int apiId, String msg) {
        super.onFailure(apiId, msg);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        super.onException(apiId, msg);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        Toast.makeText(StudentFeesPayActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void upLoadImageOnCloud(final int pos) {

        if (pos == listImages.size()) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            payFeesRequest.attachment = listAmazonS3Url;
            AppLog.e(TAG, "send data : " + new Gson().toJson(payFeesRequest));

            LeafManager leafManager = new LeafManager();
            leafManager.payFeesByStudent(this, groupId, team_id, user_id, payFeesRequest);

        } else {
            final String key = AmazoneHelper.getAmazonS3Key(Constants.FILE_TYPE_IMAGE);

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
                            Toast.makeText(StudentFeesPayActivity.this, getResources().getString(R.string.toast_upload_failed), Toast.LENGTH_SHORT).show();
                            if (progressDialog != null)
                                progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                        int percentDone = (int) percentDonef;
                        progressDialog.setMessage("Uploading Image " + percentDone + "% " + (pos + 1) + " out of " + listImages.size() + ", please wait...");

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
                        Toast.makeText(StudentFeesPayActivity.this, getResources().getString(R.string.image_upload_error), Toast.LENGTH_SHORT).show();
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

}
