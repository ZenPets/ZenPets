package co.zenpets.groomers.landing;

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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.zenpets.groomers.R;
import co.zenpets.groomers.landing.newmodules.EnquiriesFragment;
import co.zenpets.groomers.landing.newmodules.HomeFragment;
import co.zenpets.groomers.landing.newmodules.ProfileFragment;
import co.zenpets.groomers.utils.AppPrefs;
import co.zenpets.groomers.utils.helpers.ZenApiClient;
import co.zenpets.groomers.utils.models.groomers.Groomer;
import co.zenpets.groomers.utils.models.groomers.GroomersAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewLandingActivity extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN GROOMER ACCOUNT ID AND OTHER DETAILS **/
    private String GROOMER_ID = null;
    private String GROOMER_NAME = null;
    private String GROOMER_LOGO = null;

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

        /* DETERMINE IF THE GROOMER IS LOGGED IN */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            /* FETCH THE GROOMER'S PROFILE */
            fetchProfile();
        }

        /* CREATE THE NOTIFICATION CHANNEL */
        createNotificationChannel();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(
                NewLandingActivity.this,
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

    /***** FETCH THE KENNEL OWNER'S PROFILE *****/
    private void fetchProfile() {
        GroomersAPI api = ZenApiClient.getClient().create(GroomersAPI.class);
        Call<Groomer> call = api.fetchGroomerProfile(GROOMER_ID);
        call.enqueue(new Callback<Groomer>() {
            @Override
            public void onResponse(Call<Groomer> call, Response<Groomer> response) {
                /* GET THE GROOMER ID */
                Groomer data = response.body();
                if (data != null)   {
                    /* GET THE GROOMER ID */
                    String groomerID = data.getGroomerID();
                    getApp().setGroomerID(groomerID);
                }
            }

            @Override
            public void onFailure(Call<Groomer> call, Throwable t) {
            }
        });
    }

    /* UPDATE THE TRAINER'S DEVICE TOKEN */
    private void updateDeviceToken(String deviceToken) {
        GroomersAPI api = ZenApiClient.getClient().create(GroomersAPI.class);
        Call<Groomer> call = api.updateGroomerToken(GROOMER_ID, deviceToken);
        call.enqueue(new Callback<Groomer>() {
            @Override
            public void onResponse(Call<Groomer> call, Response<Groomer> response) {
            }

            @Override
            public void onFailure(Call<Groomer> call, Throwable t) {
//                Log.e("TOKEN EXCEPTION", t.getMessage());
            }
        });
    }

    /****** CREATE THE NOTIFICATION CHANNEL *****/
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.default_notification_channel_id);
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