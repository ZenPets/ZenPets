package biz.zenpets.users.doctors;

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
import biz.zenpets.users.utils.adapters.doctors.DoctorsListAdapter;
import biz.zenpets.users.utils.helpers.classes.FilterLocationActivity;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.helpers.location.classes.FetchCityID;
import biz.zenpets.users.utils.helpers.location.classes.FetchLocalityID;
import biz.zenpets.users.utils.helpers.location.interfaces.FetchCityIDInterface;
import biz.zenpets.users.utils.helpers.location.interfaces.FetchLocalityIDInterface;
import biz.zenpets.users.utils.models.doctors.DoctorsAPI;
import biz.zenpets.users.utils.models.doctors.list.Doctor;
import biz.zenpets.users.utils.models.doctors.list.Doctors;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ConstantConditions")
public class DoctorsList extends AppCompatActivity
        implements FetchCityIDInterface, FetchLocalityIDInterface {

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

    /* THE DOCTOR ARRAY LIST INSTANCE */
    private ArrayList<Doctor> arrDoctors = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtLocation) AppCompatTextView txtLocation;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listDoctors) RecyclerView listDoctors;
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

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* INSTANTIATE THE LOCATION CLIENT */
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(DoctorsList.this);

        /* SHOW THE PROGRESS AND FETCH THE USER'S LOCATION */
        linlaProgress.setVisibility(View.VISIBLE);
        getUserLocation();

        /* CONFIGURE THE ACTIONBAR */
        configAB();
    }

    /***** FETCH THE LIST OF DOCTORS *****/
    private void fetchDoctors() {
        DoctorsAPI api = ZenApiClient.getClient().create(DoctorsAPI.class);
        Call<Doctors> call = api.fetchDoctorsList(FINAL_CITY_ID, FINAL_LOCALITY_ID);
        call.enqueue(new Callback<Doctors>() {
            @Override
            public void onResponse(Call<Doctors> call, Response<Doctors> response) {
                if (response.body() != null && response.body().getDoctors() != null)    {
                    arrDoctors = response.body().getDoctors();
                    if (arrDoctors.size() > 0)  {
                        /* SHOW THE DOCTORS RECYCLER AND HIDE THE EMPTY LAYOUT */
                        listDoctors.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);

                        /* SET THE DOCTORS RECYCLER VIEW */
                        listDoctors.setAdapter(new DoctorsListAdapter(DoctorsList.this, arrDoctors, LATLNG_ORIGIN));

                        /* HIDE THE PROGRESS AFTER LOADING THE DATA */
                        linlaProgress.setVisibility(View.GONE);
                    } else {
                        /* SET THE EMPTY TEXT */
                        txtEmpty.setText(getString(R.string.doctor_list_empty_message));

                        /* HIDE THE RECYCLER AND SHOW THE EMPTY LAYOUT */
                        listDoctors.setVisibility(View.GONE);
                        linlaEmpty.setVisibility(View.VISIBLE);

                        /* HIDE THE PROGRESS AFTER LOADING THE DATA */
                        linlaProgress.setVisibility(View.GONE);
                    }
                } else {
                    /* SET THE EMPTY TEXT */
                    txtEmpty.setText(getString(R.string.doctor_list_empty_message));

                    /* HIDE THE RECYCLER AND SHOW THE EMPTY LAYOUT */
                    listDoctors.setVisibility(View.GONE);
                    linlaEmpty.setVisibility(View.VISIBLE);

                    /* HIDE THE PROGRESS AFTER LOADING THE DATA */
                    linlaProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Doctors> call, Throwable t) {
//                Log.e("DOCTORS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

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
                                /* GET THE CITY ID */
                                new FetchCityID(this).execute(DETECTED_CITY);

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

    @Override
    public void onCityID(String result) {
        /* GET THE RESULT */
        FINAL_CITY_ID = result;

        /* CHECK FOR A VALID RESULT */
        if (FINAL_CITY_ID != null)   {
            /* GET THE LOCALITY ID */
            new FetchLocalityID(this).execute(DETECTED_LOCALITY);
        } else {
            /* SET THE ERROR MESSAGE */
            txtEmpty.setText(getString(R.string.doctor_list_location_not_served));

            /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
            linlaEmpty.setVisibility(View.VISIBLE);
            listDoctors.setVisibility(View.GONE);

            /* HIDE THE PROGRESS */
            linlaProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLocalityID(String result) {
        /* GET THE RESULT */
        FINAL_LOCALITY_ID = result;

        /* CHECK FOR A VALID, NON NULL RESULT */
        if (FINAL_LOCALITY_ID != null)  {
            /* FETCH THE LIST OF DOCTORS */
            fetchDoctors();
        } else {
            /* SET THE ERROR MESSAGE */
            txtEmpty.setText(getString(R.string.doctor_list_location_not_served));

            /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
            linlaEmpty.setVisibility(View.VISIBLE);
            listDoctors.setVisibility(View.GONE);

            /* HIDE THE PROGRESS */
            linlaProgress.setVisibility(View.GONE);
        }
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listDoctors.setLayoutManager(manager);
        listDoctors.setHasFixedSize(true);

        /* SET THE ADAPTER */
        listDoctors.setAdapter(new DoctorsListAdapter(DoctorsList.this, arrDoctors, LATLNG_ORIGIN));
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

                /* SHOW THE PROGRESS AND FETCH THE LIST OF DOCTORS */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchDoctors();
            }
        }
    }
}