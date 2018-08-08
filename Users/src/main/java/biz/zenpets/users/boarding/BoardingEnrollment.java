package biz.zenpets.users.boarding;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.TypefaceSpan;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.helpers.classes.location.LocationPickerActivity;
import biz.zenpets.users.utils.models.boarding.Boarding;
import biz.zenpets.users.utils.models.boarding.BoardingsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardingEnrollment extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE USER ID **/
    private String USER_ID = null;

    /** THE LOCATION REQUEST CODE **/
    private final int REQUEST_LOCATION = 1;

    /** THE HOME BOARDING ENROLLMENT DATA **/
    String BOARDING_ID = null;
    String BOARDING_ADDRESS = null;
    String BOARDING_PIN_CODE = null;
    Double BOARDING_LATITUDE = null;
    Double BOARDING_LONGITUDE = null;
    String BOARDING_DATE = null;

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog dialog;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.inputAddress) TextInputLayout inputAddress;
    @BindView(R.id.edtAddress) TextInputEditText edtAddress;
    @BindView(R.id.inputPinCode) TextInputLayout inputPinCode;
    @BindView(R.id.edtPinCode) TextInputEditText edtPinCode;
    @BindView(R.id.txtLocation) TextView txtLocation;
    @BindView(R.id.mapBoarding) MapView mapBoarding;

    /** CHOOSE THE BOARDING'S LOCATION ON A MAP **/
    @OnClick(R.id.btnLocationPicker) void chooseLocation()  {
        Intent intent = new Intent(this, LocationPickerActivity.class);
        startActivityForResult(intent, REQUEST_LOCATION);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_boarding_enrollment);
        ButterKnife.bind(this);
        mapBoarding.onCreate(savedInstanceState != null ? savedInstanceState.getBundle("boarding_map_save_state") : null);
        mapBoarding.onResume();
        mapBoarding.setClickable(false);

        /* GET THE USER ID */
        USER_ID = getApp().getUserID();

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* CALCULATE THE BOARDING DATE */
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        BOARDING_DATE = format.format(date);
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Home Boarding Enrollment";
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
        MenuInflater inflater = new MenuInflater(BoardingEnrollment.this);
        inflater.inflate(R.menu.activity_save_cancel, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuSave:
                /* CHECK BOARDING DETAILS */
                checkBoardingDetails();
                break;
            case R.id.menuCancel:
                this.finish();
                break;
            default:
                break;
        }
        return false;
    }

    /** CHECK BOARDING DETAILS **/
    private void checkBoardingDetails() {
        /* HIDE THE KEYBOARD **/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtAddress.getWindowToken(), 0);
        }

        /* COLLECT THE BOARDING DETAILS */
        BOARDING_ADDRESS = edtAddress.getText().toString().trim();
        BOARDING_PIN_CODE = edtPinCode.getText().toString().trim();

        /* VERIFY THE KENNEL DETAILS */
        if (TextUtils.isEmpty(BOARDING_ADDRESS))   {
            inputAddress.setError("Provide your address");
            inputAddress.setErrorEnabled(true);
            inputAddress.requestFocus();
            inputPinCode.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(BOARDING_PIN_CODE))  {
            inputPinCode.setError("Provide the Pin Code");
            inputPinCode.setErrorEnabled(true);
            inputPinCode.requestFocus();
            inputAddress.setErrorEnabled(false);
        } else if (BOARDING_LATITUDE == null || BOARDING_LONGITUDE == null) {
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            Toast.makeText(getApplicationContext(), "Please mark your location on the Map", Toast.LENGTH_LONG).show();
        } else {
            /* REGISTER THE USER FOR HOME BOARDING */
            registerHomeBoarding();
        }
    }

    /** REGISTER THE USER FOR HOME BOARDING **/
    private void registerHomeBoarding() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Enabling Home Boarding on your account...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();

        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        BoardingsAPI api = ZenApiClient.getClient().create(BoardingsAPI.class);
        Call<Boarding> call = api.enableHomeBoarding(
                USER_ID, BOARDING_ADDRESS, BOARDING_PIN_CODE,
                String.valueOf(BOARDING_LATITUDE), String.valueOf(BOARDING_LONGITUDE), BOARDING_DATE, "1");
        call.enqueue(new Callback<Boarding>() {
            @Override
            public void onResponse(Call<Boarding> call, Response<Boarding> response) {
                if (response.isSuccessful())    {
                    /* GET THE NEW BOARDING ID */
                    BOARDING_ID = response.body().getBoardingID();
                    Log.e("BOARDING ID", BOARDING_ID);

                    Intent success = new Intent();
                    setResult(RESULT_OK, success);
                    Toast.makeText(getApplicationContext(), "Registered successfully...", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    dialog.dismiss();
                    Toast.makeText(
                            getApplicationContext(),
                            "There was an error posting the new adoption. Please try again",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Boarding> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_LOCATION) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    BOARDING_LATITUDE = bundle.getDouble("LATITUDE");
                    BOARDING_LONGITUDE = bundle.getDouble("LONGITUDE");
                    if (BOARDING_LATITUDE != null && BOARDING_LONGITUDE != null) {
                        final LatLng latLng = new LatLng(BOARDING_LATITUDE, BOARDING_LONGITUDE);
                        mapBoarding.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                googleMap.getUiSettings().setMapToolbarEnabled(false);
                                googleMap.getUiSettings().setAllGesturesEnabled(false);
                                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                googleMap.setBuildingsEnabled(true);
                                googleMap.setTrafficEnabled(false);
                                googleMap.setIndoorEnabled(false);
                                MarkerOptions options = new MarkerOptions();
                                options.position(latLng);
                                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                Marker mMarker = googleMap.addMarker(options);
                                googleMap.addMarker(options);

                                /* MOVE THE MAP CAMERA */
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMarker.getPosition(), 10));
                                googleMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
                            }
                        });

                        /* SHOW THE BOARDING MAP */
                        mapBoarding.setVisibility(View.VISIBLE);

                        /* GET THE APPROXIMATE ADDRESS FOR DISPLAY */
                        try {
                            Geocoder geocoder;
                            List<Address> addresses;
                            geocoder = new Geocoder(this, Locale.getDefault());
                            addresses = geocoder.getFromLocation(BOARDING_LATITUDE, BOARDING_LONGITUDE, 1);
                            String address = addresses.get(0).getAddressLine(0);
                            if (!TextUtils.isEmpty(address)) {
                                txtLocation.setText(address);
                            } else {
                                // TODO: DISPLAY THE COORDINATES INSTEAD
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        /* HIDE THE BOARDING MAP */
                        mapBoarding.setVisibility(View.GONE);
                    }
                } else {
                    /* HIDE THE BOARDING MAP */
                    mapBoarding.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapBoarding.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapBoarding.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapBoarding.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapBoarding.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapBoarding.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Bundle bundle = new Bundle(outState);
        mapBoarding.onSaveInstanceState(bundle);
        outState.putBundle("boarding_map_save_state", bundle);
        super.onSaveInstanceState(outState);
    }
}