package biz.zenpets.users.dashboard;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import biz.zenpets.users.R;
import biz.zenpets.users.dashboard.modules.DashboardFragment;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.user.UserData;
import biz.zenpets.users.utils.models.user.UsersAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewLandingActivity extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN USER'S EMAIL ADDRESS **/
    String USER_EMAIL = null;

    /** THE LOGGED IN USER'S DETAILS **/
    String USER_ID = null;
    String USER_NAME = null;
    String USER_DISPLAY_PROFILE = null;

    /** A FRAGMENT INSTANCE **/
    private Fragment mContent;

    /** DECLARE THE LAYOUT ELEMENTS **/
    ActionBarDrawerToggle mDrawerToggle;
    CircleImageView imgvwUserProfile;
    AppCompatTextView txtUserName;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.drawerLayout) DrawerLayout drawerLayout;
    @BindView(R.id.navigationView) NavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_landing_activity);
        ButterKnife.bind(this);

        /* CONFIGURE THE TOOLBAR */
        configToolbar();

        /* CONFIGURE THE NAVIGATION BAR */
        configureNavBar();

        /* SHOW THE FIRST FRAGMENT (DASHBOARD) */
        if (savedInstanceState == null) {
            Fragment mContent = new DashboardFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame, mContent, "KEY_FRAG")
                    .commit();
        }

        /* DETERMINE IF THE USER IS LOGGED IN */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            /* FETCH THE USER'S PROFILE */
            fetchUserProfile(user.getUid());
        }
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
//                    case R.id.dashProfile:
//                        mContent = new ProfileFragment();
//                        switchFragment(mContent);
//                        return true;
//                    case R.id.dashDoctors:
//                        mContent = new DoctorsFragment();
//                        switchFragment(mContent);
//                        return true;
//                    case R.id.dashPromote:
//                        mContent = new PromoteFragment();
//                        switchFragment(mContent);
//                        return true;
//                    case R.id.dashConsult:
//                        mContent = new ConsultationsFragment();
//                        switchFragment(mContent);
//                        return true;
//                    case R.id.dashFeedback:
//                        mContent = new FeedbackFragment();
//                        switchFragment(mContent);
//                        return true;
//                    case R.id.dashHealthTips:
//                        mContent = new HealthTipsFragment();
//                        switchFragment(mContent);
//                        return true;
//                    case R.id.dashCalendar:
//                        mContent = new CalendarFragment();
//                        switchFragment(mContent);
//                        return true;
//                    case R.id.dashPatients:
//                        mContent = new PatientsFragment();
//                        switchFragment(mContent);
//                        return true;
//                    case R.id.dashReports:
//                        mContent = new ReportsFragment();
//                        switchFragment(mContent);
//                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });
    }

    /***** FETCH THE USER'S PROFILE *****/
    private void fetchUserProfile(String userAuthID) {
        UsersAPI api = ZenApiClient.getClient().create(UsersAPI.class);
        Call<UserData> call = api.fetchProfile(userAuthID);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                /* GET THE USER'S ID */
                UserData data = response.body();
                if (data != null)   {
                    String userID = data.getUserID();
//                    Log.e("USER ID", userID);
                    getApp().setUserID(userID);

                    /* FIREBASE MESSAGING TEST */
                    FirebaseMessaging.getInstance().subscribeToTopic("appointments");
                    String token = FirebaseInstanceId.getInstance().getToken();
//                    Log.e("TOKEN", token);

                    /* UPDATE THE DOCTOR'S DEVICE TOKEN */
                    updateDeviceToken(token);
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
//                Log.e("USER PROFILE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** UPDATE THE USER'S DEVICE TOKEN *****/
    private void updateDeviceToken(String deviceToken) {
        String USER_ID = getApp().getUserID();
        UsersAPI api = ZenApiClient.getClient().create(UsersAPI.class);
        Call<UserData> call = api.updateUserToken(USER_ID, deviceToken);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                Crashlytics.logException(t);
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

    /** METHOD TO CHANGE THE FRAGMENT **/
    private void switchFragment(Fragment fragment) {

        /* HIDE THE NAV DRAWER */
        drawerLayout.closeDrawer(GravityCompat.START);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, fragment)
                .addToBackStack(null)
                .commit();
    }
}