package biz.zenpets.users.kennels;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.adapters.kennels.KennelsAdapter;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.kennels.Kennel;
import biz.zenpets.users.utils.models.kennels.Kennels;
import biz.zenpets.users.utils.models.kennels.KennelsAPI;
import biz.zenpets.users.utils.models.location.City;
import biz.zenpets.users.utils.models.location.LocationsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KennelsList extends AppCompatActivity {

    /** A FUSED LOCATION PROVIDER CLIENT INSTANCE**/
    private FusedLocationProviderClient locationProviderClient;

    /** A LOCATION INSTANCE **/
    private Location location;

    /** STRING TO HOLD THE DETECTED CITY NAME FOR QUERYING THE ADOPTIONS **/
    private String DETECTED_CITY = null;
    private String FINAL_CITY_ID = null;

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int ACCESS_FINE_LOCATION_CONSTANT = 200;

    /** THE LATLNG INSTANCE FOR GETTING THE USER'S CURRENT COORDINATES **/
    private LatLng LATLNG_ORIGIN;

    /** THE KENNELS ADAPTER AND AN ARRAY LIST TO STORE THE LIST OF KENNELS **/
    KennelsAdapter adapter;
    ArrayList<Kennel> arrKennels = new ArrayList<>();

    /** THE PAGE NUMBER COUNTER **/
    int pageNumber = 1;

    /** BOOLEAN TO TRACK WHEN KENNELS ARE BEING LOADED **/
    Boolean blnLoading = false;

    /** BOOLEAN TO CHECK IF MORE DATA IS AVAILABLE **/
    Boolean blnDataAvailable = true;

    int pastVisiblesItems, visibleItemCount, totalItemCount;

    /** A LINEAR LAYOUT MANAGER INSTANCE **/
    LinearLayoutManager manager;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtLocation) TextView txtLocation;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listKennels) RecyclerView listKennels;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kennels_list);
        ButterKnife.bind(this);

        /* INSTANTIATE THE LOCATION CLIENT */
        locationProviderClient = LocationServices.getFusedLocationProviderClient(KennelsList.this);

        /* FETCH THE USER'S LOCATION */
        getUsersLocation();

        /* SET THE SCROLL LISTENER FOR THE RECYCLER VIEW */
        listKennels.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItemCount = manager.getChildCount();
                    totalItemCount = manager.getItemCount();
                    pastVisiblesItems = manager.findFirstVisibleItemPosition();

                    if (blnDataAvailable && !blnLoading) {
                        /* TOGGLE THE LOADING BOOLEAN TO TRUE */
                        blnLoading = true;

                        /* INCREMENT THE PAGE NUMBER */
                        pageNumber++;

                        /* FETCH THE NEXT SET OF KENNELS */
                        fetchKennels();
                    }
                }
            }
        });
    }

    /***** FETCH THE KENNEL LISTINGS IN THE DETECTED CITY *****/
    private void fetchKennels() {
        /* INSTANTIATE THE KENNELS ADAPTER */
        adapter = new KennelsAdapter(KennelsList.this, arrKennels, LATLNG_ORIGIN);

        /* CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<Kennels> call = api.fetchKennelsListByCity(FINAL_CITY_ID, String.valueOf(pageNumber));
        call.enqueue(new Callback<Kennels>() {
            @Override
            public void onResponse(Call<Kennels> call, Response<Kennels> response) {
//                Log.e("RAW", String.valueOf(response.raw()));
                if (response.body().getError()) {
                    /* TOGGLE THE BOOLEAN TO INDICATE NO FURTHER DATA AVAILABLE */
                    blnDataAvailable = false;
                } else {
                    try {
                        String strResult = new Gson().toJson(response.body());
                        JSONObject JORoot = new JSONObject(strResult);
                        if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
                            JSONArray JAKennels = JORoot.getJSONArray("kennels");

                            /* AN INSTANCE OF THE KENNELS DATA MODEL CLASS */
                            Kennel data;

                            for (int i = 0; i < JAKennels.length(); i++) {
                                JSONObject JOKennels = JAKennels.getJSONObject(i);
//                                Log.e("KENNEL", String.valueOf(JOKennels));

                                /* INSTANTIATE THE KENNELS DATA MODEL INSTANCE */
                                data = new Kennel();

                                /* GET THE KENNEL ID */
                                if (JOKennels.has("kennelID"))  {
                                    data.setKennelID(JOKennels.getString("kennelID"));
                                } else {
                                    data.setKennelID(null);
                                }

                                /* GET THE KENNEL NAME */
                                if (JOKennels.has("kennelName"))    {
                                    data.setKennelName(JOKennels.getString("kennelName"));
                                } else {
                                    data.setKennelName(null);
                                }

                                /* GET THE KENNEL'S COVER PHOTO */
                                if (JOKennels.has("kennelCoverPhoto")
                                        && !JOKennels.getString("kennelCoverPhoto").equalsIgnoreCase("")
                                        && !JOKennels.getString("kennelCoverPhoto").equalsIgnoreCase("null"))  {
                                    data.setKennelCoverPhoto(JOKennels.getString("kennelCoverPhoto"));
                                } else {
                                    data.setKennelCoverPhoto(null);
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

                                /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                                arrKennels.add(data);
                                adapter.notifyDataSetChanged();

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    /* TOGGLE THE BOOLEAN TO INDICATE DATA IS AVAILABLE */
                    blnDataAvailable = true;
                }

                /* SET THE LOADING TO FALSE */
                blnLoading = false;

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Kennels> call, Throwable t) {
                Log.e("KENNELS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE USER'S LOCATION *****/
    private void getUsersLocation() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(KennelsList.this,
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
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(
                                        KennelsList.this,
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

                                /* FETCH THE LOCATION USING A GEOCODER */
                                fetchLocation();
                            } else {
                                Crashlytics.logException(task.getException());
                            }
                        }
                    });
        }
    }

    /***** FETCH THE LOCATION USING A GEOCODER *****/
    private void fetchLocation() {
        Geocoder geocoder = new Geocoder(KennelsList.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0)   {
                DETECTED_CITY = addresses.get(0).getLocality();

                if (DETECTED_CITY != null)  {
                    if (!DETECTED_CITY.equalsIgnoreCase("null")) {
                        /* SET THE LOCATION AND FETCH THE CITY ID*/
                        txtLocation.setText(DETECTED_CITY);
                        fetchCityID();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***** FETCH THE CITY ID *****/
    private void fetchCityID() {
        /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
        linlaProgress.setVisibility(View.VISIBLE);

        LocationsAPI api = ZenApiClient.getClient().create(LocationsAPI.class);
        Call<City> call = api.getCityID(DETECTED_CITY);
        call.enqueue(new Callback<City>() {
            @Override
            public void onResponse(Call<City> call, Response<City> response) {
                /* GET THE DATA */
                City city = response.body();
                if (city != null)   {
                    /* GET THE CITY ID */
                    FINAL_CITY_ID = city.getCityID();
                    if (FINAL_CITY_ID != null)  {
                        /* TOGGLE THE LOADING BOOLEAN AND FETCH THE KENNEL LISTINGS IN THE DETECTED CITY */
                        blnLoading = true;
                        fetchKennels();
                    } else {
                        new MaterialDialog.Builder(KennelsList.this)
                                .title("Location not Served!")
                                .content("We currently do not serve this City. but fear not. We will have you covered shortly.")
                                .positiveText("OKAY")
                                .theme(Theme.LIGHT)
                                .icon(ContextCompat.getDrawable(KennelsList.this, R.drawable.ic_info_outline_black_24dp))
                                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                .show();
                    }
                } else {
                    new MaterialDialog.Builder(KennelsList.this)
                            .title("Location not Served!")
                            .content("We currently do not serve this City. but fear not. We will have you covered shortly.")
                            .positiveText("OKAY")
                            .theme(Theme.LIGHT)
                            .icon(ContextCompat.getDrawable(KennelsList.this, R.drawable.ic_info_outline_black_24dp))
                            .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<City> call, Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_FINE_LOCATION_CONSTANT)   {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                /* FETCH THE USER'S LOCATION */
                getUsersLocation();
            } else {
                new MaterialDialog.Builder(this)
                        .icon(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_black_24dp))
                        .title(getString(R.string.doctor_location_denied_title))
                        .cancelable(true)
                        .content(getString(R.string.adoption_location_denied_message))
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
                                        KennelsList.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CONSTANT);
                            }
                        }).show();
            }
        }
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listKennels.setLayoutManager(manager);
        listKennels.setHasFixedSize(true);

        /* INSTANTIATE AND SET THE ADAPTER */
        listKennels.setAdapter(adapter);
    }
}