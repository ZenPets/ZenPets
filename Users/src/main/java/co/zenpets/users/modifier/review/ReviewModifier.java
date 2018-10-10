package co.zenpets.users.modifier.review;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;

import co.zenpets.users.R;
import co.zenpets.users.utils.adapters.visit.VisitReasonsAdapter;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.reviews.Review;
import co.zenpets.users.utils.models.reviews.ReviewsAPI;
import co.zenpets.users.utils.models.visit.Reason;
import co.zenpets.users.utils.models.visit.Reasons;
import co.zenpets.users.utils.models.visit.ReasonsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewModifier extends AppCompatActivity {

    /** THE INCOMING REVIEW ID AND CLINIC ID **/
    private String REVIEW_ID = null;
    String CLINIC_ID = null;

    /** THE VISIT REASONS ARRAY LIST **/
    private ArrayList<Reason> arrReasons = new ArrayList<>();

    /** STRINGS TO HOLD THE CLINIC RATING ID AND CLINIC RATING **/
    String CLINIC_RATING_ID = null;
    String CLINIC_RATING = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwDoctorProfile) SimpleDraweeView imgvwDoctorProfile;
    @BindView(R.id.txtDoctorName) AppCompatTextView txtDoctorName;
    @BindView(R.id.scrollContainer) ScrollView scrollContainer;
    @BindView(R.id.rdgRecommend) RadioGroup rdgRecommend;
    @BindView(R.id.btnYes) AppCompatRadioButton btnYes;
    @BindView(R.id.btnNo) AppCompatRadioButton btnNo;
    @BindView(R.id.rdgStartTime) RadioGroup rdgStartTime;
    @BindView(R.id.btnOnTime) AppCompatRadioButton btnOnTime;
    @BindView(R.id.btnTenLate) AppCompatRadioButton btnTenLate;
    @BindView(R.id.btnHalfHourLate) AppCompatRadioButton btnHalfHourLate;
    @BindView(R.id.btnHourLate) AppCompatRadioButton btnHourLate;
    @BindView(R.id.ratingClinicExperience) AppCompatRatingBar ratingClinicExperience;
    @BindView(R.id.spnVisitReason) AppCompatSpinner spnVisitReason;
    @BindView(R.id.inputExperience) TextInputLayout inputExperience;
    @BindView(R.id.edtExperience) AppCompatEditText edtExperience;
    @BindView(R.id.txtTermsOfService) AppCompatTextView txtTermsOfService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_modifier);
        ButterKnife.bind(this);

        /* FETCH VISIT REASONS */
        fetchVisitReasons();

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* CONFIGURE THE TOOLBAR **/
        configTB();
    }

    /***** GET THE REVIEW DETAILS *****/
    private void fetchReviewDetails()   {
        ReviewsAPI api = ZenApiClient.getClient().create(ReviewsAPI.class);
        Call<Review> call = api.fetchDoctorReviewDetails(REVIEW_ID, CLINIC_ID);
        call.enqueue(new Callback<Review>() {
            @Override
            public void onResponse(@NonNull Call<Review> call, @NonNull Response<Review> response) {
//                Log.e("REVIEW DETAILS", String.valueOf(response.raw()));
                Review review = response.body();
                if (review != null) {
                    /* GET THE DOCTOR ID */
                    String DOCTOR_ID = review.getDoctorID();

                    /* GET AND SET THE DOCTOR'S PREFIX AND NAME */
                    String DOCTOR_PREFIX = review.getDoctorPrefix();
                    String DOCTOR_NAME = review.getDoctorName();
                    txtDoctorName.setText(getString(R.string.review_creator_doc_name_placeholder, DOCTOR_PREFIX, DOCTOR_NAME));

                    /* GET AND SET THE DOCTOR'S DISPLAY PROFILE */
                    String DOCTOR_DISPLAY_PROFILE = review.getDoctorDisplayProfile();
                    if (DOCTOR_DISPLAY_PROFILE != null) {
                        Uri uri = Uri.parse(DOCTOR_DISPLAY_PROFILE);
                        imgvwDoctorProfile.setImageURI(uri);
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.ic_person_black_24dp)
                                .build();
                        imgvwDoctorProfile.setImageURI(request.getSourceUri());
                    }

                    /* GET THE USER'S ID */
                    String USER_ID = review.getUserID();

                    /* GET THE VISIT REASON ID */
                    String VISIT_REASON_ID = review.getVisitReasonID();

                    /* GET AND SET THE RECOMMEND STATUS */
                    String RECOMMEND_STATUS = review.getRecommendStatus();
                    if (RECOMMEND_STATUS != null)   {
                        if (RECOMMEND_STATUS.equalsIgnoreCase("Yes"))   {
                            btnYes.setChecked(true);
                            btnNo.setChecked(false);
                        } else if (RECOMMEND_STATUS.equalsIgnoreCase("No")) {
                            btnNo.setChecked(true);
                            btnYes.setChecked(false);
                        }
                    }

                    /* GET THE APPOINTMENT STATUS */
                    String APPOINTMENT_STATUS = review.getAppointmentStatus();
                    if (APPOINTMENT_STATUS != null) {
                        if (APPOINTMENT_STATUS.equalsIgnoreCase("On Time")) {
                            btnOnTime.setChecked(true);
                            btnTenLate.setChecked(false);
                            btnHalfHourLate.setChecked(false);
                            btnHourLate.setChecked(false);
                        } else if (APPOINTMENT_STATUS.equalsIgnoreCase("Ten Minutes Late")) {
                            btnTenLate.setChecked(true);
                            btnOnTime.setChecked(false);
                            btnHalfHourLate.setChecked(false);
                            btnHourLate.setChecked(false);
                        } else if (APPOINTMENT_STATUS.equalsIgnoreCase("30 Minutes Late")) {
                            btnHalfHourLate.setChecked(true);
                            btnOnTime.setChecked(false);
                            btnTenLate.setChecked(false);
                            btnHourLate.setChecked(false);
                        } else if (APPOINTMENT_STATUS.equalsIgnoreCase("More Than An Hour late")) {
                            btnHourLate.setChecked(true);
                            btnOnTime.setChecked(false);
                            btnTenLate.setChecked(false);
                            btnHalfHourLate.setChecked(false);
                        }
                    }

                    /* GET AND SET THE CLINIC RATING ID AND THE CLINIC RATING */
                    CLINIC_RATING_ID = review.getClinicRatingID();
                    CLINIC_RATING = review.getClinicRating();
                    if (CLINIC_RATING != null && !CLINIC_RATING.equalsIgnoreCase("null")) {
                        ratingClinicExperience.setRating(Float.parseFloat(CLINIC_RATING));
                    } else {
                        ratingClinicExperience.setRating(0);
                    }

                    /* GET AND SET THE DOCTOR EXPERIENCE */
                    String DOCTOR_EXPERIENCE = review.getDoctorExperience();
                    edtExperience.setText(DOCTOR_EXPERIENCE);
                    int cursorPosition = edtExperience.length();
                    edtExperience.setSelection(cursorPosition);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Review> call, @NonNull Throwable t) {
//                Log.e("REVIEW FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null
                && bundle.containsKey("REVIEW_ID")
                && bundle.containsKey("CLINIC_ID"))  {
            REVIEW_ID = bundle.getString("REVIEW_ID");
            CLINIC_ID = bundle.getString("CLINIC_ID");
            if (REVIEW_ID != null && CLINIC_ID != null)  {
                /* GET THE REVIEW DETAILS */
                fetchReviewDetails();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** FETCH VISIT REASONS *****/
    private void fetchVisitReasons() {
        ReasonsAPI api = ZenApiClient.getClient().create(ReasonsAPI.class);
        Call<Reasons> call = api.visitReasons();
        call.enqueue(new Callback<Reasons>() {
            @Override
            public void onResponse(Call<Reasons> call, Response<Reasons> response) {
                arrReasons = response.body().getReasons();

                /* SET THE ADAPTER TO THE VISIT REASONS SPINNER */
                spnVisitReason.setAdapter(new VisitReasonsAdapter(ReviewModifier.this, arrReasons));
            }

            @Override
            public void onFailure(@NonNull Call<Reasons> call, Throwable t) {
//                Log.e("REASONS FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Update Your Review";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(strTitle);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(getApplicationContext());
        inflater.inflate(R.menu.activity_review_creator, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuSubmit:
                /* VALIDATE REVIEW DATA **/
                validateData();
                break;
            default:
                break;
        }
        return false;
    }

    /** VALIDATE REVIEW DATA **/
    private void validateData() {
    }
}