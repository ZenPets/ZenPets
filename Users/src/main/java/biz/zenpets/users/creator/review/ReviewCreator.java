package biz.zenpets.users.creator.review;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.adapters.visit.VisitReasonsAdapter;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.helpers.doctors.reviews.AddNewReview;
import biz.zenpets.users.utils.helpers.doctors.reviews.AddNewReviewInterface;
import biz.zenpets.users.utils.helpers.doctors.reviews.PostClinicRating;
import biz.zenpets.users.utils.helpers.doctors.reviews.PostClinicRatingInterface;
import biz.zenpets.users.utils.models.doctors.DoctorsAPI;
import biz.zenpets.users.utils.models.doctors.list.Doctor;
import biz.zenpets.users.utils.models.visit.Reason;
import biz.zenpets.users.utils.models.visit.Reasons;
import biz.zenpets.users.utils.models.visit.ReasonsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings({"ConstantConditions", "deprecation"})
public class ReviewCreator extends AppCompatActivity
        implements /*FetchDoctorDetailsInterface,*/ AddNewReviewInterface, PostClinicRatingInterface {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE INCOMING DOCTOR AND CLINIC ID **/
    private String DOCTOR_ID = null;
    private String CLINIC_ID = null;

    /** THE USER ID **/
    private String USER_ID = null;

    /** DATA TYPES TO HOLD THE USER SELECTIONS **/
    private String RECOMMEND_STATUS = "Yes";
    private String APPOINTMENT_STATUS = "On Time";
    private String CLINIC_RATING = "2.0";
    private String VISIT_REASON_ID = null;

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog dialog;

    /** THE VISIT REASONS ARRAY LIST **/
    private ArrayList<Reason> arrReasons = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwDoctorProfile) SimpleDraweeView imgvwDoctorProfile;
    @BindView(R.id.txtDoctorName) AppCompatTextView txtDoctorName;
    @BindView(R.id.scrollContainer) ScrollView scrollContainer;
    @BindView(R.id.rdgRecommend) RadioGroup rdgRecommend;
    @BindView(R.id.rdgStartTime) RadioGroup rdgStartTime;
    @BindView(R.id.ratingClinicExperience) AppCompatRatingBar ratingClinicExperience;
    @BindView(R.id.spnVisitReason) AppCompatSpinner spnVisitReason;
    @BindView(R.id.inputExperience) TextInputLayout inputExperience;
    @BindView(R.id.edtExperience) AppCompatEditText edtExperience;
    @BindView(R.id.txtTermsOfService) AppCompatTextView txtTermsOfService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_creator);
        ButterKnife.bind(this);

        /* GET THE USER ID */
        USER_ID = getApp().getUserID();

        /* CONFIGURE THE TOOLBAR **/
        configTB();

        /* GET THE INCOMING DATA **/
        getIncomingData();

        /* CHECK THE RECOMMENDATION STATUS **/
        rdgRecommend.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.btnYes:
                        RECOMMEND_STATUS = "Yes";
                        break;
                    case R.id.btnNo:
                        RECOMMEND_STATUS = "No";
                        break;
                    default:
                        break;
                }
            }
        });

        /* CHECK THE APPOINTMENT START STATUS **/
        rdgStartTime.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.btnOnTime:
                        APPOINTMENT_STATUS = "On Time";
                        break;
                    case R.id.btnTenLate:
                        APPOINTMENT_STATUS = "Ten Minutes Late";
                        break;
                    case R.id.btnHalfHourLate:
                        APPOINTMENT_STATUS = "30 Minutes Late";
                        break;
                    case R.id.btnHourLate:
                        APPOINTMENT_STATUS = "More Than An Hour late";
                        break;
                    default:
                        break;
                }
            }
        });

        /* FETCH VISIT REASONS */
        fetchVisitReasons();

        /* SELECT A VISIT REASON */
        spnVisitReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                VISIT_REASON_ID = arrReasons.get(position).getVisitReasonID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

