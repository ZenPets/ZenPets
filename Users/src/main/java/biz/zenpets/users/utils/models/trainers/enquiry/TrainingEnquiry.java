package biz.zenpets.users.utils.models.trainers.enquiry;

import com.google.gson.annotations.SerializedName;

public class TrainingEnquiry {
    @SerializedName("error") private Boolean error;
    @SerializedName("trainingMasterID") private String trainingMasterID;
    @SerializedName("trainerModuleID") private String trainerModuleID;
    @SerializedName("userID") private String userID;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getTrainingMasterID() {
        return trainingMasterID;
    }

    public void setTrainingMasterID(String trainingMasterID) {
        this.trainingMasterID = trainingMasterID;
    }

    public String getTrainerModuleID() {
        return trainerModuleID;
    }

    public void setTrainerModuleID(String trainerModuleID) {
        this.trainerModuleID = trainerModuleID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}