package biz.zenpets.users.kennels;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.TypefaceSpan;
import biz.zenpets.users.utils.adapters.kennels.KennelsAdapter;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.helpers.location.classes.FetchCityID;
import biz.zenpets.users.utils.helpers.location.interfaces.FetchCityIDInterface;
import biz.zenpets.users.utils.models.kennels.kennels.Kennel;
import biz.zenpets.users.utils.models.kennels.kennels.Kennels;
import biz.zenpets.users.utils.models.kennels.kennels.KennelsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewKennelsList extends AppCompatActivity
        implements FetchCityIDInterface, SearchView.OnQueryTextListener {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int ACCESS_FINE_LOCATION_CONSTANT = 200;

    /** A FUSED LOCATION PROVIDER CLIENT INSTANCE**/
    private FusedLocationProviderClient locationProviderClient;

    /** A LOCATION INSTANCE **/
    private Location location;

    /** STRING TO HOLD THE DETECTED CITY NAME AND THE COORDINATES **/
    private String DETECTED_CITY = null;
    private String FINAL_CITY_ID = null;
    private LatLng LATLNG_ORIGIN;

    /** THE SEARCH VIEW INSTANCE **/
    private SearchView searchView;

    /** THE KENNEL DATA MODEL INSTANCE **/
    Kennel data;

    /** THE KENNELS ADAPTER AND ARRAY LIST INSTANCE **/
    private KennelsAdapter kennelsAdapter;
    private ArrayList<Kennel> arrKennels = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
//    @BindView(R.id.txtLocation) TextView txtLocation;
//    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listKennels) RecyclerView listKennels;
    @BindView(R.id.progressLoading) ProgressBar progressLoading;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
    @BindView(R.id.txtEmpty) TextView txtEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kennels_list);
        ButterKnife.bind(this);

        /* INSTANTIATE THE KENNELS ADAPTER */
        kennelsAdapter = new KennelsAdapter(NewKennelsList.this, arrKennels, LATLNG_ORIGIN);

        /* INSTANTIATE THE LOCATION CLIENT */
        locationProviderClient = LocationServices.getFusedLocationProviderClient(NewKennelsList.this);

        /* SHOW THE PROGRESS AND FETCH THE USER'S LOCATION */
