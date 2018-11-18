package co.zenpets.users.details.doctors;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.zenpets.users.R;
import co.zenpets.users.creator.appointment.AppointmentClinicSelector;
import co.zenpets.users.creator.profile.ProfileEditor;
import co.zenpets.users.creator.review.ReviewClinicSelector;
import co.zenpets.users.modifier.review.ReviewModifier;
import co.zenpets.users.utils.AppPrefs;
import co.zenpets.users.utils.adapters.doctors.clinics.DoctorClinicsAdapter;
import co.zenpets.users.utils.adapters.reviews.ReviewsAdapter;
import co.zenpets.users.utils.helpers.classes.ZenAPIScalar;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.doctors.DoctorsAPI;
import co.zenpets.users.utils.models.doctors.clinics.Clinic;
import co.zenpets.users.utils.models.doctors.clinics.Clinics;
import co.zenpets.users.utils.models.doctors.clinics.ClinicsAPI;
import co.zenpets.users.utils.models.doctors.modules.QualificationsAPI;
import co.zenpets.users.utils.models.doctors.profile.DoctorProfile;
import co.zenpets.users.utils.models.doctors.subscription.SubscriptionData;
import co.zenpets.users.utils.models.doctors.subscription.SubscriptionsAPI;
import co.zenpets.users.utils.models.reviews.Review;
import co.zenpets.users.utils.models.reviews.Reviews;
import co.zenpets.users.utils.models.reviews.ReviewsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorProfileActivity extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN USER'S ID **/
    private String USER_ID = null;

    /** THE INCOMING DOCTOR ID **/
    private String DOCTOR_ID = null;

    /* A STRING BUILDER INSTANCE FOR THE DOCTOR'S EDUCATION */
    private StringBuilder sb;

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int CALL_PHONE_CONSTANT = 200;

    /** THE DOCTOR'S PHONE NUMBER **/
    private String DOCTOR_PHONE_NUMBER = null;

    /** THE DOCTOR'S SUBSCRIPTION STATUS FLAG **/
    private boolean blnSubscriptionStatus = false;

    /* THE ARRAY LIST INSTANCES */
    private ArrayList<Review> arrReviews = new ArrayList<>();
    private ArrayList<Clinic> arrClinics = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.appBar) AppBarLayout appBar;
    @BindView(R.id.toolbarLayout) CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.imgvwDoctorProfile) SimpleDraweeView imgvwDoctorProfile;
    @BindView(R.id.txtDoctorName) AppCompatTextView txtDoctorName;
    @BindView(R.id.txtDoctorEducation) AppCompatTextView txtDoctorEducation;
    @BindView(R.id.txtExperience) AppCompatTextView txtExperience;
    @BindView(R.id.txtVotes) AppCompatTextView txtVotes;
    @BindView(R.id.txtDoctorCharges) AppCompatTextView txtDoctorCharges;
    @BindView(R.id.linlaReviews) LinearLayout linlaReviews;
    @BindView(R.id.linlaReviewsProgress) LinearLayout linlaReviewsProgress;
    @BindView(R.id.listReviews) RecyclerView listReviews;
    @BindView(R.id.linlaNoReviews) LinearLayout linlaNoReviews;
    @BindView(R.id.linlaClinics) LinearLayout linlaClinics;
    @BindView(R.id.linlaClinicsProgress) LinearLayout linlaClinicsProgress;
    @BindView(R.id.listClinics) RecyclerView listClinics;
    @BindView(R.id.linlaNoClinics) LinearLayout linlaNoClinics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_profile_activity);
        ButterKnife.bind(this);

        /* GET THE USER'S ID */
        USER_ID = getApp().getUserID();

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* FETCH THE INCOMING DATA */
        fetchIncomingData();

        /* FETCH THE DOCTOR'S SUBSCRIPTION */
        fetchDoctorSubscription();

        /* SHOW THE PROGRESS AND FETCH THE FIRST 3 REVIEWS FOR THE DOCTOR */
        linlaReviewsProgress.setVisibility(View.VISIBLE);
        listReviews.setVisibility(View.GONE);
        fetchDoctorReviewsSubset();

        /* SHOW THE PROGRESS AND FETCH THE LIST OF CLINICS */
        linlaClinicsProgress.setVisibility(View.VISIBLE);
        listClinics.setVisibility(View.GONE);
        fetchDoctorClinics();

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

    /***** FETCH THE LIST OF CLINICS WHERE THE DOCTOR PRACTICES *****/
    private void fetchDoctorClinics() {
        ClinicsAPI api = ZenApiClient.getClient().create(ClinicsAPI.class);
        Call<Clinics> call = api.fetchDoctorClinics(DOCTOR_ID);
        call.enqueue(new Callback<Clinics>() {
            @Override
            public void onResponse(Call<Clinics> call, Response<Clinics> response) {
                if (response.body() != null && response.body().getClinics() != null)    {
                    arrClinics = response.body().getClinics();
                    if (arrClinics.size() > 0)  {
                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY REVIEWS VIEW */
                        linlaClinics.setVisibility(View.VISIBLE);
                        linlaNoClinics.setVisibility(View.GONE);
                        listClinics.setVisibility(View.VISIBLE);

                        /* SET THE REVIEWS ADAPTER TO THE RECYCLER VIEW */
                        listClinics.setAdapter(new DoctorClinicsAdapter(DoctorProfileActivity.this, arrClinics));
                    } else {
                        /* SHOW THE NO CLINICS LAYOUT */
                        linlaNoClinics.setVisibility(View.VISIBLE);
                        linlaClinics.setVisibility(View.GONE);
                        listClinics.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE NO CLINICS LAYOUT */
                    linlaNoClinics.setVisibility(View.VISIBLE);
                    linlaClinics.setVisibility(View.GONE);
                    listClinics.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DOCTOR'S CLINICS */
                linlaClinicsProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Clinics> call, Throwable t) {
//                Log.e("CLINICS FAILURE", t.getMessage());
            }
        });
    }

    /***** FETCH THE DOCTOR'S SUBSCRIPTION *****/
    private void fetchDoctorSubscription() {
        SubscriptionsAPI api = ZenApiClient.getClient().create(SubscriptionsAPI.class);
        Call<SubscriptionData> call = api.checkDoctorSubscription(DOCTOR_ID);
        call.enqueue(new Callback<SubscriptionData>() {
            @Override
            public void onResponse(Call<SubscriptionData> call, Response<SubscriptionData> response) {
//                Log.e("SUB RESPONSE", String.valueOf(response.raw()));
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
                } else {
                    blnSubscriptionStatus = false;
                    invalidateOptionsMenu();
                }
            }

            @Override
            public void onFailure(Call<SubscriptionData> call, Throwable t) {
            }
        });
    }

    /***** FETCH THE FIRST 3 REVIEWS FOR THE DOCTOR *****/
    private void fetchDoctorReviewsSubset() {
        ReviewsAPI api = ZenApiClient.getClient().create(ReviewsAPI.class);
        Call<Reviews> call = api.fetchDoctorReviewsSubset(DOCTOR_ID);
        call.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                if (response.body() != null && response.body().getReviews() != null)    {
                    arrReviews = response.body().getReviews();
                    if (arrReviews.size() > 0)  {
                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY REVIEWS VIEW */
                        linlaReviews.setVisibility(View.VISIBLE);
                        linlaNoReviews.setVisibility(View.GONE);
                        listReviews.setVisibility(View.VISIBLE);

                        /* SET THE REVIEWS ADAPTER TO THE RECYCLER VIEW */
                        listReviews.setAdapter(new ReviewsAdapter(DoctorProfileActivity.this, arrReviews));
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
            }
        });
    }

    /***** FETCH THE DOCTOR'S DETAILS *****/
    private void fetchDoctorDetails() {
        DoctorsAPI api = ZenApiClient.getClient().create(DoctorsAPI.class);
        Call<DoctorProfile> call = api.fetchDoctorProfile(DOCTOR_ID);
        call.enqueue(new Callback<DoctorProfile>() {
            @Override
            public void onResponse(Call<DoctorProfile> call, Response<DoctorProfile> response) {
                DoctorProfile data = response.body();
                if (data != null)   {
                    /* GET AND SET THE DOCTOR'S PREFIX AND NAME */
                    String DOCTOR_PREFIX = data.getDoctorPrefix();
                    String DOCTOR_NAME = data.getDoctorName();
                    txtDoctorName.setText(getString(R.string.doctor_details_doc_name_placeholder, DOCTOR_PREFIX, DOCTOR_NAME));
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

                    /* GET AND SET THE EXPERIENCE (IN YEARS) */
                    String DOCTOR_EXPERIENCE = data.getDoctorExperience();
                    txtExperience.setText(getString(R.string.doctor_details_doc_exp_placeholder, DOCTOR_EXPERIENCE));

                    /* GET AND SET THE DOCTOR CHARGES */
                    String CLINIC_CURRENCY = data.getCurrencySymbol();
                    String DOCTOR_CHARGES = data.getDoctorCharges();
                    txtDoctorCharges.setText(getString(R.string.doctor_details_doc_charges_placeholder, CLINIC_CURRENCY, DOCTOR_CHARGES));

                    /* GET THE DOCTOR'S PHONE NUMBER */
                    DOCTOR_PHONE_NUMBER = data.getDoctorPhoneNumber();

                    /* FETCH THE DOCTOR'S EDUCATIONAL QUALIFICATIONS */
                    fetchDoctorEducation();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<DoctorProfile> call, Throwable t) {
//                Log.e("PROFILE FAILURE", t.getMessage());
            }
        });
    }

    /***** FETCH THE DOCTOR'S EDUCATIONAL QUALIFICATIONS *****/
    private void fetchDoctorEducation() {
        QualificationsAPI api = ZenAPIScalar.getClient().create(QualificationsAPI.class);
        Call<String> call = api.fetchDoctorEducationString(DOCTOR_ID);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    String strResult = response.body();
                    JSONObject JORoot = new JSONObject(strResult);
                    if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
                        JSONArray JAQualifications = JORoot.getJSONArray("qualifications");
                        sb = new StringBuilder();
                        for (int i = 0; i < JAQualifications.length(); i++) {
                            JSONObject JOQualifications = JAQualifications.getJSONObject(i);

                            if (JOQualifications.has("doctorEducationName"))    {
                                String doctorEducationName = JOQualifications.getString("doctorEducationName");
                                sb.append(doctorEducationName).append(", ");
                            }
                        }
                        String strEducation = sb.toString();
                        if (strEducation.endsWith(", "))    {
                            strEducation = strEducation.substring(0, strEducation.length() - 2);
                            txtDoctorEducation.setText(strEducation);
                        } else {
                            txtDoctorEducation.setText(strEducation);
                        }
                    } else {
                        sb = null;
                        txtDoctorEducation.setText(getString(R.string.doctor_details_doc_education_empty));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
//                Log.e("EDUCATION FAILURE", t.getMessage());
            }
        });
    }

    /***** FETCH THE INCOMING DATA *****/
    private void fetchIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("DOCTOR_ID"))  {
            DOCTOR_ID = bundle.getString("DOCTOR_ID");
            if (DOCTOR_ID != null)  {
                /* FETCH THE DOCTOR'S DETAILS */
                fetchDoctorDetails();
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
        MenuInflater inflater = new MenuInflater(DoctorProfileActivity.this);
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
                Intent intentAppointment = new Intent(getApplicationContext(), AppointmentClinicSelector.class);
                intentAppointment.putExtra("DOCTOR_ID", DOCTOR_ID);
                startActivity(intentAppointment);
                break;
            case R.id.menuCall:
                if (ContextCompat.checkSelfPermission(DoctorProfileActivity.this,
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
                                        DoctorProfileActivity.this,
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
                Review review = response.body();
                if (review != null) {
                    String reviewID = review.getReviewID();
                    Intent intent = new Intent(DoctorProfileActivity.this, ReviewModifier.class);
                    intent.putExtra("REVIEW_ID", reviewID);
                    startActivityForResult(intent, 101);
                } else {
                    String profileStatus = getApp().getProfileStatus();
                    if (profileStatus.equalsIgnoreCase("Incomplete"))   {
                        String message = "You need to complete your Profile before you can provide feedback. To complete your profile Details, click on the \"Complete Profile\" button.";
                        new MaterialDialog.Builder(DoctorProfileActivity.this)
                                .icon(ContextCompat.getDrawable(DoctorProfileActivity.this, R.drawable.ic_info_outline_black_24dp))
                                .title("Profile Incomplete")
                                .cancelable(true)
                                .content(message)
                                .positiveText("Complete Profile")
                                .theme(Theme.LIGHT)
                                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        Intent intent = new Intent(DoctorProfileActivity.this, ProfileEditor.class);
                                        startActivity(intent);
                                    }
                                }).show();
                    } else {
                        Intent intentNewFeedback = new Intent(getApplicationContext(), ReviewClinicSelector.class);
                        intentNewFeedback.putExtra("DOCTOR_ID", DOCTOR_ID);
                        startActivity(intentNewFeedback);
                    }
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
//                Log.e("CHECK FAILURE", t.getMessage());
            }
        });
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        /* CONFIGURE THE REVIEWS RECYCLER VIEW */
        LinearLayoutManager reviews = new LinearLayoutManager(this);
        reviews.setOrientation(LinearLayoutManager.VERTICAL);
        reviews.setAutoMeasureEnabled(true);
        listReviews.setLayoutManager(reviews);
        listReviews.setHasFixedSize(true);
        listReviews.setNestedScrollingEnabled(false);
        listReviews.setAdapter(new ReviewsAdapter(DoctorProfileActivity.this, arrReviews));

        /* CONFIGURE THE CLINICS RECYCLER VIEW */
        LinearLayoutManager clinics = new LinearLayoutManager(this);
        clinics.setOrientation(LinearLayoutManager.VERTICAL);
        clinics.setAutoMeasureEnabled(true);
        listClinics.setLayoutManager(clinics);
        listClinics.setHasFixedSize(true);
        listClinics.setNestedScrollingEnabled(false);
        listClinics.setAdapter(new DoctorClinicsAdapter(DoctorProfileActivity.this, arrClinics));
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
        if (ContextCompat.checkSelfPermission(DoctorProfileActivity.this,
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
                                DoctorProfileActivity.this,
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
}