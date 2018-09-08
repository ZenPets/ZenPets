package biz.zenpets.users.doctors;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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
import biz.zenpets.users.utils.adapters.doctors.DoctorsAdapter;
import biz.zenpets.users.utils.helpers.classes.FilterLocationActivity;
import biz.zenpets.users.utils.helpers.classes.PaginationScrollListener;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.clinics.promotions.Promotion;
import biz.zenpets.users.utils.models.doctors.DoctorsAPI;
import biz.zenpets.users.utils.models.doctors.list.Doctor;
import biz.zenpets.users.utils.models.doctors.list.DoctorPages;
import biz.zenpets.users.utils.models.doctors.list.Doctors;
import biz.zenpets.users.utils.models.location.City;
import biz.zenpets.users.utils.models.location.Locality;
import biz.zenpets.users.utils.models.location.LocationsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ConstantConditions")
public class DoctorsList extends AppCompatActivity
        /*implements FetchCityIDInterface, FetchLocalityIDInterface*/ {

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

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 0;
    private int currentPage = PAGE_START;

    /* THE DOCTORS ADAPTER AND THE ARRAY LIST INSTANCE */
    private DoctorsAdapter doctorsAdapter;
    private ArrayList<Doctor> arrDoctors = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtLocation) AppCompatTextView txtLocation;
