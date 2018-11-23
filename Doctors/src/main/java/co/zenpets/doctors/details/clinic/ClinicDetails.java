package co.zenpets.doctors.details.clinic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.zenpets.doctors.R;
import co.zenpets.doctors.creator.clinic.images.ClinicImagesManager;
import co.zenpets.doctors.modifier.clinic.ClinicLogoUpdater;
import co.zenpets.doctors.modifier.clinic.ClinicModifier;
import co.zenpets.doctors.utils.AppPrefs;
import co.zenpets.doctors.utils.TypefaceSpan;
import co.zenpets.doctors.utils.adapters.clinics.ClinicImagesDetailsAdapter;
import co.zenpets.doctors.utils.adapters.doctors.DoctorsAdapter;
import co.zenpets.doctors.utils.helpers.ZenApiClient;
import co.zenpets.doctors.utils.models.clinics.ClinicData;
import co.zenpets.doctors.utils.models.clinics.details.ClinicDetailsAPI;
import co.zenpets.doctors.utils.models.clinics.images.ClinicImagesAPI;
import co.zenpets.doctors.utils.models.clinics.images.ImageData;
import co.zenpets.doctors.utils.models.clinics.images.ImagesData;
import co.zenpets.doctors.utils.models.doctors.Doctor;
import co.zenpets.doctors.utils.models.doctors.Doctors;
import co.zenpets.doctors.utils.models.doctors.DoctorsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClinicDetails extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE INCOMING CLINIC ID **/
    private String CLINIC_ID = null;

    /** THE LOGGED IN DOCTOR'S ID**/
    private String LOGGED_IN_DOCTOR_ID = null;

    /** THE CLINIC DOCTORS AND IMAGES ARRAY LIST **/
    private ArrayList<Doctor> arrDoctors = new ArrayList<>();
    private ArrayList<ImageData> arrImages = new ArrayList<>();

    /* DATA TYPES TO HOLD THE CLINIC DETAILS */
    private String DOCTOR_ID = null;
    private String CLINIC_NAME = null;
    private String CLINIC_ADDRESS = null;
    private String CLINIC_LANDMARK = null;
    private String CLINIC_PIN_CODE = null;
    private Double CLINIC_LATITUDE = null;
    private Double CLINIC_LONGITUDE = null;
    private String CLINIC_LOGO = null;
    private String CLINIC_PHONE_1 = null;
    private String CLINIC_PHONE_2 = null;
    private String COUNTRY_ID = null;
    private String COUNTRY_NAME = null;
    private String STATE_ID = null;
    private String STATE_NAME = null;
    private String CITY_ID = null;
    private String CITY_NAME = null;
    private String LOCALITY_ID = null;
    private String LOCALITY_NAME = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.imgvwClinicLogo) SimpleDraweeView imgvwClinicLogo;
    @BindView(R.id.txtClinicName) AppCompatTextView txtClinicName;
    @BindView(R.id.txtClinicAddress) AppCompatTextView txtClinicAddress;
    @BindView(R.id.clinicMap) MapView clinicMap;
    @BindView(R.id.txtPhone1) AppCompatTextView txtPhone1;
    @BindView(R.id.txtPhone2) AppCompatTextView txtPhone2;
    @BindView(R.id.linlaDoctors) LinearLayout linlaDoctors;
    @BindView(R.id.listDoctors) RecyclerView listDoctors;
    @BindView(R.id.linlaNoDoctors) LinearLayout linlaNoDoctors;
    @BindView(R.id.linlaClinicImages) LinearLayout linlaClinicImages;
    @BindView(R.id.listClinicImages) RecyclerView listClinicImages;
    @BindView(R.id.linlaNoClinicImages) LinearLayout linlaNoClinicImages;
    @BindView(R.id.txtManageImages) AppCompatTextView txtManageImages;

    /** EDIT THE CLINIC LOGO **/
    @OnClick(R.id.linlaLogoEdit) protected void editLogo()    {
        final BottomSheetDialog sheetDialog = new BottomSheetDialog(ClinicDetails.this);
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.logo_updater_sheet, null);
        sheetDialog.setContentView(view);
        sheetDialog.show();

        /* CAST THE CHOOSER ELEMENTS */
        LinearLayout linlaUpload = view.findViewById(R.id.linlaUpload);
