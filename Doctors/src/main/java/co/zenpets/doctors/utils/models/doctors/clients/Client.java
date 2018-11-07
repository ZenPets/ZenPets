package co.zenpets.doctors.utils.models.doctors.clients;

import com.google.gson.annotations.SerializedName;

public class Client {

    @SerializedName("clientID") private String clientID;
    @SerializedName("doctorID") private String doctorID;
    @SerializedName("userID") private String userID;
    @SerializedName("userName") private String userName;
    @SerializedName("userDisplayProfile") private String userDisplayProfile;
    @SerializedName("userEmail") private String userEmail;
    @SerializedName("userPhonePrefix") private String userPhonePrefix;
    @SerializedName("userPhoneNumber") private String userPhoneNumber;
    @SerializedName("userGender") private String userGender;
    @SerializedName("clientName") private String clientName;
    @SerializedName("clientPhonePrefix") private String clientPhonePrefix;
    @SerializedName("clientPhoneNumber") private String clientPhoneNumber;

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhonePrefix() {
        return userPhonePrefix;
    }

    public void setUserPhonePrefix(String userPhonePrefix) {
        this.userPhonePrefix = userPhonePrefix;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhonePrefix() {
        return clientPhonePrefix;
    }

    public void setClientPhonePrefix(String clientPhonePrefix) {
        this.clientPhonePrefix = clientPhonePrefix;
    }

    public String getClientPhoneNumber() {
        return clientPhoneNumber;
    }

    public void setClientPhoneNumber(String clientPhoneNumber) {
        this.clientPhoneNumber = clientPhoneNumber;
    }
}