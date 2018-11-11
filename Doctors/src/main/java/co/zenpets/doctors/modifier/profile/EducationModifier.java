package co.zenpets.doctors.modifier.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.TypefaceSpan;
import co.zenpets.doctors.utils.adapters.doctors.modules.EducationSelectorAdapter;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.models.doctors.modules.Qualification;
import co.zenpets.doctors.utils.models.doctors.modules.QualificationsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EducationModifier extends AppCompatActivity {

    /** THE INCOMING EDUCATION ID **/
    private String EDUCATION_ID = null;

    /** DATA TYPES TO HOLD THE ENTERED DATA **/
    private String DOCTOR_ID = null;
    private String EDUCATION_NAME = null;
    private String COLLEGE_NAME = null;
    private String EDUCATION_YEAR = null;

    /** THE YEARS ARRAY LIST **/
    private final ArrayList<String> arrYears = new ArrayList<>();

    /** THE EDUCATIONS LIST INSTANCE **/
    private List<String> arrEducation = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.spnEducation) AppCompatSpinner spnEducation;
    @BindView(R.id.inputCollege) TextInputLayout inputCollege;
    @BindView(R.id.edtCollege) TextInputEditText edtCollege;
    @BindView(R.id.spnYear) AppCompatSpinner spnYear;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.education_modifier);
        ButterKnife.bind(this);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* POPULATE THE EDUCATIONAL QUALIFICATIONS SPINNER */
        String[] strDegrees = getResources().getStringArray(R.array.doctor_education);
        arrEducation = Arrays.asList(strDegrees);
        spnEducation.setAdapter(new EducationSelectorAdapter(
                EducationModifier.this,
                R.layout.custom_spinner_row,
                arrEducation));

        /* CHANGE THE EDUCATIONAL QUALIFICATION */
        spnEducation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EDUCATION_NAME = arrEducation.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* POPULATE THE YEARS SPINNER */
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i >= 1900; i--) {
            arrYears.add(Integer.toString(i));
        }
        spnYear.setAdapter(new EducationSelectorAdapter(
                EducationModifier.this,
                R.layout.custom_spinner_row,
                arrYears));

        /* CHANGE THE EDUCATIONAL QUALIFICATION YEAR */
        spnYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EDUCATION_YEAR = arrYears.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* GET THE INCOMING EDUCATION ID */
        getIncomingData();
    }

    /***** FETCH EDUCATION DETAILS *****/
    private void fetchEducationDetails() {
        QualificationsAPI api = ZenApiClient.getClient().create(QualificationsAPI.class);
        Call<Qualification> call = api.fetchEducationDetails(EDUCATION_ID);
        call.enqueue(new Callback<Qualification>() {
            @Override
            public void onResponse(@NonNull Call<Qualification> call, @NonNull Response<Qualification> response) {
                Qualification data = response.body();
                if (data != null)   {
                    /* GET THE DOCTOR ID */
                    DOCTOR_ID = data.getDoctorID();

                    /* GET AND SET THE COLLEGE NAME */
                    COLLEGE_NAME = data.getDoctorCollegeName();
                    edtCollege.setText(COLLEGE_NAME);

                    /* GET AND SET THE EDUCATION NAME */
                    EDUCATION_NAME = data.getDoctorEducationName();
                    int intEducationPosition = getEducationIndex(arrEducation, EDUCATION_NAME);
                    spnEducation.setSelection(intEducationPosition);

                    /* GET AND SET THE EDUCATION YEAR */
                    EDUCATION_YEAR = data.getDoctorEducationYear();
                    int intYearPosition = getYearIndex(arrYears, EDUCATION_YEAR);
                    spnYear.setSelection(intYearPosition);
                } else {
                }
            }

            @Override
            public void onFailure(@NonNull Call<Qualification> call, @NonNull Throwable t) {
//                Log.e("FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE INCOMING EDUCATION ID *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("EDUCATION_ID"))   {
            EDUCATION_ID = bundle.getString("EDUCATION_ID");
            if (EDUCATION_ID != null)   {
                /* FETCH EDUCATION DETAILS */
                fetchEducationDetails();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** GET THE EDUCATION YEAR POSITION *****/
    private int getYearIndex(List<String> array, String educationYear) {
        int index = 0;
        for (int i =0; i < array.size(); i++) {
            if (array.get(i).equals(educationYear))   {
                index = i;
                break;
            }
        }
        return index;
    }

    private int getEducationIndex(List<String> array, String educationName) {
        int index = 0;
        for (int i =0; i < array.size(); i++) {
            if (array.get(i).equals(educationName))   {
                index = i;
                break;
            }
        }
        return index;
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    @SuppressWarnings("ConstantConditions")
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Update Qualification";
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
        MenuInflater inflater = new MenuInflater(EducationModifier.this);
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
                /* CHECK ALL QUALIFICATION DETAILS */
                checkQualifications();
                break;
            case R.id.menuCancel:
                finish();
                break;
            default:
                break;
        }
        return false;
    }

    /***** CHECK ALL QUALIFICATION DETAILS *****/
    private void checkQualifications() {
        /* HIDE THE KEYBOARD */
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtCollege.getWindowToken(), 0);
        }

        /* COLLECT ALL THE DATA */
        COLLEGE_NAME = edtCollege.getText().toString().trim();

        /* VALIDATE THE DATA */
        if (TextUtils.isEmpty(COLLEGE_NAME))    {
            inputCollege.setError("Please provide the college name");
            inputCollege.setErrorEnabled(true);
        } else {
            inputCollege.setErrorEnabled(false);
            /* UPDATE THE EDUCATION RECORD */
            updateEducationRecord();
        }
    }

    /***** UPDATE THE EDUCATION RECORD *****/
    private void updateEducationRecord() {
        QualificationsAPI apiInterface = ZenApiClient.getClient().create(QualificationsAPI.class);
        Call<Qualification> call = apiInterface.updateEducationDetails(
                EDUCATION_ID, DOCTOR_ID, COLLEGE_NAME, EDUCATION_NAME, EDUCATION_YEAR);
        call.enqueue(new Callback<Qualification>() {
            @Override
            public void onResponse(@NonNull Call<Qualification> call, @NonNull Response<Qualification> response) {
                if (response.isSuccessful())    {
                    /* FINISH THE ACTIVITY */
                    Toast.makeText(getApplicationContext(), "Successfully updated record...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    //TODO: SHOW AN ERROR FOR POSTING NEW SERVICE
                }
            }

            @Override
            public void onFailure(@NonNull Call<Qualification> call, @NonNull Throwable t) {
//                Log.e("FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }
}