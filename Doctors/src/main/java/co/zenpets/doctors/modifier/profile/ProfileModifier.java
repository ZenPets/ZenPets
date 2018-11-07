package co.zenpets.doctors.modifier.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
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
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.TypefaceSpan;
import co.zenpets.doctors.utils.adapters.doctors.DoctorPrefixAdapter;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.helpers.classes.location.CitySelectorActivity;
import co.zenpets.doctors.utils.helpers.classes.location.CountrySelectorActivity;
import co.zenpets.doctors.utils.helpers.classes.location.StateSelectorActivity;
import co.zenpets.doctors.utils.models.doctors.profile.DoctorProfileAPI;
import co.zenpets.doctors.utils.models.doctors.profile.DoctorProfileData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileModifier extends AppCompatActivity {

    /** THE INCOMING DOCTOR ID **/
    private String DOCTOR_ID = null;

    /** DATA TYPES TO HOLD THE PROFILE DETAILS **/
    private String DOCTOR_PREFIX = null;
    private String DOCTOR_NAME = null;
    private String DOCTOR_EMAIL = null;
    private String DOCTOR_PHONE = null;
    private String DOCTOR_ADDRESS = null;
    private String COUNTRY_ID = null;
    private String COUNTRY_NAME = null;
    private String STATE_ID = null;
    private String STATE_NAME = null;
    private String CITY_ID = null;
    private String CITY_NAME = null;
    private String DOCTOR_GENDER = null;
    private String DOCTOR_SUMMARY = null;
    private String DOCTOR_EXPERIENCE = null;
    private String DOCTOR_CHARGES = null;

    /** THE PREFIX ARRAY LIST **/
    private List<String> arrPrefix = new ArrayList<>();

    /** REQUEST CODES **/
    private final int REQUEST_COUNTRY = 101;
    private final int REQUEST_STATE = 102;
    private final int REQUEST_CITY = 103;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.spnPrefix) AppCompatSpinner spnPrefix;
    @BindView(R.id.inputFullName) TextInputLayout inputFullName;
    @BindView(R.id.edtFullName) TextInputEditText edtFullName;
    @BindView(R.id.inputEmailAddress) TextInputLayout inputEmailAddress;
    @BindView(R.id.edtEmailAddress) TextInputEditText edtEmailAddress;
    @BindView(R.id.inputPhoneNumber) TextInputLayout inputPhoneNumber;
    @BindView(R.id.edtPhoneNumber) TextInputEditText edtPhoneNumber;
    @BindView(R.id.inputAddress) TextInputLayout inputAddress;
    @BindView(R.id.edtAddress) TextInputEditText edtAddress;
    @BindView(R.id.txtCountryLabel) AppCompatTextView txtCountryLabel;
    @BindView(R.id.txtCountryName) AppCompatTextView txtCountryName;
    @BindView(R.id.txtStateLabel) AppCompatTextView txtStateLabel;
    @BindView(R.id.txtStateName) AppCompatTextView txtStateName;
    @BindView(R.id.txtCityLabel) AppCompatTextView txtCityLabel;
    @BindView(R.id.txtCityName) AppCompatTextView txtCityName;
    @BindView(R.id.rdgGender) RadioGroup rdgGender;
    @BindView(R.id.rdbtnMale) AppCompatRadioButton rdbtnMale;
    @BindView(R.id.rdbtnFemale) AppCompatRadioButton rdbtnFemale;
    @BindView(R.id.inputSummary) TextInputLayout inputSummary;
    @BindView(R.id.edtSummary) TextInputEditText edtSummary;
    @BindView(R.id.inputExperience) TextInputLayout inputExperience;
    @BindView(R.id.edtExperience) TextInputEditText edtExperience;
    @BindView(R.id.inputCharges) TextInputLayout inputCharges;
    @BindView(R.id.edtCharges) TextInputEditText edtCharges;

    /** CHANGE THE DOCTOR'S COUNTRY **/
    @OnClick(R.id.txtCountryLabel) void selectCountryLabel() {
        Intent intent = new Intent(ProfileModifier.this, CountrySelectorActivity.class);
        intent.putExtra("COUNTRY_ID", COUNTRY_ID);
        startActivityForResult(intent, REQUEST_COUNTRY);
    }
    @OnClick(R.id.txtCountryName) void selectCountry()  {
        Intent intent = new Intent(ProfileModifier.this, CountrySelectorActivity.class);
        intent.putExtra("COUNTRY_ID", COUNTRY_ID);
        startActivityForResult(intent, REQUEST_COUNTRY);
    }

    /** CHANGE THE DOCTOR'S STATE **/
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

    /** CHANGE THE DOCTOR'S CITY **/
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
        setContentView(R.layout.profile_modifier);
        ButterKnife.bind(this);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* POPULATE THE PREFIX SPINNER */
        String[] strServes = getResources().getStringArray(R.array.prefix);
        arrPrefix = Arrays.asList(strServes);
        spnPrefix.setAdapter(new DoctorPrefixAdapter(
                ProfileModifier.this,
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

        /* FETCH THE INCOMING DATA */
        fetchIncomingData();
    }

    /***** FETCH THE DOCTOR PROFILE DETAILS *****/
    private void fetchDoctorProfile() {
        DoctorProfileAPI api = ZenApiClient.getClient().create(DoctorProfileAPI.class);
        Call<DoctorProfileData> call = api.fetchDoctorProfile(DOCTOR_ID);
        call.enqueue(new Callback<DoctorProfileData>() {
            @Override
            public void onResponse(Call<DoctorProfileData> call, Response<DoctorProfileData> response) {
                DoctorProfileData data = response.body();
                if (data != null)   {

                    /* GET AND SET THE DOCTOR'S PREFIX */
                    DOCTOR_PREFIX = data.getDoctorPrefix();
                    int intPrefixPosition = getPrefixIndex(arrPrefix, DOCTOR_PREFIX);
                    spnPrefix.setSelection(intPrefixPosition);

                    /* GET AND SET THE DOCTOR'S NAME */
                    DOCTOR_NAME = data.getDoctorName();
                    edtFullName.setText(DOCTOR_NAME);

                    /* GET AND SET THE DOCTOR'S EMAIL */
                    DOCTOR_EMAIL = data.getDoctorEmail();
                    edtEmailAddress.setText(DOCTOR_EMAIL);

                    /* GET AND SET THE DOCTOR'S PHONE */
                    DOCTOR_PHONE = data.getDoctorPhoneNumber();
                    edtPhoneNumber.setText(DOCTOR_PHONE);

                    /* GET AND SET THE DOCTOR'S ADDRESS */
                    DOCTOR_ADDRESS = data.getDoctorAddress();
                    edtAddress.setText(DOCTOR_ADDRESS);

                    /* GET AND SET THE DOCTOR'S COUNTRY ID AND NAME */
                    COUNTRY_ID = data.getCountryID();
                    COUNTRY_NAME = data.getCountryName();
                    txtCountryName.setText(COUNTRY_NAME);

                    /* GET AND SET THE DOCTOR'S STATE ID AND NAME */
                    STATE_ID = data.getStateID();
                    STATE_NAME = data.getStateName();
                    txtStateName.setText(STATE_NAME);

                    /* GET AND SET THE DOCTOR'S CITY ID AND NAME */
                    CITY_ID = data.getCityID();
                    CITY_NAME = data.getCityName();
                    txtCityName.setText(CITY_NAME);

                    /* GET AND SET THE DOCTOR'S GENDER */
                    DOCTOR_GENDER = data.getDoctorGender();
                    if (DOCTOR_GENDER.equalsIgnoreCase("Male")) {
                        rdbtnMale.setChecked(true);
                        rdbtnFemale.setChecked(false);
                    } else {
                        rdbtnFemale.setChecked(true);
                        rdbtnMale.setChecked(false);
                    }

                    /* GET AND SET THE DOCTOR'S SUMMARY */
                    DOCTOR_SUMMARY = data.getDoctorSummary();
                    edtSummary.setText(DOCTOR_SUMMARY);

                    /* GET AND SET THE DOCTOR'S EXPERIENCE */
                    DOCTOR_EXPERIENCE = data.getDoctorExperience();
                    edtExperience.setText(DOCTOR_EXPERIENCE);

                    /* GET AND SET THE DOCTOR'S CHARGES */
                    DOCTOR_CHARGES = data.getDoctorCharges();
                    edtCharges.setText(DOCTOR_CHARGES);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<DoctorProfileData> call, Throwable t) {
//                Log.e("PROFILE FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /***** CHECK FOR ALL PROFILE DETAILS  *****/
    private void checkProfileDetails() {
        /* HIDE THE KEYBOARD **/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtFullName.getWindowToken(), 0);
        }

        /* COLLECT THE NECESSARY DATA **/
        DOCTOR_NAME = edtFullName.getText().toString().trim();
        DOCTOR_EMAIL = edtEmailAddress.getText().toString().trim();
        DOCTOR_PHONE = edtPhoneNumber.getText().toString().trim();
        DOCTOR_ADDRESS = edtAddress.getText().toString().trim();
        DOCTOR_SUMMARY = edtSummary.getText().toString().trim();
        DOCTOR_EXPERIENCE = edtExperience.getText().toString().trim();
        DOCTOR_CHARGES = edtCharges.getText().toString().trim();

        /* CHECK IF THE EMAIL ADDRESS IS VALID */
        boolean blnValidEmail = isValidEmail(DOCTOR_EMAIL);

        /* VALIDATE THE DATA **/
        if (TextUtils.isEmpty(DOCTOR_NAME)) {
            inputFullName.setErrorEnabled(true);
            inputFullName.setError("Provide your full name");
            inputFullName.requestFocus();
            inputEmailAddress.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputExperience.setErrorEnabled(false);
            inputCharges.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(DOCTOR_EMAIL)) {
            inputEmailAddress.setErrorEnabled(true);
            inputEmailAddress.setError("Provide your email address");
            inputEmailAddress.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputExperience.setErrorEnabled(false);
            inputCharges.setErrorEnabled(false);
        } else if (!blnValidEmail)  {
            inputEmailAddress.setErrorEnabled(true);
            inputEmailAddress.setError("Provide a valid email address");
            inputEmailAddress.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputExperience.setErrorEnabled(false);
            inputCharges.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(DOCTOR_PHONE)) {
            edtPhoneNumber.setError("Enter your phone number");
            edtPhoneNumber.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputExperience.setErrorEnabled(false);
            inputCharges.setErrorEnabled(false);
        } else if (COUNTRY_ID == null) {
            Toast.makeText(getApplicationContext(), "Select your Country", Toast.LENGTH_LONG).show();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputExperience.setErrorEnabled(false);
            inputCharges.setErrorEnabled(false);
        } else if (STATE_ID == null) {
            Toast.makeText(getApplicationContext(), "Select your State", Toast.LENGTH_LONG).show();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputExperience.setErrorEnabled(false);
            inputCharges.setErrorEnabled(false);
        } else if (CITY_ID == null) {
            Toast.makeText(getApplicationContext(), "Select your City", Toast.LENGTH_LONG).show();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputExperience.setErrorEnabled(false);
            inputCharges.setErrorEnabled(false);
        } else if (DOCTOR_SUMMARY.isEmpty())    {
            inputSummary.setErrorEnabled(true);
            inputSummary.setError("Tell us something about your practise");
            inputSummary.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputExperience.setErrorEnabled(false);
            inputCharges.setErrorEnabled(false);
        } else if (DOCTOR_EXPERIENCE.isEmpty()) {
            inputExperience.setErrorEnabled(true);
            inputExperience.setError("Provide your experience (in years)");
            inputExperience.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputCharges.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(DOCTOR_CHARGES))   {
            inputCharges.setErrorEnabled(true);
            inputCharges.setError("Please your minimum consultation charges");
            inputCharges.requestFocus();
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputExperience.setErrorEnabled(false);
        } else {
            /* DISABLE ALL ERRORS */
            inputFullName.setErrorEnabled(false);
            inputEmailAddress.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputSummary.setErrorEnabled(false);
            inputExperience.setErrorEnabled(false);
            inputCharges.setErrorEnabled(false);

            /* UPDATE THE DOCTOR'S PROFILE */
            updateProfile();
        }
    }

    /***** UPDATE THE DOCTOR'S PROFILE *****/
    private void updateProfile() {
        DoctorProfileAPI api = ZenApiClient.getClient().create(DoctorProfileAPI.class);
//        Call<AccountData> call = api.updateDoctorProfile(
//                DOCTOR_ID, DOCTOR_PREFIX, DOCTOR_NAME, DOCTOR_EMAIL, "91", DOCTOR_PHONE,
//                DOCTOR_ADDRESS, COUNTRY_ID, STATE_ID, CITY_ID,
//                DOCTOR_GENDER, DOCTOR_SUMMARY, DOCTOR_EXPERIENCE, DOCTOR_CHARGES
//        );
//        call.enqueue(new Callback<AccountData>() {
//            @Override
//            public void onResponse(Call<AccountData> call, Response<AccountData> response) {
//                if (response.isSuccessful())    {
//                    Intent success = new Intent();
//                    setResult(RESULT_OK, success);
//                    Toast.makeText(getApplicationContext(), "Updated successfully...", Toast.LENGTH_LONG).show();
//                    finish();
//                } else {
//                    Toast.makeText(getApplicationContext(), "Update failed...", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<AccountData> call, Throwable t) {
////                Crashlytics.logException(t);
////                Log.e("UPDATE FAILURE", t.getMessage());
//            }
//        });
    }

    /***** FETCH THE INCOMING DATA *****/
    private void fetchIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("DOCTOR_ID"))  {
            DOCTOR_ID = bundle.getString("DOCTOR_ID");
            if (DOCTOR_ID != null)  {
                /* FETCH THE DOCTOR PROFILE DETAILS */
                fetchDoctorProfile();
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
        String strTitle = "Update Profile";
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
        MenuInflater inflater = new MenuInflater(ProfileModifier.this);
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
                /* CHECK FOR ALL PROFILE DETAILS  */
                checkProfileDetails();
                break;
            case R.id.menuCancel:
                finish();
                break;
            default:
                break;
        }
        return false;
    }

    /***** GET THE PREFIX POSITION *****/
    private int getPrefixIndex(List<String> arrPrefix, String doctorPrefix) {
        int index = 0;
        for (int i =0; i < arrPrefix.size(); i++) {
            if (arrPrefix.get(i).equalsIgnoreCase(doctorPrefix))   {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)    {
            if (requestCode == REQUEST_COUNTRY) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    /* GET AND SET THE NEW COUNTRY */
                    COUNTRY_ID = bundle.getString("COUNTRY_ID");
                    COUNTRY_NAME = bundle.getString("COUNTRY_NAME");
                    txtCountryName.setText(COUNTRY_NAME);
                }
            } else if (requestCode == REQUEST_STATE)    {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    /* GET AND SET THE NEW STATE */
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

    /***** VALIDATE EMAIL SYNTAX / FORMAT *****/
    private static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}