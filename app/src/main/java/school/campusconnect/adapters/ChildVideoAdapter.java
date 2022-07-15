package school.campusconnect.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import school.campusconnect.Assymetric.AGVRecyclerViewAdapter;
import school.campusconnect.Assymetric.AsymmetricItem;
import school.campusconnect.Assymetric.multiimages.ItemImage;
import school.campusconnect.LeafApplication;
import school.campusconnect.R;
import school.campusconnect.activities.FullScreenVideoMultiActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.VideoPlayActivity;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.VideoOfflineObject;
import school.campusconnect.utils.AmazoneHelper;
import school.campusconnect.utils.AmazoneImageDownload;
import school.campusconnect.utils.AmazoneVideoDownload;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;

public class ChildVideoAdapter extends AGVRecyclerViewAdapter<ChildVideoAdapter.ViewHolder> {
    private final ArrayList<String> thumbnailImages;
    private List<ItemImage> items;
    private final ArrayList<String> allImageList;
    private int currentOffset=0;
    private boolean isCol2Avail=false;
    private int mDisplay = 0;
    private int mTotal = 0;
    private Context context;

    AmazoneVideoDownload videoDownloader;

    public ChildVideoAdapter(int mDisplay, Context context, ArrayList<String> allImageList,ArrayList<String> thumbnailImages) {
        this.allImageList = allImageList;
        this.thumbnailImages = thumbnailImages;
        this.mDisplay = mDisplay;
        this.mTotal = allImageList.size();
        this.context = context;
        videoDownloader = new AmazoneVideoDownload(context);
        for (String s : allImageList) {
            Log.e("ChildAdapter Images", s);
        }

        items=new ArrayList<>();

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
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("RecyclerViewActivity", "onCreateView");
        return new ViewHolder(parent, viewType, items, allImageList);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("RecyclerViewActivity", "onBindView position=" + position);
        holder.bind(items, position, mDisplay, mTotal, context);
    }

    @Override
    public int getItemCount() {
        if(mDisplay==2 && items.size()>2)
            return 2;
        else if(mDisplay==4 && items.size()>4)
        {
            return 4;
        }
        else
        {
            return items.size();
        }

    }

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
        private final ImageView imgDownloadVideo;
        private final ImageView img_play;
        private final TextView textView;
        private final ArrayList<String> allImageList;
        private final ImageView imgCancel;
        private final ProgressBar progressBar;
        private final FrameLayout llProgress;
        private final ProgressBar progressBar1;
        AmazoneVideoDownload asyncTask;
        public ViewHolder(ViewGroup parent, int viewType, List<ItemImage> items, ArrayList<String> allImageList) {
            super(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.adapter_video_item, parent, false));

