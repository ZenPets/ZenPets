package co.zenpets.groomers.credentials;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.zenpets.groomers.R;
import co.zenpets.groomers.landing.LandingActivity;
import co.zenpets.groomers.utils.AppPrefs;
import co.zenpets.groomers.utils.helpers.ZenApiClient;
import co.zenpets.groomers.utils.models.groomers.Groomer;
import co.zenpets.groomers.utils.models.groomers.GroomersAPI;
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

    /** THE GROOMER ACCOUNT ID **/
    private String GROOMER_ID = null;

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
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivityForResult(intent, 101);
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
                    /* SAVE THE GROOMER ACCOUNT ID */
                    saveGroomerID();
                }
            }
        };
    }

    /***** SAVE THE GROOMER ACCOUNT ID *****/
    private void saveGroomerID() {
        GroomersAPI apiInterface = ZenApiClient.getClient().create(GroomersAPI.class);
        Call<Groomer> call = apiInterface.fetchGroomerID(user.getUid());
        call.enqueue(new Callback<Groomer>() {
            @Override
            public void onResponse(Call<Groomer> call, Response<Groomer> response) {
//                Log.e("GROOMER RESPONSE", String.valueOf(response.raw()));
                /* GET THE GROOMER ACCOUNT ID */
                GROOMER_ID = response.body().getGroomerID();
                if (GROOMER_ID != null)    {
                    /* SET GROOMER ACCOUNT ID TO THE APP'S PRIVATE SHARED PREFERENCES */
                    getApp().setGroomerID(GROOMER_ID);

                    /* SHOW THE LANDING ACTIVITY */
                    Intent showLanding = new Intent(LoginActivity.this, LandingActivity.class);
                    showLanding.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(showLanding);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Groomer> call, Throwable t) {
//                Log.e("GROOMER FAILURE", t.getMessage());
            }
        });
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