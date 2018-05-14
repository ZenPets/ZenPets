package biz.zenpets.users.utils.services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import biz.zenpets.users.details.trainers.enquiry.TrainerEnquiryActivity;
import biz.zenpets.users.utils.AppPrefs;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** A TAG INSTANCE FOR LOGGING **/
    private static final String TAG = "MyFirebaseMsgService";

    /** A NOTIFICATION UTILS CLASS INSTANCE **/
    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage == null)
            return;

        /* CHECK IF THE MESSAGE CONTAINS A PAYLOAD */
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        /* CHECK IF THE MESSAGE CONTAINS A DATA PAYLOAD */
        if (remoteMessage.getData().size() > 0) {
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                Log.e("JSON", String.valueOf(json));
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    /** HANDLE THE NOTIFICATION **/
    private void handleNotification(String message) {
        Log.e("MESSAGE", message);
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            /* APP IS IN THE FOREGROUND, BROADCAST THE MESSAGE */
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            /* PLAY THE NOTIFICATION SOUND */
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext(), AppPrefs.zenChannelID());
            notificationUtils.playNotificationSound();
        }
    }

    /** HANDLE THE DATA MESSAGE **/
    private void handleDataMessage(JSONObject json) {
        try {
            JSONObject data = json.getJSONObject("data");
            String notificationTitle = data.getString("notificationTitle");
            String notificationMessage = data.getString("notificationMessage");
            boolean isBackground = data.getBoolean("is_background");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");
            String strReference = null;
            String strTrainerID = null;
            String strModuleID = null;
            if (payload.has("notificationReference")) {
                strReference = payload.getString("notificationReference");
                Log.e("REFERENCE", strReference);
                if (strReference.equalsIgnoreCase("Enquiry"))   {
                    strTrainerID = payload.getString("trainerID");
                    strModuleID = payload.getString("moduleID");
                    Log.e("TRAINER ID", strTrainerID);
                    Log.e("MODULE ID", strModuleID);
                }
            }

            Log.e(TAG, "title: " + notificationTitle);
            Log.e(TAG, "message: " + notificationMessage);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "timestamp: " + timestamp);

            if (strReference.equalsIgnoreCase("Enquiry"))   {
                Intent intent = new Intent(getApplicationContext(), TrainerEnquiryActivity.class);
                intent.putExtra("TRAINER_ID", strTrainerID);
                intent.putExtra("MODULE_ID", strModuleID);
                showNotificationMessage(getApplicationContext(), notificationTitle, notificationMessage, timestamp, intent);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /** SHOW TEXT ONLY NOTIFICATIONS **/
    private void showNotificationMessage(Context context, String notificationTitle, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context, AppPrefs.zenChannelID());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(notificationTitle, message, timeStamp, intent);
    }
}