package school.campusconnect.utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.activities.AddGalleryPostActivity;
import school.campusconnect.activities.BaseActivity;
import school.campusconnect.adapters.MultiImageAdapter;
import school.campusconnect.adapters.TeamListAdapter;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.ActivityMultipleImageSwipeBinding;
import school.campusconnect.databinding.ItemPagerBinding;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

public class MultipleImageSwipeActivity extends BaseActivity {

   ActivityMultipleImageSwipeBinding binding;

    ArrayList<String> listImages;



    private String album_id = "",type = "";
    private boolean isEdit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_multiple_image_swipe);

        listImages = getIntent().getStringArrayListExtra("image_list");
        album_id = getIntent().getStringExtra("album_id");
        type= getIntent().getStringExtra("type");
        isEdit = getIntent().getBooleanExtra("edit",false);

        Log.e("FullScreenMultiActivity","isEdit"+isEdit+"\n type"+type+"\nalbum id"+album_id);



        for (String s : listImages) {
            Log.e("Images", s);
        }


        binding.iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (isEdit)
        {
            binding.iconAdd.setVisibility(View.VISIBLE);
        }
        else
        {
            binding.iconAdd.setVisibility(View.GONE);
        }


        binding.iconAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddGalleryPostActivity.class);
                intent.putExtra("isEdit", true);
                intent.putExtra("album_id", album_id);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });

        binding.iconShareExternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isDownloaded = true;

                if (listImages.size()> 0)
                {

                    for (int i = 0;i<listImages.size();i++)
                    {
                        if (!AmazoneImageDownload.isImageDownloaded((listImages.get(i))))
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

                            files.add(AmazoneImageDownload.getDownloadPath(listImages.get(i)));

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

        ImageAdapter adapter = new ImageAdapter(this,listImages);
        binding.viewPager.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {

        finish();
    }

    public class ImageAdapter extends PagerAdapter {
        Context context;
        ArrayList<String> listImages;
        LayoutInflater mLayoutInflater;
        AmazoneImageDownload asyncTask;
        String imagePreviewUrl;
        ItemPagerBinding itemView;
        ImageAdapter(Context context,ArrayList<String> listImages){
            this.listImages = listImages;
            this.context=context;
            imagePreviewUrl = LeafPreference.getInstance(context).getString("PREVIEW_URL","https://ik.imagekit.io/mxfzvmvkayv/");
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return listImages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            itemView = DataBindingUtil.inflate(mLayoutInflater,R.layout.item_pager,container,false);

            container.addView(itemView.getRoot());

            if(AmazoneImageDownload.isImageDownloaded(listImages.get(position))){
                itemView.llProgress.setVisibility(View.GONE);
                itemView.ivDownload.setVisibility(View.GONE);
                Picasso.with(context).load(AmazoneImageDownload.getDownloadPath(listImages.get(position))).placeholder(R.drawable.placeholder_image).networkPolicy(NetworkPolicy.OFFLINE).into(itemView.ivImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Log.e("Picasso", "Error : ");
                    }
                });
            }
            else
            {
                {
                    String path = Constants.decodeUrlToBase64(listImages.get(position));
                    String newStr = path.substring(path.indexOf("/images")+1);
                    Picasso.with(context).load(imagePreviewUrl+newStr+"?tr=w-50").placeholder(R.drawable.placeholder_image).into(itemView.ivImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Log.e("Picasso", "Error : ");
                        }
                    });
                    itemView.ivDownload.setVisibility(View.VISIBLE);
                }
            }



            itemView.ivDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemView.ivDownload.setVisibility(View.GONE);
                    itemView.llProgress.setVisibility(View.VISIBLE);
                    itemView.progressBar.setVisibility(View.VISIBLE);
                    asyncTask = AmazoneImageDownload.download(context, listImages.get(position), new AmazoneImageDownload.AmazoneDownloadSingleListener() {
                        @Override
                        public void onDownload(File file) {
                            itemView.llProgress.setVisibility(View.GONE);
                            itemView.progressBar.setVisibility(View.GONE);

                            Picasso.with(context).load(file).placeholder(R.drawable.placeholder_image).fit().into(itemView.ivImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {

                                }
                            });
                        }

                        @Override
                        public void error(String msg) {
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    itemView.llProgress.setVisibility(View.GONE);
                                    itemView.progressBar.setVisibility(View.GONE);


                                }
                            });
                        }

                        @Override
                        public void progressUpdate(int progress, int max) {
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(progress>0){
                                        itemView.progressBar.setVisibility(View.GONE);
                                    }
                                    itemView.progressBar.setProgress(progress);
                                }
                            });
                        }
                    });


                 }
            });

            itemView.imgCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    asyncTask.cancel(true);
                }
            });

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout)object);
        }
    }
}