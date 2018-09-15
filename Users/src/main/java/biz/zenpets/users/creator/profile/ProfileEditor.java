package biz.zenpets.users.creator.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.landing.LandingActivity;
import biz.zenpets.users.landing.NewLandingActivity;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.adapters.location.CitiesAdapter;
import biz.zenpets.users.utils.adapters.location.StatesAdapter;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.location.Cities;
import biz.zenpets.users.utils.models.location.City;
import biz.zenpets.users.utils.models.location.LocationsAPI;
import biz.zenpets.users.utils.models.location.State;
import biz.zenpets.users.utils.models.location.States;
import biz.zenpets.users.utils.models.user.UserData;
import biz.zenpets.users.utils.models.user.UsersAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileEditor extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** STRINGS TO HOLD THE USER DETAILS **/
    private String USER_AUTH_ID = null;
    private String USER_DISPLAY_NAME = null;
    private String USER_EMAIL = null;
    private String USER_PROFILE = null;
    private String USER_PHONE_NUMBER = null;
    private String USER_GENDER = "Male";
    private String USER_PROFILE_STATUS = null;

    /** THE SELECTED COUNTRY, STATE AND CITY ID **/
    private final String COUNTRY_ID = "51";
    private String STATE_ID = null;
    private String CITY_ID = null;

    /* A PROGRESS DIALOG INSTANCE */
    private ProgressDialog progressDialog;

    /** THE STATES ADAPTER AND ARRAY LIST **/
    private ArrayList<State> arrStates = new ArrayList<>();

    /** CITIES ADAPTER AND ARRAY LIST **/
    private ArrayList<City> arrCities = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwProfilePicture) SimpleDraweeView imgvwProfilePicture;
    @BindView(R.id.edtDisplayName) AppCompatEditText edtDisplayName;
    @BindView(R.id.txtEmailAddress) AppCompatTextView txtEmailAddress;
    @BindView(R.id.inputPhoneNumber) TextInputLayout inputPhoneNumber;
    @BindView(R.id.edtPhoneNumber) AppCompatEditText edtPhoneNumber;
