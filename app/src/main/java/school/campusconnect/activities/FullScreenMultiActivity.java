package school.campusconnect.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.adapters.MultiImageAdapter;
import school.campusconnect.datamodel.Media.ImagePathTBL;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AmazoneHelper;
import school.campusconnect.utils.AmazoneImageDownload;
import school.campusconnect.utils.AmazoneVideoDownload;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.MultipleImageSwipeActivity;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

public class FullScreenMultiActivity extends BaseActivity implements MultiImageAdapter.OnImageClickListener {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    @Bind(R.id.iconBack)
    ImageView iconBack;

    @Bind(R.id.iconShareExternal)
    ImageView iconShareExternal;

    @Bind(R.id.iconAdd)
    ImageView iconAdd;

    ArrayList<String> listImages;

    MultiImageAdapter adapter;
    boolean isFromFloatService = false;
    private String album_id = "",type = "";
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_multiple);
        ButterKnife.bind(this);

        listImages = getIntent().getStringArrayListExtra("image_list");
        album_id = getIntent().getStringExtra("album_id");
        type= getIntent().getStringExtra("type");
        isEdit = getIntent().getBooleanExtra("edit",false);

        Log.e("FullScreenMultiActivity","isEdit"+isEdit+"\n type"+type+"\nalbum id"+album_id);


        if(getIntent().hasExtra("from"))
        isFromFloatService = getIntent().getStringExtra("from").equalsIgnoreCase("floatservice");
        else
        isFromFloatService = false;

        for (String s : listImages) {
            Log.e("Images", s);
        }

        final LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        adapter = new MultiImageAdapter(listImages, this);
        recyclerView.setAdapter(adapter);

        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (isEdit)
        {
            iconAdd.setVisibility(View.VISIBLE);
        }
        else
        {
            iconAdd.setVisibility(View.GONE);
        }

        iconAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddGalleryPostActivity.class);
                intent.putExtra("isEdit", true);
                intent.putExtra("album_id", album_id);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });

        iconShareExternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                boolean isDownloaded = true;

                if (listImages.size()> 0)
                {


                    for (int i = 0;i<listImages.size();i++)
                    {

                        String key =  Constants.decodeUrlToBase64(listImages.get(i)).replace(AmazoneHelper.BUCKET_NAME_URL, "");
                        String Filepath;

                        if (key.contains("/")) {
                            String[] splitStr = key.split("/");
                            Filepath = splitStr[1];
                        } else {
                            Filepath = key;
                        }

                        if (ImagePathTBL.getLastInserted(Filepath).size() == 0)
                        {
                            isDownloaded = false;
                        }

                    }

                }

                if (listImages != null && listImages.size() > 0)
                {
                    if (isDownloaded)
                    {
                        ArrayList<File> files =new ArrayList<>();

                        for (int i = 0;i<listImages.size();i++)
                        {
                            String key =  Constants.decodeUrlToBase64(listImages.get(i)).replace(AmazoneHelper.BUCKET_NAME_URL, "");
                            String Filepath;

                            if (key.contains("/")) {
                                String[] splitStr = key.split("/");
                                Filepath = splitStr[1];
                            } else {
                                Filepath = key;
                            }

                            files.add(new File(ImagePathTBL.getLastInserted(Filepath).get(0).url));

                        }

                        ArrayList<Uri> uris = new ArrayList<>();

                        for(File file: files){

                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                                uris.add(FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file));
                            } else {
                                uris.add(Uri.fromFile(file));
                            }

                        }

                        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                        intent.setType("*/*");
                        intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                        startActivity(Intent.createChooser(intent, "Share File"));
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.smb_no_file_download),Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

    }





    @Override
    public void onImageClick(String imagePath) {

        Intent i = new Intent(this, MultipleImageSwipeActivity.class);
        i.putStringArrayListExtra("image_list", listImages);
        i.putExtra("album_id", album_id);
        i.putExtra("type", type);
        i.putExtra("edit",isEdit);
        this.startActivity(i);

       /* Intent i = new Intent(this, FullScreenActivity.class);
        i.putExtra("image", imagePath);
        startActivity(i);*/
    }

    @Override
    public void onBackPressed() {
        if(!isFromFloatService)
        super.onBackPressed();
        else
        {
            setResult(RESULT_OK);
            finish();
        }
    }
}

