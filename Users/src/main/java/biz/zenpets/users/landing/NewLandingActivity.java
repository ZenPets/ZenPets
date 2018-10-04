package biz.zenpets.users.landing;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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
import biz.zenpets.users.utils.adapters.doctors.DoctorsSubsetAdapter;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.adoptions.adoption.Adoption;
import biz.zenpets.users.utils.models.doctors.DoctorsAPI;
import biz.zenpets.users.utils.models.doctors.list.Doctor;
import biz.zenpets.users.utils.models.doctors.list.Doctors;
import biz.zenpets.users.utils.models.location.City;
import biz.zenpets.users.utils.models.location.Locality;
import biz.zenpets.users.utils.models.location.LocationsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewLandingActivity extends AppCompatActivity {

    /** A FUSED LOCATION PROVIDER CLIENT INSTANCE**/
    private FusedLocationProviderClient mFusedLocationClient;

    /** A LOCATION INSTANCE **/
    private Location mLocation;

    /** STRING TO HOLD THE DETECTED CITY NAME AND LOCALITY FOR QUERYING THE DOCTORS INFORMATION **/
    private String DETECTED_CITY = null;
    private String FINAL_CITY_ID = null;
    private String DETECTED_LOCALITY = null;
    private String FINAL_LOCALITY_ID = null;

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int ACCESS_FINE_LOCATION_CONSTANT = 200;

    /** THE LATLNG INSTANCE FOR GETTING THE USER'S CURRENT COORDINATES **/
    private LatLng LATLNG_ORIGIN;

    /** THE DOCTORS ARRAY LIST INSTANCE **/
    private ArrayList<Doctor> arrDoctors = new ArrayList<>();

    /** THE ADOPTIONS ARRAY INSTANCE **/
    ArrayList<Adoption> arrAdoptions = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.listDoctors) RecyclerView listDoctors;
    @BindView(R.id.emptyDoctors) ConstraintLayout emptyDoctors;
    @BindView(R.id.listAdoptions) RecyclerView listAdoptions;
    @BindView(R.id.emptyAdoptions) ConstraintLayout emptyAdoptions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_landing_activity);
        ButterKnife.bind(this);

        /* INSTANTIATE THE LOCATION CLIENT */
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(NewLandingActivity.this);

        /* FETCH THE USER'S LOCATION */
        fetchUsersLocation();
    }

    /** FETCH THE LIST OF DOCTORS **/
    private void fetchDoctors() {
        DoctorsAPI api = ZenApiClient.getClient().create(DoctorsAPI.class);
        Call<Doctors> call = api.fetchDoctorsSubset(
                FINAL_CITY_ID, FINAL_LOCALITY_ID,
                String.valueOf(LATLNG_ORIGIN.latitude), String.valueOf(LATLNG_ORIGIN.longitude)
        );
        call.enqueue(new Callback<Doctors>() {
            @Override
            public void onResponse(Call<Doctors> call, Response<Doctors> response) {
//                Log.e("DOCTORS RESPONSE", String.valueOf(response.raw()));
                if (response.body() != null && response.body().getDoctors() != null)    {

                    /* PROCESS THE RESPONSE */
                    arrDoctors = processResult(response);
                    if (arrDoctors.size() > 0)  {
                        listDoctors.setAdapter(new DoctorsSubsetAdapter(NewLandingActivity.this, arrDoctors, LATLNG_ORIGIN));
                    } else {
                    }
                }
            }

            @Override
            public void onFailure(Call<Doctors> call, Throwable t) {
//                Log.e("SUBSET FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    private ArrayList<Doctor> processResult(Response<Doctors> response) {
        ArrayList<Doctor> doctors = new ArrayList<>();
        try {
            String strResult = new Gson().toJson(response.body());
            JSONObject JORoot = new JSONObject(strResult);
            if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
                JSONArray JADoctors = JORoot.getJSONArray("doctors");
                if (JADoctors.length() > 0) {
                    Doctor data;
                    for (int i = 0; i < JADoctors.length(); i++) {
                        final JSONObject JODoctors = JADoctors.getJSONObject(i);
                        data = new Doctor();

                        /* GET THE CLINIC ID */
                        if (JODoctors.has("clinicID"))  {
                            data.setClinicID(JODoctors.getString("clinicID"));
                        } else {
                            data.setClinicID(null);
                        }

                        /* GET THE CLINIC NAME */
                        if (JODoctors.has("clinicName"))    {
                            data.setClinicName(JODoctors.getString("clinicName"));
                        } else {
                            data.setClinicName(null);
                        }

                        /* GET THE CLINIC LOGO */
                        if (JODoctors.has("clinicLogo")
                                &&  JODoctors.getString("clinicLogo") != null
                                && !JODoctors.getString("clinicLogo").equalsIgnoreCase("null")
                                && !JODoctors.getString("clinicLogo").equalsIgnoreCase(""))  {
                            data.setClinicLogo(JODoctors.getString("clinicLogo"));
                        } else {
                            data.setClinicLogo(null);
                        }

                        /* GET THE CLINIC ADDRESS */
                        if (JODoctors.has("clinicAddress")) {
                            data.setClinicAddress(JODoctors.getString("clinicAddress"));
                        } else {
                            data.setClinicAddress(null);
                        }

                        /* GET THE CLINIC PIN CODE */
                        if (JODoctors.has("clinicPinCode"))   {
                            data.setClinicPinCode(JODoctors.getString("clinicPinCode"));
                        } else {
                            data.setClinicPinCode(null);
                        }

                        /* GET THE CLINIC COUNTY ID */
                        if (JODoctors.has("countryID")) {
                            data.setCountryID(JODoctors.getString("countryID"));
                        } else {
                            data.setCountryID(null);
                        }

                        /* GET THE CLINIC COUNTRY NAME */
                        if (JODoctors.has("countryName"))   {
                            data.setCountryName(JODoctors.getString("countryName"));
                        } else {
                            data.setCountryName(null);
                        }

                        /* GET THE CLINIC STATE ID */
                        if (JODoctors.has("stateID"))   {
                            data.setStateID(JODoctors.getString("stateID"));
                        } else {
                            data.setStateID(null);
                        }

                        /* GET THE CLINIC STATE NAME */
                        if (JODoctors.has("stateName")) {
                            data.setStateName(JODoctors.getString("stateName"));
                        } else {
                            data.setStateName(null);
                        }

                        /* GET THE CLINIC CITY ID */
                        if (JODoctors.has("cityID"))    {
                            data.setCityID(JODoctors.getString("cityID"));
                        } else {
                            data.setCityID(null);
                        }

                        /* GET THE CLINIC CITY NAME */
                        if (JODoctors.has("cityName"))  {
                            data.setCityName(JODoctors.getString("cityName"));
                        } else {
                            data.setCityName(null);
                        }

                        /* GET THE CLINIC LOCALITY ID */
                        if (JODoctors.has("localityID"))    {
                            data.setLocalityID(JODoctors.getString("localityID"));
                        } else {
                            data.setLocalityID(null);
                        }

                        /* GET THE CLINIC LOCALITY NAME */
                        if (JODoctors.has("localityName"))  {
                            data.setLocalityName(JODoctors.getString("localityName"));
                        } else {
                            data.setLocalityName(null);
                        }

                        /* GET THE KENNEL LATITUDE AND LONGITUDE */
                        if (JODoctors.has("clinicLatitude") && JODoctors.has("clinicLongitude"))   {
                            data.setClinicLatitude(JODoctors.getString("clinicLatitude"));
                            data.setClinicLongitude(JODoctors.getString("clinicLongitude"));
                        } else {
                            data.setClinicLatitude(null);
                            data.setClinicLongitude(null);
                        }

                        /* GET THE KENNEL DISTANCE */
                        if (JODoctors.has("clinicDistance")) {
                            data.setClinicDistance(JODoctors.getString("clinicDistance"));
                        } else {
                            data.setClinicDistance(null);
                        }

                        /* GET THE DOCTOR ID */
                        if (JODoctors.has("doctorID"))    {
                            data.setDoctorID(JODoctors.getString("doctorID"));
                        } else {
                            data.setDoctorID(null);
                        }

                        /* GET THE DOCTOR'S DISPLAY PROFILE */
                        if (JODoctors.has("doctorDisplayProfile"))    {
                            data.setDoctorDisplayProfile(JODoctors.getString("doctorDisplayProfile"));
                        } else {
                            data.setDoctorDisplayProfile(null);
                        }

                        /* GET THE DOCTOR'S PREFIX */
                        if (JODoctors.has("doctorPrefix"))    {
                            data.setDoctorPrefix(JODoctors.getString("doctorPrefix"));
                        } else {
                            data.setDoctorPrefix(null);
                        }

                        /* GET THE DOCTOR'S NAME */
                        if (JODoctors.has("doctorName"))    {
                            data.setDoctorName(JODoctors.getString("doctorName"));
                        } else {
                            data.setDoctorName(null);
                        }

                        /* GET THE DOCTOR'S EXPERIENCE */
                        if (JODoctors.has("doctorExperience"))    {
                            data.setDoctorExperience(JODoctors.getString("doctorExperience"));
                        } else {
                            data.setDoctorExperience(null);
                        }

                        /* GET THE TOTAL REVIEWS, POSITIVE, AND FINALLY, CALCULATE THE PERCENTAGES */
                        if (JODoctors.has("doctorReviews")
                                && JODoctors.has("doctorPositives"))  {
                            String doctorReviews = JODoctors.getString("doctorReviews");
                            String doctorPositives = JODoctors.getString("doctorPositives");

                            int TOTAL_VOTES = Integer.parseInt(doctorReviews);
                            int TOTAL_LIKES = Integer.parseInt(doctorPositives);

                            /* CALCULATE THE PERCENTAGE OF LIKES */
                            double percentLikes = ((double)TOTAL_LIKES / TOTAL_VOTES) * 100;
                            int finalPercentLikes = (int)percentLikes;
                            String strLikesPercentage = String.valueOf(finalPercentLikes) + "%";

                            /* GET THE TOTAL NUMBER OF REVIEWS / VOTES */
                            Resources resReviews = getResources();
                            String reviewQuantity = null;
                            if (TOTAL_VOTES == 0)   {
                                reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
                            } else if (TOTAL_VOTES == 1)    {
                                reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
                            } else if (TOTAL_VOTES > 1) {
                                reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
                            }
                            String strVotes = reviewQuantity;
                            String open = getString(R.string.doctor_list_votes_open);
                            String close = getString(R.string.doctor_list_votes_close);
                            data.setDoctorVoteStats(getString(R.string.doctor_list_votes_placeholder, strLikesPercentage, open, strVotes, close));
                        } else {
                            data.setDoctorVoteStats("0");
                        }

                        /* GET THE AVERAGE CLINIC RATING */
                        if (JODoctors.has("clinicRating"))  {
                            data.setClinicRating(JODoctors.getString("clinicRating"));
                        } else {
                            data.setClinicRating("0");
                        }

                        /* GET THE CURRENCY SYMBOL */
                        if (JODoctors.has("currencySymbol"))  {
                            data.setCurrencySymbol(JODoctors.getString("currencySymbol"));
                        } else {
                            data.setCurrencySymbol(null);
                        }

                        /* GET THE DOCTOR'S CHARGES */
                        if (JODoctors.has("doctorCharges"))  {
                            data.setDoctorCharges(JODoctors.getString("doctorCharges"));
                        } else {
                            data.setDoctorCharges(null);
                        }

                        /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                        doctors.add(data);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
//            Log.e("EXCEPTION", e.getMessage());
            Crashlytics.logException(e);
        }
        return doctors;
    }

    /** FETCH THE USER'S LOCATION **/
    private void fetchUsersLocation() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(NewLandingActivity.this,
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
                                new MaterialDialog.Builder(NewLandingActivity.this)
                                        .icon(ContextCompat.getDrawable(NewLandingActivity.this, R.drawable.ic_info_outline_black_24dp))
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
                                                        NewLandingActivity.this,
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
                                        NewLandingActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CONSTANT);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_FINE_LOCATION_CONSTANT);
            }
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();

                                /* GET THE ORIGIN LATLNG */
                                LATLNG_ORIGIN = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
//                                Log.e("LATLNG", String.valueOf(LATLNG_ORIGIN));

                                /* CONFIGURE THE RECYCLER VIEW */
                                configRecycler();

                                /* FETCH THE LOCATION USING A GEOCODER INSTANCE */
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
        Geocoder geocoder = new Geocoder(NewLandingActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
            if (addresses.size() > 0)   {
                DETECTED_CITY = addresses.get(0).getLocality();
                DETECTED_LOCALITY = addresses.get(0).getSubLocality();

                if (DETECTED_CITY != null)  {
                    if (!DETECTED_CITY.equalsIgnoreCase("null")) {
                        if (DETECTED_LOCALITY != null)  {
                            if (!DETECTED_LOCALITY.equalsIgnoreCase("null"))   {
                                /* FETCH THE CITY ID */
                                fetchCityID();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
//            Log.e("GEOCODER", e.getMessage());
            Crashlytics.logException(e);
        }
    }

    /** FETCH THE CITY ID **/
    private void fetchCityID() {
        LocationsAPI api = ZenApiClient.getClient().create(LocationsAPI.class);
        Call<City> call = api.getCityID(DETECTED_CITY);
        call.enqueue(new Callback<City>() {
            @Override
            public void onResponse(Call<City> call, Response<City> response) {
//                Log.e("CITY RESPONSE", String.valueOf(response.raw()));
                City city = response.body();
                if (city != null)   {
                    /* GET THE RESULT */
                    FINAL_CITY_ID = city.getCityID();

                    /* CHECK FOR A VALID RESULT */
                    if (FINAL_CITY_ID != null)   {
                        /* FETCH THE LOCALITY ID */
                        fetchLocalityID();
                    } else {
                        /* SET THE ERROR MESSAGE */
//                        txtEmpty.setText(getString(R.string.doctor_list_location_not_served));

                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
//                        linlaEmpty.setVisibility(View.VISIBLE);
                        listDoctors.setVisibility(View.GONE);
                    }
                } else {
                    /* SET THE ERROR MESSAGE */
//                    txtEmpty.setText(getString(R.string.doctor_list_location_not_served));

                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
//                    linlaEmpty.setVisibility(View.VISIBLE);
                    listDoctors.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<City> call, Throwable t) {
//                Log.e("CITY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** FETCH THE LOCALITY ID **/
    private void fetchLocalityID() {
        LocationsAPI api = ZenApiClient.getClient().create(LocationsAPI.class);
        Call<Locality> call = api.getLocalityID(DETECTED_LOCALITY);
        call.enqueue(new Callback<Locality>() {
            @Override
            public void onResponse(Call<Locality> call, Response<Locality> response) {
//                Log.e("LOCALITY RESPONSE", String.valueOf(response.raw()));
                Locality locality = response.body();
                if (locality != null)   {
                    /* GET THE RESULT */
                    FINAL_LOCALITY_ID = locality.getLocalityID();

                    /* CHECK FOR A VALID, NON NULL RESULT */
                    if (FINAL_LOCALITY_ID != null)  {
                        /* FETCH THE LIST OF DOCTORS */
                        fetchDoctors();
                    } else {
                        /* SET THE ERROR MESSAGE */
//                        txtEmpty.setText(getString(R.string.doctor_list_location_not_served));

                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
//                        linlaEmpty.setVisibility(View.VISIBLE);
                        listDoctors.setVisibility(View.GONE);
                    }
                } else {
                    /* SET THE ERROR MESSAGE */
//                    txtEmpty.setText(getString(R.string.doctor_list_location_not_served));

                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
//                    linlaEmpty.setVisibility(View.VISIBLE);
                    listDoctors.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Locality> call, Throwable t) {
//                Log.e("LOCALITY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listDoctors.setLayoutManager(manager);
        listDoctors.setHasFixedSize(true);

        /* SET THE ADAPTER */
        listDoctors.setAdapter(new DoctorsSubsetAdapter(NewLandingActivity.this, arrDoctors, LATLNG_ORIGIN));
    }
}