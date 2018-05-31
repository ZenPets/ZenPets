package biz.zenpets.users;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import biz.zenpets.users.creator.profile.ProfileEditor;
import biz.zenpets.users.landing.LandingActivity;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.helpers.location.classes.FetchCityID;
import biz.zenpets.users.utils.helpers.location.classes.FetchLocalityID;
import biz.zenpets.users.utils.helpers.location.interfaces.FetchCityIDInterface;
import biz.zenpets.users.utils.helpers.location.interfaces.FetchLocalityIDInterface;
import biz.zenpets.users.utils.models.user.UserData;
import biz.zenpets.users.utils.models.user.UsersAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity
        implements FetchCityIDInterface, FetchLocalityIDInterface {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** A FIREBASE USER INSTANCE **/
    FirebaseUser user;

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int ACCESS_FINE_LOCATION_CONSTANT = 200;

    /** A FUSED LOCATION PROVIDER CLIENT INSTANCE**/
    private FusedLocationProviderClient locationProviderClient;

    /** A LOCATION INSTANCE **/
    private Location location;

    /** STRING TO HOLD THE DETECTED CITY AND LOCALITY FOR QUERYING THE ADOPTIONS **/
    private String DETECTED_CITY = null;
    private String FINAL_CITY_ID = null;
    private String DETECTED_LOCALITY = null;
    private String FINAL_LOCALITY_ID = null;

    /** THE LATLNG INSTANCE FOR GETTING THE USER'S CURRENT COORDINATES **/
    private LatLng LATLNG_ORIGIN;

    /** CAST THE LAYOUT ELEMENT/S **/
    @BindView(R.id.txtAppName) AppCompatTextView txtAppName;
    @BindView(R.id.progressLoading) ProgressBar progressLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        ButterKnife.bind(this);

        /* SHOW THE PROGRESS BAR */
        progressLoading.setVisibility(View.VISIBLE);

        /* INSTANTIATE THE LOCATION CLIENT */
        locationProviderClient = LocationServices.getFusedLocationProviderClient(SplashScreen.this);

        /* FETCH THE USER'S LOCATION */
        fetchUsersLocation();

        /* SET THE CUSTOM FONT */
        txtAppName.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Bariol_Light.otf"));

        /* CONFIGURE AND SHOW THE ANIMATION ON THE APP NAME  */
        Animation animIn = new AlphaAnimation(0.0f, 1.0f);
        animIn.setDuration(2000);
        txtAppName.startAnimation(animIn);

//        animIn.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                /* DETERMINE IF THE USER IS LOGGED IN */
//                user = FirebaseAuth.getInstance().getCurrentUser();
//                if (user != null)   {
//
//                    /* SHOW THE PROGRESS BAR */
//                    progressLoading.setVisibility(View.VISIBLE);
//
//                    /* FETCH THE USER'S LOCATION */
//                    fetchUsersLocation();
//                } else {
//                    /* SHOW THE PROGRESS BAR */
//                    progressLoading.setVisibility(View.VISIBLE);
//
//                    /* FETCH THE USER'S LOCATION */
//                    fetchUsersLocation();
//
//                    Intent showLogin = new Intent(SplashScreen.this, LoginActivity.class);
//                    showLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(showLogin);
//                    finish();
//                }
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//        });
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

    /** FETCH THE USER'S LOCATION **/
    private void fetchUsersLocation() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(SplashScreen.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)   {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))    {
                /* SHOW THE DIALOG */
                new MaterialDialog.Builder(this)
                        .icon(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_black_24dp))
                        .title(getString(R.string.location_permission_title))
                        .cancelable(true)
                        .content(getString(R.string.location_permission_message))
                        .positiveText(getString(R.string.permission_grant))
                        .negativeText(getString(R.string.permission_deny))
                        .theme(Theme.LIGHT)
                        .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.cancel();
                                new MaterialDialog.Builder(SplashScreen.this)
                                        .icon(ContextCompat.getDrawable(SplashScreen.this, R.drawable.ic_info_outline_black_24dp))
                                        .title(getString(R.string.doctor_location_denied_title))
                                        .cancelable(true)
                                        .content(getString(R.string.doctor_location_denied_message))
                                        .positiveText(getString(R.string.permission_grant))
                                        .negativeText(getString(R.string.permission_nevermind))
                                        .theme(Theme.LIGHT)
                                        .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.cancel();
                                            }
                                        })
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.cancel();
                                                ActivityCompat.requestPermissions(
                                                        SplashScreen.this,
                                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CONSTANT);
                                            }
                                        }).show();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(
                                        SplashScreen.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CONSTANT);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_FINE_LOCATION_CONSTANT);
            }
        } else {
            locationProviderClient.getLastLocation()
                    .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                location = task.getResult();

                                /* GET THE ORIGIN LATLNG */
                                LATLNG_ORIGIN = new LatLng(location.getLatitude(), location.getLongitude());
//                                Log.e("LAT LNG", String.valueOf(LATLNG_ORIGIN));

                                /* FETCH THE LOCATION USING GEOCODER */
                                fetchTheLocation();
                            } else {
//                                Log.e("EXCEPTION", String.valueOf(task.getException()));
                                Crashlytics.logException(task.getException());
                            }
                        }
                    });
        }
    }

    /***** FETCH THE LOCATION USING GEOCODER *****/
    private void fetchTheLocation() {
        Geocoder geocoder = new Geocoder(SplashScreen.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0)   {
                DETECTED_CITY = addresses.get(0).getLocality();
                DETECTED_LOCALITY = addresses.get(0).getSubLocality();

                if (DETECTED_CITY != null)  {
                    if (!DETECTED_CITY.equalsIgnoreCase("null")) {
                        if (DETECTED_LOCALITY != null)  {
                            if (!DETECTED_LOCALITY.equalsIgnoreCase("null"))   {
                                /* GET THE CITY ID */
                                new FetchCityID(this).execute(DETECTED_CITY);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
//            Log.e("GEOCODER", e.getMessage());
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onCityID(String result) {
        /* GET THE RESULT */
        FINAL_CITY_ID = result;

        /* CHECK FOR A VALID RESULT */
        if (FINAL_CITY_ID != null)   {
            /* GET THE LOCALITY ID */
            new FetchLocalityID(this).execute(DETECTED_LOCALITY);
        }
    }

    @Override
    public void onLocalityID(String result) {
        /* GET THE RESULT */
        FINAL_LOCALITY_ID = result;

        /* CHECK FOR A VALID, NON NULL RESULT */
        if (FINAL_LOCALITY_ID != null)  {
//            Log.e("LOCALITY ID", FINAL_LOCALITY_ID);

            /* SET THE CITY AND LOCALITY ID AND THE ORIGIN LAT LNG TO SHARED PREFS */
            getApp().setCityDetails(FINAL_CITY_ID, DETECTED_CITY);
            getApp().setLocalityDetails(FINAL_LOCALITY_ID, DETECTED_LOCALITY);
            getApp().setOriginLatitude(String.valueOf(LATLNG_ORIGIN.latitude));
            getApp().setOriginLongitude(String.valueOf(LATLNG_ORIGIN.longitude));

            /* DETERMINE IF THE USER IS LOGGED IN */
            user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null)   {
                /* FETCH THE USER'S PROFILE */
                fetchProfile(user.getUid());
            } else {
                /* SHOW THE LOGIN SCREEN */
                Intent showLogin = new Intent(SplashScreen.this, LoginActivity.class);
                showLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(showLogin);
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_FINE_LOCATION_CONSTANT)   {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                /* FETCH THE USER'S LOCATION */
                fetchUsersLocation();
            } else {
                new MaterialDialog.Builder(this)
                        .icon(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_black_24dp))
                        .title(getString(R.string.doctor_location_denied_title))
                        .cancelable(true)
                        .content(getString(R.string.adoption_location_denied_message))
                        .positiveText(getString(R.string.permission_grant))
                        .negativeText(getString(R.string.permission_nevermind))
                        .theme(Theme.LIGHT)
                        .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.cancel();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(
                                        SplashScreen.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CONSTANT);
                            }
                        }).show();
            }
        }
    }
}