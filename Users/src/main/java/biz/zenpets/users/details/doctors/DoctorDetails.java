package biz.zenpets.users.details.doctors;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.adapters.clinics.ClinicImagesAdapter;
import biz.zenpets.users.utils.adapters.reviews.ReviewsAdapter;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.helpers.clinics.ratings.FetchClinicRatings;
import biz.zenpets.users.utils.helpers.clinics.ratings.FetchClinicRatingsInterface;
import biz.zenpets.users.utils.helpers.doctors.details.FetchDoctorDetails;
import biz.zenpets.users.utils.helpers.doctors.details.FetchDoctorDetailsInterface;
import biz.zenpets.users.utils.helpers.doctors.education.FetchDoctorEducation;
import biz.zenpets.users.utils.helpers.doctors.education.FetchDoctorEducationInterface;
import biz.zenpets.users.utils.helpers.doctors.subscription.FetchDoctorSubscription;
import biz.zenpets.users.utils.helpers.doctors.subscription.FetchDoctorSubscriptionInterface;
import biz.zenpets.users.utils.helpers.timings.FetchDoctorTimings;
import biz.zenpets.users.utils.helpers.timings.FetchDoctorTimingsInterface;
import biz.zenpets.users.utils.helpers.timings.friday.FridayAfternoonTimings;
import biz.zenpets.users.utils.helpers.timings.friday.FridayAfternoonTimingsInterface;
import biz.zenpets.users.utils.helpers.timings.friday.FridayMorningTimings;
import biz.zenpets.users.utils.helpers.timings.friday.FridayMorningTimingsInterface;
import biz.zenpets.users.utils.helpers.timings.monday.MondayAfternoonTimings;
import biz.zenpets.users.utils.helpers.timings.monday.MondayAfternoonTimingsInterface;
import biz.zenpets.users.utils.helpers.timings.monday.MondayMorningTimings;
import biz.zenpets.users.utils.helpers.timings.monday.MondayMorningTimingsInterface;
import biz.zenpets.users.utils.helpers.timings.saturday.SaturdayAfternoonTimings;
import biz.zenpets.users.utils.helpers.timings.saturday.SaturdayAfternoonTimingsInterface;
import biz.zenpets.users.utils.helpers.timings.saturday.SaturdayMorningTimings;
import biz.zenpets.users.utils.helpers.timings.saturday.SaturdayMorningTimingsInterface;
import biz.zenpets.users.utils.helpers.timings.sunday.SundayAfternoonTimings;
import biz.zenpets.users.utils.helpers.timings.sunday.SundayAfternoonTimingsInterface;
import biz.zenpets.users.utils.helpers.timings.sunday.SundayMorningTimings;
import biz.zenpets.users.utils.helpers.timings.sunday.SundayMorningTimingsInterface;
import biz.zenpets.users.utils.helpers.timings.thursday.ThursdayAfternoonTimings;
import biz.zenpets.users.utils.helpers.timings.thursday.ThursdayAfternoonTimingsInterface;
import biz.zenpets.users.utils.helpers.timings.thursday.ThursdayMorningTimings;
import biz.zenpets.users.utils.helpers.timings.thursday.ThursdayMorningTimingsInterface;
import biz.zenpets.users.utils.helpers.timings.tuesday.TuesdayAfternoonTimings;
import biz.zenpets.users.utils.helpers.timings.tuesday.TuesdayAfternoonTimingsInterface;
import biz.zenpets.users.utils.helpers.timings.tuesday.TuesdayMorningTimings;
import biz.zenpets.users.utils.helpers.timings.tuesday.TuesdayMorningTimingsInterface;
import biz.zenpets.users.utils.helpers.timings.wednesday.WednesdayAfternoonTimings;
import biz.zenpets.users.utils.helpers.timings.wednesday.WednesdayAfternoonTimingsInterface;
import biz.zenpets.users.utils.helpers.timings.wednesday.WednesdayMorningTimings;
import biz.zenpets.users.utils.helpers.timings.wednesday.WednesdayMorningTimingsInterface;
import biz.zenpets.users.utils.models.clinics.images.ClinicImage;
import biz.zenpets.users.utils.models.reviews.Review;
import biz.zenpets.users.utils.models.reviews.Reviews;
import biz.zenpets.users.utils.models.reviews.ReviewsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ConstantConditions")
public class DoctorDetails extends AppCompatActivity
        implements FetchDoctorDetailsInterface, FetchClinicRatingsInterface,
        FetchDoctorEducationInterface, FetchDoctorSubscriptionInterface,
        /*FetchClinicImagesInterface,*/ FetchDoctorTimingsInterface,
        SundayMorningTimingsInterface, SundayAfternoonTimingsInterface,
        MondayMorningTimingsInterface, MondayAfternoonTimingsInterface,
        TuesdayMorningTimingsInterface, TuesdayAfternoonTimingsInterface,
        WednesdayMorningTimingsInterface, WednesdayAfternoonTimingsInterface,
        ThursdayMorningTimingsInterface, ThursdayAfternoonTimingsInterface,
        FridayMorningTimingsInterface, FridayAfternoonTimingsInterface,
        SaturdayMorningTimingsInterface, SaturdayAfternoonTimingsInterface {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE INCOMING CLINIC ID AND THE DOCTOR ID **/
    private String DOCTOR_ID = null;
    private String CLINIC_ID = null;

    /** TODAY'S DAY **/
    private String TODAY_DAY = null;

    /** DATA TYPE TO STORE THE DATA **/
    private String CLINIC_NAME;
    private String CLINIC_ADDRESS;
    private Double CLINIC_LATITUDE;
    private Double CLINIC_LONGITUDE;
    private String DOCTOR_PREFIX;
    private String DOCTOR_NAME;
    private String DOCTOR_PHONE_NUMBER = null;

    /* THE SERVICES AND SUBSET ADAPTER AND ARRAY LISTS **/
//    ServicesAdapter servicesAdapter;
//    final ArrayList<ServicesData> arrServicesSubset = new ArrayList<>();
//    final ArrayList<ServicesData> arrServices = new ArrayList<>();

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
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.imgvwClinicCover) SimpleDraweeView imgvwClinicCover;
    @BindView(R.id.imgvwDoctorProfile) SimpleDraweeView imgvwDoctorProfile;
    @BindView(R.id.txtDoctorName) AppCompatTextView txtDoctorName;
    @BindView(R.id.txtDoctorEducation) AppCompatTextView txtDoctorEducation;
    @BindView(R.id.linlaExperience) LinearLayout linlaExperience;
    @BindView(R.id.txtExperience) AppCompatTextView txtExperience;
    @BindView(R.id.linlaVotes) LinearLayout linlaVotes;
    @BindView(R.id.txtVotes) AppCompatTextView txtVotes;
    @BindView(R.id.txtDoctorCharges) AppCompatTextView txtDoctorCharges;
    @BindView(R.id.txtClinicName) AppCompatTextView txtClinicName;
    @BindView(R.id.clinicRating) AppCompatRatingBar clinicRating;
    @BindView(R.id.txtClinicAddress) AppCompatTextView txtClinicAddress;
    @BindView(R.id.clinicMap) MapView clinicMap;
    @BindView(R.id.linlaTimingMorning) LinearLayout linlaTimingMorning;
    @BindView(R.id.txtMorningOpen) AppCompatTextView txtMorningOpen;
    @BindView(R.id.txtTimingsMorning) AppCompatTextView txtTimingsMorning;
    @BindView(R.id.txtMorningClosed) AppCompatTextView txtMorningClosed;
    @BindView(R.id.linlaTimingAfternoon) LinearLayout linlaTimingAfternoon;
    @BindView(R.id.txtAfternoonOpen) AppCompatTextView txtAfternoonOpen;
    @BindView(R.id.txtTimingAfternoon) AppCompatTextView txtTimingAfternoon;
    @BindView(R.id.txtAfternoonClosed) AppCompatTextView txtAfternoonClosed;
    @BindView(R.id.linlaReviewsProgress) LinearLayout linlaReviewsProgress;
    @BindView(R.id.linlaReviews) LinearLayout linlaReviews;
    @BindView(R.id.listReviews) RecyclerView listReviews;
    @BindView(R.id.linlaNoReviews) LinearLayout linlaNoReviews;
    @BindView(R.id.linlaImagesContainer) LinearLayout linlaImagesContainer;
    @BindView(R.id.linlaClinicImages) LinearLayout linlaClinicImages;
    @BindView(R.id.listClinicImages) RecyclerView listClinicImages;
    @BindView(R.id.linlaNoClinicImages) LinearLayout linlaNoClinicImages;
