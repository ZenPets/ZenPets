package co.zenpets.kennels.details.kennel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
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

import co.zenpets.kennels.R;
import co.zenpets.kennels.modifier.images.KennelImageManager;
import co.zenpets.kennels.modifier.kennel.KennelCoverUpdater;
import co.zenpets.kennels.utils.adapters.images.KennelImagesAdapter;
import co.zenpets.kennels.utils.adapters.reviews.ReviewsAdapter;
import co.zenpets.kennels.utils.models.helpers.ZenApiClient;
import co.zenpets.kennels.utils.models.images.KennelImage;
import co.zenpets.kennels.utils.models.images.KennelImages;
import co.zenpets.kennels.utils.models.images.KennelImagesAPI;
import co.zenpets.kennels.utils.models.kennels.Kennel;
import co.zenpets.kennels.utils.models.kennels.KennelsAPI;
import co.zenpets.kennels.utils.models.reviews.Review;
import co.zenpets.kennels.utils.models.reviews.ReviewCount;
import co.zenpets.kennels.utils.models.reviews.Reviews;
import co.zenpets.kennels.utils.models.reviews.ReviewsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KennelDetails extends AppCompatActivity {

    /** THE INCOMING KENNEL ID **/
    String KENNEL_ID = null;

    /** THE REVIEWS SUBSET ARRAY LIST INSTANCE **/
    private ArrayList<Review> arrReviewsSubset = new ArrayList<>();

    /** THE KENNEL IMAGES ARRAY LIST INSTANCE **/
    private ArrayList<KennelImage> arrImages = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.appBar) AppBarLayout appBar;
    @BindView(R.id.toolbarLayout) CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.imgvwKennelCoverPhoto) SimpleDraweeView imgvwKennelCoverPhoto;
    @BindView(R.id.imgvwKennelOwnerDisplayProfile) SimpleDraweeView imgvwKennelOwnerDisplayProfile;
    @BindView(R.id.txtKennelName) TextView txtKennelName;
    @BindView(R.id.txtKennelOwnerName) TextView txtKennelOwnerName;
    @BindView(R.id.txtVotes) TextView txtVotes;
    @BindView(R.id.kennelRating) RatingBar kennelRating;
    @BindView(R.id.txtPhoneNumber1) TextView txtPhoneNumber1;
    @BindView(R.id.linlaPhoneNumber2) LinearLayout linlaPhoneNumber2;
    @BindView(R.id.txtPhoneNumber2) TextView txtPhoneNumber2;
    @BindView(R.id.txtKennelAddress) TextView txtKennelAddress;
    @BindView(R.id.kennelMap) MapView kennelMap;
    @BindView(R.id.linlaReviewsProgress) LinearLayout linlaReviewsProgress;
    @BindView(R.id.listReviews) RecyclerView listReviews;
    @BindView(R.id.txtAllReviews) TextView txtAllReviews;
    @BindView(R.id.linlaNoReviews) LinearLayout linlaNoReviews;
    @BindView(R.id.linlaImagesProgress) LinearLayout linlaImagesProgress;
    @BindView(R.id.listKennelImages) RecyclerView listKennelImages;
    @BindView(R.id.linlaNoImages) LinearLayout linlaNoImages;

    /** EDIT THE KENNEL COVER PHOTO **/
    @OnClick(R.id.linlaCoverEdit) void editCoverPhoto()    {
        final BottomSheetDialog sheetDialog = new BottomSheetDialog(KennelDetails.this);
        View view = getLayoutInflater().inflate(R.layout.cover_updater_sheet, null);
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
                Intent intent = new Intent(KennelDetails.this, KennelCoverUpdater.class);
                intent.putExtra("KENNEL_ID", KENNEL_ID);
                intent.putExtra("KENNEL_NAME", txtKennelName.getText().toString().trim());
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

    /** SHOW ALL REVIEWS **/
    @OnClick(R.id.txtAllReviews) void showAllReviews()  {
    }

    /** MANAGE KENNEL IMAGES **/
    @OnClick(R.id.txtManageImages) void manageImages()  {
        Intent intent = new Intent(KennelDetails.this, KennelImageManager.class);
        intent.putExtra("KENNEL_ID", KENNEL_ID);
        intent.putExtra("KENNEL_NAME", txtKennelName.getText().toString().trim());
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kennel_details);
        ButterKnife.bind(this);
        kennelMap.onCreate(savedInstanceState);
        kennelMap.onResume();
        kennelMap.setClickable(false);

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

//        /* GET THE INCOMING DATA */
//        getIncomingData();

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

    /***** FETCH THE KENNEL DETAILS *****/
    private void fetchKennelDetails() {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<Kennel> call = api.fetchKennelDetails(KENNEL_ID);
        call.enqueue(new Callback<Kennel>() {
            @Override
            public void onResponse(Call<Kennel> call, Response<Kennel> response) {
                Kennel kennel = response.body();
                if (kennel != null) {
                    /* GET AND SET THE KENNEL NAME */
                    if (kennel.getKennelName() != null) {
                        txtKennelName.setText(kennel.getKennelName());
                        toolbarLayout.setTitleEnabled(false);
                        toolbarLayout.setTitle(kennel.getKennelName());
                    }

                    /* GET AND SET THE KENNEL OWNER'S NAME */
                    txtKennelOwnerName.setText(kennel.getKennelOwnerName());

                    /* GET AND SET THE KENNEL COVER PHOTO */
                    String strKennelCoverPhoto = kennel.getKennelCoverPhoto();
                    if (strKennelCoverPhoto != null
                            && !strKennelCoverPhoto.equalsIgnoreCase("")
                            && !strKennelCoverPhoto.equalsIgnoreCase("null")) {
                        Uri uri = Uri.parse(strKennelCoverPhoto);
                        imgvwKennelCoverPhoto.setImageURI(uri);
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.empty_graphic)
                                .build();
                        imgvwKennelCoverPhoto.setImageURI(request.getSourceUri());
                    }

                    /* GET AND SET THE KENNEL OWNER'S DISPLAY PROFILE*/
                    String strKennelOwnerDisplayProfile = kennel.getKennelCoverPhoto();
                    if (strKennelOwnerDisplayProfile != null
                            && !strKennelOwnerDisplayProfile.equalsIgnoreCase("")
                            && !strKennelOwnerDisplayProfile.equalsIgnoreCase("null")) {
                        Uri uri = Uri.parse(strKennelOwnerDisplayProfile);
                        imgvwKennelOwnerDisplayProfile.setImageURI(uri);
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.empty_graphic)
                                .build();
                        imgvwKennelCoverPhoto.setImageURI(request.getSourceUri());
                    }

                    /* GET AND SET THE KENNEL PHONE NUMBER (#1) */
                    txtPhoneNumber1.setText(kennel.getKennelPhoneNumber1());

                    /* GET AND SET THE KENNEL PHONE NUMBER (#2) */
                    String strKennelPhoneNumber2 = kennel.getKennelPhoneNumber2();
                    if (strKennelPhoneNumber2 != null
                            && !strKennelPhoneNumber2.equalsIgnoreCase("")
                            && !strKennelPhoneNumber2.equalsIgnoreCase("null"))    {
                        txtPhoneNumber2.setText(strKennelPhoneNumber2);
                        linlaPhoneNumber2.setVisibility(View.VISIBLE);
                    } else {
                        linlaPhoneNumber2.setVisibility(View.GONE);
                    }

                    /* SET THE KENNEL ADDRESS */
                    String kennelAddress = kennel.getKennelAddress();
                    String kennelPinCode = kennel.getKennelPinCode();
                    String cityName = kennel.getCityName();
                    txtKennelAddress.setText(getString(R.string.kennel_details_address_placeholder, kennelAddress, cityName, kennelPinCode));

                    /* SET THE CLINIC LOCATION ON THE MAP */
                    Double kennelLatitude = Double.valueOf(kennel.getKennelLatitude());
                    Double kennelLongitude = Double.valueOf(kennel.getKennelLongitude());
                    final LatLng latLng = new LatLng(kennelLatitude, kennelLongitude);
                    kennelMap.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(KennelDetails.this, R.raw.zen_map_style);
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
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Kennel> call, Throwable t) {
//                Log.e("DETAILS FAILURE", t.getMessage());
            }
        });
    }

    /** FETCH THE KENNEL'S REVIEW COUNT **/
    private void fetchReviewCount() {
        ReviewsAPI api = ZenApiClient.getClient().create(ReviewsAPI.class);
        Call<ReviewCount> call = api.fetchKennelReviewCount(KENNEL_ID);
        call.enqueue(new Callback<ReviewCount>() {
            @Override
            public void onResponse(Call<ReviewCount> call, Response<ReviewCount> response) {
                ReviewCount count = response.body();
                if (count != null)  {
                    int countReviews = Integer.parseInt(count.getTotalReviews());
                    if (countReviews > 0)   {
                        txtAllReviews.setText(getString(R.string.kennel_details_all_reviews_placeholder, String.valueOf(countReviews)));
                        txtAllReviews.setVisibility(View.VISIBLE);
                    } else {
                        txtAllReviews.setVisibility(View.GONE);
                    }
                } else {
                    txtAllReviews.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ReviewCount> call, Throwable t) {
//                Log.e("COUNT FAILURE", t.getMessage());
            }
        });
    }

    /** FETCH THE KENNEL REVIEWS **/
    private void fetchKennelReviews() {
        ReviewsAPI api = ZenApiClient.getClient().create(ReviewsAPI.class);
        Call<Reviews> call = api.fetchKennelReviewsSubset(KENNEL_ID);
        call.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
//                Log.e("RAW REVIEWS", String.valueOf(response.raw()));
                if (response.body() != null && response.body().getReviews() != null)    {
                    arrReviewsSubset = response.body().getReviews();
                    if (arrReviewsSubset.size() > 0)    {
                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY REVIEWS VIEW */
                        linlaNoReviews.setVisibility(View.GONE);
                        listReviews.setVisibility(View.VISIBLE);

                        /* SET THE REVIEWS ADAPTER TO THE RECYCLER VIEW */
                        listReviews.setAdapter(new ReviewsAdapter(KennelDetails.this, arrReviewsSubset));
                    } else {
                        /* SHOW THE NO REVIEWS LAYOUT */
                        linlaNoReviews.setVisibility(View.VISIBLE);
                        listReviews.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE NO REVIEWS LAYOUT */
                    linlaNoReviews.setVisibility(View.VISIBLE);
                    listReviews.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE REVIEWS */
                linlaReviewsProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {
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

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        LinearLayoutManager reviews = new LinearLayoutManager(this);
        reviews.setOrientation(LinearLayoutManager.VERTICAL);
        reviews.isAutoMeasureEnabled();
        listReviews.setLayoutManager(reviews);
        listReviews.setHasFixedSize(true);
        listReviews.setNestedScrollingEnabled(false);
        listReviews.setAdapter(new ReviewsAdapter(KennelDetails.this, arrReviewsSubset));

        LinearLayoutManager llmClinicImages = new LinearLayoutManager(this);
        llmClinicImages.setOrientation(LinearLayoutManager.HORIZONTAL);
        llmClinicImages.isAutoMeasureEnabled();
        listKennelImages.setLayoutManager(llmClinicImages);
        listKennelImages.setHasFixedSize(true);
        listKennelImages.setNestedScrollingEnabled(false);
        listKennelImages.setAdapter(new KennelImagesAdapter(KennelDetails.this, arrImages));
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("KENNEL_ID"))    {
            KENNEL_ID = bundle.getString("KENNEL_ID");
            if (KENNEL_ID != null)  {
                /* FETCH THE KENNEL DETAILS */
                fetchKennelDetails();

                /* SHOW THE REVIEWS PROGRESS AND FETCH THE KENNEL'S REVIEW COUNT AND THE KENNEL'S REVIEWS */
                linlaReviewsProgress.setVisibility(View.VISIBLE);
                fetchReviewCount();
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
        kennelMap.onResume();

        /* GET THE INCOMING DATA */
        getIncomingData();
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
}