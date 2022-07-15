package school.campusconnect.utils.address;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Utils;

public class FindAddress extends AsyncTask<Void, Void, Void> implements PlacesClient {
    private static final String TAG = "FindAddress";
    double latitude, longitude;
    String sublocality = "";
    String locality = "";
    Address addressFromCatch;
    Address address;
    Context context;
    FindAddressListener listener;
    private List<Address> addressList;

    ///TEST COMMIT

    public FindAddress(double latitude, double longitude, Context context, FindAddressListener listener) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        addressList = null;
        try {
            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
            addressList = geocoder.getFromLocation(latitude, longitude, 5);

            if (addressList != null && addressList.size() > 0) {
                AppLog.e("ADDRESS", "addressList.size() > 0 " + addressList.toString());

                address = addressList.get(0);
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            AppLog.e(TAG , "Exception Occured : "+e.getLocalizedMessage());

         /*   Geocoder geocoder;

            geocoder = new Geocoder(context, Locale.getDefault());

            try {
                addressList = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException ioException) {
                ioException.printStackTrace();
                AppLog.e(TAG, "error is " + ioException.toString());
            }*/

         /*   if (!Places.isInitialized()) {
                Places.initialize(context, String.valueOf(R.string.mapApi), Locale.ENGLISH);
            }
            else
            {

            }*/



        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (address != null) {

            AppLog.e(TAG, "Address :: " + address.getAddressLine(0));

            String result = address.getAddressLine(0);

            listener.onLocationDetect(address);
        } else {
            AppLog.e(TAG, "Address :: " + null);
            listener.onLocationDetect(address);
        }


//        if(!result.equalsIgnoreCase(","))
//            listener=null;

    }

    @NonNull
    @Override
    public Task<FindAutocompletePredictionsResponse> findAutocompletePredictions(@NonNull FindAutocompletePredictionsRequest findAutocompletePredictionsRequest) {
        return null;
    }

    @NonNull
    @Override
    public Task<FetchPhotoResponse> fetchPhoto(@NonNull FetchPhotoRequest fetchPhotoRequest) {
        return null;
    }

    @NonNull
    @Override
    public Task<FetchPlaceResponse> fetchPlace(@NonNull FetchPlaceRequest fetchPlaceRequest) {
        return null;
    }

    @NonNull
    @Override
    public Task<FindCurrentPlaceResponse> findCurrentPlace(@NonNull FindCurrentPlaceRequest findCurrentPlaceRequest) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        return null;
    }

    public interface FindAddressListener
    {
        void locationUpdate(Location location);

        public void onLocationDetect(Address locality);
    }

  /*  public Address getAddress(double lat, double lng) {


        String address = "";
        Address newAddress=null;

        newAddress = new Address(Locale.getDefault());

        Log.e("getADD", "lat " + lat);
        Log.e("getADD", "lng " + lng);

        try {

            JSONObject jsonObj = getJSONfromURL("https://trustserv.org/webservice_v5/v1/getAddressByLatLong/" + lat + ","
                    + lng);
            AppLog.e("Response :: ",jsonObj.toString());
            String Status = jsonObj.getString("status");
            if (Status.equalsIgnoreCase("OK")) {
                JSONArray Results = jsonObj.getJSONArray("results");
                JSONObject zero = Results.getJSONObject(0);
                JSONArray address_components = zero.getJSONArray("address_components");
                address = zero.getString("formatted_address");

                AppLog.e(TAG,address);

                // newAddress = new Gson().fromJson(address,Address.class);

                if (address != null && !address.equals("")) {
                    AppLog.e("getADD", "MainAddress is " + address);

                    newAddress.setAddressLine(0,address);
                    newAddress.setLatitude(lat);
                    newAddress.setLongitude(lng);


                    for (int i = 0; i < address_components.length(); i++) {
                        try {
                            JSONObject zero2 = address_components.getJSONObject(i);
                            String long_name = zero2.getString("long_name");
                            JSONArray mtypes = zero2.getJSONArray("types");

                            String Type = mtypes.getString(0);

                            if (Type.equalsIgnoreCase("administrative_area_level_1")) {
                                sublocality = long_name;
                                newAddress.setAdminArea(long_name);
                                AppLog.e(TAG,"Admin :"+newAddress.getAdminArea());
                            }



                            if (Type.equalsIgnoreCase("sublocality")) {
                                sublocality = long_name;

                            }

                            if (Type.equalsIgnoreCase("locality")) {
                                locality = long_name;
                                newAddress.setLocality(locality);
                            }

                            if (Type.equalsIgnoreCase("postal_code")) {
                                String postal = long_name;
                                newAddress.setPostalCode(postal);
                            }

                            if (Type.equalsIgnoreCase("country")) {
                                String country = long_name;
                                newAddress.setCountryName(country);
                            }

                        } catch (Exception e) {

                            AppLog.e(TAG,"Error: "+e.getLocalizedMessage());
                            e.printStackTrace();


                        }
                    }
                    return newAddress;
                }

             //   return newAddress;
               *//* address = nullCheck(strAddress1, 0) + nullCheck(strAddress2, 0) + nullCheck(strCity, 0)
                        + nullCheck(strState, 0) + nullCheck(strCountry, 0) + nullCheck(strCounty, 0) + nullCheck(strPIN, 1);

                if (!isNumeric(address.substring(address.length() - 2))) {
                    address = address.substring(0, address.length() - 2);
                }*//*

            }else{
                return newAddress;
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppLog.e("getADD", "error is " + e.toString());
        }


        return newAddress;

    }*/

    public JSONObject getJSONfromURL(String urlString) {

        // initialize
        InputStream is = null;
        String result = "";
        JSONObject jObject = null;

        // http post
        URL url;
        HttpURLConnection urlConnection = null;
        //JSONArray response = new JSONArray();
        Log.e("URL :: ",urlString);
        try {
            url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                result = Utils.readStream(urlConnection.getInputStream());
                Log.v("CatalogClient", result);
                //response = new JSONArray(responseString);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // convert response to string
            /*try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                result = sb.toString();
            } catch (Exception e) {
                printLog("log_tag", "Error converting result " + e.toString());
            }*/

        // try parse the string to a JSON object
        try {
            jObject = new JSONObject(result);
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        return jObject;
    }
}