//    @BindView(R.id.linlaServices) LinearLayout linlaServices;
//    @BindView(R.id.listServices) RecyclerView listServices;
//    @BindView(R.id.linlaNoServices) LinearLayout linlaNoServices;
//    @BindView(R.id.btnBook) AppCompatButton btnBook;

    /** THE CUSTOM TIMINGS LAYOUT ELEMENTS **/
    private AppCompatTextView txtSunMorning;
    private AppCompatTextView txtSunAfternoon;
    private AppCompatTextView txtMonMorning;
    private AppCompatTextView txtMonAfternoon;
    private AppCompatTextView txtTueMorning;
    private AppCompatTextView txtTueAfternoon;
    private AppCompatTextView txtWedMorning;
    private AppCompatTextView txtWedAfternoon;
    private AppCompatTextView txtThuMorning;
    private AppCompatTextView txtThuAfternoon;
    private AppCompatTextView txtFriMorning;
    private AppCompatTextView txtFriAfternoon;
    private AppCompatTextView txtSatMorning;
    private AppCompatTextView txtSatAfternoon;

    /* THE CUSTOM SERVICES LAYOUT ELEMENTS **/
//    RecyclerView listDoctorServices;

    /** THE ALL CUSTOM VIEWS **/
    private View custAllTimings;
