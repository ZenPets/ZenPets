package co.zenpets.users.utils.models.appointment.notifications;

import com.google.gson.annotations.SerializedName;

public class AppointmentNotification {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("deviceToken") private String deviceToken;
    @SerializedName("notificationTitle") private String notificationTitle;
    @SerializedName("notificationMessage") private String notificationMessage;
    @SerializedName("notificationReference") private String notificationReference;
    @SerializedName("appointmentID") private String appointmentID;
    @SerializedName("userID") private String userID;
    @SerializedName("userName") private String userName;
    @SerializedName("userDisplayProfile") private String userDisplayProfile;
}