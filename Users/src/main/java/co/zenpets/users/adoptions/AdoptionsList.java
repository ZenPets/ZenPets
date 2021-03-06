package co.zenpets.users.adoptions;

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

import co.zenpets.users.R;
import co.zenpets.users.utils.adapters.adoptions.TestAdoptionsAdapter;
import co.zenpets.users.utils.helpers.classes.FilterAdoptionsActivity;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.adoptions.adoption.Adoption;
import co.zenpets.users.utils.models.adoptions.adoption.Adoptions;
import co.zenpets.users.utils.models.adoptions.adoption.AdoptionsAPI;
import co.zenpets.users.utils.models.adoptions.promotion.Promotion;
import co.zenpets.users.utils.models.location.City;
import co.zenpets.users.utils.models.location.LocationsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdoptionsList extends AppCompatActivity {

    /** A FUSED LOCATION PROVIDER CLIENT INSTANCE**/
    private FusedLocationProviderClient locationProviderClient;

    /** A LOCATION INSTANCE **/
    private Location location;

    /** STRING TO HOLD THE DETECTED CITY NAME FOR QUERYING THE ADOPTIONS **/
    private String DETECTED_CITY = null;
    private String FINAL_CITY_ID = null;

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int ACCESS_FINE_LOCATION_CONSTANT = 200;

    /** THE ADOPTION DATA MODEL INSTANCE **/
    Adoption data;

    /** THE PROMOTED ADOPTIONS AND ADOPTIONS ARRAY LIST INSTANCE **/
    private ArrayList<Promotion> arrPromoted = new ArrayList<>();
    private final ArrayList<Adoption> arrAdoptions = new ArrayList<>();

    /** THE ADOPTION FILTER STRINGS **/
    private String FILTER_PET_GENDER = null;
    private String FILTER_PET_SPECIES = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtLocation) AppCompatTextView txtLocation;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
//    @BindView(R.id.listPromoted) RecyclerView listPromoted;
    @BindView(R.id.listAdoptions) RecyclerView listAdoptions;
