package co.zenpets.users.utils.services;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import co.zenpets.users.utils.AppPrefs;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.user.UserData;
import co.zenpets.users.utils.models.user.UsersAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        /* GET THE UPDATED TOKEN */
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        Log.e(TAG, "Refreshed token: " + refreshedToken);

        /* SET THE DEVICE TOKEN TO SHARED PREFS */
        getApp().setDeviceToken(refreshedToken);

        /* UPDATE THE USER'S DEVICE TOKEN */
        updateDeviceToken(refreshedToken);
    }

    /** UPDATE THE USER'S DEVICE TOKEN **/
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
//                Crashlytics.logException(t);
            }
        });
    }
}