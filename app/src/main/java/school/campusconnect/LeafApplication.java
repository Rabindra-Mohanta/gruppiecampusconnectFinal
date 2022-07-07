package school.campusconnect;

import android.app.Application;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.multidex.MultiDex;
import androidx.appcompat.app.AppCompatDelegate;

import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import school.campusconnect.utils.AppLog;

import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.instacart.library.truetime.TrueTime;
import com.instacart.library.truetime.TrueTimeRx;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import school.campusconnect.network.LeafApiClient;
import school.campusconnect.utils.TypeFaceUtil;


public class LeafApplication extends Application  {

    private static final String TAG = "LeafApplication";
    private LeafApiClient apiClient;
    public static final String IMAGE_DIRECTORY_NAME = "school";

    private static LeafApplication sIntance;
    ArrayList<String> shareFileList;
    String type = "";
    public ArrayList<String> getShareFileList() {
        return shareFileList;
    }

    public String getType() {
        return type;
    }

    public void setShareFileList(ArrayList<String> shareFileList, String type) {
        this.shareFileList = shareFileList;
        this.type = type;
    }

    @Override
    public void onCreate() {
        ActivityLifecycleCallback.register(this);
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        RxJavaPlugins.setErrorHandler(e -> {
            if (e instanceof UndeliverableException) {
                e = e.getCause();
            }
            else
            {
                Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread() , e);
            }

        });
        sIntance = this;
        MultiDex.install(this);
      /*  Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);*/

        TypeFaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/lato_regular.ttf");

        //NOFIREBASEDATABASE
       /* FirebaseAuth mAuth = FirebaseAuth.getInstance();
        AppLog.e(TAG, "firebase logged in : +"+(mAuth.getCurrentUser()!=null));
        if(mAuth.getCurrentUser()==null)
        {
            mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())
                    {
                        AppLog.e(TAG, "isSuccessful : true");
                    }
                    else
                    {
                        AppLog.e(TAG, "isSuccessful : false");
                    }
                }
            });
        }*/


/*

        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        //AppLog.e(TAG+": ","zoom initialize, "+"appkey: "+appPref.getString(AppPref.ZOOM_KEY)+" ,APP_SECRET: "+appPref.getString(AppPref.ZOOM_SECRET));


        //zoomSDK.initialize(this, APP_KEY, APP_SECRET, this);
        zoomSDK.initialize(this, APP_KEY , APP_SECRET ,  this);///APP_KEY , APP_SECRET
        zoomSDK.addAuthenticationListener(new ZoomSDKAuthenticationListener() {
            @Override
            public void onZoomSDKLoginResult(long result) {

            }

            @Override
            public void onZoomSDKLogoutResult(long result) {

            }

            @Override
            public void onZoomIdentityExpired() {

            }

            @Override
            public void onZoomAuthIdentityExpired() {

            }
        });
*/

        TrueTimeRx.build()
                .initializeRx("time.google.com")
                .subscribeOn(Schedulers.io())
                .subscribe(date -> {
                    Log.v(TAG, "TrueTime was initialized and we have a time: " + date);
                }, throwable -> {
                    throwable.printStackTrace();
                });

    }

    public static LeafApplication getInstance() {
        AppLog.e(TAG,"getInstance()");
        return sIntance;
    }




    public synchronized LeafApiClient getApiClient() {
        AppLog.e(TAG,"getApiClient()");
//        if (apiClient == null) {
            apiClient = LeafApiClient.getInstance(getApplicationContext());
//        }
        return apiClient;
    }

    public synchronized LeafApiClient getApiClientYoutube() {
       AppLog.e("YOTU", "getApiClientYoutube");
//        if (apiClient == null) {
        apiClient = LeafApiClient.getInstanceYoutube(getApplicationContext());
//        } else
        return apiClient;
    }
/*
    @Override
    public void onZoomSDKInitializeResult(int i, int i1) {

    }

    @Override
    public void onZoomAuthIdentityExpired() {

    }*/

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void isSizeAvailable()
    {
        long NUM_BYTES_NEEDED_FOR_MY_APP = 1024 * 1024 * 10L;
        StorageManager storageManager =  getApplicationContext().getSystemService(StorageManager.class);
        UUID appSpecificInternalDirUuid = null;
        UUID appSpecificInternalDirUuidFileDir = null;
        UUID appSpecificInternalDirUuidExternalDir = null;

        try {
            appSpecificInternalDirUuid = storageManager.getUuidForPath(getCacheDir());

            appSpecificInternalDirUuidFileDir = storageManager.getUuidForPath(getFilesDir());

            appSpecificInternalDirUuidExternalDir = storageManager.getUuidForPath(getExternalFilesDir(null));

        } catch (IOException e) {
            e.printStackTrace();
        }
        long availableBytes = 0;
        long availableBytesFileDir = 0;
        long availableBytesExternalDir = 0;

        try {
            availableBytes = storageManager.getAllocatableBytes(appSpecificInternalDirUuid);
            availableBytesFileDir = storageManager.getAllocatableBytes(appSpecificInternalDirUuidFileDir);
            availableBytesExternalDir = storageManager.getAllocatableBytes(appSpecificInternalDirUuidExternalDir);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e(TAG,"availableBytes"+availableBytes);
        Log.e(TAG,"availableBytesFileDir"+availableBytesFileDir);
        Log.e(TAG,"availableBytesExternalDir"+availableBytesExternalDir);

        if (availableBytes >= NUM_BYTES_NEEDED_FOR_MY_APP)
        {
            try {
                storageManager.allocateBytes(appSpecificInternalDirUuid, NUM_BYTES_NEEDED_FOR_MY_APP);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            // To request that the user remove all app cache files instead, set    // "action" to ACTION_CLEAR_APP_CACHE.    Intent storageIntent = new Intent();    storageIntent.setAction(ACTION_MANAGE_STORAGE);
          }
    }
    public File AppFilesPath(){
        File mainFolder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mainFolder  = new File(getFilesDir(), LeafApplication.getInstance().getResources().getString(R.string.app_name));
        }
        else
        {
            mainFolder = new File(getFilesDir(), LeafApplication.getInstance().getResources().getString(R.string.app_name));
        }

        if (!mainFolder.exists()) {
            mainFolder.mkdir();
        }
        return mainFolder;
    }

    public File AppFilesAudioPath(){
        File mainFolder = new File( getFilesDir(),
                LeafApplication.getInstance().getResources().getString(R.string.app_name));
        if (!mainFolder.exists()) {
            mainFolder.mkdir();
        }
        return mainFolder;
    }
}
