package co.zenpets.users.utils.helpers.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.firebase.auth.FirebaseAuth;

import co.zenpets.users.LoginActivity;
import co.zenpets.users.R;
import co.zenpets.users.utils.AppPrefs;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.user.UserData;
import co.zenpets.users.utils.models.user.UsersAPI;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** A FIREBASE AUTH INSTANCE **/
    private FirebaseAuth auth;

    /** SIGN OUT THE USER FROM THE ZEN PETS **/
    @OnClick(R.id.linlaSignOut) void signOut()    {
        new MaterialDialog.Builder(SettingsActivity.this)
                .icon(ContextCompat.getDrawable(SettingsActivity.this, R.drawable.ic_info_outline_black_24dp))
                .title("Sign Out?")
                .cancelable(true)
                .content("Are your sure you want to sign out of Zen Pets? You will stop receiving notification when you sign out. If you have any ongoing Consultations, you will not receive notifications of replies from Veterinarians as well.")
                .positiveText("Yes")
                .negativeText("No")
                .theme(Theme.LIGHT)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        /* UPDATE THE DEVICE TOKEN BY SETTING IT TO NULL */
                        updateDeviceToken();

                        auth.signOut();
                        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        ButterKnife.bind(this);

        /* INSTANTIATE THE FIREBASE AUTH INSTANCE **/
        auth = FirebaseAuth.getInstance();
    }

    /***** UPDATE THE USER'S DEVICE TOKEN *****/
    private void updateDeviceToken() {
        String USER_ID = getApp().getUserID();
        UsersAPI api = ZenApiClient.getClient().create(UsersAPI.class);
        Call<UserData> call = api.updateUserToken(USER_ID, null);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
//                Crashlytics.logException(t);
            }
        });
    }
}