//    @Override
//    public void onDoctorDetails(String[] result) {
//        /* GET THE RESULTS FROM THE ARRAY */
//        String DOCTOR_PREFIX = result[0];
//        String DOCTOR_NAME = result[1];
//        String DOCTOR_PROFILE = result[2];
//
//        /* SET THE DOCTOR'S NAME */
//        txtDoctorName.setText(getString(R.string.review_creator_doc_name_placeholder, DOCTOR_PREFIX, DOCTOR_NAME));
//
//        /* SET THE DOCTOR'S DISPLAY PROFILE */
//        if (DOCTOR_PROFILE != null) {
//            Uri uri = Uri.parse(DOCTOR_PROFILE);
//            imgvwDoctorProfile.setImageURI(uri);
//        } else {
//            ImageRequest request = ImageRequestBuilder
//                    .newBuilderWithResourceId(R.drawable.ic_person_black_24dp)
//                    .build();
//            imgvwDoctorProfile.setImageURI(request.getSourceUri());
//        }
//    }

    /***** FETCH VISIT REASONS *****/
    private void fetchVisitReasons() {
        ReasonsAPI api = ZenApiClient.getClient().create(ReasonsAPI.class);
        Call<Reasons> call = api.visitReasons();
        call.enqueue(new Callback<Reasons>() {
            @Override
            public void onResponse(Call<Reasons> call, Response<Reasons> response) {
                arrReasons = response.body().getReasons();

                /* SET THE ADAPTER TO THE VISIT REASONS SPINNER */
                spnVisitReason.setAdapter(new VisitReasonsAdapter(ReviewCreator.this, arrReasons));
            }

            @Override
            public void onFailure(@NonNull Call<Reasons> call, Throwable t) {
//                Log.e("REASONS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("DOCTOR_ID") && bundle.containsKey("CLINIC_ID"))   {
            DOCTOR_ID = bundle.getString("DOCTOR_ID");
            CLINIC_ID = bundle.getString("CLINIC_ID");
            if (DOCTOR_ID != null)  {
                /* FETCH THE DOCTOR DETAILS */
                fetchDoctorDetails();
//                new FetchDoctorDetails(this).execute(DOCTOR_ID, CLINIC_ID);
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /** FETCH THE DOCTOR DETAILS **/
    private void fetchDoctorDetails() {
        DoctorsAPI api = ZenApiClient.getClient().create(DoctorsAPI.class);
        Call<Doctor> call = api.fetchDoctorDetails(DOCTOR_ID, CLINIC_ID, null, null);
        call.enqueue(new Callback<Doctor>() {
            @Override
            public void onResponse(Call<Doctor> call, Response<Doctor> response) {
                Doctor doctor = response.body();
                if (doctor != null) {

                    /* GET THE NECESSARY DOCTOR DETAILS */
                    String DOCTOR_PREFIX = doctor.getDoctorPrefix();
                    String DOCTOR_NAME = doctor.getDoctorName();
                    String DOCTOR_PROFILE = doctor.getDoctorDisplayProfile();

                    /* SET THE DOCTOR'S NAME */
                    txtDoctorName.setText(getString(R.string.review_creator_doc_name_placeholder, DOCTOR_PREFIX, DOCTOR_NAME));

                    /* SET THE DOCTOR'S DISPLAY PROFILE */
                    if (DOCTOR_PROFILE != null) {
                        Uri uri = Uri.parse(DOCTOR_PROFILE);
                        imgvwDoctorProfile.setImageURI(uri);
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.ic_person_black_24dp)
                                .build();
                        imgvwDoctorProfile.setImageURI(request.getSourceUri());
                    }
                }
            }

            @Override
            public void onFailure(Call<Doctor> call, Throwable t) {
//                Log.e("DETAILS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Post A Review";
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

    /***** VALIDATE REVIEW DATA *****/
    private void validateData() {
        /* HIDE THE KEYBOARD **/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtExperience.getWindowToken(), 0);
        }

        /* GET THE DATA **/
        String DOCTOR_EXPERIENCE = edtExperience.getText().toString().trim();
        CLINIC_RATING = String.valueOf(ratingClinicExperience.getRating());

        /* VERIFY ALL REQUIRED DATA **/
        int sdk = Build.VERSION.SDK_INT;
        if (TextUtils.isEmpty(RECOMMEND_STATUS))    {
            if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
                rdgRecommend.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.error_background));
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    rdgRecommend.setBackground(ContextCompat.getDrawable(this, R.drawable.error_background));
                }
            }
            scrollContainer.smoothScrollTo(0, rdgRecommend.getTop());
        } else if (TextUtils.isEmpty(APPOINTMENT_STATUS))   {
            if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
                rdgStartTime.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.error_background));
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    rdgStartTime.setBackground(ContextCompat.getDrawable(this, R.drawable.error_background));
                }
            }
            scrollContainer.smoothScrollTo(0, rdgStartTime.getTop());
        } else if (TextUtils.isEmpty(DOCTOR_EXPERIENCE))    {
            inputExperience.setErrorEnabled(true);
            inputExperience.setError("Please provide your experience");
            inputExperience.requestFocus();
        } else {
            inputExperience.setErrorEnabled(false);

            /* SHOW THE PROGRESS DIALOG AND POST THE NEW REVIEW */
            dialog = new ProgressDialog(this);
            dialog.setMessage("Please wait while we publish your review..");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
            new AddNewReview(this).execute(DOCTOR_ID, USER_ID, VISIT_REASON_ID,
                    RECOMMEND_STATUS, APPOINTMENT_STATUS, DOCTOR_EXPERIENCE);
        }
    }

    @Override
    public void onReviewResult(String result) {
        if (result != null) {
            /* POST THE CLINIC RATING */
            new PostClinicRating(this).execute(CLINIC_ID, USER_ID, CLINIC_RATING);
        } else {
            /* DISMISS THE DIALOG */
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "There was an error creating the new Clinic. Please try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRatingResult(String result) {
        if (result != null) {
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Successfully published your review", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        } else {
            dialog.dismiss();
            Toast.makeText(
                    getApplicationContext(),
                    "There was an error publishing your review. Please try again",
                    Toast.LENGTH_LONG).show();
        }
    }
}