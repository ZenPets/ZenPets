package biz.zenpets.users.details.trainers;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.adapters.trainers.TrainerReviewsAdapter;
import biz.zenpets.users.utils.adapters.trainers.TrainingModulesAdapter;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.trainers.Trainer;
import biz.zenpets.users.utils.models.trainers.TrainersAPI;
import biz.zenpets.users.utils.models.trainers.modules.Module;
import biz.zenpets.users.utils.models.trainers.modules.Modules;
import biz.zenpets.users.utils.models.trainers.modules.ModulesAPI;
import biz.zenpets.users.utils.models.trainers.modules.ModulesMinMax;
import biz.zenpets.users.utils.models.trainers.reviews.TrainerReview;
import biz.zenpets.users.utils.models.trainers.reviews.TrainerReviews;
import biz.zenpets.users.utils.models.trainers.reviews.TrainerReviewsAPI;
import biz.zenpets.users.utils.models.trainers.reviews.TrainerReviewsCount;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainerDetails extends AppCompatActivity {

    /** THE INCOMING TRAINER ID **/
    String TRAINER_ID = null;

    /** THE TRAINER REVIEWS ARRAY LIST **/
    private ArrayList<TrainerReview> arrReviewsSubset = new ArrayList<>();

    /** THE TRAINER'S TRAINING MODULES ARRAY LIST **/
    ArrayList<Module> arrModules = new ArrayList<>();

    /** THE TOTAL VOTES, TOTAL LIKES AND TOTAL DISLIKES **/
    int TOTAL_VOTES = 0;
    int TOTAL_LIKES = 0;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.appBar) AppBarLayout appBar;
    @BindView(R.id.toolbarLayout) CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.imgvwTrainerDisplayProfile) SimpleDraweeView imgvwTrainerDisplayProfile;
    @BindView(R.id.txtTrainerName) AppCompatTextView txtTrainerName;
    @BindView(R.id.txtVotes) AppCompatTextView txtVotes;
    @BindView(R.id.txtTrainerCharges) AppCompatTextView txtTrainerCharges;
    @BindView(R.id.txtTrainerAddress) AppCompatTextView txtTrainerAddress;
    @BindView(R.id.trainerMap) MapView trainerMap;
    @BindView(R.id.linlaTrainingModules) LinearLayout linlaTrainingModules;
    @BindView(R.id.linlaTrainingModulesProgress) LinearLayout linlaTrainingModulesProgress;
    @BindView(R.id.listTrainingModules) RecyclerView listTrainingModules;
    @BindView(R.id.txtAllTrainingModules) AppCompatTextView txtAllTrainingModules;
    @BindView(R.id.linlaNoTrainingModules) LinearLayout linlaNoTrainingModules;
    @BindView(R.id.linlaReviews) LinearLayout linlaReviews;
    @BindView(R.id.linlaReviewsProgress) LinearLayout linlaReviewsProgress;
    @BindView(R.id.listReviews) RecyclerView listReviews;
    @BindView(R.id.txtAllReviews) AppCompatTextView txtAllReviews;
    @BindView(R.id.linlaNoReviews) LinearLayout linlaNoReviews;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainer_details);
        ButterKnife.bind(this);
        trainerMap.onCreate(savedInstanceState != null ? savedInstanceState.getBundle("trainer_map_save_state") : null);
        trainerMap.onResume();
        trainerMap.setClickable(false);

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* FETCH THE TRAINER'S FEEDBACK */
        fetchTrainerFeedback();

        /* FETCH THE RANGE OF FEES */
        fetchFeeRange();

        /* SHOW THE PROGRESS AND FETCH THE FIRST 3 TRAINING MODULES */
        linlaTrainingModulesProgress.setVisibility(View.VISIBLE);
        listTrainingModules.setVisibility(View.GONE);
        fetchTrainingModules();

        /* SHOW THE PROGRESS AND FETCH THE FIRST 3 REVIEWS FOR THE DOCTOR */
        linlaReviewsProgress.setVisibility(View.VISIBLE);
        listReviews.setVisibility(View.GONE);
        fetchReviewCount();
        fetchDoctorReviews();

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

    /***** FETCH THE TRAINER'S DETAILS *****/
    private void fetchTrainerDetails()  {
        TrainersAPI api = ZenApiClient.getClient().create(TrainersAPI.class);
        Call<Trainer> call = api.fetchTrainerDetails(TRAINER_ID);
        call.enqueue(new Callback<Trainer>() {
            @Override
            public void onResponse(Call<Trainer> call, Response<Trainer> response) {
                Trainer data = response.body();
                if (data != null)   {
                    /* GET AND SET THE TRAINER'S NAME */
                    String TRAINER_NAME = data.getTrainerName();
                    if (TRAINER_NAME != null)   {
                        txtTrainerName.setText(TRAINER_NAME);
                        toolbarLayout.setTitleEnabled(false);
                        toolbarLayout.setTitle(TRAINER_NAME);
                    }

                    /* GET AND SET THE TRAINER'S DISPLAY PROFILE */
                    String TRAINER_DISPLAY_PROFILE = data.getTrainerDisplayProfile();
                    if (TRAINER_DISPLAY_PROFILE != null)    {
                        Uri uriClinic = Uri.parse(TRAINER_DISPLAY_PROFILE);
                        imgvwTrainerDisplayProfile.setImageURI(uriClinic);
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.ic_person)
                                .build();
                        imgvwTrainerDisplayProfile.setImageURI(request.getSourceUri());
                    }

                    /* GET AND SET THE TRAINER'S ADDRESS */
                    String TRAINER_ADDRESS = data.getTrainerAddress();
                    String CITY_NAME = data.getCityName();
                    String TRAINER_PINCODE = data.getTrainerPincode();
                    String STATE_NAME = data.getStateName();
                    txtTrainerAddress.setText(getString(R.string.trainer_list_address_placeholder, TRAINER_ADDRESS, CITY_NAME, TRAINER_PINCODE, STATE_NAME));

                    /* GET AND SET THE TRAINER'S LATITUDE AND LONGITUDE ON THE MAP */
                    Double TRAINER_LATITUDE = Double.valueOf(data.getTrainerLatitude());
                    Double TRAINER_LONGITUDE = Double.valueOf(data.getTrainerLongitude());
                    if (TRAINER_LATITUDE != null && TRAINER_LONGITUDE != null) {
                        final LatLng latLng = new LatLng(TRAINER_LATITUDE, TRAINER_LONGITUDE);
                        trainerMap.getMapAsync(new OnMapReadyCallback() {
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
//                                        Intent intent = new Intent(getApplicationContext(), MapDetails.class);
//                                        intent.putExtra("DOCTOR_ID", DOCTOR_ID);
//                                        intent.putExtra("DOCTOR_NAME", DOCTOR_PREFIX + " " + DOCTOR_NAME);
//                                        intent.putExtra("DOCTOR_PHONE_NUMBER", DOCTOR_PHONE_NUMBER);
//                                        intent.putExtra("CLINIC_ID", CLINIC_ID);
//                                        intent.putExtra("CLINIC_NAME", CLINIC_NAME);
//                                        intent.putExtra("CLINIC_LATITUDE", TRAINER_LATITUDE);
//                                        intent.putExtra("CLINIC_LONGITUDE", TRAINER_LONGITUDE);
//                                        intent.putExtra("CLINIC_ADDRESS", CLINIC_ADDRESS);
//                                        startActivity(intent);
                                    }
                                });
                            }
                        });
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Trainer> call, Throwable t) {
                Log.e("DETAILS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    private void fetchTrainerFeedback() {
        TrainerReviewsAPI apiYes = ZenApiClient.getClient().create(TrainerReviewsAPI.class);
        Call<TrainerReviews> callYes = apiYes.fetchPositiveTrainerReviews(TRAINER_ID, "Yes");
        callYes.enqueue(new Callback<TrainerReviews>() {
            @Override
            public void onResponse(Call<TrainerReviews> call, Response<TrainerReviews> response) {
                if (response.body() != null && response.body().getReviews() != null)    {
                    ArrayList<TrainerReview> arrReview = response.body().getReviews();
                    TOTAL_LIKES = arrReview.size();
                    TOTAL_VOTES = TOTAL_VOTES + arrReview.size();

                    /* GET THE TOTAL NUMBER OF NEGATIVE REVIEWS */
                    TrainerReviewsAPI apiNo = ZenApiClient.getClient().create(TrainerReviewsAPI.class);
                    Call<TrainerReviews> callNo = apiNo.fetchNegativeTrainerReviews(TRAINER_ID, "No");
                    callNo.enqueue(new Callback<TrainerReviews>() {
                        @Override
                        public void onResponse(Call<TrainerReviews> call, Response<TrainerReviews> response) {
                            if (response.body() != null && response.body().getReviews() != null)    {
                                ArrayList<TrainerReview> arrReview = response.body().getReviews();
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
                        public void onFailure(Call<TrainerReviews> call, Throwable t) {
                            Crashlytics.logException(t);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<TrainerReviews> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE RANGE OF FEES *****/
    private void fetchFeeRange() {
        ModulesAPI api = ZenApiClient.getClient().create(ModulesAPI.class);
        Call<ModulesMinMax> minMaxCall = api.fetchTrainingModulesMinMax(TRAINER_ID);
        minMaxCall.enqueue(new Callback<ModulesMinMax>() {
            @Override
            public void onResponse(Call<ModulesMinMax> call, Response<ModulesMinMax> response) {
                ModulesMinMax data = response.body();
                if (data != null)   {
                    /* GET THE MINIMUM TRAINING FEE */
                    String strMinTrainingFee = data.getMinTrainingFee();

                    /* GET THE MAXIMUM TRAINING FEE */
                    String strMaxTrainingFee = data.getMaxTrainingFee();

                    /* SET THE FEE RANGE */
                    txtTrainerCharges.setText(getString(R.string.trainer_list_charges_placeholder, strMinTrainingFee, strMaxTrainingFee));
                } else {
                    txtTrainerCharges.setText(getString(R.string.trainer_list_charges_na_placeholder));
                }
            }

            @Override
            public void onFailure(Call<ModulesMinMax> call, Throwable t) {
                Log.e("RANGE FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE FIRST 3 TRAINING MODULES *****/
    private void fetchTrainingModules() {
        ModulesAPI api = ZenApiClient.getClient().create(ModulesAPI.class);
        Call<Modules> call = api.fetchTrainerModulesSubset(TRAINER_ID);
        call.enqueue(new Callback<Modules>() {
            @Override
            public void onResponse(Call<Modules> call, Response<Modules> response) {
                if (response.body() != null && response.body().getModules() != null)    {
                    arrModules = response.body().getModules();
                    if (arrModules.size() > 0)  {
                        /* SET THE ADAPTER TO THE RECYCLER VIEW */
                        listTrainingModules.setAdapter(new TrainingModulesAdapter(TrainerDetails.this, arrModules));

                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                        listTrainingModules.setVisibility(View.VISIBLE);
                        linlaNoTrainingModules.setVisibility(View.GONE);
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaNoTrainingModules.setVisibility(View.VISIBLE);
                        listTrainingModules.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaNoTrainingModules.setVisibility(View.VISIBLE);
                    listTrainingModules.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS */
                linlaTrainingModulesProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Modules> call, Throwable t) {
                Log.e("MODULES FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE DOCTOR'S REVIEWS *****/
    private void fetchDoctorReviews() {
        TrainerReviewsAPI api = ZenApiClient.getClient().create(TrainerReviewsAPI.class);
        Call<TrainerReviews> call = api.fetchTrainerReviewsSubset(TRAINER_ID);
        call.enqueue(new Callback<TrainerReviews>() {
            @Override
            public void onResponse(Call<TrainerReviews> call, Response<TrainerReviews> response) {
                if (response.body() != null && response.body().getReviews() != null)    {
                    arrReviewsSubset = response.body().getReviews();
                    if (arrReviewsSubset.size() > 0)    {
                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY REVIEWS VIEW */
                        linlaReviews.setVisibility(View.VISIBLE);
                        linlaNoReviews.setVisibility(View.GONE);
                        listReviews.setVisibility(View.VISIBLE);

                        /* SET THE REVIEWS ADAPTER TO THE RECYCLER VIEW */
                        listReviews.setAdapter(new TrainerReviewsAdapter(TrainerDetails.this, arrReviewsSubset));
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
            public void onFailure(Call<TrainerReviews> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE TRAINER'S REVIEWS COUNT *****/
    private void fetchReviewCount() {
        TrainerReviewsAPI api = ZenApiClient.getClient().create(TrainerReviewsAPI.class);
        Call<TrainerReviewsCount> call = api.fetchTrainerReviewCount(TRAINER_ID);
        call.enqueue(new Callback<TrainerReviewsCount>() {
            @Override
            public void onResponse(Call<TrainerReviewsCount> call, Response<TrainerReviewsCount> response) {
                TrainerReviewsCount count = response.body();
                if (count != null)  {
                    int countReviews = Integer.parseInt(count.getTotalReviews());
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
            public void onFailure(Call<TrainerReviewsCount> call, Throwable t) {
                Log.e("COUNT FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("TRAINER_ID")) {
            TRAINER_ID = bundle.getString("TRAINER_ID");
            if (TRAINER_ID != null) {
                /* FETCH THE TRAINER'S DETAILS */
                fetchTrainerDetails();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        trainerMap.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        trainerMap.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        trainerMap.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        trainerMap.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        trainerMap.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Bundle bundle = new Bundle(outState);
        trainerMap.onSaveInstanceState(bundle);
        outState.putBundle("trainer_map_save_state", bundle);
        super.onSaveInstanceState(outState);
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        LinearLayoutManager services = new LinearLayoutManager(this);
        services.setOrientation(LinearLayoutManager.VERTICAL);
        services.isAutoMeasureEnabled();
        listTrainingModules.setLayoutManager(services);
        listTrainingModules.setHasFixedSize(true);
        listTrainingModules.setNestedScrollingEnabled(false);
        listTrainingModules.setAdapter(new TrainingModulesAdapter(TrainerDetails.this, arrModules));

        LinearLayoutManager reviews = new LinearLayoutManager(this);
        reviews.setOrientation(LinearLayoutManager.VERTICAL);
        reviews.isAutoMeasureEnabled();
        listReviews.setLayoutManager(reviews);
        listReviews.setHasFixedSize(true);
        listReviews.setNestedScrollingEnabled(false);
        listReviews.setAdapter(new TrainerReviewsAdapter(TrainerDetails.this, arrReviewsSubset));
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
        MenuInflater inflater = new MenuInflater(TrainerDetails.this);
        inflater.inflate(R.menu.activity_trainer_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuFeedback:
            default:
                break;
        }
        return false;
    }
}