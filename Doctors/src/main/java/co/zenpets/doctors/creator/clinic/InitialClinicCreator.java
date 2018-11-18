package co.zenpets.doctors.creator.clinic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.AppPrefs;
import co.zenpets.doctors.utils.TypefaceSpan;
import co.zenpets.doctors.utils.adapters.location.CitiesAdapter;
import co.zenpets.doctors.utils.adapters.location.LocalitiesAdapter;
import co.zenpets.doctors.utils.adapters.location.StatesAdapter;
import co.zenpets.doctors.utils.helpers.classes.LocationPickerActivity;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.models.clinics.ClinicsAPI;
import co.zenpets.doctors.utils.models.doctors.clinic.Clinic;
import co.zenpets.doctors.utils.models.doctors.clinic.ClinicMapperAPI;
import co.zenpets.doctors.utils.models.location.CitiesData;
import co.zenpets.doctors.utils.models.location.CityData;
import co.zenpets.doctors.utils.models.location.LocalitiesData;
import co.zenpets.doctors.utils.models.location.LocalityData;
import co.zenpets.doctors.utils.models.location.LocationAPI;
import co.zenpets.doctors.utils.models.location.StateData;
import co.zenpets.doctors.utils.models.location.StatesData;
import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InitialClinicCreator extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN DOCTOR'S ID **/
    private String DOCTOR_ID = null;

    /** THE COUNTRY, STATES, CITIES AND LOCALITIES ARRAY LISTS **/
