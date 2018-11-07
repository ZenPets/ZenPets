package co.zenpets.doctors.utils;

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

    /** THE DOCTOR'S ID **/
    private final String DOCTOR_ID = "doctorID";

    /** THE DOCTOR'S SUBSCRIPTION LEVEL **/
    private final String DOCTOR_SUBSCRIPTION = "doctorSubscription";

    /** THE DEVICE TOKEN **/
    private final String DEVICE_TOKEN = "deviceToken";

    /** CHECK IF A PROFILE IS BEING CLAIMED **/
    private final String CLAIM_STATUS = "claimStatus";
    private final String CLAIM_APPROVED = "claimApproved";
    private final String CLAIM_ID = "claimID";

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

    /***** SET THE LOGGED IN DOCTOR'S ID *****/
    public void setDoctorID(String strUserID) {
        final SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(DOCTOR_ID, strUserID);
        edit.apply();
    }

    /***** GET THE LOGGED IN DOCTOR'S ID *****/
    public String getDoctorID()	{
        return mPreferences.getString(DOCTOR_ID, null);
    }

    /***** SET THE LOGGED IN DOCTOR'S SUBSCRIPTION STATUS *****/
    public void setSubscription(String strSubscription) {
        final SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(DOCTOR_SUBSCRIPTION, strSubscription);
        edit.apply();
    }

    /***** GET THE LOGGED IN DOCTOR'S SUBSCRIPTION STATUS *****/
    public String getSubscription()	{
        return mPreferences.getString(DOCTOR_SUBSCRIPTION, null);
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

    /***** SET THE CLAIM STATUS *****/
    public void setClaimStatus(String claimStatus) {
        final SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(CLAIM_STATUS, claimStatus);
        edit.apply();
    }

    /***** GET THE CLAIM STATUS *****/
    public String getClaimStatus()	{
        return mPreferences.getString(CLAIM_STATUS, null);
    }

    /***** SET THE CLAIM APPROVED *****/
    public void setClaimApproved(String claimApproved) {
        final SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(CLAIM_APPROVED, claimApproved);
        edit.apply();
    }

    /***** GET THE CLAIM APPROVED *****/
    public String getClaimApproved()	{
        return mPreferences.getString(CLAIM_APPROVED, null);
    }

    /***** SET THE CLAIM ID *****/
    public void setClaimID(String claimID) {
        final SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(CLAIM_ID, claimID);
        edit.apply();
    }

    /***** GET THE CLAIM ID *****/
    public String getClaimID()	{
        return mPreferences.getString(CLAIM_ID, null);
    }

    /***** A METHOD TO PROVIDE A GLOBAL CONTEXT *****/
    public static Context context() {
        return app.getApplicationContext();
    }
}