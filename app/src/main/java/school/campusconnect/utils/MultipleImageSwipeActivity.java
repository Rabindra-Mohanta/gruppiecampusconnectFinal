package school.campusconnect.utils;

import androidx.annotation.NonNull;
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
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.activities.AddGalleryPostActivity;
import school.campusconnect.activities.BaseActivity;
import school.campusconnect.activities.FullScreenActivity;
import school.campusconnect.adapters.MultiImageAdapter;
import school.campusconnect.adapters.TeamListAdapter;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.ActivityMultipleImageSwipeBinding;
import school.campusconnect.databinding.ItemPagerBinding;
import school.campusconnect.datamodel.Media.ImagePathTBL;
import school.campusconnect.views.TouchImageView;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

public class MultipleImageSwipeActivity extends BaseActivity {

   ActivityMultipleImageSwipeBinding binding;
    public static String TAG = "MultipleImageSwipeActivity";
    ArrayList<String> listImages;
    ImageAdapter adapter;


    private String album_id = "",type = "";
    private boolean isEdit = false;

    private int currentPage =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_multiple_image_swipe);

        listImages = getIntent().getStringArrayListExtra("image_list");
        album_id = getIntent().getStringExtra("album_id");
        type= getIntent().getStringExtra("type");
        isEdit = getIntent().getBooleanExtra("edit",false);

        Log.e("FullScreenMultiActivity","isEdit"+isEdit+"\n type"+type+"\nalbum id"+album_id);

        adapter = new ImageAdapter(this,listImages);
        binding.viewPager.setAdapter(adapter);

        for (String s : listImages) {
            Log.e("Images", s);
        }


        binding.iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

        binding.iconRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(TAG,"current page"+currentPage);
                adapter.rotateImage(currentPage);

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


        binding.viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                 currentPage = position;
                 adapter.rotateAllImage(currentPage);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {

        finish();
    }

    public class ImageAdapter extends PagerAdapter {
        Context context;
        ArrayList<String> listImages;
        ArrayList<TouchImageView> imageViews = new ArrayList<>();
        LayoutInflater mLayoutInflater;
        AmazoneImageDownload asyncTask;
        String imagePreviewUrl;
        private int currentPage;
        ImageAdapter(Context context,ArrayList<String> listImages){
            this.listImages = listImages;
            this.context=context;
            imagePreviewUrl = LeafPreference.getInstance(context).getString("PREVIEW_URL","https://ik.imagekit.io/mxfzvmvkayv/");
        }
        @Override
        public int getCount() {
            return listImages.size();
        }

        public void addView(TouchImageView imageView,int position)
        {
            imageViews.add(imageView);
        }


        public void rotateImage(int position)
        {

            float r = imageViews.get(position).getRotation();
            r = r+90;
            if(r>360){
                r=90;
            }
            imageViews.get(position).setRotation(r);
        }

        public void rotateAllImage(int position)
        {
            for (int b = 0;b<imageViews.size();b++)
            {
                if (position != b)
                {
                    imageViews.get(b).setRotation(0);
                }
            }
        }
        @Override
        public int getItemPosition(@NonNull Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewLayout = mLayoutInflater.inflate(R.layout.item_pager, container, false);

            FrameLayout llProgress = (FrameLayout) viewLayout.findViewById(R.id.llProgress);
            ImageView ivDownload = (ImageView) viewLayout.findViewById(R.id.ivDownload);
            TouchImageView ivImage = (TouchImageView) viewLayout.findViewById(R.id.ivImage);
            ProgressBar progressBar = (ProgressBar) viewLayout.findViewById(R.id.progressBar);
            ImageView imgCancel = (ImageView) viewLayout.findViewById(R.id.imgCancel);
         /*   ImageView iconShareExternal = (ImageView) viewLayout.findViewById(R.id.iconShareExternal);
            ImageView iconBack = (ImageView) viewLayout.findViewById(R.id.iconBack);
            ImageView iconAdd = (ImageView) viewLayout.findViewById(R.id.iconAdd);
            ImageView iconRotate = (ImageView) viewLayout.findViewById(R.id.iconRotate);*/


            String key =  Constants.decodeUrlToBase64(listImages.get(position)).replace(AmazoneHelper.BUCKET_NAME_URL, "");
            String Filepath;

            if (key.contains("/")) {
                String[] splitStr = key.split("/");
                Filepath = splitStr[1];
            } else {
                Filepath = key;
            }
            Log.e(TAG, "IMAGE PATH TBL SIZE " + Filepath );

            if (ImagePathTBL.getLastInserted(Filepath).size() > 0)
            {
                String uri = ImagePathTBL.getLastInserted(Filepath).get(0).url;

                Log.e(TAG, "IMAGE PATH " + uri );

                Picasso picasso = Picasso.with(context);

                picasso.load(new File(uri)).placeholder(R.drawable.placeholder_image).memoryPolicy(MemoryPolicy.NO_CACHE).into(ivImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Log.e(TAG, " Picasso Error : ");

                        if (!AmazoneImageDownload.isImageDownloaded(context,listImages.get(position)))
                        {
                            asyncTask = AmazoneImageDownload.download(context, listImages.get(position), new AmazoneImageDownload.AmazoneDownloadSingleListener() {
                                @Override
                                public void onDownload(Uri file) {
                                    llProgress.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);

                                    Log.e(TAG,"in Piccaso image");

                                    Glide.with(context).load(file).placeholder(R.drawable.placeholder_image).into(ivImage);
                                }

                                @Override
                                public void error(String msg) {

                                    ((Activity)context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            llProgress.setVisibility(View.GONE);
                                            progressBar.setVisibility(View.GONE);

                                            Toast.makeText(context, msg + "", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void progressUpdate(int progress, int max) {
                                    ((Activity)context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(progress>0){
                                                progressBar.setVisibility(View.GONE);
                                            }
                                            progressBar.setProgress(progress);
                                        }
                                    });
                                }
                            });
                        }
                        else
                        {
                            Glide.with(context).load(AmazoneImageDownload.getDownloadPath(context,listImages.get(position))).diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .placeholder(R.drawable.placeholder_image).into(ivImage);
                        }
                    }
                });

            }
            else
            {
                if(AmazoneImageDownload.isImageDownloaded(getApplicationContext(),listImages.get(position))){
                    llProgress.setVisibility(View.GONE);
                    ivDownload.setVisibility(View.GONE);
                    Picasso.with(context).load(AmazoneImageDownload.getDownloadPath(getApplicationContext(),listImages.get(position))).placeholder(R.drawable.placeholder_image).networkPolicy(NetworkPolicy.OFFLINE).into(ivImage, new Callback() {
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
                        Picasso.with(context).load(imagePreviewUrl+newStr+"?tr=w-50").placeholder(R.drawable.placeholder_image).into(ivImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Log.e("Picasso", "Error : ");
                            }
                        });
                        ivDownload.setVisibility(View.VISIBLE);
                    }
                }
            }


            ivDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivDownload.setVisibility(View.GONE);
                    llProgress.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    asyncTask = AmazoneImageDownload.download(context, listImages.get(position), new AmazoneImageDownload.AmazoneDownloadSingleListener() {
                        @Override
                        public void onDownload(Uri file) {
                            llProgress.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);

                            Picasso.with(context).load(file).placeholder(R.drawable.placeholder_image).into(ivImage, new Callback() {
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
                                    llProgress.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);


                                }
                            });
                        }

                        @Override
                        public void progressUpdate(int progress, int max) {
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(progress>0){
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    progressBar.setProgress(progress);
                                }
                            });
                        }
                    });


                 }
            });

            imgCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivDownload.setVisibility(View.VISIBLE);
                    llProgress.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    asyncTask.cancel(true);
                }
            });

            addView(ivImage,position);

            ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(getApplicationContext(), FullScreenActivity.class);
                    i.putExtra("image", listImages.get(position));
                    startActivity(i);
                }
            });
            container.addView(viewLayout);
            return viewLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((RelativeLayout) object);
        }
    }
}