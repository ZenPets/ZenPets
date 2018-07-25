package biz.zenpets.trainers.utils.models.trainers.enquiries;

import com.google.gson.annotations.SerializedName;

public class EnquiryMessage {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("trainingSlaveID") private String trainingSlaveID;
    @SerializedName("trainingMasterID") private String trainingMasterID;
    @SerializedName("trainerID") private String trainerID;
    @SerializedName("trainerName") private String trainerName;
    @SerializedName("trainerDisplayProfile") private String trainerDisplayProfile;
    @SerializedName("trainerToken") private String trainerToken;
    @SerializedName("userID") private String userID;
    @SerializedName("userName") private String userName;
    @SerializedName("userDisplayProfile") private String userDisplayProfile;
    @SerializedName("userToken") private String userToken;
    @SerializedName("trainingSlaveMessage") private String trainingSlaveMessage;
    @SerializedName("trainerSlaveTimestamp") private String trainerSlaveTimestamp;

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

    public String getTrainingSlaveID() {
        return trainingSlaveID;
    }

    public void setTrainingSlaveID(String trainingSlaveID) {
        this.trainingSlaveID = trainingSlaveID;
    }

    public String getTrainingMasterID() {
        return trainingMasterID;
    }

    public void setTrainingMasterID(String trainingMasterID) {
        this.trainingMasterID = trainingMasterID;
    }

    public String getTrainerID() {
        return trainerID;
    }

    public void setTrainerID(String trainerID) {
        this.trainerID = trainerID;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    public String getTrainerDisplayProfile() {
        return trainerDisplayProfile;
    }

    public void setTrainerDisplayProfile(String trainerDisplayProfile) {
        this.trainerDisplayProfile = trainerDisplayProfile;
    }

    public String getTrainerToken() {
        return trainerToken;
    }

    public void setTrainerToken(String trainerToken) {
        this.trainerToken = trainerToken;
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

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getTrainingSlaveMessage() {
        return trainingSlaveMessage;
    }

    public void setTrainingSlaveMessage(String trainingSlaveMessage) {
        this.trainingSlaveMessage = trainingSlaveMessage;
    }

    public String getTrainerSlaveTimestamp() {
        return trainerSlaveTimestamp;
    }

    public void setTrainerSlaveTimestamp(String trainerSlaveTimestamp) {
        this.trainerSlaveTimestamp = trainerSlaveTimestamp;
    }
}