package biz.zenpets.users.details.kennels.reviews;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.kennels.kennels.Kennel;
import biz.zenpets.users.utils.models.kennels.kennels.KennelsAPI;
import biz.zenpets.users.utils.models.kennels.reviews.KennelReview;
import biz.zenpets.users.utils.models.kennels.reviews.KennelReviewsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KennelReviewCreator extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE USER ID **/
    private String USER_ID = null;

    /** THE INCOMING KENNEL ID AND THE KENNEL OWNER'S ID **/
    private String KENNEL_ID = null;
    private String KENNEL_OWNER_ID = null;

    /** DATA TYPES TO HOLD THE USER SELECTIONS **/
    private String RECOMMEND_STATUS = "Yes";
    private String KENNEL_RATING = "0.0";

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog dialog;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwKennelCoverPhoto) SimpleDraweeView imgvwKennelCoverPhoto;
    @BindView(R.id.txtKennelName) TextView txtKennelName;
    @BindView(R.id.scrollContainer) ScrollView scrollContainer;
    @BindView(R.id.rdgRecommend) RadioGroup rdgRecommend;
    @BindView(R.id.ratingKennel) RatingBar ratingKennel;
    @BindView(R.id.inputExperience) TextInputLayout inputExperience;
    @BindView(R.id.edtExperience) TextInputEditText edtExperience;
    @BindView(R.id.txtTermsOfService) TextView txtTermsOfService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kennel_review_creator);
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
    }

    /** FETCH THE KENNEL'S DETAILS **/
    private void fetchKennelDetails() {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<Kennel> call = api.fetchKennelDetails(KENNEL_ID, null, null);
        call.enqueue(new Callback<Kennel>() {
            @Override
            public void onResponse(Call<Kennel> call, Response<Kennel> response) {
                if (!response.body().getError()) {
                    Kennel kennel = response.body();

                    /* GET AND SET THE KENNEL NAME */
                    String kennelName = kennel.getKennelName();
                    txtKennelName.setText(kennelName);

                    /* GET AND SET THE KENNEL COVER PHOTO */
                    String kennelCoverPhoto = kennel.getKennelCoverPhoto();
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
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Kennel> call, Throwable t) {
//                Log.e("DETAILS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("KENNEL_ID") && bundle.containsKey("KENNEL_OWNER_ID"))   {
            KENNEL_ID = bundle.getString("KENNEL_ID");
            KENNEL_OWNER_ID = bundle.getString("KENNEL_OWNER_ID");
            if (KENNEL_ID != null)  {
                /* FETCH THE KENNEL'S DETAILS */
                fetchKennelDetails();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
            finish();
        }
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
                /* VALIDATE REVIEW DATA */
                validateData();
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
            dialog.setMessage("Please wait while we publish your review..");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();

            String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
            KennelReviewsAPI api = ZenApiClient.getClient().create(KennelReviewsAPI.class);
            Call<KennelReview> call = api.newKennelReview(
                    KENNEL_ID, USER_ID, KENNEL_RATING, RECOMMEND_STATUS, KENNEL_EXPERIENCE, timeStamp);
            call.enqueue(new Callback<KennelReview>() {
                @Override
                public void onResponse(Call<KennelReview> call, Response<KennelReview> response) {
                    if (response.isSuccessful() && !response.body().getError())    {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Review published successfully...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        /* DISMISS THE DIALOG */
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error publishing review...", Toast.LENGTH_LONG).show();
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