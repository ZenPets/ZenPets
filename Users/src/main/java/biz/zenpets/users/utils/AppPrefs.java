package biz.zenpets.users.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;

import com.facebook.appevents.AppEventsLogger;
import com.facebook.drawee.backends.pipeline.Fresco;

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

    /** THE CURRENT CITY ID AND NAME **/
    private final String CITY_ID = "cityID";
    private final String CITY_NAME = "cityName";

    /** THE CURRENT LOCALITY ID AND NAME **/
    private final String LOCALITY_ID = "localityID";
    private final String LOCALITY_NAME = "localityName";

    /** THE CURRENT LATITUDE AND LONGITUDE **/
    String LATITUDE = "latitude";
    String LONGITUDE = "longitude";

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
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo("biz.zenpets.users", PackageManager.GET_SIGNATURES);
//            for (Signature signature: info.signatures)	{
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.e("FACEBOOK APP SIGNATURE", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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

    /** SET THE CITY DETAILS **/
    public void setCityDetails(String cityID, String cityName) {
        final SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(CITY_ID, cityID);
        edit.putString(CITY_NAME, cityName);
        edit.apply();
    }

    /** GET THE CITY DETAILS **/
    public String[] getCityDetails()	{
        String cityID = mPreferences.getString(CITY_ID, null);
        String cityName = mPreferences.getString(CITY_NAME, null);

        return new String[]	{cityID, cityName};
    }

    /** SET THE LOCALITY DETAILS **/
    public void setLocalityDetails(String localityID, String localityName) {
        final SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(LOCALITY_ID, localityID);
        edit.putString(LOCALITY_NAME, localityName);
        edit.apply();
    }

    /** GET THE LOCALITY DETAILS **/
    public String[] getLocalityDetails()	{
        String localityID = mPreferences.getString(LOCALITY_ID, null);
        String localityName = mPreferences.getString(LOCALITY_NAME, null);

        return new String[]	{localityID, localityName};
    }

    /** SET THE USER'S ORIGIN LATITUDE **/
    public void setOriginLatitude(String originLatLng) {
        final SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(LATITUDE, originLatLng);
        edit.apply();
    }

    /** GET THE USER'S ORIGIN LATITUDE **/
    public String getOriginLatitude()	{
        return mPreferences.getString(LATITUDE, null);
    }

    /** SET THE USER'S ORIGIN LONGITUDE **/
    public void setOriginLongitude(String longitude) {
        final SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(LONGITUDE, longitude);
        edit.apply();
    }

    /** GET THE USER'S ORIGIN LONGITUDE **/
    public String getOriginLongitude()	{
        return mPreferences.getString(LONGITUDE, null);
    }

    /***** A METHOD TO PROVIDE A GLOBAL CONTEXT *****/
    public static Context context() {
        return app.getApplicationContext();
    }
}