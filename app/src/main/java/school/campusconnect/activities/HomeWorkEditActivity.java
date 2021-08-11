package school.campusconnect.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.otaliastudios.zoom.ZoomLayout;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.ViewType;
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder;
import ja.burhanrashid52.photoeditor.shape.ShapeType;
import school.campusconnect.Assymetric.Utils;
import school.campusconnect.R;
import school.campusconnect.datamodel.homework.AssignmentRes;
import school.campusconnect.datamodel.homework.ReassignReq;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneHelper;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

import static school.campusconnect.utils.Constants.FILE_TYPE_IMAGE;

public class HomeWorkEditActivity extends BaseActivity implements OnPhotoEditorListener {

    private static final String TAG = "HomeWorkEditActivity";
    @Bind(R.id.photoEditorView)
    PhotoEditorView ivImage;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;

    PhotoEditor mPhotoEditor;
    AssignmentRes.AssignmentData assignmentData;
    ZoomLayout zoomLayout;

    private TransferUtility transferUtility;
    private ProgressDialog progressDialog;

    int currentPage = 0;
    ArrayList<String> editedPaths ;

    String comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hw_edit);
        ButterKnife.bind(this);

        assignmentData = new Gson().fromJson(getIntent().getStringExtra("item"), AssignmentRes.AssignmentData.class);
        AppLog.e(TAG, "assignmentData : " + new Gson().toJson(assignmentData));


        findViewById(R.id.iconBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  currentPage++;
             //   if ((currentPage) >= assignmentData.fileName.size()) /// IF CURRENT PAGE WAS LAST THEN , SHOW POPUP AND COMPLETE API CALL.
                    notVerifyAssignment();
             /*   else {
                    setNextPage();  // RESET PHOTOEDITOR WITH NEXT PAGE IMAGE FROM ASSIGNMENT.
                }*/
            }
        });

        transferUtility = AmazoneHelper.getTransferUtility(this);

        zoomLayout = findViewById(R.id.zoomLayout);

        if (assignmentData.fileName != null && assignmentData.fileName.size() > 0) {
            Picasso.with(this).load(Constants.decodeUrlToBase64(assignmentData.fileName.get(0))).into(ivImage.getSource());
            currentPage = 0;
            editedPaths = new ArrayList<>();

            findViewById(R.id.iconPrev).setVisibility(View.GONE);


             if(currentPage+1 >= assignmentData.fileName.size())
             {
                    findViewById(R.id.iconNext).setVisibility(View.GONE);
                    findViewById(R.id.btnSubmit).setVisibility(View.VISIBLE);
             }
             else
             {
                 findViewById(R.id.iconNext).setVisibility(View.VISIBLE);
                 findViewById(R.id.btnSubmit).setVisibility(View.GONE);
             }

            for(String s : assignmentData.fileName)
            {
                editedPaths.add("xxx");
            }


        }


        findViewById(R.id.iconPrev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                setPrevPage();
            }
        });

        findViewById(R.id.iconNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                setNextPage();
            }
        });


        setZoom(false);

        setupPhotoEditor();

    }

    private void setNextPage() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        mPhotoEditor.saveAsFile(ImageUtil.getOutputMediaFile().getAbsolutePath(), new PhotoEditor.OnSaveListener() {
            @Override
            public void onSuccess(@NonNull String imagePath)
            {
                AppLog.e(TAG, "Image Saved Successfully at : " + imagePath);

                editedPaths.set(currentPage ,imagePath);

                currentPage++;

                if(currentPage >= assignmentData.fileName.size()-1)
                {
                    findViewById(R.id.iconNext).setVisibility(View.GONE);
                    findViewById(R.id.btnSubmit).setVisibility(View.VISIBLE);
                }

                findViewById(R.id.iconPrev).setVisibility(View.VISIBLE);

                if(!editedPaths.get(currentPage).equalsIgnoreCase("xxx"))
                    Picasso.with(HomeWorkEditActivity.this).load(new File(editedPaths.get(currentPage))).into(ivImage.getSource());
                else
                Picasso.with(HomeWorkEditActivity.this).load(Constants.decodeUrlToBase64(assignmentData.fileName.get(currentPage))).into(ivImage.getSource());

                setZoom(false);
                mPhotoEditor.clearAllViews();
                tabLayout.getTabAt(0).select();

             /*   if (currentPage == assignmentData.fileName.size() - 1)
                {
                    ((TextView) findViewById(R.id.btnSubmit)).setText("Done");
                }*/
            }

            @Override
            public void onFailure(@NonNull Exception exception)
            {
                AppLog.e(TAG, "onFailure called with exception : " + exception.getLocalizedMessage());
                Toast.makeText(HomeWorkEditActivity.this, "Failed to save Image. ", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void setPrevPage()
    {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        mPhotoEditor.saveAsFile(ImageUtil.getOutputMediaFile().getAbsolutePath(), new PhotoEditor.OnSaveListener() {
            @Override
            public void onSuccess(@NonNull String imagePath)
            {
                AppLog.e(TAG, "Image Saved Successfully at : " + imagePath);

                editedPaths.set(currentPage ,imagePath);

                currentPage--;

                if(currentPage == 0)
                findViewById(R.id.iconPrev).setVisibility(View.GONE);

                if(currentPage+1 >= assignmentData.fileName.size())
                {
                    findViewById(R.id.iconNext).setVisibility(View.GONE);
                    findViewById(R.id.btnSubmit).setVisibility(View.VISIBLE);
                }
                else
                {
                    findViewById(R.id.iconNext).setVisibility(View.VISIBLE);
                    findViewById(R.id.btnSubmit).setVisibility(View.GONE);
                }

                AppLog.e(TAG , "editepath to be load : "+editedPaths.get(currentPage)+" at "+currentPage);
                mPhotoEditor.clearAllViews();

                Picasso.with(HomeWorkEditActivity.this).load(new File(editedPaths.get(currentPage))).into(ivImage.getSource());
                setZoom(false);
                tabLayout.getTabAt(0).select();

             /*   if (currentPage == assignmentData.fileName.size() - 1)
                {
                    ((TextView) findViewById(R.id.btnSubmit)).setText("Done");
                }*/
            }

            @Override
            public void onFailure(@NonNull Exception exception)
            {
                AppLog.e(TAG, "onFailure called with exception : " + exception.getLocalizedMessage());
                Toast.makeText(HomeWorkEditActivity.this, "Failed to save Image. ", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupPhotoEditor() {

        //Use custom font using latest support library
        Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium);

//loading font from asset
        Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "fonts/emojione-android.ttf");

        mPhotoEditor = new PhotoEditor.Builder(this, ivImage)
                .setPinchTextScalable(true)
                .setClipSourceImage(true)
                .setDefaultTextTypeface(mTextRobotoTf)
                .setDefaultEmojiTypeface(mEmojiTypeFace)
                .build();
        mPhotoEditor.setOnPhotoEditorListener(this);


        findViewById(R.id.imgUndo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoEditor.undo();
            }
        });
        findViewById(R.id.imgRedo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoEditor.redo();
            }
        });

        /*// default Brush
        ShapeBuilder mShapeBuilder = new ShapeBuilder()
                .withShapeOpacity(100)
                .withShapeType(ShapeType.RECTANGLE)
                .withShapeSize(10);
        mPhotoEditor.setBrushDrawingMode(true);
        mPhotoEditor.setShape(mShapeBuilder);
        mPhotoEditor.setBrushColor(getResources().getColor(R.color.colorBrushBox));*/

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                AppLog.e(TAG, "onTabSelected() :" + tab.getPosition());
                //zoomLayout.setEnabled(false);

                selectTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                AppLog.e(TAG, "onTabUnselected() :" + tab.getPosition());

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                AppLog.e(TAG, "onTabReselected() :" + tab.getPosition());

                selectTab(tab.getPosition());
            }
        });

        tabLayout.getTabAt(4).select();
    }

    private void setZoom(boolean value) {
        zoomLayout.setZoomEnabled(value);
        zoomLayout.setHorizontalPanEnabled(value);
        zoomLayout.setVerticalPanEnabled(value);
    }

    private void selectTab(int position) {
        switch (position) {
            case 0: {

                setZoom(false);

                ShapeBuilder mShapeBuilder = new ShapeBuilder()
                        .withShapeOpacity(100)
                        .withShapeType(ShapeType.BRUSH)
                        .withShapeSize(10);
                mPhotoEditor.setShape(mShapeBuilder);
                mPhotoEditor.setBrushDrawingMode(true);

                mPhotoEditor.setBrushColor(getResources().getColor(R.color.colorBrushBox));
                break;
            }
            case 1: {

                setZoom(false);
                ShapeBuilder mShapeBuilder = new ShapeBuilder()
                        .withShapeOpacity(100)
                        .withShapeType(ShapeType.RECTANGLE)
                        .withShapeSize(10);
                mPhotoEditor.setBrushDrawingMode(true);
                mPhotoEditor.setShape(mShapeBuilder);
                mPhotoEditor.setBrushColor(getResources().getColor(R.color.colorBrushBox));
                break;
            }
            case 2: {
                setZoom(false);
                showAddTextDialog();
                break;
            }
            case 3: {
                setZoom(false);
                AppLog.e(TAG, "Eraser Size : " + mPhotoEditor.getBrushSize());


                mPhotoEditor.setBrushSize(getResources().getDimensionPixelSize(R.dimen.erasersize));
                mPhotoEditor.brushEraser();
                AppLog.e(TAG, "Eraser Size After Setting : " + mPhotoEditor.getEraserSize());

                break;
            }

            case 4: {
                setZoom(true);
                mPhotoEditor.setBrushSize(0);
                mPhotoEditor.setBrushColor(getResources().getColor(R.color.colorTransparent));
            }

        }
    }

    @Override
    public void onEditTextChangeListener(View rootView, String text, int colorCode) {

    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {

    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {

    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {

    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {

    }

    @Override
    public void onTouchSourceImage(MotionEvent event) {

    }

    private void showAddTextDialog() {
        final Dialog dialog = new Dialog(this, R.style.AppTheme_AlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_text);
        final EditText etTitle = dialog.findViewById(R.id.etTitle);
        RadioButton rbt1 = dialog.findViewById(R.id.rbt1);
        RadioButton rbt2 = dialog.findViewById(R.id.rbt2);
        RadioButton rbt3 = dialog.findViewById(R.id.rbt3);
        RadioButton rbt4 = dialog.findViewById(R.id.rbt4);

        ImageView ivTrophy = dialog.findViewById(R.id.iv_trophy);
        ImageView ivCrown = dialog.findViewById(R.id.iv_crown);

        rbt1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    etTitle.setVisibility(View.GONE);
            }
        });
        rbt2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    etTitle.setVisibility(View.GONE);
            }
        });
        rbt3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    etTitle.setVisibility(View.GONE);
            }
        });
        rbt4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    etTitle.setVisibility(View.VISIBLE);
            }
        });

        dialog.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbt4.isChecked()) {
                    if (!TextUtils.isEmpty(etTitle.getText().toString().trim())) {
                        mPhotoEditor.addText(etTitle.getText().toString(), getResources().getColor(R.color.pink));
                        dialog.dismiss();
                    } else {
                        Toast.makeText(HomeWorkEditActivity.this, "Please Add Text", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (rbt1.isChecked()) {
                        mPhotoEditor.addText("Write Properly", getResources().getColor(R.color.pink));
                    } else if (rbt2.isChecked()) {
                        mPhotoEditor.addText("Spelling Mistakes", getResources().getColor(R.color.pink));
                    } else if (rbt3.isChecked()) {
                        mPhotoEditor.addText("Improve Handwriting", getResources().getColor(R.color.pink));
                    }
                    dialog.dismiss();
                }
            }
        });

        ivTrophy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoEditor.addImage(getBitmapFromVectorDrawable(HomeWorkEditActivity.this, R.drawable.icon_trophy));

                dialog.dismiss();
            }
        });

        ivCrown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoEditor.addImage(getBitmapFromVectorDrawable(HomeWorkEditActivity.this, R.drawable.icon_crown));
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void notVerifyAssignment() {
        final Dialog dialog = new Dialog(this, R.style.AppTheme_AlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_comment);
        final EditText etTitle = dialog.findViewById(R.id.etTitle);
        final TextView tvComment = dialog.findViewById(R.id.tvComment);
        final CheckBox chkVerify = dialog.findViewById(R.id.chkVerify);
        final CheckBox chkReAssign = dialog.findViewById(R.id.chkReAssign);
        chkVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkVerify.setChecked(true);
                chkReAssign.setChecked(false);
                tvComment.setVisibility(View.GONE);
            }
        });
        chkReAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkVerify.setChecked(false);
                chkReAssign.setChecked(true);
                tvComment.setVisibility(View.VISIBLE);
            }
        });

        chkReAssign.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tvComment.setVisibility(View.GONE);
                } else {
                    tvComment.setVisibility(View.VISIBLE);
                }
            }
        });
        dialog.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkVerify.isChecked()) {

                    if (ActivityCompat.checkSelfPermission(HomeWorkEditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mPhotoEditor.saveAsFile(ImageUtil.getOutputMediaFile().getAbsolutePath(), new PhotoEditor.OnSaveListener() {
                        @Override
                        public void onSuccess(@NonNull String imagePath)
                        {
                            AppLog.e(TAG , "Image Saved Successfully at : "+imagePath);

                            progressDialog = new ProgressDialog(HomeWorkEditActivity.this);
                            progressDialog.setMessage("Uploading Image...");
                            progressDialog.show();

                            editedPaths.set(currentPage , imagePath);

                           // uploadImageOnCloud(imagePath,etTitle.getText().toString().trim());
                            uploadThumbnail(editedPaths,0);
                        }

                        @Override
                        public void onFailure(@NonNull Exception exception)
                        {
                            AppLog.e(TAG , "onFailure called with exception : "+exception.getLocalizedMessage());
                            Toast.makeText(HomeWorkEditActivity.this , "Failed to save Image. " , Toast.LENGTH_SHORT).show();
                        }
                    });

                    dialog.dismiss();

                } else
                    {
                        dialog.dismiss();
                        Intent intent = new Intent();
                        intent.putExtra("isVerify", false);
                        intent.putExtra("comments", etTitle.getText().toString().trim());
                        setResult(RESULT_OK, intent);
                        finish();
                }
            }
        });
        dialog.show();
    }


    private void uploadThumbnail(ArrayList<String> listThumbnails, int index) {
        if (index == listThumbnails.size())
        {

            Intent intent = new Intent();
            intent.putExtra("isVerify", true);
            intent.putExtra("comments", comments);
            intent.putExtra("_finalUrl", editedPaths);
            setResult(RESULT_OK, intent);
            finish();
        }
        else {
            final String key = AmazoneHelper.getAmazonS3KeyThumbnail(FILE_TYPE_IMAGE);
            AppLog.e(TAG , "uploadImageOnCloud called with key : "+key);
            File file = new File(listThumbnails.get(index));
            TransferObserver observer = transferUtility.upload(AmazoneHelper.BUCKET_NAME, key,
                    file, CannedAccessControlList.PublicRead);

            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    AppLog.e(TAG, "onStateChanged: " + id + ", " + state.name());
                    if (state.toString().equalsIgnoreCase("COMPLETED"))
                    {
                        progressDialog.dismiss();
                        String _finalUrl = AmazoneHelper.BUCKET_NAME_URL + key;

                        Log.e("FINALURL", "url is " + _finalUrl);

                        _finalUrl = Constants.encodeStringToBase64(_finalUrl);

                        Log.e("FINALURL", "encoded url is " + _finalUrl);

                        editedPaths.set(index,_finalUrl);

                        uploadThumbnail(editedPaths , index+1);
                    }
                    if (TransferState.FAILED.equals(state)) {
                        Toast.makeText(HomeWorkEditActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                    int percentDone = (int) percentDonef;

                    progressDialog.setMessage("Uploading Image " + percentDone + "% , please wait...");

                    AppLog.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                            + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
                }

                @Override
                public void onError(int id, Exception ex) {
                    progressDialog.dismiss();
                    AppLog.e(TAG, "Upload Error : " + ex);
                    Toast.makeText(HomeWorkEditActivity.this, getResources().getString(R.string.image_upload_error), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    private void uploadImageOnCloud(String imagePath)
    {
        final String key = AmazoneHelper.getAmazonS3KeyThumbnail(FILE_TYPE_IMAGE);
        AppLog.e(TAG , "uploadImageOnCloud called with key : "+key);
        File file = new File(imagePath);
        TransferObserver observer = transferUtility.upload(AmazoneHelper.BUCKET_NAME, key,
                file, CannedAccessControlList.PublicRead);

        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                AppLog.e(TAG, "onStateChanged: " + id + ", " + state.name());
                if (state.toString().equalsIgnoreCase("COMPLETED")) {
                    progressDialog.dismiss();

                }
                if (TransferState.FAILED.equals(state)) {
                    Toast.makeText(HomeWorkEditActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                progressDialog.setMessage("Uploading Image " + percentDone + "% , please wait...");

                AppLog.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                        + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                progressDialog.dismiss();
                AppLog.e(TAG, "Upload Error : " + ex);
                Toast.makeText(HomeWorkEditActivity.this, getResources().getString(R.string.image_upload_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
