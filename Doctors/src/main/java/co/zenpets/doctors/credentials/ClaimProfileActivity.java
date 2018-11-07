package co.zenpets.doctors.credentials;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.AppPrefs;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.models.doctors.claims.Claim;
import co.zenpets.doctors.utils.models.doctors.claims.DoctorClaimsAPI;
import co.zenpets.doctors.utils.models.doctors.profile.DoctorProfileAPI;
import co.zenpets.doctors.utils.models.doctors.profile.DoctorProfileData;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClaimProfileActivity extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE INCOMING DOCTOR ID **/
    private String DOCTOR_ID = null;

    /** STRINGS TO HOLD THE DOCTOR'S DATA **/
    private String DOCTOR_PREFIX = null;
    private String DOCTOR_NAME = null;
    String DOCTOR_EMAIL = null;
    private String DOCTOR_ADDRESS = null;
    String COUNTRY_NAME = null;
    String STATE_NAME = null;
    String CITY_NAME = null;
    String DOCTOR_GENDER = null;
    String DOCTOR_DISPLAY_PROFILE = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.txtDoctorName) AppCompatTextView txtDoctorName;
    @BindView(R.id.txtDoctorAddress) AppCompatTextView txtDoctorAddress;
    @BindView(R.id.txtDoctorLikes) AppCompatTextView txtDoctorLikes;
    @BindView(R.id.txtDoctorExp) AppCompatTextView txtDoctorExp;
    @BindView(R.id.txtDoctorCharges) AppCompatTextView txtDoctorCharges;
    @BindView(R.id.inputEmailAddress) TextInputLayout inputEmailAddress;
    @BindView(R.id.edtEmailAddress) TextInputEditText edtEmailAddress;
    @BindView(R.id.inputPhone) TextInputLayout inputPhone;
    @BindView(R.id.edtPhone) TextInputEditText edtPhone;

    /** CLAIM THE DOCTOR PROFILE **/
    @OnClick(R.id.btnConfirmClaim) void claimProfile()  {
        checkClaimDetails();
    }

    /***** CHECK THE CLAIM DETAILS *****/
    private void checkClaimDetails() {
        /* HIDE THE KEYBOARD */
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtEmailAddress.getWindowToken(), 0);
        }

        /* COLLECT ALL THE DATA */
        String CLAIM_EMAIL = edtEmailAddress.getText().toString();
        String CLAIM_PHONE = edtPhone.getText().toString();

        /* VALIDATE THE EMAIL */
        boolean blnValidEmail = isValidEmail(CLAIM_EMAIL);

        /* VALIDATE THE DATA */
        if (TextUtils.isEmpty(CLAIM_EMAIL)) {
            inputEmailAddress.setError("Please provide you email address");
            inputEmailAddress.setErrorEnabled(true);
            inputPhone.setErrorEnabled(false);
        } else if (!blnValidEmail)  {
            inputEmailAddress.setError("Please provide a valid email address");
            inputEmailAddress.setErrorEnabled(true);
            inputPhone.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(CLAIM_PHONE))  {
            inputPhone.setError("Please provide your phone number");
            inputPhone.setErrorEnabled(true);
            inputEmailAddress.setErrorEnabled(false);
        } else {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Submitting your claim...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();

            /* ADD A NEW CLAIM */
            DoctorClaimsAPI api = ZenApiClient.getClient().create(DoctorClaimsAPI.class);
            Call<Claim> call = api.newDoctorClaim(
                    DOCTOR_ID, CLAIM_EMAIL, CLAIM_PHONE, "Pending", "No",
                    String.valueOf(System.currentTimeMillis() / 1000)
            );
            call.enqueue(new Callback<Claim>() {
                @Override
                public void onResponse(Call<Claim> call, Response<Claim> response) {
                    if (response.isSuccessful())    {
                        /* GET THE CLAIM ID */
                        String CLAIM_ID = response.body().getDoctorClaimID();

                        /* SET THE CLAIM STATUS AND ID TO SHARED PREFERENCES */
                        getApp().setClaimID(CLAIM_ID);
                        getApp().setClaimStatus("Pending");
                        getApp().setClaimApproved("No");
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "There was an error posting the new adoption. Please try again",
                                Toast.LENGTH_LONG).show();
                    }

                    /* DISMISS THE DIALOG */
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<Claim> call, Throwable t) {
//                    Log.e("CLAIM FAILURE", t.getMessage());
//                    Crashlytics.logException(t);
                }
            });
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.claim_profile_activity);
        ButterKnife.bind(this);

        /* GET THE INCOMING DATA */
        getIncomingData();
    }

    /***** FETCH THE DOCTOR'S DETAILS *****/
    private void fetchDoctorDetails() {
        DoctorProfileAPI apiInterface = ZenApiClient.getClient().create(DoctorProfileAPI.class);
        Call<DoctorProfileData> call = apiInterface.fetchDoctorProfile(DOCTOR_ID);
        call.enqueue(new Callback<DoctorProfileData>() {
            @Override
            public void onResponse(Call<DoctorProfileData> call, Response<DoctorProfileData> response) {
                DoctorProfileData data = response.body();

                if (data != null)   {
                    /* SET THE DOCTOR'S PREFIX AND NAME */
                    DOCTOR_PREFIX = data.getDoctorPrefix();
                    DOCTOR_NAME = data.getDoctorName();
                    txtDoctorName.setText(getString(R.string.doctor_profile_name_placeholder, DOCTOR_PREFIX, DOCTOR_NAME));

                    /* SET THE DOCTOR'S ADDRESS  */
                    DOCTOR_ADDRESS = data.getDoctorAddress();
                    txtDoctorAddress.setText(DOCTOR_ADDRESS);

//                    /* SET THE DOCTOR'S GENDER */
//                    DOCTOR_GENDER = data.getDoctorGender();
//                    txtGender.setText(DOCTOR_GENDER);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE PROFILE */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<DoctorProfileData> call, Throwable t) {
//                Log.e("FAILURE", t.getMessage());
//                Crashlytics.logException(t);
                linlaProgress.setVisibility(View.GONE);
            }
        });

        /* HIDE THE PROGRESS AFTER FETCHING THE DOCTOR'S PROFILE */
        linlaProgress.setVisibility(View.GONE);
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("DOCTOR_ID"))  {
            DOCTOR_ID = bundle.getString("DOCTOR_ID");
            if (DOCTOR_ID != null)  {
                /* SHOW THE PROGRESS AND FETCH THE DOCTOR'S DETAILS */
                linlaProgress.setVisibility(View.VISIBLE);
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

    /***** VALIDATE EMAIL SYNTAX / FORMAT *****/
    private static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}