package biz.zenpets.trainers.utils.models.trainers;

import com.google.gson.annotations.SerializedName;

public class Trainer {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("trainerID") private String trainerID;
    @SerializedName("trainerAuthID") private String trainerAuthID;
    @SerializedName("trainerName") private String trainerName;
    @SerializedName("trainerEmail") private String trainerEmail;
    @SerializedName("trainerPhonePrefix") private String trainerPhonePrefix;
    @SerializedName("trainerPhoneNumber") private String trainerPhoneNumber;
    @SerializedName("trainerAddress") private String trainerAddress;
    @SerializedName("trainerPincode") private String trainerPincode;
    @SerializedName("trainerLandmark") private String trainerLandmark;
    @SerializedName("trainerLatitude") private Double trainerLatitude;
    @SerializedName("trainerLongitude") private Double trainerLongitude;
    @SerializedName("countryID") private String countryID;
    @SerializedName("countryName") private String countryName;
    @SerializedName("stateID") private String stateID;
    @SerializedName("stateName") private String stateName;
    @SerializedName("cityID") private String cityID;
    @SerializedName("cityName") private String cityName;
    @SerializedName("trainerGender") private String trainerGender;
    @SerializedName("trainerProfile") private String trainerProfile;
    @SerializedName("trainerDisplayProfile") private String trainerDisplayProfile;
    @SerializedName("trainerToken") private String trainerToken;

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

    public String getTrainerID() {
        return trainerID;
    }

    public void setTrainerID(String trainerID) {
        this.trainerID = trainerID;
    }

    public String getTrainerAuthID() {
        return trainerAuthID;
    }

    public void setTrainerAuthID(String trainerAuthID) {
        this.trainerAuthID = trainerAuthID;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    public String getTrainerEmail() {
        return trainerEmail;
    }

    public void setTrainerEmail(String trainerEmail) {
        this.trainerEmail = trainerEmail;
    }

    public String getTrainerPhonePrefix() {
        return trainerPhonePrefix;
    }

    public void setTrainerPhonePrefix(String trainerPhonePrefix) {
        this.trainerPhonePrefix = trainerPhonePrefix;
    }

    public String getTrainerPhoneNumber() {
        return trainerPhoneNumber;
    }

    public void setTrainerPhoneNumber(String trainerPhoneNumber) {
        this.trainerPhoneNumber = trainerPhoneNumber;
    }

    public String getTrainerAddress() {
        return trainerAddress;
    }

    public void setTrainerAddress(String trainerAddress) {
        this.trainerAddress = trainerAddress;
    }

    public String getTrainerPincode() {
        return trainerPincode;
    }

    public void setTrainerPincode(String trainerPincode) {
        this.trainerPincode = trainerPincode;
    }

    public String getTrainerLandmark() {
        return trainerLandmark;
    }

    public void setTrainerLandmark(String trainerLandmark) {
        this.trainerLandmark = trainerLandmark;
    }

    public Double getTrainerLatitude() {
        return trainerLatitude;
    }

    public void setTrainerLatitude(Double trainerLatitude) {
        this.trainerLatitude = trainerLatitude;
    }

    public Double getTrainerLongitude() {
        return trainerLongitude;
    }

    public void setTrainerLongitude(Double trainerLongitude) {
        this.trainerLongitude = trainerLongitude;
    }

    public String getCountryID() {
        return countryID;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getStateID() {
        return stateID;
    }

    public void setStateID(String stateID) {
        this.stateID = stateID;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCityID() {
        return cityID;
    }

    public void setCityID(String cityID) {
        this.cityID = cityID;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getTrainerGender() {
        return trainerGender;
    }

    public void setTrainerGender(String trainerGender) {
        this.trainerGender = trainerGender;
    }

    public String getTrainerProfile() {
        return trainerProfile;
    }

    public void setTrainerProfile(String trainerProfile) {
        this.trainerProfile = trainerProfile;
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
}