package co.zenpets.kennels.utils.models.enquiries;

import com.google.gson.annotations.SerializedName;

public class Enquiry {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("kennelEnquiryID") private String kennelEnquiryID;
    @SerializedName("userID") private String userID;
    @SerializedName("userName") private String userName;
    @SerializedName("userDisplayProfile") private String userDisplayProfile;
    @SerializedName("kennelID") private String kennelID;
    @SerializedName("kennelName") private String kennelName;
    @SerializedName("kennelEnquiryMessage") private String kennelEnquiryMessage;
    @SerializedName("kennelEnquiryRead") private String kennelEnquiryRead;
    @SerializedName("kennelEnquiryTimestamp") private String kennelEnquiryTimestamp;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKennelEnquiryID() {
        return kennelEnquiryID;
    }

    public void setKennelEnquiryID(String kennelEnquiryID) {
        this.kennelEnquiryID = kennelEnquiryID;
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

    public String getKennelID() {
        return kennelID;
    }

    public void setKennelID(String kennelID) {
        this.kennelID = kennelID;
    }

    public String getKennelName() {
        return kennelName;
    }

    public void setKennelName(String kennelName) {
        this.kennelName = kennelName;
    }

    public String getKennelEnquiryMessage() {
        return kennelEnquiryMessage;
    }

    public void setKennelEnquiryMessage(String kennelEnquiryMessage) {
        this.kennelEnquiryMessage = kennelEnquiryMessage;
    }

    public String getKennelEnquiryRead() {
        return kennelEnquiryRead;
    }

    public void setKennelEnquiryRead(String kennelEnquiryRead) {
        this.kennelEnquiryRead = kennelEnquiryRead;
    }

    public String getKennelEnquiryTimestamp() {
        return kennelEnquiryTimestamp;
    }

    public void setKennelEnquiryTimestamp(String kennelEnquiryTimestamp) {
        this.kennelEnquiryTimestamp = kennelEnquiryTimestamp;
    }
}