//        linlaProgress.setVisibility(View.VISIBLE);
        progressLoading.setVisibility(View.VISIBLE);
        fetchUsersLocation();

        /* CONFIGURE THE RECYCLER VIEW **/
        configRecycler();
    }

    private class fetchKennels extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String strUrl = AppPrefs.context().getString(R.string.url_kennels_list);
            HttpUrl.Builder builder = HttpUrl.parse(strUrl).newBuilder();
            builder.addQueryParameter("cityID", FINAL_CITY_ID);
            String FINAL_URL = builder.build().toString();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(FINAL_URL)
                    .build();
            okhttp3.Call call = client.newCall(request);
            try {
                okhttp3.Response response = call.execute();
                String strResult = response.body().string();
                JSONObject JORoot = new JSONObject(strResult);
                if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
                    JSONArray JAKennels = JORoot.getJSONArray("kennels");
                    if (JAKennels.length() > 0) {
                        for (int i = 0; i < JAKennels.length(); i++) {
                            JSONObject JOKennels = JAKennels.getJSONObject(i);
                            data = new Kennel();

                            /* GET THE KENNEL ID */
                            if (JOKennels.has("kennelID"))  {
                                data.setKennelID(JOKennels.getString("kennelID"));
                            } else {
                                data.setKennelID(null);
                            }

                            /* GET THE KENNEL COVER PHOTO */
                            if (JOKennels.has("kennelCoverPhoto")
                                    && !JOKennels.getString("kennelCoverPhoto").equalsIgnoreCase("")
                                    && !JOKennels.getString("kennelCoverPhoto").equalsIgnoreCase("null"))  {
                                data.setKennelCoverPhoto(JOKennels.getString("kennelCoverPhoto"));
                            } else {
                                data.setKennelCoverPhoto(null);
                            }

                            /* GET THE KENNEL NAME */
                            if (JOKennels.has("kennelName"))    {
                                data.setKennelName(JOKennels.getString("kennelName"));
                            } else {
                                data.setKennelName(null);
                            }

                            /* GET THE KENNEL OWNER'S ID */
                            if (JOKennels.has("kennelOwnerID")) {
                                data.setKennelOwnerID(JOKennels.getString("kennelOwnerID"));
                            } else {
                                data.setKennelOwnerID(null);
                            }

                            /* GET THE KENNEL OWNER'S NAME */
                            if (JOKennels.has("kennelOwnerName"))   {
                                data.setKennelOwnerName(JOKennels.getString("kennelOwnerName"));
                            } else {
                                data.setKennelOwnerName(null);
                            }

                            /* GET THE KENNEL OWNER'S DISPLAY PROFILE */
                            if (JOKennels.has("kennelOwnerDisplayProfile")) {
                                data.setKennelOwnerDisplayProfile(JOKennels.getString("kennelOwnerDisplayProfile"));
                            } else {
                                data.setKennelOwnerDisplayProfile(null);
                            }

                            /* GET THE KENNEL ADDRESS */
                            if (JOKennels.has("kennelAddress")) {
                                data.setKennelAddress(JOKennels.getString("kennelAddress"));
                            } else {
                                data.setKennelAddress(null);
                            }

                            /* GET THE KENNEL PIN CODE */
                            if (JOKennels.has("kennelPinCode")) {
                                data.setKennelPinCode(JOKennels.getString("kennelPinCode"));
                            } else {
                                data.setKennelPinCode(null);
                            }

                            /* GET THE KENNEL COUNTY ID */
                            if (JOKennels.has("countryID")) {
                                data.setCountryID(JOKennels.getString("countryID"));
                            } else {
                                data.setCountryID(null);
                            }

                            /* GET THE KENNEL COUNTRY NAME */
                            if (JOKennels.has("countryName"))   {
                                data.setCountryName(JOKennels.getString("countryName"));
                            } else {
                                data.setCountryName(null);
                            }

                            /* GET THE KENNEL STATE ID */
                            if (JOKennels.has("stateID"))   {
                                data.setStateID(JOKennels.getString("stateID"));
                            } else {
                                data.setStateID(null);
                            }

                            /* GET THE KENNEL STATE NAME */
                            if (JOKennels.has("stateName")) {
                                data.setStateName(JOKennels.getString("stateName"));
                            } else {
                                data.setStateName(null);
                            }

                            /* GET THE KENNEL CITY ID */
                            if (JOKennels.has("cityID"))    {
                                data.setCityID(JOKennels.getString("cityID"));
                            } else {
                                data.setCityID(null);
                            }

                            /* GET THE KENNEL CITY NAME */
                            if (JOKennels.has("cityName"))  {
                                data.setCityName(JOKennels.getString("cityName"));
                            } else {
                                data.setCityName(null);
                            }

                            /* GET THE KENNEL LATITUDE */
                            if (JOKennels.has("kennelLatitude"))    {
                                data.setKennelLatitude(JOKennels.getString("kennelLatitude"));
                            } else {
                                data.setKennelLatitude(null);
                            }

                            /* GET THE KENNEL LONGITUDE */
                            if (JOKennels.has("kennelLongitude"))   {
                                data.setKennelLongitude(JOKennels.getString("kennelLongitude"));
                            } else {
                                data.setKennelLongitude(null);
                            }

                            /* GET THE CLINIC LATITUDE AND LONGITUDE */
                            String kennelLatitude = JOKennels.getString("kennelLatitude");
                            String kennelLongitude = JOKennels.getString("kennelLongitude");
                            if (kennelLatitude != null
                                    && !kennelLatitude.equalsIgnoreCase("0")
                                    && kennelLongitude != null
                                    && !kennelLongitude.equalsIgnoreCase("0"))    {

                                /* GET THE DESTINATION (CLINIC) */
                                Double latitude = Double.valueOf(kennelLatitude);
                                Double longitude = Double.valueOf(kennelLongitude);
                                LatLng LATLNG_DESTINATION = new LatLng(latitude, longitude);
                                String URL_DISTANCE = getUrl(LATLNG_ORIGIN, LATLNG_DESTINATION);
                                OkHttpClient clientDistance = new OkHttpClient();
                                Request requestDistance = new Request.Builder()
                                        .url(URL_DISTANCE)
                                        .build();
                                okhttp3.Call callDistance = clientDistance.newCall(requestDistance);
                                okhttp3.Response respDistance = callDistance.execute();
                                String strDistance = respDistance.body().string();
                                JSONObject JORootDistance = new JSONObject(strDistance);
                                JSONArray array = JORootDistance.getJSONArray("routes");
                                JSONObject JORoutes = array.getJSONObject(0);
                                JSONArray JOLegs= JORoutes.getJSONArray("legs");
                                JSONObject JOSteps = JOLegs.getJSONObject(0);
                                JSONObject JODistance = JOSteps.getJSONObject("distance");
                                if (JODistance.has("text")) {
                                    data.setKennelDistance(JODistance.getString("text"));
                                } else {
                                    data.setKennelDistance("Unknown");
                                }
                            } else {
                                data.setKennelDistance("Unknown");
                            }

                            /* GET THE KENNEL'S PHONE PREFIX #1*/
                            if (JOKennels.has("kennelPhonePrefix1"))    {
                                data.setKennelPhonePrefix1(JOKennels.getString("kennelPhonePrefix1"));
                            } else {
                                data.setKennelPhonePrefix1(null);
                            }

                            /* GET THE KENNEL'S PHONE NUMBER #1*/
                            if (JOKennels.has("kennelPhoneNumber1"))    {
                                data.setKennelPhoneNumber1(JOKennels.getString("kennelPhoneNumber1"));
                            } else {
                                data.setKennelPhoneNumber1(null);
                            }

                            /* GET THE KENNEL'S PHONE PREFIX #2*/
                            if (JOKennels.has("kennelPhonePrefix2"))    {
                                data.setKennelPhonePrefix2(JOKennels.getString("kennelPhonePrefix2"));
                            } else {
                                data.setKennelPhonePrefix2(null);
                            }

                            /* GET THE KENNEL'S PHONE NUMBER #2*/
                            if (JOKennels.has("kennelPhoneNumber2"))    {
                                data.setKennelPhoneNumber2(JOKennels.getString("kennelPhoneNumber2"));
                            } else {
                                data.setKennelPhoneNumber1(null);
                            }

                            /* GET THE KENNEL'S PET CAPACITY */
                            if (JOKennels.has("kennelPetCapacity"))    {
                                data.setKennelPetCapacity(JOKennels.getString("kennelPetCapacity"));
                            } else {
                                data.setKennelPetCapacity(null);
                            }

                            /* THE TOTAL VOTES, TOTAL LIKES AND TOTAL DISLIKES */
                            int TOTAL_VOTES = 0;
                            int TOTAL_LIKES = 0;

                            /* GET THE POSITIVE REVIEWS / FEEDBACK */
                            String URL_POSITIVE_REVIEWS = AppPrefs.context().getString(R.string.url_positive_kennel_reviews);
                            HttpUrl.Builder builderPositive = HttpUrl.parse(URL_POSITIVE_REVIEWS).newBuilder();
                            builderPositive.addQueryParameter("kennelID", data.getKennelID());
                            builderPositive.addQueryParameter("kennelRecommendStatus", "Yes");
                            String FINAL_URL_POSITIVE = builderPositive.build().toString();
                            OkHttpClient clientPositive = new OkHttpClient();
                            Request requestPositive = new Request.Builder()
                                    .url(FINAL_URL_POSITIVE)
                                    .build();
                            okhttp3.Call callPositive = clientPositive.newCall(requestPositive);
                            okhttp3.Response responsePositive = callPositive.execute();
                            String strPositiveReview = responsePositive.body().string();
                            JSONObject JORootPositive = new JSONObject(strPositiveReview);
                            if (JORootPositive.has("error") && JORootPositive.getString("error").equalsIgnoreCase("false")) {
                                JSONArray JAPositiveReviews = JORootPositive.getJSONArray("reviews");
                                TOTAL_LIKES = JAPositiveReviews.length();
                                TOTAL_VOTES = TOTAL_VOTES + JAPositiveReviews.length();
                            }

                            /* GET THE POSITIVE REVIEWS / FEEDBACK */
                            String URL_NEGATIVE_REVIEWS = AppPrefs.context().getString(R.string.url_negative_kennel_reviews);
                            HttpUrl.Builder builderNegative = HttpUrl.parse(URL_NEGATIVE_REVIEWS).newBuilder();
                            builderNegative.addQueryParameter("kennelID", data.getKennelID());
                            builderNegative.addQueryParameter("kennelRecommendStatus", "No");
                            String FINAL_URL_Negative = builderNegative.build().toString();
                            OkHttpClient clientNegative = new OkHttpClient();
                            Request requestReviews = new Request.Builder()
                                    .url(FINAL_URL_Negative)
                                    .build();
                            okhttp3.Call reviewCall = clientNegative.newCall(requestReviews);
                            okhttp3.Response responseNegative = reviewCall.execute();
                            String strNegativeReview = responseNegative.body().string();
                            JSONObject JORootNegative = new JSONObject(strNegativeReview);
                            if (JORootNegative.has("error") && JORootNegative.getString("error").equalsIgnoreCase("false")) {
                                JSONArray JANegativeReviews = JORootNegative.getJSONArray("reviews");
                                TOTAL_VOTES = TOTAL_VOTES + JANegativeReviews.length();
                            }

                            /* GET THE TOTAL LIKES */
                            data.setKennelLikes(String.valueOf(TOTAL_LIKES));

                            /* CALCULATE THE PERCENTAGE OF LIKES */
                            double percentLikes = ((double)TOTAL_LIKES / TOTAL_VOTES) * 100;
                            int finalPercentLikes = (int)percentLikes;
                            data.setKennelLikesPercent(String.valueOf(finalPercentLikes) + "%");

                            /* GET THE TOTAL NUMBER OF REVIEWS / VOTES */
                            Resources resReviews = AppPrefs.context().getResources();
                            String reviewQuantity = null;
                            if (TOTAL_VOTES == 0)   {
                                reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
                            } else if (TOTAL_VOTES == 1)    {
                                reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
                            } else if (TOTAL_VOTES > 1) {
                                reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
                            }
                            data.setKennelVotes(reviewQuantity);

                            /* GET THE KENNEL RATING */

                            /* ADD THE GATHERED DATA TO THE ARRAY LIST */
                            arrKennels.add(data);
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
//                                    if (linlaProgress.getVisibility() == View.VISIBLE)  {
//                                        linlaProgress.setVisibility(View.GONE);
//                                    }
                                    kennelsAdapter.notifyDataSetChanged();
                                }
                            }; runOnUiThread(runnable);
                        }
