package co.zenpets.groomers.utils.models.enquiries;

import com.google.gson.annotations.SerializedName;

public class Enquiry {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("enquiryID") private String enquiryID;
    @SerializedName("userID") private String userID;
    @SerializedName("userName") private String userName;
    @SerializedName("userDisplayProfile") private String userDisplayProfile;
    @SerializedName("groomerID") private String groomerID;
    @SerializedName("groomerName") private String groomerName;
    @SerializedName("enquiryMessage") private String enquiryMessage;
    @SerializedName("enquiryRead") private String enquiryRead;
    @SerializedName("enquiryTimestamp") private String enquiryTimestamp;

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

    public String getEnquiryID() {
        return enquiryID;
    }

    public void setEnquiryID(String enquiryID) {
        this.enquiryID = enquiryID;
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

    public String getGroomerID() {
        return groomerID;
    }

    public void setGroomerID(String groomerID) {
        this.groomerID = groomerID;
    }

    public String getGroomerName() {
        return groomerName;
    }

    public void setGroomerName(String groomerName) {
        this.groomerName = groomerName;
    }

    public String getEnquiryMessage() {
        return enquiryMessage;
    }

    public void setEnquiryMessage(String enquiryMessage) {
        this.enquiryMessage = enquiryMessage;
    }

    public String getEnquiryRead() {
        return enquiryRead;
    }

    public void setEnquiryRead(String enquiryRead) {
        this.enquiryRead = enquiryRead;
    }

    public String getEnquiryTimestamp() {
        return enquiryTimestamp;
    }

    public void setEnquiryTimestamp(String enquiryTimestamp) {
        this.enquiryTimestamp = enquiryTimestamp;
    }
}