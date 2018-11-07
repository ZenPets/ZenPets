package co.zenpets.doctors.creator.doctor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.TypefaceSpan;
import co.zenpets.doctors.utils.adapters.doctors.modules.EducationSelectorAdapter;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.models.doctors.modules.Qualification;
import co.zenpets.doctors.utils.models.doctors.modules.QualificationsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EducationCreator extends AppCompatActivity {

    /** THE INCOMING DOCTOR ID **/
    private String DOCTOR_ID = null;

    /** DATA TYPES TO HOLD THE ENTERED DATA **/
    private String EDUCATION_NAME = null;
    private String COLLEGE_NAME = null;
    private String EDUCATION_YEAR = null;

    /** BOOLEAN INSTANCE TO CHECK IF NEW RECORD WAS SUCCESSFULLY CREATED **/
    boolean blnSuccess = false;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.spnEducation) AppCompatSpinner spnEducation;
    @BindView(R.id.inputCollege) TextInputLayout inputCollege;
    @BindView(R.id.edtCollege) AppCompatEditText edtCollege;
    @BindView(R.id.spnYear) AppCompatSpinner spnYear;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.education_creator);
        ButterKnife.bind(this);

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* POPULATE THE EDUCATIONAL QUALIFICATIONS SPINNER */
        String[] strDegrees = getResources().getStringArray(R.array.doctor_education);
        final List<String> arrEducation;
        arrEducation = Arrays.asList(strDegrees);
        spnEducation.setAdapter(new EducationSelectorAdapter(
                EducationCreator.this,
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
        final ArrayList<String> years = new ArrayList<>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i >= 1900; i--) {
            years.add(Integer.toString(i));
        }
        spnYear.setAdapter(new EducationSelectorAdapter(
                EducationCreator.this,
                R.layout.custom_spinner_row,
                years));

        /* CHANGE THE EDUCATIONAL QUALIFICATION YEAR */
        spnYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EDUCATION_YEAR = years.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
            /* SAVE THE RECORD */
            saveEducationRecord();
        }
    }

    /***** SAVE THE RECORD *****/
    private void saveEducationRecord() {
        QualificationsAPI apiInterface = ZenApiClient.getClient().create(QualificationsAPI.class);
        Call<Qualification> call = apiInterface.newDoctorEducation(
                DOCTOR_ID, COLLEGE_NAME, EDUCATION_NAME, EDUCATION_YEAR);
        call.enqueue(new Callback<Qualification>() {
            @Override
            public void onResponse(Call<Qualification> call, Response<Qualification> response) {
                if (response.isSuccessful())    {
                    /* FINISH THE ACTIVITY */
                    Toast.makeText(getApplicationContext(), "Successfully added the Educational Qualification", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    //TODO: SHOW AN ERROR FOR POSTING NEW SERVICE
                }
            }

            @Override
            public void onFailure(Call<Qualification> call, Throwable t) {
//                Log.e("FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "New Qualification";
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
        MenuInflater inflater = new MenuInflater(EducationCreator.this);
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

    /** GET THE INCOMING DATA **/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("DOCTOR_ID"))    {
            DOCTOR_ID = bundle.getString("DOCTOR_ID");
            if (TextUtils.isEmpty(DOCTOR_ID))   {
                Toast.makeText(getApplicationContext(), "Failed to get required information", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required information", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}