package co.zenpets.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import co.zenpets.location.utils.CitiesData;
import co.zenpets.location.utils.CityData;
import co.zenpets.location.utils.LocationAPI;
import co.zenpets.location.utils.StateData;
import co.zenpets.location.utils.StatesAdapter;
import co.zenpets.location.utils.StatesData;
import co.zenpets.location.utils.ZenApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends AppCompatActivity {

    /** THE DATABASE INSTANCE **/
    private DBResto db;

    private ArrayList<StateData> arrStates = new ArrayList<>();
    private ArrayList<CityData> arrCities = new ArrayList<>();

    private String STATE_ID = null;
//    private String SELECTED_STATE_NAME = null;
    private String CITY_ID = null;
//    private String SELECTED_CITY_NAME = null;

    /* THE LOCALITIES COUNT */
    int intCount = 0;

    /** CAST THE LAYOUT ELEMENTS **/
    Toolbar myToolbar;
    Spinner spnStates, spnCities;
    EditText edtLatMin, edtLatMax, edtLngMin, edtLngMax;
    AppCompatTextView txtCoordinates;
    Button btnFetch;
    RecyclerView listLocalities;

    /** LOCALITIES ADAPTER AND ARRAY LIST **/
    LocalitiesAdapter adapter;
    ArrayList<String> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* INSTANTIATE THE DATABASE HELPER CLASS */
        db = new DBResto(MainActivity.this);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* CAST THE SPINNERS */
        spnStates = findViewById(R.id.spnStates);
        spnCities = findViewById(R.id.spnCities);

        /* CAST THE TEXT VIEW */
        txtCoordinates = findViewById(R.id.txtCoordinates);

        /* FETCH THE LIST OF STATES IN THE SELECTED COUNTRY */
        getAllStates();

        /* SELECT A STATE */
        spnStates.setOnItemSelectedListener(selectState);

        /* SELECT A CITY */
        spnCities.setOnItemSelectedListener(selectCity);

        /* INSTANTIATE THE LOCALITIES ADAPTER */
        adapter = new LocalitiesAdapter(MainActivity.this, arrayList);

        /* CAST THE LAYOUT ELEMENTS */
        edtLatMin = findViewById(R.id.edtLatMin);
        edtLatMax = findViewById(R.id.edtLatMax);
        edtLngMin = findViewById(R.id.edtLngMin);
        edtLngMax = findViewById(R.id.edtLngMax);
        btnFetch = findViewById(R.id.btnFetch);
        listLocalities = findViewById(R.id.listLocalities);
        LinearLayoutManager reviews = new LinearLayoutManager(this);
        reviews.setOrientation(LinearLayoutManager.VERTICAL);
        reviews.isAutoMeasureEnabled();
        listLocalities.setLayoutManager(reviews);
        listLocalities.setHasFixedSize(true);
        listLocalities.setNestedScrollingEnabled(false);
        listLocalities.setAdapter(adapter);

        /* FETCH THE LOCATIONS */
        btnFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* HIDE THE KEYBOARD */
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(edtLatMin.getWindowToken(), 0);
                }
                if (edtLatMin.getText().toString() != null
                        && edtLatMax.getText().toString() != null
                        && edtLngMin.getText().toString() != null
                        && edtLngMax.getText().toString() != null)  {
                    /* FETCH THE COORDINATES */
                    arrayList.clear();
                    adapter.notifyDataSetChanged();
                    intCount = 0;
                    new fetchLocations().execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Fill all coordinates", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class fetchLocations extends AsyncTask<Void, Void, Void> {

        String CITY_NAME = null;
        String LOCALITY_NAME = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Double minLatitude = Double.valueOf(edtLatMin.getText().toString());
            Double maxLatitude = Double.valueOf(edtLatMax.getText().toString());
            Random rLat = new Random();
            Double randomLatitude = minLatitude + (maxLatitude - minLatitude) * rLat.nextDouble();

            Double minLongitude = Double.valueOf(edtLngMin.getText().toString());
            Double maxLongitude = Double.valueOf(edtLngMax.getText().toString());
            Random rLng = new Random();
            Double randomLongitude = minLongitude + (maxLongitude - minLongitude) * rLng.nextDouble();

            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(randomLatitude,randomLongitude, 1);
                if (addresses.size() > 0) {

                    CITY_NAME = addresses.get(0).getLocality();
                    LOCALITY_NAME = addresses.get(0).getSubLocality();
                    if (CITY_NAME != null && LOCALITY_NAME != null) {
//                        Log.e("CITY", CITY_NAME);
//                        Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
                    }
                }
            } catch (final IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (CITY_NAME != null && LOCALITY_NAME != null) {
                Log.e("CITY NAME", CITY_NAME);
                if (CITY_NAME.equalsIgnoreCase("Mangaluru")) {
                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
                    createLocality("120", LOCALITY_NAME);
                    arrayList.add(LOCALITY_NAME);
                    adapter.notifyDataSetChanged();

                    /* INCREMENT THE NUMBER IN THE TITLE */
                    intCount++;
                    myToolbar.setTitle("Localities Count: " + intCount);
                    getSupportActionBar().setTitle("Localities Count: " + intCount);

                } else if (CITY_NAME.equalsIgnoreCase("Guwahati")) {
                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
                    createLocality("70", LOCALITY_NAME);
                    arrayList.add(LOCALITY_NAME);
                    adapter.notifyDataSetChanged();

                    /* INCREMENT THE NUMBER IN THE TITLE */
                    intCount++;
                    myToolbar.setTitle("Localities Count: " + intCount);
                    getSupportActionBar().setTitle("Localities Count: " + intCount);

                } else if (CITY_NAME.equalsIgnoreCase("Kamrup")) {
                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
                    createLocality("94", LOCALITY_NAME);
                    arrayList.add(LOCALITY_NAME);
                    adapter.notifyDataSetChanged();

                    /* INCREMENT THE NUMBER IN THE TITLE */
                    intCount++;
                    myToolbar.setTitle("Localities Count: " + intCount);
                    getSupportActionBar().setTitle("Localities Count: " + intCount);

                } else if (CITY_NAME.equalsIgnoreCase("Nagaon")) {
                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
                    createLocality("137", LOCALITY_NAME);
                    arrayList.add(LOCALITY_NAME);
                    adapter.notifyDataSetChanged();

                    /* INCREMENT THE NUMBER IN THE TITLE */
                    intCount++;
                    myToolbar.setTitle("Localities Count: " + intCount);
                    getSupportActionBar().setTitle("Localities Count: " + intCount);

                } else if (CITY_NAME.equalsIgnoreCase("Buxar"))  {
                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
                    createLocality("36", LOCALITY_NAME);
                    arrayList.add(LOCALITY_NAME);
                    adapter.notifyDataSetChanged();

                    /* INCREMENT THE NUMBER IN THE TITLE */
                    intCount++;
                    myToolbar.setTitle("Localities Count: " + intCount);
                    getSupportActionBar().setTitle("Localities Count: " + intCount);

                } else if (CITY_NAME.equalsIgnoreCase("Darbhanga"))  {
                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
                    createLocality("48", LOCALITY_NAME);
                    arrayList.add(LOCALITY_NAME);
                    adapter.notifyDataSetChanged();

                    /* INCREMENT THE NUMBER IN THE TITLE */
                    intCount++;
                    myToolbar.setTitle("Localities Count: " + intCount);
                    getSupportActionBar().setTitle("Localities Count: " + intCount);

                } else if (CITY_NAME.equalsIgnoreCase("Katihar"))  {
                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
                    createLocality("102", LOCALITY_NAME);
                    arrayList.add(LOCALITY_NAME);
                    adapter.notifyDataSetChanged();

                    /* INCREMENT THE NUMBER IN THE TITLE */
                    intCount++;
                    myToolbar.setTitle("Localities Count: " + intCount);
                    getSupportActionBar().setTitle("Localities Count: " + intCount);

                } else if (CITY_NAME.equalsIgnoreCase("Madhubani"))  {
                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
                    createLocality("117", LOCALITY_NAME);
                    arrayList.add(LOCALITY_NAME);
                    adapter.notifyDataSetChanged();

                    /* INCREMENT THE NUMBER IN THE TITLE */
                    intCount++;
                    myToolbar.setTitle("Localities Count: " + intCount);
                    getSupportActionBar().setTitle("Localities Count: " + intCount);

                } else if (CITY_NAME.equalsIgnoreCase("Patna")) {
                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
                    createLocality("166", LOCALITY_NAME);
                    arrayList.add(LOCALITY_NAME);
                    adapter.notifyDataSetChanged();

                    /* INCREMENT THE NUMBER IN THE TITLE */
                    intCount++;
                    myToolbar.setTitle("Localities Count: " + intCount);
                    getSupportActionBar().setTitle("Localities Count: " + intCount);

                } else if (CITY_NAME.equalsIgnoreCase("Puducherry"))  {
                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
                    createLocality("168", LOCALITY_NAME);
                    arrayList.add(LOCALITY_NAME);
                    adapter.notifyDataSetChanged();

                    /* INCREMENT THE NUMBER IN THE TITLE */
                    intCount++;
                    myToolbar.setTitle("Localities Count: " + intCount);
                    getSupportActionBar().setTitle("Localities Count: " + intCount);

                } else {
                    /* FETCH LOCATIONS (RECURSIVE) */
                    new fetchLocations().execute();
                }
            } else {
                /* FETCH LOCATIONS (RECURSIVE) */
                new fetchLocations().execute();
            }
        }
    }

    private void createLocality(String cityID, String localityName) {
        /* ADD THE NEW LOCALITY TO THE DATABASE */
        db.addLocality(cityID, localityName);

        /* FETCH LOCATIONS (RECURSIVE) */
        new fetchLocations().execute();
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setSubtitle(null);
    }

    /***** FETCH THE LIST OF STATES IN THE SELECTED COUNTRY *****/
    private void getAllStates() {
        LocationAPI api = ZenApiClient.getClient().create(LocationAPI.class);
        Call<StatesData> call = api.allStates("51");
        call.enqueue(new Callback<StatesData>() {
            @Override
            public void onResponse(Call<StatesData> call, Response<StatesData> response) {
                arrStates = response.body().getStates();
                if (arrStates != null && arrStates.size() > 0)  {
                    /* INSTANTIATE THE STATES ADAPTER */
                    StatesAdapter statesAdapter = new StatesAdapter(MainActivity.this, arrStates);

                    /* SET THE STATES SPINNER */
                    spnStates.setAdapter(statesAdapter);
                }
            }

            @Override
            public void onFailure(Call<StatesData> call, Throwable t) {
                Log.e("STATES FAILURE", t.getMessage());
            }
        });
    }

    /***** FETCH THE LIST OF CITIES IN THE SELECTED STATE *****/
    private void getAllCities() {
        LocationAPI api = ZenApiClient.getClient().create(LocationAPI.class);
        Call<CitiesData> call = api.allCities(STATE_ID);
        call.enqueue(new Callback<CitiesData>() {
            @Override
            public void onResponse(Call<CitiesData> call, Response<CitiesData> response) {
                arrCities = response.body().getCities();
                if (arrCities != null && arrCities.size() > 0)  {
                    /* INSTANTIATE THE CITIES ADAPTER */
                    CitiesAdapter citiesAdapter = new CitiesAdapter(MainActivity.this, arrCities);

                    /* SET THE CITIES SPINNER */
                    spnCities.setAdapter(citiesAdapter);
                }
            }

            @Override
            public void onFailure(Call<CitiesData> call, Throwable t) {
                Log.e("CITIES FAILURE", t.getMessage());
            }
        });
    }

    /***** SELECT A THE STATE *****/
    private final AdapterView.OnItemSelectedListener selectState = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            STATE_ID = arrStates.get(position).getStateID();

            /* CLEAR THE CITIES ARRAY LIST */
            if (arrCities != null)
                arrCities.clear();

            /* FETCH THE LIST OF CITIES */
            if (STATE_ID != null)   {
                /* FETCH THE LIST OF CITIES IN THE SELECTED STATE */
                getAllCities();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    /***** SELECT A CITY *****/
    private final AdapterView.OnItemSelectedListener selectCity = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            CITY_ID = arrCities.get(position).getCityID();

            /* FETCH THE LIST OF CITY COORDINATES */
            if (CITY_ID != null)   {
                if (CITY_ID.equalsIgnoreCase("70"))   {
                    txtCoordinates.setText(getString(R.string.assam_guwahati));
                } else if (CITY_ID.equalsIgnoreCase("94"))    {
                    txtCoordinates.setText(getString(R.string.assam_kamrup));
                } else if (CITY_ID.equalsIgnoreCase("137"))    {
                    txtCoordinates.setText(getString(R.string.assam_nagaon));
                } else if (CITY_ID.equalsIgnoreCase("36"))  {
                    txtCoordinates.setText(getString(R.string.bihar_buxar));
                } else if (CITY_ID.equalsIgnoreCase("48"))  {
                    txtCoordinates.setText(getString(R.string.bihar_darbhanga));
                } else if (CITY_ID.equalsIgnoreCase("102"))    {
                    txtCoordinates.setText(getString(R.string.bihar_katihar));
                } else if (CITY_ID.equalsIgnoreCase("117"))    {
                    txtCoordinates.setText(getString(R.string.bihar_madhubani));
                } else if (CITY_ID.equalsIgnoreCase("166")) {
                    txtCoordinates.setText(getString(R.string.bihar_patna));
                } else if (CITY_ID.equalsIgnoreCase("168")){
                    txtCoordinates.setText(getString(R.string.pondicherry));
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
}