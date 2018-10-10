package co.zenpets.trainers.utils.models.trainers.enquiries;

import com.google.gson.annotations.SerializedName;

public class Enquiry {

    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("trainingSlaveID") private String trainingSlaveID;
    @SerializedName("trainingMasterID") private String trainingMasterID;
    @SerializedName("userID") private String userID;
    @SerializedName("userName") private String userName;
    @SerializedName("userDisplayProfile") private String userDisplayProfile;
    @SerializedName("trainerModuleID") private String trainerModuleID;
    @SerializedName("trainerModuleName") private String trainerModuleName;
    @SerializedName("trainerID") private String trainerID;
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

    public String getTrainerModuleID() {
        return trainerModuleID;
    }

    public void setTrainerModuleID(String trainerModuleID) {
        this.trainerModuleID = trainerModuleID;
    }

    public String getTrainerModuleName() {
        return trainerModuleName;
    }

    public void setTrainerModuleName(String trainerModuleName) {
        this.trainerModuleName = trainerModuleName;
    }

    public String getTrainerID() {
        return trainerID;
    }

    public void setTrainerID(String trainerID) {
        this.trainerID = trainerID;
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