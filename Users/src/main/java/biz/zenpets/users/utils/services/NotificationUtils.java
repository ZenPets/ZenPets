package biz.zenpets.users.utils.services;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import biz.zenpets.users.R;

public class NotificationUtils {

    private static String TAG = NotificationUtils.class.getSimpleName();

    private Context mContext;
    private String CHANNEL_ID;

    public NotificationUtils(Context mContext, String CHANNEL_ID) {
        this.mContext = mContext;
        this.CHANNEL_ID = CHANNEL_ID;
    }

    public void showNotificationMessage(final String title, final String message, final String timeStamp, Intent intent) {
        /* CHECK FOR EMPTY PUSH MESSAGE */
        if (TextUtils.isEmpty(message))
            return;

        /* SET THE NOTIFICATION ICON */
        final int icon = R.drawable.zen_pets_notification_icon;

        /* SET THE INTENT PARAMETERS */
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
        playNotificationSound();
    }

    /** SHOW THE NOTIFICATION WITH THE DEFAULT STYLE **/
    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(message);

        /* CONFIGURE THE NOTIFICATION PARAMETERS */
        NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext, CHANNEL_ID);
        notification.setContentTitle(title);
        notification.setContentText(message);
        notification.setContentIntent(resultPendingIntent);
        notification.setSound(alarmSound);
        notification.setShowWhen(true);
        notification.setWhen(System.currentTimeMillis());
        notification.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.zen_pets_notification_icon));
        notification.setPriority(NotificationCompat.PRIORITY_HIGH);
        notification.setAutoCancel(true);
        notification.setSmallIcon(R.drawable.zen_pets_notification);
//        notification.setSmallIcon(getNotificationIcon());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)  {
//            notification.setSmallIcon(R.drawable.icon_silhouette);
//            notification.setColor(mContext.getResources().getColor(android.R.color.white));
//        } else {
//            notification.setSmallIcon(R.drawable.zen_pets_notification_icon);
//        }

        /* SHOW THE NOTIFICATION */
        NotificationManagerCompat manager = NotificationManagerCompat.from(mContext);
        manager.notify(Config.NOTIFICATION_ID, notification.build());

//        Notification notification;
//        notification = mBuilder.setTicker(title).setWhen(0)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setContentIntent(resultPendingIntent)
//                .setSound(alarmSound)
//                .setShowWhen(true)
//                .setWhen(System.currentTimeMillis())
//                .setSmallIcon(getNotificationIcon())
//                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setAutoCancel(true)
//                .build();

//        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//        assert notificationManager != null;
//        notificationManager.notify(Config.NOTIFICATION_ID, notification);
    }

    /** SET THE ICON BACKGROUND FOR PLATFORMS ABOVE LOLLIPOP (ANDROID 5 AND ABOVE)**/
//    private int getNotificationIcon() {
//        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
//        return useWhiteIcon ? R.drawable.icon_silhouette: R.drawable.zen_pets_notification_icon;
//    }

    /** PLAY THE NOTIFICATION SOUND  **/
    public void playNotificationSound() {
        try {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** CHECK IF THE APP IS THE BACKGROUND **/
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    /** CLEAR THE TRAY NOTIFICATION MESSAGES **/
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    /** GET THE MILLISECONDS **/
    private static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}