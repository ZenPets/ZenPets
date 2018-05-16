package biz.zenpets.trainers;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import biz.zenpets.trainers.credentials.LoginActivity;
import biz.zenpets.trainers.landing.LandingActivity;
import biz.zenpets.trainers.utils.AppPrefs;
import biz.zenpets.trainers.utils.helpers.ZenApiClient;
import biz.zenpets.trainers.utils.models.trainers.Trainer;
import biz.zenpets.trainers.utils.models.trainers.TrainersAPI;
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

    /** THE TRAINER'S ID **/
    private String TRAINER_ID = null;

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
                    /* SAVE THE TRAINER'S ID */
                    saveTrainersID();
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

    /***** SAVE THE TRAINER'S ID *****/
    private void saveTrainersID() {
        TrainersAPI apiInterface = ZenApiClient.getClient().create(TrainersAPI.class);
        Call<Trainer> call = apiInterface.fetchTrainerID(user.getUid());
        call.enqueue(new Callback<Trainer>() {
            @Override
            public void onResponse(Call<Trainer> call, Response<Trainer> response) {
                /* GET THE TRAINERS'S ID */
                TRAINER_ID = response.body().getTrainerID();
                if (TRAINER_ID != null)    {
                    /* SET TRAINER'S ID TO THE APP'S PRIVATE SHARED PREFERENCES */
                    getApp().setTrainerID(TRAINER_ID);

                    /* SHOW THE LANDING ACTIVITY */
                    Intent showLanding = new Intent(SplashScreen.this, LandingActivity.class);
                    showLanding.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(showLanding);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Trainer> call, Throwable t) {
//                Log.e("TRAINER FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }
}