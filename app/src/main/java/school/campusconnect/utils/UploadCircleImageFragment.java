package school.campusconnect.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.activities.AddClassStudentActivity;
import school.campusconnect.activities.ProfileActivity2;
import school.campusconnect.activities.ProfileConstituencyActivity;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.ProfileValidationError;
import school.campusconnect.datamodel.profile.ProfileItem;
import school.campusconnect.datamodel.profile.ProfileItemUpdate;
import school.campusconnect.datamodel.student.StudentRes;
import school.campusconnect.fragments.ProfileFragmentConst;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.crop.Crop;

public class UploadCircleImageFragment extends BaseUploadImageFragment implements View.OnClickListener, LeafManager.OnCommunicationListener, LeafManager.OnAddUpdateListener<ProfileValidationError> {
    public static final int REQUEST_LOAD_CAMERA_IMAGE = 101;
    public static final int REQUEST_LOAD_GALLERY_IMAGE = 102;
    CircleImageView imgService;
    CircleImageView imgPlus;
    // RelativeLayout imgPlusLayout;
    public boolean isImageChanged = false;
    public boolean isForUpload = false;
    public boolean isGroupOrProfile = false;
    ImageView imgDefault;
    private static String TAG = "UploadCircleImageFragment";

    public boolean isEditEnabled() {
        return isEditEnabled;
    }

    public void setEditEnabled(boolean editEnabled) {
        isEditEnabled = editEnabled;
    }

    boolean isEditEnabled = true;

    public UploadCircleImageFragment() {

    }

    @Override
    public void setImageString(BitmapDrawable drawable) {
        mProfileImage = ImageUtil.encodeTobase64(drawable.getBitmap());
        //imgPlusLayout.setVisibility(View.GONE);
        imgPlus.setVisibility(View.GONE);
        imgDefault.setVisibility(View.GONE);
    }

    public void setImageFromString(String base64Image) {
        Bitmap bitmap = ImageUtil.decodeFromBase64(base64Image);
        if (bitmap != null) {
            mProfileImage = base64Image;
            //imgPlusLayout.setVisibility(View.GONE);
            imgPlus.setVisibility(View.GONE);
            imgDefault.setVisibility(View.GONE);
            imgService.setImageBitmap(bitmap);
        }
    }

    public void updateDefaultPhoto(boolean update) {
        if (update)
            if (getActivity() instanceof ProfileActivity2)
                ((ProfileActivity2) getActivity()).callUpdateApi();
            else if (getActivity() instanceof ProfileConstituencyActivity)
                ((ProfileConstituencyActivity) getActivity()).callUpdateApi();

        getDefaultImageView().setVisibility(View.VISIBLE);

        if (!isGroupOrProfile)
            getDefaultImageView().setImageResource(R.drawable.icon_default_propic);
        else
            getDefaultImageView().setImageResource(R.drawable.icon_default_propic);

        Log.e(TAG, "Update Default Photo Called Visibility : " + getDefaultImageView().getVisibility());
    }

    public void setInitialLatterImage(String name) {


       /* getDefaultImageView().setVisibility(View.VISIBLE);
        TextDrawable drawable = TextDrawable.builder()
                .buildRect(ImageUtil.getTextLetter(name), ImageUtil.getRandomColor(1));
        getDefaultImageView().setImageDrawable(drawable);*/


        TextDrawable drawable = TextDrawable.builder()
                .buildRound(ImageUtil.getTextLetter(name), ImageUtil.getRandomColor(1));
        imgDefault.setImageDrawable(drawable);
        imgDefault.setVisibility(View.VISIBLE);


        // imgPlus.setImageResource(R.drawable.img);

    }

    public ImageView getDefaultImageView() {
        return imgPlus;
    }

    @Override
    public ImageView getImageView() {
        return imgService;
    }

