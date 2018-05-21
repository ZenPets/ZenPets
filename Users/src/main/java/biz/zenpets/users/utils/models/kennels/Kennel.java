package biz.zenpets.users.utils.models.kennels;

import com.google.gson.annotations.SerializedName;

public class Kennel {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("kennelID") private String kennelID;
    @SerializedName("kennelOwnerID") private String kennelOwnerID;
    @SerializedName("kennelOwnerName") private String kennelOwnerName;
    @SerializedName("kennelOwnerDisplayProfile") private String kennelOwnerDisplayProfile;
    @SerializedName("kennelName") private String kennelName;
    @SerializedName("kennelAddress") private String kennelAddress;
    @SerializedName("kennelPinCode") private String kennelPinCode;
    @SerializedName("countryID") private String countryID;
    @SerializedName("countryName") private String countryName;
    @SerializedName("stateID") private String stateID;
    @SerializedName("stateName") private String stateName;
    @SerializedName("cityID") private String cityID;
    @SerializedName("cityName") private String cityName;
    @SerializedName("kennelLatitude") private String kennelLatitude;
    @SerializedName("kennelLongitude") private String kennelLongitude;
    @SerializedName("kennelPhonePrefix1") private String kennelPhonePrefix1;
    @SerializedName("kennelPhoneNumber1") private String kennelPhoneNumber1;
    @SerializedName("kennelPhonePrefix2") private String kennelPhonePrefix2;
    @SerializedName("kennelPhoneNumber2") private String kennelPhoneNumber2;
    @SerializedName("kennelLargePetCapacity") private String kennelLargePetCapacity;
    @SerializedName("kennelMediumPetCapacity") private String kennelMediumPetCapacity;
    @SerializedName("kennelSmallPetCapacity") private String kennelSmallPetCapacity;

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

    public String getKennelID() {
        return kennelID;
    }

    public void setKennelID(String kennelID) {
        this.kennelID = kennelID;
    }

    public String getKennelOwnerID() {
        return kennelOwnerID;
    }

    public void setKennelOwnerID(String kennelOwnerID) {
        this.kennelOwnerID = kennelOwnerID;
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

    public String getKennelName() {
        return kennelName;
    }

    public void setKennelName(String kennelName) {
        this.kennelName = kennelName;
    }

    public String getKennelAddress() {
        return kennelAddress;
    }

    public void setKennelAddress(String kennelAddress) {
        this.kennelAddress = kennelAddress;
    }

    public String getKennelPinCode() {
        return kennelPinCode;
    }

    public void setKennelPinCode(String kennelPinCode) {
        this.kennelPinCode = kennelPinCode;
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

    public String getKennelLatitude() {
        return kennelLatitude;
    }

    public void setKennelLatitude(String kennelLatitude) {
        this.kennelLatitude = kennelLatitude;
    }

    public String getKennelLongitude() {
        return kennelLongitude;
    }

    public void setKennelLongitude(String kennelLongitude) {
        this.kennelLongitude = kennelLongitude;
    }

    public String getKennelPhonePrefix1() {
        return kennelPhonePrefix1;
    }

    public void setKennelPhonePrefix1(String kennelPhonePrefix1) {
        this.kennelPhonePrefix1 = kennelPhonePrefix1;
    }

    public String getKennelPhoneNumber1() {
        return kennelPhoneNumber1;
    }

    public void setKennelPhoneNumber1(String kennelPhoneNumber1) {
        this.kennelPhoneNumber1 = kennelPhoneNumber1;
    }

    public String getKennelPhonePrefix2() {
        return kennelPhonePrefix2;
    }

    public void setKennelPhonePrefix2(String kennelPhonePrefix2) {
        this.kennelPhonePrefix2 = kennelPhonePrefix2;
    }

    public String getKennelPhoneNumber2() {
        return kennelPhoneNumber2;
    }

    public void setKennelPhoneNumber2(String kennelPhoneNumber2) {
        this.kennelPhoneNumber2 = kennelPhoneNumber2;
    }

    public String getKennelLargePetCapacity() {
        return kennelLargePetCapacity;
    }

    public void setKennelLargePetCapacity(String kennelLargePetCapacity) {
        this.kennelLargePetCapacity = kennelLargePetCapacity;
    }

    public String getKennelMediumPetCapacity() {
        return kennelMediumPetCapacity;
    }

    public void setKennelMediumPetCapacity(String kennelMediumPetCapacity) {
        this.kennelMediumPetCapacity = kennelMediumPetCapacity;
    }

    public String getKennelSmallPetCapacity() {
        return kennelSmallPetCapacity;
    }

    public void setKennelSmallPetCapacity(String kennelSmallPetCapacity) {
        this.kennelSmallPetCapacity = kennelSmallPetCapacity;
    }
}