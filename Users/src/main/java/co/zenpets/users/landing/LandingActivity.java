package co.zenpets.users.landing;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.zenpets.users.R;
import co.zenpets.users.landing.modules.HomeFragment;
import co.zenpets.users.landing.modules.ProfileFragment;
import co.zenpets.users.landing.modules.QuestionsFragment;
import co.zenpets.users.utils.AppPrefs;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.helpers.connectivity.ConnectivityChecker;
import co.zenpets.users.utils.helpers.connectivity.ConnectivityInterface;
import co.zenpets.users.utils.models.user.UserData;
import co.zenpets.users.utils.models.user.UsersAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LandingActivity extends AppCompatActivity implements ConnectivityInterface {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.bottomNavigation) BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_activity);
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

        /* DETERMINE IF THE USER IS LOGGED IN */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            /* FETCH THE USER'S PROFILE */
            fetchUserProfile(user.getEmail());
        }

        /* CREATE THE NOTIFICATION CHANNEL */
        createNotificationChannel();

        /* CHECK FOR INTERNET CONNECTIVITY */
        new ConnectivityChecker(LandingActivity.this).execute();
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

    /***** SELECT A FRAGMENT  *****/
    private void selectFragment(MenuItem item) {
        item.setChecked(true);

        switch (item.getItemId())   {
            case R.id.menuHomeFragment:
                switchFragment(new HomeFragment());
                break;
            case R.id.menuQuestionFragment:
                switchFragment(new QuestionsFragment());
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

    /****** CREATE THE NOTIFICATION CHANNEL *****/
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.default_notification_channel_name);
            String description = getString(R.string.default_notification_channel_desc);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            Log.e("CHANNEL ID", AppPrefs.zenChannelID());
            NotificationChannel channel = new NotificationChannel(AppPrefs.zenChannelID(), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void checkConnectivity(Boolean result) {
        if (result) {
            Log.e("STATUS", "Internet is available...");
//            Toast.makeText(
//                    getApplicationContext(),
//                    "Internet is available...",
//                    Toast.LENGTH_SHORT).show();
        } else {
            Log.e("STATUS", "Internet unavailable!!!");
//            Toast.makeText(
//                    getApplicationContext(),
//                    "Internet unavailable!!!",
//                    Toast.LENGTH_SHORT).show();
        }
    }
}