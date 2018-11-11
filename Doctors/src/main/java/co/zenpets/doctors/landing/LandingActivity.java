package co.zenpets.doctors.landing;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import co.zenpets.doctors.R;
import co.zenpets.doctors.landing.modules.CalendarFragment;
import co.zenpets.doctors.landing.modules.ClientsFragment;
import co.zenpets.doctors.landing.modules.ClinicsFragment;
import co.zenpets.doctors.landing.modules.ConsultationsFragment;
import co.zenpets.doctors.landing.modules.DashboardFragment;
import co.zenpets.doctors.landing.modules.NewProfileFragment;
import co.zenpets.doctors.landing.modules.ReviewsFragment;
import co.zenpets.doctors.landing.modules.feedback.FeedbackFragment;
import co.zenpets.doctors.landing.modules.help.HelpFragment;
import co.zenpets.doctors.landing.modules.settings.SettingsFragment;
import co.zenpets.doctors.utils.AppPrefs;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.models.doctors.profile.DoctorProfileAPI;
import co.zenpets.doctors.utils.models.doctors.profile.DoctorProfileData;
import co.zenpets.doctors.utils.models.doctors.subscription.Subscription;
import co.zenpets.doctors.utils.models.doctors.subscription.SubscriptionAPI;
import co.zenpets.doctors.utils.models.doctors.token.TokenAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LandingActivity extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN DOCTOR'S ID AND OTHER DETAILS **/
    private String DOCTOR_ID = null;
    private String DOCTOR_NAME = null;
    private String DOCTOR_DISPLAY_PROFILE = null;

    /** A FRAGMENT INSTANCE **/
    private Fragment mContent;

    /** DECLARE THE LAYOUT ELEMENTS **/
    private ActionBarDrawerToggle mDrawerToggle;
    private SimpleDraweeView imgvwUserProfile;
    private AppCompatTextView txtUserName;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.drawerLayout) DrawerLayout drawerLayout;
    @BindView(R.id.navigationView) NavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_activity);
        ButterKnife.bind(this);

        /* GET THE DOCTOR'S ID */
        DOCTOR_ID = getApp().getDoctorID();

        /* GET AND SET THE DOCTOR'S SUBSCRIPTION STATUS */
        checkSubscriptionStatus();

        /* CONFIGURE THE TOOLBAR */
        configToolbar();

        /* CONFIGURE THE NAVIGATION BAR */
        configureNavBar();

        /* GET THE USER DETAILS */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            /* CHECK IF THE USER HAS VERIFIED */
            if (!user.isEmailVerified())    {
                /* SHOW THE UNVERIFIED DIALOG */
                showVerificationDialog(user);
            }

            /* FETCH THE DOCTOR'S PROFILE */
            fetchDoctorProfile();
        }

        /* SHOW THE FIRST FRAGMENT (DASHBOARD) */
        if (savedInstanceState == null) {
            Fragment mContent = new DashboardFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame, mContent, "KEY_FRAG")
                    .commit();
        }

        /* FIREBASE MESSAGING TEST */
        FirebaseMessaging.getInstance().subscribeToTopic("appointments");
        String token = FirebaseInstanceId.getInstance().getToken();

        /* UPDATE THE DOCTOR'S DEVICE TOKEN */
        updateDeviceToken(token);
    }

    /***** GET AND SET THE DOCTOR'S SUBSCRIPTION STATUS *****/
    private void checkSubscriptionStatus() {
        SubscriptionAPI api = ZenApiClient.getClient().create(SubscriptionAPI.class);
        Call<Subscription> call = api.fetchDoctorSubscription(DOCTOR_ID);
        call.enqueue(new Callback<Subscription>() {
            @Override
            public void onResponse(@NonNull Call<Subscription> call, @NonNull Response<Subscription> response) {
                Subscription subscription = response.body();
                if (subscription != null)   {
                    /* GET THE SUBSCRIPTION ID */
                    String strSubscriptionID = subscription.getSubscriptionID();

                    /* SET THE SUBSCRIPTION ID TO SHARED PREFERENCES */
                    getApp().setSubscription(strSubscriptionID);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Subscription> call, @NonNull Throwable t) {
//                Log.e("SUBSCRIPTION FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /** CONFIGURE THE NAVIGATION BAR **/
    private void configureNavBar() {
        /* INITIALIZE THE NAVIGATION VIEW */
        navigationView = findViewById(R.id.navigationView);
        View view = navigationView.getHeaderView(0);
        imgvwUserProfile = view.findViewById(R.id.imgvwUserProfile);
        txtUserName = view.findViewById(R.id.txtUserName);

        /* CHANGE THE FRAGMENTS ON NAVIGATION ITEM SELECTION */
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                /* CHECK IF AN ITEM IS CHECKED / NOT CHECKED */
                if (menuItem.isChecked())   {
                    menuItem.setChecked(false);
                }  else {
                    menuItem.setChecked(true);
                }

                /* CLOSE THE DRAWER */
                drawerLayout.closeDrawers();

                /* CHECK SELECTED ITEM AND SHOW APPROPRIATE FRAGMENT */
                switch (menuItem.getItemId()){
                    case R.id.dashHome:
                        mContent = new DashboardFragment();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashProfile:
                        mContent = new NewProfileFragment();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashClinics:
                        mContent = new ClinicsFragment();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashConsult:
                        mContent = new ConsultationsFragment();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashReviews:
                        mContent = new ReviewsFragment();
                        switchFragment(mContent);
                        return true;
//                    case R.id.dashHealthTips:
//                        mContent = new HealthTipsFragment();
//                        switchFragment(mContent);
//                        return true;
                    /*case R.id.dashPromote:
                        mContent = new PromoteFragment();
                        switchFragment(mContent);
                        return true;*/

                    case R.id.dashCalendar:
                        mContent = new CalendarFragment();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashClients:
                        mContent = new ClientsFragment();
                        switchFragment(mContent);
                        return true;
//                    case R.id.dashReports:
//                        mContent = new ReportsFragment();
//                        switchFragment(mContent);
//                        return true;

                    case R.id.dashHelp:
                        mContent = new HelpFragment();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashFeedback:
                        mContent = new FeedbackFragment();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashSettings:
                        mContent = new SettingsFragment();
                        switchFragment(mContent);
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configToolbar() {
        Toolbar toolbar = findViewById(R.id.myToolbar);
        if (drawerLayout != null) {
            drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                syncState();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
                syncState();
            }
        };
        drawerLayout.addDrawerListener(mDrawerToggle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())   {
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(navigationView))    {
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    drawerLayout.openDrawer(navigationView);
                }
                return  true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /***** METHOD TO CHANGE THE FRAGMENT *****/
    private void switchFragment(Fragment fragment) {

        /* HIDE THE NAV DRAWER */
        drawerLayout.closeDrawer(GravityCompat.START);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, fragment)
                .commit();
    }

    /***** FETCH THE DOCTOR'S PROFILE *****/
    private void fetchDoctorProfile() {
        DoctorProfileAPI apiInterface = ZenApiClient.getClient().create(DoctorProfileAPI.class);
        Call<DoctorProfileData> call = apiInterface.fetchDoctorProfile(DOCTOR_ID);
        call.enqueue(new Callback<DoctorProfileData>() {
            @Override
            public void onResponse(@NonNull Call<DoctorProfileData> call, @NonNull Response<DoctorProfileData> response) {
                /* GET THE DOCTOR'S NAME */
                DOCTOR_NAME = response.body().getDoctorName();
                if (DOCTOR_NAME != null)    {
                    txtUserName.setText(DOCTOR_NAME);
                }

                /* GET THE DOCTOR'S DISPLAY PROFILE */
                DOCTOR_DISPLAY_PROFILE = response.body().getDoctorDisplayProfile();
                if (DOCTOR_DISPLAY_PROFILE != null) {
                    Uri uri = Uri.parse(DOCTOR_DISPLAY_PROFILE);
                    imgvwUserProfile.setImageURI(uri);
                }
            }

            @Override
            public void onFailure(@NonNull Call<DoctorProfileData> call, @NonNull Throwable t) {
//                Log.e("FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /** SHOW THE VERIFICATION DIALOG **/
    private void showVerificationDialog(final FirebaseUser user) {
        new MaterialDialog.Builder(LandingActivity.this)
                .icon(ContextCompat.getDrawable(LandingActivity.this, R.drawable.ic_info_outline_black_24dp))
                .title(getString(R.string.unverified_title))
                .cancelable(false)
                .content(getString(R.string.unverified_message))
                .positiveText(getString(R.string.unverified_send))
                .negativeText(getString(R.string.unverified_later))
                .theme(Theme.LIGHT)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        user.sendEmailVerification();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    /***** UPDATE THE DOCTOR'S DEVICE TOKEN *****/
    private void updateDeviceToken(String deviceToken) {
        /* GET THE DOCTOR ID */
        DOCTOR_ID = getApp().getDoctorID();
        TokenAPI api = ZenApiClient.getClient().create(TokenAPI.class);
        Call<DoctorProfileData> call = api.updateDoctorToken(DOCTOR_ID, deviceToken);
        call.enqueue(new Callback<DoctorProfileData>() {
            @Override
            public void onResponse(@NonNull Call<DoctorProfileData> call, @NonNull Response<DoctorProfileData> response) {
            }

            @Override
            public void onFailure(@NonNull Call<DoctorProfileData> call, @NonNull Throwable t) {
//                Log.e("FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }
}