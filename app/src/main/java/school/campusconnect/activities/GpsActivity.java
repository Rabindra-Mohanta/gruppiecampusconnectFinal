package school.campusconnect.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.GetLocationRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.GruppieTracker;
import school.campusconnect.utils.MixOperations;
import school.campusconnect.views.SMBDialogUtils;

public class GpsActivity extends BaseActivity implements OnMapReadyCallback {

    private static final String TAG = "GpsActivity";
    @Bind(R.id.toolbar)
    Toolbar mToolBar;

    @Bind(R.id.btnStart)
    Button btnStart;

    @Bind(R.id.btnStop)
    Button btnStop;

    @Bind(R.id.tvStartTime)
    TextView tvStartTime;

    GoogleMap gMap;

    LeafPreference leafPreference;

    Intent gpsService;
    private boolean isTeamAdmin;
    private String groupId;
    private String teamId;

    LeafManager leafManager;
    private BroadcastReceiver myReciever;

    Handler handler = new Handler();
    Runnable runnable;
    private Marker busMarker;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.title_gps_track));

        init();
    }

    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    AppLog.e(TAG,"my location :"+location.toString());
                    LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                    if(gMap!=null)
                    {
                        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(latLng,15)));
                    }
                    stopLocationUpdates();
                }
            }
        };

        myReciever = new MyReceiver();
        leafPreference = LeafPreference.getInstance(this);
        gpsService = new Intent(this, GruppieTracker.class);
        leafManager = new LeafManager();

        Bundle bundle = getIntent().getExtras();
        isTeamAdmin = bundle.getBoolean("isTeamAdmin", false);
        groupId = bundle.getString("group_id", "");
        teamId = bundle.getString("team_id", "");

        leafPreference.setString(LeafPreference.TRACK_GROUP_ID, groupId);
        leafPreference.setString(LeafPreference.TRACK_TEAM_ID, teamId);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (isTeamAdmin) {
            btnStart.setVisibility(View.VISIBLE);
            btnStop.setVisibility(View.VISIBLE);

            if (leafPreference.getBoolean("is_start")) {
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
            } else {
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
            }
        } else {
            btnStart.setVisibility(View.GONE);
            btnStop.setVisibility(View.GONE);

            getLocation();
        }


    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }



    @OnClick({R.id.btnStart, R.id.btnStop})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnStart:
                if (!isConnectionAvailable()) {
                    showNoNetworkMsg();
                    return;
                }
                if (!leafPreference.getBoolean("is_start")) {
                    if (!checkPlayServices())
                        return;

                    if (!CheckPermission())
                        return;

                    checkGpsAndStart();

                }
                break;
            case R.id.btnStop:
                if (!isConnectionAvailable()) {
                    showNoNetworkMsg();
                    return;
                }

                if (leafPreference.getBoolean("is_start")) {
                    SMBDialogUtils.showSMBDialogOKCancel(this, getResources().getString(R.string.smb_stop_gps_track), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            stopTracking();
                        }

                    });
                }

                break;
        }
    }

    private void startStacking() {
        startService(gpsService);
        leafPreference.setBoolean("is_start", true);
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);
    }

    private void stopTracking() {
        leafPreference.setBoolean("is_start", false);
        try {
            stopService(gpsService);
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
        } catch (Exception e) {
            AppLog.e(TAG, e.toString());
        }

        leafManager.endTrip(this, groupId, teamId);
    }

    /**
     * Method to verify google play services on the device
     * */
    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        1).show();
            } else {
                Toast.makeText(this, getResources().getString(R.string.toast_device_not_supported), Toast.LENGTH_LONG)
                        .show();
                //finish();
            }

            return false;
        }
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (isTeamAdmin)
            CheckPermission();
    }

    private boolean CheckPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return false;
        }
        if (gMap != null)
            gMap.setMyLocationEnabled(true);

        checkPlayServices();

        checkGpsAndgetUserLocation();
        return true;
    }

    private void checkGpsAndgetUserLocation() {
        final LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(Constants.INTERVAL);
        mLocationRequest.setFastestInterval(Constants.INTERVAL_FAST);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                AppLog.e(TAG, "onSuccess()");

            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AppLog.e(TAG, "onFailure -> " + e.toString());
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a saveDialog.
                    try {
                        // Show the saveDialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(GpsActivity.this,
                                1);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }

            }
        });

        if (ActivityCompat.checkSelfPermission(GpsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GpsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CheckPermission();
    }

    private void checkGpsAndStart() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(Constants.INTERVAL);
        mLocationRequest.setFastestInterval(Constants.INTERVAL_FAST);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                AppLog.e(TAG, "onSuccess()");
                startStacking();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AppLog.e(TAG, "onFailure -> " + e.toString());
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a saveDialog.
                    try {
                        // Show the saveDialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(GpsActivity.this,
                                1);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }

            }
        });
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        switch (apiId)
        {
            case LeafManager.API_END_TRIP:
                AppLog.e(TAG,"End Trip response success");
                break;
            case LeafManager.API_GET_LOCATION:
                GetLocationRes getLocationRes= (GetLocationRes) response;
                AppLog.e(TAG,"getLocationRes :"+new Gson().toJson(getLocationRes));

                if(getLocationRes.data!=null)
                {
                    updateLocationOnMapMarker(getLocationRes.data.getLatitude(),getLocationRes.data.getLongitude());
                    tvStartTime.setVisibility(View.VISIBLE);
                    tvStartTime.setText(MixOperations.getFormattedDate(getLocationRes.data.tripStartedAt,Constants.DATE_FORMAT));
                }
                else
                {
                    Toast.makeText(this,  getResources().getString(R.string.toast_location_data_not_available), Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        if (msg.contains("Unauthorized") || msg.contains("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        }
        else if(msg.contains("400") || msg.contains("Bad Request"))
        {
            Toast.makeText(this, getResources().getString(R.string.toast_location_data_not_available), Toast.LENGTH_SHORT).show();
            handler.removeCallbacks(runnable);
        }
        else
        {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onException(int apiId, String msg) {
        super.onException(apiId, msg);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(myReciever,new IntentFilter("gruppie_gps"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(myReciever);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        if(runnable!=null)
            handler.removeCallbacks(runnable);
    }

    private class MyReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateLocationOnMapMarker(intent.getDoubleExtra("lat",0),intent.getDoubleExtra("lng",0));
        }
    }

    private void updateLocationOnMap(double lat, double lng) {
        AppLog.e(TAG,"updateLocationOnMap : "+lat+","+lng);
        if(lat!=0 && lng!=0)
        {
            LatLng latLng=new LatLng(lat,lng);
            if(gMap!=null)
            {
                gMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(latLng,15)));
            }
        }
    }

    private void updateLocationOnMapMarker(double lat, double lng) {
        AppLog.e(TAG,"updateLocationOnMap : "+lat+","+lng);
        if(lat!=0 && lng!=0)
        {
            LatLng latLng=new LatLng(lat,lng);
            if(gMap!=null)
            {
                if(busMarker==null)
                {
                    busMarker=gMap.addMarker(new MarkerOptions().position(latLng)
                            .title("Marker in Sydney").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_bus)));
                }
                else
                {
                    busMarker.setPosition(latLng);
                }
                gMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(latLng,15)));
            }
        }
    }



    // Get Location for Users
    private void getLocation() {
        AppLog.e(TAG,"getLocation()");
        runnable=new Runnable() {
            @Override
            public void run() {
                AppLog.e(TAG,"run()");
                leafManager.getLocation(GpsActivity.this,groupId,teamId);
                handler.postDelayed(this,Constants.INTERVAL);
            }
        };
        handler.post(runnable);
    }

}
