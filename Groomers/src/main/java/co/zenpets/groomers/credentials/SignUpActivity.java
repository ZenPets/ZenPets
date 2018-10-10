package co.zenpets.groomers.credentials;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
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

import co.zenpets.groomers.R;
import co.zenpets.groomers.landing.LandingActivity;
import co.zenpets.groomers.utils.TypefaceSpan;
import co.zenpets.groomers.utils.adapters.location.CitiesAdapter;
import co.zenpets.groomers.utils.adapters.location.StatesAdapter;
import co.zenpets.groomers.utils.helpers.ZenApiClient;
import co.zenpets.groomers.utils.helpers.classes.LocationPickerActivity;
import co.zenpets.groomers.utils.models.groomers.Groomer;
import co.zenpets.groomers.utils.models.groomers.GroomersAPI;
import co.zenpets.groomers.utils.models.location.Cities;
import co.zenpets.groomers.utils.models.location.City;
import co.zenpets.groomers.utils.models.location.LocationAPI;
import co.zenpets.groomers.utils.models.location.State;
import co.zenpets.groomers.utils.models.location.States;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    /** A FIREBASE AUTH INSTANCE **/
    private FirebaseAuth auth;

    /** A FIREBASE USER INSTANCE **/
    private FirebaseUser user;

    /** THE COUNTRY, STATES AND CITIES ARRAY LISTS **/
    private ArrayList<State> arrStates = new ArrayList<>();
    private ArrayList<City> arrCities = new ArrayList<>();

    /** THE LOCATION REQUEST CODE **/
    private final int REQUEST_LOCATION = 101;

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int ACCESS_STORAGE_CONSTANT = 201;

    /** STRINGS TO HOLD THE COLLECTED GROOMER ACCOUNT INFORMATION **/
    String GROOMER_NAME = null;
    Uri GROOMER_LOGO_URI = null;
    String GROOMER_LOGO = null;
    String GROOMER_LOGO_FILE_NAME = null;
    String CONTACT_NAME = null;
    String CONTACT_EMAIL = null;
    String CONTACT_PASSWORD = null;
    String CONTACT_CONFIRM_PASSWORD = null;
    String GROOMER_PHONE_PREFIX_1 = "91";
    String GROOMER_PHONE_NUMBER_1 = null;
    String GROOMER_PHONE_PREFIX_2 = "91";
    String GROOMER_PHONE_NUMBER_2 = null;
    String GROOMER_ADDRESS = null;
    String GROOMER_PINCODE = null;
    String COUNTRY_ID = "51";
    String STATE_ID = null;
    String CITY_ID = null;
    String GROOMER_LATITUDE = null;
    String GROOMER_LONGITUDE = null;
    private String VALID_FROM = null;
    private String VALID_TO = null;

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog progressDialog;

    /** STRING TO HOLD THE ERROR MESSAGE, IF ANY **/
    private String strErrorMessage = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.inputGroomerName) TextInputLayout inputGroomerName;
    @BindView(R.id.edtGroomerName) TextInputEditText edtGroomerName;
    @BindView(R.id.inputContactName) TextInputLayout inputContactName;
    @BindView(R.id.edtContactName) TextInputEditText edtContactName;
    @BindView(R.id.inputEmailAddress) TextInputLayout inputEmailAddress;
    @BindView(R.id.edtEmailAddress) TextInputEditText edtEmailAddress;
    @BindView(R.id.inputPassword) TextInputLayout inputPassword;
    @BindView(R.id.edtPassword) TextInputEditText edtPassword;
    @BindView(R.id.inputConfirm) TextInputLayout inputConfirm;
    @BindView(R.id.edtConfirm) TextInputEditText edtConfirm;
    @BindView(R.id.inputPhoneNumber1) TextInputLayout inputPhoneNumber1;
    @BindView(R.id.edtPhoneNumber1) TextInputEditText edtPhoneNumber1;
    @BindView(R.id.inputPhoneNumber2) TextInputLayout inputPhoneNumber2;
    @BindView(R.id.edtPhoneNumber2) TextInputEditText edtPhoneNumber2;
    @BindView(R.id.inputAddress) TextInputLayout inputAddress;
    @BindView(R.id.edtAddress) TextInputEditText edtAddress;
    @BindView(R.id.inputPinCode) TextInputLayout inputPinCode;
    @BindView(R.id.edtPinCode) TextInputEditText edtPinCode;
    @BindView(R.id.txtLocation) TextView txtLocation;
    @BindView(R.id.spnState) Spinner spnState;
    @BindView(R.id.spnCity) Spinner spnCity;
    @BindView(R.id.imgvwLogo) ImageView imgvwLogo;
    @BindView(R.id.txtTermsOfService) TextView txtTermsOfService;

    /** MARK THE LOCATION ON THE MAP **/
    @OnClick(R.id.btnLocationPicker) void markLocation()    {
        Intent intent = new Intent(this, LocationPickerActivity.class);
        startActivityForResult(intent, REQUEST_LOCATION);
    }

    /** SELECT THE GROOMER LOGO **/
    @OnClick(R.id.imgvwLogo) void selectLogo() {
        /* CHECK STORAGE PERMISSION */
        checkStoragePermission();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);
        ButterKnife.bind(this);

        /* THE EASY IMAGE CONFIGURATION */
        EasyImage.configuration(this)
                .setImagesFolderName("Zen Pets")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(true)
                .setAllowMultiplePickInGallery(false);

        /* INSTANTIATE THE FIREBASE AUTH INSTANCE **/
        auth = FirebaseAuth.getInstance();

        /* FETCH THE LIST OF STATES IN THE SELECTED COUNTRY */
        getAllStates();

        /* SELECT A STATE */
        spnState.setOnItemSelectedListener(selectState);

        /* SELECT A CITY */
        spnCity.setOnItemSelectedListener(selectCity);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* GENERATE THE VALID FROM AND VALID TO DATES */
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = new Date();
            VALID_FROM = format.format(date);
