package co.zenpets.kennels.credentials;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.zenpets.kennels.R;
import co.zenpets.kennels.utils.TypefaceSpan;
import co.zenpets.kennels.utils.adapters.location.CitiesAdapter;
import co.zenpets.kennels.utils.adapters.location.StatesAdapter;
import co.zenpets.kennels.utils.helpers.LocationPickerActivity;
import co.zenpets.kennels.utils.legal.PrivacyPolicyActivity;
import co.zenpets.kennels.utils.legal.SellerAgreementActivity;
import co.zenpets.kennels.utils.models.helpers.ZenApiClient;
import co.zenpets.kennels.utils.models.kennels.Kennel;
import co.zenpets.kennels.utils.models.kennels.KennelsAPI;
import co.zenpets.kennels.utils.models.location.CitiesData;
import co.zenpets.kennels.utils.models.location.CityData;
import co.zenpets.kennels.utils.models.location.LocationAPI;
import co.zenpets.kennels.utils.models.location.StateData;
import co.zenpets.kennels.utils.models.location.StatesData;
import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity /*implements PaymentResultListener*/ {

//    /** THE INCOMING SELECTION **/
//    Boolean blnSelection = false;

    /** A FIREBASE AUTH INSTANCE **/
    private FirebaseAuth auth;

    /** A FIREBASE USER INSTANCE **/
    private FirebaseUser user;

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int ACCESS_STORAGE_CONSTANT = 201;

    /** THE LOCATION REQUEST CODE **/
    private final int REQUEST_LOCATION = 1;

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog progressDialog;

    /** STRING TO HOLD THE ERROR MESSAGE, IF ANY **/
    private String strErrorMessage = null;

    /** THE COUNTRY, STATES AND CITIES ARRAY LISTS **/
    private ArrayList<StateData> arrStates = new ArrayList<>();
    private ArrayList<CityData> arrCities = new ArrayList<>();

    /** STRINGS TO HOLD THE KENNEL OWNER'S SELECTIONS **/
    String KENNEL_NAME = null;
    String KENNEL_EMAIL = null;
    String KENNEL_CONTACT_NAME = null;
    String KENNEL_CONTACT_PHONE_PREFIX = "91";
    String KENNEL_CONTACT_PHONE_NUMBER = null;
    String KENNEL_PASSWORD = null;
    String KENNEL_CONFIRM_PASSWORD = null;
    String KENNEL_POSTAL_ADDRESS = null;
    String KENNEL_PIN_CODE = null;
    private final String COUNTRY_ID = "51";
    private String STATE_ID = null;
    private String CITY_ID = null;
    Double KENNEL_LATITUDE = null;
    Double KENNEL_LONGITUDE = null;
    String KENNEL_PHONE_PREFIX_1 = "91";
    String KENNEL_PHONE_NUMBER_1 = null;
    String KENNEL_PHONE_PREFIX_2 = "91";
    String KENNEL_PHONE_NUMBER_2 = null;
    String KENNEL_PET_CAPACITY = null;
    private Uri KENNEL_COVER_PHOTO_URI = null;
    String KENNEL_COVER_PHOTO_FILE_NAME = null;
    private String KENNEL_COVER_PHOTO = null;
    private String VALID_FROM = null;
    private String VALID_TO = null;
//    private int KENNEL_CHARGES = 1500;
//    private int KENNEL_FINAL_CHARGES = 1500;
    private String PAYMENT_ID = null;
    String KENNEL_VERIFIED = "Unverified";

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.inputKennelName) TextInputLayout inputKennelName;
    @BindView(R.id.edtKennelName) TextInputEditText edtKennelName;
    @BindView(R.id.inputEmailAddress) TextInputLayout inputEmailAddress;
    @BindView(R.id.edtEmailAddress) TextInputEditText edtEmailAddress;
    @BindView(R.id.inputContactName) TextInputLayout inputContactName;
    @BindView(R.id.edtContactName) TextInputEditText edtContactName;
    @BindView(R.id.inputContactPhone) TextInputLayout inputContactPhone;
    @BindView(R.id.edtContactPhone) TextInputEditText edtContactPhone;
    @BindView(R.id.inputPassword) TextInputLayout inputPassword;
    @BindView(R.id.edtPassword) TextInputEditText edtPassword;
    @BindView(R.id.inputConfirmPassword) TextInputLayout inputConfirmPassword;
    @BindView(R.id.edtConfirmPassword) TextInputEditText edtConfirmPassword;
    @BindView(R.id.inputAddress) TextInputLayout inputAddress;
    @BindView(R.id.edtAddress) TextInputEditText edtAddress;
    @BindView(R.id.inputPinCode) TextInputLayout inputPinCode;
    @BindView(R.id.edtPinCode) TextInputEditText edtPinCode;
    @BindView(R.id.spnState) Spinner spnState;
    @BindView(R.id.spnCity) Spinner spnCity;
    @BindView(R.id.txtLocation) TextView txtLocation;
    @BindView(R.id.inputPhoneNumber1) TextInputLayout inputPhoneNumber1;
    @BindView(R.id.edtPhoneNumber1) TextInputEditText edtPhoneNumber1;
    @BindView(R.id.inputPhoneNumber2) TextInputLayout inputPhoneNumber2;
    @BindView(R.id.edtPhoneNumber2) TextInputEditText edtPhoneNumber2;
    @BindView(R.id.inputPetCapacity) TextInputLayout inputPetCapacity;
    @BindView(R.id.edtPetCapacity) TextInputEditText edtPetCapacity;
    @BindView(R.id.imgvwKennelCoverPhoto) ImageView imgvwKennelCoverPhoto;
    @BindView(R.id.txtTermsOfService) TextView txtTermsOfService;

    /** PICK THE KENNEL'S LOCATION ON THE MAP **/
    @OnClick(R.id.btnLocationPicker) void pickLocation()    {
        Intent intent = new Intent(this, LocationPickerActivity.class);
        startActivityForResult(intent, REQUEST_LOCATION);
    }

    /** SELECT THE KENNEL'S COVER PHOTO / LOGO **/
    @OnClick(R.id.imgvwKennelCoverPhoto) void selectProfilePicture() {
        /* CHECK STORAGE PERMISSION */
        checkStoragePermission();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        ButterKnife.bind(this);

//        /* PRELOAD THE CHECK OUT INSTANCE */
//        Checkout.preload(SignUpActivity.this);

//        /* GET THE INCOMING SELECTION */
//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null && bundle.containsKey("OPTION_CHOICE"))   {
//            blnSelection = bundle.getBoolean("OPTION_CHOICE");
////            Log.e("SELECTION", String.valueOf(blnSelection));
//            if (blnSelection)   {
//                /* CALCULATE THE FINAL CHARGES AFTER DISCOUNT */
//                KENNEL_FINAL_CHARGES = (int) (KENNEL_CHARGES - (KENNEL_CHARGES * .25));
////                Log.e("KENNEL CHARGES", String.valueOf(KENNEL_CHARGES));
////                Log.e("KENNEL FINAL CHARGES", String.valueOf(KENNEL_FINAL_CHARGES));
//            } else {
//                KENNEL_CHARGES = 1500;
//                KENNEL_FINAL_CHARGES = 1500;
////                Log.e("KENNEL CHARGES", String.valueOf(KENNEL_CHARGES));
////                Log.e("KENNEL FINAL CHARGES", String.valueOf(KENNEL_FINAL_CHARGES));
//            }
//        }

        /* THE EASY IMAGE CONFIGURATION */
        EasyImage.configuration(this)
                .setImagesFolderName("Zen Pets")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(true)
                .setAllowMultiplePickInGallery(false);

        /* INSTANTIATE THE FIREBASE AUTH INSTANCE **/
        auth = FirebaseAuth.getInstance();

        /* SET THE TERMS OF SERVICE TEXT **/
        setTermsAndConditions(txtTermsOfService);

        /* GENERATE THE VALID FROM AND VALID TO DATES */
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = new Date();
            VALID_FROM = format.format(date);
//            Log.e("VALID FROM", VALID_FROM);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(format.parse(VALID_FROM));

            /* CALCULATE THE END DATE */
            calendar.add(Calendar.YEAR, 1);
            Date dateEnd = new Date(calendar.getTimeInMillis());
            VALID_TO = format.format(dateEnd);
//            Log.e("VALID TO", VALID_TO);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* SHOW THE PROGRESS WHILE FETCHING THE LOCATION DATA */
        linlaProgress.setVisibility(View.VISIBLE);

        /* FETCH THE LIST OF STATES IN THE SELECTED COUNTRY */
        getAllStates();

        /* SELECT A STATE */
        spnState.setOnItemSelectedListener(selectState);

        /* SELECT A CITY */
        spnCity.setOnItemSelectedListener(selectCity);
    }

    /***** FETCH THE LIST OF STATES IN THE SELECTED COUNTRY *****/
    private void getAllStates() {
        LocationAPI api = ZenApiClient.getClient().create(LocationAPI.class);
        Call<StatesData> call = api.allStates(COUNTRY_ID);
        call.enqueue(new Callback<StatesData>() {
            @Override
            public void onResponse(Call<StatesData> call, Response<StatesData> response) {
                arrStates = response.body().getStates();
                if (arrStates != null && arrStates.size() > 0)  {
                    /* INSTANTIATE THE STATES ADAPTER */
                    StatesAdapter statesAdapter = new StatesAdapter(SignUpActivity.this, arrStates);

                    /* SET THE STATES SPINNER */
                    spnState.setAdapter(statesAdapter);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<StatesData> call, Throwable t) {
//                Log.e("FAILURE", t.getMessage());
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
                    CitiesAdapter citiesAdapter = new CitiesAdapter(SignUpActivity.this, arrCities);

                    /* SET THE CITIES SPINNER */
                    spnCity.setAdapter(citiesAdapter);
                }
            }

            @Override
            public void onFailure(Call<CitiesData> call, Throwable t) {
//                Log.e("FAILURE", t.getMessage());

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

    /***** SELECT THE CITY *****/
    private final AdapterView.OnItemSelectedListener selectCity = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            CITY_ID = arrCities.get(position).getCityID();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Sign Up";
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
        MenuInflater inflater = new MenuInflater(SignUpActivity.this);
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
                /* CHECK FOR ALL ACCOUNT DETAILS */
                checkAccountDetails();
                break;
            case R.id.menuCancel:
                finish();
                break;
            default:
                break;
        }
        return false;
    }

    /***** CHECK FOR ALL ACCOUNT DETAILS *****/
    private void checkAccountDetails() {
        /* HIDE THE KEYBOARD **/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtKennelName.getWindowToken(), 0);
        }

        /* COLLECT THE NECESSARY DATA **/
        KENNEL_NAME = edtKennelName.getText().toString();
        KENNEL_EMAIL = edtEmailAddress.getText().toString().trim();
        KENNEL_CONTACT_NAME = edtContactName.getText().toString();
        KENNEL_CONTACT_PHONE_NUMBER = edtContactPhone.getText().toString();
        KENNEL_PASSWORD = edtPassword.getText().toString().trim();
        KENNEL_CONFIRM_PASSWORD = edtConfirmPassword.getText().toString().trim();
        KENNEL_POSTAL_ADDRESS = edtAddress.getText().toString().trim();
        KENNEL_PIN_CODE = edtPinCode.getText().toString();
        KENNEL_PHONE_NUMBER_1 = edtPhoneNumber1.getText().toString().trim();
        KENNEL_PHONE_NUMBER_2 = edtPhoneNumber2.getText().toString().trim();
        KENNEL_PET_CAPACITY = edtPetCapacity.getText().toString();

        if (!TextUtils.isEmpty(KENNEL_NAME) && !TextUtils.isEmpty(KENNEL_EMAIL))  {
            KENNEL_COVER_PHOTO_FILE_NAME = KENNEL_NAME.replaceAll(" ", "_").toLowerCase().trim() + "_" + KENNEL_EMAIL;
        } else {
            KENNEL_COVER_PHOTO_FILE_NAME = null;
        }
        boolean blnValidEmail = isValidEmail(KENNEL_EMAIL);

        /* VALIDATE THE DATA **/
        if (TextUtils.isEmpty(KENNEL_NAME)) {
            inputKennelName.setError(getString(R.string.sign_up_kennel_name_error));
            inputKennelName.requestFocus();
            inputKennelName.setErrorEnabled(true);
            inputEmailAddress.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputContactPhone.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputPetCapacity.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_EMAIL)) {
            inputEmailAddress.setError(getString(R.string.sign_up_email_address_error));
            inputEmailAddress.requestFocus();
            inputKennelName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(true);
            inputContactName.setErrorEnabled(false);
            inputContactPhone.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputPetCapacity.setErrorEnabled(false);
        } else if (!blnValidEmail) {
            inputEmailAddress.setError(getString(R.string.sign_up_email_address_incorrect));
            inputEmailAddress.requestFocus();
            inputKennelName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(true);
            inputContactName.setErrorEnabled(false);
            inputContactPhone.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputPetCapacity.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_CONTACT_NAME)) {
            inputContactName.setError(getString(R.string.sign_up_contact_name_error));
            inputContactName.requestFocus();
            inputKennelName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputContactName.setErrorEnabled(true);
            inputContactPhone.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputPetCapacity.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_CONTACT_PHONE_NUMBER))  {
            inputContactPhone.setError(getString(R.string.sign_up_contact_phone_error));
            inputContactPhone.requestFocus();
            inputKennelName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputContactPhone.setErrorEnabled(true);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputPetCapacity.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_PASSWORD)) {
            inputPassword.setError(getString(R.string.sign_up_password_error));
            inputPassword.requestFocus();
            inputKennelName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputContactPhone.setErrorEnabled(false);
            inputPassword.setErrorEnabled(true);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputPetCapacity.setErrorEnabled(false);
        } else if (KENNEL_PASSWORD.length() < 8) {
            inputPassword.setError(getString(R.string.sign_up_length_error));
            inputPassword.requestFocus();
            inputKennelName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputContactPhone.setErrorEnabled(false);
            inputPassword.setErrorEnabled(true);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputPetCapacity.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_CONFIRM_PASSWORD)) {
            inputConfirmPassword.setError(getString(R.string.sign_up_confirm_password_error));
            inputConfirmPassword.requestFocus();
            inputKennelName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputContactPhone.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(true);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputPetCapacity.setErrorEnabled(false);
        } else if (KENNEL_CONFIRM_PASSWORD.length() < 8) {
            inputConfirmPassword.setError(getString(R.string.sign_up_length_error));
            inputConfirmPassword.requestFocus();
            inputKennelName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputContactPhone.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(true);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputPetCapacity.setErrorEnabled(false);
        } else if (!KENNEL_CONFIRM_PASSWORD.equals(KENNEL_PASSWORD))    {
            inputConfirmPassword.setError(getString(R.string.sign_up_mismatch_error));
            inputConfirmPassword.requestFocus();
            inputKennelName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputContactPhone.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(true);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputPetCapacity.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_POSTAL_ADDRESS))  {
            inputAddress.setError(getString(R.string.sign_up_mailing_address_error));
            inputAddress.requestFocus();
            inputKennelName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputContactPhone.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(true);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputPetCapacity.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_PIN_CODE)) {
            inputPinCode.setError(getString(R.string.sign_up_pin_code_error));
            inputPinCode.requestFocus();
            inputKennelName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputContactPhone.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(true);
            inputPhoneNumber1.setErrorEnabled(false);
            inputPetCapacity.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_PHONE_NUMBER_1)) {
            inputPhoneNumber1.setError(getString(R.string.sign_up_phone_number_1_error));
            inputPhoneNumber1.requestFocus();
            inputKennelName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputContactPhone.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(true);
            inputPetCapacity.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_PET_CAPACITY))  {
            inputPetCapacity.setError(getString(R.string.sign_up_pet_capacity_error));
            inputPetCapacity.requestFocus();
            inputKennelName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputContactPhone.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputPetCapacity.setErrorEnabled(true);
        } else if (KENNEL_LATITUDE == null || KENNEL_LONGITUDE == null) {
            Toast.makeText(
                    getApplicationContext(),
                    getString(R.string.sign_up_location_error),
                    Toast.LENGTH_LONG).show();
            imgvwKennelCoverPhoto.requestFocus();
            inputKennelName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputContactPhone.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputPetCapacity.setErrorEnabled(false);
        } else if (KENNEL_COVER_PHOTO_URI == null)  {
            Toast.makeText(
                    getApplicationContext(),
                    getString(R.string.sign_up_cover_photo_error),
                    Toast.LENGTH_LONG).show();
            imgvwKennelCoverPhoto.requestFocus();
            inputKennelName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputContactPhone.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputPetCapacity.setErrorEnabled(false);
        } else {
            /* DISABLE ALL ERRORS */
            inputKennelName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputContactPhone.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputPetCapacity.setErrorEnabled(false);

            /* CREATE KENNEL ACCOUNT */
            createKennelAccount();

//            if (blnSelection)   {
//                /* VERIFY THAT THE USER IS REGISTERED AS A PET TRAINER */
//                verifyTrainerAccount();
//            } else {
//                /* START THE PAYMENT PROCESS */
//                startPaymentProcess();
//            }
        }
    }

    /** CREATE KENNEL ACCOUNT **/
    private void createKennelAccount() {
        /* SHOW THE PROGRESS DIALOG WHILE UPLOADING THE IMAGE **/
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait while we create your new account....");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        auth.createUserWithEmailAndPassword(KENNEL_EMAIL, KENNEL_PASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())   {
                    /* GET THE NEW USERS INSTANCE */
                    user = auth.getCurrentUser();

                    if (user != null)   {
                        KENNEL_EMAIL = user.getEmail();

                        /* CHECK THE KENNEL ACCOUNT STATUS */
                        checkKennelStatus();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(
                            getApplicationContext(),
                            "There was a problem creating your new account. Please try again by clicking the Save button.",
                            Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SignUpActivity.this, "EXCEPTION: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** CHECK THE KENNEL ACCOUNT STATUS **/
    private void checkKennelStatus() {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<Kennel> call = api.kennelAccountExists(KENNEL_EMAIL);
        call.enqueue(new Callback<Kennel>() {
            @Override
            public void onResponse(Call<Kennel> call, Response<Kennel> response) {
//                Log.e("STATUS RESPONSE", String.valueOf(response.raw()));
                Kennel kennel = response.body();
                if (!kennel.getError()) {
                    /* UPLOAD THE KENNEL COVER / LOGO */
                    uploadCoverPhoto();
                } else {
                    progressDialog.dismiss();
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
//                String message = response.body().getMessage();
//                if (message != null)    {
////                    Log.e("MESSAGE", message);
//                    if (message.equalsIgnoreCase("Kennel account exists..."))   {
//                        progressDialog.dismiss();
//                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                        finish();
//                    } else if (message.equalsIgnoreCase("\"Kennel account doesn't exist...\""))    {
//                        /* UPLOAD THE KENNEL COVER / LOGO */
//                        uploadCoverPhoto();
//                    }
//                }
            }

            @Override
            public void onFailure(Call<Kennel> call, Throwable t) {
//                Log.e("STATUS FAILURE", t.getMessage());
            }
        });
    }

    /** UPLOAD THE KENNEL COVER / LOGO **/
    private void uploadCoverPhoto() {
        KENNEL_COVER_PHOTO_FILE_NAME = KENNEL_NAME.replaceAll(" ", "_").toLowerCase().trim() + "_" + user.getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference refStorage = storageReference.child("Kennel Covers").child(KENNEL_COVER_PHOTO_FILE_NAME);
        UploadTask uploadTask = refStorage.putFile(KENNEL_COVER_PHOTO_URI);
        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                /* CONTINUE WITH THE TASK TO GET THE IMAGE URL */
                return refStorage.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    KENNEL_COVER_PHOTO = String.valueOf(task.getResult());
                    if (KENNEL_COVER_PHOTO != null)    {
                        /* CREATE THE NEW KENNEL OWNER'S ACCOUNT */
                        createKennelOwnersAccount();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(
                                getApplicationContext(),
                                "There was a problem creating your new account. Please try again by clicking the Save button.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    /** CREATE THE NEW KENNEL OWNER'S ACCOUNT **/
    private void createKennelOwnersAccount() {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<Kennel> call = api.registerNewKennel(user.getUid(), KENNEL_NAME, KENNEL_EMAIL,
                KENNEL_COVER_PHOTO, KENNEL_CONTACT_NAME,
                KENNEL_CONTACT_PHONE_PREFIX, KENNEL_CONTACT_PHONE_NUMBER,
                KENNEL_POSTAL_ADDRESS, KENNEL_PIN_CODE, COUNTRY_ID, STATE_ID, CITY_ID,
                String.valueOf(KENNEL_LATITUDE), String.valueOf(KENNEL_LONGITUDE),
                KENNEL_PHONE_PREFIX_1, KENNEL_PHONE_NUMBER_1, KENNEL_PHONE_PREFIX_2, KENNEL_PHONE_NUMBER_2,
                KENNEL_PET_CAPACITY, VALID_FROM, VALID_TO, KENNEL_VERIFIED);
        call.enqueue(new Callback<Kennel>() {
            @Override
            public void onResponse(Call<Kennel> call, Response<Kennel> response) {
                if (response.isSuccessful())    {
                    /* FINISH THE REGISTRATION AND MOVE TO THE LOGIN SCREEN */
                    progressDialog.dismiss();
                    auth.signOut();
                    Toast.makeText(getApplicationContext(), "Account created successfully...", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    /* SET THE ERROR MESSAGE **/
                    strErrorMessage = "There was en error signing you up. Please submit your details again.";
                    progressDialog.dismiss();

                    /* DELETE THE KENNEL OWNER'S **/
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())    {
                                /* SHOW THE ERROR MESSAGE **/
                                new MaterialDialog.Builder(SignUpActivity.this)
                                        .title("Registration Failed!")
                                        .content(strErrorMessage)
                                        .positiveText("OKAY")
                                        .theme(Theme.LIGHT)
                                        .icon(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.ic_info_black_24dp))
                                        .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Kennel> call, Throwable t) {
            }
        });
    }

    /** VERIFY THAT THE USER IS REGISTERED AS A PET TRAINER **/
//    private void verifyTrainerAccount() {
//        TrainersAPI api = ZenApiClient.getClient().create(TrainersAPI.class);
//        Call<Trainer> call = api.trainerExists(KENNEL_OWNER_EMAIL_ADDRESS);
//        call.enqueue(new Callback<Trainer>() {
//            @Override
//            public void onResponse(Call<Trainer> call, Response<Trainer> response) {
//                String message = response.body().getMessage();
//                if (message != null)    {
////                    Log.e("MESSAGE", message);
//                    if (message.equalsIgnoreCase("Trainer exists..."))   {
//                        /* START THE PAYMENT PROCESS */
//                        startPaymentProcess();
//                    } else if (message.equalsIgnoreCase("Trainer doesn't exist..."))    {
//                        /* DISPLAY THE FAILURE PROMPT */
//                        displayFailurePrompt();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Trainer> call, Throwable t) {
////                Log.e("EXISTS FAILURE", t.getMessage());
//            }
//        });
//    }

    /** DISPLAY THE FAILURE PROMPT **/
//    private void displayFailurePrompt() {
//        new MaterialDialog.Builder(SignUpActivity.this)
//                .icon(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.ic_info_black_24dp))
//                .title(getString(R.string.trainer_failed_title))
//                .cancelable(true)
//                .content(getString(R.string.trainer_failed_message))
//                .positiveText(getString(R.string.trainer_failed_try_again))
//                .negativeText(getString(R.string.trainer_failed_continue))
//                .theme(Theme.LIGHT)
//                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        /* DISMISS THE DIALOG */
//                        dialog.dismiss();
//                    }
//                })
//                .onNegative(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        /* DISMISS THE DIALOG */
//                        dialog.dismiss();
//
//                        /* SET THE DEFAULT CHARGES WITHOUT DISCOUNT */
//                        KENNEL_CHARGES = 1500;
//                        KENNEL_FINAL_CHARGES = 1500;
//
//                        /* START THE PAYMENT PROCESS */
//                        startPaymentProcess();
//                    }
//                }).show();
//    }

    /** START THE PAYMENT PROCESS **/
//    private void startPaymentProcess() {
//        final Activity activity = this;
//        final Checkout checkout = new Checkout();
//        int icon = R.mipmap.ic_launcher;
//        checkout.setImage(icon);
//        checkout.setFullScreenDisable(true);
//        try {
//            JSONObject options = new JSONObject();
//            options.put("name", "Zen Pets");
//            options.put("description", "Kennel Manager Annual Charges");
//            options.put("currency", "INR");
//            options.put("amount", KENNEL_FINAL_CHARGES * 100);
//            JSONObject preFill = new JSONObject();
//            preFill.put("email", KENNEL_OWNER_EMAIL_ADDRESS);
//            preFill.put("contact", KENNEL_OWNER_PHONE_NUMBER);
//            options.put("prefill", preFill);
//
//            checkout.open(activity, options);
//        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
//                    .show();
//            e.printStackTrace();
//        }
//    }

    /***** CREATE THE NEW ACCOUNT *****/
//    private void createAccount() {
//        /* SHOW THE PROGRESS DIALOG WHILE UPLOADING THE IMAGE **/
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Please wait while we create your new account....");
//        progressDialog.setIndeterminate(false);
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//        auth.createUserWithEmailAndPassword(KENNEL_OWNER_EMAIL_ADDRESS, KENNEL_OWNER_PASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful())   {
//                    /* GET THE NEW USERS INSTANCE */
//                    user = auth.getCurrentUser();
//
//                    if (user != null)   {
//                        KENNEL_OWNER_EMAIL_ADDRESS = user.getEmail();
//                        /* CHECK IF THE KENNEL OWNER EXISTS IN THE PRIMARY DATABASE */
//                        checkKennelOwnerExists();
//                    }
//                } else {
//                    progressDialog.dismiss();
//                    Toast.makeText(
//                            getApplicationContext(),
//                            "There was a problem creating your new account. Please try again by clicking the Save button.",
//                            Toast.LENGTH_LONG).show();
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                progressDialog.dismiss();
//                Toast.makeText(SignUpActivity.this, "EXCEPTION: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    /***** CHECK IF THE KENNEL OWNER EXISTS IN THE PRIMARY DATABASE *****/
//    private void checkKennelOwnerExists() {
//        AccountsAPI api = ZenApiClient.getClient().create(AccountsAPI.class);
//        Call<Account> call = api.kennelOwnerExists(KENNEL_OWNER_EMAIL_ADDRESS);
//        call.enqueue(new Callback<Account>() {
//            @Override
//            public void onResponse(Call<Account> call, Response<Account> response) {
//                String message = response.body().getMessage();
//                if (message != null)    {
////                    Log.e("MESSAGE", message);
//                    if (message.equalsIgnoreCase("Kennel Owner's account exists..."))   {
//                        progressDialog.dismiss();
//                        Intent intent = new Intent(SignUpActivity.this, NewLandingActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                        finish();
//                    } else if (message.equalsIgnoreCase("Kennel Owner's account doesn't exist..."))    {
//                        /* UPLOAD THE KENNEL OWNER'S DISPLAY PROFILE */
//                        uploadDisplayProfile();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Account> call, Throwable t) {
////                Log.e("EXISTS FAILURE", t.getMessage());
//            }
//        });
//    }

    /***** UPLOAD THE KENNEL OWNER'S DISPLAY PROFILE *****/
//    private void uploadDisplayProfile() {
//        KENNEL_OWNER_DISPLAY_PROFILE_FILE_NAME = KENNEL_OWNER_NAME.replaceAll(" ", "_").toLowerCase().trim() + "_" + user.getUid();
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//        final StorageReference refStorage = storageReference.child("Kennel Profiles").child(KENNEL_OWNER_DISPLAY_PROFILE_FILE_NAME);
//        UploadTask uploadTask = refStorage.putFile(KENNEL_OWNER_DISPLAY_PROFILE_URI);
//        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//            @Override
//            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                if (!task.isSuccessful()) {
//                    throw task.getException();
//                }
//
//                /* CONTINUE WITH THE TASK TO GET THE IMAGE URL */
//                return refStorage.getDownloadUrl();
//            }
//        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//            @Override
//            public void onComplete(@NonNull Task<Uri> task) {
//                if (task.isSuccessful()) {
//                    KENNEL_OWNER_DISPLAY_PROFILE = String.valueOf(task.getResult());
//                    if (KENNEL_OWNER_DISPLAY_PROFILE != null)    {
//                        /* CREATE THE NEW KENNEL OWNER'S ACCOUNT */
//                        createKennelOwnersAccount();
//                    } else {
//                        progressDialog.dismiss();
//                        Toast.makeText(
//                                getApplicationContext(),
//                                "There was a problem creating your new account. Please try again by clicking the Save button.",
//                                Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//        });
//    }

    /***** CREATE THE NEW KENNEL OWNER'S ACCOUNT *****/
//    private void createKennelOwnersAccount() {
//        AccountsAPI api = ZenApiClient.getClient().create(AccountsAPI.class);
//        Call<Account> call = api.registerKennelOwner(
//                user.getUid(), KENNEL_OWNER_NAME, KENNEL_OWNER_DISPLAY_PROFILE, KENNEL_OWNER_EMAIL_ADDRESS,
//                KENNEL_OWNER_PHONE_PREFIX, KENNEL_OWNER_PHONE_NUMBER, KENNEL_OWNER_MAILING_ADDRESS,
//                KENNEL_OWNER_PIN_CODE, COUNTRY_ID, STATE_ID, CITY_ID, PAYMENT_ID, VALID_FROM, VALID_TO);
//        call.enqueue(new Callback<Account>() {
//            @Override
//            public void onResponse(Call<Account> call, Response<Account> response) {
//                if (response.isSuccessful())    {
//                    /* FINISH THE REGISTRATION AND MOVE TO THE LOGIN SCREEN */
//                    progressDialog.dismiss();
//                    auth.signOut();
//                    Toast.makeText(getApplicationContext(), "Account created successfully...", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    /* SET THE ERROR MESSAGE **/
//                    strErrorMessage = "There was en error signing you up. Please submit your details again.";
//                    progressDialog.dismiss();
//
//                    /* DELETE THE KENNEL OWNER'S **/
//                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful())    {
//                                /* SHOW THE ERROR MESSAGE **/
//                                new MaterialDialog.Builder(SignUpActivity.this)
//                                        .title("Registration Failed!")
//                                        .content(strErrorMessage)
//                                        .positiveText("OKAY")
//                                        .theme(Theme.LIGHT)
//                                        .icon(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.ic_info_black_24dp))
//                                        .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
//                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
//                                            @Override
//                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                                dialog.dismiss();
//                                            }
//                                        }).show();
//                            }
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Account> call, Throwable t) {
////                Log.e("SIGN UP FAILURE", t.getMessage());
//            }
//        });
//    }

    /***** CHECK STORAGE PERMISSION *****/
    private void checkStoragePermission() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(SignUpActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)   {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))    {
                /* SHOW THE DIALOG */
                new MaterialDialog.Builder(this)
                        .icon(ContextCompat.getDrawable(this, R.drawable.ic_info_black_24dp))
                        .title(getString(R.string.storage_permission_title))
                        .cancelable(false)
                        .content(getString(R.string.storage_permission_message))
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
                                        SignUpActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE_CONSTANT);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ACCESS_STORAGE_CONSTANT);
            }
        } else {
            final BottomSheetDialog sheetDialog = new BottomSheetDialog(SignUpActivity.this);
            @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.image_picker_sheet, null);
            sheetDialog.setContentView(view);
            sheetDialog.show();

            /* CAST THE CHOOSER ELEMENTS */
            LinearLayout linlaGallery = view.findViewById(R.id.linlaGallery);
            LinearLayout linlaCamera = view.findViewById(R.id.linlaCamera);

            /* SELECT A GALLERY IMAGE */
            linlaGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sheetDialog.dismiss();
                    EasyImage.openGallery(SignUpActivity.this, 0);
                }
            });

            /* SELECT A CAMERA IMAGE */
            linlaCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sheetDialog.dismiss();
                    EasyImage.openCamera(SignUpActivity.this, 0);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_STORAGE_CONSTANT)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                final BottomSheetDialog sheetDialog = new BottomSheetDialog(SignUpActivity.this);
                @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.image_picker_sheet, null);
                sheetDialog.setContentView(view);
                sheetDialog.show();

                /* CAST THE CHOOSER ELEMENTS */
                LinearLayout linlaGallery = view.findViewById(R.id.linlaGallery);
                LinearLayout linlaCamera = view.findViewById(R.id.linlaCamera);

                /* SELECT A GALLERY IMAGE */
                linlaGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheetDialog.dismiss();
                        EasyImage.openGallery(SignUpActivity.this, 0);
                    }
                });

                /* SELECT A CAMERA IMAGE */
                linlaCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheetDialog.dismiss();
                        EasyImage.openCamera(SignUpActivity.this, 0);
                    }
                });
            }
        }
    }

    /***** SET THE TERMS AND CONDITIONS *****/
    private void setTermsAndConditions(TextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(getResources().getString(R.string.terms_part_1));
        spanTxt.append(getResources().getString(R.string.terms_part_2));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent showSellerAgreement = new Intent(getApplicationContext(), SellerAgreementActivity.class);
                startActivity(showSellerAgreement);
            }
        }, spanTxt.length() - getResources().getString(R.string.terms_part_2).length(), spanTxt.length(), 0);
        spanTxt.append(getResources().getString(R.string.terms_part_3));
        spanTxt.setSpan(new ForegroundColorSpan(Color.BLACK), 48, spanTxt.length(), 0);
        spanTxt.append(getResources().getString(R.string.terms_part_4));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent showPrivacyPolicy = new Intent(getApplicationContext(), PrivacyPolicyActivity.class);
                startActivity(showPrivacyPolicy);
            }
        }, spanTxt.length() - " Privacy Policy".length(), spanTxt.length(), 0);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, AppCompatTextView.BufferType.SPANNABLE);
    }

    /***** VALIDATE EMAIL SYNTAX / FORMAT *****/
    private static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_LOCATION) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    KENNEL_LATITUDE = bundle.getDouble("LATITUDE");
                    KENNEL_LONGITUDE = bundle.getDouble("LONGITUDE");
                }

                /* GET THE APPROXIMATE ADDRESS FOR DISPLAY */
                try {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(this, Locale.getDefault());
                    addresses = geocoder.getFromLocation(KENNEL_LATITUDE, KENNEL_LONGITUDE, 1);
                    String address = addresses.get(0).getAddressLine(0);
                    if (!TextUtils.isEmpty(address)) {
                        txtLocation.setText(address);
                    } else {
                        // TODO: DISPLAY THE COORDINATES INSTEAD
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                onPhotoReturned(imageFiles);
            }
        });
    }

    /***** PROCESS THE SELECTED IMAGE AND GRAB THE URI *****/
    private void onPhotoReturned(List<File> imageFiles) {
        try {
            File compressedFile = new Compressor(this)
                    .setMaxWidth(800)
                    .setMaxHeight(800)
                    .setQuality(80)
                    .setCompressFormat(Bitmap.CompressFormat.PNG)
                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath())
                    .compressToFile(imageFiles.get(0));
            Uri uri = Uri.fromFile(compressedFile);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            imgvwKennelCoverPhoto.setImageBitmap(bitmap);
            imgvwKennelCoverPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);

            /* STORE THE BITMAP AS A FILE AND USE THE FILE'S URI */
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/ZenPets");
            myDir.mkdirs();
            String fName = "photo.jpg";
            File file = new File(myDir, fName);
            if (file.exists()) file.delete();

            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                /* GET THE FINAL URI */
                KENNEL_COVER_PHOTO_URI = Uri.fromFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EasyImage.clearConfiguration(this);
    }

