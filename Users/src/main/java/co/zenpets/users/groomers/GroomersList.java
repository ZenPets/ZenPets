package co.zenpets.users.groomers;

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

import co.zenpets.users.R;
import co.zenpets.users.utils.adapters.groomers.GroomersAdapter;
import co.zenpets.users.utils.helpers.classes.PaginationScrollListener;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.groomers.groomers.Groomer;
import co.zenpets.users.utils.models.groomers.groomers.GroomerPages;
import co.zenpets.users.utils.models.groomers.groomers.Groomers;
import co.zenpets.users.utils.models.groomers.groomers.GroomersAPI;
import co.zenpets.users.utils.models.groomers.promotion.Promotion;
import co.zenpets.users.utils.models.location.City;
import co.zenpets.users.utils.models.location.LocationsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroomersList extends AppCompatActivity {

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

    /** THE GROOMERS ADAPTER AND ARRAY LIST INSTANCE **/
    private GroomersAdapter groomersAdapter;
    private ArrayList<Groomer> arrGroomers = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtLocation) TextView txtLocation;
    @BindView(R.id.listGroomers) RecyclerView listGroomers;
    @BindView(R.id.progressLoading) ProgressBar progressLoading;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
    @BindView(R.id.txtEmpty) TextView txtEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groomers_list);
        ButterKnife.bind(this);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* INSTANTIATE THE LOCATION CLIENT */
        locationProviderClient = LocationServices.getFusedLocationProviderClient(GroomersList.this);

        /* FETCH THE USER'S LOCATION */
        getUsersLocation();
    }

    /** FETCH THE FIRST LIST OF GROOMERS **/
    private void fetchGroomers() {
        GroomersAPI api = ZenApiClient.getClient().create(GroomersAPI.class);
        Call<Groomers> call = api.fetchGroomersList(
                FINAL_CITY_ID,
                String.valueOf(LATLNG_ORIGIN.latitude),
                String.valueOf(LATLNG_ORIGIN.longitude),
                String.valueOf(currentPage));
        call.enqueue(new Callback<Groomers>() {
            @Override
            public void onResponse(Call<Groomers> call, Response<Groomers> response) {
//                Log.e("GROOMERS LIST", String.valueOf(response.raw()));
                if (response.body() != null && response.body().getGroomers() != null)    {
                    /* PROCESS THE RESPONSE */
                    arrGroomers = processResult(response);
                    if (arrGroomers.size() > 0)  {
                        ArrayList<Groomer> groomers = arrGroomers;
//                        Log.e("KENNELS SIZE", String.valueOf(kennels.size()));
                        progressLoading.setVisibility(View.GONE);
                        if (groomers != null && groomers.size() > 0)
                            groomersAdapter.addAll(groomers);

                        if (currentPage <= TOTAL_PAGES) groomersAdapter.addLoadingFooter();
                        else isLastPage = true;
                    } else {
                        /* MARK THE LAST PAGE FLAG TO "TRUE" */
                        isLastPage = true;

                        /* HIDE THE PROGRESS */
                        progressLoading.setVisibility(View.GONE);

                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listGroomers.setVisibility(View.GONE);
                    }
                } else {
                    /* MARK THE LAST PAGE FLAG TO "TRUE" */
                    isLastPage = true;

                    /* HIDE THE PROGRESS */
                    progressLoading.setVisibility(View.GONE);

                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listGroomers.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Groomers> call, Throwable t) {
//                Log.e("KENNELS FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /** FETCH THE NEXT SET OF GROOMERS **/
    private void fetchNextGroomers() {
        progressLoading.setVisibility(View.VISIBLE);
        GroomersAPI api = ZenApiClient.getClient().create(GroomersAPI.class);
        Call<Groomers> call = api.fetchGroomersList(
                FINAL_CITY_ID,
                String.valueOf(LATLNG_ORIGIN.latitude),
                String.valueOf(LATLNG_ORIGIN.longitude),
                String.valueOf(currentPage));
        call.enqueue(new Callback<Groomers>() {
            @Override
            public void onResponse(Call<Groomers> call, Response<Groomers> response) {
//                Log.e("GROOMERS LIST", String.valueOf(response.raw()));
                /* PROCESS THE RESPONSE */
                arrGroomers = processResult(response);

                groomersAdapter.removeLoadingFooter();
                isLoading = false;

                ArrayList<Groomer> groomers = arrGroomers;
//                Log.e("KENNELS SIZE", String.valueOf(kennels.size()));
                if (groomers != null && groomers.size() > 0)
                    groomersAdapter.addAll(groomers);

                if (currentPage != TOTAL_PAGES) groomersAdapter.addLoadingFooter();
                else isLastPage = true;

                progressLoading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Groomers> call, Throwable t) {
//                Log.e("KENNELS FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /** PROCESS THE KENNEL RESULTS **/
    private ArrayList<Groomer> processResult(Response<Groomers> response) {
        ArrayList<Groomer> groomers = new ArrayList<>();
        ArrayList<Promotion> promotions = new ArrayList<>();
        try {
            String strResult = new Gson().toJson(response.body());
            JSONObject JORoot = new JSONObject(strResult);
            if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
                JSONArray JAGroomers = JORoot.getJSONArray("groomers");
//                Log.e("KENNELS", JAKennels.toString());
                if (JAGroomers.length() > 0) {
                    Groomer data;
                    for (int i = 0; i < JAGroomers.length(); i++) {
                        final JSONObject JOGroomers = JAGroomers.getJSONObject(i);
                        data = new Groomer();

                        /* GET THE PROMOTED GROOMERS */
                        JSONArray JAPromotions = JOGroomers.getJSONArray("promotions");
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

                                /* GET THE GROOMER ID */
                                if (JOPromotions.has("groomerID")) {
                                    promotion.setGroomerID(JOPromotions.getString("groomerID"));
                                } else {
                                    promotion.setGroomerID(null);
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

                                /* GET THE GROOMER NAME */
                                if (JOPromotions.has("groomerName"))  {
                                    promotion.setGroomerName(JOPromotions.getString("groomerName"));
                                } else {
                                    promotion.setGroomerName(null);
                                }

                                /* GET THE GROOMER LOGO */
                                if (JOPromotions.has("groomerLogo")
                                        && !JOPromotions.getString("groomerLogo").equalsIgnoreCase("")
                                        && !JOPromotions.getString("groomerLogo").equalsIgnoreCase("null"))  {
                                    promotion.setGroomerLogo(JOPromotions.getString("groomerLogo"));
                                } else {
                                    promotion.setGroomerLogo(null);
                                }

                                /* GET THE CONTACT NAME */
                                if (JOPromotions.has("contactName"))    {
                                    promotion.setContactName(JOPromotions.getString("contactName"));
                                } else {
                                    promotion.setContactName(null);
                                }

                                /* GET THE CONTACT EMAIL */
                                if (JOPromotions.has("contactEmail")) {
                                    promotion.setContactEmail(JOPromotions.getString("contactEmail"));
                                } else {
                                    promotion.setContactEmail(null);
                                }

                                /* GET THE GROOMER'S PHONE PREFIX #1*/
                                if (JOPromotions.has("groomerPhonePrefix1"))    {
                                    promotion.setGroomerPhonePrefix1(JOPromotions.getString("groomerPhonePrefix1"));
                                } else {
                                    promotion.setGroomerPhonePrefix1(null);
                                }

                                /* GET THE GROOMER'S PHONE NUMBER #1*/
                                if (JOPromotions.has("groomerPhoneNumber1"))    {
                                    promotion.setGroomerPhoneNumber1(JOPromotions.getString("groomerPhoneNumber1"));
                                } else {
                                    promotion.setGroomerPhoneNumber1(null);
                                }

                                /* GET THE GROOMER'S PHONE PREFIX #2*/
                                if (JOPromotions.has("groomerPhonePrefix2"))    {
                                    promotion.setGroomerPhonePrefix2(JOPromotions.getString("groomerPhonePrefix2"));
                                } else {
                                    promotion.setGroomerPhonePrefix2(null);
                                }

                                /* GET THE GROOMER'S PHONE NUMBER #2*/
                                if (JOPromotions.has("groomerPhoneNumber2"))    {
                                    promotion.setGroomerPhoneNumber2(JOPromotions.getString("groomerPhoneNumber2"));
                                } else {
                                    promotion.setGroomerPhoneNumber2(null);
                                }

                                /* GET THE GROOMER'S ADDRESS */
                                if (JOPromotions.has("groomerAddress"))   {
                                    promotion.setGroomerAddress(JOPromotions.getString("groomerAddress"));
                                } else {
                                    promotion.setGroomerAddress(null);
                                }

                                /* GET THE GROOMERS'S PIN CODE */
                                if (JOPromotions.has("groomerPincode")) {
                                    promotion.setGroomerPincode(JOPromotions.getString("groomerPincode"));
                                } else {
                                    promotion.setGroomerPincode(null);
                                }

                                /* GET THE GROOMER'S COUNTY ID */
                                if (JOPromotions.has("countryID")) {
                                    promotion.setCountryID(JOPromotions.getString("countryID"));
                                } else {
                                    promotion.setCountryID(null);
                                }

                                /* GET THE GROOMER'S COUNTRY NAME */
                                if (JOPromotions.has("countryName"))   {
                                    promotion.setCountryName(JOPromotions.getString("countryName"));
                                } else {
                                    promotion.setCountryName(null);
                                }

                                /* GET THE GROOMER'S STATE ID */
                                if (JOPromotions.has("stateID"))   {
                                    promotion.setStateID(JOPromotions.getString("stateID"));
                                } else {
                                    promotion.setStateID(null);
                                }

                                /* GET THE GROOMER'S STATE NAME */
                                if (JOPromotions.has("stateName")) {
                                    promotion.setStateName(JOPromotions.getString("stateName"));
                                } else {
                                    promotion.setStateName(null);
                                }

                                /* GET THE GROOMER'S CITY ID */
                                if (JOPromotions.has("cityID"))    {
                                    promotion.setCityID(JOPromotions.getString("cityID"));
                                } else {
                                    promotion.setCityID(null);
                                }

                                /* GET THE GROOMER'S CITY NAME */
                                if (JOPromotions.has("cityName"))  {
                                    promotion.setCityName(JOPromotions.getString("cityName"));
                                } else {
                                    promotion.setCityName(null);
                                }

                                /* GET THE GROOMER'S LATITUDE AND LONGITUDE */
                                if (JOPromotions.has("groomerLatitude") && JOPromotions.has("groomerLongitude"))   {
                                    promotion.setGroomerLatitude(JOPromotions.getString("groomerLatitude"));
                                    promotion.setGroomerLongitude(JOPromotions.getString("groomerLongitude"));
                                } else {
                                    promotion.setGroomerLatitude(null);
                                    promotion.setGroomerLongitude(null);
                                }

                                /* GET THE GROOMER'S DISTANCE */
                                if (JOPromotions.has("groomerDistance")) {
                                    promotion.setGroomerDistance(JOPromotions.getString("groomerDistance"));
                                } else {
                                    promotion.setGroomerDistance(null);
                                }

                                /* GET THE GROOMER VALID FROM AND TO */
                                if (JOPromotions.has("groomerValidFrom") && JOPromotions.has("groomerValidTo"))   {
                                    promotion.setGroomerValidFrom(JOPromotions.getString("groomerValidFrom"));
                                    promotion.setGroomerValidTo(JOPromotions.getString("groomerValidTo"));
                                } else {
                                    promotion.setGroomerValidFrom(null);
                                    promotion.setGroomerValidTo(null);
                                }

                                /* GET THE GROOMER TOKEN */
                                if (JOPromotions.has("groomerToken")) {
                                    promotion.setGroomerToken(JOPromotions.getString("groomerToken"));
                                } else {
                                    promotion.setGroomerToken(null);
                                }

                                /* GET THE GROOMER VERIFIED STATUS */
                                if (JOPromotions.has("groomerVerified"))  {
                                    promotion.setGroomerVerified(JOPromotions.getString("groomerVerified"));
                                } else {
                                    promotion.setGroomerVerified(null);
                                }

                                /* GET THE TOTAL REVIEWS, POSITIVE, AND FINALLY, CALCULATE THE PERCENTAGES */
                                if (JOPromotions.has("groomerReviews")
                                        && JOPromotions.has("groomerPositives"))  {
                                    String groomerReviews = JOPromotions.getString("groomerReviews");
                                    String groomerPositives = JOPromotions.getString("groomerPositives");

                                    int TOTAL_VOTES = Integer.parseInt(groomerReviews);
                                    int TOTAL_LIKES = Integer.parseInt(groomerPositives);

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
                                    promotion.setGroomerVoteStats(getString(R.string.doctor_list_votes_placeholder, strLikesPercentage, open, strVotes, close));
                                } else {
                                    promotion.setGroomerVoteStats("0 Votes");
                                }

                                /* GET THE AVERAGE GROOMER'S RATING */
                                if (JOPromotions.has("groomerRating"))  {
                                    promotion.setGroomerRating(JOPromotions.getString("groomerRating"));
                                } else {
                                    promotion.setGroomerRating("0");
                                }

                                /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                                promotions.add(promotion);
                            }
                            data.setPromotions(promotions);
                        } else {
                            data.setPromotions(null);
                        }

                        /* GET THE GROOMER ID */
                        if (JOGroomers.has("groomerID"))  {
                            data.setGroomerID(JOGroomers.getString("groomerID"));
                        } else {
                            data.setGroomerID(null);
                        }

                        /* GET THE GROOMER NAME */
                        if (JOGroomers.has("groomerName"))    {
                            data.setGroomerName(JOGroomers.getString("groomerName"));
                        } else {
                            data.setGroomerName(null);
                        }

                        /* GET THE GROOMER LOGO */
                        if (JOGroomers.has("groomerLogo")
                                && !JOGroomers.getString("groomerLogo").equalsIgnoreCase("")
                                && !JOGroomers.getString("groomerLogo").equalsIgnoreCase("null"))  {
                            data.setGroomerLogo(JOGroomers.getString("groomerLogo"));
                        } else {
                            data.setGroomerLogo(null);
                        }

                        /* GET THE GROOMER CONTACT NAME */
                        if (JOGroomers.has("contactName")) {
                            data.setContactName(JOGroomers.getString("contactName"));
                        } else {
                            data.setContactName(null);
                        }

                        /* GET THE GROOMER CONTACT EMAIL */
                        if (JOGroomers.has("contactEmail"))   {
                            data.setContactEmail(JOGroomers.getString("contactEmail"));
                        } else {
                            data.setContactEmail(null);
                        }

                        /* GET THE GROOMER'S PHONE PREFIX #1*/
                        if (JOGroomers.has("groomerPhonePrefix1"))    {
                            data.setGroomerPhonePrefix1(JOGroomers.getString("groomerPhonePrefix1"));
                        } else {
                            data.setGroomerPhonePrefix1(null);
                        }

                        /* GET THE GROOMER'S PHONE NUMBER #1*/
                        if (JOGroomers.has("groomerPhoneNumber1"))    {
                            data.setGroomerPhoneNumber1(JOGroomers.getString("groomerPhoneNumber1"));
                        } else {
                            data.setGroomerPhoneNumber1(null);
                        }

                        /* GET THE GROOMER'S PHONE PREFIX #2*/
                        if (JOGroomers.has("groomerPhonePrefix2"))    {
                            data.setGroomerPhonePrefix2(JOGroomers.getString("groomerPhonePrefix2"));
                        } else {
                            data.setGroomerPhonePrefix2(null);
                        }

                        /* GET THE GROOMER'S PHONE NUMBER #2*/
                        if (JOGroomers.has("groomerPhoneNumber2"))    {
                            data.setGroomerPhoneNumber2(JOGroomers.getString("groomerPhoneNumber2"));
                        } else {
                            data.setGroomerPhoneNumber2(null);
                        }

                        /* GET THE GROOMER ADDRESS */
                        if (JOGroomers.has("groomerAddress")) {
                            data.setGroomerAddress(JOGroomers.getString("groomerAddress"));
                        } else {
                            data.setGroomerAddress(null);
                        }

                        /* GET THE GROOMER PIN CODE */
                        if (JOGroomers.has("groomerPincode")) {
                            data.setGroomerPincode(JOGroomers.getString("groomerPincode"));
                        } else {
                            data.setGroomerPincode(null);
                        }

                        /* GET THE GROOMER'S COUNTY ID */
                        if (JOGroomers.has("countryID")) {
                            data.setCountryID(JOGroomers.getString("countryID"));
                        } else {
                            data.setCountryID(null);
                        }

                        /* GET THE GROOMER'S COUNTRY NAME */
                        if (JOGroomers.has("countryName"))   {
                            data.setCountryName(JOGroomers.getString("countryName"));
                        } else {
                            data.setCountryName(null);
                        }

                        /* GET THE GROOMER'S STATE ID */
                        if (JOGroomers.has("stateID"))   {
                            data.setStateID(JOGroomers.getString("stateID"));
                        } else {
                            data.setStateID(null);
                        }

                        /* GET THE GROOMER'S STATE NAME */
                        if (JOGroomers.has("stateName")) {
                            data.setStateName(JOGroomers.getString("stateName"));
                        } else {
                            data.setStateName(null);
                        }

                        /* GET THE GROOMER'S CITY ID */
                        if (JOGroomers.has("cityID"))    {
                            data.setCityID(JOGroomers.getString("cityID"));
                        } else {
                            data.setCityID(null);
                        }

                        /* GET THE GROOMER'S CITY NAME */
                        if (JOGroomers.has("cityName"))  {
                            data.setCityName(JOGroomers.getString("cityName"));
                        } else {
                            data.setCityName(null);
                        }

                        /* GET THE GROOMER'S LATITUDE AND LONGITUDE */
                        if (JOGroomers.has("groomerLatitude") && JOGroomers.has("groomerLongitude"))   {
                            data.setGroomerLatitude(JOGroomers.getString("groomerLatitude"));
                            data.setGroomerLongitude(JOGroomers.getString("groomerLongitude"));
                        } else {
                            data.setGroomerLatitude(null);
                            data.setGroomerLongitude(null);
                        }

                        /* GET THE GROOMER DISTANCE */
                        if (JOGroomers.has("groomerDistance")) {
                            data.setGroomerDistance(JOGroomers.getString("groomerDistance"));
                        } else {
                            data.setGroomerDistance(null);
                        }

                        /* GET THE GROOMER VALID FROM AND TO */
                        if (JOGroomers.has("groomerValidFrom") && JOGroomers.has("groomerValidTo"))   {
                            data.setGroomerValidFrom(JOGroomers.getString("groomerValidFrom"));
                            data.setGroomerValidTo(JOGroomers.getString("groomerValidTo"));
                        } else {
                            data.setGroomerValidFrom(null);
                            data.setGroomerValidTo(null);
                        }

                        /* GET THE GROOMER TOKEN */
                        if (JOGroomers.has("groomerToken")) {
                            data.setGroomerToken(JOGroomers.getString("groomerToken"));
                        } else {
                            data.setGroomerToken(null);
                        }

                        /* GET THE GROOMER VERIFIED STATUS */
                        if (JOGroomers.has("groomerVerified"))  {
                            data.setGroomerVerified(JOGroomers.getString("groomerVerified"));
                        } else {
                            data.setGroomerVerified(null);
                        }

                        /* GET THE TOTAL REVIEWS, POSITIVE, AND FINALLY, CALCULATE THE PERCENTAGES */
                        if (JOGroomers.has("groomerReviews")
                                && JOGroomers.has("groomerPositives"))  {
                            String groomerReviews = JOGroomers.getString("groomerReviews");
                            String groomerPositives = JOGroomers.getString("groomerPositives");

                            int TOTAL_VOTES = Integer.parseInt(groomerReviews);
                            int TOTAL_LIKES = Integer.parseInt(groomerPositives);

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
                            data.setGroomerVoteStats(getString(R.string.doctor_list_votes_placeholder, strLikesPercentage, open, strVotes, close));
                        } else {
                            data.setGroomerVoteStats("0 Votes");
                        }

                        /* GET THE AVERAGE GROOMER'S RATING */
                        if (JOGroomers.has("groomerRating"))  {
                            data.setGroomerRating(JOGroomers.getString("groomerRating"));
                        } else {
                            data.setGroomerRating("0");
                        }

                        /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                        groomers.add(data);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
//            Log.e("EXCEPTION", e.getMessage());
//            Crashlytics.logException(e);
        }
        return groomers;
    }

    /***** FETCH THE USER'S LOCATION *****/
    private void getUsersLocation() {
        /* CHECK FOR PERMISSION STATUS */
            if (ContextCompat.checkSelfPermission(GroomersList.this,
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
                                        GroomersList.this,
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

                                /* INSTANTIATE THE GROOMERS ADAPTER */
                                groomersAdapter = new GroomersAdapter(GroomersList.this, arrGroomers, LATLNG_ORIGIN);

                                /* CONFIGURE THE RECYCLER VIEW **/
                                configRecycler();

                                /* FETCH THE LOCATION USING A GEOCODER */
                                fetchLocation();
                            } else {
//                                Crashlytics.logException(task.getException());
                            }
                        }
                    });
        }
    }

    /***** FETCH THE LOCATION USING A GEOCODER *****/
    private void fetchLocation() {
        Geocoder geocoder = new Geocoder(GroomersList.this, Locale.getDefault());
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
                    } else {
                        new MaterialDialog.Builder(GroomersList.this)
                                .title("Location not Served!")
                                .content("We currently do not serve this City. but fear not. We will have you covered shortly.")
                                .positiveText("OKAY")
                                .theme(Theme.LIGHT)
                                .icon(ContextCompat.getDrawable(GroomersList.this, R.drawable.ic_info_outline_black_24dp))
                                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                .show();
                    }
                } else {
                    new MaterialDialog.Builder(GroomersList.this)
                            .title("Location not Served!")
                            .content("We currently do not serve this City. but fear not. We will have you covered shortly.")
                            .positiveText("OKAY")
                            .theme(Theme.LIGHT)
                            .icon(ContextCompat.getDrawable(GroomersList.this, R.drawable.ic_info_outline_black_24dp))
                            .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<City> call, Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
//                Crashlytics.logException(t);
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
                                        GroomersList.this,
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
        listGroomers.setLayoutManager(manager);
        listGroomers.setHasFixedSize(true);

        /* SET THE ADAPTER */
        listGroomers.setAdapter(groomersAdapter);

        /* CONFIGURE THE SCROLL LISTENER */
        listGroomers.addOnScrollListener(new PaginationScrollListener(manager) {
            @Override
            protected void loadMoreItems() {
                if (currentPage < TOTAL_PAGES)  {
                    isLoading = true;
                    currentPage += 1;

                    /* FETCH THE NEXT SET OF GROOMERS */
                    fetchNextGroomers();
                }
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
        GroomersAPI api = ZenApiClient.getClient().create(GroomersAPI.class);
        Call<GroomerPages> call = api.fetchGroomerPages(FINAL_CITY_ID);
        call.enqueue(new Callback<GroomerPages>() {
            @Override
            public void onResponse(Call<GroomerPages> call, Response<GroomerPages> response) {
//                Log.e("TOTAL PAGES", String.valueOf(response.raw()));
                if (response.body() != null && response.body().getTotalPages() != null) {
                    TOTAL_PAGES = Integer.parseInt(response.body().getTotalPages());
//                    Log.e("TOTAL PAGES", String.valueOf(TOTAL_PAGES));

                    /* FETCH THE FIRST LIST OF GROOMERS */
                    fetchGroomers();
                }
            }

            @Override
            public void onFailure(Call<GroomerPages> call, Throwable t) {
//                Crashlytics.logException(t);
            }
        });
    }
}