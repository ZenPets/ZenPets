package biz.zenpets.users.utils.helpers.classes;

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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.models.location.City;
import biz.zenpets.users.utils.models.location.Localities;
import biz.zenpets.users.utils.models.location.Locality;
import biz.zenpets.users.utils.models.location.LocationsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterLocationActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    /** A FUSED LOCATION PROVIDER CLIENT INSTANCE**/
    private FusedLocationProviderClient providerClient;

    /** A LOCATION INSTANCE **/
    private Location location;

    /** STRING TO HOLD THE DETECTED CITY NAME AND LOCALITY FOR QUERYING THE DOCTORS INFORMATION **/
    private String DETECTED_CITY = null;
    private String FINAL_CITY_ID = null;
    private String DETECTED_LOCALITY = null;
    private String FINAL_LOCALITY_ID = null;

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int ACCESS_FINE_LOCATION_CONSTANT = 200;

    /** BOOLEAN TO CHECK IF LOCATION IS MANUAL ('FALSE') OR AUTO ('TRUE') **/
    private boolean blnStatus = false;

    /** THE SEARCH VIEW INSTANCE **/
    private SearchView searchView;

    /** THE LOCALITIES SEARCH ADAPTER AND ARRAY LIST **/
    private LocalitiesSearchAdapter searchAdapter;
    private ArrayList<Locality> arrLocalities = new ArrayList<>();
    private final ArrayList<Locality> arrFilteredResults = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listLocalities) RecyclerView listLocalities;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** USE THE CURRENT LOCATION **/
    @OnClick(R.id.linlaCurrentLocation) protected void currentLocation()    {
        blnStatus = true;

        /* FETCH THE USER'S LOCATION */
        getUserLocation();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_location_list);
        ButterKnife.bind(this);

        /* INSTANTIATE THE LOCALITIES SEARCH ADAPTER */
        searchAdapter = new LocalitiesSearchAdapter(arrLocalities);

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* INSTANTIATE THE LOCATION CLIENT */
        providerClient = LocationServices.getFusedLocationProviderClient(FilterLocationActivity.this);

        /* FETCH THE USER'S LOCATION */
        getUserLocation();

        /* CONFIGURE THE ACTIONBAR */
        configAB();
    }

    /***** FETCH THE USER'S LOCATION *****/
    private void getUserLocation() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(FilterLocationActivity.this,
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
                                        FilterLocationActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CONSTANT);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_FINE_LOCATION_CONSTANT);
            }
        } else {
            providerClient.getLastLocation()
                    .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                location = task.getResult();

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_FINE_LOCATION_CONSTANT)   {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                /* FETCH THE USER'S LOCATION */
                getUserLocation();
            } else {
                // TODO : SHOW THE DIALOG
            }
        }
    }

    /***** FETCH THE LOCATION USING GEOCODER *****/
    private void fetchTheLocation() {
        Geocoder geocoder = new Geocoder(FilterLocationActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0)   {
                DETECTED_CITY = addresses.get(0).getLocality();
                DETECTED_LOCALITY = addresses.get(0).getSubLocality();
                if (DETECTED_CITY != null && !DETECTED_CITY.equalsIgnoreCase("null"))  {
                    if (blnStatus)  {
                        if (DETECTED_LOCALITY != null && !DETECTED_LOCALITY.equalsIgnoreCase("null")) {
                            /* FETCH THE CITY ID */
                            fetchCityID();
                        }
                    } else {
                        /* GET THE LIST OF LOCALITIES */
                        fetchLocalities();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***** GET THE CITY ID *****/
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
                        /* GET THE LOCALITY ID */
                        fetchLocalityID();
                    } else {
                        new MaterialDialog.Builder(FilterLocationActivity.this)
                                .title("Location not Served!")
                                .content("There was a problem fetching Doctors near or at your current location. ")
                                .positiveText("OKAY")
                                .theme(Theme.LIGHT)
                                .icon(ContextCompat.getDrawable(FilterLocationActivity.this, R.drawable.ic_info_outline_black_24dp))
                                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                .show();
                    }
                } else {
                    new MaterialDialog.Builder(FilterLocationActivity.this)
                            .title("Location not Served!")
                            .content("There was a problem fetching Doctors near or at your current location. ")
                            .positiveText("OKAY")
                            .theme(Theme.LIGHT)
                            .icon(ContextCompat.getDrawable(FilterLocationActivity.this, R.drawable.ic_info_outline_black_24dp))
                            .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<City> call, Throwable t) {
//                Log.e("CITY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE LOCALITY ID *****/
    private void fetchLocalityID() {
        LocationsAPI api = ZenApiClient.getClient().create(LocationsAPI.class);
        Call<Locality> call = api.getLocalityID(DETECTED_LOCALITY);
        call.enqueue(new Callback<Locality>() {
            @Override
            public void onResponse(Call<Locality> call, Response<Locality> response) {
                /* GET THE DATA */
                Locality locality = response.body();
                if (locality != null)   {
                    /* GET THE LOCALITY ID */
                    FINAL_LOCALITY_ID = locality.getLocalityID();
                    if (FINAL_LOCALITY_ID != null)  {
                        Intent intent = new Intent();
                        intent.putExtra("LOCALITY_ID", FINAL_LOCALITY_ID);
                        intent.putExtra("LOCALITY_NAME", DETECTED_LOCALITY);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        new MaterialDialog.Builder(FilterLocationActivity.this)
                                .title("Location not Served!")
                                .content("There was a problem fetching Doctors near or at your current location. ")
                                .positiveText("OKAY")
                                .theme(Theme.LIGHT)
                                .icon(ContextCompat.getDrawable(FilterLocationActivity.this, R.drawable.ic_info_outline_black_24dp))
                                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                .show();
                    }
                } else {
                    new MaterialDialog.Builder(FilterLocationActivity.this)
                            .title("Location not Served!")
                            .content("There was a problem fetching Doctors near or at your current location. ")
                            .positiveText("OKAY")
                            .theme(Theme.LIGHT)
                            .icon(ContextCompat.getDrawable(FilterLocationActivity.this, R.drawable.ic_info_outline_black_24dp))
                            .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<Locality> call, Throwable t) {
//                Log.e("LOCALITY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE LIST OF LOCALITIES *****/
    private void fetchLocalities() {
        /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
        linlaProgress.setVisibility(View.VISIBLE);
        
        LocationsAPI api = ZenApiClient.getClient().create(LocationsAPI.class);
        Call<Localities> call = api.fetchLocalitiesByCity(DETECTED_CITY);
        call.enqueue(new Callback<Localities>() {
            @Override
            public void onResponse(Call<Localities> call, Response<Localities> response) {
                /* GET THE LIST OF LOCALITIES */
                arrLocalities = response.body().getLocalities();

                /* CAST THE PRIMARY ARRAY DATA TO THE FILTERED ARRAY */
                arrFilteredResults.addAll(arrLocalities);

                /* INSTANTIATE THE LOCATION SEARCH ADAPTER */
                searchAdapter = new LocalitiesSearchAdapter(arrFilteredResults);

                /* SET THE ADAPTER TO THE LOCALITIES RECYCLER VIEW */
                listLocalities.setAdapter(searchAdapter);

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Localities> call, Throwable t) {
//                Log.e("LOCALITIES FAILURE", t.getMessage());
                Crashlytics.logException(t);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_location_search, menu);
        MenuItem search = menu.findItem(R.id.menuSearchLocations);
        searchView = (SearchView) search.getActionView();
        searchView.setIconified(true);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String search) {
        searchAdapter.getFilter().filter(search);
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

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listLocalities.setLayoutManager(manager);
        listLocalities.setHasFixedSize(true);

        /* SET THE ADAPTER */
        listLocalities.setAdapter(searchAdapter);
    }

    /***** THE LOCATION SEARCH ADAPTER *****/
    private class LocalitiesSearchAdapter
            extends RecyclerView.Adapter<LocalitiesSearchAdapter.LocalitiesVH>
            implements Filterable {

        /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
        private final ArrayList<Locality> mArrayList;
        private ArrayList<Locality> mFilteredList;

        LocalitiesSearchAdapter(ArrayList<Locality> arrLocalities) {

            /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
            this.mArrayList = arrLocalities;
            this.mFilteredList = arrLocalities;
        }

        @Override
        public int getItemCount() {
            return mFilteredList.size();
        }

        @Override
        public void onBindViewHolder(LocalitiesSearchAdapter.LocalitiesVH holder, final int position) {
            final Locality data = mFilteredList.get(position);

            /* SET THE LOCALITY NAME */
            if (data.getLocalityName() != null) {
                holder.txtLocalityName.setText(data.getLocalityName());
            }

            /* PASS THE RESULT BACK TO THE CALLING ACTIVITY */
            holder.txtLocalityName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchView.clearFocus();
                    Intent intent = new Intent();
                    intent.putExtra("LOCALITY_ID", data.getLocalityID());
                    intent.putExtra("LOCALITY_NAME", data.getLocalityName());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }

        @Override
        public LocalitiesSearchAdapter.LocalitiesVH onCreateViewHolder(ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.filter_location_item, parent, false);

            return new LocalitiesSearchAdapter.LocalitiesVH(itemView);
        }

        class LocalitiesVH extends RecyclerView.ViewHolder	{
            final TextView txtLocalityName;

            LocalitiesVH(View v) {
                super(v);
                txtLocalityName = v.findViewById(R.id.txtLocalityName);
            }
        }

        @Override
        public Filter getFilter() {
            return new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String charString = constraint.toString();

                    if (charString.isEmpty()) {
                        mFilteredList = mArrayList;
                    } else {
                        ArrayList<Locality> filteredList = new ArrayList<>();
                        for (Locality data : mArrayList) {
                            if (data.getLocalityName().toLowerCase().contains(charString)) {
                                filteredList.add(data);
                            }
                        }
                        mFilteredList = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mFilteredList;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    //noinspection unchecked
                    mFilteredList = (ArrayList<Locality>) results.values;
                    notifyDataSetChanged();
                }
            };
        }
    }
}