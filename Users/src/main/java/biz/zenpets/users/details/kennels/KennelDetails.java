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
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.creator.profile.ProfileEditor;
import biz.zenpets.users.details.kennels.reviews.KennelReviewCreator;
import biz.zenpets.users.details.kennels.reviews.KennelReviewsActivity;
import biz.zenpets.users.modifier.kennel.KennelReviewModifier;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.adapters.kennels.reviews.KennelReviewsAdapter;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.kennels.kennels.Kennel;
import biz.zenpets.users.utils.models.kennels.kennels.KennelsAPI;
import biz.zenpets.users.utils.models.kennels.reviews.KennelRating;
import biz.zenpets.users.utils.models.kennels.reviews.KennelReview;
import biz.zenpets.users.utils.models.kennels.reviews.KennelReviews;
import biz.zenpets.users.utils.models.kennels.reviews.KennelReviewsAPI;
import biz.zenpets.users.utils.models.kennels.reviews.KennelReviewsCount;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KennelDetails extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN USER'S ID **/
    String USER_ID = null;

    /** THE INCOMING KENNEL ID **/
    String KENNEL_ID = null;

    /** A KENNEL API AND KENNEL REVIEWS API INSTANCE **/
    KennelsAPI apiKennel = ZenApiClient.getClient().create(KennelsAPI.class);
    KennelReviewsAPI apiReview = ZenApiClient.getClient().create(KennelReviewsAPI.class);

    /** THE TOTAL VOTES, TOTAL LIKES AND TOTAL DISLIKES **/
    int TOTAL_VOTES = 0;
    int TOTAL_LIKES = 0;

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int CALL_PHONE_CONSTANT = 200;

    /** THE REVIEWS ADAPTER AND ARRAY LISTS **/
    private ArrayList<KennelReview> arrReviewsSubset = new ArrayList<>();

    /** DATA TYPES TO HOLD THE KENNEL DETAILS **/
    String KENNEL_COVER_PHOTO = null;
    String KENNEL_NAME = null;
    String KENNEL_OWNER_ID = null;
    String KENNEL_OWNER_DISPLAY_PROFILE = null;
    String KENNEL_OWNER_NAME = null;
    String KENNEL_ADDRESS = null;
    String CITY_NAME = null;
    String STATE_NAME = null;
    String KENNEL_PIN_CODE = null;
    Double KENNEL_LATITUDE = null;
    Double KENNEL_LONGITUDE = null;
    String KENNEL_PHONE_NUMBER_1 = null;
    String KENNEL_PHONE_NUMBER_2 = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.appBar) AppBarLayout appBar;
    @BindView(R.id.toolbarLayout) CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.imgvwKennelCoverPhoto) SimpleDraweeView imgvwKennelCoverPhoto;
    @BindView(R.id.txtKennelName) TextView txtKennelName;
    @BindView(R.id.imgvwKennelOwnerDisplayProfile) SimpleDraweeView imgvwKennelOwnerDisplayProfile;
    @BindView(R.id.txtKennelOwnerName) TextView txtKennelOwnerName;
    @BindView(R.id.txtVotes) TextView txtVotes;
    @BindView(R.id.kennelRating) AppCompatRatingBar kennelRating;
    @BindView(R.id.txtKennelCharges) TextView txtKennelCharges;
    @BindView(R.id.linlaPhoneNumber1) LinearLayout linlaPhoneNumber1;
    @BindView(R.id.txtPhoneNumber1) TextView txtPhoneNumber1;
    @BindView(R.id.linlaPhoneNumber2) LinearLayout linlaPhoneNumber2;
    @BindView(R.id.txtPhoneNumber2) TextView txtPhoneNumber2;
    @BindView(R.id.txtKennelAddress) TextView txtKennelAddress;
    @BindView(R.id.kennelMap) MapView kennelMap;
    @BindView(R.id.linlaReviewsProgress) LinearLayout linlaReviewsProgress;
    @BindView(R.id.listReviews) RecyclerView listReviews;
    @BindView(R.id.txtAllReviews) TextView txtAllReviews;
    @BindView(R.id.linlaNoReviews) LinearLayout linlaNoReviews;

    /** SHOW THE CHARGES INFO **/
    @OnClick(R.id.imgvwChargesInfo) void showChargesInfo()  {
    }

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
        Call<Kennel> call = apiKennel.fetchKennelDetails(KENNEL_ID);
        call.enqueue(new Callback<Kennel>() {
            @Override
            public void onResponse(Call<Kennel> call, Response<Kennel> response) {
                Log.e("RAW RESPONSE", String.valueOf(response.raw()));
                Kennel data = response.body();
                if (data != null)   {

                    /* GET AND SET THE KENNEL'S COVER PHOTO */
                    KENNEL_COVER_PHOTO = data.getKennelCoverPhoto();
                    if (KENNEL_COVER_PHOTO != null
                            && !KENNEL_COVER_PHOTO.equalsIgnoreCase("")
                            && !KENNEL_COVER_PHOTO.equalsIgnoreCase("null"))    {
                        Uri uriClinic = Uri.parse(KENNEL_COVER_PHOTO);
                        imgvwKennelCoverPhoto.setImageURI(uriClinic);
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.ic_business_black_24dp)
                                .build();
                        imgvwKennelCoverPhoto.setImageURI(request.getSourceUri());
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
                    if (KENNEL_OWNER_DISPLAY_PROFILE != null
                            && !KENNEL_OWNER_DISPLAY_PROFILE.equalsIgnoreCase("")
                            && !KENNEL_OWNER_DISPLAY_PROFILE.equalsIgnoreCase("null"))    {
                        Uri uriProfile = Uri.parse(KENNEL_OWNER_DISPLAY_PROFILE);
                        imgvwKennelOwnerDisplayProfile.setImageURI(uriProfile);
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.ic_person_black_24dp)
                                .build();
                        imgvwKennelOwnerDisplayProfile.setImageURI(request.getSourceUri());
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

                    /* SHOW THE PROGRESS AND FETCH THE FIRST 3 REVIEWS FOR THE DOCTOR */
                    linlaReviewsProgress.setVisibility(View.VISIBLE);
                    listReviews.setVisibility(View.GONE);
                    txtAllReviews.setVisibility(View.GONE);
                    fetchReviewCount();
                    fetchKennelReviews();

                    /* FETCH THE KENNEL'S RATING */
                    fetchKennelRatings();

                    /* FETCH THE KENNEL'S FEEDBACK */
                    fetchKennelFeedback();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Kennel> call, Throwable t) {
                Log.e("KENNEL FAILURES", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE DOCTOR'S REVIEWS COUNT *****/
    private void fetchReviewCount() {
        Call<KennelReviewsCount> call = apiReview.fetchKennelReviewCount(KENNEL_ID);
        call.enqueue(new Callback<KennelReviewsCount>() {
            @Override
            public void onResponse(Call<KennelReviewsCount> call, Response<KennelReviewsCount> response) {
                KennelReviewsCount count = response.body();
                if (count != null)  {
                    int countReviews = Integer.parseInt(count.getTotalReviews());
                    if (countReviews > 0)   {
                        txtAllReviews.setText(getString(R.string.doctor_details_all_reviews_placeholder, String.valueOf(countReviews)));
                        txtAllReviews.setVisibility(View.VISIBLE);
                    } else {
                        txtAllReviews.setText(getString(R.string.doctor_details_all_reviews));
                        txtAllReviews.setVisibility(View.GONE);
                    }
                } else {
                    txtAllReviews.setText(getString(R.string.doctor_details_all_reviews));
                    txtAllReviews.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<KennelReviewsCount> call, Throwable t) {
//                Log.e("COUNT FAILURE", t.getMessage());
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

    /** FETCH THE KENNEL'S RATING **/
    private void fetchKennelRatings() {
        Call<KennelRating> call = apiReview.fetchKennelRatings(KENNEL_ID);
        call.enqueue(new Callback<KennelRating>() {
            @Override
            public void onResponse(Call<KennelRating> call, Response<KennelRating> response) {
                KennelRating rating = response.body();
                if (rating != null) {
                    String strRating = rating.getAvgKennelRating();
                    if (strRating != null && !strRating.equalsIgnoreCase("null")) {
                        kennelRating.setRating(Float.parseFloat(strRating));
                    } else {
                        kennelRating.setRating(0);
                    }
                } else {
                    kennelRating.setRating(0);
                }
            }

            @Override
            public void onFailure(Call<KennelRating> call, Throwable t) {
                Log.e("RATINGS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** FETCH THE KENNEL'S FEEDBACK **/
    private void fetchKennelFeedback() {
        Call<KennelReviews> callYes = apiReview.fetchPositiveKennelReviews(KENNEL_ID, "Yes");
        callYes.enqueue(new Callback<KennelReviews>() {
            @Override
            public void onResponse(Call<KennelReviews> call, Response<KennelReviews> response) {
                if (response.body() != null && response.body().getReviews() != null)    {
                    ArrayList<KennelReview> arrReview = response.body().getReviews();
                    TOTAL_LIKES = arrReview.size();
                    TOTAL_VOTES = TOTAL_VOTES + arrReview.size();

                    /* GET THE TOTAL NUMBER OF NEGATIVE REVIEWS */
                    Call<KennelReviews> callNo = apiReview.fetchNegativeKennelReviews(KENNEL_ID, "No");
                    callNo.enqueue(new Callback<KennelReviews>() {
                        @Override
                        public void onResponse(Call<KennelReviews> call, Response<KennelReviews> response) {
                            if (response.body() != null && response.body().getReviews() != null)    {
                                ArrayList<KennelReview> arrReview = response.body().getReviews();
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
                            } else {
                                String strVotes = "0";
                                String strLikesPercentage = "0%";
                                String open = getString(R.string.doctor_list_votes_open);
                                String close = getString(R.string.doctor_list_votes_close);
                                txtVotes.setText(getString(R.string.doctor_list_votes_placeholder, strLikesPercentage, open, strVotes, close));
                            }
                        }

                        @Override
                        public void onFailure(Call<KennelReviews> call, Throwable t) {
                            Crashlytics.logException(t);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<KennelReviews> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /** GET THE INCOMING DATA **/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("KENNEL_ID"))  {
            KENNEL_ID = bundle.getString("KENNEL_ID");
            if (KENNEL_ID != null)  {
                /* FETCH THE KENNEL DETAILS */
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
                Log.e("RESPONSE RAW", String.valueOf(response.raw()));
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