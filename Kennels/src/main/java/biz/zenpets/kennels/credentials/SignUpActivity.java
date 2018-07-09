package biz.zenpets.kennels.credentials;

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
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.ArrayList;
import java.util.List;

import biz.zenpets.kennels.R;
import biz.zenpets.kennels.landing.NewLandingActivity;
import biz.zenpets.kennels.utils.TypefaceSpan;
import biz.zenpets.kennels.utils.adapters.location.CitiesAdapter;
import biz.zenpets.kennels.utils.adapters.location.StatesAdapter;
import biz.zenpets.kennels.utils.legal.PrivacyPolicyActivity;
import biz.zenpets.kennels.utils.legal.SellerAgreementActivity;
import biz.zenpets.kennels.utils.models.account.Account;
import biz.zenpets.kennels.utils.models.account.AccountsAPI;
import biz.zenpets.kennels.utils.models.helpers.ZenApiClient;
import biz.zenpets.kennels.utils.models.location.CitiesData;
import biz.zenpets.kennels.utils.models.location.CityData;
import biz.zenpets.kennels.utils.models.location.LocationAPI;
import biz.zenpets.kennels.utils.models.location.StateData;
import biz.zenpets.kennels.utils.models.location.StatesData;
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

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int ACCESS_STORAGE_CONSTANT = 201;

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog progressDialog;

    /** STRING TO HOLD THE ERROR MESSAGE, IF ANY **/
    private String strErrorMessage = null;

    /** THE COUNTRY, STATES AND CITIES ARRAY LISTS **/
    private ArrayList<StateData> arrStates = new ArrayList<>();
    private ArrayList<CityData> arrCities = new ArrayList<>();

    /** STRINGS TO HOLD THE KENNEL OWNER'S SELECTIONS **/
    String KENNEL_OWNER_NAME = null;
    String KENNEL_OWNER_EMAIL_ADDRESS = null;
    String KENNEL_OWNER_PASSWORD = null;
    String KENNEL_OWNER_CONFIRM_PASSWORD = null;
    String KENNEL_OWNER_PHONE_PREFIX = null;
    String KENNEL_OWNER_PHONE_NUMBER = null;
    String KENNEL_OWNER_MAILING_ADDRESS = null;
    String KENNEL_OWNER_PIN_CODE = null;
    private final String COUNTRY_ID = "51";
    private String STATE_ID = null;
    private String CITY_ID = null;
    private Uri KENNEL_OWNER_DISPLAY_PROFILE_URI = null;
    String KENNEL_OWNER_DISPLAY_PROFILE_FILE_NAME = null;
    private String KENNEL_OWNER_DISPLAY_PROFILE = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
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
    @BindView(R.id.inputPinCode) TextInputLayout inputPinCode;
    @BindView(R.id.edtPinCode) TextInputEditText edtPinCode;
    @BindView(R.id.spnState) AppCompatSpinner spnState;
    @BindView(R.id.spnCity) AppCompatSpinner spnCity;
    @BindView(R.id.imgvwProfilePicture) AppCompatImageView imgvwProfilePicture;
    @BindView(R.id.txtTermsOfService) TextView txtTermsOfService;

    /** SELECT THE PROFILE PICTURE **/
    @OnClick(R.id.imgvwProfilePicture) void selectProfilePicture() {
        /* CHECK STORAGE PERMISSION */
        checkStoragePermission();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        ButterKnife.bind(this);

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
                Crashlytics.logException(t);
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
                Crashlytics.logException(t);

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
            imm.hideSoftInputFromWindow(edtFullName.getWindowToken(), 0);
        }

        /* COLLECT THE NECESSARY DATA **/
        KENNEL_OWNER_NAME = edtFullName.getText().toString().trim();
        KENNEL_OWNER_EMAIL_ADDRESS = edtEmailAddress.getText().toString().trim();
        KENNEL_OWNER_PASSWORD = edtPassword.getText().toString().trim();
        KENNEL_OWNER_CONFIRM_PASSWORD = edtConfirmPassword.getText().toString().trim();
        KENNEL_OWNER_PHONE_PREFIX = "91";
        KENNEL_OWNER_PHONE_NUMBER = edtPhoneNumber.getText().toString().trim();
        KENNEL_OWNER_MAILING_ADDRESS = edtAddress.getText().toString().trim();
        KENNEL_OWNER_PIN_CODE = edtPinCode.getText().toString();

        if (!TextUtils.isEmpty(KENNEL_OWNER_NAME) && !TextUtils.isEmpty(KENNEL_OWNER_EMAIL_ADDRESS))  {
            KENNEL_OWNER_DISPLAY_PROFILE_FILE_NAME = KENNEL_OWNER_NAME.replaceAll(" ", "_").toLowerCase().trim() + "_" + KENNEL_OWNER_EMAIL_ADDRESS;
        } else {
            KENNEL_OWNER_DISPLAY_PROFILE_FILE_NAME = null;
        }
        boolean blnValidEmail = isValidEmail(KENNEL_OWNER_EMAIL_ADDRESS);

        /* VALIDATE THE DATA **/
        if (TextUtils.isEmpty(KENNEL_OWNER_NAME)) {
            inputFullName.setErrorEnabled(true);
            inputFullName.setError("Provide your full name");
            inputFullName.requestFocus();
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_OWNER_EMAIL_ADDRESS)) {
            inputEmailAddress.setErrorEnabled(true);
            inputEmailAddress.setError("Provide your email address");
            inputEmailAddress.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
        } else if (!blnValidEmail)  {
            inputEmailAddress.setErrorEnabled(true);
            inputEmailAddress.setError("Provide a valid email address");
            inputEmailAddress.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_OWNER_PASSWORD)) {
            inputPassword.setErrorEnabled(true);
            inputPassword.setError("Choose a password");
            inputPassword.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (KENNEL_OWNER_PASSWORD.length() < 8) {
            inputPassword.setErrorEnabled(true);
            inputPassword.setError("Password has to be at least 8 characters");
            inputPassword.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_OWNER_CONFIRM_PASSWORD)) {
            inputConfirmPassword.setErrorEnabled(true);
            inputConfirmPassword.setError("Confirm your Password");
            inputConfirmPassword.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (KENNEL_OWNER_CONFIRM_PASSWORD.length() < 8) {
            inputConfirmPassword.setErrorEnabled(true);
            inputConfirmPassword.setError("Password has to be at least 8 characters");
            inputConfirmPassword.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (!KENNEL_OWNER_CONFIRM_PASSWORD.equals(KENNEL_OWNER_PASSWORD))    {
            inputConfirmPassword.setErrorEnabled(true);
            inputConfirmPassword.setError("The passwords do not match");
            inputConfirmPassword.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_OWNER_PHONE_NUMBER)) {
            inputPhoneNumber.setErrorEnabled(true);
            inputPhoneNumber.setError("Enter your phone number");
            inputPhoneNumber.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_OWNER_MAILING_ADDRESS))  {
            inputAddress.setErrorEnabled(true);
            inputAddress.setError("Provide your mailing address");
            inputAddress.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputPhoneNumber.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_OWNER_PIN_CODE))  {
            inputPinCode.setErrorEnabled(true);
            inputPinCode.setError("Provide your Pin Code");
            inputPinCode.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputPhoneNumber.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
        } else if (COUNTRY_ID == null) {
            Toast.makeText(getApplicationContext(), "Select your Country", Toast.LENGTH_LONG).show();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputPhoneNumber.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (STATE_ID == null) {
            Toast.makeText(getApplicationContext(), "Select your State", Toast.LENGTH_LONG).show();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputPhoneNumber.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (CITY_ID == null) {
            Toast.makeText(getApplicationContext(), "Select your City", Toast.LENGTH_LONG).show();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputPhoneNumber.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (KENNEL_OWNER_DISPLAY_PROFILE_URI == null)  {
            Toast.makeText(getApplicationContext(), "Select a Profile Picture", Toast.LENGTH_LONG).show();
            imgvwProfilePicture.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputPhoneNumber.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else {
            /* DISABLE ALL ERRORS */
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(false);
            inputConfirmPassword.setErrorEnabled(false);
            inputPhoneNumber.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);

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

        auth.createUserWithEmailAndPassword(KENNEL_OWNER_EMAIL_ADDRESS, KENNEL_OWNER_PASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())   {
                    /* GET THE NEW USERS INSTANCE */
                    user = auth.getCurrentUser();

                    if (user != null)   {
                        KENNEL_OWNER_EMAIL_ADDRESS = user.getEmail();
                        /* CHECK IF THE KENNEL OWNER EXISTS IN THE PRIMARY DATABASE */
                        checkKennelOwnerExists();
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
                Crashlytics.logException(e);
            }
        });
    }

    /***** CHECK IF THE KENNEL OWNER EXISTS IN THE PRIMARY DATABASE *****/
    private void checkKennelOwnerExists() {
        AccountsAPI api = ZenApiClient.getClient().create(AccountsAPI.class);
        Call<Account> call = api.kennelOwnerExists(KENNEL_OWNER_EMAIL_ADDRESS);
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                String message = response.body().getMessage();
                if (message != null)    {
//                    Log.e("MESSAGE", message);
                    if (message.equalsIgnoreCase("Kennel Owner's account exists..."))   {
                        progressDialog.dismiss();
                        Intent intent = new Intent(SignUpActivity.this, NewLandingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else if (message.equalsIgnoreCase("Kennel Owner's account doesn't exist..."))    {
                        /* UPLOAD THE KENNEL OWNER'S DISPLAY PROFILE */
                        uploadDisplayProfile();
                    }
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
//                Log.e("EXISTS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** UPLOAD THE KENNEL OWNER'S DISPLAY PROFILE *****/
    private void uploadDisplayProfile() {
        KENNEL_OWNER_DISPLAY_PROFILE_FILE_NAME = KENNEL_OWNER_NAME.replaceAll(" ", "_").toLowerCase().trim() + "_" + user.getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference refStorage = storageReference.child("Kennel Profiles").child(KENNEL_OWNER_DISPLAY_PROFILE_FILE_NAME);
        refStorage.putFile(KENNEL_OWNER_DISPLAY_PROFILE_URI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadURL = taskSnapshot.getDownloadUrl();
//                Log.e("URL", String.valueOf(downloadURL));
                KENNEL_OWNER_DISPLAY_PROFILE = String.valueOf(downloadURL);
                if (KENNEL_OWNER_DISPLAY_PROFILE != null)    {
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Log.e("UPLOAD EXCEPTION", e.toString());
                Crashlytics.logException(e);
            }
        });
    }

    /***** CREATE THE NEW KENNEL OWNER'S ACCOUNT *****/
    private void createKennelOwnersAccount() {
        AccountsAPI api = ZenApiClient.getClient().create(AccountsAPI.class);
        Call<Account> call = api.registerKennelOwner(
                user.getUid(), KENNEL_OWNER_NAME, KENNEL_OWNER_DISPLAY_PROFILE, KENNEL_OWNER_EMAIL_ADDRESS,
                KENNEL_OWNER_PHONE_PREFIX, KENNEL_OWNER_PHONE_NUMBER, KENNEL_OWNER_MAILING_ADDRESS,
                KENNEL_OWNER_PIN_CODE, COUNTRY_ID, STATE_ID, CITY_ID);
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
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
            public void onFailure(Call<Account> call, Throwable t) {
//                Log.e("SIGN UP FAILURE", t.getMessage());
                Crashlytics.logException(t);
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
                KENNEL_OWNER_DISPLAY_PROFILE_URI = Uri.fromFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EasyImage.clearConfiguration(this);
    }
}