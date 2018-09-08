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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.TypefaceSpan;
import biz.zenpets.users.utils.adapters.location.CitiesAdapter;
import biz.zenpets.users.utils.adapters.location.StatesAdapter;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.helpers.classes.location.LocationPickerActivity;
import biz.zenpets.users.utils.models.boarding.Boarding;
import biz.zenpets.users.utils.models.boarding.BoardingsAPI;
import biz.zenpets.users.utils.models.location.Cities;
import biz.zenpets.users.utils.models.location.City;
import biz.zenpets.users.utils.models.location.LocationsAPI;
import biz.zenpets.users.utils.models.location.State;
import biz.zenpets.users.utils.models.location.States;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.util.Calendar.MONTH;

public class BoardingEnrollment extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE USER ID **/
    private String USER_ID = null;

    /** THE LOCATION REQUEST CODE **/
    private final int REQUEST_LOCATION = 1;

    /** THE STATES ADAPTER AND ARRAY LIST **/
    private ArrayList<State> arrStates = new ArrayList<>();

    /** CITIES ADAPTER AND ARRAY LIST **/
    private ArrayList<City> arrCities = new ArrayList<>();

    /** THE HOME BOARDING ENROLLMENT DATA **/
    String BOARDING_ID = null;
    String BOARDING_ADDRESS = null;
    String BOARDING_PIN_CODE = null;
    String COUNTRY_ID = "51";
    String STATE_ID = null;
    String CITY_ID = null;
    Double BOARDING_LATITUDE = null;
    Double BOARDING_LONGITUDE = null;
    String BOARDING_EXPERIENCE = null;
    String BOARDING_SINCE = null;
    String BOARDING_PRICE = null;
    String BOARDING_DATE = null;
    String BOARDING_ACTIVE = "1";

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog dialog;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.inputAddress) TextInputLayout inputAddress;
    @BindView(R.id.edtAddress) TextInputEditText edtAddress;
    @BindView(R.id.inputPinCode) TextInputLayout inputPinCode;
    @BindView(R.id.edtPinCode) TextInputEditText edtPinCode;
    @BindView(R.id.spnState) Spinner spnState;
    @BindView(R.id.spnCity) Spinner spnCity;
    @BindView(R.id.txtLocation) TextView txtLocation;
    @BindView(R.id.inputExperience) TextInputLayout inputExperience;
    @BindView(R.id.edtExperience) TextInputEditText edtExperience;
    @BindView(R.id.txtSince) TextView txtSince;
    @BindView(R.id.inputBoardingPrice) TextInputLayout inputBoardingPrice;
    @BindView(R.id.edtBoardingPrice) TextInputEditText edtBoardingPrice;

    /** CHOOSE THE BOARDING'S LOCATION ON A MAP **/
    @OnClick(R.id.btnLocationPicker) void chooseLocation()  {
        Intent intent = new Intent(this, LocationPickerActivity.class);
        startActivityForResult(intent, REQUEST_LOCATION);
    }

    /** SELECT THE "SINCE" DATE **/
    @OnClick(R.id.btnSinceSelector) void selectSince()  {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = DatePickerDialog.newInstance(
                BoardingEnrollment.this,
                now.get(Calendar.YEAR),
                now.get(MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show(getFragmentManager(), "DatePickerDialog");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_boarding_enrollment);
        ButterKnife.bind(this);

        /* GET THE USER ID */
        USER_ID = getApp().getUserID();

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* CALCULATE THE BOARDING DATE */
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        BOARDING_DATE = format.format(date);

        /* SHOW THE PROGRESS AND FETCH THE LIST OF STATES */
        fetchStates();

        /* SELECT A STATE */
        spnState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                STATE_ID = arrStates.get(position).getStateID();

                /* CLEAR THE CITIES ARRAY LIST */
                arrCities.clear();

                /* FETCH THE LIST OF CITIES */
                if (STATE_ID != null)   {
                    fetchCities();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* SELECT A CITY */
        spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CITY_ID = arrCities.get(position).getCityID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
        BOARDING_EXPERIENCE = edtExperience.getText().toString().trim();
        BOARDING_PRICE = edtBoardingPrice.getText().toString().trim();

        /* VERIFY THE KENNEL DETAILS */
        if (TextUtils.isEmpty(BOARDING_ADDRESS))   {
            inputAddress.setError("Provide your address");
            inputAddress.setErrorEnabled(true);
            inputAddress.requestFocus();
            inputPinCode.setErrorEnabled(false);
            inputExperience.setErrorEnabled(false);
            inputBoardingPrice.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(BOARDING_PIN_CODE))  {
            inputPinCode.setError("Provide the Pin Code");
            inputPinCode.setErrorEnabled(true);
            inputPinCode.requestFocus();
            inputAddress.setErrorEnabled(false);
        } else if (BOARDING_LATITUDE == null || BOARDING_LONGITUDE == null) {
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            Toast.makeText(getApplicationContext(), "Please mark your location on the Map", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(BOARDING_EXPERIENCE)) {
            inputExperience.setError("Enter your experience boarding pets");
            inputExperience.setErrorEnabled(true);
            inputExperience.requestFocus();
        } else if (TextUtils.isEmpty(BOARDING_SINCE)) {
            Toast.makeText(getApplicationContext(), "Select since when you are boarding Pets", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(BOARDING_PRICE))   {
            inputBoardingPrice.setError("Provide the Per Day price of Boarding");
            inputBoardingPrice.setErrorEnabled(true);
            inputBoardingPrice.requestFocus();
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
        Call<Boarding> call = api.registerHomeBoarding(
                USER_ID, BOARDING_ADDRESS, BOARDING_PIN_CODE,
                CITY_ID, STATE_ID, COUNTRY_ID,
                String.valueOf(BOARDING_LATITUDE), String.valueOf(BOARDING_LONGITUDE),
                BOARDING_EXPERIENCE, BOARDING_SINCE, BOARDING_PRICE,
                BOARDING_DATE, BOARDING_ACTIVE);
        call.enqueue(new Callback<Boarding>() {
            @Override
            public void onResponse(Call<Boarding> call, Response<Boarding> response) {
                if (response.isSuccessful())    {
                    /* GET THE NEW BOARDING ID */
                    BOARDING_ID = response.body().getBoardingID();
//                    Log.e("BOARDING ID", BOARDING_ID);

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
                }
            } else {
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int month, int date) {
        /* GET THE SELECTED DATE */
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        /* FOR THE DATABASE ONLY !!!! */
        BOARDING_SINCE = sdf.format(cal.getTime());
//        Log.e("BOARDING SINCE", BOARDING_SINCE);

        /* FOR DISPLAY ONLY !!!! */
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String selectedDate = dateFormat.format(cal.getTime());
        txtSince.setText(selectedDate);
    }

    /** FETCH THE LIST OF STATES **/
    private void fetchStates() {
        LocationsAPI api = ZenApiClient.getClient().create(LocationsAPI.class);
        Call<States> call = api.allStates(COUNTRY_ID);
        call.enqueue(new Callback<States>() {
            @Override
            public void onResponse(Call<States> call, Response<States> response) {
                arrStates = response.body().getStates();

                /* SET THE ADAPTER TO THE STATES SPINNER */
                spnState.setAdapter(new StatesAdapter(BoardingEnrollment.this, arrStates));
            }

            @Override
            public void onFailure(Call<States> call, Throwable t) {
//                Log.e("STATES FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** FETCH THE LIST OF CITIES **/
    private void fetchCities() {
        LocationsAPI api = ZenApiClient.getClient().create(LocationsAPI.class);
        Call<Cities> call = api.allCities(STATE_ID);
        call.enqueue(new Callback<Cities>() {
            @Override
            public void onResponse(Call<Cities> call, Response<Cities> response) {
                arrCities = response.body().getCities();

                /* SET THE ADAPTER TO THE CITIES SPINNER */
                spnCity.setAdapter(new CitiesAdapter(BoardingEnrollment.this, arrCities));
            }

            @Override
            public void onFailure(Call<Cities> call, Throwable t) {
//                Log.e("CITIES FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }
}