package biz.zenpets.users.utils.models.kennels.promotion;

import com.google.gson.annotations.SerializedName;

public class Promotion {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("promotedID") private String promotedID;
    @SerializedName("kennelID") private String kennelID;
    @SerializedName("optionID") private String optionID;
    @SerializedName("paymentID") private String paymentID;
    @SerializedName("promotedFrom") private String promotedFrom;
    @SerializedName("promotedTo") private String promotedTo;
    @SerializedName("promotedTimestamp") private String promotedTimestamp;
    @SerializedName("kennelOwnerID") private String kennelOwnerID;
    @SerializedName("kennelOwnerName") private String kennelOwnerName;
    @SerializedName("kennelOwnerDisplayProfile") private String kennelOwnerDisplayProfile;
    @SerializedName("kennelName") private String kennelName;
    @SerializedName("kennelCoverPhoto") private String kennelCoverPhoto;
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
    @SerializedName("kennelDistance") private String kennelDistance;
    @SerializedName("kennelPhonePrefix1") private String kennelPhonePrefix1;
    @SerializedName("kennelPhoneNumber1") private String kennelPhoneNumber1;
    @SerializedName("kennelPhonePrefix2") private String kennelPhonePrefix2;
    @SerializedName("kennelPhoneNumber2") private String kennelPhoneNumber2;
    @SerializedName("kennelPetCapacity") private String kennelPetCapacity;
    @SerializedName("kennelLikes") private String kennelLikes;
    @SerializedName("kennelVotes") private String kennelVotes;
    @SerializedName("kennelLikesPercent") private String kennelLikesPercent;

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

    public String getPromotedID() {
        return promotedID;
    }

    public void setPromotedID(String promotedID) {
        this.promotedID = promotedID;
    }

    public String getKennelID() {
        return kennelID;
    }

    public void setKennelID(String kennelID) {
        this.kennelID = kennelID;
    }

    public String getOptionID() {
        return optionID;
    }

    public void setOptionID(String optionID) {
        this.optionID = optionID;
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public String getPromotedFrom() {
        return promotedFrom;
    }

    public void setPromotedFrom(String promotedFrom) {
        this.promotedFrom = promotedFrom;
    }

    public String getPromotedTo() {
        return promotedTo;
    }

    public void setPromotedTo(String promotedTo) {
        this.promotedTo = promotedTo;
    }

    public String getPromotedTimestamp() {
        return promotedTimestamp;
    }

    public void setPromotedTimestamp(String promotedTimestamp) {
        this.promotedTimestamp = promotedTimestamp;
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

    public String getKennelLikes() {
        return kennelLikes;
    }

    public void setKennelLikes(String kennelLikes) {
        this.kennelLikes = kennelLikes;
    }

    public String getKennelVotes() {
        return kennelVotes;
    }

    public void setKennelVotes(String kennelVotes) {
        this.kennelVotes = kennelVotes;
    }

    public String getKennelLikesPercent() {
        return kennelLikesPercent;
    }

    public void setKennelLikesPercent(String kennelLikesPercent) {
        this.kennelLikesPercent = kennelLikesPercent;
    }
}