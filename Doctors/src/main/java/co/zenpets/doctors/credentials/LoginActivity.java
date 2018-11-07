package co.zenpets.doctors.credentials;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Patterns;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import co.zenpets.doctors.R;
import co.zenpets.doctors.creator.clinic.ClinicSearch;
import co.zenpets.doctors.creator.clinic.InitialClinicCreator;
import co.zenpets.doctors.landing.LandingActivity;
import co.zenpets.doctors.utils.AppPrefs;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.models.doctors.Doctor;
import co.zenpets.doctors.utils.models.doctors.DoctorsAPI;
import co.zenpets.doctors.utils.models.doctors.clinic.Clinic;
import co.zenpets.doctors.utils.models.doctors.clinic.ClinicCheckerAPI;
import co.zenpets.doctors.utils.models.doctors.clinic.ClinicCheckerData;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE FIREBASE AUTH INSTANCE **/
    private FirebaseAuth auth;

    /** THE FIREBASE AUTH STATE LISTENER INSTANCE **/
    private FirebaseAuth.AuthStateListener stateListener;

    /** THE FIREBASE USER INSTANCE **/
    private FirebaseUser user;

    /** THE DOCTOR ID **/
    private String DOCTOR_ID = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtAppName) AppCompatTextView txtAppName;
    @BindView(R.id.txtAppIdentifier) AppCompatTextView txtAppIdentifier;
    @BindView(R.id.inputEmailAddress) TextInputLayout inputEmailAddress;
    @BindView(R.id.edtEmailAddress) TextInputEditText edtEmailAddress;
    @BindView(R.id.inputPassword) TextInputLayout inputPassword;
    @BindView(R.id.edtPassword) TextInputEditText edtPassword;

    /** PERFORM THE SIGN IN **/
    @OnClick(R.id.btnSignIn) protected void signIn()  {
        String strEmail = edtEmailAddress.getText().toString().trim();
        String strPassword = edtPassword.getText().toString().trim();
        boolean isValid = isValidEmail(strEmail);
        if (TextUtils.isEmpty(strEmail)) {
            inputEmailAddress.setErrorEnabled(true);
            inputPassword.setErrorEnabled(false);
            inputEmailAddress.setError("Please enter your email address");
        } else if (!isValid)    {
            inputEmailAddress.setErrorEnabled(true);
            inputPassword.setErrorEnabled(false);
            inputEmailAddress.setError("Please enter a valid email address");
        } else if (TextUtils.isEmpty(strPassword))  {
            inputEmailAddress.setErrorEnabled(false);
            inputPassword.setErrorEnabled(true);
            inputPassword.setError("Please enter your password");
        } else {
            auth.signInWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful())    {
                        String result = String.valueOf(task.getException());
                        if (result.contains("There is no user record corresponding to this identifier"))    {
                            /* SHOW UNREGISTERED USER DIALOG */
                            showUnregisteredUser();
                        } else if (result.contains("The password is invalid or the user does not have a password"))   {
                            /* SHOW AUTHENTICATION FAILED DIALOG */
                            showAuthFailed();
                        }
                    }
                }
            });
        }
    }

    /** CREATE A NEW ACCOUNT **/
    @OnClick(R.id.txtCreateAccount) protected void newAccount()   {
        new MaterialDialog.Builder(LoginActivity.this)
                .icon(ContextCompat.getDrawable(LoginActivity.this, R.drawable.ic_info_outline_black_24dp))
                .title("Select Option")
                .cancelable(true)
                .content("If you have received an Email / Phone Call / Letter / Message informing you that your profile has been listed, select the option \"Claim Profile\".\n\nIf you haven't been intimated by us, select the option \"Create Account\"")
                .positiveText("Create Account")
                .negativeText("Claim Profile")
                .theme(Theme.LIGHT)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                        startActivityForResult(intent, 101);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(LoginActivity.this, ClaimProfileList.class);
                        startActivityForResult(intent, 101);
                    }
                }).show();
    }

    /** FORGOT ACCOUNT PASSWORD **/
    @OnClick(R.id.txtForgotPassword) protected void forgotPassword()   {
        Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
        startActivityForResult(intent, 102);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);

        /* SET THE CUSTOM FONT */
        txtAppName.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Bariol_Light.otf"));
        txtAppIdentifier.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Bariol_Light.otf"));

        /* INITIALIZE THE FIREBASE AUTH INSTANCE */
        auth = FirebaseAuth.getInstance();

        /* START THE AUTH STATE LISTENER */
        stateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    /* SAVE THE DOCTOR ID */
                    saveDoctorID();
                }
            }
        };
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
//                Log.e("FAILURE", t.getMessage());
//                Crashlytics.logException(t);
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
                    Intent showLanding = new Intent(LoginActivity.this, LandingActivity.class);
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

    /***** SHOW THE NO CLINICS ADDED YET DIALOG *****/
    private void showNoClinics() {
        new MaterialDialog.Builder(LoginActivity.this)
                .icon(ContextCompat.getDrawable(LoginActivity.this, R.drawable.ic_info_outline_black_24dp))
                .title(getString(R.string.splash_clinic_prompter_title))
                .cancelable(true)
                .content(getString(R.string.splash_clinic_prompter_message))
                .positiveText(getString(R.string.splash_clinic_prompter_new))
                .negativeText(getString(R.string.splash_clinic_prompter_search))
                .theme(Theme.LIGHT)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(LoginActivity.this, InitialClinicCreator.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(LoginActivity.this, ClinicSearch.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }).show();
    }

    /** SHOW UNREGISTERED USER DIALOG **/
    private void showUnregisteredUser() {
        new MaterialDialog.Builder(LoginActivity.this)
                .icon(ContextCompat.getDrawable(LoginActivity.this, R.drawable.ic_info_outline_black_24dp))
                .title(getString(R.string.login_unregistered_title))
                .cancelable(true)
                .content(getString(R.string.login_unregistered_message))
                .positiveText(getString(R.string.login_unregistered_create))
                .negativeText(getString(R.string.login_unregistered_cancel))
                .theme(Theme.LIGHT)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent signUp = new Intent(LoginActivity.this, SignUpActivity.class);
                        startActivity(signUp);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    /** SHOW AUTHENTICATION FAILED DIALOG **/
    private void showAuthFailed() {
        new MaterialDialog.Builder(LoginActivity.this)
                .icon(ContextCompat.getDrawable(LoginActivity.this, R.drawable.ic_info_outline_black_24dp))
                .title(getString(R.string.login_auth_failed_title))
                .cancelable(true)
                .content(getString(R.string.login_auth_failed_message))
                .positiveText(getString(R.string.login_auth_failed_forgot_password))
                .negativeText(getString(R.string.login_auth_failed_cancel))
                .theme(Theme.LIGHT)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent forgotPassword = new Intent(LoginActivity.this, ForgotPassword.class);
                        startActivity(forgotPassword);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(stateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (stateListener != null)  {
            auth.removeAuthStateListener(stateListener);
        }
    }

    /***** VALIDATE EMAIL SYNTAX / FORMAT *****/
    private static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}