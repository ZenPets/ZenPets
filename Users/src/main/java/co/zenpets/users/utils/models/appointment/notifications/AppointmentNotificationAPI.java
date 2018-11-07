package co.zenpets.users.utils.models.appointment.notifications;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AppointmentNotificationAPI {

    /** SEND A NEW APPOINTMENT NOTIFICATION TO THE VETERINARIAN **/
    @POST("sendVetAppointmentNotification")
    @FormUrlEncoded
    Call<AppointmentNotification> sendVetAppointmentNotification(
            @Field("deviceToken") String deviceToken,
            @Field("notificationTitle") String notificationTitle,
            @Field("notificationMessage") String notificationMessage,
            @Field("notificationReference") String notificationReference,
            @Field("appointmentID") String appointmentID,
            @Field("appointmentDate") String appointmentDate,
            @Field("appointmentTime") String appointmentTime,
            @Field("userID") String userID,
            @Field("userName") String userName,
            @Field("userDisplayProfile") String userDisplayProfile);
}