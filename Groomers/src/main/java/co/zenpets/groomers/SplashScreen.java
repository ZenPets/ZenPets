package co.zenpets.groomers;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import co.zenpets.groomers.credentials.LoginActivity;
import co.zenpets.groomers.landing.LandingActivity;
import co.zenpets.groomers.utils.AppPrefs;
import co.zenpets.groomers.utils.helpers.ZenApiClient;
import co.zenpets.groomers.utils.models.groomers.Groomer;
import co.zenpets.groomers.utils.models.groomers.GroomersAPI;
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

    /** THE GROOMER'S ACCOUNT ID **/
    private String GROOMER_ID = null;

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
                /* DETERMINE IF THE USER IS LOGGED IN */
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null)   {
                    /* SAVE THE GROOMER ACCOUNT ID */
                    saveGroomerID();
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

    /** SAVE THE GROOMER ACCOUNT ID **/
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
                    Intent showLanding = new Intent(SplashScreen.this, LandingActivity.class);
                    showLanding.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(showLanding);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Groomer> call, Throwable t) {
//                Log.e("GROOMER FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }
}