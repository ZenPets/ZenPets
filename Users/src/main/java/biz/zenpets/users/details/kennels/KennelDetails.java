package biz.zenpets.users.details.kennels;

import android.Manifest;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.creator.profile.ProfileEditor;
import biz.zenpets.users.details.kennels.reviews.KennelReviewCreator;
import biz.zenpets.users.details.kennels.reviews.KennelReviewsActivity;
import biz.zenpets.users.modifier.kennel.KennelReviewModifier;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.adapters.kennels.images.KennelImagesAdapter;
import biz.zenpets.users.utils.adapters.kennels.reviews.KennelReviewsAdapter;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.kennels.images.KennelImage;
import biz.zenpets.users.utils.models.kennels.images.KennelImages;
import biz.zenpets.users.utils.models.kennels.images.KennelImagesAPI;
import biz.zenpets.users.utils.models.kennels.kennels.Kennel;
import biz.zenpets.users.utils.models.kennels.kennels.KennelsAPI;
import biz.zenpets.users.utils.models.kennels.reviews.KennelReview;
import biz.zenpets.users.utils.models.kennels.reviews.KennelReviews;
import biz.zenpets.users.utils.models.kennels.reviews.KennelReviewsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KennelDetails extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN USER'S ID **/
    private String USER_ID = null;

    /** THE INCOMING KENNEL ID **/
    private String KENNEL_ID = null;

    /** THE INCOMING ORIGIN LATITUDE AND LONGITUDE **/
    String ORIGIN_LATITUDE = null;
    String ORIGIN_LONGITUDE = null;

    /** A KENNEL API AND KENNEL REVIEWS API INSTANCE **/
    private final KennelsAPI apiKennel = ZenApiClient.getClient().create(KennelsAPI.class);
    private final KennelReviewsAPI apiReview = ZenApiClient.getClient().create(KennelReviewsAPI.class);

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int CALL_PHONE_CONSTANT = 200;

    /** THE REVIEWS ADAPTER AND ARRAY LISTS **/
    private ArrayList<KennelReview> arrReviewsSubset = new ArrayList<>();

    /** THE KENNEL IMAGES ARRAY LIST INSTANCE **/
    private ArrayList<KennelImage> arrImages = new ArrayList<>();

    /** DATA TYPES TO HOLD THE KENNEL DETAILS **/
    private String KENNEL_COVER_PHOTO = null;
    private String KENNEL_NAME = null;
    private String KENNEL_OWNER_ID = null;
    private String KENNEL_OWNER_DISPLAY_PROFILE = null;
    private String KENNEL_OWNER_NAME = null;
    private String KENNEL_ADDRESS = null;
    private String CITY_NAME = null;
    private String STATE_NAME = null;
    private String KENNEL_PIN_CODE = null;
    private Double KENNEL_LATITUDE = null;
    private Double KENNEL_LONGITUDE = null;
    String KENNEL_DISTANCE = null;
    private String KENNEL_PHONE_NUMBER_1 = null;
    private String KENNEL_PHONE_NUMBER_2 = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.appBar) AppBarLayout appBar;
    @BindView(R.id.toolbarLayout) CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.imgvwKennelCoverPhoto) ImageView imgvwKennelCoverPhoto;
    @BindView(R.id.imgvwKennelOwnerDisplayProfile) CircleImageView imgvwKennelOwnerDisplayProfile;
    @BindView(R.id.txtKennelName) TextView txtKennelName;
    @BindView(R.id.txtKennelOwnerName) TextView txtKennelOwnerName;
    @BindView(R.id.txtVotes) TextView txtVotes;
    @BindView(R.id.kennelRating) RatingBar kennelRating;
    @BindView(R.id.linlaPhoneNumber1) LinearLayout linlaPhoneNumber1;
    @BindView(R.id.txtPhoneNumber1) TextView txtPhoneNumber1;
    @BindView(R.id.linlaPhoneNumber2) LinearLayout linlaPhoneNumber2;
    @BindView(R.id.txtPhoneNumber2) TextView txtPhoneNumber2;
    @BindView(R.id.txtKennelAddress) TextView txtKennelAddress;
    @BindView(R.id.kennelMap) MapView kennelMap;
    @BindView(R.id.txtKennelDistance) TextView txtKennelDistance;
    @BindView(R.id.linlaReviewsProgress) LinearLayout linlaReviewsProgress;
    @BindView(R.id.listReviews) RecyclerView listReviews;
    @BindView(R.id.txtAllReviews) TextView txtAllReviews;
    @BindView(R.id.linlaNoReviews) LinearLayout linlaNoReviews;
    @BindView(R.id.linlaImagesProgress) LinearLayout linlaImagesProgress;
    @BindView(R.id.listKennelImages) RecyclerView listKennelImages;
    @BindView(R.id.linlaNoImages) LinearLayout linlaNoImages;

