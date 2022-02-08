package school.campusconnect.utils.address;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import static com.activeandroid.Cache.getContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;


import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import school.campusconnect.R;
import school.campusconnect.databinding.FragmentSelectAddressBinding;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;

/**
 * Created by frenzin07 on 9/14/2018.
 */

public class SelectAddressFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationInterface, FindAddress.FindAddressListener, View.OnClickListener {

    String TAG = "SelectAddressFragment";
    private static final int LOCATION_PERMISSION = 1;
    private static final int GOOGLE_API_CLIENT_ID = 121;

    private static final int AUTOCOMPLETE_REQUEST_CODE = 11;

    FragmentSelectAddressBinding binding;
    SupportMapFragment mapFragment;
    GoogleMap map;
    String postalCode, lat, lng;

    String filter = "all";

    HashMap<String, Integer> mapMarker = new HashMap<>();
    DecimalFormat df = new DecimalFormat("##.####");
    boolean hasLocation;


    PlaceArrayAdapter mPlaceArrayAdapter;
    GoogleApiClient mGoogleApiClient;

    FindAddress findAddress;
    Address selAddress;

    private Address add;

    GetLocation getLocation;

    Location location;
    Double defaultLat = 12.9716, defaultLng = 77.5946;

    boolean isFromPreview;
    boolean isFromRegister;

    Button btnSubmit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_select_address, container, false);

        getLocation = new GetLocation(getActivity(), this);
        getLocation.requestLocation();
        init(binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public void onResume() {

        super.onResume();
      //  ((Base) getActivity()).setToolBar(getString(R.string.title_selectaddress));

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .enableAutoManage(getActivity(), GOOGLE_API_CLIENT_ID, this)
                    .addConnectionCallbacks(this)
                    .build();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }


    private void init(View view) {


        binding.etSearch.setOnClickListener(this);
        binding.etAddress.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);


        if (getArguments() != null) {

            isFromPreview = getArguments().getBoolean("frompreview", false);

            filter = getArguments().getString("filter", "all");
            Bundle bundle = getArguments();
            binding.etAddress.setText(bundle.getString(Constants.KEY_BUNDLE_ADDRESS));
            binding.etLandmark.setText(bundle.getString(Constants.KEY_BUNDLE_LOCALITY));
            binding.etPinCode.setText(bundle.getString(Constants.KEY_BUNDLE_POSTAL));
           // edtFloor.setText(bundle.getString(Constants.KEYS.KEY_BUNDLE_FLOOR));
              /*  if(!isFromRegister)
                edtPostalCode.setText(bundle.getString(Constants.KEYS.KEY_BUNDLE_POSTAL));*/
               /* else
                {
                    edtPostalCode.setVisibility(View.GONE);
                }*/

            if (bundle.getParcelable(Constants.KEY_BUNDLE_LOCATION) != null) {
                Address address = bundle.getParcelable(Constants.KEY_BUNDLE_LOCATION);
                if (address != null) {
                    try {
                        defaultLat = address.getLatitude();
                        defaultLng = address.getLongitude();
                    } catch (Exception ex) {
                        defaultLat = 23.445345;
                        defaultLng = 72.34234;
                    }
                } else {
                    defaultLat = 23.445345;
                    defaultLng = 72.34234;
                }
            }

            Log.e(TAG, "Select Address : " + defaultLat + " , " + defaultLng);

        }

        //edtSearch.addTextChangedListener(new TextChange());

        if (isFromPreview) {
            // edtPostalCode.setVisibility(View.GONE);
          //  edtFloor.setVisibility(View.GONE);
        }

        getGPS();

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // setPlaceHolder();

        //geocoder = new Geocoder(getActivity(), Locale.getDefault());


    }

