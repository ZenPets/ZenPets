package biz.zenpets.users.details.groomer;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import biz.zenpets.users.creator.profile.ProfileEditor;
import biz.zenpets.users.details.groomer.enquiry.GroomerEnquiryActivity;
import biz.zenpets.users.details.groomer.reviews.GroomerReviewCreator;
import biz.zenpets.users.modifier.groomer.GroomerReviewModifier;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.adapters.groomers.images.GroomerImagesAdapter;
import biz.zenpets.users.utils.adapters.groomers.reviews.GroomerReviewsAdapter;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.groomers.groomers.Groomer;
import biz.zenpets.users.utils.models.groomers.groomers.GroomersAPI;
import biz.zenpets.users.utils.models.groomers.images.GroomerImage;
import biz.zenpets.users.utils.models.groomers.images.GroomerImages;
import biz.zenpets.users.utils.models.groomers.images.GroomerImagesAPI;
import biz.zenpets.users.utils.models.groomers.review.GroomerReview;
import biz.zenpets.users.utils.models.groomers.review.GroomerReviews;
import biz.zenpets.users.utils.models.groomers.review.GroomerReviewsAPI;
import biz.zenpets.users.utils.models.groomers.statistics.GroomerStats;
import biz.zenpets.users.utils.models.groomers.statistics.GroomerStatsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroomerDetails extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN USER'S ID **/
    private String USER_ID = null;

    /** THE INCOMING GROOMER ID **/
    private String GROOMER_ID = null;

    /** THE INCOMING ORIGIN LATITUDE AND LONGITUDE **/
    String ORIGIN_LATITUDE = null;
    String ORIGIN_LONGITUDE = null;

    /** THE CURRENT DATE **/
    String CURRENT_DATE = null;

    /** THE REVIEWS ADAPTER AND ARRAY LISTS **/
    private ArrayList<GroomerReview> arrReviewsSubset = new ArrayList<>();

    /** THE KENNEL IMAGES ARRAY LIST INSTANCE **/
    private ArrayList<GroomerImage> arrImages = new ArrayList<>();

    /** DATA TYPES TO HOLD THE GROOMER'S DETAILS **/
    String GROOMER_LOGO = null;
    String GROOMER_NAME = null;
    String GROOMER_ADDRESS = null;
    String CITY_NAME = null;
    String STATE_NAME = null;
    String GROOMER_PIN_CODE = null;
    String GROOMER_PHONE_NUMBER_1 = null;
    String GROOMER_PHONE_NUMBER_2 = null;
    Double GROOMER_LATITUDE = null;
    Double GROOMER_LONGITUDE = null;
    String GROOMER_DISTANCE = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.appBar) AppBarLayout appBar;
    @BindView(R.id.toolbarLayout) CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.imgvwGroomerLogo) SimpleDraweeView imgvwGroomerLogo;
    @BindView(R.id.txtGroomerName) TextView txtGroomerName;
    @BindView(R.id.txtVotes) TextView txtVotes;
    @BindView(R.id.groomerRating) RatingBar groomerRating;
    @BindView(R.id.linlaPhoneNumber1) LinearLayout linlaPhoneNumber1;
    @BindView(R.id.txtPhoneNumber1) TextView txtPhoneNumber1;
    @BindView(R.id.linlaPhoneNumber2) LinearLayout linlaPhoneNumber2;
    @BindView(R.id.txtPhoneNumber2) TextView txtPhoneNumber2;
    @BindView(R.id.txtGroomerAddress) TextView txtGroomerAddress;
    @BindView(R.id.groomerMap) MapView groomerMap;
    @BindView(R.id.txtGroomerDistance) TextView txtGroomerDistance;
    @BindView(R.id.linlaReviewsProgress) LinearLayout linlaReviewsProgress;
    @BindView(R.id.listReviews) RecyclerView listReviews;
    @BindView(R.id.txtAllReviews) TextView txtAllReviews;
    @BindView(R.id.linlaNoReviews) LinearLayout linlaNoReviews;
    @BindView(R.id.linlaImagesProgress) LinearLayout linlaImagesProgress;
    @BindView(R.id.listGroomerImages) RecyclerView listGroomerImages;
    @BindView(R.id.linlaNoImages) LinearLayout linlaNoImages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groomer_details);
        ButterKnife.bind(this);
        groomerMap.onCreate(savedInstanceState != null ? savedInstanceState.getBundle("groomer_map_save_state") : null);
        groomerMap.onResume();
        groomerMap.setClickable(false);

        /* GET THE USER'S ID */
        USER_ID = getApp().getUserID();

        /* GET THE CURRENT DATE IN THE REQUIRED FORMAT */
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        CURRENT_DATE = format.format(date);
//        Log.e("CURRENT DATE", CURRENT_DATE);

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

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

        /* PUBLISH A NEW GROOMER CLICK STATUS */
        publishClickStatus();
    }

    /** FETCH THE GROOMER DETAILS **/
    private void fetchGroomerDetails() {
        GroomersAPI api = ZenApiClient.getClient().create(GroomersAPI.class);
        Call<Groomer> call = api.fetchGroomerDetails(GROOMER_ID, ORIGIN_LATITUDE, ORIGIN_LONGITUDE);
        call.enqueue(new Callback<Groomer>() {
            @Override
            public void onResponse(Call<Groomer> call, Response<Groomer> response) {
                Log.e("GROOMER DETAILS", String.valueOf(response.raw()));
                Groomer data = response.body();
                if (data != null)   {

                    /* GET AND SET THE GROOMER'S LOGO */
                    GROOMER_LOGO = data.getGroomerLogo();
                    if (GROOMER_LOGO != null)    {
                        Uri uriClinic = Uri.parse(GROOMER_LOGO);
                        imgvwGroomerLogo.setImageURI(uriClinic);
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.ic_business_black_24dp)
                                .build();
                        imgvwGroomerLogo.setImageURI(request.getSourceUri());
                    }

                    /* GET AND SET THE GROOMER NAME */
                    GROOMER_NAME = data.getGroomerName();
                    if (GROOMER_NAME != null)    {
                        txtGroomerName.setText(data.getGroomerName());
                        toolbarLayout.setTitleEnabled(false);
                        toolbarLayout.setTitle(data.getGroomerName());
                    }

                    /* GET AND SET THE GROOMER'S ADDRESS */
                    GROOMER_ADDRESS = data.getGroomerAddress();
                    CITY_NAME = data.getCityName();
                    STATE_NAME = data.getStateName();
                    GROOMER_PIN_CODE = data.getGroomerPincode();
                    txtGroomerAddress.setText(getString(R.string.doctor_details_address_placeholder, GROOMER_ADDRESS, CITY_NAME, STATE_NAME, GROOMER_PIN_CODE));

                    /* GET AND SET THE GROOMER'S PHONE NUMBER 1 */
                    GROOMER_PHONE_NUMBER_1 = data.getGroomerPhoneNumber1();
                    if (GROOMER_PHONE_NUMBER_1 != null
                            && !GROOMER_PHONE_NUMBER_1.equalsIgnoreCase("")
                            && !GROOMER_PHONE_NUMBER_1.equalsIgnoreCase("null")) {
                        linlaPhoneNumber1.setVisibility(View.VISIBLE);
                        txtPhoneNumber1.setText(GROOMER_PHONE_NUMBER_1);
                    } else {
                        linlaPhoneNumber1.setVisibility(View.GONE);
                    }

                    /* GET AND SET THE GROOMER'S PHONE NUMBER 2 */
                    GROOMER_PHONE_NUMBER_2 = data.getGroomerPhoneNumber2();
                    if (GROOMER_PHONE_NUMBER_2 != null
                            && !GROOMER_PHONE_NUMBER_2.equalsIgnoreCase("")
                            && !GROOMER_PHONE_NUMBER_2.equalsIgnoreCase("null")) {
                        linlaPhoneNumber2.setVisibility(View.VISIBLE);
                        txtPhoneNumber2.setText(GROOMER_PHONE_NUMBER_2);
                    } else {
                        linlaPhoneNumber2.setVisibility(View.GONE);
                    }

                    /* GET AND SET THE GROOMER'S LATITUDE AND LONGITUDE ON THE MAP */
                    GROOMER_LATITUDE = Double.valueOf(data.getGroomerLatitude());
                    GROOMER_LONGITUDE = Double.valueOf(data.getGroomerLongitude());
                    if (GROOMER_LATITUDE != null && GROOMER_LONGITUDE != null) {
                        final LatLng latLng = new LatLng(GROOMER_LATITUDE, GROOMER_LONGITUDE);
                        groomerMap.getMapAsync(new OnMapReadyCallback() {
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
//                                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//                                    @Override
//                                    public void onMapClick(LatLng latLng) {
//                                        Intent intent = new Intent(getApplicationContext(), MapDetails.class);
//                                        intent.putExtra("DOCTOR_ID", DOCTOR_ID);
//                                        intent.putExtra("DOCTOR_NAME", DOCTOR_PREFIX + " " + DOCTOR_NAME);
//                                        intent.putExtra("KENNEL_PHONE_NUMBER_1", KENNEL_PHONE_NUMBER_1);
//                                        intent.putExtra("CLINIC_ID", CLINIC_ID);
//                                        intent.putExtra("CLINIC_NAME", CLINIC_NAME);
//                                        intent.putExtra("CLINIC_LATITUDE", KENNEL_LATITUDE);
//                                        intent.putExtra("CLINIC_LONGITUDE", KENNEL_LONGITUDE);
//                                        intent.putExtra("CLINIC_ADDRESS", CLINIC_ADDRESS);
//                                        startActivity(intent);
//                                    }
//                                });
                            }
                        });
                    }

                    /* GET THE GROOMER'S DISTANCE */
                    GROOMER_DISTANCE = data.getGroomerDistance();
                    String strTilde = getString(R.string.generic_tilde);
                    txtGroomerDistance.setText(getString(R.string.doctor_list_clinic_distance_placeholder, strTilde, GROOMER_DISTANCE));

                    /* GET THE REVIEW COUNT */
                    String countReviews = data.getGroomerReviews();
                    txtAllReviews.setText(getString(R.string.doctor_details_all_reviews_placeholder, String.valueOf(countReviews)));

                    /* GET THE TOTAL REVIEWS, POSITIVE, AND FINALLY, CALCULATE THE PERCENTAGES */
                    String groomerReviews = data.getGroomerReviews();
                    String groomerPositives = data.getGroomerPositives();

                    int TOTAL_VOTES = Integer.parseInt(groomerReviews);
                    int TOTAL_LIKES = Integer.parseInt(groomerPositives);

                    /* CALCULATE THE PERCENTAGE OF LIKES */
                    double percentLikes = ((double)TOTAL_LIKES / TOTAL_VOTES) * 100;
                    int finalPercentLikes = (int)percentLikes;
                    String strLikesPercentage = String.valueOf(finalPercentLikes) + "%";

                    /* GET THE TOTAL NUMBER OF REVIEWS / VOTES */
                    Resources resReviews = getResources();
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

                    /* SET THE GROOMER'S AVERAGE RATING */
                    if (data.getGroomerRating() != null && !data.getGroomerRating().equalsIgnoreCase("null")) {
                        Float dblRating = Float.valueOf(data.getGroomerRating());
                        groomerRating.setRating(dblRating);
                    } else {
                        groomerRating.setRating(0);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Groomer> call, Throwable t) {
//                Log.e("KENNEL FAILURES", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** FETCH THE FIRST 3 REVIEWS FOR THE GROOMER **/
    private void fetchGroomerReviews() {
        GroomerReviewsAPI api = ZenApiClient.getClient().create(GroomerReviewsAPI.class);
        Call<GroomerReviews> call = api.fetchGroomerReviewsSubset(GROOMER_ID);
        call.enqueue(new Callback<GroomerReviews>() {
            @Override
            public void onResponse(Call<GroomerReviews> call, Response<GroomerReviews> response) {
                Log.e("REVIEWS RAW", String.valueOf(response.raw()));
                if (response.body() != null && response.body().getReviews() != null)    {
                    arrReviewsSubset = response.body().getReviews();
                    if (arrReviewsSubset.size() > 0)    {
                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY REVIEWS VIEW */
                        linlaNoReviews.setVisibility(View.GONE);
                        listReviews.setVisibility(View.VISIBLE);
                        txtAllReviews.setVisibility(View.VISIBLE);

                        /* SET THE REVIEWS ADAPTER TO THE RECYCLER VIEW */
                        listReviews.setAdapter(new GroomerReviewsAdapter(GroomerDetails.this, arrReviewsSubset));
                    } else {
                        /* SHOW THE NO REVIEWS LAYOUT */
                        linlaNoReviews.setVisibility(View.VISIBLE);
                        txtAllReviews.setVisibility(View.GONE);
                        listReviews.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE NO REVIEWS LAYOUT */
                    linlaNoReviews.setVisibility(View.VISIBLE);
                    txtAllReviews.setVisibility(View.GONE);
                    listReviews.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE REVIEWS */
                linlaReviewsProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<GroomerReviews> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /** FETCH THE LIST OF THE GROOMER'S IMAGES **/
    private void fetchGroomerImages() {
        GroomerImagesAPI api = ZenApiClient.getClient().create(GroomerImagesAPI.class);
        Call<GroomerImages> call = api.fetchGroomerImages(GROOMER_ID);
        call.enqueue(new Callback<GroomerImages>() {
            @Override
            public void onResponse(Call<GroomerImages> call, Response<GroomerImages> response) {
                if (response.body() != null && response.body().getImages() != null) {
                    arrImages = response.body().getImages();
                    if (arrImages.size() > 0) {
                        /* SET THE SERVICES ADAPTER TO THE RECYCLER VIEW */
                        listGroomerImages.setAdapter(new GroomerImagesAdapter(GroomerDetails.this, arrImages));

                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY IMAGES VIEW */
                        linlaNoImages.setVisibility(View.GONE);
                        listGroomerImages.setVisibility(View.VISIBLE);
                    } else {
                        /* SHOW THE NO IMAGES LAYOUT */
                        linlaNoImages.setVisibility(View.VISIBLE);
                        listGroomerImages.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE NO IMAGES LAYOUT */
                    linlaNoImages.setVisibility(View.VISIBLE);
                    listGroomerImages.setVisibility(View.GONE);
                }

                /* HIDE THE IMAGES PROGRESS AFTER FETCHING THE DATA */
                linlaImagesProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<GroomerImages> call, Throwable t) {
                Log.e("IMAGES FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** GET THE INCOMING DATA **/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null
                && bundle.containsKey("GROOMER_ID")
                && bundle.containsKey("ORIGIN_LATITUDE")
                && bundle.containsKey("ORIGIN_LONGITUDE"))  {
            GROOMER_ID = bundle.getString("GROOMER_ID");
            ORIGIN_LATITUDE = bundle.getString("ORIGIN_LATITUDE");
            ORIGIN_LONGITUDE = bundle.getString("ORIGIN_LONGITUDE");
            if (GROOMER_ID != null && ORIGIN_LATITUDE != null && ORIGIN_LONGITUDE != null)  {
                Log.e("LATITUDE", ORIGIN_LATITUDE);
                Log.e("LONGITUDE", ORIGIN_LONGITUDE);
                /* FETCH THE GROOMER DETAILS */
                fetchGroomerDetails();

                /* SHOW THE PROGRESS AND FETCH THE FIRST 3 REVIEWS FOR THE GROOMER */
                linlaReviewsProgress.setVisibility(View.VISIBLE);
                listReviews.setVisibility(View.GONE);
                txtAllReviews.setVisibility(View.GONE);
                fetchGroomerReviews();

                /* SHOW THE IMAGES PROGRESS AND FETCH THE LIST OF THE GROOMER'S IMAGES */
                linlaImagesProgress.setVisibility(View.VISIBLE);
                fetchGroomerImages();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /** PUBLISH A NEW GROOMER CLICK STATUS **/
    private void publishClickStatus() {
        GroomerStatsAPI api = ZenApiClient.getClient().create(GroomerStatsAPI.class);
        Call<GroomerStats> call = api.publishGroomerClickStatus(GROOMER_ID, USER_ID, CURRENT_DATE);
        call.enqueue(new Callback<GroomerStats>() {
            @Override
            public void onResponse(Call<GroomerStats> call, Response<GroomerStats> response) {
//                if (response.body().getMessage() != null)
//                    Log.e("MESSAGE", response.body().getMessage());
            }

            @Override
            public void onFailure(Call<GroomerStats> call, Throwable t) {
//                Log.e("DISPLAYED FAILED", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        groomerMap.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        groomerMap.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        groomerMap.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        groomerMap.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        groomerMap.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Bundle bundle = new Bundle(outState);
        groomerMap.onSaveInstanceState(bundle);
        outState.putBundle("groomer_map_save_state", bundle);
        super.onSaveInstanceState(outState);
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
        MenuInflater inflater = new MenuInflater(GroomerDetails.this);
        inflater.inflate(R.menu.activity_kennel_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuEnquire:
                Intent intent = new Intent(GroomerDetails.this, GroomerEnquiryActivity.class);
                intent.putExtra("GROOMER_ID", GROOMER_ID);
                intent.putExtra("GROOMER_NAME", GROOMER_NAME);
                intent.putExtra("GROOMER_LOGO", GROOMER_LOGO);
                startActivity(intent);
                break;
            case R.id.menuFeedback:
                /* CHECK IF THE USER HAS POSTED A REVIEW FOR THIS GROOMER */
                checkReviewStatus();
                break;
            default:
                break;
        }
        return false;
    }

    /** CHECK IF THE USER HAS POSTED A REVIEW FOR THIS GROOMER **/
    private void checkReviewStatus() {
        GroomerReviewsAPI api = ZenApiClient.getClient().create(GroomerReviewsAPI.class);
        Call<GroomerReview> call = api.checkUserGroomerReview(USER_ID, GROOMER_ID);
        call.enqueue(new Callback<GroomerReview>() {
            @Override
            public void onResponse(Call<GroomerReview> call, Response<GroomerReview> response) {
                Log.e("RESPONSE RAW", String.valueOf(response.raw()));
                if (!response.body().getError()) {
                    GroomerReview review = response.body();
                    if (review != null) {
                        String reviewID = review.getReviewID();
                        Intent intent = new Intent(GroomerDetails.this, GroomerReviewModifier.class);
                        intent.putExtra("REVIEW_ID", reviewID);
                        startActivityForResult(intent, 102);
                    }
                } else {
                    String profileStatus = getApp().getProfileStatus();
                    if (profileStatus.equalsIgnoreCase("Incomplete"))   {
                        String message = "You need to complete your Profile before you can provide feedback. To complete your profile Details, click on the \"Complete Profile\" button.";
                        new MaterialDialog.Builder(GroomerDetails.this)
                                .icon(ContextCompat.getDrawable(GroomerDetails.this, R.drawable.ic_info_outline_black_24dp))
                                .title("Profile Incomplete")
                                .cancelable(true)
                                .content(message)
                                .positiveText("Complete Profile")
                                .theme(Theme.LIGHT)
                                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        Intent intent = new Intent(GroomerDetails.this, ProfileEditor.class);
                                        startActivity(intent);
                                    }
                                }).show();
                    } else {
                        Intent intentNewFeedback = new Intent(getApplicationContext(), GroomerReviewCreator.class);
                        intentNewFeedback.putExtra("GROOMER_ID", GROOMER_ID);
                        startActivityForResult(intentNewFeedback, 101);
                    }
                }
            }

            @Override
            public void onFailure(Call<GroomerReview> call, Throwable t) {
//                Log.e("CHECK FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        LinearLayoutManager reviews = new LinearLayoutManager(this);
        reviews.setOrientation(LinearLayoutManager.VERTICAL);
        reviews.isAutoMeasureEnabled();
        listReviews.setLayoutManager(reviews);
        listReviews.setHasFixedSize(true);
        listReviews.setNestedScrollingEnabled(false);
        listReviews.setAdapter(new GroomerReviewsAdapter(GroomerDetails.this, arrReviewsSubset));

        LinearLayoutManager llmClinicImages = new LinearLayoutManager(this);
        llmClinicImages.setOrientation(LinearLayoutManager.HORIZONTAL);
        llmClinicImages.isAutoMeasureEnabled();
        listGroomerImages.setLayoutManager(llmClinicImages);
        listGroomerImages.setHasFixedSize(true);
        listGroomerImages.setNestedScrollingEnabled(false);
        listGroomerImages.setAdapter(new GroomerImagesAdapter(GroomerDetails.this, arrImages));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)    {
            if (requestCode == 101 || requestCode == 102)   {
                /* CLEAR THE ARRAY LIST */
                arrReviewsSubset.clear();

                /* SHOW THE REVIEWS PROGRESS AND FETCH THE REVIEW AGAIN */
                linlaReviewsProgress.setVisibility(View.VISIBLE);
                listReviews.setVisibility(View.GONE);
                txtAllReviews.setVisibility(View.GONE);
                linlaNoReviews.setVisibility(View.GONE);
                fetchGroomerReviews();
            }
        }
    }
}