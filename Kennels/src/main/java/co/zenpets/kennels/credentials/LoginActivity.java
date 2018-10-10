package co.zenpets.kennels.credentials;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import co.zenpets.kennels.R;
import co.zenpets.kennels.landing.NewLandingActivity;
import co.zenpets.kennels.utils.AppPrefs;
import co.zenpets.kennels.utils.models.account.Account;
import co.zenpets.kennels.utils.models.account.AccountsAPI;
import co.zenpets.kennels.utils.models.helpers.ZenApiClient;
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

    /** THE KENNEL OWNER'S ID **/
    private String KENNEL_OWNER_ID = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtAppName) TextView txtAppName;
    @BindView(R.id.txtAppIdentifier) TextView txtAppIdentifier;
    @BindView(R.id.inputEmailAddress) TextInputLayout inputEmailAddress;
    @BindView(R.id.edtEmailAddress) TextInputEditText edtEmailAddress;
    @BindView(R.id.inputPassword) TextInputLayout inputPassword;
    @BindView(R.id.edtPassword) TextInputEditText edtPassword;

    /** PERFORM THE SIGN IN **/
    @OnClick(R.id.btnSignIn) protected void signIn()  {
        performSignIn();
    }

    /** CREATE A NEW ACCOUNT **/
    @OnClick(R.id.txtCreateAccount) protected void newAccount()   {
        new MaterialDialog.Builder(LoginActivity.this)
                .icon(ContextCompat.getDrawable(LoginActivity.this, R.drawable.ic_info_black_24dp))
                .title(getString(R.string.sign_up_prompt_trainer_title))
                .cancelable(true)
                .content(getString(R.string.sign_up_prompt_trainer_message))
                .positiveText(getString(R.string.sign_up_prompt_trainer_yes))
                .negativeText(getString(R.string.sign_up_prompt_trainer_no))
                .theme(Theme.LIGHT)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                        intent.putExtra("OPTION_CHOICE", true);
                        startActivityForResult(intent, 101);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                        intent.putExtra("OPTION_CHOICE", false);
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
        setContentView(R.layout.login_activity_new);
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
                    /* SAVE THE KENNEL OWNER'S ID */
                    saveKennelOwnerID();
                }
            }
        };
    }

    /***** SAVE THE KENNEL OWNER'S ID *****/
    private void saveKennelOwnerID() {
        AccountsAPI apiInterface = ZenApiClient.getClient().create(AccountsAPI.class);
        Call<Account> call = apiInterface.fetchKennelOwnerID(user.getUid());
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                /* GET THE TRAINERS'S ID */
                KENNEL_OWNER_ID = response.body().getKennelOwnerID();
                if (KENNEL_OWNER_ID != null)    {
                    /* SET KENNEL OWNER'S ID TO THE APP'S PRIVATE SHARED PREFERENCES */
                    getApp().setKennelOwnerID(KENNEL_OWNER_ID);

                    /* SHOW THE LANDING ACTIVITY */
                    Intent showLanding = new Intent(LoginActivity.this, NewLandingActivity.class);
                    showLanding.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(showLanding);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
//                Log.e("KENNEL OWNERS FAILURE", t.getMessage());
            }
        });
    }

    /***** PERFORM THE SIGN IN *****/
    private void performSignIn() {
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

    /** SHOW UNREGISTERED USER DIALOG **/
    private void showUnregisteredUser() {
        new MaterialDialog.Builder(LoginActivity.this)
                .icon(ContextCompat.getDrawable(LoginActivity.this, R.drawable.ic_info_black_24dp))
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
                .icon(ContextCompat.getDrawable(LoginActivity.this, R.drawable.ic_info_black_24dp))
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