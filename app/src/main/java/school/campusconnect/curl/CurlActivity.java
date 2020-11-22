/*
   Copyright 2012 Harri Smatt

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package school.campusconnect.curl;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;

import school.campusconnect.R;

import static school.campusconnect.activities.ViewPDFActivity.itemData;


public class CurlActivity extends AppCompatActivity implements /*Toolbar.OnMenuItemClickListener,*/ CurlView.OnPageChangeListener {


    private static final int MENU_ID_RESET_ZOOM = 1;
    private static final int MENU_ID_LOCK_ORIENTATION = 2;
    private static final int MENU_ID_GO_TO_PAGE = 3;
    private static final int MENU_ID_NEXT_PAGE = 4;
    private static final int MENU_ID_PREV_PAGE = 5;
    private static final int MENU_ID_ABOUT = 6;
    private static final int MENU_ID_SAVE_PDF = 7;
    private static final int MENU_ID_VIEW_ALL_PAGES = 8;

    private static final String KEY_ABOUT_DIALOG = "aboutDialog";
    private static final String KEY_GOTO_PAGE_DIALOG = "gotoPageDialog";


    private CurlView mCurlView;
    private Toolbar toolbar;
    public static final String KEY_ORIENTATION_LOCK = "orientationLock";
    private int page_width_in_pixels, page_height_in_pixels;

    private String[] pages;
    private SharedPreferences preferences;
    private AlertDialog aboutDialog, gotoDialog;
    private PageProvider pageProvider;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private ImageView ivDownload;