//            Log.e("VALID FROM", VALID_FROM);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(format.parse(VALID_FROM));

            /* CALCULATE THE END DATE */
            calendar.add(Calendar.MONTH, 3);
            Date dateEnd = new Date(calendar.getTimeInMillis());
            VALID_TO = format.format(dateEnd);
//            Log.e("VALID TO", VALID_TO);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /** CHECK FOR ALL ACCOUNT DETAILS **/
    private void checkAccountDetails() {
        /* HIDE THE KEYBOARD **/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtGroomerName.getWindowToken(), 0);
        }

        /* COLLECT THE NECESSARY DATA **/
        GROOMER_NAME = edtGroomerName.getText().toString().trim();
        CONTACT_NAME = edtContactName.getText().toString().trim();
        CONTACT_EMAIL = edtEmailAddress.getText().toString().trim();
        CONTACT_PASSWORD = edtPassword.getText().toString().trim();
        CONTACT_CONFIRM_PASSWORD = edtConfirm.getText().toString().trim();
        GROOMER_PHONE_NUMBER_1 = edtPhoneNumber1.getText().toString().trim();
        GROOMER_PHONE_NUMBER_2 = edtPhoneNumber2.getText().toString().trim();
        GROOMER_ADDRESS = edtAddress.getText().toString().trim();
        GROOMER_PINCODE = edtPinCode.getText().toString().trim();

        /* VERIFY A VALID EMAIL ADDRESS */
        boolean blnValidEmail = isValidEmail(CONTACT_EMAIL);

        /* VALIDATE THE DATA **/
        if (TextUtils.isEmpty(GROOMER_NAME)) {
            inputGroomerName.setErrorEnabled(true);
            inputGroomerName.setError(getString(R.string.sign_up_groomer_name_error));
            inputGroomerName.requestFocus();
            inputContactName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirm.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(CONTACT_NAME)) {
            inputContactName.setErrorEnabled(true);
            inputContactName.setError(getString(R.string.sign_up_contact_name_error));
            inputContactName.requestFocus();
            inputGroomerName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirm.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(CONTACT_EMAIL)) {
            inputEmailAddress.setErrorEnabled(true);
            inputEmailAddress.setError(getString(R.string.sign_up_no_email_error));
            inputEmailAddress.requestFocus();
            inputGroomerName.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirm.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (!blnValidEmail)  {
            inputEmailAddress.setErrorEnabled(true);
            inputEmailAddress.setError(getString(R.string.sign_up_invalid_email_error));
            inputEmailAddress.requestFocus();
            inputGroomerName.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirm.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(CONTACT_PASSWORD)) {
            inputPassword.setErrorEnabled(true);
            inputPassword.setError(getString(R.string.sign_up_password_error));
            inputPassword.requestFocus();
            inputGroomerName.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputConfirm.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (CONTACT_PASSWORD.length() < 8) {
            inputPassword.setErrorEnabled(true);
            inputPassword.setError(getString(R.string.sign_up_password_length_error));
            inputPassword.requestFocus();
            inputGroomerName.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputConfirm.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(CONTACT_CONFIRM_PASSWORD)) {
            inputConfirm.setErrorEnabled(true);
            inputConfirm.setError(getString(R.string.sign_up_confirm_password_error));
            inputConfirm.requestFocus();
            inputContactName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (CONTACT_CONFIRM_PASSWORD.length() < 8) {
            inputConfirm.setErrorEnabled(true);
            inputConfirm.setError(getString(R.string.sign_up_confirm_password_length_error));
            inputConfirm.requestFocus();
            inputGroomerName.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (!CONTACT_CONFIRM_PASSWORD.equals(CONTACT_PASSWORD)) {
            inputConfirm.setErrorEnabled(true);
            inputConfirm.setError(getString(R.string.sign_up_confirm_password_mismatch));
            inputConfirm.requestFocus();
            inputGroomerName.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(GROOMER_PHONE_NUMBER_1)) {
            inputPhoneNumber1.setErrorEnabled(true);
            inputPhoneNumber1.setError(getString(R.string.sign_up_phone_1_error));
            inputPhoneNumber1.requestFocus();
            inputGroomerName.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirm.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(GROOMER_ADDRESS)) {
            inputAddress.setErrorEnabled(true);
            inputAddress.setError(getString(R.string.sign_up_mailing_address_error));
            inputAddress.requestFocus();
            inputGroomerName.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirm.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(GROOMER_PINCODE)) {
            inputPinCode.setErrorEnabled(true);
            inputPinCode.setError(getString(R.string.sign_up_pin_code_error));
            inputPinCode.requestFocus();
            inputGroomerName.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirm.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
        } else if (GROOMER_LATITUDE == null || GROOMER_LONGITUDE == null) {
            Toast.makeText(getApplicationContext(), "Mark the location on the Map", Toast.LENGTH_LONG).show();
            inputGroomerName.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirm.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (GROOMER_LOGO_URI == null)    {
            Toast.makeText(getApplicationContext(), "Select a Logo or a Cover Photo", Toast.LENGTH_LONG).show();
            imgvwLogo.requestFocus();
            inputGroomerName.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirm.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else {
            /* DISABLE ALL ERROR MESSAGES */
            inputGroomerName.setErrorEnabled(false);
            inputContactName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirm.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);

            /* CREATE THE NEW ACCOUNT */
            createAccount();
        }
    }

    /** CREATE THE NEW ACCOUNT **/
    private void createAccount() {
        /* SHOW THE PROGRESS DIALOG WHILE UPLOADING THE IMAGE **/
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait while we create your new account....");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        auth.createUserWithEmailAndPassword(CONTACT_EMAIL, CONTACT_PASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())   {
                    /* GET THE NEW USERS INSTANCE */
                    user = auth.getCurrentUser();

                    if (user != null)   {
                        CONTACT_EMAIL = user.getEmail();
                        /* CHECK IF THE GROOMER ACCOUNT EXISTS */
                        checkAccountExists();
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

    /** CHECK IF THE GROOMER ACCOUNT EXISTS **/
    private void checkAccountExists() {
        GroomersAPI api = ZenApiClient.getClient().create(GroomersAPI.class);
        Call<Groomer> call = api.groomerExists(CONTACT_EMAIL);
        call.enqueue(new Callback<Groomer>() {
            @Override
            public void onResponse(Call<Groomer> call, Response<Groomer> response) {
                String message = response.body().getMessage();
                if (message != null)    {
//                    Log.e("MESSAGE", message);
                    if (message.equalsIgnoreCase("Groomer account exists..."))   {
                        progressDialog.dismiss();
                        Intent intent = new Intent(SignUpActivity.this, LandingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else if (message.equalsIgnoreCase("Groomer account doesn't exist..."))    {
                        /* UPLOAD THE GROOMER'S LOGO OR THE COVER PHOTO */
                        uploadLogo();
                    }
                }
            }

            @Override
            public void onFailure(Call<Groomer> call, Throwable t) {
//                Log.e("EXISTS FAILURE", t.getMessage());
            }
        });
    }

    /** UPLOAD THE GROOMER'S LOGO OR THE COVER PHOTO **/
    private void uploadLogo() {
        GROOMER_LOGO_FILE_NAME = GROOMER_NAME.replaceAll(" ", "_").toLowerCase().trim() + "_" + user.getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference refStorage = storageReference.child("Groomer Logos").child(GROOMER_LOGO_FILE_NAME);
        UploadTask uploadTask = refStorage.putFile(GROOMER_LOGO_URI);
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
                    Uri downloadURL = task.getResult();
                    GROOMER_LOGO = String.valueOf(downloadURL);
                    if (GROOMER_LOGO != null)    {
                        /* CREATE THE NEW GROOMER ACCOUNT */
                        createGroomerAccount();
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

    /** CREATE THE NEW GROOMER ACCOUNT **/
    private void createGroomerAccount() {
        GroomersAPI api = ZenApiClient.getClient().create(GroomersAPI.class);
        Call<Groomer> call = api.registerGroomerAccount(
                user.getUid(), GROOMER_NAME, GROOMER_LOGO, CONTACT_NAME, CONTACT_EMAIL,
                GROOMER_PHONE_PREFIX_1, GROOMER_PHONE_NUMBER_1, GROOMER_PHONE_PREFIX_2, GROOMER_PHONE_NUMBER_2,
                GROOMER_ADDRESS, GROOMER_PINCODE, COUNTRY_ID, STATE_ID, CITY_ID,
                GROOMER_LATITUDE, GROOMER_LONGITUDE,
                null, VALID_FROM, VALID_TO, "Unverified");
        call.enqueue(new Callback<Groomer>() {
            @Override
            public void onResponse(Call<Groomer> call, Response<Groomer> response) {
                if (response.isSuccessful())    {

                    /* GET THE NEW GROOMER ACCOUNT ID */
                    final String groomerID = response.body().getGroomerID();
//                    Log.e("GROOMER ID", groomerID);

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
                    strErrorMessage = "There was an error signing you up. Please submit your details again.";
                    progressDialog.dismiss();

                    /* DELETE THE USER **/
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
                                        .icon(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.ic_info_outline_black_24dp))
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
            public void onFailure(Call<Groomer> call, Throwable t) {
//                Log.e("SIGN UP FAILURE", t.getMessage());
            }
        });
    }

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

    /***** FETCH THE LIST OF STATES IN THE SELECTED COUNTRY *****/
    private void getAllStates() {
        LocationAPI api = ZenApiClient.getClient().create(LocationAPI.class);
        Call<States> call = api.allStates(COUNTRY_ID);
        call.enqueue(new Callback<States>() {
            @Override
            public void onResponse(Call<States> call, Response<States> response) {
                arrStates = response.body().getStates();
                if (arrStates != null && arrStates.size() > 0)  {
                    /* INSTANTIATE THE STATES ADAPTER */
                    StatesAdapter statesAdapter = new StatesAdapter(SignUpActivity.this, arrStates);

                    /* SET THE STATES SPINNER */
                    spnState.setAdapter(statesAdapter);
                }

//                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
//                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<States> call, Throwable t) {
//                Log.e("FAILURE", t.getMessage());
            }
        });
    }

    /***** FETCH THE LIST OF CITIES IN THE SELECTED STATE *****/
    private void getAllCities() {
        LocationAPI api = ZenApiClient.getClient().create(LocationAPI.class);
        Call<Cities> call = api.allCities(STATE_ID);
        call.enqueue(new Callback<Cities>() {
            @Override
            public void onResponse(Call<Cities> call, Response<Cities> response) {
                arrCities = response.body().getCities();
                if (arrCities != null && arrCities.size() > 0)  {
                    /* INSTANTIATE THE CITIES ADAPTER */
                    CitiesAdapter citiesAdapter = new CitiesAdapter(SignUpActivity.this, arrCities);

                    /* SET THE CITIES SPINNER */
                    spnCity.setAdapter(citiesAdapter);
                }
            }

            @Override
            public void onFailure(Call<Cities> call, Throwable t) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_LOCATION) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    GROOMER_LATITUDE = String.valueOf(bundle.getDouble("LATITUDE"));
                    GROOMER_LONGITUDE = String.valueOf(bundle.getDouble("LONGITUDE"));
                    txtLocation.setText(getString(R.string.sign_up_lat_lng_placeholder, GROOMER_LATITUDE, GROOMER_LONGITUDE));
//                    Log.e("COORDS", TRAINER_LATITUDE + " " + TRAINER_LONGITUDE);
                }

//                /* GET THE APPROXIMATE ADDRESS FOR DISPLAY */
//                try {
//                    Geocoder geocoder;
//                    List<Address> addresses;
//                    geocoder = new Geocoder(this, Locale.getDefault());
//                    addresses = geocoder.getFromLocation(Double.valueOf(GROOMER_LATITUDE), Double.valueOf(GROOMER_LONGITUDE), 1);
//                    String address = addresses.get(0).getAddressLine(0);
//                    if (!TextUtils.isEmpty(address)) {
//                        txtLocation.setText(address);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
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
            imgvwLogo.setImageBitmap(bitmap);
            imgvwLogo.setScaleType(ImageView.ScaleType.CENTER_CROP);

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
                GROOMER_LOGO_URI = Uri.fromFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***** CHECK STORAGE PERMISSION *****/
    private void checkStoragePermission() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(SignUpActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)   {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))    {
                /* SHOW THE DIALOG */
                new MaterialDialog.Builder(this)
                        .icon(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_black_24dp))
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

    /***** VALIDATE EMAIL SYNTAX / FORMAT *****/
    private static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EasyImage.clearConfiguration(this);
    }
}