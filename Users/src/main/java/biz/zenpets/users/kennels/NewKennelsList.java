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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.TypefaceSpan;
import biz.zenpets.users.utils.adapters.kennels.KennelsAdapter;
import biz.zenpets.users.utils.helpers.kennels.classes.FetchKennels;
import biz.zenpets.users.utils.helpers.kennels.interfaces.FetchKennelsInterface;
import biz.zenpets.users.utils.helpers.location.classes.FetchCityID;
import biz.zenpets.users.utils.helpers.location.interfaces.FetchCityIDInterface;
import biz.zenpets.users.utils.models.kennels.kennels.Kennel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NewKennelsList extends AppCompatActivity
        implements FetchCityIDInterface, FetchKennelsInterface, SearchView.OnQueryTextListener {

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

    /* THE KENNELS ARRAY LIST INSTANCE */
    private ArrayList<Kennel> arrKennels = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
//    @BindView(R.id.txtLocation) TextView txtLocation;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listKennels) RecyclerView listKennels;
    @BindView(R.id.progressLoading) ProgressBar progressLoading;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
    @BindView(R.id.txtEmpty) TextView txtEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kennels_list);
        ButterKnife.bind(this);

        /* INSTANTIATE THE LOCATION CLIENT */
        locationProviderClient = LocationServices.getFusedLocationProviderClient(NewKennelsList.this);

        /* SHOW THE PROGRESS AND FETCH THE USER'S LOCATION */
        linlaProgress.setVisibility(View.VISIBLE);
        fetchUsersLocation();

        /* CONFIGURE THE RECYCLER VIEW **/
        configRecycler();
    }

    @Override
    public void onKennels(ArrayList<Kennel> data) {
        /* CAST THE DATA IN THE GLOBAL INSTANCE */
        arrKennels = data;

        /* CHECK FOR CONTENTS */
        if (arrKennels.size() > 0)  {
            /* SET THE ADAPTER */
            listKennels.setAdapter(new KennelsAdapter(NewKennelsList.this, arrKennels));

            /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
            listKennels.setVisibility(View.VISIBLE);
            linlaEmpty.setVisibility(View.GONE);

            /* HIDE THE PROGRESS BAR */
            linlaProgress.setVisibility(View.GONE);
        } else {
            /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
            linlaEmpty.setVisibility(View.VISIBLE);
            listKennels.setVisibility(View.GONE);

            /* HIDE THE PROGRESS BAR */
            linlaProgress.setVisibility(View.GONE);
        }
    }

    /** GET THE LIST OF KENNELS **/
    private void fetchKennels() {
//        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
//        Call<Kennels> call = api.fetchKennelsListByCity(FINAL_CITY_ID);
//        call.enqueue(new Callback<Kennels>() {
//            @Override
//            public void onResponse(Call<Kennels> call, Response<Kennels> response) {
//                if (response.body() != null && response.body().getKennels() != null)    {
//                    arrKennels = response.body().getKennels();
//                    if (arrKennels.size() > 0)  {
//                        /* SET THE ADAPTER */
//                        listKennels.setAdapter(new KennelsAdapter(NewKennelsList.this, arrKennels, LATLNG_ORIGIN));
//
//                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
//                        listKennels.setVisibility(View.VISIBLE);
//                        linlaEmpty.setVisibility(View.GONE);
//
//                        /* HIDE THE PROGRESS BAR */
//                        linlaProgress.setVisibility(View.GONE);
//                    } else {
//                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
//                        linlaEmpty.setVisibility(View.VISIBLE);
//                        listKennels.setVisibility(View.GONE);
//
//                        /* HIDE THE PROGRESS BAR */
//                        linlaProgress.setVisibility(View.GONE);
//                    }
//                } else {
//                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
//                    linlaEmpty.setVisibility(View.VISIBLE);
//                    listKennels.setVisibility(View.GONE);
//
//                    /* HIDE THE PROGRESS BAR */
//                    linlaProgress.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Kennels> call, Throwable t) {
//                Log.e("KENNELS FAILURE", t.getMessage());
//                Crashlytics.logException(t);
//            }
//        });
    }

    /** FETCH THE KENNEL SEARCH RESULTS **/
//    private void fetchKennelResults(String query) {
//        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
//        Call<Kennels> call = api.searchKennels(FINAL_CITY_ID, query);
//        call.enqueue(new Callback<Kennels>() {
//            @Override
//            public void onResponse(Call<Kennels> call, Response<Kennels> response) {
//                if (response.body() != null && response.body().getKennels() != null)    {
//                    arrKennels = response.body().getKennels();
//                    if (arrKennels.size() > 0)  {
//                        /* SET THE ADAPTER */
//                        listKennels.setAdapter(new KennelsAdapter(NewKennelsList.this, arrKennels, LATLNG_ORIGIN));
//
//                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
//                        listKennels.setVisibility(View.VISIBLE);
//                        linlaEmpty.setVisibility(View.GONE);
//
//                        /* HIDE THE PROGRESS BAR */
//                        linlaProgress.setVisibility(View.GONE);
//                    } else {
//                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
//                        linlaEmpty.setVisibility(View.VISIBLE);
//                        listKennels.setVisibility(View.GONE);
//
//                        /* HIDE THE PROGRESS BAR */
//                        linlaProgress.setVisibility(View.GONE);
//                    }
//                } else {
//                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
//                    linlaEmpty.setVisibility(View.VISIBLE);
//                    listKennels.setVisibility(View.GONE);
//
//                    /* HIDE THE PROGRESS BAR */
//                    linlaProgress.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Kennels> call, Throwable t) {
//                Log.e("KENNELS FAILURE", t.getMessage());
//                Crashlytics.logException(t);
//            }
//        });
//    }

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
                                Log.e("EXCEPTION", String.valueOf(task.getException()));
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
                    Log.e("CITY", DETECTED_CITY);
                    new FetchCityID(this).execute(DETECTED_CITY);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("GEOCODER", e.getMessage());
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
            Log.e("CITY ID", FINAL_CITY_ID);
            /* GET THE LIST OF KENNELS */
            new FetchKennels(this).execute(FINAL_CITY_ID, LATLNG_ORIGIN.latitude, LATLNG_ORIGIN.longitude);
            fetchKennels();
        } else {
            /* SET THE ERROR MESSAGE */
            txtEmpty.setText(getString(R.string.doctor_list_location_not_served));

            /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
            linlaEmpty.setVisibility(View.VISIBLE);
            listKennels.setVisibility(View.GONE);

            /* HIDE THE PROGRESS */
            linlaProgress.setVisibility(View.GONE);
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
        listKennels.setAdapter(new KennelsAdapter(NewKennelsList.this, arrKennels));
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
            linlaProgress.setVisibility(View.VISIBLE);
//            fetchKennelResults(query);
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