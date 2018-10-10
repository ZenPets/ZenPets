package co.zenpets.kennels.utils.models.notifications;

import com.google.gson.annotations.SerializedName;

public class Notification {
    @SerializedName("deviceToken") private String deviceToken;
    @SerializedName("notificationTitle") private String notificationTitle;
    @SerializedName("notificationMessage") private String notificationMessage;
    @SerializedName("notificationReference") private String notificationReference;
    @SerializedName("kennelID") private String kennelID;
    @SerializedName("kennelEnquiryID") private String kennelEnquiryID;

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

    public String getKennelID() {
        return kennelID;
    }

    public void setKennelID(String kennelID) {
        this.kennelID = kennelID;
    }

    public String getKennelEnquiryID() {
        return kennelEnquiryID;
    }

    public void setKennelEnquiryID(String kennelEnquiryID) {
        this.kennelEnquiryID = kennelEnquiryID;
    }
}