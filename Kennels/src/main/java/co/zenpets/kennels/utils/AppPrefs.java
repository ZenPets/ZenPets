package co.zenpets.kennels.utils;

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

//    /** THE KENNEL OWNER'S ID **/
//    private String KENNEL_OWNER_ID = null;

    /** THE KENNEL ID **/
    private String KENNEL_ID = null;

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

//    /***** SET THE LOGGED IN KENNEL OWNER'S ID *****/
//    public void setKennelOwnerID(String strUserID) {
//        final SharedPreferences.Editor edit = mPreferences.edit();
//        edit.putString(KENNEL_OWNER_ID, strUserID);
//        edit.apply();
//    }
//
//    /***** GET THE LOGGED IN KENNEL OWNER'S ID *****/
//    public String getKennelOwnerID()	{
//        return mPreferences.getString(KENNEL_OWNER_ID, null);
//    }

    /***** SET THE LOGGED IN KENNEL ID *****/
    public void setKennelID(String strUserID) {
        final SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(KENNEL_ID, strUserID);
        edit.apply();
    }

    /***** GET THE LOGGED IN KENNEL ID *****/
    public String getKennelID()	{
        return mPreferences.getString(KENNEL_ID, null);
    }

    /***** A METHOD TO RETURN THE NOTIFICATION CHANNEL ID *****/
    public static String zenChannelID()   {
        return app.NOTIFICATION_CHANNEL_ID;
    }
}