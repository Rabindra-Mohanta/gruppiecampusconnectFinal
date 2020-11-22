package school.campusconnect;

import android.app.Application;
import androidx.multidex.MultiDex;
import androidx.appcompat.app.AppCompatDelegate;
import school.campusconnect.utils.AppLog;

import com.clevertap.android.sdk.ActivityLifecycleCallback;

import school.campusconnect.network.LeafApiClient;
import school.campusconnect.utils.TypeFaceUtil;


public class LeafApplication extends Application  {

    private static final String TAG = "LeafApplication";
    private LeafApiClient apiClient;

    private static LeafApplication sIntance;

    @Override
    public void onCreate() {
        ActivityLifecycleCallback.register(this);
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        sIntance = this;
        MultiDex.install(this);
      /*  Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);*/

        TypeFaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/lato_regular.ttf");
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
}