//    @BindView(R.id.progressLoading) ProgressBar progressLoading;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** FILTER THE ADOPTION LISTINGS **/
    @OnClick(R.id.fabFilterAdoptions) protected void filterAdoptions()  {
        Intent intent = new Intent(AdoptionsList.this, FilterAdoptionsActivity.class);
        intent.putExtra("PET_GENDER", FILTER_PET_GENDER);
        intent.putExtra("PET_SPECIES", FILTER_PET_SPECIES);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adoptions_list);
        ButterKnife.bind(this);

        /* INSTANTIATE THE LOCATION CLIENT */
        locationProviderClient = LocationServices.getFusedLocationProviderClient(AdoptionsList.this);

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
        Call<Adoptions> call = api.fetchTestAdoptions(FINAL_CITY_ID, FILTER_PET_SPECIES, FILTER_PET_GENDER, "1");
        call.enqueue(new Callback<Adoptions>() {
            @Override
            public void onResponse(Call<Adoptions> call, Response<Adoptions> response) {
//                Log.e("ADOPTIONS LIST", String.valueOf(response.raw()));
                try {
                    String strResult = new Gson().toJson(response.body());
                    JSONObject JORoot = new JSONObject(strResult);
//                    Log.e("ROOT", String.valueOf(JORoot));
                    if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
                        JSONArray JAAdoptions = JORoot.getJSONArray("adoptions");
                        JSONArray JAPromoted = JORoot.getJSONArray("promotions");
//                        Log.e("PROMOTED", String.valueOf(JAPromoted));
                        for (int i = 0; i < JAAdoptions.length(); i++) {
                            JSONObject JOAdoptions = JAAdoptions.getJSONObject(i);
//                            Log.e("ADOPTIONS", String.valueOf(JOAdoptions));
                        }
                    }
                    linlaProgress.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                if (response.isSuccessful() && response.body().getAdoptions() != null)    {
//                    arrAdoptions = response.body().getAdoptions();
//
//                    /* CHECK THE ARRAY LIST SIZE AND SHOW THE APPROPRIATE LAYOUT */
//                    if (arrAdoptions.size() > 0)    {
//                        /* SET THE ADAPTER TO THE ADOPTIONS RECYCLER */
//                        listAdoptions.setAdapter(new TestAdoptionsAdapter(AdoptionsList.this, arrAdoptions));
//
//                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
//                        listAdoptions.setVisibility(View.VISIBLE);
//                        linlaEmpty.setVisibility(View.GONE);
//
//                        /* HIDE THE PROGRESS BAR AFTER LOADING THE DATA */
//                        linlaProgress.setVisibility(View.GONE);
//                    } else {
//                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
//                        linlaEmpty.setVisibility(View.VISIBLE);
//                        listAdoptions.setVisibility(View.GONE);
//
//                        /* HIDE THE PROGRESS BAR AFTER LOADING THE DATA */
//                        linlaProgress.setVisibility(View.GONE);
//                    }
//                } else {
//                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
//                    linlaEmpty.setVisibility(View.VISIBLE);
//                    listAdoptions.setVisibility(View.GONE);
//
//                    /* HIDE THE PROGRESS BAR AFTER LOADING THE DATA */
//                    linlaProgress.setVisibility(View.GONE);
//                }
            }

            @Override
            public void onFailure(Call<Adoptions> call, Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
            }
        });
    }

    /***** FETCH THE USER'S LOCATION *****/
    private void getUsersLocation() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(AdoptionsList.this,
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
                                        AdoptionsList.this,
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
                            }
                        }
                    });
        }
    }

    /***** FETCH THE LOCATION USING A GEOCODER *****/
    private void fetchLocation() {
        Geocoder geocoder = new Geocoder(AdoptionsList.this, Locale.getDefault());
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
//        progressLoading.setVisibility(View.VISIBLE);
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
                        /* FETCH THE ADOPTION LISTINGS */
                        fetchAdoptions();
                    } else {
                        new MaterialDialog.Builder(AdoptionsList.this)
                                .title("Location not Served!")
                                .content("We currently do not serve this City. but fear not. We will have you covered shortly.")
                                .positiveText("OKAY")
                                .theme(Theme.LIGHT)
                                .icon(ContextCompat.getDrawable(AdoptionsList.this, R.drawable.ic_info_outline_black_24dp))
                                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                .show();
                    }
                } else {
                    new MaterialDialog.Builder(AdoptionsList.this)
                            .title("Location not Served!")
                            .content("We currently do not serve this City. but fear not. We will have you covered shortly.")
                            .positiveText("OKAY")
                            .theme(Theme.LIGHT)
                            .icon(ContextCompat.getDrawable(AdoptionsList.this, R.drawable.ic_info_outline_black_24dp))
                            .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<City> call, Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
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
                                        AdoptionsList.this,
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
        listAdoptions.setAdapter(new TestAdoptionsAdapter(AdoptionsList.this, arrAdoptions));

//        /* SET THE CONFIGURATION FOR THE PROMOTED ADOPTIONS LIST */
//        LinearLayoutManager managerPromoted = new LinearLayoutManager(this);
//        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        managerPromoted.setAutoMeasureEnabled(true);
//        listPromoted.setLayoutManager(managerPromoted);
//        listPromoted.setHasFixedSize(true);
//        listPromoted.setNestedScrollingEnabled(false);
//
//        /* CONFIGURE THE ADAPTER */
//        listPromoted.setAdapter(new PromotedAdoptionsAdapter(AdoptionsList.this, arrPromoted));
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
                        arrAdoptions.clear();
                        fetchAdoptions();
                    }
                    if (FILTER_PET_SPECIES != null && FILTER_PET_GENDER != null)    {
                        arrAdoptions.clear();
                        fetchAdoptions();
                    } else if (FILTER_PET_SPECIES != null && FILTER_PET_GENDER == null) {
                        arrAdoptions.clear();
                        fetchAdoptions();
                    } else if (FILTER_PET_SPECIES == null && FILTER_PET_GENDER != null) {
                        arrAdoptions.clear();
                        fetchAdoptions();
                    }
                }
            }
        }
    }
}