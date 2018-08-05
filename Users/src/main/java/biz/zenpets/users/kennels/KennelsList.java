package biz.zenpets.users.kennels;

import android.Manifest;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import biz.zenpets.users.utils.helpers.classes.PaginationScrollListener;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.kennels.kennels.Kennel;
import biz.zenpets.users.utils.models.kennels.kennels.KennelPages;
import biz.zenpets.users.utils.models.kennels.kennels.Kennels;
import biz.zenpets.users.utils.models.kennels.kennels.KennelsAPI;
import biz.zenpets.users.utils.models.kennels.promotion.Promotion;
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

    /** A LATLNG INSTANCE TO HOLD THE CURRENT COORDINATES **/
    private LatLng LATLNG_ORIGIN;

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int ACCESS_FINE_LOCATION_CONSTANT = 200;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 0;
    private int currentPage = PAGE_START;

    /** THE KENNELS ADAPTER AND ARRAY LIST INSTANCE **/
    private KennelsAdapter kennelsAdapter;
    private ArrayList<Kennel> arrKennels = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtLocation) TextView txtLocation;
    @BindView(R.id.listKennels) RecyclerView listKennels;
    @BindView(R.id.progressLoading) ProgressBar progressLoading;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
    @BindView(R.id.txtEmpty) TextView txtEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kennels_list);
        ButterKnife.bind(this);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* INSTANTIATE THE LOCATION CLIENT */
        locationProviderClient = LocationServices.getFusedLocationProviderClient(KennelsList.this);

        /* FETCH THE USER'S LOCATION */
        getUsersLocation();
    }

    /** FETCH THE FIRST LIST OF KENNELS **/
    private void fetchKennels() {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<Kennels> call = api.fetchKennelsListByCity(
                FINAL_CITY_ID,
                String.valueOf(currentPage),
                String.valueOf(LATLNG_ORIGIN.latitude),
                String.valueOf(LATLNG_ORIGIN.longitude));
        call.enqueue(new Callback<Kennels>() {
            @Override
            public void onResponse(Call<Kennels> call, Response<Kennels> response) {
                if (response.body() != null && response.body().getKennels() != null)    {
//                    Log.e("KENNELS LIST", String.valueOf(response.raw()));
                    /* PROCESS THE RESPONSE */
                    arrKennels = processResult(response);
                    if (arrKennels.size() > 0)  {
                        ArrayList<Kennel> kennels = arrKennels;
//                        Log.e("KENNELS SIZE", String.valueOf(kennels.size()));
                        progressLoading.setVisibility(View.GONE);
                        if (kennels != null && kennels.size() > 0)
                            kennelsAdapter.addAll(kennels);

                        if (currentPage <= TOTAL_PAGES) kennelsAdapter.addLoadingFooter();
                        else isLastPage = true;
                    } else {
                        /* MARK THE LAST PAGE FLAG TO "TRUE" */
                        isLastPage = true;

                        /* HIDE THE PROGRESS */
                        progressLoading.setVisibility(View.GONE);

                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listKennels.setVisibility(View.GONE);
                    }
                } else {
                    /* MARK THE LAST PAGE FLAG TO "TRUE" */
                    isLastPage = true;

                    /* HIDE THE PROGRESS */
                    progressLoading.setVisibility(View.GONE);

                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listKennels.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Kennels> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /** FETCH THE NEXT SET OF KENNELS **/
    private void fetchNextKennels() {
        progressLoading.setVisibility(View.VISIBLE);
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<Kennels> call = api.fetchKennelsListByCity(
                FINAL_CITY_ID,
                String.valueOf(currentPage),
                String.valueOf(LATLNG_ORIGIN.latitude),
                String.valueOf(LATLNG_ORIGIN.longitude));
        call.enqueue(new Callback<Kennels>() {
            @Override
            public void onResponse(Call<Kennels> call, Response<Kennels> response) {
//                Log.e("KENNELS LIST", String.valueOf(response.raw()));
                /* PROCESS THE RESPONSE */
                arrKennels = processResult(response);

                kennelsAdapter.removeLoadingFooter();
                isLoading = false;

                ArrayList<Kennel> kennels = arrKennels;
//                Log.e("KENNELS SIZE", String.valueOf(kennels.size()));
                if (kennels != null && kennels.size() > 0)
                    kennelsAdapter.addAll(kennels);

                if (currentPage != TOTAL_PAGES) kennelsAdapter.addLoadingFooter();
                else isLastPage = true;

                progressLoading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Kennels> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /** PROCESS THE KENNEL RESULTS **/
    private ArrayList<Kennel> processResult(Response<Kennels> response) {
        ArrayList<Kennel> kennels = new ArrayList<>();
        ArrayList<Promotion> promotions = new ArrayList<>();
        try {
            String strResult = new Gson().toJson(response.body());
            JSONObject JORoot = new JSONObject(strResult);
            if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
                JSONArray JAKennels = JORoot.getJSONArray("kennels");
                if (JAKennels.length() > 0) {
                    Kennel data;
                    for (int i = 0; i < JAKennels.length(); i++) {
                        final JSONObject JOKennels = JAKennels.getJSONObject(i);
                        data = new Kennel();

                        /* GET THE PROMOTED KENNELS */
                        JSONArray JAPromotions = JOKennels.getJSONArray("promotions");
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

                                /* GET THE ADOPTION ID */
                                if (JOPromotions.has("kennelID")) {
                                    promotion.setKennelID(JOPromotions.getString("kennelID"));
                                } else {
                                    promotion.setKennelID(null);
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

                                /* GET THE KENNEL ID */
                                if (JOPromotions.has("kennelID"))  {
                                    promotion.setKennelID(JOPromotions.getString("kennelID"));
                                } else {
                                    promotion.setKennelID(null);
                                }

                                /* GET THE KENNEL COVER PHOTO */
                                if (JOPromotions.has("kennelCoverPhoto")
                                        && !JOPromotions.getString("kennelCoverPhoto").equalsIgnoreCase("")
                                        && !JOPromotions.getString("kennelCoverPhoto").equalsIgnoreCase("null"))  {
                                    promotion.setKennelCoverPhoto(JOPromotions.getString("kennelCoverPhoto"));
                                } else {
                                    promotion.setKennelCoverPhoto(null);
                                }

                                /* GET THE KENNEL NAME */
                                if (JOPromotions.has("kennelName"))    {
                                    promotion.setKennelName(JOPromotions.getString("kennelName"));
                                } else {
                                    promotion.setKennelName(null);
                                }

                                /* GET THE KENNEL OWNER'S ID */
                                if (JOPromotions.has("kennelOwnerID")) {
                                    promotion.setKennelOwnerID(JOPromotions.getString("kennelOwnerID"));
                                } else {
                                    promotion.setKennelOwnerID(null);
                                }

                                /* GET THE KENNEL OWNER'S NAME */
                                if (JOPromotions.has("kennelOwnerName"))   {
                                    promotion.setKennelOwnerName(JOPromotions.getString("kennelOwnerName"));
                                } else {
                                    promotion.setKennelOwnerName(null);
                                }

                                /* GET THE KENNEL OWNER'S DISPLAY PROFILE */
                                if (JOPromotions.has("kennelOwnerDisplayProfile")) {
                                    promotion.setKennelOwnerDisplayProfile(JOPromotions.getString("kennelOwnerDisplayProfile"));
                                } else {
                                    promotion.setKennelOwnerDisplayProfile(null);
                                }

                                /* GET THE KENNEL ADDRESS */
                                if (JOPromotions.has("kennelAddress")) {
                                    promotion.setKennelAddress(JOPromotions.getString("kennelAddress"));
                                } else {
                                    promotion.setKennelAddress(null);
                                }

                                /* GET THE KENNEL PIN CODE */
                                if (JOPromotions.has("kennelPinCode")) {
                                    promotion.setKennelPinCode(JOPromotions.getString("kennelPinCode"));
                                } else {
                                    promotion.setKennelPinCode(null);
                                }

                                /* GET THE KENNEL COUNTY ID */
                                if (JOPromotions.has("countryID")) {
                                    promotion.setCountryID(JOPromotions.getString("countryID"));
                                } else {
                                    promotion.setCountryID(null);
                                }

                                /* GET THE KENNEL COUNTRY NAME */
                                if (JOPromotions.has("countryName"))   {
                                    promotion.setCountryName(JOPromotions.getString("countryName"));
                                } else {
                                    promotion.setCountryName(null);
                                }

                                /* GET THE KENNEL STATE ID */
                                if (JOPromotions.has("stateID"))   {
                                    promotion.setStateID(JOPromotions.getString("stateID"));
                                } else {
                                    promotion.setStateID(null);
                                }

                                /* GET THE KENNEL STATE NAME */
                                if (JOPromotions.has("stateName")) {
                                    promotion.setStateName(JOPromotions.getString("stateName"));
                                } else {
                                    promotion.setStateName(null);
                                }

                                /* GET THE KENNEL CITY ID */
                                if (JOPromotions.has("cityID"))    {
                                    promotion.setCityID(JOPromotions.getString("cityID"));
                                } else {
                                    promotion.setCityID(null);
                                }

                                /* GET THE KENNEL CITY NAME */
                                if (JOPromotions.has("cityName"))  {
                                    promotion.setCityName(JOPromotions.getString("cityName"));
                                } else {
                                    promotion.setCityName(null);
                                }

                                /* GET THE KENNEL LATITUDE AND LONGITUDE */
                                if (JOPromotions.has("kennelLatitude") && JOPromotions.has("kennelLongitude"))   {
                                    promotion.setKennelLatitude(JOPromotions.getString("kennelLatitude"));
                                    promotion.setKennelLongitude(JOPromotions.getString("kennelLongitude"));
                                } else {
                                    promotion.setKennelLatitude(null);
                                    promotion.setKennelLongitude(null);
                                }

                                /* GET THE KENNEL DISTANCE */
                                if (JOPromotions.has("kennelDistance")) {
                                    promotion.setKennelDistance(JOPromotions.getString("kennelDistance"));
                                } else {
                                    promotion.setKennelDistance(null);
                                }

                                /* GET THE KENNEL'S PHONE PREFIX #1*/
                                if (JOPromotions.has("kennelPhonePrefix1"))    {
                                    promotion.setKennelPhonePrefix1(JOPromotions.getString("kennelPhonePrefix1"));
                                } else {
                                    promotion.setKennelPhonePrefix1(null);
                                }

                                /* GET THE KENNEL'S PHONE NUMBER #1*/
                                if (JOPromotions.has("kennelPhoneNumber1"))    {
                                    promotion.setKennelPhoneNumber1(JOPromotions.getString("kennelPhoneNumber1"));
                                } else {
                                    promotion.setKennelPhoneNumber1(null);
                                }

                                /* GET THE KENNEL'S PHONE PREFIX #2*/
                                if (JOPromotions.has("kennelPhonePrefix2"))    {
                                    promotion.setKennelPhonePrefix2(JOPromotions.getString("kennelPhonePrefix2"));
                                } else {
                                    promotion.setKennelPhonePrefix2(null);
                                }

                                /* GET THE KENNEL'S PHONE NUMBER #2*/
                                if (JOPromotions.has("kennelPhoneNumber2"))    {
                                    promotion.setKennelPhoneNumber2(JOPromotions.getString("kennelPhoneNumber2"));
                                } else {
                                    promotion.setKennelPhoneNumber1(null);
                                }

                                /* GET THE KENNEL'S PET CAPACITY */
                                if (JOPromotions.has("kennelPetCapacity"))    {
                                    promotion.setKennelPetCapacity(JOPromotions.getString("kennelPetCapacity"));
                                } else {
                                    promotion.setKennelPetCapacity(null);
                                }

                                /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                                promotions.add(promotion);
                            }
                            data.setPromotions(promotions);
                        } else {
                            data.setPromotions(null);
                        }

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

                        /* GET THE KENNEL LATITUDE AND LONGITUDE */
                        if (JOKennels.has("kennelLatitude") && JOKennels.has("kennelLongitude"))   {
                            data.setKennelLatitude(JOKennels.getString("kennelLatitude"));
                            data.setKennelLongitude(JOKennels.getString("kennelLongitude"));
                        } else {
                            data.setKennelLatitude(null);
                            data.setKennelLongitude(null);
                        }

                        /* GET THE KENNEL DISTANCE */
                        if (JOKennels.has("kennelDistance")) {
                            data.setKennelDistance(JOKennels.getString("kennelDistance"));
                        } else {
                            data.setKennelDistance(null);
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

                        /* GET THE TOTAL REVIEWS, POSITIVE, AND FINALLY, CALCULATE THE PERCENTAGES */
                        if (JOKennels.has("kennelReviews")
                                && JOKennels.has("kennelPositives"))  {
                            String kennelReviews = JOKennels.getString("kennelReviews");
                            String kennelPositives = JOKennels.getString("kennelPositives");

                            int TOTAL_VOTES = Integer.parseInt(kennelReviews);
                            int TOTAL_LIKES = Integer.parseInt(kennelPositives);

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
                            data.setKennelVoteStats(getString(R.string.doctor_list_votes_placeholder, strLikesPercentage, open, strVotes, close));
                        } else {
                            data.setKennelReviews("0");
                        }

                        /* GET THE AVERAGE KENNEL RATING */
                        if (JOKennels.has("kennelRating"))  {
                            data.setKennelRating(JOKennels.getString("kennelRating"));
                        } else {
                            data.setKennelRating("0");
                        }

                        /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                        kennels.add(data);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
//            Log.e("EXCEPTION", e.getMessage());
            Crashlytics.logException(e);
        }
        return kennels;
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

                                /* INSTANTIATE THE KENNELS ADAPTER */
                                kennelsAdapter = new KennelsAdapter(KennelsList.this, arrKennels, LATLNG_ORIGIN);

                                /* CONFIGURE THE RECYCLER VIEW **/
                                configRecycler();

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
        progressLoading.setVisibility(View.VISIBLE);

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
                        /* FETCH THE TOTAL NUMBER OF PAGES */
                        fetchTotalPages();

                        /* FETCH THE FIRST LIST OF KENNELS */
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
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listKennels.setLayoutManager(manager);
        listKennels.setHasFixedSize(true);

        /* SET THE ADAPTER */
        listKennels.setAdapter(kennelsAdapter);

        /* CONFIGURE THE SCROLL LISTENER */
        listKennels.addOnScrollListener(new PaginationScrollListener(manager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                /* FETCH THE NEXT SET OF KENNELS */
                fetchNextKennels();
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

    /** FETCH THE TOTAL NUMBER OF PAGES **/
    private void fetchTotalPages() {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<KennelPages> call = api.fetchKennelPages(FINAL_CITY_ID);
        call.enqueue(new Callback<KennelPages>() {
            @Override
            public void onResponse(Call<KennelPages> call, Response<KennelPages> response) {
                if (response.body() != null && response.body().getTotalPages() != null) {
                    TOTAL_PAGES = Integer.parseInt(response.body().getTotalPages());
//                    Log.e("TOTAL PAGES", String.valueOf(TOTAL_PAGES));
                }
            }

            @Override
            public void onFailure(Call<KennelPages> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }
}