//    @BindView(R.id.spnCountry) AppCompatSpinner spnCountry;
    @BindView(R.id.spnState) AppCompatSpinner spnState;
    @BindView(R.id.spnCity) AppCompatSpinner spnCity;
    @BindView(R.id.rdgGender) RadioGroup rdgGender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_editor);
        ButterKnife.bind(this);

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* GET THE FIREBASE USER INSTANCE */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)   {
            /* GET THE USER'S AUTH ID */
            USER_AUTH_ID = user.getUid();
            USER_DISPLAY_NAME = user.getDisplayName();
            USER_EMAIL = user.getEmail();
            if (user.getProviderData().get(1).getProviderId().equalsIgnoreCase("google.com"))   {
                USER_PROFILE = String.valueOf(user.getProviderData().get(1).getPhotoUrl()) + "?sz=600";
            } else if (user.getProviderData().get(1).getProviderId().equalsIgnoreCase("facebook.com")){
                USER_PROFILE = "https://graph.facebook.com/" + user.getProviderData().get(1).getUid() + "/picture?width=4000";
            }

            /* SET THE USER'S DETAILS */
            if (USER_DISPLAY_NAME != null)  {
                edtDisplayName.setText(USER_DISPLAY_NAME);
            }

            /* SET THE USER'S EMAIL ADDRESS */
            if (USER_EMAIL != null) {
                txtEmailAddress.setText(USER_EMAIL);
            }

            /* SET THE USER'S DISPLAY PROFILE */
            if (USER_PROFILE != null)   {
                Uri uri = Uri.parse(USER_PROFILE);
                imgvwProfilePicture.setImageURI(uri);
            } else {
                ImageRequest request = ImageRequestBuilder
                        .newBuilderWithResourceId(R.drawable.ic_person_black_24dp)
                        .build();
                imgvwProfilePicture.setImageURI(request.getSourceUri());
            }
        }

        /* FETCH THE LIST OF STATES */
        fetchStates();

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

    /***** FETCH THE LIST OF STATES *****/
    private void fetchStates() {
        LocationsAPI api = ZenApiClient.getClient().create(LocationsAPI.class);
        Call<States> call = api.allStates(COUNTRY_ID);
        call.enqueue(new Callback<States>() {
            @Override
            public void onResponse(Call<States> call, Response<States> response) {
                arrStates = response.body().getStates();

                /* SET THE ADAPTER TO THE STATES SPINNER */
                spnState.setAdapter(new StatesAdapter(ProfileEditor.this, arrStates));
            }

            @Override
            public void onFailure(Call<States> call, Throwable t) {
//                Log.e("STATES FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE LIST OF CITIES *****/
    private void fetchCities() {
        LocationsAPI api = ZenApiClient.getClient().create(LocationsAPI.class);
        Call<Cities> call = api.allCities(STATE_ID);
        call.enqueue(new Callback<Cities>() {
            @Override
            public void onResponse(Call<Cities> call, Response<Cities> response) {
                arrCities = response.body().getCities();

                /* SET THE ADAPTER TO THE CITIES SPINNER */
                spnCity.setAdapter(new CitiesAdapter(ProfileEditor.this, arrCities));
            }

            @Override
            public void onFailure(Call<Cities> call, Throwable t) {
//                Log.e("CITIES FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Complete Your Profile";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(strTitle);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(ProfileEditor.this);
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
            imm.hideSoftInputFromWindow(edtDisplayName.getWindowToken(), 0);
        }

        /* COLLECT THE INFORMATION */
        USER_PHONE_NUMBER = edtPhoneNumber.getText().toString().trim();

        /* SET THE USER PROFILE STATUS TO "COMPLETE" */
        USER_PROFILE_STATUS = "Complete";

        /* VALIDATE THE PHONE NUMBER */
        if (TextUtils.isEmpty(USER_PHONE_NUMBER))   {
            inputPhoneNumber.setErrorEnabled(true);
            inputPhoneNumber.setError("Please provide your Mobile Number");
            inputPhoneNumber.requestFocus();
        } else {
            inputPhoneNumber.setErrorEnabled(false);
            /* CREATE THE USER'S PROFILE */
            createUserProfile();
        }
    }

    /***** CREATE THE USER'S PROFILE *****/
    private void createUserProfile() {
        /* INSTANTIATE THE PROGRESS DIALOG INSTANCE */
        progressDialog = new ProgressDialog(ProfileEditor.this);
        progressDialog.setMessage("Please wait while we create your Profile....");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        UsersAPI api = ZenApiClient.getClient().create(UsersAPI.class);
        Call<UserData> call = api.register(
                USER_AUTH_ID, USER_DISPLAY_NAME, USER_EMAIL,
                USER_PROFILE, "91", USER_PHONE_NUMBER, USER_GENDER,
                "51", STATE_ID, CITY_ID, USER_PROFILE_STATUS);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                /* DISMISS THE DIALOG  */
                progressDialog.dismiss();

                /* PROCESS THE RESULT */
                if (response.isSuccessful())    {
                    Toast.makeText(getApplicationContext(), "Your profile was created successfully", Toast.LENGTH_SHORT).show();
                    getApp().setUserID(response.body().getUserID());
                    getApp().setProfileStatus("Complete");
                    getApp().setUserID(USER_AUTH_ID);
                    Intent intent = new Intent(ProfileEditor.this, NewLandingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    /* SHOW THE ERROR MESSAGE */
                    new MaterialDialog.Builder(ProfileEditor.this)
                            .title("Registration Failed!")
                            .content("There was a problem creating your Profile. Please try again...")
                            .positiveText("OKAY")
                            .theme(Theme.LIGHT)
                            .icon(ContextCompat.getDrawable(ProfileEditor.this, R.drawable.ic_info_outline_black_24dp))
                            .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
//                Log.e("REGISTER FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }
}