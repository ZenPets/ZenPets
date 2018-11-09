package co.zenpets.location;

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

public class MainActivity extends AppCompatActivity {

    /** THE DATABASE INSTANCE **/
    private DBResto db;

    private ArrayList<StateData> arrStates = new ArrayList<>();
    private ArrayList<CityData> arrCities = new ArrayList<>();

    private String STATE_ID = null;
    private String SELECTED_STATE_NAME = null;
    private String CITY_ID = null;
    private String SELECTED_CITY_NAME = null;

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
//        spnCities = findViewById(R.id.spnCities);
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
                if (edtLatMin.getText().toString() != null
                        && edtLatMax.getText().toString() != null
                        && edtLngMin.getText().toString() != null
                        && edtLngMax.getText().toString() != null)  {
                    /* FETCH THE COORDINATES */
                    new fetchLocations().execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Fill all coordinates", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

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
//                    if (CITY_NAME != null && LOCALITY_NAME != null) {
//                        Log.e("CITY", CITY_NAME);
//                        Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    }
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
//                Log.e("CITY NAME", CITY_NAME);
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

                } else {
                    /* FETCH LOCATIONS (RECURSIVE) */
                    new fetchLocations().execute();
                }
            } else {
                /* FETCH LOCATIONS (RECURSIVE) */
                new fetchLocations().execute();
            }

//            /***** KERALA *****/
//            if (CITY_NAME != null && LOCALITY_NAME != null) {
//                if (CITY_NAME.equalsIgnoreCase("Ernakulam") && LOCALITY_NAME != null) {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("59", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Idukki"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("81", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Kalpetta"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("92", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Kannur"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("96", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Kasaragod"))    {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("101", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Kochi"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("105", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Kollam"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("107", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Kottayam"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("108", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Kozhikode"))    {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("110", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Meenadom"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("124", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Palakkad"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("154", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Parassala"))    {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("161", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Panjim"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("159", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Pathanamthitta"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("164", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Taliparamba"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("196", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Thiruvananthapuram"))    {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("201", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Thrissur"))    {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("203", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Wayanad"))    {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("215", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else {
//                    /* FETCH LOCATIONS (RECURSIVE) */
//                    new fetchLocations().execute();
//                }
//            } else {
//                /* FETCH LOCATIONS (RECURSIVE) */
//                new fetchLocations().execute();
//            }

//            /***** GOA *****/
//            if (CITY_NAME != null && LOCALITY_NAME != null) {
//                if (CITY_NAME.equalsIgnoreCase("Bardez") && LOCALITY_NAME != null) {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("22", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Benaulim"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("27", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Bicholim"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("226", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Canacona"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("227", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Caranzalem"))    {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("37", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Colva"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("45", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Cuncolim"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("228", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Curchorem"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("229", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Mapusa"))    {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("122", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Margao"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("123", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Mormugao"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("230", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Nerul"))    {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("147", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Panjim"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("159", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Pernem"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("231", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Ponda"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("232", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Porvorim"))    {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("169", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Quepem"))    {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("233", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Sanguem"))    {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("234", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Sanquelim"))    {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("235", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Valpoi"))    {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("236", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Vasco da Gama"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("210", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else {
//                    /* FETCH LOCATIONS (RECURSIVE) */
//                    new fetchLocations().execute();
//                }
//            } else {
//                /* FETCH LOCATIONS (RECURSIVE) */
//                new fetchLocations().execute();
//            }

//            /***** WEST BENGAL *****/
//            if (CITY_NAME != null && LOCALITY_NAME != null) {
////                Log.e("CITY", CITY_NAME);
////                Log.e("LOCALITY", LOCALITY_NAME);
//                if (CITY_NAME.equalsIgnoreCase("Asansol") && LOCALITY_NAME != null) {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("239", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Bardhaman"))    {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("240", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Barrackpore"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("25", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Darjeeling"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("243", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Durgapur"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("238", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Howrah"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("219", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Kolkata"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("106", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Maheshtala"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("221", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Malda"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("241", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else if (CITY_NAME.equalsIgnoreCase("Siliguri"))  {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality("218", LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
//                } else {
//                    /* FETCH LOCATIONS (RECURSIVE) */
//                    new fetchLocations().execute();
//                }
//            } else {
//                /* FETCH LOCATIONS (RECURSIVE) */
//                new fetchLocations().execute();
//            }

