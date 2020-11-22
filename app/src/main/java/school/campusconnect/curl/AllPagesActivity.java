package school.campusconnect.curl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.LruCache;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import school.campusconnect.R;

import static school.campusconnect.activities.ViewPDFActivity.itemData;

public class AllPagesActivity extends AppCompatActivity {


    public static final String KEY_SELECTED_PAGE = "selectedPage";
    public static final String KEY_BACK_PRESSED = "backPressed";

    private int bookCurrentIndex;

    ArrayList<byte[]> itemByteArray = new ArrayList<>();
    ArrayList<Bitmap> list_bitmap = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (getResources().getBoolean(R.bool.enable_fullscreen)) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        super.onCreate(savedInstanceState);

        performOrientationLock();


        Intent intent = getIntent();
        if (intent != null) {
            bookCurrentIndex = intent.getIntExtra(KEY_SELECTED_PAGE, 0);
            int size = intent.getIntExtra("barray_size", 0);
            for (int i = 0; i < size; i++) {
                byte[] bytes = intent.getByteArrayExtra("barray" + i);
                itemByteArray.add(bytes);
                list_bitmap.add(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        }


//        try {
//            String[] mBitmapIds = getAssets().list("pages");
//            Arrays.sort(mBitmapIds);
        ListAdapter list = new ListAdapter(this, itemData, itemByteArray);
        GridView grid = new GridView(this);
        grid.setAdapter(list);
        grid.setNumColumns(GridView.AUTO_FIT);
        grid.setSelector(android.R.color.transparent);
        grid.setBackgroundColor(ContextCompat.getColor(this, R.color.grid_background));
        setContentView(grid);
        /*} catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }*/

    }


    private void performOrientationLock() {
        SharedPreferences preferences = getSharedPreferences("book", MODE_PRIVATE);
        int orientation = preferences.getInt(CurlActivity.KEY_ORIENTATION_LOCK, ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setRequestedOrientation(orientation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }


    @Override
    public void onBackPressed() {
        /*Intent intent = new Intent(this, CurlActivity.class);
        intent.putExtra(AllPagesActivity.KEY_SELECTED_PAGE, bookCurrentIndex);
        intent.putExtra(AllPagesActivity.KEY_BACK_PRESSED, true);
        finish();

        startActivity(intent);*/
        super.onBackPressed();
    }


}


class ListAdapter extends BaseAdapter {

    AllPagesActivity context;
    ArrayList<Bitmap> items;
    ArrayList<byte[]> listByteArray;
    private LruCache<String, Bitmap> mMemoryCache;

    public ListAdapter(AllPagesActivity context, ArrayList<Bitmap> items, ArrayList<byte[]> listByteArray) {
        this.context = context;
        this.items = items;
        this.listByteArray = listByteArray;

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }

        };
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int arg0) {
        return items.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View convertView, ViewGroup arg2) {
        ImageView img = null;

        final View view;


        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.grid_view, null);
            img = (ImageView) view.findViewById(R.id.imageView);
            img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            img.setTag(R.id.TAG_PAGENUM_ID, Integer.valueOf(arg0));

            img.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int eventaction = event.getAction();
                    View overlay = view.findViewById(R.id.overlay);
                    if (eventaction == MotionEvent.ACTION_DOWN) {
                        overlay.setVisibility(View.VISIBLE);
                    } else if (eventaction == MotionEvent.ACTION_UP) {

                        overlay.setVisibility(View.GONE);
                        Intent intent = new Intent(context, CurlActivity.class);
                        int p = (Integer) v.getTag(R.id.TAG_PAGENUM_ID);
                        intent.putExtra(AllPagesActivity.KEY_SELECTED_PAGE, p);

                        /*intent.putExtra("barray_size", listByteArray.size());
                        for (int i = 0; i < listByteArray.size(); i++) {
                            intent.putExtra("barray"+i, listByteArray.get(i));
                        }*/

//                    Bundle bundle = new Bundle();
//                    bundle.putParcelableArray("bitmap_array", bitmaps);
//                    intent.putExtras(bundle);
//                    intent.putExtra("bitmap_array", itemByteArray);
//                        startActivity(intent);

                        context.finish();

                        context.startActivity(intent);
                        v.performClick();
                    } else if (eventaction == MotionEvent.ACTION_CANCEL) {
                        overlay.setVisibility(View.GONE);
                    }
                    return true;
                }
            });


        } else {
            view = convertView;
            img = (ImageView) convertView.findViewById(R.id.imageView);
        }

        TextView page = (TextView) view.findViewById(R.id.pageNum);

        if (page != null) {
            page.setText(Integer.toString(arg0 + 1));
            img.setTag(R.id.TAG_PAGE_ID, page);
            img.setTag(R.id.TAG_OVERLAY_ID, view.findViewById(R.id.overlay));
        }


        img.setImageBitmap(items.get(arg0));

//        loadBitmap(arg0, img);

        return view;
    }

   /* public void loadBitmap(int resId, ImageView imageView) {
        if (cancelPotentialWork(resId, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            task.execute(resId);
        }
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(
                    bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    public static boolean cancelPotentialWork(int data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final int bitmapData = bitmapWorkerTask.data;
            if (bitmapData != data) {
                bitmapWorkerTask.cancel(true);
            } else {
                return false;
            }
        }

        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return (Bitmap) mMemoryCache.get(key);
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        public int data = 0;
        private final WeakReference<ImageView> imageViewReference;

        public BitmapWorkerTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }


        @Override
        protected Bitmap doInBackground(Integer... params) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;

            int size = Math.min(width, height) / 3;

            data = params[0];
            final Bitmap bitmap = decodeSampledBitmapFromResource(context, data, size, size);

            addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
            return bitmap;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                    TextView page = (TextView) imageView.getTag(R.id.TAG_PAGE_ID);


                    page.getLayoutParams().width = bitmap.getWidth() + imageView.getPaddingLeft() + imageView.getPaddingRight();
                    page.requestLayout();

                    View view = (View) imageView.getTag(R.id.TAG_OVERLAY_ID);
                    view.getLayoutParams().width = bitmap.getWidth() + imageView.getPaddingLeft() + imageView.getPaddingRight();
                    view.getLayoutParams().height = bitmap.getHeight();
                    view.requestLayout();

                    ((View) imageView.getParent()).setVisibility(View.VISIBLE);

                }
            }
        }
    }


    public Bitmap decodeSampledBitmapFromResource(Context context, int id, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        AssetManager assetManager = context.getAssets();
        InputStream istr;
        String path = "pages/" + items[id];
        try {
            istr = assetManager.open(path);
            BitmapFactory.decodeStream(istr, null, options);
            istr.close();

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            istr = assetManager.open(path);
            Bitmap bmp = BitmapFactory.decodeStream(istr, null, options);
            istr.close();

            return bmp;
        } catch (IOException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }


    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }*/

}

