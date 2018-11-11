package co.zenpets.doctors.utils.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import co.zenpets.doctors.R;
import co.zenpets.doctors.details.appointment.AppointmentDetails;

public class NewFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        /* NOTIFICATION FROM */
        Log.e("FROM", remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.e("PAYLOAD", String.valueOf(remoteMessage.getData()));
        }

        sendNotification(String.valueOf(remoteMessage.getData()));
    }
    private void sendNotification(String messageBody) {
        try {
            JSONObject json = new JSONObject(messageBody);
            JSONObject data = json.getJSONObject("data");
            Log.e("DATA", String.valueOf(data));
            String notificationTitle = data.getString("notificationTitle");
            String notificationMessage = data.getString("notificationMessage");
            JSONObject payload = data.getJSONObject("payload");
            Log.e("PAYLOAD", String.valueOf(payload));

            /* THE REFERENCE */
            String strReference;

            /* THE USER DETAILS */
            String USER_ID = null;
            String USER_NAME;
            String USER_DISPLAY_PROFILE;

            /* THE APPOINTMENT DETAILS */
            String APPOINTMENT_ID;
            String APPOINTMENT_DATE;
            String APPOINTMENT_TIME;

            if (payload.has("notificationReference")) {
                Intent intent;
                strReference = payload.getString("notificationReference");
                Log.e("REFERENCE", strReference);
                if (strReference.equalsIgnoreCase("New Appointment"))   {
                    /* GET THE APPOINTMENT DETAILS */
                    APPOINTMENT_ID = payload.getString("appointmentID");
                    APPOINTMENT_DATE = payload.getString("appointmentDate");
                    APPOINTMENT_TIME = payload.getString("appointmentTime");
                    Log.e("APPOINTMENT ID", APPOINTMENT_ID);
                    Log.e("APPOINTMENT DATE", APPOINTMENT_DATE);
                    Log.e("APPOINTMENT TIME", APPOINTMENT_TIME);
                    USER_ID = payload.getString("userID");
                    USER_NAME = payload.getString("userName");
                    USER_DISPLAY_PROFILE = payload.getString("userDisplayProfile");
                    Bitmap bmpOriginal = Glide
                            .with(this)
                            .asBitmap()
                            .load(USER_DISPLAY_PROFILE)
                            .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();
                    Bitmap bmpDisplayProfile = getCircleBitmap(bmpOriginal);

                    Log.e("DISPLAY PROFILE", USER_DISPLAY_PROFILE);
                    intent = new Intent(this, AppointmentDetails.class);
                    intent.putExtra("APPOINTMENT_ID", APPOINTMENT_ID);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                            PendingIntent.FLAG_ONE_SHOT);

                    String channelId = getString(R.string.default_notification_channel_id);
                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(this, channelId)
                                    .setSmallIcon(R.drawable.zen_pets_notification)
                                    .setContentTitle("New Appointment by " + USER_NAME)
//                                    .setContentText("Appointment set for " + APPOINTMENT_TIME + " on " + APPOINTMENT_DATE)
                                    .setAutoCancel(true)
                                    .setLargeIcon(bmpDisplayProfile)
                                    .setSound(defaultSoundUri)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .bigText("Appointment set for " + APPOINTMENT_TIME + " on " + APPOINTMENT_DATE))
                                    .setContentIntent(pendingIntent);

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    // Since android Oreo notification channel is needed.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(channelId,
                                "Channel human readable title",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }

                    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                }
            }

//            Intent intent = new Intent(this, LandingActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                    PendingIntent.FLAG_ONE_SHOT);
//
//            String channelId = getString(R.string.default_notification_channel_id);
//            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            NotificationCompat.Builder notificationBuilder =
//                    new NotificationCompat.Builder(this, channelId)
//                            .setSmallIcon(R.drawable.zen_pets_notification)
//                            .setContentTitle(getString(R.string.splash_title))
//                            .setContentText(messageBody)
//                            .setAutoCancel(true)
//                            .setSound(defaultSoundUri)
//                            .setContentIntent(pendingIntent);
//
//            NotificationManager notificationManager =
//                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//            // Since android Oreo notification channel is needed.
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                NotificationChannel channel = new NotificationChannel(channelId,
//                        "Channel human readable title",
//                        NotificationManager.IMPORTANCE_DEFAULT);
//                notificationManager.createNotificationChannel(channel);
//            }
//
//            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getCircleBitmap(Bitmap bmpOriginal) {
        final Bitmap output = Bitmap.createBitmap(bmpOriginal.getWidth(),
                bmpOriginal.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bmpOriginal.getWidth(), bmpOriginal.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bmpOriginal, rect, rect, paint);

        return output;
    }
}