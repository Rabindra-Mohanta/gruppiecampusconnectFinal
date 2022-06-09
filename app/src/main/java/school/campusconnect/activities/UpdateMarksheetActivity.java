package school.campusconnect.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.mobileconnectors.s3.transferutility.UploadOptions;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amulyakhare.textdrawable.TextDrawable;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.AddPostResponse;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.marksheet.AddMarkSheetReq;
import school.campusconnect.datamodel.marksheet.MarkCardListResponse;
import school.campusconnect.datamodel.marksheet.StudentMarkCardListResponse;
import school.campusconnect.datamodel.marksheet.UploadMarkRequest;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneHelper;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.views.SMBDialogUtils;

import static school.campusconnect.activities.AddGalleryPostActivity.REQUEST_LOAD_CAMERA_IMAGE;
import static school.campusconnect.activities.AddGalleryPostActivity.REQUEST_LOAD_GALLERY_IMAGE;
import static school.campusconnect.activities.AddGalleryPostActivity.REQUEST_LOAD_PDF;

public class UpdateMarksheetActivity extends BaseActivity {

    private static final String TAG = "AttendanceActivity";
    @Bind(R.id.toolbar)
    Toolbar mToolBar;

    @Bind(R.id.rvAttendance)
    RecyclerView rvAttendance;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.spMarkCard)
    Spinner spMarkCard;

    private String groupId;
    private String teamId;
    private String mark_card_id;
    private LeafPreference leafPreference;
    private LeafManager leafManager;

    ArrayList<StudentMarkCardListResponse.SubjectDataTeam> list = new ArrayList<>();
    private MarkSheetStudentAdapter adapter;
    private ImageView img_image;
    private ProgressBar pbImgLoading;
    private ImageView imgDoc;
    private Uri imageCaptureFile;
    private String pdfPath;
    private String imgPath;
    private TransferUtility transferUtility;
    private int selectedPos = -1;
    List<MarkCardListResponse.MarkCardData> markCardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_marksheet);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_update_mark_card));

        init_();

        showLoadingBar(progressBar,false);
        //   progressBar.setVisibility(View.VISIBLE);
        LeafManager leafManager = new LeafManager();
        leafManager.getMarkCardList(this, groupId, teamId);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void init_() {
        transferUtility = AmazoneHelper.getTransferUtility(this);
        leafPreference = LeafPreference.getInstance(this);
        leafManager = new LeafManager();
        Bundle bundle = getIntent().getExtras();
        groupId = bundle.getString("group_id", "");
        teamId = bundle.getString("team_id", "");
        AppLog.e(TAG, ",groupId:" + groupId + ",teamId:" + teamId);

        rvAttendance.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MarkSheetStudentAdapter(list);
        rvAttendance.setAdapter(adapter);


        spMarkCard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (markCardList != null) {
                    mark_card_id = markCardList.get(i).getMarksCardId();
                    AppLog.e(TAG, "getStudents : ");
                    list.clear();
                    adapter.notifyDataSetChanged();
                    showLoadingBar(progressBar,false);
                    //   progressBar.setVisibility(View.VISIBLE);
                    leafManager.getMarkCardStudents(UpdateMarksheetActivity.this, GroupDashboardActivityNew.groupId, teamId, mark_card_id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        if (progressBar != null)
            hideLoadingBar();
          //  progressBar.setVisibility(View.GONE);
        switch (apiId) {
            case LeafManager.API_MARK_CARD_LIST: {
                MarkCardListResponse res = (MarkCardListResponse) response;
                markCardList = res.getData();
                AppLog.e(TAG, "ClassResponse " + markCardList);

                String[] stud = new String[markCardList.size()];
                for (int i = 0; i < markCardList.size(); i++) {
                    stud[i] = markCardList.get(i).getTitle();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stud);
                spMarkCard.setAdapter(adapter);

                if (markCardList == null || markCardList.size() == 0) {
                    Toast.makeText(this, getResources().getString(R.string.toast_create_mark_card), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case LeafManager.API_STUDENTS:
                StudentMarkCardListResponse res = (StudentMarkCardListResponse) response;
                AppLog.e(TAG, "StudentRes :" + res);
                list.clear();
                list.addAll(res.getData());
                adapter.notifyDataSetChanged();
                break;
            case LeafManager.API_MARK_SHEET:
                Toast.makeText(this, getResources().getString(R.string.toast_mark_card_uploaded), Toast.LENGTH_SHORT).show();
                AddPostResponse addPostResponse = (AddPostResponse) response;
                AppLog.e(TAG, "addPostResponse : " + addPostResponse);
                adapter.updateTrue(selectedPos);
                break;
        }
    }

    @Override
    public void onException(int apiId, String msg) {
        super.onException(apiId, msg);
        if (progressBar != null)
            hideLoadingBar();
        //  progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(int apiId, String msg) {
        if (progressBar != null)
            hideLoadingBar();
        //  progressBar.setVisibility(View.GONE);
        AppLog.e("onFailure", "Failure");
        if (msg.contains("401") || msg.contains("Unauthorized")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public class MarkSheetStudentAdapter extends RecyclerView.Adapter<MarkSheetStudentAdapter.ViewHolder> {

        private final ArrayList<StudentMarkCardListResponse.SubjectDataTeam> listAttendance;
        private Context mContext;

        public MarkSheetStudentAdapter(ArrayList<StudentMarkCardListResponse.SubjectDataTeam> listAttendance) {
            this.listAttendance = listAttendance;
        }

        @Override
        public MarkSheetStudentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_marksheet_student, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final StudentMarkCardListResponse.SubjectDataTeam item = listAttendance.get(position);
            holder.tvName.setText(item.name);
            holder.tvNumber.setText(getResources().getString(R.string.lbl_roll_No)+". " + (TextUtils.isEmpty(item.rollNumber) ? "" : item.rollNumber));

            if (!TextUtils.isEmpty(item.image)) {
                holder.imgLead_default.setVisibility(View.INVISIBLE);
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.image)).resize(50, 50).
                        into(holder.imgLead, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.imgLead_default.setVisibility(View.INVISIBLE);
                                AppLog.e("LeadAdapter", "Item Not Empty , On Success ");
                            }

                            @Override
                            public void onError() {
                                AppLog.e("LeadAdapter", "Item Not Empty , On Error");

                                holder.imgLead_default.setVisibility(View.VISIBLE);
                                TextDrawable drawable = TextDrawable.builder()
                                        .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position));
                                holder.imgLead_default.setImageDrawable(drawable);
                            }
                        });
            } else {
                AppLog.e("LeadAdapter", "Item Empty ");
                holder.imgLead_default.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position));
                holder.imgLead_default.setImageDrawable(drawable);
            }

            if (item.isMarksCardUploaded) {
                holder.btnUpload.setVisibility(View.GONE);
            } else {
                holder.btnUpload.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public int getItemCount() {
            return listAttendance.size();
        }

        public void updateTrue(int selectedPos) {
            if (selectedPos != -1) {
                listAttendance.get(selectedPos).isMarksCardUploaded = true;
                notifyItemChanged(selectedPos);
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.tvName)
            TextView tvName;

            @Bind(R.id.tvNumber)
            TextView tvNumber;

            @Bind(R.id.img_lead)
            ImageView imgLead;
            @Bind(R.id.img_lead_default)
            ImageView imgLead_default;
            @Bind(R.id.btnUpload)
            View btnUpload;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                btnUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUploadMarkSheetDialog(listAttendance.get(getAdapterPosition()), getAdapterPosition());
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoMarkSheetList(listAttendance.get(getAdapterPosition()));
                    }
                });
            }
        }
    }

    private void gotoMarkSheetList(StudentMarkCardListResponse.SubjectDataTeam studentData) {
//        if (studentData.isMarksCardUploaded) {
        Intent intent = new Intent(this, MarkSheetListActivity.class);
        intent.putExtra("group_id", groupId);
        intent.putExtra("team_id", teamId);
        intent.putExtra("mark_card_id", mark_card_id);
        intent.putExtra("user_id", studentData.studentId);
        intent.putExtra("name", studentData.getName());
        intent.putExtra("roll_no", studentData.getRollNumber());
        intent.putExtra("role", "teacher");
        startActivity(intent);
//        }
    }

    class SubjectMarksAdapter extends RecyclerView.Adapter<SubjectMarksAdapter.ViewHolder> {

        private final ArrayList<Map<String, Object>> subjectMarks;
        private final ArrayList<String> maxMarkList;
        private final ArrayList<String> minMarkList;

        public SubjectMarksAdapter(ArrayList<Map<String, Object>> subjectMarks, ArrayList<String> maxMarkList, ArrayList<String> minMarkList) {
            this.subjectMarks = subjectMarks;
            this.minMarkList = minMarkList;
            this.maxMarkList = maxMarkList;
        }

        @NonNull
        @Override
        public SubjectMarksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_add_subject_marks, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SubjectMarksAdapter.ViewHolder viewHolder, final int i) {
            final Map.Entry item = getItem(i);
            if (item != null) {
                viewHolder.tvSubject.setText(item.getKey().toString() + "");
                viewHolder.etMarks.setText(item.getValue().toString() + "");
                viewHolder.etMarksMax.setText(maxMarkList.get(i));
                viewHolder.etMinMarks.setText(minMarkList.get(i));

                viewHolder.etMarks.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(TextUtils.isEmpty(s.toString())){
                            return;
                        }
                        if (TextUtils.isDigitsOnly(s.toString())) {
                            item.setValue(Integer.parseInt(s.toString()));
                        } else {
                            item.setValue(s.toString());
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        }

        private Map.Entry getItem(int position) {
            Map<String, Object> item = subjectMarks.get(0);
            Iterator<Map.Entry<String, Object>> it = item.entrySet().iterator();
            int cnt = 0;
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (cnt == position) {
                    return pair;
                }
                cnt++;
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return subjectMarks.get(0).entrySet().size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.tvSubject)
            TextView tvSubject;
            @Bind(R.id.etMarks)
            EditText etMarks;
            @Bind(R.id.etMarksMax)
            EditText etMarksMax;
            @Bind(R.id.etMinMarks)
            EditText etMinMarks;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    private void showUploadMarkSheetDialog(final StudentMarkCardListResponse.SubjectDataTeam studentData, int adapterPosition) {
        imgPath = "";
        pdfPath = "";
        selectedPos = adapterPosition;
        final Dialog dialog = new Dialog(this, R.style.AppTheme_AlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_mark_sheet);

        final ArrayList<Map<String, Object>> subjectMarks = new ArrayList<>();

        HashMap<String, Object> item1 = new HashMap<>();
        ArrayList<String> maxMarkList = new ArrayList<>();
        ArrayList<String> minMarkList = new ArrayList<>();

        Iterator<Map.Entry<String, HashMap<String, String>>> it = studentData.subjects.get(0).entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, HashMap<String, String>> pair = it.next();

            item1.put(pair.getKey(), "");

            HashMap<String, String> pairMap = pair.getValue();

            maxMarkList.add(pairMap.get("max"));
            minMarkList.add(pairMap.get("min"));
        }
        subjectMarks.add(item1);
        SubjectMarksAdapter adapter = new SubjectMarksAdapter(subjectMarks, maxMarkList, minMarkList);
        RecyclerView rvMarks = dialog.findViewById(R.id.rvMarks);
        rvMarks.setAdapter(adapter);
        pbImgLoading = dialog.findViewById(R.id.pbImgLoading);
        imgDoc = dialog.findViewById(R.id.imgDoc);

        dialog.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.e(TAG, "subjectMarks : " + subjectMarks.toString());
                UploadMarkRequest uploadMarkRequest = new UploadMarkRequest();
                uploadMarkRequest.subjectMarks = subjectMarks;

                if (isAllMark(subjectMarks)) {
                    dialog.dismiss();
                    AppLog.e(TAG, "Request : " + uploadMarkRequest);
                    showLoadingBar(progressBar,false);
                  //  progressBar.setVisibility(View.VISIBLE);
                    leafManager.addMarksheet(UpdateMarksheetActivity.this, groupId, teamId, mark_card_id, studentData.getStudentId(), studentData.getRollNumber(), uploadMarkRequest);
                } else {
                    Toast.makeText(UpdateMarksheetActivity.this, getResources().getString(R.string.txt_please_provide_marks), Toast.LENGTH_SHORT).show();
                }

            }
        });
        dialog.show();
    }

    private boolean isAllMark(ArrayList<Map<String, Object>> subjectMarks) {
        Map<String, Object> item = subjectMarks.get(0);
        Iterator<Map.Entry<String, Object>> it = item.entrySet().iterator();
        int cnt = 0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (TextUtils.isEmpty(pair.getValue().toString().trim())) {
                return false;
            }
        }
        return true;
    }

    public void addPost(AddMarkSheetReq addMarkSheetReq) {
        hide_keyboard();
        if (isConnectionAvailable()) {
            if (progressBar != null)
                showLoadingBar(progressBar,false);
               // progressBar.setVisibility(View.VISIBLE);
            upLoadImageOnCloud(addMarkSheetReq);
        } else {
            showNoNetworkMsg();
        }

    }

    private void upLoadImageOnCloud(final AddMarkSheetReq addMarkSheetReq) {
        final String key = AmazoneHelper.getAmazonS3Key(addMarkSheetReq.fileType);
        TransferObserver observer ;
        UploadOptions option = UploadOptions.
                builder().bucket(AmazoneHelper.BUCKET_NAME).
                cannedAcl(CannedAccessControlList.PublicRead).build();
        try {
            observer = transferUtility.upload(key,
                    getContentResolver().openInputStream(Uri.parse(addMarkSheetReq.fileName.get(0))), option);
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    AppLog.e(TAG, "onStateChanged: " + id + ", " + state.name());
                    if (state.toString().equalsIgnoreCase("COMPLETED")) {
                        Log.e("MULTI_IMAGE", "onStateChanged " + 0);
                        updateList(addMarkSheetReq, key);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                    int percentDone = (int) percentDonef;

                    AppLog.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                            + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
                }

                @Override
                public void onError(int id, Exception ex) {
                    hideLoadingBar();
                //    progressBar.setVisibility(View.GONE);
                    AppLog.e(TAG, "Upload Error : " + ex);
                    Toast.makeText(UpdateMarksheetActivity.this, getResources().getString(R.string.image_upload_error), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void updateList(AddMarkSheetReq addMarkSheetReq, String key) {
        String _finalUrl = AmazoneHelper.BUCKET_NAME_URL + key;

        Log.e("FINALURL", "url is " + _finalUrl);

        _finalUrl = Constants.encodeStringToBase64(_finalUrl);

        Log.e("FINALURL", "encoded url is " + _finalUrl);

        addMarkSheetReq.fileName.set(0, _finalUrl);
        AppLog.e(TAG, "Send Data : " + addMarkSheetReq);

        // leafManager.addMarksheet(this,groupId,teamId,selectedData.getStudentId(),selectedData.getRollNumber(),addMarkSheetReq);
    }

    private boolean isValid(EditText etTitle) {
        if (!isValueValidOnly(etTitle)) {
            Toast.makeText(UpdateMarksheetActivity.this, getResources().getString(R.string.toast_please_enter_title), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(imgPath) && TextUtils.isEmpty(pdfPath)) {
            Toast.makeText(this, getResources().getString(R.string.toast_select_image_pdf), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
            case 23:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectPdf(REQUEST_LOAD_PDF);
                } else {
                    Log.e("AddPost" + "permission", "denied camera");
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

    public void showPhotoDialog(int resId) {
        SMBDialogUtils.showSMBSingleChoiceDialog(UpdateMarksheetActivity.this,
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

    private void removeImage() {
        imgPath = "";
        Picasso.with(this).load(R.drawable.icon_gallery).into(img_image);
    }

    private void removePdf() {
        pdfPath = "";
        Picasso.with(this).load(R.drawable.icon_doc).into(imgDoc);
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


    private void selectPdf(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, requestCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOAD_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            final Uri selectedImage = data.getData();
//            String path = ImageUtil.getPath(this, selectedImage);
            AppLog.e(TAG, "path : " + selectedImage);
            try {
//                File file = new File(path);
                Picasso.with(this).load(selectedImage).resize(80, 80).into(img_image);
            } catch (Exception e) {
                e.printStackTrace();
            }
            imgPath = selectedImage.toString();
            removePdf();

        } else if (requestCode == REQUEST_LOAD_CAMERA_IMAGE && resultCode == Activity.RESULT_OK) {
//            String path = cameraFile.getAbsolutePath();
            AppLog.e(TAG, "imageCaptureFile : " + imageCaptureFile);
            try {
//                File file = new File(path);
                Picasso.with(this).load(imageCaptureFile).resize(80, 80).into(img_image);
            } catch (Exception e) {
                e.printStackTrace();
            }
            imgPath = imageCaptureFile.toString();
            removePdf();
        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_LOAD_PDF) {
                Uri selectedImageURI = data.getData();
                Log.e("SelectedURI : ", selectedImageURI.toString());
               /* if (selectedImageURI.toString().startsWith("content")) {
                    pdfPath = ImageUtil.getPath(this, selectedImageURI);
                } else {
                    pdfPath = selectedImageURI.getPath();
                }*/
                pdfPath = selectedImageURI.toString();

                if (TextUtils.isEmpty(pdfPath)) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_select_pdf), Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.e("PDF", "imgUrl is " + pdfPath);

                if (!TextUtils.isEmpty(pdfPath))
                    Picasso.with(this).load(R.drawable.pdf_thumbnail).into(imgDoc);

                removeImage();
            }
        }
    }

    private class SendNotification extends AsyncTask<String, String, String> {
        private String server_response;

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
                urlConnection.setRequestProperty("Authorization", BuildConfig.API_KEY_FIREBASE1+BuildConfig.API_KEY_FIREBASE2);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                try {
                    JSONObject object = new JSONObject();

                    String title = getResources().getString(R.string.app_name);
                    String message = "Attendance submitted";
                    String topic = groupId + "_" + teamId;
                    object.put("to", "/topics/" + topic);

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title", title);
                    notificationObj.put("body", message);
                    object.put("notification", notificationObj);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("groupId", groupId);
                    dataObj.put("createdById", LeafPreference.getInstance(UpdateMarksheetActivity.this).getString(LeafPreference.LOGIN_ID));
                    dataObj.put("teamId", teamId);
                    dataObj.put("title", title);
                    dataObj.put("Notification_type", "attendance");
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
}
