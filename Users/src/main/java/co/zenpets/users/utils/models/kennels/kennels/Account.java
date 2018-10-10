package co.zenpets.users.utils.models.kennels.kennels;

import com.google.gson.annotations.SerializedName;

public class Account {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("kennelOwnerID") private String kennelOwnerID;
    @SerializedName("kennelOwnerAuthID") private String kennelOwnerAuthID;
    @SerializedName("kennelOwnerName") private String kennelOwnerName;
    @SerializedName("kennelOwnerDisplayProfile") private String kennelOwnerDisplayProfile;
    @SerializedName("kennelOwnerEmail") private String kennelOwnerEmail;
    @SerializedName("kennelOwnerPhonePrefix") private String kennelOwnerPhonePrefix;
    @SerializedName("kennelOwnerPhoneNumber") private String kennelOwnerPhoneNumber;
    @SerializedName("kennelOwnerAddress") private String kennelOwnerAddress;
    @SerializedName("kennelOwnerPinCode") private String kennelOwnerPinCode;
    @SerializedName("countryID") private String countryID;
    @SerializedName("countryName") private String countryName;
    @SerializedName("stateID") private String stateID;
    @SerializedName("stateName") private String stateName;
    @SerializedName("cityID") private String cityID;
    @SerializedName("cityName") private String cityName;
    @SerializedName("kennelOwnerToken") private String kennelOwnerToken;

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

    public String getKennelOwnerID() {
        return kennelOwnerID;
    }

    public void setKennelOwnerID(String kennelOwnerID) {
        this.kennelOwnerID = kennelOwnerID;
    }

    public String getKennelOwnerAuthID() {
        return kennelOwnerAuthID;
    }

    public void setKennelOwnerAuthID(String kennelOwnerAuthID) {
        this.kennelOwnerAuthID = kennelOwnerAuthID;
    }

    public String getKennelOwnerName() {
        return kennelOwnerName;
    }

    public void setKennelOwnerName(String kennelOwnerName) {
        this.kennelOwnerName = kennelOwnerName;
    }

    public String getKennelOwnerDisplayProfile() {
        return kennelOwnerDisplayProfile;
    }

    public void setKennelOwnerDisplayProfile(String kennelOwnerDisplayProfile) {
        this.kennelOwnerDisplayProfile = kennelOwnerDisplayProfile;
    }

    public String getKennelOwnerEmail() {
        return kennelOwnerEmail;
    }

    public void setKennelOwnerEmail(String kennelOwnerEmail) {
        this.kennelOwnerEmail = kennelOwnerEmail;
    }

    public String getKennelOwnerPhonePrefix() {
        return kennelOwnerPhonePrefix;
    }

    public void setKennelOwnerPhonePrefix(String kennelOwnerPhonePrefix) {
        this.kennelOwnerPhonePrefix = kennelOwnerPhonePrefix;
    }

    public String getKennelOwnerPhoneNumber() {
        return kennelOwnerPhoneNumber;
    }

    public void setKennelOwnerPhoneNumber(String kennelOwnerPhoneNumber) {
        this.kennelOwnerPhoneNumber = kennelOwnerPhoneNumber;
    }

    public String getKennelOwnerAddress() {
        return kennelOwnerAddress;
    }

    public void setKennelOwnerAddress(String kennelOwnerAddress) {
        this.kennelOwnerAddress = kennelOwnerAddress;
    }

    public String getKennelOwnerPinCode() {
        return kennelOwnerPinCode;
    }

    public void setKennelOwnerPinCode(String kennelOwnerPinCode) {
        this.kennelOwnerPinCode = kennelOwnerPinCode;
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

    public String getKennelOwnerToken() {
        return kennelOwnerToken;
    }

    public void setKennelOwnerToken(String kennelOwnerToken) {
        this.kennelOwnerToken = kennelOwnerToken;
    }
}