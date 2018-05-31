package biz.zenpets.users.utils.models.kennels.kennels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Kennel {

    @SerializedName("kennelID")
    @Expose
    private String kennelID;
    @SerializedName("kennelOwnerID")
    @Expose
    private String kennelOwnerID;
    @SerializedName("kennelOwnerName")
    @Expose
    private String kennelOwnerName;
    @SerializedName("kennelOwnerDisplayProfile")
    @Expose
    private String kennelOwnerDisplayProfile;
    @SerializedName("kennelName")
    @Expose
    private String kennelName;
    @SerializedName("kennelCoverPhoto")
    @Expose
    private String kennelCoverPhoto;
    @SerializedName("kennelAddress")
    @Expose
    private String kennelAddress;
    @SerializedName("kennelPinCode")
    @Expose
    private String kennelPinCode;
    @SerializedName("countryID")
    @Expose
    private String countryID;
    @SerializedName("countryName")
    @Expose
    private String countryName;
    @SerializedName("stateID")
    @Expose
    private String stateID;
    @SerializedName("stateName")
    @Expose
    private String stateName;
    @SerializedName("cityID")
    @Expose
    private String cityID;
    @SerializedName("cityName")
    @Expose
    private String cityName;
    @SerializedName("kennelLatitude")
    @Expose
    private String kennelLatitude;
    @SerializedName("kennelLongitude")
    @Expose
    private String kennelLongitude;
    @SerializedName("kennelDistance")
    @Expose
    private String kennelDistance;
    @SerializedName("kennelPhonePrefix1")
    @Expose
    private String kennelPhonePrefix1;
    @SerializedName("kennelPhoneNumber1")
    @Expose
    private String kennelPhoneNumber1;
    @SerializedName("kennelPhonePrefix2")
    @Expose
    private String kennelPhonePrefix2;
    @SerializedName("kennelPhoneNumber2")
    @Expose
    private String kennelPhoneNumber2;
    @SerializedName("kennelPetCapacity")
    @Expose
    private String kennelPetCapacity;

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

    public String getKennelCoverPhoto() {
        return kennelCoverPhoto;
    }

    public void setKennelCoverPhoto(String kennelCoverPhoto) {
        this.kennelCoverPhoto = kennelCoverPhoto;
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

    public String getKennelDistance() {
        return kennelDistance;
    }

    public void setKennelDistance(String kennelDistance) {
        this.kennelDistance = kennelDistance;
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

    public String getKennelPetCapacity() {
        return kennelPetCapacity;
    }

    public void setKennelPetCapacity(String kennelPetCapacity) {
        this.kennelPetCapacity = kennelPetCapacity;
    }
}