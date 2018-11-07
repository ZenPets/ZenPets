package co.zenpets.groomers.utils.services;

//public class MyFirebaseMessagingService extends FirebaseMessagingService {
//
//    /** A TAG INSTANCE FOR LOGGING **/
//    private static final String TAG = "MyFirebaseMsgService";
//
//    /** A NOTIFICATION UTILS CLASS INSTANCE **/
//    private NotificationUtils notificationUtils;
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//
//        if (remoteMessage == null)
//            return;
//
//        /* CHECK IF THE MESSAGE CONTAINS A PAYLOAD */
//        if (remoteMessage.getNotification() != null) {
//            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
//            handleNotification(remoteMessage.getNotification().getBody());
//        }
//
//        /* CHECK IF THE MESSAGE CONTAINS A DATA PAYLOAD */
//        if (remoteMessage.getData().size() > 0) {
//            try {
//                JSONObject json = new JSONObject(remoteMessage.getData().toString());
//                Log.e("JSON", String.valueOf(json));
//                handleDataMessage(json);
//            } catch (Exception e) {
////                Log.e(TAG, "Exception: " + e.getMessage());
////                Crashlytics.logException(e);
//            }
//        }
//    }
//
//    /** HANDLE THE NOTIFICATION **/
//    private void handleNotification(String message) {
////        Log.e("MESSAGE", message);
//        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//            /* APP IS IN THE FOREGROUND, BROADCAST THE MESSAGE */
//            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
//            pushNotification.putExtra("message", message);
//            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//            /* PLAY THE NOTIFICATION SOUND */
//            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext(), AppPrefs.zenChannelID());
//            notificationUtils.playNotificationSound();
//        }
//    }
//
//    /** HANDLE THE DATA MESSAGE **/
//    private void handleDataMessage(JSONObject json) {
//        try {
//            JSONObject data = json.getJSONObject("data");
//            Log.e("DATA", String.valueOf(data));
//            String notificationTitle = data.getString("notificationTitle");
//            String notificationMessage = data.getString("notificationMessage");
//            JSONObject payload = data.getJSONObject("payload");
//
//            String strReference = null;
//
//            /* ENQUIRY STRINGS */
//            String strEnquiryID = null;
//            String strUserID = null;
//
//            /* REVIEW STRINGS */
//            String strReviewID = null;
//            if (payload.has("notificationReference")) {
//                strReference = payload.getString("notificationReference");
//                Log.e("REFERENCE", strReference);
//                if (strReference.equalsIgnoreCase("New Review"))   {
//                    strReviewID = payload.getString("reviewID");
//                    Log.e("REVIEW ID", strReviewID);
//                } else if (strReference.equalsIgnoreCase("Groomer Enquiry"))   {
//                    strEnquiryID = payload.getString("enquiryID");
//                    strUserID = payload.getString("userID");
////                    Log.e("ENQUIRY ID", strEnquiryID);
//                }
//            }
//
//            if (strReference.equalsIgnoreCase("New Review"))   {
//                Intent intent = new Intent(getApplicationContext(), ReviewDetails.class);
//                intent.putExtra("REVIEW_ID", strReviewID);
//                showNotificationMessage(getApplicationContext(), notificationTitle, notificationMessage, intent);
//            } else if (strReference.equalsIgnoreCase("Groomer Enquiry"))   {
//                Intent intent = new Intent(getApplicationContext(), EnquiryDetails.class);
//                intent.putExtra("ENQUIRY_ID", strEnquiryID);
//                intent.putExtra("USER_ID", strUserID);
//                showNotificationMessage(getApplicationContext(), notificationTitle, notificationMessage, intent);
//            }
//        } catch (JSONException e) {
////            Log.e(TAG, "Json Exception: " + e.getMessage());
////            Crashlytics.logException(e);
//        } catch (Exception e) {
////            Log.e(TAG, "Exception: " + e.getMessage());
////            Crashlytics.logException(e);
//        }
//    }
//
//    /** SHOW TEXT ONLY NOTIFICATIONS **/
//    private void showNotificationMessage(Context context, String notificationTitle, String message, Intent intent) {
//        notificationUtils = new NotificationUtils(context, AppPrefs.zenChannelID());
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        notificationUtils.showNotificationMessage(notificationTitle, message, intent);
//    }
//}