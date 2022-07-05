package school.campusconnect.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import school.campusconnect.Assymetric.AGVRecyclerViewAdapter;
import school.campusconnect.Assymetric.AsymmetricItem;
import school.campusconnect.Assymetric.multiimages.ItemImage;
import school.campusconnect.R;
import school.campusconnect.activities.FullScreenActivity;
import school.campusconnect.activities.FullScreenMultiActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.ViewPDFActivity;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.Media.ImagePathTBL;
import school.campusconnect.datamodel.notificationList.AllNotificationTable;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.AmazoneImageDownload;
import school.campusconnect.utils.AmazoneVideoDownload;
import school.campusconnect.utils.Constants;

public class ChildAdapter extends AGVRecyclerViewAdapter<ChildAdapter.ViewHolder> {

    private final String TAG = "ChildAdapter";
    private List<ItemImage> items;
    private final ArrayList<String> allImageList;

    private int currentOffset = 0;
    private boolean isCol2Avail = false;
    private int mDisplay = 0;
    private int mTotal = 0;
    private Context context;
    private String userToken = "";
  //  private boolean isBirthday = false;
    //Bitmap BirthdayTempleteBitmap,MlaBitmap,UserBitmap;
    boolean showDownloadButton = true;

    String imagePreviewUrl = "";
    public ChildAdapter(String userToken,int mDisplay, int mTotal, Context context, ArrayList<String> allImageList) {
        this.allImageList = allImageList;
        this.mDisplay = mDisplay;
        this.mTotal = mTotal;
        this.context = context;
        this.userToken = userToken;
        for (String s : allImageList) {
            Log.e(TAG,"Images"+ s);
        }

        items = new ArrayList<>();

        // ArrayList<ItemImage> tempData=new ArrayList<>();
        for (int i = 0; i < allImageList.size(); i++) {
            ItemImage itemImage = new ItemImage(allImageList.get(i));
            int colSpan1;
            int rowSpan1;

            if (allImageList.size() == 1) {
                colSpan1 = 2;
                rowSpan1 = 2;
            } else {
                colSpan1 = 1;
                rowSpan1 = 1;
            }

            if (colSpan1 == 2 && !isCol2Avail)
                isCol2Avail = true;
            else if (colSpan1 == 2 && isCol2Avail)
                colSpan1 = 1;

            itemImage.setColumnSpan(colSpan1);
            itemImage.setRowSpan(rowSpan1);
            itemImage.setPosition(currentOffset + i);
            items.add(itemImage);

          /*  int size = Constants.MAX_IMAGE_NUM;
            if (tempData.size() < size)
                size = tempData.size();
            for (int j = 0; j < size; j++) {
                items.add(tempData.get(j));
            }*/
        }


        imagePreviewUrl = LeafPreference.getInstance(context).getString("PREVIEW_URL","https://ik.imagekit.io/mxfzvmvkayv/");
    }


    public ChildAdapter(int mDisplay, int mTotal, Context context, ArrayList<String> allImageList) {
        this.allImageList = allImageList;
        this.mDisplay = mDisplay;
        this.mTotal = mTotal;
        this.context = context;
        for (String s : allImageList) {
            Log.e(TAG,"Images"+ s);
        }

        items = new ArrayList<>();

        // ArrayList<ItemImage> tempData=new ArrayList<>();
        for (int i = 0; i < allImageList.size(); i++) {
            ItemImage itemImage = new ItemImage(allImageList.get(i));
            int colSpan1;
            int rowSpan1;

            if (allImageList.size() == 1) {
                colSpan1 = 2;
                rowSpan1 = 2;
            } else {
                colSpan1 = 1;
                rowSpan1 = 1;
            }

            if (colSpan1 == 2 && !isCol2Avail)
                isCol2Avail = true;
            else if (colSpan1 == 2 && isCol2Avail)
                colSpan1 = 1;

            itemImage.setColumnSpan(colSpan1);
            itemImage.setRowSpan(rowSpan1);
            itemImage.setPosition(currentOffset + i);
            items.add(itemImage);

          /*  int size = Constants.MAX_IMAGE_NUM;
            if (tempData.size() < size)
                size = tempData.size();
            for (int j = 0; j < size; j++) {
                items.add(tempData.get(j));
            }*/
        }


        imagePreviewUrl = LeafPreference.getInstance(context).getString("PREVIEW_URL","https://ik.imagekit.io/mxfzvmvkayv/");
    }

    public void setShowDownloadButton(boolean showOrhide)
    {
        showDownloadButton = showOrhide;
    }

