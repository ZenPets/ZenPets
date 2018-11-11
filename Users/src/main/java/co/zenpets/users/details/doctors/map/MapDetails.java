package co.zenpets.users.details.doctors.map;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.zenpets.users.R;
import co.zenpets.users.creator.appointment.AppointmentSlotCreator;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.helpers.directions.DataParser;
import co.zenpets.users.utils.models.doctors.subscription.SubscriptionData;
import co.zenpets.users.utils.models.doctors.subscription.SubscriptionsAPI;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressWarnings("ConstantConditions")
public class MapDetails extends AppCompatActivity 
        implements OnMapReadyCallback {

    /** THE INCOMING DATA **/
    private String DOCTOR_ID = null;
    private String DOCTOR_NAME = null;
    private String DOCTOR_PHONE_NUMBER = null;
    private String CLINIC_ID = null;
    private String CLINIC_NAME = null;
    private Double CLINIC_LATITUDE = null;
    private Double CLINIC_LONGITUDE = null;
    private String CLINIC_ADDRESS = null;
    private String CLINIC_DISTANCE = null;

    /** THE DOCTOR'S SUBSCRIPTION STATUS FLAG **/
    private boolean blnSubscriptionStatus = false;

    /** A FUSED LOCATION PROVIDER CLIENT INSTANCE**/
    private FusedLocationProviderClient mFusedLocationClient;

    /** A LOCATION INSTANCE **/
    private Location mLastLocation;

    /** THE LATLNG INSTANCES FOR CALCULATING THE DISTANCE **/
    private LatLng LATLNG_ORIGIN;
    private LatLng LATLNG_DESTINATION;

    /** A MARKER INSTANCE **/
    private Marker mMarker;

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int ACCESS_FINE_LOCATION_CONSTANT = 200;

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int CALL_PHONE_CONSTANT = 201;

    /** DECLARE A GOOGLE MAP INSTANCE **/
    private GoogleMap clinicMap;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtDoctorName) AppCompatTextView txtDoctorName;
    @BindView(R.id.txtClinicName) AppCompatTextView txtClinicName;
    @BindView(R.id.txtClinicAddress) AppCompatTextView txtClinicAddress;
    @BindView(R.id.txtDistance) AppCompatTextView txtDistance;

    /** SHOW THE DIRECTIONS IN GOOGLE MAP **/
    @OnClick(R.id.txtDirections) void showDirections() {
        showClinicDirections();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_details);
        ButterKnife.bind(this);

        /* INSTANTIATE THE LOCATION CLIENT */
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MapDetails.this);

        /* FETCH THE USER'S LOCATION */
        getUserLocation();

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* CAST THE GOOGLE MAP INSTANCE */
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.clinicMap);
        mapFragment.getMapAsync(this);
    }

    /***** GET THE USER'S LOCATION *****/
    private void getUserLocation() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(MapDetails.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)   {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))    {
                /* SHOW THE DIALOG */
                new MaterialDialog.Builder(this)
                        .icon(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_black_24dp))
                        .title("Permission Required")
                        .cancelable(true)
                        .content("\nZen Pets requires the Location Permission to show you the appropriate listings near / at your location. \n\nFor a seamless experience, we recommend granting Zen Pets this permission.")
                        .positiveText("Grant Permission")
                        .negativeText("Not Now")
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
                                        MapDetails.this,
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
                                mLastLocation = task.getResult();

                                /* GET THE ORIGIN LATLNG */
                                LATLNG_ORIGIN = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//                                Log.e("ORIGIN", String.valueOf(LATLNG_ORIGIN));

                                /* GET THE INCOMING DATA */
                                getIncomingData();
                            } else {
//                                Log.e("EXCEPTION", String.valueOf(task.getException()));
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
                //TODO: TRY ALTERNATIVE METHOD
            }
        } else if (requestCode == CALL_PHONE_CONSTANT)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                /* CALL THE PHONE NUMBER */
                callPhone();
            } else {
                /* DIAL THE PHONE NUMBER */
                dialPhone();
            }
        }
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null
                && bundle.containsKey("DOCTOR_ID")
                && bundle.containsKey("DOCTOR_NAME")
                && bundle.containsKey("DOCTOR_PHONE_NUMBER")
                && bundle.containsKey("CLINIC_ID")
                && bundle.containsKey("CLINIC_NAME")
                && bundle.containsKey("CLINIC_LATITUDE")
                && bundle.containsKey("CLINIC_LONGITUDE")
                && bundle.containsKey("CLINIC_ADDRESS")) {
            DOCTOR_ID = bundle.getString("DOCTOR_ID");
            DOCTOR_NAME = bundle.getString("DOCTOR_NAME");
            DOCTOR_PHONE_NUMBER = bundle.getString("DOCTOR_PHONE_NUMBER");
            CLINIC_ID = bundle.getString("CLINIC_ID");
            CLINIC_NAME = bundle.getString("CLINIC_NAME");
            CLINIC_LATITUDE = bundle.getDouble("CLINIC_LATITUDE");
            CLINIC_LONGITUDE = bundle.getDouble("CLINIC_LONGITUDE");
            CLINIC_ADDRESS = bundle.getString("CLINIC_ADDRESS");

            /* FETCH THE DOCTOR'S SUBSCRIPTION */
            fetchDoctorSubscription();

            /* INSTANTIATE THE LATLNG DESTINATION */
            LATLNG_DESTINATION = new LatLng(CLINIC_LATITUDE, CLINIC_LONGITUDE);
            Log.e("LONGITUDE", String.valueOf(CLINIC_LONGITUDE));
            Log.e("LATITUDE", String.valueOf(CLINIC_LATITUDE));
            Log.e("DESTINATION", String.valueOf(LATLNG_DESTINATION));

            /* SET THE DISTANCE BETWEEN THE ORIGIN AND THE DESTINATION */
            String URL_DISTANCE = getUrl(LATLNG_ORIGIN, LATLNG_DESTINATION);
//            Log.e("URL", URL_DISTANCE);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(URL_DISTANCE)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                    Log.e("FAILURE", e.toString());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        String strResult = response.body().string();
                        JSONObject JORoot = new JSONObject(strResult);
//                        Log.e("ROOT", String.valueOf(JORoot));
                        JSONArray array = JORoot.getJSONArray("routes");
                        JSONObject JORoutes = array.getJSONObject(0);
                        JSONArray JOLegs= JORoutes.getJSONArray("legs");
                        JSONObject JOSteps = JOLegs.getJSONObject(0);
                        JSONObject JODistance = JOSteps.getJSONObject("distance");
                        if (JODistance.has("text")) {
                            CLINIC_DISTANCE = JODistance.getString("text");
                        } else {
                            CLINIC_DISTANCE = "Not Available";
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txtDistance.setText(getString(R.string.generic_tilde) + " " + CLINIC_DISTANCE);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            if (DOCTOR_NAME != null)    {
                txtDoctorName.setText(DOCTOR_NAME);
            }

            if (CLINIC_NAME != null)    {
                txtClinicName.setText(CLINIC_NAME);
            }

            if (CLINIC_ADDRESS != null) {
                txtClinicAddress.setText(CLINIC_ADDRESS);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** SETUP THE MAP MARKERS *****/
    private void setupMapMarkers() {
        MarkerOptions options = new MarkerOptions();
        options.position(LATLNG_ORIGIN);
//        options.title("Current Position");
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMarker = clinicMap.addMarker(options);
        mMarker.showInfoWindow();
        mMarker.setDraggable(true);

        /* MOVE THE MAP CAMERA */
        clinicMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMarker.getPosition(), 10));
        clinicMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);

        LATLNG_DESTINATION = new LatLng(CLINIC_LATITUDE, CLINIC_LONGITUDE);
//        Log.e("CLINIC COORDS", String.valueOf(destination));
        MarkerOptions clinicOptions = new MarkerOptions();
        clinicOptions.position(LATLNG_DESTINATION);
        clinicOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        clinicMap.addMarker(clinicOptions);

        float distance[] = new float[1];
        Location.distanceBetween(LATLNG_ORIGIN.latitude, LATLNG_ORIGIN.longitude, LATLNG_DESTINATION.latitude, LATLNG_DESTINATION.longitude, distance);

        // Getting URL to the Google Directions API
        String url = getUrl(LATLNG_ORIGIN, LATLNG_DESTINATION);
//        Log.e("onMapClick", url.toString());
        FetchUrl FetchUrl = new FetchUrl();

        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
        //move map camera
        clinicMap.moveCamera(CameraUpdateFactory.newLatLng(LATLNG_ORIGIN));
        clinicMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        clinicMap = googleMap;
        clinicMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        clinicMap.setBuildingsEnabled(true);
        clinicMap.getUiSettings().setMapToolbarEnabled(false);

        /* SETUP THE MAP MARKERS */
        setupMapMarkers();

        /* SET THE MAP STYLE */
//        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.zen_map_style);
//        clinicMap.setMapStyle(mapStyleOptions);
    }

    /** PARSE THE RESULT OF THE DIRECTIONS URL CALL **/
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        String distance = null;

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DataParser parser = new DataParser();

                JSONArray array = jObject.getJSONArray("routes");
                JSONObject JORoutes = array.getJSONObject(0);
                JSONArray JOLegs= JORoutes.getJSONArray("legs");
                JSONObject JOSteps = JOLegs.getJSONObject(0);
                JSONObject JODistance = JOSteps.getJSONObject("distance");
                if (JODistance.has("text")) {
                    distance = JODistance.getString("text");
                }

                /* START PARSING THE ROUTES */
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            if (result.size() != 0) {
                /* TRAVERSING THROUGH ALL THE ROUTES */
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    /* FETCHING THE I-TH ROUTE */
                    List<HashMap<String, String>> path = result.get(i);

                    /* FETCHING ALL THE POINTS IN THE I-TH ROUTE */
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    /* ADDING ALL THE POINTS IN THE ROUTE TO THE LINE OPTIONS */
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(Color.RED);
                }
            }

            /* DRAWING POLYLINE IN THE GOOGLE MAP FOR THE I-TH ROUTE */
            if(lineOptions != null) {
                clinicMap.addPolyline(lineOptions);
            }
        }
    }

    /** FETCH DATA FROM THE DIRECTIONS URL **/
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            /* FOR STORING DATA FROM THE WEB SERVICE */
            String data = "";

            try {
                /* FETCH THE DATA FROM THE WEB SERVICE */
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            /* INVOKES THE THREAD FOR PARSING THE JSON DATA */
            parserTask.execute(result);
        }
    }

    /** CREATE THE DIRECTIONS URL **/
    private String getUrl(LatLng origin, LatLng dest) {
        /* ORIGIN OF THE ROUTE */
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        /* DESTINATION OF THE ROUTE */
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        /* THE API KEY */
        String strKey = "key=" + getString(R.string.google_directions_api_key);

        /* SENSOR ENABLED */
        String sensor = "sensor=false";

        /* BUILDING THE PARAMETERS FOR THE WEB SERVICE */
        String parameters = str_origin + "&" + str_dest + "&" + strKey + "&" + sensor;

        /* THE OUTPUT FORMAT */
        String output = "json";

        /* BUILD THE FINAL URL FOR THE WEB SERVICE */
        String strUrl = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
//        Log.e("DIRECTIONS", strUrl);
        return strUrl;
    }

    /** DOWNLOAD THE URL TO FETCH THE DIRECTIONS **/
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            /* CREATING AN HTTP CONNECTION TO COMMUNICATE WITH THE URL */
            urlConnection = (HttpURLConnection) url.openConnection();

            /* CONNECTING TO THE URL */
            urlConnection.connect();

            /* READING DATA FROM THE URL */
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /***** CONFIGURE THE TOOLBAR *****/
    @SuppressWarnings("ConstantConditions")
    private void configTB() {
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
        MenuInflater inflater = new MenuInflater(MapDetails.this);
        inflater.inflate(R.menu.activity_doctor_details, menu);

        MenuItem menuBook = menu.findItem(R.id.menuBook);
        MenuItem menuCall = menu.findItem(R.id.menuCall);

        if (blnSubscriptionStatus) {
            menuBook.setVisible(true);
            menuCall.setVisible(false);
        } else {
            menuCall.setVisible(true);
            menuBook.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuBook:
                Intent intentAppointment = new Intent(getApplicationContext(), AppointmentSlotCreator.class);
                intentAppointment.putExtra("DOCTOR_ID", DOCTOR_ID);
                intentAppointment.putExtra("CLINIC_ID", CLINIC_ID);
                startActivity(intentAppointment);
                break;
            case R.id.menuCall:
                if (ContextCompat.checkSelfPermission(MapDetails.this,
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED)   {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE))    {
                        /* SHOW THE DIALOG */
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setCancelable(false);
                        builder.setIcon(R.drawable.ic_info_outline_black_24dp);
                        builder.setTitle("Permission Required");
                        builder.setMessage("\nZen Pets requires the permission to call the DoctorProfile's phone number. \n\nFor a seamless experience, we recommend granting Zen Pets this permission.");
                        builder.setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(
                                        MapDetails.this,
                                        new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_CONSTANT);
                            }
                        });
                        builder.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                CALL_PHONE_CONSTANT);
                    }
                } else {
                    /* CALL THE PHONE NUMBER */
                    callPhone();
                }
                break;
            default:
                break;
        }
        return false;
    }

    /***** CHECK IF GOOGLE MAPS IS INSTALLED *****/
    private boolean appInstalledOrNot(String s) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(s, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    /***** CALL THE PHONE NUMBER *****/
    private void callPhone() {
        if (ContextCompat.checkSelfPermission(MapDetails.this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED)   {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE))    {
                /* SHOW THE DIALOG */
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setIcon(R.drawable.ic_info_outline_black_24dp);
                builder.setTitle("Permission Required");
                builder.setMessage("\nZen Pets requires the permission to call the DoctorProfile's phone number. \n\nFor a seamless experience, we recommend granting Zen Pets this permission.");
                builder.setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(
                                MapDetails.this,
                                new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_CONSTANT);
                    }
                });
                builder.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        CALL_PHONE_CONSTANT);
            }
        } else {
            String myData= "tel:" + DOCTOR_PHONE_NUMBER;
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(myData));
            startActivity(intent);
        }
    }

    /***** DIAL THE PHONE NUMBER *****/
    private void dialPhone() {
        String myData= "tel:" + DOCTOR_PHONE_NUMBER;
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(myData));
        startActivity(intent);
    }

    /***** FETCH THE DOCTOR'S SUBSCRIPTION *****/
    private void fetchDoctorSubscription() {
        SubscriptionsAPI api = ZenApiClient.getClient().create(SubscriptionsAPI.class);
        retrofit2.Call<SubscriptionData> call = api.fetchDoctorSubscription(DOCTOR_ID);
        call.enqueue(new retrofit2.Callback<SubscriptionData>() {
            @Override
            public void onResponse(retrofit2.Call<SubscriptionData> call, retrofit2.Response<SubscriptionData> response) {
                SubscriptionData data = response.body();
                if (data != null) {
                    /* GET THE SUBSCRIPTION ID */
                    String subscriptionID = data.getSubscriptionID();

                    if (subscriptionID != null && !subscriptionID.equalsIgnoreCase("")) {
                        /* PROCESS THE SUBSCRIPTION LEVEL AND INVALIDATE THE OPTIONS MENU */
                        blnSubscriptionStatus = !subscriptionID.equalsIgnoreCase("1");
                        invalidateOptionsMenu();
                    } else {
                        blnSubscriptionStatus = false;
                        invalidateOptionsMenu();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<SubscriptionData> call, Throwable t) {
//                Log.e("SUBSCRIPTION FAILURE", t.getMessage());
            }
        });
    }

    /***** SHOW THE DIRECTIONS IN GOOGLE MAP *****/
    private void showClinicDirections() {
        String strOrigin =
                "saddr=" + Double.toString(LATLNG_ORIGIN.latitude) + "," + Double.toString(LATLNG_ORIGIN.longitude);

        String strDestination =
                "&daddr=" + Double.toString(LATLNG_DESTINATION.latitude) + "," + Double.toString(LATLNG_DESTINATION.longitude);
        String mapURL = "http://maps.google.com/maps?" + strOrigin + strDestination + "&dirflg=d";

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(mapURL));
        startActivity(i);

//        boolean isAppInstalled = appInstalledOrNot("com.google.android.apps.maps");
//        if (isAppInstalled) {
//            String strOrigin =
//                    "saddr=" + Double.toString(LATLNG_ORIGIN.latitude) + "," + Double.toString(LATLNG_ORIGIN.longitude);
//
//            String strDestination =
//                    "&daddr=" + Double.toString(LATLNG_DESTINATION.latitude) + "," + Double.toString(LATLNG_DESTINATION.longitude);
//
//            Intent intent = new Intent(
//                    android.content.Intent.ACTION_VIEW,
//                    Uri.parse("http://maps.google.com/maps?" + strOrigin + strDestination + "&dirflg=d"));
//
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
//            startActivity(intent);
//        } else {
//            new MaterialDialog.Builder(MapDetails.this)
//                    .title("Google Maps Required")
//                    .content("Google Maps is necessary to show you the directions to the Clinic. Please install Google Maps to use this feature")
//                    .positiveText("Download It")
//                    .negativeText("Not Now")
//                    .theme(Theme.LIGHT)
//                    .icon(ContextCompat.getDrawable(MapDetails.this, R.drawable.ic_info_outline_black_24dp))
//                    .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
//                    .onPositive(new MaterialDialog.SingleButtonCallback() {
//                        @Override
//                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                            String strAppPackage = "com.google.android.apps.maps";
//                            try {
//                                startActivity(new Intent(
//                                        Intent.ACTION_VIEW,
//                                        Uri.parse("market://details?id=" + strAppPackage)));
//
//                            } catch (ActivityNotFoundException e) {
//                                startActivity(new Intent(
//                                        Intent.ACTION_VIEW,
//                                        Uri.parse("https://play.google.com/store/apps/details?id=" + strAppPackage)));
//                            }
//                        }
//                    })
//                    .onNegative(new MaterialDialog.SingleButtonCallback() {
//                        @Override
//                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                            dialog.dismiss();
//                        }
//                    })
//                    .show();
//        }
    }
}