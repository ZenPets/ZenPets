package co.zenpets.users.modifier.pet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
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
import android.widget.CompoundButton;
import android.widget.LinearLayout;
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
import co.zenpets.users.utils.adapters.pet.vaccinations.VaccinesAdapter;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.pets.vaccination.Vaccination;
import co.zenpets.users.utils.models.pets.vaccination.VaccinationsAPI;
import co.zenpets.users.utils.models.pets.vaccines.Vaccine;
import co.zenpets.users.utils.models.pets.vaccines.Vaccines;
import co.zenpets.users.utils.models.pets.vaccines.VaccinesAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VaccinationRecordModifier extends AppCompatActivity {

    /** THE INCOMING VACCINATION ID **/
    private String VACCINATION_ID = null;

    /** DATA TYPES FOR HOLDING THE COLLECTED DATA **/
    private String VACCINE_ID = null;
    private String VACCINATION_NAME = null;
    private String VACCINATION_DATE = null;
    private String VACCINATION_NEXT_DATE = null;
    private String VACCINATION_REMINDER = "False";
    private String VACCINATION_NOTES = null;

    /** THE VACCINES SPINNER ADAPTER AND ARRAY LIST **/
    private ArrayList<Vaccine> arrVaccines = new ArrayList<>();

    /** A PROGRESS DIALOG **/
    private ProgressDialog dialog;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.spnVaccineTypes) AppCompatSpinner spnVaccineTypes;
    @BindView(R.id.txtVaccinationDate) AppCompatTextView txtVaccinationDate;
    @BindView(R.id.chkbxRemind) AppCompatCheckBox chkbxRemind;
    @BindView(R.id.linlaNextDate) LinearLayout linlaNextDate;
    @BindView(R.id.txtVaccinationNextDate) AppCompatTextView txtVaccinationNextDate;
    @BindView(R.id.inpVaccineNotes) TextInputLayout inpVaccineNotes;
    @BindView(R.id.edtVaccineNotes) TextInputEditText edtVaccineNotes;

    /** SELECT THE PET'S DATE OF VACCINATION **/
    @OnClick(R.id.btnVaccinationDate) void selectVaccinationDate()   {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int month, int date) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, date);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                /* FOR THE DATABASE ONLY !!!! */
                VACCINATION_DATE = sdf.format(cal.getTime());

                /* FOR DISPLAY ONLY !!!! */
                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
                String selectedDate = dateFormat.format(cal.getTime());
                txtVaccinationDate.setText(selectedDate);
            }
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show(getFragmentManager(), "DatePickerDialog");
    }

    /** SELECT THE PET'S NEXT DATE OF VACCINATION **/
    @OnClick(R.id.btnVaccinationNextDate) void nextDate()   {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int month, int date) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, date);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                /* FOR THE DATABASE ONLY !!!! */
                VACCINATION_NEXT_DATE = sdf.format(cal.getTime());

                /* FOR DISPLAY ONLY !!!! */
                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
                String selectedDate = dateFormat.format(cal.getTime());
                txtVaccinationNextDate.setText(selectedDate);
            }
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show(getFragmentManager(), "DatePickerDialog");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vaccination_modifier);
        ButterKnife.bind(this);

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* SELECT A VACCINE */
        spnVaccineTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* GET THE SELECTED VACCINE ID AND NAME */
                VACCINE_ID = arrVaccines.get(position).getVaccineID();
                VACCINATION_NAME = arrVaccines.get(position).getVaccineName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* TOGGLE THE REMINDER CHECK BOX */
        chkbxRemind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)  {
                    VACCINATION_REMINDER = "True";
                    linlaNextDate.setVisibility(View.VISIBLE);
                } else {
                    VACCINATION_REMINDER = "False";
                    linlaNextDate.setVisibility(View.GONE);
                }
            }
        });
    }

    /***** GET THE VACCINATION DETAILS *****/
    private void fetchVaccinationDetails() {
        VaccinationsAPI api = ZenApiClient.getClient().create(VaccinationsAPI.class);
        Call<Vaccination> call = api.fetchVaccinationDetails(VACCINATION_ID);
        call.enqueue(new Callback<Vaccination>() {
            @Override
            public void onResponse(Call<Vaccination> call, Response<Vaccination> response) {
                Vaccination vaccination = response.body();
                if (vaccination != null)    {
                    /* GET THE VACCINE ID AND NAME */
                    VACCINE_ID = vaccination.getVaccineID();
                    int intVaccinePosition = getVaccineIndex(arrVaccines, VACCINE_ID);
                    spnVaccineTypes.setSelection(intVaccinePosition);

                    /* GET THE VACCINATION DATE */
                    if (vaccination.getVaccinationDate() != null)  {
                        VACCINATION_DATE = vaccination.getVaccinationDate();
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                        try {
                            Date date = inputFormat.parse(VACCINATION_DATE);
                            String strDate = outputFormat.format(date);
                            txtVaccinationDate.setText(strDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    /* GET THE REMINDER STATUS */
                    VACCINATION_REMINDER = vaccination.getVaccinationReminder();
                    if (VACCINATION_REMINDER.equalsIgnoreCase("true"))  {
                        chkbxRemind.setChecked(true);
                        linlaNextDate.setVisibility(View.VISIBLE);

                        /* GET THE NEXT VACCINATION DATE */
                        VACCINATION_NEXT_DATE = vaccination.getVaccinationNextDate();
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                        try {
                            Date date = inputFormat.parse(VACCINATION_NEXT_DATE);
                            String strDate = outputFormat.format(date);
                            txtVaccinationNextDate.setText(strDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else if (VACCINATION_REMINDER.equalsIgnoreCase("false"))  {
                        linlaNextDate.setVisibility(View.GONE);
                    }

                    /* GET THE VACCINATION NOTES */
                    VACCINATION_NOTES = vaccination.getVaccinationNotes();
                    if (VACCINATION_NOTES != null && !VACCINATION_NOTES.equalsIgnoreCase("null"))   {
                        edtVaccineNotes.setText(VACCINATION_NOTES);
                        edtVaccineNotes.setSelection(edtVaccineNotes.getText().length());
                    }

                    /* HIDE THE PROGRESS BAR AFTER FETCHING THE VACCINATION DETAILS */
                    linlaProgress.setVisibility(View.GONE);
                } else {
                    /* HIDE THE PROGRESS BAR */
                    linlaProgress.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(), "Failed to get required data...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Vaccination> call, Throwable t) {
//                Log.e("DETAILS FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Update Vaccination";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(strTitle);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(VaccinationRecordModifier.this);
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
                /* VALIDATE THE VACCINATION DETAILS */
                validateVaccinationDetails();
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

    /***** VALIDATE THE VACCINATION DETAILS *****/
    private void validateVaccinationDetails() {
        /* HIDE THE KEYBOARD */
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(edtVaccineNotes.getWindowToken(), 0);

        /* GET THE VACCINE NOTES */
        if (TextUtils.isEmpty(edtVaccineNotes.getText().toString()))    {
            VACCINATION_NOTES = "Null";
        } else {
            VACCINATION_NOTES = edtVaccineNotes.getText().toString();
        }

        /* SHOW THE PROGRESS AND UPDATE THE VACCINATION RECORD */
        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating the Vaccination Record...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();
        VaccinationsAPI api = ZenApiClient.getClient().create(VaccinationsAPI.class);
        Call<Vaccination> call = api.updateVaccinationRecord(
                VACCINATION_ID, VACCINE_ID, VACCINATION_DATE, VACCINATION_NEXT_DATE, VACCINATION_REMINDER, VACCINATION_NOTES
        );
        call.enqueue(new Callback<Vaccination>() {
            @Override
            public void onResponse(Call<Vaccination> call, Response<Vaccination> response) {
                if (response.isSuccessful())    {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Successfully updated vaccination record", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "There was an error updating the record. Please try again", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Vaccination> call, Throwable t) {
//                Log.e("EXCEPTION", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("VACCINATION_ID")) {
            VACCINATION_ID = bundle.getString("VACCINATION_ID");
            if (VACCINATION_ID != null) {
                /* SHOW THE PROGRESS BAR AND FETCH THE LIST OF VACCINES */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchVaccines();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** FETCH THE LIST OF VACCINES *****/
    private void fetchVaccines() {
        VaccinesAPI api = ZenApiClient.getClient().create(VaccinesAPI.class);
        Call<Vaccines> call = api.allVaccines();
        call.enqueue(new Callback<Vaccines>() {
            @Override
            public void onResponse(Call<Vaccines> call, Response<Vaccines> response) {
                if (response.body() != null && response.body().getVaccines() != null)   {
                    arrVaccines = response.body().getVaccines();

                    /* SET THE ADAPTER TO THE VISIT REASONS SPINNER */
                    spnVaccineTypes.setAdapter(new VaccinesAdapter(VaccinationRecordModifier.this, arrVaccines));

                    /* FETCH THE VACCINATION DETAILS */
                    fetchVaccinationDetails();
                } else {
                    /* HIDE THE PROGRESS BAR */
                    linlaProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Vaccines> call, Throwable t) {
//                Log.e("VACCINES FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE VACCINE POSITION *****/
    private int getVaccineIndex(ArrayList<Vaccine> array, String vaccineID) {
        int index = 0;
        for (int i =0; i < array.size(); i++) {
            if (array.get(i).getVaccineID().equalsIgnoreCase(vaccineID))   {
                index = i;
                break;
            }
        }
        return index;
    }
}