//    private ConsultationView custAllServices;

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
//    @OnClick(R.id.txtAllServices) void showAllServices()    {
//        showDoctorServices();
//    }

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_details);
        ButterKnife.bind(this);
        clinicMap.onCreate(savedInstanceState != null ? savedInstanceState.getBundle("clinic_map_save_state") : null);
        clinicMap.onResume();
        clinicMap.setClickable(false);

        /* INFLATE THE CUSTOM VIEWS */
        custAllTimings = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_all_timings_view, null);
//        custAllServices = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_all_services_list, null);

        /* INSTANTIATE THE ADAPTERS **/
//        servicesAdapter = new ServicesAdapter(DoctorDetail.this, arrServices);
        reviewsAdapter = new ReviewsAdapter(DoctorDetails.this, arrReviewsSubset);
        imagesAdapter = new ClinicImagesAdapter(DoctorDetails.this, arrImages);

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* GET TODAY'S DAY */
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        Date d = new Date();
        TODAY_DAY = sdf.format(d);
//        Log.e("TODAY", TODAY_DAY);

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* FETCH THE DOCTOR'S SUBSCRIPTION */
        new FetchDoctorSubscription(this).execute(DOCTOR_ID);

        /* FETCH THE DOCTOR'S EDUCATIONAL QUALIFICATIONS */
        new FetchDoctorEducation(this).execute(DOCTOR_ID);

//        /** FETCH THE FIRST 3 SERVICES OFFERED BY THE DOCTOR **/
//        new fetchServicesSubset().execute();

        /* SHOW THE PROGRESS AND FETCH THE FIRST 3 REVIEWS FOR THE DOCTOR */
        linlaReviewsProgress.setVisibility(View.VISIBLE);
        listReviews.setVisibility(View.GONE);
        fetchDoctorReviews();
//        new FetchDoctorReviewsSubset(this).execute(DOCTOR_ID);

        /* FETCH CLINIC IMAGES */
