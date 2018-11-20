package co.zenpets.kennels.landing;

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

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.zenpets.kennels.R;
import co.zenpets.kennels.landing.modules.DashboardFragment;
import co.zenpets.kennels.landing.modules.ReportsFragment;
import co.zenpets.kennels.landing.modules.ReviewsFragment;
import co.zenpets.kennels.landing.newmodules.EnquiriesFragment;
import co.zenpets.kennels.landing.newmodules.ProfileFragment;
import co.zenpets.kennels.landing.others.FeedbackFragment;
import co.zenpets.kennels.landing.others.HelpFragment;
import co.zenpets.kennels.landing.others.SettingsFragment;
import co.zenpets.kennels.utils.AppPrefs;
import co.zenpets.kennels.utils.models.helpers.ZenApiClient;
import co.zenpets.kennels.utils.models.kennels.Kennel;
import co.zenpets.kennels.utils.models.kennels.KennelsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LandingActivity extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN KENNEL ID AND IT'S DETAILS **/
    String KENNEL_ID = null;
    String KENNEL_NAME = null;
    String KENNEL_COVER_PHOTO = null;

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

        /* GET THE KENNEL ID */
        KENNEL_ID = getApp().getKennelID();

        /* CONFIGURE THE TOOLBAR */
        configToolbar();

        /* CONFIGURE THE NAVIGATION BAR */
        configureNavBar();

        /* DETERMINE IF THE KENNEL OWNER IS LOGGED IN */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            /* FETCH THE KENNEL DETAILS */
            fetchKennelDetails();
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

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(
                LandingActivity.this,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String token = instanceIdResult.getToken();
//                        Log.e("NEW TOKEN", token);

                        /* UPDATE THE GROOMER'S DEVICE TOKEN */
                        updateDeviceToken(token);
                    }
                });
    }

    /** FETCH THE KENNEL DETAILS **/
    private void fetchKennelDetails() {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<Kennel> call = api.fetchKennelDetails(KENNEL_ID);
        call.enqueue(new Callback<Kennel>() {
            @Override
            public void onResponse(Call<Kennel> call, Response<Kennel> response) {
//                Log.e("KENNEL RESPONSE", String.valueOf(response.raw()));
                Kennel kennel = response.body();
                if (kennel != null) {
                    /* GET THE KENNEL NAME */
                    KENNEL_NAME = kennel.getKennelName();
                    if (KENNEL_NAME != null)    {
//                        Log.e("KENNEL NAME", KENNEL_NAME);
                        txtKennelOwnerName.setText(KENNEL_NAME);
                    }

                    /* GET THE KENNEL COVER PHOTO */
                    KENNEL_COVER_PHOTO = kennel.getKennelCoverPhoto();
                    if (KENNEL_COVER_PHOTO != null) {
//                        Log.e("KENNEL COVER", KENNEL_COVER_PHOTO);
                        Uri uri = Uri.parse(KENNEL_COVER_PHOTO);
                        imgvwKennelOwnerDisplayProfile.setImageURI(uri);
                    }
                }
            }

            @Override
            public void onFailure(Call<Kennel> call, Throwable t) {
//                Log.e("DETAILS FAILURE", t.getMessage());
            }
        });
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
//                    case R.id.dashKennels:
//                        mContent = new KennelsFragment();
//                        switchFragment(mContent);
//                        return true;
                    case R.id.dashReviews:
                        mContent = new ReviewsFragment();
                        switchFragment(mContent);
                        return true;

                    case R.id.dashEnquiries:
                        mContent = new EnquiriesFragment();
                        switchFragment(mContent);
                        return true;
//                    case R.id.dashClients:
//                        mContent = new ClientsFragment();
//                        switchFragment(mContent);
//                        return true;
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

    /* UPDATE THE TRAINER'S DEVICE TOKEN */
    private void updateDeviceToken(String deviceToken) {
        String KENNEL_ID = getApp().getKennelID();
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<Kennel> call = api.updateKennelToken(KENNEL_ID, deviceToken);
        call.enqueue(new Callback<Kennel>() {
            @Override
            public void onResponse(Call<Kennel> call, Response<Kennel> response) {
            }

            @Override
            public void onFailure(Call<Kennel> call, Throwable t) {
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