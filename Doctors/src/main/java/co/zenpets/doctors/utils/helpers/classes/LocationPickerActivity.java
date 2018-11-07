package co.zenpets.doctors.utils.helpers.classes;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.TypefaceSpan;

public class LocationPickerActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    /** A GOOGLE MAP INSTANCE **/
    private GoogleMap clinicMap;

    /** A FUSED LOCATION PROVIDER CLIENT INSTANCE**/
    private FusedLocationProviderClient mFusedLocationClient;

    /** A LOCATION INSTANCE **/
    private Location mLastLocation;

    /** A MARKER INSTANCE **/
    private Marker mMarker;

    /* A LATLNG INSTANCE */
    private LatLng SELECTED_COORDINATES;

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int ACCESS_FINE_LOCATION_CONSTANT = 200;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_picker);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* INSTANTIATE THE LOCATION CLIENT */
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(LocationPickerActivity.this);

        /* FETCH THE USER'S LOCATION */
        getUserLocation();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /***** GET THE USER'S LOCATION *****/
    private void getUserLocation() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(LocationPickerActivity.this,
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

                                /* SHOW THE PERMISSION DENIED DIALOG */
                                AlertDialog.Builder builder = new AlertDialog.Builder(LocationPickerActivity.this);
                                builder.setCancelable(false);
                                builder.setIcon(R.drawable.ic_info_outline_black_24dp);
                                builder.setTitle("Permission Denied!");
                                builder.setMessage("\nSince the location permission was denied, Zen Pets will not be able to find your location automatically for you. \n\nYou will now be sent back to the previous screen. Please perform the same action again and grant the permission. You can always revoke this permission later.");
                                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        Intent cancel = new Intent();
                                        setResult(RESULT_CANCELED, cancel);
                                        finish();
                                    }
                                });
                                builder.show();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(
                                        LocationPickerActivity.this,
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
                                SELECTED_COORDINATES = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                                /* SETUP THE MAP MARKERS */
                                setupMapMarkers();
                            } else {
//                                Log.e("EXCEPTION", String.valueOf(task.getException()));
//                                Crashlytics.logException(task.getException());
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
                /* SHOW THE DIALOG */
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setIcon(R.drawable.ic_info_outline_black_24dp);
                builder.setTitle("Permission Denied!");
                builder.setMessage("\nSince the location permission was denied, Zen Pets will not be able to find your location automatically for you. Please choose your location manually.");
                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        }
    }

    /***** SETUP THE MAP MARKERS *****/
    private void setupMapMarkers() {
        MarkerOptions options = new MarkerOptions();
        options.position(SELECTED_COORDINATES);
        options.title("Current Position");
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMarker = clinicMap.addMarker(options);
        mMarker.showInfoWindow();
        mMarker.setDraggable(true);

        /* MOVE THE MAP CAMERA */
        clinicMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMarker.getPosition(), 18));
        clinicMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        clinicMap = googleMap;
        clinicMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        clinicMap.setBuildingsEnabled(true);
        clinicMap.getUiSettings().setMapToolbarEnabled(false);
        clinicMap.setOnMarkerDragListener(this);
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = "Location Picker";
//        String strTitle = getString(R.string.add_a_new_medicine_record);
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getApplicationContext()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(LocationPickerActivity.this);
        inflater.inflate(R.menu.activity_save_cancel, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent back = new Intent();
                setResult(RESULT_CANCELED, back);
                this.finish();
                break;
            case R.id.menuSave:
                /* SEND THE COORDINATES TO THE CREATE ACCOUNT ACTIVITY */
                Intent success = new Intent();
                Bundle bundle = new Bundle();
                bundle.putDouble("LATITUDE", SELECTED_COORDINATES.latitude);
                bundle.putDouble("LONGITUDE", SELECTED_COORDINATES.longitude);
                success.putExtras(bundle);
                setResult(RESULT_OK, success);
                finish();
                break;
            case R.id.menuCancel:
                Intent cancel = new Intent();
                setResult(RESULT_CANCELED, cancel);
                this.finish();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        SELECTED_COORDINATES = marker.getPosition();
//        Toast.makeText(getApplicationContext(), "NEW COORDINATES: " + String.valueOf(SELECTED_COORDINATES), Toast.LENGTH_SHORT).show();
    }
}