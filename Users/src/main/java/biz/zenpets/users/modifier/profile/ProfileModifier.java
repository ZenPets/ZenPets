package biz.zenpets.users.modifier.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.helpers.classes.location.CitySelectorActivity;
import biz.zenpets.users.utils.helpers.classes.location.StateSelectorActivity;
import biz.zenpets.users.utils.models.user.UserData;
import biz.zenpets.users.utils.models.user.UsersAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileModifier extends AppCompatActivity {

    /** STRINGS TO HOLD THE USER DETAILS **/
    private String USER_AUTH_ID = null;
    private String USER_NAME = null;
    private String USER_EMAIL = null;
    private String USER_DISPLAY_PROFILE = null;
    private String USER_PHONE_NUMBER = null;
    private final String COUNTRY_ID = null;
//    String COUNTRY_NAME = null;
private String STATE_ID = null;
    private String STATE_NAME = null;
    private String CITY_ID = null;
    private String CITY_NAME = null;
    private String USER_GENDER = "Male";

    /** REQUEST CODES **/
//    private final int REQUEST_COUNTRY = 101;
    private final int REQUEST_STATE = 102;
    private final int REQUEST_CITY = 103;

    /* A PROGRESS DIALOG INSTANCE */
    private ProgressDialog progressDialog;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwProfilePicture) SimpleDraweeView imgvwProfilePicture;
    @BindView(R.id.inputUserName) TextInputLayout inputUserName;
    @BindView(R.id.edtUserName) TextInputEditText edtUserName;
    @BindView(R.id.inputUserEmail) TextInputLayout inputUserEmail;
    @BindView(R.id.edtUserEmail) TextInputEditText edtUserEmail;
    @BindView(R.id.inputPhoneNumber) TextInputLayout inputPhoneNumber;
    @BindView(R.id.edtPhoneNumber) TextInputEditText edtPhoneNumber;
//    @BindView(R.id.txtCountryName) AppCompatTextView txtCountryName;
    @BindView(R.id.txtStateName) AppCompatTextView txtStateName;
    @BindView(R.id.txtCityName) AppCompatTextView txtCityName;
    @BindView(R.id.rdgGender) RadioGroup rdgGender;
    @BindView(R.id.rdbtnMale) AppCompatRadioButton rdbtnMale;
    @BindView(R.id.rdbtnFemale) AppCompatRadioButton rdbtnFemale;