//
                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                        Runnable run = new Runnable() {
                            @Override
                            public void run() {
                                listKennels.setVisibility(View.VISIBLE);
                                linlaEmpty.setVisibility(View.GONE);
                            }
                        }; runOnUiThread(run);
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        Runnable run = new Runnable() {
                            @Override
                            public void run() {
                                linlaEmpty.setVisibility(View.VISIBLE);
                                listKennels.setVisibility(View.GONE);
                            }
                        }; runOnUiThread(run);
                    }
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    Runnable run = new Runnable() {
                        @Override
                        public void run() {
                            linlaEmpty.setVisibility(View.VISIBLE);
                            listKennels.setVisibility(View.GONE);
                        }
                    }; runOnUiThread(run);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

//            /* SET THE ADAPTER TO THE RECYCLER VIEW */
//            listKennels.setAdapter(kennelsAdapter);

            /* HIDE THE PROGRESSBAR AFTER LOADING THE DATA */
            progressLoading.setVisibility(View.GONE);
        }
    }

    /** CONCATENATE THE URL TO THE GOOGLE MAPS API FOR GETTING THE DISTANCE **/
    private String getUrl(LatLng origin, LatLng destination) {
        /* ORIGIN OF THE ROUTE */
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        /* DESTINATION OF THE ROUTE */
        String str_dest = "destination=" + destination.latitude + "," + destination.longitude;

        /* SENSOR ENABLED */
        String sensor = "sensor=false&key=" + AppPrefs.context().getString(R.string.google_directions_api_key);

        /* BUILDING THE PARAMETERS FOR THE WEB SERVICE */
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        /* THE OUTPUT FORMAT */
        String output = "json";

        /* BUILD THE FINAL URL FOR THE WEB SERVICE */
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
    }

    /** GET THE LIST OF KENNELS **/
    private void fetchKennels() {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<Kennels> call = api.fetchKennelsListByCity(FINAL_CITY_ID);
        call.enqueue(new Callback<Kennels>() {
            @Override
            public void onResponse(Call<Kennels> call, Response<Kennels> response) {
                if (response.body() != null && response.body().getKennels() != null)    {
                    arrKennels = response.body().getKennels();
                    if (arrKennels.size() > 0)  {
                        /* SET THE ADAPTER */
                        listKennels.setAdapter(new KennelsAdapter(NewKennelsList.this, arrKennels, LATLNG_ORIGIN));

                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                        listKennels.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);

                        /* HIDE THE PROGRESS BAR */
//                        linlaProgress.setVisibility(View.GONE);
                        progressLoading.setVisibility(View.GONE);
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listKennels.setVisibility(View.GONE);

                        /* HIDE THE PROGRESS BAR */
//                        linlaProgress.setVisibility(View.GONE);
                        progressLoading.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listKennels.setVisibility(View.GONE);

                    /* HIDE THE PROGRESS BAR */
//                    linlaProgress.setVisibility(View.GONE);
                    progressLoading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Kennels> call, Throwable t) {
//                Log.e("KENNELS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** FETCH THE KENNEL SEARCH RESULTS **/
    private void fetchKennelResults(String query) {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<Kennels> call = api.searchKennels(FINAL_CITY_ID, query);
        call.enqueue(new Callback<Kennels>() {
            @Override
            public void onResponse(Call<Kennels> call, Response<Kennels> response) {
                if (response.body() != null && response.body().getKennels() != null)    {
                    arrKennels = response.body().getKennels();
                    if (arrKennels.size() > 0)  {
                        /* SET THE ADAPTER */
                        listKennels.setAdapter(new KennelsAdapter(NewKennelsList.this, arrKennels, LATLNG_ORIGIN));

                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                        listKennels.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);

                        /* HIDE THE PROGRESS BAR */
//                        linlaProgress.setVisibility(View.GONE);
                        progressLoading.setVisibility(View.GONE);
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listKennels.setVisibility(View.GONE);

                        /* HIDE THE PROGRESS BAR */
//                        linlaProgress.setVisibility(View.GONE);
                        progressLoading.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listKennels.setVisibility(View.GONE);

                    /* HIDE THE PROGRESS BAR */
//                    linlaProgress.setVisibility(View.GONE);
                    progressLoading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Kennels> call, Throwable t) {
//                Log.e("KENNELS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** FETCH THE USER'S LOCATION **/
    private void fetchUsersLocation() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(NewKennelsList.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)   {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))    {
                /* SHOW THE DIALOG */
                new MaterialDialog.Builder(this)
                        .icon(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_black_24dp))
                        .title(getString(R.string.location_permission_title))
                        .cancelable(true)
                        .content(getString(R.string.location_permission_message))
                        .positiveText(getString(R.string.permission_grant))
                        .negativeText(getString(R.string.permission_deny))
                        .theme(Theme.LIGHT)
                        .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.cancel();
                                new MaterialDialog.Builder(NewKennelsList.this)
                                        .icon(ContextCompat.getDrawable(NewKennelsList.this, R.drawable.ic_info_outline_black_24dp))
                                        .title(getString(R.string.doctor_location_denied_title))
                                        .cancelable(true)
                                        .content(getString(R.string.doctor_location_denied_message))
                                        .positiveText(getString(R.string.permission_grant))
                                        .negativeText(getString(R.string.permission_nevermind))
                                        .theme(Theme.LIGHT)
                                        .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.cancel();
                                            }
                                        })
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.cancel();
                                                ActivityCompat.requestPermissions(
                                                        NewKennelsList.this,
                                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CONSTANT);
                                            }
                                        }).show();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(
                                        NewKennelsList.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CONSTANT);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_FINE_LOCATION_CONSTANT);
            }
        } else {
            locationProviderClient.getLastLocation()
                    .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                location = task.getResult();

                                /* GET THE ORIGIN LATLNG */
                                LATLNG_ORIGIN = new LatLng(location.getLatitude(), location.getLongitude());

                                /* FETCH THE LOCATION USING GEOCODER */
                                fetchTheLocation();
                            } else {
//                                Log.e("EXCEPTION", String.valueOf(task.getException()));
                                Crashlytics.logException(task.getException());
                            }
                        }
                    });
        }
    }

    /***** FETCH THE LOCATION USING GEOCODER *****/
    private void fetchTheLocation() {
        Geocoder geocoder = new Geocoder(NewKennelsList.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0)   {
                DETECTED_CITY = addresses.get(0).getLocality();

                if (DETECTED_CITY != null && !DETECTED_CITY.equalsIgnoreCase("null")) {
//                    Log.e("CITY", DETECTED_CITY);
                    new FetchCityID(this).execute(DETECTED_CITY);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
//            Log.e("GEOCODER", e.getMessage());
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onCityID(String result) {
        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* GET THE RESULT */
        FINAL_CITY_ID = result;

        /* CHECK FOR A VALID RESULT */
        if (FINAL_CITY_ID != null)   {
//            Log.e("CITY ID", FINAL_CITY_ID);
            /* FETCH THE LIST OF KENNELS */
//            new FetchKennels(this).execute(FINAL_CITY_ID, LATLNG_ORIGIN.latitude, LATLNG_ORIGIN.longitude);
            new fetchKennels().execute();
//            fetchKennels();
        } else {
            /* SET THE ERROR MESSAGE */
            txtEmpty.setText(getString(R.string.doctor_list_location_not_served));

            /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
            linlaEmpty.setVisibility(View.VISIBLE);
            listKennels.setVisibility(View.GONE);

            /* HIDE THE PROGRESS */
//            linlaProgress.setVisibility(View.GONE);
            progressLoading.setVisibility(View.GONE);
        }
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listKennels.setLayoutManager(manager);
        listKennels.setHasFixedSize(true);

        /* SET THE ADAPTER */
        listKennels.setAdapter(kennelsAdapter);
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Find Kennels";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getApplicationContext()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        String strSubtitle = "In " + DETECTED_CITY;
        SpannableString subTitle = new SpannableString(strSubtitle);
        subTitle.setSpan(new TypefaceSpan(getApplicationContext()), 0, subTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(subTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search_kennels, menu);
        MenuItem search = menu.findItem(R.id.menuSearchKennels);
        searchView = (SearchView) search.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.search_hint_kennels));
        searchView.setIconified(true);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query != null)  {
            /* CLEAR THE ARRAY, SHOW THE PROGRESS AND FETCH THE KENNEL SEARCH RESULTS */
            arrKennels.clear();
//            linlaProgress.setVisibility(View.VISIBLE);
            progressLoading.setVisibility(View.VISIBLE);
            fetchKennelResults(query);
            searchView.clearFocus();
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_FINE_LOCATION_CONSTANT)   {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                /* FETCH THE USER'S LOCATION */
                fetchUsersLocation();
            } else {
                new MaterialDialog.Builder(this)
                        .icon(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_black_24dp))
                        .title(getString(R.string.doctor_location_denied_title))
                        .cancelable(true)
                        .content(getString(R.string.doctor_location_denied_message))
                        .positiveText(getString(R.string.permission_grant))
                        .negativeText(getString(R.string.permission_nevermind))
                        .theme(Theme.LIGHT)
                        .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.cancel();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(
                                        NewKennelsList.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CONSTANT);
                            }
                        }).show();
            }
        }
    }
}