/*    private void setPlaceHolder() {


//        String bound[] =Constants.BOUND_BOTTOM_RIGHT.split(",");
//        String bound2[] =Constants.BOUND_TOP_LEFT.split(",");
//
//        LatLngBounds BOUNDS_INDIA = new LatLngBounds
//                (new LatLng(Double.parseDouble(bound[0]), Double.parseDouble(bound[1])),
//                        new LatLng(Double.parseDouble(bound2[0]), Double.parseDouble(bound2[1])));


        mPlaceArrayAdapter = new PlaceArrayAdapter(getContext(), android.R.layout.simple_dropdown_item_1line,
                null, null);
        edtSearch.setAdapter(mPlaceArrayAdapter);


        final ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
                = new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(PlaceBuffer places) {
                if (!places.getStatus().isSuccess()) {
                    Log.e(TAG, "Place query did not complete. Error: " +
                            places.getStatus().toString());
                    return;
                }
                // Selecting the first object buffer.
                final Place place = places.get(0);

                LatLng latLng = place.getLatLng();

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));


                AppLog.e(TAG, place.getLatLng().toString() + "");


                CharSequence attributions = places.getAttributions();

            }
        };

        edtSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(i);


                final String placeId = String.valueOf(item.placeId);
                AppLog.e("AddQuestion: ", "Selected: " + item.description);
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);


                AppLog.e("AddQuestion", "Fetching details for ID: " + item.placeId);

                hideKeyboard();
            }
        });


    }*/


    public void getGPS() {
        int hasPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasPermission == PackageManager.PERMISSION_GRANTED) {
            getLocation = new GetLocation(getActivity(), this);
            getLocation.requestLocation();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        }

    }
    public boolean isValidField(EditText editText) {
        String text = editText.getText().toString().trim();

        if (TextUtils.isEmpty(text)) {
            editText.setError("Require");
            editText.requestFocus();
            return false;
        }
        editText.setError(null);
        return true;
    }

    public boolean isValidField(EditText editText, TextView tvMsg, String msg) {
        String text = editText.getText().toString().trim();

        if (TextUtils.isEmpty(text)) {
            tvMsg.setText(msg);
            editText.requestFocus();
            return false;
        }
        return true;
    }


    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnSubmit:

                //getAddress();

                if (selAddress != null && isValidField(binding.etAddress) && isValidField(binding.etLandmark) && isValidField(binding.etPinCode))
                    sendAddress(selAddress);

                break;


            case R.id.etSearch: {
                Log.e(TAG,"editSearch");
                initAutoComplet();
                break;
            }

        }
    }

    private void initAutoComplet() {
        Log.e(TAG,"initAuto_Complete_CAll");
        Places.initialize(getActivity().getApplicationContext(), getString(R.string.mapApi));

        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .setCountry("IN")
                .build(getActivity());
        try {
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        } catch (Exception ex) {
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        map.setOnMarkerClickListener(this);

        map.setOnCameraMoveListener(this);

        map.setOnCameraIdleListener(this);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
        } else
            map.setMyLocationEnabled(true);

        if (location == null)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(defaultLat, defaultLng), 17f));


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions

            return;
        }
        map.setMyLocationEnabled(true);
    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case LOCATION_PERMISSION: {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "GPS PERMISSION GRANTED");
                    hasLocation = true;
                    getGPS();
                } else {
                    hasLocation = false;
                }

                break;
            }


        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "OnActivity Result Called" + requestCode);

        switch (requestCode) {

            case AUTOCOMPLETE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    AppLog.e(TAG, "Place: " + place.getName() + ", " + place.getId());
                    Location location = new Location("");
                    location.setLatitude(place.getLatLng().latitude);
                    location.setLongitude(place.getLatLng().longitude);
                    locationUpdate(location);
                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    // TODO: Handle the error.
                    Status status = Autocomplete.getStatusFromIntent(data);
                    AppLog.e(TAG, status.getStatusMessage());
                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;

        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onCameraIdle() {


        Log.e(TAG, "OnCamer Idel Called");

        LatLng loc = map.getCameraPosition().target;

        findAddress = new FindAddress(loc.latitude, loc.longitude, getContext(), this);
        findAddress.execute();

        binding.progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());
    }

    @Override
    public void onConnected(Bundle bundle) {
//        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);

        Log.e("Google Client Connected", "called");

    }

    @Override
    public void onConnectionSuspended(int i) {
        // mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e("Connection Suspended", "called");
    }

    @SuppressLint("MissingPermission")
    @Override
    public void locationUpdate(Location location) {
        Log.e(TAG, "locationUpdate : " + location);
        try {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17f));

            map.setMyLocationEnabled(true);
        } catch (Exception ex) {

        }
        this.location = location;

    }

    @Override
    public void startLocationTimer() {

    }

    @Override
    public void onLocationDetect(Address address) {

        binding.progressBar.setVisibility(View.GONE);

        if (address != null) {

            selAddress = address;
            // edtSearch.setText(selAddress.getAddressLine(0));
            binding.etAddress.setText(selAddress.getAddressLine(0));
            binding.etLandmark.setText(selAddress.getSubAdminArea());
            binding.etPinCode.setText(selAddress.getPostalCode());
            binding.etSearch.clearFocus();

            getLocation.stopFusedLocationUpdates();
            postalCode = address.getPostalCode();

            lat = String.valueOf(address.getLatitude());
            lng = String.valueOf(address.getLongitude());

            Log.e(TAG, address.getAddressLine(0) + "");
            // sendAddress(address);
        } else {
            Log.e(TAG, "Address Null");
            Toast.makeText(getContext(),"Address Not found",Toast.LENGTH_SHORT).show();
        }
    }

    private void sendAddress(Address address) {// address object used to store lat lang , and also contain visible address line
        binding.progressBar.setVisibility(View.GONE);

        Log.e(TAG,"address"+address);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.KEY_BUNDLE_LOCATION, address);
        bundle.putString(Constants.KEY_BUNDLE_ADDRESS, binding.etAddress.getText().toString());
        bundle.putString(Constants.KEY_BUNDLE_LOCALITY,binding.etLandmark.getText().toString());
       // bundle.putString(Constants.KEYS.KEY_BUNDLE_FLOOR, edtFloor.getText().toString());
        bundle.putString(Constants.KEY_BUNDLE_POSTAL, binding.etPinCode.getText().toString());
        bundle.putString(Constants.KEY_BUNDLE_LATITUDE, lat);
        bundle.putString(Constants.KEY_BUNDLE_LONGITUDE, lng);

        Intent i = new Intent(getContext(), AddressActivity.class);
        i.putExtras(bundle);
        getActivity().setResult(RESULT_OK, i);
        getActivity().finish();

    }

    private class TextChange implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
           /* if(charSequence.length()==0){
                imgClearSearch.setVisibility(View.GONE);
            }else{
                imgClearSearch.setVisibility(View.VISIBLE);
            }*/
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}