    String pdf = "";
    String title = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getResources().getBoolean(R.bool.enable_fullscreen)) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        page_width_in_pixels = getResources().getInteger(R.integer.page_width_in_pixels);
        page_height_in_pixels = getResources().getInteger(R.integer.page_height_in_pixels);

        preferences = getSharedPreferences("book", MODE_PRIVATE);

        int index = 0;
        if (getLastCustomNonConfigurationInstance() != null) {
            index = (Integer) getLastCustomNonConfigurationInstance();
        }

        ivDownload = (ImageView) findViewById(R.id.ivDownload);
        final Intent intent = getIntent();

        if (intent != null) {
            pdf = intent.getExtras().getString("pdf", "");
            title = intent.getExtras().getString("name", "");
//            index = intent.getIntExtra(AllPagesActivity.KEY_SELECTED_PAGE, -1);
            ivDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdf));
                        startActivity(browserIntent);
                    } catch (ActivityNotFoundException e) {

                    }
                }
            });
        }

        mCurlView = (CurlView) findViewById(R.id.curl);
        mCurlView.setOnPageChangeListener(this);
        pageProvider = new PageProvider();
        mCurlView.setPageProvider(pageProvider);
        mCurlView.setSizeChangedObserver(new SizeChangedObserver());
        if (index == 0) {
            mCurlView.setCurrentIndex(index);
        } else {
            assert intent != null;
            if (intent.getBooleanExtra(AllPagesActivity.KEY_BACK_PRESSED, false)) {

            } else {
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    int add = index % 2 == 0 ? 0 : 1;
                    index = (int) Math.ceil(index / 2) + add;
                } else {
                    if (intent.getIntExtra(AllPagesActivity.KEY_SELECTED_PAGE, -1) == -1) {
                        index *= 2;
                    }
                }
            }

            mCurlView.setCurrentIndex(index);
        }

        mCurlView.setCurrentIndex(index);
        assert intent != null;
        intent.removeExtra(AllPagesActivity.KEY_SELECTED_PAGE);
        intent.removeExtra(AllPagesActivity.KEY_BACK_PRESSED);

        mCurlView.setBackgroundColor(ContextCompat.getColor(this, R.color.background_color));
        mCurlView.setAllowLastPageCurl(false);

        performOrientationLock();

        prepareToolbar();
        // This is something somewhat experimental. Before uncommenting next
        // line, please see method comments in CurlView.
        // mCurlView.setEnableTouchPressure(true);

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(KEY_ABOUT_DIALOG, false)) {
                showAboutDialog();
            }
            if (savedInstanceState.getBoolean(KEY_GOTO_PAGE_DIALOG, false)) {
                gotoPage();
            }
        }
    }


    private void performOrientationLock() {
        int orientation = preferences.getInt(KEY_ORIENTATION_LOCK, ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setRequestedOrientation(orientation);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (aboutDialog != null && aboutDialog.isShowing()) {
            outState.putBoolean(KEY_ABOUT_DIALOG, true);
        }

        if (gotoDialog != null && gotoDialog.isShowing()) {
            outState.putBoolean(KEY_GOTO_PAGE_DIALOG, true);
        }

    }


    private void prepareToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
//        setTitle(R.string.toolbar_title);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
//        createMenuItems();
//        toolbar.setOnMenuItemClickListener(this);
    }


    @SuppressLint("AlwaysShowAction")
    private void createMenuItems() {
        Menu m = toolbar.getMenu();

        m.clear();


        m.add(Menu.NONE, MENU_ID_PREV_PAGE, Menu.NONE, getString(R.string.menu_title_prev_page))
                .setEnabled(mCurlView.panZoomManager.zoom == 1f)
                .setIcon(R.drawable.ic_skip_previous_black_24dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        m.getItem(0).getIcon().setAlpha(mCurlView.panZoomManager.zoom == 1f ? 255 : 50);


        m.add(Menu.NONE, MENU_ID_NEXT_PAGE, Menu.NONE, getString(R.string.menu_title_next_page))
                .setEnabled(mCurlView.panZoomManager.zoom == 1f)
                .setIcon(R.drawable.ic_skip_next_black_24dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        m.getItem(1).getIcon().setAlpha(mCurlView.panZoomManager.zoom == 1f ? 255 : 50);

        m.add(Menu.NONE, MENU_ID_GO_TO_PAGE, Menu.NONE, getString(R.string.menu_title_go_to_page));
        m.add(Menu.NONE, MENU_ID_RESET_ZOOM, Menu.NONE, getString(R.string.menu_title_reset_zoom));


        m.add(Menu.NONE, MENU_ID_VIEW_ALL_PAGES, Menu.NONE, getString(R.string.menu_title_view_all_pages));


        /*if (getResources().getBoolean(R.bool.enable_pdf_save)) {
            m.add(Menu.NONE, MENU_ID_SAVE_PDF, Menu.NONE, getString(R.string.menu_title_save_pdf));
        }*/


        boolean oriChecked = preferences.getInt(KEY_ORIENTATION_LOCK, ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        m.add(Menu.NONE, MENU_ID_LOCK_ORIENTATION, Menu.NONE, getString(R.string.menu_title_lock_orientation)).setCheckable(true).setChecked(oriChecked);

        /*m.add(Menu.NONE, MENU_ID_ABOUT, Menu.NONE, getString(R.string.menu_title_about));*/

    }


    @Override
    public void onPause() {
        super.onPause();
        mCurlView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        performOrientationLock();
        mCurlView.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pdf, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mCurlView.getCurrentIndex();
    }

    /**
     * Bitmap provider.
     */
    private class PageProvider implements CurlView.PageProvider {

        //        String[] mBitmapIds;
        ArrayList<Bitmap> bitmapArrayList = itemData;


        public PageProvider() {
            /*try {
                mBitmapIds = getAssets().list("pages");

                if (mBitmapIds.length == 0) {
                    Toast.makeText(CurlActivity.this, "No page found!", Toast.LENGTH_LONG).show();
                    return;
                }


                Arrays.sort(mBitmapIds);


            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(CurlActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }*/
        }


        @Override
        public int getPageCount() {

            int orientation = getResources().getConfiguration().orientation;

            if (orientation == Configuration.ORIENTATION_LANDSCAPE)
                return (int) Math.ceil((double) bitmapArrayList.size() / 2.0);
            else
                return bitmapArrayList.size();
        }


        public boolean isEven() {
            return bitmapArrayList.size() % 2 == 0;
        }

        private Bitmap loadBitmap(int width, int height, int index) {

            width = page_width_in_pixels;
            height = page_height_in_pixels;


            Bitmap b = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            b.eraseColor(0xffffff);
            Canvas c = new Canvas(b);

            int orientation = getResources().getConfiguration().orientation;
            Matrix flip = null;

            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                flip = new Matrix();
                if (index % 2 == 0)
                    flip.postScale(1f, 1f);
                else
                    flip.postScale(-1f, 1f);
            }


//            Bitmap bmp = getBitmapFromAsset("pages/" + mBitmapIds[index]);
            Bitmap bmp = bitmapArrayList.get(index);
            Bitmap flipped = null;


            if (flip != null)
                flipped = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), flip, true);

            BitmapDrawable d = new BitmapDrawable(getResources(), flipped == null ? bmp : flipped);

            int margin = 0;
            int border = 0;
            Rect r = new Rect(margin, margin, width - margin, height - margin);

            int imageWidth = r.width() - (border * 2);
            int imageHeight = imageWidth * d.getIntrinsicHeight()
                    / d.getIntrinsicWidth();
            if (imageHeight > r.height() - (border * 2)) {
                imageHeight = r.height() - (border * 2);
                imageWidth = imageHeight * d.getIntrinsicWidth()
                        / d.getIntrinsicHeight();
            }

            r.right = r.left + imageWidth + border + border;
            r.top += ((r.height() - imageHeight) / 2) - border;
            r.bottom = r.top + imageHeight + border + border;

            Paint p = new Paint();
            p.setColor(0xFFC0C0C0);
            c.drawRect(r, p);
            r.left += border;
            r.right -= border;
            r.top += border;
            r.bottom -= border;

            d.setBounds(r);
            d.draw(c);


            return b;
        }

        private Bitmap getBitmapFromAsset(String strName) {
            AssetManager assetManager = getAssets();
            InputStream istr = null;
            try {
                istr = assetManager.open(strName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(istr);
            return bitmap;
        }

        @Override
        public void updatePage(CurlPage page, int width, int height, int index) {

            try {
                int orientation = getResources().getConfiguration().orientation;

                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {


                    if (index == 0) {

                        Bitmap front = loadBitmap(width, height, 0);
                        Bitmap back = loadBitmap(width, height, 1);
                        page.setTexture(front, CurlPage.SIDE_FRONT);
                        page.setTexture(back, CurlPage.SIDE_BACK);

                    } else if (index == getPageCount() - 1) {

                        if (bitmapArrayList.size() % 2 == 0) {
                            Bitmap back = loadBitmap(width, height, bitmapArrayList.size() - 1);
                            page.setTexture(back, CurlPage.SIDE_BACK);
                            Bitmap front = loadBitmap(width, height, bitmapArrayList.size() - 2);
                            page.setTexture(front, CurlPage.SIDE_FRONT);
                        } else {
                            Bitmap front = loadBitmap(width, height, bitmapArrayList.size() - 1);
                            page.setTexture(front, CurlPage.SIDE_FRONT);
                        }

                    } else {
                        Bitmap front = loadBitmap(width, height, index * 2);
                        Bitmap back = loadBitmap(width, height, index * 2 + 1);
                        page.setTexture(front, CurlPage.SIDE_FRONT);
                        page.setTexture(back, CurlPage.SIDE_BACK);
                    }
                } else {
                    Bitmap front = loadBitmap(width, height, index);
                    page.setTexture(front, CurlPage.SIDE_BOTH);
                    page.setColor(ContextCompat.getColor(CurlActivity.this, R.color.page_back_color_with_alpha), CurlPage.SIDE_BACK);
                }

            }
            catch(Exception ex)
            {
            }

        }


    }


    /**
     * CurlView size changed observer.
     */
    private class SizeChangedObserver implements CurlView.SizeChangedObserver {
        @Override
        public void onSizeChanged(int w, int h) {

            if (w > h) {
                float marginH = 0f;
                float marginV = 0f;

                if (page_height_in_pixels > page_width_in_pixels) {
                    float scale = (float) h / page_height_in_pixels;
                    float width = (page_width_in_pixels * 2f) * scale;
                    float pagePerc = (width / (float) w) * 100;
                    float marginPerc = 100f - pagePerc;
                    marginH = (marginPerc / 100f) / 2f;
                } else {
                    float scale = (float) w / (page_width_in_pixels * 2);
                    float height = page_height_in_pixels * scale;
                    float pagePerc = (height / (float) h) * 100;
                    float marginPerc = 100f - pagePerc;
                    marginV = (marginPerc / 100f) / 2f;
                }

                mCurlView.setViewMode(CurlView.SHOW_TWO_PAGES);
                mCurlView.setMargins(marginH, marginV, marginH, marginV);

            } else {
                float marginH = 0f;
                float marginV = 0f;
                float scale = (float) w / page_width_in_pixels;
                float height = page_height_in_pixels * scale;
                float pagePerc = (height / (float) h) * 100;
                float marginPerc = 100f - pagePerc;
                marginV = (marginPerc / 100f) / 2f;

                mCurlView.setViewMode(CurlView.SHOW_ONE_PAGE);
                mCurlView.setMargins(marginH, marginV, marginH, marginV);
            }
        }
    }

    public static float scale(final float valueIn, final float baseMin, final float baseMax, final float limitMin, final float limitMax) {
        return ((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin;
    }

    private void gotoPage() {
        AlertDialog.Builder b = new Builder(this);
        b.setTitle(getResources().getString(R.string.menu_title_go_to_page));
        final int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            pages = new String[pageProvider.getPageCount()];

            for (int i = 0; i < pages.length; i++) {
                pages[i] = String.valueOf(i + 1);
            }

        } else {
            int even = pageProvider.isEven() ? 1 : 0;
            pages = new String[(int) Math.ceil(pageProvider.getPageCount() + even)];

            for (int i = 0; i < pages.length; i++) {

                if (i == 0) {
                    pages[i] = "1";
                } else if (i == pages.length - 1) {
                    if (pageProvider.isEven()) {
                        pages[i] = String.valueOf(pageProvider.getPageCount() * 2);
                    } else {
                        pages[i] = String.valueOf(pageProvider.getPageCount() * 2 - 2) + "/" + String.valueOf(pageProvider.getPageCount() * 2 - 1);
                    }
                } else {
                    int index = i;
                    pages[i] = String.valueOf(index * 2) + "/" + String.valueOf(index * 2 + 1);
                }

            }
        }


        b.setItems(pages, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                String selected = pages[which];
                int index;

                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    index = Integer.parseInt(selected) - 1;
                } else {
                    index = which;
                }
                mCurlView.panZoomManager.reset();
                mCurlView.setCurrentIndex(index);

            }

        });
        gotoDialog = b.create();
        gotoDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;

            case R.id.pdf_reset_zoom:
                mCurlView.panZoomManager.reset();
                break;
            /*case MENU_ID_LOCK_ORIENTATION:

                boolean checked = item.isChecked();
                item.setChecked(!checked);

                int nextOrientation;
                if (item.isChecked()) {
                    int orientation = getResources().getConfiguration().orientation;

                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        nextOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    } else {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        nextOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    }

                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    nextOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

                }


                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(KEY_ORIENTATION_LOCK, nextOrientation);
                editor.apply();

                break;*/
            case R.id.pdf_go_to:
                gotoPage();
                break;

            case R.id.pdf_next:
                mCurlView.panZoomManager.reset();
                mCurlView.nextPage();
                break;

            case R.id.pdf_pre:
                mCurlView.panZoomManager.reset();
                mCurlView.prevPage();
                break;

            /*case R.id.pdf_view_all:
                finish();
                Intent intent = new Intent(this, AllPagesActivity.class);
                intent.putExtra(AllPagesActivity.KEY_SELECTED_PAGE, mCurlView.getCurrentIndex());
                startActivity(intent);
                break;*/

            /*case MENU_ID_SAVE_PDF:
                if (verifyStoragePermissions(this)) {
                    savePDF();
                }

                break;

            case MENU_ID_ABOUT:
                showAboutDialog();*/
        }
        /*if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }*/


        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case MENU_ID_RESET_ZOOM:
                mCurlView.panZoomManager.reset();
                break;
            case MENU_ID_LOCK_ORIENTATION:

                boolean checked = item.isChecked();
                item.setChecked(!checked);

                int nextOrientation;
                if (item.isChecked()) {
                    int orientation = getResources().getConfiguration().orientation;

                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        nextOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    } else {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        nextOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    }

                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    nextOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

                }


                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(KEY_ORIENTATION_LOCK, nextOrientation);
                editor.apply();

                break;
            case MENU_ID_GO_TO_PAGE:
                gotoPage();
                break;

            case MENU_ID_NEXT_PAGE:
                mCurlView.panZoomManager.reset();
                mCurlView.nextPage();
                break;

            case MENU_ID_PREV_PAGE:
                mCurlView.panZoomManager.reset();
                mCurlView.prevPage();
                break;

            case MENU_ID_VIEW_ALL_PAGES:
                finish();
                Intent intent = new Intent(this, AllPagesActivity.class);
                intent.putExtra(AllPagesActivity.KEY_SELECTED_PAGE, mCurlView.getCurrentIndex());
                startActivity(intent);
                break;

            case MENU_ID_SAVE_PDF:
                if (verifyStoragePermissions(this)) {
                    savePDF();
                }

                break;

            case MENU_ID_ABOUT:
                showAboutDialog();
        }


        return true;
    }*/


    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }


    private void savePDF() {
        try {
            String pdfName = pdf;
            if (pdfName == null) {
                Toast.makeText(this, getResources().getString(R.string.pdf_file_not_exists), Toast.LENGTH_LONG).show();
                return;
            }
            byte[] pdfBytes = readPDF(pdfName);
            final Uri savedFileUri = saveData(pdfBytes, pdfName);


            if (savedFileUri != null) {
                Toast.makeText(this, getResources().getString(R.string.pdf_saved_successfully), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static boolean verifyStoragePermissions(Activity activity) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }


        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        return permission == PackageManager.PERMISSION_GRANTED;
    }


    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            savePDF();
        }
    }


    private String getPdfPath() throws IOException {
        String[] list = getAssets().list("pdf");
        if (list.length > 0)
            return list[0];
        return null;
    }


    private byte[] readPDF(String fileName) throws IOException {
        InputStream is = getAssets().open("pdf/" + fileName);
        byte[] bytes = new byte[is.available()];
        is.read(bytes);
        is.close();
        return bytes;
    }


    private Uri saveData(byte[] data, String fileName) throws IOException {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, fileName);

        FileOutputStream stream = new FileOutputStream(file, true);
        stream.write(data);
        stream.close();
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        sendBroadcast(intent);
        return uri;
    }


    private void showAboutDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);


        dialogBuilder.setMessage(Html.fromHtml("<p>1. Content: Images from <a href='http://www.pixabay.com'>pixabay.com</a> in public domain.</p>" +
                "<p>2. The Flipbook app is a modified enhanced version of Harri Sm�tt's <a href='https://github.com/harism/android_page_curl'>Page Curl app</a> " +
                "with <a href='http://www.apache.org/licenses/LICENSE-2.0'>Apache License</a>.</p>"));

        aboutDialog = dialogBuilder.create();
        aboutDialog.show();
        ((TextView) aboutDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }


    @Override
    public void pageChanged(final int curPage) {

        final int orientation = getResources().getConfiguration().orientation;


        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String title = "";


                String pageLabel = getString(R.string.page_label);


                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    if (curPage != pageProvider.getPageCount())
                        title = MessageFormat.format(pageLabel, (curPage + 1), pageProvider.getPageCount());

                } else {
                    int realCount = pageProvider.getPageCount() * 2;

                    if (!pageProvider.isEven())
                        realCount--;

                    if (curPage == 0) {
                        title = MessageFormat.format(pageLabel, (curPage + 1), realCount);
                    } else if (curPage == pageProvider.getPageCount()) {
                        if (pageProvider.isEven()) {
                            title = MessageFormat.format(pageLabel, realCount, realCount);
                        } else {
                            title = "";
                        }
                    } else {
                        title = MessageFormat.format(pageLabel, (curPage * 2) + "/" + (curPage * 2 + 1), realCount);
                    }


                }
                toolbar.setSubtitle(title);
            }
        });


    }
}


