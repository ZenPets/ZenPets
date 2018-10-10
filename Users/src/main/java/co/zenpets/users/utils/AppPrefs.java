package co.zenpets.users.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;

import com.facebook.appevents.AppEventsLogger;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.security.MessageDigest;

public class AppPrefs extends MultiDexApplication {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    /***** SHARED PREFERENCES INSTANCE *****/
    private SharedPreferences mPreferences;

    /** THE USER ID **/
    private final String USER_ID = "userID";

    /** THE PROFILE STATUS **/
    private final String PROFILE_STATUS = "profileStatus";

    /** THE DEVICE TOKEN **/
    private final String DEVICE_TOKEN = "deviceToken";

    /** THE NOTIFICATION CHANNEL ID **/
    private final String NOTIFICATION_CHANNEL_ID = "ZEN_CHANNEL_29081980";

    /** THE APPLICATION INSTANCE **/
    private static AppPrefs app;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        /* INSTANTIATE THE APP INSTANCE */
        app = this;

        /* MULTIDEX */
        MultiDex.install(this);

        /* INSTANTIATE THE FACEBOOK SDK **/
        AppEventsLogger.activateApp(this);

        /* INSTANTIATE THE PREFERENCE MANAGER */
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        /* INSTANTIATE THE FRESCO LIBRARY */
        Fresco.initialize(this);

        /* GET THE "SHA" FOR FACEBOOK LOGIN */
        try {
            PackageInfo info = getPackageManager().getPackageInfo("co.zenpets.users", PackageManager.GET_SIGNATURES);
            for (Signature signature: info.signatures)	{
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
//                Log.e("FACEBOOK APP SIGNATURE", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***** SET THE LOGGED IN USER'S ID *****/
    public void setUserID(String strUserID) {
        final SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(USER_ID, strUserID);
        edit.apply();
    }

    /***** GET THE LOGGED IN USERS'S ID *****/
    public String getUserID()	{
        return mPreferences.getString(USER_ID, null);
    }

    /***** SET THE USER'S PROFILE STATUS *****/
    public void setProfileStatus(String profileStatus) {
        final SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(PROFILE_STATUS, profileStatus);
        edit.apply();
    }

    /***** GET THE USERS'S PROFILE STATUS *****/
    public String getProfileStatus()	{
        return mPreferences.getString(PROFILE_STATUS, null);
    }

    /***** SET THE DEVICE TOKEN *****/
    public void setDeviceToken(String deviceToken) {
        final SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(DEVICE_TOKEN, deviceToken);
        edit.apply();
    }

    /***** GET THE DEVICE TOKEN *****/
    public String getDeviceToken()	{
        return mPreferences.getString(DEVICE_TOKEN, null);
    }

    /***** A METHOD TO RETURN THE NOTIFICATION CHANNEL ID *****/
    public static String zenChannelID()   {
        return app.NOTIFICATION_CHANNEL_ID;
    }

    /***** A METHOD TO PROVIDE A GLOBAL CONTEXT *****/
    public static Context context() {
        return app.getApplicationContext();
    }
}