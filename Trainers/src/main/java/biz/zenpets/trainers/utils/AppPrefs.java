package biz.zenpets.trainers.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.facebook.drawee.backends.pipeline.Fresco;

public class AppPrefs extends MultiDexApplication {

    /** THE APPLICATION INSTANCE **/
    private static AppPrefs app;

    /***** SHARED PREFERENCES INSTANCE *****/
    private SharedPreferences mPreferences;

    /** THE TRAINER'S ID **/
    private final String TRAINER_ID = "trainerID";

    /** THE TRAINER'S SUBSCRIPTION LEVEL **/
    private final String TRAINER_SUBSCRIPTION = "trainerSubscription";

    /** THE DEVICE TOKEN **/
    private final String DEVICE_TOKEN = "deviceToken";

    /** THE NOTIFICATION CHANNEL ID **/
    private final String NOTIFICATION_CHANNEL_ID = "ZEN_CHANNEL_29081980";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /* INSTANTIATE THE APP INSTANCE */
        app = this;

        /* MULTIDEX */
        MultiDex.install(this);

        /* INSTANTIATE THE PREFERENCE MANAGER */
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        /* INSTANTIATE THE FRESCO LIBRARY */
        Fresco.initialize(this);
    }

    /***** A METHOD TO PROVIDE A GLOBAL CONTEXT *****/
    public static Context context() {
        return app.getApplicationContext();
    }

    /***** SET THE LOGGED IN TRAINER'S ID *****/
    public void setTrainerID(String strUserID) {
        final SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(TRAINER_ID, strUserID);
        edit.apply();
    }

    /***** GET THE LOGGED IN TRAINER'S ID *****/
    public String getTrainerID()	{
        return mPreferences.getString(TRAINER_ID, null);
    }

    /***** SET THE LOGGED IN TRAINER'S SUBSCRIPTION STATUS *****/
    public void setSubscription(String strSubscription) {
        final SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(TRAINER_SUBSCRIPTION, strSubscription);
        edit.apply();
    }

    /***** GET THE LOGGED IN TRAINER'S SUBSCRIPTION STATUS *****/
    public String getSubscription()	{
        return mPreferences.getString(TRAINER_SUBSCRIPTION, null);
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
}