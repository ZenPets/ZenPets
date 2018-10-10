package co.zenpets.groomers.details.groomer;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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

import co.zenpets.groomers.R;
import co.zenpets.groomers.images.GroomerImageManager;
import co.zenpets.groomers.utils.AppPrefs;
import co.zenpets.groomers.utils.adapters.images.ImagesAdapter;
import co.zenpets.groomers.utils.adapters.reviews.ReviewsAdapter;
import co.zenpets.groomers.utils.helpers.ZenApiClient;
import co.zenpets.groomers.utils.models.groomers.Groomer;
import co.zenpets.groomers.utils.models.groomers.GroomersAPI;
import co.zenpets.groomers.utils.models.images.GroomerImage;
import co.zenpets.groomers.utils.models.images.GroomerImages;
import co.zenpets.groomers.utils.models.images.GroomerImagesAPI;
import co.zenpets.groomers.utils.models.reviews.Review;
import co.zenpets.groomers.utils.models.reviews.Reviews;
import co.zenpets.groomers.utils.models.reviews.ReviewsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroomerDetails extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN GROOMER ACCOUNT ID **/
    private String GROOMER_ID = null;

    /** THE GROOMER DETAILS **/
    String GROOMER_NAME = null;
    String GROOMER_LOGO = null;
    String CONTACT_NAME = null;
    private Double GROOMER_LATITUDE = null;
    private Double GROOMER_LONGITUDE = null;

    /** THE REVIEWS ARRAY LIST **/
    private ArrayList<Review> arrReviewsSubset = new ArrayList<>();

    /** THE IMAGES ARRAY LIST **/
    private ArrayList<GroomerImage> arrImages = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.appBar) AppBarLayout appBar;
    @BindView(R.id.toolbarLayout) CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.imgvwGroomerLogo) ImageView imgvwGroomerLogo;
    @BindView(R.id.txtGroomerName) TextView txtGroomerName;
    @BindView(R.id.txtContactName) TextView txtContactName;
    @BindView(R.id.txtVotes) TextView txtVotes;
    @BindView(R.id.groomerRating) RatingBar groomerRating;
    @BindView(R.id.linlaPhoneNumber1) LinearLayout linlaPhoneNumber1;
    @BindView(R.id.txtPhoneNumber1) TextView txtPhoneNumber1;
    @BindView(R.id.linlaPhoneNumber2) LinearLayout linlaPhoneNumber2;
    @BindView(R.id.txtPhoneNumber2) TextView txtPhoneNumber2;
    @BindView(R.id.txtGroomerAddress) TextView txtGroomerAddress;
    @BindView(R.id.groomerMap) MapView groomerMap;
    @BindView(R.id.linlaReviewsProgress) LinearLayout linlaReviewsProgress;
    @BindView(R.id.listReviews) RecyclerView listReviews;
    @BindView(R.id.txtAllReviews) TextView txtAllReviews;
    @BindView(R.id.linlaNoReviews) LinearLayout linlaNoReviews;
    @BindView(R.id.linlaImagesProgress) LinearLayout linlaImagesProgress;
    @BindView(R.id.listImages) RecyclerView listImages;
    @BindView(R.id.linlaNoImages) LinearLayout linlaNoImages;

    /** SHOW THE IMAGES MANAGER **/
    @OnClick(R.id.txtManageImages) void showImagesManager() {
        Intent intent = new Intent(GroomerDetails.this, GroomerImageManager.class);
        intent.putExtra("GROOMER_ID", GROOMER_ID);
        intent.putExtra("GROOMER_NAME", txtGroomerName.getText().toString().trim());
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groomer_details);
        ButterKnife.bind(this);
        ButterKnife.bind(this);
        groomerMap.onCreate(savedInstanceState != null ? savedInstanceState.getBundle("groomer_map_save_state") : null);
        groomerMap.onResume();
        groomerMap.setClickable(false);

        /* GET THE GROOMER ACCOUNT ID */
        GROOMER_ID = getApp().getGroomerID();

        /* FETCH THE GROOMER DETAILS */
        fetchGroomerDetails();

        /* SHOW THE PROGRESS AND FETCH THE FIRST 3 REVIEWS FOR THE GROOMER */
        linlaReviewsProgress.setVisibility(View.VISIBLE);
        listReviews.setVisibility(View.GONE);
        txtAllReviews.setVisibility(View.GONE);
        fetchGroomerReviews();

        /* SHOW THE IMAGES PROGRESS AND FETCH THE LIST OF IMAGES */
        linlaImagesProgress.setVisibility(View.VISIBLE);
        fetchGroomerImages();

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

    /** FETCH THE LIST OF IMAGES **/
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
                        listImages.setAdapter(new ImagesAdapter(GroomerDetails.this, arrImages));

                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY IMAGES VIEW */
                        linlaNoImages.setVisibility(View.GONE);
                        listImages.setVisibility(View.VISIBLE);
                    } else {
                        /* SHOW THE NO IMAGES LAYOUT */
                        linlaNoImages.setVisibility(View.VISIBLE);
                        listImages.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE NO IMAGES LAYOUT */
                    linlaNoImages.setVisibility(View.VISIBLE);
                    listImages.setVisibility(View.GONE);
                }

                /* HIDE THE IMAGES PROGRESS AFTER FETCHING THE DATA */
                linlaImagesProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<GroomerImages> call, Throwable t) {
            }
        });
    }

    /** FETCH THE FIRST 3 REVIEWS FOR THE GROOMER **/
    private void fetchGroomerReviews() {
        ReviewsAPI api = ZenApiClient.getClient().create(ReviewsAPI.class);
        Call<Reviews> call = api.fetchGroomerReviewsSubset(GROOMER_ID);
        call.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
//                Log.e("REVIEWS RAW", String.valueOf(response.raw()));
                if (response.body() != null && response.body().getReviews() != null)    {
                    arrReviewsSubset = response.body().getReviews();
                    if (arrReviewsSubset.size() > 0)    {
                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY REVIEWS VIEW */
                        linlaNoReviews.setVisibility(View.GONE);
                        listReviews.setVisibility(View.VISIBLE);
                        txtAllReviews.setVisibility(View.VISIBLE);

                        /* SET THE REVIEWS ADAPTER TO THE RECYCLER VIEW */
                        listReviews.setAdapter(new ReviewsAdapter(GroomerDetails.this, arrReviewsSubset));
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
            public void onFailure(Call<Reviews> call, Throwable t) {
//                Crashlytics.logException(t);
            }
        });
    }

    /** FETCH THE GROOMER DETAILS **/
    private void fetchGroomerDetails() {
        GroomersAPI api = ZenApiClient.getClient().create(GroomersAPI.class);
        Call<Groomer> call = api.fetchGroomerDetails(GROOMER_ID);
        call.enqueue(new Callback<Groomer>() {
            @Override
            public void onResponse(Call<Groomer> call, Response<Groomer> response) {
//                Log.e("PROFILE RESPONSE", String.valueOf(response.raw()));

                Groomer groomer = response.body();
                if (groomer != null)    {

                    /* GET AND SET THE GROOMER'S NAME */
                    GROOMER_NAME = groomer.getGroomerName();
                    if (GROOMER_NAME != null)    {
                        txtGroomerName.setText(GROOMER_NAME);
                        toolbarLayout.setTitleEnabled(false);
                        toolbarLayout.setTitle(GROOMER_NAME);
                    }

                    /* GET THE DOCTOR'S DISPLAY PROFILE */
                    GROOMER_LOGO = groomer.getGroomerLogo();
                    if (GROOMER_LOGO != null) {
                        Picasso.with(GroomerDetails.this)
                                .load(GROOMER_LOGO)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .fit()
                                .into(imgvwGroomerLogo, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(GroomerDetails.this)
                                                .load(GROOMER_LOGO)
                                                .into(imgvwGroomerLogo);
                                    }
                                });
                    }

                    /* GET AND SET THE CONTACT NAME */
                    CONTACT_NAME = groomer.getContactName();
                    txtContactName.setText(CONTACT_NAME);

                    /* GET AND SET THE PHONE NUMBER (#1) */
                    txtPhoneNumber1.setText(groomer.getGroomerPhoneNumber1());

                    /* GET AND SET THE PHONE NUMBER (#2) */
                    String groomerPhoneNumber2 = groomer.getGroomerPhoneNumber2();
                    if (groomerPhoneNumber2 != null
                            && !groomerPhoneNumber2.equalsIgnoreCase("")
                            && !groomerPhoneNumber2.equalsIgnoreCase("null"))    {
                        txtPhoneNumber2.setText(groomerPhoneNumber2);
                        linlaPhoneNumber2.setVisibility(View.VISIBLE);
                    } else {
                        linlaPhoneNumber2.setVisibility(View.GONE);
                    }

                    /* SET THE GROOMER'S ADDRESS */
                    String groomerAddress = groomer.getGroomerAddress();
                    String groomerPincode = groomer.getGroomerPincode();
                    String cityName = groomer.getCityName();
                    txtGroomerAddress.setText(getString(R.string.groomer_details_address_placeholder, groomerAddress, cityName, groomerPincode));

                    /* GET THE REVIEW COUNT */
                    String countReviews = groomer.getGroomerReviews();
                    txtAllReviews.setText(getString(R.string.groomer_details_all_reviews_placeholder, String.valueOf(countReviews)));

                    /* GET THE TOTAL REVIEWS, POSITIVE, AND FINALLY, CALCULATE THE PERCENTAGES */
                    String kennelReviews = groomer.getGroomerReviews();
                    String kennelPositives = groomer.getGroomerPositives();

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
                    String open = getString(R.string.groomer_details_votes_open);
                    String close = getString(R.string.groomer_details_votes_close);
                    txtVotes.setText(getString(R.string.groomer_details_votes_placeholder, strLikesPercentage, open, strVotes, close));

                    /* SET THE AVERAGE KENNEL RATING */
                    if (groomer.getGroomerRating() != null && !groomer.getGroomerRating().equalsIgnoreCase("null")) {
                        Float dblRating = Float.valueOf(groomer.getGroomerRating());
                        groomerRating.setRating(dblRating);
                    } else {
                        groomerRating.setRating(0);
                    }

                    /* GET AND SET THE GROOMER'S LATITUDE AND LONGITUDE ON THE MAP */
                    GROOMER_LATITUDE = Double.valueOf(groomer.getGroomerLatitude());
                    GROOMER_LONGITUDE = Double.valueOf(groomer.getGroomerLongitude());
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
                            }
                        });
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Groomer> call, Throwable t) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return false;
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
}