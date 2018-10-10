package co.zenpets.users.utils.models.trainers.modules;

import com.google.gson.annotations.SerializedName;

public class ModuleImage {

    @SerializedName("trainerModuleImageID") private String trainerModuleImageID;
    @SerializedName("trainerModuleID") private String trainerModuleID;
    @SerializedName("trainerModuleImageURL") private String trainerModuleImageURL;
    @SerializedName("trainerModuleImageCaption") private String trainerModuleImageCaption;

    public String getTrainerModuleImageID() {
        return trainerModuleImageID;
    }

    public void setTrainerModuleImageID(String trainerModuleImageID) {
        this.trainerModuleImageID = trainerModuleImageID;
    }

    public String getTrainerModuleID() {
        return trainerModuleID;
    }

    public void setTrainerModuleID(String trainerModuleID) {
        this.trainerModuleID = trainerModuleID;
    }

    public String getTrainerModuleImageURL() {
        return trainerModuleImageURL;
    }

    public void setTrainerModuleImageURL(String trainerModuleImageURL) {
        this.trainerModuleImageURL = trainerModuleImageURL;
    }

    public String getTrainerModuleImageCaption() {
        return trainerModuleImageCaption;
    }

    public void setTrainerModuleImageCaption(String trainerModuleImageCaption) {
        this.trainerModuleImageCaption = trainerModuleImageCaption;
    }
}