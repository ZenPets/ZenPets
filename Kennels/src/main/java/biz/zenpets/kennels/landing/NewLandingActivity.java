package biz.zenpets.kennels.landing;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import biz.zenpets.kennels.R;
import biz.zenpets.kennels.landing.newmodules.EnquiriesFragment;
import biz.zenpets.kennels.landing.newmodules.ProfileFragment;
import biz.zenpets.kennels.landing.newmodules.HomeFragment;
import biz.zenpets.kennels.utils.AppPrefs;
import biz.zenpets.kennels.utils.models.account.Account;
import biz.zenpets.kennels.utils.models.account.AccountsAPI;
import biz.zenpets.kennels.utils.models.helpers.ZenApiClient;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewLandingActivity extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.bottomNavigation) BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_landing_activity);
        ButterKnife.bind(this);

        /* SELECT AND SHOW THE DEFAULT FRAGMENT (HOME FRAGMENT) */
        Menu menu = bottomNavigation.getMenu();
        selectFragment(menu.getItem(0));


        /* CHANGE THE FRAGMENT BASED ON BOTTOM NAV SELECTION */
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return false;
            }
        });

        /* DETERMINE IF THE KENNEL OWNER IS LOGGED IN */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            /* FETCH THE KENNEL OWNER'S PROFILE */
            fetchProfile(user.getUid());
        }

        /* CREATE THE NOTIFICATION CHANNEL */
        createNotificationChannel();
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

                    /* FIREBASE MESSAGING TEST */
                    String token = FirebaseInstanceId.getInstance().getToken();

                    /* UPDATE THE DOCTOR'S DEVICE TOKEN */
                    updateDeviceToken(token);
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

    /***** SELECT A FRAGMENT  *****/
    private void selectFragment(MenuItem item) {
        item.setChecked(true);

        switch (item.getItemId())   {
            case R.id.menuHomeFragment:
                switchFragment(new HomeFragment());
                break;
            case R.id.menuEnquiriesFragment:
                switchFragment(new EnquiriesFragment());
                break;
            case R.id.menuProfileFragment:
                switchFragment(new ProfileFragment());
                break;
            default:
                break;
        }
    }

    /***** METHOD TO CHANGE THE FRAGMENT *****/
    private void switchFragment(Fragment fragment) {
        if (fragment == null)
            return;

        FragmentManager manager = getSupportFragmentManager();
        if (manager != null)    {
            FragmentTransaction transaction = manager.beginTransaction();
            if (transaction != null)    {
                transaction.replace(R.id.mainContent, fragment);
                transaction.commit();
            }
        }
    }
}