//        new FetchClinicImages(this).execute(CLINIC_ID);

        /* CONFIGURE THE TOOLBAR */
        configTB();
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
                        listReviews.setAdapter(new ReviewsAdapter(DoctorDetails.this, arrReviewsSubset));
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
//                Log.e("REVIEWS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    @Override
    public void onDoctorDetails(String[] result) {
        /* GET THE RESULTS FROM THE ARRAY */
        DOCTOR_PREFIX = result[0];
        DOCTOR_NAME = result[1];
        String DOCTOR_PROFILE = result[2];
        String CLINIC_LOGO = result[3];
        String DOCTOR_EXPERIENCE = result[4];
        String CLINIC_CURRENCY = result[5];
        String DOCTOR_CHARGES = result[6];
        CLINIC_NAME = result[7];
        CLINIC_ADDRESS = result[8];
        String CLINIC_CITY = result[9];
        String CLINIC_STATE = result[10];
        String CLINIC_PIN_CODE = result[11];
        String CLINIC_LANDMARK = result[12];
        DOCTOR_PHONE_NUMBER = result[13];
        CLINIC_LATITUDE = Double.valueOf(result[14]);
        CLINIC_LONGITUDE = Double.valueOf(result[15]);
        String DOCTOR_LIKES_PERCENT = result[16];
        String DOCTOR_VOTES = result[17];

        /* SET THE DOCTOR'S NAME */
        txtDoctorName.setText(getString(R.string.doctor_details_doc_name_placeholder, DOCTOR_PREFIX, DOCTOR_NAME));

        /* SET THE DOCTOR'S DISPLAY PROFILE */
        Uri uri = Uri.parse(DOCTOR_PROFILE);
        imgvwDoctorProfile.setImageURI(uri);

        /* SET THE CLINIC COVER PICTURE */
        if (CLINIC_LOGO != null)    {
            Uri uriClinic = Uri.parse(CLINIC_LOGO);
            imgvwClinicCover.setImageURI(uriClinic);
        } else {
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.ic_business_black_24dp)
                    .build();
            imgvwClinicCover.setImageURI(request.getSourceUri());
        }

        /* SET THE EXPERIENCE (IN YEARS) */
        txtExperience.setText(getString(R.string.doctor_details_doc_exp_placeholder, DOCTOR_EXPERIENCE));

        /* SET THE CLINIC NAME */
        txtClinicName.setText(CLINIC_NAME);

        /* SET THE DOCTOR CHARGES */
        txtDoctorCharges.setText(getString(R.string.doctor_details_doc_charges_placeholder, CLINIC_CURRENCY, DOCTOR_CHARGES));

        /* SET THE CLINIC ADDRESS */
        txtClinicAddress.setText(getString(R.string.doctor_details_address_placeholder, CLINIC_ADDRESS, CLINIC_CITY, CLINIC_STATE, CLINIC_PIN_CODE));

        /* SET THE CLINIC'S LOCATION ON THE MAP */
        if (CLINIC_LATITUDE != null && CLINIC_LONGITUDE != null) {
            final LatLng latLng = new LatLng(CLINIC_LATITUDE, CLINIC_LONGITUDE);
            clinicMap.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
//                    MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(DoctorDetails.this, R.raw.zen_map_style);
//                    googleMap.setMapStyle(mapStyleOptions);
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

        /* SET THE VOTES PERCENTAGE AND NUMBER OF VOTES*/
        txtVotes.setText(getString(R.string.doctor_details_votes_placeholder, DOCTOR_LIKES_PERCENT, DOCTOR_VOTES));

        /* GET THE CLINIC RATING */
        new FetchClinicRatings(this).execute(CLINIC_ID);

        /* GET THE TIMINGS DEPENDING ON TODAY'S DAY */
        if (TODAY_DAY.equalsIgnoreCase("Sunday"))   {
            new SundayMorningTimings(DoctorDetails.this).execute(DOCTOR_ID, CLINIC_ID);
            new SundayAfternoonTimings(DoctorDetails.this).execute(DOCTOR_ID, CLINIC_ID);
        } else if (TODAY_DAY.equalsIgnoreCase("Monday"))    {
            new MondayMorningTimings(DoctorDetails.this).execute(DOCTOR_ID, CLINIC_ID);
            new MondayAfternoonTimings(DoctorDetails.this).execute(DOCTOR_ID, CLINIC_ID);
        } else if (TODAY_DAY.equalsIgnoreCase("Tuesday"))   {
            new TuesdayMorningTimings(DoctorDetails.this).execute(DOCTOR_ID, CLINIC_ID);
            new TuesdayAfternoonTimings(DoctorDetails.this).execute(DOCTOR_ID, CLINIC_ID);
        } else if (TODAY_DAY.equalsIgnoreCase("Wednesday")) {
            new WednesdayMorningTimings(DoctorDetails.this).execute(DOCTOR_ID, CLINIC_ID);
            new WednesdayAfternoonTimings(DoctorDetails.this).execute(DOCTOR_ID, CLINIC_ID);
        } else if (TODAY_DAY.equalsIgnoreCase("Thursday"))  {
            new ThursdayMorningTimings(DoctorDetails.this).execute(DOCTOR_ID, CLINIC_ID);
            new ThursdayAfternoonTimings(DoctorDetails.this).execute(DOCTOR_ID, CLINIC_ID);
        } else if (TODAY_DAY.equalsIgnoreCase("Friday"))    {
            new FridayMorningTimings(DoctorDetails.this).execute(DOCTOR_ID, CLINIC_ID);
            new FridayAfternoonTimings(DoctorDetails.this).execute(DOCTOR_ID, CLINIC_ID);
        } else if (TODAY_DAY.equalsIgnoreCase("Saturday"))  {
            new SaturdayMorningTimings(DoctorDetails.this).execute(DOCTOR_ID, CLINIC_ID);
            new SaturdayAfternoonTimings(DoctorDetails.this).execute(DOCTOR_ID, CLINIC_ID);
        }

        /* HIDE THE PROGRESS AFTER LOADING THE DATA */
        linlaProgress.setVisibility(View.GONE);
    }

    @Override
    public void clinicRatings(String rating) {
        if (rating != null && !rating.equalsIgnoreCase("null")) {
            clinicRating.setRating(Float.parseFloat(rating));
        } else {
            clinicRating.setRating(0);
        }
    }

    @Override
    public void onDoctorSubscription(Boolean result) {
        /* CAST THE RESULT IN THE BOOLEAN FLAG */
        blnSubscriptionStatus = result;

        /* INVALIDATE THE OPTIONS MENU */
        invalidateOptionsMenu();
    }

    @Override
    public void onEducationResult(StringBuilder builder) {
        if (builder != null) {
            String strEducation = builder.toString();
            if (strEducation.endsWith(", "))    {
                strEducation = strEducation.substring(0, strEducation.length() - 2);
                txtDoctorEducation.setText(strEducation);
            } else {
                txtDoctorEducation.setText(strEducation);
            }
        } else {
            txtDoctorEducation.setText(getString(R.string.doctor_details_doc_education_empty));
        }
    }

