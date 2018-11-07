package co.zenpets.groomers.landing;

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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import co.zenpets.groomers.R;
import co.zenpets.groomers.landing.modules.DashboardFragment;
import co.zenpets.groomers.landing.modules.EnquiriesFragment;
import co.zenpets.groomers.landing.modules.ProfileFragment;
import co.zenpets.groomers.landing.modules.ReportsFragment;
import co.zenpets.groomers.landing.modules.ReviewsFragment;
import co.zenpets.groomers.landing.modules.others.FeedbackFragment;
import co.zenpets.groomers.landing.modules.others.HelpFragment;
import co.zenpets.groomers.landing.modules.others.SettingsFragment;
import co.zenpets.groomers.utils.AppPrefs;
import co.zenpets.groomers.utils.helpers.ZenApiClient;
import co.zenpets.groomers.utils.models.groomers.Groomer;
import co.zenpets.groomers.utils.models.groomers.GroomersAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LandingActivity extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN GROOMER ACCOUNT ID AND OTHER DETAILS **/
    private String GROOMER_ID = null;
    private String GROOMER_NAME = null;
    private String GROOMER_LOGO = null;

    /** A FRAGMENT INSTANCE **/
    private Fragment mContent = new ReportsFragment();

    /** DECLARE THE LAYOUT ELEMENTS **/
    private ActionBarDrawerToggle mDrawerToggle;
    private ImageView imgvwGroomerLogo;
    private TextView txtGroomerName;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.drawerLayout) DrawerLayout drawerLayout;
    @BindView(R.id.navigationView) NavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_activity);
        ButterKnife.bind(this);

        /* GET THE GROOMER ACCOUNT ID */
        GROOMER_ID = getApp().getGroomerID();

        /* CONFIGURE THE TOOLBAR */
        configToolbar();

        /* CONFIGURE THE NAVIGATION BAR */
        configureNavBar();

        /* GET THE USER DETAILS */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            /* FETCH THE GROOMER'S PROFILE */
            fetchGroomerProfile();
        }

        /* SHOW THE FIRST FRAGMENT (DASHBOARD) */
        if (savedInstanceState == null) {
            Fragment mContent = new DashboardFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame, mContent, "KEY_FRAG")
                    .commit();
        }

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(
                LandingActivity.this,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String token = instanceIdResult.getToken();
                        Log.e("NEW TOKEN", token);

                        /* UPDATE THE GROOMER'S DEVICE TOKEN */
                        updateDeviceToken(token);
                    }
                });
    }

    /** FETCH THE GROOMER'S PROFILE **/
    private void fetchGroomerProfile() {
        GroomersAPI api = ZenApiClient.getClient().create(GroomersAPI.class);
        Call<Groomer> call = api.fetchGroomerProfile(GROOMER_ID);
        call.enqueue(new Callback<Groomer>() {
            @Override
            public void onResponse(Call<Groomer> call, Response<Groomer> response) {
//                Log.e("PROFILE RESPONSE", String.valueOf(response.raw()));

                /* GET AND SET THE GROOMER'S NAME */
                GROOMER_NAME = response.body().getGroomerName();
                if (GROOMER_NAME != null)    {
                    txtGroomerName.setText(GROOMER_NAME);
                }

                /* GET THE DOCTOR'S DISPLAY PROFILE */
                GROOMER_LOGO = response.body().getGroomerLogo();
                if (GROOMER_LOGO != null) {
                    Picasso.with(LandingActivity.this)
                            .load(GROOMER_LOGO)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .fit()
                            .into(imgvwGroomerLogo, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onError() {
                                    Picasso.with(LandingActivity.this)
                                            .load(GROOMER_LOGO)
                                            .into(imgvwGroomerLogo);
                                }
                            });
                }
            }

            @Override
            public void onFailure(Call<Groomer> call, Throwable t) {
            }
        });
    }

    /** CONFIGURE THE NAVIGATION BAR **/
    private void configureNavBar() {
        /* INITIALIZE THE NAVIGATION VIEW */
        navigationView = findViewById(R.id.navigationView);
        View view = navigationView.getHeaderView(0);
        imgvwGroomerLogo = view.findViewById(R.id.imgvwGroomerLogo);
        txtGroomerName = view.findViewById(R.id.txtGroomerName);

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
                    case R.id.dashReviews:
                        mContent = new ReviewsFragment();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashEnquiries:
                        mContent = new EnquiriesFragment();
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
        drawerLayout.closeDrawer(GravityCompat.START);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, fragment)
                .commit();
    }

    /** UPDATE THE GROOMER'S DEVICE TOKEN **/
    private void updateDeviceToken(String deviceToken) {
        GroomersAPI api = ZenApiClient.getClient().create(GroomersAPI.class);
        Call<Groomer> call = api.updateGroomerToken(GROOMER_ID, deviceToken);
        call.enqueue(new Callback<Groomer>() {
            @Override
            public void onResponse(Call<Groomer> call, Response<Groomer> response) {
            }

            @Override
            public void onFailure(Call<Groomer> call, Throwable t) {
//                Crashlytics.logException(t);
            }
        });
    }
}