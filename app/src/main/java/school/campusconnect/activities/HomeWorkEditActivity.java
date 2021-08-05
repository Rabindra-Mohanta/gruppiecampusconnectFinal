package school.campusconnect.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
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

import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.tabs.TabLayout;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.ViewType;
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder;
import ja.burhanrashid52.photoeditor.shape.ShapeType;
import school.campusconnect.R;
import school.campusconnect.datamodel.homework.AssignmentRes;
import school.campusconnect.datamodel.homework.ReassignReq;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;

public class HomeWorkEditActivity extends BaseActivity implements OnPhotoEditorListener {

    private static final String TAG = "HomeWorkEditActivity";
    @Bind(R.id.photoEditorView)
    PhotoEditorView ivImage;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;

    PhotoEditor mPhotoEditor;
    AssignmentRes.AssignmentData assignmentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hw_edit);
        ButterKnife.bind(this);

        assignmentData = new Gson().fromJson(getIntent().getStringExtra("item"), AssignmentRes.AssignmentData.class);
        AppLog.e(TAG, "assignmentData : " + assignmentData);
        if (assignmentData.fileName != null && assignmentData.fileName.size() > 0) {
            Picasso.with(this).load(Constants.decodeUrlToBase64(assignmentData.fileName.get(0))).into(ivImage.getSource());
        }

        findViewById(R.id.iconBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notVerifyAssignment();
            }
        });


        setupPhotoEditor();

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

        // default Brush
        ShapeBuilder mShapeBuilder = new ShapeBuilder()
                .withShapeOpacity(100)
                .withShapeType(ShapeType.RECTANGLE)
                .withShapeSize(10);
        mPhotoEditor.setBrushDrawingMode(true);
        mPhotoEditor.setShape(mShapeBuilder);
        mPhotoEditor.setBrushColor(getResources().getColor(R.color.colorPrimary));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                AppLog.e(TAG, "onTabSelected() :" + tab.getPosition());
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

    }

    private void selectTab(int position) {
        switch (position) {
            case 0: {
                ShapeBuilder mShapeBuilder = new ShapeBuilder()
                        .withShapeOpacity(100)
                        .withShapeType(ShapeType.BRUSH)
                        .withShapeSize(10);
                mPhotoEditor.setShape(mShapeBuilder);
                mPhotoEditor.setBrushDrawingMode(true);
                mPhotoEditor.setBrushColor(getResources().getColor(R.color.colorPrimary));
                break;
            }
            case 1: {
                ShapeBuilder mShapeBuilder = new ShapeBuilder()
                        .withShapeOpacity(100)
                        .withShapeType(ShapeType.RECTANGLE)
                        .withShapeSize(10);
                mPhotoEditor.setBrushDrawingMode(true);
                mPhotoEditor.setShape(mShapeBuilder);
                mPhotoEditor.setBrushColor(getResources().getColor(R.color.colorPrimary));
                break;
            }
            case 2: {
                showAddTextDialog();
                break;
            }
            case 3: {
                mPhotoEditor.brushEraser();
                mPhotoEditor.setBrushEraserSize(100);
                break;
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
                        mPhotoEditor.addText(etTitle.getText().toString(), getResources().getColor(R.color.colorPrimary));
                        dialog.dismiss();
                    } else {
                        Toast.makeText(HomeWorkEditActivity.this, "Please Add Text", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (rbt1.isChecked()) {
                        mPhotoEditor.addText("Write Properly", getResources().getColor(R.color.colorPrimary));
                    } else if (rbt2.isChecked()) {
                        mPhotoEditor.addText("Spelling Mistakes", getResources().getColor(R.color.colorPrimary));
                    } else if (rbt3.isChecked()) {
                        mPhotoEditor.addText("Improve Handwriting", getResources().getColor(R.color.colorPrimary));
                    }
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
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
                etTitle.setVisibility(View.GONE);
                tvComment.setVisibility(View.GONE);
            }
        });
        chkReAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkVerify.setChecked(false);
                chkReAssign.setChecked(true);
                etTitle.setVisibility(View.VISIBLE);
                tvComment.setVisibility(View.VISIBLE);
            }
        });

        chkReAssign.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etTitle.setVisibility(View.GONE);
                    tvComment.setVisibility(View.GONE);
                } else {
                    etTitle.setVisibility(View.VISIBLE);
                    tvComment.setVisibility(View.VISIBLE);
                }
            }
        });
        dialog.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkVerify.isChecked()) {
                    dialog.dismiss();
                    Intent intent = new Intent();
                    intent.putExtra("isVerify", true);
                    intent.putExtra("comments", "");
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    if (!TextUtils.isEmpty(etTitle.getText().toString().trim())) {
                        dialog.dismiss();
                        Intent intent = new Intent();
                        intent.putExtra("isVerify", false);
                        intent.putExtra("comments", etTitle.getText().toString().trim());
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(HomeWorkEditActivity.this, "Please Add Comment", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        dialog.show();
    }

}