//    ArrayList<CountryData> arrCountries = new ArrayList<>();
    private ArrayList<StateData> arrStates = new ArrayList<>();
    private ArrayList<CityData> arrCities = new ArrayList<>();
    private ArrayList<LocalityData> arrLocalities = new ArrayList<>();

    /** STRINGS TO HOLD THE USER PROVIDED INFORMATION **/
    private String CLINIC_NAME = null;
    private String PHONE_NUMBER_1 = null;
    private String PHONE_NUMBER_2 = null;
    private String CLINIC_ADDRESS = null;
    //    String COUNTRY_ID = null;
    private String STATE_ID = null;
    private String CITY_ID = null;
    private String LOCALITY_ID = null;
    private String PIN_CODE = null;
    private String LANDMARK = null;
    private Double CLINIC_LATITUDE;
    private Double CLINIC_LONGITUDE;
    private String FILE_NAME = null;
    private Uri LOGO_URI = null;
    private String LOGO_URL = null;

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog dialog;

    /** THE LOCATION REQUEST CODE **/
    private final int REQUEST_LOCATION = 1;

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int ACCESS_STORAGE_CONSTANT = 200;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.inputClinicName) TextInputLayout inputClinicName;
    @BindView(R.id.edtClinicName) AppCompatEditText edtClinicName;
    @BindView(R.id.inputPhone1) TextInputLayout inputPhone1;
    @BindView(R.id.edtPhone1) AppCompatEditText edtPhone1;
    @BindView(R.id.inputPhone2) TextInputLayout inputPhone2;
    @BindView(R.id.edtPhone2) AppCompatEditText edtPhone2;
    @BindView(R.id.inputClinicAddress) TextInputLayout inputClinicAddress;
    @BindView(R.id.edtClinicAddress) AppCompatEditText edtClinicAddress;
    @BindView(R.id.inputPinCode) TextInputLayout inputPinCode;
    @BindView(R.id.edtPinCode) AppCompatEditText edtPinCode;
    //    @BindView(R.id.spnCountry) AppCompatSpinner spnCountry;
    @BindView(R.id.spnState) AppCompatSpinner spnState;
    @BindView(R.id.spnCity) AppCompatSpinner spnCity;
    @BindView(R.id.spnLocalities) AppCompatSpinner spnLocalities;
    @BindView(R.id.inputLandmark) TextInputLayout inputLandmark;
    @BindView(R.id.edtLandmark) AppCompatEditText edtLandmark;
    @BindView(R.id.txtLocation) AppCompatTextView txtLocation;
    @BindView(R.id.imgvwLogo) AppCompatImageView imgvwLogo;

    /** SELECT THE CLINIC LOCATION ON A MAP **/
    @OnClick(R.id.btnLocationPicker) protected void pickLocation()  {
        Intent intent = new Intent(this, LocationPickerActivity.class);
        startActivityForResult(intent, REQUEST_LOCATION);
    }

    /** CHOOSE THE CLINIC LOGO **/
    @OnClick(R.id.imgvwLogo) protected void chooseLogo()    {
        final BottomSheetDialog sheetDialog = new BottomSheetDialog(InitialClinicCreator.this);
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
                EasyImage.openGallery(InitialClinicCreator.this, 0);
            }
        });

        /* SELECT A CAMERA IMAGE */
        linlaCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetDialog.dismiss();
                EasyImage.openCamera(InitialClinicCreator.this, 0);
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clinic_creator);
        ButterKnife.bind(this);

        /* CHECK STORAGE PERMISSION */
        checkStoragePermission();

        /* THE EASY IMAGE CONFIGURATION */
        EasyImage.configuration(this)
                .setImagesFolderName("Zen Pets")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(true)
                .setAllowMultiplePickInGallery(false);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* GET THE LOGGED IN DOCTOR'S ID */
        DOCTOR_ID = getApp().getDoctorID();
        if (DOCTOR_ID != null)    {

            /* FETCH THE LIST OF STATES IN THE SELECTED COUNTRY */
            getAllStates();

//            /* SELECT A COUNTRY */
//            spnCountry.setOnItemSelectedListener(selectCountry);

            /* SELECT A STATE */
            spnState.setOnItemSelectedListener(selectState);

            /* SELECT A CITY */
            spnCity.setOnItemSelectedListener(selectCity);

            /* SELECT A LOCALITY */
            spnLocalities.setOnItemSelectedListener(selectLocality);
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** FETCH THE LIST OF STATES IN THE SELECTED COUNTRY *****/
    private void getAllStates() {
        LocationAPI api = ZenApiClient.getClient().create(LocationAPI.class);
        Call<StatesData> call = api.allStates("51");
        call.enqueue(new Callback<StatesData>() {
            @Override
            public void onResponse(@NonNull Call<StatesData> call, @NonNull Response<StatesData> response) {
                arrStates = response.body().getStates();
                if (arrStates != null && arrStates.size() > 0)  {
                    /* INSTANTIATE THE STATES ADAPTER */
                    StatesAdapter statesAdapter = new StatesAdapter(InitialClinicCreator.this, arrStates);

                    /* SET THE STATES SPINNER */
                    spnState.setAdapter(statesAdapter);

                }
            }

            @Override
            public void onFailure(@NonNull Call<StatesData> call, @NonNull Throwable t) {
//                Log.e("FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE LIST OF CITIES IN THE SELECTED STATE *****/
    private void getAllCities() {
        LocationAPI api = ZenApiClient.getClient().create(LocationAPI.class);
        Call<CitiesData> call = api.allCities(STATE_ID);
        call.enqueue(new Callback<CitiesData>() {
            @Override
            public void onResponse(@NonNull Call<CitiesData> call, @NonNull Response<CitiesData> response) {
                arrCities = response.body().getCities();
                if (arrCities != null && arrCities.size() > 0)  {
                    /* INSTANTIATE THE CITIES ADAPTER */
                    CitiesAdapter citiesAdapter = new CitiesAdapter(InitialClinicCreator.this, arrCities);

                    /* SET THE CITIES SPINNER */
                    spnCity.setAdapter(citiesAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CitiesData> call, @NonNull Throwable t) {
//                Log.e("FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE LIST OF CITIES IN THE SELECTED STATE *****/
    private void getAllLocalities() {
        LocationAPI api = ZenApiClient.getClient().create(LocationAPI.class);
        Call<LocalitiesData> call = api.allLocalities(CITY_ID);
        call.enqueue(new Callback<LocalitiesData>() {
            @Override
            public void onResponse(@NonNull Call<LocalitiesData> call, @NonNull Response<LocalitiesData> response) {
                arrLocalities = response.body().getLocalities();
                if (arrLocalities != null && arrLocalities.size() > 0)  {
                    /* INSTANTIATE THE LOCALITIES ADAPTER */
                    LocalitiesAdapter localitiesAdapter = new LocalitiesAdapter(InitialClinicCreator.this, arrLocalities);

                    /* SET THE LOCALITIES SPINNERS */
                    spnLocalities.setAdapter(localitiesAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LocalitiesData> call, @NonNull Throwable t) {
//                Log.e("FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /***** CHECK FOR ALL CLINIC DETAILS *****/
    private void checkClinicDetails() {
        /* HIDE THE KEYBOARD */
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtClinicName.getWindowToken(), 0);
        }

        /* COLLECT ALL THE DATA */
        CLINIC_NAME = edtClinicName.getText().toString().trim();
        PHONE_NUMBER_1 = edtPhone1.getText().toString().trim();
        PHONE_NUMBER_2 = edtPhone2.getText().toString().trim();
        CLINIC_ADDRESS = edtClinicAddress.getText().toString().trim();
        PIN_CODE = edtPinCode.getText().toString().trim();
        LANDMARK = edtLandmark.getText().toString().trim();

        /* GENERATE THE FILE NAME */
        if (!(LOGO_URI == null) && !TextUtils.isEmpty(CLINIC_NAME))    {
            FILE_NAME = CLINIC_NAME.replaceAll(" ", "_").toLowerCase().trim();
        } else {
            FILE_NAME = null;
        }

        /* VALIDATE THE DATA */
        if (TextUtils.isEmpty(CLINIC_NAME)) {
            inputClinicName.setErrorEnabled(false);
            inputPhone1.setErrorEnabled(false);
            inputClinicAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputClinicName.setError(getString(R.string.clinic_name_empty));
            inputClinicName.requestFocus();
        } else if (TextUtils.isEmpty(PHONE_NUMBER_1)) {
            inputClinicName.setErrorEnabled(false);
            inputPhone1.setErrorEnabled(false);
            inputClinicAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhone1.setError(getString(R.string.clinic_phone_empty));
            inputPhone1.requestFocus();
        } else if (TextUtils.isEmpty(CLINIC_ADDRESS)) {
            inputClinicName.setErrorEnabled(false);
            inputPhone1.setErrorEnabled(false);
            inputClinicAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputClinicAddress.setError(getString(R.string.clinic_postal_address_empty));
            inputClinicAddress.requestFocus();
        } else if (TextUtils.isEmpty(PIN_CODE)) {
            inputClinicName.setErrorEnabled(false);
            inputPhone1.setErrorEnabled(false);
            inputClinicAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPinCode.setError(getString(R.string.clinic_pin_code_empty));
            inputPinCode.requestFocus();
        }
//        else if (TextUtils.isEmpty(COUNTRY_ID) || COUNTRY_ID == null) {
//            inputClinicName.setErrorEnabled(false);
//            inputPhone1.setErrorEnabled(false);
//            inputClinicAddress.setErrorEnabled(false);
//            inputPinCode.setErrorEnabled(false);
//            Toast.makeText(getApplicationContext(), "Please select a Country", Toast.LENGTH_LONG).show();
//        }
        else if (TextUtils.isEmpty(STATE_ID) || STATE_ID == null) {
            inputClinicName.setErrorEnabled(false);
            inputPhone1.setErrorEnabled(false);
            inputClinicAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            Toast.makeText(getApplicationContext(), "Please select a State", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(CITY_ID)) {
            inputClinicName.setErrorEnabled(false);
            inputPhone1.setErrorEnabled(false);
            inputClinicAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            Toast.makeText(getApplicationContext(), "Please select a City", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(LOCALITY_ID) || LOCALITY_ID == null)   {
            inputClinicName.setErrorEnabled(false);
            inputPhone1.setErrorEnabled(false);
            inputClinicAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            Toast.makeText(getApplicationContext(), "Please select a Locality", Toast.LENGTH_LONG).show();
        } else if (CLINIC_LONGITUDE == null || CLINIC_LATITUDE == null) {
            inputClinicName.setErrorEnabled(false);
            inputPhone1.setErrorEnabled(false);
            inputClinicAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            Toast.makeText(getApplicationContext(), "Please select / mark your Location on the Map", Toast.LENGTH_LONG).show();
        } else if (FILE_NAME == null)    {
            inputClinicName.setErrorEnabled(false);
            inputPhone1.setErrorEnabled(false);
            inputClinicAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            Toast.makeText(getApplicationContext(), getString(R.string.clinic_logo_empty), Toast.LENGTH_LONG).show();
        } else {
            inputClinicName.setErrorEnabled(false);
            inputPhone1.setErrorEnabled(false);
            inputClinicAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);

            /* UPLOAD THE CLINIC LOGO */
            uploadClinicLogo();
        }
    }

    /***** UPLOAD THE CLINIC LOGO *****/
    private void uploadClinicLogo() {
        /* SHOW THE PROGRESS DIALOG WHILE UPLOADING THE IMAGE **/
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait while we add the new Clinic...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference refStorage = storageReference.child("Clinic Logos").child(FILE_NAME);
        UploadTask uploadTask = refStorage.putFile(LOGO_URI);
        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                /* CONTINUE WITH THE TASK TO GET THE IMAGE URL */
                return refStorage.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadURL = task.getResult();
                    LOGO_URL = String.valueOf(downloadURL);
                    if (LOGO_URL != null)    {
                        /* CREATE THE NEW CLINIC */
                        ClinicsAPI api = ZenApiClient.getClient().create(ClinicsAPI.class);
                        Call<Clinic> call = api.newClinic(
                                DOCTOR_ID, "51", STATE_ID, CITY_ID, LOCALITY_ID,
                                CLINIC_NAME, CLINIC_ADDRESS, LANDMARK, PIN_CODE,
                                String.valueOf(CLINIC_LATITUDE), String.valueOf(CLINIC_LONGITUDE),
                                LOGO_URL, "91", PHONE_NUMBER_1, "91", PHONE_NUMBER_2, "Pending");
                        call.enqueue(new Callback<Clinic>() {
                            @Override
                            public void onResponse(Call<Clinic> call, Response<Clinic> response) {
                                if (response.isSuccessful())    {
                                    /* MAP THE DOCTOR TO THE CLINIC */
                                    String clinicID = response.body().getClinicID();
                                    mapTheDoctor(clinicID);
                                } else {
                                    /* DISMISS THE DIALOG */
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "There was a problem creating the new Clinic. Please try again", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Clinic> call, Throwable t) {
                            }
                        });
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "There was a problem creating your new account. Please try again by clicking the Save button.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    /***** MAP THE DOCTOR TO THE SELECTED CLINIC *****/
    private void mapTheDoctor(String result) {
        ClinicMapperAPI api = ZenApiClient.getClient().create(ClinicMapperAPI.class);
        Call<String> call = api.newDoctorClinic(DOCTOR_ID, result, "Pending");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful())    {
                    /* DISMISS THE DIALOG */
                    dialog.dismiss();
                    Intent success = new Intent();
                    setResult(RESULT_OK, success);
                    Toast.makeText(getApplicationContext(), "The new Clinic was successfully created.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "There was an error mapping to this Clinic. Please try again", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                Log.e("FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = "Clinic Details";
//        String strTitle = getString(R.string.add_a_new_pet);
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
        MenuInflater inflater = new MenuInflater(InitialClinicCreator.this);
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
                /* CHECK FOR ALL CLINIC DETAILS */
                checkClinicDetails();
                break;
            case R.id.menuCancel:
                finish();
                break;
            default:
                break;
        }
        return false;
    }

//    /***** SELECT A COUNTRY *****/
//    private final AdapterView.OnItemSelectedListener selectCountry = new AdapterView.OnItemSelectedListener() {
//        @Override
//        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            COUNTRY_ID = arrCountries.get(position).getCountryID();
//
//            /* CLEAR THE STATES ARRAY LIST */
//            arrStates.clear();
//
//            /* FETCH THE LIST OF STATES */
//            if (COUNTRY_ID != null) {
//                /* FETCH THE LIST OF STATES IN THE SELECTED COUNTRY */
//                new GetAllStates(InitialClinicCreator.this).execute(COUNTRY_ID);
//            }
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> parent) {
//        }
//    };

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

    /***** SELECT A CITY *****/
    private final AdapterView.OnItemSelectedListener selectCity = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            CITY_ID = arrCities.get(position).getCityID();

            /* CLEAR THE LOCALITIES ARRAY LIST */
            if (arrLocalities != null)
                arrLocalities.clear();

            /* FETCH THE LIST OF LOCALITIES */
            if (CITY_ID != null)   {
                /* FETCH THE LIST OF LOCALITIES IN THE SELECTED CITY */
                getAllLocalities();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    /***** SELECT A LOCALITY *****/
    private final AdapterView.OnItemSelectedListener selectLocality = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            LOCALITY_ID = arrLocalities.get(i).getLocalityID();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    };

//    @Override
//    public void onCountriesResult(ArrayList<CountryData> arrCountries) {
//        this.arrCountries = arrCountries;
//
//        /* INSTANTIATE THE COUNTRIES ADAPTER */
//        CountriesAdapter countriesAdapter = new CountriesAdapter(InitialClinicCreator.this, this.arrCountries);
//
//        /* SET THE COUNTRIES SPINNER */
//        spnCountry.setAdapter(countriesAdapter);
//    }

//    @Override
//    public void onStatesResult(ArrayList<StateData> arrStates) {
//        this.arrStates = arrStates;
//
//        /* INSTANTIATE THE STATES ADAPTER */
//        StatesAdapter statesAdapter = new StatesAdapter(InitialClinicCreator.this, this.arrStates);
//
//        /* SET THE STATES SPINNER */
//        spnState.setAdapter(statesAdapter);
//    }

//    @Override
//    public void onCitiesResult(ArrayList<CityData> arrCities) {
//        this.arrCities = arrCities;
//
//        /* INSTANTIATE THE CITIES ADAPTER */
//        CitiesAdapter citiesAdapter = new CitiesAdapter(InitialClinicCreator.this, this.arrCities);
//
//        /* SET THE CITIES SPINNER */
//        spnCity.setAdapter(citiesAdapter);
//    }

//    @Override
//    public void onLocalitiesResult(ArrayList<LocalityData> arrLocalities) {
//        this.arrLocalities = arrLocalities;
//
//        /* INSTANTIATE THE LOCALITIES ADAPTER */
//        LocalitiesAdapter localitiesAdapter = new LocalitiesAdapter(InitialClinicCreator.this, arrLocalities);
//
//        /* SET THE LOCALITIES SPINNER */
//        spnLocalities.setAdapter(localitiesAdapter);
//    }

    private void checkStoragePermission() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(InitialClinicCreator.this,
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
                                        InitialClinicCreator.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE_CONSTANT);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ACCESS_STORAGE_CONSTANT);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_STORAGE_CONSTANT)   {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                /* SHOW THE DIALOG */
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setIcon(R.drawable.ic_info_outline_black_24dp);
                builder.setTitle("Permission Denied");
                builder.setMessage("\nSince the storage permission was denied, you cannot choose the Clinic's Logo. The Logo is compulsory field and hence the new Clinic cannot be created...");
                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_LOCATION) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    CLINIC_LATITUDE = bundle.getDouble("LATITUDE");
                    CLINIC_LONGITUDE = bundle.getDouble("LONGITUDE");
//                    Log.e("COORDS", CLINIC_LATITUDE + " " + CLINIC_LONGITUDE);
                }

                /* GET THE APPROXIMATE ADDRESS FOR DISPLAY */
                try {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(this, Locale.getDefault());
                    addresses = geocoder.getFromLocation(CLINIC_LATITUDE, CLINIC_LONGITUDE, 1);
                    String address = addresses.get(0).getAddressLine(0);
                    if (!TextUtils.isEmpty(address)) {
                        txtLocation.setText(address);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
//                Log.e("SIZE", String.valueOf(imageFiles.size()));
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
                    .setQuality(100)
                    .setCompressFormat(Bitmap.CompressFormat.PNG)
                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath())
                    .compressToFile(imageFiles.get(0));
            Uri uri = Uri.fromFile(compressedFile);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            imgvwLogo.setImageBitmap(bitmap);
            imgvwLogo.setScaleType(ImageView.ScaleType.CENTER_CROP);

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
                LOGO_URI = Uri.fromFile(file);
//                Log.e("URI", String.valueOf(LOGO_URI));
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