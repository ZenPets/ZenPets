
package co.zenpets.users.utils.models.trainers.modules;

import com.google.gson.annotations.SerializedName;

public class Module {

    @SerializedName("trainerModuleID") private String trainerModuleID;
    @SerializedName("trainerID") private String trainerID;
    @SerializedName("trainerModuleName") private String trainerModuleName;
    @SerializedName("trainerModuleDuration") private String trainerModuleDuration;
    @SerializedName("trainerModuleDurationUnit") private String trainerModuleDurationUnit;
    @SerializedName("trainerModuleSessions") private String trainerModuleSessions;
    @SerializedName("trainerModuleDetails") private String trainerModuleDetails;
    @SerializedName("trainerModuleFormat") private String trainerModuleFormat;
    @SerializedName("trainerModuleSize") private String trainerModuleSize;
    @SerializedName("trainerModuleFees") private String trainerModuleFees;

    public String getTrainerModuleID() {
        return trainerModuleID;
    }

    public void setTrainerModuleID(String trainerModuleID) {
        this.trainerModuleID = trainerModuleID;
    }

    public String getTrainerID() {
        return trainerID;
    }

    public void setTrainerID(String trainerID) {
        this.trainerID = trainerID;
    }

    public String getTrainerModuleName() {
        return trainerModuleName;
    }

    public void setTrainerModuleName(String trainerModuleName) {
        this.trainerModuleName = trainerModuleName;
    }

    public String getTrainerModuleDuration() {
        return trainerModuleDuration;
    }

    public void setTrainerModuleDuration(String trainerModuleDuration) {
        this.trainerModuleDuration = trainerModuleDuration;
    }

    public String getTrainerModuleDurationUnit() {
        return trainerModuleDurationUnit;
    }

    public void setTrainerModuleDurationUnit(String trainerModuleDurationUnit) {
        this.trainerModuleDurationUnit = trainerModuleDurationUnit;
    }

    public String getTrainerModuleSessions() {
        return trainerModuleSessions;
    }

    public void setTrainerModuleSessions(String trainerModuleSessions) {
        this.trainerModuleSessions = trainerModuleSessions;
    }

    public String getTrainerModuleDetails() {
        return trainerModuleDetails;
    }

    public void setTrainerModuleDetails(String trainerModuleDetails) {
        this.trainerModuleDetails = trainerModuleDetails;
    }

    public String getTrainerModuleFormat() {
        return trainerModuleFormat;
    }

    public void setTrainerModuleFormat(String trainerModuleFormat) {
        this.trainerModuleFormat = trainerModuleFormat;
    }

    public String getTrainerModuleSize() {
        return trainerModuleSize;
    }

    public void setTrainerModuleSize(String trainerModuleSize) {
        this.trainerModuleSize = trainerModuleSize;
    }

    public String getTrainerModuleFees() {
        return trainerModuleFees;
    }

    public void setTrainerModuleFees(String trainerModuleFees) {
        this.trainerModuleFees = trainerModuleFees;
    }
}