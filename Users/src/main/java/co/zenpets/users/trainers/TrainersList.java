package co.zenpets.users.trainers;

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
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import co.zenpets.users.R;
import co.zenpets.users.utils.adapters.trainers.TrainersAdapter;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.location.City;
import co.zenpets.users.utils.models.location.LocationsAPI;
import co.zenpets.users.utils.models.trainers.Trainer;
import co.zenpets.users.utils.models.trainers.Trainers;
import co.zenpets.users.utils.models.trainers.TrainersAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainersList extends AppCompatActivity {

    /** A FUSED LOCATION PROVIDER CLIENT INSTANCE**/
    private FusedLocationProviderClient locationProviderClient;

    /** A LOCATION INSTANCE **/
    private Location location;

    /** STRING TO HOLD THE DETECTED CITY NAME FOR QUERYING THE ADOPTIONS **/
    private String DETECTED_CITY = null;
    private String FINAL_CITY_ID = null;

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int ACCESS_FINE_LOCATION_CONSTANT = 200;

    /** THE TRAINERS ARRAY LIST **/
    private ArrayList<Trainer> arrTrainers = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtLocation) AppCompatTextView txtLocation;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listTrainers) RecyclerView listTrainers;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainers_list);
        ButterKnife.bind(this);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /* INSTANTIATE THE LOCATION CLIENT */
        locationProviderClient = LocationServices.getFusedLocationProviderClient(TrainersList.this);

        /* FETCH THE USER'S LOCATION */
        getUsersLocation();
    }

    /***** FETCH THE LIST OF TRAINERS IN THE SELECTED CITY *****/
    private void fetchTrainers() {
        TrainersAPI api = ZenApiClient.getClient().create(TrainersAPI.class);
        Call<Trainers> call = api.fetchTrainersList(FINAL_CITY_ID);
        call.enqueue(new Callback<Trainers>() {
            @Override
            public void onResponse(Call<Trainers> call, Response<Trainers> response) {
                if (response.isSuccessful() && response.body().getTrainers() != null)    {
                    arrTrainers = response.body().getTrainers();

                    /* CHECK THE ARRAY LIST SIZE AND SHOW THE APPROPRIATE LAYOUT */
                    if (arrTrainers.size() > 0)    {
                        /* SET THE ADAPTER TO THE TRAINERS RECYCLER */
                        listTrainers.setAdapter(new TrainersAdapter(TrainersList.this, arrTrainers));

                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                        listTrainers.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);

                        /* HIDE THE PROGRESS BAR AFTER LOADING THE DATA */
                        linlaProgress.setVisibility(View.GONE);
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listTrainers.setVisibility(View.GONE);

                        /* HIDE THE PROGRESS BAR AFTER LOADING THE DATA */
                        linlaProgress.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listTrainers.setVisibility(View.GONE);

                    /* HIDE THE PROGRESS BAR AFTER LOADING THE DATA */
                    linlaProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Trainers> call, Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE USER'S LOCATION *****/
    private void getUsersLocation() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(TrainersList.this,
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
                                        TrainersList.this,
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
//                                Crashlytics.logException(task.getException());
                            }
                        }
                    });
        }
    }

    /***** FETCH THE LOCATION USING A GEOCODER *****/
    private void fetchLocation() {
        Geocoder geocoder = new Geocoder(TrainersList.this, Locale.getDefault());
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
                        /* FETCH THE LIST OF TRAINERS IN THE SELECTED CITY */
                        fetchTrainers();
                    } else {
                        new MaterialDialog.Builder(TrainersList.this)
                                .title("Location not Served!")
                                .content("We currently do not serve this City. but fear not. We will have you covered shortly.")
                                .positiveText("OKAY")
                                .theme(Theme.LIGHT)
                                .icon(ContextCompat.getDrawable(TrainersList.this, R.drawable.ic_info_outline_black_24dp))
                                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                .show();
                    }
                } else {
                    new MaterialDialog.Builder(TrainersList.this)
                            .title("Location not Served!")
                            .content("We currently do not serve this City. but fear not. We will have you covered shortly.")
                            .positiveText("OKAY")
                            .theme(Theme.LIGHT)
                            .icon(ContextCompat.getDrawable(TrainersList.this, R.drawable.ic_info_outline_black_24dp))
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
                                        TrainersList.this,
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
        /* SET THE CONFIGURATION */
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listTrainers.setLayoutManager(manager);
        listTrainers.setHasFixedSize(true);

        /* INSTANTIATE AND SET THE ADAPTER */
        listTrainers.setAdapter(new TrainersAdapter(TrainersList.this, arrTrainers));
    }
}