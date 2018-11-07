package co.zenpets.doctors.utils.models.appointments.notifications;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AppointmentNotificationAPI {

    /** SEND AN APPOINTMENT UPDATE NOTIFICATION TO THE USER **/
    @POST("sendUserAppointmentNotification")
    @FormUrlEncoded
    Call<AppointmentNotification> sendUserAppointmentNotification(
            @Field("deviceToken") String deviceToken,
            @Field("notificationTitle") String notificationTitle,
            @Field("notificationMessage") String notificationMessage,
            @Field("notificationReference") String notificationReference,
            @Field("appointmentID") String appointmentID,
            @Field("appointmentDate") String appointmentDate,
            @Field("appointmentTime") String appointmentTime,
            @Field("doctorID") String doctorID,
            @Field("doctorPrefix") String doctorPrefix,
            @Field("doctorName") String doctorName,
            @Field("doctorDisplayProfile") String doctorDisplayProfile);
}