//    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listDoctors) RecyclerView listDoctors;
    @BindView(R.id.progressLoading) ProgressBar progressLoading;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
    @BindView(R.id.txtEmpty) AppCompatTextView txtEmpty;

    /** SELECT THE LOCATION MANUALLY **/
    @OnClick(R.id.linlaLocationSelector) protected void selectLocation()    {
        Intent intent = new Intent(DoctorsList.this, FilterLocationActivity.class);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctors_list);
        ButterKnife.bind(this);

        /* INSTANTIATE THE LOCATION CLIENT */
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(DoctorsList.this);

        /* FETCH THE USER'S LOCATION */
        getUserLocation();

        /* CONFIGURE THE ACTIONBAR */
        configAB();
    }
    /***** FETCH THE FIRST LIST OF DOCTORS *****/
    private void fetchDoctors() {
        DoctorsAPI api = ZenApiClient.getClient().create(DoctorsAPI.class);
        Call<Doctors> call = api.fetchDoctorsList(
                FINAL_CITY_ID,
                FINAL_LOCALITY_ID,
                String.valueOf(LATLNG_ORIGIN.latitude),
                String.valueOf(LATLNG_ORIGIN.longitude),
                String.valueOf(currentPage));
        call.enqueue(new Callback<Doctors>() {
            @Override
            public void onResponse(Call<Doctors> call, Response<Doctors> response) {
//                Log.e("KENNELS LIST", String.valueOf(response.raw()));

                if (response.body() != null && response.body().getDoctors() != null)    {
                    /* PROCESS THE RESPONSE */
                    arrDoctors = processResult(response);
                    if (arrDoctors.size() > 0)  {
                        ArrayList<Doctor> doctors = arrDoctors;
                        progressLoading.setVisibility(View.GONE);
                        if (doctors != null && doctors.size() > 0)
                            doctorsAdapter.addAll(doctors);

                        if (currentPage <= TOTAL_PAGES) doctorsAdapter.addLoadingFooter();
                        else isLastPage = true;
                    } else {
                        /* MARK THE LAST PAGE FLAG TO "TRUE" */
                        isLastPage = true;

                        /* HIDE THE PROGRESS */
                        progressLoading.setVisibility(View.GONE);

                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listDoctors.setVisibility(View.GONE);
                    }
                } else {
                    /* MARK THE LAST PAGE FLAG TO "TRUE" */
                    isLastPage = true;

                    /* HIDE THE PROGRESS */
                    progressLoading.setVisibility(View.GONE);

                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listDoctors.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Doctors> call, Throwable t) {
//                Log.e("DOCTORS FAILURE", t.getMessage());
                Crashlytics.logException(t);

                /* HIDE THE PROGRESS */
                progressLoading.setVisibility(View.GONE);
            }
        });
    }

    /** FETCH THE NEXT SET OF DOCTORS **/
    private void fetchNextDoctors() {
        DoctorsAPI api = ZenApiClient.getClient().create(DoctorsAPI.class);
        Call<Doctors> call = api.fetchDoctorsList(
                FINAL_CITY_ID,
                FINAL_LOCALITY_ID,
                String.valueOf(LATLNG_ORIGIN.latitude),
                String.valueOf(LATLNG_ORIGIN.longitude),
                String.valueOf(currentPage));
        call.enqueue(new Callback<Doctors>() {
            @Override
            public void onResponse(Call<Doctors> call, Response<Doctors> response) {
//                Log.e("KENNELS LIST", String.valueOf(response.raw()));

                if (response.body() != null && response.body().getDoctors() != null)    {
                    /* PROCESS THE RESPONSE */
                    arrDoctors = processResult(response);

                    doctorsAdapter.removeLoadingFooter();
                    isLoading = false;

                    ArrayList<Doctor> doctors = arrDoctors;
//                    Log.e("KENNELS SIZE", String.valueOf(doctors.size()));
                    if (doctors != null && doctors.size() > 0)
                        doctorsAdapter.addAll(doctors);

                    if (currentPage != TOTAL_PAGES) doctorsAdapter.addLoadingFooter();
                    else isLastPage = true;

                    progressLoading.setVisibility(View.GONE);
                } else {
                    /* MARK THE LAST PAGE FLAG TO "TRUE" */
                    isLastPage = true;

                    /* HIDE THE PROGRESS */
                    progressLoading.setVisibility(View.GONE);

                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listDoctors.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Doctors> call, Throwable t) {
//                Log.e("DOCTORS FAILURE", t.getMessage());
                Crashlytics.logException(t);

                /* HIDE THE PROGRESS */
                progressLoading.setVisibility(View.GONE);
            }
        });
    }

    /** PROCESS THE KENNEL RESULTS **/
    private ArrayList<Doctor> processResult(Response<Doctors> response) {
        ArrayList<Doctor> doctors = new ArrayList<>();
        ArrayList<Promotion> promotions = new ArrayList<>();
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

                        /* GET THE PROMOTED KENNELS */
                        JSONArray JAPromotions = JODoctors.getJSONArray("promotions");
//                        Log.e("PROMOTIONS", String.valueOf(JAPromotions));
                        if (JAPromotions.length() > 0) {
                            Promotion promotion;
                            for (int j = 0; j < JAPromotions.length(); j++) {
                                JSONObject JOPromotions = JAPromotions.getJSONObject(j);
//                                Log.e("PROMOTIONS", String.valueOf(JOPromotions));
                                promotion = new Promotion();

                                /* GET THE PROMOTION ID */
                                if (JOPromotions.has("promotedID")) {
                                    promotion.setPromotedID(JOPromotions.getString("promotedID"));
                                } else {
                                    promotion.setPromotedID(null);
                                }

                                /* GET THE CLINIC ID */
                                if (JOPromotions.has("clinicID")) {
                                    promotion.setClinicID(JOPromotions.getString("clinicID"));
                                } else {
                                    promotion.setClinicID(null);
                                }

                                /* GET THE OPTION ID */
                                if (JOPromotions.has("optionID"))   {
                                    promotion.setOptionID(JOPromotions.getString("optionID"));
                                } else {
                                    promotion.setOptionID(null);
                                }

                                /* GET THE PAYMENT ID */
                                if (JOPromotions.has("paymentID"))  {
                                    promotion.setPaymentID(JOPromotions.getString("paymentID"));
                                } else {
                                    promotion.setPaymentID(null);
                                }

                                /* GET THE PROMOTED FROM DATE */
                                if (JOPromotions.has("promotedFrom"))   {
                                    promotion.setPromotedFrom(JOPromotions.getString("promotedFrom"));
                                } else {
                                    promotion.setPromotedFrom(null);
                                }

                                /* GET THE PROMOTED TO DATE */
                                if (JOPromotions.has("promotedTo")) {
                                    promotion.setPromotedTo(JOPromotions.getString("promotedTo"));
                                } else {
                                    promotion.setPromotedTo(null);
                                }

                                /* GET THE PROMOTED TIME STAMP */
                                if (JOPromotions.has("promotedTimestamp"))  {
                                    promotion.setPromotedTimestamp(JOPromotions.getString("promotedTimestamp"));
                                } else {
                                    promotion.setPromotedTimestamp(null);
                                }

                                /* GET THE CLINIC NAME */
                                if (JOPromotions.has("clinicName"))  {
                                    promotion.setClinicName(JOPromotions.getString("clinicName"));
                                } else {
                                    promotion.setClinicName(null);
                                }

                                /* GET THE CLINIC LOGO */
                                if (JOPromotions.has("clinicLogo"))  {
                                    promotion.setClinicLogo(JOPromotions.getString("clinicLogo"));
                                } else {
                                    promotion.setClinicLogo(null);
                                }

                                /* GET THE CLINIC ADDRESS */
                                if (JOPromotions.has("clinicAddress")) {
                                    promotion.setClinicAddress(JOPromotions.getString("clinicAddress"));
                                } else {
                                    promotion.setClinicAddress(null);
                                }

                                /* GET THE CLINIC LANDMARK */
                                if (JOPromotions.has("clinicLandmark")) {
                                    promotion.setClinicLandmark(JOPromotions.getString("clinicLandmark"));
                                } else {
                                    promotion.setClinicLandmark(null);
                                }

                                /* GET THE CLINIC PIN CODE */
                                if (JOPromotions.has("clinicPinCode")) {
                                    promotion.setClinicPinCode(JOPromotions.getString("clinicPinCode"));
                                } else {
                                    promotion.setClinicPinCode(null);
                                }

                                /* GET THE CLINIC COUNTRY ID */
                                if (JOPromotions.has("countryID")) {
                                    promotion.setCountryID(JOPromotions.getString("countryID"));
                                } else {
                                    promotion.setCountryID(null);
                                }

                                /* GET THE CLINIC COUNTRY NAME */
                                if (JOPromotions.has("countryName"))   {
                                    promotion.setCountryName(JOPromotions.getString("countryName"));
                                } else {
                                    promotion.setCountryName(null);
                                }

                                /* GET THE CLINIC STATE ID */
                                if (JOPromotions.has("stateID"))   {
                                    promotion.setStateID(JOPromotions.getString("stateID"));
                                } else {
                                    promotion.setStateID(null);
                                }

                                /* GET THE CLINIC STATE NAME */
                                if (JOPromotions.has("stateName")) {
                                    promotion.setStateName(JOPromotions.getString("stateName"));
                                } else {
                                    promotion.setStateName(null);
                                }

                                /* GET THE CLINIC CITY ID */
                                if (JOPromotions.has("cityID"))    {
                                    promotion.setCityID(JOPromotions.getString("cityID"));
                                } else {
                                    promotion.setCityID(null);
                                }

                                /* GET THE CLINIC CITY NAME */
                                if (JOPromotions.has("cityName"))  {
                                    promotion.setCityName(JOPromotions.getString("cityName"));
                                } else {
                                    promotion.setCityName(null);
                                }

                                /* GET THE CLINIC LOCALITY ID */
                                if (JOPromotions.has("localityID"))    {
                                    promotion.setLocalityID(JOPromotions.getString("localityID"));
                                } else {
                                    promotion.setLocalityID(null);
                                }

                                /* GET THE CLINIC LOCALITY NAME */
                                if (JOPromotions.has("localityName"))  {
                                    promotion.setLocalityName(JOPromotions.getString("localityName"));
                                } else {
                                    promotion.setLocalityName(null);
                                }

                                /* GET THE CLINIC LATITUDE AND LONGITUDE */
                                if (JOPromotions.has("clinicLatitude") && JOPromotions.has("clinicLongitude"))   {
                                    promotion.setClinicLatitude(JOPromotions.getString("clinicLatitude"));
                                    promotion.setClinicLongitude(JOPromotions.getString("clinicLongitude"));
                                } else {
                                    promotion.setClinicLatitude(null);
                                    promotion.setClinicLongitude(null);
                                }

                                /* GET THE CLINIC DISTANCE */
                                if (JOPromotions.has("clinicDistance")) {
                                    promotion.setClinicDistance(JOPromotions.getString("clinicDistance"));
                                } else {
                                    promotion.setClinicDistance(null);
                                }

                                /* GET THE CURRENCY SYMBOL */
                                if (JOPromotions.has("currencySymbol"))  {
                                    promotion.setCurrencySymbol(JOPromotions.getString("currencySymbol"));
                                } else {
                                    promotion.setCurrencySymbol(null);
                                }

                                /* GET THE DOCTOR'S CHARGES */
                                if (JOPromotions.has("doctorCharges"))  {
                                    promotion.setDoctorCharges(JOPromotions.getString("doctorCharges"));
                                } else {
                                    promotion.setDoctorCharges(null);
                                }

                                /* GET THE TOTAL REVIEWS, POSITIVE, AND FINALLY, CALCULATE THE PERCENTAGES */
                                if (JOPromotions.has("doctorReviews")
                                        && JOPromotions.has("doctorPositives"))  {
                                    String doctorReviews = JOPromotions.getString("doctorReviews");
                                    String doctorPositives = JOPromotions.getString("doctorPositives");

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
                                    promotion.setDoctorVoteStats(getString(R.string.doctor_list_votes_placeholder, strLikesPercentage, open, strVotes, close));
                                } else {
                                    data.setDoctorVoteStats("0");
                                }

                                /* GET THE AVERAGE CLINIC RATING */
                                if (JOPromotions.has("clinicRating"))  {
                                    promotion.setClinicRating(JOPromotions.getString("clinicRating"));
                                } else {
                                    promotion.setClinicRating("0");
                                }

                                /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                                promotions.add(promotion);
                            }
                            data.setPromotions(promotions);
                        } else {
                            data.setPromotions(null);
                        }

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

//    /***** FETCH THE FIRST LIST OF DOCTORS *****/
//    private void fetchDoctors() {
//        DoctorsAPI api = ZenApiClient.getClient().create(DoctorsAPI.class);
//        Call<Doctors> call = api.fetchDoctorsList(
//                FINAL_CITY_ID,
//                FINAL_LOCALITY_ID,
//                String.valueOf(LATLNG_ORIGIN.latitude),
//                String.valueOf(LATLNG_ORIGIN.longitude),
//                String.valueOf(currentPage));
//        call.enqueue(new Callback<Doctors>() {
//            @Override
//            public void onResponse(Call<Doctors> call, Response<Doctors> response) {
//                Log.e("DOCTORS RAW", String.valueOf(response.raw()));
//                if (response.body() != null && response.body().getDoctors() != null)    {
//                    arrDoctors = response.body().getDoctors();
//                    if (arrDoctors.size() > 0)  {
//                        /* SHOW THE DOCTORS RECYCLER AND HIDE THE EMPTY LAYOUT */
//                        listDoctors.setVisibility(View.VISIBLE);
//                        linlaEmpty.setVisibility(View.GONE);
//
//                        /* SET THE DOCTORS RECYCLER VIEW */
//                        listDoctors.setAdapter(new DoctorsListAdapter(DoctorsList.this, arrDoctors, LATLNG_ORIGIN));
//
//                        /* HIDE THE PROGRESS AFTER LOADING THE DATA */
//                        linlaProgress.setVisibility(View.GONE);
//                    } else {
//                        /* SET THE EMPTY TEXT */
//                        txtEmpty.setText(getString(R.string.doctor_list_empty_message));
//
//                        /* HIDE THE RECYCLER AND SHOW THE EMPTY LAYOUT */
//                        listDoctors.setVisibility(View.GONE);
//                        linlaEmpty.setVisibility(View.VISIBLE);
//
//                        /* HIDE THE PROGRESS AFTER LOADING THE DATA */
//                        linlaProgress.setVisibility(View.GONE);
//                    }
//                } else {
//                    /* SET THE EMPTY TEXT */
//                    txtEmpty.setText(getString(R.string.doctor_list_empty_message));
//
//                    /* HIDE THE RECYCLER AND SHOW THE EMPTY LAYOUT */
//                    listDoctors.setVisibility(View.GONE);
//                    linlaEmpty.setVisibility(View.VISIBLE);
//
//                    /* HIDE THE PROGRESS AFTER LOADING THE DATA */
//                    linlaProgress.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Doctors> call, Throwable t) {
//                Log.e("DOCTORS FAILURE", t.getMessage());
//                Crashlytics.logException(t);
//            }
//        });
//    }

    /***** FETCH THE USER'S LOCATION *****/
    private void getUserLocation() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(DoctorsList.this,
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
                                new MaterialDialog.Builder(DoctorsList.this)
                                        .icon(ContextCompat.getDrawable(DoctorsList.this, R.drawable.ic_info_outline_black_24dp))
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
                                                        DoctorsList.this,
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
                                        DoctorsList.this,
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

                                /* INSTANTIATE THE KENNELS ADAPTER */
                                doctorsAdapter = new DoctorsAdapter(DoctorsList.this, arrDoctors, LATLNG_ORIGIN);

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
        Geocoder geocoder = new Geocoder(DoctorsList.this, Locale.getDefault());
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

                                /* SET THE LOCATION */
                                txtLocation.setText(getString(R.string.doctor_list_tb_location_placeholder, DETECTED_LOCALITY, DETECTED_CITY));
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
                        txtEmpty.setText(getString(R.string.doctor_list_location_not_served));

                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listDoctors.setVisibility(View.GONE);
                    }
                } else {
                    /* SET THE ERROR MESSAGE */
                    txtEmpty.setText(getString(R.string.doctor_list_location_not_served));

                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
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
                        /* FETCH THE TOTAL NUMBER OF PAGES */
                        fetchTotalPages();

                        /* FETCH THE LIST OF DOCTORS */
                        fetchDoctors();
                    } else {
                        /* SET THE ERROR MESSAGE */
                        txtEmpty.setText(getString(R.string.doctor_list_location_not_served));

                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listDoctors.setVisibility(View.GONE);
                    }
                } else {
                    /* SET THE ERROR MESSAGE */
                    txtEmpty.setText(getString(R.string.doctor_list_location_not_served));

                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
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
        listDoctors.setAdapter(doctorsAdapter);

        /* CONFIGURE THE SCROLL LISTENER */
        listDoctors.addOnScrollListener(new PaginationScrollListener(manager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                /* FETCH THE NEXT SET OF DOCTORS */
                fetchNextDoctors();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_FINE_LOCATION_CONSTANT)   {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                /* FETCH THE USER'S LOCATION */
                getUserLocation();
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
                                        DoctorsList.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CONSTANT);
                            }
                        }).show();
            }
        }
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setSubtitle(null);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 101)  {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                DETECTED_LOCALITY = bundle.getString("LOCALITY_NAME");
                FINAL_LOCALITY_ID = bundle.getString("LOCALITY_ID");

                /* SET THE LOCATION */
                txtLocation.setText(getString(R.string.doctor_list_tb_location_placeholder, DETECTED_LOCALITY, DETECTED_CITY));

                /* CLEAR THE ARRAY LIST */
                arrDoctors.clear();

                /* FETCH THE TOTAL NUMBER OF PAGES */
                fetchTotalPages();

                /* FETCH THE LIST OF DOCTORS */
                fetchDoctors();
            }
        }
    }

    /** FETCH THE TOTAL NUMBER OF PAGES **/
    private void fetchTotalPages() {
        DoctorsAPI api = ZenApiClient.getClient().create(DoctorsAPI.class);
        Call<DoctorPages> call = api.fetchDoctorPages(FINAL_CITY_ID, FINAL_LOCALITY_ID);
        call.enqueue(new Callback<DoctorPages>() {
            @Override
            public void onResponse(Call<DoctorPages> call, Response<DoctorPages> response) {
                if (response.body() != null && response.body().getTotalPages() != null) {
                    TOTAL_PAGES = Integer.parseInt(response.body().getTotalPages());
                }
            }

            @Override
            public void onFailure(Call<DoctorPages> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }
}