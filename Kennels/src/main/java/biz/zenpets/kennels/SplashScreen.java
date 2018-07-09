package biz.zenpets.kennels;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import biz.zenpets.kennels.credentials.LoginActivity;
import biz.zenpets.kennels.landing.NewLandingActivity;
import biz.zenpets.kennels.utils.AppPrefs;
import biz.zenpets.kennels.utils.models.account.Account;
import biz.zenpets.kennels.utils.models.account.AccountsAPI;
import biz.zenpets.kennels.utils.models.helpers.ZenApiClient;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** A FIREBASE USER INSTANCE  **/
    private FirebaseUser user;

    /** THE KENNEL OWNER'S ID **/
    private String KENNEL_OWNER_ID = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtAppName) TextView txtAppName;

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
                /* DETERMINE IF THE USER IS LOGGED IN */
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null)   {
                    /* SAVE THE KENNEL OWNER'S ID */
                    saveKennelOwnerID();
                } else {
                    Intent showLogin = new Intent(SplashScreen.this, LoginActivity.class);
                    showLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(showLogin);
                    finish();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
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
//                    Log.e("KENNEL OWNER ID", KENNEL_OWNER_ID);
                    /* SET KENNEL OWNER'S ID TO THE APP'S PRIVATE SHARED PREFERENCES */
                    getApp().setKennelOwnerID(KENNEL_OWNER_ID);

                    /* SHOW THE LANDING ACTIVITY */
                    Intent showLanding = new Intent(SplashScreen.this, NewLandingActivity.class);
                    showLanding.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(showLanding);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
//                Log.e("KENNEL OWNERS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }
}