package school.campusconnect.utils.crop;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;

import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.databinding.ActivityCropDialogBinding;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class CropDialogActivity extends AppCompatActivity {

    ActivityCropDialogBinding binding;
    Uri uri;
    boolean isCamera;

    public Uri cropImageFile;
    public File mediaFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_crop_dialog);

        Log.e("CropDialogActivity",getIntent().getStringExtra("path"));

        uri =  Uri.parse(getIntent().getStringExtra("path"));
        isCamera = getIntent().getBooleanExtra("isCamera",true);

        if (!isCamera)
        {
            mediaFile = ImageUtil.getOutputMediaFile();
            cropImageFile = Uri.fromFile(mediaFile);

        }



        Glide.with(this).load(uri).into(binding.img);

        binding.btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isCamera)
                {
                    CropImage.activity(uri)
                            .setOutputUri(cropImageFile)
                            .start(CropDialogActivity.this);
                }
                else
                {
                    CropImage.activity(uri)
                            .setOutputUri(uri)
                            .start(CropDialogActivity.this);
                }

            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                setResult(RESULT_CANCELED,i);
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                Log.e("CropDialog","resultUri "+resultUri);

                Intent i = new Intent();
                i.putExtra("Data",String.valueOf(resultUri));
                setResult(RESULT_OK,i);
                finish();


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e("CropDialog","error"+error);
            }
        }
    }

}