//    /** SHOW THE CHARGES INFO **/
//    @OnClick(R.id.imgvwChargesInfo) void showChargesInfo()  {
//    }

    /** SHOW ALL THE KENNEL REVIEWS **/
    @OnClick(R.id.txtAllReviews) void showAllReviews()  {
        Intent intent = new Intent(getApplicationContext(), KennelReviewsActivity.class);
        intent.putExtra("KENNEL_ID", KENNEL_ID);
        intent.putExtra("KENNEL_NAME", KENNEL_NAME);
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kennel_details);
        ButterKnife.bind(this);
        kennelMap.onCreate(savedInstanceState != null ? savedInstanceState.getBundle("kennel_map_save_state") : null);
        kennelMap.onResume();
        kennelMap.setClickable(false);

        /* GET THE USER'S ID */
        USER_ID = getApp().getUserID();

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
    }

    /** FETCH THE KENNEL DETAILS **/
    private void fetchKennelDetails() {
        Call<Kennel> call = apiKennel.fetchKennelDetails(KENNEL_ID, ORIGIN_LATITUDE, ORIGIN_LONGITUDE);
        call.enqueue(new Callback<Kennel>() {
            @Override
            public void onResponse(Call<Kennel> call, Response<Kennel> response) {
//                Log.e("RAW RESPONSE", String.valueOf(response.raw()));
                Kennel data = response.body();
                if (data != null)   {

                    /* GET AND SET THE KENNEL'S COVER PHOTO */
                    KENNEL_COVER_PHOTO = data.getKennelCoverPhoto();
//                    Log.e("COVER PHOTO", KENNEL_COVER_PHOTO);
                    if (KENNEL_COVER_PHOTO != null) {
                        Picasso.with(KennelDetails.this)
                                .load(KENNEL_COVER_PHOTO)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .fit()
                                .centerCrop()
                                .into(imgvwKennelCoverPhoto, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(KennelDetails.this)
                                                .load(KENNEL_COVER_PHOTO)
                                                .fit()
                                                .centerCrop()
                                                .error(R.drawable.ic_business_black_24dp)
                                                .into(imgvwKennelCoverPhoto, new com.squareup.picasso.Callback() {
                                                    @Override
                                                    public void onSuccess() {
                                                    }

                                                    @Override
                                                    public void onError() {
//                                                        Log.e("Picasso","Could not fetch image");
                                                    }
                                                });
                                    }
                                });
                    }

                    /* GET AND SET THE KENNEL NAME */
                    KENNEL_NAME = data.getKennelName();
                    if (KENNEL_NAME != null)    {
                        txtKennelName.setText(data.getKennelName());
                        toolbarLayout.setTitleEnabled(false);
                        toolbarLayout.setTitle(data.getKennelName());
                    }

                    /* GET THE KENNEL OWNER'S ID */
                    KENNEL_OWNER_ID = data.getKennelOwnerID();

                    /* GET AND SET THE KENNEL OWNER'S DISPLAY PROFILE */
                    KENNEL_OWNER_DISPLAY_PROFILE = data.getKennelOwnerDisplayProfile();
                    if (KENNEL_OWNER_DISPLAY_PROFILE != null) {
                        Picasso.with(KennelDetails.this)
                                .load(KENNEL_OWNER_DISPLAY_PROFILE)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .fit()
                                .centerCrop()
                                .into(imgvwKennelOwnerDisplayProfile, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(KennelDetails.this)
                                                .load(KENNEL_OWNER_DISPLAY_PROFILE)
                                                .fit()
                                                .centerCrop()
                                                .error(R.drawable.ic_person_black_24dp)
                                                .into(imgvwKennelOwnerDisplayProfile, new com.squareup.picasso.Callback() {
                                                    @Override
                                                    public void onSuccess() {
                                                    }

                                                    @Override
                                                    public void onError() {
//                                                        Log.e("Picasso","Could not fetch image");
                                                    }
                                                });
                                    }
                                });
                    }

                    /* GET AND SET THE KENNEL OWNER'S NAME */
                    KENNEL_OWNER_NAME = data.getKennelOwnerName();
                    if (KENNEL_OWNER_NAME != null)  {
                        txtKennelOwnerName.setText(KENNEL_OWNER_NAME);
                    }

                    /* GET AND SET THE KENNEL ADDRESS */
                    KENNEL_ADDRESS = data.getKennelAddress();
                    CITY_NAME = data.getCityName();
                    STATE_NAME = data.getStateName();
                    KENNEL_PIN_CODE = data.getKennelPinCode();
                    txtKennelAddress.setText(getString(R.string.doctor_details_address_placeholder, KENNEL_ADDRESS, CITY_NAME, STATE_NAME, KENNEL_PIN_CODE));

                    /* GET AND SET THE KENNEL PHONE NUMBER 1 */
                    KENNEL_PHONE_NUMBER_1 = data.getKennelPhoneNumber1();
                    if (KENNEL_PHONE_NUMBER_1 != null
                            && !KENNEL_PHONE_NUMBER_1.equalsIgnoreCase("")
                            && !KENNEL_PHONE_NUMBER_1.equalsIgnoreCase("null")) {
                        linlaPhoneNumber1.setVisibility(View.VISIBLE);
                        txtPhoneNumber1.setText(KENNEL_PHONE_NUMBER_1);
                    } else {
                        linlaPhoneNumber1.setVisibility(View.GONE);
                    }

                    /* GET AND SET THE KENNEL PHONE NUMBER 2 */
                    KENNEL_PHONE_NUMBER_2 = data.getKennelPhoneNumber2();
                    if (KENNEL_PHONE_NUMBER_2 != null
                            && !KENNEL_PHONE_NUMBER_2.equalsIgnoreCase("")
                            && !KENNEL_PHONE_NUMBER_2.equalsIgnoreCase("null")) {
                        linlaPhoneNumber2.setVisibility(View.VISIBLE);
                        txtPhoneNumber2.setText(KENNEL_PHONE_NUMBER_2);
                    } else {
                        linlaPhoneNumber2.setVisibility(View.GONE);
                    }

                    /* GET AND SET THE CLINIC LATITUDE AND LONGITUDE ON THE MAP */
                    KENNEL_LATITUDE = Double.valueOf(data.getKennelLatitude());
                    KENNEL_LONGITUDE = Double.valueOf(data.getKennelLongitude());
                    if (KENNEL_LATITUDE != null && KENNEL_LONGITUDE != null) {
                        final LatLng latLng = new LatLng(KENNEL_LATITUDE, KENNEL_LONGITUDE);
                        kennelMap.getMapAsync(new OnMapReadyCallback() {
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

                    /* GET THE KENNEL DISTANCE */
                    KENNEL_DISTANCE = data.getKennelDistance();
                    String strTilde = getString(R.string.generic_tilde);
                    txtKennelDistance.setText(getString(R.string.doctor_list_clinic_distance_placeholder, strTilde, KENNEL_DISTANCE));

                    /* GET THE REVIEW COUNT */
                    String countReviews = data.getKennelReviews();
                    txtAllReviews.setText(getString(R.string.doctor_details_all_reviews_placeholder, String.valueOf(countReviews)));

                    /* GET THE TOTAL REVIEWS, POSITIVE, AND FINALLY, CALCULATE THE PERCENTAGES */
                    String kennelReviews = data.getKennelReviews();
                    String kennelPositives = data.getKennelPositives();

                    int TOTAL_VOTES = Integer.parseInt(kennelReviews);
                    int TOTAL_LIKES = Integer.parseInt(kennelPositives);

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

                    /* SET THE AVERAGE KENNEL RATING */
                    if (data.getKennelRating() != null && !data.getKennelRating().equalsIgnoreCase("null")) {
                        Float dblRating = Float.valueOf(data.getKennelRating());
                        kennelRating.setRating(dblRating);
                    } else {
                        kennelRating.setRating(0);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Kennel> call, Throwable t) {
//                Log.e("KENNEL FAILURES", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE KENNEL'S REVIEWS *****/
    private void fetchKennelReviews() {
        Call<KennelReviews> call = apiReview.fetchKennelReviewsSubset(KENNEL_ID);
        call.enqueue(new Callback<KennelReviews>() {
            @Override
            public void onResponse(Call<KennelReviews> call, Response<KennelReviews> response) {
                Log.e("REVIEWS RAW", String.valueOf(response.raw()));
                if (response.body() != null && response.body().getReviews() != null)    {
                    arrReviewsSubset = response.body().getReviews();
                    if (arrReviewsSubset.size() > 0)    {
                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY REVIEWS VIEW */
                        linlaNoReviews.setVisibility(View.GONE);
                        listReviews.setVisibility(View.VISIBLE);
                        txtAllReviews.setVisibility(View.VISIBLE);

                        /* SET THE REVIEWS ADAPTER TO THE RECYCLER VIEW */
                        listReviews.setAdapter(new KennelReviewsAdapter(KennelDetails.this, arrReviewsSubset));
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
            public void onFailure(Call<KennelReviews> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /** FETCH THE LIST OF KENNEL IMAGES **/
    private void fetchKennelImages() {
        KennelImagesAPI api = ZenApiClient.getClient().create(KennelImagesAPI.class);
        Call<KennelImages> call = api.fetchKennelImages(KENNEL_ID);
        call.enqueue(new Callback<KennelImages>() {
            @Override
            public void onResponse(Call<KennelImages> call, Response<KennelImages> response) {
                if (response.body() != null && response.body().getImages() != null) {
                    arrImages = response.body().getImages();
                    if (arrImages.size() > 0) {
                        /* SET THE SERVICES ADAPTER TO THE RECYCLER VIEW */
                        listKennelImages.setAdapter(new KennelImagesAdapter(KennelDetails.this, arrImages));

                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY IMAGES VIEW */
                        linlaNoImages.setVisibility(View.GONE);
                        listKennelImages.setVisibility(View.VISIBLE);
                    } else {
                        /* SHOW THE NO IMAGES LAYOUT */
                        linlaNoImages.setVisibility(View.VISIBLE);
                        listKennelImages.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE NO IMAGES LAYOUT */
                    linlaNoImages.setVisibility(View.VISIBLE);
                    listKennelImages.setVisibility(View.GONE);
                }

                /* HIDE THE IMAGES PROGRESS AFTER FETCHING THE DATA */
                linlaImagesProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<KennelImages> call, Throwable t) {
            }
        });
    }

    /** GET THE INCOMING DATA **/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null
                && bundle.containsKey("KENNEL_ID")
                && bundle.containsKey("ORIGIN_LATITUDE")
                && bundle.containsKey("ORIGIN_LONGITUDE"))  {
            KENNEL_ID = bundle.getString("KENNEL_ID");
            ORIGIN_LATITUDE = bundle.getString("ORIGIN_LATITUDE");
            ORIGIN_LONGITUDE = bundle.getString("ORIGIN_LONGITUDE");
            if (KENNEL_ID != null && ORIGIN_LATITUDE != null && ORIGIN_LONGITUDE != null)  {
//                Log.e("LATITUDE", ORIGIN_LATITUDE);
//                Log.e("LONGITUDE", ORIGIN_LONGITUDE);
                /* FETCH THE KENNEL DETAILS */
                fetchKennelDetails();

                /* SHOW THE PROGRESS AND FETCH THE FIRST 3 REVIEWS FOR THE DOCTOR */
                linlaReviewsProgress.setVisibility(View.VISIBLE);
                listReviews.setVisibility(View.GONE);
                txtAllReviews.setVisibility(View.GONE);
                fetchKennelReviews();

                /* SHOW THE IMAGES PROGRESS AND FETCH THE LIST OF KENNEL IMAGES */
                linlaImagesProgress.setVisibility(View.VISIBLE);
                fetchKennelImages();
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
        MenuInflater inflater = new MenuInflater(KennelDetails.this);
        inflater.inflate(R.menu.activity_kennel_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuCall:
                if (ContextCompat.checkSelfPermission(KennelDetails.this,
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
                                        KennelDetails.this,
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
                /* CHECK IF THE USER HAS POSTED A REVIEW FOR THIS KENNEL */
                checkReviewStatus();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        kennelMap.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        kennelMap.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        kennelMap.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        kennelMap.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        kennelMap.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Bundle bundle = new Bundle(outState);
        kennelMap.onSaveInstanceState(bundle);
        outState.putBundle("kennel_map_save_state", bundle);
        super.onSaveInstanceState(outState);
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

    /** CHECK IF THE USER HAS POSTED A REVIEW FOR THIS KENNEL **/
    private void checkReviewStatus() {
        Call<KennelReview> call = apiReview.checkUserKennelReview(USER_ID, KENNEL_ID);
        call.enqueue(new Callback<KennelReview>() {
            @Override
            public void onResponse(Call<KennelReview> call, Response<KennelReview> response) {
//                Log.e("RESPONSE RAW", String.valueOf(response.raw()));
                if (!response.body().getError()) {
                    KennelReview review = response.body();
                    if (review != null) {
                        String reviewID = review.getKennelReviewID();
                        Intent intent = new Intent(KennelDetails.this, KennelReviewModifier.class);
                        intent.putExtra("REVIEW_ID", reviewID);
                        startActivityForResult(intent, 102);
                    }
                } else {
                    String profileStatus = getApp().getProfileStatus();
                    if (profileStatus.equalsIgnoreCase("Incomplete"))   {
                        String message = "You need to complete your Profile before you can provide feedback. To complete your profile Details, click on the \"Complete Profile\" button.";
                        new MaterialDialog.Builder(KennelDetails.this)
                                .icon(ContextCompat.getDrawable(KennelDetails.this, R.drawable.ic_info_outline_black_24dp))
                                .title("Profile Incomplete")
                                .cancelable(true)
                                .content(message)
                                .positiveText("Complete Profile")
                                .theme(Theme.LIGHT)
                                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        Intent intent = new Intent(KennelDetails.this, ProfileEditor.class);
                                        startActivity(intent);
                                    }
                                }).show();
                    } else {
                        Intent intentNewFeedback = new Intent(getApplicationContext(), KennelReviewCreator.class);
                        intentNewFeedback.putExtra("KENNEL_ID", KENNEL_ID);
                        intentNewFeedback.putExtra("KENNEL_OWNER_ID", KENNEL_OWNER_ID);
                        startActivityForResult(intentNewFeedback, 101);
                    }
                }
            }

            @Override
            public void onFailure(Call<KennelReview> call, Throwable t) {
//                Log.e("CHECK FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** CALL THE PHONE NUMBER *****/
    private void callPhone() {
        if (ContextCompat.checkSelfPermission(KennelDetails.this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED)   {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE))    {
                /* SHOW THE DIALOG */
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setIcon(R.drawable.ic_info_outline_black_24dp);
                builder.setTitle("Permission Required");
                builder.setMessage("\nZen Pets requires the permission to call the Kennel's phone number. \n\nFor a seamless experience, we recommend granting Zen Pets this permission.");
                builder.setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(
                                KennelDetails.this,
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
            String myData= "tel:" + KENNEL_PHONE_NUMBER_1;
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(myData));
            startActivity(intent);
        }
    }

    /***** DIAL THE PHONE NUMBER *****/
    private void dialPhone() {
        String myData= "tel:" + KENNEL_PHONE_NUMBER_1;
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(myData));
        startActivity(intent);
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        LinearLayoutManager reviews = new LinearLayoutManager(this);
        reviews.setOrientation(LinearLayoutManager.VERTICAL);
        reviews.isAutoMeasureEnabled();
        listReviews.setLayoutManager(reviews);
        listReviews.setHasFixedSize(true);
        listReviews.setNestedScrollingEnabled(false);
        listReviews.setAdapter(new KennelReviewsAdapter(KennelDetails.this, arrReviewsSubset));

        LinearLayoutManager llmClinicImages = new LinearLayoutManager(this);
        llmClinicImages.setOrientation(LinearLayoutManager.HORIZONTAL);
        llmClinicImages.isAutoMeasureEnabled();
        listKennelImages.setLayoutManager(llmClinicImages);
        listKennelImages.setHasFixedSize(true);
        listKennelImages.setNestedScrollingEnabled(false);
        listKennelImages.setAdapter(new KennelImagesAdapter(KennelDetails.this, arrImages));
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
                fetchKennelReviews();
            }
        }
    }
}