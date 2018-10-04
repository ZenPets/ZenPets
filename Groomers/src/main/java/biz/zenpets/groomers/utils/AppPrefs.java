package biz.zenpets.groomers.utils;

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

    /** THE GROOMER ACCOUNT ID **/
    private final String GROOMER_ID = "groomerID";

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

    /***** SET THE LOGGED IN GROOMER ID *****/
    public void setGroomerID(String strUserID) {
        final SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(GROOMER_ID, strUserID);
        edit.apply();
    }

    /***** GET THE LOGGED IN GROOMER ID *****/
    public String getGroomerID()	{
        return mPreferences.getString(GROOMER_ID, null);
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