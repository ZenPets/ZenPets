package co.zenpets.doctors.modifier.clinic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.TypefaceSpan;
import co.zenpets.doctors.utils.helpers.classes.LocationPickerActivity;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.models.clinics.ClinicData;
import co.zenpets.doctors.utils.models.clinics.ClinicsAPI;
import co.zenpets.doctors.utils.models.clinics.details.ClinicDetailsAPI;
import co.zenpets.doctors.utils.models.doctors.clinic.Clinic;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClinicModifier extends AppCompatActivity {

    /** THE INCOMING CLINIC ID **/
    private String CLINIC_ID = null;

    /** STRINGS TO HOLD THE USER PROVIDED INFORMATION **/
    private String CLINIC_NAME = null;
    private String PHONE_NUMBER_1 = null;
    private String PHONE_NUMBER_2 = null;
    private String CLINIC_ADDRESS = null;
    private String CLINIC_LANDMARK = null;
    private String CLINIC_PIN_CODE = null;
    private String COUNTRY_ID = "51";
    private String STATE_ID = null;
    private String STATE_NAME = null;
    private String CITY_ID = null;
    private String CITY_NAME = null;
    private String LOCALITY_ID = null;
    private String LOCALITY_NAME = null;
    private String PIN_CODE = null;
    private String LANDMARK = null;
    private Double CLINIC_LATITUDE;
    private Double CLINIC_LONGITUDE;

    /** THE LOCATION REQUEST CODE **/
    private final int REQUEST_LOCATION = 101;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.inputClinicName) TextInputLayout inputClinicName;
    @BindView(R.id.edtClinicName) AppCompatEditText edtClinicName;
    @BindView(R.id.inputPhone1) TextInputLayout inputPhone1;
    @BindView(R.id.edtPhone1) AppCompatEditText edtPhone1;
    @BindView(R.id.inputPhone2) TextInputLayout inputPhone2;
    @BindView(R.id.edtPhone2) AppCompatEditText edtPhone2;
    @BindView(R.id.inputClinicAddress) TextInputLayout inputClinicAddress;
    @BindView(R.id.edtClinicAddress) AppCompatEditText edtClinicAddress;
    @BindView(R.id.inputPinCode) TextInputLayout inputPinCode;
    @BindView(R.id.edtPinCode) AppCompatEditText edtPinCode;
    @BindView(R.id.txtClinicState) AppCompatTextView txtClinicState;
    @BindView(R.id.txtClinicCity) AppCompatTextView txtClinicCity;
    @BindView(R.id.txtClinicLocality) AppCompatTextView txtClinicLocality;
    @BindView(R.id.inputLandmark) TextInputLayout inputLandmark;
    @BindView(R.id.edtLandmark) AppCompatEditText edtLandmark;
    @BindView(R.id.txtLocation) AppCompatTextView txtLocation;

    /** SELECT THE CLINIC LOCATION ON A MAP **/
    @OnClick(R.id.btnLocationPicker) protected void pickLocation()  {
        Intent intent = new Intent(this, LocationPickerActivity.class);
        startActivityForResult(intent, REQUEST_LOCATION);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clinic_modifier);
        ButterKnife.bind(this);

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* CONFIGURE THE ACTIONBAR */
        configAB();
    }

    /***** FETCH THE CLINIC DETAILS *****/
    private void fetchClinicDetails() {
        ClinicDetailsAPI apiInterface = ZenApiClient.getClient().create(ClinicDetailsAPI.class);
        Call<ClinicData> call = apiInterface.fetchClinicDetails(CLINIC_ID);
        call.enqueue(new Callback<ClinicData>() {
            @Override
            public void onResponse(Call<ClinicData> call, Response<ClinicData> response) {
                ClinicData data = response.body();
                if (data != null)   {
                    CLINIC_NAME = data.getClinicName();
                    CLINIC_ADDRESS = data.getClinicAddress();
                    CLINIC_LANDMARK = data.getClinicLandmark();
                    CLINIC_PIN_CODE = data.getClinicPinCode();
                    CLINIC_LATITUDE = Double.valueOf(data.getClinicLatitude());
                    CLINIC_LONGITUDE = Double.valueOf(data.getClinicLongitude());
                    PHONE_NUMBER_1 = data.getClinicPhone1();
                    PHONE_NUMBER_2 = data.getClinicPhone2();
                    COUNTRY_ID = data.getCountryID();
                    STATE_ID = data.getStateID();
                    CITY_ID = data.getCityID();
                    LOCALITY_ID = data.getLocalityID();

                    /* SET THE CLINIC NAME */
                    edtClinicName.setText(CLINIC_NAME);

                    /* SET THE CLINIC ADDRESS */
                    edtClinicAddress.setText(CLINIC_ADDRESS);

                    /* SET THE CLINIC LANDMARK */
                    edtLandmark.setText(CLINIC_LANDMARK);

                    /* SET THE PIN CODE */
                    edtPinCode.setText(CLINIC_PIN_CODE);

                    /* SET THE CLINIC PHONE 1 */
                    if (PHONE_NUMBER_1 != null && !PHONE_NUMBER_1.equals(""))   {
                        edtPhone1.setText(PHONE_NUMBER_1);
                    } else {
                        edtPhone1.setText("");
                    }

                    /* SET THE CLINIC PHONE 2 */
                    if (PHONE_NUMBER_2 != null && !PHONE_NUMBER_2.equals(""))   {
                        edtPhone2.setText(PHONE_NUMBER_2);
                    } else {
                        edtPhone2.setText("");
                    }

                    /* GET AND SET THE STATE NAME */
                    STATE_NAME = data.getStateName();
                    txtClinicState.setText(STATE_NAME);

                    /* GET AND SET THE CITY NAME */
                    CITY_NAME = data.getCityName();
                    txtClinicCity.setText(CITY_NAME);

                    /* GET AND SET THE LOCALITY NAME */
                    LOCALITY_NAME = data.getLocalityName();
                    txtClinicLocality.setText(LOCALITY_NAME);

                    /* GET THE APPROXIMATE ADDRESS FOR DISPLAY */
                    try {
                        Geocoder geocoder;
                        List<Address> addresses;
                        geocoder = new Geocoder(ClinicModifier.this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(CLINIC_LATITUDE, CLINIC_LONGITUDE, 1);
                        String address = addresses.get(0).getAddressLine(0);
                        if (!TextUtils.isEmpty(address)) {
                            txtLocation.setText(address);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ClinicData> call, Throwable t) {
//                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("CLINIC_ID")) {
            CLINIC_ID = bundle.getString("CLINIC_ID");
            if (CLINIC_ID != null)  {
                /* FETCH THE CLINIC DETAILS */
                fetchClinicDetails();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Update Clinic";
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
        MenuInflater inflater = new MenuInflater(ClinicModifier.this);
        inflater.inflate(R.menu.activity_save_cancel, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menuSave:
                /* CHECK FOR ALL CLINIC DETAILS */
                checkClinicDetails();
                break;
            case R.id.menuCancel:
                finish();
                break;
            default:
                break;
        }
        return false;
    }

    /***** CHECK FOR ALL CLINIC DETAILS *****/
    private void checkClinicDetails() {
        /* HIDE THE KEYBOARD */
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtClinicName.getWindowToken(), 0);
        }

        /* COLLECT ALL THE DATA */
        CLINIC_NAME = edtClinicName.getText().toString().trim();
        PHONE_NUMBER_1 = edtPhone1.getText().toString().trim();
        PHONE_NUMBER_2 = edtPhone2.getText().toString().trim();
        CLINIC_ADDRESS = edtClinicAddress.getText().toString().trim();
        CLINIC_LANDMARK = edtLandmark.getText().toString().trim();
        PIN_CODE = edtPinCode.getText().toString().trim();
        LANDMARK = edtLandmark.getText().toString().trim();

        /* VALIDATE THE DATA */
        if (TextUtils.isEmpty(CLINIC_NAME)) {
            inputClinicName.setErrorEnabled(false);
            inputPhone1.setErrorEnabled(false);
            inputClinicAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputClinicName.setError(getString(R.string.clinic_name_empty));
            inputClinicName.requestFocus();
        } else if (TextUtils.isEmpty(PHONE_NUMBER_1))   {
            inputClinicName.setErrorEnabled(false);
            inputPhone1.setErrorEnabled(false);
            inputClinicAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhone1.setError(getString(R.string.clinic_phone_empty));
            inputPhone1.requestFocus();
        } else if (TextUtils.isEmpty(CLINIC_ADDRESS)) {
            inputClinicName.setErrorEnabled(false);
            inputPhone1.setErrorEnabled(false);
            inputClinicAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputClinicAddress.setError(getString(R.string.clinic_postal_address_empty));
            inputClinicAddress.requestFocus();
        } else if (TextUtils.isEmpty(PIN_CODE)) {
            inputClinicName.setErrorEnabled(false);
            inputPhone1.setErrorEnabled(false);
            inputClinicAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPinCode.setError(getString(R.string.clinic_pin_code_empty));
            inputPinCode.requestFocus();
        } else if (CLINIC_LONGITUDE == null || CLINIC_LATITUDE == null) {
            inputClinicName.setErrorEnabled(false);
            inputPhone1.setErrorEnabled(false);
            inputClinicAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            Toast.makeText(getApplicationContext(), "Please select / mark your Location on the Map", Toast.LENGTH_LONG).show();
        } else {
            inputClinicName.setErrorEnabled(false);
            inputPhone1.setErrorEnabled(false);
            inputClinicAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);

            /* SHOW THE PROGRESS DIALOG WHILE UPLOADING THE IMAGE **/
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Updating the Clinic details...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();

            /* UPDATE THE CLINIC DETAILS */
            ClinicsAPI api = ZenApiClient.getClient().create(ClinicsAPI.class);
            Call<Clinic> call = api.updateClinic(
                    CLINIC_ID, CLINIC_NAME, CLINIC_ADDRESS, CLINIC_LANDMARK, CLINIC_PIN_CODE,
                    String.valueOf(CLINIC_LATITUDE), String.valueOf(CLINIC_LONGITUDE),
                    "91", PHONE_NUMBER_1, "91", PHONE_NUMBER_2);
            call.enqueue(new Callback<Clinic>() {
                @Override
                public void onResponse(Call<Clinic> call, Response<Clinic> response) {
                    if (response.isSuccessful())    {
                        /* DISMISS THE DIALOG AND FINISH THE ACTIVITY */
                        dialog.dismiss();
                        Intent success = new Intent();
                        setResult(RESULT_OK, success);
                        Toast.makeText(getApplicationContext(), "Updated successfully...", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        /* DISMISS THE DIALOG */
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Update failed...", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Clinic> call, Throwable t) {
//                    Log.e("UPDATE FAILURE", t.getMessage());
//                    Crashlytics.logException(t);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_LOCATION) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    CLINIC_LATITUDE = bundle.getDouble("LATITUDE");
                    CLINIC_LONGITUDE = bundle.getDouble("LONGITUDE");
                }

                /* GET THE APPROXIMATE ADDRESS FOR DISPLAY */
                try {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(this, Locale.getDefault());
                    addresses = geocoder.getFromLocation(CLINIC_LATITUDE, CLINIC_LONGITUDE, 1);
                    String address = addresses.get(0).getAddressLine(0);
                    if (!TextUtils.isEmpty(address)) {
                        txtLocation.setText(address);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}