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
import android.support.v7.widget.AppCompatRadioButton;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.AppPrefs;
import co.zenpets.doctors.utils.TypefaceSpan;
import co.zenpets.doctors.utils.adapters.doctors.DoctorPrefixAdapter;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.legal.NewPrivacyPolicyActivity;
import co.zenpets.doctors.utils.legal.SellerAgreementActivity;
import co.zenpets.doctors.utils.models.doctors.account.AccountData;
import co.zenpets.doctors.utils.models.doctors.account.SignUpAPI;
import co.zenpets.doctors.utils.models.doctors.profile.DoctorProfileAPI;
import co.zenpets.doctors.utils.models.doctors.profile.DoctorProfileData;
import co.zenpets.doctors.utils.models.doctors.subscription.Subscription;
import co.zenpets.doctors.utils.models.doctors.subscription.SubscriptionAPI;
import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
public class ClaimSignUpActivity extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE INCOMING DOCTOR ID **/
    private String DOCTOR_ID = null;

    /** A FIREBASE AUTH INSTANCE **/
    private FirebaseAuth auth;

    /** A FIREBASE USER INSTANCE **/
    private FirebaseUser user;

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int ACCESS_STORAGE_CONSTANT = 201;

    /** STRING TO HOLD THE ERROR MESSAGE, IF ANY **/
    private String strErrorMessage = null;

    /** THE DOCTOR DETAILS **/
    private String DOCTOR_AUTH_ID = null;
    private String DOCTOR_PREFIX = null;
    private String DOCTOR_NAME = null;
    private String DOCTOR_EMAIL = null;
    private String DOCTOR_PHONE_PREFIX = null;
    private String DOCTOR_PHONE_NUMBER = null;
    private String DOCTOR_ADDRESS = null;
    private String COUNTRY_ID = "51";
    private String COUNTRY_NAME = "India";
    private String STATE_ID = null;
    private String STATE_NAME = null;
    private String CITY_ID = null;
    private String CITY_NAME = null;
    private String DOCTOR_GENDER = "Male";
    private String DOCTOR_SUMMARY = null;
    private String DOCTOR_EXPERIENCE = null;
    private String DOCTOR_CHARGES = null;
    private String DOCTOR_DISPLAY_PROFILE = null;
    private String DOCTOR_DISPLAY_PROFILE_FILE_NAME = null;
    private Uri DOCTOR_DISPLAY_PROFILE_URI = null;
    private final String DOCTOR_TOKEN = null;
    private String PASSWORD = null;

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog dialog;

    /** CAST THE LAYOUT ELEMENTS **/
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
    @BindView(R.id.txtDoctorState) AppCompatTextView txtDoctorState;
    @BindView(R.id.txtDoctorCity) AppCompatTextView txtDoctorCity;
    @BindView(R.id.rdgGender) RadioGroup rdgGender;
    @BindView(R.id.rdbtnMale) AppCompatRadioButton rdbtnMale;
    @BindView(R.id.rdbtnFemale) AppCompatRadioButton rdbtnFemale;
    @BindView(R.id.inputSummary) TextInputLayout inputSummary;
    @BindView(R.id.edtSummary) TextInputEditText edtSummary;
    @BindView(R.id.inputDoctorsExperience) TextInputLayout inputDoctorsExperience;
    @BindView(R.id.edtDoctorsExperience) TextInputEditText edtDoctorsExperience;
    @BindView(R.id.inputDoctorsCharges) TextInputLayout inputDoctorsCharges;
    @BindView(R.id.edtDoctorsCharges) TextInputEditText edtDoctorsCharges;
    @BindView(R.id.imgvwDoctorProfile) AppCompatImageView imgvwDoctorProfile;
    @BindView(R.id.txtTermsOfService) AppCompatTextView txtTermsOfService;

    /** SELECT THE PROFILE PICTURE **/
    @OnClick(R.id.imgvwDoctorProfile) void selectProfilePicture() {
        /* CHECK STORAGE PERMISSION */
        checkStoragePermission();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.claim_sign_up_activity);
        ButterKnife.bind(this);

        /* INSTANTIATE THE FIREBASE AUTH INSTANCE **/
        auth = FirebaseAuth.getInstance();

        /* THE EASY IMAGE CONFIGURATION */
        EasyImage.configuration(this)
                .setImagesFolderName("Zen Pets")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(true)
                .setAllowMultiplePickInGallery(false);

        /* SET THE TERMS OF SERVICE TEXT **/
        setTermsAndConditions(txtTermsOfService);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* POPULATE THE PREFIX SPINNER */
        String[] strServes = getResources().getStringArray(R.array.prefix);
        final List<String> arrPrefix;
        arrPrefix = Arrays.asList(strServes);
        spnPrefix.setAdapter(new DoctorPrefixAdapter(
                ClaimSignUpActivity.this,
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

    /***** FETCH THE DOCTOR DETAILS *****/
    private void fetchDoctorDetails() {
        DoctorProfileAPI api = ZenApiClient.getClient().create(DoctorProfileAPI.class);
        Call<DoctorProfileData> call = api.fetchDoctorProfile(DOCTOR_ID);
        call.enqueue(new Callback<DoctorProfileData>() {
            @Override
            public void onResponse(Call<DoctorProfileData> call, Response<DoctorProfileData> response) {
                DoctorProfileData data = response.body();
                if (data != null)   {
                    /* GET THE AUTH ID */
                    DOCTOR_AUTH_ID = data.getDoctorAuthID();

                    /* GET THE DOCTOR'S PREFIX */
                    DOCTOR_PREFIX = data.getDoctorPrefix();

                    /* GET THE DOCTOR'S NAME */
                    DOCTOR_NAME = data.getDoctorName();
                    if (DOCTOR_NAME != null
                            && !DOCTOR_NAME.equalsIgnoreCase("")
                            && !DOCTOR_NAME.equalsIgnoreCase("null"))  {
                        edtFullName.setText(DOCTOR_NAME);
                    }

                    /* GET THE DOCTOR'S EMAIL ADDRESS */
                    DOCTOR_EMAIL = data.getDoctorEmail();
                    if (DOCTOR_EMAIL != null
                            && !DOCTOR_EMAIL.equalsIgnoreCase("")
                            && !DOCTOR_EMAIL.equalsIgnoreCase("null"))  {
                        edtEmailAddress.setText(DOCTOR_EMAIL);
                    }

                    /* GET THE DOCTOR'S PHONE PREFIX AND NUMBER */
                    DOCTOR_PHONE_PREFIX = data.getDoctorPhonePrefix();
                    DOCTOR_PHONE_NUMBER = data.getDoctorPhoneNumber();

                    /* GET THE DOCTOR'S ADDRESS */
                    DOCTOR_ADDRESS = data.getDoctorAddress();
                    if (DOCTOR_ADDRESS != null
                            && !DOCTOR_ADDRESS.equalsIgnoreCase("")
                            && !DOCTOR_ADDRESS.equalsIgnoreCase("null"))    {
                        edtAddress.setText(DOCTOR_ADDRESS);
                    }

                    /* GET THE DOCTOR'S COUNTRY ID AND NAME */
                    COUNTRY_ID = data.getCountryID();
                    COUNTRY_NAME = data.getCountryName();

                    /* GET THE DOCTOR'S STATE ID AND NAME */
                    STATE_ID = data.getStateID();
                    STATE_NAME = data.getStateName();
                    txtDoctorState.setText(STATE_NAME);

                    /* GET THE DOCTOR'S CITY ID AND NAME */
                    CITY_ID = data.getCityID();
                    CITY_NAME = data.getCityName();
                    txtDoctorCity.setText(CITY_NAME);

                    /* GET THE DOCTOR'S GENDER */
                    DOCTOR_GENDER = data.getDoctorGender();
                    if (DOCTOR_GENDER != null
                            && !DOCTOR_GENDER.equalsIgnoreCase("")
                            && !DOCTOR_GENDER.equalsIgnoreCase("null"))    {
                        if (DOCTOR_GENDER.equalsIgnoreCase("Male")) {
                            rdbtnMale.setChecked(true);
                            rdbtnFemale.setChecked(false);
                        } else if (DOCTOR_GENDER.equalsIgnoreCase("Female"))    {
                            rdbtnFemale.setChecked(true);
                            rdbtnMale.setChecked(false);
                        } else {
                            rdbtnMale.setChecked(true);
                            rdbtnFemale.setChecked(false);
                        }
                    } else {
                        rdbtnMale.setChecked(true);
                        rdbtnFemale.setChecked(false);
                    }

                    /* GET THE DOCTOR'S SUMMARY */
                    DOCTOR_SUMMARY = data.getDoctorSummary();

                    /* GET THE DOCTOR'S EXPERIENCE*/
                    DOCTOR_EXPERIENCE = data.getDoctorExperience();

                    /* GET THE DOCTOR'S CHARGES */
                    DOCTOR_CHARGES = data.getDoctorCharges();

                    /* GET THE DOCTOR'S DISPLAY PROFILE */
                    DOCTOR_DISPLAY_PROFILE = data.getDoctorDisplayProfile();
                }
            }

            @Override
            public void onFailure(Call<DoctorProfileData> call, Throwable t) {
//                Log.e("PROFILE FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("DOCTOR_ID"))  {
            DOCTOR_ID = bundle.getString("DOCTOR_ID");
            if (DOCTOR_ID != null)  {
                /* FETCH THE DOCTOR DETAILS */
                fetchDoctorDetails();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** SET THE TERMS AND CONDITIONS *****/
    private void setTermsAndConditions(AppCompatTextView view) {
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
                Intent showPrivacyPolicy = new Intent(getApplicationContext(), NewPrivacyPolicyActivity.class);
                startActivity(showPrivacyPolicy);
            }
        }, spanTxt.length() - " Privacy Policy".length(), spanTxt.length(), 0);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, AppCompatTextView.BufferType.SPANNABLE);
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Complete Sign Up";
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
        MenuInflater inflater = new MenuInflater(ClaimSignUpActivity.this);
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
            inputPhoneNumber.setErrorEnabled(false);
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
            inputPhoneNumber.setErrorEnabled(false);
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
            inputPhoneNumber.setErrorEnabled(false);
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
            inputPhoneNumber.setErrorEnabled(false);
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
            inputPhoneNumber.setErrorEnabled(false);
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
            inputPhoneNumber.setErrorEnabled(false);
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
            inputPhoneNumber.setErrorEnabled(false);
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
            inputPhoneNumber.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputDoctorsExperience.setErrorEnabled(false);
            inputDoctorsCharges.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(DOCTOR_PHONE_NUMBER)) {
            inputPhoneNumber.setErrorEnabled(true);
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
            inputPhoneNumber.setErrorEnabled(false);
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
            inputPhoneNumber.setErrorEnabled(false);
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
            inputPhoneNumber.setErrorEnabled(false);
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
            inputPhoneNumber.setErrorEnabled(false);
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
            inputPhoneNumber.setErrorEnabled(false);
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
            inputPhoneNumber.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputDoctorsExperience.setErrorEnabled(false);
        } else if (DOCTOR_DISPLAY_PROFILE_FILE_NAME == null) {
            Toast.makeText(getApplicationContext(), "Select a Profile Picture", Toast.LENGTH_LONG).show();
            imgvwDoctorProfile.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputPhoneNumber.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputDoctorsExperience.setErrorEnabled(false);
            inputDoctorsCharges.setErrorEnabled(false);
        } else if (DOCTOR_DISPLAY_PROFILE_URI == null)  {
            Toast.makeText(getApplicationContext(), "Select a Profile Picture", Toast.LENGTH_LONG).show();
            imgvwDoctorProfile.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPhoneNumber.setErrorEnabled(false);
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
            inputPhoneNumber.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputDoctorsExperience.setErrorEnabled(false);
            inputDoctorsCharges.setErrorEnabled(false);

            /* CREATE THE DOCTOR'S ACCOUNT */
            createAccount();
        }
    }

    /***** CREATE THE DOCTOR'S ACCOUNT *****/
    private void createAccount() {
        /* SHOW THE PROGRESS DIALOG WHILE UPLOADING THE IMAGE **/
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait while we update your account....");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();

        auth.createUserWithEmailAndPassword(DOCTOR_EMAIL, PASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())   {
                    /* GET THE NEW USERS INSTANCE */
                    user = auth.getCurrentUser();

                    if (user != null)   {
                        DOCTOR_EMAIL = user.getEmail();
                        /* UPLOAD THE DOCTOR'S DISPLAY PROFILE */
                        uploadProfile();
                    }
                } else {
                    dialog.dismiss();
                    Toast.makeText(
                            getApplicationContext(),
                            "There was a problem creating your new account. Please try again by clicking the Save button.",
                            Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(ClaimSignUpActivity.this, "EXCEPTION: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /***** UPLOAD THE DOCTOR'S DISPLAY PROFILE *****/
    private void uploadProfile() {
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//        StorageReference refStorage = storageReference.child("Doctor Profiles").child(DOCTOR_DISPLAY_PROFILE_FILE_NAME);
//        refStorage.putFile(DOCTOR_DISPLAY_PROFILE_URI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Uri downloadURL = taskSnapshot.getDownloadUrl();
//                DOCTOR_DISPLAY_PROFILE = String.valueOf(downloadURL);
//                if (DOCTOR_DISPLAY_PROFILE != null)    {
//                    /* UPDATE THE DOCTOR'S ACCOUNT */
//                    updateAccount();
//                } else {
//                    dialog.dismiss();
//                    Toast.makeText(
//                            getApplicationContext(),
//                            "There was a problem creating your new account. Please try again by clicking the Save button.",
//                            Toast.LENGTH_LONG).show();
//                }
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
////                Log.e("UPLOAD EXCEPTION", e.toString());
////                Crashlytics.logException(e);
//            }
//        });
    }

    /***** UPDATE THE DOCTOR'S ACCOUNT *****/
    private void updateAccount() {
        SignUpAPI api = ZenApiClient.getClient().create(SignUpAPI.class);
        Call<AccountData> call = api.updateDoctor(
                DOCTOR_ID, user.getUid(),
                DOCTOR_PREFIX, DOCTOR_NAME, DOCTOR_EMAIL,
                DOCTOR_PHONE_PREFIX, DOCTOR_PHONE_NUMBER, DOCTOR_ADDRESS,
                COUNTRY_ID, STATE_ID, CITY_ID,
                DOCTOR_GENDER, DOCTOR_SUMMARY, DOCTOR_EXPERIENCE, DOCTOR_CHARGES, DOCTOR_DISPLAY_PROFILE,
                "True", DOCTOR_TOKEN);
        call.enqueue(new Callback<AccountData>() {
            @Override
            public void onResponse(Call<AccountData> call, Response<AccountData> response) {
                if (response.isSuccessful())    {
                    /* CREATE THE DEFAULT SUBSCRIPTION RECORD */
                    createSubscriptionRecord(DOCTOR_ID);
                } else {
                    /* SET THE ERROR MESSAGE **/
                    strErrorMessage = "There was en error signing you up. Please submit your details again.";
                    dialog.dismiss();

                    /* DELETE THE USER **/
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())    {
                                /* SHOW THE ERROR MESSAGE **/
                                new MaterialDialog.Builder(ClaimSignUpActivity.this)
                                        .title("Registration Failed!")
                                        .content(strErrorMessage)
                                        .positiveText("OKAY")
                                        .theme(Theme.LIGHT)
                                        .icon(ContextCompat.getDrawable(ClaimSignUpActivity.this, R.drawable.ic_info_outline_black_24dp))
                                        .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                        .show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<AccountData> call, Throwable t) {
//                Log.e("SIGN UP FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /***** CREATE THE DEFAULT SUBSCRIPTION RECORD *****/
    private void createSubscriptionRecord(String doctorID) {
        /* GET THE START AND END DATES */
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        long START_DATE = today.getTime() / 1000;
        calendar.add(Calendar.YEAR, 1);
        Date nextYear = calendar.getTime();
        long END_DATE = nextYear.getTime() / 1000;

        SubscriptionAPI api = ZenApiClient.getClient().create(SubscriptionAPI.class);
        Call<Subscription> call = api.newDoctorSubscription(
                doctorID,
                "1",
                String.valueOf(START_DATE),
                String.valueOf(END_DATE));
        call.enqueue(new Callback<Subscription>() {
            @Override
            public void onResponse(Call<Subscription> call, Response<Subscription> response) {
                if (response.isSuccessful())    {
                    /* SET THE APPROVED STATUS TO "YES" */
                    getApp().setClaimApproved("Yes");

                    dialog.dismiss();
                    auth.signOut();
                    Intent intent = new Intent(ClaimSignUpActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Subscription> call, Throwable t) {

            }
        });
    }

    /***** VALIDATE EMAIL SYNTAX / FORMAT *****/
    private static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /***** CHECK STORAGE PERMISSION *****/
    private void checkStoragePermission() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(ClaimSignUpActivity.this,
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
                                        ClaimSignUpActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE_CONSTANT);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ACCESS_STORAGE_CONSTANT);
            }
        } else {
            final BottomSheetDialog sheetDialog = new BottomSheetDialog(ClaimSignUpActivity.this);
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
                    EasyImage.openGallery(ClaimSignUpActivity.this, 0);
                }
            });

            /* SELECT A CAMERA IMAGE */
            linlaCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sheetDialog.dismiss();
                    EasyImage.openCamera(ClaimSignUpActivity.this, 0);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_STORAGE_CONSTANT)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                final BottomSheetDialog sheetDialog = new BottomSheetDialog(ClaimSignUpActivity.this);
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
                        EasyImage.openGallery(ClaimSignUpActivity.this, 0);
                    }
                });

                /* SELECT A CAMERA IMAGE */
                linlaCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheetDialog.dismiss();
                        EasyImage.openCamera(ClaimSignUpActivity.this, 0);
                    }
                });
            }
        }
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
            imgvwDoctorProfile.setImageBitmap(bitmap);
            imgvwDoctorProfile.setScaleType(ImageView.ScaleType.CENTER_CROP);

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
//                Log.e("URI", String.valueOf(DOCTOR_DISPLAY_PROFILE_URI));
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
}