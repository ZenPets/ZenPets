package co.zenpets.doctors.credentials;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
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
import android.widget.RadioGroup;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.zenpets.doctors.R;
import co.zenpets.doctors.landing.LandingActivity;
import co.zenpets.doctors.utils.TypefaceSpan;
import co.zenpets.doctors.utils.adapters.doctors.DoctorPrefixAdapter;
import co.zenpets.doctors.utils.adapters.location.CitiesAdapter;
import co.zenpets.doctors.utils.adapters.location.StatesAdapter;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.legal.NewPrivacyPolicyActivity;
import co.zenpets.doctors.utils.legal.SellerAgreementActivity;
import co.zenpets.doctors.utils.models.doctors.account.SignUp;
import co.zenpets.doctors.utils.models.doctors.account.SignUpAPI;
import co.zenpets.doctors.utils.models.doctors.exists.DoctorExists;
import co.zenpets.doctors.utils.models.doctors.exists.DoctorExistsAPI;
import co.zenpets.doctors.utils.models.doctors.subscription.Subscription;
import co.zenpets.doctors.utils.models.doctors.subscription.SubscriptionAPI;
import co.zenpets.doctors.utils.models.location.CitiesData;
import co.zenpets.doctors.utils.models.location.CityData;
import co.zenpets.doctors.utils.models.location.LocationAPI;
import co.zenpets.doctors.utils.models.location.StateData;
import co.zenpets.doctors.utils.models.location.StatesData;
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

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int ACCESS_STORAGE_CONSTANT = 201;

    /** STRINGS TO HOLD THE USER PROVIDED INFORMATION **/
    private String DOCTOR_PREFIX = null;
    private String DOCTOR_NAME = null;
    private String DOCTOR_EMAIL = null;
    private String DOCTOR_PHONE_PREFIX = "91";
    private String DOCTOR_PHONE_NUMBER = null;
    private String DOCTOR_ADDRESS = null;
    private final String COUNTRY_ID = "51";
    private String STATE_ID = null;
    private String CITY_ID = null;
    private String DOCTOR_GENDER = "Male";
    private String DOCTOR_SUMMARY = null;
    private String DOCTOR_EXPERIENCE = null;
    private String DOCTOR_CHARGES = null;
    private String DOCTOR_DISPLAY_PROFILE = null;
    private String DOCTOR_DISPLAY_PROFILE_FILE_NAME = null;
    private Uri DOCTOR_DISPLAY_PROFILE_URI = null;
    private String PASSWORD = null;
    private String VALID_FROM = null;
    private String VALID_TO = null;

    /** STRING TO HOLD THE ERROR MESSAGE, IF ANY **/
    private String strErrorMessage = null;

    /** THE COUNTRY, STATES AND CITIES ARRAY LISTS **/
    private ArrayList<StateData> arrStates = new ArrayList<>();
    private ArrayList<CityData> arrCities = new ArrayList<>();

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog progressDialog;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.spnPrefix) AppCompatSpinner spnPrefix;
    @BindView(R.id.inputFullName) TextInputLayout inputFullName;
    @BindView(R.id.edtFullName) TextInputEditText edtFullName;
    @BindView(R.id.inputEmailAddress) TextInputLayout inputEmailAddress;
    @BindView(R.id.edtEmailAddress) TextInputEditText edtEmailAddress;
    @BindView(R.id.inputPassword) TextInputLayout inputPassword;
    @BindView(R.id.edtPassword) TextInputEditText edtPassword;
    @BindView(R.id.inputConfirmPassword) TextInputLayout inputConfirmPassword;
    @BindView(R.id.edtConfirmPassword) TextInputEditText edtConfirmPassword;
    @BindView(R.id.inputPhoneNumber) TextInputLayout inputPhoneNumber;
    @BindView(R.id.edtPhoneNumber) TextInputEditText edtPhoneNumber;
    @BindView(R.id.inputAddress) TextInputLayout inputAddress;
    @BindView(R.id.edtAddress) TextInputEditText edtAddress;
    @BindView(R.id.spnState) AppCompatSpinner spnState;
    @BindView(R.id.spnCity) AppCompatSpinner spnCity;
    @BindView(R.id.rdgGender) RadioGroup rdgGender;
    @BindView(R.id.inputSummary) TextInputLayout inputSummary;
    @BindView(R.id.edtSummary) TextInputEditText edtSummary;
    @BindView(R.id.inputDoctorsExperience) TextInputLayout inputDoctorsExperience;
    @BindView(R.id.edtDoctorsExperience) TextInputEditText edtDoctorsExperience;
    @BindView(R.id.inputDoctorsCharges) TextInputLayout inputDoctorsCharges;
    @BindView(R.id.edtDoctorsCharges) TextInputEditText edtDoctorsCharges;
    @BindView(R.id.imgvwProfilePicture) AppCompatImageView imgvwProfilePicture;
    @BindView(R.id.txtTermsOfService) AppCompatTextView txtTermsOfService;

    /** SELECT THE PROFILE PICTURE **/
    @OnClick(R.id.imgvwProfilePicture) void selectProfilePicture() {
        /* CHECK STORAGE PERMISSION */
        checkStoragePermission();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);
        ButterKnife.bind(this);

        /* INSTANTIATE THE FIREBASE AUTH INSTANCE **/
        auth = FirebaseAuth.getInstance();

        /* THE EASY IMAGE CONFIGURATION */
        EasyImage.configuration(this)
                .setImagesFolderName("Zen Pets")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(true)
                .setAllowMultiplePickInGallery(false);

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

        /* SET THE TERMS OF SERVICE TEXT **/
        setTermsAndConditions(txtTermsOfService);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* SHOW THE PROGRESS WHILE FETCHING THE LOCATION DATA */
        linlaProgress.setVisibility(View.VISIBLE);

        /* POPULATE THE PREFIX SPINNER */
        String[] strServes = getResources().getStringArray(R.array.prefix);
        final List<String> arrPrefix;
        arrPrefix = Arrays.asList(strServes);
        spnPrefix.setAdapter(new DoctorPrefixAdapter(
                SignUpActivity.this,
                R.layout.doctor_prefix_row,
                arrPrefix));

        /* SELECT A PREFIX */
        spnPrefix.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DOCTOR_PREFIX = arrPrefix.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

