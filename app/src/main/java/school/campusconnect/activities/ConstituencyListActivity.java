package school.campusconnect.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ConstituencyRes;
import school.campusconnect.datamodel.GroupDetailResponse;
import school.campusconnect.datamodel.GroupItem;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.views.SMBDialogUtils;

public class ConstituencyListActivity extends BaseActivity implements LeafManager.OnCommunicationListener {

    private static final String TAG = "TalukListActivity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    private AlertDialog editDialog;

    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;


    SharedPreferences wallPref;
    private MenuItem removeWallMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taluk_list);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setTitle(getResources().getString(R.string.app_name));

        _init();

        setBackgroundImage();

        getConstituencyList();
    }
    private void setBackgroundImage() {
        if (wallPref.contains(Constants.BACKGROUND_IMAGE)) {
            String path = wallPref.getString(Constants.BACKGROUND_IMAGE, "");
            if (TextUtils.isEmpty(path))
                return;

            AppLog.e(TAG, "path background : " + path);
            Picasso.with(this).load(path).into(imgBackground, new Callback() {
                @Override
                public void onSuccess() {
                    AppLog.e(TAG, "onSuccess background");
                }

                @Override
                public void onError() {
                    AppLog.e(TAG, "onError background");
                    imgBackground.setImageResource(R.drawable.app_icon);
                }
            });
        } else {
            imgBackground.setImageResource(R.drawable.app_icon);
        }

        if (wallPref.contains(Constants.BACKGROUND_IMAGE)) {
            if (removeWallMenu != null)
                removeWallMenu.setVisible(true);
        } else {
            if (removeWallMenu != null)
                removeWallMenu.setVisible(false);
        }
    }

    private void _init() {
        imgBackground = findViewById(R.id.imgBackground);
        wallPref = getSharedPreferences(BuildConfig.APPLICATION_ID + ".wall", Context.MODE_PRIVATE);
        imgBackground.setVisibility(View.VISIBLE);
        rvClass.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        AppLog.e(TAG, "BackStack count : " + fm.getBackStackEntryCount());

        editDialog = SMBDialogUtils.showSMBDialogOKCancel_(this, getResources().getString(R.string.msg_app_exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ConstituencyListActivity.super.onBackPressed();
            }
        });

    }


    @Override
    protected void onStop() {
        super.onStop();
        if (editDialog != null) {
            editDialog.cancel();
        }
    }

    public boolean isConnectionAvailable() {
        ConnectivityManager connectivityManager;
        NetworkInfo networkinfo;
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        networkinfo = connectivityManager.getActiveNetworkInfo();

        return (networkinfo != null && networkinfo.isConnectedOrConnecting());

    }

    private void getConstituencyList() {
        if (isConnectionAvailable()) {
            progressBar.setVisibility(View.VISIBLE);
            LeafManager leafManager = new LeafManager();
            leafManager.getConstituencyList(this);
        } else {
            showNoNetworkMsg();
        }

    }

    public void showNoNetworkMsg() {
        /*SMBDialogUtils.showSMBDialogOK(getActivity(), getString(R.string.no_internet), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });*/
        try {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.toolbar), R.string.no_internet, Snackbar.LENGTH_SHORT)
                    .setAction("SETTINGS", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
                    }).setActionTextColor(Color.WHITE);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.snackbar_textsize));
            textView.setTextColor(Color.WHITE);
            snackbar.show();

        } catch (Exception e) {
            AppLog.e("SnackBar", "error is " + e.toString());
            SMBDialogUtils.showSMBDialogOK(this, getString(R.string.no_internet), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    dialog.dismiss();
                }
            });
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);
        if (apiId == LeafManager.API_ID_GROUP_DETAIL) {
            AppLog.e("UserExist->", "getGroupDetail api response");

            GroupDetailResponse gRes = (GroupDetailResponse) response;
            AppLog.e(TAG, "group detail ->" + new Gson().toJson(gRes));
            LeafPreference.getInstance(this).setInt(Constants.TOTAL_MEMBER, gRes.data.get(0).totalUsers);
            //save group detail
            LeafPreference.getInstance(this).setString(Constants.GROUP_DATA, new Gson().toJson(gRes.data.get(0)));
           // LeafPreference.getInstance(this).setString(Constants.TYPE,  gRes.data.get(0).type);


            Intent login = new Intent(this, GroupDashboardActivityNew.class);
            startActivity(login);
        }else {
            ConstituencyRes taluksRes = (ConstituencyRes) response;
            AppLog.e(TAG, "ConstituencyRes " + taluksRes);
            LeafPreference.getInstance(getApplicationContext()).setInt(LeafPreference.GROUP_COUNT,taluksRes.data.size());
            rvClass.setAdapter(new GroupAdapterNew(taluksRes.data));
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {

    }

    @Override
    public void onException(int apiId, String msg) {

    }

    public class GroupAdapterNew extends RecyclerView.Adapter<GroupAdapterNew.ViewHolder> {
        List<GroupItem> list;
        private Context mContext;

        public GroupAdapterNew(List<GroupItem> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_taluks, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final GroupItem item = list.get(position);

            if (!TextUtils.isEmpty(item.image)) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.image)).resize(200, 200).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgGroupNew,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.imgGroupNewDefault.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.image)).resize(200, 200).into(holder.imgGroupNew, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.imgGroupNewDefault.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.imgGroupNewDefault.setVisibility(View.VISIBLE);
                                        TextDrawable drawable = TextDrawable.builder()
                                                .buildRound(ImageUtil.getTextLetter(item.categoryName), ImageUtil.getRandomColor(position));
                                        holder.imgGroupNewDefault.setImageDrawable(drawable);
                                        AppLog.e("Picasso", "Error : ");
                                    }
                                });
                            }
                        });
            } else {
                holder.imgGroupNewDefault.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(item.categoryName), ImageUtil.getRandomColor(position));
                holder.imgGroupNewDefault.setImageDrawable(drawable);
            }

            holder.txtName.setText(item.categoryName);
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    txtEmpty.setText("No Constituency found.");
                } else {
                    txtEmpty.setText("");
                }

                return list.size();
            } else {
                txtEmpty.setText("No Constituency found.");
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.txt_group_name)
            TextView txtName;

            @Bind(R.id.img_group_new)
            CircleImageView imgGroupNew;

            @Bind(R.id.img_group_new_default)
            ImageView imgGroupNewDefault;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            @OnClick({R.id.relative})
            public void onMovieClick(View v) {
                switch (v.getId()) {

                    case R.id.relative:
                        onTaluksSelect(list.get(getLayoutPosition()));
                        break;
                }
            }
        }
    }

    private void onTaluksSelect(GroupItem item) {

        if (item.groupCount == 1) {
            LeafManager manager = new LeafManager();
            progressBar.setVisibility(View.VISIBLE);
            manager.getGroupDetail(this, item.getGroupId());
        } else {
            Intent intent = new Intent(ConstituencyListActivity.this, Home.class);
            intent.putExtra("from", "CONSTITUENCY");
            intent.putExtra("category", item.category);
            intent.putExtra("categoryName", item.categoryName);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        menu.findItem(R.id.menu_search).setVisible(false);
        menu.findItem(R.id.menu_change_pass).setVisible(true);
        menu.findItem(R.id.menu_set_wallpaper).setVisible(true);
        menu.findItem(R.id.menu_change_pin).setVisible(true);
        menu.findItem(R.id.menu_change_mobile).setVisible(true);
        menu.findItem(R.id.menu_change_language).setVisible(true);

        removeWallMenu = menu.findItem(R.id.menu_remove_wallpaper);
        if (wallPref.contains(Constants.BACKGROUND_IMAGE)) {
            removeWallMenu.setVisible(true);
        } else {
            removeWallMenu.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 21) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGallery(REQUEST_LOAD_GALLERY_IMAGE);
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOAD_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            final Uri selectedImage = data.getData();

            SMBDialogUtils.showSMBDialogOKCancel(this, getResources().getString(R.string.smb_do_you_like_set_wallpaper), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectedImage(selectedImage);
                }
            });


        }
    }
    private void selectedImage(Uri selectedImage) {
        try {
            if (selectedImage != null)
                wallPref.edit().putString(Constants.BACKGROUND_IMAGE, selectedImage.toString()).apply();
            setBackgroundImage();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error in set wallpaper", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                logout();
                finish();
                return true;
            case R.id.menu_change_pass:
                Intent intent = new Intent(this, ChangePasswordActivity.class);
                startActivity(intent);
                return true;


            case R.id.menu_change_mobile:
                Intent intentNumber = new Intent(this, ChangeNumberActivity.class);
                startActivity(intentNumber);
                return true;

            case R.id.menu_change_pin:
                Intent intentpin= new Intent(this, ChangePinActivity.class);
                startActivity(intentpin);
                return true;


            case R.id.menu_change_language:
                Intent intent1= new Intent(this, ChangeLanguageActivity.class);
                startActivity(intent1);
                return true;


            case R.id.menu_set_wallpaper:
                if (checkPermissionForWriteExternal()) {
                    startGallery(REQUEST_LOAD_GALLERY_IMAGE);
                } else {
                    requestPermissionForWriteExternal(21);
                }
                return true;
            case R.id.menu_remove_wallpaper:
                wallPref.edit().clear().commit();
                setBackgroundImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void requestPermissionForWriteExternal(int code) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            AppLog.e(TAG, "requestPermissionForWriteExternal");
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
        }
    }
    private int REQUEST_LOAD_GALLERY_IMAGE = 112;
    ImageView imgBackground;
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
}