package biz.zenpets.users.boarding;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import biz.zenpets.users.utils.adapters.boardings.BoardingsAdapter;
import biz.zenpets.users.utils.helpers.classes.PaginationScrollListener;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.boarding.Boarding;
import biz.zenpets.users.utils.models.boarding.BoardingPages;
import biz.zenpets.users.utils.models.boarding.Boardings;
import biz.zenpets.users.utils.models.boarding.BoardingsAPI;
import biz.zenpets.users.utils.models.location.City;
import biz.zenpets.users.utils.models.location.LocationsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardingsList extends AppCompatActivity {

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

    /** THE BOARDINGS ADAPTER AND ARRAY LIST INSTANCE **/
    private BoardingsAdapter adapter;
    private ArrayList<Boarding> arrBoardings = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtLocation) TextView txtLocation;
    @BindView(R.id.listBoardings) RecyclerView listBoardings;
    @BindView(R.id.progressLoading) ProgressBar progressLoading;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
    @BindView(R.id.txtEmpty) TextView txtEmpty;

    /** REGISTER THE USER FOR HOME BOARDING **/
    @OnClick(R.id.txtEmpty) void registerBoarding()    {
        Intent intent = new Intent(BoardingsList.this, BoardingEnrollment.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_boarding_list);
        ButterKnife.bind(this);

        /* INSTANTIATE THE LOCATION CLIENT */
        locationProviderClient = LocationServices.getFusedLocationProviderClient(BoardingsList.this);

        /* FETCH THE USER'S LOCATION */
        getUsersLocation();
    }

    /** FETCH THE FIRST LIST OF BOARDINGS **/
    private void fetchBoardings() {
        BoardingsAPI api = ZenApiClient.getClient().create(BoardingsAPI.class);
        Call<Boardings> call = api.fetchBoardingsList(
                FINAL_CITY_ID,
                String.valueOf(currentPage),
                String.valueOf(LATLNG_ORIGIN.latitude),
                String.valueOf(LATLNG_ORIGIN.longitude));
        call.enqueue(new Callback<Boardings>() {
            @Override
            public void onResponse(Call<Boardings> call, Response<Boardings> response) {
                if (response.body() != null && response.body().getBoardings() != null)    {
//                    Log.e("BOARDINGS LIST", String.valueOf(response.raw()));
                    /* PROCESS THE RESPONSE */
                    arrBoardings = processResult(response);
                    if (arrBoardings.size() > 0)  {
                        progressLoading.setVisibility(View.GONE);
                        if (arrBoardings != null && arrBoardings.size() > 0)
                            adapter.addAll(arrBoardings);

                        if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
                        else isLastPage = true;
                    } else {
                        /* MARK THE LAST PAGE FLAG TO "TRUE" */
                        isLastPage = true;

                        /* HIDE THE PROGRESS */
                        progressLoading.setVisibility(View.GONE);

                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listBoardings.setVisibility(View.GONE);
                    }
                } else {
                    /* MARK THE LAST PAGE FLAG TO "TRUE" */
                    isLastPage = true;

                    /* HIDE THE PROGRESS */
                    progressLoading.setVisibility(View.GONE);

                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listBoardings.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Boardings> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /** FETCH THE NEXT SET OF BOARDINGS **/
    private void fetchNextBoardings() {
        progressLoading.setVisibility(View.VISIBLE);
        BoardingsAPI api = ZenApiClient.getClient().create(BoardingsAPI.class);
        Call<Boardings> call = api.fetchBoardingsList(
                FINAL_CITY_ID,
                String.valueOf(currentPage),
                String.valueOf(LATLNG_ORIGIN.latitude),
                String.valueOf(LATLNG_ORIGIN.longitude));
        call.enqueue(new Callback<Boardings>() {
            @Override
            public void onResponse(Call<Boardings> call, Response<Boardings> response) {
//                Log.e("KENNELS LIST", String.valueOf(response.raw()));
                /* PROCESS THE RESPONSE */
                arrBoardings = processResult(response);

                adapter.removeLoadingFooter();
                isLoading = false;

                if (arrBoardings != null && arrBoardings.size() > 0)
                    adapter.addAll(arrBoardings);

                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;

                progressLoading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Boardings> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /** PROCESS THE BOARDINGS RESULTS **/
    private ArrayList<Boarding> processResult(Response<Boardings> response) {
        ArrayList<Boarding> boarding = new ArrayList<>();
        try {
            String strResult = new Gson().toJson(response.body());
            JSONObject JORoot = new JSONObject(strResult);
            if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
                JSONArray JABoardings = JORoot.getJSONArray("boardings");
                if (JABoardings.length() > 0) {
                    Boarding data;
                    for (int i = 0; i < JABoardings.length(); i++) {
                        final JSONObject JOBoardings = JABoardings.getJSONObject(i);
//                        Log.e("BOARDING", String.valueOf(JOBoardings));
                        data = new Boarding();

                        /* GET THE BOARDING ID */
                        if (JOBoardings.has("boardingID"))  {
                            data.setBoardingID(JOBoardings.getString("boardingID"));
                        } else {
                            data.setBoardingID(null);
                        }

                        /* GET THE USER ID */
                        if (JOBoardings.has("userID"))    {
                            data.setUserID(JOBoardings.getString("userID"));
                        } else {
                            data.setUserID(null);
                        }

                        /* GET THE BOARDING ADDRESS */
                        if (JOBoardings.has("boardingAddress")) {
                            data.setBoardingAddress(JOBoardings.getString("boardingAddress"));
                        } else {
                            data.setBoardingAddress(null);
                        }

                        /* GET THE BOARDING PIN CODE */
                        if (JOBoardings.has("boardingPincode"))   {
                            data.setBoardingPincode(JOBoardings.getString("boardingPincode"));
                        } else {
                            data.setBoardingPincode(null);
                        }

                        /* GET THE BOARDING LATITUDE */
                        if (JOBoardings.has("boardingLatitude")) {
                            data.setBoardingLatitude(JOBoardings.getString("boardingLatitude"));
                        } else {
                            data.setBoardingLatitude(null);
                        }

                        /* GET THE BOARDING LONGITUDE */
                        if (JOBoardings.has("boardingLongitude")) {
                            data.setBoardingLongitude(JOBoardings.getString("boardingLongitude"));
                        } else {
                            data.setBoardingLongitude(null);
                        }

                        /* GET THE BOARDING DISTANCE */
                        if (JOBoardings.has("boardingDistance")) {
                            data.setBoardingDistance(JOBoardings.getString("boardingDistance"));
                        } else {
                            data.setBoardingDistance(null);
                        }

                        /* GET THE BOARDING DATE */
                        if (JOBoardings.has("boardingDate")) {
                            data.setBoardingDate(JOBoardings.getString("boardingDate"));
                        } else {
                            data.setBoardingDate(null);
                        }

                        /* GET THE BOARDING ACTIVE  */
                        if (JOBoardings.has("boardingActive")) {
                            data.setBoardingActive(JOBoardings.getString("boardingActive"));
                        } else {
                            data.setBoardingActive(null);
                        }

                        /* GET THE USER'S NAME */
                        if (JOBoardings.has("userName"))    {
                            data.setUserName(JOBoardings.getString("userName"));
                        } else {
                            data.setUserName(null);
                        }

                        /* GET THE USER'S DISPLAY PROFILE */
                        if (JOBoardings.has("userDisplayProfile"))    {
                            data.setUserDisplayProfile(JOBoardings.getString("userDisplayProfile"));
                        } else {
                            data.setUserDisplayProfile(null);
                        }

                        /* GET THE USER'S TOKEN */
                        if (JOBoardings.has("userToken"))    {
                            data.setUserToken(JOBoardings.getString("userToken"));
                        } else {
                            data.setUserToken(null);
                        }

                        /* GET THE KENNEL COUNTY ID */
                        if (JOBoardings.has("countryID")) {
                            data.setCountryID(JOBoardings.getString("countryID"));
                        } else {
                            data.setCountryID(null);
                        }

                        /* GET THE KENNEL COUNTRY NAME */
                        if (JOBoardings.has("countryName"))   {
                            data.setCountryName(JOBoardings.getString("countryName"));
                        } else {
                            data.setCountryName(null);
                        }

                        /* GET THE KENNEL STATE ID */
                        if (JOBoardings.has("stateID"))   {
                            data.setStateID(JOBoardings.getString("stateID"));
                        } else {
                            data.setStateID(null);
                        }

                        /* GET THE KENNEL STATE NAME */
                        if (JOBoardings.has("stateName")) {
                            data.setStateName(JOBoardings.getString("stateName"));
                        } else {
                            data.setStateName(null);
                        }

                        /* GET THE KENNEL CITY ID */
                        if (JOBoardings.has("cityID"))    {
                            data.setCityID(JOBoardings.getString("cityID"));
                        } else {
                            data.setCityID(null);
                        }

                        /* GET THE KENNEL CITY NAME */
                        if (JOBoardings.has("cityName"))  {
                            data.setCityName(JOBoardings.getString("cityName"));
                        } else {
                            data.setCityName(null);
                        }

                        /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                        boarding.add(data);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
//            Log.e("EXCEPTION", e.getMessage());
            Crashlytics.logException(e);
        }
        return boarding;
    }

    /***** FETCH THE USER'S LOCATION *****/
    private void getUsersLocation() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(BoardingsList.this,
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
                                        BoardingsList.this,
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

                                /* INSTANTIATE THE BOARDINGS ADAPTER */
                                adapter = new BoardingsAdapter(BoardingsList.this, arrBoardings, LATLNG_ORIGIN);

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
        Geocoder geocoder = new Geocoder(BoardingsList.this, Locale.getDefault());
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
                        new MaterialDialog.Builder(BoardingsList.this)
                                .title("Location not Served!")
                                .content("We currently do not serve this City. but fear not. We will have you covered shortly.")
                                .positiveText("OKAY")
                                .theme(Theme.LIGHT)
                                .icon(ContextCompat.getDrawable(BoardingsList.this, R.drawable.ic_info_outline_black_24dp))
                                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                .show();
                    }
                } else {
                    new MaterialDialog.Builder(BoardingsList.this)
                            .title("Location not Served!")
                            .content("We currently do not serve this City. but fear not. We will have you covered shortly.")
                            .positiveText("OKAY")
                            .theme(Theme.LIGHT)
                            .icon(ContextCompat.getDrawable(BoardingsList.this, R.drawable.ic_info_outline_black_24dp))
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
                                        BoardingsList.this,
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
        listBoardings.setLayoutManager(manager);
        listBoardings.setHasFixedSize(true);

        /* SET THE ADAPTER */
        listBoardings.setAdapter(adapter);

        /* CONFIGURE THE SCROLL LISTENER */
        listBoardings.addOnScrollListener(new PaginationScrollListener(manager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                /* FETCH THE NEXT SET OF BOARDINGS */
                fetchNextBoardings();
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

    /** FETCH THE TOTAL NUMBER OF PAGES **/
    private void fetchTotalPages() {
        BoardingsAPI api = ZenApiClient.getClient().create(BoardingsAPI.class);
        Call<BoardingPages> call = api.fetchBoardingPages(FINAL_CITY_ID);
        call.enqueue(new Callback<BoardingPages>() {
            @Override
            public void onResponse(Call<BoardingPages> call, Response<BoardingPages> response) {
                if (response.body() != null && response.body().getTotalPages() != null) {
                    TOTAL_PAGES = Integer.parseInt(response.body().getTotalPages());
//                    Log.e("TOTAL PAGES", String.valueOf(TOTAL_PAGES));

                    /* SHOW THE PROGRESS FETCH THE FIRST LIST OF BOARDINGS */
                    progressLoading.setVisibility(View.VISIBLE);
                    fetchBoardings();
                }
            }

            @Override
            public void onFailure(Call<BoardingPages> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }
}
