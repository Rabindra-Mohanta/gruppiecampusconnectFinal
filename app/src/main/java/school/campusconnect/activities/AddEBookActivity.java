package school.campusconnect.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.mobileconnectors.s3.transferutility.UploadOptions;
import com.amazonaws.services.s3.model.CannedAccessControlList;

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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.adapters.AddEBookAdapter;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.GroupValidationError;
import school.campusconnect.datamodel.ebook.AddEbookReq;
import school.campusconnect.datamodel.ebook.EBooksResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneHelper;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.GetThumbnail;
import school.campusconnect.utils.ImageUtil;

public class AddEBookActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<GroupValidationError> {

    private static final String TAG = "CreateTeamActivity";

    @Bind(R.id.etPdf)
    TextView etPdf;

    @Bind(R.id.tvSubject)
    EditText etSubject;

    @Bind(R.id.etName)
    EditText etName;

    @Bind(R.id.etDesc)
    EditText etDesc;

    @Bind(R.id.imgAdd)
    ImageView imgAdd;

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.rvSubjects)
    RecyclerView rvSubjects;

    LeafManager leafManager;

    AddEBookAdapter adapter = new AddEBookAdapter();

    ArrayList<String> pdfSelected = new ArrayList<>();

    public int REQUEST_LOAD_PDF = 103;
    private ProgressDialog progressDialog;
    private AddEbookReq mainRequest;
    private TransferUtility transferUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ebook);

        init();

    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        transferUtility = AmazoneHelper.getTransferUtility(this);

        ButterKnife.bind(this);

        rvSubjects.setAdapter(adapter);

        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_add_ebook));
        leafManager = new LeafManager();

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etSubject.getText().toString().trim())) {
                    Toast.makeText(AddEBookActivity.this, getResources().getString(R.string.toast_please_add_subject), Toast.LENGTH_SHORT).show();
                    etSubject.requestFocus();
                } else if (pdfSelected.size() == 0) {
                    Toast.makeText(AddEBookActivity.this, getResources().getString(R.string.toast_select_pdf), Toast.LENGTH_SHORT).show();
                } else {
                    adapter.add(new EBooksResponse.SubjectBook(etSubject.getText().toString(), etDesc.getText().toString(), pdfSelected));
                    hide_keyboard(view);
                    pdfSelected.clear();
                    etSubject.setText("");
                    etDesc.setText("");
                    etPdf.setText(getResources().getString(R.string.hint_select_Pdf_Book));
                    etSubject.requestFocus();
                }
            }
        });


    }

    public void hide_keyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (view == null) {
            view = new View(this);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @OnClick({R.id.btnCreateClass, R.id.etPdf})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCreateClass:
                if (isValid()) {
                    if (!TextUtils.isEmpty(etSubject.getText().toString().trim()) && pdfSelected.size() > 0) {

                    } else if (TextUtils.isEmpty(etSubject.getText().toString().trim()) && pdfSelected.size() == 0) {
                        if (adapter.getList().size() == 0) {
                            Toast.makeText(this, getResources().getString(R.string.toast_add_one_subject_pdf), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        if (TextUtils.isEmpty(etSubject.getText().toString().trim())) {
                            Toast.makeText(AddEBookActivity.this, getResources().getString(R.string.toast_please_add_subject), Toast.LENGTH_SHORT).show();
                            etSubject.requestFocus();
                            return;
                        } else if (pdfSelected.size() == 0) {
                            Toast.makeText(AddEBookActivity.this,  getResources().getString(R.string.toast_select_pdf), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if (!isConnectionAvailable()) {
                        showNoNetworkMsg();
                        return;
                    }

                    AddEbookReq request = new AddEbookReq();
                    request.className = etName.getText().toString();
                    request.subjectBooks = adapter.getList();
                    if (!TextUtils.isEmpty(etSubject.getText().toString().trim()) && pdfSelected.size() > 0) {
                        request.subjectBooks.add((new EBooksResponse.SubjectBook(etSubject.getText().toString(), etDesc.getText().toString(), pdfSelected)));
                    }
                    AppLog.e(TAG, "request :" + request);
                    progressDialog.setMessage("Uploading Pdf...");
                    progressDialog.show();
                    uploadToAmazone(request);

                }
                break;

            case R.id.etPdf:
                if (checkPermissionForWriteExternal()) {
                    selectPdf();
                } else {
                    requestPermissionForWriteExternal(23);
                }
                break;
        }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 23) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectPdf();
            } else {
                Log.e("AddPost" + "permission", "denied camera");
            }
        }
    }

    public boolean isValid() {
        boolean valid = true;
        if (!isValueValidOnly(etName)) {
            Toast.makeText(this, getResources().getString(R.string.toast_enter_class_name), Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        if (progressBar != null)
            hideLoadingBar();
        //progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_EBOOK_REGISTER:

                new SendNotification().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                finish();
                break;

        }
    }

    private class SendNotification extends AsyncTask<String, String, String> {
        private String server_response;

        public SendNotification() {

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
                    String name = LeafPreference.getInstance(AddEBookActivity.this).getString(LeafPreference.NAME);
                    String message = name + " has added new ebook.";
                    topic = GroupDashboardActivityNew.groupId;
                    object.put("to", "/topics/" + topic);

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title", title);
                    notificationObj.put("body", message);
                    //  object.put("notification", notificationObj);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("groupId", GroupDashboardActivityNew.groupId);
                    dataObj.put("createdById", LeafPreference.getInstance(AddEBookActivity.this).getString(LeafPreference.LOGIN_ID));
                    dataObj.put("teamId", "");
                    dataObj.put("title", title);
                    dataObj.put("Notification_type", "EBookAdd");
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

    @Override
    public void onFailure(int apiId, ErrorResponseModel<GroupValidationError> error) {
        if (progressBar != null)
            hideLoadingBar();
        //progressBar.setVisibility(View.GONE);

        if (error.status.equals("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            if (apiId == LeafManager.API_SUBJECTS_DELETE) {
                GroupValidationError groupValidationError = (GroupValidationError) error;
                Toast.makeText(this, groupValidationError.message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, error.title, Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Override
    public void onException(int apiId, String msg) {
        super.onException(apiId, msg);
        if (progressBar != null)
            hideLoadingBar();
        //progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }

    private void selectPdf() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        intent.setType("application/pdf");
        startActivityForResult(intent, REQUEST_LOAD_PDF);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        if (requestCode == REQUEST_LOAD_PDF && resultCode == Activity.RESULT_OK && data != null) {
            pdfSelected.clear();
            etPdf.setText("");
            ClipData clipData = data.getClipData();
            if (clipData == null) {
                Uri selectedImageURI = data.getData();
                Log.e("SelectedURI : ", selectedImageURI.toString());
                /*if (selectedImageURI.toString().startsWith("content")) {
                    pdfSelected.add(ImageUtil.getPath(this, selectedImageURI));
                } else {
                    pdfSelected.add(selectedImageURI.getPath());
                }*/
                pdfSelected.add(selectedImageURI.toString());
            } else {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    final Uri uri1 = item.getUri();
//                    String path = ImageUtil.getPath(this, uri1);
                    pdfSelected.add(uri1.toString());
                }
            }
            if (pdfSelected.size() == 0) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_select_pdf), Toast.LENGTH_SHORT).show();
            } else {
                etPdf.setText(pdfSelected.size() + getResources().getString(R.string.txt_book_selected));
            }
        }
    }

    ArrayList<ArrayList<String>> thumbnailListMulti = new ArrayList<>();

    private void uploadToAmazone(AddEbookReq request) {
        mainRequest = request;
        thumbnailListMulti.clear();
        createMultipleListThumbnail(0);
    }

    private void createMultipleListThumbnail(int index) {
        if (mainRequest.subjectBooks.size() == index) {
            upLoadThumbnailOnCloud(0, 0);
        } else {
            GetThumbnail.create(mainRequest.subjectBooks.get(index).fileName, new GetThumbnail.GetThumbnailListener() {
                @Override
                public void onThumbnail(ArrayList<String> listThumbnails) {
                    if (listThumbnails != null) {
                        thumbnailListMulti.add(listThumbnails);
                        createMultipleListThumbnail(index + 1);
                    } else {
                        Toast.makeText(AddEBookActivity.this, getResources().getString(R.string.toast_upload_failed), Toast.LENGTH_SHORT).show();
                    }

                }
            }, Constants.FILE_TYPE_PDF);
        }
    }

    private void upLoadImageOnCloud(final int pos, final int pdfPos) {

        final String key = AmazoneHelper.getAmazonS3Key(mainRequest.subjectBooks.get(pos).fileType);

        TransferObserver observer;
        UploadOptions option = UploadOptions.
                builder().bucket(AmazoneHelper.BUCKET_NAME).
                cannedAcl(CannedAccessControlList.PublicRead).build();
        try {
            observer = transferUtility.upload(key,
                    getContentResolver().openInputStream(Uri.parse(mainRequest.subjectBooks.get(pos).fileName.get(pdfPos))), option);
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    AppLog.e(TAG, "onStateChanged: " + id + ", " + state.name());
                    if (state.toString().equalsIgnoreCase("COMPLETED")) {
                        Log.e("MULTI_IMAGE", "onStateChanged " + pos);
                        updateList(pos, pdfPos, key);
                    }
                    if (TransferState.FAILED.equals(state)) {
                        Toast.makeText(AddEBookActivity.this, getResources().getString(R.string.toast_upload_failed), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        hideLoadingBar();
                        //progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                    int percentDone = (int) percentDonef;
                    progressDialog.setMessage("Uploading Pdf " + percentDone + "% " + (pdfPos + 1) + "/" + mainRequest.subjectBooks.get(pos).fileName.size() + " From " + (pos + 1) + "/" + mainRequest.subjectBooks.size() + " Subject");
                    AppLog.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                            + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
                }

                @Override
                public void onError(int id, Exception ex) {
                    hideLoadingBar();
                    //progressBar.setVisibility(View.GONE);
                    progressDialog.dismiss();
                    AppLog.e(TAG, "Upload Error : " + ex);
                    Toast.makeText(AddEBookActivity.this, getResources().getString(R.string.upload_error), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void updateList(int pos, int pdfPos, String key) {
        String _finalUrl = AmazoneHelper.BUCKET_NAME_URL + key;

        Log.e("FINALURL", "url is " + _finalUrl);

        _finalUrl = Constants.encodeStringToBase64(_finalUrl);

        Log.e("FINALURL", "encoded url is " + _finalUrl);

        mainRequest.subjectBooks.get(pos).fileName.set(pdfPos, _finalUrl);
        pdfPos = pdfPos + 1;
        if (mainRequest.subjectBooks.get(pos).fileName.size() == pdfPos) {
            pos = pos + 1;
            if (pos == mainRequest.subjectBooks.size()) {

                for (int i = 0; i < mainRequest.subjectBooks.size(); i++) {
                    mainRequest.subjectBooks.get(i).thumbnailImage = thumbnailListMulti.get(i);
                }
                progressDialog.dismiss();
                AppLog.e(TAG, "mainRequest :" + mainRequest);
          //      progressBar.setVisibility(View.VISIBLE);
                showLoadingBar(progressBar);
                leafManager.addEBook(this, GroupDashboardActivityNew.groupId, mainRequest);
            } else {
                pdfPos = 0;
                upLoadImageOnCloud(pos, pdfPos);
            }
        } else {
            upLoadImageOnCloud(pos, pdfPos);
        }
    }


    private void upLoadThumbnailOnCloud(final int pos, final int pdfPos) {

        final String key = AmazoneHelper.getAmazonS3KeyThumbnail(Constants.FILE_TYPE_PDF);

        TransferObserver observer ;
        UploadOptions option = UploadOptions.
                builder().bucket(AmazoneHelper.BUCKET_NAME).
                cannedAcl(CannedAccessControlList.PublicRead).build();
        try {
            observer = transferUtility.upload(key,
                    getContentResolver().openInputStream(Uri.parse(thumbnailListMulti.get(pos).get(pdfPos))), option);
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    AppLog.e(TAG, "onStateChanged: " + id + ", " + state.name());
                    if (state.toString().equalsIgnoreCase("COMPLETED")) {
                        Log.e("MULTI_IMAGE", "onStateChanged " + pos);
                        updateThumbnailList(pos, pdfPos, key);
                    }
                    if (TransferState.FAILED.equals(state)) {
                        Toast.makeText(AddEBookActivity.this, getResources().getString(R.string.toast_upload_failed), Toast.LENGTH_SHORT).show();
                        hideLoadingBar();
                        //progressBar.setVisibility(View.GONE);
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                    int percentDone = (int) percentDonef;
                    //progressDialog.setMessage("Uploading Pdf " + percentDone + "% " + (pdfPos + 1) + "/" + mainRequest.subjectBooks.get(pos).fileName.size() + " From " + (pos + 1) + "/" + mainRequest.subjectBooks.size() + " Subject");
                    AppLog.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                            + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
                }

                @Override
                public void onError(int id, Exception ex) {
                    hideLoadingBar();
                    //progressBar.setVisibility(View.GONE);
                    progressDialog.dismiss();
                    AppLog.e(TAG, "Upload Error : " + ex);
                    Toast.makeText(AddEBookActivity.this, getResources().getString(R.string.upload_error), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    private void updateThumbnailList(int pos, int pdfPos, String key) {
        String _finalUrl = AmazoneHelper.BUCKET_NAME_URL + key;

        Log.e("FINALURL", "url is " + _finalUrl);

        _finalUrl = Constants.encodeStringToBase64(_finalUrl);

        Log.e("FINALURL", "encoded url is " + _finalUrl);

        thumbnailListMulti.get(pos).set(pdfPos, _finalUrl);
        pdfPos = pdfPos + 1;
        if (thumbnailListMulti.get(pos).size() == pdfPos) {
            pos = pos + 1;
            if (pos == thumbnailListMulti.size()) {
                upLoadImageOnCloud(0, 0);
            } else {
                pdfPos = 0;
                upLoadThumbnailOnCloud(pos, pdfPos);
            }
        } else {
            upLoadThumbnailOnCloud(pos, pdfPos);
        }
    }
}