//        /* FETCH THE LIST OF COUNTRIES */
//        getAllCountries();

        /* FETCH THE LIST OF STATES IN THE SELECTED COUNTRY */
        getAllStates();

        /* SELECT A COUNTRY */
//        spnCountry.setOnItemSelectedListener(selectCountry);

        /* SELECT A STATE */
        spnState.setOnItemSelectedListener(selectState);

        /* SELECT A CITY */
        spnCity.setOnItemSelectedListener(selectCity);

        /* SELECT THE USER'S GENDER */
        rdgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rdbtnMale:
                        /* SET THE GENDER TO MALE */
                        DOCTOR_GENDER = "Male";
                        break;
                    case R.id.rdbtnFemale:
                        /* SET THE GENDER TO FEMALE */
                        DOCTOR_GENDER = "Female";
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /***** CHECK FOR ALL ACCOUNT DETAILS  *****/
    private void checkAccountDetails() {
        /* HIDE THE KEYBOARD **/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtFullName.getWindowToken(), 0);
        }

        /* COLLECT THE NECESSARY DATA **/
        DOCTOR_NAME = edtFullName.getText().toString().trim();
        DOCTOR_EMAIL = edtEmailAddress.getText().toString().trim();
        PASSWORD = edtPassword.getText().toString().trim();
        String CONFIRM_PASSWORD = edtConfirmPassword.getText().toString().trim();
        DOCTOR_PHONE_PREFIX = "91";
        DOCTOR_PHONE_NUMBER = edtPhoneNumber.getText().toString().trim();
        DOCTOR_ADDRESS = edtAddress.getText().toString().trim();
        DOCTOR_SUMMARY = edtSummary.getText().toString().trim();
        DOCTOR_EXPERIENCE = edtDoctorsExperience.getText().toString().trim();
        DOCTOR_CHARGES = edtDoctorsCharges.getText().toString().trim();

        if (!TextUtils.isEmpty(DOCTOR_NAME) && !TextUtils.isEmpty(DOCTOR_EMAIL))  {
            DOCTOR_DISPLAY_PROFILE_FILE_NAME = DOCTOR_NAME.replaceAll(" ", "_").toLowerCase().trim() + "_" + DOCTOR_EMAIL;
        } else {
            DOCTOR_DISPLAY_PROFILE_FILE_NAME = null;
        }
        boolean blnValidEmail = isValidEmail(DOCTOR_EMAIL);

        /* VALIDATE THE DATA **/
        if (TextUtils.isEmpty(DOCTOR_NAME)) {
            inputFullName.setErrorEnabled(true);
            inputFullName.setError("Provide your full name");
            inputFullName.requestFocus();
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputDoctorsExperience.setErrorEnabled(false);
            inputDoctorsCharges.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(DOCTOR_EMAIL)) {
            inputEmailAddress.setErrorEnabled(true);
            inputEmailAddress.setError("Provide your email address");
            inputEmailAddress.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputDoctorsExperience.setErrorEnabled(false);
            inputDoctorsCharges.setErrorEnabled(false);
        } else if (!blnValidEmail)  {
            inputEmailAddress.setErrorEnabled(true);
            inputEmailAddress.setError("Provide a valid email address");
            inputEmailAddress.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputDoctorsExperience.setErrorEnabled(false);
            inputDoctorsCharges.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(PASSWORD)) {
            inputPassword.setErrorEnabled(true);
            inputPassword.setError("Choose a password");
            inputPassword.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputDoctorsExperience.setErrorEnabled(false);
            inputDoctorsCharges.setErrorEnabled(false);
        } else if (PASSWORD.length() < 8) {
            inputPassword.setErrorEnabled(true);
            inputPassword.setError("Password has to be at least 8 characters");
            inputPassword.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputDoctorsExperience.setErrorEnabled(false);
            inputDoctorsCharges.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(CONFIRM_PASSWORD)) {
            inputConfirmPassword.setErrorEnabled(true);
            inputConfirmPassword.setError("Confirm your Password");
            inputConfirmPassword.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputDoctorsExperience.setErrorEnabled(false);
            inputDoctorsCharges.setErrorEnabled(false);
        } else if (CONFIRM_PASSWORD.length() < 8) {
            inputConfirmPassword.setErrorEnabled(true);
            inputConfirmPassword.setError("Password has to be at least 8 characters");
            inputConfirmPassword.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputDoctorsExperience.setErrorEnabled(false);
            inputDoctorsCharges.setErrorEnabled(false);
        } else if (!CONFIRM_PASSWORD.equals(PASSWORD))    {
            inputConfirmPassword.setErrorEnabled(true);
            inputConfirmPassword.setError("The passwords do not match");
            inputConfirmPassword.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputDoctorsExperience.setErrorEnabled(false);
            inputDoctorsCharges.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(DOCTOR_PHONE_NUMBER)) {
            inputPhoneNumber.setError("Enter your phone number");
            inputPhoneNumber.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputDoctorsExperience.setErrorEnabled(false);
            inputDoctorsCharges.setErrorEnabled(false);
        } else if (COUNTRY_ID == null) {
            Toast.makeText(getApplicationContext(), "Select your Country", Toast.LENGTH_LONG).show();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputDoctorsExperience.setErrorEnabled(false);
            inputDoctorsCharges.setErrorEnabled(false);
        } else if (STATE_ID == null) {
            Toast.makeText(getApplicationContext(), "Select your State", Toast.LENGTH_LONG).show();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputDoctorsExperience.setErrorEnabled(false);
            inputDoctorsCharges.setErrorEnabled(false);
        } else if (CITY_ID == null) {
            Toast.makeText(getApplicationContext(), "Select your City", Toast.LENGTH_LONG).show();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputDoctorsExperience.setErrorEnabled(false);
            inputDoctorsCharges.setErrorEnabled(false);
        } else if (DOCTOR_SUMMARY.isEmpty())    {
            inputSummary.setErrorEnabled(true);
            inputSummary.setError("Tell us something about your practise");
            inputSummary.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputDoctorsExperience.setErrorEnabled(false);
            inputDoctorsCharges.setErrorEnabled(false);
        } else if (DOCTOR_EXPERIENCE.isEmpty()) {
            inputDoctorsExperience.setErrorEnabled(true);
            inputDoctorsExperience.setError("Provide your experience (in years)");
            inputDoctorsExperience.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputDoctorsCharges.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(DOCTOR_CHARGES))   {
            inputDoctorsCharges.setErrorEnabled(true);
            inputDoctorsCharges.setError("Please your minimum consultation charges");
            inputDoctorsCharges.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputDoctorsExperience.setErrorEnabled(false);
        } else if (DOCTOR_DISPLAY_PROFILE_URI == null)  {
            Toast.makeText(getApplicationContext(), "Select a Profile Picture", Toast.LENGTH_LONG).show();
            imgvwProfilePicture.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputDoctorsExperience.setErrorEnabled(false);
            inputDoctorsCharges.setErrorEnabled(false);
        } else {
            /* DISABLE ALL ERRORS */
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputDoctorsExperience.setErrorEnabled(false);
            inputDoctorsCharges.setErrorEnabled(false);

            /* CREATE THE NEW ACCOUNT */
            createAccount();
        }
    }

    /***** CREATE THE NEW ACCOUNT *****/
    private void createAccount() {
        /* SHOW THE PROGRESS DIALOG WHILE UPLOADING THE IMAGE **/
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait while we create your new account....");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        auth.createUserWithEmailAndPassword(DOCTOR_EMAIL, PASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())   {
                    /* GET THE NEW USERS INSTANCE */
                    user = auth.getCurrentUser();

                    if (user != null)   {
                        DOCTOR_EMAIL = user.getEmail();
                        /* CHECK IF THE USER RECORD EXISTS IN THE PRIMARY DATABASE */
                        checkUserExists();
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
//                Crashlytics.logException(e);
            }
        });
    }

    /***** CHECK IF THE USER RECORD EXISTS IN THE PRIMARY DATABASE *****/
    private void checkUserExists() {
        DoctorExistsAPI api = ZenApiClient.getClient().create(DoctorExistsAPI.class);
        Call<DoctorExists> call = api.doctorExists(DOCTOR_EMAIL);
        call.enqueue(new Callback<DoctorExists>() {
            @Override
            public void onResponse(@NonNull Call<DoctorExists> call, @NonNull Response<DoctorExists> response) {
                String message = response.body().getMessage();
                if (message != null)    {
//                    Log.e("MESSAGE", message);
                    if (message.equalsIgnoreCase("User record exists..."))   {
                        progressDialog.dismiss();
                        Intent intent = new Intent(SignUpActivity.this, LandingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else if (message.equalsIgnoreCase("User record doesn't exist..."))    {
                        /* UPLOAD THE USER PROFILE PICTURE */
                        uploadProfilePicture();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DoctorExists> call, @NonNull Throwable t) {
//                Log.e("EXISTS FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /* UPLOAD THE USER PROFILE PICTURE */
    private void uploadProfilePicture() {
        DOCTOR_DISPLAY_PROFILE_FILE_NAME = DOCTOR_NAME.replaceAll(" ", "_").toLowerCase().trim() + "_" + user.getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference refStorage = storageReference.child("Doctor Profiles").child(DOCTOR_DISPLAY_PROFILE_FILE_NAME);
        UploadTask uploadTask = refStorage.putFile(DOCTOR_DISPLAY_PROFILE_URI);
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
                    DOCTOR_DISPLAY_PROFILE = String.valueOf(downloadURL);
                    if (DOCTOR_DISPLAY_PROFILE != null)    {
                        /* CREATE THE NEW DOCTOR'S RECORD */
                        createDoctorRecord();
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

    /***** CREATE THE NEW DOCTOR'S RECORD *****/
    private void createDoctorRecord() {
        SignUpAPI api = ZenApiClient.getClient().create(SignUpAPI.class);
        Call<SignUp> call = api.newDoctor(
                user.getUid(), DOCTOR_PREFIX, DOCTOR_NAME,
                DOCTOR_EMAIL, DOCTOR_PHONE_PREFIX, DOCTOR_PHONE_NUMBER,
                DOCTOR_ADDRESS, COUNTRY_ID, STATE_ID, CITY_ID,
                DOCTOR_GENDER, DOCTOR_SUMMARY, DOCTOR_EXPERIENCE, DOCTOR_CHARGES, DOCTOR_DISPLAY_PROFILE);
        call.enqueue(new Callback<SignUp>() {
            @Override
            public void onResponse(@NonNull Call<SignUp> call, @NonNull Response<SignUp> response) {
                if (response.isSuccessful())    {
                    final String doctorID = response.body().getDoctorID();

                    /* SHOW THE OFFER DIALOG **/
                    new MaterialDialog.Builder(SignUpActivity.this)
                            .title("An Offer For You!")
                            .content("For our new Veterinarians, we are offering a free upgrade to help you see all the features of the \"Zen Pets - For Doctors\" application." +
                                    "\n\nThis upgrade is valid for 3 months starting today.")
                            .positiveText("Absolutely")
                            .negativeText("Not Now")
                            .theme(Theme.LIGHT)
                            .icon(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.ic_info_outline_black_24dp))
                            .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();

                                    /* CREATE THE FREE TRIAL UPGRADE SUBSCRIPTION RECORD */
                                    createTrialSubscription(doctorID);
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                    progressDialog.dismiss();
                                    auth.signOut();
                                    Toast.makeText(getApplicationContext(), "Account created successfully...", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            }).show();
                } else {
                    /* SET THE ERROR MESSAGE **/
                    strErrorMessage = "There was en error signing you up. Please submit your details again.";
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
            public void onFailure(@NonNull Call<SignUp> call, @NonNull Throwable t) {
//                Log.e("SIGN UP FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /***** CREATE THE FREE TRIAL UPGRADE SUBSCRIPTION RECORD *****/
    private void createTrialSubscription(String doctorID) {
        SubscriptionAPI api = ZenApiClient.getClient().create(SubscriptionAPI.class);
        Call<Subscription> call = api.newDoctorSubscription(
                doctorID, VALID_FROM, VALID_TO, "Free Trial");
        call.enqueue(new Callback<Subscription>() {
            @Override
            public void onResponse(@NonNull Call<Subscription> call, @NonNull Response<Subscription> response) {
                if (response.isSuccessful())    {
                    String subscriptionID = response.body().getSubscriptionID();
                    progressDialog.dismiss();
                    auth.signOut();
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Subscription> call, @NonNull Throwable t) {
//                Crashlytics.logException(t);
            }
        });
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
                /* CHECK FOR ALL ACCOUNT DETAILS  */
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
        Call<StatesData> call = api.allStates(COUNTRY_ID);
        call.enqueue(new Callback<StatesData>() {
            @Override
            public void onResponse(@NonNull Call<StatesData> call, @NonNull Response<StatesData> response) {
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
            public void onFailure(@NonNull Call<StatesData> call, @NonNull Throwable t) {
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
            public void onResponse(@NonNull Call<CitiesData> call, @NonNull Response<CitiesData> response) {
                arrCities = response.body().getCities();
                if (arrCities != null && arrCities.size() > 0)  {
                    /* INSTANTIATE THE CITIES ADAPTER */
                    CitiesAdapter citiesAdapter = new CitiesAdapter(SignUpActivity.this, arrCities);

                    /* SET THE CITIES SPINNER */
                    spnCity.setAdapter(citiesAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CitiesData> call, @NonNull Throwable t) {
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

    /***** SET THE TERMS AND CONDITIONS *****/
    private void setTermsAndConditions(AppCompatTextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(getResources().getString(R.string.terms_part_1));
        spanTxt.append(getResources().getString(R.string.terms_part_2));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent showSellerAgreement = new Intent(getApplicationContext(), SellerAgreementActivity.class);
                startActivity(showSellerAgreement);
            }
        }, spanTxt.length() - getResources().getString(R.string.terms_part_2).length(), spanTxt.length(), 0);
        spanTxt.append(getResources().getString(R.string.terms_part_3));
        spanTxt.setSpan(new ForegroundColorSpan(Color.BLACK), 48, spanTxt.length(), 0);
        spanTxt.append(getResources().getString(R.string.terms_part_4));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent showPrivacyPolicy = new Intent(getApplicationContext(), NewPrivacyPolicyActivity.class);
                startActivity(showPrivacyPolicy);
            }
        }, spanTxt.length() - " Privacy Policy".length(), spanTxt.length(), 0);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, AppCompatTextView.BufferType.SPANNABLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
            imgvwProfilePicture.setImageBitmap(bitmap);
            imgvwProfilePicture.setScaleType(ImageView.ScaleType.CENTER_CROP);

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
                DOCTOR_DISPLAY_PROFILE_URI = Uri.fromFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
//            Crashlytics.logException(e);
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