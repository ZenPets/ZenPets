package biz.zenpets.users;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import biz.zenpets.users.creator.profile.ProfileEditor;
import biz.zenpets.users.landing.LandingActivity;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.user.UserData;
import biz.zenpets.users.utils.models.user.UsersAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** CAST THE LAYOUT ELEMENT/S **/
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
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null)   {
                    /* FETCH THE USER'S PROFILE */
                    fetchProfile(user.getUid());
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
                    getApp().setProfileStatus(profileComplete);
//                    Log.e("STATUS", profileComplete);

                    /* CHECK THE PROFILE COMPLETE STATUS */
                    if (profileComplete.equalsIgnoreCase("Complete"))   {
                        Intent showLanding = new Intent(SplashScreen.this, LandingActivity.class);
                        showLanding.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(showLanding);
                        finish();
                    } else if (profileComplete.equalsIgnoreCase("Incomplete"))  {
                        Intent intent = new Intent(SplashScreen.this, ProfileEditor.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
//                Log.e("USER PROFILE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }
}