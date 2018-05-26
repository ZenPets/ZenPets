package biz.zenpets.users.utils.models.kennels;

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

    public Kennel withKennelID(String kennelID) {
        this.kennelID = kennelID;
        return this;
    }

    public String getKennelOwnerID() {
        return kennelOwnerID;
    }

    public void setKennelOwnerID(String kennelOwnerID) {
        this.kennelOwnerID = kennelOwnerID;
    }

    public Kennel withKennelOwnerID(String kennelOwnerID) {
        this.kennelOwnerID = kennelOwnerID;
        return this;
    }

    public String getKennelOwnerName() {
        return kennelOwnerName;
    }

    public void setKennelOwnerName(String kennelOwnerName) {
        this.kennelOwnerName = kennelOwnerName;
    }

    public Kennel withKennelOwnerName(String kennelOwnerName) {
        this.kennelOwnerName = kennelOwnerName;
        return this;
    }

    public String getKennelOwnerDisplayProfile() {
        return kennelOwnerDisplayProfile;
    }

    public void setKennelOwnerDisplayProfile(String kennelOwnerDisplayProfile) {
        this.kennelOwnerDisplayProfile = kennelOwnerDisplayProfile;
    }

    public Kennel withKennelOwnerDisplayProfile(String kennelOwnerDisplayProfile) {
        this.kennelOwnerDisplayProfile = kennelOwnerDisplayProfile;
        return this;
    }

    public String getKennelName() {
        return kennelName;
    }

    public void setKennelName(String kennelName) {
        this.kennelName = kennelName;
    }

    public Kennel withKennelName(String kennelName) {
        this.kennelName = kennelName;
        return this;
    }

    public String getKennelCoverPhoto() {
        return kennelCoverPhoto;
    }

    public void setKennelCoverPhoto(String kennelCoverPhoto) {
        this.kennelCoverPhoto = kennelCoverPhoto;
    }

    public Kennel withKennelCoverPhoto(String kennelCoverPhoto) {
        this.kennelCoverPhoto = kennelCoverPhoto;
        return this;
    }

    public String getKennelAddress() {
        return kennelAddress;
    }

    public void setKennelAddress(String kennelAddress) {
        this.kennelAddress = kennelAddress;
    }

    public Kennel withKennelAddress(String kennelAddress) {
        this.kennelAddress = kennelAddress;
        return this;
    }

    public String getKennelPinCode() {
        return kennelPinCode;
    }

    public void setKennelPinCode(String kennelPinCode) {
        this.kennelPinCode = kennelPinCode;
    }

    public Kennel withKennelPinCode(String kennelPinCode) {
        this.kennelPinCode = kennelPinCode;
        return this;
    }

    public String getCountryID() {
        return countryID;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }

    public Kennel withCountryID(String countryID) {
        this.countryID = countryID;
        return this;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Kennel withCountryName(String countryName) {
        this.countryName = countryName;
        return this;
    }

    public String getStateID() {
        return stateID;
    }

    public void setStateID(String stateID) {
        this.stateID = stateID;
    }

    public Kennel withStateID(String stateID) {
        this.stateID = stateID;
        return this;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public Kennel withStateName(String stateName) {
        this.stateName = stateName;
        return this;
    }

    public String getCityID() {
        return cityID;
    }

    public void setCityID(String cityID) {
        this.cityID = cityID;
    }

    public Kennel withCityID(String cityID) {
        this.cityID = cityID;
        return this;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Kennel withCityName(String cityName) {
        this.cityName = cityName;
        return this;
    }

    public String getKennelLatitude() {
        return kennelLatitude;
    }

    public void setKennelLatitude(String kennelLatitude) {
        this.kennelLatitude = kennelLatitude;
    }

    public Kennel withKennelLatitude(String kennelLatitude) {
        this.kennelLatitude = kennelLatitude;
        return this;
    }

    public String getKennelLongitude() {
        return kennelLongitude;
    }

    public void setKennelLongitude(String kennelLongitude) {
        this.kennelLongitude = kennelLongitude;
    }

    public Kennel withKennelLongitude(String kennelLongitude) {
        this.kennelLongitude = kennelLongitude;
        return this;
    }

    public String getKennelPhonePrefix1() {
        return kennelPhonePrefix1;
    }

    public void setKennelPhonePrefix1(String kennelPhonePrefix1) {
        this.kennelPhonePrefix1 = kennelPhonePrefix1;
    }

    public Kennel withKennelPhonePrefix1(String kennelPhonePrefix1) {
        this.kennelPhonePrefix1 = kennelPhonePrefix1;
        return this;
    }

    public String getKennelPhoneNumber1() {
        return kennelPhoneNumber1;
    }

    public void setKennelPhoneNumber1(String kennelPhoneNumber1) {
        this.kennelPhoneNumber1 = kennelPhoneNumber1;
    }

    public Kennel withKennelPhoneNumber1(String kennelPhoneNumber1) {
        this.kennelPhoneNumber1 = kennelPhoneNumber1;
        return this;
    }

    public String getKennelPhonePrefix2() {
        return kennelPhonePrefix2;
    }

    public void setKennelPhonePrefix2(String kennelPhonePrefix2) {
        this.kennelPhonePrefix2 = kennelPhonePrefix2;
    }

    public Kennel withKennelPhonePrefix2(String kennelPhonePrefix2) {
        this.kennelPhonePrefix2 = kennelPhonePrefix2;
        return this;
    }

    public String getKennelPhoneNumber2() {
        return kennelPhoneNumber2;
    }

    public void setKennelPhoneNumber2(String kennelPhoneNumber2) {
        this.kennelPhoneNumber2 = kennelPhoneNumber2;
    }

    public Kennel withKennelPhoneNumber2(String kennelPhoneNumber2) {
        this.kennelPhoneNumber2 = kennelPhoneNumber2;
        return this;
    }

    public String getKennelPetCapacity() {
        return kennelPetCapacity;
    }

    public void setKennelPetCapacity(String kennelPetCapacity) {
        this.kennelPetCapacity = kennelPetCapacity;
    }

    public Kennel withKennelPetCapacity(String kennelPetCapacity) {
        this.kennelPetCapacity = kennelPetCapacity;
        return this;
    }
}