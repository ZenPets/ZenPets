package biz.zenpets.users.details.doctors;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.creator.appointment.AppointmentSlotCreator;
import biz.zenpets.users.creator.profile.ProfileEditor;
import biz.zenpets.users.creator.review.ReviewCreator;
import biz.zenpets.users.details.doctors.map.MapDetails;
import biz.zenpets.users.details.doctors.reviews.DoctorReviews;
import biz.zenpets.users.modifier.review.ReviewModifier;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.adapters.clinics.ClinicImagesAdapter;
import biz.zenpets.users.utils.adapters.doctors.services.ServicesAdapter;
import biz.zenpets.users.utils.adapters.reviews.ReviewsAdapter;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.clinics.images.ClinicImage;
import biz.zenpets.users.utils.models.clinics.images.ClinicImages;
import biz.zenpets.users.utils.models.clinics.images.ClinicImagesAPI;
import biz.zenpets.users.utils.models.doctors.DoctorsAPI;
import biz.zenpets.users.utils.models.doctors.list.Doctor;
import biz.zenpets.users.utils.models.doctors.modules.Qualification;
import biz.zenpets.users.utils.models.doctors.modules.Qualifications;
import biz.zenpets.users.utils.models.doctors.modules.QualificationsAPI;
import biz.zenpets.users.utils.models.doctors.modules.Service;
import biz.zenpets.users.utils.models.doctors.modules.Services;
import biz.zenpets.users.utils.models.doctors.modules.ServicesAPI;
import biz.zenpets.users.utils.models.doctors.modules.ServicesCount;
import biz.zenpets.users.utils.models.doctors.subscription.SubscriptionData;
import biz.zenpets.users.utils.models.doctors.subscription.SubscriptionsAPI;
import biz.zenpets.users.utils.models.doctors.timings.Timing;
import biz.zenpets.users.utils.models.doctors.timings.TimingsAPI;
import biz.zenpets.users.utils.models.reviews.Review;
import biz.zenpets.users.utils.models.reviews.ReviewCount;
import biz.zenpets.users.utils.models.reviews.Reviews;
import biz.zenpets.users.utils.models.reviews.ReviewsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ConstantConditions")
public class DoctorDetailsNew extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN USER'S ID **/
    private String USER_ID = null;

    /** THE INCOMING DOCTOR ID, CLINIC ID  AND THE ORIGIN LATITUDE AND LONGITUDE**/
    private String DOCTOR_ID = null;
    private String CLINIC_ID = null;
    String ORIGIN_LATITUDE = null;
    String ORIGIN_LONGITUDE = null;

    /** TODAY'S DAY **/
    private String TODAY_DAY = null;

    /** DATA TYPE TO STORE THE DATA **/
    private String CLINIC_NAME;
    private String CLINIC_ADDRESS;
    private Double CLINIC_LATITUDE;
    private Double CLINIC_LONGITUDE;
    private String CLINIC_DISTANCE;
    private String CLINIC_RATING;
    private String DOCTOR_PREFIX;
    private String DOCTOR_NAME;
    private String DOCTOR_PHONE_NUMBER = null;

    /** THE TOTAL VOTES, TOTAL LIKES AND TOTAL DISLIKES **/
    private int TOTAL_VOTES = 0;
    private int TOTAL_LIKES = 0;

    /** THE DOCTOR DAILY TIMING STRINGS **/
    private String SUN_MOR_FROM = null;
    private String SUN_MOR_TO = null;
    private String SUN_AFT_FROM = null;
    private String SUN_AFT_TO = null;
    private String MON_MOR_FROM = null;
    private String MON_MOR_TO = null;
    private String MON_AFT_FROM = null;
    private String MON_AFT_TO = null;
    private String TUE_MOR_FROM = null;
    private String TUE_MOR_TO = null;
    private String TUE_AFT_FROM = null;
    private String TUE_AFT_TO = null;
    private String WED_MOR_FROM = null;
    private String WED_MOR_TO = null;
    private String WED_AFT_FROM = null;
    private String WED_AFT_TO = null;
    private String THU_MOR_FROM = null;
    private String THU_MOR_TO = null;
    private String THU_AFT_FROM = null;
    private String THU_AFT_TO = null;
    private String FRI_MOR_FROM = null;
    private String FRI_MOR_TO = null;
    private String FRI_AFT_FROM = null;
    private String FRI_AFT_TO = null;
    private String SAT_MOR_FROM = null;
    private String SAT_MOR_TO = null;
    private String SAT_AFT_FROM = null;
    private String SAT_AFT_TO = null;

    /* THE SERVICES AND SUBSET ADAPTER AND ARRAY LISTS **/
    private ServicesAdapter servicesAdapter;
    private ArrayList<Service> arrServicesSubset = new ArrayList<>();
    private ArrayList<Service> arrServices = new ArrayList<>();

    /** THE REVIEWS AND SUBSET ADAPTER AND ARRAY LISTS **/
    private ReviewsAdapter reviewsAdapter;
    private ArrayList<Review> arrReviewsSubset = new ArrayList<>();

    /** THE CLINIC IMAGES ADAPTER AND ARRAY LIST **/
    private ClinicImagesAdapter imagesAdapter;
    private ArrayList<ClinicImage> arrImages = new ArrayList<>();

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int CALL_PHONE_CONSTANT = 200;

    /** THE DOCTOR'S SUBSCRIPTION STATUS FLAG **/
    private boolean blnSubscriptionStatus = false;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.appBar) AppBarLayout appBar;
    @BindView(R.id.toolbarLayout) CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.imgvwClinicCover) SimpleDraweeView imgvwClinicCover;
    @BindView(R.id.imgvwDoctorProfile) SimpleDraweeView imgvwDoctorProfile;
    @BindView(R.id.txtDoctorName) TextView txtDoctorName;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.txtDoctorEducation) TextView txtDoctorEducation;
    @BindView(R.id.linlaExperience) LinearLayout linlaExperience;
    @BindView(R.id.txtExperience) TextView txtExperience;
    @BindView(R.id.linlaVotes) LinearLayout linlaVotes;
    @BindView(R.id.txtVotes) TextView txtVotes;
    @BindView(R.id.txtDoctorCharges) TextView txtDoctorCharges;
    @BindView(R.id.txtClinicName) TextView txtClinicName;
    @BindView(R.id.clinicRating) AppCompatRatingBar clinicRating;
    @BindView(R.id.txtClinicAddress) TextView txtClinicAddress;
    @BindView(R.id.clinicMap) MapView clinicMap;
    @BindView(R.id.txtClinicDistance) TextView txtClinicDistance;
    @BindView(R.id.linlaTimingMorning) LinearLayout linlaTimingMorning;
    @BindView(R.id.txtMorningOpen) TextView txtMorningOpen;
    @BindView(R.id.txtTimingsMorning) TextView txtTimingsMorning;
    @BindView(R.id.txtMorningClosed) TextView txtMorningClosed;
    @BindView(R.id.linlaTimingAfternoon) LinearLayout linlaTimingAfternoon;
    @BindView(R.id.txtAfternoonOpen) TextView txtAfternoonOpen;
    @BindView(R.id.txtTimingAfternoon) TextView txtTimingAfternoon;
    @BindView(R.id.txtAfternoonClosed) TextView txtAfternoonClosed;
    @BindView(R.id.linlaReviewsProgress) LinearLayout linlaReviewsProgress;
    @BindView(R.id.linlaReviews) LinearLayout linlaReviews;
    @BindView(R.id.listReviews) RecyclerView listReviews;
    @BindView(R.id.linlaNoReviews) LinearLayout linlaNoReviews;
    @BindView(R.id.txtAllReviews) TextView txtAllReviews;
    @BindView(R.id.linlaServicesProgress) LinearLayout linlaServicesProgress;
    @BindView(R.id.linlaServices) LinearLayout linlaServices;
    @BindView(R.id.listServices) RecyclerView listServices;
    @BindView(R.id.linlaNoServices) LinearLayout linlaNoServices;
    @BindView(R.id.txtAllServices) TextView txtAllServices;
    @BindView(R.id.linlaImagesContainer) LinearLayout linlaImagesContainer;
    @BindView(R.id.linlaClinicImages) LinearLayout linlaClinicImages;
    @BindView(R.id.listClinicImages) RecyclerView listClinicImages;
    @BindView(R.id.linlaNoClinicImages) LinearLayout linlaNoClinicImages;