//    /** CHANGE THE USER'S COUNTRY **/
//    @OnClick(R.id.txtCountryLabel) void selectCountryLabel() {
//        Intent intent = new Intent(ProfileModifier.this, CountrySelectorActivity.class);
//        intent.putExtra("COUNTRY_ID", COUNTRY_ID);
//        startActivityForResult(intent, REQUEST_COUNTRY);
//    }
//    @OnClick(R.id.txtCountryName) void selectCountry()  {
//        Intent intent = new Intent(ProfileModifier.this, CountrySelectorActivity.class);
//        intent.putExtra("COUNTRY_ID", COUNTRY_ID);
//        startActivityForResult(intent, REQUEST_COUNTRY);
//    }

    /** CHANGE THE USER'S STATE **/
    @OnClick(R.id.txtStateLabel) void selectStateLabel() {
        Intent intent = new Intent(ProfileModifier.this, StateSelectorActivity.class);
        intent.putExtra("COUNTRY_ID", COUNTRY_ID);
        startActivityForResult(intent, REQUEST_STATE);
    }
    @OnClick(R.id.txtStateName) void selectState()  {
        Intent intent = new Intent(ProfileModifier.this, StateSelectorActivity.class);
        intent.putExtra("COUNTRY_ID", COUNTRY_ID);
        startActivityForResult(intent, REQUEST_STATE);
    }

    /** CHANGE THE USER'S CITY **/
    @OnClick(R.id.txtCityLabel) void selectCityLabel() {
        Intent intent = new Intent(ProfileModifier.this, CitySelectorActivity.class);
        intent.putExtra("STATE_ID", STATE_ID);
        startActivityForResult(intent, REQUEST_CITY);
    }
    @OnClick(R.id.txtCityName) void selectCity()  {
        Intent intent = new Intent(ProfileModifier.this, CitySelectorActivity.class);
        intent.putExtra("STATE_ID", STATE_ID);
        startActivityForResult(intent, REQUEST_CITY);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_modifier_new);
        ButterKnife.bind(this);

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* GET THE FIREBASE USER INSTANCE */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)   {
            /* FETCH THE USER DETAILS */
            fetchUserDetails(user);
        }

        /* SELECT THE USER'S GENDER */
        rdgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rdbtnMale:
                        /* SET THE GENDER TO MALE */
                        USER_GENDER = "Male";
                        break;
                    case R.id.rdbtnFemale:
                        /* SET THE GENDER TO FEMALE */
                        USER_GENDER = "Female";
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /***** FETCH THE USER DETAILS *****/
    private void fetchUserDetails(FirebaseUser user) {
        /* GET THE USER'S AUTH ID */
        USER_AUTH_ID = user.getUid();

        /* QUERY THE API */
        UsersAPI api = ZenApiClient.getClient().create(UsersAPI.class);
        Call<UserData> call = api.fetchProfile(USER_AUTH_ID);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                UserData data = response.body();
                if (data != null)   {
                    /* GET AND SET THE USER DISPLAY PROFILE */
                    if (data.getUserDisplayProfile() != null)   {
                        USER_DISPLAY_PROFILE = data.getUserDisplayProfile();
                        if (USER_DISPLAY_PROFILE != null)   {
                            Uri uri = Uri.parse(USER_DISPLAY_PROFILE);
                            imgvwProfilePicture.setImageURI(uri);
                        } else {
                            ImageRequest request = ImageRequestBuilder
                                    .newBuilderWithResourceId(R.drawable.ic_person_black_24dp)
                                    .build();
                            imgvwProfilePicture.setImageURI(request.getSourceUri());
                        }
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.ic_person_black_24dp)
                                .build();
                        imgvwProfilePicture.setImageURI(request.getSourceUri());
                    }

                    /* GET AND SET THE USER'S NAME */
                    if (data.getUserName() != null) {
                        USER_NAME = data.getUserName();
                        edtUserName.setText(USER_NAME);
                    } else {
                    }

                    /* GET AND SET THE USER'S EMAIL ADDRESS */
                    if (data.getUserEmail() != null)    {
                        USER_EMAIL = data.getUserEmail();
                        edtUserEmail.setText(USER_EMAIL);
                    } else {
                    }

                    /* GET AND SET THE USER'S PHONE NUMBER */
                    if (data.getUserPhoneNumber() != null)  {
                        USER_PHONE_NUMBER = data.getUserPhoneNumber();
                        edtPhoneNumber.setText(USER_PHONE_NUMBER);
                    } else {
                    }

//                    /* GET AND SET THE USER'S COUNTRY ID AND NAME */
//                    if (data.getCountryID() != null)    {
//                        COUNTRY_ID = data.getCountryID();
//                        COUNTRY_NAME = data.getCountryName();
//                        txtCountryName.setText(COUNTRY_NAME);
//                    } else {
//                        COUNTRY_ID = null;
//                        COUNTRY_NAME = null;
//                    }

                    /* GET AND SET THE USER'S STATE ID AND NAME */
                    if (data.getStateID() != null)  {
                        STATE_ID = data.getStateID();
                        STATE_NAME = data.getStateName();
                        txtStateName.setText(STATE_NAME);
                    } else {
                        STATE_ID = null;
                        STATE_NAME = null;
                    }

                    /* GET AND SET THE USER'S CITY ID AND NAME */
                    if (data.getCityID() != null)   {
                        CITY_ID = data.getCityID();
                        CITY_NAME = data.getCityName();
                        txtCityName.setText(CITY_NAME);
                    } else {
                        CITY_ID = null;
                        CITY_NAME = null;
                    }

                    /* GET AND SET THE USER'S GENDER */
                    USER_GENDER = data.getUserGender();
                    if (USER_GENDER.equalsIgnoreCase("Male")) {
                        rdbtnMale.setChecked(true);
                        rdbtnFemale.setChecked(false);
                    } else {
                        rdbtnFemale.setChecked(true);
                        rdbtnMale.setChecked(false);
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
//                Log.e("PROFILE FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Modify Profile";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(strTitle);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(ProfileModifier.this);
        inflater.inflate(R.menu.activity_profile_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuSave:
                /* UPDATE THE USER DETAILS */
                updateUserDetails();
                break;
            default:
                break;
        }
        return false;
    }

    /***** UPDATE THE USER DETAILS *****/
    private void updateUserDetails() {
        /* HIDE THE KEYBOARD */
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtUserName.getWindowToken(), 0);
        }

        /* COLLECT THE INFORMATION */
        USER_NAME = edtUserName.getText().toString().trim();
        USER_PHONE_NUMBER = edtPhoneNumber.getText().toString().trim();

        /* VALIDATE THE USER DETAILS */
        if (TextUtils.isEmpty(USER_NAME))   {
            inputUserName.setErrorEnabled(true);
            inputUserName.setError("Please enter your Display Name");
            inputUserName.requestFocus();
            inputPhoneNumber.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(USER_PHONE_NUMBER))   {
            inputPhoneNumber.setErrorEnabled(true);
            inputPhoneNumber.setError("Please provide your Mobile Number");
            inputPhoneNumber.requestFocus();
            inputUserName.setErrorEnabled(false);
        } else if (USER_PHONE_NUMBER.length() < 10 || USER_PHONE_NUMBER.length() > 10) {
            inputPhoneNumber.setErrorEnabled(true);
            inputPhoneNumber.setError("Enter a valid Mobile Number");
            inputPhoneNumber.requestFocus();
            inputUserName.setErrorEnabled(false);
        } else if (STATE_ID == null) {
            Toast.makeText(getApplicationContext(), "Please select a State", Toast.LENGTH_LONG).show();
        } else if (CITY_ID == null)  {
            Toast.makeText(getApplicationContext(), "Please select a City", Toast.LENGTH_LONG).show();
        } else {
            /* DISABLE THE ERRORS IF THEY WERE SHOWN */
            inputUserName.setErrorEnabled(false);
            inputPhoneNumber.setErrorEnabled(false);

            /* CREATE THE USER'S PROFILE */
            updateUserProfile();
        }
    }

    /***** UPDATE THE USER'S PROFILE *****/
    private void updateUserProfile() {
        /* INSTANTIATE THE PROGRESS DIALOG INSTANCE */
        progressDialog = new ProgressDialog(ProfileModifier.this);
        progressDialog.setMessage("Updating your profile...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        UsersAPI api = ZenApiClient.getClient().create(UsersAPI.class);
        Call<UserData> call = api.updateProfile(
                USER_EMAIL, USER_NAME, "91", USER_PHONE_NUMBER,
                USER_GENDER, "51", STATE_ID, CITY_ID
        );
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                /* DISMISS THE DIALOG  */
                progressDialog.dismiss();

                /* PROCESS THE RESULT */
                if (response.isSuccessful())    {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    /* SHOW THE ERROR MESSAGE */
                    new MaterialDialog.Builder(ProfileModifier.this)
                            .title("Update Failed!")
                            .content("There was a problem updating your Profile. Please try again...")
                            .positiveText("OKAY")
                            .theme(Theme.LIGHT)
                            .icon(ContextCompat.getDrawable(ProfileModifier.this, R.drawable.ic_info_outline_black_24dp))
                            .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
//                Log.e("UPDATE FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)    {
//            if (requestCode == REQUEST_COUNTRY) {
//                Bundle bundle = data.getExtras();
//                if (bundle != null) {
//                    /* GET AND SET THE NEW COUNTRY */
//                    COUNTRY_ID = bundle.getString("COUNTRY_ID");
//                    COUNTRY_NAME = bundle.getString("COUNTRY_NAME");
//                    txtCountryName.setText(COUNTRY_NAME);
//                }
//            } else if (requestCode == REQUEST_STATE)    {
//                Bundle bundle = data.getExtras();
//                if (bundle != null) {
//                    /* GET AND SET THE NEW STATE */
//                    STATE_ID = bundle.getString("STATE_ID");
//                    STATE_NAME = bundle.getString("STATE_NAME");
//                    txtStateName.setText(STATE_NAME);
//                }
//            } else if (requestCode == REQUEST_CITY)    {
//                Bundle bundle = data.getExtras();
//                if (bundle != null) {
//                    /* GET AND SET THE NEW CITY */
//                    CITY_ID = bundle.getString("CITY_ID");
//                    CITY_NAME = bundle.getString("CITY_NAME");
//                    txtCityName.setText(CITY_NAME);
//                }
//            }

            if (requestCode == REQUEST_STATE)    {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    /* GET AND SET THE NEW STATE */
                    String stateID = bundle.getString("STATE_ID");
                    if (!stateID.equalsIgnoreCase(STATE_ID)) {
                        txtCityName.setText("Tap here to choose a City");
                        CITY_ID = null;
                    }
                    STATE_ID = bundle.getString("STATE_ID");
                    STATE_NAME = bundle.getString("STATE_NAME");
                    txtStateName.setText(STATE_NAME);
                }
            } else if (requestCode == REQUEST_CITY)    {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    /* GET AND SET THE NEW CITY */
                    CITY_ID = bundle.getString("CITY_ID");
                    CITY_NAME = bundle.getString("CITY_NAME");
                    txtCityName.setText(CITY_NAME);
                }
            }
        }
    }
}