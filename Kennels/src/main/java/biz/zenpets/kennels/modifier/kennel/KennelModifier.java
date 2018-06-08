package biz.zenpets.kennels.modifier.kennel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.crashlytics.android.Crashlytics;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import biz.zenpets.kennels.R;
import biz.zenpets.kennels.utils.adapters.capacity.PetCapacityAdapter;
import biz.zenpets.kennels.utils.models.helpers.ZenApiClient;
import biz.zenpets.kennels.utils.models.kennels.Kennel;
import biz.zenpets.kennels.utils.models.kennels.KennelsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KennelModifier extends AppCompatActivity {

    /** THE INCOMING KENNEL ID **/
    String KENNEL_ID = null;

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int ACCESS_STORAGE_CONSTANT = 201;

    /** DATA TYPES TO HOLD THE KENNEL DETAILS **/
    String KENNEL_NAME = null;
    String KENNEL_COVER_PHOTO = null;
    Uri KENNEL_COVER_PHOTO_URI = null;
    String KENNEL_COVER_PHOTO_FILE_NAME = null;
    String KENNEL_ADDRESS = null;
    String KENNEL_PIN_CODE = null;
    String COUNTRY_ID = "51";
    String COUNTRY_NAME = null;
    String STATE_ID = null;
    String STATE_NAME = null;
    String CITY_ID = null;
    String CITY_NAME = null;
    Double KENNEL_LATITUDE = null;
    Double KENNEL_LONGITUDE = null;
    String KENNEL_PHONE_PREFIX_1 = "91";
    String KENNEL_PHONE_NUMBER_1 = null;
    String KENNEL_PHONE_PREFIX_2 = "91";
    String KENNEL_PHONE_NUMBER_2 = null;
    String KENNEL_PET_CAPACITY = null;

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog progressDialog;

    /** A PET CAPACITY LIST INSTANCE **/
    private List<String> arrCapacity = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.scrollDetails) ScrollView scrollDetails;
    @BindView(R.id.inputKennelID) TextInputLayout inputKennelID;
    @BindView(R.id.edtKennelID) TextInputEditText edtKennelID;
    @BindView(R.id.inputKennelName) TextInputLayout inputKennelName;
    @BindView(R.id.edtKennelName) TextInputEditText edtKennelName;
    @BindView(R.id.inputAddress) TextInputLayout inputAddress;
    @BindView(R.id.edtAddress) TextInputEditText edtAddress;
    @BindView(R.id.inputPinCode) TextInputLayout inputPinCode;
    @BindView(R.id.edtPinCode) TextInputEditText edtPinCode;
    @BindView(R.id.txtCountryName) TextView txtCountryName;
    @BindView(R.id.txtStateName) TextView txtStateName;
    @BindView(R.id.txtCityName) TextView txtCityName;
    @BindView(R.id.mapKennel) MapView mapKennel;
    @BindView(R.id.inputPhoneNumber1) TextInputLayout inputPhoneNumber1;
    @BindView(R.id.edtPhoneNumber1) TextInputEditText edtPhoneNumber1;
    @BindView(R.id.inputPhoneNumber2) TextInputLayout inputPhoneNumber2;
    @BindView(R.id.edtPhoneNumber2) TextInputEditText edtPhoneNumber2;
    @BindView(R.id.spnPetCapacity) Spinner spnPetCapacity;
    @BindView(R.id.imgvwKennelCoverPhoto) ImageView imgvwKennelCoverPhoto;

    /** UPDATE THE MAP (THE LATITUDE AND LONGITUDE REALLY) **/
    @OnClick(R.id.txtUpdateMap) void updateMap()    {
    }

    /** SELECT THE KENNEL'S COVER PHOTO **/
    @OnClick(R.id.imgvwKennelCoverPhoto) void selectCoverPhoto() {
        /* CHECK STORAGE PERMISSION */
        checkStoragePermission();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kennel_modifier);
        ButterKnife.bind(this);
        mapKennel.onCreate(savedInstanceState != null ? savedInstanceState.getBundle("kennel_map_save_state") : null);
        mapKennel.onResume();
        mapKennel.setClickable(false);

        /* THE EASY IMAGE CONFIGURATION */
        EasyImage.configuration(this)
                .setImagesFolderName("Zen Pets")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(true)
                .setAllowMultiplePickInGallery(false);

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* POPULATE THE PET CAPACITY SPINNER */
        String[] strServes = getResources().getStringArray(R.array.pet_capacity);
        arrCapacity = Arrays.asList(strServes);
        spnPetCapacity.setAdapter(new PetCapacityAdapter(
                KennelModifier.this,
                R.layout.pet_capacity_row,
                arrCapacity));

        /* SELECT THE PET CAPACITY */
        spnPetCapacity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KENNEL_PET_CAPACITY = arrCapacity.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /***** FETCH THE KENNEL DETAILS *****/
    private void fetchKennelDetails() {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<Kennel> call = api.fetchKennelDetails(KENNEL_ID);
        call.enqueue(new Callback<Kennel>() {
            @Override
            public void onResponse(Call<Kennel> call, Response<Kennel> response) {
                Log.e("RAW", String.valueOf(response.raw()));
                Kennel kennel = response.body();
                if (kennel != null) {

                    /* GET AND SET THE KENNEL ID */
                    if (kennel.getKennelID() != null)   {
                        edtKennelID.setText(kennel.getKennelID());
                    }

                    /* GET AND SET THE KENNEL NAME */
                    KENNEL_NAME = kennel.getKennelName();
                    if (KENNEL_NAME != null) {
                        edtKennelName.setText(KENNEL_NAME);
                    }

                    /* GET AND SET THE KENNEL ADDRESS */
                    KENNEL_ADDRESS = kennel.getKennelAddress();
                    if (KENNEL_ADDRESS != null)  {
                        edtAddress.setText(KENNEL_ADDRESS);
                    }

                    /* GET AND SET THE KENNEL PIN CODE */
                    KENNEL_PIN_CODE = kennel.getKennelPinCode();
                    if (KENNEL_PIN_CODE != null)  {
                        edtPinCode.setText(KENNEL_PIN_CODE);
                    }

                    /* GET AND SET THE COUNTRY, STATE AND CITY NAMES */
                    COUNTRY_ID = kennel.getCountryID();
                    COUNTRY_NAME = kennel.getCountryName();
                    STATE_ID = kennel.getStateID();
                    STATE_NAME = kennel.getStateName();
                    CITY_ID = kennel.getCityID();
                    CITY_NAME = kennel.getCityName();

                    if (COUNTRY_NAME != null && STATE_NAME != null && CITY_NAME != null)   {
                        txtCountryName.setText(COUNTRY_NAME);
                        txtStateName.setText(STATE_NAME);
                        txtCityName.setText(CITY_NAME);
                    }

                    /* GET AND SET THE KENNEL'S PHONE NUMBER #1 */
                    KENNEL_PHONE_NUMBER_1 = kennel.getKennelPhoneNumber1();
                    if (KENNEL_PHONE_NUMBER_1 != null
                            && !KENNEL_PHONE_NUMBER_1.equalsIgnoreCase("")
                            && !KENNEL_PHONE_NUMBER_1.equalsIgnoreCase("null")) {
                        edtPhoneNumber1.setText(KENNEL_PHONE_NUMBER_1);
                    }

                    /* GET AND SET THE KENNEL'S PHONE NUMBER #2 */
                    KENNEL_PHONE_NUMBER_2 = kennel.getKennelPhoneNumber2();
                    if (KENNEL_PHONE_NUMBER_2 != null
                            && !KENNEL_PHONE_NUMBER_2.equalsIgnoreCase("")
                            && !KENNEL_PHONE_NUMBER_2.equalsIgnoreCase("null")) {
                        edtPhoneNumber2.setText(KENNEL_PHONE_NUMBER_2);
                    }

                    /* GET AND SET THE PET CAPACITY */
                    KENNEL_PET_CAPACITY = kennel.getKennelPetCapacity();
                    if (KENNEL_PET_CAPACITY != null)  {
                        int intCapacityPosition = getCapacityIndex(arrCapacity, KENNEL_PET_CAPACITY);
                        spnPetCapacity.setSelection(intCapacityPosition);
                    }

                    /* GET AND SET THE KENNEL'S COVER PHOTO */
                    KENNEL_COVER_PHOTO = kennel.getKennelCoverPhoto();
                    if (KENNEL_COVER_PHOTO != null
                            && !KENNEL_COVER_PHOTO.equalsIgnoreCase("")
                            && !KENNEL_COVER_PHOTO.equalsIgnoreCase("null")) {
                        Uri uri = Uri.parse(KENNEL_COVER_PHOTO);
                        imgvwKennelCoverPhoto.setImageURI(uri);
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.empty_graphic)
                                .build();
                        imgvwKennelCoverPhoto.setImageURI(request.getSourceUri());
                    }

                    /* GET AND SET THE KENNEL MAP */
                    KENNEL_LATITUDE = Double.valueOf(kennel.getKennelLatitude());
                    KENNEL_LONGITUDE = Double.valueOf(kennel.getKennelLongitude());
                    if (KENNEL_LATITUDE != null && KENNEL_LONGITUDE != null)  {
                        final LatLng latLng = new LatLng(KENNEL_LATITUDE, KENNEL_LONGITUDE);
                        mapKennel.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                googleMap.getUiSettings().setMapToolbarEnabled(false);
                                googleMap.getUiSettings().setAllGesturesEnabled(false);
                                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                googleMap.setBuildingsEnabled(true);
                                googleMap.setTrafficEnabled(false);
                                googleMap.setIndoorEnabled(false);
                                MarkerOptions options = new MarkerOptions();
                                options.position(latLng);
                                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                Marker mMarker = googleMap.addMarker(options);
                                googleMap.addMarker(options);

                                /* MOVE THE MAP CAMERA */
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMarker.getPosition(), 10));
                                googleMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
                            }
                        });
                    }

                    /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                    linlaProgress.setVisibility(View.GONE);
                } else {
                    /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                    linlaProgress.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Kennel> call, Throwable t) {
                Log.e("DETAILS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE CAPACITY POSITION *****/
    private int getCapacityIndex(List<String> arrCapacity, String kennelPetCapacity) {
        int index = 0;
        for (int i =0; i < arrCapacity.size(); i++) {
            if (arrCapacity.get(i).equalsIgnoreCase(kennelPetCapacity))   {
                index = i;
                break;
            }
        }
        return index;
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("KENNEL_ID"))    {
            KENNEL_ID = bundle.getString("KENNEL_ID");
            if (KENNEL_ID != null)  {
                /* SHOW THE PROGRESS AND FETCH THE KENNEL DETAILS */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchKennelDetails();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Modify Kennel Listing";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(strTitle);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(KennelModifier.this);
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

        /* VERIFY THE KENNEL DETAILS */
        if (TextUtils.isEmpty(KENNEL_NAME)) {
            inputKennelName.setError("Provide the Kennel's name");
            inputKennelName.setErrorEnabled(true);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_ADDRESS))   {
            inputAddress.setError("Provide the Kennel's address");
            inputAddress.setErrorEnabled(true);
            inputKennelName.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_PIN_CODE))  {
            inputPinCode.setError("Provide the Pin Code");
            inputPinCode.setErrorEnabled(true);
            inputKennelName.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_PHONE_NUMBER_1))    {
            inputPhoneNumber1.setError("Provide at least 1 Phone Number");
            inputPhoneNumber1.setErrorEnabled(true);
            inputKennelName.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        }

        /* CHECK IF THE KENNEL COVER IS BEING UPDATE AND THEN SET THE KENNEL COVER FILE NAME */
        if (KENNEL_COVER_PHOTO_URI != null && !TextUtils.isEmpty(KENNEL_NAME)) {
            KENNEL_COVER_PHOTO_FILE_NAME = KENNEL_ID + "_" + KENNEL_NAME.replaceAll(" ", "_").toLowerCase().trim();

            /* SHOW THE PROGRESS DIALOG **/
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Updating the Kennel's listing...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();

            /* UPDATE THE KENNEL COVER PHOTO */
            updateKennelCover();
        } else {
            /* SET THE KENNEL COVER TO NULL */
            KENNEL_COVER_PHOTO_FILE_NAME = null;

            /* SHOW THE PROGRESS DIALOG **/
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Updating the Kennel's listing...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();

            /* UPDATE THE KENNEL COVER PHOTO */
            updateKennelListing();
        }
    }

    /***** UPDATE THE KENNEL COVER PHOTO *****/
    private void updateKennelCover() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference refStorage = storageReference.child("Kennel Covers").child(KENNEL_COVER_PHOTO_FILE_NAME);
        refStorage.putFile(KENNEL_COVER_PHOTO_URI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadURL = taskSnapshot.getDownloadUrl();
                Log.e("URL", String.valueOf(downloadURL));
                KENNEL_COVER_PHOTO = String.valueOf(downloadURL);
                if (KENNEL_COVER_PHOTO != null)    {
                    /* UPDATE THE KENNEL'S LISTING */
                    updateKennelListing();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(
                            getApplicationContext(),
                            "There was a problem creating your new account. Please try again by clicking the Save button.",
                            Toast.LENGTH_LONG).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.e("UPLOAD EXCEPTION", e.toString());
                Crashlytics.logException(e);
            }
        });
    }

    /** UPDATE THE KENNEL'S LISTING **/
    private void updateKennelListing() {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<Kennel> call = api.updateKennel(
                KENNEL_ID, KENNEL_NAME, KENNEL_COVER_PHOTO, KENNEL_ADDRESS, KENNEL_PIN_CODE,
                COUNTRY_ID, STATE_ID, CITY_ID, String.valueOf(KENNEL_LATITUDE), String.valueOf(KENNEL_LONGITUDE),
                KENNEL_PHONE_PREFIX_1, KENNEL_PHONE_NUMBER_1, KENNEL_PHONE_PREFIX_2, KENNEL_PHONE_NUMBER_2,
                KENNEL_PET_CAPACITY
        );
        call.enqueue(new Callback<Kennel>() {
            @Override
            public void onResponse(Call<Kennel> call, Response<Kennel> response) {
                if (response.isSuccessful())    {
                    progressDialog.dismiss();
                    Intent success = new Intent();
                    setResult(RESULT_OK, success);
                    Toast.makeText(getApplicationContext(), "Updated successfully...", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Update failed...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Kennel> call, Throwable t) {
                progressDialog.dismiss();
                Crashlytics.logException(t);
                Log.e("UPDATE FAILURE", t.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapKennel.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapKennel.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapKennel.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapKennel.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapKennel.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Bundle bundle = new Bundle(outState);
        mapKennel.onSaveInstanceState(bundle);
        outState.putBundle("kennel_map_save_state", bundle);
        super.onSaveInstanceState(outState);
    }

    /***** CHECK STORAGE PERMISSION *****/
    private void checkStoragePermission() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(KennelModifier.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)   {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))    {
                /* SHOW THE DIALOG */
                new MaterialDialog.Builder(this)
                        .icon(ContextCompat.getDrawable(this, R.drawable.ic_info_black_24dp))
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
                                        KennelModifier.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE_CONSTANT);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ACCESS_STORAGE_CONSTANT);
            }
        } else {
            final BottomSheetDialog sheetDialog = new BottomSheetDialog(KennelModifier.this);
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
                    EasyImage.openGallery(KennelModifier.this, 0);
                }
            });

            /* SELECT A CAMERA IMAGE */
            linlaCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sheetDialog.dismiss();
                    EasyImage.openCamera(KennelModifier.this, 0);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_STORAGE_CONSTANT)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                final BottomSheetDialog sheetDialog = new BottomSheetDialog(KennelModifier.this);
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
                        EasyImage.openGallery(KennelModifier.this, 0);
                    }
                });

                /* SELECT A CAMERA IMAGE */
                linlaCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheetDialog.dismiss();
                        EasyImage.openCamera(KennelModifier.this, 0);
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
                KENNEL_COVER_PHOTO_URI = Uri.fromFile(file);
                Log.e("URI", String.valueOf(KENNEL_COVER_PHOTO_URI));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }
}