   /* public ChildAdapter(int mDisplay, int mTotal, Context context, ArrayList<String> allImageList,boolean isBirthday) {
        this.allImageList = allImageList;
        this.isBirthday = isBirthday;
        this.mDisplay = mDisplay;
        this.mTotal = mTotal;
        this.context = context;
        for (String s : allImageList) {
            Log.e("ChildAdapter Images", s);
        }

        items = new ArrayList<>();

        // ArrayList<ItemImage> tempData=new ArrayList<>();
        for (int i = 0; i < allImageList.size(); i++) {
            ItemImage itemImage = new ItemImage(allImageList.get(i));
            int colSpan1;
            int rowSpan1;

            if (allImageList.size() == 1) {
                colSpan1 = 2;
                rowSpan1 = 2;
            } else {
                colSpan1 = 1;
                rowSpan1 = 1;
            }

            if (colSpan1 == 2 && !isCol2Avail)
                isCol2Avail = true;
            else if (colSpan1 == 2 && isCol2Avail)
                colSpan1 = 1;

            itemImage.setColumnSpan(colSpan1);
            itemImage.setRowSpan(rowSpan1);
            itemImage.setPosition(currentOffset + i);
            items.add(itemImage);

          *//*  int size = Constants.MAX_IMAGE_NUM;
            if (tempData.size() < size)
                size = tempData.size();
            for (int j = 0; j < size; j++) {
                items.add(tempData.get(j));
            }*//*
        }


        imagePreviewUrl = LeafPreference.getInstance(context).getString("PREVIEW_URL","https://ik.imagekit.io/mxfzvmvkayv/");
    }*/

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateView");
        return new ViewHolder(parent, viewType, items, allImageList);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG, "onBindView position=" + position);
        holder.bind(items, position, mDisplay, mTotal, context);

    }

    @Override
    public int getItemCount()
    {
        if (mDisplay == 2 && items.size() > 2)
            return 2;
        else if (mDisplay == 4 && items.size() > 4) {
            return 4;
        } else {
            return items.size();
        }
    }

 /*   public void isBirthday(boolean isBirthday)
    {
        this.isBirthday = isBirthday;
    }*/
    @Override
    public AsymmetricItem getItem(int position) {
        return (AsymmetricItem) items.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? 1 : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mImageView;
        private final TextView textView;
        private final ArrayList<String> allImageList;
        private final ImageView imgDownload;
        private final ImageView imgCancel;
        private final ProgressBar progressBar;
        private final FrameLayout llProgress;
        private final ProgressBar progressBar1;
        AmazoneImageDownload asyncTask;

        public ViewHolder(ViewGroup parent, int viewType, List<ItemImage> items, ArrayList<String> allImageList) {
            super(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.adapter_item, parent, false));

            this.allImageList = allImageList;
            for (String s : allImageList) {
                Log.e(TAG,"ViewHolder Images"+ s);
            }
            mImageView = (ImageView) itemView.findViewById(R.id.mImageView);
            textView = (TextView) itemView.findViewById(R.id.tvCount);
            imgDownload = (ImageView) itemView.findViewById(R.id.imgDownload);
            imgCancel = (ImageView) itemView.findViewById(R.id.imgCancel);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            progressBar1 = (ProgressBar) itemView.findViewById(R.id.progressBar1);
            llProgress = (FrameLayout) itemView.findViewById(R.id.llProgress);

        }

        public void bind(final List<ItemImage> item, final int position, int mDisplay, int mTotal, final Context mContext) {


            if (ImagePathTBL.getLastInserted(Constants.decodeUrlToBase64(item.get(position).getImagePath())).size() > 0)
            {
                Uri uri = Uri.parse(ImagePathTBL.getLastInserted(Constants.decodeUrlToBase64(item.get(position).getImagePath())).get(0).url);

                Log.e(TAG, "IMAGE PATH " + uri );

             /*   Picasso picasso = Picasso.with(context);
                picasso.load(uri).placeholder(R.drawable.placeholder_image).memoryPolicy(MemoryPolicy.NO_CACHE).into(mImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Log.e(TAG, " Picasso Error : ");
                    *//*    asyncTask = AmazoneImageDownload.download(mContext, item.get(position).getImagePath(), new AmazoneImageDownload.AmazoneDownloadSingleListener() {
                            @Override
                            public void onDownload(Uri file) {
                                llProgress.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                progressBar1.setVisibility(View.GONE);
                                Picasso.with(mContext).load(file).placeholder(R.drawable.placeholder_image).fit().into(mImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Log.e(TAG,"Picasso Error : ");
                                    }
                                });
                            }

                            @Override
                            public void error(String msg) {
                                ((Activity)mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        llProgress.setVisibility(View.GONE);
                                        progressBar.setVisibility(View.GONE);
                                        progressBar1.setVisibility(View.GONE);
                                        Toast.makeText(mContext, msg + "", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void progressUpdate(int progress, int max) {
                                ((Activity)mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(progress>0){
                                            progressBar1.setVisibility(View.GONE);
                                        }
                                        progressBar.setProgress(progress);
                                    }
                                });
                            }
                        });*//*

                    }
                });
*/
                Glide.with(mContext).load(uri).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.placeholder_image).onlyRetrieveFromCache(true).addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.e(TAG,"onLoadFailed"+e.getMessage());
                              /*  asyncTask = AmazoneImageDownload.download(mContext, item.get(position).getImagePath(), new AmazoneImageDownload.AmazoneDownloadSingleListener() {
                                    @Override
                                    public void onDownload(Uri file) {
                                        llProgress.setVisibility(View.GONE);
                                        progressBar.setVisibility(View.GONE);
                                        progressBar1.setVisibility(View.GONE);
                                        Picasso.with(mContext).load(file).placeholder(R.drawable.placeholder_image).fit().into(mImageView, new Callback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onError() {
                                                Log.e(TAG,"Picasso Error : ");
                                            }
                                        });
                                    }

                                    @Override
                                    public void error(String msg) {
                                        ((Activity)mContext).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                llProgress.setVisibility(View.GONE);
                                                progressBar.setVisibility(View.GONE);
                                                progressBar1.setVisibility(View.GONE);
                                                Toast.makeText(mContext, msg + "", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void progressUpdate(int progress, int max) {
                                        ((Activity)mContext).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(progress>0){
                                                    progressBar1.setVisibility(View.GONE);
                                                }
                                                progressBar.setProgress(progress);
                                            }
                                        });
                                    }
                                });*/
                                return false;
                            }


                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.e(TAG,"onResourceReady");
                                return false;
                            }
                        }).into(mImageView);
            }
            else
            {
                if(AmazoneImageDownload.isImageDownloaded(context,item.get(position).getImagePath())){
                    llProgress.setVisibility(View.GONE);
                    imgDownload.setVisibility(View.GONE);

                    //     Log.e(TAG, "AmazoneImageDownload image "  + AmazoneImageDownload.getDownloadPath(context,item.get(position).getImagePath()));
                    Glide.with(mContext).load(AmazoneImageDownload.getDownloadPath(context,item.get(position).getImagePath())).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.placeholder_image).into(mImageView);
                }
                else {

               /* if (isBirthday)
                {
                    BirthdayTempleteBitmap = drawableToBitmap(context.getResources().getDrawable(R.drawable.birthday_templete));
                    MlaBitmap = drawableToBitmap(context.getResources().getDrawable(R.drawable.mla));

                    Log.e(TAG,"BirthdayTempleteBitmap H "+BirthdayTempleteBitmap.getHeight());
                    Log.e(TAG,"BirthdayTempleteBitmap W "+BirthdayTempleteBitmap.getWidth());

                    Log.e(TAG,"MlaBitmap H "+MlaBitmap.getHeight());
                    Log.e(TAG,"MlaBitmap W "+MlaBitmap.getWidth());


                    asyncTask = AmazoneImageDownload.download(mContext, item.get(position).getImagePath(), new AmazoneImageDownload.AmazoneDownloadSingleListener() {
                        @Override
                        public void onDownload(File file) {
                            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                            UserBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),bmOptions);

                            Log.e(TAG,"UserBitmap H "+UserBitmap.getHeight());
                            Log.e(TAG,"UserBitmap W "+UserBitmap.getWidth());

                            File file1 = createBitmap(BirthdayTempleteBitmap,MlaBitmap,UserBitmap,file);

                            Picasso.with(mContext).load(file1).placeholder(R.drawable.placeholder_image).fit().into(mImageView, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Log.e("Picasso", "Error : ");
                                }
                            });

                        }
                        @Override
                        public void error(String msg) {
                            ((Activity)mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Toast.makeText(mContext, msg + "", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void progressUpdate(int progress, int max) {
                            ((Activity)mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                }
                            });
                        }
                    });
                }*/
               /* else
                {

                }*/

                    String path = Constants.decodeUrlToBase64(item.get(position).getImagePath());
                    String newStr = path.substring(path.indexOf("/images")+1);

                    Log.e(TAG,"full path "+imagePreviewUrl+newStr+"?tr=w-50");

                    Log.e(TAG,"imagePreviewUrl "+imagePreviewUrl);

                    Log.e(TAG,"newStr "+newStr);

                    Picasso.with(mContext).load(imagePreviewUrl+newStr+"?tr=w-50").placeholder(R.drawable.placeholder_image).into(mImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Log.e(TAG, " Picasso Error : ");
                        }
                    });
                    imgDownload.setVisibility(View.VISIBLE);

                    imgDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imgDownload.setVisibility(View.GONE);
                            llProgress.setVisibility(View.VISIBLE);
                            progressBar1.setVisibility(View.VISIBLE);

                            asyncTask = AmazoneImageDownload.download(mContext, item.get(position).getImagePath(), new AmazoneImageDownload.AmazoneDownloadSingleListener() {
                                @Override
                                public void onDownload(Uri file) {
                                    llProgress.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                    progressBar1.setVisibility(View.GONE);
                                    Picasso.with(mContext).load(file).placeholder(R.drawable.placeholder_image).fit().into(mImageView, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError() {
                                            Log.e(TAG,"Picasso Error : ");
                                        }
                                    });
                                }

                                @Override
                                public void error(String msg) {
                                    ((Activity)mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            llProgress.setVisibility(View.GONE);
                                            progressBar.setVisibility(View.GONE);
                                            progressBar1.setVisibility(View.GONE);
                                            Toast.makeText(mContext, msg + "", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void progressUpdate(int progress, int max) {
                                    ((Activity)mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(progress>0){
                                                progressBar1.setVisibility(View.GONE);
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
                            imgDownload.setVisibility(View.VISIBLE);
                            llProgress.setVisibility(View.GONE);
                            progressBar1.setVisibility(View.GONE);
                            asyncTask.cancel(true);
                        }
                    });


                    /*AUTO DOWNLOAD*/
                    imgDownload.setVisibility(View.GONE);
                    llProgress.setVisibility(View.VISIBLE);
                    progressBar1.setVisibility(View.VISIBLE);

                    asyncTask = AmazoneImageDownload.download(mContext, item.get(position).getImagePath(), new AmazoneImageDownload.AmazoneDownloadSingleListener() {
                        @Override
                        public void onDownload(Uri file) {
                            llProgress.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            progressBar1.setVisibility(View.GONE);
                            Picasso.with(mContext).load(file).placeholder(R.drawable.placeholder_image).fit().into(mImageView, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Log.e(TAG,"Picasso Error : ");
                                }
                            });
                        }

                        @Override
                        public void error(String msg) {
                            ((Activity)mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    llProgress.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                    progressBar1.setVisibility(View.GONE);
                                    Toast.makeText(mContext, msg + "", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void progressUpdate(int progress, int max) {
                            ((Activity)mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(progress>0){
                                        progressBar1.setVisibility(View.GONE);
                                    }
                                    progressBar.setProgress(progress);
                                }
                            });
                        }
                    });
                    /*AUTO END DOWNLOAD*/
                }
            }


            textView.setText("+" + (mTotal - mDisplay));
            if (mTotal > mDisplay) {
                if (position == mDisplay - 1) {
                    textView.setVisibility(View.VISIBLE);
                    mImageView.setAlpha(72);
                } else {
                    textView.setVisibility(View.INVISIBLE);
                    mImageView.setAlpha(255);
                }
            } else {
                mImageView.setAlpha(255);
                textView.setVisibility(View.INVISIBLE);
            }

            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (allImageList.size() == 1) {
                        Intent i = new Intent(mContext, FullScreenActivity.class);
                        i.putExtra("image", allImageList.get(0));
                        mContext.startActivity(i);
                    } else {
                        Intent i = new Intent(mContext, FullScreenMultiActivity.class);
                        i.putStringArrayListExtra("image_list", allImageList);
                        mContext.startActivity(i);
                    }
                }
            });


            // textView.setText(String.valueOf(item.getPosition()));
        }
    }

    private File createBitmap(Bitmap birthdayTempleteBitmap, Bitmap mlaBitmap, Bitmap userBitmap, File file) {

        Bitmap result = Bitmap.createBitmap(birthdayTempleteBitmap.getWidth(), birthdayTempleteBitmap.getHeight(), birthdayTempleteBitmap.getConfig());
        Canvas canvas = new Canvas(result);

        canvas.drawBitmap(birthdayTempleteBitmap,0,0,null);
        Paint paint = new Paint();
        paint.setColor(context.getResources().getColor(R.color.black));
        paint.setTextSize(result.getHeight()*0.15f);

        String user= "user";
        Rect rect = new Rect();
        paint.getTextBounds(user,0,user.length(),rect);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawBitmap(mlaBitmap, (float) (birthdayTempleteBitmap.getWidth()-mlaBitmap.getWidth()*1.4), (float) (birthdayTempleteBitmap.getHeight()-mlaBitmap.getHeight()*1.4), null);
        canvas.drawBitmap(userBitmap,userBitmap.getWidth()*0.4f , userBitmap.getWidth()*0.4f, null);
        canvas.drawText(user,result.getWidth()-rect.width()-50,result.getHeight()*0.30f,paint);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.JPEG, 60, bytes);


        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(bytes.toByteArray());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"IOException"+e.getMessage());
        }

        return file;

    }

    public Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}


