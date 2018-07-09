package biz.zenpets.users.adoptions;

import android.Manifest;
import android.content.Intent;
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
import biz.zenpets.users.utils.adapters.adoptions.NewAdoptionsAdapter;
import biz.zenpets.users.utils.helpers.classes.FilterAdoptionsActivity;
import biz.zenpets.users.utils.helpers.classes.PaginationScrollListener;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.adoptions.adoption.Adoption;
import biz.zenpets.users.utils.models.adoptions.adoption.AdoptionPages;
import biz.zenpets.users.utils.models.adoptions.adoption.Adoptions;
import biz.zenpets.users.utils.models.adoptions.adoption.AdoptionsAPI;
import biz.zenpets.users.utils.models.adoptions.promotion.Promotion;
import biz.zenpets.users.utils.models.location.City;
import biz.zenpets.users.utils.models.location.LocationsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestAdoptionsList extends AppCompatActivity {

    /** A FUSED LOCATION PROVIDER CLIENT INSTANCE**/
    private FusedLocationProviderClient locationProviderClient;

    /** A LOCATION INSTANCE **/
    private Location location;

    /** STRING TO HOLD THE DETECTED CITY NAME FOR QUERYING THE ADOPTIONS **/
    private String DETECTED_CITY = null;
    private String FINAL_CITY_ID = null;

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int ACCESS_FINE_LOCATION_CONSTANT = 200;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 0;
    private int currentPage = PAGE_START;

    /** THE PROMOTED ADOPTIONS ADAPTER AND ADOPTIONS ARRAY LIST INSTANCES **/
    private NewAdoptionsAdapter adoptionsAdapter;
    private ArrayList<Adoption> arrAdoptions = new ArrayList<>();

    /** THE ADOPTION FILTER STRINGS **/
    private String FILTER_PET_GENDER = null;
    private String FILTER_PET_SPECIES = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtLocation) AppCompatTextView txtLocation;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listAdoptions) RecyclerView listAdoptions;
    @BindView(R.id.progressLoading) ProgressBar progressLoading;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** FILTER THE ADOPTION LISTINGS **/
    @OnClick(R.id.fabFilterAdoptions) protected void filterAdoptions()  {
        Intent intent = new Intent(TestAdoptionsList.this, FilterAdoptionsActivity.class);
        intent.putExtra("PET_GENDER", FILTER_PET_GENDER);
        intent.putExtra("PET_SPECIES", FILTER_PET_SPECIES);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adoptions_list);
        ButterKnife.bind(this);

        /* INSTANTIATE THE ADOPTION ADAPTER */
        adoptionsAdapter = new NewAdoptionsAdapter(TestAdoptionsList.this, arrAdoptions);

        /* INSTANTIATE THE LOCATION CLIENT */
        locationProviderClient = LocationServices.getFusedLocationProviderClient(TestAdoptionsList.this);

        /* FETCH THE USER'S LOCATION */
        getUsersLocation();

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* CONFIGURE THE RECYCLER VIEW **/
        configRecycler();
    }

    /* FETCH THE ADOPTION LISTINGS */
    private void fetchAdoptions() {
        AdoptionsAPI api = ZenApiClient.getClient().create(AdoptionsAPI.class);
        Call<Adoptions> call = api.fetchTestAdoptions(FINAL_CITY_ID, FILTER_PET_SPECIES, FILTER_PET_GENDER, String.valueOf(currentPage));
        call.enqueue(new Callback<Adoptions>() {
            @Override
            public void onResponse(Call<Adoptions> call, Response<Adoptions> response) {
//                Log.e("ADOPTIONS LIST", String.valueOf(response.raw()));
                /* PROCESS THE RESPONSE */
                arrAdoptions = processResult(response);

                ArrayList<Adoption> adoptions = arrAdoptions;
                progressLoading.setVisibility(View.GONE);
                adoptionsAdapter.addAll(adoptions);

                if (currentPage <= TOTAL_PAGES) adoptionsAdapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<Adoptions> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /** FETCH THE NEXT SET OF ADOPTION LISTINGS **/
    private void fetchNextAdoptionsPage() {
        progressLoading.setVisibility(View.VISIBLE);
        AdoptionsAPI api = ZenApiClient.getClient().create(AdoptionsAPI.class);
        Call<Adoptions> call = api.fetchTestAdoptions(FINAL_CITY_ID, FILTER_PET_SPECIES, FILTER_PET_GENDER, String.valueOf(currentPage));
        call.enqueue(new Callback<Adoptions>() {
            @Override
            public void onResponse(Call<Adoptions> call, Response<Adoptions> response) {
//                Log.e("ADOPTIONS LIST", String.valueOf(response.raw()));
                /* PROCESS THE RESPONSE */
                arrAdoptions = processResult(response);

                adoptionsAdapter.removeLoadingFooter();
                isLoading = false;

                ArrayList<Adoption> adoptions = arrAdoptions;
                adoptionsAdapter.addAll(adoptions);

                if (currentPage != TOTAL_PAGES) adoptionsAdapter.addLoadingFooter();
                else isLastPage = true;

                progressLoading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Adoptions> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /** PROCESS THE ADOPTION RESULTS **/
    private ArrayList<Adoption> processResult(Response<Adoptions> response) {
        ArrayList<Adoption> adoptions = new ArrayList<>();
        ArrayList<Promotion> promotions = new ArrayList<>();
        try {
            String strResult = new Gson().toJson(response.body());
            JSONObject JORoot = new JSONObject(strResult);
//            Log.e("ROOT", String.valueOf(JORoot));
            if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
                JSONArray JAAdoptions = JORoot.getJSONArray("adoptions");
                for (int i = 0; i < JAAdoptions.length(); i++) {
                    JSONObject JOAdoptions = JAAdoptions.getJSONObject(i);
                    Adoption data = new Adoption();

                    /* GET THE PROMOTED ADOPTIONS */
                    JSONArray JAPromotions = JOAdoptions.getJSONArray("promotions");
//                    Log.e("PROMOTIONS", String.valueOf(JAPromotions));
                    if (JAPromotions.length() > 0)  {
                        for (int j = 0; j < JAPromotions.length(); j++) {
                            JSONObject JOPromotions = JAPromotions.getJSONObject(j);
//                            Log.e("PROMOTIONS", String.valueOf(JOPromotions));
                            Promotion promotion = new Promotion();

                            /* GET THE PROMOTION ID */
                            if (JOPromotions.has("promotedID")) {
                                promotion.setPromotedID(JOPromotions.getString("promotedID"));
                            } else {
                                promotion.setPromotedID(null);
                            }

                            /* GET THE ADOPTION ID */
                            if (JOPromotions.has("adoptionID")) {
                                promotion.setAdoptionID(JOPromotions.getString("adoptionID"));
                            } else {
                                promotion.setAdoptionID(null);
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

                            /* GET THE PET TYPE ID AND NAME */
                            if (JOPromotions.has("petTypeID") && JOPromotions.has("petTypeName")) {
                                promotion.setPetTypeID(JOPromotions.getString("petTypeID"));
                                promotion.setPetTypeName(JOPromotions.getString("petTypeName"));
                            } else {
                                promotion.setPetTypeID(null);
                                promotion.setPetTypeName(null);
                            }

                            /* GET THE BREED ID AND NAME */
                            if (JOPromotions.has("breedID") && JOPromotions.has("breedName")) {
                                promotion.setBreedID(JOPromotions.getString("breedID"));
                                promotion.setBreedName(JOPromotions.getString("breedName"));
                            } else {
                                promotion.setBreedID(null);
                                promotion.setBreedName(null);
                            }

                            /* GET THE USER ID AND NAME */
                            if (JOPromotions.has("userID") && JOPromotions.has("userName")) {
                                promotion.setUserID(JOPromotions.getString("userID"));
                                promotion.setUserName(JOPromotions.getString("userName"));
                            } else {
                                promotion.setUserID(null);
                                promotion.setUserName(null);
                            }

                            /* GET THE CITY ID AND NAME */
                            if (JOPromotions.has("cityID") && JOPromotions.has("cityName")) {
                                promotion.setCityID(JOPromotions.getString("cityID"));
                                promotion.setCityName(JOPromotions.getString("cityName"));
                            } else {
                                promotion.setCityID(null);
                                promotion.setCityName(null);
                            }

                            /* GET THE ADOPTION NAME */
                            if (JOPromotions.has("adoptionName"))  {
                                promotion.setAdoptionName(JOPromotions.getString("adoptionName"));
                            } else {
                                promotion.setAdoptionName(null);
                            }

                            /* GET THE ADOPTION COVER PHOTO */
                            if (JOPromotions.has("adoptionCoverPhoto"))  {
                                promotion.setAdoptionCoverPhoto(JOPromotions.getString("adoptionCoverPhoto"));
                            } else {
                                promotion.setAdoptionCoverPhoto(null);
                            }

                            /* GET THE ADOPTION DESCRIPTION */
                            if (JOPromotions.has("adoptionDescription"))  {
                                promotion.setAdoptionDescription(JOPromotions.getString("adoptionDescription"));
                            } else {
                                promotion.setAdoptionDescription(null);
                            }

                            /* GET THE ADOPTION GENDER */
                            if (JOPromotions.has("adoptionGender"))  {
                                promotion.setAdoptionGender(JOPromotions.getString("adoptionGender"));
                            } else {
                                promotion.setAdoptionGender(null);
                            }

                            /* GET THE ADOPTION TIME STAMP */
                            if (JOPromotions.has("adoptionTimeStamp"))  {
                                promotion.setAdoptionTimeStamp(JOPromotions.getString("adoptionTimeStamp"));
                            } else {
                                promotion.setAdoptionTimeStamp(null);
                            }

                            /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                            promotions.add(promotion);
                        }
                        data.setPromotions(promotions);
//                        Log.e("PROMOTIONS SIZE", String.valueOf(promotions.size()));
                    } else {
                        data.setPromotions(null);
                    }

                    /* GET THE ADOPTION ID */
                    if (JOAdoptions.has("adoptionID"))  {
                        data.setAdoptionID(JOAdoptions.getString("adoptionID"));
                    } else {
                        data.setAdoptionID(null);
                    }

                    /* GET THE PET TYPE ID */
                    if (JOAdoptions.has("petTypeID"))   {
                        data.setPetTypeID(JOAdoptions.getString("petTypeID"));
                    } else {
                        data.setPetTypeID(null);
                    }

                    /* GET THE PET TYPE NAME */
                    if (JOAdoptions.has("petTypeName")) {
                        data.setPetTypeName(JOAdoptions.getString("petTypeName"));
                    } else {
                        data.setPetTypeName(null);
                    }

                    /* GET THE BREED ID */
                    if (JOAdoptions.has("breedID")) {
                        data.setBreedID(JOAdoptions.getString("breedID"));
                    }  else {
                        data.setBreedID(null);
                    }

                    /* GET THE BREED NAME */
                    if (JOAdoptions.has("breedName"))   {
                        data.setBreedName(JOAdoptions.getString("breedName"));
                    } else {
                        data.setBreedName(null);
                    }

                    /* GET THE USER ID */
                    if (JOAdoptions.has("userID"))  {
                        data.setUserID(JOAdoptions.getString("userID"));
                    } else {
                        data.setUserID(null);
                    }

                    /* GET THE USER'S NAME */
                    if (JOAdoptions.has("userName"))    {
                        data.setUserName(JOAdoptions.getString("userName"));
                    } else {
                        data.setUserName(null);
                    }

                    /* GET THE CITY ID */
                    if (JOAdoptions.has("cityID"))  {
                        data.setCityID(JOAdoptions.getString("cityID"));
                    } else {
                        data.setCityID(null);
                    }

                    /* GET THE CITY NAME */
                    if (JOAdoptions.has("cityName"))    {
                        data.setCityName(JOAdoptions.getString("cityName"));
                    } else {
                        data.setCityName(null);
                    }

                    /* GET THE ADOPTION NAME */
                    if (JOAdoptions.has("adoptionName"))    {
                        data.setAdoptionName(JOAdoptions.getString("adoptionName"));
                    } else {
                        data.setAdoptionName(null);
                    }

                    /* GET THE ADOPTION COVER PHOTO */
                    if (JOAdoptions.has("adoptionCoverPhoto"))  {
                        data.setAdoptionCoverPhoto(JOAdoptions.getString("adoptionCoverPhoto"));
                    } else {
                        data.setAdoptionCoverPhoto(null);
                    }

                    /* GET THE ADOPTION DESCRIPTION */
                    if (JOAdoptions.has("adoptionDescription")) {
                        data.setAdoptionDescription(JOAdoptions.getString("adoptionDescription"));
                    } else {
                        data.setAdoptionDescription(null);
                    }

                    /* GET THE ADOPTION GENDER */
                    if (JOAdoptions.has("adoptionGender")
                            && !JOAdoptions.getString("adoptionGender").equalsIgnoreCase("null")
                            && JOAdoptions.getString("adoptionGender") != null)  {
                        data.setAdoptionGender(JOAdoptions.getString("adoptionGender"));
                    } else {
                        data.setAdoptionGender(null);
                    }

                    /* GET THE ADOPTION TIME STAMP */
                    if (JOAdoptions.has("adoptionTimeStamp"))   {
                        data.setAdoptionTimeStamp(JOAdoptions.getString("adoptionTimeStamp"));
                    } else {
                        data.setAdoptionTimeStamp(null);
                    }

                    /* GET THE ADOPTION STATUS */
                    if (JOAdoptions.has("adoptionStatus"))  {
                        data.setAdoptionStatus(JOAdoptions.getString("adoptionStatus"));
                    } else {
                        data.setAdoptionStatus(null);
                    }

                    /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                    adoptions.add(data);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
//            Log.e("EXCEPTION", e.getMessage());
            Crashlytics.logException(e);
        }
        return adoptions;
    }

    /***** FETCH THE USER'S LOCATION *****/
    private void getUsersLocation() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(TestAdoptionsList.this,
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
                                        TestAdoptionsList.this,
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
        Geocoder geocoder = new Geocoder(TestAdoptionsList.this, Locale.getDefault());
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
//        linlaProgress.setVisibility(View.VISIBLE);

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

                        /* FETCH THE FIRST LIST OF ADOPTION LISTINGS */
                        fetchAdoptions();
                    } else {
                        new MaterialDialog.Builder(TestAdoptionsList.this)
                                .title("Location not Served!")
                                .content("We currently do not serve this City. but fear not. We will have you covered shortly.")
                                .positiveText("OKAY")
                                .theme(Theme.LIGHT)
                                .icon(ContextCompat.getDrawable(TestAdoptionsList.this, R.drawable.ic_info_outline_black_24dp))
                                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                .show();
                    }
                } else {
                    new MaterialDialog.Builder(TestAdoptionsList.this)
                            .title("Location not Served!")
                            .content("We currently do not serve this City. but fear not. We will have you covered shortly.")
                            .positiveText("OKAY")
                            .theme(Theme.LIGHT)
                            .icon(ContextCompat.getDrawable(TestAdoptionsList.this, R.drawable.ic_info_outline_black_24dp))
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
                                        TestAdoptionsList.this,
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

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION FOR THE ADOPTIONS LIST */
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listAdoptions.setLayoutManager(manager);
        listAdoptions.setHasFixedSize(true);

        /* INSTANTIATE AND SET THE ADOPTIONS ADAPTER */
        listAdoptions.setAdapter(adoptionsAdapter);

        /* CONFIGURE THE SCROLL LISTENER */
        listAdoptions.addOnScrollListener(new PaginationScrollListener(manager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                /* FETCH THE NEXT SET OF ADOPTION LISTINGS */
                fetchNextAdoptionsPage();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 101)  {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                if (bundle.containsKey("PET_GENDER") || bundle.containsKey("PET_SPECIES"))  {
                    FILTER_PET_GENDER = bundle.getString("PET_GENDER");
                    FILTER_PET_SPECIES = bundle.getString("PET_SPECIES");
                    if (FILTER_PET_SPECIES != null && FILTER_PET_SPECIES.equalsIgnoreCase("Both"))    {
                        FILTER_PET_SPECIES = null;

                        /* CLEAR THH ARRAY LIST AND THE ADAPTER */
//                        arrAdoptions.clear();
                        adoptionsAdapter.clear();

                        /* RESET THE PAGE NUMBER AND TOTAL PAGES */
                        currentPage = PAGE_START;
                        TOTAL_PAGES = 0;

                        /* FETCH THE LIST OF ADOPTIONS AGAIN */
                        fetchAdoptions();
                    }

                    if (FILTER_PET_SPECIES != null && FILTER_PET_GENDER != null)    {

                        /* CLEAR THH ARRAY LIST AND THE ADAPTER */
//                        arrAdoptions.clear();
                        adoptionsAdapter.clear();

                        /* RESET THE PAGE NUMBER AND TOTAL PAGES */
                        currentPage = PAGE_START;
                        TOTAL_PAGES = 0;

                        /* FETCH THE LIST OF ADOPTIONS AGAIN */
                        fetchAdoptions();
                    } else if (FILTER_PET_SPECIES != null && FILTER_PET_GENDER == null) {

                        /* CLEAR THH ARRAY LIST AND THE ADAPTER */
//                        arrAdoptions.clear();
                        adoptionsAdapter.clear();

                        /* RESET THE PAGE NUMBER AND TOTAL PAGES */
                        currentPage = PAGE_START;
                        TOTAL_PAGES = 0;

                        /* FETCH THE LIST OF ADOPTIONS AGAIN */
                        fetchAdoptions();
                    } else if (FILTER_PET_SPECIES == null && FILTER_PET_GENDER != null) {

                        /* CLEAR THH ARRAY LIST AND THE ADAPTER */
//                        arrAdoptions.clear();
                        adoptionsAdapter.clear();

                        /* RESET THE PAGE NUMBER AND TOTAL PAGES */
                        currentPage = PAGE_START;
                        TOTAL_PAGES = 0;

                        /* FETCH THE LIST OF ADOPTIONS AGAIN */
                        fetchAdoptions();
                    }
                }
            }
        }
    }

    /** FETCH THE TOTAL NUMBER OF PAGES **/
    private void fetchTotalPages() {
        AdoptionsAPI api = ZenApiClient.getClient().create(AdoptionsAPI.class);
        Call<AdoptionPages> call = api.fetchAdoptionPages(FINAL_CITY_ID, FILTER_PET_SPECIES, FILTER_PET_GENDER);
        call.enqueue(new Callback<AdoptionPages>() {
            @Override
            public void onResponse(Call<AdoptionPages> call, Response<AdoptionPages> response) {
                if (response.body() != null && response.body().getTotalPages() != null) {
                    TOTAL_PAGES = Integer.parseInt(response.body().getTotalPages());
//                    Log.e("TOTAL PAGES", String.valueOf(TOTAL_PAGES));
                }
            }

            @Override
            public void onFailure(Call<AdoptionPages> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }
}