//            if (CITY_NAME != null && CITY_NAME.equalsIgnoreCase("Barrackpore")) {
//                if (LOCALITY_NAME != null
//                        && CITY_NAME != null
//                        && CITY_NAME.equalsIgnoreCase("Barrackpore")) {
//                    Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                    createLocality(LOCALITY_NAME);
//                    arrayList.add(LOCALITY_NAME);
//                    adapter.notifyDataSetChanged();
////                    sb.append(LOCALITY_NAME).append("\n\n");
////                    txtResults.setText(sb);
//                } else {
//                    /* FETCH LOCATIONS (RECURSIVE) */
//                    new fetchLocations().execute();
//                }
//            } else {
//                /* FETCH LOCATIONS (RECURSIVE) */
//                new fetchLocations().execute();
//            }
        }
    }

//    private void createLocality(String localityName) {
//        /* ADD THE NEW LOCALITY TO THE DATABASE */
//        db.addLocality("25", localityName);
//
//        /* FETCH LOCATIONS (RECURSIVE) */
//        new fetchLocations().execute();
//    }

    private void createLocality(String cityID, String localityName) {
        /* ADD THE NEW LOCALITY TO THE DATABASE */
        db.addLocality(cityID, localityName);

        /* FETCH LOCATIONS (RECURSIVE) */
        new fetchLocations().execute();
    }

//    private void fetchLocations() {
//
////        17.9370061,83.4214103,17z;
////        17.6876491,83.0857883,17z;
//
////        17.9187141,83.4543693,17z;
////        17.5416661,83.0873573,17z;
//
////        17.9268801,83.4368593,17z;
////        17.5986171,83.0701913,17z;
//
////        17.7105031,83.1306153,17z;
////        17.6844041,83.2933113,17z;
//
////        17.6900791,83.2925433,17z;
////        17.8277051,83.1993903,17z;
//
////        17.8509571,83.2584993,17z;
////        17.7796611,83.3838163,17z;
//
////        17.8340861,83.4129983,17z;
////        17.9018741,83.3790843,17z
//
//        Double minLatitude = 17.5406991;
//        Double maxLatitude = 17.9379251;
//        Random rLat = new Random();
//        Double randomLatitude = minLatitude + (maxLatitude - minLatitude) * rLat.nextDouble();
//
//        Double minLongitude = 83.0873023;
//        Double maxLongitude = 83.4217703;
//        Random rLng = new Random();
//        Double randomLongitude = minLongitude + (maxLongitude - minLongitude) * rLng.nextDouble();
//
//        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
//        try {
//            List<Address> addresses = geocoder.getFromLocation(randomLatitude,randomLongitude, 1);
//            if (addresses.size() > 0) {
//
//                String CITY_NAME = addresses.get(0).getLocality();
////                Log.e("CITY NAME", CITY_NAME);
//                if (CITY_NAME != null && CITY_NAME.equalsIgnoreCase("Visakhapatnam")) {
//
//                    String LOCALITY_NAME = addresses.get(0).getSubLocality();
//                    if (LOCALITY_NAME != null
//                            && CITY_NAME != null
//                            && CITY_NAME.equalsIgnoreCase("Visakhapatnam")) {
//                        Log.e("LOCALITY", LOCALITY_NAME + " : " + CITY_NAME);
//                        createLocality(LOCALITY_NAME);
//                    } else {
//                        /* FETCH LOCATIONS (RECURSIVE) */
//                        fetchLocations();
//                    }
//                } else {
//                    /* FETCH LOCATIONS (RECURSIVE) */
//                    fetchLocations();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    private void createLocality(String localityName) {
//        /* ADD THE NEW LOCALITY TO THE DATABASE */
//        db.addLocality("42", localityName);
//
//        /* FETCH LOCATIONS (AGAIN) */
//        fetchLocations();
//    }

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
//                Log.e("FAILURE", t.getMessage());
//                Crashlytics.logException(t);
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
//                Log.e("FAILURE", t.getMessage());
//                Crashlytics.logException(t);

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
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
}