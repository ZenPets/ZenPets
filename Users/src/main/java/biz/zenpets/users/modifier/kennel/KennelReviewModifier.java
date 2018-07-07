package biz.zenpets.users.modifier.kennel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.kennels.reviews.KennelReview;
import biz.zenpets.users.utils.models.kennels.reviews.KennelReviewsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KennelReviewModifier extends AppCompatActivity {

    /** THE INCOMING REVIEW ID **/
    private String REVIEW_ID = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwKennelCoverPhoto) SimpleDraweeView imgvwKennelCoverPhoto;
    @BindView(R.id.txtKennelName) TextView txtKennelName;
    @BindView(R.id.scrollContainer) ScrollView scrollContainer;
    @BindView(R.id.rdgRecommend) RadioGroup rdgRecommend;
    @BindView(R.id.btnYes) RadioButton btnYes;
    @BindView(R.id.btnNo) RadioButton btnNo;
    @BindView(R.id.ratingKennel) RatingBar ratingKennel;
    @BindView(R.id.inputExperience) TextInputLayout inputExperience;
    @BindView(R.id.edtExperience) TextInputEditText edtExperience;
    @BindView(R.id.txtTermsOfService) TextView txtTermsOfService;

    /** STRINGS TO HOLD THE COLLECTED REVIEW INFORMATION **/
    private String RECOMMEND_STATUS = null;
    private String KENNEL_RATING = null;
    private String KENNEL_EXPERIENCE = null;

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kennel_review_modifier);
        ButterKnife.bind(this);

        /* CONFIGURE THE TOOLBAR **/
        configTB();

        /* GET THE INCOMING DATA */
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
    }

    /***** GET THE REVIEW DETAILS *****/
    private void fetchReviewDetails()   {
        KennelReviewsAPI api = ZenApiClient.getClient().create(KennelReviewsAPI.class);
        Call<KennelReview> call = api.fetchKennelReviewDetails(REVIEW_ID);
        call.enqueue(new Callback<KennelReview>() {
            @Override
            public void onResponse(@NonNull Call<KennelReview> call, @NonNull Response<KennelReview> response) {
//                Log.e("REVIEW DETAILS", String.valueOf(response.raw()));
                KennelReview review = response.body();
                if (review != null) {

                    /* GET AND SET THE KENNEL NAME */
                    String kennelName = review.getKennelName();
                    txtKennelName.setText(kennelName);

                    /* GET AND SET THE KENNEL COVER PHOTO */
                    String kennelCoverPhoto = review.getKennelCoverPhoto();
                    if (kennelCoverPhoto != null
                            && !kennelCoverPhoto.equalsIgnoreCase("")
                            && !kennelCoverPhoto.equalsIgnoreCase("null"))    {
                        Uri uriClinic = Uri.parse(kennelCoverPhoto);
                        imgvwKennelCoverPhoto.setImageURI(uriClinic);
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.ic_business_black_24dp)
                                .build();
                        imgvwKennelCoverPhoto.setImageURI(request.getSourceUri());
                    }

                    /* GET AND SET THE RECOMMEND STATUS */
                    RECOMMEND_STATUS = review.getKennelRecommendStatus();
                    if (RECOMMEND_STATUS != null)   {
                        if (RECOMMEND_STATUS.equalsIgnoreCase("Yes"))   {
                            btnYes.setChecked(true);
                            btnNo.setChecked(false);
                        } else if (RECOMMEND_STATUS.equalsIgnoreCase("No")) {
                            btnNo.setChecked(true);
                            btnYes.setChecked(false);
                        }
                    }

                    /* GET AND SET THE KENNEL RATING */
                    KENNEL_RATING = review.getKennelRating();
                    if (KENNEL_RATING != null && !KENNEL_RATING.equalsIgnoreCase("null")) {
                        Double dblRating = Double.valueOf(KENNEL_RATING);
                        String finalRating = String.format("%.1f", dblRating);
                        ratingKennel.setRating(Float.parseFloat(finalRating));
                    } else {
                        ratingKennel.setRating(0);
                    }

                    /* GET AND SET THE KENNEL EXPERIENCE */
                    KENNEL_EXPERIENCE = review.getKennelExperience();
                    edtExperience.setText(KENNEL_EXPERIENCE);
                    int cursorPosition = edtExperience.length();
                    edtExperience.setSelection(cursorPosition);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<KennelReview> call, @NonNull Throwable t) {
//                Log.e("REVIEW FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("REVIEW_ID"))  {
            REVIEW_ID = bundle.getString("REVIEW_ID");
            if (REVIEW_ID != null)  {
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

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Update Your Kennel Review";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(strTitle);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(getApplicationContext());
        inflater.inflate(R.menu.activity_save_cancel, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuSave:
                /* VALIDATE REVIEW DATA */
                validateData();
                break;
            case R.id.menuCancel:
                break;
            default:
                break;
        }
        return false;
    }

    /** VALIDATE REVIEW DATA **/
    private void validateData() {
        /* HIDE THE KEYBOARD **/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtExperience.getWindowToken(), 0);
        }

        /* GET THE DATA **/
        String KENNEL_EXPERIENCE = edtExperience.getText().toString().trim();
        KENNEL_RATING = String.valueOf(ratingKennel.getRating());

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
        } else if (TextUtils.isEmpty(KENNEL_EXPERIENCE))    {
            inputExperience.setErrorEnabled(true);
            inputExperience.setError("Please provide your experience");
            inputExperience.requestFocus();
        } else {
            inputExperience.setErrorEnabled(false);

            /* SHOW THE PROGRESS DIALOG AND POST THE NEW REVIEW */
            dialog = new ProgressDialog(this);
            dialog.setMessage("Please wait while we update your review..");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();

            String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
            KennelReviewsAPI api = ZenApiClient.getClient().create(KennelReviewsAPI.class);
            Call<KennelReview> call = api.updateKennelReview(
                    REVIEW_ID, KENNEL_RATING, RECOMMEND_STATUS, KENNEL_EXPERIENCE, timeStamp);
            call.enqueue(new Callback<KennelReview>() {
                @Override
                public void onResponse(Call<KennelReview> call, Response<KennelReview> response) {
                    if (response.isSuccessful() && !response.body().getError())    {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Review update successfully...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        /* DISMISS THE DIALOG */
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error updating review...", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<KennelReview> call, Throwable t) {
//                    Log.e("REVIEW FAILURE", t.getMessage());
                    Crashlytics.logException(t);
                }
            });
        }
    }
}