package biz.zenpets.users.utils.services;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import biz.zenpets.users.utils.AppPrefs;

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
    }
}