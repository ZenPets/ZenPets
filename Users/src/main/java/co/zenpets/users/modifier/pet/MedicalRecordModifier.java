package co.zenpets.users.modifier.pet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import co.zenpets.users.R;
import co.zenpets.users.utils.adapters.pet.records.RecordTypesAdapter;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.pets.records.MedicalRecord;
import co.zenpets.users.utils.models.pets.records.MedicalRecordsAPI;
import co.zenpets.users.utils.models.pets.records.RecordType;
import co.zenpets.users.utils.models.pets.records.RecordTypes;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicalRecordModifier extends AppCompatActivity {

    /** THE INCOMING MEDICAL RECORD ID **/
    private String MEDICAL_RECORD_ID = null;

    /** THE RECORD TYPES ARRAY LIST INSTANCE **/
    private ArrayList<RecordType> arrTypes = new ArrayList<>();

    /** DATA TYPES TO HOLD THE COLLECTED DATA **/
    private String RECORD_TYPE_ID = null;
    private String RECORD_TYPE_NAME = null;
    private String USER_ID = null;
    private String PET_ID = null;
    private String RECORD_NOTES = null;
    private String RECORD_DATE = null;

    /** A PROGRESS DIALOG **/
    private ProgressDialog dialog;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.spnRecordType) AppCompatSpinner spnRecordType;
    @BindView(R.id.inputRecordNotes) TextInputLayout inputRecordNotes;
    @BindView(R.id.edtRecordNotes) TextInputEditText edtRecordNotes;
    @BindView(R.id.txtRecordDate) AppCompatTextView txtRecordDate;

    /** MODIFY THE RECORD DATE **/
    @OnClick(R.id.btnRecordDate) void changeDate()  {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int month, int date) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, date);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                /* FOR THE DATABASE ONLY !!!! */
                RECORD_DATE = sdf.format(cal.getTime());

                /* FOR DISPLAY ONLY !!!! */
                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
                String selectedDate = dateFormat.format(cal.getTime());
                txtRecordDate.setText(selectedDate);
            }
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show(getFragmentManager(), "DatePickerDialog");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medical_record_modifier);
        ButterKnife.bind(this);

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* SELECT A RECORD TYPE */
        spnRecordType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* GET THE SELECTED RECORD TYPE ID AND NAME */
                RECORD_TYPE_ID = arrTypes.get(position).getRecordTypeID();
                RECORD_TYPE_NAME = arrTypes.get(position).getRecordTypeName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /***** FETCH THE RECORD DETAILS *****/
    private void fetchDetails() {
        MedicalRecordsAPI api = ZenApiClient.getClient().create(MedicalRecordsAPI.class);
        Call<MedicalRecord> call = api.fetchRecordDetails(MEDICAL_RECORD_ID);
        call.enqueue(new Callback<MedicalRecord>() {
            @Override
            public void onResponse(Call<MedicalRecord> call, Response<MedicalRecord> response) {
                MedicalRecord record = response.body();
                if (record != null) {

                    /* GET THE RECORD TYPE ID AND NAME */
                    RECORD_TYPE_ID = record.getRecordTypeID();
                    RECORD_TYPE_NAME = record.getRecordTypeName();
                    int intProblemPosition = getProblemIndex(arrTypes, RECORD_TYPE_ID);
                    spnRecordType.setSelection(intProblemPosition);

                    /* GET THE USER ID */
                    USER_ID = record.getUserID();

                    /* GET THE PET ID */
                    PET_ID = record.getPetID();

                    /* GET THE RECORD NOTES */
                    String RECORD_NOTES = record.getMedicalRecordNotes();
                    if (RECORD_NOTES != null && !RECORD_NOTES.equalsIgnoreCase("null"))
                        edtRecordNotes.setText(RECORD_NOTES);

                    /* GET THE RECORD DATE */
                    if (record.getMedicalRecordDate() != null)  {
                        RECORD_DATE = record.getMedicalRecordDate();
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                        try {
                            Date date = inputFormat.parse(RECORD_DATE);
                            String strDate = outputFormat.format(date);
                            txtRecordDate.setText(strDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<MedicalRecord> call, Throwable t) {
//                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("RECORD_ID"))   {
            MEDICAL_RECORD_ID = bundle.getString("RECORD_ID");
            if (MEDICAL_RECORD_ID != null)  {
                /* FETCH THE RECORD TYPES */
                fetchRecordTypes();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required data...", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required data...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** FETCH THE RECORD TYPES *****/
    private void fetchRecordTypes() {
        MedicalRecordsAPI api = ZenApiClient.getClient().create(MedicalRecordsAPI.class);
        Call<RecordTypes> call = api.allRecordTypes();
        call.enqueue(new Callback<RecordTypes>() {
            @Override
            public void onResponse(Call<RecordTypes> call, Response<RecordTypes> response) {
                arrTypes = response.body().getRecords();

                /* INSTANTIATE THE RECORD TYPES ADAPTER */
                RecordTypesAdapter adapter = new RecordTypesAdapter(MedicalRecordModifier.this, arrTypes);

                /* SET THE ADAPTER TO THE PET TYPES SPINNER */
                spnRecordType.setAdapter(adapter);

                /* FETCH THE MEDICAL RECORD DETAILS */
                fetchDetails();
            }

            @Override
            public void onFailure(Call<RecordTypes> call, Throwable t) {
//                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Update Record";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(strTitle);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(MedicalRecordModifier.this);
        inflater.inflate(R.menu.activity_save_cancel, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuSave:
                /* VALIDATE THE RECORD DETAILS */
                validateRecordDetails();
                break;
            case R.id.menuCancel:
                /* CANCEL CATEGORY CREATION */
                finish();
                break;
            default:
                break;
        }
        return false;
    }

    /***** VALIDATE THE RECORD DETAILS *****/
    private void validateRecordDetails() {
        /* HIDE THE KEYBOARD */
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(edtRecordNotes.getWindowToken(), 0);

        /* GET THE RECORD NOTES */
        if (TextUtils.isEmpty(edtRecordNotes.getText().toString()))    {
            RECORD_NOTES = "Null";
        } else {
            RECORD_NOTES = edtRecordNotes.getText().toString();
        }

        /* SHOW THE PROGRESS AND UPDATE THE MEDICAL RECORD */
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait while we publish the Medical Record...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();
        MedicalRecordsAPI api = ZenApiClient.getClient().create(MedicalRecordsAPI.class);
        Call<MedicalRecord> call = api.updateMedicalRecord(
                MEDICAL_RECORD_ID, RECORD_TYPE_ID, USER_ID, PET_ID, RECORD_NOTES, RECORD_DATE
        );
        call.enqueue(new Callback<MedicalRecord>() {
            @Override
            public void onResponse(Call<MedicalRecord> call, Response<MedicalRecord> response) {
                if (response.isSuccessful())    {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Successfully updated medical record", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "There was an error updating the record. Please try again", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<MedicalRecord> call, Throwable t) {
//                Log.e("EXCEPTION", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE PROBLEM POSITION *****/
    private int getProblemIndex(ArrayList<RecordType> array, String problemID) {
        int index = 0;
        for (int i =0; i < array.size(); i++) {
            if (array.get(i).getRecordTypeID().equalsIgnoreCase(problemID))   {
                index = i;
                break;
            }
        }
        return index;
    }
}