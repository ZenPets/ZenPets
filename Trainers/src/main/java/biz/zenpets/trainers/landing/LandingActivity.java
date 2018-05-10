package biz.zenpets.trainers.landing;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import biz.zenpets.trainers.R;
import biz.zenpets.trainers.landing.modules.CalendarFragment;
import biz.zenpets.trainers.landing.modules.ClientsFragment;
import biz.zenpets.trainers.landing.modules.DashboardFragment;
import biz.zenpets.trainers.landing.modules.ProfileFragment;
import biz.zenpets.trainers.landing.modules.ReportsFragment;
import biz.zenpets.trainers.landing.modules.ReviewsFragment;
import biz.zenpets.trainers.landing.modules.TrainingModulesFragment;
import biz.zenpets.trainers.landing.modules.others.FeedbackActivity;
import biz.zenpets.trainers.landing.modules.others.HelpFragment;
import biz.zenpets.trainers.landing.modules.others.SettingsActivity;
import biz.zenpets.trainers.utils.AppPrefs;
import biz.zenpets.trainers.utils.helpers.ZenApiClient;
import biz.zenpets.trainers.utils.models.trainers.Trainer;
import biz.zenpets.trainers.utils.models.trainers.TrainersAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LandingActivity extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN TRAINERS'S ID AND OTHER DETAILS **/
    private String TRAINER_ID = null;
    private String TRAINER_NAME = null;
    private String TRAINER_DISPLAY_PROFILE = null;

    /** A FRAGMENT INSTANCE **/
    private Fragment mContent;

    /** DECLARE THE LAYOUT ELEMENTS **/
    private ActionBarDrawerToggle mDrawerToggle;
    private SimpleDraweeView imgvwTrainerDisplayProfile;
    private AppCompatTextView txtTrainerName;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.drawerLayout) DrawerLayout drawerLayout;
    @BindView(R.id.navigationView) NavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_activity);
        ButterKnife.bind(this);

        /* GET THE TRAINER'S ID */
        TRAINER_ID = getApp().getTrainerID();

        /* CONFIGURE THE TOOLBAR */
        configToolbar();

        /* CONFIGURE THE NAVIGATION BAR */
        configureNavBar();

        /* GET THE USER DETAILS */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            /* FETCH THE TRAINER'S PROFILE */
            fetchTrainerProfile();
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
        FirebaseMessaging.getInstance().subscribeToTopic("enquiries");
        String token = FirebaseInstanceId.getInstance().getToken();

        /* UPDATE THE TRAINER'S DEVICE TOKEN */
        updateDeviceToken(token);
    }

    /** CONFIGURE THE NAVIGATION BAR **/
    private void configureNavBar() {
        /* INITIALIZE THE NAVIGATION VIEW */
        navigationView = findViewById(R.id.navigationView);
        View view = navigationView.getHeaderView(0);
        imgvwTrainerDisplayProfile = view.findViewById(R.id.imgvwTrainerDisplayProfile);
        txtTrainerName = view.findViewById(R.id.txtTrainerName);

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
                        mContent = new ProfileFragment();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashModules:
                        mContent = new TrainingModulesFragment();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashReviews:
                        mContent = new ReviewsFragment();
                        switchFragment(mContent);
                        return true;

                    case R.id.dashCalendar:
                        mContent = new CalendarFragment();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashClients:
                        mContent = new ClientsFragment();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashReports:
                        mContent = new ReportsFragment();
                        switchFragment(mContent);
                        return true;

                    case R.id.dashHelp:
                        mContent = new HelpFragment();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashFeedback:
                        Intent intentFeedback = new Intent(LandingActivity.this, FeedbackActivity.class);
                        startActivity(intentFeedback);
                        return true;
                    case R.id.dashSettings:
                        Intent intentSettings = new Intent(LandingActivity.this, SettingsActivity.class);
                        startActivity(intentSettings);
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

    /***** FETCH THE TRAINER'S PROFILE *****/
    private void fetchTrainerProfile() {
        TrainersAPI api = ZenApiClient.getClient().create(TrainersAPI.class);
        Call<Trainer> call = api.fetchTrainerProfile(TRAINER_ID);
        call.enqueue(new Callback<Trainer>() {
            @Override
            public void onResponse(Call<Trainer> call, Response<Trainer> response) {
            }

            @Override
            public void onFailure(Call<Trainer> call, Throwable t) {
            }
        });
    }

    /* UPDATE THE TRAINER'S DEVICE TOKEN */
    private void updateDeviceToken(String deviceToken) {
        TrainersAPI api = ZenApiClient.getClient().create(TrainersAPI.class);
        Call<Trainer> call = api.updateTrainerToken(TRAINER_ID, deviceToken);
        call.enqueue(new Callback<Trainer>() {
            @Override
            public void onResponse(Call<Trainer> call, Response<Trainer> response) {
            }

            @Override
            public void onFailure(Call<Trainer> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }
}