            this.allImageList = allImageList;
            for (String s : allImageList) {
                Log.e("ViewHolder Images", s);
            }
            mImageView = (ImageView) itemView.findViewById(R.id.mImageView);
            textView = (TextView) itemView.findViewById(R.id.tvCount);
            imgDownloadVideo = (ImageView) itemView.findViewById(R.id.imgDownloadVideo);
            img_play = (ImageView) itemView.findViewById(R.id.img_play);
            imgCancel = (ImageView) itemView.findViewById(R.id.imgCancel);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            progressBar1 = (ProgressBar) itemView.findViewById(R.id.progressBar1);
            llProgress = (FrameLayout) itemView.findViewById(R.id.llProgress);
        }

        public void bind(final List<ItemImage> item, final int position, int mDisplay, int mTotal, final Context mContext) {

            Log.e("MULTI_BIND", "image " + position + "is " + Constants.decodeUrlToBase64(item.get(position).getImagePath()));

            if(thumbnailImages!=null && thumbnailImages.size()>position){
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(thumbnailImages.get(position))).placeholder(R.drawable.video_place_holder).into(mImageView);

            }
            if(new AmazoneVideoDownload(mContext).isVideoDownloaded(mContext,item.get(position).getImagePath())){

                Log.e("ChildVideo","Downloaded"+position);
                img_play.setVisibility(View.VISIBLE);
                imgDownloadVideo.setVisibility(View.GONE);
            }else {

                if (!TextUtils.isEmpty(item.get(position).getImagePath())) {
                    String url = Constants.decodeUrlToBase64(item.get(position).getImagePath());
                    String key = url.replace(AmazoneHelper.BUCKET_NAME_URL, "");
                    File file;
                    if (key.contains("/")) {
                        String[] splitStr = key.split("/");
                        file = new File(getDirForMedia(splitStr[0]), splitStr[1]);
                    } else {
                        file = new File(getDirForMedia(""), key);
                    }
                    Log.e("ChildVideo","Downloaded Not "+position +"Path "+file.getAbsolutePath());
                }


                img_play.setVisibility(View.GONE);
                imgDownloadVideo.setVisibility(View.VISIBLE);
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

            imgDownloadVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imgDownloadVideo.setVisibility(View.GONE);
                    llProgress.setVisibility(View.VISIBLE);
                    progressBar1.setVisibility(View.VISIBLE);
                    asyncTask = AmazoneVideoDownload.download(context, item.get(position).getImagePath(), new AmazoneVideoDownload.AmazoneDownloadSingleListener() {
                        @Override
                        public void onDownload(Uri file) {
                            llProgress.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            progressBar1.setVisibility(View.GONE);
                            img_play.setVisibility(View.VISIBLE);
                            imgDownloadVideo.setVisibility(View.GONE);

                            AppLog.e(GroupDashboardActivityNew.class.getName(), "filename saved in preference : "+item.get(position).getImagePath());

                            try {
                                saveVideoNameOffline(item.get(position).getImagePath() , file.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void error(String msg) {
                            ((Activity)mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                }
                            });
                        }

                        @Override
                        public void progressUpdate(int progress, int max) {
                            if(progress>0){
                                progressBar1.setVisibility(View.GONE);
                            }
                            ((Activity)mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(progress);
                                }
                            });
                        }
                    });
                }
            });

            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (allImageList.size() == 1){

                        Intent i = new Intent(mContext, VideoPlayActivity.class);
                        i.putExtra("video", allImageList.get(0));

                        if (thumbnailImages != null) {
                            i.putExtra("thumbnail", thumbnailImages.get(0));
                        }
                        mContext.startActivity(i);
                    } else {
                        Intent i = new Intent(mContext, FullScreenVideoMultiActivity.class);
                        i.putStringArrayListExtra("video_list", allImageList);
                        i.putStringArrayListExtra("thumbnailImages", thumbnailImages);
                        mContext.startActivity(i);
                    }
                }
            });

            imgCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imgDownloadVideo.setVisibility(View.VISIBLE);
                    llProgress.setVisibility(View.GONE);
                    progressBar1.setVisibility(View.GONE);
                    asyncTask.cancel(true);
                }
            });

            // textView.setText(String.valueOf(item.getPosition()));
        }
    }

    private static File getDirForMedia(String folder) {
        File mainFolder = LeafApplication.getInstance().AppFilesPath();
        if (!mainFolder.exists()) {
            mainFolder.mkdir();
        }

        if (TextUtils.isEmpty(folder)) {
            return mainFolder;
        } else {
            File file = new File(mainFolder, folder);
            if (!file.exists()) {
                file.mkdir();
            }
            return file;
        }
    }

    public void saveVideoNameOffline(String fileName, String filePath)
    {
        VideoOfflineObject offlineObject = new VideoOfflineObject();
        offlineObject.setVideo_filename(fileName);
        offlineObject.setVideo_filepath(filePath);
        offlineObject.setVideo_date(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        LeafPreference preference = LeafPreference.getInstance(context);

        if(!preference.getString(LeafPreference.OFFLINE_VIDEONAMES).equalsIgnoreCase(""))
        {
            ArrayList<VideoOfflineObject> offlineObjects = new Gson().fromJson(preference.getString(LeafPreference.OFFLINE_VIDEONAMES), new TypeToken<ArrayList<VideoOfflineObject>>() {
            }.getType());

            offlineObjects.add(offlineObject);
            preference.setString(LeafPreference.OFFLINE_VIDEONAMES , new Gson().toJson(offlineObjects));

        }
        else
        {
            ArrayList<VideoOfflineObject> offlineObjects = new ArrayList<>();
            offlineObjects.add(offlineObject);
            preference.setString(LeafPreference.OFFLINE_VIDEONAMES , new Gson().toJson(offlineObjects));
        }

    }
}


