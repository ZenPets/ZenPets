package co.zenpets.users.utils.models.adoptions.notifications;

import com.google.gson.annotations.SerializedName;

public class AdoptionNotification {
    @SerializedName("error") private Boolean error;
    @SerializedName("deviceToken") private String deviceToken;
    @SerializedName("notificationTitle") private String notificationTitle;
    @SerializedName("notificationMessage") private String notificationMessage;
    @SerializedName("notificationReference") private String notificationReference;
    @SerializedName("adoptionID") private String adoptionID;
    @SerializedName("userID") private String userID;
    @SerializedName("userName") private String userName;
    @SerializedName("userDisplayProfile") private String userDisplayProfile;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

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

    public String getAdoptionID() {
        return adoptionID;
    }

    public void setAdoptionID(String adoptionID) {
        this.adoptionID = adoptionID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDisplayProfile() {
        return userDisplayProfile;
    }

    public void setUserDisplayProfile(String userDisplayProfile) {
        this.userDisplayProfile = userDisplayProfile;
    }
}