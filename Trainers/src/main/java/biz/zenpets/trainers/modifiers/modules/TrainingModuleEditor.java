package biz.zenpets.trainers.modifiers.modules;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import biz.zenpets.trainers.R;
import biz.zenpets.trainers.utils.TypefaceSpan;
import biz.zenpets.trainers.utils.adapters.modules.TrainingDurationNumberAdapter;
import biz.zenpets.trainers.utils.adapters.modules.TrainingDurationUnitAdapter;
import biz.zenpets.trainers.utils.helpers.ZenApiClient;
import biz.zenpets.trainers.utils.models.trainers.modules.Module;
import biz.zenpets.trainers.utils.models.trainers.modules.ModulesAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainingModuleEditor extends AppCompatActivity {

    /** THE INCOMING MODULE ID **/
    String MODULE_ID = null;

    /** STRINGS TO HOLD THE MODULE DETAILS **/
    String TRAINER_ID = null;
    String MODULE_NAME = null;
    String MODULE_DURATION = null;
    String MODULE_DURATION_UNITS = null;
    String MODULE_SESSIONS = null;
    String MODULE_DETAILS = null;
    String MODULE_FORMAT = null;
    String MODULE_GROUP_SIZE = null;
    String MODULE_FEES = null;

    /** THE ARRAY LIST INSTANCES **/
    List<String> arrDurationNumber = new ArrayList<>();
    List<String> arrDurationUnits = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.inputModuleName) TextInputLayout inputModuleName;
    @BindView(R.id.edtModuleName) TextInputEditText edtModuleName;
    @BindView(R.id.spnDurationNumber) AppCompatSpinner spnDurationNumber;
    @BindView(R.id.spnDurationUnit) AppCompatSpinner spnDurationUnit;
    @BindView(R.id.inputSessions) TextInputLayout inputSessions;
    @BindView(R.id.edtSessions) TextInputEditText edtSessions;
    @BindView(R.id.inputDetails) TextInputLayout inputDetails;
    @BindView(R.id.edtDetails) TextInputEditText edtDetails;
    @BindView(R.id.rgFormat) RadioGroup rgFormat;
    @BindView(R.id.rdbtnIndividual) AppCompatRadioButton rdbtnIndividual;
    @BindView(R.id.rdbtnGroup) AppCompatRadioButton rdbtnGroup;
    @BindView(R.id.inputGroup) TextInputLayout inputGroup;
    @BindView(R.id.edtGroup) TextInputEditText edtGroup;
    @BindView(R.id.inputFees) TextInputLayout inputFees;
    @BindView(R.id.edtFees) TextInputEditText edtFees;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_modules_modifier);
        ButterKnife.bind(this);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* POPULATE THE DURATION NUMBER SPINNER */
        String[] strDurationNumber = getResources().getStringArray(R.array.duration_number);
        arrDurationNumber = Arrays.asList(strDurationNumber);
        spnDurationNumber.setAdapter(new TrainingDurationNumberAdapter(
                TrainingModuleEditor.this,
                R.layout.training_module_sessions_row,
                arrDurationNumber));

        /* SELECT THE DURATION NUMBER */
        spnDurationNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MODULE_DURATION = arrDurationNumber.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* POPULATE THE DURATION UNITS SPINNER */
        String[] strDurationUnits = getResources().getStringArray(R.array.duration_units);
        arrDurationUnits = Arrays.asList(strDurationUnits);
        spnDurationUnit.setAdapter(new TrainingDurationUnitAdapter(
                TrainingModuleEditor.this,
                R.layout.training_module_sessions_row,
                arrDurationUnits));

        /* SELECT THE DURATION UNIT */
        spnDurationUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MODULE_DURATION_UNITS = arrDurationUnits.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* SELECT THE TRAINING MODULE FORMAT (INDIVIDUAL TRAINING / GROUP TRAINING) */
        rgFormat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rdbtnIndividual:
                        /* SET THE FORMAT TO INDIVIDUAL */
                        MODULE_FORMAT = "Individual";

                        /* HIDE THE GROUP SIZE INPUT */
                        inputGroup.setVisibility(View.GONE);
                        break;
                    case R.id.rdbtnGroup:
                        /* SET THE FORMAT TO GROUP */
                        MODULE_FORMAT = "Group";

                        /* SHOW THE GROUP SIZE INPUT */
                        inputGroup.setVisibility(View.VISIBLE);
                        inputGroup.requestFocus();
                        break;
                    default:
                        break;
                }
            }
        });

        /* GET THE INCOMING DATA */
        getIncomingData();
    }

    /***** FETCH MODULE DETAILS *****/
    private void fetchModuleDetails() {
        ModulesAPI api = ZenApiClient.getClient().create(ModulesAPI.class);
        Call<Module> call = api.fetchModuleDetails(MODULE_ID);
        call.enqueue(new Callback<Module>() {
            @Override
            public void onResponse(Call<Module> call, Response<Module> response) {
                Module module = response.body();
                if (module != null) {

                    /* GET AND SET THE MODULE NAME */
                    MODULE_NAME = module.getTrainerModuleName();
                    if (MODULE_NAME != null)    {
                        edtModuleName.setText(MODULE_NAME);
                    }

                    /* GET AND SET THE MODULE DURATION */
                    MODULE_DURATION = module.getTrainerModuleDuration();
                    if (MODULE_DURATION != null)    {
                        int intDurationPosition = getDurationPosition(arrDurationNumber, MODULE_DURATION);
                        spnDurationNumber.setSelection(intDurationPosition);
                    }

                    /* GET AND SET THE MODULE DURATION UNIT */
                    MODULE_DURATION_UNITS = module.getTrainerModuleDurationUnit();
                    if (MODULE_DURATION_UNITS != null)    {
                        int intDurationUnitPosition = getDurationUnitPosition(arrDurationUnits, MODULE_DURATION_UNITS);
                        spnDurationUnit.setSelection(intDurationUnitPosition);
                    }

                    /* GET AND SET THE NUMBER OF SESSIONS */
                    MODULE_SESSIONS = module.getTrainerModuleSessions();
                    if (MODULE_SESSIONS != null)    {
                        edtSessions.setText(MODULE_SESSIONS);
                    }

                    /* GET AND SET THE MODULE DETAILS */
                    MODULE_DETAILS = module.getTrainerModuleDetails();
                    if (MODULE_DETAILS != null) {
                        edtDetails.setText(MODULE_DETAILS);
                    }

                    /* GET AND SET THE MODULE FORMAT */
                    MODULE_FORMAT = module.getTrainerModuleFormat();
                    if (MODULE_FORMAT != null)  {
                        /* CHECK IF THE FORMAT IS "INDIVIDUAL" OR "GROUP"  */
                        if (MODULE_FORMAT.equalsIgnoreCase("Individual"))   {
                            rdbtnIndividual.setChecked(true);
                            rdbtnGroup.setChecked(false);

                            /* HIDE THE GROUP SIZE INPUT */
                            inputGroup.setVisibility(View.GONE);
                        } else if (MODULE_FORMAT.equalsIgnoreCase("Group")) {
                            rdbtnGroup.setChecked(true);
                            rdbtnIndividual.setChecked(false);

                            /* SHOW THE GROUP SIZE INPUT */
                            inputGroup.setVisibility(View.VISIBLE);

                            /* GET THE GROUP SIZE */
                            MODULE_GROUP_SIZE = module.getTrainerModuleSize();
                            edtGroup.setText(MODULE_GROUP_SIZE);
                        }
                    }

                    /* GET AND SET THE MODULE FEES */
                    MODULE_FEES = module.getTrainerModuleFees();
                    if (MODULE_FEES != null)    {
                        edtFees.setText(MODULE_FEES);
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<Module> call, Throwable t) {
                Log.e("DETAILS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE DURATION POSITION *****/
    private int getDurationPosition(List<String> arrDurationNumber, String moduleDuration) {
        int index = 0;
        for (int i =0; i < arrDurationNumber.size(); i++) {
            if (arrDurationNumber.get(i).equalsIgnoreCase(moduleDuration))   {
                index = i;
                break;
            }
        }
        return index;
    }

    /***** GET THE DURATION UNITS POSITION *****/
    private int getDurationUnitPosition(List<String> arrDurationUnits, String moduleDurationUnits) {
        int index = 0;
        for (int i =0; i < arrDurationUnits.size(); i++) {
            if (arrDurationUnits.get(i).contains(moduleDurationUnits))   {
                index = i;
                break;
            }
        }
        return index;
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("MODULE_ID"))  {
            MODULE_ID = bundle.getString("MODULE_ID");
            if (MODULE_ID != null)  {
                /* FETCH MODULE DETAILS */
                fetchModuleDetails();
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
        String strTitle = "Update Training Module";
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
        MenuInflater inflater = new MenuInflater(TrainingModuleEditor.this);
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
                /* CHECK FOR ALL MODULE DETAILS */
                checkModuleDetails();
                break;
            case R.id.menuCancel:
                finish();
                break;
            default:
                break;
        }
        return false;
    }

    /***** CHECK FOR ALL MODULE DETAILS *****/
    private void checkModuleDetails() {
        /* HIDE THE KEYBOARD */
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(edtModuleName.getWindowToken(), 0);

        /* GET THE MODULE DETAILS */
        MODULE_NAME = edtModuleName.getText().toString().trim();
        MODULE_SESSIONS = edtSessions.getText().toString().trim();
        MODULE_DETAILS = edtDetails.getText().toString().trim();
        if (MODULE_FORMAT.equalsIgnoreCase("Group"))    {
            MODULE_GROUP_SIZE = edtGroup.getText().toString().trim();
        }
        MODULE_FEES = edtFees.getText().toString().trim();

        /* VALIDATE THE DATA **/
        if (TextUtils.isEmpty(MODULE_NAME)) {
            inputModuleName.setError("Enter the Training Module's name");
            inputModuleName.requestFocus();
            inputModuleName.setErrorEnabled(true);
            inputSessions.setErrorEnabled(false);
            inputDetails.setErrorEnabled(false);
            inputGroup.setErrorEnabled(false);
            inputFees.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(MODULE_SESSIONS))  {
            inputSessions.setError("Enter the total number of sessions");
            inputSessions.requestFocus();
            inputSessions.setErrorEnabled(true);
            inputModuleName.setErrorEnabled(false);
            inputDetails.setErrorEnabled(false);
            inputGroup.setErrorEnabled(false);
            inputFees.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(MODULE_DETAILS))   {
            inputDetails.setError("Provide the Training Module's details");
            inputDetails.requestFocus();
            inputDetails.setErrorEnabled(true);
            inputModuleName.setErrorEnabled(false);
            inputSessions.setErrorEnabled(false);
            inputGroup.setErrorEnabled(false);
            inputFees.setErrorEnabled(false);
        } else if (MODULE_FORMAT.equalsIgnoreCase("Group") && TextUtils.isEmpty(MODULE_GROUP_SIZE)) {
            inputGroup.setError("Provide size of the training group");
            inputGroup.requestFocus();
            inputGroup.setErrorEnabled(true);
            inputDetails.setErrorEnabled(false);
            inputModuleName.setErrorEnabled(false);
            inputSessions.setErrorEnabled(false);
            inputFees.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(MODULE_FEES))  {
            inputFees.setError("Provide the training fee");
            inputFees.requestFocus();
            inputFees.setErrorEnabled(true);
            inputGroup.setErrorEnabled(false);
            inputDetails.setErrorEnabled(false);
            inputModuleName.setErrorEnabled(false);
            inputSessions.setErrorEnabled(false);
        } else {
            /* DISABLE ALL ERRORS */
            inputModuleName.setErrorEnabled(false);
            inputSessions.setErrorEnabled(false);
            inputDetails.setErrorEnabled(false);
            inputGroup.setErrorEnabled(false);
            inputFees.setErrorEnabled(false);

            /* PUBLISH THE UPDATED TRAINING MODULE */
            updateTrainingModule();
        }
    }

    /***** PUBLISH THE UPDATED TRAINING MODULE *****/
    private void updateTrainingModule() {
        /* INSTANTIATE THE DIALOG INSTANCE */
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait while we publish your training module..");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();

        /* GET THE SINGULAR OR PLURAL FOR DAYS / MONTHS */
        String strUnits;
        if (MODULE_DURATION_UNITS.equalsIgnoreCase("day/s")) {
            strUnits = "Day";
        } else {
            strUnits = "Month";
        }

        ModulesAPI api = ZenApiClient.getClient().create(ModulesAPI.class);
        Call<Module> call = api.updateTrainerModule(
                MODULE_ID, MODULE_NAME, MODULE_DURATION, strUnits, MODULE_SESSIONS,
                MODULE_DETAILS, MODULE_FORMAT, MODULE_GROUP_SIZE, MODULE_FEES);
        call.enqueue(new Callback<Module>() {
            @Override
            public void onResponse(Call<Module> call, Response<Module> response) {
                if (response.isSuccessful())    {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Update successful...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Update failed...", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Module> call, Throwable t) {
                Log.e("UPDATE FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }
}