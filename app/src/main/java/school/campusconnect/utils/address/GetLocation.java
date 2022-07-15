package school.campusconnect.utils.address;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by frenzin07 on 9/26/2018.
 */

public class GetLocation {

    private static final int REQUEST_CHECK_SETTINGS = 129;
    private static final int GPS_PERMISSION = 124;
    String TAG = "GetLocation";
    Activity activity;
    FusedLocationProviderClient locationProviderClient;
    LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    Location location;
    LocationInterface listener;
    LocationManager mLocationManager;


    public GetLocation(final Activity activity, final LocationInterface locationInterface) {
        this.activity = activity;
        this.listener = locationInterface;

        mLocationManager = (LocationManager) activity
                .getSystemService(LOCATION_SERVICE);

        createLocationRequest();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.e(TAG, "OnLocationResultCalled : " + locationResult);
                if (locationResult == null) {
                    Log.e(TAG, "Location Null");
                    listener.locationUpdate(location);
                    return;
                }

                Log.e(TAG, "Location found: " + locationResult.getLastLocation().getLatitude() + "," + locationResult.getLastLocation().getLongitude());

                location = locationResult.getLastLocation();
                listener.locationUpdate(location);
                Log.e(TAG, "Location: " + location.getLongitude() + "," + location.getLatitude());

            }
        };

        locationProviderClient = LocationServices.getFusedLocationProviderClient(activity);

    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {

        return location;
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        //mLocationRequest.setNumUpdates(1);
        //mLocationRequest.setFastestInterval(100);
        mLocationRequest.setMaxWaitTime(3000);
        mLocationRequest.setSmallestDisplacement(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    public void requestLocation() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                Log.e(TAG, "OnSucessCalled");

                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                locationProviderClient.getLastLocation().addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null)
                            startLocationUpdates();
                        else {
                            Log.e(TAG, "Fused Last Location : " + location);
                            listener.locationUpdate(location);
                            //startLocationUpdates();
                        }
                    }

                });

            }
        });

        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().

                        Log.e(TAG, "Current Activity: " + activity.getClass().getSimpleName());

                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(activity,
                                REQUEST_CHECK_SETTINGS);

                    } catch (IntentSender.SendIntentException sendEx) {

                        Log.e(TAG, "Gps denied");
                        // Ignore the error.
                    }
                }
            }
        });
       /* if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }*/
    }


    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        Log.e(TAG, "startLocationUpdates called");

        listener.startLocationTimer();

        locationProviderClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                Looper.myLooper());

        getLocationThroughNetwork();
    }

    public void stopFusedLocationUpdates() {
        locationProviderClient.removeLocationUpdates(mLocationCallback);

    }

    public void stopNetworkLocationUpdates() {
        mLocationManager.removeUpdates((LocationListener) activity);
    }


    public void getLocationThroughNetwork() {
        try {

            //GETTING GPS LCOATION......
            boolean isGPSEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // First get location from Network Provider
            if (isGPSEnabled) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    listener.locationUpdate(null);
                    return;
                }


                if (mLocationManager != null) {
                    Location location = mLocationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        Log.e(TAG, "GPS Location Last Location : " + location);
                        listener.locationUpdate(location);
                    } else {
                        Location location2 = mLocationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location2 != null) {
                            Log.e(TAG, "Network Location Last Location : " + location);
                            listener.locationUpdate(location2);
                        } else {
                            mLocationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    0,
                                    0, (LocationListener) activity);
                        }
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }


}