//    @Override
//    public void onReviewSubset(ArrayList<Review> data) {
//        /* CAST THE RESULTS IN THE GLOBAL INSTANCE */
//        arrReviewsSubset = data;
//
//        if (arrReviewsSubset.size() > 0)    {
//            /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY REVIEWS VIEW */
//            linlaReviews.setVisibility(View.VISIBLE);
//            linlaNoReviews.setVisibility(View.GONE);
//            listReviews.setVisibility(View.VISIBLE);
//
//            /* SET THE REVIEWS ADAPTER TO THE RECYCLER VIEW */
//            listReviews.setAdapter(new ReviewsAdapter(DoctorDetails.this, arrReviewsSubset));
//        } else {
//            /* SHOW THE NO REVIEWS LAYOUT */
//            linlaNoReviews.setVisibility(View.VISIBLE);
//            linlaReviews.setVisibility(View.GONE);
//            listReviews.setVisibility(View.GONE);
//        }
//
//        /* HIDE THE PROGRESS AFTER FETCHING THE REVIEWS */
//        linlaReviewsProgress.setVisibility(View.GONE);
//    }

//    @Override
//    public void onClinicImages(ArrayList<ClinicImagesData> data) {
//        /* CAST THE RESULTS IN THE GLOBAL INSTANCE */
//        arrImages = data;
//
//        if (arrImages.size() > 0)   {
//            /* SET THE SERVICES ADAPTER TO THE RECYCLER VIEW */
//            listClinicImages.setAdapter(new ClinicImagesAdapter(DoctorDetails.this, arrImages));
//
//            /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT  */
//            linlaClinicImages.setVisibility(View.VISIBLE);
//            linlaNoClinicImages.setVisibility(View.GONE);
//        } else {
//            /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
//            linlaNoClinicImages.setVisibility(View.VISIBLE);
//            linlaClinicImages.setVisibility(View.GONE);
//        }
//    }

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
        MaterialDialog dialog = new MaterialDialog.Builder(DoctorDetails.this)
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
        new FetchDoctorTimings(this).execute(DOCTOR_ID, CLINIC_ID);

        /* SHOW THE DIALOG */
        dialog.show();
    }

    @Override
    public void onDoctorTimings(String[] result) {
        /* GET THE RESULTS */
        String SUN_MOR_FROM = result[0];
        String SUN_MOR_TO = result[1];
        String SUN_AFT_FROM = result[2];
        String SUN_AFT_TO = result[3];
        String MON_MOR_FROM = result[4];
        String MON_MOR_TO = result[5];
        String MON_AFT_FROM = result[6];
        String MON_AFT_TO = result[7];
        String TUE_MOR_FROM = result[8];
        String TUE_MOR_TO = result[9];
        String TUE_AFT_FROM = result[10];
        String TUE_AFT_TO = result[11];
        String WED_MOR_FROM = result[12];
        String WED_MOR_TO = result[13];
        String WED_AFT_FROM = result[14];
        String WED_AFT_TO = result[15];
        String THU_MOR_FROM = result[16];
        String THU_MOR_TO = result[17];
        String THU_AFT_FROM = result[18];
        String THU_AFT_TO = result[19];
        String FRI_MOR_FROM = result[20];
        String FRI_MOR_TO = result[21];
        String FRI_AFT_FROM = result[22];
        String FRI_AFT_TO = result[23];
        String SAT_MOR_FROM = result[24];
        String SAT_MOR_TO = result[25];
        String SAT_AFT_FROM = result[26];
        String SAT_AFT_TO = result[27];

        /* SET THE SUNDAY MORNING TIMINGS */
        if (SUN_MOR_FROM != null && SUN_MOR_TO != null) {
            txtSunMorning.setText(getString(R.string.doctor_details_timings_placeholder, SUN_MOR_FROM, SUN_MOR_TO));
//                txtSunMorning.setText(SUN_MOR_FROM + " - " + SUN_MOR_TO);
        } else {
            txtSunMorning.setText(R.string.doctor_details_timings_closed);
        }

        /* SET THE SUNDAY AFTERNOON TIMINGS */
        if (SUN_AFT_FROM != null && SUN_AFT_TO != null) {
            txtSunAfternoon.setText(getString(R.string.doctor_details_timings_placeholder, SUN_AFT_FROM, SUN_AFT_TO));
        } else {
            txtSunAfternoon.setText(R.string.doctor_details_timings_closed);
        }

        /* SET THE MONDAY MORNING TIMINGS */
        if (MON_MOR_FROM != null && MON_MOR_TO != null) {
            txtMonMorning.setText(getString(R.string.doctor_details_timings_placeholder, MON_MOR_FROM, MON_MOR_TO));
        } else {
            txtMonMorning.setText(R.string.doctor_details_timings_closed);
        }

        /* SET THE MONDAY AFTERNOON TIMINGS */
        if (MON_AFT_FROM != null && MON_AFT_TO != null) {
            txtMonAfternoon.setText(getString(R.string.doctor_details_timings_placeholder, MON_AFT_FROM, MON_AFT_TO));
        } else {
            txtMonAfternoon.setText(R.string.doctor_details_timings_closed);
        }

        /* SET THE TUESDAY MORNING TIMINGS */
        if (TUE_MOR_FROM != null && TUE_MOR_TO != null) {
            txtTueMorning.setText(getString(R.string.doctor_details_timings_placeholder, TUE_MOR_FROM, TUE_MOR_TO));
        } else {
            txtTueMorning.setText(R.string.doctor_details_timings_closed);
        }

        /* SET THE TUESDAY AFTERNOON TIMINGS */
        if (TUE_AFT_FROM != null && TUE_AFT_TO != null) {
            txtTueAfternoon.setText(getString(R.string.doctor_details_timings_placeholder, TUE_AFT_FROM, TUE_AFT_TO));
        } else {
            txtTueAfternoon.setText(R.string.doctor_details_timings_closed);
        }

        /* SET THE WEDNESDAY MORNING TIMINGS */
        if (WED_MOR_FROM != null && WED_MOR_TO != null) {
            txtWedMorning.setText(getString(R.string.doctor_details_timings_placeholder, WED_MOR_FROM, WED_MOR_TO));
        } else {
            txtWedMorning.setText(R.string.doctor_details_timings_closed);
        }

        /* SET THE WEDNESDAY AFTERNOON TIMINGS */
        if (WED_AFT_FROM != null && WED_AFT_TO != null) {
            txtWedAfternoon.setText(getString(R.string.doctor_details_timings_placeholder, WED_AFT_FROM, WED_AFT_TO));
        } else {
            txtWedAfternoon.setText(R.string.doctor_details_timings_closed);
        }

        /* SET THE THURSDAY MORNING TIMINGS */
        if (THU_MOR_FROM != null && THU_MOR_TO != null)   {
            txtThuMorning.setText(getString(R.string.doctor_details_timings_placeholder, THU_MOR_FROM, THU_MOR_TO));
        } else {
            txtThuMorning.setText(R.string.doctor_details_timings_closed);
        }

        /* SET THE THURSDAY AFTERNOON TIMINGS */
        if (THU_AFT_FROM != null && THU_AFT_TO != null) {
            txtThuAfternoon.setText(getString(R.string.doctor_details_timings_placeholder, THU_AFT_FROM, THU_AFT_TO));
        } else {
            txtThuAfternoon.setText(R.string.doctor_details_timings_closed);
        }

        /* SET THE FRIDAY MORNING TIMINGS */
        if (FRI_MOR_FROM != null && FRI_MOR_TO != null) {
            txtFriMorning.setText(getString(R.string.doctor_details_timings_placeholder, FRI_MOR_FROM, FRI_MOR_TO));
        } else {
            txtFriMorning.setText(R.string.doctor_details_timings_closed);
        }

        /* SET THE FRIDAY AFTERNOON TIMINGS */
        if (FRI_AFT_FROM != null && FRI_AFT_TO != null) {
            txtFriAfternoon.setText(getString(R.string.doctor_details_timings_placeholder, FRI_AFT_FROM, FRI_AFT_FROM));
        } else {
            txtFriAfternoon.setText(R.string.doctor_details_timings_closed);
        }

        /* SET THE SATURDAY MORNING TIMINGS */
        if (SAT_MOR_FROM != null && SAT_MOR_TO != null) {
            txtSatMorning.setText(getString(R.string.doctor_details_timings_placeholder, SAT_MOR_FROM, SAT_MOR_TO));
        } else {
            txtSatMorning.setText(R.string.doctor_details_timings_closed);
        }

        /* SET THE SATURDAY AFTERNOON TIMINGS */
        if (SAT_AFT_FROM != null && SAT_AFT_TO != null) {
            txtSatAfternoon.setText(getString(R.string.doctor_details_timings_placeholder, SAT_AFT_FROM, SAT_AFT_TO));
        } else {
            txtSatAfternoon.setText(R.string.doctor_details_timings_closed);
        }
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
//        LinearLayoutManager services = new LinearLayoutManager(this);
//        services.setOrientation(LinearLayoutManager.VERTICAL);
//        listServices.setLayoutManager(services);
//        listServices.setHasFixedSize(true);
//        listServices.setAdapter(servicesAdapter);

        LinearLayoutManager reviews = new LinearLayoutManager(this);
        reviews.setOrientation(LinearLayoutManager.VERTICAL);
        reviews.setAutoMeasureEnabled(true);
        listReviews.setLayoutManager(reviews);
        listReviews.setHasFixedSize(true);
        listReviews.setNestedScrollingEnabled(false);
        listReviews.setAdapter(reviewsAdapter);

        LinearLayoutManager llmClinicImages = new LinearLayoutManager(this);
        llmClinicImages.setOrientation(LinearLayoutManager.HORIZONTAL);
        llmClinicImages.setAutoMeasureEnabled(true);
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
                && bundle.containsKey("CLINIC_ID"))    {
            DOCTOR_ID = bundle.getString("DOCTOR_ID");
            CLINIC_ID = bundle.getString("CLINIC_ID");
            if (DOCTOR_ID != null && CLINIC_ID != null)  {
                /* SHOW THE PROGRESS BAR AND FETCH THE DOCTOR DETAILS **/
                linlaProgress.setVisibility(View.VISIBLE);
                new FetchDoctorDetails(this).execute(DOCTOR_ID, CLINIC_ID);
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onSundayMorningResult(String[] response) {
        String morningFrom = response[0];
        String morningTo = response[1];

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
    }

    @Override
    public void onSundayAfternoonResult(String[] response) {
        String afternoonFrom = response[0];
        String afternoonTo = response[1];

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
    }

    @Override
    public void onMondayMorningResult(String[] response) {
        String morningFrom = response[0];
        String morningTo = response[1];

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
    }

    @Override
    public void onMondayAfternoonResult(String[] response) {
        String afternoonFrom = response[0];
        String afternoonTo = response[1];

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
    }

    @Override
    public void onTuesdayMorningResult(String[] response) {
        String morningFrom = response[0];
        String morningTo = response[1];

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
    }

    @Override
    public void onTuesdayAfternoonResult(String[] response) {
        String afternoonFrom = response[0];
        String afternoonTo = response[1];

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
    }

    @Override
    public void onWednesdayMorningResult(String[] response) {
        String morningFrom = response[0];
        String morningTo = response[1];

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
    }

    @Override
    public void onWednesdayAfternoonResult(String[] response) {
        String afternoonFrom = response[0];
        String afternoonTo = response[1];

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
    }

    @Override
    public void onThursdayMorningResult(String[] response) {
        String morningFrom = response[0];
        String morningTo = response[1];

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
    }

    @Override
    public void onThursdayAfternoonResult(String[] response) {
        String afternoonFrom = response[0];
        String afternoonTo = response[1];

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
    }

    @Override
    public void onFridayMorningResult(String[] response) {
        String morningFrom = response[0];
        String morningTo = response[1];

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
    }

    @Override
    public void onFridayAfternoonResult(String[] response) {
        String afternoonFrom = response[0];
        String afternoonTo = response[1];

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
    }

    @Override
    public void onSaturdayMorningResult(String[] response) {
        String morningFrom = response[0];
        String morningTo = response[1];

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
    }

    @Override
    public void onSaturdayAfternoonResult(String[] response) {
        String afternoonFrom = response[0];
        String afternoonTo = response[1];

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
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "DoctorProfile Details";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(strTitle);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(DoctorDetails.this);
        inflater.inflate(R.menu.activity_doctor_details, menu);

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
                if (ContextCompat.checkSelfPermission(DoctorDetails.this,
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED)   {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE))    {
                        /* SHOW THE DIALOG */
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setCancelable(false);
                        builder.setIcon(R.drawable.ic_info_outline_black_24dp);
                        builder.setTitle("Permission Required");
                        builder.setMessage("\nZen Pets requires the permission to call the DoctorProfile's phone number. \n\nFor a seamless experience, we recommend granting Zen Pets this permission.");
                        builder.setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(
                                        DoctorDetails.this,
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
                String profileStatus = getApp().getProfileStatus();
                if (profileStatus.equalsIgnoreCase("Incomplete"))   {
                    String message = "You need to complete your Profile before you can provide feedback. To complete your profile Details, click on the \"Complete Profile\" button.";
                    new MaterialDialog.Builder(DoctorDetails.this)
                            .icon(ContextCompat.getDrawable(DoctorDetails.this, R.drawable.ic_info_outline_black_24dp))
                            .title("Profile Incomplete")
                            .cancelable(true)
                            .content(message)
                            .positiveText("Complete Profile")
                            .theme(Theme.LIGHT)
                            .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    Intent intent = new Intent(DoctorDetails.this, ProfileEditor.class);
                                    startActivity(intent);
                                }
                            }).show();
                } else {
                    Intent intentNewFeedback = new Intent(getApplicationContext(), ReviewCreator.class);
                    intentNewFeedback.putExtra("DOCTOR_ID", DOCTOR_ID);
                    intentNewFeedback.putExtra("CLINIC_ID", CLINIC_ID);
                    startActivity(intentNewFeedback);
                }
                break;
            default:
                break;
        }
        return false;
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
        if (ContextCompat.checkSelfPermission(DoctorDetails.this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED)   {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE))    {
                /* SHOW THE DIALOG */
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setIcon(R.drawable.ic_info_outline_black_24dp);
                builder.setTitle("Permission Required");
                builder.setMessage("\nZen Pets requires the permission to call the DoctorProfile's phone number. \n\nFor a seamless experience, we recommend granting Zen Pets this permission.");
                builder.setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(
                                DoctorDetails.this,
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