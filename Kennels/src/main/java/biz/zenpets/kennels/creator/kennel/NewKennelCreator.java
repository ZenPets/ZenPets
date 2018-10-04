package biz.zenpets.kennels.creator.kennel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import biz.zenpets.kennels.R;
import biz.zenpets.kennels.utils.AppPrefs;
import biz.zenpets.kennels.utils.TypefaceSpan;
import biz.zenpets.kennels.utils.adapters.capacity.PetCapacityAdapter;
import biz.zenpets.kennels.utils.adapters.location.CitiesAdapter;
import biz.zenpets.kennels.utils.adapters.location.StatesAdapter;
import biz.zenpets.kennels.utils.helpers.LocationPickerActivity;
import biz.zenpets.kennels.utils.models.helpers.ZenApiClient;
import biz.zenpets.kennels.utils.models.location.CitiesData;
import biz.zenpets.kennels.utils.models.location.CityData;
import biz.zenpets.kennels.utils.models.location.LocationAPI;
import biz.zenpets.kennels.utils.models.location.StateData;
import biz.zenpets.kennels.utils.models.location.StatesData;
import biz.zenpets.kennels.utils.models.test.TestKennel;
import biz.zenpets.kennels.utils.models.test.TestKennelsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewKennelCreator extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE KENNEL OWNER ID **/
    String KENNEL_OWNER_ID = null;

    /** PERMISSION REQUEST CONSTANT **/
    private static final int ACCESS_STORAGE_CONSTANT = 201;

    /** THE LOCATION REQUEST CODE **/
    private final int REQUEST_LOCATION = 1;

    /** DATA TYPES TO HOLD THE KENNEL DETAILS **/
    String KENNEL_ID = null;
    String KENNEL_NAME = null;
    String KENNEL_COVER_PHOTO = null;
    Uri KENNEL_COVER_PHOTO_URI = null;
    String KENNEL_COVER_PHOTO_FILE_NAME = null;
    String KENNEL_ADDRESS = null;
    String KENNEL_PIN_CODE = null;
    String COUNTRY_ID = "51";
    String STATE_ID = null;
    String CITY_ID = null;
    Double KENNEL_LATITUDE = null;
    Double KENNEL_LONGITUDE = null;
    String KENNEL_PHONE_PREFIX_1 = "91";
    String KENNEL_PHONE_NUMBER_1 = null;
    String KENNEL_PHONE_PREFIX_2 = "91";
    String KENNEL_PHONE_NUMBER_2 = null;
    String KENNEL_SMALL_CAPACITY = null;
    String KENNEL_MEDIUM_CAPACITY = null;
    String KENNEL_LARGE_CAPACITY = null;
    String KENNEL_X_LARGE_CAPACITY = null;

    /** ADDITIONAL KENNEL CHARGES **/
    String ADDITIONAL_KENNEL_COST = "1000";

    /** THE STATES ADAPTER AND ARRAY LIST **/
    private ArrayList<StateData> arrStates = new ArrayList<>();

    /** CITIES ADAPTER AND ARRAY LIST **/
    private ArrayList<CityData> arrCities = new ArrayList<>();

    /** PET CAPACITY LIST INSTANCES **/
    private List<String> arrSmallCapacity = new ArrayList<>();
    private List<String> arrMediumCapacity = new ArrayList<>();
    private List<String> arrLargeCapacity = new ArrayList<>();
    private List<String> arrXLargeCapacity = new ArrayList<>();

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog progressDialog;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.inputKennelName) TextInputLayout inputKennelName;
    @BindView(R.id.edtKennelName) TextInputEditText edtKennelName;
    @BindView(R.id.inputAddress) TextInputLayout inputAddress;
    @BindView(R.id.edtAddress) TextInputEditText edtAddress;
    @BindView(R.id.inputPinCode) TextInputLayout inputPinCode;
    @BindView(R.id.edtPinCode) TextInputEditText edtPinCode;
    @BindView(R.id.spnState) Spinner spnState;
    @BindView(R.id.spnCity) Spinner spnCity;
    @BindView(R.id.txtLocation) TextView txtLocation;
    @BindView(R.id.inputPhoneNumber1) TextInputLayout inputPhoneNumber1;
    @BindView(R.id.edtPhoneNumber1) TextInputEditText edtPhoneNumber1;
    @BindView(R.id.inputPhoneNumber2) TextInputLayout inputPhoneNumber2;
    @BindView(R.id.edtPhoneNumber2) TextInputEditText edtPhoneNumber2;
    @BindView(R.id.spnSmallCapacity) Spinner spnSmallCapacity;
    @BindView(R.id.spnMediumCapacity) Spinner spnMediumCapacity;
    @BindView(R.id.spnLargeCapacity) Spinner spnLargeCapacity;
    @BindView(R.id.spnXLargeCapacity) Spinner spnXLargeCapacity;
    @BindView(R.id.imgvwKennelCoverPhoto) SimpleDraweeView imgvwKennelCoverPhoto;

    /** SELECT THE KENNEL'S LOCATION ON THE MAP **/
    @OnClick(R.id.btnLocationPicker) void selectLocation()  {
        Intent intent = new Intent(this, LocationPickerActivity.class);
        startActivityForResult(intent, REQUEST_LOCATION);
    }

    /** PICK THE KENNEL'S COVER PHOTO **/
    @OnClick(R.id.imgvwKennelCoverPhoto) void selectCoverPhoto()    {
        /* CHECK STORAGE PERMISSION */
        checkStoragePermission();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kennel_creator_new);
        ButterKnife.bind(this);

        /* THE EASY IMAGE CONFIGURATION */
        EasyImage.configuration(this)
                .setImagesFolderName("Zen Pets")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(true)
                .setAllowMultiplePickInGallery(false);

        /* GET THE KENNEL OWNER'S ID */
        KENNEL_OWNER_ID = getApp().getKennelOwnerID();

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* SHOW THE PROGRESS AND FETCH THE LIST OF STATES */
        fetchStates();

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

        /* POPULATE THE SMALL CAPACITY SPINNER */
        String[] strSmallCapacity = getResources().getStringArray(R.array.pet_capacity);
        arrSmallCapacity = Arrays.asList(strSmallCapacity);
        spnSmallCapacity.setAdapter(new PetCapacityAdapter(
                NewKennelCreator.this,
                R.layout.pet_capacity_row,
                arrSmallCapacity));

        /* SELECT THE SMALL PET CAPACITY */
        spnSmallCapacity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KENNEL_SMALL_CAPACITY = arrSmallCapacity.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* POPULATE THE MEDIUM CAPACITY SPINNER */
        String[] strMediumCapacity = getResources().getStringArray(R.array.pet_capacity);
        arrMediumCapacity = Arrays.asList(strMediumCapacity);
        spnMediumCapacity.setAdapter(new PetCapacityAdapter(
                NewKennelCreator.this,
                R.layout.pet_capacity_row,
                arrMediumCapacity));

        /* SELECT THE MEDIUM PET CAPACITY */
        spnMediumCapacity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KENNEL_MEDIUM_CAPACITY = arrMediumCapacity.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* POPULATE THE LARGE CAPACITY SPINNER */
        String[] strLargeCapacity = getResources().getStringArray(R.array.pet_capacity);
        arrLargeCapacity = Arrays.asList(strLargeCapacity);
        spnLargeCapacity.setAdapter(new PetCapacityAdapter(
                NewKennelCreator.this,
                R.layout.pet_capacity_row,
                arrLargeCapacity));

        /* SELECT THE LARGE PET CAPACITY */
        spnLargeCapacity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KENNEL_LARGE_CAPACITY = arrLargeCapacity.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* POPULATE THE EXTRA LARGE CAPACITY SPINNER */
        String[] strXLargeCapacity = getResources().getStringArray(R.array.pet_capacity);
        arrXLargeCapacity = Arrays.asList(strXLargeCapacity);
        spnXLargeCapacity.setAdapter(new PetCapacityAdapter(
                NewKennelCreator.this,
                R.layout.pet_capacity_row,
                arrXLargeCapacity));

        /* SELECT THE EXTRA LARGE PET CAPACITY */
        spnXLargeCapacity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KENNEL_X_LARGE_CAPACITY = arrXLargeCapacity.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /** CHECK KENNEL DETAILS **/
    private void checkKennelDetails() {
        /* HIDE THE KEYBOARD **/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtKennelName.getWindowToken(), 0);
        }

        /* COLLECT THE KENNEL DETAILS */
        KENNEL_NAME = edtKennelName.getText().toString().trim();
        KENNEL_ADDRESS = edtAddress.getText().toString().trim();
        KENNEL_PIN_CODE = edtPinCode.getText().toString().trim();
        KENNEL_PHONE_NUMBER_1 = edtPhoneNumber1.getText().toString().trim();
        KENNEL_PHONE_NUMBER_2 = edtPhoneNumber2.getText().toString().trim();

        /* GENERATE THE FILE NAME */
        if (!(KENNEL_COVER_PHOTO_URI == null) && !TextUtils.isEmpty(KENNEL_NAME))    {
            KENNEL_COVER_PHOTO_FILE_NAME = KENNEL_OWNER_ID + "_" + KENNEL_NAME.replaceAll(" ", "_").toLowerCase().trim();
        } else {
            KENNEL_COVER_PHOTO_FILE_NAME = null;
        }

        /* VERIFY THE KENNEL DETAILS */
        if (TextUtils.isEmpty(KENNEL_NAME)) {
            inputKennelName.setError("Provide the Kennel's name");
            inputKennelName.setErrorEnabled(true);
            inputKennelName.requestFocus();
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_ADDRESS))   {
            inputAddress.setError("Provide the Kennel's address");
            inputAddress.setErrorEnabled(true);
            inputAddress.requestFocus();
            inputKennelName.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_PIN_CODE))  {
            inputPinCode.setError("Provide the Pin Code");
            inputPinCode.setErrorEnabled(true);
            inputPinCode.requestFocus();
            inputKennelName.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_PHONE_NUMBER_1))    {
            inputPhoneNumber1.setError("Provide the Phone Number");
            inputPhoneNumber1.setErrorEnabled(true);
            inputPhoneNumber1.requestFocus();
            inputKennelName.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (KENNEL_LATITUDE == null || KENNEL_LONGITUDE == null) {
            inputKennelName.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            Toast.makeText(getApplicationContext(), "Please mark the Kennel's location on the Map", Toast.LENGTH_LONG).show();
        } else if (KENNEL_COVER_PHOTO_FILE_NAME == null)    {
            inputKennelName.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            Toast.makeText(getApplicationContext(), "Please select the Kennel's Cover Photo.", Toast.LENGTH_LONG).show();
        } else {
            inputKennelName.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);

            /* UPLOAD THE KENNEL'S COVER PHOTO  */
            uploadKennelCover();
        }
    }

    /** UPLOAD THE KENNEL'S COVER PHOTO  **/
    private void uploadKennelCover() {
        /* SHOW THE PROGRESS DIALOG **/
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Publishing the new Kennel...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference refStorage = storageReference.child("Kennel Covers").child(KENNEL_COVER_PHOTO_FILE_NAME);
        UploadTask uploadTask = refStorage.putFile(KENNEL_COVER_PHOTO_URI);
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
                    KENNEL_COVER_PHOTO = String.valueOf(task.getResult());
                    Log.e("KENNEL COVER PHOTO", KENNEL_COVER_PHOTO);
                    if (KENNEL_COVER_PHOTO != null)    {
                        /* UPLOAD THE NEW KENNEL LISTING */
                        uploadKennelListing();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(
                                getApplicationContext(),
                                "Problem publishing your new Kennel...",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    /** UPLOAD THE NEW KENNEL LISTING **/
    private void uploadKennelListing() {
        TestKennelsAPI api = ZenApiClient.getClient().create(TestKennelsAPI.class);
        Call<TestKennel> call = api.registerNewTestKennel(
                KENNEL_OWNER_ID, KENNEL_NAME, KENNEL_COVER_PHOTO, KENNEL_ADDRESS, KENNEL_PIN_CODE,
                COUNTRY_ID, STATE_ID, CITY_ID, String.valueOf(KENNEL_LATITUDE), String.valueOf(KENNEL_LONGITUDE),
                KENNEL_PHONE_PREFIX_1, KENNEL_PHONE_NUMBER_1, KENNEL_PHONE_PREFIX_2, KENNEL_PHONE_NUMBER_2,
                KENNEL_SMALL_CAPACITY, KENNEL_MEDIUM_CAPACITY, KENNEL_LARGE_CAPACITY, KENNEL_X_LARGE_CAPACITY,
                "Unverified"
        );
        call.enqueue(new Callback<TestKennel>() {
            @Override
            public void onResponse(Call<TestKennel> call, Response<TestKennel> response) {
                Log.e("RESPONSE", String.valueOf(response.raw()));
                if (response.isSuccessful())    {
                    /* GET THE KENNEL ID */
                    KENNEL_ID = response.body().getKennelID();

                    progressDialog.dismiss();
                    Intent success = new Intent();
                    setResult(RESULT_OK, success);
                    Toast.makeText(getApplicationContext(), "Kennel published successfully...", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Failed to publish new kennel...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TestKennel> call, Throwable t) {
                progressDialog.dismiss();
                Crashlytics.logException(t);
                Log.e("PUBLISH FAILURE", t.getMessage());
            }
        });
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "New Kennel";
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
        MenuInflater inflater = new MenuInflater(NewKennelCreator.this);
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
                /* CHECK KENNEL DETAILS */
                checkKennelDetails();
                break;
            case R.id.menuCancel:
                this.finish();
                break;
            default:
                break;
        }
        return false;
    }

    /** FETCH THE LIST OF STATES **/
    private void fetchStates() {
        LocationAPI api = ZenApiClient.getClient().create(LocationAPI.class);
        Call<StatesData> call = api.allStates(COUNTRY_ID);
        call.enqueue(new Callback<StatesData>() {
            @Override
            public void onResponse(Call<StatesData> call, Response<StatesData> response) {
                arrStates = response.body().getStates();

                /* SET THE ADAPTER TO THE STATES SPINNER */
                spnState.setAdapter(new StatesAdapter(NewKennelCreator.this, arrStates));
            }

            @Override
            public void onFailure(Call<StatesData> call, Throwable t) {
//                Log.e("STATES FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** FETCH THE LIST OF CITIES **/
    private void fetchCities() {
        LocationAPI api = ZenApiClient.getClient().create(LocationAPI.class);
        Call<CitiesData> call = api.allCities(STATE_ID);
        call.enqueue(new Callback<CitiesData>() {
            @Override
            public void onResponse(Call<CitiesData> call, Response<CitiesData> response) {
                arrCities = response.body().getCities();

                /* SET THE ADAPTER TO THE CITIES SPINNER */
                spnCity.setAdapter(new CitiesAdapter(NewKennelCreator.this, arrCities));
            }

            @Override
            public void onFailure(Call<CitiesData> call, Throwable t) {
//                Log.e("CITIES FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** CHECK STORAGE PERMISSION *****/
    private void checkStoragePermission() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(NewKennelCreator.this,
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
                                        NewKennelCreator.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE_CONSTANT);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ACCESS_STORAGE_CONSTANT);
            }
        } else {
            final BottomSheetDialog sheetDialog = new BottomSheetDialog(NewKennelCreator.this);
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
                    EasyImage.openGallery(NewKennelCreator.this, 0);
                }
            });

            /* SELECT A CAMERA IMAGE */
            linlaCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sheetDialog.dismiss();
                    EasyImage.openCamera(NewKennelCreator.this, 0);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_STORAGE_CONSTANT)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                final BottomSheetDialog sheetDialog = new BottomSheetDialog(NewKennelCreator.this);
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
                        EasyImage.openGallery(NewKennelCreator.this, 0);
                    }
                });

                /* SELECT A CAMERA IMAGE */
                linlaCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheetDialog.dismiss();
                        EasyImage.openCamera(NewKennelCreator.this, 0);
                    }
                });
            } else {
                /* SHOW THE DIALOG */
                new MaterialDialog.Builder(this)
                        .icon(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_black_24dp))
                        .title(getString(R.string.storage_permission_title))
                        .cancelable(false)
                        .content(getString(R.string.storage_permission_denied))
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
                                        NewKennelCreator.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE_CONSTANT);
                            }
                        }).show();
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
                    KENNEL_LATITUDE = bundle.getDouble("LATITUDE");
                    KENNEL_LONGITUDE = bundle.getDouble("LONGITUDE");
                }

                /* GET THE APPROXIMATE ADDRESS FOR DISPLAY */
                try {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(this, Locale.getDefault());
                    addresses = geocoder.getFromLocation(KENNEL_LATITUDE, KENNEL_LONGITUDE, 1);
                    String address = addresses.get(0).getAddressLine(0);
                    if (!TextUtils.isEmpty(address)) {
                        txtLocation.setText(address);
                    } else {
                        // TODO: DISPLAY THE COORDINATES INSTEAD
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

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
            imgvwKennelCoverPhoto.setImageURI(uri);
//            imgvwKennelCoverPhoto.setImageBitmap(bitmap);
//            imgvwKennelCoverPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);

            /* STORE THE BITMAP AS A FILE AND USE THE FILE'S URI */
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/Zen Pets");
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
                KENNEL_COVER_PHOTO_URI = Uri.fromFile(file);
//                Log.e("URI", String.valueOf(KENNEL_COVER_PHOTO_URI));
            } catch (IOException e) {
                e.printStackTrace();
                Crashlytics.logException(e);
//                Log.e("EXCEPTION", e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
//            Log.e("EXCEPTION", e.getMessage());
        }
    }
}