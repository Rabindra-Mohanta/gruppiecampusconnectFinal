package school.campusconnect.utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import school.campusconnect.R;
import school.campusconnect.activities.SplashActivity;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.network.LeafManager;

public class GruppieTracker extends Service implements LeafManager.OnCommunicationListener {
    private static final String TAG = "GPsTracker";
    private static final String CHANNEL_ID = "gruppie_gps_01";
    private int notificationId = 101;
    LeafPreference leafPreference;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    LeafManager leafManager;
    private String groupId;
    private String teamId;

    public GruppieTracker() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate()");

        leafPreference = LeafPreference.getInstance(this);
        leafManager=new LeafManager();

        groupId=leafPreference.getString(LeafPreference.TRACK_GROUP_ID);
        teamId=leafPreference.getString(LeafPreference.TRACK_TEAM_ID);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        AppLog.e(TAG,"mFusedLocationClient :"+mFusedLocationClient);

        createLocationRequest();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    sendLocation(location);
                }
            }
        };
        startLocationUpdates();
    }

    private void sendLocation(Location location) {
        AppLog.e(TAG,"location : "+location);
        String msg="location : "+location.getLatitude()+","+location.getLongitude();
       // showNotification(msg);
        leafManager.startUpdateTrip(this,groupId,teamId,location.getLatitude(),location.getLongitude());

        //send broadcast to activity
        Intent intent=new Intent("gruppie_gps");
        intent.putExtra("lat",location.getLatitude());
        intent.putExtra("lng",location.getLongitude());
        sendBroadcast(intent);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.INTERVAL);
        mLocationRequest.setFastestInterval(Constants.INTERVAL_FAST);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }


    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG,"onStartCommand()");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        Log.e(TAG,"onDestroy()");
    }

    private void showNotification(String message) {

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = "notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, mBuilder.build());

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        AppLog.e(TAG,"onSuccess:"+response);
    }

    @Override
    public void onFailure(int apiId, String msg) {
        AppLog.e(TAG,"onFailure:"+msg);
    }

    @Override
    public void onException(int apiId, String msg) {
        AppLog.e(TAG,"onException:"+msg);
    }
}
