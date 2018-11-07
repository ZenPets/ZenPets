package co.zenpets.users;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import co.zenpets.users.creator.profile.ProfileEditor;
import co.zenpets.users.landing.LandingActivity;
import co.zenpets.users.utils.AppPrefs;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.user.UserData;
import co.zenpets.users.utils.models.user.UserExistsData;
import co.zenpets.users.utils.models.user.UsersAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE FIREBASE AUTH INSTANCE **/
    private FirebaseAuth mAuth;

    /** A FIREBASE USER INSTANCE **/
    private FirebaseUser user;

    /** THE FIREBASE AUTH STATE LISTENER INSTANCE **/
    private FirebaseAuth.AuthStateListener mAuthListener;

    /** A GOOGLE API CLIENT INSTANCE **/
    private GoogleApiClient apiClient;

    /** THE STATIC REQUEST CODE FOR PERFORMING THE GOOGLE SIGN IN **/
    private static final int GOOGLE_SIGN_IN = 101;

    /** THE FACEBOOK CALLBACK MANAGER **/
    private CallbackManager callbackManager;

    /** THE USER DETAILS **/
    private String USER_DISPLAY_NAME = null;
    private String USER_AUTH_ID = null;
    private String USER_EMAIL = null;
    private String USER_PROFILE = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtAppName) AppCompatTextView txtAppName;
    @BindView(R.id.txtLoginMessage) AppCompatTextView txtLoginMessage;
    @BindView(R.id.fbLogin) LoginButton fbLogin;

    @OnClick(R.id.imgvwGoogleSignIn) void googleLogin()   {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    @OnClick(R.id.imgvwFacebookSignIn) void facebookLogin()   {
        fbLogin.performClick();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);

        /* SET THE CUSTOM FONT */
        txtAppName.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Bariol_Light.otf"));
        txtLoginMessage.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf"));

        /* INITIALIZE THE FIREBASE AUTH INSTANCE **/
        mAuth = FirebaseAuth.getInstance();

        /* START THE AUTH STATE LISTENER **/
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    USER_AUTH_ID = user.getUid();
                    USER_DISPLAY_NAME = user.getDisplayName();
                    USER_EMAIL = user.getEmail();
                    if (user.getProviderData().get(1).getProviderId().equalsIgnoreCase("google.com"))   {
                        USER_PROFILE = String.valueOf(user.getProviderData().get(1).getPhotoUrl()) + "?sz=600";
                    } else if (user.getProviderData().get(1).getProviderId().equalsIgnoreCase("facebook.com")){
                        USER_PROFILE = "https://graph.facebook.com/" + user.getProviderData().get(1).getUid() + "/picture?width=4000";
                    }

                    /* CHECK IF THE USER RECORD EXISTS **/
//                    new checkUserExists().execute();
                    checkUserExists(user.getEmail());
                }
            }
        };

        /* CONFIGURE THE GOOGLE SIGN IN OPTIONS **/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        /* CONFIGURE THE GOOGLE API CLIENT INSTANCE **/
        apiClient = new GoogleApiClient.Builder(LoginActivity.this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        /* CONFIGURE FOR THE FACEBOOK LOGIN **/
        callbackManager = CallbackManager.Factory.create();
        fbLogin.setReadPermissions("email", "public_profile");
        fbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
//                Log.e("FB SUCCESS", String.valueOf(loginResult));
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
//                Log.e("FB ERROR", String.valueOf(error.getMessage()));
//                Crashlytics.logException(error);
            }
        });
    }

    /***** CHECK IF THE USER RECORD EXISTS *****/
    private void checkUserExists(String email) {
        UsersAPI api = ZenApiClient.getClient().create(UsersAPI.class);
        Call<UserExistsData> call = api.profileExists(email);
        call.enqueue(new Callback<UserExistsData>() {
            @Override
            public void onResponse(Call<UserExistsData> call, Response<UserExistsData> response) {
//                Log.e("RESPONSE", String.valueOf(response.raw()));
                UserExistsData data = response.body();
                if (data != null)   {
                    String message = data.getMessage();
                    if (message != null)    {
                        if (message.equalsIgnoreCase("User record exists..."))   {
                           /* FETCH THE USER'S PROFILE */
                            fetchProfile(user.getEmail());
                        } else if (message.equalsIgnoreCase("User record doesn't exist..."))    {
                            Intent intent = new Intent(LoginActivity.this, ProfileEditor.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UserExistsData> call, Throwable t) {
//                Log.e("USER EXISTS", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE USER'S PROFILE *****/
    private void fetchProfile(String userAuthID) {
        UsersAPI api = ZenApiClient.getClient().create(UsersAPI.class);
        Call<UserData> call = api.fetchProfile(userAuthID);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                UserData data = response.body();
                if (data != null)   {
                    /* GET THE USER'S ID */
                    String userID = data.getUserID();
                    getApp().setUserID(userID);

                    /* GET THE PROFILE COMPLETE STATUS */
                    String profileComplete = data.getProfileComplete();

                    /* CHECK THE PROFILE COMPLETE STATUS */
                    if (profileComplete.equalsIgnoreCase("Complete"))   {
                        Intent showLanding = new Intent(LoginActivity.this, LandingActivity.class);
                        showLanding.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(showLanding);
                        finish();
                    } else if (profileComplete.equalsIgnoreCase("Incomplete"))  {
                        Intent intent = new Intent(LoginActivity.this, LandingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
//                Log.e("USER PROFILE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN)  {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
//                Log.e("Google Login Status", String.valueOf(result.getStatus()));
                Toast.makeText(getApplicationContext(), "Google sign in failed. Please try again..", Toast.LENGTH_LONG).show();
            }
        } else {
            /* FOR THE FACEBOOK LOGIN **/
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /** HANDLE THE FACEBOOK LOGIN SUCCESS **/
    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())    {
//                            Log.e("FB", String.valueOf(task.getResult()));
                            Toast.makeText(getApplicationContext(), "Facebook login successful", Toast.LENGTH_SHORT).show();
                        } else {
//                            Log.e("FB EXCEPTION", String.valueOf(task.getException()));
//                            Crashlytics.logException(task.getException());
                            Toast.makeText(getApplicationContext(), "Facebook sign in failed. Please try again..", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /** HANDLE THE GOOGLE LOGIN SUCCESS **/
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())    {
                            Toast.makeText(getApplicationContext(), "Google sign in successful", Toast.LENGTH_SHORT).show();
                        } else {
//                            Log.e("Google Login", String.valueOf(task.getException()));
                            Toast.makeText(getApplicationContext(), "Google authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}