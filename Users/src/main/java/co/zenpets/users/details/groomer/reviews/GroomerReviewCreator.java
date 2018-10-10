package co.zenpets.users.details.groomer.reviews;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import co.zenpets.users.R;
import co.zenpets.users.utils.AppPrefs;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.groomers.groomers.Groomer;
import co.zenpets.users.utils.models.groomers.groomers.GroomersAPI;
import co.zenpets.users.utils.models.groomers.notifications.Notification;
import co.zenpets.users.utils.models.groomers.notifications.NotificationsAPI;
import co.zenpets.users.utils.models.groomers.review.GroomerReview;
import co.zenpets.users.utils.models.groomers.review.GroomerReviewsAPI;
import co.zenpets.users.utils.models.user.UserData;
import co.zenpets.users.utils.models.user.UsersAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroomerReviewCreator extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN USER'S ID, NAME AND DISPLAY PROFILE **/
    private String USER_ID = null;
    private String USER_NAME = null;
    private String USER_DISPLAY_PROFILE = null;

    /** THE INCOMING GROOMER ID **/
    private String GROOMER_ID = null;

    /** THE GROOMER'S DEVICE TOKEN **/
    String DEVICE_TOKEN = null;

    /** DATA TYPES TO HOLD THE USER SELECTIONS **/
    private String RECOMMEND_STATUS = "Yes";
    private String GROOMER_RATING = "0.0";

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog dialog;

    /** THE NEW POSTED REVIEW ID **/
    String REVIEW_ID = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwGroomerLogo) SimpleDraweeView imgvwGroomerLogo;
    @BindView(R.id.txtGroomerName) TextView txtGroomerName;
    @BindView(R.id.scrollContainer) ScrollView scrollContainer;
    @BindView(R.id.rdgRecommend) RadioGroup rdgRecommend;
    @BindView(R.id.ratingGroomer) RatingBar ratingGroomer;
    @BindView(R.id.inputExperience) TextInputLayout inputExperience;
    @BindView(R.id.edtExperience) TextInputEditText edtExperience;
    @BindView(R.id.txtTermsOfService) TextView txtTermsOfService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groomer_review_creator);
        ButterKnife.bind(this);

        /* GET THE USER ID */
        USER_ID = getApp().getUserID();

        /* GET THE USER'S DETAILS */
        fetchUserDetails();

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

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("GROOMER_ID"))   {
            GROOMER_ID = bundle.getString("GROOMER_ID");
            if (GROOMER_ID != null)  {
                /* FETCH THE GROOMER'S DETAILS */
                fetchGroomerDetails();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /** FETCH THE GROOMER'S DETAILS **/
    private void fetchGroomerDetails() {
        GroomersAPI api = ZenApiClient.getClient().create(GroomersAPI.class);
        Call<Groomer> call = api.fetchGroomerDetails(GROOMER_ID, null, null);
        call.enqueue(new Callback<Groomer>() {
            @Override
            public void onResponse(Call<Groomer> call, Response<Groomer> response) {
                if (!response.body().getError()) {
                    Groomer groomer = response.body();

                    /* GET THE DEVICE TOKEN */
                    DEVICE_TOKEN = groomer.getGroomerToken();
                    Log.e("TOKEN", DEVICE_TOKEN);

                    /* GET AND SET THE GROOMER'S NAME */
                    String groomerName = groomer.getGroomerName();
                    txtGroomerName.setText(groomerName);

                    /* GET AND SET THE GROOMER'S LOGO */
                    String groomerLogo = groomer.getGroomerLogo();
                    if (groomerLogo != null
                            && !groomerLogo.equalsIgnoreCase("")
                            && !groomerLogo.equalsIgnoreCase("null"))    {
                        Uri uriClinic = Uri.parse(groomerLogo);
                        imgvwGroomerLogo.setImageURI(uriClinic);
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.ic_business_black_24dp)
                                .build();
                        imgvwGroomerLogo.setImageURI(request.getSourceUri());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Groomer> call, Throwable t) {
//                Log.e("DETAILS FAILURE", t.getMessage());
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
        GROOMER_RATING = String.valueOf(ratingGroomer.getRating());

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

            /* GET THE CURRENT TIME STAMP */
            String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);

            /* GET THE CURRENT DATE */
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = new Date();
            String REVIEW_DATE = format.format(date);

            GroomerReviewsAPI api = ZenApiClient.getClient().create(GroomerReviewsAPI.class);
            Call<GroomerReview> call = api.newGroomerReview(
                    GROOMER_ID, USER_ID, GROOMER_RATING, RECOMMEND_STATUS, KENNEL_EXPERIENCE, timeStamp, REVIEW_DATE);
            call.enqueue(new Callback<GroomerReview>() {
                @Override
                public void onResponse(Call<GroomerReview> call, Response<GroomerReview> response) {
                    if (response.isSuccessful() && !response.body().getError())    {
                        /* GET THE REVIEW ID */
                        REVIEW_ID = response.body().getReviewID();

                        dialog.dismiss();

                        /* SEND A NOTIFICATION TO THE GROOMER'S DEVICE */
                        sendReviewNotification();
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
                public void onFailure(Call<GroomerReview> call, Throwable t) {
//                    Log.e("REVIEW FAILURE", t.getMessage());
                }
            });
        }
    }

    /** SEND A NOTIFICATION TO THE GROOMER'S DEVICE **/
    private void sendReviewNotification() {
        NotificationsAPI api = ZenApiClient.getClient().create(NotificationsAPI.class);
        Call<Notification> call = api.sendGroomerReviewNotification(
                DEVICE_TOKEN, "New review posted by " + USER_NAME,
                edtExperience.getText().toString(), "New Review", REVIEW_ID,
                USER_ID, USER_NAME, USER_DISPLAY_PROFILE);
        call.enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, Response<Notification> response) {
                Log.e("PUSH RESPONSE", String.valueOf(response.raw()));
            }

            @Override
            public void onFailure(Call<Notification> call, Throwable t) {
                Log.e("PUSH FAILURE", t.getMessage());
            }
        });
    }

    /***** GET THE USER'S DETAILS *****/
    private void fetchUserDetails() {
        UsersAPI api = ZenApiClient.getClient().create(UsersAPI.class);
        Call<UserData> call = api.fetchUserDetails(USER_ID);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                UserData data = response.body();
                if (data != null)   {
                    USER_NAME = data.getUserName();
                    USER_DISPLAY_PROFILE = data.getUserDisplayProfile();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
//                Log.e("PROFILE FAILURE", t.getMessage());
            }
        });
    }
}