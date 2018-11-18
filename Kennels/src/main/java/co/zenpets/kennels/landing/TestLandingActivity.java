package co.zenpets.kennels.landing;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.zenpets.kennels.R;
import co.zenpets.kennels.utils.AppPrefs;
import co.zenpets.kennels.utils.models.helpers.ZenApiClient;
import co.zenpets.kennels.utils.models.kennels.Kennel;
import co.zenpets.kennels.utils.models.kennels.KennelsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestLandingActivity extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN KENNEL ID AND IT'S DETAILS **/
    String KENNEL_ID = null;
    String KENNEL_NAME = null;
    String KENNEL_COVER_PHOTO = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwKennelCoverPhoto) ImageView imgvwKennelCoverPhoto;
    @BindView(R.id.txtKennelName) TextView txtKennelName;
    @BindView(R.id.listEnquiries) RecyclerView listEnquiries;

    /** SHOW THE KENNEL DETAILS **/
    @OnClick(R.id.txtViewKennel) void showKennelDetails()  {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_activity_test);
        ButterKnife.bind(this);

        /* GET THE KENNEL ID */
        KENNEL_ID = getApp().getKennelID();

        /* DETERMINE IF THE KENNEL OWNER IS LOGGED IN */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            /* FETCH THE KENNEL DETAILS */
            fetchKennelDetails();
        }

        /* CREATE THE NOTIFICATION CHANNEL */
        createNotificationChannel();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(
                TestLandingActivity.this,
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
                        txtKennelName.setText(KENNEL_NAME);
                    }

                    /* GET THE KENNEL COVER PHOTO */
                    KENNEL_COVER_PHOTO = kennel.getKennelCoverPhoto();
                    if (KENNEL_COVER_PHOTO != null) {
//                        Log.e("KENNEL COVER", KENNEL_COVER_PHOTO);
                        Uri uri = Uri.parse(KENNEL_COVER_PHOTO);
                        imgvwKennelCoverPhoto.setImageURI(uri);
                    }
                }
            }

            @Override
            public void onFailure(Call<Kennel> call, Throwable t) {
//                Log.e("DETAILS FAILURE", t.getMessage());
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
}