    public static UploadCircleImageFragment newInstance(String photoUrl, Boolean isForUpload, Boolean isGroupOrProfile) {
        UploadCircleImageFragment fragment = new UploadCircleImageFragment();
        Bundle b = new Bundle();
        b.putBoolean(ISFORUPLOAD, isForUpload);
        b.putString(EXTRA_PHOTO_URL, photoUrl);
        b.putBoolean(ISGROUP, isGroupOrProfile);
        fragment.setArguments(b);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_upload_circle_image, container, false);
        AppLog.e(TAG, "onCreateView");
        imgService = (CircleImageView) rootView.findViewById(R.id.img_service);
        //imgPlusLayout = (RelativeLayout) rootView.findViewById(R.id.upload_img);
        imgPlus = (CircleImageView) rootView.findViewById(R.id.img_plus);
        imgDefault = (ImageView) rootView.findViewById(R.id.imgDefault);
        imgService.setOnClickListener(this);
        imgPlus.setOnClickListener(this);
        //imgPlusLayout.setOnClickListener(this);
        SELECTED_IMAGE_TYPE = COVER_IMAGE;
        String url = getArguments().getString(EXTRA_PHOTO_URL, "");

        _finalUrl = url;

        isForUpload = getArguments().getBoolean(ISFORUPLOAD);
        isGroupOrProfile = getArguments().getBoolean(ISGROUP);
        if (url != null && !url.isEmpty()) {
            if (Constants.decodeUrlToBase64(url).contains("http")) {
                updatePhotoFromUrl(url);
            } else {
                setImageFromString(url);
            }
            imgPlus.setVisibility(View.GONE);
            imgDefault.setVisibility(View.GONE);
            //imgPlusLayout.setVisibility(View.GONE);
        } else {
            //
            updateDefaultPhoto(false);
            imgPlus.setVisibility(View.VISIBLE);
            String name = LeafPreference.getInstance(getActivity()).getString(LeafPreference.NAME);
            setInitialLatterImage(name);

        }
        //imgPlus.setVisibility(View.GONE);
        //imgPlusLayout.setVisibility(View.GONE);
        return rootView;

    }


    public void onClick(View v) {


        if (!isEditEnabled) {
            return;
        }

        switch (v.getId()) {

            case R.id.img_service:
                final Dialog settingsDialog = new Dialog(getActivity());
                LayoutInflater inflater = ((Activity) getActivity()).getLayoutInflater();
                View newView = (View) inflater.inflate(R.layout.img_layout_profile, null);
                settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(newView);

                settingsDialog.show();
                ImageView iv = (ImageView) newView.findViewById(R.id.img_popup);
                ImageView ivEdit = (ImageView) newView.findViewById(R.id.ivEdit);

                ivEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (checkPermissionForWriteExternal()) {
                            showPhotoDialog(R.array.array_image_modify);
                            settingsDialog.dismiss();
                        } else {
                            requestPermissionForWriteExternal(21);
                        }
                    }
                });

                if (ProfileActivity2.profileImage != null && !ProfileActivity2.profileImage.isEmpty()) {
                    Picasso.with(getActivity()).load(Constants.decodeUrlToBase64(ProfileActivity2.profileImage)).into(iv);
                }
                if (ProfileFragmentConst.profileImage != null && !ProfileFragmentConst.profileImage.isEmpty()) {
                    Picasso.with(getActivity()).load(Constants.decodeUrlToBase64(ProfileFragmentConst.profileImage)).into(iv);
                } else {
                    Picasso.with(getActivity()).load(R.drawable.icon_default_user).into(iv);

                }
                settingsDialog.show();
                /*if (checkPermissionForWriteExternal()) {
                    showPhotoDialog(R.array.array_image_modify);
                } else {
                    requestPermissionForWriteExternal(21);
                }*/

                break;
            /*case R.id.upload_img:
                showPhotoDialog(R.array.array_image);
                break;*/
            case R.id.img_plus:
                if (checkPermissionForWriteExternal()) {
                    showPhotoDialog(R.array.array_image);
                } else {
                    requestPermissionForWriteExternal(22);
                }
                break;

        }

    }

    @Override
    public void beginCrop(Uri source, int code) {
        File outputFile;
//        File outputDir = getActivity().getCacheDir();
        File outputDir = new File(Environment.getExternalStorageDirectory() + File.separator);
        try {
            // Create a media file folderName
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());
            outputFile = File.createTempFile("IMG_" + timeStamp, ".jpg", outputDir);
            Uri destination = Uri.fromFile(outputFile);
            Crop.of(source, destination).withAspect(10, 10)
                    .start(getActivity(), code);
            Log.e("beCrop", "try completed");
        } catch (IOException e) {
            e.printStackTrace();
            outputFile = ImageUtil.getOutputMediaFile();
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                Uri destination = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".fileprovider", outputFile);
                Crop.of(source, destination).withAspect(10, 10).start(getActivity(), code);
            } else {
                Uri destination = Uri.fromFile(outputFile);
                Crop.of(source, destination).withAspect(10, 10).start(getActivity(), code);
            }
            Log.e("beCrop", "catch error is " + e.toString());
        }


    }

    @Override
    public void removeImage() {
        imgService.setImageBitmap(null);
        isImageChanged = true;
        mProfileImage = "";
        //imgPlusLayout.setVisibility(View.VISIBLE);
        imgPlus.setVisibility(View.VISIBLE);
        imgDefault.setVisibility(View.VISIBLE);
        _finalUrl = "";
        updateDefaultPhoto(true);
    }

    @Override
    public int getRequestCode(int id) {
        if (id == 0)
            return REQUEST_LOAD_CAMERA_IMAGE;
        else return REQUEST_LOAD_GALLERY_IMAGE;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        AppLog.e(TAG, "onActivityResult");
        if (requestCode == REQUEST_LOAD_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            boolean excludeCrop = getArguments().getBoolean("exclude_crop", false);
            if (excludeCrop) {
                Uri uri = onImageSelected(data);
                ImageCompressionAsyncTask imageCompressionAsyncTask = new ImageCompressionAsyncTask(getActivity(), 656, 230, false);
                imageCompressionAsyncTask.setOnImageCompressed(new ImageCompressionAsyncTask.OnImageCompressed() {
                    @Override
                    public void onCompressedImage(ProfileImage profileImage) {
                        if (profileImage.imageString.isEmpty()) {
                            Toast.makeText(getActivity(), getResources().getString(R.string.toast_not_able_to_compress), Toast.LENGTH_SHORT).show();
                        } else {
                            setImageToView(profileImage);
                        }

                    }
                });
                imageCompressionAsyncTask.execute(uri.toString());
            } else {
                onImageSelected(data, 109);
            }

        } else if (requestCode == REQUEST_LOAD_CAMERA_IMAGE && resultCode == Activity.RESULT_OK) {
            if (imageCaptureFile != null) {
                galleryAddPic(imageCaptureFile.getPath());
                boolean excludeCrop = getArguments().getBoolean("exclude_crop", false);
                if (excludeCrop) {

                    ImageCompressionAsyncTask imageCompressionAsyncTask = new ImageCompressionAsyncTask(getActivity(), 656, 230, !isForUpload);
                    imageCompressionAsyncTask.setOnImageCompressed(new ImageCompressionAsyncTask.OnImageCompressed() {
                        @Override
                        public void onCompressedImage(ProfileImage profileImage) {
                            if (profileImage.imageString.isEmpty()) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.toast_not_able_to_compress), Toast.LENGTH_SHORT).show();
                            } else {
                                setImageToView(profileImage);
                            }
                        }
                    });
                    imageCompressionAsyncTask.execute(imageCaptureFile.toString());
                } else {
                    beginCrop(imageCaptureFile, 109);
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.msg_unable_get_camera_image), Toast.LENGTH_SHORT).show();
            }


        } else if (requestCode == 109) {
            Log.e("CROP_TRACK", "req 109");
            handleCrop(resultCode, data);
        }
    }

    @Override
    public void setImageToView(ProfileImage profileImage) {
        Log.e(TAG, "from profile setImageToView method 1 " + profileImage.imageString);
        //imgPlusLayout.setVisibility(View.GONE);
        imgPlus.setVisibility(View.GONE);
        imgDefault.setVisibility(View.GONE);
        mProfileImage = profileImage.imageString;
        profileImage.imageString = mProfileImage;
        // imgService.setImageBitmap(profileImage.image);
        if (Constants.decodeUrlToBase64(profileImage.imageString).contains("http")) {
            updatePhotoFromUrl(profileImage.imageString);
        } else {
            setImageFromString(profileImage.imageString);
        }

        isImageChanged = true;

        if(getActivity() instanceof AddClassStudentActivity)
        {
            StudentRes.StudentData  studentData =new StudentRes.StudentData();
            studentData=AddClassStudentActivity.studentData;
            studentData.image=profileImage.imageString;


            AddClassStudentActivity.leafManager.editClassStudent(this,AddClassStudentActivity.group_id,AddClassStudentActivity.team_id,AddClassStudentActivity.userId,AddClassStudentActivity.gruppieRollNoNumber,studentData);
        }



        if(getActivity() instanceof ProfileConstituencyActivity)
        {


            ProfileItemUpdate profileItemUpdate = new ProfileItemUpdate();
            ProfileItem Item=ProfileFragmentConst.item;
            profileItemUpdate.name=Item.name;
            profileItemUpdate.dob=Item.dob;
            profileItemUpdate.email=Item.email;
            profileItemUpdate.address=Item.occupation;
            profileItemUpdate.qualification=Item.qualification;
            profileItemUpdate.caste=Item.caste;
            profileItemUpdate.subcaste=Item.subcaste;
            profileItemUpdate.religion=Item.religion;
            profileItemUpdate.gender=Item.gender;
            profileItemUpdate.bloodGroup=Item.bloodGroup;
            profileItemUpdate.voterId=Item.voterId;
            profileItemUpdate.designation=Item.designation;
            profileItemUpdate.image=profileImage.imageString;

            ProfileFragmentConst.leafManager.updateProfileDetails(this,profileItemUpdate);



        }



        if (getActivity() instanceof ProfileActivity2)
            ((ProfileActivity2) getActivity()).callUpdateApi();
        else if (getActivity() instanceof ProfileConstituencyActivity)
            ((ProfileConstituencyActivity) getActivity()).callUpdateApi();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 21:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showPhotoDialog(R.array.array_image_modify);
                    Log.e("AddPost" + "permission", "granted camera");
                } else {
                    Log.e("AddPost" + "permission", "denied camera");
                }
                break;

            case 22:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showPhotoDialog(R.array.array_image);
                    Log.e("AddPost" + "permission", "granted camera");
                } else {
                    Log.e("AddPost" + "permission", "denied camera");
                }
                break;
        }
    }

    private boolean checkPermissionForWriteExternal() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            Log.e("External" + "permission", "checkpermission , granted");
            return true;
        } else {
            Log.e("External" + "permission", "checkpermission , denied");
            return false;
        }
    }

    public void requestPermissionForWriteExternal(int code) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_storage_permission_needed), Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        Toast.makeText(getActivity(),"Profile pic Changed",Toast.LENGTH_LONG).show();

    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<ProfileValidationError> error) {
        Toast.makeText(getActivity(),"something went wrong try again",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(int apiId, String msg) {
        Toast.makeText(getActivity(),"something went wrong try again",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onException(int apiId, String msg) {
        Toast.makeText(getActivity(),"something went wrong try again",Toast.LENGTH_LONG).show();
    }
}