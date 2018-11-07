package co.zenpets.doctors;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.zenpets.doctors.creator.clinic.ClinicSearch;
import co.zenpets.doctors.creator.clinic.InitialClinicCreator;
import co.zenpets.doctors.credentials.ClaimProfileStatus;
import co.zenpets.doctors.credentials.LoginActivity;
import co.zenpets.doctors.landing.LandingActivity;
import co.zenpets.doctors.utils.AppPrefs;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.models.doctors.Doctor;
import co.zenpets.doctors.utils.models.doctors.DoctorsAPI;
import co.zenpets.doctors.utils.models.doctors.clinic.Clinic;
import co.zenpets.doctors.utils.models.doctors.clinic.ClinicCheckerAPI;
import co.zenpets.doctors.utils.models.doctors.clinic.ClinicCheckerData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** A FIREBASE USER INSTANCE  **/
    private FirebaseUser user;

    /** THE DOCTOR'S ID **/
    private String DOCTOR_ID = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtAppName) AppCompatTextView txtAppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        ButterKnife.bind(this);

        /* SET THE CUSTOM FONT */
        txtAppName.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Bariol_Light.otf"));

        /* CONFIGURE THE ANIMATIONS */
        Animation animIn = new AlphaAnimation(0.0f, 1.0f);
        animIn.setDuration(2000);

        /* ANIMATE THE APP NAME */
        txtAppName.startAnimation(animIn);
        animIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                /* CHECK IF A PROFILE IS BEING CLAIMED */
                String claimApproved = getApp().getClaimApproved();
                if (claimApproved == null)    {
                    /* DETERMINE IF THE USER IS LOGGED IN */
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null)   {
                        /* SAVE THE DOCTOR ID */
                        saveDoctorID();
                    } else {
                        Intent showLogin = new Intent(SplashScreen.this, LoginActivity.class);
                        showLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(showLogin);
                        finish();
                    }
                } else if (claimApproved.equalsIgnoreCase("no")){
                    Intent showStatus = new Intent(SplashScreen.this, ClaimProfileStatus.class);
                    showStatus.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(showStatus);
                    finish();
                } else if (claimApproved.equalsIgnoreCase("yes"))    {
                    /* DETERMINE IF THE USER IS LOGGED IN */
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null)   {
                        /* SAVE THE DOCTOR ID */
                        saveDoctorID();
                    } else {
                        Intent showLogin = new Intent(SplashScreen.this, LoginActivity.class);
                        showLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(showLogin);
                        finish();
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /***** SAVE THE DOCTOR ID *****/
    private void saveDoctorID() {
        DoctorsAPI apiInterface = ZenApiClient.getClient().create(DoctorsAPI.class);
        Call<Doctor> call = apiInterface.getDoctorID(user.getUid());
        call.enqueue(new Callback<Doctor>() {
            @Override
            public void onResponse(Call<Doctor> call, Response<Doctor> response) {
                /* GET THE DOCTOR'S ID */
                DOCTOR_ID = response.body().getDoctorID();
                if (DOCTOR_ID != null)    {
                    /* SET DOCTOR'S ID TO THE APP'S PRIVATE SHARED PREFERENCES */
                    getApp().setDoctorID(DOCTOR_ID);

                    /* CHECK IF THE DOCTOR HAS CREATED / MAPPED A CLINIC */
                    clinicChecker();
                }
            }

            @Override
            public void onFailure(Call<Doctor> call, Throwable t) {
            }
        });
    }

    /***** CHECK IF THE DOCTOR HAS ADDED / MAPPED ANY CLINIC *****/
    private void clinicChecker() {
        ClinicCheckerAPI apiInterface = ZenApiClient.getClient().create(ClinicCheckerAPI.class);
        Call<ClinicCheckerData> call = apiInterface.fetchDoctorClinics(DOCTOR_ID);
        call.enqueue(new Callback<ClinicCheckerData>() {
            @Override
            public void onResponse(Call<ClinicCheckerData> call, Response<ClinicCheckerData> response) {
                ArrayList<Clinic> arrClinics = response.body().getClinics();
                if (arrClinics != null && arrClinics.size() > 0)    {
                    Intent showLanding = new Intent(SplashScreen.this, LandingActivity.class);
                    showLanding.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(showLanding);
                    finish();
                } else {
                    /* SHOW THE NO CLINICS DIALOG */
                    showNoClinics();
                }
            }

            @Override
            public void onFailure(Call<ClinicCheckerData> call, Throwable t) {
            }
        });
    }

//    @Override
//    public void onClinicChecker(Boolean result) {
//        if (result) {
//            Intent showLanding = new Intent(SplashScreen.this, LandingActivity.class);
//            showLanding.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(showLanding);
//            finish();
//        } else {
//            /* SHOW THE NO CLINICS DIALOG */
//            showNoClinics();
//        }
//    }

    /***** SHOW THE NO CLINICS ADDED YET DIALOG *****/
    private void showNoClinics() {
        new MaterialDialog.Builder(SplashScreen.this)
                .icon(ContextCompat.getDrawable(SplashScreen.this, R.drawable.ic_info_outline_black_24dp))
                .title(getString(R.string.splash_clinic_prompter_title))
                .cancelable(false)
                .content(getString(R.string.splash_clinic_prompter_message))
                .positiveText(getString(R.string.splash_clinic_prompter_new))
                .negativeText(getString(R.string.splash_clinic_prompter_search))
                .theme(Theme.LIGHT)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(SplashScreen.this, InitialClinicCreator.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(SplashScreen.this, ClinicSearch.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }).show();
    }
}