//        LinearLayout linlaView = view.findViewById(R.id.linlaView);

        /* UPLOAD A NEW CLINIC LOGO */
        linlaUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetDialog.dismiss();
                Intent intent = new Intent(ClinicDetails.this, ClinicLogoUpdater.class);
                intent.putExtra("CLINIC_ID", CLINIC_ID);
                intent.putExtra("CLINIC_NAME", CLINIC_NAME);
                startActivityForResult(intent, 101);
            }
        });

//        /* SELECT A CAMERA IMAGE */
//        linlaView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sheetDialog.dismiss();
//            }
//        });
    }
    
    /** MANAGE THE CLINIC IMAGES **/
    @OnClick(R.id.txtManageImages) protected void manageImages()    {
        Intent intent = new Intent(ClinicDetails.this, ClinicImagesManager.class);
        intent.putExtra("CLINIC_ID", CLINIC_ID);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clinic_details);
        ButterKnife.bind(this);
        clinicMap.onCreate(savedInstanceState);
        clinicMap.onResume();
        clinicMap.setClickable(false);

        /* GET THE LOGGED IN DOCTOR'S ID */
        LOGGED_IN_DOCTOR_ID = getApp().getDoctorID();
        if (LOGGED_IN_DOCTOR_ID != null)    {
            /* GET THE INCOMING DATA */
            getIncomingData();
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info", Toast.LENGTH_SHORT).show();
            finish();
        }

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* CONFIGURE THE RECYCLER VIEW **/
        configRecycler();
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("CLINIC_ID"))    {
            CLINIC_ID = bundle.getString("CLINIC_ID");
            if (CLINIC_ID != null)  {
                /* FETCH THE CLINIC DETAILS */
                fetchClinicDetails();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** FETCH THE CLINIC DETAILS *****/
    private void fetchClinicDetails() {
        ClinicDetailsAPI apiInterface = ZenApiClient.getClient().create(ClinicDetailsAPI.class);
        Call<ClinicData> call = apiInterface.fetchClinicDetails(CLINIC_ID);
        call.enqueue(new Callback<ClinicData>() {
            @Override
            public void onResponse(@NonNull Call<ClinicData> call, @NonNull Response<ClinicData> response) {
                DOCTOR_ID = response.body().getDoctorID();
                CLINIC_NAME = response.body().getClinicName();
                CLINIC_ADDRESS = response.body().getClinicAddress();
                CLINIC_LANDMARK = response.body().getClinicLandmark();
                CLINIC_PIN_CODE = response.body().getClinicPinCode();
                CLINIC_LATITUDE = Double.valueOf(response.body().getClinicLatitude());
                CLINIC_LONGITUDE = Double.valueOf(response.body().getClinicLongitude());
                CLINIC_LOGO = response.body().getClinicLogo();
                CLINIC_PHONE_1 = response.body().getClinicPhone1();
                CLINIC_PHONE_2 = response.body().getClinicPhone2();
                COUNTRY_ID = response.body().getCountryID();
                COUNTRY_NAME = response.body().getCountryName();
                STATE_ID = response.body().getStateID();
                STATE_NAME = response.body().getStateName();
                CITY_ID = response.body().getCityID();
                CITY_NAME = response.body().getCityName();
                LOCALITY_ID = response.body().getLocalityID();
                LOCALITY_NAME = response.body().getLocalityName();

                /* SET THE CLINIC LOGO */
                Uri uri = Uri.parse(CLINIC_LOGO);
                imgvwClinicLogo.setImageURI(uri);

                /* SET THE CLINIC NAME */
                txtClinicName.setText(CLINIC_NAME);

                /* SET THE CLINIC ADDRESS */
                txtClinicAddress.setText(CLINIC_ADDRESS + "\n" + CLINIC_LANDMARK + ",\n" + CITY_NAME + ", " + STATE_NAME + ", " + CLINIC_PIN_CODE);

                /* SET THE CLINIC PHONE 1 */
                if (CLINIC_PHONE_1 != null && !CLINIC_PHONE_1.equals(""))   {
                    txtPhone1.setText(CLINIC_PHONE_1);
                } else {
                    txtPhone1.setText("N.A");
                }

                /* SET THE CLINIC PHONE 2 */
                if (CLINIC_PHONE_2 != null && !CLINIC_PHONE_2.equals(""))   {
                    txtPhone2.setText(CLINIC_PHONE_2);
                } else {
                    txtPhone2.setText("N.A");
                }

                /* SET THE CLINIC LOCATION ON THE MAP */
                final LatLng latLng = new LatLng(CLINIC_LATITUDE, CLINIC_LONGITUDE);
                clinicMap.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(ClinicDetails.this, R.raw.zen_map_style);
                        googleMap.setMapStyle(mapStyleOptions);
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

                /* FETCH THE CLINIC IMAGES */
                fetchClinicImages();
            }

            @Override
            public void onFailure(@NonNull Call<ClinicData> call, @NonNull Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE CLINIC IMAGES *****/
    private void fetchClinicImages() {
        ClinicImagesAPI apiInterface = ZenApiClient.getClient().create(ClinicImagesAPI.class);
        Call<ImagesData> call = apiInterface.fetchClinicImages(CLINIC_ID);
        call.enqueue(new Callback<ImagesData>() {
            @Override
            public void onResponse(@NonNull Call<ImagesData> call, @NonNull Response<ImagesData> response) {
                arrImages = response.body().getImages();
                if (arrImages != null && arrImages.size() > 0)  {
                    /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                    linlaClinicImages.setVisibility(View.VISIBLE);
                    linlaNoClinicImages.setVisibility(View.GONE);

                    /* SET THE SERVICES ADAPTER TO THE RECYCLER VIEW */
                    listClinicImages.setAdapter(new ClinicImagesDetailsAdapter(ClinicDetails.this, arrImages));
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaNoClinicImages.setVisibility(View.VISIBLE);
                    linlaClinicImages.setVisibility(View.GONE);
                }

                /* FETCH THE LIST OF DOCTOR'S */
                fetchClinicDoctors();
            }

            @Override
            public void onFailure(@NonNull Call<ImagesData> call, @NonNull Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    private void fetchClinicDoctors() {
        /* CLEAR THE ARRAY LIST */
        if (arrDoctors != null)
            arrDoctors.clear();

        DoctorsAPI api = ZenApiClient.getClient().create(DoctorsAPI.class);
        Call<Doctors> call = api.fetchClinicDoctors(CLINIC_ID);
        call.enqueue(new Callback<Doctors>() {
            @Override
            public void onResponse(@NonNull Call<Doctors> call, @NonNull Response<Doctors> response) {
                if (response.body() != null && response.body().getDoctors() != null)    {
                    arrDoctors = response.body().getDoctors();
                    if (arrDoctors.size() > 0)  {
                        linlaDoctors.setVisibility(View.VISIBLE);
                        linlaNoDoctors.setVisibility(View.GONE);
                        listDoctors.setAdapter(new DoctorsAdapter(ClinicDetails.this, arrDoctors));
                    } else {
                        linlaNoDoctors.setVisibility(View.VISIBLE);
                        linlaDoctors.setVisibility(View.GONE);
                    }
                } else {
                    linlaNoDoctors.setVisibility(View.VISIBLE);
                    linlaDoctors.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Doctors> call, @NonNull Throwable t) {
//                Log.e("DOCTOR FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION FOR THE DOCTORS LIST */
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listDoctors.setLayoutManager(manager);
        listDoctors.setHasFixedSize(true);

        /* SET THE DOCTORS ADAPTER */
        listDoctors.setAdapter(new DoctorsAdapter(this, arrDoctors));

        /* CONFIGURE THE CLINIC IMAGES LIST */
        LinearLayoutManager llmClinicImages = new LinearLayoutManager(this);
        llmClinicImages.setOrientation(LinearLayoutManager.HORIZONTAL);
        llmClinicImages.setAutoMeasureEnabled(true);
        listClinicImages.setLayoutManager(llmClinicImages);
        listClinicImages.setHasFixedSize(true);
        listClinicImages.setNestedScrollingEnabled(false);

        /* SET THE CLINIC IMAGES ADAPTER */
        listClinicImages.setAdapter(new ClinicImagesDetailsAdapter(this, arrImages));
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Clinic Details";
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
        MenuInflater inflater = new MenuInflater(ClinicDetails.this);
        inflater.inflate(R.menu.activity_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menuEdit:
                /* EDIT THE CLINIC */
                Intent intent = new Intent(ClinicDetails.this, ClinicModifier.class);
                intent.putExtra("CLINIC_ID", CLINIC_ID);
                startActivityForResult(intent, 102);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)  {
            if (requestCode == 101 || requestCode == 102)   {
                /* FETCH THE CLINIC DETAILS */
                fetchClinicDetails();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        clinicMap.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        clinicMap.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        clinicMap.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clinicMap.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        clinicMap.onLowMemory();
    }
}