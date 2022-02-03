package school.campusconnect.utils.address;

/**
 * Created by frenzin07 on 7/9/2018.
 */


import android.content.Context;
import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import school.campusconnect.utils.AppLog;

public class PlaceArrayAdapter extends ArrayAdapter<PlaceArrayAdapter.PlaceAutocomplete> implements Filterable {
    private static final String TAG = "PlaceArrayAdapter";
    private GoogleApiClient mGoogleApiClient;
    private AutocompleteFilter mPlaceFilter;

    private LatLngBounds mBounds;
    private ArrayList<PlaceAutocomplete> mResultList;
    private ArrayList<PlaceAutocomplete> filterResult = new ArrayList<>();
    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);

    private static final
    LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(23.63936, 68.14712), new LatLng(28.20453, 97.34466));


    Context context;
    /**
     * Constructor
     *
     * @param context  Context
     * @param resource Layout resource
     * @param bounds   Used to specify the search bounds
     * @param filter   Used to specify place types
     */
    public PlaceArrayAdapter(Context context, int resource, LatLngBounds bounds,
                             AutocompleteFilter filter) {
        super(context, resource);
        mBounds = bounds;
        mPlaceFilter = filter;
        this.context=context;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        if (googleApiClient == null || !googleApiClient.isConnected()) {
            mGoogleApiClient = null;
        } else {
            mGoogleApiClient = googleApiClient;
        }
    }

    @Override
    public int getCount() {
        if(mResultList==null)
            return 0;
        else
            return mResultList.size();
    }

    @Override
    public PlaceAutocomplete getItem(int position) {
        return mResultList.get(position);
    }

    private ArrayList<PlaceAutocomplete> getPredictions(CharSequence constraint) {


        if(mGoogleApiClient==null){
            AppLog.e(TAG,"Google Api Client NULL");
        }


        if (mGoogleApiClient.isConnected()) {

            AppLog.e("PLACES_APII", "111111 " + constraint);

//           AppLog.i("", "Starting autocomplete query for: " + constraint);

            AutocompleteFilter.Builder autocompleFilter = new AutocompleteFilter.Builder()/*.setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)*/;
            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
                            .getAutocompletePredictions(mGoogleApiClient, constraint.toString(),
                                    mBounds, autocompleFilter.build());


            // Submit the query to the autocomplete API and retrieve a PendingResult that will
            // contain the results when the query completes.
            /*PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
                            .getAutocompletePredictions(mGoogleApiClient, constraint.toString(),
                                    mBounds, null);*/
            AppLog.e("PLACES_APII", "2");

            // This method should have been called off the main UI thread. Block and wait for at most 60s
            // for a result from the API.
            AutocompletePredictionBuffer autocompletePredictions = results
                    .await(60, TimeUnit.SECONDS);
            AppLog.e("PLACES_APII", "3");

            // Confirm that the query completed successfully, otherwise return null
            final Status status = autocompletePredictions.getStatus();
              AppLog.e("PLACES_APII", "4 " + status);

            if (!status.isSuccess()) {

                autocompletePredictions.release();
                return null;
            }
            AppLog.e("PLACES_APII", "6 Query completed. Received " + autocompletePredictions.getCount() +
                    " predictions.");

            AppLog.i("", "Query completed. Received " + autocompletePredictions.getCount()
                    + " predictions.");

            // Copy the results into our own data structure, because we can't hold onto the buffer.
            // AutocompletePrediction objects encapsulate the API response (place ID and description).

            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
            ArrayList resultList = new ArrayList<>(autocompletePredictions.getCount());
            // AppLog.e("PLACES_APII", "7");

            filterResult.clear();

            while (iterator.hasNext()) {
                AppLog.e("PLACES_APII", "hasNext");
                AutocompletePrediction prediction = iterator.next();
                // Get the details of this prediction and copy it into a new PlaceAutocomplete object.

                AppLog.e("TEST_PREDICTION", "FullText is " + prediction.getFullText(STYLE_BOLD));

                if (true)
                {
                    String splitAdd[]=prediction.getFullText(STYLE_BOLD).toString().split(",");
                    if(splitAdd.length>=4)
                    {
                        // if(!((AddQuestionActivity)mContext).blockAddress.contains(prediction.getFullText(STYLE_BOLD).toString()))
                        // {
                        resultList.add(new PlaceAutocomplete(prediction.getPlaceId(),prediction.getFullText(null)));
                        //}

                    }

                }

            }
            // AppLog.e("PLACES_APII", "8");
            // Release the buffer now that all data has been copied.
            autocompletePredictions.release();
            //AppLog.e("PLACES_APII", "9");
            return resultList;
        }

        Log.e(TAG, "Google API client is not connected.");
        return null;
    }

    @Override
    public Filter getFilter() {
        final Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null) {
                    // Query the autocomplete API for the entered constraint
                    mResultList = getPredictions(constraint);
                   // String userCity = SessionManager.getInstance(context).getString(Constants.USER_CITY);
                    //log.e("UserCity",userCity+" ");

                    if (mResultList != null) {
                        // Results
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    public class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence description;

        PlaceAutocomplete(CharSequence placeId, CharSequence description) {
            this.placeId = placeId;
            this.description = description;
        }

        @Override
        public String toString() {
            return description.toString();
        }
    }
}
