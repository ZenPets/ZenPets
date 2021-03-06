package co.zenpets.kennels.utils.models.kennels;

import com.google.gson.annotations.SerializedName;

public class Kennel {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("kennelID") private String kennelID;
    @SerializedName("kennelName") private String kennelName;
    @SerializedName("kennelEmail") private String kennelEmail;
    @SerializedName("kennelCoverPhoto") private String kennelCoverPhoto;
    @SerializedName("kennelContactName") private String kennelContactName;
    @SerializedName("kennelContactPhonePrefix") private String kennelContactPhonePrefix;
    @SerializedName("kennelContactPhoneNumber") private String kennelContactPhoneNumber;
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
    @SerializedName("kennelPetCapacity") private String kennelPetCapacity;
    @SerializedName("kennelValidFrom") private String kennelValidFrom;
    @SerializedName("kennelValidTo") private String kennelValidTo;
    @SerializedName("kennelPaymentID") private String kennelPaymentID;
    @SerializedName("kennelToken") private String kennelToken;
    @SerializedName("kennelReviews") private String kennelReviews;
    @SerializedName("kennelPositives") private String kennelPositives;
    @SerializedName("kennelVoteStats") private String kennelVoteStats;
    @SerializedName("kennelRating") private String kennelRating;
    @SerializedName("kennelVerified") private String kennelVerified;

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

    public String getKennelName() {
        return kennelName;
    }

    public void setKennelName(String kennelName) {
        this.kennelName = kennelName;
    }

    public String getKennelEmail() {
        return kennelEmail;
    }

    public void setKennelEmail(String kennelEmail) {
        this.kennelEmail = kennelEmail;
    }

    public String getKennelCoverPhoto() {
        return kennelCoverPhoto;
    }

    public void setKennelCoverPhoto(String kennelCoverPhoto) {
        this.kennelCoverPhoto = kennelCoverPhoto;
    }

    public String getKennelContactName() {
        return kennelContactName;
    }

    public void setKennelContactName(String kennelContactName) {
        this.kennelContactName = kennelContactName;
    }

    public String getKennelContactPhonePrefix() {
        return kennelContactPhonePrefix;
    }

    public void setKennelContactPhonePrefix(String kennelContactPhonePrefix) {
        this.kennelContactPhonePrefix = kennelContactPhonePrefix;
    }

    public String getKennelContactPhoneNumber() {
        return kennelContactPhoneNumber;
    }

    public void setKennelContactPhoneNumber(String kennelContactPhoneNumber) {
        this.kennelContactPhoneNumber = kennelContactPhoneNumber;
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

    public String getKennelPetCapacity() {
        return kennelPetCapacity;
    }

    public void setKennelPetCapacity(String kennelPetCapacity) {
        this.kennelPetCapacity = kennelPetCapacity;
    }

    public String getKennelValidFrom() {
        return kennelValidFrom;
    }

    public void setKennelValidFrom(String kennelValidFrom) {
        this.kennelValidFrom = kennelValidFrom;
    }

    public String getKennelValidTo() {
        return kennelValidTo;
    }

    public void setKennelValidTo(String kennelValidTo) {
        this.kennelValidTo = kennelValidTo;
    }

    public String getKennelPaymentID() {
        return kennelPaymentID;
    }

    public void setKennelPaymentID(String kennelPaymentID) {
        this.kennelPaymentID = kennelPaymentID;
    }

    public String getKennelToken() {
        return kennelToken;
    }

    public void setKennelToken(String kennelToken) {
        this.kennelToken = kennelToken;
    }

    public String getKennelReviews() {
        return kennelReviews;
    }

    public void setKennelReviews(String kennelReviews) {
        this.kennelReviews = kennelReviews;
    }

    public String getKennelPositives() {
        return kennelPositives;
    }

    public void setKennelPositives(String kennelPositives) {
        this.kennelPositives = kennelPositives;
    }

    public String getKennelVoteStats() {
        return kennelVoteStats;
    }

    public void setKennelVoteStats(String kennelVoteStats) {
        this.kennelVoteStats = kennelVoteStats;
    }

    public String getKennelRating() {
        return kennelRating;
    }

    public void setKennelRating(String kennelRating) {
        this.kennelRating = kennelRating;
    }

    public String getKennelVerified() {
        return kennelVerified;
    }

    public void setKennelVerified(String kennelVerified) {
        this.kennelVerified = kennelVerified;
    }
}