//    @BindView(R.id.btnBook) AppCompatButton btnBook;

    /** THE CUSTOM TIMINGS LAYOUT ELEMENTS **/
    private TextView txtSunMorning;
    private TextView txtSunAfternoon;
    private TextView txtMonMorning;
    private TextView txtMonAfternoon;
    private TextView txtTueMorning;
    private TextView txtTueAfternoon;
    private TextView txtWedMorning;
    private TextView txtWedAfternoon;
    private TextView txtThuMorning;
    private TextView txtThuAfternoon;
    private TextView txtFriMorning;
    private TextView txtFriAfternoon;
    private TextView txtSatMorning;
    private TextView txtSatAfternoon;

    /* THE CUSTOM SERVICES LAYOUT ELEMENTS **/
    RecyclerView listDoctorServices;

    /** THE ALL CUSTOM VIEWS **/
    private View custAllTimings;
    private View custAllServices;

    /** SHOW THE CHARGES DIALOG **/
    @OnClick(R.id.imgvwChargesInfo) void showChargesInfo()    {
        showChargesDialog();
    }

    /** SHOW ALL TIMINGS **/
    @OnClick(R.id.txtAllTimings) void showAllTimings()  {
        showDoctorTimings();
    }

    /** SHOW ALL REVIEWS **/
    @OnClick(R.id.txtAllReviews) void showAllReviews()  {
        Intent intent = new Intent(getApplicationContext(), DoctorReviews.class);
        intent.putExtra("DOCTOR_ID", DOCTOR_ID);
        intent.putExtra("DOCTOR_PREFIX", DOCTOR_PREFIX);
        intent.putExtra("DOCTOR_NAME", DOCTOR_NAME);
        startActivity(intent);
    }

    /* SHOW ALL SERVICES **/
    @OnClick(R.id.txtAllServices) void showAllServices()    {
        showDoctorServices();
    }

    /** SHOW ALL SERVICES **/
    private void showDoctorServices() {
        /* CLEAR THE ARRAY LIST */
        arrServices.clear();

        /* CONFIGURE THE DIALOG */
        MaterialDialog dialog = new MaterialDialog.Builder(DoctorDetailsNew.this)
                .theme(Theme.LIGHT)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .title("ALL SERVICES")
                .customView(custAllServices, false)
                .positiveText("Dismiss")
                .build();

        /* CAST AND CONFIGURE THE RECYCLER VIEW */
        listDoctorServices = dialog.getCustomView().findViewById(R.id.listDoctorServices);
        LinearLayoutManager llmServices = new LinearLayoutManager(this);
        llmServices.setOrientation(LinearLayoutManager.VERTICAL);
        listDoctorServices.setLayoutManager(llmServices);
        listDoctorServices.setAdapter(servicesAdapter);

        /* FETCH A LIST OF THE DOCTOR'S SERVICES */
        fetchDoctorServices(dialog);
    }

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_details_new);
        ButterKnife.bind(this);
        clinicMap.onCreate(savedInstanceState != null ? savedInstanceState.getBundle("clinic_map_save_state") : null);
        clinicMap.onResume();
        clinicMap.setClickable(false);

        /* GET THE USER'S ID */
        USER_ID = getApp().getUserID();

        /* INFLATE THE CUSTOM VIEWS */
        custAllTimings = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_all_timings_view, null);
        custAllServices = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_all_services_list, null);

        /* INSTANTIATE THE ADAPTERS **/
        servicesAdapter = new ServicesAdapter(DoctorDetailsNew.this, arrServicesSubset);
        reviewsAdapter = new ReviewsAdapter(DoctorDetailsNew.this, arrReviewsSubset);
        imagesAdapter = new ClinicImagesAdapter(DoctorDetailsNew.this, arrImages);

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* GET TODAY'S DAY */
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        Date d = new Date();
        TODAY_DAY = sdf.format(d);

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* FETCH THE DOCTOR'S SUBSCRIPTION */
        fetchDoctorSubscription();

        /* FETCH THE DOCTOR'S EDUCATIONAL QUALIFICATIONS */
        fetchDoctorEducation();

        /* SHOW THE PROGRESS AND FETCH THE FIRST 3 REVIEWS FOR THE DOCTOR */
        linlaReviewsProgress.setVisibility(View.VISIBLE);
        listReviews.setVisibility(View.GONE);
        fetchReviewCount();
        fetchDoctorReviews();

        /* SHOW THE PROGRESS AND FETCH A SUBSET OF THE DOCTOR'S SERVICES */
        linlaServicesProgress.setVisibility(View.VISIBLE);
        listServices.setVisibility(View.GONE);
        fetchServicesCount();
        fetchDoctorServicesSubset();

        /* FETCH CLINIC IMAGES */
        fetchClinicImages();

        /* CONFIGURE THE APP BAR LAYOUT */
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            /* BOOLEAN TO TRACK IF TOOLBAR IS SHOWING */
            boolean isShow = false;

            /* SCROLL RANGE */
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1)  {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    toolbarLayout.setTitleEnabled(true);
                } else if (isShow) {
                    isShow = false;
                    toolbarLayout.setTitleEnabled(false);
                }
            }
        });

        /* CONFIGURE THE TOOLBAR */
        configTB();
    }

    /***** FETCH THE DOCTOR'S DETAILS *****/
    private void fetchDoctorDetails() {
        DoctorsAPI api = ZenApiClient.getClient().create(DoctorsAPI.class);
        Call<Doctor> call = api.fetchDoctorDetails(DOCTOR_ID, CLINIC_ID, ORIGIN_LATITUDE, ORIGIN_LONGITUDE);
        call.enqueue(new Callback<Doctor>() {
            @Override
            public void onResponse(Call<Doctor> call, Response<Doctor> response) {
//                Log.e("DETAILS RAW", String.valueOf(response.raw()));
                Doctor data = response.body();
                if (data != null) {

                    /* GET AND SET THE DOCTOR'S PREFIX AND NAME */
                    DOCTOR_PREFIX = data.getDoctorPrefix();
                    DOCTOR_NAME = data.getDoctorName();
                    txtDoctorName.setText(getString(R.string.appointment_creator_details_doctor, DOCTOR_PREFIX, DOCTOR_NAME));
                    toolbarLayout.setTitleEnabled(false);
                    toolbarLayout.setTitle(getString(R.string.doctor_details_doc_name_placeholder, DOCTOR_PREFIX, DOCTOR_NAME));

                    /* GET AND SET THE DOCTOR'S DISPLAY PROFILE */
                    String DOCTOR_PROFILE = data.getDoctorDisplayProfile();
                    if (DOCTOR_PROFILE != null) {
                        Uri uri = Uri.parse(DOCTOR_PROFILE);
                        imgvwDoctorProfile.setImageURI(uri);
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.ic_person_black_24dp)
                                .build();
                        imgvwDoctorProfile.setImageURI(request.getSourceUri());
                    }

                    /* GET AND SET THE CLINIC LOGO */
                    String CLINIC_LOGO = data.getClinicLogo();
                    if (CLINIC_LOGO != null)    {
                        Uri uriClinic = Uri.parse(CLINIC_LOGO);
                        imgvwClinicCover.setImageURI(uriClinic);
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.ic_business_black_24dp)
                                .build();
                        imgvwClinicCover.setImageURI(request.getSourceUri());
                    }

                    /* GET AND SET THE DOCTOR EXPERIENCE */
                    String DOCTOR_EXPERIENCE = data.getDoctorExperience();
                    txtExperience.setText(getString(R.string.doctor_details_doc_exp_placeholder, DOCTOR_EXPERIENCE));

                    /* GET AND SET THE DOCTOR'S CHARGES */
                    String CLINIC_CURRENCY = data.getCurrencySymbol();
                    String DOCTOR_CHARGES = data.getDoctorCharges();
                    txtDoctorCharges.setText(getString(R.string.doctor_details_doc_charges_placeholder, CLINIC_CURRENCY, DOCTOR_CHARGES));

                    /* GET AND SET THE CLINIC NAME AND ADDRESS DETAILS */
                    CLINIC_NAME = data.getClinicName();
                    CLINIC_ADDRESS = data.getClinicAddress();
                    String CLINIC_CITY = data.getCityName();
                    String CLINIC_STATE = data.getStateName();
                    String CLINIC_PIN_CODE = data.getClinicPinCode();
                    txtClinicName.setText(CLINIC_NAME);
                    txtClinicAddress.setText(getString(R.string.doctor_details_address_placeholder, CLINIC_ADDRESS, CLINIC_CITY, CLINIC_STATE, CLINIC_PIN_CODE));

                    /* GET AND SET THE CLINIC'S RATINGS */
                    CLINIC_RATING = data.getClinicRating();
                    if (CLINIC_RATING != null && !CLINIC_RATING.equalsIgnoreCase("null")) {
                        clinicRating.setRating(Float.parseFloat(CLINIC_RATING));
                    } else {
                        clinicRating.setRating(0);
                    }

                    /* GET THE DOCTOR'S PHONE NUMBER */
                    DOCTOR_PHONE_NUMBER = data.getDoctorPhoneNumber();

                    /* FETCH THE DOCTOR'S FEEDBACK */
                    fetchDoctorFeedback();

                    /* GET AND SET THE CLINIC DISTANCE */
                    CLINIC_DISTANCE = data.getClinicDistance();
                    if (CLINIC_DISTANCE != null
                            && !CLINIC_DISTANCE.equals("Unknown")
                            && !CLINIC_DISTANCE.equalsIgnoreCase("null"))  {
                        String strTilde = getString(R.string.generic_tilde);
                        txtClinicDistance.setText(getString(R.string.doctor_list_clinic_distance_placeholder, strTilde, CLINIC_DISTANCE));
                    } else {
                        String strInfinity = getString(R.string.generic_infinity);
                        txtClinicDistance.setText(getString(R.string.doctor_list_clinic_distance_placeholder, strInfinity, CLINIC_DISTANCE));
                    }

                    /* GET AND SET THE CLINIC LATITUDE AND LONGITUDE ON THE MAP */
                    CLINIC_LATITUDE = Double.valueOf(data.getClinicLatitude());
                    CLINIC_LONGITUDE = Double.valueOf(data.getClinicLongitude());
                    if (CLINIC_LATITUDE != null && CLINIC_LONGITUDE != null) {
                        final LatLng latLng = new LatLng(CLINIC_LATITUDE, CLINIC_LONGITUDE);
                        clinicMap.getMapAsync(new OnMapReadyCallback() {
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

                                /* SHOW THE MAP DETAILS AND DIRECTIONS */
                                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                    @Override
                                    public void onMapClick(LatLng latLng) {
                                        Intent intent = new Intent(getApplicationContext(), MapDetails.class);
                                        intent.putExtra("DOCTOR_ID", DOCTOR_ID);
                                        intent.putExtra("DOCTOR_NAME", DOCTOR_PREFIX + " " + DOCTOR_NAME);
                                        intent.putExtra("DOCTOR_PHONE_NUMBER", DOCTOR_PHONE_NUMBER);
                                        intent.putExtra("CLINIC_ID", CLINIC_ID);
                                        intent.putExtra("CLINIC_NAME", CLINIC_NAME);
                                        intent.putExtra("CLINIC_LATITUDE", CLINIC_LATITUDE);
                                        intent.putExtra("CLINIC_LONGITUDE", CLINIC_LONGITUDE);
                                        intent.putExtra("CLINIC_ADDRESS", CLINIC_ADDRESS);
                                        startActivity(intent);
                                    }
                                });
                            }
                        });
                    }

                    /* GET THE TIMINGS DEPENDING ON TODAY'S DAY */
                    if (TODAY_DAY.equalsIgnoreCase("Sunday"))   {
                        sundayMorningTimings();
                        sundayAfternoonTimings();
                    } else if (TODAY_DAY.equalsIgnoreCase("Monday"))    {
                        mondayMorningTimings();
                        mondayAfternoonTimings();
                    } else if (TODAY_DAY.equalsIgnoreCase("Tuesday"))   {
                        tuesdayMorningTimings();
                        tuesdayAfternoonTimings();
                    } else if (TODAY_DAY.equalsIgnoreCase("Wednesday")) {
                        wednesdayMorningTimings();
                        wednesdayAfternoonTimings();
                    } else if (TODAY_DAY.equalsIgnoreCase("Thursday"))  {
                        thursdayMorningTimings();
                        thursdayAfternoonTimings();
                    } else if (TODAY_DAY.equalsIgnoreCase("Friday"))    {
                        fridayMorningTimings();
                        fridayAfternoonTimings();
                    } else if (TODAY_DAY.equalsIgnoreCase("Saturday"))  {
                        saturdayMorningTimings();
                        saturdayAfternoonTimings();
                    }

                    /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                    linlaProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Doctor> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE DOCTOR'S FEEDBACK *****/
    private void fetchDoctorFeedback() {
        ReviewsAPI apiYes = ZenApiClient.getClient().create(ReviewsAPI.class);
        Call<Reviews> callYes = apiYes.fetchPositiveReviews(DOCTOR_ID, "Yes");
        callYes.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                if (response.body() != null && response.body().getReviews() != null)    {
                    ArrayList<Review> arrReview = response.body().getReviews();
                    TOTAL_LIKES = arrReview.size();
                    TOTAL_VOTES = TOTAL_VOTES + arrReview.size();

                    /* GET THE TOTAL NUMBER OF NEGATIVE REVIEWS */
                    ReviewsAPI apiNo = ZenApiClient.getClient().create(ReviewsAPI.class);
                    Call<Reviews> callNo = apiNo.fetchNegativeReviews(DOCTOR_ID, "No");
                    callNo.enqueue(new Callback<Reviews>() {
                        @Override
                        public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                            if (response.body() != null && response.body().getReviews() != null)    {
                                ArrayList<Review> arrReview = response.body().getReviews();
                                TOTAL_VOTES = TOTAL_VOTES + arrReview.size();

                                /* CALCULATE THE PERCENTAGE OF LIKES */
                                double percentLikes = ((double)TOTAL_LIKES / TOTAL_VOTES) * 100;
                                int finalPercentLikes = (int)percentLikes;
                                String strLikesPercentage = String.valueOf(finalPercentLikes) + "%";

                                /* GET THE TOTAL NUMBER OF REVIEWS / VOTES */
                                Resources resReviews = AppPrefs.context().getResources();
                                String reviewQuantity = null;
                                if (TOTAL_VOTES == 0)   {
                                    reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
                                } else if (TOTAL_VOTES == 1)    {
                                    reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
                                } else if (TOTAL_VOTES > 1) {
                                    reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
                                }
                                String strVotes = reviewQuantity;
                                String open = getString(R.string.doctor_list_votes_open);
                                String close = getString(R.string.doctor_list_votes_close);
                                txtVotes.setText(getString(R.string.doctor_list_votes_placeholder, strLikesPercentage, open, strVotes, close));
                            }
                        }

                        @Override
                        public void onFailure(Call<Reviews> call, Throwable t) {
                            Crashlytics.logException(t);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

//    /***** GET THE CLINIC RATING *****/
//    private void fetchClinicRatings() {
//        ClinicRatingsAPI api = ZenApiClient.getClient().create(ClinicRatingsAPI.class);
//        Call<ClinicRating> call = api.fetchClinicRatings(CLINIC_ID);
//        call.enqueue(new Callback<ClinicRating>() {
//            @Override
//            public void onResponse(Call<ClinicRating> call, Response<ClinicRating> response) {
//                ClinicRating rating = response.body();
//                if (rating != null) {
//                    String strRating = rating.getClinicRating();
//                    if (strRating != null && !strRating.equalsIgnoreCase("null")) {
//                        clinicRating.setRating(Float.parseFloat(strRating));
//                    } else {
//                        clinicRating.setRating(0);
//                    }
//                } else {
//                    clinicRating.setRating(0);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ClinicRating> call, Throwable t) {
//                Crashlytics.logException(t);
//            }
//        });
//    }

    /***** FETCH THE DOCTOR'S REVIEWS COUNT *****/
    private void fetchReviewCount() {
        ReviewsAPI api = ZenApiClient.getClient().create(ReviewsAPI.class);
        Call<ReviewCount> call = api.fetchDoctorReviewCount(DOCTOR_ID);
        call.enqueue(new Callback<ReviewCount>() {
            @Override
            public void onResponse(Call<ReviewCount> call, Response<ReviewCount> response) {
                ReviewCount count = response.body();
                if (count != null)  {
                    int countReviews = Integer.parseInt(count.getTotal_reviews());
                    if (countReviews > 0)   {
                        txtAllReviews.setText(getString(R.string.doctor_details_all_reviews_placeholder, String.valueOf(countReviews)));
                    } else {
                        txtAllReviews.setText(getString(R.string.doctor_details_all_reviews));
                    }
                } else {
                    txtAllReviews.setText(getString(R.string.doctor_details_all_reviews));
                }
            }

            @Override
            public void onFailure(Call<ReviewCount> call, Throwable t) {
//                Log.e("COUNT FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** FETCH THE NUMBER OF SERVICES **/
    private void fetchServicesCount() {
        ServicesAPI api = ZenApiClient.getClient().create(ServicesAPI.class);
        Call<ServicesCount> call = api.fetchDoctorServicesCount(DOCTOR_ID);
        call.enqueue(new Callback<ServicesCount>() {
            @Override
            public void onResponse(Call<ServicesCount> call, Response<ServicesCount> response) {
                ServicesCount count = response.body();
                if (count != null)  {
                    int countServices = Integer.parseInt(count.getTotalServices());
                    if (countServices > 0)   {
                        txtAllServices.setText(getString(R.string.doctor_details_all_services_placeholder, String.valueOf(countServices)));
                    } else {
                        txtAllServices.setText(getString(R.string.doctor_details_all_services));
                    }
                } else {
                    txtAllServices.setText(getString(R.string.doctor_details_all_services));
                }
            }

            @Override
            public void onFailure(Call<ServicesCount> call, Throwable t) {
//                Log.e("COUNT FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE DOCTOR'S REVIEWS *****/
    private void fetchDoctorReviews() {
        ReviewsAPI api = ZenApiClient.getClient().create(ReviewsAPI.class);
        Call<Reviews> call = api.fetchDoctorReviewsSubset(DOCTOR_ID);
        call.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                if (response.body() != null && response.body().getReviews() != null)    {
                    arrReviewsSubset = response.body().getReviews();
                    if (arrReviewsSubset.size() > 0)    {
                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY REVIEWS VIEW */
                        linlaReviews.setVisibility(View.VISIBLE);
                        linlaNoReviews.setVisibility(View.GONE);
                        listReviews.setVisibility(View.VISIBLE);

                        /* SET THE REVIEWS ADAPTER TO THE RECYCLER VIEW */
                        listReviews.setAdapter(new ReviewsAdapter(DoctorDetailsNew.this, arrReviewsSubset));
                    } else {
                        /* SHOW THE NO REVIEWS LAYOUT */
                        linlaNoReviews.setVisibility(View.VISIBLE);
                        linlaReviews.setVisibility(View.GONE);
                        listReviews.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE NO REVIEWS LAYOUT */
                    linlaNoReviews.setVisibility(View.VISIBLE);
                    linlaReviews.setVisibility(View.GONE);
                    listReviews.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE REVIEWS */
                linlaReviewsProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /** FETCH A LIST OF THE DOCTOR'S SERVICES
     * @param dialog**/
    private void fetchDoctorServices(final MaterialDialog dialog) {
        ServicesAPI api = ZenApiClient.getClient().create(ServicesAPI.class);
        Call<Services> call = api.fetchDoctorServices(DOCTOR_ID);
        call.enqueue(new Callback<Services>() {
            @Override
            public void onResponse(Call<Services> call, Response<Services> response) {
//                Log.e("SERVICES", String.valueOf(response.raw()));
                if (response.body() != null && response.body().getServices() != null)    {
                    arrServices = response.body().getServices();
                    if (arrServices.size() > 0)    {
//                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY REVIEWS VIEW */
//                        linlaServices.setVisibility(View.VISIBLE);
//                        linlaNoServices.setVisibility(View.GONE);
//                        listServices.setVisibility(View.VISIBLE);
//
                        /* INSTANTIATE THE ADAPTER */
                        servicesAdapter = new ServicesAdapter(DoctorDetailsNew.this, arrServices);

                        /* SET THE SERVICES ADAPTER TO THE RECYCLER VIEW */
                        listDoctorServices.setAdapter(servicesAdapter);
                    } else {
//                        /* SHOW THE NO SERVICES LAYOUT */
//                        linlaNoServices.setVisibility(View.VISIBLE);
//                        linlaServices.setVisibility(View.GONE);
//                        listServices.setVisibility(View.GONE);
                    }
                } else {
//                    /* SHOW THE NO SERVICES LAYOUT */
//                    linlaNoServices.setVisibility(View.VISIBLE);
//                    linlaServices.setVisibility(View.GONE);
//                    listServices.setVisibility(View.GONE);
                }

                /* SHOW THE DIALOG */
                dialog.show();

//                /* HIDE THE PROGRESS AFTER FETCHING THE SERVICES */
//                linlaServicesProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Services> call, Throwable t) {
//                Log.e("SERVICES FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** FETCH A SUBSET OF THE DOCTOR'S SERVICES **/
    private void fetchDoctorServicesSubset() {
        ServicesAPI api = ZenApiClient.getClient().create(ServicesAPI.class);
        Call<Services> call = api.fetchServicesSubset(DOCTOR_ID);
        call.enqueue(new Callback<Services>() {
            @Override
            public void onResponse(Call<Services> call, Response<Services> response) {
//                Log.e("SERVICES SUBSET", String.valueOf(response.raw()));
                if (response.body() != null && response.body().getServices() != null)    {
                    arrServicesSubset = response.body().getServices();
                    if (arrServicesSubset.size() > 0)    {
                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY REVIEWS VIEW */
                        linlaServices.setVisibility(View.VISIBLE);
                        linlaNoServices.setVisibility(View.GONE);
                        listServices.setVisibility(View.VISIBLE);

                        /* SET THE SERVICES ADAPTER TO THE RECYCLER VIEW */
                        listServices.setAdapter(new ServicesAdapter(DoctorDetailsNew.this, arrServicesSubset));
                    } else {
                        /* SHOW THE NO SERVICES LAYOUT */
                        linlaNoServices.setVisibility(View.VISIBLE);
                        linlaServices.setVisibility(View.GONE);
                        listServices.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE NO SERVICES LAYOUT */
                    linlaNoServices.setVisibility(View.VISIBLE);
                    linlaServices.setVisibility(View.GONE);
                    listServices.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE SERVICES */
                linlaServicesProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Services> call, Throwable t) {
//                Log.e("SERVICES FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** SHOW THE CHARGES DIALOG *****/
    private void showChargesDialog() {
        new MaterialDialog.Builder(this)
                .icon(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_black_24dp))
                .title("Consultation Fees")
                .cancelable(true)
                .content("The fees are indicative and might vary depending on the services required and offered. \n\nNOTE: the fees are payable at the Clinic. There are no charges for booking an appointment")
                .positiveText("Dismiss")
                .theme(Theme.LIGHT)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    /***** SHOW ALL TIMINGS *****/
    private void showDoctorTimings() {
        MaterialDialog dialog = new MaterialDialog.Builder(DoctorDetailsNew.this)
                .title("ALL TIMINGS")
                .customView(custAllTimings, false)
                .positiveText("Dismiss")
                .build();

        /* CAST THE LAYOUT ELEMENTS */
        txtSunMorning = dialog.getCustomView().findViewById(R.id.txtSunMorning);
        txtSunAfternoon = dialog.getCustomView().findViewById(R.id.txtSunAfternoon);
        txtMonMorning = dialog.getCustomView().findViewById(R.id.txtMonMorning);
        txtMonAfternoon = dialog.getCustomView().findViewById(R.id.txtMonAfternoon);
        txtTueMorning = dialog.getCustomView().findViewById(R.id.txtTueMorning);
        txtTueAfternoon = dialog.getCustomView().findViewById(R.id.txtTueAfternoon);
        txtWedMorning = dialog.getCustomView().findViewById(R.id.txtWedMorning);
        txtWedAfternoon = dialog.getCustomView().findViewById(R.id.txtWedAfternoon);
        txtThuMorning = dialog.getCustomView().findViewById(R.id.txtThuMorning);
        txtThuAfternoon = dialog.getCustomView().findViewById(R.id.txtThuAfternoon);
        txtFriMorning = dialog.getCustomView().findViewById(R.id.txtFriMorning);
        txtFriAfternoon = dialog.getCustomView().findViewById(R.id.txtFriAfternoon);
        txtSatMorning = dialog.getCustomView().findViewById(R.id.txtSatMorning);
        txtSatAfternoon = dialog.getCustomView().findViewById(R.id.txtSatAfternoon);

        /* FETCH THE DOCTOR'S TIMINGS */
        fetchDoctorTimings();
//        new FetchDoctorTimings(this).execute(DOCTOR_ID, CLINIC_ID);

        /* SHOW THE DIALOG */
        dialog.show();
    }

    /***** FETCH THE DOCTOR'S TIMINGS *****/
    private void fetchDoctorTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchDoctorTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
//                Log.e("RESPONSE", String.valueOf(response.raw()));
                Timing timing = response.body();
                if (timing != null) {
                    SUN_MOR_FROM = timing.getSunMorFrom();
                    SUN_MOR_TO = timing.getSunMorTo();
                    SUN_AFT_FROM = timing.getSunAftFrom();
                    SUN_AFT_TO = timing.getSunAftTo();
                    MON_MOR_FROM = timing.getMonMorFrom();
                    MON_MOR_TO = timing.getMonMorTo();
                    MON_AFT_FROM = timing.getMonAftFrom();
                    MON_AFT_TO = timing.getMonAftTo();
                    TUE_MOR_FROM = timing.getTueMorFrom();
                    TUE_MOR_TO = timing.getTueMorTo();
                    TUE_AFT_FROM = timing.getTueAftFrom();
                    TUE_AFT_TO = timing.getTueAftTo();
                    WED_MOR_FROM = timing.getWedMorFrom();
                    WED_MOR_TO = timing.getWedMorTo();
                    WED_AFT_FROM = timing.getWedAftFrom();
                    WED_AFT_TO = timing.getWedAftTo();
                    THU_MOR_FROM = timing.getThuMorFrom();
                    THU_MOR_TO = timing.getThuMorTo();
                    THU_AFT_FROM = timing.getThuAftFrom();
                    THU_AFT_TO = timing.getThuAftTo();
                    FRI_MOR_FROM = timing.getFriMorFrom();
                    FRI_MOR_TO = timing.getFriMorTo();
                    FRI_AFT_FROM = timing.getFriAftFrom();
                    FRI_AFT_TO = timing.getFriAftTo();
                    SAT_MOR_FROM = timing.getSatMorFrom();
                    SAT_MOR_TO = timing.getSatMorTo();
                    SAT_AFT_FROM = timing.getSatAftFrom();
                    SAT_AFT_TO = timing.getSatAftTo();
                } else {
                    SUN_MOR_FROM = null;
                    SUN_MOR_TO = null;
                    SUN_AFT_FROM = null;
                    SUN_AFT_TO = null;
                    MON_MOR_FROM = null;
                    MON_MOR_TO = null;
                    MON_AFT_FROM = null;
                    MON_AFT_TO = null;
                    TUE_MOR_FROM = null;
                    TUE_MOR_TO = null;
                    TUE_AFT_FROM = null;
                    TUE_AFT_TO = null;
                    WED_MOR_FROM = null;
                    WED_MOR_TO = null;
                    WED_AFT_FROM = null;
                    WED_AFT_TO = null;
                    THU_MOR_FROM = null;
                    THU_MOR_TO = null;
                    THU_AFT_FROM = null;
                    THU_AFT_TO = null;
                    FRI_MOR_FROM = null;
                    FRI_MOR_TO = null;
                    FRI_AFT_FROM = null;
                    FRI_AFT_TO = null;
                    SAT_MOR_FROM = null;
                    SAT_MOR_TO = null;
                    SAT_AFT_FROM = null;
                    SAT_AFT_TO = null;
                }

                /* SET THE SUNDAY MORNING TIMINGS */
                if (SUN_MOR_FROM != null && !SUN_MOR_FROM.equalsIgnoreCase("null") && !SUN_MOR_FROM.equalsIgnoreCase("")
                        && SUN_MOR_TO != null && !SUN_MOR_TO.equalsIgnoreCase("null") && !SUN_MOR_TO.equalsIgnoreCase("")) {
                    txtSunMorning.setText(getString(R.string.doctor_details_timings_placeholder, SUN_MOR_FROM, SUN_MOR_TO));
                } else {
                    txtSunMorning.setText(R.string.doctor_details_timings_closed);
                }

                /* SET THE SUNDAY AFTERNOON TIMINGS */
                if (SUN_AFT_FROM != null && !SUN_AFT_FROM.equalsIgnoreCase("null") && !SUN_AFT_FROM.equalsIgnoreCase("")
                        && SUN_AFT_TO != null && !SUN_AFT_TO.equalsIgnoreCase("null") && !SUN_AFT_TO.equalsIgnoreCase("")) {
                    txtSunAfternoon.setText(getString(R.string.doctor_details_timings_placeholder, SUN_AFT_FROM, SUN_AFT_TO));
                } else {
                    txtSunAfternoon.setText(R.string.doctor_details_timings_closed);
                }

                /* SET THE MONDAY MORNING TIMINGS */
                if (MON_MOR_FROM != null && !MON_MOR_FROM.equalsIgnoreCase("null") && !MON_MOR_FROM.equalsIgnoreCase("")
                        && MON_MOR_TO != null && !MON_MOR_TO.equalsIgnoreCase("null") && !MON_MOR_TO.equalsIgnoreCase("")) {
                    txtMonMorning.setText(getString(R.string.doctor_details_timings_placeholder, MON_MOR_FROM, MON_MOR_TO));
                } else {
                    txtMonMorning.setText(R.string.doctor_details_timings_closed);
                }

                /* SET THE MONDAY AFTERNOON TIMINGS */
                if (MON_AFT_FROM != null && !MON_AFT_FROM.equalsIgnoreCase("null") && !MON_AFT_FROM.equalsIgnoreCase("")
                        && MON_AFT_TO != null && !MON_AFT_TO.equalsIgnoreCase("null") && !MON_AFT_TO.equalsIgnoreCase("")) {
                    txtMonAfternoon.setText(getString(R.string.doctor_details_timings_placeholder, MON_AFT_FROM, MON_AFT_TO));
                } else {
                    txtMonAfternoon.setText(R.string.doctor_details_timings_closed);
                }

                /* SET THE TUESDAY MORNING TIMINGS */
                if (TUE_MOR_FROM != null && !TUE_MOR_FROM.equalsIgnoreCase("null") && !TUE_MOR_FROM.equalsIgnoreCase("")
                        && TUE_MOR_TO != null && !TUE_MOR_TO.equalsIgnoreCase("null") && !TUE_MOR_TO.equalsIgnoreCase("")) {
                    txtTueMorning.setText(getString(R.string.doctor_details_timings_placeholder, TUE_MOR_FROM, TUE_MOR_TO));
                } else {
                    txtTueMorning.setText(R.string.doctor_details_timings_closed);
                }

                /* SET THE TUESDAY AFTERNOON TIMINGS */
                if (TUE_AFT_FROM != null && !TUE_AFT_FROM.equalsIgnoreCase("null") && !TUE_AFT_FROM.equalsIgnoreCase("")
                        && TUE_AFT_TO != null && !TUE_AFT_TO.equalsIgnoreCase("null") && !TUE_AFT_TO.equalsIgnoreCase("")) {
                    txtTueAfternoon.setText(getString(R.string.doctor_details_timings_placeholder, TUE_AFT_FROM, TUE_AFT_TO));
                } else {
                    txtTueAfternoon.setText(R.string.doctor_details_timings_closed);
                }

                /* SET THE WEDNESDAY MORNING TIMINGS */
                if (WED_MOR_FROM != null && !WED_MOR_FROM.equalsIgnoreCase("null") && !WED_MOR_FROM.equalsIgnoreCase("")
                        && WED_MOR_TO != null && !WED_MOR_TO.equalsIgnoreCase("null") && !WED_MOR_TO.equalsIgnoreCase("")) {
                    txtWedMorning.setText(getString(R.string.doctor_details_timings_placeholder, WED_MOR_FROM, WED_MOR_TO));
                } else {
                    txtWedMorning.setText(R.string.doctor_details_timings_closed);
                }

                /* SET THE WEDNESDAY AFTERNOON TIMINGS */
                if (WED_AFT_FROM != null && !WED_AFT_FROM.equalsIgnoreCase("null") && !WED_AFT_FROM.equalsIgnoreCase("")
                        && WED_AFT_TO != null && !WED_AFT_TO.equalsIgnoreCase("null") && !WED_AFT_TO.equalsIgnoreCase("")) {
                    txtWedAfternoon.setText(getString(R.string.doctor_details_timings_placeholder, WED_AFT_FROM, WED_AFT_TO));
                } else {
                    txtWedAfternoon.setText(R.string.doctor_details_timings_closed);
                }

                /* SET THE THURSDAY MORNING TIMINGS */
                if (THU_MOR_FROM != null && !THU_MOR_FROM.equalsIgnoreCase("null") && !THU_MOR_FROM.equalsIgnoreCase("")
                        && THU_MOR_TO != null && !THU_MOR_TO.equalsIgnoreCase("null") && !THU_MOR_TO.equalsIgnoreCase(""))   {
                    txtThuMorning.setText(getString(R.string.doctor_details_timings_placeholder, THU_MOR_FROM, THU_MOR_TO));
                } else {
                    txtThuMorning.setText(R.string.doctor_details_timings_closed);
                }

                /* SET THE THURSDAY AFTERNOON TIMINGS */
                if (THU_AFT_FROM != null && !THU_AFT_FROM.equalsIgnoreCase("null") && !THU_AFT_FROM.equalsIgnoreCase("")
                        && THU_AFT_TO != null && !THU_AFT_TO.equalsIgnoreCase("null") && !THU_AFT_TO.equalsIgnoreCase("")) {
                    txtThuAfternoon.setText(getString(R.string.doctor_details_timings_placeholder, THU_AFT_FROM, THU_AFT_TO));
                } else {
                    txtThuAfternoon.setText(R.string.doctor_details_timings_closed);
                }

                /* SET THE FRIDAY MORNING TIMINGS */
                if (FRI_MOR_FROM != null && !FRI_MOR_FROM.equalsIgnoreCase("null") && !FRI_MOR_FROM.equalsIgnoreCase("")
                        && FRI_MOR_TO != null && !FRI_MOR_TO.equalsIgnoreCase("null") && !FRI_MOR_TO.equalsIgnoreCase("")) {
                    txtFriMorning.setText(getString(R.string.doctor_details_timings_placeholder, FRI_MOR_FROM, FRI_MOR_TO));
                } else {
                    txtFriMorning.setText(R.string.doctor_details_timings_closed);
                }

                /* SET THE FRIDAY AFTERNOON TIMINGS */
                if (FRI_AFT_FROM != null && !FRI_AFT_FROM.equalsIgnoreCase("null") && !FRI_AFT_FROM.equalsIgnoreCase("")
                        && FRI_AFT_TO != null && !FRI_AFT_TO.equalsIgnoreCase("null") && !FRI_AFT_TO.equalsIgnoreCase("")) {
                    txtFriAfternoon.setText(getString(R.string.doctor_details_timings_placeholder, FRI_AFT_FROM, FRI_AFT_FROM));
                } else {
                    txtFriAfternoon.setText(R.string.doctor_details_timings_closed);
                }

                /* SET THE SATURDAY MORNING TIMINGS */
                if (SAT_MOR_FROM != null && !SAT_MOR_FROM.equalsIgnoreCase("null") && !SAT_MOR_FROM.equalsIgnoreCase("")
                        && SAT_MOR_TO != null && !SAT_MOR_TO.equalsIgnoreCase("null") && !SAT_MOR_TO.equalsIgnoreCase("")) {
                    txtSatMorning.setText(getString(R.string.doctor_details_timings_placeholder, SAT_MOR_FROM, SAT_MOR_TO));
                } else {
                    txtSatMorning.setText(R.string.doctor_details_timings_closed);
                }

                /* SET THE SATURDAY AFTERNOON TIMINGS */
                if (SAT_AFT_FROM != null && !SAT_AFT_FROM.equalsIgnoreCase("null") && !SAT_AFT_FROM.equalsIgnoreCase("")
                        && SAT_AFT_TO != null && !SAT_AFT_TO.equalsIgnoreCase("null") && !SAT_AFT_TO.equalsIgnoreCase("")) {
                    txtSatAfternoon.setText(getString(R.string.doctor_details_timings_placeholder, SAT_AFT_FROM, SAT_AFT_TO));
                } else {
                    txtSatAfternoon.setText(R.string.doctor_details_timings_closed);
                }
            }

            @Override
            public void onFailure(Call<Timing> call, Throwable t) {
//                Log.e("TIMINGS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        LinearLayoutManager services = new LinearLayoutManager(this);
        services.setOrientation(LinearLayoutManager.VERTICAL);
        services.isAutoMeasureEnabled();
        listServices.setLayoutManager(services);
        listServices.setHasFixedSize(true);
        listServices.setNestedScrollingEnabled(false);
        listServices.setAdapter(new ServicesAdapter(DoctorDetailsNew.this, arrServicesSubset));

        LinearLayoutManager reviews = new LinearLayoutManager(this);
        reviews.setOrientation(LinearLayoutManager.VERTICAL);
        reviews.isAutoMeasureEnabled();
        listReviews.setLayoutManager(reviews);
        listReviews.setHasFixedSize(true);
        listReviews.setNestedScrollingEnabled(false);
        listReviews.setAdapter(reviewsAdapter);

        LinearLayoutManager llmClinicImages = new LinearLayoutManager(this);
        llmClinicImages.setOrientation(LinearLayoutManager.HORIZONTAL);
        llmClinicImages.isAutoMeasureEnabled();
        listClinicImages.setLayoutManager(llmClinicImages);
        listClinicImages.setHasFixedSize(true);
        listClinicImages.setNestedScrollingEnabled(false);
        listClinicImages.setAdapter(imagesAdapter);
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null
                && bundle.containsKey("DOCTOR_ID")
                && bundle.containsKey("CLINIC_ID")
                && bundle.containsKey("ORIGIN_LATITUDE")
                && bundle.containsKey("ORIGIN_LONGITUDE"))    {
            DOCTOR_ID = bundle.getString("DOCTOR_ID");
            CLINIC_ID = bundle.getString("CLINIC_ID");
            ORIGIN_LATITUDE = bundle.getString("ORIGIN_LATITUDE");
            ORIGIN_LONGITUDE = bundle.getString("ORIGIN_LONGITUDE");
            if (DOCTOR_ID != null && CLINIC_ID != null
                    && ORIGIN_LATITUDE != null && ORIGIN_LONGITUDE != null)  {
                /* SHOW THE PROGRESS AND FETCH THE DOCTOR DETAILS **/
                linlaProgress.setVisibility(View.VISIBLE);
                fetchDoctorDetails();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** FETCH THE SUNDAY MORNING TIMINGS *****/
    private void sundayMorningTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchSundayMorningTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                Timing timing = response.body();
                if (timing != null) {
                    String morningFrom = timing.getSunMorFrom();
                    String morningTo = timing.getSunMorTo();
                    if (morningFrom != null && !morningFrom.equalsIgnoreCase("null")
                            && morningTo != null && !morningTo.equalsIgnoreCase("null"))   {
                        txtTimingsMorning.setVisibility(View.VISIBLE);
                        txtMorningOpen.setVisibility(View.VISIBLE);
                        txtMorningClosed.setVisibility(View.GONE);
                        txtTimingsMorning.setText(getString(R.string.doctor_details_timings_placeholder, morningFrom, morningTo));
                    } else {
                        txtTimingsMorning.setVisibility(View.GONE);
                        txtMorningOpen.setVisibility(View.GONE);
                        txtMorningClosed.setVisibility(View.VISIBLE);
                    }
                } else {
                    txtTimingsMorning.setVisibility(View.GONE);
                    txtMorningOpen.setVisibility(View.GONE);
                    txtMorningClosed.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Timing> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE SUNDAY AFTERNOON TIMINGS *****/
    private void sundayAfternoonTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchSundayAfternoonTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                Timing timing = response.body();
                if (timing != null) {
                    String afternoonFrom = timing.getSunAftFrom();
                    String afternoonTo = timing.getSunAftTo();

                    if (afternoonFrom != null && !afternoonFrom.equalsIgnoreCase("null")
                            && afternoonTo != null && !afternoonTo.equalsIgnoreCase("null"))   {
                        txtTimingAfternoon.setVisibility(View.VISIBLE);
                        txtAfternoonOpen.setVisibility(View.VISIBLE);
                        txtAfternoonClosed.setVisibility(View.GONE);
                        txtTimingAfternoon.setText(getString(R.string.doctor_details_timings_placeholder, afternoonFrom, afternoonTo));
                    } else {
                        txtTimingAfternoon.setVisibility(View.GONE);
                        txtAfternoonOpen.setVisibility(View.GONE);
                        txtAfternoonClosed.setVisibility(View.VISIBLE);
                    }
                } else {
                    txtTimingAfternoon.setVisibility(View.GONE);
                    txtAfternoonOpen.setVisibility(View.GONE);
                    txtAfternoonClosed.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Timing> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE MONDAY MORNING TIMINGS *****/
    private void mondayMorningTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchMondayMorningTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                Timing timing = response.body();
                if (timing != null) {
                    String morningFrom = timing.getMonMorFrom();
                    String morningTo = timing.getMonMorTo();
                    if (morningFrom != null && !morningFrom.equalsIgnoreCase("null")
                            && morningTo != null && !morningTo.equalsIgnoreCase("null"))   {
                        txtTimingsMorning.setVisibility(View.VISIBLE);
                        txtMorningOpen.setVisibility(View.VISIBLE);
                        txtMorningClosed.setVisibility(View.GONE);
                        txtTimingsMorning.setText(getString(R.string.doctor_details_timings_placeholder, morningFrom, morningTo));
                    } else {
                        txtTimingsMorning.setVisibility(View.GONE);
                        txtMorningOpen.setVisibility(View.GONE);
                        txtMorningClosed.setVisibility(View.VISIBLE);
                    }
                } else {
                    txtTimingsMorning.setVisibility(View.GONE);
                    txtMorningOpen.setVisibility(View.GONE);
                    txtMorningClosed.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Timing> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE MONDAY AFTERNOON TIMINGS *****/
    private void mondayAfternoonTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchMondayAfternoonTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                Timing timing = response.body();
                if (timing != null) {
                    String afternoonFrom = timing.getMonAftFrom();
                    String afternoonTo = timing.getMonAftTo();

                    if (afternoonFrom != null && !afternoonFrom.equalsIgnoreCase("null")
                            && afternoonTo != null && !afternoonTo.equalsIgnoreCase("null"))   {
                        txtTimingAfternoon.setVisibility(View.VISIBLE);
                        txtAfternoonOpen.setVisibility(View.VISIBLE);
                        txtAfternoonClosed.setVisibility(View.GONE);
                        txtTimingAfternoon.setText(getString(R.string.doctor_details_timings_placeholder, afternoonFrom, afternoonTo));
                    } else {
                        txtTimingAfternoon.setVisibility(View.GONE);
                        txtAfternoonOpen.setVisibility(View.GONE);
                        txtAfternoonClosed.setVisibility(View.VISIBLE);
                    }
                } else {
                    txtTimingAfternoon.setVisibility(View.GONE);
                    txtAfternoonOpen.setVisibility(View.GONE);
                    txtAfternoonClosed.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Timing> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE TUESDAY MORNING TIMINGS *****/
    private void tuesdayMorningTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchTuesdayMorningTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                Timing timing = response.body();
                if (timing != null) {
                    String morningFrom = timing.getTueMorFrom();
                    String morningTo = timing.getTueMorTo();
                    if (morningFrom != null && !morningFrom.equalsIgnoreCase("null")
                            && morningTo != null && !morningTo.equalsIgnoreCase("null"))   {
                        txtTimingsMorning.setVisibility(View.VISIBLE);
                        txtMorningOpen.setVisibility(View.VISIBLE);
                        txtMorningClosed.setVisibility(View.GONE);
                        txtTimingsMorning.setText(getString(R.string.doctor_details_timings_placeholder, morningFrom, morningTo));
                    } else {
                        txtTimingsMorning.setVisibility(View.GONE);
                        txtMorningOpen.setVisibility(View.GONE);
                        txtMorningClosed.setVisibility(View.VISIBLE);
                    }
                } else {
                    txtTimingsMorning.setVisibility(View.GONE);
                    txtMorningOpen.setVisibility(View.GONE);
                    txtMorningClosed.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Timing> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE TUESDAY AFTERNOON TIMINGS *****/
    private void tuesdayAfternoonTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchTuesdayAfternoonTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                Timing timing = response.body();
                if (timing != null) {
                    String afternoonFrom = timing.getTueAftFrom();
                    String afternoonTo = timing.getTueAftTo();

                    if (afternoonFrom != null && !afternoonFrom.equalsIgnoreCase("null")
                            && afternoonTo != null && !afternoonTo.equalsIgnoreCase("null"))   {
                        txtTimingAfternoon.setVisibility(View.VISIBLE);
                        txtAfternoonOpen.setVisibility(View.VISIBLE);
                        txtAfternoonClosed.setVisibility(View.GONE);
                        txtTimingAfternoon.setText(getString(R.string.doctor_details_timings_placeholder, afternoonFrom, afternoonTo));
                    } else {
                        txtTimingAfternoon.setVisibility(View.GONE);
                        txtAfternoonOpen.setVisibility(View.GONE);
                        txtAfternoonClosed.setVisibility(View.VISIBLE);
                    }
                } else {
                    txtTimingAfternoon.setVisibility(View.GONE);
                    txtAfternoonOpen.setVisibility(View.GONE);
                    txtAfternoonClosed.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Timing> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE WEDNESDAY MORNING TIMINGS *****/
    private void wednesdayMorningTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchWednesdayMorningTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                Timing timing = response.body();
                if (timing != null) {
                    String morningFrom = timing.getWedMorFrom();
                    String morningTo = timing.getWedMorTo();
                    if (morningFrom != null && !morningFrom.equalsIgnoreCase("null")
                            && morningTo != null && !morningTo.equalsIgnoreCase("null"))   {
                        txtTimingsMorning.setVisibility(View.VISIBLE);
                        txtMorningOpen.setVisibility(View.VISIBLE);
                        txtMorningClosed.setVisibility(View.GONE);
                        txtTimingsMorning.setText(getString(R.string.doctor_details_timings_placeholder, morningFrom, morningTo));
                    } else {
                        txtTimingsMorning.setVisibility(View.GONE);
                        txtMorningOpen.setVisibility(View.GONE);
                        txtMorningClosed.setVisibility(View.VISIBLE);
                    }
                } else {
                    txtTimingsMorning.setVisibility(View.GONE);
                    txtMorningOpen.setVisibility(View.GONE);
                    txtMorningClosed.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Timing> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE WEDNESDAY AFTERNOON TIMINGS *****/
    private void wednesdayAfternoonTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchWednesdayAfternoonTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                Timing timing = response.body();
                if (timing != null) {
                    String afternoonFrom = timing.getWedAftFrom();
                    String afternoonTo = timing.getWedAftTo();

                    if (afternoonFrom != null && !afternoonFrom.equalsIgnoreCase("null")
                            && afternoonTo != null && !afternoonTo.equalsIgnoreCase("null"))   {
                        txtTimingAfternoon.setVisibility(View.VISIBLE);
                        txtAfternoonOpen.setVisibility(View.VISIBLE);
                        txtAfternoonClosed.setVisibility(View.GONE);
                        txtTimingAfternoon.setText(getString(R.string.doctor_details_timings_placeholder, afternoonFrom, afternoonTo));
                    } else {
                        txtTimingAfternoon.setVisibility(View.GONE);
                        txtAfternoonOpen.setVisibility(View.GONE);
                        txtAfternoonClosed.setVisibility(View.VISIBLE);
                    }
                } else {
                    txtTimingAfternoon.setVisibility(View.GONE);
                    txtAfternoonOpen.setVisibility(View.GONE);
                    txtAfternoonClosed.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Timing> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE THURSDAY MORNING TIMINGS *****/
    private void thursdayMorningTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchThursdayMorningTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                Timing timing = response.body();
                if (timing != null) {
                    String morningFrom = timing.getThuMorFrom();
                    String morningTo = timing.getThuMorTo();
                    if (morningFrom != null && !morningFrom.equalsIgnoreCase("null")
                            && morningTo != null && !morningTo.equalsIgnoreCase("null"))   {
                        txtTimingsMorning.setVisibility(View.VISIBLE);
                        txtMorningOpen.setVisibility(View.VISIBLE);
                        txtMorningClosed.setVisibility(View.GONE);
                        txtTimingsMorning.setText(getString(R.string.doctor_details_timings_placeholder, morningFrom, morningTo));
                    } else {
                        txtTimingsMorning.setVisibility(View.GONE);
                        txtMorningOpen.setVisibility(View.GONE);
                        txtMorningClosed.setVisibility(View.VISIBLE);
                    }
                } else {
                    txtTimingsMorning.setVisibility(View.GONE);
                    txtMorningOpen.setVisibility(View.GONE);
                    txtMorningClosed.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Timing> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE THURSDAY AFTERNOON TIMINGS *****/
    private void thursdayAfternoonTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchThursdayAfternoonTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                Timing timing = response.body();
                if (timing != null) {
                    String afternoonFrom = timing.getThuAftFrom();
                    String afternoonTo = timing.getThuAftTo();

                    if (afternoonFrom != null && !afternoonFrom.equalsIgnoreCase("null")
                            && afternoonTo != null && !afternoonTo.equalsIgnoreCase("null"))   {
                        txtTimingAfternoon.setVisibility(View.VISIBLE);
                        txtAfternoonOpen.setVisibility(View.VISIBLE);
                        txtAfternoonClosed.setVisibility(View.GONE);
                        txtTimingAfternoon.setText(getString(R.string.doctor_details_timings_placeholder, afternoonFrom, afternoonTo));
                    } else {
                        txtTimingAfternoon.setVisibility(View.GONE);
                        txtAfternoonOpen.setVisibility(View.GONE);
                        txtAfternoonClosed.setVisibility(View.VISIBLE);
                    }
                } else {
                    txtTimingAfternoon.setVisibility(View.GONE);
                    txtAfternoonOpen.setVisibility(View.GONE);
                    txtAfternoonClosed.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Timing> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE FRIDAY MORNING TIMINGS *****/
    private void fridayMorningTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchFridayMorningTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                Timing timing = response.body();
                if (timing != null) {
                    String morningFrom = timing.getFriMorFrom();
                    String morningTo = timing.getFriMorTo();
                    if (morningFrom != null && !morningFrom.equalsIgnoreCase("null")
                            && morningTo != null && !morningTo.equalsIgnoreCase("null"))   {
                        txtTimingsMorning.setVisibility(View.VISIBLE);
                        txtMorningOpen.setVisibility(View.VISIBLE);
                        txtMorningClosed.setVisibility(View.GONE);
                        txtTimingsMorning.setText(getString(R.string.doctor_details_timings_placeholder, morningFrom, morningTo));
                    } else {
                        txtTimingsMorning.setVisibility(View.GONE);
                        txtMorningOpen.setVisibility(View.GONE);
                        txtMorningClosed.setVisibility(View.VISIBLE);
                    }
                } else {
                    txtTimingsMorning.setVisibility(View.GONE);
                    txtMorningOpen.setVisibility(View.GONE);
                    txtMorningClosed.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Timing> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE FRIDAY AFTERNOON TIMINGS *****/
    private void fridayAfternoonTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchFridayAfternoonTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                Timing timing = response.body();
                if (timing != null) {
                    String afternoonFrom = timing.getFriAftFrom();
                    String afternoonTo = timing.getFriAftTo();

                    if (afternoonFrom != null && !afternoonFrom.equalsIgnoreCase("null")
                            && afternoonTo != null && !afternoonTo.equalsIgnoreCase("null"))   {
                        txtTimingAfternoon.setVisibility(View.VISIBLE);
                        txtAfternoonOpen.setVisibility(View.VISIBLE);
                        txtAfternoonClosed.setVisibility(View.GONE);
                        txtTimingAfternoon.setText(getString(R.string.doctor_details_timings_placeholder, afternoonFrom, afternoonTo));
                    } else {
                        txtTimingAfternoon.setVisibility(View.GONE);
                        txtAfternoonOpen.setVisibility(View.GONE);
                        txtAfternoonClosed.setVisibility(View.VISIBLE);
                    }
                } else {
                    txtTimingAfternoon.setVisibility(View.GONE);
                    txtAfternoonOpen.setVisibility(View.GONE);
                    txtAfternoonClosed.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Timing> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE SATURDAY MORNING TIMINGS *****/
    private void saturdayMorningTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchSaturdayMorningTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                Timing timing = response.body();
                if (timing != null) {
                    String morningFrom = timing.getSatMorFrom();
                    String morningTo = timing.getSatMorTo();
                    if (morningFrom != null && !morningFrom.equalsIgnoreCase("null")
                            && morningTo != null && !morningTo.equalsIgnoreCase("null"))   {
                        txtTimingsMorning.setVisibility(View.VISIBLE);
                        txtMorningOpen.setVisibility(View.VISIBLE);
                        txtMorningClosed.setVisibility(View.GONE);
                        txtTimingsMorning.setText(getString(R.string.doctor_details_timings_placeholder, morningFrom, morningTo));
                    } else {
                        txtTimingsMorning.setVisibility(View.GONE);
                        txtMorningOpen.setVisibility(View.GONE);
                        txtMorningClosed.setVisibility(View.VISIBLE);
                    }
                } else {
                    txtTimingsMorning.setVisibility(View.GONE);
                    txtMorningOpen.setVisibility(View.GONE);
                    txtMorningClosed.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Timing> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE SATURDAY AFTERNOON TIMINGS *****/
    private void saturdayAfternoonTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchSaturdayAfternoonTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                Timing timing = response.body();
                if (timing != null) {
                    String afternoonFrom = timing.getSatAftFrom();
                    String afternoonTo = timing.getSatAftTo();

                    if (afternoonFrom != null && !afternoonFrom.equalsIgnoreCase("null")
                            && afternoonTo != null && !afternoonTo.equalsIgnoreCase("null"))   {
                        txtTimingAfternoon.setVisibility(View.VISIBLE);
                        txtAfternoonOpen.setVisibility(View.VISIBLE);
                        txtAfternoonClosed.setVisibility(View.GONE);
                        txtTimingAfternoon.setText(getString(R.string.doctor_details_timings_placeholder, afternoonFrom, afternoonTo));
                    } else {
                        txtTimingAfternoon.setVisibility(View.GONE);
                        txtAfternoonOpen.setVisibility(View.GONE);
                        txtAfternoonClosed.setVisibility(View.VISIBLE);
                    }
                } else {
                    txtTimingAfternoon.setVisibility(View.GONE);
                    txtAfternoonOpen.setVisibility(View.GONE);
                    txtAfternoonClosed.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Timing> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(DoctorDetailsNew.this);
        inflater.inflate(R.menu.activity_doctor_details, menu);

        /* CHECK THE SUBSCRIPTION STATUS */
        MenuItem menuBook = menu.findItem(R.id.menuBook);
        MenuItem menuCall = menu.findItem(R.id.menuCall);

        if (blnSubscriptionStatus) {
            menuBook.setVisible(true);
            menuCall.setVisible(false);
        } else {
            menuCall.setVisible(true);
            menuBook.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuBook:
                Intent intentAppointment = new Intent(getApplicationContext(), AppointmentSlotCreator.class);
                intentAppointment.putExtra("DOCTOR_ID", DOCTOR_ID);
                intentAppointment.putExtra("CLINIC_ID", CLINIC_ID);
                startActivity(intentAppointment);
                break;
            case R.id.menuCall:
                if (ContextCompat.checkSelfPermission(DoctorDetailsNew.this,
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED)   {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE))    {
                        /* SHOW THE DIALOG */
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setCancelable(false);
                        builder.setIcon(R.drawable.ic_info_outline_black_24dp);
                        builder.setTitle("Permission Required");
                        builder.setMessage("\nZen Pets requires the permission to call the Doctor's phone number. \n\nFor a seamless experience, we recommend granting Zen Pets this permission.");
                        builder.setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(
                                        DoctorDetailsNew.this,
                                        new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_CONSTANT);
                            }
                        });
                        builder.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                CALL_PHONE_CONSTANT);
                    }
                } else {
                    /* CALL THE PHONE NUMBER */
                    callPhone();
                }
                break;
            case R.id.menuFeedback:
                /* CHECK IF THE USER HAS POSTED A REVIEW FOR THIS DOCTOR */
                checkReviewStatus();
                break;
            default:
                break;
        }
        return false;
    }

    /* CHECK IF THE USER HAS POSTED A REVIEW FOR THIS DOCTOR */
    private void checkReviewStatus() {
        ReviewsAPI api = ZenApiClient.getClient().create(ReviewsAPI.class);
        Call<Review> call = api.checkUserDoctorReview(USER_ID, DOCTOR_ID);
        call.enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                Log.e("REVIEW RESPONSE", String.valueOf(response.raw()));
                Review review = response.body();
                if (review != null) {
                    if (review.getError())  {
                        String profileStatus = getApp().getProfileStatus();
                        if (profileStatus.equalsIgnoreCase("Incomplete"))   {
                            String message = "You need to complete your Profile before you can provide feedback. To complete your profile Details, click on the \"Complete Profile\" button.";
                            new MaterialDialog.Builder(DoctorDetailsNew.this)
                                    .icon(ContextCompat.getDrawable(DoctorDetailsNew.this, R.drawable.ic_info_outline_black_24dp))
                                    .title("Profile Incomplete")
                                    .cancelable(true)
                                    .content(message)
                                    .positiveText("Complete Profile")
                                    .theme(Theme.LIGHT)
                                    .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            Intent intent = new Intent(DoctorDetailsNew.this, ProfileEditor.class);
                                            startActivity(intent);
                                        }
                                    }).show();
                        } else {
                            Intent intentNewFeedback = new Intent(getApplicationContext(), ReviewCreator.class);
                            intentNewFeedback.putExtra("DOCTOR_ID", DOCTOR_ID);
                            intentNewFeedback.putExtra("CLINIC_ID", CLINIC_ID);
                            startActivity(intentNewFeedback);
                        }
                    } else {
                        String reviewID = review.getReviewID();
                        Intent intent = new Intent(DoctorDetailsNew.this, ReviewModifier.class);
                        intent.putExtra("REVIEW_ID", reviewID);
                        intent.putExtra("CLINIC_ID", CLINIC_ID);
                        startActivityForResult(intent, 101);
                    }
                } else {
                    String profileStatus = getApp().getProfileStatus();
                    if (profileStatus.equalsIgnoreCase("Incomplete"))   {
                        String message = "You need to complete your Profile before you can provide feedback. To complete your profile Details, click on the \"Complete Profile\" button.";
                        new MaterialDialog.Builder(DoctorDetailsNew.this)
                                .icon(ContextCompat.getDrawable(DoctorDetailsNew.this, R.drawable.ic_info_outline_black_24dp))
                                .title("Profile Incomplete")
                                .cancelable(true)
                                .content(message)
                                .positiveText("Complete Profile")
                                .theme(Theme.LIGHT)
                                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        Intent intent = new Intent(DoctorDetailsNew.this, ProfileEditor.class);
                                        startActivity(intent);
                                    }
                                }).show();
                    } else {
                        Intent intentNewFeedback = new Intent(getApplicationContext(), ReviewCreator.class);
                        intentNewFeedback.putExtra("DOCTOR_ID", DOCTOR_ID);
                        intentNewFeedback.putExtra("CLINIC_ID", CLINIC_ID);
                        startActivity(intentNewFeedback);
                    }
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
//                Log.e("CHECK FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CALL_PHONE_CONSTANT)   {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                /* CALL THE PHONE NUMBER */
                callPhone();
            } else {
                /* DIAL THE PHONE NUMBER */
                dialPhone();
            }
        }
    }

    /***** CALL THE PHONE NUMBER *****/
    private void callPhone() {
        if (ContextCompat.checkSelfPermission(DoctorDetailsNew.this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED)   {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE))    {
                /* SHOW THE DIALOG */
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setIcon(R.drawable.ic_info_outline_black_24dp);
                builder.setTitle("Permission Required");
                builder.setMessage("\nZen Pets requires the permission to call the Doctor's phone number. \n\nFor a seamless experience, we recommend granting Zen Pets this permission.");
                builder.setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(
                                DoctorDetailsNew.this,
                                new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_CONSTANT);
                    }
                });
                builder.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        CALL_PHONE_CONSTANT);
            }
        } else {
            String myData= "tel:" + DOCTOR_PHONE_NUMBER;
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(myData));
            startActivity(intent);
        }
    }

    /***** DIAL THE PHONE NUMBER *****/
    private void dialPhone() {
        String myData= "tel:" + DOCTOR_PHONE_NUMBER;
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(myData));
        startActivity(intent);
    }

    /***** FETCH THE DOCTOR'S EDUCATIONAL QUALIFICATIONS *****/
    private void fetchDoctorEducation() {
        QualificationsAPI apiInterface = ZenApiClient.getClient().create(QualificationsAPI.class);
        Call<Qualifications> call = apiInterface.fetchDoctorEducation(DOCTOR_ID);
        call.enqueue(new Callback<Qualifications>() {
            @Override
            public void onResponse(Call<Qualifications> call, Response<Qualifications> response) {
                StringBuilder sb;
                ArrayList<Qualification> arrEducation = response.body().getQualifications();
                if (arrEducation != null && arrEducation.size() > 0)    {
                    sb = new StringBuilder();
                    for (int i = 0; i < arrEducation.size(); i++) {
                        String education = arrEducation.get(i).getDoctorEducationName();
                        sb.append(education).append(", ");
                    }
                } else {
                    sb = null;
                }

                if (sb != null) {
                    String strEducation = sb.toString();
                    if (strEducation.endsWith(", "))    {
                        strEducation = strEducation.substring(0, strEducation.length() - 2);
                        txtDoctorEducation.setText(strEducation);
                    } else {
                        txtDoctorEducation.setText(strEducation);
                    }
                }
            }

            @Override
            public void onFailure(Call<Qualifications> call, Throwable t) {
            }
        });
    }

    /***** FETCH THE DOCTOR'S SUBSCRIPTION *****/
    private void fetchDoctorSubscription() {
        SubscriptionsAPI api = ZenApiClient.getClient().create(SubscriptionsAPI.class);
        Call<SubscriptionData> call = api.fetchDoctorSubscription(DOCTOR_ID);
        call.enqueue(new Callback<SubscriptionData>() {
            @Override
            public void onResponse(Call<SubscriptionData> call, Response<SubscriptionData> response) {
                SubscriptionData data = response.body();
                if (data != null) {
                    /* GET THE SUBSCRIPTION ID */
                    String subscriptionID = data.getSubscriptionID();

                    if (subscriptionID != null && !subscriptionID.equalsIgnoreCase("")) {
                        /* PROCESS THE SUBSCRIPTION LEVEL AND INVALIDATE THE OPTIONS MENU */
                        blnSubscriptionStatus = !subscriptionID.equalsIgnoreCase("1");
                        invalidateOptionsMenu();
                    } else {
                        blnSubscriptionStatus = false;
                        invalidateOptionsMenu();
                    }
                }
            }

            @Override
            public void onFailure(Call<SubscriptionData> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH CLINIC IMAGES *****/
    private void fetchClinicImages() {
        ClinicImagesAPI api = ZenApiClient.getClient().create(ClinicImagesAPI.class);
        Call<ClinicImages> call = api.fetchClinicImages(CLINIC_ID);
        call.enqueue(new Callback<ClinicImages>() {
            @Override
            public void onResponse(Call<ClinicImages> call, Response<ClinicImages> response) {
                if (response.body() != null && response.body().getImages() != null) {
                    arrImages = response.body().getImages();
                    if (arrImages.size() > 0) {
                        /* SET THE SERVICES ADAPTER TO THE RECYCLER VIEW */
                        listClinicImages.setAdapter(new ClinicImagesAdapter(DoctorDetailsNew.this, arrImages));

                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT  */
                        linlaClinicImages.setVisibility(View.VISIBLE);
                        linlaNoClinicImages.setVisibility(View.GONE);
                    } else {
                        /* HIDE THE IMAGES CONTAINER  */
                        linlaImagesContainer.setVisibility(View.GONE);
                    }
                } else {
                    /* HIDE THE IMAGES CONTAINER  */
                    linlaImagesContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ClinicImages> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Bundle bundle = new Bundle(outState);
        clinicMap.onSaveInstanceState(bundle);
        outState.putBundle("clinic_map_save_state", bundle);
        super.onSaveInstanceState(outState);
    }
}