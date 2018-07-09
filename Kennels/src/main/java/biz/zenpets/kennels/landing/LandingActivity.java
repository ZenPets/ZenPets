package biz.zenpets.kennels.landing;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
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

import biz.zenpets.kennels.R;
import biz.zenpets.kennels.landing.modules.ClientsFragment;
import biz.zenpets.kennels.landing.modules.DashboardFragment;
import biz.zenpets.kennels.landing.newmodules.EnquiriesFragment;
import biz.zenpets.kennels.landing.modules.KennelsFragment;
import biz.zenpets.kennels.landing.newmodules.ProfileFragment;
import biz.zenpets.kennels.landing.modules.ReportsFragment;
import biz.zenpets.kennels.landing.modules.ReviewsFragment;
import biz.zenpets.kennels.landing.others.FeedbackFragment;
import biz.zenpets.kennels.landing.others.HelpFragment;
import biz.zenpets.kennels.landing.others.SettingsFragment;
import biz.zenpets.kennels.utils.AppPrefs;
import biz.zenpets.kennels.utils.models.account.Account;
import biz.zenpets.kennels.utils.models.account.AccountsAPI;
import biz.zenpets.kennels.utils.models.helpers.ZenApiClient;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LandingActivity extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE KENNEL OWNER DETAILS **/
    String KENNEL_OWNER_ID = null;

    /** A FRAGMENT INSTANCE **/
    private Fragment mContent;

    /** DECLARE THE LAYOUT ELEMENTS **/
    private ActionBarDrawerToggle mDrawerToggle;
    private SimpleDraweeView imgvwKennelOwnerDisplayProfile;
    private AppCompatTextView txtKennelOwnerName;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.drawerLayout) DrawerLayout drawerLayout;
    @BindView(R.id.navigationView) NavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_activity);
        ButterKnife.bind(this);

        /* GET THE KENNEL OWNER'S ID */
        KENNEL_OWNER_ID = getApp().getKennelOwnerID();

        /* CONFIGURE THE TOOLBAR */
        configToolbar();

        /* CONFIGURE THE NAVIGATION BAR */
        configureNavBar();

        /* DETERMINE IF THE KENNEL OWNER IS LOGGED IN */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            /* FETCH THE KENNEL OWNER'S PROFILE */
            fetchProfile(user.getUid());
        }

        /* SHOW THE FIRST FRAGMENT (DASHBOARD) */
        if (savedInstanceState == null) {
            Fragment mContent = new DashboardFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame, mContent, "KEY_FRAG")
                    .commit();
        }

        /* CREATE THE NOTIFICATION CHANNEL */
        createNotificationChannel();

        /* FIREBASE MESSAGING TEST */
        String token = FirebaseInstanceId.getInstance().getToken();

        /* UPDATE THE DOCTOR'S DEVICE TOKEN */
        updateDeviceToken(token);
    }

    /** CONFIGURE THE NAVIGATION BAR **/
    private void configureNavBar() {
        /* INITIALIZE THE NAVIGATION VIEW */
        navigationView = findViewById(R.id.navigationView);
        View view = navigationView.getHeaderView(0);
        imgvwKennelOwnerDisplayProfile = view.findViewById(R.id.imgvwKennelOwnerDisplayProfile);
        txtKennelOwnerName = view.findViewById(R.id.txtKennelOwnerName);

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
                    case R.id.dashKennels:
                        mContent = new KennelsFragment();
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

    /***** FETCH THE KENNEL OWNER'S PROFILE *****/
    private void fetchProfile(String kennelOwnerAuthID) {
        AccountsAPI api = ZenApiClient.getClient().create(AccountsAPI.class);
        Call<Account> call = api.fetchKennelOwnerProfileAuthID(kennelOwnerAuthID);
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                /* GET THE USER'S ID */
                Account data = response.body();
                if (data != null)   {
                    /* GET THE KENNEL OWNER'S ID */
                    String kennelOwnerID = data.getKennelOwnerID();
                    getApp().setKennelOwnerID(kennelOwnerID);

                    /* GET THE KENNEL OWNER'S NAME */
                    String kennelOwnerName = data.getKennelOwnerName();
                    txtKennelOwnerName.setText(kennelOwnerName);

                    /* GET AND SET THE KENNEL OWNER'S DISPLAY PROFILE */
                    String kennelOwnerDisplayProfile = data.getKennelOwnerDisplayProfile();
                    if (kennelOwnerDisplayProfile != null) {
                        Uri uri = Uri.parse(kennelOwnerDisplayProfile);
                        imgvwKennelOwnerDisplayProfile.setImageURI(uri);
                    }
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
//                Log.e("KENNEL OWNER PROFILE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /* UPDATE THE TRAINER'S DEVICE TOKEN */
    private void updateDeviceToken(String deviceToken) {
        String KENNEL_OWNER_ID = getApp().getKennelOwnerID();
        AccountsAPI api = ZenApiClient.getClient().create(AccountsAPI.class);
        Call<Account> call = api.updateKennelOwnerToken(KENNEL_OWNER_ID, deviceToken);
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /****** CREATE THE NOTIFICATION CHANNEL *****/
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.default_notification_channel_name);
            String description = getString(R.string.default_notification_channel_desc);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(AppPrefs.zenChannelID(), name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}