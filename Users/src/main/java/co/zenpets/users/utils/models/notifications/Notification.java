package co.zenpets.users.utils.models.notifications;

import com.google.gson.annotations.SerializedName;

public class Notification {
    @SerializedName("error") private Boolean error;
    @SerializedName("deviceToken") private String deviceToken;
    @SerializedName("notificationTitle") private String notificationTitle;
    @SerializedName("notificationMessage") private String notificationMessage;
    @SerializedName("notificationReference") private String notificationReference;
    @SerializedName("trainerID") private String trainerID;
    @SerializedName("moduleID") private String moduleID;

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public String getNotificationReference() {
        return notificationReference;
    }

    public void setNotificationReference(String notificationReference) {
        this.notificationReference = notificationReference;
    }

    public String getTrainerID() {
        return trainerID;
    }

    public void setTrainerID(String trainerID) {
        this.trainerID = trainerID;
    }

    public String getModuleID() {
        return moduleID;
    }

    public void setModuleID(String moduleID) {
        this.moduleID = moduleID;
    }
}