//    @Override
//    public void onPaymentSuccess(String razorPaymentID) {
//        try {
//            /* CAST THE PAYMENT ID IN THE GLOBAL INSTANCE */
//            PAYMENT_ID = razorPaymentID;
//
//            /* LOG THE RESULT FOR TESTING PURPOSES */
////            Log.e("PAYMENT SUCCESS", "Payment was made successfully with the ID: " + razorPaymentID);
//
//            /* CAPTURE THE KENNEL PAYMENT */
//            captureKennelPayment(razorPaymentID);
//        } catch (Exception e) {
//        }
//    }
//
//    @Override
//    public void onPaymentError(int code, String response) {
//        try {
//            /* SHOW THE DIALOG */
//            new MaterialDialog.Builder(this)
//                    .icon(ContextCompat.getDrawable(this, R.drawable.ic_info_black_24dp))
//                    .title(getString(R.string.payment_failed_title))
//                    .cancelable(false)
//                    .content(getString(R.string.payment_failed_message))
//                    .positiveText(getString(R.string.payment_failed_try_again))
//                    .negativeText(getString(R.string.payment_failed_cancel))
//                    .theme(Theme.LIGHT)
//                    .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
//                    .onNegative(new MaterialDialog.SingleButtonCallback() {
//                        @Override
//                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                            Intent intent = new Intent();
//                            setResult(RESULT_CANCELED, intent);
//                            finish();
//                        }
//                    })
//                    .onPositive(new MaterialDialog.SingleButtonCallback() {
//                        @Override
//                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                            /* TRY THE PAYMENT PROCESS AGAIN */
//                            startPaymentProcess();
//                        }
//                    }).show();
//            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
////            Log.e("PAYMENT ERROR EXCEPTION", e.getMessage());
//        }
//    }
//
//    /** CAPTURE THE KENNEL PAYMENT **/
//    private void captureKennelPayment(final String razorPaymentID) {
//        String apiKey = getString(R.string.razor_pay_api_key_id);
//        String apiSecret = getString(R.string.razor_pay_api_key_secret);
//        String strCredentials = Credentials.basic(apiKey, apiSecret);
//        String strUrl = "https://" + apiKey + ":" + apiSecret + "@api.razorpay.com/v1/payments/" + razorPaymentID + "/capture";
////        Log.e("CAPTURE URL", strUrl);
////        Log.e("AMOUNT", String.valueOf(KENNEL_FINAL_CHARGES * 100));
//        OkHttpClient client = new OkHttpClient();
//        String strAmount = String.valueOf(KENNEL_FINAL_CHARGES * 100);
////        Log.e("STR AMOUNT", strAmount);
//        RequestBody body = new FormBody.Builder()
//                .add("amount", String.valueOf(KENNEL_FINAL_CHARGES * 100))
//                .build();
//        Request request = new Request.Builder()
//                .header("Authorization", strCredentials)
//                .url(strUrl)
//                .post(body)
//                .build();
////        Log.e("REQUEST", String.valueOf(request));
//        client.newCall(request).enqueue(new okhttp3.Callback() {
//            @Override
//            public void onFailure(okhttp3.Call call, IOException e) {
////                Log.e("FAILURE", e.getMessage());
//            }
//
//            @Override
//            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
//                /* CHECK IF THE PAYMENT WAS CAPTURED SUCCESSFULLY */
//                checkCaptureStatus(razorPaymentID);
////                Log.e("RESPONSE", String.valueOf(response));
//            }
//        });
//    }
//
//    /** CHECK IF THE PAYMENT WAS CAPTURED SUCCESSFULLY **/
//    private void checkCaptureStatus(final String razorPaymentID) {
//        String apiKey = getString(R.string.razor_pay_api_key_id);
//        String apiSecret = getString(R.string.razor_pay_api_key_secret);
//        String strCredentials = Credentials.basic(apiKey, apiSecret);
//        String URL_CHECK_CAPTURE = "https://" + apiKey + ":" + apiSecret + "@api.razorpay.com/v1/payments/" + razorPaymentID;
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .header("Authorization", strCredentials)
//                .url(URL_CHECK_CAPTURE)
//                .build();
////        Log.e("CAPTURE URL", URL_CHECK_CAPTURE);
//        client.newCall(request).enqueue(new okhttp3.Callback() {
//            @Override
//            public void onFailure(okhttp3.Call call, IOException e) {
//            }
//
//            @Override
//            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
//                try {
//                    String strResult = response.body().string();
//                    JSONObject JORoot = new JSONObject(strResult);
////                    Log.e("ROOT", String.valueOf(JORoot));
//                    if (JORoot.has("error_code") && JORoot.getString("error_code").equalsIgnoreCase("null")) {
//                        /* CHECK THE CAPTURED STATUS */
//                        if (JORoot.has("captured")) {
//                            String CAPTURED_STATUS = JORoot.getString("captured");
//                            if (CAPTURED_STATUS.equalsIgnoreCase("true"))   {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        /* CREATE THE KENNEL OWNER'S ACCOUNT */
//                                        createAccount();
////                                        Toast.makeText(getApplicationContext(), "Captured successfully...", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            } else {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
////                                        Toast.makeText(getApplicationContext(), "An error occurred...", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//
//                            }
//                        } else {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
////                                    Toast.makeText(getApplicationContext(), "An error occurred...", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
}