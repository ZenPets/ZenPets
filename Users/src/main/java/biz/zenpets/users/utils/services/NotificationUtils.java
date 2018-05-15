package biz.zenpets.users.utils.services;

import android.app.ActivityManager;
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

import java.util.List;

import biz.zenpets.users.R;

public class NotificationUtils {

    private static String TAG = NotificationUtils.class.getSimpleName();

    private Context mContext;
    private String CHANNEL_ID;

    public NotificationUtils(Context mContext, String CHANNEL_ID) {
        this.mContext = mContext;
        this.CHANNEL_ID = CHANNEL_ID;
    }

    public void showNotificationMessage(final String title, final String message,Intent intent) {
        /* CHECK FOR EMPTY PUSH MESSAGE */
        if (TextUtils.isEmpty(message))
            return;

        /* SET THE INTENT PARAMETERS */
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        /* CONFIGURE THE NOTIFICATION SOUND */
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        showSmallNotification(title, message, resultPendingIntent, alarmSound);
        playNotificationSound();
    }

    /** SHOW THE NOTIFICATION WITH THE DEFAULT STYLE **/
    private void showSmallNotification(String title, String message, PendingIntent resultPendingIntent, Uri alarmSound) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)  {
            notification.setSmallIcon(R.drawable.zen_pets_notification);
            notification.setColor(mContext.getResources().getColor(R.color.accent));
        } else {
            notification.setSmallIcon(R.drawable.zen_pets_notification_icon);
        }

        /* SHOW THE NOTIFICATION */
        NotificationManagerCompat manager = NotificationManagerCompat.from(mContext);
        manager.notify(Config.NOTIFICATION_ID, notification.build());
    }

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
}